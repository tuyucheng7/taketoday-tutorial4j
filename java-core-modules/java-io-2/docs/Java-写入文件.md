## 1. 概述

在本教程中，我们将探索使用Java写入文件的不同方法。我们将使用BufferedWriter、PrintWriter、FileOutputStream、DataOutputStream、RandomAccessFile、FileChannel和Java7 Files实用程序类。

我们还将研究在写入时锁定文件，并讨论写入文件的一些最终要点。

本教程是Baeldung 上[Java“回归基础”系列的一部分。](https://www.baeldung.com/java-tutorial)

## 延伸阅读：

## [Java——将数据附加到文件](https://www.baeldung.com/java-append-to-file)

将数据附加到文件的快速实用指南。

[阅读更多](https://www.baeldung.com/java-append-to-file)→

## [Java 中的 FileNotFoundException](https://www.baeldung.com/java-filenotfound-exception)

Java 中 FileNotFoundException 的快速实用指南。

[阅读更多](https://www.baeldung.com/java-filenotfound-exception)→

## [如何使用Java文件](https://www.baeldung.com/java-copy-file)

看一下在Java中文件的一些常用方法。

[阅读更多](https://www.baeldung.com/java-copy-file)→

## 2.用BufferedWriter写

让我们从简单开始，使用BufferedWriter将String写入新文件：

```java
public void whenWriteStringUsingBufferedWritter_thenCorrect() 
  throws IOException {
    String str = "Hello";
    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
    writer.write(str);
    
    writer.close();
}
```

文件中的输出将是：

```bash
Hello
```

然后我们可以将一个字符串附加到现有文件：

```java
@Test
public void whenAppendStringUsingBufferedWritter_thenOldContentShouldExistToo() 
  throws IOException {
    String str = "World";
    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
    writer.append(' ');
    writer.append(str);
    
    writer.close();
}
```

该文件将是：

```bash
Hello World
```

## 3. 用PrintWriter写

接下来，让我们看看如何使用PrintWriter将格式化文本写入文件：

```java
@Test
public void givenWritingStringToFile_whenUsingPrintWriter_thenCorrect() 
  throws IOException {
    FileWriter fileWriter = new FileWriter(fileName);
    PrintWriter printWriter = new PrintWriter(fileWriter);
    printWriter.print("Some String");
    printWriter.printf("Product name is %s and its price is %d $", "iPhone", 1000);
    printWriter.close();
}
```

生成的文件将包含：

```bash
Some String
Product name is iPhone and its price is 1000$
```

请注意，我们不仅将原始字符串写入文件，而且还使用printf方法写入了一些格式化文本。

我们可以使用FileWriter、BufferedWriter甚至System.out创建编写器。

## 4.用FileOutputStream写

现在让我们看看如何使用FileOutputStream将二进制数据写入文件。

以下代码将String转换为字节并使用FileOutputStream将字节写入文件：

```java
@Test
public void givenWritingStringToFile_whenUsingFileOutputStream_thenCorrect() 
  throws IOException {
    String str = "Hello";
    FileOutputStream outputStream = new FileOutputStream(fileName);
    byte[] strToBytes = str.getBytes();
    outputStream.write(strToBytes);

    outputStream.close();
}
```

文件中的输出当然是：

```bash
Hello
```

## 5.用DataOutputStream写

接下来，让我们看一下如何使用DataOutputStream将String写入文件：

```java
@Test
public void givenWritingToFile_whenUsingDataOutputStream_thenCorrect() 
  throws IOException {
    String value = "Hello";
    FileOutputStream fos = new FileOutputStream(fileName);
    DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(fos));
    outStream.writeUTF(value);
    outStream.close();

    // verify the results
    String result;
    FileInputStream fis = new FileInputStream(fileName);
    DataInputStream reader = new DataInputStream(fis);
    result = reader.readUTF();
    reader.close();

    assertEquals(value, result);
}
```

## 6.用RandomAccessFile写

现在让我们说明如何在现有文件中写入和编辑，而不是仅仅写入一个全新的文件或附加到现有文件。简单地说：我们需要随机访问。

RandomAccessFile使我们能够在给定偏移量的情况下在文件中的特定位置写入 - 从文件的开头 - 以字节为单位。

此代码写入一个整数值，该值具有从文件开头给定的偏移量：

```java
private void writeToPosition(String filename, int data, long position) 
  throws IOException {
    RandomAccessFile writer = new RandomAccessFile(filename, "rw");
    writer.seek(position);
    writer.writeInt(data);
    writer.close();
}
```

如果我们想读取存储在特定位置的int，我们可以使用这个方法：

```java
private int readFromPosition(String filename, long position) 
  throws IOException {
    int result = 0;
    RandomAccessFile reader = new RandomAccessFile(filename, "r");
    reader.seek(position);
    result = reader.readInt();
    reader.close();
    return result;
}
```

为了测试我们的函数，让我们写一个整数，编辑它，最后读回它：

```java
@Test
public void whenWritingToSpecificPositionInFile_thenCorrect() 
  throws IOException {
    int data1 = 2014;
    int data2 = 1500;
    
    writeToPosition(fileName, data1, 4);
    assertEquals(data1, readFromPosition(fileName, 4));
    
    writeToPosition(fileName2, data2, 4);
    assertEquals(data2, readFromPosition(fileName, 4));
}
```

## 7. 用FileChannel写

如果我们正在处理大文件，FileChannel可以比标准 IO 更快。以下代码使用FileChannel将String写入文件：

```java
@Test
public void givenWritingToFile_whenUsingFileChannel_thenCorrect() 
  throws IOException {
    RandomAccessFile stream = new RandomAccessFile(fileName, "rw");
    FileChannel channel = stream.getChannel();
    String value = "Hello";
    byte[] strBytes = value.getBytes();
    ByteBuffer buffer = ByteBuffer.allocate(strBytes.length);
    buffer.put(strBytes);
    buffer.flip();
    channel.write(buffer);
    stream.close();
    channel.close();

    // verify
    RandomAccessFile reader = new RandomAccessFile(fileName, "r");
    assertEquals(value, reader.readLine());
    reader.close();
}
```

## 8. 用文件类写

Java 7 引入了一种使用文件系统的新方法，以及一个新的实用程序类：Files。

使用Files类，我们可以创建、移动、和删除文件和目录。它还可以用于读取和写入文件：

```java
@Test
public void givenUsingJava7_whenWritingToFile_thenCorrect() 
  throws IOException {
    String str = "Hello";

    Path path = Paths.get(fileName);
    byte[] strToBytes = str.getBytes();

    Files.write(path, strToBytes);

    String read = Files.readAllLines(path).get(0);
    assertEquals(str, read);
}
```

## 9.写入临时文件

现在让我们尝试写入一个临时文件。以下代码创建一个临时文件并向其中写入一个字符串：

```java
@Test
public void whenWriteToTmpFile_thenCorrect() throws IOException {
    String toWrite = "Hello";
    File tmpFile = File.createTempFile("test", ".tmp");
    FileWriter writer = new FileWriter(tmpFile);
    writer.write(toWrite);
    writer.close();

    BufferedReader reader = new BufferedReader(new FileReader(tmpFile));
    assertEquals(toWrite, reader.readLine());
    reader.close();
}
```

如我们所见，有趣且不同的只是临时文件的创建。在那之后，写入文件是一样的。

## 10. 写入前锁定文件

最后，当写入文件时，我们有时需要额外确保没有其他人同时写入该文件。基本上，我们需要能够在写入时锁定该文件。

让我们使用FileChannel在写入文件之前尝试锁定文件：

```java
@Test
public void whenTryToLockFile_thenItShouldBeLocked() 
  throws IOException {
    RandomAccessFile stream = new RandomAccessFile(fileName, "rw");
    FileChannel channel = stream.getChannel();

    FileLock lock = null;
    try {
        lock = channel.tryLock();
    } catch (final OverlappingFileLockException e) {
        stream.close();
        channel.close();
    }
    stream.writeChars("test lock");
    lock.release();

    stream.close();
    channel.close();
}
```

请注意，如果我们尝试获取锁时文件已经被锁定，则会抛出OverlappingFileLockException 。

## 11.注意事项

在探索了这么多写入文件的方法之后，让我们讨论一些重要的注意事项：

-   如果我们尝试从不存在的文件中读取，则会抛出FileNotFoundException 。
-   如果我们尝试写入一个不存在的文件，该文件将首先被创建并且不会抛出异常。
-   使用后关闭流非常重要，因为它不会隐式关闭，以释放与其关联的任何资源。
-   在输出流中，close()方法在释放资源之前调用flush()，这会强制将任何缓冲字节写入流中。

查看常见的使用实践，我们可以看到，例如PrintWriter用于写入格式化文本，FileOutputStream用于写入二进制数据，DataOutputStream用于写入原始数据类型，RandomAccessFile用于写入特定位置，FileChannel用于写入速度更快更大的文件。这些类的一些 API 确实允许更多，但这是一个很好的起点。

## 12.总结

本文介绍了使用Java将数据写入文件的多种选择。