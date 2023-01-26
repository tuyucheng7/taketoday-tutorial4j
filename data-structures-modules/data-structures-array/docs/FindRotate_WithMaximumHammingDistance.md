## 1. 问题描述

给定一个由n个元素组成的数组，创建一个新数组，该数组是由给定数组旋转之后形成的，两个数组之间的汉明距离最大。
两个长度相等的数组或字符串之间的汉明距离是对应字符(元素)不同的位置数。

注意：给定输入可以有多个输出。

示例：

```
输入:  1 4 1
输出:  2
解释:  最大汉明距离 = 2。我们用 4 1 1 或 1 1 4 得到这个汉明距离

输入:  N = 4
      2 4 8 0
输出:  4
解释: 最大汉明距离 = 4。我们用 4 8 0 2 得到这个汉明距离。所有位置都可以被另一个数字替换。其他可能的解决方案是 8 0 2 4 和 0 2 4 8。
```

## 2. 算法实现

创建另一个数组，它的大小是原始数组的两倍，这样这个新数组(副本数组)的元素就是原始数组中以相同顺序重复两次的元素。
例如，如果原始数组是{1, 4, 1}，那么副本数组是{1, 4, 1, 1, 4, 1}。

现在，遍历副本数组，并在每次移位(或旋转)时找到汉明距离。所以我们检查{4, 1, 1}，{1, 1, 4}，{1, 4, 1}，选择汉明距离最大的输出。

以下是上述方法的具体实现：

```java
public class MaximumHammingDistance {

  public static int maxHamming(int[] arr, int n) {
    int[] temp = new int[2  n];
    for (int i = 0; i < n; i++)
      temp[i] = temp[n + i] = arr[i];
    int maxHamming = 0;
    for (int i = 1; i < n; i++) {
      int currentHamming = 0;
      for (int j = i, k = 0; j < (i + n); j++, k++)
        if (temp[j] != arr[k])
          currentHamming++;
      if (currentHamming == n)
        return n;
      maxHamming = Math.max(maxHamming, currentHamming);
    }
    return maxHamming;
  }
}
```

时间复杂度：O(n<sup>2</sup>)，其中n是给定数组的大小。

辅助空间：O(n)

## 3. 常数空间

我们将原始数组序列的元素与其旋转数组进行比较。旋转的数组是使用移位索引方法实现的，你可以将原始索引处的元素与移位索引处的元素进行比较，而不需要任何额外的空间。

以下是上述方法的具体实现：

```java
public class MaximumHammingDistance {

  public static int maxHammingWithConstantSpace(int[] arr, int n) {
    int maxHamming = 0;
    for (int j = 1; j < n; j++) {
      maxHamming = 0;
      for (int i = 0; i < n; i++) {
        if (arr[i] != arr[(i + j) % n])
          maxHamming++;
      }
      if (maxHamming == n)
        return maxHamming;
    }
    return maxHamming;
  }
}
```

时间复杂度：O(n<sup>2</sup>)，其中n是给定数组的大小。

辅助空间：O(1)