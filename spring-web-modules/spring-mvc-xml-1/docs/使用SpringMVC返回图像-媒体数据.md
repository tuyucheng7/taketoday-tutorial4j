## 1. 概述

在本教程中，我们将说明如何使用Spring MVC框架返回图像和其他媒体。

我们将讨论几种方法，从直接操作HttpServletResponse开始，而不是转向受益于[Message Conversion](https://www.baeldung.com/spring-httpmessageconverter-rest)、[Content Negotiation](https://www.baeldung.com/spring-mvc-content-negotiation-json-xml)和Spring的Resource抽象的方法。我们将仔细研究它们中的每一个，并讨论它们的优点和缺点。

## 2. 使用HttpServletResponse

图像下载的最基本方法是直接针对响应对象并模拟纯Servlet实现，并使用以下代码片段进行演示：

```java
@RequestMapping(value = "/image-manual-response", method = RequestMethod.GET)
public void getImageAsByteArray(HttpServletResponse response) throws IOException {
    InputStream in = servletContext.getResourceAsStream("/WEB-INF/images/image-example.jpg");
    response.setContentType(MediaType.IMAGE_JPEG_VALUE);
    IOUtils.copy(in, response.getOutputStream());
}
```

发出以下请求将在浏览器中呈现图像：

```bash
http://localhost:8080/spring-mvc-xml/image-manual-response.jpg
```

由于org.apache.commons.io包中的IOUtils，实现相当简单明了。然而，该方法的缺点是它对潜在的变化不稳健。MIME类型是硬编码的，转换逻辑的更改或图像位置的外部化需要更改代码。

下一节讨论更灵活的方法。

## 3. 使用HttpMessageConverter

上一节讨论了一种不利用Spring MVC框架的[消息转换](https://www.baeldung.com/spring-httpmessageconverter-rest)和[内容协商功能](https://www.baeldung.com/spring-mvc-content-negotiation-json-xml)的基本方法。要引导这些功能，我们需要：

-   使用@ResponseBody注解来注解控制器方法
-   根据控制器方法的返回类型注册一个适当的消息转换器(例如ByteArrayHttpMessageConverter需要将字节数组正确转换为图像文件)

### 3.1 配置

为了展示转换器的配置，我们将使用内置的ByteArrayHttpMessageConverter，它会在方法返回byte[]类型时转换消息。

默认情况下会注册ByteArrayHttpMessageConverter，但配置与任何其他内置或自定义转换器类似。

应用消息转换器bean需要在Spring MVC上下文中注册一个适当的MessageConverter bean并设置它应该处理的媒体类型。你可以使用\<mvc:message-converters\>标签通过XML定义它。

此标签应在\<mvc:annotation-driven\>标签内定义，如以下示例所示：

```xml
<mvc:annotation-driven>
    <mvc:message-converters>
        <bean class="org.springframework.http.converter.ByteArrayHttpMessageConverter">
            <property name="supportedMediaTypes">
                <list>
                    <value>image/jpeg</value>
                    <value>image/png</value>
                </list>
            </property>
        </bean>
    </mvc:message-converters>
</mvc:annotation-driven>
```

上述配置部分会为image/jpeg和image/png响应内容类型注册ByteArrayHttpMessageConverter。如果mvc配置中不存在\<mvc:message-converters\>标签，则将注册默认的转换器集。

此外，你可以使用Java配置注册消息转换器：

```java
@Override
public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(byteArrayHttpMessageConverter());
}

@Bean
public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
    ByteArrayHttpMessageConverter arrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
    arrayHttpMessageConverter.setSupportedMediaTypes(getSupportedMediaTypes());
    return arrayHttpMessageConverter;
}

private List<MediaType> getSupportedMediaTypes() {
    List<MediaType> list = new ArrayList<MediaType>();
    list.add(MediaType.IMAGE_JPEG);
    list.add(MediaType.IMAGE_PNG);
    list.add(MediaType.APPLICATION_OCTET_STREAM);
    return list;
}
```

### 3.2 实现

现在我们可以实现我们的方法来处理媒体请求。如上所述，你需要使用@ResponseBody注解标记你的控制器方法，并使用byte[]作为返回类型：

```java
@RequestMapping(value = "/image-byte-array", method = RequestMethod.GET)
public @ResponseBody byte[] getImageAsByteArray() throws IOException {
    InputStream in = servletContext.getResourceAsStream("/WEB-INF/images/image-example.jpg");
    return IOUtils.toByteArray(in);
}
```

要测试该方法，请在浏览器中发出以下请求：

```bash
http://localhost:8080/spring-mvc-xml/image-byte-array.jpg
```

在优势方面，该方法对HttpServletResponse一无所知，转换过程是高度可配置的，从使用可用转换器到指定自定义转换器。响应的内容类型不必硬编码，而是根据请求路径后缀.jpg[协商](https://www.baeldung.com/spring-mvc-content-negotiation-json-xml)。

这种方法的缺点是你需要显式实现从数据源(本地文件、外部存储等)检索图像的逻辑，并且你无法控制响应的标头或状态代码。

## 4. 使用ResponseEntity类

你可以返回包含在ResponseEntity中的byte[]图像。Spring MVC ResponseEntity不仅可以控制HTTP响应的主体，还可以控制标头和响应状态代码。按照这种方法，你需要将方法的返回类型定义为ResponseEntity<byte[]>并在方法主体中创建返回ResponseEntity对象。

```java
@RequestMapping(value = "/image-response-entity", method = RequestMethod.GET)
public ResponseEntity<byte[]> getImageAsResponseEntity() {
    HttpHeaders headers = new HttpHeaders();
    InputStream in = servletContext.getResourceAsStream("/WEB-INF/images/image-example.jpg");
    byte[] media = IOUtils.toByteArray(in);
    headers.setCacheControl(CacheControl.noCache().getHeaderValue());
    
    ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(media, headers, HttpStatus.OK);
    return responseEntity;
}
```

使用ResponseEntity允许你为给定请求配置响应代码。

显式设置响应代码在遇到异常事件时特别有用，例如，如果图像未找到(FileNotFoundException)或已损坏(IOException)。在这些情况下，所需要的只是在适当的catch块中设置响应代码，例如new ResponseEntity<>(null, headers, HttpStatus.NOT_FOUND)。

此外，如果你需要在响应中设置一些特定的标头，这种方法比通过方法接受的HttpServletResponse对象作为参数设置标头更直接。它使方法签名清晰且重点突出。

## 5. 使用Resource返回图片

最后，你可以以Resource对象的形式返回图像。

Resource接口是用于抽象访问低级资源的接口，它在Spring中作为标准java.net.URL类的更有能力的替代品引入。它允许轻松访问不同类型的资源(本地文件、远程文件、类路径资源)，而无需编写显式检索它们的代码。

要使用此方法，应将方法的返回类型设置为Resource并且你需要使用@ResponseBody注解对该方法进行注解。

### 5.1 实现

```java
@ResponseBody
@RequestMapping(value = "/image-resource", method = RequestMethod.GET)
public Resource getImageAsResource() {
   return new ServletContextResource(servletContext, "/WEB-INF/images/image-example.jpg");
}
```

或者，如果我们想要更多地控制响应标头：

```java
@RequestMapping(value = "/image-resource", method = RequestMethod.GET)
@ResponseBody
public ResponseEntity<Resource> getImageAsResource() {
    HttpHeaders headers = new HttpHeaders();
    Resource resource = new ServletContextResource(servletContext, "/WEB-INF/images/image-example.jpg");
    return new ResponseEntity<>(resource, headers, HttpStatus.OK);
}
```

使用这种方法，你将图像视为可以使用ResourceLoader接口实现加载的资源。在这种情况下，你从图像的确切位置抽象出来，ResourceLoader决定从哪里加载它。

它提供了一种使用配置来控制图像位置的通用方法，并且无需编写文件加载代码。

## 6. 总结

在上述方法中，我们从基本方法开始，然后使用受益于框架的消息转换特性的方法。我们还讨论了如何在不直接处理响应对象的情况下获取设置的响应代码和响应标头。

最后，我们从图像位置的角度增加了灵活性，因为从哪里检索图像是在更容易动态更改的配置中定义的。

[使用Spring下载图像或文件](https://www.baeldung.com/spring-controller-return-image-file)解释了如何使用Spring Boot实现相同的目的。