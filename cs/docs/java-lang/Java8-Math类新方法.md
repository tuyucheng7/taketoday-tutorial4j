## 1. 概述

通常，当我们想到Java版本 8 带来的新特性时，首先想到的是函数式编程和 lambda 表达式。

尽管如此，除了那些重要的功能之外，还有其他功能，可能影响较小但也很有趣，而且很多时候并不为人所知，甚至没有被任何评论涵盖。

在本教程中，我们将列举并举例说明添加到语言核心类之一的每个新方法：[java.lang.Math](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Math.html)。

## 2. 新的exact()方法

首先，我们有一组新方法扩展了一些现有的和最常见的算术运算。

正如我们将看到的，它们是不言自明的，因为它们与它们派生的方法具有完全相同的功能，但增加了抛出异常以防万一，结果值溢出了它们类型的最大值或最小值.

我们可以将这些方法与整数和长整数一起用作参数。

### 2.1. 精确添加()

添加两个参数，在溢出的情况下抛出ArithmeticException (适用于所有Exact()方法)：

```java
Math.addExact(100, 50);               // returns 150
Math.addExact(Integer.MAX_VALUE, 1);  // throws ArithmeticException
```

### 2.2. 精确减法()

从第一个参数中减去第二个参数的值，在减法溢出的情况下抛出ArithmeticException ：

```java
Math.subtractExact(100, 50);           // returns 50
Math.subtractExact(Long.MIN_VALUE, 1); // throws ArithmeticException
```

### 2.3. 增量精确()

将参数递增 1，在溢出时抛出ArithmeticException ：

```java
Math.incrementExact(100);               // returns 101
Math.incrementExact(Integer.MAX_VALUE); // throws ArithmeticException
```

### 2.4. 递减精确()

将参数减一，在溢出的情况下抛出ArithmeticException ：

```java
Math.decrementExact(100);            // returns 99
Math.decrementExact(Long.MIN_VALUE); // throws ArithmeticException
```

### 2.5. 乘法精确()

将两个参数相乘，在乘积溢出的情况下抛出ArithmeticException ：

```java
Math.multiplyExact(100, 5);            // returns 500
Math.multiplyExact(Long.MAX_VALUE, 2); // throws ArithmeticException
```

### 2.6. 取反()

更改参数的符号，在溢出的情况下抛出ArithmeticException。

在这种情况下，我们必须考虑内存中值的内部表示，以理解为什么会出现溢出，因为它不像其他“精确”方法那样直观：

```java
Math.negateExact(100);               // returns -100
Math.negateExact(Integer.MIN_VALUE); // throws ArithmeticException
```

第二个示例需要解释，因为它不明显：溢出是由于Integer.MIN_VALUE为 −2.147.483.648，而另一方面Integer.MAX_VALUE为 2.147.483.647，因此返回值不适合Integer一个单位。

## 3.其他方法

### 3.1. floorDiv()

将第一个参数除以第二个参数，然后对结果执行floor()操作，返回小于或等于商的Integer ：

```java
Math.floorDiv(7, 2));  // returns 3

```

确切的商是 3.5，所以floor(3.5) == 3。

让我们看另一个例子：

```java
Math.floorDiv(-7, 2)); // returns -4

```

确切的商是 -3.5，所以floor(-3.5) == -4。

### 3.2. modDiv()

这个方法类似于前面的方法floorDiv()，但是将floor()操作应用于除法的模数或余数而不是商：

```java
Math.modDiv(5, 3));  // returns 2

```

如我们所见，两个正数的modDiv()与 % operator 相同。让我们看一个不同的例子：

```java
Math.modDiv(-5, 3));  // returns 1

```

它返回 1 而不是 2，因为floorDiv(-5, 3)是 -2 而不是 -1。

### 3.3. 下一步()

返回参数的较低值(支持float或double参数)：

```java
float f = Math.nextDown(3);  // returns 2.9999998
double d = Math.nextDown(3); // returns 2.999999761581421
```

## 4。总结

在本文中，我们简要描述了在Java平台版本 8 中添加到类java.lang.Math中的所有新方法的功能，还看到了如何使用它们的一些示例。