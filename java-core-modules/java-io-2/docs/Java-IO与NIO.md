## 1. 概述

处理输入和输出是Java程序员的常见任务。在本教程中，我们将了解原始的[java.io](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/package-summary.html) ( [IO](https://www.baeldung.com/java-io) ) 库和更新的[java.nio](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/package-summary.html) ( [NIO](https://www.baeldung.com/tag/java-nio/) ) 库，以及它们在通过网络通信时的不同之处。

## 2. 主要特点

让我们先看看这两个包的主要特性。

### 2.1. IO——java.io _

Java 1.0 中引入了 java.io 包，Java 1.1 中引入了Reader 。它提供：

-   InputStream 和[OutputStream](https://www.baeldung.com/java-outputstream) – 一次提供一个字节的数据
-   Reader 和Writer – 流的便利包装器
-   阻塞模式——等待完整的消息

### 2.2. NIO—— java.nio

java.nio包在Java1.4 中引入并在Java1.7 (NIO.2) 中更新，具有[增强的文件操作](https://www.baeldung.com/java-nio-2-file-api)和[ASynchronousSocketChannel](https://www.baeldung.com/java-nio2-async-socket-channel)。它提供：

-   缓冲区 ——一次读取数据块
-   CharsetDecoder – 用于将原始字节映射到/从可读字符
-   Channel—— 与外界沟通
-   [Selector——](https://www.baeldung.com/java-nio-selector)在SelectableChannel上启用多路复用，并提供对任何准备好 I/O 的Channel的访问
-   非阻塞模式——读取任何准备好的东西

现在让我们看看在向服务器发送数据或读取其响应时如何使用这些包中的每一个。

## 3. 配置我们的测试服务器

在这里，我们将使用[WireMock](https://www.baeldung.com/introduction-to-wiremock)模拟另一台服务器，以便我们可以独立运行测试。

我们将配置它来侦听我们的请求并向我们发送响应，就像真正的 Web 服务器一样。我们还将使用动态端口，这样我们就不会与本地机器上的任何服务发生冲突。

让我们为具有测试范围的 WireMock 添加 Maven 依赖项：

```xml
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock-jre8</artifactId>
    <version>2.26.3</version>
    <scope>test</scope>
</dependency>
```

在测试类中，让我们定义一个 JUnit @Rule以在空闲端口上启动 WireMock。然后，我们将其配置为在请求预定义资源时返回 HTTP 200 响应，消息正文为 JSON 格式的一些文本：

```java
@Rule public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

private String REQUESTED_RESOURCE = "/test.json";

@Before
public void setup() {
    stubFor(get(urlEqualTo(REQUESTED_RESOURCE))
      .willReturn(aResponse()
      .withStatus(200)
      .withBody("{ "response" : "It worked!" }")));
}
```

现在我们已经设置了我们的模拟服务器，我们准备运行一些测试。

## 4. 阻塞IO——java.io

让我们通过从网站读取一些数据来了解原始的阻塞 IO 模型是如何工作的。我们将使用[java.net.Socket](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/Socket.html)来访问操作系统的端口之一。

### 4.1. 发送请求

在此示例中，我们将创建一个 GET 请求来检索我们的资源。首先，让我们创建一个Socket来访问我们的 WireMock 服务器正在侦听的端口：

```java
Socket socket = new Socket("localhost", wireMockRule.port())
```

对于正常的 HTTP 或 HTTPS 通信，端口将为 80 或 443。但是，在这种情况下，我们使用wireMockRule.port()来访问我们之前设置的动态端口。

现在让我们在 socket 上打开一个OutputStream，包装在一个OutputStreamWriter中并将它传递给一个PrintWriter来编写我们的消息。让我们确保我们刷新缓冲区以便发送我们的请求：

```java
OutputStream clientOutput = socket.getOutputStream();
PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientOutput));
writer.print("GET " + TEST_JSON + " HTTP/1.0rnrn");
writer.flush();
```

### 4.2. 等待回应

让我们在套接字上打开InputStream 以访问响应，使用 BufferedReader 读取流[，](https://www.baeldung.com/java-buffered-reader)并将其存储在StringBuilder中：

```java
InputStream serverInput = socket.getInputStream();
BufferedReader reader = new BufferedReader(new InputStreamReader(serverInput));
StringBuilder ourStore = new StringBuilder();
```

让我们使用reader.readLine()来阻塞，等待一个完整的行，然后将该行追加到我们的商店。我们将继续阅读，直到我们得到一个null，这表明流的结尾：

```java
for (String line; (line = reader.readLine()) != null;) {
   ourStore.append(line);
   ourStore.append(System.lineSeparator());
}
```

## 5. 非阻塞IO—— java.nio

现在，让我们看看nio包的非阻塞 IO 模型如何使用相同的示例。

这一次，我们将创建一个[java.nio.channel。SocketChannel](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/channels/SocketChannel.html)来访问我们服务器上的端口而不是java.net.Socket，并传递给它一个InetSocketAddress。

### 5.1. 发送请求

首先，让我们打开我们的 SocketChannel：

```java
InetSocketAddress address = new InetSocketAddress("localhost", wireMockRule.port());
SocketChannel socketChannel = SocketChannel.open(address);
```

现在，让我们使用标准的 UTF-8字符集来编码和编写我们的消息：

```java
Charset charset = StandardCharsets.UTF_8;
socket.write(charset.encode(CharBuffer.wrap("GET " + REQUESTED_RESOURCE + " HTTP/1.0rnrn")));
```

### 5.2. 阅读回复

发送请求后，我们可以使用原始缓冲区以非阻塞模式读取响应。

由于我们将处理文本，因此我们需要一个用于 原始字节的[ByteBuffer和一个用于转换字符的](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/ByteBuffer.html)[CharBuffer](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/CharBuffer.html)(由[CharsetDecoder](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/charset/CharsetDecoder.html)辅助)：

```java
ByteBuffer byteBuffer = ByteBuffer.allocate(8192);
CharsetDecoder charsetDecoder = charset.newDecoder();
CharBuffer charBuffer = CharBuffer.allocate(8192);
```

如果数据以多字节字符集发送，我们的CharBuffer将有剩余空间。

请注意，如果我们需要特别快的性能，我们可以使用ByteBuffer.allocateDirect()在本机内存中创建一个[MappedByteBuffer](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/MappedByteBuffer.html)。然而，在我们的例子中，使用标准堆中的allocate()已经足够快了。

在处理缓冲区时，我们需要知道缓冲区有多大(容量)，我们在缓冲区的什么位置(当前位置)，以及我们能走多远(极限)。

因此，让我们从SocketChannel中读取数据，将其传递给我们的ByteBuffer以存储我们的数据。我们从SocketChannel的读取将完成，我们的ByteBuffer的当前位置设置为要写入的下一个字节(就在最后一个写入的字节之后)，但其限制不变：

```java
socketChannel.read(byteBuffer)
```

我们的SocketChannel.read()返回可以写入缓冲区的读取字节数。如果套接字断开连接，这将为 -1。

当我们的缓冲区因为我们还没有处理它的所有数据而没有剩余空间时，那么SocketChannel.read()将返回零字节读取但我们的buffer.position()仍然大于零。

为确保我们从缓冲区中的正确位置开始读取，我们将使用Buffer.flip () 将ByteBuffer的当前位置设置为零，并将其限制为SocketChannel写入的最后一个字节。然后我们将使用我们的storeBufferContents方法保存缓冲区内容，稍后我们将查看。最后，我们将使用buffer.compact()来压缩缓冲区并设置当前位置以备下次从SocketChannel 读取。

由于我们的数据可能会分段到达，让我们将读取缓冲区的代码包装在一个带有终止条件的循环中，以检查我们的套接字是否仍处于连接状态，或者我们是否已断开连接但缓冲区中仍有数据：

```java
while (socketChannel.read(byteBuffer) != -1 || byteBuffer.position() > 0) {
    byteBuffer.flip();
    storeBufferContents(byteBuffer, charBuffer, charsetDecoder, ourStore);
    byteBuffer.compact();
}
```

我们不要忘记close()我们的套接字(除非我们在 try-with-resources 块中打开它)：

```java
socketChannel.close();
```

### 5.3. 从我们的缓冲区存储数据

来自服务器的响应将包含标头，这可能会使数据量超过我们缓冲区的大小。因此，我们将使用StringBuilder在消息到达时构建完整的消息。

为了存储我们的消息，我们首先将原始字节解码为CharBuffer中的字符。然后我们将翻转指针，以便我们可以读取我们的字符数据，并将其附加到我们的可扩展StringBuilder。最后，我们将清除CharBuffer，为下一个写/读周期做好准备。

所以现在，让我们实现完整的storeBufferContents()方法，传入我们的缓冲区、CharsetDecoder和StringBuilder：

```java
void storeBufferContents(ByteBuffer byteBuffer, CharBuffer charBuffer, 
  CharsetDecoder charsetDecoder, StringBuilder ourStore) {
    charsetDecoder.decode(byteBuffer, charBuffer, true);
    charBuffer.flip();
    ourStore.append(charBuffer);
    charBuffer.clear();
}
```

## 六，总结

在本文中，我们了解了原始java.io模型如何阻塞、等待请求并使用Stream来操作它接收到的数据。

相比之下，java.nio库允许使用Buffer和Channel进行非阻塞通信，并且可以提供直接内存访问以获得更快的性能。然而，这种速度带来了处理缓冲区的额外复杂性。