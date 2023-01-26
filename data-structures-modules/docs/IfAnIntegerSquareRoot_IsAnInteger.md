## 1. 概述

完全平方是可以表示为两个相等整数乘积的数字。

在本文中，我们将介绍多种方法来确定整数是否是完全平方。此外，我们将讨论每种技术的优缺点，以确定其效率以及哪种技术最快。

## 2. 检查整数是否为完全平方

众所周知，Java为我们提供了两种用于定义整数的数据类型。第一个是int，代表32位的数字，另一个是long，代表64位的数字。
在本文中，我们将使用long数据类型来处理最坏的情况(可能的最大整数)。

由于Java用64位表示long类型，long的范围是-9,223,372,036,854,775,808到9,223,372,036,854,775,807。
而且，由于我们处理的是完全平方，我们只关心处理正整数的集合，因为任何整数与自身相乘总是会产生一个正数。

此外，由于最大的数字约为2<sup>63</sup>，这意味着大约有2<sup>31.5</sup>个整数的平方小于2<sup>63</sup>。
此外，我们可以假设拥有这些数字的查找表是低效的。

### 2.1 使用Java中的sqrt()方法

检查整数是否是完全平方的最简单和最直接的方法是使用sqrt()方法。众所周知，sqrt方法返回一个double值。
因此，我们需要做的是将结果转换为int并将其相乘。然后，我们检查结果是否等于我们开始的整数：

```
public static boolean isPerfectSquareByUsingSqrt(long n) {
  if (n <= 0) {
    return false;
  }
  double perfectSquare = Math.sqrt(n);
  long tst = (long) (perfectSquare + 0.5);
  return tst  tst == n;
}
```

请注意，由于在处理double值时可能会遇到精度错误，我们可能需要将0.5添加到结果中。有时，分配给double变量时，整数可以用小数点表示。

例如，如果我们将数字3分配给一个double变量，那么它的值可能是3.00000001或2.99999999。因此，为了避免这种表示，我们在将其转换为long之前添加0.5以确保我们得到实际值。

另外，如果我们用一个数字来测试sqrt()方法，我们会注意到执行时间很快。另一方面，如果我们需要多次调用sqrt()方法，并尝试减少sqrt()方法执行的操作次数，这种微优化实际上可以会有所不同。

### 2.2 使用二分搜索

我们可以使用二分查找来查找数字的平方根，而无需使用sqrt方法。

由于数字的范围是从1到2<sup>63</sup>，因此平方根在1到2<sup>31.5</sup>之间。因此，二分查找算法需要大约16次迭代才能得到平方根：

```
public static boolean isPerfectSquareByUsingBinarySearch(long low, long high, long number) {
  long check = (low + high) / 2L;
  if (high < low) {
    return false;
  }
  if (number == check  check) {
    return true;
  } else if (number < check  check) {
    high = check - 1L;
    return isPerfectSquareByUsingBinarySearch(low, high, number);
  } else {
    low = check + 1L;
    return isPerfectSquareByUsingBinarySearch(low, high, number);
  }
}
```

### 2.3 二分搜索的增强

为了增强二分查找，我们可以注意到，如果我们确定基本数字的位数，就可以得到平方根的范围。

例如，如果数字只有一位，那么平方根的范围在1到4之间。原因是一位数的最大整数是9，它的根是3。另外，如果数字是由两位数组成，范围在4到10之间，以此类推。

所以，我们可以建立一个查找表，根据我们开始的数字的位数来指定平方根的范围。这将减少二分查找的范围。所以，它可以使用更少的迭代来获得平方根：

```java
public class BinarySearchRange {
  private long low;
  private long high;
  // standard constructor and getters
}
```

```
private void initiateOptimizedBinarySearchLookupTable() {
  lookupTable.add(new BinarySearchRange());
  lookupTable.add(new BinarySearchRange(1L, 4L));
  lookupTable.add(new BinarySearchRange(3L, 10L));
  for (int i = 3; i < 20; i++) {
      lookupTable.add(new BinarySearchRange(
        lookupTable.get(i - 2).low  10,
        lookupTable.get(i - 2).high  10
        )
      );
  }
}
```

```
public boolean isPerfectSquareByUsingOptimizedBinarySearch(long number) {
  int numberOfDigits = Long.toString(number).length();
  return isPerfectSquareByUsingBinarySearch(
    lookupTable.get(numberOfDigits).low,
    lookupTable.get(numberOfDigits).high,number);
}
```

### 2.4 牛顿法

一般来说，我们可以用牛顿法求任意数的平方根，甚至是非整数的平方根。牛顿法的基本思想是假设一个数字X是一个数字N的平方根。
然后，我们可以开始一个循环并继续计算平方根，它肯定会朝着N的正确平方根移动。

但是，通过对牛顿法的一些修改，我们可以使用它来检查整数是否是完全平方：

```
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

## 3. 优化整数平方根算法

正如我们所讨论的，有多种算法可以检查整数的平方根。尽管如此，我们总是可以通过使用一些技巧来优化任何算法。

我们应该考虑避免执行将确定平方根的主要操作。例如，我们可以直接排除负数。

一个不可否认的事实是“完全平方只能以16进制的 0、1、4或9结尾”。因此，我们可以在开始计算之前将整数转换为16进制数。之后，我们排除了将数字视为非完全平方根的情况：

```
public static boolean isPerfectSquareWithOptimization(long n) {
  if (n < 0)
    return false;
  switch ((int) (n & 0xF)) {
    case 0:
    case 1:
    case 4:
    case 9:
      long tst = (long) Math.sqrt(n);
      return tst  tst == n;
    default:
      return false;
  }
}
```

## 4. 总结

在本文中，我们讨论了确定整数是否为完全平方的多种方法。正如我们所看到的，我们总是可以通过使用一些技巧来增强算法。

这些技巧可以在开始算法的主要操作之前排除大量情况。原因是很多整数很容易被确定为非完全平方数。