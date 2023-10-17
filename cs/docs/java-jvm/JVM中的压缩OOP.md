## 1. 概述

JVM 为我们管理内存。这减轻了开发人员的内存管理负担，因此我们不需要手动操作对象指针，这被证明是耗时且容易出错的。

在幕后，JVM 结合了许多巧妙的技巧来优化内存管理过程。一种技巧是使用压缩指针，我们将在本文中对其进行评估。首先，让我们看看 JVM 在运行时如何表示对象。

## 2.运行时对象表示

HotSpot JVM 使用称为[oop](https://www.baeldung.com/java-memory-layout)或普通对象指针[的](https://www.baeldung.com/java-memory-layout)数据结构来表示对象。这些oops等同于本机 C 指针。[instanceOop](https://github.com/openjdk/jdk15/blob/master/src/hotspot/share/oops/instanceOop.hpp)是一种特殊的oop ， 它表示 Java 中的对象实例。此外，JVM 还支持 保存在[OpenJDK 源代码树中的一些其他](http://hg.openjdk.java.net/jdk8/jdk8/hotspot/file/87ee5ee27509/src/share/vm/oops/)oops。

让我们看看 JVM 如何在内存中布置instanceOop 。

### 2.1. 对象内存布局

instanceOop的内存布局很简单：它只是对象标头，紧接着是零个或多个对实例字段的引用。

对象头的 JVM 表示包括：

-   一个标记词有多种用途，例如Biased Locking、 Identity Hash Values和GC。它不是 oop，但由于历史原因，它位于[OpenJDK 的 oop](http://hg.openjdk.java.net/jdk8/jdk8/hotspot/file/87ee5ee27509/src/share/vm/oops/markOop.hpp)源代码树中。此外，标记字状态仅包含一个 [uintptr_t](https://github.com/openjdk/jdk15/blob/e208d9aa1f185c11734a07db399bab0be77ef15f/src/hotspot/share/oops/markWord.hpp#L96)， 因此，其大小在 32 位和 64 位体系结构中分别在 4 和 8 字节之间变化
-   一个可能是压缩的 Klass word，它表示指向类元数据的指针。在 Java 7 之前，它们指向[永久代](https://www.baeldung.com/native-memory-tracking-in-jvm)，但从 Java 8 开始，它们指向元[空间](https://www.baeldung.com/native-memory-tracking-in-jvm)
-   强制对象对齐的 32 位间隙 。这使得布局对硬件更加友好，我们稍后会看到

在标头之后，将有零个或多个对实例字段的引用。在这种情况下，一个字是一个本地机器字，因此在传统的 32 位机器上是 32 位的，在更现代的系统上是 64 位的。

数组的对象头，除了mark和klass字之外，还有一个[32位的字](https://github.com/openjdk/jdk15/blob/e208d9aa1f185c11734a07db399bab0be77ef15f/src/hotspot/share/oops/arrayOop.hpp#L35)来表示它的长度。

### 2.2. 废物剖析

假设我们要从传统的 32 位架构切换到更现代的 64 位机器。起初，我们可能希望立即获得性能提升。但是，当涉及到 JVM 时，情况并非总是如此。

这种可能的性能下降的主要原因是 64 位对象引用。64 位引用占用的空间是 32 位引用的两倍，因此这通常会导致更多的内存消耗和更频繁的 GC 周期。专用于 GC 周期的时间越多，应用程序线程的 CPU 执行片就越少。

那么，我们是否应该切换回去并再次使用那些 32 位架构？即使这是一个选项，如果不做更多的工作，我们也不可能在 32 位进程空间中拥有超过 4 GB 的堆空间。

## 3. 压缩的 OOP

事实证明，JVM 可以通过压缩对象指针或oops 来避免内存浪费， 因此我们可以两全其美：在 64 位机器中允许超过 4 GB 的堆空间和 32 位引用！

### 3.1. 基本优化

正如我们之前看到的，JVM 向对象添加了填充，以便它们的大小是 8 字节的倍数。使用这些填充， oops 中的最后三位 始终为零。这是因为 8 的倍数在二进制中总是以000结尾。

![无标题图 2](https://www.baeldung.com/wp-content/uploads/2019/04/Untitled-Diagram-2-e1554193172973.jpg)

由于 JVM 已经知道最后三位始终为零，因此将这些无关紧要的零存储在堆中毫无意义。相反，它假设它们在那里并存储了 3 个我们以前无法放入 32 位的更重要的位。现在，我们有一个带有 3 个右移零的 32 位地址，因此我们将一个 35 位指针压缩为一个 32 位指针。这意味着我们最多可以使用 32 GB – 2 32+3 =2 35 = 32 GB – 堆空间而不使用 64 位引用。

为了使这个优化工作，当 JVM 需要在内存中找到一个对象时，它会将指针向左移动 3 位(基本上是将那些 3 个零加回到末尾)。另一方面，当加载指向堆的指针时，JVM 将指针向右移动 3 位以丢弃那些先前添加的零。基本上，JVM 会执行更多的计算以节省一些空间。 幸运的是，对于大多数 CPU 来说，位移位是一项非常微不足道的操作。

要启用 oop 压缩，我们可以使用 -XX:+UseCompressedOops 调整标志。oop 压缩是从 Java 7 开始的 默认行为，只要最大堆大小小于 32 GB。当最大堆大小超过 32 GB 时，JVM 将自动关闭 oop 压缩。因此，需要以不同方式管理超过 32 Gb 堆大小的内存利用率。

### 3.2. 超过 32 GB

当 Java 堆大小大于 32GB 时，也可以使用压缩指针。尽管默认的对象对齐方式是 8 字节，但可以使用-XX: ObjectAlignmentInBytes 调整标志配置该值 。指定的值应该是 2 的幂并且必须在 8 和 256 的范围内。

我们可以使用压缩指针计算最大可能的堆大小，如下所示：

```plaintext
4 GB  ObjectAlignmentInBytes
```

例如，当对象对齐为 16 字节时，我们最多可以使用 64 GB 的堆空间和压缩指针。

请注意，随着对齐值的增加，对象之间未使用的空间也可能增加。因此，我们可能不会意识到使用具有大 Java 堆大小的压缩指针的任何好处。

### 3.3. 未来的GC

[ZGC是](https://www.baeldung.com/jvm-zgc-garbage-collector)[Java 11](https://openjdk.java.net/jeps/333)中的新增功能，是一种实验性和可扩展的低延迟垃圾收集器。

它可以处理不同范围的堆大小，同时将 GC 暂停时间保持在 10 毫秒以下。由于ZGC需要使用[64位彩色指针](https://youtu.be/kF_r3GE3zOo?t=643)， 所以不支持压缩引用。因此，必须权衡使用像 ZGC 这样的超低延迟 GC 与使用更多内存。

从 Java 15 开始，ZGC 支持压缩类指针，但仍然缺乏对压缩 OOP 的支持。

然而，所有新的 GC 算法都不会为了低延迟而牺牲内存。例如， [Shenandoah GC](https://openjdk.java.net/jeps/379)除了是暂停时间短的 GC 之外还支持压缩引用。

此外，Shenandoah 和 ZGC 都已[在 Java 15](https://openjdk.java.net/projects/jdk/15/)中完成。

## 4。总结

在本文中，我们描述了64 位架构中的 JVM 内存管理问题。我们查看了压缩指针和对象对齐，并且了解了 JVM 如何解决这些问题，从而允许我们使用更大的堆大小、更少浪费的指针和最少的额外计算。

有关压缩引用的更详细讨论，强烈建议查看[Aleksey Shipilëv](https://shipilev.net/jvm/anatomy-quarks/23-compressed-references/)的另一篇精彩文章。另外，要了解对象分配在 HotSpot JVM 中是如何工作的，请查看[Java 中对象的内存布局一](https://www.baeldung.com/java-memory-layout)文。