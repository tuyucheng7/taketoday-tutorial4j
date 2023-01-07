## 1. 概述

术语套接字编程指的是编写跨多台计算机执行的程序，其中设备都使用网络相互连接。

我们可以使用两种通信协议进行套接字编程：用户数据报协议 (UDP) 和传输控制协议 (TCP)。

两者之间的主要区别在于 UDP 是无连接的，这意味着客户端和服务器之间没有会话，而 TCP 是面向连接的，这意味着必须首先在客户端和服务器之间建立独占连接才能进行通信.

本教程介绍了TCP/IP 网络上的套接字编程，并演示了如何使用Java编写客户端/服务器应用程序。UDP 不是主流协议，因此可能不会经常遇到。

## 2.项目设置

Java 提供了一组类和接口，用于处理客户端和服务器之间的低级通信细节。

这些主要包含在java.net包中，因此我们需要进行以下导入：

```java
import java.net.;
```

我们还需要java.io包，它为我们提供了在通信时写入和读取的输入和输出流：

```java
import java.io.;
```

为了简单起见，我们将在同一台计算机上运行我们的客户端和服务器程序。如果我们要在不同的联网计算机上执行它们，唯一会改变的是 IP 地址。在这种情况下，我们将在127.0.0.1上使用localhost。

## 3. 简单例子

让我们亲身体验涉及客户端和服务器的最基本示例。这将是一个双向通信应用程序，客户端向服务器问候，服务器响应。

我们将使用以下代码在名为GreetServer.java的类中创建服务器应用程序。

我们将包含主要方法和全局变量，以引起人们对我们将如何在本文中运行所有服务器的注意。对于本文中的其余示例，我们将省略此类重复代码：

```java
public class GreetServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void start(int port) {
        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String greeting = in.readLine();
            if ("hello server".equals(greeting)) {
                out.println("hello client");
            }
            else {
                out.println("unrecognised greeting");
            }
    }

    public void stop() {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }
    public static void main(String[] args) {
        GreetServer server=new GreetServer();
        server.start(6666);
    }
}
```

我们还将使用以下代码创建一个名为GreetClient.java的客户端：

```java
public class GreetClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendMessage(String msg) {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    public void stopConnection() {
        in.close();
        out.close();
        clientSocket.close();
    }
}
```

现在让我们启动服务器。在我们的 IDE 中，我们只需将它作为Java应用程序运行即可。

然后我们将使用单元测试向服务器发送问候语，确认服务器发送问候语作为响应：

```java
@Test
public void givenGreetingClient_whenServerRespondsWhenStarted_thenCorrect() {
    GreetClient client = new GreetClient();
    client.startConnection("127.0.0.1", 6666);
    String response = client.sendMessage("hello server");
    assertEquals("hello client", response);
}
```

这个例子让我们对本文后面的内容有所了解。因此，我们可能还不能完全理解这里发生的事情。

在接下来的部分中，我们将使用这个简单的示例来剖析套接字通信，并深入研究更复杂的通信。

## 4. 套接字如何工作

我们将使用上面的示例逐步完成本节的不同部分。

根据定义，套接字是网络上不同计算机上运行的两个程序之间双向通信链路的一个端点。[套接字绑定到端口号](https://www.baeldung.com/cs/port-vs-socket)，以便传输层可以识别数据要发送到的应用程序。

### 4.1. 服务器

通常，服务器在网络上的特定计算机上运行，并具有绑定到特定端口号的套接字。在我们的例子中，我们将使用与客户端相同的计算机，并在端口6666上启动服务器：

```java
ServerSocket serverSocket = new ServerSocket(6666);
```

服务器只是等待，监听客户端发出连接请求的套接字。这发生在下一步中：

```java
Socket clientSocket = serverSocket.accept();
```

当服务器代码遇到accept方法时，它会阻塞，直到客户端向它发出连接请求。

如果一切顺利，服务器将接受连接。接受后，服务器获得一个新套接字clientSocket，绑定到相同的本地端口6666，并将其远程端点设置为客户端的地址和端口。

此时，新的Socket对象将服务器置于与客户端的直接连接中。然后我们可以访问输出流和输入流，分别向客户端写入消息和从客户端接收消息：

```java
PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
```

现在服务器能够与客户端无休止地交换消息，直到套接字用它的流关闭。

但是，在我们的示例中，服务器只能在关闭连接之前发送问候响应。这意味着如果我们再次运行测试，服务器将拒绝连接。

为了允许通信的连续性，我们必须在while循环内从输入流中读取，并且仅在客户端发送终止请求时退出。我们将在下一节中看到这一点。

对于每个新客户端，服务器都需要一个由accept调用返回的新套接字。我们使用serverSocket继续监听连接请求，同时满足连接客户端的需求。在我们的第一个示例中，我们还没有考虑到这一点。

### 4.2. 客户端

客户端必须知道运行服务器的机器的主机名或 IP，以及服务器正在侦听的端口号。

为了发出连接请求，客户端尝试在服务器的机器和端口上与服务器会合：

```java
Socket clientSocket = new Socket("127.0.0.1", 6666);
```

客户端还需要向服务器表明自己的身份，因此它绑定到系统分配的本地端口号，它将在此连接期间使用。我们自己不处理这个。

上面的构造函数只在服务器接受连接时创建一个新的套接字；否则，我们将得到连接被拒绝的异常。创建成功后，我们就可以从中获取输入输出流来与服务器通信：

```java
PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
```

客户端的输入流连接到服务器的输出流，就像服务器的输入流连接到客户端的输出流一样。

## 5.持续沟通

我们当前的服务器会阻塞，直到客户端连接到它，然后再次阻塞以侦听来自客户端的消息。在单个消息之后，它会关闭连接，因为我们还没有处理连续性。

因此，它仅对 ping 请求有帮助。但是想象一下我们想要实现一个聊天服务器；肯定需要服务器和客户端之间的连续来回通信。

我们必须创建一个 while 循环来持续观察服务器的输入流以获取传入消息。

因此，让我们创建一个名为EchoServer.java 的新服务器，其唯一目的是回显从客户端接收到的任何消息：

```java
public class EchoServer {
    public void start(int port) {
        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
        if (".".equals(inputLine)) {
            out.println("good bye");
            break;
         }
         out.println(inputLine);
    }
}
```

请注意，我们添加了一个终止条件，当我们收到句点字符时 while 循环退出。

我们将使用 main 方法启动EchoServer，就像我们为GreetServer所做的那样。这一次，我们在另一个端口上启动它，例如4444，以避免混淆。

EchoClient类似于GreetClient ，因此我们可以代码。为了清楚起见，我们将它们分开。

在不同的测试类中，我们将创建一个测试来显示对 EchoServer 的多个请求将在服务器不关闭套接字的情况下得到服务。只要我们从同一个客户端发送请求，情况就是如此。

处理多个客户端是一种不同的情况，我们将在后续部分中看到。

现在让我们创建一个设置方法来启动与服务器的连接：

```java
@Before
public void setup() {
    client = new EchoClient();
    client.startConnection("127.0.0.1", 4444);
}
```

我们还将创建一个tearDown方法来释放我们所有的资源。这是我们使用网络资源的每种情况的最佳实践：

```java
@After
public void tearDown() {
    client.stopConnection();
}
```

然后我们将用几个请求测试我们的回显服务器：

```java
@Test
public void givenClient_whenServerEchosMessage_thenCorrect() {
    String resp1 = client.sendMessage("hello");
    String resp2 = client.sendMessage("world");
    String resp3 = client.sendMessage("!");
    String resp4 = client.sendMessage(".");
    
    assertEquals("hello", resp1);
    assertEquals("world", resp2);
    assertEquals("!", resp3);
    assertEquals("good bye", resp4);
}
```

这是对初始示例的改进，在初始示例中，我们只在服务器关闭连接之前通信一次。现在我们发送一个终止信号来告诉服务器我们何时完成会话。

## 6. 有多个客户端的服务器

尽管前面的示例比第一个示例有所改进，但它仍然不是一个很好的解决方案。服务器必须能够同时为许多客户端和许多请求提供服务。

处理多个客户端是我们将在本节中介绍的内容。

我们将在这里看到的另一个特性是，同一个客户端可以断开连接并再次重新连接，而不会出现连接拒绝异常或服务器上的连接重置。我们以前无法做到这一点。

这意味着我们的服务器将在来自多个客户端的多个请求中变得更加健壮和有弹性。

为此，我们将为每个新客户端创建一个新套接字，并为客户端在不同线程上的请求提供服务。同时服务的客户端数将等于运行的线程数。

主线程在侦听新连接时将运行一个 while 循环。

现在让我们看看实际效果。我们将创建另一个名为EchoMultiServer.java 的服务器。在其中，我们将创建一个处理程序线程类来管理每个客户端在其套接字上的通信：

```java
public class EchoMultiServer {
    private ServerSocket serverSocket;

    public void start(int port) {
        serverSocket = new ServerSocket(port);
        while (true)
            new EchoClientHandler(serverSocket.accept()).start();
    }

    public void stop() {
        serverSocket.close();
    }

    private static class EchoClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(
              new InputStreamReader(clientSocket.getInputStream()));
            
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (".".equals(inputLine)) {
                    out.println("bye");
                    break;
                }
                out.println(inputLine);
            }

            in.close();
            out.close();
            clientSocket.close();
    }
}
```

请注意，我们现在在while循环中调用accept。每次执行while循环时，它都会阻塞accept调用，直到有新客户端连接为止。然后为该客户端创建处理程序线程EchoClientHandler 。

线程内部发生的事情与 EchoServer 相同，我们只处理一个客户端。EchoMultiServer将这项工作委托给EchoClientHandler，以便它可以在while循环中继续监听更多的客户端。

我们仍将使用EchoClient来测试服务器。这一次，我们将创建多个客户端，每个客户端从服务器发送和接收多条消息。

让我们在端口5555上使用其 main 方法启动我们的服务器。

为清楚起见，我们仍将测试放在新套件中：

```java
@Test
public void givenClient1_whenServerResponds_thenCorrect() {
    EchoClient client1 = new EchoClient();
    client1.startConnection("127.0.0.1", 5555);
    String msg1 = client1.sendMessage("hello");
    String msg2 = client1.sendMessage("world");
    String terminate = client1.sendMessage(".");
    
    assertEquals(msg1, "hello");
    assertEquals(msg2, "world");
    assertEquals(terminate, "bye");
}

@Test
public void givenClient2_whenServerResponds_thenCorrect() {
    EchoClient client2 = new EchoClient();
    client2.startConnection("127.0.0.1", 5555);
    String msg1 = client2.sendMessage("hello");
    String msg2 = client2.sendMessage("world");
    String terminate = client2.sendMessage(".");
    
    assertEquals(msg1, "hello");
    assertEquals(msg2, "world");
    assertEquals(terminate, "bye");
}
```

我们可以根据需要创建尽可能多的此类测试用例，每个测试用例都会产生一个新的客户端，服务器将为它们提供服务。

## 七、总结

在本文中，我们重点介绍了基于 TCP/IP 的套接字编程，并用Java编写了一个简单的客户端/服务器应用程序。