## 1. 概述

在本快速教程中，我们将说明如何将给定数字四舍五入到最接近的百位。

例如：
99变成100
200.2变成300
400 变成400

## 2.实施

首先，我们将在输入参数上 调用Math.ceil() 。Math.ceil() 返回大于或等于参数的最小整数。例如，如果输入是 200.2 Math.ceil()将返回 201。

接下来，我们将结果加 99 并除以 100。我们利用整数[除法](https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.17.2) 来截断商的小数部分。 最后，我们将商乘以 100 以获得所需的输出。

这是我们的实现：

```java
static long round(double input) {
    long i = (long) Math.ceil(input);
    return ((i + 99) / 100)  100;
};
```

## 3. 测试

让我们测试一下实现：

```java
@Test
public void givenInput_whenRound_thenRoundUpToTheNearestHundred() {
    assertEquals("Rounded up to hundred", 100, RoundUpToHundred.round(99));
    assertEquals("Rounded up to three hundred ", 300, RoundUpToHundred.round(200.2));
    assertEquals("Returns same rounded value", 400, RoundUpToHundred.round(400));
}
```

## 4。总结

在这篇简短的文章中，我们展示了如何将数字四舍五入到最接近的百位。