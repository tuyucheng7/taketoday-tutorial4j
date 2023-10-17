## 1. 概述

在本教程中，我们将编写一个Java程序来查找给定整数的所有因数。

## 二、问题简介

在我们开始编写Java代码之前，让我们先了解一下整数的因数是什么。

给定一个整数n ，如果整数 i可以完全整除数字i，则该整数i就是n的因数。这里完全可整除意味着当我们用n除以i时，余数为零。

几个例子可以快速解释它：

-   n = 10，其因子：1、2、5和10
-   n = 13，其因数：1和13
-   n = 1 , n只有一个因子：1
-   n = 0 , 零没有因数

如示例所示，通常情况下，整数n的因子始终包含1和n，即使n是质数，例如13也是如此。但是，零是一个特殊的整数。它没有因素。

现在我们了解了因子的概念，让我们创建一个Java程序来查找给定整数的所有因子。

为简单起见，我们将使用单元测试断言来验证我们的解决方案是否按预期工作。

## 3.创建一个方法来找到一个整数的所有因子

找到整数n的所有因子的最直接方法是从 1 循环到n并测试哪个数字可以完全整除 n。我们可以将那些可以整除n的数字存储在一个 Set中。当循环结束时，这个Set将包含n的所有因子。

在Java中实现这个想法对我们来说不是一项具有挑战性的工作：

```java
static Set<Integer> getAllFactorsVer1(int n) {
    Set<Integer> factors = new HashSet<>();
    for (int i = 1; i <= n; i++) {
        if (n % i == 0) {
            factors.add(i);
        }
    }
    return factors;
}

```

接下来，让我们编写一些测试来检查我们的方法是否按预期工作。首先，让我们创建一个Map来准备一些要测试的数字及其预期因子：

```java
final static Map<Integer, Set<Integer>> FACTOR_MAP = ImmutableMap.of(
    0, ImmutableSet.of(),
    1, ImmutableSet.of(1),
    20, ImmutableSet.of(1, 2, 4, 5, 10, 20),
    24, ImmutableSet.of(1, 2, 3, 4, 6, 8, 12, 24),
    97, ImmutableSet.of(1, 97),
    99, ImmutableSet.of(1, 3, 9, 11, 33, 99),
    100, ImmutableSet.of(1, 2, 4, 5, 10, 20, 25, 50, 100)
);

```

现在，对于上面FACTOR_MAP中的每个数字，我们调用我们已经实现的getAllFactorsVer1()方法来查看它是否可以找到所需的因子：

```java
FACTOR_MAP.forEach((number, expected) -> assertEquals(expected, FactorsOfInteger.getAllFactorsVer1(number)));
```

如果我们运行它，测试就会通过。所以，这个方法解决了问题，太棒了！

眼尖的人可能会发现我们将方法命名为Ver1。 通常，这意味着我们将在教程中介绍不同的版本。换言之，该方案仍有改进的空间。

接下来，让我们看看如何优化版本 1 的实现。

## 4.优化-版本2

让我们回顾一下方法中的主要逻辑：

```java
for (int i = 1; i <= n; i++) {
   if (n % i == 0) {
       factors.add(i);
   }
}
```

如上面的代码所示，我们将执行n % i计算n次。现在，如果我们检查一个整数的因子，我们会发现因子总是成对出现。我们以n=100为例来理解这个因子特性：

```bash
   1    2    4    5    10    20    25    50    100
   │    │    │    │    |      │     │     │     │
   │    │    │    │  [10,10]  │     │     │     │
   │    │    │    │           │     │     │     │
   │    │    │    └──[5, 20] ─┘     │     │     │
   │    │    │                      │     │     │
   │    │    └───────[4, 25]────────┘     │     │
   │    │                                 │     │
   │    └────────────[2, 50]──────────────┘     │
   │                                            │
   └─────────────────[1, 100]───────────────────┘

```

正如我们所见，100的所有因数都是成对出现的。因此，如果我们找到n的一个因子i，我们就可以得到成对的一个i'= n/i。也就是说，我们不需要循环 n次。相反，我们检查从 1 到数字n的平方根 并找到所有i和i'对。这样，给定 n=100，我们只循环十次。

接下来，让我们优化我们的版本 1 方法：

```java
static Set<Integer> getAllFactorsVer2(int n) {
    Set<Integer> factors = new HashSet<>();
    for (int i = 1; i <= Math.sqrt(n); i++) {
        if (n % i == 0) {
            factors.add(i);
            factors.add(n / i);
        }
    }
    return factors;
}

```

如上面的代码所示，我们使用了Java标准库中的Math.sqrt()方法来计算[n](https://www.baeldung.com/java-find-if-square-root-is-integer)[的平方根](https://www.baeldung.com/java-find-if-square-root-is-integer)。

现在，让我们使用相同的测试数据测试第二个版本的实现：

```java
FACTOR_MAP.forEach((number, expected) -> assertEquals(expected, FactorsOfInteger.getAllFactorsVer2(number)));
```

如果我们运行测试，它就会通过。所以优化后的版本 2 按预期工作。

我们已经成功地将因子确定时间从n减少到n的平方根。这是一个显着的改进。但是，仍有进一步优化的空间。那么，接下来，让我们进一步分析一下。

## 5.进一步优化-版本3

首先，让我们做一些简单的数学分析。

众所周知，给定的整数n可以是偶数或奇数。如果 n是偶数，我们不能断言它的因子是偶数还是奇数。例如，20的因数是1、2、4、5、10、20。所以有偶数和奇数。

但是，如果 n是奇数，则它的所有因子也必须是奇数。例如99的因数是1、3、9、11、33、99，所以都是奇数。

因此，我们可以根据n是否为奇数来调整循环的增量步长。当我们的循环从i = 1开始时，如果给定一个奇数，我们可以设置增量step = 2以跳过对所有偶数的检查。

接下来，让我们在版本 3 中实现这个想法：

```java
static Set<Integer> getAllFactorsVer3(int n) {
    Set<Integer> factors = new HashSet<>();
    int step = n % 2 == 0 ? 1 : 2;
    for (int i = 1; i <= Math.sqrt(n); i += step) {
        if (n % i == 0) {
            factors.add(i);
            factors.add(n / i);
        }
    }
    return factors;
}

```

通过这种优化，如果n是偶数，循环将执行sqrt(n)次，与版本 2 相同。

但是，如果 n 是奇数，则循环总共执行sqrt(n)/2次。

最后，让我们测试我们的版本 3 解决方案：

```java
FACTOR_MAP.forEach((number, expected) -> assertEquals(expected, FactorsOfInteger.getAllFactorsVer3(number)));
```

如果我们试一试，测试就会通过。所以，它正确地完成了工作。

## 六，总结

在本文中，我们创建了一个Java方法来查找整数的所有因子。此外，我们还讨论了对初始解决方案的两个优化。