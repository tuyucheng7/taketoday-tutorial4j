## 1. 概述

在本教程中，我们将了解什么是非阻塞数据结构，以及为什么它们是基于锁的并发数据结构的重要替代方案。

首先，我们将复习一些术语，例如无障碍、无锁和无等待。

其次，我们将了解非阻塞算法的基本构建块，例如CAS(比较和交换)。

然后我们将研究Java中无锁队列的实现，最后，我们将概述实现无等待的方法。

## 2. 锁定与饥饿

首先，**让我们看看阻塞线程和饥饿线程之间的区别**。

<img src="../assets/img_2.png">

在上图中，线程2获取了数据结构上的锁。当线程1也尝试获取锁时，它需要等待直到线程2释放锁；在获得锁之前它不会继续执行。**如果我们在线程2持有锁时挂起它，那么线程1将不得不永远等待**。

下图说明了线程饥饿：

<img src="../assets/img_3.png">

在这里，线程2访问数据结构，但不获取锁。线程1同时尝试访问数据结构，检测到并发访问，并立即返回，通知线程无法完成(红色)操作。然后线程1将再次尝试，直到成功完成操作(绿色)。

这种方法的优点是我们不需要锁。**但是，可能发生的情况是，如果线程2(或其他线程)以高频率访问数据结构，那么线程1需要进行大量尝试，直到最终成功。我们称之为饥饿**。

稍后我们将看到CAS操作如何实现非阻塞访问。

## 3. 非阻塞数据结构的类型

我们可以区分三个级别的非阻塞数据结构。

### 3.1 无障碍

无障碍是非阻塞数据结构中最弱的形式。**在这里，我们只要求保证一个线程在所有其他线程都挂起的情况下继续进行**。

更准确地说，如果所有其他线程都被挂起，一个线程将不会继续饿死。从这个意义上说，这与使用锁不同，如果线程正在等待锁并且持有锁的线程被挂起，则等待线程将永远等待。

### 3.2 无锁

**如果在任何时候至少有一个线程可以继续，则数据结构提供无锁**。所有其他线程可能正在挨饿。与无障碍的不同之处在于，即使没有线程被挂起，也至少有一个非饥饿线程。

### 3.3 无等待

如果一个数据结构是无锁的，并且每个线程都保证在有限数量的步骤后继续进行，那么数据结构就是无等待的，也就是说，线程不会因为“不合理的大”数量的步骤而挨饿。

### 3.4 概括

让我们用图形表示来总结这些定义：

<img src="../assets/img_4.png">

图中的第一部分显示了无障碍，因为只要我们暂停其他线程(底部为黄色)，线程1(顶部线程)就可以继续(绿色箭头)。

中间部分显示无锁。至少线程1可以前进，而其他线程可能正在挨饿(红色箭头)。

最后一部分显示了无等待。在这里，我们保证线程1在饥饿一段时间(红色箭头)后可以继续(绿色箭头)。

## 4. 非阻塞原语

在本节中，我们将介绍三个基本操作，它们可以帮助我们在数据结构上构建无锁操作。

### 4.1 Compare and Swap

**用于避免锁定的基本操作之一是比较和交换(CAS)操作**。

CAS的思想是，只有当变量的值仍然与我们从主内存中获取变量值时的值相同时，它才会被更新。**CAS是一个原子操作，这意味着获取和更新是一个单一的操作**：

<img src="../assets/img_5.png">

在这里，两个线程都从主内存中获取值3。线程2成功(绿色)并将变量更新为8。由于线程1的第一个CAS预计该值仍为3，因此CAS失败(红色)。于是线程1再次获取该值，第二次CAS成功。

**这里重要的是，CAS不会获取数据结构上的锁，但如果更新成功则返回true，否则返回false**。

以下代码片段概述了CAS的工作原理：

```java
volatile int value;

boolean cas(int expectedValue, int newValue) {
    if(value == expectedValue) {
        value = newValue;
        return true;
    }
    return false;
}
```

只有当值仍然具有预期值时，我们才使用新值更新该值，否则它返回false。以下代码片段显示了如何调用CAS：

```java
void testCas() {
    int v = value;
    int x = v + 1;

    while(!cas(v, x)) {
        v = value;
        x = v + 1;
    }
}
```

我们尝试更新我们的值，直到CAS操作成功，即返回true。

**但是，线程可能会陷入饥饿状态**。如果其他线程同时对同一变量执行CAS，则可能会发生这种情况，因此该操作对于特定线程永远不会成功(或者需要花费不合理的时间才能成功)。尽管如此，如果CAS失败，我们可以知道另一个线程已经成功，因此我们也可以确保全局进度，这是无锁所要求的。

**需要注意的是硬件应该支持CAS，使其成为真正的原子操作而无需使用锁定**。

Java在[sun.misc.Unsafe](https://www.baeldung.com/java-unsafe)类中提供了CAS的实现。但是，在大多数情况下，我们不应该直接使用这个类，而是使用[原子变量](https://www.baeldung.com/java-atomic-variables)。

此外，CAS并不能防止A-B-A问题。我们将在下一节中进行讨论。

### 4.2 加载链接/存储条件

CAS的替代方法是Load-Link/Store-Conditional。让我们首先回顾一下CAS。正如我们之前所见，CAS只有在主存中的值仍然是我们期望的值时才会更新该值。

但是，如果值已更改，并且同时又更改回其先前的值，则CAS也会成功。

下图说明了这种情况：

<img src="../assets/img_6.png">

线程1和线程2都读取了变量的值，即3。然后线程2执行CAS，成功地将变量设置为8。然后，线程2再次执行CAS将变量设置回3，这也成功了。最后，线程1执行CAS，期望值为3，并且也成功，即使我们的变量的值在两者之间被修改了两次。

这被称为ABA问题。当然，根据用例的不同，这种行为可能不是问题。但是，其他人可能不希望这样做。Java通过[AtomicStampedReference](https://www.baeldung.com/java-atomicstampedreference)类提供了Load-Link/Store-Conditional的实现。

### 4.3 获取并添加

另一种选择是fetch-and-add。此操作将主存储器中的变量递增给定值。**同样，重要的一点是操作以原子方式发生，这意味着没有其他线程可以干扰**。

Java在其原子类中提供了fetch-and-add的实现。例如AtomicInteger.incrementAndGet()，它递增值并返回新值；和AtomicInteger.getAndIncrement()，它返回旧值然后递增该值。

## 5. 从多个线程访问链表队列

为了更好地理解两个(或多个)线程同时访问队列的问题，让我们看一个队列和两个尝试同时添加元素的线程。

我们将要查看的队列是一个双链接的FIFO队列，我们在最后一个元素(L)之后添加新元素，变量tail指向最后一个元素：

<img src="../assets/img_7.png">

要添加一个新元素，线程需要执行三个步骤：

1. 创建新元素(N和M)，并将指向下一个元素的指针设置为null
2. 将前一个元素的引用指向L，将L的下一个元素的引用分别指向N和M
3. tail节点指向N和M

<img src="../assets/img_8.png">

如果两个线程同时执行这些步骤，会出现什么问题？如果上图中的步骤按照ABCD或ACBD的顺序执行，L以及tail都会指向M，N将保持与队列断开连接。

如果步骤按照ACDB的顺序执行，tail会指向N，而L会指向M，这将导致队列不一致：

<img src="../assets/img_9.png">

**当然，解决这个问题的一种方法是让一个线程获取队列上的锁。我们将在下一章中看到的解决方案将通过使用我们之前提到的CAS操作，在无锁操作的帮助下解决问题**。

## 6. Java中的非阻塞队列

让我们看一下Java中的基本无锁队列。首先，让我们看一下类成员和构造函数：

```java
public class NonBlockingQueue<T> {

    private final AtomicReference<Node<T>> head, tail;
    private final AtomicInteger size;

    public NonBlockingQueue() {
        head = new AtomicReference<>(null);
        tail = new AtomicReference<>(null);
        size = new AtomicInteger();
        size.set(0);
    }
}
```

**重要的部分是将head和tail引用声明为AtomicReferences，这可确保对这些引用的任何更新都是原子操作**。Java中的这种数据类型实现了必要的CAS操作。

接下来我们看一下Node类的实现：

```java
private static class Node<T> {
    private volatile T value;
    private volatile Node<T> next;
    private volatile Node<T> previous;

    public Node(T value) {
        this.value = value;
        this.next = null;
    }

    // getters and setters ...
}
```

**在这里，重要的部分是将对previous和next节点的引用声明为volatile**。这确保我们始终在主内存中更新这些引用(因此对所有线程都是直接可见的)，实际节点值value也是如此。

### 6.1 无锁添加

我们的无锁添加操作将确保我们在尾部添加新元素并且不会与队列断开连接，即使多个线程想要同时添加新元素：

```java
public void add(T element) {
    if (element == null) {
        throw new NullPointerException();
    }

    Node<T> node = new Node<>(element);
    Node<T> currentTail;
    do {
        currentTail = tail.get();
        node.setPrevious(currentTail);
    } while (!tail.compareAndSet(currentTail, node));

    if (node.previous != null) {
        node.previous.next = node;
    }

    head.compareAndSet(null, node); // if we are inserting the first element
    size.incrementAndGet();
}
```

要注意的重要部分是突出显示的行，我们尝试将新节点添加到队列中，直到CAS操作成功更新tail，该tail必须仍然是我们添加新节点的同一个tail。

### 6.2 无锁获取

与添加操作类似，无锁获取操作将确保我们返回最后一个元素并将tail移动到当前位置：

```java
public T get() {
    if (head.get() == null) {
        throw new NoSuchElementException();
    }

    Node<T> currentHead;
    Node<T> nextNode;
    do {
        currentHead = head.get();
        nextNode = currentHead.getNext();
    } while (!head.compareAndSet(currentHead, nextNode));

    size.decrementAndGet();
    return currentHead.getValue();
}
```

同样，要注意的重要部分是突出显示的行。CAS操作确保只有在同时没有其他节点被移除的情况下，我们才移动currentHead。

**Java已经提供了一个非阻塞队列的实现，即ConcurrentLinkedQueue**。这是[本文](https://www.cs.rochester.edu/~scott/papers/1996_PODC_queues.pdf)中描述的M.Michael和L.Scott的无锁队列的实现。Java文档指出它是一个无等待队列，实际上它是无锁的。Java 8文档正确地将实现称为无锁。

## 7. 无等待队列

正如我们所看到的，上面的实现是无锁的，但不是无等待的。如果有许多线程访问我们的队列，那么add和get方法中的while循环可能会循环很长时间(或者永远循环，尽管这不太可能)。

我们如何实现无等待？一般来说，无等待算法的实现是相当棘手的。我们建议有兴趣的读者阅读[这篇论文](http://chaoran.me/assets/pdf/wfq-ppopp16.pdf)，其中详细描述了无等待队列。**在本文中，让我们看看如何实现无等待队列的基本思想**。

无等待队列要求每个线程都取得有保证的进展(在有限数量的步骤之后)。换句话说，我们的add和get方法中的while循环必须在一定数量的步骤后成功。

为了实现这一点，我们为每个线程分配一个辅助线程。如果该辅助线程成功地将一个元素添加到队列中，它将帮助另一个线程在插入另一个元素之前插入其元素。

由于辅助线程本身有一个辅助线程，并且在整个线程列表中，每个线程都有一个辅助线程，因此我们可以保证每个线程都插入一次后，最迟插入成功。下图说明了这个想法：

<img src="../assets/img_10.png">

当然，当我们可以动态添加或删除线程时，事情会变得更加复杂。

## 8. 总结

在本文中，我们了解了非阻塞数据结构的基础知识。我们解释了不同的级别和基本操作，如CAS。

然后，我们查看了Java中无锁队列的基本实现。最后，我们概述了如何实现无等待的想法。