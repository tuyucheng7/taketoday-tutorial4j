## 1. 概述

在本文中，我们将看到 Guava 库中提供的一些有用的数学运算。

Guava 提供了四个数学实用程序类：

1.  IntMath – 对 int 值的操作
2.  LongMath – 长值运算
3.  BigIntegerMath – BigIntegers上的操作
4.  DoubleMath – 对双精度值的运算

## 2. IntMath实用程序

IntMath用于对整数值执行数学运算。我们将通过可用的方法列表来解释它们的每个行为。

### 2.1. 二项式(int n，int k)

此函数计算 n 和 k 的二项式系数。它确保结果在整数范围内。否则，它给出Integer.MAX_VALUE。可以使用公式 n/k(nk) 得出答案：

```java
@Test
public void whenBinomialOnTwoInt_shouldReturnResultIfUnderInt() {
    int result = IntMath.binomial(6, 3);
 
    assertEquals(20, result);
}

@Test
public void whenBinomialOnTwoInt_shouldReturnIntMaxIfOVerflowInt() {
    int result = IntMath.binomial(Integer.MAX_VALUE, 3);
 
    assertEquals(Integer.MAX_VALUE, result);
}
```

### 2.2. ceilingPowerOfTwo(int x)

这将计算大于或等于 x 的最小二乘方的值。结果 n 满足 2^(n-1) < x < 2 ^n：

```java
@Test
public void whenCeilPowOfTwoInt_shouldReturnResult() {
  int result = IntMath.ceilingPowerOfTwo(20);
 
  assertEquals(32, result);
}
```

### 2.3. checkedAdd(int a, int b)和其他

此函数计算两个参数的总和。这个提供了额外的检查，如果结果溢出则抛出ArithmeticException ：

```java
@Test
public void whenAddTwoInt_shouldReturnTheSumIfNotOverflow() {
    int result = IntMath.checkedAdd(1, 2);
 
    assertEquals(3, result);
}

@Test(expected = ArithmeticException.class)
public void whenAddTwoInt_shouldThrowArithmeticExceptionIfOverflow() {
    IntMath.checkedAdd(Integer.MAX_VALUE, 100);
}

```

Guava 为其他三个可能溢出的运算符检查了方法：checkedMultiply、checkedPow和checkedSubtract。

### 2.4. 除法(int p，int q，RoundingMode 模式)

这是一个简单的除法，但允许我们定义舍入模式：

```java
@Test
public void whenDivideTwoInt_shouldReturnTheResultForCeilingRounding() {
    int result = IntMath.divide(10, 3, RoundingMode.CEILING);
 
    assertEquals(4, result);
}
    
@Test(expected = ArithmeticException.class)
public void whenDivideTwoInt_shouldThrowArithmeticExIfRoundNotDefinedButNeeded() {
    IntMath.divide(10, 3, RoundingMode.UNNECESSARY);
}
```

### 2.5. 阶乘(int n)

计算 nie 前 n 个正整数的乘积的阶乘值。如果 n = 0，则返回 1；如果结果不适合 int 范围，则返回Integer.MAX_VALUE 。结果可以通过nx(n-1) x(n-2) x ….. x 2 x 1得到：

```java
@Test
public void whenFactorialInt_shouldReturnTheResultIfInIntRange() {
    int result = IntMath.factorial(5);
 
    assertEquals(120, result);
}

@Test
public void whenFactorialInt_shouldReturnIntMaxIfNotInIntRange() {
    int result = IntMath.factorial(Integer.MAX_VALUE);
 
    assertEquals(Integer.MAX_VALUE, result);
}
```

### 2.6. floorPowerOfTwo(int x)

返回两个的最大幂，其结果小于或等于 x。结果 n 满足 2^n < x < 2 ^(n+1)：

```java
@Test
public void whenFloorPowerOfInt_shouldReturnValue() {
    int result = IntMath.floorPowerOfTwo(30);
 
    assertEquals(16, result);
}
```

### 2.7. gcd(int a, int b)

这个函数给出了 a 和 b 的最大公约数：

```java
@Test
public void whenGcdOfTwoInt_shouldReturnValue() {
    int result = IntMath.gcd(30, 40);
    assertEquals(10, result);
}
```

### 2.8. isPowerOfTwo(int x)

返回 x 是否为 2 的幂。如果该值是 2 的幂，则返回 true，否则返回 false：

```java
@Test
public void givenIntOfPowerTwo_whenIsPowOfTwo_shouldReturnTrue() {
    boolean result = IntMath.isPowerOfTwo(16);
 
    assertTrue(result);
}

@Test
public void givenIntNotOfPowerTwo_whenIsPowOfTwo_shouldReturnFalse() {
    boolean result = IntMath.isPowerOfTwo(20);
 
    assertFalse(result);
}
```

### 2.9. isPrime(int n)

此函数将告诉我们传递的数字是否为质数：

```java
@Test
public void givenNonPrimeInt_whenIsPrime_shouldReturnFalse() {
    boolean result = IntMath.isPrime(20);
 
    assertFalse(result);
}
```

### 2.10. log10(int x, RoundingMode 模式)

此 API 计算给定数字的以 10 为底的对数。使用提供的舍入模式对结果进行舍入：

```java
@Test
public void whenLog10Int_shouldReturnTheResultForCeilingRounding() {
    int result = IntMath.log10(30, RoundingMode.CEILING);
 
    assertEquals(2, result);
}

@Test(expected = ArithmeticException.class)
public void whenLog10Int_shouldThrowArithmeticExIfRoundNotDefinedButNeeded() {
    IntMath.log10(30, RoundingMode.UNNECESSARY);
}
```

### 2.11. log2(int x, RoundingMode 模式)

返回给定数字的以 2 为底的对数。使用提供的舍入模式对结果进行舍入：

```java
@Test
public void whenLog2Int_shouldReturnTheResultForCeilingRounding() {
    int result = IntMath.log2(30, RoundingMode.CEILING);
 
    assertEquals(5, result);
}

@Test(expected = ArithmeticException.class)
public void whenLog2Int_shouldThrowArithmeticExIfRoundNotDefinedButNeeded() {
    IntMath.log2(30, RoundingMode.UNNECESSARY);
}
```

### 2.12. 均值(整数 x，整数 y)

使用此函数，我们可以计算两个值的平均值：

```java
@Test
public void whenMeanTwoInt_shouldReturnTheResult() {
    int result = IntMath.mean(30, 20);
 
    assertEquals(25, result);
}
```

### 2.13. 模组(整数 x，整数 m)

返回一个数除以另一个数的整数除法的余数：

```java
@Test
public void whenModTwoInt_shouldReturnTheResult() {
    int result = IntMath.mod(30, 4);
    assertEquals(2, result);
}
```

### 2.14. pow(int b, int k)

返回 b 的 k 次方的值：

```java
@Test
public void whenPowTwoInt_shouldReturnTheResult() {
    int result = IntMath.pow(6, 4);
 
    assertEquals(1296, result);
}
```

### 2.15. saturatedAdd(int a, int b)和其他

一个求和函数，其优点是在发生时分别返回值Integer.MAX_VALUE或Integer.MIN_VALUE，从而控制任何上溢或下溢：

```java
@Test:
public void whenSaturatedAddTwoInt_shouldReturnTheResult() {
    int result = IntMath.saturatedAdd(6, 4);
 
    assertEquals(10, result);
}

@Test
public void whenSaturatedAddTwoInt_shouldReturnIntMaxIfOverflow() {
    int result = IntMath.saturatedAdd(Integer.MAX_VALUE, 1000);
 
    assertEquals(Integer.MAX_VALUE, result);
}

```

还有其他三个饱和 API：saturatedMultiply、saturatedPow和saturatedSubtract。

### 2.16. sqrt(int x, RoundingMode 模式)

返回给定数字的平方根。使用提供的舍入模式对结果进行舍入：

```java
@Test
public void whenSqrtInt_shouldReturnTheResultForCeilingRounding() {
    int result = IntMath.sqrt(30, RoundingMode.CEILING);
 
    assertEquals(6, result);
}

@Test(expected = ArithmeticException.class)
public void whenSqrtInt_shouldThrowArithmeticExIfRoundNotDefinedButNeded() {
    IntMath.sqrt(30, RoundingMode.UNNECESSARY);
}
```

## 3. LongMath实用程序

LongMath具有Long值的实用程序。大多数操作与IntMath实用程序类似，此处描述了少数例外。

### 3.1. mod(long x, int m)和mod(long x, long m)

返回 x mod m。x除以m的整数余数：

```java
@Test
public void whenModLongAndInt_shouldModThemAndReturnTheResult() {
    int result = LongMath.mod(30L, 4);
 
    assertEquals(2, result);
}
@Test
public void whenModTwoLongValues_shouldModThemAndReturnTheResult() {
    long result = LongMath.mod(30L, 4L);
 
    assertEquals(2L, result);
}
```

## 4.BigIntegerMath实用程序

BigIntegerMath用于对BigInteger类型执行数学运算。

这个实用程序有一些类似于IntMath 的方法。

## 5. DoubleMath 实用程序

DoubleMath实用程序用于对双精度值执行操作。

与BigInteger实用程序类似，可用操作的数量是有限的，并且与IntMath实用程序有相似之处。我们将列出一些仅适用于此实用程序类的特殊功能。

### 5.1. 是数学整数(双 x)

返回 x 是否为数学整数。它检查数字是否可以表示为整数而不会丢失数据：

```java
@Test
public void givenInt_whenMathematicalDouble_shouldReturnTrue() {
    boolean result = DoubleMath.isMathematicalInteger(5);
 
    assertTrue(result);
}

@Test
public void givenDouble_whenMathematicalInt_shouldReturnFalse() {
    boolean result = DoubleMath.isMathematicalInteger(5.2);
 
    assertFalse(result);
}
```

### 5.2. log2(双 x)

计算 x 的以 2 为底的对数：

```java
@Test
public void whenLog2Double_shouldReturnResult() {
    double result = DoubleMath.log2(4);
 
    assertEquals(2, result, 0);
}
```

## 六. 总结

在本快速教程中，我们探索了一些有用的 Guava 数学实用函数。