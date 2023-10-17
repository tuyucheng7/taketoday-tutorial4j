## 1. 概述

在本教程中，我们将探索如何在 Spring Framework 6 中使用[RSocket ](https://www.baeldung.com/spring-boot-rsocket)*。*

**随着 Spring Framework 版本 6 中引入声明性 RSocket 客户端，使用[RSocket变得更加简单](https://www.baeldung.com/rsocket)\*。\*此功能消除了对重复样板代码的需要，使开发人员能够更高效、更有效地使用 RSocket。**

## 2.Maven依赖

我们首先在首选 IDE 中创建一个 Spring Boot 项目，并将[*spring-boot-starter-rsocket*](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-rsocket)依赖项添加到*pom.xml*文件中：

```xml
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-rsocket</artifactId>
    <version>3.1.4</version>
</dependency> 
复制
```

## 3. 创建RSocket服务器

首先，我们将创建一个使用控制器来管理传入请求的响应程序：

```java
@MessageMapping("MyDestination")
public Mono<String> message(Mono<String> input) {
    return input.doOnNext(msg -> System.out.println("Request is:" + msg + ",Request!"))
      .map(msg -> msg + ",Response!");
}复制
```

*此外，我们将在application.properties*文件中添加以下属性，以使服务器能够通过MyDestination 侦听端口*7000* *：*

```yaml
spring.rsocket.server.port=7000复制
```

## 4. 客户端代码

现在，我们需要开发客户端代码。为了简单起见，我们将在同一项目中但在单独的包中创建客户端代码。事实上，它们必须处于一个独特的项目中。

首先，让我们创建客户端界面：

```java
public interface MessageClient {

    @RSocketExchange("MyDestination")
    Mono<String> sendMessage(Mono<String> input);
}
复制
```

当使用我们的客户端接口时，**我们使用\*@RSocketExchange\*来显示 RSocket 端点。基本上，这只是意味着我们需要一些信息来建立端点路径。我们可以通过分配共享路径在接口级别实现这一点**。它非常简单，可以帮助我们知道我们想要使用哪个端点。

## 5. 测试

*每个 Spring Boot 项目都包含一个用@SpringBootApplication*注解的类。该类在项目加载时运行。因此，我们可以使用这个类并添加一些bean来测试场景。

### 5.1. 创建*RSocketServiceProxyFactory* Bean

首先，我们需要创建一个 bean 来生成*RSocketServiceProxyFactory*。

该工厂负责创建 RSocket 服务接口的代理实例。它处理这些代理的创建，并通过指定服务器将接收传入连接的主机和端口来建立与 RSocket 服务器的必要连接：

```java
@Bean
public RSocketServiceProxyFactory getRSocketServiceProxyFactory(RSocketRequester.Builder requestBuilder) {
    RSocketRequester requester = requestBuilder.tcp("localhost", 7000);
    return RSocketServiceProxyFactory.builder(requester).build();
}复制
```

### 5.2. 创建消息客户端

然后，我们将创建一个负责生成客户端接口的*Bean* **：**

```java
@Bean
public MessageClient getClient(RSocketServiceProxyFactory factory) {
    return factory.createClient(MessageClient.class);
}复制
```

### 5.3. 创建 Runner *Bean*

最后，让我们创建一个使用*MessageClient*实例从服务器发送和接收消息的运行程序 bean：

```typescript
@Bean
public ApplicationRunner runRequestResponseModel(MessageClient client) {
    return args -> {
        client.sendMessage(Mono.just("Request-Response test "))
          .doOnNext(message -> {
              System.out.println("Response is :" + message);
          })
          .subscribe();
    };
}复制
```

### 5.4. 检测结果

当我们通过命令行运行我们的Spring Boot项目时，会显示以下结果：

```powershell
>>c.b.r.responder.RSocketApplication : Started 
>>RSocketApplication in 1.127 seconds (process running for 1.398)
>>Request is:Request-Response test ,Request!
>>Response is :Request-Response test ,Response!复制
```

## 6.RSocket交互模型

RSocket 是一种二进制协议，用于创建快速且响应灵敏的分布式应用程序。它提供了用于在服务器和客户端之间交换数据的不同通信模式。

通过这些交互模型，开发人员可以设计满足数据流、待办事项和应用程序行为的特定要求的系统。

RSocket 有四种主要的交互模型可用。**这些方法之间的主要区别在于输入和输出的基数。**

### 6.1. 请求-响应

在这种方法中，每个请求都会收到一个响应。因此，我们使用基数为 1 的[*Mono*](https://www.baeldung.com/reactor-core)请求，并收到基数相同的*Mono响应。*

到目前为止，本文中的所有代码都基于请求-响应模型。

### 6.2. 请求流

当我们订阅时事通讯时，我们会从服务器定期收到更新流。当客户端发出初始请求时，服务器会发送数据流作为响应。

请求可以是*Mono*或*Void*，但响应将始终是[*Flux*](https://www.baeldung.com/java-reactor-flux-vs-mono)：

```java
@MessageMapping("Counter")
public Flux<String> Counter() {
    return Flux.range(1, 10)
      .map(i -> "Count is: " + i);
}复制
```

### 6.3. 一劳永逸

当我们通过邮件寄出一封信时，我们通常只是将其放入邮箱中，并不期望收到回复。类似地，在“即发即弃”上下文中，响应可以为*null*或单个*Mono*：

```java
@MessageMapping("Warning")
public Mono<Void> Warning(Mono<String> error) {
    error.doOnNext(e -> System.out.println("warning is :" + e))
      .subscribe();
    return Mono.empty();
}复制
```

### 6.4. 渠道

想象一下一个允许双向通信的对讲机，双方可以同时说和听，就像进行对话一样。这种类型的通信依赖于发送和接收数据*Flux*：

```java
@MessageMapping("channel")
public Flux<String> channel(Flux<String> input) {
    return input.doOnNext(i -> {
          System.out.println("Received message is : " + i);
      })
      .map(m -> m.toUpperCase())
      .doOnNext(r -> {
          System.out.println("RESPONSE IS :" + r);
      });
}复制
```

## 七、结论

在本文中，我们探索了 Spring 6 中新的声明式 RSocket 客户端功能。我们还学习了如何将其与 @RSocketExchange*注释*一起使用。

此外，我们还详细了解了如何创建和设置服务代理，以便我们可以使用 TCP 协议轻松、安全地连接到远程端点。