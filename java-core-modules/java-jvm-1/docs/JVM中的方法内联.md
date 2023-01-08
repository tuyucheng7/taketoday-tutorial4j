## 1. 概述

在本教程中，我们将了解Java虚拟机中的方法内联及其工作原理。

我们还将了解如何从 JVM 获取和读取与内联相关的信息，以及我们可以如何使用这些信息来优化我们的代码。

## 2. 什么是方法内联？

基本上，内联是一种在运行时优化已编译源代码的方法，方法是用其主体替换最常执行的方法的调用。

尽管涉及编译，但它不是由传统的javac编译器执行的，而是由 JVM 本身执行的。更准确地说，这是JVM 的一部分 Just-In-Time (JIT) 编译器的职责；javac只生成一个字节码，让 JIT 发挥作用并优化源代码。

这种方法最重要的后果之一是，如果我们使用旧Java编译代码，相同的 . class文件在较新的 JVM 上会更快。这样我们就不需要重新编译源代码，而只需要更新Java。

## 3. JIT 是如何做到的？

本质上，JIT 编译器试图内联我们经常调用的方法，这样我们就可以避免方法调用的开销。在决定是否内联方法时，它会考虑两件事。

首先，它使用计数器来跟踪我们调用该方法的次数。当该方法被调用超过特定次数时，它就会变得“热”。该阈值默认设置为 10,000，但我们可以在Java启动期间通过 JVM 标志对其进行配置。我们绝对不想内联所有内容，因为这会很耗时并且会产生巨大的字节码。

我们应该记住，只有当我们达到稳定状态时，内联才会发生。这意味着我们需要多次重复执行，以便为 JIT 编译器提供足够的分析信息。

此外，“热”并不能保证该方法将被内联。如果它太大，JIT 将不会内联它。可接受的大小受-XX:FreqInlineSize=标志限制，该标志指定要为方法内联的字节码指令的最大数量。

尽管如此，强烈建议不要更改此标志的默认值，除非我们完全确定它会产生什么影响。默认值取决于平台——对于 64 位 Linux，它是 325。

JIT通常内联静态、私有或最终方法。虽然公共方法也可以内联，但并不是每个公共方法都必须内联。JVM 需要确定这种方法只有一个实现。任何额外的子类都会阻止内联，性能将不可避免地下降。

## 4. 寻找热门方法

我们当然不想猜测 JIT 在做什么。因此，我们需要一些方法来查看哪些方法是内联的或未内联的。通过在启动期间设置一些额外的 JVM 标志，我们可以轻松实现这一点并将所有这些信息记录到标准输出：

```plaintext
-XX:+PrintCompilation -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining
```

当 JIT 编译发生时，第一个标志将被记录。第二个标志启用其他标志，包括-XX:+PrintInlining，它将打印内联的方法和位置。

这将以树的形式向我们展示内联方法。叶子用以下选项之一进行注解和标记：

-   内联(热) ——这个方法被标记为热并且是内联的
-   太大——该方法不热，但它生成的字节码太大，所以它没有内联
-   热方法太大——这是一个热方法，但由于字节码太大，它没有内联

我们应该关注第三个值，并尝试优化带有“hot method too big”标签的方法。

一般情况下，如果我们发现了一个条件语句非常复杂的热点方法，我们应该尽量将if语句的内容分离出来，增加粒度，以便JIT对代码进行优化。switch和for循环语句也是如此。

我们可以得出总结，手动方法内联是我们不需要为了优化代码而做的事情。JVM 的效率更高，而且我们可能会使代码变得冗长且难以理解。

### 4.1. 例子

现在让我们看看如何在实践中检查这一点。我们将首先创建一个简单的类来计算前N 个连续正整数的总和：

```java
public class ConsecutiveNumbersSum {

    private long totalSum;
    private int totalNumbers;

    public ConsecutiveNumbersSum(int totalNumbers) {
        this.totalNumbers = totalNumbers;
    }

    public long getTotalSum() {
        totalSum = 0;
        for (int i = 0; i < totalNumbers; i++) {
            totalSum += i;
        }
        return totalSum;
    }
}
```

接下来，一个简单的方法将利用类来执行计算：

```java
private static long calculateSum(int n) {
    return new ConsecutiveNumbersSum(n).getTotalSum();
}
```

最后，我们将多次调用该方法，看看会发生什么：

```java
for (int i = 1; i < NUMBERS_OF_ITERATIONS; i++) {
    calculateSum(i);
}
```

在第一次运行中，我们将运行它 1,000 次(小于上述阈值 10,000)。如果我们在输出中搜索calculateSum()方法，我们将找不到它。这是意料之中的，因为我们调用它的次数不够多。

如果我们现在将迭代次数更改为 15,000 并再次搜索输出，我们将看到：

```plaintext
664 262 % inlining.cn.tuyucheng.taketoday.InliningExample::main @ 2 (21 bytes)
  @ 10   inlining.cn.tuyucheng.taketoday.InliningExample::calculateSum (12 bytes)   inline (hot)
```

我们可以看到，这次该方法满足了内联的条件，JVM 对其进行了内联。

再次值得一提的是，如果方法太大，无论迭代次数如何，JIT 都不会内联它。我们可以通过在运行应用程序时添加另一个标志来检查这一点：

```plaintext
-XX:FreqInlineSize=10
```

正如我们在前面的输出中看到的，我们的方法的大小是 12 个字节。-XX: FreqInlineSize标志将符合内联条件的方法大小限制为 10 个字节。因此，这次内联不应该发生。事实上，我们可以通过再看一下输出来确认这一点：

```plaintext
330 266 % inlining.cn.tuyucheng.taketoday.InliningExample::main @ 2 (21 bytes)
  @ 10   inlining.cn.tuyucheng.taketoday.InliningExample::calculateSum (12 bytes)   hot method too big
```

尽管出于说明目的我们在此处更改了标志值，但我们必须强调建议不要更改-XX:FreqInlineSize标志的默认值，除非绝对必要。

## 5.总结

在本文中，我们了解了 JVM 中的方法内联是什么以及 JIT 是如何进行内联的。我们描述了如何检查我们的方法是否符合内联条件，并建议如何通过尝试减少经常调用的长方法的大小来利用此信息，这些方法太大而无法内联。

最后，我们说明了如何在实践中识别热门方法。