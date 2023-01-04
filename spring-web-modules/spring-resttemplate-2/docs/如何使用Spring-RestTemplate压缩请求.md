## 1. 简介

在这个简短的教程中，我们将了解如何发送包含压缩数据的HTTP请求。

此外，我们还将了解如何配置Spring Web应用程序以处理压缩请求。

## 2. 发送压缩请求

首先，让我们创建一个压缩字节数组的方法。这很快就会派上用场：

```java
public static byte[] compress(byte[] body) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(baos)) {
        gzipOutputStream.write(body);
    }
    return baos.toByteArray();
}
```

接下来，我们需要实现一个ClientHttpRequestInterceptor来修改请求。请注意，我们将发送适当的HTTP压缩标头并调用我们的主体压缩方法：

```java
public ClientHttpResponse intercept(HttpRequest req, byte[] body, ClientHttpRequestExecution exec)
  throws IOException {
    HttpHeaders httpHeaders = req.getHeaders();
    httpHeaders.add(HttpHeaders.CONTENT_ENCODING, "gzip");
    httpHeaders.add(HttpHeaders.ACCEPT_ENCODING, "gzip");
    return exec.execute(req, compress(body));
}
```

我们的拦截器获取出站请求正文并使用GZIP格式对其进行压缩。在此示例中，我们使用Java的标准GZIPOutputStream为我们完成这项工作。

此外，我们必须为数据编码添加适当的标头。这让目标端点知道它正在处理GZIP压缩数据。

最后，我们将拦截器添加到我们的RestTemplate定义中：

```java
@Bean
public RestTemplate getRestTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.getInterceptors().add(new CompressingClientHttpRequestInterceptor());
    return restTemplate;
}
```

## 3. 处理压缩请求

默认情况下，大多数Web服务器不理解包含压缩数据的请求。Spring Web应用程序也不例外。因此，我们需要配置它们来处理此类请求。

目前，只有Jetty和Undertow网络服务器处理带有GZIP格式数据的请求体。请参阅我们关于[Spring Boot应用程序配置](https://www.baeldung.com/spring-boot-application-configuration)的文章以设置Jetty或Undertow Web服务器。

### 3.1 Jetty Web服务器

在此示例中，我们通过添加Jetty GzipHandler来自定义Jetty Web服务器。此Jetty处理程序旨在压缩响应和解压缩请求。

但是，将它添加到Jetty Web服务器是不够的。我们需要将inflateBufferSize设置为大于零的值以启用解压缩：

```java
@Bean
public JettyServletWebServerFactory jettyServletWebServerFactory() {
    JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
    factory.addServerCustomizers(server -> {
        GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setInflateBufferSize(1);
        gzipHandler.setHandler(server.getHandler());

        HandlerCollection handlerCollection = new HandlerCollection(gzipHandler);
        server.setHandler(handlerCollection);
    });
    return factory;
}
```

### 3.2 Undertow Web服务器

同样，我们可以自定义一个Undertow Web服务器来自动为我们解压请求。在这种情况下，我们需要添加一个自定义的RequestEncodingHandler。

我们配置编码处理程序来处理来自请求的GZIP源数据：

```java
@Bean
public UndertowServletWebServerFactory undertowServletWebServerFactory() {
    UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
    factory.addDeploymentInfoCustomizers((deploymentInfo) -> {
        deploymentInfo.addInitialHandlerChainWrapper(handler -> new RequestEncodingHandler(handler)
            .addEncoding("gzip", GzipStreamSourceConduit.WRAPPER));
    });
    return factory;
}
```

## 4. 总结

这就是我们需要做的让压缩请求工作的全部！

在本教程中，我们介绍了如何为压缩请求内容的RestTemplate创建拦截器。此外，我们研究了如何在我们的Spring Web应用程序中自动解压缩这些请求。

重要的是要注意，我们应该只将压缩内容发送到能够处理此类请求的Web服务器。