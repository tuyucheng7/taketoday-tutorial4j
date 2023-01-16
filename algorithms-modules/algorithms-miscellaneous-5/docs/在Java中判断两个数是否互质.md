## 1. 概述

给定两个整数a和b ，如果能整除两者的唯一因数是 1，我们就说它们是互质数。互质数或互质数是互质数的同义词。

在本快速教程中，我们将逐步介绍使用Java解决此问题的方法。

## 2. 最大公约数算法

事实证明，如果2 个数a和b的最大公约数 ( gcd )为 1(即gcd(a, b) = 1)，则a和b互质。因此，判断两个数是否互质只需要判断gcd是否为 1。

## 3. 欧几里得算法实现

在本节中，我们将使用[欧几里得算法](https://en.wikipedia.org/wiki/Euclidean_algorithm)来计算 2 个数的gcd。

在我们展示我们的实现之前，让我们总结一下算法并看一个如何应用它的快速示例以便理解。

所以，假设我们有两个整数a和b。在迭代方法中，我们首先将a除以b并得到余数。接下来，我们为a分配 b 的值，并为b分配余数。我们重复这个过程直到b = 0。最后，当我们到达这一点时，我们返回 a 的值作为gcd结果，如果a = 1，我们可以说a和b互质。

让我们在两个整数a = 81 和 b = 35上尝试一下。

在这种情况下，81 和 35 的余数 (81 % 35) 是 11。因此，在第一个迭代步骤中，我们以a = 35和 b = 11结束。因此，我们将进行另一次迭代。

35除以11 的余数是2。结果，我们现在 有 a = 11(我们交换了值)和 b = 2。我们继续吧。

再执行一步将导致a = 2和 b = 1。现在，我们接近尾声了。

最后，再进行一次迭代后，我们将达到a = 1 和 b = 0。该算法返回 1，我们可以得出总结， 81 和 35确实互质。

### 3.1. 命令式实施

首先，让我们实现欧几里得算法的命令式Java版本，如上所述：

```java
int iterativeGCD(int a, int b) {
    int tmp;
    while (b != 0) {
        if (a < b) {
            tmp = a;
            a = b;
            b = tmp;
        }
        tmp = b;
        b = a % b;
        a = tmp;
    }
    return a;
}

```

正如我们所注意到的，在 a 小于 b的情况下，我们在继续之前交换值。当 b 为 0 时算法停止。

### 3.2. 递归实现

接下来，让我们看一下递归实现。这可能更干净，因为它避免了显式变量值交换：

```java
int recursiveGCD(int a, int b) {
    if (b == 0) {
        return a;
    }
    if (a < b) {
        return recursiveGCD(b, a);
    }
    return recursiveGCD(b, a % b);
}

```

## 4. 使用 BigInteger的实现

但是等等——gcd算法不是已经用Java实现了吗？是的！BigInteger类提供了一个gcd方法，该方法实现了用于查找最大公约数的欧几里得算法。

使用这种方法，我们可以更容易地起草相对质数算法：

```java
boolean bigIntegerRelativelyPrime(int a, int b) {
    return BigInteger.valueOf(a).gcd(BigInteger.valueOf(b)).equals(BigInteger.ONE);
}

```

## 5.总结

在本快速教程中，我们介绍了使用gcd算法的三种实现来解决查找两个数是否互质问题的解决方案。