## 1. 概述

在本教程中，我们将探索[Spring WebFlux](https://www.baeldung.com/spring-5-functional-web)中的WebClient过滤器，这是一个函数式的响应式Web框架。

## 2. 请求过滤器

过滤器可以拦截、检查和修改客户端请求(或响应)。过滤器非常适合为每个请求添加功能，因为逻辑保留在一个地方。用例包括监视、修改、记录和验证客户端请求，仅举几例。

请求具有由零个或多个过滤器组成的有序链。

在Spring Reactive中，过滤器是函数接口ExchangeFilterFunction的实例。filter函数有两个参数：要修改的ClientRequest和下一个ExchangeFilterFunction。

通常，filter函数通过调用过滤器链中的下一个函数返回：

```java
ExchangeFilterFunction filterFunction = (clientRequest, nextFilter) -> {
    LOG.info("WebClient fitler executed");
    return nextFilter.exchange(clientRequest);
};
```

## 3. WebClient过滤器

实现请求过滤器后，我们必须将其“附加”到WebClient实例。这只能在创建WebClient时完成。

那么，让我们看看如何创建WebClient。第一个选项是使用或不使用基本URL调用WebClient.create()：

```java
WebClient webClient = WebClient.create();
```

不幸的是，这不允许添加过滤器。那么，第二个选项就是我们正在寻找的选项。

通过使用WebClient.builder()我们可以添加过滤器：

```java
WebClient webClient = WebClient.builder()
  	.filter(filterFunction)
  	.build();
```

## 4. 自定义过滤器

让我们从计算客户端发送的HTTP GET请求的过滤器开始。

过滤器检查请求方法并在GET请求的情况下自增“全局”计数器：

```java
ExchangeFilterFunction countingFunction = (clientRequest, nextFilter) -> {
    HttpMethod httpMethod = clientRequest.method();
    if (httpMethod == HttpMethod.GET) {
        getCounter.incrementAndGet();
    }
    return nextFilter.exchange(clientRequest);
};
```

我们将定义的第二个过滤器将版本号附加到请求URL路径。我们利用ClientRequest.from()方法从当前请求对象创建一个新的请求对象并设置修改后的URL。

随后，我们继续使用新修改的请求对象执行过滤器链：

```java
ExchangeFilterFunction urlModifyingFilter = (clientRequest, nextFilter) -> {
    String oldUrl = clientRequest.url().toString();
    URI newUrl = URI.create(oldUrl + "/" + version);
    ClientRequest filteredRequest = ClientRequest.from(clientRequest)
      	.url(newUrl)
      	.build();
    return nextFilter.exchange(filteredRequest);
};
```

接下来，让我们定义一个过滤器来记录发送请求的方法及其URL。这些详细信息在请求对象中可用。

然后，我们所要做的就是打印输出流：

```java
ExchangeFilterFunction loggingFilter = (clientRequest, nextFilter) -> {
    printStream.print("Sending request " + clientRequest.method() + " " + clientRequest.url());
    return nextFilter.exchange(clientRequest);
};
```

## 5. 标准过滤器

**最后，让我们看看基本身份验证**-一个非常常见的请求过滤用例。

工具类ExchangeFilterFunctions提供了basicAuthentication()过滤器函数，它负责将authorization标头添加到请求中。

因此，我们不需要为它定义过滤器：

```java
WebClient webClient = WebClient.builder()
  	.filter(ExchangeFilterFunctions.basicAuthentication(user, password))
  	.build();

```

## 6. 总结

在这篇简短的文章中，我们探讨了在Spring中过滤WebFlux客户端。