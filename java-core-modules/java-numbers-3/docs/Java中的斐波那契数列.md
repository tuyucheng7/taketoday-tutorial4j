## 1. 概述

在本教程中，我们将研究斐波那契数列。

具体来说，我们将实现三种方法来计算斐波那契数列的第n项，最后一种是常数时间解。

## 2.斐波那契数列

斐波那契数列是一系列数字，其中每一项都是前两项的总和。它的前两项是0和1。

例如，该系列的前 11 项是0、1、1、2、3、5、8、13、21、34和55。

在数学术语中，斐波那契数列S n由递归关系定义：

```java
S(n) = S(n-1) + S(n-2), with S(0) = 0 and S(1) = 1
```

现在，让我们看看如何计算斐波那契数列的第n项。我们将关注的三种方法是递归、迭代和使用 Binet 公式。

### 2.1. 递归方法

对于我们的第一个解决方案，我们直接用Java简单地表达递归关系：

```java
public static int nthFibonacciTerm(int n) {
    if (n == 1 || n == 0) {
        return n;
    }
    return nthFibonacciTerm(n-1) + nthFibonacciTerm(n-2);
}
```

如我们所见，我们检查n是否等于0或1。如果为真，则返回该值。在任何其他情况下，我们递归调用函数来计算第( n -1)项和第 ( n -2)项并返回它们的和。

递归的方法虽然实现起来简单，但是我们看到这种方法做了很多重复的计算。例如，为了计算第6项，我们调用计算第5项和第4项。此外，计算第5项的调用会再次调用计算第4项。由于这个事实，[递归方法](https://www.baeldung.com/java-recursion)做了很多多余的工作。

事实证明，这使得它的[时间复杂度](https://www.baeldung.com/cs/fibonacci-computational-complexity)呈指数级增长；准确地说是O(Φ n ) 。

### 2.2. 迭代法

在迭代方法中，我们可以避免递归方法中所做的重复计算。相反，我们计算序列的项并[存储前两项以计算下一项](https://www.baeldung.com/java-knapsack#dp)。

我们来看看它的实现：

```java
public static int nthFibonacciTerm(int n) {
    if(n == 0 || n == 1) {
        return n;
    }
    int n0 = 0, n1 = 1;
    int tempNthTerm;
    for (int i = 2; i <= n; i++) {
        tempNthTerm = n0 + n1;
        n0 = n1;
        n1 = tempNthTerm;
    }
    return n1;
}
```

首先，我们检查要计算的项是第0项还是第1项。如果是这样，我们返回初始值。否则，我们使用n0和n1计算第二项。然后，我们修改n0和n1变量的值以分别存储第1项和第 2项。我们继续迭代，直到我们计算出所需的术语。

迭代方法通过将最后两个斐波那契项存储在变量中来避免重复工作。迭代法的时间复杂度和空间复杂度分别为O(n)和O(1)。

### 2.3. 比奈公式

我们只根据前两个定义了第n个斐波那契数。现在，我们将看看 Binet 的公式来计算恒定时间内的第n个斐波那契数。

斐波那契项维持一个称为黄金比例的比率，用 Φ 表示，希腊字符发音为“phi” 。

首先，让我们看看黄金比例是如何计算的：

```java
Φ = ( 1 + √5 )/2 = 1.6180339887...
```

现在，让我们看看Binet 的公式：

```java
Sn = Φⁿ–(– Φ⁻ⁿ)/√5
```

实际上，这意味着我们应该能够通过一些算术运算得到第n个斐波那契数。

让我们用Java来表达：

```java
public static int nthFibonacciTerm(int n) {
    double squareRootOf5 = Math.sqrt(5);
    double phi = (1 + squareRootOf5)/2;
    int nthTerm = (int) ((Math.pow(phi, n) - Math.pow(-phi, -n))/squareRootOf5);
    return nthTerm;
}
```

我们首先计算squareRootof5和phi并将它们存储在变量中。稍后，我们应用 Binet 的公式来获得所需的项。

由于我们在这里处理无理数，我们只会得到一个近似值。因此，我们需要为更高的斐波那契数保留[更多的小数位，以解决舍入误差。](https://www.baeldung.com/java-bigdecimal-biginteger)

我们看到上述方法在常数时间内计算第n个斐波那契项，或O(1)。

## 3.总结

在这篇简短的文章中，我们研究了斐波那契数列。我们研究了递归和迭代解决方案。然后，我们应用 Binet 的公式来创建一个恒定时间的解决方案。