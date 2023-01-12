## 1. 概述

WebSocket 通过提供双向、全双工、实时客户端/服务器通信，为服务器和 Web 浏览器之间有效通信的限制提供了一种替代方法。服务器可以随时向客户端发送数据。因为它运行在 TCP 之上，所以它还提供了低延迟的低级通信并减少了每条消息的开销。

在本文中，我们将通过创建一个类似聊天的应用程序来了解 WebSockets 的JavaAPI。

## 2.JSR 356

[JSR 356](https://jcp.org/en/jsr/detail?id=356)或 WebSocket 的JavaAPI 指定了一个 API，Java 开发人员可以使用该 API 将 WebSockets 与其应用程序集成在一起——无论是在服务器端还是在Java客户端。

此JavaAPI 提供服务器端和客户端组件：

-   服务器：javax.websocket.server包中的所有内容。
-   客户端：javax.websocket包的内容，它由客户端 API 以及服务器和客户端的通用库组成。

## 3. 使用 WebSockets 建立聊天

我们将构建一个非常简单的类似聊天的应用程序。任何用户都可以从任何浏览器打开聊天，输入他的名字，登录聊天并开始与连接到聊天的每个人交流。

我们将从将最新的依赖项添加到pom.xml文件开始：

```xml
<dependency>
    <groupId>javax.websocket</groupId>
    <artifactId>javax.websocket-api</artifactId>
    <version>1.1</version>
</dependency>
```

最新版本可在[此处](https://search.maven.org/classic/#search|ga|1|javax.websocket-api)找到。

为了将 Java对象转换成它们的 JSON 表示，反之亦然，我们将使用 Gson：

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.0</version>
</dependency>
```

最新版本在[Maven 中央](https://search.maven.org/classic/#search|ga|1|com.google.code.gson)存储库中可用。

### 3.1. 端点配置

有两种配置端点的方法：基于注解和基于扩展。可以扩展javax.websocket.Endpoint类或使用专用的方法级注解。由于与编程模型相比，注解模型导致更清晰的代码，因此注解已成为编码的常规选择。在这种情况下，WebSocket 端点生命周期事件由以下注解处理：

-   @ServerEndpoint：如果用@ServerEndpoint 装饰，容器确保类作为WebSocket服务器监听特定 URI 空间的可用性
-   @ClientEndpoint：用这个注解修饰的类被当作WebSocket客户端
-   @OnOpen ：当启动新的WebSocket连接时，容器调用带有@OnOpen的Java方法
-   @OnMessage：一个Java方法，用@OnMessage注解，当消息发送到端点时从WebSocket容器接收信息
-   @OnError：当通信出现问题时调用带有@OnError的方法
-   @OnClose : 用于装饰一个Java方法，当WebSocket连接关闭时由容器调用

### 3.2. 编写服务器端点

我们通过使用@ServerEndpoint注解来声明一个Java类WebSocket服务器端点。我们还指定部署端点的 URI。URI 相对于服务器容器的根定义，并且必须以正斜杠开头：

```java
@ServerEndpoint(value = "/chat/{username}")
public class ChatEndpoint {

    @OnOpen
    public void onOpen(Session session) throws IOException {
        // Get session and WebSocket connection
    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException {
        // Handle new messages
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        // WebSocket connection closes
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }
}
```

上面的代码是我们的聊天类应用程序的服务器端点框架。如所见，我们有 4 个注解映射到它们各自的方法。下面你可以看到这些方法的实现：

```java
@ServerEndpoint(value="/chat/{username}")
public class ChatEndpoint {
 
    private Session session;
    private static Set<ChatEndpoint> chatEndpoints 
      = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();

    @OnOpen
    public void onOpen(
      Session session, 
      @PathParam("username") String username) throws IOException {
 
        this.session = session;
        chatEndpoints.add(this);
        users.put(session.getId(), username);

        Message message = new Message();
        message.setFrom(username);
        message.setContent("Connected!");
        broadcast(message);
    }

    @OnMessage
    public void onMessage(Session session, Message message) 
      throws IOException {
 
        message.setFrom(users.get(session.getId()));
        broadcast(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
 
        chatEndpoints.remove(this);
        Message message = new Message();
        message.setFrom(users.get(session.getId()));
        message.setContent("Disconnected!");
        broadcast(message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    private static void broadcast(Message message) 
      throws IOException, EncodeException {
 
        chatEndpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote().
                      sendObject(message);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
```

当新用户登录时(@OnOpen)立即映射到活动用户的数据结构。然后，创建一条消息并使用广播方法将其发送到所有端点。

每当任何连接的用户发送新消息(@OnMessage)时，也会使用此方法——这是聊天的主要目的。

如果在某个时候发生错误，带有注解@OnError的方法会处理它。可以使用此方法记录有关错误的信息并清除端点。

最后，当用户不再连接到聊天时，@OnClose方法会清除端点并向所有用户广播用户已断开连接。

## 4.消息类型

WebSocket 规范支持两种在线数据格式——文本和二进制。API 支持这两种格式，添加了使用规范中定义的Java对象和健康检查消息 (ping-pong) 的功能：

-   Text：任何文本数据(java.lang.String，原语或它们的等效包装类)
-   Binary : 由java.nio.ByteBuffer或byte[](字节数组)表示的二进制数据(例如音频、图像等)
-  Java对象：该 API 使可以在代码中使用本机(Java 对象)表示，并使用自定义转换器(编码器/解码器)将它们转换为 WebSocket 协议允许的兼容在线格式(文本、二进制)
-   Ping-Pong：javax.websocket.PongMessage是 WebSocket 对端发送的响应健康检查 (ping) 请求的确认

对于我们的应用程序，我们将使用Java 对象。我们将创建用于编码和解码消息的类。

### 4.1. 编码器

编码器采用Java对象并生成适合作为消息传输的典型表示，例如 JSON、XML 或二进制表示。可以通过实现Encoder.Text<T>或Encoder.Binary<T>接口来使用编码器。

在下面的代码中，我们定义了要编码的Message类，在方法encode中，我们使用 Gson 将Java对象编码为 JSON：

```java
public class Message {
    private String from;
    private String to;
    private String content;
    
    //standard constructors, getters, setters
}
public class MessageEncoder implements Encoder.Text<Message> {

    private static Gson gson = new Gson();

    @Override
    public String encode(Message message) throws EncodeException {
        return gson.toJson(message);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
    }

    @Override
    public void destroy() {
        // Close resources
    }
}
```

### 4.2. 解码器

解码器与编码器相反，用于将数据转换回Java对象。可以使用Decoder.Text<T>或Decoder.Binary<T>接口实现解码器。

正如我们在编码器中看到的那样，解码方法是我们获取在发送到端点的消息中检索到的 JSON 并使用 Gson 将其转换为名为Message 的Java类的地方：

```java
public class MessageDecoder implements Decoder.Text<Message> {

    private static Gson gson = new Gson();

    @Override
    public Message decode(String s) throws DecodeException {
        return gson.fromJson(s, Message.class);
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
    }

    @Override
    public void destroy() {
        // Close resources
    }
}
```

### 4.3. 在 Server Endpoint 中设置编码器和解码器

让我们通过在类级别注解@ServerEndpoint添加为编码和解码数据而创建的类来将所有内容放在一起：

```java
@ServerEndpoint( 
  value="/chat/{username}", 
  decoders = MessageDecoder.class, 
  encoders = MessageEncoder.class )
```

每次将消息发送到端点时，它们都会自动转换为 JSON 或Java对象。

## 5.总结

在本文中，我们了解了 WebSockets 的JavaAPI 是什么，以及它如何帮助我们构建诸如实时聊天之类的应用程序。

我们看到了用于创建端点的两种编程模型：注解和编程。我们使用注解模型为我们的应用程序定义了一个端点以及生命周期方法。

此外，为了能够在服务器和客户端之间来回通信，我们看到我们需要编码器和解码器来将Java对象转换为 JSON，反之亦然。

JSR 356 API 非常简单，基于注解的编程模型使得构建 WebSocket 应用程序变得非常容易。

要运行我们在示例中构建的应用程序，我们需要做的就是在 Web 服务器中部署 war 文件并转到 URL：http://localhost:8080/java-websocket/。可以在[此处](https://github.com/eugenp/tutorials/tree/master/java-websocket)找到存储库的链接。