## 1. 概述

在本教程中，我们将概述[Dijkstra](https://www.baeldung.com/java-dijkstra)和[Bellman-Ford](https://www.baeldung.com/cs/bellman-ford)算法。我们将讨论它们的相同点和不同点。

然后，我们将总结何时使用每种算法。

## 2. Dijkstra 算法

Dijkstra 算法是 SSSP(单源最短路径)算法之一。因此，它计算从源节点到[图](https://www.baeldung.com/cs/graph-theory-intro)中所有节点的最短路径。

尽管众所周知 Dijkstra 算法适用于加权[图](https://www.baeldung.com/cs/graphs)，但它适用于边的非负权重。我们将很快解释这样做的原因。

### 2.1. 理论构想

在 Dijkstra 算法中，我们从源节点开始并将其距离初始化为零。接下来，我们将源节点推入成本为零的优先级队列。

之后，我们执行多个步骤。在每一步中，我们提取成本最低的节点，更新其邻居的距离，并在需要时将它们推入优先级队列。当然，每个相邻节点都以其各自的新成本插入，这等于提取节点的成本加上我们刚刚通过的边。

我们继续访问所有节点，直到没有更多节点可从优先级队列中提取。然后，我们返回计算出的距离。

Dijkstra 算法的复杂度为![O(V + E cdot log(V))](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1a917892c63fd5cf673ed60b912bbc72_l3.svg)，其中![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54e215a7a583b4f357a5a627420bcf2f_l3.svg)是节点数，![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-638a7387bd72763290cc777a9b509c38_l3.svg)是图中的边[数](https://www.baeldung.com/java-graphs)。

### 2.2. 概念验证

在 Dijkstra 算法中，我们总是提取成本最低的节点。我们可以在非负边的情况下证明这种方法的正确性。

假设成本最低的节点是![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)。另外，假设我们想要提取![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)成本高于 的其他节点![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)。换句话说，我们有：

![Dist_u < Dist_v](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a5d5a99462806b0f290aa4ff22dbc368_l3.svg)

如果我们先提取，我们不可能![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)以更低的成本到达![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)。这背后的原因是![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)本身成本较高。因此，任何将我们![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)从 出发的路径![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)的成本都等于 的成本加上从到![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)的距离。换句话说，我们试图证明：![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)

![Dist_u > Dist_v + 路径(v, u)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8ea5f3708410cce94c34cad34c256388_l3.svg)

但是，我们已经知道![距离你](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9ac5945aa2967815c46787adfdb03a01_l3.svg)小于![距离_v](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-83908c9e101248a58327be2ff9274953_l3.svg)。由于![路径(u，v)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-67e21f275c99200b0471b6906cffb2e3_l3.svg)具有非负权重，因此最后一个方程永远不会成立。因此，以最小代价提取节点总是最优的。

### 2.3. 限制

因此，我们证明了 Dijkstra 算法的最优性。但是，为此，我们假设所有边都具有非负权重。因此，![路径(u，v)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-67e21f275c99200b0471b6906cffb2e3_l3.svg)也总是非负的。

当处理具有负权重的图时，Dijkstra 算法无法正确计算最短路径。原因是这![路径(u，v)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-67e21f275c99200b0471b6906cffb2e3_l3.svg)可能是负面的，这将使得以较低的成本达到目标成为![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)可能![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)。因此，我们无法证明选择成本最低的节点的最优性。

### 2.4. 的优点和缺点

Dijkstra 算法的主要优点是它的复杂度相当低，几乎是线性的。但是，在处理负权重时，不能使用 Dijkstra 算法。

此外，在处理密集图时![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-638a7387bd72763290cc777a9b509c38_l3.svg)，![V^2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-83a75f1c59979202f3b291dffabdf09c_l3.svg)如果我们需要计算任意一对节点之间的最短路径，则使用 Dijkstra 算法不是一个好的选择。

这样做的原因是 Dijkstra 的时间复杂度是![O(V + E cdot log(V))](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1a917892c63fd5cf673ed60b912bbc72_l3.svg). 由于![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-638a7387bd72763290cc777a9b509c38_l3.svg)几乎相等![V^2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-83a75f1c59979202f3b291dffabdf09c_l3.svg)，所以复杂度变为![O(V + V^2 cdot log(V))](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-131eb702c33c195921c0babd67b7252f_l3.svg)。

当我们需要计算每对节点之间的最短路径时，我们需要调用 Dijkstra 算法，从图中的每个节点开始。因此，总复杂度将变为![O(V^2 + V^3 cdot log(V))](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9b1f848439891e33dda9811b84f887cb_l3.svg)。

这不是一个足够好的复杂度的原因是相同的可以使用[Floyd-Warshall](https://www.baeldung.com/cs/floyd-warshall-shortest-path)算法计算，它的时间复杂度为![O(V^3)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4bf2d180b2023c342819f333345f3dc7_l3.svg). 因此，它可以以较低的复杂性给出相同的结果。

## 3. 贝尔曼-福特算法

与 Dijkstra 算法一样，Bellman-Ford 算法也是 SSSP 算法之一。因此，它计算从起始源节点到加权图中所有节点的最短路径。但是，Bellman-Ford 算法背后的概念与 Dijkstra 的不同。

### 3.1. 理论构想

在 Bellman-Ford 算法中，我们首先用 初始化所有节点的所有距离![infty](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ac47b919d94a96e82a20265519dbcd65_l3.svg)，除了源节点，它被初始化为零。接下来，我们执行![V-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e8f21abcda2302be5c10e7d4cc54fd42_l3.svg)步骤。

在每一步中，我们迭代图中的所有边。对于从节点![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)到的每条边，如果需要![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)，我们更新各自的距离![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)。新的可能距离等于 的距离![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)加上 和 之间的边的权![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)重![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)。

步骤之后![V-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e8f21abcda2302be5c10e7d4cc54fd42_l3.svg)，所有节点的距离都正确，我们停止算法。

Bellman-Ford 算法的时间复杂度为![O(Vc点E)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-41e50ce869e2683af22af1f31924b222_l3.svg)，其中![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54e215a7a583b4f357a5a627420bcf2f_l3.svg)是顶点数，![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-638a7387bd72763290cc777a9b509c38_l3.svg)是图中的边数。这种复杂性的原因是我们执行![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54e215a7a583b4f357a5a627420bcf2f_l3.svg)步骤。在每一步中，我们访问图中的所有边。

### 3.2. 概念验证

Bellman-Ford 算法假定在![V-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e8f21abcda2302be5c10e7d4cc54fd42_l3.svg)执行步骤后，所有节点的距离一定是正确的。让我们来证明这个假设。

图中的任何非循环路径最多只能有![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54e215a7a583b4f357a5a627420bcf2f_l3.svg)节点，这意味着它有![V-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e8f21abcda2302be5c10e7d4cc54fd42_l3.svg)边。如果一条路径有多个![V-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e8f21abcda2302be5c10e7d4cc54fd42_l3.svg)边，则意味着该路径有一个环，因为它有多个![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54e215a7a583b4f357a5a627420bcf2f_l3.svg)节点。因此，它必须多次访问同一个节点。

我们可以保证任何最短路径都不会经过循环。否则，我们可以移除循环，并获得更好的路径。

回到Bellman-Ford算法，我们可以保证![V-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e8f21abcda2302be5c10e7d4cc54fd42_l3.svg)算法经过几步之后，会覆盖所有可能的最短路径。因此，算法保证给出最优解。

### 3.3. 限制

因此，我们证明了 Bellman-Ford 算法给出了 SSSP 问题的最优解。然而，我们证明的第一个限制是通过一个循环可以改进最短路径！

唯一正确的情况是当我们有一个循环的边总和为负时。在那种情况下，我们通常无法计算最短路径，因为我们总是可以通过在循环内多迭代一次来获得更短的路径。因此，最短路径这个词就失去了意义。

然而，即使图有负权重，只要我们没有负环，我们的证明仍然成立。

事实上，我们可以使用 Bellman-Ford 算法来检查是否存在负循环。我们唯一需要做的更新是保存我们在执行![V-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e8f21abcda2302be5c10e7d4cc54fd42_l3.svg)步骤后计算的距离。![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54e215a7a583b4f357a5a627420bcf2f_l3.svg)接下来，我们以与之前相同的方式再执行一个步骤(步骤编号)。

之后，我们检查是否有一个节点获得了更好的路径。如果是这样，那么我们必须至少有一个负循环导致该节点获得更短的路径。

第二个限制与[无向](https://www.baeldung.com/cs/graphs-directed-vs-undirected-graph)图有关。尽管我们确实总能将无向图转换为有向图，但Bellman-Ford 在处理负权重时无法处理无向图。

就 Bellman-Ford 算法而言，如果 和 之间的边![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)具有![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)负权重，我们现在有一个负循环。循环由从![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)到![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)和返回到形成![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)，其权重等于![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)和之间的边的两倍![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)。

### 3.4. 的优点和缺点

Bellman-Ford 算法的主要优点是它能够处理负权重s。但是，Bellman-Ford 算法的复杂度比 Dijkstra 算法大得多。因此，Dijkstra 的算法有更多的应用，因为具有负权重的图通常被认为是一种罕见的情况。

如前所述，Bellman-Ford 算法可以处理具有非负权重的有向图和无向图。但是，它只能处理具有负权重的有向图，只要我们没有负环。

此外，我们可以使用 Bellman-Ford 算法来检查是否存在负循环，如前所述。

## 4. 非负权重示例

让我们以具有非负权重的图为例，看看 Dijkstra 算法如何计算最短路径。

![正权重](https://www.baeldung.com/wp-content/uploads/sites/4/2020/07/Positive_Weights-1024x256.png)

首先，我们推![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)送到优先级队列并将其距离设置为零。接下来，我们提取它，访问它的邻居，并更新它们的距离。之后，我们![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)从优先级队列中提取，因为它的距离最短，更新它的邻居，并将它们推入优先级队列。

下一个要提取的节点是![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)因为它具有最短路径。和以前一样，我们更新它的邻居并在需要时将它们推送到队列中。最后，我们![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)从队列中提取，它现在有正确的最短路径。

值得注意的是，两者![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)的![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)距离都更新了不止一次。在 的情况下![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)，我们首先将它的距离设置为 6。然而，当我们提取 时，我们用距离 5 的更好路径![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)更新了距离。![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)

这同样适用于![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)。当我们提取 时![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)，我们将它的距离更新为等于 9。但是，当我们提取 时![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)，我们找到了到 的更好路径![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)，它的距离等于 8。

在每一步中，我们唯一确定的距离是最低的距离。因此，我们一直从优先级队列中提取它并更新它的邻居。当我们没有更多的节点可以从优先级队列中提取时，所有的最短路径都已经正确计算出来了。

## 5.负权重示例

现在，让我们看一个包含负权重但没有负循环的图形示例。

![负权重](https://www.baeldung.com/wp-content/uploads/sites/4/2020/07/Negative_Weights-1024x254.png)

每条边附近的红色数字显示其各自的顺序。我们执行了三个步骤。在每一步中，我们按顺序迭代边缘并更新距离。

首先，我们更新了![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)到第一条边的距离，更新了![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)到第三条边的距离，更新了![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)到第五条边的距离。接下来，我们更新了![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)与第二条边的距离，并更新了![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)与第五条边的距离。在第三步，我们没有更新任何距离。

我们可以注意到，在![V-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e8f21abcda2302be5c10e7d4cc54fd42_l3.svg)我们已经执行的步骤之后执行任意数量的步骤都不会改变任何距离。因此，我们保证图中不包含负循环。

## 6.负循环示例

现在让我们看一个具有负循环的示例，并解释 Bellman-Ford 算法如何检测负循环。

![负循环](https://www.baeldung.com/wp-content/uploads/sites/4/2020/07/Negative_Cycles_1-1024x254.png)

在第一步中，我们更新了![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)到第一条边的距离、![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)到第三条边的距离和![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)到第五条边的距离。接下来，我们更新了![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)到第二条边的距离和![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)到第五条边的权重。第三，我们![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)从第四条边开始更新权重。

但是，与前面的示例不同，此示例包含一个负循环。负循环是![ACB](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8e7ef5df5d2adffe777a465020c33c29_l3.svg)因为这个循环上的权值之和为-1。

让我们再执行几次迭代，看看 Bellman-Ford 算法是否可以检测到它。

![负循环 2](https://www.baeldung.com/wp-content/uploads/sites/4/2020/07/Negative_Cycles_2-1024x254.png)

第一张图包含执行这些![V-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e8f21abcda2302be5c10e7d4cc54fd42_l3.svg)步骤后产生的距离。如果我们再执行一步，我们会注意到我们更新了![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)到第二条边的距离和![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)到第四条边的距离。如果我们继续执行迭代，我们会注意到节点![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)、![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)和![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)保持较小的距离，因为它们在负循环内。

因此，由于我们至少有一个距离被更新的节点，我们可以声明该图具有负循环。

## 七、比较

看一下Dijkstra算法和Bellman-Ford算法的异同点：

```

```

正如我们所见，Dijkstra 算法在降低时间复杂度方面表现更好。然而，当我们有负权重时，我们必须使用 Bellman-Ford 算法。另外，如果我们想知道图中是否包含负环，Bellman-Ford 算法可以帮助我们。

要记住一件事，在负权重甚至负循环的情况下，Bellman-Ford 算法只能帮助我们处理有向图。

## 八、总结

在本教程中，我们概述了 Dijkstra 和 Bellman-Ford 算法。我们列出了每种算法的所有局限性、优点和缺点。最后，我们比较了他们的优缺点。