## 1. 概述

在日常工作中，我们往往并不关心JVM的内部内存分配情况。

但是，了解 JVM 内存模型的基础知识对于性能优化和提高代码质量大有帮助。

在本文中，我们将探讨静态方法和成员的 JVM 存储。

## 2. JVM的内存分类

在深入探讨静态成员的内存分配之前，我们必须重新认识一下 JVM 的内存结构。

### 2.1. 堆内存

[堆内存是](https://www.baeldung.com/java-stack-heap#heap-space-in-java)所有 JVM 线程共享的运行时数据区域，用于为所有类实例和数组分配内存。

Java 将堆内存分为两类——新生代和老年代。

JVM 在内部将新生代分为 Eden 和 Survivor Space。同样，Tenured Space 是 Old Generation 的正式名称。

堆内存中对象的生命周期由[称为垃圾收集器](https://www.baeldung.com/jvm-garbage-collectors)的自动内存管理系统管理。

因此，垃圾收集器可以自动释放对象或将其移动到堆内存的各个部分(年轻代到老年代)。

### 2.2. 非堆内存

非堆内存主要由存储类结构、字段、方法数据和方法/构造函数代码的方法区组成。

与Heap内存类似，所有JVM线程都可以访问方法区。

方法区，也称为永久代 (PermGen)，在逻辑上被视为堆内存的一部分，尽管 JVM 的更简单实现可能选择不对其进行垃圾回收。

然而，[Java 8 移除了 PermGen 空间](https://openjdk.java.net/jeps/122)并引入了一个名为 Metaspace 的新原生内存空间。

### 2.3. 高速缓存存储器

JVM预留缓存内存区域用于编译和存储本地代码，例如JVM内部结构和JIT编译器产生的本地代码。

## 3. Java 8之前的静态成员存储

在 Java 8 之前，PermGen 存储静态成员，如静态方法和静态变量。此外，PermGen 还存储 interned 字符串。

换句话说，PermGen 空间存储变量及其技术值，可以是基元或引用。

## 4. Java 8 及更高版本的静态成员存储

正如我们已经讨论过的，[PermGen 空间在 Java 8 中被 Metaspace 取代](https://www.baeldung.com/java-permgen-metaspace)，导致静态成员的内存分配发生变化。

从 Java 8 开始，元空间只存储类元数据，堆内存保留静态成员。此外，堆内存还为驻留字符串提供存储空间。

## 5.总结

在这篇简短的文章中，我们探讨了静态成员的 JVM 存储。

首先，我们快速了解了 JVM 的内存模型。然后，我们讨论了 Java 8 前后静态成员的 JVM 存储。

简单地说，我们知道静态成员是 Java 8 之前的 PermGen 的一部分。但是，从 Java 8 开始，它们是堆内存的一部分。