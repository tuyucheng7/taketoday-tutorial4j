## 1. 概述

在本文中，我们将通过用户数据报协议([UDP](https://en.wikipedia.org/wiki/User_Datagram_Protocol))探讨与Java的网络通信。

**UDP是一种在网络上传输独立数据包的通信协议，不保证到达也不保证传递顺序**。

互联网上的大多数通信都是通过传输控制协议(TCP)进行的，但是，UDP也有它的一席之地，我们将在下一节中探讨这一点。

## 2. 为什么要使用UDP？

UDP与更常见的TCP[有很大不同](https://www.baeldung.com/cs/udp-vs-tcp)。但在考虑UDP的表面层缺点之前，重要的是要了解没有开销可以使它比TCP快得多。

除了速度之外，我们还需要记住，某些类型的通信不需要TCP的可靠性，而是看重低延迟。视频是一个很好的应用程序示例，它可能会受益于在UDP而不是TCP上运行。

## 3. 构建UDP应用程序

构建UDP应用程序与构建TCP系统非常相似；唯一的区别是我们不在客户端和服务器之间建立点对点连接。

设置也非常简单。Java附带了对UDP的内置网络支持-它是java.net包的一部分。因此，要通过UDP执行网络操作，我们只需要从java.net包中导入类：java.net.DatagramSocket和java.net.DatagramPacket。

在接下来的部分中，我们将学习如何设计通过UDP进行通信的应用程序；我们将为此应用程序使用流行的echo协议。

首先，我们将构建一个echo服务器，它可以将发送给它的任何消息发回，然后是一个echo客户端，它可以向服务器发送任意消息，最后，我们将测试应用程序以确保一切正常。

## 4. 服务器

在UDP通信中，单个消息封装在通过DatagramSocket发送的DatagramPacket中。

让我们从设置一个简单的服务器开始：

```java
public class EchoServer extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

    public EchoServer() {
        socket = new DatagramSocket(4445);
    }

    public void run() {
        running = true;

        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            String received = new String(packet.getData(), 0, packet.getLength());

            if (received.equals("end")) {
                running = false;
                continue;
            }
            socket.send(packet);
        }
        socket.close();
    }
}
```

我们创建一个全局DatagramSocket，我们将在整个过程中使用它来发送数据包，一个字节数组来包装我们的消息，以及一个名为running的状态变量。

为简单起见，服务器扩展了Thread，因此我们可以在run方法中实现所有内容。

在run方法中，我们创建了一个while循环，它一直运行到running因某些错误或来自客户端的终止消息而变为false为止。

在循环的顶部，我们实例化一个DatagramPacket来接收传入的消息。

接下来，我们调用套接字上的receive方法。此方法会阻塞，直到消息到达并将消息存储在传递给它的DatagramPacket的字节数组中。

收到消息后，我们检索客户端的地址和端口，因为我们要发回响应。

接下来，我们创建一个DatagramPacket用于向客户端发送消息。请注意接收数据包的签名差异。这一个还需要我们向其发送消息的客户端的地址和端口。

## 5. 客户端

现在让我们为这个新服务器推出一个简单的客户端：

```java
public class EchoClient {
    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    public EchoClient() {
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");
    }

    public String sendEcho(String msg) {
        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        socket.send(packet);
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());
        return received;
    }

    public void close() {
        socket.close();
    }
}
```

该代码与服务器的代码没有什么不同。我们有全局DatagramSocket和服务器地址。我们在构造函数中实例化这些。

我们有一个单独的方法将消息发送到服务器并返回响应。

我们首先将字符串消息转换为字节数组，然后创建用于发送消息的DatagramPacket。

接下来-我们发送消息。我们立即将DatagramPacket转换为接收包。

当回声到达时，我们将字节转换为字符串并返回字符串。

## 6. 测试

在类UDPTest.java中，我们只需创建一个测试来检查我们的两个应用程序的回显能力：

```java
public class UDPTest {
    EchoClient client;

    @Before
    public void setup(){
        new EchoServer().start();
        client = new EchoClient();
    }

    @Test
    public void whenCanSendAndReceivePacket_thenCorrect() {
        String echo = client.sendEcho("hello server");
        assertEquals("hello server", echo);
        echo = client.sendEcho("server is working");
        assertFalse(echo.equals("hello server"));
    }

    @After
    public void tearDown() {
        client.sendEcho("end");
        client.close();
    }
}
```

在设置中，我们启动服务器并创建客户端。在tearDown方法中，我们向服务器发送终止消息，以便它可以关闭，同时我们关闭客户端。

## 7. 总结

在本文中，我们了解了用户数据报协议并成功构建了我们自己的通过UDP进行通信的客户端-服务器应用程序。