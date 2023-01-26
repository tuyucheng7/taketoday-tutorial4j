## 1. 算法分析

使用Morris遍历，我们可以在不使用栈和递归的情况下遍历树。前序的算法与[中序的Morris遍历](InOrderTreeTraversal_WithoutStackRecursion.md)算法几乎相似。

```
1. if 左子节点为空，则打印current节点数据。移动到右子节点。
   else 使中序前驱的右子节点指向current节点。出现两种情况：
      a) 中序前驱的右子节点已经指向current节点。将右子节点设置为null。移动到current节点的右子节点。
      b) 右子节点为null。将其设置为current节点。打印current节点的数据并移动到current节点的左子节点。
2. 迭代直到current节点不为null。
```

## 2. 算法实现

下面是上述算法的实现：

```java
public class PreOrderTraversal {
  Node root;

  public PreOrderTraversal(Node root) {
    this.root = root;
  }

  public void morrisTraversalPreorder() {
    morrisTraversalPreorder(root);
  }

  private void morrisTraversalPreorder(Node root) {
    while (root != null) {
      // 如果左子节点为null，打印当前节点数据，并移动到右子节点
      if (root.left == null) {
        System.out.print(root.key + " ");
        root = root.right;
      } else {
        // 找到中序前驱
        Node current = root.left;
        while (current.right != null && current.right != root)
          current = current.right;
        // 如果中序前驱的右子节点已经指向root
        if (current.right == root) {
          current.right = null;
          root = root.right;
          // 如果中序前驱的右子节点不指向root，则打印该节点并使右子节点指向root
        } else {
          System.out.print(root.key + " ");
          current.right = root;
          root = root.left;
        }
      }
    }
  }
}
```

Morris遍历在此过程中修改了树。它在向下移动树时建立右链接，在向上移动树时重置右链接。因此，如果不允许写操作，则无法应用该算法。