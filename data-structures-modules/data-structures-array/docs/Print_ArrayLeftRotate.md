## 1. 问题描述

给定一个大小为n的数组和多个值，我们需要将该数组向左旋转。如何快速打印多个左旋后的数组？

示例：

```
输入: arr[] = {1, 3, 5, 7, 9}
k1 = 1
k2 = 3
k3 = 4
k4 = 6
输出: 
3 5 7 9 1
7 9 1 3 5
9 1 3 5 7
3 5 7 9 1

输入: arr[] = {1, 3, 5, 7, 9}
k1 = 14 
输出: 
9 1 3 5 7
```

## 2. 算法实现

在[快速查找数组的多个左旋转](FindMultiple_LeftRotation.md)一文中讨论了一种解决方案。

上面讨论的解决方案需要额外的空间。不过我们同样介绍了一种不需要额外空间的优化方案。因此这里就不在赘述

在下面的实现中，我们将使用标准Java API，这将使解决方案更加优化和易于实现。

```java
public class FindMultipleLeftRotation {

  public static void leftRotateUsingApi(int[] arr, int n, int k) {
    Collections.rotate(Arrays.asList(arr), n - k);
    for (int i = 0; i < n; i++)
      System.out.print(arr[i] + " ");
  }
}
```

注意：数组本身在旋转后会更新。

时间复杂度：O(n)

辅助空间：O(1)