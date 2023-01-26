## 1. 问题描述

编写一个函数rotate(arr[], d, n)，将大小为n的数组arr[]旋转d个元素。示例：

```
输入: arr[] = [1, 2, 3, 4, 5, 6, 7]  d = 2
输出: arr[] = [3, 4, 5, 6, 7, 1, 2]
```

设arr[]包含元素为：

<img src="../assets/Reversal_Algorithm_For_ArrayRotation-1.png">

例如，如果用户提供一个输入数组，其中数组应旋转2个位置，则生成的数组为：

<img src="../assets/Reversal_Algorithm_For_ArrayRotation-2.png">

## 2. 算法思想

```
初始化A = arr[0...d-1], B = arr[d...n-1]

1) 执行以下操作，直到A的大小等于B的大小
   a) 如果A更短，则将B分为Bl和Br，使得Br与A长度相同。交换A和Br以将ABlBr更改为BrBlA。现在A在它的最终位置，所以在B的剩余数组片段上重复。
   b) 如果A更长，则将A分成Al和Ar，使得Al与B长度相同。交换Al和B以将AlArB更改为BArAl。现在B在它的最终位置，所以在A的剩余数组片段上重复。
  
2) 最后，当A和B的大小相等时，将它们进行块交换。
```

## 3. 递归实现

```java
public class ArrayRotation {

  public static void rotationUsingBlockSwap(int[] arr, int d, int n) {
    rotationUsingBlockSwapRecursive(arr, 0, d, n);
  }

  public static void rotationUsingBlockSwapRecursive(int[] arr, int i, int d, int n) {
    // 如果要旋转的元素数为零或等于数组大小, return
    if (d == 0 || d == n)
      return;
    // 如果要旋转的元素数正好是数组大小的一半
    if (n - d == d) {
      swap(arr, i, n - d + i, d);
      return;
    }

    // 如果A数组更短
    if (d < n - d) {
      swap(arr, i, n - d + i, d);
      rotationUsingBlockSwapRecursive(arr, i, d, n - d);
    } else { // 如果B数组更短
      swap(arr, i, d, n - d);
      rotationUsingBlockSwapRecursive(arr, n - d + i, 2  d - n, d);
    }
  }

  private static void swap(int[] arr, int fi, int si, int d) {
    int i, temp;
    for (i = 0; i < d; i++) {
      temp = arr[fi + i];
      arr[fi + i] = arr[si + i];
      arr[si + i] = temp;
    }
  }
}
```

## 4. 迭代实现

```java
public class ArrayRotation {

  public static void rotationUsingBlockSwapIterative(int[] arr, int d, int n) {
    int i, j;
    if (d == 0 || d == n)
      return;
    // 如果要旋转的元素数大于数组大小
    if (d > n)
      d %= n;
    i = d;
    j = n - d;
    while (i != j) {
      if (i < j) { // 如果A数组更短
        swap(arr, d - i, d + j - i, i);
        j -= i;
      } else { // 如果B数组更短
        swap(arr, d - i, d, j);
        i -= j;
      }
    }
    swap(arr, d - i, d, i); // 最后，块交换A和B
  }

  private static void swap(int[] arr, int fi, int si, int d) {
    int i, temp;
    for (i = 0; i < d; i++) {
      temp = arr[fi + i];
      arr[fi + i] = arr[si + i];
      arr[si + i] = temp;
    }
  }
}
```

## 5. 复杂度分析

时间复杂度：O(n)

辅助空间：O(1)