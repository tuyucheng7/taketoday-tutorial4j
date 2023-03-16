## 1. 概述

**WireMock**是一个用于stubbing和mocking Web服务的库。它构建了一个HTTP服务器，我们可以像连接到实际的Web服务一样连接到该服务器。

当[WireMock](http://wiremock.org/)服务器运行时，我们可以设置期望，调用服务，然后验证其行为。

## 2. Maven依赖

为了使用WireMock库，我们需要在POM中包含此依赖项：

```xml
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock</artifactId>
    <version>1.58</version>
    <scope>test</scope>
</dependency>
```

## 3. 以编程方式管理的服务器

本节将介绍如何手动配置WireMock服务器，即不支持JUnit自动配置。我们用一个非常简单的stub来演示用法。

### 3.1 服务器设置

首先，我们实例化一个WireMock服务器：

```java
WireMockServer wireMockServer = new WireMockServer(String host, int port);
```

如果未提供任何参数，则服务器主机默认为localhost，服务器端口默认为8080。

然后我们可以使用两个简单的方法来启动和停止服务器：

```java
wireMockServer.start();
```

和：

```java
wireMockServer.stop();
```

### 3.2 基本用法

我们将首先演示WireMock库的基本用法，其中提供了无需任何进一步配置的确切URL的stub。

让我们创建一个服务器实例：

```java
WireMockServer wireMockServer = new WireMockServer();
```

在客户端连接到WireMock服务器之前，它必须正在运行：

```java
wireMockServer.start();
```

然后对Web服务进行stubbed：

```java
configureFor("localhost", 8080);
stubFor(get(urlEqualTo("/tuyucheng")).willReturn(aResponse().withBody("Welcome to Tuyucheng!")));
```

本教程使用Apache HttpClient API来表示连接到服务器的客户端：

```java
CloseableHttpClient httpClient = HttpClients.createDefault();
```

执行请求，然后返回响应：

```java
HttpGet request = new HttpGet("http://localhost:8080/tuyucheng");
HttpResponse httpResponse = httpClient.execute(request);
```

我们将使用工具方法将httpResponse变量转换为字符串：

```java
String responseString = convertResponseToString(httpResponse);
```

以下是该转换工具方法的实现：

```java
private static String convertResponseToString(HttpResponse response) throws IOException {
	InputStream responseStream = response.getEntity().getContent();
	Scanner scanner = new Scanner(responseStream, "UTF-8");
	String stringResponse = scanner.useDelimiter("\\Z").next();
	scanner.close();
	return stringResponse;
}
```

以下代码验证服务器是否已收到了对预期URL的请求，并且返回客户端的响应是正确发送的内容：

```java
verify(getRequestedFor(urlEqualTo("/tuyucheng")));
assertEquals("Welcome to Tuyucheng!", stringResponse);
```

最后，我们应该停止WireMock服务器以释放系统资源：

```java
wireMockServer.stop();
```

## 4. JUnit托管服务器

与第3节相比，本节说明了在JUnit Rule的帮助下使用WireMock服务器。

### 4.1 服务器设置

我们可以使用@Rule注解将WireMock服务器集成到JUnit测试用例中。这允许JUnit管理生命周期，在每个测试方法之前启动服务器并在方法返回后停止它。

与以编程方式管理的服务器类似，可以将JUnit管理的WireMock服务器创建为具有给定端口号的Java对象：

```java
@Rule
public WireMockRule wireMockRule = new WireMockRule(int port);
```

如果没有提供参数，服务器端口将采用默认值8080。服务器主机默认为localhost和其他配置可以使用Options接口指定。

### 4.2 URL匹配

设置WireMockRule实例后，下一步是配置stub。

在本小节中，我们将使用正则表达式为服务端点提供REST stub：

```java
stubFor(get(urlPathMatching("/tuyucheng/.*"))
    .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", APPLICATION_JSON)
        .withBody("\"testing-library\": \"WireMock\"")));
```

让我们继续创建HTTP客户端，执行请求并接收响应：

```java
CloseableHttpClient httpClient = HttpClients.createDefault();
HttpGet request = new HttpGet(String.format("http://localhost:%s/tuyucheng/wiremock", port));
HttpResponse httpResponse = httpClient.execute(request);
String stringResponse = convertHttpResponseToString(httpResponse);
```

上面的代码片段使用了转换工具方法：

```java
private static String convertHttpResponseToString(HttpResponse httpResponse) throws IOException {
	InputStream inputStream = httpResponse.getEntity().getContent();
	return convertInputStreamToString(inputStream);
}
```

而该工具方法又使用了另一个私有方法：

```java
private static String convertInputStreamToString(InputStream inputStream) {
	Scanner scanner = new Scanner(inputStream, "UTF-8");
	String string = scanner.useDelimiter("\\Z").next();
	scanner.close();
	return string;
}
```

stub的操作由下面的测试代码验证：

```java
verify(getRequestedFor(urlEqualTo("/tuyucheng/wiremock")));
assertEquals(200, httpResponse.getStatusLine().getStatusCode());
assertEquals(APPLICATION_JSON, httpResponse.getFirstHeader("Content-Type").getValue());
assertEquals("\"testing-library\": \"WireMock\"", stringResponse);
```

### 4.3 请求头匹配

现在我们将演示如何通过标头匹配对REST API进行stub。

首先我们从stub配置开始：

```java
stubFor(get(urlPathEqualTo("/tuyucheng/wiremock"))
    .withHeader("Accept", matching("text/.*"))
    .willReturn(aResponse()
        .withStatus(503)
        .withHeader("Content-Type", "text/html")
        .withBody("!!! Service Unavailable !!!")));
```

与前面的小节类似，我们在相同的工具方法的帮助下使用HttpClient API说明HTTP交互：

```java
CloseableHttpClient httpClient = HttpClients.createDefault();
HttpGet request = new HttpGet(String.format("http://localhost:%s/tuyucheng/wiremock", port));
request.addHeader("Accept", "text/html");
HttpResponse httpResponse = httpClient.execute(request);
String stringResponse = convertHttpResponseToString(httpResponse);
```

以下验证和断言确认了我们之前创建的stub的功能：

```java
verify(getRequestedFor(urlEqualTo("/tuyucheng/wiremock")));
assertEquals(503, httpResponse.getStatusLine().getStatusCode());
assertEquals("text/html", httpResponse.getFirstHeader("Content-Type").getValue());
assertEquals("!!! Service Unavailable !!!", stringResponse);
```

### 4.4 请求正文匹配

我们还可以使用WireMock库来stub具有正文匹配的REST API。

这是此类stub的配置：

```java
stubFor(post(urlEqualTo("/tuyucheng/wiremock"))
    .withHeader("Content-Type", equalTo(APPLICATION_JSON))
    .withRequestBody(containing("\"testing-library\": \"WireMock\""))
    .withRequestBody(containing("\"creator\": \"Tom Akehurst\""))
    .withRequestBody(containing("\"website\": \"wiremock.org\""))
    .willReturn(aResponse().withStatus(200)));
```

然后创建一个将用作请求主体的StringEntity对象：

```java
InputStream jsonInputStream = this.getClass().getClassLoader().getResourceAsStream("wiremock_intro.json");
String jsonString = convertInputStreamToString(jsonInputStream);
StringEntity entity = new StringEntity(jsonString);
```

上面的代码使用之前定义的转换工具方法之一convertInputStreamToString。

以下是src/test/resources目录中wiremock_intro.json文件的内容：

```json
{
    "testing-library": "WireMock",
    "creator": "Tom Akehurst",
    "website": "wiremock.org"
}
```

我们可以配置和运行HTTP请求和响应：

```java
CloseableHttpClient httpClient = HttpClients.createDefault();
HttpPost request = new HttpPost(String.format("http://localhost:%s/tuyucheng/wiremock", port));
request.addHeader("Content-Type", APPLICATION_JSON);
request.setEntity(entity);
HttpResponse response = httpClient.execute(request);
```

这是用于验证stub的测试代码：

```java
verify(postRequestedFor(urlEqualTo("/tuyucheng/wiremock"))
    .withHeader("Content-Type", equalTo(APPLICATION_JSON)));
assertEquals(200, response.getStatusLine().getStatusCode());
```

### 4.5 stub优先级

前面的小节处理HTTP请求仅匹配单个stub的情况。

如果请求的匹配项不止一个，则情况会更加复杂。默认情况下，在这种情况下，最近添加的stub将优先。

但是，用户可以自定义该行为以更好地控制WireMock stub。

我们将演示当传入的请求同时匹配两个不同的stub(同时设置和不设置优先级)时WireMock服务器的操作。

这两种情况都将使用以下私有的工具方法：

```java
private HttpResponse generateClientAndReceiveResponseForPriorityTests() throws IOException {
	CloseableHttpClient httpClient = HttpClients.createDefault();
	HttpGet request = new HttpGet(String.format("http://localhost:%s/tuyucheng/wiremock", port));
	request.addHeader("Accept", "text/xml");
	return httpClient.execute(request);
}
```

首先，我们在不考虑优先级的情况下配置两个stub：

```java
stubFor(get(urlPathMatching("/tuyucheng/.*"))
    .willReturn(aResponse().withStatus(200)));
stubFor(get(urlPathEqualTo("/tuyucheng/wiremock"))
    .withHeader("Accept", matching("text/.*"))
    .willReturn(aResponse().withStatus(503)));
```

接下来，我们创建一个HTTP客户端并使用工具方法执行请求：

```java
HttpResponse httpResponse = generateClientAndReceiveResponseForPriorityTests();
```

以下代码片段验证当请求与这两个stub都匹配时，是否应用了最后一个配置的stub，而不考虑之前定义的stub：

```java
verify(getRequestedFor(urlEqualTo("/tuyucheng/wiremock")));
assertEquals(503, httpResponse.getStatusLine().getStatusCode());
```

下面我们继续演示设置了优先级的stub，其中atPriority方法中指定的数字越小表示优先级越高：

```java
stubFor(get(urlPathMatching("/tuyucheng/.*"))
    .atPriority(1)
    .willReturn(aResponse().withStatus(200)));
stubFor(get(urlPathEqualTo("/tuyucheng/wiremock"))
    .atPriority(2)
    .withHeader("Accept", matching("text/.*"))
    .willReturn(aResponse().withStatus(503)));
```

现在我们将执行HTTP请求的创建和执行：

```java
HttpResponse httpResponse = generateClientAndReceiveResponseForPriorityTests();
```

以下代码验证优先级的影响，其中应用第一个配置的stub而不是最后一个：

```java
verify(getRequestedFor(urlEqualTo("/tuyucheng/wiremock")));
assertEquals(200, httpResponse.getStatusLine().getStatusCode());
```

## 5. 总结

本文介绍了WireMock以及如何设置和配置此库以使用各种技术测试REST API，包括匹配URL、请求头和正文。