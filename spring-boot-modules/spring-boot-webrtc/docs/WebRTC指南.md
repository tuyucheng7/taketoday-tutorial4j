## 1. 概述

当两个浏览器需要通信时，它们之间通常需要一个服务器来协调通信，在它们之间传递消息。但是在中间有一个服务器会导致浏览器之间的通信延迟。

在本教程中，我们将了解[WebRTC](https://webrtc.org/)，这是一个开源项目，它使浏览器和移动应用程序能够直接相互实时通信。然后，我们将通过编写一个创建点对点连接以在两个 HTML 客户端之间共享数据的简单应用程序来查看它的运行情况。

我们将使用 HTML、JavaScript 和 WebSocket 库以及 Web 浏览器中的内置 WebRTC 支持来构建客户端。而且，我们将使用Spring Boot构建一个信令服务器，使用 WebSocket 作为通信协议。最后，我们将看到如何将视频和音频流添加到此连接。

## 2. WebRTC的基础和概念

让我们看看两个浏览器在没有 WebRTC 的典型场景下是如何通信的。

假设我们有两个浏览器，浏览器 1需要向浏览器 2发送消息。浏览器 1首先将它发送到服务器：

 

[![WebRTC 2 1](https://www.baeldung.com/wp-content/uploads/2019/12/webrtc-2-1.png)](https://www.baeldung.com/wp-content/uploads/2019/12/webrtc-2-1.png)

Server收到消息后，进行处理，找到Browser 2，并向其发送消息：

 

[![WebRTC 2 2](https://www.baeldung.com/wp-content/uploads/2019/12/webrtc-2-2.png)](https://www.baeldung.com/wp-content/uploads/2019/12/webrtc-2-2.png)

由于服务器必须在将消息发送到浏览器 2 之前对其进行处理，因此通信几乎是实时进行的。当然，我们希望它是 实时的。

WebRTC 通过在两个浏览器之间创建一个直接通道解决了这个问题，消除了对服务器的需求：

 

[![WebRTC 2 3](https://www.baeldung.com/wp-content/uploads/2019/12/webrtc-2-3.png)](https://www.baeldung.com/wp-content/uploads/2019/12/webrtc-2-3.png)

结果，将消息从一个浏览器传递到另一个浏览器所需的时间大大减少，因为消息现在直接从发送者路由到接收者。它还消除了服务器带来的繁重工作和带宽，并使其在相关客户端之间共享。

## 3. 支持 WebRTC 和内置功能

WebRTC 受到 Chrome、Firefox、Opera 和 Microsoft Edge 等主要浏览器以及 Android 和 iOS 等平台的支持。

WebRTC 不需要在我们的浏览器中安装任何外部插件，因为该解决方案与浏览器捆绑在一起，开箱即用。

此外，在一个典型的涉及视频和音频传输的实时应用中，我们不得不严重依赖C++库，我们必须处理很多问题，包括：

-   丢包隐藏
-   回声消除
-   带宽适应性
-   动态抖动缓冲
-   自动增益控制
-   降噪和抑制
-   图片“清洁”

但是WebRTC 在后台处理所有这些问题，使客户端之间的实时通信变得更加简单。

## 4.点对点连接

与客户端-服务器通信不同，服务器有一个已知地址，并且客户端已经知道要与之通信的服务器的地址，在 P2P(点对点)连接中，没有一个点有直接地址给另一个同行。

要建立点对点连接，需要几个步骤来允许客户端：

-   让自己可以进行交流
-   相互识别并共享网络相关信息
-   共享并同意所涉及的数据格式、模式和协议
-   共享数据

WebRTC 定义了一组用于执行这些步骤的 API 和方法。

为了让客户端相互发现、共享网络细节，然后共享数据格式，WebRTC 使用了一种称为信令的机制。

## 5.信令

信令是指涉及网络发现、会话创建、会话管理和交换媒体功能元数据的过程。

这是必不可少的，因为客户需要预先了解对方才能启动通信。

为了实现所有这些，WebRTC 没有指定信令标准，而是将其留给开发人员实现。因此，这为我们提供了在具有任何技术和支持协议的一系列设备上使用 WebRTC 的灵活性。

### 5.1. 构建信令服务器

对于信令服务器，我们将使用Spring Boot构建一个 WebSocket 服务器。我们可以从[Spring Initializr](https://start.spring.io/)生成的空Spring Boot项目开始。

要将 WebSocket 用于我们的实现，让我们将依赖项添加到我们的pom.xml：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
    <version>2.4.0</version>
</dependency>
```

我们总能从[Maven Central](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-websocket)找到要使用的最新版本。

信令服务器的实现很简单——我们将创建一个客户端应用程序可以用来注册为 WebSocket 连接的端点。

要在Spring Boot中执行此操作，让我们编写一个@Configuration类来扩展WebSocketConfigurer并覆盖registerWebSocketHandlers方法：

```java
@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SocketHandler(), "/socket")
          .setAllowedOrigins("");
    }
}
```

请注意，我们已将/socket标识为我们将从下一步构建的客户端注册的 URL。我们还将SocketHandler作为参数传递给addHandler方法——这实际上是我们接下来要创建的消息处理程序。

### 5.2. 在信令服务器中创建消息处理程序

下一步是创建一个消息处理程序来处理我们将从多个客户端接收的 WebSocket 消息。

这对于帮助不同客户端之间交换元数据以建立直接的 WebRTC 连接至关重要。

在这里，为了简单起见，当我们收到来自客户端的消息时，我们会将其发送给除自身以外的所有其他客户端。

为此，我们可以从 Spring WebSocket 库扩展TextWebSocketHandler并覆盖handleTextMessage和 afterConnectionEstablished方法：

```java
@Component
public class SocketHandler extends TextWebSocketHandler {

    List<WebSocketSession>sessions = new CopyOnWriteArrayList<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
      throws InterruptedException, IOException {
        for (WebSocketSession webSocketSession : sessions) {
            if (webSocketSession.isOpen() && !session.getId().equals(webSocketSession.getId())) {
                webSocketSession.sendMessage(message);
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }
}

```

正如我们在afterConnectionEstablished 方法中看到的那样，我们将接收到的会话添加到会话列表中，以便我们可以跟踪所有客户端。

当我们收到来自任何客户端的消息时，如handleTextMessage 中所示，我们遍历列表中的所有客户端会话，并通过比较发送者的会话 ID 和列表中的会话。

## 6. 交换元数据

在 P2P 连接中，客户端之间可能非常不同。例如，Android 上的 Chrome 可以连接到 Mac 上的 Mozilla。

因此，这些设备的媒体功能可能差异很大。因此，对等方之间的握手必须就用于通信的媒体类型和编解码器达成一致。

在此阶段，WebRTC 使用 SDP(会话描述协议)在客户端之间就元数据达成一致。 

为实现这一点，发起对等点创建一个必须由另一个对等点设置为远程描述符的提议。此外，另一个对等端随后生成一个应答，该应答被发起对等端接受为远程描述符。

当这个过程完成时，连接就建立了。

## 7. 设置客户端

让我们创建我们的 WebRTC 客户端，这样它既可以充当发起端，也可以充当远程端。

我们将首先创建一个名为index.html的 HTML 文件和一个名为client.js 的 JavaScript 文件， index.html 将使用该文件。

为了连接到我们的信令服务器，我们创建了一个 WebSocket 连接。假设我们构建的Spring Boot信令服务器运行在[http://localhost:8080](http://localhost:8080/)上，我们可以创建连接：

```javascript
var conn = new WebSocket('ws://localhost:8080/socket');
```

要向信令服务器发送消息，我们将创建一个发送方法，用于在接下来的步骤中传递消息：

```javascript
function send(message) {
    conn.send(JSON.stringify(message));
}
```

## 8. 设置一个 简单的RTCDataChannel

在client.js中设置客户端后，我们需要为RTCPeerConnection类创建一个对象：

```javascript
configuration = null;
var peerConnection = new RTCPeerConnection(configuration);
```

在此示例中，配置对象的目的是传入 STUN(NAT 会话遍历实用程序)和 TURN(使用中继遍历 NAT)服务器以及我们将在本教程后面部分讨论的其他配置。对于此示例，传入null就足够了。

现在，我们可以创建一个dataChannel用于消息传递：

```javascript
var dataChannel = peerConnection.createDataChannel("dataChannel", { reliable: true });
```

随后，我们可以为数据通道上的各种事件创建监听器：

```javascript
dataChannel.onerror = function(error) {
    console.log("Error:", error);
};
dataChannel.onclose = function() {
    console.log("Data channel is closed");
};
```

## 9. 与 ICE 建立连接

建立 WebRTC 连接的下一步涉及 ICE(交互式连接建立)和 SDP 协议，其中对等点的会话描述在两个对等点被交换和接受。

信令服务器用于在对等点之间发送此信息。这涉及客户端通过信令服务器交换连接元数据的一系列步骤。

### 9.1. 创建报价

首先，我们创建一个报价并将其设置为peerConnection的本地描述。然后我们将报价发送给另一个同行：

```javascript
peerConnection.createOffer(function(offer) {
    send({
        event : "offer",
        data : offer
    });
    peerConnection.setLocalDescription(offer);
}, function(error) {
    // Handle error here
});
```

此处，发送方法调用信令服务器以传递报价信息。

请注意，我们可以使用任何服务器端技术自由实现发送方法的逻辑。

### 9.2. 处理 ICE 候选人

其次，我们需要处理 ICE 候选人。 WebRTC 使用 ICE(交互式连接建立)协议来发现对等点并建立连接。

当我们在peerConnection上设置本地描述时，它会触发一个icecandidate事件。

此事件应将候选者传输到远程对等点，以便远程对等点可以将其添加到其远程候选者集中。

为此，我们为onicecandidate事件创建一个侦听器：

```javascript
peerConnection.onicecandidate = function(event) {
    if (event.candidate) {
        send({
            event : "candidate",
            data : event.candidate
        });
    }
};
```

当收集到所有候选者时， icecandidate事件再次触发，候选字符串为空。

我们还必须将这个候选对象传递给远程对等方。我们传递这个空的候选字符串以确保远程对等方知道所有icecandidate对象都已收集。

此外，再次触发相同的事件以指示 ICE 候选对象收集已完成，候选对象的值在该事件中设置为null 。这不需要传递给远程对等点。

### 9.3. 接收 ICE 候选人

第三，我们需要处理其他节点发送的 ICE 候选。

远程对等方在收到此候选人后，应将其添加到其候选人池中：

```javascript
peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
```

### 9.4. 收到报价

之后，当其他对等方收到报价时，必须将其设置为远程描述。此外，它必须生成一个answer，发送给发起方：

```javascript
peerConnection.setRemoteDescription(new RTCSessionDescription(offer));
peerConnection.createAnswer(function(answer) {
    peerConnection.setLocalDescription(answer);
        send({
            event : "answer",
            data : answer
        });
}, function(error) {
    // Handle error here
});
```

### 9.5. 收到答复

最后，发起对等点收到答案并将其设置为远程描述：

```javascript
handleAnswer(answer){
    peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
}
```

这样，WebRTC 就建立了一个成功的连接。

现在，我们可以直接在两个对等点之间发送和接收数据，而无需信令服务器。

## 10.发送消息

现在我们已经建立了连接，我们可以使用dataChannel的send方法在对等点之间发送消息：

```javascript
dataChannel.send(“message”);
```

同样，要在另一个节点上接收消息，让我们为onmessage事件创建一个侦听器：

```javascript
dataChannel.onmessage = function(event) {
    console.log("Message:", event.data);
};
```

要在数据通道上接收消息，我们还必须在peerConnection对象上添加一个回调：

```javascript
peerConnection.ondatachannel = function (event) {
    dataChannel = event.channel;
};
```

通过这一步，我们创建了一个功能齐全的 WebRTC 数据通道。我们现在可以在客户端之间发送和接收数据。此外，我们可以为此添加视频和音频频道。

## 11.添加视频和音频通道

当WebRTC建立P2P连接后，我们可以方便的直接传输音视频流。

### 11.1. 获取媒体流

首先，我们需要从浏览器获取媒体流。WebRTC为此提供了一个API：

```javascript
const constraints = {
    video: true,audio : true
};
navigator.mediaDevices.getUserMedia(constraints).
  then(function(stream) { / use the stream / })
    .catch(function(err) { / handle the error / });
```

我们可以使用约束对象指定视频的帧速率、宽度和高度。

约束对象还允许指定在移动设备的情况下使用的相机：

```javascript
var constraints = {
    video : {
        frameRate : {
            ideal : 10,
            max : 15
        },
        width : 1280,
        height : 720,
        facingMode : "user"
    }
};
```

此外，如果我们想要启用后置摄像头，可以将facingMode的值设置为“environment”而不是“user” 。

### 11.2. 发送流

其次，我们必须将流添加到 WebRTC 对等连接对象：

```javascript
peerConnection.addStream(stream);
```

将流添加到对等连接会触发已连接对等点上的addstream事件。

### 11.3. 接收流

第三，为了接收远程端的流，我们可以创建一个监听器。

让我们将此流设置为 HTML 视频元素：

```javascript
peerConnection.onaddstream = function(event) {
    videoElement.srcObject = event.stream;
};
```

## 12.NAT问题

在现实世界中，防火墙和 NAT(网络地址遍历)设备将我们的设备连接到公共互联网。

NAT 为设备提供一个 IP 地址以供在本地网络中使用。所以，这个地址在本地网络之外是不可访问的。没有公共地址，同行无法与我们沟通。

为了解决这个问题，WebRTC 使用了两种机制：

1.  眩晕
2.  转动

## 13. 使用STUN

STUN 是解决此问题的最简单方法。在与对等方共享网络信息之前，客户端向 STUN 服务器发出请求。STUN 服务器的职责是返回它收到请求的 IP 地址。

因此，通过查询 STUN 服务器，我们可以获得自己的面向公众的 IP 地址。然后，我们将此 IP 和端口信息共享给我们要连接的对等方。其他同行可以做同样的事情来共享他们面向公众的 IP。

要使用 STUN 服务器，我们可以简单地在配置对象中传递 URL 以创建RTCPeerConnection对象：

```javascript
var configuration = {
    "iceServers" : [ {
        "url" : "stun:stun2.1.google.com:19302"
    } ]
};

```

## 14. 使用转

相比之下，TURN 是一种在 WebRTC 无法建立 P2P 连接时使用的回退机制。TURN 服务器的作用是直接在对等点之间中继数据。在这种情况下，实际的数据流流经 TURN 服务器。使用默认实现，TURN 服务器也充当 STUN 服务器。

TURN 服务器是公开可用的，即使在防火墙或代理后面，客户端也可以访问它们。

但是，使用 TURN 服务器并不是真正的 P2P 连接，因为存在中间服务器。

注意：当我们无法建立 P2P 连接时，TURN 是最后的手段。当数据流经 TURN 服务器时，它需要大量带宽，在这种情况下我们不使用 P2P。

与 STUN 类似，我们可以在同一个配置对象中提供 TURN 服务器 URL：

```javascript
{
  'iceServers': [
    {
      'urls': 'stun:stun.l.google.com:19302'
    },
    {
      'urls': 'turn:10.158.29.39:3478?transport=udp',
      'credential': 'XXXXXXXXXXXXX',
      'username': 'XXXXXXXXXXXXXXX'
    },
    {
      'urls': 'turn:10.158.29.39:3478?transport=tcp',
      'credential': 'XXXXXXXXXXXXX',
      'username': 'XXXXXXXXXXXXXXX'
    }
  ]
}
```

## 15.总结

在本教程中，我们讨论了什么是 WebRTC 项目并介绍了它的基本概念。然后我们构建了一个简单的应用程序来在两个 HTML 客户端之间共享数据。

我们还讨论了创建和建立 WebRTC 连接所涉及的步骤。

此外，我们研究了在 WebRTC 失败时使用 STUN 和 TURN 服务器作为后备机制。