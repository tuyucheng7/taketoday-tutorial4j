## 1. 概述

在这个快速教程中，我们将看看**List和Set之间的转换**，从普通Java开始，然后使用Guava和[Apache Commons Collections](https://commons.apache.org/proper/commons-collections/)库，最后使用Java 10。

## 2. 将List转换为Set

### 2.1 使用纯Java

让我们从使用Java将List转换为Set开始：

```java
void givenUsingCoreJava_whenListConvertedToSet_thenCorrect() {
    List<Integer> sourceList = Arrays.asList(0, 1, 2, 3, 4, 5);
    Set<Integer> targetSet = new HashSet<>(sourceList);
}
```

正如我们所见，转换过程是类型安全且简单的，因为每个集合的构造函数都接受另一个集合作为源。

### 2.2 使用Guava

让我们使用Guava进行相同的转换：

```java
void givenUsingGuava_whenListConvertedToSet_thenCorrect() {
    List<Integer> sourceList = Lists.newArrayList(0, 1, 2, 3, 4, 5);
    Set<Integer> targetSet = Sets.newHashSet(sourceList);
}
```

### 2.3 使用Apache Commons Collections

接下来让我们使用Commons Collections API在List和Set之间进行转换：

```java
void givenUsingCommonsCollections_whenListConvertedToSet_thenCorrect() {
    List<Integer> sourceList = Lists.newArrayList(0, 1, 2, 3, 4, 5);
    Set<Integer> targetSet = new HashSet<>(6);
    CollectionUtils.addAll(targetSet, sourceList);
}
```

### 2.4 使用Java 10

另一种选择是使用Java 10中引入的[Set.copyOf](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Set.html#copyOf(java.util.Collection))静态工厂方法：

```java
void givenUsingJava10_whenListConvertedToSet_thenCorrect() {
    List sourceList = Lists.newArrayList(0, 1, 2, 3, 4, 5);
    Set targetSet = Set.copyOf(sourceList);
}
```

请注意，以这种方式创建的Set是[不可修改](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Set.html#unmodifiable)的。

## 3. 将Set转换为List

### 3.1 使用纯Java

现在让我们使用Java进行反向转换，从Set到List：

```java
void givenUsingCoreJava_whenSetConvertedToList_thenCorrect() {
   Set<Integer> sourceSet = Sets.newHashSet(0, 1, 2, 3, 4, 5);
   List<Integer> targetList = new ArrayList<>(sourceSet);
}
```

### 3.2 使用Guava

我们可以使用Guava解决方案做同样的事情：

```java
void givenUsingGuava_whenSetConvertedToList_thenCorrect() {
    Set<Integer> sourceSet = Sets.newHashSet(0, 1, 2, 3, 4, 5);
    List<Integer> targetList = Lists.newArrayList(sourceSet);
}
```

这与java方法非常相似，只是重复的代码少了一点。

### 3.3 使用Apache Commons Collections

下面是使用Commons Collections的方法：

```java
void givenUsingCommonsCollections_whenSetConvertedToList_thenCorrect() {
    Set<Integer> sourceSet = Sets.newHashSet(0, 1, 2, 3, 4, 5);
    List<Integer> targetList = new ArrayList<>(6);
    CollectionUtils.addAll(targetList, sourceSet);
}
```

### 3.4 使用Java 10

最后，我们可以使用Java 10中引入的[List.copyOf](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/List.html#copyOf(java.util.Collection))：

```java
void givenUsingJava10_whenSetConvertedToList_thenCorrect() {
    Set<Integer> sourceSet = Sets.newHashSet(0, 1, 2, 3, 4, 5);
    List<Integer> targetList = List.copyOf(sourceSet);
}
```

需要记住的是，由此生成的List是[不可修改](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/List.html#unmodifiable)的。

## 4. 总结

在本教程中，我们通过使用原始Java、Guava、Apache Commons Collections、以及Java 10介绍了如何将List和Set集合相互转换。