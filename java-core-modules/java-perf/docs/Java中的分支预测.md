## 1. 概述

分支预测是计算机科学中一个有趣的概念，可以对我们的应用程序的性能产生深远的影响。然而，它通常没有得到很好的理解，大多数开发人员也很少关注它。

在这篇文章中，我们将探讨它到底是什么，它如何影响我们的软件，以及我们可以做些什么。

## 2. 什么是指令流水线？

当我们编写任何计算机程序时，我们都是在编写一组我们希望计算机按顺序执行的命令。

早期的计算机会一次运行这些。这意味着每个命令都被加载到内存中，完整地执行，只有当它完成时才会加载下一个命令。

指令流水线是对此的改进。它们允许处理器将工作分成几部分，然后并行执行不同的部分。然后，这将允许处理器在加载下一个准备就绪的命令时执行一个命令。

处理器内部更长的流水线不仅可以简化每个部分，还可以并行执行其中的更多部分。这可以提高系统的整体性能。

例如，我们可以有一个简单的程序：

```java
int a = 0;
a += 1;
a += 2;
a += 3;
```

这可能由包含获取、解码、执行、存储段的管道处理为：

[![分支预测1](https://www.baeldung.com/wp-content/uploads/2019/12/branch_prediction1.png)](https://www.baeldung.com/wp-content/uploads/2019/12/branch_prediction1.png)

我们可以在这里看到四个命令的整体执行是如何并行运行的，从而使整个序列更快。

## 3. 有什么危害？

处理器需要执行的某些命令会导致流水线问题。这些是任何命令，其中管道的一部分的执行依赖于较早的部分，但这些较早的部分可能尚未执行。

树枝是一种特殊形式的危险。它们导致执行朝两个方向之一进行，并且在解决分支之前不可能知道哪个方向。这意味着任何通过分支加载命令的尝试都是不安全的，因为我们无法知道从哪里加载它们。

让我们改变我们的简单程序来引入一个分支：

```java
int a = 0;
a += 1;
if (a < 10) {
  a += 2;
}
a += 3;
```

其结果与之前相同，但我们 在中间引入了一个if语句。计算机将看到它，并且在它被解析之前将无法加载通过它的命令。因此，流程将类似于：

[![分支预测2](https://www.baeldung.com/wp-content/uploads/2019/12/branch_prediction2.png)](https://www.baeldung.com/wp-content/uploads/2019/12/branch_prediction2.png)

我们可以立即看到这对我们程序的执行的影响，以及执行相同结果需要多少个时钟步骤。

## 4. 什么是分支预测？

分支预测是对上述内容的增强，我们的计算机将尝试预测分支将走哪条路，然后采取相应的行动。

在我们上面的示例中，处理器可能会预测 if (a < 10)可能为 真，因此它的行为就好像指令 a += 2是下一个要执行的指令一样。这将导致流程看起来像：

[![分支预测3](https://www.baeldung.com/wp-content/uploads/2019/12/branch_prediction3.png)](https://www.baeldung.com/wp-content/uploads/2019/12/branch_prediction3.png)

我们可以立即看到这提高了我们程序的性能——它现在需要 9 个滴答而不是 11 个滴答，所以速度提高了 19%。

不过，这并非没有风险。如果分支预测出错，那么它将开始排队不应该执行的指令。如果发生这种情况，那么计算机将需要丢弃它们并重新开始。

让我们改变我们的条件，让它现在为false：

```java
int a = 0;
a += 1;
if (a > 10) {
  a += 2;
}
a += 3;
```

这可能会执行类似：

[![分支预测4](https://www.baeldung.com/wp-content/uploads/2019/12/branch_prediction4.png)](https://www.baeldung.com/wp-content/uploads/2019/12/branch_prediction4.png)

现在这比以前的流程慢了，尽管我们做得更少了！处理器错误地预测分支将评估为true，开始排队 a += 2指令，然后不得不丢弃它并在分支评估为false 时重新开始。

## 5. 对代码的实际影响

既然我们知道什么是分支预测以及有什么好处，那么它对我们有什么影响呢？毕竟，我们谈论的是在高速计算机上损失几个处理器周期，所以它肯定不会引人注意。

有时这是真的。但有时它会对我们应用程序的性能产生惊人的影响。这在很大程度上取决于我们在做什么。具体来说，这取决于我们在短时间内做了多少。

### 5.1. 计数列表条目

让我们尝试对列表中的条目进行计数。我们将生成一个数字列表，然后计算其中有多少小于某个截止值。这与上面的示例非常相似，但我们是在一个循环中执行它，而不是仅仅作为一条指令：

```java
List<Long> numbers = LongStream.range(0, top)
    .boxed()
    .collect(Collectors.toList());

if (shuffle) {
    Collections.shuffle(numbers);
}

long cutoff = top / 2;
long count = 0;

long start = System.currentTimeMillis();
for (Long number : numbers) {
    if (number < cutoff) {
        ++count;
    }
}
long end = System.currentTimeMillis();

LOG.info("Counted {}/{} {} numbers in {}ms",
    count, top, shuffle ? "shuffled" : "sorted", end - start);
```

请注意，我们只是对进行计数的循环计时，因为这是我们感兴趣的。那么，这需要多长时间？

如果我们生成足够小的列表，那么代码运行速度太快以至于无法计时 — 大小为 100,000 的列表仍然显示 0 毫秒的时间。然而，当列表变得足够大以至于我们可以对其进行计时时，我们可以看到基于是否对列表进行洗牌的显着差异。对于 10,000,000 个数字的列表：

-   已排序 – 44 毫秒
-   洗牌 - 221ms

也就是说，混洗列表的计数时间比排序列表长 5 倍，即使实际计数的数字相同。

然而，对列表进行排序的行为比仅仅执行计数要昂贵得多。我们应该始终分析我们的代码并确定是否有任何性能提升是有益的。

### 5.2. 分支顺序

根据以上内容，if/else语句中分支的顺序 应该很重要似乎是合理的。也就是说，与重新排序分支相比，我们可以预期以下表现会更好：

```java
if (mostLikely) {
  // Do something
} else if (lessLikely) {
  // Do something
} else if (leastLikely) {
  // Do something
}
```

然而，现代计算机可以通过使用分支预测缓存来避免这个问题。事实上，我们也可以测试一下：

```java
List<Long> numbers = LongStream.range(0, top)
  .boxed()
  .collect(Collectors.toList());
if (shuffle) {
    Collections.shuffle(numbers);
}

long cutoff = (long)(top  cutoffPercentage);
long low = 0;
long high = 0;

long start = System.currentTimeMillis();
for (Long number : numbers) {
    if (number < cutoff) {
        ++low;
    } else {
        ++high;
    }
}
long end = System.currentTimeMillis();

LOG.info("Counted {}/{} numbers in {}ms", low, high, end - start);
```

当计算 10,000,000 个数字时，无论cutoffPercentage的值如何，这段代码的执行时间大约为排序后的数字约 35 毫秒，打乱后的数字约 200 毫秒。

这是因为分支预测器正在平等地处理两个分支，并正确地猜测我们将为它们走哪条路。

### 5.3. 组合条件

如果我们可以在一个或两个条件之间进行选择怎么办？可能以具有相同行为的不同方式重写我们的逻辑，但我们应该这样做吗？

例如，如果我们将两个数字与 0 进行比较，另一种方法是将它们相乘并将结果与 0 进行比较。这就是用乘法代替条件。但这值得吗？

让我们考虑一个例子：

```java
long[] first = LongStream.range(0, TOP)
  .map(n -> Math.random() < FRACTION ? 0 : n)
  .toArray();
long[] second = LongStream.range(0, TOP)
  .map(n -> Math.random() < FRACTION ? 0 : n)
  .toArray();

long count = 0;
long start = System.currentTimeMillis();
for (int i = 0; i < TOP; i++) {
    if (first[i] != 0 && second[i] != 0) {
        ++count;
    }
}
long end = System.currentTimeMillis();

LOG.info("Counted {}/{} numbers using separate mode in {}ms", count, TOP, end - start);
```

如上所述，我们在循环内的条件可以被替换。这样做实际上会影响运行时间：

-   单独的条件 - 40ms
-   多重和单一条件 - 22ms

所以使用两个不同条件的选项实际上需要两倍的时间来执行。

## 六，总结

我们已经了解了什么是分支预测以及它如何对我们的程序产生影响。这可以为我们提供一些额外的工具，以确保我们的程序尽可能高效。

然而，与往常一样，我们需要记住在进行重大更改之前分析我们的代码。有时可能会发生这样的情况，即以其他方式进行更改以帮助分支预测成本更高。