## 1. 简介

在本教程中，我们首先了解什么是最小生成树。之后，我们将使用[Prim 的算法](https://www.baeldung.com/cs/prim-algorithm)来找到一个。

## 2. 最小生成树

最小生成树 (MST) 是一种加权的、无向的、连通的图，其总边权重已通过删除较重的边而最小化。换句话说，我们保持图的所有顶点完好无损，但我们可能会删除一些边，使所有边的总和最小。

我们从加权图开始，因为如果这些边根本没有权重，则最小化总边权重是没有意义的。让我们看一个示例图：

[![主0001](https://www.baeldung.com/wp-content/uploads/2019/12/prim0001.jpg)](https://www.baeldung.com/wp-content/uploads/2019/12/prim0001.jpg)

上图是一个加权的、无向的、连通的图。这是该图的 MST：

[![原始0000](https://www.baeldung.com/wp-content/uploads/2019/12/prim0000.jpg)](https://www.baeldung.com/wp-content/uploads/2019/12/prim0000.jpg)

但是，图的 MST 不是唯一的。如果一个图有多个 MST，则每个 MST 具有相同的总边权重。

## 3. 普里姆算法

Prim 的算法将加权的、无向的、连通的图作为输入，并返回该图的 MST 作为输出。

它以贪婪的方式工作。在第一步中，它选择一个任意顶点。此后，每个新步骤都会将最近的顶点添加到目前构建的树中，直到没有断开连接的顶点为止。

让我们在此图上逐步运行 Prim 算法：

[![主0007](https://www.baeldung.com/wp-content/uploads/2019/12/prim0007.jpg)](https://www.baeldung.com/wp-content/uploads/2019/12/prim0007.jpg)

假设开始算法的任意顶点是 B，我们有 A、C 和 E 三个选择。相应的边权重为 2、2 和 5，因此最小值为 2。在这种情况下，我们有两条边的权重为 2，因此我们可以选择其中之一(选择哪一个并不重要)。我们选A：

[![主0006](https://www.baeldung.com/wp-content/uploads/2019/12/prim0006.jpg)](https://www.baeldung.com/wp-content/uploads/2019/12/prim0006.jpg)

现在我们有一个包含两个顶点 A 和 B 的树。我们可以选择 A 或 B 的任何尚未添加的边，这些边会导致未添加的顶点。所以，我们可以选择 AC、BC 或 BE。

Prim 的算法选择最小值，即 2，或 BC：

[![主0003](https://www.baeldung.com/wp-content/uploads/2019/12/prim0003.jpg)](https://www.baeldung.com/wp-content/uploads/2019/12/prim0003.jpg)

现在我们有一棵树，其中有三个顶点和三个可能的边可以向前移动：CD、CE 或 BE。AC 不包括在内，因为它不会向树中添加新顶点。这三个中的最小权重是 1。

然而，有两条边的权重都为 1。因此，Prim 的算法在此步骤中选择其中一条(同样，哪一条并不重要)：[![主0004](https://www.baeldung.com/wp-content/uploads/2019/12/prim0004.jpg)](https://www.baeldung.com/wp-content/uploads/2019/12/prim0004.jpg)

只剩下一个要连接的顶点，所以我们可以从 CE 和 BE 中选择。可以将我们的树连接到它的最小权重为 1，Prim 的算法将选择它：

[![主0005](https://www.baeldung.com/wp-content/uploads/2019/12/prim0005.jpg)](https://www.baeldung.com/wp-content/uploads/2019/12/prim0005.jpg)

由于输入图的所有顶点现在都出现在输出树中，Prim 的算法结束。因此，这棵树是输入图的 MST。

## 4.实施

顶点和边组成图，所以我们需要一个数据结构来存储这些元素。让我们创建类Edge：

```java
public class Edge {

    private int weight;
    private boolean isIncluded = false;

}
```

由于 Prim 的算法适用于加权图，因此每条边都必须具有权重。isIncluded显示边是否存在于最小生成树中。

现在，让我们添加Vertex类：

```java
public class Vertex {

    private String label = null;
    private Map<Vertex, Edge> edges = new HashMap<>();
    private boolean isVisited = false;

}
```

每个Vertex都可以有一个label。我们使用边图来存储顶点之间的连接。最后，isVisited显示该顶点到目前为止是否已被 Prim 算法访问过。

让我们创建我们的Prim类，我们将在其中实现逻辑：

```java
public class Prim {

    private List<Vertex> graph;

}
```

一个顶点列表足以存储整个图，因为在每个Vertex中，我们有一个Map<Vertex, Edge>来标识所有连接。在Prim 内部，我们创建了一个run()方法：

```java
public void run() {
    if (graph.size() > 0) {
        graph.get(0).setVisited(true);
    }
    while (isDisconnected()) {
        Edge nextMinimum = new Edge(Integer.MAX_VALUE);
        Vertex nextVertex = graph.get(0);
        for (Vertex vertex : graph) {
            if (vertex.isVisited()) {
                Pair<Vertex, Edge> candidate = vertex.nextMinimum();
                if (candidate.getValue().getWeight() < nextMinimum.getWeight()) {
                    nextMinimum = candidate.getValue();
                    nextVertex = candidate.getKey();
                }
            }
        }
        nextMinimum.setIncluded(true);
        nextVertex.setVisited(true);
    }
}
```

我们首先将List<Vertex> 图的第一个元素设置为已访问。第一个元素可以是任何顶点，具体取决于它们最初添加到列表中的顺序。如果到目前为止还没有访问任何顶点， isDisconnected()返回true ：

```java
private boolean isDisconnected() {
    for (Vertex vertex : graph) {
        if (!vertex.isVisited()) {
            return true;
        }
    }
    return false;
}
```

在最小生成树isDisconnected()时，我们遍历已经访问过的顶点并找到具有最小权重的边作为下一个顶点的候选：

```java
public Pair<Vertex, Edge> nextMinimum() {
    Edge nextMinimum = new Edge(Integer.MAX_VALUE);
    Vertex nextVertex = this;
    Iterator<Map.Entry<Vertex,Edge>> it = edges.entrySet()
        .iterator();
    while (it.hasNext()) {
        Map.Entry<Vertex,Edge> pair = it.next();
        if (!pair.getKey().isVisited()) {
            if (!pair.getValue().isIncluded()) {
                if (pair.getValue().getWeight() < nextMinimum.getWeight()) {
                    nextMinimum = pair.getValue();
                    nextVertex = pair.getKey();
                }
            }
        }
    }
    return new Pair<>(nextVertex, nextMinimum);
}
```

我们在主循环中找到所有candidates中的最小值并将其存储在nextVertex中。然后，我们将nextVertex设置为已访问。循环继续，直到所有顶点都被访问。

最后，存在 每个isIncluded等于true的边。

请注意，由于nextMinimum()遍历边缘，因此此实现的时间复杂度为O(V 2 )。如果我们将边存储在优先级队列中(按权重排序)，则该算法将在O(E log V)中执行。

## 5. 测试

好的，现在我们已经有了一些代码，让我们用一个真实的例子来测试它。首先，我们构造我们的图表：

```java
public static List<Vertex> createGraph() {
    List<Vertex> graph = new ArrayList<>();
    Vertex a = new Vertex("A");
    ...
    Vertex e = new Vertex("E");
    Edge ab = new Edge(2);
    a.addEdge(b, ab);
    b.addEdge(a, ab);
    ...
    Edge ce = new Edge(1);
    c.addEdge(e, ce);
    e.addEdge(c, ce);
    graph.add(a);
    ...
    graph.add(e);
    return graph;
}
```

Prim类的构造函数获取它并将其存储在类中。我们可以使用originalGraphToString()方法打印输入图：

```java
Prim prim = new Prim(createGraph());
System.out.println(prim.originalGraphToString());
```

我们的示例将输出：

```plaintext
A --- 2 --- B
A --- 3 --- C
B --- 5 --- E
B --- 2 --- C
C --- 1 --- E
C --- 1 --- D
```

现在，我们运行 Prim 的算法并使用minimumSpanningTreeToString()方法打印生成的 MST ：

```java
prim.run();
prim.resetPrintHistory();
System.out.println(prim.minimumSpanningTreeToString());
```

最后，我们打印出我们的 MST：

```plaintext
A --- 2 --- B
B --- 2 --- C
C --- 1 --- E
C --- 1 --- D
```

## 六. 总结

在本文中，我们了解了 Prim 算法如何找到图的最小生成树。