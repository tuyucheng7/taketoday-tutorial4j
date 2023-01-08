## 1. 概述

在本教程中，我们将介绍 在 Java中将String转换为[BigDecimal的多种方法。](https://www.baeldung.com/java-bigdecimal-biginteger)

## 2.大小数

BigDecimal表示不可变的任意精度带符号十进制数。它由两部分组成：

-   未缩放的值——任意精度整数
-   scale——一个 32 位整数，表示小数点右边的位数

例如，BigDecimal 3.14 的未缩放值为 314，缩放值为 2。

如果为零或正数，则小数位数是小数点右边的位数。

如果为负数，则将数字的未缩放值乘以 10 的缩放负数次方。因此，BigDecimal表示的数字的值为 (Unscaled value × 10 -Scale )。

Java 中的BigDecimal类提供基本算术、标度操作、比较、格式转换和散列运算。

此外，我们将BigDecimal用于高精度算术、需要控制比例的计算和舍入行为。一个这样的例子是涉及金融交易的计算。

我们可以使用以下方法之一在Java中将String转换为BigDecimal ：

-   BigDecimal(String)构造函数
-   BigDecimal.valueOf()方法
-   DecimalFormat.parse()方法

让我们在下面讨论它们中的每一个。

## 3.BigDecimal (字符串)

在Java中将String转换为BigDecimal的最简单方法是使用BigDecimal(String)构造函数：

```java
BigDecimal bigDecimal = new BigDecimal("123");
assertEquals(new BigDecimal(123), bigDecimal);
```

## 4.BigDecimal.valueOf ()

我们还可以使用 [BigDecimal.valueOf(double)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/math/BigDecimal.html#(double)) 方法将String转换为BigDecimal 。

这是一个两步过程。第一步是将String转换为Double。第二步是将Double转换为BigDecimal：

```java
BigDecimal bigDecimal = BigDecimal.valueOf(Double.valueOf("123.42"));
assertEquals(new BigDecimal(123.42).setScale(2, BigDecimal.ROUND_HALF_UP), bigDecimal);
```

必须注意，某些浮点数不能使用Double值精确表示。这是因为Double类型的浮点数在内存中表示。实际上，该数字以尽可能接近输入的Double数字的有理形式表示。结果，一些浮点数变得[不准确](https://www.baeldung.com/cs/floating-point-numbers-inaccuracy)。

## 5.DecimalFormat.parse ()

当表示值的 String 具有更复杂的格式时，我们可以使用 DecimalFormat。

例如，我们可以在不删除非数字符号的情况下转换基于十进制的 long 值：

```java
BigDecimal bigDecimal = new BigDecimal(10692467440017.111).setScale(3, BigDecimal.ROUND_HALF_UP);

DecimalFormatSymbols symbols = new DecimalFormatSymbols();
symbols.setGroupingSeparator(',');
symbols.setDecimalSeparator('.');
String pattern = "#,##0.0#";
DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
decimalFormat.setParseBigDecimal(true);

// parse the string value
BigDecimal parsedStringValue = (BigDecimal) decimalFormat.parse("10,692,467,440,017.111");

assertEquals(bigDecimal, parsedStringValue);
```

DecimalFormat.parse 方法返回一个 Number，我们使用setParseBigDecimal(true)将其转换为BigDecimal数字 。

通常， [DecimalFormat](https://www.baeldung.com/java-decimalformat)比我们需要的更高级。因此，我们应该改用 new BigDecimal(String) 或 BigDecimal.valueOf() 。

## 6.无效转换

Java 提供了处理无效数字String的通用异常。

值得注意的是， 当我们传递 null时， new BigDecimal(String)、BigDecimal.valueOf()和DecimalFormat.parse会抛出NullPointerException：

```java
@Test(expected = NullPointerException.class)
public void givenNullString_WhenBigDecimalObjectWithStringParameter_ThenNullPointerExceptionIsThrown() {
    String bigDecimal = null;
    new BigDecimal(bigDecimal);
}

@Test(expected = NullPointerException.class)
public void givenNullString_WhenValueOfDoubleFromString_ThenNullPointerExceptionIsThrown() {
    BigDecimal.valueOf(Double.valueOf(null));
}

@Test(expected = NullPointerException.class)
public void givenNullString_WhenDecimalFormatOfString_ThenNullPointerExceptionIsThrown()
  throws ParseException {
    new DecimalFormat("#").parse(null);
}
```

类似地，new BigDecimal(String)和BigDecimal.valueOf()在我们传递无法解析为BigDecimal (例如 & ) 的无效字符串时 抛出 NumberFormatException：

```java
@Test(expected = NumberFormatException.class)
public void givenInalidString_WhenBigDecimalObjectWithStringParameter_ThenNumberFormatExceptionIsThrown() {
    new BigDecimal("&");
}

@Test(expected = NumberFormatException.class)
public void givenInalidString_WhenValueOfDoubleFromString_ThenNumberFormatExceptionIsThrown() {
    BigDecimal.valueOf(Double.valueOf("&"));
}

```

最后， 当我们传递一个无效的 String时， DecimalFormat.parse 抛出一个 ParseException：

```java
@Test(expected = ParseException.class)
public void givenInalidString_WhenDecimalFormatOfString_ThenNumberFormatExceptionIsThrown()
  throws ParseException {
    new DecimalFormat("#").parse("&");
}
```

## 七、总结

在本文中，我们了解到Java为我们提供了多种将String转换为BigDecimal值的方法。通常，我们建议 为此目的使用新的 BigDecimal(String)方法。