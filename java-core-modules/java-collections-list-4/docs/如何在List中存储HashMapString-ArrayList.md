## 1. 概述

在本教程中，我们将讨论如何在Java中将[HashMap](https://www.baeldung.com/java-hashmap)存储在[List中。](https://www.baeldung.com/java-collections)首先，我们将对Java中的HashMap和 List数据结构做一个简短的解释。然后，我们将编写一个简单的代码来解决这个问题。

## 2. Java中的HashMap和List

Java为我们提供了具有各种属性和特性的不同数据结构来存储对象。其中，HashMap是将唯一键映射到值的键值对集合。此外，[List](https://www.baeldung.com/java-arraylist)包含一系列相同类型的对象。

我们可以在这些数据结构中放置简单值或复杂对象。

## 3.将HashMap<String, ArrayList<String>>存储在列表中

让我们举一个简单的例子，我们在其中创建一个HashMap列表。 对于每个书籍类别，都有一个HashMap将书名映射到其作者。

首先，我们定义javaB ookAuthorsMap， 它将Java相关书籍的名称映射到它的作者列表：

```java
HashMap<String, List<String>> javaBooksAuthorsMap = new HashMap<>();

```

此外，我们定义phpBooksAuthorsMap 来保存 PHP 类别的书籍的名称和作者：

```java
HashMap<String, List<String>> phpBooksAuthorsMap = new HashMap<>();
```

然后，我们定义 booksAuthorsMapsList来保存不同类别的HashMap ：

```java
List<HashMap<String, List<String>>> booksAuthorsMapsList = new ArrayList<>();
```

现在，我们有一个包含两个HashMap的列表。

为了测试它，我们可以将一些书籍信息放在javaB ookAuthorsMap 和phpBooksAuthorsMap列表中。然后，我们将它们添加到booksAuthorsMapsList。最后，我们确保将 HashMap添加到 列表中。

让我们看看下面的单元测试：

```java
@Test
public void givenMaps_whenAddToList_thenListContainsMaps() {
    HashMap<String, List<String>> javaBooksAuthorsMap = new HashMap<>();
    HashMap<String, List<String>> phpBooksAuthorsMap = new HashMap<>();
    javaBooksAuthorsMap.put("Head First Java", Arrays.asList("Kathy Sierra", "Bert Bates"));
    javaBooksAuthorsMap.put("Effective Java", Arrays.asList("Joshua Bloch"));
    javaBooksAuthorsMap.put("OCAJavaSE 8",
      Arrays.asList("Kathy Sierra", "Bert Bates", "Elisabeth Robson"));
    phpBooksAuthorsMap.put("The Joy of PHP", Arrays.asList("Alan Forbes"));
    phpBooksAuthorsMap.put("Head First PHP & MySQL",
    Arrays.asList("Lynn Beighley", "Michael Morrison"));

    booksAuthorsMapsList.add(javaBooksAuthorsMap);
    booksAuthorsMapsList.add(phpBooksAuthorsMap);

    assertTrue(booksAuthorsMapsList.get(0).keySet()
      .containsAll(javaBooksAuthorsMap.keySet().stream().collect(Collectors.toList())));
    assertTrue(booksAuthorsMapsList.get(1).keySet()
      .containsAll(phpBooksAuthorsMap.keySet().stream().collect(Collectors.toList())));
}
```

## 4。总结

在本文中，我们讨论了在Java中将 HashMap 存储在 List 中。然后，我们编写了一个简单的示例，其中我们将HashMap<String, ArrayList<String>>添加到两个书籍类别的List<String>中。