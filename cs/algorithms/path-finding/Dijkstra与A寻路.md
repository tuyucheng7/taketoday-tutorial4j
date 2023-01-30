## 1. 概述

Dijkstra 算法和 A 是众所周知的在[图](https://www.baeldung.com/cs/graph-theory-intro)中搜索最佳路径的技术。在本教程中，我们将讨论它们的相同点和不同点。

## 2.寻找最优路径

在 AI 搜索问题中，我们有一个图，其节点是 AI 代理的状态，边对应于代理从一种状态到另一种状态必须采取的动作。任务是找到从起始状态到通过目标测试的状态的最佳路径。

例如，起始状态可以是棋盘上棋子的初始位置，任何白方获胜的状态都是其搜索的目标状态——黑方的目标状态也是如此。

当边有成本时，一条路径的总成本是其组成边成本的总和，那么起始状态和目标状态之间的最优路径是成本最低的。我们可以使用 Dijkstra 算法、Uniform-Cost Search 和 A 等算法找到它。

## 3. Dijkstra 算法

[Dijkstra 算法](https://www.baeldung.com/cs/dijkstra)的输入包含图形![G=(V, E, c)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cfa7d0d5f25e2d4b9e147abc4c701b62_l3.svg)、源顶点![小号在V](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-081b31a78979737e394a3a8f479e95c4_l3.svg)和目标节点![吨在V](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-72e17330ef8da697f6f198b65aa67a30_l3.svg)。![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54e215a7a583b4f357a5a627420bcf2f_l3.svg)是顶点集，![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-638a7387bd72763290cc777a9b509c38_l3.svg)是边集，是边![c(u, v)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-776af0e4701d3ff674f11da13fd1ba95_l3.svg)的成本![(u, v) 在 E](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0e16bae0985c34b7ea0114f70c8bf541_l3.svg)。与 AI 搜索问题的联系很简单。![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54e215a7a583b4f357a5a627420bcf2f_l3.svg)对应于状态，对应于![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)起始状态，目标测试是检查是否等于![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fd9cb27edab3f0a8a249bc80cc9c6ee2_l3.svg)。

Dijkstra 将![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54e215a7a583b4f357a5a627420bcf2f_l3.svg)图中的顶点集分成两个不相交且互补的子集：![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)和![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)。包含我们找到的最佳路径的顶点。相反，包含我们目前不知道其最佳路径但具有其实际成本上限的节点。最初，Dijkstra 将所有顶点放入并将上限设置为for every 。 ![boldsymbol{S}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-75a48496cdfd8146879f0f5c84cb878b_l3.svg)![boldsymbol{s}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a55a318d9744b4e7ffd2bf35dc0e975b_l3.svg)![boldsymbol{Q}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4def3e98030ed191b1837ac284d5d5e1_l3.svg)![boldsymbol{g}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6b20d394d51641b851f953f04daa0458_l3.svg)![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)![克(你)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7df66d6f3419e7f91c8ef82ec6094979_l3.svg)![+infty](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7d024847687df5ed74470976a0770de7_l3.svg)![你在V](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-64ea49a79654f14746fcefa38d54f179_l3.svg)

Dijkstra在每一步将一个顶点从 移动到 ，直到它![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)移动到。它选择删除具有最小值的节点。这就是为什么通常是优先队列。![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fd9cb27edab3f0a8a249bc80cc9c6ee2_l3.svg)![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e88010d25c51c0c42c505ee1004ed182_l3.svg)![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)

当从 中移除![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)时![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)，该算法检查所有向外的边缘![(u, v) 在 E](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0e16bae0985c34b7ea0114f70c8bf541_l3.svg)并检查![g(v)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d82f6d140605b1feaefbf1732200a565_l3.svg)是否![g(u) + c(u,v) < g(v)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c1e5d16b4578755282b145532c7452a3_l3.svg)。如果是这样，Dijkstra 找到了一个更紧的上限，所以它设置![g(v)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d82f6d140605b1feaefbf1732200a565_l3.svg)为![g(u) + c(u,v)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f0bd94e518416fc496f291fd9561d121_l3.svg)。此步骤称为放松操作。

该算法的不变性是，无论何时选择![boldsymbol{uin Q}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5908844ecfa3c0cab7e1cd4f3a635390_l3.svg)松弛其边缘并将其移至![boldsymbol{S}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-75a48496cdfd8146879f0f5c84cb878b_l3.svg)，![boldsymbol{g(u)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-db4939f2b30993d5255639e828d21549_l3.svg)都等于从![boldsymbol{s}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a55a318d9744b4e7ffd2bf35dc0e975b_l3.svg)到的最佳路径的成本![boldsymbol{u}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9c7105ad71e0c2e50f7172862e65018e_l3.svg)。

## 4.一个

在 AI 中，许多问题的状态图非常大，以至于它们无法容纳主存储器，甚至是无限的。所以，我们不能使用 Dijkstra 来寻找最优路径。相反，我们使用[UCS](https://www.baeldung.com/cs/uniform-cost-search-vs-dijkstras)。它在[逻辑上等同于 Dijkstra](https://www.aaai.org/ocs/index.php/SOCS/SOCS11/paper/viewFile/4017/4357)，但可以处理无限图。

但是，由于 UCS 在统一成本的波浪中系统地探索状态，因此它可能会扩展不必要的更多状态。例如，让我们假设 UCS 已经达到一个状态，其路径成本最优![C'](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3ebb4f9f345fb4da84e1be816e58d89f_l3.svg)，其直接后继是所寻求的目标状态(具有最优成本![C^](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c54c57203284cb06ef2bf376b46991c5_l3.svg))。根据其搜索的性质，UCS 只有在访问路径成本在![C'](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3ebb4f9f345fb4da84e1be816e58d89f_l3.svg)和之间的所有状态后才能达到目标![C^](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c54c57203284cb06ef2bf376b46991c5_l3.svg)。问题是可能有很多这样的状态。

[A 算法](https://www.baeldung.com/cs/a-star-algorithm)旨在避免这种情况。它不仅考虑一个状态离起点有多远，还考虑它离目标有多近。后一个成本是未知的，要确定它，我们必须找到所有节点和最近目标之间成本最低的路径，而这与我们目前正在解决的问题一样困难！因此，我们只能使用或多或少精确的成本估算来达到最接近的目标。

这些估计可通过我们称为[启发式](https://www.baeldung.com/cs/heuristics)的函数获得。这就是为什么我们说 A 是有信息的，而我们称 UCS 为无信息搜索算法。后者仅使用问题定义来解决问题，而前者使用启发式方法提供的额外知识。

### 4.1. 启发式

启发式![boldsymbol{h}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e72e0c36890a0312cf17c31f231e5fcf_l3.svg)是一种函数，它接收状态并输出从到最近目标![boldsymbol{u}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9c7105ad71e0c2e50f7172862e65018e_l3.svg)的最佳路径成本的估计值。![boldsymbol{u}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9c7105ad71e0c2e50f7172862e65018e_l3.svg)例如，如果状态表示 2D 地图上的点，则欧几里德和曼哈顿距离是启发式的良好候选者：

![小时](https://www.baeldung.com/wp-content/uploads/sites/4/2021/10/heur.jpg)

然而，启发式的使用并不是 Dijkstra 和 A 之间的唯一区别。

### 4.2. 边境

A 与 UCS 的不同之处仅在于其边界的排序。UCS 按值对边界中的节点进行排序![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e88010d25c51c0c42c505ee1004ed182_l3.svg)。它们表示从起始状态到边界节点状态的路径成本。在 A 中，我们通过 对边界进行排序![boldsymbol{g+h}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b5771db2b9083b4ae5edf55dee0a6fd8_l3.svg)。如果![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)是边界中的任意节点，我们将 的值解释![g(u)+h(u)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e171fa855b2513f5d121de06f2d6213e_l3.svg)为连接起点和目标并通过![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)的状态的最优路径的估计成本。

现在，可能会出现混淆，因为我们定义![H](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2ce27f7d2d82e3b238176ec7e7ee9118_l3.svg)为将状态作为其输入但现在给它一个搜索节点。区别只是技术上的，因为我们可以定义![h(u)=h(u.state)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-461c0b48dcf2f0f22d3a4fb68ca4f225_l3.svg).

## 5.假设的差异

Dijkstra 有两个假设：

-   该图是有限的
-   边缘成本是非负的

第一个假设是必要的，因为 Dijkstra 将所有节点都![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)放在了开头。如果第二个假设不成立，我们应该使用[Bellman-Ford 算法](https://www.baeldung.com/cs/bellman-ford)。这两个假设确保 Dijkstra 始终终止并返回最佳路径或从开始状态无法到达目标的通知。

A是不同的。以下两个假设对于算法总是终止是必要的：

-   边缘具有严格的正成本![boldsymbol{geq varepsilon > 0}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f85e67f176358ff331cc1f60774adb0c_l3.svg)
-   状态图是有限的，或者从一开始就可以达到目标状态

因此，如果图的所有边都是正的并且存在从起点到目标的路径，则 A 可以处理无限图。但是，即使在有限图中也可能没有零成本边。此外，A 还应满足其他要求，以便它仅返回最佳路径。我们称这样的算法为最优。

### 5.1. A 的最优性

首先，我们应该注意到 A 有两种形式：[类树搜索 (TLS) 和图搜索 (GS)](https://www.baeldung.com/cs/graph-search-vs-tree-like-search)。TLS 不检查重复状态，而 GS 会检查。

如果我们不检查重复状态并使用 TLS A，那么![boldsymbol{h}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e72e0c36890a0312cf17c31f231e5fcf_l3.svg)算法应该是最优的。如果启发式从不高估达到目标的成本，我们就说它是[可接受的。](http://aima.cs.berkeley.edu/)因此，如果是从到目标![c^(u)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6fceb4e5cbea731bc85fda5761a3920b_l3.svg)的最佳路径的实际成本，那么对于任何都是可接受的。即使是不可接受的启发式方法也可能会引导我们找到最佳路径，甚至为我们提供最佳算法，但这并不能保证。![美国](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1d8767a20dac2ef4fc4004902f89b1ce_l3.svg)![H](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2ce27f7d2d82e3b238176ec7e7ee9118_l3.svg)![h(u) leq c^(u)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-34e121cff8fc1c2fda3e6b634e4ee224_l3.svg)![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)

如果我们想避免重复状态，我们使用 GS A，并且为了使其最优，我们必须使用一致的启发式。如果启发式算法![H](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2ce27f7d2d82e3b238176ec7e7ee9118_l3.svg)满足任何节点![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)及其子节点的以下条件，则该启发式算法是一致的![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)：

```
  
```

其中是状态图![c(u,v)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cfc668556601e5de02aa8a7a148c1ef8_l3.svg)中边的成本。![u.staterightarrow v.state](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b5dee2a7e74d714bbcaeb3ef5f1fa382_l3.svg)一致的启发式总是可以接受的，但反之则不一定成立。

## 6.搜索树和搜索轮廓

Dijkstra 算法和 A 都在图上叠加了搜索树。 由于树中的路径是唯一的， 因此搜索树中的每个节点都代表一个状态和一条到该状态的路径。因此，我们可以说这两种算法在执行过程中的每个点都维护着从起始状态开始的路径树。然而，Dijkstra 和 A 的不同之处在于它们在树中包含节点的顺序。

我们可以通过在图表上绘制搜索波前来可视化差异。![[C', C'']](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3a4aba0c89bf6d2764d4face16eeda53_l3.svg)我们将每个 wave 定义为添加到树中时路径成本在范围内的节点集![C'](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3ebb4f9f345fb4da84e1be816e58d89f_l3.svg)，我们![C''](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7f952906ad0119eb9f1b22687e733548_l3.svg)在其中预先选择边界值。如果我们适当地选择增量值![C_1, C_2, ldots](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6dbf63c66a2e5bf440838350ec5311cb_l3.svg)来定义波浪，它们将向我们展示搜索是如何通过图表进行的。

### 6.1. 迪克斯特拉

搜索树的根是![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)，它的节点是 中的顶点![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)。由于不变性，我们知道节点代表最优路径。边的非负性假设以及选择成本最低的节点从 移动![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)到的策略![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)确保了 Dijkstra 的另一个属性。对于任何，它可以移动到![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)其最佳路径成本高于![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)仅在移动其最佳路径成本![leq C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-589bf9c0256875a7cc53f1cc86744468_l3.svg)为的所有状态之后的状态![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)。

因此，在 Dijkstra 中分隔波浪的等高线看起来或多或少像均匀的圆圈：

![均匀的轮廓](https://www.baeldung.com/wp-content/uploads/sites/4/2021/10/uniform_contours.jpg)

因此，Dijkstra 的搜索树在图导出的拓扑中的所有方向上均匀生长。

### 6.2. 一种

UCS 和 Dijskra 在状态图中向各个方向传播并具有统一的轮廓，而 A 偏向于某些方向而不是其他方向。在具有相同![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e88010d25c51c0c42c505ee1004ed182_l3.svg)值的两个节点之间，A 更喜欢具有更好![H](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2ce27f7d2d82e3b238176ec7e7ee9118_l3.svg)值的节点。因此，启发式在目标状态的方向上拉伸轮廓：

![拉伸轮廓](https://www.baeldung.com/wp-content/uploads/sites/4/2021/10/stretched_contours.jpg)

## 7.复杂性的差异

在这里，我们将比较 Dijkstra 算法和 A 的空间和时间复杂度

### 7.1. 迪克斯特拉

最坏情况下的时间复杂度取决于[图的稀疏性](https://www.baeldung.com/cs/graphs-sparse-vs-dense)和要实现的数据结构![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)。例如，在稠密图中![|E|=O(|V|^2)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3b110aa9dacd2ae50f3aaeb61899cc7f_l3.svg)，并且由于 Dijkstra 对每条边检查两次，因此其最坏情况下的时间复杂度也是![O(|V|^2)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7c4a9e68907fa69a1aef77a40c300ff6_l3.svg)。

但是，如果图是稀疏的，![|和|](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1ebfd1cf570f822ea7191e0e88b785de_l3.svg)则无法与![|V|^2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b9fd7a36c1b77bff62a7d410ff30c5eb_l3.svg). 有了斐波那契堆![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)，时间复杂度就变成了![O(|E|+|V|log |V|)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5c5ecb9a36fd44155f348ff5458eeabd_l3.svg)。

由于![Qcup S = V](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d9da06a14e1e6223ef7cfff1c0c168e3_l3.svg)，空间复杂度为![O(|V|)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6c282d77e999a9a590196468c6472e55_l3.svg)。

### 7.2. 一种

GS A的[时间和空间复杂性](https://www.baeldung.com/cs/time-vs-space-complexity)受状态图大小的限制。在最坏的情况下，A 将需要![O(|V|)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6c282d77e999a9a590196468c6472e55_l3.svg)内存并具有![O(|V|+|E|)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8876640eb9846566c971873493590257_l3.svg)时间复杂度，类似于 Dijkstra 和 UCS。然而，这是最坏的情况。实际上，A 的搜索树比 Dijkstra 和 UCS 的搜索树小。这是因为启发式算法通常会修剪 Dijkstra 在同一问题上生成的树的大部分。由于这些原因，A 专注于前沿中有前途的节点，并比 Dijkstra 或 UCS 更快地找到最佳路径。

TLS A 的最坏情况时间为![Oleft(b^{leftlfloor frac{C^}{varepsilon}rightrfloor + 1}right)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0d60e6cc873ad6c47530254d82b4f704_l3.svg)，其中![b](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ad69adf868bc701e561aa555db995f1f_l3.svg)是分支因子的上限，![C^](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c54c57203284cb06ef2bf376b46991c5_l3.svg)是最优路径的成本，![varepsilon>0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d571edd36a6c5638344acfa4e3445b2e_l3.svg)是最小边成本。然而，它的有效复杂度在实践中并没有那么糟糕，因为 A 到达的节点更少。TLS A 的空间复杂度也是如此。

## 8.算法的层次结构

在某种程度上，Dijkstra 是 A 的一个实例。如果我们使用始终返回 0 的简单启发式算法，A 将简化为 UCS。但是，UCS 在具有相同搜索树的意义上等同于 Dijkstra。因此，我们可以使用![h(cdot)=0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ae138e3ac54d8d122685ee5e6ed0f6a7_l3.svg)用作启发式的 A 来模拟 Dijkstra。

## 9.例子

让我们看看 Dijkstra 和 A 如何在下图中找到最佳![开始](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-795e60f0ae09708b0b6ea0f736e7b65a_l3.svg)路径![目标](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1fe8d9767faa75ceda9da2de19b1c1f3_l3.svg)：

![图2](https://www.baeldung.com/wp-content/uploads/sites/4/2021/10/graph-2.jpg)

Dijkstra 的搜索树横跨整个图：

![是树](https://www.baeldung.com/wp-content/uploads/sites/4/2021/10/DA_tree-1.jpg)

但是，如果我们使用以下启发式估计：

```
  
```

A 树将只包含最佳路径：

![一棵星树](https://www.baeldung.com/wp-content/uploads/sites/4/2021/10/A_star_tree.jpg)

这意味着 A 不会扩展任何不必要的节点！

## 10.总结

所以，这是 Dijkstra 与 A 的总结。

```

```

正如我们所见，A 的效率源于所使用的启发式函数的属性。因此，A 的实现需要额外的步骤来设计质量启发式。它必须通过图形有效地引导搜索，但计算量也很轻。

## 11.总结

在本文中，我们讨论了 Dijkstra 算法和 A。我们介绍了它们的差异并解释了为什么后者在实践中更快。