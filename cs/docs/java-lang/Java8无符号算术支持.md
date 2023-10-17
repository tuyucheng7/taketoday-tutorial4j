## 1. 概述

从Java诞生之日起，所有数字数据类型都是有符号的。然而，在许多情况下，需要使用无符号值。例如，如果我们统计一个事件的发生次数，我们不希望遇到负值。

从版本 8 开始，对无符号算术的支持最终成为 JDK 的一部分。这种支持以 Unsigned Integer API 的形式出现，主要包含Integer和Long类中的静态方法。

在本教程中，我们将介绍此 API 并提供有关如何正确使用无符号数的说明。

## 2. 位级表示

要了解如何处理有符号数和无符号数，让我们先看看它们在位级别的表示。

在Java中，数字使用[二进制补码](https://www.baeldung.com/cs/two-complement)系统进行编码。这种编码以相同的方式实现许多基本的算术运算，包括加法、减法和乘法，无论操作数是有符号的还是无符号的。

通过代码示例，事情应该更清楚。为了简单起见，我们将使用字节原始数据类型的变量。其他整型数字类型的操作类似，例如short、int或long。

假设我们有一些 值为 100的字节类型。这个数字有二进制表示 0110_0100。

让我们将这个值加倍：

```java
byte b1 = 100;
byte b2 = (byte) (b1 << 1);
```

给定代码中的左移运算符将变量b1中的所有位向左移动一个位置，从技术上讲，使其值增加一倍。变量b2的二进制表示将是1100_1000。

在无符号类型系统中，此值表示等于2^7 + 2^6 + 2^3或 200的十进制数。然而，在带符号的系统中，最左边的位用作符号位。因此，结果是 -2^7 + 2^6 + 2^3或-56。

快速测试可以验证结果：

```java
assertEquals(-56, b2);
```

我们可以看到有符号数和无符号数的计算是一样的。只有当 JVM 将二进制表示解释为十进制数时才会出现差异。

加法、减法和乘法运算可以处理无符号数，而无需对 JDK 进行任何更改。其他操作(例如比较或除法)以不同方式处理有符号数和无符号数。

这就是 Unsigned Integer API 发挥作用的地方。

## 3.无符号整数API

Unsigned Integer API 为Java8 中的无符号整数运算提供支持。该 API 的大多数成员都是Integer和Long类中的静态方法。

这些类中的方法的工作方式类似。因此，为了简洁起见，我们将只关注Integer类，而忽略Long类。

### 3.1. 比较

Integer类定义了一个名为compareUnsigned的方法来比较无符号数。此方法将所有二进制值都视为无符号，忽略符号位的概念。

让我们从int数据类型边界处的两个数字开始：

```java
int positive = Integer.MAX_VALUE;
int negative = Integer.MIN_VALUE;
```

如果我们将这些数字作为带符号的值进行比较，正数显然大于负数：

```java
int signedComparison = Integer.compare(positive, negative);
assertEquals(1, signedComparison);
```

当将数字作为无符号值进行比较时，最左边的位被认为是最高有效位而不是符号位。因此，结果是不同的，正值小于负值：

```java
int unsignedComparison = Integer.compareUnsigned(positive, negative);
assertEquals(-1, unsignedComparison);
```

如果我们看一下这些数字的二进制表示，应该会更清楚：

-   MAX_VALUE -> 0111_1111_…_1111
-   MIN_VALUE -> 1000_0000_…_0000

当最左边的位为常规值位时， MIN_VALUE 在二进制中比MAX_VALUE大一个单位。该测试证实：

```java
assertEquals(negative, positive + 1);
```

### 3.2. 除法和模数

就像比较运算一样，无符号除法和模运算将所有位都作为值位进行处理。因此，当我们对有符号数和无符号数执行这些操作时，商和余数是不同的：

```java
int positive = Integer.MAX_VALUE;
int negative = Integer.MIN_VALUE;

assertEquals(-1, negative / positive);
assertEquals(1, Integer.divideUnsigned(negative, positive));

assertEquals(-1, negative % positive);
assertEquals(1, Integer.remainderUnsigned(negative, positive));
```

### 3.3. 解析

使用parseUnsignedInt方法解析String时，文本参数可以表示大于MAX_VALUE的数字。

parseInt方法无法解析像这样的大值，该方法只能处理从MIN_VALUE到MAX_VALUE的数字的文本表示。

以下测试用例验证解析结果：

```java
Throwable thrown = catchThrowable(() -> Integer.parseInt("2147483648"));
assertThat(thrown).isInstanceOf(NumberFormatException.class);

assertEquals(Integer.MAX_VALUE + 1, Integer.parseUnsignedInt("2147483648"));
```

请注意，parseUnsignedInt方法可以解析指示大于MAX_VALUE的数字的字符串，但无法解析任何负表示。

### 3.4. 格式化

与解析类似，在格式化数字时，无符号操作将所有位视为值位。因此，我们可以生成一个大约两倍于MAX_VALUE的数字的文本表示。

以下测试用例在两种情况下确认MIN_VALUE的格式化结果——有符号和无符号：

```java
String signedString = Integer.toString(Integer.MIN_VALUE);
assertEquals("-2147483648", signedString);

String unsignedString = Integer.toUnsignedString(Integer.MIN_VALUE);
assertEquals("2147483648", unsignedString);
```

## 4. 优点和缺点

许多开发人员，尤其是那些来自支持无符号数据类型(例如 C)的语言的开发人员，欢迎引入无符号算术运算。然而，这不一定是好事。

对无符号数的需求主要有两个原因。

首先，有些情况永远不会出现负值，使用无符号类型可以首先防止出现这种值。其次，对于无符号类型，与有符号类型相比，我们可以将可用正值的范围扩大一倍。

让我们分析一下呼吁无符号数背后的基本原理。

当变量应始终为非负数时，小于0的值可能有助于指示异常情况。

例如，String.indexOf方法返回某个字符在字符串中第一次出现的位置。索引 -1 可以很容易地表示没有这样的字符。

使用无符号数的另一个原因是值空间的扩展。但是，如果带符号类型的范围不够，则加倍范围不太可能就足够了。

如果数据类型不够大，我们需要使用另一种支持更大值的数据类型，例如使用long而不是int，或者使用BigInteger而不是long。

Unsigned Integer API 的另一个问题是数字的二进制形式是相同的，无论它是有符号的还是无符号的。因此很容易混合有符号和无符号值，这可能会导致意外结果。

## 5.总结

Java 中对无符号算术的支持是应许多人的要求而来的。但是，它带来的好处尚不清楚。我们在使用这个新功能时应该谨慎行事，以避免出现意外结果。