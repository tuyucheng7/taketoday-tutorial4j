## 1. 概述

Ping(Packet Internet 或 Inter-Network Groper)是一种[网络](https://www.baeldung.com/cs/osi-transport-vs-networking-layer)实用程序，用于检查[网络](https://www.baeldung.com/cs/osi-transport-vs-networking-layer)上的[主机](https://en.wikipedia.org/wiki/Host_(network))是否可以访问远程计算机或节点。Ping 可以使用多种协议，例如 AppleTalk、无连接网络服务 (CLNS)、[IP](https://www.baeldung.com/cs/client-vs-server-terminology)、互联网数据包交换 (IPX)、Apollo、VIP(VINES 互联网协议)和 Xerox 网络系统 (XNS)。在本教程中，我们将尝试解释 ping 使用的协议并探索它们的各个方面。

## 2. Ping 互联网协议

用于网络的默认协议是[Internet 协议](https://en.wikipedia.org/wiki/Internet_Protocol)(IP)。ping 过程涉及IP 堆栈中的多个层，例如[Internet 控制消息协议](https://www.baeldung.com/cs/popular-network-protocols)(ICMP)、[地址解析协议(ARP)。](https://www.baeldung.com/cs/popular-network-protocols)Ping 随带网络支持的操作系统一起检查是否可以到达 [IP 地址。](https://www.baeldung.com/cs/networking-ip-vs-port) 根据使用目的，ping 使用 ICMP 和 ARP 协议，与[TCP 和 UDP](https://www.baeldung.com/cs/udp-vs-tcp)不同。ping 通常用作通用术语，用于使用[Telnet](https://en.wikipedia.org/wiki/Telnet)和 Nmap等不同工具测试 TCP 和 UDP 端口的连接。让我们考虑[tcping](https://www.elifulkerson.com/projects/tcping.php)，另一个通过 TCP 端口进行 ping 操作的控制台应用程序。

### 2.1. ARP协议

地址解析是指在网络中查找计算机地址的过程。Ping 使用ARP 协议来识别远程计算机的MAC 地址。假设带有源计算机(比如主机 1)IP 地址的 ARP 请求被发送到目标计算机(比如主机 2)。作为对 ARP 请求的答复，主机 2 发送一个包含其 IP 和 MAC 地址的答复。为了避免重复的地址解析请求，主机 1 将缓存解析的地址(IP 和 MAC)一段时间(短)：  ![CS1-1](https://www.baeldung.com/wp-content/uploads/sites/4/2021/08/cs1-1.png)

### 2.2. ICMP

[ICMP](https://www.baeldung.com/cs/popular-network-protocols)协议为 ping 命令提供没有特定端口号的代码和类型： ping 实用程序包含 ICMP 协议的客户端接口，并使用回显请求(类型 8)和回显回复(类型 0)消息。当源机器发出 ping 命令时，会向目标机器的 IP 地址发送回显请求数据包：  ![CS2-1](https://www.baeldung.com/wp-content/uploads/sites/4/2021/08/cs2-1.png)生成此 ICMP 回显请求以确认目标机器是否可达。目标机器使用 ICMP 回显回复来响应回显请求。在此回复中，ping 程序接收到目标机器 IP、延迟信息、指示传输时间的时间戳值。有两种 ICMP 变体可用于支持[IPv4 和 IPv6](https://www.baeldung.com/cs/ipv4-vs-ipv6)。

### 2.3. AppleTalk

Ping 将 AEP(AppleTalk Echo Protocol)数据包发送到目标 AppleTalk 节点并等待回复。我们可以看到下面的回复文本格式：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a98f80c8cb6e25e60ac2c1d8c2091631_l3.svg)



### 2.4. 中枢神经系统

为了检查远程 CLNS 节点的状态，Ping 使用“ping clns”命令，我们可以在下面看到回复文本格式和描述：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e541c57ab5903f3568b6e19aa7f3f05a_l3.svg)



## 3. Ping 是如何工作的？

ping 的工作很简单，让我们在命令提示符下输入“ping”：

```shell
C:>ping

Usage: ping [-t] [-a] [-n count] [-l size] [-f] [-i TTL] [-v TOS]
            [-r count] [-s count] [[-j host-list] | [-k host-list]]
            [-w timeout] [-R] [-S srcaddr] [-c compartment] [-p]
            [-4] [-6] target_name

Options:
    -t             Ping the specified host until stopped.
                   To see statistics and continue - type Control-Break;
                   To stop - type Control-C.
    -a             Resolve addresses to hostnames.
    -n count       Number of echo requests to send.
    -l size        Send buffer size.
    -f             Set Don't Fragment flag in packet (IPv4-only).
    -i TTL         Time To Live.
    -v TOS         Type Of Service (IPv4-only. This setting has been deprecated
                   and has no effect on the type of service field in the IP
                   Header).
    -r count       Record route for count hops (IPv4-only).
    -s count       Timestamp for count hops (IPv4-only).
    -j host-list   Loose source route along host-list (IPv4-only).
    -k host-list   Strict source route along host-list (IPv4-only).
    -w timeout     Timeout in milliseconds to wait for each reply.
    -R             Use routing header to test reverse route also (IPv6-only).
                   Per RFC 5095 the use of this routing header has been
                   deprecated. Some systems may drop echo requests if
                   this header is used.
    -S srcaddr     Source address to use.
    -c compartment Routing compartment identifier.
    -p             Ping a Hyper-V Network Virtualization provider address.
    -4             Force using IPv4.
    -6             Force using IPv6.
Ping 执行以下检查：
```

-   网络上源IP和目的IP之间是否有连接。
-   连接速度([延迟](https://www.baeldung.com/cs/propagation-vs-transmission-delay))。

大多数情况下，使用不带任何选项的 ping 命令足以验证网络中的机器是否可达。但是，对于任何连接问题的高级故障排除，都会使用“选项”。选项 -f、-v、-r、-s、-j、-k 支持 IPv4，而 -R 和 -S 仅支持 IPv6。ping命令向目的地址发送多次回显请求并显示结果。我们可以看到回复提供了请求状态、回复中收到的字节数、延迟、丢包和往返时间等信息，如下所示：

```shell
C:>ping www.baeldung.com

Pinging www.baeldung.com [2606:4700:20::681a:c4a] with 32 bytes of data:
Request timed out.
Reply from 2606:4700:20::681a:c4a: time=110ms
Reply from 2606:4700:20::681a:c4a: time=114ms
Reply from 2606:4700:20::681a:c4a: time=107ms

Ping statistics for 2606:4700:20::681a:c4a:
    Packets: Sent = 4, Received = 3, Lost = 1 (25% loss),
Approximate round trip times in milli-seconds:
    Minimum = 107ms, Maximum = 114ms, Average = 110ms
```

## 4.如何使用Ping？

Ping 可用于多种用途，例如用于跟踪系统使用情况的脚本、跟踪网络访问的故障排除以及查找网络中的设备。ping的用途大致可以分为以下几类：

-   故障排除——可以使用一系列 ping 命令来解决连接问题，以确定 IP 是否可达。
-   设备发现——Ping 允许在网络中找到连接的设备。
-   网络监控- 常用的是使用 ping 的 -t 选项(直到停止)。如果回复超时或延迟时间过长，则系统访问存在问题。

让我们考虑下表，比较 ping 在 Windows 和 Linux 中的使用：



![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9b8a742d7ec809105111d0c7b29b5ad8_l3.svg)



## 5.安全问题

Ping 回复可能会产生有关操作系统、机器 IP 和 MAC 的信息，并可能引起安全问题。老练的网络攻击者可能会利用此信息对目标机器进行恶意攻击。在已识别的网络中，攻击者可能会使用 ping 来获取可访问和响应的系统列表。为避免安全漏洞，许多防火墙会阻止来自不受信任网络的 ping 请求。

## 六，总结

在本文中，我们介绍了 ping 命令背后的概念和协议。Ping 用于评估目标机器的连接状态和相关网络参数。我们可以使用它来解决连接问题，例如速度变慢、数据包丢失和网络监控。除了 IP 协议外，ping 还可以使用其他几种协议来调试不同的网络问题。