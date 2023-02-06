## 1. 概述

在本教程中，我们将探讨如何使用Java HttpClient连接到HTTPS URL。我们还将学习如何将客户端与没有有效SSL证书的URL一起使用。

在旧版本的Java中，我们更喜欢使用Apache HTTPClient和OkHttp等库来连接服务器。在Java 11中，JDK中添加了[改进的HttpClient库](https://www.baeldung.com/java-9-http-client)。让我们探索如何使用它通过SSL调用服务。

## 2. 使用Java HttpClient调用HTTPS URL

我们将使用测试用例来运行客户端代码。出于测试目的，我们将使用在HTTPS上运行的现有URL。

让我们编写代码来设置客户端并调用服务：

```java
HttpClient httpClient = HttpClient.newHttpClient();
    
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://www.google.com/"))
    .build();

HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
```

在这里，我们首先使用HttpClient.newHttpClient()方法创建了一个客户端。然后，我们创建了一个请求并设置了我们要访问的服务的URL。最后，我们使用HttpClient.send()方法发送请求并收集响应-一个HttpResponse对象，其中包含作为字符串的响应主体。

当我们将上面的代码放入测试用例并执行下面的断言时，我们会观察到它通过了：

```java
assertEquals(200, response.statusCode());
```

## 3. 调用无效的HTTPS URL

现在，让我们将URL更改为另一个没有有效SSL证书的URL。我们可以通过更改请求对象来做到这一点：

```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(new URI("https://www.testingmcafeesites.com/"))
    .build();
```

当我们再次运行测试时，我们得到以下错误：

```shell
Caused by: java.security.cert.CertificateException: No subject alternative DNS name matching www.testingmcafeesites.com found.
  at java.base/sun.security.util.HostnameChecker.matchDNS(HostnameChecker.java:212)
  at java.base/sun.security.util.HostnameChecker.match(HostnameChecker.java:103)
```

这是因为URL没有有效的SSL证书。

## 4. 绕过SSL证书验证

为了解决我们上面遇到的错误，让我们看看绕过SSL证书验证的解决方案。

在Apache HttpClient中，我们可以修改客户端以[绕过证书验证](https://www.baeldung.com/httpclient-ssl)。但是，我们不能使用Java HttpClient来做到这一点。**我们将不得不依靠对JVM进行更改来禁用主机名验证**。

一种方法是[将网站的证书导入Java KeyStore](https://www.baeldung.com/java-import-cer-certificate-into-keystore)。这是一种常见的做法，如果有少量内部受信任的网站，这是一个不错的选择。

但是，如果有大量网站或太多需要管理的环境，这可能会变得令人厌烦。在这种情况下，**我们可以使用属性jdk.internal.httpclient.disableHostnameVerification来禁用主机名验证**。

我们可以在运行应用程序时将此属性设置为命令行参数：

```bash
java -Djdk.internal.httpclient.disableHostnameVerification=true -jar target/java-httpclient-ssl-1.0.0.jar
```

**或者，我们可以在创建客户端之前以编程方式设置此属性**：

```java
Properties props = System.getProperties();
props.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());

HttpClient httpClient = HttpClient.newHttpClient();
```

当我们现在运行测试时，我们会看到它通过了。

**我们应该注意，更改属性将意味着对所有请求都禁用证书验证。这可能是不可取的，尤其是在生产中**。但是，在非生产环境中引入此属性很常见。

## 5. 我们可以在Spring中使用Java HttpClient吗？

Spring提供了两个流行的接口来发出HTTP请求：

-   用于同步请求的RestTemplate，以及
-   用于同步和异步请求的WebClient

两者都可以与流行的HTTP客户端一起使用，例如Apache HttpClient、OkHttp和旧的HttpURLConnection。但是，**我们不能将Java HttpClient插入这两个接口。它被视为它们的替代品**。

我们可以使用Java HttpClient进行同步和异步请求，转换请求和响应，添加超时等，因此可以直接使用，不需要Spring的接口。

## 6. 总结

在本文中，我们探讨了如何使用Java HTTP客户端连接到需要SSL的服务器。我们还研究了将客户端与没有有效证书的URL一起使用的方法。