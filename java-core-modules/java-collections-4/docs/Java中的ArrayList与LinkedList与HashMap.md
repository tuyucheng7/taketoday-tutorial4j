## 1. 概述

Java 中的集合基于几个核心接口和十几个实现类。不同实现的广泛选择有时会导致混淆。

决定将哪种集合类型用于特定用例并非易事。该决定会对我们的代码可读性和性能产生重大影响。

我们不会在一篇文章中解释所有类型的集合，而是解释三种最常见的集合：ArrayList、LinkedList和HashMap。在本教程中，我们将了解它们如何存储数据、性能以及何时使用它们的建议。

## 2.收藏品

集合只是一个将其他对象组合在一起的Java对象。[Java 集合框架](https://www.baeldung.com/java-collections)包含一组用于表示和操作集合的数据结构和算法。如果应用得当，所提供的数据结构有助于减少编程工作量并提高性能。

### 2.1. 接口

Java 集合框架包含四个基本接口：List、Set、Map 和Queue。在查看实现类之前了解这些接口的预期用途很重要。

让我们快速浏览一下我们将在本文中使用的四个核心接口中的三个：

-   List接口专用于存储有序的对象集合。它允许我们按位置访问和插入新元素，以及保存重复值
-   Map接口支持数据的键值对映射。要访问某个值，我们需要知道它的唯一键
-   Queue接口支持基于先进先出顺序存储数据。类似于现实世界中的队列

[HashMap ](https://www.baeldung.com/java-hashmap)实现了Map接口。List接口由ArrayList[和 ](https://www.baeldung.com/java-arraylist)LinkedList[实现](https://www.baeldung.com/java-linkedlist)。LinkedList还实现了 Queue接口。

### 2.2. 列表与地图

我们有时会遇到的一种常见反模式是试图使用地图来维持秩序。因此，不使用更适合该工作的其他集合类型。

仅仅因为我们可以用单一集合类型解决许多问题并不意味着我们应该这样做。

让我们看一个不好的例子，我们使用地图根据位置键保存数据：

```java
Map<Integer, String> map = new HashMap<>();
map.put(1, "Daniel");
map.put(2, "Marko");
for (String name : map.values()) {
    assertThat(name).isIn(map.values());
}
assertThat(map.values()).containsExactlyInAnyOrder("Daniel", "Marko");
```

当我们遍历映射值时，我们不能保证以我们放入它们的相同顺序检索它们。这仅仅是因为映射不是为维护元素的顺序而设计的。

我们可以使用列表以更具可读性的方式重写此示例。列表按定义排序，因此我们可以按照插入它们的相同顺序遍历项目：

```java
List<String> list = new ArrayList<>();
list.add("Daniel");
list.add("Marko");
for (String name : list) {
    assertThat(name).isIn(list);
}
assertThat(list).containsExactly("Daniel", "Marko");
```

地图专为基于唯一键的快速访问和搜索而设计。当我们想要维护顺序或使用基于位置的索引时，列表是一个自然的选择。

## 3.数组列表

ArrayList是Java中最常用的List接口的实现。它基于内置[数组](https://www.baeldung.com/java-arrays-guide)，但可以随着我们添加或删除元素而动态增长和收缩。

我们使用从零开始的索引来访问列表元素。我们可以在末尾或列表的特定位置插入一个新元素：

```java
List<String> list = new ArrayList<>();
list.add("Daniel");
list.add(0, "Marko");
assertThat(list).hasSize(2);
assertThat(list.get(0)).isEqualTo("Marko");
```

要从列表中删除元素，我们需要提供对象引用或其索引：

```java
List<String> list = new ArrayList<>(Arrays.asList("Daniel", "Marko"));
list.remove(1);
assertThat(list).hasSize(1);
assertThat(list).doesNotContain("Marko");
```

### 3.1. 表现

ArrayList在Java中为我们提供了动态数组。虽然比内置数组慢，但ArrayList帮助我们节省了一些编程工作并提高了代码的可读性。

当我们谈论[时间复杂度时](https://www.baeldung.com/java-collections-complexity)，我们使用大 O 表示法。该符号描述了执行算法的时间如何随着输入的大小而增长。

ArrayList允许随机访问，因为数组是基于索引的。这意味着访问任何元素总是需要一个常数时间O(1)。

添加新元素也需要O(1)时间，除非在特定位置/索引上添加元素，然后它需要O(n)。检查给定列表中是否存在特定元素以线性O(n)时间运行。

删除元素也是如此。我们需要遍历整个数组以找到选择要删除的元素。

### 3.2. 用法

每当我们不确定要使用哪种集合类型时，从ArrayList 开始可能是个好主意。请记住，根据索引访问项目会非常快。但是，根据物品的价值搜索物品或在特定位置添加/删除物品的成本很高。

当保持相同的项目顺序很重要时，使用ArrayList 是有意义的，并且基于位置/索引的快速访问时间是一个重要标准。

当项目的顺序不重要时，避免使用ArrayList 。 另外，当项目经常需要添加到特定位置时，尽量避免使用它。同样，请记住，当搜索特定项目值是一项重要要求时， ArrayList可能不是最佳选择，尤其是在列表很大的情况下。

## 4.链表

LinkedList是双向链表的实现。实现List和Deque(队列的扩展)接口。与ArrayList不同，当我们将数据存储在LinkedList中时，每个元素都维护到前一个元素的链接。

除了标准的List插入 方法外，LinkedList还支持可以在列表的开头或结尾添加元素的其他方法：

```java
LinkedList<String> list = new LinkedList<>();
list.addLast("Daniel");
list.addFirst("Marko");
assertThat(list).hasSize(2);
assertThat(list.getLast()).isEqualTo("Daniel");
```

此列表实现还提供了从列表开头或结尾删除元素的方法：

```java
LinkedList<String> list = new LinkedList<>(Arrays.asList("Daniel", "Marko", "David"));
list.removeFirst();
list.removeLast();
assertThat(list).hasSize(1);
assertThat(list).containsExactly("Marko");
```

实现的Deque接口提供了类似队列的方法来检索、添加和删除元素：

```java
LinkedList<String> list = new LinkedList<>();
list.push("Daniel");
list.push("Marko");
assertThat(list.poll()).isEqualTo("Marko");
assertThat(list).hasSize(1);
```

### 4.1. 表现

LinkedList比ArrayList消耗更多的内存，因为 每个节点都存储对前一个和下一个元素的两个引用。

LinkedList中的插入、添加和删除操作更快，因为没有在后台完成数组的大小调整。当在列表中间某处添加新项目时，只需要更改周围元素中的引用。

LinkedList支持在集合中的任意位置进行O(1)常数时间插入。但是，它在访问特定位置的项目时效率较低，需要O(n) 时间。

删除一个元素也需要O(1)常数时间，因为我们只需要修改几个指针。检查给定列表中是否存在特定元素需要O(n)线性时间，与ArrayList 相同。

### 4.2. 用法

大多数时候我们可以使用ArrayList 作为默认的List实现。但是，在某些用例中，我们应该使用LinkedList。这些包括当我们更喜欢恒定的插入和删除时间，而不是恒定的访问时间和有效的内存使用时。

当保持相同的项目顺序和快速插入时间(在任何位置添加和删除项目)是一个重要标准时，使用LinkedList 是有意义的。

与ArrayList一样，当项目的顺序不重要时，我们应该避免使用LinkedList 。当快速访问时间或搜索项目是一项重要要求时，LinkedList不是最佳选择。 

## 5.哈希表

与ArrayList和LinkedList不同，HashMap实现了Map接口。这意味着每个键都映射到一个值。我们总是需要知道从集合中检索相应值的键：

```java
Map<String, String> map = new HashMap<>();
map.put("123456", "Daniel");
map.put("654321", "Marko");
assertThat(map.get("654321")).isEqualTo("Marko");
```

同样，我们只能使用其键从集合中删除一个值：

```java
Map<String, String> map = new HashMap<>();
map.put("123456", "Daniel");
map.put("654321", "Marko");
map.remove("654321");
assertThat(map).hasSize(1);
```

### 5.1. 表现

有人可能会问，为什么不简单地使用一个List并把所有的键都去掉呢？特别是因为HashMap消耗更多内存来保存键并且它的条目没有排序。答案在于搜索元素的性能优势。

HashMap在检查键是否存在或基于键检索值方面非常有效。这些操作平均需要O(1)。

基于键从HashMap添加和删除元素需要O(1)常数时间。在不知道密钥的情况下检查元素需要线性时间O(n)，因为有必要遍历所有元素。

### 5.2. 用法

与 ArrayList一起， HashMap是Java中最常用的数据结构之一。与其他列表实现不同，HashMap利用索引执行到特定值的跳转，使搜索时间恒定，即使对于大型集合也是如此。

仅当唯一键可用于我们要存储的数据时，使用HashMap才有意义。我们应该在基于键搜索项目时使用它，快速访问时间是一个重要的要求。

当维护集合中项目的相同顺序很重要时，我们应该避免使用HashMap 。

## 六，总结

在本文中，我们探讨了Java中的三种常见集合类型：ArrayList、LinkedList和HashMap。我们查看了它们在添加、删除和搜索项目方面的表现。基于此，我们提供了关于何时在我们的Java应用程序中应用它们的建议。

在示例中，我们仅介绍了添加和删除项目的基本方法。要更详细地了解每个实现 API，请访问我们专门的[ArrayList、](https://www.baeldung.com/java-arraylist) [ArrayList](https://www.baeldung.com/java-arraylist)和[HashMap](https://www.baeldung.com/java-hashmap)文章。