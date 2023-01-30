## 1. 概述

在本教程中，我们将讨论[最小生成树](https://www.baeldung.com/cs/minimum-spanning-vs-shortest-path-trees)中的 cut 属性。

此外，我们将给出几个切割示例，并讨论最小生成树中切割属性的正确性。

## 2. 切割的定义

在图论中，割可以定义为将图划分为两个[不相交子集](https://en.wikipedia.org/wiki/Disjoint_sets)的分区。

让我们正式定义一个切割。![C = (S_1, S_2)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8182f11bc502aac2f3ebed751f7603c7_l3.svg)连通图中的切割![G(V, E)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-eaa54ad1d5903544229dbbebdf92afbd_l3.svg)将顶点集![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54e215a7a583b4f357a5a627420bcf2f_l3.svg)划分为两个不相交的子集![S_1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-84d4bfd3363731567c67eb8a850fd49c_l3.svg)，并且![S_2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cc1abfbd7778f03adf5b5b5d797e0c43_l3.svg)。

在图论中，在本次讨论中会出现一些与割相关的术语：割集、割顶点和割边。在进一步讨论之前，让我们在这里讨论这些定义。

![C(S_1, S_2)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c4b075d2dfe57677d1f468817bbcff64_l3.svg)连通图的割的割集![G(V, E)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-eaa54ad1d5903544229dbbebdf92afbd_l3.svg)可以定义为一个端点在 中![S_1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-84d4bfd3363731567c67eb8a850fd49c_l3.svg)，另一个端点在 中的边集![S_2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cc1abfbd7778f03adf5b5b5d797e0c43_l3.svg)。例如![C(S_1, S_2)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c4b075d2dfe57677d1f468817bbcff64_l3.svg)的![G(V, E) = {(i, j) 在 E |  j in S_1, j in S_2 }](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-423af16d92b6c43bfbce5cfa40438eb4_l3.svg)

![V_c](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e7c98d16c02d1b832c96dd4194afe57e_l3.svg)如果存在连通图，则顶点是割顶点，并且从中![G(V, E)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-eaa54ad1d5903544229dbbebdf92afbd_l3.svg)移除会断开该图。![V_c](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e7c98d16c02d1b832c96dd4194afe57e_l3.svg)![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)

如果和断开图，则边![E_c](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c93297fef9d6f0bb54767d8e81ebf3cb_l3.svg)是连通图的切割边。![G(V, E)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-eaa54ad1d5903544229dbbebdf92afbd_l3.svg)![E_c 在 E](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-162041100987516c90f04766d4d28b24_l3.svg)![G - E_c](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c57f415b997d6cbf9063c97c4b7737ff_l3.svg)

## 3.例子

在本节中，我们将看到一个剪切示例。我们还将演示如何找到割集、割顶点和割边。

首先我们来看一张连通图：

![1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/10/1.png)

在这里，我们采取了![G(V, E)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-eaa54ad1d5903544229dbbebdf92afbd_l3.svg)where![V=(V1, V2, V3, V4, V5, V6, V7)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-12220fa57d9eaa8d453d58067d75fc35_l3.svg)和![E=(E1, E2, E3, E4, E5, E6, E7, E8, E9)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4ead3074d978a513b7bac226cd4d640b_l3.svg)。![C(S_1, S_2)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c4b075d2dfe57677d1f468817bbcff64_l3.svg)现在让我们在 a 中定义一个切割![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)：

![2](https://www.baeldung.com/wp-content/uploads/sites/4/2020/10/2.png)

所以在这里，切割![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)断开了图形![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)并将其分为两个部分![S_1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-84d4bfd3363731567c67eb8a850fd49c_l3.svg)和![S_2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cc1abfbd7778f03adf5b5b5d797e0c43_l3.svg)。

现在让我们讨论切割顶点。根据定义，删除割点将使图断开。如果我们观察图形![mathbf{G}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cd40e30a0123ad10a0c0fd3bcaeb09ce_l3.svg)，我们可以看到有两个切割顶点：![mathbf{V3}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-eae47479c35fc7181d30c70fbac19b1e_l3.svg)和![mathbf{V4}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-75cf3a54f3cd1df757aeeeeaa7b5c6bf_l3.svg)。我们来验证一下。首先，我们![V3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c1cbf471642379e1134d853c34e0f9ea_l3.svg)从中删除顶点![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)：

![v3 删除](https://www.baeldung.com/wp-content/uploads/sites/4/2020/10/v3-remove.png)

我们可以看到顶点的移除![V3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c1cbf471642379e1134d853c34e0f9ea_l3.svg)断开了图![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)并将其分成两个图。因此，我们验证了![V3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c1cbf471642379e1134d853c34e0f9ea_l3.svg)是 中的切割顶点![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)。![V4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8fe893fb5d100282c2f834c90a38226c_l3.svg)现在让我们从中删除顶点![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)：

![v4 删除](https://www.baeldung.com/wp-content/uploads/sites/4/2020/10/v4-remove.png)

同样，当我们从 中删除![V4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8fe893fb5d100282c2f834c90a38226c_l3.svg)时![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)，它会断开图形并创建两个图形。因此，![V4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8fe893fb5d100282c2f834c90a38226c_l3.svg)也是 中的割点![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)。

让我们谈谈切边。根据定义，如果我们删除切割边，它会断开图形并产生两个或多个子图。在![mathbf{G}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cd40e30a0123ad10a0c0fd3bcaeb09ce_l3.svg)中，很容易看出边![mathbf{E4}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e22ffbfa7812be17e44056c42de8d974_l3.svg) 是切边。如果我们从 中删除![E4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2313d6602964338d0e99b296836be88b_l3.svg)，![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)它将把图![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)分成两个子图：

![e4 删除](https://www.baeldung.com/wp-content/uploads/sites/4/2020/10/e4-remove.png)

接下来是切割集。割的割集定义为两个端点在两个图中的一组边。在这里，切割的切割集是。我们可以看到belongs to的一个端点，另一个端点 in 。![mathbf{C(S_1, S_2)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5bbffa975f1463631eedb855c6682e44_l3.svg)![mathbf{G}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cd40e30a0123ad10a0c0fd3bcaeb09ce_l3.svg) ![mathbf{E4}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6bc3f3a333a01588d07cabae8cbeaad6_l3.svg)![E4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2313d6602964338d0e99b296836be88b_l3.svg)![S_1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-84d4bfd3363731567c67eb8a850fd49c_l3.svg)![S_2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cc1abfbd7778f03adf5b5b5d797e0c43_l3.svg)

## 4. 剪辑的变体

切割有两种流行的变体：最大切割和最小切割。在本节中，我们将通过示例讨论这两种变体。

最小割和最大割都存在于加权连通图中。最小割是删除断开图形的边的最小权重和。类似地，最大割是删除断开图形的边的最大权重和。

让我们找到最大和最小切割：

![最大值最小值](https://www.baeldung.com/wp-content/uploads/sites/4/2020/10/maxmin.png)

这里我们采用的是连通加权图![G_1(V_1, E_1)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7c29011a8eb493e236892029eade37b5_l3.svg)。此外，我们在图中定义了 4 个切割![G_1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e3192da0128dfabe5fce82166bdc373c_l3.svg)。

因此，根据定义，我们将对每个切割的边的权重求和。让我们从![切 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-248847b24fa1e71e8d69f52fa5db10a8_l3.svg). 的总边权重![切 1 =](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8b6f1d938bc871f9f2aeae883091f9eb_l3.svg)总和![E1、E3、E10 = 2 + 2 + 4 = 8](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6c06339449c8a2688637d2bde572a74a_l3.svg)。这样， 和 的权重![剪辑 2，剪辑 3，](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-64f134ba3a768013240e4595d0d6009b_l3.svg)将![切4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9c6250e6eb2ef50ac57fa6e389a19124_l3.svg)是![10, 6, 7](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cbf27ea688ae5e98bae90a866e520540_l3.svg)。

因此，最小切割![mathbf{切 3}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b3d2c4f86a88039696cbcae6dd862227_l3.svg)的权重为 6 ， 最大切割![mathbf{G_1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-59fe54ed88f0d8dba51195a3c9924d1b_l3.svg)为![mathbf{切 2}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-663b63538015723b1ab7f9738cbd6ea3_l3.svg) 中的边权重之和![mathbf{切 2}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-663b63538015723b1ab7f9738cbd6ea3_l3.svg)大于 中的所有其他切割![mathbf{G_1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-59fe54ed88f0d8dba51195a3c9924d1b_l3.svg)。

## 5. 最小生成树中的切割属性

### 5.1. 陈述

现在我们知道割将图的顶点集分成两组或更多组。割集包含一组边，其一个端点在一个图中，另一个端点在另一个图中。在构建最小生成树(MST)时，原始图应该是加权连通图。让我们假设 MST 中的所有边成本都是不同的。

根据割的性质，如果割集中有一条边在割集中所有其他边中具有最小的边权重或成本，则该边应包含在最小生成树中。

### 5.2. 例子

我们在这里采用加权连通图：

![例子1-1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/10/example1-1.png)

在此示例中，切割将图![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)分为两个子图![S_1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-84d4bfd3363731567c67eb8a850fd49c_l3.svg)(绿色顶点)和![S_2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cc1abfbd7778f03adf5b5b5d797e0c43_l3.svg)(粉红色顶点)。的割集![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)为![(E2, E3, E5, E6)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3c2db209295a56d53feff2464498f6b7_l3.svg)。现在根据切割属性，切割集中的最小加权边应该出现在 的最小生成树中![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)。这里来自割集的最小加权边是![E5](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f46d6135e3e09c0c3a28269890908e95_l3.svg)。

现在我们将构建一个最小生成树![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)并检查边缘![E5](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f46d6135e3e09c0c3a28269890908e95_l3.svg)是否存在：

![例子2-1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/10/example2-1.png)

这是 的最小生成树之一![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)，正如我们所见，![E5](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f46d6135e3e09c0c3a28269890908e95_l3.svg)这里存在边。所以我们可以说 cut 属性适用于图形![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)。

其他图表呢？ cut 属性是否适用于所有其他最小生成树？让我们在下一节中找出答案。

### 5.3. 切割属性证明

现在得出 cut 属性适用于所有最小生成树的总结，我们将在本节中提供正式证明。

假设我们![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)从图构建最小生成树![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)。我们还定义了一个切割![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)，将顶点集分成两组![P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fda1e51b12ba3624074fcbebad72b1fc_l3.svg)和![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)。此外，我们假设存在![E_C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-27bb7e57f60b2008ee44a3845718418d_l3.svg)连接两个集合![P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fda1e51b12ba3624074fcbebad72b1fc_l3.svg)、和 的边![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)，并且具有最小的权重。

现在我们通过假设边缘![mathbf{E_C}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-38f857760802770a4fd824b1321556e0_l3.svg)不是 MST 的一部分来开始这个证明![mathbf{T}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8cd28d45ff41f5e90e5bc849432c480a_l3.svg)。如果我们包含![E_C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-27bb7e57f60b2008ee44a3845718418d_l3.svg)到 MST中，看看会发生什么会很有趣![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)。如果我们包含![E_C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-27bb7e57f60b2008ee44a3845718418d_l3.svg)在中![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)，它会创建一个[循环](https://www.baeldung.com/cs/cycles-undirected-graph)。但是根据MST的定义，循环不能成为MST的一部分。

现在，如果我们分析 MST ![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)，必须有一些边缘![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)，让我们将其命名为![钾](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7fb8d8d37cb2b48aee9e97aee7728d8f_l3.svg)，除了![E_C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-27bb7e57f60b2008ee44a3845718418d_l3.svg)它有一个端点![P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fda1e51b12ba3624074fcbebad72b1fc_l3.svg)和另一个端点![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)。现在最初，我们假设![E_C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-27bb7e57f60b2008ee44a3845718418d_l3.svg)在连接![P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fda1e51b12ba3624074fcbebad72b1fc_l3.svg)和的所有边中具有最小的权重![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)。这意味着边![mathbf{K}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7b14a7490aefdbfda048a0dd63bc126f_l3.svg)的权重必须高于![mathbf{E_C}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-38f857760802770a4fd824b1321556e0_l3.svg)。

因此![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)是生成树而不是最小生成树。如果我们包含边![E_C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-27bb7e57f60b2008ee44a3845718418d_l3.svg)然后构建 MST，则 MST 的总权重![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)将小于前一个。此外，![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)不能同时包含两者![E_C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-27bb7e57f60b2008ee44a3845718418d_l3.svg)，![钾](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7fb8d8d37cb2b48aee9e97aee7728d8f_l3.svg)因为它会创建一个循环。![E_C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-27bb7e57f60b2008ee44a3845718418d_l3.svg)因此，我们最初认为不属于 MST 的假设![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)应该是错误的。

因此我们可以得出总结，割集中的最小加权边应该是该图的最小生成树的一部分。

让我们用一个例子来简化证明：

![切 1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/10/cut-1.png)

我们正在使用一个连接的加权图![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)。现在让我们定义一个切割![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)：![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)

![切 2](https://www.baeldung.com/wp-content/uploads/sites/4/2020/10/cut-2.png)

割![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)把图![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)分成两个子图![P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fda1e51b12ba3624074fcbebad72b1fc_l3.svg)和![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)。现在有两条边相连![P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fda1e51b12ba3624074fcbebad72b1fc_l3.svg)，![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)其中![E_C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-27bb7e57f60b2008ee44a3845718418d_l3.svg)有最小加权边。![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)首先，我们将构建一个不![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)包含边的最小生成树![E_C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-27bb7e57f60b2008ee44a3845718418d_l3.svg)：

![切 3](https://www.baeldung.com/wp-content/uploads/sites/4/2020/10/cut-3.png)

这里最小生成树的总权![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)值为![19](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1feb3cd728603c593fe8b9e21dff8938_l3.svg)。之前我们定义的![E_C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-27bb7e57f60b2008ee44a3845718418d_l3.svg)是切割集中的最小加权边。这意味着边缘的权重![钾](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7fb8d8d37cb2b48aee9e97aee7728d8f_l3.svg)应该大于边缘![E_C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-27bb7e57f60b2008ee44a3845718418d_l3.svg)。在这种情况下，当前构建的生成树不是 MST，因为我们可以构建一个权重低于当前生成树的生成树：

![切 4](https://www.baeldung.com/wp-content/uploads/sites/4/2020/10/cut-4-1024x546.png)

正如我们所看到的，当我们将边包含![钾](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7fb8d8d37cb2b48aee9e97aee7728d8f_l3.svg)在生成树中时，生成树的总权重将变得![19](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1feb3cd728603c593fe8b9e21dff8938_l3.svg) 比我们![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)通过包含边构建时的权重更高![E_C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-27bb7e57f60b2008ee44a3845718418d_l3.svg)。因此，如果我们包含边![mathbf{K}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7b14a7490aefdbfda048a0dd63bc126f_l3.svg)，那么它就不是最小生成树。

因此，我们证明了最小生成树对应的连通加权图应该包含割集的最小加权边。

### 5.4. 应用

[Prim 算法](https://www.baeldung.com/java-prim-algorithm) 和[Kruskal](https://www.baeldung.com/cs/kruskals-vs-prims-algorithm)算法等最短路径算法使用 cut 属性来构造最小生成树。

## 六，总结

在本教程中，我们讨论了最小生成树中的 cut 属性。

我们提出了切割属性的正确性，并表明切割属性对所有最小生成树都有效。