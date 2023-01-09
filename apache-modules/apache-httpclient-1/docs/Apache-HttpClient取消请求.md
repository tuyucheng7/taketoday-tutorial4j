## 1. 概述

本快速教程展示了如何使用 Apache HttpClient 取消 HTTP 请求。

这对于可能会长时间运行的请求或大型下载文件特别有用，否则会不必要地消耗带宽和连接。

如果你想更深入地研究并学习其他可以使用 HttpClient 做的很酷的事情——请继续[阅读主要的 HttpClient 教程](https://www.baeldung.com/httpclient-guide)。

## 2. 中止 GET 请求

要中止正在进行的请求，客户端可以简单地使用：

```java
request.abort();
```

这将确保客户端不必消耗整个请求体来释放连接：

```java
@Test
public void whenRequestIsCanceled_thenCorrect() 
  throws ClientProtocolException, IOException {
    HttpClient instance = HttpClients.custom().build();
    HttpGet request = new HttpGet(SAMPLE_URL);
    HttpResponse response = instance.execute(request);

    try {
        System.out.println(response.getStatusLine());
        request.abort();
    } finally {
        response.close();
    }
}
```

## 3.总结

本文说明了如何使用 HTTP 客户端中止正在进行的请求。停止长时间运行的请求的另一种选择是确保它们会[超时](https://www.baeldung.com/httpclient-timeout)。