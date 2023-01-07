## 1. 概述

在本快速教程中，我们将学习如何查找连接到服务器的客户端计算机的 IP 地址。

我们将创建一个简单的客户端-服务器场景，以允许我们探索可用于 TCP/IP 通信的java.net API。

## 2. 背景

Java 应用程序使用套接字通过 Internet 进行通信和发送数据。Java 为客户端应用程序提供了java.net.Socket类。

java.net.ServerSocket类用于基于[TCP /IP](https://www.baeldung.com/cs/udp-vs-tcp)的服务器端套接字实现。但是，我们只关注 TCP/IP 应用程序。

## 3. 示例用例

假设我们有一个应用程序服务器在我们的系统上运行。该服务器向客户端发送问候消息。在这种情况下，服务器使用 TCP 套接字进行通信。

应用服务器绑定到特定的 TCP 端口。它的套接字地址是该端口和本地网络接口的 IP 地址的组合。出于这个原因，客户端应该使用这个特定的套接字地址来连接到服务器。

## 4. 示例应用

现在我们已经定义了我们的用例，让我们从构建服务器开始。

### 4.1. 应用服务器

首先，我们需要实例化一个ServerSocket来监听传入的连接请求。ServerSocket类的构造函数需要一个端口号作为参数：

```java
public class ApplicationServer {

    private ServerSocket serverSocket;
    private Socket connectedSocket;
  
    public void startServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        connectedSocket = serverSocket.accept();
        //...

```

### 4.2. 获取客户端IP地址

现在我们已经为传入的客户端建立了套接字，让我们看看如何获取客户端的 IP 地址。Socket实例包含远程客户端的套接字地址。我们可以使用getRemoteSocketAddress方法来检查它。

getRemoteSocketAddress方法返回一个SocketAddress类型的对象。这是一个抽象的Java类。在这个例子中，我们知道它是一个 TCP/IP 连接，所以我们可以将它转换为InetSocketAddress：

```java
InetSocketAddress socketAddress = (InetSocketAddress) connectedSocket.getRemoteSocketAddress();
```

正如我们已经看到的，套接字地址是 IP 地址和端口号的组合。我们可以使用getAddress来获取 IP 地址。这将返回一个InetAddress对象。但是，我们也可以使用getHostAddress来获取 IP 地址的字符串表示形式：

```java
String clientIpAddress = socketAddress.getAddress()
    .getHostAddress();
```

### 4.3. 向客户发送问候消息

现在，服务器和客户端可以交换问候消息：

```java
String msg = in.readLine();
System.out.println("Message received from the client :: " + msg);
PrintWriter out = new PrintWriter(connectedSocket.getOutputStream(), true);
out.println("Hello Client !!");
```

## 5. 测试应用

现在让我们构建一个客户端应用程序来测试我们的代码。该客户端将在单独的计算机上运行并连接到我们的服务器。

### 5.1. 构建客户端应用程序

首先，我们需要使用 IP 地址和端口号与服务建立Socket连接：

```java
public class ApplicationClient {
    public void connect(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
    }
}
```

与服务器应用程序类似，我们将使用BufferedReader和PrintWriter来读取和写入套接字。为了向服务器发送消息，让我们创建一个方法来写入连接的套接字：

```java
public void sendGreetings(String msg) throws IOException {
    out.println(msg);
    String reply = in.readLine();
    System.out.println("Reply received from the server :: " + reply);
}

```

### 5.2. 运行应用程序

接下来，让我们运行客户端应用程序，为其[选择一个空闲端口。](https://www.baeldung.com/java-free-port)

之后，我们需要从另一台 PC 启动客户端应用程序。对于这个例子，假设服务器机器的 IP 地址是192.168.0.100并且端口 5000 是空闲的：

```java
java -cp clientaddress.cn.tuyucheng.taketoday.ApplicationClient 192.168.0.100 5000 Hello
```

在这里，我们假设客户端和服务器在同一个网络上。客户端与服务器建立连接成功后，服务器控制台会打印出客户端的IP地址。

例如，如果客户端 IP 地址是 192.168.0.102，我们应该能够在控制台中看到它：

```nginx
IP address of the connected client :: 192.168.0.102
```

### 5.3. 后台发生了什么？

通常，当应用程序服务器启动时，ServerSocket使用给定的端口号和通配符 IP 地址实例化一个套接字对象。之后，它将其状态更改为“侦听”以接收传入的连接请求。然后，当客户端发送连接请求时，ServerSocket通过调用accept方法实例化一个新的套接字 。

新创建的套接字实例包含服务器以及远程客户端的 IP 地址和端口。对于服务器 IP 地址，ServerSocket类使用它接收传入请求的本地网络接口的 IP 地址。然后，为了获得远程客户端 IP 地址，它解码接收到的 TCP 数据包的 IP 标头并使用源地址。

## 六，总结

在本文中，我们定义了一个示例客户端-服务器用例，并使用[Java 套接字编程](https://www.baeldung.com/a-guide-to-java-sockets)来查找连接到服务器的客户端的 IP 地址。