## 1. 概述

在这个简短的教程中，我们将了解Arrays.asList(array)和ArrayList(Arrays.asList(array)) 之间的区别。

## 2. Arrays.asList

让我们从[Arrays.asList](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Arrays.html#asList(T...))方法开始。

使用此方法，我们可以将数组转换为固定大小的List对象。这个List 只是一个包装器，它使数组可以作为列表使用。没有数据被或创建。

另外，我们不能修改它的长度，因为不允许添加或删除元素。

但是，我们可以修改数组中的单个项目。请注意，我们对List的单个项目所做的所有修改都将反映在我们的原始数组中：

```java
String[] stringArray = new String[] { "A", "B", "C", "D" };
List stringList = Arrays.asList(stringArray);

```

现在，让我们看看如果我们修改stringList的第一个元素会发生什么：

```java
stringList.set(0, "E");
 
assertThat(stringList).containsExactly("E", "B", "C", "D");
assertThat(stringArray).containsExactly("E", "B", "C", "D");
```

正如我们所看到的，我们的原始数组也被修改了。列表和数组现在都以相同的顺序包含完全相同的元素。

现在让我们尝试向stringList中插入一个新元素：

```java
stringList.add("F");
java.lang.UnsupportedOperationException
	at java.base/java.util.AbstractList.add(AbstractList.java:153)
	at java.base/java.util.AbstractList.add(AbstractList.java:111)
```

如我们所见，向List添加/删除元素将抛出java.lang.UnsupportedOperationException。

## 3. ArrayList(数组.asList(数组))

与Arrays.asList方法类似，当我们需要从数组创建List时，我们可以使用ArrayList<>(Arrays.asList(array))。

但是，与我们之前的示例不同，这是数组的独立副本，这意味着修改新列表不会影响原始数组。此外，我们拥有常规ArrayList 的所有功能，例如添加和删除元素：

```java
String[] stringArray = new String[] { "A", "B", "C", "D" }; 
List stringList = new ArrayList<>(Arrays.asList(stringArray));

```

现在让我们修改stringList的第一个元素：

```java
stringList.set(0, "E");
 
assertThat(stringList).containsExactly("E", "B", "C", "D");
```

现在，让我们看看我们的原始数组发生了什么：

```java
assertThat(stringArray).containsExactly("A", "B", "C", "D");
```

如我们所见，我们的原始数组保持不变。

在结束之前，如果我们看一下 [JDK 源代码](http://hg.openjdk.java.net/jdk8/jdk8/jdk/file/687fd7c7986d/src/share/classes/java/util/Arrays.java#l3791)，我们可以看到Arrays.asList方法返回一种不同于[java.util.ArrayList的](http://hg.openjdk.java.net/jdk8/jdk8/jdk/file/687fd7c7986d/src/share/classes/java/util/ArrayList.java#l106)[ArrayList](http://hg.openjdk.java.net/jdk8/jdk8/jdk/file/687fd7c7986d/src/share/classes/java/util/Arrays.java#l3800)类型。主要区别在于返回的ArrayList仅包装现有数组 — 它不实现添加和删除方法。

## 4。总结

在这篇简短的文章中，我们了解了将数组转换为ArrayList的两种方法之间的区别。我们看到了这两个选项的行为方式以及它们实现内部数组的方式之间的区别。