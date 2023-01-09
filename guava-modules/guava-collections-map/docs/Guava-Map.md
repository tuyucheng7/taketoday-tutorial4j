## 1. 概述

在本教程中，我们将说明你可以利用 Guava 来处理Java地图的最有用的方法。

让我们从非常简单的开始，使用 Guava创建一个 没有new运算符的HashMap ：

```java
Map<String, String> aNewMap = Maps.newHashMap();
```

## 2.不可变映射

接下来——让我们看看如何使用Guava创建ImmutableMap：

```java
@Test
public void whenCreatingImmutableMap_thenCorrect() {
    Map<String, Integer> salary = ImmutableMap.<String, Integer> builder()
      .put("John", 1000)
      .put("Jane", 1500)
      .put("Adam", 2000)
      .put("Tom", 2000)
      .build();

    assertEquals(1000, salary.get("John").intValue());
    assertEquals(2000, salary.get("Tom").intValue());
}
```

## 3.SortedMap _

现在——让我们来看看创建和使用SortedMap的过程。

在下面的例子中——我们使用相应的 Guava 构建器创建一个排序的地图：

```java
@Test
public void whenUsingSortedMap_thenKeysAreSorted() {
    ImmutableSortedMap<String, Integer> salary = new ImmutableSortedMap
      .Builder<String, Integer>(Ordering.natural())
      .put("John", 1000)
      .put("Jane", 1500)
      .put("Adam", 2000)
      .put("Tom", 2000)
      .build();

    assertEquals("Adam", salary.firstKey());
    assertEquals(2000, salary.lastEntry().getValue().intValue());
}
```

## 4.BiMap_ _

接下来——让我们讨论如何使用BiMap。我们可以使用BiMap将键映射回值，因为它确保值是唯一的。

在下面的例子中——我们创建了一个BiMap并且我们得到了它的inverse()：

```java
@Test
public void whenCreateBiMap_thenCreated() {
    BiMap<String, Integer> words = HashBiMap.create();
    words.put("First", 1);
    words.put("Second", 2);
    words.put("Third", 3);

    assertEquals(2, words.get("Second").intValue());
    assertEquals("Third", words.inverse().get(3));
}
```

## 5.多图

现在——让我们来看看Multimap。

我们可以使用Multimap将每个键与多个值相关联，如下例所示：

```java
@Test
public void whenCreateMultimap_thenCreated() {
    Multimap<String, String> multimap = ArrayListMultimap.create();
    multimap.put("fruit", "apple");
    multimap.put("fruit", "banana");
    multimap.put("pet", "cat");
    multimap.put("pet", "dog");

    assertThat(multimap.get("fruit"), containsInAnyOrder("apple", "banana"));
    assertThat(multimap.get("pet"), containsInAnyOrder("cat", "dog"));
}
```

## 5.表

现在让我们看一下番石榴表；如果我们需要多个键来索引一个值，我们使用表。

在下面的例子中——我们将使用一个表来存储城市之间的距离：

```java
@Test
public void whenCreatingTable_thenCorrect() {
    Table<String,String,Integer> distance = HashBasedTable.create();
    distance.put("London", "Paris", 340);
    distance.put("New York", "Los Angeles", 3940);
    distance.put("London", "New York", 5576);

    assertEquals(3940, distance.get("New York", "Los Angeles").intValue());
    assertThat(distance.columnKeySet(), 
      containsInAnyOrder("Paris", "New York", "Los Angeles"));
    assertThat(distance.rowKeySet(), containsInAnyOrder("London", "New York"));
}
```

我们还可以使用Tables.transpose()翻转行和列键，如下例所示：

```java
@Test
public void whenTransposingTable_thenCorrect() {
    Table<String,String,Integer> distance = HashBasedTable.create();
    distance.put("London", "Paris", 340);
    distance.put("New York", "Los Angeles", 3940);
    distance.put("London", "New York", 5576);

    Table<String, String, Integer> transposed = Tables.transpose(distance);

    assertThat(transposed.rowKeySet(), 
      containsInAnyOrder("Paris", "New York", "Los Angeles"));
    assertThat(transposed.columnKeySet(), containsInAnyOrder("London", "New York"));
}
```

## 6.类到实例映射

接下来 – 让我们看一下ClassToInstanceMap。如果我们希望对象的类成为键，则可以使用ClassToInstanceMap，如下例所示：

```java
@Test
public void whenCreatingClassToInstanceMap_thenCorrect() {
    ClassToInstanceMap<Number> numbers = MutableClassToInstanceMap.create();
    numbers.putInstance(Integer.class, 1);
    numbers.putInstance(Double.class, 1.5);

    assertEquals(1, numbers.get(Integer.class));
    assertEquals(1.5, numbers.get(Double.class));
}
```

## 7.使用Multimap分组列表

接下来 – 让我们看看如何使用Multimap对List进行分组。在下面的示例中——我们使用Multimaps.index()按名称的长度对名称列表进行分组：

```java
@Test
public void whenGroupingListsUsingMultimap_thenGrouped() {
    List<String> names = Lists.newArrayList("John", "Adam", "Tom");
    Function<String,Integer> func = new Function<String,Integer>(){
        public Integer apply(String input) {
            return input.length();
        }
    };
    Multimap<Integer, String> groups = Multimaps.index(names, func);

    assertThat(groups.get(3), containsInAnyOrder("Tom"));
    assertThat(groups.get(4), containsInAnyOrder("John", "Adam"));
}
```

## 八. 总结

在本快速教程中，我们讨论了使用 Guava 库处理地图的最常见和最有用的用例。