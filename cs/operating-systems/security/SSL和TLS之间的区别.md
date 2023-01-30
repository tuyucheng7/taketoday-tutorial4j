## 1. 概述

在本教程中，我们将讨论为数据传输提供安全连接的两种协议：SSL 和 TLS。我们将探讨协议的工作过程。

最后，我们将介绍它们之间的核心区别。

## 2. HTTP和HTTPS简介

我们使用[超文本传输协议 (HTTP)](https://www.baeldung.com/cs/popular-network-protocols)通过 Internet 传输网站数据。HTTP 以明文格式发送数据。只要我们不通过 Internet 发送敏感的个人数据(包括信用卡号和密码)，这种明文格式就可以使用。如果发件人发送的数据未加密，则坐在中间的黑客可以访问敏感信息。

[HTTPS](https://en.wikipedia.org/wiki/HTTPS)或安全 HTTP 的引入是为了解决 HTTP 中的安全问题。在 HTTPS 中，我们可以在计算机和服务器之间以加密形式发送数据。因此，坐在中间的黑客只能提取加密数据。不过，为了查看原始消息，黑客需要发件人安全存储的解密密钥。

此外，为了增强安全性，现在没有敏感信息的网站都使用 HTTPS。我们使用不同的端口号进行不安全和安全的通信。例如，HTTP 通常使用标准端口 80，而 HTTPS 使用端口 443。

## 3. SSL和TLS简介

[安全套接字层 (SSL)](https://www.baeldung.com/java-ssl)和[传输层安全性 (TLS)](https://en.wikipedia.org/wiki/Transport_Layer_Security)在 Internet 和本地网络上的两个设备之间提供安全连接。此外，它们还提供一种机制来加密在设备和服务器之间传输的数据。此外，加密机制有助于防止未经授权访问敏感数据。

SSL 和 TLS 都提供身份验证、加密和数据完整性检查。连接到服务器的 Web 浏览器通常使用 SSL 和 TLS。但是，安全版本协议(包括 HTTPS、FTPS、SMTPS)使用 TLS over SSL。

TLS 是 SSL 的后继者。TLS 通过使用[非对称加密](https://www.baeldung.com/cs/symmetric-vs-asymmetric-cryptography)密钥提供数据验证。网站发送一个称为 SSL 证书的[数字证书](https://www.baeldung.com/import-cer-file-into-truststore)来标识自己。一旦 Web 浏览器检查了证书，数据传输就会继续进行。

通常，在HTTP请求中，服务器主要提供认证。此外，在某些情况下，客户端也可以提供身份验证，但这是可选的。此外，客户端验证服务器的数字证书以确保其为正版网站。

SSL/TLS 使用对称密码术对交换的数据进行加密以保护隐私。此外，它还有助于确保数据完整性。我们使用标签或[消息验证码 (MAC)](https://www.baeldung.com/java-mac-address)检查数据完整性。

TLS 是 SSL 的较新版本，具有显着的相似性，但仍存在一些差异。由于相似性，TLS 有时也称为 SSL/TLS。现在让我们谈谈 SSL 和 TLS 的历史。

SSL 1.0 于 1995 年由[Netscape](https://en.wikipedia.org/wiki/Netscape)开发，从未发布。SSL 2.0 于 1995 年发布。更新版本 SSL 3.0 在一年后的 1996 年发布。所有 SSL 版本都被弃用，主要原因是存在安全漏洞。

TLS 1.0 于 1999 年由[IETF](https://en.wikipedia.org/wiki/Internet_Engineering_Task_Force)发布。它于 2006 年更新为 TLS 1.1。此外，TLS 1.2 于 2008 年发布，随后于 2018 年发布了 TLS 1.3。此外，TLS 1.0 和 1.1 版本现已弃用。截至 2022 年，TLS 1.2 和 1.3 是唯一未弃用的 TLS 版本：

![无标题图](https://www.baeldung.com/wp-content/uploads/sites/4/2022/03/Untitled-Diagram.drawio-2.png)

## 4. SSL/TLS 握手过程

由于现在所有 SSL 版本都已弃用，让我们谈谈 TLS 握手过程。

首先，握手发生在客户端和服务器之间。最初，客户端发送一条问候消息、客户端支持的 TLS 版本列表以及用于启动握手过程的加密算法。因此，服务器会在选择的 TLS 版本、加密算法及其数字证书(包括公钥)的旁边回复一条问候消息。

一旦客户端收到服务器的响应，它就会验证服务器的数字证书，并共享一个用服务器的公钥加密的密钥，以使其免受黑客攻击。接下来，服务器使用其私钥解密密钥。此时，客户端和服务端都拥有了加密和解密数据的密钥。

而且，现在客户端将经过MAC和哈希认证的加密数据发送给服务器。一旦服务器收到数据，它就会开始解密和认证过程。成功完成后，服务器会向客户端响应一条加密且经过身份验证的消息。最后，客户端使用其密钥对数据进行解密和验证。这就是 SSL/TLS 握手的工作原理。

现在，如果客户端或服务器在任何时候未能解密或验证数据，握手就会失败。现在让我们看一下图表，它简要介绍了握手过程：

![握手](https://www.baeldung.com/wp-content/uploads/sites/4/2022/03/handshake.png)

TLS 使用基于[哈希的消息身份验证代码 (HMAC)](https://www.baeldung.com/java-hmac)进行数据完整性检查。TLS 1.3 使用[关联数据的认证加密 (AEAD)](https://en.wikipedia.org/wiki/Authenticated_encryption)，它提供加密和认证。SSL 使用基于 [消息验证码 (MAC)的](https://en.wikipedia.org/wiki/Message_authentication_code)[MD5 和 SHA1](https://www.baeldung.com/cs/md5-vs-sha-algorithms)。

此外，TLS 提供了一组与 SSL 不同的密码套件。与以前的 TLS 版本相比，TLS 1.3 密码套件放弃了各种遗留算法以提供更高的安全性。SSL 一般使用[Fortezza 密码套件](https://en.wikipedia.org/wiki/Fortezza)。

## 5. SSL 和 TLS 的区别

让我们看看 SSL 和 TLS 之间的核心区别：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e8f5632ee2ec9cd0fc7d1797a6aabad1_l3.svg)

## 六，总结

在本教程中，我们讨论了 TLS 和 SSL 协议。我们用图表解释了工作过程。

最后，我们介绍了它们之间的核心区别。