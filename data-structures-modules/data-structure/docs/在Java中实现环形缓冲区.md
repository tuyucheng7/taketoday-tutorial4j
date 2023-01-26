## 1. 概述

在本教程中，我们将学习如何在Java中实现环形缓冲区。

## 2.环形缓冲区

Ring Buffer(或Circular Buffer)是一种有界循环数据结构，用于在两个或多个线程之间缓冲数据。当我们不断写入环形缓冲区时，它会在到达末尾时环绕。

### 2.1. 这个怎么运作

环形缓冲区是使用在边界处环绕的固定大小数组实现的。

除了数组之外，它还跟踪三件事：

-   缓冲区中的下一个可用插槽以插入元素，
-   缓冲区中的下一个未读元素，
-   和数组的结尾——缓冲区环绕到数组开头的点

[![环形缓冲区阵列](https://www.baeldung.com/wp-content/uploads/2020/06/Ring-Buffer-Array-768x376-1.jpeg)](https://www.baeldung.com/wp-content/uploads/2020/06/Ring-Buffer-Array-768x376-1.jpeg)

环形缓冲区如何处理这些要求的机制因实现而异。例如，关于该主题的[维基百科](https://en.wikipedia.org/wiki/Circular_buffer#Circular_buffer_mechanics)条目显示了一种使用四指针的方法。

我们将借用[Disruptor](https://www.baeldung.com/lmax-disruptor-concurrency)使用序列实现环形缓冲区的方法。

我们需要知道的第一件事是容量——缓冲区的固定最大大小。接下来，我们将使用两个单调递增的 序列：

-   写入序列：从 -1 开始，随着我们插入一个元素而递增 1
-   读取序列：从 0 开始，随着我们消耗一个元素而递增 1

我们可以使用 mod 操作将序列映射到数组中的索引：

```plaintext
arrayIndex = sequence % capacity

```

mod 操作将序列环绕边界以在缓冲区中派生一个槽：

[![RB环](https://www.baeldung.com/wp-content/uploads/2020/06/RB-RING-1-768x409-1.jpeg)](https://www.baeldung.com/wp-content/uploads/2020/06/RB-RING-1-768x409-1.jpeg)

让我们看看如何插入一个元素：

```plaintext
buffer[++writeSequence % capacity] = element

```

我们在插入元素之前预先增加序列。

为了消耗一个元素，我们做一个后增量：

```plaintext
element = buffer[readSequence++ % capacity]

```

在这种情况下，我们对序列执行后增量。消耗一个元素不会将它从缓冲区中移除——它只是留在数组中直到被覆盖。

### 2.2. 空缓冲区和满缓冲区

当我们环绕数组时，我们将开始覆盖缓冲区中的数据。如果缓冲区已满，我们可以选择要么覆盖最旧的数据而不管读取器是否已经消费过它，要么阻止覆盖尚未读取的数据。

如果读者可以承受错过中间值或旧值(例如，股票行情代码)，我们可以覆盖数据而无需等待它被消耗。另一方面，如果读者必须消耗所有值(如电子商务交易)，我们应该等待(阻塞/忙等待)直到缓冲区有可用的槽。

如果缓冲区的大小等于其容量，则缓冲区已满，其中其大小等于未读元素的数量：

```plaintext
size = (writeSequence - readSequence) + 1
isFull = (size == capacity)

```

[![RB全](https://www.baeldung.com/wp-content/uploads/2020/06/RB-Full-267x300.jpeg)](https://www.baeldung.com/wp-content/uploads/2020/06/RB-Full.jpeg)

如果写入序列滞后于读取序列，则缓冲区为空：

```plaintext
isEmpty = writeSequence < readSequence

```

[![RB 空](https://www.baeldung.com/wp-content/uploads/2020/06/RB-EMPTY-1-768x405-1.jpeg)](https://www.baeldung.com/wp-content/uploads/2020/06/RB-EMPTY-1-768x405-1.jpeg)如果缓冲区为空，则返回 空值。

### 2.2. 的优点和缺点

环形缓冲区是一种高效的 FIFO 缓冲区。它使用可以预先分配的固定大小数组，并允许高效的内存访问模式。所有缓冲区操作都是恒定时间O(1)，包括消耗一个元素，因为它不需要移动元素。

另一方面，确定环形缓冲区的正确大小至关重要。例如，如果缓冲区过小且读取速度慢，写入操作可能会阻塞很长时间。我们可以使用动态调整大小，但它需要四处移动数据，而且我们会错过上面讨论的大部分优势。

## 3.Java实现

现在我们了解了环形缓冲区的工作原理，让我们继续用Java实现它。

### 3.1. 初始化

首先，让我们定义一个构造函数，用预定义的容量初始化缓冲区：

```java
public CircularBuffer(int capacity) {
    this.capacity = (capacity < 1) ? DEFAULT_CAPACITY : capacity;
    this.data = (E[]) new Object[this.capacity];
    this.readSequence = 0;
    this.writeSequence = -1;
}

```

这将创建一个空缓冲区并初始化序列字段，如上一节所述。

### 3.2. 提供

接下来，我们将实现 提供操作，将一个元素插入缓冲区中下一个可用插槽，并在成功时返回true。如果缓冲区找不到空槽，则返回false ，也就是说，我们不能覆盖未读的值。

让我们用 Java实现offer方法：

```java
public boolean offer(E element) {
    boolean isFull = (writeSequence - readSequence) + 1 == capacity;
    if (!isFull) {
        int nextWriteSeq = writeSequence + 1;
        data[nextWriteSeq % capacity] = element;
        writeSequence++;
        return true;
    }
    return false;
}

```

因此，我们正在递增写入序列并计算数组中下一个可用插槽的索引。然后，我们将数据写入缓冲区并存储更新的写入序列。

让我们试试看：

```java
@Test
public void givenCircularBuffer_whenAnElementIsEnqueued_thenSizeIsOne() {
    CircularBuffer buffer = new CircularBuffer<>(defaultCapacity);

    assertTrue(buffer.offer("Square"));
    assertEquals(1, buffer.size());
}

```

### 3.3. 轮询

最后，我们将实现检索和删除下一个未读元素的轮询操作。轮询操作不会删除元素，但会增加读取序列。

让我们来实现它：

```java
public E poll() {
    boolean isEmpty = writeSequence < readSequence;
    if (!isEmpty) {
        E nextValue = data[readSequence % capacity];
        readSequence++;
        return nextValue;
    }
    return null;
}

```

在这里，我们通过计算数组中的索引来读取当前读取序列中的数据。然后，如果缓冲区不为空，我们将递增序列并返回值。

让我们测试一下：

```java
@Test
public void givenCircularBuffer_whenAnElementIsDequeued_thenElementMatchesEnqueuedElement() {
    CircularBuffer buffer = new CircularBuffer<>(defaultCapacity);
    buffer.offer("Triangle");
    String shape = buffer.poll();

    assertEquals("Triangle", shape);
}

```

## 4.生产者消费者问题

我们已经讨论了使用环形缓冲区在两个或多个线程之间交换数据，这是称为[生产者-消费者](https://en.wikipedia.org/wiki/Producer–consumer_problem)问题的同步问题的一个示例。[在Java中，我们可以使用信号量](https://www.baeldung.com/java-semaphore)、[有界队列](https://www.baeldung.com/java-blocking-queue#multithreaded-producer-consumer-example)、环形缓冲区等多种方式来解决生产者-消费者问题。

让我们实现一个基于环形缓冲区的解决方案。

### 4.1. 可变序列字段

我们对环形缓冲区的实现不是线程安全的。让我们为简单的单一生产者和单一消费者案例使其成为线程安全的。

生产者将数据写入缓冲区并递增 writeSequence，而消费者仅从缓冲区读取数据并递增 readSequence。因此，支持数组是无竞争的，我们可以在没有任何同步的情况下逃脱。

但是我们仍然需要确保消费者可以看到writeSequence字段的最新值([visibility](https://www.baeldung.com/java-volatile#1-memory-visibility))，并且 在缓冲区中数据实际可用之前writeSequence不会更新( [ordering](https://www.baeldung.com/java-volatile#2-reordering))。

[在这种情况下，我们可以通过将序列字段设置为volatile](https://www.baeldung.com/java-volatile)来使环形缓冲区并发且无锁：

```java
private volatile int writeSequence = -1, readSequence = 0;

```

在offer方法中，对 volatile字段 writeSequence的写入保证了对缓冲区的写入发生在更新序列之前。同时，volatile可见性保证确保消费者始终看到writeSequence的最新值。

### 4.2. 制作人

让我们实现一个写入环形缓冲区的简单生产者 Runnable ：

```java
public void run() {
    for (int i = 0; i < items.length;) {
        if (buffer.offer(items[i])) {
           System.out.println("Produced: " + items[i]);
            i++;
        }
    }
}

```

生产者线程将在循环中等待一个空槽(忙等待)。

### 4.3. 消费者

我们将实现一个从缓冲区读取的消费者Callable：

```java
public T[] call() {
    T[] items = (T[]) new Object[expectedCount];
    for (int i = 0; i < items.length;) {
        T item = buffer.poll();
        if (item != null) {
            items[i++] = item;
            System.out.println("Consumed: " + item);
        }
    }
    return items;
}

```

如果消费者线程从缓冲区接收到空值，则它会继续而不打印。

让我们编写我们的驱动程序代码：

```java
executorService.submit(new Thread(new Producer<String>(buffer)));
executorService.submit(new Thread(new Consumer<String>(buffer)));

```

执行我们的生产者-消费者程序会产生如下输出：

```bash
Produced: Circle
Produced: Triangle
  Consumed: Circle
Produced: Rectangle
  Consumed: Triangle
  Consumed: Rectangle
Produced: Square
Produced: Rhombus
  Consumed: Square
Produced: Trapezoid
  Consumed: Rhombus
  Consumed: Trapezoid
Produced: Pentagon
Produced: Pentagram
Produced: Hexagon
  Consumed: Pentagon
  Consumed: Pentagram
Produced: Hexagram
  Consumed: Hexagon
  Consumed: Hexagram

```

## 5.总结

在本教程中，我们学习了如何实现环形缓冲区，并探讨了如何使用它来解决生产者-消费者问题。