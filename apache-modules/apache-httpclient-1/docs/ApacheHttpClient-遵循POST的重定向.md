## 1. 概述

本快速教程将展示如何配置 Apache HttpClient 以自动遵循 POST 请求的重定向。

如果你想更深入地研究并学习其他可以使用 HttpClient 做的很酷的事情——请继续[阅读主要的 HttpClient 教程](https://www.baeldung.com/httpclient-guide)。

默认情况下，只会自动遵循导致重定向的 GET 请求。如果使用HTTP 301 Moved Permanently或302 Found响应 POST 请求，则不会自动遵循重定向。

这是由[HTTP RFC 2616](https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3)指定的：

>   如果收到 301 状态代码以响应 GET 或 HEAD 以外的请求，则用户代理不得自动重定向请求，除非用户可以确认，因为这可能会改变发出请求的条件。

当然，在某些用例中，我们需要更改该行为并放宽严格的 HTTP 规范。

首先，让我们检查一下默认行为：

```java
@Test
public void givenPostRequest_whenConsumingUrlWhichRedirects_thenNotRedirected() 
  throws ClientProtocolException, IOException {
    HttpClient instance = HttpClientBuilder.create().build();
    HttpResponse response = instance.execute(new HttpPost("http://t.co/I5YYd9tddw"));
    assertThat(response.getStatusLine().getStatusCode(), equalTo(301));
}
```

如你所见，默认情况下不遵循重定向，我们返回301 状态代码。

## 2.HTTP POST重定向

### 2.1. 对于 HttpClient 4.3 及之后的版本

在 HttpClient 4.3 中，为客户端的创建和配置引入了更高级别的 API：

```java
@Test
public void givenRedirectingPOST_whenConsumingUrlWhichRedirectsWithPOST_thenRedirected() 
  throws ClientProtocolException, IOException {
    HttpClient instance = 
      HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
    HttpResponse response = instance.execute(new HttpPost("http://t.co/I5YYd9tddw"));
    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
}
```

请注意，HttpClientBuilder现在是流畅 API 的起点，它允许以比以前更易读的方式对客户端进行完整配置。

### 2.2. 对于 HttpClient 4.2

在之前版本的HttpClient(4.2)中我们可以直接在客户端配置重定向策略：

```java
@SuppressWarnings("deprecation")
@Test
public void givenRedirectingPOST_whenConsumingUrlWhichRedirectsWithPOST_thenRedirected() 
  throws ClientProtocolException, IOException {
    DefaultHttpClient client = new DefaultHttpClient();
    client.setRedirectStrategy(new LaxRedirectStrategy());

    HttpResponse response = client.execute(new HttpPost("http://t.co/I5YYd9tddw"));
    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
}
```

请注意，现在，使用新的LaxRedirectStrategy，放宽了 HTTP 限制，重定向也通过 POST 进行- 导致200 OK状态代码。

### 2.3. HttpClient 4.2 之前的版本

在 HttpClient 4.2 之前，LaxRedirectStrategy类不存在，所以我们需要自己动手：

```java
@Test
public void givenRedirectingPOST_whenConsumingUrlWhichRedirectsWithPOST_thenRedirected() 
  throws ClientProtocolException, IOException {
    DefaultHttpClient client = new DefaultHttpClient();
    client.setRedirectStrategy(new DefaultRedirectStrategy() {
        / Redirectable methods. /
        private String[] REDIRECT_METHODS = new String[] { 
            HttpGet.METHOD_NAME, HttpPost.METHOD_NAME, HttpHead.METHOD_NAME 
        };

        @Override
        protected boolean isRedirectable(String method) {
            for (String m : REDIRECT_METHODS) {
                if (m.equalsIgnoreCase(method)) {
                    return true;
                }
            }
            return false;
        }
    });

    HttpResponse response = client.execute(new HttpPost("http://t.co/I5YYd9tddw"));
    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
}
```

## 3.总结

本快速指南说明了如何配置任何版本的 Apache HttpClient 以遵循 HTTP POST 请求的重定向——放宽严格的 HTTP 标准。