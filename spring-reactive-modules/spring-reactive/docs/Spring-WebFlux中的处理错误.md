## 1. 概述

在本教程中，**我们将通过一个实际示例来了解可用于处理Spring WebFlux项目中的错误的各种策略**。

我们还将指出使用一种策略比另一种策略更有利的地方，并在最后提供指向完整源代码的链接。

## 2. 项目构建

Maven设置与我们[上一篇文章](https://www.baeldung.com/spring-webflux)相同，它提供了对Spring WebFlux的介绍。

对于我们的示例，**我们将使用一个RESTful端点，该端点将用户名作为查询参数并返回“Hello username”作为结果**。

首先，让我们创建一个路由函数，将/hello请求路由到传入的handler中名为handleRequest的方法：

```java
@Bean
public RouterFunction<ServerResponse> routes(Handler handler) {
    return RouterFunctions.route(GET("/hello")
        .and(accept(TEXT_PLAIN)), 
            handler::handleRequest);
}
```

接下来，我们将定义调用sayHello()方法的handleRequest()方法，并找到一种在ServerResponse主体中包含/返回其结果的方法：

```java
public Mono<ServerResponse> handleRequest(ServerRequest serverRequest) {
    return ServerResponse.ok()
        .body(sayHello(serverRequest), String.class);
}
```

最后，sayHello()方法是一个拼接“Hello”字符串和用户名的工具方法：

```java
private Mono<String> sayHello(ServerRequest request) {
    try {
        return Mono.just("Hello, " + request.queryParam("name").get());
    } catch (Exception e) {
        return Mono.error(e);
    }
}
```

只要用户名作为我们请求的一部分存在，例如，如果端点被调用为“/hello?username=Tonni”，则该端点将始终正常运行。

但是，**如果我们在没有指定用户名的情况下调用相同的端点，例如“/hello”，它将抛出异常**。

下面，我们将看看我们可以在哪里以及如何重新组织我们的代码来处理WebFlux中的这个异常。

## 3. 在函数级别处理错误

Mono和Flux API中内置了两个关键运算符，用于在函数级别处理错误。

让我们简要探讨一下它们及其用法。

### 3.1 使用onErrorReturn处理错误

**每当发生错误时，我们可以使用onErrorReturn()返回一个静态默认值**：

```java
public Mono<ServerResponse> handleWithErrorReturn(ServerRequest request) {
    return sayHello(request)
        .onErrorReturn("Hello, Stranger")
        .flatMap(s -> ServerResponse.ok()
            .contentType(MediaType.TEXT_PLAIN)
            .bodyValue(s));
}
```

在这里，只要错误链接函数sayHello()抛出异常，我们都会返回一个静态的“Hello Stranger”。

### 3.2 使用onErrorResume处理错误

我们可以通过三种方式使用onErrorResume来处理错误：

+ 计算动态回退值
+ 使用回退方法执行替代路径
+ 捕获、包装和重新抛出错误，例如作为自定义业务异常

让我们看看如何计算一个值：

```java
public Mono<ServerResponse> handleWithErrorResumeAndDynamicFallback(ServerRequest request) {
    return sayHello(request)
        .flatMap(s -> ServerResponse.ok()
            .contentType(MediaType.TEXT_PLAIN)
            .bodyValue(s))
        .onErrorResume(e -> (Mono.just("Hi, I looked around for your name but found: " + e.getMessage()))
            .flatMap(s -> ServerResponse.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(s)));
}
```

在这里，每当sayHello()抛出异常时，我们将返回一个字符串，该字符串由附加到字符串“Error”的动态获取的错误消息组成。

接下来，**让我们在发生错误时调用一个fallback方法**：

```java
public Mono<ServerResponse> handleWithErrorResumeAndFallbackMethod(ServerRequest request) {
    return sayHello(request)
        .flatMap(s -> ServerResponse.ok()
            .contentType(MediaType.TEXT_PLAIN)
            .bodyValue(s))
        .onErrorResume(e -> sayHelloFallback()
            .flatMap(s -> ServerResponse.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(s)));
}

private Mono<String> sayHelloFallback() {
    return Mono.just("Hello, Stranger");
}
```

在这里，每当sayHello()抛出异常时，我们都会调用fallback方法sayHelloFallback()。

使用onErrorResume()的最后一种方法是**捕获、包装并重新抛出错误**，例如，作为我们自定义的NameRequiredException：

```java
public Mono<ServerResponse> handleWithErrorResumeAndCustomException(ServerRequest request) {
    return ServerResponse.ok()
        .body(sayHello(request)
            .onErrorResume(e -> Mono.error(new NameRequiredException(
                HttpStatus.BAD_REQUEST,
                "please provide a name", e))), String.class);
}

public class NameRequiredException extends ResponseStatusException {

    public NameRequiredException(HttpStatus status, String message, Throwable e) {
        super(status, message, e);
    }
}
```

在这里，每当sayHello()抛出异常时，我们都会抛出带有“please provide a name”消息的自定义异常。

## 4. 在全局级别处理错误

到目前为止，我们提供的所有示例都是在函数级别解决了错误处理。

**但是，我们可以选择在全局级别处理我们的WebFlux错误**。为此，我们只需要采取两个步骤：

+ 自定义全局错误响应属性
+ 实现全局错误处理程序

我们的处理程序引发的异常将自动转换为HTTP状态和JSON错误正文。

为了自定义这些，我们可以简单地**扩展DefaultErrorAttributes类**并覆盖它的getErrorAttributes()方法：

```java
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> map = super.getErrorAttributes(request, options);
        map.put("status", HttpStatus.BAD_REQUEST);
        map.put("message", "please provide a name");
        return map;
    }
}
```

在这里，我们希望将状态BAD_REQUEST和消息“please provide a name”作为错误属性的一部分在发生异常时返回。

接下来，让我们**实现全局错误处理程序**。

为此，Spring提供了一个方便的AbstractErrorWebExceptionHandler类，供我们在处理全局错误时扩展和实现：

```java
@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalErrorWebExceptionHandler(GlobalErrorAttributes g, ApplicationContext applicationContext,
                                          ServerCodecConfigurer serverCodecConfigurer) {
        super(g, new WebProperties.Resources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(final ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
              RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(final ServerRequest request) {
        final Map<String, Object> errorPropertiesMap = getErrorAttributes(request, ErrorAttributeOptions.defaults());

        return ServerResponse.status(HttpStatus.BAD_REQUEST)
              .contentType(MediaType.APPLICATION_JSON)
              .body(BodyInserters.fromValue(errorPropertiesMap));
    }
}
```

在此示例中，我们将全局错误处理程序的order值设置为-2，这是为了给它比**在@Order(-1)注册的DefaultErrorWebExceptionHandler**更高的优先级。

errorAttributes对象将是我们在Web异常处理程序的构造函数中传递的对象的精确副本。理想情况下，这应该是我们自定义的错误属性类。

然后我们明确声明我们希望将所有错误处理请求路由到renderErrorResponse()方法。

最后，我们获取错误属性并将它们插入到服务器响应正文中。

然后，这会生成一个JSON 响应，其中包含错误详细信息、HTTP状态码和机器客户端的异常消息。对于浏览器客户端，它有一个“白标”错误处理程序，以HTML格式呈现相同的数据。当然，这可以定制。

## 5. 总结

在本文中，我们研究了可用于处理Spring WebFlux项目中的错误的各种策略，并指出了使用一种策略可能比另一种策略更有利的地方。