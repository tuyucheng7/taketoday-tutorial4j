## 1. 概述

[TCP](https://www.baeldung.com/cs/udp-vs-tcp)旨在将某种可靠的面向连接的数据流作为目标。这样，它就可以克服一些尽力而为的 IP 协议设计限制。从这个意义上讲，它具有[流量和拥塞控制](https://www.baeldung.com/cs/tcp-flow-control-vs-congestion-control)、优先级标记、错误检测和恢复功能。如果需要，它还可以将大数据包分成碎片。在这种情况下，片段将在目的地重新组装。

所以我们知道TCP在某种程度上应该能够检测到错误。它是如何做到的？它的效果如何？检测会失败吗？如果是这样，我们如何解决它？

在本教程中，我们将探索这些问题并讨论一些方法来提高我们的 TCP 依赖系统的健壮性，并提供更好的错误检测甚至纠正。

## 2. TCP数据包中的校验和

下表显示了 TCP 数据包标头。正如我们所看到的，有一个名为Checksum 的字段。该字段用于存储[16 位校验和](https://www.baeldung.com/cs/crc-vs-checksum)。它是使用 IP 标头部分[、TCP 标头](https://www.baeldung.com/cs/messages-payload-header-overhead)(假设校验和字段为零)和数据包的有效负载的补码计算得出的。

使用补码的校验和在当时并不是最稳健的错误检测算法。但是，评估速度非常非常快，几乎没有内存开销。速度和内存复杂性是互联网协议诞生之初的紧迫问题：

```
  
```

当接收端检测到校验和不匹配时，它会丢弃接收到的数据包。结果，发件人将永远不会收到其确认。因此，发送方将在超时后重试发送数据包。这是 TCP 的[三次握手](https://www.baeldung.com/cs/handshakes)预期行为。因此，与 TCP/IP 上的任何类型的数据丢失一样，这将对数据流造成滞后和随机延迟。

## 3.碰撞

one 的补码可能是第一个开发的主要哈希算法。评估哈希算法时出现的一个重要概念是[Collision](https://www.baeldung.com/cs/simple-hashing-vs-salted-hashing)。当两个不同的对象评估为完全相同的哈希码时，就会发生冲突。当哈希码用于错误检测或唯一标识任何对象表示时，这尤其有害。

在任何依赖[散列](https://www.baeldung.com/cs/simple-hashing-vs-salted-hashing)的应用程序中，冲突意味着系统可能会被误认为两条不同的信息是相同的。例如，密码通常存储为某种哈希。在这种情况下，冲突意味着攻击者可能获得访问权限，不仅可以通过猜测确切的密码，还可以通过猜测计算结果为相同哈希码的任何其他字节序列。这样，暴力破解可能需要更少的尝试。

无论如何，TCP 使用 16 位校验和，这有什么好处吗？事实上，主要的校验和批评是生成相同哈希码的数据包差异非常低。数据包上 16 个不同位的任何倍数都会导致相同的校验和。更健壮的算法将需要完全不同的包来创建碰撞。

请注意，在发生冲突的情况下，如果更改的位位于数据包标头上，则接收方可能会出现一些行为，从有效载荷数据中出现一些乱码到根本不接收数据。但实际上，实际风险取决于数据包流经的 Internet 路由的可靠性。具有低误码率的更高质量链路可降低在同一数据包中错误检测到 16 位的倍数的几率。

## 4. 应用层变通方案

### 4.1. 改进错误检测

当然，在某些应用程序中，我们可能不希望有丝毫错误未被检测到的机会。在这种情况下，我们可以在[应用层](https://www.baeldung.com/cs/osi-model)消息中添加更健壮的错误检测。我们可以使用相对快速且可靠的[循环冗余校验——CRC32](https://www.baeldung.com/cs/crc-vs-checksum)算法(例如用于 ZIP 文件)。此外，为了实现更高的保护，我们可以使用更强大的哈希算法，如 SHA-256(与某些已知加密货币使用的相同)。

检测越稳健，开销越大，延迟和计算能力就越高。应用层 CRC 保护的一个很好的例子是[HTTP 压缩算法(在 RFC 7231 中定义)](https://httpwg.org/specs/rfc7231.html#header.content-encoding)。如果应用程序使用我们自己设计的应用层协议，则[添加普通 CRC](https://www.baeldung.com/java-checksums)、[受 CRC 保护的数据压缩](https://www.baeldung.com/java-compress-and-uncompress)甚至[加密哈希代码](https://www.baeldung.com/sha-256-hashing-java)并不困难。

在选择哈希算法时，我们必须决定我们需要走多远才能确保所需的保护。曾经被认为是安全的算法，如[MD5](https://www.baeldung.com/cs/md5-vs-sha-algorithms)，早已被破解。有一些工具可以快速创建与目标哈希代码冲突的字节序列(请参阅[hashcat](https://hashcat.net/hashcat/)和[johntheripper](https://www.openwall.com/john/))。

另一种选择是使用[流控制传输协议 – SCTP(在 RFC 4960 中定义)](https://datatracker.ietf.org/doc/html/rfc4960)。[旨在取代 TCP，](https://www.baeldung.com/cs/tcp-udp-vs-sctp)它使用 CRC32 哈希码具有更好的容错能力。然而，[它的使用并没有实现炒作](https://en.wikipedia.org/wiki/Stream_Control_Transmission_Protocol#Motivation_and_adoption)。而且，由于它不常用，我们可能会遇到通过具有严格配置的防火墙保护网络的问题。

### 4.2. 改进纠错

我们可以走得更远，为数据添加冗余，不仅可以检测错误，还可以使用称为[前向纠错](https://en.wikipedia.org/wiki/Error_correction_code#Forward_error_correction)的技术在不需要重传的情况下进行恢复。由于可以设计纠错算法来任意定义所需的鲁棒性，鲁棒性越强，数据冗余和开销就越多。

顺便说一句，TCP 上最常见的数据包错误肯定会导致网络内核子系统丢弃数据包。所以他们会强制重传。为了避免任何重传，我们应该使用允许我们更好地控制其错误检测机制的传输协议。

例如，我们可以使用[UDP](https://www.baeldung.com/cs/udp-vs-tcp)并在没有可选校验和的情况下直接在应用程序上制作数据包。之所以需要，是因为现代操作系统在默认情况下会填充 UDP 上可选的 16 位校验和字段。

## 5.总结

在本教程中，我们已经看到，事实上，在 TCP 上错误检测失败的可能性相对较小。但是，由于大多数应用程序层协议或文件类型已经增加了额外的安全性，因此在常见情况下这并不是真正的问题。

无论如何，如果我们确实需要稳健性保证或正在设计我们自己的关键任务应用程序，我们可以根据任何要求任意提高其可靠性，前提是我们可以负担相关的开销。