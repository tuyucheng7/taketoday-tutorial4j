## 1. 概述

在本文中，我将展示如何配置 Apache HttpClient 以停止跟随重定向。

默认情况下，遵循 HTTP 规范，HttpClient 将自动遵循 redirects。

对于某些用例，这可能完全没问题，但肯定有一些用例不希望这样做——我们现在将研究如何更改该默认行为并停止跟随重定向。

如果你想更深入地研究并学习其他可以使用 HttpClient 做的很酷的事情——请继续[阅读主要的 HttpClient 教程](https://www.baeldung.com/httpclient-guide)。

## 2. 不要跟随重定向

### 2.1. 在 HttpClient 4.3 之前

在旧版本的 Http Client(4.3 之前)中，我们可以配置客户端对重定向的操作，如下所示：

```java
@Test
public void givenRedirectsAreDisabled_whenConsumingUrlWhichRedirects_thenNotRedirected() 
  throws ClientProtocolException, IOException {
    DefaultHttpClient instance = new DefaultHttpClient();

    HttpParams params = new BasicHttpParams();
    params.setParameter(ClientPNames.HANDLE_REDIRECTS, false);
    // HttpClientParams.setRedirecting(params, false); // alternative

    HttpGet httpGet = new HttpGet("http://t.co/I5YYd9tddw");
    httpGet.setParams(params);
    CloseableHttpResponse response = instance.execute(httpGet);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(301));
}
```

请注意可用于配置重定向行为的替代 API，而无需设置实际的原始http.protocol.handle-redirects参数：

```java
HttpClientParams.setRedirecting(params, false);
```

另请注意，在禁用跟随重定向的情况下，我们现在可以检查 Http 响应状态代码是否确实是301 永久移动——这是应该的。

### 2.2. HttpClient 4.3 之后

HttpClient 4.3 引入了一个更干净、更高级的 API来构建和配置客户端：

```java
@Test
public void givenRedirectsAreDisabled_whenConsumingUrlWhichRedirects_thenNotRedirected() 
  throws ClientProtocolException, IOException {
    HttpClient instance = HttpClientBuilder.create().disableRedirectHandling().build();
    HttpResponse response = instance.execute(new HttpGet("http://t.co/I5YYd9tddw"));

    assertThat(response.getStatusLine().getStatusCode(), equalTo(301));
}
```

请注意，新的 API 为整个客户端配置了这种重定向行为——而不仅仅是单个请求。

## 3.总结

本快速教程介绍了如何配置 Apache HttpClient(4.3之前和之后)以防止它自动跟随 HTTP 重定向。