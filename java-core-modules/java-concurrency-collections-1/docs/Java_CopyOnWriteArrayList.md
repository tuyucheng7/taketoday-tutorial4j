## 1. 概述

在这篇文章中，我们介绍java.util.concurrent包中的CopyOnWriteArrayList。

在多线程程序中，这是一个非常有用的构造，当我们想要以线程安全的方式迭代列表而不需要显式同步时。

## 2. CopyOnWriteArrayList API

CopyOnWriteArrayList的设计使用了一种巧妙的技术来使其线程安全，而无需同步。当我们使用任何修改方法(例如add()或remove())时，CopyOnWriteArrayList的全部内容都会复制到新的内部副本中。因此，我们也可以以安全的方式遍历列表，即使发生并发修改。

当我们在CopyOnWriteArrayList上调用iterator()方法时，我们会得到一个由CopyOnWriteArrayList内容的不可变快照备份的迭代器。

它的内容是创建迭代器时ArrayList中数据的精确副本。即使在此期间，其他线程在集合中添加或删除了一个元素，该修改也会生成新的数据副本，这个副本将用于从该集合中进行任何进一步的数据查找。

这种数据结构的特点使得它在我们迭代它比修改它更频繁的情况下特别有用。如果在我们的场景中添加元素是一种常见的操作，那么CopyOnWriteArrayList不是一个好的选择，因为额外的副本拷贝肯定会导致性能下降。

## 3. 插入时迭代CopyOnWriteArrayList

假设我们创建了一个存储整数的CopyOnWriteArrayList实例：

```java
final CopyOnWriteArrayList<Integer> numbers = new CopyOnWriteArrayList<>(new Integer[]{1, 3, 5, 8});
```

接下来，我们要遍历该数组，因此我们从中获取一个Iterator实例：

```java
Iterator<Integer> iterator = numbers.iterator();
```

获取迭代器后，我们向numbers集合中添加一个新元素：

```java
numbers.add(10);
```

记住，当我们为CopyOnWriteArrayList创建迭代器时，我们会在调用iterator()时获得集合中数据的不可变快照。

因此，在遍历第一次获取到的迭代器时，结果中不应该包含数字10：

```java
List<Integer> result = new LinkedList<>();
iterator.forEachRemaining(result::add);
assertThat(result).containsOnly(1, 3, 5, 8);
```

当我们使用新创建的迭代器时进行遍历时，我们的后续遍历可以返回添加的数字10：

```java
Iterator<Integer> iterator2 = numbers.iterator();
List<Integer> result2 = new LinkedList<>();
iterator2.forEachRemaining(result2::add);

assertThat(result2).containsOnly(1, 3, 5, 8, 10);
```

## 4. 不允许在迭代时删除

创建CopyOnWriteArrayList是为了允许即使在底层集合被修改时也可以安全地迭代元素。

由于复制机制的原因，不允许对返回的Iterator执行remove()操作，这会导致UnsupportedOperationException：

```java
public class CopyOnWriteArrayListUnitTest {

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
}
```

## 5. 总结

在这个教程中，我们介绍了java.util.concurrent包中的CopyOnWriteArrayList实现。我们阐明了这个集合的实现语义，以及它如何以线程安全的方式对其进行迭代，而其他线程可以继续从中插入或删除元素。