## 1. 概述

在本教程中，我们将讨论二叉搜索树中的插入过程。我们将举例说明插入过程，分析插入算法的复杂度。

## 2. 二叉搜索树的快速回顾

[二叉搜索树 (BST)](https://www.baeldung.com/cs/binary-search-trees)是一种基于树的有序数据结构，满足二分搜索特性。二分搜索性质指出二叉搜索树中所有左节点的值都小于其根节点，而二叉搜索树中所有右节点的值均大于其根节点。

二叉搜索树是一种非常有效的数据结构，用于在树中插入、删除、查找和删除节点。

## 3.插入示例

在插入过程中，给定一个新节点，我们将该节点插入到 BST 中的适当位置。这里的要点是在 BST 中找到正确的位置，以便插入新节点。此外，我们需要确保在插入新节点后树满足二分搜索属性。

让我们以二叉搜索树为例，我们想插入一个值为 的节点![17](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-99fb9008ca63ec321863e498803ce34e_l3.svg)：

![3](https://www.baeldung.com/wp-content/uploads/sites/4/2020/12/3-1024x438.png)

插入过程的伪代码可以在[二叉搜索树快速指南中](https://www.baeldung.com/cs/binary-search-trees)找到。

## 4. 插入的时间复杂度

### 4.1. 最坏的情况

假设现有的二叉搜索树在每一层都有一个节点，并且它是一棵左偏树或右偏树——这意味着所有节点的一侧都有子节点或根本没有子节点。我们想要插入一个节点，其值在右偏二叉搜索树的情况下大于最高级别节点的值，或者在左偏二叉搜索树的情况下小于最高级别节点的值。让我们看一个示例来演示这些情况：

![4](https://www.baeldung.com/wp-content/uploads/sites/4/2020/12/4.png)

在这个例子中，我们采用右偏二叉搜索树，我们想插入一个值为 的节点![18](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-389b768e82881d2558eba252411bb388_l3.svg)。请注意，新节点的值大于现有二叉搜索树中最高级别节点的值。

当我们插入一个新节点时，我们首先检查根节点的值。如果新节点的值大于根节点，我们就在右子树中寻找可能的插入位置。否则，我们探索左子树以进行插入。

这里的新节点在右子树中搜索可能的插入位置。我们将新节点的值与当前树的所有节点进行比较，并在二叉搜索树的最后一层找到它的插入位置。有趣的是，在这种情况下，我们需要将新节点的值与现有树中的所有节点进行比较。让我们举一个左偏二叉搜索树的例子：

![5-1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/12/5-1.png)

在这里，我们要插入一个值为 的节点![9](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-824dc08b6ac6c7e5c07f1113ebaab27b_l3.svg)。首先，我们看到根节点的值。由于新节点的值小于根节点的值，我们搜索左子树以进行插入。我们再次将新节点的值与现有树中每个节点的值进行比较。最后，我们将新节点插入到二叉搜索树的最后一层。

在这两种情况下，我们都必须从根节点移动到最深的叶节点，以便找到插入新节点的索引。![否](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7354bae77b50b7d1faed3e8ea7a3511a_l3.svg)如果二叉搜索树中有节点，我们需要![否](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7354bae77b50b7d1faed3e8ea7a3511a_l3.svg)比较以插入我们的新节点。因此，在这种情况下，插入过程的总体时间复杂度为 ![mathbf{mathcal{O}(N)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a873d84ab92ed465c4316ca678ae933f_l3.svg).

### 4.2. 平均情况

在这种情况下，现有的二叉搜索树是[平衡树](https://www.baeldung.com/cs/height-balanced-tree)。与最坏的情况不同，我们不需要将新节点的值与现有树中的每个节点进行比较：

![6](https://www.baeldung.com/wp-content/uploads/sites/4/2020/12/6-1024x434.png)

现有的二叉搜索树是一棵平衡树，当每一层都有![2^大号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6d63ecfc302c9699ff915a0d24e89e6d_l3.svg)节点时，其中![大号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-48d71fca322532f0abc2c4ad2cf98154_l3.svg)是树的层级。现在我们要插入一个带有 value 的节点![26](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b0318505db93e696379fc6c67d56fca6_l3.svg)。首先，我们检查根节点的值。由于它小于新节点的值，我们探索正确的子树以进行插入。这里我们需要做的比较次数![3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ce2009a45822333037922ccca0872a55_l3.svg)就是![(大+1)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5a92e8e7bd1ca621a58f510769712fc8_l3.svg)。

对于我们要插入的任何新节点，![(大+1)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5a92e8e7bd1ca621a58f510769712fc8_l3.svg)将是所需的最大比较次数，即二叉搜索树的[高度](https://www.baeldung.com/cs/height-balanced-tree)(这里，L 是现有二叉搜索树的级别)。二叉搜索树的高度也等于![冷静的](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-053d528f77b609a65d4e8bb472c7a2bb_l3.svg)，其中![否](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7354bae77b50b7d1faed3e8ea7a3511a_l3.svg)是二叉搜索树中节点的总数。

因此，在最佳和平均情况下，二叉搜索树中插入操作的时间复杂度为 ![mathbf{mathcal{O}(logN)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a0a95764485ad4fcb5d7da276a324922_l3.svg)。

### 4.3. 最佳案例

最好的情况发生在所有节点都在根的右子树中，要插入的节点在左子树中，或者所有节点都在根的左子树中，要插入的节点在右子树中。让我们用一个例子来形象化它：

![最佳 1-1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/12/best-1-1.png)

这里现有的二叉搜索树是一棵左偏树，我们想插入一个值![22](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e7064dd7b030560934178c324bc1b251_l3.svg)大于根节点值的新节点。由于树是左偏的，因此我们只需要将新节点的值与根节点进行比较。现在让我们看看现有树右偏的另一种情况：

![最佳 2](https://www.baeldung.com/wp-content/uploads/sites/4/2020/12/best-2.png)

这里现有树是右偏树，新节点的值小于根节点。因此，我们只需要执行一次比较即可插入新节点。因此在最好的情况下， 二叉搜索树中插入操作的时间复杂度为 ![mathbf{mathcal{O}(1)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f732a4fd6d3ca554b70fda280cd74077_l3.svg)。

## 5.总结

在本教程中，我们详细讨论了二叉搜索树的插入过程。我们介绍了时间复杂度分析，并通过示例演示了不同的时间复杂度案例。