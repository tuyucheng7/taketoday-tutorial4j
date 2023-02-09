## 一、概述

[Stream API](https://www.baeldung.com/java-8-streams)是 Java 8 中的主要新特性之一。

在本教程中，我们将讨论一个有趣的话题：*Stream.of()*和[*IntStream.range()*](https://www.baeldung.com/java-listing-numbers-within-a-range#intstream)之间的区别。

## 二、问题简介

我们可以使用***Stream.of()\*****方法初始化\*Stream\*对象，例如\*Stream.of(1, 2, 3, 4, 5)\***。或者，如果我们想要初始化一个整数*Stream*，***[IntStream](https://www.baeldung.com/java-8-primitive-streams)\* 是一种更直接的类型，例如\*IntStream.range(1, 6)\***。但是，这两种方法创建的整数*Stream*的行为可能不同。

像往常一样，我们将通过一个例子来理解这个问题。首先，让我们以不同的方式创建两个*Stream ：*

```java
Stream<Integer> normalStream = Stream.of(1, 2, 3, 4, 5);
IntStream intStreamByRange = IntStream.range(1, 6);复制
```

接下来，我们将对上面的两个*Stream*执行相同的例程：

```java
STREAM.peek(add to a result list)
  .sorted()
  .findFirst();复制
```

*因此，我们在每个Stream*上调用三个方法：

-   首先——调用*[peek()](https://www.baeldung.com/java-streams-peek-api)*方法将处理过的元素收集到结果列表中
-   然后——对元素进行排序
-   *最后——从Stream*中取出第一个元素

由于两个*Stream*包含相同的整数元素，我们认为在执行后，两个结果列表也应该包含相同的整数。那么接下来，让我们编写一个测试来检查它是否产生了我们期望的结果：

```java
List<Integer> normalStreamPeekResult = new ArrayList<>();
List<Integer> intStreamPeekResult = new ArrayList<>();

// First, the regular Stream
normalStream.peek(normalStreamPeekResult::add)
  .sorted()
  .findFirst();
assertEquals(Arrays.asList(1, 2, 3, 4, 5), normalStreamPeekResult);

// Then, the IntStream
intStreamByRange.peek(intStreamPeekResult::add)
  .sorted()
  .findFirst();
assertEquals(Arrays.asList(1), intStreamPeekResult);复制
```

*执行后发现， normalStream.peek()*填充的结果列表包含所有整数元素。然而，由*intStreamByRange.peek()*填充的列表只有一个元素。

接下来，让我们弄清楚为什么它会这样工作。

## 3. 流是惰性的

在我们解释为什么两个*Stream*在前面的测试中产生不同的结果列表之前，让我们先了解一下 Java Streams 在设计上是惰性的。

“惰性”意味着*Stream*仅在被告知产生结果时才执行所需的操作。换句话说，**在执行终端操作之前，不会执行对 Stream 的中间操作。**这种惰性行为可能是一个优势，因为它允许更有效的处理并防止不必要的计算。

为了快速理解这种惰性行为，让我们暂时摆脱之前测试中的*sort()*方法调用并重新运行它：

```java
List<Integer> normalStreamPeekResult = new ArrayList<>();
List<Integer> intStreamPeekResult = new ArrayList<>();

// First, the regular Stream
normalStream.peek(normalStreamPeekResult::add)
  .findFirst();
assertEquals(Arrays.asList(1), normalStreamPeekResult);

// Then, the IntStream
intStreamByRange.peek(intStreamPeekResult::add)
  .findFirst();
assertEquals(Arrays.asList(1), intStreamPeekResult);复制
```

两个*Stream*这次都只填充了相应结果列表中的第一个元素。这是因为**findFirst [\*()\*](https://www.baeldung.com/java-stream-findfirst-vs-findany#usingstreamfindfirst)方法是终端操作，只需要一个元素——第一个。**

现在我们了解了*Stream*是惰性的，接下来，让我们弄清楚为什么当*sorted()*方法加入聚会时两个结果列表不同。

## 4. 调用*sorted()* 可能会将*流*变成“Eager”

首先，让我们看一下*Stream.of()*初始化的*Stream*。终端操作*findFirst()只需要**Stream*中的第一个整数。但它是***sorted()\*****操作****之后的第一个**。

我们知道必须遍历所有整数才能对它们进行排序。因此，调用*sorted()*已将*Stream*变为“eager”。因此，**每个元素都会调用\*peek()方法。\***

另一方面， ***IntStream.range()\*返回顺序排序的\*IntStream\***。也就是说，*IntStream*对象的输入已经排序了。此外，**当它对已经排序的输入进行排序时，Java 应用优化以[使\*sorted()\*操作成为无操作](https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/stream/SortedOps.java#L136)。** 因此，结果列表中仍然只有一个元素。

接下来，让我们看另一个基于*TreeSet的**Stream*示例：

```java
List<String> peekResult = new ArrayList<>();

TreeSet<String> treeSet = new TreeSet<>(Arrays.asList("CCC", "BBB", "AAA", "DDD", "KKK"));

treeSet.stream()
  .peek(peekResult::add)
  .sorted()
  .findFirst();

assertEquals(Arrays.asList("AAA"), peekResult);复制
```

我们知道*TreeSet*是一个有序的集合。因此，我们看到 *peekResult*列表只包含一个字符串，尽管我们调用了*sorted()*。

## 5.结论

在本文中，我们以*Stream.of()*和*IntStream.range()*为例来了解调用*sorted()*可能会使 Stream 从“懒惰”变为“急切” *。*