## 1. 概述

[LinkedList是](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/LinkedList.html)[List](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/List.html)和[Deque](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Deque.html)接口的双向链表实现它实现所有可选列表操作并允许所有元素(包括null)。

## 2.特点

你可以在下面找到LinkedList最重要的属性：

-   索引到列表中的操作将从开头或结尾遍历列表，以更接近指定索引者为准
-   它不[同步](https://stackoverflow.com/a/1085745/2486904)
-   它的[Iterator](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Iterator.html)和[ListIterator](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ListIterator.html)迭代器是[fail-fast](https://stackoverflow.com/questions/17377407/what-is-fail-safe-fail-fast-iterators-in-java-how-they-are-implemented)(这意味着在迭代器创建后，如果列表被修改，将抛出[ConcurrentModificationException )](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ConcurrentModificationException.html)
-   每个元素都是一个节点，它保留对下一个和上一个元素的引用
-   它维护插入顺序

虽然LinkedList不是同步的，但我们可以通过调用[Collections.synchronizedList](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collections.html#synchronizedList(java.util.List))方法来检索它的同步版本，例如：

```java
List list = Collections.synchronizedList(new LinkedList(...));
```

## 3.与ArrayList的比较

尽管它们都实现了List接口，但它们具有不同的语义——这肯定会影响使用哪一个的决定。

### 3.1. 结构

ArrayList是由Array支持的基于索引的数据结构。它提供对其元素的随机访问，性能等于 O(1)。

另一方面，LinkedList将其数据存储为元素列表，每个元素都链接到其上一个和下一个元素。在这种情况下，项目的搜索操作的执行时间等于 O(n)。

### 3.2. 操作

项的插入、添加和删除操作在LinkedList中更快，因为当将元素添加到集合中的任意位置时无需调整数组大小或更新索引，只有周围元素中的引用会发生变化。

### 3.3. 内存使用情况

LinkedList比ArrayList消耗更多的内存，因为LinkedList中的每个节点都存储两个引用，一个用于其前一个元素，一个用于其下一个元素，而ArrayList仅包含数据及其索引。

## 4.用法

以下是一些代码示例，展示了如何使用LinkedList：

### 4.1. 创建

```java
LinkedList<Object> linkedList = new LinkedList<>();
```

### 4.2. 添加元素

LinkedList实现了List和Deque接口，除了标准的add()和addAll()方法之外，你还可以找到addFirst()和addLast()，它们分别在开头或结尾添加一个元素。

### 4.3. 删除元素

与元素添加类似，此列表实现提供了removeFirst()和removeLast()。


此外，还有方便的方法removeFirstOccurence()和removeLastOccurence()返回布尔值(如果集合包含指定元素则为真)。

### 4.4. 队列操作

Deque接口提供类似队列的行为(实际上Deque扩展了Queue接口)：

```java
linkedList.poll();
linkedList.pop();
```

这些方法检索第一个元素并将其从列表中删除。

poll()和pop()之间的区别在于pop将在空列表上抛出NoSuchElementException()，而poll返回 null。API pollFirst()和pollLast()也可用。

例如，推送API 的工作原理如下：

```java
linkedList.push(Object o);
```

它将元素作为集合的头部插入。

LinkedList还有许多其他方法，已经使用过Lists的用户应该熟悉其中的大部分方法。Deque提供的其他方法可能是“标准”方法的便捷替代方法。

完整的文档可以在[这里](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/LinkedList.html)找到。

## 5.总结

ArrayList通常是默认的List实现。

但是，在某些用例中，使用LinkedList会更合适，例如对恒定插入/删除时间(例如，频繁插入/删除/更新)、恒定访问时间和有效内存使用的偏好。