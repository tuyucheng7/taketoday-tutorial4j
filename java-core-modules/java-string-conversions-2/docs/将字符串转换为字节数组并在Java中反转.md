## 1. 概述

 在Java中，我们经常需要在String和byte数组之间进行转换。在本教程中，我们将详细研究这些操作。

## 延伸阅读：

## [Java InputStream 到字节数组和 ByteBuffer](https://www.baeldung.com/convert-input-stream-to-array-of-bytes)

如何使用纯 Java、Guava 或 Commons IO 将 InputStream 转换为 byte[]。

[阅读更多](https://www.baeldung.com/convert-input-stream-to-array-of-bytes)→

## [Java – 读取字节数组](https://www.baeldung.com/java-convert-reader-to-byte-array)

如何使用纯 Java、Guava 或 Apache Commons IO 库将 Reader 转换为 byte[]。

[阅读更多](https://www.baeldung.com/java-convert-reader-to-byte-array)→

## [Java 字节数组到 InputStream](https://www.baeldung.com/convert-byte-array-to-input-stream)

如何使用纯Java或 Guava 将 byte[] 转换为 InputStream。

[阅读更多](https://www.baeldung.com/convert-byte-array-to-input-stream)→

首先，我们将了解将String转换为字节数组的各种方法。然后我们将反向查看类似的操作。

## 2. 将字符串转换为字节数组

字符串 在Java中存储为 Unicode 字符数组。要将其转换为 字节数组，我们将字符序列转换为字节序列。对于此翻译，我们使用[Charset](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/nio/charset/Charset.html)的一个实例。此类指定char序列和byte序列之间的映射。

我们将上述过程称为编码。

在Java中，我们可以通过多种方式将String编码为字节数组。让我们通过示例详细了解它们中的每一个。

### 2.1. 使用 String.getBytes()

String类提供了三个重载 的getBytes方法来将String编码为字节数组：

-   [getBytes()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#getBytes(java.nio.charset.Charset)) – 使用平台的默认字符集进行编码
-   [getBytes (String charsetName) ](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#getBytes(java.lang.String))– 使用指定的字符集进行编码
-   [getBytes (Charset charset)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#getBytes(java.nio.charset.Charset)) – 使用提供的字符集进行编码

首先，让我们使用平台的默认字符集对字符串进行编码：

```java
String inputString = "Hello World!";
byte[] byteArrray = inputString.getBytes();
```

上述方法是平台相关的，因为它使用平台的默认字符集。我们可以通过调用Charset.defaultCharset()来获取这个字符集。

然后让我们使用命名字符集对字符串进行编码：

```java
@Test
public void whenGetBytesWithNamedCharset_thenOK() 
  throws UnsupportedEncodingException {
    String inputString = "Hello World!";
    String charsetName = "IBM01140";

    byte[] byteArrray = inputString.getBytes("IBM01140");
    
    assertArrayEquals(
      new byte[] { -56, -123, -109, -109, -106, 64, -26,
        -106, -103, -109, -124, 90 },
      byteArrray);
}
```

如果不支持指定的字符集，此方法将抛出UnsupportedEncodingException 。

如果输入包含字符集不支持的字符，则上述两个版本的行为是未定义的。相反，第三个版本使用字符集的默认替换字节数组来编码不受支持的输入。

接下来，让我们调用getBytes()方法的第三个版本，并传递一个Charset 实例：

```java
@Test
public void whenGetBytesWithCharset_thenOK() {
    String inputString = "Hello ਸੰਸਾਰ!";
    Charset charset = Charset.forName("ASCII");

    byte[] byteArrray = inputString.getBytes(charset);

    assertArrayEquals(
      new byte[] { 72, 101, 108, 108, 111, 32, 63, 63, 63,
        63, 63, 33 },
      byteArrray);
}
```

这里我们使用工厂方法[Charset.forName](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/nio/charset/Charset.html#forName(java.lang.String))来获取Charset的实例。如果请求的字符集名称无效，此方法将抛出运行时异常。如果当前 JVM 支持该字符集，它还会抛出运行时异常。

但是，某些字符集保证在每个Java平台上都可用。[StandardCharsets](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/charset/StandardCharsets.html)类为这些字符集定义常量。

最后，让我们使用一种标准字符集进行编码：

```java
@Test
public void whenGetBytesWithStandardCharset_thenOK() {
    String inputString = "Hello World!";
    Charset charset = StandardCharsets.UTF_16;

    byte[] byteArrray = inputString.getBytes(charset);

    assertArrayEquals(
      new byte[] { -2, -1, 0, 72, 0, 101, 0, 108, 0, 108, 0,
        111, 0, 32, 0, 87, 0, 111, 0, 114, 0, 108, 0, 100, 0, 33 },
      byteArrray);
}
```

至此，我们完成了各种getBytes版本的审核。接下来我们看看Charset 本身提供的方法。

### 2.2. 使用 Charset.encode()

Charset类提供了 encode() ，这是一种将 Unicode 字符编码为字节的便捷方法。此方法始终使用字符集的默认替换字节数组替换无效输入和不可映射字符。

让我们使用encode方法将String转换为字节数组：

```java
@Test
public void whenEncodeWithCharset_thenOK() {
    String inputString = "Hello ਸੰਸਾਰ!";
    Charset charset = StandardCharsets.US_ASCII;

    byte[] byteArrray = charset.encode(inputString).array();

    assertArrayEquals(
      new byte[] { 72, 101, 108, 108, 111, 32, 63, 63, 63, 63, 63, 33 },
      byteArrray);
}
```

正如我们在上面看到的，不受支持的字符已被字符集的默认替换 字节63 替换。

到目前为止，我们使用的方法在内部使用[CharsetEncoder](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/charset/CharsetEncoder.html)类来执行编码。让我们在下一节中检查这个类。

### 2.3. 字符集编码器

CharsetEncoder将 Unicode 字符转换为给定字符集的字节序列。此外，它还提供了对编码过程的细粒度控制。

让我们使用此类将String转换为字节数组：

```java
@Test
public void whenUsingCharsetEncoder_thenOK()
  throws CharacterCodingException {
    String inputString = "Hello ਸੰਸਾਰ!";
    CharsetEncoder encoder = StandardCharsets.US_ASCII.newEncoder();
    encoder.onMalformedInput(CodingErrorAction.IGNORE)
      .onUnmappableCharacter(CodingErrorAction.REPLACE)
      .replaceWith(new byte[] { 0 });

    byte[] byteArrray = encoder.encode(CharBuffer.wrap(inputString))
                          .array();

    assertArrayEquals(
      new byte[] { 72, 101, 108, 108, 111, 32, 0, 0, 0, 0, 0, 33 },
      byteArrray);
}
```

在这里，我们通过调用Charset对象的newEncoder 方法来创建CharsetEncoder的实例。

然后我们通过调用onMalformedInput()和onUnmappableCharacter() 方法为错误条件指定操作。 我们可以指定以下操作：

-   忽略——丢弃错误的输入
-   REPLACE——替换错误的输入
-   [REPORT——通过返回CoderResult](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/charset/CoderResult.html)对象或抛出[CharacterCodingException](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/nio/charset/CharacterCodingException.html)来报告错误

此外，我们使用replaceWith()方法来指定替换字节数组。

因此，我们已经完成了对将 String 转换为字节数组的各种方法的回顾。接下来我们看一下反向操作。

## 3. 将字节数组转换为字符串

我们将字节数组转换为字符串的过程称为解码。与编码类似，此过程需要一个Charset。

但是，我们不能只使用任何字符集来解码字节数组。特别是，我们应该使用将String编码 为字节数组的字符集。

我们还可以通过多种方式将字节数组转换为字符串。让我们详细检查它们中的每一个。

### 3.1. 使用字符串构造函数

String类有几个构造函数，它们将字节数组作为输入。它们都类似于getBytes方法，但工作方式相反。

因此，让我们使用平台的默认字符集将字节数组转换为字符串：

```java
@Test
public void whenStringConstructorWithDefaultCharset_thenOK() {
    byte[] byteArrray = { 72, 101, 108, 108, 111, 32, 87, 111, 114,
      108, 100, 33 };
    
    String string = new String(byteArrray);
    
    assertNotNull(string);
}
```

请注意，我们在这里没有断言有关解码字符串内容的任何内容。这是因为它可能会解码为不同的内容，具体取决于平台的默认字符集。

为此，我们一般应该避免使用这种方法。

然后让我们使用命名字符集进行解码：

```java
@Test
public void whenStringConstructorWithNamedCharset_thenOK()
    throws UnsupportedEncodingException {
    String charsetName = "IBM01140";
    byte[] byteArrray = { -56, -123, -109, -109, -106, 64, -26, -106,
      -103, -109, -124, 90 };

    String string = new String(byteArrray, charsetName);
        
    assertEquals("Hello World!", string);
}
```

如果指定的字符集在 JVM 上不可用，则此方法会抛出异常。

接下来，让我们使用一个Charset对象来进行解码：

```java
@Test
public void whenStringConstructorWithCharSet_thenOK() {
    Charset charset = Charset.forName("UTF-8");
    byte[] byteArrray = { 72, 101, 108, 108, 111, 32, 87, 111, 114,
      108, 100, 33 };

    String string = new String(byteArrray, charset);

    assertEquals("Hello World!", string);
}
```

最后，让我们使用一个标准的字符集：

```java
@Test
public void whenStringConstructorWithStandardCharSet_thenOK() {
    Charset charset = StandardCharsets.UTF_16;
        
    byte[] byteArrray = { -2, -1, 0, 72, 0, 101, 0, 108, 0, 108, 0,
      111, 0, 32, 0, 87, 0, 111, 0, 114, 0, 108, 0, 100, 0, 33 };

    String string = new String(byteArrray, charset);

    assertEquals("Hello World!", string);
}
```

到目前为止，我们已经使用构造函数将字节数组转换为字符串，现在我们将研究其他方法。

### 3.2. 使用 Charset.decode()

Charset类提供了将ByteBuffer转换为String的decode()方法：

```java
@Test
public void whenDecodeWithCharset_thenOK() {
    byte[] byteArrray = { 72, 101, 108, 108, 111, 32, -10, 111,
      114, 108, -63, 33 };
    Charset charset = StandardCharsets.US_ASCII;
    String string = charset.decode(ByteBuffer.wrap(byteArrray))
                      .toString();

    assertEquals("Hello �orl�!", string);
}
```

此处，无效输入被字符集的默认替换字符替换。

### 3.3. 字符集解码器

请注意，所有以前的内部解码方法都使用[CharsetDecoder](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/charset/CharsetDecoder.html) 类。我们可以直接使用此类对解码过程进行细粒度控制：

```java
@Test
public void whenUsingCharsetDecoder_thenOK()
  throws CharacterCodingException {
    byte[] byteArrray = { 72, 101, 108, 108, 111, 32, -10, 111, 114,
      108, -63, 33 };
    CharsetDecoder decoder = StandardCharsets.US_ASCII.newDecoder();

    decoder.onMalformedInput(CodingErrorAction.REPLACE)
      .onUnmappableCharacter(CodingErrorAction.REPLACE)
      .replaceWith("?");

    String string = decoder.decode(ByteBuffer.wrap(byteArrray))
                      .toString();

    assertEquals("Hello ?orl?!", string);
}
```

在这里，我们将无效输入和不支持的字符替换为“?”。

如果我们想在输入无效的情况下得到通知，我们可以更改解码器：

```java
decoder.onMalformedInput(CodingErrorAction.REPORT)
  .onUnmappableCharacter(CodingErrorAction.REPORT)
```

## 4。总结

在本文中，我们研究了将String转换为字节数组的多种方法，反之亦然。我们应该根据输入数据以及无效输入所需的控制级别来选择合适的方法。