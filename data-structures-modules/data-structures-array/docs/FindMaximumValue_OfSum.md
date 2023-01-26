## 1. 问题描述

给定一个数组，只允许对数组执行旋转操作。我们可以根据需要多次旋转数组。返回i  arr[i]的最大可能总和。

示例：

```
输入: arr[] = {1, 20, 2, 10}
输出: 72
说明: 我们可以通过旋转数组两次得到72。
{2, 10, 1, 20}
20  3 + 1  2 + 10  1 + 2  0 = 72

输入: arr[] = {10, 1, 2, 3, 4, 5, 6, 7, 8, 9}
输出: 330
说明: 我们可以通过将数组旋转9次得到 330。
{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
0  1 + 1  2 + 2  3 ... 9  10 = 330
```

## 2. 算法分析

一个简单的解决方案是逐个查找所有可能情况，检查每个旋转后数组的和并返回最大和。该解的时间复杂度为O(n<sup>2</sup>)。

我们可以使用有效的解决方案在O(n)时间内解决这个问题。设Rj为旋转j次后i  arr[i]的值。
其思想是从前一个旋转总和计算下一个旋转总和，即从R(j - 1)计算Rj。我们可以将结果的初始值计算为R0，然后继续计算下一个旋转值。

如何有效地从R(j - 1)计算Rj？

这可以在O(1)时间内完成。详细步骤如下：

```
让我们计算没有旋转时的i  arr[i]的初始值R0 = 0  arr[0] + 1  arr[1] +...+ (n-1)  arr[n-1]

旋转1次后，arr[n-1]成为数组的第一个元素，arr[0]成为第二个元素，arr[1]成为第三个元素等等。

R1 = 0  arr[n-1] + 1  arr[0] + ... + (n-1)  arr[n-2]

R1 - R0 = arr[0] + arr[1] + ... + arr[n-2] - (n-1)  arr[n-1]

经过2次旋转arr[n-2]，成为数组的第一个元素，arr[n-1]成为第二个元素，arr[0]成为第三个元素等等。

R2 = 0  arr[n-2] + 1  arr[n-1] +...+ (n-1)  arr[n-3]

R2 - R1 = arr[0] + arr[1] + ... + arr[n-3] - (n-1)  arr[n-2] + arr[n-1]

如果我们仔细观察上述数值，我们可以观察到下方公式

Rj - R(j - 1) = arrSum - n  arr[n-j]

其中arrSum是所有数组元素的和。即，arrSum = ∑ arr[i] (0 <= i <= n-1)
```

以下是完整的算法：

```
1. 计算数组所有元素的和。假设为“arrSum”。
2. 通过对给定数组执行i  arr[i]来计算R0。让这个值为currentVal。
3. 初始化结果：maxVal=currentVal // maxVal为返回的结果。
4. j = 1 to n - 1  // 这个循环从Rj-1计算Rj
   a) currentVal = currentVal + arrSum - n  arr[n-j];
   b) If (currentrVal > maxVal)
       maxVal = currentVal 
5. return maxVal
```

## 3. 算法实现

以下是上述算法的具体实现：

```java
public class MaximumValueWithRotation {

  public static int maxSum(int[] arr) {
    int arrSum = 0;
    int n = arr.length;
    int currentVal = 0;
    for (int i = 0; i < n; i++) {
      arrSum += arr[i];
      currentVal += i  arr[i];
    }
    int maxVal = currentVal;
    for (int j = 1; j < n; j++) {
      currentVal = currentVal + arrSum - n  arr[n - j];
      if (currentVal > maxVal)
        maxVal = currentVal;
    }
    return maxVal;
  }
}
```

时间复杂度：O(n)

辅助空间：O(1)