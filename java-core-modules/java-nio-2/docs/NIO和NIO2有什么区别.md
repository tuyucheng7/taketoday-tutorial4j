## 1. 概述

在本教程中，我们将介绍JavaIO 功能以及它们在不同Java版本中的变化。首先，我们将介绍初始Java版本中的java.io包。接下来，我们将回顾Java 1.4 中引入的java.nio包。最后，我们将介绍java.nio.file包，通常称为 NIO.2 包。

## 2.Java NIO包

[第一个Java版本与java.io包](https://www.baeldung.com/java-io-file)一起发布，引入了一个 File类来访问文件系统。File类表示文件和目录，并提供对文件系统的有限操作。可以创建和删除文件，检查它们是否存在，检查读/写访问等。

它也有一些缺点：

-   缺少方法——要一个文件，我们需要创建两个File实例并使用一个缓冲区来读取一个文件实例并写入另一个File实例。
-   错误处理错误 ——一些方法返回布尔值作为操作成功与否的指示符。
-   一组有限的文件属性——名称、路径、读/写权限、可用内存大小等等。
-   阻塞 API——我们的线程被阻塞，直到 IO 操作完成。

要读取文件，我们需要一个FileInputStream实例来从文件中读取字节：

```java
@Test
public void readFromFileUsingFileIO() throws Exception {
    File file = new File("src/test/resources/nio-vs-nio2.txt");
    FileInputStream in = new FileInputStream(file);
    StringBuilder content = new StringBuilder();
    int data = in.read();
    while (data != -1) {
        content.append((char) data);
        data = in.read();
    }
    in.close();
    assertThat(content.toString()).isEqualTo("Hello from file!");
}
```

接下来，Java 1.4 引入了捆绑在java.nio包中的非阻塞 IO API (nio 代表新 IO)。NIO 的引入是为了克服java.io包的局限性。这个包引入了三个核心类：Channel、Buffer和Selector。

### 2.1. 渠道

[Java NIO Channel](https://www.baeldung.com/java-filechannel)是一个允许我们读写缓冲区的类。Channel类类似于Streams(这里我们说的是 IO Streams，而不是Java1.8 Streams)，但有一些不同之处。Channel是双向的，而Streams通常是单向的，可以异步读写。

Channel类有几个实现，包括用于文件系统读/写的FileChannel ，使用 UDP 通过网络读/写的[DatagramChannel ](https://www.baeldung.com/java-nio-datagramchannel)，以及使用 TCP 通过网络读/写的SocketChannel 。

### 2.2. 缓冲

缓冲区是一块内存，我们可以从中读取或写入数据。NIO Buffer对象包装了一个内存块。Buffer类提供了一组功能来处理内存块。要使用Buffer对象，我们需要了解Buffer类的三个主要属性：容量、位置和限制。

-   容量定义了内存块的大小。当我们向缓冲区写入数据时，我们只能写入有限的长度。当缓冲区已满时，我们需要读取数据或清除它。
-   该位置是我们写入数据的起点。一个空的缓冲区从 0 开始到capacity – 1。还有，当我们读取数据时，我们是从位置值开始的。
-   限制意味着我们如何写入和读取缓冲区。

Buffer类有多种变体。每个原始Java类型一个，不包括Boolean类型和[MappedByteBuffer](https://www.baeldung.com/java-mapped-byte-buffer)。

要使用缓冲区，我们需要知道几个重要的方法：

-   allocate(int value)——我们使用这个方法来创建一个特定大小的缓冲区。
-   flip() – 此方法用于从写入模式切换到读取模式
-   clear() –清除缓冲区内容的方法
-   compact() –只清除我们已经读过的内容的方法
-   rewind() –将位置重置为 0，以便我们可以重新读取缓冲区中的数据

使用前面描述的概念，让我们使用Channel和Buffer类从文件中读取内容：

```java
@Test
public void readFromFileUsingFileChannel() throws Exception {
    RandomAccessFile file = new RandomAccessFile("src/test/resources/nio-vs-nio2.txt", "r");
    FileChannel channel = file.getChannel();
    StringBuilder content = new StringBuilder();
    ByteBuffer buffer = ByteBuffer.allocate(256);
    int bytesRead = channel.read(buffer);
    while (bytesRead != -1) {
        buffer.flip();
        while (buffer.hasRemaining()) {
            content.append((char) buffer.get());
        }
        buffer.clear();
        bytesRead = channel.read(buffer);
    }
    file.close();
    assertThat(content.toString()).isEqualTo("Hello from file!");
}
```

初始化所有必需的对象后，我们从通道读取到缓冲区。接下来，在 while 循环中，我们使用flip()方法将缓冲区标记为读取，一次读取一个字节，并将其附加到我们的结果中。最后，我们清除数据并读取另一批数据。

### 2.3. 选择器

[Java NIO Selector](https://www.baeldung.com/java-nio-selector)允许我们用一个线程管理多个通道。要使用一个选择器对象来监控多个通道，每个通道实例都必须处于非阻塞模式，我们必须注册它。通道注册后，我们得到一个SelectionKey对象，代表通道和选择器之间的连接。当我们有多个通道连接到一个选择器时，我们可以使用select()方法来检查有多少通道可供使用。调用select()方法后，我们可以使用selectedKeys()方法来获取所有就绪的频道。

### 2.4. NIO封装的缺点

引入的java.nio包的变化更多地与低级数据 IO 相关。虽然他们允许非阻塞 API，但其他方面仍然存在问题：

-   对符号链接的有限支持
-   对文件属性访问的有限支持
-   缺少更好的文件系统管理工具

## 3.JavaNIO.2 包

Java 1.7 引入了新的java.nio.file包，也称为[NIO.2 包](https://www.baeldung.com/java-nio-2-file-api)。此包遵循java.nio包中不支持的非阻塞 IO 异步方法。最重要的变化与高级文件操作有关。它们与Files、Path和Paths类一起添加。最显着的底层变化是添加了[AsynchroniousFileChannel](https://www.baeldung.com/java-nio2-async-file-channel)和AsyncroniousSocketChannel。

Path对象表示由定界符分隔的目录和文件名的层次序列。根组件在最左边，而文件在右边。此类提供实用方法，如getFileName()、 getParent()等。Path类还提供resolve和relativize方法，帮助构建不同文件之间的路径。Paths 类是一组静态实用方法，它们接收String或URI以创建Path实例。

Files类提供实用方法，这些方法使用前面描述的Path类并对文件、目录和符号链接进行操作。它还提供了一种使用readAttributes()方法读取许多文件属性的方法。

最后，让我们看看 NIO.2 在读取文件方面与之前的 IO 版本相比如何：

```java
@Test
public void readFromFileUsingNIO2() throws Exception {
    List<String> strings = Files.readAllLines(Paths.get("src/test/resources/nio-vs-nio2.txt"));
    assertThat(strings.get(0)).isEqualTo("Hello from file!");
}
```

## 4。总结

在本文中，我们介绍了java.nio和java.nio.file包的基础知识。正如我们所见，NIO.2 并不是 NIO 包的新版本。NIO 包为非阻塞 IO 引入了低级 API，而 NIO.2 引入了更好的文件管理。这两个包不是同义词，而是互相恭维。