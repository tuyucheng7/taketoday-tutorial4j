## 1. 简介

在本教程中，我们将探索Spring MVC中的@Async注解，然后我们将熟悉Spring WebFlux，我们的目标是更好地理解这两者之间的区别。

## 2. 实现场景

在这里，我们想选择一个场景来展示我们如何使用这些API中的每一个来实现一个简单的Web应用程序。此外，我们特别有兴趣在每种情况下看到更多关于线程管理和阻塞或非阻塞I/O的信息。

让我们选择一个具有一个返回字符串结果的端点的Web应用程序，这里的重点是请求会经过一个200ms小延迟的Filter，然后Controller需要500ms计算并返回结果。

接下来，我们将在两个端点上使用[Apache ab](https://httpd.apache.org/docs/2.4/programs/ab.html)模拟负载，并使用[JConsole](https://docs.oracle.com/en/java/javase/11/management/using-jconsole.html)监控我们的应用程序行为。

可能值得一提的是，在本文中，**我们的目标不是这两个API之间的基准测试，只是一个小的负载测试，以便我们可以跟踪线程管理**。

## 3. Spring MVC异步

Spring 3.0引入了@Async注解，@Async的目标是允许应用程序在单独的线程上运行重负载作业。此外，如果有兴趣，调用者可以等待结果。因此，返回类型不能为void，它可以是[Future](https://www.baeldung.com/java-future)、[CompletableFuture](https://www.baeldung.com/java-completablefuture)或ListenableFuture中的任何一个。

此外，[Spring 3.2](https://docs.spring.io/spring/docs/3.2.x/spring-framework-reference/html/new-in-3.2.html)引入了org.springframework.web.context.request.async包，与[Servlet 3.0](https://download.oracle.com/otndocs/jcp/servlet-3.0-fr-oth-JSpec/)一起，将异步过程的乐趣带到了web层。因此，从Spring 3.2开始，@Async可以在注解为@Controller或@RestController的类中使用。

当客户端发起请求时，它会遍历过滤器链中所有匹配的过滤器，直到到达DispatcherServlet实例。

然后，Servlet负责请求的异步分派，它通过调用AsyncWebRequest#startAsync将请求标记为已启动，将请求处理转移到WebSyncManager的实例，并在不提交响应的情况下完成其工作，过滤器链也以相反的方向遍历到根。

WebAsyncManager在其关联的ExecutorService中提交请求处理作业，当结果准备就绪时，它会通知DispatcherServlet将响应返回给客户端。

## 4. Spring异步实现

让我们通过编写我们的应用程序类AsyncVsWebFluxApp来开始实施。在这里，@EnableAsync为我们的Spring Boot应用程序实现了启用异步的魔力：

```java
@SpringBootApplication
@EnableAsync
public class AsyncVsWebFluxApp {
    public static void main(String[] args) {
        SpringApplication.run(AsyncVsWebFluxApp.class, args);
    }
}
```

然后我们有AsyncFilter，它实现了javax.servlet.Filter，不要忘记在doFilter方法中模拟延迟：

```java
@Component
public class AsyncFilter implements Filter {
    // ...
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // sleep for 200ms 
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
```

最后，我们使用“/async_result”端点开发我们的AsyncController ：

```java
@RestController
public class AsyncController {
    @GetMapping("/async_result")
    @Async
    public CompletableFuture getResultAsyc(HttpServletRequest request) {
        // sleep for 500 ms
        return CompletableFuture.completedFuture("Result is ready!");
    }
}
```

由于getResultAsync上面的@Async，此方法在应用程序的默认[ExecutorService](https://www.baeldung.com/java-executor-service-tutorial)上的单独线程中执行。但是，可以[为我们的方法设置一个特定的ExecutorService](https://www.baeldung.com/spring-async)。

测试时间！让我们运行应用程序，安装Apache ab或任何工具来模拟负载，然后我们可以通过“async_result”端点发送一堆并发请求，我们可以执行JConsole并将其附加到我们的java应用程序以监控进程：

```bash
ab -n 1600 -c 40 localhost:8080/async_result
```

<img src="../assets/img.png">

## 5. Spring WebFlux

Spring 5.0引入了[WebFlux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html)以非阻塞方式支持响应式Web，WebFlux基于Reactor API，它只是[响应流](https://www.reactive-streams.org/)的另一个出色实现。

**Spring WebFlux通过其非阻塞I/O支持响应式背压和**[Servlet 3.1+](https://blogs.oracle.com/arungupta/whats-new-in-servlet-31-java-ee-7-moving-forward)**，因此，它可以在Netty、Undertow、Jetty、Tomcat或任何Servlet 3.1+兼容服务器上运行**。

尽管所有服务器不使用相同的线程管理和并发控制模型，但只要支持非阻塞I/O和响应式背压，Spring WebFlux就可以正常工作。

Spring WebFlux允许我们使用Mono、Flux及其丰富的运算符集以声明方式分解逻辑。此外，除了@Controller注解的端点之外，我们还可以拥有函数式端点，尽管我们现在也可以在[Spring MVC](../../spring-boot-mvc-2/docs/SpringMVC中的函数式控制器.md)中使用这些端点。

## 6. Spring WebFlux实现

对于[WebFlux](https://www.baeldung.com/spring-webflux)实现，我们走与异步相同的路径。所以首先，让我们创建AsyncVsWebFluxApp：

```java
@SpringBootApplication
public class AsyncVsWebFluxApp {
    public static void main(String[] args) {
        SpringApplication.run(AsyncVsWebFluxApp.class, args);
    }
}
```

然后让我们编写实现WebFilter的WebFluxFilter，我们将产生一个有意的延迟，然后将请求传递给过滤器链：

```java
@Component
public class WebFluxFilter implements org.springframework.web.server.WebFilter {

    @Override
    public Mono filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        return Mono
              .delay(Duration.ofMillis(200))
              .then(
                    webFilterChain.filter(serverWebExchange)
              );
    }
}
```

最后，我们有了WebFluxController，它公开了一个名为“/flux_result”的端点并返回一个Mono<String\>作为响应：

```java
@RestController
public class WebFluxController {

    @GetMapping("/flux_result")
    public Mono getResult(ServerHttpRequest request) {
        return Mono.defer(() -> Mono.just("Result is ready!"))
              .delaySubscription(Duration.ofMillis(500));
    }
}
```

对于测试，我们采用与我们的异步示例应用程序相同的方法。以下是示例结果：


```bash
ab -n 1600 -c 40 localhost:8080/flux_result
```

<img src="../assets/img_1.png">

## 7. 有什么区别？

Spring Async支持Servlet 3.0规范，但Spring WebFlux支持Servlet 3.1+，它带来了一些差异：

-   Spring Async I/O模型在与客户端通信期间是阻塞的，它可能会导致慢速客户端出现性能问题。另一方面，Spring WebFlux提供了一个非阻塞I/O模型。
-   读取请求主体或请求部分在Spring Async中是阻塞的，而在Spring WebFlux中是非阻塞的。
-   在Spring Async中，Filter和Servlet是同步工作的，但是Spring WebFlux支持完全异步通信。
-   与Spring Async相比，Spring WebFlux与更广泛的Web/应用程序服务器兼容，例如Netty和Undertow。

此外，Spring WebFlux支持响应式背压，因此与Spring MVC Async和Spring MVC相比，我们可以更好地控制我们应该如何对快速生产者做出反应。

得益于其背后的Reactor API，Spring Flux还向函数式编码风格和声明式API分解有了明显的转变。

是否所有这些项目都引导我们使用Spring WebFlux？好吧，**Spring Async甚至Spring MVC可能是许多项目的正确答案，具体取决于系统所需的负载可伸缩性或可用性**。

关于可伸缩性，Spring Async比同步Spring MVC实现给了我们更好的结果。Spring WebFlux，由于其响应性，为我们提供了弹性和更高的可用性。

## 8. 总结

在本文中，我们进一步了解了Spring Async和Spring WebFlux，然后我们通过基本负载测试从理论上和实践上对它们进行了比较。