## 1. 概述

在本教程中，我们将了解一种称为 B 树的数据结构及其变体——B+树。我们将了解它们的特性、它们是如何创建的，以及如何在数据库管理系统中有效地使用它们来创建和管理索引。

最后，我们将使用我们获得的知识来比较 B 树和 B+ 树。

## 2.什么是B树？

B 树是一种自平衡树结构，旨在存储大量数据以进行快速查询和检索。它们经常与它们的密切关系—— [二叉搜索树](https://www.baeldung.com/java-binary-tree)混淆。尽管它们都是一种[m路](https://webdocs.cs.ualberta.ca/~holte/T26/m-way-trees.html)搜索树，但二叉搜索树被认为是一种特殊类型的 B 树。

B 树在一个节点中可以有多个键/值对，按键顺序升序排序。我们称这个键/值对为有效载荷。有时我们可能会听到有效载荷的值部分被称为“卫星数据”或简称为“数据”。

在数据库的上下文中，键可以是行的主键或索引列，值可以是实际的行记录本身或对它的引用。

除了负载之外，节点还保留对其子节点的引用。由于每个节点通常占用一个磁盘页面，因此这些子引用通常指的是子节点在二级存储中所在的页面 ID。与任何搜索树一样，B 树的组织方式使得任何子树的键必须大于其左侧子树的键。

### 2.1. B 树的属性

要将一棵树归类为 B 树，它必须满足以下条件：

-   顺序 B 树中的节点![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)最多可以有![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)子节点
-   每个内部节点(非叶节点和非根节点)至少可以有 ( ![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)/2) 个子节点(四舍五入)
-   根应该至少有两个孩子——除非它是叶子
-   有子节点的非叶节点![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)应该有![k-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7dfca2445cd362ac42fb9032c9cf2367_l3.svg)键
-   所有叶子必须出现在同一水平面上

下图向我们展示了 B 树的示例。这棵树的最大孩子数是3，所以我们可以断定这是一棵3阶树。所有叶子都在同一层，根节点和内部节点至少有两个孩子满足所有条件B树：

![树满 2](https://www.baeldung.com/wp-content/uploads/sites/4/2020/05/btreefull-2.png)

### 2.2. 构建 B 树

现在，让我们从头开始构建我们的树。为此，我们将学习如何在保持树平衡的同时将项目插入树中。

因为我们从一棵空树开始，所以我们插入的第一个项目将成为我们树的根节点。此时，根节点具有键/值对。键为 1，但值被描绘为星号以使其更易于表示，并表明它是对记录的引用。

根节点也有指向其左右子节点的指针，显示为键左右两侧的小矩形。由于该节点没有子节点，因此这些指针暂时为空：

![btree1-1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/05/btree1-1.png)

我们知道这棵树的阶数是 3，所以它最多只能有 2 个键。所以我们可以将key为2的payload按升序添加到根节点：

![btree2-1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/05/btree2-1.png)

接下来，如果我们想插入 3，为了保持树平衡并满足 B 树的条件，我们需要执行所谓的拆分操作。

我们可以通过选择中间键来确定如何拆分节点。当我们选择中间键时，我们需要考虑节点中已经存在的键以及要插入的键。在这种情况下，2 是用于拆分节点的中间密钥。这意味着 2 左边的值将进入左子树，2 右边的值将进入右子树，而 2 本身将被提升为新根节点的一部分：

![btree3-1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/05/btree3-1.png)

通过在每次我们即将超过节点中的最大键数时执行此拆分操作，我们可以保持树的自平衡。

现在，让我们插入 4。要确定需要将其放置在何处，我们必须记住 B 树的组织方式使得右侧的子树比左侧的子树具有更多的键。因此，密钥 4 属于右子树。由于右子树仍然有容量，我们可以简单地将 4 与 3 按升序相加：

![btree4-1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/05/btree4-1-768x300-1.png)

我们的右子树现在已满负荷，因此要插入 5，我们需要使用与上面解释的相同的拆分逻辑。我们将节点一分为二，键 3 进入左子树，键 5 进入右子树，留下 4 与 2 一起提升为根节点。

![btree5-1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/05/btree5-1-768x288-1.png)

这种重新平衡为我们在最右边的子树中提供了空间来插入 6：

![btree6-2](https://www.baeldung.com/wp-content/uploads/sites/4/2020/05/btree6-2.png)

接下来，我们尝试插入 7。但是，由于最右边的树现在已满负荷，我们知道我们需要进行另一次拆分操作并提升其中一个键。

可是等等！根节点也满了，这意味着它也需要分裂。

所以，我们最终分两步完成。首先，我们需要拆分右边的节点5和6，使得7在右边，5在左边，6会被提升。

然后，为了提升 6，我们需要拆分根节点，使 4 成为新根的一部分，而 6 和 2 成为左右子树的父节点：

![btree7-1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/05/btree7-1-1024x471-1.png)

以这种方式继续，我们通过添加键 8、9 和 10 来填充树，直到我们得到最终的树：

![btreefull-3](https://www.baeldung.com/wp-content/uploads/sites/4/2020/05/btreefull-3.png)

### 2.3. 搜索 B 树

B 树被认为非常有利，因为它们![O(日志(n))](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b15503718d0ccc0cf4cf9137e087efc0_l3.svg)在插入、删除和搜索操作时提供对数时间复杂度。

让我们看看如何使用下面的伪代码对 B 树执行搜索操作：

 ![[begin{minipage}{300} begin{algorithm}[H] SetAlgoLined SetKwProg{FUNC}{Function}{:}{end} FUNC{Search($node, searchKey$)} { i = 0 ;  While{$i < node.Payloads.Count$ and $searchKey > node.Payloads[i].Key$}{ i++;  } If{$searchKey = node.Payloads[i].Key$}{ Return $node.Payloads[i].Value$;  } If{$node.IsLeaf = true$}{ Return null;  } Return Search($node.Children[i]$, $searchKey$);  } caption{B-树搜索} end{算法} end{minipage} ]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-418467149bbbc56e31c2086bdb0d758d_l3.svg)

我们的伪代码假设如下：

-   一个节点有一个键/值对数组node.Payloads和一个对其子节点node.Children的引用数组
-   节点对象有一个node.IsLeaf属性，它确定它是否是叶节点
-   node.Payloads.Count是指一个节点中的payload数量

如果我们假设树的键引用表的 ID，并且我们想要搜索 ID 为 6 的记录，让我们看看如何应用此伪代码。这意味着我们将调用定义的Search方法在带有参数node = B 树的根节点和searchKey = 6的伪代码中。

使用 while 循环，我们遍历根节点键，增加计数器值i，直到找到大于或等于 6 的键，或者直到我们完成该节点的键。循环以i的值为 1 结束。

现在使用i = 1作为索引，我们选择node.Children[1](索引为 1 的子节点)。遍历它的键，我们找到node.Payload[0]。Key与我们的searchKey完全匹配。我们通过返回node.Payload[0] .Value 来结束搜索，这是与键 6 关联的卫星数据。

下图以灰色阴影显示了我们的搜索路径：

![BtreeSearch-2](https://www.baeldung.com/wp-content/uploads/sites/4/2020/05/BtreeSearch-2.png)

## 3. B+树

B树最著名的版本是B+树。B+树与B树的区别主要有两个方面：

-   所有叶节点都在一个双向链表中链接在一起
-   卫星数据仅存储在叶节点上。内部节点仅保存密钥并充当正确叶节点的路由器

让我们看看如果我们将其内容表示为 B+ 树，我们的 B 树会是什么样子：

![bplustreefull-2](https://www.baeldung.com/wp-content/uploads/sites/4/2020/05/bplustreefull-2.png)

请注意，有些键似乎是重复的，如 2、4 和 5。这是因为，与叶节点不同，B+ 树的内部节点不能保存卫星数据，因此我们必须以某种方式确保叶节点包括所有键/值对。

让我们从头开始构建我们的树，看看这是如何发生的。

### 3.1. 构建B+树

首先，我们将按升序将键 1 和键 2 插入根节点：

![bplustree1-1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/05/bplustree1-1.png)

当我们来插入Key 3时，我们发现这样做会超出根节点的容量。

与普通的 B 树类似，这意味着我们需要执行拆分操作。然而，与 B 树不同的是，我们必须新的最右边叶节点中的第一个键。如前所述，这样我们就可以确保叶节点中的键 2 有一个键/值对：

![bplustree3-2](https://www.baeldung.com/wp-content/uploads/sites/4/2020/05/bplustree3-2.png)

接下来，我们将 Key 4 添加到最右边的叶节点。由于已经满了，我们需要再进行一次拆分操作，将Key 3到根节点：

![bplustree4-1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/05/bplustree4-1.png)

现在，让我们将 5 添加到最右边的叶节点。再次保持顺序，我们将拆分叶节点并向上 4。由于这会溢出根节点，我们必须执行另一次拆分操作，将根节点拆分为两个节点并将 3 提升为一个新节点根节点：

![bplustree5-1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/05/bplustree5-1.png)

请注意拆分叶节点和拆分内部节点之间的区别。当我们在第二次拆分操作中拆分内部节点时，我们没有密钥 3。

以同样的方式，我们不断添加从 6 到 10 的键，每次都在必要时进行拆分和，直到我们到达最终的树：

![bplustreefull-3](https://www.baeldung.com/wp-content/uploads/sites/4/2020/05/bplustreefull-3.png)

### 3.2. 搜索 B+树

在 B+ 树中搜索特定键与在普通 B 树中搜索键非常相似。让我们看看在 B+ 树上再次搜索 Key 6 会是什么样子：

![bplustreeSearch-1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/05/bplustreeSearch-1.png)

阴影节点向我们展示了我们为找到匹配项而采取的路径。 推导告诉我们，在一棵B+树中查找，意味着我们必须一直向下到一个叶子节点才能得到卫星数据。与我们可以在任何级别找到数据的 B 树相反。

除了精确键匹配查询外，B+树还支持范围查询。这是因为 B+ 树叶节点都链接在一起。要执行范围查询，我们需要做的是：

-   找到一个完全匹配搜索最低键
-   从那里开始，按照链表直到我们到达具有最大键的叶节点

## 4. 数据库环境中的 B 树

存储和管理数据是计算的基础部分。主内存被认为是主要的数据存储，但我们不能在其中存储所有内容，因为它易失且价格昂贵。这就是我们拥有更便宜且不易失的二级存储的原因。辅助存储通常以称为页面的单元存储在磁盘上。

将元素从磁盘传输到内存需要从磁盘读取。单个磁盘读取执行整页访问，即使我们只是试图从该页读取一个元素。磁盘读取不如主内存读取快，因为每次我们在那里进行读取时都会有一次寻道和旋转延迟。我们需要的磁盘访问越多，搜索操作花费的时间就越长。

DBMS 利用 B 树索引的对数效率来减少查找特定记录所需的读取次数。B 树通常被构建为每个节点在内存中占用一个页面，并且它们被设计为通过要求每个节点至少半满来减少访问次数。

## 5. B树和B+树的比较

让我们介绍一下 B 树和 B+ 树之间最明显的比较点：

-   在 B+树中，搜索关键字可以重复，但 B 树不是这样
-   B+树只允许卫星数据存储在叶节点中，而B-树则同时在叶节点和内部节点中存储数据
-   在 B+ 树中，存储在叶节点上的数据使搜索更有效率，因为我们可以在内部节点中存储更多键——这意味着我们需要访问更少的节点
-   从B+树中删除数据更容易，也更省时，因为我们只需要从叶子节点中删除数据
-   B+树中的叶节点链接在一起，使范围搜索操作高效快捷

最后，虽然 B 树很有用，但 B+ 树更受欢迎。事实上，99%的数据库管理系统都使用B+树进行索引。

这是因为 B+树在内部节点中不保存任何数据。这最大化了节点中存储的键的数量，从而最小化了树中所需的级别数。较小的树深度总是意味着更快的搜索。

## 六，总结

在本文中，我们仔细研究了 B 树和 B+ 树。我们了解了它们是如何构建的以及如何搜索某个键。

最后，根据我们了解到的信息，我们能够在结构、用法和受欢迎程度方面对两者进行比较。