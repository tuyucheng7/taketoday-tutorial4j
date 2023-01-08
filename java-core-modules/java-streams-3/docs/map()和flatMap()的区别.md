## 1. 概述

map()和flatMap() API 源于函数式语言。在Java8 中，我们可以在Optional、Stream和CompletableFuture中找到它们(尽管名称略有不同)。

流表示一系列对象，而可选项是表示可以存在或不存在的值的类。在其他聚合操作中，我们有map()和flatMap()方法。

尽管两者具有相同的返回类型，但它们却完全不同。让我们通过分析流和可选项的一些示例来解释这些差异。

## 延伸阅读：

## [在Java中遍历 Map](https://www.baeldung.com/java-iterate-map)

了解在Java中循环访问 Map 条目的不同方法。

[阅读更多](https://www.baeldung.com/java-iterate-map)→

## [使用 Jackson 进行地图序列化和反序列化](https://www.baeldung.com/jackson-map)

使用 Jackson 序列化和反序列化JavaMaps 的快速实用指南。

[阅读更多](https://www.baeldung.com/jackson-map)→

## [如何在Java中的映射中存储重复的键？](https://www.baeldung.com/java-map-duplicate-keys)

在Java中使用多重映射处理重复键的快速实用指南。

[阅读更多](https://www.baeldung.com/java-map-duplicate-keys)→

## 2.Optionals中的Map和Flatmap

map()方法与Optional一起工作得很好——如果函数返回我们需要的确切类型：

```java
Optional<String> s = Optional.of("test");
assertEquals(Optional.of("TEST"), s.map(String::toUpperCase));
```

然而，在更复杂的情况下，我们可能会得到一个也返回Optional的函数。在这种情况下，使用map()会导致嵌套结构，因为map()实现会在内部进行额外的包装。

让我们看另一个例子来更好地理解这种情况：

```java
assertEquals(Optional.of(Optional.of("STRING")), 
  Optional
  .of("string")
  .map(s -> Optional.of("STRING")));
```

如我们所见，我们最终得到了嵌套结构Optional<Optional<String>>。虽然它有效，但使用起来非常麻烦，并且不提供任何额外的 null 安全性，因此最好保持扁平结构。

这正是flatMap()帮助我们做的事情：

```java
assertEquals(Optional.of("STRING"), Optional
  .of("string")
  .flatMap(s -> Optional.of("STRING")));
```

## 3. Streams中的Map和Flatmap

这两种方法对于Optional的工作方式相似。

map()方法将底层序列包装在Stream实例中，而flatMap()方法允许避免嵌套Stream<Stream<R>>结构。

在这里，map()生成一个Stream ，该 Stream由将toUpperCase()方法应用于输入Stream的元素的结果组成：

```java
List<String> myList = Stream.of("a", "b")
  .map(String::toUpperCase)
  .collect(Collectors.toList());
assertEquals(asList("A", "B"), myList);
```

map()在这种简单的情况下工作得很好。但是如果我们有更复杂的东西，比如作为输入的列表列表呢？

让我们看看它是如何工作的：

```java
List<List<String>> list = Arrays.asList(
  Arrays.asList("a"),
  Arrays.asList("b"));
System.out.println(list);
```

此代码段打印列表[[a], [b]]的列表。

现在让我们使用flatMap()：

```java
System.out.println(list
  .stream()
  .flatMap(Collection::stream)
  .collect(Collectors.toList()));
```

这样一个片段的结果将被展平为[a, b]。

flatMap ()方法首先将输入的Stream of Streams扁平化为Stream of Strings(有关扁平化的更多信息，请参阅[本文](https://www.baeldung.com/java-flatten-nested-collections))。此后，它的工作方式类似于map()方法。

## 4。总结

Java 8 让我们有机会使用最初在函数式语言中使用的map()和flatMap()方法。

我们可以在Streams和 Optionals 上调用它们。这些方法帮助我们通过应用提供的映射函数来获取映射对象。