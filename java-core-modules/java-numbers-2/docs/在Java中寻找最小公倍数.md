## 1. 概述

两个非零整数(a, b)的[最小公倍数(LCM) 是可同时被](https://en.wikipedia.org/wiki/Least_common_multiple)a和b整除的最小正整数。

在本教程中，我们将了解查找两个或更多数字的 LCM 的不同方法。我们必须注意，负整数和零不是 LCM 的候选者。

## 2. 使用简单算法计算两个数的 LCM

[我们可以利用乘法](https://en.wikipedia.org/wiki/Multiplication)重复加法这个简单的事实来求出两个数的最小公倍数。

### 2.1. 算法

找到 LCM 的简单算法是一种迭代方法，它利用了两个数字的 LCM 的一些基本属性。

首先，我们知道任何零数的最小公倍数本身就是零。因此，只要给定整数中的任何一个为 0，我们就可以提前退出该过程。

其次，我们还可以利用两个非零整数的 LCM 的下界是两个数的绝对值中较大者这一事实。

此外，如前所述，LCM 永远不可能是负整数。因此，我们将仅使用整数的绝对值来查找可能的倍数，直到找到公倍数。

让我们看看确定 lcm(a, b) 需要遵循的确切过程：

1.  如果 a = 0 或 b = 0，则返回 lcm(a, b) = 0，否则转到步骤 2。
2.  计算两个数的绝对值。
3.  将 lcm 初始化为步骤 2 中计算的两个值中的较高者。
4.  如果 lcm 可以被较低的绝对值整除，则返回。
5.  将 lcm 增加两者中较高的绝对值，然后转到步骤 4。

在我们开始实施这个简单方法之前，让我们试运行一下以找到 lcm(12, 18)。

由于 12 和 18 都是正数，我们跳到第 3 步，初始化 lcm = max(12, 18) = 18，然后继续。

在我们的第一次迭代中，lcm = 18，它不能被 12 完全整除。因此，我们将它递增 18 并继续。

在第二次迭代中，我们可以看到 lcm = 36，现在可以被 12 整除。因此，我们可以从算法返回并得出 lcm(12, 18) 为 36 的总结。

### 2.2. 执行 

让我们用Java实现该算法。我们的lcm()方法需要接受两个整数参数并将其 LCM 作为返回值。

我们可以注意到，上述算法涉及对数字执行一些数学运算，例如查找绝对值、最小值和最大值。为此，我们可以分别使用[Math](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Math.html)类的相应静态方法，例如abs()、min()和max()。

让我们实现我们的lcm()方法：

```java
public static int lcm(int number1, int number2) {
    if (number1 == 0 || number2 == 0) {
        return 0;
    }
    int absNumber1 = Math.abs(number1);
    int absNumber2 = Math.abs(number2);
    int absHigherNumber = Math.max(absNumber1, absNumber2);
    int absLowerNumber = Math.min(absNumber1, absNumber2);
    int lcm = absHigherNumber;
    while (lcm % absLowerNumber != 0) {
        lcm += absHigherNumber;
    }
    return lcm;
}
```

接下来我们也来验证一下这个方法：

```java
@Test
public void testLCM() {
    Assert.assertEquals(36, lcm(12, 18));
}
```

上述测试用例通过断言 lcm(12, 18) 为 36来验证lcm()方法的正确性。

## 3. 使用质因数分解方法

[算术基本定理](https://en.wikipedia.org/wiki/Fundamental_theorem_of_arithmetic)指出，可以将每个大于 1 的整数唯一地表示为素数幂的乘积。

因此，对于任何整数 N > 1，我们有 N = (2 k1 )  (3 k2 )  (5 k3 )  ...

使用该定理的结果，我们现在将了解用于查找两个数的 LCM 的质因数分解方法。

### 3.1. 算法

质因数分解方法根据两个数的质数分解计算[LCM](https://proofwiki.org/wiki/LCM_from_Prime_Decomposition)。我们可以使用质因数分解中的质因数和指数来计算两个数的 LCM：

什么时候，|一个| = (2 p1 )  (3 p2 )  (5 p3 )  …
和|b| = (2 q1 )  (3 q2 )  (5 q3 )  …
那么，lcm(a, b) = (2 max(p 1 , q 1 ) )  (3 max(p 2 , q 2 ) )  (5最大值 (p 3 , q 3 ) ) …

让我们看看如何使用这种方法计算 12 和 18 的 LCM：

首先，我们需要将两个数的绝对值表示为质因数的乘积：
12 = 2  2  3 = 2²  3¹
18 = 2  3  3 = 2¹  3²

我们在这里可以注意到，上述表示中的质因数是 2 和 3。

接下来，让我们确定 LCM 的每个素因子的指数。我们通过从两种表示中获取更高的权力来做到这一点。

使用此策略，LCM 中 2 的次方将为 max(2, 1) = 2，而 LCM 中 3 的次方将为 max(1, 2) = 2。

最后，我们可以通过将素因子与上一步中获得的相应幂相乘来计算 LCM。因此，我们有 lcm(12, 18) = 2²  3² = 36。

### 3.2. 执行

我们的Java实现使用两个数字的质因数分解表示来查找 LCM。

为此，我们的getPrimeFactors()方法需要接受一个整数参数并为我们提供其质因数分解表示。在Java中，我们可以使用HashMap表示数字的质因数分解，其中每个键表示质因数，与键关联的值表示相应因数的指数。

让我们看一下getPrimeFactors()方法的迭代实现：

```java
public static Map<Integer, Integer> getPrimeFactors(int number) {
    int absNumber = Math.abs(number);

    Map<Integer, Integer> primeFactorsMap = new HashMap<Integer, Integer>();

    for (int factor = 2; factor <= absNumber; factor++) {
        while (absNumber % factor == 0) {
            Integer power = primeFactorsMap.get(factor);
            if (power == null) {
                power = 0;
            }
            primeFactorsMap.put(factor, power + 1);
            absNumber /= factor;
        }
    }

    return primeFactorsMap;
}
```

我们知道12和18的质因数分解图分别是{2→2, 3→1}和{2→1, 3→2}。让我们用它来测试上面的方法：

```java
@Test
public void testGetPrimeFactors() {
    Map<Integer, Integer> expectedPrimeFactorsMapForTwelve = new HashMap<>();
    expectedPrimeFactorsMapForTwelve.put(2, 2);
    expectedPrimeFactorsMapForTwelve.put(3, 1);

    Assert.assertEquals(expectedPrimeFactorsMapForTwelve, 
      PrimeFactorizationAlgorithm.getPrimeFactors(12));

    Map<Integer, Integer> expectedPrimeFactorsMapForEighteen = new HashMap<>();
    expectedPrimeFactorsMapForEighteen.put(2, 1);
    expectedPrimeFactorsMapForEighteen.put(3, 2);

    Assert.assertEquals(expectedPrimeFactorsMapForEighteen, 
      PrimeFactorizationAlgorithm.getPrimeFactors(18));
}
```

我们的lcm()方法首先使用getPrimeFactors()方法为每个数字查找质因数分解图。接下来，它使用两个数字的质因数分解图来找到它们的 LCM。让我们看看这个方法的迭代实现：

```java
public static int lcm(int number1, int number2) {
    if(number1 == 0 || number2 == 0) {
        return 0;
    }

    Map<Integer, Integer> primeFactorsForNum1 = getPrimeFactors(number1);
    Map<Integer, Integer> primeFactorsForNum2 = getPrimeFactors(number2);

    Set<Integer> primeFactorsUnionSet = new HashSet<>(primeFactorsForNum1.keySet());
    primeFactorsUnionSet.addAll(primeFactorsForNum2.keySet());

    int lcm = 1;

    for (Integer primeFactor : primeFactorsUnionSet) {
        lcm = Math.pow(primeFactor, 
          Math.max(primeFactorsForNum1.getOrDefault(primeFactor, 0),
            primeFactorsForNum2.getOrDefault(primeFactor, 0)));
    }

    return lcm;
}
```

作为一种好的做法，我们现在将验证lcm()方法的逻辑正确性：

```java
@Test
public void testLCM() {
    Assert.assertEquals(36, PrimeFactorizationAlgorithm.lcm(12, 18));
}
```

## 4.使用欧几里德算法

[两个数字的LCM](https://en.wikipedia.org/wiki/Least_common_multiple)和[GCD](https://en.wikipedia.org/wiki/Greatest_common_divisor)(最大公因数)之间存在一个有趣的关系，即两个数字[乘积的绝对值等于它们的 GCD 和 LCM 的乘积](https://proofwiki.org/wiki/Product_of_GCD_and_LCM)。

如前所述，gcd(a, b)  lcm(a, b) = |a  b|。

因此，lcm(a, b) = |a  b|/gcd(a, b)。

使用这个公式，我们原来寻找 lcm(a,b) 的问题现在已经简化为只寻找 gcd(a,b)。

当然，有多种策略可以找到两个数字的 GCD。然而，众所周知，[欧几里得算法](https://en.wikipedia.org/wiki/Euclidean_algorithm)是最有效的算法之一。

为此，我们简单了解一下这个算法的症结所在，可以概括为两个关系：

-   gcd (a, b) = gcd(|a%b|, |a| ); 哪里|一个| >= |b|
-   gcd(p, 0) = gcd(0, p) = |p|

让我们看看如何使用上述关系找到 lcm(12, 18)：

我们有 gcd(12, 18) = gcd(18%12, 12) = gcd(6,12) = gcd(12%6, 6) = gcd(0, 6) = 6

因此，lcm(12, 18) = |12 x 18| / gcd(12, 18) = (12 x 18) / 6 = 36

我们现在将看到欧几里得算法的递归实现：

```java
public static int gcd(int number1, int number2) {
    if (number1 == 0 || number2 == 0) {
        return number1 + number2;
    } else {
        int absNumber1 = Math.abs(number1);
        int absNumber2 = Math.abs(number2);
        int biggerValue = Math.max(absNumber1, absNumber2);
        int smallerValue = Math.min(absNumber1, absNumber2);
        return gcd(biggerValue % smallerValue, smallerValue);
    }
}
```

上面的实现使用了数字的绝对值——因为 GCD 是完美除以两个数字的最大正整数，我们对负除数不感兴趣。

我们现在准备验证上述实现是否按预期工作：

```java
@Test
public void testGCD() {
    Assert.assertEquals(6, EuclideanAlgorithm.gcd(12, 18));
}
```

### 4.1. 两个数字的LCM

使用较早的方法找到 GCD，我们现在可以轻松计算 LCM。同样，我们的lcm()方法需要接受两个整数作为输入以返回它们的 LCM。让我们看看如何在Java中实现这个方法：

```java
public static int lcm(int number1, int number2) {
    if (number1 == 0 || number2 == 0)
        return 0;
    else {
        int gcd = gcd(number1, number2);
        return Math.abs(number1  number2) / gcd;
    }
}
```

我们现在可以验证上述方法的功能：

```java
@Test
public void testLCM() {
    Assert.assertEquals(36, EuclideanAlgorithm.lcm(12, 18));
}
```

### 4.2. 使用BigInteger类的大数 LCM

要计算大数的 LCM，我们可以利用[BigInteger](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/math/BigInteger.html) 类。

在内部，BigInteger类的gcd()方法使用混合算法来优化计算性能。此外，由于BigInteger对象是不可变的，实现利用[MutableBigInteger](https://github.com/openjdk/jdk/blob/6bab0f539fba8fb441697846347597b4a0ade428/src/java.base/share/classes/java/math/MutableBigInteger.java)类的可变实例来避免频繁的内存重新分配。

首先，它使用传统的欧几里德算法，用较低整数的模数重复替换较高整数。

结果，这对不仅越来越小，而且在连续分裂后彼此越来越近。最终，在各自的int[]值数组中保存两个MutableBigInteger对象的大小所需的int数量的差异达到 1 或 0。

在这个阶段，策略切换到[Binary GCD 算法](https://en.wikipedia.org/wiki/Binary_GCD_algorithm) 以获得更快的计算结果。

在这种情况下，我们也将通过将数字乘积的绝对值除以它们的 GCD 来计算 LCM。与我们之前的示例类似，我们的lcm()方法将两个BigInteger值作为输入，并将这两个数字的 LCM 作为BigInteger返回。让我们看看它的实际效果：

```java
public static BigInteger lcm(BigInteger number1, BigInteger number2) {
    BigInteger gcd = number1.gcd(number2);
    BigInteger absProduct = number1.multiply(number2).abs();
    return absProduct.divide(gcd);
}
```

最后，我们可以用一个测试用例来验证这一点：

```java
@Test
public void testLCM() {
    BigInteger number1 = new BigInteger("12");
    BigInteger number2 = new BigInteger("18");
    BigInteger expectedLCM = new BigInteger("36");
    Assert.assertEquals(expectedLCM, BigIntegerLCM.lcm(number1, number2));
}
```

## 5.总结

在本教程中，我们讨论了在Java中查找两个数的最小公倍数的各种方法。

此外，我们还了解了数字与其 LCM 和 GCD 的乘积之间的关系。给定可以有效计算两个数的 GCD 的算法，我们还将 LCM 计算问题简化为 GCD 计算问题之一。