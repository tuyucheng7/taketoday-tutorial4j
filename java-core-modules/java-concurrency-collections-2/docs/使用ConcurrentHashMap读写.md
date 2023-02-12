## 1. 简介

在本教程中，我们将学习如何使用ConcurrentHashMap类以线程安全的方式读取和写入哈希表数据结构。

## 2. 概述

ConcurrentHashMap是[ConcurrentMap接口](https://www.baeldung.com/java-concurrent-map)的一种实现，它是Java提供的线程安全集合之一。它由常规Map支持，并且与[Hashtable](https://www.baeldung.com/java-hash-table)的工作方式类似，我们将在以下部分中介绍一些细微差别。

### 2.1 有用的方法

ConcurrentHashMap API规范提供了使用集合的实用方法。在本教程中，我们将主要研究其中两个：

-   get(K key)：检索给定键处的元素，这就是我们的读取方法。
-   computeIfPresent(K key, BiFunction<K, V, V> remappingFunction)：如果键存在，则将remappingFunction应用于给定键处的值。

我们将在第3节中看到这些方法的实际应用。

### 2.2 为什么使用ConcurrentHashMap

ConcurrentHashMap和常规HashMap之间的主要区别在于，前者实现了读取的总并发性和写入的高并发性。

**读取操作保证不会被阻塞或阻塞一个key，写入操作被阻塞并阻塞Map Entry级别的其他写入**。这两个想法在我们想要实现高吞吐量和最终一致性的环境中很重要。

HashTable和Collections.synchronizedMap集合也实现了读写并发。但是，它们的效率较低，因为它们锁定了整个集合，而不是仅锁定线程正在写入的Entry。

另一方面，**ConcurrentHashMap类锁定在Map Entry级别**。因此，不会阻塞其他线程写入其他Map键。因此，要实现高吞吐量，与HashTable和synchronizedMap集合相比，多线程环境中的ConcurrentHashMap是更好的选择。

## 3. 线程安全操作

ConcurrentHashMap实现了代码需要被视为线程安全的大部分保证，这有助于避免[Java中一些常见的并发陷阱](https://www.baeldung.com/java-common-concurrency-pitfalls)。

为了说明ConcurrentHashMap在多线程环境中的工作原理，我们将使用一个Java测试来检索和更新给定数字的频率。让我们首先定义测试的基本结构：

```java
class ConcurrentHashMapUnitTest {

    private final Map<Integer, Integer> frequencyMap = new ConcurrentHashMap<>();

    @BeforeEach
    public void setup() {
        frequencyMap.put(0, 0);
        frequencyMap.put(1, 0);
        frequencyMap.put(2, 0);
    }

    @AfterEach
    public void teardown() {
        frequencyMap.clear();
    }

    private static void sleep(int timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
```

上面的类定义了数字的频率Map，一个用初始值填充它的setup方法，一个清除其内容的teardown方法，以及一个处理InterruptedException的辅助方法sleep。

### 3.1 读

ConcurrentHashMap允许完全并发读取，这意味着**任何给定数量的线程都可以同时读取相同的键**。这也意味着读取不会阻塞，也不会被写入操作阻塞。因此，从Map中读取可能会得到“旧的”或不一致的值。

让我们看一个例子，一个线程写入一个键，第二个线程在写入完成前读取，第三个线程在写入完成后读取：

```java
@Test
public void givenOneThreadIsWriting_whenAnotherThreadReads_thenGetCorrectValue() throws Exception {
    ExecutorService threadExecutor = Executors.newFixedThreadPool(3);

    Runnable writeAfter1Sec = () -> frequencyMap.computeIfPresent(1, (k, v) -> {
        sleep(1);
        return frequencyMap.get(k) + 1;
    });

    Callable<Integer> readNow = () -> frequencyMap.get(1);
    Callable<Integer> readAfter1001Ms = () -> {
        TimeUnit.MILLISECONDS.sleep(1001);
        return frequencyMap.get(1);
    };

    threadExecutor.submit(writeAfter1Sec);
    List<Future<Integer>> results = threadExecutor.invokeAll(asList(readNow, readAfter1001Ms));

    assertEquals(0, results.get(0).get());
    assertEquals(1, results.get(1).get());

    if (threadExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
        threadExecutor.shutdown();
    }
}
```

让我们仔细看看上面的代码中发生了什么：

1.  我们首先定义一个具有一个写入线程和两个读取线程的ExecutorService。写入操作需要1秒钟才能完成，因此，在此之前的任何读取都应该得到旧结果。在此之后的任何读取(在本例中恰好是1秒之后)都应该获得更新后的值。
2.  然后，我们使用invokeAll调用所有读取线程，并按顺序将结果收集到列表中。因此，列表的位置0指的是第一次读取，位置1指的是第二次读取。
3.  最后，我们使用assertEquals验证已完成任务的结果 并关闭ExecutorService。

从该代码中，我们得出结论，即使其他线程同时写入同一资源，读取也不会被阻塞。**如果我们将读取和写入想象成事务，则ConcurrentHashMap实现了读取的最终一致性**。这意味着我们不会总是读取一致的值(最新的值)，但是一旦Map停止接收写入，读取就会再次变得一致。查看此[事务简介](https://www.baeldung.com/cs/transactions-intro)以获取有关最终一致性的更多详细信息。

>   提示：如果你还想使读取阻塞并被其他读取阻塞，请不要使用get()方法。相反，你可以实现一个标识BiFunction，该函数返回给定键上未修改的值，并将该函数传递给computeIfPresent方法。使用它，我们将牺牲读取速度来防止读取旧值或不一致值时出现问题。

### 3.2 写

如前所述，**ConcurrentHashMap实现了写入的部分并发，这会阻塞对同一Map键的其他写入，并允许写入不同的键**。这对于在多线程环境中实现高吞吐量和写入一致性至关重要。为了说明一致性，让我们定义一个测试，其中两个线程写入同一资源并检查Map如何处理：

```java
@Test
public void givenOneThreadIsWriting_whenAnotherThreadWritesAtSameKey_thenWaitAndGetCorrectValue() throws Exception {
    ExecutorService threadExecutor = Executors.newFixedThreadPool(2);

    Callable<Integer> writeAfter5Sec = () -> frequencyMap.computeIfPresent(1, (k, v) -> {
        sleep(5);
        return frequencyMap.get(k) + 1;
    });

    Callable<Integer> writeAfter1Sec = () -> frequencyMap.computeIfPresent(1, (k, v) -> {
        sleep(1);
        return frequencyMap.get(k) + 1;
    });

    List<Future<Integer>> results = threadExecutor.invokeAll(asList(writeAfter5Sec, writeAfter1Sec));

    assertEquals(1, results.get(0).get());
    assertEquals(2, results.get(1).get());

    if (threadExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
        threadExecutor.shutdown();
    }
}
```

上面的测试显示了两个写入线程被提交给ExecutorService。第一个线程需要5秒钟写入，第二个线程需要1秒钟写入。第一个线程获取锁并阻塞Map键1处的任何其他写入活动。因此，第二个线程必须等待5秒钟，直到第一个线程释放锁。第一次写入完成后，第二个线程将获取最新的值并在1秒钟内更新它。

ExecutorService的results列表按任务提交的顺序排列，因此第一个元素应返回1，第二个元素应返回2。

ConcurrentHashMap的另一个用例是实现不同Map键中写入的高吞吐量。让我们用另一个单元测试来说明这一点，该单元测试使用两个写入线程来更新Map中的不同键：

```java
@Test
public void givenOneThreadIsWriting_whenAnotherThreadWritesAtDifferentKey_thenNotWaitAndGetCorrectValue() throws Exception {
    ExecutorService threadExecutor = Executors.newFixedThreadPool(2);

    Callable<Integer> writeAfter5Sec = () -> frequencyMap.computeIfPresent(1, (k, v) -> {
        sleep(5);
        return frequencyMap.get(k) + 1;
    });

    AtomicLong time = new AtomicLong(System.currentTimeMillis());
    Callable<Integer> writeAfter1Sec = () -> frequencyMap.computeIfPresent(2, (k, v) -> {
        sleep(1);
        time.set((System.currentTimeMillis() - time.get()) / 1000);
        return frequencyMap.get(k) + 1;
    });

    threadExecutor.invokeAll(asList(writeAfter5Sec, writeAfter1Sec));

    assertEquals(1, time.get());

    if (threadExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
        threadExecutor.shutdown();
    }
}
```

该测试验证第二个线程不需要等待第一个线程完成，因为写入发生在不同的Map键上。因此，第二次写入只需1秒钟即可完成。**在ConcurrentHashMap中，线程可以在不同的Map Entry中同时工作，并且与其他线程安全结构相比，并发写入操作更快**。

## 4. 总结

在本文中，我们了解了如何写入和读取ConcurrentHashMap以实现写入和读取的高吞吐量以及读取的最终一致性。