## 1. 概述

在[图论中](https://www.baeldung.com/cs/graphs)，SSSP(单源最短路径)算法解决了寻找从起始节点(源)到图中所有其他节点的最短路径的问题。属于该定义的主要算法是[广度优先搜索 (BFS)](https://www.baeldung.com/java-breadth-first-search)和[Dijkstra](https://www.baeldung.com/java-dijkstra)算法。

在本教程中，我们将对这两种算法进行一般性解释。此外，我们将展示它们之间的差异以及何时使用它们。

## 2. 通用算法

两种算法具有相同的总体思想。我们从源节点开始，逐个节点地探索图表。在每一步中，我们总是去到离源节点最近的节点。

让我们看一个更好地解释通用算法的流程图：

![SSSP算法](https://www.baeldung.com/wp-content/uploads/sites/4/2020/05/SSSP-Algorithm-1024x339.png)

 

 

如你所见，前两步是用较大的值初始化距离并将源节点添加到队列中。接下来，我们执行迭代，直到队列变空。在每次迭代中，我们从队列中提取一个与源节点距离最短的节点。

之后，我们访问提取节点的所有邻居并检查我们能够达到的新距离。如果新的距离比旧的好，我们更新这个节点的距离并将它推到队列中。最后，算法继续执行另一次迭代，直到队列变空。

算法结束后，我们将得到从源节点到图中所有其他节点的最短路径。

但是，由于[图形](https://www.baeldung.com/java-graphs)是加权的或未加权的，我们不能对这两种情况使用完全相同的算法。因此，我们有两种算法。BFS 计算未加权图中的最短路径。另一方面，Dijkstra 算法在加权图中计算相同的东西。

## 3. BFS 算法

在处理未加权的图时，我们总是关心减少访问边的数量。因此，我们确信源节点的所有直接邻居的距离都等于 1。接下来我们可以确定的是源节点的所有第二个邻居的距离都等于二，依此类推。

在我们覆盖图中的所有节点之前，这个想法一直有效。唯一的例外是如果我们到达了一个之前访问过的节点。在这种情况下，我们应该忽略它。原因是我们之前一定是用较短的路径到达的。

值得注意的是，在所有边具有相同权重的加权图中，BFS 算法可以正确计算最短路径。原因是它关心减少访问边的数量，这在所有边的权重相等的情况下是正确的。

让我们考虑需要对我们的通用算法进行的更新。我们遵循的主要方法是始终从我们之前到达的节点开始。[这看起来类似于我们可以在简单队列](https://www.baeldung.com/java-queue)中找到的 FIFO(先进先出)概念。因此，我们使用带有 BFS 的简单队列。我们可以看到我们不应该对我们的通用算法做更多的更新。

由于我们使用的是普通队列，因此![O(1)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-66c97a4dfb9f2e2983629033366d7018_l3.svg)推送和弹出操作具有时间复杂度。因此，总时间复杂度为![O(V+E)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-126b48fbb229921e24629cf2c5e4b2d9_l3.svg)，其中![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54e215a7a583b4f357a5a627420bcf2f_l3.svg)是图中的顶点数，![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-638a7387bd72763290cc777a9b509c38_l3.svg)是图中的边数。

## 4. Dijkstra 算法

对于加权图，相邻节点不一定总是具有最短路径。但是，任何更短的路径都无法到达具有最短边的邻居。原因是所有其他边都具有更大的权重，因此单独穿过它们会增加距离。

Dijkstra 的算法使用这个想法提出了一种贪婪的方法。在每一步中，我们选择路径最短的节点。我们固定此成本并将此节点的邻居添加到队列中。因此，队列必须能够根据最小成本对其中的节点进行排序。我们可以考虑使用[优先级队列](https://www.baeldung.com/java-queue#priority_queues)来实现。

我们还有一个问题。在未加权的图中，当我们从不同的路径到达一个节点时，我们确信第一次应该有最短路径。在加权图中，情况并非总是如此。如果我们到达了路径较短的节点，我们必须更新它的距离并将它添加到队列中。这意味着可以多次添加同一个节点。

因此，我们必须始终将提取的节点的成本与其实际存储的成本进行比较。如果提取的距离大于存储的距离，则意味着该节点是在早期阶段添加的。后来，我们一定是找到了一条更短的路径并更新了它。我们还再次将节点添加到队列中，因此可以安全地忽略此提取。

因为我们只访问每个节点的邻居一次，所以我们也只访问一次边。此外，我们可以使用时间复杂度![O(log n)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c4e696c3d48ee360ea28fbb80622d356_l3.svg)为 push 和 pop 操作的优先级队列。因此，总时间复杂度为![O(V + E(log V))](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9d5a3172a32156eaa8cc1da2e037f12c_l3.svg)。

## 5.例子

看看下图。它包含将 BFS 算法应用于简单的未加权图的示例。字母对应节点索引，数字表示执行BFS算法后存储的距离：

 

![SSSP BFS](https://www.baeldung.com/wp-content/uploads/sites/4/2020/05/SSSP-BFS-1024x563.png)

 

我们可以清楚地看到算法逐层访问节点。首先，![(甲、乙、丙)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5e490d0a9cd96fd5a0f539ad304ac9b4_l3.svg)在第 1 层访问所有邻居。接下来，在第 2 层访问所有第二个邻居![(四，五)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-45328f9fcadbcb94798a6a5fe30f10b6_l3.svg)，依此类推。结果，我们能够计算出从源节点开始到所有节点的最短路径![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)。

现在，让我们看一下将 Dijkstra 算法应用于同一图的加权版本的结果：

 

![SSSP 迪杰斯特拉](https://www.baeldung.com/wp-content/uploads/sites/4/2020/05/SSSP-Dijkstra-1024x563.png)

 

虽然 node![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)离源 一步之遥，但![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)Dijkstra 的算法首先探索 node![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)因为边缘 to![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)的成本最低。

接下来，![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)它从 找到一条到节点 的较短路径![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)，这是算法存储的路径。

我们还可以注意到，虽然它离 一步之遥![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)，但我们并没有![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)直接访问。![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)原因是和之间的边![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)具有非常大的成本。幸运的是，我们能够![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)从比直接边缘更短的路径到达。

## 6.比较

下表显示了两种算法之间的总结比较：

```

```

## 七、总结

在本教程中，我们介绍了 BFS 和 Dijkstra 算法的通用算法。接下来，我们解释了两种算法之间的主要异同。

之后，我们查看了将这两种算法应用于同一图的未加权和加权版本的结果。

最后，我们对两种算法进行了比较。