## 1. 概述

在本教程中，我们将创建一个简单的Web应用程序，该应用程序使用Spring框架4.0引入的**新WebSocket功能**实现消息传递。

WebSockets是Web浏览器和服务器之间的**双向、全双工、持久连接**。一旦建立了WebSocket连接，连接将保持打开状态，直到客户端或服务器决定关闭此连接。

一个典型的用例可能是当一个应用程序涉及多个用户相互通信时，例如在聊天中。我们将在示例中构建一个简单的聊天客户端。

## 2. Maven依赖

由于这是一个基于Maven的项目，我们首先将所需的依赖项添加到pom.xml中：

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

此外，我们需要添加Jackson依赖项，因为我们将使用JSON来构建消息正文。

这允许Spring将我们的Java对象转换为JSON或从JSON转换：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-core</artifactId>
    <version>2.10.2</version>
</dependency>

<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId> 
    <version>2.10.2</version>
</dependency>
```

在[Maven Central](https://search.maven.org/classic/)上查找上述库的最新版本。

## 3. 在Spring中启用WebSocket

首先，我们启用WebSocket功能。为此，我们需要向我们的应用程序添加一个配置，并使用@EnableWebSocketMessageBroker标注此类。

顾名思义，它支持WebSocket消息处理，由消息代理支持：

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat");
        registry.addEndpoint("/chat").withSockJS();
    }
}
```

在这里我们可以看到configureMessageBroker方法用于**配置消息代理**。

首先，我们启用内存中的消息代理将消息传送回以“/topic”为前缀的目的地上的客户端。

我们通过指定“/app”前缀来完成我们的简单配置，以过滤目标应用程序注解方法(通过@MessageMapping)。

registerStompEndpoints方法注册“/chat”端点，启用**Spring的[STOMP](https://stomp.github.io/stomp-specification-1.2.html#Abstract)支持**。请记住，为了弹性，我们还在此处添加了一个无需SockJS即可工作的端点。

当以“/app”为前缀时，此端点是ChatController.send()方法映射到处理的端点。

它还**启用[SockJS](https://github.com/sockjs/sockjs-protocol)回退选项**，以便在WebSocket不可用时可以使用备用消息传递选项。这很有用，因为并非所有浏览器都支持WebSocket，并且可能会被限制性网络代理排除。

回退允许应用程序使用WebSocket API，但在运行时在必要时优雅地降级为非WebSocket替代方案。

## 4. 创建消息模型

现在我们已经设置了项目并配置了WebSocket功能，我们需要创建要发送的消息。

端点将接收包含发件人姓名和STOMP消息中正文为JSON对象的文本的消息。

该消息可能如下所示：

```json
{
    "from": "John",
    "text": "Hello!"
}
```

为了对携带文本的消息进行建模，我们可以创建一个具有from和text属性的简单Java对象：

```java
public class Message {

    private String from;
    private String text;

    // getters and setters
}
```

默认情况下，Spring将使用Jackson库将我们的模型对象与JSON相互转换。

## 5. 创建消息处理控制器

正如我们所看到的，Spring处理STOMP消息传递的方法是将控制器方法关联到配置的端点。我们可以通过@MessageMapping注解来做到这一点。

端点和控制器之间的关联使我们能够在需要时处理消息：

```java
@MessageMapping("/chat")
@SendTo("/topic/messages")
public OutputMessage send(Message message) throws Exception {
    String time = new SimpleDateFormat("HH:mm").format(new Date());
    return new OutputMessage(message.getFrom(), message.getText(), time);
}
```

在我们的示例中，我们将创建另一个名为OutputMessage的模型对象来表示发送到配置目标的输出消息。我们用发件人和从传入消息中获取的消息文本填充我们的对象，并设置了时间戳。

处理消息后，我们将其发送到使用@SendTo注解定义的适当目的地。“/topic/messages”目的地的所有订阅者都将收到该消息。

## 6. 创建浏览器客户端

在服务器端进行配置后，我们将使用[sockjs-client](https://github.com/sockjs/sockjs-client)库构建一个与我们的消息系统交互的简单HTML页面。

首先，我们需要导入sockjs和stomp JavaScript客户端库。

接下来，我们可以创建一个connect()函数来打开与我们端点的通信，一个sendMessage()函数来发送我们的STOMP消息和一个disconnect()函数来关闭通信：

```html
<html>
<head>
    <title>Chat WebSocket</title>
    <script src="resources/js/sockjs-0.3.4.js"></script>
    <script src="resources/js/stomp.js"></script>
    <script type="text/javascript">
        var stompClient = null;

        function setConnected(connected) {
            document.getElementById('connect').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('conversationDiv').style.visibility
                    = connected ? 'visible' : 'hidden';
            document.getElementById('response').innerHTML = '';
        }

        function connect() {
            var socket = new SockJS('/chat');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function (frame) {
                setConnected(true);
                console.log('Connected: ' + frame);
                stompClient.subscribe('/topic/messages', function (messageOutput) {
                    showMessageOutput(JSON.parse(messageOutput.body));
                });
            });
        }

        function disconnect() {
            if (stompClient != null) {
                stompClient.disconnect();
            }
            setConnected(false);
            console.log("Disconnected");
        }

        function sendMessage() {
            var from = document.getElementById('from').value;
            var text = document.getElementById('text').value;
            stompClient.send("/app/chat", {},
                    JSON.stringify({'from': from, 'text': text}));
        }

        function showMessageOutput(messageOutput) {
            var response = document.getElementById('response');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.appendChild(document.createTextNode(messageOutput.from + ": "
                    + messageOutput.text + " (" + messageOutput.time + ")"));
            response.appendChild(p);
        }
    </script>
</head>
<body onload="disconnect()">
<div>
    <div>
        <input type="text" id="from" placeholder="Choose a nickname"/>
    </div>
    <br/>
    <div>
        <button id="connect" onclick="connect();">Connect</button>
        <button id="disconnect" disabled="disabled" onclick="disconnect();">
            Disconnect
        </button>
    </div>
    <br/>
    <div id="conversationDiv">
        <input type="text" id="text" placeholder="Write a message..."/>
        <button id="sendMessage" onclick="sendMessage();">Send</button>
        <p id="response"></p>
    </div>
</div>
</body>
</html>
```

## 7. 测试示例

为了测试我们的示例，我们可以打开几个浏览器窗口并访问聊天页面：

```html
http://localhost:8080
```

完成此操作后，我们可以通过输入昵称并点击连接按钮来加入聊天。如果我们撰写并发送一条消息，我们可以在所有加入聊天的浏览器会话中看到它。

下面是屏幕截图：

<img src="../assets/img.png">

## 8. 总结

在本文中，我们探讨了Spring的WebSocket支持。我们已经看到了它的服务器端配置，并使用sockjs和stomp JavaScript库构建了一个简单的客户端对应物。