## 1. 概述

在本文中，我们将了解Java集合框架中的ArrayList类。我们将讨论它的属性、常见用例以及优缺点。

ArrayList驻留在Java核心库中，因此你不需要任何额外的库。为了使用它，只需添加以下导入语句：

```java
import java.util.ArrayList;
```

列表表示值的有序序列，其中某些值可能出现多次。

ArrayList是构建在数组之上的List实现之一，它能够在你添加/删除元素时动态增长和收缩。可以通过从零开始的索引轻松访问元素。此实现具有以下属性：

-   随机访问需要O(1)时间
-   添加元素需要分摊常数时间O(1)
-   插入/删除需要O(n)时间
-   未排序数组的搜索时间为O(n) ，已排序数组的搜索时间为O(log n)

## 2.创建一个ArrayList

ArrayList有几个构造函数，我们将在本节中介绍它们。

首先，请注意ArrayList是一个泛型类，因此你可以使用你想要的任何类型对其进行参数化，并且编译器将确保，例如，你将无法将Integer值放入Strings集合中。此外，从集合中检索元素时不需要转换元素。

其次，将通用接口List用作变量类型是一种很好的做法，因为它将它与特定实现分离。

### 2.1. 默认无参数构造函数

```java
List<String> list = new ArrayList<>();
assertTrue(list.isEmpty());
```

我们只是创建一个空的ArrayList实例。

### 2.2. 构造函数接受初始容量

```java
List<String> list = new ArrayList<>(20);
```

你可以在此处指定基础数组的初始长度。这可以帮助你避免在添加新项目时不必要地调整大小。

### 2.3. 构造函数接受集合

```java
Collection<Integer> numbers 
  = IntStream.range(0, 10).boxed().collect(toSet());

List<Integer> list = new ArrayList<>(numbers);
assertEquals(10, list.size());
assertTrue(numbers.containsAll(list));
```

请注意，Collection实例的该元素用于填充基础数组。

## 3. 将元素添加到ArrayList

你可以在末尾或特定位置插入一个元素：

```java
List<Long> list = new ArrayList<>();

list.add(1L);
list.add(2L);
list.add(1, 3L);

assertThat(Arrays.asList(1L, 3L, 2L), equalTo(list));
```

你也可以一次插入一个集合或多个元素：

```java
List<Long> list = new ArrayList<>(Arrays.asList(1L, 2L, 3L));
LongStream.range(4, 10).boxed()
  .collect(collectingAndThen(toCollection(ArrayList::new), ys -> list.addAll(0, ys)));
assertThat(Arrays.asList(4L, 5L, 6L, 7L, 8L, 9L, 1L, 2L, 3L), equalTo(list));
```

## 4. 遍历ArrayList 

有两种类型的迭代器可用：Iterator和ListIterator。

前者使你有机会单向遍历列表，而后者则允许你双向遍历列表。

在这里，我们将只向你展示ListIterator：

```java
List<Integer> list = new ArrayList<>(
  IntStream.range(0, 10).boxed().collect(toCollection(ArrayList::new))
);
ListIterator<Integer> it = list.listIterator(list.size());
List<Integer> result = new ArrayList<>(list.size());
while (it.hasPrevious()) {
    result.add(it.previous());
}

Collections.reverse(list);
assertThat(result, equalTo(list));
```

你还可以使用迭代器搜索、添加或删除元素。

## 5.搜索ArrayList 

我们将演示如何使用集合进行搜索：

```java
List<String> list = LongStream.range(0, 16)
  .boxed()
  .map(Long::toHexString)
  .collect(toCollection(ArrayList::new));
List<String> stringsToSearch = new ArrayList<>(list);
stringsToSearch.addAll(list);
```

### 5.1. 搜索未排序的列表

为了找到一个元素，你可以使用indexOf()或lastIndexOf()方法。他们都接受一个对象并返回int值：

```java
assertEquals(10, stringsToSearch.indexOf("a"));
assertEquals(26, stringsToSearch.lastIndexOf("a"));
```

如果你想找到满足谓词的所有元素，你可以使用Java8 Stream API ([在此处](https://www.baeldung.com/java-8-streams)阅读更多相关信息)使用Predicate过滤集合，如下所示：

```java
Set<String> matchingStrings = new HashSet<>(Arrays.asList("a", "c", "9"));

List<String> result = stringsToSearch
  .stream()
  .filter(matchingStrings::contains)
  .collect(toCollection(ArrayList::new));

assertEquals(6, result.size());
```

也可以使用for循环或迭代器：

```java
Iterator<String> it = stringsToSearch.iterator();
Set<String> matchingStrings = new HashSet<>(Arrays.asList("a", "c", "9"));

List<String> result = new ArrayList<>();
while (it.hasNext()) {
    String s = it.next();
    if (matchingStrings.contains(s)) {
        result.add(s);
    }
}
```

### 5.2. 搜索排序列表

如果你有一个排序数组，那么你可以使用比线性搜索更快的二进制搜索算法：

```java
List<String> copy = new ArrayList<>(stringsToSearch);
Collections.sort(copy);
int index = Collections.binarySearch(copy, "f");
assertThat(index, not(equalTo(-1)));
```

请注意，如果未找到元素，则将返回 -1。

## 6. 从ArrayList中删除元素

为了删除一个元素，你应该找到它的索引，然后才通过remove()方法执行删除。此方法的重载版本，它接受一个对象，搜索它并执行删除第一次出现的相等元素：

```java
List<Integer> list = new ArrayList<>(
  IntStream.range(0, 10).boxed().collect(toCollection(ArrayList::new))
);
Collections.reverse(list);

list.remove(0);
assertThat(list.get(0), equalTo(8));

list.remove(Integer.valueOf(0));
assertFalse(list.contains(0));
```

但是在使用诸如Integer之类的盒装类型时要小心。为了删除一个特定的元素，你应该首先装箱int值，否则，一个元素将被它的索引删除。

你也可以使用前面提到的Stream API来删除多个项目，但我们不会在这里展示。为此，我们将使用迭代器：

```java
Set<String> matchingStrings
 = HashSet<>(Arrays.asList("a", "b", "c", "d", "e", "f"));

Iterator<String> it = stringsToSearch.iterator();
while (it.hasNext()) {
    if (matchingStrings.contains(it.next())) {
        it.remove();
    }
}
```

## 七、总结

在这篇快速文章中，我们了解了Java中的 ArrayList。

我们展示了如何创建ArrayList实例，如何使用不同的方法添加、查找或删除元素。