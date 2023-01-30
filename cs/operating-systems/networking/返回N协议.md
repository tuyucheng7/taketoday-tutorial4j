## 1. 概述

Go-Back-N 和 Selective Repeat 协议是基本的滑动窗口协议，可帮助我们更好地理解计算机网络传输层中可靠数据传输背后的关键思想。

在本教程中，我们将描述 Go-Back-N 协议的工作原理。![否](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7354bae77b50b7d1faed3e8ea7a3511a_l3.svg)此外，我们将讨论窗口大小和序列号空间大小之间的关系，![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)以及 的选择如何![否](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7354bae77b50b7d1faed3e8ea7a3511a_l3.svg)影响算法的性能。

## 2. 返回 N

滑动窗口(流水线)协议通过不要求发送方在发送另一个帧之前等待确认来实现网络带宽的利用。

在 Go-Back-N 中，发送方控制数据包流， 这意味着我们有一个简单的虚拟接收方。因此，我们首先讨论服务器如何处理数据包。

### 2.1. 寄件人

发送方有一系列要发送的帧。我们假设窗口大小为![否](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7354bae77b50b7d1faed3e8ea7a3511a_l3.svg). 此外，存在两个指针来跟踪发送基址 ( ![发送_基地](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f5bfc40d9e5fe0bdd21a4d2013e7d72c_l3.svg)) 和下一个要发送的数据包 ( ![下一个序号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a278a1428adfadfebd047a55375a00d1_l3.svg))。

![返回N-4](https://www.baeldung.com/wp-content/uploads/sites/4/2020/06/GoBackN-4.png)

首先，发送方首先发送第一帧。最初，![发送_base = 0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-49bd8f1bebf2857bee5596160e533856_l3.svg)和![下一个序号 = 0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-730ad5dc8f92c7e530ec10f84b457622_l3.svg)。虽然有更多的数据包要发送，但![下一个序号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a278a1428adfadfebd047a55375a00d1_l3.svg)比; 小![发送_base + N](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-aa86c593e82b572957a0e12f3b6aee4f_l3.svg)；发送方发送![下一个序号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a278a1428adfadfebd047a55375a00d1_l3.svg)指针指向的数据包，然后递增![下一个序号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a278a1428adfadfebd047a55375a00d1_l3.svg)。

同时，![发送_基地](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f5bfc40d9e5fe0bdd21a4d2013e7d72c_l3.svg)在接收到来自接收方的确认数据包后递增。接收到重复的 ACK 消息不会触发任何机制。

整个发送窗口有一个计时器，它测量数据包在![发送_基地](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f5bfc40d9e5fe0bdd21a4d2013e7d72c_l3.svg). 因此，如果发生超时，发送方将重新启动定时器并重新发送从 开始的发送窗口中的所有![发送_基地](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f5bfc40d9e5fe0bdd21a4d2013e7d72c_l3.svg)数据包。

总而言之，我们可以用以下伪代码表示发送方的算法：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3c7c539e8cc8962b7800d0b77ef71567_l3.svg)

### 2.2. 收件人

Go-Back-N 的接收器实现尽可能简单：

接收方只跟踪预期的序列号以接收下一个：![下一个序号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a278a1428adfadfebd047a55375a00d1_l3.svg)。

没有接收缓冲区；无序的数据包被简单地丢弃。同样，损坏的数据包也会被静默丢弃。

它总是在接收到新数据包(成功或不成功)时发送对接收到的最后一个有序数据包的确认。因此，如果出现问题，它将生成重复的确认消息。

总结一下，接收方算法的伪代码是：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-abb284f48700cba77d029f4501ced96e_l3.svg)

总的来说，基于 ACK、无 NAK 的 Go-Back-N 协议的解释到此结束，它涵盖了与使用序列号、累积确认、校验和和超时/重传进行可靠数据传输相关的问题。

## 3. 累积确认和序号

Go-Back-N 协议采用累积确认的使用。也就是说，接收到帧![textbf{n}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-66674599730467ff37ddd82eb20c5e42_l3.svg)的确认意味着帧![textbf{n-1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d756e02dfd31a5fc152ff9e64a277eb6_l3.svg)、![textbf{n-2}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f46c9c5f0395b19e4d563d0ecc08ca11_l3.svg)等也被确认。我们将此类确认表示为 ACK ![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)。

让![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)表示我们用来标记帧的最大可能序列号。再次假设我们的窗口大小为![否](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7354bae77b50b7d1faed3e8ea7a3511a_l3.svg).

现在，让我们想象一个简单的场景：

1.  发送方发送窗口中的帧，从 0 到![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)
2.  作为响应，它接收 ACK ![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)，标记帧![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg), ![S-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-84b3d36318e58b6986fb31359cd8a846_l3.svg), 等等
3.  然后发送方发送第二组帧，再次从 0 到![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)
4.  之后，发送方收到另一个ACK![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)

在发送方的角度来看，最后一步中的确认代表什么？第二批包是否全部丢失或发送成功？如果![S=N](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-847d593e792f64c3b8a1ac64f3be4e1c_l3.svg)，发送者不可能知道真正的结果。这就是为什么我们必须有严格的不等式 ![textbf{N}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cc5516b68b3290ac21b1e63d938ca42c_l3.svg)< ![textbf{S}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e067610b811e50cbfe483bcc23d87aa0_l3.svg)。

## 4. 利用率和窗口大小

正如我们之前所说，流水线协议是对[停止等待协议](https://en.wikipedia.org/wiki/Stop-and-wait_ARQ)的改进。为了获得更好的网络利用率，我们在给定时间在发送方和接收方之间有多个“传输中”的帧。

![否](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7354bae77b50b7d1faed3e8ea7a3511a_l3.svg)表示 Go-Back-N 中的窗口大小，允许发送方在收到确认之前发送。基本上 if ![N = 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-950996c945878749450a02dcbb523cb8_l3.svg)，我们有一个停止等待的实现。

忽略任何开销的最大可能链路利用率公式为：

 ![[利用率 leq frac{N}{1 + 2 times BD}]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1af6e7bdc44633d5f9f60b63313baa19_l3.svg)

在公式中，![蓝光](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6427bd9a7844f23e7d42292c2d06d031_l3.svg)是带宽延迟乘积，表示在给定时间链路上可以承载多少数据。它的计算方法是数据链路的容量乘以往返延迟时间。

![textbf{N}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cc5516b68b3290ac21b1e63d938ca42c_l3.svg)从理论上讲，我们通过求解上面的等式找到最大可能![textbf{利用率 = 1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4552c9a78178c21faad73a0f2bcb9fb7_l3.svg)。然而，在实践中，我们需要使用更小的![textbf{N}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cc5516b68b3290ac21b1e63d938ca42c_l3.svg)值。

这有两个主要原因。

首先，我们需要考虑接收方能够以何种速率处理到达的数据包以及具有高网络利用率。

如果接收方无法处理数据包，它将丢弃它们。在这种情况下，发送方将一遍又一遍地重传相同的数据包，成功发送的数据包率将不够低。因此，实现高利用率是没有意义的。

防止快速发送者在数据中淹没慢速接收者的概念称为流量控制。流量控制强制使用较小的窗口大小![否](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7354bae77b50b7d1faed3e8ea7a3511a_l3.svg)，这与接收方的处理速度有关。

其次，虽然同样重要的是限制窗口大小![textbf{N}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cc5516b68b3290ac21b1e63d938ca42c_l3.svg)是拥塞控制。网络(部分)中存在太多数据包会导致数据包延迟和数据包丢失，从而降低性能。事实上，发送方应该尊重网络中的每个元素，以便利用整体网络性能。

## 5.总结

综上所述，Go-Back-N 协议对发送方和接收方都起作用，以确保可靠的数据传输。

我们还讨论了累积确认如何强制我们使用![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)大于的值![否](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7354bae77b50b7d1faed3e8ea7a3511a_l3.svg)以及如何选择合理的窗口大小![否](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7354bae77b50b7d1faed3e8ea7a3511a_l3.svg)以实现更好的利用率。