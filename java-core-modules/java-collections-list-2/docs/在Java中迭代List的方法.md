## 1. 概述

遍历列表的元素是程序中最常见的任务之一。

在本教程中，我们将回顾在Java中执行此操作的不同方法。我们将专注于按顺序遍历列表，尽管[反向](https://www.baeldung.com/java-list-iterate-backwards) 操作也很简单。

## 延伸阅读：

## [在Java中迭代一个集合](https://www.baeldung.com/java-iterate-set)

了解如何在 Java中迭代Set的元素。

[阅读更多](https://www.baeldung.com/java-iterate-set)→

## [在Java中遍历 Map](https://www.baeldung.com/java-iterate-map)

了解在Java中循环访问 Map 条目的不同方法。

[阅读更多](https://www.baeldung.com/java-iterate-map)→

## [如何使用索引迭代流](https://www.baeldung.com/java-stream-indices)

了解使用索引迭代Java8 Streams 的几种方法

[阅读更多](https://www.baeldung.com/java-stream-indices)→

## 2.for循环_

首先，让我们回顾一些[for循环](https://www.baeldung.com/java-loops)选项。

我们将从为示例定义国家列表开始：

```java
List<String> countries = Arrays.asList("Germany", "Panama", "Australia");
```

### 2.1. 循环基础_

最常见的迭代流程控制语句是基本的 for循环。

for循环定义了三种用分号分隔的语句。第一条语句是初始化语句。第二个定义终止条件。最后一条语句是更新子句。

这里我们简单地使用一个整数变量作为索引：

```java
for (int i = 0; i < countries.size(); i++) {
    System.out.println(countries.get(i));
}
```

在初始化时，我们必须声明一个整型变量来指定起点。此变量通常用作列表索引。

终止条件是一个在评估后返回布尔值的表达式。一旦这个表达式的计算结果为假，循环就结束了。

update 子句用于修改索引变量的当前状态，增加或减少它直到终止点。

### 2.2. 增强for循环

增强的 for循环是一个简单的结构，它允许我们访问列表的每个元素。它类似于基本的 for 循环，但更具可读性和紧凑性。因此，它是遍历列表最常用的形式之一。

请注意，增强的 for 循环比基本的 for循环更简单：

```java
for (String country : countries) {
    System.out.println(country); 
}
```

## 3.迭代器

[迭代器](https://www.baeldung.com/java-iterator)是一种设计模式，它为我们提供了一个标准接口来遍历数据结构，而不必担心内部表示。

这种遍历数据结构的方式提供了许多优点，其中我们可以强调我们的代码不依赖于实现。

因此，该结构可以是二叉树或双向链表，因为Iterator将我们从执行遍历的方式中抽象出来。这样，我们就可以轻松地替换代码中的数据结构，而不会出现令人不快的问题。

### 3.1. 迭代器

在Java中，Iterator模式反映在java.util.Iterator类中。它广泛用于JavaCollections。Iterator中有两个关键方法，hasNext()和next()方法。

在这里，我们将演示两者的使用：

```java
Iterator<String> countriesIterator = countries.iterator();

while(countriesIterator.hasNext()) {
    System.out.println(countriesIterator.next()); 
}
```

hasNext()方法检查列表中是否还有剩余元素。

next()方法返回迭代中的下一个元素。

### 3.2. 列表迭代器

ListIterator 允许我们以正向或反向顺序遍历元素列表。

使用ListIterator向前滚动列表遵循类似于Iterator使用的机制。这样，我们就可以用next()方法将迭代器向前移动，我们可以使用hasNext()方法找到列表的末尾。

如我们所见，ListIterator看起来与我们之前使用的Iterator非常相似：

```java
ListIterator<String> listIterator = countries.listIterator();

while(listIterator.hasNext()) {
    System.out.println(listIterator.next());
}
```

## 4.forEach ()

### 4.1. Iterable.forEach()

从Java8 开始，我们可以使用[forEach ()方法](https://www.baeldung.com/foreach-java)遍历列表的元素。该方法定义在[Iterable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Iterable.html)接口中，可以接受 Lambda 表达式作为参数。

语法非常简单：

```java
countries.forEach(System.out::println);
```

在forEach函数之前，Java 中的所有迭代器都是活动的，这意味着它们涉及一个遍历数据集合的 for 或 while 循环，直到满足特定条件。

通过在Iterable接口中引入forEach作为函数，所有实现Iterable的类都添加了forEach函数。

### 4.2. 流.forEach()

我们还可以将值集合转换为 Stream，并可以访问forEach()、 map()和filter() 等操作。

在这里，我们将演示流的典型用途：

```java
countries.stream().forEach((c) -> System.out.println(c));
```

## 5.总结

在本文中，我们演示了使用JavaAPI 迭代列表元素的不同方法。这些选项包括for循环、增强的 for 循环、Iterator、ListIterator和forEach()方法(包含在Java8 中)。

然后我们学习了如何将forEach()方法与Streams一起使用。