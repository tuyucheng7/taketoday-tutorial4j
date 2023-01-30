## 1. 概述

在本教程中，我们将讨论各种类型的 DNS 记录。最后，我们将讨论如何将 DNS 记录重定向到不同的端口。

## 2.DNS简介

在计算机网络中，我们可以使用[IP 地址](https://www.baeldung.com/cs/ipv4-vs-ipv6)来识别设备。另一方面，人类发现使用基于单词的地址很容易。因此，我们很容易记住像google.com这样的域名。但是，记住 IP 地址86.112.245.76是很困难的。

因此，需要保留一个目录，将这些域名映射到它们的服务器 IP 地址。[域名系统 (DNS)](https://www.baeldung.com/cs/dns-intro)就像域名的电话簿。此外，它还提供特定域名的服务器 IP 地址。服务器地址有两部分：IP地址和[端口号](https://www.baeldung.com/cs/networking-ip-vs-port)。

要查找域名的 IP 地址，设备会在本地[缓存内存](https://www.baeldung.com/cs/cache-memory)中搜索 DNS 记录。因此，如果找不到该条目，请求将转到解析器 ISP 缓存服务器。此外，如果我们仍然无法在 ISP 缓存中找到记录，根服务器就会被赋予获取该域的 IP 地址的任务。[它有助于使用顶级域 (TLD)](https://www.baeldung.com/cs/dns-intro) 和[名称服务器](https://www.baeldung.com/cs/dns-intro)解析 IP 地址。

DNS记录是将域名信息映射到IP地址的记录。DNS 记录不仅限于 IP 地址，还包含其他一些信息。常见的 DNS 记录类型有[A、CNAME、Mx、SPF、Txt、DKIM、NS、CAA 和 SRV](https://en.wikipedia.org/wiki/List_of_DNS_record_types)记录。

我们使用规范名称记录 (CNAME) 将一个域或子域名称映射到另一个域。邮件交换器记录(MxRecord)是一个域名的邮件服务器记录。例如，如果域名的邮件服务器与主机提供的服务器不同，则可以通过 MxRecord 提供 DNS 信息。SPF 和 DKIM 记录对于邮件服务器很重要。此外，它们在电子邮件安全方面也有帮助，包括身份验证和防止[电子邮件欺骗](https://www.baeldung.com/cs/security-mitm)。

## 3.SRV记录

为了将 DNS 重定向到不同的端口，服务记录 (SRV) 起着重要作用。SRV 记录是一种 DNS 记录，其中包含有关特定服务位置的信息。此外，SRV 记录包含连接到该服务的服务器地址。

可以使用 SRV 记录配置不同的服务，如[SMTP](https://en.wikipedia.org/wiki/Simple_Mail_Transfer_Protocol)、[VoIP和即时消息。](https://en.wikipedia.org/wiki/Voice_over_IP)此外，SRV 记录包括 IP 地址、端口和[生存时间 (TTL)](https://www.baeldung.com/cs/ipv4-datagram)。让我们讨论 SRV 记录的所有字段：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5219e85cb1d773d3d31a0319c2764819_l3.svg)

SRV 记录中的名称字段表示域名并且必须以点结尾。服务显示所需服务的缩写形式。协议字段提供有关服务中使用的特定协议的信息。一般来说，最流行的协议是[TCP](https://www.baeldung.com/cs/udp-vs-tcp)和[UDP](https://www.baeldung.com/cs/udp-vs-tcp)。生存时间 (TTL) 表示我们希望将 DNS 记录保存在高速缓存中的时间。因此，DNS 记录的变化是在超过 TTL 后反映出来的。

优先级是表示给予主机的优先级的数字。较低的值表示较高的优先级。假设有两台服务器，A 和 B。因此，如果服务器 A 的优先级值低于服务器 B，则请求流量将始终转到服务器 A。

权重字段表示赋予主机的权重。给定相同的优先级，如果特定主机的权重更高，则它有更大的机会被选择用于传入流量。此外，如果两个服务器具有相同的优先级值，请求流量将在服务器之间分配。因此，优先级值最高、权重较高的服务器收到的请求数相对较多。

端口表示可供服务器连接的端口类型。通常，端口是 TCP 或 UDP 端口。最后，字段目标表示提供某些特定服务的机器的主机名。此外，还有两个字段：IN，表示 DNS 类，SRV 表示记录的类型。

现在让我们看一下SRV记录的一般立场形式：

```vhdl
_Service._Protocol.Name. TTL IN SRV Priority Weight Port Target.

```

SRV 记录的一些示例是：

```apache
_testservice._tcp.example.com. 18000 IN SRV 10 40 5555 tar1.example.com.
```

## 4. 将 DNS 重定向到不同的端口

下面说说SRV记录的一个应用。我们要解决一个域的不同子域连接到不同的服务器或端口的问题：

![服务记录](https://www.baeldung.com/wp-content/uploads/sites/4/2022/05/srvrecord.png)

假设我们要将域example.com的多个子域连接到 IP 地址的不同端口：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-affc70a2b4c992039742f24340a4440d_l3.svg)

运行在同一个公共 IP 地址上的多个服务器通过不同的端口公开。此外，我们希望将三个不同的子域分配给不同的端口。因此，通过这种方式，我们可以保证不同子域的请求到各自的服务器上。因此，这可以通过创建 SRV 记录来实现。对于子域，我们可以创建服务记录 (SRV)：

```apache
_xyzservice._tcp.example.com. 18000 IN SRV 10 40 5555 sub1.example.com.
_xyzservice._tcp.example.com. 18000 IN SRV 10 30 5556 sub2.example.com.
_xyzservice._tcp.example.com. 18000 IN SRV 10 30 5557 sub3.example.com.
```

因此，不同子域的不同请求，会去到公网IP地址的不同端口。如果不同的服务器通过不同的端口公开，则对特定子域的请求将转到其对应的服务器。另外，上面提到的SRV记录需要A记录来指定特定服务器的IP地址。因此，让我们看一下 A 记录条目：

```css
sub1.example.com. 18000 IN A <IP Address>
sub2.example.com. 18000 IN A <IP Address>
sub3.example.com. 18000 IN A <IP Address>

```

这样，SRV 记录就可以将不同的子域请求划分到不同的物理服务器或端口来重定向 DNS 地址。

## 5.总结

在本教程中，我们讨论了几种类型的 DNS 记录。我们详细研究了 SRV 记录。最后，我们解释了 SRV 记录如何帮助将 DNS 重定向到不同的端口。