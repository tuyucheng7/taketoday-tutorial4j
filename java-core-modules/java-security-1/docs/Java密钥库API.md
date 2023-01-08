## 1. 概述

在本教程中，我们将学习如何使用KeyStore API 在Java中管理加密密钥和证书。

## 2.密钥库

如果我们需要在Java中管理密钥和证书，我们需要一个keystore，它只是密钥和证书的别名 条目的安全集合。

我们通常将密钥库保存到文件系统中，我们可以用密码保护它。

默认情况下，Java 有一个位于JAVA_HOME/ jre /lib/security/cacerts的密钥库文件。我们可以使用默认密钥库密码changeit访问此密钥库。

现在我们已经建立了一些背景，让我们创建我们的第一个。

## 3. 创建密钥库

### 3.1. 建造

[我们可以使用keytool](https://docs.oracle.com/en/java/javase/11/tools/keytool.html)轻松创建一个密钥库 ，或者我们可以使用 KeyStore API 以编程方式进行创建：

```java
KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
```

这里我们使用了默认类型，尽管有[一些](https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#keystore-types)可用的密钥库类型，如jceks或pkcs12。

我们可以使用-Dkeystore.type参数覆盖默认的“JKS”(Oracle 专有密钥库协议)类型：

```bash
-Dkeystore.type=pkcs12
```

或者我们可以在getInstance中列出一种支持的格式：

```java
KeyStore ks = KeyStore.getInstance("pkcs12");

```

### 3.2. 初始化

最初，我们需要 加载密钥库：

```java
char[] pwdArray = "password".toCharArray();
ks.load(null, pwdArray);

```

无论是创建新密钥库还是打开现有密钥库，我们都会使用 加载。我们将通过传递null作为第一个参数来告诉KeyStore创建一个新的 。

我们还提供了一个密码，用于将来访问密钥库。我们也可以将其设置为null，尽管这会泄露我们的秘密。

### 3.3. 贮存

最后，我们将新的密钥库保存到文件系统：

```java
try (FileOutputStream fos = new FileOutputStream("newKeyStoreFileName.jks")) {
    ks.store(fos, pwdArray);
}

```

请注意，上面未显示的是getInstance、 加载 和 存储每次抛出的几个已检查异常 。

## 4.加载密钥库

要加载密钥库，我们首先需要像以前一样创建一个KeyStore实例。

不过这一次，我们将指定格式，因为我们正在加载一个现有的格式：

```java
KeyStore ks = KeyStore.getInstance("JKS");
ks.load(new FileInputStream("newKeyStoreFileName.jks"), pwdArray);
```

如果我们的 JVM 不支持我们传递的密钥库类型，或者如果它与我们打开的文件系统上的密钥库类型不匹配，我们将得到一个KeyStoreException：

```java
java.security.KeyStoreException: KEYSTORE_TYPE not found
```

此外，如果密码错误，我们将得到一个 UnrecoverableKeyException：

```java
java.security.UnrecoverableKeyException: Password verification failed
```

## 5. 存储条目

在密钥库中，我们可以存储三种不同的条目，每一种都在其别名下：

-   Symmetric Keys(在JCE中称为Secret Keys)
-   非对称密钥(在 JCE 中称为公钥和私钥)
-   可信证书

让我们来看看每一个。

### 5.1. 保存对称密钥

我们可以在密钥库中存储的最简单的东西是对称密钥。

要保存对称密钥，我们需要三样东西：

1.  别名——这只是我们将来用来指代条目的名称
2.  一个密钥——包裹在 KeyStore.SecretKeyEntry中
3.  密码——包裹在所谓的 ProtectionParam 中

```java
KeyStore.SecretKeyEntry secret
 = new KeyStore.SecretKeyEntry(secretKey);
KeyStore.ProtectionParameter password
 = new KeyStore.PasswordProtection(pwdArray);
ks.setEntry("db-encryption-secret", secret, password);
```

请记住，密码不能为空；但是，它可以是空字符串。 如果我们将条目的密码保留为null ，我们将得到KeyStoreException：

```java
java.security.KeyStoreException: non-null password required to create SecretKeyEntry
```

我们需要将密钥和密码包装在包装类中，这似乎有点奇怪。

我们包装密钥，因为 setEntry是一种通用方法，也可用于其他条目类型。条目的类型允许 KeyStore API 以不同的方式对待它。

我们包装密码是因为 KeyStore API 支持回调到 GUI 和 CLI 以从最终用户那里收集密码。我们可以查看[KeyStore .CallbackHandlerProtection Javadoc以获取](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/security/KeyStore.CallbackHandlerProtection.html) 更多详细信息。

我们还可以使用此方法更新现有密钥；我们只需要用相同的别名和密码以及我们的新秘密再次调用它。

### 5.2. 保存私钥

存储非对称密钥有点复杂，因为我们需要处理证书链。

KeyStore API 为我们提供了一个名为setKeyEntry 的专用方法，它 比通用的 setEntry 方法更方便。

因此，要保存非对称密钥，我们需要四样东西：

1.  别名——像以前一样
2.  私钥——因为我们没有使用通用方法，所以密钥不会被包装。此外，在我们的例子中，它应该是PrivateKey 的一个实例。
3.  密码- 用于访问条目。这次，密码是强制性的。
4.  证书链——这证明了相应的公钥

```java
X509Certificate[] certificateChain = new X509Certificate[2];
chain[0] = clientCert;
chain[1] = caCert;
ks.setKeyEntry("sso-signing-key", privateKey, pwdArray, certificateChain);
```

当然，这里可能会出现很多错误，比如pwdArray是否为null：

```java
java.security.KeyStoreException: password can't be null
```

但是有一个非常奇怪的异常需要注意，如果pwdArray是一个空数组就会发生：

```java
java.security.UnrecoverableKeyException: Given final block not properly padded
```

要更新，我们只需使用相同的别名和新的privateKey和 certificateChain 再次调用该方法。

快速复习一下[如何生成证书链](https://support.sas.com/documentation/cdl/en/secref/69831/HTML/default/viewer.htm#p0gy97oedcx0fin1n83srxchqpzk.htm)可能也很有价值。

### 5.3. 保存可信证书

存储受信任的证书非常简单。 它只需要别名和证书 本身，它是Certificate类型 ：

```java
ks.setCertificateEntry("google.com", trustedCertificate);
```

通常，证书不是我们生成的，而是来自第三方的。

因此，请务必注意，KeyStore实际上并不验证此证书。我们应该在存储之前自行验证它。

要更新，我们只需使用相同的别名和新的 trustedCertificate再次调用该方法。

## 6.阅读条目

现在我们已经写了一些条目，我们当然想阅读它们。

### 6.1. 读取单个条目

首先，我们可以通过别名提取密钥和证书：

```java
Key ssoSigningKey = ks.getKey("sso-signing-key", pwdArray);
Certificate google = ks.getCertificate("google.com");
```

如果没有该名称的条目，或者它属于不同的类型，则getKey 仅返回 null：

```java
public void whenEntryIsMissingOrOfIncorrectType_thenReturnsNull() {
    // ... initialize keystore
    // ... add an entry called "widget-api-secret"

   Assert.assertNull(ks.getKey("some-other-api-secret"));
   Assert.assertNotNull(ks.getKey("widget-api-secret"));
   Assert.assertNull(ks.getCertificate("widget-api-secret")); 
}
```

但是如果密钥的密码是错误的，我们会得到我们之前谈到的同样奇怪的错误：

```java
java.security.UnrecoverableKeyException: Given final block not properly padded
```

### 6.2. 检查密钥库是否包含别名

由于KeyStore仅使用Map存储条目 ，因此它可以在不检索条目的情况下检查是否存在：

```java
public void whenAddingAlias_thenCanQueryWithoutSaving() {
    // ... initialize keystore
    // ... add an entry called "widget-api-secret"
    assertTrue(ks.containsAlias("widget-api-secret"));
    assertFalse(ks.containsAlias("some-other-api-secret"));
}
```

### 6.3. 检查条目的种类

KeyStore #entryInstanceOf更强大一些。

它类似于containsAlias，除了它还会检查条目类型：

```java
public void whenAddingAlias_thenCanQueryByType() {
    // ... initialize keystore
    // ... add a secret entry called "widget-api-secret"
    assertTrue(ks.containsAlias("widget-api-secret"));
    assertFalse(ks.entryInstanceOf(
      "widget-api-secret",
      KeyType.PrivateKeyEntry.class));
}
```

## 7.删除条目

KeyStore当然 支持删除我们添加的条目：

```java
public void whenDeletingAnAlias_thenIdempotent() {
    // ... initialize a keystore
    // ... add an entry called "widget-api-secret"
    assertEquals(ks.size(), 1);
    ks.deleteEntry("widget-api-secret");
    ks.deleteEntry("some-other-api-secret");
    assertFalse(ks.size(), 0);
}
```

幸运的是，deleteEntry 是幂等的，因此无论条目是否存在，该方法的反应都是一样的。

## 8. 删除密钥库

如果我们想删除我们的密钥库，API 对我们没有帮助，但我们仍然可以使用Java来完成：

```java
Files.delete(Paths.get(keystorePath));
```

或者，作为替代方案，我们可以保留密钥库并只删除条目：

```java
Enumeration<String> aliases = keyStore.aliases();
while (aliases.hasMoreElements()) {
    String alias = aliases.nextElement();
    keyStore.deleteEntry(alias);
}
```

## 9.总结

在本文中，我们学习了如何使用KeyStore API管理证书和密钥。我们讨论了什么是密钥库，并探讨了如何创建、加载和删除密钥库。我们还演示了如何在密钥库中存储密钥或证书，以及如何使用新值加载和更新现有条目。