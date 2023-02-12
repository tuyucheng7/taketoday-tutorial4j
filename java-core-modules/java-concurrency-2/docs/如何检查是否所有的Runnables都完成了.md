## 1. 概述

在本文中，我们将获取一个[Runnable](https://www.baeldung.com/java-runnable-callable)对象列表并检查它们是否都已完成。正如我们所知，Runnable是一个接口，其实例可以作为[Thread](https://www.baeldung.com/java-thread-lifecycle)运行。我们将使用诸如CompletableFuture和ThreadPoolExecutor之类的包装对象来运行这些线程。

## 2. 示例设置

让我们创建一个基本的Runnable，它只会记录一条消息，然后暂停1秒：

```java
static Runnable RUNNABLE = () -> {
    try {
        System.out.println("launching runnable");
        Thread.sleep(1000);
    } catch (InterruptedException e) {
    }
};
```

现在，我们将创建一个Runnable列表。在此示例中，我们将重复添加相同的Runnable。实现此目的的一种方法是使用[IntStream](https://www.baeldung.com/java-intstream-convert)：

```java
List<Runnable> runnables = IntStream.range(0, 5)
    .mapToObj(x -> RUNNABLE)
    .collect(Collectors.toList());
```

现在让我们看看如何运行这些Runnable对象并了解它们是否全部完成。

## 3. 使用CompletableFuture

**从Java 8开始，我们可以使用内置的[CompletableFuture](https://www.baeldung.com/java-completablefuture)的isDone()方法来达到这个目的**。

CompletableFuture对象使Java中的异步编程更加容易。鉴于我们的Runnable列表，我们将使用CompletableFuture的runAsync()方法异步运行相关任务。请注意，默认情况下，所有这些任务都将在[ForkJoinPool](https://www.baeldung.com/java-fork-join)上运行。

为了进一步的目的，我们希望将所有结果的CompletableFuture包装在一个[数组](https://www.baeldung.com/java-arrays-guide)中：

```java
CompletableFuture<?>[] completableFutures = runnables.stream()
    .map(CompletableFuture::runAsync)
    .toArray(CompletableFuture<?>[]::new);
```

现在，我们所有的Runnable任务都被包装到CompletableFuture执行中。这意味着这些任务将在我们的程序继续运行时在后台异步运行。

为了查明我们程序中的任何一点是否所有执行都已完成，我们将从我们的数组中创建一个新的包装CompletableFuture。allOf()方法将允许我们这样做。然后，我们将isDone()方法直接应用于包装的CompletableFuture：

```java
boolean isEveryRunnableDone = CompletableFuture.allOf(completableFutures)
    .isDone();
```

如果CompletableFuture中的任何一个仍在运行，则isEveryRunnableDone将为false，否则将为true。

## 4. 使用ThreadPoolExecutor

从Java 5开始，[线程池](https://www.baeldung.com/thread-pool-java-and-guava)提供了额外的工具来帮助管理并发环境中的资源。特别是，他们维护一些统计数据，例如他们持有的已完成任务的数量。

### 4.1 统计剩余任务数

让我们创建一个具有5个线程的ThreadPoolExecutor。然后，我们将使用execute()方法提交每个Runnable以供执行：

```java
ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
runnables.forEach(executor::execute);
```

**现在我们可以使用getActiveCount()方法计算ThreadPoolExecutor中正在运行的任务数**：

```java
int numberOfActiveThreads = executor.getActiveCount();
```

这里的问题是，我们是否可以将这个数字与0进行比较，以检查是否有任何Runnable仍在运行？事情实际上比这要复杂一点。问题在于**getActiveCount()方法返回的数字是一个近似值，如类的文档所述。因此，我们不能依赖它来做出任何决定**。

### 4.2 检查是否所有任务都已终止

getActiveCount()方法不会返回准确的值，因为这样做可能需要大量计算。因此，让我们立即放弃实现我们自己的计数器的选项。

**另一方面，awaitTermination()方法可以让我们知道是否所有任务都已完成**。但首先，我们需要调用执行器的shutdown()方法。调用此方法将确保所有提交的任务都将完成。但是，它会阻止将新任务添加到执行器：

```java
executor.shutdown();
```

我们已确保我们的ThreadPoolExecutor将正确关闭。我们现在可以通过调用[awaitTermination()](https://www.baeldung.com/java-executor-wait-for-threads#after-executors-shutdown)随时检查池中是否有任何正在运行的任务。此方法将阻塞，直到给定超时或直到所有任务完成。例如，为了我们的示例，让我们使用1秒超时：

```java
boolean isEveryRunnableDome = executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
```

如果所有任务在1秒内完成，该方法立即返回true。否则，程序将被阻塞一秒钟，然后返回false。

最后但同样重要的是，我们应该注意，**如果任何底层线程被中断，awaitTermination()将抛出[InterruptedException](https://www.baeldung.com/java-interrupted-exception)**。

## 5. 总结

在本教程中，我们了解了如何检查所有Runnable是否已完成。对于高于8的Java版本，由于CompletableFuture类，这非常简单。对于旧版本，我们需要明智地选择超时，因为程序可能会在我们设置的持续时间内被阻塞。