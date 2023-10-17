## 1. 概述

在本文中，我们将了解[UUID](https://www.ietf.org/rfc/rfc4122.txt)(通用唯一标识符)代码，有时也称为 GUID(全局唯一标识符)。简而言之，它是一个以“-”分隔的十六进制字符形式的 128 位长数字：

```java
e58ed763-928c-4155-bee9-fdbaaadc15f3
```

一个标准的 UUID 代码包含 32 个十六进制数字和 4 个“-”符号，这使得它的长度等于 36 个字符。还有一个 Nil UUID 代码，其中所有位都设置为零。

在这里，我们将看看Java 中的UUID类。首先，我们将了解如何使用类本身。然后我们将看看不同类型的 UUID 以及我们如何在Java中生成它们。

## 2. UUID类

UUID 类有一个构造函数，它需要两个长参数来描述最重要的 64 位和最不重要的 64 位：

```java
UUID uuid = new UUID(mostSignificant64Bits, leastSignificant64Bits);
```

直接使用构造函数的缺点是我们必须构造 UUID 的位模式，当我们想重新创建一个 UUID 对象时，这可能是一个很好的解决方案。但大多数时候，我们使用 UUID 来标识一些东西，并且可以分配一个随机值。因此，UUID类提供了三个我们可以使用的静态方法。

首先，我们可以使用.nameUUIDFromBytes()方法创建一个版本 3 UUIF ，它需要一个字节数组作为参数：

```java
UUID uuid = UUID.nameUUIDFromBytes(bytes);

```

其次，我们可以从先前生成的代码中解析 UUID 字符串值：

```java
UUID uuid = UUID.fromString(uuidHexDigitString);
```

同样，此方法使用一些输入来创建 UUID 代码。但是，有一种更方便的方法可以在不提供任何参数作为输入的情况下创建 UUID。最后，使用.randomUUID()方法，我们可以创建一个版本 4 UUID：

```java
UUID uuid = UUID.randomUUID();
```

接下来，我们将尝试了解 UUID 的结构。

## 三、结构

特别是，让我们考虑以下带有相应掩码的 UUID：

```plaintext
123e4567-e89b-42d3-a456-556642440000
xxxxxxxx-xxxx-Bxxx-Axxx-xxxxxxxxxxxx
```

### 3.1. UUID变体

在上面的示例中，A表示定义 UUID 布局的变体。UUID 中的所有其他位取决于变量字段的布局。因此，变体表示 A 的三个最高有效位：

```html
  MSB1    MSB2    MSB3
   0       X       X     reserved (0)
   1       0       X     current variant (2)
   1       1       0     reserved for Microsoft (6)
   1       1       1     reserved for future (7)
```

上面例子UUID中的A值为“a”，二进制为10xx。因此，布局变体为 2。

### 3.2. UUID版本

同样，B代表版本。在示例 UUID 中， B的值为4，这意味着它使用的是版本 4。

对于Java中的任何 UUID 对象，我们可以使用.variant()和 .version()方法检查变体和版本：

```java
UUID uuid = UUID.randomUUID();
int variant = uuid.variant();
int version = uuid.version();
```

此外，变体 2 UUID 有五个不同的版本：

-   基于时间 (UUIDv1)
-   DCE 安全 (UUIDv2)
-   基于名称(UUIDv3 和 UUIDv5)
-   随机 (UUIDv4)

但是，Java 只提供了 v3 和 v4 的实现。或者，我们可以使用构造函数生成其他类型。

## 4. UUID 版本

### 4.1. 版本 1

UUID 版本 1 使用当前时间戳和生成 UUID 的设备的 MAC 地址。特别是，时间戳是从 1582 年 10 月 15 日开始以 100 纳秒为单位测量的。不过，如果隐私是一个问题，我们可以使用一个随机的 48 位数字而不是 MAC 地址。

考虑到这一点，让我们生成最低有效位和最高有效位的 64 位作为 long 值：

```java
private static long get64LeastSignificantBitsForVersion1() {
    Random random = new Random();
    long random63BitLong = random.nextLong() & 0x3FFFFFFFFFFFFFFFL;
    long variant3BitFlag = 0x8000000000000000L;
    return random63BitLong + variant3BitFlag;
}

```

上面，我们组合了两个 long 值，表示随机 long 值的最后 63 位和 3 位变体标志。接下来，我们使用时间戳创建 64 个最高有效位：

```java
private static long get64MostSignificantBitsForVersion1() {
    final long timeForUuidIn100Nanos = System.currentTimeMillis();
    final long time_low = (timeForUuidIn100Nanos & 0x0000_0000_FFFF_FFFFL) << 32; 
    final long time_mid = ((timeForUuidIn100Nanos >> 32) & 0xFFFF) << 16;
    final long version = 1 << 12; final long time_hi = ((timeForUuidIn100Nanos >> 48) & 0x0FFF);
    return time_low + time_mid + version + time_hi;
}
```

然后我们可以将这两个值传递给 UUID 的构造函数：

```java
public static UUID generateType1UUID() {
    long most64SigBits = get64MostSignificantBitsForVersion1();
    long least64SigBits = get64LeastSignificantBitsForVersion1();
    return new UUID(most64SigBits, least64SigBits);
}
```

### 4.2. 版本 2

接下来，版本 2 也使用时间戳和 MAC 地址。但是，[RFC 4122](https://tools.ietf.org/html/rfc4122)并未指定确切的生成细节，因此我们不会在本文中查看实现。

### 4.3. 版本 3 和 5

第 3 版和第 5 版 UUID 使用从唯一名称空间中提取的散列名称。此外，名称的概念不限于文本形式。例如，域名系统 (DNS)、对象标识符 (OID)、URL 等都被视为有效的命名空间。

```plaintext
UUID = hash(NAMESPACE_IDENTIFIER + NAME)
```

详细来说，UUIDv3 和 UUIDv5 之间的区别在于哈希算法——v3 使用 MD5(128 位)，而 v5 使用截断为 128 位的 SHA-1(160 位)。对于这两个版本，我们替换位以相应地更正版本和变体。

或者，我们可以从先前的命名空间和给定名称生成类型 3 UUID，并使用方法.nameUUIDFromBytes()：

```java
byte[] nameSpaceBytes = bytesFromUUID(namespace);
byte[] nameBytes = name.getBytes("UTF-8");
byte[] result = joinBytes(nameSpaceBytes, nameBytes);

UUID uuid = UUID.nameUUIDFromBytes(result);
```

在这里，我们将命名空间的十六进制字符串转换为字节数组，然后将其与名称组合以创建 UUID。

为了简单起见，我们不会描述版本 5 的[实现](https://github.com/eugenp/tutorials/blob/eb633a5b19658f8c2afc176c4dfc5510540ed10d/core-java-modules/core-java-uuid/src/main/java/com/baeldung/uuid/UUIDGenerator.java#L77)，因为它是相似的。但是，请记住Java不实现类型 5。

### 4.4. 版本 4

最后，我们已经描述了如何生成版本 4 UUID。同样，我们调用UUID 类提供的randomUUID()方法来获取 UUIDv4。

## 5.总结

在本教程中，我们看到了 UUID 的结构和各种现有版本。首先，我们了解了如何在Java中创建 UUID。然后，我们更详细地描述了一些 UUID 版本。最后，我们提供了一些代码示例来手动生成自定义需求的 UUID 代码。