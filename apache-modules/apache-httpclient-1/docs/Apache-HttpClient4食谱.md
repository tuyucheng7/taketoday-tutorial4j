## 1. 概述

本说明书展示了如何在各种示例和用例中使用 Apache HttpClient。

重点是HttpClient 4.3.x 及更高版本，因此一些示例可能不适用于旧版本的 API。

食谱的格式以示例为重点且实用——无需多余的细节和解释。

如果你想更深入地研究并学习其他可以使用 HttpClient 做的很酷的事情——请继续[阅读主要的 HttpClient 教程](https://www.baeldung.com/httpclient-guide)。

## 2.食谱

创建 http 客户端

```java
CloseableHttpClient client = HttpClientBuilder.create().build();
```

发送基本的 GET 请求

```java
instance.execute(new HttpGet("http://www.google.com"));
```

获取 HTTP 响应的状态代码

```java
CloseableHttpResponse response = instance.execute(new HttpGet("http://www.google.com"));
assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
```

获取响应的媒体类型

```java
CloseableHttpResponse response = instance.execute(new HttpGet("http://www.google.com"));
String contentMimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();
assertThat(contentMimeType, equalTo(ContentType.TEXT_HTML.getMimeType()));
```

获取响应的主体

```java
CloseableHttpResponse response = instance.execute(new HttpGet("http://www.google.com"));
String bodyAsString = EntityUtils.toString(response.getEntity());
assertThat(bodyAsString, notNullValue());
```

在请求上配置超时

```java
@Test(expected = SocketTimeoutException.class)
public void givenLowTimeout_whenExecutingRequestWithTimeout_thenException() 
    throws ClientProtocolException, IOException {
    RequestConfig requestConfig = RequestConfig.custom()
      .setConnectionRequestTimeout(1000).setConnectTimeout(1000).setSocketTimeout(1000).build();
    HttpGet request = new HttpGet(SAMPLE_URL);
    request.setConfig(requestConfig);
    instance.execute(request);
}
```

在整个客户端上配置超时

```java
RequestConfig requestConfig = RequestConfig.custom().
    setConnectionRequestTimeout(1000).setConnectTimeout(1000).setSocketTimeout(1000).build();
HttpClientBuilder builder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig);
```

发送 POST 请求

```java
instance.execute(new HttpPost(SAMPLE_URL));
```

向请求添加参数

```java
List<NameValuePair> params = new ArrayList<NameValuePair>();
params.add(new BasicNameValuePair("key1", "value1"));
params.add(new BasicNameValuePair("key2", "value2"));
request.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
```

配置如何为 HTTP 请求处理重定向

```java
CloseableHttpClient instance = HttpClientBuilder.create().disableRedirectHandling().build();
CloseableHttpResponse response = instance.execute(new HttpGet("http://t.co/I5YYd9tddw"));
assertThat(response.getStatusLine().getStatusCode(), equalTo(301));
```

配置请求的标头

```java
HttpGet request = new HttpGet(SAMPLE_URL);
request.addHeader(HttpHeaders.ACCEPT, "application/xml");
response = instance.execute(request);
```

从响应中获取标头

```java
CloseableHttpResponse response = instance.execute(new HttpGet(SAMPLE_URL));
Header[] headers = response.getHeaders(HttpHeaders.CONTENT_TYPE);
assertThat(headers, not(emptyArray()));
```

关闭/释放资源

```java
response = instance.execute(new HttpGet(SAMPLE_URL));
try {
    HttpEntity entity = response.getEntity();
    if (entity != null) {
        InputStream instream = entity.getContent();
        instream.close();
    }
} finally {
    response.close();
}
```

## 3.深入HttpClient

如果使用得当，HttpClient 库是一个非常强大的工具——如果你想开始探索客户端可以做什么——请查看一些教程：

-   [HttpClient – 获取状态码](https://www.baeldung.com/httpclient-status-code)

-   [HttpClient – 设置自定义标头](https://www.baeldung.com/httpclient-custom-http-header)

你还可以通过探索[整个系列](https://www.baeldung.com/httpclient-guide)更深入地了解 HttpClient。

## 4. 总结

这种格式与我通常构建文章的方式有点不同——我在[Google Guava](https://www.baeldung.com/guava-collections)、[Hamcrest](https://www.baeldung.com/hamcrest-collections-arrays)和[Mockito](https://www.baeldung.com/mockito-verify)上发布了一些关于给定主题的内部开发指南——现在是 HttpClient。目标是让这些信息在网上随时可用——并在我遇到新的有用示例时添加到其中。