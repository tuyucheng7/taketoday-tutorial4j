---
layout: post
title:  使用Selenium处理浏览器选项卡
category: java-string
copyright: java-string
excerpt: Java String
---

## 1. 概述

在本教程中，我们将介绍在Java中将String转换为double的多种方法。

## 2. Double.parseDouble

我们可以使用Double将String转换为double。解析双方法：

```java
assertEquals(1.23, Double.parseDouble("1.23"), 0.000001);
```

## 3. Double.valueOf

同样，我们可以使用Double.valueOf方法将String转换为[装箱的Double](https://www.tuyucheng.com/java-generics#generics-primitive-data-types)：

```java
assertEquals(1.23, Double.valueOf("1.23"), 0.000001);
```

请注意，Double.valueOf的返回值是一个装箱的Double。从Java 5开始，这个装箱的Double由编译器在需要时转换为原始double。

一般来说，我们应该喜欢Double.parseDouble，因为它不需要编译器执行自动拆箱。

## 4. 十进制格式解析

当表示double的String具有更复杂的格式时，我们可以使用DecimalFormat。

例如，我们可以在不删除非数字符号的情况下转换基于十进制的货币值：

```java
DecimalFormat format = new DecimalFormat("u00A4#,##0.00");
format.setParseBigDecimal(true);

BigDecimal decimal = (BigDecimal) format.parse("-$1,000.57");

assertEquals(-1000.57, decimal.doubleValue(), 0.000001);
```

与Double.valueOf类似，DecimalFormat.parse方法返回一个Number，我们可以使用doubleValue方法将其转换为原始双精度数。此外，我们使用setParseBigDecimal方法强制DecimalFormat.parse返回BigDecimal。

通常，DecimalFormat比我们需要的更高级，因此，我们应该更喜欢Double.parseDouble或Double.valueOf。

要了解有关DecimalFormat的更多信息，请查看[DecimalFormat](https://www.tuyucheng.com/java-decimalformat)[实用指南](https://www.tuyucheng.com/java-decimalformat)。

## 5. 无效转换

Java提供了一个统一的接口来处理无效的数字String。

值得注意的是，当我们传递null时，Double.parseDouble、Double.valueOf和DecimalFormat.parse会抛出NullPointerException。

同样，当我们传递无法解析为double的无效字符串(例如&)时，Double.parseDouble和Double.valueOf会抛出NumberFormatException。

最后，当我们传递一个无效的字符串时，DecimalFormat.parse会抛出一个ParseException。

## 6. 避免弃用的转换

在Java 9之前，我们可以通过实例化Double从String创建一个装箱的Double：

```java
new Double("1.23");
```

从版本9开始，Java正式弃用了这种方法。

## 7. 总结

总之，Java为我们提供了多种将String转换为double值的方法。

通常，我们建议使用Double.parseDouble除非需要装箱的Double。
与往常一样，本教程的完整源代码可在[GitHub](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/java-core-modules/java-string-algorithms-1)上获得。
