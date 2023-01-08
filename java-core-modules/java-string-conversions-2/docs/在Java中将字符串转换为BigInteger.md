## 1. 概述

在本教程中，我们将演示如何将[String](https://www.baeldung.com/java-string)转换为[BigInteger](https://www.baeldung.com/java-bigdecimal-biginteger)。BigInteger通常用于处理非常大的数值，这些数值通常是任意算术计算的结果。

## 2. 转换十进制(以 10 为底)整数字符串

要将十进制String转换为BigInteger，我们将使用BigInteger(String value)构造函数：

```java
String inputString = "878";
BigInteger result = new BigInteger(inputString);
assertEquals("878", result.toString());
```

## 3. 转换非十进制整数字符串

当使用默认的BigInteger(String value)构造函数转换非十进制String时，比如十六进制数，我们可能会得到NumberFormatException： 

```java
String inputString = "290f98";
new BigInteger(inputString);
```

这个异常可以用两种方式处理。

一种方法是使用BigInteger(String value, int radix)构造函数：

```java
String inputString = "290f98";
BigInteger result = new BigInteger(inputString, 16);
assertEquals("2690968", result.toString());
```

在这种情况下，我们将[基数](https://en.wikipedia.org/wiki/Radix)或基数指定为 16，以便将十六进制转换为十进制。

另一种方法是先将非[十进制字符串转换为字节数组](https://www.baeldung.com/java-byte-arrays-hex-strings)，然后使用BigIntenger(byte [] bytes) 构造函数：

```java
byte[] inputStringBytes = inputString.getBytes();
BigInteger result = new BigInteger(inputStringBytes);
assertEquals("290f98", new String(result.toByteArray()));
```

这给了我们正确的结果，因为BigIntenger(byte [] bytes)构造函数将包含二进制补码表示的字节数组转换为BigInteger。

## 4。总结

在本文中，我们研究了几种在 Java中将String转换为BigIntger的方法。