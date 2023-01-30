## 1. 概述

在处理字符串问题时，我们会遇到一个叫做[回文](https://www.baeldung.com/java-palindrome)的术语。

在本教程中，我们将展示什么是回文。此外，我们还将解释[Mahacher](https://www.baeldung.com/java-palindrome-substrings)算法，该算法处理字符串中的回文子串![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)。

最后，我们将给出一些使用 Manacher 算法的应用程序。

## 2.定义

### 2.1. 回文的定义

首先，我们需要定义什么是回文。回文串在向前和向后阅读时是相同的。例如，字符串 { ![阿巴](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-03232e6097b33dfb35c6403bb63ef1a0_l3.svg), ![一个](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-846b982ad05cb9d733595cd50b8fdc0b_l3.svg), ![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0e55b0b3943237ccfc96979505679274_l3.svg), ![算盘](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-afcd691b810ff1c9db193058e2ec1a75_l3.svg)} 是回文，而 { ![ab](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e4f5ccaf300b272b883388de7244e634_l3.svg), ![cbbd](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-38f487d9ea9326a24ac7e58d29fe42fb_l3.svg)} 不是。

根据这个定义，我们可以定义回文的中心。回文的中心是将其分成两半的字符串的中间。每一半都是另一半的镜像。

让我们来看下面的例子：

![例子回文](https://www.baeldung.com/wp-content/uploads/sites/4/2020/09/Example_Palindrome-1536x971-1-1024x647.png)

正如我们所见，在奇数长度回文的情况下，中心是回文本身的一个元素。中心之前的部分称为回文的左侧。同样，中心之后的部分称为右侧。

然而，在偶数长度回文的情况下，我们没有中心元素。相反，我们只有回文的左侧和右侧。

在这两种情况下，回文的右侧都是左侧的镜像。换句话说，左右两边互为反面。

需要考虑的另一个注意事项是，如果字符串![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)是回文，那么如果我们从 中删除第一个和最后一个字符![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)，我们也会得到一个回文字符串。

类似地，如果一个字符串![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)是一个回文串，那么在开头和结尾添加相同的字符![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)也会产生一个回文串。

一旦我们稍后查看算法，这些属性将被证明是有用的。

### 2.2. 定义问题

现在我们对回文有了更好的理解，我们可以解释一下 Manacher 算法解决的问题。

一开始，问题给了我们一个字符串![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)。之后，问题要求我们计算两个数组![奇P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-897d4ca1195e4ec4b7a3c266b5fc873a_l3.svg)和![甚至P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-eb668ae0099a167f2f56396edb51e32f_l3.svg)。两个数组都应存储以每个索引为中心的最长回文子串 (LPS) 的指示![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)。

结果，![s[i - oddP[i], i + oddP[i]]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c7373748ba0324603ac14c3cb9b6ef5e_l3.svg)将是最长的奇数长度回文，其中心位于索引处![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)。同样，![s[i - evenP[i] + 1, i + evenP[i]]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5c72e5f834c72f5b4cb26e6e0a32dfec_l3.svg)将是最长的偶数长度回文，使得其左侧的最后一个字符位于 index 处![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)。

首先，我们将解释解决这个问题的简单方法。之后，我们将解释 Manacher 算法对朴素方法所做的优化。

## 3. 天真的方法

天真的方法很简单。从字符串中的每个索引开始，它会尝试尽可能长地扩展两边。

让我们看一下朴素的方法实现：

```

```

我们遍历![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)string中所有可能的索引![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)。对于每个索引，我们设置![奇P[i]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4532c38f87138fd4a5c74ed639e85fe9_l3.svg)为零。此外，我们将![大号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-48d71fca322532f0abc2c4ad2cf98154_l3.svg)和设置![R](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d6abdd487c56e5efbb2c9522ed4b9360_l3.svg)为要检查的下一个范围的索引。

接下来，我们检查当前范围是否形成回文。这个想法是，如果范围![[左，右]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-aeea634230c22e2940acb7070696d9ef_l3.svg)形成回文，我们移动到下一个范围，即![[L-1, R+1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8afc9fce3fc8583579d03d74d28cf05a_l3.svg); 我们只需要检查 index![L-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e1bb1dfe54f5b0a513c13a3592d40bbf_l3.svg)和处的字符![R+1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9bb4a0040a411ddda59b8d22fab34fd2_l3.svg)。

如果当前范围形成回文，那么我们增加![奇P[i]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4532c38f87138fd4a5c74ed639e85fe9_l3.svg)一个。此外，我们将范围扩展到左侧和右侧。

之后，我们执行类似的操作来计算![甚至P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-eb668ae0099a167f2f56396edb51e32f_l3.svg)数组。不同之处在于我们关注 和 的初始![大号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-48d71fca322532f0abc2c4ad2cf98154_l3.svg)值![R](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d6abdd487c56e5efbb2c9522ed4b9360_l3.svg)。原因是在偶数长度的回文中，![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)是回文子串左侧最后一个字符的索引。其余类似于计算![奇P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-897d4ca1195e4ec4b7a3c266b5fc873a_l3.svg)数组。

最后，我们返回计算的![奇P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-897d4ca1195e4ec4b7a3c266b5fc873a_l3.svg)和![甚至P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-eb668ae0099a167f2f56396edb51e32f_l3.svg)数组。

朴素方法的复杂性在于![O(n^2)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-894959b13d80157796705e7eafb4d243_l3.svg)，其中![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)是字符串的长度![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)。

## 4.马纳赫算法

Manacher 的算法以与朴素方法类似的方式计算![奇P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-897d4ca1195e4ec4b7a3c266b5fc873a_l3.svg)和数组。![甚至P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-eb668ae0099a167f2f56396edb51e32f_l3.svg)但是，如果可能，它会尝试使用已经计算出的值，而不是从头开始检查所有可能的范围。

先解释一下大概的思路。之后，我们可以讨论实现和复杂性。

### 4.1. 大概的概念

让我们以下面的例子来更好地解释奇数回文的思想：

![马纳赫奇例](https://www.baeldung.com/wp-content/uploads/sites/4/2020/09/Manacher_Odd_Example-1536x454-1-1024x303.png)

假设我们已经计算了![奇P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-897d4ca1195e4ec4b7a3c266b5fc873a_l3.svg)从索引 0 到 7 的所有值，我们需要计算![奇P[8]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d03ed5d80cfdbfa767dd4b8532e87d5f_l3.svg).

在计算之前![奇P[8]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d03ed5d80cfdbfa767dd4b8532e87d5f_l3.svg)，我们将把结束的回文子串尽可能地保持在最右边。选择最右边范围的原因将在第 4.4 节中解释。在我们的例子中，就是 substring ![s[3, 9]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f097ce089552aad43dc73a95893f4945_l3.svg)。因此，![大号=3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-baef69694d821e3a3844ea8d3beeec39_l3.svg)和![R=9](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c43e30845812282cb50852f6894f50a5_l3.svg)。

现在，要计算![奇P[8]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d03ed5d80cfdbfa767dd4b8532e87d5f_l3.svg)，我们可以遵循朴素的方法。但是，我们可以使用已经计算出的值来减少比较次数。

既然我们知道子串是回文，那么我们就可以根据范围的中心![s[长, 右]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2f53200657d900493b6d171ae1bfc89f_l3.svg)计算索引的镜像。在此示例中，中心是索引 6。因此，镜像是。![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)![[左，右]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-aeea634230c22e2940acb7070696d9ef_l3.svg)![我=8](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2556690afb599295671bcb8b01e76a9c_l3.svg)![j=4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-90968d86bffec68e355742d2b69e0a8f_l3.svg)

由于左侧是右侧的镜像，我们可以说![奇数P[i] = 奇数P[j]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-015ea34d3e6834c8e44665307d1a1d73_l3.svg)。

但是，有两种情况需要考虑。第一种情况类似于给定的示例。我们可以扩展![奇P[i]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4532c38f87138fd4a5c74ed639e85fe9_l3.svg)的值![奇P[j] = 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5d6156b9ac6c23fa7a65405419828004_l3.svg)。因此，我们可以![奇P[i]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4532c38f87138fd4a5c74ed639e85fe9_l3.svg)从 2 开始扩展并向前移动，而无需检查 的值![奇P[i]=1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5e19b7e441141621f963925cd57b1502_l3.svg)。在示例中，我们将得到![奇P[8]=2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-51c5fbde1d7d609f8793abf8a3f17c2a_l3.svg).

第二种情况在第二个例子中给出：

![Manacher 奇例 2-1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/09/Manacher_Odd_Example_2-1-1536x454-1-1024x303.png)

在这种情况下，我们不能说那是![奇P[i]=奇P[j]=4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d5ee99b924137811a0ac7d2a7800bee0_l3.svg) 因为 ![奇P[j]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e39b2b67ba7d42750708d5e162278bd4_l3.svg)对应于范围![[0, 8]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a646c91a48f97b852a365a00f827e917_l3.svg)，它超出了![大号=3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-baef69694d821e3a3844ea8d3beeec39_l3.svg)和的范围![R=9](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c43e30845812282cb50852f6894f50a5_l3.svg)。

因此，我们只能确定 的值，![奇P[i]=Ri=1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e3450326c27290838411aca72d244436_l3.svg)因为它是 range 中包含的最大范围![[3, 9]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-41bd887e448d2f7f7d9069cf68a8930e_l3.svg)。之后，我们可以尝试按照天真的方法扩展范围。在此示例中，范围不能再扩展。

### 4.2. 奇数回文算法

现在我们了解了总体思路，我们可以检查实现。看一下 Manacher 奇数回文算法的实现：

```

```

一开始，我们用![大号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-48d71fca322532f0abc2c4ad2cf98154_l3.svg)0 和初始化，表示一个空范围。然后，我们从左到右遍历字符串。![R](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d6abdd487c56e5efbb2c9522ed4b9360_l3.svg)![-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-37abf2e602a43ae0ff9f12b1536fa74c_l3.svg)![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)

对于每个 index ![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)，我们初始化![奇P[i]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4532c38f87138fd4a5c74ed639e85fe9_l3.svg)为朴素方法的默认值，即零。现在，我们尝试使用已经计算出的值。

如果![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)在范围内![[左，右]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-aeea634230c22e2940acb7070696d9ef_l3.svg)，我们更新值![奇P[i]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4532c38f87138fd4a5c74ed639e85fe9_l3.svg)成为它的镜像索引的值。但是，我们注意镜像索引结果超出范围的情况![[左，右]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-aeea634230c22e2940acb7070696d9ef_l3.svg)。

因此，我们取 between![奇P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-897d4ca1195e4ec4b7a3c266b5fc873a_l3.svg)中的最小值作为镜像索引，以及我们可以从当前回文范围中取的最大长度![[左，右]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-aeea634230c22e2940acb7070696d9ef_l3.svg)。

既然![左+右](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ce19ad7d5e92c99acbc0d5cd5c5d223c_l3.svg)是 的镜像索引![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)，那么检查 是否![里](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-169d6d2d186aefdcf30efceae30fc632_l3.svg)小于就足够了![奇P[L+Ri]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1aa7b2e20b542241c2515714258161fa_l3.svg)。原因是![R-i](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6cab7daa11ebf1e7d7bbaa8e9c3366d1_l3.svg)等于![j-L](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ef0cd0a9af181f4b8aa25585d3657bc5_l3.svg)，其中![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)是的镜像索引![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)。因此，检查它们中的任何一个是否小于![奇P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-897d4ca1195e4ec4b7a3c266b5fc873a_l3.svg)镜像索引就足够了。

一旦我们将初始值分配给![奇P[i]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4532c38f87138fd4a5c74ed639e85fe9_l3.svg)，我们就简单地遵循天真的方法并尝试尽可能扩大以 index![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)为中心的范围。但是，在这种情况下，我们从![奇P[i] + 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-13f1248edf8caba83304be898b4f36ab_l3.svg).

之后，我们检查是否需要更新 和 的![大号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-48d71fca322532f0abc2c4ad2cf98154_l3.svg)值![R](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d6abdd487c56e5efbb2c9522ed4b9360_l3.svg)。由于我们需要存储尽可能靠右的回文范围，因此我们检查当前范围是否比存储范围更靠右。如果是这样，我们更新 和 的![大号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-48d71fca322532f0abc2c4ad2cf98154_l3.svg)值![R](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d6abdd487c56e5efbb2c9522ed4b9360_l3.svg)。

最后，我们返回计算出的数组![奇P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-897d4ca1195e4ec4b7a3c266b5fc873a_l3.svg)。

### 4.3. 偶数回文算法

偶数长度回文的算法与奇数长度回文的算法相比有小的修改。看一下它的实现：

```

```

该算法与算法 2 类似。但是，我们更改了![剩下](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-38a9feb4cc268557d58c068f561392d1_l3.svg)和的初始值![正确的](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6fa3bbfa25e9833c97903c647b48dda5_l3.svg)以适应下一个要检查的范围。原因是偶数长度的回文有两个中心索引。在我们的实现中，![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)是两个中心索引之间的左侧索引。

之后，我们尽量扩大回文范围。我们注意从值开始比较![偶数P[i]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ba908e18f4565be0b253eb814f716448_l3.svg)，而不是总是从零开始。

完成后，我们将更新当前存储的最右侧范围。

最后，我们返回计算出的数组![甚至P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-eb668ae0099a167f2f56396edb51e32f_l3.svg)。

### 4.4. 概念验证

首先，让我们证明始终保留最右边的回文子串的最优性。假设我们要保留一个右侧较小的范围。然而，它是一个更大的范围，而且它的起点比存储的范围更靠左。

在这种情况下，我们将最小化计算或![R-i](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6cab7daa11ebf1e7d7bbaa8e9c3366d1_l3.svg)时考虑的值。因此，我们最终可能会得到或的较小初始值。结果，我们最终会执行比预期更多的比较。![奇P[i]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4532c38f87138fd4a5c74ed639e85fe9_l3.svg)![偶数P[i]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ba908e18f4565be0b253eb814f716448_l3.svg)![奇P[i]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4532c38f87138fd4a5c74ed639e85fe9_l3.svg)![偶数P[i]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ba908e18f4565be0b253eb814f716448_l3.svg)

由此，我们可以得出总结，保持最右边的范围始终是最佳的。

现在，让我们讨论复杂性。

### 4.5. 复杂

乍一看，我们可能认为复杂性类似于朴素的方法。然而，更仔细的检查会告诉我们，每次我们![奇P[i]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4532c38f87138fd4a5c74ed639e85fe9_l3.svg)要么从 要么![奇数 P[L + R - i]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1f9bccb5e85663625bc474d405d45b61_l3.svg)从开始![R-i](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6cab7daa11ebf1e7d7bbaa8e9c3366d1_l3.svg)。

如果![奇数 P[L + R - i]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1f9bccb5e85663625bc474d405d45b61_l3.svg)小于![R-i](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6cab7daa11ebf1e7d7bbaa8e9c3366d1_l3.svg)，则范围不再扩大。原因是如果范围扩大，值![奇数 P[L + R - i]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1f9bccb5e85663625bc474d405d45b61_l3.svg)会更大，因为范围的两边是![[左，右]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-aeea634230c22e2940acb7070696d9ef_l3.svg)彼此的镜像。

因此，在这种情况下，我们不会进行任何成功的比较。

否则，如果![R-i](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6cab7daa11ebf1e7d7bbaa8e9c3366d1_l3.svg)小于![奇数 P[L + R - i]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1f9bccb5e85663625bc474d405d45b61_l3.svg)，那么我们可能会成功进行比较。但是，在这种情况下，我们将探索比当前范围更靠右的回文范围。因此，每次成功的比较都将导致![R](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d6abdd487c56e5efbb2c9522ed4b9360_l3.svg)向右的后续向前移动。

结果，每次成功的比较操作也会导致向前迈出一步![R](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d6abdd487c56e5efbb2c9522ed4b9360_l3.svg)。此外，我们可以注意到它![R](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d6abdd487c56e5efbb2c9522ed4b9360_l3.svg)永远不会减少。

因此，内部 while 循环在大多数![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)时候都会被执行。因此，Manacher 算法的复杂度为![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg)，其中![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)字符串中的字符数为![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)。

## 5.应用

让我们检查一些可以使用 Manacher 算法的应用程序。

### 5.1. 最长回文子串

假设问题给了我们一个字符串![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)，要求我们计算字符串内部最长的回文子串![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)。

这是 Manacher 算法的直接应用。我们只需计算两个数组![奇P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-897d4ca1195e4ec4b7a3c266b5fc873a_l3.svg)和![甚至P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-eb668ae0099a167f2f56396edb51e32f_l3.svg)，如算法 2 和 3 所示。

由于这些数组为每个索引存储了最长的回文子串，我们只需要检查值![奇数P[i] times 2 + 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3604877e49e434bc989ba8b561ef95b6_l3.svg)和![偶数 P[i] times 2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-58c9bde44e50d84de63e7ed869c64cc5_l3.svg)。从所有这些值中，答案是其中可能的最大值。

所描述方法的时间复杂度是![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg)，其中![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)是字符串的长度![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)。

### 5.2. 回文子串数

另一个应用是计算给定字符串中回文子串的数量![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)。

类似地，我们计算两个数组![奇P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-897d4ca1195e4ec4b7a3c266b5fc873a_l3.svg)和![甚至P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-eb668ae0099a167f2f56396edb51e32f_l3.svg)，如算法 2 和 3 所示。之后，我们迭代这两个数组的所有可能值。

该数组![奇P](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-897d4ca1195e4ec4b7a3c266b5fc873a_l3.svg)存储最长回文子串每边的长度。通过删除每个子串的第一个和最后一个字符，我们得到一个新的回文。

因此，我们应该计算：

 ![[sumOdd = sum_{i=0}^{n - 1} oddP[i] + 1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f7ea39e837ad72aa0ba6805108661c4f_l3.svg)

请注意，我们为每个索引添加了一个，![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)表示每个字符都是它自己的回文。

此外，我们还需要计算：

 ![[sumEven = sum_{i=0}^{n - 1} evenP[i]]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-26081e98357b0cb1e4a236c1b1ecabd7_l3.svg)

最后，我们应该返回 的值![奇数和+偶数和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-63f969db7be59f444d171a20091440f0_l3.svg)。

所描述的解决方案可以在![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg)时间复杂度上实现，其中![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)是 string 的长度![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)。

### 5.3. 不同回文子串的数量

这个问题与上一个问题类似。然而，不是一般地计算回文子串的数量，而是要求我们计算不同回文子串的数量。因此，如果两个或多个相等的子串是回文，答案只需加一。

这个问题有点棘手，需要很好地理解 Manacher 的算法。

首先，当我们取 index 的镜像的 LPS 值时![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)，这个值对应于与 index 处的子串完全相同的子串![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)。我们不需要计算这个值，因为我们在镜像索引的时候已经计算过了。

但是，每个扩展操作都可能产生一个新的不同子串。请注意，大多数![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)时候都会进行扩展操作。

我们可以使用哈希函数，类似于[Rabin-Karp](https://www.baeldung.com/cs/rabin-karp-algorithm)算法中使用的函数。哈希函数可以以恒定的时间复杂度给我们某个子串的哈希值。因此，我们可以在线性时间内计算出所有扩容后子串的哈希值。

最后，答案是没有重复的结果哈希值的数量。为此，我们可以将所有哈希值插入到一个[哈希集中](https://www.baeldung.com/java-hashset)。然后，答案将是散列集的大小，因为它只添加相同的值一次。

这种方法可以在![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg)时间复杂度上有效地实现，其中![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)是字符串的长度![秒](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edc883862ceed1a21913f60358e31d8_l3.svg)。

## 六，总结

在本教程中，我们解释了术语回文并讨论了给定字符串中的回文子串。

首先，我们解释了天真的方法。

之后，我们解释了 Manacher 的算法及其获得奇数长度和偶数长度回文的两个版本。

最后，我们提出了一些可以使用线性时间复杂度的 Manacher 算法解决的问题。