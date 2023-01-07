## 1. 概述

我们经常使用映射来存储键值对的集合。然后，在某些时候，我们经常需要[迭代它们](https://www.baeldung.com/java-iterate-map)。

在本教程中，我们将比较不同的地图迭代方法，突出显示何时使用Map.Entry可能有益。然后，我们将了解如何 使用Map.Entry来创建元组。最后，我们将创建一个有序的元组列表。

## 2.优化地图迭代

假设我们有一个以作者姓名为键的书名映射：

```java
Map<String, String> map = new HashMap<>();

map.put("Robert C. Martin", "Clean Code");
map.put("Joshua Bloch", "Effective Java");
```

让我们比较两种从地图中获取所有键和值的方法。

### 2.1. 使用Map.keySet

首先，考虑以下因素：

```java
for (String key : bookMap.keySet()) {
    System.out.println("key: " + key + " value: " + bookMap.get(key));
}
```

在这里，循环遍历keySet。对于每个键，我们使用Map.get获取相应的值。虽然这是使用映射中所有条目的明显方法，但它需要对每个条目进行两次操作——一次获取下一个键，一次使用get查找值。

如果我们只需要映射中的键，keySet是一个不错的选择。但是，有一种更快的方法来获取键和值。

### 2.2. 使用 Map.entrySet代替

让我们重写迭代以使用entrySet：

```java
for (Map.Entry<String, String> book: bookMap.entrySet()) {
    System.out.println("key: " + book.getKey() + " value: " + book.getValue());
}
```

在此示例中，我们的循环遍历Map.Entry对象的集合。由于 Map.Entry将键和值一起存储在一个类中，我们可以在一个操作中同时获取它们。

同样的规则适用于[使用Java8 流操作](https://www.baeldung.com/java-maps-streams)。通过entrySet流式处理和使用 Entry对象效率更高，并且需要的代码更少。

## 3. 使用元组

元组是一种数据结构，具有固定数量和顺序的元素。我们可以认为 Map.Entry是一个存储两个元素的元组——一个键和一个值。但是，由于 Map.Entry是一个接口，我们需要一个实现类。在本节中，我们将探讨 JDK 提供的一种实现：AbstractMap.SimpleEntry。

### 3.1. 创建元组

首先，考虑Book类：

```java
public class Book {
    private String title;
    private String author;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }
    ...
```

接下来，让我们创建一个Map.Entry元组，其中 ISBN 作为键，Book对象作为值：

```java
Map.Entry<String, Book> tuple;

```

最后，让我们用AbstractMap.SimpleEntry实例化我们的元组 ：

```java
tuple = new AbstractMap.SimpleEntry<>("9780134685991", new Book("EffectiveJava3d Edition", "Joshua Bloch"));

```

### 3.2. 创建有序的元组列表

使用元组时，将它们作为有序列表通常很有用。

首先，我们将定义我们的元组列表：

```java
List<Map.Entry<String, Book>> orderedTuples = new ArrayList<>();

```

其次，让我们在列表中添加一些条目：

```java
orderedTuples.add(new AbstractMap.SimpleEntry<>("9780134685991", 
  new Book("EffectiveJava3d Edition", "Joshua Bloch")));
orderedTuples.add(new AbstractMap.SimpleEntry<>("9780132350884", 
  new Book("Clean Code","Robert C Martin")));
```

### 3.3. 与地图比较

为了比较与Map的差异，让我们添加一个具有已存在键的新条目：

```java
orderedTuples.add(new AbstractMap.SimpleEntry<>("9780132350884", 
  new Book("Clean Code", "Robert C Martin")));

```

其次，我们将遍历我们的列表，显示所有键和值：

```java
for (Map.Entry<String, Book> tuple : orderedTuples) {
    System.out.println("key: " + tuple.getKey() + " value: " + tuple.getValue());
}
```

最后，让我们看看输出：

```bash
key: 9780134685991 value: Book{title='EffectiveJava3d Edition', author='Joshua Bloch'}
key: 9780132350884 value: Book{title='Clean Code', author='Robert C Martin'}
key: 9780132350884 value: Book{title='Clean Code', author='Robert C Martin'}
```

请注意，我们可以有重复的键，这与基本的 Map不同，其中每个键都必须是唯一的。这是因为我们使用了List实现来存储我们的SimpleEntry对象，这意味着所有对象都是相互独立的。

### 3.4. 条目对象列表

我们应该注意到 Entry的目的不是充当通用元组。库类通常为此目的提供[一个通用的 Pair类。](https://www.baeldung.com/java-pairs)

但是，我们可能会发现在为 Map准备数据或从 Map 提取数据时需要临时使用条目列表。

## 4。总结

在本文中，我们将 Map.entrySet视为迭代地图键的替代方法。

然后我们研究了如何将Map.Entry用作元组。

最后，我们创建了一个有序元组列表，将差异与基本Map进行比较。