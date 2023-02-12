## 1. 概述

在本文中，我们将研究java.util.concurrent包中的[SynchronousQueue](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/SynchronousQueue.html)。

简单地说，这个实现允许我们以线程安全的方式在线程之间交换信息。

## 2. API介绍

**SynchronousQueue只有两个支持的操作：take()和put()，并且这两个操作都是阻塞的**。

例如，当我们要向队列中添加一个元素时，我们需要调用put()方法。该方法将一直阻塞，直到其他线程调用take()方法，表示它已准备好获取元素。

虽然SynchronousQueue实现了Queue接口，但我们应该将其视为两个线程之间单个元素的交换点，其中一个线程正在传递一个元素，另一个线程正在获取该元素。

## 3. 使用共享变量实现切换

为了了解为什么SynchronousQueue如此有用，我们将使用两个线程之间的共享变量实现一个逻辑，接下来，我们将使用SynchronousQueue重写该逻辑，使我们的代码更简单、更易读。

假设我们有两个线程(一个生产者和一个消费者)-当生产者设置共享变量的值时，我们希望向消费者线程发送信号。接下来，消费者线程将从共享变量中获取值。

我们将使用CountDownLatch来协调这两个线程，以防止消费者访问尚未设置的共享变量的值的情况。

我们将定义一个原子整数sharedState变量和一个CountDownLatch用于协调处理：

```java
ExecutorService executor = Executors.newFixedThreadPool(2);
AtomicInteger sharedState = new AtomicInteger();
CountDownLatch countDownLatch = new CountDownLatch(1);
```

生产者将一个随机整数保存到sharedState变量中，并在countDownLatch上执行countDown()方法，向消费者发出信号，表示消费者此时可以从sharedState中获取值：

```java
Runnable producer = () -> {
    int producedElement = ThreadLocalRandom
        .current()
        .nextInt();
    LOG.info("Saving an element: " + producedElement + " to the exchange point");
    sharedState.set(producedElement);
    countDownLatch.countDown();
};
```

消费者将使用await()方法等待countDownLatch。当生产者发出已设置变量的信号时，消费者将从sharedState中获取它：

```java
Runnable consumer = () -> {
    try {
        countDownLatch.await();
        int consumedElement = sharedState.get();
        LOG.info("consumed an element: " + consumedElement + " from the exchange point");
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
};
```

最后但同样重要的是，让我们开始我们的程序：

```java
executor.execute(producer);
executor.execute(consumer);

executor.awaitTermination(500, TimeUnit.MILLISECONDS);
executor.shutdown();
assertEquals(countDownLatch.getCount(), 0);
```

它将产生以下输出：

```shell
...... >>> Saving an element: 626048884 to the exchange point 
...... >>> consumed an element: 626048884 from the exchange point
```

我们可以看到，要实现这样一个简单的功能(如在两个线程之间交换一个元素)，需要编写大量的代码。在下一节中，我们以另一种简单的方式来实现此目的。

## 4. 使用同步队列实现切换

现在让我们使用SynchronousQueue实现与上一节相同的功能。它具有双重效果，因为我们可以使用它来在线程之间交换状态，并协调该操作，这样我们就不需要使用除了SynchronousQueue之外的任何东西。

首先，我们将定义一个队列：

```java
ExecutorService executor = Executors.newFixedThreadPool(2);
final SynchronousQueue<Integer> queue = new SynchronousQueue<>();
```

生产者将调用put()方法，该方法将阻塞，直到其他线程从队列中获取元素：

```java
Runnable producer = () -> {
    int producedElement = ThreadLocalRandom.current().nextInt();
    try {
        LOG.info("Saving an element: " + producedElement + " to the exchange point");
        queue.put(producedElement);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
};
```

消费者将简单地使用take()方法检索该元素：

```java
Runnable consumer = () -> {
    try {
        Integer consumedElement = queue.take();
        LOG.info("consumed an element: " + consumedElement + " from the exchange point");
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
};
```

接下来启动我们的程序：

```java
executor.execute(producer);
executor.execute(consumer);

executor.awaitTermination(500, TimeUnit.MILLISECONDS);
executor.shutdown();
assertEquals(0, queue.size());
```

它将产生以下输出：

```shell
...... >>> Saving an element: -1117417662 to the exchange point 
...... >>> consumed an element: -1117417662 from the exchange point
```

我们可以看到SynchronousQueue被用作线程之间的交换点，这比前面使用共享变量和CountDownLatch的示例要好得多，也更容易理解。

## 5. 总结

在本快速教程中，我们了解了SynchronousQueue构造。我们创建了一个使用共享状态在两个线程之间交换数据的程序，然后重写了该程序以利用SynchronousQueue构造。这用作协调生产者和消费者线程的交换点。