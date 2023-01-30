## 1. 概述

在本教程中，我们将展示如何在[二叉搜索树](https://www.baeldung.com/cs/binary-tree-vs-binary-search-tree)![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)中找到第-th 最小的元素。

## 2. 第![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)-th 最小的元素

让我们假设一个二叉搜索树![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)有![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)元素：![a_1, a_2, ldots, a_n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-66850f5b25557305ea5e46628d13f17f_l3.svg)。我们想找到第![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)-th 最小的。形式上，如果![a_{(1)} leq a_{(2)} leq ldots leq a_{(n)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a8bafb6cbdbb1bf96164fd2316212f28_l3.svg)是 的排序排列![a_1, a_2, ldots, a_n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-66850f5b25557305ea5e46628d13f17f_l3.svg)，我们想要找到![a_{(k)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2fd681cd1d249ed126c247f061c63c19_l3.svg)给定的![k in {1, 2, ldots, n}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d7462f8e565dd829eb63d3c174964848_l3.svg)。

我们以这棵树为例：

![二叉搜索树的例子](https://www.baeldung.com/wp-content/uploads/sites/4/2022/03/binary-search-tree-example.jpg)

它是一棵二叉搜索树，因为每个节点的值都低于其右后代的值并大于或等于其左子树中的值。例如，让![k=5](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9a10a42d9e19c10981710ec6d922a899_l3.svg)。这是树中第五小的数字：

![树中第五小的元素](https://www.baeldung.com/wp-content/uploads/sites/4/2022/03/The-fifth-smallest.jpg)

## 3.![boldsymbol{k}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-575c9190fdf843dc68c3624f5237ba62_l3.svg)通过中序遍历找到第-th小的元素

由于树的[中序遍历](https://www.baeldung.com/cs/tree-traversal-time-complexity)按排序顺序输出其元素，因此我们可以运行遍历直到获得![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)元素。到那时，我们可以停下来，因为我们已经得到了我们想要的元素：

```

```

我们将遍历过程用作[迭代器](https://www.baeldung.com/java-iterator)，因此使用了关键字[yield](https://realpython.com/introduction-to-python-generators/)。当然，完全递归算法也可以。

### 3.1. 复杂

这种方法适用于每个二叉搜索树，但具有线性时间复杂度。对于![k=n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9031f801acfa608091ff845c0331c7f2_l3.svg)，算法遍历整棵树以获得最大的元素。

## 4.![boldsymbol{k}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-575c9190fdf843dc68c3624f5237ba62_l3.svg)通过跳过子树找到第 -th 最小的元素

![boldsymbol{k}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-575c9190fdf843dc68c3624f5237ba62_l3.svg)如果我们将其后代的数量存储到每个节点中，我们可以更有效地找到第- 个最小元素。例如：

![有大小的树](https://www.baeldung.com/wp-content/uploads/sites/4/2022/03/binary-tree-with-sizes.tif.jpg)

附加信息可以加快(按顺序)遍历。如果我们![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)之前访问过节点![X](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e5fbfa0bbbd9f3051cd156a0f1b5e31_l3.svg)，并且![X](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e5fbfa0bbbd9f3051cd156a0f1b5e31_l3.svg)的左子树有![嗯](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a01f1555785d2234d4294020a2e3968a_l3.svg)节点，那么如果 就可以跳过遍历![m + ell < k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9bfe25003790454df8a4cbb1fee31029_l3.svg)。为什么？因为我们可以确定![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)第 - 个最小的元素不在子树中。因此，“跳过”它是安全的。在这样做时，我们区分两种情况。如果![m + ell = k - 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-59e7d2eb23c66fad487a3e660ea824e3_l3.svg)，则![X](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e5fbfa0bbbd9f3051cd156a0f1b5e31_l3.svg)是![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)树中第 - 个最小的元素。如果![m + ell < k - 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fd19bfe3b540be9627aa82fc0021a397_l3.svg)，我们可以跳过![X](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e5fbfa0bbbd9f3051cd156a0f1b5e31_l3.svg)并直接进入右子树。

相反，如果![m + ell geq k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-684ed8324b1a923ff6002cf8242cd9d7_l3.svg)，第![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)- 个最小的元素肯定在左子树中。

### 4.1. 伪代码

这是伪代码：

```

```

一开始，我们可以测试是否![T.root.size < k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d135683fe743fcc1fbc7fdf1ac8f32e1_l3.svg)检查元素是否存在。

### 4.2. 例子

为了显示修改后的遍历的效率，让我们在找到第五小元素之前检查它在上面的树中访问了多少个节点：

![通过跳跃找到第五小](https://www.baeldung.com/wp-content/uploads/sites/4/2022/03/The-fifth-smallest-with-jumps.jpg)

相比之下，通常的中序搜索会访问我们跳过的左子树中的所有节点：

![第五小，没有跳跃](https://www.baeldung.com/wp-content/uploads/sites/4/2022/03/The-fifth-smallest-no-jumps.jpg)

### 4.3. 复杂

在最坏的情况下，我们跳过的子树是尽可能小的。![k=n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9031f801acfa608091ff845c0331c7f2_l3.svg)如果树中的每个节点只有右孩子，就会发生这种情况。这样的树本质上是一个链表。我们在每一步都跳过零个节点，![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)总共遍历。

通常，我们跳过其子节点的每个节点都在从根节点到包含所查找值的节点的路径上。因此，该算法的最坏情况[时间复杂度](https://www.baeldung.com/cs/time-vs-space-complexity)为![boldsymbol{O(h)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3edb0f28068c853d30a2b052afed6187_l3.svg)，其中![boldsymbol{h}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e72e0c36890a0312cf17c31f231e5fcf_l3.svg)树的高度为，即从根到叶子的最长路径。如果树是[平衡](https://www.baeldung.com/cs/balanced-trees)的，![h in O(log n)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-47da250c9d9201669a140c676951e6f7_l3.svg)则该算法具有对数复杂度。如果没有关于子树大小的附加信息，![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg)即使树是平衡的，普通遍历也会有运行时间。

然而，由于平衡树增加了计算开销，因此只有当查找比插入和删除更频繁时，这种方法才有用。该算法的理想用例是我们构建一次并且之后不会更改的搜索树。

## 5.总结

在本文中，我们展示了如何在[二叉搜索树](https://www.baeldung.com/cs/binary-search-trees)![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)中找到第-th 最小的元素。我们可以使用通常的中序遍历，但它有一个复杂性。如果我们使用平衡树，则将每个子树的大小保持在其根中可以提供更有效的方法。![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg)