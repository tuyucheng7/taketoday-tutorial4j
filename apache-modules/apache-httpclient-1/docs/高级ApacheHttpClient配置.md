## 1. 概述

在本文中，我们将研究 Apache [HttpClient](https://hc.apache.org/httpcomponents-client-5.1.x/examples.html)库的高级用法。

我们将查看向 HTTP 请求添加自定义标头的示例，并将了解如何配置客户端以通过代理服务器授权和发送请求。

我们将使用 Wiremock 来存根 HTTP 服务器。如果你想阅读有关 Wiremock 的更多信息，请查看[这篇文章](https://www.baeldung.com/introduction-to-wiremock)。

## 2. 带有自定义用户代理标头的 HTTP 请求

假设我们要向HTTP GET 请求添加自定义User-Agent标头。User-Agent标头包含一个特征字符串，该字符串允许网络协议对等方识别请求软件用户代理的应用程序类型、操作系统和软件供应商或软件版本。

在开始编写 HTTP 客户端之前，我们需要启动嵌入式模拟服务器：

```java
@Rule
public WireMockRule serviceMock = new WireMockRule(8089);
```

当我们创建一个HttpGet实例时，我们可以简单地使用setHeader()方法来传递我们的标头名称和值。该标头将添加到 HTTP 请求中：

```java
String userAgent = "BaeldungAgent/1.0"; 
HttpClient httpClient = HttpClients.createDefault();

HttpGet httpGet = new HttpGet("http://localhost:8089/detail");
httpGet.setHeader(HttpHeaders.USER_AGENT, userAgent);

HttpResponse response = httpClient.execute(httpGet);

assertEquals(response.getStatusLine().getStatusCode(), 200);
```

我们正在添加一个User-Agent标头并通过execute()方法发送该请求。

当针对 URL /detail的 URL /detail 发送 GET 请求时，其标头User-Agent的值等于“BaeldungAgent/1.0”，然后 serviceMock将返回 200 HTTP 响应代码：

```java
serviceMock.stubFor(get(urlEqualTo("/detail"))
  .withHeader("User-Agent", equalTo(userAgent))
  .willReturn(aResponse().withStatus(200)));
```

## 3.在POST请求体中发送数据

通常，当我们执行 HTTP POST 方法时，我们希望传递一个实体作为请求主体。创建HttpPost对象的实例时，我们可以使用setEntity()方法将正文添加到该请求：

```java
String xmlBody = "<xml><id>1</id></xml>";
HttpClient httpClient = HttpClients.createDefault();
HttpPost httpPost = new HttpPost("http://localhost:8089/person");
httpPost.setHeader("Content-Type", "application/xml");

StringEntity xmlEntity = new StringEntity(xmlBody);
httpPost.setEntity(xmlEntity);

HttpResponse response = httpClient.execute(httpPost);

assertEquals(response.getStatusLine().getStatusCode(), 200);
```

我们正在创建一个主体为XML格式的StringEntity实例。重要的是将Content-Type标头设置为“ application/xml ”，以便将有关我们发送的内容类型的信息传递给服务器。当serviceMock收到带有 XML 正文的 POST 请求时，它会返回状态码 200 OK：

```java
serviceMock.stubFor(post(urlEqualTo("/person"))
  .withHeader("Content-Type", equalTo("application/xml"))
  .withRequestBody(equalTo(xmlBody))
  .willReturn(aResponse().withStatus(200)));
```

## 4. 通过代理服务器发送请求

通常，我们的 Web 服务可以在执行一些额外逻辑、缓存静态资源等的代理服务器后面。当我们创建 HTTP 客户端并向实际服务发送请求时，我们不想在每个 HTTP 请求。

为了测试这种情况，我们需要启动另一个嵌入式 Web 服务器：

```java
@Rule
public WireMockRule proxyMock = new WireMockRule(8090);
```

对于两个嵌入式服务器，第一个实际服务在 8089 端口上，代理服务器在 8090 端口上侦听。

我们通过创建一个将HttpHost实例代理作为参数的DefaultProxyRoutePlanner，将我们的HttpClient配置为通过代理发送所有请求：

```java
HttpHost proxy = new HttpHost("localhost", 8090);
DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
HttpClient httpclient = HttpClients.custom()
  .setRoutePlanner(routePlanner)
  .build();

```

我们的代理服务器将所有请求重定向到侦听 8090 端口的实际服务。在测试结束时，我们验证请求是通过代理发送到我们的实际服务的：

```java
proxyMock.stubFor(get(urlMatching("."))
  .willReturn(aResponse().proxiedFrom("http://localhost:8089/")));

serviceMock.stubFor(get(urlEqualTo("/private"))
  .willReturn(aResponse().withStatus(200)));

assertEquals(response.getStatusLine().getStatusCode(), 200);
proxyMock.verify(getRequestedFor(urlEqualTo("/private")));
serviceMock.verify(getRequestedFor(urlEqualTo("/private")));
```

## 5.配置HTTP客户端通过代理授权

扩展前面的例子，有一些情况是使用代理服务器来执行授权。在这样的配置中，代理可以授权所有请求并将它们传递给隐藏在代理后面的服务器。

我们可以将 HttpClient 配置为通过代理发送每个请求，连同将用于执行授权过程的授权标头。

假设我们有一个代理服务器只授权一个用户——“ username_admin ” ，密码为“ secret_password ” 。

我们需要使用将通过代理授权的用户凭据创建BasicCredentialsProvider实例。为了使HttpClient自动添加具有正确值的Authorization标头，我们需要创建一个带有提供的凭据的HttpClientContext和一个存储凭据的BasicAuthCache：

```java
HttpHost proxy = new HttpHost("localhost", 8090);
DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);

//Client credentials
CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
credentialsProvider.setCredentials(new AuthScope(proxy), 
  new UsernamePasswordCredentials("username_admin", "secret_password"));

// Create AuthCache instance
AuthCache authCache = new BasicAuthCache();

BasicScheme basicAuth = new BasicScheme();
authCache.put(proxy, basicAuth);
HttpClientContext context = HttpClientContext.create();
context.setCredentialsProvider(credentialsProvider);
context.setAuthCache(authCache);

HttpClient httpclient = HttpClients.custom()
  .setRoutePlanner(routePlanner)
  .setDefaultCredentialsProvider(credentialsProvider)
  .build();
```

当我们设置HttpClient 时，向我们的服务发出请求将导致通过代理发送带有授权标头的请求以执行授权过程。它将在每个请求中自动设置。

让我们对服务执行一个实际请求：

```java
HttpGet httpGet = new HttpGet("http://localhost:8089/private");
HttpResponse response = httpclient.execute(httpGet, context);
```

使用我们的配置验证httpClient上的execute()方法确认请求通过具有Authorization标头的代理：

```java
proxyMock.stubFor(get(urlMatching("/private"))
  .willReturn(aResponse().proxiedFrom("http://localhost:8089/")));
serviceMock.stubFor(get(urlEqualTo("/private"))
  .willReturn(aResponse().withStatus(200)));

assertEquals(response.getStatusLine().getStatusCode(), 200);
proxyMock.verify(getRequestedFor(urlEqualTo("/private"))
  .withHeader("Authorization", containing("Basic")));
serviceMock.verify(getRequestedFor(urlEqualTo("/private")));
```

## 六. 总结

本文介绍如何配置 Apache HttpClient以执行高级 HTTP 调用。我们看到了如何通过代理服务器发送请求以及如何通过代理进行授权。