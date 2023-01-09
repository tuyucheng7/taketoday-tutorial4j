## 1. 概述

本快速教程将展示如何使用 Apache HttpClient发送自定义User-Agent标头。

## 2.在HttpClient上设置User-Agent 

### 2.1. 在 HttpClient 4.3 之前

使用旧版本的 Http Client(4.3 之前)时，设置User-Agent的值是通过低级 API完成的：

```java
client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 Firefox/26.0");
```

也可以通过更高级别的 API完成同样的操作——无需处理原始的http.useragent属性：

```java
HttpProtocolParams.setUserAgent(client.getParams(), "Mozilla/5.0 Firefox/26.0");
```

一个完整的例子看起来像这样：

```java
@Test
public void whenClientUsesCustomUserAgent_thenCorrect() 
  throws ClientProtocolException, IOException {
    DefaultHttpClient client = new DefaultHttpClient();
    HttpProtocolParams.setUserAgent(client.getParams(), "Mozilla/5.0 Firefox/26.0");

    HttpGet request = new HttpGet("http://www.github.com");
    client.execute(request);
}
```

### 2.2. HttpClient 4.3 之后

在最新版本的 Apache 客户端(4.3 之后)中，通过新的 Fluent API，以更简洁的方式实现了同样的功能：

```java
@Test
public void whenRequestHasCustomUserAgent_thenCorrect() 
  throws ClientProtocolException, IOException {
    HttpClient instance = HttpClients.custom().setUserAgent("Mozilla/5.0 Firefox/26.0").build();
    instance.execute(new HttpGet("http://www.github.com"));
}
```

## 3.为个别请求设置User-Agent

自定义User-Agent标头也可以在单个请求上设置，而不是在整个HttpClient上设置：

```java
@Test
public void givenDeprecatedApi_whenRequestHasCustomUserAgent_thenCorrect() 
  throws ClientProtocolException, IOException {
    HttpClient instance = HttpClients.custom().build();
    HttpGet request = new HttpGet(SAMPLE_URL);
    request.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 Firefox/26.0");
    instance.execute(request);
}
```

## 4. 总结

本文说明了如何使用 HttpClient 发送带有自定义User-Agent标头的请求——例如模拟特定浏览器的行为。