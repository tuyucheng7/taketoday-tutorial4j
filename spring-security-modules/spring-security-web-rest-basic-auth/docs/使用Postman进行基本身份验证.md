## 1. 概述

在本教程中，我们介绍如何使用Postman测试使用基本身份验证保护的端点。

我们介绍如何使用“Authorization”选项卡根据原始凭据生成标头。之后，我们学习如何手动操作。最后，我们将了解Postman Interceptor的工作原理以及它是如何派上用场的。

## 2. 基本认证

**基本身份验证是一种通过特殊标头保护HTTP请求的方法**：

```http
Authorization: Basic <credentials>
```

要生成credentials令牌，我们需要写入用户名和密码，并用分号字符连接。之后，我们需要使用Base64对结果字符串进行编码。

假设用户名是“admin”，密码是“tuyucheng2”。首先，我们将创建credentials字符串，即“admin:tuyucheng2”。然后，我们将使用Base64对其进行编码，添加“Basic”关键字，并将其设置为标头的值：

```http
Authorization: Basic YWRtaW46YmFlbGR1bmc=
```

## 3. Authorization选项卡

首先，让我们发送一个GET请求到一个基本身份验证保护的端点，并期望响应的状态为Unauthorized：

<img src="../assets/img.png"> 


现在，让我们添加凭据。为此，**我们只需点击“Authorization”选项卡并选择“Basic Auth”作为授权类型**，然后我们输入用户名和密码：


<img src="../assets/img_1.png">

因此，我们可以看到请求被授权并且响应码是200。此外，如果我们访问“code”链接，我们可以看到Authorization头现在是如何添加到请求中的：

```http
GET /postman-test HTTP/1.1
Host: localhost:8080
Authorization: Basic YWRtaW46dHV5dWNoZW5nMg==
Cookie: JSESSIONID=CEA488E79E1F9CDC099767C4F0F0E726
```

## 4. 手动添加标头

Postman允许我们手动添加标头，因此，**如果我们已经拥有credentials令牌，我们可以直接添加Authorization标头**。

我们可以从“Header”选项卡中执行此操作。首先，我们将“Authorization”设置为key，然后我们添加credentials令牌：


<img src="../assets/img_2.png">

如果我们检查HTTP请求，我们会发现与之前的请求没有什么不同。

## 5. Postman拦截器

Postman Interceptor是一个Chrome扩展，它允许我们将Postman应用程序绑定到浏览器会话。**换句话说，它允许Postman代表在浏览器上登录的用户执行请求**。

首先，我们需要下载并安装[Chrome扩展程序](https://chrome.google.com/webstore/detail/postman-interceptor/aicmkgpgakddgnaphhhpliifpcfhicfo)。之后，我们通过单击卫星图标从Postman应用程序中启用拦截器：

 

[![拦截器 1](https://www.baeldung.com/wp-content/uploads/2022/09/interceptor-1.png)](https://www.baeldung.com/wp-content/uploads/2022/09/interceptor-1.png)

现在，Postman应用程序与浏览器会话绑定在一起。如果我们浏览网络，我们将能够在Postman的“历史记录”选项卡中看到所有请求。但是，如果我们现在尝试执行GET请求，我们仍然会得到401 Unauthorized响应，因为我们还没有登录。

让我们使用浏览器导航到Basic Auth-secured页面：

 

[![拦截器 2](https://www.baeldung.com/wp-content/uploads/2022/09/interceptor_2.jpg)](https://www.baeldung.com/wp-content/uploads/2022/09/interceptor_2.jpg)

在我们使用浏览器弹窗登录后，我们可以回到Postman并再次执行请求。这一次，请求将被授权。

## 6. 总结

在本文中，我们了解了基本身份验证的工作原理，并介绍了使用Postman测试安全端点的各种方法。

我们演示了如何手动添加Authorization标头，以及如何使用Postman根据原始凭据生成它。最后，我们介绍了Postman Interceptor，使用它来代表从浏览器登录的用户发送请求。

