## 1. 概述

在Java中处理String时，我们有时需要将它们编码为特定的字符集。

## 延伸阅读：

## [字符编码指南](https://www.baeldung.com/java-char-encoding)

探索Java中的字符编码并了解常见的陷阱。

[阅读更多](https://www.baeldung.com/java-char-encoding)→

## [Java URL 编码/解码指南](https://www.baeldung.com/java-url-encoding-decoding)

本文讨论了Java中的 URL 编码、一些陷阱以及如何避免它们。

[阅读更多](https://www.baeldung.com/java-url-encoding-decoding)→

## [Java Base64编码与解码](https://www.baeldung.com/java-base64-encode-and-decode)

如何使用Java8 中引入的新 API 以及 Apache Commons 在Java中进行 Base64 编码和解码。

[阅读更多](https://www.baeldung.com/java-base64-encode-and-decode)→

本教程是一个实用指南，展示了将字符串编码为UTF-8 字符集的不同方法。

如需更深入的技术研究，请参阅我们[的字符编码指南](https://www.baeldung.com/java-char-encoding)。

## 2. 定义问题

为了展示Java编码，我们将使用德语字符串“Entwickeln Sie mit Vergnügen”：

```java
String germanString = "Entwickeln Sie mit Vergnügen";
byte[] germanBytes = germanString.getBytes();

String asciiEncodedString = new String(germanBytes, StandardCharsets.US_ASCII);

assertNotEquals(asciiEncodedString, germanString);
```

这个使用 US_ASCII 编码的字符串在打印时为我们提供了值“Entwickeln Sie mit Vergn?gen”，因为它不理解非 ASCII ü 字符。

但是当我们将使用所有英文字符的 ASCII 编码字符串转换为 UTF-8 时，我们得到相同的字符串：

```java
String englishString = "Develop with pleasure";
byte[] englishBytes = englishString.getBytes();

String asciiEncondedEnglishString = new String(englishBytes, StandardCharsets.US_ASCII);

assertEquals(asciiEncondedEnglishString, englishString);
```

让我们看看当我们使用 UTF-8 编码时会发生什么。

## 3. 使用 CoreJava编码

让我们从核心库开始。

String在Java中是不可变的，这意味着我们无法更改String字符编码。为了实现我们想要的，我们需要字符串的字节，然后创建一个具有所需编码的新字节。

首先，我们获取String字节，然后使用检索到的字节和所需的字符集创建一个新字节：

```java
String rawString = "Entwickeln Sie mit Vergnügen";
byte[] bytes = rawString.getBytes(StandardCharsets.UTF_8);

String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8);

assertEquals(rawString, utf8EncodedString);
```

## 4. 使用Java7 StandardCharsets编码

或者，我们可以使用Java 7中引入的StandardCharsets类来对String进行编码。

首先，我们将字符串解码为字节，其次，我们将字符串编码为 UTF-8：

```java
String rawString = "Entwickeln Sie mit Vergnügen";
ByteBuffer buffer = StandardCharsets.UTF_8.encode(rawString); 

String utf8EncodedString = StandardCharsets.UTF_8.decode(buffer).toString();

assertEquals(rawString, utf8EncodedString);
```

## 5. 使用 Commons-Codec 编码

除了使用核心 Java，我们还可以使用[Apache Commons Codec](https://commons.apache.org/proper/commons-codec/) 来达到相同的效果。

Apache Commons Codec 是一个方便的包，包含用于各种格式的简单编码器和解码器。

首先，让我们从项目配置开始。

使用 Maven 时，我们必须将[commons -codec依赖](https://search.maven.org/search?q=g:commons-codec AND a:commons-codec)项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>commons-codec</groupId>
    <artifactId>commons-codec</artifactId>
    <version>1.14</version>
</dependency>
```

然后，在我们的例子中，最有趣的类是[StringUtils](https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/binary/StringUtils.html) ，它提供了对String进行编码的方法。

使用此类，获取 UTF-8 编码的字符串非常简单：

```java
String rawString = "Entwickeln Sie mit Vergnügen"; 
byte[] bytes = StringUtils.getBytesUtf8(rawString);
 
String utf8EncodedString = StringUtils.newStringUtf8(bytes);

assertEquals(rawString, utf8EncodedString);
```

## 六，总结

将字符串编码为 UTF-8 并不困难，但不是那么直观。本文介绍了三种方法，使用核心Java或 Apache Commons Codec。