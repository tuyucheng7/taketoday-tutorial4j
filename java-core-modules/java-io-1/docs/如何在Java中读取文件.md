## 1. 概述

在本教程中，我们将探讨在Java中读取 File 的不同方法。

首先，我们将学习如何使用标准Java类从类路径、URL 或 JAR 文件加载文件。

其次，我们将了解如何使用BufferedReader、Scanner、StreamTokenizer、DataInputStream、SequenceInputStream和FileChannel读取内容 。我们还将讨论如何读取 UTF-8 编码的文件。

最后，我们将探讨在Java7 和Java8 中加载和读取文件的新技术。

本文是Baeldung 上[“Java – 回到基础”系列](https://www.baeldung.com/java-tutorial)的一部分 。

## 延伸阅读：

## [Java——创建文件](https://www.baeldung.com/java-how-to-create-a-file)

如何使用带有 NIO 或 Commons IO 的 JDK 6、JDK 7 在Java中创建文件。

[阅读更多](https://www.baeldung.com/java-how-to-create-a-file)→

## [Java——写入文件](https://www.baeldung.com/java-write-to-file)

使用Java将数据写入文件的多种方法。

[阅读更多](https://www.baeldung.com/java-write-to-file)→

## 2.设置

### 2.1. 输入文件

在本文中的大多数示例中，我们将读取文件名为fileTest.txt的文本文件，其中包含一行：

```plaintext
Hello, world!
```

对于几个示例，我们将使用不同的文件；在这些情况下，我们会明确提及文件及其内容。

### 2.2. 辅助方法

我们将使用一组仅包含核心Java类的测试示例，并且在测试中，我们将使用带有[Hamcrest](https://www.baeldung.com/hamcrest-collections-arrays)匹配器的断言。

测试将共享一个通用的readFromInputStream方法，该方法将InputStream转换为String以便于断言结果：

```java
private String readFromInputStream(InputStream inputStream)
  throws IOException {
    StringBuilder resultStringBuilder = new StringBuilder();
    try (BufferedReader br
      = new BufferedReader(new InputStreamReader(inputStream))) {
        String line;
        while ((line = br.readLine()) != null) {
            resultStringBuilder.append(line).append("n");
        }
    }
  return resultStringBuilder.toString();
}
```

请注意，还有其他方法可以实现相同的结果。我们可以参考[这篇文章](https://www.baeldung.com/convert-input-stream-to-a-file)了解一些替代方案。

## 3. 从类路径中读取文件

### 3.1. 使用标准 Java

本节介绍如何读取类路径上可用的文件。我们将阅读src/main/resources下可用的“ fileTest.txt ” ：

```java
@Test
public void givenFileNameAsAbsolutePath_whenUsingClasspath_thenFileData() {
    String expectedData = "Hello, world!";
    
    Class clazz = FileOperationsTest.class;
    InputStream inputStream = clazz.getResourceAsStream("/fileTest.txt");
    String data = readFromInputStream(inputStream);

    Assert.assertThat(data, containsString(expectedData));
}
```

在上面的代码片段中，我们使用当前类使用getResourceAsStream方法加载文件，并传递要加载的文件的绝对路径。

同样的方法也适用于ClassLoader实例：

```java
ClassLoader classLoader = getClass().getClassLoader();
InputStream inputStream = classLoader.getResourceAsStream("fileTest.txt");
String data = readFromInputStream(inputStream);
```

我们使用getClass().getClassLoader()获取当前类的类加载器。

主要区别在于，在ClassLoader实例上使用getResourceAsStream时，路径被视为从类路径的根开始的绝对路径。

当针对Class实例使用时，路径可以是相对于包的路径，也可以是绝对路径，由前导斜杠暗示。

当然，请注意，在实践中，打开的流应该始终关闭，例如我们示例中的InputStream ：

```java
InputStream inputStream = null;
try {
    File file = new File(classLoader.getResource("fileTest.txt").getFile());
    inputStream = new FileInputStream(file);
    
    //...
}     
finally {
    if (inputStream != null) {
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### 3.2. 使用commons-io库

另一个常见的选择是使用[commons-io](https://www.baeldung.com/apache-commons-io)包的FileUtils类：

```java
@Test
public void givenFileName_whenUsingFileUtils_thenFileData() {
    String expectedData = "Hello, world!";
        
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("fileTest.txt").getFile());
    String data = FileUtils.readFileToString(file, "UTF-8");
        
    assertEquals(expectedData, data.trim());
}
```

这里我们将File对象传递给FileUtils类的readFileToString()方法。这个实用程序类设法加载内容，而无需编写任何样板代码来创建InputStream实例和读取数据。

同一个库还提供了IOUtils 类：

```java
@Test
public void givenFileName_whenUsingIOUtils_thenFileData() {
    String expectedData = "Hello, world!";
        
    FileInputStream fis = new FileInputStream("src/test/resources/fileTest.txt");
    String data = IOUtils.toString(fis, "UTF-8");
        
    assertEquals(expectedData, data.trim());
}
```

这里我们将FileInputStream对象传递给IOUtils类的toString()方法。这个实用程序类的行为方式与前一个相同，目的是创建一个InputStream实例并读取数据。

## 4.使用BufferedReader读取

现在让我们关注解析文件内容的不同方法。

我们将从使用 BufferedReader 读取文件的简单方法开始：

```java
@Test
public void whenReadWithBufferedReader_thenCorrect()
  throws IOException {
     String expected_value = "Hello, world!";
     String file ="src/test/resources/fileTest.txt";
     
     BufferedReader reader = new BufferedReader(new FileReader(file));
     String currentLine = reader.readLine();
     reader.close();

    assertEquals(expected_value, currentLine);
}
```

请注意，当到达文件末尾时，readLine()将返回null 。

## 5. 使用JavaNIO 读取文件

在 JDK7 中，NIO 包进行了重大更新。

让我们看一个使用Files类和readAllLines 方法的示例。readAllLines方法接受路径。 

Path类可以被认为是java.io.File的升级，带有一些额外的操作。

### 5.1. 读取小文件

以下代码显示了如何使用新的Files类读取小文件：

```java
@Test
public void whenReadSmallFileJava7_thenCorrect()
  throws IOException {
    String expected_value = "Hello, world!";

    Path path = Paths.get("src/test/resources/fileTest.txt");

    String read = Files.readAllLines(path).get(0);
    assertEquals(expected_value, read);
}
```

请注意，如果我们需要二进制数据，也可以使用readAllBytes()方法。

### 5.2. 读取大文件

如果我们想用Files类读取一个大文件，我们可以使用 BufferedReader 。

以下代码使用新的Files类和BufferedReader读取文件：

```java
@Test
public void whenReadLargeFileJava7_thenCorrect()
  throws IOException {
    String expected_value = "Hello, world!";

    Path path = Paths.get("src/test/resources/fileTest.txt");

    BufferedReader reader = Files.newBufferedReader(path);
    String line = reader.readLine();
    assertEquals(expected_value, line);
}
```

### 5.3. 使用Files.lines()读取文件

JDK8在Files类中提供了lines()方法。它返回一个字符串元素流。

让我们看一个示例，了解如何将数据读入字节并使用 UTF-8 字符集对其进行解码。

以下代码使用新的Files.lines()读取文件：

```java
@Test
public void givenFilePath_whenUsingFilesLines_thenFileData() {
    String expectedData = "Hello, world!";
         
    Path path = Paths.get(getClass().getClassLoader()
      .getResource("fileTest.txt").toURI());
         
    Stream<String> lines = Files.lines(path);
    String data = lines.collect(Collectors.joining("n"));
    lines.close();
         
    Assert.assertEquals(expectedData, data.trim());
}
```

将 Stream 与 IO 通道(如文件操作)一起使用，我们需要使用close()方法显式关闭流 。

如我们所见，Files API 提供了另一种将文件内容读入字符串的简单方法。

在接下来的部分中，我们将看看在某些情况下可能适用的其他不太常见的读取文件的方法。

## 6.扫码阅读

接下来让我们使用扫描仪从文件中读取。这里我们将使用空格作为分隔符：

```java
@Test
public void whenReadWithScanner_thenCorrect()
  throws IOException {
    String file = "src/test/resources/fileTest.txt";
    Scanner scanner = new Scanner(new File(file));
    scanner.useDelimiter(" ");

    assertTrue(scanner.hasNext());
    assertEquals("Hello,", scanner.next());
    assertEquals("world!", scanner.next());

    scanner.close();
}
```

请注意，默认分隔符是空格，但多个分隔符可以与Scanner一起使用。

Scanner类在从控制台读取内容时很有用，或者当内容包含原始值时，带有已知的分隔符[(](https://www.baeldung.com/java-scanner)例如：由空格分隔的整数列表)。

## 7. 使用StreamTokenizer读取

[现在让我们使用StreamTokenizer](https://www.baeldung.com/java-streamtokenizer)将文本文件读入令牌。

tokenizer 的工作原理是首先弄清楚下一个标记是什么，字符串还是数字。我们通过查看tokenizer.ttype 字段来做到这一点。

然后我们将读取基于此类型的实际令牌：

-   tokenizer.nval——如果类型是数字
-   tokenizer.sval——如果类型是字符串

在这个例子中，我们将使用一个不同的输入文件，它只包含：

```bash
Hello 1
```

以下代码从文件中读取字符串和数字：

```java
@Test
public void whenReadWithStreamTokenizer_thenCorrectTokens()
  throws IOException {
    String file = "src/test/resources/fileTestTokenizer.txt";
   FileReader reader = new FileReader(file);
    StreamTokenizer tokenizer = new StreamTokenizer(reader);

    // token 1
    tokenizer.nextToken();
    assertEquals(StreamTokenizer.TT_WORD, tokenizer.ttype);
    assertEquals("Hello", tokenizer.sval);

    // token 2    
    tokenizer.nextToken();
    assertEquals(StreamTokenizer.TT_NUMBER, tokenizer.ttype);
    assertEquals(1, tokenizer.nval, 0.0000001);

    // token 3
    tokenizer.nextToken();
    assertEquals(StreamTokenizer.TT_EOF, tokenizer.ttype);
    reader.close();
}
```

请注意文件结尾标记是如何在末尾使用的。

这种方法对于将输入流解析为标记很有用。

## 8. 使用DataInputStream读取

我们可以使用DataInputStream从文件中读取二进制或原始数据类型。

以下测试使用DataInputStream读取文件：

```java
@Test
public void whenReadWithDataInputStream_thenCorrect() throws IOException {
    String expectedValue = "Hello, world!";
    String file ="src/test/resources/fileTest.txt";
    String result = null;

    DataInputStream reader = new DataInputStream(new FileInputStream(file));
    int nBytesToRead = reader.available();
    if(nBytesToRead > 0) {
        byte[] bytes = new byte[nBytesToRead];
        reader.read(bytes);
        result = new String(bytes);
    }

    assertEquals(expectedValue, result);
}
```

## 9. 使用FileChannel读取

如果我们正在读取一个大文件，FileChannel可以比标准 IO 更快。

以下代码使用FileChannel和RandomAccessFile从文件中读取数据字节：

```java
@Test
public void whenReadWithFileChannel_thenCorrect()
  throws IOException {
    String expected_value = "Hello, world!";
    String file = "src/test/resources/fileTest.txt";
    RandomAccessFile reader = new RandomAccessFile(file, "r");
    FileChannel channel = reader.getChannel();

    int bufferSize = 1024;
    if (bufferSize > channel.size()) {
        bufferSize = (int) channel.size();
    }
    ByteBuffer buff = ByteBuffer.allocate(bufferSize);
    channel.read(buff);
    buff.flip();
    
    assertEquals(expected_value, new String(buff.array()));
    channel.close();
    reader.close();
}
```

## 10. 读取 UTF-8 编码文件

现在让我们看看如何使用BufferedReader 读取 UTF-8 编码的文件。在这个例子中，我们将读取一个包含中文字符的文件：

```java
@Test
public void whenReadUTFEncodedFile_thenCorrect()
  throws IOException {
    String expected_value = "青空";
    String file = "src/test/resources/fileTestUtf8.txt";
    BufferedReader reader = new BufferedReader
      (new InputStreamReader(new FileInputStream(file), "UTF-8"));
    String currentLine = reader.readLine();
    reader.close();

    assertEquals(expected_value, currentLine);
}
```

## 11. 从 URL 读取内容

要从 URL 中读取内容，我们将在示例中使用“ / ”URL：

```java
@Test
public void givenURLName_whenUsingURL_thenFileData() {
    String expectedData = "Baeldung";

    URL urlObject = new URL("/");
    URLConnection urlConnection = urlObject.openConnection();
    InputStream inputStream = urlConnection.getInputStream();
    String data = readFromInputStream(inputStream);

    Assert.assertThat(data, containsString(expectedData));
}
```

还有其他连接到 URL 的方法。这里我们使用了标准 SDK 中可用的URL和URLConnection类。

## 12. 从 JAR 中读取文件

要读取位于 JAR 文件中的文件，我们需要一个包含文件的 JAR。对于我们的示例，我们将从“ hamcrest-library-1.3.jar ”文件中读取“ LICENSE.txt ” ：

```java
@Test
public void givenFileName_whenUsingJarFile_thenFileData() {
    String expectedData = "BSD License";

    Class clazz = Matchers.class;
    InputStream inputStream = clazz.getResourceAsStream("/LICENSE.txt");
    String data = readFromInputStream(inputStream);

    Assert.assertThat(data, containsString(expectedData));
}
```

这里我们要加载驻留在 Hamcrest 库中的LICENSE.txt ，因此我们将使用有助于获取资源的Matcher类。也可以使用类加载器加载相同的文件。

## 13.总结

如我们所见，使用纯Java加载文件和从中读取数据有多种可能性。

我们可以从不同的位置加载文件，如类路径、URL 或 jar 文件。

然后我们可以使用BufferedReader逐行读取，Scanner使用不同的分隔符读取，StreamTokenizer将文件读入令牌，DataInputStream读取二进制数据和原始数据类型，SequenceInput Stream将多个文件链接成一个流，FileChannel读取更快来自大文件等。