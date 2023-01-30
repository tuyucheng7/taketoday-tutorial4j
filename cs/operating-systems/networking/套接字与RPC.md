## 1. 概述

在本教程中，我们将探讨用于网络通信的两种技术：套接字和 RPC。

我们还将介绍它们之间的核心区别。

## 2.插座

[套接字](https://www.baeldung.com/cs/port-vs-socket)是允许两个进程通过网络进行通信的软件结构。它主要响应实现远程信息交换功能的需求。此外，我们在集中式通信过程中使用套接字。

Socket 结合了[IP 地址](https://www.baeldung.com/cs/ipv4-vs-ipv6)和[端口号](https://www.baeldung.com/cs/networking-ip-vs-port)。IP 地址是标识网络中机器的唯一编号。此外，端口号是网络中进程的唯一逻辑标识符。它有助于识别过程。一般来说，我们在socket编程中主要使用两种通信模式：非连接的[UDP](https://www.baeldung.com/cs/udp-vs-tcp)模式和连接的[TCP](https://www.baeldung.com/cs/udp-vs-tcp)模式。

套接字是分布式通信机器的一个端点，它使用套接字来实现[客户端-服务器模型](https://www.baeldung.com/cs/client-vs-server-terminology)并解决不同的网络应用。分布式通信需要两个套接字作为两个端点提供：一个在客户端，一个在服务器。

因此，服务器套接字通常负责从客户端套接字接收信息，并根据客户端-服务器应用程序请求对其做出响应。相比之下，在网络通信中不可能在一台机器上找到绑定不同进程的IP地址和端口号。但是，如果客户端知道服务器的 IP 地址和端口号，则可以在两台机器之间创建套接字通信。

### 2.1. 工作程序

正如我们已经讨论过的，套接字地址由 IP 地址和端口号组成。IP 地址有助于识别计算机网络中的特定系统。此外，端口号唯一标识系统中的特定进程：

![dsfsdfs.drawio](https://www.baeldung.com/wp-content/uploads/sites/4/2022/06/dsfsdfs.drawio.png)

套接字可以使用 TCP 或 UPD 进行通信。我们来看一个客户端尝试TCP和UDP方式连接服务器的流程图：

![图UdpTcp](https://www.baeldung.com/wp-content/uploads/sites/4/2022/06/DiagramUdpTcp.drawio.png)

当不需要遵循客户端-服务器架构时，我们可以在套接字连接中使用 UDP。因此，在发送或接收信息之前，不必在机器之间建立连接。端点响应客户端而不检查两台机器是否有资格交换数据。

在这种情况下，我们不能保证收到的数据包含从客户端发送的相同数据包。它不可靠，但允许大量用户访问端点服务器。

在使用 TCP 连接的套接字的情况下，我们通过在发送和接收数据时在套接字的两个端点之间建立连接来验证所有数据的完整性。连接后，两台机器都会测试通信端口以确保数据能够成功传输。

客户端通常会询问已经拥有的服务器的身份，服务器以自己的身份响应并请求客户端的身份。通过确认通信建立，客户端和服务器之间的数据交换准备开始。

### 2.2. 应用

套接字的使用因分布式应用程序的目标而异。使用其中一种提供的模式 (UDP/TCP) 的应用程序必须考虑每种通信模式可用的特征，包括安全性、[延迟](https://www.baeldung.com/cs/packet-time-latency-bandwidth)或[数据完整性](https://en.wikipedia.org/wiki/Data_integrity)。但是，我们可以在需要消息交换的不同域中使用套接字。

[Zoom](https://en.wikipedia.org/wiki/Zoom_Video_Communications)、[Discord](https://en.wikipedia.org/wiki/Discord)和[Skype](https://en.wikipedia.org/wiki/Skype_for_Business)等视频会议应用程序使用带有 UDP 的套接字连接，因为视频流需要连续的数据传输。因此，它可以防止应用程序暂停以重新传输丢失的数据包。此外，大多数在线游戏都使用带有 UDP 的套接字。

[Whatsapp](https://en.wikipedia.org/wiki/WhatsApp) 和[Viber](https://en.wikipedia.org/wiki/Viber)等 IP 语音 (VoIP) 应用程序需要在语音/视频通话期间连续传输语音/视频。因此，这些应用程序使用带有 UDP 的套接字连接。此外，一个更重要的应用是[域名系统(DNS)](https://www.baeldung.com/cs/dns-intro)。

另一方面，[文件传输协议 (FTP)](https://www.baeldung.com/cs/active-vs-passive-ftp)使用与 TCP 的套接字连接。文件传输中数据丢失的后果是不可接受的。因此，FTP 服务器必须使用 TCP 协议而不是 UDP 协议，以便检查所有数据包并在检测到丢失数据包时请求重新传输数据包。

其他使用带 TCP 连接的套接字的应用程序包括[超文本传输协议 (HTTP)](https://www.baeldung.com/cs/popular-network-protocols)和通过 Watsapp、[Facebook](https://en.wikipedia.org/wiki/Facebook)、[Instagram](https://en.wikipedia.org/wiki/Instagram)和[Google Chat](https://en.wikipedia.org/wiki/Google_Chat)等应用程序进行的文本通信。

## 3.远程调用

远程过程调用 (RPC) 是进程之间的一种通信协议，它允许一台机器使用网络上另一台机器的参数来执行特定的过程。它通常在分布式系统中实现，其中不同位置的不同站点在物理上是分开的。RPC 是一种高级范式，它在实现过程中考虑了传输协议的存在。

### 3.1. 工作程序

当某台机器从系统中的另一台机器调用远程过程时，源调用机器等待结果。在目标调用机中，从接收到的消息中提取过程参数，并将其发送到包含所需过程的机器。该过程使用提供的参数执行。此外，我们将结果返回给发出该调用的机器。让我们讨论调用调用的步骤。

[最初，客户端使用客户端存根过程](https://en.wikipedia.org/wiki/Stub_(distributed_computing))以给定参数在本地调用过程。客户端存根驻留在客户端自己的地址空间中。[接下来，存根将参数封装为消息，并通过传输层](https://www.baeldung.com/cs/osi-model)将其发送到远程服务器。

服务器使用服务器存根接收消息。一旦服务器收到消息，服务器存根就会将消息转换为参数。此外，使用来自存根的转换结果维护服务器中远程过程的执行。执行结果返回到服务器存根以转换为消息。最后，我们将包含结果的消息转发给服务器的传输层。

服务器的传输层将包含结果的消息传输到客户端的传输层，客户端的传输层将其发送回客户端存根。此外，客户端存根将消息转换为远程过程获得的结果。

### 3.2. 应用

在对过程的远程调用中可以找到各种 RPC 协议的使用，无论是在本地还是从分布式系统中的不同机器。让我们讨论一些使用 RPC 协议的应用程序。

[远程文件共享 (RFS)](https://en.wikipedia.org/wiki/Remote_File_Sharing)在数据库分发中使用 RPC。对数据库的远程访问在响应时间方面提高了系统质量并减少了服务器拥塞。此外，[网络文件系统 (NFS)](https://en.wikipedia.org/wiki/Network_File_System)使用 RPC 协议。

更多使用 RPC 的应用程序包括[Windows Communication Foundation (WCF) ，它是](https://en.wikipedia.org/wiki/Windows_Communication_Foundation)[.NET 框架](https://en.wikipedia.org/wiki/.NET_Framework)中的一个开源编程接口，用于构建连接的、面向服务的应用程序。[Google Web Toolkit](https://en.wikipedia.org/wiki/Google_Web_Toolkit)使用 RPC 与服务器服务通信。

## 4. 差异

套接字和 RPC 共享相同的目标，即创建一个信息流通道，两个不同的端点进程在本地或远处可以使用消息相互通信。让我们讨论一下它们之间的一些核心区别：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e9a6f20f491185d9f1c8e5c89fe5067d_l3.svg)

## 5.总结

在本教程中，我们讨论了两种可以在网络中创建信息流通道的技术：套接字和 RPC。我们介绍了它们之间的核心区别。