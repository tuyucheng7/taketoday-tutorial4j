## 1. 概述

在本快速教程中，我们将探讨Java中[信号量](https://www.baeldung.com/cs/semaphore)和互斥量的基础知识。

## 2. 信号量

我们将从java.util.concurrent.Semaphore开始。我们可以使用信号量来限制访问特定资源的并发线程数。

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

请注意我们是如何使用以下方法的：

+ tryAcquire()：如果许可立即可用则返回true并获取它，否则返回false。而acquire()获取许可并阻塞直到许可可用
+ release()：释放许可
+ availablePermits()：返回当前可用的许可数

为了测试我们的登录队列，我们将首先尝试达到限制并检查下一次登录尝试是否会被阻止：

```java
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
```

接下来，在我们注销一个用户后，应该有可用的许可：

```java
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
```

## 3. 定时信号量

接下来，我们将讨论Apache Commons中的TimedSemaphore。TimedSemaphore允许多个许可作为一个简单的信号量，但是在给定的时间段内，在这个时间段之后重置时间并释放所有许可。

我们可以使用TimedSemaphore来构建一个简单的延迟队列，如下所示：

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

当我们使用以1秒为时间段的延迟队列并且在1秒内使用完所有许可后，应该没有可用的许可：

```java 
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
```

**但是在休眠一段时间后，信号量应该重置并释放许可**：

```java
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
```

## 4. Semaphore与Mutex

互斥量(Mutex)的作用类似于二进制信号量，我们可以用它来实现互斥。

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

当许多线程试图同时访问计数器时，**它们将被简单地阻塞在队列中**：

```java
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
```

当我们等待时，所有线程都将访问计数器并且没有线程留在队列中：

```java
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
```

## 5. 总结

在本文中，我们探讨了Java中信号量的基础知识。