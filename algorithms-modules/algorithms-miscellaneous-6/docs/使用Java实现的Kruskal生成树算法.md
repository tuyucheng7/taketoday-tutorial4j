## 1. 概述

在之前的文章中，我们介绍了[Prim的寻找最小生成树的算法](https://www.baeldung.com/java-prim-algorithm)。在本文中，我们将使用另一种方法 Kruskal 算法来解决最小和最大生成树问题。

## 2.生成树

无向图的生成树是一个连通的子图，它以尽可能少的边数覆盖所有的图节点。一般来说，一个图可能有不止一棵生成树。下图显示了带有生成树的图(生成树的边为红色)：

[![生成树](https://www.baeldung.com/wp-content/uploads/2019/12/spanning_tree.png)](https://www.baeldung.com/wp-content/uploads/2019/12/spanning_tree.png)

如果图是边加权的，我们可以将生成树的权重定义为其所有边的权重之和。最小生成树是所有可能的生成树中权重最小的生成树。 下图显示了边加权图上的最小生成树：

[![最小生成树](https://www.baeldung.com/wp-content/uploads/2019/12/minimum_spanning_tree.png)](https://www.baeldung.com/wp-content/uploads/2019/12/minimum_spanning_tree.png)

同样，最大生成树在所有生成树中具有最大的权重。下图显示了边加权图上的最大生成树：

[![最大生成树](https://www.baeldung.com/wp-content/uploads/2019/12/maximum_spanning_tree.png)](https://www.baeldung.com/wp-content/uploads/2019/12/maximum_spanning_tree.png)

## 3. Kruskal 算法

给定一个图，我们可以使用 Kruskal 算法找到它的最小生成树。如果图中的节点数为V，则其每棵生成树都应具有 (V-1) 条边并且不包含循环。我们可以用以下伪代码描述 Kruskal 算法：

```c#
Initialize an empty edge set T. 
Sort all graph edges by the ascending order of their weight values. 
foreach edge in the sorted edge list
    Check whether it will create a cycle with the edges inside T.
    If the edge doesn't introduce any cycles, add it into T. 
    If T has (V-1) edges, exit the loop. 
return T
```

让我们在示例图上逐步运行 Kruskal 的最小生成树算法：

[![最小生成树算法](https://www.baeldung.com/wp-content/uploads/2019/12/minimum_spanning_tree_alg.png)](https://www.baeldung.com/wp-content/uploads/2019/12/minimum_spanning_tree_alg.png)

首先，我们选择边 (0, 2) 因为它的权重最小。然后，我们可以添加边 (3, 4) 和 (0, 1)，因为它们不会创建任何循环。现在下一个候选边是权重为 9 的边 (1, 2)。但是，如果我们包含这条边，我们将生成一个循环 (0, 1, 2)。因此，我们丢弃这条边，继续选择下一个最小的边。最后，算法通过添加权重为 10 的边 (2, 4) 来完成。

为了计算最大生成树，我们可以将排序顺序改为降序。其他步骤保持不变。下图显示了在我们的示例图上逐步构建最大生成树。

[![最大生成树算法](https://www.baeldung.com/wp-content/uploads/2019/12/maxmum_spanning_tree_alg.png)](https://www.baeldung.com/wp-content/uploads/2019/12/maxmum_spanning_tree_alg.png)

## 4. 不相交集的循环检测

在 Kruskal 的算法中，关键部分是检查如果我们将一条边添加到现有边集中，它是否会创建一个循环。我们可以使用几种图循环检测算法。例如，我们可以使用[深度优先搜索(DFS)算法](https://www.baeldung.com/java-graph-has-a-cycle)来遍历图并检测是否存在循环。

但是，每次测试一条新边时，我们都需要对现有边进行循环检测。更快的解决方案是使用具有不相交数据结构的 Union-Find 算法，因为它还 使用增量边缘添加方法来检测循环。我们可以将其纳入我们的生成树构建过程。

### 4.1. 不相交集和生成树构造

首先，我们将图中的每个节点视为仅包含一个节点的单独集合。然后，每次我们引入一条边时，我们检查它的两个节点是否在同一个集合中。如果答案是肯定的，那么它将创建一个循环。否则，我们将两个不相交的集合合并为一个集合，并包括生成树的边。

我们可以重复上述步骤，直到构建出整个生成树。

例如，在上面的最小生成树构造中，我们首先有5个节点集：{0}、{1}、{2}、{3}、{4}。当我们检查第一条边 (0, 2) 时，它的两个节点在不同的节点集中。因此，我们可以包含这条边并将 {0} 和 {2} 合并为一组 {0, 2}。

我们可以对边 (3, 4) 和 (0, 1) 进行类似的操作。然后节点集变为 {0, 1, 2} 和 {3, 4}。当我们检查下一条边 (1, 2) 时，我们可以看到这条边的两个节点都在同一个集合中。因此，我们丢弃这条边，继续检查下一条。最后，边 (2, 4) 满足我们的条件，我们可以将它包含在最小生成树中。

### 4.2. 不相交集实现

我们可以使用树结构来表示不相交的集合。每个节点都有一个父 指针来引用其父节点。在每个集合中，都有一个唯一的根节点代表这个集合。根节点有一个自引用的 父指针。

让我们使用Java类来定义不相交的集合信息：

```java
public class DisjointSetInfo {
    private Integer parentNode;
    DisjointSetInfo(Integer parent) {
        setParentNode(parent);
    }
 
    //standard setters and getters
}
```

让我们用从 0 开始的整数标记每个图节点。我们可以使用列表数据结构List<DisjointSetInfo> nodes来存储图的不相交集信息。一开始，每个节点都是自己集合的代表成员：

```java
void initDisjointSets(int totalNodes) {
    nodes = new ArrayList<>(totalNodes);
    for (int i = 0; i < totalNodes; i++) {
        nodes.add(new DisjointSetInfo(i));
    }
}

```

### 4.3. 查找操作

要找到一个节点所属的集合，我们可以沿着节点的父链向上，直到到达根节点：

```java
Integer find(Integer node) {
    Integer parent = nodes.get(node).getParentNode();
    if (parent.equals(node)) {
        return node;
    } else {
        return find(parent);
    }
}
```

不相交的集合可能具有高度不平衡的树结构。我们可以通过使用路径压缩技术改进查找操作。

由于我们在通往根节点的途中访问的每个节点都是同一集合的一部分，因此我们可以将根节点直接附加到其父 引用。下次我们访问这个节点时，我们需要一个查找路径来获取根节点：

```java
Integer pathCompressionFind(Integer node) {
    DisjointSetInfo setInfo = nodes.get(node);
    Integer parent = setInfo.getParentNode();
    if (parent.equals(node)) {
        return node;
    } else {
        Integer parentNode = find(parent);
        setInfo.setParentNode(parentNode);
        return parentNode;
    }
}
```

### 4.4. 联盟运作

如果边的两个节点在不同的集合中，我们会将这两个集合合并为一个。我们可以通过将一个代表节点的根设置为另一个代表节点来实现这种联合操作：

```java
void union(Integer rootU, Integer rootV) {
    DisjointSetInfo setInfoU = nodes.get(rootU);
    setInfoU.setParentNode(rootV);
}
```

当我们为合并集选择随机根节点时，这个简单的联合操作可能会产生高度不平衡的树。我们可以使用union by rank技术来提高性能。

由于影响查找操作运行时间的是树深度，因此我们将树较短的集合附加到树较长的集合。如果原始两棵树的深度相同，则此技术只会增加合并树的深度。

为此，我们首先向DisjointSetInfo类添加一个等级属性：

```java
public class DisjointSetInfo {
    private Integer parentNode;
    private int rank;
    DisjointSetInfo(Integer parent) {
        setParentNode(parent);
        setRank(0);
    }
 
    //standard setters and getters
}
```

开始时，不相交的单个节点的秩为 0。在两个集合的并集期间，具有更高秩的根节点成为合并集合的根节点。只有当原来的两个等级相同时，我们才将新根节点的等级增加一：

```java
void unionByRank(int rootU, int rootV) {
    DisjointSetInfo setInfoU = nodes.get(rootU);
    DisjointSetInfo setInfoV = nodes.get(rootV);
    int rankU = setInfoU.getRank();
    int rankV = setInfoV.getRank();
    if (rankU < rankV) {
        setInfoU.setParentNode(rootV);
    } else {
        setInfoV.setParentNode(rootU);
        if (rankU == rankV) {
            setInfoU.setRank(rankU + 1);
        }
    }
}
```

### 4.5. 循环检测

我们可以通过比较两次查找操作的结果来确定两个节点是否在同一个不相交的集合中。如果它们具有相同的代表性根节点，那么我们检测到一个循环。否则，我们使用联合操作合并两个不相交的集合：

```java
boolean detectCycle(Integer u, Integer v) {
    Integer rootU = pathCompressionFind(u);
    Integer rootV = pathCompressionFind(v);
    if (rootU.equals(rootV)) {
        return true;
    }
    unionByRank(rootU, rootV);
    return false;
}

```

仅使用按等级合并技术的循环检测[的运行时间为O(logV)](https://en.wikipedia.org/wiki/Disjoint-set_data_structure)。我们可以通过路径压缩和按等级合并技术获得更好的性能。运行时间为[O(α(V))](https://en.wikipedia.org/wiki/Disjoint-set_data_structure)，其中α(V)是节点总数的[反阿克曼函数。](https://en.wikipedia.org/wiki/Ackermann_function#Inverse)它是一个小常数，在我们的实际计算中小于 5。

## 5. Kruskal算法的Java实现

我们可以使用[Google Guava中的](https://search.maven.org/classic/#search|ga|1|g%3A"com.google.guava" a%3A"guava")[ValueGraph](https://guava.dev/releases/snapshot-jre/api/docs/com/google/common/graph/ValueGraph.html)数据结构来表示边加权图。

要使用ValueGraph，我们首先需要将 Guava 依赖项添加到我们项目的pom.xml 文件中：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

我们可以将上述循环检测方法包装到一个CycleDetector 类中，并在 Kruskal 算法中使用它。由于最小和最大生成树构造算法只有细微差别，我们可以使用一个通用函数来实现两种构造：

```java
ValueGraph<Integer, Double> spanningTree(ValueGraph<Integer, Double> graph, boolean minSpanningTree) {
    Set<EndpointPair> edges = graph.edges();
    List<EndpointPair> edgeList = new ArrayList<>(edges);

    if (minSpanningTree) {
        edgeList.sort(Comparator.comparing(e -> graph.edgeValue(e).get()));
    } else {
        edgeList.sort(Collections.reverseOrder(Comparator.comparing(e -> graph.edgeValue(e).get())));
    }

    int totalNodes = graph.nodes().size();
    CycleDetector cycleDetector = new CycleDetector(totalNodes);
    int edgeCount = 0;

    MutableValueGraph<Integer, Double> spanningTree = ValueGraphBuilder.undirected().build();
    for (EndpointPair edge : edgeList) {
        if (cycleDetector.detectCycle(edge.nodeU(), edge.nodeV())) {
            continue;
        }
        spanningTree.putEdgeValue(edge.nodeU(), edge.nodeV(), graph.edgeValue(edge).get());
        edgeCount++;
        if (edgeCount == totalNodes - 1) {
            break;
        }
    }
    return spanningTree;
}
```

在 Kruskal 算法中，我们首先按权重对所有图边进行排序。此操作需要O(ElogE)时间，其中E是边的总数。

然后我们使用循环遍历排序后的边列表。在每次迭代中，我们检查是否通过将边添加到当前生成树边集中来形成循环。这个带有循环检测的循环最多需要O(ElogV)时间。

因此，整体运行时间为O(ELogE + ELogV)。由于E的值在O(V 2 )的范围内，因此 Kruskal 算法的时间复杂度为O(ElogE)或O(ElogV)。

## 六. 总结

在本文中，我们学习了如何使用 Kruskal 算法查找图的最小或最大生成树。