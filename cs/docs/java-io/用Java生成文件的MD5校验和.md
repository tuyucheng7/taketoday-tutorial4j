## 1. 概述

校验和是用于唯一标识文件的字符序列。它最常用于验证文件的副本是否与原始文件相同。

在这个简短的教程中，我们将了解如何使用 Java为文件生成 MD5 校验和。

## 2.使用MessageDigest类

我们可以轻松地使用java.security包中的MessageDigest类来生成文件的 MD5 校验和：

```java
byte[] data = Files.readAllBytes(Paths.get(filePath));
byte[] hash = MessageDigest.getInstance("MD5").digest(data);
String checksum = new BigInteger(1, hash).toString(16);
```

## 3. 使用 Apache Commons 编解码器

我们还可以使用[Apache Commons Codec库中的](https://search.maven.org/search?q=g:commons-codec)DigestUtils类来实现相同的目标。

让我们向我们的pom.xml文件添加一个依赖项：

```xml
<dependency>
    <groupId>commons-codec</groupId>
    <artifactId>commons-codec</artifactId>
    <version>1.15</version>
</dependency>
```

现在，我们只需使用md5Hex()方法来获取文件的 MD5 校验和：

```java
try (InputStream is = Files.newInputStream(Paths.get(filePath))) {
    String checksum = DigestUtils.md5Hex(is);
    // ....
}
```

我们不要忘记使用[try-with-resources](https://www.baeldung.com/java-try-with-resources)这样我们就不必担心关闭流。

## 4.使用番石榴

最后，我们可以使用[Guava](https://www.baeldung.com/guava-guide)的ByteSource对象的hash()方法：

```java
File file = new File(filePath);
ByteSource byteSource = com.google.common.io.Files.asByteSource(file);
HashCode hc = byteSource.hash(Hashing.md5());
String checksum = hc.toString();
```

## 5.总结

在本快速教程中，我们展示了用 Java为文件生成 MD5 校验和的不同方法。