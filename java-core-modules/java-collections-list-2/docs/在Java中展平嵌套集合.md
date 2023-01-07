## 1. 概述

在这篇简短的文章中，我们将探讨如何在Java中展平嵌套集合。

## 2. 嵌套集合示例

假设我们有一个String类型列表的列表。

```java
List<List<String>> nestedList = asList(
  asList("one:one"), 
  asList("two:one", "two:two", "two:three"), 
  asList("three:one", "three:two", "three:three", "three:four"));
```

## 3.用forEach扁平化列表

为了将这个嵌套集合展平为字符串列表，我们可以将forEach与Java8 方法引用一起使用：

```java
public <T> List<T> flattenListOfListsImperatively(
    List<List<T>> nestedList) {
    List<T> ls = new ArrayList<>();
    nestedList.forEach(ls::addAll);
    return ls;
}

```

在这里你可以看到实际的方法：

```java
@Test
public void givenNestedList_thenFlattenImperatively() {
    List<String> ls = flattenListOfListsImperatively(nestedList);
    
    assertNotNull(ls);
    assertTrue(ls.size() == 8);
    assertThat(ls, IsIterableContainingInOrder.contains(
      "one:one",
      "two:one", "two:two", "two:three", "three:one",
      "three:two", "three:three", "three:four"));
}
```

## 4.用flatMap扁平化列表

我们还可以通过使用Stream API中的flatMap方法来展平嵌套列表。

这允许我们展平嵌套的Stream结构并最终将所有元素收集到一个特定的集合中：

```java
public <T> List<T> flattenListOfListsStream(List<List<T>> list) {
    return list.stream()
      .flatMap(Collection::stream)
      .collect(Collectors.toList());    
}

```

这是实际操作的逻辑：

```java
@Test
public void givenNestedList_thenFlattenFunctionally() {
    List<String> ls = flattenListOfListsStream(nestedList);
    
    assertNotNull(ls);
    assertTrue(ls.size() == 8);
}
```

## 5.总结

Java 8 中的简单forEach 或 flatMap方法与方法引用相结合，可用于展平嵌套集合。