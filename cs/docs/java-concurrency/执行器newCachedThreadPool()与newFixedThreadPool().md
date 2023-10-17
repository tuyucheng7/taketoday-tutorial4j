## 1. 概述

在[线程池](https://www.baeldung.com/java-executor-service-tutorial)实现方面，Java 标准库提供了大量可供选择的选项。固定和缓存线程池在这些实现中非常普遍。

在本教程中，我们将了解线程池是如何在幕后工作的，然后比较这些实现及其用例。

## 2.缓存线程池

让我们看看Java在调用[Executors.newCachedThreadPool()](https://github.com/openjdk/jdk/blob/6bab0f539fba8fb441697846347597b4a0ade428/src/java.base/share/classes/java/util/concurrent/Executors.java#L217)时是如何创建缓存线程池的：

```java
public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, 
      new SynchronousQueue<Runnable>());
}
```

缓存线程池使用“同步切换”来排队新任务。同步切换的基本思想很简单但又违反直觉：当且仅当另一个线程同时获取该项目时，才可以对项目进行排队。换句话说，SynchronousQueue Java不能Java 容纳任何任务。

假设有一个新任务进来。如果队列中有一个空闲线程在等待，那么任务生产者就会将任务交给那个线程。否则，由于队列总是满的，执行者创建一个新线程来处理该任务。

缓存池从零线程开始，并可能增长到具有 Integer.MAX_VALUE 线程。实际上，缓存线程池的唯一限制是可用的系统资源。

为了更好地管理系统资源，缓存线程池将删除保持空闲一分钟的线程。

### 2.1. 用例

缓存线程池配置将线程(因此得名)缓存一小段时间，以便将它们重新用于其他任务。因此，当我们处理合理数量的短期任务时，它的效果最好。 

这里的关键是“合理”和“短暂”。为了阐明这一点，让我们评估一个缓存池不适合的场景。在这里，我们将提交一百万个任务，每个任务需要 100 微秒才能完成：

```java
Callable<String> task = () -> {
    long oneHundredMicroSeconds = 100_000;
    long startedAt = System.nanoTime();
    while (System.nanoTime() - startedAt <= oneHundredMicroSeconds);

    return "Done";
};

var cachedPool = Executors.newCachedThreadPool();
var tasks = IntStream.rangeClosed(1, 1_000_000).mapToObj(i -> task).collect(toList());
var result = cachedPool.invokeAll(tasks);
```

这将创建大量线程，转化为不合理的内存使用，更糟糕的是，大量 CPU 上下文切换。这两种异常都会严重损害整体性能。

因此，当执行时间不可预测时，我们应该避免使用线程池，比如 IO-bound 任务。

## 3.固定线程池

让我们看看[固定线程](https://github.com/openjdk/jdk/blob/6bab0f539fba8fb441697846347597b4a0ade428/src/java.base/share/classes/java/util/concurrent/Executors.java#L91)池是如何工作的：

```java
public static ExecutorService newFixedThreadPool(int nThreads) {
    return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, 
      new LinkedBlockingQueue<Runnable>());
}
```

与缓存线程池相反，这个线程池使用了一个无限队列，队列中有固定数量的永不过期线程。因此，固定线程池不会尝试使用固定数量的线程来执行传入的任务，而不是不断增加线程数。当所有线程都忙时，执行器将新任务排队。这样，我们就可以更好地控制程序的资源消耗。

因此，固定线程池更适合执行时间不可预测的任务。

## 4. 不幸的相似之处

到目前为止，我们只是列举了缓存线程池和固定线程池之间的区别。

除了所有这些差异，它们都使用 [AbortPolicy](https://www.baeldung.com/java-rejectedexecutionhandler#1-abort-policy) 作为它们的[饱和策略](https://www.baeldung.com/java-rejectedexecutionhandler)。因此，我们希望这些执行器在无法接受甚至无法排队更多任务时抛出异常。

让我们看看现实世界中会发生什么。

缓存线程池在极端情况下会继续创建越来越多的线程，因此实际上它们永远不会达到饱和点。同样，固定线程池会不断地在它们的队列中添加越来越多的任务。因此，固定池也永远不会达到饱和点。

由于两个池都不会饱和，当负载异常高时，它们会消耗大量内存来创建线程或排队任务。雪上加霜的是，缓存的线程池还会招致大量的处理器上下文切换。

无论如何，为了更好地控制资源消耗，强烈建议创建自定义 [ThreadPoolExecutor](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.html)：

```java
var boundedQueue = new ArrayBlockingQueue<Runnable>(1000);
new ThreadPoolExecutor(10, 20, 60, SECONDS, boundedQueue, new AbortPolicy());

```

在这里，我们的线程池最多可以有 20 个线程，最多只能排队 1000 个任务。此外，当它不能再接受任何负载时，它只会抛出一个异常。

## 5.总结

在本教程中，我们浏览了 JDK 源代码以了解不同的Executor是 如何在底层工作的。然后，我们比较了固定线程池和缓存线程池及其用例。

最后，我们尝试用自定义线程池解决那些池的资源消耗失控问题。