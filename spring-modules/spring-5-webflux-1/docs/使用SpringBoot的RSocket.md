## 1. 概述

RSocket是一种提供Reactive Streams语义的应用程序协议，例如，它可以作为HTTP的替代品。

在本教程中，我们将研究在[Spring Boot](https://www.baeldung.com/spring-boot-start)中使用[RSocket](https://www.baeldung.com/rsocket)，特别是它如何帮助抽象出较低级别的RSocket API。

## 2. 依赖关系

让我们从添加[spring-boot-starter-rsocket](https://mvnrepository.com/search?q=spring-boot-starter-rsocket)依赖项开始：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-rsocket</artifactId>
</dependency>
```

这将传递地引入RSocket相关的依赖项，例如[rsocket-core](https://mvnrepository.com/search?q=rsocket-core)和[rsocket-transport-netty](https://mvnrepository.com/search?q=rsocket-transport-netty)。

## 3. 示例应用程序

现在我们开始编写代码，为了突出RSocket提供的交互模型，我们将创建一个交易应用程序：由一个客户端和一个服务器组成。

### 3.1 服务器设置

首先，让我们设置服务器，它将是一个启动RSocket服务器的Spring Boot应用程序。

**由于我们添加了**[spring-boot-starter-rsocket](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-rsocket/2.2.0.M2)**依赖项，Spring Boot为我们自动配置了一个RSocket服务器**。与Spring Boot一样，我们可以以属性驱动的方式更改RSocket服务器的默认配置值。

例如，让我们通过在application.properties文件中添加以下配置来更改RSocket服务器的端口：

```properties
spring.rsocket.server.port=7000
```

我们还可以根据需要更改[其他属性](https://docs.spring.io/spring-boot/docs/2.2.0.M2/reference/html/appendix.html#rsocket-properties)以进一步修改我们的服务器。

### 3.2 客户端设置

接下来，让我们设置客户端，它也是一个Spring Boot应用程序。

虽然Spring Boot会自动配置大多数与RSocket相关的组件，但我们也需要定义一些bean来完成设置：

```java
@Configuration
public class ClientConfiguration {

    @Bean
    public RSocketRequester getRSocketRequester() {
        RSocketRequester.Builder builder = RSocketRequester.builder();

        return builder
              .rsocketConnector(
                    rSocketConnector ->
                          rSocketConnector.reconnect(
                                Retry.fixedDelay(2, Duration.ofSeconds(2))
                          )
              )
              .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
              .tcp("localhost", 7000);
    }
}
```

在这里，我们将创建RSocket客户端并将其配置为在端口7000上使用TCP传输。请注意，这是我们之前配置的服务器端口。

在定义了这个bean配置之后，我们就有了一个基本结构。

接下来，**我们将探索不同的交互模型**，看看Spring Boot如何实现这些。

## 4. RSocket和Spring Boot请求/响应

让我们从请求/响应开始说起，**这可能是最常见和最熟悉的交互模型，因为HTTP也采用这种类型的通信**。

在这种交互模型中，客户端启动通信并发送请求。之后，服务器执行操作并向客户端返回响应，从而完成通信。

在我们的交易应用程序中，客户将询问给定股票的当前市场数据。作为回报，服务器将传递请求的数据。

### 4.1 服务端

在服务器端，我们应该首先创建一个控制器来保存我们的处理程序方法。但是，我们将使用@MessageMapping注解，**而不是像Spring MVC中那样使用**[@RequestMapping](https://www.baeldung.com/spring-requestmapping)**或**[@GetMapping](https://www.baeldung.com/spring-new-requestmapping-shortcuts)**注解**：

```java
@Controller
public class MarketDataRSocketController {

    private final MarketDataRepository marketDataRepository;

    public MarketDataRSocketController(MarketDataRepository marketDataRepository) {
        this.marketDataRepository = marketDataRepository;
    }

    @MessageMapping("currentMarketData")
    public Mono<MarketData> currentMarketData(MarketDataRequest marketDataRequest) {
        return marketDataRepository.getOne(marketDataRequest.getStock());
    }
}
```

我们使用@Controller注解来定义一个处理程序，该处理程序应该处理传入的RSocket请求。此外，@MessageMapping注解允许我们可以定义我们感兴趣的路由以及如何对请求做出响应。

在这种情况下，服务器监听currentMarketData路由，该路由**将单个结果作为Mono<MarketData\>返回给客户端**。

### 4.2 客户端

接下来，我们的RSocket客户端应该询问股票的当前价格并得到单个响应。

要发起请求，我们应该使用RSocketRequester类：

```java
@RestController
public class MarketDataRestController {

    private final RSocketRequester rSocketRequester;

    public MarketDataRestController(RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }

    @GetMapping(value = "/current/{stock}")
    public Publisher<MarketData> current(@PathVariable("stock") String stock) {
        return rSocketRequester
              .route("currentMarketData")
              .data(new MarketDataRequest(stock))
              .retrieveMono(MarketData.class);
    }
}
```

请注意，在我们的例子中，RSocket客户端也是一个REST控制器，我们从中调用我们的RSocket服务器。所以，我们使用@RestController和@GetMapping来定义我们的请求/响应端点。

在控制器方法中，我们使用RSocketRequester并指定路由。事实上，这正是RSocket服务器所期望的路由。然后我们传递请求数据，最后，**当我们调用retrieveMono()方法时，Spring Boot会发起请求/响应交互**。

## 5. RSocket和Spring Boot即发即弃

接下来，我们介绍“即发即弃(fire-and-forget)”交互模型。顾名思义，客户端向服务器发送请求，但不期望得到响应。

在我们的交易应用程序中，一些客户端将充当数据源，并将市场数据推送到服务器。

### 5.1 服务端

让我们在服务器应用程序中创建另一个端点：

```java
@MessageMapping("collectMarketData")
public Mono<Void> collectMarketData(MarketData marketData) {
    marketDataRepository.add(marketData);
    return Mono.empty();
}
```

同样，我们使用路由值collectMarketData定义了一个新的@MessageMapping。此外，Spring Boot会自动将传入的有效负载转换为MarketData实例。

但是，这里最大的区别是**我们返回一个Mono<Void\>，因为客户端不需要我们的响应**。

### 5.2 客户端

让我们看看如何启动即发即弃请求。

我们将创建另一个REST端点：

```java
@GetMapping(value = "/collect")
public Publisher<Void> collect() {
    return rSocketRequester
            .route("collectMarketData")
            .data(getMarketData())
            .send();
}

private MarketData getMarketData() {
    return new MarketData("X", random.nextInt(10));
}
```

在这里，我们指定了路由，我们的有效负载将是一个MarketData实例。**由于我们使用send()方法而不是retrieveMono()方法来发起请求，因此交互模型变成了即发即弃**。

## 6. RSocket和Spring Boot请求流

请求流式处理是一种更复杂的交互模型，其中客户端发送请求，但随着时间的推移从服务器获得多个响应。

为了模拟这种交互模型，客户将询问给定股票的所有市场数据。

### 6.1 服务端

我们添加另一个消息映射方法：

```java
@MessageMapping("feedMarketData")
public Flux<MarketData> feedMarketData(MarketDataRequest marketDataRequest) {
    return marketDataRepository.getAll(marketDataRequest.getStock());
}
```

如我们所见，这个处理程序方法与其他方法非常相似。不同的是，**我们返回一个Flux<MarketData\>而不是Mono<MarketData\>**。最后，我们的RSocket服务器会向客户端发送多个响应。

### 6.2 客户端

在客户端，我们应该创建一个端点来启动我们的请求/流通信：

```java
@GetMapping(value = "/feed/{stock}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Publisher<MarketData> feed(@PathVariable("stock") String stock) {
    return rSocketRequester
          .route("feedMarketData")
          .data(new MarketDataRequest(stock))
          .retrieveFlux(MarketData.class);
}
```

首先，我们定义路由和请求负载。**然后，我们使用retrieveFlux()方法调用来定义我们的响应期望**，这是决定交互模型的部分。

还要注意，由于我们的客户端也是REST服务器，因此它将响应媒体类型定义为MediaType.TEXT_EVENT_STREAM_VALUE。

## 7. 异常处理

现在让我们看看如何以声明的方式处理服务器应用程序中的异常。

在执行请求/响应时，我们可以简单地使用@MessageExceptionHandler注解：

```java
@MessageExceptionHandler
public Mono<MarketData> handleException(Exception e) {
    return Mono.just(MarketData.fromException(e));
}
```

在这里，我们用@MessageExceptionHandler标注了我们的异常处理方法。因此，它将处理所有类型的异常，因为Exception是所有其他异常类的父类。

我们可以更具体一些，为不同的异常类型创建不同的异常处理方法。

这当然是针对请求/响应模型的，因此我们返回了一个Mono<MarketData\>，我**们希望这里的返回类型与我们的交互模型的返回类型相匹配**。

## 8. 总结

在本教程中，我们介绍了Spring Boot的RSocket支持，并详细介绍了RSocket提供的不同交互模型。