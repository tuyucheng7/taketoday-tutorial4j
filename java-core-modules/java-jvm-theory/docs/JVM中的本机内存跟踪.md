## **一、概述**

有没有想过为什么 Java 应用程序消耗的内存比通过众所周知的 *-Xms*和*-Xmx*调整标志指定的数量多得多？由于各种原因和可能的优化，JVM 可能会分配额外的本机内存。这些额外的分配最终会使消耗的内存超出*-Xmx* 限制。

在本教程中，我们将列举 JVM 中本机内存分配的一些常见来源及其大小调整标志，然后学习如何使用*本机内存跟踪*来监视它们。

## **2. 本地分配**

堆通常是 Java 应用程序中最大的内存消耗者，但还有其他消耗者。**除了堆之外，JVM 还从本机内存中分配相当大的块来维护其类元数据、应用程序代码、JIT 生成的代码、内部数据结构等。在接下来的部分中，我们将探讨其中的一些分配**。

### 2.1. 元空间

**为了维护一些关于加载类的元数据，JVM 使用了一个专用的非堆区域，称为\*Metaspace\***。在 Java 8 之前，等效项称为*PermGen*或*Permanent Generation*。Metaspace 或 PermGen 包含有关已加载类的元数据，而不是它们的实例，这些实例保存在堆中。

这里重要的是**堆大小配置不会影响元空间大小**，因为元空间是堆外数据区域。为了限制元空间大小，我们使用其他调整标志：

-    *-XX:MetaspaceSize*和*-XX:MaxMetaspaceSize*设置最小和最大元空间大小
-   在 Java 8 之前，*-XX:PermSize*和*-XX:MaxPermSize*设置最小和最大 PermGen 大小

### 2.2. 线程

JVM 中最消耗内存的数据区域之一是堆栈，它与每个线程同时创建。堆栈存储局部变量和部分结果，在方法调用中起着重要作用。

默认线程堆栈大小取决于平台，但在大多数现代 64 位操作系统中，它约为 1 MB。此大小可通过*-Xss* 调整标志进行配置。

与其他数据区域相比，**当线程数没有限制时，分配给堆栈的总内存实际上是无限的。** 还值得一提的是，JVM 本身需要一些线程来执行其内部操作，如 GC 或即时编译。

### 2.3. 代码缓存

为了在不同的平台上运行 JVM 字节码，需要将其转换为机器指令。JIT 编译器在程序执行时负责此编译。

**当 JVM 将字节码编译为汇编指令时，它会将这些指令存储在称为代码缓存的特殊非堆数据区域中 \*。\*** 代码缓存可以像 JVM 中的其他数据区域一样进行管理。-XX *:InitialCodeCacheSize* 和 *-XX:ReservedCodeCacheSize* 调整标志确定代码缓存的初始大小和最大可能大小。

### 2.4. 垃圾收集

JVM 附带了一些 GC 算法，每个算法都适用于不同的用例。所有这些 GC 算法都有一个共同特征：它们需要使用一些堆外数据结构来执行它们的任务。这些内部数据结构消耗更多的本机内存。

### 2.5. 符号

让我们从 *字符串开始，* 它是应用程序和库代码中最常用的数据类型之一。由于它们无处不在，它们通常占据堆的很大一部分。如果大量这些字符串包含相同的内容，那么堆的很大一部分将被浪费。

为了节省一些堆空间，我们可以为每个 *String* 存储一个版本，让其他的引用存储的版本。 **这个过程称为\*字符串实习。\*** 由于 JVM 只能 intern *Compile Time String Constants，* 我们可以手动调用*intern()* 方法来处理我们想要 intern 的字符串。

**JVM 将驻留字符串存储在一个特殊的本机固定大小哈希表中，称为*****字符串表，*****也称为[\*字符串池\*](https://www.baeldung.com/java-string-pool)*****。****我们可以通过-XX:StringTableSize* 调整标志配置表大小（即桶的数量） 。

除了字符串表之外，还有另一个本地数据区域，称为*运行时常量池。* JVM 使用此池来存储常量，例如必须在运行时解析的编译时数字文字或方法和字段引用。

### 2.6. 本机字节缓冲区

JVM 通常会怀疑大量本机分配，但有时开发人员也可以直接分配本机内存。最常见的方法是JNI 和 NIO 的直接 ByteBuffers 调用 *malloc* *。*

### 2.7. 额外的调整标志

在本节中，我们针对不同的优化场景使用了一些 JVM 调优标志。使用以下提示，我们可以找到与特定概念相关的几乎所有调整标志：

```shell
$ java -XX:+PrintFlagsFinal -version | grep <concept>复制
```

PrintFlagsFinal打印 JVM 中的所有*–XX**选项* 。例如，要查找所有与元空间相关的标志：

```shell
$ java -XX:+PrintFlagsFinal -version | grep Metaspace
      // truncated
      uintx MaxMetaspaceSize                          = 18446744073709547520                    {product}
      uintx MetaspaceSize                             = 21807104                                {pd product}
      // truncated复制
```

## **3.本机内存跟踪（NMT）**

现在我们知道了 JVM 中本机内存分配的常见来源，是时候了解如何监控它们了。**首先，我们应该使用另一个 JVM 调整标志启用本机内存跟踪：\*-XX:NativeMemoryTracking=off|sumary|detail。\*** 默认情况下，NMT 处于关闭状态，但我们可以启用它来查看其观察结果的摘要或详细视图。

假设我们要跟踪典型 Spring Boot 应用程序的本机分配：

```shell
$ java -XX:NativeMemoryTracking=summary -Xms300m -Xmx300m -XX:+UseG1GC -jar app.jar复制
```

在这里，我们启用 NMT，同时分配 300 MB 的堆空间，使用 G1 作为我们的 GC 算法。

### 3.1. 即时快照

当启用 NMT 时，我们可以随时使用 *jcmd* 命令获取本机内存信息：

```shell
$ jcmd <pid> VM.native_memory复制
```

为了找到 JVM 应用程序的 PID，我们可以使用 *jps* 命令：

```shell
$ jps -l                    
7858 app.jar // This is our app
7899 sun.tools.jps.Jps复制
```

现在，如果我们将 *jcmd* 与适当的*pid*一起使用， *VM.native_memory* 会使 JVM 打印出有关本机分配的信息：

```shell
$ jcmd 7858 VM.native_memory复制
```

让我们逐段分析 NMT 输出。

### 3.2. 总分配

NMT 报告总的保留和提交内存如下：

```shell
Native Memory Tracking:
Total: reserved=1731124KB, committed=448152KB复制
```

**预留内存代表我们的应用程序可能使用的内存总量。相反，提交的内存等于我们的应用程序现在使用的内存量。**

尽管分配了 300 MB 的堆，但为我们的应用程序保留的总内存将近 1.7 GB，远不止于此。同样，提交的内存约为 440 MB，这同样比 300 MB 多得多。

在总计部分之后，NMT 报告每个分配源的内存分配。因此，让我们深入探讨每个来源。

### 3.3. 堆

NMT 报告了我们预期的堆分配：

```shell
Java Heap (reserved=307200KB, committed=307200KB)
          (mmap: reserved=307200KB, committed=307200KB)复制
```

300 MB 的保留和提交内存，与我们的堆大小设置相匹配。

### 3.4. 元空间

以下是 NMT 对加载类的类元数据的描述：

```shell
Class (reserved=1091407KB, committed=45815KB)
      (classes #6566)
      (malloc=10063KB #8519) 
      (mmap: reserved=1081344KB, committed=35752KB)复制
```

将近 1 GB 的保留空间和 45 MB 的空间用于加载 6566 个类。

### 3.5. 线

这是关于线程分配的 NMT 报告：

```shell
Thread (reserved=37018KB, committed=37018KB)
       (thread #37)
       (stack: reserved=36864KB, committed=36864KB)
       (malloc=112KB #190) 
       (arena=42KB #72)复制
```

总共为 37 个线程分配了 36 MB 的内存——每个堆栈几乎 1 MB。JVM 在创建时将内存分配给线程，因此保留分配和提交分配是相等的。

### 3.6. 代码缓存

让我们看看 NMT 对 JIT 生成和缓存的汇编指令的看法：

```shell
Code (reserved=251549KB, committed=14169KB)
     (malloc=1949KB #3424) 
     (mmap: reserved=249600KB, committed=12220KB)复制
```

目前，将近 13 MB 的代码被缓存，这个数量可能会增加到大约 245 MB。

### 3.7. GC

这是关于 G1 GC 内存使用情况的 NMT 报告：

```shell
GC (reserved=61771KB, committed=61771KB)
   (malloc=17603KB #4501) 
   (mmap: reserved=44168KB, committed=44168KB)复制
```

正如我们所看到的，将近 60 MB 被保留并致力于帮助 G1。

让我们看看更简单的 GC（比如串行 GC）的内存使用情况：

```shell
$ java -XX:NativeMemoryTracking=summary -Xms300m -Xmx300m -XX:+UseSerialGC -jar app.jar复制
```

串行 GC 几乎不使用 1 MB：

```shell
GC (reserved=1034KB, committed=1034KB)
   (malloc=26KB #158) 
   (mmap: reserved=1008KB, committed=1008KB)复制
```

显然，我们不应该仅仅因为它的内存使用而选择 GC 算法，因为串行 GC 的 stop-the-world 特性可能会导致性能下降。然而，有[几种 GC 可供选择](https://www.baeldung.com/jvm-garbage-collectors)，它们各自以不同方式平衡内存和性能。

### 3.8. 象征

下面是NMT关于符号分配的报告，比如字符串表和常量池：

```shell
Symbol (reserved=10148KB, committed=10148KB)
       (malloc=7295KB #66194) 
       (arena=2853KB #1)复制
```

将近 10 MB 分配给符号。

### 3.9. NMT 随着时间的推移

**NMT 允许我们跟踪内存分配如何随时间变化。**首先，我们应该将应用程序的当前状态标记为基线：

```shell
$ jcmd <pid> VM.native_memory baseline
Baseline succeeded复制
```

然后，过了一会儿，我们可以将当前内存使用情况与该基线进行比较：

```shell
$ jcmd <pid> VM.native_memory summary.diff复制
```

NMT，使用 + 和 – 符号，会告诉我们内存使用率在那个时期是如何变化的：

```shell
Total: reserved=1771487KB +3373KB, committed=491491KB +6873KB
-             Java Heap (reserved=307200KB, committed=307200KB)
                        (mmap: reserved=307200KB, committed=307200KB)
 
-             Class (reserved=1084300KB +2103KB, committed=39356KB +2871KB)
// Truncated复制
```

总保留内存和提交内存分别增加了 3 MB 和 6 MB。可以很容易地发现内存分配中的其他波动。

### 3.10. 详细NMT

NMT 可以提供关于整个内存空间映射的非常详细的信息。要启用此详细报告，我们应该使用*-XX:NativeMemoryTracking=detail* 调整标志。

## 4。结论

在本文中，我们列举了 JVM 中本机内存分配的不同贡献者。然后，我们学习了如何检查正在运行的应用程序以监控其本机分配。有了这些见解，我们可以更有效地调整我们的应用程序并调整我们的运行时环境。