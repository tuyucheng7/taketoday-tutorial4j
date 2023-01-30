## 1. 概述

在本教程中，我们将了解如何在使用 VPN 时检查和修改查询的 DNS 服务器。

## 2.定义

域名系统 (DNS) 是将人类可记忆的域名转换为相应的数字互联网协议 (IP) 地址的系统。DNS 服务器包含公共 IP 地址及其关联主机名的数据库。如果 DNS 名称服务器拥有查询域的记录，则它是“权威的”；否则，如果它使用临时缓存而不拥有记录，则为“非权威”。

[nslookup](https://en.wikipedia.org/wiki/Nslookup)是用于 Windows、macOS 和 Linux 的命令行实用程序，用于查询 Internet 域名服务器。[dig](https://en.wikipedia.org/wiki/Dig_(command))是另一个用于 macOS 和 Linux 的命令行工具，它提供比nslookup更多的信息，显示查询的名称服务器的答案。Android 和 iPhone 在商店中有相同的实用程序。不管怎样，我们都可以使用[nslookup.io](https://www.nslookup.io/)等在线工具。

VPN 代表“虚拟专用网络”，是使用公共网络时受保护的网络连接。VPN 服务通过将我们的 Internet 流量传输到远程服务器来隐藏我们的实际 IP 地址。它有助于在公共 WLAN 内安全浏览，绕过 Internet 提供商和政府的控制和限制，或视频流或社交网络的区域禁令。但是，使用 VPN 并不意味着 DNS 流量总是绕过不需要的检查。

## 3. 分离模式或全隧道 VPN

简而言之，我们可以将 VPN 服务分为两类。

“全隧道”VPN 通过 VPN 路由和加密所有互联网流量。因此，DNS 请求也被加密并且不受 Internet 提供商的控制。另一方面，无法访问本地网络资源。

需要“分离模式”VPN 才能同时访问 VPN 可用的本地资源和远程资源。例如，它在工作环境中用于划分企业的 VPN 流量和其他个人非 VPN 流量。目前还不清楚在这种情况下 DNS 请求是如何处理的。

[![VPN 隧道(拆分模式和全隧道)](https://www.baeldung.com/wp-content/uploads/sites/4/2022/04/vpn_tunnel.png)](https://www.baeldung.com/wp-content/uploads/sites/4/2022/04/vpn_tunnel.png)

然而，这个总结不足以描述 VPN 服务不同配置的复杂性。由于 VPN 的高度可配置性，VPN 的种类和样式千变万化。查询哪些 DNS 服务器完全取决于服务器和客户端的配置方式。

这就是为什么我们需要工具来调查每一种可能的情况。在以下示例中，我们假设我们使用的是最新版本的操作系统。

## 4. 获取 DNS 客户端配置的命令行工具

让我们继续检查我们的机器正在使用哪些 DNS 服务器。正如稍后解释的那样，请记住，如果存在透明的 DNS 代理，这可能与现实不符或可能误导我们。

有时我们可能没有足够的信息，例如，DNS 服务器可能是我们的路由器，实际使用的 DNS 将取决于路由器配置。在虚拟机的情况下，虚拟网络适配器和虚拟 LAN 的设计可以隐藏实际 DNS 服务器的地址。

在 macOS 和 Linux 上，/ etc/ [resolv.conf](https://en.wikipedia.org/wiki/Resolv.conf)列出了用于查找 DNS 名称的名称服务器。但是，Linux 网络管理器不会更新resolv.conf，并且大多数 macOS 进程(包括 Web 浏览器)都不使用它。实际上，我们最好忽略这个文件。

### 4.1. 视窗

[ipconfig](https://docs.microsoft.com/en-us/windows-server/administration/windows-commands/ipconfig) /all显示所有当前的 TCP/IP 网络配置。输出是一个参数列表，我们对以“DNS 服务器”开头的行感兴趣。

### 4.2. 苹果系统

[scutil](https://ss64.com/osx/scutil.html) –dns报告当前的 DNS 配置，但输出非常冗长。我们可以过滤它，立即得到我们感兴趣的信息： scutil –dns | grep '名称服务器[[0-9]]'。

### 4.3. Linux

[nmcli](https://linux.die.net/man/1/nmcli)是一个命令行工具，用于控制 NetworkManager 并获取其状态。它的输出包含多个部分，包括“DNS 配置”。

## 5. DNS 泄漏和透明 DNS 代理

DNS 劫持(也称为 DNS 投毒或 DNS 重定向)和 DNS 欺骗是破坏 DNS 查询解析的做法。除了这些最终用户难以补救的问题外，还有 DNS 泄漏。

使用 VPN 时，如果 DNS 请求未受保护地到达 DNS 服务器，通常是 Internet 服务提供商 (ISP) 的 DNS 服务器，则会发生 DNS 泄漏。DNS 泄漏导致的可识别个人身份的 DNS 数据包对安全和隐私构成严重威胁，尤其是在审查制度严格且可能对个人造成影响的国家。

此外，一些 ISP 使用“透明 DNS 代理”来拦截所有 DNS 查询请求并代理结果。换句话说，他们强迫我们在我们不知情的情况下使用他们的 DNS 服务。

这就是为什么我们需要一种可靠的方法来测试我们实际使用的 DNS 服务器。

### 5.1. DNS 泄漏测试器的工作原理

DNS 泄漏测试器通常是网站或命令行脚本。他们都与使用其域的权威 DNS 服务器和内部数据库的服务器联系，以发现其访问者使用的 DNS 服务器。

更详细地说，测试人员生成查询以假装随机生成的唯一子域下的资源。该子域可以是 UUID(通用唯一标识符)，例如779298b9-57ac-40a3-9359-a482b113203a。任何计算机都可以生成 UUID，几乎可以肯定该标识符不会与已经创建或将要创建的标识符重复。

要从此唯一子域检索资源，浏览器或脚本需要子域的 IP 地址。由于测试人员以前从未使用过该子域的名称，因此除了权威服务器之外，没有任何 DNS 服务器可以知道 IP 地址。所以用户使用的非权威DNS服务器向测试者的权威DNS服务器要IP。

权威 DNS 服务器将随机生成的域名的唯一部分 (UUID) 与内部数据库中查询的非权威 DNS 服务器的 IP 进行匹配。此外，测试人员将用户的公共 IP 与查询的 UUID 配对。通过连接这两个共享相同 UUID 的数据库表，测试人员获得用户使用的非权威 DNS 服务器。如果它是不需要的 DNS 服务器(如 ISP 的服务器)，则存在 DNS 泄漏。

附带说明一下，很明显，如果站点所有者可以控制权威 DNS 服务器，则任何网站都可以检测到我们正在使用的 DNS。一个“好奇”的网站可以使用 DNS 泄漏来识别我们的实际 ISP，而不考虑我们的 VPN，从而查看我们的大致地理位置。对于地理上受阻的内容，这是一个问题，在最坏的情况下，可能会阻碍我们隐藏身份。

### 5.2. 用于测试 DNS 泄漏的开源脚本

Github [macvk/dnsleaktest](https://github.com/macvk/dnsleaktest)存储库提供了一个脚本来测试 DNS 泄漏。它在适用于 Linux 的 Bash 和 Python 以及适用于 Windows 的 macOS 和 Powershell 中可用。对于所有三个操作系统，还有预构建的可执行二进制文件。在后台，此脚本连接到[bash.ws/dnsleak](https://bash.ws/dnsleak)。

[D7EAD/Faucet](https://github.com/D7EAD/Faucet)是另一个类似的 GitHub 存储库，包含 C++ 源代码和适用于 Windows 的预编译可执行文件。在幕后，它还连接到bash.ws。

### 5.3. 用于测试 DNS 泄漏的第三方服务

按字母顺序排列，一些在线工具是[bash.ws](https://bash.ws/dnsleak)、[browserleaks.com](https://browserleaks.com/ip)、[dnscheck.tools](https://dnscheck.tools/#advanced)、[dnsleak.com](https://dnsleak.com/)、[dnsleaktest.com](https://dnsleaktest.com/)、[ipleak.net](https://www.perfect-privacy.com/en/tests/dns-leaktest)、[perfect-privacy.com](https://ipleak.net/)等等。此列表绝不是详尽无遗的，因为我们可以在任何搜索引擎上找到范围广泛的类似服务：所有这些都是提供一键式测试的网站。

另一种选择是适用于 Android的[DNS 泄漏测试应用程序。](https://play.google.com/store/apps/details?id=ws.bash.dnsleak)我们还可以在 WordPress 中集成一个 DNS 泄漏测试器，感谢[macvk/vpn-leaks-test](https://github.com/macvk/vpn-leaks-test)，或者在 Kodi 中，感谢[Space2Walker/plugin.program.dnsleaktest](https://github.com/Space2Walker/plugin.program.dnsleaktest)。

### 5.4. 如何实现用于 DNS 泄漏测试的服务器

如果我们不想被第三方服务所束缚，就得自己搭建服务器。nhdms [/dns-leak-server](https://github.com/nhdms/dns-leak-server)存储库包含设置 Linux 服务器及其 DNS 管理以检查 DNS 泄漏的说明。

## 6.使用VPN时修改DNS

由于 VPN 客户端种类繁多，我们需要在本次讨论中缩小潜在客户端的配置范围。因此我们假设使用[官方 OpenVPN 客户端的默认安装](https://openvpn.net/vpn-client/)，它支持任何 OpenVPN 协议兼容的服务。它适用于 Windows、macOS、Linux、Android 和 iOS。

实际上，此客户端适用于提供扩展名为.ovpn的文件的任何 VPN 服务。许多商业 VPN 服务提供一组.ovpn文件作为其专有客户端的替代方案。

假设我们要使用 Cisco OpenDNS 主服务器 208.67.222.222。默认情况下，在高级设置中，如果 VPN 隧道未定义任何 VPN DNS 服务器，OpenVPN 客户端将使用 Google DNS 服务器作为后备。但它不提供强制使用自定义 DNS 的选项。

要修改 DNS，我们只需使用纯文本编辑器手动编辑.ovpn文件，例如 Windows 上的记事本、macOS 上的 Aquamacs、Gedit 或 Linux 上的 Xed。在<ca>标记之前，让我们添加选项register-dns和block-outside-dns(如果不存在)。第一个强制客户端优先选择已配置的 DNS 服务器，而不是它可能从 DHCP 接收到的任何其他服务器。第二个防止 DNS 泄漏到配置服务器以外的任何服务器。然后让我们添加选项dhcp-option DNS 208.67.222.222。

让我们看一个例子(不是xxxx，而是VPN 服务器的 IP 地址，后跟端口号)。在这种情况下，协议 TCP 和端口 443 用于欺骗防火墙，假装 VPN 流量是 HTTPS 流量：

```plaintext
client
dev tun
proto tcp
remote x.x.x.x 443
resolv-retry infinite
nobind
persist-key
persist-tun
remote-cert-tls server
auth SHA512
cipher AES-256-CBC
ignore-unknown-option block-outside-dns
block-outside-dns
verb 0
log /dev/null
status /dev/null
register-dns
dhcp-option DNS 208.67.222.222
<ca>
```

我们现在可以断开 VPN 并将新的.ovpn文件导入客户端(如果我们愿意，还可以删除当前的 VPN 配置文件)。连接到新配置文件后，我们可以使用[dnsleaktest.com](https://dnsleaktest.com/)快速检查使用的 DNS 服务器。它们应该是 Cisco OpenDNS 服务器；它们的 IP 可能会有所不同，因为 DNS 查询分布在全球数据中心之间。

此外，流行的和最新的 Linux 发行版不需要 OpenVPN 客户端。通过.ovpn文件支持 OpenVPN 连接已经集成在网络管理器小程序中，具有更改 DNS 的能力。

## 七、总结

在本教程中，我们了解了 DNS 在 VPN 连接期间的实际使用方式，这可能与预期不同。我们看到了如何检测它们并改变它们。

这种深入探索帮助我们理解了为什么我们的隐私很快就会失控。然而，DNS 服务器只是要牢记的众多方面之一，因为在关键监视环境中进行匿名浏览不仅涉及技术方面，还涉及行为方面。