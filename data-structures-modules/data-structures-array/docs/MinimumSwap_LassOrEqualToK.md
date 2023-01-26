## 1. 问题描述

给定一个包含n个正整数和一个数字k的数组。找出将所有小于或等于k的数字组合在一起所需的最小交换次数。

```
输入: arr[] = {2, 1, 5, 6, 3}, k = 3
输出: 1
解释: 要将元素2、1、3放在一起，科技将元素“5”与“3”交换，这样最终数组将是arr[] = {2, 1, 3, 6, 5}

输入: arr[] = {2, 7, 9, 5, 8, 7, 4}, k = 5
输出: 2
```

## 2. 算法分析

一个简单的解决方案是首先计算所有小于或等于k的元素。然后遍历每个子数组并交换那些值大于k的元素。这种方法的时间复杂度是O(n<sup>2</sup>)
一种有效的方法是使用双指针和滑动窗口。这种方法的时间复杂度是O(n)。

1. 统计小于或等于k的所有元素的个数。记为count。
2. 对长度为count的窗口使用双指针，每次跟踪此范围内有多少元素大于k，记为bad
3. 对每个长度为count的窗口重复步骤2，并在其中取最少的bad计数。这将是最终的答案。

## 3. 算法实现

```java
public class MinimumSwap {

  public static int minSwap(int[] arr, int k) {
    int n = arr.length;
    int count = 0;
    for (int i = 0; i < n; i++) // 统计小于或等于k的元素个数
      if (arr[i] <= k)
        ++count;
    int bad = 0;
    for (int i = 0; i < count; i++) // 在当前大小为count的窗口中统计大于k的元素个数
      if (arr[i] > k)
        ++bad;
    int res = bad; // 初始化结果
    for (int i = 0, j = count; j < n; ++i, ++j) {
      if (arr[i] > k)
        --bad; // 递减前一个窗口的计数
      if (arr[j] > k)
        ++bad; // 递增当前窗口的计数
      res = Math.min(res, bad);
    }
    return res;
  }
}
```