## 1. 概述

通过本教程，我们将了解如何替换JavaArrayList中特定索引处的元素。

## 2. 常见做法

要替换现有元素，首先，我们需要[找到该元素](https://www.baeldung.com/find-list-element-java)在ArrayList中的确切位置。这个位置就是我们所说的索引。然后，我们可以用新元素替换旧元素。

在JavaArrayList中替换元素的最常见方法是使用set (int index, Object element)方法。set()方法有两个参数：现有项目的索引和新项目。

ArrayList的索引是从零开始的。因此，要替换第一个元素，0 必须是作为参数传递的索引。

如果提供的索引超出范围，将发生IndexOutOfBoundsException。

## 3.实施

让我们通过一个示例来了解如何在特定索引处替换JavaArrayList中的元素。

```java
List<Integer> EXPECTED = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

List<Integer> aList = new ArrayList<>(Arrays.asList(1, 2, 7, 4, 5));
aList.set(2, 3);

assertThat(aList).isEqualTo(EXPECTED);
```

首先，我们创建一个包含五个元素的ArrayList 。然后，我们用值 7 替换第三个元素，索引 2 为 3。最后，我们可以看到值 7 的索引 2 从列表中删除并更新为新值 3。另外，请注意列表大小为不受影响。

## 4。总结

在这篇快速文章中，我们学习了如何替换JavaArrayList中特定索引处的元素。此外，你可以将此方法用于任何其他列表类型，如LinkedList。只需确保你使用的列表不是不可变的。