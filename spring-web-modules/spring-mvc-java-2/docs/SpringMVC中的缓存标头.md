## 1. 概述

在本教程中，我们将了解HTTP缓存。我们还将研究在客户端和Spring MVC应用程序之间实现此机制的各种方法。

## 2. 介绍HTTP缓存

当我们在浏览器上打开网页时，通常会从网络服务器下载大量资源：

<img src="../assets/img.png">

例如，在此示例中，浏览器需要为一个/login页面下载三个资源。浏览器对每个网页发出多个HTTP请求是很常见的。现在，如果我们非常频繁地请求此类页面，则会导致大量网络流量并需要更长的时间来提供这些页面。

为了减少网络负载，HTTP协议允许浏览器[缓存](https://www.baeldung.com/spring-cache-tutorial)其中一些资源。如果启用，浏览器可以在本地缓存中保存资源的副本。因此，浏览器可以从本地存储中提供这些页面，而不是通过网络请求它们：

<img src="../assets/img_1.png">

Web 服务器可以通过在响应中添加[Cache-Control](https://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html)标头来指示浏览器缓存特定资源。

由于资源被缓存为本地副本，因此存在从浏览器提供陈旧内容的风险。因此，Web服务器通常会在Cache-Control标头中添加一个过期时间。

在以下部分中，我们将在来自Spring MVC控制器的响应中添加此标头。稍后，我们还将看到Spring API根据过期时间来验证缓存的资源。

## 3. 控制器响应中的Cache-Control

### 3.1 使用ResponseEntity

最直接的方法是使用Spring提供的CacheControl构建器类：

```java
@GetMapping("/hello/{name}")
@ResponseBody
public ResponseEntity<String> hello(@PathVariable String name) {
    CacheControl cacheControl = CacheControl.maxAge(60, TimeUnit.SECONDS)
        .noTransform()
        .mustRevalidate();
    return ResponseEntity.ok()
        .cacheControl(cacheControl)
        .body("Hello " + name);
}
```

这将在响应中添加一个Cache-Control标头：

```java
@Test
void whenHome_thenReturnCacheHeader() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders.get("/hello/tuyucheng"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.header().string("Cache-Control","max-age=60, must-revalidate, no-transform"));
}
```

### 3.2 使用HttpServletResponse

通常，控制器需要从处理程序方法返回视图名称。但是，ResponseEntity类不允许我们在返回视图名称的同时处理请求体。

或者，对于此类控制器，我们可以直接在HttpServletResponse中设置Cache-Control标头：

```java
@GetMapping(value = "/home/{name}")
public String home(@PathVariable String name, final HttpServletResponse response) {
    response.addHeader("Cache-Control", "max-age=60, must-revalidate, no-transform");
    return "home";
}
```

这还将在类似于上一节的 HTTP 响应中添加Cache-Control标头：

```java
@Test
void whenHome_thenReturnCacheHeader() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders.get("/home/tuyucheng"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.header().string("Cache-Control","max-age=60, must-revalidate, no-transform"))
        .andExpect(MockMvcResultMatchers.view().name("home"));
}
```

## 4. 静态资源的缓存控制

通常，我们的Spring MVC应用程序[提供大量静态资源](https://www.baeldung.com/spring-mvc-static-resources)，如HTML、CSS和JS文件。由于此类文件会占用大量网络带宽，因此浏览器缓存它们很重要。我们将在响应中使用Cache-Control标头再次启用它。

Spring允许我们在资源映射中控制这种缓存行为：

```java
@Override
public void addResourceHandlers(final ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/resources/**").addResourceLocations("/resources/")
        .setCacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS).noTransform().mustRevalidate());
}
```

这确保在/resources下定义的所有资源都在响应中返回Cache-Control标头。 

## 5. 拦截器中的缓存控制

我们可以[在我们的Spring MVC应用程序中使用拦截器](https://www.baeldung.com/spring-mvc-handlerinterceptor)为每个请求做一些预处理和后处理，这是我们可以控制应用程序缓存行为的另一个占位符。

现在，我们将使用Spring提供的WebContentInterceptor，而不是实现自定义拦截器：

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    WebContentInterceptor interceptor = new WebContentInterceptor();
    interceptor.addCacheMapping(CacheControl.maxAge(60, TimeUnit.SECONDS)
        .noTransform()
        .mustRevalidate(), "/login/*");
    registry.addInterceptor(interceptor);
}
```

在这里，我们注册了WebContentInterceptor并添加了与上几节类似的Cache-Control标头。值得注意的是，我们可以为不同的URL模式添加不同的Cache-Control标头。

在上面的示例中，对于以/login开头的所有请求，我们将添加此标头：

```java
@Test
void whenInterceptor_thenReturnCacheHeader() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders.get("/login/tuyucheng"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.header().string("Cache-Control","max-age=60, must-revalidate, no-transform"));
}
```

## 6. Spring MVC中的缓存验证

到目前为止，我们已经讨论了在响应中包含Cache-Control标头的各种方法。这表明客户端或浏览器根据max-age等配置属性缓存资源。

为每个资源添加缓存过期时间通常是个好主意。因此，浏览器可以避免从缓存中提供过期资源。

尽管浏览器应该始终检查是否过期，但可能没有必要每次都重新获取资源。如果浏览器可以验证服务器上的资源没有改变，它可以继续提供它的缓存版本。为此，HTTP为我们提供了两个响应头：

1.  Etag：一个HTTP响应头，存储一个唯一的哈希值，用来判断缓存资源在服务器上是否发生了变化，相应的If-None-Match请求头必须携带最后一个Etag值
2.  LastModified：一个HTTP响应头，存储资源上次更新的时间单位，相应的If-Unmodified-Since请求头必须携带上次修改日期

我们可以使用这些标头中的任何一个来检查是否需要重新获取过期的资源。验证标头后，服务器可以重新发送资源或发送304 HTTP代码以表示没有变化。对于后一种情况，浏览器可以继续使用缓存的资源。

LastModified标头只能存储精确到秒的时间间隔，在需要更短到期时间的情况下，这可能是一个限制。因此，建议改用Etag。由于[Etag标头存储一个哈希值](https://www.baeldung.com/etags-for-rest-with-spring)，因此可以创建一个唯一的哈希值，直到更精细的间隔(如纳秒)。

也就是说，让我们看看使用LastModified是什么样子的。

Spring提供了一些实用方法来检查请求是否包含过期标头：

```java
@GetMapping(value = "/productInfo/{name}")
public ResponseEntity<String> validate(@PathVariable String name, WebRequest request) {
 
    ZoneId zoneId = ZoneId.of("GMT");
    long lastModifiedTimestamp = LocalDateTime.of(2020, 02, 4, 19, 57, 45)
        .atZone(zoneId).toInstant().toEpochMilli();
     
    if (request.checkNotModified(lastModifiedTimestamp)) {
        return ResponseEntity.status(304).build();
    }
     
    return ResponseEntity.ok().body("Hello " + name);
}
```

Spring提供了checkNotModified()方法来检查自上次请求以来资源是否已被修改：

```java
@Test
void whenValidate_thenReturnCacheHeader() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add(IF_UNMODIFIED_SINCE, "Tue, 04 Feb 2020 19:57:25 GMT");
    this.mockMvc.perform(MockMvcRequestBuilders.get("/productInfo/tuyucheng").headers(headers))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().is(304));
}
```

## 7. 总结

在本文中，我们通过使用Spring MVC中的Cache-Control响应标头了解了HTTP缓存，我们可以使用ResponseEntity类或通过静态资源的资源映射在控制器的响应中添加标头。

我们还可以使用Spring拦截器为特定的URL模式添加此标头。