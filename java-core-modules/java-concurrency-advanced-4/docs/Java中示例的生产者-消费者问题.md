## 1. 概述

在本教程中，我们将学习如何在Java中实现生产者-消费者问题。**这个问题也被称为有界缓冲区问题**。

有关该问题的更多详细信息，我们可以参考[生产者-消费者问题](https://en.wikipedia.org/wiki/Producer%E2%80%93consumer_problem)维基页面。对于Java线程/并发基础知识，请务必访问我们的[Java并发](https://www.baeldung.com/java-concurrency)文章。

## 2. 生产者-消费者问题

生产者和消费者是两个独立的进程。这两个进程共享一个公共缓冲区或队列。生产者不断产生某些数据并将其推送到缓冲区，而消费者从缓冲区中消费这些数据。

让我们回顾一下显示这个简单场景的图表：

<img src="../assets/img_1.png">

**本质上，这个问题有一定的复杂性需要处理**：

+ 生产者和消费者可能尝试同时更新队列，这可能会导致数据丢失或不一致。
+ 生产者可能比消费者慢。在这种情况下，消费者会快速处理元素并等待。
+ 在某些情况下，消费者可能比生产者慢。这种情况会导致队列溢出问题。
+ 在实际场景中，我们可能有多个生产者、多个消费者，或两者兼而有之。这可能会导致相同的消息被不同的消费者处理。

下图描述了具有多个生产者和多个使用者的案例：

<img src="../assets/img_2.png">

我们需要处理资源共享和同步来解决一些复杂性：

+ 添加和删除数据时在队列上同步
+ 在队列为空时，消费者必须等到生产者将新数据添加到队列中
+ 当队列已满时，生产者必须等到消费者消费数据并且队列有一些空缓冲区

## 3. 使用线程的Java示例

我们为问题的每个实体定义了一个单独的类。

### 3.1 消息类

Message类保存生成的数据：

```java
public class Message {
    private int id;
    private double data;

    // constructors and getter/setters
}
```

数据可以是任何类型。它可能是一个JSON字符串、一个复杂对象或只是一个数字。此外，将数据封装到Message类中也不是强制性的。

### 3.2 数据队列类

共享队列和相关对象被包装到DataQueue类中：

```java
public class DataQueue {
    private final Queue<Message> queue = new LinkedList<>();
    private final int maxSize;
    private final Object FULL_QUEUE = new Object();
    private final Object EMPTY_QUEUE = new Object();

    DataQueue(int maxSize) {
        this.maxSize = maxSize;
    }

    // other methods ...
}
```

为了创建有界缓冲区，需要使用一个队列并指定maxSize。

在Java中，同步块使用一个对象来实现线程同步。**每个对象都有一个内部锁**。只有先获得锁的线程才被允许执行同步块。

在这里，我们创建了两个引用对象FULL_QUEUE和EMPTY_QUEUE，用于同步。除此之外，它们没有任何作用，因此我们使用Object对其进行初始化。

当队列已满时，生产者等待FULL_QUEUE对象。并且，消费者在消费消息后立即通知。

生产者进程调用waitOnFull方法：

```java
public void waitOnFull() throws InterruptedException {
    synchronized (FULL_QUEUE) {
        FULL_QUEUE.wait();
    }
}
```

而消费者进程通过notifyAllForFull方法通知生产者：

```java
public void notifyAllForFull() {
    synchronized (FULL_QUEUE) {
        FULL_QUEUE.notifyAll();
    }
}
```

如果队列为空，则消费者等待EMPTY_QUEUE对象。并且，一旦将消息添加到队列中，生产者就会通知它。

消费者进程使用waitOnEmpty方法等待：

```java
public void waitOnEmpty() throws InterruptedException {
    synchronized (EMPTY_QUEUE) {
        EMPTY_QUEUE.wait();
    }
}
```

生产者使用notifyAllForEmpty方法通知消费者：

```java
public void notifyAllForEmpty() {
    synchronized (EMPTY_QUEUE) {
        EMPTY_QUEUE.notifyAll();
    }
}
```

生产者使用add()方法将消息添加到队列中：

```java
public void add(Message message) {
    synchronized (queue) {
        queue.add(message);
    }
}
```

消费者调用remove方法从队列中检索消息：

```java
public Message remove() {
    synchronized (queue) {
        return queue.poll();
    }
}
```

### 3.3 生产者类

Producer类实现Runnable接口，因此可以看作一个单独的线程：

```java
public class Producer implements Runnable {
    private final DataQueue dataQueue;
    private volatile boolean runFlag;

    private static int idSequence = 0;

    public Producer(DataQueue dataQueue) {
        this.dataQueue = dataQueue;
        runFlag = true;
    }

    @Override
    public void run() {
        produce();
    }

    // other methods ...
}
```

构造函数使用共享的dataQueue参数。**成员变量runFlag有助于优雅地停止生产者进程**，它被初始化为true。

线程启动调用produce()方法：

```java
public void produce() {
    while (runFlag) {
        Message message = generateMessage();
        while (dataQueue.isFull()) {
            try {
                dataQueue.waitOnFull();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        if (!runFlag) {
            break;
        }
        dataQueue.add(message);
        dataQueue.notifyAllForEmpty();
    }
    System.out.println("Producer Stopped");
}

private Message generateMessage() {
    Message message = new Message(++idSequence, Math.random());
    System.out.printf("[%s] Generated Message. Id: %d, Data: %f\n", Thread.currentThread().getName(), message.getId(), message.getData());

    // Sleeping on random time to make it realistic
    ThreadUtil.sleep((long) (message.getData() * 100));

    return message;
}
```

生产者在一个while循环中持续运行。当runFlag为false时，此循环中断。

在每次迭代中，它都会生成一条消息。然后，它检查队列是否已满并根据需要等待。代替if块，使用while循环来检查队列是否已满。**这是为了避免从等待状态中虚假唤醒**。

当生产者从等待中醒来时，它会检查它是否仍然需要继续或从进程中退出。它将消息添加到队列中，并通知正在等待EMPTY_QUEUE的消费者。

stop()方法优雅地终止进程：

```java
public void stop() {
    runFlag = false;
    dataQueue.notifyAllForFull();
}
```

将runFlag更改为false后，将通知所有处于“FULL_QUEUE”状态的生产者。这可确保所有生产者线程终止。

### 3.4 消费者类

Consumer类实现Runnable接口，因此可以看作一个单独的线程：

```java
public class Consumer implements Runnable {
    private final DataQueue dataQueue;
    private volatile boolean runFlag;

    public Consumer(DataQueue dataQueue) {
        this.dataQueue = dataQueue;
        runFlag = true;
    }

    @Override
    public void run() {
        consume();
    }

    // other methods ...
}
```

它的构造函数有一个共享的dataQueue作为参数。runFlag被初始化为true，此标志在需要时停止消费者进程。

**当线程启动时，它调用consume方法**：

```java
public void consume() {
    while (runFlag) {
        Message message;
        if (dataQueue.isEmpty()) {
            try {
                dataQueue.waitOnEmpty();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        if (!runFlag) {
            break;
        }
        message = dataQueue.remove();
        dataQueue.notifyAllForFull();
        useMessage(message);
    }
    System.out.println("Consumer Stopped");
}

private void useMessage(Message message) {
    if (message != null) {
        System.out.printf("[%s] Consuming Message. Id: %d, Data: %f\n", Thread.currentThread().getName(), message.getId(), message.getData());

        // Sleeping on random time to make it realistic
        ThreadUtil.sleep((long) (message.getData() * 100));
    }
}
```

它有一个连续运行的while循环。并且，当runFlag为false时，此进程会正常停止。

每次迭代都会检查队列是否为空。**如果队列为空，则消费者等待生成消息**。while循环也用于此等待来避免虚假唤醒。

当消费者从等待中醒来时，它会检查runFlag。如果标志为false，则它会跳出循环。否则，它会从队列中读取一条消息，并通知生产者它正在“FULL_QUEUE”状态中等待。最后，它消费消息。

为了优雅地停止进程，它同样使用stop()方法：

```java
public void stop() {
    runFlag = false;
    dataQueue.notifyAllForEmpty();
}
```

runFlag设置为false后，通知所有处于EMPTY_QUEUE状态等待的消费者。这可确保所有消费者线程终止。

### 3.5 运行生产者线程和消费者线程

让我们创建一个具有最大所需容量的dataQueue对象：

```java
DataQueue dataQueue = new DataQueue(5);
```

现在，让我们创建生产者对象和一个线程：

```java
Producer producer = new Producer(dataQueue);
Thread producerThread = new Thread(producer);
```

然后，我们将初始化一个消费者对象和一个线程：

```java
Consumer consumer = new Consumer(dataQueue);
Thread consumerThread = new Thread(consumer);
```

最后，我们启动线程来启动进程：

```java
producerThread.start();
consumerThread.start();
```

它会一直运行，直到我们想要停止这些线程。停止它们很简单：

```java
producer.stop();
consumer.stop();
```

### 3.6 运行多个生产者和消费者

运行多个生产者和消费者类似于单个生产者和消费者的情况。**我们只需要创建所需数量的线程并启动它们**：

让我们创建多个生产者和线程并启动它们：

```java
Producer producer = new Producer(dataQueue);
for(int i = 0; i < producerCount; i++) {
    Thread producerThread = new Thread(producer);
    producerThread.start();
}
```

接下来，让我们创建所需数量的消费者对象和线程：

```java
Consumer consumer = new Consumer(dataQueue);
for(int i = 0; i < consumerCount; i++) {
    Thread consumerThread = new Thread(consumer);
    consumerThread.start();
}
```

我们可以通过在生产者和消费者对象上调用stop()方法来优雅地停止进程：

```java
producer.stop();
consumer.stop();
```

## 4. 使用BlockingQueue的简化示例

Java提供了一个线程安全的BlockingQueue接口。换句话说，**多个线程可以在这个队列中添加和删除，而不会出现任何并发问题**。

如果队列已满，它的put()方法会阻塞调用线程。同样，如果队列为空，它的take()方法会阻塞调用线程。

### 4.1 创建有界阻塞队列

我们可以使用构造函数中的容量值创建一个有界的BlockingQueue：

```java
BlockingQueue<Double> blockingQueue = new LinkedBlockingDeque<>(5);
```

### 4.2 简化produce方法

在producer()方法中，我们可以避免队列的显式同步：

```java
private void produce() {
    while (true) {
        double value = generateValue();
        try {
            blockingQueue.put(value);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
        }
        System.out.printf("[%s] Value produced: %f\n", Thread.currentThread().getName(), value);
    }
}
```

此方法不断地生成对象并将它们添加到队列中。

### 4.3 简化consume方法

consume()方法也不显式使用同步：

```java
private void consume() {
    while (true) {
        Double value;
        try {
            value = blockingQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
        }
        // Consume value
        System.out.printf("[%s] Value consumed: %f\n", Thread.currentThread().getName(), value);
    }
}
```

它只是从队列中获取一个值，不断地消费它。

### 4.4 运行生产者和消费者线程

我们可以根据需要创建任意数量的生产者和消费者线程：

```java
for (int i = 0; i < 2; i++) {
    Thread producerThread = new Thread(this::produce);
    producerThread.start();
}

for (int i = 0; i < 3; i++) {
    Thread consumerThread = new Thread(this::consume);
    consumerThread.start();
}
```

## 5. 总结

在本文中，我们学习了如何使用Java线程来实现生产者-消费者问题。此外，我们还学习了如何运行具有多个生产者和消费者的场景。