## 1. 概述

让我们考虑一个场景，其中两方想要通信，他们需要一种方法来验证他们收到的消息没有被篡改。[基于哈希的消息认证码](https://en.wikipedia.org/wiki/HMAC)(HMAC) 是一个很好的解决方案。

在本教程中，我们将了解如何在Java中使用 HMAC 算法。

## 2. 哈希消息认证码(HMAC)

HMAC 是一种密码学方法，可以保证双方之间消息的完整性。

HMAC 算法由一个密钥和一个散列函数组成。密钥是一条唯一的信息或一串字符。消息的发送者和接收者都知道它。

哈希函数是一种将一个序列转换为另一个序列的映射算法。

下图显示了高级 HMAC 算法：

[![java中的hmac](https://www.baeldung.com/wp-content/uploads/2021/11/hmac-in-java.png)](https://www.baeldung.com/wp-content/uploads/2021/11/hmac-in-java.png)

HMAC 使用加密哈希函数，例如[MD5](https://www.baeldung.com/java-md5)和[SHA-](https://www.baeldung.com/sha-256-hashing-java)。

## 3. HMAC 使用 JDK API

Java 为 HMAC 生成提供了一个内置的[Mac类](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/javax/crypto/Mac.html)。初始化Mac对象后，我们调用doFinal()方法进行HMAC操作。此方法返回包含 HMAC 结果的字节数组。

让我们定义一种使用各种哈希算法(例如 MD5、SHA-1、SHA-224、SHA-256、SHA-384 和 SHA-512)计算 HMAC 的方法：

```java
public static String hmacWithJava(String algorithm, String data, String key)
  throws NoSuchAlgorithmException, InvalidKeyException {
    SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), algorithm);
    Mac mac = Mac.getInstance(algorithm);
    mac.init(secretKeySpec);
    return bytesToHex(mac.doFinal(data.getBytes()));
}
```

下面写一个例子测试来说明HMAC的计算：

```java
@Test
public void givenDataAndKeyAndAlgorithm_whenHmacWithJava_thenSuccess()
    throws NoSuchAlgorithmException, InvalidKeyException {

    String hmacSHA256Value = "5b50d80c7dc7ae8bb1b1433cc0b99ecd2ac8397a555c6f75cb8a619ae35a0c35";
    String hmacSHA256Algorithm = "HmacSHA256";
    String data = "baeldung";
    String key = "123456";

    String result = HMACUtil.hmacWithJava(hmacSHA256Algorithm, data, key);

    assertEquals(hmacSHA256Value, result);
}
```

在此测试中，我们使用具有简单字符串数据和密钥的HmacSHA512算法。然后，我们断言 HMAC 结果等于预期数据。

## 4.Apache 公共库

[Apache Commons 库](https://commons.apache.org/)还提供了一个用于 HMAC 计算的实用程序类。

### 4.1. 添加 Maven 依赖

要使用 Apache Commons 实用程序类，我们需要将[commons-codec](https://search.maven.org/search?q=g:commons-codec)添加到我们的 pom.xml 中：

```xml
<dependency>
    <groupId>commons-codec</groupId>
    <artifactId>commons-codec</artifactId>
    <version>1.15</version>
</dependency>

```

### 4.2. HmacUtils类

为了计算 HMAC，我们可以使用HmacUtils类。初始化HmacUtils对象后，我们调用hmacHex()方法执行 HMAC 操作。此方法返回包含 HMAC 结果的十六进制字符串。

让我们创建一个生成 HMAC 的方法：

```java
public static String hmacWithApacheCommons(String algorithm, String data, String key) {
    String hmac = new HmacUtils(algorithm, key).hmacHex(data);
    return hmac;
}
```

让我们写一个示例测试：

```java
@Test
public void givenDataAndKeyAndAlgorithm_whenHmacWithApacheCommons_thenSuccess() {

    String hmacMD5Value = "621dc816b3bf670212e0c261dc9bcdb6";
    String hmacMD5Algorithm = "HmacMD5";
    String data = "baeldung";
    String key = "123456";

    String result = HMACUtil.hmacWithApacheCommons(hmacMD5Algorithm, data, key);

    assertEquals(hmacMD5Value, result);
}
```

在此测试中，我们使用HmacMD5算法。

## 5. BouncyCastle 图书馆

同样，我们也可以使用[BouncyCastle](https://www.baeldung.com/java-bouncy-castle)库。BouncyCastle 是我们可以在Java中使用的加密 API 的集合。

### 5.1. 添加 Maven 依赖

在我们开始使用该库之前，我们需要将[bcpkix-jdk15to18](https://search.maven.org/search?q=a:bcpkix-jdk15to18)依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcpkix-jdk15to18</artifactId>
    <version>1.69</version>
</dependency>
```

### 5.2. Hmac类

我们将首先根据要使用的散列算法实例化HMac类。然后我们将使用update()方法用输入数据更新 HMAC 对象。最后，我们将调用doFinal()方法来生成 HMAC 代码：

```java
public static String hmacWithBouncyCastle(String algorithm, String data, String key) {
    Digest digest = getHashDigest(algorithm);

    HMac hMac = new HMac(digest);
    hMac.init(new KeyParameter(key.getBytes()));

    byte[] hmacIn = data.getBytes();
    hMac.update(hmacIn, 0, hmacIn.length);
    byte[] hmacOut = new byte[hMac.getMacSize()];

    hMac.doFinal(hmacOut, 0);
    return bytesToHex(hmacOut);
}

private static Digest getHashDigest(String algorithm) {
    switch (algorithm) {
      case "HmacMD5":
        return new MD5Digest();
      case "HmacSHA256":
        return new SHA256Digest();
      case "HmacSHA384":
        return new SHA384Digest();
      case "HmacSHA512":
        return new SHA512Digest();
    }
    return new SHA256Digest();
}
```

下面是一个为字符串数据生成 HMAC 然后对其进行验证的示例：

```java
@Test
public void givenDataAndKeyAndAlgorithm_whenHmacWithBouncyCastle_thenSuccess() {

    String hmacSHA512Value = "b313a21908df55c9e322e3c65a4b0b7561ab1594ca806b3affbc0d769a1" +
      "290c1922aa6622587bea3c0c4d871470a6d06f54dbd20dbda84250e2741eb01f08e33";
    String hmacSHA512Algorithm = "HmacSHA512";
    String data = "baeldung";
    String key = "123456";

    String result = HMACUtil.hmacWithBouncyCastle(hmacSHA512Algorithm, data, key);

    assertEquals(hmacSHA512Value, result);
}
```

在此测试中，我们使用HmacSHA512算法。

## 六，总结

HMAC 提供数据完整性检查。在本文中，我们学习了如何使用Java中的 HMAC 算法为输入字符串数据生成 HMAC。此外，我们还讨论了 Apache Commons 和 BouncyCastle 库在 HMAC 计算中的使用。