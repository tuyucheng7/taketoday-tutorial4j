## 1. 概述

在本教程中，我们将讨论Thread类中的不同join()方法。我们将详细介绍这些方法和一些示例代码。

与wait()和notify()方法一样，join()是线程间同步的另一种机制。

你可以快速浏览[本教程](https://www.baeldung.com/java-wait-notify)以了解有关wait()和notify()的更多信息。

## 2. Thread.join()

join()方法在[Thread](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Thread.html#join())类中定义：

> public final void join() throws InterruptedException Waits for this thread to die.

**当我们在线程上调用join()方法时，调用线程将进入等待状态。它保持等待状态，直到引用的线程终止**。

我们可以在以下代码中看到这种行为：

```java
class SampleThread extends Thread {
    public int processingCount;

    SampleThread(int processingCount) {
        this.processingCount = processingCount;
        LOGGER.debug("Thread " + this.getName() + " created");
    }

    @Override
    public void run() {
        LOGGER.debug("Thread " + this.getName() + " started");
        while (processingCount > 0) {
            try {
                Thread.sleep(1000); // Simulate some work being done by thread
            } catch (InterruptedException e) {
                LOGGER.debug("Thread " + this.getName() + " interrupted.");
            }
            processingCount--;
            LOGGER.debug("Inside Thread " + this.getName() + ", processingCount = " + processingCount);
        }
        LOGGER.debug("Thread " + this.getName() + " exiting");
    }
}

@Test
void givenStartedThread_whenJoinCalled_waitsTillCompletion() throws InterruptedException {
    Thread t2 = new SampleThread(1);
    t2.start();
    LOGGER.debug("Invoking join.");
    t2.join();
    LOGGER.debug("Returned from join");
    assertFalse(t2.isAlive());
}
```

执行代码时，我们应该期望得到类似于以下的结果：

```shell
DEBUG ... - Thread Thread-3 created
DEBUG ... - Invoking join.
DEBUG ... - Thread Thread-3 started
DEBUG ... - Inside Thread Thread-3, processingCount = 0
DEBUG ... - Thread Thread-3 exiting
DEBUG ... - Returned from join
```

**如果引用的线程被中断，join()方法也可能返回**。在这种情况下，该方法抛出InterruptedException。

最后，**如果引用的线程已经终止或尚未启动，则对join()方法的调用将立即返回**。

```java
Thread t1 = new SampleThread(0);
t1.join(); // returns immediately
```

## 3. 带超时的Thread.Join()

如果引用的线程被阻塞或处理时间过长，则join()方法将继续等待。这可能会成为一个问题，因为调用线程将变得无响应。为了处理这些情况，我们使用join()方法的重载版本，它允许我们指定超时期限。

**join()方法有两种[超时版本](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/Thread.html#join(long))的重载**：

> public final void join(long millis) throws InterruptedException：等待此线程死亡的时间最多为millis毫秒。0意味着永远等待。
> 
> public final void join(long millis,int nanos) throws InterruptedException：等待此线程死亡的时间最多为millis毫秒加nanos纳秒。

我们可以按如下方式使用超时版本的join()：

```java
@Test
void givenStartedThread_whenTimedJoinCalled_waitsUntilTimedOut() throws InterruptedException {
    Thread t3 = new SampleThread(10);
    t3.start();
    t3.join(1000);
    assertTrue(t3.isAlive());
}
```

在这种情况下，调用线程等待线程t3完成大约1秒。如果线程t3在此时间段内没有完成，则join()方法将控制权返回给调用方法。

**超时版本的join()取决于操作系统的计时。因此，我们不能假设join()会等待指定的时间**。

## 4. Thread.join()和同步

除了等待终止之外，调用join()方法还有一个同步效果。**join()创建一个“[happens-before](https://docs.oracle.com/javase/specs/jls/se8/html/jls-17.html#jls-17.4.5)”关系**：

> 线程中的所有操作发生在任何其他线程从该线程上的join()成功返回之前。

这意味着当线程t1调用t2.join()时，t2所做的所有更改在返回时在t1中可见。但是，如果我们不调用join()或使用其他同步机制，则无法保证即使其他线程已完成，当前线程也能看到其他线程中的更改。

因此，即使对处于终止状态的线程调用join()方法会立即返回，但在某些情况下我们仍然需要调用它。

我们可以在下面看到一个不正确同步代码的示例：

```java
@Test
@Disabled
void givenThreadTerminated_checkForEffect_notGuaranteed() {
    SampleThread t4 = new SampleThread(10);
    t4.start();
    // not guaranteed to stop even if t4 finishes.
    do {

    } while (t4.processingCount > 0);
}
```

为了正确同步上述代码，我们可以在循环中添加定时的t4.join()调用或使用其他一些同步机制。

## 5. 总结

join()方法对于线程间同步非常有用。在本文中，我们讨论了join()方法及它的行为。我们还检查了使用join()方法的代码。