## 1. 简介

在本教程中，我们将探索使用[OkHttp](https://www.baeldung.com/guide-to-okhttp)解码 JSON 响应的几种技术。

## 2.OkHttp响应

OkHttp 是用于Java和 Android 的 HTTP 客户端，具有透明处理 GZIP、响应缓存和从网络问题中恢复等功能。

尽管有这些强大的功能，OkHttp 没有内置的 JSON、XML 和其他内容类型的编码器/解码器。然而，我们可以在 XML/JSON 绑定库的帮助下实现这些，或者我们可以使用高级库，如[Feign](https://www.baeldung.com/intro-to-feign)或[Retrofit](https://www.baeldung.com/retrofit)。

要实现我们的 JSON 解码器，我们需要从服务调用的结果中提取 JSON。为此，我们可以通过Response对象的body()方法访问正文。ResponseBody类有几个用于提取此数据的选项：

-   byteStream()：将正文的原始字节公开为InputStream；我们可以将其用于所有格式，但通常用于二进制文件和文件
    
-   charStream()：当我们有文本响应时， charStream()将其InputStream包装在一个Reader中，并根据响应的内容类型处理编码，如果响应头中未设置字符集，则为“UTF-8”；但是，当使用charStream()时，我们无法更改Reader的编码
-   string()：将整个响应主体作为String返回；管理与charStream()相同的编码，但如果我们需要不同的编码，我们可以使用source().readString(charset)代替

在本文中，我们将使用string()因为我们的响应很小而且我们没有内存或性能问题。当性能和内存很重要时， byteStream()和charStream()方法是生产系统中更好的选择。

首先，让我们将[okhttp](https://search.maven.org/search?q=g:com.squareup.okhttp3 a:okhttp)添加到我们的 pom.xml 文件中：

```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId> 
    <version>3.14.2</version> 
</dependency>
```

然后，我们对SimpleEntity进行建模以测试我们的解码器：

```java
public class SimpleEntity {
    protected String name;

    public SimpleEntity(String name) {
        this.name = name;
    }
    
    // no-arg constructor, getters, and setters
}

```

现在，我们将开始我们的测试：

```java
SimpleEntity sampleResponse = new SimpleEntity("Baeldung");

OkHttpClient client = // build an instance;
MockWebServer server = // build an instance;
Request request = new Request.Builder().url(server.url("...")).build();
```

## 3.用Jackson 解码ResponseBody

[Jackson](https://www.baeldung.com/jackson)是最流行的 JSON-Object 绑定库之一。

让我们将[jackson-databind](https://search.maven.org/search?q=g:com.fasterxml.jackson.core a:jackson-databind)添加到我们的 pom.xml 中：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.0</version>
</dependency>
```

Jackson 的ObjectMapper让我们可以将 JSON 转换为对象。因此，我们可以使用ObjectMapper.readValue()解码响应：

```java
ObjectMapper objectMapper = new ObjectMapper(); 
ResponseBody responseBody = client.newCall(request).execute().body(); 
SimpleEntity entity = objectMapper.readValue(responseBody.string(), SimpleEntity.class);

Assert.assertNotNull(entity);
Assert.assertEquals(sampleResponse.getName(), entity.getName());
```

## 4.用Gson解码ResponseBody

[Gson](https://www.baeldung.com/gson-deserialization-guide)是另一个有用的库，用于将 JSON 映射到对象，反之亦然。

让我们将[gson](https://search.maven.org/search?q=g:com.google.code.gson a:gson)添加到我们的 pom.xml 文件中：

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.5</version>
</dependency>
```

让我们看看如何使用Gson.fromJson()来解码响应主体：

```java
Gson gson = new Gson(); 
ResponseBody responseBody = client.newCall(request).execute().body();
SimpleEntity entity = gson.fromJson(responseBody.string(), SimpleEntity.class);

Assert.assertNotNull(entity);
Assert.assertEquals(sampleResponse.getName(), entity.getName());

```

## 5.总结

在本文中，我们探索了几种使用 Jackson 和 Gson 解码 OkHttp 的 JSON 响应的方法。