## 1. 概述

在本教程中，我们将了解 LRU[缓存](https://en.wikipedia.org/wiki/Cache_(computing))并查看Java中的实现。

## 2.LRU缓存

最近最少使用 (LRU) 缓存是一种缓存逐出算法，它按使用顺序组织元素。在 LRU 中，顾名思义，最长时间未使用的元素将从缓存中逐出。

例如，如果我们有一个容量为三项的缓存：

[![截图来自-2021-07-03-14-30-34-1](https://www.baeldung.com/wp-content/uploads/2021/07/Screenshot-from-2021-07-03-14-30-34-1.png)](https://www.baeldung.com/wp-content/uploads/2021/07/Screenshot-from-2021-07-03-14-30-34-1.png)

最初，缓存是空的，我们将元素 8 放入缓存中。元素 9 和 6 像以前一样被缓存。但是现在，缓存容量已满，要放入下一个元素，我们必须驱逐缓存中最近最少使用的元素。

在我们用Java实现 LRU 缓存之前，了解缓存的一些方面是很好的：

-   所有操作应按 O(1) 的顺序运行
-   缓存大小有限
-   所有缓存操作都必须支持并发
-   如果缓存已满，添加新项必须调用 LRU 策略

### 2.1. LRU 缓存的结构

现在，让我们考虑一个有助于我们设计缓存的问题。

我们如何设计一个数据结构，可以在恒定时间内执行读取、排序(时间排序)和删除元素等操作？

看来要找到这个问题的答案，我们还需要深入思考前面讲过的LRU缓存及其特点：

-   在实践中，LRU 缓存是一种[队列](https://www.baeldung.com/java-queue)——如果一个元素被重新访问，它会走到驱逐顺序的末尾
-   由于缓存的大小有限，因此该队列将具有特定的容量。每当引入新元素时，都会将其添加到队列的头部。当驱逐发生时，它发生在队列的尾部。
-   命中缓存中的数据必须在恒定时间内完成，这在Queue中是不可能的！但是，Java 的[HashMap](https://www.baeldung.com/java-hashmap)数据结构是可能的
-   必须在常数时间内移除最近最少使用的元素，这意味着对于Queue的实现，我们将使用[DoublyLinkedList](https://en.wikipedia.org/wiki/Doubly_linked_list)而不是[SingleLinkedList](https://www.baeldung.com/java-linkedlist)或数组

所以，LRU 缓存只不过是DoublyLinkedList和HashMap的组合，如下所示：

[![截图来自-2021-07-09-02-10-25-1](https://www.baeldung.com/wp-content/uploads/2021/07/Screenshot-from-2021-07-09-02-10-25-1.png)](https://www.baeldung.com/wp-content/uploads/2021/07/Screenshot-from-2021-07-09-02-10-25-1.png)

这个想法是将键保留在Map上， 以便快速访问Queue中的数据。

### 2.2. LRU算法

LRU 算法非常简单！如果键存在于HashMap 中，则缓存命中；否则，这是缓存未命中。

发生缓存未命中后，我们将执行两个步骤：

1.  在列表前面添加一个新元素。
2.  在HashMap中添加一个新条目并引用列表的头部。

而且，我们将在缓存命中后执行两个步骤：

1.  删除命中元素并将其添加到列表前面。
2.  使用对列表前面的新引用更新HashMap 。

现在，是时候看看我们如何在Java中实现 LRU 缓存了！

## 3.Java实现

首先，我们将定义缓存接口：

```java
public interface Cache<K, V> {
    boolean set(K key, V value);
    Optional<V> get(K key);
    int size();
    boolean isEmpty();
    void clear();
}
```

现在，我们将定义代表缓存的LRUCache类：

```java
public class LRUCache<K, V> implements Cache<K, V> {
    private int size;
    private Map<K, LinkedListNode<CacheElement<K,V>>> linkedListNodeMap;
    private DoublyLinkedList<CacheElement<K,V>> doublyLinkedList;

    public LRUCache(int size) {
        this.size = size;
        this.linkedListNodeMap = new HashMap<>(maxSize);
        this.doublyLinkedList = new DoublyLinkedList<>();
    }
   // rest of the implementation
}
```

我们可以创建具有特定大小的LRUCache 实例。在此实现中，我们使用HashMap集合来存储对LinkedListNode的所有引用。

现在，让我们讨论对LRUCache的操作。

### 3.1. 看跌操作

第一个是put方法：

```java
public boolean put(K key, V value) {
    CacheElement<K, V> item = new CacheElement<K, V>(key, value);
    LinkedListNode<CacheElement<K, V>> newNode;
    if (this.linkedListNodeMap.containsKey(key)) {
        LinkedListNode<CacheElement<K, V>> node = this.linkedListNodeMap.get(key);
        newNode = doublyLinkedList.updateAndMoveToFront(node, item);
    } else {
        if (this.size() >= this.size) {
            this.evictElement();
        }
        newNode = this.doublyLinkedList.add(item);
    }
    if(newNode.isEmpty()) {
        return false;
    }
    this.linkedListNodeMap.put(key, newNode);
    return true;
 }
```

首先，我们在存储所有键/引用的linkedListNodeMap中找到键。如果该键存在，则发生缓存命中，它已准备好从DoublyLinkedList中检索CacheElement并将其移动到前面。

之后，我们用新的引用更新linkedListNodeMap并将其移动到列表的前面：

```java
public LinkedListNode<T> updateAndMoveToFront(LinkedListNode<T> node, T newValue) {
    if (node.isEmpty() || (this != (node.getListReference()))) {
        return dummyNode;
    }
    detach(node);
    add(newValue);
    return head;
}
```

首先，我们检查节点是否为空。此外，节点的引用必须与列表相同。之后，我们从列表中分离节点并将newValue添加到列表中。

但是如果键不存在，就会发生缓存未命中，我们必须将一个新键放入linkedListNodeMap中。在此之前，我们先检查列表大小。如果列表已满，我们必须从列表中驱逐最近最少使用的元素。

### 3.2. 获取操作

我们来看看我们的get操作：

```java
public Optional<V> get(K key) {
   LinkedListNode<CacheElement<K, V>> linkedListNode = this.linkedListNodeMap.get(key);
   if(linkedListNode != null && !linkedListNode.isEmpty()) {
       linkedListNodeMap.put(key, this.doublyLinkedList.moveToFront(linkedListNode));
       return Optional.of(linkedListNode.getElement().getValue());
   }
   return Optional.empty();
 }
```

正如我们在上面看到的，这个操作很简单。首先，我们从linkedListNodeMap中获取节点，然后检查它是否不为 null 或为空。

其余操作与之前相同，仅moveToFront方法有一处不同：

```java
public LinkedListNode<T> moveToFront(LinkedListNode<T> node) {
    return node.isEmpty() ? dummyNode : updateAndMoveToFront(node, node.getElement());
}
```

现在，让我们创建一些测试来验证我们的缓存是否正常工作：

```java
@Test
public void addSomeDataToCache_WhenGetData_ThenIsEqualWithCacheElement(){
    LRUCache<String,String> lruCache = new LRUCache<>(3);
    lruCache.put("1","test1");
    lruCache.put("2","test2");
    lruCache.put("3","test3");
    assertEquals("test1",lruCache.get("1").get());
    assertEquals("test2",lruCache.get("2").get());
    assertEquals("test3",lruCache.get("3").get());
}

```

现在，让我们测试驱逐政策：

```java
@Test
public void addDataToCacheToTheNumberOfSize_WhenAddOneMoreData_ThenLeastRecentlyDataWillEvict(){
    LRUCache<String,String> lruCache = new LRUCache<>(3);
    lruCache.put("1","test1");
    lruCache.put("2","test2");
    lruCache.put("3","test3");
    lruCache.put("4","test4");
    assertFalse(lruCache.get("1").isPresent());
 }
```

## 4. 处理并发

到目前为止，我们假设我们的缓存只是在单线程环境中使用。

为了使这个容器线程安全，我们需要同步所有公共方法。让我们在之前的实现中添加一个[ReentrantReadWriteLock](https://www.baeldung.com/java-thread-safety#reentrant-locks)和[ConcurrentHashMap](https://www.baeldung.com/java-concurrent-map)：

```java
public class LRUCache<K, V> implements Cache<K, V> {
    private int size;
    private final Map<K, LinkedListNode<CacheElement<K,V>>> linkedListNodeMap;
    private final DoublyLinkedList<CacheElement<K,V>> doublyLinkedList;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public LRUCache(int size) {
        this.size = size;
        this.linkedListNodeMap = new ConcurrentHashMap<>(size);
        this.doublyLinkedList = new DoublyLinkedList<>();
    }
// ...
}
```

我们更喜欢使用可重入读/写锁而不是将方法声明为[同步](https://www.baeldung.com/java-thread-safety#synchronized-statements)的，因为它使我们更灵活地决定何时使用读写锁。

### 4.1. 写锁

现在，让我们在put方法中添加对writeLock的调用：

```java
public boolean put(K key, V value) {
  this.lock.writeLock().lock();
   try {
       //..
   } finally {
       this.lock.writeLock().unlock();
   }
}
```

当我们在资源上使用writeLock时，只有持有锁的线程才能写入或读取资源。因此，所有其他尝试读取或写入资源的线程都必须等待，直到当前锁持有者释放它。

这对于防止 [死锁](https://www.baeldung.com/cs/os-deadlock)非常重要。如果try块中的任何操作失败，我们仍然会在方法结束时使用finally块退出函数之前释放锁。

需要writeLock的其他操作之一是evictElement，我们在put方法中使用了它：

```java
private boolean evictElement() {
    this.lock.writeLock().lock();
    try {
        //...
    } finally {
        this.lock.writeLock().unlock();
    }
}
```

### 4.2. 读锁

现在是时候向get方法添加一个readLock调用了：

```java
public Optional<V> get(K key) {
    this.lock.readLock().lock();
    try {
        //...
    } finally {
        this.lock.readLock().unlock();
    }
}

```

这似乎正是我们用put方法所做的。唯一的区别是我们使用readLock而不是writeLock。因此，读写锁之间的这种区别允许我们在缓存未更新时并行读取缓存。

现在，让我们在并发环境中测试我们的缓存：

```java
@Test
public void runMultiThreadTask_WhenPutDataInConcurrentToCache_ThenNoDataLost() throws Exception {
    final int size = 50;
    final ExecutorService executorService = Executors.newFixedThreadPool(5);
    Cache<Integer, String> cache = new LRUCache<>(size);
    CountDownLatch countDownLatch = new CountDownLatch(size);
    try {
        IntStream.range(0, size).<Runnable>mapToObj(key -> () -> {
            cache.put(key, "value" + key);
            countDownLatch.countDown();
       }).forEach(executorService::submit);
       countDownLatch.await();
    } finally {
        executorService.shutdown();
    }
    assertEquals(cache.size(), size);
    IntStream.range(0, size).forEach(i -> assertEquals("value" + i,cache.get(i).get()));
}
```

## 5.总结

在本教程中，我们了解了 LRU 缓存到底是什么，包括它的一些最常见的特性。然后，我们看到了一种用Java实现 LRU 缓存的方法，并探索了一些最常见的操作。

最后，我们介绍了使用锁机制的并发操作。