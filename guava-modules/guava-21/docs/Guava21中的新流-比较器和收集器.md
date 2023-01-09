## 1. 简介

本文是关于 Google Guava 库版本 21 推出的新功能系列的第一篇。我们将讨论新添加的类以及与以前版本的 Guava 相比的一些主要变化。

更具体地说，我们将讨论common.collect包中的添加和更改。

Guava 21 在common.collect包中引入了一些新的有用的功能；让我们快速浏览一下其中的一些新实用程序，以及如何充分利用它们。

## 2.溪流

我们都对Java8 中最新添加的java.util.stream.Stream感到兴奋。好吧，Guava 现在可以很好地利用流并提供 Oracle 可能错过的功能。

Streams是一个静态实用程序类，具有一些处理Java8 流所需的实用程序。

### 2.1. Streams.stream()

Streams类提供了四种使用Iterable、Iterator、Optional和Collection创建流的方法。

但是，不推荐使用Collection创建流，因为它由Java8 开箱即用地提供：

```java
List<Integer> numbers = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
Stream<Integer> streamFromCollection = Streams.stream(numbers);
Stream<Integer> streamFromIterator = Streams.stream(numbers.iterator());
Stream<Integer> streamFromIterable = Streams.stream((Iterable<Integer>) numbers);
Stream<Integer> streamFromOptional = Streams.stream(Optional.of(1));

```

Streams类还提供带有OptionalDouble、OptionalLong和OptionalInt的风格。这些方法返回一个仅包含该元素的流，否则为空流：

```java
LongStream streamFromOptionalLong = Streams.stream(OptionalLong.of(1));
IntStream streamFromOptionalInt = Streams.stream(OptionalInt.of(1));
DoubleStream streamFromOptionalDouble = Streams.stream(OptionalDouble.of(1.0));
```

### 2.2. 流.concat()

Streams类提供了连接多个同类流的方法。

```java
Stream<Integer> concatenatedStreams = Streams.concat(streamFromCollection, streamFromIterable,streamFromIterator);
```

concat功能有几种风格—— LongStream、IntStream和DoubleStream。

### 2.3. 流.findLast()

Streams有一个实用方法，可以使用findLast()方法查找流中的最后一个元素。

如果流中没有元素，则此方法返回最后一个元素或Optional.empty() ：

```java
List<Integer> integers = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
Optional<Integer> lastItem = Streams.findLast(integers.stream());
```

findLast ()方法适用于LongStream、IntStream和DoubleStream。

### 2.4. Streams.mapWithIndex()

通过使用mapWithIndex()方法，流的每个元素都携带有关其各自位置(索引)的信息：

```java
mapWithIndex( Stream.of("a", "b", "c"), (str, index) -> str + ":" + index)
```

这将返回Stream.of(“a:0″,”b:1″,”c:2”)。

使用重载的 mapWithIndex()可以通过IntStream、LongStream和DoubleStream实现相同的效果。

### 2.5. Streams.zip()

为了使用某些函数映射两个流的相应元素，只需使用Streams 的 zip 方法：

```java
Streams.zip(
  Stream.of("candy", "chocolate", "bar"),
  Stream.of("$1", "$2","$3"),
  (arg1, arg2) -> arg1 + ":" + arg2
);
```

这将返回Stream.of(“candy:$1″,”chocolate:$2″,”bar:$3”);

结果流将仅与两个输入流中较短的一个一样长；如果一个流更长，它的额外元素将被忽略。

## 3.比较器

Guava Ordering类已弃用，并且在较新版本中处于删除阶段。JDK 8 中已经包含了Ordering类的大部分功能。

Guava 引入了Comparators以提供Java 8 标准库尚未提供的额外排序功能。

让我们快速浏览一下这些。

### 3.1. 比较器.isInOrder()

如果 Iterable 中的每个元素都大于或等于前一个元素，则此方法返回 true，如Comparator所指定：

```java
List<Integer> integers = Arrays.asList(1,2,3,4,4,6,7,8,9,10);
boolean isInAscendingOrder = Comparators.isInOrder(
  integers, new AscedingOrderComparator());
```

### 3.2. 比较器.isInStrictOrder()

与isInOrder()方法非常相似，但它严格遵守条件，元素不能等于前一个，它必须大于。对于此方法，前面的代码将返回 false。

### 3.3. Comparators.lexicographical()

此 API 返回一个新的Comparator实例——它按词典(字典)顺序排序，比较相应的元素成对。在内部，它创建了LexicographicalOrdering<S>()的一个新实例。

## 4.更多收藏家

MoreCollectors包含一些非常有用的收集器，它们在Java8 java.util.stream.Collectors中不存在，并且与com.google.common类型无关。

让我们来看看其中的一些。

### 4.1. MoreCollectors.toOptional()

在这里，Collector将包含零个或一个元素的流转换为Optional：

```java
List<Integer> numbers = Arrays.asList(1);
Optional<Integer> number = numbers.stream()
  .map(e -> e  2)
  .collect(MoreCollectors.toOptional());
```

如果流包含多个元素——收集器将抛出IllegalArgumentException。


### 4.2. MoreCollectors.onlyElement()

使用此 API，收集器获取仅包含一个元素的流并返回该元素；如果流包含多个元素，则抛出IllegalArgumentException；如果流包含零个元素，则抛出NoSuchElementException。

## 5.实习生.InternerBuilder

这是Guava 库中已经存在的实习生的内部构建器类。它提供了一些方便的方法来定义你喜欢的Interner的并发级别和类型(弱或强) ：

```java
Interners interners = Interners.newBuilder()
  .concurrencyLevel(2)
  .weak()
  .build();
```

## 六. 总结

在这篇快速文章中，我们探讨了Guava 21的common.collect 包中新增的功能。