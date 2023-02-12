## 1. 概述

**LinkedBlockingQueue和ConcurrentLinkedQueue是Java中最常用的两个并发队列**。尽管这两个队列通常被用作并发数据结构，但它们之间存在细微的特征和行为差异。

在这个简短的教程中，我们将讨论这两个队列并解释它们的异同。

## 2. LinkedBlockingQueue

**LinkedBlockingQueue是一个可选的有界阻塞队列实现**，这意味着可以根据需要指定队列大小。

让我们创建一个最多可包含100个元素的LinkedBlockingQueue：

```java
BlockingQueue<Integer> boundedQueue = new LinkedBlockingQueue<>(100);
```

我们还可以通过不指定大小来创建一个无界的LinkedBlockingQueue：

```java
BlockingQueue<Integer> unboundedQueue = new LinkedBlockingQueue<>();
```

无界队列意味着在创建时未指定队列的大小。因此，队列可以随着元素的添加而动态增长。但是，如果没有剩余内存，则队列会抛出java.lang.OutOfMemoryError。

我们也可以从现有集合创建一个LinkedBlockingQueue：

```java
Collection<Integer> listOfNumbers = Arrays.asList(1,2,3,4,5);
BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(listOfNumbers);
```

**LinkedBlockingQueue类实现了BlockingQueue接口，该接口为它提供了阻塞特性**。

阻塞队列表示如果队列已满(当队列有界时)或变为空，则该队列将阻塞访问线程。如果队列已满，则添加新元素将阻塞访问线程，除非有空间可用于新元素。同样，如果队列为空，则访问元素会阻塞调用线程：

```java
ExecutorService executorService = Executors.newFixedThreadPool(1);
LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
executorService.submit(() -> {
    try {
        queue.take();
    } 
    catch (InterruptedException e) {
        // exception handling
    }
});
```

在上面的代码片段中，我们正在访问一个空队列。因此，take方法会阻塞调用线程。

LinkedBlockingQueue的阻塞特性与一些成本相关联，这种成本是因为每个put或take操作都在生产者或消费者线程之间进行锁竞争。因此，在具有许多生产者和消费者的场景中，put和take操作可能会更慢。

## 3. ConcurrentLinkedQueue

**ConcurrentLinkedQueue是一个无界、线程安全且非阻塞的队列**。

让我们创建一个空的ConcurrentLinkedQueue：

```java
ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
```

我们也可以从现有集合创建一个ConcurrentLinkedQueue：

```java
Collection<Integer> listOfNumbers = Arrays.asList(1,2,3,4,5);
ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>(listOfNumbers);
```

与LinkedBlockingQueue不同，**ConcurrentLinkedQueue是一个非阻塞队列**。因此，如果队列为空，它不会阻塞线程。相反，它返回null。由于它是无界的，如果没有额外的内存来添加新元素，它将抛出java.lang.OutOfMemoryError。

除了非阻塞之外，ConcurrentLinkedQueue还具有其他功能。

在任何生产者-消费者场景中，消费者都不会与生产者竞争；但是，多个生产者将相互竞争：

```java
int element = 1;
ExecutorService executorService = Executors.newFixedThreadPool(2);
ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();

Runnable offerTask = () -> queue.offer(element);

Callable<Integer> pollTask = () -> {
    while (queue.peek() != null) {
        return queue.poll().intValue();
    }
    return null;
};

executorService.submit(offerTask);
Future<Integer> returnedElement = executorService.submit(pollTask);
assertThat(returnedElement.get().intValue(), is(equalTo(element)));
```

第一个任务offerTask将一个元素添加到队列中，第二个任务pollTask从队列中检索一个元素。**pollTask还会首先检查队列中的元素，因为ConcurrentLinkedQueue是非阻塞的并且可能返回空值**。

## 4. 相似之处

LinkedBlockingQueue和ConcurrentLinkedQueue都是队列实现，并且具有一些共同特征。让我们讨论一下这两个队列的相似之处：

1. **两者都实现了Queue接口**
2. 他们都**使用链表节点**来存储其元素
3. **两者都适用于并发访问场景**

## 5. 差异

尽管这两个队列有一定的相似之处，但也存在实质性的特征差异：

|  功能  |                                         LinkedBlockingQueue                                          |        ConcurrentLinkedQueue        |
|:----:|:----------------------------------------------------------------------------------------------------:|:-----------------------------------:|
|  **阻塞**  |                                     它是一个阻塞队列，实现了BlockingQueue接口                                      |    它是一个非阻塞队列，没有实现BlockingQueue接口    |
| **队列大小** |                                    它是一个可选的有界队列，这意味着在创建过程中可以定义队列大小                                    |     它是一个无界队列，在创建过程中没有指定队列大小的规定     |
| **锁定性质** |                                              它是一个**基于锁的队列**                                              |                它是一个**无锁队列**             |
|  **算法**  |                                            它**基于双锁队列算法**实现其锁定                                            |    它依赖于**Michael&Scott算法实现无阻塞、无锁队列**    |
|  **实现**  |   在双锁队列算法机制中，LinkedBlockingQueue使用了两种不同的锁-putLock和takeLock。put/take操作使用第一种锁类型，而take/poll操作使用另一种锁类型   |          **它使用CAS(比较和交换)进行操作**          |
| **阻塞行为** |                                     它是一个阻塞队列。因此，当队列为空时，它会阻塞访问线程                                      |        当队列为空时返回null，不会阻塞访问线程        |


## 6. 总结

在本文中，我们了解了LinkedBlockingQueue和ConcurrentLinkedQueue。

首先，我们分别讨论了这两个队列的实现以及它们的一些特性。然后，我们看到了这两个队列实现之间的相似之处。最后，我们探讨了这两种队列实现之间的差异。