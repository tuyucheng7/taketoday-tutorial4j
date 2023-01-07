## 1. 概述

在本快速教程中，我们将了解[Java NIO](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/package-summary.html)库中提供的[FileChannel](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/channels/FileChannel.html)类。我们将讨论如何使用FileChannel和ByteBuffer读写数据。

我们还将探讨使用FileChannel及其其他一些文件操作功能的优势。

## 2、 FileChannel的优势

FileChannel的优点包括：

-   在文件中的特定位置读写
-   将文件的一部分直接加载到内存中，效率更高
-   我们可以更快地将文件数据从一个通道传输到另一个通道
-   我们可以锁定文件的一部分以限制其他线程的访问
-   为避免数据丢失，我们可以强制立即将对文件的更新写入存储

## 3. 使用FileChannel读取

当我们读取大文件时，FileChannel 的执行速度比标准 I/O 快。

我们应该注意到，虽然Java NIO的一部分，但 FileChannel操作是阻塞的并且没有非阻塞模式。

### 3.1. 使用FileChannel读取文件

让我们了解如何在包含以下内容的文件上使用FileChannel读取文件：

```java
Hello world
```

此测试读取文件并检查它是否已读取：

```java
@Test
public void givenFile_whenReadWithFileChannelUsingRandomAccessFile_thenCorrect() 
  throws IOException {
    try (RandomAccessFile reader = new RandomAccessFile("src/test/resources/test_read.in", "r");
        FileChannel channel = reader.getChannel();
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {

        int bufferSize = 1024;
        if (bufferSize > channel.size()) {
           bufferSize = (int) channel.size();
        }
        ByteBuffer buff = ByteBuffer.allocate(bufferSize);

        while (channel.read(buff) > 0) {
            out.write(buff.array(), 0, buff.position());
            buff.clear();
        }
        
     String fileContent = new String(out.toByteArray(), StandardCharsets.UTF_8);
 
     assertEquals("Hello world", fileContent);
    }
}
```

在这里，我们使用FileChannel、RandomAccessFile和ByteBuffer从文件中读取字节。

我们还应该注意，多个并发线程可以安全地使用FileChannels。 但是，一次只允许一个线程执行涉及更新频道位置或更改其文件大小的操作。这会阻止其他线程尝试类似的操作，直到前一个操作完成。

但是，提供显式通道位置的操作可以并发运行而不会被阻塞。

### 3.2. 打开文件通道

为了使用FileChannel读取文件，我们必须打开它。

让我们看看如何使用RandomAccessFile打开FileChannel ：

```java
RandomAccessFile reader = new RandomAccessFile(file, "r");
FileChannel channel = reader.getChannel();
```

模式“r”表示该频道仅“开放阅读”。我们应该注意，关闭RandomAccessFile 也会关闭关联的通道。

接下来，我们将看到打开一个FileChannel以使用FileInputStream读取文件：

```java
FileInputStream fin= new FileInputStream(file);
FileChannel channel = fin.getChannel();
```

同样，关闭FileInputStream 也会关闭与其关联的通道。

### 3.3. 从FileChannel读取数据

要读取数据，我们可以使用其中一种读取方法。

让我们看看如何读取字节序列。我们将使用ByteBuffer来保存数据：

```java
ByteBuffer buff = ByteBuffer.allocate(1024);
int noOfBytesRead = channel.read(buff);
String fileContent = new String(buff.array(), StandardCharsets.UTF_8);

assertEquals("Hello world", fileContent);
```

接下来，我们将看到如何从文件位置开始读取字节序列：

```java
ByteBuffer buff = ByteBuffer.allocate(1024);
int noOfBytesRead = channel.read(buff, 5);
String fileContent = new String(buff.array(), StandardCharsets.UTF_8);
assertEquals("world", fileContent);
```

我们应该注意到需要Charset将字节数组解码为String。

我们指定字节最初编码的字符集。没有它，我们可能会得到乱码。特别是，像UTF-8和UTF-16这样的多字节编码可能无法解码文件的任意部分，因为一些多字节字符可能不完整。

## 4. 用FileChannel写

### 4.1. 使用FileChannel写入文件

让我们探讨如何使用FileChannel编写：

```java
@Test
public void whenWriteWithFileChannelUsingRandomAccessFile_thenCorrect()   
  throws IOException {
    String file = "src/test/resources/test_write_using_filechannel.txt";
    try (RandomAccessFile writer = new RandomAccessFile(file, "rw");
        FileChannel channel = writer.getChannel()){
        ByteBuffer buff = ByteBuffer.wrap("Hello world".getBytes(StandardCharsets.UTF_8));
 
        channel.write(buff);
 
     // verify
     RandomAccessFile reader = new RandomAccessFile(file, "r");
     assertEquals("Hello world", reader.readLine());
     reader.close();
    }
}
```

### 4.2. 打开文件通道

为了使用FileChannel写入文件，我们必须打开它。

让我们看看如何使用RandomAccessFile打开FileChannel ：

```java
RandomAccessFile writer = new RandomAccessFile(file, "rw");
FileChannel channel = writer.getChannel();
```

模式“rw”表示该通道“打开以进行读写”。

我们还看看如何使用FileOutputStream打开FileChannel：

```java
FileOutputStream fout = new FileOutputStream(file);
FileChannel channel = fout.getChannel();

```

### 4.3. 使用FileChannel写入数据

要使用FileChannel写入数据，我们可以使用其中一种写入方法。

让我们看看如何写入字节序列，使用ByteBuffer来存储数据：

```java
ByteBuffer buff = ByteBuffer.wrap("Hello world".getBytes(StandardCharsets.UTF_8));
channel.write(buff);

```

接下来，我们将看到如何从文件位置开始写入字节序列：

```java
ByteBuffer buff = ByteBuffer.wrap("Hello world".getBytes(StandardCharsets.UTF_8));
channel.write(buff, 5);

```

## 5. 目前职位

FileChannel允许我们获取和更改我们正在读取或写入的位置。

让我们看看如何获得当前位置：

```java
long originalPosition = channel.position();
```

接下来，让我们看看如何设置位置：

```java
channel.position(5);
assertEquals(originalPosition + 5, channel.position());
```

## 6. 获取文件的大小

让我们看看如何使用[FileChannel.size](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/channels/FileChannel.html#size())方法来获取文件的大小(以字节为单位)：

```java
@Test
public void whenGetFileSize_thenCorrect() 
  throws IOException {
    RandomAccessFile reader = new RandomAccessFile("src/test/resources/test_read.in", "r");
    FileChannel channel = reader.getChannel();

    // the original file size is 11 bytes.
    assertEquals(11, channel.size());

    channel.close();
    reader.close();
}
```

## 7.截断文件

让我们了解如何使用[FileChannel.truncate](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/channels/FileChannel.html#truncate(long))方法将文件截断为给定大小(以字节为单位)：

```java
@Test
public void whenTruncateFile_thenCorrect() 
  throws IOException {
    String input = "this is a test input";

    FileOutputStream fout = new FileOutputStream("src/test/resources/test_truncate.txt");
    FileChannel channel = fout.getChannel();

    ByteBuffer buff = ByteBuffer.wrap(input.getBytes());
    channel.write(buff);
    buff.flip();

    channel = channel.truncate(5);
    assertEquals(5, channel.size());

    fout.close();
    channel.close();
}


```

## 8.强制文件更新到存储

出于性能原因，操作系统可能会缓存文件更改，如果系统崩溃，数据可能会丢失。要强制文件内容和元数据连续写入磁盘，我们可以使用force方法：

```java
channel.force(true);
```

仅当文件驻留在本地设备上时才能保证使用此方法。

## 9. 将文件的一部分加载到内存中

[让我们看看如何使用FileChannel.map](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/channels/FileChannel.html#map(java.nio.channels.FileChannel.MapMode,long,long))在内存中加载文件的一部分。我们使用[FileChannel.MapMode.READ_ONLY](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/channels/FileChannel.MapMode.html)以只读模式打开文件：

```java
@Test
public void givenFile_whenReadAFileSectionIntoMemoryWithFileChannel_thenCorrect() 
  throws IOException { 
    try (RandomAccessFile reader = new RandomAccessFile("src/test/resources/test_read.in", "r");
        FileChannel channel = reader.getChannel();
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {

        MappedByteBuffer buff = channel.map(FileChannel.MapMode.READ_ONLY, 6, 5);

        if(buff.hasRemaining()) {
          byte[] data = new byte[buff.remaining()];
          buff.get(data);
          assertEquals("world", new String(data, StandardCharsets.UTF_8));	
        }
    }
}
```

同样，我们可以使用FileChannel.MapMode.READ_WRITE以读写模式打开文件。

我们还可以使用FileChannel.MapMode.PRIVATE模式，其中更改不适用于原始文件。 

## 10.锁定文件的一部分

[让我们了解如何使用FileChannel.tryLock](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/channels/FileChannel.html#tryLock(long,long,boolean))方法锁定文件的一部分以防止并发访问该部分 ：

```java
@Test
public void givenFile_whenWriteAFileUsingLockAFileSectionWithFileChannel_thenCorrect() 
  throws IOException { 
    try (RandomAccessFile reader = new RandomAccessFile("src/test/resources/test_read.in", "rw");
        FileChannel channel = reader.getChannel();
        FileLock fileLock = channel.tryLock(6, 5, Boolean.FALSE )){
 
        //do other operations...
 
        assertNotNull(fileLock);
    }
}
```

tryLock方法尝试获取文件部分的锁。如果请求的文件部分已经被另一个线程阻塞，它会抛出[OverlappingFileLockException](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/channels/OverlappingFileLockException.html)异常。此方法还采用布尔参数来请求共享锁或独占锁。

我们应该注意，某些操作系统可能不允许共享锁，而是默认为独占锁。

## 11. 关闭FileChannel

最后，当我们使用完FileChannel时，我们必须关闭它。在我们的示例中，我们使用了 [try-with-resources](https://www.baeldung.com/java-try-with-resources)。

如果需要，我们可以直接使用close方法关闭FileChannel  ：

```java
channel.close();
```

## 12.总结

在本教程中，我们了解了如何使用FileChannel来读写文件。此外，我们探索了如何读取和更改文件大小及其当前读/写位置，并研究了如何在并发或数据关键应用程序中使用FileChannels。