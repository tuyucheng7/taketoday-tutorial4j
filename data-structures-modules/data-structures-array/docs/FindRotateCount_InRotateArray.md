## 1. 问题描述

考虑按升序排序的不同数字的数组arr[]。假设此数组已顺时针旋转k次。给定这样一个数组，求k的值。

```
输入: arr[] = {15, 18, 2, 3, 6, 12}
输出: 2
解释: 初始化数组必须为{2, 3, 6, 12, 15, 18}. 在将初始数组旋转两次后，我们得到了给定的数组。

输入: arr[] = {7, 9, 11, 12, 5}
输出: 4

输入: arr[] = {7, 9, 11, 12, 15};
输出: 0
```

## 2. 使用线性搜索

如果我们仔细看以上的示例，我们可以注意到旋转的次数等于数组中最小元素的索引。一个简单的线性搜索解决方案是找到最小元素并返回其索引。

下面是上述思想的具体实现：

```java
public class FindRotateCount {

  public static int countRotationsUsingLinearSearch(int[] arr, int n) {
    int min = arr[0];
    int minIndex = 0;
    for (int i = 1; i < n; i++) {
      if (arr[i] < min) {
        min = arr[i];
        minIndex = i;
      }
    }
    return minIndex;
  }
}
```

时间复杂度：O(n)

辅助空间：O(1)

## 3. 使用二分搜索

这里我们也找到最小元素的索引，但使用了二分搜索：

+ 最小元素是唯一前一个元素大于它的元素。如果没有前一个元素，则没有数组没有旋转(第一个元素是最小的)。
  我们通过将其与第(mid-1)'th 和 (mid+1)'th 元素进行比较来检查中间元素的条件。

+ 如果最小元素不在中间(既不是mid也不是mid + 1)，则最小元素位于左半部分或右半部分。
    1. 如果中间元素小于最后一个元素，则最小元素位于左半部分。
    2. 否则最小元素位于右半部分。

以下为具体实现：

```java
public class FindRotateCount {

  public static int countRotationUsingBinarySearch(int[] arr, int low, int high) {
    int minIndex = 0;
    if (low > high)
      return 0;
    if (high == low)
      return low;
    int mid = low + (high - low) / 2;
    if (mid > low && arr[mid] < arr[mid - 1])
      return mid;
    if (mid < high && arr[mid] > arr[mid + 1])
      return mid + 1;
    if (arr[high] > arr[mid])
      return countRotationUsingBinarySearch(arr, low, mid - 1);
    return countRotationUsingBinarySearch(arr, mid + 1, high);
  }
}
```

时间复杂度：O(log n)

辅助空间：O(log n)

## 4. 迭代二分搜索

```java
public class FindRotateCount {

  public static int countRotationUsingBinarySearchIterative(int[] arr, int n) {
    int low = 0;
    int high = n - 1;
    while (low <= high) {
      int mid = low + (high - low) / 2;
      int previous = (mid - 1 + n) % n;
      int next = (mid + 1) % n;
      if (arr[mid] <= arr[previous] && arr[mid] <= arr[next])
        return mid;
      else if (arr[mid] <= arr[high])
        high = mid - 1;
      else if (arr[mid] >= arr[low])
        low = mid + 1;
    }
    return 0;
  }
}
```

时间复杂度：O(log n)

辅助空间：O(1)