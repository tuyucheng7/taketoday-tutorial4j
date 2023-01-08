## 1. 概述

在本教程中，我们将研究位于java.lang包中的System.gc()方法。

明确调用System.gc()是一种不好的做法。让我们尝试了解调用此方法可能有用的原因以及是否存在任何用例。

## 2. 垃圾收集

当有指示时，Java 虚拟机决定执行垃圾收集。这些指示因一个[GC 实现](https://www.baeldung.com/jvm-garbage-collectors)而异。它们基于不同的启发式。但是，有一些时刻肯定会执行 GC：

-   老年代(Tenured space)已满，触发major/full GC
-   新生代(Eden + Survivor0 + Survivor1 spaces) 已满，触发minor GC

唯一独立于 GC 实现的是对象是否有资格被垃圾收集。

现在，我们来看看System.gc()方法本身。

## 3.系统.gc()

方法的调用很简单：

```java
System.gc()
```

[Oracle](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/System.html#gc())官方文档指出：

>   调用gc方法表明Java 虚拟机努力回收未使用的对象，以便使它们当前占用的内存可用于快速重用。

不能保证会触发实际的 GC。

System.gc() [触发主要的 GC](https://www.oracle.com/java/technologies/javase/gc-tuning-6.html#other_considerations)。因此，根据你的垃圾收集器实施情况，存在在 stop-the-world 阶段花费一些时间的风险。结果，我们有一个不可靠的工具，可能会严重影响性能。

对每个人来说，显式垃圾收集调用的存在应该是一个严重的危险信号。

我们可以通过使用-XX:DisableExplicitGC JVM 标志来阻止System.gc()做任何工作。

### 3.1. 性能调优

值得注意的是，就在抛出OutOfMemoryError 之前， JVM 将执行完整的 GC。因此，显式调用System.gc() 不会使我们免于失败。

现在的垃圾收集器真的很聪明。他们了解内存使用情况和其他统计数据，以便能够做出正确的决定。因此，我们应该信任他们。

在内存问题的情况下，我们有[一堆设置](https://docs.oracle.com/javase/9/gctuning/JSGCT.pdf)可以更改以调整我们的应用程序——从选择不同的垃圾收集器开始，通过设置所需的应用程序时间/GC 时间比率，最后以设置内存段的固定大小结束。

还有一些方法可以[减轻由显式调用引起的 Full GC 的影响](https://docs.oracle.com/javase/8/docs/technotes/guides/vm/cms-6.html)。我们可以使用其中一个标志：

```bash
-XX:+ExplicitGCInvokesConcurrent
```

或者：

```bash
-XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses
```

如果我们真的想让我们的应用程序正常工作，我们应该解决真正的底层内存问题。

在下一章中，我们将看到显式调用System.gc()似乎很有用的实际示例。

## 4.使用示例

### 4.1. 设想

让我们编写一个测试应用程序。我们想找到调用System.gc()可能有用的情况。

次要垃圾回收比主要垃圾回收更频繁。所以，我们或许应该关注后者。如果单个对象“幸存”了几次收集并且仍然可以从 GC 根访问，则它会被移动到永久空间。

假设我们有大量存在一段时间的对象。然后，在某个时候，我们正在清除对象集合。也许现在是运行System.gc()的好时机？

### 4.2. 演示应用

我们将创建一个简单的控制台应用程序，使我们能够模拟该场景：

```java
public class DemoApplication {

    private static final Map<String, String> cache = new HashMap<String, String>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()) {
            final String next = scanner.next();
            if ("fill".equals(next)) {
                for (int i = 0; i < 1000000; i++) { 
                    cache.put(randomUUID().toString(), randomUUID().toString()); 
                } 
            } else if ("invalidate".equals(next)) {
                cache.clear();
            } else if ("gc".equals(next)) {
                System.gc();
            } else if ("exit".equals(next)) {
                System.exit(0);
            } else {
                System.out.println("unknown");
            }
        }
    }
}
```

### 4.3. 运行演示

让我们使用一些额外的标志来运行我们的应用程序：

```bash
-XX:+PrintGCDetails -Xloggc:gclog.log -Xms100M -Xmx500M -XX:+UseConcMarkSweepGC
```

需要前两个标志来记录 GC 信息。接下来的两个标志设置初始堆大小，然后设置最大堆大小。我们希望保持较小的堆大小以强制 GC 更加活跃。最后，我们决定使用 CMS——并发标记和清除垃圾收集器。是时候运行我们的应用程序了！

首先，让我们尝试填充 tenured space。键入填充。

我们可以[调查我们的gclog.log](https://www.baeldung.com/java-verbose-gc)文件以查看发生了什么。我们将看到大约 15 个集合。为单个集合记录的行如下所示：

```bash
197.057: [GC (Allocation Failure) 197.057: [ParNew: 67498K->40K(75840K), 0.0016945 secs] 
  168754K->101295K(244192K), 0.0017865 secs] [Times: user=0.01 sys=0.00, real=0.00 secs] secs]
```

如我们所见，内存已满。

接下来，让我们通过键入gc强制执行System.gc()。我们可以看到内存使用情况没有显着变化：

```bash
238.810: [Full GC (System.gc()) 238.810: [CMS: 101255K->101231K(168352K); 0.2634318 secs] 
  120693K->101231K(244192K), [Metaspace: 32186K->32186K(1079296K)], 0.2635908 secs] 
  [Times: user=0.27 sys=0.00, real=0.26 secs]
```

再运行几次后，我们将看到内存大小保持在同一水平。

让我们通过输入invalidate来清除缓存。我们应该不会在gclog.log文件中看到更多的日志行。

我们可以尝试多次填充缓存，但没有 GC 发生。这是我们可以智取垃圾收集器的时刻。现在，在强制 GC 之后，我们将看到如下一行：

```bash
262.124: [Full GC (System.gc()) 262.124: [CMS: 101523K->14122K(169324K); 0.0975656 secs] 
  103369K->14122K(245612K), [Metaspace: 32203K->32203K(1079296K)], 0.0977279 secs]
  [Times: user=0.10 sys=0.00, real=0.10 secs]
```

我们释放了大量内存！但现在真的有必要吗？发生了什么？

根据这个例子，当我们释放大对象或使缓存失效时，调用System.gc() 似乎很诱人。

## 五、其他用途

显式调用System.gc()方法可能有用的原因很少。

一个可能的原因是在服务器启动后清理内存——我们正在启动一个服务器或应用程序，它会做很多准备工作。之后，还有很多对象需要敲定。然而，这种准备后的清洁不应该是我们的责任。

另一个是内存泄漏分析—— 它更像是一种调试实践，而不是我们希望保留在生产代码中的东西。调用System.gc()并看到堆空间仍然很高可能表明存在[内存泄漏](https://www.baeldung.com/java-memory-leaks)。

## 6.总结

在本文中，我们研究了System.gc() 方法以及它何时可能有用。

当涉及到我们应用程序的正确性时，我们永远不应该依赖它。GC 在大多数情况下比我们更聪明，如果出现任何内存问题，我们应该考虑调整虚拟机而不是进行这种显式调用。