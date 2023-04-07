## **一、概述**

**有时编写仅能运行的代码是不够的。**我们可能想知道内部发生了什么，例如内存是如何分配的、使用一种编码方法优于另一种编码方法的后果、并发执行的含义、提高性能的领域等。我们可以为此使用分析器。

Java Profiler 是一种**在 JVM 级别监视 Java 字节码构造和操作的**工具。这些代码构造和操作包括对象创建、迭代执行（包括递归调用）、方法执行、线程执行和垃圾收集。

在本教程中，我们将探索主要的 Java Profiler：[JProfiler](https://www.ej-technologies.com/products/jprofiler/overview.html)、[YourKit](https://www.yourkit.com/java/profiler/)、[Java VisualVM](https://visualvm.github.io/)、[Netbeans Profiler](https://netbeans.apache.org/kb/docs/java/profiler-intro.html)和[IntelliJ Profiler](https://lp.jetbrains.com/intellij-idea-profiler/)。

## **2.J档案**

JProfiler 是许多开发人员的首选。通过直观的 UI，JProfiler 提供了用于查看系统性能、内存使用情况、潜在内存泄漏和线程分析的界面。

有了这些信息，我们就可以很容易地看到我们需要在底层系统中优化、消除或改变什么。

此产品需要[购买许可证](https://www.ej-technologies.com/buy/jprofiler/select)，但也提供免费试用。

JProfiler 的界面如下所示：

[![jprofiler 概述探测](https://www.baeldung.com/wp-content/uploads/2017/10/1-jprofiler-overview-probing-1024x793.png)](https://www.baeldung.com/wp-content/uploads/2017/10/1-jprofiler-overview-probing.png)[JProfiler 概览界面及功能](https://www.baeldung.com/wp-content/uploads/2017/10/1-jprofiler-overview-probing.png)

与大多数分析器一样，我们可以将此工具用于本地和远程应用程序。**这意味着无需在远程机器上安装任何东西就**可以分析运行在远程机器上的 Java 应用程序。

**JProfiler 还为 SQL 和 NoSQL 数据库**提供高级分析。它为分析 JDBC、JPA/Hibernate、MongoDB、Casandra 和 HBase 数据库提供特定支持。

下面的屏幕截图显示了带有当前连接列表的 JDBC 探测接口：

[![jprofiler 数据库探测 1](https://www.baeldung.com/wp-content/uploads/2017/10/2-jprofiler-database-probing-1-1024x789.png)](https://www.baeldung.com/wp-content/uploads/2017/10/2-jprofiler-database-probing-1.png)[JProfiler 数据库探测视图](https://www.baeldung.com/wp-content/uploads/2017/10/2-jprofiler-database-probing-1.png)

如果我们热衷于了解**与我们的数据库交互的调用树**并查看**可能泄漏的连接**，JProfiler 可以很好地处理这个问题。

Live Memory 是 JProfiler 的一项功能，它允许我们**查看应用程序当前的内存使用情况**。我们可以查看对象声明和实例或完整调用树的内存使用情况。

对于分配调用树，我们可以选择查看活动对象、垃圾收集对象或两者的调用树。我们还可以决定此分配树是否应该用于特定类或包，或所有类。

下面的屏幕显示了所有具有实例计数的对象的实时内存使用情况：

[![jprofiler 实时内存](https://www.baeldung.com/wp-content/uploads/2017/10/3-jprofiler-live-memory-1024x795.png)](https://www.baeldung.com/wp-content/uploads/2017/10/3-jprofiler-live-memory.png)[JProfiler 实时内存视图](https://www.baeldung.com/wp-content/uploads/2017/10/3-jprofiler-live-memory.png)

JProfiler 支持**与流行的 IDE 集成，**例如 Eclipse、NetBeans 和 IntelliJ。甚至可以**从快照导航到源代码。**

## **3.你的工具包**

YourKit Java Profiler 在许多不同的平台上运行，并为每个支持的操作系统（Windows、MacOS、Linux、Solaris、FreeBSD 等）提供单独的安装。

与 JProfiler 一样，YourKit 具有可视化线程、垃圾收集、内存使用和内存泄漏的核心功能，并**支持通过 ssh 隧道进行本地和远程分析**。

YourKit 提供用于商业用途的[付费许可证](https://www.yourkit.com/java/profiler/purchase/)，其中包括免费试用，以及用于非商业用途的低成本或免费许可证。

下面是 Tomcat 服务器应用程序的内存分析结果的快速浏览：

[![yourkit tomcat 分析内存](https://www.baeldung.com/wp-content/uploads/2017/10/4-yourkit-tomcat-profiling-memory-1024x674.png)](https://www.baeldung.com/wp-content/uploads/2017/10/4-yourkit-tomcat-profiling-memory.png)[YourKit Java Profiler Tomcat 服务器应用程序的内存分析](https://www.baeldung.com/wp-content/uploads/2017/10/4-yourkit-tomcat-profiling-memory.png)

当我们想要**分析抛出的异常**时，YourKit 也能派上用场。我们可以很容易地找出抛出的异常类型，以及每个异常发生的次数。

YourKit 有一个有趣的**CPU 分析功能，允许集中分析我们代码的某些区域，**例如线程中的方法或子树。这是非常强大的，因为它允许通过其假设功能进行条件分析。

图 5 显示了线程分析接口的示例：

[![yourkit线程分析](https://www.baeldung.com/wp-content/uploads/2017/10/5-yourkit-threads-profiling-1024x789.png)](https://www.baeldung.com/wp-content/uploads/2017/10/5-yourkit-threads-profiling.png)[图 5. YourKit Java Profiler 线程分析界面](https://www.baeldung.com/wp-content/uploads/2017/10/5-yourkit-threads-profiling.png)

我们还可以使用 YourKit**分析 SQL 和 NoSQL 数据库调用**。它甚至提供了已执行的实际查询的视图。

虽然这不是技术考虑，但 YourKit 的宽松许可模型使其成为多用户或分布式团队以及单一许可购买的不错选择。

## **4. Java 可视化虚拟机**

Java VisualVM 是一种用于 Java 应用程序的简化但强大的分析工具。这是一个免费的开源分析器。

该工具在 JDK 8 之前**与 Java 开发工具包 (JDK) 捆绑在一起，但在 JDK 9 中被删除，现在作为独立工具分发：** [VisualVM 下载](https://visualvm.github.io/download.html)。

它的操作依赖于 JDK 中提供的其他独立工具，例如*JConsole*、*jstat*、*jstack*、*jinfo*和*jmap*。

下面我们可以看到一个使用 Java VisualVM 进行的持续分析会话的简单概览界面：

[![视觉虚拟机概览](https://www.baeldung.com/wp-content/uploads/2017/10/6-visualvm-overview-1024x582.png)](https://www.baeldung.com/wp-content/uploads/2017/10/6-visualvm-overview.png)[Java VisualVM 本地 tomcat 服务器应用程序分析](https://www.baeldung.com/wp-content/uploads/2017/10/6-visualvm-overview.png)

Java VisualVM 的一个有趣的优点是我们可以**扩展它以开发新的功能作为插件**。然后我们可以将这些插件添加到 Java VisualVM 内置的更新中心。

Java VisualVM 支持**本地和远程分析**，以及内存和 CPU 分析。**连接到远程应用程序需要提供凭据**（必要时提供主机名/IP 和密码），**但不提供对 ssh 隧道的支持**。我们还可以选择**通过即时更新**（通常每 2 秒一次）启用实时分析。

下面我们可以看到使用 Java VisualVM 分析的 Java 应用程序的内存前景：

[![visualvm 示例内存](https://www.baeldung.com/wp-content/uploads/2017/10/7-visualvm-sample-memory-1024x583.png)](https://www.baeldung.com/wp-content/uploads/2017/10/7-visualvm-sample-memory.png)[Java VisualVM 内存堆直方图](https://www.baeldung.com/wp-content/uploads/2017/10/7-visualvm-sample-memory.png)

 

借助 Java VisualVM 的快照功能，我们可以**对分析会话进行快照以供以后分析**。

## **5.NetBeans 探查器**

NetBeans Profiler**与 Oracle 的开源 NetBeans IDE 捆绑在一起**。

虽然此分析器**与 Java VisualVM 有很多相似之处**，但当我们希望将所有内容都包装在一个程序（IDE + Profiler）中时，它是一个不错的选择。上面讨论的所有其他分析器都提供插件来增强 IDE 集成。

下面的屏幕截图显示了 NetBeans Profiler 界面的示例：

[![netbeans 遥测视图](https://www.baeldung.com/wp-content/uploads/2017/10/8-netbeans-telemetry-view-1024x564.png)](https://www.baeldung.com/wp-content/uploads/2017/10/8-netbeans-telemetry-view.png)[Netbeans Profiler 遥测接口](https://www.baeldung.com/wp-content/uploads/2017/10/8-netbeans-telemetry-view.png)

Netbeans Profiler 也是**轻量级开发和分析的不错选择**。它提供了一个单一的窗口来配置和控制分析会话并显示结果。**它提供了解垃圾收集发生频率**的独特功能。

## 6.IntelliJ 探查器

[IntelliJ Profiler](https://blog.jetbrains.com/idea/2020/03/profiling-tools-and-intellij-idea-ultimate/)是一个简单但功能强大的 CPU 和内存分配分析工具。它结合了两种流行的 Java 分析器的强大功能：[JFR](https://www.baeldung.com/java-flight-recorder-monitoring)和异步分析器。

虽然有一些高级功能，但主要重点是易用性。IntelliJ Profiler 让我们只需点击几下即可开始，无需任何配置，同时提供有用的功能来协助我们的日常开发工作。

作为 IntelliJ IDEA Ultimate 的一部分，**IntelliJ Profiler 可以通过单击附加到进程，**我们可以在快照和源代码之间导航，就好像它们是一个一样。它的其他功能，如微分火焰图，使我们能够直观地评估不同方法的性能，并快速有效地深入了解运行时操作：

-   [![img](https://www.baeldung.com/wp-content/uploads/2017/10/intellij-profiler-dark-1024x651.png)](https://www.baeldung.com/wp-content/uploads/2017/10/intellij-profiler-dark.png)

-   [![img](https://www.baeldung.com/wp-content/uploads/2017/10/intellij-profiler-light-1024x651.png)](https://www.baeldung.com/wp-content/uploads/2017/10/intellij-profiler-light.png)



IntelliJ Profiler 适用于 Windows、Linux 和 macOS。

## **7. 其他实体轮廓仪**

这里值得一提的是[Java Mission Control](http://www.oracle.com/technetwork/java/javaseproducts/mission-control/java-mission-control-1998576.html)、[New Relic](https://newrelic.com/)和[Prefix](https://stackify.com/prefix/)（来自[Stackify](https://stackify.com/)）。这些总体市场份额较小，但绝对值得一提。例如，Stackify 的 Prefix 是一款出色的轻量级分析工具，不仅适用于分析 Java 应用程序，还适用于分析其他 Web 应用程序。

## **八、结论**

在本文中，我们讨论了概要分析和 Java Profiler。我们研究了每个 Profiler 的特性，以及是什么决定了它们之间的潜在选择。

有许多可用的 Java 分析器，其中一些具有独特的特性。正如我们在本文中看到的，选择使用哪种 Java 分析器主要取决于开发人员选择的工具、所需的分析级别以及分析器的特性。