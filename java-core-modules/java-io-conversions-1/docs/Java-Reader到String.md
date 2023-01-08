在本快速教程中，我们将使用纯 Java、Guava 和 Apache Commons IO 库将Reader转换为 String。

本文是Baeldung 上[“Java – 回归基础”系列的一部分。](https://www.baeldung.com/java-tutorial)

## 1.用Java

让我们从一个简单的Java解决方案开始，该解决方案从Reader顺序读取字符：

```java
@Test
public void givenUsingPlainJava_whenConvertingReaderIntoStringV1_thenCorrect() 
  throws IOException {
    StringReader reader = new StringReader("text");
    int intValueOfChar;
    String targetString = "";
    while ((intValueOfChar = reader.read()) != -1) {
        targetString += (char) intValueOfChar;
    }
    reader.close();
}
```

如果要阅读的内容很多，批量读取的解决方案会更好：

```java
@Test
public void givenUsingPlainJava_whenConvertingReaderIntoStringV2_thenCorrect() 
  throws IOException {
    Reader initialReader = new StringReader("text");
    char[] arr = new char[8  1024];
    StringBuilder buffer = new StringBuilder();
    int numCharsRead;
    while ((numCharsRead = initialReader.read(arr, 0, arr.length)) != -1) {
        buffer.append(arr, 0, numCharsRead);
    }
    initialReader.close();
    String targetString = buffer.toString();
}
```

## 2.用番石榴

Guava 提供了一个可以直接进行转换的实用程序：

```java
@Test
public void givenUsingGuava_whenConvertingReaderIntoString_thenCorrect() 
  throws IOException {
    Reader initialReader = CharSource.wrap("With Google Guava").openStream();
    String targetString = CharStreams.toString(initialReader);
    initialReader.close();
}
```

## 3.使用通用IO

与 Apache Commons IO 相同——有一个 IO 实用程序能够执行直接转换：

```java
@Test
public void givenUsingCommonsIO_whenConvertingReaderIntoString_thenCorrect() 
  throws IOException {
    Reader initialReader = new StringReader("With Apache Commons");
    String targetString = IOUtils.toString(initialReader);
    initialReader.close();
}
```

就这样了——将Reader转换为普通字符串的 4 种方法。