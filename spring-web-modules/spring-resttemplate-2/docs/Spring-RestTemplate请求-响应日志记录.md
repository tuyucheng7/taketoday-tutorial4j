## 1. 概述

在本教程中，我们将学习如何实现高效的RestTemplate请求/响应日志记录。这对于调试两个服务器之间的交换特别有用。

不幸的是，Spring Boot没有提供一种简单的方法来检查或记录简单的JSON响应主体。

我们将探索几种记录HTTP标头或HTTP主体的方法，这是最有趣的部分。

注意：Spring RestTemplate将被弃用，由WebClient取代。你可以在此处找到使用WebClient的类似文章：[记录Spring WebClient调用](https://www.baeldung.com/spring-log-webclient-calls)。

## 2. 使用RestTemplate进行基本日志记录

让我们开始在application.properties文件中配置RestTemplate记录器：

```properties
logging.level.org.springframework.web.client.RestTemplate=DEBUG
```

因此，我们只能看到请求URL、方法、正文和响应状态等基本信息：

```bash
o.s.w.c.RestTemplate - HTTP POST http://localhost:8082/spring-rest/persons
o.s.w.c.RestTemplate - Accept=[text/plain, application/json, application/*+json, */*]
o.s.w.c.RestTemplate - Writing [my request body] with org.springframework.http.converter.StringHttpMessageConverter
o.s.w.c.RestTemplate - Response 200 OK
```

但是，这里没有记录响应正文，这很不幸，因为这是最有趣的部分。

为了解决这个问题，我们将选择Apache HttpClient或Spring拦截器。

## 3. 使用Apache HttpClient记录标头/正文

首先，我们必须让RestTemplate使用[Apache HttpClient](https://hc.apache.org/httpcomponents-client-4.5.x/index.html)实现。

我们需要[Maven依赖项](https://search.maven.org/artifact/org.apache.httpcomponents/httpclient)：

```xml
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.12</version>
</dependency>
```

创建RestTemplate实例时，我们应该告诉它我们正在使用Apache HttpClient：

```java
RestTemplate restTemplate = new RestTemplate();
restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
```

然后，让我们在application.properties文件中配置客户端记录器：

```properties
logging.level.org.apache.http=DEBUG
logging.level.httpclient.wire=DEBUG
```

现在我们可以看到请求/响应头和正文：

```bash
    o.a.http.headers - http-outgoing-0 >> POST /spring-rest/persons HTTP/1.1
    o.a.http.headers - http-outgoing-0 >> Accept: text/plain, application/json, application/*+json, */*
// ... more request headers
    o.a.http.headers - http-outgoing-0 >> User-Agent: Apache-HttpClient/4.5.9 (Java/1.8.0_171)
    o.a.http.headers - http-outgoing-0 >> Accept-Encoding: gzip,deflate
org.apache.http.wire - http-outgoing-0 >> "POST /spring-rest/persons HTTP/1.1[\r][\n]"
org.apache.http.wire - http-outgoing-0 >> "Accept: text/plain, application/json, application/*+json, */*[\r][\n]"
org.apache.http.wire - http-outgoing-0 >> "Content-Type: text/plain;charset=ISO-8859-1[\r][\n]"
// ... more request headers
org.apache.http.wire - http-outgoing-0 >> "[\r][\n]"
org.apache.http.wire - http-outgoing-0 >> "my request body"
org.apache.http.wire - http-outgoing-0 << "HTTP/1.1 200 [\r][\n]"
org.apache.http.wire - http-outgoing-0 << "Content-Type: application/json[\r][\n]"
// ... more response headers
org.apache.http.wire - http-outgoing-0 << "Connection: keep-alive[\r][\n]"
org.apache.http.wire - http-outgoing-0 << "[\r][\n]"
org.apache.http.wire - http-outgoing-0 << "21[\r][\n]"
org.apache.http.wire - http-outgoing-0 << "["Lucie","Jackie","Danesh","Tao"][\r][\n]"
```

但是，这些日志很冗长，不便于调试。

我们将在下一章中看到如何解决这个问题。

## 4. 使用RestTemplate拦截器记录正文

作为另一种解决方案，我们可以为[RestTemplate配置拦截器](https://www.baeldung.com/spring-rest-template-interceptor)。

### 4.1 日志拦截器实现

首先，让我们创建一个新的LoggingInterceptor来自定义我们的日志。这个拦截器将请求主体记录为一个简单的字节数组。但是，对于响应，我们必须阅读整个正文流：

```java
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    static Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest req, byte[] reqBody, ClientHttpRequestExecution ex) throws IOException {
        LOGGER.debug("Request body: {}", new String(reqBody, StandardCharsets.UTF_8));
        ClientHttpResponse response = ex.execute(req, reqBody);
        InputStreamReader isr = new InputStreamReader(response.getBody(), StandardCharsets.UTF_8);
        String body = new BufferedReader(isr).lines()
              .collect(Collectors.joining("\n"));
        LOGGER.debug("Response body: {}", body);
        return response;
    }
}
```

当心，这个拦截器对响应内容本身有影响，我们将在下一章中发现。

### 4.2 将拦截器与RestTemplate一起使用

现在，我们必须处理一个流问题：当拦截器使用响应流时，我们的客户端应用程序将看到一个空的响应主体。

为避免这种情况，我们应该使用BufferingClientHttpRequestFactory：它将流内容缓冲到内存中。这样，它可以被读取两次：一次被我们的拦截器读取，第二次被我们的客户端应用程序读取：

```java
ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
RestTemplate restTemplate = new RestTemplate(factory);
```

然而，使用这个工厂有一个性能缺陷，我们将在下一小节中描述。

然后我们可以将我们的日志记录拦截器添加到RestTemplate实例，我们将把它附加在现有拦截器之后，如果有的话：

```java
List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
if (CollectionUtils.isEmpty(interceptors)) {
    interceptors = new ArrayList<>();
}
interceptors.add(new LoggingInterceptor());
restTemplate.setInterceptors(interceptors);
```

因此，日志中只会显示必要的信息：

```bash
c.t.t.r.l.LoggingInterceptor - Request body: my request body
c.t.t.r.l.LoggingInterceptor - Response body: ["Lucie","Jackie","Danesh","Tao"]
```

### 4.3 RestTemplate拦截器缺点

如前所述，使用BufferingClientHttpRequestFactory有一个严重的缺点：它抵消了流式处理的好处。因此，将整个身体数据加载到内存中可能会使我们的应用程序面临性能问题。更糟糕的是，它可能导致OutOfMemoryError。

为防止这种情况，一种可能的选择是假设当数据量增加时这些详细日志将被关闭，这通常发生在生产中。例如，只有在记录器上启用DEBUG级别时，我们才能使用缓冲的RestTemplate实例：

```java
RestTemplate restTemplate = null;
if (LOGGER.isDebugEnabled()) {
    ClientHttpRequestFactory factory 
    = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
    restTemplate = new RestTemplate(factory);
} else {
    restTemplate = new RestTemplate();
}
```

同样，我们将确保我们的拦截器仅在启用DEBUG日志记录时读取响应：

```java
if (LOGGER.isDebugEnabled()) {
    InputStreamReader isr = new InputStreamReader(response.getBody(), StandardCharsets.UTF_8);
    String body = new BufferedReader(isr)
        .lines()
        .collect(Collectors.joining("\n"));
    LOGGER.debug("Response body: {}", body);
}
```

## 5. 总结

RestTemplate请求/响应日志记录不是一件简单的事情，因为Spring Boot没有开箱即用。

幸运的是，我们已经看到我们可以使用Apache HttpClient记录器来获取交换数据的详细跟踪。

或者，我们可以实现自定义拦截器来获取更多人类可读的日志。但是，这可能会导致大数据量的性能缺陷。