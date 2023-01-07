## 1. 概述

[在本教程中，我们将学习如何在ArrayList](https://www.baeldung.com/java-arraylist)中的特定位置插入对象。

## 2.例子

如果我们想将一个元素添加到[ArrayList](https://www.baeldung.com/java-arraylist)的特定位置，我们可以使用通过实现List<E>接口提供的add(int index, E element) 方法。这个方法让我们在特定索引处添加一个元素。

如果索引超出范围 (index < 0 or index > size())，它也可以抛出IndexOutOfBoundsException。这意味着如果我们在ArrayList中只有 4 个项目，我们不能使用它在位置 4 添加项目，因为我们从 0 开始计数。我们必须在这里使用标准的add(E e) 方法。

首先，我们将创建一个新的ArrayList并向其添加四个元素：

```java
List<Integer> integers = new ArrayList<>();
integers.add(5);
integers.add(6);
integers.add(7);
integers.add(8);
System.out.println(integers);
```

这将导致：

[![img_637528671724b](https://www.baeldung.com/wp-content/uploads/2022/11/img_637528671724b.svg)](https://www.baeldung.com/wp-content/uploads/2022/11/img_637528671724b.svg)

现在，如果我们在索引 1 处添加另一个元素：

```java
integers.add(1,9);
System.out.println(integers);
```

ArrayList 内部将首先移动从给定索引开始的对象：

[![img_637528683cc07](https://www.baeldung.com/wp-content/uploads/2022/11/img_637528683cc07.svg)](https://www.baeldung.com/wp-content/uploads/2022/11/img_637528683cc07.svg)

这是可行的，因为ArrayList是一个可增长的数组，可以根据需要自动调整容量：

[![img_63752869916a0](https://www.baeldung.com/wp-content/uploads/2022/11/img_63752869916a0.svg)](https://www.baeldung.com/wp-content/uploads/2022/11/img_63752869916a0.svg)

然后在给定索引处添加新项目：

[![img_6375286ad6a38](https://www.baeldung.com/wp-content/uploads/2022/11/img_6375286ad6a38.svg)](https://www.baeldung.com/wp-content/uploads/2022/11/img_6375286ad6a38.svg)

添加特定索引将导致ArrayList的平均操作性能为 O(n/2 ) 。例如，[LinkedList](https://www.baeldung.com/java-linkedlist)的平均复杂度为 O(n/4)，如果索引为 0，则复杂度为 O(1)。因此，如果我们严重依赖于在特定位置添加元素，则需要仔细研究[LinkedList 。](https://www.baeldung.com/java-linkedlist)

我们还可以看到元素的顺序不再正确。当我们在特定位置手动添加项目时，这是我们经常想要实现的。否则，我们可以使用integers.sort(Integer::compareTo) 再次对ArrayList进行排序 或实现我们自己的比较器。

## 3.总结

在本文中，我们讨论了add(int index, E element) 方法，因此我们可以在特定位置向ArrayList<E>添加新元素。我们必须注意保持在ArrayList的索引范围内，并确保我们允许正确的对象。