## 1. 问题描述

给定一个二叉树，以一种方式打印层序遍历，即所有层的节点都打印在单独的行中。

例如，考虑以下树

<img src="../assets/LevelOrderTraversal_LineByLine.png">

```
上述树的输出应该是:
20
8 22
4 12
10 14


          1
       /     
      2       3
    /          
   4     5       6
        /       /
       7    8   9
上述树的输出应该是:
1
2 3
4 5 6
7 8 9<
```

## 2. 算法分析

请注意，这与我们需要将所有节点一起打印的简单层序遍历不同。这里我们需要在不同的行中打印不同层的节点。

一个简单的解决方案是使用[二叉树的层序遍历](LevelOrderTraversal_BinaryTree.md)中讨论的递归函数进行打印，
并在每次调用printGivenLevel()后打印一个新行。

```java
public class LevelOrderLineByLine {
  Node root;

  public LevelOrderLineByLine(Node root) {
    this.root = root;
  }

  public void printLevelOrder(Node root) {
    int h = height(root);
    for (int i = 1; i <= h; i++) {
      printGivenLevel(root, i);
      System.out.println();
    }
  }

  private void printGivenLevel(Node root, int level) {
    if (level == 0)
      return;
    if (level == 1)
      System.out.print(root.key + " ");
    else if (level > 1) {
      if (root.left != null)
        printGivenLevel(root.left, level - 1);
      if (root.right != null)
        printGivenLevel(root.right, level - 1);
    }
  }

  public int height(Node root) {
    if (root == null)
      return 0;
    else {
      int lHeight = height(root.left);
      int rHeight = height(root.right);
      return lHeight > rHeight ? lHeight + 1 : rHeight + 1;
    }
  }
}
```

上述解的时间复杂度为O(n<sup>2</sup>)

如何将迭代层序遍历(本方法的方法2)修改为逐行层级？

我们计算当前层级的节点。对于每个节点，我们将其子节点enqueue队列。

```java
public class LevelOrderLineByLine {
  Node root;

  public LevelOrderLineByLine(Node root) {
    this.root = root;
  }

  public void printLevelOrderUsingQueue(Node root) {
    if (root == null)
      return;
    Queue<Node> nodes = new LinkedList<>();
    nodes.add(root);
    while (true) {
      int nodesCount = nodes.size();
      if (nodesCount == 0)
        break;
      while (nodesCount > 0) {
        Node temp = nodes.peek();
        nodes.remove();
        System.out.print(temp.key + " ");
        if (temp.left != null)
          nodes.add(temp.left);
        if (temp.right != null)
          nodes.add(temp.right);
        nodesCount--;
      }
      System.out.println();
    }
  }
}
```

该方法的时间复杂度为O(n)，其中n是给定二叉树中的节点数。

## 3. 使用两个队列

这里讨论使用两个队列的不同方法。我们可以在第一个队列中插入第一层的节点并打印它，并在从第一个队列弹出时将其左右节点插入到第二个队列中。
现在开始打印第二个队列，并在弹出之前将其左右子节点插入第一个队列。继续这个过程，直到两个队列都变空。

以下为该方法的具体实现：

```java
public class LevelOrderLineByLine {
  Node root;

  public LevelOrderLineByLine(Node root) {
    this.root = root;
  }

  public void printLevelOrderUsingTwoQueue(Node root) {
    if (root == null)
      return;
    Queue<Node> q1 = new LinkedList<>();
    Queue<Node> q2 = new LinkedList<>();
    q1.add(root);
    while (!q1.isEmpty() || !q2.isEmpty()) {
      while (!q1.isEmpty()) {
        if (q1.peek().left != null)
          q2.add(q1.peek().left);
        if (q1.peek().right != null)
          q2.add(q1.peek().right);
        System.out.print(q1.peek().key + " ");
        q1.remove();
      }
      System.out.println();
      while (!q2.isEmpty()) {
        if (q2.peek().left != null)
          q1.add(q2.peek().left);
        if (q2.peek().right != null)
          q1.add(q2.peek().right);
        System.out.print(q2.peek().key + " ");
        q2.remove();
      }
      System.out.println();
    }
  }
}
```

时间复杂度：O(n)

## 4. 其他方法

这里介绍使用一个队列的不同方法。首先在队列中插入根节点和null元素。此null元素用作分隔符。
接下来，从队列顶部弹出，并将其左右子节点添加到队列末端，然后在队列顶部打印。继续此过程，直到队列变为空。

```java
public class LevelOrderLineByLine {
  Node root;

  public LevelOrderLineByLine(Node root) {
    this.root = root;
  }

  public void printLevelOrderOtherMethod(Node root) {
    if (root == null)
      return;
    Queue<Node> nodes = new LinkedList<>();
    // 将根节点push到队列中
    nodes.add(root);
    // 将null push到队列中，用作标识符，当从队列取出元素为null，表示应该打印下一层的节点
    nodes.add(null);
    while (!nodes.isEmpty()) {
      Node current = nodes.poll();
      // 如果取出的元素不为null，将该节点的左右节点push到队列中，并打印当前节点数据
      if (current != null) {
        if (current.left != null)
          nodes.add(current.left);
        if (current.right != null)
          nodes.add(current.right);
        System.out.print(current.key + " ");
      } else {
        // 如果取出的元素的为null，表示应该打印下一层的节点，因此继续将null添加到下一层节点后面，并打印空行。
        if (!nodes.isEmpty()) {
          nodes.add(null);
          System.out.println();
        }
      }
    }
  }
}
```

时间复杂度：O(n)