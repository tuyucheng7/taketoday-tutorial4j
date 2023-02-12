## 1. 概述

本教程介绍了Java中的线程池。我们将从标准Java库中的不同实现开始，然后查看Google的Guava库。

## 2. 线程池

在Java中，线程被映射到系统级线程，这是操作系统的资源。如果我们不受控制地创建线程，我们可能会很快耗尽这些资源。

操作系统也会在线程之间进行上下文切换-以模拟并行性。一个简单的观点是，我们产生的线程越多，每个线程花在实际工作上的时间就越少。

线程池模式有助于在多线程应用程序中节省资源，并将并行性包含在某些预定义的限制中。

当我们使用线程池时，**我们以并行任务的形式编写并发代码，并将它们提交给线程池的实例执行**。该实例控制多个重复使用的线程来执行这些任务。

<img src="../assets/img.png">

该模式允许我们**控制应用程序创建的线程数量及其生命周期**。我们还能够安排任务的执行，并将传入的任务保留在队列中

## 3. Java中的线程池

### 3.1 Executors、Executor和ExecutorService

Executors工具类包含多个用于创建预配置线程池实例的方法。该类是一个很好的起点，如果我们不需要应用任何自定义的线程池配置，我们可以使用它。

我们使用Executor和ExecutorService接口来处理Java中的不同线程池实现。通常，**我们应该保持代码与线程池的实际实现分离**，并在整个应用程序中使用这些接口。

#### 3.1.1 Executor

**Executor接口有一个单独的execute方法来提交Runnable实例以供执行**。

让我们看一个快速示例，了解如何使用Executors API获取一个Executor实例，该实例由一个单线程池和一个无界队列支持，用于按顺序执行任务。

在这里，我们运行一个简单的任务，在屏幕上打印“Hello World”。我们将任务作为Lambda(Java 8特性)提交，它被推断为Runnable：

```java
Executor executor = Executors.newSingleThreadExecutor();
executor.execute(() -> System.out.println("Hello World"));
```

#### 3.1.2 ExecutorService

ExecutorService接口包含大量方法来**控制任务的进度和管理线程池的终止**。使用此接口，我们可以提交要执行的任务，还可以使用返回的Future实例控制它们的执行。

现在我们将创建一个ExecutorService，提交一个任务，然后使用返回的Future对象的get()方法等待提交的任务完成并返回值：

```java
@Test
void whenUsingExecutorServiceAndFuture_thenCanWaitOnFutureResult() throws InterruptedException, ExecutionException {
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    Future<String> future = executorService.submit(() -> "Hello World");
    // some operations
    String result = future.get();

    assertEquals("Hello World", result);
}
```

当然，在实际应用场景中，我们通常不会立即调用Future.get()，而是等到我们真正需要计算的结果值时再调用它。

在这里，我们调用重载的submit()方法，它可以接收Runnable或Callable类型的参数。这两个都是函数式接口，我们可以将它们作为Lambda传递(从Java 8开始)。

Runnable的唯一方法run()不会抛出异常，也不会返回值。Callable接口可能更方便，因为它允许我们抛出异常并可以返回一个值。

因此，为了让编译器推断出传递给submit方法的Lambda参数为Callable类型，只需从Lambda中返回一个值。

有关使用ExecutorService接口和Future的更多示例，请参阅[Java ExecutorService指南](../../java-concurrency-simple/docs/Java_ExecutorService.md)。

### 3.2 ThreadPoolExecutor

ThreadPoolExecutor是一个可扩展的线程池实现，具有许多用于微调线程池属性的参数和钩子。

我们将在这里讨论的主要配置参数是**corePoolSize**、**maximumPoolSize**和**keepAliveTime**。

该线程池由固定数量的核心线程组成，这些线程始终保留在线程池内部。它还包含一些多余的线程，这些线程可能会生成，然后在不再需要时终止。

corePoolSize参数是将被实例化并保留在线程池中的核心线程数。当一个新任务进来时，**如果所有核心线程都忙并且内部队列已满，则允许线程池增长到maximumPoolSize**。

keepAliveTime参数是允许多余线程(实例化的线程超过corePoolSize)以空闲状态存在的时间间隔。默认情况下，ThreadPoolExecutor只考虑删除非核心线程。为了将相同的删除策略应用于核心线程，我们可以使用[allowCoreThreadTimeOut(true)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.html#allowCoreThreadTimeOut(boolean))方法。

这些参数涵盖了广泛的用例，但**最典型的配置是在Executors静态方法中预定义的**。

#### 3.2.1 newFixedThreadPool

让我们看一个例子。newFixedThreadPool方法创建一个ThreadPoolExecutor，其corePoolSize和MaximumPoolSide参数值相等，并且keepAliveTime为零。这意味着该线程池中的线程数始终相同：

```java
@Test
void whenUsingFixedThreadPool_thenCoreAndMaximumThreadSizeAreTheSame() {
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

    executor.submit(() -> {
        Thread.sleep(1000);
        return null;
    });
    executor.submit(() -> {
        Thread.sleep(1000);
        return null;
    });
    executor.submit(() -> {
        Thread.sleep(1000);
        return null;
    });

    assertEquals(2, executor.getPoolSize());
    assertEquals(1, executor.getQueue().size());
}
```

在这里，我们实例化一个固定线程数为2的ThreadPoolExecutor。这意味着如果同时运行的任务数始终小于或等于2，则它们会立即执行。否则，**其中一些任务可能会被放入队列中等待轮到它们**。

我们提交了三个Callable任务，它们通过睡眠1000毫秒来模拟繁重的工作。前两个任务将同时运行，第三个任务必须在队列中等待。我们可以通过在提交任务后立即调用getPoolSize()和getQueue().size()方法来验证它。

#### 3.2.2 Executors.newCachedThreadPool()

我们可以使用Executors.newCachedThreadPool()创建另一个预配置的ThreadPoolExecutor。此方法不接收任何参数配置线程池。我们将corePoolSize设置为0，并将maximumPoolSize设置为Integer.MAX_VALUE。最后，keepAliveTime为60秒：

```java
@Test
void whenUsingCachedThreadPool_thenPoolSizeGrowsUnbounded() {
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    executor.submit(() -> {
        Thread.sleep(1000);
        return null;
    });
    executor.submit(() -> {
        Thread.sleep(1000);
        return null;
    });
    executor.submit(() -> {
        Thread.sleep(1000);
        return null;
    });

    assertEquals(3, executor.getPoolSize());
    assertEquals(0, executor.getQueue().size());
}
```

这些参数值意味着**缓存线程池可以无限制地增长以容纳任意数量的提交任务**。但是当不再需要线程时，它们将在60秒不活动后被处理掉。一个典型的用例是当我们的应用程序中有很多短期任务时。

队列大小将始终为0，因为在内部使用了SynchronousQueue实例。在SynchronousQueue中，插入和删除操作对总是同时进行。因此，队列实际上从未包含任何内容。

#### 3.2.3 Executors.newSingleThreadExecutor()

Executors.newSingleThreadExecutor()创建另一种典型形式的ThreadPoolExecutor，其中包含一个线程。**单线程线程池非常适合创建事件循环**。corePoolSize和maximumPoolSize参数都等于1，keepAliveTime为0。

下例中的任务将按顺序运行，因此任务完成后counter的值将为2：

```java
@Test
void whenUsingSingleThreadPool_thenTasksExecuteSequentially() throws InterruptedException {
    CountDownLatch lock = new CountDownLatch(2);
    AtomicInteger counter = new AtomicInteger();

    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.submit(() -> {
        counter.set(1);
        lock.countDown();
    });
    executor.submit(() -> {
        counter.compareAndSet(1, 2);
        lock.countDown();
    });

    assertTimeout(Duration.ofMillis(1000), () -> lock.await(1000, TimeUnit.MILLISECONDS));
    assertEquals(2, counter.get());
}
```

此外，ThreadPoolExecutor用一个不可变的包装器装饰，因此在创建后无法对其进行重新配置。请注意，这也是我们无法将其强制转换为ThreadPoolExecutor的原因。

### 3.3 ScheduledThreadPoolExecutor

ScheduledThreadPoolExecutor扩展了ThreadPoolExecutor类，还通过几个附加方法实现了ScheduledExecutorService接口：

+ schedule()方法允许我们在指定的延迟后运行一次任务。
+ scheduleAtFixedRate()方法允许我们在指定的初始延迟后运行任务，然后在一定时间段内重复运行它。**period参数是在任务开始时间之间测量的时间**，因此执行速率是固定的。
+ scheduleWithFixedDelay()方法与scheduleAtFixedRate()类似，它重复运行给定的任务，但**指定的延迟是在上一个任务结束和下一个任务开始之间测量的**。执行率可能会有所不同，具体取决于运行任何给定任务所需的时间。

我们通常使用Executors.newScheduledThreadPool()方法来创建一个具有给定的corePoolSize、无界的maximumPoolSize和keepAliveTime为0的ScheduledThreadPoolExecutor。

以下是如何安排任务在500毫秒后执行：

```java
@Test
void whenSchedulingTask_thenTaskExecutesWithinGivenPeriod() throws InterruptedException {
    CountDownLatch lock = new CountDownLatch(1);

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
    executor.schedule(() -> {
        LOG.debug("Hello World");
        lock.countDown();
    }, 500, TimeUnit.MILLISECONDS);

    assertTimeout(Duration.ofMillis(1000), () -> lock.await(1000, TimeUnit.MILLISECONDS));
}
```

下面的代码演示了如何在延迟500毫秒后运行任务，然后每隔100毫秒重复一次。调度任务之后，我们使用CountDownLatch锁等待它触发三次。然后我们使用Future.cancel(true)方法来取消任务：

```java
@Test
void whenSchedulingTaskWithFixedPeriod_thenTaskExecutesMultipleTimes() throws InterruptedException {
    CountDownLatch lock = new CountDownLatch(3);

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
    ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> {
        LOG.debug("Hello World");
        lock.countDown();
    }, 500, 100, TimeUnit.MILLISECONDS);

    lock.await();
    future.cancel(true);
}
```

### 3.4 ForkJoinPool

ForkJoinPool是Java 7中引入的fork/join框架的核心部分。它解决了**递归算法中生成多个任务的常见问题**。在递归的情况下，如果使用一个简单的ThreadPoolExecutor，我们会很快用完线程，因为每个任务或子任务都需要自己的线程来运行。

在fork/join框架中，任何任务都可以生成(fork)多个子任务并使用join()方法等待它们完成。fork/join框架的好处是**它不会为每个任务或子任务创建一个新线程**，而是实现工作窃取算法。这个框架在我们的[Java中的Fork/Join框架指南](../../java-concurrency-advanced-2/docs/Java中的Fork-Join框架指南.md)中有详细的描述。

让我们看一个使用ForkJoinPool遍历节点树并计算所有叶节点值之和的简单示例。下面是一个由节点、int值和一组子节点组成的树的简单实现：

```java
public class TreeNode {
    private int value;
    private Set<TreeNode> children;

    TreeNode(int value, TreeNode... children) {
        this.value = value;
        this.children = Sets.newHashSet(children);
    }

    public int getValue() {
        return value;
    }

    public Set<TreeNode> getChildren() {
        return children;
    }
}
```

现在，如果我们想并行地对树中的所有值求和，我们需要实现一个RecursiveTask<Integer\>接口。每个任务接收自己的节点并将其值添加到其子节点的值之和中。要计算子节点值之和，任务实现执行以下操作：

+ 流化子节点集合
+ 映射子节点流，为每个元素创建一个新的CountingTask
+ 通过fork来运行每个子任务
+ 通过对每个fork任务调用join()方法来收集结果
+ 使用Collectors.summingInt()收集器对结果进行求和

```java
public class CountingTask extends RecursiveTask<Integer> {
    private final TreeNode node;

    CountingTask(TreeNode node) {
        this.node = node;
    }

    @Override
    protected Integer compute() {
        return node.getValue() + node.getChildren().stream()
              .map(childNode -> new CountingTask(childNode).fork())
              .mapToInt(ForkJoinTask::join)
              .sum();
    }
}
```

实际运行计算的代码非常简单：

```java
@Test
void whenUsingForkJoinPool_thenSumOfTreeElementsIsCalculatedCorrectly() {
    TreeNode tree = new TreeNode(5,
        new TreeNode(3),
        new TreeNode(2,
            new TreeNode(2),
            new TreeNode(8)));

    ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
    int sum = forkJoinPool.invoke(new CountingTask(tree));

    assertEquals(20, sum);
}
```

## 4. Guava中线程池的实现

[Guava](https://github.com/google/guava)是一个强大的Google开发的工具库。它有许多有用的并发类，包括几个方便的ExecutorService实现。实现类不可直接实例化或子类化，因此创建它们实例的唯一入口点是MoreExecutors工具类。

### 4.1 添加Guava依赖

我们将以下依赖项添加到我们的Maven pom文件中，以将Guava库包含到我们的项目中。在[Maven Central](https://search.maven.org/search?q=g:com.google.guava)中可以找到最新版本的Guava库：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

### 4.2 Direct Executor和Direct Executor Service

有时我们希望根据某些条件在当前线程或线程池中运行任务。我们更愿意使用单个Executor接口，只需切换实现即可。尽管想出在当前线程中运行任务的Executor或ExecutorService的实现并不难，但这仍然需要编写一些样板代码。

很高兴，Guava为我们提供了预定义的实例。

下面是一个演示在同一线程中执行任务的示例。提供的任务休眠了500毫秒，但它会**阻塞当前线程**，并且在执行调用完成后立即可以使用结果：

```java
@Test
void whenExecutingTaskWithDirectExecutor_thenTheTaskIsExecutedInTheCurrentThread() {
    Executor executor = MoreExecutors.directExecutor();
    AtomicBoolean executed = new AtomicBoolean();

    executor.execute(() -> {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executed.set(true);
    });

    assertTrue(executed.get());
}
```

directExecutor()方法返回的实例实际上是一个静态单例，因此使用此方法根本不会在对象创建上产生任何开销。

我们应该更倾向这种方法而不是MoreExecutors.newDirectExecutorService()，因为该API在每次调用时都会创建一个完整的Executor实现。

### 4.3 Exiting Executor Services

另一个常见问题是在线程池仍在运行其任务时**关闭虚拟机**。即使有一个取消机制，也不能保证当ExecutorService关闭时，任务会表现良好并停止工作。这可能会导致JVM在任务继续工作时无限期挂起。

为了解决这个问题，Guava引入了一系列现有的ExecutorService。**它们基于与JVM一起终止的守护线程**。

这些ExecutorService还使用Runtime.getRuntime().addShutdownHook()方法添加了一个关机钩子，并防止VM在放弃挂起的任务之前终止配置的时间量。

在下面的示例中，我们正在提交包含无限循环的任务，但我们使用配置为100毫秒的exitingExecutorService，以在JVM终止前等待任务。

```java
public class ExitingExecutorServiceExample {

    public static void main(String... args) {
        final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
        final ExecutorService executorService = MoreExecutors.getExitingExecutorService(executor, 100, TimeUnit.MILLISECONDS);
        executorService.submit((Runnable) () -> {
            while (true) {
            }
        });
    }
}
```

如果没有exitingExecutorService，此任务将导致虚拟机无限期挂起。

### 4.4 监听器装饰器

监听器装饰器允许我们包装ExecutorService并在任务提交时接收ListenableFuture实例，而不是简单的Future实例。ListenableFuture接口扩展了Future，并有一个额外的方法addListener()。此方法允许添加一个在Future完成时调用的监听器。

我们很少想直接使用ListenableFuture.addListener()方法，**但它对于Futures工具类中的大多数工具方法都是必不可少的**。

例如，使用Futures.allAsList()方法，我们可以将多个ListenableFuture实例组合在一个ListenableFuture中，该ListenableFuture在成功完成所有组合的Future后完成：

```java
@Test
void whenJoiningFuturesWithAllAsList_thenCombinedFutureCompletesAfterAllFuturesComplete() throws ExecutionException, InterruptedException {
    ExecutorService executorService = Executors.newCachedThreadPool();
    ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(executorService);
    
    ListenableFuture<String> future1 = listeningExecutorService.submit(() -> "Hello");
    ListenableFuture<String> future2 = listeningExecutorService.submit(() -> "World");
    
    String greeting = String.join(" ", Futures.allAsList(future1, future2).get());
    assertEquals("Hello World", greeting);
}
```

## 5. 总结

在本文中，我们讨论了线程池模式及其在标准Java库和Google的Guava库中的实现。