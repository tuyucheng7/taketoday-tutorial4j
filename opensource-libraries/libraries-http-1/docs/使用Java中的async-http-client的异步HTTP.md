## 1. 概述

[AsyncHttpClient (AHC) 是一个构建在](https://github.com/AsyncHttpClient/async-http-client)[Netty](https://www.baeldung.com/netty)之上的库，目的是轻松执行 HTTP 请求并异步处理响应。

在本文中，我们将介绍如何配置和使用 HTTP 客户端，如何使用 AHC 执行请求和处理响应。

## 2.设置

可以在[Maven 存储库](https://mvnrepository.com/artifact/org.asynchttpclient/async-http-client)中找到该库的最新版本。我们应该小心使用组 ID为 org.asynchttpclient的依赖项，而不是使用com.ning 的依赖项：

```xml
<dependency>
    <groupId>org.asynchttpclient</groupId>
    <artifactId>async-http-client</artifactId>
    <version>2.2.0</version>
</dependency>
```

## 3.HTTP客户端配置

获取 HTTP 客户端的最直接方法是使用Dsl类。静态asyncHttpClient()方法返回一个AsyncHttpClient对象：

```java
AsyncHttpClient client = Dsl.asyncHttpClient();
```

如果我们需要 HTTP 客户端的自定义配置，我们可以使用构建器DefaultAsyncHttpClientConfig.Builder构建AsyncHttpClient对象：

```java
DefaultAsyncHttpClientConfig.Builder clientBuilder = Dsl.config()
```

这提供了配置超时、代理服务器、HTTP 证书等的可能性：

```java
DefaultAsyncHttpClientConfig.Builder clientBuilder = Dsl.config()
  .setConnectTimeout(500)
  .setProxyServer(new ProxyServer(...));
AsyncHttpClient client = Dsl.asyncHttpClient(clientBuilder);
```

一旦我们配置并获得了 HTTP 客户端的实例，我们就可以在整个应用程序中重用它。我们不需要为每个请求都创建一个实例，因为它在内部会创建新的线程和连接池，这会导致性能问题。

此外，重要的是要注意，一旦我们完成使用客户端，我们应该调用close()方法以防止任何内存泄漏或挂起资源。

## 4. 创建 HTTP 请求

我们可以通过两种方法使用 AHC 定义 HTTP 请求：

-   边界
-   未绑定

就性能而言，这两种请求类型之间没有重大差异。它们只代表我们可以用来定义请求的两个单独的 API。绑定请求与创建它的 HTTP 客户端相关联，如果没有另外指定，默认情况下将使用该特定客户端的配置。

例如，当创建绑定请求时，会从 HTTP 客户端配置中读取disableUrlEncoding标志，而对于未绑定请求，此标志默认设置为 false。这很有用，因为可以通过使用作为 VM 参数传递的系统属性来更改客户端配置，而无需重新编译整个应用程序：

```java
java -jar -Dorg.asynchttpclient.disableUrlEncodingForBoundRequests=true
```

可以在ahc-default.properties文件中找到完整的属性列表。

### 4.1. 绑定请求

要创建绑定请求，我们使用AsyncHttpClient类中以前缀“prepare”开头的辅助方法。此外，我们可以使用prepareRequest()方法接收已创建的Request对象。

例如，prepareGet()方法将创建一个 HTTP GET 请求：

```java
BoundRequestBuilder getRequest = client.prepareGet("http://www.baeldung.com");
```

### 4.2. 未绑定请求

可以使用RequestBuilder类创建未绑定请求：

```java
Request getRequest = new RequestBuilder(HttpConstants.Methods.GET)
  .setUrl("http://www.baeldung.com")
  .build();
```

或者使用Dsl帮助类，它实际上使用RequestBuilder来配置请求的 HTTP 方法和 URL：

```java
Request getRequest = Dsl.get("http://www.baeldung.com").build()
```

## 5. 执行 HTTP 请求

库的名称为我们提供了有关如何执行请求的提示。AHC 支持同步和异步请求。

执行请求取决于它的类型。当使用绑定请求时，我们使用BoundRequestBuilder类中的execute()方法，当我们有未绑定请求时，我们将使用AsyncHttpClient接口中的 executeRequest() 方法的实现之一来执行它。

### 5.1. 同步地

该库被设计为异步的，但在需要时我们可以通过阻塞Future对象来模拟同步调用。execute()和executeRequest()方法都返回一个ListenableFuture<Response>对象。该类扩展了Java的Future接口，从而继承了get()方法，可以用来阻塞当前线程，直到HTTP请求完成并返回响应：

```java
Future<Response> responseFuture = boundGetRequest.execute();
responseFuture.get();
Future<Response> responseFuture = client.executeRequest(unboundRequest);
responseFuture.get();
```

在尝试调试部分代码时使用同步调用很有用，但不建议在异步执行可带来更好性能和吞吐量的生产环境中使用。

### 5.2. 异步地

当我们谈论异步执行时，我们也谈论处理结果的监听器。AHC 库提供了 3 种类型的侦听器，可用于异步 HTTP 调用：

-   异步处理器
-   异步完成处理器
-   ListenableFuture监听器

AsyncHandler侦听器提供了在 HTTP 调用完成之前控制和处理它的可能性。使用它可以处理与 HTTP 调用相关的一系列事件：

```java
request.execute(new AsyncHandler<Object>() {
    @Override
    public State onStatusReceived(HttpResponseStatus responseStatus)
      throws Exception {
        return null;
    }

    @Override
    public State onHeadersReceived(HttpHeaders headers)
      throws Exception {
        return null;
    }

    @Override
    public State onBodyPartReceived(HttpResponseBodyPart bodyPart)
      throws Exception {
        return null;
    }

    @Override
    public void onThrowable(Throwable t) {

    }

    @Override
    public Object onCompleted() throws Exception {
        return null;
    }
});
```

State枚举让我们控制 HTTP 请求的处理。通过返回State.ABORT，我们可以在特定时刻停止处理，通过使用State.CONTINUE，我们让处理完成。

值得一提的是，AsyncHandler不是线程安全的，在执行并发请求时不应重复使用。

AsyncCompletionHandler继承了AsyncHandler接口的所有方法，并添加了onCompleted(Response)辅助方法来处理调用完成。所有其他侦听器方法都被覆盖以返回状态。CONTINUE，从而使代码更具可读性：

```java
request.execute(new AsyncCompletionHandler<Object>() {
    @Override
    public Object onCompleted(Response response) throws Exception {
        return response;
    }
});
```

ListenableFuture接口允许我们添加将在 HTTP 调用完成时运行的侦听器。

此外，它让我们从监听器中执行代码——通过使用另一个线程池：

```java
ListenableFuture<Response> listenableFuture = client
  .executeRequest(unboundRequest);
listenableFuture.addListener(() -> {
    Response response = listenableFuture.get();
    LOG.debug(response.getStatusCode());
}, Executors.newCachedThreadPool());
```

此外，添加侦听器的选项，ListenableFuture接口让我们可以将Future响应转换为CompletableFuture。

## 七. 总结

AHC 是一个非常强大的库，有很多有趣的特性。它提供了一种非常简单的方法来配置 HTTP 客户端以及执行同步和异步请求的能力。