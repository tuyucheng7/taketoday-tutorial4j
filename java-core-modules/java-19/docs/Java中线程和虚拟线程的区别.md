## 1. 简介

在本教程中，我们将展示Java中的传统线程与[Project Loom](https://www.baeldung.com/openjdk-project-loom)中引入的虚拟线程之间的区别。

接下来，我们将分享虚拟线程的几个用例以及项目引入的API。

在我们开始之前，我们需要注意这个项目正在积极开发中。我们将在早期访问loom VM上运行示例：openjdk-15-loom+4-55_windows-x64_bin。

较新版本的构建可以自由更改和破坏当前的API。也就是说，API已经有了重大变化，因为以前使用的java.lang.Fiber类已被删除并替换为新的java.lang.VirtualThread类。

## 2. 线程与虚拟线程的高级概述

在高层次上，线程由操作系统管理和调度，而虚拟线程由虚拟机管理和调度。现在，要创建一个新的内核线程，我们必须进行系统调用，这是一个代价高昂的操作。

这就是为什么我们使用线程池而不是根据需要重新分配和释放线程的原因。接下来，如果我们想通过添加更多线程来扩展我们的应用程序，由于上下文切换和它们的内存占用，维护这些线程的成本可能会很高并影响处理时间。

然后，通常我们不想阻塞这些线程，这会导致使用非阻塞I/O API和异步API，这可能会使我们的代码混乱。

相反，虚拟线程由JVM管理。因此，它们的分配不需要系统调用，并且不受操作系统上下文切换的影响。此外，虚拟线程在载体线程上运行，载体线程是底层使用的实际内核线程。因此，由于我们摆脱了系统的上下文切换，我们可以生成更多这样的虚拟线程。

接下来，虚拟线程的一个关键属性是它们不会阻塞我们的载体线程。这样一来，阻塞一个虚拟线程就变成了一种成本更低的操作，因为JVM将调度另一个虚拟线程，而使载体线程不受阻塞。

最终，我们不需要接触NIO或异步API。这应该会产生更易读、更容易理解和调试的代码。然而，continuation可能会阻塞载体线程-具体来说，当线程调用本地方法并从那里执行阻塞操作时。

## 3. 新线程构建器API

在Loom中，我们在Thread类中获得了新的构建器API，以及几个工厂方法。让我们看看如何创建标准工厂和虚拟工厂并将它们用于我们的线程执行：

```java
Runnable printThread = () -> System.out.println(Thread.currentThread());
        
ThreadFactory virtualThreadFactory = Thread.builder().virtual().factory();
ThreadFactory kernelThreadFactory = Thread.builder().factory();

Thread virtualThread = virtualThreadFactory.newThread(printThread);
Thread kernelThread = kernelThreadFactory.newThread(printThread);

virtualThread.start();
kernelThread.start();
```

这是上面运行的输出：

```shell
Thread[Thread-0,5,main]
VirtualThread[<unnamed>,ForkJoinPool-1-worker-3,CarrierThreads]
```

这里，第一条语句是内核线程的标准toString输出。

现在，我们在输出中看到虚拟线程没有名称，它正在CarrierThreads线程组的Fork-Join池的工作线程上执行。

正如我们所见，无论底层实现如何，API都是相同的，这意味着我们可以轻松地在虚拟线程上运行现有代码。

此外，我们不需要学习新的API来使用它们。

## 4. 虚拟线程组合

它是一个continuation和一个scheduler，它们一起构成了一个虚拟线程。现在，我们的用户模式调度程序可以是Executor接口的任何实现。上面的例子告诉我们，默认情况下，我们在ForkJoinPool上运行。

现在，类似于内核线程-可以在CPU上执行，然后停放、重新安排回来，然后恢复执行，continuation是一个执行单元，可以启动，然后停放(让步)、重新安排回来，然后恢复它从停止的地方以相同的方式执行，并且仍然由JVM管理，而不是依赖于操作系统。

请注意，continuation是一个低级API，程序员应该使用更高级别的API(例如构建器API)来运行虚拟线程。

然而，为了展示它是如何在幕后工作的，现在我们将运行我们的实验continuation：

```java
var scope = new ContinuationScope("C1");
var c = new Continuation(scope, () -> {
    System.out.println("Start C1");
    Continuation.yield(scope);
    System.out.println("End C1");
});

while (!c.isDone()) {
    System.out.println("Start run()");
    c.run();
    System.out.println("End run()");
}
```

这是上面运行的输出：

```shell
Start run()
Start C1
End run()
Start run()
End C1
End run()
```

在这个例子中，我们运行了我们的continuation，并在某个时候决定停止处理。然后，一旦我们重新运行它，我们就会从它停止的地方继续。通过输出，我们看到run()方法被调用了两次，但是continuation开始了一次，然后在第二次运行时从它停止的地方继续执行。

这就是JVM处理阻塞操作的方式。一旦发生阻塞操作，continuation就会让步，使载体线程畅通无阻。

所以，发生的事情是我们的主线程在其调用堆栈上为run()方法创建了一个新的堆栈帧并继续执行。然后，在继续让步之后，JVM保存其执行的当前状态。

接下来，主线程继续执行，就好像run()方法返回并继续while循环一样。在第二次调用continuation的run方法后，JVM将主线程的状态恢复到continuation已经产生并完成执行的点。

## 5. 总结

在本文中，我们讨论了内核线程和虚拟线程之间的区别。接下来，我们展示了如何使用Project Loom中的新线程构建器API来运行虚拟线程。

最后，我们展示了continuation是什么以及它是如何工作的。我们可以通过检查[早期访问](https://jdk.java.net/loom/)VM来进一步探索Project Loom的状态。或者，我们可以探索更多已经标准化的[Java并发](https://www.baeldung.com/java-concurrency) API。