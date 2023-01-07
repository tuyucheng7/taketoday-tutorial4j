## 1. 概述

在本文中，我们将了解负载因子在Java的HashMap中的重要性以及它如何影响映射的性能。

## 2.什么是HashMap？

[HashMap](https://www.baeldung.com/java-hashmap)类属于Java Collection框架，提供了Map接口的基本实现。当我们想根据键值对存储数据时，我们可以使用它。这些键值对称为映射条目，由Map.Entry类表示。

## 3.HashMap内部结构

在讨论负载因子之前，让我们先回顾一下几个术语：

-   -   哈希
    -   容量
    -   临界点
    -   重新散列
    -   碰撞

HashMap的工作原理是散列——一种将对象数据映射到某个有代表性的整数值的算法。哈希函数应用于键对象以计算存储桶的索引，以便存储和检索任何键值对。

容量是HashMap中桶的数量。初始容量是创建M ap时的容量。最后，HashMap的默认初始容量为 16。

随着HashMap中元素数量的增加，容量也在不断扩大。负载因子是决定何时增加Map容量的度量。默认负载系数为容量的 75%。

HashMap的阈值大约是当前容量和负载因子的乘积。重新散列是重新计算已存储条目的散列码的过程。简单地说，当哈希表中的条目数超过阈值时，将对Map进行重新哈希处理，使其具有大约两倍于以前的桶数。

当哈希函数为两个不同的键返回相同的桶位置时，就会发生冲突。

让我们创建我们的HashMap：

```java
Map<String, String> mapWithDefaultParams = new HashMap<>();
mapWithDefaultParams.put("1", "one");
mapWithDefaultParams.put("2", "two");
mapWithDefaultParams.put("3", "three");
mapWithDefaultParams.put("4", "four");

```

这是我们地图的结构：

[![带有默认参数的 HashMap](https://www.baeldung.com/wp-content/uploads/2021/02/HashMapwithDefaultParams-1.jpg)](https://www.baeldung.com/wp-content/uploads/2021/02/HashMapwithDefaultParams-1.jpg)

如我们所见，我们的HashMap是使用默认初始容量 (16) 和默认加载因子 (0.75) 创建的。另外，阈值是 16  0.75 = 12，这意味着它会在添加第 12 个条目(键值对)后将容量从 16 增加到 32。

## 4.自定义初始容量和负载因子

在上一节中，我们使用默认构造函数创建了HashMap 。在接下来的部分中，我们将看到如何创建一个将初始容量和加载因子传递给构造函数的HashMap 。

### 4.1. 初始容量

首先，让我们创建一个具有初始容量的Map ：

```java
Map<String, String> mapWithInitialCapacity = new HashMap<>(5);

```

它将创建一个具有初始容量 (5) 和默认负载因子 (0.75)的空Map 。

### 4.2. 具有初始容量和负载系数

同样，我们可以使用初始容量和负载因子来创建我们的Map ：

```java
Map<String, String> mapWithInitialCapacityAndLF = new HashMap<>(5, 0.5f);

```

在这里，它将创建一个初始容量为 5 且负载因子为 0.5的空Map 。

## 5.性能

虽然我们可以灵活地选择初始容量和负载因子，但我们需要明智地选择它们。它们都会影响Map的性能。让我们深入了解这些参数与性能的关系。

### 5.1. 复杂

我们知道，HashMap内部使用哈希码作为存储键值对的基础。如果hashCode()方法编写得当，HashMap会将项目分布到所有桶中。因此，HashMap在常数时间O(1)内存储和检索条目。

但是，当项目数量增加且桶大小固定时，就会出现问题。它将在每个桶中包含更多项目，并且会扰乱时间复杂度。

解决方案是我们可以在item数量增加的时候增加bucket的数量。然后我们可以在所有桶中重新分配项目。通过这种方式，我们将能够在每个桶中保持恒定数量的项目并保持O(1)的时间复杂度。

在这里，负载因子帮助我们决定何时增加桶的数量。负载系数越低，就会有更多的空闲桶，因此发生碰撞的机会就越少。这将帮助我们实现更好的Map性能。因此，我们需要保持较低的负载因子以实现较低的时间复杂度。

HashMap通常具有O(n)的空间复杂度，其中n是条目数。较高的负载因子值会减少空间开销，但会增加查找成本。

### 5.2. 重新散列

当Map中的项数超过阈值限制时， Map的容量将增加一倍。如前所述，当容量增加时，我们需要在所有桶中平均分配所有条目(包括现有条目和新条目)。在这里，我们需要重新散列。即对每个已有的键值对，以增加的容量为参数，重新计算哈希码。

基本上，当负载因子增加时，复杂性增加。进行重新散列以保持所有操作的低负载因子和低复杂性。

让我们初始化我们的地图：

```java
Map<String, String> mapWithInitialCapacityAndLF = new HashMap<>(5,0.75f);
mapWithInitialCapacityAndLF.put("1", "one");
mapWithInitialCapacityAndLF.put("2", "two");
mapWithInitialCapacityAndLF.put("3", "three");
mapWithInitialCapacityAndLF.put("4", "four");
mapWithInitialCapacityAndLF.put("5", "five");
```

让我们看一下Map的结构：

[![之前的HashMap](https://www.baeldung.com/wp-content/uploads/2021/02/HashMap_before-1.jpg)](https://www.baeldung.com/wp-content/uploads/2021/02/HashMap_before-1.jpg)

现在，让我们向Map添加更多条目：

```sas
mapWithInitialCapacityAndLF.put("6", "Six");
mapWithInitialCapacityAndLF.put("7", "Seven");
//.. more entries
mapWithInitialCapacityAndLF.put("15", "fifteen");
```

让我们再次观察我们的Map结构：

[![之后的哈希图](https://www.baeldung.com/wp-content/uploads/2021/02/HashMap_after-1.jpg)](https://www.baeldung.com/wp-content/uploads/2021/02/HashMap_after-1.jpg)

尽管重新散列有助于保持低复杂度，但这是一个昂贵的过程。如果我们需要存储大量数据，我们应该创建具有足够容量的HashMap 。这比自动重新散列更有效。

### 5.3. 碰撞

错误的哈希码算法可能会发生冲突，并且通常会降低Map的性能。

在Java8 之前，Java 中的HashMap通过使用[LinkedList](https://www.baeldung.com/java-linkedlist)来存储 map 条目来处理碰撞。如果一个键最终出现在另一个条目已经存在的同一个桶中，它将被添加到LinkedList的头部。在最坏的情况下，这会将复杂度增加到O(n)。

为避免此问题，Java 8 及更高版本使用平衡树(也称为[红黑树](https://www.baeldung.com/cs/red-black-trees))代替LinkedList来存储冲突条目。这将HashMap的最坏情况下的性能从O(n)提高到O(log n)。

HashMap最初使用LinkedList。然后当条目数超过一定阈值时，它将用平衡二叉树替换LinkedList 。TREEIFY_THRESHOLD常量决定了这个阈值。目前这个值为8，也就是说如果同一个bucket中的元素超过8个，Map会用树来容纳。

## 六，总结

在本文中，我们讨论了一种最流行的数据结构：HashMap。我们还看到了负载因子和容量如何影响其性能。