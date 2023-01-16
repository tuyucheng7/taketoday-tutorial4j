## 1. 概述

树是计算机科学中最重要的数据结构之一。我们通常对平衡树感兴趣，因为它具有有价值的特性。它们的结构允许在对数时间内执行查询、插入、删除等操作。

在本教程中，我们将学习如何确定二叉树是否平衡。

## 2.定义

首先，让我们介绍一些定义，以确保我们在同一页面上：

-   [二叉树](https://www.baeldung.com/java-binary-tree)——一种每个节点都有零个、一个或两个子节点的树
-   一棵树的高度——从根到叶子的最大距离(与最深叶子的深度相同)
-   平衡树——一种树，其中对于每个子树，从根到任何叶子的最大距离最多比从根到任何叶子的最小距离大一个

我们可以在下面找到平衡二叉树的示例。三个绿色边缘是如何[确定高度](https://www.baeldung.com/cs/height-balanced-tree)的简单可视化，而数字表示级别。

[![二叉树](https://www.baeldung.com/wp-content/uploads/2019/11/Zrzut-ekranu-2019-10-31-o-15.31.40.png)](https://www.baeldung.com/wp-content/uploads/2019/11/Zrzut-ekranu-2019-10-31-o-15.31.40.png)

## 3.领域对象

那么，让我们从树的类开始：

```java
public class Tree {
    private int value;
    private Tree left;
    private Tree right;

    public Tree(int value, Tree left, Tree right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }
}

```

为了简单起见，假设每个节点都有一个整数值。请注意，如果左树和右树为空， 则表示我们的节点是叶子。

在我们介绍我们的主要方法之前，让我们看看它应该返回什么：

```java
private class Result {
    private boolean isBalanced;
    private int height;

    private Result(boolean isBalanced, int height) {
        this.isBalanced = isBalanced;
        this.height = height;
    }
}
```

因此，对于每次调用，我们都会获得有关高度和平衡的信息。

## 4.算法

有了平衡树的定义，我们就可以想出一个算法。我们需要做的是检查每个节点所需的属性。它可以通过递归深度优先搜索遍历轻松实现。

现在，将为每个节点调用我们的递归方法。此外，它将跟踪当前深度。每次调用都会返回有关身高和平衡的信息。

现在，让我们来看看我们的深度优先方法：

```java
private Result isBalancedRecursive(Tree tree, int depth) {
    if (tree == null) {
        return new Result(true, -1);
    }

    Result leftSubtreeResult = isBalancedRecursive(tree.left(), depth + 1);
    Result rightSubtreeResult = isBalancedRecursive(tree.right(), depth + 1);

    boolean isBalanced = Math.abs(leftSubtreeResult.height - rightSubtreeResult.height) <= 1;
    boolean subtreesAreBalanced = leftSubtreeResult.isBalanced && rightSubtreeResult.isBalanced;
    int height = Math.max(leftSubtreeResult.height, rightSubtreeResult.height) + 1;

    return new Result(isBalanced && subtreesAreBalanced, height);
}
```

首先，我们需要考虑节点是否为null的情况：我们将返回true(这意味着树是平衡的)和-1作为高度。

然后，我们对左右子树进行两次递归调用，使深度保持最新。

此时，我们已经为当前节点的子节点执行了计算。现在，我们拥有检查余额所需的所有数据：

-   isBalanced变量检查孩子的身高，以及
-   substreesAreBalanced 指示子树是否也平衡

最后，我们可以返回有关平衡和高度的信息。使用外观方法简化第一个递归调用可能也是一个好主意：

```java
public boolean isBalanced(Tree tree) {
    return isBalancedRecursive(tree, -1).isBalanced;
}
```

## 5.总结

在本文中，我们讨论了如何确定二叉树是否平衡。我们已经解释了深度优先搜索方法。