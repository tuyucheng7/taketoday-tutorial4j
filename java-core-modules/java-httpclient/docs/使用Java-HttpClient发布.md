## 1. 概述

Java [HttpClient](https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpClient.html)[ API 是在Java11 中引入的。](https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpClient.html)该 API实现了最新 HTTP 标准的客户端。它支持 HTTP/1.1 和 HTTP/2，同步和异步编程模型。

我们可以用它来发送 HTTP 请求并检索它们的响应。在Java11 之前，我们不得不依赖基本的[URLConnection](https://www.baeldung.com/java-http-request) 实现或第三方库，例如[Apache HttpClient](https://www.baeldung.com/httpclient-guide)。

在本教程中，我们将了解使用JavaHttpClient发送 POST 请求。我们将展示如何发送同步和异步 POST 请求，以及并发 POST 请求。此外，我们将检查如何向 POST 请求添加身份验证参数和 JSON 主体。

最后，我们将看到如何上传文件和提交表单数据。因此，我们将涵盖大多数常见用例。

## 2.准备POST请求

在我们发送 HTTP 请求之前，我们首先需要创建一个HttpClient实例。

可以使用newBuilder方法从其构建器配置和创建HttpClient实例。否则，如果不需要配置，我们可以使用newHttpClient实用程序方法来创建默认客户端：

```java
HttpClient client = HttpClient.newHttpClient();
```

HttpClient默认使用 HTTP/2。如果服务器不支持 HTTP/2，它也会自动降级到 HTTP/1.1。

现在我们已准备好从其构建器创建HttpRequest的实例。稍后我们将使用客户端实例发送此请求。POST 请求的最少参数是服务器 URL、请求方法和正文：

```java
HttpRequest request = HttpRequest.newBuilder()
  .uri(URI.create(serviceUrl))
  .POST(HttpRequest.BodyPublishers.noBody())
  .build();
```

需要通过BodyPublisher类提供请求正文。它是一个反应流发布者，可以按需发布请求主体流。在我们的示例中，我们使用了一个不发送请求主体的主体发布者。

## 3.发送POST请求

现在我们已经准备好 POST 请求，让我们看看发送它的不同选项。

### 3.1. 同步地

我们可以使用此默认发送方法发送准备好的请求。此方法将阻止我们的代码，直到收到响应为止：

```java
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString())
```

BodyHandlers实用程序实现各种有用的处理程序，例如将响应主体作为字符串处理或将响应主体流式 传输到文件。收到响应后，HttpResponse对象将包含响应状态、标头和正文：

```java
assertThat(response.statusCode())
  .isEqualTo(200);
assertThat(response.body())
  .isEqualTo("{"message":"ok"}");
```

### 3.2. 异步地

我们可以使用sendAsync方法异步发送上一示例中的相同请求。这个方法不会阻塞我们的代码，而是会立即返回一个[CompletableFuture](https://www.baeldung.com/java-completablefuture) 实例：

```java
CompletableFuture<HttpResponse<String>> futureResponse = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
```

CompletableFuture在HttpResponse 可用后完成：

```java
HttpResponse<String> response = futureResponse.get();
assertThat(response.statusCode()).isEqualTo(200);
assertThat(response.body()).isEqualTo("{"message":"ok"}");
```

### 3.3. 同时

我们可以将[Streams](https://www.baeldung.com/java-8-streams)与 CompletableFutures结合起来 ，以便同时发出多个请求并等待它们的响应：

```java
List<CompletableFuture<HttpResponse<String>>> completableFutures = serviceUrls.stream()
  .map(URI::create)
  .map(HttpRequest::newBuilder)
  .map(builder -> builder.POST(HttpRequest.BodyPublishers.noBody()))
  .map(HttpRequest.Builder::build)
  .map(request -> client.sendAsync(request, HttpResponse.BodyHandlers.ofString()))
  .collect(Collectors.toList());
```

现在，让我们等待所有请求完成，以便我们可以一次处理所有响应：

```java
CompletableFuture<List<HttpResponse<String>>> combinedFutures = CompletableFuture
  .allOf(completableFutures.toArray(new CompletableFuture[0]))
  .thenApply(future ->
    completableFutures.stream()
      .map(CompletableFuture::join)
      .collect(Collectors.toList()));
```

当我们使用allOf和join方法组合所有响应时，我们得到一个新的CompletableFuture来保存我们的响应：

```java
List<HttpResponse<String>> responses = combinedFutures.get();
responses.forEach((response) -> {
  assertThat(response.statusCode()).isEqualTo(200);
  assertThat(response.body()).isEqualTo("{"message":"ok"}");
});
```

## 4. 添加认证参数

我们可以在客户端级别为所有请求的 HTTP 身份验证设置一个身份验证器：

```java
HttpClient client = HttpClient.newBuilder()
  .authenticator(new Authenticator() {
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(
        "baeldung",
        "123456".toCharArray());
      }
  })
  .build();
```

但是，HttpClient不会发送基本凭据，直到使用来自服务器的WWW-Authenticate标头对它们进行质询。

为了绕过这个，我们总是可以手动创建和发送基本授权标头：

```java
HttpRequest request = HttpRequest.newBuilder()
  .uri(URI.create(serviceUrl))
  .POST(HttpRequest.BodyPublishers.noBody())
  .header("Authorization", "Basic " + 
    Base64.getEncoder().encodeToString(("baeldung:123456").getBytes()))
  .build();
```

## 5. 添加正文

到目前为止，在示例中，我们还没有向 POST 请求添加任何正文。但是，POST 方法通常用于通过请求体向服务器发送数据。

### 5.1. JSON 正文

BodyPublishers实用程序实现各种有用的发布者，例如从字符串或文件发布请求正文。我们可以将 JSON 数据发布为 String，使用 UTF-8 字符集转换：

```java
HttpRequest request = HttpRequest.newBuilder()
  .uri(URI.create(serviceUrl))
  .POST(HttpRequest.BodyPublishers.ofString("{"action":"hello"}"))
  .build();
```

### 5.2. 上传文件

让我们创建一个[临时文件](https://www.baeldung.com/junit-5-temporary-directory)，我们可以使用它通过HttpClient上传：

```java
Path file = tempDir.resolve("temp.txt");
List<String> lines = Arrays.asList("1", "2", "3");
Files.write(file, lines);
```

HttpClient提供了一个单独的方法BodyPublishers.ofFile，用于将文件添加到 POST 主体。我们可以简单地将我们的临时文件添加为方法参数，API 会处理剩下的事情：

```java
HttpRequest request = HttpRequest.newBuilder()
  .uri(URI.create(serviceUrl))
  .POST(HttpRequest.BodyPublishers.ofFile(file))
  .build();
```

### 5.3. 提交表格

与文件相反，HttpClient不提供用于发布表单数据的单独方法。因此，我们将再次需要使用BodyPublishers.ofString方法：

```java
Map<String, String> formData = new HashMap<>();
formData.put("username", "baeldung");
formData.put("message", "hello");

HttpRequest request = HttpRequest.newBuilder()
  .uri(URI.create(serviceUrl))
  .POST(HttpRequest.BodyPublishers.ofString(getFormDataAsString(formData)))
  .build();
```

但是，我们需要使用自定义实现将表单数据从Map转换为String ：

```java
private static String getFormDataAsString(Map<String, String> formData) {
    StringBuilder formBodyBuilder = new StringBuilder();
    for (Map.Entry<String, String> singleEntry : formData.entrySet()) {
        if (formBodyBuilder.length() > 0) {
            formBodyBuilder.append("&");
        }
        formBodyBuilder.append(URLEncoder.encode(singleEntry.getKey(), StandardCharsets.UTF_8));
        formBodyBuilder.append("=");
        formBodyBuilder.append(URLEncoder.encode(singleEntry.getValue(), StandardCharsets.UTF_8));
    }
    return formBodyBuilder.toString();
}
```

## 六，总结

在本文中，我们 探索了使用Java11 中引入的JavaHttpClient API 发送 POST 请求。

我们学习了如何创建HttpClient实例并准备 POST 请求。我们看到了如何同步、异步和并发发送准备好的请求。接下来，我们还看到了如何添加基本身份验证参数。

最后，我们研究了向 POST 请求添加正文。我们介绍了 JSON 负载、上传文件和提交表单数据。