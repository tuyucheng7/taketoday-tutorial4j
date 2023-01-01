## 1. 简介

使用Spring，我们通常有很多方法来实现相同的目标，包括微调HTTP响应。

在这个简短的教程中，我们将了解如何使用ResponseEntity设置HTTP响应的正文、状态和标头。

## 2. ResponseEntity

**ResponseEntity表示整个HTTP响应：状态代码、标头和正文**，因此，我们可以使用它来完全配置HTTP响应。

如果我们想使用它，我们必须从端点返回它；Spring负责其余的工作。

ResponseEntity是一种泛型类型，因此，我们可以使用任何类型作为响应主体：

```java
@GetMapping("/hello")
ResponseEntity<String> hello() {
    return new ResponseEntity<>("Hello World!", HttpStatus.OK);
}
```

由于我们以编程方式指定响应状态，因此我们可以针对不同的场景返回不同的状态代码：

```java
@GetMapping("/age")
ResponseEntity<String> age(@RequestParam("yearOfBirth") int yearOfBirth) {
 
    if (isInFuture(yearOfBirth)) {
        return new ResponseEntity<>(
          "Year of birth cannot be in the future", 
          HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>("Your age is " + calculateAge(yearOfBirth), HttpStatus.OK);
}
```

此外，我们可以设置HTTP标头：

```java
@GetMapping("/customHeader")
ResponseEntity<String> customHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Custom-Header", "foo");
        
    return new ResponseEntity<>("Custom header set", headers, HttpStatus.OK);
}
```

此外，**ResponseEntity提供了两个嵌套的构建器接口**：HeadersBuilder及其子接口BodyBuilder。因此，我们可以通过ResponseEntity的静态方法访问它们的功能。

最简单的情况是带有正文和HTTP 200响应代码的响应：

```java
@GetMapping("/hello")
ResponseEntity<String> hello() {
    return ResponseEntity.ok("Hello World!");
}
```

对于最常用的[HTTP状态代码](https://www.baeldung.com/cs/http-status-codes)，我们使用静态方法：

```java
BodyBuilder accepted();
BodyBuilder badRequest();
BodyBuilder created(java.net.URI location);
HeadersBuilder<?> noContent();
HeadersBuilder<?> notFound();
BodyBuilder ok();
```

此外，我们可以使用BodyBuilder status(HttpStatus status)和BodyBuilder status(int status)方法来设置任何HTTP状态。

最后，使用ResponseEntity<T> BodyBuilder.body(T body)我们可以设置HTTP响应主体：

```java
@GetMapping("/age")
ResponseEntity<String> age(@RequestParam("yearOfBirth") int yearOfBirth) {
    if (isInFuture(yearOfBirth)) {
        return ResponseEntity.badRequest().body("Year of birth cannot be in the future");
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body("Your age is " + calculateAge(yearOfBirth));
}
```

我们还可以设置自定义标头：

```java
@GetMapping("/customHeader")
ResponseEntity<String> customHeader() {
    return ResponseEntity.ok()
        .header("Custom-Header", "foo")
        .body("Custom header set");
}
```

由于BodyBuilder.body()返回的是ResponseEntity而不是BodyBuilder，因此它应该是最后一次调用。

请注意，使用HeaderBuilder我们无法设置响应主体的任何属性。

从控制器返回ResponseEntity<T\>对象时，我们可能会在处理请求时遇到异常或错误，并希望**将与错误相关的信息表示为某种其他类型(比方说E)返回给用户**。

Spring 3.2通过**新的@ControllerAdvice注解带来了对全局@ExceptionHandler的支持**，它可以处理这些类型的场景。有关详细信息，请参阅我们现有的[文章](https://www.baeldung.com/exception-handling-for-rest-with-spring)。

**虽然ResponseEntity非常强大，但我们不应该过度使用它**。在简单的情况下，还有其他选项可以满足我们的需求，并且它们会产生更简洁的代码。

## 3. 备选方案

### 3.1 @ResponseBody

在经典的Spring MVC应用程序中，端点通常返回呈现的HTML页面。有时我们只需要返回实际数据；例如，当我们将端点与AJAX一起使用时。

在这种情况下，我们可以使用@ResponseBody标注请求处理程序方法，Spring将该方法的结果值视为HTTP响应主体本身。

有关更多信息，[本文是一个很好的起点](https://www.baeldung.com/spring-request-response-body)。

### 3.2 @ResponseStatus

当端点成功返回时，Spring提供HTTP 200(OK)响应。如果端点抛出异常，Spring会查找异常处理程序来告知要使用的HTTP状态。

我们可以使用@ResponseStatus标注这些方法，因此，Spring**返回一个自定义的HTTP状态**。

有关更多示例，请访问我们关于[自定义状态代码](https://www.baeldung.com/spring-response-status)的文章。

### 3.3 直接操纵响应

Spring还允许我们直接访问javax.servlet.http.HttpServletResponse对象；我们只需要将它声明为方法参数：

```java
@GetMapping("/manual")
void manual(HttpServletResponse response) throws IOException {
    response.setHeader("Custom-Header", "foo");
    response.setStatus(200);
    response.getWriter().println("Hello World!");
}
```

由于Spring在底层实现之上提供了抽象和附加功能，因此**我们不应该以这种方式操作响应**。

## 4. 总结

在本文中，我们讨论了在Spring中操作HTTP响应的多种方法，并检查了它们的优缺点。