## 1. 概述

本文将重点介绍在Spring中使用ETag、REST API的集成测试以及使用curl的消费场景。

## 2. REST和ETag

来自关于ETag支持的官方Spring文档：

[ETag](https://en.wikipedia.org/wiki/HTTP_ETag)(实体标签)是由符合HTTP/1.1标准的Web服务器返回的HTTP响应标头，用于确定给定URL上的内容变化。

我们可以将ETag用于两件事-缓存和条件请求。**ETag值可以被认为是从响应主体的字节中计算出的哈希值**，由于该服务可能使用加密哈希函数，因此即使对正文进行最小的修改也会极大地改变输出，从而改变ETag的值。这仅适用于强ETag，该协议也提供[弱Etag](https://datatracker.ietf.org/doc/html/rfc2616#section-13.3.3)。

使用If-\*标头将标准GET请求转换为条件GET，与ETag一起使用的两个If-\*标头是“[If-None-Match](https://datatracker.ietf.org/doc/html/rfc2616#section-14.26)”和“[If-Match](https://datatracker.ietf.org/doc/html/rfc2616#section-14.24)”，它们每个都有自己的语义，如本文后面所述。

## 3. 使用curl进行客户端-服务器通信

我们可以将涉及ETag的简单客户端-服务器通信分解为以下步骤：

首先，客户端进行REST API调用，**响应包含将存储以供进一步使用的ETag标头**：

```bash
curl -H "Accept: application/json" -i http://localhost:8080/spring-boot-rest/foos/1
```

```http request
HTTP/1.1 200 OK
ETag: "f88dd058fe004909615a64f01be66a7"
Content-Type: application/json;charset=UTF-8
Content-Length: 52
```

**对于下一个请求，客户端将包含If-None-Match请求标头和上一步中的ETag值**。如果服务器上的资源未更改，则响应将不包含正文和状态代码304 – Not Modified：

```bash
curl -H "Accept: application/json" -H 'If-None-Match: "f88dd058fe004909615a64f01be66a7"'
 -i http://localhost:8080/spring-boot-rest/foos/1
```

```http request
HTTP/1.1 304 Not Modified
ETag: "f88dd058fe004909615a64f01be66a7"
```

现在，在再次检索资源之前，让我们通过执行更新来更改它：

```bash
curl -H "Content-Type: application/json" -i 
  -X PUT --data '{ "id":1, "name":"Transformers2"}' 
    http://localhost:8080/spring-boot-rest/foos/1
```

```http request
HTTP/1.1 200 OK
ETag: "d41d8cd98f00b204e9800998ecf8427e" 
Content-Length: 0
```

最后，我们发出最后一个请求以再次检索Foo。请记住，自上次请求以来我们已经对其进行了更新，因此之前的ETag值应该不再有效。响应将包含新数据和新的ETag，同样可以存储以供进一步使用：

```bash
curl -H "Accept: application/json" -H 'If-None-Match: "f88dd058fe004909615a64f01be66a7"' -i 
  http://localhost:8080/spring-boot-rest/foos/1
```

```http request
HTTP/1.1 200 OK
ETag: "03cb37ca667706c68c0aad4cb04c3a211"
Content-Type: application/json;charset=UTF-8
Content-Length: 56
```

这就是你所拥有的-广泛使用ETag并节省带宽。

## 4. Spring中的ETag支持

关于Spring支持：在Spring中使用ETag非常容易设置并且对应用程序完全透明，**我们可以通过在web.xml中添加一个简单的过滤器来启用支持**：

```xml
<filter>
   <filter-name>etagFilter</filter-name>
   <filter-class>org.springframework.web.filter.ShallowEtagHeaderFilter</filter-class>
</filter>
<filter-mapping>
   <filter-name>etagFilter</filter-name>
   <url-pattern>/foos/*</url-pattern>
</filter-mapping>
```

我们将过滤器映射到与RESTful API本身相同的URI模式，过滤器本身是自Spring 3.0以来ETag功能的标准实现。

**实现是浅层的**-应用程序根据响应计算ETag，这将节省带宽但不会降低服务器性能。

因此，将受益于ETag支持的请求仍将作为标准请求处理，消耗它通常消耗的任何资源(数据库连接等)，并且只有在将其响应返回给客户端之前，ETag支持才会启动在。

届时，ETag将从响应主体中计算出来，并设置在资源本身上；此外，如果在请求中设置了If-None-Match标头，它也会被处理。

ETag机制的更深层次的实现可能会提供更大的好处，例如从缓存中服务一些请求而根本不必执行计算，但实现绝对不会像浅层方法那样简单，也不会像浅层方法那样可插入在这里描述。

### 4.1 基于Java的配置

通过**在我们的Spring上下文中声明一个ShallowEtagHeaderFilter bean**，让我们看看基于Java的配置是什么样子的：

```java
@Bean
public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
    return new ShallowEtagHeaderFilter();
}
```

请记住，如果我们需要提供进一步的过滤器配置，我们可以声明一个FilterRegistrationBean实例：

```java
@Bean
public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
    FilterRegistrationBean<ShallowEtagHeaderFilter> filterRegistrationBean = new FilterRegistrationBean<>( new ShallowEtagHeaderFilter());
    filterRegistrationBean.addUrlPatterns("/foos/*");
    filterRegistrationBean.setName("etagFilter");
    return filterRegistrationBean;
}
```

最后，如果我们不使用Spring Boot，我们可以使用AbstractAnnotationConfigDispatcherServletInitializer的getServletFilters方法设置过滤器。

### 4.2 使用ResponseEntity的eTag()方法

这个方法是在Spring框架4.1中引入的，**我们可以用它来控制单个端点获取的ETag值**。

例如，假设我们使用版本化实体作为[乐观锁定机制](https://www.baeldung.com/jpa-optimistic-locking)来访问我们的数据库信息。

我们可以使用版本本身作为ETag来指示实体是否已被修改：

```java
@GetMapping(value = "/{id}/custom-etag")
public ResponseEntity<Foo> findByIdWithCustomEtag(@PathVariable("id") final Long id) {

    // ...Foo foo = ...

    return ResponseEntity.ok()
        .eTag(Long.toString(foo.getVersion()))
        .body(foo);
}
```

如果请求的条件标头与缓存数据匹配，服务将检索相应的304-Not Modified状态。

## 5. 测试ETag

让我们从简单开始，**我们需要验证检索单个资源的简单请求的响应实际上会返回“ETag”标头**：

```java
@Test
public void givenResourceExists_whenRetrievingResource_thenEtagIsAlsoReturned() {
    // Given
    String uriOfResource = createAsUri();

    // When
    Response findOneResponse = RestAssured.given()
        .header("Accept", "application/json")
        .get(uriOfResource);

    // Then
    assertNotNull(findOneResponse.getHeader("ETag"));
}
```

**接下来，我们验证ETag行为的快乐路径**，如果从服务器检索资源的请求使用了正确的ETag值，则服务器不会检索资源：

```java
@Test
public void givenResourceWasRetrieved_whenRetrievingAgainWithEtag_thenNotModifiedReturned() {
    // Given
    String uriOfResource = createAsUri();
    Response findOneResponse = RestAssured.given().
      header("Accept", "application/json").get(uriOfResource);
    String etagValue = findOneResponse.getHeader(HttpHeaders.ETAG);

    // When
    Response secondFindOneResponse= RestAssured.given()
        .header("Accept", "application/json")
        .headers("If-None-Match", etagValue)
        .get(uriOfResource);

    // Then
    assertTrue(secondFindOneResponse.getStatusCode() == 304);
}
```

步骤分解：

-   我们创建并检索一个资源，存储ETag值
-   发送一个新的检索请求，这次带有指定先前存储的ETag值的“If-None-Match”标头
-   在第二次请求中，服务器简单地返回一个304 Not Modified，因为资源本身在两次检索操作之间确实没有被修改

**最后，我们验证Resource在第一次和第二次检索请求之间发生变化的情况**：

```java
@Test
public void 
  givenResourceWasRetrievedThenModified_whenRetrievingAgainWithEtag_thenResourceIsReturned() {
    // Given
    String uriOfResource = createAsUri();
    Response findOneResponse = RestAssured.given()
        .header("Accept", "application/json")
        .get(uriOfResource);
    String etagValue = findOneResponse.getHeader(HttpHeaders.ETAG);

    existingResource.setName(randomAlphabetic(6));
    update(existingResource);

    // When
    Response secondFindOneResponse= RestAssured.given()
        .header("Accept", "application/json")
        .headers("If-None-Match", etagValue)
        .get(uriOfResource);

    // Then
    assertTrue(secondFindOneResponse.getStatusCode() == 200);
}
```

步骤分解：

-   我们首先创建并检索一个资源，并存储ETag值以供进一步使用
-   然后我们更新相同的资源
-   发送一个新的GET请求，这次带有指定我们之前存储的ETag的“If-None-Match”标头
-   在第二次请求时，服务器将返回200 OK以及完整的资源，因为ETag值不再正确，因为我们同时更新了资源

最后，最后一个测试，由于该功能[尚未在Spring中实现而无法](https://jira.springsource.org/browse/SPR-10164)运行，是对If-MatchHTTP标头的支持：

```java
@Test
public void givenResourceExists_whenRetrievedWithIfMatchIncorrectEtag_then412IsReceived() {
    // Given
    T existingResource = getApi().create(createNewEntity());

    // When
    String uriOfResource = baseUri + "/" + existingResource.getId();
    Response findOneResponse = RestAssured.given()
        .header("Accept", "application/json")
        .headers("If-Match", randomAlphabetic(8))
        .get(uriOfResource);

    // Then
    assertTrue(findOneResponse.getStatusCode() == 412);
}
```

步骤分解：

-   我们创建一个资源
-   然后使用指定不正确的ETag值的“If-Match”标头检索它，这是一个有条件的GET请求
-   服务器应该返回412 Precondition Failed

## 6. ETags很大

**我们只使用ETag进行读取操作**，存在一个[RFC](http://greenbytes.de/tech/webdav/draft-reschke-http-etag-on-write-latest.html#rfc.section.1.1)试图阐明实现应该如何处理写操作上的ETags，这不是标准的，但读起来很有趣。

当然，ETag机制还有其他可能的用途，例如用于乐观锁定机制以及处理[相关的“丢失更新问题”](https://www.w3.org/1999/04/Editing/)。

在使用ETag时，还有几个已知的[潜在陷阱和注意事项](https://www.mnot.net/blog/2007/08/07/etags)需要注意。

## 7. 总结

本文仅粗浅地介绍了Spring和ETag的可能性。

有关支持ETag的RESTful服务的完整实施，以及验证ETag行为的集成测试，请查看[GitHub项目]()。