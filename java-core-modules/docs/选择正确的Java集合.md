## 一、简介

在本教程中，我们将讨论如何在 Java 库中选择合适的集合接口和类。我们在讨论中跳过遗留集合，例如[*Vector*](https://www.baeldung.com/java-arraylist-vs-vector)*、[Stack](https://www.baeldung.com/java-stack)和[Hashtable](https://www.baeldung.com/java-hash-table)* ，因为我们需要避免使用它们以支持新集合。并发集合值得单独讨论，所以我们也不讨论它们。

## 2. Java 库中的集合接口

在尝试有效地使用它们之前了解 Java 库中集合接口和类的组织是非常有用的。Collection接口是*[所有](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collection.html)* 集合接口的根。[*List*](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/util/List.html)、*[Set](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Set.html)和*[*Queue*](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/util/Queue.html)接口扩展了*Collection*。

Java 库中的映射不被视为常规集合，因此[*Map*](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/util/Map.html)接口不扩展*Collection。*下面是 Java 库中的接口关系图：

[![收藏](https://www.baeldung.com/wp-content/uploads/2022/11/1-1.png)](https://www.baeldung.com/wp-content/uploads/2022/11/1-1.png)

任何具体的集合实现（集合类）都派生自集合接口之一。集合类的语义由它们的接口定义，因为具体的集合为其父接口定义的操作提供特定的实现。因此，我们需要在选择合适的集合类之前选择合适的集合接口。

## 3.选择合适的收款接口

选择正确的集合接口有些简单。事实上，下图显示了一个逻辑接口选择流程：

[![接口选型图](https://www.baeldung.com/wp-content/uploads/2022/11/Interface-Selection-Diagram-1.png)](https://www.baeldung.com/wp-content/uploads/2022/11/Interface-Selection-Diagram-1.png)

总而言之，当元素的插入顺序很重要并且存在重复元素时，我们使用列表。当元素被视为一组对象时使用集合，没有重复项，插入顺序无关紧要。

*当需要LIFO*、*FIFO*或按优先级语义删除时使用队列 ，最后，当需要键和值的关联时使用映射。

## 4. 选择正确的集合实现

下面我们可以找到按它们实现的接口分隔的集合类的比较表。这些比较是基于常见操作及其性能进行的。具体来说，操作的性能是使用[*Big-O*](https://www.baeldung.com/java-algorithm-complexity)表示法估计的。[可以在集合操作的](https://www.baeldung.com/java-collections-complexity)基准中找到有关 Java 集合中操作持续时间的更实用的指南 。

### 4.1. 列表

让我们从列表比较表开始。列表的常见操作是添加和删除元素、通过索引访问元素、遍历元素和查找元素：

| 列表比较表                                            | 在开头添加/删除元素 | 在中间添加/删除元素 | 最后添加/删除元素 | 获取第 i 个元素（随机访问） | 查找元素                     | 遍历顺序 |
| ----------------------------------------------------- | ------------------- | ------------------- | ----------------- | --------------------------- | ---------------------------- | -------- |
| *[数组列表](https://www.baeldung.com/java-arraylist)* | *在）*              | *在）*              | *O(1)*            | *O(1)*                      | *O(n)* , *O(log(n))*如果排序 | 如插入   |
| [*链表*](https://www.baeldung.com/java-linkedlist)    | *O(1)*              | *O(1)*              | *O(1)*            | *在）*                      | *在）*                       | 如插入   |

正如我们所看到的，*ArrayList*擅长在最后添加和删除元素，以及对元素的随机访问。相反，它不擅长在任意位置添加和删除元素。同时，*LinkedList*擅长在任意位置添加和删除元素。但是，它不支持真正的*O(1)*随机访问。因此，对于列表，默认选择*ArrayList*，直到我们需要在任意位置快速添加和删除元素。

### 4.2. 套

对于集合，我们感兴趣的是添加和删除元素、遍历元素和查找元素：

| 套装比较表                                                  | 添加元素     | 删除元素     | 查找元素     | 遍历顺序                 |
| ----------------------------------------------------------- | ------------ | ------------ | ------------ | ------------------------ |
| [*哈希集*](https://www.baeldung.com/java-hashset)           | 摊销*O(1)*   | 摊销*O(1)*   | *O(1)*       | 随机的，通过散列函数分散 |
| [*链接哈希集*](https://www.baeldung.com/java-linkedhashset) | 摊销*O(1)*   | 摊销*O(1)*   | *O(1)*       | 如插入                   |
| [*树集*](https://www.baeldung.com/java-tree-set)            | *O(日志(n))* | *O(日志(n))* | *O(日志(n))* | 排序，根据元素比较标准   |
| [*枚举集*](https://www.baeldung.com/java-enumset)           | *O(1)*       | *O(1)*       | *O(1)*       | 根据枚举值的定义顺序     |

如我们所见，默认选择是*HashSet*集合，因为它对于它支持的所有操作都非常快。此外，如果元素的插入顺序也很重要，我们会使用*LinkedHashSet*。*基本上，它是HashSet*的扩展，它通过在内部使用链表结构来跟踪元素的插入顺序。

如果需要对元素进行排序并且在添加和删除元素时需要保留排序顺序，那么我们使用*TreeSet*。

如果集合的元素只是单个枚举类型的枚举值，那么最明智的选择是*EnumSet*。

### 4.3. 尾巴

队列可以分为两组：

1.  *LinkedList*、[*ArrayDeque*](https://www.baeldung.com/java-array-deque) – [*Queue*](https://www.baeldung.com/java-queue)接口实现可以充当堆栈、队列和出队数据结构。通常，*ArrayDeque比**LinkedList*快。因此这是默认选择
2.  *[PriorityQueue](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/PriorityQueue.html) –* 由二进制堆数据结构支持的队列接口实现。用于快速 ( *O(1)* ) 元素检索，具有最高优先级。*O(log(n))*时间内的添加和删除工作

### 4.4. 地图

与集合类似，我们考虑添加和删除元素、遍历元素和查找元素的映射操作：

| 地图比较表                                             | 添加元素     | 删除元素     | 查找元素     | 遍历顺序                 |
| ------------------------------------------------------ | ------------ | ------------ | ------------ | ------------------------ |
| [*哈希表*](https://www.baeldung.com/java-hashmap)      | 摊销*O(1)*   | 摊销*O(1)*   | *O(1)*       | 随机的，通过散列函数分散 |
| [*链表*](https://www.baeldung.com/java-linked-hashmap) | 摊销*O(1)*   | 摊销*O(1)*   | *O(1)*       | 如插入                   |
| [*树图*](https://www.baeldung.com/java-treemap)        | *O(日志(n))* | *O(日志(n))* | *O(日志(n))* | 排序，根据元素比较标准   |
| [*枚举图*](https://www.baeldung.com/java-enum-map)     | *O(1)*       | *O(1)*       | *O(1)*       | 根据枚举值的定义顺序     |

maps 的选择逻辑类似于 sets 的选择逻辑：我们默认使用*HashMap* ，如果另外，插入顺序很重要，则使用*LinkedHashMap ，排序时使用**TreeMap*，当键属于特定枚举类型的值时使用*EnumMap 。*

最后，还有两个*Map*接口的实现，它们有非常具体的应用：[*IdentityHashMap*](https://www.baeldung.com/java-identityhashmap)和[*WeakHashMap*](https://www.baeldung.com/java-weakhashmap)。

## 五、具体馆藏选择示意图

我们可以扩展图表来选择合适的集合接口来选择具体的集合实现：

[![具体馆藏选择图](https://www.baeldung.com/wp-content/uploads/2022/11/Concrete-Collection-Selection-Diagram.png)](https://www.baeldung.com/wp-content/uploads/2022/11/Concrete-Collection-Selection-Diagram.png)

## 六，结论

在本文中，我们了解了 Java 库中的集合接口和集合类。此外，我们还提出了选择正确接口和实现的方法。