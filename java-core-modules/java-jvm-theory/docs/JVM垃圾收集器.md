## 1. 概述

在本快速教程中，我们将演示不同JVM 垃圾收集 (GC)实现的基础知识。然后我们将学习如何在我们的应用程序中启用特定类型的垃圾收集。

## 2. 垃圾回收简介

鉴于名称，垃圾收集似乎会处理从内存中查找和删除垃圾的问题。然而，实际上，垃圾收集会跟踪 JVM 堆空间中可用的每个对象，并删除未使用的对象。

基本上，GC的工作分为两个简单的步骤，称为标记和清除：

-   标记——这是垃圾收集器识别哪些内存正在使用，哪些没有使用的地方。
-   清除——此步骤删除在“标记”阶段识别的对象。

好处：

-   无需手动内存分配/释放处理，因为未使用的内存空间由GC自动处理
-   没有处理[悬挂指针的开销](https://en.wikipedia.org/wiki/Dangling_pointer)
-   自动[内存泄漏](https://en.wikipedia.org/wiki/Memory_leak)管理(GC本身不能保证内存泄漏的完整证明解决方案；但是，它处理了很大一部分)

缺点：

-   由于JVM必须跟踪对象引用的创建/删除，因此此活动需要比原始应用程序更多的 CPU 能力。它可能会影响需要大内存的请求的性能。
-   程序员无法控制专用于释放不再需要的对象的 CPU 时间的调度。
-   使用某些 GC 实现可能会导致应用程序意外停止。
-   自动内存管理不会像正确的手动内存分配/解除分配那样高效。

## 3. GC 实现

JVM有五种GC实现方式：

-   串行垃圾收集器
-   并行垃圾收集器
-   CMS 垃圾收集器
-   G1 垃圾收集器
-   Z 垃圾收集器

### 3.1. 串行垃圾收集器

这是最简单的 GC 实现，因为它基本上使用单个线程。结果，这个GC实现在运行时冻结了所有应用程序线程。因此，在多线程应用程序(如服务器环境)中使用它不是一个好主意。

然而，Twitter工程师在 QCon 2012 上就Serial Garbage Collector 的性能发表了精彩的演讲，这[是](https://www.infoq.com/presentations/JVM-Performance-Tuning-twitter-QCon-London-2012)更好地了解这个收集器的好方法。

Serial GC 是大多数对暂停时间要求不高且运行在客户端类型机器上的应用程序的首选垃圾收集器。要启用串行垃圾收集器，我们可以使用以下参数：

```bash
java -XX:+UseSerialGC -jar Application.java
```

### 3.2. 并行垃圾收集器

它是JVM的默认GC ，有时也称为吞吐量收集器。与Serial Garbage Collector不同，它使用多个线程来管理堆空间，但它也会在执行GC时冻结其他应用程序线程。

如果我们使用这个GC，我们可以指定最大垃圾收集线程和暂停时间、吞吐量和占用空间(堆大小)。

可以使用命令行选项-XX:ParallelGCThreads=<N>控制垃圾收集器线程的数量。

最大暂停时间目标(两次GC之间的间隔 [以毫秒为单位] )使用命令行选项-XX:MaxGCPauseMillis=<N>指定。

进行垃圾收集所花费的时间与垃圾收集之外花费的时间称为最大吞吐量目标，可以通过命令行选项-XX:GCTimeRatio=<N> 指定。

最大堆占用空间(程序运行时所需的堆内存量)使用选项-Xmx<N> 指定。

要启用并行垃圾收集器，我们可以使用以下参数：

```bash
java -XX:+UseParallelGC -jar Application.java
```

### 3.3. CMS 垃圾收集器

Concurrent Mark Sweep (CMS)实现使用多个垃圾收集器线程进行垃圾收集。它专为喜欢较短垃圾收集暂停的应用程序而设计，并且可以在应用程序运行时与垃圾收集器共享处理器资源。

简单地说，使用这种类型的 GC 的应用程序平均响应较慢，但不会停止响应以执行垃圾收集。

这里需要注意的一点是，由于此GC是并发的，因此调用显式垃圾收集(例如在并发进程运行时使用System.gc() )将导致Concurrent Mode Failure / Interruption。

如果超过 98% 的总时间花在了CMS垃圾收集上，并且只有不到 2% 的堆被回收，那么CMS收集器将抛出OutOfMemoryError。如有必要，我们可以通过在命令行中添加选项-XX:-UseGCOverheadLimit来禁用此功能。

此收集器还有一种称为增量模式的模式，该模式在 Java SE 8 中已弃用，并可能在未来的主要版本中删除。

要启用CMS 垃圾收集器，我们可以使用以下标志：

```bash
java -XX:+UseParNewGC -jar Application.java
```

[从 Java 9](https://openjdk.java.net/jeps/291)开始，CMS 垃圾收集器已被弃用。因此，如果我们尝试使用它，JVM 会打印一条警告消息：

```bash
>> java -XX:+UseConcMarkSweepGC --version
Java HotSpot(TM) 64-Bit Server VM warning: Option UseConcMarkSweepGC was deprecated 
in version 9.0 and will likely be removed in a future release.
java version "9.0.1"
```

此外，[Java 14](https://openjdk.java.net/jeps/363)完全放弃了 CMS 支持：

```bash
>> java -XX:+UseConcMarkSweepGC --version
OpenJDK 64-Bit Server VM warning: Ignoring option UseConcMarkSweepGC; 
support was removed in 14.0
openjdk 14 2020-03-17
```

### 3.4. G1 垃圾收集器

G1(垃圾优先)垃圾收集器专为运行在具有大内存空间的多处理器机器上的应用程序而设计。它可从JDK7 Update 4和更高版本中获得。

G1收集器将取代CMS收集器，因为它的性能效率更高。

与其他收集器不同，G1收集器将堆划分为一组大小相等的堆区域，每个区域都是连续的虚拟内存范围。在执行垃圾回收时，G1会显示一个并发的全局标记阶段(即阶段 1，称为标记)来确定整个堆中对象的活跃度。

标记阶段完成后，G1知道哪些区域大部分是空的。它首先在这些区域收集数据，这通常会产生大量可用空间(即第 2 阶段，称为清理)。这就是为什么这种垃圾收集方法被称为垃圾优先的原因。

要启用G1 垃圾收集器，我们可以使用以下参数：

```bash
java -XX:+UseG1GC -jar Application.java
```

### 3.5. Java 8 的变化

Java 8u20又引入了一个JVM参数，用于通过创建相同字符串的过多实例来减少不必要的内存使用。这通过将重复的String值删除到全局单个char[]数组来优化堆内存。

我们可以通过添加-XX:+UseStringDeduplication作为JVM参数来启用此参数。

### 3.6. Z 垃圾收集器

[ZGC(Z 垃圾收集器) ](https://www.baeldung.com/jvm-zgc-garbage-collector)是一种可扩展的低延迟垃圾收集器，作为 Linux 的实验性选项在 Java 11 中首次亮相。JDK 14 在 Windows 和 macOS 操作系统下引入了ZGC 。ZGC从 Java 15 开始获得生产状态。

ZGC并发执行所有昂贵的工作，不会停止应用程序线程的执行超过 10 毫秒，这使其适用于需要低延迟的应用程序。它使用带有彩色指针的负载屏障在线程运行时执行并发操作，并且它们用于跟踪堆使用情况。

参考着色(彩色指针)是ZGC的核心概念。这意味着ZGC使用引用的一些位(元数据位)来标记对象的状态。它还可以处理大小从 8MB 到 16TB 不等的堆。此外，暂停时间不会随着堆、活动集或根集大小的增加而增加。

与G1 类似，Z 垃圾收集器对堆进行分区，只是堆区域可以有不同的大小。

要启用Z 垃圾收集器，我们可以在低于 15的JDK版本中使用以下参数：

```bash
java -XX:+UnlockExperimentalVMOptions -XX:+UseZGC Application.java
```

从版本 15 开始，我们不需要实验模式：

```bash
java -XX:+UseZGC Application.java
```

我们应该注意到ZGC不是默认的垃圾收集器。

## 4。总结

在本文中，我们研究了不同的JVM 垃圾收集实现及其用例。

可以在[此处](http://www.oracle.com/technetwork/java/javase/gc-tuning-6-140523.html)找到更详细的文档。