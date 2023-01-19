## 1. 简介

内容类型指示如何解释请求/响应中存在的数据。每当控制器收到 Web 请求时，它都会使用或生成某些媒体类型。在这个请求-响应模型中，可以使用/生产多种媒体类型，JSON 就是其中之一。

在本快速教程中，我们将探讨在Spring MVC中设置内容类型的不同方法。

## 2. Spring中的@RequestMapping

简单的说，[@RequestMapping](https://www.baeldung.com/spring-requestmapping)是一个重要的注解，将web请求映射到Spring控制器。它具有各种属性，包括 HTTP 方法、请求参数、标头和媒体类型。

通常，媒体类型分为两类：可消费的和可生产的。除了这些，我们还可以[在 Spring 中定义一个自定义的媒体类型](https://www.baeldung.com/spring-rest-custom-media-type)。主要目的是将主要映射限制为我们的请求处理程序的媒体类型列表。

### 2.1. 耗材类型

使用consumes属性，我们可以指定控制器将从客户端接受的媒体类型。我们也可以提供媒体类型列表。让我们定义一个简单的端点：

```java
@RequestMapping(value = "/greetings", method = RequestMethod.POST, consumes="application/json")
public void addGreeting(@RequestBody ContentType type, Model model) {
    // code here
}
```

如果客户端指定资源无法使用的媒体类型，系统将生成 HTTP“415 Unsupported Media Type”错误。

### 2.2. 可生产的媒体类型

与consumes属性相反，produces指定资源可以生成并发送回客户端的媒体类型。毫无疑问，我们可以使用选项列表。如果某个资源无法生成所请求的资源，系统将生成 HTTP“406 Not Acceptable”错误。

让我们从一个公开 JSON 字符串的 API 的简单示例开始。

这是我们的终点：

```java
@RequestMapping(
  value = "/greetings-with-response-body", 
  method = RequestMethod.GET, 
  produces="application/json"
) 
@ResponseBody
public String getGreetingWhileReturnTypeIsString() { 
    return "{"test": "Hello using @ResponseBody"}";
}
```

我们将使用 CURL 对此进行测试：

```bash
curl http://localhost:8080/greetings-with-response-body
```

上面的命令产生响应：

```json
{ "test": "Hello using @ResponseBody" }

```

根据标头中存在的内容类型，@ResponseBody仅将方法返回值绑定到 Web 响应正文。

## 3.内容类型设置不当

当方法的返回类型为String，并且类路径中不存在 JSON Mapper 时，返回值由StringHttpMessageConverter类处理，该类将内容类型设置为“text/plain”。这通常会导致控制器无法生成预期内容类型的问题。

让我们看看解决这个问题的不同方法。

### 3.1. 将@ResponseBody与 JSON 映射器一起使用

[Jackson ](https://www.baeldung.com/jackson)[ObjectMapper](https://www.baeldung.com/jackson-object-mapper-tutorial)类从字符串、流或文件中解析 JSON 。如果 Jackson 在类路径上，则 Spring 应用程序中的任何控制器都默认呈现 JSON 响应。

要在类路径中包含[Jackson](https://search.maven.org/search?q=g:com.fasterxml.jackson.core AND a:jackson-databind)，我们需要在pom.xml中添加以下依赖项：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.12.4</version>
</dependency>

```

我们将添加一个单元测试来验证响应：

```java
@Test
public void givenReturnTypeIsString_whenJacksonOnClasspath_thenDefaultContentTypeIsJSON() 
  throws Exception {
    
    // Given
    String expectedMimeType = "application/json";
    
    // Then
    String actualMimeType = this.mockMvc.perform(MockMvcRequestBuilders.get("/greetings-with-response-body", 1))
      .andReturn().getResponse().getContentType();

    Assert.assertEquals(expectedMimeType, actualMimeType);
}
```

### 3.2. 使用ResponseEntity

与[@ResponseBody 不同](https://www.baeldung.com/spring-request-response-body)，[ResponseEntity](https://www.baeldung.com/spring-response-entity)是代表整个 HTTP 响应的通用类型。因此，我们可以控制进入其中的任何内容：状态代码、标头和正文。

让我们定义一个新端点：

```java
@RequestMapping(
  value = "/greetings-with-response-entity",
  method = RequestMethod.GET, 
  produces = "application/json"
)
public ResponseEntity<String> getGreetingWithResponseEntity() {
    final HttpHeaders httpHeaders= new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    return new ResponseEntity<String>("{"test": "Hello with ResponseEntity"}", httpHeaders, HttpStatus.OK);
}
```

在浏览器的开发者控制台中，我们可以看到以下响应：

```json
{"test": "Hello with ResponseEntity"}
```

使用ResponseEntity，我们应该在我们的[调度程序 servlet](https://www.baeldung.com/spring-dispatcherservlet)中有注解驱动的标签：

```xml
<mvc:annotation-driven />
```

简单地说，上面的标签可以更好地控制[Spring MVC](https://www.baeldung.com/spring-mvc)的内部工作。

我们将使用测试用例验证响应的内容类型：

```java
@Test
public void givenReturnTypeIsResponseEntity_thenDefaultContentTypeIsJSON() throws Exception {
    
    // Given
    String expectedMimeType = "application/json";
    
    // Then
    String actualMimeType = this.mockMvc.perform(MockMvcRequestBuilders.get("/greetings-with-response-entity", 1))
      .andReturn().getResponse().getContentType();

    Assert.assertEquals(expectedMimeType, actualMimeType);
}

```

### 3.3. 使用Map<String, Object>返回类型

最后但同样重要的是，我们还可以通过将返回类型从String更改为Map来设置内容类型。此Map返回类型将需要封送处理，并返回一个 JSON 对象。

这是我们的新端点：

```java
@RequestMapping(
  value = "/greetings-with-map-return-type", 
  method = RequestMethod.GET, 
  produces = "application/json"
)
@ResponseBody
public Map<String, Object> getGreetingWhileReturnTypeIsMap() {
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("test", "Hello from map");
    return map;
}
```

让我们看看实际效果：

```bash
curl http://localhost:8080/greetings-with-map-return-type
```

curl 命令返回一个 JSON 响应：

```json
{ "test": "Hello from map" }
```

## 4. 总结

在本文中，我们了解了如何在Spring MVC中设置内容类型，首先是在类路径中添加 Json 映射器，然后使用 ResponseEntity，最后将返回类型从 String 更改为 Map。