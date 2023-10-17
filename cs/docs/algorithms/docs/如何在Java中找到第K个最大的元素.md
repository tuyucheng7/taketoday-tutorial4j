## 1. 简介

在本文中，我们将介绍在唯一数字序列中查找第k大元素的各种解决方案。我们将在示例中使用一个整数数组。

我们还将讨论每种算法的平均和最坏情况下的时间复杂度。

## 2.解决方案

现在让我们探讨一些可能的解决方案——一种使用普通排序，另两种使用从快速排序派生的快速选择算法。

### 2.1. 排序

当我们思考这个问题时，可能想到的最明显的解决方案就是 对数组进行排序。

让我们定义所需的步骤：

-   对数组进行升序排序
-   由于数组的最后一个元素将是最大的元素，因此第k个最大的元素将位于第 x个索引处，其中x = length(array) – k

正如我们所见，解决方案很简单，但需要对整个数组进行排序。因此，时间复杂度将为O(nlogn)：

```java
public int findKthLargestBySorting(Integer[] arr, int k) {
    Arrays.sort(arr);
    int targetIndex = arr.length - k;
    return arr[targetIndex];
}
```

另一种方法是按降序对数组进行排序，并简单地返回第(k-1)个索引上的元素：

```java
public int findKthLargestBySortingDesc(Integer[] arr, int k) {
    Arrays.sort(arr, Collections.reverseOrder());
    return arr[k-1];
}
```

### 2.2. 快速选择

这可以被认为是对先前方法的优化。在这里，我们选择[QuickSort](https://www.geeksforgeeks.org/quick-sort/)进行排序。分析问题陈述，我们意识到我们实际上不需要对整个数组进行排序——我们只需要重新排列它的内容，使数组的第k个元素成为第k个最大或最小的元素。

在 QuickSort 中，我们选择一个枢轴元素并将其移动到正确的位置。我们还围绕它对数组进行分区。在 QuickSelect 中，想法是在枢轴本身是第k个最大元素的点处停止。

如果我们不对枢轴的左右两侧进行重复，我们可以进一步优化算法。我们只需要根据枢轴的位置对其中之一进行递归即可。

我们先看一下QuickSelect算法的基本思想：

-   选择一个枢轴元素并相应地对数组进行分区
    -   选择最右边的元素作为枢轴
    -   重新排列数组，使枢轴元素放置在正确的位置——所有小于枢轴的元素将位于较低的索引处，大于枢轴的元素将放置在高于枢轴的索引处
-   如果 pivot 位于数组中的第k个元素，则退出该过程，因为 pivot 是第k个最大的元素
-   如果主元位置大于k，则对左子数组继续处理，否则对右子数组重复处理

我们可以编写通用逻辑，它也可用于查找第k个最小元素。我们将定义一个方法findKthElementByQuickSelect()，它将返回排序数组中的第k个元素。

如果我们按升序对数组进行排序，则数组的第k个元素将是第k个最小的元素。要找到第k个最大的元素，我们可以传递k= length(Array) – k。

让我们实现这个解决方案：

```java
public int 
  findKthElementByQuickSelect(Integer[] arr, int left, int right, int k) {
    if (k >= 0 && k <= right - left + 1) {
        int pos = partition(arr, left, right);
        if (pos - left == k) {
            return arr[pos];
        }
        if (pos - left > k) {
            return findKthElementByQuickSelect(arr, left, pos - 1, k);
        }
        return findKthElementByQuickSelect(arr, pos + 1,
          right, k - pos + left - 1);
    }
    return 0;
}
```

现在让我们实现分区方法，它选择最右边的元素作为基准，将其放在适当的索引处，并以较低索引处的元素应小于基准元素的方式对数组进行分区。

同样，索引较高的元素将大于枢轴元素：

```java
public int partition(Integer[] arr, int left, int right) {
    int pivot = arr[right];
    Integer[] leftArr;
    Integer[] rightArr;

    leftArr = IntStream.range(left, right)
      .filter(i -> arr[i] < pivot)
      .map(i -> arr[i])
      .boxed()
      .toArray(Integer[]::new);

    rightArr = IntStream.range(left, right)
      .filter(i -> arr[i] > pivot)
      .map(i -> arr[i])
      .boxed()
      .toArray(Integer[]::new);

    int leftArraySize = leftArr.length;
    System.arraycopy(leftArr, 0, arr, left, leftArraySize);
    arr[leftArraySize+left] = pivot;
    System.arraycopy(rightArr, 0, arr, left + leftArraySize + 1,
      rightArr.length);

    return left + leftArraySize;
}
```

有一种更简单的迭代方法来实现分区：

```java
public int partitionIterative(Integer[] arr, int left, int right) {
    int pivot = arr[right], i = left;
    for (int j = left; j <= right - 1; j++) {
        if (arr[j] <= pivot) {
            swap(arr, i, j);
            i++;
        }
    }
    swap(arr, i, right);
    return i;
}

public void swap(Integer[] arr, int n1, int n2) {
    int temp = arr[n2];
    arr[n2] = arr[n1];
    arr[n1] = temp;
}
```

该解决方案平均在O(n)时间内有效。但是，在最坏的情况下，时间复杂度将为O(n^2)。

### 2.3. 具有随机分区的 QuickSelect

这种方法是对先前方法的轻微修改。如果数组几乎/完全排序并且如果我们选择最右边的元素作为主元，则左右子数组的划分将非常不均匀。

此方法建议以随机方式选择初始枢轴元素。不过，我们不需要更改分区逻辑。

我们不调用partition，而是调用randomPartition方法，它会选择一个随机元素并将其与最右边的元素交换，然后最终调用partition方法。

让我们实现randomPartition方法：

```java
public int randomPartition(Integer arr[], int left, int right) {
    int n = right - left + 1;
    int pivot = (int) (Math.random())  n;
    swap(arr, left + pivot, right);
    return partition(arr, left, right);
}
```

在大多数情况下，此解决方案比前一种情况效果更好。

随机化 QuickSelect 的预期时间复杂度为O(n)。

然而，最坏的时间复杂度仍然是O(n^2)。

## 3.总结

在本文中，我们讨论了在唯一数字数组中查找第k个最大(或最小)元素的不同解决方案。最简单的解决方案是对数组进行排序并返回第k个元素。此解决方案的时间复杂度为O(nlogn)。

我们还讨论了快速选择的两种变体。该算法并不简单，但在一般情况下具有O(n)的时间复杂度。