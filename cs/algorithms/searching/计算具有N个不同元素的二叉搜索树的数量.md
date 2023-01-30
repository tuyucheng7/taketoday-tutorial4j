## 1. 概述

[二叉树](https://www.baeldung.com/cs/binary-tree-intro)是一种分层数据结构，其中每个节点最多有两个子节点。此外，[二叉搜索树 (BST)](https://www.baeldung.com/cs/tree-structures-differences#binary-search-tree)是一种更具体的二叉树，其主要特征是已排序。[因此，当且仅当二叉树的中序遍历](https://www.baeldung.com/cs/depth-first-traversal-methods#in-order-traversal)导致排序序列时，二叉树是 BST 。

本教程将展示如何根据树节点数计算二叉搜索树的数量。

## 2. 二叉搜索树的唯一数目

在 BST 中，每个节点都包含一个可排序的键。例如，我们可以用整数标记每个节点。因此，BST 的每个节点中的关键字都大于或等于左子树中存储的任意关键字，小于或等于右子树中存储的任意关键字。

给定一个![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)不同数字的列表，我们有兴趣计算由这些数字标记的 BST 的数量。不失一般性，我们可以假设数字是![1, 2, 3, ..., n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b3c16fe0ddbef482a0f66be2d2432b6e_l3.svg)。例如，有 5 种可能的二叉搜索树![1, 2, 3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8d0259d685aae49f056787d48197e219_l3.svg)：

![独特的 bst](https://www.baeldung.com/wp-content/uploads/sites/4/2021/07/unique_bst.png)

虽然在小的时候很容易枚举出所有可能的树结构，但是在大![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)的时候唯一的 BST 结构的数量增加得非常快![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)。例如，![16796](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0ce433e3f84bca76e46f4a6dcd43c830_l3.svg)当 时有唯一的 BST 结构![n=10](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d74f3e93537ada990103b9d3c68d90a1_l3.svg)。因此，我们需要用一种算法来计算这个数字。在本教程中，我们将展示计算此数字的不同方法。

## 3.递归方法

让![f(n)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0222be892a743dbfc5e23b3638f32af6_l3.svg)表示 number 的唯一 BST 结构的总数![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)。对于![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)唯一数字列表，我们可以选择任何数字作为根。例如，我们可以选择数字![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)( ![我在 [1,n]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fbbeaa16773fcd4df709bc4086f3a0bf_l3.svg)) 作为根。然后，数字 in![[1, i-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2fe623840ffd1e5dda668fed3b8b8c1a_l3.svg)将在左子树中。此外，中的数字![[i+1,n]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-561f0958ccc060374ca003bdb5a571e8_l3.svg)将位于右子树中。

由于我们![(i-1)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a19e2a014a9a9c71a19046cd8739625a_l3.svg)的左子树中有数字，因此左子树具有![f(i-1)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed01d434265afaf6519689fdb7b344e3_l3.svg)唯一的 BST 结构。同样，右子树具有![f(ni)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f015d32d1ad87c09c67edc14572cc83a_l3.svg)独特的树结构。此外，左右子树中的排列是独立的。因此，当是根时，我们共有![f(i-1) times f(ni)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-06921c62543ee770c39ef45200ca712c_l3.svg)独特的树结构。![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)

由于我们有![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)可能的方法来选择根，我们可以将![f(i-1) times f(ni)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-06921c62543ee770c39ef45200ca712c_l3.svg)的所有可能值加在一起![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-30b0929987b57e90b3708ee044157ee8_l3.svg)

如果我们在一个子树中没有任何节点，那么我们将只考虑另一个子树中唯一树结构的总数。因此，公式的基本情况是![f(0) = 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c4b7f8ca32a51a819a99816071c8a321_l3.svg)。

由此，我们可以根据上式构造一个递归算法：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c58145071e0112f8d268141787c11fb2_l3.svg)

## 4.动态规划方法

通常，基于递归公式的递归算法很简单。但是，它有很多重复计算。为避免这种情况，我们可以使用[动态规划](https://www.baeldung.com/cs/divide-and-conquer-vs-dynamic-programming#dynamic-programming-approach)方法来计算数字。

我们可以先![f(n)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0222be892a743dbfc5e23b3638f32af6_l3.svg)用较小的数字进行计算，然后将结果存储到一个数组中。然后，当我们计算更大的数字时，我们可以重用之前从较小的数字计算的结果：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d25a596b2bb8c56310968a63fbc5ef13_l3.svg)

在这个算法中，我们使用循环来逐步计算我们的数字。对于一个数字![f(i)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d2d1f438d9eb08d58de24260c979d34a_l3.svg)，我们使用另一个循环根据我们的递归公式计算它的值。

例如，我们的基本情况是![f(0) = 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c4b7f8ca32a51a819a99816071c8a321_l3.svg)。然后，我们![f(1)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-70e6f89b1c1a89142ed29da1721d37e6_l3.svg)根据 的值进行计算![f(0)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c29afa9fbc584101cba5f7b8ffb32472_l3.svg)。之后，下一步是![f(2)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4c754ab3318b9e8ad1d0c18f3368fef0_l3.svg)根据 和 的值![f(0)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c29afa9fbc584101cba5f7b8ffb32472_l3.svg)进行计算![f(1)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-70e6f89b1c1a89142ed29da1721d37e6_l3.svg)。

我们对从 1 到 的每个数字重复此过程![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)。因此，当我们计算 时，计算![f(i)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d2d1f438d9eb08d58de24260c979d34a_l3.svg)所需的所有值都已经可用。最终，我们将计算![f(n)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0222be892a743dbfc5e23b3638f32af6_l3.svg)并返回这个值。

由于我们有一个嵌套循环来进行计算，因此该算法的总体时间复杂度为![O(n^2)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-894959b13d80157796705e7eafb4d243_l3.svg).

## 5. 加泰罗尼亚数字法

不同 BST 的数量实际上是一个[Catalan Number](https://www.baeldung.com/cs/calculate-number-different-bst#catalan-number)：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2641d47e62f2e5710cf4c8d2dcb72be9_l3.svg)

因此，我们可以![f(n)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0222be892a743dbfc5e23b3638f32af6_l3.svg)直接从加泰罗尼亚数字计算我们的数字。但是，我们需要根据这个公式计算 3 个阶乘数。

为了避免耗时的阶乘计算，我们还可以在我们的算法中使用加泰罗尼亚数的以下递归属性：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-491fe798aff2bb36c9116c22535dd6fa_l3.svg)

因此，基于这种递归的算法是：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8b1d748476c0fa8ec14fe26a0c47ca06_l3.svg)

由于我们只使用一个循环来进行计算，因此整体运行时间为![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg).

## 六，总结

本教程展示了一个递归公式来计算具有![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)节点的唯一二叉搜索树的数量。此外，我们提供了几种计算此数字的方法。因此，加泰罗尼亚数法是其中计算该数字最快的算法。然而，动态规划方法更直观，因为它直接来自 BST 结构的分析。