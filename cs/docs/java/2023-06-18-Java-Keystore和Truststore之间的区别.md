---
layout: post
title:  使用Selenium处理浏览器选项卡
category: java
copyright: java
excerpt: Java
---

## 1.概述

在本快速教程中，我们将概述Java密钥库和Java信任库之间的区别。

## 2.概念

在大多数情况下，当我们的应用程序需要通过SSL/TLS进行通信时，我们会使用密钥库和信任库。

通常，这些是受密码保护的文件，与我们正在运行的应用程序位于同一文件系统中。在Java 8之前，这些文件使用的默认格式是JKS。

从Java 9开始，默认的密钥库格式为PKCS12。JKS和PKCS12最大的不同在于，JKS是Java特有的一种格式，而PKCS12是一种标准化的、语言中立的加密私钥和证书存储方式。

## 3.Java密钥库

Java密钥库存储私钥条目、带有公钥的证书，或者只是我们可能用于各种加密目的的秘密密钥。它通过别名存储每个以便于查找。

一般来说，密钥库保存我们的应用程序拥有的密钥，我们可以用它来证明消息的完整性和发送者的真实性，比如通过签署有效负载。

通常，当我们是服务器并且想要使用HTTPS时，我们会使用密钥库。在SSL握手期间，服务器从密钥库中查找私钥，并将其对应的公钥和证书提供给客户端。

类似地，如果客户端也需要对自己进行身份验证，这种情况称为相互身份验证，那么客户端也有一个密钥库，并提供它的公钥和证书。

没有默认的密钥库，所以如果我们想使用加密通道，我们必须设置javax.net.ssl.keyStore和javax.net.ssl.keyStorePassword。如果我们的密钥库格式与默认格式不同，我们可以使用javax.net.ssl.keyStoreType来自定义它。

当然，我们也可以使用这些密钥来满足其他需求。私钥可以签名或解密数据，公钥可以验证或加密数据。秘密密钥也可以执行这些功能。密钥库是我们可以保存这些密钥的地方。

我们还可以[通过编程方式与密钥库进行交互](https://www.baeldung.com/java-keystore)。

## 4.Java信任库

信任库则相反。密钥库通常持有可识别我们身份的证书，而信任库则持有可识别他人身份的证书。

在Java中，我们使用它来信任我们即将与之通信的第三方。

以我们之前的例子为例。如果客户端通过HTTPS与基于Java的服务器通信，服务器将从其密钥库中查找关联的密钥，并将公钥和证书提供给客户端。

我们，客户端，然后在我们的信任库中查找关联的证书。如果外部服务器提供的证书或证书颁发机构不在我们的信任库中，我们将收到SSLHandshakeException，连接将无法成功建立。

Java捆绑了一个名为cacerts的信任库，它位于$JAVA_HOME/jre/lib/security目录中。

它包含默认的、受信任的证书颁发机构：

```plaintext
$ keytool -list -keystore cacerts
Enter keystore password:
Keystore type: JKS
Keystore provider: SUN

Your keystore contains 92 entries

verisignclass2g2ca [jdk], 2018-06-13, trustedCertEntry,
Certificate fingerprint (SHA1): B3:EA:C4:47:76:C9:C8:1C:EA:F2:9D:95:B6:CC:A0:08:1B:67:EC:9D
```

我们可以在这里看到信任库包含92个受信任的证书条目，其中一个条目是verisignclass2gca条目。这意味着JVM将自动信任由verisignclass2g2ca签名的证书。

我们可以通过javax.net.ssl.trustStore属性覆盖默认的信任库位置。同样，我们可以设置javax.net.ssl.trustStorePassword和javax.net.ssl.trustStoreType来指定信任库的密码和类型。

## 5.总结

在本文中，我们讨论了Java密钥库和Java信任库之间的主要区别，以及它们的用途。

我们还了解了如何使用系统属性覆盖默认值。

要深入了解Java中的加密通信，我们可以查看以下[SSL指南](https://www.baeldung.com/java-ssl)或[JSSE参考指南。](https://docs.oracle.com/en/java/javase/11/security/java-secure-socket-extension-jsse-reference-guide.html)
