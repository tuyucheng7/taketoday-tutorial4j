## 1. 概述

[HTTP 隧道](https://en.wikipedia.org/wiki/HTTP_tunnel) 连接两台被受控访问(例如[防火墙](https://www.baeldung.com/cs/firewalls-intro))隔开的计算机。隧道可以由放置在防火墙后面的[代理服务器建立。](https://www.baeldung.com/cs/proxy-vs-reverse-proxy)代理服务器的作用是使用 HTTP 代理协议在不加密的情况下中继 HTTP 请求。[Traceroute](https://www.baeldung.com/cs/request-timed-out-vs-destination-host-unreachable)用于了解请求在网络中一台计算机与另一台计算机之间的通信路由。

但是，traceroute 是使用 ICMP 协议的服务，因此 HTTP 代理无法通过隧道传输它们。另一种方法是使用[HTTP CONNECT](https://www.baeldung.com/cs/http-versions)通过代理服务器创建 HTTP 隧道。

在本教程中，我们将展示如何建立 HTTP 隧道以跟踪通过带防火墙的代理的通信路径。

## 2.HTTP隧道

隧道，也称为“端口转发”，有助于通过公共网络传输专用网络请求。

[代理服务器在 DMZ(隔离区](https://www.baeldung.com/cs/dmz-networking-security))中创建 HTTP 隧道。DMZ(外围网络)是企业网络上的一个独立区域。 企业网络的公共和私有网络资产可以访问 DMZ。一般在DMZ内部，我们可以放置我们希望企业网络外的用户能够访问的网络资产：

![此图显示非军事区 (DMZ)。 DMZ 是网络上的一个特殊区域，可以从企业外部和内部访问。](https://www.baeldung.com/wp-content/uploads/sites/4/2022/08/DMZ-2.png)

当我们建立HTTP隧道时，私网和公[网](https://www.baeldung.com/cs/routers-vs-switches-vs-access-points) 的通信使用基于HTTP协议的[封装](https://www.baeldung.com/cs/dns-authoritative-server-ip)。可以使用 HTTP Connect 或常用的[HTTP 方法](https://www.baeldung.com/cs/http-versions) (如 POST、GET、PUT 和 DELETE)建立 HTTP 隧道。

### 2.1. HTTP 代理服务器

代理服务器可帮助客户和服务提供商在多个级别提供多种类型的匿名性。对私人代理的需求如下：

-   防止[黑客追踪原始IP地址](https://www.baeldung.com/cs/cia-triad)
-   [验证](https://www.baeldung.com/cs/authentication-vs-authorization)用户
-   [缓存](https://www.baeldung.com/cs/cache-tlb-miss-page-fault)网页内容
-   根据本地访问策略跟踪内部服务器请求的负载和[数据包](https://www.baeldung.com/cs/tcp-max-packet-size)标头。

例如，当我们浏览访问www.baeldung.com时，它会向我们组织的代理服务器发送一个 HTTP 请求。代理服务器从baeldung.com区域的[权威服务器](https://www.baeldung.com/cs/dns-authoritative-server-ip)获取HTTP 响应并将其中继回浏览器，如下图所示：

![该图显示了通过代理服务器的浏览器通信](https://www.baeldung.com/wp-content/uploads/sites/4/2022/08/HTTP-Proxy-Server.png)

### 2.2. HTTP连接

现在让我们讨论一种流行的隧道方法，称为 HTTP CONNECT。在此方法中，浏览器请求 HTTP 代理服务器将[TCP 连接](https://www.baeldung.com/cs/tcp-max-packet-size)中继到目标服务器。然后服务器代表请求者客户端(浏览器)建立隧道，代理服务器中继 TCP 流。

建立隧道请求时，使用HTTP协议；设置隧道后，HTTP 代理服务器将中继 TCP 连接。

## 3. 路由跟踪

当我们使用互联网[连接](https://www.baeldung.com/cs/tcp-max-packet-size)到计算机时，它会经历多个网络跃点。要跟踪给定数据包所采用的确切路由，我们可以使用[traceroute](https://www.baeldung.com/linux/traceroute-command)(Unix、Linux、Mac OS X)或 tracert(Windows)命令。命令输出可能因请求者位置、 [路由器](https://www.baeldung.com/cs/routers-vs-switches-vs-access-points)可用性和使用指标而异。

### 3.1. 如何使用Tracert？

让我们从一个简单的例子开始——让我们为baeldung.com域执行 tracert 命令：

```shell
C:>tracert baeldung.com
```

结果应如下所示：

```shell
Tracing route to baeldung.com [2606:4700:3108::ac42:2b08]
over a maximum of 30 hops:

  1     1 ms     1 ms     1 ms  2001:8f8:1b27:4401:ea1b:69ff:fe06:7880
  2                          Request timed out.
  3     4 ms     3 ms     4 ms  2001:8f8:3:d106::1
  4     8 ms     5 ms     6 ms  2001:8f8:0:10:0:23:208:5
  5     6 ms     5 ms     6 ms  2001:8f8:0:10:0:20:23:1
  6     6 ms     6 ms    45 ms  2001:8f8:0:20:cd::2
  7     6 ms    10 ms     6 ms  2606:4700:3108::ac42:2b08

Trace complete.
```

要连接到baeldung.com，请求需要跳转通过不同的路由器。在结果中，我们可以看到从本地网络 (#1) 开始，数据包如何通过不同的跃点到达 #7 ( baeldung.com)的目的地。

下表给出了结果的解释：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-320eeaa8adab02a5dec977cc4e3702be_l3.svg)

## 4. 使用 HTTP 隧道进行 Traceroute

现在让我们探讨如何将 traceroute 与 HTTP 隧道一起使用。[tracert 命令使用类似于ping](https://www.baeldung.com/cs/protocols-ping)命令的低层网络协议(ICMP、UDP) 。HTTP 隧道使用更高层。因此不能直接在 HTTP 隧道中使用 tracert。

在代理后面使用 tracert 的解决方法是使用 SSH。[SSH](https://www.baeldung.com/cs/ssh-intro)客户端使用客户端端口向代理服务器发送tracert命令，并通过代理服务器接收目的站点的响应：

![客户端和代理服务器之间的 SSH 隧道](https://www.baeldung.com/wp-content/uploads/sites/4/2022/08/SSH-Tiunnel-2.png)

 

以下是基本注意事项：

-   我们需要到达托管目标站点的服务器的端口 443 (HTTPS) 或 80 (HTTP)
-   我们需要用户[凭证来访问](https://www.baeldung.com/cs/authentication-vs-authorization)代理服务器来监听来自客户端机器的请求
-   隧道建立后才能运行traceart

## 5.总结

在本文中，我们讨论了 HTTP 隧道和 HTTP 代理服务器，并展示了如何使用traceart命令获取域的跟踪路由。我们还解释了使用 HTTP 隧道和 SSH 在代理后面使用 traceart 的变通方法。