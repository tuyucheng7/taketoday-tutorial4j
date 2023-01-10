## 1. 概述

通常，在我们的 Web 应用程序中管理 HTTP 请求的请求和响应周期时，我们需要一种方法来接入此链。通常，这是在我们完成请求之前或在我们的 servlet 代码完成之后添加一些自定义行为。

[OkHttp](https://square.github.io/okhttp/)是适用于 Android 和Java应用程序的高效 HTTP 和 HTTP/2 客户端。在之前的教程中，我们了解了如何使用 OkHttp的[基础知识。](https://www.baeldung.com/guide-to-okhttp)

在本教程中，我们将学习如何拦截 HTTP 请求和响应对象。

## 2.拦截器

顾名思义，拦截器是可插入的Java组件，我们可以使用它们在请求发送到我们的应用程序代码之前拦截和处理请求。

同样，它们为我们提供了一种强大的机制，可以在容器将响应发送回客户端之前处理服务器响应。

当我们想要更改 HTTP 请求中的某些内容时，这尤其有用，例如添加新的控制标头、更改请求的主体，或者只是生成日志以帮助我们进行调试。

使用拦截器的另一个好处是它们让我们可以将通用功能封装在一个地方。让我们想象一下，我们想要将一些逻辑全局应用到我们所有的请求和响应对象，例如错误处理。

将这种逻辑放入拦截器至少有几个优点：

-   我们只需要在一个地方维护此代码而不是我们所有的端点
-   发出的每个请求都以相同的方式处理错误

最后，我们还可以监视、重写和重试来自拦截器的调用。

## 3. 常见用法

当一个明显的选择时，一些其他常见的任务包括：

-   记录请求参数和其他有用信息
-   向我们的请求添加身份验证和授权标头
-   格式化我们的请求和响应主体
-   压缩发送给客户端的响应数据
-   通过添加一些 cookie 或额外的标头信息来改变我们的响应标头

我们将在后续部分中看到一些实际应用的示例。

## 4.依赖关系

当然，我们需要将标准的[okhttp依赖](https://search.maven.org/classic/#search|gav|1|g%3A"com.squareup.okhttp3" AND a%3A"okhttp")项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.9.1</version>
</dependency>
```

我们还需要另一个专门用于测试的依赖项。让我们添加 OkHttp [mockwebserver工件](https://search.maven.org/classic/#search|ga|1|g%3A"com.squareup.okhttp3" AND a%3A"mockwebserver")：

```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>mockwebserver</artifactId>
    <version>4.9.1</version>
    <scope>test</scope>
</dependency>
```

现在我们已经配置了所有必要的依赖项，我们可以继续编写我们的第一个拦截器。

## 5. 定义一个简单的日志拦截器

让我们从定义我们自己的拦截器开始。为了简单起见，我们的拦截器将记录请求标头和请求 URL：

```java
public class SimpleLoggingInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleLoggingInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        LOGGER.info("Intercepted headers: {} from URL: {}", request.headers(), request.url());

        return chain.proceed(request);
    }
}
```

如我们所见，要创建我们的拦截器，我们只需要继承Interceptor接口，它有一个强制方法intercept(Chain chain)。然后我们可以继续用我们自己的实现覆盖这个方法。

首先，在打印出标头和请求 URL 之前，我们通过调用chain.request()获取传入请求。

重要的是要注意每个拦截器实现的关键部分是对chain.proceed(request)的调用。

这个看起来简单的方法是我们发出信号表示我们想要点击我们的应用程序代码，产生一个响应来满足请求的地方。

### 5.1. 一起插入

要真正使用这个拦截器，我们需要做的就是在构建OkHttpClient实例时调用addInterceptor方法，它应该可以正常工作：

```java
OkHttpClient client = new OkHttpClient.Builder() 
  .addInterceptor(new SimpleLoggingInterceptor())
  .build();
```

我们可以根据需要继续为任意数量的拦截器调用addInterceptor方法。请记住，它们将按照添加的顺序被调用。

### 5.2. 测试拦截器

现在，我们已经定义了第一个拦截器；让我们继续编写我们的第一个集成测试：

```java
@Rule
public MockWebServer server = new MockWebServer();

@Test
public void givenSimpleLogginInterceptor_whenRequestSent_thenHeadersLogged() throws IOException {
    server.enqueue(new MockResponse().setBody("Hello Baeldung Readers!"));
        
    OkHttpClient client = new OkHttpClient.Builder()
      .addInterceptor(new SimpleLoggingInterceptor())
      .build();

    Request request = new Request.Builder()
      .url(server.url("/greeting"))
      .header("User-Agent", "A Baeldung Reader")
      .build();

    try (Response response = client.newCall(request).execute()) {
        assertEquals("Response code should be: ", 200, response.code());
        assertEquals("Body should be: ", "Hello Baeldung Readers!", response.body().string());
    }
}
```

首先，我们使用 OkHttp MockWebServer [JUnit 规则](https://www.baeldung.com/junit-4-rules)。

这是一个轻量级的、可编写脚本的 Web 服务器，用于测试我们将用来测试我们的拦截器的 HTTP 客户端。通过使用此规则，我们将为每个集成测试创建一个干净的服务器实例。

考虑到这一点，现在让我们来看看我们测试的关键部分：

-   首先，我们设置一个模拟响应，在正文中包含一条简单的消息
-   然后，我们构建我们的OkHttpClient并配置我们的SimpleLoggingInterceptor
-   接下来，我们设置要发送的带有一个User-Agent标头的请求
-   最后一步是发送请求并验证收到的响应代码和正文是否符合预期

### 5.3. 运行测试

最后，当我们运行测试时，我们会看到我们的 HTTP User-Agent标头被记录：

```plaintext
16:07:02.644 [main] INFO  c.b.o.i.SimpleLoggingInterceptor - Intercepted headers: User-Agent: A Baeldung Reader
 from URL: http://localhost:54769/greeting
```

### 5.4. 使用内置的HttpLoggingInterceptor

尽管我们的日志记录拦截器很好地演示了我们如何定义拦截器，但值得一提的是 OkHttp 有一个我们可以利用的内置记录器。

为了使用这个记录器，我们需要一个额外的[Maven 依赖项](https://search.maven.org/classic/#search|ga|1|g%3A"com.squareup.okhttp3" AND a%3A"logging-interceptor")：

```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>logging-interceptor</artifactId>
    <version>4.9.1</version>
</dependency>
```

然后我们可以继续实例化我们的记录器并定义我们感兴趣的日志记录级别：

```java
HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
logger.setLevel(HttpLoggingInterceptor.Level.HEADERS);
```

在此示例中，我们只对查看标头感兴趣。

## 6. 添加自定义响应头

现在我们了解了创建拦截器背后的基础知识。现在让我们看一下另一个典型的用例，我们修改其中一个 HTTP 响应标头。

如果我们想要添加我们自己的专有应用程序 HTTP 标头或重写从我们的服务器返回的标头之一，这将很有用：

```java
public class CacheControlResponeInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder()
          .header("Cache-Control", "no-store")
          .build();
    }
}
```

和以前一样，我们调用chain.proceed方法，但这次没有事先使用请求对象。当响应返回时，我们使用它来创建一个新的响应并将Cache-Control标头设置为no-store。

实际上，我们不太可能希望每次都让浏览器从服务器拉取数据，但我们可以使用这种方法在我们的响应中设置任何标头。

## 7. 使用拦截器进行错误处理

如前所述，我们还可以使用拦截器来封装一些我们希望全局应用于所有请求和响应对象的逻辑，例如错误处理。

假设我们想要在响应不是 HTTP 200 响应时返回带有状态和消息的轻量级 JSON 响应。

考虑到这一点，我们将首先定义一个简单的 bean 来保存错误消息和状态代码：

```java
public class ErrorMessage {

    private final int status;
    private final String detail;

    public ErrorMessage(int status, String detail) {
        this.status = status;
        this.detail = detail;
    }
    
    // Getters and setters
}
```

接下来，我们将创建我们的拦截器：

```java
public class ErrorResponseInterceptor implements Interceptor {
    
    public static final MediaType APPLICATION_JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        
        if (!response.isSuccessful()) {
            Gson gson = new Gson();
            String body = gson.toJson(
              new ErrorMessage(response.code(), "The response from the server was not OK"));
            ResponseBody responseBody = ResponseBody.create(body, APPLICATION_JSON);

            ResponseBody originalBody = response.body();
            if (originalBody != null) {
                originalBody.close();
            }
            
            return response.newBuilder().body(responseBody).build();
        }
        return response;
    }
}
```

很简单，我们的拦截器检查响应是否成功，如果不成功，则创建一个包含响应代码和简单消息的 JSON 响应。请注意，在这种情况下，我们必须记住关闭原始响应的主体，以释放与其关联的任何资源。

```json
{
    "status": 500,
    "detail": "The response from the server was not OK"
}
```

## 8. 网络拦截器

到目前为止，我们介绍的拦截器就是 OkHttp 所说的应用程序拦截器。然而，OkHttp 还支持另一种称为网络拦截器的拦截器。

我们可以用与前面解释的完全相同的方式定义我们的网络拦截器。但是，我们需要在创建 HTTP 客户端实例时调用addNetworkInterceptor方法：

```java
OkHttpClient client = new OkHttpClient.Builder()
  .addNetworkInterceptor(new SimpleLoggingInterceptor())
  .build();
```

应用程序和网络 inceptors 之间的一些重要区别包括：

-   应用程序拦截器总是被调用一次，即使 HTTP 响应来自缓存
-   网络拦截器挂接到网络级别，是放置重试逻辑的理想位置
-   同样，当我们的逻辑不依赖响应的实际内容时，我们应该考虑使用网络拦截器
-   使用网络拦截器可以让我们访问承载请求的连接，包括用于连接到网络服务器的 IP 地址和 TLS 配置等信息。
-   应用程序拦截器无需担心重定向和重试等中间响应
-   相反，如果我们有适当的重定向，网络拦截器可能会被多次调用

正如我们所看到的，这两种选择都有各自的优点。所以这真的取决于我们自己选择的特定用例。

但是，应用程序拦截器通常可以很好地完成这项工作。

## 9.总结

在本文中，我们了解了如何使用 OkHttp 创建拦截器。首先，我们首先解释什么是拦截器，以及如何定义一个简单的日志拦截器来检查我们的 HTTP 请求标头。

然后我们看到了如何在我们的响应对象中设置一个标题和一个不同的主体。最后，我们快速浏览了应用程序拦截器和网络拦截器之间的一些区别。