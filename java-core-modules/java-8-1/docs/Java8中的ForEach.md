## 1. 概述

在Java 8中引入的forEach循环为程序员提供了一种新的、简洁的方式来迭代集合。

在本教程中，我们将了解如何将forEach与集合一起使用，它需要什么样的参数，以及此循环与增强for循环有何不同。

## 2. forEach的基础知识

在Java中，Collection接口以Iterable作为其父接口。这个接口在Java 8中添加了一个新的API：

```java
void forEach(Consumer<? super T> action)
```

简单地说，forEach的Java文档声明它”对Iterable的每个元素执行给定的操作，直到所有元素都被处理或该操作引发异常“。

因此，使用forEach，我们可以迭代集合并对每个元素执行给定的操作，就像任何其他Iterator一样。

例如，考虑以下迭代和打印字符串集合的for循环版本：

```java
for (String name : names) {
    System.out.println(name);
}
```

我们可以使用forEach来编写：

```java
names.forEach(name -> {
    System.out.println(name);
});
```

## 3. 使用forEach方法

我们使用forEach来迭代集合并对每个元素执行特定操作。**要执行的操作包含在实现Consumer接口的类中，并作为参数传递给forEach方法**。

Consumer接口是一个[函数式接口](https://www.baeldung.com/java-8-functional-interfaces)(具有单个抽象方法的接口)，它接收输入并且不返回结果。

以下是它的定义：

```java
@FunctionalInterface
public interface Consumer {
    void accept(T t);
}
```

因此，任何Consumer接口的实现，例如简单地打印String的Consumer：

```java
Consumer<String> printConsumer = new Consumer<String>() {
    public void accept(String name) {
        System.out.println(name);
    };
};
```

都可以作为参数传递给forEach：

```java
names.forEach(printConsumer);
```

但这并不是通过Consumer创建操作并使用forEach API的唯一方法。

让我们看看使用forEach方法的三种最常用的方式。

### 3.1 匿名Consumer实现

我们可以使用匿名类实例化Consumer接口的实现，然后将其作为参数应用于forEach方法：

```java
Consumer<String> printConsumer= new Consumer<String>() {
    public void accept(String name) {
        System.out.println(name);
    }
};
names.forEach(printConsumer);
```

这很好用。但是如果我们分析这个例子，我们会发现有用的部分实际上是accept()方法中的代码。

尽管Lambda表达式现在已成为规范并且是一种更简单的方法，但仍然值得了解如何实现Consumer接口。

### 3.2 Lambda表达式

Java 8函数式接口的主要好处是我们可以使用Lambda表达式来实例化它们并避免使用笨重的匿名类实现。

由于Consumer是一个函数式接口，我们可以用Lambda来表达它：

```java
(argument) -> { //body }
```

因此，我们的printConsumer可以简化成以下方式：

```java
name -> System.out.println(name)
```

我们可以将其传递给forEach：

```java
names.forEach(name -> System.out.println(name));
```

自从在Java 8中引入Lambda表达式以来，这可能是使用forEach方法的最常见方式。

### 3.3 方法引用

我们也可以使用方法引用语法而不是普通的Lambda语法，其中我们引用的是一个已经存在的方法来对类执行操作：

```java
names.forEach(System.out::println);
```

## 4. 使用forEach

### 4.1 迭代集合

**任何实现Collection类型的集合(list、set、queue等)都具有使用forEach的相同语法**。

因此，正如我们所看到的，我们可以这样迭代List集合的元素：

```java
List<String> names = Arrays.asList("Larry", "Steve", "James");

names.forEach(System.out::println);
```

Set集合类似：

```java
Set<String> uniqueNames = new HashSet<>(Arrays.asList("Larry", "Steve", "James"));

uniqueNames.forEach(System.out::println);
```

最后，同样实现Collection的Queue也是如此：

```java
Queue<String> namesQueue = new ArrayDeque<>(Arrays.asList("Larry", "Steve", "James"));

namesQueue.forEach(System.out::println);
```

### 4.2 使用forEach迭代Map

Map并没有实现Iterable，但它确实提供了自己的forEach变体，**Map中的forEach方法采用BiConsumer作为参数**。

Java 8在Iterable的forEach中引入了BiConsumer而不是Consumer，以便可以同时对Map的key和value执行操作。

让我们创建一个Map：

```java
Map<Integer, String> namesMap = new HashMap<>();
namesMap.put(1, "Larry");
namesMap.put(2, "Steve");
namesMap.put(3, "James");
```

接下来，让我们使用Map的forEach遍历namesMap：

```java
namesMap.forEach((key, value) -> System.out.println(key + " " + value));
```

正如我们在这里看到的，我们使用BiConsumer来遍历Map的条目：

```java
(key, value) -> System.out.println(key + " " + value)
```

### 4.3 通过迭代entrySet来迭代Map

我们还可以使用Iterable的forEach来迭代Map的EntrySet。

**由于Map的键值对存储在名为EntrySet的Set集合中，因此我们可以使用forEach对其进行迭代**：

```java
namesMap.entrySet().forEach(entry -> System.out.println(
      entry.getKey() + " " + entry.getValue()));
```

## 5. Foreach与For循环

从简单的角度来看，这两个循环提供相同的功能：遍历集合中的元素。

**它们之间的主要区别在于它们是不同的迭代器。增强for循环是一个外部迭代器，而新的forEach方法是内部的**。

### 5.1 内部迭代器-forEach

这种类型的迭代器在后台管理迭代，让程序员只需编写集合元素的代码即可。

相反，迭代器管理迭代并确保逐个处理元素。

让我们看一个内部迭代器的例子：

```java
names.forEach(name -> System.out.println(name));
```

在上面的forEach方法中，我们可以看到提供的参数是一个lambda表达式。这意味着该方法只需要知道**要做什么**，所有的迭代工作都将在提供的lambda内部处理。

### 5.2 外部迭代器-for循环

外部迭代器混合了循环**应该做什么，并且应该怎么做**。

Enumerations、Iterators和增强for循环都是外部迭代器(参考iterator()、next()或hasNext()方法)。在所有这些迭代器中，我们的任务是指定如何执行迭代。

考虑这个熟悉的循环：

```java
for (String name : names) {
    System.out.println(name);
}
```

虽然我们在迭代集合时没有显式调用hasNext()或next()方法，但使迭代工作的底层代码使用这些方法。这意味着这些操作的复杂性对程序员来说是隐藏的，但它仍然存在。

与内部迭代器相反，在该迭代器中，集合本身进行迭代，这里我们需要外部代码来从集合中取出每个元素。

## 6. 总结

在本文中，我们演示了forEach方法是如何工作的，以及可以接收什么样的实现作为参数，以便对集合中的每个元素执行操作。