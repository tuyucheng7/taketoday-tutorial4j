## 1. 概述

在本文中，我们将学习Java提供的有关队列的内容。首先，我们将探讨[Java 集合框架](https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/util/package-summary.html#CollectionsFramework)中队列的基础知识。

接下来，我们将探讨Java集合框架中的固定大小队列实现。最后，我们将创建一个固定大小的队列实现。

## 2.Java集合框架队列

Java 集合框架提供了不同的[队列](https://www.baeldung.com/java-queue)实现，我们可以根据需要使用它们。例如，如果我们需要一个线程安全的实现，我们可以使用[ConcurrentLinkedQueue](https://www.baeldung.com/java-concurrent-queues)。同样，如果我们需要指定元素在队列中的排序方式，我们可以使用[PriorityQueue](https://www.baeldung.com/cs/priority-queue)。

Java 集合框架提供了几种不同的固定大小队列实现。一种这样的实现是[ArrayBlockingQueue——](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ArrayBlockingQueue.html)一个使用固定数组存储元素的 FIFO 有界队列。队列的大小一旦创建就无法修改。

另一个例子是[LinkedBlockingQueue](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/LinkedBlockingQueue.html)。此实现也是一个 FIFO 队列，但它是有界的，因此如果未设置容量，则队列的限制将为Integer.MAX_VALUE元素。当我们不在并发环境中时，链接队列比数组队列提供更好的吞吐量。

我们可以在Java 集合框架中找到的最后一个固定大小队列是LinkedBlockingDeque[实现](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/LinkedBlockingQueue.html)。但是，请注意，此类实现了Deque接口而不仅仅是 Queue接口。

尽管该框架提供了许多队列实现，但我们可能并不总能找到适合我们解决方案的一个。在这种情况下，我们可以创建自己的实现。

## 3. Java中的队列接口

为了更好地理解示例，让我们仔细看看Queue接口。Queue接口扩展了Collection接口并添加了特定 的插入、提取和检查操作。每个操作都有两种形式，一种在操作失败时抛出异常，另一种返回特定值。

我们来看看Queue接口中定义的插入操作：

```java
boolean add(E e);
boolean offer(E e);
```

如果队列未满，两者都会将一个新元素添加到队列的尾部。offer()方法在队列满时会返回false，无法插入元素，而add()方法会抛出IllegalStateException异常。

接下来，我们在Queue接口中定义了提取操作 ：

```java
E remove();
E poll();
```

这些方法将返回队列的头部并从队列中删除元素。它们之间的区别在于，如果队列为空， poll()方法将返回null，而remove()方法将抛出 NoSuchElementException异常。

最后，Queue接口中定义的检查方法：

```java
E element();
E peek();
```

请注意Queue接口的所有方法中的类型E。这意味着Queue 接口是[通用](https://www.baeldung.com/java-generics)的：

```java
public interface Queue<E> extends Collection<E> {
    // ...
}
```

## 4. FIFO 固定大小队列实现，满时移除最旧的元素

让我们创建我们的队列实现，一个固定大小的 FIFO 队列，它将在最老的元素满时删除它。我们称它为FifoFixedSizeQueue。

AbstractQueue [抽象类](https://www.baeldung.com/java-abstract-class)是一个很好的起点，因为它定义了一个 FIFO 队列并实现了Queue和Collection接口中的大部分方法。

让我们将固定大小的队列实现为一个元素数组。我们将使用一个对象数组 来存储我们的数据，这允许我们插入任何类型的对象。此外，我们将保留一个计数属性来指示队列中元素的数量：

```java
public class FifoFixedSizeQueue<E> extends AbstractQueue<E> {
    final Object[] items;
    int count;

    public FifoFixedSizeQueue(int capacity) {
        super();

        items = new Object[capacity];
        count = 0;
    }

    ...
}

```

在上面，我们可以看到构造函数初始化了一个数组，该数组将保存队列的元素和计数属性。由于队列为空，数组的所有元素都将为空，计数属性将为零。

接下来，让我们实现所有的抽象方法。

### 4.1. offer ()方法实现

主要规则是应该插入所有 元素，但是如果队列已满，则必须先移除最老的元素。我们还需要确保队列不允许插入空元素：

```java
public boolean offer(E e) {
    if (e == null) {
        throw new NullPointerException("Queue doesn't allow nulls");
    }
    if (count == items.length) {
        this.poll();
    }
    this.items[count] = e;
    count++;
    return true;
}

```

首先，由于不允许空值，我们检查元素是否为空。如果是这种情况，我们将抛出一个NullPointerException：

```java
if (e == null) {
    throw new NullPointerException("Queue doesn't allow nulls");
}

```

接下来，我们检查队列是否已满，如果是，我们将从队列中删除最老的元素：

```java
while (count >= items.length) {
    this.poll();
}

```

最后，我们将元素插入到队列的尾部并更新元素的队列数：

```java
this.items[count] = e;
count++;

```

### 4.2. poll()方法实现

poll()方法将返回队列的头部元素并将其从队列中移除。如果队列为空，poll()方法将返回null：

```java
@Override
public E poll() {
    if (count <= 0) {
        return null;
    }
    E item = (E) items[0];
    shiftLeft();
    count--;
    return item;
}

```

首先，我们检查队列是否为空，如果为空则返回null：

```java
if (count <= 0) {
    return null;
}

```

如果不是，我们将队列的头部存储在一个局部变量中，该变量将在最后返回，并将队列的所有元素向队列头部移动一步，调用shiftLeft()方法：

```java
E item = (E) items[0];
shiftLeft();
```

最后，我们更新元素的队列数量并返回队列的头部。

在继续之前，让我们检查一下方法shiftLeft()。此方法从队列中的第二个元素开始，一直到末尾，将每个元素移到队列头部的一个位置：

```java
private void shiftLeft() {
    int i = 1;
    while (i < items.length) {
        if (items[i] == null) {
            break;
        }
        items[i - 1] = items[i];
        i++;
    }
}

```

### 4.3. Peek方法实现

peek()方法与 poll()方法一样工作， 但不会从队列中删除元素：

```java
public E peek() {
    if (count <= 0) {
        return null;
    }
    return (E) items[0];
}
```

## 5.总结

在本文中，我们介绍了Java集合框架队列的基础知识。我们已经了解了框架必须提供的固定大小队列，并学习了如何创建我们自己的队列。