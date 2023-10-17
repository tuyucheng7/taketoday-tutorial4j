## 1. 概述

**Java 21**预计将于 2023 年 9 月发布，成为继 Java 17 之后的下一个长期支持版本。在新功能中，我们可以看到 Java[*集合框架*](https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/util/doc-files/coll-index.html)的更新 ，称为**Sequenced Collections**。

序列**集合**提案作为一项改变游戏规则的增强功能脱颖而出，有望重新定义开发人员与集合交互的方式。**此功能将新接口注入现有层次结构，提供无缝机制来使用内置默认方法访问集合的第一个和最后一个元素。此外，它还支持获取集合的反向视图。**

在本文中，我们将探讨这一新的增强功能、其潜在风险以及它带来的优势。

## 2. 动机

缺乏具有定义的遭遇顺序的集合的通用超类型一直是问题和抱怨的根源。此外，**缺乏访问第一个和最后一个元素以及按相反顺序迭代的统一方法** 一直是 Java*集合框架*的一个长期限制。

*我们可以以List*和*Deque*为例：两者都定义了遇到顺序，但它们的公共超类型*Collection*没有定义。同样，*Set*不定义遇到顺序，但某些子类型（例如*SortedSet*和*LinkedHashSet*）定义了遇到顺序。因此，对遭遇顺序的支持分布在类型层次结构中，与遭遇顺序相关的操作要么不一致，要么缺失。

为了演示这种不一致，我们来比较一下访问不同集合类型的第一个和最后一个元素：

|                  | **访问第一个元素**                | **访问最后一个元素**        |
| ---------------- | --------------------------------- | --------------------------- |
| ***列表***       | *列表.get(0)*                     | *列表.get(列表.size() – 1)* |
| ***Deque***      | *双端队列.getFirst()*             | *deque.getLast()*           |
| ***排序集***     | *排序集.first()*                  | *排序集.last()*             |
| ***链接哈希集*** | *linkedHashSet.iterator().next()* | *// 丢失的*                 |

当尝试获取集合的反向视图时，也会发生同样的情况。虽然从第一个元素到最后一个元素迭代集合的元素遵循清晰一致的模式，但以相反的方向这样做会带来挑战。

为了说明这一点，在处理 NavigableSet 时*，*我们可以使用*DescendingSet()*方法。对于*Deque*，*降序迭代器（）*方法被证明是有用的。类似地，在处理*List*时，*listIterator()*方法效果很好。*但是， LinkedHashSet*的情况并非如此，因为它不提供任何反向迭代支持。

**所有这些差异导致了代码库的碎片化和复杂性，使得在 API 中表达某些有用的概念变得具有挑战性。**

## 3. 新的Java集合层次结构

**此新功能引入了用于排序集合、排序集和排序映射的三个新接口，这些接口已添加到现有的集合层次结构中：**

[![新的收藏层次结构](https://www.baeldung.com/wp-content/uploads/2023/09/new-hierarchy-diagram.png)](https://www.baeldung.com/wp-content/uploads/2023/09/new-hierarchy-diagram.png)

此图片是 JEP 431 官方文档的一部分[**：序列化集合**](https://openjdk.org/jeps/431)（[来源](https://cr.openjdk.org/~smarks/collections/SequencedCollectionDiagram20220216.png)）。

### 3.1. *顺序集合*

**有序集合是其元素具有定义的遇到顺序的\*集合。\***新的*SequencedCollection*接口提供了在集合两端添加、检索或删除元素的方法，以及获取集合的逆序视图的方法。

```java
interface SequencedCollection<E> extends Collection<E> {

    // new method
    SequencedCollection<E> reversed();

    // methods promoted from Deque
    void addFirst(E);
    void addLast(E);

    E getFirst();
    E getLast();

    E removeFirst();
    E removeLast();
}复制
```

*除reverse()*之外的所有方法都是默认方法，提供默认实现，并且从*Deque*提升。reversed *()*方法提供原始集合的逆序视图。此外，对原始集合的任何修改都可以在反向视图中看到。

add **()*和*remove\*()*方法是可选的，并在其默认实现中抛出*UnsupportedOperationException ，主要是为了支持不可修改的集合和具有已定义排序顺序的集合的情况。*如果集合为空，get **()*和*remove\*()*方法将抛出*NoSuchElementException 。*

### 3.2. *序列集*

**序列集可以定义为专门的\*Set\*，其功能类似于\*SequencedCollection\*，确保不存在重复元素。**SequencedSet接口扩展了*SequencedCollection*并重写了其returned *(* *)*方法。唯一的区别是*SequencedSet.reversed()*的返回类型是*SequencedSet*。

```java
interface SequencedSet<E> extends Set<E>, SequencedCollection<E> {

    // covariant override
    SequencedSet<E> reversed();
}复制
```

### 3.3. *顺序图*

**排序映射是其条目具有定义的遭遇顺序的\*映射。\***SequencedMap不扩展*SequencedCollection*，并提供自己的方法来操作集合两端的元素*。*

```java
interface SequencedMap<K, V> extends Map<K, V> {
    
    // new methods
    SequencedMap<K, V> reversed();
    SequencedSet<K> sequencedKeySet();
    SequencedCollection<V> sequencedValues();
    SequencedSet<Entry<K, V>> sequencedEntrySet();

    V putFirst(K, V);
    V putLast(K, V);

    // methods promoted from NavigableMap
    Entry<K, V> firstEntry();
    Entry<K, V> lastEntry();
    Entry<K, V> pollFirstEntry();
    Entry<K, V> pollLastEntry();
}
复制
```

与*SequencedCollection*类似，*put\*()*方法对于不可修改的映射或具有已定义排序顺序的映射抛出*UnsupportedOperationException*。*此外，在空地图上调用从NavigableMap*提升的方法之一会导致抛出*NoSuchElementException*。

## 4、风险

新接口的引入应该不会影响仅使用集合实现的代码。但是，如果在我们的代码库中定义自定义集合类型，可能会出现几种冲突：

- **方法命名**：引入的新方法可能会与现有类上的方法发生冲突。例如，如果我们有一个*List*接口的自定义实现，该实现已经定义了*getFirst()*方法，但返回类型与*SequencedCollection*中定义的*getFirst()*不同，那么在升级到 Java 21 时，它将导致源不兼容。
- **协变重写**：*List*和*Deque都提供了**returned()*方法的协变重写，一个返回*List*，另一个返回*Deque*。因此，任何实现这两个接口的自定义集合在升级到 Java 21 时都会导致编译时错误，因为编译器无法选择其中之一。

**报告[JDK-8266572](https://bugs.openjdk.org/browse/JDK-8266572)包含对不兼容风险的完整分析。**

## 5. 结论

总之，**顺序集合标志着**[Java 集合](https://www.baeldung.com/java-collections)的重大飞跃。通过满足长期以来对以统一方式处理具有定义的遇到顺序的集合的需求，Java 使开发人员能够更高效、更直观地工作。新接口建立了更清晰的结构和一致的行为，从而产生更健壮和可读的代码。