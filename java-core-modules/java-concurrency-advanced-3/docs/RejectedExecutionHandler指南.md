## 1. 概述

Java中的[Executor框架](https://www.baeldung.com/java-executor-service-tutorial)将任务的提交与任务的执行分离。虽然这种方法很好地抽象了任务执行的细节，但有时我们仍然需要对其进行配置以实现更优化的执行。

在本文中，我们将了解当线程池无法接收更多任务时会发生什么。然后，我们将学习如何通过适当地应用饱和策略来控制这种极端情况。

## 2. 重温线程池

下图显示了[ExecutorService](https://www.baeldung.com/thread-pool-java-and-guava)在内部的工作方式：

<img src="../assets/img.png">

以下是我们**向Executor提交新任务时发生的情况**：

1. 如果其中一个线程可用，它将处理该任务
2. 否则，Executor会将新任务添加到其队列中
3. 当一个线程完成当前任务时，它会从队列中选取另一个任务

### 2.1 ThreadPoolExecutor

大多数Executor实现实现使用众所周知的[ThreadPoolExecutor](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.html)作为它们的基础实现。因此，为了更好地理解任务队列的工作原理，我们应该仔细看看它的构造函数：

```java
public ThreadPoolExecutor(
    int corePoolSize,
    int maximumPoolSize,
    long keepAliveTime,
    TimeUnit unit,
    BlockingQueue<Runnable> workQueue,
    RejectedExecutionHandler handler
)
```

### 2.2 核心池大小

corePoolSize参数确定线程池的初始大小。**通常，Executor确保线程池至少包含corePoolSize个线程**。

但是，如果我们启用[allowCoreThreadTimeOut](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.html#allowCoreThreadTimeOut(boolean))参数，则线程可能会更少。

### 2.3 最大池大小

假设所有核心线程都忙于执行一些任务。因此，Executor将新任务排入队列，直到它们有机会在以后处理。

当此队列已满时，Executor可以向线程池添加更多线程。**maximumPoolSize为线程池可能包含的线程数设置了上限**。

当这些线程保持一段时间空闲时，Executor可以将它们从池中移除。因此，线程池大小可以收缩回其核心线程数大小。

### 2.4 队列

如前所述，当所有核心线程都很忙时，Executor会将新任务添加到队列中。**排队有三种不同的方法**：

+ 无界队列：队列可以容纳无限数量的任务。由于此队列永远不会填满，因此Executor会忽略maximumPoolSize参数。[固定大小](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Executors.html#newFixedThreadPool(int))和[单线程Executor](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Executors.html#newSingleThreadExecutor())都使用这种方法。
+ 有界队列：顾名思义，队列只能容纳有限数量的任务。因此，当有界队列填满时，线程池将增长。
+ 同步切换：令人惊讶的是，这个队列不能容纳任何任务！使用这种方法，**当且仅当有另一个线程同时在另一端选择相同的任务时，我们才能将任务排队**。[缓存Executor](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Executors.html#newCachedThreadPool())在内部使用这种方法。

当我们使用有界排队或同步切换时，让我们假设以下场景：

+ 所有核心线程都忙
+ 内部队列已满
+ 线程池增长到其最大可能大小，所有这些线程也都很忙

**当一个新任务进来时会发生什么？**

## 3. 饱和策略

当所有线程都很忙并且内部队列已满时，Executor就会饱和。

一旦达到饱和状态，Executor就可以执行预定义的操作，这些操作称为饱和策略。**我们可以通过将RejectedExecutionHandler的实例传递给其构造函数来修改Executor的饱和策略**。

幸运的是，Java为此类提供了一些内置实现，每个实现都涵盖了一个特定的用例。在以下部分中，我们将详细评估这些政策。

### 3.1 中止(Abort)策略

默认策略是[中止策略](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.AbortPolicy.html)。**中止策略导致Executor抛出RejectedExecutionException**：

```java
@Test
void givenAbortPolicy_whenSaturated_thenShouldThrowRejectedExecutionException() {
    executor = new ThreadPoolExecutor(1, 1, 0, MILLISECONDS, new SynchronousQueue<>(), new AbortPolicy());
    executor.execute(() -> waitFor(250));

    assertThatThrownBy(() -> executor.execute(() -> System.out.println("Will be rejected"))).isInstanceOf(RejectedExecutionException.class);
}
```

由于第一个任务需要很长时间才能执行，因此Executor拒绝了第二个任务。

### 3.2 调用者运行(Caller-Runs)策略

此策略不是在另一个线程中异步运行任务，而是**使调用者线程执行任务**：

```java
private ThreadPoolExecutor executor;

@AfterEach
void shutdownExecutor() {
    if (executor != null && !executor.isTerminated()) {
        executor.shutdownNow();
    }
}

@Test
void givenCallerRunsPolicy_whenSaturated_thenTheCallerThreadRunsTheTask() {
    executor = new ThreadPoolExecutor(1, 1, 0, MILLISECONDS, new SynchronousQueue<>(), new CallerRunsPolicy());
    executor.execute(() -> waitFor(250));

    long startTime = System.currentTimeMillis();
    executor.execute(() -> waitFor(500));
    long blockedDuration = System.currentTimeMillis() - startTime;

    assertThat(blockedDuration).isGreaterThanOrEqualTo(500);
}

private void waitFor(int millis) {
    try {
        Thread.sleep(millis);
    } catch (InterruptedException ignored) {
    }
}
```

提交第一个任务后，Executor不能再接受新的任务。因此，该新任务会由调用线程执行，调用者线程(main线程)将阻塞，直到第二个任务返回。

**[调用者运行策略](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.CallerRunsPolicy.html)使得实现简单形式的限制变得容易**。也就是说，一个慢速的消费者可以减慢一个快速的生产者来控制任务提交流程。

### 3.3 丢弃(Discard)策略

**[丢弃策略](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.DiscardPolicy.html)在提交失败时静默丢弃新任务**：

```java
@Test
void givenDiscardPolicy_whenSaturated_thenExecutorDiscardsTheNewTask() throws InterruptedException {
    executor = new ThreadPoolExecutor(1, 1, 0, MILLISECONDS, new SynchronousQueue<>(), new DiscardPolicy());
    executor.execute(() -> waitFor(100));

    BlockingQueue<String> queue = new LinkedBlockingDeque<>();
    executor.execute(() -> queue.offer("Result"));

    assertThat(queue.poll(200, MILLISECONDS)).isNull();
}
```

在这里，第二个任务向队列发布了一条简单的消息。因为它从来没有机会执行，队列仍然是空的，即使我们在它上面阻塞了一段时间。

### 3.4 丢弃最旧(Discard-Oldest)策略

**[丢弃最旧策略](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.DiscardOldestPolicy.html)首先从队列头部删除一个任务，然后重新提交新任务**：

```java
@Test
void givenDiscardOldestPolicy_whenSaturated_thenExecutorDiscardsTheOldestTask() {
    executor = new ThreadPoolExecutor(1, 1, 0, MILLISECONDS, new ArrayBlockingQueue<>(2), new DiscardOldestPolicy());
    executor.execute(() -> waitFor(100));

    BlockingQueue<String> queue = new LinkedBlockingDeque<>();
    executor.execute(() -> queue.offer("First"));
    executor.execute(() -> queue.offer("Second"));
    executor.execute(() -> queue.offer("Third"));
    waitFor(150);
    
    List<String> results = new ArrayList<>();
    queue.drainTo(results);
    
    assertThat(results).containsExactlyInAnyOrder("Second", "Third");
}
```

这一次，我们使用的是只能容纳两个任务的有界队列。以下是我们提交这四个任务时发生的情况：

+ 第一个任务占用单个线程100毫秒
+ Executor将第二个和第三个任务排队成功
+ 当第四个任务到达时，丢弃最旧的策略会删除最旧的任务(queue.offer("First"))以便为新任务腾出空间

**丢弃最旧的策略和优先级队列不能很好地协同工作**。因为优先级队列的头部是具有最高优先级的任务，如果我们使用该策略，**可能会丢弃最重要的任务**。

### 3.5 自定义策略

也可以通过实现[RejectedExecutionHandler](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/RejectedExecutionHandler.html)接口来提供自定义饱和策略：

```java
private static class GrowPolicy implements RejectedExecutionHandler {
    private final Lock lock = new ReentrantLock();

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        lock.lock();
        try {
            executor.setMaximumPoolSize(executor.getMaximumPoolSize() + 1);
        } finally {
            lock.unlock();
        }

        executor.submit(r);
    }
}
```

在此示例中，当Executor饱和时，我们将线程池的maximumPoolSize增加1，然后重新提交相同的任务：

```java
@Test
void givenGrowPolicy_whenSaturated_thenExecutorIncreaseTheMaxPoolSize() {
    executor = new ThreadPoolExecutor(1, 1, 0, MILLISECONDS, new ArrayBlockingQueue<>(2), new GrowPolicy());
    executor.execute(() -> waitFor(100));

    BlockingQueue<String> queue = new LinkedBlockingDeque<>();
    executor.execute(() -> queue.offer("First"));
    executor.execute(() -> queue.offer("Second"));
    executor.execute(() -> queue.offer("Third"));
    waitFor(150);
    
    List<String> results = new ArrayList<>();
    queue.drainTo(results);
    
    assertThat(results).containsExactlyInAnyOrder("First", "Second", "Third");
}
```

正如预期的那样，所有四个任务都将执行。

### 3.6 关闭

除了负载的Executor之外，**饱和策略也适用于所有已关闭的Executor**：

```java
@Test
void givenExecutorIsTerminated_whenSubmittedNewTask_thenTheSaturationPolicyApplies() {
    ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, MILLISECONDS, new LinkedBlockingQueue<>());
    executor.shutdownNow();

    assertThatThrownBy(() -> executor.execute(() -> {}))
        .isInstanceOf(RejectedExecutionException.class);
}
```

**对于处于关闭过程中的所有Executor也是如此**：

```java
@Test
void givenExecutorIsTerminating_whenSubmittedNewTask_thenTheSaturationPolicyApplies() {
    ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, MILLISECONDS, new LinkedBlockingQueue<>());
    executor.execute(() -> waitFor(100));
    executor.shutdown();

    assertThatThrownBy(() -> executor.execute(() -> {}))
        .isInstanceOf(RejectedExecutionException.class);
}
```

## 4. 总结

在本教程中，首先，我们对Java中的线程池进行了相当快速的复习。然后，在介绍了饱和Executor之后，我们了解了如何以及何时应用不同的饱和策略。