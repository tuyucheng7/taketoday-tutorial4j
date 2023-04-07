## 一、概述

在本教程中，我们将介绍 Java 中并发队列的一些主要实现。有关队列的一般介绍，请参阅我们的[Java*队列*接口](https://www.baeldung.com/java-queue)指南一文。

## 2.尾巴

在多线程应用中，队列需要处理多个并发的生产者-消费者场景。**正确选择并发队列对于在我们的算法中实现良好性能至关重要。** 

首先，我们将看到阻塞队列和非阻塞队列之间的一些重要区别。然后，我们将看看一些实施和最佳实践。

## 2. 阻塞队列与非阻塞队列

[*BlockingQueue*](https://www.baeldung.com/java-blocking-queue)提供了**一种简单的线程安全机制**。在这个队列中，线程需要等待队列可用。生产者将在添加元素之前等待可用容量，而消费者将等到队列为空。在这些情况下，非阻塞队列将抛出异常或返回一个特殊值，如*null*或*false*。

为了实现这种阻塞机制，*BlockingQueue接口在普通**Queue*函数之上公开了两个函数：*put*和*take*。这些函数等同于标准*Queue中的**添加*和*删除*。

## 3.并发*队列*实现

### 3.1. *数组阻塞队列*

顾名思义，这个队列内部使用了一个数组。因此，它是**一个有界队列，这意味着它具有固定大小**。

一个简单的工作队列是一个示例用例。这种场景通常是低生产者与消费者的比例，我们将耗时的任务分配给多个工人。由于此队列不能无限增长，因此**如果内存有问题，大小限制将作为安全阈值**。

说到内存，需要注意的是队列预先分配了数组。虽然这可能会提高吞吐量，但**也可能会消耗不必要的内存**。例如，大容量队列可能会长时间保持空状态。

此外，*ArrayBlockingQueue*对*put*和*take*操作使用单个锁。这确保不会以性能损失为代价覆盖条目。

### 3.2. *链接阻塞队列*

LinkedBlockingQueue使用[*LinkedList变体，*](https://www.baeldung.com/java-linkedlist)[*其中*](https://www.baeldung.com/java-queue-linkedblocking-concurrentlinked#linkedblockingqueue)每个队列项都是一个新节点。虽然这使得队列在原则上是无界的，但它仍然有一个*Integer.MAX_VALUE*的硬限制。

另一方面，我们可以使用构造函数*LinkedBlockingQueue(int capacity)*来设置队列大小。

这个队列使用不同的锁来进行*放置*和*获取*操作。因此，这两个操作可以并行完成并提高吞吐量。

既然*LinkedBlockingQueue*可以是有界的也可以是无界的，为什么我们要使用 ArrayBlockingQueue*而*不是这个呢？**每次从队列中添加或删除项目时，*****LinkedBlockingQueue\***都需要分配和释放节点。出于这个原因， 如果队列快速增长和快速收缩，*ArrayBlockingQueue可能是更好的选择。*

*LinkedBlockingQueue*的性能据说是不可预测的。换句话说，我们总是需要分析我们的场景以确保我们使用正确的数据结构。

### 3.3. *优先阻塞队列*

**当我们需要按特定顺序消费项目时，**[*PriorityBlockingQueue*](https://www.baeldung.com/java-priority-blocking-queue)是我们的首选解决方案。为此，PriorityBlockingQueue*使用*基于数组的二进制堆。

虽然它在内部使用单个锁定机制，但*take操作可以与**put*操作同时发生。使用简单的自旋锁使这成为可能。

一个典型的用例是使用不同优先级的任务。**我们不希望低优先级任务取代高优先级任务**。

### 3.4. *延迟队列*

**当消费者只能拿过期物品时，**我们使用[*DelayQueue*](https://www.baeldung.com/java-delay-queue) 。有趣的是，它在内部使用*PriorityQueue*来根据项目的到期时间对项目进行排序。

由于这不是通用队列，因此它涵盖的场景不如 ArrayBlockingQueue*或*LinkedBlockingQueue*多*。例如，我们可以使用这个队列来实现一个类似于 NodeJS 中的简单事件循环。我们将异步任务放在队列中，以便在它们过期时进行处理。

### 3.5. *链接传输队列*

LinkedTransferQueue引入了一种*传输*方法[*。*](https://www.baeldung.com/java-transfer-queue)虽然其他队列通常在生产或消费项目时阻塞，但 LinkedTransferQueue*允许***生产者等待项目的消费**。

当我们需要保证我们放入队列的特定项目已被某人拿走时，我们使用*LinkedTransferQueue 。*此外，我们可以使用这个队列实现一个简单的反压算法。实际上，通过在消费之前阻止生产者，**消费者可以推动生产的消息流**。

### 3.6. *同步队列*

虽然队列通常包含许多项目，但[*SynchronousQueue*](https://www.baeldung.com/java-synchronous-queue)始终最多只有一个项目。换句话说，我们需要将*SynchronousQueue*视为**在两个线程之间交换一些数据的简单方法**。

当我们有两个线程需要访问共享状态时，我们通常使用[*CountDownLatch*](https://www.baeldung.com/java-countdown-latch)或其他同步机制来同步它们。通过使用*SynchronousQueue*，我们可以避免**这种手动线程同步**。

### 3.7. *并发链接队列*

ConcurrentLinkedQueue是本指南[*中*](https://www.baeldung.com/java-queue-linkedblocking-concurrentlinked#concurrentlinkedqueue)唯一的非阻塞队列。因此，它提供了一种“无等待”算法，其中***add\*和\*poll\*保证线程安全并立即返回**。[这个队列使用CAS (Compare-And-Swap)](https://www.baeldung.com/lock-free-programming)而不是锁。

在内部，它基于[*Simple, Fast, and Practical Non-Blocking and Blocking Concurrent Queue Algorithms*](https://www.cs.rochester.edu/u/scott/papers/1996_PODC_queues.pdf)中的算法，作者是 Maged M. Michael 和 Michael L. Scott。

它是**现代[反应式系统](https://www.baeldung.com/java-reactive-systems)**的完美候选者，在现代反应式系统中，通常禁止使用阻塞数据结构。

另一方面，如果我们的消费者最终在循环中等待，我们可能应该选择阻塞队列作为更好的选择。

## 4。结论

在本指南中，我们介绍了不同的并发队列实现，讨论了它们的优缺点。考虑到这一点，我们可以更好地开发高效、耐用且可用的系统。