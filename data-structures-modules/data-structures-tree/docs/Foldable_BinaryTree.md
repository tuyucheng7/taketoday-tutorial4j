## 1. 问题描述

给定一棵二叉树，确定这棵树是否可以折叠。如果树的左右子树在结构上互为镜像，则该树可以被认为是可折叠树。一棵空树也认为是可折叠的。

```
考虑以下树：
(a)和(b)可以折叠。
(c)和(d)不可以折叠。

(a)
       10
     /    
    7      15
         /
      9  11

(b)
        10
       /  
      7    15
     /      
    9       11

(c)
        10
       /  
      7   15
     /    /
    5   11

(d)

         10
       /   
      7     15
    /      /
   9   10  12
```

## 2. 算法分析

将左子树更改为其镜像并将其与右子树进行比较。

```
1. 如果该树为空，返回true。
2. 将左侧子树转换为其镜像mirror(root.left)；
3. 检查左子树和右子树的结构是否相同并保存结果。
  res = isStructSame(root.left, root.right); /isStructSame()递归比较两个子树的结构并返回，如果结构相同则为true /
4. 还原在步骤(2)中所做的更改以获取原始树。mirror(root.left)。
5. 返回步骤3中保存的res结果。
```

以下为具体实现：

```java
public class FoldableBinaryTree {
  Node root;

  boolean isFoldable(Node root) {
    if (root == null)
      return true;
    // 将左子树转换为其镜像
    mirror(root.left);
    // 比较右子树和左子树其镜像的结构
    boolean res = isSameStructure(root.left, root.right);
    // 恢复原始左子树
    mirror(root.left);
    return res;
  }

  private boolean isSameStructure(Node a, Node b) {
    if (a == null && b == null)
      return true;
    return a != null && b != null && isSameStructure(a.left, b.left) && isSameStructure(a.right, b.right);
  }

  private void mirror(Node node) {
    if (node == null)
      return;
    else {
      Node temp;
      // 转换子节点
      mirror(node.left);
      mirror(node.right);
      // 交换父节点指向两个子节点的指针
      temp = node.left;
      node.left = node.right;
      node.right = temp;
    }
  }
}
```

时间复杂度：O(n)

## 3. 检查左右子树是否互为镜像

主要有两个函数：

```
// 检查树是否可以折叠
isFoldable(root)
1. if 树为空，则返回true。
2. else 否则检查左右子树彼此是否互为镜像。使用isFoldableUtil(root.left,root.right)
```

```
// 检查n1和n2是否相互镜像。
isFoldableUtil(n1, n2)
1. 如果两个子树都为空，则返回true。
2. 如果其中一个为空而另一个不为空，则返回false。
3. 如果满足以下条件，则返回true
   a) n1.left与n2.right互为镜像
   b) n1.right与n2.left互为镜像
```

以下为具体实现：

```java
public class FoldableBinaryTree {
  Node root;

  public boolean isFoldableOtherMethod(Node root) {
    if (root == null)
      return true;
    return isFoldableOtherMethodUtil(root.left, root.right);
  }

  // 检查一个父节点的两个子节点n1，n2是否互为镜像
  private boolean isFoldableOtherMethodUtil(Node n1, Node n2) {
    // 如果左右子树都为null，返回true
    if (n1 == null && n2 == null)
      return true;
    // 如果其中一个子树为null且另一个子树不为null，返回false
    if (n1 == null || n2 == null)
      return false;
    // 否则两个子树都不为null，继续检查两个子树的子树是否互为镜像。
    return isFoldableOtherMethodUtil(n1.left, n2.right)
        && isFoldableOtherMethodUtil(n1.right, n2.left);
  }
}
```

## 4. 迭代实现

其思想是使用BFS和队列遍历树。

为了证明它是一棵可折叠树，我们需要检查两个条件是否为null。

1. 左子树的左子节点 = 右子树的右子节点，两者都不应该为null。
2. 左子树的右子节点 = 右子树的左子节点，两者都不应该为null。

以下是上述方法的具体实现：

```java
public class FoldableBinaryTree {
  Node root;

  public boolean isFoldableUsingBFS(Node root) {
    Queue<Node> nodes = new LinkedList<>();
    if (root != null) {
      // 如果根节点不为null，初始化添加左右子节点
      nodes.add(root.left);
      nodes.add(root.right);
    }
    while (!nodes.isEmpty()) {
      // 从队列移出两个前两个节点，检查null条件
      Node p = nodes.remove();
      Node r = nodes.remove();
      // 如果两者都为null，继续检查队列中的其他元素。
      if (p == null && r == null)
        continue;
      // 如果左右子树的两个节点有一个为null而另一个不为null，返回false
      if ((p == null && r != null) || (p != null && r == null))
        return false;
      /
          按顺序添加新的左右子树的子节点到队列：
          1. 左子树的左子节点
          2. 右子树的右子节点
          3. 左子树的右子节点
          4. 右子树的左子节点
       /
      nodes.add(p.left);
      nodes.add(r.right);
      nodes.add(p.right);
      nodes.add(r.left);
    }
    // 最后，当完成所有检查，返回true
    return true;
  }
}
```