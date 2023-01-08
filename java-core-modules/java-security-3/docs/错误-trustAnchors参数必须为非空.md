## 1. 概述

在本教程中，我们将解释什么是信任锚。此外，我们将显示TrustStore的默认位置和预期的文件格式。最后，我们将澄清错误的原因：“ java.security.InvalidAlgorithmParameterException：trust anchors parameter must be non-empty”。

## 2. 信任锚定义

让我们首先解释一下什么是[信任锚](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/security/cert/TrustAnchor.html)。在密码系统中，信任锚定义了假定和派生信任的根实体。在像 X.509 这样的架构中，根证书是一个信任锚。此外，根证书保证链中所有其他证书的信任。

## 3. TrustStore位置和格式

现在让我们看一下Java 中的[TrustStore](https://www.baeldung.com/java-keystore-truststore-difference#java-truststore)位置和格式。首先，Java在两个位置(按顺序)查找TrustStore ：

-   $JAVA_HOME/lib/security/jssecacerts
-   $JAVA_HOME/lib/security/cacerts

我们可以使用参数-Djavax.net.ssl.trustStore 覆盖默认位置。

此外，参数-Djavax.net.ssl.trustStorePassword 允许我们向TrustStore提供密码。最后，命令看起来像这样：

```java
java -Djavax.net.ssl.trustStore=/some/loc/on/server/ our_truststore.jks -Djavax.net.ssl.trustStorePassword=our_password -jar application.jar
```

此外，[JKS](https://www.baeldung.com/convert-pem-to-jks#file-formats)是默认的TrustStore格式。参数-Djavax.net.ssl.trustStoreType允许覆盖默认的TrustStore类型。

让我们看一下Java 16 中针对$JAVA_HOME/lib/security/cacerts执行的keytool实用程序的输出：

```bash
$ keytool -list -cacerts
Enter keystore password:
Keystore type: JKS
Keystore provider: SUN

Your keystore contains 90 entries
....
```

正如预期的那样，KeyStore类型是 JKS。此外，我们还获得了文件中存储的所有 90 个证书。

## 四、异常原因

现在让我们看一下异常“ java.security.InvalidAlgorithmParameterException：trustAnchors 参数必须为非空”。

首先，Java 运行时仅在[PKIXParameters类中创建](https://www.baeldung.com/java-list-trusted-certificates#reading-certificates-from-a-specified-keystore)[InvalidAlgorithmParameterException](https://cr.openjdk.java.net/~iris/se/11/latestSpec/api/java.base/java/security/InvalidAlgorithmParameterException.html)，该类用于从KeyStore读取证书。PKIXParameters的构造函数从作为参数给定的KeyStore 中收集trustAnchors 。

当提供的KeyStore没有trustAnchors时抛出异常：

```java
...
if (trustAnchors.isEmpty()) {
    throw new InvalidAlgorithmParameterException("the trustAnchors " +
        "parameter must be non-empty");
}
...
```

让我们尝试重现这个案例。首先，让我们创建一个空的KeyStore：

```java
private KeyStore getKeyStore() throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {
    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
    ks.load(null, "changeIt".toCharArray());
    return ks;
}
```

现在让我们测试PKIXParameters类的实例化：

```java
@Test
public void whenOpeningTrustStore_thenExceptionIsThrown() throws Exception {
    KeyStore keyStore = getKeyStore();
    InvalidAlgorithmParameterException invalidAlgorithmParameterException =
      Assertions.assertThrows(InvalidAlgorithmParameterException.class, () -> new PKIXParameters(keyStore));
    Assertions.assertEquals("the trustAnchors parameter must be non-empty", invalidAlgorithmParameterException.getMessage());
}
```

也就是说，构造函数按预期抛出了异常。换句话说，当给定的KeyStore中没有受信任的证书时，不可能创建PKIXParameters类的实例。

## 5.总结

在这篇简短的文章中，我们描述了什么是信任锚。然后，我们展示了默认的TrustStore位置和文件格式。最后，我们展示了“trust anchors parameter must be non-empty”错误的原因。