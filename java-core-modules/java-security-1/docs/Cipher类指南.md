## 1. 概述

简而言之，加密是对消息进行编码的过程，只有授权用户才能理解或访问它。

该消息称为明文，使用加密算法(一种密码)进行加密，生成的密文只能由授权用户通过解密读取。

在本文中，我们详细描述了核心Cipher类，它在Java中提供密码加密和解密功能。

## 2.密码类

Java Cryptography Extension (JCE) 是Java Cryptography Architecture (JCA) 的一部分，它为应用程序提供用于数据加密和解密以及私有数据散列的加密算法。

Cipher类——位于javax.crypto包中——构成了 JCE 框架的核心，提供加密和解密的功能[。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/javax/crypto/Cipher.html)

### 2.1. 密码实例化

要实例化Cipher对象，我们调用静态getInstance方法，传递所请求转换的名称。可选地，可以指定提供者的名称。

让我们编写一个示例类来说明Cipher的实例化：

```java
public class Encryptor {

    public byte[] encryptMessage(byte[] message, byte[] keyBytes) 
      throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        //...
    }
}
```

转换AES/ECB/PKCS5Padding告诉getInstance方法将Cipher对象实例化为具有 ECB[操作模式](https://en.wikipedia.org/wiki/Block_cipher_mode_of_operation)和 PKCS5[填充方案的](https://en.wikipedia.org/wiki/Padding_(cryptography))[AES](https://en.wikipedia.org/wiki/Advanced_Encryption_Standard)密码。

我们还可以通过在转换中仅指定算法来实例化Cipher对象：

```java
Cipher cipher = Cipher.getInstance("AES");
```

在这种情况下，Java 将为模式和填充方案使用特定于提供者的默认值。

请注意，如果转换为null、空或格式无效，或者提供者不支持它，则getInstance将抛出NoSuchAlgorithmException 。

如果转换包含不受支持的填充方案，它将抛出NoSuchPaddingException 。

### 2.2. 线程安全

Cipher 类是有状态的，没有任何形式的内部同步。 事实上，像 [init()](https://github.com/openjdk/jdk/blob/1aa653957619acfdb5f08ce0f3a1ad1a17cfa127/src/java.base/share/classes/javax/crypto/Cipher.java#L1235) 或 [update()](https://github.com/openjdk/jdk/blob/1aa653957619acfdb5f08ce0f3a1ad1a17cfa127/src/java.base/share/classes/javax/crypto/Cipher.java#L1820) 这样的方法会改变特定 Cipher 实例的内部状态。

因此，Cipher 类不是线程安全的。 所以我们应该为每个加密/解密需要创建一个 Cipher 实例。

### 2.3. 钥匙

Key接口表示加密操作的密钥[。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/security/Key.html) 密钥是不透明的容器，其中包含编码密钥、密钥的编码格式及其加密算法。

密钥通常是通过[密钥生成器](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/javax/crypto/KeyGenerator.html)、证书或使用[密钥工厂的](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/security/KeyFactory.html)[密钥规范](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/security/spec/KeySpec.html)获得的。

让我们从提供的密钥字节创建一个对称密钥：

```java
SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
```

### 2.4. 密码初始化

我们调用init()方法用[密钥](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/security/Key.html)或[证书](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/security/cert/Certificate.html)和指示密码操作模式的操作模式来初始化 C ipher对象。

或者，我们可以传入一个随机源。默认情况下，使用最高优先级已安装提供程序的[SecureRandom实现。](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/security/SecureRandom.html)否则，它将使用系统提供的源。

我们可以选择性地指定一组特定于算法的参数。例如，我们可以传递一个[IvParameterSpec](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/javax/crypto/spec/IvParameterSpec.html)来指定一个[初始化向量](https://en.wikipedia.org/wiki/Initialization_vector)。

以下是可用的密码操作模式：

-   ENCRYPT_MODE：将密码对象初始化为加密模式
-   DECRYPT_MODE：将密码对象初始化为解密模式
-   WRAP_MODE：将密码对象初始化为[密钥包装](https://en.wikipedia.org/wiki/Key_Wrap)模式
-   UNWRAP_MODE：将密码对象初始化为[密钥解包](https://en.wikipedia.org/wiki/Key_Wrap)模式

让我们初始化Cipher对象：

```java
Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
cipher.init(Cipher.ENCRYPT_MODE, secretKey);
// ...
```

现在，如果提供的密钥不适合初始化密码，例如当密钥长度/编码无效时，init方法将抛出InvalidKeyException 。

当密码需要某些无法从密钥确定的算法参数时，或者如果密钥的密钥大小超过最大允许密钥大小(由配置的 JCE 权限策略文件确定)，也会抛出此[错误](https://docs.oracle.com/javase/9/security/java-cryptography-architecture-jca-reference-guide.htm#JSSEC-GUID-EFA5AC2D-644E-4CD9-8523-C6D3936D5FB1)。

让我们看一个使用证书的例子：

```java
public byte[] encryptMessage(byte[] message, Certificate certificate) 
  throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
 
    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    cipher.init(Cipher.ENCRYPT_MODE, certificate);
    // ...
}
```

Cipher对象通过调用getPublicKey方法从证书中获取用于数据加密的公钥。

### 2.5. 加密和解密

初始化Cipher对象后，我们调用doFinal()方法执行加密或解密操作。此方法返回包含加密或解密消息的字节数组。

doFinal ()方法还将Cipher对象重置为之前通过调用init()方法初始化时所处的状态，使Cipher对象可用于加密或解密其他消息。

让我们在encryptMessage方法中调用doFinal：

```java
public byte[] encryptMessage(byte[] message, byte[] keyBytes)
  throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, 
    BadPaddingException, IllegalBlockSizeException {
 
    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    return cipher.doFinal(message);
}
```

要执行解密操作，我们将操作模式更改为DECRYPT_MODE：

```java
public byte[] decryptMessage(byte[] encryptedMessage, byte[] keyBytes) 
  throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, 
    BadPaddingException, IllegalBlockSizeException {
 
    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
    cipher.init(Cipher.DECRYPT_MODE, secretKey);
    return cipher.doFinal(encryptedMessage);
}
```

### 2.6. 供应商

JCE旨在使用[基于提供者的架构](https://en.wikipedia.org/wiki/Provider_model)，允许将合格的加密库(例如[BouncyCastle)](https://www.bouncycastle.org/)作为安全提供者插入，并允许无缝添加新算法。

现在让我们将 BouncyCastle 添加为安全提供程序。我们可以静态或动态地添加安全提供程序。

要静态添加 BouncyCastle，我们修改位于<JAVA_HOME>/jre/lib/security文件夹中的java.security文件。

我们在列表的末尾添加行：

```plaintext
...
security.provider.4=com.sun.net.ssl.internal.ssl.Provider
security.provider.5=com.sun.crypto.provider.SunJCE
security.provider.6=sun.security.jgss.SunProvider
security.provider.7=org.bouncycastle.jce.provider.BouncyCastleProvider
```

添加提供者属性时，属性键的格式为security.provider.N，其中数字N比列表中的最后一个多 1。

我们还可以动态添加 BouncyCastle 安全提供程序，而无需修改安全文件：

```java
Security.addProvider(new BouncyCastleProvider());
```

我们现在可以在密码初始化期间指定提供者：

```java
Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "BC");
```

BC指定 BouncyCastle 作为提供者。我们可以通过Security.getProviders()方法获取注册提供者的列表。

## 3.测试加解密

让我们编写一个示例测试来说明消息加密和解密。

在这个测试中，我们使用带有 128 位密钥的 AES 加密算法并断言解密结果等于原始消息文本：

```java
@Test
public void whenIsEncryptedAndDecrypted_thenDecryptedEqualsOriginal() 
  throws Exception {
 
    String encryptionKeyString =  "thisisa128bitkey";
    String originalMessage = "This is a secret message";
    byte[] encryptionKeyBytes = encryptionKeyString.getBytes();

    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    SecretKey secretKey = new SecretKeySpec(encryptionKeyBytes, "AES");
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);

    byte[] encryptedMessageBytes = cipher.doFinal(message.getBytes());

    cipher.init(Cipher.DECRYPT_MODE, secretKey);

    byte[] decryptedMessageBytes = cipher.doFinal(encryptedMessageBytes);
    assertThat(originalMessage).isEqualTo(new String(decryptedMessageBytes));
}
```

## 4。总结

在本文中，我们讨论了Cipher类并提供了使用示例。有关Cipher类和 JCE Framework的更多详细信息，请参阅[类文档](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/javax/crypto/Cipher.html)和[Java Cryptography Architecture (JCA) Reference Guide](https://docs.oracle.com/javase/9/security/java-cryptography-architecture-jca-reference-guide.htm)。