## 1. 概述

在本文中，我们将使用[WebSocket](https://www.baeldung.com/websockets-spring)创建一个应用程序并使用Postman对其进行测试。

## 2. Java WebSockets

**WebSocket是Web浏览器和服务器之间的双向、全双工、持久连接**。一旦建立了WebSocket连接，连接将保持打开状态，直到客户端或服务器决定关闭此连接。

WebSocket协议是使我们的应用程序处理实时消息的方法之一。最常见的替代方法是长轮询和服务器发送事件。这些解决方案中的每一个都有其优点和缺点。

在Spring中使用WebSockets的一种方法是使用STOMP子协议。**但是，在本文中，我们将使用原始WebSockets，因为截至今天，Postman中不支持STOMP**。

## 3. Postman设置

[Postman](https://www.baeldung.com/postman-testing-collections)是一个用于构建和使用API的API平台。使用Postman时，我们不需要为了测试而编写HTTP客户端基础结构代码。相反，我们创建称为集合的测试套件，并让Postman与我们的API进行交互。

## 4. 使用WebSocket的应用程序

我们将构建一个简单的应用程序。我们应用程序的工作流程将是：

-   服务器向客户端发送一次性消息
-   它定期向客户端发送消息
-   从客户端接收到消息后，它会记录这些消息并将其发送回客户端
-   客户端向服务器发送非周期性消息
-   客户端从服务器接收消息并记录它们

工作流程图如下：

<img src="../assets/img_1.png">

## 5. Spring WebSocket

我们的服务器由两部分组成。**Spring WebSocket事件处理程序和Spring WebSocket配置**。我们将在下面分别讨论它们：

### 5.1 Spring WebSocket配置

我们可以通过添加@EnableWebSocket注解在Spring服务器中启用WebSocket支持。

**在相同的配置中，我们还将为WebSocket端点注册已实现的WebSocket处理程序**：

```java
@Configuration
@EnableWebSocket
public class ServerWebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), "/websocket");
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new ServerWebSocketHandler();
    }
}
```

### 5.2 Spring WebSocket处理程序

WebSocket处理程序类扩展了TextWebSocketHandler。**此处理程序使用handleTextMessage回调方法从客户端接收消息**。sendMessage方法将消息发送回客户端：

```java
@Override
public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String request = message.getPayload();
    logger.info("Server received: {}", request);
        
    String response = String.format("response from server to '%s'", HtmlUtils.htmlEscape(request));
    logger.info("Server sends: {}", response);
    session.sendMessage(new TextMessage(response));
}
```

@Scheduled方法使用相同的sendMessage方法向活跃客户端广播周期性消息：

```java
@Scheduled(fixedRate = 10000)
void sendPeriodicMessages() throws IOException {
    for (WebSocketSession session : sessions) {
        if (session.isOpen()) {
            String broadcast = "server periodic message " + LocalTime.now();
            logger.info("Server sends: {}", broadcast);
            session.sendMessage(new TextMessage(broadcast));
        }
    }
}
```

我们的测试端点将是：

```html
ws://localhost:8080/websocket
```

## 6. Postman测试

现在我们的端点已准备就绪，我们可以使用Postman对其进行测试。**要测试WebSocket，我们必须有v8.5.0或更高版本**。

在开始使用Postman之前，我们将运行我们的服务器。现在让我们继续。

首先，启动Postman应用程序。一旦开始，我们就可以继续。

从UI加载后，选择”New“：

<img src="../assets/img_2.png">

将打开一个新的弹出窗口。**从那里选择WebSocket Request**：

<img src="../assets/img_3.png">

**我们将测试一个原始的WebSocket请求**。屏幕应如下所示：

<img src="../assets/img_4.png">

现在让我们添加我们的URL。按Connect按钮并测试连接：

<img src="../assets/img_5.png">

因此，连接工作正常。正如我们从控制台中看到的那样，我们正在从服务器获取响应。让我们现在尝试发送消息，服务器将响应：

<img src="../assets/img_6.png">

测试完成后，我们只需单击“Disconnect”按钮即可断开连接。

## 7. 总结

在本文中，我们创建了一个简单的应用程序来测试与WebSocket的连接，并使用Postman对其进行了测试。