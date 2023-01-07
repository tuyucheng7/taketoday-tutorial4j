## 1. 概述

在本快速教程中，我们将通过示例了解SocketException的原因。

当然，我们还将讨论如何处理异常。

## 2、 SocketException的产生原因

SocketException的最常见原因是向关闭的[套接字](https://www.baeldung.com/a-guide-to-java-sockets)连接写入数据或从中读取数据。另一个原因是在读取套接字缓冲区中的所有数据之前关闭连接。

让我们仔细看看一些常见的根本原因。

### 2.1. 慢速网络

网络连接不良可能是根本问题。设置较高的套接字连接超时可以降低慢速连接的SocketException率：

```java
socket.setSoTimeout(30000); // timeout set to 30,000 ms
```

### 2.2. 防火墙干预

网络[防火墙](https://www.baeldung.com/cs/firewalls-intro)可以关闭套接字连接。如果我们可以访问防火墙，我们可以将其关闭并查看是否可以解决问题。

[否则，我们可以使用Wireshark](https://www.wireshark.org/)等网络监控工具来检查防火墙活动。

### 2.3. 长空闲连接

空闲连接可能会被另一端遗忘(以节省资源)。如果我们必须长时间使用一个连接，我们可以发送[心跳消息](https://en.wikipedia.org/wiki/Heartbeat_message)来防止空闲状态。

### 2.4. 应用程序错误

最后但同样重要的是，SocketException可能由于我们代码中的错误或错误而发生。

为了演示这一点，让我们在端口 6699 上启动一个服务器：

```java
SocketServer server = new SocketServer();
server.start(6699);
```

服务器启动后，我们将等待来自客户端的消息：

```java
serverSocket = new ServerSocket(port);
clientSocket = serverSocket.accept();
out = new PrintWriter(clientSocket.getOutputStream(), true);
in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
String msg = in.readLine();
```

一旦我们得到它，我们将响应并关闭连接：

```java
out.println("hi");
in.close();
out.close();
clientSocket.close();
serverSocket.close();
```

因此，假设客户端连接到我们的服务器并发送“hi”：

```java
SocketClient client = new SocketClient();
client.startConnection("127.0.0.1", 6699);
client.sendMessage("hi");
```

到目前为止，一切都很好。

但是，如果客户端发送另一条消息：

```java
client.sendMessage("hi again");
```

由于客户端在连接中止后向服务器发送“hi again” ，因此发生SocketException 。

## 3. SocketException的处理 

处理SocketException非常简单直接。与任何其他已检查的异常类似，我们必须抛出它或用 try-catch 块包围它。

让我们处理示例中的异常：

```java
try {
    client.sendMessage("hi");
    client.sendMessage("hi again");
} catch (SocketException e) {
    client.stopConnection();
}
```

在这里，我们在 异常发生后关闭了客户端连接。重试将不起作用，因为连接已关闭。我们应该开始一个新的连接：

```java
client.startConnection("127.0.0.1", 6699);
client.sendMessage("hi again");
```

## 4。总结

在本文中，我们了解了导致SocketException 的原因以及如何处理它。