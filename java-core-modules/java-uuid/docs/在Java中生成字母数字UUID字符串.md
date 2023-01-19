## 1. 概述

[UUID](https://www.baeldung.com/java-uuid)(Universally Unique Identifier)，也称为 GUID(Globally Unique Identifier)，是一个 128 位的值，对于所有实际用途来说都是唯一的。与大多数其他编号方案不同，它们的唯一性不依赖于中央注册机构或生成它们的各方之间的协调。

在本教程中，我们将看到在Java中生成 UUID 标识符的两种不同实现方法。

## 2.结构

让我们看一个示例 UUID，然后是 UUID 的规范表示：

```markdown
123e4567-e89b-42d3-a456-556642440000
xxxxxxxx-xxxx-Bxxx-Axxx-xxxxxxxxxxxx
```

标准表示由 32 个十六进制(base-16)数字组成，以连字符分隔的五组显示，形式为 8-4-4-4-12，共 36 个字符(32 个十六进制字符和 4 个连字符) .

Nil UUID 是 UUID 的一种特殊形式，其中所有位都为零。

### 2.1. 变体

在上面的标准表示中，A表示 UUID 变体，它决定了 UUID 的布局。UUID 中的所有其他位取决于变体字段中位的设置。

变体由A的三个最高有效位确定：

```markdown
  MSB1    MSB2    MSB3
   0       X       X     reserved (0)
   1       0       X     current variant (2)
   1       1       0     reserved for Microsoft (6)
   1       1       1     reserved for future (7)
```

上述UUID中A的值为“a”。“a”(=10xx) 的二进制等价物显示变体为 2。

### 2.1. 版本

再看标准表示，B代表版本。version 字段包含一个描述给定 UUID 类型的值。上面示例 UUID 中的版本(B的值)是 4。

UUID有五种不同的基本类型：

1.  版本 1(基于时间)：基于当前时间戳，从 1582 年 10 月 15 日开始以 100 纳秒为单位测量，与创建 UUID 的设备的 MAC 地址连接。
2.  版本 2(DCE – 分布式计算环境)：使用当前时间以及本地计算机上网络接口的 MAC 地址(或节点)。此外，版本 2 UUID 将时间字段的低位部分替换为本地标识符，例如创建 UUID 的本地帐户的用户 ID 或组 ID。
3.  版本 3(基于名称)：UUID 是使用命名空间和名称的哈希值生成的。命名空间标识符是 UUID，如域名系统 (DNS)、对象标识符 (OID) 和 URL。
4.  版本 4(随机生成)：在此版本中，UUID 标识符是随机生成的，不包含有关它们创建时间或生成它们的机器的任何信息。
5.  版本 5(使用 SHA-1 基于名称)：使用与版本 3 相同的方法生成，但散列算法有所不同。此版本使用命名空间标识符和名称的 SHA-1(160 位)哈希。

## 3. UUID类

Java 有一个内置的实现来管理 UUID 标识符，无论我们是想随机生成 UUID 还是使用构造函数创建它们。

UUID类有一个构造函数：

```java
UUID uuid = new UUID(long mostSignificant64Bits, long leastSignificant64Bits);
```

如果我们想使用这个构造函数，我们需要提供两个long值。但是，这需要我们自己构造 UUID 的位模式。

为方便起见，可以使用三种静态方法来创建UUID。

第一种方法从给定的字节数组创建一个版本 3 UUID：

```java
UUID uuid = UUID.nameUUIDFromBytes(byte[] bytes);
```

其次，randomUUID()方法创建一个版本 4 UUID。这是创建UUID实例最方便的方法：

```java
UUID uuid = UUID.randomUUID();
```

第三个静态方法在给定UUID的字符串表示形式的情况下返回一个 UUID 对象：

```java
UUID uuid = UUID.fromString(String uuidHexDigitString);
```

现在让我们看一下在不使用内置UUID类的情况下生成 UUID 的一些实现。

## 4.实施

我们将根据要求将实现分为两类。第一类是只需要唯一的标识符，为此，UUIDv1和UUIDv4是最佳选择。在第二类中，如果我们需要始终从给定名称生成相同的 UUID，我们将需要UUIDv3或UUIDv5。

由于 RFC 4122 未指定确切的生成细节，因此我们不会在本文中查看UUIDv2的实现。

现在让我们看看我们提到的类别的实现。

### 4.1. 版本 1 和 4

首先，如果隐私是一个问题，UUIDv1也可以用一个随机的 48 位数字而不是 MAC 地址生成。在本文中，我们将研究这种替代方案。

首先，我们将生成 64 个最低和最高有效位作为long值：

```java
private static long get64LeastSignificantBitsForVersion1() {
    long random63BitLong = new Random().nextLong() & 0x3FFFFFFFFFFFFFFFL;
    long variant3BitFlag = 0x8000000000000000L;
    return random63BitLong + variant3BitFlag;
}

private static long get64MostSignificantBitsForVersion1() {
    LocalDateTime start = LocalDateTime.of(1582, 10, 15, 0, 0, 0);
    Duration duration = Duration.between(start, LocalDateTime.now());
    long seconds = duration.getSeconds();
    long nanos = duration.getNano();
    long timeForUuidIn100Nanos = seconds  10000000 + nanos  100;
    long least12SignificantBitOfTime = (timeForUuidIn100Nanos & 0x000000000000FFFFL) >> 4;
    long version = 1 << 12;
    return (timeForUuidIn100Nanos & 0xFFFFFFFFFFFF0000L) + version + least12SignificatBitOfTime;
}
```

然后我们可以将这两个值传递给UUID的构造函数：

```java
public static UUID generateType1UUID() {

    long most64SigBits = get64MostSignificantBitsForVersion1();
    long least64SigBits = get64LeastSignificantBitsForVersion1();

    return new UUID(most64SigBits, least64SigBits);
}
```

我们现在将看到如何生成 UUIDv4。该实现使用随机数作为来源。Java 实现是SecureRandom，它使用不可预测的值作为种子来生成随机数，以减少发生冲突的机会。

让我们生成一个版本 4 UUID：

```markdown
UUID uuid = UUID.randomUUID();
```

然后，让我们使用“SHA-256”和随机UUID生成一个唯一密钥：

```markdown
MessageDigest salt = MessageDigest.getInstance("SHA-256");
salt.update(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
String digest = bytesToHex(salt.digest());
```

### 4.2. 版本 3 和 5

UUID 是使用命名空间和名称的哈希值生成的。命名空间标识符是 UUID，如域名系统 (DNS)、对象标识符 (OID) 和 URL。我们看一下算法的伪代码：

```markdown
UUID = hash(NAMESPACE_IDENTIFIER + NAME)
```

UUIDv3和UUIDv5之间的唯一区别是哈希算法 — v3 使用 MD5(128 位)，而 v5 使用 SHA-1(160 位)。

对于UUIDv3，我们将使用UUID类中的方法nameUUIDFromBytes(String namespace, String name)，它采用字节数组并应用 MD5 哈希。

因此，让我们首先从命名空间和特定名称中提取字节表示，并将它们连接到一个数组中以将其发送到 UUID api：

```java
byte[] nameSpaceBytes = bytesFromUUID(namespace);
byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
byte[] result = joinBytes(nameSpaceBytes, nameBytes);
```

最后一步是将从上一个过程中获得的结果传递给nameUUIDFromBytes()方法。此方法还将设置变体和版本字段：

```java
UUID uuid = UUID.nameUUIDFromBytes(result);
```

现在让我们看看UUIDv5的实现。重要的是要注意Java不提供生成版本 5 的内置实现。

让我们检查代码以生成最低和最高有效位，同样作为long值：

```java
private static long getLeastAndMostSignificantBitsVersion5(final byte[] src, final int offset) {
    long ans = 0;
    for (int i = offset + 7; i >= offset; i -= 1) {
        ans <<= 8;
        ans |= src[i] & 0xffL;
    }
    return ans;
}
```

现在，我们需要定义使用名称生成 UUID 的方法。此方法将使用UUID类中定义的默认构造函数：

```java
public static UUID generateType5UUID(String name) {

    try {

        byte[] bytes = name.getBytes(StandardCharsets.UTF_8);
        MessageDigest md = MessageDigest.getInstance("SHA-1");

        byte[] hash = md.digest(bytes);

        long msb = getLeastAndMostSignificantBitsVersion5(hash, 0);
        long lsb = getLeastAndMostSignificantBitsVersion5(hash, 8);
         // Set the version field
        msb &= ~(0xfL << 12);
        msb |= 5L << 12;
        // Set the variant field to 2
        lsb &= ~(0x3L << 62);
        lsb |= 2L << 62;
        return new UUID(msb, lsb);

    } catch (NoSuchAlgorithmException e) {
        throw new AssertionError(e);
    }
}
```

## 5.总结

在本文中，我们了解了有关 UUID 标识符的主要概念以及如何使用内置类生成它们。然后，我们看到了针对不同版本的 UUID 及其应用范围的一些高效实现。