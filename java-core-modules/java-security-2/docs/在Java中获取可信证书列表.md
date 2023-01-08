## 1. 概述

在本快速教程中，我们将学习如何通过快速实用的示例读取Java中的受信任证书列表。

## 2.加载KeyStore

Java 将受信任的证书存储在一个名为cacerts的特殊文件中，该文件位于我们的Java安装文件夹中。

让我们从读取此文件并将其加载到[KeyStore](https://www.baeldung.com/java-keystore#what-is-a-keystore)开始：

```java
private KeyStore loadKeyStore() {
    String relativeCacertsPath = "/lib/security/cacerts".replace("/", File.separator);
    String filename = System.getProperty("java.home") + relativeCacertsPath;
    FileInputStream is = new FileInputStream(filename);

    KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
    String password = "changeit";
    keystore.load(is, password.toCharArray());

    return keystore;
}
```

此KeyStore的默认密码是“changeit”，但如果之前在我们的系统中更改过，它可能会有所不同。

加载后，KeyStore将保存我们信任的证书，接下来，我们将了解如何读取它们。

## 3.从指定KeyStore中读取证书

我们将使用[PKIXParameters](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/security/cert/PKIXParameters.html) 类，它将KeyStore作为构造函数参数：

```java
@Test
public void whenLoadingCacertsKeyStore_thenCertificatesArePresent() {
    KeyStore keyStore = loadKeyStore();
    PKIXParameters params = new PKIXParameters(keyStore);

    Set<TrustAnchor> trustAnchors = params.getTrustAnchors();
    List<Certificate> certificates = trustAnchors.stream()
      .map(TrustAnchor::getTrustedCert)
      .collect(Collectors.toList());

    assertFalse(certificates.isEmpty());
}
```

PKIXParameters类通常用于验证证书，但在我们的示例中，我们只是使用它从我们的KeyStore中提取证书。

在创建PKIXParametrs的实例时，它会构建一个[TrustAnchor](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/security/cert/TrustAnchor.html)列表，其中将包含我们的KeyStore 中存在的受信任证书。

TrustAnchor实例仅表示受信任的证书。

## 4. 从默认密钥库读取证书

我们还可以通过使用TrustManagerFactory 类并在没有KeyStore 的情况下初始化它来获取我们系统中存在的受信任证书的列表，这将使用默认的KeyStore。

如果我们不显式提供KeyStore，则默认使用上一章中的相同密钥库：

```java
@Test
public void whenLoadingDefaultKeyStore_thenCertificatesArePresent() {
    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init((KeyStore) null);

    List<TrustManager> trustManagers = Arrays.asList(trustManagerFactory.getTrustManagers());
    List<X509Certificate> certificates = trustManagers.stream()
      .filter(X509TrustManager.class::isInstance)
      .map(X509TrustManager.class::cast)
      .map(trustManager -> Arrays.asList(trustManager.getAcceptedIssuers()))
      .flatMap(Collection::stream)
      .collect(Collectors.toList());

    assertFalse(certificates.isEmpty());
}
```

在上面的示例中，我们使用了X509TrustManager，这是一个专门 用于[验证 SSL 连接的远程部分的](https://www.baeldung.com/x-509-authentication-in-spring-security)TrustManager。

请注意，此行为可能取决于特定的 JDK 实现，因为[规范](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/javax/net/ssl/TrustManagerFactory.html#init(java.security.KeyStore))没有定义在init() KeyStore参数为null 的情况下应该发生什么。

## 5.证书别名

证书别名只是一个唯一标识证书的字符串。

在Java导入的默认证书中，还有一个由公共互联网域名注册商 GoDaddy 颁发的知名证书，我们将在我们的测试中使用它：

```java
String GODADDY_CA_ALIAS = "godaddyrootg2ca [jdk]";
```

让我们看看如何读取KeyStore中存在的所有证书别名：

```java
@Test
public void whenLoadingKeyStore_thenGoDaddyCALabelIsPresent() {
    KeyStore keyStore = loadKeyStore();

    Enumeration<String> aliasEnumeration = keyStore.aliases();
    List<String> aliases = Collections.list(aliasEnumeration);
    assertTrue(aliases.contains(GODADDY_CA_ALIAS));
}
```

在下一个示例中，我们将看到如何通过别名检索证书：

```java
@Test
public void whenLoadingKeyStore_thenGoDaddyCertificateIsPresent() {
    KeyStore keyStore = loadKeyStore();

    Certificate goDaddyCertificate = keyStore.getCertificate(GODADDY_CA_ALIAS);
    assertNotNull(goDaddyCertificate);
}
```

## 六，总结

在这篇简短的文章中，我们通过快速实用的示例了解了在Java中列出受信任证书的不同方法。