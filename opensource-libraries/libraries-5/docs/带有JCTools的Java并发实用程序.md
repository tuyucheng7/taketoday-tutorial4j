## 1. 概述

在本教程中，我们将介绍 [JCTools](https://github.com/JCTools/JCTools) (Java 并发工具)库。

简而言之，这提供了许多适合在多线程环境中工作的实用数据结构。

## 2. 非阻塞算法

传统上，在可变共享状态上工作的多线程代码使用锁来确保数据一致性和发布(一个线程所做的更改对另一个线程可见)。

这种方法有许多缺点：

-   线程可能会在尝试获取锁时被阻塞，在另一个线程的操作完成之前不会取得任何进展——这有效地防止了并行性
-   锁争用越严重，JVM 花在处理调度线程、管理争用和等待线程队列上的时间就越多，它所做的实际工作就越少
-   如果涉及多个锁并且以错误的顺序获取/释放它们，则可能会出现死锁
-   可能存在[优先级倒置](https://en.wikipedia.org/wiki/Priority_inversion)风险——高优先级线程被锁定以试图获得由低优先级线程持有的锁
-   大多数时候使用粗粒度锁，这会严重损害并行性——细粒度锁需要更仔细的设计，增加了锁定开销并且更容易出错

另一种方法是使用非阻塞算法，即任何线程的故障或挂起都不会导致另一个线程的故障或挂起的算法。

如果保证至少有一个相关线程在任意时间段内取得进展，则非阻塞算法是无锁的，即在处理过程中不会出现死锁。

此外，如果每个线程的进度也有保证，那么这些算法是 无等待的。

[这是优秀的Java Concurrency in Practice](https://www.amazon.com/Java-Concurrency-Practice-Brian-Goetz/dp/0321349601) 一书中的非阻塞堆栈示例；它定义了基本状态：

```java
public class ConcurrentStack<E> {

    AtomicReference<Node<E>> top = new AtomicReference<Node<E>>();

    private static class Node <E> {
        public E item;
        public Node<E> next;

        // standard constructor
    }
}
```

还有一些 API 方法：

```java
public void push(E item){
    Node<E> newHead = new Node<E>(item);
    Node<E> oldHead;
    
    do {
        oldHead = top.get();
        newHead.next = oldHead;
    } while(!top.compareAndSet(oldHead, newHead));
}

public E pop() {
    Node<E> oldHead;
    Node<E> newHead;
    do {
        oldHead = top.get();
        if (oldHead == null) {
            return null;
        }
        newHead = oldHead.next;
    } while (!top.compareAndSet(oldHead, newHead));
    
    return oldHead.item;
}
```

我们可以看到该算法使用细粒度的比较和交换([CAS](https://en.wikipedia.org/wiki/Compare-and-swap))指令并且是无锁的 (即使多个线程同时调用top.compareAndSet()，其中一个也保证成功)但不会等待- free因为不能保证 CAS 最终会在任何特定线程上成功。

## 3.依赖性

首先，让我们将 JCTools 依赖项添加到我们的 pom.xml中：

```xml
<dependency>
    <groupId>org.jctools</groupId>
    <artifactId>jctools-core</artifactId>
    <version>2.1.2</version>
</dependency>
```

请注意，最新的可用版本在[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"org.jctools" AND a%3A"jctools-core")上可用。

## 4. JCTools 队列

该库提供了许多队列以在多线程环境中使用，即一个或多个线程写入一个队列，一个或多个线程以线程安全的无锁方式从队列中读取。

所有Queue实现 的通用接口是org.jctools.queues.MessagePassingQueue。

### 4.1. 队列类型

所有队列都可以根据其生产者/消费者策略进行分类：

-   单个生产者，单个消费者——此类使用前缀 Spsc命名，例如SpscArrayQueue
-   单个生产者，多个消费者——使用 Spmc前缀，例如 SpmcArrayQueue
-   多个生产者，单个消费者——使用Mpsc前缀，例如MpscArrayQueue
-   多个生产者，多个消费者——使用Mpmc前缀，例如 MpmcArrayQueue

重要的是要注意内部没有策略检查，即如果使用不当，队列可能会悄悄地发生故障。

例如，下面的测试从两个线程填充一个单一生产者队列并通过，即使消费者不能保证看到来自不同生产者的数据：

```java
SpscArrayQueue<Integer> queue = new SpscArrayQueue<>(2);

Thread producer1 = new Thread(() -> queue.offer(1));
producer1.start();
producer1.join();

Thread producer2 = new Thread(() -> queue.offer(2));
producer2.start();
producer2.join();

Set<Integer> fromQueue = new HashSet<>();
Thread consumer = new Thread(() -> queue.drain(fromQueue::add));
consumer.start();
consumer.join();

assertThat(fromQueue).containsOnly(1, 2);
```

### 4.2. 队列实现

总结以上分类，JCTools队列列表如下：

-   SpscArrayQueue——单生产者，单消费者，内部使用数组，绑定容量
-   SpscLinkedQueue——单生产者，单消费者，内部使用链表，容量
-   SpscChunkedArrayQueue -单一生产者，单一消费者，从初始容量开始并增长到最大容量
-   SpscGrowableArrayQueue -单一生产者，单一消费者，从初始容量开始并增长到最大容量。这是与SpscChunkedArrayQueue相同的合同 ，唯一的区别是内部块管理。建议使用 SpscChunkedArrayQueue因为它有一个简化的实现
-   SpscUnboundedArrayQueue -单一生产者，单一消费者，内部使用数组，无限制容量
-   SpmcArrayQueue——单生产者，多消费者，内部使用数组，绑定容量
-   MpscArrayQueue——多生产者，单消费者，内部使用数组，绑定容量
-   MpscLinkedQueue——多生产者，单消费者，内部使用链表，容量无限制
-   MpmcArrayQueue——多个生产者，多个消费者，内部使用数组，绑定容量

### 4.3. 原子队列

上一节中提到的所有队列都使用[sun.misc.Unsafe](https://www.baeldung.com/java-unsafe)。然而，随着Java9 和 [JEP-260](https://openjdk.java.net/jeps/260)的出现，这个 API 默认情况下变得不可访问。

因此，有替代队列使用 java.util.concurrent.atomic.AtomicLongFieldUpdater (公共 API，性能较低)而不是sun.misc.Unsafe。

它们是从上面的队列生成的，它们的名称 之间插入了Atomic一词 ，例如 SpscChunkedAtomicArrayQueue或 MpmcAtomicArrayQueue。

建议尽可能使用“常规”队列，并且 仅在sun.misc.Unsafe被禁止/无效的环境(如 HotSpot Java9+ 和 JRockit)中使用 AtomicQueues 。

### 4.4. 容量

所有 JCTools 队列也可能具有最大容量或未绑定。当队列已满且受容量限制时，它会停止接受新元素。

在下面的例子中，我们：

-   排队
-   确保在那之后它停止接受新元素
-   从中排出并确保之后可以添加更多元素

请注意，为了便于阅读，删除了一些代码语句。完整的实现可以[在 GitHub 上找到](https://github.com/eugenp/tutorials/blob/master/libraries-5/src/test/java/com/baeldung/jctools/JCToolsUnitTest.java#L45)：

```java
SpscChunkedArrayQueue<Integer> queue = new SpscChunkedArrayQueue<>(8, 16);
CountDownLatch startConsuming = new CountDownLatch(1);
CountDownLatch awakeProducer = new CountDownLatch(1);

Thread producer = new Thread(() -> {
    IntStream.range(0, queue.capacity()).forEach(i -> {
        assertThat(queue.offer(i)).isTrue();
    });
    assertThat(queue.offer(queue.capacity())).isFalse();
    startConsuming.countDown();
    awakeProducer.await();
    assertThat(queue.offer(queue.capacity())).isTrue();
});

producer.start();
startConsuming.await();

Set<Integer> fromQueue = new HashSet<>();
queue.drain(fromQueue::add);
awakeProducer.countDown();
producer.join();
queue.drain(fromQueue::add);

assertThat(fromQueue).containsAll(
  IntStream.range(0, 17).boxed().collect(toSet()));
```

## 5.其他JCTools数据结构

JCTools 也提供了一些非队列数据结构。

所有这些都列在下面：

-   NonBlockingHashMap – 一种无锁的ConcurrentHashMap替代方案，具有更好的缩放属性和通常更低的变异成本。它是通过sun.misc.Unsafe实现的，因此不建议在 HotSpot Java9+ 或 JRockit 环境中使用此类
-   NonBlockingHashMapLong—— 与 NonBlockingHashMap类似，但使用原始长键
-   NonBlockingHashSet – NonBlockingHashMap的简单包装器， 如 JDK 的java.util.Collections.newSetFromMap()
-   NonBlockingIdentityHashMap – 与 NonBlockingHashMap类似，但按身份比较键。
-   NonBlockingSetInt – 一个多线程位向量集，实现为原始longs数组。在静默自动装箱的情况下无效

## 6. 性能测试

让我们使用[JMH](https://openjdk.java.net/projects/code-tools/jmh/) 来比较 JDK 的ArrayBlockingQueue与 JCTools 队列的性能。JMH 是来自 Sun/Oracle JVM 大师的开源微基准测试框架，它保护我们免受编译器/jvm 优化算法的不确定性影响。请随时在[本文](https://www.baeldung.com/java-microbenchmark-harness)中获得更多详细信息。

请注意，为了提高可读性，下面的代码片段遗漏了几条语句。[请在GitHub 上](https://github.com/eugenp/tutorials/blob/master/libraries-5/src/main/java/com/baeldung/jctools/MpmcBenchmark.java)找到完整的源代码：

```java
public class MpmcBenchmark {

    @Param({PARAM_UNSAFE, PARAM_AFU, PARAM_JDK})
    public volatile String implementation;

    public volatile Queue<Long> queue;

    @Benchmark
    @Group(GROUP_NAME)
    @GroupThreads(PRODUCER_THREADS_NUMBER)
    public void write(Control control) {
        // noinspection StatementWithEmptyBody
        while (!control.stopMeasurement && !queue.offer(1L)) {
            // intentionally left blank
        }
    }

    @Benchmark
    @Group(GROUP_NAME)
    @GroupThreads(CONSUMER_THREADS_NUMBER)
    public void read(Control control) {
        // noinspection StatementWithEmptyBody
        while (!control.stopMeasurement && queue.poll() == null) {
            // intentionally left blank
        }
    }
}
```

结果(第 95 个百分位数的摘录，每次操作的纳秒数)：

```bash
MpmcBenchmark.MyGroup:MyGroup·p0.95 MpmcArrayQueue sample 1052.000 ns/op
MpmcBenchmark.MyGroup:MyGroup·p0.95 MpmcAtomicArrayQueue sample 1106.000 ns/op
MpmcBenchmark.MyGroup:MyGroup·p0.95 ArrayBlockingQueue sample 2364.000 ns/op
```

我们可以看到MpmcArrayQueue 的性能略好于 MpmcAtomicArrayQueue而 ArrayBlockingQueue慢两倍。 

## 7. 使用 JCTools 的缺点

使用 JCTools 有一个重要的缺点——无法强制正确使用库类。例如，考虑在我们的大型成熟项目中开始使用 MpscArrayQueue的情况(注意必须有单个消费者)。

不幸的是，由于项目很大，有可能有人犯了编程或配置错误，现在从多个线程读取队列。该系统似乎像以前一样工作，但现在消费者有可能错过一些消息。这是一个真正的问题，可能会产生很大的影响并且很难调试。

理想情况下，应该可以运行具有强制 JCTools 确保线程访问策略的特定系统属性的系统。例如，本地/测试/暂存环境(但不是生产环境)可能已将其打开。遗憾的是，JCTools 不提供这样的属性。

另一个考虑是，即使我们确保 JCTools 比 JDK 的对应物快得多，但这并不意味着我们的应用程序获得与我们开始使用自定义队列实现相同的速度。大多数应用程序不会在线程之间交换大量对象，并且主要受 I/O 限制。

## 八. 总结

我们现在对 JCTools 提供的实用程序类有了基本的了解，并看到了它们与重负载下的 JDK 对应类相比的表现如何。

总之，只有当我们在线程之间交换大量对象时才值得使用该库，即便如此，也有必要非常小心地保留线程访问策略。