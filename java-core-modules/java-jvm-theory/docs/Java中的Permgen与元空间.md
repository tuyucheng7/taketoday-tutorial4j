## **一、简介**

在本快速教程中，**我们将研究**Java 环境中 PermGen 和 Metaspace 内存区域之间的差异。

重要的是要记住，从 Java 8 开始，Metaspace 取代了 PermGen——带来了一些实质性的变化。

## **2.永久代**

**PermGen（永久代）是从主存堆中分离出来的特殊堆空间**。

JVM 跟踪 PermGen 中加载的类元数据。此外，JVM 将所有静态内容存储在该内存部分中。这包括所有静态方法、原始变量和对静态对象的引用。

此外，**它还包含有关字节码、名称和 JIT 信息的数据**。在 Java 7 之前，String Pool 也是这块内存的一部分。[我们的文章](https://www.baeldung.com/java-string-pool)中列出了固定池大小的缺点。

32 位 JVM 的默认最大内存大小为 64 MB，64 位版本为 82 MB。

但是，我们可以使用 JVM 选项更改默认大小：

-   *-XX:PermSize=[size]*是PermGen空间的初始或最小大小
-   *-XX:MaxPermSize=[size]*为最大尺寸

最重要的是，**Oracle 在 JDK 8 版本中完全移除了这个内存空间。**因此，如果我们在 Java 8 和更新版本中使用这些调整标志，我们将收到以下警告：

```bash
>> java -XX:PermSize=100m -XX:MaxPermSize=200m -version
OpenJDK 64-Bit Server VM warning: Ignoring option PermSize; support was removed in 8.0
OpenJDK 64-Bit Server VM warning: Ignoring option MaxPermSize; support was removed in 8.0
...复制
```

**由于内存大小有限，PermGen 参与了著名的\*OutOfMemoryError 的\***产生。简而言之，[类加载器](https://www.baeldung.com/java-classloaders)没有正确地进行垃圾回收，因此产生了内存泄漏。

因此，我们收到[内存空间错误](https://www.baeldung.com/java-gc-overhead-limit-exceeded)；这主要发生在开发环境中，同时创建新的类加载器。

## **3.元空间**

简单的说，Metaspace是一个新的内存空间——从Java 8版本开始；**它取代了旧的 PermGen 内存空间**。最显着的区别是它如何处理内存分配。

具体来说，**这个本机内存区域默认会自动增长**。

我们还有新的标志来调整内存：

-   *MetaspaceSize*和*MaxMetaspaceSize——*我们可以设置元空间上限。
-   *MinMetaspaceFreeRatio – 是*[垃圾收集](https://www.baeldung.com/jvm-garbage-collectors)后可用的类元数据容量的最小百分比
-   *MaxMetaspaceFreeRatio* – 是垃圾收集后可用的类元数据容量的最大百分比，以避免空间量减少

此外，垃圾收集过程也从这一变化中获得了一些好处。一旦类元数据使用量达到其最大元空间大小，垃圾收集器现在会自动触发对死类的清理。

因此，通过这种改进，JVM 减少了出现***OutOfMemory\*****错误的****机会**。

尽管有所有这些改进，我们仍然需要监视和[调整元空间](https://www.baeldung.com/jvm-parameters)以避免内存泄漏。

## **4.总结**

在这篇简短的文章中，我们简要描述了 PermGen 和 Metaspace 内存区域。此外，我们还解释了它们之间的主要区别。

PermGen 仍然存在于 JDK 7 和更早的版本中，但是 Metaspace 为我们的应用程序提供了更灵活和可靠的内存使用。