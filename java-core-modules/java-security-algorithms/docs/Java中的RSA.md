## 1. 概述

RSA，或者换句话说，[Rivest–Shamir–Adleman](https://en.wikipedia.org/wiki/RSA_(cryptosystem))，是一种非对称加密算法。[它与DES](https://en.wikipedia.org/wiki/Data_Encryption_Standard)或[AES](https://www.baeldung.com/java-aes-encryption-decryption)等对称算法的不同之处在于它有两个密钥。我们可以与任何人共享的公钥用于加密数据。还有一个我们只为自己保留的私有文件，用于解密数据

在本教程中，我们将学习如何在Java中生成、存储和使用 RSA 密钥。

## 2.生成RSA密钥对

在开始实际加密之前，我们需要生成 RSA 密钥对。我们可以使用java.security包中的KeyPairGenerator轻松做到这一点：

```java
KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
generator.initialize(2048);
KeyPair pair = generator.generateKeyPair();
```

生成的密钥大小为 2048 位。

接下来，我们可以提取私钥和公钥：

```java
PrivateKey privateKey = pair.getPrivate();
PublicKey publicKey = pair.getPublic();
```

我们将使用公钥加密数据，使用私钥解密数据。

## 3. 在文件中存储密钥

将密钥对存储在内存中并不总是一个好的选择。大多数情况下，密钥会长期保持不变。在这种情况下，将它们存储在文件中会更方便。

要将密钥保存在文件中，我们可以使用getEncoded方法，它以其主要编码格式返回密钥内容：

```java
try (FileOutputStream fos = new FileOutputStream("public.key")) {
    fos.write(publicKey.getEncoded());
}
```

要从文件中读取密钥，我们首先需要将内容加载为字节数组：

```java
File publicKeyFile = new File("public.key");
byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
```

然后使用KeyFactory重新创建实际实例：

```java
KeyFactory keyFactory = KeyFactory.getInstance("RSA");
EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
keyFactory.generatePublic(publicKeySpec);
```

关键字节内容需要用EncodedKeySpec类包装。在这里，我们使用 X509EncodedKeySpec ，它表示我们用于保存文件的Key::getEncoded方法的默认算法。

在这个例子中，我们只保存和读取公钥文件。可以使用相同的步骤来处理私钥。

请记住，使用私钥尽可能安全地保存文件，并尽可能限制访问权限。未经授权的访问可能会带来安全问题。

## 4. 使用字符串

现在，让我们看看如何加密和解密简单的字符串。首先，我们需要一些数据来处理：

```java
String secretMessage = "Baeldung secret message";
```

其次，我们需要一个[Cipher](https://www.baeldung.com/java-cipher-class)对象初始化为使用我们之前生成的公钥加密：

```java
Cipher encryptCipher = Cipher.getInstance("RSA");
encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
```

准备就绪后，我们可以调用doFinal方法来加密我们的消息。请注意，它只接受字节数组参数，因此我们需要先转换我们的字符串：

```java
byte[] secretMessageBytes = secretMessage.getBytes(StandardCharsets.UTF_8);)
byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);
```

现在，我们的消息已成功编码。如果我们想将其存储在数据库中或通过[REST API发送，](https://www.baeldung.com/rest-with-spring-series)[使用 Base64 Alphabet 编码](https://www.baeldung.com/java-base64-encode-and-decode)会更方便：

```java
String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);
```

这样，消息将更具可读性和更易于使用。

现在，让我们看看如何将消息解密为其原始形式。为此，我们需要另一个Cipher实例。这次我们将使用解密模式和私钥对其进行初始化：

```java
Cipher decryptCipher = Cipher.getInstance("RSA");
decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
```

我们将像以前一样使用doFinal方法调用密码：

```java
byte[] decryptedMessageBytes = decryptCipher.doFinal(encryptedMessageBytes);
String decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);
```

最后，我们来验证加解密过程是否正确：

```java
assertEquals(secretMessage, decryptedMessage);
```

## 5. 使用文件

也可以加密整个文件。例如，让我们创建一个包含一些文本内容的临时文件：

```java
Path tempFile = Files.createTempFile("temp", "txt");
Files.writeString(tempFile, "some secret message");
```

在开始加密之前，我们需要将其内容转换为字节数组：

```java
byte[] fileBytes = Files.readAllBytes(tempFile);
```

现在，我们可以使用加密密码：

```java
Cipher encryptCipher = Cipher.getInstance("RSA");
encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
byte[] encryptedFileBytes = encryptCipher.doFinal(fileBytes);
```

最后，我们可以用新的加密内容覆盖它：

```java
try (FileOutputStream stream = new FileOutputStream(tempFile.toFile())) {
    stream.write(encryptedFileBytes);
}
```

解密过程看起来非常相似。唯一的区别是使用私钥在解密模式下初始化的密码：

```java
byte[] encryptedFileBytes = Files.readAllBytes(tempFile);
Cipher decryptCipher = Cipher.getInstance("RSA");
decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
byte[] decryptedFileBytes = decryptCipher.doFinal(encryptedFileBytes);
try (FileOutputStream stream = new FileOutputStream(tempFile.toFile())) {
    stream.write(decryptedFileBytes);
}
```

作为最后一步，我们可以验证文件内容是否与原始值匹配：

```java
String fileContent = Files.readString(tempFile);
Assertions.assertEquals("some secret message", fileContent);
```

## 6.总结

在本文中，我们学习了如何在Java中创建 RSA 密钥以及如何使用它们来加密和解密消息和文件。