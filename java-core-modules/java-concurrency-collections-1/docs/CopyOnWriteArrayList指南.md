## 1. 概述

在这篇简短的文章中，我们将研究java.util.concurrent包中的[CopyOnWriteArrayList](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/CopyOnWriteArrayList.html)。

这在多线程程序中是一个非常有用的结构-当我们想要在没有显式同步的情况下以线程安全的方式迭代列表时。

## 2. CopyOnWriteArrayList API

CopyOnWriteArrayList的设计使用了一种巧妙的技术使其在不需要同步的情况下是线程安全的。当我们使用任何修改方法(例如add()或remove())时，CopyOnWriteArrayList的全部内容都会复制到新的内部副本中。

由于这个简单的事实，**我们可以以安全的方式遍历列表，即使发生并发修改也是如此**。

当我们在CopyOnWriteArrayList上调用iterator()方法时，我们会得到一个由CopyOnWriteArrayList内容的不可变快照备份的迭代器。

它的内容是创建迭代器时ArrayList中数据的精确副本。即使在此期间，其他线程在列表中添加或删除元素，该修改也会制作数据的新副本，这个副本将用于从该列表中进行任何进一步的数据查找。

这种数据结构的特性使得它在我们迭代它比修改它更频繁的情况下特别有用。如果在我们的场景中添加元素是一种常见的操作，那么CopyOnWriteArrayList将不是一个好的选择-因为额外的副本拷贝肯定会导致性能下降。

## 3. 插入时遍历CopyOnWriteArrayList

假设我们正在创建一个存储整数的CopyOnWriteArrayList实例：

```java
final CopyOnWriteArrayList<Integer> numbers = new CopyOnWriteArrayList<>(new Integer[]{1, 3, 5, 8});
```

接下来，我们要遍历该数组，因此我们从中获取一个Iterator实例：

```java
Iterator<Integer> iterator = numbers.iterator();
```

获取迭代器后，我们向numbers列表中添加一个新元素：

```java
numbers.add(10);
```

请记住，当我们为CopyOnWriteArrayList创建迭代器时，我们会在调用iterator()时获得列表中数据的不可变快照。

因此，在对其进行遍历时，我们不会在遍历中看到数字10：

```java
List<Integer> result = new LinkedList<>();
iterator.forEachRemaining(result::add);
assertThat(result).containsOnly(1, 3, 5, 8);
```

当我们使用新创建的迭代器时进行遍历时，我们的后续遍历也将返回添加的数字10：

```java
Iterator<Integer> iterator2 = numbers.iterator();
List<Integer> result2 = new LinkedList<>();
iterator2.forEachRemaining(result2::add);

assertThat(result2).containsOnly(1, 3, 5, 8, 10);
```

## 4. 不允许在迭代时删除

创建CopyOnWriteArrayList是为了允许即使在底层列表被修改时也可以安全地迭代元素。

由于复制机制的原因，不允许对返回的Iterator执行remove()操作-这会导致UnsupportedOperationException：

```java
@Test(expected = UnsupportedOperationException.class)
public void givenCopyOnWriteList_whenIterateOverItAndTryToRemoveElement_thenShouldThrowException() {
    // given
    final CopyOnWriteArrayList<Integer> numbers = new CopyOnWriteArrayList<>(new Integer[]{1, 3, 5, 8});

    // when
    Iterator<Integer> iterator = numbers.iterator();
    while (iterator.hasNext()) {
        iterator.remove();
    }
}
```

## 5. 总结

在本快速教程中，我们了解了java.util.concurrent包中的CopyOnWriteArrayList实现。

我们看到了这个列表的实现语义以及它如何以线程安全的方式迭代，而其他线程可以继续在其中插入或删除元素。