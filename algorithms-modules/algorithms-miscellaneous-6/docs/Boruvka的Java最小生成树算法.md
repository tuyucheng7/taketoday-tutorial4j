## 1. 概述

在本教程中，我们将了解 Boruvka 算法的Java实现，该算法用于查找边加权图的最小生成树 (MST)。

它早于[Prim](https://www.baeldung.com/java-prim-algorithm)和[Kruskal 的](https://www.baeldung.com/java-spanning-trees-kruskal)算法，但仍然可以被认为是两者的结合。

## 2. Boruvka 的算法

我们将直接进入手头的算法。让我们先回顾一下历史，然后再回顾一下算法本身。

### 2.1. 历史

[1926 年， Otakar Boruvka](https://en.wikipedia.org/wiki/Otakar_Borůvka)首次提出了一种寻找给定图的 MST 的方法。这甚至在计算机出现之前就已经存在，并且实际上被建模为设计一个高效的配电系统。

Georges Sollin 于 1965 年重新发现了它并将其用于并行计算。

### 2.2. 算法

该算法的中心思想是从一堆树开始，每个顶点代表一棵孤立的树。然后，我们需要不断添加边以减少孤立树的数量，直到我们拥有一棵连通树。

让我们通过示例图逐步了解这一点：

-   第 0 步：创建图表
-   第 1 步：从一堆未连接的树开始(树数 = 顶点数)
-   第 2 步：当存在未连接的树时，对于每个未连接的树：
    -   找到重量更轻的边缘
    -   添加此边以连接另一棵树

[![Boruvka-1](https://www.baeldung.com/wp-content/uploads/2020/03/Boruvka-1.png)](https://www.baeldung.com/wp-content/uploads/2020/03/Boruvka-1.png)

## 3.Java实现

现在让我们看看如何在Java中实现它。

### 3.1. UnionFind数据结构

首先，我们需要一个数据结构来存储顶点的父级和等级。

让我们为此目的定义一个类UnionFind ，它有两个方法： union和find：

```java
public class UnionFind {
    private int[] parents;
    private int[] ranks;

    public UnionFind(int n) {
        parents = new int[n];
        ranks = new int[n];
        for (int i = 0; i < n; i++) {
            parents[i] = i;
            ranks[i] = 0;
        }
    }

    public int find(int u) {
        while (u != parents[u]) {
            u = parents[u];
        }
        return u;
    }

    public void union(int u, int v) {
        int uParent = find(u);
        int vParent = find(v);
        if (uParent == vParent) {
            return;
        }

        if (ranks[uParent] < ranks[vParent]) { 
            parents[uParent] = vParent; 
        } else if (ranks[uParent] > ranks[vParent]) {
            parents[vParent] = uParent;
        } else {
            parents[vParent] = uParent;
            ranks[uParent]++;
        }
    }
}

```

我们可以认为这个类是一个帮助结构，用于维护我们的顶点之间的关系并逐渐建立我们的 MST。

要确定两个顶点u和v是否属于同一棵树，我们看find(u)是否返回与find(v)相同的父节点。union方法用于组合树。我们很快就会看到这种用法。

### 3.2. 从用户输入图表

现在我们需要一种方法从用户那里获取图形的顶点和边，并将它们映射到我们可以在运行时在算法中使用的对象。

因为我们将使用 JUnit 来测试我们的算法，所以这部分在@Before方法中进行：

```java
@Before
public void setup() {
    graph = ValueGraphBuilder.undirected().build();
    graph.putEdgeValue(0, 1, 8);
    graph.putEdgeValue(0, 2, 5);
    graph.putEdgeValue(1, 2, 9);
    graph.putEdgeValue(1, 3, 11);
    graph.putEdgeValue(2, 3, 15);
    graph.putEdgeValue(2, 4, 10);
    graph.putEdgeValue(3, 4, 7);
}

```

在这里，我们使用了 Guava 的MutableValueGraph<Integer, Integer>来存储我们的图表。然后我们使用ValueGraphBuilder构造一个无向加权图。

putEdgeValue方法采用三个参数，两个Integer用于顶点，第三个Integer用于权重，如MutableValueGraph的通用类型声明所指定。

正如我们所见，这与我们之前图表中显示的输入相同。

### 3.3. 导出最小生成树

最后，我们来到了问题的关键，算法的实现。

我们将在一个名为BoruvkaMST的类中执行此操作。首先，让我们声明几个实例变量：

```java
public class BoruvkaMST {
    private static MutableValueGraph<Integer, Integer> mst = ValueGraphBuilder.undirected().build();
    private static int totalWeight;
}

```

如我们所见，我们在这里使用MutableValueGraph<Integer, Integer>来表示 MST。

其次，我们将定义一个构造函数，所有魔法都在这里发生。它需要一个参数——我们之前构建的图表。

它做的第一件事是初始化输入图顶点的UnionFind。最初，所有顶点都是它们自己的父节点，每个顶点的秩为 0：

```java
public BoruvkaMST(MutableValueGraph<Integer, Integer> graph) {
    int size = graph.nodes().size();
    UnionFind uf = new UnionFind(size);

```

接下来，我们将创建一个循环来定义创建 MST 所需的迭代次数——最多 log V 次或直到我们有 V-1 条边，其中 V 是顶点数：

```java
for (int t = 1; t < size && mst.edges().size() < size - 1; t = t + t) {
    EndpointPair<Integer>[] closestEdgeArray = new EndpointPair[size];

```

在这里，我们还初始化了一个边数组closestEdgeArray——以存储最近的、权重较小的边。

之后，我们将定义一个内部for循环来遍历图形的所有边以填充我们的closestEdgeArray。

如果两个顶点的父节点相同，则为同一棵树，我们不将其添加到数组中。否则，我们将当前边的权重与其父顶点的边的权重进行比较。如果较小，则我们将其添加到closestEdgeArray：

```java
for (EndpointPair<Integer> edge : graph.edges()) {
    int u = edge.nodeU();
    int v = edge.nodeV();
    int uParent = uf.find(u);
    int vParent = uf.find(v);
    
    if (uParent == vParent) {
        continue;
    }

    int weight = graph.edgeValueOrDefault(u, v, 0);

    if (closestEdgeArray[uParent] == null) {
        closestEdgeArray[uParent] = edge;
    }
    if (closestEdgeArray[vParent] == null) {
        closestEdgeArray[vParent] = edge;
    }

    int uParentWeight = graph.edgeValueOrDefault(closestEdgeArray[uParent].nodeU(),
      closestEdgeArray[uParent].nodeV(), 0);
    int vParentWeight = graph.edgeValueOrDefault(closestEdgeArray[vParent].nodeU(),
      closestEdgeArray[vParent].nodeV(), 0);

    if (weight < uParentWeight) {
        closestEdgeArray[uParent] = edge;
    }
    if (weight < vParentWeight) {
        closestEdgeArray[vParent] = edge;
    }
}

```

然后，我们将定义第二个内部循环来创建树。我们会将上述步骤中的边添加到这棵树中，而不会将相同的边添加两次。此外，我们将在我们的UnionFind上执行一个联合来派生和存储新创建的树顶点的父母和等级：

```java
for (int i = 0; i < size; i++) {
    EndpointPair<Integer> edge = closestEdgeArray[i];
    if (edge != null) {
        int u = edge.nodeU();
        int v = edge.nodeV();
        int weight = graph.edgeValueOrDefault(u, v, 0);
        if (uf.find(u) != uf.find(v)) {
            mst.putEdgeValue(u, v, weight);
            totalWeight += weight;
            uf.union(u, v);
        }
    }
}

```

在最多重复这些步骤 log V 次或直到我们有 V-1 条边之后，生成的树就是我们的 MST。

## 4.测试

最后，让我们看一个简单的 JUnit 来验证我们的实现：

```java
@Test
public void givenInputGraph_whenBoruvkaPerformed_thenMinimumSpanningTree() {
   
    BoruvkaMST boruvkaMST = new BoruvkaMST(graph);
    MutableValueGraph<Integer, Integer> mst = boruvkaMST.getMST();

    assertEquals(30, boruvkaMST.getTotalWeight());
    assertEquals(4, mst.getEdgeCount());
}

```

正如我们所看到的，我们得到了权重为 30 和 4 条边的 MST，与图片示例相同。

## 5.总结

在本教程中，我们看到了 Boruvka 算法的Java实现。其时间复杂度为 O(E log V)，其中 E 为边数，V 为顶点数。