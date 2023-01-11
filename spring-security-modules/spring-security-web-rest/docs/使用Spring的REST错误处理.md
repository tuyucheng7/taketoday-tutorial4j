## 1. 概述

本教程将说明如何使用 Spring 为 REST API 实现异常处理。我们还将获得一些历史概览，并查看不同版本引入了哪些新选项。

在 Spring 3.2 之前，在 Spring MVC 应用程序中处理异常的两种主要方法是HandlerExceptionResolver或@ExceptionHandler注解。两者都有一些明显的缺点。

从 3.2 开始，我们有了@ControllerAdvice注解来解决前两个解决方案的局限性，并在整个应用程序中促进统一的异常处理。

现在Spring 5 引入了 ResponseStatusException 类——一种在我们的 REST API 中进行基本错误处理的快速方法。

所有这些都有一个共同点：它们很好地处理了关注点的分离。该应用程序可以正常抛出异常以指示某种失败，然后将单独处理。

最后，我们将看到Spring Boot带来了什么，以及我们如何配置它以满足我们的需求。

## 进一步阅读：

## [REST API 的自定义错误消息处理](https://www.baeldung.com/global-error-handler-in-a-spring-rest-api)

使用 Spring 为 REST API 实现全局异常处理程序。

[阅读更多](https://www.baeldung.com/global-error-handler-in-a-spring-rest-api)→

## [Spring Data REST 验证器指南](https://www.baeldung.com/spring-data-rest-validators)

Spring Data REST 验证器的快速实用指南

[阅读更多](https://www.baeldung.com/spring-data-rest-validators)→

## [Spring MVC 自定义验证](https://www.baeldung.com/spring-mvc-custom-validator)

了解如何构建自定义验证注解并在 Spring MVC 中使用它。

[阅读更多](https://www.baeldung.com/spring-mvc-custom-validator)→

## 2.方案一：Controller级@ExceptionHandler

第一个解决方案适用于@Controller级别。我们将定义一个方法来处理异常并使用@ExceptionHandler 对其进行注解：

```java
public class FooController{
    
    //...
    @ExceptionHandler({ CustomException1.class, CustomException2.class })
    public void handleException() {
        //
    }
}
```

这种方法有一个主要缺点：@ExceptionHandler注解的方法只对特定的 Controller 有效，而不是对整个应用程序全局有效。当然，将它添加到每个控制器使其不太适合一般的异常处理机制。

我们可以通过让所有控制器扩展一个基本控制器类来解决这个限制。

然而，这种解决方案对于应用程序来说可能是个问题，无论出于何种原因，这是不可能的。例如，控制器可能已经从另一个基类扩展而来，该基类可能在另一个 jar 中或不可直接修改，或者它们本身可能不可直接修改。

接下来，我们将研究另一种解决异常处理问题的方法——一种全局的并且不包括对现有工件(例如控制器)的任何更改。

## 3.解决方案2：HandlerExceptionResolver

第二种解决方案是定义一个HandlerExceptionResolver。这将解决应用程序抛出的任何异常。它还将允许我们在 REST API 中实现统一的异常处理机制。

在使用自定义解析器之前，让我们回顾一下现有的实现。

### 3.1。异常处理程序异常解析器

这个解析器是在 Spring 3.1 中引入的，默认情况下在DispatcherServlet中启用。这实际上是前面介绍的@ExceptionHandler机制如何工作的核心组件。

### 3.2. DefaultHandlerExceptionResolver

这个解析器是在 Spring 3.0 中引入的，默认情况下它在DispatcherServlet中启用。

它用于解决相应的[HTTP 状态代码](https://www.baeldung.com/cs/http-status-codes)的标准 Spring 异常，即客户端错误4xx和服务器错误5xx状态代码。[这是它处理的 Spring 异常的完整列表](http://static.springsource.org/spring/docs/3.2.x/spring-framework-reference/html/mvc.html#mvc-ann-rest-spring-mvc-exceptions)以及它们如何映射到状态代码。

虽然它确实正确设置了响应的状态代码，但一个限制是它没有为响应的正文设置任何内容。对于 REST API——状态码实际上不足以提供给客户端的信息——响应也必须有一个主体，以允许应用程序提供有关失败的附加信息。

这可以通过ModelAndView配置视图分辨率和渲染错误内容来解决，但该解决方案显然不是最优的。这就是为什么 Spring 3.2 引入了一个更好的选项，我们将在后面的部分讨论。

### 3.3. ResponseStatusExceptionResolver

这个解析器也在 Spring 3.0 中引入，默认情况下在DispatcherServlet中启用。

它的主要职责是使用自定义异常上可用的@ResponseStatus注解并将这些异常映射到 HTTP 状态代码。

这样的自定义异常可能如下所示：

```java
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class MyResourceNotFoundException extends RuntimeException {
    public MyResourceNotFoundException() {
        super();
    }
    public MyResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public MyResourceNotFoundException(String message) {
        super(message);
    }
    public MyResourceNotFoundException(Throwable cause) {
        super(cause);
    }
}
```

与DefaultHandlerExceptionResolver相同，此解析器在处理响应主体的方式上受到限制——它确实将状态代码映射到响应上，但主体仍然为空。

### 3.4. 自定义HandlerExceptionResolver

DefaultHandlerExceptionResolver和ResponseStatusExceptionResolver的组合对于为 Spring RESTful 服务提供良好的错误处理机制大有帮助。如前所述，缺点是无法控制响应的主体。

理想情况下，我们希望能够输出 JSON 或 XML，具体取决于客户端要求的格式(通过Accept标头)。

仅此一项就证明了创建一个新的自定义异常解析器是合理的：

```java
@Component
public class RestResponseStatusExceptionResolver extends AbstractHandlerExceptionResolver {

    @Override
    protected ModelAndView doResolveException(
      HttpServletRequest request, 
      HttpServletResponse response, 
      Object handler, 
      Exception ex) {
        try {
            if (ex instanceof IllegalArgumentException) {
                return handleIllegalArgument(
                  (IllegalArgumentException) ex, response, handler);
            }
            ...
        } catch (Exception handlerException) {
            logger.warn("Handling of [" + ex.getClass().getName() + "] 
              resulted in Exception", handlerException);
        }
        return null;
    }

    private ModelAndView 
      handleIllegalArgument(IllegalArgumentException ex, HttpServletResponse response) 
      throws IOException {
        response.sendError(HttpServletResponse.SC_CONFLICT);
        String accept = request.getHeader(HttpHeaders.ACCEPT);
        ...
        return new ModelAndView();
    }
}
```

这里要注意的一个细节是我们可以访问请求本身，因此我们可以考虑客户端发送的Accept标头的值。

例如，如果客户端请求application/json，那么在出现错误情况的情况下，我们希望确保返回一个使用application/json编码的响应正文。

另一个重要的实现细节是我们返回一个 ModelAndView——这是响应的主体，它允许我们在上面设置任何必要的东西。

这种方法是一种一致且易于配置的机制，用于 Spring REST 服务的错误处理。

但是，它确实有局限性：它与低级HtttpServletResponse 交互并适合使用ModelAndView的旧 MVC 模型，因此仍有改进的空间。

## 4.解决方案3：@ControllerAdvice

Spring 3.2通过 @ControllerAdvice注解支持全局@ExceptionHandler 。

这启用了一种脱离旧 MVC 模型并利用ResponseEntity以及@ExceptionHandler的类型安全性和灵活性的机制：

```java
@ControllerAdvice
public class RestResponseEntityExceptionHandler 
  extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value 
      = { IllegalArgumentException.class, IllegalStateException.class })
    protected ResponseEntity<Object> handleConflict(
      RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "This should be application specific";
        return handleExceptionInternal(ex, bodyOfResponse, 
          new HttpHeaders(), HttpStatus.CONFLICT, request);
    }
}
```

@ControllerAdvice注解允许我们将之前的多个分散的@ExceptionHandler整合到一个单一的全局错误处理组件中。

实际的机制非常简单，但也非常灵活：

-   它使我们可以完全控制响应的主体以及状态代码。
-   它提供了多个异常到同一方法的映射，以便一起处理。
-   它充分利用了较新的 RESTful ResposeEntity响应。
    

这里要记住的一件事是将使用@ExceptionHandler声明的异常与用作方法参数的异常相匹配。

如果这些不匹配，编译器不会抱怨——没有理由应该抱怨——Spring 也不会抱怨。

但是，当在运行时实际抛出异常时，异常解析机制将失败，并显示：

```bash
java.lang.IllegalStateException: No suitable resolver for argument [0] [type=...]
HandlerMethod details: ...
```

## 5.解决方案4：ResponseStatusException(Spring 5及以上)

Spring 5 引入了ResponseStatusException类。

我们可以创建它的一个实例，提供一个HttpStatus和一个可选的原因和原因：

```java
@GetMapping(value = "/{id}")
public Foo findById(@PathVariable("id") Long id, HttpServletResponse response) {
    try {
        Foo resourceById = RestPreconditions.checkFound(service.findOne(id));

        eventPublisher.publishEvent(new SingleResourceRetrievedEvent(this, response));
        return resourceById;
     }
    catch (MyResourceNotFoundException exc) {
         throw new ResponseStatusException(
           HttpStatus.NOT_FOUND, "Foo Not Found", exc);
    }
}
```

使用ResponseStatusException有什么好处？

-   非常适合原型设计：我们可以非常快速地实施基本解决方案。
-   一种类型，多种状态码：一种异常类型可能导致多种不同的响应。与@ExceptionHandler相比，这减少了紧密耦合。
-   我们不必创建尽可能多的自定义异常类。
-   我们可以更好地控制异常处理，因为可以通过编程方式创建异常。

那么权衡呢？

-   没有统一的异常处理方式：与提供全局方法的@ControllerAdvice相比，执行一些应用程序范围的约定更加困难。
-   代码：我们可能会发现自己在多个控制器中代码。

我们还应该注意到，可以在一个应用程序中组合不同的方法。

例如，我们可以 全局 实现@ControllerAdvice ，也可以在本地实现ResponseStatusException 。

但是，我们需要小心：如果可以以多种方式处理同一个异常，我们可能会注意到一些令人惊讶的行为。一种可能的约定是始终以一种方式处理一种特定类型的异常。

有关更多详细信息和更多示例，请参阅我们的[ResponseStatusException](https://www.baeldung.com/spring-response-status-exception)[教程](https://www.baeldung.com/spring-response-status-exception)。

## 6、处理Spring Security中的访问被拒绝

当经过身份验证的用户尝试访问他没有足够权限访问的资源时，会发生访问被拒绝。

### 6.1。REST 和方法级安全性

最后，让我们看看如何处理由方法级安全注解(@PreAuthorize、@PostAuthorize和@Secure )引发的 Access Denied 异常。

当然，我们也会使用前面讨论过的全局异常处理机制来处理AccessDeniedException：

```java
@ControllerAdvice
public class RestResponseEntityExceptionHandler 
  extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ AccessDeniedException.class })
    public ResponseEntity<Object> handleAccessDeniedException(
      Exception ex, WebRequest request) {
        return new ResponseEntity<Object>(
          "Access denied message here", new HttpHeaders(), HttpStatus.FORBIDDEN);
    }
    
    ...
}
```

## 7.Spring Boot支持

Spring Boot 提供了一个 ErrorController实现以合理的方式处理错误。

简而言之，它为浏览器提供后备错误页面(又名 Whitelabel 错误页面)，并为 RESTful、非 HTML 请求提供 JSON 响应：

```plaintext
{
    "timestamp": "2019-01-17T16:12:45.977+0000",
    "status": 500,
    "error": "Internal Server Error",
    "message": "Error processing the request!",
    "path": "/my-endpoint-with-exceptions"
}
```

像往常一样，Spring Boot 允许使用属性配置这些功能：

-   server.error.whitelabel.enabled：可用于禁用 Whitelabel 错误页面并依赖 servlet 容器提供 HTML 错误消息
-   server.error.include-stacktrace：始终 具有值；在 HTML 和 JSON 默认响应中包含堆栈跟踪
-   server.error.include-message： 从 2.3 版本开始，Spring Boot 在响应中隐藏了message字段，以避免泄露敏感信息；我们可以使用这个属性和一个 always 值来启用它

除了这些属性之外，我们还可以为 /错误 提供我们自己的视图解析器映射，覆盖 Whitelabel 页面。

我们还可以通过在上下文中包含一个ErrorAttributes bean 来自定义我们希望在响应中显示的属性 。我们可以扩展Spring Boot提供的 DefaultErrorAttributes类以使事情变得更简单：

```java
@Component
public class MyCustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(
      WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = 
          super.getErrorAttributes(webRequest, options);
        errorAttributes.put("locale", webRequest.getLocale()
            .toString());
        errorAttributes.remove("error");

        //...

        return errorAttributes;
    }
}
```

如果我们想进一步定义(或覆盖)应用程序将如何处理特定内容类型的错误，我们可以注册一个 ErrorController bean。

同样，我们可以利用Spring Boot提供的默认 BasicErrorController 来帮助我们。

例如，假设我们想要自定义我们的应用程序如何处理在 XML 端点中触发的错误。我们所要做的就是使用 @RequestMapping定义一个公共方法，并声明它产生application/xml媒体类型：

```java
@Component
public class MyErrorController extends BasicErrorController {

    public MyErrorController(
      ErrorAttributes errorAttributes, ServerProperties serverProperties) {
        super(errorAttributes, serverProperties.getError());
    }

    @RequestMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Map<String, Object>> xmlError(HttpServletRequest request) {
        
    // ...

    }
}
```

注意：这里我们仍然依赖于server.error.我们可能已经在项目中定义的引导属性，这些属性绑定到ServerProperties bean。

## 8. 总结

本文讨论了在 Spring 中为 REST API 实现异常处理机制的几种方法，从旧机制开始，继续支持 Spring 3.2 和 4.x 和 5.x。

与往常一样，本文中提供的代码可[在 GitHub 上](https://github.com/eugenp/tutorials/tree/master/spring-boot-rest)获得。

Spring Security相关的代码可以查看[spring-security-rest](https://github.com/eugenp/tutorials/tree/master/spring-security-modules/spring-security-web-rest)模块。