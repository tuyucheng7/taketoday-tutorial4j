## 1. 问题描述

给定一棵二叉树，编写一个迭代函数来打印给定二叉树的前序遍历。

要将固有的递归过程转换为迭代过程，我们需要一个显式栈。下面是一个简单的基于栈的迭代过程，用于打印前序遍历。

## 2. 算法分析

```
1. 创建一个空栈stack并将根节点push到栈中。
2. 在stack不为空时执行以下操作。
   a) 从栈中弹出一个节点并打印它。
   b) 将弹出节点的右子节点push到栈中。
   C) 将弹出节点的左子节点push到栈中。
```

先push右子节点，以确保首先处理左子节点。因为栈是后进先出的。

## 3. 算法实现

```java
public class PreOrderTraversal {
  Node root;

  public PreOrderTraversal(Node root) {
    this.root = root;
  }

  public void preOrderUsingStack(Node root) {
    if (root == null)
      return;
    // 创建一个空栈stack并将根节点push到栈中。
    Stack<Node> stack = new Stack<>();
    stack.add(root);
    // 循环直到栈为空
    while (!stack.isEmpty()) {
      // 弹出一个节点，并打印
      Node temp = stack.peek();
      System.out.print(temp.key + " ");
      stack.pop();
      // 如果弹出节点的左右子节点不为null，则将右，左子节点顺序push到栈中
      if (temp.right != null)
        stack.push(temp.right);
      if (temp.left != null)
        stack.push(temp.left);
    }
  }
}
```

时间复杂度：O(n)

辅助空间：O(h)，其中h是树的高度。

在上面的解决方案中，我们可以看到左子节点一push到栈中就会弹出，因此不需要将其push到栈中。

主要思想是从根节点开始遍历树，并在左子节点不为null时继续打印左子节点，同时将每个节点的右子节点push到辅助栈中。
一旦我们到达一个空节点，从辅助栈中弹出一个右子节点并在辅助栈不为空时重复该过程。

这是对之前方法的微观优化，两种解决方案都使用渐近相似的辅助空间。

以下是上述方法的具体实现：

```java
public class PreOrderTraversal {
  Node root;

  public PreOrderTraversal(Node root) {
    this.root = root;
  }

  public void preOrderUsingStackOptimization(Node root) {
    if (root == null)
      return;
    Stack<Node> stack = new Stack<>();
    stack.add(root);
    // 从根节点开始(设置current节点为根节点)
    Node current = root;
    // 循环直到栈为空并且current为null
    while (current != null || !stack.isEmpty()) {
      // current不为null时打印左子节点并将右子节点push到栈中。
      while (current != null) {
        System.out.print(current.key + " ");
        if (current.right != null) {
          stack.push(current.right);
        }
        current = current.left;
      }
      // 当current为null时我们执行该if语句，从栈中取出一个右子节点。
      if (!stack.isEmpty())
        current = stack.pop();
    }
  }
}
```

时间复杂度：O(n)

辅助空间：O(h)，其中h是树的高度。