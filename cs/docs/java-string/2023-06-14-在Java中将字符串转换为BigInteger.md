---
layout: post
title:  使用Selenium处理浏览器选项卡
category: java-string
copyright: java-string
excerpt: Java String
---

## 1. 概述

在本教程中，我们将演示如何将[String](https://www.tuyucheng.com/java-string)转换为[BigInteger](https://www.tuyucheng.com/java-bigdecimal-biginteger)。BigInteger通常用于处理非常大的数值，这些数值通常是任意算术计算的结果。

## 2. 转换十进制(以10为底)整数字符串

要将十进制String转换为BigInteger，我们将使用BigInteger(Stringvalue)构造函数：

```java
String inputString = "878";
BigInteger result = new BigInteger(inputString);
assertEquals("878", result.toString());
```

## 3. 转换非十进制整数字符串

当使用默认的BigInteger(Stringvalue)构造函数转换非十进制String时，比如十六进制数，我们可能会得到NumberFormatException：

```java
String inputString = "290f98";
new BigInteger(inputString);
```

这个异常可以用两种方式处理。

一种方法是使用BigInteger(Stringvalue,intradix)构造函数：

```java
String inputString = "290f98";
BigInteger result = new BigInteger(inputString, 16);
assertEquals("2690968", result.toString());
```

在这种情况下，我们将[基数](https://en.wikipedia.org/wiki/Radix)或基数指定为16，以便将十六进制转换为十进制。

另一种方法是先将非[十进制字符串转换为字节数组](https://www.tuyucheng.com/java-byte-arrays-hex-strings)，然后使用BigIntenger(byte[]bytes)构造函数：

```java
byte[] inputStringBytes = inputString.getBytes();
BigInteger result = new BigInteger(inputStringBytes);
assertEquals("290f98", new String(result.toByteArray()));
```

这给了我们正确的结果，因为BigIntenger(byte[]bytes)构造函数将包含二进制补码表示的字节数组转换为BigInteger。

## 4. 总结

在本文中，我们研究了几种在Java中将String转换为BigIntger的方法。
与往常一样，本教程的完整源代码可在[GitHub](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/java-core-modules/java-string-algorithms-1)上获得。
