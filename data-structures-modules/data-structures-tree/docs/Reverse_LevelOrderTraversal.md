## 1. 问题描述

在之前的文章中，我们已经讨论了树的层序遍历。主要思想是先打印第一层，然后打印第二层，依此类推。
像层序遍历一样，每层都是从左到右打印的。

<img src="../assets/Reverse_LevelOrderTraversal.png">

上述树的反向层序遍历为“4 5 2 3 1”。

[二叉树的层序遍历](LevelOrderTraversal_BinaryTree.md)一文中两种用于正常层序遍历的方法都可以很容易地修改为进行反向层序遍历。

## 2. 方法一(用于打印给定层的递归函数)

我们可以很容易地修改[二叉树的层序遍历](LevelOrderTraversal_BinaryTree.md)一文中用于正常层序遍历的方法1。
在方法1中，我们有一个方法printCurrentLevel()，它打印给定的层。
我们唯一需要更改的是，我们不是从第一层调用printCurrentLevel()到最后一层，而是从最后一层调用它到第一层。

以下为上述方法的具体实现：

```java
public class ReverseLevelOrderTraversal {
  Node root;

  public ReverseLevelOrderTraversal(Node root) {
    this.root = root;
  }

  private int height(Node root) {
    if (root == null)
      return 0;
    else {
      int lHeight = height(root.left);
      int rHeight = height(root.right);
      return lHeight > rHeight ? lHeight + 1 : rHeight + 1;
    }
  }

  public void reverseLevelOrder(Node root) {
    int height = height(root);
    int i;
    for (i = height; i >= 1; i--)
      printCurrentLevel(root, i);
  }

  private void printCurrentLevel(Node root, int level) {
    if (level == 0)
      return;
    if (level == 1)
      System.out.print(root.key + " ");
    else {
      if (root.left != null)
        printCurrentLevel(root.left, level - 1);
      if (root.right != null)
        printCurrentLevel(root.right, level - 1);
    }
  }
}
```

此方法的最坏情况时间复杂度为O(n<sup>2</sup>)。对于倾斜树，printCurrentLevel()需要O(n)个时间，其中n是倾斜树中的节点数。
所以printLevelOrder()的时间复杂度是O(n)+O(n-1)+O(n-2)+..+O(1)，为O(n<sup>2</sup>)。

## 3. 使用栈和队列

我们也可以很容易地修改[二叉树的层序遍历](LevelOrderTraversal_BinaryTree.md)一文中用于正常层序遍历的方法2。
其思想是使用deque(双端队列)来获得反向层序顺序。双端队列允许在两端插入和删除。如果我们进行普通层序遍历而不是打印节点，
则将节点压入栈，然后打印双端队列的内容，我们会得到上述示例树的“5 4 3 2 1”，但输出应该是“ 4 5 2 3 1”。
所以为了得到正确的序列(每一层从左到右)，我们以相反的顺序处理节点的子节点，我们首先将右子树push到双端队列，然后处理左子树。

```java
public class ReverseLevelOrderTraversal {
  Node root;

  public ReverseLevelOrderTraversal(Node root) {
    this.root = root;
  }

  public void reverseLevelOrderUsingQueueAndStack(Node root) {
    Stack<Node> S = new Stack<>();
    Queue<Node> Q = new LinkedList<>();
    Q.add(root);
    // 以下是与普通层序遍历的区别
    // 1) 我们不打印节点，而是将节点压入到栈
    // 2) 在左子树之前访问右子树
    while (!Q.isEmpty()) {
      root = Q.peek();
      Q.remove();
      S.push(root);
      // enqueue右子节点
      if (root.right != null)
        // 注意：右子节点在左子节点之前插入
        Q.add(root.right);
      // enqueue左子节点
      if (root.left != null) {
        Q.add(root.left);
      }
    }
    // 逐个弹出栈中的所有节点并打印
    while (!S.empty()) {
      root = S.peek();
      System.out.print(root.key + " ");
      S.pop();
    }
  }
}
```

时间复杂度：O(n)，其中n是二叉树中的节点数。