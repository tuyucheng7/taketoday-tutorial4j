## 一、概述

在本教程中，我们将介绍 Java 平台上的安全基础知识。我们还将关注可用于编写安全应用程序的内容。

安全是**一个涵盖很多领域的广泛话题**。其中一些是语言本身的一部分，例如访问修饰符和类加载器。此外，其他服务可作为服务使用，其中包括数据加密、安全通信、身份验证和授权等。

因此，在本教程中获得对所有这些内容的有意义的洞察是不切实际的。但是，我们将尝试至少获得有意义的词汇。

## 二、语言特点

最重要的是，**Java 中的安全性从语言特性级别开始**。这使我们能够编写安全代码，并从许多隐式安全功能中受益：

-   静态数据类型化：Java 是一种静态类型化语言，它**减少了运行时检测类型相关错误的可能性**
-   访问修饰符：Java 允许我们使用**不同的访问修饰符，如 public 和 private 来控制对字段、方法和类的访问**
-   自动内存管理：Java 具有**基于垃圾回收的内存管理**，使开发人员无需手动管理
-   字节码验证：Java 是一种编译语言，这意味着它将代码转换为与平台无关的字节码，**运行时验证它加载的每个字节码以供执行**

这不是 Java 提供的安全功能的完整列表，但足以给我们一些保证！

## 3. Java 中的安全架构

在我们开始探索特定领域之前，让我们花一些时间了解 Java 中安全性的核心架构。

Java 中安全的核心原则是由**可互操作和可扩展的\*Provider\*实现**驱动的。*Provider*的特定实现可以实现部分或全部安全服务。

*例如，提供者*可能实现的一些典型服务是：

-   加密算法（例如 DSA、RSA 或 SHA-256）
-   密钥生成、转换和管理工具（例如特定于算法的密钥）

Java 附带**许多内置提供程序**。此外，应用程序可以按优先顺序配置多个提供程序。

[![Java 供应商](https://www.baeldung.com/wp-content/uploads/2019/09/Java-Providers.jpg)](https://www.baeldung.com/wp-content/uploads/2019/09/Java-Providers.jpg)

 

因此，Java 中的提供者框架**按照对**它们设置的偏好顺序在所有提供者中搜索服务的特定实现。

此外，始终可以在此体系结构中实现具有可插入安全功能的自定义提供程序。

## 4.密码学

密码学是一般安全功能和 Java 中安全功能的基石。这是指**在对手面前进行安全通信的工具和技术**。

### 4.1. Java密码学

Java[加密体系结构 (JCA)](https://docs.oracle.com/javase/9/security/java-cryptography-architecture-jca-reference-guide.htm)提供了一个框架来访问和实现 Java 中的加密功能，包括：

-   数字签名
-   [消息摘要](https://www.baeldung.com/java-password-hashing)
-   [对称和非对称密码](https://www.baeldung.com/java-cipher-class)
-   消息认证码
-   密钥生成器和密钥工厂

最重要的是，Java 使用[基于](https://docs.oracle.com/javase/9/security/howtoimplaprovider.htm)[*Provider 的*](https://docs.oracle.com/javase/9/security/howtoimplaprovider.htm)加密函数实现。

此外，Java 包括用于常用加密算法（例如 RSA、DSA 和 AES 等）的内置提供程序。我们可以使用这些算法为静止、使用中或运动中的**数据增加安全性。**

### 4.2. 密码学实践

应用程序中一个非常常见的用例是存储用户密码。我们稍后会使用它进行身份验证。现在，很明显存储纯文本密码会危及安全性。

因此，一种解决方案是以这种过程可重复但仅单向的方式对密码进行加扰。这个过程被称为加密哈希函数，SHA1 就是这样一种流行的算法。

那么，让我们看看如何在 Java 中做到这一点：

```java
MessageDigest md = MessageDigest.getInstance("SHA-1");
byte[] hashedPassword = md.digest("password".getBytes());复制
```

在这里，*MessageDigest*是我们感兴趣的加密服务。我们使用***getInstance\*** **()方法从任何可用的安全提供程序请求此服务**。

## 5.公钥基础设施

公钥基础设施 (PKI) 是指**使用公钥加密在网络上安全交换信息的设置**。此设置依赖于通信各方之间建立的信任。这种信任基于由中立且受信任的权威机构（称为证书颁发机构 (CA)）颁发的数字证书。

### 5.1. Java 中的 PKI 支持

Java 平台有 API 来[促进数字证书的创建、存储和验证](https://docs.oracle.com/javase/9/security/java-pki-programmers-guide.htm)：

-   [*KeyStore*](https://www.baeldung.com/java-keystore)：Java 提供了*KeyStore*类用于持久存储加密密钥和可信证书。在这里， ***KeyStore\*可以代表 key-store 和 trust-store files**。这些文件具有相似的内容，但用途不同。
-   *CertStore*：此外，Java 具有*CertStore*类，它表示可能不受信任的证书和吊销列表的公共存储库。我们需要检索证书和吊销列表，**以便在其他用途中构建证书路径**。

Java 有一个**名为“cacerts”的内置信任库**，其中包含众所周知的 CA 的证书。

### 5.2. PKI 的 Java 工具

Java 有一些非常方便的工具来促进可信通信：

-   有一个名为“keytool”的内置工具来创建和管理密钥库和信任库
-   还有另一个工具“jarsigner”，我们可以使用它来签署和验证 JAR 文件

### 5.3. 在 Java 中使用证书

让我们看看如何使用 Java 中的证书来使用 SSL 建立安全连接。相互认证的 SSL 连接需要我们做两件事：

-   出示证书——我们需要向通信中的另一方出示有效证书。为此，我们需要加载密钥库文件，其中必须有我们的公钥：

```java
KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
char[] keyStorePassword = "changeit".toCharArray();
try(InputStream keyStoreData = new FileInputStream("keystore.jks")){
    keyStore.load(keyStoreData, keyStorePassword);
}复制
```

-   验证证书——我们还需要验证另一方在通信中提供的证书。为此，我们需要加载信任库，我们必须先在其中信任来自其他方的证书：

```java
KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
// Load the trust-store from filesystem as before复制
```

我们很少需要以编程方式执行此操作，并且通常在运行时将系统参数传递给 Java：

```powershell
-Djavax.net.ssl.trustStore=truststore.jks 
-Djavax.net.ssl.keyStore=keystore.jks复制
```

## 6.认证

身份验证是根据密码、令牌或当今可用的各种其他凭证等附加数据来**验证用户或机器的身份的过程。**

### 6.1. Java 中的身份验证

Java API 使用[可插入的登录模块](https://docs.oracle.com/javase/9/security/java-authentication-and-authorization-service-jaas-loginmodule-developers-guide1.htm)为应用程序提供不同且通常是多种身份验证机制。*LoginContext*提供了这个抽象，它反过来引用配置并加载适当的*LoginModule*。

虽然多个供应商提供了他们的登录模块，但**Java 有一些默认的可供使用**：

-   *Krb5LoginModule*，用于基于 Kerberos 的身份验证
-   *JndiLoginModule*，用于由 LDAP 存储支持的基于用户名和密码的身份验证
-   *KeyStoreLoginModule*，用于基于密钥的身份验证

### 6.2. 实例登录

最常见的身份验证机制之一是用户名和密码。*让我们看看如何通过JndiLoginModule*实现这一点。

该模块负责从用户处获取用户名和密码，并根据 JNDI 中配置的目录服务对其进行验证：

```java
LoginContext loginContext = new LoginContext("Sample", new SampleCallbackHandler());
loginContext.login();复制
```

在这里，我们使用***LoginContext\*****的实例来执行登录**。*LoginContext*采用登录配置中条目的名称——在本例中为“Sample”。此外，我们必须提供一个*CallbackHandler*实例，使用*LoginModule*与用户交互以获取用户名和密码等详细信息。

让我们看一下我们的登录配置：

```powershell
Sample {
  com.sun.security.auth.module.JndiLoginModule required;
};复制
```

很简单，它表明我们将*JndiLoginModule*用作必需的*LoginModule*。

## 7. 安全通信

通过网络进行的通信容易受到许多攻击媒介的攻击。例如，有人可能会接入网络并在传输时读取我们的数据包。多年来，业界已经建立了许多协议来保护这种通信。

### 7.1. Java 对安全通信的支持

Java 提供[API 来](https://docs.oracle.com/javase/9/security/java-secure-socket-extension-jsse-reference-guide.htm)通过**加密、消息完整性以及客户端和服务器身份验证**来保护网络通信：

-   SSL/TLS：SSL 及其后继者 TLS 通过数据加密和公钥基础设施为不受信任的网络通信提供安全性。Java 通过包“ *java.security.ssl ”中定义的**SSLSocket*提供对 SSL/TLS 的支持。
-   SASL：简单身份验证和安全层 (SASL) 是客户端和服务器之间的身份验证标准。Java 支持 SASL 作为包“ *java.security.sasl* ”的一部分。
-   GGS-API/Kerberos：通用安全服务 API (GSS-API) 通过各种安全机制（如 Kerberos v5）提供对安全服务的统一访问。Java 支持 GSS-API 作为包“ *java.security.jgss* ”的一部分。

### 7.2. SSL 通信实战

现在让我们看看如何使用[*SSLSocket*](https://www.baeldung.com/java-ssl)[在Java](https://www.baeldung.com/java-ssl)中打开与其他方的安全连接：

```java
SocketFactory factory = SSLSocketFactory.getDefault();
try (Socket connection = factory.createSocket(host, port)) {
    BufferedReader input = new BufferedReader(
      new InputStreamReader(connection.getInputStream()));
    return input.readLine();
}复制
```

在这里，我们使用*SSLSocketFactory*来创建*SSLSocket*。作为其中的一部分，我们可以设置可选参数，例如密码套件和要使用的协议。

为了使其正常工作，我们必须像之前看到的那样**创建并设置我们的密钥库和信任库。**

## 8.访问控制

访问控制是指**保护敏感资源（如文件系统**或代码库）免受未经授权的访问。这通常是通过限制对此类资源的访问来实现的。

### 8.1. Java 中的访问控制

我们可以使用**通过*****SecurityManager\*****类调解的****类*****Policy\*****和*****Permission\***[在 Java 中实现访问控制](https://docs.oracle.com/en/java/javase/11/security/java-authentication-and-authorization-service-jaas-reference-guide.html) 。*SecurityManager是“* *java.lang* ”包的一部分，负责在 Java 中执行访问控制检查。

当类加载器在运行时加载一个类时，它会自动授予封装在*Permission*对象中的类一些默认权限。除了这些默认权限之外，我们还可以通过安全策略授予类更多的影响力。这些由类*Policy*表示。

在代码执行序列期间，如果运行时遇到对受保护资源的请求，***SecurityManager\***会通过调用堆栈**根据已安装的*****策略\*****验证请求的*****权限。\***因此，它要么授予权限，要么抛出*SecurityException*。

### 8.2. Java 策略工具

*Java 有一个默认的Policy*实现，它从属性文件中读取授权数据。但是，这些策略文件中的策略条目必须采用特定格式。

Java 附带“policytool”，这是一种用于编写策略文件的图形实用程序。

### 8.3. 通过示例进行访问控制

让我们看看如何在 Java 中限制对资源（如文件）的访问：

```java
SecurityManager securityManager = System.getSecurityManager();
if (securityManager != null) {
    securityManager.checkPermission(
      new FilePermission("/var/logs", "read"));
}复制
```

在这里，我们使用*SecurityManager*来验证我们对文件的读取请求，包装在*FilePermission*中。

但是，*SecurityManager*将此请求委托给*AccessController*。*AccessController*在内部使用已安装的*策略*来做出决定。

让我们看一个策略文件的例子：

```powershell
grant {
  permission 
    java.security.FilePermission
      <<ALL FILES>>, "read";
};复制
```

我们实质上是为每个人授予对所有文件的读取权限。但是，**我们可以通过安全策略提供更细粒度的控制**。

值得注意的是，在 Java 中可能不会默认安装*SecurityManager 。*我们可以通过始终使用以下参数启动 Java 来确保这一点：

```powershell
-Djava.security.manager -Djava.security.policy=/path/to/sample.policy复制
```

## 9. XML 签名

XML 签名**在保护数据和提供数据完整性方面很有用**。W3C 提供了 XML 签名治理的建议。我们可以使用 XML 签名来保护任何类型的数据，例如二进制数据。

### 9.1. Java 中的 XML 签名

Java API 支持根据推荐指南[生成和验证 XML 签名。](https://docs.oracle.com/javase/9/security/xml-digital-signature1.htm)Java XML 数字签名 API 封装在“ *java.xml.crypto* ”包中。

签名本身只是一个 XML 文档。XML 签名可以分为三种类型：

-   分离的：这种类型的签名位于 Signature 元素外部的数据之上
-   包络：这种类型的签名覆盖在 Signature 元素内部的数据上
-   Enveloped：这种类型的签名覆盖在包含 Signature 元素本身的数据上

当然，Java 支持创建和验证上述所有类型的 XML 签名。

### 9.2. 创建 XML 签名

现在，我们将卷起袖子为我们的数据生成一个 XML 签名。例如，我们可能要通过网络发送 XML 文档。因此，**我们希望我们的接收者能够验证其完整性**。

那么，让我们看看如何在 Java 中实现这一点：

```java
XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");
DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
documentBuilderFactory.setNamespaceAware(true);
 
Document document = documentBuilderFactory
  .newDocumentBuilder().parse(new FileInputStream("data.xml"));
 
DOMSignContext domSignContext = new DOMSignContext(
  keyEntry.getPrivateKey(), document.getDocumentElement());
 
XMLSignature xmlSignature = xmlSignatureFactory.newXMLSignature(signedInfo, keyInfo);
xmlSignature.sign(domSignContext);复制
```

为澄清起见，我们正在为文件*“data.xml”中的数据生成一个 XML 签名。*同时，这段代码有几点需要注意：

-   首先，*XMLSignatureFactory*是生成XML签名的工厂类
-   *XMLSigntaure*需要一个*SignedInfo*对象，用于计算签名
-   *XMLSigntaure*还需要*KeyInfo*，它封装了签名密钥和证书
-   最后，*XMLSignature使用封装为**DOMSignContext 的*私钥对文档进行签名

结果，**XML 文档现在将包含 Signature 元素**，可用于验证其完整性。

## 10. 超越核心 Java 的安全性

正如我们现在所看到的，Java 平台提供了许多编写安全应用程序所必需的功能。然而，有时，这些都是非常低级的，不能直接应用于，例如，网络上的标准安全机制。

例如，在我们的系统上工作时，**我们通常不想阅读完整的 OAuth RFC 并自己实施它**。我们通常需要更快、更高级别的方法来实现安全性。这就是应用程序框架发挥作用的地方——它们帮助我们用更少的样板代码实现我们的目标。

而且，在 Java 平台上——**通常这意味着 Spring Security**。该框架是 Spring 生态系统的一部分，但实际上可以在纯 Spring 应用程序之外使用。

简而言之，它有助于以简单、声明性的高级方式实现身份验证、授权和其他安全功能。

当然，Spring Security在[Learn Spring Security 课程的](https://www.baeldung.com/learn-spring-security-course)[一系列教程](https://www.baeldung.com/security-spring)中以及以指导方式进行了广泛介绍。

## 11.结论

简而言之，在本教程中，我们了解了 Java 中的高级安全架构。此外，我们了解了 Java 如何为我们提供一些标准加密服务的实现。

我们还看到了一些常见的模式，我们可以应用这些模式在身份验证和访问控制等领域实现可扩展和可插入的安全性。

综上所述，这只是让我们先睹为快，了解 Java 的安全特性。因此，本教程中讨论的每个领域都值得进一步探索。但希望我们应该有足够的洞察力开始朝这个方向发展！