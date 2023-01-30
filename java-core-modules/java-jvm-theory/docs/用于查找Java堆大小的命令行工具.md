## 1. 概述

在本快速教程中，我们将熟悉几种获取正在运行的 Java 应用程序的堆大小的不同方法。

## 2. jcmd

要查找正在运行的 Java 应用程序的堆和元空间相关信息，我们可以使用[jcmd](https://docs.oracle.com/en/java/javase/11/tools/jcmd.html) 命令行实用程序：

```java
jcmd  GC.heap_info
```

[首先，让我们使用jps](https://docs.oracle.com/en/java/javase/11/tools/jps.html) 命令查找特定 Java 应用程序的进程 ID ：

```java
$ jps -l
73170 org.jetbrains.idea.maven.server.RemoteMavenServer36
4309  quarkus.jar
12070 sun.tools.jps.Jps
```

如上所示，我们的[Quarkus](https://www.baeldung.com/quarkus-io)应用程序的进程 ID 是 4309。现在我们有了进程 ID，让我们看看堆信息：

```plaintext
$ jcmd 4309 GC.heap_info
4309:
 garbage-first heap   total 206848K, used 43061K
  region size 1024K, 43 young (44032K), 3 survivors (3072K)
 Metaspace       used 12983K, capacity 13724K, committed 13824K, reserved 1060864K
  class space    used 1599K, capacity 1740K, committed 1792K, reserved 1048576K
```

此应用程序使用 G1 或垃圾优先[GC 算法](https://www.baeldung.com/jvm-garbage-collectors)：

-   第一行报告当前堆大小为 202 MB (206848 K) – 同时，42 MB (43061 K) 正在使用中
-   G1 region是1MB，有43个region标记为young，3个标记为survivors space
-   元空间的当前容量约为 13.5 MB (13724 K)。在这 13.5 MB 中，使用了大约 12.5 MB (12983 K)。此外，我们最多可以拥有 1 GB 的元空间 (1048576 K)。此外，保证有 13842 KB 可供 Java 虚拟机使用，也称为已提交内存
-   最后一行显示了有多少元空间用于存储类信息

此输出可能会根据 GC 算法而改变。例如，如果我们通过 “-XX:+UnlockExperimentalVMOptions -XX:+UseZGC”运行与[ZGC](https://www.baeldung.com/jvm-zgc-garbage-collector)相同的 Quarkus 应用程序：

```plaintext
ZHeap           used 28M, capacity 200M, max capacity 1024M
Metaspace       used 21031K, capacity 21241K, committed 21504K, reserved 22528K
```

如上所示，我们使用了 28 MB 的堆和大约 20 MB 的元空间。在撰写本文时，Intellij IDEA 仍在使用具有以下堆信息的 CMS GC：

```plaintext
par new generation   total 613440K, used 114299K
  eden space 545344K,  18% used
  from space 68096K,  16% used
  to   space 68096K,   0% used
 concurrent mark-sweep generation total 1415616K, used 213479K
 Metaspace       used 423107K, capacity 439976K, committed 440416K, reserved 1429504K
  class space    used 55889K, capacity 62488K, committed 62616K, reserved 1048576K
```

我们可以在堆配置中发现 CMS GC 的经典分代性质。

## 3.站立

除了 [jcmd 之外](https://www.baeldung.com/java-heap-dump-capture#2-jcmd)，我们还可以使用 [jstat](https://docs.oracle.com/en/java/javase/11/tools/jstat.html) 从正在运行的应用程序中找出相同的信息。例如，我们可以使用jstat -gc查看堆统计信息：

```bash
$ jstat -gc 4309
S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     
0.0    0.0    0.0    0.0   129024.0  5120.0   75776.0    10134.6   20864.0
MU      CCSC   CCSU     YGC     YGCT    FGC    FGCT     CGC    CGCT     GCTGCT
19946.2 2688.0 2355.0    2      0.007    1      0.020    0     0.000     0.027
```

每列代表特定内存区域的内存容量或利用率：

-   S0C — 第一个幸存者空间的容量
-   S1C — 第二个幸存者空间的容量
-   S0U — 第一个幸存者的已用空间
-   S1U — 第二个幸存者的已用空间
-   EC - 伊甸园空间容量
-   EU——伊甸园的已用空间
-   OC——老一代容量
-   OU——老年代的已用空间
-   MC - 元空间容量
-   MU - 来自元空间的已用空间
-   CCSC - 压缩类空间容量
-   CCSU — 压缩类的已用空间
-   YGC — 次要 GC 的数量
-   YGCT — minor GC 花费的时间
-   FGC — 完整 GC 的数量
-   FGCT — 完整 GC 所花费的时间
-   CGC — 并发 GC 的数量
-   CGCT — 花在并发 GC 上的时间
-   GCT——所有GC花费的时间

jstat 还有其他与内存相关的选项， 例如：

-   -gccapacity 报告不同内存区域的不同容量
-   -gcutil 只显示每个区域的利用率 
-   -gccause 与-gcutil 相同， 但添加了上次 GC 的原因和可能的当前 GC 事件

## 4. 命令行参数

如果我们使用堆配置选项(例如[-Xms和 -Xmx](https://www.baeldung.com/jvm-parameters#explicit-heap-memory---xms-and-xmx-options))运行 Java 应用程序，那么还有一些其他技巧可以找到指定的值。

例如，以下是 jps 报告这些值的方式：

```bash
$ jps -lv
4309 quarkus.jar -Xms200m -Xmx1g
```

使用这种方法，我们只能找到这些静态值。因此，没有办法知道当前提交的内存。

除了 jps之外，其他一些工具也会报告同样的事情。例如， “jcmd <pid> VM.command_line” 也会报告这些细节：

```bash
$ jcmd 4309 VM.command_line
4309:
VM Arguments:
jvm_args: -Xms200m -Xmx1g
java_command: quarkus.jar
java_class_path (initial): quarkus.jar
Launcher Type: SUN_STANDARD
```

此外，在大多数基于 Unix 的系统上，我们可以使用 procps 包中的 [ps ：](https://www.baeldung.com/linux/ps-command) 

```bash
$ ps -ef | grep quarkus
... java -Xms200m -Xmx1g -jar quarkus.jar
```

最后，在 Linux 上，我们可以使用 /proc 虚拟文件系统及其 pid 文件：

```bash
$ cat /proc/4309/cmdline
java -Xms200m -Xmx1g -jar quarkus.jar
```

cmdline 文件位于以 Quarkus pid 命名的目录中，包含应用程序的命令行条目。 

## 5.总结

在这个快速教程中，我们看到了几种不同的方法来获取正在运行的 Java 应用程序的堆大小。