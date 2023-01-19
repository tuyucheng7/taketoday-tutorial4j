## 1. 概述

在本教程中，我们介绍创建线程安全的HashSet实例的可能性，以及什么是HashSet的ConcurrentHashMap的等价物。此外，我们将研究每种方法的优缺点。

## 2. 使用ConcurrentHashMap工厂方法的线程安全HashSet

首先，我们看一下ConcurrentHashMap的静态方法newKeySet()。基本上，此方法返回一个标准java.util.Set接口的实例，并允许使用标准方法，如add()、contains()等。

```java
Set<Integer> threadSafeUniqueNumbers = ConcurrentHashMap.newKeySet();
threadSafeUniqueNumbers.add(23);
threadSafeUniqueNumbers.add(45);
```

返回的Set的性能类似于HashSet，因为两者都是使用基于哈希算法实现的。此外，由于实现使用的ConcurrentHashMap，同步逻辑带来的开销也是最小的。

缺点是该方法仅从Java 8开始才可用。

## 3. 使用ConcurrentHashMap实例方法的线程安全HashSet

ConcurrentHashMap还包含两个实例方法来创建线程安全的Set实例。分别是newKeySet()和newKeySet(defaultValue)，它们彼此略有不同。

这两个方法都会创建一个与原始Map链接的集合。换句话说，每次我们向原始ConcurrentHashMap添加新Entry时，Set都会接收该值。接下来，让我们看看这两种方法之间的区别。

### 3.1 newKeySet()方法

如上所述，newKeySet()返回一个包含原始Map所有key的Set。此方法与newKeySet(defaultValue)的主要区别在于，该方法不支持向Set添加新元素。因此，如果我们尝试调用add()或addAll()之类的方法，我们将得到UnsupportedOperationException。

尽管remove(object)或clear()这样的操作可以按预期工作，但我们需要注意，Set上的任何更改都将反映在原始Map中：

```java
class ConcurrentHashSetUnitTest {

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
}
```

以下是上面测试的输出：

```text
Map before remove: {1=One, 2=Two, 3=Three}
Set before remove: [1, 2, 3]
Set after remove: [1, 3]
Map after remove: {1=One, 3=Three}
```

### 3.2 newKeySet(defaultValue)方法

与上面提到的方法相比，newKeySet(defaultValue)返回一个Set实例，该实例支持通过调用set上的add()或addAll()来添加新元素。

注意作为参数传递的defaultValue，它被用作Map中通过add()或addAll()方法添加的每个新Entry的value。以下测试显示了其工作原理：

```java
class ConcurrentHashSetUnitTest {

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
}
```

以下是上面测试的输出：

```text
Map before add: {1=One, 2=Two, 3=Three}
Set before add: [1, 2, 3]
Map after add: {1=One, 2=Two, 3=Three, 4=SET-ENTRY, 5=SET-ENTRY}
Set after add: [1, 2, 3, 4, 5]
```

## 4. 使用Collections工具类的线程安全HashSet

我们也可以使用java.util.Collections中的synchronizedSet()方法来创建一个线程安全的HashSet实例：

```java
Set<Integer> syncNumbers = Collections.synchronizedSet(new HashSet<>());
syncNumbers.add(1);
```

在使用这种方法之前，我们需要意识到它的效率低于上面介绍的方法。与实现低级并发机制的ConcurrentHashMap相比，synchronizedSet()基本上只是将Set实例包装到同步装饰器中。

## 5. 使用CopyOnWriteArraySet的线程安全Set

最后一种创建线程安全Set实现的方法是CopyOnWriteArraySet：

```java
Set<Integer> copyOnArraySet = new CopyOnWriteArraySet<>();
copyOnArraySet.add(1);
```

尽管使用这个类看起来很有吸引力，但我们需要考虑一些严重的性能缺陷。在幕后，CopyOnWriteArraySet使用数组而不是HashMap来存储数据。这意味着像contains()或remove()这样的操作时间复杂度为O(n)，而当使用由ConcurrentHashMap支持的Set时，复杂度为O(1)。

当Set大小通常保持较小且只读操作占多数时，建议使用此实现。

## 6. 总结

在本文中，我们介绍了创建线程安全Set实例的不同可能性，并讨论了它们之间的区别。首先我们介绍了ConcurrentHashMap中的newKeySet()静态方法。当需要线程安全的HashSet实现时，这应该是首选。之后我们比较了ConcurrentHashMap静态方法和实例方法newKeySet()、newKeySet(defaultValue)之间的区别。

最后，我们还介绍了Collections.synchronizedSet()和CopyOnWriteArraySet以及存在的性能缺陷。