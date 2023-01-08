在本快速教程中，我们将说明如何使用普通 Java、Guava 或 Apache Commons IO 将文件转换为阅读器 。让我们开始吧。

本文是Baeldung 上[“Java – 回归基础”系列的一部分。](https://www.baeldung.com/java-tutorial)

## 1. 使用纯 Java

我们先来看简单的Java方案：

```java
@Test
public void givenUsingPlainJava_whenConvertingFileIntoReader_thenCorrect() 
  throws IOException {
    File initialFile = new File("src/test/resources/initialFile.txt");
    initialFile.createNewFile();
    Reader targetReader = new FileReader(initialFile);
    targetReader.close();
}
```

## 2.用番石榴

现在——让我们看看同样的转换，这次使用 Guava 库：

```java
@Test
public void givenUsingGuava_whenConvertingFileIntoReader_thenCorrect() throws 
  IOException {
    File initialFile = new File("src/test/resources/initialFile.txt");
    com.google.common.io.Files.touch(initialFile);
    Reader targetReader = Files.newReader(initialFile, Charset.defaultCharset());
    targetReader.close();
}
```

## 3.使用通用IO

最后，让我们以 Commons IO 代码示例结束，通过中间字节数组进行转换：

```java
@Test
public void givenUsingCommonsIO_whenConvertingFileIntoReader_thenCorrect() 
  throws IOException {
    File initialFile = new File("src/test/resources/initialFile.txt");
    FileUtils.touch(initialFile);
    FileUtils.write(initialFile, "With Commons IO");
    byte[] buffer = FileUtils.readFileToByteArray(initialFile);
    Reader targetReader = new CharSequenceReader(new String(buffer));
    targetReader.close();
}
```

我们有了它——将文件转换为阅读器的3 种方法——首先使用纯 Java，然后使用 Guava，最后使用 Apache Commons IO 库。