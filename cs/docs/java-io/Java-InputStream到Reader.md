在本快速教程中，我们将了解使用Java将InputStream转换为Reader，然后是 Guava，最后是 Apache Commons IO。

本文是Baeldung 上[“Java– 回归基础”系列的一部分。](https://www.baeldung.com/java-tutorial)

## 1.用Java

首先，让我们看一下简单的Java解决方案——使用现成的InputStreamReader：

```java
@Test
public void givenUsingPlainJava_whenConvertingInputStreamIntoReader_thenCorrect() 
  throws IOException {
    InputStream initialStream = new ByteArrayInputStream("With Java".getBytes());
    
    Reader targetReader = new InputStreamReader(initialStream);

    targetReader.close();
}
```

## 2.用番石榴

接下来——让我们看看Guava 解决方案——使用中间字节数组和字符串：

```java
@Test
public void givenUsingGuava_whenConvertingInputStreamIntoReader_thenCorrect() 
  throws IOException {
    InputStream initialStream = ByteSource.wrap("With Guava".getBytes()).openStream();
    
    byte[] buffer = ByteStreams.toByteArray(initialStream);
    Reader targetReader = CharSource.wrap(new String(buffer)).openStream();

    targetReader.close();
}
```

请注意，Java 解决方案比这种方法更简单。

## 3.使用通用IO

最后——使用 Apache Commons IO 的解决方案——也使用中间字符串：

```java
@Test
public void givenUsingCommonsIO_whenConvertingInputStreamIntoReader_thenCorrect() 
  throws IOException {
    InputStream initialStream = IOUtils.toInputStream("With Commons IO");
    
    byte[] buffer = IOUtils.toByteArray(initialStream);
    Reader targetReader = new CharSequenceReader(new String(buffer));

    targetReader.close();
}
```

现在你已经掌握了 - 将输入流转换为JavaReader 的3 种快速方法。