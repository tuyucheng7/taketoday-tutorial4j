## 1. 概述

多线程编程允许我们并发运行线程，每个线程可以处理不同的任务。因此，它可以优化资源的使用，特别是当我们的计算机具有多个CPU或CPU有多个核心时。

有时，我们希望控制多个线程同时启动。在本教程中，我们首先说明我们的需求，尤其是“完全相同的时间”的含义。此外，我们将讨论如何在Java中同时启动两个线程。

## 2. 了解需求

我们的需求是：“同时启动两个线程”。

这个要求看起来很容易理解。但是，如果我们仔细考虑一下，是否可能同时启动两个线程？

首先，每个线程都会消耗CPU时间来工作。因此，如果我们的应用程序在单核CPU的计算机上运行，则不可能同时启动两个线程。

如果我们的计算机有多核CPU或多个CPU核心，两个线程可能会同时启动。但是，我们无法在Java中控制它。

这是因为当我们在Java中使用线程时，Java线程调度依赖于操作系统的线程调度。因此，不同的操作系统可能会以不同的方式处理它。

此外，根据爱因斯坦的狭义相对论，如果我们以更严格的方式讨论“完全相同的时间”：

> 从绝对意义上说，如果两个不同的事件在空间上是分开的，就不可能同时发生。

无论我们的CPU离主板或CPU中的内核有多近，都有空间。因此，我们无法确保两个线程同时启动。

那么，这是否意味着该需求无效？

不，这是一个有效的要求。即使我们不能让两个线程完全同时启动，我们也可以通过一些同步技术非常接近。

在大多数实际情况下，当我们需要两个线程“同时”启动时，这些技术可能会对我们有所帮助。

在本教程中，我们将探索解决此问题的几种方法：

+ 使用CountDownLatch类
+ 使用CyclicBarrier类
+ 使用Phaser类

所有方法都遵循相同的思想：我们不会真正同时启动两个线程。相反，我们在线程启动后立即阻塞线程，并尝试同时恢复它们的执行。

由于我们的测试与线程调度相关，因此值得在本教程中提及运行测试的环境：

+ CPU：11th Gen Intel(R) Core(TM) i5-1135G7。处理器时钟在2.6和4.3GHz之间(4.1 4核，4 GHz 6核)
+ 操作系统：64位Windows，内核版本11
+ Java：Java 17

现在，让我们看看CountDownLatch和CyclicBarrier的作用。

## 3. 使用CountDownLatch类

CountDownLatch是Java 5中引入的一个同步器，作为java.util.concurrent包的一部分。
通常，我们使用CountDownLatch来阻塞线程，直到其他线程完成它们的任务。

简单地说，我们在一个latch对象中设置一个计数，并将latch对象与一些线程相关联。当我们启动这些线程时，它们将被阻塞，直到latch的计数变为零。

另一方面，在其他线程中，我们可以控制在什么条件下减少计数并让阻塞的线程恢复，例如，当主线程中的某些任务完成时。

### 3.1 工作线程

现在，让我们看看如何使用CountDownLatch类解决问题。

首先，我们创建一个线程类。称之为WorkerWithCountDownLatch：

```java
public class WorkerWithCountDownLatch extends Thread {
    private final CountDownLatch latch;

    public WorkerWithCountDownLatch(String name, CountDownLatch latch) {
        this.latch = latch;
        setName(name);
    }

    @Override
    public void run() {
        try {
            System.out.printf("[ %s ] created, blocked by the latchn", getName());
            latch.await();
            System.out.printf("[ %s ] starts at: %sn", getName(), Instant.now());
            // do actual work here ...
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

我们在WorkerWithCountDownLatch类中添加了一个latch对象。首先，我们来了解一下latch对象的作用。

在run()方法中，我们调用了latch.await()方法。这意味着，如果我们启动工作线程，它将检查latch的计数。线程将被阻塞，直到计数为零。

通过这种方式，我们就可以在主线程中创建一个count=1的CountDownLatch(1)锁存器，并将latch对象关联到我们要同时启动的两个工作线程。

当我们希望两个线程继续执行它们的实际工作时，我们通过在主线程中调用latch.countDown()来释放latch。

接下来，我们来看看主线程是如何控制两个工作线程的。

### 3.2 主线程

我们在usingCountDownLatch()方法中实现主线程的逻辑：

```java
public class ThreadsStartAtSameTime {

    private static void usingCountDownLatch() throws InterruptedException {
        System.out.println("===============================================");
        System.out.println("        >>> Using CountDownLatch <<<<");
        System.out.println("===============================================");

        CountDownLatch latch = new CountDownLatch(1);

        WorkerWithCountDownLatch worker1 = new WorkerWithCountDownLatch("Worker with latch 1", latch);
        WorkerWithCountDownLatch worker2 = new WorkerWithCountDownLatch("Worker with latch 2", latch);

        worker1.start();
        worker2.start();

        Thread.sleep(10); // simulation of some actual work

        System.out.println("-----------------------------------------------");
        System.out.println(" Now release the latch:");
        System.out.println("-----------------------------------------------");
        latch.countDown();
    }
}
```

然后，我们可以从main()方法调用上面的usingCountDownLatch()方法。当我们运行main()方法时，我们会得到以下输出：

```text
===============================================
        >>> Using CountDownLatch <<<<
===============================================
[ Worker with latch 1 ] created, blocked by the latch
[ Worker with latch 2 ] created, blocked by the latch
-----------------------------------------------
 Now release the latch:
-----------------------------------------------
[ Worker with latch 2 ] starts at: 2022-09-23T04:19:19.653220700Z
[ Worker with latch 1 ] starts at: 2022-09-23T04:19:19.653220700Z
```

如上面的输出所示，两个工作线程几乎同时启动。

## 4. 使用CyclicBarrier类

CyclicBarrier类是Java 5中引入的另一个同步器。
本质上，CyclicBarrier允许固定数量的线程在继续执行之前相互等待达到一个公共点。

接下来，让我们看看我们如何使用CyclicBarrier类来解决我们的问题。

### 4.1 工作线程

我们先来看看我们的工作线程的实现：

```java
public class WorkerWithCyclicBarrier extends Thread {
    private final CyclicBarrier barrier;

    public WorkerWithCyclicBarrier(String name, CyclicBarrier barrier) {
        this.barrier = barrier;
        this.setName(name);
    }

    @Override
    public void run() {
        try {
            System.out.printf("[ %s ] created, blocked by the barriern", getName());
            barrier.await();
            System.out.printf("[ %s ] starts at: %sn", getName(), Instant.now());
            // do actual work here...
        } catch (InterruptedException | BrokenBarrierException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

我们将barrier对象与工作线程相关联。当线程启动时，我们立即调用barrier.await()方法。

这样，工作线程就会被阻塞，等待各方调用barrier.await()来恢复。

### 4.2 主线程

接下来，我们看看如何控制在主线程中恢复两个工作线程的执行：

```java
public class ThreadsStartAtSameTime {

    private static void usingCyclicBarrier() throws BrokenBarrierException, InterruptedException {
        System.out.println("n===============================================");
        System.out.println("        >>> Using CyclicBarrier <<<<");
        System.out.println("===============================================");

        CyclicBarrier barrier = new CyclicBarrier(3);

        WorkerWithCyclicBarrier worker1 = new WorkerWithCyclicBarrier("Worker with barrier 1", barrier);
        WorkerWithCyclicBarrier worker2 = new WorkerWithCyclicBarrier("Worker with barrier 2", barrier);

        worker1.start();
        worker2.start();

        Thread.sleep(10);// simulation of some actual work

        System.out.println("-----------------------------------------------");
        System.out.println(" Now open the barrier:");
        System.out.println("-----------------------------------------------");
        barrier.await();
    }
}
```

我们的目标是让两个工作线程同时恢复。所以，算上主线程，我们一共有3个线程。

如上面的方法所示，我们在主线程中创建了一个包含3个parties(参与线程数)的屏障对象。接下来，我们创建并启动两个工作线程。

正如我们之前所讨论的，两个工作线程被阻塞，等待屏障恢复打开。

在主线程中，我们可以做一些实际的工作。当我们决定打开屏障时，我们调用方法barrier.await()让两个工作线程继续执行。

如果我们在main()方法中调用usingCyclicBarrier()，得到的输出如下：

```text
===============================================
        >>> Using CyclicBarrier <<<<
===============================================
[ Worker with barrier 1 ] created, blocked by the barrier
[ Worker with barrier 2 ] created, blocked by the barrier
-----------------------------------------------
 Now open the barrier:
-----------------------------------------------
[ Worker with barrier 1 ] starts at: 2022-09-23T04:26:54.513223400Z
[ Worker with barrier 2 ] starts at: 2022-09-23T04:26:54.513223400Z
```

## 5. 使用Phaser类

Phaser类是Java 7中引入的同步器。它类似于CyclicBarrier和CountDownLatch。但是，Phaser类更灵活。

例如，与CyclicBarrier和CountDownLatch不同，Phaser允许我们动态注册参与线程数。

### 5.1 工作线程

首先我们先看看实现，然后了解它是如何工作的：

```java
public class WorkerWithPhaser extends Thread {
    private final Phaser phaser;

    public WorkerWithPhaser(String name, Phaser phaser) {
        this.phaser = phaser;
        phaser.register();
        setName(name);
    }

    @Override
    public void run() {
        try {
            System.out.printf("[ %s ] created, blocked by the phasern", getName());
            phaser.arriveAndAwaitAdvance();
            System.out.printf("[ %s ] starts at: %sn", getName(), Instant.now());
            // do actual work here...
        } catch (IllegalStateException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

当一个工作线程被实例化时，我们通过调用phaser.register()将当前线程注册到给定的Phaser对象。这样，当前线程就成为了Phaser的一个参与线程。

接下来，当工作线程启动时，我们立即调用phaser.arriveAndAwaitAdvance()。
因此，我们告诉Phaser当前线程已经到达，并将等待其他参与线程的到达来继续进行。当然，在其他参与线程到来之前，当前线程就被阻塞了。

### 5.2 主线程

```java
public class ThreadsStartAtSameTime {

    private static void usingPhaser() throws InterruptedException {
        System.out.println("n===============================================");
        System.out.println("        >>> Using Phaser <<<");
        System.out.println("===============================================");

        Phaser phaser = new Phaser();
        phaser.register();

        WorkerWithPhaser worker1 = new WorkerWithPhaser("Worker with phaser 1", phaser);
        WorkerWithPhaser worker2 = new WorkerWithPhaser("Worker with phaser 2", phaser);

        worker1.start();
        worker2.start();

        Thread.sleep(10);// simulation of some actual work

        System.out.println("-----------------------------------------------");
        System.out.println(" Now open the phaser barrier:");
        System.out.println("-----------------------------------------------");
        phaser.arriveAndAwaitAdvance();
    }
}
```

在上面的代码中，我们可以看到，主线程也将自己注册为Phaser对象的参与线程。

在我们创建并阻塞了两个工作线程之后，主线程也调用了phaser.arriveAndAwaitAdvance()。
这样，我们打开了Phaser屏障，让两个工作线程可以同时恢复执行。

最后，让我们在main()方法中调用usingPhaser()方法：

```text
===============================================
        >>> Using Phaser <<<
===============================================
[ Worker with phaser 1 ] created, blocked by the phaser
[ Worker with phaser 2 ] created, blocked by the phaser
-----------------------------------------------
 Now open the phaser barrier:
-----------------------------------------------
[ Worker with phaser 2 ] starts at: 2022-09-23T04:26:54.559380600Z
[ Worker with phaser 1 ] starts at: 2022-09-23T04:26:54.559380600Z
```

## 6. 总结

在本文中，我们介绍了同时启动两个线程的3种方法：分别使用使用CountDownLatch、CyclicBarrier和Phaser。

他们的使用是相似的，阻塞两个线程并试图让它们同时恢复执行。

在我的个人笔记本上，运行多次之后总是会输出相同的时间，这意味着它们确实是同时恢复执行的，这取决于具体的运行环境。
即使不能做到完全同时，但对于现实世界的大多数情况来说，都已经足够了。