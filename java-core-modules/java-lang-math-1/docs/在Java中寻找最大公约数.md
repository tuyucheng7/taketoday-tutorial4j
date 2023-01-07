## 1. 概述

在数学中，两个非零整数的[GCD是](https://en.wikipedia.org/wiki/Greatest_common_divisor)将每个整数均分的最大正整数。

在本教程中，我们将介绍三种查找两个整数的最大公约数 (GCD) 的方法。此外，我们将研究它们在Java中的实现。

## 2.蛮力

对于我们的第一种方法，我们从 1 迭代到给定的最小数字，并检查给定的整数是否可以被索引整除。划分给定数字的最大索引是给定数字的 GCD：

```java
int gcdByBruteForce(int n1, int n2) {
    int gcd = 1;
    for (int i = 1; i <= n1 && i <= n2; i++) {
        if (n1 % i == 0 && n2 % i == 0) {
            gcd = i;
        }
    }
    return gcd;
}
```

正如我们所见，上述实现的复杂度为O(min(n1, n2))，因为我们需要迭代循环n次(相当于较小的数)才能找到 GCD。

## 3. 欧几里得算法

其次，我们可以使用欧几里得算法来找到 GCD。欧几里得算法不仅高效而且易于理解，易于在Java中使用递归实现。

欧几里德的方法取决于两个重要的定理：

-   首先，如果我们从较大的数字中减去较小的数字，GCD 不会改变——因此，如果我们继续减去这个数字，我们最终会得到他们的 GCD
-   第二，当小数刚好整除大数时，小数就是给定的两个数的GCD。

请注意，在我们的实现中，我们将使用模而不是减法，因为它基本上一次是很多减法：

```java
int gcdByEuclidsAlgorithm(int n1, int n2) {
    if (n2 == 0) {
        return n1;
    }
    return gcdByEuclidsAlgorithm(n2, n1 % n2);
}
```

另外，请注意我们如何在算法的递归步骤中在n1的位置使用n2并在 n2 的位置使用余数。

此外，[Euclid 算法](https://www.baeldung.com/cs/euclid-time-complexity)的复杂度为O(Log min(n1, n2))，与我们之前看到的蛮力方法相比要好。

## 4. Stein 算法或二进制 GCD 算法

最后，我们可以使用 Stein 算法，也称为二元 GCD 算法，来求两个非负整数的 GCD。该算法使用简单的算术运算，如算术移位、比较和减法。

Stein 算法重复应用以下与 GCD 相关的基本恒等式来找到两个非负整数的 GCD：

1.  gcd(0, 0) = 0, gcd(n1, 0) = n1, gcd(0, n2) = n2
2.  当n1和n2都是偶数时，则gcd(n1, n2) = 2  gcd(n1/2, n2/2)，因为 2 是公约数
3.  如果n1是偶数且n2是奇数，则gcd(n1, n2) = gcd(n1/2, n2)，因为 2 不是公约数，反之亦然
4.  如果n1和n2都是奇数，并且n1 >= n2，则gcd(n1, n2) = gcd((n1-n2)/2, n2)反之亦然

我们重复步骤 2-4 直到n1等于n2或n1 = 0。GCD 是(2 n )  n2。这里，n是在执行步骤 2 时发现 2 在n1和n2中常见的次数：

```java
int gcdBySteinsAlgorithm(int n1, int n2) {
    if (n1 == 0) {
        return n2;
    }

    if (n2 == 0) {
        return n1;
    }

    int n;
    for (n = 0; ((n1 | n2) & 1) == 0; n++) {
        n1 >>= 1;
        n2 >>= 1;
    }

    while ((n1 & 1) == 0) {
        n1 >>= 1;
    }

    do {
        while ((n2 & 1) == 0) {
            n2 >>= 1;
        }

        if (n1 > n2) {
            int temp = n1;
            n1 = n2;
            n2 = temp;
        }
        n2 = (n2 - n1);
    } while (n2 != 0);
    return n1 << n;
}
```

我们可以看到，我们使用算术移位运算来除以或乘以 2。此外，我们使用减法来减少给定的数字。

当n1 > n2时，Stein 算法的复杂度为O((log 2 n1) 2 )而。当n1 < n2时，它是O((log 2 n2) 2 )。

## 5.总结

在本教程中，我们研究了计算两个数的 GCD 的各种方法。我们还用Java实现了这些，并快速了解了它们的复杂性。