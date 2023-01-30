## 1. 概述

在本教程中，我们将了解两种数据结构：哈希表和 trie。我们将定义一个可以用哈希表或 trie 数据结构解决的问题。然后，我们将比较这两个解决方案并指出它们之间的异同。

## 二、问题描述

让我们从可以通过哈希表或特里树解决的问题开始。给定一个包含字符串列表的字典![{s_1, s_2, ..., s_N}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-38094ec66413bfee56d56e838768be5a_l3.svg)和一个 string ![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fd9cb27edab3f0a8a249bc80cc9c6ee2_l3.svg)，我们要检查它是否![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fd9cb27edab3f0a8a249bc80cc9c6ee2_l3.svg)在字典中。

## 3.哈希表解决方案

我们可以使用[哈希表](https://www.baeldung.com/cs/hash-table-vs-balanced-binary-tree#1-hash-table)来构造字典。哈希表(也称为哈希映射)是一种数据结构，用于以未排序的方式将键映射到值。在我们的问题中，我们可以将字典中的每个字符串视为哈希表的键。

### 3.1. 哈希表构造

当我们将一个字符串放入哈希表时，我们需要一个[哈希函数](https://www.baeldung.com/cs/hashing#hashing)来计算字符串的哈希值。散列函数可以接受可变长度的输入字符串并产生固定长度的输出值。

通常，哈希函数的输出是一个整数索引，指向我们在哈希表中存储字符串的位置。例如，我们可以使用以下[多项式滚动哈希函数](https://en.wikipedia.org/wiki/Rolling_hash)将字符串转换为整数：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6f8b632ca2a704c895e4425424b8e53c_l3.svg)

其中![p](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5faad0904f612a3fa5b27faafb8dc903_l3.svg)和![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)是一些正数。

由于哈希函数需要考虑输入字符串的所有字符，因此哈希函数的时间复杂度为![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg)，其中![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)为输入字符串的长度。因此，构建字典的时间与所有字符串的字符总数成线性关系。

### 3.2. 哈希表查找

当我们检查一个字符串![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fd9cb27edab3f0a8a249bc80cc9c6ee2_l3.svg)是否在哈希表中时，我们可以使用相同的哈希函数来计算 的哈希值![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fd9cb27edab3f0a8a249bc80cc9c6ee2_l3.svg)。然后，我们查找哈希表以查看它是否包含计算的哈希值。

哈希值计算需要![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg)时间，其中![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)是输入字符串的长度![吨](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fd9cb27edab3f0a8a249bc80cc9c6ee2_l3.svg)。通常，如果我们有一个好的散列函数，查找过程需要恒定的时间。因此，整体查找时间复杂度为![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg)。

## 4.特里解决方案

我们还可以使用[trie](https://www.baeldung.com/cs/tries-prefix-trees)数据结构来构造字典。特里树或前缀树是一种特殊的搜索树，其中节点通常由字符串作为键。

在 trie 中，两个节点之间的链接表示键控字符串中的一个字符。例如，以下 trie 表示字符串字典![{to, tea, 10, in, inn}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9993583af538fd08163073833d8a580e_l3.svg)：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-93d2a2a0a7918144bed1920cdb7e8973_l3.svg)

如果一个节点表示字典中的整个字符串，我们将其标记为完整节点。

### 4.1. 特里构造

当我们[将字符串插入到 trie](https://www.baeldung.com/cs/tries-prefix-trees#insert-an-element-in-a-trie)中时，我们从![根](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-98fe607fdcb50415059be670f5541cfe_l3.svg)节点开始并搜索与第一个字符对应的链接。如果链接已经存在，我们沿着链接向下移动到下一个子级别的树，并继续搜索下一个字符。如果链接不存在，我们创建一个新节点并将其链接到与当前关键字符匹配的父链接。

我们对输入字符串的每个字符重复这个构造步骤，直到我们完成所有字符。然后，我们将最后一个节点标记为完整节点。

例如，要![教](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6c7758f2a3e2a6a70fa6d4f0a4baabf0_l3.svg)在上面的示例 trie 中添加一个新词“”，我们首先从节点开始沿着“ ![茶](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a0fd35df64b4881115acd5a9834349e2_l3.svg)”路径。然后，我们为角色和![根](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-98fe607fdcb50415059be670f5541cfe_l3.svg)创建额外的节点和链接。最后，我们将最后一个节点标记为完整节点：![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-276a76eafbebc4494deafceec7cc4ddd_l3.svg)![H](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2ce27f7d2d82e3b238176ec7e7ee9118_l3.svg)

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-43611522509fcb4152243752c8d8b84e_l3.svg)

在 trie 构造中，我们不需要哈希函数来计算键。但是，我们仍然需要遍历输入字符串的每个字符。因此，将字符串添加到 trie 中的时间复杂度也是输入字符串![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg)的![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)长度。

### 4.2. 特里查询

我们可以使用类似的方式来[检查一个字符串是否在 trie 中](https://www.baeldung.com/cs/tries-prefix-trees#lookup)。我们可以从 开始，![根](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-98fe607fdcb50415059be670f5541cfe_l3.svg)并根据输入字符串的内容跟随字符链接。如果我们能找到所有字符的匹配路径，并且最后一个节点是一个完整节点，则输入字符串存在于 trie 中。否则，trie 不包含输入字符串。

由于我们只遍历输入字符串的每个字符一次，因此整体查找时间复杂度也是![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg).

## 5.比较

这两种方法是不一样的。让我们从一些方面来比较两者。

### 5.1. 查询速度

我们在查找哈希表的字符串时，首先会计算该字符串的哈希值，这需要![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg)时间。然后，假设我们有一个很好的散列函数，在内存中定位散列值将需要![O(1)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-66c97a4dfb9f2e2983629033366d7018_l3.svg)时间。因此，整体查找时间复杂度为![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg)。

当我们为 trie 查找字符串时，我们遍历字符串的每个字符并在 trie 中定位其对应的节点。整体查找时间复杂度也是![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg).

然而，trie 有更多的开销来检索整个字符串。我们需要多次访问内存以沿着字符路径定位 trie 节点。对于哈希表，我们只需要为输入字符串计算一次哈希值。因此，我们在哈希表中查找整个字符串时，速度相对更快。

对于哈希表，我们总是计算整个输入字符串的哈希值，无论该字符串是否存在于哈希表中。对于a trie，我们可以在找不到匹配的字符链接时提前停止搜索。因此，如果输入字符串在 trie 中不存在，则 trie 的查找速度可能会更快。

### 5.2. 内存要求

当我们第一次构建哈希表时，我们通常会预先分配一大块内存，通过对内存大小进行统一哈希来避免[冲突](https://www.baeldung.com/cs/hash-collision-weak-vs-strong-resistance)。以后我们往哈希表中插入字符串的时候，只需要存储字符串内容即可。

对于 trie 数据结构，我们需要存储额外的数据，例如字符链接指针和完整节点标志。因此，trie 需要更多的内存来存储字符串数据。但是，如果有很多公共前缀，内存需求就会变小，因为我们可以共享前缀节点。

总的来说，哈希表和 trie 之间的内存需求是基于预先分配的哈希表内存和输入字典字符串的大小。

### 5.3. 应用

哈希表虽然查找速度相对较快，但它只支持整个字符串的精确匹配。trie 解决方案更灵活，可以支持更多应用，例如自动完成。此外，我们可以使用 trie 轻松地按字母顺序打印字典中的所有单词。

因此，如果我们要做一个全文查找的应用，哈希表会更好一些，因为它的查找速度更快。但是，如果我们想返回所有与前缀匹配的单词，那么 trie 就是我们的解决方案。

## 六，总结

在本文中，我们讨论了两种数据结构：哈希表和 trie。我们比较了这两种数据结构的时间复杂度和内存需求。此外，我们还讨论了适合使用它们的应用程序。