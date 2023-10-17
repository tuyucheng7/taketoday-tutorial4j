## 1. 概述

[在本教程中，我们将介绍Java 内存管理](https://www.baeldung.com/jvm-garbage-collectors)的基本问题以及不断寻找更好的方法来实现它的需要。这将主要介绍 Java 中引入的名为 Shenandoah 的新实验性垃圾收集器，以及它与其他垃圾收集器的比较。

## 2. 了解垃圾收集中的挑战

垃圾收集器是一种自动内存管理形式，其中像 JVM 这样的运行时管理在其上运行的用户程序的内存分配和回收。有几种算法可以实现垃圾收集器。这些包括引用计数、标记清除、标记压缩和。

### 2.1. 垃圾收集器的注意事项

根据我们用于垃圾收集的算法，它可以在用户程序挂起时运行，也可以与用户程序同时运行。前者以长时间暂停(也称为世界停止暂停)导致的高延迟为代价实现了更高的吞吐量。后者旨在获得更好的延迟，但会牺牲吞吐量。

事实上，大多数现代收集器都使用混合策略，他们同时应用停止世界和并发方法。它通常通过将堆空间划分为年轻代和老年代来工作。分代收集器然后在年轻代中使用停止世界收集并在老年代中使用并发收集，可能以增量方式减少暂停。

然而，真正的最佳选择是找到一个垃圾收集器，它运行时暂停最少并提供高吞吐量 ——所有这些都具有可预测的堆大小行为，可以从小到大不等！这是一场持续不断的斗争，它从早期就一直保持着 Java 垃圾回收创新的步伐。

### 2.2. Java 中现有的垃圾收集器

一些传统的[垃圾收集器](https://www.baeldung.com/jvm-garbage-collectors)包括串行和并行收集器。他们是分代收集器，在年轻一代使用，在老一代使用标记紧凑：

[![垃圾收集器串行并行 1](https://www.baeldung.com/wp-content/uploads/2021/01/Garbage-Collector-Serial-Parallel-1.jpg)](https://www.baeldung.com/wp-content/uploads/2021/01/Garbage-Collector-Serial-Parallel-1.jpg)

在提供良好的吞吐量的同时，它们也面临着长时间的 stop-the-world 停顿问题。

Java 1.4 中引入的[Concurrent Mark Sweep (CMS) 收集器](https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/cms.html)是分代的、并发的、低暂停收集器。它适用于年轻一代的和老一代的标记清除：

[![垃圾收集器 CMS 1](https://www.baeldung.com/wp-content/uploads/2021/01/Garbage-Collector-CMS-1.jpg)](https://www.baeldung.com/wp-content/uploads/2021/01/Garbage-Collector-CMS-1.jpg)

它试图通过与用户程序同时完成大部分工作来最小化暂停时间。尽管如此，它仍然存在导致不可预测的暂停的问题，需要更多的 CPU 时间，并且不适合大小大于 4 GB 的堆。

作为 CMS 的长期替代品，[垃圾优先(Garbage First，G1)收集器](https://docs.oracle.com/javase/9/gctuning/garbage-first-garbage-collector.htm)在 Java 7 中被引入。G1 是一个分代的、并行的、并发的、增量压缩的低暂停收集器。它适用于年轻一代的和老一代的标记紧凑：

[![垃圾收集器 G1-1](https://www.baeldung.com/wp-content/uploads/2021/01/Garbage-Collector-G1-1.jpg)](https://www.baeldung.com/wp-content/uploads/2021/01/Garbage-Collector-G1-1.jpg)

然而，G1 也是一个区域化的收集器，并将堆区域划分为更小的区域。这给它带来了更可预测的暂停的好处。针对具有大量内存的多处理器机器，G1也没有摆脱暂停。

因此，寻找更好的垃圾收集器的竞赛仍在继续，尤其是进一步减少暂停时间的垃圾收集器。JVM 最近推出了一系列实验性收集器，如 Z、Epsilon 和 Shenandoah。除此之外，G1 继续得到更多改进。

目标实际上是尽可能接近无暂停的 Java！

## 3. Shenandoah 垃圾收集器

[Shenandoah](https://wiki.openjdk.java.net/display/shenandoah/Main)是Java 12 中引入的实验性收集器，定位为延迟专家。它试图通过与用户程序同时执行更多的垃圾收集工作来减少暂停时间。

例如，Shenendoah 尝试同时执行对象重定位和压缩。这实质上意味着 Shenandoah 中的暂停时间不再与堆大小成正比。因此，无论堆大小如何，它都可以提供一致的低暂停行为。

### 3.1. 堆结构

Shenandoah 和 G1 一样，是一个区域化的收集器。这意味着它将堆区域划分为大小相等的区域集合。区域基本上是内存分配或回收的单位：

[![垃圾收集器 Shenandoah 堆结构](https://www.baeldung.com/wp-content/uploads/2021/01/Garbage-Collector-Shenandoah-Heap-Structure.jpg)](https://www.baeldung.com/wp-content/uploads/2021/01/Garbage-Collector-Shenandoah-Heap-Structure.jpg)

但是，与 G1 和其他分代收集器不同，Shenandoah 不将堆区域划分为分代。因此，它必须在每个周期标记大部分存活对象，而分代收集器可以避免这种情况。

### 3.2. 对象布局

在 Java 中，内存中的对象不仅包括数据字段——它们还携带一些额外的信息。这些额外信息包括标头(包含指向对象类的指针)和标记字。标记字有多种用途，如转发指针、年龄位、锁定和散列：

[![垃圾收集器 Shenandoah 对象布局](https://www.baeldung.com/wp-content/uploads/2021/01/Garbage-Collector-Shenandoah-Object-Layout.jpg)](https://www.baeldung.com/wp-content/uploads/2021/01/Garbage-Collector-Shenandoah-Object-Layout.jpg)

Shenandoah为这个对象布局添加了一个额外的词。这用作间接指针并允许 Shenandoah 移动对象而无需更新对它们的所有引用。这也称为布鲁克斯指针。

### 3.3. 障碍

在 stop-the-world 模式下执行收集周期更简单，但是当我们与用户程序同时执行时，复杂性就会飙升。它对并发标记和压缩等收集阶段提出了不同的挑战。

解决方案在于通过我们所说的屏障拦截所有堆访问。Shenandoah 和 G1 等其他并发收集器使用屏障来确保堆的一致性。然而，障碍是昂贵的操作并且通常倾向于降低收集器的吞吐量。

例如，对象的读写操作可能会被收集器使用屏障拦截：

[![垃圾收集器屏障](https://www.baeldung.com/wp-content/uploads/2021/01/Garbage-Collector-Barriers.jpg)](https://www.baeldung.com/wp-content/uploads/2021/01/Garbage-Collector-Barriers.jpg)

Shenandoah在不同阶段使用多个屏障，如 SATB 屏障、读屏障和写屏障。我们将在后面的部分中看到它们的用处。

### 3.4. 模式、启发式和失败模式

模式定义了 Shenandoah 运行的方式，比如它使用的障碍，它们还定义了它的性能特征。提供三种模式：normal/SATB、iu 和 passive。正常/SATB 模式是默认模式。

启发式确定收集应何时开始以及应包括哪些区域。这些包括自适应、静态、紧凑和激进，自适应作为默认启发式。例如，它可能会选择具有 60% 或更多垃圾的区域，并在 75% 的区域已分配时开始收集周期。

Shenandoah 需要比分配它的用户程序更快地收集堆。但是，有时它可能会落后，从而导致其中一种故障模式。这些失败模式包括步调、退化收集，以及在最坏情况下的完整收集。

## 4. Shenandoah 收集阶段

Shenandoah 的收集周期主要包括三个阶段：标记、疏散和更新引用。尽管这些阶段中的大部分工作与用户程序同时发生，但仍有一小部分必须在停止世界模式下发生。

### 4.1. 打标

标记是识别堆中所有对象或其中不可访问部分的过程。我们可以通过从根对象开始并遍历对象图来找到可达对象来做到这一点。在遍历时，我们还为每个对象分配三种颜色之一：白色、灰色或黑色：

[![垃圾收集器 Shenandoah 标记 1](https://www.baeldung.com/wp-content/uploads/2021/01/Garbage-Collector-Shenandoah-Marking-1.jpg)](https://www.baeldung.com/wp-content/uploads/2021/01/Garbage-Collector-Shenandoah-Marking-1.jpg)

在 stop-the-world 模式下标记更简单，但在并发模式下会变得复杂。这是因为用户程序在进行标记时同时改变了对象图。Shenandoah 通过使用开始快照 (SATB) 算法解决了这个问题。

这意味着在标记开始时处于活动状态或自标记开始以来已分配的任何对象都被视为活动对象。Shenandoah使用 SATB 屏障来维护堆的 SATB 视图。

虽然大部分标记是同时完成的，但仍有一些部分是在 stop-the-world 模式下完成的。在 stop-the-world 模式下发生的部分是扫描根集的初始标记和耗尽所有待处理队列并重新扫描根集的最终标记。final-mark 还准备了指示要疏散区域的集合集。

### 4.2. 清理和疏散

一旦标记完成，垃圾区域就可以被回收了。垃圾区域是不存在活动对象的区域。清理同时发生。

现在，下一步是将集合集中的活动对象移动到其他区域。这样做是为了减少内存分配中的碎片，因此也称为紧凑。疏散或压实完全同时发生。

现在，这就是 Shenandoah 不同于其他收藏家的地方。对象的并发重定位是棘手的，因为用户程序会继续读取和写入它们。Shenandoah 通过对对象的 Brooks 指针执行比较和交换操作以指向其空间版本来设法实现这一点：

[![垃圾收集器 Shenandoah 屏障](https://www.baeldung.com/wp-content/uploads/2021/01/Garbage-Collector-Shenandoah-Barriers.jpg)](https://www.baeldung.com/wp-content/uploads/2021/01/Garbage-Collector-Shenandoah-Barriers.jpg)

此外，Shenandoah使用读写屏障来确保在并发疏散期间保持严格的“to-space”不变量。这意味着读取和写入必须从保证在疏散中幸存下来的空间发生。

### 4.3. 参考更新

收集周期中的这个阶段是遍历堆并更新对在疏散期间移动的对象的引用：

[![垃圾收集器 Shenandoah 更新参考](https://www.baeldung.com/wp-content/uploads/2021/01/Garbage-Collector-Shenandoah-Update-Refs.jpg)](https://www.baeldung.com/wp-content/uploads/2021/01/Garbage-Collector-Shenandoah-Update-Refs.jpg)

同样，更新参考阶段主要是同时完成的。有短暂的 init-update-refs 初始化更新参考阶段和 final-update-refs 重新更新根集并从收集集中回收区域。只有这些需要停止世界模式。

## 5. 与其他实验收藏家的比较

Shenandoah 并不是最近在 Java 中引入的唯一实验性垃圾收集器。其他包括 Z 和 Epsilon。让我们了解他们如何与雪兰多进行比较。

### 5.1. Z收藏家

[Z 收集器](https://www.baeldung.com/jvm-zgc-garbage-collector)在 Java 11 中引入，是单代低延迟收集器，专为非常大的堆大小而设计——我们正在谈论数 TB 的领域。Z 收集器与用户程序并发完成大部分工作，并利用堆引用的加载屏障。

此外，Z 收集器通过称为指针着色的技术利用 64 位指针。在这里，彩色指针存储有关堆上对象的额外信息。Z 收集器使用存储在指针中的额外信息重新映射对象以减少内存碎片。

从广义上讲，Z 收集器的目标与 Shenandoah 的目标相似。它们都旨在实现与堆大小不成正比的低暂停时间。但是，与 Z 收集器相比，Shenandoah 有更多可用的调整选项。

### 5.2. 厄普西隆收集器

[同样在 Java 11 中引入的Epsilon](https://www.baeldung.com/jvm-epsilon-gc-garbage-collector)具有非常不同的垃圾收集方法。它基本上是一个被动或“无操作”收集器，这意味着它处理内存分配但不回收它！因此，当堆内存用完时，JVM 会简单地关闭。

但是我们为什么要使用这样的收集器呢？基本上，任何垃圾收集器都会间接影响用户程序的性能。很难对应用程序进行基准测试并了解垃圾收集对其的影响。

Epsilon 正是为这个目的服务的。它只是消除了垃圾收集器的影响，让我们独立运行应用程序。但是，这要求我们对应用程序的内存要求有一个非常清楚的了解。因此，我们可以从应用程序中获得更好的性能。

显然，Epsilon 的目标与 Shenandoah 的目标截然不同。

## 六，总结

在本文中，我们了解了 Java 中垃圾收集的基础知识以及不断改进它的必要性。我们详细讨论了 Java 中引入的最新实验性收集器——Shenandoah。我们还了解了它与 Java 中可用的其他实验性收集器的对比情况。

追求通用垃圾收集器不会很快实现！因此，虽然 G1 仍然是默认收集器，但这些新增功能为我们提供了在低延迟情况下使用 Java 的选项。但是，我们不应该将它们视为其他高吞吐量收集器的直接替代品。