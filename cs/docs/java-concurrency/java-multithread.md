## 什么是多线程？

多线程意味着同一应用程序内有多个执行线程，线程就像执行应用程序的单独CPU。因此，多线程应用程序就像具有多个CPU同时执行代码的不同部分的应用程序。

![一个内部有两个线程执行的应用程序。](https://jenkov.com/images/java-concurrency/introduction-1.png)

但线程并不等于CPU。通常，单个CPU将在多个线程之间共享其执行时间，在给定的时间内在执行每个线程之间切换。还可以让应用程序的线程由不同的CPU执行。

![具有由不同线程执行的线程的多个应用程序。](https://jenkov.com/images/java-concurrency/introduction-2.png)

## 为什么要使用多线程？

人们在应用程序中使用多线程有几个原因，其中最常见的是：

-   更好地利用单个CPU
-   更好地利用多个CPU或CPU核心
-   在响应能力方面更好的用户体验
-   在公平性方面更好的用户体验

### 更好地利用单个CPU

最常见的原因之一是能够更好地利用计算机中的资源。例如，如果一个线程正在等待对通过网络发送的请求的响应，则另一个线程可以同时使用CPU来执行其他操作。

此外，如果计算机有多个CPU，或者CPU有多个执行核心，那么多线程还可以帮助你的应用程序利用这些额外的CPU核心。

### 更好地利用多个CPU或CPU内核

如果计算机包含多个CPU或CPU包含多个执行核心，则你的应用程序需要使用多个线程才能利用所有CPU或CPU核心。

单个线程最多只能利用单个CPU，正如我上面提到的，有时甚至不能完全利用单个CPU。

### 响应能力方面更好的用户体验

使用多线程的另一个原因是提供更好的用户体验。例如，如果你单击GUI中的按钮，这会导致通过网络发送请求，那么哪个线程执行该请求就很重要。

如果你使用同时更新GUI的同一线程，则当GUI线程等待请求响应时，用户可能会遇到“GUI挂起”的情况。

相反，这样的请求可以由后台线程执行，因此GUI线程可以同时自由地响应其他用户请求。

### 公平性方面更好的用户体验

第四个原因是在用户之间更公平地共享计算机资源。

例如，假设一台服务器接收来自客户端的请求，并且只有一个线程来执行这些请求。如果客户端发送的请求需要很长时间才能处理，则所有其他客户端的请求都必须等待，直到该请求完成。

通过让每个客户端的请求由其自己的线程执行，那么没有任何单个任务可以完全独占CPU。

## 多线程与多任务

过去，计算机只有一个CPU，一次只能执行一个程序。大多数小型计算机的功能不足以同时执行多个程序，因此没有尝试。

公平地说，许多大型机系统能够同时执行多个程序的时间比个人计算机要长很多年。

### 多任务处理

后来出现了多任务处理，这意味着计算机可以同时执行多个程序(也称为任务或进程)。但这并不是真正的“同时”。单个CPU在程序之间共享，操作系统会在正在运行的程序之间切换，在切换之前执行每个程序一段时间。

多任务处理给软件开发人员带来了新的挑战。程序不能再假设拥有所有可用的CPU时间，也不能假设拥有所有内存或任何其他计算机资源。一个“好公民”程序应该释放它不再使用的所有资源，以便其他程序可以使用它们。

### 多线程

后来出现了多线程，这意味着你可以在同一个程序中执行多个线程。执行线程可以被认为是执行程序的CPU，当多个线程执行同一个程序时，就像多个CPU在同一个程序中执行一样。

## 多线程很难

多线程是提高某些类型程序性能的好方法。然而，多线程比多任务更具挑战性。

线程在同一个程序中执行，因此同时读取和写入相同的内存，这可能会导致单线程程序中不会出现的错误。其中一些错误可能在单CPU计算机上看不到，因为两个线程永远不会真正“同时”执行。

但是，现代计算机配备了多核CPU，甚至还配备了多个CPU。这意味着单独的线程可以由单独的核心或CPU同时执行。

![多CPU计算机上的多线程](https://jenkov.com/images/java-concurrency/java-concurrency-tutorial-introduction-1.png)

如果一个线程读取某个内存位置，而另一个线程写入该内存位置，则第一个线程最终会读取什么值？旧值？第二个线程写入的值？或者是两者的混合值？或者，如果两个线程同时写入同一内存位置，完成后会留下什么值？第一个线程写入的值？第二个线程写入的值？或者混合写入两个值？

如果没有适当的预防措施，这些结果都可能发生。这种行为甚至是不可预测的，结果可能会不时发生变化。

因此，作为开发人员，了解如何采取正确的预防措施非常重要，这意味着学习控制线程如何访问内存、文件、数据库等共享资源。这是本Java并发教程讨论的主题之一。

## Java中的多线程和并发

Java是最早为开发人员提供多线程功能的语言之一，Java从一开始就具有多线程功能。因此，Java开发人员经常面临上述问题。

该线索主要关注Java中的多线程，但多线程中出现的一些问题与多任务和分布式系统中出现的问题类似。因此，在这条线索中也可能会提到多任务和分布式系统。因此使用“并发”一词而不是“多线程”。

## 并发模型

第一个Java并发模型假设在同一应用程序中执行的多个线程也将共享对象。这种类型的并发模型通常称为“共享状态并发模型”。许多并发语言结构和实用程序都是为了支持这种并发模型而设计的。

然而，自从第一本Java并发书籍编写以来，甚至自从Java 5并发实用程序发布以来，并发架构和设计领域发生了很多事情

共享状态并发模型会导致许多并发问题，这些问题很难优雅地解决。因此，一种称为“无共享”或“分离状态”的替代并发模型越来越受欢迎。在分离状态并发模型中，线程不共享任何对象或数据。这样就避免了很多共享状态并发模型的并发访问问题。

新的异步“分离状态”平台和工具包如Netty、Vert.x以及Play/Akka和Qbit已经出现。新的非阻塞并发算法已经发布，并且LMax Disrupter等新的非阻塞工具已添加到我们的工具包中。Java 7中的Fork/Join框架以及Java 8中的集合流API引入了新的函数式编程并行性。

随着所有这些新的发展，我是时候更新这个Java并发教程了。因此，本教程再次进行中。只要有时间编写新教程就会发布。

## Java并发学习指南

如果你是Java并发的新手，我建议你遵循下面的学习计划。你也可以在本页左侧的菜单中找到所有主题的链接。

一般并发和多线程理论：

-   [多线程的好处](https://jenkov.com/tutorials/java-concurrency/benefits.html)
-   [多线程成本](https://jenkov.com/tutorials/java-concurrency/costs.html)
-   [并发模型](https://jenkov.com/tutorials/java-concurrency/concurrency-models.html)
-   [同线程](https://jenkov.com/tutorials/java-concurrency/same-threading.html)
-   [并发与并行](https://jenkov.com/tutorials/java-concurrency/concurrency-vs-parallelism.html)

Java并发的基础知识：

-   [创建和启动Java线程](https://jenkov.com/tutorials/java-concurrency/creating-and-starting-threads.html)
-   [竞争条件和关键部分](https://jenkov.com/tutorials/java-concurrency/race-conditions-and-critical-sections.html)
-   [线程安全和共享资源](https://jenkov.com/tutorials/java-concurrency/thread-safety.html)
-   [线程安全和不变性](https://jenkov.com/tutorials/java-concurrency/thread-safety-and-immutability.html)
-   [Java内存模型](https://jenkov.com/tutorials/java-concurrency/java-memory-model.html)
-   [Java发生在保证之前](https://jenkov.com/tutorials/java-concurrency/java-happens-before-guarantee.html)
-   [Java同步块](https://jenkov.com/tutorials/java-concurrency/synchronized.html)
-   [Java易失性关键字](https://jenkov.com/tutorials/java-concurrency/volatile.html)
-   [Java并发中的CPU缓存一致性](https://jenkov.com/tutorials/java-concurrency/cache-coherence-in-java-concurrency.html)
-   [Java线程局部](https://jenkov.com/tutorials/java-concurrency/threadlocal.html)
-   [Java线程信号](https://jenkov.com/tutorials/java-concurrency/thread-signaling.html)

Java并发中的典型问题：

-   [僵局](https://jenkov.com/tutorials/java-concurrency/deadlock.html)
-   [预防死锁](https://jenkov.com/tutorials/java-concurrency/deadlock-prevention.html)
-   [饥饿与公平](https://jenkov.com/tutorials/java-concurrency/starvation-and-fairness.html)
-   [嵌套监视器锁定](https://jenkov.com/tutorials/java-concurrency/nested-monitor-lockout.html)
-   [滑倒情况](https://jenkov.com/tutorials/java-concurrency/slipped-conditions.html)
-   [虚假分享](https://jenkov.com/tutorials/java-concurrency/false-sharing.html)
-   [线程拥塞](https://jenkov.com/tutorials/java-concurrency/thread-congestion.html)

Java并发结构有助于解决上述问题：

-   [Java中的锁](https://jenkov.com/tutorials/java-concurrency/locks.html)
-   [Java中的读/写锁](https://jenkov.com/tutorials/java-concurrency/read-write-locks.html)
-   [再入锁定](https://jenkov.com/tutorials/java-concurrency/reentrance-lockout.html)
-   [信号量](https://jenkov.com/tutorials/java-concurrency/semaphores.html)
-   [阻塞队列](https://jenkov.com/tutorials/java-concurrency/blocking-queues.html)
-   [线程池](https://jenkov.com/tutorials/java-concurrency/thread-pools.html)
-   [比较和交换](https://jenkov.com/tutorials/java-concurrency/compare-and-swap.html)

Java并发实用程序(java.util.concurrent)：

-   [Java并发实用程序-java.util.concurrent](https://jenkov.com/java-util-concurrent/index.html)

更多主题：

-   [同步器的剖析](https://jenkov.com/tutorials/java-concurrency/anatomy-of-a-synchronizer.html)
-   [非阻塞算法](https://jenkov.com/tutorials/java-concurrency/non-blocking-algorithms.html)
-   [阿姆达尔定律](https://jenkov.com/tutorials/java-concurrency/amdahls-law.html)
-   [参考](https://jenkov.com/tutorials/java-concurrency/references.html)