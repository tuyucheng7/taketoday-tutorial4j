## 1. 概述

在本教程中，我们将重点介绍[Java 套接字编程](https://www.baeldung.com/a-guide-to-java-sockets)的超时异常。我们的目标是了解为什么会出现这些异常，以及如何处理它们。

## 2.Java套接字和超时

套接字是两个计算机应用程序之间逻辑链接的一个端点。换句话说，它是应用程序用来通过网络发送和接收数据的逻辑接口。

一般来说，套接字是IP地址和端口号的组合。每个套接字都分配有一个用于标识服务的特定端口号。

基于连接的服务使用基于[TCP 的](https://www.baeldung.com/cs/udp-vs-tcp)流套接字。为此，Java 提供了用于客户端编程的java.net.Socket类。相反，服务器端 TCP/IP 编程使用java.net.ServerSocket类。

另一种套接字是基于[UDP 的](https://www.baeldung.com/udp-in-java)数据报套接字，用于无连接服务。Java为 UDP 操作提供了java.net.DatagramSocket 。但是，在本教程中，我们将重点关注 TCP/IP 套接字。

## 3.连接超时

### 3.1. 什么是“连接超时”？

为了从客户端建立到服务器的连接，调用了套接字构造函数，它实例化了一个套接字对象。构造函数将远程主机地址和端口号作为输入参数。之后，它会尝试根据给定的参数与远程主机建立连接。

该操作会阻塞所有其他进程，直到成功建立连接。但是，如果在一定时间后连接不成功，程序将抛出带有“连接超时”消息的ConnectionException ：

```java
java.net.ConnectException: Connection timed out: connect
```

在服务器端，ServerSocket类持续侦听传入的连接请求。当ServerSocket收到一个连接请求时，它会调用accept()方法来实例化一个新的套接字对象。同样，此方法也会阻塞，直到它与远程客户端建立成功连接。

如果 TCP 握手未完成，则连接仍然不成功。结果，程序抛出一个IOException指示在建立新连接时发生错误。

### 3.2. 为什么会发生？

连接超时错误可能有多种原因：

-   没有服务正在侦听远程主机上的给定端口
-   远程主机不接受任何连接
-   远程主机不可用
-   网速慢
-   没有到远程主机的转发路径

### 3.3. 如何处理？

阻塞时间没有限制，程序员可以为客户端和服务器操作预先设置超时选项。对于客户端，我们将首先创建一个空套接字。之后，我们将使用connect(SocketAddress endpoint, int timeout)方法并设置超时参数：

```java
Socket socket = new Socket(); 
SocketAddress socketAddress = new InetSocketAddress(host, port); 
socket.connect(socketAddress, 30000);
```

超时单位以毫秒为单位，应该大于 0。但是，如果在方法调用返回之前超时到期，则会抛出SocketTimeoutException：

```java
Exception in thread "main" java.net.SocketTimeoutException: Connect timed out
```

对于服务器端，我们将使用setSoTimeout(int timeout)方法来设置超时值。超时值定义ServerSocket.accept()方法将阻塞多长时间：

```java
ServerSocket serverSocket = new new ServerSocket(port);
serverSocket.setSoTimeout(40000);
```

同样，超时单位应该以毫秒为单位，并且应该大于 0。如果在方法返回之前超时已经过去，它将抛出SocketTimeoutException。

有时，防火墙出于安全原因会阻止某些端口。因此，当客户端尝试与服务器建立连接时，可能会出现“连接超时”错误。因此，在将端口绑定到服务之前，我们应该检查防火墙设置以查看它是否阻止了端口。

## 4.读取超时

### 4.1. 什么是“读取超时”？

InputStream中的read ()方法调用会阻塞，直到它完成从套接字中读取数据字节。该操作一直等到它从套接字中读取至少一个数据字节。但是，如果该方法在未指定的时间后未返回任何内容，它会抛出带有“读取超时”错误消息的InterrupedIOException：

```java
java.net.SocketTimeoutException: Read timed out
```

### 4.2. 为什么会发生？

从客户端来看，如果服务器响应和发送信息的时间更长，就会发生“读取超时”错误。这可能是由于互联网连接速度慢，或者主机可能处于离线状态。

从服务器端来说，当服务器读取数据的时间比预设的超时时间长时，就会发生这种情况。

### 4.3. 如何处理？

对于 TCP 客户端和服务器，我们可以使用setSoTimeout(int timeout)方法指定socketInputStream.read()方法阻塞的时间量 ：

```java
Socket socket = new Socket(host, port);
socket.setSoTimeout(30000);
```

但是，如果在方法返回之前超时已过，程序将抛出SocketTimeoutException。

## 5.总结

在本文中，我们讨论了Java套接字编程中的超时异常，并学习了如何处理它们。