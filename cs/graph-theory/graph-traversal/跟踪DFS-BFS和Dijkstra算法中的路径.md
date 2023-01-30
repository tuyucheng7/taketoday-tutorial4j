## 1. 概述

在本教程中，我们将展示如何使用三种算法跟踪路径：深度优先搜索、广度优先搜索和 Dijkstra 算法。更准确地说，我们将展示几种获取图中起始节点和目标节点之间最短路径的方法，而不仅仅是它们的长度。

## 2. 在递归深度优先搜索中追踪路径

[深度优先搜索 (DFS)](https://www.baeldung.com/cs/depth-first-search-intro)有两种[实现](https://www.baeldung.com/java-depth-first-searchd)方式：递归和迭代。在前者中追踪到目标节点的最短路径很简单。我们只需要在到达目标节点后展开递归时存储节点：

```

```

在这里，我们将![小路](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-481f3e20706a1ca950af447b307bfacb_l3.svg)其视为一个数组并将节点添加到它的前面，以使它们按照从起始节点到目标节点的顺序排列。另一种方法是使用堆栈。但是，如果顺序无关紧要，我们可以避免为路径使用单独的数据结构。我们可以在当前节点通过![目标](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-768d81587a9a61594d54d1b029615919_l3.svg)测试后立即打印它，并在从递归调用返回后在 for 循环中执行的 if 语句中继续打印![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-796872219106704832bd95ce08640b7b_l3.svg)，直到我们返回![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg). 这样，我们就避免了使用额外的内存。

### 2.1. 例子

让我们以下图为例：

![示例 1](https://www.baeldung.com/wp-content/uploads/sites/4/2021/10/example1.jpg)

![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)我们想要从到的最短路径![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)。![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)以正确的顺序扩展节点的子节点，DFS 找到和之间的最短路径![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)：

![example1路径](https://www.baeldung.com/wp-content/uploads/sites/4/2021/10/example1path.jpg)

然后，它返回![[T]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b046b88ef5231f9f99b3921141fb931a_l3.svg)到它在其中扩展![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)并![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)添加到![[T]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b046b88ef5231f9f99b3921141fb931a_l3.svg)get的调用![[C, T]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-71af1fd5ea52e03f71c84ec043c9b37b_l3.svg)。![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)对and做同样的事情![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)，DFS 返回![[A、B、C、T]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-189121d373d5556ba73bb6998e8ca886_l3.svg)到原始调用，在![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)它前面添加，并给我们![[S, A, B, C, T]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-20107a614cc6136376f3232cf47e9f3b_l3.svg)作为最短路径。

## 3. 在迭代深度优先搜索中追踪路径

然而，递归 DFS 可能比迭代变体慢：

```

```

我们可以通过两种方式跟踪迭代 DFS 中的路径。在一种方法中，在访问一个节点之后，我们记住它的父节点在搜索树中的哪个节点。这样，在找到目标节点后，我们就可以按照父子层次结构重构路径。在另一种方法中，我们存储每个节点的完整路径。因此，当搜索结束时，通向目标节点的节点也可用。

我们可以使用不同的数据结构来实现这两种技术，有或没有递归。所有的方法都是一样的，我们选择哪一种在很大程度上取决于我们的偏好。

### 3.1. 牢记“亲情”

关键思想是记住我们探索的节点的父节点。假设我们扩展了一个节点![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)并确定了它的子节点![v_1, v_2, ldots, v_m](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-50cae02663051596ce03a5f743ec8a49_l3.svg)。对于每个孩子，我们必须记住它的父母是![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg)：

```

```

### 3.2. 重建路径

然后，为了重建路径，我们只需要读取内存来获取目标的父节点，然后是它的父节点，依此类推，直到到达起始节点：

```

```

这样，我们就得到了从目标节点到起始节点的路径。 如果我们想要反过来，那么我们应该反向打印路径：

```

```

### 3.3. 如何实现内存

我们可以使用不同的数据结构来表示![boldsymbol{u}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9c7105ad71e0c2e50f7172862e65018e_l3.svg)作为父级的信息。![boldsymbol{v}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a5dbf97105434321658a275e8d6092f0_l3.svg)例如，我们可以存储一个指向![boldsymbol{u}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9c7105ad71e0c2e50f7172862e65018e_l3.svg)in![boldsymbol{v}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a5dbf97105434321658a275e8d6092f0_l3.svg)的指针。然后，读取内存相当于遍历链表。

我们也可以使用哈希映射。 一种常见的做法是为每个节点分配一个唯一的正整数作为其id，将id视为索引，并将父子信息存储在数组中。假设那![记忆](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cc9a3b27b1b57f4bee4a183705ab9d57_l3.svg)是那个数组。那么，节点的父节点![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)就是id为 的节点![内存[i]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-67f3b44677f08c0f99aca00017900212_l3.svg)。它的父级是![内存[内存[i]]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-28fe98a5a7917d3000bcdac3bb605d1a_l3.svg)，依此类推。

### 3.4. 例子

![记忆](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cc9a3b27b1b57f4bee4a183705ab9d57_l3.svg)如果 DFS按照对应于![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)和之间最短浴池的顺序扩展节点，则 DFS 会增长![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)：

![例子2](https://www.baeldung.com/wp-content/uploads/sites/4/2021/10/example2-731x1024.jpg)

### 3.5. 搜索时记住路径

我们还可以在运行 DFS 时跟踪当前活动节点的路径。展开节点后![boldsymbol{u}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9c7105ad71e0c2e50f7172862e65018e_l3.svg)，我们会记住它的子节点的路径![boldsymbols{u}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-97b3e7c5e08693efd6f73c293c913b02_l3.svg)是它在搜索树中的子节点，一旦算法识别出目标节点，我们就知道我们已经有了到它的路径。因此，我们不必从记忆的信息中重建它：

```

```

这种方法类似于递归 DFS 中的路径跟踪。但是，我们使用了更多的内存，因为我们在![bodsymbol{边界}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e72ca8eeaf76701271fe831faa5491a4_l3.svg). 在递归 DFS 中，我们只跟踪一条路径。

### 3.6. 例子

这种方法在前面的例子中会像这样工作：

![例子3-1](https://www.baeldung.com/wp-content/uploads/sites/4/2021/10/example3-1-731x1024.jpg)

## 4. 在广度优先搜索中追踪路径

我们用于 DFS 的相同方法也适用于[广度优先搜索 (BFS)](https://www.baeldung.com/java-breadth-first-search)。DFS 和 BFS 之间唯一的算法区别在于队列：前者使用 LIFO，而后者使用 FIFO 实现。但是，这会导致 BFS 使用比 DFS 更多的内存，因此路径存储可能不是我们的最佳选择。相反，我们应该考虑使用指向其父节点的指针扩展节点或使用哈希映射。该方法不会引起太多内存开销：

```

```

我们像算法 5 一样重建路径。我们唯一需要注意的是，算法 5 中的“ ![你左箭头](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8a5ff9e1c2a3f2741fae9a072fff05d5_l3.svg) find the parent of ![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e817933126862db10ae510d35359568e_l3.svg) in ![记忆](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cc9a3b27b1b57f4bee4a183705ab9d57_l3.svg) ”行应该实现为“ ![u leftarrow u.parent](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-df5ed0fe9cca69e12f693235858b6100_l3.svg)”以与我们的 BFS 一起工作。

### 4.1. 例子

这就是 BFS 在我们的示例中找到从![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)到的路径的方式![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)：

![示例 bfs](https://www.baeldung.com/wp-content/uploads/sites/4/2021/10/example_bfs-739x1024.jpg)

## 5. 追踪 Dijkstra 算法中的路径

与 DFS 和 BFS 不同，[Dijkstra 算法 (DA)](https://www.baeldung.com/java-dijkstra)查找图中从起始节点到所有其他节点的最短路径的长度。尽管仅限于有限图，但与 DFS 和 BFS 相比，DA 可以处理正加权边。

我们对内存应用与之前相同的想法并将其适应 DA。该算法将在更新图中节点与起始节点之间的当前已知最短路径长度后立即更新内存。数组在[带路径追踪的DA伪代码中](https://en.wikipedia.org/wiki/Dijkstra's_algorithm#Pseudocode)![上一页](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f34e98bebdf4afdabff6c1ba4c0ece93_l3.svg)起到了内存的作用：

```

```

使用![上一页](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f34e98bebdf4afdabff6c1ba4c0ece93_l3.svg)，我们可以重建![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)任意节点 和之间的路径![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fd9cb27edab3f0a8a249bc80cc9c6ee2_l3.svg)：

```

```

如我们所见，这与我们在 DFS 中使用的策略相同。

## 六，总结

在本文中，我们展示了几种在深度优先搜索、广度优先搜索和 Dijkstra 算法中追踪路径的方法。