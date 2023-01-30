## 1. 概述

传输控制协议 ( [TCP](https://www.baeldung.com/cs/popular-network-protocols) ) 有助于通过连接的网络发送文件或消息。每个文件在发送方网络被分成数据包。数据包在到达接收网络时被合并。 TCP 数据包的最大大小为 64K ([65535 字节](https://www.quora.com/What-is-a-the-maximum-packet-size-of-a-TCP-packet))。 通常，数据包大小受到网络资源 的最大传输单元 ( [MTU ) 的限制。](https://www.baeldung.com/cs/networking-packet-fragment-frame-datagram-segment)MTU 是由网络中的硬件设置的数据传输限制的最大大小。数据包大小不应超过 MTU。 在本教程中，我们将尝试解释每个术语背后的概念并探索它的各个方面。

## 2.TCP连接

[TCP 连接使用 TCP，它是](https://www.baeldung.com/cs/tcp-protocol-syn-ack)[Internet 协议族](https://www.baeldung.com/cs/protocols-ping)中的核心[协议](https://www.baeldung.com/cs/popular-network-protocols)之一，为数字通信提供了可靠的协议。TCP 从服务器、路由器、交换机等网络资源接收消息，将它们拆分成数据包，最后将它们转发到目标网络资源。几乎所有涉及互联网的连接都使用 TCP 连接。我们将在后续部分中详细解释数据包的工作原理。我们以邮件通信为例来理解TCP通信：从上图我们可以看出一封邮件经过了 [OSI模型](https://www.baeldung.com/cs/osi-model)的七层处理。 源服务器的每一层都与目标服务器的相应层通信。![模型轴](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/Email-communication-1.png) 在应用层，我们发送邮件时，邮件客户端使用SMTP协议与邮件服务器进行通信。表示层将我们的邮件转换为 ASCII 和图像。会话层建立并维护与目标服务器的连接。 [传输层](https://www.baeldung.com/cs/osi-transport-vs-networking-layer)将 消息拆分为多个数据包，并添加源服务器和目标服务器的端口信息。 [网络层通过添加相应的IP地址来](https://www.baeldung.com/cs/non-routable-ip-address)定义数据包的路由路径 。有趣的是， 即使每个数据包都为同一个目标而行，它们所使用的行进路线也可能不同。 数据链路层准备要通过以太网传输的数据包，物理层最终 通过[LAN](https://www.baeldung.com/cs/routers-vs-switches-vs-access-points) 电缆、 WiFi[或](https://www.baeldung.com/cs/ap-vs-station-vs-bridge-vs-router) 宽带 [等物理连接](https://en.wikipedia.org/wiki/Broadband)[传输](https://www.baeldung.com/cs/packet-transmission-time) 帧 。下图解释了上述过程：当数据包到达目标网络的物理层(上图中的 Host2)时，目标电子邮件服务器的每一层都会处理数据包以检索数据并将我们的电子邮件显示在目标收件箱中. ![osi-模型示例](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/PictureFrame-1-1.png)

## 3. 帧&数据包 

[计算机网络](https://www.baeldung.com/cs/networking-packet-fragment-frame-datagram-segment)中的数字数据交换 使用 [帧和数据包](https://www.baeldung.com/cs/osi-packets-vs-frames)。帧和数据包之间的主要区别在于，帧是比特的串行集合，而数据包是封装在帧中的分段数据。 数据包是 OSI 模型中网络层中的单个数据单元。 每个数据包通常包括一个 [报头和一个有效载荷](https://www.baeldung.com/cs/messages-payload-header-overhead)。标头具有源和目标网络设备的端口和 IP 地址。数据或消息内容是有效载荷。  例如，当我们传输一个图像文件时，文件被分成几个数据包。数据包包含单独传输的图像部分。接收网络设备重新加入数据包以重建图像文件。最后，它们组合起来检索相同的图像文件： ![数据包示例](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/Packet-communication-1.png)数据包包含将它们定向到目标地址的信息以及检查传输错误和数据完整性的信息。有效地拆分数据有助于网络管理不同的网络参数，例如带宽、路由和设备连接。

## 4. 为什么数据包大小很重要？

数据包 [丢失](https://www.ir.com/guides/what-is-network-packet-loss#:~:text=Packet loss describes packets of,we discuss in detail below.) 率取决于数据包大小。l arge 是数据包的大小，丢包的概率越大。数据包大小对通信网络参数(如数据包丢失率和吞吐量)产生不同的影响。 我们必须将数据包大小保持在 MTU 以下以获得更好的 TCP 连接性能。[最大数据包大小](https://www.lifewire.com/definition-of-mtu-817948) 应在 1500 字节(宽带)和 576 字节(拨号 [)](https://www.baeldung.com/cs/handshakes)之间。[路由器](https://www.baeldung.com/cs/routers-vs-switches-vs-access-points#introduction) 可以从接口 [配置信息](https://www.baeldung.com/cs/routing-table-entry)中得到目标连接的 MTU 。 

### 4.1. 好处

基于数据包的 TCP 通信的优点如下：

-   -   有效利用网络带宽。

-   根据通信标准使用可变数据包大小。 
-   每个数据包根据最佳网络路由独立传输。
-   不需要 [专用通道](https://www.baeldung.com/cs/ssl-vs-ssh) 来路由数据包，而是使用连接到目标网络的任何可用网络路径。
-   使用操作系统的功能配置数据包大小。
-   较小的数据包大小可以提供更好的网络延迟

### 4.2. 缺点

基于数据包的 TCP 连接的缺点如下：

-   配置大于 MTU 的数据包大小会导致[jabbering](https://networkencyclopedia.com/jabber/)。
-   小数据包可能会导致[传输速度变慢](https://www.baeldung.com/cs/tcp-flow-control-vs-congestion-control)。
-   当最大数据包大小超过网络的物理 MTU 时，它会影响性能。 

## 5. 不同 MTU 的数据包分片 

假设源设备使用 TCP 通信，通过网络发送 IP 数据包。 数据包大小必须小于目标和中间网络的 MTU。 这种限制是由网络数据链路层和硬件 MTU 造成的。那么如果数据包大于中间网络或目标设备的 MTU 会怎样呢？答案是将数据包进一步分成几部分，这个过程称为分段。 各个部分是片段，然后在目标网络重新加入以检索完整的数据包。但是，根据[IP 协议](https://www.baeldung.com/cs/popular-network-protocols#internet-protocol-ip)的类型，例如[IPv4](https://www.baeldung.com/cs/ipv4-subnets)和[IPv6](https://www.baeldung.com/cs/ipv4-vs-ipv6) ，可能会出现以下情况：

-   IPv4：当 Don't Fragment (DF) 标志处于活动状态时，无法对数据包进行分段。如果 DF 状态为非活动状态，则路由器可以将数据包拆分为片段。目标设备稍后可以重新加入片段。它会将数据包返回到源网络而不分片。
-   Pv6：数据包不能被路由器分割并返回到源网络。

现在让我们看看[分片](https://www.baeldung.com/cs/ipv4-datagram)是如何工作的。下图显示了 1500 字节的源数据链路或 MTU 和 1200 字节的目标 MTU。考虑到允许分片，1400 Bytes 的数据包大小被分为 1200 Bytes 和 200 Bytes：![包头示例](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/Fragmentation-1.png)我们一定想知道在分片过程中原始数据包发生了什么？它们被丢弃，其中的数据变得支离破碎。

### 5.1. 好处

-   减少开销。
-   [没有路径 MTU 发现 (PMTUD)](https://en.wikipedia.org/wiki/Path_MTU_Discovery)的开销。

### 5.2. 缺点

-   片段丢失可能需要重新发送数据包并再次启动片段。
-   第一个片段仅包含标头，这可能会导致依赖于检查标头的设备出现问题。
-   碎片可能需要重新排序，特别是如果只有很少的数据包是碎片化的。

## 6. 人的重要性

MTU 是一种估计单位，指示网络资源可以有效确认的数据包的最大大小。 这是其他网络在建立 TCP 连接时应该知道的最重要的参数。更大的 MTU 支持更多的数据以适应更少的数据包，从而提高传输速度和性能。但是，如果在通信中发生错误，则数据包需要更长的时间才能重新传输。较小的 MTU 可以改善网络延迟。

## 六，总结

本文概述了 TCP 连接的最大数据包大小。首先，我们讨论了数据包大小的概念。然后我们讨论了了解数据包大小的重要性。有关 TCP 连接数据包大小的信息有助于了解 TCP 连接性能。总之，源网络必须确保数据包大小不应超过目标网络的 MTU。