## 1. 概述

在本文中，我们将介绍CountDownLatch类，并演示如何在几个实际示例中使用它。

本质上，通过使用CountDownLatch，我们可以使一个线程阻塞，直到其他线程完成给定的任务。

## 2. 在并发编程中的用法

简单地说，CountDownLatch有一个计数器字段，你可以根据需要递减。然后我们可以使用它来阻塞调用线程，直到计数器到零为止。

如果我们做一些并行处理，我们可以使用与我们要跨其工作的线程数相同的计数器值来实例化CountDownLatch，
然后，我们可以在每个线程完成后调用countdown()，确保调用await()的依赖线程将阻塞，直到工作线程完成。

## 3. 等待线程池完成

让我们通过创建一个Worker并使用CountDownLatch字段在其完成时发出信号来说明这种模式：

```java
public class Worker implements Runnable {
    private final List<String> outputScraper;
    private final CountDownLatch countDownLatch;

    public Worker(List<String> outputScraper, CountDownLatch countDownLatch) {
        this.outputScraper = outputScraper;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        // Do some work
        System.out.println("Doing some logic");
        outputScraper.add("Counted down");
        countDownLatch.countDown();
    }
}
```

然后，我们创建一个测试，以证明我们可以通过CountDownLatch来等待Worker实例完成。

```java
class CountdownLatchExampleIntegrationTest {

    @Test
    void whenParallelProcessing_thenMainThreadWillBlockUntilCompletion() throws InterruptedException {
        List<String> outputScraper = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch countDownLatch = new CountDownLatch(5);
        List<Thread> workers = Stream.generate(() -> new Thread(new Worker(outputScraper, countDownLatch)))
                .limit(5)
                .toList();

        workers.forEach(Thread::start);
        countDownLatch.await(); // Block until workers finish
        outputScraper.add("Latch released");

        assertThat(outputScraper).containsExactly("Counted down", "Counted down", "Counted down",
                "Counted down", "Counted down", "Latch released");
    }
}
```

当然，“Latch released”将始终是最后一个输出，因为它取决于CountDownLatch的释放。

请注意，如果我们没有调用await()，我们无法保证线程执行的顺序，因此测试将随机失败。

## 4. 等待线程池的开始

如果我们以上一个例子为例，但这次启动了数千个线程，而不是五个线程，那么很可能在我们对后面的线程调用start()之前，
前面的许多线程已经完成了处理。这可能会使尝试和重现并发问题变得困难，因为我们无法让所有线程并行运行。

为了解决这个问题，我们让CountDownLatch与前一个示例中的工作方式不同。
我们可以阻塞每个子线程，直到其他所有子线程都已启动，而不是在某些子线程完成之前阻塞父线程。

让我们修改run()方法，使其在处理之前阻塞：

```java
public class WaitingWorker implements Runnable {
    private final List<String> outputScraper;
    private final CountDownLatch readyThreadCounter;
    private final CountDownLatch callingThreadBlocker;
    private final CountDownLatch completedThreadCounter;

    public WaitingWorker(List<String> outputScraper, CountDownLatch readyThreadCounter, CountDownLatch callingThreadBlocker, CountDownLatch completedThreadCounter) {
        this.outputScraper = outputScraper;
        this.readyThreadCounter = readyThreadCounter;
        this.callingThreadBlocker = callingThreadBlocker;
        this.completedThreadCounter = completedThreadCounter;
    }

    @Override
    public void run() {
        // Mark this thread as read / started
        readyThreadCounter.countDown();
        try {
            callingThreadBlocker.await();
            outputScraper.add("Counted down");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        completedThreadCounter.countDown();
    }
}
```

现在，让我们修改我们的测试，使其阻塞，直到所有Workers都已开始，解除阻塞Workers，然后继续阻塞，直到Workers完成：

```java
class CountdownLatchExampleIntegrationTest {

    @Test
    void whenDoingLotsOfThreadsInParallel_thenStartThemAtTheSameTime() throws InterruptedException {
        List<String> outputScraper = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch readyThreadCounter = new CountDownLatch(5);
        CountDownLatch callingThreadBlocker = new CountDownLatch(1);
        CountDownLatch completedThreadCounter = new CountDownLatch(5);
        List<Thread> workers = Stream.generate(() -> new Thread(new WaitingWorker(outputScraper, readyThreadCounter, callingThreadBlocker, completedThreadCounter)))
                .limit(5)
                .toList();

        workers.forEach(Thread::start);
        readyThreadCounter.await(); // Block until workers start
        outputScraper.add("Workers ready");
        callingThreadBlocker.countDown();
        completedThreadCounter.await(); // Block until workers finish
        outputScraper.add("Workers complete");

        assertThat(outputScraper).containsExactly("Workers ready", "Counted down", "Counted down", "Counted down",
                "Counted down", "Counted down", "Workers complete");
    }
}
```

这种模式对于尝试重现并发错误非常有用，因为它可以用来迫使数千个线程尝试并行执行某些逻辑。

## 5. 提前终止CountDownLatch

有时，我们可能会遇到这样一种情况，即Worker在CountDownLatch倒计时之前错误终止。这可能会导致它永远不会达到零，并且await()
永远不会终止：

```java
public class BrokenWorker implements Runnable {
    private final List<String> outputScraper;
    private final CountDownLatch countDownLatch;

    BrokenWorker(final List<String> outputScraper, final CountDownLatch countDownLatch) {
        this.outputScraper = outputScraper;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        if (true) {
            throw new RuntimeException("Oh dear");
        }
        countDownLatch.countDown();
        outputScraper.add("Counted down");
    }
}
```

让我们修改之前的测试，使用BrokenWorker，以显示await()将如何永久阻塞：

```java
class CountdownLatchExampleIntegrationTest {

    @Test
    void whenFailingToParallelProcess_thenMainThreadShouldTimeout() throws InterruptedException {
        List<String> outputScraper = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch countDownLatch = new CountDownLatch(5);
        List<Thread> workers = Stream.generate(() -> new Thread(new BrokenWorker(outputScraper, countDownLatch)))
                .limit(5)
                .toList();

        workers.forEach(Thread::start);
        final boolean result = countDownLatch.await(3L, TimeUnit.SECONDS);

        assertThat(result).isFalse();
    }
}
```

显然，这不是我们想要的行为，应用程序继续运行比无限阻塞要好得多。

为了解决这个问题，我们在对await()的调用中添加一个timeout参数。

```text
boolean completed = countDownLatch.await(3L, TimeUnit.SECONDS);
assertThat(completed).isFalse();
```

正如我们所见，测试最终将超时，而await()将返回false。

## 6. 总结

在本文中，我们演示了如何使用CountDownLatch来阻塞一个线程，直到其他线程完成一些处理。

我们还展示了如何通过确保线程并行运行来帮助调试并发性问题。