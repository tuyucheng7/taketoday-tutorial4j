## 1. 概述

在这篇简短的文章中，我们将了解如何在我们的控制器中抛出异常以及如何使用Spring MockMvc测试这些异常。

## 2. 在控制器中抛出异常

让我们开始学习如何从控制器启动异常。

我们可以把我们从控制器公开的服务想象成普通的Java函数：

```java
@GetMapping("/exception/throw")
public void getException() throws Exception {
    throw new Exception("error");
}
```

现在，让我们看看调用此服务时会发生什么。首先，我们会注意到服务的响应代码是500，这意味着内部服务器错误。

其次，我们收到这样的响应主体：

```json
{
    "timestamp": 1592074599854,
    "status": 500,
    "error": "Internal Server Error",
    "message": "No message available",
    "trace": "java.lang.Exception at cn.tuyucheng.taketoday.controllers.ExceptionController.getException(ExceptionController.java:26) ..."
}
```

总之，当我们从RestController抛出异常时，服务响应会自动映射到500响应代码，并且异常的堆栈跟踪包含在响应主体中。

## 3. 将异常映射到HTTP响应代码

现在我们将学习如何将异常映射到500以外的不同响应代码。

为此，我们将创建自定义异常并使用Spring提供的ResponseStatus注解。让我们创建这些自定义异常：

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadArgumentsException extends RuntimeException {

    public BadArgumentsException(String message) {
        super(message);
    }
}
```

```java
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalException extends RuntimeException {

    public InternalException(String message) {
        super(message);
    }
}
```

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

第二步也是最后一步是在我们的控制器中创建一个简单的服务来抛出这些异常：

```java
@GetMapping("/exception/{exception_id}")
public void getSpecificException(@PathVariable("exception_id") String pException) {
    if("not_found".equals(pException)) {
        throw new ResourceNotFoundException("resource not found");
    }
    else if("bad_arguments".equals(pException)) {
        throw new BadArgumentsException("bad arguments");
    }
    else {
        throw new InternalException("internal error");
    }
}
```

现在，让我们看看服务对我们映射的不同异常的不同响应：

-   对于not_found，我们收到404的响应代码
-   给定值bad_arguments，我们收到400的响应代码
-   对于任何其他值，我们仍然会收到500作为响应代码

除了响应代码之外，我们还会收到一个与上一节中收到的响应主体格式相同的主体。

## 4. 测试我们的控制器

最后，我们将了解如何测试我们的控制器是否抛出正确的异常。

第一步是创建一个测试类并创建一个MockMvc实例：

```java
@Autowired
private MockMvc mvc;
```

接下来，让我们为我们的服务可以接收的每个值创建测试用例：

```java
@Test
public void givenNotFound_whenGetSpecificException_thenNotFoundCode() throws Exception {
    String exceptionParam = "not_found";

    mvc.perform(get("/exception/{exception_id}", exceptionParam)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
        .andExpect(result -> assertEquals("resource not found", result.getResolvedException().getMessage()));
}

@Test
public void givenBadArguments_whenGetSpecificException_thenBadRequest() throws Exception {
    String exceptionParam = "bad_arguments";

    mvc.perform(get("/exception/{exception_id}", exceptionParam)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadArgumentsException))
        .andExpect(result -> assertEquals("bad arguments", result.getResolvedException().getMessage()));
}

@Test
public void givenOther_whenGetSpecificException_thenInternalServerError() throws Exception {
    String exceptionParam = "dummy";

    mvc.perform(get("/exception/{exception_id}", exceptionParam)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof InternalException))
        .andExpect(result -> assertEquals("internal error", result.getResolvedException().getMessage()));
}
```

通过这些测试，我们正在检查响应代码、引发的异常类型以及该异常的消息是否是每个值的预期消息。

## 5. 总结

在本教程中，我们学习了如何处理Spring RestController中的异常，以及如何测试每个公开的服务是否抛出预期的异常。