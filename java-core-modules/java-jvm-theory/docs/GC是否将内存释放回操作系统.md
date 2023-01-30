## 1. 概述

[垃圾收集器 (GC)](https://www.baeldung.com/jvm-garbage-collectors)处理Java 中的内存管理。因此，程序员不需要显式处理内存分配和释放。

在Java中，JVM一开始就预留了一定的内存。有时，实际使用的内存明显少于保留量。在这种情况下，我们更愿意将多余的内存返回给操作系统。

整个过程取决于[用于垃圾收集的算法](https://www.baeldung.com/java-choosing-gc-algorithm)。因此，我们可以根据所需的行为选择 GC 和 JVM 的类型。

在本教程中，我们将探讨 GC 的内存管理及其与操作系统的交互。

## 2. JVM内存组织

JVM在初始化时，会在其内部创建不同类型的内存区域，如[Heap区、Stack区、](https://www.baeldung.com/java-stack-heap) Method Area、PC Registers、Native Method Stack等。

GC 处理堆存储。因此，我们将在本文中关注与堆相关的内存交互。

[我们可以分别使用标志](https://www.baeldung.com/jvm-parameters)-Xms 和 -Xmx指定初始和最大堆大小。如果-Xms 小于-Xmx，则表示JVM 一开始并没有将所有保留内存提交到堆中。简而言之，堆大小从 -Xms 开始，可以扩展到 -Xmx。这允许开发人员配置所需堆内存的大小。

现在，当应用程序运行时，不同的对象在堆内分配内存。在垃圾收集时，GC 释放未引用的对象并释放内存。这个释放的内存目前是堆本身的一部分，因为它是一个 CPU 密集型过程，在每次释放后与操作系统交互。

对象以分散的方式驻留在堆中。GC 需要压缩内存并创建一个空闲块返回给 OS。它涉及在返回内存时执行额外的进程。此外，Java 应用程序可能在稍后阶段需要额外的内存。为此， 我们需要再次与操作系统通信，申请更多的内存。此外，我们无法确保在请求的时间操作系统中内存的可用性。因此，使用内部堆而不是频繁调用操作系统来获取内存是一种更安全的方法。

但是，如果我们的应用程序不需要整个堆内存，我们只是阻塞可用资源，这些资源可能已被操作系统用于其他应用程序。考虑到这一点，JVM 引入了高效和自动化的内存释放技术。

## 3.垃圾收集器

在不同的发行版本中，Java 引入了不同类型的[GC](https://www.baeldung.com/jvm-garbage-collectors)。堆和操作系统之间的内存交互依赖于 JVM 和 GC 实现。一些 GC 实现积极支持堆收缩。堆收缩是将多余内存从堆中释放回操作系统以优化资源使用的过程。

例如，并行 GC 不会轻易将未使用的内存释放回操作系统。另一方面，一些GC会分析内存消耗，并据此决定从堆中释放一些空闲内存。G1、Serial、[Shenandoah 和 Z GC](https://www.baeldung.com/jvm-experimental-garbage-collectors)支持堆缩小。

现在让我们探讨这些过程。

### 3.1. 垃圾优先 (G1) GC

自 Java 9 以来，G1 一直是默认的 GC。它支持没有长时间停顿的压缩过程。它使用内部自适应优化算法，根据应用程序使用情况分析所需的 RAM，并在需要时取消提交内存。

初始实现支持在完整 GC 之后或并发循环事件期间缩小堆。然而，在理想情况下，我们希望及时将未使用的内存归还给操作系统，尤其是在我们的应用程序空闲期间。我们希望 GC 在运行时动态适应我们的应用程序的内存使用情况。

Java 在不同的 GC 中包含了这样的能力。对于 G1，[JEP 346](https://openjdk.java.net/jeps/346)引入了这些变化。从 Java 12 及更高版本开始，堆收缩也可以在并发[评论阶段进行](http://hg.openjdk.java.net/jdk/jdk/rev/08041b0d7c08)。G1 尝试在应用程序空闲时分析堆使用情况，并根据需要触发定期垃圾收集。G1 可以根据G1PeriodicGCInvokesConcurrent选项启动并发循环或完整 GC。循环执行后，G1 需要调整堆大小并将释放的内存返回给 OS。

### 3.2. 串行GC

串行 GC 还支持堆收缩行为。与 G1 相比，它需要额外的四个完整的 GC 周期来取消提交释放的内存。

### 3.3. 中关村

[ZGC](https://www.baeldung.com/jvm-zgc-garbage-collector)是在 Java 11 中引入的。它还通过在[JEP 351](https://openjdk.java.net/jeps/351)中将未使用的内存返回给操作系统的功能得到了增强。

### 3.4. 雪兰多GC

[Shenandoah](https://wiki.openjdk.java.net/display/shenandoah/Main)是并发 GC。它异步执行垃圾收集。消除对完整 GC 的需求极大地有助于应用程序的性能优化。

## 4. 使用 JVM 标志

我们之前已经看到我们可以使用 JVM 命令行选项指定堆大小。同样，我们可以使用不同的标志来配置 GC 的默认堆收缩行为：

-   -XX:GCTimeRatio：指定应用程序执行和 GC 执行之间所需的时间间隔。我们可以用它来让 GC 运行更长时间
-   -XX:MinHeapFreeRatio：指定垃圾回收后堆中空闲空间的最小预期比例
-   -XX:MaxHeapFreeRatio：指定垃圾回收后堆中空闲空间的最大预期比例

如果堆中的可用空闲空间高于使用-XX:MaxHeapFreeRatio选项指定的比率，则 GC 可以将未使用的内存返回给操作系统。我们可以配置上述标志的值来限制堆中未使用的内存量。我们为并发垃圾收集过程提供了类似的选项：

-   -XX:InitiatingHeapOccupancyPercent：指定启动并发垃圾回收所需的堆占用百分比。
-   -XX:-ShrinkHeapInSteps：立即将堆大小减小到-XX:MaxHeapFreeRatio值。默认实现需要此过程的多个垃圾收集周期。

## 5.总结

在本文中，我们已经看到 Java 提供了不同类型的 GC 来满足不同的需求。GC 可以回收空闲内存并将其返回给主机操作系统。我们可以根据需要选择 GC 的类型。

我们还探讨了使用 JVM 参数来调整 GC 行为以达到所需的性能水平。此外，我们可以选择通过 JVM 动态扩展内存利用率。我们应该考虑与我们的应用程序和所涉及资源的每个选择选项相关的权衡。