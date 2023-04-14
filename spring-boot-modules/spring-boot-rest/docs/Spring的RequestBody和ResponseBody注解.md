## 1. 概述

在本快速教程中，我们简要概述了 Spring @RequestBody和@ResponseBody注解。

## 延伸阅读：

## [Spring 处理程序映射指南](https://www.baeldung.com/spring-handler-mappings)

本文解释了 HandlerMapping 实现如何将 URL 解析为特定的 Handler。

[阅读更多](https://www.baeldung.com/spring-handler-mappings)→

## [Spring 控制器快速指南](https://www.baeldung.com/spring-controllers)

Spring 控制器的快速实用指南 - 适用于典型的 MVC 应用程序和 REST API。

[阅读更多](https://www.baeldung.com/spring-controllers)→

## [Spring @Controller 和@RestController 注解](https://www.baeldung.com/spring-controller-vs-restcontroller)

了解 Spring MVC 中@Controller 和@RestController 注解的区别。

[阅读更多](https://www.baeldung.com/spring-controller-vs-restcontroller)→

## 2. @RequestBody

简单地说，@ RequestBody注解将HttpRequest主体映射到传输或域对象，从而可以将入站HttpRequest主体自动反序列化到 Java 对象上。

首先，让我们看一下 Spring 控制器方法：

```java
@PostMapping("/request")
public ResponseEntity postController(
  @RequestBody LoginForm loginForm) {
 
    exampleService.fakeAuthenticate(loginForm);
    return ResponseEntity.ok(HttpStatus.OK);
}
```

假设指定了适当的类型，Spring 会自动将 JSON 反序列化为 Java 类型。

默认情况下，我们用@RequestBody注解注解的类型必须对应于从我们的客户端控制器发送的 JSON：

```java
public class LoginForm {
    private String username;
    private String password;
    // ...
}
```

在这里，我们用来表示HttpRequest正文的对象映射到我们的LoginForm对象。

让我们使用 CURL 对此进行测试：

```bash
curl -i 
-H "Accept: application/json" 
-H "Content-Type:application/json" 
-X POST --data 
  '{"username": "johnny", "password": "password"}' "https://localhost:8080/.../request"
```

这就是我们使用@RequestBody注解创建 Spring REST API 和 Angular 客户端所需的全部内容。

## 3. @ResponseBody

@ResponseBody注解告诉控制器返回的对象自动序列化为 JSON 并传递回HttpResponse对象。

假设我们有一个自定义的Response对象：

```java
public class ResponseTransfer {
    private String text; 
    
    // standard getters/setters
}
```

接下来，可以实现关联的控制器：

```java
@Controller
@RequestMapping("/post")
public class ExamplePostController {

    @Autowired
    ExampleService exampleService;

    @PostMapping("/response")
    @ResponseBody
    public ResponseTransfer postResponseController(
      @RequestBody LoginForm loginForm) {
        return new ResponseTransfer("Thanks For Posting!!!");
     }
}
```

在我们浏览器的开发者控制台或者使用像 Postman 这样的工具，我们可以看到如下响应：

```plaintext
{"text":"Thanks For Posting!!!"}
```

请记住，我们不需要使用 @ResponseBody 注解来注解@RestController-注解的控制器，因为 Spring 默认情况下会这样做。

### 3.1. 设置内容类型

当我们使用@ResponseBody注解时，我们仍然能够显式设置我们的方法返回的内容类型。

为此，我们可以使用@RequestMapping的 produces属性。请注意，@PostMapping、@GetMapping等注解为该参数定义了别名。

现在让我们添加一个发送 JSON 响应的新端点：

```java
@PostMapping(value = "/content", produces = MediaType.APPLICATION_JSON_VALUE)
@ResponseBody
public ResponseTransfer postResponseJsonContent(
  @RequestBody LoginForm loginForm) {
    return new ResponseTransfer("JSON Content!");
}
```

在示例中，我们使用了MediaType.APPLICATION_JSON_VALUE常量。或者，我们可以直接使用application/json。

接下来，让我们实现一个新方法，映射到相同的/content路径，但返回 XML 内容：

```java
@PostMapping(value = "/content", produces = MediaType.APPLICATION_XML_VALUE)
@ResponseBody
public ResponseTransfer postResponseXmlContent(
  @RequestBody LoginForm loginForm) {
    return new ResponseTransfer("XML Content!");
}
```

现在，根据请求标头中发送的Accept参数的值，我们将得到不同的响应。

让我们看看实际效果：

```bash
curl -i  
-H "Accept: application/json"  
-H "Content-Type:application/json"  
-X POST --data 
  '{"username": "johnny", "password": "password"}' "https://localhost:8080/.../content"
```

CURL 命令返回一个 JSON 响应：

```bash
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 20 Feb 2020 19:43:06 GMT

{"text":"JSON Content!"}
```

现在，让我们更改Accept参数：

```bash
curl -i 
-H "Accept: application/xml" 
-H "Content-Type:application/json" 
-X POST --data
  '{"username": "johnny", "password": "password"}' "https://localhost:8080/.../content"
```

正如预期的那样，这次我们得到了一个 XML 内容：

```java
HTTP/1.1 200
Content-Type: application/xml
Transfer-Encoding: chunked
Date: Thu, 20 Feb 2020 19:43:19 GMT

<ResponseTransfer><text>XML Content!</text></ResponseTransfer>
```

## 4。总结

我们为 Spring 应用程序构建了一个简单的 Angular 客户端，它演示了如何使用@RequestBody 和@ResponseBody注解。

此外，我们展示了如何在使用@ResponseBody时设置内容类型。