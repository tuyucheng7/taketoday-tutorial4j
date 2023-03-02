## 1. 概述

[WebSocket](https://datatracker.ietf.org/doc/html/rfc6455)在客户端和服务器之间提供事件驱动的双向全双工连接。WebSocket通信涉及握手、消息传递(发送和接收消息)和关闭连接。

**在本教程中，我们将学习如何使用[浏览器](https://caniuse.com/?search=websocket)和其他流行工具调试WebSocket**。

## 2. 构建WebSocket

让我们从[构建一个WebSocket服务器](https://www.baeldung.com/websockets-spring)开始，该服务器将股票代码更新推送到客户端。

### 2.1 Maven依赖项

首先，让我们声明[Spring WebSocket](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-websocket/3.0.3)依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
    <version>2.5.4</version>
</dependency>
```

### 2.2 Spring Boot配置

接下来，让我们定义启用WebSocket支持所需的@Configuration：

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stock-ticks").withSockJS();
    }
}
```

**请注意，此配置提供了一个基于消息代理的WebSocket并注册了STOMP端点**。

此外，让我们创建一个向订阅者发送模拟股票更新的控制器：

```java
private SimpMessagingTemplate simpMessagingTemplate;
 
public void sendTicks() { 
    simpMessagingTemplate.convertAndSend("/topic/ticks", getStockTicks());
}
```

### 2.3 客户端-用户界面

让我们构建一个[HTML5](https://html.spec.whatwg.org/)页面来显示来自服务器的更新：

```html
<div class="spinner-border text-primary" role="status">
    <span class="visually-hidden">Loading ...</span>
</div>
```

接下来，让我们使用[SockJS](https://github.com/sockjs/sockjs-client)连接到WebSocket服务器：

```javascript
function connect() {
    let socket = new SockJS('/stock-ticks');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/topic/ticks', function (ticks) {
            // ...
        });
    });
}
```

**在这里，我们使用SockJS打开一个WebSocket，然后订阅主题/topic/ticks**。最终，当服务器生成消息时，客户端使用消息并将其显示在用户界面上。

### 2.4 示范

让我们启动服务器并在浏览器中打开应用程序：

```shell
mvn spring-boot:run
```

因此，我们看到股票报价每3秒变化一次，而无需页面刷新或服务器轮询：

<video class="wp-video-shortcode" id="video-113192-1_html5" width="580" height="234" preload="metadata" src="https://www.baeldung.com/wp-content/uploads/2021/11/Websocket-2.mp4?_=1" style="box-sizing: border-box; font-family: Helvetica, Arial; max-width: 100%; display: inline-block; width: 580px; height: 233.812px;"></video>

到目前为止，我们已经构建了一个在WebSocket上接收股票报价的应用程序。接下来，让我们学习如何调试这个应用程序。

## 3. 火狐浏览器

**[Mozilla Firefox](https://www.mozilla.org/en-US/firefox/new/)有一个WebSocket检查器以及其他Web开发工具**。在Firefox中，我们可以通过多种方式启用开发者工具：

-   **Windows和Linux**：Ctrl + Shift + I或F12或Application Menu → More Tools → Web
-   **macOS**：Cmd + Opt + I

接下来，单击Network Monitor → WS以打开WebSockets面板：

<img src="../assets/img_7.png">

在WebSocket检查器处于活动状态的情况下，让我们进一步探索它。

### 3.1 握手

在Firefox中打开URL http://localhost:8080。**打开开发人员工具后，我们现在可以看到HTTP握手**。点击请求以分析握手：

<img src="../assets/img_8.png">

在Headers选项卡下，我们看到带有协议升级的请求和响应标头以及其他WebSocket标头。

### 3.2 消息交换

随后，**握手后，消息交换开始**。单击Response选项卡以查看消息交换：

<img src="../assets/img_9.png">

在“Response”窗格中，绿色的向上箭头显示客户端请求，红色的向下箭头表示服务器响应。

### 3.3 连接终止

在WebSockets中，客户端或服务器都可以关闭连接。

首先，让我们模拟客户端连接终止。单击HTML页面上的Disconnect按钮并查看Response选项卡：

<img src="../assets/img_10.png">

在这里，我们将看到来自客户端的连接终止请求。

接下来，让我们关闭服务器以模拟服务器端连接关闭。由于无法访问服务器，连接关闭：

<img src="../assets/img_11.png">

**[RFC6455–WebSocket协议](https://datatracker.ietf.org/doc/html/rfc6455#section-7.4)指定**：

-   **1000：正常关闭**
-   **1001：服务器已关闭，或用户已离开页面**

## 4. 谷歌浏览器

[Google Chrome](https://www.google.com/intl/en_in/chrome/)有一个WebSocket检查器，它是开发人员工具的一部分，类似于Firefox。我们可以通过几种方式激活WebSocket检查器：

-   **Windows和Linux**：Ctrl + Shift + I或Ctrl + Shift + J或F12或Application Menu → More Tools → Developer Tools
-   **macOS**：Cmd + Opt + I

接下来，单击Network → WS面板以打开WebSocket面板：

<img src="../assets/img_12.png">

### 4.1 握手

现在，在Chrome中打开URL http://localhost:8080并在开发者工具中点击请求：

<img src="../assets/img_13.png">

在Headers选项卡下，我们注意到所有WebSocket标头，包括握手。

### 4.2 消息交换

接下来，让我们检查客户端和服务器之间的消息交换。在开发人员工具上，单击“Messages”选项卡：

<img src="../assets/img_14.png">

与在Firefox中一样，我们可以查看消息交换，包括CONNECT请求、SUBSCRIBE请求和MESSAGE交换。

### 4.3 连接终止

最后，我们将调试客户端和服务器端的连接终止。但是，首先，让我们关闭客户端连接：

<img src="../assets/img_15.png">

我们可以看到客户端和服务器之间的连接正常终止。接下来，让我们模拟一个服务器终止连接：

<img src="../assets/img_16.png">

成功的连接终止结束了客户端和服务器之间的消息交换。

## 5. Wireshark

[Wireshark](https://www.wireshark.org/)是最流行、使用最广泛的网络协议嗅探工具。那么接下来，让我们看看如何使用Wireshark嗅探和分析WebSocket流量。

### 5.1 捕获流量

与其他工具不同，我们必须为Wireshark捕获流量，然后对其进行分析。因此，让我们从捕获流量开始。

在Windows中，当我们打开Wireshark时，它会显示所有可用的网络接口以及实时网络流量。因此，选择正确的网络接口来捕获网络数据包至关重要。

**通常，如果WebSocket服务器以localhost(127.0.0.1)运行，则网络接口将是一个环回适配器**：

<img src="../assets/img_17.png">

接下来，要开始捕获数据包，请双击该接口。一旦选择了正确的接口，我们就可以根据协议进一步过滤数据包。

**在Linux中，使用[tcpdump](https://www.baeldung.com/linux/sniffing-packet-tcpdump)命令来捕获网络流量**。例如，打开一个shell终端并使用以下命令生成数据包捕获文件websocket.pcap：

```shell
tcpdump -w websocket.pcap -s 2500 -vv -i lo
```

然后，使用Wireshark打开websocket.pcap文件。

### 5.2 握手

让我们尝试分析到目前为止捕获的网络数据包。**首先，由于初始握手是在HTTP协议上进行的，所以让我们过滤http协议的数据包**：

<img src="../assets/img_18.png">

接下来，要获得握手的详细视图，请右键单击packet → Follow → TCP Stream：

<img src="../assets/img_19.png">

### 5.3 消息交换

回想一下，在初始握手之后，客户端和服务器通过WebSocket协议进行通信。因此，让我们过滤WebSocket的数据包。显示的其余数据包揭示了连接和消息交换：

<img src="../assets/img_20.png">

### 5.4 连接终止

首先，让我们调试客户端连接终止。启动Wireshark抓包，在HTML页面点击Disconnect按钮，查看网络包：

<img src="../assets/img_21.png">

同样，让我们模拟服务器端连接终止。首先，启动数据包捕获，然后关闭WebSocket服务器：

<img src="../assets/img_22.png">

## 6. Postman

**截至目前，[Postman](https://www.postman.com/)对WebSockets的支持仍处于Beta阶段**。但是，我们仍然可以使用它来调试我们的WebSockets：

打开Postman并按Ctrl + N或New → WebSocket Request：

<img src="../assets/img_23.png">

接下来，在Enter Server URL文本框中，输入WebSocket URL并单击Connect：

<img src="../assets/img_24.png">

### 6.1 握手

连接成功后，在Messages部分，单击连接请求以查看握手详细信息：

<img src="../assets/img_25.png">

### 6.2 消息交换

现在，让我们检查客户端和服务器之间的消息交换：

<img src="../assets/img_26.png">

一旦客户端订阅了主题，我们就可以看到客户端和服务器之间的消息流。

### 6.3 连接终止

此外，让我们看看如何调试客户端和服务器的连接终止。首先，单击Postman中的Disconnect按钮从客户端关闭连接：

<img src="../assets/img_27.png">

同样，要检查服务器连接终止，请关闭服务器：

<img src="../assets/img_28.png">

## 7. Spring WebSocket客户端

最后，**让我们使用基于[Spring的Java客户端](https://www.baeldung.com/websockets-api-java-spring-client)调试WebSockets**：

```java
WebSocketClient client = new StandardWebSocketClient();
WebSocketStompClient stompClient = new WebSocketStompClient(client);
stompClient.setMessageConverter(new MappingJackson2MessageConverter());
StompSessionHandler sessionHandler = new StompClientSessionHandler();
stompClient.connect(URL, sessionHandler);
```

这将创建一个WebSocket客户端，然后注册一个STOMP客户端会话处理程序。

接下来，**让我们定义一个扩展[StompSessionHandlerAdapter](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/messaging/simp/stomp/StompSessionHandlerAdapter.html)的处理程序**。StompSessionHandlerAdapter类有意不提供除方法getPayloadType之外的实现。因此，让我们为这些方法提供一个有意义的实现：

```java
public class StompClientSessionHandler extends StompSessionHandlerAdapter {

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe("/topic/ticks", this);
    }

    // other methods ...
}
```

接下来，当我们运行这个客户端时，我们会得到类似于以下的日志：

```shell
16:35:49.135 [WebSocketClient-AsyncIO-8] INFO StompClientSessionHandler - Subscribed to topic: /topic/ticks
16:35:50.291 [WebSocketClient-AsyncIO-8] INFO StompClientSessionHandler - Payload -> {MSFT=17, GOOGL=48, AAPL=54, TSLA=73, HPE=89, AMZN=-5}
```

在日志中，我们可以看到连接和消息交换。此外，**当客户端启动并运行时，我们可以使用Wireshark嗅探WebSocket数据包**：

<img src="../assets/img_29.png">

## 8. 总结

在本教程中，我们学习了如何使用一些最流行和广泛使用的工具来调试WebSocket。随着WebSockets的使用和普及与日俱增，我们可以预期调试工具的数量会增加并变得更先进。