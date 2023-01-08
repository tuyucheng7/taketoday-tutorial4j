## 1. 概述

[String](https://www.baeldung.com/java-string)类的charAt()方法返回 String 给定位置的字符。这是一个有用的方法，从Java语言的 1.0 版开始就可以使用。

在本教程中，我们将通过一些示例来探索此方法的用法。我们还将学习如何将某个位置的字符作为字符串获取。

## 2. charAt()方法

让我们看一下String类的方法签名：

```java
public char charAt(int index) {...}
```

此方法返回输入参数中指定索引处的字符。索引范围从 0(第一个字符)到字符串的总长度 – 1(最后一个字符)。

现在，让我们看一个例子：

```java
String sample = "abcdefg";
Assert.assertEquals('d', sample.charAt(3));

```

在这种情况下，结果是字符串的第四个字符——字符“d”。

## 3. 预期异常

如果参数索引为负或者等于或大于字符串的长度，则抛出运行时异常IndexOutOfBoundsException ：

```java
String sample = "abcdefg";
assertThrows(IndexOutOfBoundsException.class, () -> sample.charAt(-1));
assertThrows(IndexOutOfBoundsException.class, () -> sample.charAt(sample.length()));

```

## 4. 获取字符串形式的字符

正如我们之前提到的，charAt()方法返回一个char。通常，我们需要一个String文字来代替。

有多种方法可以将结果转换为String。让我们假设以下所有示例的字符串文字：

```java
String sample = "abcdefg";
```

### 4.1. 使用Character.toString()方法 

我们可以用 Character.toString()方法包装charAt()的结果：

```java
assertEquals("a", Character.toString(sample.charAt(0)));

```

### 4.2. 使用String.valueOf()方法 

最后，我们可以使用静态方法String。价值()：

```java
assertEquals("a", String.valueOf(sample.charAt(0)));

```

## 5.总结

在本文中，我们学习了如何使用charAt()方法获取String给定位置的字符。我们还看到了使用它时可能发生的异常以及将字符作为String获取的几种不同方法。