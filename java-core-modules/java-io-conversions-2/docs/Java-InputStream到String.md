## 1. 概述

在本教程中，我们将了解如何将InputStream转换为 String。

我们将从使用普通 Java(包括 Java8/9 解决方案)开始，然后研究使用[Guava](https://github.com/google/guava)和[Apache Commons IO](https://commons.apache.org/proper/commons-io/)库。

本文是Baeldung 上[“Java – 回归基础”系列的一部分。](https://www.baeldung.com/java-tutorial)

## 延伸阅读：

## [Java InputStream 到字节数组和 ByteBuffer](https://www.baeldung.com/convert-input-stream-to-array-of-bytes)

如何使用纯 Java、Guava 或 Commons IO 将 InputStream 转换为 byte[]。

[阅读更多](https://www.baeldung.com/convert-input-stream-to-array-of-bytes)→

## [Java – 将 InputStream 写入文件](https://www.baeldung.com/convert-input-stream-to-a-file)

如何将 InputStream 写入文件 - 使用 Java、Guava 和 Commons IO 库。

[阅读更多](https://www.baeldung.com/convert-input-stream-to-a-file)→

## [Java – InputStream 到 Reader](https://www.baeldung.com/java-convert-inputstream-to-reader)

如何使用 Java、Guava 和 Apache Commons IO 库将 InputStream 转换为 Reader。

[阅读更多](https://www.baeldung.com/java-convert-inputstream-to-reader)→

## 2. 使用Java转换 – StringBuilder

让我们看一下使用普通 Java、一个InputStream和一个简单的StringBuilder 的简单的、较低级别的方法：

```java
@Test
public void givenUsingJava5_whenConvertingAnInputStreamToAString_thenCorrect() 
  throws IOException {
    String originalString = randomAlphabetic(DEFAULT_SIZE);
    InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

    StringBuilder textBuilder = new StringBuilder();
    try (Reader reader = new BufferedReader(new InputStreamReader
      (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
        int c = 0;
        while ((c = reader.read()) != -1) {
            textBuilder.append((char) c);
        }
    }
    assertEquals(textBuilder.toString(), originalString);
}
```

## 3. 使用Java8 转换 – BufferedReader

Java 8为BufferedReader带来了一个新的[lines()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/BufferedReader.html#lines())方法。让我们看看如何使用它来将InputStream转换为String：

```java
@Test
public void givenUsingJava8_whenConvertingAnInputStreamToAString_thenCorrect() {
    String originalString = randomAlphabetic(DEFAULT_SIZE);
    InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

    String text = new BufferedReader(
      new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("n"));

    assertThat(text, equalTo(originalString));
}
```

重要的是要提到lines()在底层使用readLine()方法。readLine()假定一行由换行符(“n”)、回车符(“r”)或回车符后紧跟换行符中的任何一个终止。换句话说，它支持所有常见的行尾样式：Unix、Windows，甚至是旧的 Mac OS。

另一方面，当我们使用Collectors.joining()时，我们需要明确决定要为创建的String使用哪种类型的 EOL 。

我们还可以使用Collectors.joining(System.lineSeparator())，在这种情况下输出取决于系统设置。

## 4. 使用Java9 转换 – InputStream.readAllBytes()

如果我们使用的是Java9 或更高版本，我们可以使用添加到InputStream的新readAllBytes方法：

```java
@Test
public void givenUsingJava9_whenConvertingAnInputStreamToAString_thenCorrect() throws IOException {
    String originalString = randomAlphabetic(DEFAULT_SIZE);
    InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

    String text = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    
    assertThat(text, equalTo(originalString));
}
```

我们需要知道，这个简单的代码适用于可以方便地将所有字节读入字节数组的简单情况。我们不应该用它来读取具有大量数据的输入流。

## 5. 使用Java和扫描器进行转换

接下来，让我们看一个使用标准文本扫描器的普通Java示例：

```java
@Test
public void givenUsingJava7_whenConvertingAnInputStreamToAString_thenCorrect() 
  throws IOException {
    String originalString = randomAlphabetic(8);
    InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

    String text = null;
    try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
        text = scanner.useDelimiter("A").next();
    }

    assertThat(text, equalTo(originalString));
}
```

请注意，InputStream将随着Scanner的关闭而关闭。

还值得澄清一下useDelimiter(“A”) 的作用。这里我们传递了'A'，这是一个边界标记正则表达式，表示输入的开始。本质上，这意味着next()调用读取整个输入流。

这是Java7 示例而不是Java5 示例的唯一原因是使用了try-with-resources语句。如果我们把它变成一个标准的try-finally块，它就可以用Java5 编译得很好。

## 6. 使用ByteArrayOutputStream进行转换

最后，让我们看另一个简单的Java示例，这次使用ByteArrayOutputStream类：

```java
@Test
public void givenUsingPlainJava_whenConvertingAnInputStreamToString_thenCorrect()
  throws IOException {
    String originalString = randomAlphabetic(8);
    InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    int nRead;
    byte[] data = new byte[1024];
    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, nRead);
    }

    buffer.flush();
    byte[] byteArray = buffer.toByteArray();
        
    String text = new String(byteArray, StandardCharsets.UTF_8);
    assertThat(text, equalTo(originalString));
}
```

在此示例中，通过读取和写入字节块将InputStream转换为ByteArrayOutputStream 。然后将OutputStream转换为字节数组，用于创建String。

## 7. 使用java.nio转换

另一种解决方案是将InputStream的内容到文件中，然后将其转换为String：

```java
@Test
public void givenUsingTempFile_whenConvertingAnInputStreamToAString_thenCorrect() 
  throws IOException {
    String originalString = randomAlphabetic(DEFAULT_SIZE);
    InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

    Path tempFile = 
      Files.createTempDirectory("").resolve(UUID.randomUUID().toString() + ".tmp");
    Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
    String result = new String(Files.readAllBytes(tempFile));

    assertThat(result, equalTo(originalString));
}
```

这里我们使用java.nio.file.Files类来创建一个临时文件，并将InputStream的内容到该文件中。然后使用同一个类通过readAllBytes()方法将文件内容转换为String。

## 8. 用 Guava 转换

让我们从一个利用ByteSource功能的 Guava 示例开始：

```java
@Test
public void givenUsingGuava_whenConvertingAnInputStreamToAString_thenCorrect() 
  throws IOException {
    String originalString = randomAlphabetic(8);
    InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

    ByteSource byteSource = new ByteSource() {
        @Override
        public InputStream openStream() throws IOException {
            return inputStream;
        }
    };

    String text = byteSource.asCharSource(Charsets.UTF_8).read();

    assertThat(text, equalTo(originalString));
}
```

让我们回顾一下这些步骤：

-   首先——我们将InputStream包装到ByteSource 中，据我们所知，这是最简单的方法。
-   然后——我们将ByteSource视为具有 UTF8 字符集的CharSource。
-   最后——我们使用CharSource将其作为字符串读取。

一种更简单的转换方法是使用 Guava，但需要显式关闭流；幸运的是，我们可以简单地使用 try-with-resources 语法来解决这个问题：

```java
@Test
public void givenUsingGuavaAndJava7_whenConvertingAnInputStreamToAString_thenCorrect() 
  throws IOException {
    String originalString = randomAlphabetic(8);
    InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
 
    String text = null;
    try (Reader reader = new InputStreamReader(inputStream)) {
        text = CharStreams.toString(reader);
    }
 
    assertThat(text, equalTo(originalString));
}
```

## 9. 使用 Apache Commons IO 进行转换

现在让我们看看如何使用 Commons IO 库来做到这一点。

这里的一个重要警告是，与 Guava 不同，这些示例都不会关闭 InputStream ：

```java
@Test
public void givenUsingCommonsIo_whenConvertingAnInputStreamToAString_thenCorrect() 
  throws IOException {
    String originalString = randomAlphabetic(8);
    InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

    String text = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
    assertThat(text, equalTo(originalString));
}
```

我们还可以使用StringWriter进行转换：

```java
@Test
public void givenUsingCommonsIoWithCopy_whenConvertingAnInputStreamToAString_thenCorrect() 
  throws IOException {
    String originalString = randomAlphabetic(8);
    InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

    StringWriter writer = new StringWriter();
    String encoding = StandardCharsets.UTF_8.name();
    IOUtils.copy(inputStream, writer, encoding);

    assertThat(writer.toString(), equalTo(originalString));
}
```

## 10.总结

在本文中，我们学习了如何将 InputStream转换为 String。我们从使用纯Java开始，然后探讨了如何使用[Guava](https://github.com/google/guava)和[Apache Commons IO](https://commons.apache.org/proper/commons-io/)库。