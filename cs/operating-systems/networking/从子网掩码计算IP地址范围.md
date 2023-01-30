## 1. 概述

我们在设计网络的时候，总是需要知道这个网络中需要多少个地址。此外，我们还想知道我们网络中的 IP 地址范围，以便我们可以为网络中的每个设备分配一个地址。

在本教程中，我们将展示一种简单的方法来查找给定子网掩码的 IP 地址范围。

## 二、问题说明

在 IPv4 中，IP 地址由 32 位数字组成。我们用 4 个八位字节(每个八位字节)表示它。通常，当我们提到一个网络时，我们也指一个子网。这个子网可以是一个数字(例如/24)，我们可以把它写成IP地址(例如/255.255.255.0)。从这个子网，我们想找出这个网络中的 IP 地址是什么。

例如，如果我们有一个网络 192.168.0.1/24(或子网 255.255.255.0)，那么我们有 256 个可用地址，从 192.168.0.0 到 192.168.0.255。

## 三、算法思路

一般来说，如果我们将IP地址作为一个32位数字来处理，并且我们有一个子网掩码![X](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e5fbfa0bbbd9f3051cd156a0f1b5e31_l3.svg)，那么子网掩码理论上可以取值在![[0, 32]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-62824376821f333e6a79f8776e80a631_l3.svg). 然后，我们有两件事要估计，给定子网掩码的可能地址数量是多少，以及起始地址是什么。

我们可以通过以下公式轻松获得可能地址的数量![2^{(32 - x)}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-849e268fe18c208df753096f0bdf6238_l3.svg)。这意味着如果我们有一个子网掩码![0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8354ade9c79ec6a7ac658f2c3032c9df_l3.svg)，那么可能的地址是![2^{32}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-aa395d5309b200ec5e25b7cd900585bd_l3.svg)。如果我们的子网掩码为![32](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8304649ce156ac9f7daee9a539530a52_l3.svg)，则可能的地址数为![2^0 = 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1fa131ae599f1773c58d3bf80be76618_l3.svg)，这意味着给定地址是这种情况下唯一可能的地址。再举一个子网掩码的例子![/24](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0f3b3bf44da1b3152698b3d374e970fb_l3.svg)，我们将有![2^{(32 - 24)} = 2^{8} = 256](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-73b09be5bb5cbb3d703bb9c67bcccad1_l3.svg)地址。

然后，我们需要以八位字节的形式解释这个子网掩码。如果我们有一个子网掩码![/24](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0f3b3bf44da1b3152698b3d374e970fb_l3.svg)，我们实际上有一个 32 位数字，其中最左边的 24 位为 1，其余为 0：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2e0fb683550f97dba21ab8d376927498_l3.svg)

从上表中，我们可以看出如何解释子网掩码。所以，如果我们有一个像 192.168.0.10/24 这样的 IP 地址，我们可以将它写在一个表中以显示二进制八位字节与：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3b7e09968d74346db6294ea4f158e3c1_l3.svg)

## 4. 子网起始地址

要在以下子网掩码中找到起始地址，我们只需在 IP 地址和子网掩码之间进行[二进制“与”](https://www.baeldung.com/java-bitwise-vs-logical-and)运算：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8c6a15d5c93d1783c1d21fd2138bbb31_l3.svg)

## 5. 子网最后地址

最后， 我们通过将 子网掩码的按位二进制逆运算应用于第一个 IP 地址的[“或”](https://www.baeldung.com/java-bitwise-operators)运算来计算最后一个 IP 地址：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c50a3edcd1ccb5479b18b3b8e672c5b7_l3.svg)

通过这些简单的步骤，我们知道如何找到可能的 IP 地址数。我们还可以找到第一个和最后一个 IP 地址，范围从 192.168.0.0 到 192.168.0.255。

## 6.例子

如果我们的子网掩码不正好位于八位字节之一，则会发生相同的想法。让我们看看 192.168.0.10/30 的例子。通过遵循相同的想法，我们有![2^2 = 4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-576ba2cfa6c35062bae8082b4b992cc0_l3.svg)可能的 IP 地址：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d7f0d9566bf54cd0299c4e6be0802512_l3.svg)

让我们再举一个例子，其中子网掩码不在最后一个八位位组中，如 10.0.0.0/20。在这种情况下，我们有![2^{(32 - 20)} = 2^{12} = 4096](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1597ed8bfe526c1166c15d4b00532663_l3.svg)可能的 IP 地址：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5ab68c582149dedffff68eb70f9b9dda_l3.svg)

幸运的是，在大多数编程语言中，我们不需要在二进制和十进制之间进行转换来进行二进制运算。因此，我们可以直接对大多数可数数字(如整数或字符)应用二元运算。换句话说，我们可以用 32 位数字或使用![4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d4d95642629f734574671d47307d46c3_l3.svg)每个 8 位的数字(如字符或字节)来表示 IP 地址。

## 7.IPv6

在 IPv6 中，子网的相同思想与 IPv4 类似。但区别在于地址的大小和允许的范围。与 IPv4 中的位相比，IPv6 地址具有位。![128](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-44912fc1243af4ce095a43c39b87cb4f_l3.svg)![32](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8304649ce156ac9f7daee9a539530a52_l3.svg)与IPv4 中的八位字节![8](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e4888e98f77eb93ff65bfecac28d3c5e_l3.svg)相比，地址以段表示。![4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d4d95642629f734574671d47307d46c3_l3.svg)每个段是 4 个十六进制数字，范围从![0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8354ade9c79ec6a7ac658f2c3032c9df_l3.svg)到![FFFF](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-62f1777cea1434900f9e2711a52d8882_l3.svg)与 IPv4 中每八位字节 8 位相比，十进制范围![0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8354ade9c79ec6a7ac658f2c3032c9df_l3.svg)为![255](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6553d5412ad37ed5b124c516491c4988_l3.svg). 与 IPv4 中使用的点 (.) 相比，IPv6 中块之间的分隔是冒号 (:)。

通常，我们可以像在 IPv4 中那样获取可用 IP 地址的数量。所以，如果我们有一个 size 的子网![X](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e5fbfa0bbbd9f3051cd156a0f1b5e31_l3.svg)，那么我们就有![2^{128 - x}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-75ea80bb482dd6edd671fbdba61ddf07_l3.svg)可用的 IP 地址。我们可以通过按位应用并使用大小![128](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-44912fc1243af4ce095a43c39b87cb4f_l3.svg)位掩码来获得其中的第一个，其中第一个![X](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e5fbfa0bbbd9f3051cd156a0f1b5e31_l3.svg)位是 1，其余位是 0。我们可以通过将第一个地址添加到掩码的倒数来获得最后一个地址(![128](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-44912fc1243af4ce095a43c39b87cb4f_l3.svg)第一位![X](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7e5fbfa0bbbd9f3051cd156a0f1b5e31_l3.svg)为零的位，其余为一)。

让我们看一个 IPv6 示例![2001:124A:2000:1000:0000:0000:0000:0000/48](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0e6761a98f66942814c88f01a761219a_l3.svg)。请注意，每个段都写为 4 个十六进制数字，等于 16 个二进制位。地址的数量将是![2^{128 - 48} = 2^{80}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dca28d8ca29617ba79b46719dc07444c_l3.svg)：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6c1bd5cbac5d01764fd4d4313a5af8ab_l3.svg)

请注意，这![FFFF](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-62f1777cea1434900f9e2711a52d8882_l3.svg)等同于![16](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c33a5122bad511e3ec324cd866a0a4dc_l3.svg)ones ![1111111111111111](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e62b87824334402901439fa81fbaddbc_l3.svg)。另一个需要注意的是，IPv6 地址也可以通过删除任何段中的前导零来采用缩写形式。

因此，我们编写示例首地址：

![2001:124A:2000:0:0:0:0:0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4be043f077e0de9077a789ec3f5dd3ce_l3.svg)

另一种缩短形式是用双冒号替换其中一个空段序列(所有零段)。因此，此地址的另一种缩写形式变为：

![2001:124A:2000::](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4416c104c4b5f88c14a66655b2f23433_l3.svg)

请注意，缩短版本不允许超过一次省略零段。所以，如果我们有并解决：

![2001:124A:2000:0000:0000:1234:0000:0000](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c1726f6efd16ff1dad4893098b6a40f7_l3.svg)

![IP地址掩码](https://www.baeldung.com/wp-content/uploads/sites/4/2020/10/IP-adress-mask-300x112.jpg)

缩短的版本将是：

![2001:124A:2000::1234:0:0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-15c6de10054bb707635d7c804661742e_l3.svg)

或者：

![2001:124A:2000:0:0:1234::](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-59a15472d507f73786a4fd647d25789d_l3.svg)

在这个缩短的版本中，我们可以知道删除了多少个零，因为我们知道我们有 128 位分为 8 段。因此，我们知道从 8 中删除了多少段，这些是省略的零。

## 8. 复杂性

查找 IP 地址范围的时间和空间复杂性是![O(1)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-66c97a4dfb9f2e2983629033366d7018_l3.svg)因为我们只需要将子网掩码放入公式中即可查找范围。

## 9.总结

在本文中，我们解释了一种根据子网掩码估计可能的 IP 地址范围的简单方法。