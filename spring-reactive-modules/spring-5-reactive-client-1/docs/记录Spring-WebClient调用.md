## 1. 概述

在本教程中，我们将展示如何自定义Spring的[WebClient](https://www.baeldung.com/spring-5-webclient)(一个响应式HTTP客户端)来记录请求和响应。

## 2. WebClient

WebClient是一个基于[Spring WebFlux](https://www.baeldung.com/spring-webflux)的HTTP请求的响应式和非阻塞接口。它有一个函数式、流式的API，其中包含用于声明式组合的响应类型。

在幕后，WebClient调用HTTP客户端。Reactor Netty是默认的，同时也支持Jetty的响应式HttpClient。此外，可以通过为WebClient设置ClientConnector来插入HTTP客户端的其他实现。

## 3. 记录请求和响应

WebClient默认使用的HttpClient是Netty实现的，所以当我们**将reactor.netty.http.client日志记录级别更改为DEBUG**后，我们可以看到一些请求日志记录，但是如果我们需要自定义日志，我们可以通过[WebClient#filters](https://www.baeldung.com/spring-webclient-filters)配置我们的记录器：

```java
WebClient
  	.builder()
  	.filters(exchangeFilterFunctions -> {
      	exchangeFilterFunctions.add(logRequest());
      	exchangeFilterFunctions.add(logResponse());
  	})
  	.build()
```

在此代码片段中，我们添加了两个单独的过滤器来记录请求和响应。

让我们使用ExchangeFilterFunction#ofRequestProcessor来实现logRequest：

```java
ExchangeFilterFunction logRequest() {
    return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
        if (log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder("Request: \n");
            //append clientRequest method and url
            clientRequest
                .headers()
                .forEach((name, values) -> values.forEach(value -> /* append header key/value */));
            log.debug(sb.toString());
        }
        return Mono.just(clientRequest);
    });
}
```

logResponse是相同的，**但我们必须改用ExchangeFilterFunction#ofResponseProcessor**。

现在我们可以将reactor.netty.http.client日志级别更改为INFO或ERROR以获得更清晰的输出。

## 4. 记录请求和响应主体

HTTP客户端具有记录请求和响应主体的功能。因此，**为了实现这个目标，我们将在我们的WebClient中使用启用日志的HTTP客户端**。

我们可以通过手动设置WebClient.Builder#clientConnector来做到这一点-让我们看看Jetty和Netty HTTP客户端。

### 4.1 使用Jetty HttpClient进行日志记录

首先，让我们将[jetty-reactive-httpclient](https://central.sonatype.com/artifact/org.eclipse.jetty/jetty-reactive-httpclient/3.0.8)的Maven依赖添加到我们的pom中：

```xml
<dependency>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-reactive-httpclient</artifactId>
    <version>1.1.6</version>
</dependency>
```

然后我们将创建一个自定义的Jetty HttpClient：

```java
SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
HttpClient httpClient = new HttpClient(sslContextFactory) {
    @Override
    public Request newRequest(URI uri) {
        Request request = super.newRequest(uri);
        return enhance(request);
    }
};
```

在这里，我们覆盖了HttpClient#newRequest，然后将Request包装在日志增强器中。

接下来，我们需要在请求中注册事件，以便我们可以在请求的每个部分可用时进行记录：

```java
Request enhance(Request request) {
    StringBuilder group = new StringBuilder();
    request.onRequestBegin(theRequest -> {
        // append request url and method to group
    });
    request.onRequestHeaders(theRequest -> {
        for (HttpField header : theRequest.getHeaders()) {
            // append request headers to group
        }
    });
    request.onRequestContent((theRequest, content) -> {
        // append content to group
    });
    request.onRequestSuccess(theRequest -> {
        log.debug(group.toString());
        group.delete(0, group.length());
    });
    group.append("\n");
    request.onResponseBegin(theResponse -> {
        // append response status to group
    });
    request.onResponseHeaders(theResponse -> {
        for (HttpField header : theResponse.getHeaders()) {
            // append response headers to group
        }
    });
    request.onResponseContent((theResponse, content) -> {
        // append content to group
    });
    request.onResponseSuccess(theResponse -> {
        log.debug(group.toString());
    });
    return request;
}
```

最后，我们必须构建WebClient实例：

```java
WebClient
    .builder()
    .clientConnector(new JettyClientHttpConnector(httpClient))
    .build()
```

当然，和之前一样，我们需要将RequestLogEnhancer的日志级别设置为DEBUG。

### 4.2 使用Netty HttpClient进行日志记录

首先，让我们创建一个Netty HttpClient：

```java
HttpClient httpClient = HttpClient
  	.create()
  	.wiretap(true)
```

启用窃听后，每个请求和响应都将被详细记录。

接下来，我们必须将Netty的客户端包reactor.netty.http.client的日志级别设置为DEBUG：

```properties
logging.level.reactor.netty.http.client=DEBUG
```

现在，让我们构建WebClient：

```java
WebClient
    .builder()
    .clientConnector(new ReactorClientHttpConnector(httpClient))
    .build()
```

我们的WebClient将详细记录每个请求和响应，**但Netty内置记录器的默认格式包含主体的十六进制和文本表示以及大量有关请求和响应事件的数据**。

因此，如果我们只需要Netty的文本记录器，我们可以配置HttpClient：

```java
HttpClient httpClient = HttpClient
  	.create()
  	.wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);
```

## 5. 总结

在本教程中，我们在使用Spring WebClient时使用了多种技术来记录请求和响应数据。