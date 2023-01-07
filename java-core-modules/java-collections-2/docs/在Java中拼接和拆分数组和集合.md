## 1. 概述

在这个快速教程中，我们将学习如何在Java中连接和拆分数组和集合，充分利用新的流支持。

## 2.连接两个数组

让我们首先使用Stream.concat将两个数组连接在一起：

```java
@Test
public void whenJoiningTwoArrays_thenJoined() {
    String[] animals1 = new String[] { "Dog", "Cat" };
    String[] animals2 = new String[] { "Bird", "Cow" };
    
    String[] result = Stream.concat(
      Arrays.stream(animals1), Arrays.stream(animals2)).toArray(String[]::new);

    assertArrayEquals(result, new String[] { "Dog", "Cat", "Bird", "Cow" });
}
```

## 3.加入两个集合

让我们对两个集合做同样的连接：

```java
@Test
public void whenJoiningTwoCollections_thenJoined() {
    Collection<String> collection1 = Arrays.asList("Dog", "Cat");
    Collection<String> collection2 = Arrays.asList("Bird", "Cow", "Moose");
    
    Collection<String> result = Stream.concat(
      collection1.stream(), collection2.stream())
      .collect(Collectors.toList());

    assertTrue(result.equals(Arrays.asList("Dog", "Cat", "Bird", "Cow", "Moose")));
}
```

## 4.使用过滤器连接两个集合

现在，让我们加入两个过滤大于 10 的数字集合：

```java
@Test
public void whenJoiningTwoCollectionsWithFilter_thenJoined() {
    Collection<String> collection1 = Arrays.asList("Dog", "Cat");
    Collection<String> collection2 = Arrays.asList("Bird", "Cow", "Moose");
    
    Collection<String> result = Stream.concat(
      collection1.stream(), collection2.stream())
      .filter(e -> e.length() == 3)
      .collect(Collectors.toList());

    assertTrue(result.equals(Arrays.asList("Dog", "Cat", "Cow")));
}
```

## 5. 将数组连接成字符串

接下来，让我们使用Collector将 Array 连接到String中：

```java
@Test
public void whenConvertArrayToString_thenConverted() {
    String[] animals = new String[] { "Dog", "Cat", "Bird", "Cow" };
    String result = Arrays.stream(animals).collect(Collectors.joining(", "));

    assertEquals(result, "Dog, Cat, Bird, Cow");
}
```

## 6. 将一个集合连接成一个字符串

让我们做同样的事情，但使用Collection ：

```java
@Test
public void whenConvertCollectionToString_thenConverted() {
    Collection<String> animals = Arrays.asList("Dog", "Cat", "Bird", "Cow");
    String result = animals.stream().collect(Collectors.joining(", "));

    assertEquals(result, "Dog, Cat, Bird, Cow");
}
```

## 7. 将映射连接到字符串中

接下来，让我们从Map创建一个String。

该过程与前面的示例非常相似，但这里我们有一个额外的步骤来首先加入每个Map Entry：

```java
@Test
public void whenConvertMapToString_thenConverted() {
    Map<Integer, String> animals = new HashMap<>();
    animals.put(1, "Dog");
    animals.put(2, "Cat");
    animals.put(3, "Cow");

    String result = animals.entrySet().stream()
      .map(entry -> entry.getKey() + " = " + entry.getValue())
      .collect(Collectors.joining(", "));

    assertEquals(result, "1 = Dog, 2 = Cat, 3 = Cow");
}
```

## 8. 将嵌套集合连接成一个字符串

让我们做一些更复杂的事情。让我们将一些嵌套的Collections连接到一个String中。

在下面的示例中，我们首先在每个嵌套的 Collection 中加入，然后我们加入每个嵌套的结果：

```java
@Test
public void whenConvertNestedCollectionToString_thenConverted() {
    Collection<List<String>> nested = new ArrayList<>();
    nested.add(Arrays.asList("Dog", "Cat"));
    nested.add(Arrays.asList("Cow", "Pig"));

    String result = nested.stream().map(
      nextList -> nextList.stream()
        .collect(Collectors.joining("-")))
      .collect(Collectors.joining("; "));

    assertEquals(result, "Dog-Cat; Cow-Pig");
}
```

## 9.加入时处理空值

让我们看看如何使用过滤器来跳过任何空值：

```java
@Test
public void whenConvertCollectionToStringAndSkipNull_thenConverted() {
    Collection<String> animals = Arrays.asList("Dog", "Cat", null, "Moose");
    String result = animals.stream()
      .filter(Objects::nonNull)
      .collect(Collectors.joining(", "));

    assertEquals(result, "Dog, Cat, Moose");
}
```

## 10. 将一个集合一分为二

让我们在中间将一个数字集合分成两个集合：

```java
@Test
public void whenSplitCollectionHalf_thenConverted() {
    Collection<String> animals = Arrays.asList(
        "Dog", "Cat", "Cow", "Bird", "Moose", "Pig");
    Collection<String> result1 = new ArrayList<>();
    Collection<String> result2 = new ArrayList<>();
    AtomicInteger count = new AtomicInteger();
    int midpoint = Math.round(animals.size() / 2);

    animals.forEach(next -> {
        int index = count.getAndIncrement();
        if (index < midpoint) {
            result1.add(next);
        } else {
            result2.add(next);
        }
    });

    assertTrue(result1.equals(Arrays.asList("Dog", "Cat", "Cow")));
    assertTrue(result2.equals(Arrays.asList("Bird", "Moose", "Pig")));
}
```

## 11.按字长拆分数组

接下来，让我们按单词的长度拆分一个数组：

```java
@Test
public void whenSplitArrayByWordLength_thenConverted() {
    String[] animals = new String[] { "Dog", "Cat", "Bird", "Cow", "Pig", "Moose"};
    Map<Integer, List<String>> result = Arrays.stream(animals)
      .collect(Collectors.groupingBy(String::length));

    assertTrue(result.get(3).equals(Arrays.asList("Dog", "Cat", "Cow", "Pig")));
    assertTrue(result.get(4).equals(Arrays.asList("Bird")));
    assertTrue(result.get(5).equals(Arrays.asList("Moose")));
}
```

## 12. 将字符串拆分为数组

现在让我们做相反的事情，让我们把一个字符串拆分成一个数组：

```java
@Test
public void whenConvertStringToArray_thenConverted() {
    String animals = "Dog, Cat, Bird, Cow";
    String[] result = animals.split(", ");

    assertArrayEquals(result, new String[] { "Dog", "Cat", "Bird", "Cow" });
}
```

## 13.将字符串拆分成一个集合

此示例与上一个示例类似，只是将Array转换为Collection的额外步骤：

```java
@Test
public void whenConvertStringToCollection_thenConverted() {
    String animals = "Dog, Cat, Bird, Cow";
    Collection<String> result = Arrays.asList(animals.split(", "));

    assertTrue(result.equals(Arrays.asList("Dog", "Cat", "Bird", "Cow")));
}
```

## 14. 将字符串拆分为映射

现在，让我们从String创建一个Map。我们需要将字符串拆分两次，一次针对每个条目，最后一次针对键和值：

```java
@Test
public void whenConvertStringToMap_thenConverted() {
    String animals = "1 = Dog, 2 = Cat, 3 = Bird";

    Map<Integer, String> result = Arrays.stream(
      animals.split(", ")).map(next -> next.split(" = "))
      .collect(Collectors.toMap(entry -> Integer.parseInt(entry[0]), entry -> entry[1]));

    assertEquals(result.get(1), "Dog");
    assertEquals(result.get(2), "Cat");
    assertEquals(result.get(3), "Bird");
}
```

## 15.使用多个分隔符拆分字符串

最后，让我们使用正则表达式拆分具有多个分隔符的字符串，我们还将删除所有空结果：

```java
@Test
public void whenConvertCollectionToStringMultipleSeparators_thenConverted() {
    String animals = "Dog. , Cat, Bird. Cow";

    Collection<String> result = Arrays.stream(animals.split("[,|.]"))
      .map(String::trim)
      .filter(next -> !next.isEmpty())
      .collect(Collectors.toList());

    assertTrue(result.equals(Arrays.asList("Dog", "Cat", "Bird", "Cow")));
}
```

## 16.总结

在本教程中，我们利用简单的String.split函数和强大的Java8 Stream，演示了如何连接和拆分数组和集合。