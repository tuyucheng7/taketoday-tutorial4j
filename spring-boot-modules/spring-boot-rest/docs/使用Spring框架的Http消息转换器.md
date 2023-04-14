## 1. 概述

在本教程中，我们将学习如何在 Spring中配置HttpMessageConverters。

简而言之，我们可以使用消息转换器通过 HTTP 将 Java 对象编组到 JSON 和 XML 以及从中解组。

## 延伸阅读：

## [Spring MVC 内容协商](https://www.baeldung.com/spring-mvc-content-negotiation-json-xml)

在 Spring MVC 应用程序中配置内容协商以及启用和禁用各种可用策略的指南。

[阅读更多](https://www.baeldung.com/spring-mvc-content-negotiation-json-xml)→

## [使用 Spring MVC 返回图像/媒体数据](https://www.baeldung.com/spring-mvc-image-media-data)

本文展示了使用 Spring MVC 返回图像(或其他媒体)的备选方案，并讨论了每种方法的优缺点。

[阅读更多](https://www.baeldung.com/spring-mvc-image-media-data)→

## [Spring REST API 中的二进制数据格式](https://www.baeldung.com/spring-rest-api-with-binary-data-formats)

在本文中，我们探讨了如何配置 Spring REST 机制以利用我们用 Kryo 说明的二进制数据格式。此外，我们还展示了如何使用 Google Protocol buffers 支持多种数据格式。

[阅读更多](https://www.baeldung.com/spring-rest-api-with-binary-data-formats)→

## 2. 基础知识

2.1. 启用 Web MVC

首先，需要为 Web 应用程序配置 Spring MVC 支持。 一种方便且非常可定制的方法是使用@EnableWebMvc注解：

```java
@EnableWebMvc
@Configuration
@ComponentScan({ "cn.tuyucheng.taketoday.web" })
public class WebConfig implements WebMvcConfigurer {
    
    // ...
    
}
```

请注意，此类实现 了 WebMvcConfigurer，这将允许我们使用自己的更改默认的 Http 转换器列表。

### 2.2. 默认消息转换器

默认情况下，预先启用以下HttpMessageConverter实例：

-   ByteArrayHttpMessageConverter – 转换字节数组
-   StringHttpMessageConverter – 转换字符串
-   ResourceHttpMessageConverter – 将org.springframework.core.io.Resource转换为任何类型的八位字节流
-   SourceHttpMessageConverter – 转换javax.xml.transform.Source
-   FormHttpMessageConverter – 将表单数据与MultiValueMap<String, String>相互转换
-   Jaxb2RootElementHttpMessageConverter – 将 Java 对象与 XML 相互转换(仅当类路径中存在 JAXB2 时才添加)
-   MappingJackson2HttpMessageConverter – 转换 JSON(仅当类路径中存在 Jackson 2 时才添加)
    
-   MappingJacksonHttpMessageConverter – 转换 JSON(仅当类路径中存在 Jackson 时才添加)
-   AtomFeedHttpMessageConverter – 转换 Atom 提要(仅当类路径中存在 Rome 时才添加)
-   RssChannelHttpMessageConverter – 转换 RSS 提要(仅当类路径中存在 Rome 时才添加)

## 3. 客户端-服务器通信——仅 JSON

### 3.1. 高级内容协商

每个HttpMessageConverter实现都有一个或多个关联的 MIME 类型。

收到新请求时，Spring 将使用“ Accept ”标头来确定它需要响应的媒体类型。

然后它将尝试找到能够处理该特定媒体类型的已注册转换器。最后，它将使用它来转换实体并发回响应。

该过程类似于接收包含 JSON 信息的请求。该框架将使用“ Content-Type ”标头来确定请求正文的媒体类型。

然后它将搜索可以将客户端发送的正文转换为 Java 对象的HttpMessageConverter 。

让我们用一个简单的例子来阐明这一点：

-   客户端向/foos发送 GET 请求，Accept头设置为application/json，以获取所有Foo资源作为 JSON。
-   Foo Spring Controller 被命中，并返回相应的Foo Java实体。
-   然后 Spring 使用 Jackson 消息转换器之一将实体编组为 JSON。

现在让我们看看它是如何工作的细节，以及我们如何利用@ResponseBody和@RequestBody注解。

### 3.2. @ResponseBody

Controller 方法上的@ResponseBody向 Spring 指示该方法的返回值直接序列化为 HTTP Response 的主体。如上所述，客户端指定的“接受”标头将用于选择适当的 Http 转换器来编组实体：

```java
@GetMapping("/{id}")
public @ResponseBody Foo findById(@PathVariable long id) {
    return fooService.findById(id);
}
```

现在客户端将在请求中将“Accept”标头指定给application/json(例如，curl命令)：

```shell
curl --header "Accept: application/json" 
  http://localhost:8080/spring-boot-rest/foos/1
```

Foo类：

```java
public class Foo {
    private long id;
    private String name;
}
```

和 HTTP 响应主体：

```javascript
{
    "id": 1,
    "name": "Paul",
}
```

### 3.3. @请求体

我们可以 在 Controller 方法的参数上 使用@RequestBody注解 来指示HTTP 请求的主体被反序列化为特定的 Java 实体。为了确定合适的转换器，Spring 将使用来自客户端请求的“Content-Type”标头： 

```java
@PutMapping("/{id}")
public @ResponseBody void update(@RequestBody Foo foo, @PathVariable String id) {
    fooService.update(foo);
}
```

接下来，我们将使用一个 JSON 对象来使用它，将“Content-Type ”指定为application/json：

```shell
curl -i -X PUT -H "Content-Type: application/json"  
-d '{"id":"83","name":"klik"}' http://localhost:8080/spring-boot-rest/foos/1
```

我们会返回一个 200 OK，一个成功的响应：

```bash
HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
Content-Length: 0
Date: Fri, 10 Jan 2014 11:18:54 GMT
```

## 4.自定义转换器配置

我们还可以 通过实现WebMvcConfigurer接口 并覆盖configureMessageConverters方法来自定义消息转换器：

```java
@EnableWebMvc
@Configuration
@ComponentScan({ "cn.tuyucheng.taketoday.web" })
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        messageConverters.add(createXmlHttpMessageConverter());
        messageConverters.add(new MappingJackson2HttpMessageConverter());
    }

    private HttpMessageConverter<Object> createXmlHttpMessageConverter() {
        MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();

        XStreamMarshaller xstreamMarshaller = new XStreamMarshaller();
        xmlConverter.setMarshaller(xstreamMarshaller);
        xmlConverter.setUnmarshaller(xstreamMarshaller);

        return xmlConverter;
    } 
}
```

在此示例中，我们正在创建一个新转换器MarshallingHttpMessageConverter，并使用 Spring XStream 支持对其进行配置。这提供了很大的灵活性，因为我们正在使用底层编组框架的低级 API，在本例中为 XStream，我们可以根据需要对其进行配置。

请注意，此示例需要将 XStream 库添加到类路径中。

另请注意，通过扩展此支持类，我们将丢失先前预注册的默认消息转换器。

当然，我们现在可以通过定义我们自己的MappingJackson2HttpMessageConverter 为 Jackson 做同样的事情。我们可以在此转换器上设置自定义ObjectMapper，并根据需要对其进行配置。

在这种情况下，XStream 是选定的编组器/解组器实现，但 也可以使用[其他](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/oxm/Marshaller.html)实现，如JibxMarshaller 。

此时，在后端启用 XML 的情况下，我们可以使用带有 XML 表示的 API：

```bash
curl --header "Accept: application/xml" 
  http://localhost:8080/spring-boot-rest/foos/1
```

### 4.1. 春季启动支持

如果我们使用 Spring Boot，我们可以避免像上面那样实现WebMvcConfigurer 并手动添加所有消息转换器。

我们可以在上下文中定义不同 的HttpMessageConverter bean，Spring Boot 会自动将它们添加到它创建的自动配置中：

```java
@Bean
public HttpMessageConverter<Object> createXmlHttpMessageConverter() {
    MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();

    // ...

    return xmlConverter;
}
```

## 5. 将 Spring 的RestTemplate与 HTTP 消息转换器一起使用

与服务器端一样，可以在 Spring RestTemplate的客户端配置 HTTP 消息转换。

我们将在适当的时候使用“ Accept ”和“ Content-Type ”标头配置模板。然后我们将尝试通过Foo资源的完整编组和解组来使用 REST API ，包括 JSON 和 XML。

### 5.1. 检索没有接受标头的资源

```java
@Test
public void whenRetrievingAFoo_thenCorrect() {
    String URI = BASE_URI + "foos/{id}";

    RestTemplate restTemplate = new RestTemplate();
    Foo resource = restTemplate.getForObject(URI, Foo.class, "1");

    assertThat(resource, notNullValue());
}
```

### 5.2. 使用application/xml Accept标头检索资源

现在让我们以 XML 表示形式显式检索资源。我们将定义一组转换器并将它们设置在RestTemplate 上。

因为我们正在使用 XML，所以我们将使用与以前相同的 XStream 编组器：

```java
@Test
public void givenConsumingXml_whenReadingTheFoo_thenCorrect() {
    String URI = BASE_URI + "foos/{id}";

    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setMessageConverters(getXmlMessageConverters());

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<Foo> response = 
      restTemplate.exchange(URI, HttpMethod.GET, entity, Foo.class, "1");
    Foo resource = response.getBody();

    assertThat(resource, notNullValue());
}

private List<HttpMessageConverter<?>> getXmlMessageConverters() {
    XStreamMarshaller marshaller = new XStreamMarshaller();
    marshaller.setAnnotatedClasses(Foo.class);
    MarshallingHttpMessageConverter marshallingConverter = 
      new MarshallingHttpMessageConverter(marshaller);

    List<HttpMessageConverter<?>> converters = new ArrayList<>();
    converters.add(marshallingConverter);
    return converters;
}
```

### 5.3. 使用application/json Accept标头检索资源

同样，现在让我们通过请求 JSON 来使用 REST API：

```java
@Test
public void givenConsumingJson_whenReadingTheFoo_thenCorrect() {
    String URI = BASE_URI + "foos/{id}";

    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setMessageConverters(getJsonMessageConverters());

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    HttpEntity<String> entity = new HttpEntity<String>(headers);

    ResponseEntity<Foo> response = 
      restTemplate.exchange(URI, HttpMethod.GET, entity, Foo.class, "1");
    Foo resource = response.getBody();

    assertThat(resource, notNullValue());
}

private List<HttpMessageConverter<?>> getJsonMessageConverters() {
    List<HttpMessageConverter<?>> converters = new ArrayList<>();
    converters.add(new MappingJackson2HttpMessageConverter());
    return converters;
}
```

### 5.4. 使用 XML内容类型更新资源 

最后，我们将 JSON 数据发送到 REST API，并通过Content-Type标头指定该数据的媒体类型：

```java
@Test
public void givenConsumingXml_whenWritingTheFoo_thenCorrect() {
    String URI = BASE_URI + "foos";
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setMessageConverters(getJsonAndXmlMessageConverters());

    Foo resource = new Foo("jason");
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType((MediaType.APPLICATION_XML));
    HttpEntity<Foo> entity = new HttpEntity<>(resource, headers);

    ResponseEntity<Foo> response = 
      restTemplate.exchange(URI, HttpMethod.POST, entity, Foo.class);
    Foo fooResponse = response.getBody();

    assertThat(fooResponse, notNullValue());
    assertEquals(resource.getName(), fooResponse.getName());
}

private List<HttpMessageConverter<?>> getJsonAndXmlMessageConverters() {
    List<HttpMessageConverter<?>> converters = getJsonMessageConverters();
    converters.addAll(getXmlMessageConverters());
    return converters;
}
```

这里有趣的是我们能够混合媒体类型。我们正在发送 XML 数据，但我们正在等待从服务器返回的 JSON 数据。这显示了 Spring 转换机制的真正强大之处。

## 六，总结

在本文中，我们了解了 Spring MVC 如何允许我们指定和完全自定义 Http 消息转换器以自动将 Java 实体编组/取消编组到 XML 或 JSON。当然，这是一个简单的定义，消息转换机制可以做的还有很多，正如我们从上一个测试示例中看到的那样。

我们还研究了如何利用与RestTemplate客户端相同的强大机制，从而以一种完全类型安全的方式使用 API。