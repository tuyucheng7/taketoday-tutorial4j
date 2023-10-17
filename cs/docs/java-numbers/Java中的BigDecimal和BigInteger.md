## 1. 概述

在本教程中，我们将演示[BigDecimal ](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/math/BigDecimal.html)和[BigInteger ](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/math/BigInteger.html)类。

我们将描述这两种数据类型、它们的特点以及它们的使用场景。我们还将简要介绍使用这两个类的各种操作。

## 2.大小数

BigDecimal表示不可变的任意精度带符号十进制数。它由两部分组成：

-   未缩放的值——任意精度整数
-   scale——一个 32 位整数，表示小数点右边的位数

例如，BigDecimal 3.14 的未缩放值为 314，小数位数为 2。

我们使用BigDecimal进行高精度运算。我们还将它用于需要控制比例和舍入行为的计算。一个这样的例子是涉及金融交易的计算。

我们可以从String、字符数组、int、long和BigInteger创建一个BigDecimal对象：

```java
@Test
public void whenBigDecimalCreated_thenValueMatches() {
    BigDecimal bdFromString = new BigDecimal("0.1");
    BigDecimal bdFromCharArray = new BigDecimal(new char[] {'3','.','1','6','1','5'});
    BigDecimal bdlFromInt = new BigDecimal(42);
    BigDecimal bdFromLong = new BigDecimal(123412345678901L);
    BigInteger bigInteger = BigInteger.probablePrime(100, new Random());
    BigDecimal bdFromBigInteger = new BigDecimal(bigInteger);
        
    assertEquals("0.1",bdFromString.toString());
    assertEquals("3.1615",bdFromCharArray.toString());
    assertEquals("42",bdlFromInt.toString());
    assertEquals("123412345678901",bdFromLong.toString());
    assertEquals(bigInteger.toString(),bdFromBigInteger.toString());
}
```

我们还可以从double创建BigDecimal：

```java
@Test
public void whenBigDecimalCreatedFromDouble_thenValueMayNotMatch() {
    BigDecimal bdFromDouble = new BigDecimal(0.1d);
    assertNotEquals("0.1", bdFromDouble.toString());
}
```

但是，在这种情况下，结果与预期不同(即 0.1)。这是因为：

-   双构造函数进行精确翻译
-   0.1 在double中没有精确表示

因此，我们应该使用 S tring构造函数而不是double构造函数。

此外，我们可以 使用valueOf 静态方法将double和long转换为BigDecimal ：

```java
@Test
public void whenBigDecimalCreatedUsingValueOf_thenValueMatches() {
    BigDecimal bdFromLong1 = BigDecimal.valueOf(123412345678901L);
    BigDecimal bdFromLong2 = BigDecimal.valueOf(123412345678901L, 2);
    BigDecimal bdFromDouble = BigDecimal.valueOf(0.1d);

    assertEquals("123412345678901", bdFromLong1.toString());
    assertEquals("1234123456789.01", bdFromLong2.toString());
    assertEquals("0.1", bdFromDouble.toString());
}
```

在转换为BigDecimal之前，此方法将double转换为其String表示形式。此外，它可以重用对象实例。

因此，我们应该优先使用valueOf方法而不是构造函数。

## 3. BigDecimal操作

就像其他Number类(Integer、Long、Double等)一样，BigDecimal提供算术和比较运算。它还提供用于比例操作、舍入和格式转换的操作。

它不会重载算术(+、-、/、)或逻辑(>.< 等)运算符。相反，我们使用相应的方法——add、subtract、multiply、divide和compareTo。

BigDecimal 具有提取各种属性的方法，例如精度、比例和符号：

```java
@Test
public void whenGettingAttributes_thenExpectedResult() {
    BigDecimal bd = new BigDecimal("-12345.6789");
        
    assertEquals(9, bd.precision());
    assertEquals(4, bd.scale());
    assertEquals(-1, bd.signum());
}
```

我们使用compareTo方法比较两个 BigDecimals 的值：

```java
@Test
public void whenComparingBigDecimals_thenExpectedResult() {
    BigDecimal bd1 = new BigDecimal("1.0");
    BigDecimal bd2 = new BigDecimal("1.00");
    BigDecimal bd3 = new BigDecimal("2.0");

    assertTrue(bd1.compareTo(bd3) < 0);
    assertTrue(bd3.compareTo(bd1) > 0);
    assertTrue(bd1.compareTo(bd2) == 0);
    assertTrue(bd1.compareTo(bd3) <= 0);
    assertTrue(bd1.compareTo(bd2) >= 0);
    assertTrue(bd1.compareTo(bd3) != 0);
}
```

此方法在比较时忽略比例。

另一方面，只有当两个BigDecimal对象的 value 和 scale 相等时，equals方法才认为它们相等。因此， 通过此方法比较时， BigDecimals 1.0 和 1.00 不相等。

```java
@Test
public void whenEqualsCalled_thenSizeAndScaleMatched() {
    BigDecimal bd1 = new BigDecimal("1.0");
    BigDecimal bd2 = new BigDecimal("1.00");
        
    assertFalse(bd1.equals(bd2));
}
```

我们通过调用相应的方法来执行算术运算：

```java
@Test
public void whenPerformingArithmetic_thenExpectedResult() {
    BigDecimal bd1 = new BigDecimal("4.0");
    BigDecimal bd2 = new BigDecimal("2.0");

    BigDecimal sum = bd1.add(bd2);
    BigDecimal difference = bd1.subtract(bd2);
    BigDecimal quotient = bd1.divide(bd2);
    BigDecimal product = bd1.multiply(bd2);

    assertTrue(sum.compareTo(new BigDecimal("6.0")) == 0);
    assertTrue(difference.compareTo(new BigDecimal("2.0")) == 0);
    assertTrue(quotient.compareTo(new BigDecimal("2.0")) == 0);
    assertTrue(product.compareTo(new BigDecimal("8.0")) == 0);
}
```

由于BigDecimal是不可变的，因此这些操作不会修改现有对象。相反，他们返回新对象。

## 4.四舍五入和 BigDecimal

通过舍入一个数字，我们将其替换为另一个具有更短、更简单和更有意义的表示形式。例如，我们将 24.784917 美元四舍五入为 24.78 美元，因为我们没有分数。

要使用的精度和舍入模式因计算而异。例如，美国联邦纳税申报表指定使用HALF_UP四舍五入为整数金额。

有两个类控制舍入行为——RoundingMode和MathContext。

枚举[RoundingMode](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/math/RoundingMode.html) 提供八种舍入模式： 

-   CEILING—— 向正无穷大舍入
-   FLOOR—— 向负无穷大舍入
-   UP—— 从零开始四舍五入
-   DOWN – 向零舍入
-   HALF_UP – 向“最近的邻居”舍入，除非两个邻居是等距的，在这种情况下向上舍入
-   HALF_DOWN—— 向“最近的邻居”舍入，除非两个邻居是等距的，在这种情况下向下舍入
-   HALF_EVEN – 向“最近的邻居”舍入，除非两个邻居是等距的，在这种情况下，向偶数邻居舍入
-   UNNECESSARY—— 不需要舍入， 如果不可能得到准确的结果，则抛出ArithmeticException

HALF_EVEN舍入模式最大限度地减少了舍入操作造成的偏差。它经常被使用。它也被称为 银行家的四舍五入。

[MathContext ](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/math/MathContext.html)封装了精度和舍入模式。有几个预定义的 MathContext：

-   DECIMAL32 – 7 位精度和 HALF_EVEN 舍入模式
-   DECIMAL64 – 16 位精度和 HALF_EVEN 舍入模式
-   DECIMAL128 – 34 位精度和 HALF_EVEN 舍入模式
-   UNLIMITED——无限精度算术

使用此类，我们可以使用指定的精度和舍入行为来舍入BigDecimal数字：

```java
@Test
public void whenRoundingDecimal_thenExpectedResult() {
    BigDecimal bd = new BigDecimal("2.5");
    // Round to 1 digit using HALF_EVEN
    BigDecimal rounded = bd
        .round(new MathContext(1, RoundingMode.HALF_EVEN));

    assertEquals("2", rounded.toString());
}
```

现在，让我们使用示例计算来检查舍入概念。

让我们编写一个方法来计算给定数量和单价的商品的总支付金额。我们还应用贴现率和销售税率。我们使用setScale方法将最终结果四舍五入到美分：

```java
public static BigDecimal calculateTotalAmount(BigDecimal quantity,
    BigDecimal unitPrice, BigDecimal discountRate, BigDecimal taxRate) { 
    BigDecimal amount = quantity.multiply(unitPrice);
    BigDecimal discount = amount.multiply(discountRate);
    BigDecimal discountedAmount = amount.subtract(discount);
    BigDecimal tax = discountedAmount.multiply(taxRate);
    BigDecimal total = discountedAmount.add(tax);

    // round to 2 decimal places using HALF_EVEN
    BigDecimal roundedTotal = total.setScale(2, RoundingMode.HALF_EVEN);
        
    return roundedTotal;
}
```

现在，让我们为这个方法编写一个单元测试：

```java
@Test
public void givenPurchaseTxn_whenCalculatingTotalAmount_thenExpectedResult() {
    BigDecimal quantity = new BigDecimal("4.5");
    BigDecimal unitPrice = new BigDecimal("2.69");
    BigDecimal discountRate = new BigDecimal("0.10");
    BigDecimal taxRate = new BigDecimal("0.0725");

    BigDecimal amountToBePaid = BigDecimalDemo
      .calculateTotalAmount(quantity, unitPrice, discountRate, taxRate);

    assertEquals("11.68", amountToBePaid.toString());
}
```

## 5.大整数

BigInteger表示不可变的任意精度整数。它类似于原始整数类型，但允许任意大的值。

当涉及的整数大于long类型的限制时使用。 例如，50 的阶乘是 30414093201713378043612608166064768844377641568960512000000000000。这个值对于int 或 long数据类型来说太大了，无法处理。它只能存储在BigInteger变量中。

它广泛用于安全和密码学应用程序。

我们可以从字节数组或String创建BigInteger：

```java
@Test
public void whenBigIntegerCreatedFromConstructor_thenExpectedResult() {
    BigInteger biFromString = new BigInteger("1234567890987654321");
    BigInteger biFromByteArray = new BigInteger(
       new byte[] { 64, 64, 64, 64, 64, 64 });
    BigInteger biFromSignMagnitude = new BigInteger(-1,
       new byte[] { 64, 64, 64, 64, 64, 64 });

    assertEquals("1234567890987654321", biFromString.toString());
    assertEquals("70644700037184", biFromByteArray.toString());
    assertEquals("-70644700037184", biFromSignMagnitude.toString());
}
```

此外，我们可以使用静态方法valueOf将long转换为BigInteger：

```java
@Test
public void whenLongConvertedToBigInteger_thenValueMatches() {
    BigInteger bi =  BigInteger.valueOf(2305843009213693951L);
        
    assertEquals("2305843009213693951", bi.toString());
}
```

## 6. BigInteger的操作

与int和long类似，BigInteger实现了所有的算术和逻辑运算。但是，它不会使操作员超载。

它还实现了Math类的相应方法：abs、min、max、pow、signum。

我们使用compareTo方法比较两个 BigInteger 的值：

```java
@Test
public void givenBigIntegers_whentCompared_thenExpectedResult() {
    BigInteger i = new BigInteger("123456789012345678901234567890");
    BigInteger j = new BigInteger("123456789012345678901234567891");
    BigInteger k = new BigInteger("123456789012345678901234567892");

    assertTrue(i.compareTo(i) == 0);
    assertTrue(j.compareTo(i) > 0);
    assertTrue(j.compareTo(k) < 0);
}
```

我们通过调用相应的方法来执行算术运算：

```java
@Test
public void givenBigIntegers_whenPerformingArithmetic_thenExpectedResult() {
    BigInteger i = new BigInteger("4");
    BigInteger j = new BigInteger("2");

    BigInteger sum = i.add(j);
    BigInteger difference = i.subtract(j);
    BigInteger quotient = i.divide(j);
    BigInteger product = i.multiply(j);

    assertEquals(new BigInteger("6"), sum);
    assertEquals(new BigInteger("2"), difference);
    assertEquals(new BigInteger("2"), quotient);
    assertEquals(new BigInteger("8"), product);
}
```

由于BigInteger是不可变的，因此这些操作不会修改现有对象。与int和long不同，这些操作不会溢出。

BigInteger具有类似于int和long的位操作。但是，我们需要使用方法而不是运算符：

```java
@Test
public void givenBigIntegers_whenPerformingBitOperations_thenExpectedResult() {
    BigInteger i = new BigInteger("17");
    BigInteger j = new BigInteger("7");

    BigInteger and = i.and(j);
    BigInteger or = i.or(j);
    BigInteger not = j.not();
    BigInteger xor = i.xor(j);
    BigInteger andNot = i.andNot(j);
    BigInteger shiftLeft = i.shiftLeft(1);
    BigInteger shiftRight = i.shiftRight(1);

    assertEquals(new BigInteger("1"), and);
    assertEquals(new BigInteger("23"), or);
    assertEquals(new BigInteger("-8"), not);
    assertEquals(new BigInteger("22"), xor);
    assertEquals(new BigInteger("16"), andNot);
    assertEquals(new BigInteger("34"), shiftLeft);
    assertEquals(new BigInteger("8"), shiftRight);
}
```

它有额外的位操作方法：

```java
@Test
public void givenBigIntegers_whenPerformingBitManipulations_thenExpectedResult() {
    BigInteger i = new BigInteger("1018");

    int bitCount = i.bitCount();
    int bitLength = i.bitLength();
    int getLowestSetBit = i.getLowestSetBit();
    boolean testBit3 = i.testBit(3);
    BigInteger setBit12 = i.setBit(12);
    BigInteger flipBit0 = i.flipBit(0);
    BigInteger clearBit3 = i.clearBit(3);

    assertEquals(8, bitCount);
    assertEquals(10, bitLength);
    assertEquals(1, getLowestSetBit);
    assertEquals(true, testBit3);
    assertEquals(new BigInteger("5114"), setBit12);
    assertEquals(new BigInteger("1019"), flipBit0);
    assertEquals(new BigInteger("1010"), clearBit3);
}
```

BigInteger提供了 GCD 计算和模运算的方法：

```java
@Test
public void givenBigIntegers_whenModularCalculation_thenExpectedResult() {
    BigInteger i = new BigInteger("31");
    BigInteger j = new BigInteger("24");
    BigInteger k = new BigInteger("16");

    BigInteger gcd = j.gcd(k);
    BigInteger multiplyAndmod = j.multiply(k).mod(i);
    BigInteger modInverse = j.modInverse(i);
    BigInteger modPow = j.modPow(k, i);

    assertEquals(new BigInteger("8"), gcd);
    assertEquals(new BigInteger("12"), multiplyAndmod);
    assertEquals(new BigInteger("22"), modInverse);
    assertEquals(new BigInteger("7"), modPow);
}
```

它还具有与素数生成和素数测试相关的方法：

```java
@Test
public void givenBigIntegers_whenPrimeOperations_thenExpectedResult() {
    BigInteger i = BigInteger.probablePrime(100, new Random());
        
    boolean isProbablePrime = i.isProbablePrime(1000);
    assertEquals(true, isProbablePrime);
}
```

## 七、总结

在本快速教程中，我们探讨了 BigDecimal 和 BigInteger 类。 它们对于高级数值计算很有用，在这些计算中原始整数类型是不够的。