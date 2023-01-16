## 1. 概述

最大子数组问题是在任何给定数组中找到具有最大总和的一系列连续元素的任务。

例如，在下面的数组中，突出显示的子数组具有最大 sum(6)：

[![最大子数组](https://www.baeldung.com/wp-content/uploads/2019/12/max_subarray_example.jpg)](https://www.baeldung.com/wp-content/uploads/2019/12/max_subarray_example.jpg)

在本教程中，我们将了解在数组中查找最大子数组的两种解决方案。我们将使用O(n) [时间和空间复杂度](https://www.baeldung.com/java-algorithm-complexity)来设计其中之一。

## 2. 暴力破解算法

蛮力是一种解决问题的迭代方法。在大多数情况下，解决方案需要对数据结构进行多次迭代。在接下来的几节中，我们将应用这种方法来解决最大子数组问题。

### 2.1. 方法

一般来说，第一个想到的解决方案是计算每个可能的子数组的和，并返回具有最大和的子数组。

首先，我们将计算从索引 0 开始的每个子数组的总和。同样，我们将找到从0到n-1的每个索引开始的所有子数组，其中n是数组的长度：

[![蛮力算法](https://www.baeldung.com/wp-content/uploads/2019/12/brute-force-v2.jpg)](https://www.baeldung.com/wp-content/uploads/2019/12/brute-force-v2.jpg)

 

所以我们将从索引0开始，并将每个元素添加到迭代中的运行总和。我们还将跟踪目前看到的最大总和。此迭代显示在上图的左侧。

在图像的右侧，我们可以看到从索引3开始的迭代。在这张图片的最后一部分，我们得到了索引3和6之间的最大和的子数组。

但是，我们的算法将继续查找从0到n-1之间的索引开始的所有子数组。

### 2.2. 执行

现在让我们看看如何用Java实现这个解决方案：

```java
public int maxSubArray(int[] nums) {
 
    int n = nums.length;
    int maximumSubArraySum = Integer.MIN_VALUE;
    int start = 0;
    int end = 0;
 
    for (int left = 0; left < n; left++) {
 
        int runningWindowSum = 0;
 
        for (int right = left; right < n; right++) {
            runningWindowSum += nums[right];
 
            if (runningWindowSum > maximumSubArraySum) {
                maximumSubArraySum = runningWindowSum;
                start = left;
                end = right;
            }
        }
    }
    logger.info("Found Maximum Subarray between {} and {}", start, end);
    return maximumSubArraySum;
}
```

正如预期的那样， 如果当前总和大于先前的最大总和，我们将更新maximumSubArraySum 。值得注意的是，我们随后还更新了 开始 和 结束以找出该子数组的索引位置。

### 2.3. 复杂

一般来说，蛮力解决方案会多次遍历数组以获得所有可能的解决方案。这意味着此解决方案所花费的时间随数组中元素的数量呈二次方增长。对于小尺寸的阵列，这可能不是问题。但随着数组大小的增长，此解决方案效率不高。

通过检查代码，我们还可以看到有两个嵌套的 for 循环。因此，我们可以得出总结，该算法的时间复杂度为O(n 2 )。

在后面的部分中，我们将使用动态规划以O(n)的复杂度解决这个问题。

## 3. 动态规划

动态规划通过将问题分解为更小的子问题来解决问题。这与分治算法求解技术非常相似。然而，主要区别在于动态规划只解决一个子问题一次。

然后它存储这个子问题的结果，然后重用这个结果来解决其他相关的子问题。这个过程被称为 memoization。

### 3.1. 卡丹的算法

Kadane 算法是最大子数组问题的一种流行解决方案，该解决方案基于动态规划。

解决动态规划问题最重要的挑战是找到最优子问题。

### 3.2. 方法

让我们换一种方式来理解这个问题：

[![卡丹算法](https://www.baeldung.com/wp-content/uploads/2019/12/kadane-1.jpg)](https://www.baeldung.com/wp-content/uploads/2019/12/kadane-1.jpg)

在上图中，我们假设最大子数组在最后一个索引位置结束。因此，子数组的最大和为：

```plaintext
maximumSubArraySum = max_so_far + arr[n-1]
```

max_so_far是在索引n-2处结束的子数组的最大总和。这也显示在上图中。

现在，我们可以将这个假设应用于数组中的任何索引。例如，以n-2结尾的最大子数组和可以计算为：

```plaintext
maximumSubArraySum[n-2] = max_so_far[n-3] + arr[n-2]
```

因此，我们可以得出总结：

```plaintext
maximumSubArraySum[i] = maximumSubArraySum[i-1] + arr[i]
```

现在，由于数组中的每个元素都是大小为 1 的特殊子数组，我们还需要检查元素是否大于最大和本身：

```plaintext
maximumSubArraySum[i] = Max(arr[i], maximumSubArraySum[i-1] + arr[i])
```

通过查看这些等式，我们可以看到我们需要在数组的每个索引处找到最大的子数组和。因此，我们将问题划分为n个子问题。我们可以通过仅迭代数组一次来找到每个索引处的最大总和：

[![卡丹算法](https://www.baeldung.com/wp-content/uploads/2019/12/kadane-final.jpg)](https://www.baeldung.com/wp-content/uploads/2019/12/kadane-final.jpg)

突出显示的元素显示迭代中的当前元素。在每个索引处，我们将应用先前导出的方程来计算max_ending_here的值。这有助于我们确定是应该将当前元素包含在子数组中，还是从该索引开始一个新的子数组。

另一个变量max_so_far用于存储迭代期间找到的最大子数组和。一旦我们遍历最后一个索引，max_so_far将存储最大子数组的总和。

### 3.3. 执行

同样，让我们看看现在如何按照上述方法在Java中实现 Kadane 算法：

```java
public int maxSubArraySum(int[] arr) {
 
    int size = arr.length;
    int start = 0;
    int end = 0;
 
    int maxSoFar = arr[0], maxEndingHere = arr[0];
 
    for (int i = 1; i < size; i++) {
        if (arr[i] > maxEndingHere + arr[i]) {
            start = i;
            maxEndingHere = arr[i];
        } else
            maxEndingHere = maxEndingHere + arr[i];
 
        if (maxSoFar < maxEndingHere) {
            maxSoFar = maxEndingHere;
            end = i;
        }
    }
    logger.info("Found Maximum Subarray between {} and {}", Math.min(start, end), end);
    return maxSoFar;
}
```

在这里，我们更新了开始 和 结束 以找到最大的子数组索引。

请注意，我们将Math.min(start, end)而不是 start作为最大子数组的起始索引。这是因为，如果数组只包含负数，则最大子数组本身就是最大的元素。在这种情况下，if (arr[i] > maxEndingHere + arr[i])将始终为true。即start的值大于end的值。

### 3.4. 复杂

由于我们只需要对数组进行一次迭代，因此该算法的时间复杂度为O(n)。

因此，正如我们所见，此解决方案所花费的时间随着数组中元素的数量线性增长。因此，它比我们在上一节中讨论的蛮力方法更有效。

## 4. 总结

在本快速教程中，我们描述了解决最大子数组问题的两种方法。

首先，我们探索了一种蛮力方法，发现这种迭代解决方案导致了二次时间。后来我们又讨论了Kadane算法，用动态规划在线性时间内解决了这个问题。