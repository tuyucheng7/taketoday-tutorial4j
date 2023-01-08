## 1. 概述

Java 8 Stream API 引入了两个经常被误解的方法：findAny()和findFirst()。

在本快速教程中，我们将了解这两种方法之间的区别以及何时使用它们。

## 延伸阅读：

## [在Java中过滤 Optional 流](https://www.baeldung.com/java-filter-stream-of-optional)

在Java8 和Java9 中过滤可选流的快速实用指南

[阅读更多](https://www.baeldung.com/java-filter-stream-of-optional)→

## [Java 8 中的原始类型流](https://www.baeldung.com/java-8-primitive-streams)

使用原始类型的Java8 Streams 的快速实用指南。

[阅读更多](https://www.baeldung.com/java-8-primitive-streams)→

## [可迭代到Java中的流](https://www.baeldung.com/java-iterable-to-stream)

本文解释了如何将 Iterable 转换为 Stream 以及 Iterable 接口不直接支持它的原因。

[阅读更多](https://www.baeldung.com/java-iterable-to-stream)→

## 2. 使用 Stream.findAny()

顾名思义，findAny()方法允许我们从Stream中查找任何元素。当我们在不注意遇到顺序的情况下寻找元素时，我们会使用它：

该方法返回一个Optional实例，如果Stream为空，则该实例为空：

```java
@Test
public void createStream_whenFindAnyResultIsPresent_thenCorrect() {
    List<String> list = Arrays.asList("A","B","C","D");

    Optional<String> result = list.stream().findAny();

    assertTrue(result.isPresent());
    assertThat(result.get(), anyOf(is("A"), is("B"), is("C"), is("D")));
}
```

在非并行操作中，它很可能会返回Stream中的第一个元素，但这并不能保证。

为了在处理并行操作时获得最佳性能，无法可靠地确定结果：

```java
@Test
public void createParallelStream_whenFindAnyResultIsPresent_thenCorrect()() {
    List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
    Optional<Integer> result = list
      .stream().parallel()
      .filter(num -> num < 4).findAny();

    assertTrue(result.isPresent());
    assertThat(result.get(), anyOf(is(1), is(2), is(3)));
}
```

## 3. 使用 Stream.findFirst()

findFirst()方法查找Stream中的第一个元素。因此，当我们特别想要序列中的第一个元素时，我们会使用此方法。

当没有遇到顺序时，它返回Stream中的任何元素。根据java.util.streams包文档，“Streams 可能有也可能没有定义的相遇顺序。这取决于来源和中间操作。”

返回类型也是一个Optional实例，如果Stream也为空，则返回类型为空：

```java
@Test
public void createStream_whenFindFirstResultIsPresent_thenCorrect() {

    List<String> list = Arrays.asList("A", "B", "C", "D");

    Optional<String> result = list.stream().findFirst();

    assertTrue(result.isPresent());
    assertThat(result.get(), is("A"));
}
```

findFirst方法的行为在并行场景中不会改变。如果遇到顺序存在，它将始终确定性地运行。

## 4。总结

在本文中，我们研究了Java8 Streams API 的findAny()和findFirst()方法。

findAny ()方法返回Stream中的任何元素，而findFirst()方法返回Stream中的第一个元素。