## 1. 概述

很多时候在编写软件时，我们需要能够找到图中两点之间的最佳路线。这在电脑游戏中非常常用，但也用于地图软件(例如 Google 地图)，并且也可以在许多其他类型的软件中找到用途。

[Dijkstra 算法](https://en.wikipedia.org/wiki/Dijkstra's_algorithm)是一种非常流行的寻路算法，用于寻找同一图中两点之间的最短路线。

## 2.什么是寻路？

寻路是一种图遍历算法，我们有一个开始和结束节点，需要确定两者之间的最佳路径。这既涉及路线上的步数，也涉及每一步的成本。

这个成本取决于我们正在使用的图表类型——真实世界的地图可能以米为单位测量，而像[文明](https://en.wikipedia.org/wiki/Civilization_(series))这样的电脑游戏可能基于正在移动的单位类型和它所处的地形类型搬过去

甚至还有遍历状态机的寻路应用，图中每个节点对应一个状态，每条边对应一个转移。然后，这可以用于找到两个状态之间最有效的一组转换，例如在解决[Rubik's Cube](https://en.wikipedia.org/wiki/Rubik's_Cube)时。

## 3. Dijkstra 算法

Dijkstra 算法是一种寻路算法，通过图形生成每条路径，然后选择总成本最低的路径。

这是通过迭代计算图中每个节点的距离来工作的，从起始节点开始，一直持续到我们到达结束节点。在每次迭代中，我们都有一个“当前节点”，我们为可以从它到达的每个节点计算一个新的最佳分数。

### 3.1. 流程图

![算法流程图示例2](https://www.baeldung.com/wp-content/uploads/sites/4/2020/08/A-Algorithm-Flowchart-Example2-627x1024-1.png)

### 3.2. 伪代码

现在我们知道该算法将如何工作，它看起来像什么？让我们探索一些更详细地描述该算法的伪代码。

该算法的核心是一个迭代过程，每次都查看当前最好的节点，直到达到我们的目标。

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e87c1d4efcea5b4e82462e7b8fe734c7_l3.svg)

作为其中的一部分，我们需要能够找到当前最好的节点。这是迄今为止我们计算出的分数最低的节点，我们还没有访问过。

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f4dc094ae7f556d219231f5a2ea7fadb_l3.svg)

接下来我们需要计算节点的新分数。这是我们来自的节点的当前分数加上从它到达新节点的边缘成本。

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-10fd17d75f0ffd576dd0690076fd66fb_l3.svg)

最后，一旦我们达到目标，我们就需要实际构建我们的路线。这是我们必须遍历才能从头到尾的节点列表。通过在每个节点上记录最好的前一个节点，我们正在有效地构建这个列表，所以我们只需要将它放在一起。

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9b436e6944d2dcdf679c0eb4c5e5841f_l3.svg)

## 4。总结

在这里，我们了解了一般的寻路算法，以及具体的 Dijkstra 算法。我们还看到了它是如何工作的，所以我们可以看到如何将其应用到我们未来的问题中。