## 1. 概述

在这篇简短的文章中，我们将看看Java核心中的标准sleep()和wait()方法，并介绍它们之间的异同。

## 2. wait和sleep的一般区别

简单地说，**wait()是一个用于线程同步的实例方法**。

它可以在任何对象上调用，因为它是在java.lang.Object中定义的，但**只能从同步代码块调用它**。它释放对象上的锁，以便另一个线程可以进入并获取锁。

另一方面，Thread.sleep()是一个可以从任何上下文调用的静态方法。**Thread.sleep()暂停当前线程，不释放任何锁**。

下面是对这两个核心API的一个非常简单的初步介绍：

```java
public class WaitSleepExample {
    private static final Logger LOG = LoggerFactory.getLogger(WaitSleepExample.class);
    private static final Object LOCK = new Object();

    public static void main(String... args) throws InterruptedException {
        sleepWaitInSynchronizedBlocks();
    }

    private static void sleepWaitInSynchronizedBlocks() throws InterruptedException {
        Thread.sleep(1000); // called on the thread
        LOG.debug("Thread '" + Thread.currentThread().getName() + "' is woken after sleeping for 1 second");

        synchronized (LOCK) {
            LOCK.wait(1000); // called on the object, synchronization required
            LOG.debug("Object '" + LOCK + "' is woken after waiting for 1 second");
        }
    }
}
```

运行以上代码的输出为：

```shell
...... >>> Thread 'main' is woken after sleeping for 1 second 
...... >>> Object 'java.lang.Object@1786dec2' is woken after waiting for 1 second 
```

## 3. 唤醒wait和sleep

当我们使用sleep()方法时，线程会在指定的时间间隔后启动，除非它被中断。

对于wait()，唤醒过程要复杂一些。我们可以通过在正在等待的监视器上调用notify()或notifyAll()方法来唤醒线程。

如果要唤醒所有处于等待状态的线程，可以使用notifyAll()。与wait()方法本身类似，必须从同步上下文中调用notify()和notifyAll()。

例如，以下是wait的使用方式：

```java
public class ThreadA {
    private static final Logger LOG = LoggerFactory.getLogger(ThreadA.class);
    private static final ThreadB b = new ThreadB();

    public static void main(String... args) throws InterruptedException {
        b.start();

        synchronized (b) {
            while (b.sum == 0) {
                LOG.debug("Waiting for ThreadB to complete...");
                b.wait();
            }
            LOG.debug("ThreadB has completed. Sum from that thread is: " + b.sum);
        }
    }
}
```

然后，**另一个线程就可以通过在监视器上调用notify()来唤醒等待的线程**。

```java
class ThreadB extends Thread {
    int sum;

    @Override
    public void run() {
        synchronized (this) {
            int i = 0;
            while (i < 100000) {
                sum += i;
                i++;
            }
            notify();
        }
    }
}
```

运行以上代码的输出为：

```shell
...... >>> Waiting for ThreadB to complete... 
...... >>> ThreadB has completed. Sum from that thread is: 704982704
```

## 4. 总结

本文是对Java中wait和sleep语义的快速入门。

通常，我们应该使用sleep()来控制一个线程的执行时间，而wait()用于多线程同步。当然，在充分理解了基础知识之后，还有很多东西值得探索。