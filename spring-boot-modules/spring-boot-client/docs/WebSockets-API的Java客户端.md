## 1. 简介

HTTP(超文本传输协议)是一种无状态的请求-响应协议，它的简单设计使其具有很强的可扩展性，但不适合高度交互的实时Web应用程序且效率低下，因为每个请求/响应都需要传输大量开销。

由于HTTP是同步的，而实时应用程序需要是异步的，因此轮询或长轮询([Comet](https://en.wikipedia.org/wiki/Comet_(programming)))等任何解决方案往往都很复杂且效率低下。

为了解决上述问题，我们需要一个基于标准的、双向的和全双工的协议，它可以被服务器和客户端使用，这导致了[JSR 356 API](http://www.oracle.com/technetwork/articles/java/jsr356-1937161.html)的引入-在这篇文章中，我们将演示它的示例用法。

## 2. Maven依赖

我们将Spring WebSocket依赖项包含到我们的项目中：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-websocket</artifactId>
    <version>5.2.2.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-messaging</artifactId>
    <version>5.2.2.RELEASE</version>
</dependency>
```

## 3. STOMP

Stream Text-Oriented Messaging Protocol(STOMP)是一种简单、可互操作的有线格式，允许客户端和服务器与几乎所有消息代理进行通信，它是AMQP(高级消息队列协议)和JMS(Java消息服务)的替代方案。

STOMP定义了客户端/服务器使用消息语义进行通信的协议，语义位于WebSockets之上并定义映射到WebSockets帧的帧。

使用STOMP使我们能够灵活地使用不同的编程语言开发客户端和服务器。在当前示例中，我们将使用STOMP在客户端和服务器之间进行消息传递。

## 4. WebSocket服务器

[你可以在本文]()中阅读有关构建WebSocket服务器的更多信息。

## 5. WebSocket客户端

要与WebSocket服务器通信，客户端必须通过向服务器发送HTTP请求并正确设置Upgrade标头来启动WebSocket连接：

```http request
GET ws://websocket.example.com/ HTTP/1.1
Origin: http://example.com
Connection: Upgrade
Host: websocket.example.com
Upgrade: websocket
```

请注意，WebSocket URL使用ws和wss方案，第二个表示安全的WebSocket。

如果启用了WebSockets支持，服务器会通过在响应中发送Upgrade标头进行响应。

```http request
HTTP/1.1 101 WebSocket Protocol Handshake
Date: Wed, 16 Oct 2013 10:07:34 GMT
Connection: Upgrade
Upgrade: WebSocket
```

一旦此过程(也称为WebSocket握手)完成，初始HTTP连接将替换为同一TCP/IP连接之上的WebSocket连接，之后任何一方都可以共享数据。

此客户端连接由WebSocketStompClient实例发起。

### 5.1 WebSocketStompClient

如第3节所述，我们首先需要建立WebSocket连接，这是使用WebSocketClient类完成的。

可以使用以下方式配置WebSocketClient：

-   StandardWebSocketClient由任何JSR-356实现提供，如Tyrus
-   Jetty 9+原生WebSocket API提供的JettyWebSocketClient
-   Spring的WebSocketClient的任何实现

在我们的示例中，我们将使用StandardWebSocketClient，它是WebSocketClient的一个实现：

```java
WebSocketClient client = new StandardWebSocketClient();

WebSocketStompClient stompClient = new WebSocketStompClient(client);
stompClient.setMessageConverter(new MappingJackson2MessageConverter());

StompSessionHandler sessionHandler = new MyStompSessionHandler();
stompClient.connect(URL, sessionHandler);

new Scanner(System.in).nextLine(); // Don't close immediately.
```

默认情况下，WebSocketStompClient支持SimpleMessageConverter。由于我们正在处理JSON消息，因此我们将消息转换器设置为MappingJackson2MessageConverter以便将JSON有效负载转换为对象。

在连接到端点时，我们传递了一个StompSessionHandler实例，它处理afterConnected和handleFrame等事件。

如果我们的服务器支持SockJs，那么我们可以修改客户端以使用[SockJsClient](https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#websocket-fallback-sockjs-client)而不是StandardWebSocketClient。

### 5.2 StompSessionHandler

我们可以使用StompSession来订阅WebSocket主题，这可以通过创建StompSessionHandlerAdapter的实例来完成，该实例又实现了StompSessionHandler。

StompSessionHandler为STOMP会话提供生命周期事件，这些事件包括会话建立时的回调和失败时的通知。

一旦WebSocket客户端连接到端点，StompSessionHandler就会收到通知，并在我们使用StompSession订阅主题的地方调用afterConnected()方法：

```java
@Override
public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
    session.subscribe("/topic/messages", this);
    session.send("/app/chat", getSampleMessage());
}

@Override
public void handleFrame(StompHeaders headers, Object payload) {
    Message msg = (Message) payload;
    logger.info("Received : " + msg.getText()+ " from : " + msg.getFrom());
}
```

确保WebSocket服务器正在运行并运行客户端，该消息将显示在控制台上：

```shell
INFO c.t.t.w.client.MyStompSessionHandler - New session established : 53b993eb-7ad6-4470-dd80-c4cfdab7f2ba
INFO c.t.t.w.client.MyStompSessionHandler - Subscribed to /topic/messages
INFO c.t.t.w.client.MyStompSessionHandler - Message sent to websocket server
INFO c.t.t.w.client.MyStompSessionHandler - Received : Howdy!! from : Nicky
```

## 6. 总结

在本快速教程中，我们实现了一个基于Spring的WebSocket客户端。