## 1. 概述

[List](https://www.baeldung.com/tag/java-list/)是我们在使用Java时常用的集合类型。

正如我们所知，我们可以轻松地[在一行中](https://www.baeldung.com/java-init-list-one-line)[初始化一个List](https://www.baeldung.com/java-init-list-one-line)。例如，当我们想要初始化一个只有一个元素的List时，我们可以使用[Arrays.asList()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Arrays.html#asList(T...))方法或[Collections.singletonList()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collections.html#singletonList(T))方法。

在本教程中，我们将讨论这两种方法之间的区别。然后，为简单起见，我们将使用单元测试断言来验证某些操作是否按预期运行。

## 2. Arrays.asList()方法

首先，Arrays.asList ()方法返回一个固定大小的列表。

任何结构更改都将抛出UnsupportedOperationException，例如，向列表添加新元素或从列表中删除元素。现在，让我们通过测试来检查一下：

```java
List<String> arraysAsList = Arrays.asList("ONE");
assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(
    () -> arraysAsList.add("TWO")
);

```

如果我们试一试，测试就会通过。在上面的代码中，我们使用[了 Assertj 的异常断言](https://www.baeldung.com/assertj-exception-assertion)来验证当我们尝试向列表添加新元素时是否抛出了UnsupportedOperationException 。

即使我们不能在列表上调用add()或remove()操作，我们也可以使用set()方法更改列表中的元素 ：

```java
arraysAsList.set(0, "A brand new string");
assertThat(arraysAsList.get(0)).isEqualTo("A brand new string");
```

这一次，我们用一个新的String对象设置列表中的元素。如果我们执行测试，它就会通过。

最后，我们来讨论一下Arrays.asList()方法的数组和返回列表的关系。

正如方法名称所暗示的那样，此方法使数组作为 List工作。让我们了解“让数组像列表一样工作”是什么意思。

Arrays.asList ()方法返回一个List对象，它由给定的 array 支持。也就是说，该方法不会将数组中的元素到新的List对象中。相反，该方法提供给定数组的列表视图。因此，我们对数组所做的任何更改都将在返回的列表中可见。同样，对列表所做的更改也将在数组中可见：

```java
String[] theArray = new String[] { "ONE", "TWO" };
List<String> theList = Arrays.asList(theArray);
//changing the list, the array is changed too
theList.set(0, "ONE [changed in list]");
assertThat(theArray[0]).isEqualTo("ONE [changed in list]");

//changing the array, the list is changed too
theArray[1] = "TWO [changed in array]";
assertThat(theList.get(1)).isEqualTo("TWO [changed in array]");

```

测试通过。所以对于数组和返回的列表，如果我们在一侧做了一些改变，另一侧也会改变。

## 3. Collections.singletonList()方法

首先， singletonList()方法返回的列表只有一个元素。与Arrays.asList()方法不同，singletonList()返回一个不可变列表。

换句话说，不允许对singletonList() 返回的列表进行结构和非结构更改。一个测试可以快速说明这一点：

```java
List<String> singletonList = Collections.singletonList("ONE");
assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(
    () -> singletonList.add("TWO")
);
assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(
    () -> singletonList.set(0, "A brand new string")
);

```

如果我们运行测试，它就会通过。因此，无论我们是向列表添加元素还是更改列表中的元素，它都会抛出UnsupportedOperationException。

值得一提的是，如果我们看一下返回列表类的源代码，就会发现与其他List实现不同，返回列表中的单个元素不会存储在数组或任何其他复杂数据结构中。相反，列表直接包含元素对象：

```java
private static class SingletonList<E> extends AbstractList<E> implements RandomAccess, Serializable {
    ...
    private final E element;

    SingletonList(E obj) {element = obj;}
    ...
}
```

因此，它会占用更少的内存。

## 4. 简短总结

最后，我们将Arrays.asList()方法和Collections.singletonList()方法的特点总结在一个表格中，以便更好地了解：

|                  | 数组.asList() | 集合.singletonList() |
| :--------------: | :-------------: | :--------------------: |
|   结构变化   |     不允许      |         不允许         |
| 非结构性变化 |      允许       |         不允许         |
|   数据结构   |   由数组支持    |      直接握住元素      |

## 5.总结

在这篇快速文章中，我们讨论了Arrays.asList()方法和Collections.singletonList()方法。

当我们想要初始化一个只有一个元素的固定大小的列表时，我们可以考虑使用Collections.singletonList()方法。但是，如果需要更改返回列表中的元素，我们可以选择Arrays.asList()方法。