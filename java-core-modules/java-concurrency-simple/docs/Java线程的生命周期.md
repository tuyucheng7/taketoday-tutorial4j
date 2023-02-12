## 1. 概述

在本文中，我们将详细讨论Java中的一个核心概念-线程的生命周期。

我们将使用快速说明图，当然还有实用的代码片段，以更好地理解线程执行期间的这些状态。

要开始理解Java中的线程，[这篇](https://www.baeldung.com/java-runnable-vs-extending-thread)关于创建线程的文章是一个很好的起点。

## 2. Java中的多线程

**在Java语言中，多线程是由线程的核心概念驱动的**。在它们的生命周期中，线程会经历各种状态：

<img src="../assets/img.png">

## 3. Java线程的生命周期

java.lang.Thread类包含一个静态的State枚举-它定义了线程的潜在状态。在任何给定的时间点，线程只能处于以下状态之一：

+ **NEW**：一个新创建的线程，还没有开始执行
+ **RUNNABLE**：正在运行或准备执行，但它正在等待资源分配
+ **BLOCKED**：等待获取监视器锁以进入或重新进入同步块/方法
+ **WAITING**：等待其他线程执行特定操作，没有任何时间限制
+ **TIMED_WAITING**：等待其他线程在指定时间段内执行特定操作
+ **TERMINATED**：已完成执行

上图涵盖了所有这些状态；现在让我们详细讨论其中的每一个。

### 3.1 NEW

**新线程(或出生线程)是已创建但尚未启动的线程**。它一直保持这种状态，直到我们使用start()方法启动它。

以下代码片段显示了一个新创建的处于NEW状态的线程：

```java
Runnable runnable = new NewState();
Thread t = new Thread(runnable);
Log.info(t.getState());
```

由于我们还没有启动上述线程，因此方法t.getState()会打印：

```java
NEW
```

### 3.2 RUNNABLE

当我们创建了一个新线程并在其上调用start()方法时，它会从NEW状态转移到RUNNABLE状态。**处于此状态的线程正在运行或准备运行，但它们正在等待系统分配资源**。

在多线程环境中，线程调度器(JVM的一部分)为每个线程分配固定的时间量。因此它会运行一段特定的时间，然后将控制权交给其他RUNNABLE线程。

例如，让我们将t.start()调用添加到之前的代码中，并尝试访问其当前状态：

```java
Runnable runnable = new NewState();
Thread t = new Thread(runnable);
t.start();
Log.info(t.getState());
```

以上代码**最有可能**打印以下输出：

```shell
RUNNABLE
```

请注意，在此示例中，并不总是保证当我们调用t.getState()时仍处于RUNNABLE状态。

它可能会立即被线程调度器调度并可能完成执行。在这种情况下，我们可能会得到不同的输出。

### 3.3 BLOCKED

线程当前不符合运行条件时处于阻塞状态。**它在等待监视器锁并试图访问被其他线程锁定的代码段时进入此状态**。

让我们尝试重现这个状态：

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
2. t1启动并进入同步的commonResource()方法；这意味着只有一个线程可以访问它；试图访问此方法的所有其他后续线程将被阻止进一步执行，直到当前线程完成处理。
3. 当t1进入这个方法时，它保持在无限while循环中；这只是为了模拟繁重的处理，以便所有其他线程都无法进入此方法。
4. 现在当我们启动t2时，它试图进入commonResource()方法，由于t1已经进入该方法，因此t2将保持在BLOCKED状态。

在这种状态下，我们调用t2.getState()并得到如下输出：

```shell
BLOCKED
```

### 3.4 WAITING

**当一个线程正在等待其他线程执行特定操作时，它处于WAITING状态**。根据[Java文档](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/Thread.State.html#WAITING)，任何线程都可以通过调用以下三种方法中的任何一种来进入此状态：

1. object.wait()
2. thread.join()
3. LockSupport.park()

请注意，在wait()和join()中，我们没有定义任何超时时间，因为该场景将在下一节中介绍。

我们有一个[单独的教程](https://www.baeldung.com/java-wait-notify)，详细讨论了wait()、notify()和notifyAll()的使用。

现在，让我们尝试重现此状态：

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

让我们讨论一下我们在这里做什么：

1. 我们创建并启动t1线程
2. t1线程执行时创建并启动t2线程
3. 当t2的处理继续进行时，我们调用t2.join()，这会将t1置于WAITING状态，直到t2完成执行
4. 由于t1正在等待t2完成，我们从t2调用t1.getState()

如你所料，这里的输出是：

```shell
WAITING
```

### 3.5 TIMED_WAITING

**当一个线程在规定的时间内等待另一个线程执行特定的操作时，它处于TIMED_WAITING状态**。

根据[Java文档](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/Thread.State.html#TIMED_WAITING)的说法，有五种方法可以将线程置于TIMED_WAITING状态：

1. thread.sleep(long millis)
2. wait(int timeout)或者wait(int timeout, int nanos)
3. thread.join(long millis)
4. LockSupport.parkNanos
5. LockSupport.parkUntil

要详细了解Java中wait()和sleep()之间的区别，请查看此处的[这篇](https://www.baeldung.com/java-wait-and-sleep)专门文章。

现在，让我们尝试快速重现此状态：

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

在这里，我们创建并启动了一个线程t1，该线程进入休眠状态，超时时间为5秒；输出将是：

```shell
TIMED_WAITING
```

### 3.6 TERMINATED

这是线程死亡的状态。**当它完成执行或因为异常终止时，它处于TERMINATED状态**。

我们有一篇[专门的文章](https://www.baeldung.com/java-thread-stop)讨论停止线程的不同方法。

让我们尝试在以下示例中实现此状态：

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

在这里，当我们启动线程t1时，下一条语句Thread.sleep(1000)为t1提供了足够的时间来完成执行，因此该程序打印的输出如下：

```shell
TERMINATED
```

除了线程状态，我们还可以使用[isAlive()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Thread.html#isAlive())方法来确定线程是否处于活动状态。例如，如果我们在这个线程上调用isAlive()方法：

```java
Assertions.assertFalse(t1.isAlive());
```

它返回false。简而言之，**当且仅当线程已启动且尚未死亡时，线程才处于活动状态**。

## 4. 总结

在本教程中，我们了解了Java中线程的生命周期。我们查看了Thread.State枚举定义的所有六个状态，并通过快速示例重现了它们。

尽管代码片段在几乎每台机器上都会给出相同的输出，但在某些特殊情况下，我们可能会得到一些不同的输出，因为无法确定线程调度器的确切行为。