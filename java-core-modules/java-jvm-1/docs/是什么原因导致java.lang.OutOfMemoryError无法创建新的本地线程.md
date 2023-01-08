## 1. 概述

在本教程中，我们将讨论java.lang.OutOfMemoryError: unable to create new native thread错误的原因和可能的补救措施。

## 2. 理解问题

### 2.1. 问题原因

大多数Java应用程序本质上都是多线程的，由多个组件组成，执行特定的任务，并在不同的线程中执行。但是，底层操作系统 (OS)对Java应用程序可以创建的最大线程数施加了限制。

当JVM向底层操作系统请求新线程并且操作系统无法创建新的内核 线程(也称为操作系统或系统线程)时，JVM 会抛出无法创建新的[本机](https://www.baeldung.com/cs/os-kernel)线程错误。事件的顺序如下：

1.  在Java虚拟机 (JVM) 中运行的应用程序请求新线程
2.  JVM 本机代码向操作系统发送请求以创建新的内核线程
3.  操作系统尝试创建一个需要内存分配的新内核线程
4.  操作系统拒绝本机内存分配，因为
    -   请求的Java 进程已耗尽其内存地址空间
    -   操作系统已耗尽其[虚拟内存](https://www.baeldung.com/cs/virtual-memory)
5.  然后Java进程返回java.lang.OutOfMemoryError: unable to create new native thread错误

### 2.2. 线程分配模型

操作系统通常有两种类型的线程——用户线程(由Java应用程序创建的线程)和内核线程。用户线程在内核线程之上得到支持，内核线程由操作系统管理。

它们之间，存在三种共同的关系：

1.  多对一——许多用户线程映射到一个内核线程
2.  一对一——一个用户线程映射到一个内核线程
3.  多对多——许多用户线程多路复用到更少或相等数量的内核线程

## 3.重现错误

我们可以通过在连续循环中创建线程然后让线程等待来轻松重现此问题：

```java
while (true) {
  new Thread(() -> {
    try {
        TimeUnit.HOURS.sleep(1);     
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
  }).start();
}
```

由于我们在每个线程上坚持一个小时，同时不断创建新线程，我们将很快达到操作系统的最大线程数。

## 4.解决方案

解决此错误的一种方法是增加操作系统级别的线程限制配置。

但是，这不是理想的解决方案，因为OutOfMemoryError可能表示编程错误。让我们看看其他一些解决这个问题的方法。

### 4.1. 利用执行器服务框架

利用Java的[执行器服务框架](https://www.baeldung.com/java-executor-service-tutorial)进行线程管理可以在一定程度上解决这个问题。默认的执行器服务框架，或者自定义的执行器配置，可以控制线程的创建。

我们可以使用Executors#newFixedThreadPool方法来设置一次可以使用的最大线程数：

```java
ExecutorService executorService = Executors.newFixedThreadPool(5);

Runnable runnableTask = () -> {
  try {
    TimeUnit.HOURS.sleep(1);
  } catch (InterruptedException e) {
      // Handle Exception
  }
};

IntStream.rangeClosed(1, 10)
  .forEach(i -> executorService.submit(runnableTask));

assertThat(((ThreadPoolExecutor) executorService).getQueue().size(), is(equalTo(5)));
```

在上面的示例中，我们首先创建一个固定线程池，其中包含五个线程和一个让线程等待一小时的可运行任务。然后我们将十个这样的任务提交给线程池，并断言有五个任务正在执行程序服务队列中等待。

由于线程池有五个线程，因此它最多可以同时处理五个任务。

### 4.2. 捕获和分析线程转储

[捕获和分析线程转储](https://www.baeldung.com/java-thread-dump)对于了解线程的状态很有用。

让我们看一个示例线程转储，看看我们可以学到什么：

[![VisualVM线程转储](https://www.baeldung.com/wp-content/uploads/2020/06/VisualVMThreadDump.png)](https://www.baeldung.com/wp-content/uploads/2020/06/VisualVMThreadDump.png)

上面的线程快照来自JavaVisualVM，用于前面介绍的示例。此快照清楚地演示了连续的线程创建。

一旦我们确定有连续的线程创建，我们就可以捕获应用程序的线程转储来识别创建线程的源代码：

[![ThreadDump源代码](https://www.baeldung.com/wp-content/uploads/2020/06/ThreadDumpSourceCode.png)](https://www.baeldung.com/wp-content/uploads/2020/06/ThreadDumpSourceCode.png)

在上面的快照中，我们可以识别负责创建线程的代码。这为采取适当的措施提供了有用的见解。

## 5.总结

在本文中，我们了解了java.lang.OutOfMemoryError: unable to create new native thread错误，我们发现它是由Java 应用程序中过多的线程创建引起的。

我们通过将ExecutorService 框架和线程转储分析视为解决此问题的两种有用措施，探索了一些解决和分析错误的解决方案。