## 1. 概述

在本文中，我们将介绍解决并发生产者-消费者问题的最有用的构造之一java.util.concurrent。我们将研究[BlockingQueue](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/BlockingQueue.html)接口的API以及该接口中的方法如何使编写并发程序更容易。

在本文的后面，我们将演示一个具有多个生产者线程和多个消费者线程的简单程序的案例。

## 2. BlockingQueue类型

我们可以区分两种类型的BlockingQueue：

+ 无界队列：几乎可以无限增长
+ 有界队列：定义了最大容量

### 2.1 无界队列

创建无界队列很简单：

```java
BlockingQueue<String> blockingQueue = new LinkedBlockingDeque<>();
```

blockingQueue的容量将设置为Integer.MAX_VALUE，将元素添加到无界队列的所有操作都不会阻塞，因此它可能会增长到非常大的大小。

在使用无界BlockingQueue设计生产者-消费者程序时，最重要的是消费者应该能够像生产者向队列中添加消息一样快速地消费消息。否则，内存可能会被填满，我们会得到OutOfMemory异常。

### 2.2 有界队列

第二种类型的队列是有界队列，我们可以通过将容量作为参数传递给构造函数来创建此类队列：

```java
BlockingQueue<String> blockingQueue = new LinkedBlockingDeque<>(10);
```

这里我们创建了一个容量等于10的blockingQueue。这意味着当生产者尝试将元素添加(取决于用于添加元素的方法，例如offer()、add()、put())到已经满的队列中时，它将阻塞，直到有空间可供插入对象为止。否则，操作将失败。

使用有界队列是设计并发程序的一种好方法，因为当我们将元素插入到已经满的队列中时，该操作需要等待，直到消费者赶上并在队列中腾出一些可用空间。这使我们很容易就能实现节流。

## 3. BlockingQueue API

BlockingQueue接口中有两种类型的方法-负责向队列添加元素的方法和检索这些元素的方法。当队列已满/为空时，这两组方法中的每个行为都不同。

### 3.1 添加元素

+ add()：如果添加成功，则返回true，否则抛出IllegalStateException
+ put()：将指定的元素插入队列，必要时等待空闲容量
+ offer()：如果插入成功，则返回true，否则返回false
+ offer(E e, long timeout, TimeUnit unit)：尝试将元素插入队列，并在指定的超时内等待可用容量

### 3.2 检索元素

+ take()：等待队列的头元素并将其删除。如果队列为空，它将阻塞并等待元素变为可用
+ poll(long timeout, TimeUnit unit)：检索并删除队列的头元素，如果需要，等待指定的超时时间，以便元素可用。超时后返回null

在构建生产者-消费者程序时，这些方法是BlockingQueue接口中最重要的API。

## 4. 多线程生产者-消费者案例

让我们创建一个由两部分组成的程序-生产者和消费者。

生产者将生成一个从0到100的随机数，并将该数字放入阻塞队列中。我们将有4个生产者线程并使用put()方法进行阻塞，直到队列中有可用空间为止。

需要记住的重要一点是，我们需要阻止消费者线程无限期地等待元素出现在队列中。

从生产者向消费者发出没有更多消息要处理的信号的一个好方法是发送一条称为“毒丸”(poison pill)的特殊消息。我们需要向消费者发送尽可能多的“毒丸”。然后，当消费者从队列中接收到特殊的“毒丸”消息时，它将优雅地完成执行。

让我们看一个生产者类：

```java
public class NumbersProducer implements Runnable {
    private BlockingQueue<Integer> numbersQueue;
    private final int poisonPill;
    private final int poisonPillPerProducer;

    public NumbersProducer(BlockingQueue<Integer> numbersQueue, int poisonPill, int poisonPillPerProducer) {
        this.numbersQueue = numbersQueue;
        this.poisonPill = poisonPill;
        this.poisonPillPerProducer = poisonPillPerProducer;
    }

    public void run() {
        try {
            generateNumbers();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void generateNumbers() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            numbersQueue.put(ThreadLocalRandom.current().nextInt(100));
        }
        for (int j = 0; j < poisonPillPerProducer; j++) {
            numbersQueue.put(poisonPill);
        }
    }
}
```

我们的生产者构造函数接收BlockingQueue作为参数，BlockingQueue用于协调生产者和消费者之间的处理。我们看到，generateNumbers()方法会将100个元素放入队列中。它还需要放入“毒丸”消息，以了解在执行完成时必须将何种类型的消息放入队列。该消息需要被放入poisonPillPerProducer次到队列中。

每个消费者将使用take()方法从BlockingQueue中获取一个元素，因此它将一直阻塞，直到队列中有一个元素为止。从队列中获取整数后，它会检查消息是否为“毒丸”，如果是，则线程的执行完成。否则，它将在标准输出上打印出结果以及当前线程的名称。

这将使我们深入了解消费者的内部运作：

```java
public class NumbersConsumer implements Runnable {
    private final BlockingQueue<Integer> queue;
    private final int poisonPill;

    NumbersConsumer(BlockingQueue<Integer> queue, int poisonPill) {
        this.queue = queue;
        this.poisonPill = poisonPill;
    }

    public void run() {
        try {
            while (true) {
                Integer number = queue.take();
                if (number.equals(poisonPill)) {
                    return;
                }
                String result = number.toString();
                System.out.println(Thread.currentThread().getName() + " result: " + result);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

需要注意的重要一点是队列的使用。与生产者构造函数相同，队列作为参数传递。我们之所以可以这样做，是因为BlockingQueue可以在线程之间共享，而无需任何显式同步。

现在我们有了生产者和消费者，我们可以开始我们的程序了。我们需要定义队列的容量，并将其设置为100个元素。

我们希望有4个生产者线程，而消费者线程的数量等于可用处理器核心的数量：

```java
public class BlockingQueueUsage {

    public static void main(String[] args) {
        int BOUND = 10;
        int N_PRODUCERS = 4;
        int N_CONSUMERS = Runtime.getRuntime().availableProcessors();
        int poisonPill = Integer.MAX_VALUE;
        int poisonPillPerProducer = N_CONSUMERS / N_PRODUCERS;
        int mod = N_CONSUMERS % N_PRODUCERS;
        BlockingQueue<Integer> queue = new LinkedBlockingDeque<>(BOUND);

        for (int i = 1; i < N_PRODUCERS; i++) {
            new Thread(new NumbersProducer(queue, poisonPill, poisonPillPerProducer)).start();
        }
        for (int i = 1; i < N_CONSUMERS; i++) {
            new Thread(new NumbersConsumer(queue, poisonPill)).start();
        }
        new Thread(new NumbersProducer(queue, poisonPill, poisonPillPerProducer + mod)).start();
    }
}
```

BlockingQueue是使用具有容量的构造方法创建的。我们创建了4个生产者和N个消费者，并将“毒丸”消息指定为Integer.MAX_VALUE，因为在正常的情况下，我们的生产者永远不可能生成这样的值。这里需要注意的最重要的一点是，BlockingQueue用于协调它们之间的工作。

当我们运行程序时，4个生产者线程会将随机整数放入BlockingQueue中，消费者将从队列中取出这些元素。每个线程会将线程名称连同结果打印到标准输出。

## 5. 总结

本文展示了BlockingQueue的一个实际用法，并解释了用于添加和检索元素的方法。此外，我们还展示了如何使用BlockingQueue构建多线程生产者-消费者程序来以协调生产者和消费者之间的工作。