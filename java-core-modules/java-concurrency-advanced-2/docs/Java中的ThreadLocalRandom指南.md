## 1. 概述

生成随机值是一项非常常见的任务。这就是Java提供java.util.Random类的原因。

**但是，这个类在多线程环境中表现不佳**。

简而言之，Random在多线程环境中性能不佳的原因是由于争用-假设多个线程共享同一个Random实例。

为了解决这个限制，**Java在JDK 7中引入了java.util.concurrent.ThreadLocalRandom类-用于在多线程环境中生成随机数**。

让我们看看ThreadLocalRandom是如何执行的，以及如何在实际应用程序中使用它。

## 2. ThreadLocalRandom优于Random

**ThreadLocalRandom是[ThreadLocal](https://www.baeldung.com/java-threadlocal)和Random类的组合(稍后将详细介绍)，并且与当前线程隔离**。因此，它通过简单地避免对Random实例的任何并发访问，在多线程环境中实现了更好的性能。

一个线程获得的随机数不受另一个线程的影响，而java.util.Random提供全局随机数。

此外，与Random不同，ThreadLocalRandom不支持显式设置种子。相反，它重写了从Random继承的setSeed(long seed)方法，以便在调用时始终抛出UnsupportedOperationException。

### 2.1 线程争用

到目前为止，我们已经确定Random类在高并发环境中表现不佳。为了更好地理解这一点，让我们看看它的一个主要操作[next(int)](https://github.com/openjdk/jdk/blob/a8a2246158bc53414394b007cbf47413e62d942e/src/java.base/share/classes/java/util/Random.java#L198)是如何实现的：

```java
private final AtomicLong seed;

protected int next(int bits) {
    long oldseed, nextseed;
    AtomicLong seed = this.seed;
    do {
        oldseed = seed.get();
        nextseed = (oldseed * multiplier + addend) & mask;
    } while (!seed.compareAndSet(oldseed, nextseed));

    return (int)(nextseed >>> (48 - bits));
}
```

这是[线性同余生成器](https://en.wikipedia.org/wiki/Linear_congruential_generator)算法的Java实现。很明显，所有线程都共享同一个seed实例变量。

为了生成下一个随机比特，它首先尝试通过compareAndSet或简称CAS以原子方式更改共享种子(seed)值。

**当多个线程尝试使用CAS并发更新种子时，一个线程获胜并更新种子，其余线程则失败。丢失的线程将一遍又一遍地尝试相同的过程，直到它们有机会更新值并最终生成随机数**。

该算法是无锁的，不同线程可以并发进行。然而，**当争用较高时，CAS失败和重试的次数将显著影响总体性能**。

另一方面，ThreadLocalRandom完全消除了这种争用，因为每个线程都有自己的Random实例，因此也有自己的受限种子。

现在让我们看一下生成随机int、long和double值的一些方法。

## 3. 使用ThreadLocalRandom生成随机值

根据Oracle文档，**我们只需要调用ThreadLocalRandom.current()方法，它将返回当前线程的ThreadLocalRandom实例**。然后，我们可以通过调用类的可用实例方法来生成随机值。

让我们生成一个无边界的随机int值：

```java
int unboundedRandomValue = ThreadLocalRandom.current().nextInt();
```

接下来，让我们看看如何生成一个随机有界int值，这意味着一个介于给定下限和上限之间的值。

下面是生成0到100之间的随机int值的示例：

```java
int boundedRandomValue = ThreadLocalRandom.current().nextInt(0, 100);
```

请注意，0是包含的下限，100是排除的上限。

我们可以通过调用nextLong()和nextDouble()方法来为long和double生成随机值，方法与上面的示例类似。

Java 8还添加了nextGaussian()方法用于生成下一个正态分布值，该值与生成器序列的平均值为0.0，标准偏差为1.0。

与Random类一样，我们也可以使用doubles()、ints()和longs()方法来生成随机值流。

## 4. 使用JMH比较ThreadLocalRandom和Random

让我们看看如何使用这两个类在多线程环境中生成随机值，然后使用JMH比较它们的性能。

首先，让我们创建一个示例，其中所有线程共享一个Random实例。在这里，我们将使用Random实例生成随机值的任务提交给ExecutorService：

```java
ExecutorService executor = Executors.newWorkStealingPool();
List<Callable<Integer>> callables = new ArrayList<>();
Random random = new Random();
for (int i = 0; i < 1000; i++) {
    callables.add(() -> {
         return random.nextInt();
    });
}
executor.invokeAll(callables);
```

让我们使用JMH基准测试来检查上述代码的性能：

```shell
# Run complete. Total time: 00:00:36
Benchmark                                            Mode Cnt Score    Error    Units
ThreadLocalRandomBenchMarker.randomValuesUsingRandom avgt 20  771.613 ± 222.220 us/op
```

同样，现在让我们使用ThreadLocalRandom而不是Random实例，它为线程池中的每个线程使用一个ThreadLocalRandom实例：

```java
ExecutorService executor = Executors.newWorkStealingPool();
List<Callable<Integer>> callables = new ArrayList<>();
for (int i = 0; i < 1000; i++) {
    callables.add(() -> {
        return ThreadLocalRandom.current().nextInt();
    });
}
executor.invokeAll(callables);
```

以下是使用ThreadLocalRandom的结果：

```shell
# Run complete. Total time: 00:00:36
Benchmark                                                       Mode Cnt Score    Error   Units
ThreadLocalRandomBenchMarker.randomValuesUsingThreadLocalRandom avgt 20  624.911 ± 113.268 us/op
```

最后，通过比较上面Random和ThreadLocalRandom的JMH结果，我们可以清楚地看到，使用Random生成1000个随机值所花费的平均时间为772微秒，而使用ThreadLocalRandom大约是625微秒。

因此，**我们可以得出结论，ThreadLocalRandom在高并发环境下效率更高**。

要了解有关**JMH**的更多信息，请在[此处](https://www.baeldung.com/java-microbenchmark-harness)查看我们之前的文章。

## 5. 实现细节

将ThreadLocalRandom视为ThreadLocal和Random类的组合是一个很好的理解模型。事实上，这种模型与Java 8之前的实际实现是一致的。

**然而，从Java 8开始，随着ThreadLocalRandom成为单例，这就变得基本不一样了**。下面是[current()](https://github.com/openjdk/jdk14u/blob/89deef4dd8b7aac7c3cea6e13c494a438d34d4c4/src/java.base/share/classes/java/util/concurrent/ThreadLocalRandom.java#L176)方法在Java 8+中的形式：

```java
static final ThreadLocalRandom instance = new ThreadLocalRandom();

public static ThreadLocalRandom current() {
    if (U.getInt(Thread.currentThread(), PROBE) == 0)
        localInit();

    return instance;
}
```

的确，共享一个全局Random实例会在高争用情况下导致次优性能。但是，为每个线程使用一个专用实例也是一种矫枉过正的做法。

**每个线程只需要维护自己的种子值，而不是每个线程的专用Random实例**。从Java 8开始，[Thread](https://github.com/openjdk/jdk14u/blob/d48548f5b7713e0d51b107a5e2dfd60383edbd88/src/java.base/share/classes/java/lang/Thread.java#L2059)类本身已经过改造以维护种子值：

```java
public class Thread implements Runnable {
    // omitted
    @jdk.internal.vm.annotation.Contended("tlr")
    long threadLocalRandomSeed;

    @jdk.internal.vm.annotation.Contended("tlr")
    int threadLocalRandomProbe;

    @jdk.internal.vm.annotation.Contended("tlr")
    int threadLocalRandomSecondarySeed;
}
```

threadLocalRandomSeed变量负责维护ThreadLocalRandom的当前种子值。此外，二级种子threadLocalRandomSecondarySeed通常由ForkJoinPool等内部使用。

此实现包含了一些优化，以使ThreadLocalRandom具有更好的性能：

+ 通过使用@Contented注解避免[虚假共享](https://alidg.me/blog/2020/4/24/thread-local-random#false-sharing)，该注解基本上添加了足够的填充(padding)以将争用变量隔离在它们自己的缓存行中
+ 使用sun.misc.Unsafe来更新这三个变量，而不是使用反射API
+ 避免与ThreadLocal实现相关联的额外哈希表查找

## 6. 总结

本文说明了java.util.Random和java.util.concurrent.ThreadLocalRandom之间的区别。

我们还看到了ThreadLocalRandom在多线程环境中相对于Random的优势，以及性能和我们如何使用该类生成随机值。

ThreadLocalRandom是对JDK的简单补充，但它可以在高度并发的应用程序中产生显著影响。