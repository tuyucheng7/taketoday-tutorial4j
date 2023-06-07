---
layout: post
title:  使用Selenium处理浏览器选项卡
category: java-collection
copyright: java-collection
excerpt: Java Collection
---

## 1. 概述

在本教程中，我们将探索使用所有值null或零初始化[Java ArrayList的不同方法。](https://www.baeldung.com/java-arraylist)我们也可以随心所欲地进行初始化，并使用不同的数值或对象来初始化列表。

## 2. 使用for循环

在考虑使用所需值或对象初始化ArrayList的问题时，我们想到的第一个解决方案是使用简单的for循环。理所当然的，这是一个直接可行的解决方案：

```java
ArrayList<Integer> arrayList = new ArrayList<>();
for (int i = 0; i< 10; i++) {
    arrayList.add(null);
    // arrayList.add(0);
}
```

我们声明一个空的ArrayList并使用add()方法进行循环。

## 3. 使用ArrayList构造器方法

另一种可能不太为人所知的方法是使用[ArrayList](https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html#ArrayList-java.util.Collection-)类的[构造函数之一。](https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html#ArrayList-java.util.Collection-)这将一个集合作为参数，并构造一个新的ArrayList，其中包含指定列表中的元素，顺序是集合的迭代器返回它们的顺序。为了向我们的构造函数提供所需的列表，我们将使用[Collections](https://www.baeldung.com/java-collections)[类的](https://www.baeldung.com/java-collections)nCopies()函数。此函数将元素和所需副本数作为参数。我们还可以编写一个单元测试来检查我们的构造函数是否正常工作：

```java
@Test
public void whenInitializingListWithNCopies_thenListIsCorrectlyPopulated() {
    // when
    ArrayList<Integer> list = new ArrayList<>(Collections.nCopies(10, 0));

    // then
    Assertions.assertEquals(10, list.size());
    Assertions.assertTrue(list.stream().allMatch(elem -> elem == 0));
}
```

我们将检查列表是否包含所需数量的元素，以及是否所有元素都等于我们要求的值。有多种方法可以[检查列表的元素是否完全相同](https://www.baeldung.com/java-list-all-equal)。对于我们的示例，我们使用[Java Stream API的](https://www.baeldung.com/java-8-streams)allMatch()函数。

## 4. 使用Java Stream API

在前面的示例中，我们使用[Java Stream API](https://www.baeldung.com/java-streams)来确定我们是否正确初始化了列表。但是，Java Stream的功能远不止于此。我们可以使用静态函数generate()根据供应商生成无限数量的元素：

```java
@Test
public void whenInitializingListWithStream_thenListIsCorrectlyPopulated() {
    
    // when
    ArrayList<Integer> listWithZeros = Stream.generate(() -> 0)
      .limit(10).collect(Collectors.toCollection(ArrayList::new));

    ArrayList<Object> listWithNulls = Stream.generate(() -> null)
      .limit(10).collect(Collectors.toCollection(ArrayList::new));

    // then
    Assertions.assertEquals(10, listWithZeros.size());
    Assertions.assertTrue(listWithZeros.stream().allMatch(elem -> elem == 0));

    Assertions.assertEquals(10, listWithNulls.size());
    Assertions.assertTrue(listWithNulls.stream().allMatch(Objects::isNull));
}
```

limit()函数将一个数字作为参数。这表示流应限制为的元素数，并且该方法返回一个新的Stream，该新Stream由从原始流中选取的对象组成。

## 5. 使用IntStream

[我们可以使用IntStream](https://www.baeldung.com/java-intstream-convert)类用所需的数值初始化列表。这是一个派生自BaseStream的类，就像Stream接口一样。这意味着此类能够完成Stream类能够完成的大多数事情。这个类让我们创建一个原始数字流。然后我们使用boxed()函数将基元包装到对象中。之后，我们可以轻松收集所有生成的数字：

```java
@Test
public void whenInitializingListWithIntStream_thenListIsCorrectlyPopulated() {
    // when
    ArrayList<Integer> list = IntStream.of(new int[10])
      .boxed()
      .collect(Collectors.toCollection(ArrayList::new));

    // then
    Assertions.assertEquals(10, list.size());
    Assertions.assertTrue(list.stream().allMatch(elem -> elem == 0));
}
```

我们还应该考虑到此方法仅适用于插入原始数字。因此，我们不能使用此方法来初始化具有空值的列表。

## 6. 使用Arrays.asList

asList()是java.util.Arrays类的一个方法。使用此方法，我们可以将数组转换为集合。所以，对于这个方法，我们应该初始化一个数组。因为我们的数组在初始化时只包含空值，所以我们使用fill()方法用我们想要的值填充它，在我们的例子中是0。此方法的工作方式类似于nCopies()，使用作为参数给定的值填充我们的数组。用零填充数组后，我们最终可以使用toList()函数将其转换为列表：

```java
@Test
public void whenInitializingListWithAsList_thenListIsCorrectlyPopulated() {
    // when
    Integer[] integers = new Integer[10];
    Arrays.fill(integers, 0);
    List<Integer> integerList = Arrays.asList(integers);

    // then
    Assertions.assertEquals(10, integerList.size());
    Assertions.assertTrue(integerList.stream().allMatch(elem -> elem == 0));
}
```

在这个例子中，我们应该考虑到我们得到的是一个List而不是ArrayList。如果我们尝试向列表中添加一个新元素，我们将得到一个UnsupportedOperationException。使用上一节介绍的方法可以轻松解决此问题。我们需要将List转换为ArrayList。我们可以通过将integerList声明更改为：

```java
List<Integer> integerList = new ArrayList<>(Arrays.asList(integers));
```

此外，我们可以通过删除fill()方法调用来使此方法将空值添加到我们的列表中。如前所述，数组默认初始化为空值。

## 7. 使用Vector类

与ArrayList类一样，Java Vector类表示可增长的对象数组。此外，Vector是实现List接口的Java遗留类。所以我们可以轻松地将其转换为列表。然而，尽管这两个实体之间有很多相似之处，[但它们是不同的](https://www.baeldung.com/java-arraylist-vs-vector)并且有不同的用例。一个相当显着的区别是Vector类的所有方法都是同步的。

Vector在我们的问题中的优势在于它可以用任意数量的元素进行初始化。除此之外，它的所有元素默认情况下都是空的：

```java
@Test
public void whenInitializingListWithVector_thenListIsCorrectlyPopulated() {
    // when
    List<Integer> integerList = new Vector<>() {{setSize(10);}};

    // then
    Assertions.assertEquals(10, integerList.size());
    Assertions.assertTrue(integerList.stream().allMatch(elem -> elem == null));
}
```

我们使用函数setSize()来用所需数量的元素初始化Vector。之后，Vector将用空值填充自身。我们必须考虑到只有当我们想在我们的列表中插入空值时，这个方法才会对我们有帮助。

我们还可以像前面的示例一样使用ArrayList类的构造函数或使用方法addAll()将所有元素添加到新初始化的空ArrayList中，从而将列表转换为ArrayList。

## 8. 总结

在这个快速教程中，我们探讨了需要使用null或0值初始化ArrayList时的所有备选方案。特别是，我们通过了使用流、数组、向量或示例循环的示例。
