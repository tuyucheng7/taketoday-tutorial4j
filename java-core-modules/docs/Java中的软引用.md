## **一、概述**

在这篇简短的文章中，我们将讨论 Java 中的软引用。

我们将解释它们是什么、我们为什么需要它们以及如何创建它们。

## **2. 什么是软引用？**

垃圾收集器可以根据内存需求清除软引用对象（或软可达对象）。**软可达对象没有指向它的强引用**。

当垃圾收集器被调用时，它开始遍历堆中的所有元素。GC 将引用类型对象存储在一个特殊的队列中。

检查完堆中的所有对象后，GC 会通过从上述队列中删除对象来确定应删除哪些实例。

这些规则因 JVM 实现而异，但文档指出，**在 JVM 抛出 OutOfMemoryError 之前，保证清除对软可访问对象的所有软引用\*。\***

但是，不能保证清除软引用的时间或清除对不同对象的一组此类引用的顺序。

通常，JVM 实现会在清除最近创建的引用或最近使用的引用之间进行选择。

软可达对象在最后一次被引用后会保持存活一段时间。默认值是堆中每兆字节的生命周期一秒。*[可以使用-XX:SoftRefLRUPolicyMSPerMB](http://www.oracle.com/technetwork/java/hotspotfaq-138619.html#gc_softrefs)*标志调整此值。

例如，要将值更改为 2.5 秒（2500 毫秒），我们可以使用：

```bash
-XX:SoftRefLRUPolicyMSPerMB=2500复制
```

与弱引用相比，软引用可以有更长的生命周期，因为它们会一直存在，直到需要额外的内存。

因此，如果我们需要尽可能长时间地在内存中保存对象，它们是更好的选择。

## **3.软引用的用例**

**软引用可用于实现内存敏感缓存**，其中内存管理是一个非常重要的因素。

只要软引用的引用对象是强可达的，也就是说——实际在使用中，引用就不会被清除。

例如，缓存可以通过保持对这些条目的强引用来防止其最近使用的条目被丢弃，而让垃圾收集器自行决定丢弃剩余的条目。

## **4. 使用软引用**

[*在 Java 中，软引用由java.lang.ref.SoftReference*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ref/SoftReference.html)类表示。

我们有两个选项来初始化它。

第一种方法是只传递一个引用对象：

```java
StringBuilder builder = new StringBuilder();
SoftReference<StringBuilder> reference1 = new SoftReference<>(builder);复制
```

[*第二个选项意味着传递对java.lang.ref.ReferenceQueue 的*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ref/ReferenceQueue.html)引用以及对引用对象的引用。**引用队列旨在让我们了解垃圾收集器执行的操作。**当它决定删除该引用的引用对象时，它将引用对象附加到引用队列。

以下是使用ReferenceQueue 初始化*SoftReference 的*方法*：*

```java
ReferenceQueue<StringBuilder> referenceQueue = new ReferenceQueue<>();
SoftReference<StringBuilder> reference2
 = new SoftReference<>(builder, referenceQueue);复制
```

作为[*java.lang.ref.Reference*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ref/Reference.html)，它包含方法[*get*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ref/Reference.html#get())和[*clear*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ref/Reference.html#clear())分别获取和重置引用对象：

```java
StringBuilder builder1 = reference2.get();
reference2.clear();
StringBuilder builder2 = reference2.get(); // null
复制
```

每次我们处理这种引用时，我们都需要确保 get 返回的引用对象*存在*：

```java
StringBuilder builder3 = reference2.get();
if (builder3 != null) {
    // GC hasn't removed the instance yet
} else {
    // GC has cleared the instance
}复制
```

## **5.结论**

在本教程中，我们熟悉了软引用的概念及其用例。

此外，我们还学习了如何创建一个并以编程方式使用它。