## 1. 概述

Happy-path REST很好理解，而Spring使这在Java中很容易做到。

在本教程中，我们将使用Spring将Java异常作为JSON响应的一部分进行传递。

## 2. 带注解的解决方案

我们使用三个基本的Spring MVC注解来解决这个问题：

+ @RestControllerAdvice中包含@ControllerAdvice和@ResponseBody。@ControllerAdvice表示被标注类用于所有控制器的切面类，
  @ResponseBody告诉Spring将该方法的响应呈现为JSON。

+ **@ExceptionHandler告诉Spring给定的方法用于处理哪些异常**。

它们一起创建了一个Spring bean，用于处理我们为其配置的任何异常。

## 3. 例子

首先，让我们创建一个任意自定义作为返回给客户端的响应：

```java
import java.io.Serial;

public class CustomException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public CustomException() {
        super("Custom exception message.");
    }
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

请注意，我们添加了@ResponseStatus注解。这指定要发送给客户端的状态码，在我们的例子中是500内部服务器错误。
此外，@ResponseBody确保将对象以JSON的方式发送回客户端。最后，下面是一个控制器，在这里只是简单的抛出我们的自定义异常：

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