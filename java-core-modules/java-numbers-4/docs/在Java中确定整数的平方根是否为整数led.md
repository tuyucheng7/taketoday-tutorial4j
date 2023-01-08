## 1. 概述

完全平方数是可以表示为两个相等整数的乘积的数字。

在本文中，我们将发现多种方法来确定一个整数是否是Java中的完美平方。此外，我们还将讨论每种技术的优缺点，以确定其效率以及哪种技术最快。

## 2. 检查一个整数是否是一个完美的正方形

众所周知，Java 为我们提供了两种定义整数的数据类型。第一个是int，表示 32 位的数字，另一个是long，表示 64 位的数字。在本文中，我们将使用long数据类型来处理最坏的情况(可能的最大整数)。

由于Java用64位来表示长数，所以长数的范围是从-9,223,372,036,854,775,808到 9 ,223,372,036,854,775,807。而且，由于我们处理的是完全平方数，我们只关心处理正整数集，因为任何整数乘以自身总是会产生正数。

此外，由于最大的数约为 2 63，这意味着大约有 2 31.5个整数的平方小于 2 63。此外，我们可以假设拥有这些数字的查找表是低效的。

### 2.1. 在Java中使用sqrt方法

检查整数是否为完全平方数的最简单直接的方法是使用sqrt函数。正如我们所知，sqrt函数返回一个双精度值。因此，我们需要做的是将结果转换为int并自乘。然后，我们检查结果是否等于我们开始的整数：

```java
public static boolean isPerfectSquareByUsingSqrt(long n) {
    if (n <= 0) {
        return false;
    }
    double squareRoot = Math.sqrt(n);
    long tst = (long)(squareRoot + 0.5);
    return tsttst == n;
}
```

请注意，由于在处理双精度值时可能会遇到精度误差，我们可能需要将结果加 0.5 。有时，整数在分配给双精度变量时可以用小数点表示。

例如，如果我们将数字 3 分配给一个双精度变量，那么它的值可能是 3.00000001 或 2.99999999。因此，为避免这种表示，我们在将其转换为long之前添加 0.5以确保我们获得实际值。

此外，如果我们用一个数字测试sqrt函数，我们会注意到执行时间很快。另一方面，如果我们需要多次调用sqrt函数，并且我们尝试减少sqrt函数执行的操作数，这种微优化实际上会有所作为。

### 2.2. 使用二进制搜索

我们可以使用二进制搜索来查找数字的平方根，而无需使用sqrt函数。

由于数字的范围是从 1 到 2 63，所以根在 1 到 2 31.5之间。因此，二分搜索算法需要大约 16 次迭代才能得到平方根：

```java
public boolean isPerfectSquareByUsingBinarySearch(long low, long high, long number) {
    long check = (low + high) / 2L;
    if (high < low) {
        return false;
    }
    if (number == check  check) {
        return true;
    }
    else if (number < check  check) {
        high = check - 1L;
        return isPerfectSquareByUsingBinarySearch(low, high, number);
    }
    else {
        low = check + 1L;
        return isPerfectSquareByUsingBinarySearch(low, high, number);
    }
}
```

### 2.3. 二进制搜索的增强

为了增强二分搜索，我们可以注意到，如果我们确定基本数的位数，就可以得到根的范围。

例如，如果一个数只有一位数，那么平方根的取值范围是1到4之间。原因是一位数的最大整数是9，它的根是3。另外，如果这个数是由两位数字组成，取值范围在4到10之间，依此类推。

所以，我们可以建立一个查找表，根据我们开始的数字的位数来指定平方根的范围。这将减少二进制搜索的范围。因此，它需要更少的迭代次数来获得平方根：

```java
public class BinarySearchRange {
    private long low;
    private long high;

    // standard constructor and getters
}
private void initiateOptimizedBinarySearchLookupTable() {
    lookupTable.add(new BinarySearchRange());
    lookupTable.add(new BinarySearchRange(1L, 4L));
    lookupTable.add(new BinarySearchRange(3L, 10L));
    for (int i = 3; i < 20; i++) {
        lookupTable.add(
          new BinarySearchRange(
            lookupTable.get(i - 2).low  10,
            lookupTable.get(i - 2).high  10));
    }
}
public boolean isPerfectSquareByUsingOptimizedBinarySearch(long number) {
    int numberOfDigits = Long.toString(number).length();
    return isPerfectSquareByUsingBinarySearch(
      lookupTable.get(numberOfDigits).low,
      lookupTable.get(numberOfDigits).high,
     number);
}
```

### 2.4. 牛顿整数运算法

一般来说，我们可以用牛顿法求出任何数的平方根，即使是非整数。牛顿法的基本思想是假设数字X是数字N的平方根。之后，我们可以开始一个循环，不断计算根，一定会朝着N的正确平方根移动。

然而，通过对牛顿法的一些修改，我们可以用它来检查一个整数是否是一个完美的平方：

```java
public static boolean isPerfectSquareByUsingNewtonMethod(long n) {
    long x1 = n;
    long x2 = 1L;
    while (x1 > x2) {
        x1 = (x1 + x2) / 2L;
        x2 = n / x1;
    }
    return x1 == x2 && n % x1 == 0L;
}
```

## 3.优化整数平方根算法

正如我们所讨论的，有多种算法可以检查整数的平方根。尽管如此，我们总是可以通过使用一些技巧来优化任何算法。

技巧应考虑避免执行将确定平方根的主要操作。比如我们可以直接排除负数。

我们可以使用的事实之一是“完全正方形只能以 16 进制的 0、1、4 或 9 结尾”。因此，我们可以在开始计算之前将整数转换为 16 进制。之后，我们排除将数字视为非完美平方根的情况：

```java
public static boolean isPerfectSquareWithOptimization(long n) {
    if (n < 0) {
        return false;
    }
    switch((int)(n & 0xF)) {
        case 0: case 1: case 4: case 9:
            long tst = (long)Math.sqrt(n);
            return tsttst == n;
        default:
            return false;
    }
}
```

## 4。总结

在本文中，我们讨论了多种确定整数是否为完全平方数的方法。正如我们所见，我们总是可以通过使用一些技巧来增强算法。

这些技巧将在开始算法的主要操作之前排除大量情况。原因是很多整数可以很容易地确定为非完全平方。