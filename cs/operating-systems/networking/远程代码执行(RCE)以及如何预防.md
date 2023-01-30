## 1. 概述

远程代码执行 (RCE) 是[OWASP](https://owasp.org/www-project-top-ten/)认可的漏洞，允许攻击者远程在目标系统上运行恶意代码。如果不加以控制，它可能会导致系统完全受损和数据丢失。

在本教程中，我们将学习远程代码执行安全漏洞的基本概念。我们将研究 RCE 攻击的工作原理和一些可能导致 RCE 的常见做法。此外，我们将学习各种防止 RCE 攻击的策略。通过了解 RCE 的风险，我们可以保护我们的基础设施和数据免受潜在威胁。

## 2. 什么是 RCE？

RCE 漏洞是通过代码注入、恶意电子邮件或附件以及操作系统中的漏洞执行的。如果实施得当，攻击者可以获得对敏感数据的完全访问权限，并有可能将恶意软件传播到目标机器。

要执行 RCE，攻击者首先要识别应用程序中的易受攻击模块。这是攻击者的经验发挥关键作用的部分。或者，他们可以运行手动测试或使用自动化工具来扫描漏洞。一旦发现易受攻击的代码，他们就会使用有效载荷来利用它。

让我们考虑一个场景，黑客需要在网站中发现漏洞。

首先，他们首先将易受攻击的有效负载注入网站的搜索栏。此外，他们会制作包含恶意代码的搜索查询并将其发送到网站。收到搜索请求后，服务器将执行恶意代码。最终，这允许黑客访问网站的数据库和客户信息。

然后黑客可以泄露敏感数据，例如客户姓名、地址和信用卡号：

![img](https://www.baeldung.com/wp-content/uploads/sites/4/2023/01/rce1.png)

他们还可以在网站上植入恶意软件以感染更多用户。在这种情况下，网站未能正确验证用户输入导致了 RCE 漏洞。结果，黑客获得了对系统和敏感数据的未授权访问。

## 3. RCE 是如何工作的？

至此，我们对RCE漏洞有了基本的了解。现在让我们用一个例子来理解 RCE 的工作原理。当应用程序未正确清理或验证输入字段时，可以注入 RCE。考虑以下用于登录表单的[PHP代码：](https://www.php.net/)

```bash
<?php
$username = $_POST['username'];
$password = $_POST['password'];

$query = "SELECT  FROM users WHERE username='$username' AND password='$password'";
$result = mysqli_query($conn, $query);

if (mysqli_num_rows($result) > 0) {
// login successful
} else {
// login failed
}
?>
```

上面的 PHP 代码是一个登录脚本，用于使用给定的用户名和密码验证用户的存在。考虑使用恶意用户输入的攻击者，例如'; 系统('rm -rf /')；//，那么SELECT查询将如下所示：

```bash
SELECT  FROM users WHERE username='''; system('rm -rf /'); //' AND password=''
```

上述查询将删除所有文件，有效地危及[安全性](https://www.baeldung.com/category/security-2)。为了避免此类[攻击](https://www.baeldung.com/cs/active-vs-passive-attacks-security)，清理和验证用户输入至关重要。

RCE 也可以通过语言解释器漏洞引入。例如，如果我们使用具有任何已知漏洞的 PHP 版本，攻击者可能会通过执行任意代码来利用该漏洞。

## 4.执行RCE的不同方式

远程代码评估和存储代码评估是执行 RCE 的两种不同方法。

在远程代码评估中，攻击者将恶意代码注入 Web 应用程序。易受攻击的代码可以通过搜索栏等用户输入字段注入并远程执行。这允许攻击者访问服务器上的敏感数据。或者，他们也可以发送利用漏洞的恶意电子邮件附件。

执行 RCE 的另一种方法是使用存储代码评估。攻击者将恶意软件发送到系统以稍后执行。该恶意软件未经适当验证就存储在数据库中。稍后，当真正的客户端请求网页时，服务器会将其与不受信任的代码一起从数据库中提取并执行：

![img](https://www.baeldung.com/wp-content/uploads/sites/4/2023/01/rce2.png)

这两种方法的主要区别在于注入代码的位置和执行时间。RCE 的两种方法都允许攻击者在目标系统上执行任意代码。但是，两种方法注入代码的具体位置和时间不同。

### 4.1. 远程代码评估的类型

根据注入代码的位置，可以存在三种类型的 RCE 攻击：[服务器端注入](https://owasp.org/www-community/attacks/Server-Side_Includes_(SSI)_Injection)、[客户端注入](https://owasp.org/www-community/attacks/Code_Injection)和shell 注入。

服务器端注入攻击涉及将易受攻击的代码注入 Web 应用程序或数据库，以便在服务器上执行它。这种类型的攻击针对用户输入字段，例如搜索框或登录表单。没有正确验证或清理输入的应用程序更容易受到服务器端注入的攻击。

客户端注入攻击将恶意代码注入客户端应用程序，例如 Web 浏览器。这是通过利用客户端软件漏洞的恶意广告、电子邮件或网站来完成的。

Shell 注入攻击将恶意代码注入到服务器运行的 Shell 中。这是通过允许命令执行的用户输入字段或通过操作系统或网络设备中的现有漏洞来完成的。

## 5. 预防和缓解策略

有多种预防和缓解策略可以帮助防止远程代码执行 (RCE) 攻击。

首先，确保正确验证用户输入。这包括过滤掉任何潜在的恶意字符或代码，并仅在验证后处理数据：

```bash
if (preg_match('/[^a-zA-Z0-9]/', $username) || preg_match('/[^a-zA-Z0-9]/', $password))
{
    exit(); 
}
```

此验证使用正则表达式只允许在用户名和密码字段中使用字母数字。此外，此验证消除了使用特殊字符利用代码的可能性。

还有一些处理 RCE 的策略：

-   遵守安全编码实践有助于降低自定义代码中出现 RCE 漏洞的风险。例如，我们可以避免使用[eval()](https://www.php.net/manual/en/function.eval.php)和其他有风险的函数。
-   定期更新软件、[Web 服务器](https://www.baeldung.com/java-servers)、[数据库](https://www.baeldung.com/java-in-memory-databases)和[操作系统](https://www.baeldung.com/cs/os-kernel)是避免 RCE 的另一个好习惯。
-   使用[HTTPS](https://www.baeldung.com/java-https-client-certificate-authentication)和[SFTP等安全通信协议可以通过](https://www.baeldung.com/java-file-sftp)[加密](https://www.baeldung.com/java-aes-encryption-decryption)数据和防止篡改来防止 RCE 攻击。
-   使用强密码和实施多因素身份验证可以让攻击者更难获得对系统和资源的未授权访问，从而有助于防止 RCE 攻击。
-   定期扫描漏洞有助于在利用之前识别和解决 RCE 漏洞。

上述步骤不会查明易受攻击的代码，但有助于减少源代码中的易受攻击区域。

### 5.1. 检测 RCE 的工具

有几种工具可以检测 RCE 漏洞：

-   [漏洞扫描工具](https://www.baeldung.com/cs/vulnerability-pen-testing)可以扫描系统和网络以查找已知漏洞，包括 RCE 漏洞。
-   应用程序安全测试工具有助于在应用程序级别测试 Web 应用程序。
-   我们可以使用网络安全监控工具来[监控网络流量](https://www.baeldung.com/linux/monitor-network-usage)是否存在可疑活动。
-   安全专业人员可以使用渗透测试工具来模拟 RCE 攻击并测试组织针对这些攻击的防御措施。

定期使用这些工具作为综合漏洞管理计划的一部分是一种很好的做法，以便及时识别和解决 RCE漏洞。

## 六，总结

在本文中，我们了解了远程代码执行漏洞。它允许攻击者远程在目标系统上执行任意代码。首先，我们探索了不同类型的 RCE 攻击，包括服务器端注入攻击和客户端注入攻击。

此外，我们检查了执行 RCE 的两种主要方法：远程代码评估和存储代码评估。我们讨论了这些方法之间的差异，并提供了每种方法的示例。最后，我们介绍了几种预防和缓解策略，以保护 Web 应用程序免受 RCE 攻击。