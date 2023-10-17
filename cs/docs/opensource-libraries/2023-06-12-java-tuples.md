---
layout: post
title:  Javatuples简介
category: libraries
copyright: libraries
excerpt: javatuples
---

## 1. 概述

元组是多个元素的集合，这些元素可能彼此相关，也可能不相关。换句话说，元组可以被认为是匿名对象。

例如，\["RAM", 16, "Astra"]是一个包含3个元素的元组。

在本文中，我们将快速浏览一个非常简单的库[javatuples](http://www.javatuples.org/index.html)，它允许我们使用基于元组的数据结构。

## 2. 内置Javatuples类

这个库为我们提供了十个不同的类，足以满足我们与元组相关的大部分要求：

-   Unit<A>
-   Pair<A,B>
-   Triplet<A,B,C>
-   Quartet<A,B,C,D>
-   Quintet<A,B,C,D,E>
-   Sextet<A,B,C,D,E,F>
-   Septet<A,B,C,D,E,F,G>
-   Octet<A,B,C,D,E,F,G,H>
-   Ennead<A,B,C,D,E,F,G,H,I>
-   Decade<A,B,C,D,E,F,G,H,I,J>

除了上面的类之外，还有两个额外的类，KeyValue<A,B\>和LabelValue<A,B\>，它们提供类似于Pair<A,B\>的功能，但在语义上有所不同。

**根据[官方网站](http://www.javatuples.org/index.html)，javatuples中的所有类都是类型安全且不可变的**。每个元组类都实现Iterable、Serializable和Comparable接口。

## 3. 添加Maven依赖

让我们将Maven依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.javatuples</groupId>
    <artifactId>javatuples</artifactId>
    <version>1.2</version>
</dependency>
```

请检查中央Maven仓库以获取[最新版本](https://search.maven.org/classic/#search|gav|1|g%3A"org.javatuples"ANDa%3A"javatuples")。

## 4. 创建元组

创建元组非常简单，我们可以使用相应的构造函数：

```java
Pair<String, Integer> pair = new Pair<String, Integer>("A pair", 55);
```

还有一种不那么冗长和语义优雅的创建元组的方法：

```java
Triplet<String, Integer, Double> triplet = Triplet.with("hello", 23, 1.2);
```

我们还可以从Iterable创建元组：

```java
List<String> listOfNames = Arrays.asList("john", "doe", "anne", "alex");
Quartet<String, String, String, String> quartet = Quartet.fromCollection(collectionOfNames);
```

请注意，**集合中的元素数应与我们要创建的元组的类型相匹配**。例如，我们不能使用上述集合创建一个Quintet，因为它恰好需要5个元素。对于阶数高于Quintet的任何其他元组类也是如此。

但是，我们可以通过在fromIterable()方法中指定起始索引，使用上述集合创建低阶元组，如Pair或Triplet：

```java
Pair<String, String> pairFromList = Pair.fromIterable(listOfNames, 2);
```

上面的代码将创建一个包含“anne”和“alex”的Pair。

也可以从任何数组方便地创建元组：

```java
String[] names = new String[] {"john", "doe", "anne"};
Triplet<String, String, String> triplet2 = Triplet.fromArray(names);
```

## 5. 从元组中获取值

javatuples中的每个类都有一个getValueX()方法，用于从元组中获取值，其中X指定元素在元组中的顺序。与数组中的索引一样，X的值从0开始。

让我们创建一个新的Quartet并获取一些值：

```java
Quartet<String, Double, Integer, String> quartet = Quartet.with("john", 72.5, 32, "1051 SW");

String name = quartet.getValue0();
Integer age = quartet.getValue2();
 
assertThat(name).isEqualTo("john");
assertThat(age).isEqualTo(32);
```

正如我们所见，“john”的位置为0，“72.5”为1，依此类推。

请注意，getValueX()方法是类型安全的。这意味着，不需要强制转换。

另一种方法是getValue(int pos)方法，它采用要获取的元素的从0开始的位置。**此方法不是类型安全的，需要显式强制转换**：

```java
Quartet<String, Double, Integer, String> quartet = Quartet.with("john", 72.5, 32, "1051 SW");

String name = (String) quartet.getValue(0);
Integer age = (Integer) quartet.getValue(2);
 
assertThat(name).isEqualTo("john");
assertThat(age).isEqualTo(32);
```

请注意类KeyValue和LabelValue有它们对应的方法getKey()/getValue()和getLabel()/getValue()。

## 6. 给元组赋值

与getValueX()类似，javatuple中的所有类都有setAtX()方法。同样，X是我们要设置的元素的从0开始的位置：

```java
Pair<String, Integer> john = Pair.with("john", 32);
Pair<String, Integer> alex = john.setAt0("alex");

assertThat(john.toString()).isNotEqualTo(alex.toString());
```

这里重要的是setAtX()方法的返回类型是元组类型本身，这是因为**javatuple是不可变的**，设置任何新值都会使原始实例保持不变。

## 7. 从元组中添加和删除元素

我们可以方便地向元组添加新元素。但是，这将导致创建一个更高阶的新元组：

```java
Pair<String, Integer> pair1 = Pair.with("john", 32);
Triplet<String, Integer, String> triplet1 = pair1.add("1051 SW");

assertThat(triplet1.contains("john"));
assertThat(triplet1.contains(32));
assertThat(triplet1.contains("1051 SW"));
```

从上面的示例中可以清楚地看出，将一个元素添加到Pair将创建一个新的Triplet。同样，将一个元素添加到Triplet将创建一个新的Quartet。

上面的示例还演示了javatuples中所有类提供的contains()方法的使用，这是验证元组是否包含给定值的非常方便的方法。

也可以使用add()方法将一个元组添加到另一个元组：

```java
Pair<String, Integer> pair1 = Pair.with("john", 32);
Pair<String, Integer> pair2 = Pair.with("alex", 45);
Quartet<String, Integer, String, Integer> quartet2 = pair1.add(pair2);

assertThat(quartet2.containsAll(pair1));
assertThat(quartet2.containsAll(pair2));
```

注意containsAll()方法的使用。如果pair1的所有元素都存在于quartet2中，它将返回true。

默认情况下，add()方法将元素添加为元组的最后一个元素。但是，可以使用addAtX()方法在给定位置添加元素，其中X是我们要添加元素的从0开始的位置：

```java
Pair<String, Integer> pair1 = Pair.with("john", 32);
Triplet<String, String, Integer> triplet2 = pair1.addAt1("1051 SW");

assertThat(triplet2.indexOf("john")).isEqualTo(0);
assertThat(triplet2.indexOf("1051 SW")).isEqualTo(1);
assertThat(triplet2.indexOf(32)).isEqualTo(2);
```

此示例在位置1处添加String，然后由indexOf()方法对其进行验证。请注意调用addAt1()方法后Pair<String, Integer\>和Triplet <String, String, Integer\>的类型顺序不同。

我们还可以使用任何add()或addAtX()方法添加多个元素：

```java
Pair<String, Integer> pair1 = Pair.with("john", 32);
Quartet<String, Integer, String, Integer> quartet1 = pair1.add("alex", 45);

assertThat(quartet1.containsAll("alex", "john", 32, 45));
```

为了从元组中删除元素，我们可以使用removeFromX()方法。同样，X指定要删除的元素从0开始的位置：

```java
Pair<String, Integer> pair1 = Pair.with("john", 32);
Unit<Integer> unit = pair1.removeFrom0();

assertThat(unit.contains(32));
```

## 8. 将元组转换为列表/数组

我们已经看到了如何将列表转换为元组，现在让我们看看如何将元组转换为列表：

```java
Quartet<String, Double, Integer, String> quartet = Quartet.with("john", 72.5, 32, "1051 SW");
List<Object> list = quartet.toList();

assertThat(list.size()).isEqualTo(4);
```

这很简单。这里唯一要注意的是，即使元组包含相同类型的元素，我们也将始终得到一个List<Object\>。

最后，让我们将元组转换为数组：

```java
Quartet<String, Double, Integer, String> quartet = Quartet.with("john", 72.5, 32, "1051 SW");
Object[] array = quartet.toArray();

assertThat(array.length).isEqualTo(4);
```

很明显，toArray()方法总是返回一个Object[]。

## 9. 总结

在本文中，我们探索了javatuples库并观察到它的简单性。它提供优雅的语义并且非常易于使用。

与往常一样，本教程的完整源代码可在[GitHub](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/opensource-libraries/libraries-1)上获得。