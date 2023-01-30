## 1. 概述

在本教程中，我们将详细讨论[RSA](https://www.baeldung.com/java-rsa) SecurID 令牌。首先，我们将详细介绍对 RSA 算法至关重要的[非对称密码学。](https://www.baeldung.com/cs/symmetric-vs-asymmetric-cryptography)然后，我们将讨论 RSA 令牌的一般工作原理、何时使用以及它们现在是否足够安全。

## 2. 非对称密码学

RSA算法是一种非对称密码算法，具体来说是一种公钥密码体制。非对称密码术是一种密码系统，它使用一对密钥来加密和解密数据。这些键在数学上是相连的。

第一个是私钥，用于解密消息。收件人将私钥安全地存储在自己身边，不与任何人共享。第二个是公钥，用于加密消息。收件人与任何有兴趣发送消息的人共享公钥：

[![img 624db089adf15](https://www.baeldung.com/wp-content/uploads/sites/4/2022/04/img_624db089adf15.svg)](https://www.baeldung.com/wp-content/uploads/sites/4/2022/04/img_624db089adf15.svg)

正如我们提到的，两个键在数学上是相关的。通常，使用单向函数。因此，很容易加密数据，但如果没有私钥就很难或几乎不可能解密。此外，算法的设计使得根据公钥获取私钥比较复杂。

非对称密码术对于在各种应用程序和协议中使用的许多现代密码系统至关重要，例如[TLS](https://www.baeldung.com/spring-tls-setup)、S/MIME、[数字签名](https://www.baeldung.com/java-digital-signature)、RSA SecurID 令牌。另一方面，非对称加密比对称加密慢得多。因此，非对称加密对于某些目的来说通常太慢了。因此，现代解决方案使用结合了非对称和对称密码学的混合方法。

## 3.RSA SecurID 令牌

RSA SecurID 令牌是一种执行各方[双因素身份验证的机制。](https://www.baeldung.com/spring-security-two-factor-authentication-with-soft-token)它可以是软件(应用程序、电子邮件或 SMS 传送)或硬件([密钥卡设备](https://en.wikipedia.org/wiki/RSA_SecurID#/media/File:RSA_SecurID_SID800.jpg))。该机制以固定的时间间隔(例如 60 秒)生成验证码。为进行身份验证，当事人有义务输入与特定令牌相关的凭据和实际有效的身份验证代码。

SecurID 令牌使用称为种子的内置时钟和工厂编码密钥来生成随机身份验证代码。RSA SecurID 服务器包含一个活动令牌和相关种子的数据库。此外，它还使用实时时钟。在身份验证期间，服务器知道 RSA SecurID 机制实际应该提供什么代码，并通过将其与用户输入的内容进行比较来执行身份验证：

[![img 624db08b24838](https://www.baeldung.com/wp-content/uploads/sites/4/2022/04/img_624db08b24838.svg)](https://www.baeldung.com/wp-content/uploads/sites/4/2022/04/img_624db08b24838.svg)

RSA SecurID 机制为身份验证过程带来了额外的安全层，并降低了数据泄露的可能性。虽然，它仍然容易受到一些危险和攻击。让我们描述一下。

### 3.1. 漏洞

正如我们提到的，RSA SecurID 令牌是一种双因素身份验证机制。因此，它为身份验证过程增加了额外的安全性。另一方面，有一些理论上的攻击会影响 RSA 令牌的安全性。

首先，最基本的问题是具有集成 RSA 功能的令牌设备或激活的智能手机可能会丢失或被盗。此外，任何可以窃取凭据的攻击也可以窃取身份验证代码。

其次，RSA SecurID 令牌不能抵抗中间人类型的攻击。简而言之，中间人攻击是指攻击者将自己插入认为直接连接的两方之间。因此，攻击者可以拦截或修改传输的数据。

接下来，攻击者总有可能闯入RSA服务器，获取种子等token相关数据。在这种情况下，令牌变得毫无价值。实际上，在 2011 年，当攻击者窃取了 RSA SecurID 令牌的机密数据时[，对 RSA 公司进行了一次成功的网络攻击。](https://www.wired.com/story/the-full-story-of-the-stunning-rsa-hack-can-finally-be-told/)攻击者影响了数以千计的重要客户，包括美国政府。

最后，还会出现更[具体的漏洞](https://www.cvedetails.com/vulnerability-list/vendor_id-334/product_id-1646/RSA-Securid.html)，例如特定应用程序、系统或硬件版本中的安全问题。

## 4。总结

在本文中，我们详细讨论了 RSA SecurID 令牌。如今，有很多替代方案和类似的解决方案，例如著名的移动应用程序，如 Authy 或 Google Authenticator。通常，硬件令牌正在被替换。通常，首选基于软件的解决方案，如提到的移动应用程序或 SMS 或电子邮件验证码交付。