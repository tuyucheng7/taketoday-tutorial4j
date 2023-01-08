## 1. 概述

在本教程中，我们将研究JavaFlight Recorder、其概念、基本命令以及如何使用它。

## 2.Java监控实用程序

Java 不仅是一种编程语言，还是一个拥有大量工具的非常丰富的生态系统。JDK 包含的程序允许我们编译自己的程序，以及在程序执行的整个生命周期中监视它们的状态和Java虚拟机的状态。

JDK 发行版的bin文件夹包含以下可用于分析和监控的程序：

-   Java VisualVM (jvisualvm.exe)
-   JConsole (jconsole.exe)
-   Java 任务控制(jmc.exe)
-   诊断命令工具(jcmd.exe)

我们建议探索此文件夹的内容，以了解我们拥有哪些工具可供使用。请注意，Java VisualVM 过去是 Oracle 和 Open JDK 发行版的一部分。但是，从Java9 开始，JDK 发行版不再附带JavaVisualVM。[因此，我们应该从VisualVM开源项目网站](https://visualvm.github.io/)单独下载。

在本教程中，我们将重点关注JavaFlight Recorder。这在上面提到的工具中不存在，因为它不是一个独立的程序。它的用法与上面的两个工具密切相关——Java Mission Control 和 Diagnostic Command Tools。

## 3.JavaFlight Recorder及其基本概念

Java Flight Recorder (JFR) 是一种监视工具，用于收集有关Java应用程序执行期间Java虚拟机 (JVM) 中事件的信息。JFR 是 JDK 发行版的一部分，并集成到 JVM 中。

JFR 旨在尽可能少地影响正在运行的应用程序的性能。

为了使用JFR，我们应该激活它。我们可以通过两种方式实现这一目标：

1.  启动Java应用程序时
2.  当Java应用程序已在运行时传递jcmd工具的诊断命令

JFR 没有独立的工具。我们使用JavaMission Control (JMC)，它包含一个插件，可以让我们可视化 JFR 收集的数据。

这三个组件——JFR 、 jcmd和JMC——构成 了一个完整的套件，用于收集正在运行的Java程序的低级运行时信息。我们可能会发现这些信息在优化我们的程序时非常有用，或者在出现问题时对其进行诊断。

如果我们的计算机上安装了多个版本的 Java，请务必 确保Java编译器 ( javac )、Java 启动器 ( java ) 和上述工具(JFR、jcmd 和 JMC)来自同一个Java发行版. 否则，由于不同版本的 JFR 数据格式可能不兼容，因此存在看不到任何有用数据的风险。

JFR 有两个主要概念：事件和数据流。让我们简要地讨论一下。

### 3.1. 事件

JFR 收集Java应用程序运行时 JVM 中发生的事件。这些事件与 JVM 本身的状态或程序的状态有关。事件具有名称、时间戳和其他信息(如线程信息、执行堆栈和堆状态)。

JFR 收集三种类型的事件：

-   即时事件一旦发生就会立即记录下来
-   如果持续时间超过指定阈值，则记录持续时间事件
-   示例事件用于对系统活动进行采样

### 3.2. 数据流

JFR 收集的事件包含大量数据。出于这个原因，JFR 的设计速度足够快，不会妨碍程序。

JFR 将有关事件的数据保存在单个输出文件 flight.jfr 中。 

众所周知，磁盘 I/O 操作非常昂贵。因此，JFR 在将数据块刷新到磁盘之前使用各种缓冲区来存储收集的数据。事情可能会变得有点复杂，因为在同一时刻，一个程序可能有多个具有不同选项的注册进程。

正因为如此，我们可能会在输出文件中发现比请求更多的数据，或者它可能不是按时间顺序排列的。如果我们使用 JMC，我们甚至可能不会注意到这个事实，因为它按时间顺序可视化事件。

在极少数情况下，JFR 可能无法刷新数据(例如，当事件过多或停电时)。如果发生这种情况，JFR 会尝试通知我们输出文件可能丢失了一段数据。

## 4.如何使用Java飞行记录器

JFR 是一项实验性功能，因此其使用可能会发生变化。事实上，在早期的发行版中，我们必须激活商业功能才能在生产中使用它。但是，从 JDK 11 开始，我们可以在不激活任何东西的情况下使用它。我们可以随时查阅Java官方发行说明来了解如何使用该工具。

对于 JDK 8，为了能够激活 JFR，我们应该使用+UnlockCommercialFeatures和 +FlightRecorder选项启动 JVM 。

如上所述，有两种激活 JFR 的方法。当我们在启动应用程序的同时激活它时，我们是从命令行执行的。当应用程序已经运行时，我们使用诊断命令工具。

### 4.1. 命令行

 首先，我们使用标准的 java 编译器javac将程序的 .java文件编译成.class。

编译成功后，我们可以使用以下选项启动程序：

```java
java -XX:+UnlockCommercialFeatures -XX:+FlightRecorder 
  -XX:StartFlightRecording=duration=200s,filename=flight.jfr path-to-class-file
```

其中path-to-class-file 是应用程序的入口点.class文件。

此命令启动应用程序并激活录制，录制立即开始，持续时间不超过 200 秒。收集的数据保存在输出文件 flight.jfr中。我们将在下一节中更详细地描述其他选项。

### 4.2. 诊断命令工具

我们还可以使用jcmd工具开始注册事件 。例如：

```java
jcmd 1234 JFR.start duration=100s filename=flight.jfr
```

在 JDK 11 之前，为了能够以这种方式激活 JFR，我们应该以解锁的商业功能启动应用程序：

```java
java -XX:+UnlockCommercialFeatures -XX:+FlightRecorder -cp ./out/ com.baeldung.Main
```

应用程序运行后，我们使用其进程 ID 来执行各种命令，这些命令采用以下格式：

```java
jcmd <pid|MainClass> <command> [parameters]
```

以下是诊断命令的完整列表：

-   JFR.start—— 开始一个新的 JFR 记录
-   JFR.check—— 检查正在运行的 JFR 记录
-   JFR.stop—— 停止特定的 JFR 记录
-   JFR.dump – 将 JFR 记录的内容到文件

每个命令都有一系列参数。例如， JFR.start命令具有以下参数：

-   名称 – 录音的名称；它用于稍后使用其他命令引用此记录
-   delay – 记录开始时间延迟的量纲参数，默认值为0s
-   持续时间 ——记录持续时间的时间间隔的维度参数；默认值为0s，表示无限制
-   文件名 ——包含所收集数据的文件的名称
-   maxage—— 收集数据的最大年龄的维度参数；默认值为0s，表示无限制
-   maxsize – 收集数据的最大缓冲区大小(以字节为单位)；默认值为 0，表示没有最大尺寸

我们已经在本节开头看到了这些参数的用法示例。完整的参数列表，我们可以随时查阅[Java Flight Recorded 官方文档](https://docs.oracle.com/javacomponents/jmc-5-4/jfr-runtime-guide/comline.htm#JFRUH190)。

尽管 JFR 旨在尽可能减少对 JVM 和应用程序性能的影响，但最好通过至少设置以下参数之一来限制收集数据的最大量： duration、maxage或maxsize。

## 5.Java飞行记录器实战

现在让我们使用一个示例程序来演示 JFR 的实际应用。

### 5.1. 示例程序

我们的程序将对象插入到列表中，直到 发生OutOfMemoryError。然后程序休眠一秒钟：

```java
public static void main(String[] args) {
    List<Object> items = new ArrayList<>(1);
    try {
        while (true){
            items.add(new Object());
        }
    } catch (OutOfMemoryError e){
        System.out.println(e.getMessage());
    }
    assert items.size() > 0;
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        System.out.println(e.getMessage());
    }
}
```

如果不执行这段代码，我们可以发现一个潜在的缺点：while 循环会导致高 CPU 和内存使用率。让我们使用 JFR 来了解这些缺点并可能找到其他缺点。

### 5.2. 开始注册

首先，我们通过从命令行执行以下命令来编译我们的程序：

```java
javac -d out -sourcepath src/main src/main/com/baeldung/flightrecorder/FlightRecorder.java
```

此时，我们应该在 out/com/baeldung/flightrecorder目录下找到一个文件FlightRecorder.class。

现在，我们将使用以下选项启动程序：

```java
java -XX:+UnlockCommercialFeatures -XX:+FlightRecorder 
  -XX:StartFlightRecording=duration=200s,filename=flight.jfr 
  -cp ./out/ flightrecorder.cn.tuyucheng.taketoday.FlightRecorder
```

### 5.3. 可视化数据

现在，我们将文件flight.jfr提供 给JavaMission Control，它是 JDK 分发的一部分。它帮助我们以一种直观的方式可视化有关事件的数据。

它的主屏幕向我们展示了程序在执行期间如何使用 CPU 的信息。我们看到 CPU 负载很重，由于 while 循环，这是意料之中的事情：

[![主画面.png](https://www.baeldung.com/wp-content/uploads/2019/01/main-screen.png)](https://www.baeldung.com/wp-content/uploads/2019/01/main-screen.png)

在视图的左侧，我们看到了 General、Memory、Code和 Threads等部分。每个部分都包含带有详细信息的各种选项卡。例如，Code 部分的Hot Methods选项卡包含方法调用的统计信息：

[![码屏热点方法](https://www.baeldung.com/wp-content/uploads/2019/01/code-screen-hot-methods.png)](https://www.baeldung.com/wp-content/uploads/2019/01/code-screen-hot-methods.png)

在此选项卡中，我们可以发现示例程序的另一个缺点：方法java.util.ArrayList.grow(int)已被调用 17 次，以便在每次没有足够空间添加对象时扩大数组容量。

在更现实的程序中，我们可能会看到很多其他有用的信息：

-   有关已创建对象的统计信息，当它们被垃圾收集器创建和销毁时
-   关于线程时间顺序的详细报告，当它们被锁定或激活时
-   应用程序正在执行哪些 I/O 操作

## 六，总结

在本文中，我们介绍了使用JavaFlight Recorder 监视和分析Java应用程序的主题。该工具仍处于实验阶段，因此我们应该访问其[官方网站](https://docs.oracle.com/javacomponents/jmc-5-4/jfr-runtime-guide/toc.htm)以获取更完整和最新的信息。