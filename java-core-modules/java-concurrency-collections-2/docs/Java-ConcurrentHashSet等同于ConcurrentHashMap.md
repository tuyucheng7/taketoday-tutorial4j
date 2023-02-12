## 1. 概述

在本教程中，我们将了解创建线程安全的HashSet实例的可能性有哪些，以及HashSet的ConcurrentHashMap的等价物是什么。此外，我们将研究每种方法的优点和缺点。

## 2. 使用ConcurrentHashMap工厂方法的线程安全HashSet

首先，我们看一下[ConcurrentHashMap](https://www.baeldung.com/java-concurrent-map)的静态方法newKeySet()。基本上，此方法返回一个遵循java.util.Set接口的实例，并允许使用标准方法，如add()、contains()等。

```java
Set<Integer> threadSafeUniqueNumbers = ConcurrentHashMap.newKeySet();
threadSafeUniqueNumbers.add(23);
threadSafeUniqueNumbers.add(45);
```

**此外，返回的Set的性能类似于[HashSet](https://www.baeldung.com/java-hashset)，因为两者都是使用基于哈希的算法实现的**。此外，由于实现使用的ConcurrentHashMap，同步逻辑带来的开销也是最小的。

最后，缺点是该方法**仅从Java 8开始存在**。

## 3. 使用ConcurrentHashMap实例方法的线程安全HashSet

到目前为止，我们已经了解了ConcurrentHashMap的静态方法。接下来，我们将处理可用于ConcurrentHashMap的实例方法，以创建线程安全的Set实例。有两种可用的方法，newKeySet()和newKeySet(defaultValue)，它们彼此略有不同。

**这两个方法都会创建一个与原始Map链接的集合**。换句话说，每次我们向原始ConcurrentHashMap添加新条目时，Set都会收到该值。此外，让我们看看这两种方法之间的差异。

### 3.1 newKeySet()方法

如上所述，newKeySet()返回一个包含原始Map所有键的Set。此方法与newKeySet(defaultValue)之间的主要区别在于，当前方法不支持向Set添加新元素。因此，**如果我们尝试调用add()或addAll()之类的方法，我们将得到UnsupportedOperationException**。

尽管像remove(object)或clear()这样的操作可以按预期工作，但我们需要注意，Set上的任何更改都将反映在原始Map中：

```java
@Test
void whenCreateConcurrentHashSetWithKeySetMethod_thenSetIsSyncedWithMapped() {
    // when
    ConcurrentHashMap<Integer, String> numbersMap = new ConcurrentHashMap<>();
    Set<Integer> numbersSet = numbersMap.keySet();

    numbersMap.put(1, "One");
    numbersMap.put(2, "Two");
    numbersMap.put(3, "Three");

    System.out.println("Map before remove: " + numbersMap);
    System.out.println("Set before remove: " + numbersSet);

    numbersSet.remove(2);

    System.out.println("Set after remove: " + numbersSet);
    System.out.println("Map after remove: " + numbersMap);

    // then
    assertNull(numbersMap.get(2));
}
```

以下是上面代码的输出：

```shell
Map before remove: {1=One, 2=Two, 3=Three}
Set before remove: [1, 2, 3]
Set after remove: [1, 3]
Map after remove: {1=One, 3=Three}
```

### 3.2 newKeySet(defaultValue)方法

**与上面提到的方法相比，newKeySet(defaultValue)返回一个Set实例，该实例支持通过调用Set上的add()或addAll()来添加新元素**。

注意作为参数传递的defaultValue，它被用作Map中通过add()或addAll()方法添加的每个新条目的值。以下示例显示了其工作原理：

```java
@Test
void whenCreateConcurrentHashSetWithKeySetMethodDefaultValue_thenSetIsSyncedWithMapped() {
    // when
    ConcurrentHashMap<Integer, String> numbersMap = new ConcurrentHashMap<>();
    Set<Integer> numbersSet = numbersMap.keySet("SET-ENTRY");

    numbersMap.put(1, "One");
    numbersMap.put(2, "Two");
    numbersMap.put(3, "Three");

    System.out.println("Map before add: " + numbersMap);
    System.out.println("Set before add: " + numbersSet);

    numbersSet.addAll(asList(4, 5));

    System.out.println("Map after add: " + numbersMap);
    System.out.println("Set after add: " + numbersSet);

    // then
    assertEquals("One", numbersMap.get(1));
    assertEquals("SET-ENTRY", numbersMap.get(4));
    assertEquals("SET-ENTRY", numbersMap.get(5));
}
```

以下是上面测试的输出：

```shell
Map before add: {1=One, 2=Two, 3=Three}
Set before add: [1, 2, 3]
Map after add: {1=One, 2=Two, 3=Three, 4=SET-ENTRY, 5=SET-ENTRY}
Set after add: [1, 2, 3, 4, 5]
```

## 4. 使用Collections工具类的线程安全HashSet

让我们使用java.util.Collections中可用的synchronizedSet()方法来创建一个线程安全的HashSet实例：

```java
Set<Integer> syncNumbers = Collections.synchronizedSet(new HashSet<>());
syncNumbers.add(1);
```

**在使用这种方法之前，我们需要意识到它的效率低于上面介绍的方法**。基本上，与实现低级并发机制的ConcurrentHashMap相比，synchronizedSet()只是将Set实例包装到同步装饰器中。

## 5. 使用CopyOnWriteArraySet的线程安全Set

创建线程安全Set实现的最后一种方法是[CopyOnWriteArraySet](https://www.baeldung.com/java-copy-on-write-arraylist)。创建此Set的实例很简单：

```java
Set<Integer> copyOnArraySet = new CopyOnWriteArraySet<>();
copyOnArraySet.add(1);
```

尽管使用这个类看起来很有吸引力，但我们需要考虑一些严重的性能缺陷。在幕后，CopyOnWriteArraySet使用Array而不是HashMap来存储数据。**这意味着像contains()或remove()这样的操作具有O(n)复杂度，而当使用由ConcurrentHashMap支持的Set时，复杂度为O(1)**。

建议在Set大小通常保持较小且只读操作占多数时使用此实现。

## 6. 总结

在本文中，我们看到了创建线程安全Set实例的不同可能性，并强调了它们之间的区别。**首先我们看到了ConcurrentHashMap中的newKeySet()静态方法。当需要线程安全的HashSet实现时，这应该是首选**。之后我们查看了ConcurrentHashMap静态方法和实例方法newKeySet()、newKeySet(defaultValue)之间的区别。

最后，我们还讨论了Collections.synchronizedSet()和CopyOnWriteArraySet以及它们的性能缺陷。