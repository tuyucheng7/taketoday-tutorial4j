## 1. 概述

在本文中，我们将探索Java Collections Framework (JCF)中Map接口的TreeMap实现。

TreeMap是一种地图实现，它根据键的自然顺序或更好地使用比较器(如果用户在构建时提供)来保持其条目排序。

之前，我们已经介绍了[HashMap](https://www.baeldung.com/java-hashmap)和[LinkedHashMap](https://www.baeldung.com/java-linked-hashmap)的实现，我们将意识到有很多关于这些类如何工作的相似信息。

强烈建议在阅读本文之前先阅读上述文章。

## 2. TreeMap中的默认排序

默认情况下，TreeMap根据其自然顺序对其所有条目进行排序。对于整数，这意味着升序，对于字符串，这意味着字母顺序。

让我们看看测试中的自然顺序：

```java
@Test
public void givenTreeMap_whenOrdersEntriesNaturally_thenCorrect() {
    TreeMap<Integer, String> map = new TreeMap<>();
    map.put(3, "val");
    map.put(2, "val");
    map.put(1, "val");
    map.put(5, "val");
    map.put(4, "val");

    assertEquals("[1, 2, 3, 4, 5]", map.keySet().toString());
}
```

请注意，我们以无序方式放置整数键，但在检索键集时，我们确认它们确实按升序排列。这是整数的自然排序。

同样，当我们使用字符串时，它们将按自然顺序排序，即按字母顺序排列：

```java
@Test
public void givenTreeMap_whenOrdersEntriesNaturally_thenCorrect2() {
    TreeMap<String, String> map = new TreeMap<>();
    map.put("c", "val");
    map.put("b", "val");
    map.put("a", "val");
    map.put("e", "val");
    map.put("d", "val");

    assertEquals("[a, b, c, d, e]", map.keySet().toString());
}
```

TreeMap与散列映射和链接散列映射不同，它不在任何地方使用散列原理，因为它不使用数组来存储其条目。

## 3. TreeMap自定义排序

如果我们对TreeMap的自然排序不满意，我们还可以在构建树图期间通过比较器定义自己的排序规则。

在下面的示例中，我们希望整数键按降序排列：

```java
@Test
public void givenTreeMap_whenOrdersEntriesByComparator_thenCorrect() {
    TreeMap<Integer, String> map = 
      new TreeMap<>(Comparator.reverseOrder());
    map.put(3, "val");
    map.put(2, "val");
    map.put(1, "val");
    map.put(5, "val");
    map.put(4, "val");
        
    assertEquals("[5, 4, 3, 2, 1]", map.keySet().toString());
}
```

哈希映射不保证存储的键的顺序，特别是不保证这个顺序会随着时间的推移保持不变，但是树映射保证键总是按照指定的顺序排序。

## 4. TreeMap排序的重要性

我们现在知道TreeMap以排序的顺序存储它的所有条目。由于树图的这个属性，我们可以执行类似的查询；查找“最大”、查找“最小”、查找所有小于或大于某个值的键等。

下面的代码只涵盖了这些情况的一小部分：

```java
@Test
public void givenTreeMap_whenPerformsQueries_thenCorrect() {
    TreeMap<Integer, String> map = new TreeMap<>();
    map.put(3, "val");
    map.put(2, "val");
    map.put(1, "val");
    map.put(5, "val");
    map.put(4, "val");
        
    Integer highestKey = map.lastKey();
    Integer lowestKey = map.firstKey();
    Set<Integer> keysLessThan3 = map.headMap(3).keySet();
    Set<Integer> keysGreaterThanEqTo3 = map.tailMap(3).keySet();

    assertEquals(new Integer(5), highestKey);
    assertEquals(new Integer(1), lowestKey);
    assertEquals("[1, 2]", keysLessThan3.toString());
    assertEquals("[3, 4, 5]", keysGreaterThanEqTo3.toString());
}
```

## 5. TreeMap的内部实现

TreeMap实现了NavigableMap接口，其内部工作基于[红黑树](https://www.baeldung.com/cs/red-black-trees)的原则：

```java
public class TreeMap<K,V> extends AbstractMap<K,V>
  implements NavigableMap<K,V>, Cloneable, java.io.Serializable
```

红黑树的原理超出了本文的范围，但是，为了理解它们如何适合TreeMap ，需要记住一些关键事项。

首先，红黑树是一种由节点组成的数据结构；想象一棵倒置的芒果树，它的根在天空中，树枝向下生长。根将包含添加到树中的第一个元素。

规则是从根开始，任何节点左分支中的任何元素总是小于节点本身中的元素。右边的人总是更大。正如我们之前看到的，定义大于或小于的内容取决于元素的自然顺序或构造时定义的比较器。

此规则保证树形图的条目将始终按排序和可预测的顺序排列。

其次，红黑树是一种自平衡的二叉搜索树。此属性和上述属性保证搜索、获取、放置和删除等基本操作需要对数时间O(log n)。

自我平衡是这里的关键。当我们不断插入和删除条目时，请想象树的一条边变长或另一条边变短。

这意味着操作将在较短的分支上花费更短的时间，而在离根最远的分支上花费更长的时间，这是我们不希望发生的事情。

因此，在红黑树的设计中就考虑到了这一点。对于每次插入和删除，任何边上树的最大高度都保持在O(log n)，即树不断地自我平衡。

就像哈希映射和链接哈希映射一样，树映射是不同步的，因此在多线程环境中使用它的规则与其他两个映射实现中的规则相似。

## 6. 选择正确的地图

在查看了之前的[HashMap](https://www.baeldung.com/java-hashmap)和[LinkedHashMap](https://www.baeldung.com/java-linked-hashmap)实现以及现在的TreeMap之后，重要的是对这三者进行简要比较，以指导我们选择适合的位置。

哈希映射作为提供快速存储和检索操作的通用映射实现是很好的。然而，由于其条目的混乱和无序的排列，它不尽如人意。

这导致它在有大量迭代的场景中表现不佳，因为底层数组的整个容量会影响遍历，而不仅仅是条目的数量。

链接哈希映射具有哈希映射的良好属性，并为条目添加了顺序。它在有很多迭代的地方表现更好，因为只考虑条目数而不考虑容量。

树图通过提供对键的排序方式的完全控制，将排序提升到一个新的水平。另一方面，它提供的总体性能比其他两种选择更差。

我们可以说链接的哈希映射减少了哈希映射排序中的混乱，而不会导致树映射的性能损失。

## 七、总结

在本文中，我们探讨了JavaTreeMap类及其内部实现。由于它是一系列常见 Map 接口实现中的最后一个，我们还继续简要讨论了它与其他两个实现的最佳位置。