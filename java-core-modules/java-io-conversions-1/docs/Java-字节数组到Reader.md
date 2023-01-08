在本快速教程中，我们将使用纯 Java、Guava 和 Apache Commons IO 库将简单的字节数组转换为Reader。

本文是Baeldung 上[“Java – 回归基础”系列的一部分。](https://www.baeldung.com/java-tutorial)

## 1. 使用纯 Java

让我们从简单的Java示例开始，通过中间字符串进行转换：

```java
@Test
public void givenUsingPlainJava_whenConvertingByteArrayIntoReader_thenCorrect() 
  throws IOException {
    byte[] initialArray = "With Java".getBytes();
    Reader targetReader = new StringReader(new String(initialArray));
    targetReader.close();
}
```

另一种方法是使用InputStreamReader和ByteArrayInputStream：

```java
@Test
public void givenUsingPlainJava2_whenConvertingByteArrayIntoReader_thenCorrect() 
  throws IOException {
    byte[] initialArray = "Hello world!".getBytes();
    Reader targetReader = new InputStreamReader(new ByteArrayInputStream(initialArray));
    targetReader.close();
}
```

## 2.用番石榴

接下来——让我们看看 Guava 解决方案，同样使用中间字符串：

```java
@Test
public void givenUsingGuava_whenConvertingByteArrayIntoReader_thenCorrect() 
  throws IOException {
    byte[] initialArray = "With Guava".getBytes();
    String bufferString = new String(initialArray);
    Reader targetReader = CharSource.wrap(bufferString).openStream();
    targetReader.close();
}
```

不幸的是，Guava ByteSource实用程序不允许直接转换，因此我们仍然需要使用中间字符串表示形式。

## 3.使用Apache Commons IO

类似地——Commons IO 也使用中间字符串表示形式将byte[]转换为 Reader：

```java
@Test
public void givenUsingCommonsIO_whenConvertingByteArrayIntoReader_thenCorrect() 
  throws IOException {
    byte[] initialArray = "With Commons IO".getBytes();
    Reader targetReader = new CharSequenceReader(new String(initialArray));
    targetReader.close();
}
```

我们已经有了——将字节数组转换为JavaReader 的3 种简单方法。