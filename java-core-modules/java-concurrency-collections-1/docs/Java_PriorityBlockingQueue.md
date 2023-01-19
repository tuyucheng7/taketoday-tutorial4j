## 1. 概述

在本文中，我们通过一些实际案例重点介绍PriorityBlockingQueue类。我们首先演示PriorityBlockingQueue中的元素是如何按优先级排序的。接下来，我们将演示如何使用这种类型的队列来阻塞线程。最后，我们演示在跨多个线程处理数据时如何结合使用这两个特性。

## 2. 元素优先级

与标准队列不同，添加到PriorityBlockingQueue中的元素类型必须是以下两种形式：

+ 元素实现Comparable
+ 或者可以不需要实现Comparable，前提是你提供Comparator

通过使用Comparator或Comparable实现来比较元素，PriorityBlockingQueue将始终被排序。其目的是以始终优先排序最高优先级元素的方式实现比较逻辑。然后，当我们从队列中删除一个元素时，它总是具有最高优先级的元素。

首先，我们在单个线程中使用队列，而不是在多个线程中使用它。通过这样做，可以很容易地证明元素在单元测试中是如何排序的：

```java
class PriorityBlockingQueueIntegrationTest {

    @Test
    void givenUnorderedValues_whenPolling_thenShouldOrderQueue() {
        PriorityBlockingQueue<Integer> queue = new PriorityBlockingQueue<>();
        ArrayList<Integer> polledElements = new ArrayList<>();

        queue.add(1);
        queue.add(5);
        queue.add(2);
        queue.add(3);
        queue.add(4);

        queue.drainTo(polledElements);

        assertThat(polledElements).containsExactly(1, 2, 3, 4, 5);
    }
}
```

正如我们所看到的，尽管以随机顺序将元素添加到队列中，但当我们开始轮询它们时，它们将被排序。这是因为Integer类实现了Comparable，而Comparable又用于确保我们以升序顺序将它们从队列中取出。

同样值得注意的是，当两个元素进行比较并且相同时，不能保证它们的排序方式。

## 3. 使用队列阻塞

如果我们处理的是标准队列，我们调用poll()来检索元素。但是，如果队列为空，则对poll()的调用将返回null。

PriorityBlockingQueue实现了BlockingQueue接口，
它为我们提供了一些额外的方法，允许我们对空队列执行删除操作时进行阻塞。让我们尝试使用take()方法，它应该可以做到这一点：

```java
class PriorityBlockingQueueIntegrationTest {

    @Test
    void whenPollingEmptyQueue_thenShouldBlockThread() throws InterruptedException {
        PriorityBlockingQueue<Integer> queue = new PriorityBlockingQueue<>();

        new Thread(() -> {
            System.out.println("Polling...");

            try {
                Integer poll = queue.take();
                System.out.println("Polled: " + poll);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        Thread.sleep(TimeUnit.SECONDS.toMillis(5));
        System.out.println("Adding to queue");
        queue.add(1);
    }
}
```

虽然使用sleep()是一种稍微脆弱的演示方式，但当我们运行这段代码时，我们会看到：

```text
Polling...
Adding to queue
Polled: 1
```

这证明take()在添加元素之前被阻塞：

1. 该线程将打印“Polling”以证明它已启动。
2. 然后测试将暂停大约5秒钟，以证明此时线程必须调用take()。
3. 我们将“1”添加到队列中，并且应该或多或少立即看到“Polled：1”打印，表明take()在元素可用时立即返回。

值得一提的是，BlockingQueue接口还为我们提供了在添加到满队列时进行阻塞的方法。但是，PriorityBlockingQueue是无界的。这意味着它永远不会被填满，因此总是可以添加新元素。

## 4. 结合使用阻塞和优先级

现在我们了解了PriorityBlockingQueue的两个关键概念，让我们结合使用它们。我们可以简单地扩展前面的例子，但这次将更多元素添加到队列中：

```java
class PriorityBlockingQueueIntegrationTest {

    @Test
    void whenPollingEmptyQueue_thenShouldBlockThread() throws InterruptedException {
        PriorityBlockingQueue<Integer> queue = new PriorityBlockingQueue<>();

        final Thread thread = new Thread(() -> {
            LOG.info("Polling...");
            while (true) {
                try {
                    Integer poll = queue.take();
                    LOG.info("Polled: " + poll);
                } catch (InterruptedException ignored) {

                }
            }
        });

        thread.start();
        Thread.sleep(TimeUnit.SECONDS.toMillis(5));
        LOG.info("Adding to queue");

        queue.addAll(newArrayList(1, 5, 6, 1, 2, 6, 7));
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
    }
}
```

同样，虽然由于使用了sleep()，这有点脆弱，但它仍然向我们展示了一个有效的用例。
我们创建一个阻塞的队列，等待添加元素。然后一次性添加许多元素。运行结果如下所示：

```text
20:02:56.390 [Thread-0] INFO  [c.t.t.c.p.PriorityBlockingQueueIntegrationTest] >>> Polling... 
20:03:01.400 [main] INFO  [c.t.t.c.p.PriorityBlockingQueueIntegrationTest] >>> Adding to queue 
20:03:01.408 [Thread-0] INFO  [c.t.t.c.p.PriorityBlockingQueueIntegrationTest] >>> Polled: 1 
20:03:01.408 [Thread-0] INFO  [c.t.t.c.p.PriorityBlockingQueueIntegrationTest] >>> Polled: 1 
20:03:01.408 [Thread-0] INFO  [c.t.t.c.p.PriorityBlockingQueueIntegrationTest] >>> Polled: 2 
20:03:01.408 [Thread-0] INFO  [c.t.t.c.p.PriorityBlockingQueueIntegrationTest] >>> Polled: 5 
20:03:01.408 [Thread-0] INFO  [c.t.t.c.p.PriorityBlockingQueueIntegrationTest] >>> Polled: 6 
20:03:01.408 [Thread-0] INFO  [c.t.t.c.p.PriorityBlockingQueueIntegrationTest] >>> Polled: 6 
20:03:01.408 [Thread-0] INFO  [c.t.t.c.p.PriorityBlockingQueueIntegrationTest] >>> Polled: 7 
```

## 5. 总结

在本文中，我们演示了如何使用PriorityBlockingQueue来阻塞线程，直到添加了一些元素，并且我们还能够根据优先级处理这些元素。