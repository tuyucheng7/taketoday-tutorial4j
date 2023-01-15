## 1. 概述

Java 11 引入了一个名为 Epsilon的[No-Op Garbage Collector ，它](https://openjdk.java.net/jeps/318)承诺尽可能降低 GC 开销。

在这个简短的教程中，我们将探索 Epsilon 的工作原理，并提到常见的用例。

## 2. 快速动手

让我们从弄脏我们的手开始，试一试 Epsilon GC！

我们首先需要一个创建垃圾的应用程序：

```java
class MemoryPolluter {

    static final int MEGABYTE_IN_BYTES = 1024  1024;
    static final int ITERATION_COUNT = 1024  10;

    static void main(String[] args) {
        System.out.println("Starting pollution");

        for (int i = 0; i < ITERATION_COUNT; i++) {
            byte[] array = new byte[MEGABYTE_IN_BYTES];
        }

        System.out.println("Terminating");
    }
}
```

此代码在循环中创建 1 兆字节的数组。由于我们重复循环 10240 次，这意味着我们分配了 10 GB 的内存，这可能高于可用的最大堆大小。

我们还提供了一些帮助打印来查看应用程序何时终止。

要启用 Epsilon GC，我们需要传递以下 VM 参数：

```bash
-XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC
```

当我们运行应用程序时，我们会收到以下错误：

```plaintext
Starting pollution
Terminating due to java.lang.OutOfMemoryError:Javaheap space
```

但是，当我们使用标准 VM 选项运行相同的应用程序时，它可以正常完成：

```plaintext
Starting pollution
Terminating
```

为什么第一次运行失败？似乎即使是最基本的垃圾收集器也可以清理我们刚刚演示的孩子的游戏！

那么，让我们看一下 Epsilon GC 背后的概念，以了解刚刚发生的事情。

## 3. Epsilon GC 的工作原理

Epsilon 是一个无操作垃圾收集器。

[JEP 318](https://openjdk.java.net/jeps/318)解释说：“ [Epsilon] ... 处理内存分配，但不实现任何实际的内存回收机制。一旦可用的Java堆用完，JVM 就会关闭。”

因此，这就解释了为什么我们的应用程序以OutOfMemoryError终止。

但是，它提出了一个问题：为什么我们需要一个不收集任何垃圾的垃圾收集器？

在某些情况下，我们知道可用堆就足够了，所以我们不希望 JVM 使用资源来运行 GC 任务。

此类情况的一些示例(也来自相关的 JEP)：

-   性能测试
-   内存压力测试
-   虚拟机接口测试
-   寿命极短的工作
-   最后一滴延迟改进
-   最后一滴吞吐量改进

## 4。总结

在这篇简短的文章中，我们了解了 Epsilon，这是Java11 中可用的一种无操作 GC。我们了解了使用它的含义，并回顾了一些可能会派上用场的案例。