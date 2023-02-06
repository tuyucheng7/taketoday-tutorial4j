## 1. 概述

过滤器的使用在Web应用程序中很普遍，因为它们为我们提供了一种无需更改端点即可修改请求或响应的方法。

在本快速教程中，我们将描述使用WebFlux框架实现它们的可能方法。

由于我们不会详细介绍WebFlux框架本身，你可能需要查看[本文](https://www.baeldung.com/spring-5-functional-web)以了解更多详细信息。

## 2. Maven依赖

首先，让我们声明WebFlux Maven依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

## 3. 端点

我们必须先创建一些端点。每种方法一个：基于注解和基于函数式。

让我们从基于注解的控制器开始：

```java
@GetMapping(path = "/users/{name}")
public Mono<String> getName(@PathVariable String name) {
    return Mono.just(name);
}
```

对于函数式端点，我们必须首先创建一个处理程序：

```java
@Component
public class PlayerHandler {
    public Mono<ServerResponse> getName(ServerRequest request) {
        Mono<String> name = Mono.just(request.pathVariable("name"));
        return ok().body(name, String.class);
    }
}
```

还有一个路由器配置映射：

```java
@Bean
public RouterFunction<ServerResponse> route(PlayerHandler playerHandler) {
    return RouterFunctions
        .route(GET("/players/{name}"), playerHandler::getName)
        .filter(new ExampleHandlerFilterFunction());
}
```

## 4. WebFlux过滤器的类型

WebFlux框架提供两种类型的过滤器：[WebFilter](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/server/WebFilter.html)和[HandlerFilterFunctions](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/server/HandlerFilterFunction.html)。

它们之间的主要区别在于**WebFilter实现适用于所有端点，而HandlerFilterFunction实现仅适用于基于路由的端点**。 

### 4.1 WebFilter

我们将实现WebFilter以向响应添加新标头。因此，所有响应都应具有以下行为：

```java
@Component
public class ExampleWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        serverWebExchange.getResponse()
              .getHeaders().add("web-filter", "web-filter-test");
        return webFilterChain.filter(serverWebExchange);
    }
}
```

### 4.2 HandlerFilter函数

对于这个，我们实现了一个逻辑，当“name”参数等于“test”时，将HTTP状态设置为FORBIDDEN。

```java
public class ExampleHandlerFilterFunction
      implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    @Override
    public Mono<ServerResponse> filter(ServerRequest serverRequest,
                                       HandlerFunction<ServerResponse> handlerFunction) {
        if (serverRequest.pathVariable("name").equalsIgnoreCase("test")) {
            return ServerResponse.status(FORBIDDEN).build();
        }
        return handlerFunction.handle(serverRequest);
    }
}
```

## 5. 测试

在WebFlux框架中，有一种简单的方法可以测试我们的过滤器：WebTestClient。它允许我们测试对端点的HTTP调用。

以下是基于注解的端点的示例：

```java
@Test
public void whenUserNameIsTuyucheng_thenWebFilterIsApplied() {
    EntityExchangeResult<String> result = webTestClient.get()
        .uri("/users/baeldung")
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .returnResult();

    assertEquals(result.getResponseBody(), "tuyucheng");
    assertEquals(result.getResponseHeaders().getFirst("web-filter"), "web-filter-test");
}

@Test
public void whenUserNameIsTest_thenHandlerFilterFunctionIsNotApplied() {
    webTestClient.get().uri("/users/test")
        .exchange()
        .expectStatus().isOk();
}
```

对于函数式端点：

```java
@Test
public void whenPlayerNameIsTuyucheng_thenWebFilterIsApplied() {
    EntityExchangeResult<String> result = webTestClient.get()
        .uri("/players/baeldung")
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .returnResult();

    assertEquals(result.getResponseBody(), "tuyucheng");
    assertEquals(result.getResponseHeaders().getFirst("web-filter"), "web-filter-test");
} 

@Test 
public void whenPlayerNameIsTest_thenHandlerFilterFunctionIsApplied() {
    webTestClient.get().uri("/players/test")
        .exchange()
        .expectStatus().isForbidden(); 
}
```

## 6. 总结

我们在本教程中介绍了这两种类型的WebFlux过滤器，并查看了一些代码示例。

有关WebFlux框架的更多信息，请查看[文档](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html)。