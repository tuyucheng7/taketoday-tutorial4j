## 1. 概述

在本教程中，我们将解释 Dijkstra 算法中边缘松弛的机制。松弛边是图论领域各种最短路径算法的重要技术。

## 2.寻找最短路径

寻找最短路径最著名的应用是在导航中，在导航中找到地图上两个位置之间的最短路径至关重要：

![迪克斯特拉放松](https://www.baeldung.com/wp-content/uploads/sites/4/2021/09/dijkstra_relaxing.drawio.png)

在此图中，我们看到了一个非常简单的最短路径问题，“东京、北京、纽约”是东京和纽约之间的最短路径。

寻找最短路径的成本可能很高。想象一下，在导航系统中找到两个城市之间的最短路径，起点和目标位置之间有多个城市和街道。[Dijkstra 算法](https://www.baeldung.com/cs/dijkstra)是一种非常著名的寻找最短路径的方法，因为它需要的资源非常少。它适用于每个没有[负边估值](https://www.baeldung.com/cs/dijkstra-negative-weights)的有向图。

## 3. 通过松弛边改进路径

Dijkstra 和[Bellmann Ford 的](https://www.baeldung.com/cs/bellman-ford) 算法使用一种称为边缘松弛的技术。这意味着在遍历我们的图并找到我们的最短路径期间，我们会在找到到达它的更短路径后立即更新已知节点的路径。这在图片中得到了最好的说明：

![Dijkstra 放松边缘 2](https://www.baeldung.com/wp-content/uploads/sites/4/2021/09/Dijkstra_Relaxing_Edge_2.png)

在我们的图表中，我们从 开始![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)，我们的目标是![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-638a7387bd72763290cc777a9b509c38_l3.svg)。当我们遵循 Dijkstra 算法时，我们首先从![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)到![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)。从![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)中，我们可以看到从![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)到![丁](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c10ec9debc8ec5dce4c3c5887557202d_l3.svg)的总成本为 16。但由于 Dijkstra 总是遵循尚未遍历的最短边，我们首先采用从![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)到的路径![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)。从![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)，我们可以看到到的路径![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)成本![丁](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c10ec9debc8ec5dce4c3c5887557202d_l3.svg)仅为 11，这使得路径![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg), ![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg),![丁](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c10ec9debc8ec5dce4c3c5887557202d_l3.svg)比![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg), ![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg),便宜![丁](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c10ec9debc8ec5dce4c3c5887557202d_l3.svg)。这导致我们更新路径![丁](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c10ec9debc8ec5dce4c3c5887557202d_l3.svg)并以较低的成本 11 为其分配。更新图中路径的过程称为松弛边。

## 4. 复杂性

尝试图中所有可能的路径是一个超多项式问题。这就是为什么寻找最短路径的高效算法可以为我们节省大量时间的原因。Dijkstra 的算法在用列表实现时的时间复杂度![O(V^{2})](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-56a19028c7e56d5ce8778f25c9082189_l3.svg)为 ![O(VE)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cab97e9c5d96ae5018f16d0970dc93a5_l3.svg)寻找最短路径的另一种替代方法是为图中的每一对寻找所有最短路径。

不使用松弛边缘的算法示例是[Floyd 算法](https://www.baeldung.com/cs/floyd-warshall-shortest-path)。它通过计算每对可用顶点之间的最短路径来工作，具有![O(V^{3})](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e31cdd2b89e4265f5163be53f321ef25_l3.svg)在具有负边的图形中计算最短路径的复杂性和好处。由于 Floyd 算法是不使用边缘松弛的最快的最短路径算法之一，我们可以得出总结，这种技术给我们带来了巨大的性能提升。

## 5.放松顺序

在上一段中，我们看到 Dijkstra 的复杂性与 Bellmann Ford 的不同。这可能有点令人惊讶，因为它们在遍历图形时都放松了边缘。因此，不仅放松边缘很重要，而且放松的顺序也很重要。为了更好地理解这一点，我们看一下下面的例子：

![迪杰斯特拉 1](https://www.baeldung.com/wp-content/uploads/sites/4/2021/09/Dijkstra_1.png)

首先，我们沿着路径![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg), ![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg), ![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg), ![丁](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c10ec9debc8ec5dce4c3c5887557202d_l3.svg),![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-638a7387bd72763290cc777a9b509c38_l3.svg)放宽路上的所有边：

![迪克斯特拉 3 2](https://www.baeldung.com/wp-content/uploads/sites/4/2021/09/Dijkstra_3_2.png)

然后我们注意到我们可以放松右上边缘以获得更短的路径![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-638a7387bd72763290cc777a9b509c38_l3.svg)：

![迪克斯特拉 3 3](https://www.baeldung.com/wp-content/uploads/sites/4/2021/09/Dijkstra_3_3.png)

在放宽右上边缘后，我们注意到我们还可以放宽左上边缘以获得更短的路径![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)：

![迪克斯特拉 3 4](https://www.baeldung.com/wp-content/uploads/sites/4/2021/09/Dijkstra_3_4.png)

最后，我们必须再次松弛右上边，以得到图形的最短路径。

![迪克斯特拉 3 5](https://www.baeldung.com/wp-content/uploads/sites/4/2021/09/Dijkstra_3_5.png)

正如我们所见，松弛的顺序很重要，因为先松弛左上边缘然后松弛右上边缘可以节省 1 次松弛的成本。虽然乍一看这并不多，但成本实际上是![n！](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-79091dc7f6b7a3fb457c2eb48009e337_l3.svg)针对此类边缘的。

## 七、总结

在本文中，我们讨论了松弛边的概念及其在各种最短路径算法中的重要性。此外，我们分析了在计算最短路径时不同的松弛顺序如何导致成本差异。