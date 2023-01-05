## 1. 概述

在本教程中，我们将探索使用[Spring 5的WebFlux框架](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)为非响应式端点或API设置服务响应头的不同方法。

我们可以在[以前的帖子](https://www.baeldung.com/spring-5)中找到有关此框架的更多信息。

## 2. 非响应式组件设置header

如果我们想在单个响应上设置header，我们可以使用HttpServletResponse或ResponseEntity对象。

相反，如果我们的目标是为所有或多个响应添加header，则需要配置一个过滤器。

### 2.1 使用HttpServletResponse

我们只需将HttpServletResponse对象作为参数添加到我们的REST端点，然后使用addHeader()方法：

```java
@RestController
@RequestMapping("/response-header")
public class ResponseHeaderController {

    @GetMapping("/http-servlet-response")
    public String usingHttpServletResponse(HttpServletResponse response) {
        response.addHeader("Tuyucheng-Example-Header", "Value-HttpServletResponse");
        return "Response with header using HttpServletResponse";
    }
}
```

如上面所示，我们不必返回响应对象。

### 2.2 使用ResponseEntity

在这种情况下，我们将使用ResponseEntity类提供的BodyBuilder：

```java
@GetMapping("/response-entity-builder-with-http-headers")
public ResponseEntity<String> usingResponseEntityBuilderAndHttpHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Tuyucheng-Example-Header", "Value-ResponseEntityBuilderWithHttpHeaders");
    
    return ResponseEntity.ok().headers(headers).body("Response with header using ResponseEntity");
}
```

**HttpHeaders类提供了许多方便的方法来设置最常见的header**。

### 2.3 为所有响应添加header

假设我们想要为我们的许多端点设置一个特定的header。

当然，如果我们必须在每个映射方法上复制以前的代码，那将是令人沮丧的。

实现这一点的方法是在我们的服务中配置过滤器：

```java
@WebFilter("/response-header/*")
public class AddResponseHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Tuyucheng-Example-Filter-Header", "Value-Filter");
        chain.doFilter(request, response);
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // ...
    }

    @Override
    public void destroy() {
        // ...
    }
}
```

@WebFilter注解允许我们指示此过滤器对哪些urlPatterns生效。

正如我们在[本文](https://www.baeldung.com/spring-servletcomponentscan)中指出的，为了使我们的Filter可以被Spring发现，我们需要将@ServletComponentScan注解添加到我们的Spring Boot主类上：

```java
@ServletComponentScan
@SpringBootApplication
public class ResponseHeadersApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResponseHeadersApplication.class, args);
    }
}
```

如果我们不需要@WebFilter提供的任何功能，我们可以通过在Filter类中使用@Component注解来避免这最后一步。

## 3. 响应式组件设置header

在本节中，我们将学习如何使用ServerHttpResponse、ResponseEntity或ServerResponse(用于函数式端点)类和接口在单个端点响应上设置header。

我们还将讨论如何实现Spring 5 WebFilter以在我们所有的响应中添加header。

### 3.1 使用ServerHttpResponse

这种方法与HttpServletResponse对应的方法非常相似：

```java
@GetMapping("/server-http-response")
public Mono<ResponseEntity<String>> usingServerHttpResponse() {
    String responseHeaderKey = "Tuyucheng-Example-Header";
    String responseHeaderValue = "Value-ResponseEntityBuilder";
    String responseBody = "Response with header using ResponseEntity (builder)";

    return Mono.just(ResponseEntity.ok()
            .header(responseHeaderKey, responseHeaderValue)
            .body(responseBody));
}
```

### 3.2 使用ResponseEntity

我们可以使用ResponseEntity类，就像我们对非响应式端点所做的那样：

```java
@GetMapping("/response-entity")
public Mono<ResponseEntity<String>> usingResponseEntityBuilder() {
    String responseHeaderKey = "Tuyucheng-Example-Header";
    String responseHeaderValue = "Value-ResponseEntityBuilder";
    String responseBody = "Response with header using ResponseEntity (builder)";

    return Mono.just(ResponseEntity.ok().header(responseHeaderKey, responseHeaderValue).body(responseBody));
}
```

### 3.3 使用ServerResponse

上两小节介绍的类和接口可以在带有@Controller注解的类中使用，但不适用于新的[Spring 5中的函数式端点](../../reactive-spring-4/docs/Spring_FunctionWeb.md)。

如果我们想在HandlerFunction上设置一个header，那么我们需要使用ServerResponse接口：

```java
public Mono<ServerResponse> useHandler(final ServerRequest request) {
    String responseHeaderKey = "Tuyucheng-Example-Header";
    String responseHeaderValue = "Value-Handler";
    String responseBody = "Response with header using Handler";

    return ServerResponse.ok()
            .header(responseHeaderKey, responseHeaderValue)
            .body(Mono.just(responseBody), String.class);
}
```

### 3.4 为所有响应添加header

最后，Spring 5提供了一个WebFilter接口来为服务检索到的所有响应设置一个header：

```java
@Component
public class AddResponseHeaderWebFilter implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		exchange.getResponse()
			.getHeaders()
			.add("Tuyucheng-Example-Filter-Header", "Value-Filter");
		return chain.filter(exchange);
	}
}
```

## 4. 总结

在本文中，我们介绍了多种在响应中设置header的不同方法。