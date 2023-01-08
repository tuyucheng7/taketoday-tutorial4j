## 1. 概述

ByteBuffer是[java.nio](https://www.baeldung.com/java-nio-2-file-api)包中众多有用的类之一。它用于从通道中读取数据并直接将数据写入通道。

在这个简短的教程中，我们将学习如何在Java中将ByteBuffer转换为String。

## 2. 将ByteBuffer转换为String

将ByteBuffer转换为String的过程就是解码。这个过程需要一个Charset。

可以通过三种方式将ByteBuffer转换为String：

-   从bytebuffer.array()创建一个新的字符串
-   从bytebuffer.get(bytes)创建一个新的字符串
-   使用charset.decode()

我们将使用一个简单的示例来展示将ByteBuffer转换为String的所有三种方法。

## 3. 实例

### 3.1. 从bytebuffer.array()创建新字符串

第一步是从ByteBuffer获取字节数组。为此，我们将调用ByteBuffer.array()方法。这将返回支持数组。

然后，我们可以调用String构造函数，它接受一个字节数组和字符编码来创建我们的新String：

```java
@Test
public void convertUsingNewStringFromBufferArray_thenOK() {
    String content = "baeldung";
    ByteBuffer byteBuffer = ByteBuffer.wrap(content.getBytes());

    if (byteBuffer.hasArray()) {
        String newContent = new String(byteBuffer.array(), charset);

        assertEquals(content, newContent);
    }
}
```

### 3.2. 从bytebuffer.get(bytes)创建一个新字符串

在Java中，我们可以使用new String(bytes, charset)将byte[]转换为String。

对于字符数据，我们可以使用UTF_8 字符集将byte[]转换为String。但是，当byte[]保存非文本二进制数据时，最佳做法是将byte[]转换为[Base64 编码的](https://www.baeldung.com/java-base64-encode-and-decode) String：

```java
@Test
public void convertUsingNewStringFromByteBufferGetBytes_thenOK() {
    String content = "baeldung";
    ByteBuffer byteBuffer = ByteBuffer.wrap(content.getBytes());

    byte[] bytes = new byte[byteBuffer.remaining()];
    byteBuffer.get(bytes);
    String newContent = new String(bytes, charset);

    assertEquals(content, newContent);
}
```

### 3.3. 使用charset.decode()

这是将ByteBuffer转换为String的最简单方法，没有任何问题：

```java
@Test
public void convertUsingCharsetDecode_thenOK() {
    String content = "baeldung";
    ByteBuffer byteBuffer = ByteBuffer.wrap(content.getBytes());

    String newContent = charset.decode(byteBuffer).toString();

    assertEquals(content, newContent);
}

```

## 4。总结

我们在本教程中学习了三种在Java中将ByteBuffer转换为String的方法。请记住使用正确的[字符编码](https://www.baeldung.com/java-char-encoding)，在我们的示例中，我们使用了UTF-8。