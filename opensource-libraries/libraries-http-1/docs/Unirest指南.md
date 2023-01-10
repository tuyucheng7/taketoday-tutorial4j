## 1. 概述

Unirest 是来自 Mashape 的轻量级 HTTP 客户端库。除了 Java，它还适用于 Node.js、.Net、Python、Ruby 等。

在开始之前，请注意我们将在这里对所有 HTTP 请求使用[mocky.io](https://www.mocky.io/)。

## 2.Maven 设置

首先，让我们先添加必要的依赖项：

```xml
<dependency>
    <groupId>com.mashape.unirest</groupId>
    <artifactId>unirest-java</artifactId>
    <version>1.4.9</version>
</dependency>
```

[在此处](https://search.maven.org/classic/#search|ga|1|g%3A"com.mashape.unirest"%2C a%3A"unirest-java")查看最新版本。

## 3. 简单请求

让我们发送一个简单的 HTTP 请求，以了解框架的语义：

```java
@Test
public void shouldReturnStatusOkay() {
    HttpResponse<JsonNode> jsonResponse 
      = Unirest.get("http://www.mocky.io/v2/5a9ce37b3100004f00ab5154")
      .header("accept", "application/json").queryString("apiKey", "123")
      .asJson();

    assertNotNull(jsonResponse.getBody());
    assertEquals(200, jsonResponse.getStatus());
}
```

请注意，该 API 流畅、高效且非常易于阅读。

我们使用header()和fields() API 传递标头和参数。

并且请求在asJson()方法调用中被调用；我们这里还有其他选项，例如asBinary()、asString()和asObject()。

要传递多个标头或字段，我们可以创建一个映射并将它们分别传递给.headers(Map<String, Object> headers)和.fields( Map<String, String> fields)：

```java
@Test
public void shouldReturnStatusAccepted() {
    Map<String, String> headers = new HashMap<>();
    headers.put("accept", "application/json");
    headers.put("Authorization", "Bearer 5a9ce37b3100004f00ab5154");

    Map<String, Object> fields = new HashMap<>();
    fields.put("name", "Sam Baeldung");
    fields.put("id", "PSP123");

    HttpResponse<JsonNode> jsonResponse 
      = Unirest.put("http://www.mocky.io/v2/5a9ce7853100002a00ab515e")
      .headers(headers).fields(fields)
      .asJson();
 
    assertNotNull(jsonResponse.getBody());
    assertEquals(202, jsonResponse.getStatus());
}
```

### 3.1. 传递查询参数

要将数据作为查询字符串传递，我们将使用queryString()方法：

```java
HttpResponse<JsonNode> jsonResponse 
  = Unirest.get("http://www.mocky.io/v2/5a9ce37b3100004f00ab5154")
  .queryString("apiKey", "123")
```

### 3.2. 使用路径参数

要传递任何 URL 参数，我们可以使用routeParam()方法：

```java
HttpResponse<JsonNode> jsonResponse 
  = Unirest.get("http://www.mocky.io/v2/5a9ce37b3100004f00ab5154/{userId}")
  .routeParam("userId", "123")
```

参数占位符名称必须与方法的第一个参数相同。

### 3.3. 请求正文

如果我们的请求需要一个字符串/JSON 主体，我们使用body()方法传递它：

```java
@Test
public void givenRequestBodyWhenCreatedThenCorrect() {

    HttpResponse<JsonNode> jsonResponse 
      = Unirest.post("http://www.mocky.io/v2/5a9ce7663100006800ab515d")
      .body("{"name":"Sam Baeldung", "city":"viena"}")
      .asJson();
 
    assertEquals(201, jsonResponse.getStatus());
}
```

### 3.4. 对象映射器

为了在请求中使用asObject()或body()，我们需要定义我们的对象映射器。为简单起见，我们将使用 Jackson 对象映射器。

让我们首先将以下依赖项添加到pom.xml：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.12.4</version>
</dependency>
```

始终使用[Maven Central 上](https://search.maven.org/classic/#search|gav|1|g%3A"com.fasterxml.jackson.core" AND a%3A"jackson-databind")的最新版本。

现在让我们配置我们的映射器：

```java
Unirest.setObjectMapper(new ObjectMapper() {
    com.fasterxml.jackson.databind.ObjectMapper mapper 
      = new com.fasterxml.jackson.databind.ObjectMapper();

    public String writeValue(Object value) {
        return mapper.writeValueAsString(value);
    }

    public <T> T readValue(String value, Class<T> valueType) {
        return mapper.readValue(value, valueType);
    }
});
```

请注意，setObjectMapper()只能调用一次，用于设置映射器；一旦设置了映射器实例，它将用于所有请求和响应。

现在让我们使用自定义Article对象测试新功能：

```java
@Test
public void givenArticleWhenCreatedThenCorrect() {
    Article article 
      = new Article("ID1213", "Guide to Rest", "baeldung");
    HttpResponse<JsonNode> jsonResponse 
      = Unirest.post("http://www.mocky.io/v2/5a9ce7663100006800ab515d")
      .body(article)
      .asJson();
 
    assertEquals(201, jsonResponse.getStatus());
}
```

## 4.请求方法

与任何 HTTP 客户端类似，该框架为每个 HTTP 动词提供了单独的方法：

邮政：

```java
Unirest.post("http://www.mocky.io/v2/5a9ce7663100006800ab515d")
```

放：

```java
Unirest.put("http://www.mocky.io/v2/5a9ce7663100006800ab515d")
```

得到：

```java
Unirest.get("http://www.mocky.io/v2/5a9ce7663100006800ab515d")
```

删除：

```java
Unirest.delete("http://www.mocky.io/v2/5a9ce7663100006800ab515d")
```

修补：

```java
Unirest.patch("http://www.mocky.io/v2/5a9ce7663100006800ab515d")
```

选项：

```java
Unirest.options("http://www.mocky.io/v2/5a9ce7663100006800ab515d")
```

## 五、应对方式

收到响应后，让我们检查状态代码和状态消息：

```java
//...
jsonResponse.getStatus()

//...
```

提取标题：

```java
//...
jsonResponse.getHeaders();
//...
```

获取响应正文：

```java
//...
jsonResponse.getBody();
jsonResponse.getRawBody();
//...
```

请注意，getRawBody()返回未解析的响应主体流，而getBody()使用前面部分中定义的对象映射器返回已解析的主体。

## 6.处理异步请求

Unirest 还具有处理异步请求的能力——使用java.util.concurrent.Future和回调方法：

```java
@Test
public void whenAysncRequestShouldReturnOk() {
    Future<HttpResponse<JsonNode>> future = Unirest.post(
      "http://www.mocky.io/v2/5a9ce37b3100004f00ab5154?mocky-delay=10000ms")
      .header("accept", "application/json")
      .asJsonAsync(new Callback<JsonNode>() {

        public void failed(UnirestException e) {
            // Do something if the request failed
        }

        public void completed(HttpResponse<JsonNode> response) {
            // Do something if the request is successful
        }

        public void cancelled() {
            // Do something if the request is cancelled
        }
        });
 
    assertEquals(200, future.get().getStatus());
}
```

com.mashape.unirest.http.async.Callback <T>接口提供了三个方法，failed()、cancelled()和completed()。

根据响应覆盖方法以执行必要的操作。

## 7. 文件上传

要将文件作为请求的一部分上传或发送，请将java.io.File对象作为名称为 file 的字段传递：

```java
@Test
public void givenFileWhenUploadedThenCorrect() {

    HttpResponse<JsonNode> jsonResponse = Unirest.post(
      "http://www.mocky.io/v2/5a9ce7663100006800ab515d")
      .field("file", new File("/path/to/file"))
      .asJson();
 
    assertEquals(201, jsonResponse.getStatus());
}
```

我们也可以使用ByteStream：

```java
@Test
public void givenByteStreamWhenUploadedThenCorrect() {
    try (InputStream inputStream = new FileInputStream(
      new File("/path/to/file/artcile.txt"))) {
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        HttpResponse<JsonNode> jsonResponse = Unirest.post(
          "http://www.mocky.io/v2/5a9ce7663100006800ab515d")
          .field("file", bytes, "article.txt")
          .asJson();
 
        assertEquals(201, jsonResponse.getStatus());
    }
}
```

或者直接使用输入流，将ContentType.APPLICATION_OCTET_STREAM添加为fields()方法中的第二个参数：

```java
@Test
public void givenInputStreamWhenUploadedThenCorrect() {
    try (InputStream inputStream = new FileInputStream(
      new File("/path/to/file/artcile.txt"))) {

        HttpResponse<JsonNode> jsonResponse = Unirest.post(
          "http://www.mocky.io/v2/5a9ce7663100006800ab515d")
          .field("file", inputStream, ContentType.APPLICATION_OCTET_STREAM, "article.txt").asJson();
 
        assertEquals(201, jsonResponse.getStatus());
    }
}
```

## 8. Unirest 配置

该框架还支持 HTTP 客户端的典型配置，如连接池、超时、全局标头等。

让我们设置每个路由的连接数和最大连接数：

```java
Unirest.setConcurrency(20, 5);
```

配置连接和套接字超时：

```java
Unirest.setTimeouts(20000, 15000);
```

请注意，时间值以毫秒为单位。

现在让我们为所有请求设置 HTTP 标头：

```java
Unirest.setDefaultHeader("X-app-name", "baeldung-unirest");
Unirest.setDefaultHeader("X-request-id", "100004f00ab5");
```

我们可以随时清除全局标头：

```java
Unirest.clearDefaultHeaders();
```

在某些时候，我们可能需要通过代理服务器发出请求：

```java
Unirest.setProxy(new HttpHost("localhost", 8080));
```

要注意的一个重要方面是优雅地关闭或退出应用程序。Unirest 生成一个后台事件循环来处理操作，我们需要在退出应用程序之前关闭该循环：

```java
Unirest.shutdown();
```

## 9.总结

在本教程中，我们专注于轻量级 HTTP 客户端框架 – Unirest。我们处理了一些简单的示例，既有同步模式也有异步模式。

最后，我们还使用了一些高级配置——例如连接池、代理设置等。