## 1. 概述

MD5 是一种广泛使用的加密哈希函数，可生成 128 位的哈希值。

在本文中，我们将看到使用各种Java库创建 MD5 哈希的不同方法。

## 2. MD5 使用MessageDigest类

java.security.MessageDigest类中有[散列](https://www.baeldung.com/cs/hashing)功能。这个想法是首先使用你想要用作参数的算法类型实例化MessageDigest ：

```java
MessageDigest.getInstance(String Algorithm)
```

然后使用update()函数继续更新消息摘要：

```java
public void update(byte [] input)
```

当你正在读取一个长文件时，可以多次调用上面的函数。然后最后我们需要使用digest()函数来生成哈希码：

```java
public byte[] digest()
```

下面是一个为密码生成哈希值然后验证它的示例：

```java
@Test
public void givenPassword_whenHashing_thenVerifying() 
  throws NoSuchAlgorithmException {
    String hash = "35454B055CC325EA1AF2126E27707052";
    String password = "ILoveJava";
        
    MessageDigest md = MessageDigest.getInstance("MD5");
    md.update(password.getBytes());
    byte[] digest = md.digest();
    String myHash = DatatypeConverter
      .printHexBinary(digest).toUpperCase();
        
    assertThat(myHash.equals(hash)).isTrue();
}
```

同样，我们也可以验证一个文件的校验和：

```java
@Test
public void givenFile_generatingChecksum_thenVerifying() 
  throws NoSuchAlgorithmException, IOException {
    String filename = "src/test/resources/test_md5.txt";
    String checksum = "5EB63BBBE01EEED093CB22BB8F5ACDC3";
        
    MessageDigest md = MessageDigest.getInstance("MD5");
    md.update(Files.readAllBytes(Paths.get(filename)));
    byte[] digest = md.digest();
    String myChecksum = DatatypeConverter
      .printHexBinary(digest).toUpperCase();
        
    assertThat(myChecksum.equals(checksum)).isTrue();
}
```

我们需要注意， MessageDigest不是线程安全的。因此，我们应该为每个线程使用一个新实例。

## 3. MD5 使用 Apache Commons

org.apache.commons.codec.digest.DigestUtils类使事情变得简单得多。

让我们看一个散列和验证密码的例子：

```java
@Test
public void givenPassword_whenHashingUsingCommons_thenVerifying()  {
    String hash = "35454B055CC325EA1AF2126E27707052";
    String password = "ILoveJava";

    String md5Hex = DigestUtils
      .md5Hex(password).toUpperCase();
        
    assertThat(md5Hex.equals(hash)).isTrue();
}
```

## 4. MD5 使用 Guava

下面是我们可以遵循的另一种使用com.google.common.io.Files.hash生成 MD5 校验和的方法：

```java
@Test
public void givenFile_whenChecksumUsingGuava_thenVerifying() 
  throws IOException {
    String filename = "src/test/resources/test_md5.txt";
    String checksum = "5EB63BBBE01EEED093CB22BB8F5ACDC3";
        
    HashCode hash = com.google.common.io.Files
      .hash(new File(filename), Hashing.md5());
    String myChecksum = hash.toString()
      .toUpperCase();
        
    assertThat(myChecksum.equals(checksum)).isTrue();
}
```

请注意，Hashing.md5已弃用。然而，正如[官方文档](https://guava.dev/releases/23.0/api/docs/com/google/common/hash/Hashing.html#md5--)所指出的那样，出于安全考虑，一般情况下建议不要使用 MD5。这意味着如果我们需要与需要 MD5 的遗留系统集成，我们仍然可以使用此方法。否则，我们最好考虑更安全的选择，例如[SHA-256](https://www.baeldung.com/sha-256-hashing-java)。

## 5.总结

Java API 和其他第三方 API(如 Apache commons 和 Guava)中有不同的方法来生成 MD5 哈希。根据项目的要求和项目需要遵循的依赖关系明智地选择。