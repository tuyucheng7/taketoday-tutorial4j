## 1. 简介

在本教程中，我们将介绍 AVL 树，并研究用于插入、删除和搜索值的算法。

## 2.什么是AVL树？

AVL 树以其发明者 Adelson-Velsky 和 Landis 的名字命名，是一种自平衡[二叉搜索树](https://www.baeldung.com/java-binary-tree)(BST)。

自平衡树是一种二叉搜索树，它根据一些平衡规则来平衡插入和删除后的高度。

BST 的最坏情况时间复杂度是树的高度的函数。具体来说，就是从树根到节点的最长路径。对于具有 N 个节点的 BST，假设每个节点只有零个或一个子节点。因此它的高度等于N，最坏情况下的搜索时间是O(N)。所以我们在 BST 中的主要目标是保持最大高度接近 log(N)。

节点 N 的平衡因子为height(right(N)) – height(left(N))。在 AVL 树中，节点的平衡因子只能是 1、0 或 -1 值之一。

让我们为我们的树定义一个Node对象：

```java
public class Node {
    int key;
    int height;
    Node left;
    Node right;
    ...
}
```

接下来，让我们定义AVLTree：

```java
public class AVLTree {

    private Node root;

    void updateHeight(Node n) {
        n.height = 1 + Math.max(height(n.left), height(n.right));
    }

    int height(Node n) {
        return n == null ? -1 : n.height;
    }

    int getBalance(Node n) {
        return (n == null) ? 0 : height(n.right) - height(n.left);
    }

    ...
}
```

## 3. 如何平衡 AVL 树？

AVL 树在插入或删除节点后检查其节点的平衡因子。如果节点的平衡因子大于 1 或小于 -1，则树会自行重新平衡。

有两种重新平衡树的操作：

-   右旋和
-   左旋转。

### 3.1. 右旋

让我们从正确的旋转开始。

假设我们有一个名为 T1 的 BST，Y 是根节点，X 是 Y 的左孩子，Z 是 X 的右孩子。鉴于 BST 的特征，我们知道 X < Z < Y。

在 Y 的右旋转之后，我们有一个名为 T2 的树，其中 X 是根，Y 是 X 的右孩子，Z 是 Y 的左孩子。T2 仍然是 BST，因为它保持顺序 X < Z < Y .

[![R-大-1](https://www.baeldung.com/wp-content/uploads/2020/02/R-Large-1-1024x322.png)](https://www.baeldung.com/wp-content/uploads/2020/02/R-Large-1.png)

让我们来看看我们的AVLTree的正确旋转操作：

```java
Node rotateRight(Node y) {
    Node x = y.left;
    Node z = x.right;
    x.right = y;
    y.left = z;
    updateHeight(y);
    updateHeight(x);
    return x;
}
```

### 3.2. 左旋操作

我们还有一个左旋转操作。

假设一个名为 T1 的 BST，Y 是根节点，X 是 Y 的右孩子，Z 是 X 的左孩子。鉴于此，我们知道 Y < Z < X。

在 Y 的左旋转之后，我们有一个名为 T2 的树，其中 X 是根，Y 是 X 的左孩子，Z 是 Y 的右孩子。T2 仍然是 BST，因为它保持顺序 Y < Z < X .

[![L-大-1](https://www.baeldung.com/wp-content/uploads/2020/02/L-Large-1-1024x362.png)](https://www.baeldung.com/wp-content/uploads/2020/02/L-Large-1.png)

让我们看一下我们的AVLTree的左旋转操作：

```java
Node rotateLeft(Node y) {
    Node x = y.right;
    Node z = x.left;
    x.left = y;
    y.right = z;
    updateHeight(y);
    updateHeight(x);
    return x;
}
```

### 3.3. 再平衡技术

我们可以在更复杂的组合中使用右旋转和左旋转操作，以在其节点发生任何变化后保持 AVL 树的平衡。在不平衡结构中，至少有一个节点的平衡因子等于 2 或 -2。让我们看看在这些情况下如何平衡树。

当节点 Z 的平衡因子为 2 时，以 Z 为根的子树处于这两种状态之一，将 Y 视为 Z 的右孩子。

对于第一种情况，Y (X) 的右孩子的高度大于左孩子 (T2) 的高度。我们可以通过向左旋转 Z 轻松地重新平衡树。

[![ZL-大号](https://www.baeldung.com/wp-content/uploads/2020/02/ZL-Large-1024x374.png)](https://www.baeldung.com/wp-content/uploads/2020/02/ZL-Large.png)

对于第二种情况，Y (T4) 的右孩子的高度小于左孩子 (X) 的高度。这种情况需要结合轮换操作。

[![YRZL-大号](https://www.baeldung.com/wp-content/uploads/2020/02/YRZL-Large-1024x237.png)](https://www.baeldung.com/wp-content/uploads/2020/02/YRZL-Large.png)

在这种情况下，我们首先将 Y 向右旋转，因此树的形状与前一种情况相同。然后我们可以通过 Z 的左旋转来重新平衡树。

此外，当节点 Z 的平衡因子为 -2 时，其子树处于这两种状态之一，因此我们将 Z 视为根节点，将 Y 视为其左子节点。

Y 的左孩子的高度大于其右孩子的高度，所以我们用 Z 的右旋转来平衡树。

[![ZR-大号](https://www.baeldung.com/wp-content/uploads/2020/02/ZR-Large-1024x313.png)](https://www.baeldung.com/wp-content/uploads/2020/02/ZR-Large.png)

或者在第二种情况下，Y 的右孩子的高度大于其左孩子的高度。

[![YLZR-大号](https://www.baeldung.com/wp-content/uploads/2020/02/YLZR-Large-1024x221.png)](https://www.baeldung.com/wp-content/uploads/2020/02/YLZR-Large.png)

所以，首先，我们将它转换成以前的形状，左旋转 Y，然后我们用 Z 的右旋转来平衡树。

让我们看一下AVLTree的重新平衡操作：

```java
Node rebalance(Node z) {
    updateHeight(z);
    int balance = getBalance(z);
    if (balance > 1) {
        if (height(z.right.right) > height(z.right.left)) {
            z = rotateLeft(z);
        } else {
            z.right = rotateRight(z.right);
            z = rotateLeft(z);
        }
    } else if (balance < -1) {
        if (height(z.left.left) > height(z.left.right))
            z = rotateRight(z);
        else {
            z.left = rotateLeft(z.left);
            z = rotateRight(z);
        }
    }
    return z;
}
```

我们将在为从更改节点到根的路径中的所有节点插入或删除一个节点后使用重新平衡。

## 4.插入节点

当我们要在树中插入一个键时，我们必须找到它的正确位置才能通过 BST 规则。所以我们从根开始，将它的值与新键进行比较。如果密钥更大，我们继续向右 - 否则，我们转到左边的孩子。

一旦找到合适的父节点，我们就会根据值将新键作为节点添加到左侧或右侧。

插入节点后，我们有一个 BST，但它可能不是一个 AVL Tree。因此，我们检查平衡因子并对从新节点到根的路径中的所有节点重新平衡 BST。

我们来看看插入操作：

```java
Node insert(Node node, int key) {
    if (node == null) {
        return new Node(key);
    } else if (node.key > key) {
        node.left = insert(node.left, key);
    } else if (node.key < key) {
        node.right = insert(node.right, key);
    } else {
        throw new RuntimeException("duplicate Key!");
    }
    return rebalance(node);
}
```

重要的是要记住键在树中是唯一的——没有两个节点共享相同的键。

插入算法的时间复杂度是高度的函数。由于我们的树是平衡的，我们可以假设最坏情况下的时间复杂度是 O(log(N))。

## 5.删除一个节点

要从树中删除一个键，我们首先必须在 BST 中找到它。

找到节点(称为 Z)后，我们必须引入新的候选节点作为它在树中的替代节点。如果 Z 是叶子，则候选为空。如果Z只有一个孩子，这个孩子就是候选人，但是如果Z有两个孩子，过程就稍微复杂一点。

假设Z的右孩子Y。首先，我们找到Y的最左边的节点，并将其称为X。然后，我们将Z的新值设置为等于X的值，并继续从Y中删除X。

最后我们在最后调用rebalance方法，让BST保持一颗AVL Tree。

这是我们的删除方法：

```java
Node delete(Node node, int key) {
    if (node == null) {
        return node;
    } else if (node.key > key) {
        node.left = delete(node.left, key);
    } else if (node.key < key) {
        node.right = delete(node.right, key);
    } else {
        if (node.left == null || node.right == null) {
            node = (node.left == null) ? node.right : node.left;
        } else {
            Node mostLeftChild = mostLeftChild(node.right);
            node.key = mostLeftChild.key;
            node.right = delete(node.right, node.key);
        }
    }
    if (node != null) {
        node = rebalance(node);
    }
    return node;
}
```

删除算法的时间复杂度是树的高度的函数。与insert方法类似，我们可以假设最坏情况下的时间复杂度是O(log(N))。

## 6.搜索节点

在 AVL 树中搜索节点与使用任何 BST 相同。

从树的根开始，将键与节点的值进行比较。如果键等于值，则返回节点。如果key更大，则从右孩子开始查找，否则从左孩子继续查找。

搜索的时间复杂度是高度的函数。我们可以假设最坏情况下的时间复杂度是 O(log(N))。

让我们看一下示例代码：

```java
Node find(int key) {
    Node current = root;
    while (current != null) {
        if (current.key == key) {
            break;
        }
        current = current.key < key ? current.right : current.left;
    }
    return current;
}
```

## 七. 总结

在本教程中，我们实现了具有插入、删除和搜索操作的 AVL 树。