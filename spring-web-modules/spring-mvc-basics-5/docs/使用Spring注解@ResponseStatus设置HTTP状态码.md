## 1. 简介

在Spring MVC中，我们有很多方法来设置 HTTP 响应的状态码。

在这个简短的教程中，我们将看到最直接的方法：使用@ResponseStatus注解。

## 2. 关于控制器方法

当端点成功返回时，Spring 提供 HTTP 200 (OK) 响应。

如果我们想指定控制器方法的响应状态，我们可以用@ResponseStatus 标记该方法。对于所需的响应状态，它有两个可互换的参数：代码和值。例如，我们可以[指示服务器拒绝冲泡咖啡，因为它是茶壶](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/418)：

```java
@ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
void teaPot() {}
```

当我们想要发出错误信号时，我们可以通过reason参数提供错误消息：

```java
@ResponseStatus(HttpStatus.BAD_REQUEST, reason = "Some parameters are invalid")
void onIllegalArgumentException(IllegalArgumentException exception) {}
```

请注意，当我们设置reason时，Spring 调用HttpServletResponse.sendError()。因此，它会向客户端发送一个 HTML 错误页面，这使得它不适合 REST 端点。

另请注意，当标记的方法成功完成(不抛出Exception)时，Spring 仅使用@ResponseStatus 。

## 3.使用错误处理程序

我们有三种方法可以使用@ResponseStatus将异常转换为 HTTP 响应状态：

-   使用@ExceptionHandler
-   使用@ControllerAdvice
-   标记异常类

为了使用前两个解决方案，我们必须定义一个错误处理程序方法。[你可以在本文](https://www.baeldung.com/exception-handling-for-rest-with-spring)中阅读有关此主题的更多信息。

我们可以将@ResponseStatus与这些错误处理程序方法一起使用，就像我们在上一节中使用常规 MVC 方法一样。

当我们不需要动态错误响应时，最直接的解决方案是第三种：用@ResponseStatus 标记 Exception 类：

```java
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class CustomException extends RuntimeException {}
```

当 Spring 捕获此Exception时，它会使用我们在@ResponseStatus中提供的设置。

请注意，当我们用@ResponseStatus标记异常类时，无论我们是否设置原因，Spring 总是调用HttpServletResponse.sendError()。

另请注意，Spring 对子类使用相同的配置，除非我们也用@ResponseStatus标记它们。

## 4. 总结

在本文中，我们看到了如何使用@ResponseStatus在不同的场景中设置 HTTP 响应代码，包括错误处理。