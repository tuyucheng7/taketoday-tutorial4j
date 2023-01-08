## 1. 概述

在本教程中，我们探索了在Java中提供 Base64 编码和解码功能的各种实用程序。

我们将主要说明新的Java8 API。此外，我们还使用 Apache Commons 的实用程序 API。

## 延伸阅读：

## [Java URL 编码/解码指南](https://www.baeldung.com/java-url-encoding-decoding)

本文讨论了Java中的 URL 编码、一些陷阱以及如何避免它们。

[阅读更多](https://www.baeldung.com/java-url-encoding-decoding)→

## [Java 中的 SHA-256 和 SHA3-256 哈希](https://www.baeldung.com/sha-256-hashing-java)

Java 中 SHA-256 散列的快速实用指南

[阅读更多](https://www.baeldung.com/sha-256-hashing-java)→

## [Spring Security 5 中的新密码存储](https://www.baeldung.com/spring-security-5-password-storage)

了解 Spring Security 5 中的密码加密和迁移到更好的加密算法的快速指南。

[阅读更多](https://www.baeldung.com/spring-security-5-password-storage)→

## 2. 用于 Base 64 的Java8

Java 8 终于通过java.util.Base64实用程序类向标准 API添加了 Base64 功能。

让我们从一个基本的编码器过程开始。

### 2.1.Java8 基础 Base64

基本编码器使事情变得简单，并按原样对输入进行编码，没有任何行分隔。

编码器将输入映射到A-Za-z0-9+/字符集中的一组字符。

让我们首先编码一个简单的String：

```java
String originalInput = "test input";
String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());

```

请注意我们如何通过简单的getEncoder()实用程序方法检索完整的编码器 API。

现在让我们将该字符串解码回原始形式：

```java
byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
String decodedString = new String(decodedBytes);
```

### 2.2. 没有填充的Java8 Base64 编码

在 Base64 编码中，输出编码的String的长度必须是三的倍数。编码器根据需要在输出的末尾添加一个或两个填充字符(=)以满足此要求。

解码时，解码器会丢弃这些额外的填充字符。要深入了解 Base64 中的填充，请查看[Stack Overflow 上的详细答案](https://stackoverflow.com/a/18518605/370481)。

有时，我们需要跳过输出的填充。例如，生成的String永远不会被解码回来。因此，我们可以简单地选择不使用填充进行编码：

```java
String encodedString = 
  Base64.getEncoder().withoutPadding().encodeToString(originalInput.getBytes());
```

### 2.3.Java8 网址编码

URL 编码与基本编码器非常相似。此外，它使用 URL 和文件名安全的 Base64 字母表。此外，它不添加任何行分隔：

```java
String originalUrl = "https://www.google.co.nz/?gfe_rd=cr&ei=dzbFV&gws_rd=ssl#q=java";
String encodedUrl = Base64.getUrlEncoder().encodeToString(originalURL.getBytes());

```

解码以几乎相同的方式发生。getUrlDecoder ()实用程序方法返回java.util.Base64.Decoder。所以，我们用它来解码 URL：

```java
byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedUrl);
String decodedUrl = new String(decodedBytes);

```

### 2.4.Java8 MIME 编码

让我们从生成一些基本的 MIME 输入开始编码：

```java
private static StringBuilder getMimeBuffer() {
    StringBuilder buffer = new StringBuilder();
    for (int count = 0; count < 10; ++count) {
        buffer.append(UUID.randomUUID().toString());
    }
    return buffer;
}
```

MIME 编码器使用基本字母表生成 Base64 编码的输出。但是，该格式是 MIME 友好的。

每行输出不超过 76 个字符。此外，它以回车符和换行符 ( rn ) 结尾：

```java
StringBuilder buffer = getMimeBuffer();
byte[] encodedAsBytes = buffer.toString().getBytes();
String encodedMime = Base64.getMimeEncoder().encodeToString(encodedAsBytes);
```

在解码过程中，我们可以使用返回java.util.Base64.Decoder的getMimeDecoder()方法：

```java
byte[] decodedBytes = Base64.getMimeDecoder().decode(encodedMime);
String decodedMime = new String(decodedBytes);

```

## 3. 使用 Apache Commons 代码编码/解码

首先，我们需要在pom.xml中定义[commons-codec](https://search.maven.org/classic/#search|gav|1|g%3A"commons-codec" AND a%3A"commons-codec")依赖：

```java
<dependency>
    <groupId>commons-codec</groupId>
    <artifactId>commons-codec</artifactId>
    <version>1.15</version>
</dependency>
```

主要的 API 是org.apache.commons.codec.binary.Base64类。我们可以使用各种构造函数对其进行初始化：

-   Base64(boolean urlSafe)通过控制 URL 安全模式(打开或关闭)来创建 Base64 API。
-   Base64(int lineLength)在 URL 不安全模式下创建 Base64 API 并控制行的长度(默认为 76)。
-   Base64(int lineLength, byte[] lineSeparator)通过接受额外的行分隔符创建 Base64 API，默认情况下为 CRLF(“rn”)。

创建 Base64 API 后，编码和解码都非常简单：

```java
String originalInput = "test input";
Base64 base64 = new Base64();
String encodedString = new String(base64.encode(originalInput.getBytes()));

```

此外，Base64类的decode()方法返回解码后的字符串：

```java
String decodedString = new String(base64.decode(encodedString.getBytes()));

```

另一种选择是使用Base64 的静态 API而不是创建实例：

```java
String originalInput = "test input";
String encodedString = new String(Base64.encodeBase64(originalInput.getBytes()));
String decodedString = new String(Base64.decodeBase64(encodedString.getBytes()));
```

## 4. 将 字符串转换为 字节数组

有时，我们需要将String转换为 byte[]。最简单的方法是使用String getBytes()方法：

```java
String originalInput = "test input";
byte[] result = originalInput.getBytes();

assertEquals(originalInput.length(), result.length);
```

我们也可以提供编码而不依赖于默认编码。因此，它依赖于系统：

```java
String originalInput = "test input";
byte[] result = originalInput.getBytes(StandardCharsets.UTF_16);

assertTrue(originalInput.length() < result.length);
```

如果我们的字符串是Base64编码的，我们可以使用Base64解码器：

```java
String originalInput = "dGVzdCBpbnB1dA==";
byte[] result = Base64.getDecoder().decode(originalInput);

assertEquals("test input", new String(result));
```

我们还可以使用DatatypeConverter 的 parseBase64Binary()方法：

```java
String originalInput = "dGVzdCBpbnB1dA==";
byte[] result = DatatypeConverter.parseBase64Binary(originalInput);

assertEquals("test input", new String(result));
```

最后，我们可以使用DatatypeConverter.parseHexBinary方法将十六进制字符串转换为byte[]：

```java
String originalInput = "7465737420696E707574";
byte[] result = DatatypeConverter.parseHexBinary(originalInput);

assertEquals("test input", new String(result));
```

## 5.总结

本文介绍了如何在Java中进行 Base64 编码和解码的基础知识。我们使用了Java8 和 Apache Commons 中引入的新 API。

最后，还有一些提供类似功能的其他 API：带有printHexBinary和parseBase64Binary的java.xml.bind.DataTypeConverter。