## 1. 概述

在本教程中，我们将探索Java中的深度优先搜索。

[深度优先搜索 (DFS)](https://www.baeldung.com/cs/depth-first-traversal-methods)是一种用于树和[图数据结构](https://www.baeldung.com/cs/graphs)的遍历算法。深度优先 搜索在移动到探索另一个分支之前深入每个分支。

在接下来的部分中，我们将首先查看树的实现，然后查看图。

要了解如何在Java中实现这些结构，请查看我们之前关于[二叉树](https://www.baeldung.com/java-binary-tree)和[图](https://www.baeldung.com/java-graphs)的教程。

## 2.树深度优先搜索

使用 DFS 遍历树有三种不同的顺序：

1.  前序遍历
2.  中序遍历
3.  邮政订单遍历

### 2.1. 前序遍历

前序遍历是先遍历根，再遍历左右子树。

我们可以简单地使用递归实现前序遍历：

-   访问当前节点
-   遍历左子树
-   遍历右子树

```java
public void traversePreOrder(Node node) {
    if (node != null) {
        visit(node.value);
        traversePreOrder(node.left);
        traversePreOrder(node.right);
    }
}
```

我们也可以不用递归实现前序遍历。

要实现迭代前序遍历，我们需要一个Stack，我们将完成以下步骤：

-   在我们的堆栈中扎根

-   虽然

    堆栈

    不为空

    -   弹出当前节点
    -   访问当前节点
    -   推右孩子，然后左孩子堆叠

```java
public void traversePreOrderWithoutRecursion() {
    Stack<Node> stack = new Stack<Node>();
    Node current = root;
    stack.push(root);
    while(!stack.isEmpty()) {
        current = stack.pop();
        visit(current.value);
        
        if(current.right != null) {
            stack.push(current.right);
        }    
        if(current.left != null) {
            stack.push(current.left);
        }
    }        
}
```

### 2.2. 中序遍历

对于中序遍历，我们先遍历左子树，然后遍历根，最后遍历右子树。

二叉搜索树的中序遍历意味着按节点值的递增顺序遍历节点。

我们可以简单地使用递归实现中序遍历：

```java
public void traverseInOrder(Node node) {
    if (node != null) {
        traverseInOrder(node.left);
        visit(node.value);
        traverseInOrder(node.right);
    }
}
```

我们也可以不用递归实现中序遍历：

-   用根初始化当前节点

-   

    current

    不为空或stack

    不

    为空

    -   继续将左孩子推入堆栈，直到我们到达当前节点的最左边的孩子
    -   弹出并访问栈中最左边的节点
    -   将current设置为弹出节点的右子节点

```java
public void traverseInOrderWithoutRecursion() {
    Stack stack = new Stack<>();
    Node current = root;

    while (current != null || !stack.isEmpty()) {
        while (current != null) {
            stack.push(current);
            current = current.left;
        }

        Node top = stack.pop();
        visit(top.value);
        current = top.right;
    }
}
```

### 2.3. 邮政订单遍历

最后，在后序遍历中，我们先遍历左右子树，然后再遍历根。

我们可以按照我们之前的 递归解决方案：

```java
public void traversePostOrder(Node node) {
    if (node != null) {
        traversePostOrder(node.left);
        traversePostOrder(node.right);
        visit(node.value);
    }
}
```

或者，我们也可以不用递归实现后序遍历：

-   将根节点压入堆栈

-   虽然

    堆栈

    不为空

    -   检查我们是否已经遍历了左右子树
    -   如果不是，则将右孩子和左孩子压入堆栈

```java
public void traversePostOrderWithoutRecursion() {
    Stack<Node> stack = new Stack<Node>();
    Node prev = root;
    Node current = root;
    stack.push(root);

    while (!stack.isEmpty()) {
        current = stack.peek();
        boolean hasChild = (current.left != null || current.right != null);
        boolean isPrevLastChild = (prev == current.right || 
          (prev == current.left && current.right == null));

        if (!hasChild || isPrevLastChild) {
            current = stack.pop();
            visit(current.value);
            prev = current;
        } else {
            if (current.right != null) {
                stack.push(current.right);
            }
            if (current.left != null) {
                stack.push(current.left);
            }
        }
    }   
}
```

## 3.图深度优先搜索

图和树之间的主要区别在于图可能包含循环。

所以为了避免循环查找，我们会在访问每个节点时对其进行标记。

我们将看到图 DFS 的两种实现，使用递归和不使用递归。

### 3.1. 带递归的图 DFS

首先，让我们从简单的递归开始：

-   我们将从给定的节点开始
-   将当前节点标记为已访问
-   访问当前节点
-   遍历未访问的相邻顶点

```java
public void dfs(int start) {
    boolean[] isVisited = new boolean[adjVertices.size()];
    dfsRecursive(start, isVisited);
}

private void dfsRecursive(int current, boolean[] isVisited) {
    isVisited[current] = true;
    visit(current);
    for (int dest : adjVertices.get(current)) {
        if (!isVisited[dest])
            dfsRecursive(dest, isVisited);
    }
}
```

### 3.2. 没有递归的图 DFS

我们还可以在没有递归的情况下实现图 DFS。我们将简单地使用Stack：

-   我们将从给定的节点开始

-   将起始节点压入堆栈

-   虽然

    Stack

    不为空

    -   将当前节点标记为已访问
    -   访问当前节点
    -   推送未访问的相邻顶点

```java
public void dfsWithoutRecursion(int start) {
    Stack<Integer> stack = new Stack<Integer>();
    boolean[] isVisited = new boolean[adjVertices.size()];
    stack.push(start);
    while (!stack.isEmpty()) {
        int current = stack.pop();
        if(!isVisited[current]){
            isVisited[current] = true;
            visit(current);
            for (int dest : adjVertices.get(current)) {
                if (!isVisited[dest])
                    stack.push(dest);
            }
    }
}
```

### 3.4. 拓扑排序

图深度优先搜索有很多应用。DFS 的著名应用之一是拓扑排序。

有向图的拓扑排序是其顶点的线性排序，因此对于每条边，源节点都在目标节点之前。

为了进行拓扑排序，我们需要对刚刚实现的 DFS 进行简单的添加：

-   我们需要将访问过的顶点保存在一个栈中，因为拓扑排序是将访问过的顶点倒序排列
-   只有在遍历所有邻居之后，我们才将访问过的节点压入堆栈

```java
public List<Integer> topologicalSort(int start) {
    LinkedList<Integer> result = new LinkedList<Integer>();
    boolean[] isVisited = new boolean[adjVertices.size()];
    topologicalSortRecursive(start, isVisited, result);
    return result;
}

private void topologicalSortRecursive(int current, boolean[] isVisited, LinkedList<Integer> result) {
    isVisited[current] = true;
    for (int dest : adjVertices.get(current)) {
        if (!isVisited[dest])
            topologicalSortRecursive(dest, isVisited, result);
    }
    result.addFirst(current);
}
```

## 4. 总结

在本文中，我们讨论了树和图数据结构的深度优先搜索。