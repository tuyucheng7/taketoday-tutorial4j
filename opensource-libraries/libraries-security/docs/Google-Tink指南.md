## 1. 简介

如今，许多开发人员使用加密技术来保护用户数据。

在密码学中，小的实施错误可能会造成严重后果，理解如何正确实施密码学是一项复杂且耗时的任务。

在本教程中，我们将介绍[Tink——](https://github.com/google/tink)一个多语言、跨平台的加密库，可以帮助我们实现安全的加密代码。

## 2.依赖关系

我们可以使用 Maven 或 Gradle 导入 Tink。

对于我们的教程，我们将只添加[Tink 的 Maven 依赖](https://search.maven.org/search?q=g:com.google.crypto.tink AND a:tink&core=gav)项：

```xml
<dependency>
    <groupId>com.google.crypto.tink</groupId>
    <artifactId>tink</artifactId>
    <version>1.2.2</version>
</dependency>
```

虽然我们可以改用 Gradle：

```xml
dependencies {
  compile 'com.google.crypto.tink:tink:latest'
}
```

## 3.初始化

在使用任何 Tink API 之前，我们需要对其进行初始化。

如果我们需要使用 Tink 中所有原语的所有实现，我们可以使用TinkConfig.register()方法：

```java
TinkConfig.register();
```

而例如，如果我们只需要 AEAD 原语，我们可以使用AeadConfig.register()方法：

```java
AeadConfig.register();
```

也为每个实现提供了可定制的初始化。

## 4. Tink 原语

库使用的主要对象称为原语，根据类型的不同，它包含不同的加密功能。

一个原语可以有多个实现：

| 原始       | 实现                                                        |
| ---------- | ----------------------------------------------------------- |
| AEAD       | AES-EAX、AES-GCM、AES-CTR-HMAC、KMS 信封、CHACHA20-POLY1305 |
| 流媒体AEAD | AES-GCM-HKDF-流媒体，AES-CTR-HMAC-流媒体                    |
| 确定性AEAD | AEAD：AES-SIV                                               |
| 苹果电脑   | HMAC-SHA2                                                   |
| 电子签名   | NIST 曲线上的 ECDSA，ED25519                                |
| 混合加密   | ECIES 与 AEAD 和 HKDF，(NaCl CryptoBox)                   |

我们可以通过调用相应工厂类的getPrimitive()方法并传递给它一个KeysetHandle来获得一个原语：

```java
Aead aead = AeadFactory.getPrimitive(keysetHandle);

```

### 4.1. 键组句柄

为了提供加密功能，每个原语都需要一个包含所有密钥材料和参数的密钥结构。

Tink 提供了一个对象——KeysetHandle—— 它用一些额外的参数和元数据包装了一个键集。

因此，在实例化原语之前，我们需要创建一个KeysetHandle 对象：

```java
KeysetHandle keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES256_GCM);
```

生成密钥后，我们可能希望保留它：

```java
String keysetFilename = "keyset.json";
CleartextKeysetHandle.write(keysetHandle, JsonKeysetWriter.withFile(new File(keysetFilename)));
```

然后，我们可以随后加载它：

```java
String keysetFilename = "keyset.json";
KeysetHandle keysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(new File(keysetFilename)));
```

## 5.加密

Tink 提供了多种应用 AEAD 算法的方法。让我们来看看。

### 5.1. AEAD

AEAD 提供带关联数据的身份验证加密，这意味着我们可以加密明文，并且可以选择提供应该经过身份验证但未加密的关联数据。

请注意，此算法可确保关联数据的真实性和完整性，但不能确保其保密性。

如前所述，要使用 AEAD 实现之一加密数据，我们需要初始化库并创建一个keysetHandle：

```java
AeadConfig.register();
KeysetHandle keysetHandle = KeysetHandle.generateNew(
  AeadKeyTemplates.AES256_GCM);
```

完成后，我们可以获得原语并加密所需的数据：

```java
String plaintext = "baeldung";
String associatedData = "Tink";

Aead aead = AeadFactory.getPrimitive(keysetHandle); 
byte[] ciphertext = aead.encrypt(plaintext.getBytes(), associatedData.getBytes());
```

接下来，我们可以使用decrypt()方法解密密文：

```java
String decrypted = new String(aead.decrypt(ciphertext, associatedData.getBytes()));
```

### 5.2. 流媒体AEAD

同样，当要加密的数据太大，无法一步处理时，我们可以使用流式AEAD原语：

```java
AeadConfig.register();
KeysetHandle keysetHandle = KeysetHandle.generateNew(
  StreamingAeadKeyTemplates.AES128_CTR_HMAC_SHA256_4KB);
StreamingAead streamingAead = StreamingAeadFactory.getPrimitive(keysetHandle);

FileChannel cipherTextDestination = new FileOutputStream("cipherTextFile").getChannel();
WritableByteChannel encryptingChannel =
  streamingAead.newEncryptingChannel(cipherTextDestination, associatedData.getBytes());

ByteBuffer buffer = ByteBuffer.allocate(CHUNK_SIZE);
InputStream in = new FileInputStream("plainTextFile");

while (in.available() > 0) {
    in.read(buffer.array());
    encryptingChannel.write(buffer);
}

encryptingChannel.close();
in.close();
```

基本上，我们需要WriteableByteChannel来实现这一点。

因此，要解密cipherTextFile，我们需要使用 ReadableByteChannel：

```java
FileChannel cipherTextSource = new FileInputStream("cipherTextFile").getChannel();
ReadableByteChannel decryptingChannel =
  streamingAead.newDecryptingChannel(cipherTextSource, associatedData.getBytes());

OutputStream out = new FileOutputStream("plainTextFile");
int cnt = 1;
do {
    buffer.clear();
    cnt = decryptingChannel.read(buffer);
    out.write(buffer.array());
} while (cnt>0);

decryptingChannel.close();
out.close();
```

## 6.混合加密

除了对称加密之外，Tink 还实现了一些用于混合加密的原语。

通过混合加密，我们可以获得对称密钥的效率和非对称密钥的便利性。

简而言之，我们将使用对称密钥加密明文，使用公钥仅加密对称密钥。

请注意，它仅提供保密性，而不提供发件人的身份真实性。

那么，让我们看看如何使用HybridEncrypt和HybridDecrypt：

```java
TinkConfig.register();

KeysetHandle privateKeysetHandle = KeysetHandle.generateNew(
  HybridKeyTemplates.ECIES_P256_HKDF_HMAC_SHA256_AES128_CTR_HMAC_SHA256);
KeysetHandle publicKeysetHandle = privateKeysetHandle.getPublicKeysetHandle();

String plaintext = "baeldung";
String contextInfo = "Tink";

HybridEncrypt hybridEncrypt = HybridEncryptFactory.getPrimitive(publicKeysetHandle);
HybridDecrypt hybridDecrypt = HybridDecryptFactory.getPrimitive(privateKeysetHandle);

byte[] ciphertext = hybridEncrypt.encrypt(plaintext.getBytes(), contextInfo.getBytes());
byte[] plaintextDecrypted = hybridDecrypt.decrypt(ciphertext, contextInfo.getBytes());
```

contextInfo 是来自上下文的隐式公共数据，可以为 null或空或用作 AEAD 加密的“关联数据”输入或 HKDF 的“CtxInfo”输入。

密文允许检查contextInfo的完整性，但不能检查其保密性或真实性。

## 7. 消息认证码

Tink 还支持消息身份验证代码或 MAC。

MAC 是几个字节的块，我们可以使用它来验证消息。

让我们看看如何创建 MAC，然后验证其真实性：

```java
TinkConfig.register();

KeysetHandle keysetHandle = KeysetHandle.generateNew(
  MacKeyTemplates.HMAC_SHA256_128BITTAG);

String data = "baeldung";

Mac mac = MacFactory.getPrimitive(keysetHandle);

byte[] tag = mac.computeMac(data.getBytes());
mac.verifyMac(tag, data.getBytes());
```

如果数据不真实，方法verifyMac()将抛出GeneralSecurityException。

## 8. 数字签名

除了加密 API，Tink 还支持数字签名。

为了实现数字签名，库使用PublicKeySign原语进行数据签名，并使用PublickeyVerify进行验证：

```java
TinkConfig.register();

KeysetHandle privateKeysetHandle = KeysetHandle.generateNew(SignatureKeyTemplates.ECDSA_P256);
KeysetHandle publicKeysetHandle = privateKeysetHandle.getPublicKeysetHandle();

String data = "baeldung";

PublicKeySign signer = PublicKeySignFactory.getPrimitive(privateKeysetHandle);
PublicKeyVerify verifier = PublicKeyVerifyFactory.getPrimitive(publicKeysetHandle);

byte[] signature = signer.sign(data.getBytes()); 
verifier.verify(signature, data.getBytes());
```

与前面的加密方法类似，当签名无效时，我们会得到一个GeneralSecurityException。

## 9.总结

在本文中，我们介绍了使用Java实现的 Google Tink 库。

我们已经了解了如何使用它来加密和解密数据以及如何保护其完整性和真实性。此外，我们还了解了如何使用数字签名 API 对数据进行签名。