## 1. 概述

过滤器的使用在 Web 应用程序中很普遍，因为它们为我们提供了一种无需更改端点即可修改请求或响应的方法。

在本快速教程中，我们将描述使用 WebFlux 框架实现它们的可能方法。

由于我们不会详细介绍 WebFlux 框架本身，您可能需要查看[本文](https://www.baeldung.com/spring-5-functional-web) 以了解更多详细信息。

## 2.Maven依赖

首先，让我们声明 WebFlux Maven 依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

## 3.端点

我们必须先创建一些端点。每种方法一个：基于注释和基于功能。

让我们从基于注释的控制器开始：

```java
@GetMapping(path = "/users/{name}")
public Mono<String> getName(@PathVariable String name) {
    return Mono.just(name);
}
```

对于功能端点，我们必须首先创建一个处理程序：

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

## 4. WebFlux 过滤器的类型

WebFlux 框架提供两种类型的过滤器[：](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/server/WebFilter.html)[WebFilter](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/server/HandlerFilterFunction.html)和[HandlerFilterFunctions](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/server/WebFilter.html)。

它们之间的主要区别在于WebFilter实现适用于所有端点，而HandlerFilterFunction 实现仅适用于基于Router的端点。 

### 4.1. 网络过滤器

我们将实施WebFilter以向响应添加新标头。因此，所有响应都应具有以下行为：

```java
@Component
public class ExampleWebFilter implements WebFilter {
 
    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, 
      WebFilterChain webFilterChain) {
        
        serverWebExchange.getResponse()
          .getHeaders().add("web-filter", "web-filter-test");
        return webFilterChain.filter(serverWebExchange);
    }
}
```

### 4.2. HandlerFilter函数

对于这个，我们实现了一个逻辑，当“name”参数等于“test”时，将 HTTP 状态设置为FORBIDDEN 。

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

在 WebFlux Framework 中，有一种简单的方法可以测试我们的过滤器：WebTestClient。它允许我们测试对端点的 HTTP 调用。

以下是基于注释的端点的示例：

```java
@Test
public void whenUserNameIsBaeldung_thenWebFilterIsApplied() {
    EntityExchangeResult<String> result = webTestClient.get()
      .uri("/users/baeldung")
      .exchange()
      .expectStatus().isOk()
      .expectBody(String.class)
      .returnResult();

    assertEquals(result.getResponseBody(), "baeldung");
    assertEquals(
      result.getResponseHeaders().getFirst("web-filter"), 
      "web-filter-test");
}

@Test
public void whenUserNameIsTest_thenHandlerFilterFunctionIsNotApplied() {
    webTestClient.get().uri("/users/test")
      .exchange()
      .expectStatus().isOk();
}
```

对于功能端点：

```java
@Test
public void whenPlayerNameIsBaeldung_thenWebFilterIsApplied() {
    EntityExchangeResult<String> result = webTestClient.get()
      .uri("/players/baeldung")
      .exchange()
      .expectStatus().isOk()
      .expectBody(String.class)
      .returnResult();

    assertEquals(result.getResponseBody(), "baeldung");
    assertEquals(
      result.getResponseHeaders().getFirst("web-filter"),
      "web-filter-test");
} 

@Test 
public void whenPlayerNameIsTest_thenHandlerFilterFunctionIsApplied() {
    webTestClient.get().uri("/players/test")
      .exchange()
      .expectStatus().isForbidden(); 
}
```

## 6. 总结

我们在本教程中介绍了这两种类型的 WebFlux 过滤器，并查看了一些代码示例。

有关 WebFlux Framework 的更多信息，请查看[文档](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html)。