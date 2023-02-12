## 1. 概述

在本教程中，我们将比较CyclicBarrier和CountDownLatch并尝试了解两者之间的相同点和不同点。

## 2. 它们是什么

当涉及到并发时，概念化每个项目要实现的目标可能具有挑战性。

首先，**CountDownLatch和CyclicBarrier都用于管理多线程应用程序**。

而且，**它们都旨在表示给定线程或线程组应该如何等待**。

### 2.1 CountDownLatch

CountDownLatch是一个线程等待的构造，其他线程在该锁存器上倒计时，直到它达到零。

我们可以把它想象成餐厅里正在准备的一道菜。无论哪个厨师准备了n道菜中的多少道菜，服务员都必须等到所有菜都放在盘子里。如果一个盘子里放了n道菜，任何厨师都会对他放在盘子里的每一道菜倒计时。

### 2.2 CyclicBarrier

CyclicBarrier是一种可重用的构造，其中一组线程一起等待，直到所有线程都到达。此时，屏障被打破，可以选择采取行动。

我们可以把它想象成一群朋友。每次他们计划在一家餐馆吃饭时，他们都会决定一个可以见面的共同点。他们在那里互相等待，只有大家都到了，他们才能一起去餐厅吃饭。

### 2.3 进一步阅读

有关每个单独介绍的更多详细信息，请分别参考我们之前关于[CountDownLatch](https://www.baeldung.com/java-countdown-latch)和[CyclicBarrier](https://www.baeldung.com/java-cyclic-barrier)的教程。

## 3. 任务与线程

让我们更深入地研究这两个类之间的一些语义差异。

如定义中所述，CyclicBarrier允许多个线程相互等待，而CountDownLatch允许一个或多个线程等待多个任务完成。

简而言之，**CyclicBarrier维护线程计数**，而**CountDownLatch维护任务计数**。

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

请注意，在这种情况下，**我们能够让同一个线程将计数减少两次**。

**然而，CyclicBarrier在这一点上有所不同**。

与上面的示例类似，我们创建了一个CyclicBarrier，计数也初始化为2，并在其上调用await()，这次来自同一个线程：

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
```

```java
@Test
void whenCyclicBarrier_notCompleted() {
    CyclicBarrierCountExample ex = new CyclicBarrierCountExample(2);
    boolean isCompleted = ex.callTwiceInSameThread();
    assertFalse(isCompleted);
}
```

这里的第一个区别是等待的线程本身就是屏障。

其次，也是更重要的一点，**第二个await()调用是无用的。单个线程不能触发两次到达屏障**。

事实上，因为t必须阻塞等待另一个线程调用await()-以便计数达到2，因此t对await()的第二次调用实际上不会被调用，直到屏障已经被打破。

在我们的测试中，**屏障没有被打破，因为我们只有一个线程在等待，而不是触发屏障所需的两个线程**。从返回false的cyclicBarrier.isBroken()方法中也可以看出这一点。

## 4. 可重用性

这两个类之间第二个最明显的区别是可重用性。更详细地说，**当CyclicBarrier中的屏障被打破时，计数将重置为其原始值。而CountDownLatch不同，因为计数永远不会重置**。

在下面的代码中，我们定义了一个计数为7的CountDownLatch，并通过20次不同的调用对其进行计数：

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
```

```java
@Test
void whenCountDownLatch_noReset() {
    CountdownLatchResetExample ex = new CountdownLatchResetExample(7, 20);
    int lineCount = ex.countWaits();
    
    assertTrue(lineCount <= 7);
}
```

我们观察到即使有20个不同的线程调用countDown()，计数也不会在达到零时重置。

与上面的例子类似，我们定义了一个count为7的CyclicBarrier，并从20个不同的线程等待它：

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
```

```java
@Test
void whenCyclicBarrier_reset() {
    CyclicBarrierResetExample ex = new CyclicBarrierResetExample(7, 20);
    int lineCount = ex.countWaits();
    
    assertTrue(lineCount > 7);
}
```

在这种情况下，我们观察到每次新线程运行时该值都会减少，一旦达到零，就会重置为原始值。

## 5. 总结

总而言之，CyclicBarrier和CountDownLatch都是用于多线程之间同步的有用工具。但是，它们在提供的功能方面有根本的不同。在确定哪个适合使用场景时，请仔细考虑每一个。