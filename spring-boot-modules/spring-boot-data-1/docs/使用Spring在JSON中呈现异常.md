## 1. 概述

Happy-path REST很好理解，而Spring使这在Java中变得容易。

在本教程中，我们将**使用Spring将Java异常作为JSON响应的一部分进行传递**。

如需更广泛的了解，请查看我们关于[使用Spring进行REST错误处理](https://www.baeldung.com/exception-handling-for-rest-with-spring)和[创建Java全局异常处理程序](https://www.baeldung.com/java-global-exception-handler)的文章。

## 2. 带注解的解决方案

我们将使用三个基本的Spring MVC注解来解决这个问题：

+ **@RestControllerAdvice**其中包含@ControllerAdvice将周围的类注册为每个@Controller应该知道的东西，@ResponseBody告诉Spring将该方法的响应呈现为JSON

+ **@ExceptionHandler告诉Spring应该为给定的异常调用我们的哪些方法**

它们一起创建了一个Spring bean，用于处理我们为其配置的任何异常。下面是有关[结合使用@ControllerAdvice和@ExceptionHandler](https://www.baeldung.com/exception-handling-for-rest-with-spring#controlleradvice)的更多详细信息。

## 3. 例子

首先，让我们创建一个任意的自定义异常返回给客户端：

```java
public class CustomException extends RuntimeException {
    // constructors
}
```

其次，让我们定义一个类来处理异常并将其作为JSON传递给客户端：

```java
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CustomException handleCustomException(CustomException ce) {
        return ce;
    }
}
```

请注意，我们添加了[@ResponseStatus](https://www.baeldung.com/spring-response-status#error-handling)注解。这将指定要发送给客户端的状态码，在我们的例子中是内部服务器错误500。此外，@ResponseBody确保将对象以JSON的方式发送回客户端。最后，下面是一个虚拟控制器，在这里只是简单的抛出我们的自定义异常：

```java
@Controller
public class MainController {

    @GetMapping("/")
    public void index() throws CustomException {
        throw new CustomException();
    }
}
```

## 4. 总结

在这篇文章中，我们演示了如何在Spring中处理异常。此外，我们展示了如何将它们以JSON的方式发送回客户端。