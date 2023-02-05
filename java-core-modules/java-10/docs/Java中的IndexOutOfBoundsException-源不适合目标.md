## 1. 概述

在Java中，复制List有时会产生IndexOutOfBoundsException：“Source does not fit in dest”异常。在这个简短的教程中，我们介绍使用[Collections.copy](https://www.baeldung.com/java-copy-list-to-another#collectionscopy)方法时为什么会出现此错误以及如何解决该错误。我们还将研究一种Collections.copy的替代方法来复制集合。

## 2. 重现问题

让首先我们编写一个使用Collections.copy方法创建List副本的方法：

```java
static List<Integer> copyList(List<Integer> source) {
    List<Integer> destination = new ArrayList<>(source.size());
    Collections.copy(destination, source);
    return destination;
}
```

在这里，copyList方法创建了一个[初始容量](https://www.baeldung.com/java-arraylist#2-constructor-accepting-initial-capacity)等于源列表大小的新列表。然后它尝试将源列表的元素复制到目标列表：

```java
List<Integer> source = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> copy = copyList(source);
```

但是，一旦我们调用copyList方法，它就会抛出异常java.lang.IndexOutOfBoundsException: Source does not fit in dest。

## 3. 异常原因

让我们试着理解出了什么问题。根据Collections.copy方法的文档：

>   目标列表必须至少与源列表一样长。如果它更长，则目标集合中的其余元素不受影响。

在我们的例子中，我们使用构造函数创建了一个新集合，其初始容量等于源列表的大小。**它只是分配了足够的内存，并没有真正定义元素**，新列表的大小保持为0，因为容量和大小是列表的不同属性。

因此，当Collections.copy方法试图将源列表复制到目标列表时，它会抛出java.lang.IndexOutOfBoundsException。

## 4. 解决方案

### 4.1 Collections.copy

让我们看一个使用Collections.copy方法将List复制到另一个List的例子：

```java
List<Integer> destination = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> source = Arrays.asList(11, 22, 33);
Collections.copy(destination, source);
```

在这种情况下，我们将源列表的所有三个元素复制到目标列表，Arrays.asList方法使用元素初始化列表，而不仅仅是大小，因此，我们能够成功地将源列表复制到目标列表。

如果我们交换Collections.copy方法的参数声明位置，它将抛出java.lang.IndexOutOfBoundsException，因为源列表的大小小于目标列表的大小。

在经过复制操作之后，目标列表的元素如下：

```java
[11, 22, 33, 4, 5]
```

除了Collections.copy方法之外，Java中还有其他方法可以创建List的副本。让我们来看看其中的一些。

### 4.2 ArrayList构造函数

复制List的最简单方法是使用[采用Collection参数的构造函数](https://www.baeldung.com/java-arraylist#3-constructor-accepting-collection)：

```java
List<Integer> source = Arrays.asList(11, 22, 33);
List<Integer> destination = new ArrayList<>(source);
```

在这里，我们只需将源列表传递给目标列表的构造函数，这将创建源列表的浅副本。

**目标列表将只是对源列表引用的同一对象的另一个引用。因此，任何引用所做的每一次更改都会影响同一个对象**。

因此，使用构造函数是复制不可变对象(如整数和字符串)的一个很好的选择。

### 4.3 addAll

另一种简单的方法是使用List的[addAll方法](https://www.baeldung.com/java-arraylist#Adding)：

```java
List<Integer> destination = new ArrayList<>();
destination.addAll(source);
```

addAll方法会将源列表的所有元素复制到目标列表。

关于这种方法，有几点需要注意：

1.  它创建源列表的浅副本。
2.  源列表的元素附加到目标列表。

### 4.4 Java 8 Stream

Java 8引入了[Stream API](https://www.baeldung.com/java-8-streams)，它是处理Java集合的绝佳工具。

通过stream()方法，我们使用Stream API创建列表的副本：

```java
List<Integer> copy = source
    .stream()
    .collect(Collectors.toList());
```

### 4.5 Java 10

在Java 10中复制List更加简单，使用copyOf()方法允许我们创建一个包含给定Collection元素的不可变列表：

```java
List<Integer> destination = List.copyOf(sourceList);
```

如果我们想采用这种方法，我们需要确保输入List不为null并且不包含任何null元素。

## 5. 总结

在本文中，我们介绍了Collections.copy方法如何以及为什么抛出IndexOutOfBoundException “Source does not file in dest”。除此之外，我们还说明了将List复制到另一个List的不同方法。