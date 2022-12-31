## 1. 简介

**每个OS线程都是昂贵的资源**，默认情况下，一个线程有1MB的RAM与之关联：它是堆栈大小。

在某些JVM实现中，那块内存不仅要分配，还要用零填充。除此之外，必须进行系统调用以分配本机线程，所有这些使得线程的创建成为一个相当冗长的过程。

最后，如果我们必须支持正常关闭，则有关线程过早取消的信息必须来自某个地方。

因此，**为我们的应用程序预先创建足够的线程是至关重要的**，这样启动一个线程就不会影响每个访问我们软件的用户的性能。集中线程管理也是一个好主意，为此，我们可以使用ExecutorService。在Kotlin中创建和管理线程池与Java没有太大区别，但是，语法略有不同，将执行程序服务注入业务逻辑组件要容易得多，因为我们可以利用强大的Kotlin构造函数。

## 2. ExecutorService剖析

一个典型的执行者服务包含很多部分：

```kotlin
val workerPool: ExecutorService = ThreadPoolExecutor(
    corePoolSize,
    maximumPoolSize,
    keepAliveTime,
    TimeUnit.SECONDS,
    workQueue,
    threadFactory,
    handler
)
```

这将创建一个具有初始线程数corePoolSize的线程池，系统将在workQueue非空时立即填充该线程池，每次我们向队列提交一个新任务时，都会创建一个新线程，直到线程数达到maximumPoolSize；工厂threadFactory将创建这些线程。

已经空闲keepAliveTime的线程将终止，垃圾收集器将收集它们，如果workQueue有界并溢出，处理程序将处理此类情况。

## 3. Executors API

幸运的是，我们不需要每次都选择所有这些参数，**工具类Executors提供了几个方便的预设**，让我们看看其中的一些。

### 3.1 固定线程池

**当任务流稳定且始终大致处于同一水平时**，固定线程池是好的：

```kotlin
val workerPool: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
```

固定线程池何时有用的一个明显示例是当我们始终尝试最大限度地利用我们的资源时，最佳线程数取决于任务中阻塞的非CPU操作的数量。

另一个常见的情况是单线程池。当我们需要并行执行但任务不频繁或对性能不关键时，它会派上用场：

```kotlin
val workerPool: ExecutorService = Executors.newSingleThreadExecutor()

// Later, in some method
// Do some in-thread CPU-intensive work
println("I am working hard")
workerPool.submit {
    Thread.sleep(100) // Imitate slow IO
    println("I am reporting on the progress")
}
println("Meanwhile I continue to work")
```

### 3.2 缓存的线程池是负载可调的

**缓存的线程池将根据提交任务的要求来利用资源**：

```kotlin
val workerPool: ExecutorService = Executors.newCachedThreadPool()
```

它将尝试为提交的任务重用现有线程，但如果需要，最多会创建Integer.MAX_VALUE线程，这些线程将在终止前存活60秒。因此，它提供了一个非常锋利的工具，不包含任何背压机制，负载突然达到峰值可能会使系统因OutOfMemoryError而崩溃。

我们可以通过手动创建ThreadPoolExecutor来实现类似的效果，但可以更好地控制：

```kotlin
val corePoolSize = 4
val maximumPoolSize = corePoolSize * 4
val keepAliveTime = 100L
val workQueue = SynchronousQueue<Runnable>()
val workerPool: ExecutorService = ThreadPoolExecutor(
    corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue
)
```

在该示例中，系统最多只会创建maxThreads个线程，当我们为池的任务队列使用SynchronousQueue时，模仿Executors，在达到maxThreads并发任务的限制时，下一个提交者将被阻塞，直到其中一个线程再次空闲。

### 3.2 工作窃取线程池

**工作窃取线程池是一个特殊的ExecutorService**，它不是ThreadPoolExecutors的一个版本，它像执行任何其他ExecutorService一样执行Runnable，**但其真正目的是执行ForkJoinTask**。由于它们的内部结构，它们可以分解为子任务并由池中的所有线程同时执行。

### 3.3 调度线程池

调度线程池是我们应该创建线程池而不是线程的另一点，**一个调度的线程池将以给定的延迟或以固定的速率执行一个任务(或多个任务)**：

```kotlin
val counter = AtomicInteger(0)
val worker = Executors.newSingleThreadScheduledExecutor()
worker.scheduleAtFixedRate({ counter.incrementAndGet() }, 0, 100, TimeUnit.MILLISECONDS)
```

如果在其中一个任务调用期间发生错误，运行它的线程将终止，但调度不会中断，对于下一次调用，系统将创建一个新线程。

## 4. 常见的线程池自定义

我们已经看到了ThreadPoolExecutor的各种版本，我们可以设置初始线程池大小和池中最大线程数的限制，我们还略微谈到了任务队列和线程工厂，让我们看看还有哪些其他选项可以让线程池更好地满足我们的目的。

### 4.1 线程名称

**ThreadFactory最常见的修改之一是更改线程池中线程的名称**，使其符合团队或公司的日志记录约定：

```kotlin
val worker = Executors.newFixedThreadPool(4, object : ThreadFactory {
    private val counter = AtomicInteger(0)
    override fun newThread(r: Runnable): Thread =
        Thread(null, r, "panda-thread-${counter.incrementAndGet()}")
})
```

### 4.2 工作队列和拒绝策略

**工作队列是线程池中非常重要的一部分**，如果它是一个有界队列，那么有时生产者有一个消费者没有能力消费的任务，在这种情况下，我们需要定义一个拒绝策略。如果它是一个无界队列，那么它可能会溢出，我们必须密切注意它。

让我们看看当我们的队列比我们的可用线程列表长时会发生什么：

```kotlin
class Task(val started: Long) : Callable<Long> {
    override fun call() = (System.currentTimeMillis() - started).also { Thread.sleep(100) }
}

val workerQueue = LinkedBlockingQueue<Runnable>(/*unbounded*/)
val worker = ThreadPoolExecutor(/*2 threads*/)
buildList {
    repeat(6) { add(Task(System.currentTimeMillis())) }
}.let(worker::invokeAll)
    .map(Future<Long>::get)
    .also { println(it) }
```

输出将产生一种阶梯，其值分为三个不同的组，每个组都比前一个大大约 100，如下所示：[4, 4, 110, 110, 211, 215]，这意味着线程池将任务保留在队列中，直到有空闲线程来执行它们。

因此，如果我们继续我们的示例并提供比线程池可以处理的更多任务，我们可以从以下选项中进行选择：

-   DiscardPolicy：忽略任务
-   DiscardOldestPolicy：忽略之前提交的最早的任务；改为执行这个
-   AbortPolicy：抛出异常
-   CallerRunsPolicy：调用者线程将改为运行任务
-   我们自己的自定义策略：任何RejectedExecutionHandler的实现都可以

**如果工作队列有边界或者其他原因不能接受新的任务，选择的拒绝策略就会生效**。

## 5. 终止线程池

拒绝策略可能影响我们的另一种情况是我们关闭线程池，**有三种方法可以终止线程池**，第一个只是shutdown()：

```kotlin
val worker = Executors.newFixedThreadPool(2)
buildList {
    repeat(5) { add(Task(System.currentTimeMillis())) }
}.map(worker::submit)
worker.shutdown()
worker.submit { println("any task, really") }
```

最后一行将调用拒绝策略，在本例中为AbortPolicy。

第二种关闭线程池的方法是等待几个时间单位的awaitTermination()：

```kotlin
worker.awaitTermination(10, TimeUnit.MILLISECONDS)
```

在这两种情况下，已经提交的任务都会继续运行，并且不会接受新任务，一旦完成所有正在运行的任务，线程就会死亡。

第三种方法是shutdownNow()，这不仅会阻止提交新任务，还会立即中断所有工作线程并返回仍在队列中的任务列表：

```kotlin
val unrun: List<Runnable> = worker.shutdownNow()
```

虽然所有工作线程都收到一个中断信号，但它们的行为是不确定的，因为Java中的线程取消是协作的。

**两种关闭方法都将立即返回，而awaitTermination()将阻塞指定的超时时间**。

## 6. 总结

在本教程中，我们回顾了为什么需要创建线程池以及如何创建线程池，Executors API涵盖了最常见的情况，但如果手头的任务需要，我们也可以自定义线程池设置。ThreadPoolExecutor为简单的一次性任务提供引擎，而ForkJoinPool允许我们自适应地并行化复杂的工作流，调度线程池可以运行延迟或重复的任务。