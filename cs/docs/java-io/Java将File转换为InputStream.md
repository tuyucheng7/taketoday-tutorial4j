## 1. 概述

在本快速教程中，我们将展示如何将File转换为InputStream — 首先使用纯 Java，然后使用 Guava 和 Apache Commons IO 库。

本文是Baeldung 上的[Java – 回归基础 系列的一部分。](https://www.baeldung.com/java-tutorial)

## 延伸阅读：

## [Java扫描仪](https://www.baeldung.com/java-scanner)

一组快速实用的示例，用于使用Java中的核心扫描器类 - 处理字符串、文件和用户输入。

[阅读更多](https://www.baeldung.com/java-scanner)→

## [Guava – 写入文件，从文件读取](https://www.baeldung.com/guava-write-to-file-read-from-file)

如何使用 Guava IO 支持和实用程序写入文件和读取文件。

[阅读更多](https://www.baeldung.com/guava-write-to-file-read-from-file)→

## [Java 字节数组到 InputStream](https://www.baeldung.com/convert-byte-array-to-input-stream)

如何使用纯Java或 Guava 将 byte[] 转换为 InputStream。

[阅读更多](https://www.baeldung.com/convert-byte-array-to-input-stream)→

## 2. 使用Java转换

我们可以使用Java 的[IO 包](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/package-summary.html)将一个File转换为不同的InputStream。

### 2.1. 文件输入流

让我们从第一个也是最简单的开始——使用FileInputStream：

```java
@Test
public void givenUsingPlainJava_whenConvertingFileToInputStream_thenCorrect() 
  throws IOException {
    File initialFile = new File("src/main/resources/sample.txt");
    InputStream targetStream = new FileInputStream(initialFile);
}
```

### 2.2. 数据输入流

让我们看看另一种方法，我们可以使用 DataInputStream从文件中读取二进制或原始数据：

```java
@Test
public final void givenUsingPlainJava_whenConvertingFileToDataInputStream_thenCorrect() 
  throws IOException {
      final File initialFile = new File("src/test/resources/sample.txt");
      final InputStream targetStream = 
        new DataInputStream(new FileInputStream(initialFile));
}
```

### 2.3. 序列输入流

最后，让我们看看如何使用 SequenceInputStream将两个文件的输入流连接到一个InputStream：

```java
@Test
public final void givenUsingPlainJava_whenConvertingFileToSequenceInputStream_thenCorrect() 
  throws IOException {
      final File initialFile = new File("src/test/resources/sample.txt");
      final File anotherFile = new File("src/test/resources/anothersample.txt");
      final InputStream targetStream = new FileInputStream(initialFile);
      final InputStream anotherTargetStream = new FileInputStream(anotherFile);
    
      InputStream sequenceTargetStream = 
        new SequenceInputStream(targetStream, anotherTargetStream);
}
```

请注意，为了便于阅读，我们并未关闭这些示例中的结果流。

## 3. 使用 Guava 转换

接下来，让我们看看使用中介ByteSource 的Guava 解决方案：

```java
@Test
public void givenUsingGuava_whenConvertingFileToInputStream_thenCorrect() 
  throws IOException {
    File initialFile = new File("src/main/resources/sample.txt");
    InputStream targetStream = Files.asByteSource(initialFile).openStream();
}
```

## 4. 使用 Commons IO 进行转换

最后，让我们看一下使用 Apache Commons IO 的解决方案：

```java
@Test
public void givenUsingCommonsIO_whenConvertingFileToInputStream_thenCorrect() 
  throws IOException {
    File initialFile = new File("src/main/resources/sample.txt");
    InputStream targetStream = FileUtils.openInputStream(initialFile);
}
```

我们终于得到它了。从Java文件打开流的三个简单而干净的解决方案。

## 5.总结

在本文中，我们探讨了使用不同的库将File转换为InputStream的各种方法。