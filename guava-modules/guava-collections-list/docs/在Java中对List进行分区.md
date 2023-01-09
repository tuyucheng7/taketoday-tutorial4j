## 1. 概述

在本文中，我们将说明如何将一个列表拆分为多个给定大小的子列表。

对于相对简单的操作，令人惊讶的是标准Java集合 API 不支持它。幸运的是，[Guava](https://github.com/google/guava)和[Apache Commons Collections](https://commons.apache.org/proper/commons-collections/)都以类似的方式实现了该操作。

本文是Baeldung 上[“Java– 回归基础”系列的一部分。](https://www.baeldung.com/java-tutorial)

## 延伸阅读：

## [在Java中将列表转换为字符串](https://www.baeldung.com/java-list-to-string)

了解如何使用不同的技术将列表转换为字符串。

[阅读更多](https://www.baeldung.com/java-list-to-string)→

## [在Java中洗牌集合](https://www.baeldung.com/java-shuffle-collection)

了解如何在Java中打乱各种集合。

[阅读更多](https://www.baeldung.com/java-shuffle-collection)→

## [Java 中的 Spliterator 简介](https://www.baeldung.com/java-spliterator)

了解可用于遍历和划分序列的 Spliterator 接口。

[阅读更多](https://www.baeldung.com/java-spliterator)→

## 2.使用Guava对列表进行分区

Guava 通过Lists.partition操作将 List 划分为指定大小的子列表：

```java
@Test
public void givenList_whenParitioningIntoNSublists_thenCorrect() {
    List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
    List<List<Integer>> subSets = Lists.partition(intList, 3);

    List<Integer> lastPartition = subSets.get(2);
    List<Integer> expectedLastPartition = Lists.<Integer> newArrayList(7, 8);
    assertThat(subSets.size(), equalTo(3));
    assertThat(lastPartition, equalTo(expectedLastPartition));
}
```

## 3. 使用 Guava 对集合进行分区

Guava 也可以对集合进行分区：

```java
@Test
public void givenCollection_whenParitioningIntoNSublists_thenCorrect() {
    Collection<Integer> intCollection = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);

    Iterable<List<Integer>> subSets = Iterables.partition(intCollection, 3);

    List<Integer> firstPartition = subSets.iterator().next();
    List<Integer> expectedLastPartition = Lists.<Integer> newArrayList(1, 2, 3);
    assertThat(firstPartition, equalTo(expectedLastPartition));
}
```

请记住，分区是原始集合的子列表视图，这意味着原始集合中的更改将反映在分区中：

```java
@Test
public void givenListPartitioned_whenOriginalListIsModified_thenPartitionsChangeAsWell() {
    // Given
    List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
    List<List<Integer>> subSets = Lists.partition(intList, 3);

    // When
    intList.add(9);

    // Then
    List<Integer> lastPartition = subSets.get(2);
    List<Integer> expectedLastPartition = Lists.<Integer> newArrayList(7, 8, 9);
    assertThat(lastPartition, equalTo(expectedLastPartition));
}
```

## 4. 使用 Apache Commons Collections 对列表进行分区

Apache Commons Collections 的最新版本[最近也添加](https://issues.apache.org/jira/browse/COLLECTIONS-393)了对列表分区的支持：

```java
@Test
public void givenList_whenParitioningIntoNSublists_thenCorrect() {
    List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
    List<List<Integer>> subSets = ListUtils.partition(intList, 3);

    List<Integer> lastPartition = subSets.get(2);
    List<Integer> expectedLastPartition = Lists.<Integer> newArrayList(7, 8);
    assertThat(subSets.size(), equalTo(3));
    assertThat(lastPartition, equalTo(expectedLastPartition));
}
```

Commons Collections 没有相应的选项来对原始集合进行分区，类似于 Guava Iterables.partition。

最后，同样的警告也适用于此：生成的分区是原始列表的视图。

## 5.使用Java8对列表进行分区

现在让我们看看如何使用 Java8 对我们的 List 进行分区。

### 5.1. 收集器分区方式

我们可以使用Collectors.partitioningBy()将列表拆分为 2 个子列表：

```java
@Test
public void givenList_whenParitioningIntoSublistsUsingPartitionBy_thenCorrect() {
    List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);

    Map<Boolean, List<Integer>> groups = 
      intList.stream().collect(Collectors.partitioningBy(s -> s > 6));
    List<List<Integer>> subSets = new ArrayList<List<Integer>>(groups.values());

    List<Integer> lastPartition = subSets.get(1);
    List<Integer> expectedLastPartition = Lists.<Integer> newArrayList(7, 8);
    assertThat(subSets.size(), equalTo(2));
    assertThat(lastPartition, equalTo(expectedLastPartition));
}
```

注意：生成的分区不是主列表的视图，因此主列表发生的任何更改都不会影响分区。

### 5.2. 收藏家分组方式

我们还可以使用Collectors.groupingBy()将我们的列表分成多个分区：

```java
@Test
public final void givenList_whenParitioningIntoNSublistsUsingGroupingBy_thenCorrect() {
    List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);

    Map<Integer, List<Integer>> groups = 
      intList.stream().collect(Collectors.groupingBy(s -> (s - 1) / 3));
    List<List<Integer>> subSets = new ArrayList<List<Integer>>(groups.values());

    List<Integer> lastPartition = subSets.get(2);
    List<Integer> expectedLastPartition = Lists.<Integer> newArrayList(7, 8);
    assertThat(subSets.size(), equalTo(3));
    assertThat(lastPartition, equalTo(expectedLastPartition));
}
```

注意：与Collectors.partitioningBy() 一样，生成的分区不会受到主列表更改的影响。

### 5.3. 按分隔符拆分列表

我们还可以使用 Java8 按分隔符拆分我们的列表：

```java
@Test
public void givenList_whenSplittingBySeparator_thenCorrect() {
    List<Integer> intList = Lists.newArrayList(1, 2, 3, 0, 4, 5, 6, 0, 7, 8);

    int[] indexes = 
      Stream.of(IntStream.of(-1), IntStream.range(0, intList.size())
      .filter(i -> intList.get(i) == 0), IntStream.of(intList.size()))
      .flatMapToInt(s -> s).toArray();
    List<List<Integer>> subSets = 
      IntStream.range(0, indexes.length - 1)
               .mapToObj(i -> intList.subList(indexes[i] + 1, indexes[i + 1]))
               .collect(Collectors.toList());

    List<Integer> lastPartition = subSets.get(2);
    List<Integer> expectedLastPartition = Lists.<Integer> newArrayList(7, 8);
    assertThat(subSets.size(), equalTo(3));
    assertThat(lastPartition, equalTo(expectedLastPartition));
}
```

注意：我们使用“0”作为分隔符。我们首先获取了 List 中所有“0”元素的索引，然后我们根据这些索引拆分了List。

## 六. 总结

此处介绍的解决方案使用了额外的库，即 Guava 和 Apache Commons Collections。这两者都非常轻量级并且总体上非常有用，因此将其中之一放在类路径中是非常有意义的。但是，如果这不是一个选项，那么[此处显示](http://www.vogella.com/articles/JavaAlgorithmsPartitionCollection/article.html)了仅Java的解决方案。