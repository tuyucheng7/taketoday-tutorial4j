## 1. 概述

3DES 或[三重数据加密算法](https://en.wikipedia.org/wiki/Triple_DES)是一种对称密钥块密码，它对每个数据块应用 DES 密码算法三次。

在本教程中，我们将学习如何创建 3DES 密钥并使用它们在Java中加密和解密String和文件。

## 2. 生成秘钥

生成 3DES 密钥需要几个步骤。首先，我们需要生成一个用于加密解密过程的密钥。在我们的例子中，我们将使用由随机数字和字母构成的 24 字节密钥：

```java
byte[] secretKey = "9mng65v8jf4lxn93nabf981m".getBytes();
```

请注意，不应公开共享密钥。

现在，我们将把我们的密钥包装在 SecretKeySpec 中，并将它与选定的算法相结合：

```java
SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "TripleDES");
```

在我们的例子中，我们使用TripleDES ，它是[Java 安全标准算法](https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#algorithmparameters-algorithms)之一。

我们应该提前生成的另一项是我们的密钥的[初始化向量](https://en.wikipedia.org/wiki/Initialization_vector)。我们将使用一个 8 字节的随机数字和字母数组：

```java
byte[] iv = "a76nb5h9".getBytes();
```

然后，我们将其包装在IvParameterSpec类中：

```java
IvParameterSpec ivSpec = new IvParameterSpec(iv);
```

## 3.加密String

我们现在已准备好加密简单的字符串 值。让我们首先定义一个我们将使用的字符串：

```java
String secretMessage = "Baeldung secret message";
```

接下来，我们需要一个用加密模式、密钥和我们之前生成的初始化向量初始化的[Cipher对象：](https://www.baeldung.com/java-cipher-class)

```java
Cipher encryptCipher = Cipher.getInstance("TripleDES/CBC/PKCS5Padding");
encryptCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
```

请注意，我们将TripleDES算法与[CBC](https://en.wikipedia.org/wiki/Block_cipher_mode_of_operation)和[PKCS#5 填充方案](https://en.wikipedia.org/wiki/Padding_(cryptography))一起使用。

使用Cipher，我们可以运行doFinal方法来加密我们的消息。请注意，它仅适用于字节数组，因此我们需要先转换我们的字符串：

```java
byte[] secretMessagesBytes = secretMessage.getBytes(StandardCharsets.UTF_8);
byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessagesBytes);
```

现在，我们的消息已成功加密。如果我们想将它存储在数据库中或通过[REST API发送它，](https://www.baeldung.com/rest-with-spring-series)[使用 Base64 字母表对其进行编码](https://www.baeldung.com/java-base64-encode-and-decode)会更方便：

```java
String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);
```

Base64 编码使消息更具可读性和更易于使用。

## 4. 解密 字符串

现在，让我们看看如何逆向加密过程并将消息解密为原始形式。为此，我们需要一个新的Cipher实例，但这一次，我们将在解密模式下对其进行初始化：

```java
Cipher decryptCipher = Cipher.getInstance("TripleDES/CBC/PKCS5Padding");
decryptCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
```

接下来，我们将运行doFinal方法：

```java
byte[] decryptedMessageBytes = decryptCipher.doFinal(encryptedMessageBytes);
```

现在，我们将结果解码为String变量：

```java
String decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);
```

最后，我们可以通过将结果与初始值进行比较来验证结果以确保解密过程正确执行：

```java
Assertions.assertEquals(secretMessage, decryptedMessage);
```

## 5. 使用文件

我们也可以加密整个文件。例如，让我们创建一个包含一些文本内容的临时文件：

```java
String originalContent = "Secret Baeldung message";
Path tempFile = Files.createTempFile("temp", "txt");
writeString(tempFile, originalContent);
```

接下来，让我们将其内容转换为单字节数组：

```java
byte[] fileBytes = Files.readAllBytes(tempFile);
```

现在，我们可以像使用String一样使用加密密码：

```java
Cipher encryptCipher = Cipher.getInstance("TripleDES/CBC/PKCS5Padding");
encryptCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
byte[] encryptedFileBytes = encryptCipher.doFinal(fileBytes);
```

最后，让我们用新的加密数据覆盖文件内容：

```java
try (FileOutputStream stream = new FileOutputStream(tempFile.toFile())) {
    stream.write(encryptedFileBytes);
}
```

解密过程看起来非常相似。唯一的区别是在解密模式下初始化的密码：

```java
encryptedFileBytes = Files.readAllBytes(tempFile);
Cipher decryptCipher = Cipher.getInstance("TripleDES/CBC/PKCS5Padding");
decryptCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
byte[] decryptedFileBytes = decryptCipher.doFinal(encryptedFileBytes);
```

再一次，让我们覆盖文件内容——这次，用解密的数据：

```java
try (FileOutputStream stream = new FileOutputStream(tempFile.toFile())) {
    stream.write(decryptedFileBytes);
}
```

作为最后一步，我们可以验证文件内容是否与原始值匹配：

```java
String fileContent = readString(tempFile);
Assertions.assertEquals(originalContent, fileContent);
```

## 6.总结

在本文中，我们学习了如何在Java中创建 3DES 密钥以及如何使用它来加密和解密String和文件。