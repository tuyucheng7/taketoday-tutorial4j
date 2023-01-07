## 1. 概述

在Java中，有时为了方便，我们需要创建一个小列表或将数组转换为列表。Java 为此提供了一些辅助方法。

在本教程中，我们将比较初始化小型临时数组的两种主要方法：List.of()和 Array.asList()。

## 2. 使用Arrays.asList()

[Java 1.2 中引入的Arrays.asList()](https://www.baeldung.com/java-arraylist)简化了List对象的创建，它是Java Collections Framework的一部分。它可以将数组作为输入并创建所提供数组的List对象：

```java
Integer[] array = new Integer[]{1, 2, 3, 4};
List<Integer> list = Arrays.asList(array);
assertThat(list).containsExactly(1,2,3,4);
```

正如我们所见，创建一个简单的整数列表非常容易。

### 2.1. 返回列表中不受支持的操作

asList()方法返回一个固定大小的列表。因此，添加和删除新元素会引发UnsupportedOperationException：

```java
List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
assertThrows(UnsupportedOperationException.class, () -> list.add(6));

List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
assertThrows(UnsupportedOperationException.class, () -> list.remove(1));
```

### 2.2. 使用数组

我们应该注意到该列表不会创建输入数组的副本。相反，它使用List接口包装原始数组。因此，对数组的更改也会反映在列表中：

```java
Integer[] array = new Integer[]{1,2,3};
List<Integer> list = Arrays.asList(array);
array[0] = 1000;
assertThat(list.get(0)).isEqualTo(1000);
```

### 2.3. 更改返回列表

此外，Arrays.asList()返回的列表是可变的。也就是说，我们可以更改列表的各个元素：

```java
List<Integer> list = Arrays.asList(1, 2, 3, 4);
list.set(1, 1000);
assertThat(list.get(1)).isEqualTo(1000);
```

最终，这会导致不良的副作用，导致难以发现的错误。当提供数组作为输入时，列表中的更改也将反映在数组中：

```apache
Integer[] array = new Integer[]{1, 2, 3};
List<Integer> list = Arrays.asList(array);
list.set(0,1000);
assertThat(array[0]).isEqualTo(1000);
```

让我们看看另一种创建列表的方法。

## 3. 使用List.of()

与数组相反。asList()，Java 9 引入了一个更方便的方法[List.of()](https://www.baeldung.com/java-init-list-one-line#factory-methods-java-9)。这将创建不可修改的List对象的实例：

```java
String[] array = new String[]{"one", "two", "three"};
List<String> list = List.of(array);
assertThat(list).containsExactly("two", "two", "three");
```

### 3.1. 与Arrays.asList() 的区别

与Arrays.asList()的主要区别在于List.of()返回一个不可变 列表，该列表是提供的输入 数组的副本。因此，对原始数组的更改不会反映在返回的列表中：

```java
String[] array = new String[]{"one", "two", "three"};
List<String> list = List.of(array);
array[0] = "thousand";
assertThat(list.get(0)).isEqualTo("one");
```

此外，我们无法修改列表的元素。如果我们尝试这样做，它会抛出UnsupportedOperationException：

```java
List<String> list = List.of("one", "two", "three");
assertThrows(UnsupportedOperationException.class, () -> list.set(1, "four"));
```

### 3.2. 空值

我们还应该注意List.of()不允许将空值作为输入，并且会抛出NullPointerException：

```java
assertThrows(NullPointerException.class, () -> List.of("one", null, "two"));
```

## 4。总结

这篇简短的文章探讨了使用List.of()和Arrays.asList()在Java中创建列表。