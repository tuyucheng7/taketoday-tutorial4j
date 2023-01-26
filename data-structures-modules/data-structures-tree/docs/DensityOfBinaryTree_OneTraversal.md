## 1. 问题描述

给定一棵二叉树，通过一次遍历来找到它的密度。

```
树的密度 = 大小(Size) / 高度(Height)
```

示例：

```
输入: 以下树的根节点
   10
  /   
 20   30

输出: 1.5
给定树的高度 = 2
给定树的大小 = 3

输入: 以下树的根节点
     10
    /   
  20   
 /
30
输出: 1
给定树的高度 = 3
给定树的大小 = 3
```

二叉树的密度表示二叉树的平衡程度。例如，倾斜树的密度最小，而完全树的密度最大。

## 2. 算法实现

基于两次遍历的方法非常简单。首先使用一次遍历查找高度，然后使用另一次遍历查找大小。最后返回两个值的比率。

为了在一次遍历中完成，我们计算二叉树的大小，同时找到它的高度。下面是Java实现。

```java
public class BinaryTreeDensity {
  Node root;

  public BinaryTreeDensity(Node root) {
    this.root = root;
  }

  public float density(Node root) {
    Size size = new Size();
    if (root == null)
      return 0;
    int height = heightAndSize(root, size);
    return (float) size.size / height;
  }

  // 计算树的高度以及大小
  private int heightAndSize(Node node, Size size) {
    if (node == null)
      return 0;
    // 计算左右子树的高度
    int l = heightAndSize(node.left, size);
    int r = heightAndSize(node.right, size);
    // 每计算一棵树(子树)的高度，自增size
    size.size++;
    return l > r ? l + 1 : r + 1;
  }

  // 用于表示树的大小
  static class Size {
    int size = 0;
  }
}
```