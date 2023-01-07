## 一、概述

[帕斯卡三角形](https://en.wikipedia.org/wiki/Pascal's_triangle)是三角形形式的二项式系数排列。帕斯卡三角形的数字是这样排列的，每个数字都是它上面两个数字的总和。

在本教程中，我们将了解如何在 Java 中打印 Pascal 三角形。

## 2.使用递归

我们可以使用公式nCr : n 的递归打印帕斯卡三角形！/ ( ( n – r ) !r ! )

首先，让我们创建一个递归函数：

```java
public int factorial(int i) {
    if (i == 0) {
        return 1;
    }
    return i  factorial(i - 1);
}
```

然后我们可以使用该函数打印三角形：

```java
private void printUseRecursion(int n) {
    for (int i = 0; i <= n; i++) {
        for (int j = 0; j <= n - i; j++) {
            System.out.print(" ");
        }

        for (int k = 0; k <= i; k++) {
            System.out.print(" " + factorial(i) / (factorial(i - k)  factorial(k)));
        }

        System.out.println();
    }
}
```

n = 5 的结果如下所示：

```plaintext
       1
      1 1
     1 2 1
    1 3 3 1
   1 4 6 4 1
  1 5 10 10 5 1
```

## 3.避免使用递归

不用递归打印帕斯卡三角形的另一种方法是使用二项式展开。

我们总是在每行的开头设置值 1，那么第 (n) 行和 (i) 位置的 k 值将计算为：

```plaintext
k = ( k  (n - i) / i ) 
```

让我们使用这个公式创建我们的函数：

```java
public void printUseBinomialExpansion(int n) {
    for (int line = 1; line <= n; line++) {
        for (int j = 0; j <= n - line; j++) {
            System.out.print(" ");
        }

        int k = 1;
        for (int i = 1; i <= line; i++) {
            System.out.print(k + " ");
            k = k  (line - i) / i;
        }

        System.out.println();
    }
}
```

## 4。结论

在本快速教程中，我们学习了两种在 Java 中打印 Pascal 三角形的方法。