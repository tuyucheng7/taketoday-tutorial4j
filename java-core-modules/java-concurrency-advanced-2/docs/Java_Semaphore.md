## 1. 概述

在这个教程中，我们将介绍Java中信号量和互斥量的基础知识。

## 2. Semaphore

我们可以使用信号量来限制访问特定资源的并发线程的数量。

在下面的示例中，我们将实现一个简单的登录队列来限制系统中的用户数：

```java
public class LoginQueueUsingSemaphore {
    private final Semaphore semaphore;

    LoginQueueUsingSemaphore(int slotLimit) {
        semaphore = new Semaphore(slotLimit);
    }

    boolean tryLogin() {
        return semaphore.tryAcquire();
    }

    void logout() {
        semaphore.release();
    }

    int availableSlots() {
        return semaphore.availablePermits();
    }
}
```

注意我们是如何使用以下方法的：

+ tryAcquire() - 如果有可用的许可证，则返回true，否则返回false。而acquire()获取许可证并阻塞，直到有一个可用。
+ release() - 释放许可证。
+ availablePermits() - 返回可用的许可证数量。

为了测试登录队列，我们首先演示达到最大用户数限制，并检查下一次登录尝试是否会被阻止：

```java
class SemaphoresManualTest {

    @Test
    void givenLoginQueue_whenReachLimit_thenBlocked() throws InterruptedException {
        final int slots = 10;
        final ExecutorService executor = Executors.newFixedThreadPool(slots);
        final LoginQueueUsingSemaphore loginQueue = new LoginQueueUsingSemaphore(slots);
        IntStream.range(0, slots)
                .forEach(user -> executor.execute(loginQueue::tryLogin));
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        assertEquals(0, loginQueue.availableSlots());
        assertFalse(loginQueue.tryLogin());
    }
}
```

接下来，在我们注销一个用户后，应该有可用的许可证：

```java
class SemaphoresManualTest {

    @Test
    void givenLoginQueue_whenLogout_thenSlotsAvailable() throws InterruptedException {
        final int slots = 10;
        final ExecutorService executor = Executors.newFixedThreadPool(slots);
        final LoginQueueUsingSemaphore loginQueue = new LoginQueueUsingSemaphore(slots);
        IntStream.range(0, slots)
                .forEach(user -> executor.execute(loginQueue::tryLogin));

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        assertEquals(0, loginQueue.availableSlots());
        loginQueue.logout();
        assertTrue(loginQueue.availableSlots() > 0);
        assertTrue(loginQueue.tryLogin());
    }
}
```

## 3. 定时信号量

接下来，我们将介绍apache-commons中的TimedSemaphore。TimedSemaphore允许多个许可证作为一个简单的信号量，但在给定的时间段内。
在此时间段后，时间重置，所有许可证都被释放。

我们可以使用TimedSemaphore构建一个简单的延迟队列，如下所示：

```java
public class DelayQueueUsingTimedSemaphore {
    private final TimedSemaphore semaphore;

    DelayQueueUsingTimedSemaphore(long period, int slotLimit) {
        semaphore = new TimedSemaphore(period, TimeUnit.SECONDS, slotLimit);
    }

    boolean tryAdd() {
        return semaphore.tryAcquire();
    }

    int availableSlots() {
        return semaphore.getAvailablePermits();
    }
}
```

当我们使用以1秒作为时间周期的延迟队列，并且在1秒内使用所有许可证后，应该没有可用的许可证：

```java
class SemaphoresManualTest {
    
    @Test
    void givenDelayQueue_whenReachLimit_thenBlocked() throws InterruptedException {
        final int slots = 50;
        final ExecutorService executor = Executors.newFixedThreadPool(slots);
        final DelayQueueUsingTimedSemaphore delayQueue = new DelayQueueUsingTimedSemaphore(1, slots);
        IntStream.range(0, slots)
                .forEach(user -> executor.submit(delayQueue::tryAdd));
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        assertEquals(0, delayQueue.availableSlots());
        assertFalse(delayQueue.tryAdd());
    }
}
```

但是当我们休眠1秒以上时，信号量应重置并释放许可证：

```java
class SemaphoresManualTest {

    @Test
    void givenDelayQueue_whenTimePass_thenSlotsAvailable() throws InterruptedException {
        final int slots = 50;
        final ExecutorService executor = Executors.newFixedThreadPool(slots);
        DelayQueueUsingTimedSemaphore delayQueue = new DelayQueueUsingTimedSemaphore(1, slots);
        IntStream.range(0, slots)
                .forEach(user -> executor.submit(delayQueue::tryAdd));

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        assertEquals(0, delayQueue.availableSlots());
        TimeUnit.MILLISECONDS.sleep(1000);

        assertTrue(delayQueue.availableSlots() > 0);
        assertTrue(delayQueue.tryAdd());
    }
}
```

## 4. Semaphore与Mutex

(Mutex)互斥量的作用类似于二进制信号量，我们可以用它来实现互斥。

在下面的示例中，我们将使用一个简单的二进制信号量来构建计数器：

```java
class CounterUsingMutex {
    private final Semaphore mutex;
    private int count;

    CounterUsingMutex() {
        mutex = new Semaphore(1);
        count = 0;
    }

    void increase() throws InterruptedException {
        mutex.acquire();
        this.count = this.count + 1;
        Thread.sleep(1000);
        mutex.release();
    }

    int getCount() {
        return this.count;
    }

    boolean hasQueuedThreads() {
        return mutex.hasQueuedThreads();
    }
}
```

当许多线程试图同时访问计数器时，它们将被阻塞在队列中：

```java
class SemaphoresManualTest {

    @Test
    void whenMutexAndMultipleThreads_thenBlocked() {
        final int count = 5;
        final ExecutorService executor = Executors.newFixedThreadPool(count);
        final CounterUsingMutex counter = new CounterUsingMutex();
        IntStream.range(0, count)
                .forEach(user -> executor.submit(() -> {
                    try {
                        counter.increase();
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }));
        executor.shutdown();

        assertTrue(counter.hasQueuedThreads());
    }
}
```

当我们阻塞主线程时，所有线程都可以访问计数器，队列中没有剩余线程：

```java
class SemaphoresManualTest {

    @Test
    void givenMutexAndMultipleThreads_ThenDelay_thenCorrectCount() throws InterruptedException {
        final int count = 5;
        final ExecutorService executorService = Executors.newFixedThreadPool(count);
        final CounterUsingMutex counter = new CounterUsingMutex();
        IntStream.range(0, count)
                .forEach(user -> executorService.execute(() -> {
                    try {
                        counter.increase();
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }));

        executorService.shutdown();
        assertTrue(counter.hasQueuedThreads());
        Thread.sleep(5000);
        assertFalse(counter.hasQueuedThreads());
        assertEquals(count, counter.getCount());
    }
}
```

## 5. 总结

在本文中，我们探讨了Java中信号量的基础知识。