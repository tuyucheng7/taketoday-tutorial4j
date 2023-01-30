## 1. 概述

开放系统互连 ( [OSI](https://www.baeldung.com/cs/osi-model) ) 模型是一种标准化框架，用于描述网络上计算机之间的通信。它以其分层结构使不同网络设备能够在不同的网络条件下进行通信。

结果，层上传输的数据结构是不一样的。在本教程中，我们将重点关注数据包和帧之间的区别。它们都是在不同的 OSI 模型层上使用的[协议数据传输单元。](https://www.baeldung.com/cs/networking-packet-fragment-frame-datagram-segment)

让我们首先仔细看看数据包和帧代表什么。然后，我们将研究它们之间的差异。

## 2.数据包

数据包是通过网络层传输的数据片段。

网络层封装来自[传输层](https://www.baeldung.com/cs/osi-transport-vs-networking-layer#introduction-to-transport-layer)的数据段。它的主要目的是将数据包转发到互连的异构网络中的路由器。

网络数据包源自逻辑地址并传输到另一个逻辑地址。因此，它的报头包含源地址和目的地址。

在 Internet 上，TCP/IP 模型的 Internet 协议 (IP) 类似于 OSI 模型的网络层。在网络层，路由器根据IP 数据包[标头上的](https://www.baeldung.com/cs/ipv4-datagram#ipv4-datagram)[IP 地址](https://www.baeldung.com/cs/ipv4-vs-ipv6#ip-address)将数据包转发到目的地：

![数据报](https://www.baeldung.com/wp-content/uploads/sites/4/2021/10/datagram.png)

## 3. 相框

帧是数据链路层上传输的数据单位。

数据链路层报头和有效载荷数据一起构成一个帧。帧的数据部分是来自网络层的数据包。换句话说，一个帧封装了一个数据包。

它的源地址和目标地址是物理介质访问控制 (MAC) 地址。因此，标头部分包含这些物理地址。

由于数据链路层以比特的形式接收来自物理层的数据，其主要目的是保证无差错的数据传输。因此，它实施了额外的措施来尽早检测到任何错误。因此，与其他层中的数据不同，帧包含一个尾部，其中包含用于纠错的帧校验序列 (FCS)。此外，标志标记帧的开始和结束。

[Internet 的以太网协议](https://www.baeldung.com/cs/popular-network-protocols#ethernet)与 OSI 模型的数据链路层协议密切相关。这就是为什么典型的以太网帧头除了以太网协议版本相关字段之外还包含源和目标 MAC 地址的原因。此外，纠错字节跟在数据之后：

![以太网](https://www.baeldung.com/wp-content/uploads/sites/4/2021/10/Ethernet.png)

## 4. 数据包和帧的区别

让我们根据到目前为止所学的知识总结一下数据包和帧之间的根本区别：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cc7c25e5c0237005beedcafca8a7927d_l3.svg)

它们参与的 OSI 层是主要区别。帧是数据链路层的数据单位，而数据包是网络层的传输单位。

因此，当我们将数据包链接到逻辑地址时，我们将帧关联到物理地址。

简而言之，帧和数据包的不同之处在于它们封装数据有效负载的方式。 

## 5.总结

在本文中，我们研究了 OSI 模型中的数据包和帧。

首先，我们了解了数据包，它通过网络层传输数据。然后，我们回顾了帧，数据链路层的数据单元。最后，我们比较了它们的不同之处。