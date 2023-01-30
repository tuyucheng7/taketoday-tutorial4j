## 1. 概述

在本教程中，我们将讨论两种自平衡二进制数据结构：AVL 和红黑树。我们将通过示例介绍属性和操作。

最后，我们将探讨它们之间的一些核心差异。

## 2. AVL树简介

要理解AVL树，我们先来讨论一下[二叉树数据结构](https://www.baeldung.com/cs/binary-tree-intro)。它将帮助我们理解为什么我们需要 AVL 树数据结构。

在二叉树数据结构中，最多允许有两个子节点。使用二叉树，我们可以组织数据。但是，数据并未针对搜索或遍历操作进行优化。

因此，引入[二叉搜索树(BST)](https://www.baeldung.com/cs/binary-search-trees)数据结构来克服这个问题。它是二叉树的更新版本。额外添加的属性是小于父节点的数据将被添加到左子树。同样，如果信息大于父节点，我们将它们添加到右子树中。

所有的树操作都在二叉搜索树中进行了优化，执行时间比二叉树快。但是，它仍然存在一些问题。二叉搜索树可以变成[左偏树或右偏树](https://www.baeldung.com/cs/binary-tree-intro)。让我们看一下左偏和右偏的树：

![1 左偏树](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/1_left_skewed_tree.drawio.png)

BST 数据结构的最大好处是所有树操作的时间复杂度为![mathcal{O}(log n)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0ffb2daeb5f5cfcabbbb862e8a886b89_l3.svg)，其中![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)总节点数为 。尽管当 BST 是左偏树或右偏树时，时间复杂度变为![数学{O}(n)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f92d05bae8eccfb970efb4c3ecfa1ee8_l3.svg). 这个问题的解决方案是 AVL 树数据结构。

它是一种[自平衡二叉搜索树](https://en.wikipedia.org/wiki/Self-balancing_binary_search_tree)。每个节点都有一个平衡因子，它必须是 ![mathbf{-1, 0}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b0f027f53f05c8618097f6ad72de2d06_l3.svg), 或![数学{1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-135bdbc0e735b08da000196ae4cbeda4_l3.svg)。平衡因子的计算方法是用左子树的高度减去右子树的高度。如果平衡因子大于![-1, 0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1899a989cb4c414e0881ccdc49117d85_l3.svg)或![1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-69a7c7fb1023d315f416440bca10d849_l3.svg)，我们需要应用旋转来重新平衡树。让我们看一下 AVL 树：

![1 删除avl树](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/1_Deletion_Avl_tree_.drawio-2.png)

AVL 树有一个平衡因子，它确保树操作的时间复杂度应该![mathbf{mathcal{O}(log n)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4bb4f83fef8605f05416fde495e89ed9_l3.svg)处于平均和最坏情况下。

## 3. AVL树的性质

下面说说AVL树数据结构的特性。

AVL 树也称为高度平衡树。[一棵高度](https://www.baeldung.com/cs/binary-tree-height)为 AVL 树的最大节点数 ![mathbf{H}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3e2a123af4114a601ef77eb46f1e5b72_l3.svg) 可以是 ![mathbf{(2^{H+1} - 1)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-12cab5054b114652c6c9df845cc33662_l3.svg). 此外，高度为 AVL 树中的最小节点数![H](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a7cedbc00aa5531f310166df85e3a9bb_l3.svg)可以使用递推关系计算：![N(H) = N(H - 1) + N(H -2) + 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7866fbaff2e276aeeb1da273fe54b901_l3.svg)，其中![H geq 2, N(0) = 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-814845a302fbe958127e149f2498a923_l3.svg)，和![N(1)= 2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-14721b0214330b0a4b8e842650d733c5_l3.svg)。

我们可以通过应用左旋转或右旋转来平衡 AVL 树。AVL 树上的树操作与 BST 相同。然而，在执行任何树操作之后，我们需要检查每个节点的平衡因子。因此，如果树是不平衡的，我们执行旋转以使其平衡。

右子树中的元素可能会增加，AVL 树在应用一些树操作后可能会变得不平衡。我们将不平衡的节点旋转到左侧来克服这个问题，使 AVL 树再次平衡。

左子树中的元素可能会增加，AVL 树在应用一些树操作后可能会变得不平衡。为了克服这个问题，我们将不平衡的节点旋转到右侧以使 AVL 树再次平衡。

另一种可能是当我们对AVL树进行一些树操作时，它变得不平衡，没有右子树，但左子树包含所有值大于其父节点的节点。为了克服这个问题，我们必须执行两次旋转，一次向右旋转，一次向左旋转。

类似地，如果没有左子树，AVL 树可能会变得不平衡，但右子树仅包含值小于其父节点的节点。我们必须执行两次旋转来平衡 AVL 树：一次向左旋转和一次向右旋转。

## 4. AVL 树的操作

### 4.1. 搜索中

因为每棵 AVL 树都是一个 BST，所以 AVL 树中的搜索操作类似于 BST。我们通过将要查找的元素与根节点进行比较来开始搜索过程。此外，根据所需元素的键值，我们从根节点转到左子树或右子树并重复该过程。当找到所需元素或完全探索树时，我们终止搜索过程。

在 AVL 中搜索元素的时间复杂度为 ![mathbf{mathcal{O}(log n)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4bb4f83fef8605f05416fde495e89ed9_l3.svg)，其中 AVL 树中的节点总数为![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)。

### 4.2. 插入

AVL 树中的插入操作类似于 BST 插入。虽然，在 AVL 树中，还有一个额外的步骤。我们需要在插入操作后计算每个节点的平衡因子，以确保树始终是平衡的。我们来讨论一下插入操作的步骤。

如果树为空，则在根部插入元素。如果树不为空，那么我们首先将要插入的节点的值与父节点或根节点进行比较。基于比较，我们要么转到右子树，要么转到左子树，并获得插入新节点的适当位置。

在插入过程之后，我们计算每个节点的平衡因子。如果任何节点不平衡，我们根据要求执行一些旋转，直到树再次变得平衡。AVL 树中插入过程的时间复杂度为 ![mathbf{mathcal{O}(log n)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4bb4f83fef8605f05416fde495e89ed9_l3.svg)。

让我们看一个例子。我们想在 AVL 树中插入一组具有给定键值的节点：![14, 17, 11, 78, 53, 4, 13, 12](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a97eeae00c01090e06af696a4520978e_l3.svg)。最初，树是空的。我们将一个一个地插入节点。

首先，我们插入一个键为 的节点![14](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6ebb06c89a650afa3e44d6610e6f94e8_l3.svg)。它将是 AVL 树的根节点。现在，让我们插入带有 key 的节点，![11](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ef822489b9748c10966e5e94b8463f3a_l3.svg)然后插入带有 key 的节点![17](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-99fb9008ca63ec321863e498803ce34e_l3.svg)。插入节点后，我们还计算每个节点的平衡因子：

![1 插入 AVL](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/1_Insertion_in_AVL_1.drawio-1.png)

现在让我们插入具有键值![7](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9d8e16e2c1790d6af563225a9318d119_l3.svg)和的节点![53](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dba7c058478deecd2daf03adc21aa91d_l3.svg)：

![1 插入 AVL 2](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/1_insertion_in_AVL_2.drawio.png)

到目前为止，我们有一个平衡的 AVL 树。因此，我们不需要执行任何旋转。让我们插入下一个带有键值的节点![4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d4d95642629f734574671d47307d46c3_l3.svg)：

![1 插入 AVL 3](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/1_Insertion_in_AVL_3.drawio.png)

正如我们所看到的，在插入具有 key-value 的节点后![4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d4d95642629f734574671d47307d46c3_l3.svg)，树变得不平衡。因此，我们执行了左旋转以重新平衡树。让我们插入下一个带有键值的节点![13](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-987e1ecd9a092755a62e57168a2dc001_l3.svg)：

![1 插入 AVL 4](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/1_Insertion_in_AVL_4.drawio-1.png)

AVL树是平衡的。最后，让我们插入带有 key-value 的最后一个节点![12](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e4e03262026852c1e06862a479503add_l3.svg)：

![1 插入 AVL 5](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/1_Insertion_in_AVL_5.drawio.png)

插入值为 的节点后![12](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e4e03262026852c1e06862a479503add_l3.svg)，AVL 树变得不平衡。因此，我们首先执行左旋转，然后执行右旋转以平衡 AVL 树。

### 4.3. 删除

AVL 树中的删除操作类似于 BST 删除。但是同样，我们必须在执行删除操作后计算每个节点的平衡因子。当我们要删除一个节点时，我们首先遍历树找到该节点的位置。此过程与在 AVL 树或 BST 中搜索节点或元素相同。

当我们在给定的树中找到所需的节点时，我们只需删除该节点。删除节点后，我们计算每个节点的平衡因子。如果 AVL 树不平衡，我们根据要求执行旋转。从 AVL 中删除任何元素的时间复杂度为 ![mathbf{mathcal{O}(log n)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4bb4f83fef8605f05416fde495e89ed9_l3.svg).

我们正在使用 AVL 树来显示删除示例：

![1 删除avl树](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/1_Deletion_Avl_tree_.drawio-1-1.png)

现在，例如，我们要从 AVL 树中删除以下节点：![8,12,13](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-24be86c9a5dee5376a0a3b3774e996f4_l3.svg)。让我们先删除节点![8](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e4888e98f77eb93ff65bfecac28d3c5e_l3.svg)，然后删除节点![13](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-987e1ecd9a092755a62e57168a2dc001_l3.svg)：

![1 个删除 Avl 树的副本](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/1_Copy-of-Deletion_Avl_tree_.drawio.png)

删除节点![8](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e4888e98f77eb93ff65bfecac28d3c5e_l3.svg)和![13](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-987e1ecd9a092755a62e57168a2dc001_l3.svg)后，我们计算了每个节点的平衡因子。此外，我们可以看到所有节点都是平衡的。因此，我们不需要在这里执行任何旋转。让我们删除下一个节点![12](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e4e03262026852c1e06862a479503add_l3.svg)：

![1份删除Avl树的副本](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/1_Copy-of-Copy-of-Deletion_Avl_tree_.drawio-2.png)

删除值为 的节点后![12](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e4e03262026852c1e06862a479503add_l3.svg)，我们可以看到 AVL 不再平衡。因此，为了平衡AVL树，我们需要在这里进行一次右旋：

![1 Copy of Copy of 删除 Avl 树的副本](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/1_Copy-of-Copy-of-Copy-of-Deletion_Avl_tree_.drawio.png)

## 5. 红黑树(RBT)简介

它也是一个自平衡的二叉搜索树。因此，它遵循二叉搜索树的所有先决条件。[红黑树](https://www.baeldung.com/cs/red-black-trees)也称为大致高度平衡的树。

红黑树数据结构中有两种类型的节点：红色和黑色。此外，在执行任何树操作之后，我们可能需要应用一些旋转并对节点重新着色以平衡红黑树。红黑树数据结构中树运算的复杂度与AVL树是一样的。

红黑树是一种自平衡二叉查找树，其复杂度与AVL树相同。因此，为什么我们需要额外的树数据结构呢？让我们讨论。

正如我们之前讨论的，我们需要应用旋转来平衡 AVL 树中的树。当我们必须执行多次轮换时，我们经常会遇到这种情况。旋转越多，处理越多。因此，处理将根据所需的旋转次数而变化。虽然，在红黑树中，平衡一棵树最多需要两次旋转。因此，为了便于实现和执行，引入了红黑树。

## 6. 红黑树的性质

在红黑树中，每个节点都用黑色或红色进行颜色编码。根节点和叶节点通常是黑色的。此外，如果一个节点是红色的，它的子节点必须是黑色的。让我们看一下 RBT 树：

![1 插入 RBT 3](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/1_insertion_in_RBT_3.drawio.png)

如果一个彩铃有![mathbf{n}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-262f2b064422e0639fd9e7d5e7cf039f_l3.svg)节点，彩铃的高度最多为![mathbf{2log(n+1)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f5702563c1191f3314f1683db6cb45ff_l3.svg). RBT 的高度是一个重要的属性，因为它使此树数据结构不同于 AVL 树。每次操作后，我们需要进行一些旋转并根据当前树设置节点的颜色。RBT 具有与 BST 相同的结构，但它使用额外的位来存储节点的颜色代码。

对于 RBT 中的每个节点，有四个数据：节点的值、左子树指针、右子树指针和一个用于存储节点颜色代码的变量。

## 7. 红黑树操作

在 RBT 中搜索与在 BST 中搜索任何元素相同。要搜索任何元素，我们从根节点开始，通过比较给定元素的键值向下到树。搜索任意元素的时间复杂度为 ![mathbf{mathcal{O}(log n)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4bb4f83fef8605f05416fde495e89ed9_l3.svg)。

RBT 中的插入操作遵循 BST 中的一些插入规则。如果树是空的，我们需要插入元素并将其设为黑色，因为它是根节点。当树不为空时，我们创建一个新节点并将其着色为红色。

此外，每当我们想在树中插入任何元素时，新节点的默认颜色始终为红色。这意味着子节点和父节点的颜色不应该是红色的。我们只需要确保没有相邻的红色节点。

插入一个元素后，如果插入元素的父节点为黑色，我们就不用做任何额外的处理。这将是一个平衡的 RBT。但是如果插入元素的父节点是红色的，我们需要检查父节点的兄弟节点的颜色。因此，我们需要检查与父节点处于同一级别的节点的颜色。

RBT 中的删除操作与 BST 中的删除操作相同。首先，我们必须遍历树，直到找到所需的节点。一旦找到该节点，我们就会将其从 RBT 中删除。

如果我们删除任何红色节点，它不会违反 RBT 的任何条件。因此，我们只需要像 BST 一样删除节点。但是在删除黑节点的情况下，可能会违反RBT的条件。因此，在我们删除任何黑色节点后，我们必须执行一些操作，如旋转和重新着色，以重新平衡 RBT。

[我们可以在红黑树简介中](https://www.baeldung.com/cs/red-black-trees)通过示例找到更多关于红黑树及其操作的信息。

## 8. 差异

现在让我们看看AVL和红黑树数据结构的核心区别：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6c6a20c2d0278673b91be802c7cf89ff_l3.svg)

## 9.总结

在本教程中，我们讨论了 AVL 和红黑树数据结构。我们通过示例介绍了属性和操作。

最后，我们探讨了它们之间的核心差异。