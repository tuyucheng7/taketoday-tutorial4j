## 1. 概述

在本教程中，我们将了解Java中无穷大的概念以及如何使用它。

## 2.Java数字介绍

在数学中，我们有一组实数和一组整数。显然，这两个集合都是无限的，都包含正无穷大和负无穷大。

在计算机世界中，我们需要一个内存位置来存储这些集合的值，这个位置必须是有限的，因为计算机的内存是有限的。

对于Java中的int类型，并没有涵盖无穷大的概念。我们只能存储适合我们选择的内存位置的整数。

对于实数，我们也有无穷大的概念，无论是正数还是负数。Java 中的 32 位浮点类型和 64 位双精度类型都支持这一点。接下来，我们将使用double类型来举例说明，因为它也是Java中最常用的实数类型，因为它具有更高的精度。

## 3.正无穷大

常量Double.POSITIVE_INFINITY保存double 类型的正无穷大值。该值是通过将1.0除以0.0获得的。它的字符串表示形式是Infinity。这个值是一个约定，它的十六进制表示是7FF0000000000000。每个具有此按位值的双精度变量都包含正无穷大。

## 4.负无穷大

常量Double.NEGATIVE_INFINITY保存double类型的负无穷大值。这个值是用-1.0除以0.0 得到的。它的字符串表示是-Infinity。这个值也是一个约定，它的十六进制表示是FFF0000000000000。每个具有此按位值的双精度变量都包含负无穷大。

## 5. 无限操作

让我们声明一个名为positiveInfinity的double变量并为其分配值Double.POSITIVE_INFINITY和另一个double变量negativeInfinity并为其分配值Double.NEGATIVE_INFINITY。然后，我们得到以下操作结果：

```java
Double positiveInfinity = Double.POSITIVE_INFINITY;
Double negativeInfinity = Double.NEGATIVE_INFINITY;
        
assertEquals(Double.NaN, (positiveInfinity + negativeInfinity));
assertEquals(Double.NaN, (positiveInfinity / negativeInfinity));
assertEquals(Double.POSITIVE_INFINITY, (positiveInfinity - negativeInfinity));
assertEquals(Double.NEGATIVE_INFINITY, (negativeInfinity - positiveInfinity));
assertEquals(Double.NEGATIVE_INFINITY, (positiveInfinity  negativeInfinity));

```

此处，常量Double.NaN表示不是数字的结果。

让我们看看无穷大和正数的数学运算：

```java
Double positiveInfinity = Double.POSITIVE_INFINITY;
Double negativeInfinity = Double.NEGATIVE_INFINITY;
double positiveNumber = 10.0; 
        
assertEquals(Double.POSITIVE_INFINITY, (positiveInfinity + positiveNumber));
assertEquals(Double.NEGATIVE_INFINITY, (negativeInfinity + positiveNumber));
        
assertEquals(Double.POSITIVE_INFINITY, (positiveInfinity - positiveNumber));
assertEquals(Double.NEGATIVE_INFINITY, (negativeInfinity - positiveNumber));
        
assertEquals(Double.POSITIVE_INFINITY, (positiveInfinity  positiveNumber));
assertEquals(Double.NEGATIVE_INFINITY, (negativeInfinity  positiveNumber));
       
assertEquals(Double.POSITIVE_INFINITY, (positiveInfinity / positiveNumber));
assertEquals(Double.NEGATIVE_INFINITY, (negativeInfinity / positiveNumber));
        
assertEquals(Double.NEGATIVE_INFINITY, (positiveNumber - positiveInfinity));
assertEquals(Double.POSITIVE_INFINITY, (positiveNumber - negativeInfinity));
        
assertEquals(0.0, (positiveNumber / positiveInfinity));
assertEquals(-0.0, (positiveNumber / negativeInfinity));

```

现在，让我们看看无穷大和负数的数学运算：

```java
Double positiveInfinity = Double.POSITIVE_INFINITY;
Double negativeInfinity = Double.NEGATIVE_INFINITY;
double negativeNumber = -10.0; 
        
assertEquals(Double.POSITIVE_INFINITY, (positiveInfinity + negativeNumber));
assertEquals(Double.NEGATIVE_INFINITY, (negativeInfinity + negativeNumber));
        
assertEquals(Double.POSITIVE_INFINITY, (positiveInfinity - negativeNumber));
assertEquals(Double.NEGATIVE_INFINITY, (negativeInfinity - negativeNumber));
        
assertEquals(Double.NEGATIVE_INFINITY, (positiveInfinity  negativeNumber));
assertEquals(Double.POSITIVE_INFINITY, (negativeInfinity  negativeNumber));
        
assertEquals(Double.NEGATIVE_INFINITY, (positiveInfinity / negativeNumber));
assertEquals(Double.POSITIVE_INFINITY, (negativeInfinity / negativeNumber));
        
assertEquals(Double.NEGATIVE_INFINITY, (negativeNumber - positiveInfinity));
assertEquals(Double.POSITIVE_INFINITY, (negativeNumber - negativeInfinity));
        
assertEquals(-0.0, (negativeNumber / positiveInfinity));
assertEquals(0.0, (negativeNumber / negativeInfinity));

```

有一些经验法则可以更好地记住这些操作：

-   将负无穷大和正无穷大分别替换为Infinity和-Infinity，先进行符号运算
-   对于非零和无穷大之间的任何操作，你将得到无穷大的结果
-   当我们加或除正无穷大和负无穷大时，我们得到的不是数字结果

## 6.除以零

除以零是除法的一个特例，因为它会产生负无穷大值和正无穷大值。

举例来说，让我们取一个双精度值d并检查以下除以零的结果：

```java
double d = 1.0;
        
assertEquals(Double.POSITIVE_INFINITY, (d / 0.0));
assertEquals(Double.NEGATIVE_INFINITY, (d / -0.0));
assertEquals(Double.NEGATIVE_INFINITY, (-d / 0.0));
assertEquals(Double.POSITIVE_INFINITY, (-d / -0.0));

```

## 七、总结

在本文中，我们探讨了Java中正负无穷大的概念和用法。