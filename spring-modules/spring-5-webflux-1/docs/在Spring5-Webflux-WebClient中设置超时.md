## 1. 概述

Spring 5添加了一个全新的框架[Spring WebFlux](https://www.baeldung.com/spring-webflux)，它支持我们的Web应用程序中的响应式编程。要执行HTTP请求，我们可以使用WebClient接口，该接口提供了基于[Reactor](https://www.baeldung.com/reactor-core)的函数式API。

在本教程中，我们将重点介绍**WebClient的超时设置**。我们将讨论不同的方法，如何正确设置不同的超时，包括整个应用程序中的全局和特定于请求的超时。

## 2. WebClient和HTTP客户端

在我们继续之前，让我们快速回顾一下。Spring WebFlux包含自己的客户端[WebClient](https://www.baeldung.com/spring-5-webclient)类，以响应式方式执行HTTP请求。WebClient还需要一个HTTP客户端库才能正常工作，Spring为其中一些提供了[内置支持](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-client)，但默认情况下使用[Reactor Netty](https://github.com/reactor/reactor-netty)。

大多数配置(包括超时)，都可以使用这些客户端完成。

## 3. 通过HTTP客户端配置超时

如前所述，在我们的应用程序中设置不同的WebClient超时的最简单方法是**使用底层HTTP客户端全局设置它们**，这也是最有效的方法。

由于Netty是Spring WebFlux的默认客户端库，我们将使用[Reactor Netty HttpClient](https://projectreactor.io/docs/netty/release/reference/index.html#http-client)类来介绍我们的示例。

### 3.1 响应超时

响应超时是我们在**发送请求后等待接收响应的时间**，我们可以使用[responseTimeout()](https://projectreactor.io/docs/netty/release/api/reactor/netty/http/client/HttpClient.html#responseTimeout-java.time.Duration-)方法为客户端配置它：

```java
HttpClient client = HttpClient.create()
    .responseTimeout(Duration.ofSeconds(1)); 
```

在本例中，我们将超时时间配置为1秒。Netty默认情况下不会设置响应超时。

之后，我们可以将HttpClient提供给Spring WebClient：

```java
WebClient webClient = WebClient.builder()
    .clientConnector(new ReactorClientHttpConnector(httpClient))
    .build();
```

完成此操作后，WebClient将继承底层HttpClient为所有发送的请求提供的所有配置。

### 3.2 连接超时

连接超时是必须**在客户端和服务器之间建立连接的时间段**，我们可以使用不同的ChannelOption key和[option()](https://projectreactor.io/docs/netty/release/api/reactor/netty/transport/Transport.html#option-io.netty.channel.ChannelOption-O-)方法来执行配置：

```java
HttpClient client = HttpClient.create()
    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
// create WebClient...
```

提供的值以毫秒为单位，因此我们将超时时间配置为10秒。默认情况下，Netty将该值设置为30秒。

此外，我们可以配置keep-alive选项，该选项会在连接空闲时发送TCP检查探针：

```java
HttpClient client = HttpClient.create()
    .option(ChannelOption.SO_KEEPALIVE, true)
    .option(EpollChannelOption.TCP_KEEPIDLE, 300)
    .option(EpollChannelOption.TCP_KEEPINTVL, 60)
    .option(EpollChannelOption.TCP_KEEPCNT, 8);
// create WebClient...
```

因此，我们启用了keep-alive检查，以便在空闲5分钟后以60秒的间隔进行探测。我们还将连接断开前的最大探测数设置为8。

**当在给定时间内未建立连接或未断开连接时，将引发**[ConnectTimeoutException](https://netty.io/4.1/api/io/netty/channel/ConnectTimeoutException.html)。

### 3.3 读写超时

当在特定时间段内**没有读取数据时发生读取超时**，而当写入操作**无法在特定时间完成时发生写入超时**。HttpClient允许配置额外的处理程序来配置这些超时：

```java
HttpClient client = HttpClient.create()
    .doOnConnected(conn -> conn
        .addHandler(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
        .addHandler(new WriteTimeoutHandler(10)));
// create WebClient...
```

在这种情况下，我们通过[doOnConnected()](https://projectreactor.io/docs/netty/release/api/reactor/netty/transport/ClientTransport.html#doOnConnect-java.util.function.Consumer-)方法配置了一个连接的回调，我们在其中创建了额外的处理程序。为了配置超时，我们添加了[ReadTimeOutHandler](https://netty.io/4.1/api/io/netty/handler/timeout/ReadTimeoutHandler.html)和[WriteTimeOutHandler](https://netty.io/4.1/api/io/netty/handler/timeout/WriteTimeoutHandler.html)实例，我们将它们都设置为10秒。

这些处理程序的构造函数有两种重载。对于第一种，我们提供时间量和单位，而第二种将给定的数字转换为秒。

**底层的Netty库相应地提供**[ReadTimeoutException](https://netty.io/4.1/api/io/netty/handler/timeout/ReadTimeoutException.html)**和**[WriteTimeoutException](https://netty.io/4.1/api/io/netty/handler/timeout/WriteTimeoutException.html)**类来处理错误**。

### 3.4 SSL/TLS超时

握手超时是系统**在停止操作之前尝试建立SSL连接的持续时间**，我们可以通过[secure()](https://projectreactor.io/docs/netty/release/api/reactor/netty/http/client/HttpClient.html#secure--)方法设置SSL配置：

```java
HttpClient.create()
    .secure(spec -> spec.sslContext(SslContextBuilder.forClient())
        .defaultConfiguration(SslProvider.DefaultConfigurationType.TCP)
        .handshakeTimeout(Duration.ofSeconds(30))
        .closeNotifyFlushTimeout(Duration.ofSeconds(10))
        .closeNotifyReadTimeout(Duration.ofSeconds(10)));
// create WebClient...
```

如上所述，我们将握手超时设置为30秒(默认10秒)，而close_notify flush(默认3秒)和read(默认0秒)超时设置为10秒。所有方法都由[SslProvider.Builder](https://projectreactor.io/docs/netty/release/api/reactor/netty/tcp/SslProvider.Builder.html)接口提供。

**当握手由于配置的超时而失败时，将使用**[SslHandshakeTimeoutException](https://netty.io/4.1/api/io/netty/handler/ssl/SslHandshakeTimeoutException.html)。

### 3.5 代理超时

HttpClient还支持代理功能，**如果与对等方的连接尝试未在代理超时内完成，则连接尝试失败**。我们在[proxy()](https://projectreactor.io/docs/netty/release/api/reactor/netty/transport/ClientTransport.html#proxy-java.util.function.Consumer-)配置中设置这个超时：

```java
HttpClient.create()
    .proxy(spec -> spec.type(ProxyProvider.Proxy.HTTP)
        .host("proxy")
        .port(8080)
        .connectTimeoutMillis(30000));
// create WebClient...
```

我们使用[connectTimeoutMillis()](https://projectreactor.io/docs/netty/release/api/reactor/netty/transport/ProxyProvider.Builder.html#connectTimeoutMillis-long-)将超时设置为30秒，默认值为10。

**Netty库还实现了自己的**[ProxyConnectException](https://netty.io/4.1/api/io/netty/handler/proxy/ProxyConnectException.html)**，以防出现任何失败。**

## 4. 请求级别超时

在上一节中，我们使用HttpClient全局配置了不同的超时时间。但是，我们也可以**独立于全局设置来设置特定于响应请求的超时**。

### 4.1 响应超时–使用HttpClientRequest

如前所述，我们也可以**在请求级别配置响应超时**：

```java
webClient.get()
    .uri("https://tuyucheng.com/path")
    .httpRequest(httpRequest -> {
        HttpClientRequest reactorRequest = httpRequest.getNativeRequest();
        reactorRequest.responseTimeout(Duration.ofSeconds(2));
    });
```

在上面的例子中，我们使用WebClient的[httpRequest()](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/client/WebClient.RequestHeadersSpec.html#httpRequest-java.util.function.Consumer-)方法从底层Netty库访问原生[HttpClientRequest](https://projectreactor.io/docs/netty/release/api/reactor/netty/http/client/HttpClientRequest.html)。接下来，我们使用它将超时值设置为2秒。

这种响应超时设置会**覆盖HttpClient级别上的任何响应超时**，我们还可以将此值设置为null，以删除任何先前配置的值。

### 4.2 反应超时-使用Reactor

Reactor Netty使用Reactor Core作为其Reactive Streams实现。**要配置另一个超时，我们可以使用Mono和Flux发布者提供的**[timeout()](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#timeout-java.time.Duration-)**运算符**：

```java
webClient.get()
    .uri("https://tuyucheng.com/path")
    .retrieve()
    .bodyToFlux(JsonNode.class)
    .timeout(Duration.ofSeconds(5));
```

在这种情况下，**如果在给定的5秒内没有元素到达，则会出现TimeoutException**。

请记住，最好使用Reactor Netty中提供的更具体的超时配置选项，因为它们为特定目的和用例提供了更多控制。

**timeout()方法适用于整个操作，从建立与远程对等方的连接到接收响应，它不会覆盖任何与HttpClient相关的设置**。

## 5. 异常处理

我们刚刚了解了不同的超时配置，现在是时候快速讨论异常处理了。**每种类型的超时都会提供一个专用的异常，因此我们可以使用Reactive Streams和onError操作轻松处理它们**：

```java
webClient.get()
    .uri("https://tuyucheng.com/path")
    .retrieve()
    .bodyToFlux(JsonNode.class)
    .timeout(Duration.ofSeconds(5))
    .onErrorMap(ReadTimeoutException.class, ex -> new HttpTimeoutException("ReadTimeout"))
    .onErrorReturn(SslHandshakeTimeoutException.class, new TextNode("SslHandshakeTimeout"))
    .doOnError(WriteTimeoutException.class, ex -> log.error("WriteTimeout"))
    ...
```

我们可以重用前面描述的任何异常，并使用Reactor编写我们自己的处理方法。

此外，**我们还可以根据HTTP状态码添加一些逻辑**：

```java
webClient.get()
    .uri("https://baeldung.com/path")
    .onStatus(HttpStatus::is4xxClientError, resp -> {
        log.error("ClientError {}", resp.statusCode());
        return Mono.error(new RuntimeException("ClientError"));
    })
    .retrieve()
    .bodyToFlux(JsonNode.class)
    ...
```

## 6. 总结

在本教程中，我们学习了如何使用Netty示例在Spring WebFlux中的WebClient上配置超时。

我们快速讨论了不同的超时以及在HttpClient级别正确设置它们的方法，以及如何将它们应用于我们的全局设置。然后，我们使用单个请求在特定于请求的级别配置响应超时。最后，我们说明了处理发生的异常的不同方法。