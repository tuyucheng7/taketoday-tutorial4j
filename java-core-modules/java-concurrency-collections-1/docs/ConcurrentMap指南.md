## 1. 概述

Map自然是Java集合中最广泛使用的结构之一。

而且，重要的是，[HashMap](https://www.baeldung.com/java-hashmap)不是线程安全的实现，而Hashtable确实通过同步操作来提供线程安全。

尽管Hashtable是线程安全的，但它的效率并不高。而另一个完全同步的Map，Collections.synchronizedMap也没有表现出很高的效率。如果我们想要在高并发下实现高吞吐量的线程安全，那么这些实现就不是我们应该考虑的路径。

为了解决这个问题，**Java集合框架在Java 1.5中引入了ConcurrentMap**。

以下讨论基于Java 1.8。

## 2. ConcurrentMap

ConcurrentMap是Map接口的扩展，它旨在为解决吞吐量与线程安全之间的协调问题提供一种结构和方向。

通过覆盖几个接口默认方法，ConcurrentMap为有效实现提供了指南，以提供线程安全和内存一致的原子操作。

多个默认实现被重写，禁用了空键/值支持：

+ getOrDefault
+ forEach
+ replaceAll
+ computeIfAbsent
+ computeIfPresent
+ compute
+ merge

以下API也被重写以支持原子性，但没有默认的接口实现：

+ putIfAbsent
+ remove
+ replace(key, oldValue, newValue)
+ replace(key, value)

其余操作是直接继承的，与Map基本一致。

## 3. ConcurrentHashMap

ConcurrentHashMap是开箱即用的ConcurrentMap实现。

为了获得更好的性能，它由一组节点组成，作为底层的表桶(在Java 8之前是表段)，并且在更新期间主要使用[CAS](https://en.wikipedia.org/wiki/Compare-and-swap)操作。

表存储桶在第一次插入时延迟初始化。每个存储桶都可以通过锁定存储桶中的第一个节点来独立锁定。读取操作不会阻塞，并且更新争用被最小化。

所需的段数与访问表的线程数有关，因此大多数情况下每个段正在进行的更新不会超过一次。

**在Java 8之前，所需的“段”数与访问表的线程数有关，因此大多数时候每个段正在进行的更新不会超过一次**。

这就是为什么与HashMap相比，它的构造函数提供了额外的concurrencyLevel参数来控制要使用的估计线程数：

```java
public ConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel)
```

其他两个参数：initialCapacity和loadFactor的工作原理与[HashMap](https://www.baeldung.com/java-hashmap)完全相同。

**但是，从Java 8开始，构造函数的存在只是为了向后兼容：参数只能影响Map的初始大小**。

### 3.1 线程安全

ConcurrentMap保证了多线程环境中键/值操作的内存一致性。

在将对象作为键或值放入ConcurrentMap之前，线程中的操作发生在另一个线程中访问或删除该对象之后的操作之前。

为了确认这一点，让我们看一个内存不一致的情况：

```java
@Test
void givenHashMap_whenSumParallel_thenError() throws Exception {
    Map<String, Integer> map = new HashMap<>();
    List<Integer> sumList = parallelSum100(map, 100);
    
    assertNotEquals(1, sumList
        .stream()
        .distinct()
        .count());
    
    long wrongResultCount = sumList
        .stream()
        .filter(num -> num != 100)
        .count();
    
    assertTrue(wrongResultCount > 0);
}

private List<Integer> parallelSum100(Map<String, Integer> map, int executionTimes) throws InterruptedException {
    List<Integer> sumList = new ArrayList<>(1000);
    for (int i = 0; i < executionTimes; i++) {
        map.put("test", 0);
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int j = 0; j < 10; j++) {
            executorService.execute(() -> {
                for (int k = 0; k < 10; k++)
                    map.computeIfPresent("test", (key, value) -> value + 1);
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        sumList.add(map.get("test"));
    }
    return sumList;
}
```

对于并行执行的每个map.computeIfPresent操作，HashMap不提供关于当前整数值应该是什么的一致视图，从而导致不一致和不期望的结果。

至于ConcurrentHashMap，我们可以得到一致且正确的结果：

```java
@Test
void givenConcurrentMap_whenSumParallel_thenCorrect() throws Exception {
    Map<String, Integer> map = new ConcurrentHashMap<>();
    List<Integer> sumList = parallelSum100(map, 1000);
    
    assertEquals(1, sumList
        .stream()
        .distinct()
        .count());
    
    long wrongResultCount = sumList
        .stream()
        .filter(num -> num != 100)
        .count();
    
    assertEquals(0, wrongResultCount);
}
```

### 3.2 空键/值

ConcurrentMap提供的大多数API都不允许空键或空值，例如：

```java
private ConcurrentMap<String, Object> concurrentMap;

@BeforeEach
void setup() {
    concurrentMap = new ConcurrentHashMap<>();
}

@Test
void givenConcurrentHashMap_whenPutWithNullKey_thenThrowsNPE() {
    assertThrows(NullPointerException.class, () -> concurrentMap.put(null, new Object()));
}

@Test
void givenConcurrentHashMap_whenPutNullValue_thenThrowsNPE() {
    assertThrows(NullPointerException.class, () -> concurrentMap.put("test", null));
}
```

但是，**对于compute*和merge操作，计算值可以为null，这表示键值映射如果存在则被删除，或者如果之前不存在则保持不存在**。

```java
@Test
void givenKeyPresent_whenComputeIfPresentRemappingNull_thenMappingRemoved() {
    Object oldValue = new Object();
    concurrentMap.put("test", oldValue);
    concurrentMap.computeIfPresent("test", (s, o) -> null);
    
    assertNull(concurrentMap.get("test"));
}
```

### 3.3 Stream支持

Java 8也在ConcurrentHashMap中也提供了Stream支持。

与大多数流方法不同，批量(顺序和并行)操作允许安全地进行并发修改，不会抛出ConcurrentModificationException，这也适用于它的迭代器。与流相关，还添加了几个forEach\*、search和reduce\*方法，以支持更丰富的遍历和map-reduce操作。

### 3.4 性能

**在底层，ConcurrentHashMap有点类似于HashMap**，数据访问和更新基于哈希表(虽然更复杂)。

当然，在大多数并发情况下，ConcurrentHashMap在数据检索和更新方面应该会产生更好的性能。

让我们为get和put性能编写一个快速的微基准测试，并将其与Hashtable和Collections.synchronizedMap进行比较，在4个线程中运行这两种操作50万次。

```java
@Test
void givenMaps_whenGetPut500KTimes_thenConcurrentMapFaster() throws Exception {
    final Map<String, Object> hashtable = new Hashtable<>();
    final Map<String, Object> synchronizedHashMap = Collections.synchronizedMap(new HashMap<>());
    final Map<String, Object> concurrentHashMap = new ConcurrentHashMap<>();

    final long hashtableAvgRuntime = timeElapseForGetPut(hashtable);
    final long syncHashMapAvgRuntime = timeElapseForGetPut(synchronizedHashMap);
    final long concurrentHashMapAvgRuntime = timeElapseForGetPut(concurrentHashMap);

    System.out.printf("Hashtable: %s, syncHashMap: %s, ConcurrentHashMap: %s%n", hashtableAvgRuntime, syncHashMapAvgRuntime, concurrentHashMapAvgRuntime);

    assertTrue(hashtableAvgRuntime > concurrentHashMapAvgRuntime);
    assertTrue(syncHashMapAvgRuntime > concurrentHashMapAvgRuntime);
}

private long timeElapseForGetPut(Map<String, Object> map) throws InterruptedException {
    final ExecutorService executorService = Executors.newFixedThreadPool(4);
    final long startTime = System.nanoTime();
    for (int i = 0; i < 4; i++) {
        executorService.execute(() -> {
            for (int j = 0; j < 500_000; j++) {
                final int value = ThreadLocalRandom.current().nextInt(10000);
                final String key = String.valueOf(value);
                map.put(key, value);
                map.get(key);
            }
        });
    }
    executorService.shutdown();
    executorService.awaitTermination(1, TimeUnit.MINUTES);
    return (System.nanoTime() - startTime) / 500_000;
}
```

请记住，微基准测试只关注单个场景，并不总是能很好地反映真实情况的性能。

也就是说，在具有平均开发系统的OS X系统上，我们看到100次连续运行的平均样本结果(以纳秒为单位)：

```shell
Hashtable: 786, syncHashMap: 887, ConcurrentHashMap: 196
```

在多线程环境中，当期望多个线程访问一个公共Map，ConcurrentHashMap显然更可取。

但是，当Map只能由单个线程访问时，HashMap因其简单性和可靠的性能而成为更好的选择。

### 3.5 陷阱

检索操作通常不会在ConcurrentHashMap中阻塞，并且可能与更新操作重叠。因此，为了获得更好的性能，它们只反映最近完成的更新操作的结果，如[官方Javadoc](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html)中所述。

还有其他几个事实需要牢记：

+ 聚合状态方法(包括size、isEmpty和containsValue)的结果通常仅在Map未在其他线程中进行并发更新时才有用：

  ```java
  private ExecutorService executorService;
  private Map<String, Integer> concurrentMap;
  private List<Integer> mapSizes;
  private final int MAX_SIZE = 100000;
  
  @BeforeEach
  void init() {
      executorService = Executors.newFixedThreadPool(2);
      concurrentMap = new ConcurrentHashMap<>();
      mapSizes = new ArrayList<>(MAX_SIZE);
  }
  
  @Test
  void givenConcurrentMap_whenUpdatingAndGetSize_thenError() throws InterruptedException {
      Runnable collectMapSizes = () -> {
          for (int i = 0; i < MAX_SIZE; i++) {
              mapSizes.add(concurrentMap.size());
          }
      };
      Runnable updateMapData = () -> {
          for (int i = 0; i < MAX_SIZE; i++) {
              concurrentMap.put(String.valueOf(i), i);
          }
      };
      executorService.execute(updateMapData);
      executorService.execute(collectMapSizes);
      executorService.shutdown();
      executorService.awaitTermination(1, TimeUnit.MINUTES);
  
      assertNotEquals(MAX_SIZE, mapSizes.get(MAX_SIZE - 1).intValue(), "map size collected with concurrent updates not reliable");
      assertEquals(MAX_SIZE, concurrentMap.size());
  }
  ```
  如果并发更新受到严格控制，聚合状态仍然是可靠的。
  
  **尽管这些聚合状态方法不能保证实时准确性，但它们可能足以用于监控或估计目的**。

  请注意，ConcurrentHashMap的size()的用法应该由mappingCount()代替，因为后一种方法返回一个long值，尽管在深层次来看它们是基于相同的估计。

+ **hashCode很重要**：请注意，使用许多具有完全相同的hashCode()的键肯定会降低任何哈希表的性能。

  为了改善键为Comparable时的影响，ConcurrentHashMap可以使用键之间的比较顺序来帮助打破平局。尽管如此，我们应该尽可能避免使用相同的hashCode()。
+ 迭代器仅设计为在单个线程中使用，因为它们提供弱一致性而不是快速失败遍历，并且它们永远不会抛出ConcurrentModificationException。
+ 默认初始表容量为16，并根据指定的并发级别进行调整：
  ```java
  public ConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
 
      //...
      if (initialCapacity < concurrencyLevel) {
          initialCapacity = concurrencyLevel;
      }
      //...
  }
  ```
+ 关于重新映射函数的注意事项：虽然我们可以使用提供的compute和merge*方法执行重映射操作，但我们应该保持它们快速、简短和简单，并专注于当前映射以避免意外阻塞。
+ ConcurrentHashMap中的键不是按排序顺序排列的，因此对于需要排序的情况，ConcurrentSkipListMap是一个合适的选择。

## 4. ConcurrentNavigableMap

对于需要对键进行排序的情况，我们可以使用ConcurrentSkipListMap，它是TreeMap的并发版本。

作为ConcurrentMap的补充，ConcurrentNavigableMap支持对它的键进行全排序(默认为升序)，并且可以并发导航。为了并发兼容性，将重写返回map视图的方法：

+ subMap
+ headMap
+ tailMap
+ subMap
+ headMap
+ tailMap

keySet()视图的迭代器和拆分器通过弱内存一致性得到增强：

+ navigableKeySet
+ keySet
+ descendingKeySet

## 5. ConcurrentSkipListMap

之前，我们介绍了NavigableMap接口及其实现[TreeMap](https://www.baeldung.com/java-treemap)。ConcurrentSkipListMap可以看作是一个可扩展的并发版本的TreeMap。

实际上，Java中并没有红黑树的并发实现。[SkipLists](https://en.wikipedia.org/wiki/Skip_list)的并发变体在ConcurrentSkipListMap中实现，为containsKey、get、put和remove操作及其重载提供了预期的平均log(n)时间成本。

除了TreeMap的功能外，键的插入、删除、更新和访问操作都保证了线程安全。以下是并发导航时与TreeMap的比较：

```java
@Test
void givenSkipListMap_whenNavConcurrently_thenCountCorrect() throws InterruptedException {
    NavigableMap<Integer, Integer> skipListMap = new ConcurrentSkipListMap<>();
    int count = countMapElementByPollingFirstEntry(skipListMap);
    
    assertEquals(10000 * 4, count);
}

@Test
void givenTreeMap_whenNavConcurrently_thenCountError() throws InterruptedException {
    NavigableMap<Integer, Integer> treeMap = new TreeMap<>();
    int count = countMapElementByPollingFirstEntry(treeMap);
    
    assertNotEquals(10000 * 4, count);
}

private int countMapElementByPollingFirstEntry(NavigableMap<Integer, Integer> navigableMap) throws InterruptedException {
    for (int i = 0; i < 10000 * 4; i++) {
        navigableMap.put(i, i);
    }
    AtomicInteger counter = new AtomicInteger(0);
    ExecutorService executorService = Executors.newFixedThreadPool(4);
    for (int j = 0; j < 4; j++) {
        executorService.execute(() -> {
            for (int i = 0; i < 10000; i++) {
                if (navigableMap.pollFirstEntry() != null) {
                    counter.incrementAndGet();
                }
            }
        });
    }
    executorService.shutdown();
    executorService.awaitTermination(1, TimeUnit.MINUTES);
    return counter.get();
}
```

对幕后性能问题的完整解释超出了本文的范围。详细信息可以在ConcurrentSkipListMap的Javadoc中找到，该文档位于src.zip文件中的 java/util/concurrent下。

## 6. 总结

在本文中，我们主要介绍了ConcurrentMap接口和ConcurrentHashMap的特性，并介绍了ConcurrentNavigableMap需要按键排序。