## 1. 概述

我们的服务通常必须与其他REST服务通信才能获取信息。

在Spring中，我们可以使用[RestTemplate](https://www.baeldung.com/rest-template)来执行同步HTTP请求。数据通常以JSON形式返回，RestTemplate可以为我们转换。

在本教程中，我们将探讨如何将JSON数组转换为Java中的三种不同对象结构：对象数组、POJO[数组](https://www.baeldung.com/java-pojo-class#what-is-a-pojo)和POJO列表。

## 2. JSON、POJO和服务

让我们想象一下，我们有一个端点 `http://localhost:8080/users` 返回用户列表作为以下JSON：

```json
[
    {
        "id": 1,
        "name": "user1"
    },
    {
        "id": 2,
        "name": "user2"
    }
]
```

我们需要相应的User 类来处理数据：

```java
public class User {
    private int id;
    private String name;

    // getters and setters..
}
```

对于我们的接口实现，我们编写了一个UserConsumerServiceImpl并以RestTemplate作为它的依赖：

```java
public class UserConsumerServiceImpl implements UserConsumerService {
    private final RestTemplate restTemplate;

    public UserConsumerServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ...
}
```

## 3. 映射一个JSON对象列表

当对REST请求的响应是一个JSON数组时，我们可以通过几种方法将其转换为Java集合。

让我们看一下这些选项，看看它们使我们能够多么容易地处理返回的数据。我们将研究提取REST服务返回的一些用户对象的用户名。

### 3.1 带有对象数组的RestTemplate

首先，让我们使用RestTemplate.getForEntity进行调用并使用Object[]类型的ResponseEntity来收集响应：

```java
ResponseEntity<Object[]> responseEntity = restTemplate.getForEntity(BASE_URL, Object[].class);
```

接下来，我们可以将主体提取到我们的Object数组中：

```java
Object[] objects = responseEntity.getBody();
```

这里的实际对象只是一些包含我们的数据但不使用我们的用户类型的任意结构。让我们把它转换成我们的用户对象。

为此，我们需要一个ObjectMapper：

```java
ObjectMapper mapper = new ObjectMapper();
```

我们可以将其声明为内联，尽管这通常是作为类的 私有静态最终成员完成的。

最后，我们准备提取用户名：

```java
return Arrays.stream(objects)
    .map(object -> mapper.convertValue(object, User.class))
    .map(User::getName)
    .collect(Collectors.toList());
```

使用此方法，我们基本上可以将任何内容的数组读入Java中的对象数组。例如，如果我们只想计算结果，这会很方便。

但是，它不适合进一步处理。我们不得不付出额外的努力将其转换为我们可以使用的类型。

当我们要求它生成Object作为目标类型时，[Jackson Deserializer](https://www.baeldung.com/jackson-object-mapper-tutorial)实际上将JSON反序列化为一系列LinkedHashMap对象，使用convertValue进行后处理是一种低效的开销。

如果我们首先向Jackson提供我们想要的类型，我们就可以避免它。

### 3.2 带有用户数组的RestTemplate

我们可以将User[]提供给RestTemplate，而不是Object[]：

```java
ResponseEntity<User[]> responseEntity = restTemplate.getForEntity(BASE_URL, User[].class); 
User[] userArray = responseEntity.getBody();
return Arrays.stream(userArray) 
    .map(User::getName) 
    .collect(Collectors.toList());
```

我们可以看到我们不再需要ObjectMapper.convertValue。ResponseEntity内部有User对象。但是我们仍然需要进行一些额外的转换才能使用JavaStream API并使我们的代码能够与List一起工作。

### 3.3 带有用户列表和ParameterizedTypeReference的RestTemplate

如果我们需要Jackson方便地生成一个User列表而不是一个数组，我们需要描述我们想要创建的列表。为此，我们必须使用RestTemplate交流。

此方法采用由[匿名内部类](https://www.baeldung.com/java-anonymous-classes)生成的ParameterizedTypeReference：

```java
ResponseEntity<List<User>> responseEntity = 
    restTemplate.exchange(
        BASE_URL,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<User>>() {}
    );
List<User> users = responseEntity.getBody();
return users.stream()
    .map(User::getName)
    .collect(Collectors.toList());
```

这会生成我们要使用的列表。

让我们仔细看看为什么我们需要使用ParameterizedTypeReference。

在前两个示例中，Spring可以轻松地将JSON反序列化为User.class类型标记，其中类型信息在运行时完全可用。

然而，对于泛型，如果我们尝试使用List<User\>.class就会发生类型擦除。因此，Jackson无法确定<>中的类型。

我们可以通过使用称为ParameterizedTypeReference的超类型标记来克服这个问题。将其实例化为匿名内部类-new ParameterizedTypeReference<List<User\>>() {}利用了泛型类的子类包含编译时类型信息的事实，这些信息不受类型擦除的影响，可以通过反射使用。

## 4. 总结

在本文中，我们看到了使用RestTemplate处理JSON对象的三种不同方式。我们看到了如何指定Object数组的类型和我们自己的自定义类。

然后我们了解了如何使用ParameterizedTypeReference提供类型信息以生成List。