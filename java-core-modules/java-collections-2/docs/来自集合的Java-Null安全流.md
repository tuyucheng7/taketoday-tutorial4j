## 1. 概述

在本教程中，我们将学习如何从Java集合创建空安全流。

要完全理解本材料，需要对Java8 的方法引用、Lambda 表达式、可选和 Stream API 有一定的了解。

如果你不熟悉这些主题中的任何一个，可以先看看这些以前的文章：[Java 8 中的新功能、](https://www.baeldung.com/java-8-new-features)Java[8 可选指南](https://www.baeldung.com/java-optional)和[Java 8 流简介](https://www.baeldung.com/java-8-streams-introduction)。

## 2.Maven依赖

在我们开始之前，我们需要一个 Maven 依赖项以用于某些场景：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.2</version>
</dependency>
```

[commons-collections4](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.commons" AND a%3A"commons-collections4")库可以从 Maven Central 下载。

## 3. 从集合创建流

从任何类型的Collection创建[Stream](https://www.baeldung.com/java-8-streams-introduction)的基本方法是调用集合的stream()或parallelStream()方法，具体取决于所需的流类型：

```java
Collection<String> collection = Arrays.asList("a", "b", "c");
Stream<String> streamOfCollection = collection.stream();

```

我们的收藏很可能在某个时候有外部资源。当从集合中创建流时，我们可能会以类似于下面的方法结束：

```java
public Stream<String> collectionAsStream(Collection<String> collection) {
    return collection.stream();
}

```

这可能会导致一些问题。当提供的集合指向空引用时，代码将在运行时抛出 NullPointerException。

下一节将介绍我们如何防止这种情况发生。

## 4. 使创建的集合流成为空安全的

### 4.1. 添加检查以防止空取消引用

为了防止意外的空指针异常，我们可以选择添加检查以防止在从集合创建流时出现空 引用：

```java
Stream<String> collectionAsStream(Collection<String> collection) {
    return collection == null 
      ? Stream.empty() 
      : collection.stream();
}

```

然而，这种方法有几个问题。

首先，空检查妨碍了业务逻辑，降低了程序的整体可读性。

其次，在JavaSE 8 之后，使用null来表示值的缺失被认为是错误的方法；有一种更好的方法来模拟值的缺失和存在。

请务必记住，空Collection与 null Collection不同。第一个表示我们的查询没有要显示的结果或元素，而第二个表示在此过程中刚刚发生了一种错误。

### 4.2. 使用CollectionUtils库中的 emptyIfNull方法

我们可以选择使用 Apache Commons 的[CollectionUtils](https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/CollectionUtils.html)库来确保我们的流是null安全的。这个库提供了一个emptyIfNull方法，它返回一个不可变的空集合，给定一个null集合作为参数，否则返回集合本身：

```java
public Stream<String> collectionAsStream(Collection<String> collection) {
    return emptyIfNull(collection).stream();
}

```

这是一个非常简单的策略；但是，它依赖于外部库。如果软件开发策略限制使用此类库，则此解决方案无效。

### 4.3. 使用Java8 的Optional

Java SE 8 的[Optional](https://www.baeldung.com/java-optional)是一个单值容器，它要么包含一个值，要么不包含一个值。如果缺少值，则称Optional容器为空。

使用Optional可以说是从流中创建空安全集合的最佳整体策略。

让我们看看如何使用它，然后在下面进行快速讨论：

```java
public Stream<String> collectionToStream(Collection<String> collection) {
    return Optional.ofNullable(collection)
      .map(Collection::stream)
      .orElseGet(Stream::empty);
}

```

-   Optional.ofNullable(collection)从传入的集合中创建一个Optional对象。如果集合为空，则创建一个空的Optional对象。
-   map(Collection::stream)提取Optional对象中包含的值作为map方法 ( Collection.stream() ) 的参数。
-   orElseGet(Stream::empty)在Optional对象为空的情况下返回回退值，即传入的集合为null。

因此，我们主动保护我们的代码免受意外的空指针异常。

### 4.4. 使用Java9 的Stream OfNullable

检查我们之前在 4.1 节中的三元示例，并考虑到某些元素可能为null而不是Collection的可能性，我们可以使用Stream 类中的ofNullable方法 。

我们可以将上面的例子改造成：

```java
Stream<String> collectionAsStream(Collection<String> collection) {  
  return collection.stream().flatMap(s -> Stream.ofNullable(s));
}
```

## 5.总结

在本文中，我们简要讨论了如何从给定集合创建流。然后，我们继续探索三个关键策略，以确保创建的流在从集合创建时是空安全的。

最后，我们指出了在相关情况下使用每种策略的缺点。