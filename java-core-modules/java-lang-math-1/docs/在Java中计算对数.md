## 1. 概述

在这个简短的教程中，我们将学习如何在Java中计算对数。我们将涵盖常用对数和自然对数以及具有自定义底数的对数。

## 2.对数

对数是一个数学公式，表示我们必须提高一个固定数字(基数)以产生给定数字的幂。

它以最简单的形式回答了这个问题：我们将一个数字乘以多少次才能得到另一个数字？

我们可以通过以下等式定义对数：

![{displaystyle log _{b}(x)=yquad }](https://wikimedia.org/api/rest_v1/media/math/render/svg/e1efbcdf120619ae8cdf6837c36a3c61d5c16222)正是如果![{displaystyle quad b^{y}=x.}](https://wikimedia.org/api/rest_v1/media/math/render/svg/d1b8a973d10f26d1cd61d2dd0cf1605ef1d54d42)

## 3.计算常用对数

以 10 为底的对数称为常用对数。

要在Java中计算常用对数，我们可以简单地使用 Math.log10()方法：

```java
@Test
public void givenLog10_shouldReturnValidResults() {
    assertEquals(Math.log10(100), 2);
    assertEquals(Math.log10(1000), 3);
}
```

## 4. 计算自然对数

以e为底 的对数称为自然对数。

要在Java中计算自然对数，我们使用Math.log()方法：

```java
@Test
public void givenLog10_shouldReturnValidResults() {
    assertEquals(Math.log(Math.E), 1);
    assertEquals(Math.log(10), 2.30258);
}
```

## 5. 使用自定义底数计算对数

要在Java中计算自定义底数的对数，我们使用以下恒等式：

![{displaystyle log _{b}x={frac {log _{10}x}{log _{10}b}}={frac {log _{e}x}{log _ {e}b}}.,}](https://wikimedia.org/api/rest_v1/media/math/render/svg/560323497497dc161ca4c1ad138dd0930e793e38)

```java
@Test
public void givenCustomLog_shouldReturnValidResults() {
    assertEquals(customLog(2, 256), 8);
    assertEquals(customLog(10, 100), 2);
}

private static double customLog(double base, double logNumber) {
    return Math.log(logNumber) / Math.log(base);
}
```

## 六，总结

在本教程中，我们学习了如何在Java中计算对数。