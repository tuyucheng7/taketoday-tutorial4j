## 1. 概述

这篇简短的文章将展示如何使用纯Java以及基于[Guava](https://code.google.com/p/guava-libraries/)的快速示例将Map的值转换为Array 、List或Set 。

本文是Baeldung 上[“Java – 回归基础”系列的一部分。](https://www.baeldung.com/java-tutorial)

## 延伸阅读：

## [在Java中遍历 Map](https://www.baeldung.com/java-iterate-map)

了解在Java中循环访问 Map 条目的不同方法。

[阅读更多](https://www.baeldung.com/java-iterate-map)→

## [map() 和 flatMap() 的区别](https://www.baeldung.com/java-difference-map-and-flatmap)

通过分析 Streams 和 Optionals 的一些例子来了解 map() 和 flatMap() 之间的区别。

[阅读更多](https://www.baeldung.com/java-difference-map-and-flatmap)→

## [如何在Java中的映射中存储重复的键？](https://www.baeldung.com/java-map-duplicate-keys)

在Java中使用多重映射处理重复键的快速实用指南。

[阅读更多](https://www.baeldung.com/java-map-duplicate-keys)→

## 2. 将值映射到数组

首先，让我们看看使用普通 java将 Map 的值转换为数组：

```java
@Test
public void givenUsingCoreJava_whenMapValuesConvertedToArray_thenCorrect() {
    Map<Integer, String> sourceMap = createMap();

    Collection<String> values = sourceMap.values();
    String[] targetArray = values.toArray(new String[0]);
}
```

请注意，与toArray(new T[size]) 相比， toArray(new T[0])是使用该方法的首选方式。正如 Aleksey Shipilëv 在他的[博客文章](https://shipilev.net/blog/2016/arrays-wisdom-ancients/#_conclusion)中所证明的那样，它似乎更快、更安全、更清洁。

## 3. 将值映射到列表

接下来，让我们使用纯Java将 Map 的值转换为 List：

```java
@Test
public void givenUsingCoreJava_whenMapValuesConvertedToList_thenCorrect() {
    Map<Integer, String> sourceMap = createMap();

    List<String> targetList = new ArrayList<>(sourceMap.values());
}
```

并使用番石榴：

```java
@Test
public void givenUsingGuava_whenMapValuesConvertedToList_thenCorrect() {
    Map<Integer, String> sourceMap = createMap();

    List<String> targetList = Lists.newArrayList(sourceMap.values());
}
```

## 4. 要设置的映射值

最后，让我们使用纯 java 将 Map 的值转换为 Set：

```java
@Test
public void givenUsingCoreJava_whenMapValuesConvertedToS_thenCorrect() {
    Map<Integer, String> sourceMap = createMap();

    Set<String> targetSet = new HashSet<>(sourceMap.values());
}
```

## 5.总结

如你所见，所有转换都可以使用一行代码完成，仅使用Java标准集合库。