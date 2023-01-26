## 1. 概述

在本文中，我们将介绍在Java中检查列表是否已排序的不同方法。

## 2. 迭代法

迭代法是检查列表是否排序的一种简单直观的方法。在这种方法中，我们将迭代列表并比较相邻的元素。如果任何一对两个相邻元素中没有排序，我们可以认为列表没有排序。

列表可以按自然顺序或自定义顺序排序。我们可以使用Comparable和Comparator接口讨论这两种情况。

### 2.1 使用Comparable

首先，让我们看一个元素为Comparable类型的列表示例。在这里，我们将考虑一个包含String类型对象的集合：

```
public static boolean checkIfSortedUsingIterativeApproach(List<String> listOfStrings) {
  if (isEmpty(listOfStrings) || listOfStrings.size() == 1) {
    return true;
  }
  Iterator<String> iter = listOfStrings.iterator();
  String current, previous = iter.next();
  while (iter.hasNext()) {
    current = iter.next();
    if (current.compareTo(previous) < 0) {
      return false;
    }
    previous = current;
  }
  return true;
}
```

### 2.2 使用Comparator

现在，让我们考虑一个Employee类，它没有实现Comparable。因此，在这种情况下，我们需要使用Comparator来比较列表中的相邻元素：

```
public static boolean checkIfSortedUsingIterativeApproach(List<Employee> employees, Comparator<Employee> employeeComparator) {
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

上述两个例子是相似的。唯一的区别在于我们如何比较列表中上一个的元素和当前的元素。

此外，我们还可以使用Comparator对排序检查进行精确控制。

## 3. 递归方法

现在，我们将了解如何使用递归检查列表是否排序：

```
public static boolean checkIfSortedUsingRecursion(List<String> listOfStrings) {
  return isSortedRecursive(listOfStrings, listOfStrings.size());
}

private static boolean isSortedRecursive(List<String> listOfStrings, int index) {
  if (index < 2) {
    return true;
  } else if (listOfStrings.get(index - 2).compareTo(listOfStrings.get(index - 1)) > 0) {
    return false;
  } else {
    return isSortedRecursive(listOfStrings, index - 1);
  }
}
```

## 4. 使用Guava

使用第三方库而不是编写我们自己的逻辑通常会很好。Guava库有一些实用程序类，我们可以使用它们来检查列表是否已排序。

### 4.1 Guava Ordering类

在本节中，我们将了解如何使用Guava中的Ordering类检查列表是否排序。

首先，我们将看到一个包含Comparable类型元素的集合示例：

```
public static boolean checkIfSortedUsingOrderingClass(List<String> listOfStrings) {
  return Ordering.<String>natural().isOrdered(listOfStrings);
}
```

接下来，我们将了解如何使用Comparator检查Employee对象集合是否已排序：

```
public static boolean checkIfSortedUsingOrderingClass(List<Employee> employees, Comparator<Employee> employeeComparator) {
  return Ordering.from(employeeComparator).isOrdered(employees);
}
```

此外，我们可以使用natural().reverseOrder()来检查列表是否以相反的顺序排序。
此外，我们可以使用natural().nullFirst()和natural().nullLast()来检查null是否出现在排序列表的第一个或最后一个。

### 4.2 Guava Comparators类

如果我们使用的是Java 8或更高版本，Guava在Comparators类方面提供了更好的选择。我们将看到使用此类的isInoder方法()的示例：

```
public static boolean checkIfSortedUsingComparators(List<String> listOfStrings) {
  return Comparators.isInOrder(listOfStrings, Comparator.<String>naturalOrder());
}
```

正如我们所见，在上面的例子中，我们使用了自然排序来检查列表是否排序。我们还可以使用Comparator自定义排序检查。