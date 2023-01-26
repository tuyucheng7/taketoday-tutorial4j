## 1. 问题描述

给定一个排序后围绕未知点旋转的数组。查找数组是否有两个元素的总和等于给定的“x”。可以假设数组中的所有元素都是不同的。

示例：

```
输入: arr[] = {11, 15, 6, 8, 9, 10}, x = 16
输出: true
说明: (6, 10)的和为16

输入: arr[] = {11, 15, 26, 38, 9, 10}, x = 35
输出: true
说明: (26, 9)的和为35

输入: arr[] = {11, 15, 26, 38, 9, 10}, x = 45
输出: false
说明: 数组中没有两个元素的和为45
```

## 2. 算法实现

我们已经讨论了排序数组的O(n)解决方案。我们也可以将此解决方案扩展到旋转数组。这个想法是首先在数组中找到最大的元素，它也是轴点，最大元素之后是最小的元素。
一旦我们有了最大和最小元素的索引，我们使用类似的中间相遇算法来查找是否存在一对。这里唯一的新不同是使用模运算以循环方式递增和递减索引。

以下是上述算法的实现：

```java
public class PivotBinarySearch {

  public static boolean pairInSortedRotated(int[] arr, int n, int x) {
    int i;
    for (i = 0; i < n - 1; i++)
      if (arr[i] > arr[i + 1])
        break;
    int r = i;
    int l = (i + 1) % n;
    while (l != r) {
      if (arr[l] + arr[r] == x)
        return true;
      if (arr[l] + arr[r] < x)
        l = (l + 1) % n;
      else
        r = (n + r - 1) % n;
    }
    return false;
  }
}
```

上述方法的时间复杂度为O(n)。使用二分搜索查找pivot，可以将查找pivot的步骤优化为O(Log n)。

如何计算总和为x的所有对？

主要步骤分为以下几步：

1. 找到已排序和旋转数组的pivot元素，pivot元素是数组中最大的元素。最小的元素在它的右边与其相邻。
2. 使用两个指针(left和right)，left指向最小元素，right指向最大元素。
3. 求两个指针指向的元素之和。
4. 如果总和等于x，则递增count。如果总和小于x，说明需要增加两个元素的值，通过旋转方式将左指针移动到下一个位置。如
   果总和大于x，说明需要减小两个元素的值，通过旋转方式将右指针移动到下一个位置。
5. 重复步骤3和4，直到左指针不等于右指针或左指针不等于右指针–1。
6. 返回最终的count。

下面是上述算法的实现：

```java
public class PivotBinarySearch {

  public static int pairsCountInSortedRotated(int[] arr, int n, int x) {
    int count = 0;
    int i;
    for (i = 0; i < n - 1; i++)
      if (arr[i] > arr[i + 1])
        break;
    int l = (i + 1) % n;
    int r = i;
    while (r != l) {
      int sum = arr[r] + arr[l];
      if (sum == x) {
        count++;
        if (l == (n + r - 1) % n)
          return count;
        l = (l + 1) % n;
        r = (n + r - 1) % n;
      } else if (sum < x)
        l = (l + 1) % n;
      else
        r = (n + r - 1) % n;
    }
    return count;
  }

  public static int pairsCountInSortedRotated(int[] arr, int n, int x) {
    int count = 0;
    int i;
    for (i = 0; i < n - 1; i++)
      if (arr[i] > arr[i + 1])
        break;
    int l = (i + 1) % n;
    int r = i;
    while (r != l) {
      int sum = arr[r] + arr[l];
      if (sum == x) {
        count++;
        if (l == (n + r - 1) % n)
          return count;
        l = (l + 1) % n;
        r = (n + r - 1) % n;
      } else if (sum < x)
        l = (l + 1) % n;
      else
        r = (n + r - 1) % n;
    }
    return count;
  }
}
```

时间复杂度：O(n)

辅助空间：O(1)