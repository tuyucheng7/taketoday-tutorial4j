## 1. 概述

在本文中，我们将研究java.util.concurrent包中的两个构造：[LongAdder](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/atomic/LongAdder.html)和[LongAccumulator](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/atomic/LongAccumulator.html)。

两者都是为了在多线程环境中非常高效而创建的，并且都利用非常巧妙的策略来**实现无锁同时仍然保持线程安全**。

## 2. LongAdder

让我们考虑一些经常递增某些值的逻辑，其中使用AtomicLong可能是一个瓶颈。它使用了CAS操作，在严重争用的情况下，这可能会导致大量CPU周期浪费。

另一方面，LongAdder使用了一个非常巧妙的技巧来减少线程之间的争用，当线程递增它时。

当我们想要自增LongAdder的实例时，我们需要调用increment()方法。该实现**保留了一个可以按需增长的计数器数组**。

因此，当更多的线程调用increment()时，数组会更长。数组中的每条记录都可以单独更新，从而减少争用。因此，LongAdder是从多个线程递增计数器的一种非常有效的方法。

让我们创建一个LongAdder类的实例，并从多个线程更新它：

```java
@Test
void givenMultipleThread_whenTheyWriteToSharedLongAdder_thenShouldCalculateSumForThem() throws InterruptedException {
    LongAdder counter = new LongAdder();
    ExecutorService executorService = Executors.newFixedThreadPool(8);

    int numberOfThreads = 4;
    int numberOfIncrements = 100;

    Runnable incrementAction = () -> IntStream
        .range(0, numberOfIncrements)
        .forEach(x -> counter.increment());

    for (int i = 0; i < numberOfThreads; i++) {
        executorService.submit(incrementAction);
    }

    executorService.awaitTermination(500, TimeUnit.MILLISECONDS);
    executorService.shutdown();

    assertEquals(counter.sum(), numberOfIncrements  numberOfThreads);
    assertEquals(counter.sum(), numberOfIncrements  numberOfThreads);
}
```

在我们调用sum()方法之前，LongAdder中计数器的结果不可用。该方法将迭代底层数组的所有值，并对这些值求和，返回正确的值。但我们需要小心，因为调用sum()方法的成本可能非常高：

```java
assertEquals(counter.sum(), numberOfIncrements  numberOfThreads);
```

有时，在我们调用sum()之后，我们希望清除与LongAdder实例关联的所有状态，并从头开始计数。我们可以使用sumThenReset()方法来实现这一点：

```java
@Test
void givenMultipleThread_whenTheyWriteToSharedLongAdder_thenShouldCalculateSumForThemAndResetAdderAfterward() throws InterruptedException {
    LongAdder counter = new LongAdder();
    ExecutorService executorService = Executors.newFixedThreadPool(8);

    int numberOfThreads = 4;
    int numberOfIncrements = 100;

    Runnable incrementAction = () -> IntStream
        .range(0, numberOfIncrements)
        .forEach(i -> counter.increment());

    for (int i = 0; i < numberOfThreads; i++) {
        executorService.execute(incrementAction);
    }

    executorService.awaitTermination(500, TimeUnit.MILLISECONDS);
    executorService.shutdown();

    assertEquals(counter.sumThenReset(), numberOfIncrements  numberOfThreads);
    await().until(() -> assertEquals(counter.sum(), 0));
}
```

请注意，对sum()方法的后续调用返回0，这意味着状态已成功重置。

此外，Java还提供了[DoubleAdder](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/atomic/DoubleAdder.html)来维护double值的求和，其API与LongAdder类似。

## 3. LongAccumulator

LongAccumulator也是一个非常有趣的类-它允许我们在许多场景中实现无锁算法。例如，它可用于根据提供的LongBinaryOperator来累积结果-这与Stream API中的reduce()操作类似。

LongAccumulator的实例可以通过向其构造函数提供LongBinaryOperator和初始值来创建。重要的是要记住，**如果我们为LongAccumulator提供一个累积顺序无关紧要的交换函数，它就会正确工作**。

```java
LongAccumulator accumulator = new LongAccumulator(Long::sum, 0L);
```

我们正在创建一个LongAccumulator，它将向累加器中已有的值添加一个新值。我们将LongAccumulator的初始值设置为0，因此在第一次调用accumulate()方法时，previousValue的值将为0。

让我们从多个线程调用accumulate()方法：

```java
@Test
void givenLongAccumulator_whenApplyActionOnItFromMultipleThreads_thenShouldProduceProperResult() throws InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(8);
    LongBinaryOperator sum = Long::sum;
    LongAccumulator accumulator = new LongAccumulator(sum, 0L);
    int numberOfThreads = 4;
    int numberOfIncrements = 100;

    Runnable accumulateAction = () -> IntStream
        .rangeClosed(0, numberOfIncrements)
        .forEach(accumulator::accumulate);

    for (int i = 0; i < numberOfThreads; i++) {
        executorService.execute(accumulateAction);
    }

    executorService.awaitTermination(500, TimeUnit.MILLISECONDS);
    executorService.shutdown();
    assertEquals(accumulator.get(), 20200);
}
```

请注意我们是如何将一个数字作为参数传递给accumulate()方法的。该方法将调用我们的sum()函数。

LongAccumulator使用CAS实现-这导致了这些有趣的语义。

首先，它执行定义为LongBinaryOperator的操作，然后检查previousValue的值是否更改。如果已更改，则使用新值再次执行该操作。如果没有，它会成功更改存储在accumulator中的值。

我们现在可以断言所有迭代的所有值的总和为20200：

```java
assertEquals(accumulator.get(), 20200);
```

有趣的是，Java还提供了具有相同目的和API的[DoubleAccumulator](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/atomic/DoubleAccumulator.html)用于double值。

## 4. 动态条纹(Dynamic Striping)

Java中的所有加法器和累加器实现都继承自一个名为[Striped64](https://github.com/openjdk/jdk14u/blob/master/src/java.base/share/classes/java/util/concurrent/atomic/Striped64.java)的基类。**该类使用一个状态数组将争用分配到不同的内存位置，而不是仅使用一个值来维护当前状态**。

以下是Striped64功能的简单描述 ：

<img src="../assets/img_1.png">

不同的线程更新不同的内存位置。由于我们使用的是状态数组(即条带)，因此这个想法被称为动态条带化。Striped64正是根据这个想法命名的，它可以处理64位数据类型。

我们期望动态条带化能够提高整体性能。但是，JVM分配这些状态的方式可能会产生适得其反的效果。

更具体地说，JVM可能会在堆中彼此接近地分配这些状态。这意味着一些状态可能驻留在同一个CPU缓存行中。因此，**更新一个内存位置可能会导致缓存未命中其附近的状态。这种被称为[虚假共享](https://alidg.me/blog/2020/4/24/thread-local-random#false-sharing)的现象会损害性能**。

为防止虚假共享。Striped64实现在每个状态周围添加了足够的填充(padding)，以确保每个状态都驻留在自己的缓存行中：

<img src="../assets/img_2.png">

[@Contended](https://github.com/openjdk/jdk14u/blob/master/src/java.base/share/classes/jdk/internal/vm/annotation/Contended.java)注解负责添加此填充。填充以牺牲更多内存消耗为代价来提高性能。

## 5. 总结

在这个快速教程中，我们了解了LongAdder和LongAccumulator，并展示了如何使用这两种结构来实现非常高效且无锁的解决方案。