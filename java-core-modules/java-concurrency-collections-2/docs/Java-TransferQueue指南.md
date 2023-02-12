## 1. 概述

在本文中，我们将研究来自标准java.util.concurrent包的[TransferQueue](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/TransferQueue.html)构造。

简单来说，这个队列可以让我们按照生产者-消费者模式来创建程序，并协调从生产者到消费者的消息传递。

该实现实际上与[BlockingQueue](https://www.baeldung.com/java-blocking-queue)类似-但它为我们提供了实现一种背压形式的新能力。这意味着，当生产者使用transfer()方法向消费者发送消息时，生产者将保持阻塞状态，直到消息被消费。

## 2. 一生产者–零消费者

让我们测试TransferQueue中的transfer()方法-预期的行为是生产者将被阻塞，直到消费者使用take()方法从队列中接收到消息。

为了实现这一点，我们将创建一个只有1个生产者但没有消费者的程序。生产者线程对transfer()的第一次调用将无限期阻塞，因为我们没有任何消费者从队列中获取该元素。

让我们看看Producer类的样子：

```java
public class Producer implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(Producer.class);
    private final TransferQueue<String> transferQueue;
    private final String name;
    final Integer numberOfMessagesToProduce;
    final AtomicInteger numberOfProducedMessages = new AtomicInteger();

    Producer(TransferQueue<String> transferQueue, String name, Integer numberOfMessagesToProduce) {
        this.transferQueue = transferQueue;
        this.name = name;
        this.numberOfMessagesToProduce = numberOfMessagesToProduce;
    }

    @Override
    public void run() {
        for (int i = 0; i < numberOfMessagesToProduce; i++) {
            LOG.debug("Producer: " + name + " is waiting to transfer...");
            try {
                boolean added = transferQueue.tryTransfer("A" + i, 4000, TimeUnit.MILLISECONDS);
                if (added) {
                    numberOfProducedMessages.incrementAndGet();
                    LOG.debug("Producer: " + name + " transferred element : A" + i);
                } else {
                    LOG.debug("can not add an element due to the timeout");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
```

我们将TransferQueue的一个实例以及我们要为生产者提供的名称以及应传输到队列的元素数量一起传递给构造函数。

请注意，我们使用的是带有给定超时的tryTransfer()方法。这里等待4秒钟，如果生产者无法在给定的超时时间内传输消息，它将返回false并继续处理下一条消息。生产者有一个numberOfProducedMessages变量来跟踪生产了多少条消息。

接下来，让我们看一下Consumer类：

```java
public class Consumer implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(Consumer.class);
    private final TransferQueue<String> transferQueue;
    private final String name;
    final int numberOfMessagesToConsume;
    final AtomicInteger numberOfConsumedMessages = new AtomicInteger();

    Consumer(TransferQueue<String> transferQueue, String name, int numberOfMessagesToConsume) {
        this.transferQueue = transferQueue;
        this.name = name;
        this.numberOfMessagesToConsume = numberOfMessagesToConsume;
    }

    @Override
    public void run() {
        for (int i = 0; i < numberOfMessagesToConsume; i++) {
            try {
                LOG.debug("Consumer: " + name + " is waiting to take element...");
                String element = transferQueue.take();
                longProcessing(element);
                LOG.debug("Consumer: " + name + " received element: " + element);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void longProcessing(String element) throws InterruptedException {
        numberOfConsumedMessages.incrementAndGet();
        Thread.sleep(500);
    }
}
```

它类似于生产者，但我们使用take()方法从队列中接收元素。我们还通过调用longProcessing()方法来模拟一些长时间运行的操作，在该方法中我们递增numberOfConsumedMessages变量，该变量是接收到的消息的计数器。

现在，让我们编写一个测试，只启动一个生产者：

```java
@Test
void whenUseOneProducerAndNoConsumers_thenShouldFailWithTimeout() throws InterruptedException {
    // given
    TransferQueue<String> transferQueue = new LinkedTransferQueue<>();
    ExecutorService executor = Executors.newFixedThreadPool(2);
    Producer producer = new Producer(transferQueue, "1", 3);

    // when
    executor.execute(producer);

    // then
    executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
    executor.shutdown();

    assertEquals(producer.numberOfProducedMessages.intValue(), 0);
}
```

我们希望将三个元素发送到队列，但是生产者在第一个元素上被阻塞，并且没有消费者从队列中获取该元素。我们正在使用tryTransfer()方法，该方法将阻塞直到消息被消费或达到超时。超时后会返回false表示传输失败，并开始尝试下一次传输。

这是上一个示例的输出：

```shell
15:53:57.332 [pool-1-thread-1] DEBUG cn.tuyucheng.taketoday.transferqueue.Producer - Producer: 1 is waiting to transfer...
15:54:01.344 [pool-1-thread-1] DEBUG cn.tuyucheng.taketoday.transferqueue.Producer - can not add an element due to the timeout
15:54:01.344 [pool-1-thread-1] DEBUG cn.tuyucheng.taketoday.transferqueue.Producer - Producer: 1 is waiting to transfer...
```

## 3. 一个生产者-一个消费者

让我们测试一个生产者和一个消费者的情况：

```java
@Test
void whenUseOneConsumerAndOneProducer_thenShouldProcessAllMessages() throws InterruptedException {
    // given
    TransferQueue<String> transferQueue = new LinkedTransferQueue<>();
    ExecutorService executor = Executors.newFixedThreadPool(2);
    Producer producer = new Producer(transferQueue, "1", 3);
    Consumer consumer = new Consumer(transferQueue, "1", 3);

    // when
    executor.execute(producer);
    executor.execute(consumer);

    // then
    executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
    executor.shutdown();

    assertEquals(producer.numberOfProducedMessages.intValue(), 3);
    assertEquals(consumer.numberOfConsumedMessages.intValue(), 3);
}
```

TransferQueue用作交换点，在消费者消费队列中的一个元素之前，生产者无法继续向其添加另一个元素。让我们看一下程序输出：

```shell
15:55:37.303 [pool-1-thread-2] DEBUG cn.tuyucheng.taketoday.transferqueue.Consumer - Consumer: 1 is waiting to take element...
15:55:37.303 [pool-1-thread-1] DEBUG cn.tuyucheng.taketoday.transferqueue.Producer - Producer: 1 is waiting to transfer...
15:55:37.309 [pool-1-thread-1] DEBUG cn.tuyucheng.taketoday.transferqueue.Producer - Producer: 1 transferred element : A0
15:55:37.309 [pool-1-thread-1] DEBUG cn.tuyucheng.taketoday.transferqueue.Producer - Producer: 1 is waiting to transfer...
15:55:37.823 [pool-1-thread-2] DEBUG cn.tuyucheng.taketoday.transferqueue.Consumer - Consumer: 1 received element: A0
15:55:37.823 [pool-1-thread-2] DEBUG cn.tuyucheng.taketoday.transferqueue.Consumer - Consumer: 1 is waiting to take element...
15:55:37.823 [pool-1-thread-1] DEBUG cn.tuyucheng.taketoday.transferqueue.Producer - Producer: 1 transferred element : A1
15:55:37.823 [pool-1-thread-1] DEBUG cn.tuyucheng.taketoday.transferqueue.Producer - Producer: 1 is waiting to transfer...
15:55:38.332 [pool-1-thread-2] DEBUG cn.tuyucheng.taketoday.transferqueue.Consumer - Consumer: 1 received element: A1
15:55:38.332 [pool-1-thread-2] DEBUG cn.tuyucheng.taketoday.transferqueue.Consumer - Consumer: 1 is waiting to take element...
15:55:38.332 [pool-1-thread-1] DEBUG cn.tuyucheng.taketoday.transferqueue.Producer - Producer: 1 transferred element : A2
15:55:38.845 [pool-1-thread-2] DEBUG cn.tuyucheng.taketoday.transferqueue.Consumer - Consumer: 1 received element: A2
```

我们看到，由于TransferQueue的规范，从队列中生产和消费元素是顺序的。

## 4. 多生产者-多消费者

在最后一个示例中，我们将考虑有多个消费者和多个生产者的情况：

```java
@Test
void whenMultipleConsumersAndProducers_thenProcessAllMessages() throws InterruptedException {
    // given
    TransferQueue<String> transferQueue = new LinkedTransferQueue<>();
    ExecutorService executor = Executors.newFixedThreadPool(3);
    Producer producer1 = new Producer(transferQueue, "1", 3);
    Producer producer2 = new Producer(transferQueue, "2", 3);
    Consumer consumer1 = new Consumer(transferQueue, "1", 3);
    Consumer consumer2 = new Consumer(transferQueue, "2", 3);

    // when
    executor.execute(producer1);
    executor.execute(producer2);
    executor.execute(consumer1);
    executor.execute(consumer2);

    // then
    executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
    executor.shutdown();

    assertEquals(producer1.numberOfProducedMessages.intValue(), 3);
    assertEquals(producer2.numberOfProducedMessages.intValue(), 3);
}
```

在这个例子中，我们有两个消费者和两个生产者。当程序启动时，我们看到两个生产者都可以生成一个元素，之后它们将阻塞，直到其中一个消费者从队列中取出该元素：

```shell
15:56:58.902 [pool-1-thread-1] DEBUG cn.tuyucheng.taketoday.transferqueue.Producer - Producer: 1 is waiting to transfer...
15:56:58.902 [pool-1-thread-2] DEBUG cn.tuyucheng.taketoday.transferqueue.Producer - Producer: 2 is waiting to transfer...
15:56:58.902 [pool-1-thread-3] DEBUG cn.tuyucheng.taketoday.transferqueue.Consumer - Consumer: 1 is waiting to take element...
15:56:58.909 [pool-1-thread-1] DEBUG cn.tuyucheng.taketoday.transferqueue.Producer - Producer: 1 transferred element : A0
15:56:58.909 [pool-1-thread-1] DEBUG cn.tuyucheng.taketoday.transferqueue.Producer - Producer: 1 is waiting to transfer...
15:56:59.415 [pool-1-thread-3] DEBUG cn.tuyucheng.taketoday.transferqueue.Consumer - Consumer: 1 received element: A0
15:56:59.415 [pool-1-thread-3] DEBUG cn.tuyucheng.taketoday.transferqueue.Consumer - Consumer: 1 is waiting to take element...
15:56:59.415 [pool-1-thread-2] DEBUG cn.tuyucheng.taketoday.transferqueue.Producer - Producer: 2 transferred element : A0
15:56:59.415 [pool-1-thread-2] DEBUG cn.tuyucheng.taketoday.transferqueue.Producer - Producer: 2 is waiting to transfer...
15:56:59.928 [pool-1-thread-3] DEBUG cn.tuyucheng.taketoday.transferqueue.Consumer - Consumer: 1 received element: A0
15:56:59.928 [pool-1-thread-3] DEBUG cn.tuyucheng.taketoday.transferqueue.Consumer - Consumer: 1 is waiting to take element...
15:56:59.928 [pool-1-thread-1] DEBUG cn.tuyucheng.taketoday.transferqueue.Producer - Producer: 1 transferred element : A1
15:56:59.928 [pool-1-thread-1] DEBUG cn.tuyucheng.taketoday.transferqueue.Producer - Producer: 1 is waiting to transfer...
15:57:00.441 [pool-1-thread-3] DEBUG cn.tuyucheng.taketoday.transferqueue.Consumer - Consumer: 1 received element: A1
15:57:00.441 [pool-1-thread-3] DEBUG cn.tuyucheng.taketoday.transferqueue.Consumer - Consumer: 2 is waiting to take element...
15:57:00.441 [pool-1-thread-2] DEBUG cn.tuyucheng.taketoday.transferqueue.Producer - Producer: 2 transferred element : A1
15:57:00.441 [pool-1-thread-2] DEBUG cn.tuyucheng.taketoday.transferqueue.Producer - Producer: 2 is waiting to transfer...
15:57:00.955 [pool-1-thread-3] DEBUG cn.tuyucheng.taketoday.transferqueue.Consumer - Consumer: 2 received element: A1
15:57:00.955 [pool-1-thread-3] DEBUG cn.tuyucheng.taketoday.transferqueue.Consumer - Consumer: 2 is waiting to take element...
15:57:00.955 [pool-1-thread-1] DEBUG cn.tuyucheng.taketoday.transferqueue.Producer - Producer: 1 transferred element : A2
15:57:01.465 [pool-1-thread-3] DEBUG cn.tuyucheng.taketoday.transferqueue.Consumer - Consumer: 2 received element: A2
15:57:01.465 [pool-1-thread-3] DEBUG cn.tuyucheng.taketoday.transferqueue.Consumer - Consumer: 2 is waiting to take element...
15:57:01.465 [pool-1-thread-2] DEBUG cn.tuyucheng.taketoday.transferqueue.Producer - Producer: 2 transferred element : A2
15:57:01.977 [pool-1-thread-3] DEBUG cn.tuyucheng.taketoday.transferqueue.Consumer - Consumer: 2 received element: A2
```

## 5. 总结

在本文中，我们研究了java.util.concurrent包中的TransferQueue构造。

我们看到了如何使用该构造实现生产者-消费者程序。我们使用transfer()方法来实现一种背压形式，其中生产者在消费者从队列中检索元素之前不能发送另一个元素。

当我们不希望过度生产的生产者用消息淹没队列，从而导致OutOfMemory错误时，TransferQueue非常有用。在这样的设计中，消费者将决定生产者生产消息的速度。