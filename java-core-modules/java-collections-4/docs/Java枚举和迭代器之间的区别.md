## 1. 概述

在本教程中，我们将学习Java 中的[枚举](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Enumeration.html)和[迭代器。](https://www.baeldung.com/java-iterator)我们还将学习如何在代码中使用它们以及它们之间的区别。

## 2.枚举和迭代器介绍

在本节中，我们将从概念上了解Enumeration和Iterator及其使用。

### 2.1. 枚举

[枚举](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Enumeration.html)从 1.0 版开始就出现在Java中。它是一个接口，任何实现都允许一个一个地访问元素。简单来说，它用于迭代对象集合，例如[Vector](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Vector.html)和[Hashtable](https://www.baeldung.com/java-hash-table)。

让我们看一个Enumeration的例子：

```java
Vector<Person> people = new Vector<>(getPersons());
Enumeration<Person> enumeration = people.elements();
while (enumeration.hasMoreElements()) {
    System.out.println("First Name = " + enumeration.nextElement().getFirstName());
}
```

在这里，我们使用Enumeration打印Person的firstName。elements()方法提供了对Enumeration的引用，通过使用它我们可以一个一个地访问元素。

### 2.2. 迭代器

[迭代器](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Iterator.html)从 1.2开始就出现在 Java中，用于迭代在同一版本中也引入的[集合。](https://www.baeldung.com/java-collections)

接下来，让我们使用Iterator打印Person的名字。iterator()提供了对Iterator的引用，通过使用它我们可以一个一个地访问元素：

```java
List<Person> persons = getPersons();
Iterator<Person> iterator = persons.iterator();
while (iterator.hasNext()) {
    System.out.println("First Name = " + iterator.next().getFirstName());
}
```

因此，我们可以看到Enumeration和Iterator分别从 1.0 和 1.2 开始出现在Java中，用于一次一个地迭代对象集合。

## 3.枚举和迭代器的区别

在此表中，我们将了解Enumeration和Iterator之间的区别：

|                           枚举                           |                         迭代器                         |
| :----------------------------------------------------------: | :--------------------------------------------------------: |
|    自Java1.0 以来就存在，用于枚举Vector和Hashtable     | 自Java1.2 以来就存在，用于迭代List、Set、Map等集合 |
|      包含两个方法：hasMoreElements()和nextElement()      |       包含三个方法：hasNext()、next()和remove()        |
|                       方法有很长的名字                       |                      方法有简短的名字                      |
|                   没有方法在迭代时删除元素                   |           必须在迭代时使用remove() 来删除元素            |
|Java9 中添加的asIterator()在Enumeration之上提供了Iterator。但是，在这种特殊情况下， remove()会抛出UnsupportedOperationException |   Java 8 中添加的forEachRemaining()对剩余元素执行操作    |

## 

## 

## 

## 

## 

## 

## 

## 4。总结

在本文中，我们了解了Enumeration和Iterator，如何通过代码示例使用它们，以及它们之间的各种差异。