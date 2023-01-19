## 1. 概述

在本文中，我们将比较CyclicBarrier和CountDownLatch，并试图阐明两者之间的异同。

## 2. 它们是什么

当涉及到并发时，概念化每一个都要完成的任务可能是一个挑战。

首先，CountDownLatch和CyclicBarrier都用于管理多线程应用程序。

而且，它们都旨在表示给定线程或线程组应该如何等待。

### 2.1 CountDownLatch

CountDownLatch是一个线程等待的构造，其他线程在该锁存器上倒计时，直到它达到零。

我们可以把它想象成餐馆中一道正在准备的菜。无论哪位厨师准备了n道菜中的多少道菜，服务员都必须等到所有菜都放在盘子里。
如果一个盘子里有n道菜，任何厨师都会对他放在盘子里的每一道菜倒计时。

### 2.2 CyclicBarrier

CyclicBarrier是一种可重用的构造，其中一组线程一起等待，直到所有线程到达。此时，屏障被打破，可以选择采取行动。

我们可以把它想象成一群朋友。每次他们计划在餐馆吃饭时，他们都会决定一个共同地点，在那里见面。
他们在那里互相等待，只有当每个人都到了，他们才能一起去餐馆吃饭。

## 3. 任务与线程

让我们深入了解这两个类之间的一些语义差异。

正如定义中所述，CyclicBarrier允许多个线程相互等待，而CountDownLatch允许一个或多个线程等待多个任务完成。

简而言之，CyclicBarrier维护线程计数，而CountDownLatch维护任务计数。

在下面的代码中，我们定义了一个计数为2的CountDownLatch。接下来，我们从单个线程调用countDown()两次：

```java
public class CountdownLatchCountExample {
    private final int count;

    public CountdownLatchCountExample(int count) {
        this.count = count;
    }

    public boolean callTwiceInSameThread() {
        CountDownLatch countDownLatch = new CountDownLatch(count);
        Thread t = new Thread(() -> {
            countDownLatch.countDown();
            countDownLatch.countDown();
        });
        t.start();

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return countDownLatch.getCount() == 0;
    }

    public static void main(String[] args) {
        CountdownLatchCountExample ex = new CountdownLatchCountExample(2);
        System.out.println("Is CountDown Completed : " + ex.callTwiceInSameThread());
    }
}
```

一旦CountDownLatch达到零，await()的调用就会返回。

请注意，在这种情况下，我们可以让同一线程将计数减少两次。

不过，CyclicBarrier在这一点上有所不同。

与上面的示例类似，我们创建了一个CyclicBarrier，并将计数也初始化为2，并对其调用await()，这次来自同一个线程：

```java
public class CyclicBarrierCountExample {
    private final int count;

    public CyclicBarrierCountExample(int count) {
        this.count = count;
    }

    public boolean callTwiceInSameThread() {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(count);
        Thread t = new Thread(() -> {
            try {
                cyclicBarrier.await();
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
            }
        });
        t.start();
        return cyclicBarrier.isBroken();
    }

    public static void main(String[] args) {
        CyclicBarrierCountExample ex = new CyclicBarrierCountExample(2);
        System.out.println("Count : " + ex.callTwiceInSameThread());
    }
}

class CyclicBarrierCountExampleUnitTest {

    @Test
    void whenCyclicBarrier_notCompleted() {
        CyclicBarrierCountExample ex = new CyclicBarrierCountExample(2);
        boolean isCompleted = ex.callTwiceInSameThread();
        assertFalse(isCompleted);
    }
}
```

这里的第一个区别是等待的线程本身就是屏障。

其次，也是更重要的一点，第二个await()调用是无用的。一个线程不能触发两次到达屏障。

事实上，t必须阻塞等待另一个线程调用await()，以便计数达到2，
因此t对await()的第二次调用实际上不会被调用，直到屏障已经被打破。

在我们的测试中，屏障没有被打破，因为我们只有一个线程在等待，而不是触发屏障所需的两个线程。
这一点在cyclicBarrier.isBroken()方法上也很明显，它返回false。

## 4. 可重用性

这两个类之间第二个最明显的区别是可重用性。
更详细地说，当CyclicBarrier中屏障被打破时，计数将重置为其原始值。而CountDownLatch的count不会重置。

在下面的代码中，我们使用count为7定义CountDownLatch，并通过20个不同的调用对其进行计数：

```java
public class CountdownLatchResetExample {
    private final int count;
    private final int threadCount;
    private final AtomicInteger updateCount;

    CountdownLatchResetExample(int count, int threadCount) {
        updateCount = new AtomicInteger(0);
        this.count = count;
        this.threadCount = threadCount;
    }

    public int countWaits() {
        CountDownLatch countDownLatch = new CountDownLatch(count);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                long prevValue = countDownLatch.getCount();
                countDownLatch.countDown();
                if (countDownLatch.getCount() != prevValue) {
                    updateCount.incrementAndGet();
                }
            });
        }

        executor.shutdown();
        return updateCount.get();
    }

    public static void main(String[] args) {
        CountdownLatchResetExample ex = new CountdownLatchResetExample(5, 20);
        System.out.println("Count : " + ex.countWaits());
    }
}

class CountdownLatchResetExampleManualTest {

    @Test
    void whenCountDownLatch_noReset() {
        CountdownLatchResetExample ex = new CountdownLatchResetExample(7, 20);
        int lineCount = ex.countWaits();
        assertTrue(lineCount <= 7);
    }
}
```

我们观察到，即使有20个不同的线程调用countDown()，一旦count达到零，它也不会重置。

与上面的示例类似，我们定义了一个count为7的CyclicBarrier，并从20个不同的线程等待它：

```java
public class CyclicBarrierResetExample {
    private final int count;
    private final int threadCount;
    private final AtomicInteger updateCount;

    CyclicBarrierResetExample(int count, int threadCount) {
        updateCount = new AtomicInteger(0);
        this.count = count;
        this.threadCount = threadCount;
    }

    public int countWaits() {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(count);

        ExecutorService es = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            es.execute(() -> {
                try {
                    if (cyclicBarrier.getNumberWaiting() > 0) {
                        updateCount.incrementAndGet();
                    }
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        es.shutdown();
        try {
            es.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return updateCount.get();
    }

    public static void main(String[] args) {
        CyclicBarrierResetExample ex = new CyclicBarrierResetExample(7, 20);
        System.out.println("Count : " + ex.countWaits());
    }
}

class CyclicBarrierResetExampleUnitTest {

    @Test
    void whenCyclicBarrier_reset() {
        CyclicBarrierResetExample ex = new CyclicBarrierResetExample(7, 20);
        int lineCount = ex.countWaits();
        assertTrue(lineCount > 7);
    }
}
```

在这种情况下，我们观察到，每当新线程运行时，计数就会减少，只要它达到零，就会重置为原始值。

## 5. 总结

总而言之，CyclicBarrier和CountDownLatch都是用于多线程之间同步的有用工具。
但是，就它们提供的功能而言，它们有着根本的不同。在确定哪个适合使用场景时，请仔细考虑每一个。