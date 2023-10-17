在本快速教程中，我们将使用普通Java将Reader的内容写入文件，然后是 Guava，最后是 Apache Commons IO 库。

本文是Baeldung 上[“Java – 回归基础”系列的一部分。](https://www.baeldung.com/java-tutorial)

## 1.用Java

让我们从简单的Java解决方案开始：

```java
@Test
public void givenUsingPlainJava_whenWritingReaderContentsToFile_thenCorrect() 
  throws IOException {
    Reader initialReader = new StringReader("Some text");

    int intValueOfChar;
    StringBuilder buffer = new StringBuilder();
    while ((intValueOfChar = initialReader.read()) != -1) {
        buffer.append((char) intValueOfChar);
    }
    initialReader.close();

    File targetFile = new File("src/test/resources/targetFile.txt");
    targetFile.createNewFile();

    Writer targetFileWriter = new FileWriter(targetFile);
    targetFileWriter.write(buffer.toString());
    targetFileWriter.close();
}
```

首先——我们将 Reader 的内容读入一个字符串；然后我们只是将字符串写入文件。

## 2.用番石榴

Guava 解决方案更简单——我们现在有 API 来处理将读取器写入文件：

```java
@Test
public void givenUsingGuava_whenWritingReaderContentsToFile_thenCorrect() 
  throws IOException {
    Reader initialReader = new StringReader("Some text");

    File targetFile = new File("src/test/resources/targetFile.txt");
    com.google.common.io.Files.touch(targetFile);
    CharSink charSink = com.google.common.io.Files.
      asCharSink(targetFile, Charset.defaultCharset(), FileWriteMode.APPEND);
    charSink.writeFrom(initialReader);

    initialReader.close();
}
```

## 3.使用Apache Commons IO

最后，Commons IO 解决方案——也使用更高级别的 API 从Reader读取数据并将该数据写入文件：

```java
@Test
public void givenUsingCommonsIO_whenWritingReaderContentsToFile_thenCorrect() 
  throws IOException {
    Reader initialReader = new CharSequenceReader("CharSequenceReader extends Reader");

    File targetFile = new File("src/test/resources/targetFile.txt");
    FileUtils.touch(targetFile);
    byte[] buffer = IOUtils.toByteArray(initialReader);
    FileUtils.writeByteArrayToFile(targetFile, buffer);

    initialReader.close();
}
```

我们有了它——将Reader 的内容写入 File 的3 个简单解决方案。