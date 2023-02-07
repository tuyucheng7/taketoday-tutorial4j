## 1. 概述

在本教程中，我们将研究WebClient，它是Spring 5中引入的响应式Web客户端。

我们还将查看WebTestClient，这是一个设计用于测试的WebClient。

## 2. WebClient是什么

简单地说，WebClient是一个接口，代表了执行Web请求的主要入口点。

它是作为Spring Web Reactive模块的一部分创建的，并将在响应式场景中取代经典的RestTemplate。此外，WebClient是一种响应式、非阻塞的解决方案，可在HTTP/1.1协议上运行。

需要注意的是，尽管它实际上是一个非阻塞客户端并且属于spring-webflux库，但该解决方案同时支持同步和异步操作，使其也适用于在Servlet堆栈上运行的应用程序。

这可以通过阻塞获取结果的操作来实现。当然，如果我们在处理响应式堆栈，则不建议使用这种做法。

最后，该接口有一个实现，即我们将使用的DefaultWebClient类。

## 3. 依赖

由于我们使用的是Spring Boot应用程序，因此我们只需要添加[spring-boot-starter-webflux](https://search.maven.org/search?q=a:spring-boot-starter-webflux)依赖项即可获得Spring框架的响应式Web支持。

### 3.1 Maven

让我们将以下依赖项添加到pom.xml文件中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

### 3.2 Gradle

使用Gradle，我们需要将以下代码添加到build.gradle文件中：

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
}
```

## 4. WebClient的使用

为了正确使用WebClient，我们需要知道如何：

+ 创建实例
+ 发出请求
+ 处理响应

### 4.1 创建WebClient实例

有三个选项可供选择。第一个是使用默认设置创建一个WebClient对象：

```java
WebClient client = WebClient.create();
```

第二个选项是使用给定的基本URI创建WebClient实例：

```java
WebClient client = WebClient.create("http://localhost:8080");
```

第三个选项(也是最高级的一个)是使用DefaultWebClientBuilder类构建客户端，它允许我们完全自定义：

```java
WebClient client = WebClient.builder()
    .baseUrl("http://localhost:8080")
    .defaultCookie("cookieKey", "cookieValue")
    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) 
    .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8080"))
    .build();
```

### 4.2 创建带有超时的WebClient实例

通常情况下，默认的30秒HTTP超时对于我们的需求来说太慢了，要自定义此行为，我们可以创建一个HttpClient实例并配置我们的WebClient来使用它。

我们可以：

+ **通过ChannelOption.CONNECT_TIMEOUT_MILLIS选项设置连接超时**
+ **分别使用ReadTimeoutHandler和WriteTimeoutHandler设置读取和写入超时**
+ **使用responseTimeout方法配置响应超时**

正如我们所说，所有这些都必须在我们将配置的HttpClient实例中指定：

```java
HttpClient httpClient = HttpClient.create()
    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
    .responseTimeout(Duration.ofMillis(5000))
    .doOnConnected(conn -> 
        conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
            .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

WebClient client = WebClient.builder()
    .clientConnector(new ReactorClientHttpConnector(httpClient))
    .build();
```

请注意，**虽然我们也可以在客户端请求上调用timeout，但这是一个信号超时，而不是HTTP连接、读/写或响应超时；这是Mono/Flux发布者的超时**。

### 4.3 准备请求-定义方法

首先，我们需要通过调用method(HttpMethod method)来指定一个请求的HTTP方法：

```java
UriSpec<RequestBodySpec> uriSpec = client.method(HttpMethod.POST);
```

或者调用其快捷方式方法，如get、post和delete：

```java
UriSpec<RequestBodySpec> uriSpec = client.post();
```

注意：虽然看起来我们重用了请求规范变量(WebClient.UriSpec、WebClient.RequestBodySpec、WebClient.RequestHeadersSpec、WebClient.ResponseSpec)，但这只是为了简单地介绍不同的方法。这些指令不应该重复用于不同的请求，它们会检索引用，因此后面的操作会修改我们在前面步骤中所做的定义。

### 4.4 准备请求-定义URL

下一步是提供一个URL。同样，我们有不同的方法来做到这一点。

我们可以将它作为字符串传递给uri API：

```java
RequestBodySpec bodySpec = uriSpec.uri("/resource");
```

使用UriBuilder函数：

```java
RequestBodySpec bodySpec = uriSpec.uri(
    uriBuilder -> uriBuilder.pathSegment("/resource").build());
```

或者作为java.net.URL实例：

```java
RequestBodySpec bodySpec = uriSpec.uri(URI.create("/resource"));
```

请记住，如果我们为WebClient定义了默认的基本URL，则最后一个方法将覆盖此值。

### 4.5 准备请求-定义正文

然后我们可以根据需要设置请求正文、内容类型、长度、cookie或header。

例如，如果我们想设置请求正文，有几种可用的方法。可能最常见和最直接的选择是使用bodyValue方法：

```java
RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue("data");
```

或者通过向body方法传递Publisher(以及将要发布的元素的类型)：

```java
RequestHeadersSpec<?> headersSpec = bodySpec.body(
    Mono.just(new Foo("name")), Foo.class);
```

或者，我们可以使用BodyInserters工具类。例如，让我们看看我们如何使用一个简单的对象来填充请求正文，就像我们使用bodyValue方法所做的那样：

```java
RequestHeadersSpec<?> headersSpec = bodySpec.body(
    BodyInserters.fromValue("data"));
```

类似地，如果我们使用Reactor实例，则可以使用BodyInserters#fromPublisher方法：

```java
RequestHeadersSpec headersSpec = bodySpec.body(
    BodyInserters.fromPublisher(Mono.just("data")),
    String.class);
```

该类还提供了其他直观的函数来涵盖更高级的场景。例如，如果我们必须发送multipart请求：

```java
LinkedMultiValueMap map = new LinkedMultiValueMap();
map.add("key1", "value1");
map.add("key2", "value2");
RequestHeadersSpec<?> headersSpec = bodySpec.body(
    BodyInserters.fromMultipartData(map));
```

所有这些方法都会创建一个BodyInserter实例，然后我们可以将其看作为请求的主体。

**BodyInserter是一个接口，负责使用给定的输出消息和插入期间使用的上下文填充ReactiveHttpOutputMessage正文**。

Publisher是一个响应式组件，负责提供可能无限数量的有序元素。它也是一个接口，最常用的实现是Mono和Flux。

### 4.6 准备请求-定义标头

设置好请求正文后，我们可以设置标头、cookie和媒体类型。**值将添加到实例化客户端时已设置的值中**。

此外，还支持最常用的标头，例如“If-None-Match”、“If-Modified-Since”、“Accept”和“Accept-Charset”。

以下是如何使用这些值的示例：

```java
ResponseSpec responseSpec = headersSpec.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
    .acceptCharset(StandardCharsets.UTF_8)
    .ifNoneMatch("*")
    .ifModifiedSince(ZonedDateTime.now())
    .retrieve();
```

### 4.7 获取响应

最后一个阶段是发送请求并接收响应。我们可以通过使用exchangeToMono/exchangeToFlux或retrieve方法来实现这一点。

exchangeToMono和exchangeToFlux方法允许访问ClientResponse及其状态和标头：

```java
Mono<String> response = headersSpec.exchangeToMono(response -> {
    if (response.statusCode().equals(HttpStatus.OK)) {
        return response.bodyToMono(String.class);
    } else if (response.statusCode().is4xxClientError()) {
        return Mono.just("Error response");
    } else {
        return response.createException()
            .flatMap(Mono::error);
    }
});
```

而retrieve方法是直接获取正文的最快捷方式：

```java
Mono<String> response = headersSpec.retrieve().bodyToMono(String.class);
```

请务必注意ResponseSpec.bodyToMono方法很重要，如果状态码为4xx(客户端错误)或5xx(服务器错误)，该方法将抛出WebClientException。

## 5. WebTestClient的使用

WebTestClient是测试WebFlux服务器端点的主要入口点。它具有与WebClient非常相似的API，它将大部分工作委托给内部的WebClient实例，主要专注于提供测试上下文。DefaultWebTestClient类是一个单一的接口实现。

用于测试的客户端可以绑定到真实的服务器，也可以使用特定的控制器或函数。

### 5.1 绑定到服务器

要完成对正在运行的服务器的实际请求的端到端集成测试，我们可以使用bindToServer方法：

```java
WebTestClient testClient = WebTestClient
    .bindToServer()
    .baseUrl("http://localhost:8080")
    .build();
```

### 5.2 绑定到路由

我们可以通过将特定的RouterFunction传递给bindToRouterFunction方法来测试它：

```java
RouterFunction function = RouterFunctions.route(
    RequestPredicates.GET("/resource"),
    request -> ServerResponse.ok().build()
);

WebTestClient
    .bindToRouterFunction(function)
    .build().get().uri("/resource")
    .exchange()
    .expectStatus().isOk()
    .expectBody().isEmpty();
```

### 5.3 绑定到Web处理函数

使用bindToWebHandler方法可以实现相同的行为，该方法接收WebHandler实例：

```java
WebHandler handler = exchange -> Mono.empty();
WebTestClient.bindToWebHandler(handler).build();
```

### 5.4 绑定到应用程序上下文

当我们使用bindToApplicationContext方法时，会出现更有趣的情况。它需要一个ApplicationContext并分析控制器Bean和@EnableWebFlux配置的上下文。

如果我们注入ApplicationContext的实例，一个简单的代码片段可能如下所示：

```java
@Autowired
private ApplicationContext context;

WebTestClient testClient = WebTestClient
    .bindToApplicationContext(context)
    .build();
```

### 5.5 绑定到控制器

一种更快捷的方法是提供一个我们想要通过bindToController方法测试的控制器数组。假设我们有一个Controller类，并将其注入到所需的类中，我们可以这样写：

```java
@Autowired
private Controller controller;

WebTestClient testClient = WebTestClient
    .bindToController(controller)
    .build();
```

### 5.6 发送请求

构建WebTestClient对象后，链中的所有后续操作都将类似于WebClient，直到exchange方法(一种获取响应的方法)，它提供了WebTestClient.ResponseSpec接口以使用有用的方法，如expectStatus、expectBody，和expectHeader：

```java
WebTestClient
    .bindToServer()
        .baseUrl("http://localhost:8080")
        .build()
        .post()
        .uri("/resource")
    .exchange()
        .expectStatus().isCreated()
        .expectHeader().valueEquals("Content-Type", "application/json")
        .expectBody().jsonPath("field").isEqualTo("value");
```

## 5. 总结

在本文中，我们探讨了WebClient，这是Spring 5引入的全新编程模型API，用于在客户端发出请求。

我们还通过配置客户端、准备请求和处理响应来说明它的具体使用。