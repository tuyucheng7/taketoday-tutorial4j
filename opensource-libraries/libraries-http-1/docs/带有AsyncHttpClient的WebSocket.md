## 1. 简介

AsyncHttpClient (AHC) 是一个基于 Netty 的库，旨在轻松执行异步 HTTP 调用并通过 WebSocket 协议进行通信。

在本快速教程中，我们将了解如何启动 WebSocket 连接、发送数据和处理各种控制帧。

## 2.设置

可以在[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"org.asynchttpclient" AND a%3A"async-http-client")上找到该库的最新版本。我们需要小心使用组 ID为 org.asynchttpclient的依赖项，而不是使用com.ning 的依赖项：

```xml
<dependency>
    <groupId>org.asynchttpclient</groupId>
    <artifactId>async-http-client</artifactId>
    <version>2.2.0</version>
</dependency>
```

## 3.WebSocket客户端配置

[要创建 WebSocket 客户端，我们首先必须获得本文](https://www.baeldung.com/async-http-client)所示的 HTTP 客户端，并对其进行升级以支持 WebSocket 协议。

WebSocket 协议升级的处理由WebSocketUpgradeHandler类完成。这个类实现了AsyncHandler接口，还为我们提供了一个构建器：

```java
WebSocketUpgradeHandler.Builder upgradeHandlerBuilder
  = new WebSocketUpgradeHandler.Builder();
WebSocketUpgradeHandler wsHandler = upgradeHandlerBuilder
  .addWebSocketListener(new WebSocketListener() {
      @Override
      public void onOpen(WebSocket websocket) {
          // WebSocket connection opened
      }

      @Override
      public void onClose(WebSocket websocket, int code, String reason) {
          // WebSocket connection closed
      }

      @Override
      public void onError(Throwable t) {
          // WebSocket connection error
      }
  }).build();
```

为了获取WebSocket连接对象，我们使用标准的AsyncHttpClient创建一个带有首选连接详细信息的 HTTP 请求，例如标头、查询参数或超时：

```java
WebSocket webSocketClient = Dsl.asyncHttpClient()
  .prepareGet("ws://localhost:5590/websocket")
  .addHeader("header_name", "header_value")
  .addQueryParam("key", "value")
  .setRequestTimeout(5000)
  .execute(wsHandler)
  .get();
```

## 4.发送数据

使用WebSocket对象，我们可以使用isOpen()方法检查连接是否成功打开。一旦我们建立了一个开放的连接，我们就可以使用sendTextFrame()和sendBinaryFrame()方法发送带有字符串或二进制负载的数据帧：

```java
if (webSocket.isOpen()) {
    webSocket.sendTextFrame("test message");
    webSocket.sendBinaryFrame(new byte[]{'t', 'e', 's', 't'});
}
```

## 5. 处理控制帧

WebSocket 协议支持三种类型的控制帧：ping、pong 和close。

ping 和 pong 框架主要用于实现连接的“保持活动”机制。我们可以使用sendPingFrame()和sendPongFrame()方法发送这些帧：

```java
webSocket.sendPingFrame();
webSocket.sendPongFrame();
```

关闭现有连接是通过使用sendCloseFrame()方法发送关闭帧来完成的，我们可以在其中以文本形式提供状态代码和关闭连接的原因：

```java
webSocket.sendCloseFrame(404, "Forbidden");
```

## 六. 总结

支持 WebSocket 协议，除了它提供了一种简单的方法来执行异步 HTTP 请求之外，还使 AHC 成为一个非常强大的库。