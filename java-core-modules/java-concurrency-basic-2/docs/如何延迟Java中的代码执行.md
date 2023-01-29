## 1. 概述

Java程序在其操作中添加延迟或暂停是比较常见的，这对于任务调整或暂停执行直到另一个任务完成非常有用。

本教程将介绍在Java中实现延迟的两种方法。

## 2. 基于Thread的方法

**当Java程序运行时，它会生成一个在主机上运行的进程**。该进程**至少包含一个线程，即程序运行的主(main)线程**。此外，Java支持[多线程](https://www.baeldung.com/java-thread-lifecycle)，这使应用程序能够创建与主线程并行或异步运行的新线程。

### 2.1 使用Thread.sleep()

在Java中，一种快速而肮脏的暂停方式是告诉当前线程休眠指定的时间，这可以使用Thread.sleep(milliseconds)来实现：

```java
try {
    Thread.sleep(secondsToSleep * 1000);
} catch (InterruptedException ie) {
    Thread.currentThread().interrupt();
}
```

**最好将sleep()方法包装在try/catch块中，以防另一个线程中断休眠线程**。在这种情况下，我们捕获InterruptedException并显式中断当前线程，以便稍后可以捕获并处理它。这在多线程程序中更为重要，但在单线程程序中仍然是很好的做法，以防我们以后添加其他线程。

### 2.2 使用TimeUnit.sleep()

**为了更好的可读性，我们可以使用TimeUnit.XXX.sleep(y)，其中XXX是睡眠的时间单位(秒、分钟等)，y是睡眠的时间**。该方法背后使用Thread.sleep()，下面是一个TimeUnit语法的示例：

```java
try {
    TimeUnit.SECONDS.sleep(secondsToSleep);
} catch (InterruptedException ie) {
    Thread.currentThread().interrupt();
}
```

然而，**使用这些基于Thread的方法有一些缺点**：

+ 睡眠时间并不完全精确，尤其是当使用毫秒或纳秒等较小的时间增量时
+ 当在循环内部使用时，由于其他代码的执行，睡眠会在循环迭代之间轻微漂移，因此在多次迭代之后，执行时间可能会变得越来越不精确

## 3. 基于ExecutorService的方法

Java提供了[ScheduledExecutorService](https://www.baeldung.com/java-executor-service-tutorial)接口，这是一个更健壮、更精确的解决方案。此接口可以安排代码在指定延迟后或在固定时间间隔运行一次。

要在给定延迟后运行一段代码，我们可以使用schedule()方法：

```java
ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
executorService.schedule(Classname::someTask, delayInSeconds, TimeUnit.SECONDS);
```

Classname::someTask部分是指定延迟后将运行的方法：

+ someTask是我们想要执行的方法的名称
+ Classname是包含someTask方法的类的名称

要以固定的时间间隔运行任务，我们可以使用scheduleAtFixedRate()方法：

```java
ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
executorService.scheduleAtFixedRate(Classname::someTask, 0, delayInSeconds, TimeUnit.SECONDS);
```

这会重复调用someTask()方法，在每次调用之间暂停delayInSeconds。

除了允许更多的定时选项外，ScheduledExecutorService方法还可以生成更精确的时间间隔，因此它可以防止出现漂移问题。

## 4. 总结

在本文中，我们讨论了在Java程序中创建延迟的两种方法。