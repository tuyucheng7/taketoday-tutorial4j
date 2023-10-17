## 1. 概述

在这篇简短的文章中，我们将研究读取文件中给定行号处的行的不同方法。

## 2.输入文件

让我们首先创建一个名为inputLines.txt的简单文件，我们将在所有示例中使用该文件：

```yaml
Line 1
Line 2
Line 3
Line 4
Line 5
```

## 3.使用缓冲阅读器

让我们看看众所周知的[BufferedReader](https://www.baeldung.com/java-buffered-reader)类及其不将整个文件存储到内存中的优点。

我们可以逐行读取文件并在需要时停止：

```java
@Test
public void givenFile_whenUsingBufferedReader_thenExtractedLineIsCorrect() {
    try (BufferedReader br = Files.newBufferedReader(Paths.get(FILE_PATH))) {
        for (int i = 0; i < 3; i++) {
            br.readLine();
        }

        String extractedLine = br.readLine();
        assertEquals("Line 4", extractedLine);
    }
}
```

## 4.使用扫描仪

我们可以采用的另一种类似方法是使用[Scanner](https://www.baeldung.com/java-scanner)：

```java
@Test
public void givenFile_whenUsingScanner_thenExtractedLineIsCorrect() {
    try (Scanner scanner = new Scanner(new File(FILE_PATH))) {
        for (int i = 0; i < 3; i++) {
            scanner.nextLine();
        }

        String extractedLine = scanner.nextLine();
        assertEquals("Line 4", extractedLine);
    }
}
```

尽管在小文件上， BufferedReader和Scanner 之间的区别可能不明显，但在大文件上，Scanner会更慢，因为它也[进行解析](https://www.baeldung.com/bufferedreader-vs-console-vs-scanner-in-java#parsingstream)并且缓冲区大小更小。

## 5. 使用文件 API

### 5.1. 小文件

我们可以使用[File API](https://www.baeldung.com/java-nio-2-file-api)中的Files.readAllLines()轻松地将文件内容读入内存并提取我们想要的行：

```java
@Test
public void givenSmallFile_whenUsingFilesAPI_thenExtractedLineIsCorrect() {
    String extractedLine = Files.readAllLines(Paths.get(FILE_PATH)).get(4);

    assertEquals("Line 5", extractedLine);
}
```

### 5.2. 大文件

如果我们需要处理大文件，我们应该使用lines方法，它返回一个Stream以便我们可以逐行读取文件：

```java
@Test
public void givenLargeFile_whenUsingFilesAPI_thenExtractedLineIsCorrect() {
    try (Stream lines = Files.lines(Paths.get(FILE_PATH))) {
        String extractedLine = lines.skip(4).findFirst().get();

        assertEquals("Line 5", extractedLine);
    }
}
```

## 6. 使用 Apache Commons IO

另一种选择是使用[commons-io](https://www.baeldung.com/apache-commons-io)包的FileUtils 类，它读取整个文件并将行作为String列表返回：

```java
@Test
public void givenFile_whenUsingFileUtils_thenExtractedLineIsCorrect() {
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("linesInput.txt").getFile());

    List<String> lines = FileUtils.readLines(file, "UTF-8");

    String extractedLine = lines.get(0);
    assertEquals("Line 1", extractedLine);
}
```

我们也可以使用IOUtils类来实现相同的结果，除了这次，整个内容作为String返回，我们必须自己进行拆分：

```java
@Test
public void givenFile_whenUsingIOUtils_thenExtractedLineIsCorrect() {
    String fileContent = IOUtils.toString(new FileInputStream(FILE_PATH), StandardCharsets.UTF_8);

    String extractedLine = fileContent.split(System.lineSeparator())[0];
    assertEquals("Line 1", extractedLine);
}
```

## 七、总结

在这篇简短的文章中，我们介绍了从文件中读取给定行号处的行的最常见方法。