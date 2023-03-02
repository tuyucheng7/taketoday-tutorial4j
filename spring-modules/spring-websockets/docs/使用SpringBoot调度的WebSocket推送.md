## 1. 概述

在本教程中，我们将了解如何使用[WebSockets](https://www.baeldung.com/java-websockets)从服务器向浏览器发送调度消息。另一种方法是使用[服务器发送事件](https://www.baeldung.com/spring-server-sent-events)(SSE)，但我们不会在本文中介绍它。

Spring提供了多种调度选项。首先，我们将介绍[@Scheduled](https://www.baeldung.com/spring-scheduling-annotations#scheduled)注解。然后，我们将看到一个使用Project Reactor提供的[Flux::interval](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#interval-java.time.Duration-)方法的示例。这个库对于[Webflux](https://www.baeldung.com/spring-webflux)应用程序来说是开箱即用的，并且可以在任何Java项目中用作独立的库。

此外，还存在更高级的机制，例如[Quartz调度程序](https://www.baeldung.com/quartz)，但我们不会介绍它们。

## 2. 一个简单的聊天应用程序

在[上一篇文章](https://www.baeldung.com/spring-websockets-sendtouser)中，我们使用WebSockets构建了一个聊天应用程序。让我们用一个新功能来扩展它：聊天机器人。这些机器人是将预定消息推送到浏览器的服务器端组件。

### 2.1 Maven依赖

让我们从在Maven中设置必要的依赖关系开始。要构建这个项目，我们的pom.xml应该有：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
</dependency>
<dependency>
    <groupId>com.github.javafaker</groupId>
    <artifactId>javafaker</artifactId>
    <version>1.0.2</version>
</dependency>
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
</dependency>
```

### 2.2 JavaFaker依赖

我们将使用[JavaFaker](https://www.baeldung.com/java-faker)库来生成机器人的消息。此库通常用于生成测试数据。在这里，我们将向我们的聊天室添加一位名为“Chuck Norris”的客人。

让我们看看代码：

```java
Faker faker = new Faker();
ChuckNorris chuckNorris = faker.chuckNorris();
String messageFromChuck = chuckNorris.fact();
```

Faker将为各种数据生成器提供工厂方法。我们将使用[ChuckNorris](https://dius.github.io/java-faker/apidocs/com/github/javafaker/ChuckNorris.html)生成器。调用chuckNorris.fact()将显示预定义消息列表中的随机句子。

### 2.3 数据模型

聊天应用程序使用一个简单的POJO作为消息包装器：

```java
public class OutputMessage {
    private String from;
    private String text;
    private String time;

    // standard constructors, getters/setters, equals and hashcode
}
```

综上所述，以下是我们如何创建聊天消息的示例：

```java
OutputMessage message = new OutputMessage("Chatbot 1", "Hello there!", new SimpleDateFormat("HH:mm").format(new Date())));
```

### 2.4 客户端

我们的聊天客户端是一个简单的HTML页面。它使用[SockJS客户端](https://github.com/sockjs/sockjs-client)和[STOMP](https://stomp.github.io/)消息协议。

让我们看看客户端如何订阅主题：

```xml
<html>
    <head>
        <script src="./js/sockjs-0.3.4.js"></script>
        <script src="./js/stomp.js"></script>
        <script type="text/javascript">
            // ...
            stompClient = Stomp.over(socket);

            stompClient.connect({}, function(frame) {
            // ...
            stompClient.subscribe('/topic/pushmessages', function(messageOutput) {
            showMessageOutput(JSON.parse(messageOutput.body));
            });
            });
            // ...
        </script>
    </head>
    <!-- ... -->
</html>
```

首先，我们通过SockJS协议创建了一个Stomp客户端。然后，主题订阅充当服务器和连接的客户端之间的通信通道。

在我们的仓库中，此代码位于webapp/bots.html中。我们在[http://localhost:8080/bots.html](http://localhost:8080/bots.html)本地运行时访问它。当然，我们需要根据我们部署应用程序的方式来调整主机和端口。

### 2.5 服务器端

在上一篇文章中，我们已经了解了如何在[Spring中配置WebSockets](https://www.baeldung.com/websockets-spring)。让我们稍微修改一下配置：

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // ...
        registry.addEndpoint("/chatwithbots");
        registry.addEndpoint("/chatwithbots").withSockJS();
    }
}
```

**为了推送我们的消息，我们使用实用程序类[SimpMessagingTemplate](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/messaging/simp/SimpMessagingTemplate.html)**。默认情况下，它在Spring上下文中作为@Bean提供。当[AbstractMessageBrokerConfiguration](https://github.com/spring-projects/spring-framework/blob/5b910a87c38386e870eba1f3f8154db2de4df026/spring-messaging/src/main/java/org/springframework/messaging/simp/config/AbstractMessageBrokerConfiguration.java#L392)在类路径中时，我们可以看到它是如何通过自动配置声明的。因此，我们可以将它注入到任何Spring组件中。

之后，我们使用它向主题/topic/pushmessages发布消息。我们假设我们的类将该bean注入到名为simpMessagingTemplate的变量中：

```java
simpMessagingTemplate.convertAndSend("/topic/pushmessages", new OutputMessage("Chuck Norris", faker.chuckNorris().fact(), time));
```

如前面在我们的客户端示例中所示，客户端订阅该主题以在消息到达时对其进行处理。

## 3. 定时推送消息

在Spring生态系统中，我们可以选择多种调度方式。如果我们使用Spring MVC，@Scheduled注解因其简单性而成为自然的选择。如果我们使用Spring Webflux，我们也可以使用Project Reactor的Flux::interval方法。我们将看到每个示例。

### 3.1 配置

我们的聊天机器人将使用JavaFaker的Chuck Norris生成器。我们将把它配置为一个bean，这样我们就可以将它注入到我们需要的地方。

```java
@Configuration
class AppConfig {

    @Bean
    public ChuckNorris chuckNorris() {
        return (new Faker()).chuckNorris();
    }
}
```

### 3.2 使用@Scheduled

我们的示例机器人是调度方法。当它们运行时，它们使用SimpMessagingTemplate通过WebSocket发送我们的OutputMessage POJO。

顾名思义，**[@Scheduled](https://www.baeldung.com/spring-scheduling-annotations#scheduled)注解允许重复执行方法**。有了它，我们可以使用简单的基于速率的调度或更复杂的“cron”表达式。

让我们编写我们的第一个聊天机器人：

```java
@Service
public class ScheduledPushMessages {

    @Scheduled(fixedRate = 5000)
    public void sendMessage(SimpMessagingTemplate simpMessagingTemplate, ChuckNorris chuckNorris) {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        simpMessagingTemplate.convertAndSend("/topic/pushmessages", new OutputMessage("Chuck Norris (@Scheduled)", chuckNorris().fact(), time));
    }
}
```

我们用@Scheduled(fixedRate = 5000)标注sendMessage方法，这使得sendMessage每5秒运行一次。然后，我们使用simpMessagingTemplate实例向主题发送OutputMessage。simpMessagingTemplate和chuckNorris实例作为方法参数从Spring上下文注入。

### 3.3 使用Flux::interval()

**如果我们使用WebFlux，我们可以使用[Flux::interval](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#interval-java.time.Duration-)运算符**。它将发布由所选[Duration](https://www.baeldung.com/java-period-duration#duration-class)分隔的无限长元素流。

现在，让我们在前面的示例中使用Flux。目标是每5秒发送一次Chuck Norris的报价。首先，我们需要实现InitializingBean接口以在[应用程序启动](https://www.baeldung.com/running-setup-logic-on-startup-in-spring)时订阅Flux：

```java
@Service
public class ReactiveScheduledPushMessages implements InitializingBean {

    private SimpMessagingTemplate simpMessagingTemplate;

    private ChuckNorris chuckNorris;

    @Autowired
    public ReactiveScheduledPushMessages(SimpMessagingTemplate simpMessagingTemplate, ChuckNorris chuckNorris) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chuckNorris = chuckNorris;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Flux.interval(Duration.ofSeconds(5L))
              // discard the incoming Long, replace it by an OutputMessage
              .map((n) -> new OutputMessage("Chuck Norris (Flux::interval)",
                    chuckNorris.fact(),
                    new SimpleDateFormat("HH:mm").format(new Date())))
              .subscribe(message -> simpMessagingTemplate.convertAndSend("/topic/pushmessages", message));
    }
}
```

在这里，我们使用构造函数注入来设置simpMessagingTemplate和chuckNorris实例。这一次，调度逻辑在afterPropertiesSet()中，我们在实现InitializingBean时覆盖了它。该方法将在服务启动后立即运行。

[interval](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#interval-java.time.Duration-)运算符每5秒发出一个Long。然后，[map](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#map-java.util.function.Function-)运算符丢弃该值并用我们的消息替换它。最后，我们[subscribe](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#subscribe-java.util.function.Consumer-) Flux以触发我们对每条消息的逻辑。

## 4. 总结

在本教程中，我们已经看到实用程序类SimpMessagingTemplate可以轻松地通过WebSocket推送服务器消息。此外，我们还看到了两种调度一段代码执行的方法。