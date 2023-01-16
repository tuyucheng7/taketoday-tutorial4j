## 1. 概述

在本教程中，我们将讨论大 O 表示法的含义。我们将通过几个示例来研究它对代码运行时间的影响。

## 2.大O表示法的直觉

我们经常听到使用[Big O Notation](https://www.baeldung.com/cs/big-o-notation)描述的算法的性能。

对算法性能(或算法复杂性)的研究属于[算法分析](https://en.wikipedia.org/wiki/Analysis_of_algorithms)领域。算法分析回答了算法消耗多少资源(例如磁盘空间或时间)的问题。

我们会将时间视为一种资源。通常，算法完成所需的时间越少越好。

## 3. 恒定时间算法——O(1)

算法的输入大小如何影响其运行时间？了解 Big O 的关键是了解事物的增长速度。这里讨论的速率是每个输入大小所花费的时间。

考虑这段简单的代码：

```java
int n = 1000;
System.out.println("Hey - your input is: " + n);
```

显然，上面的n是什么并不重要。这段代码需要恒定的时间来运行。它不依赖于 n 的大小。

相似地：

```java
int n = 1000;
System.out.println("Hey - your input is: " + n);
System.out.println("Hmm.. I'm doing more stuff with: " + n);
System.out.println("And more: " + n);
```

上面的例子也是常数时间。即使运行时间是原来的 3 倍， 也不取决于输入的大小 n。 我们将恒定时间算法表示如下：O(1)。请注意，O(2)、O(3)甚至O(1000)都意味着同一件事。

我们不关心运行到底需要多长时间，只关心它需要常数时间。

## 4. 对数时间算法——O (log n)

恒定时间算法(渐近地)是最快的。对数时间次之。不幸的是，它们有点难以想象。

对数时间算法的一个常见示例是[二进制搜索](https://en.wikipedia.org/wiki/Binary_search_algorithm)算法。要查看如何在Java中实现二进制搜索，[请单击此处。](https://www.baeldung.com/java-binary-search)

这里重要的是运行时间的增长与输入的对数成正比(在本例中，log 以 2 为底)：

```java
for (int i = 1; i < n; i = i  2){
    System.out.println("Hey - I'm busy looking at: " + i);
}
```

如果n为 8，则输出如下：

```java
Hey - I'm busy looking at: 1
Hey - I'm busy looking at: 2
Hey - I'm busy looking at: 4
```

我们的简单算法运行了 log(8) = 3 次。

## 5. 线性时间算法——O(n)

在对数时间算法之后，我们得到下一个最快的类别：线性时间算法。

如果我们说某些东西线性增长，我们的意思是它的增长与其输入的大小成正比。

想一个简单的 for 循环：

```java
for (int i = 0; i < n; i++) {
    System.out.println("Hey - I'm busy looking at: " + i);
}
```

这个for循环运行了多少次？n次，当然！我们不确切知道它需要多长时间才能运行——我们不担心这个。

我们所知道的是，上面介绍的简单算法将随着输入的大小线性增长。

我们更喜欢 0.1n 的运行时间而不是( 1000n + 1000)，但两者仍然是线性算法；它们的增长都与其投入的规模成正比。

同样，如果算法更改为以下内容：

```java
for (int i = 0; i < n; i++) {
    System.out.println("Hey - I'm busy looking at: " + i);
    System.out.println("Hmm.. Let's have another look at: " + i);
    System.out.println("And another: " + i);
}
```

运行时的输入n的大小仍然是线性的。我们将线性算法表示如下：O(n)。

与恒定时间算法一样，我们不关心运行时的细节。O(2n+1)与O(n)相同，因为 Big O Notation 关注输入大小的增长。

## 6. N Log N 时间算法——O(n log n)

n log n是下一类算法。运行时间的增长与输入的n log n 成正比：

```java
for (int i = 1; i <= n; i++){
    for(int j = 1; j < n; j = j  2) {
        System.out.println("Hey - I'm busy looking at: " + i + " and " + j);
    }
}

```

例如，如果 n为 8，则此算法将运行8  log(8) = 8  3 = 24次。为了大 O 表示法，for 循环中是否存在严格不等式无关紧要。

## 7. 多项式时间算法 – O(n p )

接下来我们有多项式时间算法。这些算法甚至比n log n算法还要慢。

多项式是一个总称，包含二次(n 2 )、三次 (n 3 )、四次(n 4 )等函数。重要的是要知道O(n 2 )比 O(n 3 )快，后者又比 O(n 4 )快，等等。

让我们看一下二次时间算法的一个简单示例：

```java
for (int i = 1; i <= n; i++) {
    for(int j = 1; j <= n; j++) {
        System.out.println("Hey - I'm busy looking at: " + i + " and " + j);
    }
}

```

该算法将运行 8 2 = 64次。请注意，如果我们要嵌套另一个 for 循环，这将变成一个 O(n 3 )算法。

## 8. 指数时间算法 – O( k n )

现在我们正进入危险的境地；这些算法的增长与输入大小取幂的某些因素成正比。

例如，O(2 n )算法每增加一个输入就会加倍。所以，如果n = 2，这些算法将运行四次；如果n = 3，它们将运行八次(有点像对数时间算法的反面)。

每增加一个输入，O (3 n )算法就会增加三倍，每增加一个输入， O(k n )算法就会增加 k 倍。

让我们看一个 O(2 n )时间算法的简单示例：

```java
for (int i = 1; i <= Math.pow(2, n); i++){
    System.out.println("Hey - I'm busy looking at: " + i);
}
```

该算法将运行 2 8 = 256次。

## 9. 阶乘时间算法——O (n!)

在大多数情况下，这几乎是最糟糕的。这类算法的运行时间与 输入大小的[阶乘成正比。](https://en.wikipedia.org/wiki/Factorial)

一个典型的例子是 使用蛮力方法解决[旅行商问题。](https://en.wikipedia.org/wiki/Travelling_salesman_problem)

对旅行商问题的解决方案的解释超出了本文的范围。

相反，让我们看一个简单的O(n!)算法，如前几节所述：

```java
for (int i = 1; i <= factorial(n); i++){
    System.out.println("Hey - I'm busy looking at: " + i);
}
```

其中 factorial(n)只是计算 n!。如果 n 为 8，则此算法将运行8！= 40320次。

## 10.渐近函数

Big O 就是所谓的 渐近函数。所有这一切意味着，它关注的是算法 在极限时的性能——即，当大量输入被抛给它时。

Big O 不关心你的算法对小规模输入的处理情况。它涉及大量输入(考虑对一百万个数字的列表进行排序与对 5 个数字的列表进行排序)。

另一件需要注意的事情是还有其他的渐近函数。大 Θ (theta) 和大 Ω (omega) 也都描述了算法的极限(记住， 这个极限只意味着巨大的输入)。

要理解这3个重要函数的区别，我们首先要知道Big O、Big Θ和Big Ω各自描述的是一个 集合(即元素的集合)。

在这里，我们集合的成员是算法本身：

-   Big O 描述了运行不低于特定速度的所有算法的集合(这是一个上限)
-   相反，Big Ω 描述了所有运行 速度不超过特定速度(这是下限)的算法的集合
-   最后，大 Θ 描述了以一定速度运行的所有算法的集合 (这就像平等)

我们上面给出的定义在数学上并不准确，但它们将有助于我们的理解。

通常，你会听到使用 Big O 描述的内容，但了解 Big Θ 和 Big Ω 也没什么坏处。

## 11.总结

在本文中，我们讨论了大 O 表示法，以及理解算法的复杂性如何影响代码的运行时间。

[可以在此处找到](http://bigocheatsheet.com/)不同复杂性类别的出色可视化。