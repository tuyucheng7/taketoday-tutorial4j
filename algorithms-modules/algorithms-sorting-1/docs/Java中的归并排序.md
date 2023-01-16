## 1. 简介

在本教程中，我们将了解合并排序算法及其在Java中的实现。

合并排序是最有效的排序技术之一，它基于[“分而治之”范式](https://www.baeldung.com/cs/divide-and-conquer-strategy)。

## 2.算法

合并排序是一种“分而治之”的算法，我们首先将问题划分为子问题。当子问题的解决方案准备就绪后，我们将它们组合在一起以获得问题的最终解决方案。

我们可以很容易地使用递归来实现这个算法，因为我们处理的是子问题而不是主要问题。

我们可以将算法描述为以下两步过程：

-   划分：在这一步中，我们将输入数组分成两半，枢轴是数组的中点。这一步对所有的半阵递归进行，直到没有半阵可以划分为止。
-   征服：在这一步中，我们将划分后的数组从下往上排序合并，得到排序后的数组。

下图显示了示例数组 {10, 6, 8, 5, 7, 3, 4} 的完整归并排序过程。

如果我们仔细看一下图表，我们可以看到数组被递归地分成两半，直到大小变为 1。一旦大小变为 1，合并过程开始起作用并在排序时开始合并数组：

[![合并排序1](https://www.baeldung.com/wp-content/uploads/2018/09/mergesort1.png)](https://www.baeldung.com/wp-content/uploads/2018/09/mergesort1.png)

## 3.实施

对于实现，我们将编写一个mergeSort函数，它将输入数组及其长度作为参数。这将是一个递归函数，所以我们需要基础和递归条件。

基本条件检查数组长度是否为 1，它只会返回。对于其余情况，将执行递归调用。

对于递归情况，我们获取中间索引并创建两个临时数组l[]和r[]。然后我们为两个子数组递归调用mergeSort函数：

```java
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

接下来，我们调用合并函数，它接受输入和两个子数组，以及两个子数组的开始和结束索引。

merge函数将两个子数组的元素一一比较，并将较小的元素放入输入数组中。

当我们到达其中一个子数组的末尾时，另一个数组中的其余元素将被到输入数组中，从而为我们提供最终的排序数组：

```java
public static void merge(
  int[] a, int[] l, int[] r, int left, int right) {
 
    int i = 0, j = 0, k = 0;
    while (i < left && j < right) {
        if (l[i] <= r[j]) {
            a[k++] = l[i++];
        }
        else {
            a[k++] = r[j++];
        }
    }
    while (i < left) {
        a[k++] = l[i++];
    }
    while (j < right) {
        a[k++] = r[j++];
    }
}

```

该程序的单元测试是：

```java
@Test
public void positiveTest() {
    int[] actual = { 5, 1, 6, 2, 3, 4 };
    int[] expected = { 1, 2, 3, 4, 5, 6 };
    MergeSort.mergeSort(actual, actual.length);
    assertArrayEquals(expected, actual);
}
```

## 4. 复杂性

由于归并排序是一种递归算法，其时间复杂度可以表示为如下递归关系：

```plaintext
T(n) = 2T(n/2) + O(n)
```

2T(n/2)对应子数组排序需要的时间，O(n)是合并整个数组的时间。

解决后，时间复杂度会来到O(nLogn)。

对于最坏的、平均的和最好的情况都是如此，因为它总是将数组分成两部分然后合并。

该算法的空间复杂度为O(n)，因为我们在每次递归调用中创建临时数组。

## 5.总结

在这篇简短的文章中，我们探讨了归并排序算法以及如何在Java中实现它。