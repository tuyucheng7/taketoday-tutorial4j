## 1. 概述

我们都可能已经注意到，当谈到内存消耗时，我们的 Java 应用程序的内存使用并没有遵循我们基于-Xmx(最大堆大小)选项的严格说明。事实上，JVM 有比堆更多的内存区域。

为了限制总内存使用量，需要注意一些额外的内存设置，所以让我们从 Java 应用程序的内存结构和内存分配来源开始。

## 2. Java进程的内存结构

[Java 虚拟机 (JVM)](https://spring.io/blog/2019/03/11/memory-footprint-of-the-jvm)的内存分为两大类：堆和非堆。

堆内存是 JVM 内存中最著名的部分。它存储应用程序创建的对象。JVM 在启动时启动堆。当应用程序创建对象的新实例时，该对象驻留在堆中，直到应用程序释放该实例。然后，垃圾收集器 (GC) 释放实例持有的内存。因此，堆大小根据负载而变化，尽管我们可以使用-Xmx选项配置最大 JVM 堆大小。

非堆内存弥补了其余部分。它允许我们的应用程序使用比配置的堆大小更多的内存。JVM 的非堆内存分为几个不同的区域。JVM 代码和内部结构、加载的分析器代理代码、常量池等类结构、字段和方法的元数据、方法和构造函数的代码以及驻留字符串等数据都被归类为非堆内存.

值得一提的是，我们可以使用-XX选项调整非堆内存的某些区域，例如 -XX:MaxMetaspaceSize(相当于Java 7 及更早版本中的–XX:MaxPermSize)。我们将在本教程中看到更多标志。

除了 JVM 本身，Java 进程还有其他消耗内存的区域。例如，我们有堆外技术，通常使用直接[ByteBuffer](https://www.baeldung.com/java-bytebuffer)来处理大内存并使其不受 GC 的控制[。](https://www.baeldung.com/java-bytebuffer)另一个来源是本地库使用的内存。

## 3. JVM的非堆内存区

让我们继续 JVM 的非堆内存区域。

### 3.1. 元空间

元空间是一个本地内存区域，用于存储类的元数据。当加载一个类时，JVM 将[类的元数据(](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html)即它的运行时表示)分配到元空间中。每当类加载器及其所有类从堆中移除时，它们在元空间中的分配就可以被认为被 GC 释放了。

但是，释放的元空间不一定返回给操作系统。该内存的全部或部分可能仍由 JVM 保留，以供将来的类加载重新使用。

在早于 8 的 Java 版本中，元空间称为永久代 (PermGen)。但是，与 Metaspace 是堆外内存区域不同，[PermGen 驻留在一个特殊的堆区域](https://www.baeldung.com/java-permgen-metaspace)中。

### 3.2. 代码缓存

[即时 (JIT) 编译器](https://www.baeldung.com/graal-java-jit-compiler#jit-compiler)将其输出存储在[代码缓存](https://www.baeldung.com/jvm-code-cache)区域中。JIT 编译器将字节码编译为频繁执行的部分(也称为热点)的本机代码。[Java 7 中引入的分层编译](https://www.baeldung.com/jvm-tiered-compilation)是客户端编译器 (C1) 使用插桩编译代码，然后服务器编译器 (C2) 使用分析数据以优化方式编译该代码的方法。

分层编译的目标是混合使用 C1 和 C2 编译器，以获得快速启动时间和良好的长期性能。分层编译将需要缓存在内存中的代码量增加多达四倍。从 Java 8 开始，这对 JIT 默认启用，尽管我们仍然可以禁用分层编译。

### 3.3. 线

[线程堆栈](https://www.baeldung.com/java-stack-heap#stack-memory-in-java)包含每个已执行方法的所有局部变量以及线程为到达当前执行点而调用的方法。线程堆栈只能由创建它的线程访问。

理论上，由于线程栈内存是运行线程数的函数，而且线程数没有限制，线程区是无界的，可以占用很大一部分内存。实际上，操作系统限制了线程的数量，JVM 有一个基于平台的每个线程的堆栈内存大小的默认值。

### 3.4. 垃圾收集

JVM 附带一组 GC 算法，可以根据我们应用程序的用例进行选择。无论我们使用什么算法，一定数量的本机内存都会分配给 GC 进程，但使用的内存数量因使用的[垃圾收集器](https://www.baeldung.com/jvm-garbage-collectors)而异。

### 3.5. 象征

JVM 使用 Symbol 区域来存储符号，例如字段名称、方法签名和 interned 字符串。在 Java 开发工具包 (JDK) 中，符号存储在三个不同的表中：

-   系统字典包含所有加载的类型信息，如 Java 类。
-   常量池使用符号表数据结构来保存类、方法、字段和可枚举类型的加载符号。JVM 维护一个称为[运行时常量池的每个类型常量池](https://www.baeldung.com/jvm-constant-pool)，其中包含多种常量，从编译时数字文字到运行时方法甚至字段引用。
-   字符串表包含对所有常量字符串的引用，也称为驻留字符串。

要了解字符串表，我们需要更多地了解[字符串池。](https://www.baeldung.com/java-string-pool)String Pool 是一种 JVM 机制，它通过称为 interning 的进程在池中仅存储每个文字String的一个副本来优化分配给String的内存量。字符串池有两部分：

-   驻留字符串的内容作为常规String对象存在于 Java 堆中。
-   哈希表，也就是所谓的字符串表，是在堆外分配的，包含对驻留字符串的引用。

也就是说，String Pool既有堆内的，也有堆外的。堆外部分是字符串表。虽然它通常更小，但当我们有更多的驻留字符串时，它仍然会占用大量的额外内存。

### 3.6. 竞技场

Arena是JVM自己实现的[基于Arena的内存管理](https://en.wikipedia.org/wiki/Region-based_memory_management)，区别于glibc的arena内存管理。它被 JVM 的一些子系统使用，比如编译器和符号，或者当本机代码使用依赖于 JVM arenas 的内部对象时。

### 3.7. 其他

所有其他不能归类到本机内存区域的内存使用都属于本节。例如，DirectByteBuffer的使用在这部分是间接可见的。

## 4.内存监控工具

既然我们已经发现 Java 内存使用不仅限于堆，我们将研究跟踪总内存使用的方法。可以在分析和内存监控工具的帮助下进行发现，然后，我们可以通过一些特定的调整来调整总体使用情况。

让我们快速浏览一下 JDK 附带的可用于 JVM 内存监控的工具：

-   [jmap](https://docs.oracle.com/en/java/javase/11/tools/jmap.html)是一个命令行实用程序，可以打印正在运行的 VM 或核心文件的内存映射。我们也可以使用jmap查询远程机器上的进程。但是，在 JDK8 中引入jcmd之后，建议使用jcmd而不是jmap，以增强诊断并降低性能开销。
-   [jcmd](https://www.baeldung.com/running-jvm-diagnose)用于向 JVM 发送诊断命令请求，这些请求可用于控制 Java[ Flight Recordings](https://docs.oracle.com/javacomponents/jmc-5-4/jfr-runtime-guide/about.htm#JFRUH170)、故障排除以及诊断 JVM 和 Java 应用程序。jcmd不适用于远程进程。我们将在本文中看到jcmd的一些具体用法。
-   [jhat](https://docs.oracle.com/javase/7/docs/technotes/tools/share/jhat.html)通过启动本地网络服务器可视化堆转储文件。有几种方法可以创建堆转储，例如使用jmap -dump或jcmd GC.heap_dump 文件名。
-   [hprof](https://docs.oracle.com/javase/7/docs/technotes/samples/hprof.html)能够显示 CPU 使用情况、堆分配统计信息和监控争用情况。根据请求的分析类型， hprof指示虚拟机收集相关的 JVM 工具接口 (JVM TI) 事件并将事件数据处理为分析信息。

除了 JVM 附带的工具之外，还有特定于操作系统的命令来检查进程的内存。[pmap](http://man7.org/linux/man-pages/man1/pmap.1.html)是可用于 Linux 发行版的工具，可提供 Java 进程使用的内存的完整视图。

## 5. 本机内存跟踪

[本机内存跟踪](https://docs.oracle.com/en/java/javase/11/vm/native-memory-tracking.html)(NMT) 是一种 JVM 功能，我们可以使用它来跟踪 VM 的内部内存使用情况。NMT 不会像第三方本机代码内存分配那样跟踪所有本机内存使用情况，但是，它足以满足一大类典型应用程序的需求。

要开始使用 NMT，我们必须为我们的应用程序启用它：

```bash
java -XX:NativeMemoryTracking=summary -jar app.jar
```

-XX:NativeMemoryTracking的其他可用值是off和detail。请注意，启用 NMT 会产生会影响性能的间接成本。此外，NMT 将两个机器字作为 malloc 标头添加到所有 malloced 内存中。

然后我们可以使用不带参数的[jps](https://docs.oracle.com/en/java/javase/11/tools/jps.html)或jcmd来查找我们应用程序的进程 ID (pid)：

```bash
jcmd
<pid> <our.app.main.Class>
```

找到我们的应用程序 pid 后，我们可以继续使用jcmd，它提供了一长串要监视的选项。让我们向jcmd寻求帮助以查看可用选项：

```bash
jcmd <pid> help
```

从输出中，我们看到jcmd支持不同的类别，例如 Compiler、GC、[JFR](https://docs.oracle.com/javacomponents/jmc-5-4/jfr-runtime-guide/about.htm)、JVMTI、ManagementAgent 和 VM。一些选项，如VM.metaspace、VM.native_memory可以帮助我们进行内存跟踪。让我们探讨其中的一些。

### 5.1. 本机内存摘要报告

最方便的是VM.native_memory。我们可以使用它来查看我们应用程序的 VM 内部本机内存使用情况的摘要：

```bash
jcmd <pid> VM.native_memory summary
<pid>:

Native Memory Tracking:

Total: reserved=1779287KB, committed=503683KB
- Java Heap (reserved=307200KB, committed=307200KB)
  ...
- Class (reserved=1089000KB, committed=44824KB)
  ...
- Thread (reserved=41139KB, committed=41139KB)
  ...
- Code (reserved=248600KB, committed=17172KB)
  ...
- GC (reserved=62198KB, committed=62198KB)
  ...
- Compiler (reserved=175KB, committed=175KB)
  ...
- Internal (reserved=691KB, committed=691KB)
  ...
- Other (reserved=16KB, committed=16KB)
  ...
- Symbol (reserved=9704KB, committed=9704KB)
  ...
- Native Memory Tracking (reserved=4812KB, committed=4812KB)
  ...
- Shared class space (reserved=11136KB, committed=11136KB)
  ...
- Arena Chunk (reserved=176KB, committed=176KB)
  ... 
- Logging (reserved=4KB, committed=4KB)
  ... 
- Arguments (reserved=18KB, committed=18KB)
  ... 
- Module (reserved=175KB, committed=175KB)
  ... 
- Safepoint (reserved=8KB, committed=8KB)
  ... 
- Synchronization (reserved=4235KB, committed=4235KB)
  ... 

```

查看输出，我们可以看到 Java 堆、GC 和线程等 JVM 内存区域的摘要。术语“保留”内存是指通过malloc或mmap预映射的总地址范围，因此它是该区域的最大可寻址内存。术语“提交”是指积极使用的内存。

[在这里](https://www.baeldung.com/native-memory-tracking-in-jvm#nmt)，我们可以找到输出的详细解释。要查看内存使用量的变化，我们可以依次使用VM.native_memory baseline和VM.native_memory summary.diff 。

### 5.2. 元空间和字符串表的报告

我们可以尝试jcmd的其他 VM 选项来概览本机内存的某些特定区域，例如元空间、符号和驻留字符串。

让我们试试元空间：

```bash
jcmd <pid> VM.metaspace
```

我们的输出如下所示：

```bash
<pid>:
Total Usage - 1072 loaders, 9474 classes (1176 shared):
...
Virtual space:
  Non-class space:       38.00 MB reserved,      36.67 MB ( 97%) committed 
      Class space:        1.00 GB reserved,       5.62 MB ( <1%) committed 
             Both:        1.04 GB reserved,      42.30 MB (  4%) committed 
Chunk freelists:
   Non-Class: ...
       Class: ...
Waste (percentages refer to total committed size 42.30 MB):
              Committed unused:    192.00 KB ( <1%)
        Waste in chunks in use:      2.98 KB ( <1%)
         Free in chunks in use:      1.05 MB (  2%)
     Overhead in chunks in use:    232.12 KB ( <1%)
                In free chunks:     77.00 KB ( <1%)
Deallocated from chunks in use:    191.62 KB ( <1%) (890 blocks)
                       -total-:      1.73 MB (  4%)
MaxMetaspaceSize: unlimited
CompressedClassSpaceSize: 1.00 GB
InitialBootClassLoaderMetaspaceSize: 4.00 MB
```

现在，让我们看看应用程序的字符串表：

```bash
jcmd <pid> VM.stringtable 

```

让我们看看输出：

```bash
<pid>:
StringTable statistics:
Number of buckets : 65536 = 524288 bytes, each 8
Number of entries : 20046 = 320736 bytes, each 16
Number of literals : 20046 = 1507448 bytes, avg 75.000
Total footprint : = 2352472 bytes
Average bucket size : 0.306
Variance of bucket size : 0.307
Std. dev. of bucket size: 0.554
Maximum bucket size : 4
```

## 6. JVM内存调优

我们知道 Java 应用程序正在使用总内存作为堆分配和 JVM 或第三方库的一堆非堆分配的总和。

非堆内存在负载下不太可能改变大小。通常，一旦加载了所有正在使用的类并且 JIT 已完全预热，我们的应用程序就会稳定地使用非堆内存。但是，我们可以使用一些标志来指示 JVM 如何管理某些区域的内存使用。

jcmd提供了一个VM.flag选项来查看我们的 Java 进程已经具有哪些标志，包括默认值，因此我们可以将它用作检查默认配置并了解 JVM 是如何配置的工具：

```bash
jcmd <pid> VM.flags
```

在这里，我们看到使用过的标志及其值：

```bash
<pid>:
-XX:CICompilerCount=4 
-XX:ConcGCThreads=2 
-XX:G1ConcRefinementThreads=8 
-XX:G1HeapRegionSize=1048576 
-XX:InitialHeapSize=314572800 
...
```

让我们看一下用于不同区域内存调整的一些[VM 标志](https://www.baeldung.com/jvm-parameters)。

### 6.1. 堆

我们有一些用于调整 JVM 堆的标志。要配置最小和最大堆大小，我们有-Xms ( -XX:InitialHeapSize ) 和-Xmx ( -XX:MaxHeapSize )。如果我们更喜欢将堆大小设置为物理内存的百分比，我们可以使用[-XX:MinRAMPercentage和-XX:MaxRAMPercentage](https://www.baeldung.com/java-jvm-parameters-rampercentage)。重要的是要知道，当我们分别使用-Xms和-Xmx选项时，JVM 会忽略这两个选项。

另一个可能影响内存分配模式的选项是XX:+AlwaysPreTouch。默认情况下，JVM 最大堆分配在虚拟内存中，而不是物理内存中。只要没有写操作，操作系统可能会决定不分配内存。为了避免这种情况(特别是对于巨大的DirectByteBuffers，重新分配可能需要一些时间来重新排列操作系统内存页面)，我们可以启用 -XX:+AlwaysPreTouch。Pretouching 在所有页面上写入“0”并强制操作系统分配内存，而不仅仅是保留它。预触摸会导致 JVM 启动延迟，因为它在单个线程中工作。

### 6.2. 线程堆栈

线程堆栈是每个执行方法的所有局部变量的每个线程存储。我们使用[-Xss 或XX:ThreadStackSize](https://www.baeldung.com/jvm-configure-stack-sizes)选项来配置每个线程的堆栈大小。默认线程堆栈大小取决于平台，但在大多数现代 64 位操作系统中，它最大为 1 MB。

### 6.3. 垃圾收集

我们可以使用以下标志之一设置应用程序的 GC 算法：-XX:+UseSerialGC、-XX:+UseParallelGC、-XX:+UseParallelOldGC、-XX:+UseConcMarkSweepGC或-XX:+UseG1GC。

如果我们选择 G1 作为 GC，我们可以选择通过-XX:+UseStringDeduplication启用[字符串重复数据删除](https://openjdk.org/jeps/192)。它可以节省很大一部分内存。字符串重复数据删除仅适用于长期存在的实例。为了避免这种情况，我们可以使用-XX:StringDeduplicationAgeThreshold 配置实例的有效年龄。-XX:StringDeduplicationAgeThreshold的值表示 GC 循环存活的次数。


### 6.4. 代码缓存

从 Java 9 开始，JVM 将代码缓存分为三个区域。因此，JVM 提供了特定的选项来调整每个区域：

-   -XX:NonNMethodCodeHeapSize配置非方法段，即JVM内部相关代码。默认情况下，它大约为 5 MB。
-   -XX:ProfiledCodeHeapSize配置分析代码段，这是 C1 编译代码，生命周期可能很短。默认大小约为 122 MB。
-   -XX:NonProfiledCodeHeapSize设置非分析段的大小，这是 C2 编译的代码，可能具有很长的生命周期。默认大小约为 122 MB。

### 6.5. 分配器

JVM 从[保留内存](https://github.com/corretto/corretto-11/blob/3b31d243a19774bebde63df21cc84e994a89439a/src/src/hotspot/os/linux/os_linux.cpp#L3421-L3444)开始，然后通过使用 glibc 的 malloc 和 mmap[修改内存映射](https://github.com/corretto/corretto-11/blob/3b31d243a19774bebde63df21cc84e994a89439a/src/src/hotspot/os/linux/os_linux.cpp#L3517-L3531)，使部分“保留”可用。保留和释放内存块的行为会导致碎片。分配内存中的碎片会导致内存中出现大量未使用的区域。

除了 malloc，我们还可以使用其他分配器，例如[jemalloc](http://jemalloc.net/)或[tcmalloc](https://google.github.io/tcmalloc/overview.html)。jemalloc 是一种通用的 malloc 实现，它强调碎片避免和可扩展的并发支持，因此它通常看起来比常规 glibc 的 malloc 更聪明。此外，jemalloc 还可用于[泄漏检查](https://github.com/jemalloc/jemalloc/wiki/Use-Case:-Leak-Checking)和堆分析。

### 6.6. 元空间

与堆一样，我们也有配置元空间大小的选项。要配置 Metaspace 的下限和上限，我们可以分别使用-XX:MetaspaceSize和-XX: MaxMetaspaceSize。

-XX:InitialBootClassLoaderMetaspaceSize 也可用于配置初始引导类加载器大小。

有 -XX:MinMetaspaceFreeRatio和-XX:MaxMetaspaceFreeRatio 选项来配置 GC 后可用类元数据容量的最小和最大百分比。

我们还可以使用-XX:MaxMetaspaceExpansion配置没有完整 GC 的元空间扩展的最大大小。

### 6.7. 其他非堆内存区域

还有一些标志用于调整本机内存其他区域的使用。

我们可以使用-XX:StringTableSize来指定字符串池的映射大小，其中映射大小表示不同的驻留字符串的最大数量。对于 JDK7+，默认映射大小为600013，这意味着默认情况下我们可以在池中拥有 600,013 个不同的字符串。

为了控制DirectByteBuffers的内存使用，我们可以使用[-XX:MaxDirectMemorySize。](https://www.eclipse.org/openj9/docs/xxmaxdirectmemorysize/)使用此选项，我们限制了可以为所有DirectByteBuffers保留的内存量。

对于需要加载更多类的应用，我们可以使用-XX:PredictedLoadedClassCount。这个选项从 JDK8 开始可用，它允许我们设置系统字典的桶大小。

## 七、总结

在本文中，我们探讨了 Java 进程的不同内存区域以及一些用于监视内存使用情况的工具。我们已经看到 Java 内存使用不仅仅局限于堆，因此我们使用jcmd来检查和跟踪 JVM 的内存使用情况。最后，我们回顾了一些可以帮助我们调整 Java 应用程序内存使用情况的 JVM 标志。