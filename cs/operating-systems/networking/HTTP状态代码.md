## 1. 概述

在本教程中，我们将探索我们在浏览互联网时每天都会处理的事情。HTTP 状态代码是 HTTP 标准的一部分，也是计算机如何通过万维网相互通信的一部分。我们将了解不同类别的 HTTP 状态代码，并深入了解一些最常用的状态代码的含义。

## 2. 首先，什么是HTTP？

在我们讨论 HTTP 状态代码之前，让我们快速回顾一下 HTTP 是什么。HTTP 代表超文本传输协议，是一种详细说明网络通信方式的标准。HTTP 协议最初由 Tim Berners-Lee 发明，后来由互联网工程任务组 (IETF) 的 HTTP 工作组开发。HTTP 协议的版本以 RFC(Request For Comment)文档的形式发布，我们可以通过在[RFC Editor 网站](https://www.rfc-editor.org/search/rfc_search_detail.php?title=http&pubstatus[]=Any&pub_date_type=any)上搜索找到更多详细信息。

HTTP 采用客户端(例如浏览器)和服务器之间的请求和响应的形式。当我们单击链接、在浏览器中键入 URL 或提交表单时，浏览器会向服务器发起 HTTP 请求，告诉该服务器我们希望它做什么。例如，两台计算机之间的典型 HTTP 请求/响应通信可能如下所示：

客户要求：

```http
GET / HTTP/1.1 
Host: www.google.com
```

服务器响应：

```http
HTTP/1.1 200 OK 
Date: Sat, 12 Sep 2020 14:26:56 GMT 
Expires: -1 
Cache-Control: private, max-age=0 
Content-Type: text/html; charset=ISO-8859-1 
Set-Cookie: ...truncated... 
Accept-Ranges: none 
Vary: Accept-Encoding 
Transfer-Encoding: chunked 

(html)
```

这就是当我们在浏览器中输入[https://www.google.com](https://www.google.com/)时在幕后发生的事情。响应的第一行是状态行，其中包含正在使用的 HTTP 版本、HTTP 状态代码及其关联的原因短语。在本例中，状态代码为 200，原因短语为 OK。我们将在下一节中了解这意味着什么。

## 3. HTTP 状态码

根据 IETF，HTTP 状态代码是一个三位整数代码，给出了服务器尝试理解和满足请求的结果。状态代码不适用于人类用户；相反，它们旨在让客户进行相应的解释和行动。客户端可以是浏览器、另一个服务器或发出 HTTP 请求的任何应用程序。

HTTP状态码根据状态码的第一位分为5类：

-   1xx——信息——收到请求，继续处理
-   2xx——成功——请求被成功接收并被接受
-   3xx——重定向——需要采取进一步的行动来完成请求
-   4xx – 客户端错误 – 请求包含错误的语法或无法实现
-   5xx——服务器错误——服务器未能满足明显有效的请求

通过这种理解，我们可以立即推断出上面的示例请求是服务器成功处理的请求。

接下来，让我们看一下我们可能会遇到的一些最常用的 HTTP 状态码。

### 3.1. 200 好

200 状态代码表示请求成功并且可能是以下任何操作的结果：[GET](https://www.baeldung.com/java-http-request)、[POST](https://www.baeldung.com/httpclient-post-http-request)、[PUT、PATCH](https://www.baeldung.com/http-put-patch-difference-spring)和 DELETE。当服务器响应 200 时，响应还必须包含适合该操作的负载。例如，对 GET 请求的 200 响应的负载通常包含请求的资源，如请求的 Html 页面。

### 3.2. 201 创建

201 响应表示请求成功并导致创建新资源。可能导致 201 状态代码的请求示例是当我们提交表单以创建新资源时。

### 3.3. 301 找到

301 响应告诉我们我们请求的资源已永久移动。301 响应必须包含带有新位置 URI 的Location标头。然后客户端可以使用此位置在新位置成功重复请求。

### 3.4. 400 错误请求

此状态代码表示发送到服务器的 HTTP 请求语法不正确。例如，如果我们提交了一个格式不正确的数据或错误的数据类型的表单，这可能会导致 400 响应。

### 3.5. 401未经授权

这意味着试图通过发送请求来访问资源的用户未通过正确的身份验证。根据 HTTP 标准，当服务器响应 401 时，它还必须发送一个WWW-Authenticate标头，其中包含服务器使用的身份验证方案列表。

### 3.6. 403禁止访问

403 响应通常与 401 相混淆；但是，403 响应意味着服务器由于访问或权限不足而拒绝我们的请求。即使我们通过了身份验证，也可能不允许我们提出特定请求。

### 3.7. 404 未找到

当找不到请求的资源时返回此状态代码。例如，如果我们输入一个网站上不存在或因某种原因被删除的页面的 URL，则会导致 404 响应。

看一下这个例子，我们尝试访问一个不存在的页面：

```http
POST /test HTTP/1.1 
Host: www.google.com
```

这将导致来自 Google 的 404 状态代码：

```http
HTTP/1.1 404 Not Found 
Content-Type: text/html; charset=UTF-8 
Referrer-Policy: no-referrer 
Content-Length: 1565 
Date: Sat, 12 Sep 2020 16:38:02 GMT 

(html)
```

### 3.8. 405 方法不允许

405 响应表示服务器不支持请求中使用的方法。例如，如果端点仅接受 GET 请求，而我们尝试发送 POST，则会产生 405 响应代码。如果是这种情况，服务器还必须发回一个包含允许方法列表的Allow标头字段。

例如，向 Google 发出以下请求：

```http
DELETE / HTTP/1.1 
Host: www.google.com
```

将导致响应：

```http
HTTP/1.1 405 Method Not Allowed 
Allow: GET, HEAD 
Date: Sat, 12 Sep 2020 15:39:35 GMT 
Content-Type: text/html; charset=UTF-8 
Server: gws 
Content-Length: 1591 
X-XSS-Protection: 0 
X-Frame-Options: SAMEORIGIN 

(html)
```

### 3.9. 408请求超时

此状态代码是服务器指示请求消息未在服务器准备等待的时间内完成的指示。如果客户端发送 HTTP 请求的速度太慢，则可能会发生这种情况。这可能是由于多种原因造成的，包括互联网连接速度慢。

### 3.10. 413请求实体太大

此状态代码告诉我们服务器无法或不愿处理请求，因为请求负载太大。如果我们尝试上传太大且超出服务器限制的文件，就会发生这种情况。

### 3.11. 500内部服务器错误

内部服务器错误意味着由于某种原因，服务器由于错误而无法处理请求。这通常是由于服务器上的错误或未处理的异常造成的。

### 3.12. 429 请求过多

有时，服务可能希望限制对其资源的请求数量。429 响应代码向客户端指示已超过允许的请求数。这对于限制用户速率甚至 API 货币化非常有用。

### 3.13. 418我是茶壶

418 状态代码于[1998 年 4 月作为愚人节笑话](https://tools.ietf.org/html/rfc2324))发布。它尚未注册为官方响应代码，但已被许多框架和库(如 Node.js 和 ASP.Net)用作“彩蛋”笑话. 事实上，我们甚至可以访问https://www.google.com/teapot来查看实际效果。

## 4. 未分配的 HTTP 状态代码

HTTP 状态码是可扩展的，我们上面列出的只是众多正在使用的状态码中的一小部分。状态代码的完整列表由 IANA 维护，可以在其 HTTP 状态代码注册表中查看。为了支持尽可能多的应用，建议使用官方注册的状态码；但是，有一系列未分配的状态代码，服务可以使用它们来扩展已注册的状态代码：

```http
104 to 199
209 to 225
227 to 299
309 to 399
418 to 420
427
430
432 to 450
452 to 499
509
512 to 599
```

## 5.总结

在本文中，我们了解了 HTTP 状态代码以及它们如何成为用于在 Web 上通信的 HTTP 标准的重要组成部分。我们检查了一些最常用的状态代码，并简要介绍了尚未正式使用的未分配状态代码的范围。