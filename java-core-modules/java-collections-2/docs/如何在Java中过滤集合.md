## 1. 概述

在这个简短的教程中，我们将了解在Java中过滤集合的不同方法 ——即查找满足特定条件的所有项目。

这是几乎所有Java应用程序中都存在的一项基本任务。

因此，为此目的提供功能的库数量很多。

特别是，在本教程中，我们将介绍：

-   Java 8 Streams 的filter()函数
-   Java 9过滤收集器
-   相关的Eclipse 集合API
-   Apache 的CollectionUtils filter()方法
-   Guava 的Collections2 filter()方法

## 2.使用流

自从引入Java8 以来，在我们必须处理数据集合的大多数情况下，[Streams](https://www.baeldung.com/java-8-streams-introduction)都发挥了关键作用。

因此，在大多数情况下，这是首选方法，因为它是用Java构建的，不需要额外的依赖项。

### 2.1. 使用流过滤集合

为了简单起见，在所有示例中，我们的目标都是创建一个方法，该方法仅从整数值集合中检索偶数。

因此，我们可以将用于评估每个项目的条件表达为“ value % 2 == 0 ”。

在所有情况下，我们都必须将此条件定义为Predicate对象：

```java
public Collection<Integer> findEvenNumbers(Collection<Integer> baseCollection) {
    Predicate<Integer> streamsPredicate = item -> item % 2 == 0;

    return baseCollection.stream()
      .filter(streamsPredicate)
      .collect(Collectors.toList());
}
```

重要的是要注意，我们在本教程中分析的每个库都提供了自己的 Predicate 实现，但它们仍然被定义为功能接口，因此允许我们使用 Lambda 函数来声明它们。

在这种情况下，我们使用了Java提供的预定义收集器 ，它将元素累积到一个列表中，但我们也可以使用其他收集器，如上一篇文章中所讨论的[那样](https://www.baeldung.com/java-8-collectors)。

### 2.2. 在Java9 中对集合进行分组后进行过滤

Streams 允许我们使用 [groupingBy 收集器](https://www.baeldung.com/java-groupingby-collector)聚合项目。

然而，如果我们像上一节那样进行过滤，一些元素可能会在这个收集器发挥作用之前的早期阶段被丢弃。

为此，Java9 引入了过滤收集器，目的是在分组后处理子集合。

按照我们的示例，假设我们要在过滤掉奇数之前根据每个 Integer 的位数对我们的集合进行分组：

```java
public Map<Integer, List<Integer>> findEvenNumbersAfterGrouping(
  Collection<Integer> baseCollection) {
 
    Function<Integer, Integer> getQuantityOfDigits = item -> (int) Math.log10(item) + 1;
    
    return baseCollection.stream()
      .collect(groupingBy(
        getQuantityOfDigits,
        filtering(item -> item % 2 == 0, toList())));
}
```

简而言之，如果我们使用这个收集器，我们可能会得到一个空值条目，而如果我们在分组之前进行过滤，收集器根本不会创建这样一个条目。

当然，我们会根据我们的要求选择方法。

## 3. 使用Eclipse 集合

我们还可以利用其他一些第三方库来实现我们的目标，无论是因为我们的应用程序不支持Java8，还是因为我们想利用Java未提供的一些强大功能。

Eclipse Collections就是这种情况 ，它是一个努力跟上新范例、发展和接受所有最新Java版本引入的变化的库。

我们可以从浏览我们的 [Eclipse Collections 介绍性帖子](https://www.baeldung.com/eclipse-collections)开始，以更广泛地了解该库提供的功能。

### 3.1. 依赖关系

让我们首先将以下依赖项添加到我们项目的pom.xml中：

```xml
<dependency>
    <groupId>org.eclipse.collections</groupId>
    <artifactId>eclipse-collections</artifactId>
    <version>9.2.0</version>
</dependency>
```

[eclipse-collections](https://search.maven.org/classic/#search|gav|1|g%3A"org.eclipse.collections" AND a%3A"eclipse-collections")包括所有必要的 数据结构接口和 API 本身。

### 3.2. 使用Eclipse 集合过滤集合

现在让我们在它的一种数据结构上使用 eclipse 的过滤功能，比如它的 MutableList：

```xml
public Collection<Integer> findEvenNumbers(Collection<Integer> baseCollection) {
    Predicate<Integer> eclipsePredicate
      = item -> item % 2 == 0;
 
    Collection<Integer> filteredList = Lists.mutable
      .ofAll(baseCollection)
      .select(eclipsePredicate);

    return filteredList;
}
```

作为替代方案，我们可以使用 Iterate的 select() 静态方法来定义filteredList对象：

```java
Collection<Integer> filteredList
 = Iterate.select(baseCollection, eclipsePredicate);
```

## 4. 使用 Apache 的CollectionUtils

要开始使用 Apache 的CollectionUtils库，我们可以查看[这个简短的教程](https://www.baeldung.com/apache-commons-collection-utils)，其中介绍了它的用途。

然而，在本教程中，我们将重点关注其 filter() 实现。

### 4.1. 依赖关系

首先，我们需要在pom.xml文件中添加以下依赖项：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.2</version>
</dependency>
```

### 4.2. 使用CollectionUtils过滤集合

我们现在准备好使用CollectonUtils的方法：

```java
public Collection<Integer> findEvenNumbers(Collection<Integer> baseCollection) {
    Predicate<Integer> apachePredicate = item -> item % 2 == 0;

    CollectionUtils.filter(baseCollection, apachePredicate);
    return baseCollection;
}
```

我们必须考虑到此方法通过删除与条件不匹配的每个项目来修改baseCollection 。

这意味着基础 Collection 必须是可变的，否则会抛出异常。

## 5. 使用 Guava 的 Collections2

和以前一样，我们可以阅读我们之前的帖子 [“Guava 中的过滤和转换集合”](https://www.baeldung.com/guava-filter-and-transform-a-collection) 以获取有关此主题的更多信息。

### 5.1. 依赖关系

让我们首先在pom.xml文件中添加[此依赖项：](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

### 5.2. 使用Collections2过滤集合

正如我们所见，这种方法与上一节中遵循的方法非常相似：

```java
public Collection<Integer> findEvenNumbers(Collection<Integer> baseCollection) {
    Predicate<Integer> guavaPredicate = item -> item % 2 == 0;
        
    return Collections2.filter(baseCollection, guavaPredicate);
}
```

同样，这里我们定义了一个特定于 Guava 的Predicate 对象。

在这种情况下，Guava 不会修改 baseCollection，它会生成一个新的，因此我们可以使用不可变集合作为输入。

## 六，总结

总之，我们已经看到在Java中有许多不同的过滤集合的方法。

尽管 Streams 通常是首选方法，但最好了解并牢记其他库提供的功能。

特别是如果我们需要支持旧的Java版本。但是，如果是这种情况，我们需要记住整个教程中使用的最新Java特性，例如 lambda，应该用匿名类代替。