## 1. 概述

在本教程中，我们将讨论Thread类中的join()方法。

与wait()和notify()方法一样，join()是线程间同步的另一种机制。

## 2. Thread.join()

join()方法在Thread类中定义：

> public final void join() throws InterruptedException Waits for this thread to die.

当我们在线程上调用join()方法时，调用线程将进入等待状态。它将保持等待状态，直到被引用的线程终止。

我们可以在以下代码中看到这种行为：

```java
public class ThreadJoinUnitTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadJoinUnitTest.class);

    static class SampleThread extends Thread {
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
}
```

```java
public class ThreadJoinUnitTest {

    @Test
    void givenStartedThread_whenJoinCalled_waitsTillCompletion() throws InterruptedException {
        Thread t2 = new SampleThread(1);
        t2.start();
        LOGGER.debug("Invoking join.");
        t2.join();
        LOGGER.debug("Returned from join");
        assertFalse(t2.isAlive());
    }
}
```

在执行代码时，我们应该期望得到与以下类似的结果：

```
DEBUG cn.tuyucheng.taketoday.concurrent.thread.join.ThreadJoinUnitTest - Thread Thread-3 created
DEBUG cn.tuyucheng.taketoday.concurrent.thread.join.ThreadJoinUnitTest - Invoking join.
DEBUG cn.tuyucheng.taketoday.concurrent.thread.join.ThreadJoinUnitTest - Thread Thread-3 started
DEBUG cn.tuyucheng.taketoday.concurrent.thread.join.ThreadJoinUnitTest - Inside Thread Thread-3, processingCount = 0
DEBUG cn.tuyucheng.taketoday.concurrent.thread.join.ThreadJoinUnitTest - Thread Thread-3 exiting
DEBUG cn.tuyucheng.taketoday.concurrent.thread.join.ThreadJoinUnitTest - Returned from join
```

如果引用的线程被中断，join()方法也可能返回。在这种情况下，该方法抛出InterruptedException。

最后，如果引用的线程已经终止或尚未启动，那么对join()方法的调用将立即返回。

```
Thread t1 = new SampleThread(0);
t1.join(); // returns immediately
```

## 3. 带有超时的Thread.Join()

如果引用的线程被阻塞或处理时间过长，join()方法将保持等待。
这可能会成为一个问题，因为调用线程将变得无响应。为了处理这些情况，我们使用重载版本的join()方法来指定超时时间。

join()方法有两种超时版本的重载：

+ public final void join(long millis) throws InterruptedException - 等待此线程死亡的时间最多为millis毫秒。0表示永远等待。
+ public final void join(long millis,int nanos) throws InterruptedException - 等待此线程死亡的时间最多为millis毫秒加nanos纳秒。

我们可以按如下方式使用超时版本的join()：

```java
public class ThreadJoinUnitTest {

    @Test
    void givenStartedThread_whenTimedJoinCalled_waitsUntilTimedOut() throws InterruptedException {
        Thread t3 = new SampleThread(10);
        t3.start();
        t3.join(1000);
        assertTrue(t3.isAlive());
    }
}
```

在这种情况下，调用线程等待大约1秒，等待线程t3完成。如果线程t3在此时间段内没有完成，join()方法将控制权返回给调用方法。

超时版本的join()取决于操作系统的计时。因此，我们不能确保join()会等待指定的时间。

## 4. Thread.join()和同步

除了等待终止之外，调用join()方法还具有同步效果。join()创建“happens-before”关系：

> 线程中的所有操作都发生在任何其他线程从该线程上的join()成功返回之前。

这意味着当线程t1调用t2.join()时，t2所做的所有更改在t1返回时都可见。
然而，如果我们不调用join()或使用其他同步机制，我们无法保证即使其他线程已经完成，其他线程中的更改也会对当前线程可见。

因此，即使对处于终止状态的线程的join()方法调用立即返回，我们仍需要在某些情况下调用它。

我们可以在下面看到一个不正确同步代码的示例：

```java
public class ThreadJoinUnitTest {

    @Test
    @Disabled
    void givenThreadTerminated_checkForEffect_notGuaranteed() {
        SampleThread t4 = new SampleThread(10);
        t4.start();
        // not guaranteed to stop even if t4 finishes.
        do {

        } while (t4.processingCount > 0);
    }
}
```

为了正确同步上述代码，我们可以在循环中添加定时的t4.join()调用或使用其他一些同步机制。

## 5. 总结

join()方法对于线程间同步非常有用。在本文中，我们讨论了join()方法及它的行为。