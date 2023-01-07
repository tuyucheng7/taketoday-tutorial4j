## 1. 概述

 在本快速教程中，我们将介绍在构建 HashSet 时使用值初始化HashSet的各种方法。

要探索HashSet的特性，请参阅 [此处的这篇核心文章](https://www.baeldung.com/java-hashset)。

我们将深入探讨自Java5 及之前版本以来的 Java内置方法 ，然后是自Java8 以来引入的新机制。

我们还将看到自定义实用程序方法，并最终探索第三方集合库(尤其是 Google Guava)提供的功能。

如果我们已经迁移到 JDK9+，我们可以简单地使用[集合工厂方法。](https://www.baeldung.com/java-9-collections-factory-methods)

## 2. Java内置方法

让我们首先研究自Java5 或更早版本以来可用的三种内置机制。

### 2.1. 使用另一个集合实例

我们可以传递另一个集合的现有实例 来初始化 Set。

这里我们使用内联创建的List：

```java
Set<String> set = new HashSet<>(Arrays.asList("a", "b", "c"));
```

### 2.2. 使用匿名类

在另一种方法中，我们可以使用匿名类向HashSet添加元素。

注意双花括号的使用。这种方法在技术上非常昂贵，因为它在每次调用时都会创建一个匿名类。

因此，根据我们需要初始化Set的频率，我们可以尽量避免使用这种方法：

```java
Set<String> set = new HashSet<String>(){{
    add("a");
    add("b");
    add("c");
}};
```

### 2.3. 从Java5 开始使用 Collections Utility 方法

Java 的Collections实用程序类提供名为singleton的方法来创建 具有单个元素的Set 。使用单例方法创建 的Set实例是不可变的，这意味着我们不能向它添加更多的值。

在某些情况下，尤其是在单元测试中，我们需要创建一个具有单个值的集合：

```java
Set<String> set = Collections.singleton("a");
```

## 3.定义自定义实用方法

我们可以定义一个静态的 final方法，如下所示。该方法接受可变参数。

使用 接受集合对象和值数组的 Collections.addAll 是最好的，因为值的开销很低。

该方法使用泛型，因此我们可以传递任何类型的值：

```java
public static final <T> Set<T> newHashSet(T... objs) {
    Set<T> set = new HashSet<T>();
    Collections.addAll(set, objs);
    return set;
}
```

以下是我们如何在代码中使用实用程序方法：

```java
Set<String> set = newHashSet("a","b","c");
```

## 4.从Java8开始使用Stream

随着Java8 中Stream API 的引入，我们有了更多的选择，比如Stream with Collectors：

```java
Set<String> set = Stream.of("a", "b", "c")
  .collect(Collectors.toCollection(HashSet::new));
```

## 5.使用第三方合集库

有多个第三方集合库，包括 Google Guava、Apache Commons Collections 和 Eclipse Collections 等等。

这些库提供了方便的实用方法来初始化 Set 等集合。由于[Google Guava](https://search.maven.org/classic/#search|ga|1|g%3A"com.google.guava")是最常用的一种，我们提供了一个示例。

Guava 为可变和不可变的Set对象提供了方便的方法：

```java
Set<String> set = Sets.newHashSet("a", "b", "c");
```

同样，Guava 有一个用于创建 不可变Set实例的实用程序类：

```java
Set<String> set = ImmutableSet.of("a", "b", "c");
```

## 六，总结

在本文中，我们看到了在构造HashSet时可以对其进行初始化的多种方法。

这些方法不一定涵盖所有可能的方式。本文只是尝试展示最常见的方式。

例如，此处未涵盖的一种方法可能是使用对象生成器来构造一个Set。