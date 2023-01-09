## 1. 简介

在本教程中，我们将探索几个用于在Java中将 HTTP 响应主体读取为字符串的库。从第一个版本开始，Java 就提供了[HttpURLConnection](https://www.baeldung.com/java-http-request) API。这仅包括基本功能，并且以不太用户友好而著称。

在 JDK 11 中，Java 引入了新的和改进的[HttpClient](https://www.baeldung.com/java-9-http-client) API 来处理 HTTP 通信。我们将讨论这些库，并检查替代方案，例如[Apache HttpClient](https://www.baeldung.com/httpclient-guide)和[Spring Rest Template](https://www.baeldung.com/rest-template)。

## 2.http客户端

如前所述，[HttpClient](https://www.baeldung.com/java-9-http-client)是在Java11 中加入的。它允许我们通过网络访问资源，但与HttpURLConnection不同的是，HttpClient支持 HTTP/1.1 和 HTTP/2。而且，它同时提供了同步和异步请求类型。

HttpClient 提供了一个具有很多灵活性和强大功能的现代 API。该 API 由三个核心类组成：HttpClient、HttpRequest和HttpResponse。

HttpResponse描述了HttpRequest调用的结果。HttpResponse不是直接创建的，而是在完全接收到正文后可用。

要将响应主体读取为字符串，我们首先需要创建简单的客户端和请求对象：

```java
HttpClient client = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create(DUMMY_URL))
    .build();
```

然后我们将使用BodyHandlers 并调用方法ofString()来返回响应：

```java
HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
```

## 3.HttpURL连接

[HttpURLConnection](https://www.baeldung.com/java-http-request)是一个轻量级的 HTTP 客户端，用于通过 HTTP 或 HTTPS 协议访问资源，它允许我们创建一个InputStream。一旦我们获得了InputStream，我们就可以像读取普通的本地文件一样读取它。

在Java中，我们可以用来上网的类主要有java.net.URL类和java.net.HttpURLConnection类。首先，我们将使用URL类指向网络资源。然后我们可以使用HttpURLConnection类访问它。

要从URL获取响应主体作为String，我们应该首先使用我们的URL创建一个HttpURLConnection：

```java
HttpURLConnection connection = (HttpURLConnection) new URL(DUMMY_URL).openConnection();
```

新的URL(DUMMY_URL).openConnection()返回一个HttpURLConnection。该对象允许我们添加标头或检查响应代码。

接下来，我们将从连接对象中获取InputStream ：

```java
InputStream inputStream = connection.getInputStream();
```

最后，我们需要[将InputStream转换为String](https://www.baeldung.com/convert-input-stream-to-string)。

## 4.Apache HTTP客户端

在本节中，我们将学习如何使用[Apache HttpClient](https://www.baeldung.com/httpclient-guide) 将 HTTP 响应主体作为字符串读取。

要使用这个库，我们需要将它的依赖添加到我们的 Maven 项目中：

```xml
<dependency>
    <groupId>org.apache.httpcomponents.client5</groupId>
    <artifactId>httpclient5</artifactId>
    <version>5.2</version>
</dependency>
```

我们可以通过CloseableHttpClient类检索和发送数据。要使用默认配置创建它的实例，我们可以使用HttpClients.createDefault()。

CloseableHttpClient提供了一个执行方法来发送和接收数据。此方法使用 2 个参数。第一个参数是HttpUriRequest类型，它有很多子类，包括HttpGet和HttpPost。第二个参数是[HttpClientResponseHandler类型，它从](https://hc.apache.org/httpcomponents-core-5.2.x/current/httpcore5/apidocs/org/apache/hc/core5/http/io/HttpClientResponseHandler.html)ClassicHttpResponse生成一个响应对象。

首先，我们将创建一个HttpGet对象：

```java
HttpGet request = new HttpGet(DUMMY_URL);
```

其次，我们将创建客户端：

```java
CloseableHttpClient client = HttpClients.createDefault();
```

最后，我们将从execute方法的结果中检索响应对象：

```java
String response = client.execute(request, new BasicHttpClientResponseHandler());
logger.debug("Response -> {}", response);
```

这里我们使用了BasicHttpClientResponseHandler，它将响应主体作为字符串返回。

## 5.Spring Rest模板

在本节中，我们将演示如何使用[Spring RestTemplate](https://www.baeldung.com/rest-template)将 HTTP 响应主体作为字符串读取。我们必须注意 RestTemplate 现在已被弃用。因此，我们应该考虑使用 Spring WebClient，如下一节所述。

RestTemplate类是Spring提供的一个基本工具，它提供了一个简单的模板，用于在底层 HTTP 客户端库(例如 JDK HttpURLConnection、Apache HttpClient等)上进行客户端 HTTP 操作。

RestTemplate提供了[一些有用的方法](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html)来创建 HTTP 请求和处理响应。

我们可以通过首先向我们的 Maven 项目添加一些依赖项来使用这个库：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>${spring-boot.version}</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <version>${spring-boot.version}</version>
    <scope>test</scope>
</dependency>
```

要发出 Web 请求并将响应主体作为字符串返回，我们将创建一个RestTemplate实例：

```java
RestTemplate restTemplate = new RestTemplate();
```

然后我们将通过调用方法getForObject()并传入 URL 和所需的响应类型来获取响应对象。我们将在示例中使用String.class ：

```java
String response = restTemplate.getForObject(DUMMY_URL, String.class);
```

## 6. 春季网络客户端

最后，我们将看到如何使用[Spring WebClient，](https://www.baeldung.com/spring-5-webclient)这是一种反应式、非阻塞的解决方案，取代了 Spring RestTemplate。

我们可以通过将[spring-boot-starter-webflux](https://search.maven.org/classic/#search|ga|1|a%3A"spring-boot-starter-webflux" AND g%3A"org.springframework.boot")依赖项添加到我们的 Maven 项目来使用这个库：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

执行 HTTP Get 请求的最简单方法是使用create方法：

```java
WebClient webClient = WebClient.create(DUMMY_URL);
```

执行 HTTP Get 请求的最简单方法是调用get和retrieve方法。然后我们将使用带有String.class类型的bodyToMono方法将正文提取为单个 String 实例：

```java
Mono<String> body = webClient.get().retrieve().bodyToMono(String.class);
```

最后，我们将 调用block方法来告诉 web flux 等待整个 body 流被读取并到 String 结果中：

```java
String s = body.block();
```

## 七. 总结

在本文中，我们学习了如何使用多个库将 HTTP 响应主体读取为String。