## 1. 概述

在本快速教程中，我们将学习如何清除列表中的重复元素。首先，我们将使用纯 Java，然后是 Guava，最后是基于Java8 Lambda 的解决方案。

本教程是Baeldung 上[“Java– 回归基础”系列的一部分。](https://www.baeldung.com/java-tutorial)

## 2. 使用普通Java从列表中删除重复项

我们可以使用标准的Java集合框架通过 Set轻松地从 List 中删除重复元素：

```java
public void 
  givenListContainsDuplicates_whenRemovingDuplicatesWithPlainJava_thenCorrect() {
    List<Integer> listWithDuplicates = Lists.newArrayList(5, 0, 3, 1, 2, 3, 0, 0);
    List<Integer> listWithoutDuplicates = new ArrayList<>(
      new HashSet<>(listWithDuplicates));

    assertThat(listWithoutDuplicates, hasSize(5));
    assertThat(listWithoutDuplicates, containsInAnyOrder(5, 0, 3, 1, 2));
}
```

如我们所见，原始列表保持不变。

在上面的示例中，我们使用了HashSet实现，它是一个无序集合。因此，清理后的listWithoutDuplicates的顺序可能与原始listWithDuplicates的顺序不同。

如果我们需要保留顺序，我们可以改用LinkedHashSet：

```java
public void 
  givenListContainsDuplicates_whenRemovingDuplicatesPreservingOrderWithPlainJava_thenCorrect() {
    List<Integer> listWithDuplicates = Lists.newArrayList(5, 0, 3, 1, 2, 3, 0, 0);
    List<Integer> listWithoutDuplicates = new ArrayList<>(
      new LinkedHashSet<>(listWithDuplicates));

    assertThat(listWithoutDuplicates, hasSize(5));
    assertThat(listWithoutDuplicates, containsInRelativeOrder(5, 0, 3, 1, 2));
}
```

## 延伸阅读：

## [Java 集合面试题](https://www.baeldung.com/java-collections-interview-questions)

一组实用的Collections相关的Java面试题

[阅读更多](https://www.baeldung.com/java-collections-interview-questions)→

## [Java——合并多个集合](https://www.baeldung.com/java-combine-multiple-collections)

在Java中组合多个集合的快速实用指南

[阅读更多](https://www.baeldung.com/java-combine-multiple-collections)→

## [如何使用Java在列表中查找元素](https://www.baeldung.com/find-list-element-java)

看看在Java列表中查找元素的一些快速方法

[阅读更多](https://www.baeldung.com/find-list-element-java)→

## 3. 使用 Guava 从列表中删除重复项

我们也可以使用 Guava 做同样的事情：

```java
public void 
  givenListContainsDuplicates_whenRemovingDuplicatesWithGuava_thenCorrect() {
    List<Integer> listWithDuplicates = Lists.newArrayList(5, 0, 3, 1, 2, 3, 0, 0);
    List<Integer> listWithoutDuplicates 
      = Lists.newArrayList(Sets.newHashSet(listWithDuplicates));

    assertThat(listWithoutDuplicates, hasSize(5));
    assertThat(listWithoutDuplicates, containsInAnyOrder(5, 0, 3, 1, 2));
}
```

在这里，原始列表也保持不变。

同样，清理列表中元素的顺序可能是随机的。

如果我们使用LinkedHashSet实现，我们将保留初始顺序：

```java
public void 
  givenListContainsDuplicates_whenRemovingDuplicatesPreservingOrderWithGuava_thenCorrect() {
    List<Integer> listWithDuplicates = Lists.newArrayList(5, 0, 3, 1, 2, 3, 0, 0);
    List<Integer> listWithoutDuplicates 
      = Lists.newArrayList(Sets.newLinkedHashSet(listWithDuplicates));

    assertThat(listWithoutDuplicates, hasSize(5));
    assertThat(listWithoutDuplicates, containsInRelativeOrder(5, 0, 3, 1, 2));
}
```

## 4. 使用Java8 Lambdas 从列表中删除重复项

最后，让我们看看一个新的解决方案，在Java8 中使用 Lambdas。我们将使用Stream API 中的distinct()方法，它根据equals()方法返回的结果返回一个由不同元素组成的流。

此外，对于有序流，不同元素的选择是稳定的。这意味着对于重复的元素，将保留在遇到顺序中首先出现的元素：

```java
public void 
  givenListContainsDuplicates_whenRemovingDuplicatesWithJava8_thenCorrect() {
    List<Integer> listWithDuplicates = Lists.newArrayList(5, 0, 3, 1, 2, 3, 0, 0);
    List<Integer> listWithoutDuplicates = listWithDuplicates.stream()
     .distinct()
     .collect(Collectors.toList());

    assertThat(listWithoutDuplicates, hasSize(5));
    assertThat(listWithoutDuplicates, containsInAnyOrder(5, 0, 3, 1, 2));
}
```

我们有它，三种快速的方法来清理列表中的所有重复项。

## 5.总结

在本文中，我们演示了使用纯 Java、Google Guava 和Java8 从列表中删除重复项是多么容易。