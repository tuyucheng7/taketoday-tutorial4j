## 1. 概述

在本文中，我们将研究java.util.concurrent包中的[Phaser](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Phaser.html)构造。它是一个功能非常类似于[CountDownLatch](https://www.baeldung.com/java-countdown-latch)的类，允许我们协调线程的执行。与CountDownLatch相比，它具有一些附加功能。

Phaser是一个屏障，动态数量的线程在继续执行之前需要等待。在CountDownLatch中，该数字无法动态配置，需要在创建实例时提供。

## 2. Phaser API

Phaser允许我们构建**线程在执行下一步之前需要在屏障上等待的逻辑**。

我们可以协调多个执行阶段，为每个程序阶段重用一个Phaser实例。每个阶段可以有不同数量的线程等待前进到另一个阶段。稍后我们将看一个使用Phaser的例子。

为了参与协调，线程需要向Phaser实例register()自身。请注意，这只会增加parties(注册方)的数量，我们无法检查当前线程是否已注册-我们必须对实现进行子类化才能支持这一点。

线程通过调用arriveAndWaitAdvance()来发出它已到达屏障的信号，这是一种阻塞方法。**当到达方的数量等于注册方的数量时，程序将继续执行，阶段数将增加**。我们可以通过调用getPhase()方法来获取当前的阶段号。

当线程完成它的任务时，我们应该调用arriveAndDeregister()方法来发出信号，表示当前线程不应再被计入这个特定阶段。

## 3. 使用Phaser实现逻辑

假设我们想要协调任务的多个阶段。三个线程将处理第一阶段，两个线程将处理第二阶段。

我们将创建一个实现Runnable接口的LongRunningAction类：

```java
class LongRunningAction implements Runnable {
    private String threadName;
    private Phaser phaser;

    LongRunningAction(String threadName, Phaser phaser) {
        this.threadName = threadName;
        this.ph = phaser;
        phaser.register();
    }

    @Override
    public void run() {
        ph.arriveAndAwaitAdvance();
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ph.arriveAndDeregister();
    }
}
```

当我们的LongRunningAction类被实例化时，我们使用register()方法注册到Phaser实例。这将增加使用该特定Phaser的线程数。

调用arriveAndWaitAdvance()将导致当前线程在屏障上等待。如前所述，当到达方的数量与注册方的数量相同时，执行将继续。

处理完成后，当前线程将通过调用arriveAndDeregister()方法取消注册自身。

让我们创建一个测试用例，在其中我们将启动三个LongRunningAction线程并在屏障上阻塞。接下来，在操作完成后，我们将创建两个额外的LongRunningAction线程来执行下一阶段的处理。

从主线程创建Phaser实例时，我们将1作为参数传递。这相当于从当前线程调用register()方法。我们这样做是因为，当我们创建三个工作线程时，主线程是一个协调器，因此Phaser需要注册四个线程：

```java
ExecutorService executorService = Executors.newCachedThreadPool();
Phaser phaser = new Phaser(1);

assertEquals(0, phaser.getPhase());
```

初始化后的阶段等于零。

Phaser类有一个构造函数，我们可以在其中向它传递一个父实例。当我们有大量的参与方会经历大量的同步争用成本时，它非常有用。在这种情况下，可以设置Phaser的实例，以便子Phaser组共享一个公共父级。

接下来，让我们启动三个LongRunningAction线程，它们将在屏障上等待，直到我们从主线程调用arriveAndWaitAdvance()方法。

请记住，我们已经用1初始化了Phaser，并启动三个线程，调用了register()三次。现在，三个LongRunningAction线程分别通知它们已经到达屏障，因此还需要再调用一次arriveAndAwaitAdvance()-来自主线程的调用：

```java
executorService.submit(new LongRunningAction("thread-1", phaser));
executorService.submit(new LongRunningAction("thread-2", phaser));
executorService.submit(new LongRunningAction("thread-3", phaser));

phaser.arriveAndAwaitAdvance();

assertEquals(1, phaser.getPhase());
```

该阶段完成后，getPhase()方法将返回1，因为程序完成了执行的第一阶段。

假设两个线程应该进行下一阶段的处理。我们可以利用Phaser来实现这一点，因为它允许我们动态配置应该在屏障上等待的线程数。我们另外启动两个新线程，但在主线程调用arriveAndAwaitAdvance()之前，这些线程不会继续执行(与之前的情况相同)：

```java
executorService.submit(new LongRunningAction("thread-4", phaser));
executorService.submit(new LongRunningAction("thread-5", phaser));
phaser.arriveAndAwaitAdvance();

assertEquals(2, phaser.getPhase());

ph.arriveAndDeregister();
```

之后，getPhase()方法将返回等于2的阶段号。当程序的所有阶段已经执行完毕，我们想要完成我们的程序时，我们需要调用arriveAndDeregister()方法，因为主线程仍然在Phaser中注册。当注销导致注册方的数量变为零时，Phaser终止。对同步方法的所有调用将不再阻塞，并将立即返回。

运行该程序将产生以下输出：

```shell
This is phase 0
This is phase 0
This is phase 0
Thread thread-3 before long running action
Thread thread-2 before long running action
Thread thread-1 before long running action
This is phase 1
This is phase 1
Thread thread-5 before long running action
Thread thread-4 before long running action
```

我们看到所有线程都在等待执行，直到屏障打开。只有当前一个阶段成功完成时，才会执行下一阶段的执行。

## 4. 总结

在本教程中，我们了解了来自java.util.concurrent包中的Phaser构造，并使用Phaser类实现了具有多个阶段的协调逻辑。