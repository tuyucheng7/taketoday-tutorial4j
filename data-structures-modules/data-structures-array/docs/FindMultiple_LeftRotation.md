## 1. 问题描述

给定一个大小为n的数组和多个值，我们需要围绕该数组左旋。如何快速找到多个左旋转？

示例：

```
输入: arr[] = {1, 3, 5, 7, 9}
        k1 = 1
        k2 = 3
        k3 = 4
        k4 = 6
输出: 3 5 7 9 1
     7 9 1 3 5
     9 1 3 5 7
     3 5 7 9 1

输入: arr[] = {1, 3, 5, 7, 9} 
        k1 = 14 
输出: 9 1 3 5 7
```

## 2. 算法实现

在之前的文章中已经讨论过类似的几种方法实现了。

其中最佳方法需要O(n)时间和O(1)额外空间。

有效方法：

当需要进行一次旋转时，上述方法效果很好。这些方法修改了原始数组。为了处理数组旋转的多个查询，我们使用大小为2n的临时数组并快速处理旋转。

```
1. 在temp[0...2n-1]数组中整个数组两次。
2. 在temp[]中旋转k次后数组的起始位置将是k % n。我们做
3. 打印temp[]数组，从k % n 到k % n + n。
```

以下为具体实现：

```java
public class FindMultipleLeftRotation {

  public static void preProcess(int[] arr, int n, int[] temp) {
    for (int i = 0; i < n; i++)
      temp[i] = temp[i + n] = arr[i];
  }

  public static void leftRotate(int[] arr, int n, int k, int[] temp) {
    int start = k % n;
    for (int i = start; i < n + start; i++)
      System.out.print(temp[i] + " ");
    System.out.println("n");
  }
}
```

请注意，查找旋转起始地址的任务需要O(1)时间。它打印需要O(n)时间的元素。

## 3. 空间优化方法

上述方法需要额外的空间。下面给出了一个空间优化的解决方案。

```java
public class FindMultipleLeftRotation {

  public static void spaceOptimizationLefeRotate(int[] arr, int n, int k) {
    for (int i = k; i < k + n; i++)
      System.out.print(arr[i % n] + " ");
    System.out.println("n");
  }
}
```

时间复杂度：O(n)，我们使用循环遍历n次。

辅助空间：O(1)，我们没有使用任何额外的空间。