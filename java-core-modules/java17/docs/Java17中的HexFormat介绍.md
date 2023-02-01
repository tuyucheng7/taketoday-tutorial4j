## 1. 简介

在Java中，我们通常会编写自己的方法来处理字节和十六进制字符串之间的转换。但是，Java 17引入了java.util.HexFormat，这是一个工具类，**可以将原始类型、字节数组或char数组转换为十六进制字符串，反之亦然**。

在本教程中，我们将探讨如何使用HexFormat并演示它提供的功能。

## 2. Java 17之前处理十六进制字符串

[十六进制数字系统](https://en.wikipedia.org/wiki/Hexadecimal)使用16为基数来表示数字，这意味着它由16个符号组成，通常符号0-9代表0到9的值，A-F代表10到15的值。

这是表示长二进制值的常用选择，因为与1和0的二进制字符串相比，它更容易推理。

当我们需要在十六进制字符串和字节数组之间进行转换时，开发人员通常使用String.format()编写自己的方法来实现他们的目的。

这是一个简单易懂的实现，但往往效率低下：

```java
public static String byteArrayToHex(byte[] a) {
	StringBuilder sb = new StringBuilder(a.length * 2);
	for (byte b : a) {
		sb.append(String.format("%02x", b));
	}
	return sb.toString();
}
```

另一个常用的解决方案是使用[Apache Commons Codec](https://commons.apache.org/proper/commons-codec/)库，它包含一个Hex工具类：

```java
String foo = "I am a string";
byte[] bytes = foo.getBytes();
Hex.encodeHexString(bytes);
```

我们的其他教程之一介绍了[手动执行此转换的不同方法](https://www.baeldung.com/java-byte-arrays-hex-strings)。

## 3. Java 17中HexFormat的使用

HexFormat可以在Java 17标准库中找到，它可以**处理字节和十六进制字符串之间的转换**，还支持多种格式化选项。

### 3.1 创建HexFormat

我们如何创建HexFormat的新实例**取决于我们是否需要分隔符支持**，HexFormat是线程安全的，因此一个实例可以在多个线程中使用。

HexFormat.of()是最常见的用例，当我们不关心分隔符支持时使用它：

```java
HexFormat hexFormat = HexFormat.of();
```

HexFormat.ofDelimiter(":")可用于分隔符支持，下例中使用冒号作为分隔符：

```java
HexFormat hexFormat = HexFormat.ofDelimiter(":");
```

### 3.2 字符串格式

HexFormat允许我们**为现有的HexFormat对象添加前缀、后缀和分隔符格式化选项**，我们可以使用这些来控制正在解析或生成的字符串的格式。

下面是一个同时使用这三者的示例：

```java
HexFormat hexFormat = HexFormat.of().withPrefix("[").withSuffix("]").withDelimiter(", ");
assertEquals("[48], [0c], [11]", hexFormat.formatHex(new byte[] {72, 12, 17}));
```

在这种情况下，我们使用简单的of()方法创建对象，然后使用withDelimiter()添加分隔符。

### 3.3 字节和十六进制字符串转换

现在我们已经了解了如何创建HexFormat实例，让我们来看看如何执行转换。

我们将使用创建实例的简单方法：

```java
HexFormat hexFormat = HexFormat.of();
```

接下来，让我们使用它来将String转换为byte[]：

```java
byte[] hexBytes = hexFormat.parseHex("ABCDEF0123456789");
assertArrayEquals(new byte[] { -85, -51, -17, 1, 35, 69, 103, -119 }, hexBytes);
```

再转换回去：

```java
String bytesAsString = hexFormat.formatHex(new byte[] { -85, -51, -17, 1, 35, 69, 103, -119});
assertEquals("ABCDEF0123456789", bytesAsString);
```

### 3.4 原始类型到十六进制字符串的转换

HexFormat还支持将原始类型转换为十六进制字符串：

```java
String fromByte = hexFormat.toHexDigits((byte) 64);
assertEquals("40", fromByte);

String fromLong = hexFormat.toHexDigits(1234_5678_9012_3456L);
assertEquals("000462d53c8abac0", fromLong);
```

### 3.5 大小写输出

如示例所示，HexFormat的默认行为是生成小写的十六进制值，**我们可以通过在创建HexFormat实例时调用withUpperCase()来改变这种行为**：

```java
upperCaseHexFormat = HexFormat.of().withUpperCase();
```

尽管小写是默认行为，但也存在withLowerCase()方法，这有助于使我们的代码自文档化并且对其他开发人员明确。

## 4. 总结

在Java 17中引入HexFormat解决了我们传统上在字节和十六进制字符串之间执行转换时面临的许多问题。

在本文中我们介绍了最常见的用例，但HexFormat还支持更多的小众功能。例如，有更多的转换方法和管理一个完整字节的上下半部分的能力。

[Java 17文档](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/HexFormat.html)中提供了HexFormat的官方文档。