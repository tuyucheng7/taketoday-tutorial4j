## 1. 问题描述

给定一个二叉树，确定它是否是自身的镜像。

```
例如，此二叉树是对称的：
     1
   /   
  2     2
 /    / 
3   4 4   3

但下面的不是:
    1
   / 
  2   2
      
   3    3
```

## 2. 算法分析

其思想是编写一个递归函数isMirror()，该函数将两棵树作为参数，如果树是镜像，则返回true；
如果树不是镜像，则返回false。函数的作用是递归检查根节点下的两个根节点和子树。

## 3. 算法实现

下面是上述算法的实现。

```java
public class SymmetricTree {
  Node root;

  // 检查树是否是自身的镜像
  public boolean isSymmetirc() {
    return isMirror(root, root);
  }

  private boolean isMirror(Node node1, Node node2) {
    // 如果根节点为node1和node2的树互为镜像，则返回true
    if (node1 == null && node2 == null)
      return true;
    /
       对于两棵树互为镜像，必须满足以下3个条件
       1. 它们的根节点的key必须相同
       2. 第一棵树的左子树必须与第二棵树的右子树互为镜像，即第一棵树的父节点的左子节点的key必须等于第二棵树的父节点的右子节点的key
       3. 第一棵树的右子树必须与第二棵树的左子树互为镜像，即第一棵树的父节点的右子节点的key必须等于第二棵树的父节点的左子节点的key
     /
    if (node1 != null && node2 != null && node1.key == node2.key)
      return isMirror(node1.left, node2.right) && isMirror(node1.right, node2.left);
    // 如果上述条件任何一个不成立，返回false
    return false;
  }
}
```

时间复杂度：O(n)

辅助空间：O(h)，其中h是树的最大高度。