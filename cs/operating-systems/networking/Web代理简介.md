## 1. 概述

许多公司环境使用代理服务器来更好地控制其 Internet 流量。此外，一些代理服务器甚至可以缓存 Internet 资源，从而减少带宽需求。

在本教程中，我们将介绍 Web 代理、它们是什么、它们如何工作以及它们的一些类型。

## 2. 什么是代理服务器？

在深入研究代理客户端配置之前，让我们回顾一些关键概念。代理服务器是一种软件解决方案，充当客户端和其他服务服务器之间的中介。客户端必须连接到代理服务器并要求它将请求跳到实际服务器，而不是直接到达服务器。

对代理服务器的需求可以追溯到现代互联网的早期。那时，一旦管理员开始意识到自由、不受控制的信息流动可能会导致滥用。因此，他们创建了过滤进出特定路由的互联网流量的方法。这些方式就是我们现在所知道[的防火墙](https://www.baeldung.com/cs/antivirus-vs-firewall)。

一般来说，防火墙可以使用数据包过滤来实现，比如[iptables](https://www.baeldung.com/linux/iptables-intro)，或者通过代理服务器。它们之间的主要区别在于它们必须执行什么样的操作。

### 2.1. 数据包过滤

数据包过滤的目的是快速、轻资源，并且通常在内核级别运行。因此，他们可以应用什么样的规则有一些限制。

第一个数据包过滤器只处理将规则应用于[网络和传输层](https://www.baeldung.com/cs/osi-transport-vs-networking-layer)，即通过 ACL 规则控制 IP 地址和 TCP/UDP 端口。[如今，它们可以在OSI 模型](https://www.baeldung.com/cs/osi-model)层上走得更高，直至应用层，从而提供更精细的控制。

### 2.2. 代理

另一方面，代理是在用户空间级别实现的，可以应用非常复杂的规则。例如，他们可以完全识别用户、应用程序、协议操作、过滤内容等。他们甚至创建企业级缓存，以减少带宽使用。此外，他们可以进行查找以检查某些策略是否允许任何目标服务访问。如果是这样，在什么条件下？

例如，我们可以允许游戏服务器在午餐时间在一定的带宽限制内访问一组用户。 

### 2.3. 代理如何工作

下图显示了常规互联网访问代理服务器的示意图，详细说明了连接与非代理连接的比较：

[![图像显示直接连接与代理连接对比](https://www.baeldung.com/wp-content/uploads/sites/4/2023/01/img_63bdb4e6c3a34.svg)](https://www.baeldung.com/wp-content/uploads/sites/4/2023/01/img_63bdb4e6c3a34.svg)

## 3. 代理类型

与任何复杂的软件一样，代理有很多分类。让我们看看一些更重要的。

### 3.1. 数据流向

根据数据流，代理服务器可以是：

-   转发、出站或直接代理通常用于过滤或控制 组织内部生成的指向外部服务器的流量。在本教程中，我们将专注于配置客户端软件以使用此代理类。在这个类别中，我们会找到像[Squid](http://www.squid-cache.org/)、[Privoxy](https://www.privoxy.org/)、[Tinyproxy](https://tinyproxy.github.io/)这样的软件包，甚至像带有[mod_proxy](https://httpd.apache.org/docs/current/mod/mod_proxy.html)和[Nginx的](https://www.baeldung.com/linux/nginx-multiple-proxy-endpoints)[Apache](https://httpd.apache.org/)这样的成熟网络服务器。此外，一些匿名工具(例如[tor](https://www.torproject.org/))通过实施代理服务器来工作。甚至[ssh 都有自己的代理模式](https://www.baeldung.com/linux/ssh-tunneling-and-proxying)！
-   反向或入站代理，专用于保护内部服务免受出站流量的影响。他们可以从实际服务器卸载任务。常见的应用程序是静态内容托管、数据流压缩、加密或解密(用于 TLS 或 SSL)、会话身份验证或负载平衡。在这个类别中，我们会找到 HAProxy、Apache、Nginx 和 Kubernetes [Ingress](https://www.baeldung.com/ops/kubernetes-ingress-vs-load-balancer)等软件。 有关反向代理的更多信息，请查看[本教程](https://www.baeldung.com/cs/proxy-vs-reverse-proxy)

### 3.2. 支持的协议

关于支持的协议，代理服务器可以是：

-   单一协议或面向应用程序， 当它们是为特定协议或服务而设计时
-   多协议，他们可以连接到多个目标系统

### 3.3. 部署类型

然后，对于部署，它们可以是：

-   透明：在这种情况下，网络默认网关拦截传出数据包并强制它们通过代理服务器
-   通过[Web 代理自动发现](https://en.wikipedia.org/wiki/Web_Proxy_Auto-Discovery_Protocol)( WPAD) 自动发现：网络管理员创建一个[代理自动配置文件](https://findproxyforurl.com/)(PAC)，这是一个类似 javascript 的脚本，它通知兼容的客户端如何找到代理服务器。此配置可能使用[DHCP](https://www.baeldung.com/cs/dhcp-intro)或[DNS](https://www.baeldung.com/cs/dns-intro)查询来提供托管 PAC 文件的 URL
-   由 Microsoft Group Policies 等公司范围的管理工具自动部署
-   手动配置，用户必须提供代理设置

最后，代理可以要求或不需要用户认证。同样，可以部署的身份验证方法与 Web 服务器通常可用的方法相同。

大多数操作系统，如 Windows、[Linux](https://www.baeldung.com/linux/run-and-update-linux-behind-proxy-servers)和 Mac OS 都有自己的代理配置方法。但是，用户级应用程序应该有内置的代理支持，或者必须使用额外的帮助软件。

## 4.代理URI

与任何 Internet 资源一样，代理服务器在[通用资源定位符 (URL)](https://www.baeldung.com/cs/uniform-resource-identifiers)中进行描述。常见的代理 URL 格式为：

```http
<Schema>://[<user>[:<password>]@]<Host|IP address>[.<Domain>]:<Port>/
```

模式与用于访问代理的协议有关。通常的模式是：

-   HTTP 或 HTTPS
-   袜子

此外，根据代理服务器版本(4、4a 或 5)和一些选项，SOCKS 模式可以具有后缀。例如，架构socks5h指定服务器必须执行 DNS 解析。

## 5. HTTPS 流

现在我们可能会想：如果没有代理连接的端到端连接，SSL 如何通过代理工作？

正如我们在[HTTPS 教程](https://www.baeldung.com/cs/https-urls-encrypted)中看到的，加密的安全连接协商发生在客户端和服务器之间。因此，一旦数据流被加密，它就不能被任何中间节点泄露，包括代理。

因此，标准是，一旦客户端到达代理，它就会向代理发送 CONNECT 命令。然后，代理打开一个新的服务器连接并在两个连接之间中继数据包。然后，一旦 SSL/TLS 协商结束，代理就无法掌握会话内容。因此，许多有用的代理功能，如缓存、数据压缩和内容过滤，都无法实现。

另一种方法是将代理配置为这些连接[的中间人。](https://www.baeldung.com/cs/security-mitm)在这种模式(也称为 SSL 碰撞)中，代理使用临时生成的签名证书模拟目标服务器。在那种情况下，没有简单的中继，实际上有两个 SSL 连接：客户端到代理和代理到服务器。此外，为此，客户端必须信任代理用来签署证书的证书颁发机构。

但是SSL bumping 在允许代理的全部功能的同时，还允许对任何用户内容进行完全访问。代理可以读取(和记录)密码、信用卡号、个人信息和其他所有内容。

不用说，绝对不建议使用这种模式，因为在大多数情况下，它的风险远远大于收益。

## 6. Web 代理自动发现——PAC 文件和 WPAD

许多组织寻求易于配置和部署的代理系统。透明代理的问题在于它们更难在网络级别进行配置。透明代理在边界路由器或防火墙上增加了更多开销，并增加了代理服务器本身的复杂性。

这就是 Web 代理自动发现 (WPAD) 应运而生的时候。 它由两个组件组成，代理自动配置文件 (PAC) 和 Web 代理自动发现 (WPAD) 协议。

### 6.1. 代理自动配置文件

[PAC 文件是类似 javascript 的](https://findproxyforurl.com/)代码，它们从一组规则中派生出要使用的代理。他们实现了一个名为FindProxyForURL 的函数。此函数返回用于给定 URL 或主机的代理，或者如果与目标的连接必须是直接的。

PAC 文件可以使用[函数](https://findproxyforurl.com/pac-functions/)来获取客户端的元数据，例如 IP 地址和网络。此外，还有一些函数可以进行 DNS 查找，以获取当前日期和时间。这有助于为许多情况创建精确的规则。

但是，由于 PAC 文件是在运行时解释的，因此它们会为每个请求增加一点延迟。此外，PAC 文件上的错误可能会导致难以解决的问题。调试 PAC 文件并不那么容易，因此某些浏览器具有特定的调试模式来执行此操作。

### 6.2. Web 代理自动发现 

它提供有关客户端应去哪里查找相关代理自动配置 (PAC) 文件的广告。通告可以使用 DHCP 或 DNS。

对于 Windows 桌面，它在网络配置窗格中默认启用。此外，它的设置非常易于使用组策略进行部署。[Linux 目前只支持使用图形界面](https://www.baeldung.com/linux/run-and-update-linux-behind-proxy-servers)。

## 七、总结

正如我们在本教程中看到的，代理服务器是企业网络安全的宝贵工具，许多组织依靠它们来降低风险。它们可以提供额外的安全性和性能优势。

了解它们的工作原理和不同种类对于选择部署公司范围代理的最佳方式至关重要。此外，如果它们没有按预期工作，进行故障排除也很重要。 