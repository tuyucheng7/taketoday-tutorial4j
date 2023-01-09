## 1. 概述

[Apache HttpClient](https://www.baeldung.com/httpclient-guide)是一个流行的Java库，提供高效且功能丰富的程序包，可实现最新 HTTP 标准的客户端。该库专为扩展而设计，同时为基本 HTTP 方法提供强大的支持。

在本教程中，我们将了解 Apache HttpClient API 设计。我们将解释HttpClient和CloseableHttpClient之间的区别。此外，我们将检查如何使用HttpClients或HttpClientBuilder创建CloseableHttpClient实例。

最后，我们将推荐我们应该在自定义代码中使用上述哪些 API。此外，我们还将查看哪些 API 类实现了Closeable接口，从而要求我们关闭它们的实例以释放资源。

## 2. API设计

让我们从了解 API 的设计方式开始，重点关注其高级类和接口。在下面的类图中，我们将展示经典执行 HTTP 请求和处理 HTTP 响应所需的 API 的一部分：

[![Apache 经典 HTTP 客户端](https://www.baeldung.com/wp-content/uploads/2022/04/apache_httpclient7.png)](https://www.baeldung.com/wp-content/uploads/2022/04/apache_httpclient7.png)

此外，Apache HttpClient API 还支持[异步](https://www.baeldung.com/httpasyncclient-tutorial)HTTP 请求/响应交换，以及使用[RxJava](https://www.baeldung.com/rx-java)的反应式消息交换。

## 3. HttpClient与CloseableHttpClient

HttpClient是代表 HTTP 请求执行的基本契约的高级接口。它对请求执行过程没有任何限制。此外，它将状态管理、身份验证和重定向等细节留给各个客户端实现。

我们可以将任何客户端实现转换为HttpClient接口。因此，我们可以使用它通过默认的客户端实现来执行基本的 HTTP 请求：

```java
HttpClient httpClient = HttpClients.createDefault();
HttpGet httpGet = new HttpGet(serviceUrl);
HttpResponse response = httpClient.execute(httpGet);
assertThat(response.getCode()).isEqualTo(HttpStatus.SC_OK);
```

但是，上面的代码将导致[SonarQube](https://www.baeldung.com/sonar-qube)上出现[阻塞问题](https://rules.sonarsource.com/java/RSPEC-2095)。原因是默认的客户端实现返回一个Closeable HttpClient的实例，它需要关闭。

CloseableHttpClient是一个抽象类，表示HttpClient接口的基本实现。但是，它还实现了Closeable接口。因此，我们应该在使用后关闭它的所有实例。我们可以通过使用[try-with-resources](https://www.baeldung.com/java-try-with-resources)或在finally子句中调用close方法来关闭它们：

```java
try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
    HttpGet httpGet = new HttpGet(serviceUrl);
    HttpResponse response = httpClient.execute(httpGet);
    assertThat(response.getCode()).isEqualTo(HttpStatus.SC_OK);
}
```

因此，在我们的自定义代码中，我们应该使用CloseableHttpClient类，而不是HttpClient接口。

## 4. HttpClients与HttpClientBuilder

在上面的示例中，我们使用了HttpClients类中的静态方法来获取默认客户端实现。HttpClients是一个实用程序类，包含用于创建CloseableHttpClient实例的工厂方法：

```java
CloseableHttpClient httpClient = HttpClients.createDefault();
```

我们可以使用HttpClientBuilder类实现相同的目的。HttpClientBuilder是用于创建CloseableHttpClient实例的[Builder 设计模式](https://www.baeldung.com/creational-design-patterns#builder)的实现：

```java
CloseableHttpClient httpClient = HttpClientBuilder.create().build();
```

在内部，HttpClients使用HttpClientBuilder创建客户端实现实例。因此，我们应该更愿意在我们的自定义代码中使用HttpClients 。鉴于它是一个更高级别的类，它的内部结构可能会随着新版本的发布而改变。

## 5. 资源管理

一旦超出范围，我们需要关闭CloseableHttpClient实例的原因是关闭关联的连接管理器。此外，我们还应该使用CloseableHttpResponse以确保系统资源的正确释放。

### 5.1. 可关闭的HttpResponse

CloseableHttpResponse是实现ClassicHttpResponse接口的类。但是，ClassicHttpResponse还扩展了HttpResponse、HttpEntityContainer和Closeable接口。

底层HTTP 连接由响应对象持有，以允许直接从网络套接字流式传输响应内容。因此，我们应该在自定义代码中使用CloseableHttpResponse类而不是HttpResponse接口。我们还需要确保在使用响应后调用close方法：

```java
try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
    HttpGet httpGet = new HttpGet(serviceUrl);
    try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
        HttpEntity entity = response.getEntity();
        EntityUtils.consume(entity);
    }
}
```

我们应该注意到，当响应内容没有被完全消耗时，底层连接不能被安全地重用。在这种情况下，连接将被关闭并被连接管理器丢弃。

### 5.2. 重用客户端

关闭CloseableHttpClient实例并为每个请求创建一个新实例可能是一项昂贵的操作。相反，我们可以重用CloseableHttpClient的单个实例来发送多个请求：

```java
try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
    HttpGet httpGetOne = new HttpGet(serviceOneUrl);
    try (CloseableHttpResponse responseOne = httpClient.execute(httpGetOne)) {
        HttpEntity entityOne = responseOne.getEntity();
        EntityUtils.consume(entityOne);
        assertThat(responseOne.getCode()).isEqualTo(HttpStatus.SC_OK);
    }

    HttpGet httpGetTwo = new HttpGet(serviceTwoUrl);
    try (CloseableHttpResponse responseTwo = httpClient.execute(httpGetTwo)) {
        HttpEntity entityTwo = responseTwo.getEntity();
        EntityUtils.consume(entityTwo);
        assertThat(responseTwo.getCode()).isEqualTo(HttpStatus.SC_OK);
    }
}
```

因此，我们避免关闭内部关联的连接管理器并创建一个新连接管理器。

## 六. 总结

在本文中，我们 探讨了 Apache HttpClient 的经典 HTTP API，这是一个流行的Java客户端 HTTP 库。

我们了解了 HttpClient 和 CloseableHttpClient之间的区别。此外，我们建议在自定义代码中使用CloseableHttpClient。接下来，我们了解了如何使用HttpClients 或 HttpClientBuilder创建CloseableHttpClient实例 。

最后，我们查看了CloseableHttpClient和CloseableHttpResponse类，它们都实现了Closeable 接口。我们看到他们的实例应该关闭以释放资源。