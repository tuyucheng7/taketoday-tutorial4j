## 1. 概述

在本文中，我们将深入探讨HashSet。它是最流行的Set实现之一，也是Java集合框架的组成部分。

## 2. HashSet介绍

HashSet是JavaCollections API 中的基本数据结构之一。

让我们回顾一下这个实现最重要的方面：

-   它存储唯一元素并允许空值
-   它由HashMap支持
-   它不维护插入顺序
-   它不是线程安全的

请注意，当创建HashSet的实例时，会初始化此内部HashMap：

```java
public HashSet() {
    map = new HashMap<>();
}
```

如果你想更深入地了解HashMap的工作原理，可以[在此处阅读重点介绍的文章](https://www.baeldung.com/java-hashmap)。

## 3. API

在本节中，我们将回顾最常用的方法并查看一些简单的示例。

### 3.1. 添加()

add()方法可用于将元素添加到集合中。方法契约声明只有当元素不存在于集合中时才会添加该元素。如果添加了一个元素，该方法返回true，否则返回false。

我们可以向HashSet添加一个元素，例如：

```java
@Test
public void whenAddingElement_shouldAddElement() {
    Set<String> hashset = new HashSet<>();
 
    assertTrue(hashset.add("String Added"));
}
```

从实现的角度来看，add方法是一个极其重要的方法。实现细节说明了HashSet如何在内部工作并利用HashMap 的 put方法：

```java
public boolean add(E e) {
    return map.put(e, PRESENT) == null;
}
```

map变量是对内部支持HashMap的引用：

```java
private transient HashMap<E, Object> map;
```

最好先熟悉[散列](https://www.baeldung.com/java-hashcode)码，以便详细了解元素在基于散列的数据结构中的组织方式。

总结：

-   HashMap是一个桶数组，默认容量为 16 个元素——每个桶对应一个不同的 hashcode 值
-   如果各种对象具有相同的哈希码值，它们将存储在一个桶中
-   如果达到负载因子，则会创建一个新数组，其大小是前一个数组的两倍，并且所有元素都会重新散列并重新分配到新的相应存储桶中
-   为了检索一个值，我们散列一个键，对其进行修改，然后转到相应的存储桶并在可能存在多个对象的情况下搜索潜在的链表

### 3.2. 包含()

contains方法的目的是检查一个元素是否存在于给定的HashSet中。如果找到元素，则返回true，否则返回false。

我们可以检查HashSet中的元素：

```java
@Test
public void whenCheckingForElement_shouldSearchForElement() {
    Set<String> hashsetContains = new HashSet<>();
    hashsetContains.add("String Added");
 
    assertTrue(hashsetContains.contains("String Added"));
}
```

每当将对象传递给此方法时，都会计算哈希值。然后，解析并遍历相应的桶位置。

### 3.3. 去掉()

该方法从集合中删除指定的元素(如果存在)。如果集合包含指定元素，则此方法返回true 。

让我们看一个工作示例：

```java
@Test
public void whenRemovingElement_shouldRemoveElement() {
    Set<String> removeFromHashSet = new HashSet<>();
    removeFromHashSet.add("String Added");
 
    assertTrue(removeFromHashSet.remove("String Added"));
}
```

### 3.4. 清除()

当我们打算从集合中删除所有项目时，我们使用此方法。底层实现只是简单地清除底层HashMap 中的所有元素。

让我们看看实际效果：

```java
@Test
public void whenClearingHashSet_shouldClearHashSet() {
    Set<String> clearHashSet = new HashSet<>();
    clearHashSet.add("String Added");
    clearHashSet.clear();
    
    assertTrue(clearHashSet.isEmpty());
}
```

### 3.5. 尺寸()

这是 API 中的基本方法之一。它被大量使用，因为它有助于识别HashSet中存在的元素数量。底层实现只是将计算委托给HashMap 的 size()方法。

让我们看看实际效果：

```java
@Test
public void whenCheckingTheSizeOfHashSet_shouldReturnThesize() {
    Set<String> hashSetSize = new HashSet<>();
    hashSetSize.add("String Added");
    
    assertEquals(1, hashSetSize.size());
}
```

### 3.6. 是空的()

我们可以使用此方法来判断给定的HashSet实例是否为空。如果集合不包含任何元素，则此方法返回true ：

```java
@Test
public void whenCheckingForEmptyHashSet_shouldCheckForEmpty() {
    Set<String> emptyHashSet = new HashSet<>();
    
    assertTrue(emptyHashSet.isEmpty());
}
```

### 3.7. 迭代器()

该方法返回Set中元素的迭代器。元素的访问没有特定顺序，迭代器是 fail-fast 的。

我们可以在这里观察随机迭代顺序：

```java
@Test
public void whenIteratingHashSet_shouldIterateHashSet() {
    Set<String> hashset = new HashSet<>();
    hashset.add("First");
    hashset.add("Second");
    hashset.add("Third");
    Iterator<String> itr = hashset.iterator();
    while(itr.hasNext()){
        System.out.println(itr.next());
    }
}
```

如果在创建迭代器后以任何方式修改集合，除了通过迭代器自己的 remove 方法，迭代器将抛出ConcurrentModificationException。

让我们看看实际效果：

```java
@Test(expected = ConcurrentModificationException.class)
public void whenModifyingHashSetWhileIterating_shouldThrowException() {
 
    Set<String> hashset = new HashSet<>();
    hashset.add("First");
    hashset.add("Second");
    hashset.add("Third");
    Iterator<String> itr = hashset.iterator();
    while (itr.hasNext()) {
        itr.next();
        hashset.remove("Second");
    }
}

```

或者，如果我们使用迭代器的 remove 方法，那么我们就不会遇到异常：

```java
@Test
public void whenRemovingElementUsingIterator_shouldRemoveElement() {
 
    Set<String> hashset = new HashSet<>();
    hashset.add("First");
    hashset.add("Second");
    hashset.add("Third");
    Iterator<String> itr = hashset.iterator();
    while (itr.hasNext()) {
        String element = itr.next();
        if (element.equals("Second"))
            itr.remove();
    }
 
    assertEquals(2, hashset.size());
}
```

无法保证迭代器的快速失败行为，因为在存在非同步并发修改的情况下不可能做出任何硬性保证。

快速失败迭代器会尽最大努力抛出ConcurrentModificationException。因此，编写依赖于此异常的正确性的程序是错误的。

## 4 、 HashSet如何保持唯一性？

当我们将对象放入HashSet时，它会使用对象的哈希码值来确定元素是否已不在集合中。

每个哈希码值对应于某个桶位置，桶位置可以包含各种元素，计算出的哈希值是相同的。但是具有相同hashCode的两个对象可能不相等。

因此，将使用equals()方法比较同一桶中的对象。

## 5. HashSet的性能

HashSet的性能主要受两个参数的影响——初始容量和负载因子。

将元素添加到集合的预期时间复杂度为O(1) ，在最坏的情况下(只有一个桶存在)可能会下降到O(n ) - 因此，保持正确的 HashSet容量至关重要。

重要说明：自 JDK 8 以来，[最坏情况下的时间复杂度为O(logn)。](https://openjdk.java.net/jeps/180)

负载因子描述什么是最大填充水平，高于该水平，将需要调整集合的大小。

我们还可以创建一个具有初始容量和负载因子自定义值的HashSet：

```java
Set<String> hashset = new HashSet<>();
Set<String> hashset = new HashSet<>(20);
Set<String> hashset = new HashSet<>(20, 0.5f);

```

在第一种情况下，使用默认值——初始容量 16 和负载因子 0.75。在第二个中，我们覆盖默认容量，在第三个中，我们覆盖两者。

较低的初始容量会降低空间复杂度，但会增加重新散列的频率，这是一个昂贵的过程。

另一方面，高初始容量会增加迭代成本和初始内存消耗。


根据经验：

-   高初始容量有利于大量条目加上很少或没有迭代
-   较低的初始容量适用于迭代次数较多的少量条目

因此，在两者之间取得正确的平衡非常重要。通常，默认实现经过优化并且工作正常，如果我们觉得需要调整这些参数以满足要求，我们需要谨慎行事。

## 六，总结

在本文中，我们概述了HashSet的实用程序、它的目的以及它的基本工作原理。鉴于其恒定的时间性能和避免重复的能力，我们看到了它在可用性方面的效率。

我们研究了 API 中的一些重要方法，它们如何帮助我们作为开发人员发挥HashSet的潜力。