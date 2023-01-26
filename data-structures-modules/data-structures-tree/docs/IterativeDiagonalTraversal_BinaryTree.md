## 1. 问题描述

考虑节点之间通过的坡度为1的直线。给定一棵二叉树，打印二叉树中属于同一行的所有对角线元素。

```
输入：以下树的根节点
```

<img src="../assets/DiagonalTraversal_BinaryTree.png">

```
输出： 
二叉树的对角遍历：
 8 10 14
 3 6 7 13
 1 4
Observation：root和root.right的值将优先于所有root.left的值。
```

## 2. 算法实现

思想是使用队列仅存储current节点的左子节点。打印current节点的数据后，将当前current设置为其右子节点(如果存在)。

分隔符null用于标记下一条对角线的开始。

以下是上述方法的具体实现：

```java
public class DiagonalTraversal {
  Node root;

  public DiagonalTraversal(Node root) {
    this.root = root;
  }

  public void diagonalPrintUsingIterative(Node root) {
    if (root == null)
      return;
    Queue<Node> queue = new LinkedList<>();
    // 添加根节点
    queue.add(root);
    // 添加分隔符
    queue.add(null);
    while (queue.size() > 0) {
      Node current = queue.peek();
      queue.remove();
      // 如果current是分隔符，则在下一行后面添加新的分隔符，打印空行
      if (current == null) {
        if (queue.size() == 0)
          return;
        // 打印空行
        System.out.println();
        // 再次添加分隔符
        queue.add(null);
      } else {
        while (current != null) {
          System.out.print(current.key + " ");
          // 如果左子节点存在，添加进队列
          if (current.left != null)
            queue.add(current.left);
          // 继续打印同一对角线的下一个节点
          current = current.right;
        }
      }
    }
  }
}
```

以上的方法也可以不使用分隔符，就像层序遍历一样，使用队列。只需要做小小的更改。

```
if(current.left != null) -> 将其添加到队列中
并将current指针移动到current的右侧.

if current = null, 然后从队列中删除一个节点.
```

```java
public class DiagonalTraversal {
  Node root;

  public DiagonalTraversal(Node root) {
    this.root = root;
  }

  public void diagonalPrintUsingIterativeOptimization(Node root) {
    // 如果树为空，直接return
    if (root == null)
      return;
    // 如果root不为空，则将current节点指向根节点
    Node current = root;
    // 创建队列以存储左子节点
    Queue<Node> queue = new LinkedList<>();
    // 循环直到队列为空并且current为null
    while (!queue.isEmpty() || current != null) {
      // 如果current不为null
      //1. 打印current节点的数据
      //2. 如果存在左子节点，则将其添加到队列中
      //3. 移动current指针为右子节点
      if (current != null) {
        System.out.print(current.key + " ");
        if (current.left != null)
          queue.add(current.left);
        current = current.right;
        //如果current为null，则从队列中删除一个节点并将其分配给current节点。
      } else
        current = queue.remove();
    }
  }
}
```