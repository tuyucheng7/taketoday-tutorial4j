## 1. 概述

这篇快速文章将演示几种从Spring MVC控制器返回自定义[HTTP状态代码](https://www.baeldung.com/cs/http-status-codes)的方法。

为了更清楚地向客户端表达请求的结果并使用HTTP协议的完整丰富语义，这通常很重要。例如，如果请求出现问题，为每种可能的问题发送特定的错误代码将允许客户端向用户显示适当的错误消息。

基本Spring MVC项目的设置超出了本文的范围，但你可以[在此处](https://www.baeldung.com/spring-mvc-tutorial)找到更多信息。

## 2. 返回自定义状态码

Spring提供了几种从其Controller类返回自定义状态代码的主要方法：

-   使用ResponseEntity
-   在异常类上使用@ResponseStatus注解，以及
-   使用@ControllerAdvice和@ExceptionHandler注解。

这些选项并不相互排斥；远非如此，它们实际上可以相互补充。

本文将介绍前两种方式(ResponseEntity和@ResponseStatus)。如果你想了解有关使用@ControllerAdvice和@ExceptionHandler的更多信息，可以[在此处](https://www.baeldung.com/exception-handling-for-rest-with-spring#controlleradvice)阅读相关内容。

### 2.1 通过ResponseEntity返回状态代码

在标准的Spring MVC控制器中，我们将定义一个简单的映射：

```java
@RequestMapping(value = "/controller", method = RequestMethod.GET)
@ResponseBody
public ResponseEntity sendViaResponseEntity() {
    return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
}
```

在收到对“/controller”的GET请求后，Spring将返回带有406代码(Not Acceptable)的响应。我们为这个例子任意选择了特定的响应代码。你可以返回任何HTTP状态代码(完整列表可在[此处](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes)找到)。

### 2.2 通过异常返回状态代码

我们将向控制器添加第二个方法来演示如何使用Exception返回状态代码：

```java
@RequestMapping(value = "/exception", method = RequestMethod.GET)
@ResponseBody
public ResponseEntity sendViaException() {
    throw new ForbiddenException();
}
```

收到对“/exception”的GET请求后，Spring将抛出ForbiddenException。这是我们将在单独的类中定义的自定义异常：

```java
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {}
```

此异常不需要代码。所有的工作都是由@ResponseStatus注解完成的。

在这种情况下，当抛出异常时，抛出异常的控制器会返回一个响应代码为403(Forbidden)的响应。如有必要，你还可以在注解中添加一条消息，该消息将与响应一起返回。

在这种情况下，该类将如下所示：

```java
@ResponseStatus(value = HttpStatus.FORBIDDEN, reason="To show an example of a custom message")
public class ForbiddenException extends RuntimeException {}
```

重要的是要注意，虽然在技术上可以使异常返回任何状态代码，但在大多数情况下，仅对错误代码(4XX和5XX)使用异常才合乎逻辑。

## 3. 总结

本教程展示了如何从Spring MVC控制器返回自定义状态代码。