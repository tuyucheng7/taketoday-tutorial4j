## 1. 概述

在本教程中，我们将展示几种查找![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)数组中最小数字的方法。我们还将讨论这些方法的复杂性和相对性能。

所有这些技术都可以适用于寻找![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)数组中最大数字的逆向问题。

## 二、问题描述

给定一个数字数组![boldymbol{a}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a6806b87b12f2146e1ad66147514f8d8_l3.svg)，![boldsymbol{n}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-33807c51cda3a8b6397f854effb8c2d3_l3.svg)我们的目标是找到![boldsymbol{k}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-575c9190fdf843dc68c3624f5237ba62_l3.svg)最小的元素并以非递减顺序返回它们。

例如，如果![一 = [1, 20, -30, 0, 11, 1, 503]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-bad8b198cdbaccc06ada0cdf7529239c_l3.svg)和![k=3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-94884c30f0e7b889ad62b8fb90ef8151_l3.svg)，解决方案是数组![[-30, 0, 1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ea55e87e1ad1c41c11615ccdb6624e28_l3.svg)，因为它们是 中最小的三个数字![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0e55b0b3943237ccfc96979505679274_l3.svg)。

当 时![k=1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fa9a164b15cd4025e77950a18cb40e4f_l3.svg)，问题简化为[寻找数组的最小值](https://www.baeldung.com/java-array-min-max)。

## 3. 朴素的解决方案

我们可以遍历数组![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)times 并在每次传递中找到并删除最小的数字。该解决方案的时间复杂度为![O(ntimes k)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-08e18599081fe9c6a402772116336c73_l3.svg). 它效率低下，因为它重复遍历整个数组。

另一个简单的解决方案是对整个数组![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0e55b0b3943237ccfc96979505679274_l3.svg)进行非递减排序，然后取![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)排序数组的第一个元素。这种方法的问题在于，![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)即使我们只需要![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)最小的元素，它也会对所有元素进行排序。此方法的复杂性取决于排序算法，但只要![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)远小于时效率就会很低![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)。![100 000](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9ec9d8b492475063ad4b2053df08a094_l3.svg)例如，如果我们只需要五个最小的数字，那么对整个数字数组进行排序就没有意义。

## 4. 基于最小堆的解决方案

[我们可以用最小堆](https://www.baeldung.com/cs/heap-vs-binary-search-tree)做得更好。被认为是二叉树，最小堆具有每个节点小于或等于其子节点的属性。因此，根节点是最小堆的最小值。

一旦我们从数组中构建了一个最小堆，我们就可以弹出它的根以获得其中的最小元素。然后，我们处理堆的其余部分以保留堆属性并弹出新根以获得第二小的数字。

重复过程![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)时间给了我们想要的输出：

 

![CS1](https://www.baeldung.com/wp-content/uploads/sites/4/2021/08/cs1.jpeg)

使用[Floyd 算法](https://en.wikipedia.org/wiki/Heapsort#Variations)，我们可以及时构建堆![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg)。爆根堆剩下的需要![O(log n)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2d57cfd455039a8d5f3413d90de473e0_l3.svg)时间，所以![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)这些步骤总共可以及时完成![O(k log n)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a255575c88aed0bdcd3c0d1ce98ae2ed_l3.svg)。因此，总复杂度为：

 ![[O(n+k log n)]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-af536bae3e8520d395959b2fb8c9b24d_l3.svg)

如果![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)是 的线性函数![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)(例如，![k=frac{n}{10}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c9d52805082e257df9886aedbb805a2c_l3.svg)当我们寻找底部时![10%](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e8d38d7d33e7afaa90bc4f46c8d92138_l3.svg))，那么复杂度变为

 ![[O(nlog n)]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-911d3802b0e32014f43ece6902fbb799_l3.svg)

这是[仅使用二进制比较的排序算法复杂度的下限](http://www.quretec.com/u/vilo/edu/2003-04/ATABI_2004k/BigOh/sort_lower.html)。

## 5. 基于最大堆的解决方案

我们可以使用最大堆来解决这个问题。最大堆就像最小堆，唯一的区别是每个最大堆的节点都大于或等于它的子节点。因此，最大堆的根包含它的最大值。

一开始，我们将![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)未排序数组的第一个元素放入最大堆中。然后，我们遍历剩余的![所谓的](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6a42ca1958f559355dfe9fdc73d7857c_l3.svg)元素并将它们与根进行比较。

如果当前元素 ( ![boldsymbol{a[i]}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0c7c3aa27609d5acf6969a5d76068630_l3.svg)) 低于根，那么我们知道至少有![boldsymbol{k}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-575c9190fdf843dc68c3624f5237ba62_l3.svg)元素不大于根(![k-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7dfca2445cd362ac42fb9032c9cf2367_l3.svg)当前在我们的堆中和我们刚刚比较的那个)，所以根不能在![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)最小的数字中。因此，我们将根替换为![[我]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-42e34b2b8788502423ed7c709a1494a6_l3.svg)，处理堆以保留其属性，然后移动到数组中的下一个元素。

否则，如果![boldsymbol{a[i}</strong><strong>]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9d7b2e4a9361d9a3500480b159c77736_l3.svg)不低于根，我们知道它至少大于或等于![boldsymbol{k}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-575c9190fdf843dc68c3624f5237ba62_l3.svg)数组中的其他数字，因此它不可能是![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)最小的数字之一，我们可以丢弃它：

 

 

![CS2](https://www.baeldung.com/wp-content/uploads/sites/4/2021/08/cs2.jpeg)

使用[弗洛伊德算法](https://en.wikipedia.org/wiki/Heapsort#Variations)，我们可以及时构建堆![好的)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-149cd2c04463438dcac2637a12317ab2_l3.svg)。在最坏的情况下，每个![所谓的](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6a42ca1958f559355dfe9fdc73d7857c_l3.svg)最初离开堆的元素都会低于根，因此我们将执行![O(logk)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d22e6528a1b9770f2c0dbd40045a89bc_l3.svg)根替换操作![所谓的](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6a42ca1958f559355dfe9fdc73d7857c_l3.svg)次数。因此，最坏情况下的复杂度是

 ![[O(k+(nk)log k)=O(n log k)]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c044b588738c05cbba4839ca3aeb7b87_l3.svg)

如果![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)是 的线性函数![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)，则复杂度为

 ![[O(nlog n)]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-911d3802b0e32014f43ece6902fbb799_l3.svg)

## 6.快速选择排序

这种方法结合了[QuickSelect](https://www.baeldung.com/java-kth-largest-element)和[QuickSort](https://www.baeldung.com/cs/algorithm-quicksort)。

首先，我们使用 QuickSelect 找到第![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)- 个最小的数字。它将把它放在![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)输入数组中的第 - 个位置，![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0e55b0b3943237ccfc96979505679274_l3.svg)并将![k-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7dfca2445cd362ac42fb9032c9cf2367_l3.svg)较小的数字放在![1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-69a7c7fb1023d315f416440bca10d849_l3.svg)通过的位置上，![k-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7dfca2445cd362ac42fb9032c9cf2367_l3.svg)但不会对它们进行排序。

为了从最小到最大对这些数字进行排序，我们将 QuickSort 应用于![a[1], a[2], ldots, a[k-1]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f67e0f27a4ed63cd660e0fe1c7401d8f_l3.svg). 我们不需要![[s]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c83d00b4f0a4bcc5e2fabb31299ec9a8_l3.svg)使用 QuickSort 进行处理，因为我们已经知道它是![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)第 -th 最小的：

 

 

![CS3](https://www.baeldung.com/wp-content/uploads/sites/4/2021/08/cs3.jpeg)

尽管该方法的最坏情况复杂度是

 ![[O(n^2)]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-eec50530648336ca50b70012b4e93832_l3.svg)

它的平均复杂度是

 ![[O(n+klog k)]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e6081b1e8e35a69e2fb426e48dc0e18b_l3.svg)

这很好。但是，如果![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)是 的线性函数![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)，则平均复杂度为![O(nlog n)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d3d914067f0f05c97175159d8581ab81_l3.svg)。

QuickSelSort 的一个显着优势是它使用两种算法，这两种算法在许多库中都可用，并且针对性能进行了调整，因此我们不必从头开始实现它。

## 7.部分快速排序

部分快速排序是通常快速排序的修改。不同之处在于，前者仅对可证明包含![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)最小元素的数组部分进行递归调用。

在通常的 QuickSort 中，我们得到子数组![a[i:j]=a[i,2,ldots,j]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0281994a6ff4a154f4b86faaa52a162c_l3.svg)，选择一个主元并划分![一个[我：j]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-95c12dcca8216adf91296ffafce72d8e_l3.svg)为两个较小的子数组![[我:(p-1)]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-be39dc04f63297f6532dd771c2d8d619_l3.svg)和![a[(p+1):j]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-326603faa9895e9bc93ec709efd49525_l3.svg)。由于 QuickSort 的目标是对整个数组进行排序，因此我们必须进行两次递归调用：一次用于左侧子数组，另一次用于右侧子数组。

相反，部分快速排序并不总是对正确的子数组进行排序。

如果![p + 1 > k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed513eb45d4b44efbe0614293c88bba6_l3.svg)，则部分快速排序完全忽略右子数组，因为其中没有![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)最小的数字。我们之后的所有数字都在左子数组中，因此我们只对 进行递归调用![[我:(p-1)]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-be39dc04f63297f6532dd771c2d8d619_l3.svg)。

如果![p + 1 leq k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-65f71d52d8c8c328e93f0c8139b0fc30_l3.svg)，那么一些寻找的数字在 中![一个[(p + 1)：我]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5bfd699d89d117ac448f9a1654c53fb3_l3.svg)，所以我们也必须对其进行递归调用：

 

 

![CS4](https://www.baeldung.com/wp-content/uploads/sites/4/2021/08/cs4.jpeg) ![CS5](https://www.baeldung.com/wp-content/uploads/sites/4/2021/08/cs5.jpeg)

```

```

如果我们随机选择主元，那么 Partial QuickSort 的最坏情况复杂度为![O(n^2)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-894959b13d80157796705e7eafb4d243_l3.svg)，而预期复杂度为![O(n+klog k)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c14890d38c8f437df1541450b4a9980a_l3.svg)，就像 QuickSelSort 的情况一样。但是，部分 QuickSort 平均比较次数较少。

## 8. 使用中位数的中位数进行部分快速排序

有一种方法可以将 Partial QuickSort 的最坏情况下的时间复杂度降低到![O(n+klog k)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c14890d38c8f437df1541450b4a9980a_l3.svg). 我们可以通过使用称为中位数中位数的算法选择枢轴元素来实现这一点。

### 8.1. 中位数的中位数

如果我们在递归调用之前将数组大小减小一个常数因子，则 QuickSort 和 Partial QuickSelect 算法显示出最佳的渐近性能，因为要处理的数组大小将随着递归深度呈指数下降。

[中位数中位数](https://en.wikipedia.org/wiki/Median_of_medians)(MoM) 是一种枢轴选择算法，正是这样做的。

MoM 返回输入数组底部![30%](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2bb3d409936612dafc5318ffc78487d2_l3.svg)和顶部之间的元素。因此，返回的主元以和![30%](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2bb3d409936612dafc5318ffc78487d2_l3.svg)之间的比率拆分输入数组。在最坏的情况下，我们将输入的大小减少了一个因子。由于这是已知常数的减少，因此输入大小呈指数下降。![30%:70%](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6365b995fe32d048196a9bd71578a67e_l3.svg)![70%:30%](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-02bf8511bed8edaaac8537a0dd8f9991_l3.svg)![70%](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d89a879b4f0f1648d933a5fb5c5c4a7a_l3.svg)

### 8.2. 复杂性分析

MoM![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg)及时运行。由于 Partial QuickSort![在)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f8d599809b2f7987726c648086c1981d_l3.svg)在每次调用中进行比较，MoM 引起的开销不会改变调用的渐近复杂性。然而，最坏情况的时间复杂度降低到

 ![[O(n+klog k)]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e6081b1e8e35a69e2fb426e48dc0e18b_l3.svg)

这是部分快速排序的通用版本的平均情况复杂度。

## 9.混合部分快速排序

尽管使用 MoM 的部分快速排序具有![O(n+klog k)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c14890d38c8f437df1541450b4a9980a_l3.svg)最坏情况下的时间复杂度，但在实践中，它在大多数数组上的运行速度比部分快速排序的渐进劣质版本慢。MoM 提高了最坏情况的复杂性，但由于额外的开销，使算法在大多数输入上运行得更慢。

有一种方法可以保持实际效率并同时降低最坏情况下的复杂性。我们从[IntroSelect](https://en.wikipedia.org/wiki/Introselect)中汲取灵感。

这个想法是从通常的 Partial QuickSort 开始，它随机选择主元。我们假设手头的输入不会导致算法的最坏情况行为，并且使用随机选择是安全的。很多时间，我们会是对的。平均案例模型将适用于大多数输入数组。但是，如果我们注意到随机选择会导致低效的分区，我们会求助于 MoM 以避免![O(n^2)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-894959b13d80157796705e7eafb4d243_l3.svg)最坏的情况，![O(n+klog k)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c14890d38c8f437df1541450b4a9980a_l3.svg)而改为使用它。

我们可以通过两个简单的规则切换到 MoM：

-   我们跟踪所有递归调用中的输入数组大小。如果任何![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)连续的调用都没有将输入的长度减半，其中![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)是一个预先设置的常量，那么我们切换到 MoM。
-   我们在递归调用中跟踪输入数组大小的总和。如果我们注意到调用中的总和超过![mn 次](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6ac8beb0cec7fe9c4d02689e67f21b96_l3.svg)，其中![米>1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b46d3cfcda25261e887af6219e771129_l3.svg)，那么我们将切换到 MoM。

这些规则限制递归深度以![Theta (log n)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b61a2cfef7d3a94b6742e044cc8e8899_l3.svg)确保最坏情况下的复杂度为![O(n+klog k)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c14890d38c8f437df1541450b4a9980a_l3.svg). 此外，他们将使算法在大多数输入上保持高效。让我们看看第二种策略是如何工作的：

```

```

## 10. 复杂度比较

```

```

我们看到所有基于 QuickSort 和 QuickSelect 的方法都具有相同的平均案例时间复杂度![O(n+klog k)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c14890d38c8f437df1541450b4a9980a_l3.svg)。但是，QuickSelSort 和 Partial QuickSort 具有![O(n^2)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-894959b13d80157796705e7eafb4d243_l3.svg)最坏情况下的时间复杂度。虽然渐近等价，但后者在实践中更有效。

如果我们使用 MoM 在 Partial QuickSort 中选择主元，我们可以将最坏情况的复杂度降低到![O(n+klog k)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c14890d38c8f437df1541450b4a9980a_l3.svg)，但由于 MoM 导致的开销，最终会得到一个低效的算法。Hybrid Partial QuickSort 与带 MoM 的 Partial QuickSort 具有相同的渐近复杂性。但是，它在实践中更快，因为它仅在随机选择导致低效分区时才使用 MoM。

堆提供了有趣的替代方案。然而，它们在实践中的表现取决于![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)比 大多少![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)。

如果![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)是 的线性函数，则在平均情况下![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)，所有方法都变为对数线性。![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)具有 MoM 的部分快速排序、混合部分快速排序和基于堆的解决方案也成为![O(nlog n)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d3d914067f0f05c97175159d8581ab81_l3.svg)最坏的情况。

## 11. 其他方法

除了 QuickSort，我们还可以采用其他[排序算法](https://www.baeldung.com/cs/choose-sorting-algorithm)，例如[MergeSort](https://www.baeldung.com/java-merge-sort)。

我们还可以将我们的解决方案基于[Floyd–Rivest 选择算法](https://en.wikipedia.org/wiki/Floyd–Rivest_algorithm)。它![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)像 QuickSelect 一样查找数组中的第 - 个最小元素，但平均速度更快。

## 12.总结

在本教程中，我们展示了几种查找![k](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d42bc2203d6f76ad01b27ac9acc0bee1_l3.svg)数组中最小数字的方法。我们的大部分解决方案都基于 QuickSort，但也提出了两种受堆启发的方法，并提到了我们可以采用以解决问题的其他算法。