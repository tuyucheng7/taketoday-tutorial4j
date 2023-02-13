## 1. 概述

在本教程中，我们将了解OncePerRequestFilter，它是Spring中的一种特殊类型的过滤器。我们将通过一个快速示例了解它解决了什么问题，并了解如何使用它。

## 2. 什么是OncePerRequestFilter？

让我们首先了解过滤器的工作原理。可以在Servlet执行之前或之后调用[过滤器](https://www.baeldung.com/spring-boot-add-filter)。**当请求被分派给一个Servlet时，RequestDispatcher可以将它转发到另一个Servlet。另一个Servlet也有可能具有相同的过滤器。在这种情况下，同一个过滤器会被多次调用**。

但是，我们可能希望确保每个请求只调用一次特定的过滤器。一个常见的用例是使用Spring Security时。当请求通过过滤器链时，我们可能希望某些身份验证操作只针对请求发生一次。

在这种情况下，我们可以扩展OncePerRequestFilter。**Spring保证OncePerRequestFilter对于给定的请求只执行一次**。

## 3. 对同步请求使用OncePerRequestFilter

让我们举个例子来了解如何使用这个过滤器。我们将定义一个扩展OncePerRequestFilter的AuthenticationFilter类，并覆盖doFilterInternal()方法：

```java
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {
        String usrName = request.getHeader("userName");
        logger.info("Successfully authenticated user  " + usrName);
        filterChain.doFilter(request, response);
    }
}
```

由于OncePerRequestFilter仅支持HTTP请求，因此无需像在实现Filter接口时那样强制转换请求和响应对象。

## 4. 对异步请求使用OncePerRequestFilter

对于异步请求，默认情况下不会应用OncePerRequestFilter。我们需要覆盖shouldNotFilterAsyncDispatch()和shouldNotFilterErrorDispatch()方法来支持这一点。

有时，我们只需要在初始请求线程中应用过滤器，而不是在异步调度中创建的其他线程中应用过滤器。其他时候，我们可能需要在每个附加线程中至少调用一次过滤器。在这种情况下，我们需要覆盖shouldNotFilterAsyncDispatch()方法。

如果shouldNotFilterAsyncDispatch()方法返回true，则不会为后续的异步调度调用过滤器。但是，如果它返回false，则将为每个异步调度调用过滤器，每个线程恰好一次。

类似地，**我们将覆盖shouldNotFilterErrorDispatch()方法并返回true或false，具体取决于我们是否要过滤错误调度**：

```java
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {
        String usrName = request.getHeader("userName");
        logger.info("Successfully authenticated user  " + usrName);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }
}
```

## 5. 有条件地跳过请求

通过重写shouldNotFilter()方法，我们可以有条件地仅对某些特定请求应用过滤器并跳过其他请求：

```java
@Override
protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    return Boolean.TRUE.equals(request.getAttribute(SHOULD_NOT_FILTER));
}
```

## 6. 快速示例

让我们看一个简单的示例来理解OncePerRequestFilter的行为。首先，我们将定义一个使用Spring的[DeferredResult](https://www.baeldung.com/spring-deferred-result)异步处理请求的控制器：

```java
@Controller
public class HelloController implements AutoCloseable {

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private Logger logger = LoggerFactory.getLogger(HelloController.class);

    @GetMapping(path = "/greeting")
    public DeferredResult<String> hello(HttpServletResponse response) throws Exception {
        DeferredResult<String> deferredResult = new DeferredResult<>();
        executorService.submit(() -> perform(deferredResult));
        return deferredResult;
    }

    private void perform(DeferredResult<String> dr) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dr.setResult("OK");
    }

    @Override
    public void close() throws Exception {
        executorService.shutdownNow();
    }
}
```

当异步处理请求时，两个线程都经过同一个过滤器链。因此，过滤器被调用两次：第一次是在容器线程处理请求时，第二次是在异步调度程序完成之后。异步处理完成后，将响应返回给客户端。

现在，让我们定义一个实现OncePerRequestFilter的过滤器：

```java
@Component
public class MyOncePerRequestFilter extends OncePerRequestFilter {
    private final Logger logger = LoggerFactory.getLogger(MyOncePerRequestFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {
        logger.info("Inside Once Per Request Filter originated by request {}", request.getRequestURI());
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return true;
    }
}
```

在上面的代码中，我们有意从shouldNotFilterAsyncDispatch()方法返回true。这是为了证明我们的过滤器只为容器线程调用一次，而不是为后续的异步线程调用一次。

让我们调用我们的端点来证明这一点：

```shell
curl -X GET http://localhost:8080/greeting 
```

**输出**：

```shell
10:23:24.175 [http-nio-8082-exec-1] INFO  o.a.c.c.C.[Tomcat].[localhost].[/] - Initializing Spring DispatcherServlet 'dispatcherServlet'
10:23:24.175 [http-nio-8082-exec-1] INFO  o.s.web.servlet.DispatcherServlet - Initializing Servlet 'dispatcherServlet'
10:23:24.176 [http-nio-8082-exec-1] INFO  o.s.web.servlet.DispatcherServlet - Completed initialization in 1 ms
10:23:26.814 [http-nio-8082-exec-1] INFO  c.t.t.o.MyOncePerRequestFilter - Inside OncePer Request Filter originated by request /greeting
```

现在，让我们看看我们希望请求和异步分派都调用我们的过滤器的情况。我们只需要覆盖shouldNotFilterAsyncDispatch()返回false来实现这个：

```java
@Override
protected boolean shouldNotFilterAsyncDispatch() {
    return false;
}
```

**输出**：

```shell
2:53.616 [http-nio-8082-exec-1] INFO  o.a.c.c.C.[Tomcat].[localhost].[/] - Initializing Spring DispatcherServlet 'dispatcherServlet'
10:32:53.616 [http-nio-8082-exec-1] INFO  o.s.web.servlet.DispatcherServlet - Initializing Servlet 'dispatcherServlet'
10:32:53.617 [http-nio-8082-exec-1] INFO  o.s.web.servlet.DispatcherServlet - Completed initialization in 1 ms
10:32:53.633 [http-nio-8082-exec-1] INFO  c.t.t.o.MyOncePerRequestFilter - Inside OncePer Request Filter originated by request /greeting
10:32:53.663 [http-nio-8082-exec-2] INFO  c.t.t.o.MyOncePerRequestFilter - Inside OncePer Request Filter originated by request /greeting
```

我们可以从上面的输出中看到我们的过滤器被调用了两次-第一次被容器线程调用，然后被另一个线程调用。

## 7. 总结

在本文中，我们研究了OncePerRequestFilter，它解决了哪些问题，以及如何通过一些实际示例来实现它。