## 1. 概述

在本教程中，我们将学习**如何在Spring中配置HttpMessageConverters**。

简而言之，我们可以使用消息转换器通过HTTP将Java对象编组到JSON和XML以及从中解组。

## 延伸阅读

### [Spring MVC内容协商](https://www.baeldung.com/spring-mvc-content-negotiation-json-xml)

在Spring MVC应用程序中配置内容协商以及启用和禁用各种可用策略的指南。

[阅读更多](https://www.baeldung.com/spring-mvc-content-negotiation-json-xml)→

### [使用Spring MVC返回图像/媒体数据](https://www.baeldung.com/spring-mvc-image-media-data)

本文展示了使用Spring MVC返回图像(或其他媒体)的备选方案，并讨论了每种方法的优缺点。

[阅读更多](https://www.baeldung.com/spring-mvc-image-media-data)→

### [Spring REST API中的二进制数据格式](https://www.baeldung.com/spring-rest-api-with-binary-data-formats)

在本文中，我们探讨了如何配置Spring REST机制以利用我们用Kryo说明的二进制数据格式。此外，我们还展示了如何使用Google Protocol Buffers支持多种数据格式。

[阅读更多](https://www.baeldung.com/spring-rest-api-with-binary-data-formats)→

## 2. 基础知识

### 2.1 启用Web MVC

首先，需要**为Web应用程序配置Spring MVC支持**。执行此操作的一种方便且非常可自定义的方法是使用@EnableWebMvc注解：

```java
@EnableWebMvc
@Configuration
@ComponentScan({ "cn.tuyucheng.taketoday.web" })
public class WebConfig implements WebMvcConfigurer {
    // ...
}
```

请注意，此类实现了WebMvcConfigurer，这将允许我们使用自己的Http转换器更改默认列表。

### 2.2 默认消息转换器

默认情况下，以下HttpMessageConverters实例处于预启用状态：

-   ByteArrayHttpMessageConverter：转换字节数组
-   StringHttpMessageConverter：转换字符串
-   ResourceHttpMessageConverter：将org.springframework.core.io.Resource转换为任何类型的八位字节流
-   SourceHttpMessageConverter：转换javax.xml.transform.Source
-   FormHttpMessageConverter：将表单数据与MultiValueMap<String, String\>相互转换
-   Jaxb2RootElementHttpMessageConverter：将Java对象与XML相互转换(仅当类路径中存在JAXB 2时才添加)
-   MappingJackson2HttpMessageConverter：转换JSON(仅当类路径中存在Jackson 2时才添加)
-   MappingJacksonHttpMessageConverter：转换JSON(仅当类路径中存在Jackson时才添加)
-   AtomFeedHttpMessageConverter：转换Atom提要(仅当类路径中存在Rome时才添加)
-   RssChannelHttpMessageConverter：转换RSS提要(仅当类路径中存在Rome时才添加)

## 3. 客户端服务器通信-仅JSON

### 3.1 高级内容协商

每个HttpMessageConverter实现都有一个或多个关联的MIME类型。

当收到新请求时，**Spring将使用“Accept”标头来确定它需要响应的媒体类型**。

然后它将尝试找到能够处理该特定媒体类型的已注册转换器。最后，它将使用找到的转换器来转换实体并发回响应。

该过程类似于接收包含JSON信息的请求。**框架将使用“Content-Type”标头来确定请求正文的媒体类型**。

然后它将搜索可以将客户端发送的正文转换为Java对象的HttpMessageConverter。

让我们用一个简单的例子来阐明这一点：

-   客户端向/foos发送GET请求，Accept头设置为application/json，以JSON形式获取所有Foo资源。
-   命中Foo Spring Controller，并返回相应的Foo Java实体。
-   然后Spring使用Jackson消息转换器之一将实体编组为JSON。

现在让我们看一下它是如何工作的，以及我们如何利用@ResponseBody和@RequestBody注解。

### 3.2 @ResponseBody

控制器方法上的@ResponseBody向Spring指示该**方法的返回值直接序列化为HTTP响应的主体**。如上所述，客户端指定的“Accept”标头将用于选择适当的Http转换器来编组实体：

```java
@GetMapping("/{id}")
public @ResponseBody Foo findById(@PathVariable long id) {
    return fooService.findById(id);
}
```

现在客户端将在请求中将“Accept”标头指定为application/json(例如，curl命令)：

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

和HTTP响应主体：

```json
{
    "id": 1,
    "name": "Paul"
}
```

### 3.3 @RequestBody

我们可以在控制器方法的参数上使用@RequestBody注解来指示**HTTP请求的主体被反序列化为特定的Java实体**。为了确定合适的转换器，Spring将使用来自客户端请求的“Content-Type”标头：

```java
@PutMapping("/{id}")
public @ResponseBody void update(@RequestBody Foo foo, @PathVariable String id) {
    fooService.update(foo);
}
```

接下来，我们将它与JSON对象一起使用，将“Content-Type”指定为application/json：

```shell
curl -i -X PUT -H "Content-Type: application/json"  
-d '{"id":"83","name":"klik"}' http://localhost:8080/spring-boot-rest/foos/1p://localhost:8080/spring-boot-rest/foos/1
```

我们会得到一个200 OK，一个成功的响应：

```shell
HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
Content-Length: 0
Date: Fri, 10 Jan 2022 11:18:54 GMT
```

## 4. 自定义转换器配置

我们还可以**通过实现WebMvcConfigurer接口并覆盖configureMessageConverters方法来自定义消息转换器**：

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

在这个例子中，我们创建一个新的转换器MarshallingHttpMessageConverter，并使用Spring XStream支持对其进行配置。这提供了很大的灵活性，因为我们正在使用底层编组框架的低级API，在本例中为XStream，我们可以根据需要对其进行配置。

请注意，此示例需要将XStream库添加到类路径中。

另请注意，通过扩展此支持类，我们将**丢失先前预注册的默认消息转换器**。

当然，我们现在可以通过定义我们自己的MappingJackson2HttpMessageConverter来为Jackson做同样的事情。我们可以在此转换器上设置自定义ObjectMapper，并根据需要对其进行配置。

在这种情况下，XStream是选定的编组器/解组器实现，但也可以使用[其他](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/oxm/Marshaller.html)实现，如JibxMarshaller。

此时，在后端启用XML的情况下，我们可以将API与XML表示形式一起使用：

```bash
curl --header "Accept: application/xml" 
  http://localhost:8080/spring-boot-rest/foos/1
```

### 4.1 Spring Boot支持

如果我们使用Spring Boot，我们可以避免像上面那样实现WebMvcConfigurer并手动添加所有消息转换器。

**我们可以在上下文中定义不同的HttpMessageConverter bean，Spring Boot会自动将它们添加到它创建的自动配置中**：

```java
@Bean
public HttpMessageConverter<Object> createXmlHttpMessageConverter() {
    MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();

    // ...

    return xmlConverter;
}
```

## 5. 将Spring的RestTemplate与HTTP消息转换器结合使用

与服务器端一样，可以在Spring RestTemplate的客户端配置HTTP消息转换。

我们将在适当的时候使用“Accept”和“Content-Type”标头配置模板。然后我们将尝试通过Foo资源的完整编组和解组来使用REST API，包括JSON和XML。

### 5.1 检索没有Accept标头的资源

```java
@Test
public void whenRetrievingAFoo_thenCorrect() {
    String URI = BASE_URI + "foos/{id}";

    RestTemplate restTemplate = new RestTemplate();
    Foo resource = restTemplate.getForObject(URI, Foo.class, "1");

    assertThat(resource, notNullValue());
}
```

### 5.2 使用application/xml Accept标头检索资源

现在让我们以XML表示形式显式检索资源。我们将定义一组转换器并在RestTemplate上设置它们。

因为我们使用的是XML，所以我们将使用与以前相同的XStream编组器：

```java
@Test
public void givenConsumingXml_whenReadingTheFoo_thenCorrect() {
    String URI = BASE_URI + "foos/{id}";

    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setMessageConverters(getXmlMessageConverters());

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<Foo> response = restTemplate.exchange(URI, HttpMethod.GET, entity, Foo.class, "1");
    Foo resource = response.getBody();

    assertThat(resource, notNullValue());
}

private List<HttpMessageConverter<?>> getXmlMessageConverters() {
    XStreamMarshaller marshaller = new XStreamMarshaller();
    marshaller.setAnnotatedClasses(Foo.class);
    MarshallingHttpMessageConverter marshallingConverter = new MarshallingHttpMessageConverter(marshaller);

    List<HttpMessageConverter<?>> converters = new ArrayList<>();
    converters.add(marshallingConverter);
    return converters;
}
```

### 5.3 使用application/json Accept标头检索资源

同样，现在让我们通过请求JSON来使用REST API：

```java
@Test
public void givenConsumingJson_whenReadingTheFoo_thenCorrect() {
    String URI = BASE_URI + "foos/{id}";

    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setMessageConverters(getJsonMessageConverters());

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    HttpEntity<String> entity = new HttpEntity<String>(headers);

    ResponseEntity<Foo> response = restTemplate.exchange(URI, HttpMethod.GET, entity, Foo.class, "1");
    Foo resource = response.getBody();

    assertThat(resource, notNullValue());
}

private List<HttpMessageConverter<?>> getJsonMessageConverters() {
    List<HttpMessageConverter<?>> converters = new ArrayList<>();
    converters.add(new MappingJackson2HttpMessageConverter());
    return converters;
}
```

### 5.4 使用XML内容类型更新资源

最后，我们将JSON数据发送到REST API，并通过Content-Type标头指定该数据的媒体类型：

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

    ResponseEntity<Foo> response = restTemplate.exchange(URI, HttpMethod.POST, entity, Foo.class);
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

这里有趣的是我们能够混合媒体类型。**我们正在发送XML数据，但我们等待从服务器返回的是JSON数据**。这显示了Spring转换机制的真正强大之处。

## 6. 总结

在本文中，我们了解了Spring MVC如何允许我们指定和完全自定义Http消息转换器以自动将Java实体编组/解组到XML或JSON。当然，这是一个简单的定义，消息转换机制可以做的还有很多，正如我们从上一个测试示例中看到的那样。

我们还研究了如何利用与RestTemplate客户端相同的强大机制，从而以一种完全类型安全的方式使用API。