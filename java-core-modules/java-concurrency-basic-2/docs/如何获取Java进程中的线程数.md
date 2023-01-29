## 1. 概述

线程是Java中[并发](https://www.baeldung.com/java-concurrency)的基本单元，在大多数情况下，当创建多个线程并行执行任务时，应用程序的吞吐量会增加。

但是，总有一个饱和点。毕竟，应用程序的吞吐量取决于CPU和内存资源。**在达到一定限制后，增加线程数可能会导致内存消耗增加、线程上下文切换等**。

因此，对Java应用程序中的高内存问题进行故障排除的一个良好起点是监视线程数。在本文中，我们将研究一些检查Java进程创建的线程数的方法。

## 2. 图形化Java监控工具

查看Java中线程数量的最简单方法是使用[Java VisualVM](https://www.baeldung.com/java-profilers)之类的图形工具。**除了应用程序线程之外，Java VisualVM还列出了GC或应用程序使用的任何其他线程，如JMX线程**。

此外，它还显示了线程状态等统计信息及其持续时间：

<img src="../assets/img.png">

监视线程数是Java VisualVM中最基本的功能，一般来说，图形工具更先进，允许对应用程序进行实时监控。例如，Java VisualVM允许我们对CPU堆栈跟踪进行采样，从而找到可能导致CPU瓶颈的类或方法。

Java VisualVM在Windows系统上放置在JDK安装包中，**对于部署在Linux上的应用程序，我们需要远程连接到该应用程序，这需要**[JMX](https://www.baeldung.com/java-management-extensions) **VM参数**。

因此，如果应用程序已经在没有这些参数的情况下运行，那么这些工具将无法工作。在后面的部分中，我们将看到如何使用命令行工具获取线程数。

## 3. Java API

在某些用例中，我们可能希望找到应用程序本身中的线程数。例如，在监控仪表盘上显示或在日志中显示。

在这种情况下，我们依赖Java API来获取线程数。幸运的是，Thread类中有一个activeCount() API：

```java
public class FindNumberOfThreads {

    public static void main(String[] args) {
        System.out.println("Number of threads " + Thread.activeCount());
    }
}
```

运行以上程序，控制台的输出为：

```shell
Number of threads 2
```

值得注意的是，如果我们在Java VisualVM中查看线程数量，我们会看到更多的线程数量，**这是因为activeCount()只返回同一线程组中的线程数**。Java将所有线程分成组，以便于管理。

在这个例子中，我们只有父线程组，即main：

```java
public class FindNumberOfThreads {

    public static void main(String[] args) {
        System.out.println("Current Thread Group - " + Thread.currentThread().getThreadGroup().getName());
    }
}
```

```shell
Current Thread Group - main
```

如果Java应用程序中有许多线程组，activeCount()将不会给出正确的输出。例如，它不会返回GC线程的数量。

**在这种情况下，我们可以使用JMX API**：

```java
public class FindNumberOfThreads {

    public static void main(String[] args) {
        System.out.println("Total Number of threads " + ManagementFactory.getThreadMXBean().getThreadCount());
    }
}
```

该API返回所有线程组、GC、JMX等的线程总数：

```shell
Total Number of threads 8
```

事实上，像Java VisualVM这样的JMX图形工具对其数据使用相同的API。

## 4. 命令行工具

之前，我们介绍了Java VisualVM，这是一种用于分析应用程序中活动线程的图形工具。虽然它是实时可视化线程的一个很好的工具，但它对应用程序性能有很小的影响。因此，**不建议将其用于生产环境**。

此外，正如我们所讨论的，Java VisualVM需要在Linux中进行远程连接。事实上，在某些情况下，它需要额外的配置。例如，在Docker或Kubernetes中运行的应用程序需要额外的服务和端口配置。

在这种情况下，我们必须依赖主机环境中的命令行工具来获取线程数。

**幸运的是，Java提供了一些命令来获取**[Thread dump](https://www.baeldung.com/java-thread-dump)。我们可以将Thread dump作为文本文件进行分析，也可以使用Thread dump分析器工具来检查线程的数量及其状态。

[阿里巴巴Arthas](https://www.baeldung.com/java-alibaba-arthas-intro)是另一个很棒的命令行工具，它不需要远程连接或任何特殊配置。

此外，我们还可以从几个Linux命令中获得有关线程的信息。例如，**我们可以使用**[top](https://www.baeldung.com/linux/top-command)**命令显示任何Java应用程序的所有线程**：

```shell
top -H -p 1
```

这里，-H是一个命令行参数，用于显示Java进程中的每个线程。如果不指定这个参数，top命令将显示进程中所有线程的组合统计信息。-p参数按目标应用程序的进程id过滤输出：

```shell
top - 15:59:44 up 7 days, 19:23,  0 users,  load average: 0.52, 0.41, 0.36
Threads:  37 total,   0 running,  37 sleeping,   0 stopped,   0 zombie
%Cpu(s):  3.2 us,  2.2 sy,  0.0 ni, 93.4 id,  0.8 wa,  0.0 hi,  0.3 si,  0.0 st
MiB Mem :   1989.2 total,    110.2 free,   1183.1 used,    695.8 buff/cache
MiB Swap:   1024.0 total,    993.0 free,     31.0 used.    838.8 avail Mem

  PID USER      PR  NI    VIRT    RES    SHR S  %CPU  %MEM     TIME+ COMMAND
   1 flink     20   0 2612160 304084  29784 S   0.0  14.9   0:00.07 java
  275 flink     20   0 2612160 304084  29784 S   0.0  14.9   0:02.87 java
  276 flink     20   0 2612160 304084  29784 S   0.0  14.9   0:00.37 VM Thread
  277 flink     20   0 2612160 304084  29784 S   0.0  14.9   0:00.00 Reference Handl
  278 flink     20   0 2612160 304084  29784 S   0.0  14.9   0:00.00 Finalizer
  279 flink     20   0 2612160 304084  29784 S   0.0  14.9   0:00.00 Signal Dispatch
```

如上所示，它显示了线程id，即PID和每个线程的CPU和内存利用率。与Java VisualVM类似，top命令将列出所有线程，包括GC、JMX或任何其他子进程。

要查找在上述命令中用作参数的进程ID，我们可以使用ps命令：

```shell
ps -ef | grep java
```

**事实上，我们也可以使用ps命令列出线程**：

```shell
ps -e -T | grep 1
```

-T选项告诉ps命令列出应用程序启动的所有线程：

```shell
1     1 ?        00:00:00 java
1   275 ?        00:00:02 java
1   276 ?        00:00:00 VM Thread
1   277 ?        00:00:00 Reference Handl
1   278 ?        00:00:00 Finalizer
1   279 ?        00:00:00 Signal Dispatch
1   280 ?        00:00:03 C2 CompilerThre
1   281 ?        00:00:01 C1 CompilerThre
```

这里，第一列是PID，第二列显示每个线程的Linux线程ID。

## 5. 总结

在本文中，我们看到有多种方法可以获取Java应用程序中的线程数。**在大多数情况下，使用命令行参数(如top或ps命令)应该是首选方法**。

但是，在某些情况下，我们可能还需要图形工具，例如Java VisualVM。