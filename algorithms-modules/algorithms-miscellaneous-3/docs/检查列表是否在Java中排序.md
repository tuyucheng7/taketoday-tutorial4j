## 1. 概述

在本教程中，我们将看到在Java中检查列表是否已排序的不同方法。

## 2.迭代方法

迭代方法是检查排序列表的一种简单而直观的方法。在这种方法中，我们将迭代列表并比较相邻元素。如果两个相邻元素中的任何一个未排序，我们可以说列表未排序。

列表可以按自然顺序或自定义顺序排序。我们将使用Comparable和Comparator接口涵盖这两种情况。

### 2.1. 使用可比

首先，让我们看一个列表的例子，它的元素是Comparable类型。在这里，我们将考虑一个包含String类型对象的列表：

```java
public static boolean isSorted(List<String> listOfStrings) {
    if (isEmpty(listOfStrings) || listOfStrings.size() == 1) {
        return true;
    }

    Iterator<String> iter = listOfStrings.iterator();
    String current, previous = iter.next();
    while (iter.hasNext()) {
        current = iter.next();
        if (previous.compareTo(current) > 0) {
            return false;
        }
        previous = current;
    }
    return true;
}
```

### 2.2. 使用比较器

现在，让我们考虑一个不实现Comparable的Employee类。因此，在这种情况下，我们需要使用Comparator来比较列表的相邻元素：

```java
public static boolean isSorted(List<Employee> employees, Comparator<Employee> employeeComparator) {
    if (isEmpty(employees) || employees.size() == 1) {
        return true;
    }

    Iterator<Employee> iter = employees.iterator();
    Employee current, previous = iter.next();
    while (iter.hasNext()) {
        current = iter.next();
        if (employeeComparator.compare(previous, current) > 0) {
            return false;
        }
        previous = current;
    }
    return true;
}
```

上面两个例子很相似。唯一的区别在于我们如何比较列表的前一个元素和当前元素。

此外，我们还可以使用Comparator来精确控制排序检查。有关这两者的更多信息，请参阅我们的[Comparator 和 Comparable in Java](https://www.baeldung.com/java-comparator-comparable)教程。

## 3.递归方法

现在，我们将看到如何使用递归检查排序列表：

```java
public static boolean isSorted(List<String> listOfStrings) {
    return isSorted(listOfStrings, listOfStrings.size());
}

public static boolean isSorted(List<String> listOfStrings, int index) {
    if (index < 2) {
        return true;
    } else if (listOfStrings.get(index - 2).compareTo(listOfStrings.get(index - 1)) > 0) {
        return false;
    } else {
        return isSorted(listOfStrings, index - 1);
    }
}
```

## 4.使用番石榴

使用第三方库而不是编写我们自己的逻辑通常很好。Guava 库有一些实用类，我们可以使用它们来检查列表是否已排序。

### 4.1. 番石榴订购类

在本节中，我们将了解如何使用 Guava 中的Ordering类来检查排序列表。

首先，我们将看到一个包含Comparable类型元素的列表示例：

```java
public static boolean isSorted(List<String> listOfStrings) {
    return Ordering.<String> natural().isOrdered(listOfStrings);
}
```

接下来，我们将看看如何检查Employee对象列表是否使用Comparator排序：

```java
public static boolean isSorted(List<Employee> employees, Comparator<Employee> employeeComparator) {
    return Ordering.from(employeeComparator).isOrdered(employees);
}
```

此外，我们可以使用natural().reverseOrder()来检查列表是否按倒序排序。此外，我们可以使用natural().nullFirst()和natural()。nullLast()检查null是否出现在排序列表的第一个或最后一个。

要了解有关 Guava Ordering类的更多信息，我们可以参考我们[的 Guava Ordering 指南一](https://www.baeldung.com/guava-ordering)文。

### 4.2. Guava比较器类

如果我们使用Java8 或更高版本，Guava 在Comparators类方面提供了更好的选择。我们将看到一个使用此类的isInOrder方法的示例：

```java
public static boolean isSorted(List<String> listOfStrings) {
    return Comparators.isInOrder(listOfStrings, Comparator.<String> naturalOrder());
}
```

如我们所见，在上面的示例中，我们使用了自然排序来检查排序列表。我们还可以使用Comparator来自定义排序检查。

## 5.总结

在本文中，我们了解了如何使用简单的迭代方法、递归方法和使用 Guava 检查排序列表。我们还简要介绍了Comparator和Comparable在确定排序检查逻辑时的用法。