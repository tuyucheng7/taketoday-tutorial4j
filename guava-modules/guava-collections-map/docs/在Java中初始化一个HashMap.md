## 1. 概述

在本教程中，我们将了解 在 Java中初始化HashMap 的各种方法。

我们将使用Java8 和Java9。

## 延伸阅读：

## [比较Java中的两个 HashMap](https://www.baeldung.com/java-compare-hashmaps)

了解如何比较Java中的两个 HashMap 以及找出它们之间的差异

[阅读更多](https://www.baeldung.com/java-compare-hashmaps)→

## [使用流处理地图](https://www.baeldung.com/java-maps-streams)

了解如何结合Java地图和流

[阅读更多](https://www.baeldung.com/java-maps-streams)→

## 2. 静态HashMap的静态初始化器

我们可以 使用静态代码块初始化HashMap ：

```java
public static Map<String, String> articleMapOne;
static {
    articleMapOne = new HashMap<>();
    articleMapOne.put("ar01", "Intro to Map");
    articleMapOne.put("ar02", "Some article");
}
```

这种初始化的优点是映射是可变的，但它只对静态有效。因此，可以根据需要添加和删除条目。

让我们继续测试它：

```java
@Test
public void givenStaticMap_whenUpdated_thenCorrect() {
    
    MapInitializer.articleMapOne.put(
      "NewArticle1", "Convert array to List");
    
    assertEquals(
      MapInitializer.articleMapOne.get("NewArticle1"), 
      "Convert array to List");  
}
```

我们还可以使用双括号语法初始化映射：

```java
Map<String, String> doubleBraceMap  = new HashMap<String, String>() {{
    put("key1", "value1");
    put("key2", "value2");
}};
```

请注意，我们必须尽量避免这种初始化技术，因为它会在每次使用时创建一个额外的匿名类，持有对封闭对象的隐藏引用，并可能导致内存泄漏问题。

## 3. 使用Java集合

如果我们需要创建一个带有单个条目的单例不可变映射，Collections.singletonMap() 会变得非常有用：

```java
public static Map<String, String> createSingletonMap() {
    return Collections.singletonMap("username1", "password1");
}
```

请注意，此处的映射是不可变的，如果我们尝试添加更多条目，它将抛出java.lang.UnsupportedOperationException。

我们还可以使用 Collections.emptyMap() 创建一个不可变的空映射：

```java
Map<String, String> emptyMap = Collections.emptyMap();
```

## 4.Java8 之道

在本节中，让我们看看使用Java8 Stream API 初始化地图的方法。

### 4.1. 使用 Collectors.toMap()

让我们使用一个二维字符串数组的Stream并将它们收集到一个映射中：

```java
Map<String, String> map = Stream.of(new String[][] {
  { "Hello", "World" }, 
  { "John", "Doe" }, 
}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
```

注意这里key和map的value的数据类型是一样的。

为了使其更通用，让我们获取对象 数组并执行相同的操作：

```java
 Map<String, Integer> map = Stream.of(new Object[][] { 
     { "data1", 1 }, 
     { "data2", 2 }, 
 }).collect(Collectors.toMap(data -> (String) data[0], data -> (Integer) data[1]));
```

因此，我们创建了一个键映射为String，值映射为Integer。

### 4.2. 使用 Map.Entry流

这里我们将使用 Map.Entry 的实例。 这是另一种方法，我们有不同的键和值类型。

首先，让我们使用 Entry 接口的SimpleEntry 实现 ：

```java
Map<String, Integer> map = Stream.of(
  new AbstractMap.SimpleEntry<>("idea", 1), 
  new AbstractMap.SimpleEntry<>("mobile", 2))
  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
```

现在让我们使用SimpleImmutableEntry 实现来创建地图：

```java
Map<String, Integer> map = Stream.of(
  new AbstractMap.SimpleImmutableEntry<>("idea", 1),    
  new AbstractMap.SimpleImmutableEntry<>("mobile", 2))
  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
```

### 4.3. 初始化不可变映射

在某些用例中，我们需要初始化一个不可变映射。这可以通过将 Collectors.toMap() 包装在Collectors.collectingAndThen ()中来完成：

```java
Map<String, String> map = Stream.of(new String[][] { 
    { "Hello", "World" }, 
    { "John", "Doe" },
}).collect(Collectors.collectingAndThen(
    Collectors.toMap(data -> data[0], data -> data[1]), 
    Collections::<String, String> unmodifiableMap));
```

请注意，我们应该避免使用Streams 进行此类初始化， 因为它可能会导致巨大的性能开销，并且会创建大量垃圾对象来初始化地图。

## 5.Java9 之道

Java 9 在Map接口中提供了各种工厂方法 ，可简化不可变映射的创建和初始化。

让我们继续研究这些工厂方法。

### 5.1. 地图.of()

此工厂方法不带参数、单个参数和可变参数：

```java
Map<String, String> emptyMap = Map.of();
Map<String, String> singletonMap = Map.of("key1", "value");
Map<String, String> map = Map.of("key1","value1", "key2", "value2");
```

请注意，此方法最多只支持 10 个键值对。

### 5.2. 地图.ofEntries()

它类似于 Map.of() 但对键值对的数量没有限制：

```java
Map<String, String> map = Map.ofEntries(
  new AbstractMap.SimpleEntry<String, String>("name", "John"),
  new AbstractMap.SimpleEntry<String, String>("city", "budapest"),
  new AbstractMap.SimpleEntry<String, String>("zip", "000000"),
  new AbstractMap.SimpleEntry<String, String>("home", "1231231231")
);
```

请注意，工厂方法会生成不可变映射，因此任何更改都将导致 UnsupportedOperationException。

此外，它们不允许空键或重复键。

现在，如果我们在初始化后需要一个可变或不断增长的映射，我们可以创建 Map接口的任何实现，并在构造函数中传递这些不可变映射：

```java
Map<String, String> map = new HashMap<String, String> (
  Map.of("key1","value1", "key2", "value2"));
Map<String, String> map2 = new HashMap<String, String> (
  Map.ofEntries(
    new AbstractMap.SimpleEntry<String, String>("name", "John"),    
    new AbstractMap.SimpleEntry<String, String>("city", "budapest")));
```

## 6.使用番石榴

在我们研究了使用核心Java的方法后，让我们继续使用 Guava 库初始化地图：

```java
Map<String, String> articles 
  = ImmutableMap.of("Title", "My New Article", "Title2", "Second Article");
```

这将创建一个不可变映射，并创建一个可变映射：

```java
Map<String, String> articles 
  = Maps.newHashMap(ImmutableMap.of("Title", "My New Article", "Title2", "Second Article"));
```

方法 [ImmutableMap.of()](https://guava.dev/releases/23.0/api/docs/com/google/common/collect/ImmutableMap.html#of--) 也有重载版本，最多可以接受 5 对键值参数。下面是带有 2 对参数的示例：

```java
ImmutableMap.of("key1", "value1", "key2", "value2");
```

## 七. 总结

在本文中，我们探讨了初始化Map的各种方法，特别是创建空的、单例的、不可变的和可变的映射。正如我们所看到的，自Java9 以来，该领域有了巨大的改进。 