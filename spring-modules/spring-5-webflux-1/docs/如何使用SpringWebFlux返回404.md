## 1. 概述

使用Spring Boot 2和新的非阻塞服务器Netty，我们不再使用Servlet API。
在本文中，让我们介绍如何使用WebFlux来表达不同类型的HTTP状态码。

## 2. 响应状态语义

遵循标准的Restful实践，我们自然需要使用完整的HTTP状态码来正确地表达API的语义。

### 2.1 默认返回状态

当请求顺利时，默认的响应状态是200(OK)：

```java

@RequestMapping("/statuses")
@RestController
public class ResponseStatusController {

    @GetMapping(value = "/ok", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<String> ok() {
        return Flux.just("ok");
    }
}
```

### 2.2 使用注解

我们可以更改默认返回状态，将@ResponseStatus注解添加到方法上：

```java
public class ResponseStatusController {

    @GetMapping(value = "/no-content", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Flux<String> noContent() {
        return Flux.empty();
    }
}
```

### 2.3 以编程方式更改状态

在某些情况下，根据我们服务器的行为，我们可以决定以编程方式更改返回的状态码，而不是默认使用的前缀返回状态码或使用注解。

我们可以直接在方法参数中注入ServerHttpResponse：

```java
public class ResponseStatusController {

    @GetMapping(value = "/accepted", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<String> accepted(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.ACCEPTED);
        return Flux.just("accepted");
    }
}
```

现在我们可以在代码中选择性地返回HTTP状态码。

### 2.4 抛出异常

每当我们抛出异常时，默认的HTTP返回状态码都会被忽略，Spring会尝试找到一个异常处理程序来处理它：

```java
public class ResponseStatusController {

    @GetMapping(value = "/bad-request")
    public Mono<String> badRequest() {
        return Mono.error(new IllegalArgumentException());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Illegal arguments")
    @ExceptionHandler(IllegalArgumentException.class)
    public void illegalArgument() {

    }
}
```

### 2.5 使用ResponseEntity

ResponseEntity类使我们可以选择我们想要返回的HTTP状态码，还可以使用fluent API进一步自定义我们的响应：

```java
public class ResponseStatusController {

    @GetMapping(value = "/unauthorized")
    public ResponseEntity<Mono<String>> unauthorized() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .header("X-Reason", "user-invalid")
                .body(Mono.just("unauthorized"));
    }
}
```

### 2.6 使用函数式端点

在Spring 5中，我们可以以函数方式定义端点，因此，我们也可以通过编程方式更改默认HTTP状态码：

```java
public class ResponseStatusController {

    @Bean
    public RouterFunction<ServerResponse> notFound() {
        return RouterFunctions.route(GET("/statuses/not-found"), request -> ServerResponse
                .notFound()
                .build());
    }
}
```

## 3. 总结

在实现HTTP API时，该框架提供了许多选项来智能地处理我们向客户端公开的状态码。

本文应该是一个很好的起点，以探讨这些问题，并了解如何使用干净的Restful语义推出富有表现力、友好的API。