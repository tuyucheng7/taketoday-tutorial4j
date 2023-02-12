## 1. 概述

在本教程中，我们将研究java.util.concurrent.Exchanger<T\>。这可以作为Java中两个线程在它们之间交换对象的公共点。

## 2. Exchanger简介

**Java中的Exchanger类可用于在两个线程之间共享T类型的对象**。该类仅提供一个重载方法exchange(T t)。

当调用exchange时，等待对中的另一个线程也调用它。此时，第二个线程发现第一个线程正在等待它的对象。线程交换它们持有的对象并发出交换信号，现在它们可以返回。

让我们看一个例子来理解两个线程之间使用Exchanger的消息交换：

```java
@Test
void givenThreads_whenMessageExchanged_thenCorrect() {
    Exchanger<String> exchanger = new Exchanger<>();

    Runnable taskA = () -> {
        try {
            String message = exchanger.exchange("from A");
            assertEquals("from B", message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    };

    Runnable taskB = () -> {
        try {
            String message = exchanger.exchange("from B");
            assertEquals("from A", message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    };

    CompletableFuture.allOf(runAsync(taskA), runAsync(taskB)).join();
}
```

在这里，我们有两个线程使用公共交换器在彼此之间交换String消息。让我们看一个例子，我们用一个新线程与主线程交换一个对象：

```java
@Test
void givenThread_whenExchangedMessage_thenCorrect() throws InterruptedException {
    Exchanger<String> exchanger = new Exchanger<>();

    Runnable runner = () -> {
        try {
            String message = exchanger.exchange("from runner");
            assertEquals("to runner", message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    };

    CompletableFuture<Void> result = runAsync(runner);
    String msg = exchanger.exchange("to runner");
    assertEquals("from runner", msg);
    result.join();
}
```

请注意，我们需要先启动runner线程，然后在主线程中调用exchange()。

另请注意，如果第二个线程没有及时到达交换点，则第一个线程的调用可能会超时。可以使用重载的exchange(T t, long timeout, TimeUnit timeUnit)来控制第一个线程应该等待多长时间。

## 3. 无GC数据交换

Exchanger可用于创建管道类型的模式，将数据从一个线程传递到另一个线程。在本节中，我们将创建一个简单的线程堆栈，作为管道在彼此之间连续传递数据。

```java
private static final int BUFFER_SIZE = 100;

@Test
void givenData_whenPassedThrough_thenCorrect() throws InterruptedException, ExecutionException {
    Exchanger<Queue<String>> readerExchanger = new Exchanger<>();
    Exchanger<Queue<String>> writerExchanger = new Exchanger<>();
    int counter = 0;

    Runnable reader = () -> {
        Queue<String> readerBuffer = new ConcurrentLinkedQueue<>();
        while (true) {
            readerBuffer.add(UUID.randomUUID().toString());
            if (readerBuffer.size() >= BUFFER_SIZE) {
                try {
                    readerBuffer = readerExchanger.exchange(readerBuffer);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
        }
    };

    Runnable processor = () -> {
        Queue<String> processorBuffer = new ConcurrentLinkedQueue<>();
        Queue<String> writerBuffer = new ConcurrentLinkedQueue<>();
        try {
            processorBuffer = readerExchanger.exchange(processorBuffer);
            while (true) {
                writerBuffer.add(processorBuffer.poll());
                if (processorBuffer.isEmpty()) {
                    try {
                        processorBuffer = readerExchanger.exchange(processorBuffer);
                        writerBuffer = writerExchanger.exchange(writerBuffer);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    };

    Runnable writer = () -> {
        Queue<String> writerBuffer = new ConcurrentLinkedQueue<>();
        try {
            writerBuffer = writerExchanger.exchange(writerBuffer);
            while (true) {
                System.out.println(writerBuffer.poll());
                if (writerBuffer.isEmpty()) {
                    writerBuffer = writerExchanger.exchange(writerBuffer);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    };

    CompletableFuture.allOf(runAsync(reader), runAsync(processor), runAsync(writer)).get();
}
```

在这里，我们有三个线程：reader、processor和writer。它们一起作为单个管道在它们之间交换数据。

readerExchanger在reader和processor线程之间共享，而writerExchanger在processor和writer线程之间共享。

请注意，此处的示例仅用于演示。在使用while(true)创建无限循环时，我们必须小心。

**这种在重用缓冲区的同时交换数据的模式可以减少垃圾回收**。exchange方法返回相同的队列实例，因此这些对象不会有GC。与任何阻塞队列不同，交换器不会创建任何节点或对象来保存和共享数据。

创建这样的管道类似于Disrupter模式，但有一个关键区别，Disrupter模式支持多个生产者和消费者，而交换器是在一对消费者和生产者之间使用。

## 4. 总结

因此，我们了解了Java中的Exchanger<T\>是什么，它是如何工作的，并且我们已经了解了如何使用Exchanger类。此外，我们还创建了一个管道并演示了线程之间的无GC的数据交换。