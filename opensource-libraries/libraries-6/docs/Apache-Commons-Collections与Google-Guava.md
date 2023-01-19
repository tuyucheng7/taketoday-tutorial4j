## 1. 概述

在本教程中，我们将比较两个基于Java的开源库：[Apache Commons](https://commons.apache.org/)和[Google Guava](https://github.com/google/guava)。这两个库都具有丰富的功能集，其中包含大量主要用于集合和 I/O 领域的实用程序 API。

为简洁起见，这里我们将只描述集合框架中最常用的几个以及代码示例。我们还将看到它们差异的摘要。

此外，我们还有一系列文章可以深入探讨各种[公共](https://www.baeldung.com/?s=apache+commons)和[Guava](https://www.baeldung.com/guava-guide)实用程序。

## 二、两馆简史

Google Guava 是 Google 的一个项目，主要由该组织的工程师开发，不过现在已经开源了。启动它的主要动机是将 JDK 1.5 中引入的泛型包含到JavaCollections Framework或[JCF](https://en.wikipedia.org/wiki/Java_collections_framework)中，并增强其功能。

自成立以来，该库扩展了其功能，现在包括图形、函数式编程、范围对象、缓存和字符串操作。

Apache Commons 最初是一个雅加达项目，用于补充核心Java集合 API，最终成为 Apache 软件基金会的一个项目。多年来，它已经扩展到其他各个领域的大量可重用Java组件，包括(但不限于)成像、I/O、密码学、缓存、网络、验证和对象池。

由于这是一个开源项目，来自 Apache 社区的开发人员不断向该库中添加内容以扩展其功能。但是，他们非常注意保持向后兼容性。

## 3.Maven依赖

要包含 Guava，我们需要将其依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.1-jre</version>
</dependency>
```

它的最新版本信息可以在[Maven](https://search.maven.org/search?q=g:com.google.guava  AND a:guava)上找到。

对于 Apache Commons，它有点不同。根据我们要使用的实用程序，我们必须添加那个特定的实用程序。例如，对于集合，我们需要添加：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.4</version>
</dependency>
```

在我们的代码示例中，我们将使用[commons-collections4](https://search.maven.org/search?q=g:org.apache.commons AND a:commons-collections4)。

现在让我们进入有趣的部分吧！

## 4.双向地图

可以通过键和值访问的映射称为双向映射。JCF 没有这个功能。

让我们看看我们的两种技术如何提供它们。在这两种情况下，我们都会以星期几为例，根据日期的编号获取日期的名称，反之亦然。

### 4.1. Guava 的BiMap

Guava 提供了一个接口—— [BiMap](https://www.baeldung.com/guava-bimap)，作为双向地图。它可以使用其实现EnumBiMap、EnumHashBiMap、HashBiMap或ImmutableBiMap之一进行实例化。

这里我们使用HashBiMap：

```java
BiMap<Integer, String> daysOfWeek = HashBiMap.create();
```

填充它类似于Java中的任何地图：

```java
daysOfWeek.put(1, "Monday");
daysOfWeek.put(2, "Tuesday");
daysOfWeek.put(3, "Wednesday");
daysOfWeek.put(4, "Thursday");
daysOfWeek.put(5, "Friday");
daysOfWeek.put(6, "Saturday");
daysOfWeek.put(7, "Sunday");
```

这里有一些 JUnit 测试来证明这个概念：

```java
@Test
public void givenBiMap_whenValue_thenKeyReturned() {
    assertEquals(Integer.valueOf(7), daysOfWeek.inverse().get("Sunday"));
}

@Test
public void givenBiMap_whenKey_thenValueReturned() {
    assertEquals("Tuesday", daysOfWeek.get(2));
}
```

### 4.2. Apache 的BidiMap

同样，Apache 为我们提供了它的[BidiMap](https://www.baeldung.com/commons-collections-bidi-map)接口：

```java
BidiMap<Integer, String> daysOfWeek = new TreeBidiMap<Integer, String>();
```

这里我们使用TreeBidiMap。但是，还有其他实现，例如DualHashBidiMap和DualTreeBidiMap。

为了填充它，我们可以像上面对 BiMap所做的那样放置值。

它的用法也很相似：

```java
@Test
public void givenBidiMap_whenValue_thenKeyReturned() {
    assertEquals(Integer.valueOf(7), daysOfWeek.inverseBidiMap().get("Sunday"));
}

@Test
public void givenBidiMap_whenKey_thenValueReturned() {
    assertEquals("Tuesday", daysOfWeek.get(2));
}
```

在一些简单的性能测试中，[这个双向映射](https://github.com/eugenp/tutorials/blob/master/libraries-6/src/test/java/com/baeldung/apache/commons/CollectionsUnitTest.java) 仅在插入方面落后于它的[Guava 对应物。](https://github.com/eugenp/tutorials/blob/master/libraries-6/src/test/java/com/baeldung/guava/GuavaUnitTest.java)获取键和值的速度要快得多。

## 5. 将键映射到多个值

对于我们想要将多个键映射到不同值的用例，例如水果和蔬菜的杂货车集合，这两个库为我们提供了独特的解决方案。

### 5.1. Guava 的MultiMap

首先，让我们看看如何实例化和初始化[MultiMap](https://www.baeldung.com/guava-multimap)：

```java
Multimap<String, String> groceryCart = ArrayListMultimap.create();

groceryCart.put("Fruits", "Apple");
groceryCart.put("Fruits", "Grapes");
groceryCart.put("Fruits", "Strawberries");
groceryCart.put("Vegetables", "Spinach");
groceryCart.put("Vegetables", "Cabbage");
```

然后，我们将使用几个 JUnit 测试来查看它的运行情况：

```java
@Test
public void givenMultiValuedMap_whenFruitsFetched_thenFruitsReturned() {
    List<String> fruits = Arrays.asList("Apple", "Grapes", "Strawberries");
    assertEquals(fruits, groceryCart.get("Fruits"));
}

@Test
public void givenMultiValuedMap_whenVeggiesFetched_thenVeggiesReturned() {
    List<String> veggies = Arrays.asList("Spinach", "Cabbage");
    assertEquals(veggies, groceryCart.get("Vegetables"));
}

```

此外，MultiMap使我们能够从地图中删除给定条目或整组值：

```java
@Test
public void givenMultiValuedMap_whenFuitsRemoved_thenVeggiesPreserved() {
    
    assertEquals(5, groceryCart.size());

    groceryCart.remove("Fruits", "Apple");
    assertEquals(4, groceryCart.size());

    groceryCart.removeAll("Fruits");
    assertEquals(2, groceryCart.size());
}
```

正如我们所看到的，这里我们首先从Fruits集中删除了Apple，然后删除了整个Fruits集。

### 5.2. Apache 的MultiValuedMap

同样，让我们从实例化[MultiValuedMap](https://www.baeldung.com/apache-commons-multi-valued-map)开始：

```java
MultiValuedMap<String, String> groceryCart = new ArrayListValuedHashMap<>();
```

由于填充它与我们在上一节中看到的相同，让我们快速看一下用法：

```java
@Test
public void givenMultiValuedMap_whenFruitsFetched_thenFruitsReturned() {
    List<String> fruits = Arrays.asList("Apple", "Grapes", "Strawberries");
    assertEquals(fruits, groceryCart.get("Fruits"));
}

@Test
public void givenMultiValuedMap_whenVeggiesFetched_thenVeggiesReturned() {
    List<String> veggies = Arrays.asList("Spinach", "Cabbage");
    assertEquals(veggies, groceryCart.get("Vegetables"));
}
```

我们可以看到，它的用法也是一样的！

但是，在这种情况下，我们无法灵活地删除单个条目，例如从Fruits中删除Apple 。我们只能删除整组Fruits：

```java
@Test
public void givenMultiValuedMap_whenFuitsRemoved_thenVeggiesPreserved() {
    assertEquals(5, groceryCart.size());

    groceryCart.remove("Fruits");
    assertEquals(2, groceryCart.size());
}
```

## 6. 将多个键映射到一个值

在这里，我们将以纬度和经度映射到各个城市为例：

```java
cityCoordinates.put("40.7128° N", "74.0060° W", "New York");
cityCoordinates.put("48.8566° N", "2.3522° E", "Paris");
cityCoordinates.put("19.0760° N", "72.8777° E", "Mumbai");
```

现在，我们将看看如何实现这一目标。

### 6.1. 番石榴的桌子

Guava 提供了满足上述用例的[表：](https://www.baeldung.com/guava-table)

```java
Table<String, String, String> cityCoordinates = HashBasedTable.create();
```

以下是我们可以从中得出的一些用法：

```java
@Test
public void givenCoordinatesTable_whenFetched_thenOK() {
    
    List expectedLongitudes = Arrays.asList("74.0060° W", "2.3522° E", "72.8777° E");
    assertArrayEquals(expectedLongitudes.toArray(), cityCoordinates.columnKeySet().toArray());

    List expectedCities = Arrays.asList("New York", "Paris", "Mumbai");
    assertArrayEquals(expectedCities.toArray(), cityCoordinates.values().toArray());
    assertTrue(cityCoordinates.rowKeySet().contains("48.8566° N"));
}
```

如我们所见，我们可以获得行、列和值的Set视图。

表还为我们提供了查询其行或列的能力。

让我们考虑一个电影表来证明这一点：

```java
Table<String, String, String> movies = HashBasedTable.create();

movies.put("Tom Hanks", "Meg Ryan", "You've Got Mail");
movies.put("Tom Hanks", "Catherine Zeta-Jones", "The Terminal");
movies.put("Bradley Cooper", "Lady Gaga", "A Star is Born");
movies.put("Keenu Reaves", "Sandra Bullock", "Speed");
movies.put("Tom Hanks", "Sandra Bullock", "Extremely Loud & Incredibly Close");
```

这里有一些示例，我们可以在电影 表上进行不言自明的搜索：

```java
@Test
public void givenMoviesTable_whenFetched_thenOK() {
    assertEquals(3, movies.row("Tom Hanks").size());
    assertEquals(2, movies.column("Sandra Bullock").size());
    assertEquals("A Star is Born", movies.get("Bradley Cooper", "Lady Gaga"));
    assertTrue(movies.containsValue("Speed"));
}
```

但是，Table限制我们只能将两个键映射到一个值。在 Guava 中，我们还没有其他方法可以将两个以上的键映射到一个值。

### 6.2. Apache 的 MultiKeyMap

回到我们的cityCoordinates示例，这里是我们如何使用MultiKeyMap操作它：

```java
@Test
public void givenCoordinatesMultiKeyMap_whenQueried_thenOK() {
    MultiKeyMap<String, String> cityCoordinates = new MultiKeyMap<String, String>();

    // populate with keys and values as shown previously

    List expectedLongitudes = Arrays.asList("72.8777° E", "2.3522° E", "74.0060° W");
    List longitudes = new ArrayList<>();

    cityCoordinates.forEach((key, value) -> {
      longitudes.add(key.getKey(1));
    });
    assertArrayEquals(expectedLongitudes.toArray(), longitudes.toArray());

    List expectedCities = Arrays.asList("Mumbai", "Paris", "New York");
    List cities = new ArrayList<>();

    cityCoordinates.forEach((key, value) -> {
      cities.add(value);
    });
    assertArrayEquals(expectedCities.toArray(), cities.toArray());
}
```

正如我们从上面的代码片段中看到的那样，为了获得与 Guava 的Table相同的断言，我们必须迭代MultiKeyMap。

但是，MultiKeyMap还提供了将两个以上的键映射到一个值的可能性。例如，它使我们能够将一周中的几天映射为工作日或周末：

```java
@Test
public void givenDaysMultiKeyMap_whenFetched_thenOK() {
    days = new MultiKeyMap<String, String>();
    days.put("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Weekday");
    days.put("Saturday", "Sunday", "Weekend");

    assertFalse(days.get("Saturday", "Sunday").equals("Weekday"));
}
```

## 7. Apache Commons Collections 与 Google Guava

[根据其工程师](https://code.google.com/archive/p/google-collections/wikis/Faq.wiki)的说法，Google Guava 的诞生是因为需要在库中使用泛型，而 Apache Commons 没有提供。它还遵循集合 API 要求。另一个主要优势是它处于积极开发阶段，新版本经常发布。

但是，在从集合中获取值时，Apache 在性能方面具有优势。不过，就插入时间而言，番石榴仍然占据优势。

尽管我们只比较了代码示例中的集合 API，但与 Guava 相比，Apache Commons 作为一个整体提供了更大范围的功能。

## 八. 总结

在本教程中，我们比较了 Apache Commons 和 Google Guava 提供的一些功能，特别是在集合框架方面。

在这里，我们只是触及了这两个库所提供的内容的皮毛。

而且，这不是非此即彼的比较。正如我们的代码示例所展示的，两者各有其独特的特性，并且在某些情况下两者可以共存。