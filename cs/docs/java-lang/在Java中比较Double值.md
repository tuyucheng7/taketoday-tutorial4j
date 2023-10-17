## 1. 概述

在本教程中，我们将讨论在Java中比较双精度值的不同方法。特别是，它不像比较其他原始类型那么容易。事实上，它在许多其他语言中都存在问题，不仅是 Java。

首先，我们将解释为什么使用简单的 == 运算符是不准确的，并且可能导致难以跟踪运行时中的错误。然后，我们将展示如何正确比较普通Java和常见第三方库中的双精度数。

## 2. 使用 == 运算符

使用 == 运算符进行比较的不准确性是由双精度值存储在计算机内存中的方式引起的。我们需要记住，有限的内存空间(通常是 64 位)必须容纳无限数量的值。因此，我们无法在计算机中精确表示大多数双精度值。它们必须四舍五入才能保存。

由于舍入不准确，可能会出现有趣的错误：

```java
double d1 = 0;
for (int i = 1; i <= 8; i++) {
    d1 += 0.1;
 }

double d2 = 0.1  8;

System.out.println(d1);
System.out.println(d2);
```

两个变量d1和 d2 都应等于 0.8。但是，当我们运行上面的代码时，我们会看到以下结果：

```bash
0.7999999999999999
0.8
```

在这种情况下，使用 == 运算符比较两个值会产生错误的结果。为此，我们必须使用更复杂的比较算法。

如果我们想要最好的精度和控制舍入机制，我们可以使用 [java.math.BigDecimal](https://www.baeldung.com/java-bigdecimal-biginteger)类。

## 3. 在普通Java中比较双打

在纯Java中比较双精度值的推荐算法是阈值比较方法。在这种情况下，我们需要检查两个数字之间的差异是否在指定的公差范围内，通常称为epsilon：

```java
double epsilon = 0.000001d;

assertThat(Math.abs(d1 - d2) < epsilon).isTrue();
```

epsilon 的值越小，比较精度越高。但是，如果我们指定的公差值太小，我们将得到与简单 == 比较相同的错误结果。一般来说，epsilon 的 5 位和 6 位小数的值通常是一个很好的起点。

不幸的是，标准 JDK 中没有任何实用程序可用于以推荐的精确方式比较双精度值。幸运的是，我们不需要自己编写它。我们可以使用免费且广为人知的第三方库提供的各种专用方法。

## 4. 使用 Apache Commons 数学

[Apache Commons Math](https://www.baeldung.com/apache-commons-math)是最大的致力于数学和统计组件的开源库之一。从各种不同的类和方法中，我们将特别关注org.apache.commons.math3.util.Precision类。它包含 2 个有用的equals()方法来正确比较双精度值：

```java
double epsilon = 0.000001d;

assertThat(Precision.equals(d1, d2, epsilon)).isTrue();
assertThat(Precision.equals(d1, d2)).isTrue();
```

此处使用的epsilon变量与前面的示例具有相同的含义。它是允许的绝对误差量。然而，这并不是与阈值算法的唯一相似之处。特别是，两种equals方法在底层使用相同的方法。

双参数函数版本只是[equals(d1, d2, 1)](https://commons.apache.org/proper/commons-math/javadocs/api-3.6/org/apache/commons/math3/util/Precision.html#equals(double, double, int))方法调用的快捷方式。在这种情况下，如果d1和d2 之间没有浮点数，则认为它们相等。 

## 5.使用番石榴

Google 的[Guava](https://www.baeldung.com/guava-guide)是一组扩展标准 JDK 功能的核心Java库。它在com.google.common.math包中包含大量有用的数学工具。为了在 Guava 中正确比较双精度值，让我们从DoubleMath类中实现fuzzyEquals()方法 ：

```java
double epsilon = 0.000001d;

assertThat(DoubleMath.fuzzyEquals(d1, d2, epsilon)).isTrue();
```

方法名称与 Apache Commons Math 中的不同，但它在幕后的工作原理几乎相同。唯一的区别是没有使用 epsilon 的默认值的重载方法。

## 6. 使用 JUnit

[JUnit](https://www.baeldung.com/junit)是使用最广泛的Java单元测试框架之一。通常，每个单元测试通常以分析预期值和实际值之间的差异结束。因此，测试框架必须有正确和精确的比较算法。事实上，JUnit 提供了一组用于常见对象、集合和原始类型的比较方法，包括检查双值是否相等的专用方法：

```java
double epsilon = 0.000001d;
assertEquals(d1, d2, epsilon);
```

事实上，它的工作原理与前面描述的 Guava 和 Apache Commons 的方法相同。

重要的是要指出，还有一个不推荐使用的双参数版本没有 epsilon 参数。然而，如果我们想确保我们的结果总是正确的，我们应该坚持使用三参数版本。

## 七、总结

在本文中，我们探讨了在Java中比较双精度值的不同方法。

我们已经解释了为什么简单的比较可能会导致难以跟踪运行时中的错误。然后，我们展示了如何正确比较普通Java和公共库中的值。