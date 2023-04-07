## 1.概述

在本教程中，我们将讨论Java的队列接口。

首先，我们将了解Queue的作用及其一些核心方法。接下来，我们将深入研究Java作为标准提供的一些实现。

最后，我们将在结束之前讨论线程安全。

## 2.可视化队列

让我们从一个简单的类比开始。

想象一下，我们刚刚开了我们的第一家公司——一家热狗摊。我们希望以最有效的方式为我们的小企业服务我们的新潜在客户；一次一个。首先，我们请他们在我们的展台前排好队，新顾客排在后面。由于我们的组织能力，我们现在可以公平地分发美味的热狗。

[Java中的队列](https://www.baeldung.com/cs/types-of-queues)以类似的方式工作。在我们声明我们的Queue之后，我们可以在后面添加新元素，并从前面删除它们。

事实上，我们在Java中遇到的大多数队列都以先进先出的方式工作——通常缩写为FIFO。

但是，有一个例外我们稍后会谈[到](https://www.baeldung.com/java-queue#priority_queues)。

## 3.核心方法

Queue声明了[许多](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Queue.html)需要由所有实现类编码的方法。现在让我们概述一些更重要的：

1.  offer()–在队列中插入一个新元素
2.  poll()–从队列的前面移除一个元素
3.  peek()–检查队列前面的元素而不删除它

## 4.抽象队列

[AbstractQueue](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/AbstractQueue.html)是Java提供的最简单的Queue实现。它包括一些Queue接口方法的框架实现，不包括[offer](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Queue.html#offer(E))。

当我们创建一个扩展AbstractQueue类的自定义队列时，我们必须提供一个不允许插入null元素的offer方法的实现。

此外，我们必须提供方法peek、poll、size和java.util的迭代器。

让我们使用AbstractQueue组合一个简单的Queue实现。

首先，让我们用一个LinkedList来定义我们的类来存储我们队列的元素：

```java
public class CustomBaeldungQueue<T> extends AbstractQueue<T> {

    private LinkedList<T> elements;

    public CustomBaeldungQueue() {
      this.elements = new LinkedList<T>();
    }

}
```

接下来，让我们覆盖所需的方法并提供代码：

```java
@Override
public Iterator<T> iterator() {
    return elements.iterator();
}

@Override
public int size() {
    return elements.size();
}

@Override
public boolean offer(T t) {
    if(t == null) return false;
    elements.add(t);
    return true;
}

@Override
public T poll() {
    Iterator<T> iter = elements.iterator();
    T t = iter.next();
    if(t != null){
        iter.remove();
        return t;
    }
    return null;
}

@Override
public T peek() {
    return elements.getFirst();
}
```

太好了，让我们通过快速单元测试检查它是否有效：

```java
customQueue.add(7);
customQueue.add(5);

int first = customQueue.poll();
int second = customQueue.poll();

assertEquals(7, first);
assertEquals(5, second);
```

## 4.子接口

Queue接口一般由3个主要的子接口继承。阻塞队列、传输队列和双端队列。

这3个接口共同由绝大多数Java可用队列实现。让我们快速浏览一下这些接口的用途。

### 4.1.阻塞队列

[BlockingQueue](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/BlockingQueue.html)接口支持额外的操作，这些操作强制线程根据当前状态在Queue上等待。尝试检索时，线程可能会等待队列非空，或者在添加新元素时等待队列变为空。

标准阻塞队列包括LinkedBlockingQueue、[SynchronousQueue](https://www.baeldung.com/java-synchronous-queue)和ArrayBlockingQueue。

有关更多信息，请转至我们关于[阻塞队列](https://www.baeldung.com/java-blocking-queue)的文章。

### 4.2.传输队列

[TransferQueue](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/TransferQueue.html)接口扩展了BlockingQueue接口，但针对生产者-消费者模式进行了定制。它控制从生产者到消费者的信息流，在系统中产生背压。

Java附带了TransferQueue接口的一种实现，[LinkedTransferQueue](https://www.baeldung.com/java-transfer-queue)。

### 4.3.德奎斯

Deque是Double-EndedQueue的缩写，类似于一副纸牌——可以从Deque的开头和结尾获取元素。与传统的Queue非常相似，Deque提供了添加、检索和查看位于顶部和底部的元素的方法。

有关Deque工作原理的详细指南，请查看我们的[ArrayDeque](https://www.baeldung.com/java-array-deque)[文章](https://www.baeldung.com/java-array-deque)。

## 5.优先队列

我们之前看到，我们在Java中遇到的大多数队列都遵循FIFO原则。

此规则的一个例外是[PriorityQueue](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/PriorityQueue.html)。当新元素被插入到优先队列中时，它们会根据它们的自然顺序进行排序，或者通过我们构建优先队列时提供的定义的[比较](https://www.baeldung.com/java-comparator-comparable)器进行排序。

让我们通过一个简单的单元测试来看看它是如何工作的：

```java
PriorityQueue<Integer> integerQueue = new PriorityQueue<>();

integerQueue.add(9);
integerQueue.add(2);
integerQueue.add(4);

int first = integerQueue.poll();
int second = integerQueue.poll();
int third = integerQueue.poll();

assertEquals(2, first);
assertEquals(4, second);
assertEquals(9, third);
```

尽管我们的整数被添加到PriorityQueue的顺序，我们可以看到检索顺序根据数字的自然顺序而改变。

我们可以看到同样适用于Strings：

```java
PriorityQueue<String> stringQueue = new PriorityQueue<>();

stringQueue.add("blueberry");
stringQueue.add("apple");
stringQueue.add("cherry");

String first = stringQueue.poll();
String second = stringQueue.poll();
String third = stringQueue.poll();

assertEquals("apple", first);
assertEquals("blueberry", second);
assertEquals("cherry", third);
```

## 6.线程安全

将项目添加到队列在多线程环境中特别有用。队列可以在线程之间共享，并用于在空间可用之前阻止进程——帮助我们克服一些常见的多线程问题。

例如，从多个线程写入单个磁盘会造成资源争用，并可能导致写入时间变慢。使用BlockingQueue创建单个写入线程可以缓解此问题并大大提高写入速度。

幸运的是，Java提供了ConcurrentLinkedQueue、ArrayBlockingQueue和ConcurrentLinkedDeque，它们是线程安全的，非常适合多线程程序。

## 七、总结

在本教程中，我们深入探讨了JavaQueue接口。

首先，我们探讨了Queue的作用，以及Java提供的实现。

接下来，我们了解了队列通常的FIFO原则，以及顺序不同的PriorityQueue。

最后，我们探讨了线程安全以及如何在多线程环境中使用队列。