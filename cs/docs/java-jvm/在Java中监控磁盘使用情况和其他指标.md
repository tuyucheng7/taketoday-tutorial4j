## 一、概述

在本快速教程中，我们将讨论如何在 Java 中监控关键指标。我们将专注于**磁盘空间、内存使用和线程数据——仅使用核心 Java API**。

在我们的第一个示例中，我们将使用[*File*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html)类来查询特定的磁盘信息。

然后，我们将通过深入到 [*ManagementFactory*](https://docs.oracle.com/en/java/javase/11/docs/api/java.management/java/lang/management/ManagementFactory.html) 类来分析内存使用情况和处理器信息。

最后，我们将介绍**如何使用 Java Profilers 在运行时监控这些关键指标**。

## *二、文件*类介绍

简单地说，***File\*类表示文件或目录的抽象**。它可用于**获取有关文件系统的关键信息并保持与文件路径相关的** **操作系统独立性**。在本教程中，我们将使用此类来检查 Windows 和 Linux 机器上的根分区。

## 3.*管理工厂*

**Java 提供 \*ManagementFactory\* 类作为工厂，用于获取****包含** **有关 JVM 的特定信息的**托管 beans (MXBeans) 。我们将在以下代码示例中检查两个：

### 3.1. *内存MXBean*

MemoryMXBean表示JVM 内存系统的**管理接口。**在运行时，JVM 创建此接口的单个实例，我们可以使用 *ManagementFactory*的 *getMemoryMXBean()* 方法检索该实例。

### 3.2. *ThreadMXBean*

*与MemoryMXBean*类似，*ThreadMXBean*是 JVM 线程系统的管理接口。可以使用 *getThreadMXBean()* 方法调用它并保存有关线程的关键数据。

在下面的示例中，我们将使用*ThreadMXBean*来获取 JVM 的 ***ThreadInfo\* 类——它包含有关在 JVM 上运行的线程的特定信息。**

## 3.监控磁盘使用情况

在此代码示例中，我们将使用 File 类来包含有关分区的关键信息。以下示例将返回 Windows 计算机上 C: 驱动器的可用空间、总空间和可用空间：

```java
File cDrive = new File("C:");
System.out.println(String.format("Total space: %.2f GB",
  (double)cDrive.getTotalSpace() /1073741824));
System.out.println(String.format("Free space: %.2f GB", 
  (double)cDrive.getFreeSpace() /1073741824));
System.out.println(String.format("Usable space: %.2f GB", 
  (double)cDrive.getUsableSpace() /1073741824));
复制
```

**同样，我们可以为Linux 机器的根目录**返回相同的信息：

```java
File root = new File("/");
System.out.println(String.format("Total space: %.2f GB", 
  (double)root.getTotalSpace() /1073741824));
System.out.println(String.format("Free space: %.2f GB", 
  (double)root.getFreeSpace() /1073741824));
System.out.println(String.format("Usable space: %.2f GB", 
  (double)root.getUsableSpace() /1073741824));
复制
```

上面的代码打印出定义文件的总空间、可用空间和可用空间。默认情况下，上述方法提供字节数。我们已将这些字节转换为千兆字节，以使结果更易于阅读。

## 4.监控内存使用情况

我们现在将使用***ManagementFactory\*** **类通过调用*****MemoryMXBean\*****来查询 JVM 可用的内存**。 

在此示例中，我们将主要关注查询堆内存。需要注意的是，非堆内存也可以使用*MemoryMXBean 查询：*

```java
MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
System.out.println(String.format("Initial memory: %.2f GB", 
  (double)memoryMXBean.getHeapMemoryUsage().getInit() /1073741824));
System.out.println(String.format("Used heap memory: %.2f GB", 
  (double)memoryMXBean.getHeapMemoryUsage().getUsed() /1073741824));
System.out.println(String.format("Max heap memory: %.2f GB", 
  (double)memoryMXBean.getHeapMemoryUsage().getMax() /1073741824));
System.out.println(String.format("Committed memory: %.2f GB", 
  (double)memoryMXBean.getHeapMemoryUsage().getCommitted() /1073741824));
复制
```

上面的示例分别返回初始、已用、最大和已提交的内存。[这是对这意味着](https://docs.oracle.com/en/java/javase/11/docs/api/java.management/java/lang/management/MemoryUsage.html)什么的简短解释 ：

-   Initial：JVM 在启动时向 OS 请求的初始内存
-   Used：JVM当前使用的内存量
-   Max：JVM可用的最大内存。如果达到此限制，则 可能会抛出*[OutOfMemoryException](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/OutOfMemoryError.html)* 
-   Committed：保证JVM可用的内存量

## 5.CPU使用率

接下来，我们将使用***ThreadMXBean\*获取 \*ThreadInfo\***对象的完整列表，并**查询它们以获取** **有关** JVM 上运行的**当前线程的有用信息。**

```java
ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

for(Long threadID : threadMXBean.getAllThreadIds()) {
    ThreadInfo info = threadMXBean.getThreadInfo(threadID);
    System.out.println("Thread name: " + info.getThreadName());
    System.out.println("Thread State: " + info.getThreadState());
    System.out.println(String.format("CPU time: %s ns", 
      threadMXBean.getThreadCpuTime(threadID)));
  }

复制
```

首先，代码使用 *getAllThreadIds* 方法获取当前线程的列表。对于每个线程，它然后输出线程的名称和状态，后跟线程的 CPU 时间（以纳秒为单位）。

## 6. 使用分析器监控指标

最后，值得一提的是，**我们可以在不使用任何 Java 代码的情况下监控这些关键指标**。Java Profilers 密切监视 JVM 级别的关键构造和操作，并提供内存、线程等的实时分析。

VisualVM 是 Java 分析器的一个例子，自 Java 6 以来就与 JDK 捆绑在一起。 许多集成开发环境 (IDE) 包含插件以在开发新代码时利用分析器。您可以 [在此处](https://www.baeldung.com/java-profilers)了解有关 Java Profiler 和 VisualVM 的更多信息。

## 七、结论

在本文中，我们谈到了使用核心 Java API 来查询有关磁盘使用情况、内存管理和线程信息的关键信息。

我们已经查看了使用 *File* 和 *ManagmentFactory* 类获取这些指标的多个示例。