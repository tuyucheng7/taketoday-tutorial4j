## 1. 概述

在Java中， List 有时 会产生IndexOutOfBoundsException：“Source does not fit in dest”。在这个简短的教程中，我们将研究为什么在使用[Collections.copy](https://www.baeldung.com/java-copy-list-to-another#collectionscopy) 方法时会出现此错误，以及如何解决它。我们还将查看 Collections.copy 的替代方法来制作列表的副本。

## 2.重现问题

让我们从使用Collections.copy方法创建List 副本的方法开始 ：

```java
static List<Integer> copyList(List<Integer> source) {
    List<Integer> destination = new ArrayList<>(source.size());
    Collections.copy(destination, source);
    return destination;
}
```

此处，copyList方法创建了一个新列表[，其初始容量](https://www.baeldung.com/java-arraylist#2-constructor-accepting-initial-capacity)等于源列表的大小。然后它尝试将源列表的元素到目标列表：

```java
List<Integer> source = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> copy = copyList(source);
```

但是，一旦我们调用copyList方法，它就会抛出异常java.lang.IndexOutOfBoundsException: Source does not fit in dest。

## 三、异常原因

让我们试着了解出了什么问题。根据Collections.copy方法的文档：

>   目标列表必须至少与源列表一样长。如果它更长，则目标列表中的其余元素不受影响。

在我们的示例中，我们使用初始容量等于源列表大小的构造函数创建了一个新列表。它只是分配足够的内存，实际上并不定义元素。新列表的大小保持为零，因为容量和大小是List的不同属性。

因此，当Collections.copy 方法尝试将源列表到目标列表时，它会抛出java.lang.IndexOutOfBoundsException。

## 4.解决方案

### 4.1. 收藏.copy

让我们看一个使用Collections.copy 方法将一个List到另一个List的工作示例：

```java
List<Integer> destination = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> source = Arrays.asList(11, 22, 33);
Collections.copy(destination, source);
```

在这种情况下，我们将源列表的所有三个元素到目标列表。Arrays.asList方法使用元素初始化列表，而不仅仅是大小，因此，我们能够成功地将源列表到目标列表。

如果我们只是交换Collections.copy 方法的参数，它会抛出java.lang.IndexOutOfBoundsException 因为源列表的大小小于目标列表的大小。

执行此操作后，目标列表如下所示：

```java
[11, 22, 33, 4, 5]
```

除了Collections.copy方法，Java 中还有其他方法可以List。让我们来看看其中的一些。

### 4.2. ArrayList构造器

List 的 最简单方法是使用[带有Collection参数](https://www.baeldung.com/java-arraylist#3-constructor-accepting-collection)的构造函数：

```java
List<Integer> source = Arrays.asList(11, 22, 33);
List<Integer> destination = new ArrayList<>(source);
```

在这里，我们只是将源列表传递给目标列表的构造函数，这会创建源列表的浅表副本。

目标列表只是对源列表引用的同一对象的另一个引用。因此，任何引用所做的每次更改都会影响同一个对象。

因此，使用构造函数是不可变对象(如整数和字符串)的不错选择。

### 4.3. 全部添加

另一种简单的方法是使用[List](https://www.baeldung.com/java-arraylist#Adding)[的](https://www.baeldung.com/java-arraylist#Adding)[addAll ](https://www.baeldung.com/java-arraylist#Adding)方法：

```java
List<Integer> destination = new ArrayList<>();
destination.addAll(source);
```

addAll方法会将源列表的所有元素到目标列表。

关于这种方法有几点需要注意：

1.  它创建源列表的浅表副本。
2.  源列表的元素附加到目标列表。

### 4.4.Java8流

Java 8 引入了[Stream API](https://www.baeldung.com/java-8-streams)，它是处理 Java集合的绝佳工具。

使用stream()方法，我们使用 Stream API 列表：

```java
List<Integer> copy = source.stream()
  .collect(Collectors.toList());
```

### 4.5.Java10

在Java10 中List甚至更简单。使用copyOf()方法允许我们创建一个包含给定Collection元素的不可变列表：

```java
List<Integer> destination = List.copyOf(sourceList);
```

如果我们想要采用这种方法，我们需要确保输入列表 不为空并且它不包含任何空元素。

## 5.总结

在本文中，我们研究了Collections.copy方法如何以及为何抛出IndexOutOfBoundException “Source does not file in dest”。与此同时，我们还探索了将一个列表 到另一个 列表的不同方法。