## 1. 概述

现代计算机使用各种层进行通信，以确保它们正在处理的信息以高效和稳健的方式传输。用于描述这些不同层的最常见模型是 Internet 协议套件，通常称为 TCP/IP：

![通信层 1](https://www.baeldung.com/wp-content/uploads/sites/4/2022/05/Communication-Layers-1.png)

本文将重点介绍该架构的传输层，解释两种不同通信协议之间的区别：传输层安全协议 (TLS) 和数据报传输层安全协议 (DTLS)。

## 2.传输层

Internet 协议套件中传输层最常见的组件是传输控制协议 (TCP) 和用户数据报协议 (UDP)。TCP 用于面向连接的传输——最常见的是 HTTPS 安全证书，但也有 SMTP、POP、FTP。这些可以保证网站的完整性，保护其用户的隐私并启用其他有趣的功能，例如访问用户地理定位。

TCP 还提供了一种验证信息是否正确[传递](https://www.baeldung.com/cs/tcp-protocol-syn-ack)的方法以及错误检查方法。[另一方面](https://www.baeldung.com/cs/udp-vs-tcp)，UDP 是无连接的，用于优先考虑延迟的应用程序——即视频流、游戏、广播等。这允许数据包在单程传输中更快地发送，并且错误检查仅限于简单的校验和。

这些通信协议的主要缺点是它们未加密，因此容易被恶意代理窃听、伪造和篡改。这是 TLS 和 DTLS 使用加密来保护设备之间通信的地方。在讨论 TLS 和 DTLS 时，将它们与 TCP 和 UDP 进行比较很有趣，因为它们的用例相似。TLS 和 TCP 担心缓慢但肯定地传递信息，而 UDP 和 DTLS 快速传递信息并关注延迟关键型应用程序。

## 3. 传输层安全 (TLS)

在 TLS 之前，存在安全套接字层 (SSL)，这是一种主要用于将网站身份绑定到加密密钥的协议。此过程以创建 SSL 证书结束，并在 HTTP 协议中用于在浏览器和网页或其他文档之间建立连接。

还可以提到的是，SSL 认证通常包括与网站相关的数据，例如域名和所有权信息。话虽这么说，传输层安全性是对已弃用的安全套接字层 ( SSL)的[升级。](https://www.baeldung.com/cs/ssl-vs-tls)TLS 和 SSL 之间的差异是高度技术性的，并且涉及握手过程和基于哈希的消息身份验证代码支持等方面。

### 3.1. TLS 握手

为了开始通信，客户端-服务器系统首先依赖于 3 次 TCP 握手(绿色)。在 TLS 部分的第一次完整“往返”中，一些信息以纯文本形式传输，例如 TLS 协议版本和其他选项。之后，加密隧道的客户端-服务器协商开始。

这对机器必须就 TLS 协议的版本达成一致，选择密码套件(用于保护网络连接的算法)并在必要时验证证书。我们可以看到添加假设的 30 毫秒延迟可能会因为必要的“往返”而导致问题(见下图)。然后，服务器选择一个 TLS 版本和密码套件以继续通信，并将其证书提供给客户端。

如果客户端批准证书，则客户端可以启动[Diffie-Hellman 密钥交换](https://www.comparitech.com/blog/information-security/diffie-hellman-key-exchange/)或[RSA](https://en.wikipedia.org/wiki/RSA_(cryptosystem))以就对称密钥达成一致。服务器将加密的“完成”消息返回给客户端。客户端使用对称密钥对其进行解密并验证 MAC。现在可以发送应用程序数据：

![tcpvstls 1](https://www.baeldung.com/wp-content/uploads/sites/4/2022/05/tcpvstls-1.png)

## 4. 数据报传输层安全(DTLS)

尽管数据报传输层安全性 (DTLS) 是一种独立于 TLS 的加密通信协议，但它基于 TLS，旨在为流数据提供类似的安全保证以及许多相同的功能。两种协议之间的主要区别之一是 DTLS 避免了 TLS 的长通信时间。但是，由于该协议经常在UDP之上使用，它不会对数据包进行重新排序或重传，也不保证数据包的不可重放性。DTLS 还可以在保持加密的同时提高 VPN 应用程序的性能。

## 5. 特殊差异

以下是两种协议之间的一些明显差异。

### 5.1. 显式记录

TLS 将一长串数据分成多个块。这些划分对应用程序是透明的。DTLS 不是这种情况，因为它使用可以完全发送或不发送的记录。这些记录需要由应用程序自己管理。

### 5.2. 容忍的改变

与 UDP 中一样，数据报可以丢失、重新排序和修改。因此，客户端和服务器机器都可以允许非规范化通信。例如，记录的顺序甚至可能由于多种原因(即延迟)而发生变化。但是，特别是重复项可能会引起警告。具有严重异常的记录可能会被丢弃。

### 5.3. 无验证终止

就像 UDP 一样，DTLS 中的传输没有结束信号的概念。DTLS 只是停止传输。这意味着从服务器接收数据的客户端不知道是否已交付全部数据或是否存在通信错误。TLS 用警告消息表示这一点。

### 5.4. 防止 IP欺骗

DTLS 使用 cookie 来防止 IP 欺骗，这使我们可以在不显示真实 IP 地址的情况下与服务器通信。另一方面，TLS 仅在建立 TCP 握手后才执行通信，因此很难欺骗我们的 IP。

## 6.例子

下面是两种协议的一些示例实现。

### 6.1. TLS

-   [电子邮件加密](https://www.agari.com/email-security-blog/transport-layer-security-tls-emailencryption/)(今天接近 90% 的电子邮件使用此)
-   [网络访问](https://www.techtarget.com/searchsecurity/definition/Extensible-Authentication-Protocol-EAP)(使用 EAP-TLS)
-   [单点登录服务](https://docs.aws.amazon.com/singlesignon/latest/userguide/infrastructure-security.html)(如 Amazon AWS)
-   [签署和加密文件](https://www.ssl.com/certificates/personal-business-smime-email-clientauth-document-signing-certificate/)([和代码](https://www.ssl.com/certificates/code-signing/))

### 6.2. 数字传输层

-   [Cisco AnyConnect VPN](https://www.cisco.com/c/en/us/support/docs/security/anyconnect-secure-mobility-client/116312-qanda-anyconnect-00.html) Client 使用 TLS 执行用户身份验证并发明了基于 DTLS 的 VPN
-   [Citrix Systems NetScaler](https://docs.citrix.com/en-us/legacy-archive/netscaler.html)使用 DTLS 保护 UDP
-   [WebRTC](https://www.callstats.io/blog/2018/05/16/explaining-webrtc-secure-real-time-transport-protocol-srtp)使用兼容的网络浏览器，如 Google Chrome、Opera 和 Firefox 进行 DTLS-SRTP 通信

## 七、总结

这是一篇关于 TLS 和 DTLS 通信协议之间差异的文章。