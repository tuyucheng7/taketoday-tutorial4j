## 1. 概述

在本教程中，我们将讨论一种自平衡树数据结构：B 树。我们将介绍 B 树的属性和各种操作。

## 2.简介

B树是一种树状数据结构。在这种树结构中，数据以节点和叶子的形式存储。B树被称为[自平衡排序搜索树](https://en.wikipedia.org/wiki/Self-balancing_binary_search_tree)。它是具有附加树属性的[二叉搜索树 (BST)](https://www.baeldung.com/cs/binary-search-trees)的更复杂和更新版本。

二叉搜索树和 B 树之间的主要区别在于B 树可以有多个子节点作为父节点。但是，二叉搜索树只能包含一个父节点的两个子节点。

与二叉搜索树不同，B 树的一个节点中可以有多个关键字。任何节点的键数和子节点数取决于 B 树的[顺序](https://en.wikipedia.org/wiki/Tree_traversal)。键是存储在任何节点上的值。我们来看一个B树的例子：

![示例 1](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/example_1.drawio-1024x589.png)

## 3.属性

B 树的一些性质类似于 BST。父节点的键总是大于左子树节点的键值。同样，父节点的键值总是小于右子树节点的键值。

一个节点可以有多个键。单个节点可以有两个以上的孩子。

所有叶节点必须处于同一级别。叶节点是指没有任何子节点的节点。

假设 B 树的阶数是![否](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7354bae77b50b7d1faed3e8ea7a3511a_l3.svg)。这意味着每个节点可以有最多的 ![否](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7354bae77b50b7d1faed3e8ea7a3511a_l3.svg)孩子。因此，除根节点外，每个节点都可以有最大 ![textbf{(N-1)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0e88498a7adcc73768a2bf7da50933f7_l3.svg) 键和最小键。 ![textbf{{(N/2)-1}}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-67823122085c3556d023380b16118dee_l3.svg)

让我们采用顺序 B 树![N = 6](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e75b9ef250413031ec4dcb17d9b31507_l3.svg)。根据 B-tree 的特性，任何节点都可以拥有最大的![(N-1)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b8d591aa3d3a6700d7bc61014596c9fb_l3.svg)键。因此![5](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-48348ef601c56286abf49bafe09c7af1_l3.svg)在这种情况下是键。此外，任何节点都可以有最少的![{(N/2)-1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c94a47e38913a88621effc7d10c60df9_l3.svg)密钥。因此，![{(6/2) -1} = 2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-80a14e74953c05abbd3bd9730f9b2e43_l3.svg)钥匙。

## 4. B树操作

与任何其他树数据结构一样，可以在 B 树上执行三个主要操作：搜索、插入和删除。让我们一一讨论每个操作。

### 4.1. 搜索中

B 树的结构类似于二叉搜索树，但增加了一些属性。因此，B 树中的搜索操作与 BST 相同。最初，我们从根节点开始搜索。

我们要么从根节点转到左子树，要么转到右子树，这取决于我们要搜索的节点的键值。除此之外，在 B 树中，我们有几个决定，因为一个节点可以有两个以上的分支。

B 树在最佳和最坏情况下的搜索时间复杂度为 ![textbf{mathcal{O}(logn)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c66df23d269e4f61f5656558cb546af4_l3.svg)，其中![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)表示 B 树中元素的总数。

### 4.2. 插入

插入是我们在 B 树中插入或添加元素的操作。在这个过程中，我们需要记住一些要点。我们不能在 B 树的根节点插入任何元素。我们必须从叶节点开始插入元素。

此外，在 B 树中插入节点时，我们必须检查任何节点的最大键数并按升序添加元素。

让我们以在空 B 树中插入一些节点为例。在这里，我们要![10](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f2dd7a07a97336ce3d17ca56d2618366_l3.svg)在一个空的 B 树中插入节点：![(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b653e577bdf4394d9bb2ff88bf8b5d7d_l3.svg)。假设 B 树的顺序是![数学{3}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c634b2b9b88bcd5abe9d7d39b4081440_l3.svg)。因此，一个节点可以包含的最大子节点数是![数学{3}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c634b2b9b88bcd5abe9d7d39b4081440_l3.svg)。此外，在这种情况下，节点的最大和最小键数将是![数学{2}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-276c2cef464470f3f58000ea412deecd_l3.svg)和![数学{1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-277511c02b56b209330acf2e78fd3290_l3.svg)(四舍五入)。

在插入任何元素时，我们必须记住，我们只能按升序插入值。而且，我们总是在叶节点处插入元素，使得B树总是向上生长。

![textbf{1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cd042baf913adbbacf7e51e097565a54_l3.svg)让我们从节点和开始插入过程![textbf{2}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6a4b58be37bb7cd389e7b2615f3ebe56_l3.svg)：

![NO1](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/no1.png)

接下来，我们要插入节点 ![textbf{3}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0b79ca42666f9de58732aca1b245004e_l3.svg)。但是在该叶节点中没有空间用于插入新元素，因为节点可以包含的最大键在![2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8c267d62c3d7048247917e13baec69a5_l3.svg)这里。因此，我们需要拆分节点。为了拆分节点，我们采用中间键并将该键向上移动：

![第一个元素插入](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/First_element_insertion.drawio-1024x228.png)

现在我们要插入下一个元素，即 ![textbf{4}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-32be00fb3e4c3faa5f9c9d681510e04a_l3.svg). 由于![数学{4}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-37d1fe1e61fadb833cc17cf696d76e41_l3.svg)大于![数学{2}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-276c2cef464470f3f58000ea412deecd_l3.svg)，我们首先转到右子树。在右子树中，有一个带有 key 的节点![数学{3}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c634b2b9b88bcd5abe9d7d39b4081440_l3.svg)。传入节点的键值大于![数学{3}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c634b2b9b88bcd5abe9d7d39b4081440_l3.svg)。因此，我们将把这个节点添加到 的右边![数学{3}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c634b2b9b88bcd5abe9d7d39b4081440_l3.svg)：

![下一个元素插入](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/next_element_insert.drawio.png)

让我们用 key 插入下一个节点 ![textbf{5}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-04eb56348aa6f6a5930e5a7e37191070_l3.svg)。我们首先检查根，当![数学{5}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1aa18814edfae60c751c8687b458ffc7_l3.svg)大于![数学{2}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-276c2cef464470f3f58000ea412deecd_l3.svg)时，我们转到右子树。在右子树中，我们看到![数学{5}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1aa18814edfae60c751c8687b458ffc7_l3.svg)大于![数学{3}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c634b2b9b88bcd5abe9d7d39b4081440_l3.svg)和![数学{4}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-37d1fe1e61fadb833cc17cf696d76e41_l3.svg)。因此，我们![数学{5}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1aa18814edfae60c751c8687b458ffc7_l3.svg)在 的右侧插入![数学{4}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-37d1fe1e61fadb833cc17cf696d76e41_l3.svg)。但是又没有足够的空间。![mathsf{3, 4, 5}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f096113a74958b91e6cf18594937cec6_l3.svg)因此，我们拆分它并将 的中位数移动![数学{4}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-37d1fe1e61fadb833cc17cf696d76e41_l3.svg)到上层节点：

![插入 5 个元素](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/inserting_5_element.drawio-2048x416-1-1024x208.png)

同样，我们使用 key 插入下一个节点 ![textbf{6}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-37ce5fb007bfcaa1b0033af34f5f1358_l3.svg)：

![插入 6 个元素](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/inserting_6_element.drawio.png)

最后，让我们按照B树的属性，将剩余的所有节点一一相加：

![最后的b树](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/final_b_tree.drawio-1024x391.png)

B树插入过程的时间复杂度为 ![textbf{mathcal{O}(logn)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c66df23d269e4f61f5656558cb546af4_l3.svg)。

### 4.3. 删除

删除是我们从 B 树中删除键的过程。在这个过程中，我们需要维护B树的属性。删除任何键后，树必须重新排列以遵循 B 树属性。此外，我们需要在删除之前在 B 树中搜索该键。

从 B 树中删除键时可能有两种情况。首先，我们要删除的键在叶节点中。其次，我们要删除的键在内部节点中。

让我们举一个从 B 树中删除的例子：在这里，我们采用的是 order 的 B 树![数学{5}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1aa18814edfae60c751c8687b458ffc7_l3.svg)。一个节点可以包含的最大子节点数是![数学{5}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1aa18814edfae60c751c8687b458ffc7_l3.svg). 此外，在这种情况下，节点的最大和最小键数为![数学{4}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-37d1fe1e61fadb833cc17cf696d76e41_l3.svg)和![数学{2}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-276c2cef464470f3f58000ea412deecd_l3.svg)：

![删除树示例](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/deletion_tree-example.drawio-2048x839-1-1024x420.png)

如果目标键的叶节点包含的键多于所需的最少键，则我们不必担心任何事情。我们需要删除目标键。删除后，它仍然是一个完整的B树。

如果目标键的叶节点只包含最少数量的键，我们不能简单地删除键。因为如果我们这样做，就会违反 B 树的条件。在这种情况下，如果该节点的密钥多于所需的最小密钥，则从紧邻的左节点借用密钥。

我们将把左节点的最大值传递给它的父节点。之后，价值较大的密钥将被转移到借款人节点。此外，我们可以从节点中删除目标键。假设我们要删除一个带有 key 的节点![数学{23}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b16fb622beeafd82be27c79d5648d138_l3.svg)：

![删除 23](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/deletion_of_23.drawio-1004x1024.png)

另一种可能的方法是，如果该节点的密钥多于所需的最小密钥，则从紧邻的右节点借用密钥。我们将把右边节点的最小值传给它的父节点。较小的键值将被转移到借款人节点。之后，我们可以从节点中删除目标键。假设我们要删除一个带有 key 的节点![数学{72}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b7cbf8c4c66b42f88e18b0f36a7a709e_l3.svg)：

![删除 72](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/Deletion_of_72.drawio-1005x1024.png)

现在让我们讨论当目标键节点的左兄弟节点和右兄弟节点都没有超过所需的最小键的情况。在这种情况下，我们需要合并两个节点。在两个节点中，一个节点应该包含我们的目标节点的密钥。在合并时，我们还需要考虑父节点。

假设我们要删除一个键为 的节点 ![textbf{65}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a9972b581634481f21c8bc3729961ef2_l3.svg)。它的兄弟节点包含的键数不超过最小数目。这里的第一步是合并节点。在这个例子中，我们合并了目标键节点的左节点。合并节点后，我们删除目标节点：

![删除 65](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/Deletion_of_65.drawio-809x1024.png)

现在让我们讨论目标键位于内部节点的情况：

![删除 70](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/deletion_of_70.drawio-2048x1261-1-1024x631.png)

在这种情况下，第一个可能的选择是用它的前身替换目标键。这里我们获取目标键的左节点，选择最高值键，并将其替换为目标键。在这里我们要删除节点![数学{70}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d010b53c2cf9ca16b205349d90fe8b79_l3.svg)：

![删除 70 个前身后](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/after_deletion_of_70_predecessor.drawio-2048x1041-1-1024x521.png)

如果左边没有超过最少要求的键，我们用它的顺序后继替换目标键。在这里，我们将获取目标键的右节点，选择最低值键并将其替换为目标键：

![删除 95 个后继者后](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/after_deletion_of_95_successor.drawio-1024x679.png)

如果目标键的中序后继节点和中序前驱节点没有超过所需键的最小数量，我们需要合并两个相邻节点。例如，删除![数学{77}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1af2c270d2b1310c778dc01fd6d7c339_l3.svg)：

![删除 77 个非](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/deletion_of_77_non.drawio-2048x962-1-1024x481.png)

删除77后：

![删除 77 个非](https://www.baeldung.com/wp-content/uploads/sites/4/2021/12/after_deletion_of_77_non.drawio-2048x805-1-1024x403.png)

B树删除过程的时间复杂度为 ![textbf{mathcal{O}(logn)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c66df23d269e4f61f5656558cb546af4_l3.svg)。

## 六，总结

B-Tree 是一种用于存储大量数据的最常用的数据结构。在本教程中，我们详细讨论了 B 树。我们通过示例介绍了属性和操作。