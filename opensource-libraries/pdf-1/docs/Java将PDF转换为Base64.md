## 1. 概述

在这个简短的教程中，我们将了解如何使用Java8 和 Apache Commons Codec 对 PDF 文件进行 Base64 编码和解码。

但首先，让我们快速浏览一下 Base64 的基础知识。

## 2. Base64基础知识

通过网络发送数据时，我们需要以二进制格式发送。但是如果我们只发送[0 和 1](https://www.baeldung.com/cs/two-complement)，不同的传输层协议可能会以不同的方式解释它们，我们的数据可能会在传输过程中被破坏。

因此，为了在传输二进制数据时具有可移植性和通用标准，需要使用 Base64。

由于发送方和接收方都了解并同意使用该标准，因此我们的数据丢失或被误解的可能性大大降低。

现在让我们看看将其应用于 PDF 的几种方法。

## 3. 使用Java8 进行转换

从Java8 开始，我们有一个实用程序[java.util.Base64](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Base64.html)，它为 Base64 编码方案提供编码器和解码器。[它支持RFC 4648](https://www.ietf.org/rfc/rfc4648.txt)和[RFC 2045](https://www.ietf.org/rfc/rfc2045.txt)中指定的基本、URL 安全和 MIME 类型。

### 3.1. 编码

要将 PDF 转换为 Base64，我们首先需要以字节为单位获取它并通过java.util.Base64.Encoder的编码方法传递它：

```java
byte[] inFileBytes = Files.readAllBytes(Paths.get(IN_FILE)); 
byte[] encoded = java.util.Base64.getEncoder().encode(inFileBytes);
```

在这里，IN_FILE是我们输入 PDF 的路径。

### 3.2. 流媒体编码

对于较大的文件或内存有限的系统，使用流执行编码比读取内存中的所有数据效率更高。让我们看看如何实现这一点：

```java
try (OutputStream os = java.util.Base64.getEncoder().wrap(new FileOutputStream(OUT_FILE));
  FileInputStream fis = new FileInputStream(IN_FILE)) {
    byte[] bytes = new byte[1024];
    int read;
    while ((read = fis.read(bytes)) > -1) {
        os.write(bytes, 0, read);
    }
}
```

此处，IN_FILE是我们输入 PDF 的路径，OUT_FILE是包含 Base64 编码文档的文件的路径。我们不是将整个 PDF 读入内存然后在内存中对整个文档进行编码，而是一次读取多达 1Kb 的数据并将该数据通过编码器传递到OutputStream中。

### 3.3. 解码

在接收端，我们得到编码后的文件。

所以我们现在需要解码它以取回我们的原始字节并将它们写入FileOutputStream以获得解码的 PDF：

```java
byte[] decoded = java.util.Base64.getDecoder().decode(encoded);

FileOutputStream fos = new FileOutputStream(OUT_FILE);
fos.write(decoded);
fos.flush();
fos.close();
```

在这里，OUT_FILE是我们要创建的 PDF 的路径。

## 4. 使用 Apache Commons 进行转换

接下来，我们将使用 Apache Commons Codec 包来实现相同的目的。它基于[RFC 2045](https://www.ietf.org/rfc/rfc2045.txt)，早于我们之前讨论的Java8 实现。因此，当我们需要支持多个 JDK 版本(包括遗留版本)或供应商时，这作为第三方 API 就派上用场了。

### 4.1. 行家

为了能够使用 Apache 库，我们需要向我们的pom.xml添加依赖项：

```xml
<dependency>
    <groupId>commons-codec</groupId>
    <artifactId>commons-codec</artifactId>
    <version>1.14</version>
</dependency>

```

上面的最新版本可以在[Maven Central](https://search.maven.org/search?q=g:commons-codec)上找到。

### 4.2. 编码

步骤与Java8 相同，只是这次我们将原始字节传递给[org.apache.commons.codec.binary.Base64](https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/binary/Base64.html) 类的encodeBase64方法：

```java
byte[] inFileBytes = Files.readAllBytes(Paths.get(IN_FILE));
byte[] encoded = org.apache.commons.codec.binary.Base64.encodeBase64(inFileBytes);

```

### 4.3. 流媒体编码

此库不支持流编码。

### 4.4. 解码

同样，我们只需调用decodeBase64方法并将结果写入文件：

```java
byte[] decoded = org.apache.commons.codec.binary.Base64.decodeBase64(encoded);

FileOutputStream fos = new FileOutputStream(OUT_FILE);
fos.write(decoded);
fos.flush();
fos.close();

```

## 5. 测试

现在我们将使用一个简单的 JUnit 测试来测试我们的编码和解码：

```java
public class EncodeDecodeUnitTest {

    private static final String IN_FILE = // path to file to be encoded from;
    private static final String OUT_FILE = // path to file to be decoded into;
    private static byte[] inFileBytes;

    @BeforeClass
    public static void fileToByteArray() throws IOException {
        inFileBytes = Files.readAllBytes(Paths.get(IN_FILE));
    }

    @Test
    public void givenJavaBase64_whenEncoded_thenDecodedOK() throws IOException {
        byte[] encoded = java.util.Base64.getEncoder().encode(inFileBytes);
        byte[] decoded = java.util.Base64.getDecoder().decode(encoded);
        writeToFile(OUT_FILE, decoded);

        assertNotEquals(encoded.length, decoded.length);
        assertEquals(inFileBytes.length, decoded.length);
        assertArrayEquals(decoded, inFileBytes);
    }

    @Test
    public void givenJavaBase64_whenEncodedStream_thenDecodedStreamOK() throws IOException {
        try (OutputStream os = java.util.Base64.getEncoder().wrap(new FileOutputStream(OUT_FILE));
          FileInputStream fis = new FileInputStream(IN_FILE)) {
            byte[] bytes = new byte[1024];
            int read;
            while ((read = fis.read(bytes)) > -1) {
                os.write(bytes, 0, read);
            }
        }

        byte[] encoded = java.util.Base64.getEncoder().encode(inFileBytes);
        byte[] encodedOnDisk = Files.readAllBytes(Paths.get(OUT_FILE));
        assertArrayEquals(encoded, encodedOnDisk);

        byte[] decoded = java.util.Base64.getDecoder().decode(encoded);
        byte[] decodedOnDisk = java.util.Base64.getDecoder().decode(encodedOnDisk);
        assertArrayEquals(decoded, decodedOnDisk);
    }

    @Test
    public void givenApacheCommons_givenJavaBase64_whenEncoded_thenDecodedOK() throws IOException {
        byte[] encoded = org.apache.commons.codec.binary.Base64.encodeBase64(inFileBytes);
        byte[] decoded = org.apache.commons.codec.binary.Base64.decodeBase64(encoded);

        writeToFile(OUT_FILE, decoded);

        assertNotEquals(encoded.length, decoded.length);
        assertEquals(inFileBytes.length, decoded.length);

        assertArrayEquals(decoded, inFileBytes);
    }

    private void writeToFile(String fileName, byte[] bytes) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(bytes);
        fos.flush();
        fos.close();
    }
}
```

如我们所见，我们首先在@BeforeClass方法中读取输入字节，并在我们的两个@Test方法中验证：

-   编码和解码字节数组的长度不同
-   inFileBytes和解码后的字节数组长度相同，内容也相同

当然，我们也可以打开我们创建的解码后的 PDF 文件，看到内容与我们作为输入的文件相同。

## 六. 总结

在本快速教程中，我们了解了有关[Java 的 Base64 实用程序的](https://www.baeldung.com/java-base64-encode-and-decode)更多信息。

我们还看到了使用Java8 和 Apache Commons Codec 将 PDF 与 Base64 相互转换的代码示例。有趣的是，JDK 实现比 Apache 快得多。