## 1. 概述

在本快速教程中，我们将讨论Collectors类的toMap()方法。我们将使用它将Stream收集到Map实例中。

对于此处涵盖的所有示例，我们将使用书籍列表作为起点并将其转换为不同的Map实现。

## 延伸阅读：

## [Java 8 收集器指南](https://www.baeldung.com/java-8-collectors)

本文讨论了Java8 收集器，展示了内置收集器的示例，并展示了如何构建自定义收集器。

[阅读更多](https://www.baeldung.com/java-8-collectors)→

## [将Java流收集到不可变集合](https://www.baeldung.com/java-stream-immutable-collection)

了解如何将Java流收集到不可变集合中。

[阅读更多](https://www.baeldung.com/java-stream-immutable-collection)→

## [Java 9 中的新流收集器](https://www.baeldung.com/java9-stream-collectors)

在本文中，我们探索了 JDK 9 中引入的新 Stream 收集器

[阅读更多](https://www.baeldung.com/java9-stream-collectors)→

## 2.列表到地图

我们将从最简单的情况开始，将List转换为Map。

下面是我们如何定义Book类：

```java
class Book {
    private String name;
    private int releaseYear;
    private String isbn;
    
    // getters and setters
}
```

我们将创建一个书籍列表来验证我们的代码：

```java
List<Book> bookList = new ArrayList<>();
bookList.add(new Book("The Fellowship of the Ring", 1954, "0395489318"));
bookList.add(new Book("The Two Towers", 1954, "0345339711"));
bookList.add(new Book("The Return of the King", 1955, "0618129111"));
```

对于这种情况，我们将使用toMap()方法的以下重载：

```java
Collector<T, ?, Map<K,U>> toMap(Function<? super T, ? extends K> keyMapper,
  Function<? super T, ? extends U> valueMapper)
```

使用toMap，我们可以指明如何获取地图的键和值的策略：

```java
public Map<String, String> listToMap(List<Book> books) {
    return books.stream().collect(Collectors.toMap(Book::getIsbn, Book::getName));
}
```

我们可以很容易地验证它是否有效：

```java
@Test
public void whenConvertFromListToMap() {
    assertTrue(convertToMap.listToMap(bookList).size() == 3);
}
```

## 3. 解决关键冲突

上面的示例运行良好，但是重复密钥会发生什么情况呢？

让我们想象一下，我们通过每个Book的发行年份来键入我们的Map ：

```java
public Map<Integer, Book> listToMapWithDupKeyError(List<Book> books) {
    return books.stream().collect(
      Collectors.toMap(Book::getReleaseYear, Function.identity()));
}
```

鉴于我们之前的书籍列表，我们会看到一个IllegalStateException：

```java
@Test(expected = IllegalStateException.class)
public void whenMapHasDuplicateKey_without_merge_function_then_runtime_exception() {
    convertToMap.listToMapWithDupKeyError(bookList);
}
```

要解决它，我们需要使用带有附加参数的不同方法，即mergeFunction：

```java
Collector<T, ?, M> toMap(Function<? super T, ? extends K> keyMapper,
  Function<? super T, ? extends U> valueMapper,
  BinaryOperator<U> mergeFunction)

```

让我们引入一个合并函数，它指示在发生碰撞的情况下，我们保留现有条目：

```java
public Map<Integer, Book> listToMapWithDupKey(List<Book> books) {
    return books.stream().collect(Collectors.toMap(Book::getReleaseYear, Function.identity(),
      (existing, replacement) -> existing));
}
```

或者换句话说，我们得到了先赢的行为：

```java
@Test
public void whenMapHasDuplicateKeyThenMergeFunctionHandlesCollision() {
    Map<Integer, Book> booksByYear = convertToMap.listToMapWithDupKey(bookList);
    assertEquals(2, booksByYear.size());
    assertEquals("0395489318", booksByYear.get(1954).getIsbn());
}
```

## 4.其他地图类型

默认情况下，toMap()方法将返回一个HashMap。

但是我们可以返回不同的Map实现：

```java
Collector<T, ?, M> toMap(Function<? super T, ? extends K> keyMapper,
  Function<? super T, ? extends U> valueMapper,
  BinaryOperator<U> mergeFunction,
  Supplier<M> mapSupplier)
```

其中mapSupplier是一个函数，它返回一个带有结果的新的空Map。

### 4.1. 列表到ConcurrentMap

让我们采用相同的示例并添加一个mapSupplier函数以返回一个ConcurrentHashMap：

```java
public Map<Integer, Book> listToConcurrentMap(List<Book> books) {
    return books.stream().collect(Collectors.toMap(Book::getReleaseYear, Function.identity(),
      (o1, o2) -> o1, ConcurrentHashMap::new));
}
```

我们将继续测试我们的代码：

```java
@Test
public void whenCreateConcurrentHashMap() {
    assertTrue(convertToMap.listToConcurrentMap(bookList) instanceof ConcurrentHashMap);
}
```

### 4.2. 排序图

最后，让我们看看如何返回排序后的地图。为此，我们将使用[TreeMap](https://www.baeldung.com/java-treemap)作为mapSupplier参数。

因为默认情况下TreeMap是根据其键的自然顺序进行排序的，所以我们不必自己明确地对书籍进行排序：

```java
public TreeMap<String, Book> listToSortedMap(List<Book> books) {
    return books.stream() 
      .collect(
        Collectors.toMap(Book::getName, Function.identity(), (o1, o2) -> o1, TreeMap::new));
}
```

所以在我们的例子中，返回的TreeMap将按书名的字母顺序排序：

```java
@Test
public void whenMapisSorted() {
    assertTrue(convertToMap.listToSortedMap(bookList).firstKey().equals(
      "The Fellowship of the Ring"));
}
```

## 5.总结

在本文中，我们研究了Collectors类的toMap() 方法。它允许我们从Stream创建一个新的Map。

我们还学习了如何解决键冲突和创建不同的地图实现。