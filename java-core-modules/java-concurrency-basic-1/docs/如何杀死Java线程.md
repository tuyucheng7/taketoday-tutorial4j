## 1. 概述

在这篇简短的文章中，**我们将介绍如何在Java中终止一个线程，这并不是那么简单，因为Thread.stop()方法已被弃用**。

正如Oracle的[此更新](https://docs.oracle.com/javase/8/docs/technotes/guides/concurrency/threadPrimitiveDeprecation.html)中所述，stop()可能会导致被监视的对象损坏。

## 2. 使用标志

让我们从一个创建和启动线程的类开始，这个任务不会自行结束，因此我们需要某种方法来停止该线程。

我们将为此使用一个原子标志：

```java
public class ControlSubThread implements Runnable {
    private Thread worker;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private int interval;

    public ControlSubThread(int sleepInterval) {
        interval = sleepInterval;
    }

    public void start() {
        worker = new Thread(this);
        worker.start();
    }

    public void stop() {
        running.set(false);
    }

    public void run() {
        running.set(true);
        while (running.get()) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread was interrupted, Failed to complete operation");
            }
            // do something here
        }
    }
}
```

在while循环中我们使用的不是常量true，而是一个AtomicBoolean，现在我们可以通过将其设置为true/false来启动/停止执行。

正如我们在[原子变量简介](https://www.baeldung.com/java-atomic-variables)中所解释的，使用AtomicBoolean可以防止在不同线程中设置和检查变量时发生冲突。

## 3. 中断线程

如果sleep()设置为长时间间隔，或者如果我们等待一个可能永远不会释放的锁，会发生什么？

**我们会面临着长期阻塞或永远无法完全终止的风险**。

我们可以为这种情况创建一个interrupt()，让我们在类中添加一些方法和一个新标志变量：

```java
public class ControlSubThread implements Runnable {
    private Thread worker;
    private AtomicBoolean running = new AtomicBoolean(false);
    private int interval;
    // ...

    public void interrupt() {
        running.set(false);
        worker.interrupt();
    }

    boolean isRunning() {
        return running.get();
    }

    boolean isStopped() {
        return stopped.get();
    }

    public void run() {
        running.set(true);
        stopped.set(false);
        while (running.get()) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread was interrupted, Failed to complete operation");
            }
            // do something
        }
        stopped.set(true);
    }
}
```

我们添加了一个interrupt()方法，该方法将running标志设置为false，并调用worker线程的interrupt()方法。

如果调用此方法时线程处于睡眠状态，sleep()将退出并抛出InterruptedException，就像任何其他阻塞调用一样。

这会将线程返回到while循环，并且因为上一步设置running为false，所以它将退出。

## 4. 总结

在这个快速教程中，我们研究了如何使用原子变量，结合对interrupt()的调用，优雅地关闭线程，这绝对比调用已弃用的stop()方法并冒着永远锁定和内存损坏的风险要好。