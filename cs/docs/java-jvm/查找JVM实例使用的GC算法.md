## 1. 概述

除了典型的开发实用程序(例如编译器和运行时)之外，每个 JDK 版本都附带了无数其他工具。其中一些工具可以帮助我们深入了解正在运行的应用程序。

在本文中，我们将了解如何使用此类工具来了解有关特定 JVM 实例使用的[GC 算法的更多信息。](https://www.baeldung.com/jvm-garbage-collectors)

## 2. 示例应用

在整篇文章中，我们将使用一个非常简单的应用程序：

```java
public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("Waiting for stdin");
        int read = System.in.read();
        System.out.println("I'm done: " + read);
    }
}
```

显然，这个应用程序会一直等待并一直运行，直到它从标准输入接收到一些东西。这种暂停有助于我们模仿长时间运行的 JVM 应用程序的行为。

为了使用这个应用程序，我们必须 用javac 编译App.java 文件， 然后使用 java 工具运行它。

## 3.找到JVM进程

要找到 JVM 进程使用的 GC，首先，我们应该确定该特定 JVM 实例的进程 ID。假设我们使用以下命令运行我们的应用程序：

```bash
>> java App
Waiting for stdin
```

如果我们安装了 JDK，那么查找 JVM 实例的进程 ID 的最佳方法是使用 [jps](https://docs.oracle.com/en/java/javase/11/tools/jps.html)工具。例如：

```bash
>> jps -l
69569 
48347 App
48351 jdk.jcmd/sun.tools.jps.Jps

```

如上所示，系统上运行着三个 JVM 实例。显然，第二个 JVM 实例(“App”)的描述与我们的应用程序名称相匹配。因此，我们要查找的进程 ID 是 48347。

除了 jps之外，我们始终可以使用其他通用实用程序来过滤掉正在运行的进程。例如， [procps包中著名的](https://gitlab.com/procps-ng/procps)[ps](https://www.baeldung.com/linux/ps-command) 工具也可以工作：

```bash
>> ps -ef | grep java
502 48347 36213   0  1:28AM ttys037    0:00.28 java App
```

但是， jps 使用起来更简单，需要的过滤也更少。

## 4.二手GC

现在我们知道如何查找进程 ID，让我们查找已运行的 JVM 应用程序使用的 GC 算法。

### 4.1. Java 8 及更早版本

如果我们使用的是 Java 8，我们可以使用 [jmap](https://docs.oracle.com/en/java/javase/11/tools/jmap.html) 实用程序打印堆摘要、堆直方图，甚至生成堆转储。为了找到 GC 算法，我们可以使用 -heap 选项：

```bash
>> jmap -heap <pid>
```

所以在我们的特殊情况下，我们使用 CMS GC：

```bash
>> jmap -heap 48347 | grep GC
Concurrent Mark-Sweep GC
```

对于其他 GC 算法，输出几乎相同：

```bash
>> jmap -heap 48347 | grep GC
Parallel GC with 8 thread(s)
```

### 4.2. Java 9+：jhsdb jmap

从 Java 9 开始，我们可以使用 [jhsdb jmap](https://docs.oracle.com/en/java/javase/11/tools/jps.html) 组合来打印有关 JVM 堆的一些信息。更具体地说，这个特定命令等同于前一个命令：

```bash
>> jhsdb jmap --heap --pid <pid>
```

例如，我们的应用程序现在使用 G1GC 运行：

```bash
>> jhsdb jmap --heap --pid 48347 | grep GC
Garbage-First (G1) GC with 8 thread(s)

```

### 4.3. Java 9+： jcmd

在现代 JVM 中， jcmd 命令用途广泛。例如，我们可以使用它来获取有关堆的一些一般信息：

```bash
>> jcmd <pid> VM.info
```

因此，如果我们传递应用程序的进程 ID，我们可以看到这个 JVM 实例正在使用串行 GC：

```bash
>> jcmd 48347 VM.info | grep gc
# Java VM: OpenJDK 64-Bit Server VM (15+36-1562, mixed mode, sharing, tiered, compressed oops, serial gc, bsd-amd64)
// omitted
```

G1 或 ZGC 的输出类似：

```bash
// ZGC
# Java VM: OpenJDK 64-Bit Server VM (15+36-1562, mixed mode, sharing, tiered, z gc, bsd-amd64)
// G1GC
# Java VM: OpenJDK 64-Bit Server VM (15+36-1562, mixed mode, sharing, tiered, compressed oops, g1 gc, bsd-amd64)

```

通过一点[grep](https://www.baeldung.com/linux/common-text-search)魔法，我们还可以消除所有这些噪音，只获取 GC 名称：

```bash
>> jcmd 48347 VM.info | grep -ohE "[^s^,]+sgc"
g1 gc
```

### 4.4. 命令行参数

有时，我们(或其他人)在启动 JVM 应用程序时明确指定 GC 算法。例如，我们在这里选择使用 ZGC：

```bash
>> java -XX:+UseZGC App
```

在这种情况下，有更简单的方法来查找已使用的 GC。基本上，我们所要做的就是以某种方式找到应用程序执行的命令。

例如，在基于 UNIX 的平台上，我们可以再次使用 ps命令：

```bash
>> ps -p 48347 -o command=
java -XX:+UseZGC App
```

从上面的输出可以看出，JVM 正在使用 ZGC。同样，jcmd 命令 也可以打印命令行参数：

```bash
>> jcmd 48347 VM.flags
84020:
-XX:CICompilerCount=4 -XX:-UseCompressedOops -XX:-UseNUMA -XX:-UseNUMAInterleaving -XX:+UseZGC // omitted
```

令人惊讶的是，如上所示，此命令将打印隐式和显式参数以及可调参数。所以即使我们没有明确指定 GC 算法，它也会显示选择的和默认的：

```bash
>> jcmd 48347 VM.flags | grep -ohE 'SGCs'
-XX:+UseG1GC
```

更令人惊讶的是，这也适用于 Java 8：

```bash
>> jcmd 48347 VM.flags | grep -ohE 'SGCs'
-XX:+UseParallelGC
```

## 5.总结

在本文中，我们看到了查找特定 JVM 实例使用的 GC 算法的不同方法。提到的一些方法与特定的 Java 版本相关联，还有一些是可移植的。

此外，我们看到了几种查找进程 ID 的方法，这总是需要的。