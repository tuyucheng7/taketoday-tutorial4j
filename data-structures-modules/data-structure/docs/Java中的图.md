## 1. 概述

在本教程中，我们将研究[图作为数据结构](https://www.baeldung.com/cs/graphs)的基本概念。

我们还将探索它在Java中的实现以及图形上可能的各种操作。我们还将讨论提供图形实现的Java库。

## 延伸阅读：

## [检查Java图是否有环](https://www.baeldung.com/java-graph-has-a-cycle)

了解如何检查Java中给定的有向图中是否存在循环。

[阅读更多](https://www.baeldung.com/java-graph-has-a-cycle)→

## [Java中的Dijkstra最短路径算法](https://www.baeldung.com/java-dijkstra)

Java中Dijkstra算法的解释和实现

[阅读更多](https://www.baeldung.com/java-dijkstra)→

## [JGraphT简介](https://www.baeldung.com/jgrapht)

了解如何使用 JGraphT 创建图形并探索各种图形算法。

[阅读更多](https://www.baeldung.com/jgrapht)→

## 2. 图数据结构

图是一种用于存储关联数据的数据结构，例如社交媒体平台上的人际网络。

图由顶点和边组成。顶点代表实体(例如，人)，边代表实体之间的关系(例如，一个人的友谊)。

让我们定义一个简单的图表来更好地理解这一点：

[![图1]()](https://www.baeldung.com/wp-content/uploads/2018/11/graph1.jpg)[![图1](https://www.baeldung.com/wp-content/uploads/2018/11/graph1.jpg)](https://www.baeldung.com/wp-content/uploads/2018/11/graph1.jpg)

在这里，我们定义了一个具有五个顶点和六个边的简单图。圆圈是代表人的顶点，连接两个顶点的线是代表在线门户网站上朋友的边。

根据边的属性，这个简单的图形有一些变化。让我们在接下来的部分中简要介绍一下它们。

但是，对于本教程中的Java示例，我们将只关注此处显示的简单图形。

### 2.1. 有向图

到目前为止我们定义的图有没有任何方向的边。如果这些边在其中具有方向，则生成的图称为有[向图](https://www.baeldung.com/cs/graphs-directed-vs-undirected-graph)。

这方面的一个例子可以代表谁在在线门户网站的友谊中发送了好友请求：

[![图2](https://www.baeldung.com/wp-content/uploads/2018/11/graph2.jpg)](https://www.baeldung.com/wp-content/uploads/2018/11/graph2.jpg)

在这里我们可以看到边缘有一个固定的方向。边缘也可以是双向的。

### 2.2. 加权图

同样，我们的简单图具有无偏或未加权的边。

相反，如果这些边带有相对权重，则该图称为加权图。

一个实际应用的例子可以代表在线门户网站上的友谊相对老旧：

[![图3](https://www.baeldung.com/wp-content/uploads/2018/11/graph3.jpg)](https://www.baeldung.com/wp-content/uploads/2018/11/graph3.jpg)

在这里我们可以看到边具有与之关联的权重。这为这些边缘提供了相对意义。

## 3. 图形表示

图可以用不同的形式表示，例如邻接矩阵和邻接表。每个人在不同的设置中都有其优点和缺点。

我们将在本节中介绍这些图形表示。

### 3.1. 邻接矩阵

邻接矩阵是一个方阵，其维度等于图中的顶点数。

矩阵的元素通常具有值 0 或 1。值 1 表示行和列中的顶点之间的邻接关系，否则值为 0。

让我们看看上一节中的简单图形的邻接矩阵是什么样子的：

[![图4](https://www.baeldung.com/wp-content/uploads/2018/11/graph4-300x277.jpg)](https://www.baeldung.com/wp-content/uploads/2018/11/graph4.jpg)

这种表示相当容易实现，查询起来也很高效。但是，它在 占用空间方面效率较低。

### 3.2. 邻接表

邻接表只不过是一个列表数组。数组的大小等于图中的顶点数。

数组特定索引处的列表表示该数组索引表示的顶点的相邻顶点。

让我们看看上一节中的简单图形的邻接表是什么样子的：

[![图5](https://www.baeldung.com/wp-content/uploads/2018/11/graph5-300x277.jpg)](https://www.baeldung.com/wp-content/uploads/2018/11/graph5.jpg)

这种表示相对难以创建且查询效率较低。但是，它提供了 更好的空间效率。

我们将使用邻接表来表示本教程中的图。

## 4.Java中的图形

Java 没有图数据结构的默认实现。

但是，我们可以使用JavaCollections 来实现图形。

让我们从定义一个顶点开始：

```java
class Vertex {
    String label;
    Vertex(String label) {
        this.label = label;
    }

    // equals and hashCode
}
```

上面的顶点定义只是一个标签，但这可以代表任何可能的实体，例如Person 或City。

另外请注意，我们必须重写equals()和hashCode()方法，因为这些方法是使用Java集合所必需的。

正如我们之前讨论的，图不过是顶点和边的集合，可以表示为邻接矩阵或邻接表。

让我们看看我们如何在这里使用邻接表来定义它：

```java
class Graph {
    private Map<Vertex, List<Vertex>> adjVertices;
    
    // standard constructor, getters, setters
}
```

正如我们所见，Graph类使用Java Collections 中的Map 来定义邻接表。

图数据结构可以进行多种操作，例如 创建、更新或搜索 图。

我们将通过一些更常见的操作，看看我们如何在Java中实现它们。

## 5.图变异操作

首先，我们将定义一些方法来改变图形数据结构。

让我们定义添加和删除顶点的方法：

```java
void addVertex(String label) {
    adjVertices.putIfAbsent(new Vertex(label), new ArrayList<>());
}

void removeVertex(String label) {
    Vertex v = new Vertex(label);
    adjVertices.values().stream().forEach(e -> e.remove(v));
    adjVertices.remove(new Vertex(label));
}
```

这些方法只是在顶点Set中添加和删除元素。

现在让我们也定义一个添加边的方法：

```java
void addEdge(String label1, String label2) {
    Vertex v1 = new Vertex(label1);
    Vertex v2 = new Vertex(label2);
    adjVertices.get(v1).add(v2);
    adjVertices.get(v2).add(v1);
}
```

此方法创建一个新的Edge并更新相邻的顶点Map。

以类似的方式，我们将定义removeEdge()方法：

```java
void removeEdge(String label1, String label2) {
    Vertex v1 = new Vertex(label1);
    Vertex v2 = new Vertex(label2);
    List<Vertex> eV1 = adjVertices.get(v1);
    List<Vertex> eV2 = adjVertices.get(v2);
    if (eV1 != null)
        eV1.remove(v2);
    if (eV2 != null)
        eV2.remove(v1);
}
```

接下来，让我们看看如何使用到目前为止定义的方法创建我们之前绘制的简单图形：

```java
Graph createGraph() {
    Graph graph = new Graph();
    graph.addVertex("Bob");
    graph.addVertex("Alice");
    graph.addVertex("Mark");
    graph.addVertex("Rob");
    graph.addVertex("Maria");
    graph.addEdge("Bob", "Alice");
    graph.addEdge("Bob", "Rob");
    graph.addEdge("Alice", "Mark");
    graph.addEdge("Rob", "Mark");
    graph.addEdge("Alice", "Maria");
    graph.addEdge("Rob", "Maria");
    return graph;
}
```

最后，我们将定义一个方法来获取特定顶点的相邻顶点：

```java
List<Vertex> getAdjVertices(String label) {
    return adjVertices.get(new Vertex(label));
}
```

## 6. 遍历图

现在我们已经定义了图形数据结构和函数来创建和更新它，我们可以定义一些额外的函数来遍历图形。

我们需要遍历一个图来执行任何有意义的动作，比如在图中搜索。

有两种可能的遍历图的方法：深度优先遍历和广度优先遍历。

### 6.1. 深度优先遍历

[深度优先遍历](https://www.baeldung.com/cs/depth-first-traversal-methods)从任意根顶点开始，并在探索相同级别的顶点之前沿着每个分支探索尽可能深的顶点。

让我们定义一个方法来执行深度优先遍历：

```java
Set<String> depthFirstTraversal(Graph graph, String root) {
    Set<String> visited = new LinkedHashSet<String>();
    Stack<String> stack = new Stack<String>();
    stack.push(root);
    while (!stack.isEmpty()) {
        String vertex = stack.pop();
        if (!visited.contains(vertex)) {
            visited.add(vertex);
            for (Vertex v : graph.getAdjVertices(vertex)) {              
                stack.push(v.label);
            }
        }
    }
    return visited;
}
```

在这里，我们使用Stack来存储需要遍历的顶点。

让我们在上一节中创建的图表上运行它：

```java
assertEquals("[Bob, Rob, Maria, Alice, Mark]", depthFirstTraversal(graph, "Bob").toString());
```

请注意，我们在这里使用顶点“Bob”作为遍历的根，但这可以是任何其他顶点。

### 6.2. 广度优先遍历

相比之下，广度优先遍历从任意根顶点开始，并在深入图中之前探索同一级别的所有相邻顶点。

现在让我们定义一个方法来执行广度优先遍历：

```java
Set<String> breadthFirstTraversal(Graph graph, String root) {
    Set<String> visited = new LinkedHashSet<String>();
    Queue<String> queue = new LinkedList<String>();
    queue.add(root);
    visited.add(root);
    while (!queue.isEmpty()) {
        String vertex = queue.poll();
        for (Vertex v : graph.getAdjVertices(vertex)) {
            if (!visited.contains(v.label)) {
                visited.add(v.label);
                queue.add(v.label);
            }
        }
    }
    return visited;
}
```

请注意，广度优先遍历使用队列来存储需要遍历的顶点。

让我们再次在同一个图上运行这个遍历：

```java
assertEquals(
  "[Bob, Alice, Rob, Mark, Maria]", breadthFirstTraversal(graph, "Bob").toString());
```

同样，根顶点(此处为“Bob”)也可以是任何其他顶点。

## 7.Java图形库

没有必要总是在Java中从头开始实现图形。有几个开源和成熟的库可以提供图形实现。

在接下来的几个小节中，我们将浏览其中的一些库。

### 7.1. JGraphT

[JGraphT](https://jgrapht.org/)是Java中最流行的图形数据结构库之一。它允许创建简单图、有向图和加权图等。

此外，它在图数据结构上提供了许多可能的算法。我们以前的教程[之一更详细地介绍了 JGraphT](https://www.baeldung.com/jgrapht)。

### 7.2. 谷歌番石榴

[Google Guava](https://github.com/google/guava/wiki/GraphsExplained)是一组Java库，提供一系列功能，包括图形数据结构及其算法。

它支持创建简单的Graph、ValueGraph 和Network。这些可以定义为Mutable或Immutable。

### 7.3. 阿帕奇公地

[Apache Commons](https://commons.apache.org/sandbox/commons-graph/)是一个提供可重用Java组件的 Apache 项目。这包括 Commons Graph，它提供了一个工具包来创建和管理图形数据结构。这也提供了通用的图形算法来对数据结构进行操作。

### 7.4. Sourceforge 荣格

[Java Universal Network/Graph (JUNG)](http://jung.sourceforge.net/)是一个Java框架，它提供可扩展的语言，用于对可以表示为图形的任何数据进行建模、分析和可视化。

JUNG 支持许多算法，包括聚类、分解和优化等例程。

这些库提供了许多基于图形数据结构的实现。还有更强大的基于图的框架，例如[Apache Giraph](https://giraph.apache.org/)，目前在 Facebook 用于分析其用户形成的图，以及[Apache TinkerPop](https://tinkerpop.apache.org/)，通常用于图数据库之上。

## 八. 总结

在本文中，我们将图作为一种数据结构及其表示进行了讨论。我们使用JavaCollections 在Java中定义了一个非常简单的图，还为图定义了常见的遍历。

我们还简要讨论了在Java平台之外提供图形实现的Java中可用的各种库。