## 1. 概述

在本文中，我们将探讨Java Collections API的[LinkedHashSet](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/LinkedHashSet.html)类。我们将深入研究此数据结构的特性并演示其功能。

## 2.LinkedHashSet介绍

LinkedHashSet是属于Java.util库的通用数据结构。[它是HashSet](https://www.baeldung.com/java-hashset)数据结构的直接后代，因此在每个给定时间都包含非重复元素。

除了具有贯穿其所有条目的双向链表之外，它的实现与HashSet的不同之处在于它保持可预测的迭代顺序。迭代顺序由元素插入集合的顺序定义。

LinkedHashSet使客户端免于HashSet提供的不可预测的排序，而不会招致[TreeSet](https://www.baeldung.com/java-tree-set)带来的复杂性。

虽然它的性能可能略低于HashSet，但由于维护链接列表的额外费用，它具有add()、contains()和remove()操作的恒定时间 (O1) 性能。

## 3.创建一个LinkedHashSet

有几个构造函数可用于创建LinkedHashSet。让我们来看看它们中的每一个：

### 3.1. 默认无参数构造函数

```java
Set<String> linkedHashSet = new LinkedHashSet<>();
assertTrue(linkedHashSet.isEmpty());
```

### 3.2. 使用初始容量创建

初始容量表示LinkedHashSet的初始长度。提供初始容量可防止Set在增长时不必要地调整其大小。默认初始容量为 16：

```java
LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>(20);
```

### 3.3. 从集合创建

我们还可以使用Collection的内容在创建时填充LinkedHashSet对象：

```java
@Test
 void whenCreatingLinkedHashSetWithExistingCollection_shouldContainAllElementOfCollection(){
      Collection<String> data = Arrays.asList("first", "second", "third", "fourth", "fifth");
      LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>(data);

      assertFalse(linkedHashSet.isEmpty());
      assertEquals(data.size(), linkedHashSet.size());
      assertTrue(linkedHashSet.containsAll(data) && data.containsAll(linkedHashSet));
 }
```

### 3.4. 使用初始容量和加载因子创建

当LinkedHashSet的大小增长到超过初始容量的值时，新容量是负载因子乘以之前的容量。在下面的代码片段中，初始容量设置为 20，负载因子为 3。

```java
LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>(20, 3);
```

默认加载因子为 0.75。

## 4. 向LinkedHashSet添加一个元素

我们可以分别使用add()和addAll()方法 将单个元素或元素集合添加到LinkedHashSet 。如果Set中尚不存在某个元素，则会添加该元素。当一个元素被添加到集合中时，这些方法返回true，否则返回false。

### 4.1. 添加单个元素

这是将元素添加到LinkedHashSet的实现：

```java
@Test
void whenAddingElement_shouldAddElement(){
    Set<Integer> linkedHashSet = new LinkedHashSet<>();
    assertTrue(linkedHashSet.add(0));
    assertFalse(linkedHashSet.add(0));
    assertTrue(linkedHashSet.contains(0));

}
```

### 4.2. 添加元素集合

如前所述，我们还可以将元素集合添加到LinkedHashSet：

```java
@Test
void whenAddingCollection_shouldAddAllContentOfCollection(){
    Collection<Integer> data = Arrays.asList(1,2,3);
    LinkedHashSet<Integer> linkedHashSet = new LinkedHashSet<>();

    assertTrue(linkedHashSet.addAll(data));
    assertTrue(data.containsAll(linkedHashSet) && linkedHashSet.containsAll(data));
 }
```

不添加重复项的规则也适用于addAll()方法，如下所示：

```java
@Test
void whenAddingCollectionWithDuplicateElements_shouldMaintainUniqueValuesInSet(){
    LinkedHashSet<Integer> linkedHashSet = new LinkedHashSet<>();
    linkedHashSet.add(2);
    Collection<Integer> data = Arrays.asList(1, 1, 2, 3);

    assertTrue(linkedHashSet.addAll(data));
    assertEquals(3, linkedHashSet.size());
    assertTrue(data.containsAll(linkedHashSet) && linkedHashSet.containsAll(data));
}
```

请注意，数据变量包含重复值 1，并且LinkedHashSet在调用addAll()方法之前已包含整数值 2 。

## 5. 遍历LinkedHashSet

与Collection库的所有其他后代一样，我们可以遍历LinkedHashSet。LinkedHashSet中有两种类型的迭代器可用：[Iterator](https://www.baeldung.com/java-iterator)和[Spliterator](https://www.baeldung.com/java-spliterator)。

前者只能对Collection进行遍历和执行任何基本操作，而后者将Collection拆分为子集，并在每个子集上并行执行不同的操作，从而使其成为线程安全的。

### 5.1. 用迭代器迭代

```java
@Test
void whenIteratingWithIterator_assertThatElementIsPresent(){
    LinkedHashSet<Integer> linkedHashSet = new LinkedHashSet<>();
    linkedHashSet.add(0);
    linkedHashSet.add(1);
    linkedHashSet.add(2);

    Iterator<Integer> iterator = linkedHashSet.iterator();
    for (int i = 0; i < linkedHashSet.size(); i++) {
        int nextData = iterator.next();
        assertEquals(i, nextData);
    }
}
```

### 5.2. 使用Spliterator进行迭代

```java
@Test
void whenIteratingWithSpliterator_assertThatElementIsPresent(){
    LinkedHashSet<Integer> linkedHashSet = new LinkedHashSet<>();
    linkedHashSet.add(0);
    linkedHashSet.add(1);
    linkedHashSet.add(2);

    Spliterator<Integer> spliterator = linkedHashSet.spliterator();
    AtomicInteger counter = new AtomicInteger();
    spliterator.forEachRemaining(data -> {
       assertEquals(counter.get(), (int)data);
       counter.getAndIncrement();
    });
}
```

## 6. 从LinkedHashSet中移除元素

以下是从LinkedHashSet中删除元素的不同方法：

### 6.1. 去掉()

如果我们知道要删除的确切元素，则此方法会从Set中删除一个元素。它接受一个参数，该参数是我们要删除的实际元素，如果成功删除则返回true，否则返回false：

```java
@Test
void whenRemovingAnElement_shouldRemoveElement(){
    Collection<String> data = Arrays.asList("first", "second", "third", "fourth", "fifth");
    LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>(data);

    assertTrue(linkedHashSet.remove("second"));
    assertFalse(linkedHashSet.contains("second"));
}
```

### 6.2. 移除如果()

removeIf ()方法删除满足指定谓词条件的元素。下面的示例删除了LinkedHashSet中大于 2 的所有元素：

```java
@Test
void whenRemovingAnElementGreaterThanTwo_shouldRemoveElement(){
    LinkedHashSet<Integer> linkedHashSet = new LinkedHashSet<>();
    linkedHashSet.add(0);
    linkedHashSet.add(1);
    linkedHashSet.add(2);
    linkedHashSet.add(3);
    linkedHashSet.add(4);

    linkedHashSet.removeIf(data -> data > 2);
    assertFalse(linkedHashSet.contains(3));
    assertFalse(linkedHashSet.contains(4));
}
```

### 6.3. 使用迭代器删除

迭代器也是我们可以用来从LinkedHashSet中删除元素的另一个选项。Iterator的remove()方法删除Iterator当前所在的元素：

```java
@Test
void whenRemovingAnElementWithIterator_shouldRemoveElement(){
    LinkedHashSet<Integer> linkedHashSet = new LinkedHashSet<>();
    linkedHashSet.add(0);
    linkedHashSet.add(1);
    linkedHashSet.add(2);

    Iterator<Integer> iterator = linkedHashSet.iterator();
    int elementToRemove = 1;
    assertTrue(linkedHashSet.contains(elementToRemove));
    while(iterator.hasNext()){
        if(elementToRemove == iterator.next()){
           iterator.remove();
       }
    }
    assertFalse(linkedHashSet.contains(elementToRemove));
}
```

## 七、总结

在本文中，我们研究了JavaCollections库中的LinkedHashSet数据结构。我们演示了如何通过不同的构造函数创建LinkedHashSet，添加和删除元素，以及遍历它。我们还了解了该数据结构的底层、它相对于HashSet的优势以及其常见操作的时间复杂度。