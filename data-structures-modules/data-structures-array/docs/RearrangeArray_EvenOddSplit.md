## 1. 问题描述

给定n个元素的数组。我们的任务是编写一个程序来重新排列数组，使偶数位置的元素大于它之前的所有元素，奇数位置的元素小于它之前的所有元素。

示例：

```
输入: arr[] = {1, 2, 3, 4, 5, 6, 7}
输出: {4, 5, 3, 6, 2, 7, 1}

输入: arr[] = {1, 2, 1, 4, 5, 6, 8, 8} 
输出: {4, 5, 2, 6, 1, 8, 1, 8}
```

## 2. 算法实现

解决这个问题的想法是首先创建一个原始数组的额外副本，并对的数组进行排序。现在，具有n个元素的数组中偶数位置的总数将为floor(n/2)，
其余为奇数位置。现在使用排序数组以以下方式填充原始数组中的奇数和偶数位置：

1. 奇数位置总数为n – floor(n/2)。从排序数组中的第(n-floor(n/2))个位置开始，并将元素到排序数组的第一个位置。开始从这个位置向左遍历排序后的数组，并继续向右填充原始数组中的奇数位置。
2. 从第(n-floor(n/2)+1)个位置开始向右遍历已排序的数组，并从第2个位置继续填充原始数组。

下面是上述想法的具体实现：

```java
public class EvenOddSplit {

  public static void rearrangeArr(int[] arr, int n) {
    int evenPosition = n / 2;
    int oddPotision = n - evenPosition;
    int[] temp = new int[n];
    for (int i = 0; i < n; i++)
      temp[i] = arr[i];
    Arrays.sort(temp);
    int j = oddPotision - 1;
    for (int i = 0; i < n; i += 2) {
      arr[i] = temp[j];
      j--;
    }
    j = oddPotision;
    for (int i = 1; i < n; i += 2) {
      arr[i] = temp[j];
      j++;
    }
  }
}
```

时间复杂度：O(nlogn)

辅助空间：O(n)

## 3. 其他方法

我们可以通过定义两个变量p和q来遍历数组并从最后一个赋值。

如果是偶数索引，我们将给它最大值，否则为最小值。

p =0 and q= end;

p将继续，q将减小。

```java
public class EvenOddSplit {

  public static void rearrangeArrOptimization(int[] arr, int n) {
    int p = 0;
    int q = n - 1;
    int[] temp = new int[n];
    System.arraycopy(arr, 0, temp, 0, n);
    Arrays.sort(temp);
    for (int i = n - 1; i >= 0; i--) {
      if (i % 2 != 0) {
        arr[i] = temp[q];
        q--;
      } else {
        arr[i] = temp[p];
        p++;
      }
    }
  }
}
```

此算法比前一个算法少1个循环。