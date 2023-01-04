## 1. 概述

本教程简要概述了如何使用curl测试REST API。

curl是一个用于传输数据的命令行工具，它支持大约22种协议，包括HTTP。这种组合使它成为测试我们的REST服务的非常好的临时工具。

## 2. 命令行选项

curl支持超过200个命令行选项。我们可以有零个或多个它们伴随命令中的URL。

在将其用于我们的目的之前，让我们先看一下两个可以让我们的生活更轻松的工具。

### 2.1 冗长的

当我们进行测试时，最好将详细模式设置为：

```bash
curl -v http://www.example.com/
```

因此，这些命令提供了有用的信息，例如已解析的IP地址、我们尝试连接的端口以及标头。

### 2.2 输出

默认情况下，curl将响应主体输出到标准输出。此外，我们可以提供输出选项以保存到文件：

```bash
curl -o out.json http://www.example.com/index.html
```

当响应大小很大时，这尤其有用。

## 3. 使用curl的HTTP方法

每个HTTP请求都包含一个方法。最常用的方法是GET、POST、PUT和DELETE。

### 3.1 GET

这是使用curl进行HTTP调用时的默认方法。事实上，前面显示的示例是普通的GET调用。

在端口8082上运行服务的本地实例时，我们将使用类似于此命令的命令来进行GET调用：

```bash
curl -v http://localhost:8082/spring-rest/foos/9
```

由于我们启用了详细模式，因此我们获得了更多信息以及响应正文：

```bash
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8082 (#0)
> GET /spring-rest/foos/9 HTTP/1.1
> Host: localhost:8082
> User-Agent: curl/7.60.0
> Accept: */*
>
< HTTP/1.1 200
< X-Application-Context: application:8082
< Content-Type: application/json;charset=UTF-8
< Transfer-Encoding: chunked
< Date: Sun, 15 Jul 2018 11:55:26 GMT
<
{
  "id" : 9,
  "name" : "TuwJ"
}* Connection #0 to host localhost left intact
```

### 3.2 POST

我们使用此方法将数据发送到接收服务，这意味着我们使用数据选项。

最简单的方法是将数据嵌入命令中：

```bash
curl -d 'id=9&name=tuyucheng' http://localhost:8082/spring-rest/foos/new
```

或者，我们可以将包含请求主体的文件传递给数据选项，如下所示：

```bash
curl -d @request.json -H "Content-Type: application/json" 
  http://localhost:8082/spring-rest/foos/new
```

通过按原样使用上述命令，我们可能会遇到如下错误消息：

```json
{
    "timestamp": "15-07-2018 05:57",
    "status": 415,
    "error": "Unsupported Media Type",
    "exception": "org.springframework.web.HttpMediaTypeNotSupportedException",
    "message": "Content type 'application/x-www-form-urlencoded;charset=UTF-8' not supported",
    "path": "/spring-rest/foos/new"
}
```

这是因为curl将以下默认标头添加到所有POST请求：

```bash
Content-Type: application/x-www-form-urlencoded
```

这也是浏览器在普通POST中使用的内容。在我们的使用中，我们通常希望根据我们的需要自定义标头。

例如，如果我们的服务需要JSON内容类型，那么我们可以使用-H选项来修改我们原始的POST请求：

```bash
curl -d '{"id":9,"name":"tuyucheng"}' -H 'Content-Type: application/json' 
  http://localhost:8082/spring-rest/foos/new
```

Windows命令提示符不支持像Unix风格shell那样的单引号。

因此，我们需要用双引号替换单引号，尽管我们会在必要时尝试转义它们：

```bash
curl -d "{\"id\":9,\"name\":\"tuyucheng\"}" -H "Content-Type: application/json" 
  http://localhost:8082/spring-rest/foos/new
```

此外，当我们要发送较大量的数据时，使用数据文件通常是个好主意。

### 3.3 PUT

此方法与POST非常相似，但我们在要发送现有资源的新版本时使用它。为此，我们使用-X选项。

没有提及请求方法类型，curl默认使用GET；因此，我们在PUT的情况下明确提及方法类型：

```bash
curl -d @request.json -H 'Content-Type: application/json' 
  -X PUT http://localhost:8082/spring-rest/foos/9
```

### 3.4 DELETE

同样，我们通过使用-X选项指定我们要使用DELETE：

```plaintext
curl -X DELETE http://localhost:8082/spring-rest/foos/9
```

## 4. 自定义标头

我们可以替换默认标头或添加我们自己的标头。

例如，要更改主机标头，我们这样做：

```bash
curl -H "Host: cn.tuyucheng.taketoday" http://example.com/
```

要关闭User-Agent标头，我们输入一个空值：

```bash
curl -H "User-Agent:" http://example.com/
```

测试时最常见的情况是更改Content-Type和Accept标头。我们只需要在每个标头前加上-H选项：

```bash
curl -d @request.json -H "Content-Type: application/json" 
  -H "Accept: application/json" http://localhost:8082/spring-rest/foos/new
```

## 5. 认证

需要[身份验证](https://www.baeldung.com/spring-security-basic-authentication)的服务将发回401 – Unauthorized的HTTP响应代码和关联的WWW-Authenticate标头。

对于基本身份验证，我们可以使用用户选项简单地将用户名和密码组合嵌入我们的请求中：

```bash
curl --user baeldung:secretPassword http://example.com/
```

但是，如果我们想[使用OAuth2进行身份验证](https://www.baeldung.com/rest-api-spring-oauth2-angularjs)，我们首先需要从我们的授权服务中获取access_token。

服务响应将包含access_token：

```json
{
    "access_token": "b1094abc0-54a4-3eab-7213-877142c33fh3",
    "token_type": "bearer",
    "refresh_token": "253begef-868c-5d48-92e8-448c2ec4bd91",
    "expires_in": 31234
}
```

现在我们可以在授权标头中使用令牌：

```bash
curl -H "Authorization: Bearer b1094abc0-54a4-3eab-7213-877142c33fh3" http://example.com/
```

## 6. 总结

在本文中，我们演示了使用curl的最低限度功能来测试我们的REST服务。虽然它可以做的远比这里讨论的要多，但就我们的目的而言，这些就足够了。

请随意在命令行中键入curl -h以检查所有可用选项，用于演示的REST服务可在[GitHub上](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/spring-web-modules/spring-rest-simple)获取。