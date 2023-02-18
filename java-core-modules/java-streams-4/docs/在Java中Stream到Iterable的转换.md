## 一、简介

Java [Streams](https://www.baeldung.com/java-8-streams-introduction) API 是在 Java 8 中引入的，它提供了处理元素序列的功能。*Streams API 支持对管道中的对象集合*进行链接操作，以产生所需的结果。

在本教程中，我们将研究将*Stream*用作Iterable*的*方法。

## *2.可迭代*和*迭代器*

*[Iterable](https://www.baeldung.com/java-iterator-vs-iterable#iterable-interface)* 是自 Java 1.5 以来可用的接口。实现此接口的类允许类的对象成为*[for](https://www.baeldung.com/java-for-loop#foreach)* -each 循环语句的目标。实现类不存储有关其迭代状态的任何信息，并且应该生成一个有效的*迭代器*。

**Collection 接口扩展了\*Iterable接口，并且\**Collection\*接口的所有具体实现（例如[\*ArrayList\*](https://www.baeldung.com/java-iterate-list)或[\*HashSet\*](https://www.baeldung.com/java-iterate-set) \*）都通过实现\**Iterable\*的\*iterator()\* 方法来生成迭代器。** 

Iterator [**](https://www.baeldung.com/java-iterator)接口也是 Java Collections 框架的一部分，从 Java 1.2 开始可用。*实现Iterator<T>* 的类必须提供遍历集合的实现，例如移动到下一个元素、检查是否还有更多元素或从集合中删除当前元素的能力：

```java
public interface Iterator<E> {
    boolean hasNext();
    E next();
    void remove();
}复制
```

## 3.问题陈述

现在我们已经了解了*Iterator*和*Iterable*接口的基础知识以及它们所扮演的角色，让我们来理解问题陈述。

实现*Collection*接口的类本质上实现了*Iterable<T>*接口。另一方面，流略有不同。值得注意的是，***Stream<T>\*****扩展的接口*****BaseStream<T>\* 有一个方法\*iterator()\* 但没有实现\*Iterable\*接口。**

这个限制带来了无法在*Stream上使用增强的**for* -each 循环的挑战。

我们将在接下来的部分中探讨一些解决此问题的方法，并最终探讨为什么*Stream*与*Collection*不同，它不扩展*Iterable*接口。

## 4.在 *Stream*上使用*iterator()将**Stream*转换为*Iterable*

Stream接口的 *iterator()*方法返回流元素的迭代器*。*这是一个终端流操作：

```java
Iterator<T> iterator();复制
```

但是，我们仍然无法在增强的*for* -each 循环中使用生成的迭代器：

```java
private void streamIterator(List<String> listOfStrings) {
    Stream<String> stringStream = listOfStrings.stream();
    // this does not compile
    for (String eachString : stringStream.iterator()) {
        doSomethingOnString(eachString);
    }
}复制
```

正如我们之前看到的，“for-each 循环”适用于*Iterable* 而不是*Iterator 。*为了解决这个问题，我们将迭代器转换为一个*Iterable*实例，然后应用我们想要的*for* -each 循环。 ***Iterable<T>\*** **是一个函数式接口这一事实 允许我们使用 lambda 编写代码**：

```java
for (String eachString : (Iterable<String>) () -> stringStream.iterator()) {
    doSomethingOnString(eachString);
}复制
```

**[我们可以使用方法引用](https://www.baeldung.com/java-method-references)方法进行更多重构：**

```java
for (String eachString : (Iterable<String>) stringStream::iterator) {
    doSomethingOnString(eachString.toLowerCase());
}复制
```

*在for* -each 循环中使用 Iterable 之前，也可以使用一个临时变量*iterableStream*来保存*Iterable*：

```java
Iterable<String> iterableStream = () -> stringStream.iterator();
for (String eachString : iterableStream) {
    doSomethingOnString(eachString, sentence);
}复制
```

## 5.通过转换为集合*在*for *-* each 循环中使用*Stream*

我们在上面讨论了*Collection*接口如何扩展*Iterable*接口。因此，我们可以将给定的*Stream*转换为集合并将结果用作*Iterable：*

```java
for(String eachString : stringStream.collect(Collectors.toList())) {
    doSomethingOnString(eachString);
}复制
```

## 6. 为什么*Stream*没有实现*Iterable*

*我们看到了如何*将*Stream*用作Iterable 。*List*和 Set*等*集合是在其中存储数据的数据结构，旨在在其生命周期内多次使用。这些对象被传递给不同的方法，经历多次变化，最重要的是，被多次迭代。

**另一方面，流是一次性数据结构，因此并非设计为使用\*for\* -each 循环进行迭代。**根本不希望流被一遍又一遍地迭代，并在流已经关闭和操作时抛出*IllegalStateException 。*因此，尽管*Stream*提供了一个*iterator()*方法，但它并没有扩展*Iterable*。

## 七、结论

在本文中，我们研究了将*Stream*用作*Iterable*的不同方式。

*我们简要讨论了Iterable*和*Iterator*之间的差异，以及*Stream<T>*不实现*Iterable<T>*接口的原因。