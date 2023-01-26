## 1. 问题描述

已排序后的数组在某个未知点旋转，找到其中的最小元素。

以下解决方案假设所有元素都是不同的。

```
输入: {5, 6, 1, 2, 3, 4}
输出: 1

输入: {1, 2, 3, 4}
输出: 1

输入: {2, 1}
输出: 1
```

## 2. 算法分析

一个简单的解决方案是遍历整个数组并找到一个最小值。该解决方案需要O(n)时间。

我们可以使用二分搜索在O(Logn)时间内完成它。如果我们仔细看一下上述示例，我们可以很容易地找出以下规律：

+ 最小元素是唯一其前一个大于它的元素。如果没有前一个元素，则没有旋转(第一个元素是最小的)。我们通过将中间元素与第(mid-1)'th 和(mid+1)'th元素进行比较来检查中间元素的条件。
+ 如果最小元素不在中间(既不是mid也不是mid + 1)，则最小元素位于左半部分或右半部分。
    1. 如果中间元素小于最后一个元素，则最小元素位于左半部分。
    2. 否则，最小元素位于右半部分。

以下为具体实现：

```java
public class FindMinimumElement {

  public static int findMinUsingBinarySearch(int[] arr, int low, int high) {
    if (low > high)
      return arr[0];
    if (high == low)
      return arr[low];
    int mid = low + (high - low) / 2;
    if (low < mid && arr[mid] < arr[mid - 1])
      return arr[mid];
    if (mid < high && arr[mid] > arr[mid + 1])
      return arr[mid + 1];
    if (arr[mid] < arr[high])
      return findMinUsingBinarySearch(arr, low, mid - 1);
    return findMinUsingBinarySearch(arr, mid + 1, high);
  }
}
```

## 3. 改进算法

如何处理重复项？

上述方法在最坏情况下(如果所有元素都相同)的时间复杂度为O(n)。

下面是在O(log n)时间内处理重复项的代码。

```java
public class FindMinimumElement {

  public static int findMinWithDuplicateElement(int[] arr, int low, int high) {
    while (low < high) {
      int mid = low + (high - low) / 2;
      if (arr[mid] == arr[high])
        high--;
      else if (arr[mid] > arr[high])
        low = mid + 1;
      else
        high = mid;
    }
    return arr[high];
  }
}
```