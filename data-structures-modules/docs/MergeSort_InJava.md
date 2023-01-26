## 1. 概述

在本文中，我们将了解归并排序算法及其在Java中的实现。

归并排序是最有效的排序技术之一，它基于“分而治之”思想。

## 2. 算法原理

合并排序是一种“分而治之”的算法，我们首先将问题划分为子问题。当子问题的足以被解决时，我们将它们组合在一起以获得问题的最终解决方案。

我们可以使用递归轻松地实现该算法，因为我们处理的是子问题而不是主要问题。

我们可以将该算法描述为以下两步过程：

+ 划分：在这一步中，我们将输入数组分成两半，轴是数组的中点。对所有半数组递归执行此步骤，直到没有更多半数组需要划分。
+ 合并：在这一步中，我们从下到上对划分后的数组进行排序并且合并，得到排序后的数组。

下图显示了数组{10, 6, 8, 5, 7, 3, 4}的完整归并排序过程。

如果我们仔细看这个图，我们可以看到数组被递归地分成两半，直到大小变为1。一旦大小变为1，就开始进行合并过程，并在排序时开始合并数组：

<img src="../assets/MergeSort_InJava-1.png">

## 3. 实现

对于实现，我们将编写一个mergeSort()方法，该方法将输入数组及其长度作为参数。这将是一个递归方法，因此我们需要基准条件和递归条件。

基准条件检查数组长度是否为1，它只是简单return。对于其余情况，将执行递归调用。

对于递归情况，我们获取中间索引并创建两个临时数组l[]和r[]。然后我们为两个子数组递归调用mergeSort()方法：

```
public static void mergeSort(int[] a, int n) {
  if (n < 2) {
    return;
  }
  int mid = n / 2;
  int[] l = new int[mid];
  int[] r = new int[n - mid];
  for (int i = 0; i < mid; i++) {
    l[i] = a[i];
  }
  for (int i = mid; i < n; i++) {
    r[i - mid] = a[i];
  }
  mergeSort(l, mid);
  mergeSort(r, n - mid);
  merge(a, l, r, mid, n - mid);
}
```

接下来，我们调用merge方法，它接收输入数组和两个子数组，以及两个子数组的开始和结束索引。

merge方法将两个子数组的元素一一比较，并将较小的元素放入输入数组中。

当我们到达其中一个子数组的末尾时，另一个数组中的其余元素被到输入数组中，从而为我们提供了最终的排序数组：

```
private static void merge(int[] a, int[] l, int[] r, int left, int right) {
  int i = 0, j = 0, k = 0;
  while (i < left && j < right) {
    if (l[i] <= r[j])
      a[k++] = l[i++];
    else
      a[k++] = r[j++];
  }
  while (i < left)
    a[k++] = l[i++];
  while (j < right) {
    a[k++] = r[j++];
  }
}
```

该程序的单元测试为：

```java
class MergeSortUnitTest {

  @Test
  void positiveTest() {
    int[] actual = {5, 1, 6, 2, 3, 4};
    int[] expected = {1, 2, 3, 4, 5, 6};
    MergeSort.mergeSort(actual, actual.length);
    assertArrayEquals(expected, actual);
  }
}
```

## 4. 复杂度

由于归并排序是一种递归算法，时间复杂度可以表示为以下递归关系：

> T(n) = 2T(n/2) + O(n)

2T(n/2)对应子数组排序所需的时间，O(n)是合并整个数组的时间。

求解后，时间复杂度将达到O(nLogn)。

对于最坏、平均和最好的情况都是这样，因为它总是将数组一分为二，然后合并。

该算法的空间复杂度为O(n)，因为我们在每个递归调用中创建临时数组。