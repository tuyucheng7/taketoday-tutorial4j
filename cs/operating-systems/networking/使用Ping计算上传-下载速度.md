## 1. 概述

Ping 是大多数操作系统上可用的实用程序。通常，它用于检查网络设备是否可达。

该名称源于操作模式，它涉及发送(回声请求)和接收(回声回复)信息包，其方式与声纳隐约相似。一些作者认为该名称是 Packet InterNet Groper 的首字母缩写词。

正如我们将在本教程中看到的，ping 可用于测量网络连接的带宽。

根据操作系统的不同，实现方式会有细微差别。在下面的示例中，我们将使用 Linux 系统上可用的 ping 命令。

## 2. Ping 命令剖析：ICMP 协议

### 2.1. 一般特征

ping 实用程序使用 Internet[控制消息协议 (ICMP)](https://en.wikipedia.org/wiki/Internet_Control_Message_Protocol)中的回显请求和回显回复消息，ICMP 是任何 IP 网络的组成部分。发出 ping 命令时，会向指定地址发送回显请求数据包。当远程主机收到回显请求时，它会用回显回复数据包进行响应。

ping 命令发送几个回显请求。显示每个 echo 请求的结果，显示请求是否收到成功的响应、收到响应的字节数、生存时间 (TTL) 以及接收响应所需的时间，以及有关丢包和往返时间。

ping 命令允许我们：

-   测试我们的互联网连接。
-   检查远程机器是否在线。
-   分析是否存在网络问题，例如丢包或高延迟。

### 2.2. ICMP协议

ICMP协议位于[开放系统互连(OSI)模型](https://en.wikipedia.org/wiki/OSI_model)的网络层，如下图所示：

![OSI 模型中的 ICMP](https://www.baeldung.com/wp-content/uploads/sites/4/2021/10/ICMP-in-OSI-model.svg)

每个ICMP数据包的结构如下，MTU为1500字节：

![ICMP数据包](https://www.baeldung.com/wp-content/uploads/sites/4/2021/10/ICMP-packet.svg)

对于不同的 MTU，ICMP 负载的大小会发生变化，因此通常是可变的。

## 3. 检查主机可达性

Ping 通常用于检查远程设备是否可达。例如，向托管 Baeldung 的主机发送 10 个回显请求：

```
> 平-c 10 www.baeldung.com
PING www.baeldung.com (172.67.72.45) 56 (84) 字节数据。
来自 172.67.72.45 (172.67.72.45) 的 64 个字节：icmp_seq = 1 ttl = 59 time = 13.5 ms
来自 172.67.72.45 (172.67.72.45) 的 64 个字节：icmp_seq = 2 ttl = 59 time = 14.2 ms
来自 172.67.72.45 (172.67.72.45) 的 64 个字节：icmp_seq = 3 ttl = 59 time = 13.5 ms
来自 172.67.72.45 (172.67.72.45) 的 64 个字节：icmp_seq = 4 ttl = 59 time = 15.1 ms
来自 172.67.72.45 (172.67.72.45) 的 64 个字节：icmp_seq = 5 ttl = 59 time = 14.2 ms
来自 172.67.72.45 (172.67.72.45) 的 64 个字节：icmp_seq = 6 ttl = 59 time = 13.8 ms
来自 172.67.72.45 (172.67.72.45) 的 64 个字节：icmp_seq = 7 ttl = 59 time = 14.2 ms
来自 172.67.72.45 (172.67.72.45) 的 64 个字节：icmp_seq = 8 ttl = 59 time = 14.5 ms
来自 172.67.72.45 (172.67.72.45) 的 64 个字节：icmp_seq = 9 ttl = 59 time = 19.4 ms
来自 172.67.72.45 (172.67.72.45) 的 64 个字节：icmp_seq = 10 ttl = 59 time = 16.3 ms

--- www.baeldung.com ping 统计数据 ---
发送 10 个数据包，接收 10 个数据包，0% 丢包，时间 9011ms
rtt 最小/平均/最大/mdev = 13.460/14.869/19.415/1.708 毫秒
```

输出的第一行在远程 IP 之后指定发送/接收数据包或 MTU 的大小，56 (84)，这意味着 56 字节的有效负载和 84 字节的总长度，考虑到标头的 28 字节。

正如我们从最终统计数据中看到的那样，所有 10 个数据包的响应都已收到，平均发送/回复时间或 RTT(往返时间)为 14.869 毫秒。丢失一个或多个数据包是某些连接问题的征兆。

发送的有效负载的大小可以使用 -s 参数指定。默认值为 56 字节。

### 3.3. 远程主机距离：延迟时间

如果我们想通过我们的调制解调器传输一个大文件，可能需要几秒钟、几分钟甚至几小时。我们发送的数据越少，花费的时间就越少，但这是有限制的。无论数据量多小，对于任何特定的网络设备，总有一个我们永远无法逾越的最短时间。这就是所谓的设备延迟。

通过连接发送或接收的数据包越小，延迟时间就越重要，以百分比表示，在某些情况下，延迟时间可能会超过传输时间。

我们在前面的示例中讨论的 14,869 毫秒的平均 RTT 只不过是连接的平均延迟时间。该值的一半是发送或接收信息包所需的最短时间，无论其大小如何。

了解一些现成的数据和平均延迟时间后，我们可以计算出托管我们杂志的服务器距离多远：

1.  真空中的光速为![3E5](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-636d177df47c2c8a72c1c2a7cd17a2f8_l3.svg)km/s。
2.  光纤中的光速大约是真空中光速 ( ![3E5 乘以 0.66 = 1.98E5](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4952632fa73b94675191cf5d16f78615_l3.svg)km/s) 的 66%。铜线的信号速度大致相同。
3.  回声请求加上回声回复的平均时间延迟为 14.869 毫秒。单向延迟为 7.4345 毫秒。

因此，主机距离为：

 ![[D = 7.4345 , mathrm {ms} times frac {1 , mathrm {s}} {1000 , mathrm {ms}} times1.98 mathrm { E} 05 mathrm { frac {km} {s}} = 1472 , mathrm {km}]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3a1ae47ae8eb551200c706af08dd505b_l3.svg)

这意味着服务器最多连接 1472 公里的光纤。如果 ping 结果由 ISP 缓存，或者存在“任播”路由(非唯一 IP 地址)，则实际距离可能与 GeoIP 测试的结果不同。

## 4. Ping上传/下载速度

根据我们讨论的所有元素，并以与我们在计算主机距离时所做的类似的方式，我们能够计算出连接的带宽。

让我们通过使用-s选项指定负载大小来重复我们的示例：

```
> 平-c 10-s 1472 www.baeldung.com
PING www.baeldung.com (104.26.12.74) 1472 (1500) 字节数据。
来自 93.188.101.9 (93.188.101.9) icmp_seq = 1 Frag needed and DF set (mtu = 1492)
来自 104.26.12.74 (104.26.12.74) 的 1480 个字节：icmp_seq = 2 ttl = 59 time = 17.5 ms
来自 104.26.12.74 (104.26.12.74) 的 1480 个字节：icmp_seq = 3 ttl = 59 time = 15.4 ms
来自 104.26.12.74 (104.26.12.74) 的 1480 个字节：icmp_seq = 4 ttl = 59 time = 14.5 ms
来自 104.26.12.74 (104.26.12.74) 的 1480 个字节：icmp_seq = 5 ttl = 59 time = 19.6 ms
来自 104.26.12.74 (104.26.12.74) 的 1480 个字节：icmp_seq = 6 ttl = 59 time = 19.4 ms
来自 104.26.12.74 (104.26.12.74) 的 1480 个字节：icmp_seq = 7 ttl = 59 time = 15.2 ms
来自 104.26.12.74 (104.26.12.74) 的 1480 个字节：icmp_seq = 8 ttl = 59 time = 16.7 ms
来自 104.26.12.74 (104.26.12.74) 的 1480 个字节：icmp_seq = 9 ttl = 59 time = 14.5 ms
来自 104.26.12.74 (104.26.12.74) 的 1480 个字节：icmp_seq = 10 ttl = 59 time = 13.7 ms

--- www.baeldung.com ping 统计数据 ---
发送了 10 个数据包，接收了 9 个数据包，+1 个错误，10% 的数据包丢失，时间 9011ms
rtt 最小/平均/最大/mdev = 13.710/16.288/19.640/2.027 毫秒
```

负载大小为 1472 字节，最终 MTU 为 1500 字节(1472 + 28 标头)。因此，发送/接收的总量是![1500 乘以 2 = 24](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-54163f9864d37566d76547f74680a7e4_l3.svg)kb。

使用此值和平均 RTT，我们可以计算带宽：

 ![[24 , mathrm {kb} times frac {1} {16.288 , mathrm {ms}} times frac {1000 , mathrm {ms}} {mathrm {s}} times frac {mathrm {Mb}} {1000 , mathrm {kb}} = 1.473 , frac {mathrm {Mb}} {mathrm {s}}]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3abb6a1b040b1574b37dd22b2146cd57_l3.svg)

## 5.总结

在本教程中，我们了解了如何从 ping 命令获取大量信息。带宽就是其中之一。

其他更高级的可能性需要对 ICMP 协议有透彻的了解。