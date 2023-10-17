## 1. 概述

除以零是一种在普通算术中没有意义的运算，因此是未定义的。然而，在编程中，虽然它经常与错误相关联，但情况并非总是如此。

在本文中，我们将了解在Java程序中发生被零除时会发生什么情况。

根据[除法运算的Java规范，](https://docs.oracle.com/javase/specs/jls/se14/html/jls-15.html#jls-15.17.2)我们可以识别除以零的两种不同情况：整数和浮点数。

## 2.整数

首先，对于整数，事情非常简单。将整数除以零将导致ArithmeticException：

```java
assertThrows(ArithmeticException.class, () -> {
    int result = 12 / 0;
});
assertThrows(ArithmeticException.class, () -> {
    int result = 0 / 0;
});
```

## 3. 浮点类型

但是，在处理浮点数时，不会抛出异常：

```java
assertDoesNotThrow(() -> {
    float result = 12f / 0;
});
```

为了处理这些情况，Java 使用一些特殊的数值来表示此类操作的结果：NaN、POSITIVE_INFINITY和NEGATIVE_INFINITY。

### 3.1. 钠盐

让我们首先将浮点零值除以零：

```java
assertEquals(Float.NaN, 0f / 0);
assertEquals(Double.NaN, 0d / 0);
```

这些情况下的结果是[NaN](https://www.baeldung.com/java-not-a-number)(不是数字)。

### 3.2. 无穷

接下来，让我们将一些非零值除以零：

```java
assertEquals(Float.POSITIVE_INFINITY, 12f / 0);
assertEquals(Double.POSITIVE_INFINITY, 12d / 0);
assertEquals(Float.NEGATIVE_INFINITY, -12f / 0);
assertEquals(Double.NEGATIVE_INFINITY, -12d / 0);
```

如我们所见，结果为INFINITY，其符号取决于操作数的符号。

此外，我们还可以使用负零的概念来达到NEGATIVE_INFINITY：

```java
assertEquals(Float.NEGATIVE_INFINITY, 12f / -0f);
assertEquals(Double.NEGATIVE_INFINITY, 12f / -0f);
```

### 3.3. 内存表示

那么，为什么整数被零除会抛出异常，而浮点数被零除却不会呢？

让我们从内存表示的角度来看这个。对于整数，没有可用于存储此类操作结果的位模式，而浮点数具有NaN或INFINITY等值，可用于此类情况。

现在，让我们将浮点数的二进制表示形式视为S EEEEEEE E FFFFFFF FFFFFFFF FFFFFFFF，其中一位 (S) 用于符号，8 位 (E) 用于指数，其余 (F) 用于尾数。

在NaN、POSITIVE_INFINITY和 NEGATIVE_INFINITY这三个值的每一个中，指数部分的所有位都设置为 1。

INFINITY的尾数位全部设置为 0，而NaN的尾数不为零：

```java
assertEquals(Float.POSITIVE_INFINITY, Float.intBitsToFloat(0b01111111100000000000000000000000));
assertEquals(Float.NEGATIVE_INFINITY, Float.intBitsToFloat(0b11111111100000000000000000000000));
assertEquals(Float.NaN, Float.intBitsToFloat(0b11111111100000010000000000000000));
assertEquals(Float.NaN, Float.intBitsToFloat(0b11111111100000011000000000100000));
```

## 4.总结

总而言之，在本文中，我们了解了在Java中如何除以零。

INFINITY和NaN 等值可用于浮点数，但不可用于整数。因此，整数除以零将导致异常。但是，对于float或double，Java 允许该操作。