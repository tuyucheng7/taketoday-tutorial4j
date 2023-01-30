## 1. 概述

在本教程中，我们将讨论 Floyd-Warshall 算法，然后分析其时间复杂度。

## 2. 弗洛伊德-沃歇尔算法

Floyd-Warshall 算法是一种流行的算法，用于为加权有[向图](https://www.baeldung.com/cs/graphs-directed-vs-undirected-graph)中的每个顶点对寻找[最短路径](https://www.baeldung.com/cs/minimum-spanning-vs-shortest-path-trees)。

在所有对最短路径问题中，我们需要找出图中每个顶点到所有其他顶点的所有最短路径。

现在，让我们进入算法：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f268e2f83f7921cfd77b9e161d48c80e_l3.svg)

我们将有向加权图![G(V, E)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-eaa54ad1d5903544229dbbebdf92afbd_l3.svg)作为输入。首先，我们从给定的图构造一个图矩阵。该矩阵包括图中的边权重。

接下来， 我们插入 ![textbf{mathsf{0}}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1bb0be1927b826a681eceef74e2572da_l3.svg) 矩阵的对角线位置。其余位置由输入图中的相应边权重填充。

然后，我们需要找到两个顶点之间的距离。在找到距离的同时，我们还检查两个选取的顶点之间是否有任何中间顶点。如果存在中间顶点，则我们检查通过该中间顶点的所选顶点对之间的距离。

如果遍历中间顶点时的这个距离小于不经过中间顶点的两个选取顶点之间的距离，我们更新矩阵中的最短距离值。

迭代次数等于顶点集的[基数](https://en.wikipedia.org/wiki/Cardinality)。该算法返回给定图中每个顶点到另一个顶点的最短距离。

## 3. 一个例子

让我们在加权有向图上运行 Floyd-Warshall 算法：

![1-1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/07/1-1.png)

首先，我们从输入图构造一个图矩阵。接下来，我们插入![数学{0}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d8fb28da77ac7ddb2b8cfcaf8f053657_l3.svg)矩阵中的对角线位置，其余位置将用输入图中的边权重填充：

 ![[begin{bmatrix} 0 & infty & -2 & infty  4 & 0 & 3 & infty infty & infty & 0 & 2  infty & -1 & infty & 0  end{bmatrix} quad]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-189aed51c5364e7c971a2450edd3ac7f_l3.svg)

现在，我们准备开始迭代。顶点集的基数是![4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d4d95642629f734574671d47307d46c3_l3.svg)。我们将迭代循环![数学{4}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-37d1fe1e61fadb833cc17cf696d76e41_l3.svg)时间。

让我们从第一个循环开始。对于第一个循环k =1, i=1, j= 1我们将检查是否应该更新矩阵：

![距离[i][j] > 距离[i][k] + 距离[k][j] 隐含距离[1][1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b4eff40907cc52633b9e8299b7b16f92_l3.svg)> ![距离[1][1] + 距离[1][1] 意味着 0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6fec41e0cf9542153aff768f980e8d06_l3.svg)>![0 + 0 意味着 {rm FALSE}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6997aa3afc7290b6c0a3f48ae6dd1897_l3.svg)

由于循环值不满足条件，矩阵将不会更新。

让我们继续，现在对于值k =1, i=1, j= 2并再次检查：

![距离[i][j] > 距离[i][k] + 距离[k][j] 隐含距离[1][2]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-740496eee50934c105784edfed89eddd_l3.svg)> ![距离[1][1] + 距离[1][2] 意味着 0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3495321f830218f7b1a3e3b11985ebf2_l3.svg)>![0 + infty 暗示 {rm FALSE}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-89c9960d4d34617cdf9fb45347cf9781_l3.svg)

因此，矩阵不会发生变化。这样，我们将继续检查所有的顶点对。

让我们快进到一些满足距离条件的值。

对于循环值k =1, i=2, j= 3，我们将看到满足条件：

![距离[i][j] > 距离[i][k] + 距离[k][j] 隐含距离[2][3]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cb773be73e649f1524aa0238f525191f_l3.svg)> ![距离[2][1] + 距离[1][3] 意味着 3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b775110b3c00d7315970feae5e698ebe_l3.svg)> ![4 + -2 意味着 3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fd2e4567b0c7ccea9e42c31f0c0725aa_l3.svg)>![2 暗示{rm TRUE}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c80af4f4c3257f6aa8a7df0e3546f815_l3.svg)

因此，我们将计算一个新的距离：

![距离[i][j] > 距离[i][k] + 距离[k][j] implies](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3587a03472c10f564180ec0382f6d7df_l3.svg) ![距离[2][3] = 距离[2][1] + 距离[1][3] = 2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-09f90ad69ee939aeed445eb0d366663e_l3.svg)

因此，条件满足顶点对![(2,3)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ce63f56bccc4bc54fd7c630c91d52009_l3.svg)。起初，顶点之间的距离![数学{2}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-276c2cef464470f3f58000ea412deecd_l3.svg)为。然而，我们在这里发现了一个新的最短距离。因此，我们用这个新的最短路径距离更新矩阵：![数学{3}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c634b2b9b88bcd5abe9d7d39b4081440_l3.svg)![数学{3}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c634b2b9b88bcd5abe9d7d39b4081440_l3.svg)![数学{2}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-276c2cef464470f3f58000ea412deecd_l3.svg)

 ![[begin{bmatrix} 0 & infty & -2 & infty  4 & 0 & 2 & infty infty & infty & 0 & 2  infty & -1 & infty & 0  end{bmatrix} quad]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-830bccd8031ceb22dee97e2b57460ea9_l3.svg)

让我们为三个嵌套循环取另一组值，使循环值满足算法中给出的距离条件；k=2, i= 4, j= 1 :

![距离[i][j] > 距离[i][k] + 距离[k][j] 隐含距离[4][1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dac225d97339f1087bbd520f56fd1922_l3.svg)> ![distance[4][2] + distance[2][1] implies infty](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-61c738df4f4856c53c377e5ba750c9e7_l3.svg)> ![-1 + 4 意味着 infty](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-21804deca438f5ffe7bc1d162dfecd5f_l3.svg)>![3 暗示{rm TRUE}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-25a599d23aa1d3ad5ce36c65a7c6cb20_l3.svg)

当条件满足时，我们将计算一个新的距离计算：

![距离[i][j] > 距离[i][k] + 距离[k][j] implies](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3587a03472c10f564180ec0382f6d7df_l3.svg) ![距离[4][1] = 距离[4][2] + 距离[2][1] = 3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-92aac617026f638cde44c38f66545a07_l3.svg)

因此，我们现在用这个新值更新矩阵：

 ![[begin{bmatrix} 0 & infty & -2 & infty  4 & 0 & 2 & infty infty & infty & 0 & 2  3 & -1 & infty & 0   end{bmatrix} quad]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4e498707825fe86303c1027b3b5da252_l3.svg)

同样，我们继续检查不同的循环值。最后，在算法终止后，我们将得到包含所有对最短距离的输出矩阵：

 ![[begin{bmatrix} 0 & -1 & -2 & 0  4 & 0 & 2 & 4 5 & 1 & 0 & 2  3 & -1 & 1 & 0  end{bmatrix } 四边形]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1d6077e6d4558ddf2c4b68619c902977_l3.svg)

## 4.时间复杂度分析

首先，我们将边权重插入到矩阵中。我们使用访问图中所有顶点的 for 循环来执行此操作。这可以及时执行![数学{O}(n)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f92d05bae8eccfb970efb4c3ecfa1ee8_l3.svg)。

接下来，我们有三个嵌套循环，每个循环从一个到图中的顶点总数。因此，该算法的总时间复杂度为 ![mathbf{mathcal{O}(n^3)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2dd52fd51d60707397878a2157dadd1c_l3.svg)。

## 5.总结

总而言之，在本教程中，我们讨论了 Floyd-Warshall 算法以在加权有向图中查找所有对的最短距离。

此外，我们还提供了算法的示例和时间复杂度分析。