## 1. 概述

在本教程中，我们将探讨在 Java中比较两个HashMap的不同方法。

我们将讨论检查两个HashMap是否相似的多种方法。我们还将使用Java8 Stream API 和 Guava 来获取不同HashMap之间的详细差异。

## 2. 使用Map.equals()

首先，我们将使用Map.equals()来检查两个HashMap是否具有相同的条目：

```java
@Test
public void whenCompareTwoHashMapsUsingEquals_thenSuccess() {
    Map<String, String> asiaCapital1 = new HashMap<String, String>();
    asiaCapital1.put("Japan", "Tokyo");
    asiaCapital1.put("South Korea", "Seoul");

    Map<String, String> asiaCapital2 = new HashMap<String, String>();
    asiaCapital2.put("South Korea", "Seoul");
    asiaCapital2.put("Japan", "Tokyo");

    Map<String, String> asiaCapital3 = new HashMap<String, String>();
    asiaCapital3.put("Japan", "Tokyo");
    asiaCapital3.put("China", "Beijing");

    assertTrue(asiaCapital1.equals(asiaCapital2));
    assertFalse(asiaCapital1.equals(asiaCapital3));
}
```

在这里，我们正在创建三个 HashMap对象并添加条目。然后我们使用Map.equals()来检查两个HashMap是否具有相同的条目。

Map.equals()的工作方式 是使用 Object.equals()方法比较键和值。 这意味着它仅在键和值对象都正确实现equals()时才有效。

例如，当值类型为数组时， Map.equals()不起作用，因为数组的 equals() 方法比较标识而不是数组的内容：

```java
@Test
public void whenCompareTwoHashMapsWithArrayValuesUsingEquals_thenFail() {
    Map<String, String[]> asiaCity1 = new HashMap<String, String[]>();
    asiaCity1.put("Japan", new String[] { "Tokyo", "Osaka" });
    asiaCity1.put("South Korea", new String[] { "Seoul", "Busan" });

    Map<String, String[]> asiaCity2 = new HashMap<String, String[]>();
    asiaCity2.put("South Korea", new String[] { "Seoul", "Busan" });
    asiaCity2.put("Japan", new String[] { "Tokyo", "Osaka" });

    assertFalse(asiaCity1.equals(asiaCity2));
}
```

## 3. 使用JavaStream API

我们还可以使用Java8 Stream API实现自己的方法来比较HashMap ：

```java
private boolean areEqual(Map<String, String> first, Map<String, String> second) {
    if (first.size() != second.size()) {
        return false;
    }

    return first.entrySet().stream()
      .allMatch(e -> e.getValue().equals(second.get(e.getKey())));
}
```

为简单起见，我们实现了 现在可以用来比较HashMap<String, String>对象的areEqual()方法：

```java
@Test
public void whenCompareTwoHashMapsUsingStreamAPI_thenSuccess() {
    assertTrue(areEqual(asiaCapital1, asiaCapital2));
    assertFalse(areEqual(asiaCapital1, asiaCapital3));
}
```

但是我们也可以通过使用Arrays.equals()比较两个数组来自定义我们自己的方法areEqualWithArrayValue()来处理数组值：

```java
private boolean areEqualWithArrayValue(Map<String, String[]> first, Map<String, String[]> second) {
    if (first.size() != second.size()) {
        return false;
    }

    return first.entrySet().stream()
      .allMatch(e -> Arrays.equals(e.getValue(), second.get(e.getKey())));
}
```

与Map.equals()不同，我们自己的方法将成功地将HashMap与数组值进行比较：

```java
@Test
public void whenCompareTwoHashMapsWithArrayValuesUsingStreamAPI_thenSuccess() {
    assertTrue(areEqualWithArrayValue(asiaCity1, asiaCity2)); 
    assertFalse(areEqualWithArrayValue(asiaCity1, asiaCity3));
}
```

## 4. 比较HashMap键和值

接下来，让我们看看如何比较两个HashMap键及其对应的值。

### 4.1. 比较HashMap键

首先，我们可以通过比较它们的KeySet()来检查两个HashMap是否具有相同的键：

```java
@Test
public void whenCompareTwoHashMapKeys_thenSuccess() {
    assertTrue(asiaCapital1.keySet().equals(asiaCapital2.keySet())); 
    assertFalse(asiaCapital1.keySet().equals(asiaCapital3.keySet()));
}
```

### 4.2. 比较HashMap值

接下来，我们将看看如何逐个比较HashMap值。

我们将实现一个简单的方法来使用Stream API检查两个HashMap中哪些键具有相同的值：

```java
private Map<String, Boolean> areEqualKeyValues(Map<String, String> first, Map<String, String> second) {
    return first.entrySet().stream()
      .collect(Collectors.toMap(e -> e.getKey(), 
        e -> e.getValue().equals(second.get(e.getKey()))));
}
```

我们现在可以使用 areEqualKeyValues() 来比较两个不同的HashMap，以详细查看哪些键具有相同的值，哪些具有不同的值：

```java
@Test
public void whenCompareTwoHashMapKeyValuesUsingStreamAPI_thenSuccess() {
    Map<String, String> asiaCapital3 = new HashMap<String, String>();
    asiaCapital3.put("Japan", "Tokyo");
    asiaCapital3.put("South Korea", "Seoul");
    asiaCapital3.put("China", "Beijing");

    Map<String, String> asiaCapital4 = new HashMap<String, String>();
    asiaCapital4.put("South Korea", "Seoul");
    asiaCapital4.put("Japan", "Osaka");
    asiaCapital4.put("China", "Beijing");

    Map<String, Boolean> result = areEqualKeyValues(asiaCapital3, asiaCapital4);

    assertEquals(3, result.size());
    assertThat(result, hasEntry("Japan", false));
    assertThat(result, hasEntry("South Korea", true));
    assertThat(result, hasEntry("China", true));
}
```

## 5. 使用 Guava 映射差异

最后，我们将看到如何使用 Guava Maps.difference()获取两个HashMap之间的详细差异。

此方法返回一个[MapDifference](https://google.github.io/guava/releases/20.0/api/docs/com/google/common/collect/MapDifference.html)对象，该对象具有许多有用的方法来分析地图之间的差异。让我们来看看其中的一些。

### 5.1. MapDifference.entriesDiffering()

首先，我们将使用 MapDifference.entriesDiffering()获取每个HashMap中具有不同值的公共键：

```java
@Test
public void givenDifferentMaps_whenGetDiffUsingGuava_thenSuccess() {
    Map<String, String> asia1 = new HashMap<String, String>();
    asia1.put("Japan", "Tokyo");
    asia1.put("South Korea", "Seoul");
    asia1.put("India", "New Delhi");

    Map<String, String> asia2 = new HashMap<String, String>();
    asia2.put("Japan", "Tokyo");
    asia2.put("China", "Beijing");
    asia2.put("India", "Delhi");

    MapDifference<String, String> diff = Maps.difference(asia1, asia2);
    Map<String, ValueDifference<String>> entriesDiffering = diff.entriesDiffering();

    assertFalse(diff.areEqual());
    assertEquals(1, entriesDiffering.size());
    assertThat(entriesDiffering, hasKey("India"));
    assertEquals("New Delhi", entriesDiffering.get("India").leftValue());
    assertEquals("Delhi", entriesDiffering.get("India").rightValue());
}
```

entriesDiffering ()方法返回一个新的Map，其中包含一组公共键和 ValueDifference对象作为一组值。

每个ValueDifference对象都有一个leftValue()和rightValue()方法，分别返回两个映射 中的值。

### 5.2. MapDifference.entriesOnlyOnRight()和 MapDifference.entriesOnlyOnLeft()

 然后，我们可以使用 MapDifference.entriesOnlyOnRight()和 MapDifference.entriesOnlyOnLeft()获取仅存在于一个HashMap中的条目：

```java
@Test
public void givenDifferentMaps_whenGetEntriesOnOneSideUsingGuava_thenSuccess() {
    MapDifference<String, String> diff = Maps.difference(asia1, asia2);
    Map<String, String> entriesOnlyOnRight = diff.entriesOnlyOnRight();
    Map<String, String> entriesOnlyOnLeft = diff.entriesOnlyOnLeft();
    
    assertEquals(1, entriesOnlyOnRight.size());
    assertEquals(1, entriesOnlyOnLeft.size());
    assertThat(entriesOnlyOnRight, hasEntry("China", "Beijing"));
    assertThat(entriesOnlyOnLeft, hasEntry("South Korea", "Seoul"));
}
```

### 5.3. MapDifference.entriesInCommon()

接下来，我们将使用 MapDifference.entriesInCommon() 获取公共条目：

```java
@Test
public void givenDifferentMaps_whenGetCommonEntriesUsingGuava_thenSuccess() {
    MapDifference<String, String> diff = Maps.difference(asia1, asia2);
    Map<String, String> entriesInCommon = diff.entriesInCommon();

    assertEquals(1, entriesInCommon.size());
    assertThat(entriesInCommon, hasEntry("Japan", "Tokyo"));
}
```

### 5.4. 自定义Maps.difference()行为

由于 Maps.difference()默认使用equals()和hashCode()来比较条目，因此它不适用于未正确实现它们的对象：

```java
@Test
public void givenSimilarMapsWithArrayValue_whenCompareUsingGuava_thenFail() {
    MapDifference<String, String[]> diff = Maps.difference(asiaCity1, asiaCity2);
    assertFalse(diff.areEqual());
}
```

但是，我们可以使用 Equivalence自定义比较中使用的方法。

例如，我们将为 类型String[]定义Equivalence 以根据需要比较HashMap中的String[]值 ：

```java
@Test
public void givenSimilarMapsWithArrayValue_whenCompareUsingGuavaEquivalence_thenSuccess() {
    Equivalence<String[]> eq = new Equivalence<String[]>() {
        @Override
        protected boolean doEquivalent(String[] a, String[] b) {
            return Arrays.equals(a, b);
        }

        @Override
        protected int doHash(String[] value) {
            return value.hashCode();
        }
    };

    MapDifference<String, String[]> diff = Maps.difference(asiaCity1, asiaCity2, eq);
    assertTrue(diff.areEqual());

    diff = Maps.difference(asiaCity1, asiaCity3, eq); 
    assertFalse(diff.areEqual());
}
```

## 六，总结

在本文中，我们讨论了在 Java中比较HashMap的不同方法。我们学习了多种检查两个HashMap是否相等以及如何获取详细差异的方法。