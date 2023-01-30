## 1. 概述

当客户端通过 Web 与服务器通信时，此过程由超文本传输协议 ( [HTTP](https://www.baeldung.com/cs/http-status-codes) ) 启用。HTTP 是客户端和服务器之间的请求-响应协议。

GET 和 POST 方法是两种最常见的[HTTP 请求方法](https://www.baeldung.com/java-http-request)。它们用于检索数据或将数据发送到服务器。它们是客户端-服务器模型的组成部分，该模型使客户端和服务器之间能够通过万维网 (WWW) 进行通信。

在本教程中，我们将看到这两种方法之间的区别：

## 2. GET方法

GET 用于从指定资源请求数据。它可以检索客户端的任何可见数据，例如 HTML 文档、图像和视频：

![获取方法的图像](https://www.baeldung.com/wp-content/uploads/sites/4/2023/01/Get-Method.jpg)
要发送 GET 请求，客户端需要指定它要检索[的资源的 URL 。](https://www.baeldung.com/java-url-vs-uri)然后将请求发送到服务器，服务器处理请求并将请求的数据发送回客户端。

## 3.POST方法

POST 将数据发送到服务器以创建或更新资源。例如，它通常用于向服务器提交 HTML 表单：

![POST 方法的图像](https://www.baeldung.com/wp-content/uploads/sites/4/2023/01/POST-Method.jpg)
要发送 POST 请求，客户端需要指定要向其发送数据的资源的 URL 和数据本身。然后将请求发送到服务器，服务器处理请求并将响应发送回客户端。

POST 方法通常用于提交表单或将文件上传到服务器。

## 4. GET 和 POST 的区别

### 4.1. 能见度

使用 GET 时，数据参数包含在 URL 中并且对所有人可见。但是，使用 POST 时，数据不显示在 URL 中，而是显示在 HTTP 消息体中。

### 4.2. 安全

GET 不太安全，因为 URL 包含发送的部分数据。另一方面，POST 更安全，因为参数不存储在 Web 服务器日志或浏览器历史记录中。

### 4.3. 缓存

GET 请求可以缓存并保留在浏览器历史记录中，而 POST 请求则不能。这意味着 GET 请求可以添加书签、共享和重新访问，而 POST 请求不能：

![缓存-GET-VS-POST](https://www.baeldung.com/wp-content/uploads/sites/4/2023/01/Cache-GET-POST.jpg)

### 4.4. 服务器状态

GET 请求旨在从服务器检索数据并且不修改服务器的状态。另一方面，POST 请求用于将数据发送到服务器进行处理，并可能修改服务器的状态。

### 4.5. 传输的数据量

GET 方法有最大字符数限制，而 POST 方法没有这样的限制。这是因为GET方法通过资源URL发送数据，有长度限制，而POST方法通过HTTP报文体发送数据，没有长度限制。

### 4.5. 数据类型

GET方法只支持字符串数据类型，而POST方法支持字符串、数值、二进制等[不同的数据类型。](https://www.baeldung.com/java-primitives#:~:text=2.-,Primitive Data Types,%2C double%2C boolean and char.)

## 5.总结

在本文中，我们研究了 GET 和 POST 方法之间的区别。这两种方法对于通过 WWW 进行客户端-服务器通信都是必不可少的，但具有不同的目的和限制。根据客户端和服务器的特定需求选择合适的方法很重要。