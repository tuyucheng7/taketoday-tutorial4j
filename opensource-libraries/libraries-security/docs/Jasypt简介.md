## 1. 概述

在本文中，我们将研究[Jasypt](http://www.jasypt.org/index.html) (Java 简化加密)库。

Jasypt 是一个Java库，它允许开发人员以最小的努力向项目添加基本的加密功能，而无需深入了解加密协议的实现细节。

## 2.使用简单加密

假设我们正在构建一个 Web 应用程序，用户可以在其中提交帐户私人数据。我们需要将该数据存储在数据库中，但存储纯文本是不安全的。

处理它的一种方法是将加密数据存储在数据库中，并在为特定用户检索该数据时对其进行解密。

要使用非常简单的算法执行加密和解密，我们可以使用 Jasypt 库中的[BasicTextEncryptor](http://www.jasypt.org/api/jasypt/1.8/org/jasypt/util/text/BasicTextEncryptor.html) 类：

```java
BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
String privateData = "secret-data";
textEncryptor.setPasswordCharArray("some-random-data".toCharArray());
```

然后我们可以使用encrypt()方法来加密明文：

```java
String myEncryptedText = textEncryptor.encrypt(privateData);
assertNotSame(privateData, myEncryptedText);
```

如果我们想在数据库中存储给定用户的私人数据，我们可以在不违反任何安全限制的情况下存储myEncryptedText 。如果我们想将数据解密回纯文本，我们可以使用decrypt()方法：

```java
String plainText = textEncryptor.decrypt(myEncryptedText);
 
assertEquals(plainText, privateData);
```

我们看到解密后的数据等同于之前加密的纯文本数据。

## 3.单向加密

前面的示例不是执行身份验证的理想方式，即当我们想要存储用户密码时。理想情况下，我们希望加密密码而无法解密它。当用户尝试登录我们的服务时，我们对其密码进行加密并将其与存储在数据库中的加密密码进行比较。这样我们就不需要对明文密码进行操作了。

我们可以使用BasicPasswordEncryptor类来执行单向加密：

```java
String password = "secret-pass";
BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
String encryptedPassword = passwordEncryptor.encryptPassword(password);

```

然后，我们可以将已经加密的密码与执行登录过程的用户密码进行比较，而无需解密已存储在数据库中的密码：

```java
boolean result = passwordEncryptor.checkPassword("secret-pass", encryptedPassword);

assertTrue(result);
```

## 4.配置加密算法

我们可以使用更强大的加密算法，但我们需要记住为我们的 JVM 安装[Java 加密扩展 (JCE) 无限强度管辖策略文件](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)(安装说明包含在下载中)。

在 Jasypt 中，我们可以通过使用StandardPBEStringEncryptor类来使用强加密，并使用setAlgorithm()方法对其进行自定义：

```java
StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
String privateData = "secret-data";
encryptor.setPassword("some-random-passwprd");
encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
```

让我们将加密算法设置为PBEWithMD5AndTripleDES。

接下来，加密和解密的过程看起来与之前使用BasicTextEncryptor类的过程相同：

```java
String encryptedText = encryptor.encrypt(privateData);
assertNotSame(privateData, encryptedText);

String plainText = encryptor.decrypt(encryptedText);
assertEquals(plainText, privateData);
```

## 5.使用多线程解密

当我们在多核机器上运行时，我们希望并行处理解密过程。为了获得良好的性能，我们可以使用[PooledPBEStringEncryptor](http://www.jasypt.org/api/jasypt/1.9.0/org/jasypt/encryption/pbe/PooledPBEStringEncryptor.html) 和setPoolSize() API 来创建摘要池。它们中的每一个都可以被不同的线程并行使用：

```java
PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
encryptor.setPoolSize(4);
encryptor.setPassword("some-random-data");
encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
```

最好将池大小设置为等于机器的核心数。加解密的代码和之前的一样。

## 6. 在其他框架中的使用

最后一点要注意的是，Jasypt库可以与许多其他库集成，当然包括Spring框架。

我们只需要创建一个配置来将加密支持添加到我们的 Spring 应用程序中。如果我们想将敏感数据存储到数据库中，并且我们使用Hibernate作为数据访问框架，我们还可以将Jasypt与其集成。

关于这些集成以及一些其他框架的说明，可以在[Jasypt 主页的](http://www.jasypt.org/)指南部分找到。

## 七. 总结

在本文中，我们研究了Jasypt库，它通过使用众所周知且经过测试的加密算法帮助我们创建更安全的应用程序。它包含易于使用的简单 API。