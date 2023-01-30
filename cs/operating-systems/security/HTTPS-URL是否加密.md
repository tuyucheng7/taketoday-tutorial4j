## 1. 概述

今天，在使用任何在线服务时，我们习惯于在 URL 的一侧寻找锁定图标。此图标是一个图形指示器，表明该服务应该是安全的或至少具有一定的安全级别。

它表示使用 HTTPS，这是一种安全的通信方式。但是这个数据流上的什么实际上是安全的？是否有任何关于数据交换的信息可能会被窃听，而不是加密的？HTTPS URL 是否也加密？

在本教程中，我们将一探究竟。

我们知道，数字安全和隐私的斗争几乎与技术本身一样古老。在万维网的最初几天，与大多数互联网服务一样，它的设计目的是为了简单和开放。很少关心第三方窃听通信的可能性。

[HTTP](https://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol)协议与旧的应用层协议 FTP、Telnet、SMTP 类似，使用明文连接。这意味着通信流中间路径中的任何人都可以捕获数据流并查看流经它的每一位信息。

[为解决此问题，HTTP 使用名称为HTTPS](https://en.wikipedia.org/wiki/HTTPS)的加密方法进行了扩展，首先使用 SSL，然后使用 TLS。

## 2. 为什么要加密？

[加密](https://en.wikipedia.org/wiki/Encryption#History)是一种伪装或编码消息的方法，以便只有发送者和接收者才能理解它们的含义。它已经使用了很长时间，甚至在数字革命到来之前。如今，加密是用于实现一些信息安全关键目标的方法之一：

-   机密性——防止信息意外泄露
-   完整性——通信不能被篡改，即任何可以检测到消息是否被更改、剪辑或修改的信息
-   可用性——数据在需要时可用
-   不可否认/审计——发件人应该不可能否认其作者身份
-   [身份验证](https://www.baeldung.com/cs/authentication-vs-authorization)——可以毫无疑问地断言参与通信的任何一方的身份
-   [授权](https://www.baeldung.com/cs/authentication-vs-authorization)——仅授权方的数据访问

现代加密方法依赖于使用只有端点知道的适当密钥。没有密钥，解密或任何更改至少应该是非常困难的，或者更好的是，不可能的努力。

通过增加密钥大小或使用更强大的加密算法，应该可以轻松提高篡改通信的难度。

我们不会在本教程中深入研究加密的细节，因为它[已经在另一个教程中介绍过](https://www.baeldung.com/cs/symmetric-vs-asymmetric-cryptography)。我们还可以查看这些关于[RSA](https://www.baeldung.com/java-rsa)和[AES](https://www.baeldung.com/java-aes-encryption-decryption)基础知识的教程。这些是当今可用的更常见的[非对称加密](https://en.wikipedia.org/wiki/Public-key_cryptography)，也用于 HTTPS 标准。

## 3.HTTPS

HTTPS 允许在 HTTP 消息交换中使用加密扩展。可以使用证书来验证服务器(通常情况)和客户端(以便只有特定的授权客户端才能与服务器交换数据)。

首先，让我们回顾一下 URL 是什么以及如何定义：在 HTTPS 上，[通用资源定位器](https://datatracker.ietf.org/doc/html/rfc3986)URL 指定如何在 Web 上定位任何资源。对于 HTTPS 标准，它被定义为：

```http
https://[user:password/@<hostname.domain_name>/<resouce_path>[/resource_name][?parameter_1&parameter_2&...] 
```

用户、密码、资源路径和名称以及参数是特定于应用程序的并且是可选的。字符串“ `hostname.domain_name`”也用于编译服务器名称指示器。

除了 URL 上的内联插入之外，参数还可以像这样在 HTTP 标头中发送：

```http
POST /test HTTP/1.1
Host: www.example.com
Content-Type: application/x-www-form-urlencoded
Content-Length: 27

field1=value1&field2=value2
```

该审查很重要，因此我们可以断言 URLS 的哪些部分(如果有的话)可能无法从 HTTPS 获得加密处理。

简而言之，描述典型 HTTPS 会话的一种非常简化的方式是：

[![HTTPs 1](https://www.baeldung.com/wp-content/uploads/sites/4/2021/11/HTTPs-1.svg)](https://www.baeldung.com/wp-content/uploads/sites/4/2021/11/HTTPs-1.svg)

通信的第一部分，[即 DNS 查询](https://www.baeldung.com/cs/dns-intro)，实际上并不是 HTTPS 连接的一部分。有必要将服务器主机名翻译成相应的 IP 地址。这是可以公开服务器主机名的交易所之一。另一个发生在实际的 HTTPS 连接上。

客户端知道服务器的 IP 地址后，它会打开到服务器的 HTTPS 连接。然后，在 ClientHello 消息中，它发送它打算联系的服务器名称。

例如，让我们尝试从我们的网站下载[Baeldung 徽标。](https://www.baeldung.com/wp-content/themes/baeldung/icon/logo.svg)

如我们所见，使用[Wireshark](https://www.wireshark.org/)捕获数据交换，服务器名称以明文形式公开。但是，这是此时发送的 URL 的唯一部分：

![客户你好 1](https://www.baeldung.com/wp-content/uploads/sites/4/2021/11/ClientHello-1.png)

在 ClientHello 和 ServerHello 交换之后，端点协商在应用程序数据传输期间使用的密钥。一旦建立了密钥，所有数据流都会被加密，包括发出 URI 请求的[HTTP 方法(GET、POST、PUT、HEAD、DELETE、TRACE、PATCH)。](https://www.baeldung.com/cs/http-put-vs-patch)

正如我们所见，在不知道服务器私钥的情况下，无法直接从数据捕获中读取完整的 URL。

## 4. 那么，HTTPS URLS 是否加密？

是的，完整的 URL 字符串和所有进一步的通信都被隐藏了，包括特定于应用程序的参数。

但是，由 URL 的主机名和域名部分组成的服务器名称指示符在 TLS 协商的第一部分以明文形式发送。

服务器名称指示器用于使端点服务器可以知道连接应该寻址到哪个虚拟服务器。此信息提供了选择正确识别自身所需的证书和相应的私有加密密钥所需的知识。

除了 Server Name Indicator 之外，任何拥有服务器私钥的人(或者如果可以，以某种方式猜测)，都可以使用它来解密数据流。在 Wireshark 上，有特定的配置可以添加私钥来解密 HTTPS 流量。

当前推荐的 TLS v1.3 提议加密 ServerName。但是，由于该标准尚未获得批准，我们不能指望任何主流浏览器都支持它。上面的示例使用适用于 Windows 的 Google Chrome 及其当前版本：95.0.4638.69，64 位。

我们可以在 [Cloudflare 浏览器安全检查](https://www.cloudflare.com/en-us/ssl/encrypted-sni/)中检查我们的浏览器细节。它还可以验证安全 DNS(加密 DNS 查询)和 DNS SEC(经过身份验证的 DNS)是否支持。

## 5.代理呢？

Web 代理是拦截两个端点之间流量的网关。它们可以用于多种原因：

-   提供集中式共享缓存并最大限度地减少带宽使用
-   在网段之间创建安全或逻辑边界
-   过滤合作网络(或全国网络，例如[中国的防火墙](https://en.wikipedia.org/wiki/Great_Firewall))网络以实施安全和站点访问策略

代理可以显式配置或隐藏/透明(用户不需要配置网络代理)。

在这两种情况下，他们至少可以拦截 HTTP Connect 方法以从此 HTTP 消息中收集服务器名称。

或者，在更坏的情况下，他们可以冒充服务器，根据需要创建证书，以在[中间人架构中](https://en.wikipedia.org/wiki/Man-in-the-middle_attack)向客户端伪造他们的身份。

在这种情况下，为了不触发浏览器不安全的站点，客户端必须事先接受代理服务器证书颁发机构 (CA) 作为受信任的根或中间机构。在公司网络上，可以通过设置相应的组策略来完成某些事情，甚至无需用户明确批准。

用户可以通过验证浏览的站点证书颁发机构来查看是否属于这种情况，以查看它是常规的根证书颁发机构还是公司/国家拥有的证书颁发机构。

## 六，总结

正如我们在本教程中看到的，即使完整的 URL 在 HTTPS 连接上被加密，我们也可以假设服务器名称没有加密。DNS 和 HTTPS/TLS 都可以公开服务器名称。

此外，还有一些专门用于完全窃听数据流的技术，特别是，如果有人可以访问其中一个端点的私钥，或者它以某种方式设法将自己插入到伪造端点身份的数据流中。