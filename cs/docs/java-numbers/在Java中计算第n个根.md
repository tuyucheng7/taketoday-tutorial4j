## 1. 概述

在某些情况下，尝试使用pow()在Java中查找第 n 个根是不准确的。原因是双数可能会在途中失去精度。因此，我们可能需要完善结果以处理这些情况。

## 2.问题

假设我们要计算 N 次方根：

```bash
base = 125, exponent = 3
```

换句话说，125 的 3 次方是哪个数？

前提是数字 x 的 n 次方根等于数字 x 的1/n 次方。所以我们将等式转化为：

```bash
N-th root = Math.pow(125, 1/3)
```

结果是 4.999999999999999。而 4.999999999999999 的 3 次方不是 125。那么我们如何解决这个问题呢？

## 3.正确计算N次方根

上述问题的解决方案主要是一种数学解决方法，而且非常简单。众所周知，数字 x 的 n 次根等于数字 x 的1/n 次方。

有几种方法可以利用上面的等式。首先，我们可以使用 BigDecimal并实现我们版本的[Newton-Raphson](https://en.wikipedia.org/wiki/Newton's_method)方法。其次，我们可以将结果四舍五入到最接近的数字，最后，我们可以定义结果可接受的误差范围。我们将重点关注最后两种方法。

### 3.1. 圆形的

我们现在将使用舍入来解决我们的问题。让我们重用我们之前的例子，看看我们如何获得正确的结果：

```java
public void whenBaseIs125AndNIs3_thenNthIs5() {
    double nth = Math.round(Math.pow(125, 1.0 / 3.0));
    assertEquals(5, nth, 0);
}
```

### 3.2. 误差范围

这种方法与上面的非常相似。我们只需要定义一个可接受的误差范围，假设为 0.00001：

```java
public void whenBaseIs625AndNIs4_thenNthIs5() {
    double nth = Math.pow(625, 1.0 / 4.0);
    assertEquals(5, nth, 0.00001);
}
```

测试证明我们的方法正确计算了n次方根。

## 4。总结

作为开发人员，我们必须了解数据类型及其行为。上面描述的数学方法非常有效，而且准确度很高。你可以选择更适合你的用例的那个。上述解决方案的代码可以[在 GitHub 上找到](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-numbers)。