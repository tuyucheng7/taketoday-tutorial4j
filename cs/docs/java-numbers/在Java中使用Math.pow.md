## 1. 概述

一个数的幂是指在乘法中使用该数的次数。这可以用Java轻松计算。

## 2. Math.pow例子

在查看示例之前，让我们先看一下该方法的签名：

```java
public double pow(double a, double b)
```

该方法计算a的b次方并将结果返回为double。换句话说，a乘以自身b次。

现在让我们看一个简单的例子：

```java
int intResult = (int) Math.pow(2, 3);
```

输出将为 8。请注意，如果我们想要一个Integer结果，则需要在上面的示例中进行int转换。

现在让我们传递一个double作为参数并查看结果：

```java
double dblResult = Math.pow(4.2, 3);
```

输出将为 74.08800000000001。

这里我们没有将结果转换为int，因为我们对double值感兴趣。由于我们有一个双精度值，我们可以轻松配置并使用DecimalFormat将该值四舍五入到小数点后两位，结果为 74.09：

```java
DecimalFormat df = new DecimalFormat(".00");
double dblResult = Math.pow(4.2, 3);
```

## 3.总结

在这篇快速文章中，我们了解了如何使用Java的[Math.pow()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Math.html#pow(double,double))方法来计算任何给定底数的幂。