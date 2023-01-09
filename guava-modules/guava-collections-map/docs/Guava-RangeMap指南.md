## 1. 概述

在本教程中，我们将展示如何使用 Google Guava 的RangeMap接口及其实现。

RangeMap是一种从不相交的非空范围到非空值的特殊映射。使用查询，我们可以查找该地图中任何特定范围的值。

RangeMap的基本实现是TreeRangeMap。在内部，地图使用TreeMap将键存储为范围，并将值存储为任何自定义Java对象。

## 2. Google Guava 的RangeMap

让我们看看如何使用RangeMap类。

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

## 3.创建

我们可以创建RangeMap实例的一些方法是：

-   使用TreeRangeMap类中的create方法创建可变地图：

```java
RangeMap<Integer, String> experienceRangeDesignationMap
  = TreeRangeMap.create();
```

-   如果我们打算创建一个不可变的范围映射，请使用ImmutableRangeMap类(遵循构建器模式)：

```java
RangeMap<Integer, String> experienceRangeDesignationMap
  = new ImmutableRangeMap.<Integer, String>builder()
  .put(Range.closed(0, 2), "Associate")
  .build();

```

## 4.使用

让我们从一个显示RangeMap用法的简单示例开始。

### 4.1. 基于范围内输入的检索

我们可以获得与整数范围内的值关联的值：

```java
@Test
public void givenRangeMap_whenQueryWithinRange_returnsSucessfully() {
    RangeMap<Integer, String> experienceRangeDesignationMap 
     = TreeRangeMap.create();

    experienceRangeDesignationMap.put(
      Range.closed(0, 2), "Associate");
    experienceRangeDesignationMap.put(
      Range.closed(3, 5), "Senior Associate");
    experienceRangeDesignationMap.put(
      Range.closed(6, 8),  "Vice President");
    experienceRangeDesignationMap.put(
      Range.closed(9, 15), "Executive Director");

    assertEquals("Vice President", 
      experienceRangeDesignationMap.get(6));
    assertEquals("Executive Director", 
      experienceRangeDesignationMap.get(15));
}
```

笔记：

-   Range类的closed方法假定整数值的范围在 0 到 2 之间(包括两者)
-   上例中的范围由整数组成。我们可以使用任何类型的范围，只要它实现了Comparable接口，例如String、Character、浮点小数等。
-   当我们尝试获取地图中不存在的范围的值时，RangeMap返回Null
-   在ImmutableRangeMap的情况下，一个键的范围不能与需要插入的键的范围重叠。如果发生这种情况，我们会得到一个IllegalArgumentException
-   RangeMap中的键和值都不能为null。如果其中任何一个为null，我们将得到一个NullPointerException

### 4.2. 根据范围删除值

让我们看看如何删除值。在此示例中，我们展示了如何删除与整个范围关联的值。我们还展示了如何删除基于部分键范围的值：

```java
@Test
public void givenRangeMap_whenRemoveRangeIsCalled_removesSucessfully() {
    RangeMap<Integer, String> experienceRangeDesignationMap 
      = TreeRangeMap.create();

    experienceRangeDesignationMap.put(
      Range.closed(0, 2), "Associate");
    experienceRangeDesignationMap.put(
      Range.closed(3, 5), "Senior Associate");
    experienceRangeDesignationMap.put(
      Range.closed(6, 8), "Vice President");
    experienceRangeDesignationMap.put(
      Range.closed(9, 15), "Executive Director");
 
    experienceRangeDesignationMap.remove(Range.closed(9, 15));
    experienceRangeDesignationMap.remove(Range.closed(1, 4));
  
    assertNull(experienceRangeDesignationMap.get(9));
    assertEquals("Associate", 
      experienceRangeDesignationMap.get(0));
    assertEquals("Senior Associate", 
      experienceRangeDesignationMap.get(5));
    assertNull(experienceRangeDesignationMap.get(1));
}
```

可以看出，即使从范围中部分删除值后，如果范围仍然有效，我们仍然可以获得这些值。

### 4.3. 关键范围的跨度

如果我们想知道RangeMap的整体跨度是多少，我们可以使用span方法：

```java
@Test
public void givenRangeMap_whenSpanIsCalled_returnsSucessfully() {
    RangeMap<Integer, String> experienceRangeDesignationMap = TreeRangeMap.create();
    experienceRangeDesignationMap.put(Range.closed(0, 2), "Associate");
    experienceRangeDesignationMap.put(Range.closed(3, 5), "Senior Associate");
    experienceRangeDesignationMap.put(Range.closed(6, 8), "Vice President");
    experienceRangeDesignationMap.put(Range.closed(9, 15), "Executive Director");
    experienceRangeDesignationMap.put(Range.closed(16, 30), "Managing Director");
    Range<Integer> experienceSpan = experienceRangeDesignationMap.span();

    assertEquals(0, experienceSpan.lowerEndpoint().intValue());
    assertEquals(30, experienceSpan.upperEndpoint().intValue());
}
```

### 4.4. 获取SubRangeMap 

当我们想从RangeMap中选择一个部分时，我们可以使用subRangeMap方法：

```java
@Test
public void givenRangeMap_whenSubRangeMapIsCalled_returnsSubRangeSuccessfully() {
    RangeMap<Integer, String> experienceRangeDesignationMap = TreeRangeMap.create();

    experienceRangeDesignationMap
      .put(Range.closed(0, 2), "Associate");
    experienceRangeDesignationMap
      .put(Range.closed(3, 5), "Senior Associate");
    experienceRangeDesignationMap
      .put(Range.closed(6, 8), "Vice President");
    experienceRangeDesignationMap
      .put(Range.closed(8, 15), "Executive Director");
    experienceRangeDesignationMap
      .put(Range.closed(16, 30), "Managing Director");
    RangeMap<Integer, String> experiencedSubRangeDesignationMap
      = experienceRangeDesignationMap.subRangeMap(Range.closed(4, 14));

    assertNull(experiencedSubRangeDesignationMap.get(3));
    assertTrue(experiencedSubRangeDesignationMap.asMapOfRanges().values()
      .containsAll(Arrays.asList("Executive Director", "Vice President", "Executive Director")));
}
```

此方法返回 RangeMap与给定 Range参数的交集。

### 4.5. 获得参赛资格

最后，如果我们要从RangeMap中寻找一个条目，我们使用getEntry方法：

```java
@Test
public void givenRangeMap_whenGetEntryIsCalled_returnsEntrySucessfully() {
    RangeMap<Integer, String> experienceRangeDesignationMap 
      = TreeRangeMap.create();

    experienceRangeDesignationMap.put(
      Range.closed(0, 2), "Associate");
    experienceRangeDesignationMap.put(
      Range.closed(3, 5), "Senior Associate");
    experienceRangeDesignationMap.put(
      Range.closed(6, 8), "Vice President");
    experienceRangeDesignationMap.put(
      Range.closed(9, 15), "Executive Director");
    Map.Entry<Range<Integer>, String> experienceEntry 
      = experienceRangeDesignationMap.getEntry(10);
       
    assertEquals(Range.closed(9, 15), experienceEntry.getKey());
    assertEquals("Executive Director", experienceEntry.getValue());
}
```

## 5.总结

在本教程中，我们展示了在 Guava 库中使用RangeMap的示例。它主要用于根据从地图中指定为 a 的键获取值。