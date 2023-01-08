## 1. 概述

在本快速教程中，我们将学习对Java8 流中的项目进行操作并在操作完成后将其删除的各种方法。

## 2.设置

让我们先定义我们的Item对象。这是一个具有单个int字段的简单对象。

它有一个判断对象是否符合操作条件的方法，根据内部值：

```java
class Item {
    private int value;

    // constructors

    public boolean isQualified() {
        return value % 2 == 0;
    }

    public void operate() {
        System.out.println("Even Number");
    }
}
```

现在我们可以为Stream创建一个源，它可以是Items 的集合：

```java
List<Item> itemList = new ArrayList<>();
for (int i = 0; i < 10; i++) {
    itemList.add(new Item(i));
}
```

## 3.过滤项目

在很多情况下，如果我们想对项目的子集做一些事情，我们可以使用Stream#filter方法，我们不需要先删除任何东西：

```java
itemList.stream()
  .filter(item -> item.isQualified())
  ...
```

## 4.操作和删除项目

### 4.1. 集合.removeIf

我们可以使用Streams来迭代和操作Items的集合。

使用Streams，我们可以应用称为Predicates的 lambda 函数。要阅读更多关于流和谓词的信息，我们[在这里](https://www.baeldung.com/java-8-streams-introduction)有另一篇文章。

我们可以创建一个Predicate来确定一个Item是否有资格被操作：

```java
Predicate<Item> isQualified = item -> item.isQualified();
```

我们的谓词将过滤我们要操作的项目：

```java
itemList.stream()
  .filter(isQualified)
  .forEach(item -> item.operate());
```

一旦我们完成了对流中项目的操作，我们就可以使用我们之前用于过滤的相同Predicate来删除它们：

```java
itemList.removeIf(isQualified);
```

在内部，removeIf 使用Iterator遍历列表并使用谓词匹配元素。我们现在可以从列表中删除任何匹配的元素。

### 4.2. 集合.removeAll

我们也可以使用另一个列表来保存已经操作过的元素，然后将它们从原来的列表中移除：

```java
List<Item> operatedList = new ArrayList<>();
itemList.stream()
  .filter(item -> item.isQualified())
  .forEach(item -> {
    item.operate();
    operatedList.add(item);
});
itemList.removeAll(operatedList);
```

与使用 Iterator 的removeIf不同，removeAll使用简单的 for 循环来删除列表中的所有匹配元素。

## 5.总结

在本文中，我们研究了一种在Java8 Streams 中对项目进行操作然后将其删除的方法。我们还看到了一种使用附加列表并删除所有匹配元素的方法。