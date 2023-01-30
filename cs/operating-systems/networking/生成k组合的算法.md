## 1. 概述

在本教程中，我们将学习不同的算法来生成包含![{k}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4a7414f95220ee9a6343fddcc5416427_l3.svg)元素的集合的所有元素子集![{n}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0ebdc09385947e8044a554a851d9debb_l3.svg)。这个问题被称为[![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)-combinations](https://www.baeldung.com/java-combinations-algorithm#overview)。

找到 - 组合数量的数学解决方案![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)很简单。它等于[二项式系数](https://www.baeldung.com/java-combinatorial-algorithms#generating_combinations)：

 ![[binom{n}{k} = frac{n!}{k!  (nk)!}= frac{n times (n-1) times ... times (n - k + 1)}{k times (k-1) times ... times 1} ]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cf05bc1499d5656797b68811f4f32c82_l3.svg)

例如，假设我们有一个包含 6 个元素的集合，我们想要生成 3 个元素的子集。在这种情况下，我们可以通过 6 种方式选择第一个元素。然后，我们从剩余的 5 个中选择第二个元素。最后，我们从剩余的 4 个中选择子集的第三个也是最后一个元素。到目前为止，我们的选择数正好是![frac{n!}{(nk)!}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9d821143444a3452ea54b6da95fc8a30_l3.svg)。

由于我们选择的顺序并不重要，因为我们正在构建一个子集，所以我们需要除以![克！](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3014ed0c27cd32cbf83c2349ef57cebc_l3.svg)。因此我们得出相同的公式：![frac{n!}{k!  (nk)！}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e8921cfe353d1b36cf06f0fede41ab06_l3.svg)

然而，生成所有这些子集并枚举它们比仅仅计算它们要复杂得多。让我们看看在以下部分中以不同方式创建子集的不同算法。

我们将在以下部分中回顾的算法最初由计算机科学家 Donald Knuth在[“计算机编程的艺术”中编译。](https://en.wikipedia.org/wiki/The_Art_of_Computer_Programming)

## 2. 字典生成 

字典顺序是生成组合的一种直观且方便的方式![textbf{k}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-82287109a827c80fe0a7ccb4a80c6ea9_l3.svg)。

该算法假设我们有一个![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)包含![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)元素的集合：{0, 1, … , ![n-1个](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3fd905b384548c9de7011828b88081d5_l3.svg)}。然后，我们生成包含![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)form 中元素的子集![c_k,c_k-1, ..., c_1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6b95ff8b7a91c111d30fbe1d048971c6_l3.svg)，从最小的字典顺序开始：

 

![CS1](https://www.baeldung.com/wp-content/uploads/sites/4/2021/08/cs1.png)

该算法访问所有 -![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)组合。它递归地表示多个嵌套的 for 循环。比如当![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)=3时，相当于嵌套了3个for循环，生成长度为3的组合：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-da739b6ab30cd31276cf0afb978a40fe_l3.svg)

在词典生成算法的每次迭代中，我们找到最小值![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)，即最右边的元素![c_j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5fe3540d7016e8e1cfd16d7e58b7f261_l3.svg)，我们可以增加。然后我们将后续元素设置![c_{j-1},...,c_1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec700d3ede362bf785bf6bdfcb191028_l3.svg)为它们的最小值。

例如，当我们有![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)= 6 和![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)= 3 时，算法会生成按字典顺序排列的序列：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d3c8e1271066d16b1095a669c21b6829_l3.svg)

## 3. 填满背囊一代

我们可以将 - 组合表述![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)为背包问题。随后，最佳解决方案涉及访问二叉树中的节点。

### 3.1. 二叉树

二叉树是用于组合生成的树族。它们由![T_n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54a5ddbba7135b572e4520e4185970a0_l3.svg)for表示![n = 0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-56fd6955aa1e512425f363a7fb56c72b_l3.svg)，这样：

-   如果![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)=0，则树![T_0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5cc0883377e1daf995d90c7b93cf1854_l3.svg)由单个节点组成
-   如果![n>0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f7ee4a95b309e1582dc629eeeea80595_l3.svg)，顺序二叉树![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)包含根和![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)二叉子树![T_{n-1},...,T_1,T_0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d0a5448637d874c690639901a6aba47e_l3.svg)

 

![CS2](https://www.baeldung.com/wp-content/uploads/sites/4/2021/08/cs2.png)

例如，![T_4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ae9693a1d2e11c6f3776d935b7fb67e7_l3.svg)包含根和子树![T_{3},T_2,T_1,T_0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d45357997a7347351f6ffdff953b36c3_l3.svg)：

 

![CS3](https://www.baeldung.com/wp-content/uploads/sites/4/2021/08/cs3.png)

很容易看出![T_n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54a5ddbba7135b572e4520e4185970a0_l3.svg)可以![T_{n-1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-74d50e437998e46348b3da1215fc0698_l3.svg)通过插入 的另一个副本来迭代构建![T_{n-1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-74d50e437998e46348b3da1215fc0698_l3.svg)。所以，![T_n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54a5ddbba7135b572e4520e4185970a0_l3.svg)包含![2^n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0288856e580589b0aa07b6eb5048e37e_l3.svg)节点。此外，每一层的节点数![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)等于![二项式{n}{k}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b58331b1ecfb644b41d5e68c4c8fa0eb_l3.svg)in ![T_n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54a5ddbba7135b572e4520e4185970a0_l3.svg)for ![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)in {0,…, ![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)-1}。从这个意义上说，词典生成算法的变体遍历了![T_n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54a5ddbba7135b572e4520e4185970a0_l3.svg)层上的节点![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)。

### 3.2. 填充背包算法

让我们回想一下[0-1 背包问题](https://www.baeldung.com/cs/knapsack-problem-np-completeness#1-0-1-knapsack-problem)，我们要么从一组大小中取出或不取出任何元素![{n}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0ebdc09385947e8044a554a851d9debb_l3.svg)来填充背包。我们可以用类似的方式来表述我们的问题，我们有一个 大小的背包，![textbf{k}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-82287109a827c80fe0a7ccb4a80c6ea9_l3.svg)我们想要找到集合 S 的元素的所有子集组合。让我们设置![delta_j = S_j - S_{j-1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3ee9a5c83b7e9f6a33771723b4f27b86_l3.svg)为![1 leqj<n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5ca9180241e93579d1aecc762960a492_l3.svg)。![leq k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2841d0ac2b1dc92b74654c68efeb23ed_l3.svg)表单中包含多个元素的子集![c_1,..,c_t](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8aa5bee72112e0495aabdc0a08dfaaf3_l3.svg)是此问题的有效解决方案。

在这种情况下，每个可能的解决方案都对应于 的一个节点![T_n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54a5ddbba7135b572e4520e4185970a0_l3.svg)。我们可以使用以下算法按顺序访问每个有效[节点](https://www.baeldung.com/cs/depth-first-traversal-methods#pre-order-traversal)：

 

![CS4](https://www.baeldung.com/wp-content/uploads/sites/4/2021/08/cs4.png)

该算法只访问 的有效节点![T_n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54a5ddbba7135b572e4520e4185970a0_l3.svg)，跳过无效节点。在初始化阶段之后，我们访问组合![c_1,...,c_t](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c93246a5fb3488661deaa710d16f2a85_l3.svg)，用完![k - r](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-067d2f94d7c4b78bd44bd76b254afa76_l3.svg)的空间![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)。然后我们尝试在不超过空间限制的情况下将元素添加到手头的组合中。如果一个元素![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-276a76eafbebc4494deafceec7cc4ddd_l3.svg)适合背包，我们会在用尽所有可能性后将![c - 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4bf467e00cee1a10fbac608fb3124570_l3.svg)其放入。

## 4. 旋转门一代

表示组合问题的另一种方法![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)是使用格雷码。

### 4.1. 格雷码

想象一下连接两个房间的旋转门。![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)左屋有人![所谓的](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6a42ca1958f559355dfe9fdc73d7857c_l3.svg)，右屋有人。因此，我们![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)总共有人。在这个设置中，我们让一个人通过与其他人交换来进入另一个房间，这样房间里的人数就不会改变：

 

![CS5](https://www.baeldung.com/wp-content/uploads/sites/4/2021/08/cs5.png)

为了代表房间里的人，我们使用![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)二进制字符串。0代表左边房间的人，1代表另一个房间的人。因此，当两个人使用旋转门交换位置时，组合串位中的2位被翻转。

让我们构建一个算法来生成不重复的组合序列。为了生成这样的序列，我们采用[格雷码](https://en.wikipedia.org/wiki/Gray_code)。我们将枚举排序，其中两个连续的表示总是相差一个二进制位。然后，我们将只选择其中包含![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)零和一的二进制表示![所谓的](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6a42ca1958f559355dfe9fdc73d7857c_l3.svg)。因此，代码序列将满足旋转门约束。

![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)根据定义，我们递归地生成长度为 Gray 的二进制代码。让![伽玛_n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-748fcd98524411bf7d42d2c485276b1e_l3.svg)表示长度的二进制代码![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)。我们可以更正式地表述为：

 ![[Gamma_n = 0Gamma_{n-1}, 1Gamma^R_{n-1}]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6ef1727f5382d2bb617e4ba20c095242_l3.svg)

换句话说，我们通过在![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)长度为 -1 的格雷码中插入前导 0 或 1 来生成长度的格雷码![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)。

### 4.2. 旋转门算法

由于我们的集合有![textbf{n}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-66674599730467ff37ddd82eb20c5e42_l3.svg)元素，我们用一个长度为的字符串来制定一个组合![textbf{n}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-66674599730467ff37ddd82eb20c5e42_l3.svg)。该字符串表示哪些元素包含在与 1 的组合中，而不是包含在 0 的组合中。为了确保我们只生成对![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)-combinations 问题有效的代码，我们需要生成包含![所谓的](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6a42ca1958f559355dfe9fdc73d7857c_l3.svg)0 和![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)1 的代码：

 ![[Gamma_{nk,k} = 0Gamma_{nk-1,k}, 1Gamma^R_{nk,k-1}]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b1492cae6a696ad0922e2be03925d61a_l3.svg)

例如，让我们考虑![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)=6 和![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)=3 的情况。让我们生成![伽玛_{3,3}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-38819815c8d1e1c4ca8755bea690c0b6_l3.svg)：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-956967e3a7926f6720d05c4c8c7df380_l3.svg)

将位串转换为组合![c_3c_2c_1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d5a44c1b7c993d386cb59b856e025366_l3.svg)，我们看到一个清晰的模式：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c60c17d99fda2a3dbcceb7ff4bd5d1c9_l3.svg)

在这个序列中，![c_3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-345facb00f83d0c68358cb6877512015_l3.svg)以递增的顺序出现。但对于 的固定值![c_3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-345facb00f83d0c68358cb6877512015_l3.svg)，![c_2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9257b68e89c1a195c57031c2c321d0fc_l3.svg)以递增顺序出现。此外，对于固定![c_3c_2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f256084911bdd1e4cccbbc580e5cc037_l3.svg)组合，![c_1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a387b50352a9aeddfd81637a258caa71_l3.svg)价值再次增加。因此，我们可以说组合的特征交替增加和减少。

综上所述，我们构建了旋转门算法来访问![textbf{k}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-82287109a827c80fe0a7ccb4a80c6ea9_l3.svg)组合：

 

![CS6](https://www.baeldung.com/wp-content/uploads/sites/4/2021/08/cs6.png)

## 5. 近乎完美的方案和完美的方案

![textbf{c_j}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-225046aed553c831423395cd374bce0f_l3.svg)如果每一步只改变一个，我们称灰色路径序列为齐次。由于我们有多个索引同时变化，因此旋转门算法不会生成齐次序列。例如，从 210 到 320 或 432 到 420 的过渡不遵守同质性规则。

![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)我们可以为=6 和![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)=3生成齐次 Gray 序列：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-232bffb203b6aa7a3db0c28c6dd7774d_l3.svg)

这个位串对应于一个齐次序列：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6857346b50cab0c19b0680f9dc3e9a3e_l3.svg)

更好的是，我们可以![{k}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4a7414f95220ee9a6343fddcc5416427_l3.svg)通过强同质转换生成所有组合。在这样的序列中，![textbf{c_j}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-225046aed553c831423395cd374bce0f_l3.svg)索引最多移动 2 步。这些生成方案被称为近乎完美的。

Chase 制定了一个易于计算的近乎完美序列的版本。再一次，假设我们有![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)元素，我们想要找到 -![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)组合。因此，我们递归地生成包含![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)1 和0的二进制序列：![小号 = ñ](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8d085ecfa4e59f0e7ad0119c031eb74a_l3.svg)

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c1909c8521ce333e47d91ef567d76397_l3.svg)

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e8c29a3d10768ee3e48827d46ebd4bb8_l3.svg)

因此，我们可以使用该算法生成一个![textbf{k}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-82287109a827c80fe0a7ccb4a80c6ea9_l3.svg)以近乎完美的顺序访问所有组合的序列。它被称为蔡斯序列。正好有 2 个![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)这样的组合。

从近乎完美的组合推导出来，我们想知道是否可以![textbf{k}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-82287109a827c80fe0a7ccb4a80c6ea9_l3.svg)通过仅切换相邻位来生成 组合序列。这种形式称为完美方案。数学家证明，只有当![k leq 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-66aca667ebdfb9b96402a7c79d6fe149_l3.svg)or![nk leq 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-07d64ca268e06f6c498410ad9f9abd92_l3.svg)或 or![k 次 (nk)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e7fdad7e6ac0f0813407e9051d683ba3_l3.svg)为奇数时，才有可能找到完美的组合。简而言之，找到完美方案的可能性只有四分之一。

## 6.比较

总之，![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)-combinations 生成问题构建了优雅的模式。使用所讨论策略的二进制输出构建的热图使这些模式更容易遵循：

 

![CS7](https://www.baeldung.com/wp-content/uploads/sites/4/2021/08/cs7.png)

上图发表于[“计算机编程艺术”](https://www-cs-faculty.stanford.edu/~knuth/taocp.html)，第 4A 卷(2011 年)。

## 七、总结

在本文中，我们了解了五种不同的枚举![{k}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4a7414f95220ee9a6343fddcc5416427_l3.svg)集合组合的方法。![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)顾名思义，词典顺序算法按字母顺序生成大小的子集。旋转门算法![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)以交替的字典顺序生成 - 组合。

同质生成确保连续的生成仅相差一个元素。近乎完美的方案进一步确保更改的元素最多移动两步。完美的方案允许更改元素的索引仅增加或减少一个。