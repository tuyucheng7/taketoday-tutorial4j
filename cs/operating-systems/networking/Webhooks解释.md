## 1. 概述

应用程序可以使用两种方法来接收有关另一个应用程序中发生的特定事件的实时信息：轮询和网络挂钩。

在本教程中，我们将学习这两种方法，然后更详细地了解 webhook。

## 2.轮询和Webhooks

首先，让我们看看它们是什么：

### 2.1. 轮询

在此方法中，客户端 API 定期向服务器 API 发送 HTTP 请求，并询问是否发生了特定事件。事件发生后，服务器 API 会用指示事件已发生的信息进行响应。此信息也称为负载并发送到客户端 API：

![轮询](https://www.baeldung.com/wp-content/uploads/sites/4/2022/10/Polling.png)

### 2.2. 网络钩子

当服务器应用程序上发生特定事件时，Webhook 会向客户端 API 发送消息(有效负载)：

![网钩](https://www.baeldung.com/wp-content/uploads/sites/4/2022/10/Webhook.png)

## 3. 更多关于 Webhooks

现在让我们更详细地了解 webhooks：

### 3.1. 比较 Webhook 和投票

现在让我们比较两种方法：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a4055fd421243b5e44285d63c9c1e9db_l3.svg)

通常，webhook 比轮询提供更多优势。

### 3.2. Webhook 的用例

任何需要有关另一个应用程序上发生的特定事件的实时信息的应用程序都可以使用 webhooks：

-   如果我们想在我们的 web 应用程序中接受在线支付，我们可以使用 webhooks 接收实时数据，以查看用户是否已在另一个应用程序(如 PayPal)上成功完成支付
-   实时接收有关特定事件的通知。例如，当另一个应用程序发生特定事件时，在 Slack 上接收通知

### 3.3. Webhook 是如何工作的？

Webhook 需要客户端的 webhook URL 才能将 HTTP 请求发送到客户端 API。HTTP 请求通常是 POST 请求，需要在客户端的后端进行解释：

 

![Webhook 如何](https://www.baeldung.com/wp-content/uploads/sites/4/2022/10/Webhook-How.png)

### 3.4. 我们如何保护 Webhook？

Webhook 将数据发送到客户端应用程序中的 Webhook URL。此外，webhook URL 对公众可用，这意味着除了 webhook 服务器之外，恶意用户还可以向客户端应用程序发送虚假数据。为了增加连接的安全性，我们可以：

-   强制在服务器和客户端之间建立 HTTPS 连接。
-   服务器只能使用客户端和服务器持有的密钥对消息进行哈希处理。这样，一旦客户端收到有效负载和哈希输出，它就可以验证服务器是否实际发送了有效负载。为了验证，客户端再次使用相同的散列函数和相同的私钥，并检查输出是否与服务器发送的输出匹配。

## 4。总结

在本文中，我们了解了在应用程序之间发送有关实时事件的信息的不同方法。