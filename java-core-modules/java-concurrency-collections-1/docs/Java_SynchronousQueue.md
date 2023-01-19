## 1. 概述

在本文中，我们介绍java.util.concurrent包中的SynchronousQueue。简单地说，这个实现允许我们以线程安全的方式在线程之间交换信息。

## 2. API介绍

SynchronousQueue只支持两个操作：take()和put()，并且这两个操作都是阻塞的。例如，当我们想向队列中添加一个元素时，我们需要调用put()方法。该方法将一直阻塞，直到其他线程调用take()方法，表示它已准备好获取元素。

虽然SynchronousQueue实现了Queue接口，但我们应该将其视为两个线程之间单个元素的交换点，其中一个线程传递一个元素，另一个线程接收该元素。

## 3. 使用共享变量实现切换

为了理解SynchronousQueue的作用，我们将在两个线程之间使用共享变量实现一个逻辑，然后，我们使用SynchronousQueue重写该逻辑，使我们的代码更简单、更易读。

假设我们有两个线程(一个生产者和一个消费者)，当生产者设置共享变量的值时，我们希望向消费者线程发出信号。然后，消费者线程将从共享变量中获取值。

我们将使用CountDownLatch来协调这两个线程，以防止消费者访问尚未设置的共享变量的值的情况。

我们定义一个原子整数sharedState变量和一个CountDownLatch用于协调处理：

```java
ExecutorService executor = Executors.newFixedThreadPool(2);
AtomicInteger sharedState = new AtomicInteger();
CountDownLatch countDownLatch = new CountDownLatch(1);
```

生产者将一个随机整数保存到sharedState变量中，并在countDownLatch上执行countDown()方法，向消费者发出信号，表示消费者此时可以从sharedState中获取值：

```java
Runnable producer = () -> {
    int producedElement = ThreadLocalRandom.current().nextInt();
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

最后启动消费者和生产者：

```java
executor.execute(producer);
executor.execute(consumer);

executor.awaitTermination(500, TimeUnit.MILLISECONDS);
executor.shutdown();
assertEquals(countDownLatch.getCount(), 0);
```

当运行我们的测试时，应该生成以下输出：

```text
...... >>> Saving an element: 626048884 to the exchange point 
...... >>> consumed an element: 626048884 from the exchange point
```

我们可以看到，要实现这样一个简单的功能(如在两个线程之间交换一个元素)，需要编写大量的代码。在下一节中，我们以另一种简单的方式来实现此目的。

## 4. 使用SynchronousQueue实现切换

现在让我们使用SynchronousQueue实现与上一节相同的功能。它具有双重效果，因为我们可以使用它在线程之间交换状态，并协调该操作，这样我们就不需要使用除了SynchronousQueue之外的任何东西。

首先，我们定义一个队列：

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

消费者只需使用take()方法检索该元素：

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

最后启动消费者和生产者：

```java
executor.execute(producer);
executor.execute(consumer);

executor.awaitTermination(500, TimeUnit.MILLISECONDS);
executor.shutdown();
assertEquals(0, queue.size());
```

当运行我们的测试时，应该生成以下输出：

```text
...... >>> Saving an element: -1117417662 to the exchange point 
...... >>> consumed an element: -1117417662 from the exchange point
```

我们可以看到，SynchronousQueue被用作线程之间的交换点，这比前面使用共享变量和CountDownLatch的示例要好得多，也更容易理解。

## 5. 总结

在本教程中，我们学习了SynchronousQueue构造。编写了一个生产者-消费者程序，该程序使用共享状态在两个线程之间交换数据，然后我们使用SynchronousQueue重写这个例子，充当协调生产者和消费者线程的交换点。