## 1. 概述

在本教程中，我们将讨论向Java HttpClient请求添加参数。

[Java HTTPClient](https://www.baeldung.com/java-9-http-client)作为Java 11的内置功能提供。因此，我们可以在不使用[Apache HttpClient](https://www.baeldung.com/httpclient-guide)和[OkHttp](https://www.baeldung.com/guide-to-okhttp)等第三方库的情况下发送HTTP请求。

## 2. 添加参数

HttpRequest.Builder帮助我们使用构建器模式轻松创建HTTP请求和添加参数。

**Java HttpClient API不提供任何方法来添加查询参数**。虽然我们可以使用第三方库，如[Apache HttpClient](https://www.baeldung.com/httpclient-guide)中的[URIBuilder](https://www.drafts.baeldung.com/apache-httpclient-parameters#add-parameters-to-httpclient-requests-using-uribuilder)来构建请求URI字符串。让我们看看只使用Java 11中添加的功能会是什么样子：

```java
HttpRequest request = HttpRequest.newBuilder()
    .version(HttpClient.Version.HTTP_2)
    .uri(URI.create("https://postman-echo.com/get?param1=value1¶m2=value2"))
    .GET()
    .build();
```

请注意，我们已将version()方法设置为使用HTTP版本2。Java HTTPClient默认使用HTTP2。但是，如果服务器不支持使用HTTP2的请求，则版本将自动降级为HTTP1.1。

此外，我们使用GET()作为默认的HTTP请求方法。如果我们不指定HTTP请求方法，将使用默认方法GET。

最后，我们还可以使用默认配置以简洁的形式编写相同的请求：

```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://postman-echo.com/get?param1=value1¶m2=value2"))
    .build();
```

## 3. 总结

在此示例中，我们介绍了如何向Java HTTPClient请求添加参数。

在示例中，我们使用了https://postman-echo.com提供的示例REST端点。