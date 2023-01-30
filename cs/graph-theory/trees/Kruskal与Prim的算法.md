## 1. 概述

在图论中，计算最小生成树(MST)的算法主要有两种：

1.  [克鲁斯卡尔](https://www.baeldung.com/java-spanning-trees-kruskal)算法
2.  [普里姆](https://www.baeldung.com/java-prim-algorithm)算法

在本教程中，我们将解释两者并了解它们之间的区别。

## 2. 最小生成树

生成树是形成树并连接图中所有节点的一组边。 最小生成树是具有最低成本(边权重之和)的生成树。

另外，值得注意的是，由于它是一棵树，因此MST 是在谈论无向连通图时使用的术语。

让我们考虑一个例子：

![MST](https://www.baeldung.com/wp-content/uploads/sites/4/2020/07/MST-1024x440-1.png)

正如我们所见，红色边形成了最小生成树。MST 的总成本是所取边的权重之和。在给定的示例中，所提供的 MST 的成本为 2 + 5 + 3 + 2 + 3 + 3 = 18。

然而，这并不是唯一可以形成的 MST。例如，我们可以取![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-638a7387bd72763290cc777a9b509c38_l3.svg)和之间的边，而不是取和之间的边，成本将保持不变。由此，我们可以注意到不同的 MST 是交换具有相同权重的不同边缘的原因。![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)![丁](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c10ec9debc8ec5dce4c3c5887557202d_l3.svg)![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)

因此，算法检查具有相同成本的边的不同顺序会导致不同的 MST。然而，当然，所有这些 MST 的成本肯定是相同的。

## 3. Kruskal 算法

### 3.1. 大意

Kruskal 算法背后的主要思想是根据边的权重对边进行排序。之后，我们开始根据较低的权重一条一条地取边。如果我们取一条边，并导致形成一个环，则这条边不包含在 MST 中。否则，该边包含在 MST 中。

问题是检测周期不够快。为此，我们可以使用不相交的集合数据结构。不相交的集合数据结构使我们能够轻松地将两个节点合并为一个组件。此外，它还允许我们快速检查两个节点之前是否合并过。

因此，在添加一条边之前，我们首先检查这条边的两端是否已经合并过。如果是这样，我们就不会将边缘包含在 MST 中。否则，我们将边添加到 MST 并将两个节点合并到不相交的集合数据结构中。

### 3.2. 算法

在这个算法中，我们将使用一个名为![dsu](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-154108b61789dc9e4cee935a8cc3d06f_l3.svg)which 的数据结构，它是我们在 3.1 节中讨论过的不相交集数据结构。查看 Kruskal 算法的伪代码。

```

```

首先，我们根据权重对边列表进行升序排序。其次，我们遍历所有边缘。对于每条边，我们检查它的末端之前是否合并过。如果是这样，我们就忽略这条边。

否则，我们会增加 MST 的总成本并将这条边添加到生成的 MST 中。此外，我们将这条边的两端合并到不相交的集合数据结构中。

最后，我们只返回计算出的 MST 和所取边的总成本。

Kruskal 算法的复杂度为![O(E cdot log(V))](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-99a13ad0f714a1fc5b1baba6b6500882_l3.svg)，其中![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-638a7387bd72763290cc777a9b509c38_l3.svg)是边![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54e215a7a583b4f357a5a627420bcf2f_l3.svg)数， 是图中的顶点数。这种复杂性的原因是由于排序成本。

### 3.3. 分析

由于复杂度为![O(E cdot log(V))](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-99a13ad0f714a1fc5b1baba6b6500882_l3.svg)，因此 Kruskal 算法更适合用于边数不多的稀疏图 。

然而，由于我们正在逐一检查所有边，这些边是根据权重升序排列的，这使我们能够很好地控制生成的 MST。由于不同的MST来自不同的边，代价相同，所以在Kruskal算法中，所有这些边在排序时都是一个接一个地定位。

因此，当两条或多条边具有相同的权重时，我们可以完全自由地对它们进行排序。我们使用的顺序会影响生成的 MST。当然，无论具有相同权重的边的顺序如何，成本总是相同的。但是，我们添加的边![mst](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-aaf888c07000e339db7b93eaa1ca0121_l3.svg)可能会有所不同。

另一个需要考虑的方面是 Kruskal 算法相当容易实现。唯一的限制是具有良好的不相交集数据结构和良好的排序功能。

## 4. 普里姆算法

### 4.1. 大意

基本上，Prim 算法是[Dijkstra](https://www.baeldung.com/java-dijkstra)算法的修改版本。首先，我们选择一个节点作为起点并将其所有邻居添加到[优先级队列](https://www.baeldung.com/java-queue)中。

之后，我们执行多个步骤。在每一步中，我们都会使用权重最低的边来提取我们能够到达的节点。因此，优先级队列必须包含节点和让我们到达该节点的边的权重。此外，它必须根据传递的权重对其中的节点进行排序。

对于每个提取的节点，我们将其添加到生成的 MST 中并更新 MST 的总成本。此外，我们也将其所有邻居添加到队列中。

为了获得更好的复杂度，我们可以确保每个节点在队列中只出现一次。例如，我们可以使用一个函数![添加或更新](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5d197659b0001a9206831b7e42fef9c7_l3.svg)来获取具有权重的节点和引导我们到达该节点的边。

如果该节点已经在队列中，并且新权重优于存储的权重，则该函数会删除旧节点并添加新节点。否则，如果该节点不在队列中，它只需将它与给定的权重一起添加。

### 4.2. 算法

考虑以下 Prim 算法的伪代码。

```

```

一开始，我们将源节点添加到队列中，权重为零且没有边。我们使用![披](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-21f36758b04341c7980aa18b13ced720_l3.svg)符号表示我们在这里存储一个空值。此外，我们将总成本初始化为零，并将所有节点标记为尚未包含在 MST 中。

之后，我们执行多个步骤。在每一步中，我们从队列中提取权重最低的节点。对于每个提取的节点，我们通过提取的边的权重增加 MST 的成本。此外，如果提取节点的边缘存在，我们将其添加到生成的 MST 中。

当我们完成处理提取的节点时，我们迭代它的邻居。如果邻居尚未包含在生成的 MST 中，我们使用该![添加或更新](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5d197659b0001a9206831b7e42fef9c7_l3.svg)函数将此邻居添加到队列中。此外，我们添加边的权重和边本身。

Prim 算法的复杂度为![O(E + V cdot log(V))](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a88ebdd2783baf437cba68516478b2eb_l3.svg)，其中![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-638a7387bd72763290cc777a9b509c38_l3.svg)是边![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54e215a7a583b4f357a5a627420bcf2f_l3.svg)数， 是图中的顶点数。

### 4.3. 分析

Prim 算法的优点是复杂度高，优于 Kruskal 算法。因此，Prim 算法在处理有很多边的稠密图时很有用。

然而，Prim 的算法不允许我们在出现多个具有相同权重的边时对选择的边进行太多控制。原因是只有到目前为止发现的边存储在队列中，而不是像 Kruskal 算法中的所有边。

此外，与 Kruskal 的算法不同，Prim 的算法更难实现。

## 5.比较

让我们强调一下这两种算法之间的一些主要区别。

```

```

正如我们所看到的，Kruskal 算法更适合使用，因为它更容易实现并且可以最好地控制生成的 MST。然而，Prim 的算法提供了更好的复杂性。

## 六，总结

在本教程中，我们解释了计算图的最小生成树的两种主要算法。首先，我们解释了MST这个术语。其次，我们介绍了 Kruskal 和 Prim 的算法，并对每个算法进行了分析。

第三，我们通过提供两种算法之间的比较来总结。