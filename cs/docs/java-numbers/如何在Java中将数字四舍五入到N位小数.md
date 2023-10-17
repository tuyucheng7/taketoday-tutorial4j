## 1. 概述

在这个简短的教程中，我们将学习如何在Java中将数字四舍五入到n位小数。

## 延伸阅读：

## [Java中整数的位数](https://www.baeldung.com/java-number-of-digits-in-int)

了解在Java中获取整数位数的不同方法。

[阅读更多](https://www.baeldung.com/java-number-of-digits-in-int)→

## [在Java中检查一个数是否为质数](https://www.baeldung.com/java-prime-numbers)

了解如何使用Java检查数字的素数。

[阅读更多](https://www.baeldung.com/java-prime-numbers)→

## [在Java中检查字符串是否为数字](https://www.baeldung.com/java-check-string-number)

探索确定字符串是否为数字的不同方法。

[阅读更多](https://www.baeldung.com/java-check-string-number)→

## 2. Java中的小数

Java 提供了两种可用于存储十进制数的基本类型：float和double。Double是默认类型：

```java
double PI = 3.1415;
```

但是，我们不应该将任何一种类型用于精确值，例如货币。为此，也为了四舍五入，我们可以使用BigDecimal类。

## 3.格式化小数

如果我们只想打印小数点后n位的十进制数，我们可以简单地格式化输出字符串：

```java
System.out.printf("Value with 3 digits after decimal point %.3f %n", PI);
// OUTPUTS: Value with 3 digits after decimal point 3.142
```

[或者，我们可以使用DecimalFormat](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/text/DecimalFormat.html) 类格式化值：

```java
DecimalFormat df = new DecimalFormat("###.###");
System.out.println(df.format(PI));
```

DecimalFormat允许我们显式设置舍入行为，比上面使用的String.format()提供更多的输出控制。

## 4. 使用BigDecimal舍入Double s

要将double s 舍入到n位小数，我们可以编写一个辅助方法：

```java
private static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    BigDecimal bd = new BigDecimal(Double.toString(value));
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
}
```

在此解决方案中需要注意一件重要的事情；构造BigDecimal时，我们必须始终使用BigDecimal(String) constructor。这可以防止表示不精确值的问题。

我们可以通过使用[Apache Commons Math](https://commons.apache.org/proper/commons-math/)库获得相同的结果：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-math3</artifactId>
    <version>3.5</version>
</dependency>
```

最新版本可以在[这里](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.commons" AND a%3A"commons-math3")找到。

一旦我们将库添加到项目中，我们就可以使用Precision.round()方法，它有两个参数——值和比例：

```java
Precision.round(PI, 3);
```

默认情况下，它使用与我们的辅助方法相同的HALF_UP舍入方法；因此，结果应该是相同的。

请注意，我们可以通过将所需的舍入方法作为第三个参数传递来更改舍入行为。

## 5. 使用DoubleRounder舍入双打

DoubleRounder是[decimal4j](https://github.com/tools4j/decimal4j)库中的一个实用程序。它提供了一种快速且无垃圾的方法，用于将双精度数从 0 舍入到小数点后 18 位。

我们可以通过将依赖项添加到pom.xml来获取库(最新版本可以在[这里](https://search.maven.org/classic/#search|gav|1|g%3A"org.decimal4j" AND a%3A"decimal4j")找到) ：

```xml
<dependency>
    <groupId>org.decimal4j</groupId>
    <artifactId>decimal4j</artifactId>
    <version>1.0.3</version>
</dependency>
```

现在我们可以简单地使用：

```java
DoubleRounder.round(PI, 3);
```

但是，DoubleRounder在某些情况下会失败：

```java
System.out.println(DoubleRounder.round(256.025d, 2));
// OUTPUTS: 256.02 instead of expected 256.03
```

## 6. Math.round() 方法

舍入数字的另一种方法是使用Math.Round()方法。

在这种情况下，我们可以通过乘除10^n来控制n位小数：

```java
public static double roundAvoid(double value, int places) {
    double scale = Math.pow(10, places);
    return Math.round(value  scale) / scale;
}
```

不推荐使用此方法，因为它会截断值。在许多情况下，值四舍五入不正确：

```java
System.out.println(roundAvoid(1000.0d, 17));
// OUTPUTS: 92.23372036854776 !!
System.out.println(roundAvoid(260.775d, 2));
// OUTPUTS: 260.77 instead of expected 260.78
```

因此，在此列出此方法仅供学习之用。

## 七、总结

在本文中，我们介绍了将数字四舍五入到n位小数的不同技术。

我们可以简单地格式化输出而不更改值，或者我们可以使用辅助方法对变量进行舍入。我们还讨论了一些处理这个问题的库。