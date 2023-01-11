## 1. 简介

在本教程中，我们将描述如何使用 Spring WebSockets 向单个用户发送 STOMP 消息。这很重要，因为我们有时不想将每条消息都广播给每个用户。除此之外，我们将演示如何以安全的方式发送这些消息。

有关 WebSockets 的介绍，请查看[这个](https://www.baeldung.com/websockets-spring) 很棒的教程，了解如何启动和运行。而且，为了更深入地了解安全性，请查看 [本文](https://www.baeldung.com/spring-security-websockets) 以保护的 WebSockets 实施。

## 2. 队列、主题和端点

 使用 Spring WebSockets 和 STOMP可以通过三种主要方式来说明消息的发送位置以及订阅方式：

1.  主题——对任何客户或用户开放的常见对话或聊天主题
2.  队列- 为特定用户及其当前会话保留
3.  端点——通用端点

现在，让我们快速看一下每个示例上下文路径：

-   “/主题/电影”
-   “/用户/队列/特定用户”
-   “/安全/聊天”

需要注意的是， 我们必须使用队列向特定用户发送消息，因为主题和端点不支持此功能。

## 3.配置

现在，让我们学习如何配置我们的应用程序，以便我们可以向特定用户发送消息：

```java
public class SocketBrokerConfig extends 
  AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/secured/user/queue/specific-user");
        config.setApplicationDestinationPrefixes("/spring-security-mvc-socket");
        config.setUserDestinationPrefix("/secured/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/secured/room").withSockJS();
    }
}
```

让我们确保包含一个用户目的地，因为它决定了哪些端点是为单个用户保留的。

我们还为所有队列和用户目的地添加“/secured”前缀，以使它们需要身份验证。对于未受保护的端点，我们可以删除“/secured”前缀(作为我们其他安全设置的结果)。

从pom.xml的角度来看，不需要额外的依赖项。

## 4. URL 映射

我们希望我们的客户端使用符合以下模式的 URL 映射订阅队列：

```plaintext
"/user/queue/updates"
```

此映射将由UserDestinationMessageHandler自动转换为特定 于用户会话的地址。

例如，如果我们有一个名为“user123”的用户，对应的地址将是：

```plaintext
"/queue/updates-user123"
```

在服务器端，我们将使用以下 URL 映射模式发送特定于用户的响应：

```plaintext
"/user/{username}/queue/updates"
```

这也将转换为我们已经订阅客户端的正确 URL 映射。

因此，我们看到这里的基本成分有两个方面：

1.  添加我们指定的用户目的地前缀(在 AbstractWebSocketMessageBrokerConfigurer中配置)。
2.  在映射中的某处使用 “/queue” 。

在下一节中，我们将详细了解如何执行此操作。

## 5. 调用 convertAndSendToUser()

 我们可以从 SimpMessagingTemplate或 SimpMessageSendingOperations非静态调用convertAndSendToUser ()：

```java
@Autowired
private SimpMessagingTemplate simpMessagingTemplate;

@MessageMapping("/secured/room") 
public void sendSpecific(
  @Payload Message msg, 
  Principal user, 
  @Header("simpSessionId") String sessionId) throws Exception { 
    OutputMessage out = new OutputMessage(
      msg.getFrom(), 
      msg.getText(),
      new SimpleDateFormat("HH:mm").format(new Date())); 
    simpMessagingTemplate.convertAndSendToUser(
      msg.getTo(), "/secured/user/queue/specific-user", out); 
}
```

你可能已经注意到：

```java
@Header("simpSessionId") String sessionId
```

@Header 注解允许访问入站消息公开的标头。例如，我们可以 在不需要复杂的拦截器的情况下获取当前的sessionId 。同样，我们可以通过Principal访问当前用户。

重要的是，我们在本文中采用的方法在URL 映射方面提供了对@sendToUser注解的更大自定义。有关该注解的更多信息，请查看 [这篇](https://www.baeldung.com/spring-websockets-sendtouser)精彩的文章。

在客户端，我们将 在 JavaScript 中 使用connect()来初始化 SockJS 实例并使用 STOMP 连接到我们的 WebSocket 服务器：

```javascript
var socket = new SockJS('/secured/room'); 
var stompClient = Stomp.over(socket);
var sessionId = "";

stompClient.connect({}, function (frame) {
    var url = stompClient.ws._transport.url;
    url = url.replace(
      "ws://localhost:8080/spring-security-mvc-socket/secured/room/",  "");
    url = url.replace("/websocket", "");
    url = url.replace(/^[0-9]+//, "");
    console.log("Your current session is: " + url);
    sessionId = url;
}

```

我们还访问提供的sessionId并将其附加到“ secured/room ”  URL 映射。这使我们能够动态和手动提供用户特定的订阅队列：

```javascript
stompClient.subscribe('secured/user/queue/specific-user' 
  + '-user' + that.sessionId, function (msgOut) {
     //handle messages
}

```

一切都设置好后，我们应该看到：

 

[![特定套接字特定](https://www.baeldung.com/wp-content/uploads/2018/09/Specific-Sockets-Specific-1024x554-1024x554.png)](https://www.baeldung.com/wp-content/uploads/2018/09/Specific-Sockets-Specific-1024x554.png)

在我们的服务器控制台中：

[![特定插座端子](https://www.baeldung.com/wp-content/uploads/2018/09/Specific-Sockets-Terminal-1024x494-1024x494.png)](https://www.baeldung.com/wp-content/uploads/2018/09/Specific-Sockets-Terminal-1024x494.png)

## 六. 总结

查看官方 Spring[博客](https://assets.spring.io/wp/WebSocketBlogPost.html)和[官方文档](https://docs.spring.io/spring-framework/docs/4.3.x/spring-framework-reference/html/websocket.html) 以获取有关此主题的更多信息。