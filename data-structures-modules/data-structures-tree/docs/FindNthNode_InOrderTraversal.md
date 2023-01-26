## 1. 问题描述

给定二叉树，找出按中序遍历的第n个节点。

示例：

```
输入: n = 4
              10
            /   
           20     30
         /   
        40     50
输出: 10
中序遍历是: 40 20 50 10 30

输入: n = 3
            7
          /   
         2     3
             /   
            8     5
输出: 8
中序遍历是: 2 7 8 3 5
第3个节点是8
```

## 2. 算法实现

我们进行简单的中序遍历。在进行遍历时，我们跟踪到目前为止访问的节点数。当count变为n时，我们打印节点数据。

以下是上述方法的具体实现：

```java
public class FindNthInorder {
  Node root;
  static int count = 0;

  public FindNthInorder(Node root) {
    this.root = root;
  }

  public void NthInorder(Node node, int n) {
    if (node == null)
      return;
    if (count <= n) {
      NthInorder(node.left, n);
      count++;
      if (count == n)
        System.out.println(node.key);
      NthInorder(node.right, n);
    }
  }
}
```

时间复杂度：O(n)