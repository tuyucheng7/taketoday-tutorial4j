## 1. 概述

反转二叉树是我们在技术面试中可能被要求解决的问题之一。

在本快速教程中，我们将看到解决此问题的几种不同方法。

## 2. 二叉树

二叉树是一种数据结构，其中每个元素最多有两个孩子，称为左孩子和右孩子。树的顶部元素是根节点，而子节点是内部节点。

但是，如果一个节点没有子节点，则称为叶节点。

话虽如此，让我们创建代表节点的对象：

```java
public class TreeNode {

    private int value;
    private TreeNode rightChild;
    private TreeNode leftChild;

    // Getters and setters

}
```

然后，让我们创建我们将在示例中使用的树：

```java
    TreeNode leaf1 = new TreeNode(1);
    TreeNode leaf2 = new TreeNode(3);
    TreeNode leaf3 = new TreeNode(6);
    TreeNode leaf4 = new TreeNode(9);

    TreeNode nodeRight = new TreeNode(7, leaf3, leaf4);
    TreeNode nodeLeft = new TreeNode(2, leaf1, leaf2);

    TreeNode root = new TreeNode(4, nodeLeft, nodeRight);

```

在前面的方法中，我们创建了以下结构：

[![树](https://www.baeldung.com/wp-content/uploads/2019/04/tree.png)](https://www.baeldung.com/wp-content/uploads/2019/04/tree.png)

通过从左到右反转树，我们最终将拥有以下结构：

[![树2](https://www.baeldung.com/wp-content/uploads/2019/04/tree2-1.png)](https://www.baeldung.com/wp-content/uploads/2019/04/tree2-1.png)

## 3. 反转二叉树

### 3.1. 递归方法

在第一个示例中，我们将使用递归来反转树。

首先，我们将使用树的根调用我们的方法，然后分别将其应用于左右子节点，直到到达树的叶子：

```java
public void reverseRecursive(TreeNode treeNode) {
    if(treeNode == null) {
        return;
    }

    TreeNode temp = treeNode.getLeftChild();
    treeNode.setLeftChild(treeNode.getRightChild());
    treeNode.setRightChild(temp);

    reverseRecursive(treeNode.getLeftChild());
    reverseRecursive(treeNode.getRightChild());
}
```

### 3.2. 迭代法

在第二个示例中，我们将使用迭代方法反转树。为此，我们将使用LinkedList，我们用树的根对其进行初始化。

然后，对于我们从列表中轮询的每个节点，我们在排列它们之前将其子节点添加到该列表中。

我们一直在LinkedList中添加和删除，直到我们到达树的叶子：

```java
public void reverseIterative(TreeNode treeNode) {
    List<TreeNode> queue = new LinkedList<>();

    if(treeNode != null) {
        queue.add(treeNode);
    }

    while(!queue.isEmpty()) {
        TreeNode node = queue.poll();
        if(node.getLeftChild() != null){
            queue.add(node.getLeftChild());
        }
        if(node.getRightChild() != null){
            queue.add(node.getRightChild());
        }

        TreeNode temp = node.getLeftChild();
        node.setLeftChild(node.getRightChild());
        node.setRightChild(temp);
    }
}
```

## 4. 总结

在这篇简短的文章中，我们探讨了反转二叉树的两种方法。我们已经开始使用递归方法来反转它。然后，我们最终使用迭代的方式来获得相同的结果。