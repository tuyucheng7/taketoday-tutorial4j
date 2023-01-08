## 1. 概述

我们通常使用Java[Stream API](https://www.baeldung.com/java-8-streams-introduction) 来处理数据集合。

一个很好的特性是支持对数字流的操作，比如求和操作。但是，我们不能以这种方式处理所有数字类型。

在本教程中，我们将了解如何对 BigDecimal等数字流执行求和运算。

## 2. 我们通常如何对流求和

Stream API 提供数字流，包括IntStream、DoubleStream和LongStream。

让我们通过创建一个数字流来提醒自己这些是如何工作的。然后，我们将使用[IntStream#sum](https://www.baeldung.com/java-intstream-convert)计算它的总数：

```java
IntStream intNumbers = IntStream.range(0, 3);
assertEquals(3, intNumbers.sum());
```

我们可以从Double的列表开始做类似的事情 。通过使用流，我们可以使用mapToDouble从对象流转换为DoubleStream：

```java
List<Double> doubleNumbers = Arrays.asList(23.48, 52.26, 13.5);
double result = doubleNumbers.stream()
    .mapToDouble(Double::doubleValue)
    .sum();
assertEquals(89.24, result, .1);
```

因此，如果我们能够以相同的方式对BigDecimal数字的集合求和，将会很有用。

不幸的是，没有BigDecimalStream。所以，我们需要另一种解决方案。

## 3. 使用 Reduce 将BigDecimal数相加

我们可以使用[Stream.reduce](https://www.baeldung.com/java-stream-reduce)而不是依赖sum ：

```java
Stream<Integer> intNumbers = Stream.of(5, 1, 100);
int result = intNumbers.reduce(0, Integer::sum);
assertEquals(106, result);
```

这适用于任何可以逻辑加在一起的东西，包括BigDecimal：

```java
Stream<BigDecimal> bigDecimalNumber = 
  Stream.of(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.TEN);
BigDecimal result = bigDecimalNumber.reduce(BigDecimal.ZERO, BigDecimal::add);
assertEquals(11, result);
```

reduce方法有 两个参数：

-   Identity - 相当于 0 - 它是减少的起始值
-   累加器函数——接受两个参数，到目前为止的结果和流的下一个元素

## 4。总结

在本文中，我们研究了如何在数字Stream中找到一些数字的总和。然后我们发现了如何使用 reduce作为替代方法。

使用 reduce允许我们对BigDecimal数字的集合求和。它可以应用于任何其他类型。