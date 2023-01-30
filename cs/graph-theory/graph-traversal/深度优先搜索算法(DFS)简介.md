## 1. 概述

在[图](https://www.baeldung.com/cs/graph-theory-intro)论中，主要的遍历算法之一是[DFS](https://www.baeldung.com/java-depth-first-search)(深度优先搜索)。在本教程中，我们将介绍该算法并着重于以递归和非递归方式实现它。

首先，我们将解释 DFS 算法是如何工作的，看看[递归](https://www.baeldung.com/java-recursion)版本是怎样的。此外，我们将提供一个示例来了解算法如何遍历给定的[图](https://www.baeldung.com/java-graphs)。

接下来，我们将解释非递归版本背后的想法，展示它的实现，并提供一个例子来展示两个版本如何处理示例图。

最后，我们将比较递归和非递归版本并说明何时使用它们。

## 2.定义

让我们从一般定义 DFS 算法开始，并提供一个示例来了解该算法的工作原理。

### 2.1. 一般概念

DFS 算法从一个起始节点开始![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)。在每一步中，该算法都会在 的相邻节点中选择一个未访问的节点，![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)并从该节点开始执行递归调用。当一个节点的所有邻居都被访问时，算法结束该节点![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)并返回检查发起对 node 的调用的节点的邻居![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)。

与[BFS](https://www.baeldung.com/java-breadth-first-search)算法不同，DFS 不会逐级访问节点。相反，它会尽可能深入。 一旦算法到达终点，它会尝试从上次访问的节点的其他相邻节点开始更深入。因此，深度优先搜索这个名字来源于这样一个事实，即该算法试图在每一步中深入到图形中。

为简单起见，我们假设图在[邻接表](https://www.baeldung.com/cs/graphs)数据结构中表示。所有讨论的算法都可以很容易地修改以应用于其他数据结构的情况。

### 2.2. 例子

考虑以下图形示例：

![图形示例](https://www.baeldung.com/wp-content/uploads/sites/4/2020/09/Graph_Example-1024x807-1.png)

假设我们要从 node 开始 DFS 操作![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)。假设邻接表中的所有相邻节点都按字母顺序递增排列。我们将执行以下操作：

1.  从 开始![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)，我们有两个邻居![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)， 和![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)。因此，我们从![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg).
2.  由于![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)只有一个未访问的 adjacent ![F](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-88df03c55e081c7cd9da4e7d74ba7265_l3.svg)，我们从那个继续。
3.  该节点![F](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-88df03c55e081c7cd9da4e7d74ba7265_l3.svg)有两个未访问的邻居![丁](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c10ec9debc8ec5dce4c3c5887557202d_l3.svg)和![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-638a7387bd72763290cc777a9b509c38_l3.svg)。因此，我们从节点 继续![丁](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c10ec9debc8ec5dce4c3c5887557202d_l3.svg)。
4.  ![丁](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c10ec9debc8ec5dce4c3c5887557202d_l3.svg)只有一个未访问的邻居是![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)。所以，我们搬到![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg).
5.  同样，![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)只有一个未访问的邻接点，即![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)。因此，下一个节点应该是![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)。请注意![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)has also![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)和![丁](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c10ec9debc8ec5dce4c3c5887557202d_l3.svg)as adjacents。但是，这些节点已经被访问过。
6.  ![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)只有![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-638a7387bd72763290cc777a9b509c38_l3.svg)那个是无人问津的。因此，我们移动到![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-638a7387bd72763290cc777a9b509c38_l3.svg)。
7.  最后，该节点![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)没有任何未访问的邻居。该算法将缩回到![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg), ![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg), ![丁](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c10ec9debc8ec5dce4c3c5887557202d_l3.svg), ![F](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-88df03c55e081c7cd9da4e7d74ba7265_l3.svg), ![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg), 然后![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)。这些节点不会有任何未访问的相邻节点。因此，算法简单地结束。

结果，被访问节点的最终顺序是{ ![boldsymbol{S, A, F, D, B, C, E}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f09769ec40fa6195d7b507c47c70b882_l3.svg)}。

## 3.递归DFS

下面介绍递归版本的 DFS 算法。看一下实现：

```

```

首先，我们定义![参观过](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e7eb31be786e06ddf19f5514122132d1_l3.svg)将用![错误的](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1bd109d4c0a6d9f4b9978b1f7b1a48a3_l3.svg)值初始化的数组。数组的![boldsymbol{访问过}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-50502ba4978c84a8dd38e3902de601fa_l3.svg)作用是判断访问过哪些节点，防止算法多次访问同一个节点。

数组准备就绪后![参观过](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e7eb31be786e06ddf19f5514122132d1_l3.svg)，我们调用该![数字文件系统](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9a3c5c9fef907ddbbbb0c04ce00a918a_l3.svg)函数。

该![数字文件系统](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9a3c5c9fef907ddbbbb0c04ce00a918a_l3.svg)函数首先检查节点是否![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)被访问过。如果是这样，它只是返回而不执行任何更多操作。

但是，如果未访问该节点![boldsymbol{u}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9c7105ad71e0c2e50f7172862e65018e_l3.svg)，则该函数会打印它。此外，该函数将节点标记![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)为已访问，以防止在以后的步骤中再次访问它。

由于节点已处理，我们可以迭代其邻居。对于每一个，我们![数字文件系统](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9a3c5c9fef907ddbbbb0c04ce00a918a_l3.svg)递归地调用函数。

递归版本的复杂度为![boldsymbol{O(V + E)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7155cc1a42061c2816a1b50e3d9fee5f_l3.svg)，其中![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54e215a7a583b4f357a5a627420bcf2f_l3.svg)是节点数，![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-638a7387bd72763290cc777a9b509c38_l3.svg)是图中的边数。

![数字文件系统](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9a3c5c9fef907ddbbbb0c04ce00a918a_l3.svg)复杂性背后的原因是我们只通过函数访问每个节点一次。此外，对于每个节点，我们只迭代其邻居一次。因此，我们将对图中的每条边进行两次探索。假设边在![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)和之间![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)，那么第一次探索![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)作为 的相邻边![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)，第二次探索![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)作为 的相邻边![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)。

因此，总的来说，内部![为了](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5f8e0d452dd2876554f653f09e710d64_l3.svg)循环被执行的![2 乘以 E](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec908f1dc19ce3b2bac0c19259de20d5_l3.svg)次数。

现在我们对递归 DFS 版本有了很好的了解，我们可以将其更新为迭代(非递归)。

## 4.迭代DFS

让我们从分析递归 DFS 版本开始。由此，我们可以逐步构建迭代方法。

### 4.1. 分析递归方法

阅读递归 DFS 伪代码后，我们可以得出以下注意事项：

1.  对于每个节点![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)，DFS 函数一个一个地探索其相邻节点。但是，该函数仅对未访问的函数执行递归调用。
2.  在探索相邻节点时，DFS 函数将探索的下一个节点是邻接列表中第一个未访问的节点。这个命令必须得到尊重。
3.  一旦 DFS 函数完成访问连接到节点的整个子图，它就会终止并返回到前一个节点(对节点执行递归调用的节点![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg))。
4.  它的工作原理是程序将函数的当前参数和变量压入堆栈。递归调用完成后，程序从堆栈中检索这些变量并继续执行。因此，我们需要使用手动堆栈将递归 DFS 更新为迭代版本。

让我们使用上面的注释来创建迭代版本。

### 4.2. 理论构想

我们需要做的是模拟程序栈的工作。首先，让我们将起始节点压入堆栈。然后，我们可以执行多个步骤。

在每一步中，我们将从堆栈中提取一个元素。这应该类似于开始对 DFS 函数进行新的递归调用。一旦我们从堆栈中弹出一个节点，我们就可以遍历它的邻居。

要访问提取节点的相邻节点，我们也需要将它们压入堆栈。但是，如果我们将节点压入堆栈，然后提取下一个节点，我们将得到最后一个邻居(因为堆栈是先进先出的)。

因此，我们需要按照节点在邻接表中出现的相反顺序将节点添加到堆栈中。请注意，仅当我们需要遵循递归版本的相同顺序时，此步骤才是强制性的。如果我们不关心邻接表中节点的顺序，那么我们也可以简单地将它们以任意顺序添加到堆栈中。

之后，我们将从堆栈中提取一个节点，访问它并遍历它的邻居。 这个过程应该一直持续到堆栈变空为止。

列出算法的主要思想后，我们就可以组织一个整齐的迭代实现。

### 4.3. 执行

我们来看看迭代DFS的实现。

```

```

与递归版本类似，我们将初始化一个数组![参观过](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e7eb31be786e06ddf19f5514122132d1_l3.svg)，其![错误的](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1bd109d4c0a6d9f4b9978b1f7b1a48a3_l3.svg)值将指示到目前为止我们是否已经访问过每个节点。然而，在迭代版本中，我们还初始化了一个空堆栈，它将模拟递归版本中的程序堆栈。

接下来，我们将起始节点压入堆栈。之后，我们将执行类似于递归版本的多个步骤。

在每一步中，我们从堆栈中提取一个节点，模拟进入一个新的递归 DFS 调用。然后，我们检查我们是否已经访问过这个节点。如果我们这样做了，那么我们将继续而不处理该节点或访问其邻居。

但是，如果该节点未被访问，我们将打印它的值(表明我们处理了该节点)并将其标记为已访问。此外，我们遍历它的所有相邻元素并将它们压入堆栈。将相邻元素压入堆栈模拟对它们中的每一个执行递归调用。

然而，在迭代版本中，我们必须以相反的顺序迭代邻居。因此，我们假设以相反的顺序![G[u]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8a3d18e7690d093b959e3118c51710db_l3.svg)返回相邻的。![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)

此外，我们不会将所有邻居都压入堆栈，而只会将未访问过的邻居压入堆栈。这一步是不必要的，但它允许我们减少堆栈内的节点数量，而不影响结果。

由于迭代方法的步骤与递归方法相同，因此复杂度为![boldsymbol{O(V + E)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7155cc1a42061c2816a1b50e3d9fee5f_l3.svg)，其中![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54e215a7a583b4f357a5a627420bcf2f_l3.svg)是节点数，![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-638a7387bd72763290cc777a9b509c38_l3.svg)是图中的边数。

## 5.例子

考虑以下图形示例：

![分步示例](https://www.baeldung.com/wp-content/uploads/sites/4/2020/09/Step_By_Step_Example-255x300-1.png)

让我们看看递归和非递归 DFS 版本如何打印该图的节点。

在递归 DFS 的情况下，我们在下面的示例中显示前三个步骤：

![递归示例](https://www.baeldung.com/wp-content/uploads/sites/4/2020/09/Recursive_Example_1-1024x413-1.png)

请注意，递归调用尚未结束的节点用蓝色标记。

首先，我们从节点开始![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)。![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)我们探索它的邻居，因此在第二步移动到节点。在探索节点的邻居之后![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)，我们继续到节点![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)。

现在让我们看看接下来的三个步骤是怎样的：

![递归示例 2](https://www.baeldung.com/wp-content/uploads/sites/4/2020/09/Recursive_Example_2-1024x413-1.png)

在这个例子中，我们有一些节点的递归函数已经完成。这些节点用红色标记。

我们从节点![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)探索它的邻居![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)并移动到它。

现在，事情变得棘手了。我们注意到![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)没有任何未访问的邻居。因此，我们完成了对 node 的递归调用![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)并返回到 node ![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)。

但是，该节点![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)也没有任何未访问的相邻节点。因此，我们也完成了它的递归调用，并返回到 node ![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)。

我们注意到 node![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)有一个未访问的邻居 node ![丁](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c10ec9debc8ec5dce4c3c5887557202d_l3.svg)。因此，它执行从 node 开始的递归调用![丁](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c10ec9debc8ec5dce4c3c5887557202d_l3.svg)。但是，节点![丁](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c10ec9debc8ec5dce4c3c5887557202d_l3.svg)似乎没有任何未访问的节点。因此，该函数返回到 node ![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)。

由于我们完成了对 node 的所有相邻节点的访问![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)，该函数返回到 node ![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)。我们注意到该节点![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)没有更多未访问的节点。因此，功能停止。

结果，我们注意到按顺序访问的节点是 { ![boldsymbol{S, A, C, B, D}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fa1f1651a583ddf128a8b18b8047cfc6_l3.svg)}。

现在，让我们检查迭代方法将如何处理图形示例。

### 5.1. 迭代 DFS

考虑迭代 DFS 的前三个步骤：

![迭代示例](https://www.baeldung.com/wp-content/uploads/sites/4/2020/09/Iterative_Example_1-1024x535-1.png)

在迭代 DFS 中，我们使用手动堆栈来模拟递归。堆栈用蓝色标记。此外，到目前为止所有访问过的节点都用红色标记。

![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)一开始，我们在第一步中将节点添加到堆栈中。![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)一旦我们从堆栈中弹出节点，它就会被访问。因此，我们用红色标记它。由于我们访问了 node ![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)，我们以相反的顺序将其所有邻居添加到堆栈中。

接下来，我们![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)从堆栈中弹出并压入所有未访问的邻居![丁](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c10ec9debc8ec5dce4c3c5887557202d_l3.svg)和![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)。![丁](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c10ec9debc8ec5dce4c3c5887557202d_l3.svg)在这种情况下，我们再次将节点添加到堆栈中。这完全没问题，因为 node![丁](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c10ec9debc8ec5dce4c3c5887557202d_l3.svg)是 和 的邻居![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)，![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)而且它仍然没有被访问过。

让我们来看看迭代方法的下三个步骤：

![迭代示例 2](https://www.baeldung.com/wp-content/uploads/sites/4/2020/09/Iterative_Example_2-1024x535-1.png)

我们从堆栈中弹出节点![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)，将其标记为已访问，并将其相邻的![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg).

下一步是提取![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)并标记为已访问。但是，在这种情况下，节点![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)没有任何未访问的邻居。所以，在这种情况下，我们不会将任何东西压入堆栈。

我们弹出的下一个节点是 node ![丁](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c10ec9debc8ec5dce4c3c5887557202d_l3.svg)。同样，我们已经访问了它的所有邻居，所以我们不会在它的位置上推送任何东西。

在我们访问了所有节点之后，我们仍然有节点![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)和![丁](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c10ec9debc8ec5dce4c3c5887557202d_l3.svg)堆栈内部。所以，我们弹出这些节点并继续，因为我们已经访问过它们。

请注意，我们得到了与递归方法类似的访问顺序。访问顺序也是{ ![boldsymbol{S, A, C, B, D}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fa1f1651a583ddf128a8b18b8047cfc6_l3.svg)}。

递归和迭代方法都具有相同的复杂性。因此，通常只使用递归方法更容易，因为它更易于实现。

但是，在极少数情况下，当程序的堆栈较小时，我们可能需要使用迭代方法。原因是程序在堆空间内部分配了手动栈，[堆](https://www.baeldung.com/java-stack-heap)空间通常比栈空间大。

## 六，总结

在本教程中，我们介绍了深度优先搜索算法。首先，我们解释了该算法的一般工作原理并介绍了递归版本的实现。

接下来，我们展示了如何模拟递归方法以迭代方式实现它。

之后，我们提供了两种方法的分步示例并比较了结果。

最后，我们总结了一个快速比较，向我们展示了何时使用每种方法。