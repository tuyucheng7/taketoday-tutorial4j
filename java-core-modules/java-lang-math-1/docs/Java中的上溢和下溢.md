## 1. 概述

在本教程中，我们将研究Java中数字数据类型的上溢和下溢。

我们不会深入探讨更多的理论方面——我们将只关注它何时在Java中发生。

首先，我们将了解整数数据类型，然后是浮点数据类型。对于两者，我们还将了解如何检测何时发生溢出或下溢。

## 2. 上溢与下溢

简而言之，当我们分配一个超出变量声明数据类型范围的值时，就会发生上溢和下溢。

如果(绝对)值太大，我们称之为溢出，如果值太小，我们称之为下溢。 

让我们看一个示例，我们尝试将值10 1000 (1和1000 个零)分配给类型为int或double的变量。Java 中的int或double变量的值太大，会发生溢出。

作为第二个示例，假设我们尝试将值10 -1000 (非常接近 0)分配给类型为double的变量。这个值对于Java中的double变量来说太小了，会出现下溢。

让我们更详细地看看Java在这些情况下会发生什么。

## 3. 整数数据类型

Java 中的整数数据类型有byte(8 位)、short(16 位)、int(32 位)和long (64 位)。

在这里，我们将重点关注int数据类型。相同的行为适用于其他数据类型，除了最小值和最大值不同。

Java 中int类型的整数可以是负数或正数，这意味着对于它的 32 位，我们可以在-2 31 ( -2147483648 ) 和2 31 -1 ( 2147483647 ) 之间分配值。

包装器类Integer定义了两个保存这些值的常量：Integer.MIN_VALUE 和 Integer.MAX_VALUE。

### 3.1. 例子

如果我们定义一个int类型的变量m并尝试分配一个太大的值(例如，21474836478 = MAX_VALUE + 1)，会发生什么？

该赋值的一个可能结果是m的值未定义或出现错误。

两者都是有效的结果；但是，在Java中，m的值将为-2147483648(最小值)。另一方面，如果我们尝试分配一个值 -2147483649 ( = MIN_VALUE – 1 )，m将为2147483647(最大值)。 此行为称为整数回绕。

让我们考虑以下代码片段以更好地说明此行为：

```java
int value = Integer.MAX_VALUE-1;
for(int i = 0; i < 4; i++, value++) {
    System.out.println(value);
}
```

我们将得到以下输出，它演示了溢出：

```plaintext
2147483646
2147483647
-2147483648
-2147483647

```

## 4.处理整数数据类型的下溢和上溢

发生溢出时，Java 不会抛出异常；这就是为什么很难找到溢出导致的错误的原因。我们也不能直接访问大多数 CPU 中可用的溢出标志。

但是，有多种方法可以处理可能的溢出。让我们看看其中的几种可能性。

### 4.1. 使用不同的数据类型

如果我们想要允许大于2147483647(或小于-2147483648)的值，我们可以简单地使用long数据类型或BigInteger代替。

虽然long类型的变量也可能溢出，但最小值和最大值要大得多，在大多数情况下可能就足够了。

BigInteger的值范围不受限制，但 JVM 可用内存量除外。

让我们看看如何用BigInteger重写上面的例子：

```java
BigInteger largeValue = new BigInteger(Integer.MAX_VALUE + "");
for(int i = 0; i < 4; i++) {
    System.out.println(largeValue);
    largeValue = largeValue.add(BigInteger.ONE);
}
```

我们将看到以下输出：

```plaintext
2147483647
2147483648
2147483649
2147483650
```

正如我们在输出中看到的，这里没有溢出。我们的文章[BigDecimal and BigInteger in Java](https://www.baeldung.com/java-bigdecimal-biginteger)更详细地介绍了BigInteger 。

### 4.2. 抛出异常

在某些情况下，我们不想允许更大的值，也不希望发生溢出，而是想抛出异常。

从Java8 开始，我们可以使用这些方法进行精确的算术运算。我们先来看一个例子：

```java
int value = Integer.MAX_VALUE-1;
for(int i = 0; i < 4; i++) {
    System.out.println(value);
    value = Math.addExact(value, 1);
}
```

静态方法addExact()执行正常的加法，但如果操作导致上溢或下溢，则会抛出异常：

```plaintext
2147483646
2147483647
Exception in thread "main" java.lang.ArithmeticException: integer overflow
	at java.lang.Math.addExact(Math.java:790)
	at baeldung.underoverflow.OverUnderflow.main(OverUnderflow.java:115)
```

除了addExact()之外，Java 8 中的Math包还为所有算术运算提供了相应的精确方法。有关所有这些方法的列表，请参阅[Java 文档。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Math.html)

此外，还有精确转换方法，如果在转换为另一种数据类型时发生溢出，则会抛出异常。

对于从long到int的转换：

```java
public static int toIntExact(long a)
```

对于从BigInteger到int或long的转换：

```java
BigInteger largeValue = BigInteger.TEN;
long longValue = largeValue.longValueExact();
int intValue = largeValue.intValueExact();
```

### 4.3.Java8 之前

精确算术方法是在Java8 中添加的。如果我们使用更早的版本，我们可以简单地自己创建这些方法。一种选择是实现与Java8 中相同的方法：

```java
public static int addExact(int x, int y) {
    int r = x + y;
    if (((x ^ r) & (y ^ r)) < 0) {
        throw new ArithmeticException("int overflow");
    }
    return r;
}
```

## 5. 非整数数据类型

在算术运算方面，非整数类型float 和double的行为方式与整数数据类型不同。

一个区别是对浮点数的算术运算可能会导致NaN。我们有一篇关于 [NaN in Java](https://www.baeldung.com/java-not-a-number)的专门文章，因此我们不会在本文中进一步探讨它。此外， Math包中没有针对非整数类型的精确算术方法，例如addExact 或multiplyExact 。

 Java的float和double数据类型遵循[IEEE 浮点运算标准 (IEEE 754) 。](https://en.wikipedia.org/wiki/IEEE_754)该标准是Java处理浮点数溢出和下溢方式的基础。

在下面的部分中，我们将重点介绍双精度数据类型的上溢和下溢以及我们可以采取哪些措施来处理它们发生的情况。

### 5.1. 溢出

至于整数数据类型，我们可能期望：

```java
assertTrue(Double.MAX_VALUE + 1 == Double.MIN_VALUE);
```

但是，浮点变量不是这种情况。以下是正确的：

```java
assertTrue(Double.MAX_VALUE + 1 == Double.MAX_VALUE);
```

这是因为双精度值只有有限数量的[有效位](https://www.baeldung.com/cs/most-significant-bit)。如果我们将大双精度值的值仅增加一个，则不会更改任何有效位。因此，该值保持不变。

如果我们增加变量的值，从而增加变量的有效位之一，则变量的值为INFINITY：

```java
assertTrue(Double.MAX_VALUE  2 == Double.POSITIVE_INFINITY);
```

和NEGATIVE_INFINITY对于负值：

```java
assertTrue(Double.MAX_VALUE  -2 == Double.NEGATIVE_INFINITY);
```

我们可以看到，与整数不同，没有环绕，但溢出有两种不同的可能结果：值保持不变，或者我们得到特殊值之一，POSITIVE_INFINITY或NEGATIVE_INFINITY。

### 5.2. 下溢

为双精度值的最小值定义了两个常量：MIN_VALUE (4.9e-324) 和MIN_NORMAL (2.2250738585072014E-308)。

[IEEE 浮点运算标准 (IEEE 754)](https://en.wikipedia.org/wiki/IEEE_754) 更详细地解释了它们之间的区别。

让我们关注一下为什么我们需要浮点数的最小值。

double值不能任意小，因为我们只有有限的位数来表示该值。

[Java SE 语言规范中有关类型、值和变量](https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.2.3)的章节描述了如何表示浮点类型。double的二进制表示的最小指数为-1074。这意味着 double 可以具有的最小正值是Math.pow(2, -1074)，它等于4.9e-324。

因此，Java中double的精度 不支持介于 0 和4.9e-324 之间的值，或介于-4.9e-324和0之间的负值。

那么，如果我们尝试为double类型的变量分配一个太小的值会发生什么？让我们看一个例子：

```java
for(int i = 1073; i <= 1076; i++) {
    System.out.println("2^" + i + " = " + Math.pow(2, -i));
}
```

输出：

```java
2^1073 = 1.0E-323
2^1074 = 4.9E-324
2^1075 = 0.0
2^1076 = 0.0

```

我们看到，如果我们分配的值太小，就会出现下溢，结果值为0.0(正零)。
同样，对于负值，下溢将导致值为-0.0(负零)。

## 6. 检测浮点数据类型的下溢和上溢

由于上溢将导致正无穷大或负无穷大，而下溢将导致正零或负零，因此我们不需要像整数数据类型那样的精确算术方法。相反，我们可以检查这些特殊常量来检测溢出和下溢。

如果我们想在这种情况下抛出异常，我们可以实现一个辅助方法。让我们看看如何求幂：

```java
public static double powExact(double base, double exponent) {
    if(base == 0.0) {
        return 0.0;
    }
    
    double result = Math.pow(base, exponent);
    
    if(result == Double.POSITIVE_INFINITY ) {
        throw new ArithmeticException("Double overflow resulting in POSITIVE_INFINITY");
    } else if(result == Double.NEGATIVE_INFINITY) {
        throw new ArithmeticException("Double overflow resulting in NEGATIVE_INFINITY");
    } else if(Double.compare(-0.0f, result) == 0) {
        throw new ArithmeticException("Double overflow resulting in negative zero");
    } else if(Double.compare(+0.0f, result) == 0) {
        throw new ArithmeticException("Double overflow resulting in positive zero");
    }

    return result;
}
```

在这个方法中，我们需要使用方法Double.compare()。普通比较运算符(<和>)不区分正零和负零。

## 7.正负 零

最后，让我们看一个例子，说明为什么我们在处理正负零和无穷大时需要小心。

让我们定义几个变量来演示：

```java
double a = +0f;
double b = -0f;
```

因为正负0被认为是相等的：

```java
assertTrue(a == b);
```

而正无穷大和负无穷大被认为是不同的：

```java
assertTrue(1/a == Double.POSITIVE_INFINITY);
assertTrue(1/b == Double.NEGATIVE_INFINITY);
```

但是，以下断言是正确的：

```java
assertTrue(1/a != 1/b);
```

这似乎与我们的第一个断言相矛盾。

## 八、总结

在本文中，我们了解了什么是上溢和下溢，它在Java中如何发生，以及整数和浮点数据类型之间的区别。

我们还看到了如何在程序执行期间检测上溢和下溢。