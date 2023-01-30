## 1. 概述

在图论中，最短路径问题是在图中的两个顶点之间寻找一条路径，使得路径边权值的总和最小。我们可以同时使用[Dijkstra 算法](https://www.baeldung.com/cs/dijkstra)和统一成本搜索算法来查找图中顶点之间的最短路径。

在本教程中，我们将介绍这两种算法并进行比较。

## 2. 通用Dijkstra 算法

给定所有边权重均为非负![mathbf {s}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b03cad8b95fd53490652918baac67736_l3.svg)的加权有向图中的源顶点，Dijkstra 算法可以找到 中所有其他顶点![mathbf{G = (V, E)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8f786a05b1ee32c1b4b47b6b6eb7967a_l3.svg)![mathbf{s}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e0ea94a13bb26f239865ac290ea67163_l3.svg)![mathbf{G}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cd40e30a0123ad10a0c0fd3bcaeb09ce_l3.svg)之间的最短路径。我们先从Dijkstra算法的一个总体框架说起：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-397702915e4cbda972f47b9314e09184_l3.svg)

在此算法中，我们首先将到初始顶点的距离设置为零，将所有其他顶点的距离设置为无穷大。此外，我们从一个初始顶点集开始![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)，它包含图中的所有顶点![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)。

在循环的每次迭代中，我们首先提取![你在问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3346283c52cd1a03edddd75aadf9e250_l3.svg)具有最小距离值的顶点。然后，对于每个未访问的邻居![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)，![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)我们用公式更新它的距离值![dist[v] = min(dist[v], dist[u] + w(u, v))](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f474433a6203531be59fc841927668cb_l3.svg)，其中![w(u,v)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d740528c3712c3c4f1592efe835c68_l3.svg)是边的权重![(紫外线)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a9108e842ec173df2af938619c284e08_l3.svg)。最后，对于每个顶点![在在V](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-06f8b24d3448ceb67f9d68d54d74e190_l3.svg)，包含和![距离[v]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-55c1ed9b03431d060d5915051ea21c36_l3.svg)之间的最短路径权重。![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)

Dijkstra 算法的时间复杂度取决于我们如何实现![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)。

如果我们使用带有[二元最小堆的](https://www.baeldung.com/java-heap-sort#heap-data-structure)[最小优先级队列](https://www.baeldung.com/cs/types-of-queues#priority-queue)，每次提取都需要时间，其中是 中的顶点数。有这样的操作。此外，当我们更改相邻顶点的距离值时，我们需要更新优先级队列。每次更新操作都需要时间。最多有这样的操作，其中是 中的边数。![O(日志|V|)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-554f6c2029001a0dd45a81c11d3c9be2_l3.svg)![|V|](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-02bf9c35541f63401a67c872c113fb50_l3.svg)![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)![|V|](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-02bf9c35541f63401a67c872c113fb50_l3.svg)![O(日志|V|)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-554f6c2029001a0dd45a81c11d3c9be2_l3.svg)![|和|](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1ebfd1cf570f822ea7191e0e88b785de_l3.svg)![|和|](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1ebfd1cf570f822ea7191e0e88b785de_l3.svg)![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)

因此，Dijkstra 算法的总体时间复杂度为![mathbf {O((|V| + |E|)log|V|)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-16b5f8a6c99feb03a5dda0bd65a7baa0_l3.svg)。

## 3.单对最短路径问题的Dijkstra算法

一般的 Dijkstra 算法可以找到源顶点![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)和 中所有其他顶点之间的最短路径![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)。在某些应用中，我们只想找到源顶点![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)和目标顶点之间的最短路径![d](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b7950117119e0530b9b4632250a915c5_l3.svg)。

很容易扩展一般的 Dijkstra 算法来解决这个单对最短路径问题。首先，当我们看到提取的顶点![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)是我们的目标顶点时，我们可以停止循环![d](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b7950117119e0530b9b4632250a915c5_l3.svg)。其次，我们可以使用一种新的数据结构![上一页](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f34e98bebdf4afdabff6c1ba4c0ece93_l3.svg)来记录沿最短路径的前一个顶点。这样，我们就可以在完成循环后构建整个最短路径：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9e7345b553bf00f38bd1e284af428583_l3.svg)

在![构建路径](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-440f7cbf8a77b763aa9a92a1ddede243_l3.svg)函数中，我们从目标顶点开始，![d](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b7950117119e0530b9b4632250a915c5_l3.svg)根据![上一页](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f34e98bebdf4afdabff6c1ba4c0ece93_l3.svg). 一旦到达源顶点，我们就停止构建路径![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fe30ad77297b900aa00f3002a02de6e7_l3.svg)

## 4.统一成本搜索算法

[最佳优先搜索](https://en.wikipedia.org/wiki/Best-first_search)是一种搜索算法，它通过根据指定规则扩展最有希望的顶点来遍历图。统一成本搜索算法是最佳优先搜索方案的简单版本，我们只在选择要扩展的顶点时评估起始顶点的成本。

我们还可以使用统一成本搜索算法来查找源顶点![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)与图中每个其他顶点之间的最短路径![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)。在这个算法中，我们首先从单个顶点开始![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)，然后逐渐扩展到其他顶点。与 Dijkstra 算法类似，我们![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)在每个扩展步骤中选择一个距离最小的顶点。

### 4.1. 流程图和伪代码

![CS 646 统一成本搜索](https://www.baeldung.com/wp-content/uploads/sites/4/2021/02/CS_646_Uniform_Cost_Search-892x1024.png)

在这个算法中，我们从一个初始顶点集 开始![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)，它只包含起始顶点![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)。然后，我们使用一个循环来处理里面的顶点![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)，计算出起始顶点到 中所有其他顶点的最短路径![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)。

在循环的每次迭代中，我们首先提取![你在问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3346283c52cd1a03edddd75aadf9e250_l3.svg)具有最小距离值的顶点。然后，对于 的每个邻居![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)，![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)我们使用公式 计算其距离值![dist[v] = min(dist[v], dist[u] + w(u, v))](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f474433a6203531be59fc841927668cb_l3.svg)，其中![w(u,v)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d740528c3712c3c4f1592efe835c68_l3.svg)是边的权重![(紫外线)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a9108e842ec173df2af938619c284e08_l3.svg)。如果相邻顶点已经在 中![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)，我们只需更新其关联的距离值。否则，我们将相邻顶点添加到![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)中。

我们保持这个循环过程直到![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)为空。uniform-cost 算法的时间复杂度也是![O((|V| + |E|)log|V|)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-349bbc31581b58115a4ebe4d4effad2f_l3.svg)，如果我们使用最小优先级队列来实现的话![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)。

我们可以将统一成本搜索算法的流程图翻译成伪代码：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e538cabed0691b82d1f12e8a582468c7_l3.svg)

### 4.2. 单对最短路径问题的统一成本算法

同样，我们可以扩展统一成本算法来解决单对最短路径问题：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-59451eadd510630ad3343401fa2cafb3_l3.svg)

在这个算法中，当我们看到提取的顶点![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)是我们的目标顶点时，我们停止循环![d](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b7950117119e0530b9b4632250a915c5_l3.svg)。此外，我们使用数据结构![上一页](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f34e98bebdf4afdabff6c1ba4c0ece93_l3.svg)来记录沿最短路径的前一个顶点。最后调用![构建路径](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-440f7cbf8a77b763aa9a92a1ddede243_l3.svg)函数构造最短路径。

## 5. Uniform-Cost Search 与 Dijkstra 算法的比较

Dijkstra 算法和uniform-cost 算法都可以解决时间复杂度相同的最短路径问题。它们具有相似的代码结构。此外，我们使用相同的公式![dist[v] = min(dist[v], dist[u] + w(u, v))](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f474433a6203531be59fc841927668cb_l3.svg)更新每个顶点的距离值。

这两种算法之间的主要区别在于我们如何将顶点存储在![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg). 在 Dijkstra 算法中，我们![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)用 中的所有顶点进行初始化![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)。因此，Dijkstra 算法仅适用于已知所有顶点和边的显式图。

然而，统一成本搜索算法从源顶点开始，逐渐遍历必要的图部分。因此，它既适用于显式图，也适用于隐式图。

对于单对最短路径问题，Dijkstra 算法对内存的要求更高，因为我们将整个图存储在内存中。相比之下，统一成本搜索算法只在开始时存储源顶点，一旦到达目标顶点就停止扩展。因此，uniform-cost搜索算法最后可能只存储了部分图。

尽管两种算法在单对最短路径问题上具有相同的时间复杂度，但由于内存要求，Dijkstra 算法可能更耗时。

当我们![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)使用最小堆优先级队列实现时，每个队列操作都需要![O(log n)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c4e696c3d48ee360ea28fbb80622d356_l3.svg)时间，其中![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)是 中的顶点数![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)。Dijkstra 算法将所有顶点放在![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)开头。对于一个大图，它的顶点在执行操作时会产生很大的开销![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)。

然而，统一成本搜索算法从单个顶点开始，并在路径构建过程中逐渐包括其他顶点。因此，当我们进行优先级队列操作时，我们将处理更少数量的顶点。

这两种算法之间的另一个小区别是从源顶点无法到达的顶点上的最终距离值。![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)在 Dijkstra 算法中，如果源顶点与顶点之间没有路径![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)，则其距离值 ( ![距离[v]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-55c1ed9b03431d060d5915051ea21c36_l3.svg)) 为![+infty](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7d024847687df5ed74470976a0770de7_l3.svg)。然而，在统一成本搜索算法中，我们无法在最终的距离图中找到这样的值，即![距离[v]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-55c1ed9b03431d060d5915051ea21c36_l3.svg)不存在。

我们可以用一个表格来总结我们的对比结果：

![begin{tabular} {|c |c |c |c |c|} hline &bf{初始化} & bf{内存需求} & bf{运行时间} & bf{应用程序} hline bf{Dijkstra 算法} & it{G} 中所有顶点的集合 & 需要存储整个图 & 由于需要大量内存而较慢 & 仅显式图  hline bf{统一成本搜索算法} &一个只有源顶点的集合 & 只存储必要的顶点 & 由于内存需求较少 & 隐式和显式图更快  hline end{tabular}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-76d9e8d8edd4c639a8f5f538dea58144_l3.svg)

## 六，总结

在本教程中，我们展示了 Dijkstra 算法和统一成本搜索算法。此外，我们扩展了这两种算法以解决单对最短路径问题。通过比较这两种算法，我们可以看出统一成本搜索算法在大图上的性能优于 Dijkstra 算法。