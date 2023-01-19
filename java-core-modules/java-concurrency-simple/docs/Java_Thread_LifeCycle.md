## 1. 概述

在本文中，我们将详细讨论Java中的一个核心概念 —— 线程的生命周期。

根据一张图解，配合实用的代码片段，以便更好地理解线程执行期间的这些状态。

## 2. Java中的多线程

在Java语言中，多线程是由线程的核心概念驱动的。线程在其生命周期中会经历各种状态：

<img src="../assets/img.png">

## 3. Java中线程的生命周期

java.lang.Thread类包含一个静态的State枚举，定义了线程的潜在状态。在任何给定的时间点，线程只能处于以下状态之一：

+ NEW：尚未开始执行的新创建线程
+ RUNNABLE：正在运行或准备执行，但它正在等待资源分配
+ BLOCKED：等待获取监视器锁以进入或重新进入同步块/方法
+ WAITING：等待其他线程执行特定操作而没有任何时间限制
+ TIMED_WAITING：等待其他线程在指定时间段内执行特定操作
+ TERMINATED：已经完成了执行

上图涵盖了所有这些状态；现在让我们详细讨论每一个状态阶段。

### 3.1 NEW

新线程(或出生线程)是已创建但尚未启动的线程。在我们使用start()方法启动它之前，它一直处于这种状态。

以下代码段显示了一个新创建的线程，该线程处于NEW状态：

```text
Runnable runnable = new NewState();
Thread t = new Thread(runnable);
Log.info(t.getState());
```

由于我们尚未启动上述线程，因此方法t.getState()会打印：

```text
NEW
```

### 3.2 RUNNABLE

当我们创建了一个新线程，并在其上调用start()方法时，它将从NEW状态转移到RUNNABLE状态。
处于这种状态的线程正在运行或准备运行，但它们正在等待来自系统的资源分配。

在多线程环境中，线程调度器(JVM的一部分)为每个线程分配固定的时间。因此，它会运行一段特定的时间，然后将控制权交给其他可运行的线程。

例如，我们将t.start()调用添加到前面的代码中，并尝试访问其当前状态：

```text
Runnable runnable = new NewState();
Thread t = new Thread(runnable);
t.start();
Log.info(t.getState());
```

以上代码最有可能打印以下输出：

```text
RUNNABLE
```

请注意，在本例中，并不总是保证调用t.getState()时仍处于RUNNABLE状态。

线程调度程序可能会立即对其进行调度，并可能完成执行。在这种情况下，我们可能会得到不同的输出。

### 3.3 BLOCKED

线程当前不符合运行条件时处于阻塞状态。它在等待监视器锁并试图访问被其他线程锁定的代码段时进入此状态。

下面的代码演示了什么时候会处于此状态：

```java
public class BlockedState {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new DemoThreadB());
        Thread t2 = new Thread(new DemoThreadB());

        t1.start();
        t2.start();

        Thread.sleep(1000);

        System.out.println(t2.getState());
        System.exit(0);
    }

    static class DemoThreadB implements Runnable {
        @Override
        public void run() {
            commonResource();
        }

        public static synchronized void commonResource() {
            while (true) {
                // Infinite loop to mimic heavy processing
                // Thread 't1' won't leave this method
                // when Thread 't2' enters this
            }
        }
    }
}
```

在以上代码中：

1. 我们创建了两个不同的线程t1和t2。
2. t1启动并进入同步的commonResource()方法；该方法一次只有一个线程可以访问它；
   试图访问此方法的所有其他后续线程将被阻止进一步执行，直到当前线程完成处理。
3. 当t1进入这个方法时，它保持在一个无限的while循环中；这只是为了模拟繁重的处理过程，以便所有其他线程都无法进入此方法。
4. 现在，当我们启动t2时，它试图进入commonResource()方法，由于t1已经进入该方法，因此t2将保持在BLOCKED状态。

在这种状态下，我们调用t2.getState()得到的输出为：

```text
BLOCKED
```

### 3.4 WAITING

线程在等待其他线程执行特定操作时处于等待状态。根据Java文档，任何线程都可以通过调用以下三种方法中的任何一种进入这种状态：

1. object.wait()
2. thread.join()
3. LockSupport.park()

请注意，在wait()和join()中，我们没有定义任何超时时间，下一节中我们介绍这种情况。

```java
public class WaitingState implements Runnable {
    public static Thread t1;

    public static void main(String[] args) {
        t1 = new Thread(new WaitingState());
        t1.start();
    }

    public void run() {
        Thread t2 = new Thread(new DemoThreadWS());
        t2.start();

        try {
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    static class DemoThreadWS implements Runnable {
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }

            System.out.println(WaitingState.t1.getState());
        }
    }
}
```

让我们仔细剖析以上代码：

1. 我们创建并启动t1线程。
2. t1线程执行时创建并启动t2线程。
3. 当t2线程开始处理时，我们在t1线程上调用t2.join()。这会使t1处于等待状态，直到t2完成执行。
4. 因为t1正在等待t2完成，所以我们在t2线程中调用t1.getState()。

正如你所期望的，这里的输出是：

```text
WAITING
```

### 3.5 TIMED_WAITING

当一个线程在规定的时间内等待另一个线程执行特定的操作时，它处于TIMED_WAITING状态。

根据Java文档的说法，有五种方法可以将线程置于TIMED_WAITING状态：

1. thread.sleep(long millis)
2. wait(int timeout)或者wait(int timeout, int nanos)
3. thread.join(long millis)
4. LockSupport.parkNanos
5. LockSupport.parkUntil

现在，让我们尝试快速重现这种状态：

```java
public class TimedWaitingState {

    public static void main(String[] args) throws InterruptedException {
        DemoThread obj1 = new DemoThread();
        Thread t1 = new Thread(obj1);
        t1.start();
        // The following sleep will give enough time for ThreadScheduler
        // to start processing of thread t1
        Thread.sleep(1000);
        System.out.println(t1.getState());
    }

    static class DemoThread implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }
}
```

这里，我们创建并启动了一个线程t1，它进入睡眠状态，超时时间为5秒；最后得到的输出为：

```text
TIMED_WAITING
```

### 3.6 TERMINATED

这是线程死亡的状态。当它完成执行或因为异常终止时，它处于TERMINATED状态。

让我们尝试在以下示例中实现这种状态：

```java
public class TerminatedState implements Runnable {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new TerminatedState());
        t1.start();
        Thread.sleep(1000);
        System.out.println(t1.getState());
    }

    @Override
    public void run() {
        // No processing in this block
    }
}
```

在这里，我们启动了线程t1，下一行代码Thread.sleep(1000)为t1提供了足够的时间来完成执行，因此该程序打印的输出如下：

```text
TERMINATED
```

除了线程状态，我们还可以使用isAlive()方法来确定线程是否处于活动状态。例如，如果我们在这个线程上调用isAlive()方法：

```text
Assertions.assertFalse(t1.isAlive());
```

它返回false。简单地说，当且仅当线程已启动且尚未死亡时，线程才是活动的。

## 4. 总结

在本教程中，我们了解了Java中线程的生命周期。我们介绍了State枚举定义的线程所有六种状态。

上面所有代码几乎在每台电脑上都会给出相同的输出，但在某些例外情况下，我们可能会得到一些不同的输出，因为无法确定线程调度器的确切行为。