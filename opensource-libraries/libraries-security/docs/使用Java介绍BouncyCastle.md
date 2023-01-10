## 1. 概述

[BouncyCastle](https://www.bouncycastle.org/)是一个Java库，它补充了默认的Java加密扩展 (JCE)。

在这篇介绍性文章中，我们将展示如何使用 BouncyCastle 执行加密操作，例如加密和签名。

## 2.Maven配置

在我们开始使用该库之前，我们需要将所需的依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcpkix-jdk15on</artifactId>
    <version>1.58</version>
</dependency>
```

[请注意，我们始终可以在Maven Central Repository](https://search.maven.org/classic/#search|ga|1|g%3A"org.bouncycastle" )中查找最新的依赖项版本。

## 3.设置无限强度管辖策略文件

标准Java安装在加密功能的强度方面受到限制，这是由于政策禁止使用大小超过特定值的密钥，例如 AES 为 128。

为了克服这个限制，我们需要配置无限强度的权限策略文件。

为此，我们首先需要通过此[链接](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)下载软件包。之后，我们需要将压缩文件解压缩到我们选择的目录中——其中包含两个 jar 文件：

-   local_policy.jar
-   US_export_policy.jar

最后，我们需要查找{JAVA_HOME}/lib/security文件夹并将现有策略文件替换为我们在此处提取的文件。

请注意，在Java9 中，我们不再需要下载策略文件包，将crypto.policy属性设置为unlimited就足够了：

```java
Security.setProperty("crypto.policy", "unlimited");
```

完成后，我们需要检查配置是否正常工作：

```java
int maxKeySize = javax.crypto.Cipher.getMaxAllowedKeyLength("AES");
System.out.println("Max Key Size for AES : " + maxKeySize);
```

因此：

```plaintext
Max Key Size for AES : 2147483647
```

根据getMaxAllowedKeyLength()方法返回的最大密钥大小，我们可以有把握地说无限强度策略文件已正确安装。

如果返回值等于 128，我们需要确保已将文件安装到运行代码的 JVM 中。

## 4.密码操作

### 4.1. 准备证书和私钥

在我们进入加密函数的实现之前，我们首先需要创建一个证书和一个私钥。

出于测试目的，我们可以使用这些资源：

-   [Baeldung.cer](https://github.com/eugenp/tutorials/tree/master/libraries/src/main/resources)
-   [Baeldung.p12(密码=“密码”)](https://github.com/eugenp/tutorials/tree/master/libraries/src/main/resources)

Baeldung.cer是使用国际 X.509 公钥基础设施标准的数字证书，而Baeldung.p12是包含私钥的受密码保护的[PKCS12](https://tools.ietf.org/html/rfc7292)密钥库。

让我们看看如何在Java中加载它们：

```java
Security.addProvider(new BouncyCastleProvider());
CertificateFactory certFactory= CertificateFactory
  .getInstance("X.509", "BC");
 
X509Certificate certificate = (X509Certificate) certFactory
  .generateCertificate(new FileInputStream("Baeldung.cer"));
 
char[] keystorePassword = "password".toCharArray();
char[] keyPassword = "password".toCharArray();
 
KeyStore keystore = KeyStore.getInstance("PKCS12");
keystore.load(new FileInputStream("Baeldung.p12"), keystorePassword);
PrivateKey key = (PrivateKey) keystore.getKey("baeldung", keyPassword);
```

首先，我们使用addProvider()方法动态添加BouncyCastleProvider作为安全提供程序。

这也可以通过编辑{JAVA_HOME}/jre/lib/security/java.security文件并添加以下行来静态完成：

```plaintext
security.provider.N = org.bouncycastle.jce.provider.BouncyCastleProvider
```

正确安装提供程序后，我们就使用getInstance()方法创建了一个CertificateFactory对象。

getInstance()方法有两个参数；证书类型“X.509”和安全提供商“BC”。

certFactory实例随后用于通过generateCertificate ()方法生成X509Certificate对象。

以同样的方式，我们创建了一个 PKCS12 Keystore 对象，在该对象上调用了load()方法。

getKey()方法返回与给定别名关联的私钥。

请注意，PKCS12 Keystore 包含一组私钥，每个私钥都可以有一个特定的密码，这就是为什么我们需要一个全局密码来打开 Keystore，以及一个特定的密码来检索私钥。

Certificate和私钥对主要用于非对称密码操作：

-   加密
-   解密
-   签名
-   确认

### 4.2. CMS/PKCS7加解密

在非对称加密密码学中，每次通信都需要一个公钥和一个私钥。

收件人绑定到证书，该证书在所有发件人之间公开共享。

简而言之，发件人需要收件人的证书来加密消息，而收件人需要关联的私钥才能解密消息。

让我们看一下如何使用加密证书实现encryptData()函数：

```java
public static byte[] encryptData(byte[] data,
  X509Certificate encryptionCertificate)
  throws CertificateEncodingException, CMSException, IOException {
 
    byte[] encryptedData = null;
    if (null != data && null != encryptionCertificate) {
        CMSEnvelopedDataGenerator cmsEnvelopedDataGenerator
          = new CMSEnvelopedDataGenerator();
 
        JceKeyTransRecipientInfoGenerator jceKey 
          = new JceKeyTransRecipientInfoGenerator(encryptionCertificate);
        cmsEnvelopedDataGenerator.addRecipientInfoGenerator(transKeyGen);
        CMSTypedData msg = new CMSProcessableByteArray(data);
        OutputEncryptor encryptor
          = new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES128_CBC)
          .setProvider("BC").build();
        CMSEnvelopedData cmsEnvelopedData = cmsEnvelopedDataGenerator
          .generate(msg,encryptor);
        encryptedData = cmsEnvelopedData.getEncoded();
    }
    return encryptedData;
}
```

我们使用接收者的证书创建了一个JceKeyTransRecipientInfoGenerator对象。

然后，我们创建了一个新的CMSEnvelopedDataGenerator对象并将收件人信息生成器添加到其中。

之后，我们使用 AES CBC 算法使用JceCMSContentEncryptorBuilder类创建了一个OutputEncrytor对象。

加密器稍后用于生成封装加密消息的CMSEnvelopedData对象。

最后，信封的编码表示作为字节数组返回。

现在，让我们看看decryptData()方法的实现是什么样的：

```java
public static byte[] decryptData(
  byte[] encryptedData, 
  PrivateKey decryptionKey) 
  throws CMSException {
 
    byte[] decryptedData = null;
    if (null != encryptedData && null != decryptionKey) {
        CMSEnvelopedData envelopedData = new CMSEnvelopedData(encryptedData);
 
        Collection<RecipientInformation> recipients
          = envelopedData.getRecipientInfos().getRecipients();
        KeyTransRecipientInformation recipientInfo 
          = (KeyTransRecipientInformation) recipients.iterator().next();
        JceKeyTransRecipient recipient
          = new JceKeyTransEnvelopedRecipient(decryptionKey);
        
        return recipientInfo.getContent(recipient);
    }
    return decryptedData;
}
```

首先，我们使用加密数据字节数组初始化了一个CMSEnvelopedData对象，然后我们使用getRecipients()方法检索了消息的所有预期接收者。

一旦完成，我们就创建了一个与接收者私钥关联的新JceKeyTransRecipient对象。

recipientInfo实例包含解密/封装的消息，但除非我们有相应的收件人密钥，否则我们无法检索它。

最后，给定收件人密钥作为参数，getContent()方法返回从与该收件人关联的EnvelopedData中提取的原始字节数组。

让我们编写一个简单的测试来确保一切正常工作：

```plaintext
String secretMessage = "My password is 123456Seven";
System.out.println("Original Message : " + secretMessage);
byte[] stringToEncrypt = secretMessage.getBytes();
byte[] encryptedData = encryptData(stringToEncrypt, certificate);
System.out.println("Encrypted Message : " + new String(encryptedData));
byte[] rawData = decryptData(encryptedData, privateKey);
String decryptedMessage = new String(rawData);
System.out.println("Decrypted Message : " + decryptedMessage);
```

因此：

```plaintext
Original Message : My password is 123456Seven
Encrypted Message : 0��H��...
Decrypted Message : My password is 123456Seven
```

### 4.3. CMS/PKCS7 签名和验证

签名和验证是验证数据真实性的加密操作。

让我们看看如何使用数字证书签署秘密消息：

```java
public static byte[] signData(
  byte[] data, 
  X509Certificate signingCertificate,
  PrivateKey signingKey) throws Exception {
 
    byte[] signedMessage = null;
    List<X509Certificate> certList = new ArrayList<X509Certificate>();
    CMSTypedData cmsData= new CMSProcessableByteArray(data);
    certList.add(signingCertificate);
    Store certs = new JcaCertStore(certList);

    CMSSignedDataGenerator cmsGenerator = new CMSSignedDataGenerator();
    ContentSigner contentSigner 
      = new JcaContentSignerBuilder("SHA256withRSA").build(signingKey);
    cmsGenerator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
      new JcaDigestCalculatorProviderBuilder().setProvider("BC")
      .build()).build(contentSigner, signingCertificate));
    cmsGenerator.addCertificates(certs);
    
    CMSSignedData cms = cmsGenerator.generate(cmsData, true);
    signedMessage = cms.getEncoded();
    return signedMessage;
}

```

首先，我们将输入嵌入到CMSTypedData中，然后，我们创建了一个新的CMSSignedDataGenerator对象。

我们使用SHA256withRSA作为签名算法，并使用我们的签名密钥创建一个新的ContentSigner对象。

contentSigner实例随后与签名证书一起用于创建SigningInfoGenerator对象。

将SignerInfoGenerator和签名证书添加到CMSSignedDataGenerator实例后，我们最后使用generate()方法创建一个 CMS 签名数据对象，该对象也带有 CMS 签名。

现在我们已经了解了如何签署数据，让我们看看如何验证签署的数据：

```java
public static boolean verifSignedData(byte[] signedData)
  throws Exception {
 
    X509Certificate signCert = null;
    ByteArrayInputStream inputStream
     = new ByteArrayInputStream(signedData);
    ASN1InputStream asnInputStream = new ASN1InputStream(inputStream);
    CMSSignedData cmsSignedData = new CMSSignedData(
      ContentInfo.getInstance(asnInputStream.readObject()));
    
    SignerInformationStore signers 
      = cmsSignedData.getCertificates().getSignerInfos();
    SignerInformation signer = signers.getSigners().iterator().next();
    Collection<X509CertificateHolder> certCollection 
      = certs.getMatches(signer.getSID());
    X509CertificateHolder certHolder = certCollection.iterator().next();
    
    return signer
      .verify(new JcaSimpleSignerInfoVerifierBuilder()
      .build(certHolder));
}
```

同样，我们基于签名数据字节数组创建了一个CMSSignedData对象，然后，我们使用getSignerInfos()方法检索了与签名关联的所有签名者。

在此示例中，我们仅验证了一个签名者，但对于一般用途，必须迭代getSigners()方法返回的签名者集合并分别检查每个签名者。

最后，我们使用build()方法创建了一个SignerInformationVerifier对象并将其传递给verify()方法。

如果给定对象可以成功验证签名者对象上的签名，则verify() 方法返回true 。

这是一个简单的例子：

```java
byte[] signedData = signData(rawData, certificate, privateKey);
Boolean check = verifSignData(signedData);
System.out.println(check);
```

因此：

```plaintext
true
```

## 5.总结

在本文中，我们了解了如何使用 BouncyCastle 库执行基本的加密操作，例如加密和签名。

在现实世界中，我们经常希望对我们的数据进行签名然后加密，这样只有接收方才能使用私钥对其进行解密，并根据数字签名来验证其真实性。