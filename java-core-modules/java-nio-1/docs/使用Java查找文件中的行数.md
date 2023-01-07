## 1. 概述

在本教程中，我们将学习如何在标准JavaIO API、 [Google Guav](https://www.baeldung.com/category/guava/)[ a](https://www.baeldung.com/category/guava/)和[Apache Commons IO](https://www.baeldung.com/apache-commons-io)库的帮助下使用Java查找文件中的行数。

## 2. NIO2文件

请注意，在本教程中，我们将使用以下示例值作为输入文件名和总行数：

```java
static final String INPUT_FILE_NAME = "src/main/resources/input.txt";
static final int NO_OF_LINES = 45;

```

Java 7 对现有的 IO 库进行了许多改进，并将其封装在[NIO2 下：](https://www.baeldung.com/java-nio-2-file-api)

让我们从Files开始，看看我们如何使用它的 API 来计算行数：

```java
@Test
public void whenUsingNIOFiles_thenReturnTotalNumberOfLines() throws IOException {
    try (Stream<String> fileStream = Files.lines(Paths.get(INPUT_FILE_NAME))) {
        int noOfLines = (int) fileStream.count();
        assertEquals(NO_OF_LINES, noOfLines);
    }
}
```

或者简单地使用Files#readAllLines方法：

```java
@Test
public void whenUsingNIOFilesReadAllLines_thenReturnTotalNumberOfLines() throws IOException {
    List<String> fileStream = Files.readAllLines(Paths.get(INPUT_FILE_NAME));
    int noOfLines = fileStream.size();
    assertEquals(NO_OF_LINES, noOfLines);
}
```

## 3.NIO文件通道

现在让我们检查FileChannel，这是一种用于读取行数的高性能JavaNIO 替代方案：

```java
@Test
public void whenUsingNIOFileChannel_thenReturnTotalNumberOfLines() throws IOException {
    int noOfLines = 1;
    try (FileChannel channel = FileChannel.open(Paths.get(INPUT_FILE_NAME), StandardOpenOption.READ)) {
        ByteBuffer byteBuffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
        while (byteBuffer.hasRemaining()) {
            byte currentByte = byteBuffer.get();
            if (currentByte == 'n')
                noOfLines++;
       }
    }
    assertEquals(NO_OF_LINES, noOfLines);
}
```

虽然FileChannel是在 JDK 4 中引入的，但上述解决方案仅适用于 JDK 7 或更高版本。

## 4.谷歌番石榴文件

另一种第三方库是 Google Guava Files类。这个类也可以用来计算总行数，类似于我们在 Files#readAllLines中看到的。

让我们首先在pom.xml中添加[guava](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")[依赖](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")[项](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

然后我们可以使用 readLines 获取文件行列表：

```java
@Test
public void whenUsingGoogleGuava_thenReturnTotalNumberOfLines() throws IOException {
    List<String> lineItems = Files.readLines(Paths.get(INPUT_FILE_NAME)
      .toFile(), Charset.defaultCharset());
    int noOfLines = lineItems.size();
    assertEquals(NO_OF_LINES, noOfLines);
}
```

## 5.Apache Commons IO文件实用程序

现在，让我们看看 [Apache Commons IO ](https://www.baeldung.com/apache-commons-io)FileUtils API，它是 Guava 的并行解决方案。

要使用该库，我们必须在pom.xml中包含[commons-io 依赖项](https://search.maven.org/classic/#search|gav|1|g%3A"commons-io" AND a%3A"commons-io")：

```xml
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
</dependency>
```

那时，我们可以使用 Apache Commons IO 的 FileUtils#lineIterator，它会为我们清理一些文件处理：

```java
@Test
public void whenUsingApacheCommonsIO_thenReturnTotalNumberOfLines() throws IOException {
    int noOfLines = 0;
    LineIterator lineIterator = FileUtils.lineIterator(new File(INPUT_FILE_NAME));
    while (lineIterator.hasNext()) {
        lineIterator.nextLine();
        noOfLines++;
    }
    assertEquals(NO_OF_LINES, noOfLines);
}
```

正如我们所见，这比 Google Guava 解决方案更冗长。

## 6.缓冲阅读器

那么，老派的方法呢？如果我们不在 JDK 7 上并且我们不能使用第三方库，我们有[BufferedReader](https://www.baeldung.com/java-buffered-reader)：

```java
@Test
public void whenUsingBufferedReader_thenReturnTotalNumberOfLines() throws IOException {
    int noOfLines = 0;
    try (BufferedReader reader = new BufferedReader(new FileReader(INPUT_FILE_NAME))) {
        while (reader.readLine() != null) {
            noOfLines++;
        }
    }
    assertEquals(NO_OF_LINES, noOfLines);
}
```

## 7.行号读取器

或者，我们可以使用LineNumberReader，它是[BufferedReader](https://www.baeldung.com/java-buffered-reader)的直接子类，只是稍微简洁一点：

```java
@Test
public void whenUsingLineNumberReader_thenReturnTotalNumberOfLines() throws IOException {
    try (LineNumberReader reader = new LineNumberReader(new FileReader(INPUT_FILE_NAME))) {
        reader.skip(Integer.MAX_VALUE);
        int noOfLines = reader.getLineNumber() + 1;
        assertEquals(NO_OF_LINES, noOfLines);
    }
}
```

在这里，我们调用skip方法转到文件末尾，并且我们将自行编号从 0 开始以来计数的总行数加 1。

## 8.扫描仪

最后，如果我们已经将[Scanner](https://www.baeldung.com/java-scanner) 作为更大解决方案的一部分使用，它也可以为我们解决问题：

```java
@Test
public void whenUsingScanner_thenReturnTotalNumberOfLines() throws IOException {
    try (Scanner scanner = new Scanner(new FileReader(INPUT_FILE_NAME))) {
        int noOfLines = 0;
        while (scanner.hasNextLine()) {
            scanner.nextLine();
            noOfLines++;
        }
        assertEquals(NO_OF_LINES, noOfLines);
    }
}
```

## 9.总结

在本教程中，我们探索了使用Java查找文件行数的不同方法。由于所有这些 API 的主要目的不是计算文件中的行数，因此建议根据我们的需要选择正确的解决方案。