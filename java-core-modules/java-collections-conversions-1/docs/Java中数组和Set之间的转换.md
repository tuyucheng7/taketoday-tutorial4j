## 1. 概述

在这篇简短的文章中，我们将研究数组和Set之间的转换——首先使用纯 java，然后使用 Guava 和 Apache 的 Commons Collections 库。

本文是Baeldung 上[“Java – 回归基础”系列的一部分。](https://www.baeldung.com/java-tutorial)

## 2. 将数组转换为集合

### 2.1. 使用纯 Java

让我们首先看看如何使用普通 Java将数组转换为Set：

```java
@Test
public void givenUsingCoreJavaV1_whenArrayConvertedToSet_thenCorrect() {
    Integer[] sourceArray = { 0, 1, 2, 3, 4, 5 };
    Set<Integer> targetSet = new HashSet<Integer>(Arrays.asList(sourceArray));
}
```

或者，可以先创建Set ，然后用数组元素填充：

```java
@Test
public void givenUsingCoreJavaV2_whenArrayConvertedToSet_thenCorrect() {
    Integer[] sourceArray = { 0, 1, 2, 3, 4, 5 };
    Set<Integer> targetSet = new HashSet<Integer>();
    Collections.addAll(targetSet, sourceArray);
}
```

### 2.2. 使用谷歌番石榴

接下来，让我们看看Guava 从数组到 Set 的转换：

```java
@Test
public void givenUsingGuava_whenArrayConvertedToSet_thenCorrect() {
    Integer[] sourceArray = { 0, 1, 2, 3, 4, 5 };
    Set<Integer> targetSet = Sets.newHashSet(sourceArray);
}
```

### 2.3. 使用 Apache Commons 集合

最后，让我们使用 Apache 的 Commons Collection 库进行转换：

```java
@Test
public void givenUsingCommonsCollections_whenArrayConvertedToSet_thenCorrect() {
    Integer[] sourceArray = { 0, 1, 2, 3, 4, 5 };
    Set<Integer> targetSet = new HashSet<>(6);
    CollectionUtils.addAll(targetSet, sourceArray);
}
```

## 3. 集合转数组

### 3.1. 使用纯 Java

现在让我们看看相反的情况——将现有的 Set 转换为数组：

```java
@Test
public void givenUsingCoreJava_whenSetConvertedToArray_thenCorrect() {
    Set<Integer> sourceSet = Sets.newHashSet(0, 1, 2, 3, 4, 5);
    Integer[] targetArray = sourceSet.toArray(new Integer[0]);
}
```

请注意，与toArray(new T[size]) 相比， toArray(new T[0])是使用该方法的首选方式。正如 Aleksey Shipilëv 在他的[博客文章](https://shipilev.net/blog/2016/arrays-wisdom-ancients/#_conclusion)中所证明的那样，它似乎更快、更安全、更清洁。

### 3.2. 使用番石榴

接下来——番石榴解决方案：

```java
@Test
public void givenUsingGuava_whenSetConvertedToArray_thenCorrect() {
    Set<Integer> sourceSet = Sets.newHashSet(0, 1, 2, 3, 4, 5);
    int[] targetArray = Ints.toArray(sourceSet);
}
```

请注意，我们使用的是Guava 的Ints API，因此此解决方案特定于我们正在使用的数据类型。

## 4。总结

所有这些示例和代码片段的实现都可以在[Github](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-collections-conversions)上找到——这是一个基于 Maven 的项目，因此应该很容易导入和运行。