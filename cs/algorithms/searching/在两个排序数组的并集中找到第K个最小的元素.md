## 1. 概述

在本教程中，我们将讨论在两个排序数组的并集中找到第 k个最小元素的问题。我们将介绍一种朴素的方法，然后对其进行改进。

最后，我们将比较这两种方法以及何时使用它们。

请注意，我们有[一篇](https://www.baeldung.com/java-kth-smallest-element-in-sorted-arrays)针对此问题的基于 Java 的文章。因此，在本教程中，我们将更多地关注理论思想和比较。

## 2. 定义问题

在这个问题中，我们有 2 个数组，![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)大小分别为 size![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)和![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)size ![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)。此外，问题会给我们一个整数![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)。数组![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)和![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)都应按递增顺序排序。

换句话说：

![A_i leq A_{i+1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8364fb13c0efc2f50fd7d0d95dabc91d_l3.svg)每个![0 leq i < n-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-30390f349b3434b70b836c592ad45780_l3.svg)

![B_j leq B_{j+1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-664e4f4d10164d65c419e64dab801737_l3.svg)每个![0 leq j < m-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-992b6be6c958079a7acae614d9407429_l3.svg)

我们必须在 和 的排序并集中找到第 k个最小元素。![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)和的排序并集![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)，我们指的是大小![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)为 的数组，它包含 和 的所有元素，也按升序排序。![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)![n+m](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-91263778e0adb33c8cd6939a7882bbf2_l3.svg)![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)

让我们以下面的例子来更好地解释这个想法：

![例子](https://www.baeldung.com/wp-content/uploads/sites/4/2020/08/Example-1024x583-1.png)

正如我们所见，两个数组![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)和![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)最初都是排序的。同样，数组和![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)的并集也被排序。此外，我们可以看到该数组包含来自和的所有元素。![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)

因此，如果![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)等于 6，那么答案就是 7。原因是 7 在数组中的索引 5 处![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)(因为我们从零开始对数组进行编号)。

另一种看待它的方式是，数字 7 在数组![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)和![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)中都有五个比它小的数字。因此，它是第六小值。

## 3. 天真的方法

首先，我们将探索朴素的方法。我们来看看它的实现：

```

```

一开始，我们设置了两个迭代器![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)，它们将分别向我们显示数组和![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)中的当前索引。![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)

现在，让我们考虑一下如何合并数组![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)和![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg). 我们将从头开始。每次，我们都会将最小值添加到结果数组中。此外，我们将从其位置中删除最小值。让我们在这种方法中做一些类似的事情。

在每一步中，我们将答案设置为 和 之间的较小![一个[我]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-42484bff0529bb02d3d57d306e1b611b_l3.svg)值![B[j]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-69dc117c3f8d86b2a58de95f872e1522_l3.svg)。此外，我们将具有此较小值的迭代器向前移动一步。

另外，我们会注意![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)or![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)超出各自数组范围的情况。在这种情况下，我们移动另一个迭代器。一旦我们完成遍历![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)值，我们就会打破 while 循环。

最后，我们返回存储的答案。

朴素方法的复杂性是![好的)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-149cd2c04463438dcac2637a12317ab2_l3.svg)，其中![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)是所需的索引。因此，在最坏的情况下，复杂度为![O(n+m)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-13f1f2eadce5ea106cd499d8a11b8836_l3.svg)，其中![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)是第一个数组![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)的大小， 是第二个数组的大小。

## 4.二分查找法

让我们尝试改进我们天真的方法。

### 4.1. 大概的概念

众所周知，二分查找是一种在排序范围内查找目标值的查找算法。在每一步中，我们将范围分成两半。

接下来，我们检查范围中间的值。根据结果，我们选择范围的左半部分或右半部分继续搜索。我们将在此处使用类似的东西。

在天真的方法中，我们通过![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)从![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)和中获取![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)元素找到了答案![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)。基于相同的想法，我们将执行二分查找以查找从第一个数组中取出的元素的数量![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)。

在每一步中，我们都可以计算从第二个数组中取出元素的![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)数量![j 得到 k - i](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ca1a474988bcce95acf65ff408abaefb_l3.svg)。之后，我们可以检查这个![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)和的组合是否![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)有效。根据结果，我们可以选择从 中获取更多或更少的元素![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)。

### 4.2. 执行

看一下二进制搜索方法：

```

```

首先，我们将选择要搜索的范围。显然，右侧应等于![否](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7354bae77b50b7d1faed3e8ea7a3511a_l3.svg)。

但是，选择左侧时，![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)必须考虑数组的大小。因此，左侧不应小于![公里](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9d2e5d8b3499fe0bbae238a09ba67b27_l3.svg)，也不应小于零。

现在，我们可以使用二分查找算法找到最小的![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)，使得 range from 中的最大值大于 range from中的![[0..我]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c7ff26c3afbff324f5821772ab10896d_l3.svg)最大值![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)![[0..ki]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0066a450269301a983fcd7e2ad1486c7_l3.svg)![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)。正如我们所见，我们正在![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)从![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)和中获取![我知道](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e7c995854907934f6b92cdf061eceb6d_l3.svg)元素![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)。因此，总的来说，我们采用![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)元素。

在每一步中，我们都会比较我们拥有的两个值。如果我们选择不从 中获取任何元素![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)，或者 的值![A[i-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4edb29d9a23d7e505ed667a819723ca2_l3.svg)大于![B[j-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d0392f66432b60adac6e9b0d8c6a46f2_l3.svg)，那么我们知道我们达到了有效值![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)并存储它。我们还应该尝试找到更小的值![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)。因此，我们使搜索范围等于左边的范围。

否则，这意味着我们应该继续![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)前进。在这种情况下，我们使搜索范围等于正确的范围。

最后，我们有两种情况。要么![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)等于零，这意味着我们不应该从![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg). 结果，答案将等于![B[j-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d0392f66432b60adac6e9b0d8c6a46f2_l3.svg)。

另一方面，如果我们应该从 中取出一些元素![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)，那么第 k个最小值是![B[j]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-69dc117c3f8d86b2a58de95f872e1522_l3.svg)或![A[i-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4edb29d9a23d7e505ed667a819723ca2_l3.svg)。原因是我们知道![A[i-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4edb29d9a23d7e505ed667a819723ca2_l3.svg)t 比 range 中的所有元素都大![B[0..j-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d37047f86e8cc3deee44ee3b1c9fd248_l3.svg)。

因此，答案要么是![A[i-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4edb29d9a23d7e505ed667a819723ca2_l3.svg)它本身，要么![B[j]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-69dc117c3f8d86b2a58de95f872e1522_l3.svg)是 ，以最小者为准。

二分搜索方法的复杂度为![O(日志(n))](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b15503718d0ccc0cf4cf9137e087efc0_l3.svg)，其中![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)第一个数组的大小为 。

### 4.3. 例子

让我们以第 2 部分中的相同示例为例，其中我们有两个数组![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)和![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)，并且![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)是 6。看一下该示例中的第一步：

![二进制搜索示例](https://www.baeldung.com/wp-content/uploads/sites/4/2020/08/Binary_Search_Example_1-1024x532-1.png)

一开始，搜索范围是![[0, 4]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8fd299814e4f95f4b16515153d0e48c9_l3.svg). 因此，![中](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-06247b54f0838f3d6a6aecc11b86041d_l3.svg)等于 2。因此，![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)等于 2 并![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)等于 4。

我们比较 和 的![A[i-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4edb29d9a23d7e505ed667a819723ca2_l3.svg)值![B[j-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d0392f66432b60adac6e9b0d8c6a46f2_l3.svg)。由于![A[i-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4edb29d9a23d7e505ed667a819723ca2_l3.svg)不大于![B[j-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d0392f66432b60adac6e9b0d8c6a46f2_l3.svg)，我们尝试在数组的右侧范围内搜索![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)，而不更新 的值![最好](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7a9abe06b7bc59d13e279d2990a70384_l3.svg)。

请注意，![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)和![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)表示要从每个数组中获取的元素数。由于索引从零开始，我们![A[i-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4edb29d9a23d7e505ed667a819723ca2_l3.svg)与![B[j-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d0392f66432b60adac6e9b0d8c6a46f2_l3.svg). 另请注意，红色单元格表示在每个步骤中比较的值。

让我们进入下一步：

![二进制搜索示例 2](https://www.baeldung.com/wp-content/uploads/sites/4/2020/08/Binary_Search_Example_2-1024x532-1.png)

在下一步中，搜索范围变为![[3, 4]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4d9485055382aea003d4da7748694484_l3.svg)。由此我们得出总结，![中](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-06247b54f0838f3d6a6aecc11b86041d_l3.svg)等于 3，![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)等于 3，![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)等于 3。

我们注意到![A[i-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4edb29d9a23d7e505ed667a819723ca2_l3.svg)仍然不大于![B[j-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d0392f66432b60adac6e9b0d8c6a46f2_l3.svg)。因此，我们继续搜索数组 的正确范围![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)，而不更新 的值![最好](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7a9abe06b7bc59d13e279d2990a70384_l3.svg)。

这将我们带到最后一步：

![二进制搜索示例 3](https://www.baeldung.com/wp-content/uploads/sites/4/2020/08/Binary_Search_Example_3-1024x532-1.png)

最后，搜索范围现在是![[4, 4]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c9834bc8c38a509ecd8432962592e0b9_l3.svg)。这意味着![中](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-06247b54f0838f3d6a6aecc11b86041d_l3.svg)等于 4，![一世](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-31318c5dcb226c69e0818e5f7d2422b5_l3.svg)等于 4 和![j](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b09880662630fc49b25d42badb906d51_l3.svg)等于 2。在这种情况下，我们注意到![A[i-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4edb29d9a23d7e505ed667a819723ca2_l3.svg)实际上大于![B[j-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d0392f66432b60adac6e9b0d8c6a46f2_l3.svg)。结果，我们更新了 的值![最好](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7a9abe06b7bc59d13e279d2990a70384_l3.svg)。

搜索范围变为![[4, 3]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7977a019696c70732fb8188fccf16369_l3.svg)，无效。因此，该算法不再执行任何步骤。

现在二分查找操作已经完成，答案是 和 之间的![A[i-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4edb29d9a23d7e505ed667a819723ca2_l3.svg)最小值![B[j]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-69dc117c3f8d86b2a58de95f872e1522_l3.svg)。在本例中，![一个[3]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9d02c98bf9801af671ede0f218984266_l3.svg)是 9 和![B[2]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-adb62cffc990ee6495939fb97307921f_l3.svg)7。因此，算法返回 7 作为问题的答案。

## 5.比较

在考虑简单性时，朴素的方法既易于实现又易于理解。但是，当考虑时间复杂度时，二分查找方法通常具有较低的复杂度。

然而，在![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)小于的特殊情况下![日志(n)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7a0807bf335c3dffe14c173f9a83a66c_l3.svg)，使用朴素方法应该会给我们带来更好的复杂性。

## 六，总结

在本教程中，我们解释了计算两个排序数组的并集中第K个最小元素的问题。首先，我们提出了朴素的方法。

其次，我们讨论了二进制搜索方法的理论思想和实现。

最后，我们对两种方法进行了总结比较，并说明了何时使用每种方法。