## 1. 概述

可以通过多种方式向客户端提供静态文件，使用Spring Controller不一定是最佳选择。

然而，有时控制器路由是必要的，这就是我们将在本快速教程中关注的内容。

## 2. Maven依赖

首先，我们需要向我们的pom.xml添加依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

这就是我们在这里需要做的全部，有关版本信息，请前往[Maven Central](https://search.maven.org/search?q=a:spring-boot-starter-web)。

## 3. 使用@ResponseBody

第一个直接的解决方案是在控制器方法上使用@ResponseBody注解来指示该方法返回的对象应该直接编组到HTTP响应主体：

```java
@GetMapping("/get-text")
public @ResponseBody String getText() {
    return "Hello world";
}
```

此方法将只返回字符串Hello world，而不是像更典型的MVC应用程序那样返回名称为Hello world的视图。

使用@ResponseBody，我们几乎可以返回任何媒体类型，只要我们有相应的HTTP消息转换器可以处理并将其编组到输出流即可。

## 4. 使用produces返回图像

返回字节数组允许我们返回几乎任何东西，例如图像或文件：

```java
@GetMapping(value = "/image")
public @ResponseBody byte[] getImage() throws IOException {
    InputStream in = getClass()
        .getResourceAsStream("/cn/tuyucheng/taketoday/produceimage/image.jpg");
    return IOUtils.toByteArray(in);
}
```

由于我们没有定义返回的字节数组是图像，客户端将无法将其作为图像处理。事实上，浏览器很可能只显示图像的实际字节数。

要定义返回的字节数组对应于图像，我们可以将@GetMapping注解的produces属性设置为返回对象的MIME类型：

```java
@GetMapping(
    value = "/get-image-with-media-type",
    produces = MediaType.IMAGE_JPEG_VALUE
)
public @ResponseBody byte[] getImageWithMediaType() throws IOException {
    InputStream in = getClass()
        .getResourceAsStream("/cn/tuyucheng/taketoday/produceimage/image.jpg");
    return IOUtils.toByteArray(in);
}

```

此处，produces设置为MediaType.IMAGE_JPEG_VALUE以指示返回的对象必须作为JPEG图像处理。

现在浏览器将识别并正确显示响应主体为图像。

## 5. 使用produces返回原始数据

根据我们要返回的对象类型，参数produces可以设置为很多不同的值(完整列表可以在[这里](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/MediaType.html)找到)。

如果我们想返回一个原始文件，我们可以简单地使用APPLICATION_OCTET_STREAM_VALUE：

```java
@GetMapping(
    value = "/get-file",
    produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
)
public @ResponseBody byte[] getFile() throws IOException {
    InputStream in = getClass()
        .getResourceAsStream("/cn/tuyucheng/taketoday/produceimage/data.txt");
    return IOUtils.toByteArray(in);
}
```

## 6. 动态设置contentType

现在我们将说明如何动态设置响应的内容类型。在这种情况下，我们不能使用produces参数，因为它需要一个常量，我们需要直接设置[ResponseEntity](https://www.baeldung.com/spring-response-entity)的contentType：

```java
@GetMapping("/get-image-dynamic-type")
@ResponseBody
public ResponseEntity<InputStreamResource> getImageDynamicType(@RequestParam("jpg") boolean jpg) {
    MediaType contentType = jpg ? MediaType.IMAGE_JPEG : MediaType.IMAGE_PNG;
    InputStream in = jpg ? 
        getClass().getResourceAsStream("/cn/tuyucheng/taketoday/produceimage/image.jpg") : 
        getClass().getResourceAsStream("/cn/tuyucheng/taketoday/produceimage/image.png");
    return ResponseEntity.ok()
        .contentType(contentType)
        .body(new InputStreamResource(in));
}
```

我们将根据查询参数设置返回图像的内容类型。

## 7. 总结

在这篇简短的文章中，我们讨论了一个简单的问题，即从Spring Controller返回图像或文件。