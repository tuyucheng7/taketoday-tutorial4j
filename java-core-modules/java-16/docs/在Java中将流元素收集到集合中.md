## 1. 概述

在本教程中，我们介绍将Stream转化成List的不同方法，并讨论它们之间的差异以及何时使用哪种方法。

## 2. 将流元素收集到集合中

从Stream中获取List是流管道最常用的[终端操作](https://www.baeldung.com/java-8-streams#pipeline)，在Java 16之前，我们习惯于调用Stream.collect()方法并将Collector作为其参数传递以将元素收集到其中，Collector本身是通过调用[Collectors.toList()](https://www.baeldung.com/java-8-collectors)方法创建的。

但是，有人[请求更改](https://bugs-stage.openjdk.java.net/browse/JDK-8256441)直接从Stream实例获取List的方法。**在Java 16发布之后，我们现在可以直接在Stream上调用一个新方法**[toList()](https://www.baeldung.com/java-stream-immutable-collection#3-using-streamtolist-method)**来获取List**。像[StreamEx](https://www.baeldung.com/streamex)这样的库也提供了一种直接从Stream获取List的便捷方法。

我们可以使用以下方法将流元素累积到List中：

-   Stream.collect(Collectors.toList())：从Java 8开始
-   Stream.collect([Collectors.toUnmodifiableList()](https://www.baeldung.com/java-stream-immutable-collection#1-using-javas-tounmodifiablelist))：从Java 10开始
-   Stream.toList()：从Java 16开始

下面，我们将按照发布的时间顺序使用这些方法。

## 3. 分析集合

让我们首先从上一节中描述的方法创建列表。之后，让我们分析它们的属性。

我们将在所有示例中使用以下国家/地区代码流：

```java
Stream.of(Locale.getISOCountries());
```

### 3.1 创建集合

现在，我们将使用不同的方法从给定的国家/地区代码流创建一个列表。

首先，我们使用Collectors:toList()创建一个集合：

```java
List<String> result = Stream.of(Locale.getISOCountries()).collect(Collectors.toList());
```

然后，使用Collectors.toUnmodifiableList()收集它：

```java
List<String> result = Stream.of(Locale.getISOCountries()).collect(Collectors.toUnmodifiableList());
```

在这些方法中，我们通过Collector接口将Stream累积成一个List，这会导致额外的分配和复制，因为我们不直接使用Stream。

然后，让我们使用Stream.toList()完成收集：

```java
List<String> result = Stream.of(Locale.getISOCountries()).toList();
```

在这里，我们直接从Stream中获取List，从而避免了额外的分配和复制。

**因此，与其他两种方法调用相比，直接在Stream上使用toList()更加简洁、方便和最佳**。

### 3.2 检查累积集合

让我们从检查我们创建的列表类型开始。

Collectors.toList()，将Stream元素收集到ArrayList中：

```java
java.util.ArrayList
```

Collectors.toUnmodifiableList()，将Stream元素收集到不可修改的List中。

```java
java.util.ImmutableCollections.ListN
```

Stream.toList()，将元素收集到不可修改的List中。

```java
java.util.ImmutableCollections.ListN
```

**尽管Collectors.toList()的当前实现创建的是一个可变的List，但该方法的规范本身并不保证List的类型、可变性、可序列化性或线程安全性**。

另一方面，Collectors.toUnmodifiableList()和Stream.toList()都生成不可修改的集合。

**这意味着我们可以对Collectors.toList()的元素进行添加和排序等操作，但不能对Collectors.toUnmodifiableList()和Stream.toList()的元素进行操作**。 

### 3.3 允许集合中的空元素

尽管Stream.toList()生成一个不可修改的List，但它仍然与Collectors.toUnmodifiableList()不同。这是因为Stream.toList()允许空元素，而Collectors.toUnmodifiableList()不允许空元素。但是，Collectors.toList()允许空元素。

当收集包含空元素的Stream时，Collectors.toList()不会抛出异常：

```java
Assertions.assertDoesNotThrow(() -> {
    Stream.of(null, null).collect(Collectors.toList());
});
```

当收集包含空元素的Stream时，Collectors.toUnmodifiableList()会抛出NulPointerException：

```java
Assertions.assertThrows(NullPointerException.class, () -> {
    Stream.of(null, null).collect(Collectors.toUnmodifiableList());
});
```

当收集包含空元素的Stream时，Stream.toList()不会抛出NulPointerException：

```java
Assertions.assertDoesNotThrow(() -> {
    Stream.of(null,null).toList();
});
```

**因此，在将我们的代码从Java 8迁移到Java 10或Java 16时，这是需要注意的。我们不能盲目地使用Stream.toList()来代替Collectors.toList()或Collectors.toUnmodifiableList()**。

### 3.4 分析总结

下表总结了我们分析中集合的差异和相似之处：

<img src="../assets/img.png">

## 4. 何时使用不同的 toList()方法

新增Stream.toList()的主要目的是减少Collector API的冗长，如前所示，使用Collectors方法获取List非常冗长。另一方面，使用Stream.toList()方法可以使代码简洁明了。

尽管如此，如前几节所示，Stream.toList()不能用作Collectors.toList()或Collectors.toUnmodifiableList()的快捷方式。

其次，Stream.toList()使用较少的内存，因为它的[实现独立于Collector接口](https://blogs.oracle.com/javamagazine/post/the-hidden-gems-in-java-16-and-java-17-from-streammapmulti-to-hexformat)。它将Stream元素直接累积到List中，因此，如果我们事先知道流的大小，最好使用Stream.toList()。

第三，我们知道Stream API只提供了toList()方法的实现，它不包含用于获取Map或Set的类似方法。因此，如果我们想要一种统一的方法来获取List、Map或Set等转换器，我们可以继续使用Collector API，这可以保持一致性并避免混淆。

最后，如果我们使用低于Java 16的版本，我们必须继续使用Collectors方法。

下表总结了给定方法的最佳用法：

<img src="../assets/img_1.png">

## 5. 总结

在本文中，我们分析了三种最流行的从Stream中获取List的方法。然后，我们介绍了它们主要的区别和相似之处，并且还讨论了如何以及何时使用这些方法。