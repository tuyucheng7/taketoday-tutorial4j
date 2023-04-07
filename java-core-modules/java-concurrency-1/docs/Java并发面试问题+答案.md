## 1. 概述

Java中的并发性是技术面试中提出的最复杂、最高级的主题之一。本文提供了你可能会遇到的关于该主题的一些面试问题的答案。

### Q1. 进程和线程有什么区别？

进程和线程都是并发单元，但它们有一个根本区别：进程不共享公共内存，而线程共享。

从操作系统的角度来看，进程是一个独立的软件，运行在自己的虚拟内存空间中。任何多任务操作系统(这意味着几乎所有现代操作系统)都必须在内存中分离进程，这样一个失败的进程就不会通过扰乱公共内存来拖累所有其他进程。

因此，进程通常是隔离的，它们通过进程间通信的方式进行协作，进程间通信被操作系统定义为一种中间API。

相反，线程是应用程序的一部分，它与同一应用程序的其他线程共享公共内存。使用公共内存可以减少大量开销，设计线程以更快地协作并在它们之间交换数据。

### Q2. 如何创建线程实例并运行它？

要创建线程实例，你有两种选择。首先，将一个Runnable实例传递给它的构造函数并调用start()。Runnable是一个函数式接口，因此它可以作为Lambda表达式传递：

```java
Thread thread1 = new Thread(() ->
  System.out.println("Hello World from Runnable!"));
thread1.start();
```

Thread也实现了Runnable，所以另一种启动线程的方法是创建一个匿名子类，覆盖它的run()方法，然后调用start()：

```java
Thread thread2 = new Thread() {
    @Override
    public void run() {
        System.out.println("Hello World from subclass!");
    }
};
thread2.start();
```

### Q3. 描述线程的不同状态以及状态转换何时发生。

可以使用Thread.getState()方法检查线程的状态。Thread.State枚举中描述了Thread的不同状态。他们是：

-   **NEW**：尚未通过Thread.start()启动的新Thread实例
-   **RUNNABLE**：一个正在运行的线程。之所以称为可运行，是因为在任何给定时间它都可能正在运行或等待来自线程调度程序的下一个时间段。当在其上调用Thread.start()时，新线程进入RUNNABLE状态
-   **BLOCKED**：如果一个正在运行的线程需要进入同步部分但由于另一个线程持有该部分的监视器而无法进入，则该线程将被阻塞
-   **WAITING**：如果一个线程等待另一个线程执行特定操作，则该线程进入此状态。例如，一个线程在调用它持有的监视器上的Object.wait()方法或另一个线程上的Thread.join()方法时进入此状态
-   **TIMED_WAITING**：与上面相同，但线程在调用Thread.sleep()、Object.wait()、Thread.join()和一些其他方法
-   **TERMINATED**：线程已完成其Runnable.run()方法的执行并终止

### Q4. Runnable和Callable接口有什么区别？它们是如何使用的？

Runnable接口只有一个运行方法。它表示必须在单独的线程中运行的计算单元。Runnable接口不允许此方法返回值或抛出未经检查的异常。

Callable接口有一个调用方法，代表一个有值的任务。这就是call方法返回一个值的原因。它还可以抛出异常。Callable一般用在ExecutorService实例中，启动一个异步任务，然后调用返回的Future实例获取其值。

### Q5. 什么是守护线程，它的用例是什么？如何创建守护线程？

守护线程是不会阻止JVM退出的线程。当所有非守护线程都终止时，JVM会简单地放弃所有剩余的守护线程。守护线程通常用于为其他线程执行一些支持或服务任务，但你应该考虑到它们随时可能被放弃。

要将线程作为守护进程启动，你应该在调用start()之前使用setDaemon()方法：

```java
Thread daemon = new Thread(() -> System.out.println("Hello from daemon!"));
daemon.setDaemon(true);
daemon.start();
```

奇怪的是，如果你将其作为main()方法的一部分运行，则可能不会打印消息。如果main()线程在守护程序到达打印消息点之前终止，则可能会发生这种情况。你通常不应该在守护线程中执行任何I/O，因为它们甚至无法执行它们的finally块并在被放弃时关闭资源。

### Q6. 什么是线程的中断标志？你如何设置和检查它？它与中断异常有何关系？

中断标志或中断状态是线程被中断时设置的内部线程标志。要设置它，只需在线程对象上调用thread.interrupt()即可。

如果线程当前位于抛出InterruptedException的方法之一(wait、join、sleep等)中，则此方法会立即抛出InterruptedException。线程可以根据自己的逻辑自由处理这个异常。

如果线程不在此类方法内并且调用了 thread.interrupt()，则不会发生任何特殊情况。线程有责任使用静态Thread.interrupted()或实例isInterrupted()方法定期检查中断状态。这些方法之间的区别在于静态Thread.interrupted()会清除中断标志，而isInterrupted()不会。

### Q7. 什么是Executor和ExecutorService？这些接口之间有什么区别？

Executor和ExecutorService是java.util.concurrent框架的两个相关接口。Executor是一个非常简单的接口，只有一个execute方法接收Runnable实例来执行。在大多数情况下，这是你的任务执行代码应该依赖的接口。

ExecutorService使用多种方法扩展Executor接口，用于处理和检查并发任务执行服务的生命周期(在关闭的情况下终止任务)以及用于更复杂的异步任务处理的方法，包括Futures。

有关使用Executor和ExecutorService的更多信息，请参阅文章[Java ExecutorService指南](https://www.baeldung.com/java-executor-service-tutorial)。

### Q8. 标准库中ExecutorService的可用实现有哪些？

ExecutorService接口具有三个标准实现：

-   **ThreadPoolExecutor**：用于使用线程池执行任务。线程完成任务后，它会返回池中。如果池中的所有线程都忙，则任务必须等待轮到它。
-   **ScheduledThreadPoolExecutor**：允许安排任务执行而不是在线程可用时立即运行它。它还可以安排具有固定速率或固定延迟的任务。
-   **ForkJoinPool**：是一个特殊的ExecutorService，用于处理递归算法任务。如果你将常规ThreadPoolExecutor用于递归算法，你会很快发现所有线程都在忙于等待较低级别的递归完成。ForkJoinPool实现了所谓的工作窃取算法，使其能够更有效地使用可用线程。

### Q9. 什么是Java内存模型(JMM)？描述其目的和基本思想。

Java内存模型是[第17.4章](https://docs.oracle.com/javase/specs/jls/se8/html/jls-17.html#jls-17.4)中描述的Java语言规范的一部分。它指定多个线程如何访问并发Java应用程序中的公共内存，以及一个线程的数据更改如何对其他线程可见。虽然非常简短，但如果没有强大的数学背景，JMM可能很难掌握。

对内存模型的需求源于这样一个事实，即你的Java代码访问数据的方式并不是它在较低级别上实际发生的方式。内存写入和读取可能会被Java编译器、JIT编译器甚至CPU重新排序或优化，只要这些读取和写入的可观察结果是相同的。

当你的应用程序扩展到多个线程时，这可能会导致违反直觉的结果，因为大多数这些优化都考虑了单个执行线程(跨线程优化器仍然非常难以实现)。另一个巨大的问题是现代系统中的内存是多层的：处理器的多个内核可能会在其缓存或读/写缓冲区中保留一些未刷新的数据，这也会影响从其他内核观察到的内存状态。

更糟糕的是，不同内存访问架构的存在将打破Java“一次编写，到处运行”的承诺。令程序员高兴的是，JMM指定了一些你在设计多线程应用程序时可能依赖的保证。坚持这些保证有助于程序员编写在各种体系结构之间稳定且可移植的多线程代码。

JMM的主要概念是：

-   **Actions**，这些是可以由一个线程执行并由另一个线程检测到的线程间操作，如读取或写入变量、锁定/解锁监视器等
-   **Synchronization actions**，动作的特定子集，如读/写volatile变量，或锁定/解锁监视器
-   **Program Order**(PO)，单个线程内可观察到的总操作顺序
-   **Synchronization Order**(SO)，所有同步动作之间的总顺序-它必须与Program Order一致，即如果两个同步动作在PO中先于另一个，则它们在SO中以相同的顺序发生
-   **synchronizes-with**(SW)某些同步操作之间的关系，例如监视器的解锁和同一监视器的锁定(在另一个或同一线程中)
-   **Happens-before Order**-结合PO和SW(这在集合论中称为传递闭包)来创建线程间所有动作的部分排序。如果一个动作发生在另一个动作之前，那么第二个动作可以观察到第一个动作的结果(例如，在一个线程中写入变量并在另一个线程中读取)
-   **Happens-before consistency**-如果每次读取都观察到按照发生前顺序对该位置的最后一次写入，或者通过数据竞争进行的一些其他写入，则一组操作是HB一致的
-   **Execution**-一组特定的有序动作和它们之间的一致性规则

对于给定的程序，我们可以观察到具有不同结果的多个不同的执行。但是如果一个程序是正确同步的，那么它的所有执行看起来都是顺序一致的，这意味着你可以将多线程程序推理为一组以某种顺序发生的动作。这为你省去了考虑底层重新排序、优化或数据缓存的麻烦。

### Q10. 什么是Volatile字段，JMM对此类字段有什么保证？

根据Java内存模型(请参阅Q9)，volatile字段具有特殊属性。volatile变量的读取和写入是同步操作，这意味着它们具有总顺序(所有线程将遵守这些操作的一致顺序)。根据此顺序，读取volatile变量可以保证观察到对该变量的最后一次写入。

如果你有一个从多个线程访问的字段，并且至少有一个线程写入它，那么你应该考虑将其设置为volatile，否则对于某个线程将从该字段读取的内容有一点保证。

volatile的另一个保证是写入和读取64位值(long和double)的原子性。如果没有volatile修饰符，读取此类字段可能会观察到由另一个线程部分写入的值。

### Q11. 以下哪些操作是原子操作？

-   写入非volatile int；
-   写入volatile int；
-   写入非volatile long；
-   写入volatile long；
-   自增volatile long？

对int(32位)变量的写入保证是原子的，无论它是否volatile。long(64位)变量可以分两个单独的步骤编写，例如，在32位体系结构上，因此默认情况下，没有原子性保证。但是，如果你指定volatile修饰符，则可以保证以原子方式访问long变量。

自增操作通常分多个步骤完成(检索值、更改值和写回值)，因此永远不能保证它是原子的，无论变量是否为volatile。如果你需要实现一个值的原子自增，你应该使用AtomicInteger，AtomicLong等类。

### Q12. JMM对类的最终字段有什么特殊保证？

JVM基本上保证在任何线程获取对象之前初始化类的最终字段。如果没有这种保证，由于重新排序或其他优化，在该对象的所有字段都被初始化之前，对对象的引用可能会被发布，即变得对另一个线程可见。这可能会导致对这些字段的不正当访问。

这就是为什么在创建不可变对象时，你应该始终将其所有字段设置为final，即使它们无法通过getter方法访问。

### Q13. 方法定义中的synchronized关键字是什么意思？静态方法？在块之前？

块前的synchronized关键字表示任何进入该块的线程都必须获取monitor(括号中的对象)。如果监视器已经被另一个线程获取，则前一个线程将进入BLOCKED状态，等待直到监视器被释放。

```java
synchronized(object) {
    // ...
}
```

同步实例方法具有相同的语义，但实例本身充当监视器。

```java
synchronized void instanceMethod() {
    // ...
}
```

对于静态同步方法，监视器是表示声明类的Class对象。

```java
static synchronized void staticMethod() {
    // ...
}
```

### Q14. 如果两个线程同时调用不同对象实例上的同步方法，这些线程中的一个是否会阻塞？如果方法是静态的怎么办？

如果该方法是实例方法，则该实例充当该方法的监视器。在不同实例上调用该方法的两个线程获取不同的监视器，因此它们都不会被阻塞。

如果方法是静态的，那么监视器就是Class对象。对于两个线程，监视器是相同的，因此其中一个可能会阻塞并等待另一个退出同步方法。

### Q15. Object类的Wait、Notify和Notifyall方法的目的是什么？

拥有对象监视器的线程(例如，进入对象保护的同步部分的线程)可以调用object.wait()来临时释放监视器并给其他线程获取监视器的机会。例如，可以这样做以等待特定条件。

当另一个获得监视器的线程满足条件时，它可能会调用object.notify()或object.notifyAll()并释放监视器。notify方法唤醒处于等待状态的单个线程，notifyAll方法唤醒所有等待这个监视器的线程，它们都竞争重新获取锁。

以下BlockingQueue实现显示了多个线程如何通过等待通知模式协同工作。如果我们将一个元素放入一个空队列中，所有在take方法中等待的线程都会醒来并尝试接收该值。如果我们将一个元素放入一个已满的队列中，put方法会等待get方法的调用。get方法删除一个元素，并通知在put方法中等待的线程队列中有一个空位可以放置新项目。

```java
public class BlockingQueue<T> {

	private List<T> queue = new LinkedList<T>();

	private int limit = 10;

	public synchronized void put(T item) {
		while (queue.size() == limit) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		if (queue.isEmpty()) {
			notifyAll();
		}
		queue.add(item);
	}

	public synchronized T take() throws InterruptedException {
		while (queue.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		if (queue.size() == limit) {
			notifyAll();
		}
		return queue.remove(0);
	}
}
```

### Q16. 描述死锁、活锁和饥饿的条件。描述这些情况的可能原因。

**死锁**是一组线程中无法取得进展的情况，因为该组中的每个线程都必须获取一些已被该组中的另一个线程获取的资源。最简单的情况是当两个线程需要同时锁定两个资源以进行时，第一个资源已经被一个线程锁定，第二个资源被另一个线程锁定。这些线程永远不会获得对这两个资源的锁定，因此永远不会进行。

**活锁**是多个线程对自己生成的条件或事件做出反应的情况。一个事件发生在一个线程中，必须由另一个线程处理。在此处理过程中，会发生一个新事件，该事件必须在第一个线程中处理，依此类推。这样的线程是活跃的，没有被阻塞，但仍然没有取得任何进展，因为它们用无用的工作压倒了彼此。

**饥饿**是线程无法获取资源的情况，因为其他线程(或多个线程)占用它的时间太长或具有更高的优先级。线程无法取得进展，因此无法完成有用的工作。

### Q17. 描述Fork/Join框架的目的和用例。

fork/join框架允许并行化递归算法。使用类似ThreadPoolExecutor的并行递归的主要问题是你可能会很快用完线程，因为每个递归步骤都需要自己的线程，而堆栈上的线程将处于空闲状态并等待。

fork/join框架入口点是ForkJoinPool类，它是ExecutorService的一个实现。它实现了工作窃取算法，其中空闲线程尝试从繁忙线程“窃取”工作。这允许在不同线程之间传播计算并在使用比通常线程池所需的线程更少的线程的同时取得进展。

有关fork/join框架的更多信息和代码示例，请参阅文章[Java中的Fork/Join框架指南](https://www.baeldung.com/java-fork-join)。