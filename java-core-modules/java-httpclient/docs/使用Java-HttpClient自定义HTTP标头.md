## 1. 概述

Java 11 正式引入了[Java HttpClient](https://www.baeldung.com/java-9-http-client)。在此之前，当我们需要使用 HTTP 客户端时，我们通常会使用[Apache HttpClient](https://www.baeldung.com/httpclient-guide)等第三方库。

在这个简短的教程中，我们将了解如何使用JavaHttpClient 添加自定义 HTTP 标头。

## 2.自定义HTTP标头

我们可以使用HttpRequest.Builder对象的三种方法之一轻松添加自定义标头：header、headers或setHeader。让我们看看他们的行动。

### 2.1. 使用header()方法

header ()方法允许我们一次添加一个标题。

我们可以根据需要多次添加相同的标头名称，如下例所示，它们都会被发送：

```java
HttpClient httpClient = HttpClient.newHttpClient();

HttpRequest request = HttpRequest.newBuilder()
  .header("X-Our-Header-1", "value1")
  .header("X-Our-Header-1", "value2")
  .header("X-Our-Header-2", "value2")
  .uri(new URI(url)).build();

return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
```

### 2.2. 使用headers()方法

如果我们想同时添加多个header，可以使用headers() 方法：

```java
HttpRequest request = HttpRequest.newBuilder()
  .headers("X-Our-Header-1", "value1", "X-Our-Header-2", "value2")
  .uri(new URI(url)).build();
```

此方法还允许我们向一个标头名称添加多个值：

```java
HttpRequest request = HttpRequest.newBuilder()
  .headers("X-Our-Header-1", "value1", "X-Our-Header-1", "value2")
  .uri(new URI(url)).build();
```

### 2.3. 使用setHeader()方法

最后，我们可以使用setHeader()方法来添加标题。但是，与header()方法不同，如果我们多次使用相同的标头名称，它将覆盖我们之前使用该名称设置的任何标头：

```java
HttpRequest request = HttpRequest.newBuilder()
  .setHeader("X-Our-Header-1", "value1")
  .setHeader("X-Our-Header-1", "value2")
  .uri(new URI(url)).build();
```

在上面的示例中，我们的标头的值为“value2”。

## 3.总结

总之，我们学习了使用JavaHttpClient 添加自定义 HTTP 标头的不同方法。