## 1. 概述

在本教程中，我们将讨论在 负权重图上使用[Dijkstra算法时出现的问题。](https://www.baeldung.com/cs/dijkstra)

首先，我们将回顾 Dijkstra 算法背后的思想及其工作原理。

然后，我们将在具有负权重的图上展示 Dijkstra 算法的几个问题。

## 2. 回忆 Dijkstra 的算法

### 2.1. 大意

假设我们有一个图 ，![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)其中包含编号从到 的![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54e215a7a583b4f357a5a627420bcf2f_l3.svg)节点。此外，我们还有连接这些节点的边。这些边中的每一个都有一个与之关联的权重，表示使用该边的成本。Dijkstra 算法将为我们提供从特定源节点到给定图中每个其他节点的最短路径。![1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-69a7c7fb1023d315f416440bca10d849_l3.svg)![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54e215a7a583b4f357a5a627420bcf2f_l3.svg)![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-638a7387bd72763290cc777a9b509c38_l3.svg)

回想一下，两个节点![textbf{A}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8a4d2f0828fd12776c154df2bbfacc93_l3.svg)和之间的最短路径是和![textbf{B}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7666363b561b4825ee67c60417c13ef4_l3.svg)之间所有可能路径中成本最低的路径。![textbf{A}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8a4d2f0828fd12776c154df2bbfacc93_l3.svg)![textbf{B}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7666363b561b4825ee67c60417c13ef4_l3.svg)

### 2.2. 执行

让我们来看看[实现](https://www.baeldung.com/java-dijkstra)：

```

```

最初，我们声明一个名为 的数组![成本](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-eb1f33b44b4a5c770f4297506b3754ec_l3.svg)，它存储源节点与给定图中每个其他节点之间的最短路径。此外，我们声明了一个优先级队列，用于存储已探索的节点，并根据它们的成本值将它们按升序排序。

接下来，我们将源节点添加到优先级队列中，并使到达源节点的成本等于![0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8354ade9c79ec6a7ac658f2c3032c9df_l3.svg)，因为从节点到自身的最短路径等于 0。然后只要队列还不为空，我们就会执行多个步骤。在每一步中，我们从队列中取出成本最低的节点。由于队列是根据每个节点的成本排序的，因此成本最低的节点将位于优先级队列的前面。

之后，我们检查当前节点的每个邻居是否比新发现的邻居具有更好的成本。新成本等于到达当前节点的成本加上连接当前节点与其邻居的边的权重。如果新的成本较低，我们找到了一条从源节点到这个邻居的新路径，通过当前节点到达它。在这种情况下，我们将更新邻居的成本并将其添加到优先级队列中。

最后，我们返回![成本](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-eb1f33b44b4a5c770f4297506b3754ec_l3.svg)数组，该数组存储从源节点到给定图中每个其他节点的最短路径。

### 2.3. 概念验证

Dijkstra 算法的正确性证明是基于贪婪的想法，即如果存在比任何子路径更短的路径，则更短的路径应该替换该子路径以使整个路径更短。让我们更好地说明这个想法。

在 Dijkstra 算法的每一步中，我们知道到目前为止的最短路径(队列中的每个节点代表从源到该节点的唯一路径)。然而，这意味着一旦我们探索更多节点，我们就可以在后面的步骤中找到更好的路径。因此，我们无法确定当前发现的路径是否是最短路径。

然而，我们可以确定只有一条路径，即队列中成本最低的路径。原因是其他路径的成本更高。因此，要以最低成本到达路径的末端节点，我们需要使用一些额外的边，从而导致更高的成本。因此，我们总是选择成本最低的路径并探索该路径的下一个节点。

请记住，我们的假设是基于所有边都具有非负权重的想法。否则，我们无法确定选择成本最低的路径。原因是当我们从该路径探索一些负成本边时，选择具有更高成本的不同路径可能会给我们带来更低的总成本。

在接下来的部分中，我们将描述因某些边具有负成本而导致 Dijkstra 算法失败的两种情况。

## 3.无限循环

让我们看看下面的图表：

![周期](https://www.baeldung.com/wp-content/uploads/sites/4/2021/06/Cycles-300x221-1.png)

设源节点为![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)。当我们从 运行 Dijkstra 算法时![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)，我们会将![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)和添加![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)到优先级队列，两者的成本都等于![-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-37abf2e602a43ae0ff9f12b1536fa74c_l3.svg)。

接下来，我们将弹出节点![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)，因为它在优先级队列中的所有节点中成本最低，我们将添加节点![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)和![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)，两者的成本都等于![-2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6d9d64c8550082ac0eeea0b4d66a5165_l3.svg)。在下一步中，我们弹出节点![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)并添加节点![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)和![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)，两者的成本都等于![-3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-451c06e9abe5c9ec80b1ed130555ad69_l3.svg)。然后我们回到 node ![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)。

同样的场景会反复发生；如我们所见，我们将陷入无限循环，因为在每次迭代中，我们将获得更小的成本，并且我们永远无法![丁](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c10ec9debc8ec5dce4c3c5887557202d_l3.svg)使用 Dijkstra 算法到达节点。

总之，如果图中至少包含一个负环，则 Dijkstra 算法永远不会结束。负循环是指其边的总权重为负的循环。

## 4.错误的路径

让我们看看下面的图表：

![错误的路径](https://www.baeldung.com/wp-content/uploads/sites/4/2021/06/WrongPath-300x287-1.png)

让![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)成为源节点。当我们从 运行 Dijkstra 算法时![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)，我们会将![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)和添加![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)到优先级队列，成本分别等于![5](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-48348ef601c56286abf49bafe09c7af1_l3.svg)和![7](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9d8e16e2c1790d6af563225a9318d119_l3.svg)。

接下来，我们将弹出节点![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)，因为它在优先级队列中的所有节点中成本最低，并添加![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)成本等于的节点![1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-69a7c7fb1023d315f416440bca10d849_l3.svg)。最后，我们弹出 node ![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)，Dijkstra 的算法将在这里停止。

该![成本](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-eb1f33b44b4a5c770f4297506b3754ec_l3.svg)数组将如下所示，分别![[0, 5, 1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f92edca8fcf6adf91575c1594a36928f_l3.svg)表示到达节点![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)、![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)和的最短路径的成本![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)。但是，我们可以通过路径到达![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)成本等于的节点。![3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ce2009a45822333037922ccca0872a55_l3.svg)![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg) ![右箭头](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-76319e471bb0c08bfa33603fd4f71eb2_l3.svg) ![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg) ![右箭头](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-76319e471bb0c08bfa33603fd4f71eb2_l3.svg) ![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)

发生的事情是我们没有通过，![边(A，C)= 7](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-783fc9d54057fe61cc28fb064b2d0fc4_l3.svg)因为我们以较小的成本到达了节点，并且我们忽略了使用这种贪婪方法![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)通过的所有路径。![边(A，C)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a6b260b09f5c61aa1f4c30ab625c2ba6_l3.svg)

总结这种情况，如果图包含负边但没有负环，则 Dijkstra 算法可以结束；但是，它可能会给出错误的结果。

## 5.总结

在本文中，我们回顾了 Dijkstra 算法并提供了两种在负边上失败的场景。