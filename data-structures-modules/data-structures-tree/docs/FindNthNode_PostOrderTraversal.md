## 1. 问题描述

给定一棵二叉树和一个数n，编写一个程序在给定二叉树的后序遍历中找到第n个节点。

示例：

```
输入: n = 4
              11
            /   
           21    31
         /   
        41     51
输出: 31
解释: 给定二叉树的后序遍历为 41 51 21 31 11，因此第4个节点将是31。

输入: n = 5
             25
           /    
          20    30
        /     /   
      18    22 24   32
输出: 32
```

## 2. 算法实现

解决这个问题的思路是对给定的二叉树进行后序遍历，并在遍历树时跟踪访问的节点数，当count等于n时打印当前节点。

以下是上述方法的具体实现：

```java
public class FindNthNode {
  Node root;
  static int count = 0;

  public FindNthNode(Node root) {
    this.root = root;
  }

  public void NthPostorder(Node node, int n) {
    if (node == null)
      return;
    if (count < n) {
      NthPostorder(node.left, n);
      NthPostorder(node.right, n);
      count++;
      if (count == n)
        System.out.println(node.key);
    }
  }
}
```

时间复杂度：O(n)，其中n是给定二叉树中的节点数。

辅助空间：O(1)