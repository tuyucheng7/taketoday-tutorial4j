## 1. 概述

Chronicle Queue使用内存映射文件保存每条消息，这允许我们在进程之间共享消息。

因此，它将数据直接存储到堆外内存中，从而避免GC开销，旨在为高性能应用程序提供低延迟消息框架。

在这篇快速文章中，我们介绍基本的操作集。

## 2. Maven依赖

我们需要添加以下依赖项：

```xml
<dependency>
    <groupId>net.openhft</groupId>
    <artifactId>chronicle</artifactId>
    <version>3.6.4</version>
</dependency>
```

## 3. 构建块

Chronicle Queue具有三个概念特征：

-   Excerpt：是一个数据容器
-   Appender：用于写入数据
-   Trailer：用于顺序读取数据

我们将使用Chronicle接口为读写操作保留部分内存。下面是创建实例的示例代码：

```java
File queueDir = Files.createTempDirectory("chronicle-queue").toFile();
Chronicle chronicle = ChronicleQueueBuilder.indexed(queueDir).build();
```

我们需要一个基本目录，队列将在其中保存内存映射文件中的记录。ChronicleQueueBuilder类提供了不同类型的队列，在这种情况下，我们使用了IndexedChronicleQueue，它使用顺序索引来维护队列中记录的内存偏移量。

## 4. 写入队列

要将元素写入队列，我们需要使用Chronicle实例创建ExcerptAppender类的对象。下面是将消息写入队列的示例代码：

```java
ExcerptAppender appender = chronicle.createAppender();
appender.startExcerpt();

String stringVal = "Hello World";
int intVal = 101;
long longVal = System.currentTimeMillis();
double doubleVal = 90.00192091d;

appender.writeUTF(stringValue);
appender.writeInt(intValue);
appender.writeLong(longValue);
appender.writeDouble(doubleValue);
appender.finish();
```

创建appender之后，我们使用startExcerpt方法启动appender。它以128K的默认消息容量启动一个excerpt。我们可以使用startExcerpt的重载版本来提供自定义容量。

一旦开始，我们可以使用库提供的各种写入方法将任何文本或对象值写入队列。最后，当我们完成写入时，我们将完成excerpt，将数据保存到队列中，然后再保存到磁盘中。

## 5. 从队列中读取

使用ExcerptTrailer实例可以轻松地从队列中读取值，它就像我们在Java中用来遍历集合的迭代器。

让我们从队列中读取值：

```java
ExcerptTailer tailer = chronicle.createTailer();
while (tailer.nextIndex()) {
    tailer.readUTF();
    tailer.readInt();
    tailer.readLong();
    tailer.readDouble();
}
tailer.finish();
```

创建tailer后，我们使用nextIndex方法检查是否有新的excerpt要读取。一旦ExcerptTailer有一个新的Excerpt要读取，我们就可以使用一系列读取文本和对象类型值的方法从中读取消息。

最后，我们使用finish API完成读取。

## 6. 总结

在本教程中，我们简要介绍了ChronicleQueue及其构建块，我们演示了如何创建队列、写入和读取数据。使用它提供了许多好处，包括低延迟、持久的进程间通信(IPC)以及没有垃圾收集开销。

该解决方案通过内存映射文件提供数据持久性(不会丢失数据)，它还允许来自多个进程的并发读写；但是，写入是同步处理的。