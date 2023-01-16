## 1. 概述

在本快速教程中，我们将学习如何检测给定有向图中的循环。

## 2.图形表示

对于本教程，我们将坚持使用邻接表[图形表示](https://www.baeldung.com/java-graphs#graph_representations)。

首先，让我们从在Java中定义一个Vertex开始：

```java
public class Vertex {

    private String label;
    private boolean beingVisited;
    private boolean visited;
    private List<Vertex> adjacencyList;

    public Vertex(String label) {
        this.label = label;
        this.adjacencyList = new ArrayList<>();
    }

    public void addNeighbor(Vertex adjacent) {
        this.adjacencyList.add(adjacent);
    }
    //getters and setters
}
```

这里，顶点v的adjacencyList包含与v相邻的所有顶点的列表。addNeighbor ()方法将相邻顶点添加到v的邻接列表中。

我们还定义了两个boolean参数，beingVisited和visited，分别表示该节点是当前正在被访问还是已经被访问过。

图可以被认为是一组通过边连接的顶点或节点。

那么，现在让我们快速用 Java表示一个图：

```java
public class Graph {

    private List<Vertex> vertices;

    public Graph() {
        this.vertices = new ArrayList<>();
    }

    public void addVertex(Vertex vertex) {
        this.vertices.add(vertex);
    }

    public void addEdge(Vertex from, Vertex to) {
        from.addNeighbor(to);
    }

   // ...
}
```

我们将使用addVertex()和addEdge()方法在我们的图中添加新的顶点和边。

## 3. 循环检测

为了检测有向图中的循环，我们将使用DFS遍历的变体：

-   拾取一个未访问的顶点v并将其状态标记为beingVisited

-   对于v的每个相邻顶点

    u

    ，检查：

    

    

    -   如果u已经处于beingVisited状态，这显然意味着存在向后边缘，因此检测到循环
    -   如果你还处于未访问状态，我们将以深度优先的方式递归访问你

-   将顶点v的beingVisited标志更新为false并将它的visited标志更新为true

请注意，我们图中的所有顶点最初都处于未访问状态，因为它们的beingVisited和visited标志都初始化为false。 

现在让我们看看我们的Java解决方案：

```java
public boolean hasCycle(Vertex sourceVertex) {
    sourceVertex.setBeingVisited(true);

    for (Vertex neighbor : sourceVertex.getAdjacencyList()) {
        if (neighbor.isBeingVisited()) {
            // backward edge exists
            return true;
        } else if (!neighbor.isVisited() && hasCycle(neighbor)) {
            return true;
        }
    }

    sourceVertex.setBeingVisited(false);
    sourceVertex.setVisited(true);
    return false;
}
```

我们可以使用图中的任何顶点作为源或起始顶点。

对于断开连接的图，我们必须添加一个额外的包装方法：

```java
public boolean hasCycle() {
    for (Vertex vertex : vertices) {
        if (!vertex.isVisited() && hasCycle(vertex)) {
            return true;
        }
    }
    return false;
}
```

这是为了确保我们访问断开连接的图形的每个组件以检测循环。

## 4.实施测试

让我们考虑下面的循环有向图：

[![有向图](https://www.baeldung.com/wp-content/uploads/2019/06/DirectedGraph.png)](https://www.baeldung.com/wp-content/uploads/2019/06/DirectedGraph.png)

我们可以快速编写一个 JUnit 来验证这个图的hasCycle()方法：

```java
@Test
public void givenGraph_whenCycleExists_thenReturnTrue() {

    Vertex vertexA = new Vertex("A");
    Vertex vertexB = new Vertex("B");
    Vertex vertexC = new Vertex("C")
    Vertex vertexD = new Vertex("D");

    Graph graph = new Graph();
    graph.addVertex(vertexA);
    graph.addVertex(vertexB);
    graph.addVertex(vertexC);
    graph.addVertex(vertexD);

    graph.addEdge(vertexA, vertexB);
    graph.addEdge(vertexB, vertexC);
    graph.addEdge(vertexC, vertexA);
    graph.addEdge(vertexD, vertexC);

    assertTrue(graph.hasCycle());

}
```

在这里，我们的hasCycle()方法返回true表示我们的图是循环的。

## 5.总结

在本教程中，我们学习了如何检查Java中给定的有向图中是否存在循环。