## 1. 简介

Derive4J 是一个注解处理器，支持Java8 中的各种功能概念。

在本教程中，我们将介绍 Derive4J 和该框架支持的最重要的概念：

-   代数数据类型
-   结构模式匹配
-   一级懒惰

## 2.Maven依赖

要使用 Derive4J，我们需要将[依赖 ](https://search.maven.org/search?q=g:org.derive4j AND a:derive4j)项包含在我们的项目中：

```xml
<dependency>
    <groupId>org.derive4j</groupId>
    <artifactId>derive4j</artifactId>
    <version>1.1.0</version>
    <optional>true</optional>
</dependency>
```

## 3. 代数数据类型

### 3.1. 描述

代数数据类型 (ADT) 是一种复合类型——它们是其他类型或泛型的组合。

ADT 通常分为两大类：

-   和
-   产品

代数数据类型默认存在于许多语言中，例如 Haskell 和 Scala。

### 3.2. 金额类型

Sum 是表示逻辑或运算的数据类型。 这意味着它可以是一件事或另一件事，但不能同时发生。简单来说，sum type就是不同case的集合。“sum”这个名字来源于这样一个事实，即不同值的总数就是案例总数。

Enum 是Java中最接近 sum 类型的东西。枚举有一组可能的值，但一次只能有一个。但是，我们不能在Java中将任何其他数据与Enum相关联，这是代数数据类型相对于Enum的主要优势。

### 3.3. 产品类别

Product 是表示逻辑 AND 运算的数据类型。 它是几个值的组合。

Java中的类 可以看作是一种产品类型。产品类型由它们的字段组合定义。

[我们可以在这篇维基百科文章](https://en.wikipedia.org/wiki/Algebraic_data_type)中找到有关 ADT 的更多信息 。

### 3.4. 用法

一种常用的代数数据类型是Either。 我们可以将Either视为更复杂的Optional，可以在可能缺少值或操作可能导致异常时使用。

我们需要使用至少一个抽象方法来注解一个抽象类或接口，Derive4J 将使用这些方法来生成我们的 ADT 结构。

要在 Derive4J 中创建Either数据类型，我们需要创建一个 接口：

```java
@Data
interface Either<A, B> {
    <X> X match(Function<A, X> left, Function<B, X> right);
}
```

我们的 界面用 @Data注解，这将允许 Derive4J 为我们生成正确的代码。生成的代码包含工厂方法、惰性构造函数和各种其他方法。

默认情况下，生成的代码获取带注解类的名称，但采用复数形式。但是，可以通过 inClass 参数进行配置。

现在，我们可以使用生成的代码来创建 Either ADT 并验证它是否正常工作：

```java
public void testEitherIsCreatedFromRight() {
    Either<Exception, String> either = Eithers.right("Okay");
    Optional<Exception> leftOptional = Eithers.getLeft(either);
    Optional<String> rightOptional = Eithers.getRight(either);
    Assertions.assertThat(leftOptional).isEmpty();
    Assertions.assertThat(rightOptional).hasValue("Okay");
}
```

我们还可以使用生成的match() 方法来执行函数，具体取决于 Either的哪一侧存在：

```java
public void testEitherIsMatchedWithRight() {
    Either<Exception, String> either = Eithers.right("Okay");
    Function<Exception, String> leftFunction = Mockito.mock(Function.class);
    Function<String, String> rightFunction = Mockito.mock(Function.class);
    either.match(leftFunction, rightFunction);
    Mockito.verify(rightFunction, Mockito.times(1)).apply("Okay");
    Mockito.verify(leftFunction, Mockito.times(0)).apply(Mockito.any(Exception.class));
}
```

## 4. 模式匹配

使用代数数据类型启用的功能之一是 模式匹配。

模式匹配是根据模式检查值的机制。 基本上，模式匹配是一种更强大的 [switch](https://www.baeldung.com/java-switch) 语句，但对匹配类型没有限制，也没有模式常量的要求。有关更多信息，我们可以查看 [这篇关于模式匹配的维基百科文章](https://en.wikipedia.org/wiki/Pattern_matching)。

要使用模式匹配，我们将创建一个模拟 HTTP 请求的类。用户将能够使用给定的 HTTP 方法之一：

-   得到
-   邮政
-   删除
-   放

让我们在 Derive4J 中将我们的请求类建模为 ADT，从HTTPRequest接口开始：

```java
@Data
interface HTTPRequest {
    interface Cases<R>{
        R GET(String path);
        R POST(String path);
        R PUT(String path);
        R DELETE(String path);
    }

    <R> R match(Cases<R> method);
}
```

生成的类 HttpRequests(注意复数形式)现在将允许我们根据请求的类型执行模式匹配。

为此，我们将创建一个非常简单的 HTTPServer 类，它会 根据请求的类型以不同的Status进行响应。

首先，让我们创建一个简单的 HTTPResponse 类，它将作为服务器对客户端的响应：

```java
public class HTTPResponse {
    int statusCode;
    String responseBody;

    public HTTPResponse(int statusCode, String responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
}
```

然后我们可以创建将使用模式匹配来发送正确响应的服务器：

```java
public class HTTPServer {
    public static String GET_RESPONSE_BODY = "Success!";
    public static String PUT_RESPONSE_BODY = "Resource Created!";
    public static String POST_RESPONSE_BODY = "Resource Updated!";
    public static String DELETE_RESPONSE_BODY = "Resource Deleted!";

    public HTTPResponse acceptRequest(HTTPRequest request) {
        return HTTPRequests.caseOf(request)
          .GET((path) -> new HTTPResponse(200, GET_RESPONSE_BODY))
          .POST((path,body) -> new HTTPResponse(201, POST_RESPONSE_BODY))
          .PUT((path,body) -> new HTTPResponse(200, PUT_RESPONSE_BODY))
          .DELETE(path -> new HTTPResponse(200, DELETE_RESPONSE_BODY));
    }
}
```

我们类的 acceptRequest() 方法对请求的类型使用模式匹配，并将根据请求的类型返回不同的响应：

```java
@Test
public void whenRequestReachesServer_thenProperResponseIsReturned() {
    HTTPServer server = new HTTPServer();
    HTTPRequest postRequest = HTTPRequests.POST("http://test.com/post", "Resource");
    HTTPResponse response = server.acceptRequest(postRequest);
    Assert.assertEquals(201, response.getStatusCode());
    Assert.assertEquals(HTTPServer.POST_RESPONSE_BODY, response.getResponseBody());
}
```

## 5.一流的懒惰

Derive4J 允许我们引入惰性的概念，这意味着我们的对象在我们对其执行操作之前不会被初始化。让我们将接口声明为 LazyRequest 并将生成的类配置为命名为 LazyRequestImpl：

```java
@Data(value = @Derive(
  inClass = "{ClassName}Impl",
  make = {Make.lazyConstructor, Make.constructors}
))
public interface LazyRequest {
    interface Cases<R>{
        R GET(String path);
        R POST(String path, String body);
        R PUT(String path, String body);
        R DELETE(String path);
    }

    <R> R match(LazyRequest.Cases<R> method);
}
```

我们现在可以验证生成的惰性构造函数是否正常工作：

```java
@Test
public void whenRequestIsReferenced_thenRequestIsLazilyContructed() {
    LazyRequestSupplier mockSupplier = Mockito.spy(new LazyRequestSupplier());
    LazyRequest request = LazyRequestImpl.lazy(() -> mockSupplier.get());
    Mockito.verify(mockSupplier, Mockito.times(0)).get();
    Assert.assertEquals(LazyRequestImpl.getPath(request), "http://test.com/get");
    Mockito.verify(mockSupplier, Mockito.times(1)).get();
}

class LazyRequestSupplier implements Supplier<LazyRequest> {
    @Override
    public LazyRequest get() {
        return LazyRequestImpl.GET("http://test.com/get");
    }
}
```

[我们可以在 Scala 文档](https://www.scala-lang.org/blog/2017/11/28/view-based-collections.html)中找到有关一流懒惰和示例 的更多信息。

## 六. 总结

在本教程中，我们介绍了 Derive4J 库并使用它来实现一些函数概念，例如代数数据类型和模式匹配，这些在Java中通常不可用。

我们可以[在官方 Derive4J 文档中](https://github.com/derive4j/derive4j)找到有关该库的更多信息。