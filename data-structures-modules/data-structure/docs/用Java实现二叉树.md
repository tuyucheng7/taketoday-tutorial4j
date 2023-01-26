## 1. 简介

在本教程中，我们将介绍Java中二叉树的实现。

为了本教程，我们将使用包含int值的[排序二叉树](https://www.baeldung.com/cs/binary-search-trees)。

## 延伸阅读：

## [如何打印二叉树图](https://www.baeldung.com/java-print-binary-tree-diagram)

了解如何打印二叉树图。

[阅读更多](https://www.baeldung.com/java-print-binary-tree-diagram)→

## [在Java中反转二叉树](https://www.baeldung.com/java-reversing-a-binary-tree)

在Java中反转二叉树的快速实用指南。

[阅读更多](https://www.baeldung.com/java-reversing-a-binary-tree)→

## [Java中的深度优先搜索](https://www.baeldung.com/java-depth-first-search)

Java 深度优先搜索算法指南，同时使用树和图数据结构。

[阅读更多](https://www.baeldung.com/java-depth-first-search)→

## 2. 二叉树

二叉树是一种递归数据结构，其中每个节点最多可以有 2 个子节点。

二叉树的一种常见类型是二[叉搜索树](https://www.baeldung.com/cs/bst-validation)，其中每个节点的值都大于或等于左子树中的节点值，并且小于或等于右子树中的节点值树。

这是这种二叉树的直观表示：

[![树一](https://www.baeldung.com/wp-content/uploads/2017/12/Tree-1.jpg)](https://www.baeldung.com/wp-content/uploads/2017/12/Tree-1.jpg)

对于实现，我们将使用一个辅助Node类来存储int值，并保留对每个子节点的引用：

```java
class Node {
    int value;
    Node left;
    Node right;

    Node(int value) {
        this.value = value;
        right = null;
        left = null;
    }
}
```

然后我们将添加树的起始节点，通常称为根：

```java
public class BinaryTree {

    Node root;

    // ...
}
```

## 三、常用操作

现在让我们看看我们可以在二叉树上执行的最常见的操作。

### 3.1. 插入元素

我们要介绍的第一个操作是插入新节点。

首先，我们必须找到我们要添加新节点的地方，以保持树的排序。我们将从根节点开始遵循这些规则：

-   如果新节点的值低于当前节点的值，我们转到左孩子
-   如果新节点的值大于当前节点的值，我们转到右孩子
-   当当前节点为空时，我们已经到达叶节点，我们可以在该位置插入新节点

然后我们将创建一个递归方法来进行插入：

```java
private Node addRecursive(Node current, int value) {
    if (current == null) {
        return new Node(value);
    }

    if (value < current.value) {
        current.left = addRecursive(current.left, value);
    } else if (value > current.value) {
        current.right = addRecursive(current.right, value);
    } else {
        // value already exists
        return current;
    }

    return current;
}
```

接下来我们将创建从根节点开始递归的公共方法：

```java
public void add(int value) {
    root = addRecursive(root, value);
}
```

让我们看看如何使用此方法从我们的示例中创建树：

```java
private BinaryTree createBinaryTree() {
    BinaryTree bt = new BinaryTree();

    bt.add(6);
    bt.add(4);
    bt.add(8);
    bt.add(3);
    bt.add(5);
    bt.add(7);
    bt.add(9);

    return bt;
}
```

### 3.2. 查找元素

现在让我们添加一个方法来检查树是否包含特定值。

和以前一样，我们将首先创建一个遍历树的递归方法：

```java
private boolean containsNodeRecursive(Node current, int value) {
    if (current == null) {
        return false;
    } 
    if (value == current.value) {
        return true;
    } 
    return value < current.value
      ? containsNodeRecursive(current.left, value)
      : containsNodeRecursive(current.right, value);
}
```

在这里，我们通过将其与当前节点中的值进行比较来搜索该值；然后我们将根据结果继续左孩子或右孩子。

接下来我们将创建从根开始的公共方法：

```java
public boolean containsNode(int value) {
    return containsNodeRecursive(root, value);
}
```

然后我们将创建一个简单的测试来验证树是否确实包含插入的元素：

```java
@Test
public void givenABinaryTree_WhenAddingElements_ThenTreeContainsThoseElements() {
    BinaryTree bt = createBinaryTree();

    assertTrue(bt.containsNode(6));
    assertTrue(bt.containsNode(4));
 
    assertFalse(bt.containsNode(1));
}
```

添加的所有节点都应包含在树中。

### 3.3. 删除元素

另一个常见的操作是从树中删除一个节点。

首先，我们必须以与之前类似的方式找到要删除的节点：

```java
private Node deleteRecursive(Node current, int value) {
    if (current == null) {
        return null;
    }

    if (value == current.value) {
        // Node to delete found
        // ... code to delete the node will go here
    } 
    if (value < current.value) {
        current.left = deleteRecursive(current.left, value);
        return current;
    }
    current.right = deleteRecursive(current.right, value);
    return current;
}
```

一旦我们找到要删除的节点，有 3 种主要的不同情况：

-   一个节点没有孩子——这是最简单的情况；我们只需要在其父节点中将此节点替换为null
-   一个节点只有一个孩子——在父节点中，我们用它唯一的孩子替换这个节点。
-   一个节点有两个孩子——这是最复杂的情况，因为它需要树重组

让我们看看当节点是叶节点时我们将如何实现第一种情况：

```java
if (current.left == null && current.right == null) {
    return null;
}
```

现在让我们继续处理节点有一个孩子的情况：

```java
if (current.right == null) {
    return current.left;
}

if (current.left == null) {
    return current.right;
}
```

在这里，我们返回非空子节点，以便可以将其分配给父节点。

最后，我们必须处理节点有两个孩子的情况。

首先，我们需要找到将替换已删除节点的节点。我们将使用即将被删除节点的右子树的最小节点：

```java
private int findSmallestValue(Node root) {
    return root.left == null ? root.value : findSmallestValue(root.left);
}
```

然后我们将最小的值分配给要删除的节点，之后，我们将从右子树中删除它：

```java
int smallestValue = findSmallestValue(current.right);
current.value = smallestValue;
current.right = deleteRecursive(current.right, smallestValue);
return current;
```

最后，我们将创建从根开始删除的公共方法：

```java
public void delete(int value) {
    root = deleteRecursive(root, value);
}
```

现在让我们检查删除是否按预期工作：

```java
@Test
public void givenABinaryTree_WhenDeletingElements_ThenTreeDoesNotContainThoseElements() {
    BinaryTree bt = createBinaryTree();

    assertTrue(bt.containsNode(9));
    bt.delete(9);
    assertFalse(bt.containsNode(9));
}
```

## 4.遍历树

在本节中，我们将探讨遍历树的不同方法，详细介绍深度优先和广度优先搜索。

我们将使用我们之前使用的同一棵树，并且我们将检查每种情况的遍历顺序。

### 4.1. 深度优先搜索

深度优先搜索是一种遍历，它在探索下一个兄弟姐妹之前尽可能深入每个孩子。

有几种方法可以执行深度优先搜索：按顺序、前序和后序。

中序遍历首先访问左子树，然后是根节点，最后是右子树：

```java
public void traverseInOrder(Node node) {
    if (node != null) {
        traverseInOrder(node.left);
        System.out.print(" " + node.value);
        traverseInOrder(node.right);
    }
}
```

如果我们调用此方法，控制台输出将显示中序遍历：

```plaintext
3 4 5 6 7 8 9
```

先序遍历先访问根节点，然后是左子树，最后是右子树：

```java
public void traversePreOrder(Node node) {
    if (node != null) {
        System.out.print(" " + node.value);
        traversePreOrder(node.left);
        traversePreOrder(node.right);
    }
}
```

让我们在控制台输出中检查前序遍历：

```plaintext
6 4 3 5 8 7 9
```

后序遍历访问左子树、右子树和末尾的根节点：

```java
public void traversePostOrder(Node node) {
    if (node != null) {
        traversePostOrder(node.left);
        traversePostOrder(node.right);
        System.out.print(" " + node.value);
    }
}
```

以下是后序节点：

```plaintext
3 5 4 7 9 8 6
```

### 4.2. 广度优先搜索

这是另一种常见的遍历类型，它在进入下一个级别之前访问一个级别的所有节点。

这种遍历也称为层序，从根开始，从左到右遍历树的所有层级。

对于实现，我们将使用队列按顺序保存每个级别的节点。我们将从列表中提取每个节点，打印其值，然后将其子节点添加到队列中：

```java
public void traverseLevelOrder() {
    if (root == null) {
        return;
    }

    Queue<Node> nodes = new LinkedList<>();
    nodes.add(root);

    while (!nodes.isEmpty()) {

        Node node = nodes.remove();

        System.out.print(" " + node.value);

        if (node.left != null) {
            nodes.add(node.left);
        }

        if (node.right != null) {
            nodes.add(node.right);
        }
    }
}
```

在这种情况下，节点的顺序将是：

```plaintext
6 4 8 3 5 7 9
```

## 5.总结

在本文中，我们学习了如何在Java中实现排序的二叉树及其最常见的操作。