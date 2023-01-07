## 1. 概述

在本教程中，我们将了解如何在Java中将long值转换为int类型。在开始编码之前，我们需要指出有关此数据类型的一些细节。

首先，在Java中，long值由带符号的 64 位数字表示。另一方面，int值由带符号的 32 位数字表示。因此，将较高的数据类型转换为较低的数据类型称为缩小类型转换。作为这些转换的结果，当long值大于Integer.MAX_VALUE和Integer.MIN_VALUE时，一些位将丢失。

此外，我们将为每个转换变体展示它如何处理等于Integer.MAX_VALUE加一的长整型值。

## 2.数据转换

### 2.1. 铸造价值观

首先，在Java中强制转换值是最常见的类型转换方式——它很简单：

```java
public int longToIntCast(long number) {
    return (int) number;
}
```

### 2.2.Java8

从Java8 开始，我们可以使用另外两种方式进行类型转换：使用[Math](https://www.baeldung.com/java-lang-math) 包或使用 lambda 函数。对于Math包，我们可以使用toIntExact方法：

```java
public int longToIntJavaWithMath(long number) {
return Math.toIntExact(number);
}
```

### 2.3. 包装类

另一方面，我们可以使用包装类Long来获取int值：

```java
public int longToIntBoxingValues(long number) {
    return Long.valueOf(number).intValue();
}
```

### 2.4. 使用BigDecimal

此外，我们可以使用BigDecimal类完成此转换：

```java
public static int longToIntWithBigDecimal(long number) {
    return new BigDecimal(number).intValueExact();
}
```

### 2.5. 使用番石榴

[接下来，我们将使用Google Guava](https://www.baeldung.com/guava-guide)的Ints类展示类型转换：

```java
public int longToIntGuava(long number) {
    return Ints.checkedCast(number);
}
```

另外，[Google Guava](https://www.baeldung.com/guava-guide)的Ints类提供了一个saturatedCast方法：

```java
public int longToIntGuavaSaturated(long number) {
    return Ints.saturatedCast(number);
}
```

### 2.6. 整数上限和下限

最后，我们需要考虑整数值有上限和下限。这些限制由Integer.MAX_VALUE和Integer.MIN_VALUE定义。对于超出这些限制的值，结果因一种方法而异。

在下一个代码片段中，我们将测试 int 值无法容纳 long 值的情况：

```java
@Test
public void longToIntSafeCast() {
    long max = Integer.MAX_VALUE + 10L;
    int expected = -2147483639;
    assertEquals(expected, longToIntCast(max));
    assertEquals(expected, longToIntJavaWithLambda(max));
    assertEquals(expected, longToIntBoxingValues(max));
}
```

使用直接转换、lambda 或使用装箱值会产生负值。在这些情况下，long 值大于Integer.MAX_VALUE，这就是结果值用负数包裹的原因。如果 long 值小于Integer.MIN_VALUE，则结果值为正数。

另一方面，本文中描述的三种方法可能会引发不同类型的异常：

```java
@Test
public void longToIntIntegerException() {
    long max = Integer.MAX_VALUE + 10L;
    assertThrows(ArithmeticException.class, () -> ConvertLongToInt.longToIntWithBigDecimal(max));
    assertThrows(ArithmeticException.class, () -> ConvertLongToInt.longToIntJavaWithMath(max));
    assertThrows(IllegalArgumentException.class, () -> ConvertLongToInt.longToIntGuava(max));
}
```

对于第一个和第二个，抛出ArithmeticException 。对于后者，抛出IllegalArgumentException 。在这种情况下，Ints.checkedCast检查整数是否超出范围。

最后，来自 Guava 的saturatedCast方法，首先检查整数限制并返回限制值是传递的数字大于或小于整数上限和下限：

```java
@Test
public void longToIntGuavaSaturated() {
    long max = Integer.MAX_VALUE + 10L;
    int expected = 2147483647;
    assertEquals(expected, ConvertLongToInt.longToIntGuavaSaturated(max));
}
```

## 3.总结

在本文中，我们通过一些示例介绍了如何在Java中将 long 类型转换为 int 类型。使用本机Java转换和一些库。