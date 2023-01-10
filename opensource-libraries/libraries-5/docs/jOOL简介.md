## 1. 概述

在本文中，我们将研究[jOOL库](https://github.com/jOOQ/jOOL)[——jOOQ](https://www.jooq.org/) 的另一个产品。

## 2.Maven依赖

让我们从向的pom.xml添加 Maven 依赖项开始：

```xml
<dependency>
    <groupId>org.jooq</groupId>
    <artifactId>jool</artifactId>
    <version>0.9.12</version>
</dependency>

```

[可以在此处](https://search.maven.org/classic/#search|gav|1|g%3A"org.jooq" AND a%3A"jool")找到最新版本。

## 3.功能接口

在Java8 中，功能接口非常有限。它们接受最大数量的两个参数并且没有很多附加功能。

jOOL 通过证明一组新的功能接口来解决这个问题，这些接口甚至可以接受 16 个参数(从[Function1](http://www.jooq.org/products/jOOλ/javadoc/0.9.11/index.html?org/jooq/lambda/function/Function1.html) 到[Function16](http://www.jooq.org/products/jOOλ/javadoc/0.9.11/index.html?org/jooq/lambda/function/Function16.html))并通过其他方便的方法进行丰富。

例如，要创建一个接受三个参数的函数，我们可以使用Function3：

```java
Function3<String, String, String, Integer> lengthSum
  = (v1, v2, v3) -> v1.length() + v2.length() + v3.length();
```

在纯Java中，需要自己实现它。除此之外，来自 jOOL 的功能接口有一个方法applyPartially()允许我们轻松地执行部分应用程序：

```java
Function2<Integer, Integer, Integer> addTwoNumbers = (v1, v2) -> v1 + v2;
Function1<Integer, Integer> addToTwo = addTwoNumbers.applyPartially(2);

Integer result = addToTwo.apply(5);

assertEquals(result, (Integer) 7);
```

当我们有一个Function2类型的方法时，我们可以使用toBiFunction()方法轻松地将它转换为标准的JavaBiFunction ：

```java
BiFunction biFunc = addTwoNumbers.toBiFunction();
```

同样， Function1类型中有一个toFunction()方法。

## 4. 元组

元组是函数式编程世界中非常重要的结构。它是一个类型化的值容器，其中每个值都可以有不同的类型。元组通常用作函数参数。

在对事件流进行转换时，它们也非常有用。在 jOOL 中，我们有元组，可以包装 1 到 16 个值，由Tuple1提供到Tuple16类型：

```java
tuple(2, 2)
```

对于四个值：

```java
tuple(1,2,3,4);

```

让我们考虑一个例子，当我们有一个包含 3 个值的元组序列时：

```java
Seq<Tuple3<String, String, Integer>> personDetails = Seq.of(
  tuple("michael", "similar", 49),
  tuple("jodie", "variable", 43));
Tuple2<String, String> tuple = tuple("winter", "summer");

List<Tuple4<String, String, String, String>> result = personDetails
  .map(t -> t.limit2().concat(tuple)).toList();

assertEquals(
  result,
  Arrays.asList(tuple("michael", "similar", "winter", "summer"), tuple("jodie", "variable", "winter", "summer"))
);
```

我们可以对元组使用不同类型的转换。首先，我们调用limit2()方法只从Tuple3 中获取两个值。然后，我们调用concat()方法来连接两个元组。

结果，我们得到了Tuple4类型的值。

## 5.序列 

Seq构造在Stream上添加更高级别的方法，同时经常在下面使用它的方法。

### 5.1. 包含操作

我们可以找到几种检查Seq 中是否存在元素的方法变体。其中一些方法使用Stream类中的anyMatch()方法：

```java
assertTrue(Seq.of(1, 2, 3, 4).contains(2));

assertTrue(Seq.of(1, 2, 3, 4).containsAll(2, 3));

assertTrue(Seq.of(1, 2, 3, 4).containsAny(2, 5));

```

### 5.2. 加盟运营

当我们有两个流并且我们想要连接它们时(类似于两个数据集的 SQL 连接操作)，使用标准Stream类并不是一种非常优雅的方式来做到这一点：

```java
Stream<Integer> left = Stream.of(1, 2, 4);
Stream<Integer> right = Stream.of(1, 2, 3);

List<Integer> rightCollected = right.collect(Collectors.toList());
List<Integer> collect = left
  .filter(rightCollected::contains)
  .collect(Collectors.toList());

assertEquals(collect, Arrays.asList(1, 2));
```

我们需要将正确的流收集到一个列表中，以防止java.lang.IllegalStateException: stream has already been operationed or closed。接下来，我们需要通过从过滤器方法访问rightCollected列表来进行副作用操作。加入两个数据集很容易出错而且不是优雅的方式。

幸运的是，Seq有一些有用的方法可以对数据集进行内联、左联和右联。这些方法隐藏了它的实现，暴露了优雅的 API。

我们可以使用innerJoin()方法进行内部连接：

```java
assertEquals(
  Seq.of(1, 2, 4).innerJoin(Seq.of(1, 2, 3), (a, b) -> a == b).toList(),
  Arrays.asList(tuple(1, 1), tuple(2, 2))
);
```

我们可以相应地进行右连接和左连接：

```java
assertEquals(
  Seq.of(1, 2, 4).leftOuterJoin(Seq.of(1, 2, 3), (a, b) -> a == b).toList(),
  Arrays.asList(tuple(1, 1), tuple(2, 2), tuple(4, null))
);

assertEquals(
  Seq.of(1, 2, 4).rightOuterJoin(Seq.of(1, 2, 3), (a, b) -> a == b).toList(),
  Arrays.asList(tuple(1, 1), tuple(2, 2), tuple(null, 3))
);
```

甚至有一个crossJoin()方法可以对两个数据集进行笛卡尔连接：

```java
assertEquals(
  Seq.of(1, 2).crossJoin(Seq.of("A", "B")).toList(),
  Arrays.asList(tuple(1, "A"), tuple(1, "B"), tuple(2, "A"), tuple(2, "B"))
);
```

### 5.3. 操作序列

Seq有许多用于操作元素序列的有用方法。让我们看看其中的一些。

我们可以使用cycle()方法从源序列中重复获取元素。它将创建一个无限流，因此我们在将结果收集到列表时需要小心，因此我们需要使用limit()方法将无限序列转换为有限序列：

```java
assertEquals(
  Seq.of(1, 2, 3).cycle().limit(9).toList(),
  Arrays.asList(1, 2, 3, 1, 2, 3, 1, 2, 3)
);
```

假设我们要将一个序列中的所有元素到第二个序列。duplicate()方法正是这样做的：

```java
assertEquals(
  Seq.of(1, 2, 3).duplicate().map((first, second) -> tuple(first.toList(), second.toList())),
  tuple(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3))
);

```

duplicate()方法的返回类型是两个序列的元组。

假设我们有一个整数序列，我们想使用一些谓词将该序列分成两个序列。我们可以使用partition()方法：

```java
assertEquals(
  Seq.of(1, 2, 3, 4).partition(i -> i > 2)
    .map((first, second) -> tuple(first.toList(), second.toList())),
  tuple(Arrays.asList(3, 4), Arrays.asList(1, 2))
);
```

### 5.4. 分组元素

使用Stream API 按键对元素进行分组既麻烦又不直观——因为我们需要将collect()方法与Collectors.groupingBy收集器一起使用。

Seq将该代码隐藏在返回Map的groupBy()方法后面，因此无需显式使用collect()方法：

```java
Map<Integer, List<Integer>> expectedAfterGroupBy = new HashMap<>();
expectedAfterGroupBy.put(1, Arrays.asList(1, 3));
expectedAfterGroupBy.put(0, Arrays.asList(2, 4));

assertEquals(
  Seq.of(1, 2, 3, 4).groupBy(i -> i % 2),
  expectedAfterGroupBy
);
```

### 5.5. 跳过元素

假设我们有一个元素序列，我们想在谓词不匹配时跳过元素。当满足谓词时，元素应该落在结果序列中。

我们可以为此使用skipWhile()方法：

```java
assertEquals(
  Seq.of(1, 2, 3, 4, 5).skipWhile(i -> i < 3).toList(),
  Arrays.asList(3, 4, 5)
);
```

我们可以使用skipUntil()方法获得相同的结果：

```java
assertEquals(
  Seq.of(1, 2, 3, 4, 5).skipUntil(i -> i == 3).toList(),
  Arrays.asList(3, 4, 5)
);
```

### 5.6. 压缩序列

当我们处理元素序列时，通常需要将它们压缩成一个序列。

可用于将两个序列压缩为一个的zip() API：

```java
assertEquals(
  Seq.of(1, 2, 3).zip(Seq.of("a", "b", "c")).toList(),
  Arrays.asList(tuple(1, "a"), tuple(2, "b"), tuple(3, "c"))
);
```

结果序列包含两个元素的元组。

当我们压缩两个序列时，但我们想以特定方式压缩它们时，我们可以将BiFunction传递给定义压缩元素方式的zip()方法：

```java
assertEquals(
  Seq.of(1, 2, 3).zip(Seq.of("a", "b", "c"), (x, y) -> x + ":" + y).toList(),
  Arrays.asList("1:a", "2:b", "3:c")
);
```

有时，通过zipWithIndex() API压缩带有此序列中元素索引的序列很有用：

```java
assertEquals(
  Seq.of("a", "b", "c").zipWithIndex().toList(),
  Arrays.asList(tuple("a", 0L), tuple("b", 1L), tuple("c", 2L))
);
```

## 6. 将已检查异常转换为未检查异常

假设我们有一个方法，它接受一个字符串并可以抛出一个已检查的异常：

```java
public Integer methodThatThrowsChecked(String arg) throws Exception {
    return arg.length();
}
```

然后我们想映射Stream的元素，将该方法应用于每个元素。没有办法更高地处理该异常，因此我们需要在map()方法中处理该异常：

```java
List<Integer> collect = Stream.of("a", "b", "c").map(elem -> {
    try {
        return methodThatThrowsChecked(elem);
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException(e);
    }
}).collect(Collectors.toList());

assertEquals(
    collect,
    Arrays.asList(1, 1, 1)
);
```

由于Java中函数式接口的设计，我们对该异常无能为力，因此在 catch 子句中，我们将已检查的异常转换为未检查的异常。

幸运的是，在 jOOL 中有一个Unchecked类，它具有可以将已检查异常转换为未检查异常的方法：

```java
List<Integer> collect = Stream.of("a", "b", "c")
  .map(Unchecked.function(elem -> methodThatThrowsChecked(elem)))
  .collect(Collectors.toList());

assertEquals(
  collect,
  Arrays.asList(1, 1, 1)
);
```

我们将对 methodThatThrowsChecked()的调用包装到Unchecked.function()方法中，该方法处理下面的异常转换。

## 七. 总结

本文介绍如何使用 jOOL 库，该库将有用的附加方法添加到Java标准Stream API。