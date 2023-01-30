## 1. 概述

在本教程中，我们将讨论计算全叉树中节点总数的问题![textbf{k}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-82287109a827c80fe0a7ccb4a80c6ea9_l3.svg)。为此，我们将首先探索迭代和递归方法，然后是纯数学方法。

## 2. 满![巨大的textbf{k}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52cc7ad61605ce0e918f51b3a75c6e82_l3.svg)叉树

让我们从熟悉一棵完整的![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)二元树开始：

![卡里树 1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/09/kary-tree-1.svg)

我们可以在这里看到除叶节点之外的所有节点都有![textbf{k}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-82287109a827c80fe0a7ccb4a80c6ea9_l3.svg)子节点(在本例中为![k=3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-94884c30f0e7b889ad62b8fb90ef8151_l3.svg))。

此外，问题要求我们在已知叶子总数 ( ![大号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-48d71fca322532f0abc2c4ad2cf98154_l3.svg)) 的情况下计算树中节点的总数。对于这种情况，我们可以计数并知道叶子总数为![27](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1ce778bce9d870f2d64539546e694405_l3.svg)，而节点总数为![40](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5990a3a2dcdb71c44462feb0ac94262a_l3.svg)。

对于所有正整数 ，![textbf{k}bmgeqtextbf{2}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e2f69d29ca8acc72288249a227f71245_l3.svg)我们可以通过知道叶子总数 来找到节点总数。

## 3.迭代求解

节点总数实质上是树的每一层节点数的总和。让我们看看如何迭代地做到这一点。

### 3.1. 算法流程

在迭代方法中，我们从叶节点开始，向上走到树的根部。由于满![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)叉树中的每个节点都有![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)子节点，所以一层的节点总数是其上一层节点总数的![textbf{k}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-82287109a827c80fe0a7ccb4a80c6ea9_l3.svg)倍数。

因此，在每次迭代中，我们向上移动一个级别并将到目前为止的节点总数增加当前级别中存在的节点数量：

![卡里树 2](https://www.baeldung.com/wp-content/uploads/sites/4/2020/09/kary-tree-2.svg)

为了防止错误的输入值，我们可以包括验证检查并在输入无效的情况下错误退出。

### 3.2. 伪代码

让我们看一下迭代方法的伪代码实现：

```

```

我们首先将节点总数 ( ![全部的](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-01443a337776c1a2e3cf07004adf69ee_l3.svg)) 和当前级别的节点数 ( ) 初始化![数数](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3e39360ad87beda80fc3e45ff5fc5851_l3.svg)为叶子数。在每次迭代中，![数数](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3e39360ad87beda80fc3e45ff5fc5851_l3.svg)减少了一个因子![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)，而![全部的](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-01443a337776c1a2e3cf07004adf69ee_l3.svg)增加了当前级别的节点数。

最后，当当前级别的节点数达到 的值时，我们停止![1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-69a7c7fb1023d315f416440bca10d849_l3.svg)，这意味着我们已经到达树的根。

## 4.递归求解

可以使用递归方法实现相同的算法思想。让我们看看如何用递归替换循环。

### 4.1. 伪代码

计算达到某一级别的节点总数的过程与将该级别的节点添加到其上一级的节点总数基本相同：

```

```

因此，记住这个递归公式，让我们看一下递归式实现的伪代码：

```

```

我们可以看到函数![ComputeTotalNodes递归](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-76513b42aee2194ee29bdb71a860b2be_l3.svg)是被![计算总节点数](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-35ebd94e2cb795bc78d8aa5f83733b58_l3.svg)函数包裹起来的核心递归部分。这种抽象有两个好处：

-   任何预验证都可以在包装函数内执行一次
-   客户端获得相同的接口，不会泄露任何内部实现细节

## 5.直接公式法

现在我们了解了迭代和递归方法，让我们学习另一种方法，我们将核心功能委托给现成的数学函数。

### 5.1. 高度预计算

从树的顶部开始，如果我们记下每一层的节点总数，那么我们将得到几何级数：

![1, k, k^2, k^3, ..., 叶子](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e36c33dc1278c2255c8d64566a0b9db8_l3.svg)

解决方案归结[为几何级数的总和](https://en.wikipedia.org/wiki/Geometric_series#Formula)。因此，我们可以使用对数函数和指数函数作为辅助函数：

```

```

通过将核心功能委托给数学函数，这种方法看起来更具可读性和简单性。

### 5.2. 直接计算

我们可以进一步简化计算节点总数的公式，去掉计算高度的中间步骤。让我们从数学上看：

```

```

最后，让我们得出一个使用此公式的算法的简化版本：

```

```

## 6. 复杂性分析

所有直接或间接使用树的高度的方法的时间复杂度均为 ![mathbf{O(log{}叶子)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a4a37b6c5ae663f7e58b29373a04db79_l3.svg). 这是因为全元树的高度与![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)树的阶数成对数关系。

然而，我们最后的方法依赖不需要我们做任何中间计算。如果我们假设[算术逻辑单元](https://en.wikipedia.org/wiki/Arithmetic_logic_unit)(ALU) 支持硬件级别的除法，那么我们算法的时间复杂度将为![mathbf{O(1)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6e2d1069e3ac2e285942fcabad45fe10_l3.svg). 否则，即使是最后一个算法的时间复杂度也是![mathbf{O(log{}叶子)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a4a37b6c5ae663f7e58b29373a04db79_l3.svg).

## 七、总结

![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)在本教程中，我们介绍了计算全叉树节点总数的不同方法。我们从头开始使用迭代和递归方法，并以基于直接数学公式的方法结束。