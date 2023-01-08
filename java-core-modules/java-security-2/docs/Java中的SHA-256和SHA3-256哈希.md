## 1. 概述

SHA(安全哈希算法)是流行的加密哈希函数之一。加密散列可用于为文本或数据文件制作签名。

在本教程中，让我们看看如何使用各种Java库执行 SHA-256 和 SHA3-256 哈希运算。

SHA [-256](https://en.wikipedia.org/wiki/SHA-2)算法生成几乎唯一的、固定大小的 256 位(32 字节)散列。这是一个单向函数，因此无法将结果解密回原始值。

目前，SHA-2 哈希被广泛使用，因为它被认为是密码学领域中最安全的哈希算法。

[SHA-3](https://en.wikipedia.org/wiki/SHA-3)是继 SHA-2 之后最新的安全散列标准。与 SHA-2 相比，SHA-3 提供了一种不同的方法来生成唯一的单向散列，并且在某些硬件实现上速度更快。SHA3-256 与 SHA-256 类似，是 SHA-3 中的 256 位定长算法。

[NIST](https://csrc.nist.gov/projects/hash-functions)于 2015 年发布了 SHA-3，因此目前 SHA-3 库的数量不如 SHA-2。直到 JDK 9，SHA-3 算法才在内置的默认提供程序中可用。

现在让我们从 SHA-256 开始。

## 延伸阅读：

## [使用 Java-LSH 在Java中进行局部敏感散列](https://www.baeldung.com/locality-sensitive-hashing)

使用 java-lsh 库在Java中应用局部敏感哈希算法的快速实用指南。

[阅读更多](https://www.baeldung.com/locality-sensitive-hashing)→

## [Java 中的 MD5 哈希](https://www.baeldung.com/java-md5)

一篇简短的文章向你展示了如何在Java中处理 MD5 散列。

[阅读更多](https://www.baeldung.com/java-md5)→

## [Java HashSet 指南](https://www.baeldung.com/java-hashset)

对Java中的 HashSet 的快速而全面的介绍。

[阅读更多](https://www.baeldung.com/java-hashset)→

## 2. Java中的MessageDigest类

Java为 SHA-256 哈希提供了内置的MessageDigest类：

```java
MessageDigest digest = MessageDigest.getInstance("SHA-256");
byte[] encodedhash = digest.digest(
  originalString.getBytes(StandardCharsets.UTF_8));
```

但是，这里我们必须使用自定义字节到十六进制转换器来获取十六进制的哈希值：

```java
private static String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder(2  hash.length);
    for (int i = 0; i < hash.length; i++) {
        String hex = Integer.toHexString(0xff & hash[i]);
        if(hex.length() == 1) {
            hexString.append('0');
        }
        hexString.append(hex);
    }
    return hexString.toString();
}
```

我们需要知道 MessageDigest不是线程安全的。因此，我们应该为每个线程使用一个新实例。

## 3.番石榴图书馆

Google Guava 库还提供了一个用于散列的实用程序类。

首先，让我们定义依赖关系：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

接下来，我们可以使用 Guava 对字符串进行哈希处理：

```java
String sha256hex = Hashing.sha256()
  .hashString(originalString, StandardCharsets.UTF_8)
  .toString();
```

## 4.Apache Commons 编解码器

同样，我们也可以使用 Apache Commons Codecs：

```xml
<dependency>
    <groupId>commons-codec</groupId>
    <artifactId>commons-codec</artifactId>
    <version>1.11</version>
</dependency>
```

这是支持 SHA-256 散列的实用程序类 — 称为DigestUtils ：

```java
String sha256hex = DigestUtils.sha256Hex(originalString);
```

## 5.充气城堡图书馆

### 5.1. Maven 依赖

```xml
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk15on</artifactId>
    <version>1.60</version>
</dependency>
```

### 5.2. 使用 Bouncy Castle 库进行散列

Bouncy Castle API 提供了一个实用程序类，用于将十六进制数据转换为字节，然后再转换回来。

但是，我们需要先使用内置的JavaAPI 填充摘要：

```java
MessageDigest digest = MessageDigest.getInstance("SHA-256");
byte[] hash = digest.digest(
  originalString.getBytes(StandardCharsets.UTF_8));
String sha256hex = new String(Hex.encode(hash));
```

## 6. SHA3-256

现在让我们继续使用 SHA3-256。Java 中的 SHA3-256 哈希与 SHA-256 没有什么不同。

### 6.1.Java中的MessageDigest类

[从 JDK 9 开始](https://docs.oracle.com/javase/9/security/oracleproviders.htm#JSSEC-GUID-3A80CC46-91E1-4E47-AC51-CB7B782CEA7D)，我们可以简单地使用内置的 SHA3-256 算法：

```java
final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
final byte[] hashbytes = digest.digest(
  originalString.getBytes(StandardCharsets.UTF_8));
String sha3Hex = bytesToHex(hashbytes);
```

### 6.2. Apache Commons 编解码器

Apache Commons Codecs为MessageDigest类提供了一个方便的DigestUtils包装器。

[这个库从1.11](https://search.maven.org/artifact/commons-codec/commons-codec/1.11/jar)版本开始支持 SHA3-256 ，并且它也[需要 JDK 9+ ：](https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/digest/MessageDigestAlgorithms.html#SHA3_256)

```java
String sha3Hex = new DigestUtils("SHA3-256").digestAsHex(originalString);
```

### 6.3. Keccak-256

Keccak-256 是另一种流行的 SHA3-256 哈希算法。目前，它作为标准 SHA3-256 的替代品。Keccak-256 提供与标准 SHA3-256 相同的安全级别，它与 SHA3-256 的区别仅在于填充规则。它已被用于多个区块链项目，例如[Monero](https://monerodocs.org/cryptography/keccak-256/)。

同样，我们需要导入 Bouncy Castle 库以使用 Keccak-256 哈希：

```java
Security.addProvider(new BouncyCastleProvider());
final MessageDigest digest = MessageDigest.getInstance("Keccak-256");
final byte[] encodedhash = digest.digest(
  originalString.getBytes(StandardCharsets.UTF_8));
String sha3Hex = bytesToHex(encodedhash);
```

我们还可以使用 Bouncy Castle API 来进行散列：

```java
Keccak.Digest256 digest256 = new Keccak.Digest256();
byte[] hashbytes = digest256.digest(
  originalString.getBytes(StandardCharsets.UTF_8));
String sha3Hex = new String(Hex.encode(hashbytes));
```

## 七、总结

在这篇简短的文章中，我们了解了使用内置库和第三方库在Java中实现 SHA-256 和 SHA3-256 哈希的几种方法。