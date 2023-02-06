## 1. 概述

在这个简短的教程中，我们将了解基本身份验证。我们将了解它是如何工作的，并配置[Java HttpClient](https://www.baeldung.com/java-9-http-client)以使用这种身份验证。

## 2. 基本认证

**基本身份验证是一种简单的身份验证方法。客户端可以通过用户名和密码进行身份验证**。这些凭据以特定格式在Authorization HTTP标头中发送。它以Basic关键字开头，后跟一个base64编码的username:password值。冒号字符在这里很重要。标头应严格遵循此格式。

例如，要使用tuyucheng用户名和HttpClient密码进行身份验证，我们必须发送此标头：

```shell
Basic YmFlbGR1bmc6SHR0cENsaWVudA==
```

我们可以通过使用base64解码器并检查解码结果来验证它。

## 3. Java HttpClient

[Java 9引入了一个新的HttpClient](https://www.baeldung.com/java-9-http-client)**作为Java 11中标准化的孵化模块**。我们将使用Java 11，因此我们可以简单地从java.net.http包中导入它，而无需任何额外的配置或依赖项。

让我们先执行一个没有任何身份验证的简单GET请求：

```java
HttpClient client = HttpClient.newHttpClient();

HttpRequest request = HttpRequest.newBuilder()
    .GET()
    .uri(new URI("https://postman-echo.com/get"))
    .build();

HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

logger.info("Status {}", response.statusCode());
```

首先，我们创建一个HttpClient，它可以用来执行HTTP请求。其次，我们使用[构建器设计模式](https://www.baeldung.com/creational-design-patterns#builder)创建一个HttpRequest。[GET方法](https://www.baeldung.com/java-9-http-client#2-specifying-the-http-method)设置请求的HTTP方法。[uri方法](https://www.baeldung.com/java-9-http-client#1-setting-uri)设置我们希望发送请求的URL。

之后，我们使用我们的客户端发送请求。send方法的第二个参数是[响应体处理程序](https://www.baeldung.com/java-9-http-client#1-handling-response-body)。这告诉客户端我们希望将响应正文视为String。

让我们运行我们的应用程序并检查日志。输出应如下所示：

```shell
INFO cn.tuyucheng.taketoday.httpclient.basicauthentication.HttpClientBasicAuthentication - Status 200
```

**我们看到HTTP状态是200，这意味着我们的请求是成功的**。在此之后，让我们看看我们如何处理身份验证。

## 4. 使用HttpClient Authenticator

在我们配置身份验证之前，我们需要一个URL来测试它。让我们使用需要身份验证的[Postman Echo端点](https://learning.postman.com/docs/developer/echo-api/)。首先，将之前的URL更改为此并再次运行应用程序：

```java
HttpRequest request = HttpRequest.newBuilder()
    .GET()
    .uri(new URI("https://postman-echo.com/basic-auth"))
    .build();
```

让我们检查日志并查找状态代码。这次我们收到了HTTP状态401“未授权”。此响应代码意味着端点需要身份验证，但客户端未发送任何凭据。

让我们更改我们的客户端，以便它发送所需的身份验证数据。**我们可以通过配置HttpClient Builder来做到这一点，我们的客户端将使用我们设置的凭据**。该端点接受用户名“postman”和密码“password”。让我们为我们的客户端添加一个身份验证器：

```java
HttpClient client = HttpClient.newBuilder()
    .authenticator(new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication("postman", "password".toCharArray());
        }
    })
    .build();
```

让我们再次运行应用程序。现在请求成功，我们收到HTTP状态200。

## 5. 使用HTTP标头进行身份验证

我们可以使用另一种方法来访问需要身份验证的端点。**我们从前面的章节中了解了Authorization标头是如何构造的，因此我们可以手动设置它的值**。尽管这必须根据请求完成，而不是通过身份验证器设置一次。

让我们删除身份验证器，看看我们如何设置请求标头。我们需要使用[base64编码](https://www.baeldung.com/java-base64-encode-and-decode#1-java-8-basic-base64)构造标头值：

```java
private static final String getBasicAuthenticationHeader(String username, String password) {
    String valueToEncode = username + ":" + password;
    return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
}
```

让我们为Authorization标头设置此值并运行应用程序：

```java
HttpRequest request = HttpRequest.newBuilder()
    .GET()
    .uri(new URI("https://postman-echo.com/basic-auth"))
    .header("Authorization", getBasicAuthenticationHeader("postman", "password"))
    .build();
```

我们的请求是成功的，这意味着我们正确地构造和设置了标头值。

## 6. 总结

在这个简短的教程中，我们了解了什么是基本身份验证以及它是如何工作的。我们通过为Java HttpClient设置身份验证器来使用具有基本身份验证的Java HttpClient。我们通过手动设置HTTP标头来使用不同的方法进行身份验证。