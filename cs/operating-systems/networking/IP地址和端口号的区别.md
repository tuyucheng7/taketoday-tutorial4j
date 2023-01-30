## 1. 概述

在本教程中，我们将讨论网络中的 IP 地址和端口。我们将介绍它们之间的一些主要区别。

## 2.IP地址

IP 的完整形式是互联网协议。连接到 Internet 的计算机可以使用 IP 地址发送和接收来自任何其他网络设备的信息。IP 地址的主要目的是实现通信。

所有连接到互联网的设备都需要一个唯一的 IP 地址。随着联网设备的增加，需要数十亿个IP地址。IPv4[或 IPv6](https://www.baeldung.com/cs/ipv4-vs-ipv6)都满足此要求。

IP 的第一个版本是 IPv ![数学{4}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-37d1fe1e61fadb833cc17cf696d76e41_l3.svg)。它使用位寻址![mathbf{32}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-96ce97945683d86094fad5d678459ea0_l3.svg)方案，允许存储超过10 亿个地址的地址。![mathbf{2^{32}}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9155fef287bfa64868181d814854555e_l3.svg)![4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d4d95642629f734574671d47307d46c3_l3.svg)

在 IPv![数学{4}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-37d1fe1e61fadb833cc17cf696d76e41_l3.svg)寻址之初，由于日常生活中使用的网络设备数量很少，互联网用户非常少。大多数网络都是私有的。当 Internet 爆发时，我们可能会用完具有 IPv![数学{4}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-37d1fe1e61fadb833cc17cf696d76e41_l3.svg)寻址方案的唯一 IP 地址，因为它仅限于![32](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8304649ce156ac9f7daee9a539530a52_l3.svg)位。

IPv6 是 IP 的最新版本。IPv6 是一种 128 位寻址方案，提供了 IPv6 中不存在的许多功能![数学{4}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-37d1fe1e61fadb833cc17cf696d76e41_l3.svg)。我们提供十六进制格式的 IPv6。

这些功能包括内置安全性、范围地址、自动配置、服务质量 (QoS)、新报头格式和更大的地址空间。此后发布的大多数操作系统![2000](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-033bdb1da0064ad7b41a3aee72c85fc2_l3.svg)都直接或间接支持 IPv6。

让我们看一个![数学{4}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-37d1fe1e61fadb833cc17cf696d76e41_l3.svg)IPv 和 IPv6 地址的例子：

![知识产权](https://www.baeldung.com/wp-content/uploads/sites/4/2021/04/IP.png)

IP 地址可分为四类：公共、私有、静态和动态。公共 IP 地址通过 Internet 路由并提供对计算机的远程访问。私有 IP 地址无法通过 Internet 路由，也不允许来自它的流量。这些用作保留 IP 地址，只能在本地网络中使用： 

![公共与私人](https://www.baeldung.com/wp-content/uploads/sites/4/2021/04/Public-vs-Private-1-1024x490.png)

静态 IP 地址由[Internet 服务提供商 (ISP)](https://en.wikipedia.org/wiki/Internet_service_provider)分配给任何网络设备，它们可以是![数学{4}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-37d1fe1e61fadb833cc17cf696d76e41_l3.svg)IPv 或 IPv6。除非我们改变网络架构，否则这种类型的 IP 地址不会改变。动态主机配置协议( [DHCP)](https://www.baeldung.com/cs/dhcp-intro)服务器分配动态 IP 地址，它们可能会发生变化：

![静态与动态](https://www.baeldung.com/wp-content/uploads/sites/4/2021/04/Static-vs-Dynamic.png)

## 3.端口号

在网络中，端口是虚拟通信端点。操作系统中的每个进程都有一个与之关联的特定端口号。端口号使计算机可以轻松识别传入流量并将它们发送到适当的进程。它也是一个有助于通过 Internet 共享信息的对接点。

在[OSI 模型](https://www.baeldung.com/cs/osi-model)中，端口是传输层的一部分。所有网络设备都支持端口。我们在网络中使用不同的端口号来确定操作系统将传入流量转发到的正确协议。例如，所有传入的[超文本传输协议 (HTTP)](https://www.baeldung.com/cs/popular-network-protocols)流量都进入端口![数学{80}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dadb58ada8b7e710be926c0e8a2fda5a_l3.svg)。

端口的主要目的是帮助计算机了解传入流量并发送它们。假设 Sam 想向 Mike 发送一个 MP3 文件。Sam 使用[文件传输协议 (FTP)](https://www.baeldung.com/cs/popular-network-protocols)将 MP3 文件传输给 Mike。现在假设在收到 Sam 的文件后，Mike 的计算机没有识别 MP3 文件并将其发送到电子邮件应用程序。

在这种情况下，电子邮件应用程序将无法打开 MP3 文件。![数学{21}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-eb9b928f569839dec8f8d8eb3e757dea_l3.svg)但是 Sam在传输 MP3 文件时使用分配给 FTP 的端口。因此 Mike 的计算机将使用此处使用的端口号识别文件并将其发送到适当的进程。此外，并行 Mike 可以在他使用端口号的计算机上加载 HTTP 网页![数学{80}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dadb58ada8b7e710be926c0e8a2fda5a_l3.svg)。

在现代计算机中，![mathbf{65,535}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-bf076f0cca0098fa623965b28e91ade5_l3.svg)可能的端口号是可用的。![数学{20}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1ab445033224e735c8c0fe6dbd5032e5_l3.svg)一些流行和常用的端口号是：![数学{21}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-eb9b928f569839dec8f8d8eb3e757dea_l3.svg)FTP、![数学{22}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c2b949b26073cbdbfb80a591994c294c_l3.svg)Secure [Shell (SSH)](https://www.baeldung.com/cs/popular-network-protocols)、[Telnet](https://en.wikipedia.org/wiki/Telnet)![数学{23}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b16fb622beeafd82be27c79d5648d138_l3.svg)、简单邮件[传输协议 (SMTP)](https://en.wikipedia.org/wiki/Simple_Mail_Transfer_Protocol)、域名[系统 (DNS)](https://www.baeldung.com/cs/dns-intro)等。![数学{25}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183473b9d1d6e55d12197cd5d6aa98ed_l3.svg)![数学{53}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-76b072d43242f9acedcd3bb81b14dc09_l3.svg)

让我们看一个例子。此处客户端计算机正尝试使用端口号与服务器建立虚拟连接![数学{23}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b16fb622beeafd82be27c79d5648d138_l3.svg)。Telnet 使用端口号，一种用于通过[TCP/IP](https://www.baeldung.com/cs/popular-network-protocols)![数学{23}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b16fb622beeafd82be27c79d5648d138_l3.svg)连接建立远程连接的协议 。一旦请求到达服务器并且服务器识别端口号，它启动一个 telnet 连接：![数学{23}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b16fb622beeafd82be27c79d5648d138_l3.svg)

![端口 1](https://www.baeldung.com/wp-content/uploads/sites/4/2021/04/port-1.png)

## 4. IP 地址与端口号

我们现在了解了 IP 地址和端口的基础知识。让我们看看 IP 地址和端口之间的区别：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-84d04992d57cd7be3abbb7f72fbf48af_l3.svg)

## 5.总结

在本教程中，我们详细了解了 IP 地址和端口。我们还介绍了 IP 地址和端口号之间的一些显着差异。