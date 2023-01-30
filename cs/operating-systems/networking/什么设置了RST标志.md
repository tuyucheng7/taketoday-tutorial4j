## 1. 概述

[传输控制协议 ( TCP](https://www.baeldung.com/cs/popular-network-protocols) ) 负责通过连接的网络传输文件或消息。它使用[标志](https://www.baeldung.com/cs/tcp-protocol-syn-ack)来指示连接状态并提供故障排除信息。特别是，只要[TCP 数据包](https://www.baeldung.com/cs/tcp-max-packet-size)不符合协议的连接标准，就会设置重置标志 (RST) 。

在本教程中，我们将讨论 RST 标志的最常见原因。但首先，我们将一般性地讨论 TCP 标志。

## 2. TCP标志

TCP 标志是 TCP 标头中的二进制位：

![突出显示 RST 标志的 TCP 标头](https://www.baeldung.com/wp-content/uploads/sites/4/2022/04/TCP-Header-1-1024x417.png)

它们传达连接的状态并有助于故障排除。它们中的每一个都在 TCP 通信中扮演着重要的角色，例如发起和关闭连接或携带数据。在这里，我们将讨论六个重要的控制标志。

[SYN](https://www.baeldung.com/cs/tcp-protocol-syn-ack)标志(同步)用于启动连接。我们设置 ACK 标志以确认数据包的安全接收以及发起/拆除请求。[RST](https://www.baeldung.com/cs/tcp-fin-vs-rst)标志表示请求的无连接状态或不符合状态。FIN 标志表示连接中断。源和目标都发送 FIN 数据包以正常连接终止。

如果接收方可以将发送的数据直接传递给应用程序而不是缓冲，我们设置[PSH标志。](https://packetlife.net/blog/2011/mar/2/tcp-flags-psh-and-urg/)紧急指针 (URG) 标志表示数据包具有优先级。通常，接收方使用队列处理数据包。但是，带有活动 URG 标志的数据包会脱离队列。 

## 3.发送RST标志的原因

TCP RST 标志表示我们应该立即终止连接。发生这种情况的原因有很多，在这里，我们将讨论最常见的原因。稍后，我们将讨论两个示例。

### 3.1. 不存在的 TCP 端口

大多数情况下，如果客户端发起到一个不存在的地址的连接，我们会收到“连接被拒绝”的错误。该错误告诉我们 TCP 连接的目标 IP 地址未在侦听。如果目标端口不存在或应用程序未在目标服务器上运行，则可能会发生这种情况。

### 3.2. 中止连接

假设我们正在中止正在运行的应用程序。这将触发通信过程的中断，并向远程服务器发送一个 RST 标志，并显示错误“Connection closed by peer”。

### 3.3. 异步连接

假设客户端处于活动状态，但服务器端在客户端不知情的情况下处于非活动状态。这可能是由于物理层的中断擦除[传输控制块 (TCB)](https://www.baeldung.com/cs/tcp-active-vs-passive)中的数据。假设服务器重新启动。由于缺少较早连接的历史记录，它会发送 RST 以响应客户端的请求。

### 3.4. 侦听端点队列已满

我们知道监听[端点](https://www.baeldung.com/cs/active-vs-passive-ftp)有一些队列。假设队列没有比到达的连接请求更快地被释放，在这种情况下，RST 被发送到新连接。

### 3.5. 时间等待暗杀

假设服务器收到一个 ACK 以响应它发送给客户端的 FIN 请求，并且客户端现在处于 Time-Wait 状态。在某些情况下，由于延迟，客户端会从服务器接收到旧的 SEQ 和 ACK 标志。作为响应，客户端向服务器发送 ACK。由于服务器没有关于已关闭连接的信息，它向客户端发送 RST。

### 3.6. 受限制的本地 IP 地址

假设出于安全原因，我们将侦听端点指定为 IP 地址 162.254.206.227:2106 而不是任何本地地址，例如 :2106。

如果客户端向 162.254.206.227:2106 以外的任何 IP 地址发送请求，服务器会将 RST 发送回客户端。

### 3.7. 传输中的防火墙

有时， 由于客户端或服务器的凭据不匹配，[防火墙](https://www.baeldung.com/cs/firewalls-intro) 被配置为发送 RST。如果穿过防火墙的 TCP SYN 与现有的防火墙会话匹配，则防火墙将提升 RST 标志。如果防火墙会话在端点不知情的情况下过期，则端点将发送 RST。 

### 3.8. TCP 缓冲区溢出

与快速 LAN 连接配对的慢速 WAN 链接上的 TCP 加速可以触发该标志。在这种情况下，LAN 填充[缓冲区](https://www.baeldung.com/cs/tcp-flow-control-vs-congestion-control)的速度快于端点通过较慢的 WAN 耗尽缓冲区的速度。

## 4. 复位序列

为了解释 RESET 序列，我们将首先回顾 TCP 连接的两个重要阶段：建立和关闭它。

### 4.1. 建立和关闭连接

使用 SYN、SYN+ACK 和 ACK 数据包实现3次[握手](https://www.baeldung.com/cs/popular-network-protocols)以建立新的 TCP 连接：

![使用 3 次握手建立新的 TCP 连接](https://www.baeldung.com/wp-content/uploads/sites/4/2022/04/Establish-New-TCP-Connection-1024x309.png)

当客户端发送FIN并且服务器以FIN + ACK响应时，我们将能够通过 3 次握手优雅地终止已建立的连接。客户端以活动的ACK 标志进行往复。因此，正常关闭需要来自 TCP 连接的每个端点的 FIN - ACK 对：

 ![使用 3 次握手优雅地关闭 TCP 连接](https://www.baeldung.com/wp-content/uploads/sites/4/2022/04/Closing-TCP-Connection-1024x309.png)

### 4.2. 使用 RST 标志关闭连接

然而，一个连接可能会被服务器或客户端通过发送一个RST标志给另一个而突然关闭。

让我们考虑客户端向服务器发送 RST。客户端不期望服务器响应ACK，服务器也不需要发送FIN / ACK交换来终止连接： 

![使用 RST 标志突然关闭 TCP 连接](https://www.baeldung.com/wp-content/uploads/sites/4/2022/04/RST-Sequence-1024x309.png)

从一端触发RST标志的结果导致向另一端请求突然停止通信。在此过程中，传输中的数据包会被丢弃。RST 标志的接收端必须停止与另一端的所有通信。 

## 5. TCP重置攻击

现在让我们谈谈一种攻击类型，在这种攻击中，黑客向服务器发送带有活动 RST 标志的恶意 TCP 数据包。这些攻击会破坏服务器的运行，从而导致[分布式拒绝服务 (DDoS)](https://us.norton.com/internetsecurity-emerging-threats-what-is-a-ddos-attack-30sectech-by-norton.html)。

当服务器收到带有活动 RST 的 TCP 段时，TCP 重置攻击可以终止 TCP 对话。例如：

1.  黑客向网络托管服务器发送 SYN 请求，
2.  服务器发送 SYN-ACK 以响应每个 SYN 请求并等待三次握手。
3.  黑客可以将带有 TCP RST 的欺骗性数据包发送到 TCP 连接的一个或两个端点，从而导致通信突然关闭。

视觉上：

![恶意客户端的 TCP RESET 攻击](https://www.baeldung.com/wp-content/uploads/sites/4/2022/04/TCP-RST-attack2a-1024x651.png)

这使得服务器立即停止连接。

## 六，总结

在本文中，我们讨论了 TCP 数据包中的 RST 标志。在多种情况下，RST 标志从一端发送到另一端，导致对话突然终止、半开 TCP 连接和不存在的服务器。

怀有恶意的黑客经常使用 TCP RST 标志来造成破坏，例如关闭网站和拒绝真正的用户访问网站。为了保证 TCP 连接的连续性和安全性，我们应该不断检查即将到来的 TCP 标志。