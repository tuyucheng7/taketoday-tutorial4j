## 1. 概述

在计算机科学中，字符串搜索意味着在大文本中查找一个或多个字符串(称为模式)的位置。

在本文中，我们将讨论 Rabin-Karp 字符串搜索算法。首先，我们将解释朴素的字符串搜索算法。接下来，我们将解释 Rabin-Karp 算法并将其应用于示例。

最后，我们将讨论该算法的一些变体。

## 2.朴素算法

### 2.1. 伪代码

这个算法的思路很简单。它的工作原理是尝试从文本中的每个可能位置开始搜索模式。

考虑以下代表朴素方法的算法：

```

```

我们尝试匹配模式![P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fda1e51b12ba3624074fcbebad72b1fc_l3.svg)，从 中的每个可能位置开始![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)。对于每个起始位置，我们不断迭代模式![P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fda1e51b12ba3624074fcbebad72b1fc_l3.svg)和文本![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)以匹配它们。

如果我们达到匹配，我们将答案加一。否则，我们会在发现第一个不匹配时中断循环。

### 2.2. 例子

例如，如果我们想查找![P =“宝贝”](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b6482ef30fe1673c0c9001a4d53b162b_l3.svg)in ，让我们看一下算法的步骤![T="ababaac"](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4479996f390e2f6054bafd316c06ce09_l3.svg)：

```

```

该算法的复杂度为![O(nc点k)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cc1f02d9f724dace1da194abcaee09a7_l3.svg)，其中![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)是文本![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)的长度， 是模式的长度。

## 3. Rabin-Karp算法

首先，我们需要了解 Rabin-Karp 算法中使用的哈希函数的一些背景知识。之后，我们将转向 Rabin-Karp 算法本身。

### 3.1. 哈希函数

以前，我们必须尝试匹配文本中每个位置的模式。为了改进这一点，我们将创建一个可以将任何字符串映射到整数值的函数。

换句话说，我们需要一个函数![H](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a7cedbc00aa5531f310166df85e3a9bb_l3.svg)(称为散列函数)，它接受一个字符串![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)并将其映射到一个整数![H(s)=x](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0c3be15d7feeb4155671a4e87a89bd11_l3.svg)。从现在开始，我们将调用![X](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e5fbfa0bbbd9f3051cd156a0f1b5e31_l3.svg)的哈希值![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)。

但是，并非每个功能都有用。散列函数应该易于计算。我们还应该能够进行预计算，在预计算中，我们应该能够在线性时间内计算出一个字符串的哈希值。

此外，经过预处理后，我们应该能够![s[i...j]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4903acf6b1f786f04130bb339166820a_l3.svg)在常数时间内计算出任意子字符串的哈希值。

### 3.2. 哈希函数示例

也许我们可以使用的最简单的散列函数之一是字符串中字母的 ASCII 码的总和。为简单起见，我们将使用![整数(x)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5a065b725b68b000f4f5996622a34d70_l3.svg)来表示 的 ASCII 码![X](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e5fbfa0bbbd9f3051cd156a0f1b5e31_l3.svg)。正式地：

 ![H(s[0...n-1])=int(s[0])+int(s[1])+....+int(s[n-1])](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9740a5977c5406c6c6b8f8e9c414a560_l3.svg)

由于我们可以使用前缀和快速计算子字符串的哈希值，因此此哈希函数是一个有效的选择。

让我们看几个例子，假设![s="aabbab"](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d231e9ef51e5126c6e4dc5708c2085ff_l3.svg)：

-   ![H(s[2...4]) = H("bba") = 98 + 98 + 97 = 293](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5375a3a75ff51ccedcd83e83556b3bea_l3.svg)
-   ![H(s[3...5]) = H("bab") = 98 + 97 + 98 = 293](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5ebe3b7b656a53c1f532f93c434d29e6_l3.svg)

请注意，![H(s[2...4])=H(s[3...5])](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4ec3e38fcbd31bc91ac8a01f63a6df31_l3.svg)即使![s[2...4] neq s[3...5]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8eeb18230218e7b06e9e0ef5f1af4534_l3.svg). 当两个不同的字符串具有相同的散列值时，这种情况称为冲突。

通常，散列函数引起的冲突越少越好。我们的散列函数有很多冲突，因为它没有考虑字母的顺序和位置。稍后，我们将讨论更好的散列函数。

使用讨论的散列函数检查散列结构的以下实现：

```

```

第一个函数![热](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fcd2236a173062325daecc57c31c66d6_l3.svg)对给定的字符串执行预先计算。我们遍历字符串![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)并计算字符串字符的 ASCII 值的前缀和。

第二个函数![获取哈希值](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-bd9e8b54c76ef8c5043604ef8afc3e37_l3.svg)用于计算给定范围的哈希值![[左，右]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-aeea634230c22e2940acb7070696d9ef_l3.svg)。为此，我们在减去范围开头的前缀和之后返回范围末尾的前缀和。因此，我们得到范围内值的总和![[左，右]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-aeea634230c22e2940acb7070696d9ef_l3.svg)。

预计算函数的复杂度为![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg)，其中![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)为字符串的长度。另外，哈希计算函数的复杂度为![O(1)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-66c97a4dfb9f2e2983629033366d7018_l3.svg)。

### 3.3. Rabin-Karp 主要思想

之前，我们解释了什么是哈希函数。散列函数最重要的特性是，如果我们有两个字符串![s_1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-72875fe35f5f804d7eeac0be099ecec2_l3.svg)and![s_2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-bcde5186fbe9faa1ad7103596c94770b_l3.svg)和![H(s_1) neq H(s_2)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4b767285e29905613ceeccf58ebf67fd_l3.svg)，我们可以安全地假设![s_1 neq s_2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b6fc6a1b99c022c1d1e7845638b8900c_l3.svg).

但是，如果![H(s_1) = H(s_2)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1d94bab179aaba664f13d7e9fda7d9ba_l3.svg)，我们不能保证，![s_1 = s_2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0f5146c45d2f1e1dd83d55a4f1283d71_l3.svg)因为两个不同的字符串可能具有相同的哈希值。

现在，让我们看一下 Rabin-Karp 字符串搜索算法：

```

```

假设我们正在寻找![P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fda1e51b12ba3624074fcbebad72b1fc_l3.svg)文本中的模式![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)。我们目前位于![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)中![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)，我们需要检查是否![T[i...i+k-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f0e7ea44ccb25d0429ef71834a7a9f92_l3.svg)等于![P[0...k-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7bcebfc9ff5dceff68c6f640e1ce3722_l3.svg)，其中![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)等于 的长度![P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fda1e51b12ba3624074fcbebad72b1fc_l3.svg)。

如果![H(P[0...m-1]) neq H(T[i...i+m-1])](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-de6245bb79288bbbfc042850b2348a34_l3.svg)那么我们可以假设这两个字符串不相等，并跳过这个位置的匹配尝试。否则，我们应该尝试逐个字符地匹配字符串。

该算法的最坏情况复杂度仍然是![O(nc点k)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cc1f02d9f724dace1da194abcaee09a7_l3.svg)，其中![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)是文本![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)的长度， 是模式的长度。

但是，如果我们使用一个好的散列函数，预期的复杂度将变为![O(n + k cdot t)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-aa9a1994e550a33e7403ae37062c8242_l3.svg)，其中![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fd9cb27edab3f0a8a249bc80cc9c6ee2_l3.svg)匹配数为。

这背后的原因是一个好的散列函数很少会引起冲突。因此，当没有匹配项时，我们很少需要比较两个子串。此外，如果我们只想检查模式是否存在，复杂度将变为![O(n+k)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a1ededa8b7e6f4e787c0736942d9daa7_l3.svg)，因为我们可以在第一次出现后中断。

### 3.4. 拉宾-卡普示例

要了解 Rabin-Karp 的工作原理，让我们重新审视我们在 2.2 节中使用的示例。与朴素算法不同，我们不需要在每个位置都匹配模式。

我们来看看匹配过程：

```

```

与朴素算法相比，我们只需要检查 5 个位置中的 2 个。

在下一节中，我们将讨论一个更好的散列函数，它可以消除大部分误报。因此，它将减少我们需要检查的位置数量。

## 4. 一个好的哈希函数

### 4.1. 定义

早些时候，我们讨论了基于 ASCII 码总和的哈希函数。但是，该哈希函数没有考虑字母的顺序或位置。例如，字符串的任何排列都将具有相同的哈希值。

相反，我们将尝试将字符串表示为 base 中的数字![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)，其中![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)是一个比所有 ASCII 代码都大的素数。

例如，假设我们有字符串![s="算盘"](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c757d0a7f6a19324b998e714a6527e0d_l3.svg)。它在 base 中的表示![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)是：

![整数('a')times B^3 + int('b')times B^2 + int('a')times B + int('c')](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a673b47f08998a47a07243c1856a3af6_l3.svg)

![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)通常，具有长度的字符串![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)将具有以下哈希值：

![int(s[0])times B^{n-1} + int(s[1]) times B^{n-2} + ... + int(s[n-2]) times B + int(s[n-1])](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-86f4fb9de9fe2c07c4a6e9719ac6566e_l3.svg)

由于我们在有效数字系统中表示字符串，因此不同字符串的哈希值将是不同的。但是，当表示一个长字符串时，散列值会变得很大。因此，计算和存储它们的效率很低。

相反，我们将计算散列值模![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)，其中![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)是一个大质数(通常在 附近![10^9](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b928d0b2256bd6e1e05e7757a77ec1be_l3.svg))。素数越大，它引起的碰撞就越少。

### 4.2. 执行

如前所述，我们应该能够在预处理后的常数时间内计算出任何子字符串的哈希值。

让我们尝试计算![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)长度字符串中每个前缀的哈希值![5](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-48348ef601c56286abf49bafe09c7af1_l3.svg)：

```

```

请注意，我们有![前缀[i] = 前缀[i-1] times B + int(s[i])](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7f3ae352df1928fdbe2ea5530ff2d08b_l3.svg)。这将使我们能够在线性时间内计算前缀数组。此外，我们还需要计算一个数组![战俘](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d8ac186a62c69da6e9c341684d20d9a2_l3.svg)来存储素数的不同幂次![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)。

但是，我们仍然需要找到一种方法来计算常数时间内任意子字符串的哈希值。

例如，假设我们要计算 的值![H(s[2...4])](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c973f480468d63e1619758a6f4f6510c_l3.svg)。结果应该是![整数(s[2]) times B^2 + int(s[3]) times B + int(s[4])](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2b3c710364c07671fe38fd3bfb40c2a1_l3.svg)。

这个值在 中是可用的![前缀[4]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ce7afef050da2f357a3dea822e1f3984_l3.svg)，但是我们需要去掉 和 对应的![小号[0]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8cd3eb31e0502ed4538a74719731aa18_l3.svg)项![小号[1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6eb96e48fce42ac35a1f2cbee3417457_l3.svg)。这两个术语在![前缀[1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-18a8f08166a631560cea075642812e43_l3.svg). 但是，当从 移动![前缀[1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-18a8f08166a631560cea075642812e43_l3.svg)到 时![前缀[4]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ce7afef050da2f357a3dea822e1f3984_l3.svg)，它们![4-1=3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4ed4e585c9adbc9f458c5a50077af375_l3.svg)乘以![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)。

要删除它们，我们需要乘以![前缀[1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-18a8f08166a631560cea075642812e43_l3.svg)然后![乙^3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-bf53f55f4d6f659a30b2af0e4dc7f2fe_l3.svg)从中减去![前缀[4]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ce7afef050da2f357a3dea822e1f3984_l3.svg)。因此，![H(s[2...4]) = 前缀[4] - 前缀[1] times B^{4-1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f0a0ba6c4aac0a42803921973b3de02e_l3.svg)。

作为一项规则，![H(s[L...R]) = 前缀[R] - 前缀[L-1] times B^{R-L+1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a3ab78dd49a50846eb53cd357034cc8f_l3.svg)。

现在我们已经准备好这些等式，让我们实现以下哈希结构：

```

```

预计算的复杂度为![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg)，其中![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)为字符串的长度。此外，哈希函数的复杂度为![O(1)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-66c97a4dfb9f2e2983629033366d7018_l3.svg).

使用此散列函数可降低发生冲突的可能性。因此，我们在大多数情况下都能达到预期的Rabin-Karp复杂度。但是，无论哈希函数如何，当有很多匹配项时，算法仍然会很慢。

在下一节中，我们将讨论 Rabin-Karp 的非确定性版本。

## 5. Rabin-Karp 变奏曲

### 5.1. 非确定性版本

这个版本背后的想法是使用多个哈希函数，每个函数都有不同的模值![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-27d6692c77760dc1111628e74a6d272f_l3.svg)和基数![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)。

如果两个字符串在多个哈希函数中具有相等的哈希值，则它们相等的概率非常高，以至于我们可以安全地假设它们在非敏感应用程序中相等。

这使我们能够完全依赖这些散列函数，而不必在散列值相等时逐个字符地检查字符串。因此，这个版本的复杂度是![O(n+k)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a1ededa8b7e6f4e787c0736942d9daa7_l3.svg)，其中![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)是文本![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)的长度， 是模式的长度。

### 5.2. 多个等长模式

Rabin-Karp 算法可以扩展到处理多个模式，只要它们具有相同的长度。

首先，我们计算每个模式的哈希值并将它们存储在映射中。接下来，当检查文本中的某个位置时，我们可以在常数时间内检查子字符串的哈希值是否在映射中可用。

如果应用于确定性版本，则此版本的预期复杂度为![O(n+SumPatterns+SumMatches)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7627d5e620367ea8224310acf1618ca0_l3.svg)，其中![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)是文本的长度，![求和模式](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6f0d61397c7ab22e68de33ff73f9dc2f_l3.svg)是所有模式![求和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-97b40b4a5539a41b2281b1aa906059a9_l3.svg)的总长度， 是所有匹配项的总长度。另一方面，如果它应用于非确定性版本，则复杂度为![O(n+SumPatterns)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3439d7ed4077ade4077a01b34f83f3a4_l3.svg).

## 六，总结

在本文中，我们讨论了字符串搜索问题。

首先，我们研究了朴素算法。

之后，我们讨论了Rabin-Karp是如何依靠哈希函数来改进这个算法的。然后，我们解释了如何选择好的哈希函数。

最后，我们研究了 Rabin-Karp 的一些变体，例如非确定性版本和将 Rabin-Karp 应用于多种模式。