---
layout: post
title:  在Java中合并流
category: libraries
copyright: libraries
excerpt: Java Stream
---

## 1. 概述

在这篇简短的文章中，我们解释了合并Java Stream的不同方式-这不是一个非常直观的操作。

## 2. 使用纯Java

JDK 8 Stream类有一些有用的静态实用方法，让我们仔细看看concat()方法。

### 2.1 合并两个流

组合2个Stream的最简单方法是使用静态Stream.concat()方法：

```java
@Test
public void whenMergingStreams_thenResultStreamContainsElementsFromBoth() {
    Stream<Integer> stream1 = Stream.of(1, 3, 5);
    Stream<Integer> stream2 = Stream.of(2, 4, 6);

    Stream<Integer> resultingStream = Stream.concat(stream1, stream2);

    assertEquals(
        Arrays.asList(1, 3, 5, 2, 4, 6),
        resultingStream.collect(Collectors.toList()));
}
```

### 2.2 合并多个流

当我们需要合并超过2个Stream时，事情会变得更加复杂。一种可能性是拼接前两个流，然后将结果与下一个流拼接，依此类推。

下一个代码片段展示了这一点：

```java
@Test
public void given3Streams_whenMerged_thenResultStreamContainsAllElements() {
    Stream<Integer> stream1 = Stream.of(1, 3, 5);
    Stream<Integer> stream2 = Stream.of(2, 4, 6);
    Stream<Integer> stream3 = Stream.of(18, 15, 36);

    Stream<Integer> resultingStream = Stream.concat(
      Stream.concat(stream1, stream2), stream3);

    assertEquals(
        Arrays.asList(1, 3, 5, 2, 4, 6, 18, 15, 36),
        resultingStream.collect(Collectors.toList()));
}
```

正如我们所看到的，这种方法对于更多流变得不可行。当然，我们可以创建中间变量或辅助方法来使其更具可读性，但这里有一个更好的选择：

```java
@Test
public void given4Streams_whenMerged_thenResultStreamContainsAllElements() {
    Stream<Integer> stream1 = Stream.of(1, 3, 5);
    Stream<Integer> stream2 = Stream.of(2, 4, 6);
    Stream<Integer> stream3 = Stream.of(18, 15, 36);
    Stream<Integer> stream4 = Stream.of(99);

    Stream<Integer> resultingStream = Stream.of(stream1, stream2, stream3, stream4)
        .flatMap(i -> i);

    assertEquals(
        Arrays.asList(1, 3, 5, 2, 4, 6, 18, 15, 36, 99),
        resultingStream.collect(Collectors.toList()));
}
```

这里发生的是：

-   我们首先创建一个包含4个流的新Stream，结果是Stream<Stream<Integer\>>
-   然后我们使用identity函数将flatMap()转换为Stream<Integer\>

## 3. 使用StreamEx

[StreamEx](https://github.com/amaembo/streamex)是一个开源Java库，它扩展了Java 8 Stream的可能性。它使用StreamEx类作为对JDK的Stream接口的增强。

### 3.1 合并流

StreamEx库允许我们使用append()实例方法合并流：

```java
@Test
public void given4Streams_whenMerged_thenResultStreamContainsAllElements() {
    Stream<Integer> stream1 = Stream.of(1, 3, 5);
    Stream<Integer> stream2 = Stream.of(2, 4, 6);
    Stream<Integer> stream3 = Stream.of(18, 15, 36);
    Stream<Integer> stream4 = Stream.of(99);

    Stream<Integer> resultingStream = StreamEx.of(stream1)
        .append(stream2)
        .append(stream3)
        .append(stream4);

    assertEquals(
        Arrays.asList(1, 3, 5, 2, 4, 6, 18, 15, 36, 99),
        resultingStream.collect(Collectors.toList()));
}
```

由于它是一个实例方法，我们可以轻松地将它链接起来并附加多个流。

请注意，如果我们将resultingStream变量指定为StreamEx类型，我们也可以使用toList()从流中创建一个List。

### 3.2 使用prepend()合并流

StreamEx还包含一种在另一个元素之前添加元素的方法，称为prepend()：

```java
@Test
public void given3Streams_whenPrepended_thenResultStreamContainsAllElements() {
    Stream<String> stream1 = Stream.of("foo", "bar");
    Stream<String> openingBracketStream = Stream.of("[");
    Stream<String> closingBracketStream = Stream.of("]");

    Stream<String> resultingStream = StreamEx.of(stream1)
        .append(closingBracketStream)
        .prepend(openingBracketStream);

    assertEquals(
        Arrays.asList("[", "foo", "bar", "]"),
        resultingStream.collect(Collectors.toList()));
}
```

## 4. 使用Jooλ

[jOOλ](https://github.com/jOOQ/jOOL)是一个JDK 8兼容库，它为JDK提供了有用的扩展。这里最重要的流抽象称为Seq。请注意，这是一个顺序流和有序流，因此调用parallel()将不起作用。

### 4.1 合并流

就像StreamEx库一样，jOOλ有一个append()方法：

```java
@Test
public void given2Streams_whenMerged_thenResultStreamContainsAllElements() {
    Stream<Integer> seq1 = Stream.of(1, 3, 5);
    Stream<Integer> seq2 = Stream.of(2, 4, 6);

    Stream<Integer> resultingSeq = Seq.ofType(seq1, Integer.class)
        .append(seq2);

    assertEquals(
        Arrays.asList(1, 3, 5, 2, 4, 6),
        resultingSeq.collect(Collectors.toList()));
}
```

此外，如果我们将resultingSeq变量指定为jOOλ Seq类型，则有一个方便的toList()方法。

### 4.2 使用prepend()合并流

正如预期的那样，由于存在append()方法，因此在jOOλ中也有一个prepend()方法：

```java
@Test
public void given3Streams_whenPrepending_thenResultStreamContainsAllElements() {
    Stream<String> seq = Stream.of("foo", "bar");
    Stream<String> openingBracketSeq = Stream.of("[");
    Stream<String> closingBracketSeq = Stream.of("]");

    Stream<String> resultingStream = Seq.ofType(seq, String.class)
        .append(closingBracketSeq)
        .prepend(openingBracketSeq);

    Assert.assertEquals(
        Arrays.asList("[", "foo", "bar", "]"),
        resultingStream.collect(Collectors.toList()));
}
```

## 5. 总结

我们看到使用JDK 8合并流相对简单。当我们需要进行大量合并时，为了可读性，使用StreamEx或jOOλ库可能是有益的。

与往常一样，本教程的完整源代码可在[GitHub](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/opensource-libraries/libraries-1)上获得。