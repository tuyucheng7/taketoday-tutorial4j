## 1. 概述

本文的重点是最短路径问题 (SPP)，它是图论中已知的基本理论问题之一，以及如何[使用 Dijkstra 算法](https://www.baeldung.com/cs/dijkstra)来解决它。

该算法的基本目标是确定起始节点与图中其余部分之间的最短路径。

## 2. Dijkstra 的最短路径问题

给定一个正[加权图](https://www.baeldung.com/cs/weighted-vs-unweighted-graphs)和一个起始节点 (A)，Dijkstra 确定图中从源到所有目的地的最短路径和距离：

[![初始图](https://www.baeldung.com/wp-content/uploads/2017/01/initial-graph-300x125.png)](https://www.baeldung.com/wp-content/uploads/2017/01/initial-graph.png)

Dijkstra 算法的核心思想是不断消除起始节点和所有可能的目的地之间较长的路径。

为了跟踪流程，我们需要有两组不同的节点，已结算和未结算。

定居节点是与源的最小距离已知的节点。未解决的节点集收集了我们可以从源到达的节点，但我们不知道距起始节点的最小距离。

以下是使用 Dijkstra 求解 SPP 的步骤列表：

-   将到startNode的距离设置为零。
-   将所有其他距离设置为无限值。
-   我们将startNode添加到未解决的节点集中。
-   虽然未解决的节点集不为空，但我们：
    -   从未解决的节点集合中选择一个评估节点，评估节点应该是与源距离最短的节点。
    -   通过在每次评估时保持最低距离来计算到直接邻居的新距离。
    -   将尚未定居的邻居添加到未定居的节点集中。

这些步骤可以汇总为两个阶段，即初始化和评估。让我们看看这如何应用于我们的示例图：

### 2.1. 初始化

在我们开始探索图中的所有路径之前，我们首先需要初始化所有具有无限距离和未知前驱的节点，源除外。

作为初始化过程的一部分，我们需要将值0赋给节点A(我们知道节点A到节点A的距离显然是0)

因此，图中其余部分中的每个节点都将通过前导节点和距离来区分：

[![步骤1](https://www.baeldung.com/wp-content/uploads/2017/01/step1-300x136.png)](https://www.baeldung.com/wp-content/uploads/2017/01/step1.png)

为了完成初始化过程，我们需要将节点 A 添加到未解决的节点中，并将其设置为在评估步骤中首先被选中。请记住，已结算的节点集仍然是空的。

### 2.2. 评估

现在我们已经初始化了图形，我们选择与未解决的集合距离最短的节点，然后我们评估所有不在已解决节点中的相邻节点：

[![第2步](https://www.baeldung.com/wp-content/uploads/2017/01/step2-300x136.png)](https://www.baeldung.com/wp-content/uploads/2017/01/step2.png)

这个想法是将边缘权重添加到评估节点距离，然后将其与目的地的距离进行比较。例如对于节点B，0+10小于INFINITY，所以节点B的新距离为10，新的前驱为A，节点C同理。

然后节点 A 从未结算节点集移动到已结算节点。

节点 B 和 C 被添加到未解决节点中，因为它们可以到达，但需要评估。

现在我们在未解决的集合中有两个节点，我们选择距离最短的一个(节点 B)，然后我们重复直到我们解决图中的所有节点：

[![第八步](https://www.baeldung.com/wp-content/uploads/2017/01/step8-300x136.png)](https://www.baeldung.com/wp-content/uploads/2017/01/step8.png)

下表总结了评估步骤中执行的迭代：

| 迭代     | 悬而未决   | 入驻          | 评估节点     | 一个  | 乙       | C        | 丁       | 和       | F        |
| -------- | ---------- | ------------- | ------------ | ----- | -------- | -------- | -------- | -------- | -------- |
| 1        | 一个       | –             | 一个         | 0     | A-10     | A-15     | X-∞      | X-∞      | X-∞      |
| 2        | 乙丙       | 一个          | 乙           | 0     | A-10     | A-15     | B-22     | X-∞      | B-25     |
| 3        | 丙、女、丁 | 一个，乙      | C            | 0     | A-10     | A-15     | B-22     | C-25     | B-25     |
| 4        | D、E、F    | 甲、乙、丙    | 丁           | 0     | A-10     | A-15     | B-22     | D-24     | D-23     |
| 5        | 英语, 英语 | A B C D       | F            | 0     | A-10     | A-15     | B-22     | D-24     | D-23     |
| 6        | 和         | A、B、C、D、F | 和           | 0     | A-10     | A-15     | B-22     | D-24     | D-23     |
| 最后 | –      | 全部      | 没有任何 | 0 | A-10 | A-15 | B-22 | D-24 | D-23 |

 

例如，符号 B-22 表示节点 B 是直接前驱，与节点 A 的总距离为 22。

最后，我们可以计算出从节点A出发的最短路径如下：

-   节点 B：A –> B(总距离 = 10)
-   节点 C : A –> C(总距离 = 15)
-   节点 D：A –> B –> D(总距离 = 22)
-   节点 E ：A –> B –> D –> E(总距离 = 24)
-   节点 F ：A –> B –> D –> F(总距离 = 23)

## 3.Java实现

在这个简单的实现中，我们将图形表示为一组节点：

```java
public class Graph {

    private Set<Node> nodes = new HashSet<>();
    
    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }

    // getters and setters 
}
```

节点可以用name、引用shortestPath的LinkedList 、与源的距离和名为adjacentNodes的邻接列表来描述：

```java
public class Node {
    
    private String name;
    
    private List<Node> shortestPath = new LinkedList<>();
    
    private Integer distance = Integer.MAX_VALUE;
    
    Map<Node, Integer> adjacentNodes = new HashMap<>();

    public void addDestination(Node destination, int distance) {
        adjacentNodes.put(destination, distance);
    }
 
    public Node(String name) {
        this.name = name;
    }
    
    // getters and setters
}
```

adjacentNodes属性用于将直接邻居与边长相关联。这是邻接表的简化实现，比邻接矩阵更适合Dijkstra算法。

至于shortestPath属性，它是一个节点列表，描述了从起始节点计算出的最短路径。

默认情况下，所有节点距离都使用Integer.MAX_VALUE进行初始化，以模拟初始化步骤中所述的无限距离。

现在，让我们实现 Dijkstra 算法：

```java
public static Graph calculateShortestPathFromSource(Graph graph, Node source) {
    source.setDistance(0);

    Set<Node> settledNodes = new HashSet<>();
    Set<Node> unsettledNodes = new HashSet<>();

    unsettledNodes.add(source);

    while (unsettledNodes.size() != 0) {
        Node currentNode = getLowestDistanceNode(unsettledNodes);
        unsettledNodes.remove(currentNode);
        for (Entry < Node, Integer> adjacencyPair: 
          currentNode.getAdjacentNodes().entrySet()) {
            Node adjacentNode = adjacencyPair.getKey();
            Integer edgeWeight = adjacencyPair.getValue();
            if (!settledNodes.contains(adjacentNode)) {
                calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                unsettledNodes.add(adjacentNode);
            }
        }
        settledNodes.add(currentNode);
    }
    return graph;
}
```

getLowestDistanceNode ()方法返回与未解决节点集距离最短的节点，而calculateMinimumDistance()方法将实际距离与新计算的距离进行比较，同时遵循新探索的路径：

```java
private static Node getLowestDistanceNode(Set < Node > unsettledNodes) {
    Node lowestDistanceNode = null;
    int lowestDistance = Integer.MAX_VALUE;
    for (Node node: unsettledNodes) {
        int nodeDistance = node.getDistance();
        if (nodeDistance < lowestDistance) {
            lowestDistance = nodeDistance;
            lowestDistanceNode = node;
        }
    }
    return lowestDistanceNode;
}
private static void CalculateMinimumDistance(Node evaluationNode,
  Integer edgeWeigh, Node sourceNode) {
    Integer sourceDistance = sourceNode.getDistance();
    if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
        evaluationNode.setDistance(sourceDistance + edgeWeigh);
        LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
        shortestPath.add(sourceNode);
        evaluationNode.setShortestPath(shortestPath);
    }
}
```

现在所有必要的部分都已准备就绪，让我们在作为本文主题的示例图上应用 Dijkstra 算法：

```java
Node nodeA = new Node("A");
Node nodeB = new Node("B");
Node nodeC = new Node("C");
Node nodeD = new Node("D"); 
Node nodeE = new Node("E");
Node nodeF = new Node("F");

nodeA.addDestination(nodeB, 10);
nodeA.addDestination(nodeC, 15);

nodeB.addDestination(nodeD, 12);
nodeB.addDestination(nodeF, 15);

nodeC.addDestination(nodeE, 10);

nodeD.addDestination(nodeE, 2);
nodeD.addDestination(nodeF, 1);

nodeF.addDestination(nodeE, 5);

Graph graph = new Graph();

graph.addNode(nodeA);
graph.addNode(nodeB);
graph.addNode(nodeC);
graph.addNode(nodeD);
graph.addNode(nodeE);
graph.addNode(nodeF);

graph = Dijkstra.calculateShortestPathFromSource(graph, nodeA);

```

计算后，为图中的每个节点设置了shortestPath和distance属性，我们可以遍历它们以验证结果是否与上一节中找到的结果完全匹配。

## 4. 总结

在本文中，我们了解了 Dijkstra 算法如何解决 SPP，以及如何在Java中实现它。