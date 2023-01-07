## 1. 概述

在本教程中，我们将探讨在Java中将Iterable转换为Collection的不同方法。

我们将从纯Java解决方案开始，然后查看 Guava 和 Apache Commons 库也提供的选项。

## 2.可迭代和迭代器

首先，我们将定义我们的Iterable：

```java
Iterable<String> iterable = Arrays.asList("john", "tom", "jane");
```

我们还将定义一个简单的Iterator —— 以强调将Iterable转换为Collection和将Iterator转换为Collection之间的区别：

```java
Iterator<String> iterator = iterable.iterator();
```

## 3. 使用纯 Java

### 3.1. 可迭代到集合

我们可以使用Java8 forEach()方法将所有元素添加到List 中：

```java
@Test
public void whenConvertIterableToListUsingJava8_thenSuccess() {
    List<String> result = new ArrayList<String>();
    iterable.forEach(result::add);

    assertThat(result, contains("john", "tom", "jane"));
}
```

或者使用Spliterator类将我们的Iterable转换为Stream，然后再转换为Collection：

```java
List<String> result = 
  StreamSupport.stream(iterable.spliterator(), false)
    .collect(Collectors.toList());
```

### 3.2. 集合的迭代器

另一方面，我们不使用forEach() ，而是使用带有Iterator的forEachRemaining()：

```java
@Test
public void whenConvertIteratorToListUsingJava8_thenSuccess() {
    List<String> result = new ArrayList<String>();
    iterator.forEachRemaining(result::add);

    assertThat(result, contains("john", "tom", "jane"));
}
```

我们还可以从Iterator创建一个Spliterator ，然后使用它将Iterator转换为Stream：

```java
List<String> result = 
  StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
    .collect(Collectors.toList());
```

### 3.3. 使用 For 循环

让我们也看一下使用非常简单的 for 循环将Iterable转换为List的解决方案：

```java
@Test
public void whenConvertIterableToListUsingJava_thenSuccess() {
    List<String> result = new ArrayList<String>();
    for (String str : iterable) {
        result.add(str);
    }

    assertThat(result, contains("john", "tom", "jane"));
}
```

另一方面，我们将hasNext()和next()与迭代器一起使用：

```java
@Test
public void whenConvertIteratorToListUsingJava_thenSuccess() {
    List<String> result = new ArrayList<String>();
    while (iterator.hasNext()) {
        result.add(iterator.next());
    }

    assertThat(result, contains("john", "tom", "jane"));
}
```

## 4.使用番石榴

还有一些可用的库提供了方便的方法来帮助我们实现这一目标。

让我们看看如何使用[Guava](https://www.baeldung.com/guava-collections)将Iterable转换为List：

我们可以使用Lists.newArrayList()从Iterable或Iterator创建一个新的List：

```java
List<String> result = Lists.newArrayList(iterable);
```

或者我们可以使用ImmutableList.copyOf()：

```java
List<String> result = ImmutableList.copyOf(iterable);
```

## 5. 使用 Apache Commons

最后，我们将使用[Apache Commons ](https://www.baeldung.com/java-commons-lang-3)IterableUtils从Iterable创建一个列表：

```java
List<String> result = IterableUtils.toList(iterable);
```

同样，我们将使用IteratorUtils从Iterator创建一个List：

```java
List<String> result = IteratorUtils.toList(iterator);
```

## 六，总结

在这篇简短的文章中，我们学习了如何使用 Java将Iterable和Iterator转换为Collection 。我们探索了使用普通Java和两个外部库的不同方式：Guava 和 Apache Commons。