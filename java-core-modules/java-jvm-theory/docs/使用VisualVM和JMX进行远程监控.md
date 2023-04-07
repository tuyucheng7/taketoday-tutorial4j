## **一、简介**

在本文中，我们将学习如何使用 VisualVM 和 Java 管理扩展 (JMX) 来远程监控 Java 应用程序。

## **2.JMX**

JMX 是**用于管理和监视 JVM 应用程序的标准 API**。JVM 具有 JMX 可用于此目的的内置工具。因此，我们通常将这些实用程序称为“开箱即用的管理工具”，或者在本例中称为“JMX 代理”。

## **3.视觉虚拟机**

VisualVM 是一种可视化工具，可为 JVM 提供轻量级分析功能。还有许多其他主流[分析工具](https://www.baeldung.com/java-profilers)。但是，**VisualVM 是免费的**，并且与 JDK 6U7 版本捆绑在一起，直到 JDK 8 的早期更新。对于其他版本，[Java VisualVM](https://visualvm.github.io/)可作为独立应用程序使用。

VisualVM**允许我们连接到本地和远程 JVM 应用程序**以进行监视。

当在任何机器上启动时，它会**自动发现并开始监视本地运行的所有 JVM 应用程序**。但是，我们需要显式连接远程应用程序。

### 3.1. JVM 连接模式

JVM 通过*[jstatd](https://docs.oracle.com/en/java/javase/11/tools/jstatd.html)* 或 [JMX](https://docs.oracle.com/javase/8/docs/technotes/guides/visualvm/jmx_connections.html)等工具公开自己以进行监视。这些工具反过来为 VisualVM 等工具提供 API 以获取分析数据。

*jstatd*程序 是一个与 JDK 捆绑在一起的守护进程。但是，它的能力有限。例如，我们无法监控 CPU 使用率，也无法进行线程转储。

另一方面，JMX 技术不需要任何守护进程在 JVM 上运行。此外，它可用于分析本地和远程 JVM 应用程序。但是，我们确实需要使用特殊属性启动 JVM 以启用开箱即用的监视功能。在本文中，我们将只关注 JMX 模式。

### **3.2. 发射**

正如我们之前看到的，我们的 JDK 版本可以与 VisualVM 捆绑在一起，也可以不与 VisualVM 捆绑在一起。无论哪种情况，我们都可以通过执行适当的二进制文件来启动它：

```bash
./jvisualvm复制
```

如果二进制文件存在于*$JAVA_HOME/bin*文件夹中，则上述命令将打开 VisualVM 界面，如果单独安装，它可能位于不同的文件夹中。

默认情况下，VisualVM 将启动并加载所有在本地运行的 Java 应用程序：

[![visualvm启动](https://www.baeldung.com/wp-content/uploads/2021/12/visualvm-launch.png)](https://www.baeldung.com/wp-content/uploads/2021/12/visualvm-launch.png)

### **3.3. 特征**

VisualVM 提供了几个有用的特性：

-   显示本地和远程 Java 应用程序进程
-   根据 CPU 使用率、GC 活动、加载类的数量和其他指标监控进程性能
-   可视化所有进程中的线程以及它们处于睡眠和等待等不同状态的时间
-   获取和显示线程转储以立即了解正在监视的进程中发生的事情

VisualVM[功能页面](https://visualvm.github.io/features.html)有一个更全面的可用功能列表。与所有精心设计的软件一样，[VisualVM 可以通过安装](https://visualvm.github.io/plugins.html)*插件选项*卡上可用的第三方插件来扩展以访问更高级和独特的功能。

## **4.远程监控**

在本节中，我们将演示如何使用 VisualVM 和 JMX 远程监控 Java 应用程序。我们还将有机会探索所有必要的配置和 JVM 启动选项。

### **4.1. 应用配置**

我们使用启动脚本启动大多数（如果不是全部的话）Java 应用程序。在此脚本中，启动命令通常将基本参数传递给 JVM 以指定应用程序的需求，例如最大和最小内存要求。

假设我们有一个打包为*MyApp.jar*的应用程序，让我们看一个包含主要 JMX 配置参数的示例启动命令：

```bash
java -Dcom.sun.management.jmxremote.port=8080 
-Dcom.sun.management.jmxremote.ssl=false 
-Dcom.sun.management.jmxremote.authenticate=false 
-Xms1024m -Xmx1024m -jar MyApp.jar复制
```

在上面的命令中，*MyApp.jar* 通过端口 8080 配置的开箱即用的监控功能启动。此外，为简单起见，我们停用了 SSL 加密和密码身份验证。

在生产环境中，理想情况下，我们应该在公共网络中保护 VisualVM 和 JVM 应用程序之间的通信。

### **4.2. 可视化虚拟机配置**

现在我们已经在本地运行了 VisualVM 并且在远程服务器上运行了*MyApp.jar ，我们可以开始远程监控会话了。*

右键单击左侧面板并选择*添加 JMX 连接*：

[![visualvm jmx连接](https://www.baeldung.com/wp-content/uploads/2021/12/visualvm-jmx-connection.png)](https://www.baeldung.com/wp-content/uploads/2021/12/visualvm-jmx-connection.png)

在出现的对话框的*“连接”*字段中输入*主机：端口*组合，然后单击*“确定”。*

如果成功，我们现在应该能够通过双击左侧面板中的新连接来看到一个监控窗口：

[![visualvm远程监控](https://www.baeldung.com/wp-content/uploads/2021/12/visualvm-remote-monitor.png)](https://www.baeldung.com/wp-content/uploads/2021/12/visualvm-remote-monitor.png)

## **5.结论**

在本文中，我们探索了使用 VisualVM 和 JMX 对 Java 应用程序的远程监控。