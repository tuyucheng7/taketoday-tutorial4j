## 1. 概述

Dijkstra 算法用于在加权图中找到从起始节点到目标节点的最短路径。该算法存在多种变体，最初用于寻找两个给定节点之间的最短路径。现在，它们更常用于查找从源到图中所有其他节点的最短路径，从而生成最短路径树。

在本教程中，我们将学习 Dijkstra 算法的概念以了解其工作原理。在本教程的最后，我们将计算时间复杂度并比较不同实现之间的运行时间。

## 2.算法

该算法于 1959 年发表，以其创建者荷兰计算机科学家 Edsger Dijkstra 的名字命名，可应用于加权图。该算法通过构建一组与源距离最短的节点，从单个源节点找到最短路径树。

### 2.1. 健康)状况

请务必注意以下几点：

-   Dijkstra 算法仅适用于连通图。
-   它仅适用于不包含任何负权重边的图。
-   它仅提供最短路径的价值或成本。
-   该算法适用于有向图和无向图。

### 2.2. Dijkstra 算法

Dijkstra 算法利用[广度优先搜索](https://www.baeldung.com/java-breadth-first-search)(BFS) 来解决单一源问题。但是，与最初的 BFS 不同的是，它使用优先级队列而不是普通[的先进先出队列](https://www.baeldung.com/java-queue)。每个项目的优先级是从源头到达它的成本。

在进入算法之前，让我们简要回顾一些重要的定义。

该图具有以下内容：

-   算法中用![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)或表示的顶点或节点![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)
-   连接两个节点的加权边： ( ![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg), ![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)) 表示一条边，并![w(u, v)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-620dc84add635208d5e693ed13bd9225_l3.svg)表示其权重

以下示例说明了一个无向图，其中每条边都有权重：

![图1](https://www.baeldung.com/wp-content/uploads/sites/4/2021/07/graph-1.png)

 

要实现 Dijkstra 算法，我们需要初始化三个值：

-   ![距离](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2ce9cbaa8582e5f4e29beb3405bdf0a6_l3.svg)![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)– 从源节点到图中每个节点的最小距离数组。在开始时![距离(小号)= 0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d5b048294b4362680ec09e49a8ea8615_l3.svg)，对于所有其他节点![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)，![距离(v) = infty](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-856541f8bec8f119a47dd9fa7829dce3_l3.svg)。当![距离](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2ce9cbaa8582e5f4e29beb3405bdf0a6_l3.svg)找到到每个节点的最短距离时，将重新计算并确定数组。
-   ![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)– 图中所有节点的优先级队列。在进度结束时，![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)将是空的。
-   ![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)– 一组指示算法访问过哪些节点。在进度结束时，![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)将包含图形的所有节点。

该算法进行如下：

-   从 中弹出![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)最小![距离(v)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-18d5715780a5d9e5b8ec115746143f0a_l3.svg)的节点![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)。在第一次运行中，源节点![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)将被选择，因为![距离(小号)= 0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d5b048294b4362680ec09e49a8ea8615_l3.svg)在初始化中。
-   将节点添加![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)到![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg), 以指示![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)已访问。
-   更新![距离](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2ce9cbaa8582e5f4e29beb3405bdf0a6_l3.svg)当前节点的每个相邻节点的值![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)如下：(1)如果![距离(v) + 权重(v, u) < 距离(u)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-363ebc1b7a1d5c013e21fc47f08f5fbd_l3.svg)，则更新![距离(你)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-08f63af71703c4072d0a16a4fbae9d91_l3.svg)为新的最小距离值，(2)否则不对 进行更新![距离(你)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-08f63af71703c4072d0a16a4fbae9d91_l3.svg)。
-   当![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)为空时，即![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)包含所有节点时，进度将停止，这意味着每个节点都已访问过。

在使用伪代码对其进行编码之前，让我们先看一个示例。

### 2.3. 例子

给定以下无向图，我们将计算图中源节点![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-558a73dc6903ad0c33580b0ce9f110d9_l3.svg)与其他节点之间的最短路径。首先，我们标记![距离(c)= 0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-12018dc50d6ab576f35f8101ffa40f5e_l3.svg); 对于其余节点，该值将是![infty](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ac47b919d94a96e82a20265519dbcd65_l3.svg)我们尚未访问过的值：

![步骤1-4](https://www.baeldung.com/wp-content/uploads/sites/4/2021/07/step1-4.png)

在这一步，我们弹出一个距离最小的节点(即 node ![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-558a73dc6903ad0c33580b0ce9f110d9_l3.svg))![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)作为当前节点，并将该节点标记![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-558a73dc6903ad0c33580b0ce9f110d9_l3.svg)为已访问(将节点添加![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-558a73dc6903ad0c33580b0ce9f110d9_l3.svg)到![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg))。接下来，我们不按特定顺序检查当前节点的邻居(![b](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ad69adf868bc701e561aa555db995f1f_l3.svg)、![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-276a76eafbebc4494deafceec7cc4ddd_l3.svg)和![d](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b7950117119e0530b9b4632250a915c5_l3.svg))。

在节点![b](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ad69adf868bc701e561aa555db995f1f_l3.svg)，我们有![距离(b) = infty > 距离(e) + 权重(e, b) = 0 + 7 = 7](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d9be8cef5793aabd3eb82ab772755ec5_l3.svg)，所以我们更新![距离(b)= 7](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0e08f29aa5920cd8043e4ba31fdf4228_l3.svg)。同样，我们更新![距离(c)= 4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-38f9dfaa6239cd6dd2521a518f9eb5f0_l3.svg)和![距离(d)= 2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-718a647519b7bb074cf7a59644b495ad_l3.svg)。此外，我们在重新计算这些节点的距离后更新优先级队列：

![步骤2-3](https://www.baeldung.com/wp-content/uploads/sites/4/2021/07/step2-3.png)

现在我们需要通过从优先级队列中弹出来选择一个新的当前节点![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)。该节点将以最小的 minimum distance 未被访问。在这种情况下，新的当前节点是![d](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b7950117119e0530b9b4632250a915c5_l3.svg)with ![距离(d)= 2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-718a647519b7bb074cf7a59644b495ad_l3.svg)。然后我们重复相邻节点的距离计算。

重复这些步骤直到![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)为空后，我们得到最短路径树的最终结果：

![第三步](https://www.baeldung.com/wp-content/uploads/sites/4/2021/07/step3.png)

### 2.3. 伪代码

以下伪代码显示了 Dijkstra 算法的详细信息：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-537ffd6514c24f9e4905cfdffb64347f_l3.svg)

要使用伪代码更深入地解释 Dijkstra 算法，我们可以阅读[Overview of Dijkstra's Algorithm](https://www.baeldung.com/cs/dijkstra)。然后我们可以学习如何[用Java实现Dijkstra最短路径算法](https://www.baeldung.com/java-dijkstra)。

## 3.实现与时间复杂度分析

我们可以通过多种方式来实现这个算法。每种方式都利用不同的数据结构来存储图形以及优先级队列。因此，这些实现之间的差异导致不同的时间复杂度。

在本节中，我们将讨论 Dijkstra 实现的两个主要情况的时间复杂度。

### 3.1. 情况1

这种情况发生在：

-   给定的图![G=(V,E)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e20a48e5bc490b67aa51b26592523b8a_l3.svg)表示为邻接矩阵。这里![w[你，v]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-599fa2754b22141e016c034721bd750c_l3.svg)存储边的权重![(紫外线)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2ab9e2f4d2b648908df4133879394778_l3.svg)。
-   优先级队列![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)表示为无序列表。

设![|和|](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1ebfd1cf570f822ea7191e0e88b785de_l3.svg)和![|V|](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-02bf9c35541f63401a67c872c113fb50_l3.svg)分别为图中的边数和顶点数。然后计算时间复杂度：

1.  添加所有![|V|](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-02bf9c35541f63401a67c872c113fb50_l3.svg)顶点![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)需要![O(|V|)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6c282d77e999a9a590196468c6472e55_l3.svg)时间。
2.  删除最小的节点![距离](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2ce9cbaa8582e5f4e29beb3405bdf0a6_l3.svg)需要![O(|V|)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6c282d77e999a9a590196468c6472e55_l3.svg)时间，我们只需要![O(1)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-66c97a4dfb9f2e2983629033366d7018_l3.svg)重新计算![距离[u]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-25f0380a6ddb69d7888c9b0ffebe74da_l3.svg)和更新![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)。因为我们在这里使用邻接矩阵，所以我们需要循环![|V|](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-02bf9c35541f63401a67c872c113fb50_l3.svg)顶点来更新![距离](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2ce9cbaa8582e5f4e29beb3405bdf0a6_l3.svg)数组。
3.  循环的每次迭代所花费的时间是，因为从每个循环![O(|V|)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6c282d77e999a9a590196468c6472e55_l3.svg)中删除一个顶点。![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)
4.  因此，总时间复杂度变为![O(|V|) + O(|V|) times O(|V|) = O(|V|^2)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-93e999d0e5e93de1b0809d580073f723_l3.svg)。

### 3.2. 案例二

这种情况在以下情况下有效：

-   给定的图![G=(V,E)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e20a48e5bc490b67aa51b26592523b8a_l3.svg)表示为邻接表。
-   优先级队列![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)表示为二叉堆或斐波那契堆。

首先，我们将讨论使用二叉堆的时间复杂度。在这种情况下，时间复杂度为：

1.  ![O(|V|)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6c282d77e999a9a590196468c6472e55_l3.svg)构建![|V|](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-02bf9c35541f63401a67c872c113fb50_l3.svg)顶点的初始优先级队列需要时间。
2.  使用邻接表表示，可以使用 BFS 遍历图的所有顶点。因此，在算法运行过程中遍历所有顶点的邻居并更新它们的![距离](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2ce9cbaa8582e5f4e29beb3405bdf0a6_l3.svg)值需要![O(|E|)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d1bb5461535d3b3621f60e6751d74aae_l3.svg)时间。
3.  循环的每次迭代所花费的时间是![O(|V|)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6c282d77e999a9a590196468c6472e55_l3.svg)，因为从![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)每个循环中删除一个顶点。
4.  二叉堆数据结构允许我们提取最小值(删除具有最小值的节点![距离](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2ce9cbaa8582e5f4e29beb3405bdf0a6_l3.svg))并及时更新元素(重新计算![距离[u]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-25f0380a6ddb69d7888c9b0ffebe74da_l3.svg))![O(日志|V|)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-554f6c2029001a0dd45a81c11d3c9be2_l3.svg)。
5.  因此，时间复杂度变为![O(|V|) + O(|E| times log|V|) + O(|V| times log|V|)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-97d0937a8f69441ebba5378fd87654c9_l3.svg)，即![O((|E|+|V|) times log|V|) = O(|E| times log|V|)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2be7e2755c9f74629b055ff1c0326beb_l3.svg)，因为![|E|  geq |V|  - 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-05cb00310aefbfa8d38e984e32367d9f_l3.svg)as![G](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1e40206e25474f738eeb7ca968031abf_l3.svg)是一个连通图。

对于二叉堆的更深入的概述和实现，我们可以阅读解释[Priority Queue](https://www.baeldung.com/cs/priority-queue)的文章。

接下来我们将使用 Fibonacci 堆计算时间复杂度。Fibonacci 堆允许我们在 中插入一个新元素![O(1)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-66c97a4dfb9f2e2983629033366d7018_l3.svg)并提取具有最小![距离](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2ce9cbaa8582e5f4e29beb3405bdf0a6_l3.svg)in的节点![O(log|V)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3ce8b3aa30be33b63282a32cd51b6253_l3.svg)。因此，时间复杂度将是：

1.  循环和 extract-min 的每次迭代所花费的时间是![O(|V|)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6c282d77e999a9a590196468c6472e55_l3.svg)，因为从![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)每个循环中删除一个顶点。
2.  遍历所有顶点的邻居并更新它们的![距离](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2ce9cbaa8582e5f4e29beb3405bdf0a6_l3.svg)值以运行算法需要![O(|E|)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d1bb5461535d3b3621f60e6751d74aae_l3.svg)时间。由于每个优先级值更新都需要![O(日志|V|)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-554f6c2029001a0dd45a81c11d3c9be2_l3.svg)时间，因此所有![距离](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2ce9cbaa8582e5f4e29beb3405bdf0a6_l3.svg)计算和优先级值更新的总和也需要![O(|E| times log|V|)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd369cbb1021efe2aaadbb0bedf71b9f_l3.svg)时间。
3.  所以整体的时间复杂度变成了![O(|V| + |E| times log|V|)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-77425670590ca18658d54d33f8139e4b_l3.svg)。

## 4.时间复杂度比较

总体而言，基于 Fibonacci 堆的实现将以最快的速度运行。相反，最慢的版本将是基于无序列表的优先级队列版本。但是，如果图形连接良好(即具有大量边，也就是具有高密度)，则这三种实现之间没有太大区别。

关于不同实现的运行时间的详细比较，可以阅读[用Dijkstra算法进行实验的](https://gabormakrai.wordpress.com/2015/02/11/experimenting-with-dijkstras-algorithm/)文章。

例如，下图说明了节点数量增加时六个变体之间的运行时间比较：

![复杂](https://www.baeldung.com/wp-content/uploads/sites/4/2021/07/complexity.png)

## 5.总结

在本文中，我们讨论了 Dijkstra 算法，这是一种流行的图上最短路径算法。

首先，我们了解了优先级队列的定义和流程。然后我们研究了一个示例，以更好地理解该算法的工作原理。

最后，我们检查并比较了算法在三种不同实现中的复杂性。