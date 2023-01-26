## 1. 算法分析

使用Morris遍历，我们可以在不使用栈和递归的情况下遍历树。Morris遍历的思想是基于线索二叉树的。
在这个遍历过程中，我们首先创建指向中序后继树的链接并使用这些链接打印数据，最后还原更改以恢复原始树。

```
1. 初始化current为根节点
2. while current不为null时
   if current没有左子节点
      a) 打印current的数据
      b) 向右继续，即current = current.right
   else
      a) 在current左子树中查找最右边的节点或节点的右子节点 == current。
         if 我们找到了某个节点的右子节点 == current
            a) 将右子节点为current节点的右子节点的更新为null
            b) 打印current的数据
            c) 向右继续，即current = current.right
         else
            a) 将current作为我们找到的最右边节点的右子节点；and
            b) 转到左子节点，即current = current.left
```

尽管通过遍历修改了树，但完成后树会恢复到其原始形状。与基于栈的遍历不同，此遍历方式不需要额外的空间。

## 2. 算法实现

```java
public class InOrderTraversal {
  Node root;

  void morrisTraversal(Node root) {
    Node current, previous;
    if (root == null)
      return;
    current = root;
    while (current != null) {
      if (current.left == null) {
        System.out.print(current.key + " ");
        current = current.right;
      } else {
        // 找到current的中序前驱节点
        previous = current.left;
        while (previous.right != null && previous.right != current)
          previous = previous.right;
        // 将current作为其中序前驱节点的右子节点
        if (previous.right != current) {
          previous.right = current;
          current = current.left;
        } else { // 恢复“if”部分所做的更改以恢复原始树，即恢复中序前驱节点的右子节点
          previous.right = null;
          System.out.print(current.key + " ");
          current = current.right;
        } // if条件结束 previous.right == null
      } // if条件结束 current.left == null
    }
  }
}
```

时间复杂度：O(n)。如果我们仔细观察，我们可以注意到树的每一条边最多遍历三次。在最坏的情况下，将创建和删除与输入树相同数量的额外边。