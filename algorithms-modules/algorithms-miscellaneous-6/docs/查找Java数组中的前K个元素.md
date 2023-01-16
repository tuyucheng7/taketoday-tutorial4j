## 1. 概述

在本教程中，我们将针对使用 Java查找数组中的k个最大元素的问题实施不同的解决方案。为了描述时间复杂度，我们将使用[大 O](https://www.baeldung.com/cs/big-o-notation)表示法。

## 2. 暴力解决方案

这个问题的蛮力解决方案是迭代给定的数组k次。在每次迭代中，我们都会 找到最大值。然后我们将从数组中删除这个值并放入输出列表中：

```java
public List findTopK(List input, int k) {
    List array = new ArrayList<>(input);
    List topKList = new ArrayList<>();

    for (int i = 0; i < k; i++) {
        int maxIndex = 0;

        for (int j = 1; j < array.size(); j++) {
            if (array.get(j) > array.get(maxIndex)) {
                maxIndex = j;
            }
        }

        topKList.add(array.remove(maxIndex));
    }

    return topKList;
}
```

如果我们假设n是给定数组的大小，则此解决方案的时间复杂度为O(n  k)。此外，这是效率最低的解决方案。

## 3.Java集合方法

然而，存在针对该问题的更有效的解决方案。在本节中，我们将使用Java集合解释其中的两个。

### 3.1. 树集

TreeSet以[红黑树](https://www.baeldung.com/cs/red-black-trees) 数据结构为骨干。因此，将值放入该集合的成本为O(log n)。TreeSet是一个排序的集合。因此，我们可以将所有值放入TreeSet 并 提取其中的前k个值：

```java
public List<Integer> findTopK(List<Integer> input, int k) {
    Set<Integer> sortedSet = new TreeSet<>(Comparator.reverseOrder());
    sortedSet.addAll(input);

    return sortedSet.stream().limit(k).collect(Collectors.toList());
}
```

此解决方案的时间复杂度为O(n  log n)。最重要的是，如果k ≥ log n，这应该比蛮力方法更有效。

请务必记住TreeSet不包含重复项。因此，该解决方案仅适用于具有不同值的输入数组。

### 3.2. 优先队列

PriorityQueue是Java中的 堆 数据结构。在它的帮助下，我们可以实现O(n  log k)的解决方案。此外，这将是一个比前一个更快的解决方案。由于上述问题，k始终小于数组的大小。所以，这意味着O(n  log k) ≤ O(n  log n)。

该算法通过给定的数组迭代一次。在每次迭代中，我们都会向堆中添加一个新元素。此外，我们将保持堆的大小小于或等于k 。因此，我们必须从堆中删除多余的元素并添加新元素。因此，在遍历数组后，堆将包含k个最大值：

```java
public List<Integer> findTopK(List<Integer> input, int k) {
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>();

    input.forEach(number -> {
        maxHeap.add(number);

        if (maxHeap.size() > k) {
            maxHeap.poll();
        }
    });

    List<Integer> topKList = new ArrayList<>(maxHeap);
    Collections.reverse(topKList);

    return topKList;
}
```

## 4.选择算法

有许多方法可以解决给定的问题。而且，虽然它超出了本教程的范围，但使用[选择算法](https://en.wikipedia.org/wiki/Selection_algorithm)方法 将是最好的，因为它会产生线性时间复杂度。

## 5.总结

在本教程中，我们描述了用于查找数组中k个最大元素的几种解决方案。