在本快速教程中，我们将研究从Reader到InputStream的转换——首先使用纯 Java，然后使用 Guava，最后使用 Apache Commons IO 库。

本文是Baeldung 上[“Java – 回归基础”系列的一部分。](https://www.baeldung.com/java-tutorial)

## 1.用Java

让我们从Java解决方案开始：

```java
@Test
public void givenUsingPlainJava_whenConvertingReaderIntoInputStream_thenCorrect() 
  throws IOException {
    Reader initialReader = new StringReader("With Java");

    char[] charBuffer = new char[8  1024];
    StringBuilder builder = new StringBuilder();
    int numCharsRead;
    while ((numCharsRead = initialReader.read(charBuffer, 0, charBuffer.length)) != -1) {
        builder.append(charBuffer, 0, numCharsRead);
    }
    InputStream targetStream = new ByteArrayInputStream(
      builder.toString().getBytes(StandardCharsets.UTF_8));

    initialReader.close();
    targetStream.close();
}
```

请注意，我们一次读取(和写入)数据块。

## 2.用番石榴

接下来——让我们看看更简单的 Guava 解决方案：

```java
@Test
public void givenUsingGuava_whenConvertingReaderIntoInputStream_thenCorrect() 
  throws IOException {
    Reader initialReader = new StringReader("With Guava");

    InputStream targetStream = 
      new ByteArrayInputStream(CharStreams.toString(initialReader)
      .getBytes(Charsets.UTF_8));

    initialReader.close();
    targetStream.close();
}
```

请注意，我们使用的是开箱即用的输入流，它将整个转换变成一个衬里。

## 3.使用通用IO

最后——让我们看看几个Commons IO 解决方案——也是简单的一个衬垫。

首先，使用[ReaderInputStream](https://commons.apache.org/proper/commons-io/apidocs/org/apache/commons/io/input/ReaderInputStream.html)：

```java
@Test
public void givenUsingCommonsIOReaderInputStream_whenConvertingReaderIntoInputStream() 
  throws IOException {
    Reader initialReader = new StringReader("With Commons IO");

    InputStream targetStream = new ReaderInputStream(initialReader, Charsets.UTF_8);

    initialReader.close();
    targetStream.close();
}
```

[最后，使用IOUtils](https://commons.apache.org/proper/commons-io/apidocs/org/apache/commons/io/IOUtils.html#toString-java.io.Reader-)进行相同的转换：

```java
@Test
public void givenUsingCommonsIOUtils_whenConvertingReaderIntoInputStream() 
  throws IOException {
    Reader initialReader = new StringReader("With Commons IO");

    InputStream targetStream = 
      IOUtils.toInputStream(IOUtils.toString(initialReader), Charsets.UTF_8);

    initialReader.close();
    targetStream.close();
}
```

请注意，我们在这里处理任何类型的阅读器——但如果你专门处理文本数据，明确指定字符集而不是使用 JVM 默认值始终是个好主意。

## 4。总结

就这样了——将Reader转换为InputStream 的3 种简单方法。