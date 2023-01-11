## 1. 概述

在本教程中，我们将讨论如何为 Spring REST API 实现全局错误处理程序。

我们将使用每个异常的语义为客户端构建有意义的错误消息，其明确目标是为该客户端提供所有信息以轻松诊断问题。

## 进一步阅读：

## [春季响应状态异常](https://www.baeldung.com/spring-response-status-exception)

了解如何在 Spring 中使用 ResponseStatusException 将状态代码应用于 HTTP 响应。

[阅读更多](https://www.baeldung.com/spring-response-status-exception)→

## [使用 Spring 的 REST 错误处理](https://www.baeldung.com/exception-handling-for-rest-with-spring)

REST API 的异常处理 - 说明 Spring 推荐的新方法和早期解决方案。

[阅读更多](https://www.baeldung.com/exception-handling-for-rest-with-spring)→

## 2.自定义错误消息

让我们从实现一个通过网络发送错误的简单结构开始—— ApiError：

```java
public class ApiError {

    private HttpStatus status;
    private String message;
    private List<String> errors;

    public ApiError(HttpStatus status, String message, List<String> errors) {
        super();
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public ApiError(HttpStatus status, String message, String error) {
        super();
        this.status = status;
        this.message = message;
        errors = Arrays.asList(error);
    }
}
```

这里的信息应该很简单：

-   status – HTTP 状态码
-   message – 与异常相关的错误消息
-   error – 构造的错误消息列表

当然，对于 Spring 中实际的异常处理逻辑，[我们将使用](https://www.baeldung.com/exception-handling-for-rest-with-spring)@ControllerAdvice注解：

```java
@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {
    ...
}
```

## 3. 处理错误请求异常

### 3.1。处理异常

现在让我们看看如何处理最常见的客户端错误——基本上是客户端向 API 发送无效请求的场景：

-   BindException – 发生致命绑定错误时引发此异常。
-   MethodArgumentNotValidException – 当使用@Valid注解的参数验证失败时引发此异常：

```java
@Override
protected ResponseEntity<Object> handleMethodArgumentNotValid(
  MethodArgumentNotValidException ex, 
  HttpHeaders headers, 
  HttpStatus status, 
  WebRequest request) {
    List<String> errors = new ArrayList<String>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
        errors.add(error.getField() + ": " + error.getDefaultMessage());
    }
    for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
        errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
    }
    
    ApiError apiError = 
      new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
    return handleExceptionInternal(
      ex, apiError, headers, apiError.getStatus(), request);
}

```

请注意，我们正在覆盖ResponseEntityExceptionHandler之外的基本方法并提供我们自己的自定义实现。

情况并非总是如此。有时，我们需要处理在基类中没有默认实现的自定义异常，稍后我们将在此处看到。

下一个：

-   MissingServletRequestPartException – 当未找到多部分请求的一部分时引发此异常。
-   MissingServletRequestParameterException – 当请求缺少参数时抛出此异常：

```java
@Override
protected ResponseEntity<Object> handleMissingServletRequestParameter(
  MissingServletRequestParameterException ex, HttpHeaders headers, 
  HttpStatus status, WebRequest request) {
    String error = ex.getParameterName() + " parameter is missing";
    
    ApiError apiError = 
      new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
    return new ResponseEntity<Object>(
      apiError, new HttpHeaders(), apiError.getStatus());
}
```

-   ConstraintViolationException – 此异常报告约束违规的结果：

```java
@ExceptionHandler({ ConstraintViolationException.class })
public ResponseEntity<Object> handleConstraintViolation(
  ConstraintViolationException ex, WebRequest request) {
    List<String> errors = new ArrayList<String>();
    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
        errors.add(violation.getRootBeanClass().getName() + " " + 
          violation.getPropertyPath() + ": " + violation.getMessage());
    }

    ApiError apiError = 
      new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
    return new ResponseEntity<Object>(
      apiError, new HttpHeaders(), apiError.getStatus());
}
```

-   TypeMismatchException – 尝试使用错误类型设置 bean 属性时引发此异常。
-   MethodArgumentTypeMismatchException – 当方法参数不是预期的类型时抛出此异常：

```java
@ExceptionHandler({ MethodArgumentTypeMismatchException.class })
public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
  MethodArgumentTypeMismatchException ex, WebRequest request) {
    String error = 
      ex.getName() + " should be of type " + ex.getRequiredType().getName();

    ApiError apiError = 
      new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
    return new ResponseEntity<Object>(
      apiError, new HttpHeaders(), apiError.getStatus());
}
```

### 3.2. 从客户端使用 API

现在让我们看一下遇到MethodArgumentTypeMismatchException的测试。

我们将发送一个id作为String而不是long的请求：

```java
@Test
public void whenMethodArgumentMismatch_thenBadRequest() {
    Response response = givenAuth().get(URL_PREFIX + "/api/foos/ccc");
    ApiError error = response.as(ApiError.class);

    assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
    assertEquals(1, error.getErrors().size());
    assertTrue(error.getErrors().get(0).contains("should be of type"));
}
```

最后，考虑到同样的请求：

```bash
Request method:	GET
Request path:	http://localhost:8080/spring-security-rest/api/foos/ccc

```

这是这种 JSON 错误响应的样子：

```bash
{
    "status": "BAD_REQUEST",
    "message": 
      "Failed to convert value of type [java.lang.String] 
       to required type [java.lang.Long]; nested exception 
       is java.lang.NumberFormatException: For input string: "ccc"",
    "errors": [
        "id should be of type java.lang.Long"
    ]
}
```

## 4.处理NoHandlerFoundException

接下来，我们可以自定义我们的 servlet 来抛出这个异常，而不是发送 404 响应：

```xml
<servlet>
    <servlet-name>api</servlet-name>
    <servlet-class>
      org.springframework.web.servlet.DispatcherServlet</servlet-class>        
    <init-param>
        <param-name>throwExceptionIfNoHandlerFound</param-name>
        <param-value>true</param-value>
    </init-param>
</servlet>
```

然后，一旦发生这种情况，我们可以像处理任何其他异常一样简单地处理它：

```java
@Override
protected ResponseEntity<Object> handleNoHandlerFoundException(
  NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();

    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), error);
    return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
}
```

这是一个简单的测试：

```java
@Test
public void whenNoHandlerForHttpRequest_thenNotFound() {
    Response response = givenAuth().delete(URL_PREFIX + "/api/xx");
    ApiError error = response.as(ApiError.class);

    assertEquals(HttpStatus.NOT_FOUND, error.getStatus());
    assertEquals(1, error.getErrors().size());
    assertTrue(error.getErrors().get(0).contains("No handler found"));
}
```

让我们看一下完整的请求：

```bash
Request method:	DELETE
Request path:	http://localhost:8080/spring-security-rest/api/xx
```

和错误 JSON 响应：

```bash
{
    "status":"NOT_FOUND",
    "message":"No handler found for DELETE /spring-security-rest/api/xx",
    "errors":[
        "No handler found for DELETE /spring-security-rest/api/xx"
    ]
}
```

接下来，我们将看看另一个有趣的例外。

## 5.处理HttpRequestMethodNotSupportedException

当我们使用不受支持的 HTTP 方法发送请求时，会发生HttpRequestMethodNotSupportedException ：

```java
@Override
protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
  HttpRequestMethodNotSupportedException ex, 
  HttpHeaders headers, 
  HttpStatus status, 
  WebRequest request) {
    StringBuilder builder = new StringBuilder();
    builder.append(ex.getMethod());
    builder.append(
      " method is not supported for this request. Supported methods are ");
    ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

    ApiError apiError = new ApiError(HttpStatus.METHOD_NOT_ALLOWED, 
      ex.getLocalizedMessage(), builder.toString());
    return new ResponseEntity<Object>(
      apiError, new HttpHeaders(), apiError.getStatus());
}
```

这是一个重现此异常的简单测试：

```java
@Test
public void whenHttpRequestMethodNotSupported_thenMethodNotAllowed() {
    Response response = givenAuth().delete(URL_PREFIX + "/api/foos/1");
    ApiError error = response.as(ApiError.class);

    assertEquals(HttpStatus.METHOD_NOT_ALLOWED, error.getStatus());
    assertEquals(1, error.getErrors().size());
    assertTrue(error.getErrors().get(0).contains("Supported methods are"));
}
```

这是完整的请求：

```bash
Request method:	DELETE
Request path:	http://localhost:8080/spring-security-rest/api/foos/1
```

和错误 JSON 响应：

```bash
{
    "status":"METHOD_NOT_ALLOWED",
    "message":"Request method 'DELETE' not supported",
    "errors":[
        "DELETE method is not supported for this request. Supported methods are GET "
    ]
}
```

## 6.处理HttpMediaTypeNotSupportedException

现在让我们处理HttpMediaTypeNotSupportedException，它发生在客户端发送具有不受支持的媒体类型的请求时：

```java
@Override
protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
  HttpMediaTypeNotSupportedException ex, 
  HttpHeaders headers, 
  HttpStatus status, 
  WebRequest request) {
    StringBuilder builder = new StringBuilder();
    builder.append(ex.getContentType());
    builder.append(" media type is not supported. Supported media types are ");
    ex.getSupportedMediaTypes().forEach(t -> builder.append(t + ", "));

    ApiError apiError = new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, 
      ex.getLocalizedMessage(), builder.substring(0, builder.length() - 2));
    return new ResponseEntity<Object>(
      apiError, new HttpHeaders(), apiError.getStatus());
}
```

这是一个遇到此问题的简单测试：

```java
@Test
public void whenSendInvalidHttpMediaType_thenUnsupportedMediaType() {
    Response response = givenAuth().body("").post(URL_PREFIX + "/api/foos");
    ApiError error = response.as(ApiError.class);

    assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, error.getStatus());
    assertEquals(1, error.getErrors().size());
    assertTrue(error.getErrors().get(0).contains("media type is not supported"));
}
```

最后，这是一个示例请求：

```bash
Request method:	POST
Request path:	http://localhost:8080/spring-security-
Headers:	Content-Type=text/plain; charset=ISO-8859-1
```

和错误 JSON 响应：

```bash
{
    "status":"UNSUPPORTED_MEDIA_TYPE",
    "message":"Content type 'text/plain;charset=ISO-8859-1' not supported",
    "errors":["text/plain;charset=ISO-8859-1 media type is not supported. 
       Supported media types are text/xml 
       application/x-www-form-urlencoded 
       application/+xml 
       application/json;charset=UTF-8 
       application/+json;charset=UTF-8 /"
    ]
}
```

## 7. 默认处理程序

最后，我们将实现一个回退处理程序——一种处理所有其他没有特定处理程序的异常的包罗万象的逻辑类型：

```java
@ExceptionHandler({ Exception.class })
public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
    ApiError apiError = new ApiError(
      HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), "error occurred");
    return new ResponseEntity<Object>(
      apiError, new HttpHeaders(), apiError.getStatus());
}
```

## 8. 总结

为 Spring REST API 构建适当的、成熟的错误处理程序非常困难，而且绝对是一个迭代过程。希望本教程将成为一个很好的起点，以及帮助 API 客户端快速轻松地诊断错误并克服它们的良好锚点。