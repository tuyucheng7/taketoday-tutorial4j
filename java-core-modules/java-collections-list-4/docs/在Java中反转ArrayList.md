## 1. 概述

[ArrayList](https://www.baeldung.com/java-arraylist)是Java中常用的List实现。

在本教程中，我们将探索如何反转ArrayList。

## 二、问题简介

像往常一样，让我们通过一个例子来理解这个问题。假设我们有一个整数列表：

```java
List<Integer> aList = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
```

反转之后，我们期待得到这样的结果：

```java
List<Integer> EXPECTED = new ArrayList<>(Arrays.asList(7, 6, 5, 4, 3, 2, 1));
```

所以，这个要求看起来很简单。但是，该问题可能有几个变体：

-   反转列表_
-   反转List并将结果作为新的List对象返回

我们将在本教程中介绍这两种情况。

Java 标准库提供了一个辅助方法来完成这项工作。我们将看到如何使用此方法快速解决问题。

此外，考虑到我们中的一些人可能正在学习 Java，我们将解决两个有趣但有效的反转List的实现。

接下来，让我们看看它们的实际效果。

## 3. 使用标准的Collections.reverse方法

Java 标准库提供了[Collections.reverse](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Collections.html#reverse(java.util.List))方法来反转给定List中元素的顺序。

这种方便的方法进行就地反转，这将反转它收到的原始列表中的顺序。但是，首先，让我们创建一个单元测试方法来理解它：

```java
List<Integer> aList = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
Collections.reverse(aList);
assertThat(aList).isEqualTo(EXPECTED);

```

当我们执行上面的测试时，它通过了。如我们所见，我们已将aList对象传递给reverse方法，然后aList对象中元素的顺序被反转。

如果我们不想更改原始List，并希望获得一个新的List对象以包含颠倒顺序的元素，我们可以将一个新的List 对象传递给 reverse方法：

```java
List<Integer> originalList = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
List<Integer> aNewList = new ArrayList<>(originalList);
Collections.reverse(aNewList);

assertThat(aNewList).isNotEqualTo(originalList).isEqualTo(EXPECTED);

```

这样我们就保持了originalList不变，aNewList中元素的顺序倒过来了。

正如我们从上面的两个示例中看到的那样，标准的Collections.reverse方法对于反转List非常方便。

但是，如果我们正在学习 Java，我们可能希望自己练习实现一种“逆向”方法。

接下来，让我们探索几个不错的实现：一个使用递归，另一个使用简单循环。

## 4.使用递归反转列表

[首先，让我们使用递归](https://www.baeldung.com/java-recursion)技术实现我们自己的列表反转方法。首先，让我们看一下实现：

```java
public static <T> void reverseWithRecursion(List<T> list) {
    if (list.size() > 1) {
        T value = list.remove(0);
        reverseWithRecursion(list);
        list.add(value);
    }
}

```

正如我们所见，上面的实现看起来非常紧凑。现在，让我们了解它是如何工作的。

我们递归逻辑中的停止条件是list.size() <=1。换句话说，如果列表对象为空或仅包含一个元素，我们将停止递归。

在每次递归调用中，我们执行“ T value = list.remove(0) ”，从列表中弹出第一个元素。它以这种方式工作：

```bash
recursion step 0: value = null, list = (1, 2, 3, ... 7)
   |_ recursion step 1: value = 1, list = (2, 3, 4,...7)
      |_ recursion step 2: value = 2, list = (3, 4, 5, 6, 7)
         |_ recursion step 3: value = 3, list = (4, 5, 6, 7)
            |_ ...
               |_ recursion step 6: value = 6, list = (7) 

```

正如我们所见，当 列表对象只包含一个元素(7)时，我们停止递归，然后从底部开始执行list.add(value) 。也就是说，我们首先将 6 添加到列表的末尾，然后是 5，然后是 4，依此类推。最后，列表中元素的顺序被原地颠倒了。此外，该方法以线性时间运行。

接下来，让我们创建一个测试来验证我们的递归实现是否按预期工作：

```java
List<Integer> aList = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
ReverseArrayList.reverseWithRecursion(aList);
assertThat(aList).isEqualTo(EXPECTED);

```

如果我们运行测试，它就会通过。所以，我们的递归实现解决了这个问题。

## 5.使用迭代反转列表

我们刚刚使用递归反转了列表。或者，我们可以使用迭代来解决问题。

首先，让我们看一下实现：

```java
public static <T> void reverseWithLoop(List<T> list) {
    for (int i = 0, j = list.size() - 1; i < j; i++) {
        list.add(i, list.remove(j));
    }
}

```

正如我们所见，迭代实现也非常简洁。但是，我们只有一个for循环，并且在循环体中，我们只有一个语句。

接下来，让我们了解它是如何工作的。

我们在给定列表上定义了两个指针i和j。指针 j始终指向列表中的最后一个元素。但是点i 从 0 增加到j-1。

我们在每个迭代步骤中删除最后一个元素，并使用list.add(i, list.remove(j))将其填充到第i 个位置。当i到达j-1时，循环结束，我们反转了列表：

```bash
Iteration step 0: i = j = null, list = (1, 2, 3,...7)
Iteration step 1: i = 0; j = 6 
                  |_ list.add(0, list.remove(6))
                  |_ list = (7, 1, 2, 3, 4, 5, 6)
Iteration step 2: i = 1; j = 6 
                  |_ list.add(1, list.remove(6))
                  |_ list = (7, 6, 1, 2, 3, 4, 5)
...
Iteration step 5: i = 4; j = 6 
                  |_ list.add(4, list.remove(6))
                  |_ list = (7, 6, 5, 4, 3, 1, 2)
Iteration step 6: i = 5; j = 6 
                  |_ list.add(5, list.remove(6))
                  |_ list = (7, 6, 5, 4, 3, 2, 1)
```

该方法也在线性时间内运行。

最后，让我们测试一下我们的方法，看看它是否按预期工作：

```java
List<Integer> aList = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
ReverseArrayList.reverseWithLoop(aList);
assertThat(aList).isEqualTo(EXPECTED);

```

当我们运行上面的测试时，它通过了。

## 六，总结

在本文中，我们通过示例介绍了如何反转ArrayList 。标准的Collections.reverse方法可以很方便地解决这个问题。

但是，如果我们想创建自己的逆向实现，我们已经学习了两种有效的就地逆向方法。