## 1. 概述

在本教程中，我们将学习如何写入文件，然后如何使用Guava IO从文件中读取。我们将讨论如何写入文件。

## 2. 使用文件编写

让我们从一个简单的示例开始，使用Files将String写入文件：

```java
@Test
public void whenWriteUsingFiles_thenWritten() throws IOException {
    String expectedValue = "Hello world";
    File file = new File("test.txt");
    Files.write(expectedValue, file, Charsets.UTF_8);
    String result = Files.toString(file, Charsets.UTF_8);
    assertEquals(expectedValue, result);
}
```

请注意，我们还可以使用Files.append() API附加到现有文件。

## 3. 使用CharSink写入文件

接下来 – 让我们看看如何使用CharSink将字符串写入文件。在下面的示例中——我们使用Files.asCharSink()从文件中获取一个CharSink ，然后用它来写入：

```java
@Test
public void whenWriteUsingCharSink_thenWritten() throws IOException {
    String expectedValue = "Hello world";
    File file = new File("test.txt");
    CharSink sink = Files.asCharSink(file, Charsets.UTF_8);
    sink.write(expectedValue);

    String result = Files.toString(file, Charsets.UTF_8);
    assertEquals(expectedValue, result);
}
```

我们还可以使用CharSink将多行写入文件。在下面的例子中——我们写了一个名字列表，我们使用一个空格作为行分隔符：

```java
@Test
public void whenWriteMultipleLinesUsingCharSink_thenWritten() throws IOException {
    List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
    File file = new File("test.txt");
    CharSink sink = Files.asCharSink(file, Charsets.UTF_8);
    sink.writeLines(names, " ");

    String result = Files.toString(file, Charsets.UTF_8);
    String expectedValue = Joiner.on(" ").join(names);
    assertEquals(expectedValue, result.trim());
}
```

## 4. 使用ByteSink写入文件

我们还可以使用ByteSink写入原始字节。在下面的示例中——我们使用Files.asByteSink()从文件中获取一个ByteSink ，然后用它来写入：

```java
@Test
public void whenWriteUsingByteSink_thenWritten() throws IOException {
    String expectedValue = "Hello world";
    File file = new File("test.txt");
    ByteSink sink = Files.asByteSink(file);
    sink.write(expectedValue.getBytes());

    String result = Files.toString(file, Charsets.UTF_8);
    assertEquals(expectedValue, result);
}
```

请注意，我们可以使用简单的转换byteSink.asCharSink()在ByteSink和CharSink之间移动。

## 5. 使用文件读取文件

接下来——让我们讨论如何使用文件从文件中读取。

在下面的示例中——我们使用简单的Files.toString() 读取文件的所有内容：

```java
@Test
public void whenReadUsingFiles_thenRead() throws IOException {
    String expectedValue = "Hello world";
    File file = new File("test.txt");
    String result = Files.toString(file, Charsets.UTF_8);

    assertEquals(expectedValue, result);
}
```

我们还可以将文件读入行列表，如下例所示：

```java
@Test
public void whenReadMultipleLinesUsingFiles_thenRead() throws IOException {
    File file = new File("test.txt");
    List<String> result = Files.readLines(file, Charsets.UTF_8);

    assertThat(result, contains("John", "Jane", "Adam", "Tom"));
}
```

请注意，我们可以使用Files.readFirstLine()来只读取文件的第一行。

## 6. 使用CharSource读取文件

接下来——让我们看看如何使用 Charsource 从文件中读取。

在下面的示例中——我们使用Files.asCharSource()从文件中获取一个CharSource ，然后使用它来使用read()读取所有文件内容：

```java
@Test
public void whenReadUsingCharSource_thenRead() throws IOException {
    String expectedValue = "Hello world";
    File file = new File("test.txt");
    CharSource source = Files.asCharSource(file, Charsets.UTF_8);

    String result = source.read();
    assertEquals(expectedValue, result);
}
```

我们还可以连接两个 CharSources 并将它们用作一个CharSource。

在下面的示例中——我们读取了两个文件，第一个包含“ Hello world ”，另一个包含“ Test ”：

```java
@Test
public void whenReadMultipleCharSources_thenRead() throws IOException {
    String expectedValue = "Hello worldTest";
    File file1 = new File("test1.txt");
    File file2 = new File("test2.txt");

    CharSource source1 = Files.asCharSource(file1, Charsets.UTF_8);
    CharSource source2 = Files.asCharSource(file2, Charsets.UTF_8);
    CharSource source = CharSource.concat(source1, source2);

    String result = source.read();
    assertEquals(expectedValue, result);
}
```

## 7. 使用CharStreams读取文件

现在 - 让我们看看如何使用CharStreams通过中介FileReader将 File 的内容读入String：

```java
@Test
public void whenReadUsingCharStream_thenRead() throws IOException {
    String expectedValue = "Hello world";
    FileReader reader = new FileReader("test.txt");
    String result = CharStreams.toString(reader);

    assertEquals(expectedValue, result);
    reader.close();
}
```

## 8. 使用ByteSource读取文件

我们可以将ByteSource用于原始字节格式的文件内容——如下例所示：

```java
@Test
public void whenReadUsingByteSource_thenRead() throws IOException {
    String expectedValue = "Hello world";
    File file = new File("test.txt");
    ByteSource source = Files.asByteSource(file);

    byte[] result = source.read();
    assertEquals(expectedValue, new String(result));
}
```

我们还可以使用slice()在特定偏移量之后开始读取字节，如下例所示：

```java
@Test
public void whenReadAfterOffsetUsingByteSource_thenRead() throws IOException {
    String expectedValue = "lo world";
    File file = new File("test.txt");
    long offset = 3;
    long len = 1000;

    ByteSource source = Files.asByteSource(file).slice(offset, len);
    byte[] result = source.read();
    assertEquals(expectedValue, new String(result));
}
```

请注意，我们可以使用byteSource.asCharSource()来获取此ByteSource的CharSource视图。


## 9. 使用字节流读取文件

接下来——让我们看看如何使用ByteStreams将文件内容读入原始字节数组；我们将使用中介FileInputStream来执行转换：

```java
@Test
public void whenReadUsingByteStream_thenRead() throws IOException {
    String expectedValue = "Hello world";
    FileInputStream reader = new FileInputStream("test.txt");
    byte[] result = ByteStreams.toByteArray(reader);
    reader.close();

    assertEquals(expectedValue, new String(result));
}
```

## 10.阅读使用资源

最后——让我们看看如何读取类路径中存在的文件——使用Resources实用程序，如下例所示：

```java
@Test
public void whenReadUsingResources_thenRead() throws IOException {
    String expectedValue = "Hello world";
    URL url = Resources.getResource("test.txt");
    String result = Resources.toString(url, Charsets.UTF_8);

    assertEquals(expectedValue, result);
}
```

## 11.总结

在本快速教程中，我们说明了使用 Guava IO支持和实用程序读取和写入文件的各种方法。