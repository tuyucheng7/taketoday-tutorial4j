## 1. 问题描述

给定一个由n个整数组成的数组arr[]，求i  arr[i]值之和的最大值，其中i从0到n-1变化。

示例：

```
输入: arr[]={8, 3, 1, 2}
输出: 29
说明: 让我们看看所有可能的旋转后的数组，
{8, 3, 1, 2} = 80 + 31 + 12 + 23 = 11
{3, 1, 2, 8} = 30 + 11 + 22 + 83 = 29
{1, 2, 8, 3} = 10 + 21 + 82 + 33 = 27
{2, 8, 3, 1} = 20 + 81 + 32 + 13 = 17

输入: arr[]={3, 2, 1}
输出: 7
说明: 让我们看看所有可能的旋转后的数组，
{3, 2, 1} = 30 + 21 + 12 = 4
{2, 1, 3} = 20 + 11 + 32 = 7
{1, 3, 2} = 10 + 31 + 22 = 7
```

## 2. 方法一

### 2.1 算法思想

该方法主要讨论需要O(n<sup>2</sup>)时间的原始解决方案。

该解决方案涉及求每次旋转后数组所有元素的和，然后确定最大总和值。

一个简单的解决方案是尝试所有可能的旋转数组。计算每个旋转后数组的i  arr[i]之和，并返回最大值。

步骤：

1. 将数组旋转0 ~ n次。
2. 计算每次旋转的总和。
3. 检查maximum是否大于当前的总和，然后更新maximum。

### 2.2 算法实现

```java
public class MaximumValueWithRotation {

  public static int maxSumNativeMethod(int[] arr, int n) {
    int res = Integer.MIN_VALUE;
    for (int i = 0; i < n; i++) {
      int current_sum = 0;
      for (int j = 0; j < n; j++) {
        int index = (j + i) % n;
        current_sum += j  arr[index];
      }
      res = Math.max(res, current_sum);
    }
    return res;
  }
}
```

### 2.3 复杂度分析

时间复杂度：O(n<sup>2</sup>)

辅助空间：O(1)

## 3. 方法二

### 3.1 算法思想

该方法讨论了在O(n)时间内解决问题的有效方法。在原始解决方案中，每次旋转都会计算这些值。因此，如果这可以在恒定时间内完成，那么复杂度将会降低。

基本方法是根据前一次旋转的总和计算新旋转的总和。这就产生了一种相似性，其中只有第一个和最后一个元素的乘数发生剧烈变化，并且每个其他元素的乘数增加或减少1。
因此，通过这种方式，可以从当前旋转的总和计算下一个旋转的总和。

算法：其思想是使用前一次旋转的值来计算这一次旋转的值。当一个数组旋转1时，i  arr[i] 的总和会发生以下变化。

1. arr[i-1]的乘数从0变为 n-1，即arr[i-1](n-1)与当前值相加。
2. 其他项的乘数减1。即，从当前值中减去(cum_sum – arr[i-1])，其中cum_sum是所有数字的总和。

```
next_val = curr_val - (cum_sum - arr[i-1]) + arr[i-1]  (n-1);

next_val = 旋转1次后∑iarr[i]的值。
curr_val = ∑iarr[i]当前值。 
cum_sum = 所有数组元素的和，∑arr[i]。

举个例子{1, 2, 3}，curr_val = 10+21+32 = 8. 
向左旋转一次后的数组为{2, 3, 1}。
next_val = curr_val - (cum_sum - arr[i-1]) + arr[i-1]  (n-1) = 8 - (6 - 1) + 1  2 = 5，与20+31+12 = 5相同。
```

### 3.2 算法实现

```java
public class MaximumValueWithRotation {

  public static int maxSumUsingFormula(int[] arr) {
    int n = arr.length;
    int cum_sum = 0;
    for (int i = 0; i < n; i++) // 计算所有数组元素的和
      cum_sum += arr[i];
    int curr_val = 0;
    for (int i = 0; i < n; i++) // 计算数组未旋转时i  arr[i]的和
      curr_val += i  arr[i];
    int res = curr_val; // 初始化返回值
    for (int i = 1; i < n; i++) { // 计算旋转i次时的和
      int next_val = curr_val - (cum_sum - arr[i - 1]) + arr[i - 1]  (n - 1); // 使用curr_val计算next_val
      curr_val = next_val; // 更新curr_val
      res = Math.max(res, next_val); // res取current_val和next_val的最大值。
    }
    return res;
  }
}
```

### 3.3 复杂度分析

时间复杂度：O(n)，因为从0到n需要一个循环来检查所有旋转的可能性，并且当前旋转的总和是从O(1)时间内的前一个旋转值计算的。

辅助空间：O(1)，不需要额外空间。

## 4. 使用pivot

该方法讨论了在O(n)时间内使用pivot的解决方案。pivot方法只能用于排序或旋转排序数组的情况。例如：{1, 2, 3, 4}或{2, 3, 4, 1}, {3, 4, 1, 2}等。

让我们假设数组已排序的情况。正如我们所知，对于数组，最大和将是当数组按升序排序时。如果是已排序的旋转数组，我们可以重新旋转数组以使其升序。
因此，在这种情况下，需要找到pivot元素，然后才能计算最大和。

算法：

1. 找到数组的pivot：如果arr[i] > arr[(i+1) % n]，则它是pivot元素。(i+1) % n用于检查最后一个和第一个元素。
2. 获得pivot后，可以通过找到与pivot的差值来计算总和，该差值将是乘数，并在计算总和时将其与当前元素相乘。

