## 1. 概述

在本教程中，我们将使用 Kotlin 编程语言实现[二叉树的基本操作。](https://www.baeldung.com/cs/binary-search-trees)

请随时查看我们的同一 [教程](https://www.baeldung.com/java-binary-tree)的 Java 版本。

## 2. 定义

在编程中，二叉树是一种树，其中每个节点的子节点不超过两个。每个节点都包含一些我们称为键的数据。

在不失一般性的情况下，让我们将我们的考虑限制在键只是整数的情况下。

所以，我们可以定义一个递归数据类型：

```java
class Node(
    var key: Int,
    var left: Node? = null,
    var right: Node? = null)
```

它包含一个值(整数值字段key)和对与其父项类型相同的左子项和右子项的可选引用。

我们看到，由于链接的性质，整个二叉树可以只用我们称为根节点的一个节点来描述。

如果我们对树结构应用一些限制，事情就会变得更有趣。在本教程中，我们假设树是有序二叉树(也称为二叉搜索树)。这意味着节点按某种顺序排列。

我们假设以下所有条件都是我们树[不变量](https://en.wikipedia.org/wiki/Invariant_(computer_science))的一部分：

1.  树不包含重复键
2.  对于每个节点，它的键大于它的左子树节点的键
3.  对于每个节点，它的键小于它的右子树节点的键

## 3. 基本操作

一些最常见的操作包括：

-   搜索具有给定值的节点
-   插入新值
-   删除现有值
-   并按一定顺序检索节点

### 3.1. 抬头

当树被排序后，查找过程变得非常高效：如果要搜索的值等于当前节点的值，则查找结束；如果要搜索的值大于当前节点的值，那么我们可以丢弃左子树，只考虑右子树：

```java
fun find(value: Int): Node? = when {
    this.value > value -> left?.findByValue(value)
    this.value < value -> right?.findByValue(value)
    else -> this
}
```

请注意，该值可能不存在于树的键中，因此查找结果可能会返回空值。

请注意，我们如何使用[Kotlin 关键字 when](https://www.baeldung.com/kotlin-when) 它是switch-case语句的 Java 模拟，但更强大和灵活。

### 3.2. 插入

由于树不允许任何重复键，因此插入新值非常容易：

1.  如果该值已经存在，则不需要任何操作
2.  如果该值不存在，则将其插入到左侧或右侧“槽”空置的节点

因此，我们可以递归地解析树以搜索应该容纳该值的子树。当值小于当前节点的键时，如果存在左子树，则选择它。如果不存在，则表示找到了插入值的位置：这是当前节点的左子节点。

同样，在值大于当前节点的键的情况下。唯一剩下的可能性是当该值等于当前节点键时：这意味着该值已经存在于树中，我们什么都不做：

```java
fun insert(value: Int) {
    if (value > this.key) {
        if (this.right == null) {
            this.right = Node(value)
        } else {
            this.right.insert(value)
        }
    } else if (value < this.key) {
        if (this.left == null) {
            this.left = Node(value)
        } else {
            this.left.insert(value)
        }
    }
```

### 3.3. 移动

首先，我们应该确定包含给定值的节点。与查找过程类似，我们扫描树以搜索节点并维护对所搜索节点的父节点的引用：

```java
fun delete(value: Int) {
    when {
        value > key -> scan(value, this.right, this)
        value < key -> scan(value, this.left, this)
        else -> removeNode(this, null)
    }
}
```

从二叉树中删除节点时，我们可能会遇到三种不同的情况。我们根据非空子节点的数量对它们进行分类。

两个子节点都为 null
这种情况处理起来非常简单，并且是唯一一种我们可能无法消除节点的情况：如果节点是根节点，我们无法消除它。否则，将父项对应的子项设置为null就足够了

[![一个](https://www.baeldung.com/wp-content/uploads/2018/10/aa.png)](https://www.baeldung.com/wp-content/uploads/2018/10/aa.png)

这种方法的实现可能如下所示：

```java
private fun removeNoChildNode(node: Node, parent: Node?) {
    if (parent == null) {
        throw IllegalStateException("Can not remove the root node without child nodes")
    }
    if (node == parent.left) {
        parent.left = null
    } else if (node == parent.right) {
        parent.right = null
    }
}
```

一个孩子为空，另一个不为空
在这种情况下，我们应该总是成功，因为它足以将唯一的孩子节点“转移”到我们要删除的节点中：

[![bb](https://www.baeldung.com/wp-content/uploads/2018/10/bb.png)](https://www.baeldung.com/wp-content/uploads/2018/10/bb.png)

我们可以直接实现这个案例：

```java
private fun removeSingleChildNode(parent: Node, child: Node) {
    parent.key = child.key
    parent.left = child.left
    parent.right = child.right
}
```

两个子节点都不为空
这种情况更加复杂，因为我们应该找到一个节点来替换我们要删除的节点。找到这个“替换”节点的一种方法是在左子树中选择一个具有最大键(肯定存在)的节点。另一种方法是对称的：我们应该在右子树中选择一个具有最小键的节点(它也存在)。这里，我们选择第一个：

[![抄送](https://www.baeldung.com/wp-content/uploads/2018/10/cc.png)](https://www.baeldung.com/wp-content/uploads/2018/10/cc.png)

一旦找到替换节点，我们应该从其父节点“重置”对它的引用。这意味着在搜索替换节点时，我们也应该返回其父节点：

```java
private fun removeTwoChildNode(node: Node) {
    val leftChild = node.left!!
    leftChild.right?.let {
        val maxParent = findParentOfMaxChild(leftChild)
        maxParent.right?.let {
            node.key = it.key
            maxParent.right = null
        } ?: throw IllegalStateException("Node with max child must have the right child!")
    } ?: run {
        node.key = leftChild.key
        node.left = leftChild.left
    }
}
```

### 3.4. 遍历

有多种方式可以访问节点。最常见的是深度优先、广度优先和层次优先搜索。在这里，我们只考虑可以是以下类型之一的深度优先搜索：

1.   预序(访问父节点，然后是左孩子，然后是右孩子)
2.  顺序(访问左孩子，然后是父节点，然后是右孩子)
3.  后序(访问左孩子，然后是右孩子，然后是父节点)

在 Kotlin 中，所有这些类型的遍历都可以通过非常简单的方式完成。例如，对于前序遍历，我们有：

```java
fun visit(): Array<Int> {
    val a = left?.visit() ?: emptyArray()
    val b = right?.visit() ?: emptyArray()
    return a + arrayOf(key) + b
}
```

请注意，Kotlin 如何允许我们使用“+”运算符连接数组。这种实现远非高效：它不是尾递归，对于更深的树，我们可能会遇到堆栈溢出异常。

## 4。总结

在本教程中，我们考虑了如何使用 Kotlin 语言构建和实现二叉搜索树的基本操作。我们演示了一些 Kotlin 结构，这些结构在 Java 中不存在，但我们可能会发现它们很有用。