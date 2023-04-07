## **一、概述**

在本文中，我们将了解 Java 语言中的 Phantom Reference 的概念。

## **2. 幻影参考**

虚引用与[软](https://www.baeldung.com/java-soft-references)引用和[弱](https://www.baeldung.com/java-weak-reference)引用有两个主要区别。

**我们无法获得幻象引用的引用对象。**referent 永远无法通过 API 直接访问，这就是为什么我们需要一个引用队列来处理这种类型的引用。

垃圾收集器**在其引用对象的 finalize 方法执行后将**幻象引用添加到引用队列。这意味着该实例仍在内存中。

## **3.用例**

它们用于两个常见的用例。

第一种技术是**确定对象何时从内存中移除，**这有助于安排对内存敏感的任务。例如，我们可以等待一个大对象被移除，然后再加载另一个。

第二种做法是**避免使用\*finalize\*方法，改进****finalization过程**。

### **3.1. 例子**

现在，让我们实现第二个用例，实际弄清楚这种引用是如何工作的。

首先，我们需要[*PhantomReference*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ref/PhantomReference.html)类的子类来定义清除资源的方法：

```java
public class LargeObjectFinalizer extends PhantomReference<Object> {

    public LargeObjectFinalizer(
      Object referent, ReferenceQueue<? super Object> q) {
        super(referent, q);
    }

    public void finalizeResources() {
        // free resources
        System.out.println("clearing ...");
    }
}复制
```

现在我们要编写一个增强的细粒度终结：

```java
ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
List<LargeObjectFinalizer> references = new ArrayList<>();
List<Object> largeObjects = new ArrayList<>();

for (int i = 0; i < 10; ++i) {
    Object largeObject = new Object();
    largeObjects.add(largeObject);
    references.add(new LargeObjectFinalizer(largeObject, referenceQueue));
}

largeObjects = null;
System.gc();

Reference<?> referenceFromQueue;
for (PhantomReference<Object> reference : references) {
    System.out.println(reference.isEnqueued());
}

while ((referenceFromQueue = referenceQueue.poll()) != null) {
    ((LargeObjectFinalizer)referenceFromQueue).finalizeResources();
    referenceFromQueue.clear();
}
复制
```

首先，我们正在初始化所有必要的对象：*referenceQueue——*跟踪排队的引用，*references——*之后执行清理工作，*largeObjects——*模拟大型数据结构。

接下来，我们使用*Object*和*LargeObjectFinalizer*类创建这些对象。

*在调用垃圾收集器之前，我们通过取消引用largeObjects*列表来手动释放大量数据。*请注意，我们使用Runtime.getRuntime().gc()*语句的快捷方式来调用垃圾收集器。

重要的是要知道*System.gc()*不会立即触发垃圾收集——它只是提示 JVM 触发该过程。

for*循环*演示了如何确保所有引用都已入队——它将为每个引用打印出*true*。

最后，我们使用了一个*while*循环来轮询排队的引用并对它们中的每一个进行清理工作。

## **4。结论**

在本快速教程中，我们介绍了 Java 的虚引用。

我们了解了这些是什么以及它们如何在一些简单而切题的示例中发挥作用。