## 1. 问题描述

给定一个表示BST前序遍历的数组，打印其后序遍历。

示例：

```
输入: 40 30 35 80 100
输出: 35 30 100 80 40

输入: 40 30 32 35 80 90 100 120
输出: 35 32 30 120 100 90 80 40
```

## 2. 算法分析

一个简单的解决方案是首先从给定的前序遍历构造BST。构建树后，对其执行后序遍历。

一种有效的方法是在不构建树的情况下找到后序遍历。其思想是遍历给定的前序数组，并保持当前元素应该位于的范围。
这是为了确保始终满足BST属性。最初，范围设置为{minval = INT_MIN, maxval = INT_MAX}。在前序遍历中，第一个元素总是根，它肯定会在初始范围内。
所以存储前序遍历数组的第一个元素。在后序遍历中，首先打印左子树和右子树，然后打印根节点数据。
因此，首先对左右子树进行递归调用，然后打印根节点的值。对于左子树范围更新为{minval, root.data}，
对于右子树范围更新为{root.data, maxval}。如果当前的前序数组元素不在为其指定的范围内，
则它不属于当前子树，从递归调用返回，直到找不到该元素的正确位置。

## 3. 算法实现

```java
public class PrintPostOrder {

  public void findPostOrderFromPreOrder(int[] pre, int n) {
    INT preIndex = new INT(0);
    findPostOrderFromPreOrderUtil(pre, n, Integer.MIN_VALUE, Integer.MAX_VALUE, preIndex);
  }

  private void findPostOrderFromPreOrderUtil(int[] pre, int n, int minValue, int maxValue, INT preIndex) {
    if (preIndex.data == n)
      return;
    if (pre[preIndex.data] < minValue || pre[preIndex.data] > maxValue)
      return;
    int val = pre[preIndex.data];
    preIndex.data++;
    findPostOrderFromPreOrderUtil(pre, n, minValue, val, preIndex);
    findPostOrderFromPreOrderUtil(pre, n, val, maxValue, preIndex);
    System.out.print(val + " ");
  }

  static class INT {
    int data;

    INT(int data) {
      this.data = data;
    }
  }
}
```

时间复杂度：O(n)，其中是节点数。

辅助空间：O(n)，函数调用栈大小。