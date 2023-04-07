## **一、概述**

在本文中，我们将了解 Java 语言中弱引用的概念。

我们将解释它们是什么、它们的用途以及如何正确使用它们。

## **2.弱引用**

**弱引用对象在弱可达时会被垃圾收集器清除。**

弱可达性意味着一个**对象既没有强引用也没有[软](https://www.baeldung.com/java-soft-references)引用指向它**。只有遍历弱引用才能到达该对象。

首先，垃圾收集器清除了弱引用，因此不再可以访问引用对象。然后将引用放置在引用队列中（如果存在任何关联），我们可以从中获取它。

同时，以前弱可达的对象将被最终确定。

### 2.1. 弱引用与软引用

有时弱引用和软引用之间的区别不清楚。软引用基本上是一个大的 LRU 缓存。也就是说，**当引用对象在不久的将来有很好的机会被重用时，我们使用软引用**。

由于软引用充当缓存，因此即使引用对象本身不可用，它也可能继续可访问。事实上，当且仅当满足以下条件时，软引用才有资格收集：

-   引用对象不是强可达的
-   最近没有访问软引用

因此，在引用对象变得不可访问后，软引用可能会在几分钟甚至几小时内可用。另一方面，弱引用只有在其引用对象仍然存在时才可用。

## **3.****用例**

正如 Java 文档所述，**弱引用最常用于实现规范化映射**。如果映射仅包含特定值的一个实例，则该映射称为规范化。它不是创建新对象，而是在映射中查找现有对象并使用它。

当然，**这些引用最广为人知的用途是[\*WeakHashMap\*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/WeakHashMap.html)类**。[*它是Map*](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/util/Map.html)接口的实现，其中每个键都存储为对给定键的弱引用。当垃圾收集器删除一个键时，与该键关联的实体也会被删除。

有关更多信息，请查看[我们的 WeakHashMap 指南](https://www.baeldung.com/java-weakhashmap)。

另一个可以使用它们的领域是**Lapsed Listener 问题**。

发布者（或主题）持有对所有订阅者（或收听者）的强引用，以通知他们发生的事件。**当侦听器无法成功取消订阅发布者时就会出现问题。**

因此，不能对侦听器进行垃圾回收，因为对它的强引用对于发布者仍然可用。因此，可能会发生内存泄漏。

该问题的解决方案可以是一个主题持有对观察者的弱引用，允许前者在不需要取消订阅的情况下被垃圾收集（请注意，这不是一个完整的解决方案，它引入了一些其他问题不是此处介绍）。

## **4. 使用弱引用**

[*弱引用由java.lang.ref.WeakReference*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ref/WeakReference.html)类表示。我们可以通过传递一个引用作为参数来初始化它。或者，我们可以提供一个[*java.lang.ref.ReferenceQueue*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ref/ReferenceQueue.html)：

```java
Object referent = new Object();
ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();

WeakReference weakReference1 = new WeakReference<>(referent);
WeakReference weakReference2 = new WeakReference<>(referent, referenceQueue);
复制
```

引用的指称可以通过[*get方法获取，并使用*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ref/Reference.html#get())[*clear*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ref/Reference.html#clear())方法手动删除：

```java
Object referent2 = weakReference1.get();
weakReference1.clear();
复制
```

[使用这种引用的安全模式与软引用](https://www.baeldung.com/java-soft-references)相同：

```java
Object referent3 = weakReference2.get();
if (referent3 != null) {
    // GC hasn't removed the instance yet
} else {
    // GC has cleared the instance
}复制
```

## **5.结论**

在本快速教程中，我们了解了 Java 中弱引用的低级概念——并重点介绍了使用这些内容的最常见场景。