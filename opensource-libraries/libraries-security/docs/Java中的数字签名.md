## 1. 概述

在本教程中，我们将了解数字签名机制以及如何使用[Java 加密体系结构 (JCA)](https://docs.oracle.com/en/java/javase/11/security/java-cryptography-architecture-jca-reference-guide.html)实现它。我们将探讨KeyPair、MessageDigest、Cipher、KeyStore、Certificate和Signature JCA API。

我们将从了解什么是数字签名、如何生成密钥对以及如何从证书颁发机构 (CA) 认证公钥开始。之后，我们将了解如何使用低级和高级 JCA API 实现数字签名。

## 2. 什么是数字签名？

### 2.1. 数字签名定义

数字签名是一种确保：

-   完整性：消息在传输过程中未被更改
-   真实性：消息的作者确实是他们声称的那个人
-   不可否认性：消息的作者以后不能否认他们是消息来源

### 2.2. 发送带有数字签名的消息

从技术上讲，数字 签名是消息的加密哈希(摘要、校验和)。这意味着我们从消息中生成哈希值，并根据选定的算法使用私钥对其进行加密。

然后发送消息、加密哈希、相应的公钥和算法。这被归类为带有数字签名的消息。

### 2.3. 接收和检查数字签名

为了检查数字签名，消息接收方从接收到的消息中生成一个新的散列，使用公钥对接收到的加密散列进行解密，并进行比较。如果它们匹配，则称数字签名已通过验证。

我们应该注意，我们只加密消息哈希，而不是消息本身。换句话说，数字签名不会尝试对消息保密。我们的数字签名仅证明消息在传输过程中未被更改。

签名通过验证后，我们可以确定只有私钥的所有者才能成为消息的作者。

## 3. 数字证书和公钥身份

证书是将身份与给定公钥相关联的文档。 证书由称为证书颁发机构 (CA) 的第三方实体签名。

我们知道，如果我们用公开的公钥解密的散列值与实际散列值匹配，那么消息就被签名了。但是，我们怎么知道公钥真的来自正确的实体呢？这是通过使用数字证书来解决的。

数字证书包含一个公钥，并且本身由另一个实体签名。该实体的签名本身可以由另一个实体等进行验证。我们最终得到了我们所说的证书链。每个顶级实体证明下一个实体的公钥。最顶层实体是自签名的，也就是说他的公钥是用他自己的私钥签名的。

X.509 是最常用的证书格式，它以二进制格式 (DER) 或文本格式 (PEM) 提供。JCA 已经通过X509Certificate类为此提供了一个实现。

## 4.KeyPair管理

由于数字签名使用私钥和公钥，我们将使用 JCA 类PrivateKey和PublicKey分别对消息进行签名和检查。

### 4.1. 获取密钥对

要创建私钥和公钥的密钥对，我们将使用Java[keytool](https://docs.oracle.com/en/java/javase/11/tools/keytool.html)。

让我们使用genkeypair命令生成密钥对：

```bash
keytool -genkeypair -alias senderKeyPair -keyalg RSA -keysize 2048 
  -dname "CN=Baeldung" -validity 365 -storetype PKCS12 
  -keystore sender_keystore.p12 -storepass changeit
```

这为我们创建了一个私钥及其对应的公钥。公钥被包装到一个 X.509 自签名证书中，该证书又被包装成一个单元素证书链。我们将证书链和私钥存储在密钥库文件sender_keystore.p12中，我们可以使用[KeyStore API](https://www.baeldung.com/java-keystore)对其进行处理。

在这里，我们使用了 PKCS12 密钥存储格式，因为它是Java专有的 JKS 格式的标准和推荐格式。此外，我们应该记住密码和别名，因为我们将在下一小节加载密钥库文件时使用它们。

### 4.2. 加载签名私钥

为了签署消息，我们需要一个PrivateKey 的实例。

使用KeyStore API，和之前的 Keystore 文件，sender_keystore.p12，我们可以得到一个PrivateKey对象：

```java
KeyStore keyStore = KeyStore.getInstance("PKCS12");
keyStore.load(new FileInputStream("sender_keystore.p12"), "changeit");
PrivateKey privateKey = 
  (PrivateKey) keyStore.getKey("senderKeyPair", "changeit");
```

### 4.3. 发布公钥

在我们可以发布公钥之前，我们必须首先决定我们是要使用自签名证书还是 CA 签名证书。

当使用自签名证书时，我们只需要将其从 Keystore 文件中导出即可。我们可以使用exportcert命令来做到这一点：

```bash
keytool -exportcert -alias senderKeyPair -storetype PKCS12 
  -keystore sender_keystore.p12 -file 
  sender_certificate.cer -rfc -storepass changeit
```

否则，如果我们要使用 CA 签名的证书，则需要创建证书签名请求 (CSR)。我们使用certreq命令执行此操作：

```bash
keytool -certreq -alias senderKeyPair -storetype PKCS12 
  -keystore sender_keystore.p12 -file -rfc 
  -storepass changeit > sender_certificate.csr
```

然后将 CSR 文件sender_certificate.csr发送到证书颁发机构以进行签名。完成后，我们将收到一个包含在 X.509 证书中的签名公钥，格式为二进制 (DER) 或文本 (PEM) 格式。在这里，我们使用了 PEM 格式的rfc选项。

我们从 CA 收到的公钥sender_certificate.cer现在已经由 CA 签名，可供客户端使用。

### 4.4. 加载公钥进行验证

获得公钥后，接收者可以使用importcert命令将其加载到他们的密钥库中：

```bash
keytool -importcert -alias receiverKeyPair -storetype PKCS12 
  -keystore receiver_keystore.p12 -file 
  sender_certificate.cer -rfc -storepass changeit
```

和以前一样使用KeyStore API，我们可以获得一个PublicKey实例：

```java
KeyStore keyStore = KeyStore.getInstance("PKCS12");
keyStore.load(new FileInputStream("receiver_keytore.p12"), "changeit");
Certificate certificate = keyStore.getCertificate("receiverKeyPair");
PublicKey publicKey = certificate.getPublicKey();
```

现在我们在发送方有一个PrivateKey实例，在接收方有一个PublicKey实例，我们可以开始签名和验证过程。

## 5. 使用MessageDigest和Cipher类的数字签名

正如我们所见，数字签名是基于散列和加密的。

通常，我们使用带有[SHA](https://www.baeldung.com/sha-256-hashing-java)或[MD5的](https://www.baeldung.com/java-md5)MessageDigest类进行散列，使用[Cipher](https://www.baeldung.com/java-cipher-class)类进行加密。

现在，让我们开始实施数字签名机制。

### 5.1. 生成消息哈希

消息可以是字符串、文件或任何其他数据。那么让我们来看一个简单文件的内容：

```java
byte[] messageBytes = Files.readAllBytes(Paths.get("message.txt"));
```

现在，使用MessageDigest，让我们使用digest 方法生成哈希：

```java
MessageDigest md = MessageDigest.getInstance("SHA-256");
byte[] messageHash = md.digest(messageBytes);
```

在这里，我们使用了最常用的 SHA-256 算法。其他替代方案是 MD5、SHA-384 和 SHA-512。

### 5.2. 加密生成的哈希

要加密消息，我们需要算法和私钥。这里我们将使用 RSA 算法。DSA 算法是另一种选择。

让我们创建一个Cipher实例并初始化它以进行加密。然后我们将调用doFinal()方法来加密之前散列的消息：

```java
Cipher cipher = Cipher.getInstance("RSA");
cipher.init(Cipher.ENCRYPT_MODE, privateKey);
byte[] digitalSignature = cipher.doFinal(messageHash);
```

签名可以保存到文件中以便稍后发送：

```java
Files.write(Paths.get("digital_signature_1"), digitalSignature);
```

至此，消息、数字签名、公钥和算法都发送完毕，接收方可以利用这些信息来验证消息的完整性。

### 5.3. 验证签名

当我们收到消息时，我们必须验证其签名。为此，我们解密接收到的加密哈希并将其与我们对接收到的消息生成的哈希进行比较。

让我们阅读收到的数字签名：

```java
byte[] encryptedMessageHash = 
  Files.readAllBytes(Paths.get("digital_signature_1"));
```

为了解密，我们创建了一个Cipher实例。然后我们调用doFinal 方法：

```java
Cipher cipher = Cipher.getInstance("RSA");
cipher.init(Cipher.DECRYPT_MODE, publicKey);
byte[] decryptedMessageHash = cipher.doFinal(encryptedMessageHash);
```

接下来，我们从收到的消息中生成一个新的消息哈希：

```java
byte[] messageBytes = Files.readAllBytes(Paths.get("message.txt"));

MessageDigest md = MessageDigest.getInstance("SHA-256");
byte[] newMessageHash = md.digest(messageBytes);
```

最后，我们检查新生成的消息哈希值是否与解密的消息哈希值匹配：

```java
boolean isCorrect = Arrays.equals(decryptedMessageHash, newMessageHash);
```

在此示例中，我们使用文本文件 message.txt来模拟我们要发送的消息，或我们收到的消息正文的位置。通常，我们希望在签名的同时收到我们的消息。

## 6. 使用签名类的数字签名

到目前为止，我们已经使用低级 API 构建了我们自己的数字签名验证过程。这有助于我们了解它的工作原理并允许我们对其进行自定义。

但是，JCA 已经以Signature类的形式提供了专用 API 。

### 6.1. 签署消息

要开始签名过程，我们首先创建Signature类的实例。为此，我们需要一个签名算法。然后我们用我们的私钥初始化签名：

```java
Signature signature = Signature.getInstance("SHA256withRSA");
signature.initSign(privateKey);
```

我们选择的签名算法，本例中的SHA256withRSA ，是哈希算法和加密算法的组合。其他替代方案包括SHA1withRSA、SHA1withDSA和MD5withRSA等。

接下来，我们继续对消息的字节数组进行签名：

```java
byte[] messageBytes = Files.readAllBytes(Paths.get("message.txt"));

signature.update(messageBytes);
byte[] digitalSignature = signature.sign();
```

我们可以将签名保存到一个文件中，以便稍后传输：

```java
Files.write(Paths.get("digital_signature_2"), digitalSignature);
```

### 6.2. 验证签名

为了验证收到的签名，我们再次创建一个Signature实例：

```java
Signature signature = Signature.getInstance("SHA256withRSA");
```

接下来，我们通过调用initVerify方法初始化用于验证的Signature对象，该方法接受一个公钥：

```java
signature.initVerify(publicKey);
```

然后，我们需要通过调用update 方法将接收到的消息字节添加到签名对象中：

```java
byte[] messageBytes = Files.readAllBytes(Paths.get("message.txt"));

signature.update(messageBytes);
```

最后，我们可以通过调用验证方法来检查签名：

```java
boolean isCorrect = signature.verify(receivedSignature);
```

## 七. 总结

在本文中，我们首先了解了数字签名的工作原理以及如何为数字证书建立信任。然后我们使用Java 密码体系结构中的MessageDigest、 Cipher和Signature类实现了数字签名。

我们详细了解了如何使用私钥对数据进行签名以及如何使用公钥验证签名。