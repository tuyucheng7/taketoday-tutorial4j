## 1. 概述

[IP 地址](https://www.baeldung.com/cs/ipv4-vs-ipv6)或互联网协议地址唯一标识互联网上的设备。因此，了解运行我们应用程序的设备的身份是某些应用程序的关键部分。

在本教程中，我们将检查使用Java检索计算机 IP 地址的各种方法。

## 2.查找本地IP地址

首先，我们来看一些获取本机本地IPv4地址的方法。

### 2.1.Java网络库的本地地址

此方法使用JavaNet 库建立 UDP 连接：

```java
try (final DatagramSocket datagramSocket = new DatagramSocket()) {
    datagramSocket.connect(InetAddress.getByName("8.8.8.8"), 12345);
    return datagramSocket.getLocalAddress().getHostAddress();
}
```

在这里，为简单起见，我们使用 Google 的主要 DNS 作为我们的目标主机并提供 IP 地址8.8.8.8。Java Net 库此时仅检查地址格式的有效性，因此地址本身可能无法访问。此外，我们使用随机端口12345通过socket.connect()方法创建 UDP 连接。在底层，它设置发送和接收数据所需的所有变量，包括机器的本地地址，而不实际向目标主机发送任何请求。

虽然此解决方案在 Linux 和 Windows 计算机上运行良好，但在 macOS 上存在问题并且不会返回预期的 IP 地址。

### 2.2. 带套接字连接的本地地址

或者，我们可以通过可靠的互联网连接使用套接字连接来查找 IP 地址：

```java
try (Socket socket = new Socket()) {
    socket.connect(new InetSocketAddress("google.com", 80));
    return socket.getLocalAddress().getHostAddress();
}
```

在这里，再次为简单起见，我们使用google.com和端口80上的连接来获取主机地址。我们可以使用任何其他 URL 来创建套接字连接，只要它是可访问的。

### 2.3. 复杂网络情况的注意事项

上面列出的方法在简单的网络情况下效果很好。但是，在机器具有更多网络接口的情况下，行为可能无法预测。

换句话说，从上述函数返回的 IP 地址将是机器上首选网络接口的地址。因此，它可能与我们期望的不同。具体需要，我们可以[查到Client Connected to a Server 的IP Address](https://www.baeldung.com/java-client-get-ip-address)。

## 3.找到公共IP地址

类似于本地IP地址，我们可能想知道当前机器的公网IP地址。公共 IP 地址是可从 Internet 访问的 IPv4 地址。此外，它可能无法唯一标识查找地址的机器。例如，同一路由器下的多台主机具有相同的公网IP地址。

简单地说，我们可以连接到 Amazon AWS checkip.amazonaws.com URL 并读取响应：

```java
String urlString = "http://checkip.amazonaws.com/";
URL url = new URL(urlString);
try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
    return br.readLine();
}
```

这在大多数情况下都很有效。但是，我们明确依赖于无法保证可靠性的外部来源。因此，作为回退，我们可以使用这些 URL 中的任何一个来检索公共 IP 地址：

-   https://ipv4.icanhazip.com/
-   http://myexternalip.com/raw
-   http://ipecho.net/plain

## 4。总结

在本文中，我们学习了如何查找当前机器的 IP 地址以及如何使用Java检索它们。我们还研究了检查本地和公共 IP 地址的各种方法。