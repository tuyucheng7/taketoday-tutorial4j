## 1. 概述

[二叉树](https://www.baeldung.com/cs/binary-tree-intro)是一种分层数据结构，其中每个节点最多有两个子节点。本教程将展示如何计算二叉树级别和节点数。我们还将检查树级别和节点数之间的关系。

## 2. 二叉树级别

在二叉树中，每个节点都有 3 个元素：一个数据元素，用于保存数据值，两个子指针指向其左右子节点：

 

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8d8e6bec34dbd5f3337a3e8a8ad860ad_l3.svg)

 

二叉树的最顶层节点是根节点。节点的级别是沿着它和根节点之间的唯一路径的边数。因此，根节点的级别为 0。如果它有子节点，则它们的级别均为 1。

二叉树的级别是所有节点中最高级别的编号。例如，我们可以用5个节点构造一棵层数为2的二叉树：

![树 5nodes 大](https://www.baeldung.com/wp-content/uploads/sites/4/2021/06/tree_5nodes_big.png)

## 3. 树级和节点数计算

要计算二叉树的层次，我们可以逐层遍历树。我们从第 0 级的根节点开始。然后我们在进入另一个级别之前访问一个级别上的每个节点。例如，上述示例树的逐级遍历顺序为1、2、3、4、5。

逐级二叉树遍历也是一种[广度优先搜索(BFS)](https://www.baeldung.com/java-breadth-first-search)方法。为了实现这种遍历，我们可以使用一个[队列](https://www.baeldung.com/cs/common-data-structures#2-queues)数据结构来保证遍历的顺序。

队列的每个元素包含两个值，当前树节点和当前树节点的层数：

 

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-aeeb5c6f0dc49d0cfc46196835ecb3b7_l3.svg)

 

遍历后，最大层数就是树的层数。我们还可以使用计数器来计算遍历过程中的节点数：

 

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1400bf0bf1d6aec88acbd81c78652869_l3.svg)

 

在这个算法中，我们从![根](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-98fe607fdcb50415059be670f5541cfe_l3.svg)节点开始。首先，我们将![根](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-98fe607fdcb50415059be670f5541cfe_l3.svg)节点及其级别编号放入队列![问](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd440a7af28975f52f03607a49307d46_l3.svg)中。然后我们使用循环按级别遍历所有树节点。

在每次迭代中，我们将当前树节点的级别增加 1，并将其与节点的左右子节点相关联。在遍历过程中，我们记录最大层数。在我们访问所有树节点之后，最大值就是二叉树的级别。

如果二叉树包含![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)节点，则该算法的总运行时间是![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg)因为我们只访问每个节点一次。

## 4.最小和最大节点数

我们可以使用上面的算法来计算二叉树的确切节点数及其级别数。![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)此外，我们可以通过理论分析确定具有层次的二叉树的最小和最大节点数。

### 4.1. 最小节点数

很容易看出，我们需要每个级别至少有一个节点来构造一个级别为 的二叉树![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)。因此，具有 level 的二叉树的最小节点数![textbf{textit{n}}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-72215ff19e94f18d4b955087aad3d41b_l3.svg)为![textbf{textit{n}+1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3c9ca8acc461c1e6352acc8e056eae96_l3.svg)。此二叉树的行为类似于 [链表](https://www.baeldung.com/java-linkedlist)数据结构：

![树最小大](https://www.baeldung.com/wp-content/uploads/sites/4/2021/06/tree_min_big.png)

我们可以用以下定理得出最小节点数：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-03174e3fbadcd141c2fb150a40a44e3e_l3.svg)

### 4.2. 最大节点数

要构造一棵节点数最大的层次二叉树![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)，我们需要确保所有内部节点都有两个孩子。此外，所有的叶节点都必须在 level ![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)。

例如，在第 0 层，我们只有根节点。在第 1 层，我们有 2 个节点，它们是根的子节点。同样，我们在第 2 层有 4 个节点，它们是第 1 层节点的子节点：

![树最大](https://www.baeldung.com/wp-content/uploads/sites/4/2021/06/tree_max_big.png)

基于这一观察，我们可以看到每一层的节点数量都比上一层增加了一倍。这是因为每个内部节点都有两个孩子。因此，层次二叉树的最大节点数为。![textbf{textit{n}}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-72215ff19e94f18d4b955087aad3d41b_l3.svg)![bf{1 + 2 + 4 + ... + 2^textbf{textit{n}} = 2^{textbf{textit{n}}+1} - 1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-267489d581d836c69f0f973b06aef433_l3.svg)我们也称这种二叉树为[满二叉树](https://www.baeldung.com/cs/complete-vs-almost-complete-binary-tree#full-binary-tree)。

我们可以用以下定理得出最大节点数：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9ca7b09ac29241f7ed5780d4ace9efd6_l3.svg)

## 5.归纳法证明

我们还可以使用归纳法来证明定理 2。对于基本情况，其中![n=0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4ec649ce41e8a80902834df9895d3054_l3.svg)，级别 0 的二叉树只是一个没有任何子节点的根节点。此外，当 时![n=0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4ec649ce41e8a80902834df9895d3054_l3.svg)，我们有![2^{0+1} - 1 = 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-019f7396b9ee17775bd5777dd07439a9_l3.svg)。因此，基本情况满足归纳假设：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e478ce6b5ceb79cb55c5ad98b84d164c_l3.svg)

在归纳步骤中，设![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)是一个级别为 的二叉树![k+1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0ac6c7d4927562b6a104d02b0ef2a694_l3.svg)。对于 的根节点![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)，它的右子树和左子树都有级别![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)：

![树感应大](https://www.baeldung.com/wp-content/uploads/sites/4/2021/06/tree_induction_big.png)

的节点![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)总数是它的两个子树和根节点的总和。因此，![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)最多包含![(2^{k+1} - 1) + (2^{k+1} - 1) + 1 = 2^{k+2} - 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3f85e86e5cd2a9cc80413d9ed667c470_l3.svg)节点。假设成立![k+1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0ac6c7d4927562b6a104d02b0ef2a694_l3.svg)，因此定理得证。

## 六，总结

本文展示了如何计算二叉树的层级和节点数。我们还提供了关于 level 的二叉树的最小和最大节点数的理论结果![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)。![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)最后，我们用归纳法证明了层次二叉树的最大节点数为![2^{n+1} - 1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3a40d68c463326e6316af76b97913a89_l3.svg)。