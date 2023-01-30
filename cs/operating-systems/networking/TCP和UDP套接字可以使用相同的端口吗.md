## 1. 概述

网络通信协议是 Internet 或计算机网络中设备之间通信的标准。在所有通信协议中，TCP 和 UDP 是两种流行且广泛使用的协议。

在本教程中，我们将讨论 TCP 和 UDP 协议的基本概念。我们将通过示例解释 TCP 和 UDP 套接字是否可以在同一端口上工作。

## 2. TCP和UDP简介

[TCP 和 UDP 是与Internet 协议 (IP)](https://www.baeldung.com/cs/popular-network-protocols)一起工作的两个标准协议。[OSI 模型](https://www.baeldung.com/cs/osi-model)的传输层中使用了 TCP 和 UDP 协议。

TCP代表传输控制协议是一种面向连接的协议。两台计算机在使用 TCP 传输数据包之前通过[握手建立](https://www.baeldung.com/cs/handshakes)[客户端-服务器](https://www.baeldung.com/cs/client-vs-server-terminology)连接。此外，TCP 将标头信息放在数据包中，以便可以在目标计算机上重新组装数据包。

当数据的可靠性至关重要时，我们使用 TCP。它还确保发送的数据是有序的。此外，如果接收方未收到发送方传输的数据，TCP 会在特定时间后重新发送数据包。此外，我们在 TCP 中使用[校验和](https://www.baeldung.com/cs/udp-vs-tcp)进行[错误控制](https://en.wikipedia.org/wiki/Error_detection_and_correction)以确保数据完整性。

用户数据报协议 (UDP) 是一种无连接协议。它不确保协议将在接收端接收数据。与 TCP 相比， UDP 具有更低的[延迟](https://www.baeldung.com/cs/tcp-max-packet-size)。它更快，因为 UDP 中没有重传机制。

时间比可靠性更重要的应用程序使用 UDP 协议。由于 UDP 是无状态的，因此需要许多连接(如媒体流)的应用程序使用 UDP。在线游戏、VOIP、流媒体使用 UDP，因为对于这些应用程序来说，时间比一致性数据更重要。

更详细的对比，我们可以参考一篇[关于TCP和UDP的区别](https://www.baeldung.com/cs/udp-vs-tcp)的文章。

## 3.网络端口

应用层程序通过端口与传输层通信。我们为不同的应用程序分配不同的端口，以便它们可以使用它们的端口与网络中任何支持网络的设备连接和交换数据。

例如，端口号是为[HTTP](https://www.baeldung.com/cs/popular-network-protocols)![80](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-561cb81b4985bdd8cabe619255089f6f_l3.svg)保留的。端口有助于[多路复用](https://en.wikipedia.org/wiki/Multiplexing)，以便多个网络连接或多个通信可以在同一设备上进行。

它是一个 16 位数字。因此，它的![2^{16}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2b046eb9d1dd37dd97a8e4e952c67a42_l3.svg)值可以介于![数学{0}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d8fb28da77ac7ddb2b8cfcaf8f053657_l3.svg)和之间![mathsf{65535}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-862534b5f70da90d0a11d7d7af0b7cf2_l3.svg)。![mathsf{65536}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4125551f72f49153105c8009732af9c8_l3.svg)因此，我们可以在计算机网络中拥有总共TCP 端口。同样，网络也可以包含![mathsf{65536}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4125551f72f49153105c8009732af9c8_l3.svg)UDP 端口。当我们将数据从一台设备发送到另一台设备时，它会转到特定的 TCP 或 UDP 端口，具体取决于我们用于通信的协议。

端口就像银行分支机构中的多个柜台。不同的客户可以使用柜台同时执行他们的银行业务。

## 4. TCP和UDP套接字可以使用同一个端口吗？

TCP 和 UDP 端口彼此不相关。TCP 端口由 TCP 堆栈解释，而 UDP 堆栈解释 UDP 端口。端口是多路复用连接的一种方式，以便多个设备可以连接到一个节点。

因此，从技术上讲，更高级别的协议可以使用相同或不同的 TCP 和 UDP 端口号。另一方面，一台计算机可以同时使用相同的 TCP 和 UDP 端口号与两个不同的服务进行通信。

例如，Web 服务器绑定到 TCP 端口![80](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-561cb81b4985bdd8cabe619255089f6f_l3.svg)。应用程序对 TCP 和 UDP 使用相同的端口号，这样更容易记住。尽管连接到同一端口号的两个应用程序可能会执行类似的任务，但 TCP 和 UDP 端口是不同的。他们彼此独立。

让我们再次考虑我们的银行示例。假设 TCP 和 UDP 是两个不同的银行。按照惯例，TCP 和 UDP 银行都决定将柜台![数学{1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-277511c02b56b209330acf2e78fd3290_l3.svg)作为接待处。此外，他们还设置柜台![2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8c267d62c3d7048247917e13baec69a5_l3.svg)和![3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ce2009a45822333037922ccca0872a55_l3.svg)处理现金。

因此，TCP和UDP银行客户都可以到柜台![数学{1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-277511c02b56b209330acf2e78fd3290_l3.svg)办理。此外，如果他们想存入现金，他们可以选择柜台![2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8c267d62c3d7048247917e13baec69a5_l3.svg)和![3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ce2009a45822333037922ccca0872a55_l3.svg)。虽然柜台号码相同，但银行完全不同。此外，两家银行都可以随时为不同的任务重新分配柜台。

因此，简而言之，TCP 和 UDP 是不相关的。更高级别的协议可能会按照约定使用相同的端口号以避免混淆。 

## 5. 一个例子

让我们来谈谈我们对 TCP 和 UDP 套接字使用相同端口号的示例。

[域名系统 (DNS)](https://www.baeldung.com/cs/dns-intro)是一个将域名与 IP 地址匹配的去中心化系统。它还使用域名获取其他与域相关的记录。

让我们看看 DNS 使用的端口号和一些流行的协议：

![位图2](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/bitmap2.png)

DNS 依赖于 UDP 和 TCP。此外，![53](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dba7c058478deecd2daf03adc21aa91d_l3.svg)TCP 和 UDP 都使用端口号来执行与 DNS 相关的任务。最初，DNS 仅与 UDP 端口兼容![53](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dba7c058478deecd2daf03adc21aa91d_l3.svg)。但是，后来添加了 TCP 端口以确保可靠性和连接重用。

DNS 通常在 UDP 上工作，但如果数据包很大，它会通过 TCP 端口连接![数学{53}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5de1e83c982cced74cb5ee9056666087_l3.svg)并执行所需的操作。与只能发送小数据包的 UDP 不同，TCP 对数据没有限制。因此，如果在 UDP 中发送请求时出现故障，通信将切换到 TCP。

[区域传输](https://en.wikipedia.org/wiki/DNS_zone_transfer)是通过将完整的 DNS 记录数据传输到另一台机器来 DNS 服务器的过程。它使用 TCP 端口![53](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dba7c058478deecd2daf03adc21aa91d_l3.svg)。其他DNS相关的操作可以![53](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dba7c058478deecd2daf03adc21aa91d_l3.svg)根据情况同时使用UDP或者TCP端口。

## 六，总结

在本教程中，我们概述了 TCP 和 UDP 套接字使用的端口。此外，我们还通过示例解释了 TCP 和 UDP 套接字是否可以使用相同的端口。