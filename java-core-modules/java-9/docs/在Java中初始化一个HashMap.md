## 1. 概述

在本教程中，我们介绍在Java中初始化HashMap的各种方法，我们将使用Java 8和Java 9。

## 2. 静态HashMap的静态初始化器

我们可以使用静态代码块初始化HashMap：

```java
public static Map<String, String> articleMapOne;

static {
    articleMapOne = new HashMap<>();
    articleMapOne.put("ar01", "Intro to Map");
    articleMapOne.put("ar02", "Some article");
}
```

这种初始化的有点是Map是可变的，但它只对静态有效。因此，可以根据需要添加和删除条目entry。

让我们编写一个测试：

```java
@Test
void givenStaticMap_whenUpdated_thenCorrect() {
    MapInitializer.articleMapOne.put("NewArticle1", "Convert array to List");
    
    assertEquals(MapInitializer.articleMapOne.get("NewArticle1"), "Convert array to List");  
}
```

我们还可以使用双大括号语法初始化Map：

```java
Map<String, String> doubleBraceMap  = new HashMap<String, String>() {{
    put("key1", "value1");
    put("key2", "value2");
}};
```

请注意，我们必须尽量避免使用这种初始化技术，因为它在每次使用时都会创建一个匿名的额外类，保存对封闭对象的隐藏引用，并可能导致内存泄漏问题。

## 3. 使用Collections

如果我们需要创建带有单个entry的单例不可变Map，Collections.singletonMap()变得非常有用：

```java
public static Map<String, String> createSingletonMap() {
    return Collections.singletonMap("username1", "password1");
}
```

请注意，这里的Map是不可变的，如果我们尝试添加更多键值对，它会抛出java.lang.UnsupportedOperationException。

我们还可以使用Collections.emptyMap()创建一个不可变的空Map：

```java
Map<String, String> emptyMap = Collections.emptyMap();
```

## 4.Java8方法

在本节中，让我们看看使用Java 8 Stream API初始化Map的方法。

### 4.1 使用Collectors.toMap()

让我们使用一个二维字符串数组的Stream并将它们收集到一个地图中：

```java
Map<String, String> map = Stream.of(new String[][] {
    { "Hello", "World" }, 
    { "John", "Doe" }, 
}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
```

注意，这里Map的key和value的数据类型是相同的。

为了使其更通用，我们使用Objects数组并执行相同的操作：

```java
 Map<String, Integer> map = Stream.of(new Object[][] { 
     { "data1", 1 }, 
     { "data2", 2 }, 
 }).collect(Collectors.toMap(data -> (String) data[0], data -> (Integer) data[1]));
```

因此，我们创建的Map的key为String，value为Integer。

### 4.2 使用Map.Entry流

这里我们将使用Map.Entry的实例。这是另一种方法，我们有不同的key和value类型。

首先，让我们使用Entry接口的SimpleEntry实现：

```java
Map<String, Integer> map = Stream.of(
  new AbstractMap.SimpleEntry<>("idea", 1), 
  new AbstractMap.SimpleEntry<>("mobile", 2))
  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
```

然后我们使用SimpleImmutableEntry实现创建Map：

```java
Map<String, Integer> map = Stream.of(
  new AbstractMap.SimpleImmutableEntry<>("idea", 1),    
  new AbstractMap.SimpleImmutableEntry<>("mobile", 2))
  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
```

### 4.3 初始化不可变Map

在某些用例中，我们需要初始化一个不可变Map，这可以通过将Collectors.toMap()包装在Collectors.collectingAndThen()中来完成：

```java
Map<String, String> map = Stream.of(new String[][] { 
    { "Hello", "World" }, 
    { "John", "Doe" },
}).collect(Collectors.collectingAndThen(
    Collectors.toMap(data -> data[0], data -> data[1]), 
    Collections::<String, String> unmodifiableMap));
```

请注意，我们应该避免使用Stream进行此类初始化，因为它可能会产生巨大的性能开销，并且会创建大量垃圾对象来初始化Map。

## 5.Java9方式

Java 9在Map接口中提供有各种工厂方法，可简化不可变Map的创建和初始化。

### 5.1 Map.of()

这个工厂方法不接收参数、单个参数和可变参数：

```java
Map<String, String> emptyMap = Map.of();
Map<String, String> singletonMap = Map.of("key", "value");
Map<String, String> map = Map.of("key1","value1", "key2", "value2");
```

请注意，此方法最多仅支持10个键值对。

### 5.2 Map.ofEntries()

它类似于Map.of()，但对键值对的数量没有限制：

```java
Map<String, String> map = Map.ofEntries(
		new AbstractMap.SimpleEntry<>("name", "John"),
		new AbstractMap.SimpleEntry<>("city", "budapest"),
		new AbstractMap.SimpleEntry<>("zip", "000000"),
		new AbstractMap.SimpleEntry<>("home", "1231231231")
);
```

请注意，工厂方法会生成不可变的Map，因此任何突变都会导致UnsupportedOperationException。此外，它们不允许空key或重复key。

如果我们在初始化后需要一个可变或不断增长的Map，我们可以创建Map接口的任何实现，并在构造函数中传递这些不可变映射：

```java
Map<String, String> map = new HashMap<String, String>(Map.of("key1", "value1", "key2", "value2"));
Map<String, String> map2 = new HashMap<String, String>(Map.ofEntries(
		new AbstractMap.SimpleEntry<String, String>("name", "John"),
		new AbstractMap.SimpleEntry<String, String>("city", "budapest")));
```

## 6. 使用Guava

Guava库也提供了初始化Map的工具方法

```java
Map<String, String> articles  = ImmutableMap.of("Title", "My New Article", "Title2", "Second Article");
```

上面的代码将创建一个不可变的Map，以下代码创建一个可变的Map：

```java
Map<String, String> articles = Maps.newHashMap(ImmutableMap.of("Title", "My New Article", "Title2", "Second Article"));
```

[ImmutableMap.of()](https://guava.dev/releases/23.0/api/docs/com/google/common/collect/ImmutableMap.html#of--)方法也有重载版本，最多可以使用5对键值参数。以下是传递2对参数的示例：

```java
ImmutableMap.of("key1", "value1", "key2", "value2");
```

## 7. 总结

在本文中，我们探讨了初始化Map的各种方法，特别是创建空的、单例的、不可变的和可变的Map。特别是从Java 9以来，有更多的工具方法可供我们选择。