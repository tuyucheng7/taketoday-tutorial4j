## 1. 概述

在本教程中，我们将探讨为什么我们不能在有向图上使用[Prim](https://www.baeldung.com/cs/prim-algorithm)和[Kruskal的算法。](https://www.baeldung.com/cs/kruskals-vs-prims-algorithm#kruskals-algorithm)

## 2.最小跨度树

这两种算法都用于在无向图中找到最小生成树。最小生成树 (MST) 是图的子图。该子图包含权重最小的边，同时包含原始图中的所有节点：

 

![g1](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/g1-300x172.png)

显示的图形可视化了权重为 9 的最小跨度树。

### 2.1. 克鲁斯卡尔算法

为了使[Kruskal 的算法](https://www.baeldung.com/java-spanning-trees-kruskal)起作用，我们需要一个无向的、加权的和连通的图。它的想法很简单：

-   创建一个数据结构![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)。
-   将权重最低的边添加到数据结构中，这样由它创建的图形不包含电路![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)。
-   继续这些步骤，直到![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)形成一棵生成树。

在这里我们已经注意到，如果我们没有正确检测到圆，我们可能会遇到问题，就像在有向图中一样。

### 2.2. 普里姆算法

[Prim 算法的](https://www.baeldung.com/java-prim-algorithm)工作原理是从一个随机节点开始并遍历图形选择权重最低的边。虽然这种方法让我们想起了 Dijkstra 的算法，但它实际上只优先考虑边的直接权重，而不是像 Dijkstra 那样优先考虑到起始节点的整个路径。

为了更仔细地观察，我们看一下伪代码：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1ae0607e73a5014b4580bbd1197d4dac_l3.svg)

### 2.3. 比较

为了快速[比较](https://www.baeldung.com/cs/kruskals-vs-prims-algorithm)这两种算法，我们得出总结，Kruskal 算法在稀疏图上运行良好并且易于实现。另一方面，Prim 的算法对于有很多边的图来说速度更快，但也更难实现。

## 3.问题实例

正如我们在算法分析中已经看到的，它们都需要一个无向图。虽然输入有向图不会自动停止算法，但几个例子可以说明为什么我们不能允许有向图本身。

### 3.1. 树状结构问题

由于我们在有向图中没有最小生成树的经典概念，因此我们将使用最小生成树状结构 (MSA)。最小跨度树状结构是一棵有向树。它包含一个节点，从该节点存在到图中每个其他节点的路径：

 

![g2](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/g2-300x250.png)

### 3.1. 克鲁斯卡尔

Kruskal 算法的反例：

![g3](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/g3-300x296.png)

 

在我们的示例中，只有一个 MSA 可能。那就是![s 右箭头 b 右箭头 a](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d6bd0d85ea16310aecdc6093e51b0e14_l3.svg)。但是运行 Kruskal 算法，我们首先选择![右箭头 b](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ee198936a9dcafc919babfb3a66d5f74_l3.svg)：

 

![g4](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/g4-300x296.png)

由于这条边不包含在我们的 MSA 中，因此不可能从本例中的 Kruskal 算法构造一条边。

### 3.2. 朴素的

对于 Prim 算法，我们有类似的设置：

 

![g5](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/g5-240x300.png)

这次唯一可能的 MSA 是![s 右箭头 a 右箭头 b](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b6177c09e0777c18db652a303d3b2035_l3.svg). Prim 的算法从一个随机节点开始，在本例中为![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)。然后它采用连接到 的最低边的边![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)，这是到 的节点![b](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ad69adf868bc701e561aa555db995f1f_l3.svg)：

 

![g6](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/g6-240x300.png)

由于边缘![s 右箭头 b](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-26fbc302547dc11b32403243061231fc_l3.svg)不包含在唯一的 MSA 中，因此以下步骤不会为此示例生成 MSA。

## 4. Chu–Liu/Edmonds 算法

Chiu-Liu/Edmonds 的算法是一种能够解决有向连通图的 MSA 问题的替代方法。为了让它工作，我们需要一个已经选择的节点 r，它充当我们 MSA 的根。该算法通过消除循环将我们的图递归地转换为 MSA。

### 4.1. 概述

我们用以下流程图描述 Chiu-Liu 算法：

 

![g7](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/g7-300x216.png)

首先，我们消除所有通向根的边，因为 MSA 的根没有传入边。其次，我们查看图中的每个节点并选择其最低的传入边 e，用 表示其源![pi(v)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-82e050bb77b2371c1177080800c6fb74_l3.svg)。然后我们检查我们是否已经有一个 MSA。如果不是，我们递归地消除图中的循环。

### 4.2. 消除循环

为了获得更好的视角，我们再看一下将循环转换为单个节点的步骤：

对于在![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54e215a7a583b4f357a5a627420bcf2f_l3.svg)和不在 中的所有节点![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)，我们创建一个![在'](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d77ab288f8841c7ba882d49808b7de50_l3.svg)具有新权重函数的集合![在'](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-151c0c50d8260a1d613b95b1ea21a2cd_l3.svg)。
对于连接到节点的所有边，![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54e215a7a583b4f357a5a627420bcf2f_l3.svg)我们执行以下规则，创建一组新的边![和'](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c3f58512002a720f1e74be90e23bada3_l3.svg)：

1. 对于![(紫外线)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a9108e842ec173df2af938619c284e08_l3.svg)带有![notin C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ea137b25fbc3a6cde51cfab864186c47_l3.svg)和的边，我们在 中![v 在 C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-726e3513c0acad64097d00740fa87c98_l3.svg)创建一条新边并定义。 2. 对于带有和的边，我们在 中创建一条新边并定义。 3. 对于节点未包含在圆中的所有边，我们保留原始图中的边和边权重。![e = (u,v_c)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-763742d3f76e98b331f24fe4491f8c87_l3.svg)![和'](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c3f58512002a720f1e74be90e23bada3_l3.svg)![w'(e) = w(u,v) - w(pi(v),v)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e8a3e039c41af67a01f4fb8282dcf8f0_l3.svg)
![(紫外线)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a9108e842ec173df2af938619c284e08_l3.svg)![你在C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-731515afd7f5d1ffaae1dc75d0be1f05_l3.svg)![v notin C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-910b981e6493ca40d46ef7126cdb3a8a_l3.svg)![e = (v_c, v)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6be253394d2508b8513733beb855b135_l3.svg)![和'](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c3f58512002a720f1e74be90e23bada3_l3.svg)![w'(e) = w(u,v)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-03a8f2978bde8439deb6f34d2db0edf2_l3.svg)

我们进行这种减少，直到我们有一个![G'](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4682bec056d92e3ca6ecbbd6e5e4f45b_l3.svg)不包含任何循环的图形。由于构造![G'](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4682bec056d92e3ca6ecbbd6e5e4f45b_l3.svg)没有任何循环，我们有一个 MSA。现在我们只需要转换![G'](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4682bec056d92e3ca6ecbbd6e5e4f45b_l3.svg)回包含所有原始节点的图形。

为此，我们看一下我们构造的节点![v_c](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f49202ab4d13c0b66acfca5fb04f4b0a_l3.svg)，它们只有一个传入边，即![u_c](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-303f317d2b5128182586ff6d9fba67eb_l3.svg)。这条边对应![(紫外线)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a9108e842ec173df2af938619c284e08_l3.svg)于我们原来的 Graph中的边![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)，v 在一个循环中。

然后我们继续![(pi(v),v)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-327543881bfd5340f45ae7fce7493737_l3.svg)从我们的原始图中删除，以打破循环。

随后对每个构造的节点执行此操作，![v_c](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f49202ab4d13c0b66acfca5fb04f4b0a_l3.svg)我们最终得到一个包含所有原始节点且无循环的图。这张图是我们 MSA 问题的解决方案。

### 4.3. 复杂

在我们最坏的情况下，我们检查图中的每个节点。对于每个节点，我们必须更改图中每条边的权重。因此我们得出的时间复杂度为![O(VE)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cab97e9c5d96ae5018f16d0970dc93a5_l3.svg)。使用合适的数据结构，例如斐波那契堆，复杂度可以降低为![O(E cdot V logV)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9db371613031ac0223e9806989f9ac58_l3.svg)

## 5.总结

在本文中，我们讨论了为什么我们既不能将 Kruskal 算法也不能将 Prim 算法应用于有向图的原因。此外，我们探索了一种替代方法，它可以为给定的有向图和根节点检索最小跨度树状结构。