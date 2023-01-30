## 1. 概述

在本教程中，我们将研究两种类型的合并算法：2-way merge 和![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)-way merge，它们都非常重要。此外，我们将简要介绍双向合并和双向![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)合并，包括它们的工作原理、如何应用某些合并算法以及它们的时间和空间复杂度。

## 2.合并算法

合并是将两种或多种类型的结构组合成一个单一结构的过程，它是[归并排序](https://www.baeldung.com/cs/efficiently-sorting-linked-lists)等算法的重要组成部分。

如果我们有两个或通常更多的数组，我们可以合并它们并得到一个数组或列表。

实际上，合并数组的大小与合并数组的总大小相同。

### 2.1. 排序合并

有多种类型的合并方法。

排序合并只是组合已经排序的结构项。

让我们看下面的例子。首先，我们采用两个或多个排序数组并比较第一项。

比较两个排序数组的第一个元素，取较小的元素并将其添加到输出数组：

![合并排序数组示例](https://www.baeldung.com/wp-content/uploads/sites/4/2022/12/2_merged-sorted_1-1024x592.png)我们继续比较较小的项并将其附加到输出数组，直到输入数组为空：

![合并排序数组示例](https://www.baeldung.com/wp-content/uploads/sites/4/2022/12/2_merged-sorted_2-1024x840.png)

此时，![阵列 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c07a2917635422fd2bf3098baae2388c_l3.svg)是空的。我们将较小的项放入![数组2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0bb83e71a780c717bf8c135a7044b36e_l3.svg)并将其附加到输出，因为另一个输入数组仍需要为空。重复该过程直到![数组2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0bb83e71a780c717bf8c135a7044b36e_l3.svg)：

![合并排序数组示例](https://www.baeldung.com/wp-content/uploads/sites/4/2022/12/2_merged-sorted_3-1024x564.png)

### 2.2. 端到端合并

这种类型的合并只是将一个结构合并到另一个结构的末尾。更不用说合并的结构可以按排序或未排序的顺序排列。

这种技术只是将一个数组附加到另一个数组的末尾，该数组可以是排序的也可以是未排序的：

![端到端合并数组示例](https://www.baeldung.com/wp-content/uploads/sites/4/2022/12/1_end-to-end-merge_1-1024x504.png)

在我们的例子中，我们追加![数组2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0bb83e71a780c717bf8c135a7044b36e_l3.svg)并![阵列 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c07a2917635422fd2bf3098baae2388c_l3.svg)创建![数组 3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fe15917c477bf786d23e0ba4d5bc72e7_l3.svg)了与合并数组相同数量的元素：

![端到端合并数组示例](https://www.baeldung.com/wp-content/uploads/sites/4/2022/12/1_end-to-end-merge_2-1024x540.png)

## 3.双向合并

[双向合并](https://docs.rs/binary-merge/latest/binary_merge/)，也称为二进制合并，通常是一种算法，它采用两个排序列表并将它们按排序顺序合并为一个列表。这是合并排序中广泛使用的方法，它按排序顺序输出给定列表的每个步骤中的最小项目，并构建一个排序列表，其中任何输入列表中的所有项目都与输入列表的总长度成比例。

如果我们分别合并两个数组大小![ 一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-803ebc86a98058148c1932e3f7bbe284_l3.svg)和![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-51cd5bfce34b4611c1146da561301e32_l3.svg)，那么合并后的数组大小将是 和 的![ 一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-04477ed8c678a0153ca87c2cb96b4a21_l3.svg)总和![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-51cd5bfce34b4611c1146da561301e32_l3.svg)。此外，合并需要进行最大![ (i + j)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4527af5668b13c7eed00160a6deae756_l3.svg)比较才能获得合并后的数组。

双向合并的详细实现如下图所示。  

我们按排序顺序比较两个数组中的第一项，然后将较小的元素附加到输出数组：

![2路合并示例](https://www.baeldung.com/wp-content/uploads/sites/4/2022/12/2_merged-sorted_1-1024x592.png)

将较小的项目附加到我们的输出数组后，我们继续比较并将它们附加到，![数组 3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fe15917c477bf786d23e0ba4d5bc72e7_l3.svg)直到输入数组为空：

![2路合并示例](https://www.baeldung.com/wp-content/uploads/sites/4/2022/12/2_merged-sorted_2-1024x840.png)

此时，![阵列 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c07a2917635422fd2bf3098baae2388c_l3.svg)是空的。另一个输入数组必须仍然是空的，因此我们将较小的项放入![数组2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0bb83e71a780c717bf8c135a7044b36e_l3.svg)并将其附加到![数组 3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fe15917c477bf786d23e0ba4d5bc72e7_l3.svg). 我们重复这个过程直到![数组2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0bb83e71a780c717bf8c135a7044b36e_l3.svg)为空。

![2路合并示例](https://www.baeldung.com/wp-content/uploads/sites/4/2022/12/2_merged-sorted_3-1024x564.png)

通过比较每个输入数组中的第一项并将它们附加到我们的输出数组，我们获得了一个合并的排序数组，其大小等于输入数组大小的总和。

双向合并算法的伪代码：

```

```

双向合并算法的工作原理是比较每个列表的第一项，并将最小的一项移到输出列表的顶部。

事实上，重复此操作直到一个列表为空，此时第二个列表的所有条目都附加到最终输出列表。

![ 好的)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0d13f5a8dc21581bbdd1f5576d0023d3_l3.svg) 当时间复杂度为时，空间复杂度为![O(1)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-58a9810327eba570e121768d504d6ede_l3.svg)。

## 4. K-Way合并算法

![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)-way 合并算法，也称为多路合并，是一种将![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)-sorted 列表作为输入并生成一个排序列表作为输出的算法，其大小![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)等于所有输入数组的大小之和。

术语“ [![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)-way 合并算法](https://cs.uno.edu/people/faculty/bill/k-way-merge-n-sort-ACM-SE-Regl-1993.pdf)”指的是接受两个以上排序列表的合并方法：

![钾](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7fb8d8d37cb2b48aee9e97aee7728d8f_l3.svg)-way合并算法的伪代码：

```

```

--way合并的详细实现![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)如下图所示：

```

```

我们首先通过比较列表的索引项来创建锦标赛树，并将![0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8354ade9c79ec6a7ac658f2c3032c9df_l3.svg)具有最小值的获胜者附加到输出列表：
![k 路合并示例](https://www.baeldung.com/wp-content/uploads/sites/4/2022/12/1_k-way-merge-1.1-1024x576.png)

我们继续这个过程，直到列表为空：

![k 路合并示例](https://www.baeldung.com/wp-content/uploads/sites/4/2022/12/1_k-way-merge-1.2-1024x576.png)

事实上，![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)-way 合并技术在![k < 8](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e6ba17fc6840687b837d3572ddc87575_l3.svg). 否则，确定每一步中的最小值将需要非常大量的比较。

空间复杂度为![ 好的)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0d13f5a8dc21581bbdd1f5576d0023d3_l3.svg)，时间复杂度为![ O(N日志K)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f2225dc3489598bc624d7c375bdceed9_l3.svg)。

## 5.总结

在本文中，我们定义了合并算法![钾](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7fb8d8d37cb2b48aee9e97aee7728d8f_l3.svg)——Way Merge 和 Two Way Merge，并讨论了它们的工作原理。合并算法的复杂性已经从时间和空间两个方面进行了解释。因此，读者将对合并算法及其应用领域有更好的理解。