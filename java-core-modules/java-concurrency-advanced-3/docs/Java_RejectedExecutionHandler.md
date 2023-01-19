## 1. 概述

Java中的Executor框架将任务的提交与任务的执行分离，虽然这种方法很好地抽象了任务执行的细节，但有时我们仍然需要对其进行配置，以实现更优化的执行。

在本文中，我们将了解线程池无法接收更多任务时会发生什么情况。然后，我们学习如何通过适当地应用饱和策略来控制这种极端情况。

## 2. 线程池回顾

下图显示了ExecutorService在内部的工作方式：

<img src="../assets/img.png">

当我们向ExecutorService提交新任务时，发生的情况如下：

1. 如果其中一个线程可用，它将处理该任务。
2. 否则，executor会将新任务添加到其队列中。
3. 当一个线程完成当前任务时，它会从队列中选取另一个任务。

### 2.1 ThreadPoolExecutor

大多数executor实现都使用ThreadPoolExecutor作为其基本实现。因此，为了更好地理解任务队列的工作原理，我们应该仔细看看它的构造函数：

```java
/@formatter:off/
public ThreadPoolExecutor(
    int corePoolSize,
    int maximumPoolSize,
    long keepAliveTime,
    TimeUnit unit,
    BlockingQueue<Runnable> workQueue,
    RejectedExecutionHandler handler
)
/@formatter:on/
```

### 2.2 corePoolSize

corePoolSize参数确定线程池的初始大小。通常，executor确保线程池至少包含corePoolSize数量的线程。但是，如果启用allowCoreThreadTimeOut参数，则线程可能会更少。

### 2.3 maximumPoolSize

假设所有核心线程都在忙于执行一些任务，因此，executor会对新任务进行排队，直到稍后它们有机会处理。当此队列已满时，executor可以向线程池添加更多线程。maximumPoolSize为线程池可能包含的线程数设定了上限。

当这些线程保持一段时间空闲时，executor可以将其从线程池中删除。因此，线程池大小可以收缩回其核心线程数大小。

### 2.4 Queueing

如前所述，当所有核心线程都很忙时，executor会将新任务添加到队列中。排队有三种不同的方法：

+ 无界队列：此队列可以容纳无限数量的任务。由于此队列从来不会填满，因此executor将忽略maximumPoolSize参数。固定大小和单线程executor都使用这种方法。
+ 有界队列：顾名思义，队列只能容纳有限数量的任务。因此，当有界队列填满时，线程池将增长。
+ 同步切换：令人惊讶的是，这个队列不能容纳任何任务！使用这种方法，当且仅当另一个线程同时在另一端选择相同的任务时，我们才能对任务进行排队。缓存executor在内部使用这种方法。

当我们使用有界排队或同步切换时，设想以下场景：

+ 所有核心线程都忙
+ 内部队列已满
+ 线程池将增长到其最大可能大小，所有这些线程也都很忙

当新任务出现时会发生什么？

## 3. 饱和策略

当所有线程都很忙，并且内部队列已满时，executor就会饱和。

一旦达到饱和状态，executor可以执行预定义的操作，这些操作称为饱和策略。我们可以通过将RejectedExecutionHandler的实例传递给其构造函数来修改executor的饱和策略。

幸运的是，Java为这个类提供了几个内置实现，每个实现都涵盖了一个特定的用例。

### 3.1 Caller-runs策略

此策略让调用线程执行任务，而不是在另一个线程中异步运行任务：

```java
class SaturationPolicyUnitTest {
    private ThreadPoolExecutor executor;

    @AfterEach
    void shutdownExecutor() {
        if (executor != null && !executor.isTerminated()) {
            executor.shutdownNow();
        }
    }

    @Disabled
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
}
```

在上面的代码中，当我们向executor提交第一个任务后，它不能再接收任何新任务。因此，该新任务会由调用线程执行，调用线程(main线程)将阻塞，直到第二个任务返回。

caller-runs策略可以实现一种简单的节流形式，也就是说，较慢的消费者可以降低较快生产者的速度来控制任务提交流。

### 3.2 Abort策略

默认策略是中止策略，中止策略导致executor抛出RejectedExecutionException：

```java
class SaturationPolicyUnitTest {

    @Disabled
    @Test
    void givenAbortPolicy_whenSaturated_thenShouldThrowRejectedExecutionException() {
        executor = new ThreadPoolExecutor(1, 1, 0, MILLISECONDS, new SynchronousQueue<>(), new AbortPolicy());
        executor.execute(() -> waitFor(250));

        assertThatThrownBy(() -> executor.execute(() -> System.out.println("Will be rejected"))).isInstanceOf(RejectedExecutionException.class);
    }
}
```

由于第一个任务需要长时间才能执行，executor会拒绝第二个任务。

### 3.3 Discard策略

当新任务提交失败时，丢弃策略会自动丢弃该任务：

```java
class SaturationPolicyUnitTest {

    @Test
    void givenDiscardPolicy_whenSaturated_thenExecutorDiscardsTheNewTask() throws InterruptedException {
        executor = new ThreadPoolExecutor(1, 1, 0, MILLISECONDS, new SynchronousQueue<>(), new DiscardPolicy());
        executor.execute(() -> waitFor(100));

        BlockingQueue<String> queue = new LinkedBlockingDeque<>();
        executor.execute(() -> queue.offer("Result"));

        assertThat(queue.poll(200, MILLISECONDS)).isNull();
    }
}
```

在这里，第二个任务将“Result”添加到队列中，但由于它从来没有机会执行，所以即使我们在它上面阻塞了一段时间，队列仍然是空的。

### 3.4 Discard-Oldest策略

根据队列的特性，丢弃最旧的策略首先从队列头删除任务(即最旧的任务)，然后重新提交新任务：

```java
class SaturationPolicyUnitTest {

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
}
```

这一次，我们使用的是只能容纳两个任务的有界队列，以下是我们提交这四个任务时发生的情况：

+ 第一个任务占用单个线程100毫秒
+ executor成功地将第二个和第三个任务排队
+ 当第四个任务到达时，丢弃最旧的策略将删除最旧的任务(queue.offer("First"))为新任务腾出空间

丢弃最旧的策略和优先级队列不能很好地配合使用，因为优先级队列的头部是具有最高优先级的任务，如果我们使用该策略，可能会丢弃最重要的任务。

### 3.5 自定义策略

我们也可以通过实现RejectedExecutionHandler接口来提供自定义饱和策略：

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

在本例中，当executor饱和时，我们将线程池的maximumPoolSize增加1，然后重新提交相同的任务：

```java
class SaturationPolicyUnitTest {

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
}
```

因此，上面测试中的所有四个任务都会执行。

### 3.6 关闭

除了负载的executor之外，饱和策略也适用于所有已关闭的executor：

```java
class SaturationPolicyUnitTest {

    @Test
    void givenExecutorIsTerminated_whenSubmittedNewTask_thenTheSaturationPolicyApplies() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, MILLISECONDS, new LinkedBlockingQueue<>());
        executor.shutdownNow();

        assertThatThrownBy(() -> executor.execute(() -> {}))
                .isInstanceOf(RejectedExecutionException.class);
    }
}
```

对于处于关闭过程中的所有executor也是如此：

```java
class SaturationPolicyUnitTest {

    @Test
    void givenExecutorIsTerminating_whenSubmittedNewTask_thenTheSaturationPolicyApplies() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, MILLISECONDS, new LinkedBlockingQueue<>());
        executor.execute(() -> waitFor(100));
        executor.shutdown();

        assertThatThrownBy(() -> executor.execute(() -> {}))
                .isInstanceOf(RejectedExecutionException.class);
    }
}
```

## 4. 总结

在本教程中，首先，我们对Java中的线程池进行了简要回顾。然后，我们了解了如何以及何时应用不同的饱和策略。