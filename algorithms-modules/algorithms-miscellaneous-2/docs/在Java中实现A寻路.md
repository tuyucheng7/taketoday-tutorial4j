## 1. 简介

寻路算法是导航地图的技术，使我们能够找到两个不同点之间的路线。不同的算法有不同的优缺点，通常是在算法的效率和它生成的路由的效率方面。

## 2. 什么是寻路算法？

寻路算法是一种将由节点和边组成的图转换为通过图的路径的技术。这个图可以是任何需要遍历的东西。对于本文，我们将尝试穿越伦敦地铁系统的一部分：

[![截图-2019-11-13-at-06.49.37](https://www.baeldung.com/wp-content/uploads/2019/11/Screenshot-2019-11-13-at-06.49.37.png)](https://www.baeldung.com/wp-content/uploads/2019/11/Screenshot-2019-11-13-at-06.49.37.png)

(“ [London Underground Overground DLR Crossrail map](https://commons.wikimedia.org/wiki/File:London_Underground_Overground_DLR_Crossrail_map.svg) ” by [sameboat](https://commons.wikimedia.org/wiki/User:Sameboat) 已获得 [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/deed.en)许可)

这有很多有趣的组件：

-   我们的起点和终点之间可能有也可能没有直接路线。例如，我们可以直接从“Earl's Court”到“Monument”，而不是“Angel”。
-   每一步都有特定的成本。在我们的例子中，这是站点之间的距离。
-   每个站点仅连接到其他站点的一小部分。例如，“摄政公园”只与“贝克街”和“牛津广场”直接相连。

所有寻路算法都将所有节点(在我们的示例中为站点)及其之间的连接以及所需的起点和终点的集合作为输入。输出通常是一组节点，它们将按照我们需要的顺序让我们从头到尾。

## 3.什么是A？

[A](https://www.baeldung.com/cs/a-star-algorithm)是一种特定的寻路算法，由 Peter Hart、Nils Nilsson 和 Bertram Raphael 于 1968 年首次发表。它通常被认为是在没有机会预先计算路线并且没有内存使用限制的情况下使用的最佳算法。

在最坏的情况下，内存和性能复杂度都可能是O(b^d)，因此虽然它总是会找到最有效的路线，但它并不总是最有效的方法。

A 实际上是[Dijkstra 算法](https://www.baeldung.com/java-dijkstra)的变体，其中提供了额外的信息来帮助选择要使用的下一个节点。这些附加信息不需要是完美的——如果我们已经拥有完美的信息，那么寻路就毫无意义。但它越好，最终结果就会越好。

## 4. A 是如何工作的？

A 算法的工作原理是迭代选择迄今为止的最佳路线，并尝试查看下一步的最佳路线。

使用此算法时，我们需要跟踪几条数据。“开放集”是我们当前正在考虑的所有节点。这不是系统中的每个节点，而是我们可能从中进行下一步的每个节点。

我们还将跟踪系统中每个节点的当前最佳分数、估计总分数和当前最佳先前节点。

作为其中的一部分，我们需要能够计算两个不同的分数。一个是从一个节点到下一个节点的分数。第二种是启发式方法，用于估算从任何节点到目的地的成本。此估计不需要准确，但更高的准确度将产生更好的结果。唯一的要求是两个分数彼此一致——也就是说，它们使用相同的单位。

一开始，我们的开放集由我们的起始节点组成，我们根本没有任何其他节点的信息。

在每次迭代中，我们将：

-   从我们的开放集中选择具有最低估计总分的节点
-   从开放集中删除该节点
-   将我们可以从中到达的所有节点添加到开放集中

当我们这样做时，我们还会计算出从这个节点到每个新节点的新分数，看看它是否比我们目前所获得的有所改进，如果是，那么我们更新我们对该节点的了解。

然后重复此操作，直到我们的开放集中具有最低估计总分的节点是我们的目的地，此时我们已经有了我们的路线。

### 4.1. 实例

例如，让我们从“Marylebone”开始，尝试找到通往“Bond Street”的路。

一开始，我们的开放集只包含“Marylebone”。这意味着这隐含地是我们获得最佳“估计总分”的节点。

我们的下一站可以是“Edgware Road”，成本为 0.4403 公里，也可以是“Baker Street”，成本为 0.4153 公里。然而，“Edgware Road”的方向错误，因此我们从这里到目的地的启发式得分为 1.4284 公里，而“贝克街”的启发式得分为 1.0753 公里。

这意味着在这次迭代之后，我们的开放集由两个条目组成——“Edgware Road”，估计总得分为 1.8687 km，“Baker Street”，估计总得分为 1.4906 km。

我们的第二次迭代将从“贝克街”开始，因为它的估计总分最低。从这里开始，我们的下一站可以是“Marylebone”、“St. 约翰伍德”、“大波特兰街”、摄政公园”或“邦德街”。

我们不会研究所有这些，但让我们以“Marylebone”作为一个有趣的例子。到达那里的成本还是 0.4153 公里，但这意味着总成本现在是 0.8306 公里。此外，从此处到目的地的启发式得分为 1.323 公里。

这意味着估计的总得分将是 2.1536 公里，这 比该节点的先前得分差。这是有道理的，因为在这种情况下，我们不得不做额外的工作才能一事无成。这意味着我们不会认为这是一条可行的路线。因此，“Marylebone”的详细信息不会更新，也不会添加回开放集。

## 5.Java实现

现在我们已经讨论了它是如何工作的，让我们实际实现它。我们将构建一个通用解决方案，然后我们将实施必要的代码以使其适用于伦敦地铁。然后我们可以通过只实现那些特定的部分来将它用于其他场景。

### 5.1. 表示图形

首先，我们需要能够表示我们希望遍历的图。这由两个类组成——单个节点，然后是整个图。

我们将使用一个名为GraphNode的接口来表示我们的各个节点：

```java
public interface GraphNode {
    String getId();
}
```

我们的每个节点都必须有一个 ID。任何其他内容都是特定于此特定图形的，一般解决方案不需要。这些类是没有特殊逻辑的简单JavaBean。

然后，我们的整体图由一个简称为 Graph的类表示：

```java
public class Graph<T extends GraphNode> {
    private final Set<T> nodes;
    private final Map<String, Set<String>> connections;
    
    public T getNode(String id) {
        return nodes.stream()
            .filter(node -> node.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No node found with ID"));
    }

    public Set<T> getConnections(T node) {
        return connections.get(node.getId()).stream()
            .map(this::getNode)
            .collect(Collectors.toSet());
    }
}
```

这将存储我们图中的所有节点，并了解哪些节点连接到哪些节点。然后我们可以通过 ID 获取任何节点，或连接到给定节点的所有节点。

在这一点上，我们能够表示我们希望的任何形式的图形，在任意数量的节点之间具有任意数量的边。

### 5.2. 我们路线上的步骤

接下来我们需要的是通过图形查找路线的机制。

第一部分是在任意两个节点之间生成分数的某种方式。我们将Scorer接口用于下一个节点的分数和对目的地的估计：

```java
public interface Scorer<T extends GraphNode> {
    double computeCost(T from, T to);
}
```

给定一个开始节点和一个结束节点，然后我们得到在它们之间移动的分数。

我们还需要一个围绕我们的节点的包装器来携带一些额外的信息。这不是 GraphNode， 而是RouteNode —— 因为它是我们计算路线中的一个节点，而不是整个图中的一个节点：

```java
class RouteNode<T extends GraphNode> implements Comparable<RouteNode> {
    private final T current;
    private T previous;
    private double routeScore;
    private double estimatedScore;

    RouteNode(T current) {
        this(current, null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    RouteNode(T current, T previous, double routeScore, double estimatedScore) {
        this.current = current;
        this.previous = previous;
        this.routeScore = routeScore;
        this.estimatedScore = estimatedScore;
    }
}
```

与 GraphNode 一样，这些是简单的JavaBean，用于存储当前路由计算的每个节点的当前状态。当我们第一次访问一个节点并且还没有关于它的其他信息时，我们已经为常见情况提供了一个简单的构造函数。

不过，这些也需要是Comparable的，这样我们就可以根据估计的分数对它们进行排序，作为算法的一部分。这意味着添加 compareTo()方法来满足Comparable接口的要求：

```java
@Override
public int compareTo(RouteNode other) {
    if (this.estimatedScore > other.estimatedScore) {
        return 1;
    } else if (this.estimatedScore < other.estimatedScore) {
        return -1;
    } else {
        return 0;
    }
}
```

### 5.3. 找到我们的路线

现在我们可以实际生成我们图表中的路线。这将是一个名为 RouteFinder的类：

```java
public class RouteFinder<T extends GraphNode> {
    private final Graph<T> graph;
    private final Scorer<T> nextNodeScorer;
    private final Scorer<T> targetScorer;

    public List<T> findRoute(T from, T to) {
        throw new IllegalStateException("No route found");
    }
}
```

我们有我们正在寻找路线的图表，以及我们的两个记分器——一个用于下一个节点的确切分数，一个用于我们目的地的估计分数。我们也有一个方法，它将采用开始和结束节点并计算两者之间的最佳路径。

这个方法就是我们的A算法。我们所有的其余代码都在这个方法中。

我们从一些基本设置开始——我们可以考虑作为下一步的节点的“开放集”，以及到目前为止我们访问过的每个节点的地图以及我们对它的了解：

```java
Queue<RouteNode> openSet = new PriorityQueue<>();
Map<T, RouteNode<T>> allNodes = new HashMap<>();

RouteNode<T> start = new RouteNode<>(from, null, 0d, targetScorer.computeCost(from, to));
openSet.add(start);
allNodes.put(from, start);
```

我们的开放集最初只有一个节点——我们的起点。没有前一个节点，到达那里的分数为 0，我们已经估计了它离我们的目的地有多远。

对开放集使用PriorityQueue意味着我们可以根据之前的 compareTo() 方法自动从中获取最佳条目。

现在我们迭代，直到我们用完要查看的节点，或者最好的可用节点是我们的目的地：

```java
while (!openSet.isEmpty()) {
    RouteNode<T> next = openSet.poll();
    if (next.getCurrent().equals(to)) {
        List<T> route = new ArrayList<>();
        RouteNode<T> current = next;
        do {
            route.add(0, current.getCurrent());
            current = allNodes.get(current.getPrevious());
        } while (current != null);
        return route;
    }

    // ...
```

找到目的地后，我们可以通过反复查看前一个节点直到到达起点来构建路线。

接下来，如果我们还没有到达目的地，我们可以算出下一步该做什么：

```java
    graph.getConnections(next.getCurrent()).forEach(connection -> { 
        RouteNode<T> nextNode = allNodes.getOrDefault(connection, new RouteNode<>(connection));
        allNodes.put(connection, nextNode);

        double newScore = next.getRouteScore() + nextNodeScorer.computeCost(next.getCurrent(), connection);
        if (newScore < nextNode.getRouteScore()) {
            nextNode.setPrevious(next.getCurrent());
            nextNode.setRouteScore(newScore);
            nextNode.setEstimatedScore(newScore + targetScorer.computeCost(connection, to));
            openSet.add(nextNode);
        }
    });

    throw new IllegalStateException("No route found");
}
```

在这里，我们正在迭代图中的连接节点。对于其中的每一个，我们都得到了我们拥有的RouteNode——如果需要的话创建一个新的。

然后我们计算这个节点的新分数，看看它是否比我们目前的分数便宜。如果是，那么我们更新它以匹配这条新路线并将其添加到开放集中以供下次考虑。

这就是整个算法。我们不断重复这一点，直到我们达到目标或未能达到目标。

### 5.4. 伦敦地铁的具体细节

到目前为止，我们拥有的是一个通用的 A 探路者，但它缺乏我们确切用例所需的细节。这意味着我们需要GraphNode和 Scorer的具体实现 。

我们的节点是地下车站，我们将使用Station类对它们进行建模：

```java
public class Station implements GraphNode {
    private final String id;
    private final String name;
    private final double latitude;
    private final double longitude;
}
```

名称对于查看输出很有用，纬度和经度用于我们的评分。

在这种情况下，我们只需要Scorer的一个实现。为此，我们将使用[Haversine 公式](https://www.baeldung.com/cs/haversine-formula)来计算两对纬度/经度之间的直线距离：

```java
public class HaversineScorer implements Scorer<Station> {
    @Override
    public double computeCost(Station from, Station to) {
        double R = 6372.8; // Earth's Radius, in kilometers

        double dLat = Math.toRadians(to.getLatitude() - from.getLatitude());
        double dLon = Math.toRadians(to.getLongitude() - from.getLongitude());
        double lat1 = Math.toRadians(from.getLatitude());
        double lat2 = Math.toRadians(to.getLatitude());

        double a = Math.pow(Math.sin(dLat / 2),2)
          + Math.pow(Math.sin(dLon / 2),2)  Math.cos(lat1)  Math.cos(lat2);
        double c = 2  Math.asin(Math.sqrt(a));
        return R  c;
    }
}
```

我们现在几乎拥有计算任意两对站之间路径所需的一切。唯一缺少的是它们之间的连接图。

让我们用它来绘制路线。我们将从 Earl's Court 到 Angel 生成一个。这有许多不同的旅行选择，至少有两条管线：

```java
public void findRoute() {
    List<Station> route = routeFinder.findRoute(underground.getNode("74"), underground.getNode("7"));

    System.out.println(route.stream().map(Station::getName).collect(Collectors.toList()));
}
```

这会生成伯爵府 -> 南肯辛顿 -> 格林公园 -> 尤斯顿 -> 天使的路线。

许多人会采取的明显路线可能是伯爵 -> 纪念碑 -> 天使，因为它的变化较少。相反，这采取了更直接的路线，尽管这意味着更多的变化。

## 六. 总结

在本文中，我们了解了 A 算法是什么、它是如何工作的，以及如何在我们自己的项目中实现它。为什么不接受并扩展它以供你自己使用呢？

也许尝试扩展它以考虑管线之间的交汇处，看看这对所选路线有何影响？