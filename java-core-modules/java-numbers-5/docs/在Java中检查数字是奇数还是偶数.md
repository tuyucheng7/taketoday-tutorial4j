## 1. 概述

众所周知，一个数的奇偶性是由它除以 2 的余数决定的。偶数产生余数 0，而奇数产生余数 1。

在本教程中，我们将看到在Java中检查数字是偶数还是奇数的多种方法。

## 2.划分法

返回除法余数的算术运算符是取[模](https://www.baeldung.com/modulo-java)运算符%。

我们可以验证一个数字是偶数还是奇数的最简单方法是通过将数字除以 2 并检查余数的数学运算：

```java
boolean isEven(int x) {
    return x % 2 == 0;
}

boolean isOdd(int x) {
    return x % 2 != 0;
}
```

让我们编写几个测试来确认我们方法的行为：

```java
assertEquals(true, isEven(2));
assertEquals(true, isOdd(3));
```

## 3. 位运算法

我们可以对一个数执行多种[按位](https://www.baeldung.com/java-bitwise-operators)运算以确定它是偶数还是奇数。

按位运算 比其他确定数字奇偶校验的方法更高效。

### 3.1. 按位或(|)

偶数OR 1 总是将数字递增 1。

奇数或1 将始终产生 相同的数字：

```java
boolean isOrEven(int x) {
    return (x | 1) > x;
}

boolean isOrOdd(int x) {
    return (x | 1) == x;
}
```

让我们通过一些测试来演示我们的代码的行为：

```java
assertEquals(true, isOrEven(4));
assertEquals(true, isOrOdd(5));
```

### 3.2. 按位与( & )

偶数AND 1 总是产生 0。另一方面，奇数AND 1的结果是 1：

```java
boolean isAndEven(int x) {
    return (x & 1) == 0;
}

boolean isAndOdd(int x) {
    return (x & 1) == 1;
}
```

我们将通过一个小测试来确认此行为：

```java
assertEquals(true, isAndEven(6));
assertEquals(true, isAndOdd(7));
```

### 3.3. 按位异或( ^ )

按位异或是 检查数字奇偶性的最佳解决方案。

偶数XOR 1 总是 将数字加 1，而n 奇数XOR 1 总是将数字 减 1：

```java
boolean isXorEven(int x) {
    return (x ^ 1) > x;
}

boolean isXorOdd(int x) {
    return (x ^ 1) < x;
}
```

让我们写一些小测试来检查我们的代码：

```java
assertEquals(true, isXorEven(8));
assertEquals(true, isXorOdd(9));
```

## 4. 最低有效位 (LSB)

我们介绍的最后一种方法是读取数字的最低有效[位](https://www.baeldung.com/java-get-bit-at-position)。

偶数的最低有效位 始终为 0，而奇数的最低有效位始终为 1：

```java
boolean isLsbEven(int x) {
    return Integer.toBinaryString(x).endsWith("0");
}

boolean isLsbOdd(int x) {
    return Integer.toBinaryString(x).endsWith("1");
}
```

我们将用几行代码来演示这种行为：

```java
assertEquals(true, isLsbEven(10));
assertEquals(true, isLsbOdd(11));
```

## 5.总结

在本文中，我们学习了多种检查数字奇偶校验的方法，即它是偶数还是奇数。我们看到，检查奇偶校验的最佳解决方案是按位异或运算。