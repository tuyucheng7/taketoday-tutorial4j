## 1. 概述

在本教程中，我们将仔细研究 Java“ *[java.net.SocketException](https://www.baeldung.com/java-socketexception)：“Broken pipeline* ”错误。在第一步中，我们将演示如何重现此异常。我们的下一步将是了解异常的主要原因，然后我们将了解如何解决此问题。

## 2. 实际例子

现在，让我们看一个生成错误“ *java.net.SocketException: “Broken pipeline* ”的示例。

简而言之，当一个设备尝试从另一台已死亡或连接已断开的设备读取或写入数据时，通常会发生管道损坏。

当连接关闭时，必须建立新的连接才能继续传输数据。否则，数据将停止传输。

### 2.1. 设置客户端和服务器

为了在本地进行模拟，我们将使用一个*Server*类作为我们的 Web 服务器，并使用一个*Client*类作为我们的客户端计算机。

一旦我们关闭[服务器套接字](https://www.baeldung.com/a-guide-to-java-sockets)，连接到该套接字的客户端仍然会发送消息并接收错误消息。

如果服务器向客户端发送一些响应，并且客户端同时失去连接，也会发生这种情况。

第一步，让我们创建一个名为*Server*的服务器类，监听端口*1234*：

```java
public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Server listening on port 1234...");

            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());
            //Add some delay for reading from client
            Thread.sleep(2000);
            InputStream in = clientSocket.getInputStream();
            System.out.println("Reading from client:" + in.read());
            in.close();
            clientSocket.close();
            serverSocket.close();
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}复制
```

其次，让我们创建一个客户端*Client*并将其附加到*1234*端口套接字：

```java
public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 1234);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write("HELLO".getBytes());
            System.out.println("Writing to server..");
            //Here we are writing again.
            outputStream.write("HI".getBytes());
            System.out.println("Writing to server again..");
            System.out.println("Closing client.");
            outputStream.close();
            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}复制
```

在这里，我们向服务器发送一些消息，[服务器正在读取](https://www.baeldung.com/java-inputstream-server-socket)并打印该消息。一旦我们运行服务器并启动客户端，我们就不会看到任何错误，因为在服务器关闭套接字之前发送了数据：

```bash
// Server console
Server listening on port 12345...
Client connected: /127.0.0.1
Reading from client:66

// Client console
writing to server..
writing to server again..
Closing client.
复制
```

### 2.2. 重现断管错误

为了得到错误，让我们延迟从*客户端*发送下一条消息，直到服务器关闭连接：

```java
public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 1234);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write("HELLO".getBytes());
            System.out.println("Writing to server..");
            // Simulating a delay after writing to the socket
            Thread.sleep(3000);
            outputStream.write("HI".getBytes());
            System.out.println("Writing to server again..");
            System.out.println("Closing client.");
            outputStream.close();
            socket.close();
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
复制
```

让我们再次运行它，看看服务器套接字已关闭，如果客户端发送消息，则会返回一个损坏的管道错误：

```bash
// Server console
Server listening on port 12345...
Client connected: /127.0.0.1
Reading from client:66

// Client console
Writing to server..
java.net.SocketException: Broken pipe (Write failed)
	at java.net.SocketOutputStream.socketWrite0(Native Method)
	at java.net.SocketOutputStream.socketWrite(SocketOutputStream.java:111)
	at java.net.SocketOutputStream.write(SocketOutputStream.java:143)
	at <span class="pl-s1">com</span>.<span class="pl-s1">baeldung</span>.<span class="pl-s1">socketexception</span>.<span class="pl-s1">brokenpipe</span>.Client.main(Client.java:18)
复制
```

## 3. 原因

此错误的一个示例是**客户端程序（例如加载网站的浏览器窗口）在从服务器完全读取数据之前崩溃或终止。如果连接关闭，此后客户端向服务器写入数据的任何尝试都会导致“管道损坏”错误。**

对于网络套接字，如果拔掉网络电缆或另一端的进程无法正常工作，则可能会发生这种情况。在这种情况下，连接可能意外终止，或者网络可能遇到问题。

就Java而言，没有专门的*BrokenPipeException*。此错误通常与其他错误捆绑在一起，例如*SocketException*和*IOException*。

客户端失去连接可能有多种原因，包括在服务器响应之前关闭浏览器、服务器过载或响应时间过长。

## 4、解决方案

无法保证客户端/服务器始终等待正常连接关闭。但是，仍然可以有效地处理管道破裂错误。

**始终建议确保客户端和服务器正确处理套接字连接并正常关闭流和套接字，以管理 Java 的“损坏的管道”错误。**

我们还必须有效地管理超时并快速响应。

再次强调，没有通用的解决办法。我们需要找出根本问题并适当解决。

## 5. 结论

在本文中，我们了解了 Java 的“ *java.net.SocketException Broken pipeline* ”错误。然后，我们讨论了错误是如何产生的，并了解了异常的原因。最后，我们研究了处理错误的可能方法。