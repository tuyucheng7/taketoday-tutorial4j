## 1. 概述

在本教程中，我们将展示使用Java生成素数的各种方法。

如果你要检查一个数字是否为质数，这里有一个关于如何做到这一点[的快速指南。](https://www.baeldung.com/java-prime-numbers)

## 2.素数

让我们从核心定义开始。质数是大于 1 的自然数，除了 1 和它本身之外没有正约数。

例如，7 是素数，因为 1 和 7 是它唯一的正整数因数，而 12 不是，因为除了 1、4 和 6 之外，它还有约数 3 和 2。

## 3.生成质数

在本节中，我们将看到如何有效地生成小于给定值的素数。

### 3.1.Java7 及之前版本——暴力破解

```java
public static List<Integer> primeNumbersBruteForce(int n) {
    List<Integer> primeNumbers = new LinkedList<>();
    for (int i = 2; i <= n; i++) {
        if (isPrimeBruteForce(i)) {
            primeNumbers.add(i);
        }
    }
    return primeNumbers;
}
public static boolean isPrimeBruteForce(int number) {
    for (int i = 2; i < number; i++) {
        if (number % i == 0) {
            return false;
        }
    }
    return true;
}

```

如你所见，primeNumbersBruteForce迭代从 2 到n的数字，并简单地调用isPrimeBruteForce()方法来检查数字是否为素数。

该方法检查每个数字是否被 2 到number-1范围内的数字整除。

如果在任何时候我们遇到一个可整除的数字，我们返回 false。最后，当我们发现该数字不能被其之前的任何数字整除时，我们返回 true 表明它是一个素数。

### 3.2. 效率与优化

之前的算法是非线性的，时间复杂度为O(n^2)。该算法的效率也不高，显然还有改进的余地。

让我们看一下isPrimeBruteForce()方法中的条件。

当一个数不是素数时，这个数可以被分解为两个因子，即a和b ，即数= a  b。如果a和b均大于n的平方根，则ab将大于n。

因此，这些因子中至少有一个必须小于或等于一个数的平方根，要检查一个数是否为质数，我们只需要测试小于或等于被检查数的平方根的因子。

此外，素数永远不可能是偶数，因为偶数都可以被 2 整除。

牢记以上想法，让我们改进算法：

```java
public static List<Integer> primeNumbersBruteForce(int n) {
    List<Integer> primeNumbers = new LinkedList<>();
    if (n >= 2) {
        primeNumbers.add(2);
    }
    for (int i = 3; i <= n; i += 2) {
        if (isPrimeBruteForce(i)) {
            primeNumbers.add(i);
        }
    }
    return primeNumbers;
}
private static boolean isPrimeBruteForce(int number) {
    for (int i = 2; ii <= number; i++) {
        if (number % i == 0) {
            return false;
        }
    }
    return true;
}

```

### 3.3. 使用Java 8

让我们看看如何使用Java8 惯用语重写之前的解决方案：

```java
public static List<Integer> primeNumbersTill(int n) {
    return IntStream.rangeClosed(2, n)
      .filter(x -> isPrime(x)).boxed()
      .collect(Collectors.toList());
}
private static boolean isPrime(int number) {
    return IntStream.rangeClosed(2, (int) (Math.sqrt(number)))
      .allMatch(n -> x % n != 0);
}

```

### 3.4. 使用埃拉托色尼筛法

还有另一种有效的方法可以帮助我们有效地生成素数，它被称为埃拉托色尼筛法。它的时间效率是O(n logn)。

我们来看看这个算法的步骤：

1.  创建一个从 2 到n的连续整数列表：(2, 3, 4, …, n)
2.  最初，让p等于 2，第一个质数
3.  从p开始，以p为增量向上计数，并在列表中标记每个大于p本身的数字。这些数字将是 2p、3p、4p 等；请注意，其中一些可能已经被标记
4.  在列表中找到第一个大于p的数字，但没有标记。如果没有这样的数字，停止。否则，让p现在等于这个数(它是下一个素数)，并从步骤 3 开始重复

最后当算法终止时，列表中所有未标记的数字都是质数。

代码如下所示：

```java
public static List<Integer> sieveOfEratosthenes(int n) {
    boolean prime[] = new boolean[n + 1];
    Arrays.fill(prime, true);
    for (int p = 2; p  p <= n; p++) {
        if (prime[p]) {
            for (int i = p  2; i <= n; i += p) {
                prime[i] = false;
            }
        }
    }
    List<Integer> primeNumbers = new LinkedList<>();
    for (int i = 2; i <= n; i++) {
        if (prime[i]) {
            primeNumbers.add(i);
        }
    }
    return primeNumbers;
}

```

### 3.5. Eratosthenes 筛法的工作示例

让我们看看当 n=30 时它是如何工作的。

[![素数](https://www.baeldung.com/wp-content/uploads/2017/11/Primes.jpg)](https://www.baeldung.com/wp-content/uploads/2017/11/Primes.jpg)

考虑上图，这里是算法进行的传递：

1.  循环从 2 开始，所以我们不标记 2 并标记 2 的所有约数。它在图像中用红色标记
2.  循环移至 3，因此我们保留 3 未标记，并标记 3 的所有尚未标记的除数。它在图像中用绿色标记
3.  循环到4，已经标记好了，继续
4.  循环移动到 5，所以我们不标记 5，标记 5 的所有尚未标记的因数。它在图像中用紫色标记
5.  我们继续上面的步骤，直到循环达到等于n的平方根

## 4。总结

在本快速教程中，我们说明了生成素数直到“N”值的方法。