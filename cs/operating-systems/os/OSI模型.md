## 1. 概述

OSI 模型代表开放系统互连模型，是描述网络系统通信功能的标准化框架。它由 7 层组成，每一层都有特定的功能要执行。

在本教程中，我们将详细讨论 OSI 模型及其层。我们将介绍每一层的描述和功能。

## 2. OSI模型介绍

在网络系统的早期，网络设备仅限于与同一供应商制造的设备进行通信。例如，X 公司生产的网络设备只能与同一公司生产的其他设备通信，而不能与任何其他公司的设备通信。

为确保国内和国际制造的网络设备能够相互通信，设备应通过标准程序进行开发。为了在计算和通信系统中实现标准化，[国际标准化组织 (ISO)](https://en.wikipedia.org/wiki/International_Organization_for_Standardization)于 1984 年发布了 OSI 模型。

OSI模型由7层组成：应用层、表示层、会话层、传输层、网络层、数据链路层、物理层。这些层中的每一层都扮演着不同的角色，它们协同工作以将数据从一个网络设备传输到另一个网络设备。

让我们看看 OSI 模型如何：

![1-6](https://www.baeldung.com/wp-content/uploads/sites/4/2020/11/1-6.png)

现在，我们将讨论每一层的主要功能：

## 3.物理层

OSI 模型中的最低层，即物理层，负责网络设备和[传输介质](https://en.wikipedia.org/wiki/Transmission_medium)之间原始数据的发送和接收。它以比特的形式存储信息或数据，并将比特从一个节点传输到另一个节点：

![2-1-2](https://www.baeldung.com/wp-content/uploads/sites/4/2020/11/2-1-2.png)

[物理层在控制传输速率](https://en.wikipedia.org/wiki/Bit_rate)方面起着至关重要的作用。每当某个设备向物理层发送数据时，它都会接收数据，将数据转换为位，然后发送到数据链路层。在传输比特时，它定义了每秒传输的比特数。

它通过设置控制接收器和发送器的时钟来促进位级别的同步。

它定义了数据在两个网络设备之间流动的传输模式。

物理层还定义了[物理拓扑](https://en.wikipedia.org/wiki/Network_topology)。物理拓扑是计算机电缆和其他网络设备的实际布局。

物理层使用的网络协议有[RS-232](https://en.wikipedia.org/wiki/RS-232)、[UTP 电缆](https://en.wikipedia.org/wiki/Twisted_pair)、[DSL](https://techterms.com/definition/dsl)。

## 4. 数据链路层

数据链路层负责两个直接相连的节点之间的数据传输。当物理层接收到数据时，它将其转换为比特并将其发送到数据链路层。数据链路层确保从物理层接收到的数据或数据包没有错误，然后将数据从一个节点传输到另一个节点。它使用其 MAC 地址将数据包发送到另一个节点。

数据链路层又分为两个子层：[逻辑链路控制(LLC)](https://en.wikipedia.org/wiki/Logical_link_control)和[媒体访问控制(MAC)](https://en.wikipedia.org/wiki/Medium_access_control)。LLC 层负责检查接收到的数据包中的错误，并同步帧。MAC 层控制网络设备对传输介质的访问。它还允许将数据从一个节点传输到另一个节点。

一旦来自物理层的原始数据到达数据链路层，它将原始数据转换为数据包，称为帧。有了框架，它还添加了标题和结尾。头部和尾部包括硬件目标和源地址的信息。这样也有助于数据包到达目的地：

![3-2](https://www.baeldung.com/wp-content/uploads/sites/4/2020/11/3-2.png)

数据链路层的主要职责是控制数据流。假设一些数据从一个高处理服务器传输到另一个低处理服务器。数据链路层确保两个服务器之间的数据速率匹配并且没有数据被破坏。

数据链路层还负责[差错控制和访问控制](https://en.wikibooks.org/wiki/Communication_Networks/Error_Control,_Flow_Control,_MAC)。

数据链路层中使用的最流行的计算机网络技术是[以太网](https://en.wikipedia.org/wiki/Ethernet)。

## 5. 网络层

网络层将数据从一个传输站传输到另一个传输站。它还有助于选择从一台主机到另一台主机的最短路径，以便在最佳时间传送数据。这个过程称为路由。它还将发送方和接收方的 IP 地址放入标头中。

网络层提供不同网络设备之间的逻辑连接。 它还在解决问题方面发挥着重要作用。当传入数据到达网络层时，它会在标头中添加目标地址和源地址：

![4-1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/11/4-1.png)

网络层使用各种协议，如[IPv4/IPv6](https://www.geeksforgeeks.org/differences-between-ipv4-and-ipv6/)、[ARP](https://en.wikipedia.org/wiki/Address_Resolution_Protocol)、[ICMP](https://en.wikipedia.org/wiki/Internet_Control_Message_Protocol)来执行其功能。

## 6.传输层

传输层工作在网络层和应用层之间。它确保数据的端到端和完整传输。它还在数据传输完成时向发送方节点提供确认。如果在传输过程中发生错误，传输层将重新传输数据。

传输层从网络层接收数据包并将数据包分成更小的数据包，称为段。这个过程称为[分段](https://en.wikipedia.org/wiki/Packet_segmentation)。它还控制数据的流向和错误，帮助数据顺利到达接收端：

![5-1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/11/5-1-1024x638.png)

在接收端，传输层以数据段的形式接收数据。它重新组装该段并确认数据转换成功。

由于传输层处理发送站和接收站之间的数据包传输，它主要使用[TCP](https://www.baeldung.com/cs/udp-vs-tcp)、[UDP](https://www.baeldung.com/cs/udp-vs-tcp)协议来传输数据包。

## 7.会话层

会话层用于建立连接、维护和同步通信设备之间的会话。它还提供身份验证并确保安全性。

以段的形式传输数据时，会话层会增加一些同步点。如果出现错误，传输将从最后一个同步点重新开始。这个过程称为同步和恢复：

![6-1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/11/6-1.png)

会话层作为对话控制器也扮演着重要的角色。对话控制器识别会话的通信模式。[它允许在半双工或全双工的](https://en.wikipedia.org/wiki/Duplex_(telecommunications))两个系统之间进行通信。

为了让应用程序能够与不同的计算机进行通信，会话层使用了像[NetBIOS](https://en.wikipedia.org/wiki/NetBIOS) 或 [PPTP](https://en.wikipedia.org/wiki/Point-to-Point_Tunneling_Protocol)这样的网络协议。

## 8. 表现层

表示层给出了两个网络系统之间交换的数据的语法和语义。它提供翻译，也称为翻译层。

它还通过为数据提供加密和解密来确保数据隐私。它有助于减少表示数据所需的位数。这个过程正式称为数据压缩：

![7-1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/11/7-1.png)

为了保证传输数据的私密性和安全性，表示层使用[SSL](https://www.cloudflare.com/en-in/learning/ssl/what-is-ssl/)或[TSL](https://www.cloudflare.com/en-in/learning/ssl/transport-layer-security-tls/)协议。

## 9. 应用层

应用层是OSI模型中的最后一层，与软件应用非常接近。它充当软件应用程序和最终用户之间的窗口。它有助于从网络访问数据并将信息显示给用户。

应用层执行多种功能，例如它允许用户远程访问文件、管理文件、从计算机检索特定数据：

![8-1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/11/8-1-1024x651.png)

应用层中使用的流行协议有 [HTTP](https://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol)、[FTP](https://en.wikipedia.org/wiki/File_Transfer_Protocol)、[DNS](https://en.wikipedia.org/wiki/Domain_Name_System)、[SNMP](https://en.wikipedia.org/wiki/Simple_Network_Management_Protocol)。

## 10.总结

在本教程中，我们详细讨论了 OSI 模型。我们对 OSI 模型的每一层及其功能进行了简要讨论。