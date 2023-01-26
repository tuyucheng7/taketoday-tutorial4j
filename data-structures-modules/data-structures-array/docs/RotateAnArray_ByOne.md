## 1. 问题描述

给定一个数组，将数组顺时针旋转1次。

示例：

```
输入: arr[] = {1, 2, 3, 4, 5}
输出: arr[] = {5, 1, 2, 3, 4}
```

## 2. 算法实现

以下是算法的实现步骤：

1. 将最后一个元素存储在变量x中。
2. 将所有元素向后移动一个位置。
3. 将数组的第一个元素替换为x。

```java
public class ArrayRotation {

  public static void rotateArrayByOne(int[] arr) {
    int n = arr.length;
    int x = arr[n - 1];
    for (int i = n - 1; i > 0; i--)
      arr[i] = arr[i - 1];
    arr[0] = x;
  }
}
```

## 3. 复杂度分析

时间复杂度：O(n)，因为我们需要遍历所有元素。

辅助空间：O(1)

## 4. 双指针

我们可以使用两个指针，比如i和j，它们分别指向数组的第一个和最后一个元素。
开始交换arr[i]和arr[j]并保持j固定，i向j移动。一直重复，直到i等于j。

```java
public class ArrayRotation {

  public static void rotateArrayByOneUsingTwoPointer(int[] arr) {
    int n = arr.length;
    int i = 0;
    int j = n - 1;
    while (i != j) {
      int temp = arr[i];
      arr[i] = arr[j];
      arr[j] = temp;
      i++;
    }
  }
}
```