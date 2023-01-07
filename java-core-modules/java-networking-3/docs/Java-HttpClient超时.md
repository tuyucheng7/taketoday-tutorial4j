## 1. 概述

在本教程中，我们将展示如何使用从Java11 开始可用的新JavaHTTP 客户端和 Java.

[如果我们需要更新知识，我们可以从Java HTTP Client](https://www.baeldung.com/java-9-http-client)教程开始。

另一方面，要了解如何使用旧库设置超时，请参阅[HttpUrlConnection。](https://www.baeldung.com/java-http-request#configuring-timeouts)

## 2.配置超时

首先，我们需要设置一个 HttpClient 以便能够发出 HTTP 请求：

```java
private static HttpClient getHttpClientWithTimeout(int seconds) {
    return HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(seconds))
      .build();
}
```

上面，我们创建了一个方法，该方法返回一个配置有超时定义为参数的HttpClient。很快，我们使用[Builder 设计模式](https://www.baeldung.com/creational-design-patterns#builder)实例化一个HttpClient并使用connectTimeout方法配置超时。此外，使用静态方法ofSeconds，我们创建了一个Duration对象的实例，它以秒为单位定义超时。

之后，我们检查HttpClient超时是否配置正确：

```java
httpClient.connectTimeout().map(Duration::toSeconds)
  .ifPresent(sec -> System.out.println("Timeout in seconds: " + sec));
```

因此，我们使用connectTimeout方法来获取超时时间。结果，它返回一个Duration的Optional ，我们将其映射到秒。

## 3. 处理超时

此外，我们需要创建一个HttpRequest对象，我们的客户端将使用它来发出 HTTP 请求：

```java
HttpRequest httpRequest = HttpRequest.newBuilder()
  .uri(URI.create("http://10.255.255.1")).GET().build();
```

为了模拟超时，我们调用一个不可路由的 IP 地址。换句话说，所有 TCP 数据包都会在之前配置的预定义持续时间后丢弃并强制超时。

现在，让我们更深入地了解如何处理超时。

### 3.1. 处理同步调用超时

例如，要进行同步调用，请使用send方法：

```java
HttpConnectTimeoutException thrown = assertThrows(
  HttpConnectTimeoutException.class,
  () -> httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()),
  "Expected send() to throw HttpConnectTimeoutException, but it didn't");
assertTrue(thrown.getMessage().contains("timed out"));
```

同步调用强制捕获HttpConnectTimeoutException扩展的IOException。因此，在上面的测试中，我们期望HttpConnectTimeoutException带有错误消息。

### 3.2. 处理异步调用超时

同样，要进行异步调用，请使用sendAsync方法：

```java
CompletableFuture<String> completableFuture = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
  .thenApply(HttpResponse::body)
  .exceptionally(Throwable::getMessage);
String response = completableFuture.get(5, TimeUnit.SECONDS);
assertTrue(response.contains("timed out"));
```

上面对sendAsync的调用 返回一个CompletableFuture<HttpResponse>。因此，我们需要定义如何在功能上处理响应。详细地说，当没有错误发生时，我们从响应中获取主体。否则，我们会从 throwable 中获取错误消息。最后，我们通过等待最多 5 秒从CompletableFuture获得结果 。同样，这个请求会在 3 秒后抛出一个HttpConnectTimeoutException ，正如我们所期望的那样。

## 4.在请求级别配置超时

上面，我们为同步和异步调用重用了相同的客户端实例。但是，我们可能希望针对每个请求以不同方式处理超时。同样，我们可以为单个请求设置超时：

```java
HttpRequest httpRequest = HttpRequest.newBuilder()
  .uri(URI.create("http://10.255.255.1"))
  .timeout(Duration.ofSeconds(1))
  .GET()
  .build();
```

同样，我们正在使用timeout方法来设置此请求的超时时间。在这里，我们为这个请求配置了 1 秒的超时。

客户端和请求之间的最短持续时间设置请求的超时时间。

## 5。总结

在本文中，我们使用新的JavaHTTP 客户端成功配置了超时，并在超时溢出时优雅地处理请求。