## 1. 概述

在本快速教程中，我们将学习如何使用核心Java库、Guava 和 Apache Commons Collections在 Array 和 List 之间进行转换。

本文是Baeldung 上[“Java – 回归基础”系列的一部分。](https://www.baeldung.com/java-tutorial)

## 延伸阅读：

## [将基元数组转换为列表](https://www.baeldung.com/java-primitive-array-to-list)

了解如何将基元数组转换为相应类型的对象列表。

[阅读更多](https://www.baeldung.com/java-primitive-array-to-list)→

## [在Java中将集合转换为 ArrayList](https://www.baeldung.com/java-convert-collection-arraylist)

一个用Java给定集合构建 ArrayLists 的简短教程。

[阅读更多](https://www.baeldung.com/java-convert-collection-arraylist)→

## [如何在Java中将列表转换为地图](https://www.baeldung.com/java-list-to-map)

了解使用核心功能和一些流行的库将 List 转换为Java中的 Map 的不同方法

[阅读更多](https://www.baeldung.com/java-list-to-map)→

## 2.列表转数组

### 2.1. 使用纯 Java

让我们从使用普通 Java从List到 Array的转换开始：

```java
@Test
public void givenUsingCoreJava_whenListConvertedToArray_thenCorrect() {
    List<Integer> sourceList = Arrays.asList(0, 1, 2, 3, 4, 5);
    Integer[] targetArray = sourceList.toArray(new Integer[0]);
}
```

请注意，我们使用该方法的首选方式是toArray(new T[0])与toArray(new T[size])。正如 Aleksey Shipilëv 在他的[博客文章](https://shipilev.net/blog/2016/arrays-wisdom-ancients/#_conclusion)中所证明的那样，它似乎更快、更安全、更清洁。

### 2.2. 使用番石榴

现在让我们使用Guava API进行相同的转换：

```java
@Test
public void givenUsingGuava_whenListConvertedToArray_thenCorrect() {
    List<Integer> sourceList = Lists.newArrayList(0, 1, 2, 3, 4, 5);
    int[] targetArray = Ints.toArray(sourceList);
}
```

## 3. 数组转列表

### 3.1. 使用纯 Java

让我们从将数组转换为List的普通Java解决方案开始：

```java
@Test
public void givenUsingCoreJava_whenArrayConvertedToList_thenCorrect() {
    Integer[] sourceArray = { 0, 1, 2, 3, 4, 5 };
    List<Integer> targetList = Arrays.asList(sourceArray);
}
```

请注意，这是一个固定大小的列表，仍将由数组支持。如果我们想要一个标准的ArrayList，我们可以简单地实例化一个：

```java
List<Integer> targetList = new ArrayList<Integer>(Arrays.asList(sourceArray));
```

### 3.2. 使用番石榴

现在让我们使用Guava API进行相同的转换：

```java
@Test
public void givenUsingGuava_whenArrayConvertedToList_thenCorrect() {
    Integer[] sourceArray = { 0, 1, 2, 3, 4, 5 };
    List<Integer> targetList = Lists.newArrayList(sourceArray);
}

```

### 3.3. 使用公共收藏

最后，让我们使用[Apache Commons Collections ](https://commons.apache.org/proper/commons-collections/javadocs/)CollectionUtils.addAll API 将数组的元素填充到一个空列表中：

```java
@Test 
public void givenUsingCommonsCollections_whenArrayConvertedToList_thenCorrect() { 
    Integer[] sourceArray = { 0, 1, 2, 3, 4, 5 }; 
    List<Integer> targetList = new ArrayList<>(6); 
    CollectionUtils.addAll(targetList, sourceArray); 
}
```

## 4。总结

所有这些示例和代码片段的实现都可以在[GitHub](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-collections-conversions)上找到。这是一个基于 Maven 的项目，因此它应该很容易导入并按原样运行。