## 1. 概述

在读取或写入文件时，我们需要确保适当的文件锁定机制到位。这确保了基于并发 I/O 的应用程序中的数据完整性。

在本教程中，我们将了解使用[Java NIO](https://www.baeldung.com/java-nio-2-file-api)库实现此目的的各种方法。

## 二、文件锁简介

一般来说，有两种类型的锁：

-   -   独占锁——也称为写锁
    -   共享锁——也称为读锁

简而言之，独占锁会在写操作完成时阻止所有其他操作(包括读取)。

相反，共享锁允许多个进程同时读取。读锁的作用是防止另一个进程获取写锁。通常，处于一致状态的文件确实应该可以被任何进程读取。

在下一节中，我们将了解Java如何处理这些类型的锁。

## 3.Java中的文件锁

Java NIO 库支持在操作系统级别锁定文件。[FileChannel](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/channels/FileChannel.html)的lock [()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/channels/FileChannel.html#lock())和[tryLock()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/channels/FileChannel.html#tryLock())方法 就是为了这个目的。

我们可以通过 FileInputStream [、](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/FileInputStream.html#getChannel()) FileOutputStream[或](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/FileOutputStream.html#getChannel())RandomAccessFile[创建](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/RandomAccessFile.html#getChannel())一个FileChannel。这三个都有一个返回FileChannel 的getChannel()方法。

或者，我们可以直接通过静态打开方法创建一个FileChannel：

```java
try (FileChannel channel = FileChannel.open(path, openOptions)) {
  // write to the channel
}
```

接下来，我们将回顾在Java中获取独占锁和共享锁的不同选项。要了解有关文件通道的更多信息，请查看我们的[Java FileChannel 指南](https://www.baeldung.com/java-filechannel)教程。

## 4.独占锁

正如我们已经了解到的，在写入文件时，我们可以通过使用独占锁来防止其他进程对其进行读取或写入。

我们通过在FileChannel类上调用lock()或tryLock()来获得独占锁。我们也可以使用他们的重载方法：

-   锁定(多头头寸，多头大小，布尔共享)
-   tryLock(长仓，长尺寸，布尔共享)

在这些情况下，共享参数必须设置为false。

要获得独占锁，我们必须使用可写的FileChannel。我们可以通过FileOutputStream或RandomAccessFile的getChannel()方法创建它。或者，如前所述，我们可以使用FileChannel类的静态打开方法。我们只需要将第二个参数设置为StandardOpenOption.APPEND：

```java
try (FileChannel channel = FileChannel.open(path, StandardOpenOption.APPEND)) { 
    // write to channel
}
```

### 4.1. 使用FileOutputStream的独占锁

从 FileOutputStream 创建的FileChannel是 可写的。因此，我们可以获取排他锁：

```java
try (FileOutputStream fileOutputStream = new FileOutputStream("/tmp/testfile.txt");
     FileChannel channel = fileOutputStream.getChannel();
     FileLock lock = channel.lock()) { 
    // write to the channel
}
```

在这里，channel.lock()要么阻塞直到获得锁，要么抛出异常。例如，如果指定的区域已被锁定，则会抛出OverlappingFileLockException 。有关可能的异常的完整列表，请参阅[Javadoc 。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/channels/FileChannel.html#lock())

我们还可以使用channel.tryLock()执行非阻塞锁定。如果由于另一个程序持有重叠的锁而未能获得锁，则它返回null。如果由于任何其他原因未能这样做，则会抛出适当的异常。

### 4.2. 使用RandomAccessFile的独占锁

对于 RandomAccessFile，我们需要在[构造函数](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/RandomAccessFile.html#(java.io.File,java.lang.String))的第二个参数上设置标志。

在这里，我们将以读写权限打开文件：

```java
try (RandomAccessFile file = new RandomAccessFile("/tmp/testfile.txt", "rw");
      FileChannel channel = file.getChannel();
      FileLock lock = channel.lock()) {
    // write to the channel
}

```

如果我们以只读模式打开文件并尝试写入其通道，它将抛出NonWritableChannelException。

### 4.3. 独占锁需要一个可写的FileChannel

如前所述，独占锁需要一个可写通道。因此，我们无法通过从FileInputStream创建的FileChannel获得独占锁：

```java
Path path = Files.createTempFile("foo","txt");
Logger log = LoggerFactory.getLogger(this.getClass());
try (FileInputStream fis = new FileInputStream(path.toFile()); 
    FileLock lock = fis.getChannel().lock()) {
    // unreachable code
} catch (NonWritableChannelException e) {
    // handle exception
}
```

在上面的示例中，lock() 方法将抛出 NonWritableChannelException。实际上，这是因为我们在 FileInputStream上调用getChannel，它创建了一个只读通道。

这个例子只是为了演示我们不能写入一个不可写的通道。在真实场景中，我们不会捕获并重新抛出异常。

## 5.共享锁

请记住，共享锁也称为读锁。因此，要获得读锁，我们必须使用可读的FileChannel。

这样的FileChannel可以通过调用FileInputStream或RandomAccessFile上的getChannel()方法来获得。同样，另一种选择是使用FileChannel类的静态打开方法。在这种情况下，我们将第二个参数设置为StandardOpenOption.READ：

```java
try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ);
    FileLock lock = channel.lock(0, Long.MAX_VALUE, true)) {
    // read from the channel
}
```

这里要注意的一件事是我们选择通过调用lock(0, Long.MAX_VALUE, true)来锁定整个文件。我们也可以通过将前两个参数更改为不同的值来仅锁定文件的特定区域。在共享锁的情况下，第三个参数必须设置为true 。

为了简单起见，我们将在下面的所有示例中锁定整个文件，但请记住，我们始终可以锁定文件的特定区域。

### 5.1. 使用FileInputStream的共享锁

从 FileInputStream获得的 FileChannel是 可读的。因此，我们可以获得一个共享锁：

```java
try (FileInputStream fileInputStream = new FileInputStream("/tmp/testfile.txt");
    FileChannel channel = fileInputStream.getChannel();
    FileLock lock = channel.lock(0, Long.MAX_VALUE, true)) {
    // read from the channel
}
```

在上面的代码片段中，对通道上的lock()的调用将会成功。那是因为共享锁只要求通道是可读的。这是因为我们是从 FileInputStream 创建的。

### 5.2. 使用RandomAccessFile的共享锁

这一次，我们可以打开具有读取权限的文件：

```java
try (RandomAccessFile file = new RandomAccessFile("/tmp/testfile.txt", "r"); 
     FileChannel channel = file.getChannel();
     FileLock lock = channel.lock(0, Long.MAX_VALUE, true)) {
     // read from the channel
}
```

在此示例中，我们创建了一个具有读取权限的RandomAccessFile。我们可以从中创建一个可读通道，从而创建一个共享锁。

### 5.3. 共享锁需要一个可读的FileChannel

因此，我们无法通过从FileOutputStream创建的FileChannel获取共享锁：

```java
Path path = Files.createTempFile("foo","txt");
try (FileOutputStream fis = new FileOutputStream(path.toFile()); 
    FileLock lock = fis.getChannel().lock(0, Long.MAX_VALUE, true)) {
    // unreachable code
} catch (NonWritableChannelException e) { 
    // handle exception
}

```

在此示例中，对lock() 的调用 尝试在从FileOutputStream创建的通道上获取共享锁 。这样的通道是只写的。它不能满足通道必须可读的需求。这将触发NonWritableChannelException。

同样，这个片段只是为了证明我们不能从不可读的通道中读取。

## 6. 需要考虑的事情

实际上，使用文件锁很困难；锁定机制不可移植。我们需要考虑到这一点来设计我们的锁定逻辑。

在 POSIX 系统中，锁是建议性的。读取或写入给定文件的不同进程必须就锁定协议达成一致。这将确保文件的完整性。操作系统本身不会强制执行任何锁定。

在 Windows 上，除非允许共享，否则锁将是独占的。讨论操作系统特定机制的优缺点不在本文的讨论范围之内。然而，在实施锁定机制时了解这些细微差别很重要。

## 七、总结

在本教程中，我们回顾了在Java中获取文件锁的几个不同选项。

首先，我们首先了解两种主要的锁定机制以及JavaNIO 库如何促进锁定文件。然后我们通过一系列简单的例子展示了我们可以在我们的应用程序中获得独占锁和共享锁。我们还了解了在使用文件锁时可能遇到的典型异常类型。