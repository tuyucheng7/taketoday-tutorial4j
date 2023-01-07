## 1. 概述

在本教程中，我们将借助一个简单的示例来讨论Java中的[Set](https://www.baeldung.com/java-set-operations)和[List](https://www.baeldung.com/java-arraylist)之间的区别。

## 2.概念差异

List和 Set都是Java Collections的成员。但是，有一些重要的区别：

-   List可以包含重复项，但Set不能
-   List将保留插入顺序，但Set可能会也可能不会
-   由于插入顺序可能无法在Set中维护，因此它不允许像 List中那样基于索引的访问

请注意，有一些Set接口的实现可以维护顺序，例如 LinkedHashSet。

## 3.代码示例

### 3.1. 允许重复

允许为List添加重复项。但是，它不适用于Set：

```java
@Test
public void givenList_whenDuplicates_thenAllowed(){
    List<Integer> integerList = new ArrayList<>();
    integerList.add(2);
    integerList.add(3);
    integerList.add(4);
    integerList.add(4);
    assertEquals(integerList.size(), 4);
}

@Test
public void givenSet_whenDuplicates_thenNotAllowed(){
    Set<Integer> integerSet = new HashSet<>();
    integerSet.add(2);
    integerSet.add(3);
    integerSet.add(4);
    integerSet.add(4);
    assertEquals(integerSet.size(), 3);
}
```

### 3.2. 维护广告订单

Set根据实现维护顺序。例如，不能保证HashSet保持顺序，但 LinkedHashSet可以。让我们看一个使用LinkedHashSet排序的例子：

```csharp
@Test
public void givenSet_whenOrdering_thenMayBeAllowed(){
    Set<Integer> set1 = new LinkedHashSet<>();
    set1.add(2);
    set1.add(3);
    set1.add(4);
    Set<Integer> set2 = new LinkedHashSet<>();
    set2.add(2);
    set2.add(3);
    set2.add(4);
    Assert.assertArrayEquals(set1.toArray(), set2.toArray());
}
```

由于不能保证Set保持顺序，因此无法对其进行索引。

## 4。总结

在本教程中，我们看到了Java 中的List和 Set之间的区别 。