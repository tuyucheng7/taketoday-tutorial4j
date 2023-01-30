## 1. 概述

在本文中，我们将详细介绍两种[密码](https://www.baeldung.com/cs/symmetric-vs-asymmetric-cryptography)算法，即[MD5](https://www.baeldung.com/java-md5)(消息摘要算法)和[SHA](https://www.baeldung.com/sha-256-hashing-java)(安全哈希算法)。我们将详细讨论它们，然后再对它们进行比较。

## 2. 密码哈希函数

首先，让我们定义一个加密哈希函数，这是上述两种算法的基本元素。加密散列函数采用可变长度输入并生成称为散列的固定大小输出。换句话说，它将任意大的输入映射到固定大小的位数组(哈希)。

[![Baeldung 示例第 2 页](https://www.baeldung.com/wp-content/uploads/sites/4/2022/01/Baeldung-Example-Page-2.svg)](https://www.baeldung.com/wp-content/uploads/sites/4/2022/01/Baeldung-Example-Page-2.svg)

密码哈希函数应该是一种单向操作。因此，使用其哈希检索数据应该是不可能的。通常，人们不应该能够从散列中猜测或检索任何有用的信息。因此，需要密码哈希函数的伪随机性。此外，加密散列函数需要具有抗冲突性。不应该有两个不同的消息产生相同的散列。

加密散列函数通常用于检查数据完整性和识别文件。比较散列比比较数据本身更容易和更快。此外，它们用于身份验证目的、在数据库中存储机密数据(例如[密码](https://www.baeldung.com/java-password-hashing))或用于密码验证。正如我们所见，加密哈希函数与应用程序或数据安全性密切相关。因此，它们应该是安全可靠的。

## 3.MD5

MD5 是一种加密散列函数，它采用任意长的数据并生成 128 位散列。虽然它被认为是密码破损的，但它仍然被广泛用于某些目的。最常见的用途之一是验证公开共享文件的完整性。MD5 算法以 512 位块的形式处理数据，这些块被分成 16 个字，每个字由 32 位组成。结果是一个 128 位散列。

让我们看看实践中的 MD5 散列。考虑以下示例：

```plaintext
MD5("The grass is always greener on the other side of the fence.") = d78298e359ac826549e3030104241a57
```

只需对输入进行简单更改(将点替换为感叹号)就会产生完全不同的散列：

```plaintext
MD5("The grass is always greener on the other side of the fence!") = 2e51f2f8daec292839411955bd77183d
```

这种特性称为[雪崩效应](https://en.wikipedia.org/wiki/Avalanche_effect)。

正如我们之前提到的，MD5 被认为是密码破损的。让我们详细谈谈它的安全性。

### 3.1. 安全

让我们回顾一下加密哈希函数最重要的属性之一：加密哈希函数需要具有抗碰撞性。 简而言之，两个输入不应该产生相同的散列。

2011 年，互联网工程任务组 (IETF) 发布了[RFC 6151](https://datatracker.ietf.org/doc/html/rfc6151)，描述了对 MD5 的可能攻击。在普通计算机上，某些攻击可能会在不到一分钟的时间内产生冲突。研究指出：

>   上述结果提供了充分的理由来消除 MD5 在需要抗碰撞的应用程序(如数字签名)中的使用。

因此，不再建议将 MD5 用于需要高安全级别的解决方案。然而，正如我们之前提到的，它被广泛用作文件的校验和。让我们考虑一个例子。独立开发者免费发布游戏。游戏文件分配有特定的哈希值。你正在从第三方网站下载游戏。如果下载文件的哈希值不同，则它不是原始文件。因此，它可能是病毒，或者文件在下载时可能已损坏(例如，由于网络问题)。

总而言之，MD5 算法存在安全漏洞，它被认为是密码破损的。如今，有更安全的算法，如 SHA-2。让我们介绍一下。

## 4. SHA-2

SHA 是广泛使用的哈希算法系列。目前主要有三个版本，分别是SHA-1、SHA-2、SHA-3。在本文中，我们将重点介绍一种流行的 SHA-2 算法。SHA-2 由使用相同算法但常数不同的不同变体组成。因此，它们产生不同长度的输出，例如 224、256 或 512 位。这些变体通常被称为 SHA-224、SHA-256、SHA-512 等。虽然，它们都是 SHA-2 的颠覆。让我们使用 MD5 部分中的示例并在实践中查看 SHA-256：

```plaintext
SHA256("The grass is always greener on the other side of the fence.") = d017bcafd6aa208df913d92796f670df44cb8d7f7b548d6f9eddcccf214ac08a
SHA256("The grass is always greener on the other side of the fence!") = a8c655db7f4d0a3a0b34209f3b89d4466332bbf2745e759e01567ac74b23a349
```

SHA2- 以其安全性着称。它用于多种用途，如加密货币、[TLS](https://www.baeldung.com/spring-tls-setup)、[SSL](https://www.baeldung.com/cs/ssl-vs-ssh)、[SSH](https://www.baeldung.com/cs/ssh-intro)、密码散列、数字签名验证。此外，法律要求在某些美国政府应用程序中使用 SHA-2，主要是为了保护机密数据。

### 4.1. 安全

我们来分析一下SHA-256算法的安全性。它是最安全和流行的哈希算法之一。首先，这是一个单向操作。因此，几乎不可能从散列重建输入。从理论上讲，暴力攻击需要![2^{256}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1f4d59abd1b91fcd49721b834e544a25_l3.svg)尝试实现这一目标。

其次，SHA-256 是抗碰撞的。这是因为存在![2^{256}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1f4d59abd1b91fcd49721b834e544a25_l3.svg)可能的散列值。因此，在实践中几乎没有碰撞的机会。

最后，SHA-256 遵循雪崩效应。输入的微小变化会产生完全不同的散列。

综上所述，SHA-256 满足加密哈希函数的所有重要要求。因此，它经常用于需要高级别安全性的应用程序中。

## 5. MD5 与 SHA-2

现在我们了解了 MD5 和 SHA-2 的基础知识。让我们比较一下。首先，MD5 产生 128 位散列。SHA-2 包含可以产生不同长度的散列的颠覆。最常见的是生成 256 位哈希的 SHA-256。

其次，SHA-2比MD5更安全，尤其是在抗碰撞方面。因此，不建议将 MD5 用于高安全性目的。另一方面，SHA-2 用于高安全性目的，例如数字签名或 SSL 握手。此外，报告的对 SHA-2 的攻击少于对 MD5 的攻击。MD5 被认为是密码破损的，可以被普通计算机攻击。

就速度而言，MD5 比 SHA-2 稍快。因此，MD5 常被用作验证文件完整性的校验和。

综上所述，在大多数情况下，SHA-2 会比 MD5 做得更好。它更安全、更可靠，而且不太可能被破坏。在速度成为主要标准之前，SHA-2 比 MD5 稍慢并不重要。SHA-2 具有产生不同长度哈希的颠覆。较长的散列意味着算法较慢。因此，SHA-256 似乎是安全性和速度之间的最佳平衡点。

## 六，总结

在本文中，我们详细讨论了 MD5 和 SHA-2 算法。然后，我们比较了两者。总结是，在大多数情况下，SHA-2 比 MD5 做得更好，尤其是在安全性方面。另一方面，MD5 可用于不需要高级别安全性且速度是主要标准的解决方案。