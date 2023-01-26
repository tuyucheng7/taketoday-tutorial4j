## 1. 问题描述

给定的数组并将其从指定位置拆分，将数组的第一部分添加到末尾。

<img src="../assets/SplitArray_AddFirstPartToEnd.png">

示例：

```
输入: arr[] = {12, 10, 5, 6, 52, 36}
     k = 2
输出: arr[] = {5, 6, 52, 36, 12, 10}
解释: 从索引2拆分，第一部分{12, 10}添加到末尾.

输入: arr[] = {3, 1, 2}
     k = 1
输出: arr[] = {1, 2, 3}
解释: 从索引1拆分，第一部分添加到末尾。
```

## 2. 简单方法

我们只需将数组左旋k次即可。

```java
public class SplitArray {

  public static void splitArray(int[] arr, int n, int k) {
    for (int i = 0; i < k; i++) {
      int temp = arr[0];
      for (int j = 0; j < n - 1; j++)
        arr[j] = arr[j + 1];
      arr[n - 1] = temp;
    }
  }
}
```

上述解的时间复杂度为O(nk)。

## 3. 临时数组

另一种方法是创建一个大小为原始数组两位的临时数组，并将数组元素按顺序两次到新数组。然后将元素从新数组到原始数组中，
方法是将k作为起始索引，直到n个元素。

以下是上述方法的具体实现：

```java
public class SplitArray {

  public static void splitArrayUsingTempArray(int[] arr, int n, int k) {
    int[] temp = new int[2  n];
    for (int i = 0; i < n; i++)
      temp[i] = temp[i + n] = arr[i];
    for (int i = 0; i < n; i++)
      arr[i] = temp[(k + i) % n];
  }
}
```

这个问题只是数组旋转问题，有关更高效的方法，可以查看之前文章的实现。