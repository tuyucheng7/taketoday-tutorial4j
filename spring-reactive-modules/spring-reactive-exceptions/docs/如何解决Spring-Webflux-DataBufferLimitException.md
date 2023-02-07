## 1. 简介

在本教程中，我们将探讨为什么我们可能会在[Spring Webflux](https://www.baeldung.com/spring-webflux)应用程序中看到DataBufferLimitException。然后，我们将看看我们可以解决相同问题的各种方法。

## 2. 理解问题

在说明到解决方案之前，让我们先了解问题。

### 2.1 什么是DataBufferLimitException？

Spring WebFlux[限制](https://docs.spring.io/spring-framework/docs/5.2.6.RELEASE/spring-framework-reference/web-reactive.html#webflux-codecs-limits)编解码器中内存中数据的缓冲，以避免应用程序内存问题。**默认情况下，这配置为262144字节**。当这对我们的用例来说还不够时，我们最终会得到DataBufferLimitException。

### 2.2 什么是编解码器？

spring-web和spring-core模块通过具有响应流背压的非阻塞I/O支持将字节内容序列化和反序列化到更高级别的对象或从更高级别的对象中反序列化。[编解码器](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-codecs)提供了Java序列化的替代方法。一个[优点](https://docs.spring.io/spring-integration/reference/html/codec.html#codec)是，通常，对象不需要实现Serializable。  

## 3. 服务器端

让我们首先从服务器的角度看一下DataBufferLimitException是如何发挥作用的。

### 3.1 重现问题

让我们尝试将大小为390KB的JSON有效负载发送到我们的Spring Webflux服务器应用程序以创建异常。我们将使用curl命令向我们的服务器发送POST请求：

```bash
curl --location --request POST 'http://localhost:8080/1.0/process' \
  --header 'Content-Type: application/json' \
  --data-binary '@/tmp/390KB.json'
```

正如我们所看到的，抛出了DataBufferLimitException：

```shell
org.springframework.core.io.buffer.DataBufferLimitException: Exceeded limit on max bytes to buffer : 262144
  at org.springframework.core.io.buffer.LimitedDataBufferList.raiseLimitException(LimitedDataBufferList.java:99) ~[spring-core-5.3.23.jar:5.3.23]
  Suppressed: reactor.core.publisher.FluxOnAssembly$OnAssemblyException: 
Error has been observed at the following site(s):
  *__checkpoint ⇢ HTTP POST "/1.0/process" [ExceptionHandlingWebHandler]
```

### 3.2 通过属性解决

最简单的解决方案是**配置应用程序属性spring.codec.max-in-memory-size**。让我们将以下内容添加到我们的application.yaml文件中：

```yaml
spring:
    codec:
        max-in-memory-size: 500KB
```

这样，我们现在应该能够在我们的应用程序中缓冲大于500KB的有效负载。

### 3.3 通过代码解决

或者，我们可以使用WebFluxConfigurer接口来配置相同的阈值。为此，我们将添加一个新的配置类WebFluxConfiguration：

```java
@Configuration
public class WebFluxConfiguration implements WebFluxConfigurer {
    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(500 * 1024);
    }
}
```

这种方法也将为我们提供相同的结果。

## 4. 客户端

现在让我们换个角度来看看客户端的行为。

### 4.1 重现问题

我们将尝试使用Webflux的WebClient重现相同的行为。让我们创建一个调用服务器的处理程序，有效负载为390KB：

```java
public Mono<Users> fetch() {
    return webClient
        .post()
        .uri("/1.0/process")
        .body(BodyInserters.fromPublisher(readRequestBody(), Users.class))
        .exchangeToMono(clientResponse -> clientResponse.bodyToMono(Users.class));
}
```

我们再次看到抛出了相同的异常，但这次是由于webClient试图发送比允许的更大的有效负载：

```shell
org.springframework.core.io.buffer.DataBufferLimitException: Exceeded limit on max bytes to buffer : 262144
  at org.springframework.core.io.buffer.LimitedDataBufferList.raiseLimitException(LimitedDataBufferList.java:99) ~[spring-core-5.3.23.jar:5.3.23]
  Suppressed: reactor.core.publisher.FluxOnAssembly$OnAssemblyException: 
Error has been observed at the following site(s):
    *__checkpoint ⇢ Body from POST http://localhost:8080/1.0/process [DefaultClientResponse]
    *__checkpoint ⇢ Handler cn.tuyucheng.taketoday.spring.reactive.springreactiveexceptions.handler.TriggerHandler@428eedd9 [DispatcherHandler]
    *__checkpoint ⇢ HTTP POST "/1.0/trigger" [ExceptionHandlingWebHandler]
```

### 4.2 通过属性解决

同样，最简单的解决方案是**配置应用程序属性spring.codec.max-in-memory-size**。让我们将以下内容添加到我们的application.yaml文件中：

```yaml
spring:
    codec:
        max-in-memory-size: 500KB
```

这样，我们现在应该能够从我们的应用程序发送大于500KB的有效负载。值得注意的是，此配置应用于整个应用程序，这意味着所有Web客户端和服务器本身。

因此，如果我们只想为特定的Web客户端配置此限制，那么这将不是一个理想的解决方案。此外，这种方法还有一个[警告](https://github.com/spring-projects/spring-boot/issues/27836)。用于创建WebClient的构建器必须由Spring自动注入，如下所示：

```java
@Bean("webClient")
public WebClient getSelfWebClient(WebClient.Builder builder) {
    return builder.baseUrl(host).build();
}
```

### 4.3 通过代码解决

我们还有一种编程方式来配置Web客户端以实现此目标。让我们创建一个具有以下配置的WebClient：

```java
@Bean("progWebClient")
public WebClient getProgSelfWebClient() {
	return WebClient
		.builder()
		.baseUrl(host)
		.exchangeStrategies(ExchangeStrategies
			.builder()
			.codecs(codecs -> codecs
				.defaultCodecs()
				.maxInMemorySize(500 * 1024))
			.build())
		.build();
}
```

这样，我们现在应该能够使用我们的Web客户端成功发送大于500KB的有效载荷。

## 5. 总结

在本文中，我们了解了DataBufferLimitException是什么，并研究了如何在服务器端和客户端修复它们。我们研究了两种方法，首先是基于属性配置，其次基于编程方式。我们希望这个异常不会再给你带来麻烦。