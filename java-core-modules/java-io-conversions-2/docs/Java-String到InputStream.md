## 1. 概述

在本快速教程中，我们将了解如何使用纯 Java、Guava 和 Apache Commons IO 库将标准 String 转换为InputStream 。

本教程是Baeldung 上的[Java – 回归基础系列的一部分。](https://www.baeldung.com/java-tutorial)

## 2. 使用纯Java转换

让我们从一个使用Java进行转换的简单示例开始——使用一个中间字节数组：

```java
@Test
public void givenUsingPlainJava_whenConvertingStringToInputStream_thenCorrect() 
  throws IOException {
    String initialString = "text";
    InputStream targetStream = new ByteArrayInputStream(initialString.getBytes());
}
```

getBytes ()方法使用平台的默认字符集对该String进行编码，因此为避免不良行为，我们可以使用getBytes(Charset charset)并控制编码过程。

## 3. 用 Guava 转换

Guava 不提供直接转换方法，但允许我们从String中获取 CharSource并轻松将其转换为ByteSource。

然后很容易获得InputStream：

```java
@Test
public void givenUsingGuava_whenConvertingStringToInputStream_thenCorrect() 
  throws IOException {
    String initialString = "text";
    InputStream targetStream = 
      CharSource.wrap(initialString).asByteSource(StandardCharsets.UTF_8).openStream();
}
```

asByteSource方法实际上标记为@Beta。这意味着它可以在未来的 Guava 版本中删除。我们需要牢记这一点。

## 4. 使用 Commons IO 转换

最后，Apache Commons IO 库提供了一个优秀的直接解决方案：

```java
@Test
public void givenUsingCommonsIO_whenConvertingStringToInputStream_thenCorrect() 
  throws IOException {
    String initialString = "text";
    InputStream targetStream = IOUtils.toInputStream(initialString);
}
```

请注意，我们在这些示例中让输入流保持打开状态，所以不要忘记关闭它。

## 5.总结

在本文中，我们介绍了三种从简单字符串中获取InputStream 的简单明了的方法。