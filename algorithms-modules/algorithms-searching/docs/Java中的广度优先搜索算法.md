## 1. 概述

在本教程中，我们将学习[广度优先搜索算法](https://www.baeldung.com/cs/graph-algorithms-bfs-dijkstra)，它允许我们通过广度优先而不是深度优先遍历节点来搜索树或图中的节点。

首先，我们将通过一些关于树和图算法的理论。之后，我们将深入探讨Java中算法的实现。最后，我们将介绍它们的时间复杂度。

## 2.广度优先搜索算法

广度优先搜索 (BFS) 算法的基本方法是通过在子节点之前探索邻居来将节点搜索到树或图结构中。

首先，我们将了解该算法如何适用于树。之后，我们将使其适用于图形，这些图形具有有时包含循环的特定约束。最后，我们将讨论该算法的性能。

### 2.1. 树木

[树的 BFS 算法](http://opendatastructures.org/versions/edition-0.1e/ods-java/6_1_BinaryTree_Basic_Binary.html#sec:bintree:traversal)背后的思想是维护一个节点队列，以确保遍历顺序。在算法开始时，队列只包含根节点。只要队列仍然包含一个或多个节点，我们就会重复这些步骤：

-   从队列中弹出第一个节点
-   如果该节点是我们要搜索的节点，则搜索结束
-   否则，将此节点的子节点添加到队列的末尾并重复上述步骤

没有循环可确保执行终止。我们将在下一节中看到如何管理周期。

### 2.2. 图表

[对于图形，](http://opendatastructures.org/versions/edition-0.1e/ods-java/12_3_Graph_Traversal.html#SECTION001531000000000000000)我们必须考虑结构中可能的循环。如果我们简单地将前面的算法应用到一个有环的图上，它就会永远循环下去。因此，我们需要保留一个已访问节点的集合，并确保我们不会访问它们两次：

-   从队列中弹出第一个节点
-   检查节点是否已经被访问过，如果是则跳过它
-   如果该节点是我们要搜索的节点，则搜索结束
-   否则，将其添加到访问过的节点
-   将此节点的子节点添加到队列中并重复这些步骤

## 3.Java实现

现在已经涵盖了理论，让我们开始编写代码并用Java实现这些算法！

### 3.1. 树木

首先，我们将实现树算法。让我们设计我们的 Tree类，它由一个值和由其他Tree的列表表示的孩子组成：

```java
public class Tree<T> {
    private T value;
    private List<Tree<T>> children;

    private Tree(T value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    public static <T> Tree<T> of(T value) {
        return new Tree<>(value);
    }

    public Tree<T> addChild(T value) {
        Tree<T> newChild = new Tree<>(value);
        children.add(newChild);
        return newChild;
    }
}
```

为避免创建循环，子项由类本身根据给定值创建。

之后，让我们提供一个search()方法：

```java
public static <T> Optional<Tree<T>> search(T value, Tree<T> root) {
    //...
}
```

正如我们前面提到的，BFS 算法使用队列来遍历节点。首先，我们将 根节点添加到这个队列中：

```java
Queue<Tree<T>> queue = new ArrayDeque<>();
queue.add(root);
```

然后，我们必须在队列不为空时循环，每次我们从队列中弹出一个节点：

```java
while(!queue.isEmpty()) {
    Tree<T> currentNode = queue.remove();
}
```

如果该节点是我们正在搜索的节点，我们将其返回，否则我们将其子节点添加到队列中：

```java
if (currentNode.getValue().equals(value)) {
    return Optional.of(currentNode);
} else {
    queue.addAll(currentNode.getChildren());
}
```

最后，如果我们访问了所有节点而没有找到我们正在搜索的节点，我们将返回一个空结果：

```java
return Optional.empty();
```

现在让我们想象一个示例树结构：

[![树的例子](https://www.baeldung.com/wp-content/uploads/2019/10/BFS-Tree-Example.png)](https://www.baeldung.com/wp-content/uploads/2019/10/BFS-Tree-Example.png)

转换成Java代码：

```java
Tree<Integer> root = Tree.of(10);
Tree<Integer> rootFirstChild = root.addChild(2);
Tree<Integer> depthMostChild = rootFirstChild.addChild(3);
Tree<Integer> rootSecondChild = root.addChild(4);
```

然后，如果搜索值 4，我们希望算法按以下顺序遍历值为 10、2 和 4 的节点：

```java
BreadthFirstSearchAlgorithm.search(4, root)
```

我们可以通过记录访问节点的值来验证：

```plaintext
[main] DEBUG  c.b.a.b.BreadthFirstSearchAlgorithm - Visited node with value: 10
[main] DEBUG  c.b.a.b.BreadthFirstSearchAlgorithm - Visited node with value: 2 
[main] DEBUG  c.b.a.b.BreadthFirstSearchAlgorithm - Visited node with value: 4
```

### 3.2. 图表

树木的情况到此结束。现在让我们看看如何处理图形。与树相反，图可以包含循环。这意味着，正如我们在上一节中看到的那样，我们必须记住我们访问过的节点以避免无限循环。我们稍后会看到如何更新算法来考虑这个问题，但首先，让我们定义我们的图形结构：

```java
public class Node<T> {
    private T value;
    private Set<Node<T>> neighbors;

    public Node(T value) {
        this.value = value;
        this.neighbors = new HashSet<>();
    }

    public void connect(Node<T> node) {
        if (this == node) throw new IllegalArgumentException("Can't connect node to itself");
        this.neighbors.add(node);
        node.neighbors.add(this);
    }
}
```

现在，我们可以看到，与树相反，我们可以自由地将一个节点与另一个节点连接起来，从而使我们有可能创建循环。唯一的例外是节点无法连接到自身。

还值得注意的是，在这种表示中，没有根节点。这不是问题，因为我们还在节点之间建立了双向连接。这意味着我们将能够从任何节点开始搜索图形。

首先，让我们重用上面的算法，适应新的结构：

```java
public static <T> Optional<Node<T>> search(T value, Node<T> start) {
    Queue<Node<T>> queue = new ArrayDeque<>();
    queue.add(start);

    Node<T> currentNode;

    while (!queue.isEmpty()) {
        currentNode = queue.remove();

        if (currentNode.getValue().equals(value)) {
            return Optional.of(currentNode);
        } else {
            queue.addAll(currentNode.getNeighbors());
        }
    }

    return Optional.empty();
}
```

我们不能这样运行算法，否则任何循环都会让它永远运行下去。因此，我们必须添加指令来处理已经访问过的节点：

```java
while (!queue.isEmpty()) {
    currentNode = queue.remove();
    LOGGER.debug("Visited node with value: {}", currentNode.getValue());

    if (currentNode.getValue().equals(value)) {
        return Optional.of(currentNode);
    } else {
        alreadyVisited.add(currentNode);
        queue.addAll(currentNode.getNeighbors());
        queue.removeAll(alreadyVisited);
    }
}

return Optional.empty();
```

如我们所见，我们首先初始化一个 包含访问节点的集合。

```java
Set<Node<T>> alreadyVisited = new HashSet<>();
```

然后，当值比较失败时，我们将节点添加到已访问的节点中：

```java
alreadyVisited.add(currentNode);
```

最后，在将节点的邻居添加到队列后，我们从队列中删除已经访问过的节点(这是检查当前节点是否存在于该集合中的另一种方法)：

```java
queue.removeAll(alreadyVisited);
```

通过这样做，我们确保算法不会陷入无限循环。

让我们通过一个例子看看它是如何工作的。首先，我们将定义一个带有循环的图：

[![图形示例](https://www.baeldung.com/wp-content/uploads/2019/10/BFS-Graph-Example.png)](https://www.baeldung.com/wp-content/uploads/2019/10/BFS-Graph-Example.png)

在Java代码中也是如此：

```java
Node<Integer> start = new Node<>(10);
Node<Integer> firstNeighbor = new Node<>(2);
start.connect(firstNeighbor);

Node<Integer> firstNeighborNeighbor = new Node<>(3);
firstNeighbor.connect(firstNeighborNeighbor);
firstNeighborNeighbor.connect(start);

Node<Integer> secondNeighbor = new Node<>(4);
start.connect(secondNeighbor);
```

再次假设我们要搜索值 4。由于没有根节点，我们可以从我们想要的任何节点开始搜索，我们将选择firstNeighborNeighbor：

```java
BreadthFirstSearchAlgorithm.search(4, firstNeighborNeighbor);
```

同样，我们将添加一个日志以查看访问了哪些节点，我们希望它们是 3、2、10 和 4，每个节点仅按此顺序访问一次：

```plaintext
[main] DEBUG c.b.a.b.BreadthFirstSearchAlgorithm - Visited node with value: 3 
[main] DEBUG c.b.a.b.BreadthFirstSearchAlgorithm - Visited node with value: 2 
[main] DEBUG c.b.a.b.BreadthFirstSearchAlgorithm - Visited node with value: 10 
[main] DEBUG c.b.a.b.BreadthFirstSearchAlgorithm - Visited node with value: 4
```

### 3.3. 复杂

现在我们已经介绍了Java中的两种算法，让我们谈谈它们的时间复杂度。我们将使用[Big-O 表示法](https://www.baeldung.com/big-o-notation)来表达它们。

让我们从树算法开始。它最多将一个节点添加到队列中一次，因此也最多访问它一次。因此，如果 n是树中的节点数，则算法的时间复杂度将为O(n)。

现在，对于图形算法，事情有点复杂。我们将最多遍历每个节点一次，但为此我们将使用具有线性复杂性的操作，例如addAll()和 removeAll()。

让我们考虑 n节点数和 c图形的连接数。然后，在最坏的情况下(没有找到节点)，我们可能会使用addAll()和 removeAll()方法来添加和删除节点，直到达到连接数，这些操作的复杂度为O(c) 。因此，假设c > n，整个算法的复杂度将为 O(c)。否则，它将是 O(n)。这通常[记 为O(n + c)](https://www.khanacademy.org/computing/computer-science/algorithms/breadth-first-search/a/analysis-of-breadth-first-search)，这可以解释为取决于n和c之间的最大数的复杂性。

为什么我们的树搜索没有这个问题？因为树中的连接数受节点数的限制。n 个节点 的树中的连接数为n – 1。

## 4. 总结

在本文中，我们了解了广度优先搜索算法以及如何在Java中实现它。

在了解了一些理论之后，我们看到了该算法的Java实现并讨论了它的复杂性。