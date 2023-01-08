## 1. 概述

在这个简短的教程中，我们将了解如何使用Java的 Math.sin()函数计算正弦值，以及如何在度数和弧度之间转换角度值。

## 2. 弧度与度数

默认情况下，Java Math库期望其三角函数的值以弧度为单位。

提醒一下， 弧度 只是表示角度度量的另一种方式，转换为：

```java
double inRadians = inDegrees  PI / 180;
inDegrees = inRadians  180 / PI;
```

Java 使用toRadians 和 toDegrees使这很容易：

```java
double inRadians = Math.toRadians(inDegrees);
double inDegrees = Math.toDegrees(inRadians);
```

每当我们使用Java的任何三角函数时，我们首先应该考虑输入的单位是什么。

## 3. 使用Math.sin

我们可以通过查看Math.s in方法来了解这一原则的实际应用，这是Java提供的众多方法之一：

```java
public static double sin(double a)
```

它等效于数学正弦函数，它期望其输入以弧度为单位。所以，假设我们有一个已知的角度：

```java
double inDegrees = 30;
```

我们首先需要将其转换为弧度：

```java
double inRadians = Math.toRadians(inDegrees);
```

然后我们可以计算正弦值：

```java
double sine = Math.sin(inRadians);
```

但是，如果我们知道它已经是弧度，那么我们就不需要进行转换：

```java
@Test
public void givenAnAngleInDegrees_whenUsingToRadians_thenResultIsInRadians() {
    double angleInDegrees = 30;
    double sinForDegrees = Math.sin(Math.toRadians(angleInDegrees)); // 0.5

    double thirtyDegreesInRadians = 1/6  Math.PI;
    double sinForRadians = Math.sin(thirtyDegreesInRadians); // 0.5

    assertTrue(sinForDegrees == sinForRadians);
}
```

由于 thiryDegreesInRadians 已经以弧度为单位，我们不需要先转换它来获得相同的结果。

## 4。总结

在这篇简短的文章中，我们回顾了弧度和度数，然后看到了如何使用 Math.sin 处理它们的示例。