## 操作系统无法提高平台线程的效率，但JDK将通过切断其线程与操作系统线程之间的一对一关系来更好地利用它们

现在Project Loom的[JEP 425](https://openjdk.java.net/jeps/425)正式预览了Java 19的虚拟线程，是时候仔细看看它们了：调度和内存管理；安装、卸载、捕获和固定；可观察性；以及你可以做什么以获得最佳的可扩展性。

在我进入虚拟线程之前，我需要重新审视经典线程，或者，我将从这里开始称呼它们，平台线程。

JDK将平台线程实现为操作系统(OS)线程的精简包装器，这是昂贵的，因此你不能拥有太多。事实上，在其他资源(如CPU或网络连接)耗尽之前，线程数常常成为限制因素。

换句话说，平台线程通常会将应用程序的吞吐量限制在远低于硬件可以支持的水平。

这就是虚拟线程的用武之地。

## 虚拟线程

操作系统无法提高平台线程的效率，但JDK可以通过切断其线程与操作系统线程之间的一对一关系来更好地利用它们。

虚拟线程是一个实例，java.lang.Thread它需要操作系统线程来完成CPU工作-但在等待其他资源时不占用操作系统线程。你会看到，当在虚拟线程中运行的代码调用JDK API中的阻塞I/O操作时，运行时会执行非阻塞操作系统调用并自动挂起虚拟线程，直到操作完成。

在此期间，其他虚拟线程可以在该OS线程上执行计算，因此它们有效地共享它。

至关重要的是，Java的虚拟线程产生的开销很小，因此可以有很多很多。

因此，正如操作系统通过将大量虚拟地址空间映射到有限数量的物理RAM来提供丰富内存的错觉一样，JDK 通过将许多虚拟线程映射到少量操作系统线程来提供丰富线程的错觉。

正如程序几乎不关心虚拟内存与物理内存一样，并发Java代码也很少关心它是在虚拟线程中运行还是在平台线程中运行。

你可以专注于编写简单明了、可能会阻塞的代码-运行时负责共享可用的操作系统线程，以将阻塞成本降低到接近于零。

虚拟线程支持线程局部变量、同步块和线程中断；Thread因此，使用并且currentThread不必更改的代码。事实上，这意味着现有的Java代码将很容易地在虚拟线程中运行，无需任何更改甚至重新编译！

一旦服务器框架提供了为每个传入请求启动新虚拟线程的选项，你需要做的就是更新框架和JDK，然后拨动开关。

## 速度、规模和结构

了解虚拟线程的用途和用途非常重要。

永远不要忘记虚拟线程不是更快的线程。虚拟线程不会神奇地每秒执行比平台线程更多的指令。

虚拟线程真正擅长的是*等待*。

由于虚拟线程不需要或阻塞操作系统线程，因此可能有数百万个虚拟线程可以耐心等待对文件系统、数据库或Web服务的请求完成。

通过最大限度地利用外部资源，虚拟线程提供了更大的规模，而不是更快的速度。换句话说，它们提高了吞吐量。

除了硬数字之外，虚拟线程还可以提高代码质量。

它们的廉价性为一种名为结构化并发的全新并发编程范例打开了大门，我在[Inside Java Newscast #17](https://www.youtube.com/watch?v=2J2tJm_iwk0&list=PLX8CzqL3ArzX8ZzPNjBgji7rznFFiOr58&index=7)中对此进行了介绍。

现在是解释虚拟线程如何工作的时候了。

## 调度和内存

操作系统调度OS线程，从而调度平台线程，但虚拟线程由JDK调度。JDK通过在称为mounting的进程中将虚拟线程分配给平台线程来间接执行此操作。JDK稍后取消分配平台线程；这称为卸载。

运行虚拟线程的平台线程称为其载体线程，从Java代码的角度来看，虚拟及其载体暂时共享操作系统线程这一事实是不可见的。例如，堆栈跟踪和线程局部变量是完全分开的。

然后将承载线程留给操作系统照常进行调度；就操作系统而言，载体线程只是一个平台线程。

为实现此过程，JDK使用专用的ForkJoinPool先进先出(FIFO)模式作为虚拟线程调度程序。(注意：这与并行流使用的公共池不同。)

默认情况下，JDK的调度程序使用与可用处理器内核一样多的平台线程，但可以使用系统属性调整该行为。

未安装的虚拟线程的堆栈帧去哪里了？它们作为堆栈块对象存储在堆上。

一些虚拟线程会有很深的调用堆栈(例如从Web框架调用的请求处理程序)，但它们生成的调用堆栈通常要浅得多(例如从文件中读取的方法)。

JDK可以通过将其所有帧从堆复制到堆栈来安装虚拟线程。当虚拟线程被卸载时，大部分帧都留在堆上并根据需要进行延迟复制。

因此，堆栈会随着应用程序的运行而增长和缩小。这对于使虚拟线程足够便宜以拥有如此多的线程并在它们之间频繁切换至关重要。未来的工作很有可能进一步降低内存需求。

## 阻塞和卸载

通常，当虚拟线程在I/O上阻塞时(例如，当它从套接字读取时)或当它调用JDK中的其他阻塞操作时(例如在taka 上BlockingQueue)，虚拟线程将卸载。

当阻塞操作准备好完成时(套接字接收到字节或队列可以分发一个元素)，该操作将虚拟线程提交回调度程序，调度程序将按FIFO顺序最终挂载它以恢复执行。

然而，尽管JDK增强建议中的先前工作，例如[JEP 353(重新实现遗留Socket API)](https://openjdk.java.net/jeps/353)和[JEP 373(重新实现遗留DatagramSocket API)](https://openjdk.java.net/jeps/373)，并非JDK中的所有阻塞操作都会卸载虚拟线程。相反，一些捕获载体线程和底层操作系统平台线程，从而阻止两者。

这种不幸的行为可能是由操作系统级别(影响许多文件系统操作)或JDK级别(例如调用Object.wait())的限制引起的。

操作系统线程的捕获通过临时向调度程序添加平台线程来补偿，因此偶尔会超过可用处理器的数量；可以使用系统属性指定最大值。

不幸的是，最初的虚拟线程提案还有一个不完善之处：当虚拟线程执行本地方法或外部函数时，或者它在同步块或方法内执行代码时，虚拟线程将被固定到其载体线程。固定线程不会在原本应该卸载的情况下卸载。

不过，在这种情况下，没有平台线程被添加到调度程序中，因为你可以采取一些措施来最大限度地减少固定的影响——稍后会详细介绍。

这意味着捕获操作和固定线程将重新引入等待某事完成的平台线程。这不会使应用程序不正确，但可能会阻碍其可伸缩性。

幸运的是，未来的工作可能会使同步非固定。并且，重构java.io包的内部结构并实现操作系统级别的 API(例如[io_uring](https://en.wikipedia.org/wiki/Io_uring)在Linux上)可能会减少捕获操作的数量。

## 虚拟线程可观察性

虚拟线程与用于观察、分析、故障排除和优化Java应用程序的现有工具完全集成。例如，[Java飞行记录器(JFR)](https://docs.oracle.com/en/java/javase/17/jfapi/why-use-jfr-api.html)可以在虚拟线程开始或结束、由于某种原因未启动或在固定时阻塞时发出事件。

要更突出地查看后一种情况，你可以通过系统属性配置运行时，以在线程被固定时阻塞时打印堆栈跟踪。堆栈跟踪突出显示导致固定的堆栈帧。

而且因为虚拟线程只是线程，所以调试器可以像通过平台线程一样逐步通过它们。当然，这些调试器用户界面可能需要更新以处理数百万个线程，否则你会得到一些非常小的滚动条！

虚拟线程自然地在层次结构中组织自己。但是，这种行为和它们的绝对数量使得传统线程转储的平面格式不合适，因此它们将坚持只转储平台线程。一种新的线程转储[jcmd](https://docs.oracle.com/en/java/javase/18/docs/specs/man/jcmd.html)将在纯文本和JSON中以有意义的方式将虚拟线程与平台线程一起呈现。

## 三个实用建议

我列表中的第一项：不要合并虚拟线程！

池只对昂贵的资源有意义，而虚拟线程并不昂贵。相反，只要你需要并发执行操作，就创建新的虚拟线程。你可以使用线程池来限制对某些资源的访问，例如对数据库的请求。不。相反，使用信号量来确保只有指定数量的线程正在访问该资源，如下所示：

[复制代码片段](https://blogs.oracle.com/javamagazine/post/java-loom-virtual-threads-platform-threads#copy)

复制到剪贴板

错误：无法复制

复制到剪贴板

错误：无法复制

```
// WITH THREAD POOL
private static final ExecutorService
  DB_POOL = Executors.newFixedThreadPool(16);

public <T> Future<T> queryDatabase(
    Callable<T> query) {
  // pool limits to 16 concurrent queries
  return DB_POOL.submit(query);
}


// WITH SEMAPHORE
private static final Semaphore
  DB_SEMAPHORE = new Semaphore(16);

public <T> T queryDatabase(
    Callable<T> query) throws Exception {
  // semaphore limits to 16 concurrent queries
  DB_SEMAPHORE.acquire();
  try {
    return query.call();
  } finally {
    DB_SEMAPHORE.release();
  }
}
```

接下来，为了使用虚拟线程实现良好的可伸缩性，通过修改经常运行并包含I/O操作的同步块和方法，尤其是长时间运行的块和方法，避免频繁和长期固定。在这种情况下，同步的一个很好的替代方法是ReentrantLock，如下所示：

[复制代码片段](https://blogs.oracle.com/javamagazine/post/java-loom-virtual-threads-platform-threads#copy)

复制到剪贴板

错误：无法复制

复制到剪贴板

错误：无法复制

```
// with synchronization (pinning 👎🏾):
// synchronized guarantees sequential access
public synchronized String accessResource() {
  return access();
}


// with ReentrantLock (not pinning 👍🏾):
private static final ReentrantLock
  LOCK = new ReentrantLock();

public String accessResource() {
  // lock guarantees sequential access
  LOCK.lock();
  try {
    return access();
  } finally {
    LOCK.unlock();
  }
}
```

最后，另一个在虚拟线程中正常工作但值得重新审视以获得更好的可伸缩性的方面是*线程局部变量*，包括常规变量和可继承变量。虚拟线程以与平台线程相同的方式支持线程局部行为，但由于虚拟线程可能非常多，因此只有在仔细考虑后才应使用线程局部行为。

事实上，作为Project Loom的一部分，模块中线程局部变量的许多使用java.base已被删除，以减少代码运行数百万线程时的内存占用。对于一些用例，目前正在[针对范围局部变量的 JEP草案](https://openjdk.java.net/jeps/8263012)中探索一个有趣的替代方案。