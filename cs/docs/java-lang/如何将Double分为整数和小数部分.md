## 1. 概述

在本教程中，我们将探讨在Java中分隔浮点类型的整数和小数部分的各种方法，即float和 double。

## 2. 浮点类型的问题

让我们从一个简单的分数开始，以及一种通过转换执行分离的简单方法：

```java
double doubleNumber = 24.04;
int intPart = (int) doubleNumber;
System.out.println("Double Number: " + doubleNumber);
System.out.println("Integer Part: " + intPart);
System.out.println("Decimal Part: " + (doubleNumber - intPart));
```

当我们尝试运行上面的代码时，这是我们得到的：

```java
Double Number: 24.04
Integer Part: 24
Decimal Part: 0.03999999999999915
```

与我们的预期相反，输出没有正确打印小数部分。因此，浮点数不适用于不能容忍舍入误差的计算。

## 3. 第一种方法：拆分字符串

首先，让我们将十进制数转换为 等效的字符串。然后我们可以 在小数点索引处拆分字符串。

让我们用一个例子来理解这一点：

```java
double doubleNumber = 24.04;
String doubleAsString = String.valueOf(doubleNumber);
int indexOfDecimal = doubleAsString.indexOf(".");
System.out.println("Double Number: " + doubleNumber);
System.out.println("Integer Part: " + doubleAsString.substring(0, indexOfDecimal));
System.out.println("Decimal Part: " + doubleAsString.substring(indexOfDecimal));

```

上面代码的输出是：

```java
Double Number: 24.04
Integer Part: 24
Decimal Part: .04
```

输出正是我们所期望的。但是，这里的问题是使用 String 的限制——这意味着我们现在无法对它执行任何其他算术运算。

## 4. 第二种方法：使用 BigDecimal

Java 中的[BigDecimal](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/math/BigDecimal.html) 类为其用户提供了对舍入行为的完全控制。此类还提供算术、比例操作、舍入、比较、散列和格式转换等操作。

让我们使用BigDecimal来获取浮点数的整数和小数部分：

```java
double doubleNumber = 24.04;
BigDecimal bigDecimal = new BigDecimal(String.valueOf(doubleNumber));
int intValue = bigDecimal.intValue();
System.out.println("Double Number: " + bigDecimal.toPlainString());
System.out.println("Integer Part: " + intValue);
System.out.println("Decimal Part: " + bigDecimal.subtract(
  new BigDecimal(intValue)).toPlainString());
```

输出将是：

```java
Double Number: 24.04
Integer Part: 24
Decimal Part: 0.04
```

正如我们在上面看到的，输出符合预期。我们还可以借助BigDecimal类中提供的方法执行算术运算。

## 5.总结

在本文中，我们讨论了分隔浮点类型的整数和小数部分的各种方法。我们还讨论了使用BigDecimal进行浮点计算的好处。

[此外，我们关于BigDecimal 和 BigInteger in Java](https://www.baeldung.com/java-bigdecimal-biginteger)的详细教程 讨论了这两个类的更多特性和使用场景。