## 1. 概述

在本教程中，我们将展示如何使用 Google Guava 的RangeSet接口及其实现。

RangeSet是一个由零个或多个非空的、断开连接的范围组成的集合。将范围添加到可变RangeSet时，任何连接的范围都会合并在一起，同时忽略空范围。

RangeSet的基本实现是TreeRangeSet。

## 2. Google Guava 的RangeSet

让我们看看如何使用RangeSet类。

### 2.1. Maven 依赖

让我们首先在pom.xml中添加 Google 的 Guava 库依赖项：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")检查最新版本的依赖项。

## 3.创作

让我们探索一些创建RangeSet实例的方法。

首先，我们可以使用TreeRangeSet类中的create方法来创建一个可变集：

```java
RangeSet<Integer> numberRangeSet = TreeRangeSet.create();
```

如果我们已经有了集合，请使用TreeRangeSet类的create方法通过传递该集合来创建一个可变集：

```java
List<Range<Integer>> numberList = Arrays.asList(Range.closed(0, 2));
RangeSet<Integer> numberRangeSet = TreeRangeSet.create(numberList);
```

最后，如果我们需要创建一个不可变范围集，请使用ImmutableRangeSet类(创建遵循构建器模式)：

```java
RangeSet<Integer> numberRangeSet 
  = new ImmutableRangeSet.<Integer>builder().add(Range.closed(0, 2)).build();

```

## 4.用法

让我们从一个显示RangeSet用法的简单示例开始。

### 4.1. 添加到范围

我们可以检查提供的输入是否在集合中任何范围项中存在的范围内：

```java
@Test
public void givenRangeSet_whenQueryWithinRange_returnsSucessfully() {
    RangeSet<Integer> numberRangeSet = TreeRangeSet.create();

    numberRangeSet.add(Range.closed(0, 2));
    numberRangeSet.add(Range.closed(3, 5));
    numberRangeSet.add(Range.closed(6, 8));

    assertTrue(numberRangeSet.contains(1));
    assertFalse(numberRangeSet.contains(9));
}
```

笔记：

-   Range类的closed方法假定整数值的范围在 0 到 2 之间(包括两者)
-   上例中的范围由整数组成。我们可以使用由任何类型组成的范围，只要它实现了Comparable接口，例如String、Character、浮点小数等
-   在ImmutableRangeSet的情况下，集合中存在的范围项不能与想要添加的范围项重叠。如果发生这种情况，我们会得到一个IllegalArgumentException
-   RangeSet的范围输入不能为空。如果输入为null，我们将得到一个NullPointerException

### 4.2. 删除范围

让我们看看如何从RangeSet中删除值：

```java
@Test
public void givenRangeSet_whenRemoveRangeIsCalled_removesSucessfully() {
    RangeSet<Integer> numberRangeSet = TreeRangeSet.create();

    numberRangeSet.add(Range.closed(0, 2));
    numberRangeSet.add(Range.closed(3, 5));
    numberRangeSet.add(Range.closed(6, 8));
    numberRangeSet.add(Range.closed(9, 15));
    numberRangeSet.remove(Range.closed(3, 5));
    numberRangeSet.remove(Range.closed(7, 10));

    assertTrue(numberRangeSet.contains(1));
    assertFalse(numberRangeSet.contains(9));
    assertTrue(numberRangeSet.contains(12));
}
```

可以看出，删除后我们仍然可以访问集合中剩余的任何范围项中存在的值。

### 4.3. 范围跨度

现在让我们看看RangeSet的整体跨度是多少：

```java
@Test
public void givenRangeSet_whenSpanIsCalled_returnsSucessfully() {
    RangeSet<Integer> numberRangeSet = TreeRangeSet.create();

    numberRangeSet.add(Range.closed(0, 2));
    numberRangeSet.add(Range.closed(3, 5));
    numberRangeSet.add(Range.closed(6, 8));
    Range<Integer> experienceSpan = numberRangeSet.span();

    assertEquals(0, experienceSpan.lowerEndpoint().intValue());
    assertEquals(8, experienceSpan.upperEndpoint().intValue());
}
```

### 4.4. 获取子范围

如果我们希望根据给定的Range获取RangeSet的一部分，我们可以使用subRangeSet方法：

```java
@Test
public void 
  givenRangeSet_whenSubRangeSetIsCalled_returnsSubRangeSucessfully() {
  
    RangeSet<Integer> numberRangeSet = TreeRangeSet.create();

    numberRangeSet.add(Range.closed(0, 2));
    numberRangeSet.add(Range.closed(3, 5));
    numberRangeSet.add(Range.closed(6, 8));
    RangeSet<Integer> numberSubRangeSet 
      = numberRangeSet.subRangeSet(Range.closed(4, 14));

    assertFalse(numberSubRangeSet.contains(3));
    assertFalse(numberSubRangeSet.contains(14));
    assertTrue(numberSubRangeSet.contains(7));
}
```

### 4.5. 补码法

接下来，让我们使用complement方法获取除RangeSet中存在的值之外的所有值：

```java
@Test
public void givenRangeSet_whenComplementIsCalled_returnsSucessfully() {
    RangeSet<Integer> numberRangeSet = TreeRangeSet.create();

    numberRangeSet.add(Range.closed(0, 2));
    numberRangeSet.add(Range.closed(3, 5));
    numberRangeSet.add(Range.closed(6, 8));
    RangeSet<Integer> numberRangeComplementSet
      = numberRangeSet.complement();

    assertTrue(numberRangeComplementSet.contains(-1000));
    assertFalse(numberRangeComplementSet.contains(2));
    assertFalse(numberRangeComplementSet.contains(3));
    assertTrue(numberRangeComplementSet.contains(1000));
}
```

### 4.6. 与范围的交集

最后，当我们想检查RangeSet中存在的范围区间是否与另一个给定范围内的部分或全部值相交时，我们可以使用intersect方法：

```java
@Test
public void givenRangeSet_whenIntersectsWithinRange_returnsSucessfully() {
    RangeSet<Integer> numberRangeSet = TreeRangeSet.create();

    numberRangeSet.add(Range.closed(0, 2));
    numberRangeSet.add(Range.closed(3, 10));
    numberRangeSet.add(Range.closed(15, 18));

    assertTrue(numberRangeSet.intersects(Range.closed(4, 17)));
    assertFalse(numberRangeSet.intersects(Range.closed(19, 200)));
}
```

## 5.总结

在本教程中，我们使用一些示例说明了 Guava 库的RangeSet。RangeSet主要用于检查值是否落在集合中存在的特定范围内。