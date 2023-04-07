## 1.概述

[集合框架](https://docs.oracle.com/javase/8/docs/technotes/guides/collections/overview.html)是Java的一个关键组件。它提供了大量的接口和实现，使我们能够以直接的方式创建和操作不同类型的集合。

虽然使用普通的非同步集合总体上很简单，但在多线程环境(也称为并发编程)中工作时，它也可能成为一个令人生畏且容易出错的过程。

因此，Java平台通过在[Collections](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collections.html#synchronizedCollection(java.util.Collection))类中实现的不同同步包装器为这种情况提供了强大的支持。

这些包装器使得通过几个静态工厂方法创建提供的集合的同步视图变得容易。

在本教程中，我们将深入研究这些静态同步包装器。此外，我们将强调同步集合和并发集合之间的区别。

## 2.synchronizedCollection()方法

我们将在本综述中介绍的第一个同步包装器是synchronizedCollection()方法。顾名思义，它返回由指定[Collection](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collection.html)支持的线程安全集合。

现在，为了更清楚地了解如何使用此方法，让我们创建一个基本的单元测试：

```java
Collection<Integer> syncCollection = Collections.synchronizedCollection(new ArrayList<>());
    Runnable listOperations = () -> {
        syncCollection.addAll(Arrays.asList(1, 2, 3, 4, 5, 6));
    };
    
    Thread thread1 = new Thread(listOperations);
    Thread thread2 = new Thread(listOperations);
    thread1.start();
    thread2.start();
    thread1.join();
    thread2.join();
    
    assertThat(syncCollection.size()).isEqualTo(12);
}

```

如上所示，使用此方法创建提供的集合的同步视图非常简单。

为了证明该方法实际上返回了一个线程安全的集合，我们首先创建了几个线程。

之后，我们以lambda表达式的形式将一个[Runnable实例注入到它们的构造函数中。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Runnable.html)让我们记住Runnable是一个功能接口，所以我们可以用lambda表达式替换它。

最后，我们只是检查每个线程是否有效地将六个元素添加到同步集合中，所以它的最终大小是十二。

## 3.synchronizedList()方法

同样，类似于synchronizedCollection()方法，我们可以使用synchronizedList()包装器来创建一个同步[列表](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/List.html)。

正如我们所料，该方法返回指定List的线程安全视图：

```java
List<Integer> syncList = Collections.synchronizedList(new ArrayList<>());
```

毫不奇怪，synchronizedList()方法的使用看起来与其更高级别的对应方法synchronizedCollection()几乎相同。

因此，正如我们刚刚在之前的单元测试中所做的那样，一旦我们创建了一个同步列表，我们就可以生成多个线程。完成之后，我们将使用它们以线程安全的方式访问/操作目标列表。

此外，如果我们想要遍历同步集合并防止意外结果，我们应该明确提供我们自己的线程安全循环实现。因此，我们可以使用同步块来实现：

```java
List<String> syncCollection = Collections.synchronizedList(Arrays.asList("a", "b", "c"));
List<String> uppercasedCollection = new ArrayList<>();
    
Runnable listOperations = () -> {
    synchronized (syncCollection) {
        syncCollection.forEach((e) -> {
            uppercasedCollection.add(e.toUpperCase());
        });
    }
};

```

在我们需要迭代同步集合的所有情况下，我们都应该实现这个习惯用法。这是因为同步集合的迭代是通过对集合的多次调用来执行的。因此，它们需要作为单个原子操作来执行。

同步块的使用保证了操作的原子性。

## 4.synchronizedMap()方法

Collections类实现了另一个简洁的同步包装器，称为synchronizedMap()。我们可以用它来轻松创建一个同步的[Map](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Map.html)。

该方法返回所提供的Map实现的线程安全视图：

```java
Map<Integer, String> syncMap = Collections.synchronizedMap(new HashMap<>());

```

## 5.synchronizedSortedMap()方法

还有一个synchronizedMap()方法的对应实现。它称为synchronizedSortedMap()，我们可以使用它来创建同步的[SortedMap](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/SortedMap.html)实例：

```java
Map<Integer, String> syncSortedMap = Collections.synchronizedSortedMap(new TreeMap<>());

```

## 6.synchronizedSet()方法

接下来，继续本次审查，我们有synchronizedSet()方法。顾名思义，它允许我们以最少的麻烦创建同步[集。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Set.html)

包装器返回由指定Set支持的线程安全集合：

```java
Set<Integer> syncSet = Collections.synchronizedSet(new HashSet<>());

```

## 7.synchronizedSortedSet()方法

最后，我们将在此处展示的最后一个同步包装器是synchronizedSortedSet()。

与我们目前审查过的其他包装器实现类似，该方法返回给定[SortedSet](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/SortedSet.html)的线程安全版本：

```java
SortedSet<Integer> syncSortedSet = Collections.synchronizedSortedSet(new TreeSet<>());

```

## 8.同步与并发集合

到目前为止，我们仔细研究了集合框架的同步包装器。

现在，让我们关注同步集合和并发集合之间的区别，例如[ConcurrentHashMap](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html)和[BlockingQueue](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/BlockingQueue.html)实现。

### 8.1.同步集合

[同步集合通过内在](https://docs.oracle.com/javase/tutorial/essential/concurrency/locksync.html)[锁定](https://docs.oracle.com/javase/tutorial/essential/concurrency/locksync.html)实现线程安全，整个集合都被锁定。内部锁定是通过包装集合方法中的同步块实现的。

正如我们所料，同步集合可确保多线程环境中的数据一致性/完整性。但是，它们可能会降低性能，因为一次只能有一个线程访问集合(也称为同步访问)。

有关如何使用同步方法和块的详细指南，请查看[我们](https://www.baeldung.com/java-synchronized)关于该主题的文章。

### 8.2.并发集合

并发集合(例如ConcurrentHashMap)通过将它们的数据分成段来实现线程安全。例如，在ConcurrentHashMap中，不同的线程可以获得每个段上的锁，因此多个线程可以同时访问Map(也称为并发访问)。

由于并发线程访问的固有优势，并发集合比同步集合性能更高。

因此，选择使用哪种类型的线程安全集合取决于每个用例的要求，并且应该相应地进行评估。

## 9.总结

在本文中，我们深入研究了Collections类中实现的一组同步包装器。

此外，我们强调了同步和并发集合之间的差异，还研究了它们为实现线程安全而实现的方法。