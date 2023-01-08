## 1. 概述

在本教程中，我们将介绍在 Java中将String转换为double的多种方法。

## 2. Double.parseDouble

我们可以 使用Double 将String转换为double 。解析双方法：

```java
assertEquals(1.23, Double.parseDouble("1.23"), 0.000001);
```

## 3. Double.valueOf

同样，我们可以使用 Double.valueOf方法将String 转换为[装箱的Double](https://www.baeldung.com/java-generics#generics-primitive-data-types)：

```java
assertEquals(1.23, Double.valueOf("1.23"), 0.000001);
```

请注意，Double.valueOf的返回值是一个装箱的Double。从Java5 开始，这个装箱的Double由编译器在需要时转换为原始double 。

一般来说，我们应该喜欢Double.parseDouble，因为它不需要编译器执行自动拆箱。

## 4.十进制格式解析

当表示double的String具有更复杂的格式时，我们可以使用 DecimalFormat。

例如，我们可以在不删除非数字符号的情况下转换基于十进制的货币值：

```java
DecimalFormat format = new DecimalFormat("u00A4#,##0.00");
format.setParseBigDecimal(true);

BigDecimal decimal = (BigDecimal) format.parse("-$1,000.57");

assertEquals(-1000.57, decimal.doubleValue(), 0.000001);
```

与 Double.valueOf类似， DecimalFormat.parse方法返回一个 Number，我们可以使用 doubleValue方法将其转换为原始双精度数。此外，我们使用setParseBigDecimal方法强制DecimalFormat.parse返回 BigDecimal。

通常， DecimalFormat比我们需要的更高级，因此，我们应该更喜欢 Double.parseDouble或 Double.valueOf。

要了解有关DecimalFormat的更多信息，请查看[DecimalFormat](https://www.baeldung.com/java-decimalformat)[实用指南](https://www.baeldung.com/java-decimalformat)。

## 5. 无效转换

Java 提供了一个统一的接口来处理无效的数字String。

值得注意的是，当我们传递null时， Double.parseDouble、 Double.valueOf和DecimalFormat.parse会抛出NullPointerException 。

同样，当我们传递无法解析为double的无效字符串(例如&)时， Double.parseDouble和Double.valueOf会抛出 NumberFormatException 。

最后，当我们传递一个无效的字符串时， DecimalFormat.parse会抛出一个ParseException 。

## 6.避免弃用的转换

在Java9 之前，我们可以通过实例化 Double从 String创建一个装箱的Double：

```java
new Double("1.23");
```

从版本 9 开始，Java 正式弃用了这种方法。

## 七、总结

总之，Java 为我们提供了多种将String转换为 double值的方法。

通常，我们建议使用 Double.parseDouble除非需要装箱的Double。