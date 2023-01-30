## 1. 概述

应用程序有时会挂起或运行缓慢，确定根本原因并不总是一项简单的任务。线程转储提供 了正在运行的 Java 进程的当前状态的快照。但是，生成的数据包括多个长文件。因此，我们需要分析 Java 线程转储并在大量不相关的信息中挖掘问题。 

在本教程中，我们将了解如何过滤掉该数据以有效诊断性能问题。此外，我们将学习检测瓶颈甚至简单的错误。

## 2. JVM中的线程

JVM 使用线程来执行每个内部和外部操作。正如我们所知，垃圾收集进程有自己的线程，而且 Java 应用程序内部的任务也创建自己的线程。

在其生命周期中，线程会经历[多种状态](https://www.baeldung.com/java-thread-lifecycle)。每个线程都有一个跟踪当前操作的执行堆栈。此外，JVM 还存储了之前调用成功的所有方法。因此，可以分析完整的堆栈以研究出现问题时应用程序发生了什么。

为了展示本教程的主题，我们将使用一个简单的[发送方-接收方应用程序 ( NetworkDriver )](https://www.baeldung.com/java-wait-notify#sender-receiver-synchronization-problem)作为示例。Java 程序发送和接收数据包，因此我们能够分析幕后发生的事情。

### 2.1. 捕获 Java 线程转储

应用程序运行后，有多种方法可以[生成](https://www.baeldung.com/java-thread-dump) 用于诊断的 Java 线程转储。在本教程中，我们将使用 JDK7+ 安装中包含的两个实用程序。首先，我们将执行[JVM 进程状态 (jps)](https://docs.oracle.com/en/java/javase/11/tools/jps.html)命令来发现我们应用程序的 PID 进程：

```bash
$ jps 
80661 NetworkDriver
33751 Launcher
80665 Jps
80664 Launcher
57113 Application

```

其次，我们获取应用程序的 PID，在本例中为NetworkDriver 旁边的 PID。然后，我们将使用[jstack](https://docs.oracle.com/en/java/javase/11/tools/jstack.html)捕获线程转储。最后，我们将结果存储在一个文本文件中：

```bash
$ jstack -l 80661 > sender-receiver-thread-dump.txt
```

### 2.2. 示例转储的结构

让我们看一下生成的线程转储。第一行显示时间戳，第二行显示 JVM：

```plaintext
2021-01-04 12:59:29
Full thread dump OpenJDK 64-Bit Server VM (15.0.1+9-18 mixed mode, sharing):
```

下一节显示安全内存回收 (SMR) 和非 JVM 内部线程：

```plaintext
Threads class SMR info:
_java_thread_list=0x00007fd7a7a12cd0, length=13, elements={
0x00007fd7aa808200, 0x00007fd7a7012c00, 0x00007fd7aa809800, 0x00007fd7a6009200,
0x00007fd7ac008200, 0x00007fd7a6830c00, 0x00007fd7ab00a400, 0x00007fd7aa847800,
0x00007fd7a6896200, 0x00007fd7a60c6800, 0x00007fd7a8858c00, 0x00007fd7ad054c00,
0x00007fd7a7018800
}
```

然后，转储显示线程列表。每个线程包含以下信息：

-   名称： 如果开发人员包含有意义的线程名称，它可以提供有用的信息
-   优先级(prior)：线程的优先级
-   Java ID(tid)：JVM给定的唯一ID
-   本机 ID (nid)：操作系统给出的唯一 ID，有助于提取与 CPU 或内存处理的相关性
-   state：线程 的[实际状态](https://www.baeldung.com/java-thread-lifecycle)
-   堆栈跟踪： 破译我们的应用程序正在发生的事情的最重要的信息来源

我们可以从上到下看到快照时不同线程在做什么。让我们只关注堆栈中等待使用消息的有趣部分：

```plaintext
"Monitor Ctrl-Break" #12 daemon prio=5 os_prio=31 cpu=17.42ms elapsed=11.42s tid=0x00007fd7a6896200 nid=0x6603 runnable  [0x000070000dcc5000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.SocketDispatcher.read0(java.base@15.0.1/Native Method)
	at sun.nio.ch.SocketDispatcher.read(java.base@15.0.1/SocketDispatcher.java:47)
	at sun.nio.ch.NioSocketImpl.tryRead(java.base@15.0.1/NioSocketImpl.java:261)
	at sun.nio.ch.NioSocketImpl.implRead(java.base@15.0.1/NioSocketImpl.java:312)
	at sun.nio.ch.NioSocketImpl.read(java.base@15.0.1/NioSocketImpl.java:350)
	at sun.nio.ch.NioSocketImpl$1.read(java.base@15.0.1/NioSocketImpl.java:803)
	at java.net.Socket$SocketInputStream.read(java.base@15.0.1/Socket.java:981)
	at sun.nio.cs.StreamDecoder.readBytes(java.base@15.0.1/StreamDecoder.java:297)
	at sun.nio.cs.StreamDecoder.implRead(java.base@15.0.1/StreamDecoder.java:339)
	at sun.nio.cs.StreamDecoder.read(java.base@15.0.1/StreamDecoder.java:188)
	- locked <0x000000070fc949b0> (a java.io.InputStreamReader)
	at java.io.InputStreamReader.read(java.base@15.0.1/InputStreamReader.java:181)
	at java.io.BufferedReader.fill(java.base@15.0.1/BufferedReader.java:161)
	at java.io.BufferedReader.readLine(java.base@15.0.1/BufferedReader.java:326)
	- locked <0x000000070fc949b0> (a java.io.InputStreamReader)
	at java.io.BufferedReader.readLine(java.base@15.0.1/BufferedReader.java:392)
	at com.intellij.rt.execution.application.AppMainV2$1.run(AppMainV2.java:61)

   Locked ownable synchronizers:
	- <0x000000070fc8a668> (a java.util.concurrent.locks.ReentrantLock$NonfairSync)
```

乍一看，我们看到主堆栈跟踪正在执行 java.io.BufferedReader.readLine，这是预期的行为。如果我们进一步向下看，我们将看到我们的应用程序在幕后执行的所有 JVM 方法。因此，我们可以通过查看源代码或其他 JVM 内部处理来确定问题的根源。

在转储结束时，我们会注意到有几个额外的线程 在执行后台操作，例如垃圾收集 (GC) 或对象 终止：

```plaintext
"VM Thread" os_prio=31 cpu=1.85ms elapsed=11.50s tid=0x00007fd7a7a0c170 nid=0x3603 runnable  
"GC Thread#0" os_prio=31 cpu=0.21ms elapsed=11.51s tid=0x00007fd7a5d12990 nid=0x4d03 runnable  
"G1 Main Marker" os_prio=31 cpu=0.06ms elapsed=11.51s tid=0x00007fd7a7a04a90 nid=0x3103 runnable  
"G1 Conc#0" os_prio=31 cpu=0.05ms elapsed=11.51s tid=0x00007fd7a5c10040 nid=0x3303 runnable  
"G1 Refine#0" os_prio=31 cpu=0.06ms elapsed=11.50s tid=0x00007fd7a5c2d080 nid=0x3403 runnable  
"G1 Young RemSet Sampling" os_prio=31 cpu=1.23ms elapsed=11.50s tid=0x00007fd7a9804220 nid=0x4603 runnable  
"VM Periodic Task Thread" os_prio=31 cpu=5.82ms elapsed=11.42s tid=0x00007fd7a5c35fd0 nid=0x9903 waiting on condition
```

最后，转储显示 Java 本机接口 (JNI) 引用。当发生内存泄漏时，我们应该特别注意这一点，因为它们不会自动被垃圾收集：

```plaintext
JNI global refs: 15, weak refs: 0
```

线程转储的结构非常相似，但我们希望摆脱为我们的用例生成的不重要数据。另一方面，我们需要保留和分组堆栈跟踪生成的大量日志中的重要信息。让我们看看如何去做！

## 3. 分析线程转储的建议

为了了解我们的应用程序发生了什么，我们需要有效地分析生成的快照。我们将获得大量信息 ，其中包含转储时所有线程的精确数据。但是，我们需要整理日志文件，进行一些过滤和分组以从堆栈跟踪中提取有用的提示。准备好转储后，我们将能够使用不同的工具分析问题。让我们看看如何破译示例转储的内容。

### 3.1. 同步问题

过滤堆栈跟踪的一个有趣技巧是线程的状态。我们将主要关注 RUNNABLE 或 BLOCKED 线程，最后是 TIMED_WAITING 线程 。这些状态将向我们指出两个或多个线程之间发生冲突的方向：

-   在死锁 情况下，多个线程运行在共享对象上持有一个同步块
-   在线程争用中，当一个 线程被阻塞等待其他线程完成时。比如上一节生成的dump

### 3.2. 执行问题

根据经验，对于异常高的 CPU 使用率，我们只需要查看 RUNNABLE 线程。我们将使用线程转储和其他命令来获取额外信息。其中一个命令是[top](https://www.baeldung.com/linux/top-command) -H -p PID，它显示哪些线程正在消耗该特定进程中的操作系统资源。我们还需要查看内部 JVM 线程，例如 GC，以防万一。另一方面，当处理性能异常低下时，我们将查看 BLOCKED 线程。

在这些情况下，单个转储肯定不足以了解正在发生的事情。我们需要 间隔很近的大量转储，以便比较不同时间相同线程的堆栈。一方面，一张快照并不总是足以找出问题的根源。另一方面，我们需要避免快照之间的噪音(信息太多)。

要了解线程随时间的演变，推荐的最佳做法是 至少进行 3 次转储，每 10 秒一次。另一个有用的技巧是将转储分成小块以避免加载文件时崩溃。

### 3.3. 建议

为了有效地破译问题的根源，我们需要在堆栈跟踪中组织大量信息。因此，我们将考虑以下建议：

-   在执行问题中，以 10 秒的间隔捕获多个快照将有助于专注于实际问题。如果需要，还建议拆分文件以避免加载崩溃
-   在创建新线程时使用命名以更好地识别你的源代码
-   根据问题，忽略内部 JVM 处理(例如 GC)
-   发出异常 CPU 或内存使用情况时，关注长时间运行或阻塞的线程
-   使用top -H -p PID将线程的堆栈与 CPU 处理相关联
-   最重要的是，使用分析器工具

手动分析 Java 线程转储可能是一项乏味的活动。对于简单的应用程序，可以确定产生问题的线程。另一方面，对于复杂的情况，我们需要工具来简化这项任务。我们将在下一节中展示如何使用这些工具，使用为示例线程争用生成的转储。

## 4.在线工具

有几种在线工具可用。在使用这类软件时我们需要考虑到安全问题。请记住， 我们可能会与第三方实体共享日志。

### 4.1. 快线

[FastThread](https://fastthread.io/)可能是分析生产环境线程转储的最佳在线工具。它提供了一个非常好的图形用户界面。它还包括多种功能，例如线程的 CPU 使用率、堆栈长度以及最常用和最复杂的方法：

[![快速线程](https://www.baeldung.com/wp-content/uploads/2021/01/fast-thread.png)](https://www.baeldung.com/wp-content/uploads/2021/01/fast-thread.png)

FastThread 结合了 REST API 功能来自动分析线程转储。使用简单的 cURL 命令，可以立即发送结果。主要缺点是安全性，因为它将 堆栈跟踪存储在云中。

### 4.2. JStack 评论

[JStack Review](https://jstack.review/)是一个分析浏览器中的转储的在线工具。它只是客户端，因此没有数据存储在你的计算机之外。从安全角度来看，这是使用它的一大优势。它提供所有线程的图形概览，显示正在运行的方法，还按状态对它们进行分组。JStack Review 将产生堆栈的线程与其余线程分开，这对于忽略非常重要，例如内部进程。最后，它还包括同步器和忽略的行：

[![堆栈](https://www.baeldung.com/wp-content/uploads/2021/01/jstack.png)](https://www.baeldung.com/wp-content/uploads/2021/01/jstack.png)

### 4.3. Spotify 在线 Java 线程转储 分析器

[Spotify Online Java Thread Dump Analyzer](https://spotify.github.io/threaddump-analyzer/)是一个用 JavaScript 编写的在线开源工具。它以纯文本显示结果，将线程与堆栈分开。它还显示正在运行的线程中最常用的方法：

[![Spotify 线程转储](https://www.baeldung.com/wp-content/uploads/2021/01/spotify-thread-dump.png)](https://www.baeldung.com/wp-content/uploads/2021/01/spotify-thread-dump.png)

## 5. 独立应用

我们还可以在本地使用几个独立的应用程序。

### 5.1. J型材

[JProfiler](https://www.ej-technologies.com/products/jprofiler/overview.html)是市场上最强大的工具，在 Java 开发人员社区中广为人知。可以使用 10 天试用许可证来测试功能。JProfiler 允许创建配置文件并将正在运行的应用程序附加到它们。它包括多种功能，可在现场识别问题，例如 CPU 和内存使用情况以及数据库分析。它还支持与 IDE 集成：

[![分析器](https://www.baeldung.com/wp-content/uploads/2021/01/jprofiler.png)](https://www.baeldung.com/wp-content/uploads/2021/01/jprofiler.png)

### 5.2. 用于 Java 的 IBM 线程监视器和转储分析器 (TMDA)

[IBM TMDA](https://www.ibm.com/support/pages/ibm-thread-and-monitor-dump-analyzer-java-tmda)可用于识别线程争用、死锁和瓶颈。它是免费分发和维护的，但不提供 IBM 的任何保证或支持：

[![IBM 线程监视器](https://www.baeldung.com/wp-content/uploads/2021/01/ibm-thread-monitor.png)](https://www.baeldung.com/wp-content/uploads/2021/01/ibm-thread-monitor.png)

### 5.3. Irockel 线程转储分析器 (TDA)

[Irockel TDA](https://github.com/irockel/tda)是一款获得 LGPL v2.1 许可的独立开源工具。最新版本 (v2.4) 于 2020 年 8 月发布，因此维护良好。它将线程转储显示为一棵树，还提供一些统计信息以简化导航：

[![铁芯 tda](https://www.baeldung.com/wp-content/uploads/2021/01/irockel-tda.png)](https://www.baeldung.com/wp-content/uploads/2021/01/irockel-tda.png)

最后，IDE 支持线程转储的基本分析，因此可以在开发期间调试应用程序。

## 5.总结

在本文中，我们演示了 Java 线程转储分析如何帮助我们查明同步或执行问题。

最重要的是，我们回顾了如何正确分析它们，包括组织快照中嵌入的大量信息的建议。