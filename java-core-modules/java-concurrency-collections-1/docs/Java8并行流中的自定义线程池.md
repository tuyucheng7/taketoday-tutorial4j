## 1. 概述

Java 8引入了Stream的概念，作为对数据执行批量操作的一种有效方式，并且可以在支持并发的环境中获取并行流。

这些流可以带来更高的性能，但要以多线程开销为代价。

在这个快速教程中，我们将了解**Stream API的最大限制之一**，并了解如何使并行流与自定义ThreadPool实例一起工作，或者，[有一个库可以处理这个问题](https://github.com/pivovarit/parallel-collectors)。

## 2. 并行流

让我们从一个简单的例子开始-在任何Collection类型上调用parallelStream方法都将返回一个可能并行的Stream：

```java
@Test
void givenList_whenCallingParallelStream_shouldBeParallelStream() {
    List<Long> aList = new ArrayList<>();
    Stream<Long> parallelStream = aList.parallelStream();

    assertTrue(parallelStream.isParallel());
}
```

在这种流中发生的处理默认使用ForkJoinPool.commonPool()，**这是一个由整个应用程序共享的线程池**。

## 3. 自定义线程池

**我们实际上可以在处理流时传递一个自定义的ThreadPool**。

以下示例让并行流使用自定义ThreadPool来计算从1到100万的long值之和：

```java
@Test
void giveRangeOfLongs_whenSummedInParallel_shouldBeEqualToExpectedTotal() throws InterruptedException, ExecutionException {
    long firstNum = 1;
    long lastNum = 1_000_000;

    List<Long> aList = LongStream.rangeClosed(firstNum, lastNum).boxed()
        .collect(Collectors.toList());

    ForkJoinPool customThreadPool = new ForkJoinPool(4);
    long actualTotal = customThreadPool.submit(
        () -> aList.parallelStream().reduce(0L, Long::sum)).get();

    assertEquals((lastNum + firstNum) * lastNum / 2, actualTotal);
}
```

我们使用并行级别为4的ForkJoinPool构造函数。需要进行一些实验来确定不同环境的最佳值，但一个好的经验法则是根据CPU的内核数来选择数量。

接下来，我们处理并行流的内容，并在reduce调用中将其相加。

这个简单的示例可能无法演示使用自定义线程池的全部用途，但在我们不想将公共线程池与长时间运行的任务(例如处理来自网络源的数据)捆绑在一起，或者公共线程池正被应用程序中的其他组件使用的情况下，其好处就显而易见了。

如果我们运行上面的测试方法，它就会通过。到目前为止，一切都很好。

但是，如果我们像在测试方法中一样在普通方法中实例化ForkJoinPool类，则可能会导致OutOfMemoryError。

接下来，让我们仔细看看内存泄漏的原因。

## 4. 谨防内存泄漏

正如我们之前谈到的，公共线程池默认情况下被整个应用程序使用。**公共线程池是一个静态的ThreadPool实例**。

因此，如果我们使用默认线程池，则不会发生内存泄漏。

现在，让我们回顾一下我们的测试方法。在测试方法中，我们创建了一个ForkJoinPool对象，当测试方法完成时，**customThreadPool对象不会被取消引用和垃圾回收-相反，它将等待分配新任务**。

也就是说，我们每次调用测试方法时，都会创建一个新的customThreadPool对象，并且不会被释放。

问题的解决方法非常简单：即在我们执行完方法后关闭customThreadPool对象：

```java
try {
    long actualTotal = customThreadPool.submit(
        () -> aList.parallelStream().reduce(0L, Long::sum)).get();
    assertEquals((lastNum + firstNum) * lastNum / 2, actualTotal);
} finally {
    customThreadPool.shutdown();
}
```

## 5. 总结

我们已经简要介绍了如何使用自定义ThreadPool运行并行流。在正确的环境中，通过正确使用并行级别，在某些情况下可以获得性能提升。

如果我们创建一个自定义的ThreadPool，我们应该记住调用它的shutdown()方法以避免内存泄漏。