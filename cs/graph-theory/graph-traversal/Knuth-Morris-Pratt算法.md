## 1. 概述

在计算机科学中，我们有很多[字符串搜索算法](https://www.baeldung.com/java-full-text-search-algorithms)。在本文中，我们将介绍 KMP (Knuth-Morris-Pratt) 算法，该算法可![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)在大文本中搜索单词的出现次数![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)。

首先，我们将解释朴素搜索算法。

接下来，我们将解释 KMP 算法背后的理论思想。

最后，我们将看一个示例以更好地理解它是如何工作的。

## 2. 朴素搜索算法

朴素搜索算法非常简单。它尝试![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)从文本中每个可能的索引开始![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)匹配单词。![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)以下算法代表了在文本中搜索单词的朴素方法![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)：

```

```

假设单词![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)是![abb](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31252ecd87afa9beefd4034eccb0325b_l3.svg)，文本![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)是![啊啊啊](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5cea9ab16147f263e5e75538bbda5c1c_l3.svg)。让我们看看下表，它显示了朴素算法的步骤：

```

```

正如我们所见，该算法会检查![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)文本中单词的所有可能位置![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)。![一个](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-846b982ad05cb9d733595cd50b8fdc0b_l3.svg)然而，在第二步和第三步中，该算法并没有利用该词部分![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)已经被匹配的事实。相反，该算法从头开始匹配整个单词![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)。

很容易看出，朴素方法的复杂性是![O(n cdot m)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2138bb57faa26b944c66248161b8db16_l3.svg)![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)，文本的长度在哪里![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)，![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)单词的长度是哪里![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)。

## 3. KMP算法

朴素方法的问题在于，当它发现不匹配时，它会移动到文本中的下一个位置，并从头![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)开始比较单词![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)。KMP 算法![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)在试图找到它在文本中的出现之前对该词进行一些分析![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)。

### 3.1. 定义

在开始对 word 进行分析之前![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)，让我们看一下一些基本定义：

1.  前缀：字符串的前缀![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)是从 的开头开始到 的![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)任意位置结束的任何子字符串![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)。换句话说，它是一个子字符串，由 range 中的字符组成![[0，我]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5d25a63013487f8367ff43d131ccfea6_l3.svg)，其中![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)代表 string 的所有可能索引![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)。
2.  后缀：字符串的后缀![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)是从 的任意位置开始![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)到 的末尾结束的任何子串![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)。换句话说，它是一个子字符串，由 range 中的字符组成![[我，n-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-969c13071685d9f3a8d33163a7fc361d_l3.svg)，其中![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)代表 string 的所有可能索引![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)。
3.  正确的前缀：正确的前缀是不等于完整字符串的任何前缀![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)。
4.  正确的后缀：正确的后缀是任何不等于完整字符串的后缀![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)。

### 3.2. LPS理论思想

正如我们所注意到的，当朴素的方法发现不匹配时，它必须在下一步从头开始。为了避免这种情况，KMP算法![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)首先对单词进行一些计算，也就是计算LPS数组。术语 LPS 是指最长的专有前缀，也是专有后缀。

让我们构建一个数组，为 word 的每个索引存储![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)LPS 的长度(这个数组的使用将在 3.4 节中解释)。为了以后更容易使用，我们将考虑单词![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)是从零开始索引的，并将 LPS 值存储在从索引 1 开始的数组中。换句话说，对于每个索引![我在[1，米]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3ea896b4212675b52317ed98f3e26a95_l3.svg)，我们将存储范围的 LPS ![[0, i-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f249753efaddc384087989659ba44363_l3.svg)。

查看下表，其中显示了单词的 LPS 数组![父亲](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5eb664568b395a40c23bd2858f5c29a8_l3.svg)：

```

```

正如我们所见，空字符串的 LPS 等于 -1，因为没有合适的前缀可供考虑。此外，对应于前缀 的索引 3 的![abb](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31252ecd87afa9beefd4034eccb0325b_l3.svg)LPS 等于零，因为它的适当后缀都不匹配任何适当的前缀。但是，对应于前缀 的索引 2 的![一个](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-846b982ad05cb9d733595cd50b8fdc0b_l3.svg)LPS 等于 1，因为该字符串![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0e55b0b3943237ccfc96979505679274_l3.svg)既是正确的前缀又是正确的后缀。

这同样适用于索引 7，它对应于前缀![父亲](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5eb664568b395a40c23bd2858f5c29a8_l3.svg)。它的 LPS 等于 4，因为该字符串![父亲](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4651f939a1b5b27e50c229c5f1c65e96_l3.svg)是最长的专有前缀，也是专有后缀。

### 3.3. LPS算法

计算 LPS 阵列的算法可能有点棘手。我们来看看它的伪代码：

```

```

首先，我们用 -1 初始化基本索引。接下来，我们迭代这个词![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)。假设前一个索引的 LPS 等于![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)。当前索引![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)有两个选项。如果第 i个字符与第 j个字符匹配，它将具有 LPS 等于![j+1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7caa0556981b3780bc707acf340d58b2_l3.svg)(我们假设![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)是从零开始索引)。

但是，如果没有匹配项，我们将需要![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)返回到第二好的 LPS。问题仍然是我们没有计算出第二好的 LPS。

由于最后一个 LPS 是![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)，这意味着到目前为止的第一个![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)字符![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)与最后一个字符匹配。![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)让我们回到第一个![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)角色，检查为他们计算的 LPS。第j个位置存储的LPS对应最长的专有后缀，也是专有前缀。因此，这个值是第二好的 LPS。

之后，我们将第 i个字符与第 j个字符进行比较。如果它们匹配，我们更新LPS 数组中第 i个索引的值。否则，我们重复此步骤，直到找到匹配项或![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)达到 -1 值。

虽然看起来该算法具有平方复杂度，但时间复杂度为![O(米)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-23dca79654236964459dcc2eec184aee_l3.svg)，其中![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)是 的长度![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)。这背后的原因是每个值![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)都会![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)增加一个。无论我们![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)后退多少，我们只会消耗增加的数量。因此，两个嵌套的 while 循环具有线性复杂度。

### 3.4. 搜索引擎优化

KMP算法依赖于LPS阵列。假设我们开始将单词的开头部分![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)与文本匹配![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)。在某些时候，我们注意到不匹配。我们将使用计算出的 LPS 数组，而不是从头开始匹配。

假设在某个步骤中，我们位于文本中的第 i个索引和单词中的第![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)j个![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)索引。这意味着目前，我们能够匹配![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)word的前缀 length ![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)。如果发现不匹配，我们需要找到单词 的第二长匹配前缀![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)，即![脂多糖[j]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-61cf69c250288a8da53b6b1b6db0c18a_l3.svg)。

如果我们继续存在不匹配，我们将始终![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)返回![脂多糖[j]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-61cf69c250288a8da53b6b1b6db0c18a_l3.svg)并尝试再次匹配。此步骤一直持续到我们找到匹配项或![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)达到其初始值 -1 为止。

当我们找到匹配项时，我们将![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)进入下一步。此外，我们增加![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)是因为匹配的部分长度增加了一个。

让我们看一下所描述算法的伪代码：

```

```

所描述算法的复杂度是![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg)，其中![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)是文本的长度![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)。这个复杂的原因和我们计算![脂多糖](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0ca89c257e0740601548cedbb79183d6_l3.svg)数组的时候类似。由于![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)每个增加一次![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)，这意味着无论我们![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)向后移动多少，它都只消耗之前为每个完成的增量![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)。因此，在最坏的情况下![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)将总共后退几步。![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)

KMP 算法的总时间复杂度为![O(n+m)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-13f1f2eadce5ea106cd499d8a11b8836_l3.svg)，其中![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)是文本的长度![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)，![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)是单词的长度![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)。这是因为，对于每次 KMP 搜索，我们首先计算 LPS 数组，然后执行 KMP 搜索过程。

## 4.例子

让我们快速将 KMP 搜索算法应用到一个小例子中。假设文本![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e093fd43ad2c244140c11afe4d4bdff_l3.svg)等于![啊啊啊](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5cea9ab16147f263e5e75538bbda5c1c_l3.svg)并且单词![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)等于![abb](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31252ecd87afa9beefd4034eccb0325b_l3.svg)。首先我们来看一下word的LPS表![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)。

```

```

现在，看一下将 KMP 搜索算法应用于给定文本的结果。

```

```

正如我们所见，在第三步和第四步中，算法使用![脂多糖](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0ca89c257e0740601548cedbb79183d6_l3.svg)数组来设置![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)它的正确位置。因此，当检测到不匹配时，算法不需要从头开始匹配单词![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)。![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)这有助于算法在最后一步中有效地找到单词的出现。

## 5.总结

在本文中，我们介绍了 KMP 搜索算法。首先，我们快速浏览一下朴素算法。接下来，我们开始讨论 KMP 搜索算法本身。我们解释了稍后用于 KMP 搜索的 LPS 阵列。

最后，我们提供了一个应用 KMP 算法在文本中搜索给定单词的示例。