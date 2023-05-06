## 1. 概述

在本教程中，我们将解决Java程序中CPU使用率过高的问题。我们将研究潜在的根本原因以及如何对此类情况进行故障排除。

## 2. 高CPU使用率的定义

**在继续之前，我们必须定义我们认为的高CPU使用率**。毕竟，这个指标取决于程序在做什么，并且可能会波动很大，甚至高达100%。

对于本文，我们将考虑Windows任务管理器或Unix/Linux [top命令](https://www.baeldung.com/linux/top-command)在很长一段时间内(从几分钟到几小时)显示%CPU使用率90-100%的情况。此外，这种使用应该是没有根据的-换句话说，该程序不应该处于密集工作之中。

## 3. 可能的根本原因

**高CPU负载有多种潜在的根本原因**。可能某些是我们在实现中引入的，而其他一些可能是由意外的系统状态或利用率引起的。

### 3.1 实现错误

我们应该检查的第一件事是我们的代码中可能存在无限循环。由于多线程的工作方式，即使在这些情况下，我们的程序仍然可以响应。

一个潜在的陷阱是在应用程序服务器(或像[Tomcat](https://www.baeldung.com/tomcat)这样的Servlet容器)上运行的Web应用程序。尽管我们可能不会在代码中显式创建新线程，但应用程序服务器会在单独的线程中处理每个请求。因此，即使某些请求陷入循环，服务器仍会继续正确处理新请求。**这会给我们一种错误的印象，以为一切都在正常运行，而实际上，应用程序性能不佳，如果有足够多的线程最终被阻塞，甚至可能会崩溃**。

### 3.2 糟糕的算法或数据结构

另一个可能的实现问题是**引入了性能不佳或与我们的特定用例不兼容的算法或数据结构**。

让我们看一个简单的例子：

```java
List<Integer> generateList() {
    return IntStream.range(0, 10000000).parallel().map(IntUnaryOperator.identity()).collect(ArrayList::new, List::add, List::addAll);
}
```

我们使用ArrayList实现生成一个包含10.000.000个数字的简单列表。

接下来，让我们访问位于末尾的列表数据：

```java
List<Integer> list = generateList();
long start = System.nanoTime();
int value = list.get(9500000);
System.out.printf("Found value %d in %d nanos\n", value, (System.nanoTime() - start));
```

由于我们使用的是ArrayList，因此索引访问非常快，并且我们收到一条消息表明：

```shell
Found value 9500000 in 49100 nanos
```

让我们看看如果List实现从ArrayList更改为LinkedList会发生什么：

```java
List<Integer> generateList() {
    return IntStream.range(0, 10000000).parallel().map(IntUnaryOperator.identity()).collect(LinkedList::new, List::add, List::addAll);
}
```

**现在运行我们的程序显示访问时间慢得多**：

```shell
Found value 9500000 in 4825900 nanos
```

我们可以看到，只要稍作改动，我们的程序就会慢100倍。

尽管我们自己永远不会引入这样的更改，但可能不知道我们如何使用generateList的其他开发人员会这样做。**此外，我们甚至可能不拥有generateList API实现，因此无法控制它**。

### 3.3 大型连续GC周期

**还有一些原因与我们的实现无关，甚至可能超出我们的控制范围**。其中之一是大型连续垃圾回收。

这取决于我们正在处理的系统类型及其用途。一个示例是聊天室应用程序，其中用户收到每条发布的消息的通知。在小范围内，简单的实现也能正常工作。

但是，如果我们的应用程序开始增长到数百万用户，其中每个用户都是多个房间的成员，则生成的通知对象的数量和速率将急剧增加。这会很快使我们的堆饱和并触发[stop-the-world](https://www.baeldung.com/jvm-experimental-garbage-collectors#2-existing-garbage-collectors-in-java)垃圾回收。当JVM清理堆时，我们的系统停止响应，这会降低用户体验。

## 4. CPU问题故障排除

从上面的例子可以明显看出，解决这些问题并不总是简单地通过检查或调试代码来完成。但是，我们可以使用一些工具来获取有关我们的程序正在发生的事情以及可能的罪魁祸首的信息。

### 4.1 使用分析器

**使用分析器始终是一个有效且安全的选择**。无论是GC循环还是无限循环，分析器都会快速将我们指向热代码路径。

市场上有许多[分析器](https://www.baeldung.com/java-profilers)，既有商业的，也有开源的。[Java Flight Recorder](https://www.baeldung.com/java-flight-recorder-monitoring)与Java Mission Control和Diagnostic Command Tool一起构成了一套工具，可帮助我们[直观地解决](https://www.baeldung.com/java-flight-recorder-monitoring)此类问题。

### 4.2 运行线程分析

如果分析器不可用，**我们可以进行一些线程分析以确定罪魁祸首**。我们可以使用不同的工具，具体取决于主机操作系统和环境，但一般来说，有两个步骤：

1.  使用显示所有正在运行的线程及其PID和CPU百分比的工具来识别罪魁祸首线程
2.  使用显示所有线程及其当前堆栈信息的JVM工具来查找罪魁祸首PID

一个这样的工具是Linux top命令。如果我们使用top命令，我们可以查看当前正在运行的进程，其中包括我们的Java进程：

```shell
PID  USER       PR  NI    VIRT    RES    SHR S  %CPU  %MEM     TIME+ COMMAND
3296 User       20   0 6162828   1.9g  25668 S 806.3  25.6   0:30.88 java
```

我们注意到PID值3296。此视图可帮助我们识别程序中的高CPU使用率，但我们需要进一步挖掘以找出其哪些线程有问题。

运行top -H为我们提供了所有正在运行的线程的列表：

```shell
 PID USER       PR  NI    VIRT    RES    SHR S  %CPU  %MEM     TIME+ COMMAND
3335 User       20   0 6162828   2.0g  26484 R  65.3  26.8   0:02.77 Thread-1
3298 User       20   0 6162828   2.0g  26484 R  64.7  26.8   0:02.94 GC Thread#0
3334 User       20   0 6162828   2.0g  26484 R  64.3  26.8   0:02.74 GC Thread#8
3327 User       20   0 6162828   2.0g  26484 R  64.0  26.8   0:02.93 GC Thread#3
```

我们看到多个GC线程占用了CPU时间，其中一个线程Thread-1的PID为335。

要获取线程转储，我们可以使用[jstack](https://www.baeldung.com/java-thread-dump#1-jstack)。如果我们运行jstack -e 3296，我们会得到程序的线程转储。我们可以通过使用其名称或十六进制的PID来找到Thread-1：

```shell
"Thread-1" #13 prio=5 os_prio=0 cpu=9430.54ms elapsed=171.26s allocated=19256B defined_classes=0 tid=0x00007f673c188000 nid=0xd07 runnable  [0x00007f671c25c000]
   java.lang.Thread.State: RUNNABLE
        at com.baeldung.highcpu.Application.highCPUMethod(Application.java:40)
        at com.baeldung.highcpu.Application.lambda$main$1(Application.java:61)
        at com.baeldung.highcpu.Application$$Lambda$2/0x0000000840061040.run(Unknown Source)
        at java.lang.Thread.run(java.base@11.0.18/Thread.java:829)
```

**请注意，PID 3335对应于十六进制的0xd07，是线程的nid值**。

使用线程转储的堆栈信息，我们现在可以深入研究有问题的代码并开始修复它。

## 5. 总结

在本文中，我们讨论了Java程序中CPU使用率高的潜在根本原因。我们通过一些示例介绍了一些解决这些情况的方法。