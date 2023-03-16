## 1. 概述

我们的服务经常与其他REST服务通信以获取信息。

从Spring 5开始，我们可以使用[WebClient](https://www.baeldung.com/spring-5-webclient)以响应式、非阻塞的方式执行这些请求。WebClient是新的WebFlux框架的一部分，它建立在Project Reactor之上。它有一个流式的、响应式API，并在其底层实现中使用HTTP协议。

当我们发出Web请求时，数据通常以JSON格式返回。WebClient可以为我们转换它。

在本文中，我们将了解如何使用WebClient将JSON数组转换为Java对象数组、[POJO](https://www.baeldung.com/java-pojo-class#what-is-a-pojo)数组和POJO列表。

## 2. 依赖项

要使用WebClient，我们需要向我们的pom.xml添加一些依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<dependency>
    <groupId>org.projectreactor</groupId>
    <artifactId>reactor-spring</artifactId>
    <version>1.0.1.RELEASE</version>
</dependency>
```

## 3. JSON、POJO和服务

让我们从一个端点http://localhost:8080/readers开始，该端点以JSON数组的形式返回读者列表以及他们最喜欢的书：

```json
[
    {
        "id": 1,
        "name": "reader1",
        "favouriteBook": {
            "author": "Milan Kundera",
            "title": "The Unbearable Lightness of Being"
        }
    },
    {
        "id": 2,
        "name": "reader2",
        "favouriteBook": {
            "author": "Douglas Adams",
            "title": "The Hitchhiker's Guide to the Galaxy"
        }
    }
]
```

我们需要相应的Reader和Book类来处理数据：

```java
public class Reader {
    private int id;
    private String name;
    private Book favouriteBook;

    // getters and setters..
}
```

```java
public class Book {
    private final String author;
    private final String title;

    // getters and setters..
}
```

对于我们的接口实现，我们编写了一个ReaderConsumerServiceImpl并以WebClient作为它的依赖项：

```java
public class ReaderConsumerServiceImpl implements ReaderConsumerService {

    private final WebClient webClient;

    public ReaderConsumerServiceImpl(WebClient webclient) {
        this.webclient = webclient;
    }

    // ...
}
```

## 4. 映射JSON对象列表

当我们从REST请求接收到JSON数组时，有多种方法可以将其转换为Java集合。让我们看看各种选项，看看处理返回的数据是多么容易。我们将研究提取读者最喜欢的书籍。

### 4.1 Mono与Flux

Project Reactor引入了Publisher的两个实现：**Mono**和**Flux**。

当我们需要处理零到多个或可能无限的结果时，Flux<T\>很有用。我们可以将Twitter提要视为示例。

当我们知道结果是一次性返回时-就像在我们的用例中一样，我们可以使用Mono<T\>。

### 4.2 带有Object数组的WebClient

首先，让我们使用WebClient.get进行GET调用，并使用Object[]类型的Mono来收集响应：

```java
Mono<Object[]> response = webClient.get()
    .accept(MediaType.APPLICATION_JSON)
    .retrieve()
    .bodyToMono(Object[].class).log();
```

接下来，让我们将主体提取到我们的Object数组中：

```java
Object[] objects = response.block();
```

这里的实际对象是一个包含我们数据的任意结构。让我们将其转换为一个Reader对象数组。

为此，我们需要一个ObjectMapper：

```java
ObjectMapper mapper = new ObjectMapper();
```

在这里，我们将其声明为内联，尽管这通常是作为类的私有静态最终成员完成的。

最后，我们准备提取读者最喜欢的书籍并将它们收集到一个列表中：

```java
return Arrays.stream(objects)
    .map(object -> mapper.convertValue(object, Reader.class))
    .map(Reader::getFavouriteBook)
    .collect(Collectors.toList());
```

当我们要求[Jackson反序列化器](https://www.baeldung.com/jackson-object-mapper-tutorial)生成Object作为目标类型时，它实际上**将JSON反序列化为一系列LinkedHashMap对象**。使用convertValue进行后处理效率低下。如果我们在反序列化期间向Jackson提供所需的类型，就可以避免这种情况。

### 4.3 带有Reader数组的WebClient

我们可以向WebClient提供Reader[]而不是Object[]：

```java
Mono<Reader[]> response = webClient.get()
    .accept(MediaType.APPLICATION_JSON)
    .retrieve()
    .bodyToMono(Reader[].class).log();
Reader[] readers = response.block();
return Arrays.stream(readers)
    .map(Reader:getFavouriteBook)
    .collect(Collectors.toList());
```

在这里，我们可以观察到我们不再需要ObjectMapper.convertValue。但是，我们仍然需要执行其他转换才能使用Java Stream API并使我们的代码能够与List一起工作。

### 4.4 带有Reader列表的WebClient 

如果我们希望Jackson生成一个Reader列表而不是一个数组，我们需要描述我们想要创建的列表。为此，我们向该方法提供由[匿名内部类](https://www.baeldung.com/java-anonymous-classes)生成的ParameterizedTypeReference：

```java
Mono<List<Reader>> response = webClient.get()
    .accept(MediaType.APPLICATION_JSON)
    .retrieve()
    .bodyToMono(new ParameterizedTypeReference<List<Reader>>() {});
List<Reader> readers = response.block();

return readers.stream()
    .map(Reader::getFavouriteBook)
    .collect(Collectors.toList());
```

这为我们提供了可以使用的列表。

让我们更深入地探讨**为什么需要使用ParameterizedTypeReference**。

当类型信息在运行时可用时，Spring的WebClient可以轻松地将JSON反序列化为Reader.class。

但是，对于泛型，如果我们尝试使用List<Reader\>.class，就会发生[泛型擦除](https://www.baeldung.com/java-type-erasure)。因此，Jackson将无法确定泛型的类型参数。

通过使用ParameterizedTypeReference，我们可以克服这个问题。将其实例化为匿名内部类利用了这样一个事实，即泛型类的子类包含编译时类型信息，这些信息不受类型擦除的影响，并且可以通过反射使用。

## 5. 总结

在本教程中，我们看到了使用WebClient处理JSON对象的三种不同方式。我们看到了指定Object数组类型和我们自己的自定义类的方法。

然后，我们学习了如何使用ParameterizedTypeReference提供信息类型以生成List。