## 1. 概述

在本快速教程中，我们将探索可用于配置 Java 虚拟机的最著名选项。

## 2.显式堆内存——Xms和Xmx选项

最常见的与性能相关的做法之一是根据应用程序要求初始化堆内存。

这就是为什么我们应该指定最小和最大堆大小。以下参数可用于实现它：

```plaintext
-Xms<heap size>[unit] 
-Xmx<heap size>[unit]
```

这里的unit表示要初始化的内存(用heap size表示)的单位。单位可以标记为“g”表示 GB，“m”表示 MB，“k”表示 KB。

例如，如果我们要为 JVM 分配最小 2 GB 和最大 5 GB 的内存，我们需要这样写：

```plaintext
-Xms2G -Xmx5G
```

从 Java 8 开始，元空间的大小[没有](https://matthung0807.blogspot.com/2019/03/about-g1-garbage-collector-permanent.html)定义。一旦达到全局限制，JVM 会自动增加它，但是，为了克服任何不必要的不稳定性，我们可以设置元空间大小：

```plaintext
-XX:MaxMetaspaceSize=<metaspace size>[unit]
```

在这里，元空间大小表示我们要分配给元空间的内存量。

根据[Oracle 指南](https://docs.oracle.com/en/java/javase/11/gctuning/factors-affecting-garbage-collection-performance.html#GUID-189AD425-F9A0-444A-AC89-C967E742B25C)，在总可用内存之后，第二大影响因素是为年轻代保留的堆的比例。默认情况下，YG 的最小大小为 1310 MB，最大大小为unlimited。

我们可以明确地分配它们：

```plaintext
-XX:NewSize=<young size>[unit] 
-XX:MaxNewSize=<young size>[unit]
```

## 3. 垃圾收集

为了提高应用程序的稳定性，选择正确的[垃圾收集](http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/gc01/index.html)算法至关重要。

JVM 有四种类型的GC实现：

-   串行垃圾收集器
-   并行垃圾收集器
-   CMS 垃圾收集器
-   G1 垃圾收集器

这些实现可以使用以下参数声明：

```plaintext
-XX:+UseSerialGC
-XX:+UseParallelGC
-XX:+USeParNewGC
-XX:+UseG1GC
```

可以在[此处找到有关](https://www.baeldung.com/jvm-garbage-collectors)垃圾收集实现的更多详细信息。

## 4. GC 日志记录

为了严格监控应用程序的健康状况，我们应该经常检查 JVM 的垃圾收集性能。最简单的方法是以人类可读的格式记录GC活动。

使用以下参数，我们可以记录GC活动：

```plaintext
-XX:+UseGCLogFileRotation 
-XX:NumberOfGCLogFiles=< number of log files > 
-XX:GCLogFileSize=< file size >[ unit ]
-Xloggc:/path/to/gc.log
```

UseGCLogFileRotation指定日志文件滚动策略，很像log4j、s4lj 等。NumberOfGCLogFiles表示单个应用程序生命周期可以写入的最大日志文件数。GCLogFileSize指定文件的最大大小。最后， loggc表示它的位置。

这里要注意的是，还有两个可用的 JVM 参数(-XX:+PrintGCTimeStamps和-XX:+PrintGCDateStamps)可用于在GC日志中打印日期时间戳。

例如，如果我们要分配最多 100 个GC日志文件，每个文件的最大大小为 50 MB，并希望将它们存储在 ' /home/user/log/'位置，我们可以使用以下语法：

```plaintext
-XX:+UseGCLogFileRotation  
-XX:NumberOfGCLogFiles=10
-XX:GCLogFileSize=50M 
-Xloggc:/home/user/log/gc.log
```

然而，问题在于总是使用一个额外的守护线程在后台监视系统时间。这种行为可能会造成一些性能瓶颈；这就是为什么最好不要在生产中使用这个参数。

## 5.处理内存不足

[大型应用程序面临内存不足错误](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/OutOfMemoryError.html)是很常见的，这反过来会导致应用程序崩溃。这是一个非常关键的场景，很难来解决问题。

这就是为什么 JVM 带有一些参数，这些参数将堆内存转储到一个物理文件中，该文件以后可以用于查找泄漏：

```plaintext
-XX:+HeapDumpOnOutOfMemoryError 
-XX:HeapDumpPath=./java_pid<pid>.hprof
-XX:OnOutOfMemoryError="< cmd args >;< cmd args >" 
-XX:+UseGCOverheadLimit
```

这里有几点需要注意：

-   HeapDumpOnOutOfMemoryError指示 JVM 在发生OutOfMemoryError时将堆转储到物理文件中
-   HeapDumpPath表示要写入文件的路径；可以给出任何文件名；但是，如果 JVM 在名称中找到<pid>标记，则导致内存不足错误的当前进程的进程 ID 将以.hprof格式
-   OnOutOfMemoryError用于在出现内存不足错误时发出要执行的紧急命令；应在 cmd args 的空间中使用正确的命令。例如，如果我们想在内存不足时立即重启服务器，我们可以设置参数：

```plaintext
-XX:OnOutOfMemoryError="shutdown -r"
```

-   UseGCOverheadLimit是一种策略，用于限制 VM 在抛出 OutOfMemory错误之前花费在 GC 上的时间比例

## 6. 32/64 位

在同时安装了32位和64位包的OS环境下，JVM会自动选择32位环境包。

如果我们想手动将环境设置为 64 位，我们可以使用以下参数来实现：

```plaintext
-d<OS bit>
```

操作系统位可以是32位或64 位。可以在[此处](http://www.oracle.com/technetwork/java/hotspotfaq-138619.html#64bit_layering)找到有关此的更多信息。

## 7.杂项

-   -server : 启用“服务器热点虚拟机”；64位JVM默认使用该参数
-   -XX:+UseStringDeduplication： Java 8u20引入了这个 JVM 参数，用于通过创建相同String的过多实例来减少不必要的内存使用这通过将重复的String值减少到单个全局 char[] 数组来优化堆内存
-   -XX:+UseLWPSynchronization : 设置LWP ( Light Weight Process )——基于同步策略而不是基于线程的同步
-   -XX:LargePageSizeInBytes：设置用于 Java 堆的大页面大小；它以 GB/MB/KB 为参数；使用更大的页面大小，我们可以更好地利用虚拟内存硬件资源；但是，这可能会导致PermGen的空间大小变大，这反过来又会强制减小 Java 堆空间的大小
-   -XX:MaxHeapFreeRatio ：设置GC后可用堆的最大百分比以避免收缩。
-   -XX:MinHeapFreeRatio ：设置GC后堆空闲的最小百分比以避免膨胀；要监视堆使用情况，你可以使用JDK 附带的[VisualVM 。](https://visualvm.github.io/)
-   -XX:SurvivorRatio : eden / survivor 空间大小的比例——例如， -XX:SurvivorRatio=6设置每个survivor 空间和eden 空间之间的比例为 1:6，
-   -XX:+UseLargePages : 如果系统支持，使用大页面内存；请注意，如果使用此 JVM 参数， OpenJDK 7往往会崩溃
    
-   -XX:+UseStringCache：启用缓存字符串池中可用的常用分配字符串
    
-   -XX:+UseCompressedStrings : 对可以用纯 ASCII 格式表示的String对象使用byte[]类型
-   -XX:+OptimizeStringConcat：它尽可能优化字符串连接操作
    

## 八、总结

在这篇快速文章中，我们了解了一些重要的 JVM 参数——可用于调整和提高一般应用程序性能。

其中一些也可用于调试目的。

如果你想更详细地探索参考参数，可以从[这里](http://www.oracle.com/technetwork/articles/java/vmoptions-jsp-140102.html)开始。