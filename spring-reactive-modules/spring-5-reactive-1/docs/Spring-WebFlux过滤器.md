## 1. 概述

过滤器的使用在Web应用程序中很普遍，因为它们为我们提供了一种在不更改端点的情况下修改请求或响应的方法。

在这个教程中，我们将描述使用WebFlux框架实现它们的可能方式。

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

我们必须先创建一些端点，包含基于注解的和基于函数风格的。

让我们从基于注解的控制器开始：

```java
@GetMapping("/users/{name}")
public Mono<String> getName(@PathVariable String name) {
    return Mono.just(name);
}
```

对于函数式端点，我们必须首先创建一个处理器：

```java
@Component
public class PlayerHandler {

    Mono<ServerResponse> getName(ServerRequest request) {
        Mono<String> name = Mono.just(request.pathVariable("name"));
        return ok().body(name, String.class);
    }
}
```

然后创建一个路由配置映射：

```java
@Configuration
public class PlayerRouter {

    @Bean
    public RouterFunction<ServerResponse> route(PlayerHandler playerHandler) {
        return RouterFunctions.route(GET("/players/{name}"), playerHandler::getName).filter(new ExampleHandlerFilterFunction());
    }
}
```

## 4. WebFlux过滤器的类型

WebFlux框架提供两种类型的过滤器：[WebFilter](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/server/WebFilter.html)和[HandlerFilterFunctions](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/server/HandlerFilterFunction.html)。

它们之间的主要区别在于WebFilter实现适用于所有端点，而HandlerFilterFunction实现仅适用于基于路由的端点。

### 4.1 WebFilter

我们将实现一个WebFilter来为响应添加一个新的header。因此，所有端点响应都应具有以下行为：

```java
@Component
public class ExampleWebFilter implements WebFilter {

    @Override
    public @NotNull Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        exchange.getResponse().getHeaders().add("web-filter", "web-filter-test");
        return chain.filter(exchange);
    }
}
```

### 4.2 HandlerFilterFunction

对于这个类型的过滤器，我们实现一个逻辑，当“name”参数等于“test”时，将HTTP状态码设置为FORBIDDEN。

```java
public class ExampleHandlerFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> handlerFunction) {
        if (request.pathVariable("name").equalsIgnoreCase("test"))
            return ServerResponse.status(HttpStatus.FORBIDDEN).build();
        return handlerFunction.handle(request);
    }
}
```

## 5. 测试

在WebFlux框架中，有一种简单的方法来测试我们的过滤器：WebTestClient。它允许我们测试对端点的HTTP调用。

以下是基于注解的端点的示例：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser
class UserControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenUserNameIsTuyucheng_thenWebFilterIsApplied() {
        EntityExchangeResult<String> result = webTestClient.get().uri("/users/tuyucheng")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        assertEquals(result.getResponseBody(), "tuyucheng");
        assertEquals(result.getResponseHeaders().getFirst("web-filter"), "web-filter-test");
    }

    @Test
    void whenUserNameIsTest_thenHandlerFilterFunctionIsNotApplied() {
        webTestClient.get().uri("/users/test")
                .exchange()
                .expectStatus().isOk();
    }
}
```

以及基于函数式端点的测试：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser
@AutoConfigureWebTestClient(timeout = "10000")
class PlayerHandlerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenPlayerNameIsTuyucheng_thenWebFilterIsApplied() {
        EntityExchangeResult<String> result = webTestClient.get()
                .uri("/players/tuyucheng")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        assertEquals(result.getResponseBody(), "tuyucheng");
        assertEquals(result.getResponseHeaders().getFirst("web-filter"), "web-filter-test");
    }

    @Test
    void whenPlayerNameIsTest_thenHandlerFilterFunctionIsApplied() {
        webTestClient.get()
                .uri("/players/test")
                .exchange()
                .expectStatus()
                .isForbidden();
    }
}
```

注意，在上面的测试类中，我们的第二个测试期望的响应码为FORBIDDEN，这是ExampleHandlerFilterFunction的作用结果。

## 6. 总结

在本教程中，我们介绍了两种类型的WebFlux过滤器，并通过实际代码演示了它们的用法。

有关WebFlux框架的更多信息，请查看[文档](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html)。