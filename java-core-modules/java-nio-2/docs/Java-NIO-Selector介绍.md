## 1. 概述

在本文中，我们将探索JavaNIO 的Selector组件的介绍部分。

选择器提供了一种机制，用于监视一个或多个 NIO 通道并识别一个或多个通道何时可用于数据传输。

这样，单个线程可用于管理多个通道，从而管理多个网络连接。

## 2. 为什么要使用选择器？

使用选择器，我们可以使用一个线程而不是多个线程来管理多个通道。线程之间的上下文切换对于操作系统来说是昂贵的，此外，每个线程都会占用内存。

因此，我们使用的线程越少越好。然而，重要的是要记住，现代操作系统和 CPU 在多任务处理方面不断进步，因此多线程的开销会随着时间的推移不断减少。

在这里，我们将讨论如何使用选择器在单个线程中处理多个通道。

另请注意，选择器不仅可以帮助你读取数据，还可以帮助你读取数据。他们还可以侦听传入的网络连接并通过慢速通道写入数据。

## 3.设置

要使用选择器，我们不需要任何特殊设置。我们需要的所有类都在核心java.nio包中，我们只需要导入我们需要的。

之后，我们可以用一个选择器对象注册多个通道。当任何通道上发生 I/O 活动时，选择器都会通知我们。这就是我们如何在单个线程上读取大量数据源。

我们使用选择器注册的任何频道都必须是SelectableChannel的子类。这些是一种特殊类型的通道，可以置于非阻塞模式。

## 4. 创建一个选择器

可以通过调用Selector类的静态open方法来创建选择器，该方法将使用系统的默认选择器提供程序来创建新的选择器：

```java
Selector selector = Selector.open();
```

## 5.注册可选频道

为了让选择器监控任何通道，我们必须向选择器注册这些通道。我们通过调用可选通道的注册方法来完成此操作。

但是在向选择器注册通道之前，它必须处于非阻塞模式：

```java
channel.configureBlocking(false);
SelectionKey key = channel.register(selector, SelectionKey.OP_READ);
```

这意味着我们不能将FileChannel与选择器一起使用，因为它们不能像我们使用套接字通道那样切换到非阻塞模式。

第一个参数是我们之前创建的Selector对象，第二个参数定义了一个 interest set ，意思是我们有兴趣通过选择器在监听通道中监听哪些事件。

我们可以监听四种不同的事件，每一种都由SelectionKey类中的常量表示：

-   连接——当客户端尝试连接到服务器时。由SelectionKey.OP_CONNECT表示
-   接受——当服务器接受来自客户端的连接时。由SelectionKey.OP_ACCEPT表示
-   读取——当服务器准备好从通道读取时。由SelectionKey.OP_READ表示
-   写入——当服务器准备好写入通道时。由SelectionKey.OP_WRITE表示

返回的对象SelectionKey表示可选通道向选择器的注册。我们将在下一节中进一步研究它。

## 6. SelectionKey对象

正如我们在上一节中看到的，当我们向选择器注册一个通道时，我们会得到一个SelectionKey对象。该对象保存表示通道注册的数据。

它包含一些重要的属性，我们必须很好地理解这些属性才能在通道上使用选择器。我们将在以下小节中查看这些属性。

### 6.1. 兴趣集

兴趣集定义了我们希望选择器在此频道上注意的事件集。它是一个整数值；我们可以通过以下方式获取这些信息。

首先，我们有SelectionKey的interestOps方法返回的兴趣集。然后我们在前面看到的SelectionKey中有事件常量。

当我们 AND 这两个值时，我们得到一个布尔值，告诉我们事件是否正在被监视：

```java
int interestSet = selectionKey.interestOps();

boolean isInterestedInAccept  = interestSet & SelectionKey.OP_ACCEPT;
boolean isInterestedInConnect = interestSet & SelectionKey.OP_CONNECT;
boolean isInterestedInRead    = interestSet & SelectionKey.OP_READ;
boolean isInterestedInWrite   = interestSet & SelectionKey.OP_WRITE;
```

### 6.2. 准备好的套装

就绪集定义通道准备好处理的事件集。它也是一个整数值；我们可以通过以下方式获取这些信息。

我们已经获得了SelectionKey的readyOps方法返回的就绪集。当我们将此值与事件常量进行 AND 操作时，就像我们在兴趣集的情况下所做的那样，我们会得到一个布尔值，表示通道是否已准备好接收特定值。

另一种更短的替代方法是使用SelectionKey的便捷方法来达到同样的目的：

```java
selectionKey.isAcceptable();
selectionKey.isConnectable();
selectionKey.isReadable();
selectionKey.isWriteable();
```

### 6.3. 这个频道

从SelectionKey对象访问正在观看的频道非常简单。我们只是调用通道方法：

```java
Channel channel = key.channel();
```

### 6.4. 选择器

就像获取频道一样，从SelectionKey对象中获取Selector对象也非常简单：

```java
Selector selector = key.selector();
```

### 6.5. 附加对象

我们可以将对象附加到SelectionKey。有时我们可能想给频道一个自定义 ID 或附加任何我们可能想要跟踪的Java对象。

附加对象是一种方便的方法。以下是你如何从SelectionKey附加和获取对象：

```java
key.attach(Object);

Object object = key.attachment();
```

或者，我们可以选择在频道注册期间附加一个对象。我们将它作为第三个参数添加到通道的register方法中，如下所示：

```java
SelectionKey key = channel.register(
  selector, SelectionKey.OP_ACCEPT, object);
```

## 7.通道键选择

到目前为止，我们已经了解了如何创建选择器、向其注册通道以及检查表示通道向选择器注册的SelectionKey对象的属性。

这只是过程的一半，现在我们必须执行一个连续的过程来选择我们之前看到的就绪集。我们使用选择器的select方法进行选择，如下所示：

```java
int channels = selector.select();
```

此方法会阻塞，直到至少有一个通道准备好进行操作。返回的整数表示其通道已准备好进行操作的键数。

接下来，我们通常会检索一组选定的键进行处理：

```java
Set<SelectionKey> selectedKeys = selector.selectedKeys();
```

我们获得的集合是SelectionKey对象，每个键代表一个已准备好进行操作的已注册通道。

在此之后，我们通常会迭代这个集合，对于每个键，我们都会获得通道并执行我们感兴趣的集合中出现的任何操作。

在通道的生命周期中，它可能会被选择多次，因为它的键出现在不同事件的就绪集中。这就是为什么我们必须有一个连续的循环来在通道事件发生时捕获和处理它们。

## 8. 完整例子

为了巩固我们在前面部分中获得的知识，我们将构建一个完整的客户端-服务器示例。

为了便于测试我们的代码，我们将构建一个回显服务器和一个回显客户端。在这种设置中，客户端连接到服务器并开始向它发送消息。服务器回显每个客户端发送的消息。

当服务器遇到特定消息时，例如end，它会将其解释为通信结束并关闭与客户端的连接。

### 8.1. 服务器

这是我们的EchoServer.java代码：

```java
public class EchoServer {

    private static final String POISON_PILL = "POISON_PILL";

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress("localhost", 5454));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        ByteBuffer buffer = ByteBuffer.allocate(256);

        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();
            while (iter.hasNext()) {

                SelectionKey key = iter.next();

                if (key.isAcceptable()) {
                    register(selector, serverSocket);
                }

                if (key.isReadable()) {
                    answerWithEcho(buffer, key);
                }
                iter.remove();
            }
        }
    }

    private static void answerWithEcho(ByteBuffer buffer, SelectionKey key)
      throws IOException {
 
        SocketChannel client = (SocketChannel) key.channel();
        client.read(buffer);
        if (new String(buffer.array()).trim().equals(POISON_PILL)) {
            client.close();
            System.out.println("Not accepting client messages anymore");
        }
        else {
            buffer.flip();
            client.write(buffer);
            buffer.clear();
        }
    }

    private static void register(Selector selector, ServerSocketChannel serverSocket)
      throws IOException {
 
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    public static Process start() throws IOException, InterruptedException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = EchoServer.class.getCanonicalName();

        ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className);

        return builder.start();
    }
}
```

这就是正在发生的事情；我们通过调用静态打开方法创建一个Selector对象。然后我们也通过调用它的静态打开方法来创建一个通道，特别是一个ServerSocketChannel实例。

这是因为ServerSocketChannel是可选择的并且适合面向流的监听套接字。

然后我们将它绑定到我们选择的端口。记得我们之前说过，在将可选通道注册到选择器之前，我们必须先将其设置为非阻塞模式。所以接下来我们这样做，然后将通道注册到选择器。

我们现阶段不需要这个频道的SelectionKey实例，所以我们不会记住它。

Java NIO 使用面向缓冲区的模型而不是面向流的模型。所以套接字通信通常通过写入和读取缓冲区来进行。

因此，我们创建一个新的ByteBuffer，服务器将对其进行写入和读取。我们将其初始化为 256 字节，它只是一个任意值，具体取决于我们计划来回传输的数据量。

最后，我们执行选择过程。我们选择就绪通道，检索它们的选择键，遍历键，并执行每个通道就绪的操作。

我们在无限循环中执行此操作，因为无论是否有活动，服务器通常都需要保持运行。

ServerSocketChannel可以处理的唯一操作是ACCEPT操作。当我们接受来自客户端的连接时，我们会获得一个SocketChannel对象，我们可以在该对象上进行读写。我们将其设置为非阻塞模式并将其注册为对选择器进行读取操作。

在后续选择之一期间，这个新频道将变为可读状态。我们检索它并将其内容读入缓冲区。作为回显服务器，我们必须将此内容写回客户端。

当我们想要写入我们一直在读取的缓冲区时，我们必须调用flip()方法。

我们最终通过调用flip方法将缓冲区设置为写入模式，并简单地写入它。

定义了start ()方法，以便在单元测试期间可以将 echo 服务器作为单独的进程启动。

### 8.2. 客户端

这是我们的EchoClient.java代码：

```java
public class EchoClient {
    private static SocketChannel client;
    private static ByteBuffer buffer;
    private static EchoClient instance;

    public static EchoClient start() {
        if (instance == null)
            instance = new EchoClient();

        return instance;
    }

    public static void stop() throws IOException {
        client.close();
        buffer = null;
    }

    private EchoClient() {
        try {
            client = SocketChannel.open(new InetSocketAddress("localhost", 5454));
            buffer = ByteBuffer.allocate(256);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendMessage(String msg) {
        buffer = ByteBuffer.wrap(msg.getBytes());
        String response = null;
        try {
            client.write(buffer);
            buffer.clear();
            client.read(buffer);
            response = new String(buffer.array()).trim();
            System.out.println("response=" + response);
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;

    }
}
```

客户端比服务器简单。

我们使用单例模式在start静态方法中实例化它。我们从这个方法调用私有构造函数。

在私有构造函数中，我们在绑定服务器通道的同一端口上打开一个连接，并且仍在同一主机上。

然后我们创建一个可以写入和读取的缓冲区。

最后，我们有一个sendMessage方法，它读取将我们传递给它的任何字符串包装到一个字节缓冲区中，该字节缓冲区通过通道传输到服务器。

然后我们从客户端通道读取以获取服务器发送的消息。我们将其作为消息的回声返回。

### 8.3. 测试

在名为EchoTest.java的类中，我们将创建一个测试用例，用于启动服务器、向服务器发送消息，并且仅在从服务器接收回相同消息时通过。作为最后一步，测试用例在完成之前停止服务器。

我们现在可以运行测试：

```java
public class EchoTest {

    Process server;
    EchoClient client;

    @Before
    public void setup() throws IOException, InterruptedException {
        server = EchoServer.start();
        client = EchoClient.start();
    }

    @Test
    public void givenServerClient_whenServerEchosMessage_thenCorrect() {
        String resp1 = client.sendMessage("hello");
        String resp2 = client.sendMessage("world");
        assertEquals("hello", resp1);
        assertEquals("world", resp2);
    }

    @After
    public void teardown() throws IOException {
        server.destroy();
        EchoClient.stop();
    }
}
```

## 9.选择器唤醒()

正如我们之前看到的，调用selector.select()会阻塞当前线程，直到其中一个被监视的通道准备好运行。我们可以通过从另一个线程调用selector.wakeup()来覆盖它。

结果是阻塞线程立即返回，而不是继续等待，无论通道是否准备就绪。

我们可以使用[CountDownLatch](https://www.baeldung.com/java-countdown-latch)并跟踪代码执行步骤来演示这一点：

```java
@Test
public void whenWakeUpCalledOnSelector_thenBlockedThreadReturns() {
    Pipe pipe = Pipe.open();
    Selector selector = Selector.open();
    SelectableChannel channel = pipe.source();
    channel.configureBlocking(false);
    channel.register(selector, OP_READ);

    List<String> invocationStepsTracker = Collections.synchronizedList(new ArrayList<>());

    CountDownLatch latch = new CountDownLatch(1);

    new Thread(() -> {
        invocationStepsTracker.add(">> Count down");
        latch.countDown();
        try {
            invocationStepsTracker.add(">> Start select");
            selector.select();
            invocationStepsTracker.add(">> End select");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }).start();

    invocationStepsTracker.add(">> Start await");
    latch.await();
    invocationStepsTracker.add(">> End await");

    invocationStepsTracker.add(">> Wakeup thread");
    selector.wakeup();
    //clean up
    channel.close();

    assertThat(invocationStepsTracker)
      .containsExactly(
        ">> Start await",
        ">> Count down",
        ">> Start select",
        ">> End await",
        ">> Wakeup thread",
        ">> End select"
    );
}
```

在这个例子中，我们使用JavaNIO 的Pipe类打开一个通道用于测试目的。我们在线程安全列表中跟踪代码执行步骤。通过分析这些步骤，我们可以看到selector.wakeup()是如何释放被selector.select()阻塞的线程的。

## 10.总结

在本文中，我们介绍了JavaNIO Selector 组件的基本用法。