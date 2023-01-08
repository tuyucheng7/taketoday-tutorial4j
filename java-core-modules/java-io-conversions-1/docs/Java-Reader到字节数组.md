本快速教程将展示如何使用纯 Java、Guava 和 Apache Commons IO 库将 Reader 转换为 byte[] 。

本文是Baeldung 上[“Java – 回归基础”系列的一部分。](https://www.baeldung.com/java-tutorial)

## 1.用Java

让我们从简单的Java解决方案开始——通过一个中间字符串：

```java
@Test
public void givenUsingPlainJava_whenConvertingReaderIntoByteArray_thenCorrect() 
  throws IOException {
    Reader initialReader = new StringReader("With Java");

    char[] charArray = new char[8  1024];
    StringBuilder builder = new StringBuilder();
    int numCharsRead;
    while ((numCharsRead = initialReader.read(charArray, 0, charArray.length)) != -1) {
        builder.append(charArray, 0, numCharsRead);
    }
    byte[] targetArray = builder.toString().getBytes();

    initialReader.close();
}
```

请注意，读取是分块进行的，而不是一次一个字符。

## 2.用番石榴

接下来——让我们看看 Guava 解决方案——同样使用中间字符串：

```java
@Test
public void givenUsingGuava_whenConvertingReaderIntoByteArray_thenCorrect() 
  throws IOException {
    Reader initialReader = CharSource.wrap("With Google Guava").openStream();

    byte[] targetArray = CharStreams.toString(initialReader).getBytes();

    initialReader.close();
}
```

请注意，我们正在使用内置的实用程序 API，而不必对普通Java示例进行任何低级转换。

## 3.使用通用IO

最后——这是一个开箱即用的 Commons IO 支持的直接解决方案：

```java
@Test
public void givenUsingCommonsIO_whenConvertingReaderIntoByteArray_thenCorrect() 
  throws IOException {
    StringReader initialReader = new StringReader("With Commons IO");

    byte[] targetArray = IOUtils.toByteArray(initialReader);

    initialReader.close();
}
```

好了，这就是将JavaReader转换为字节数组的 3 种快速方法。