## 1. 概述

在本快速教程中，我们将说明如何将InputStream写入文件。首先我们将使用纯 Java，然后是 Guava，最后是 Apache Commons IO 库。

本文是Baeldung 上[“Java– 回到基础”教程的一部分。](https://www.baeldung.com/java-tutorial)

## 延伸阅读：

## [Java – InputStream 到 Reader](https://www.baeldung.com/java-convert-inputstream-to-reader)

如何使用 Java、Guava 和 Apache Commons IO 库将 InputStream 转换为 Reader。

[阅读更多](https://www.baeldung.com/java-convert-inputstream-to-reader)→

## [Java – 将文件转换为输入流](https://www.baeldung.com/convert-file-to-input-stream)

如何从Java文件打开 InputStream - 使用纯 Java、Guava 和 Apache Commons IO 库。

[阅读更多](https://www.baeldung.com/convert-file-to-input-stream)→

## [Java InputStream 到字节数组和 ByteBuffer](https://www.baeldung.com/convert-input-stream-to-array-of-bytes)

如何使用纯 Java、Guava 或 Commons IO 将 InputStream 转换为 byte[]。

[阅读更多](https://www.baeldung.com/convert-input-stream-to-array-of-bytes)→

## 2. 使用普通Java进行转换

让我们从Java 解决方案开始：

```java
@Test
public void whenConvertingToFile_thenCorrect() throws IOException {
    Path path = Paths.get("src/test/resources/sample.txt");
    byte[] buffer = java.nio.file.Files.readAllBytes(path);

    File targetFile = new File("src/test/resources/targetFile.tmp");
    OutputStream outStream = new FileOutputStream(targetFile);
    outStream.write(buffer);

    IOUtils.closeQuietly(outStream);
}
```

请注意，在此示例中，输入流具有已知和预先确定的数据，例如磁盘上的文件或内存中的流。因此，我们不需要进行任何边界检查，如果内存允许，我们可以简单地一次性读取和写入。

如果输入流链接到正在进行的数据流，例如来自正在进行的连接的 HTTP 响应，则不能一次读取整个流。在这种情况下，我们需要确保我们继续阅读直到到达流的末尾：

```java
@Test
public void whenConvertingInProgressToFile_thenCorrect() 
  throws IOException {
 
    InputStream initialStream = new FileInputStream(
      new File("src/main/resources/sample.txt"));
    File targetFile = new File("src/main/resources/targetFile.tmp");
    OutputStream outStream = new FileOutputStream(targetFile);

    byte[] buffer = new byte[8  1024];
    int bytesRead;
    while ((bytesRead = initialStream.read(buffer)) != -1) {
        outStream.write(buffer, 0, bytesRead);
    }
    IOUtils.closeQuietly(initialStream);
    IOUtils.closeQuietly(outStream);
}
```

最后，这是我们可以使用Java8 执行相同操作的另一种简单方法：

```java
@Test
public void whenConvertingAnInProgressInputStreamToFile_thenCorrect2() 
  throws IOException {
 
    InputStream initialStream = new FileInputStream(
      new File("src/main/resources/sample.txt"));
    File targetFile = new File("src/main/resources/targetFile.tmp");

    java.nio.file.Files.copy(
      initialStream, 
      targetFile.toPath(), 
      StandardCopyOption.REPLACE_EXISTING);

    IOUtils.closeQuietly(initialStream);
}
```

## 3. 使用 Guava 转换

接下来我们看一个更简单的基于Guava的解决方案：

```java
@Test
public void whenConvertingInputStreamToFile_thenCorrect3() 
  throws IOException {
 
    InputStream initialStream = new FileInputStream(
      new File("src/main/resources/sample.txt"));
    byte[] buffer = new byte[initialStream.available()];
    initialStream.read(buffer);

    File targetFile = new File("src/main/resources/targetFile.tmp");
    Files.write(buffer, targetFile);
}
```

## 4. 使用 Commons IO 进行转换

最后，这里有一个使用 Apache Commons IO 的更快的解决方案：

```java
@Test
public void whenConvertingInputStreamToFile_thenCorrect4() 
  throws IOException {
    InputStream initialStream = FileUtils.openInputStream
      (new File("src/main/resources/sample.txt"));

    File targetFile = new File("src/main/resources/targetFile.tmp");

    FileUtils.copyInputStreamToFile(initialStream, targetFile);
}
```

至此，我们有了 3 种将InputStream写入文件的快速方法。