## 1. 概述

在本教程中，我们将讨论捕获Java应用程序线程转储的各种方法。

线程转储是Java进程所有线程状态的快照。每个线程的状态都通过堆栈跟踪显示，显示线程堆栈的内容。线程转储可用于诊断问题，因为它显示线程的活动。线程转储以纯文本形式编写，因此我们可以将它们的内容保存到一个文件中，稍后在文本编辑器中查看它们。

在接下来的部分中，我们将通过多种工具和方法来生成线程转储。

## 2. 使用 JDK 实用程序

JDK 提供了几个实用程序，可以捕获Java应用程序的线程转储。所有实用程序都位于JDK 主目录中的bin文件夹下。因此，只要这个目录在我们的系统路径中，我们就可以从命令行执行这些实用程序。

### 2.1. 堆栈

[jstack](https://docs.oracle.com/en/java/javase/11/tools/jstack.html)是一个命令行 JDK 实用程序，我们可以使用它来捕获线程转储。它获取进程的pid并在控制台中显示线程转储。或者，我们可以将其输出重定向到一个文件。

让我们看一下使用 jstack 捕获线程转储的基本命令语法：

```shell
jstack [-F] [-l] [-m] <pid>
```

所有标志都是可选的。让我们看看他们的意思：

-   -F选项强制线程转储；当jstack pid没有响应时使用方便(进程挂起)
-   -l选项指示实用程序在堆和锁中查找可拥有的同步器
-   除了Java堆栈帧之外，-m 选项还打印本机堆栈帧(C 和 C++)

让我们通过捕获线程转储并将结果重定向到文件来使用这些知识：

```shell
jstack 17264 > /tmp/threaddump.txt
```

请记住，我们可以 使用 [jps ](https://docs.oracle.com/en/java/javase/11/tools/jps.html)命令轻松获取Java进程的pid。

### 2.2. Java任务控制

[Java Mission Control](https://docs.oracle.com/javacomponents/jmc-5-5/jmc-user-guide/intro.htm#JMCCI109) (JMC) 是一种 GUI 工具，用于收集和分析来自Java应用程序的数据。启动 JMC 后，它会显示在本地机器上运行的Java进程列表。我们还可以通过 JMC 连接到远程Java进程。

我们可以在进程上右击，点击“开始飞行记录”选项。在此之后，线程选项卡显示线程转储：

[![江铃汽车](https://www.baeldung.com/wp-content/uploads/2020/03/JMC-1024x544-1.png)](https://www.baeldung.com/wp-content/uploads/2020/03/JMC-1024x544-1.png)

### 2.3. jvisualvm

[jvisualvm](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jvisualvm.html)是一个带有图形用户界面的工具，可以让我们监控、排除故障和分析Java应用程序。GUI 很简单，但非常直观且易于使用。

它的众多选项之一允许我们捕获线程转储。如果我们右键单击一个Java进程并选择“线程转储”选项，该工具将创建一个线程转储并在新选项卡中打开它：

[![JVisual虚拟机](https://www.baeldung.com/wp-content/uploads/2020/03/JVisualVM.png)](https://www.baeldung.com/wp-content/uploads/2020/03/JVisualVM.png)

从 JDK 9 开始，Visual VM 不包含在 Oracle JDK 和 Open JDK 发行版中。[因此，如果我们使用的是Java9 或更新版本，我们可以从 Visual VM 开源项目站点](https://visualvm.github.io/)获取 JVisualVM 。

### 2.4. jcmd

[jcmd](https://docs.oracle.com/en/java/javase/11/tools/jcmd.html)是一个通过向 JVM 发送命令请求来工作的工具。尽管功能强大，但它不包含任何远程功能；我们必须在运行Java进程的同一台机器上使用它。

它的众多命令之一是 Thread.print。我们可以通过指定进程的pid来使用它来获取线程转储：

```shell
jcmd 17264 Thread.print
```

### 2.5. 控制台

[jconsole](https://docs.oracle.com/en/java/javase/11/management/using-jconsole.html)让我们检查每个线程的堆栈跟踪。如果我们打开jconsole 并连接到一个正在运行的Java进程， 我们可以导航到Threads选项卡并找到每个线程的堆栈跟踪：

[![J控制台](https://www.baeldung.com/wp-content/uploads/2020/03/JConsole-1024x544-1.png)](https://www.baeldung.com/wp-content/uploads/2020/03/JConsole-1024x544-1.png)

### 2.6. 概括

事实证明，有许多方法可以使用 JDK 实用程序捕获线程转储。让我们花点时间来反思一下，并概述它们的优缺点：

-   jstack：提供最快和最简单的方法来捕获线程转储；但是，从Java8 开始可以使用更好的替代方案
-   jmc：增强的 JDK 分析和诊断工具。它最大限度地减少了通常是分析工具问题的性能开销。
-   jvisualvm：具有出色 GUI 控制台的轻量级开源分析工具
-   jcmd：非常强大，推荐用于Java8 及更高版本。具有多种用途的单一工具：捕获线程转储 ( jstack )、堆转储 ( jmap )、系统属性和命令行参数 ( jinfo )
-   jconsole：让我们检查线程堆栈跟踪信息

## 3.从命令行

在企业应用程序服务器中，出于安全原因，仅安装 JRE。因此，我们不能使用上述实用程序，因为它们是 JDK 的一部分。但是，有各种命令行替代方案可以让我们轻松捕获线程转储。

### 3.1. kill -3 命令(Linux/Unix)

在类 Unix 系统中捕获线程转储的最简单方法是通过[kill ](https://linux.die.net/man/3/kill)命令，我们可以使用该命令向使用kill() 系统调用的进程发送信号。在这个用例中，我们将向它发送-3信号。

使用我们之前示例中的相同pid，让我们看一下如何使用kill捕获线程转储：

```shell
kill -3 17264
```

这样，接收信号的Java进程将在标准输出上打印线程转储。

如果我们使用以下调整标志组合运行Java进程，那么它还会将线程转储重定向到给定文件：

```bash
-XX:+UnlockDiagnosticVMOptions -XX:+LogVMOutput -XX:LogFile=~/jvm.log
```

现在，如果我们发送 -3 信号，除了标准输出之外，转储将在 ~/jvm.log 文件中可用。

### 3.2. Ctrl + 中断 (Windows)

在 Windows 操作系统中，我们可以使用CTRL和Break 组合键捕获线程转储。要获取线程转储，请导航至用于启动Java应用程序的控制台，然后同时按下CTRL和Break 键。

值得注意的是，在某些键盘上，Break键不可用。因此，在这种情况下，可以同时使用CTRL、SHIFT和Pause键来捕获线程转储。

这两个命令都将线程转储打印到控制台。

## 4. 以编程方式使用ThreadMxBean

我们将在本文中讨论的最后一种方法是使用[JMX](https://www.baeldung.com/java-management-extensions)。我们将使用ThreadMxBean来捕获线程转储。让我们在代码中看到它：

```java
private static String threadDump(boolean lockedMonitors, boolean lockedSynchronizers) {
    StringBuffer threadDump = new StringBuffer(System.lineSeparator());
    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    for(ThreadInfo threadInfo : threadMXBean.dumpAllThreads(lockedMonitors, lockedSynchronizers)) {
        threadDump.append(threadInfo.toString());
    }
    return threadDump.toString();
}
```

在上面的程序中，我们执行了几个步骤：

1.  首先初始化一个空的StringBuffer ，用来保存各个线程的栈信息。
2.  然后我们使用ManagementFactory类来获取 ThreadMxBean 的实例。ManagementFactory是为Java平台获取托管 bean 的工厂类。 另外，ThreadMxBean 是JVM线程系统的管理接口。
3.  将lockedMonitors和lockedSynchronizers值设置为true表示在线程转储中捕获可拥有的同步器和所有锁定的监视器。

## 5.总结

在本文中，我们学习了多种捕获线程转储的方法。

首先，我们讨论了各种 JDK 实用程序，然后是命令行选项。最后，我们总结了使用 JMX 的编程方法。