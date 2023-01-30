## 1. 概述

在本教程中，我们将解释二分搜索树和 AVL 树在[时间复杂度上的差异。](https://www.baeldung.com/cs/time-vs-space-complexity)更具体地说，我们将关注从数组构造树的预期和最坏情况。

当谈到空间复杂性时，无论![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg)分析何种情况，两棵树都需要构建空间。

## 2.二叉搜索树和AVL树

在[二叉搜索树 (BST)](https://www.baeldung.com/cs/binary-tree-vs-binary-search-tree)中，每个节点的值都![<](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-33e2024a0e9d370a1dbcfcbadc5c9bca_l3.svg)大于其左后代的值和![leq](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6b1bafdb4cc9099d816ba3c561f9de94_l3.svg)其右子树中的值。

BST 的目标是允许高效搜索。尽管大多数情况下都是这样，但需要注意的是 BST 的形状取决于我们插入其节点的顺序。在最坏的情况下，普通 BST 退化为链表并具有![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg)搜索复杂性：

![偏二叉搜索树](https://www.baeldung.com/wp-content/uploads/sites/4/2022/04/Skewed-BST-300x300.jpg)

[平衡树](https://www.baeldung.com/cs/balanced-trees)就是为了克服这个问题而设计的。在构建平衡树时，我们保持适合快速搜索的形状。特别是对于[AVL树](https://www.baeldung.com/cs/red-black-tree-vs-avl-tree)，每个节点的左右子树的高度最多相差1：

![AVL 树](https://www.baeldung.com/wp-content/uploads/sites/4/2022/04/AVL.jpg)

 

该属性意味着AVL 树的高度为![boldsymbol{O(log n)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2a97646d90a7799b15137c31e3bbbb66_l3.svg). 为了确保这一点，如果插入使树不平衡，我们需要重新平衡树，这会产生计算开销。

在这里，我们将分析从一组![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)可比较对象构建 AVL 树和 BST 的复杂性：![A = [a_1, a_2, ldots, a_n]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0142fd6d512a316d237b10556922f943_l3.svg)

## 3. 最坏情况的复杂性分析

让我们首先分析最坏的情况。对于 BST 和 AVL 树，构造算法遵循相同的模式：

```

```

该算法从一棵空树开始，依次插入元素![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)直到处理整个数组。为了放置![a_i](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0ea7aa2fd00c5c5980e75b075179d117_l3.svg)，算法首先在包含 的树中寻找它![a_1, a_2, ldots, a_{i-1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-db20e559b1ff2d23a39c6756d1594043_l3.svg)。搜索过程识别![X](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e5fbfa0bbbd9f3051cd156a0f1b5e31_l3.svg)其子节点![a_i](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0ea7aa2fd00c5c5980e75b075179d117_l3.svg)应该成为的节点。然后，算法根据或插入![a_i](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0ea7aa2fd00c5c5980e75b075179d117_l3.svg)为 的左孩子或右孩子。![X](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e5fbfa0bbbd9f3051cd156a0f1b5e31_l3.svg)![a_i < x.value](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-496872374edf2e2dcfc94028eddd6e9c_l3.svg)![a_i geq x.value](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a6aabbeee7a304ba85172aac3dd76167_l3.svg)

### 3.1. 构建 BST

在最坏的情况下，当插入顺序![a_1 leq a_2 leq ldots leq a_n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0570903752e494fc939198315e4c16fc_l3.svg)使树倾斜时，[插入BST](https://www.baeldung.com/cs/tree-structures-differences#1-insertion-in-a-bst)是一个线性时间操作。

由于树本质上是一个列表，![a_i](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0ea7aa2fd00c5c5980e75b075179d117_l3.svg)在最坏的情况下添加到树中需要遍历所有先前附加的元素：![a_1 , a_2, ldots a_{i-1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1dc22791a9ed1373cb6109206374ad95_l3.svg)。总的来说，我们遵循以下![boldsymbol{O(n^2)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-80747e954a21a5d93b93213b6eff1090_l3.svg)几点：

(1) ![begin{方程式} sum_{i=1}^{n} (i-1) = sum_{i=0}^{n-1} i = frac{n(n-1)}{2 } in O(n^2) end{方程式}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-315102ce701e8f34017c0e78c164ce57_l3.svg)

因此，在最坏的情况下，构建 BST 需要二次方时间。

### 3.2. 构建 AVL 树

[插入 AVL 树需要对数线性时间](https://www.baeldung.com/cs/red-black-tree-vs-avl-tree#2-insertion)。原因是 AVL 树的高度与节点数成对数关系，因此在最坏情况下![O(log i)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f692ce92a9cab08827fd7a1ed1aa86f4_l3.svg)插入时我们只遍历边![a_i](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0ea7aa2fd00c5c5980e75b075179d117_l3.svg)总共：

(2) ![begin{方程式} sum_{i=1}^{n} O(log i) = sum_{i=1}^{n} underbrace{O(log n)}_{text{因为 } i leq n} = O(n log n) end{equation}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-820c7b14ca039392f0294f474fea5e82_l3.svg)

构建 AVL 树的最坏情况复杂度为![boldsymbol{O(n log n)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7b283c809cb79130cd494e804c77b58f_l3.svg)。因此，尽管插入可以触发重新平衡，但 AVL 树的构建速度仍然比普通 BST 快。

## 4. 预期的复杂性

在下面的分析中，我们假设所有![n！](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-79091dc7f6b7a3fb457c2eb48009e337_l3.svg)的插入顺序![A=[a_1, a_2, ldots, a_n]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-34117bf9e043b106e46ab834ce684c72_l3.svg)都是等可能的。

### 4.1. 二叉搜索树

单个插入的复杂性取决于 BST 中的平均节点深度。我们可以将其计算为节点深度之和除以节点数 ( ![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg))。

如果根的左子树有![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)节点，则右子树包含![你](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b50fb36b7dd6364dfd92efe05ab65a89_l3.svg)顶点。那么，在这样的 BST 中，深度的预期总和![D(n, i)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-766d9911c84b76db6d33380798e9c5dd_l3.svg)为：

(3) ![begin{方程} D(n, i) = n-1 + D(i) + D(ni-1) qquad (D(1)=0) end{方程}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fa781c21fe6dca7b64a3b94a093f75bd_l3.svg)

我们添加![n-1个](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3fd905b384548c9de7011828b88081d5_l3.svg)是因为根节点将所有其他节点的深度增加 1。

由于 的所有排列![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)都是等概率的，![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)因此服从 的均匀分布![{0, 1, ldots, n-1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3a4ce7268ab28c194c107fa5eea11a0b_l3.svg)。因此，具有节点的 BST 中节点的预期深度![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)为：

(4) ![begin{方程} D(n) = frac{1}{n}sum_{i=0}^{n-1} D(n, i) = n-1 + frac{2}{n }sum_{i=0}^{n}D(i) end{方程}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8c0bc497a20d6caaff77d7dd88d4d4e9_l3.svg)

求解递归，我们得到![D(n) in O(n log n)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4db7d7f33e9e1e1eceb6f59f3a2fd951_l3.svg)。因此，具有节点的 BST 中的预期节点深度![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)为![O(log n)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2d57cfd455039a8d5f3413d90de473e0_l3.svg)。因此，从一棵空树开始并插入![a_1, a_2, ldots, a_n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-66850f5b25557305ea5e46628d13f17f_l3.svg)，我们执行![O(nlogn)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ce2a2eb1367b742f42579125e1e491f3_l3.svg)以下步骤：



(5) ![begin{方程式} sum_{i=2}^{n} O(log (i-1)) = sum_{i=2}^{n} underbrace{O(log n)}_ {text{since } i -1 leq n} = O(n log n) end{equation}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-77db068d816504ab2c272caa44d7990c_l3.svg)

因此，在一般情况下，BST 的构造需要对数线性时间。

### 4.2. AVL树

由于AVL 树是平衡的并且不依赖于插入顺序，因此树深度总是![O(log n)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2d57cfd455039a8d5f3413d90de473e0_l3.svg). 此外，AVL 树最多![2^k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-aa1879d5584a853bb784da5ccdb6c2ef_l3.svg)有深度为 的节点![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)。因此，平均节点深度受以下限制：

(6) ![begin{方程} frac{1}{n}sum_{k=0}^{O(log n)}k 2^k end{方程}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4fa1c10ecf0dad9ba13416078af8dc8b_l3.svg)

自从：

 ![[sum_{k=0}^{d} k 2^k = 2^{d+1}(d-1)+2]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-86a6a1ef7b8e545d456bfa0a412b94e0_l3.svg)

AVL 树中的预期节点深度为：

(7) ![begin{方程} O left( frac{1}{n} 2^{log n + 1}(log n - 1) + 2 right) = O left( frac{1}{ n} n log n right) = O(log n) end{方程式}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4122a98caa4e987e2e9b9ba97fe29152_l3.svg)

由于平均节点深度与 BST 中的相同，因此等式 ( [5](https://www.baeldung.com/cs/bst-vs-avl-construction-complexity#id2669474992) ) 也适用于 AVL 树。因此，在预期情况下，构建 AVL 树也是对数线性操作。

## 5.总结

在本文中，我们比较了二叉搜索树 (BST) 和 AVL 树的构造复杂性。在最坏的情况下，构建 AVL 树需要![O(nlogn)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ce2a2eb1367b742f42579125e1e491f3_l3.svg)时间，而构建 BST 具有![O(n^2)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-894959b13d80157796705e7eafb4d243_l3.svg)复杂性。

但是，在预期情况下，两棵树都需要对数线性时间。