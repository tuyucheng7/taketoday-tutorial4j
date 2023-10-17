## 1. 概述

在本教程中，我们将探讨在一个范围内生成随机数的不同方法。

## 延伸阅读：

## [在Java中生成随机数](https://www.baeldung.com/java-generating-random-numbers)

了解在Java中生成随机数的不同方法。

[阅读更多](https://www.baeldung.com/java-generating-random-numbers)→

## [Java – 随机长整型、浮点型、整数和双精度](https://www.baeldung.com/java-generate-random-long-float-integer-double)

了解如何在Java中生成随机数 - 既可以是无限的，也可以是在给定的时间间隔内。

[阅读更多](https://www.baeldung.com/java-generate-random-long-float-integer-double)→

## [Java——生成随机字符串](https://www.baeldung.com/java-random-string)

使用纯Java和 Apache Commons Lang 库生成有界和无界随机字符串。

[阅读更多](https://www.baeldung.com/java-random-string)→

## 2. 生成一个范围内的随机数

### 2.1. 数学.random

Math.random给出一个大于或等于 0.0 且小于 1.0的随机双精度值。

让我们使用 Math.random方法生成给定范围[min, max)内的随机数：

```java
public int getRandomNumber(int min, int max) {
    return (int) ((Math.random()  (max - min)) + min);
}
```

为什么这样行得通？让我们看看当Math.random返回 0.0 时会发生什么，这是可能的最低输出：

```shell
0.0  (max - min) + min => min
```

因此，我们可以获得的最小数字是min。

由于 1.0 是Math.random的唯一上限，这就是我们得到的：

```java
1.0  (max - min) + min => max - min + min => max
```

因此，我们方法返回的唯一上限是max。

在下一节中，我们将看到使用Random#nextInt重复相同的模式。

### 2.2. java.util.Random.nextInt

我们也可以使用java.util.Random的实例来做同样的事情。

让我们使用java.util.Random.nextInt方法来获取随机数：

```java
public int getRandomNumberUsingNextInt(int min, int max) {
    Random random = new Random();
    return random.nextInt(max - min) + min;
}
```

最小参数(原点)是包容性的，而上限 最大值是排他性的。

### 2.3. java.util.Random.ints

java.util.Random.ints方法返回随机整数的IntStream。

因此，我们可以利用java.util.Random.ints方法并返回一个随机数：

```java
public int getRandomNumberUsingInts(int min, int max) {
    Random random = new Random();
    return random.ints(min, max)
      .findFirst()
      .getAsInt();
}
```

同样，这里指定的原点min是包含的，而max是不包含的。

## 3.总结

在本文中，我们看到了在一个范围内生成随机数的替代方法。