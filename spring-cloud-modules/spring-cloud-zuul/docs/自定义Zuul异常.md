## 一、概述

[Zuul](https://www.baeldung.com/spring-rest-with-zuul-proxy)是 Netflix 的基于 JVM 的路由器和服务器端负载均衡器。Zuul 的规则引擎提供了编写规则和过滤器的灵活性，以增强 Spring Cloud 微服务架构中的路由。

在本文中，我们将探讨如何通过编写在代码执行期间发生错误时运行的自定义错误过滤器来自定义 Zuul 中的异常和错误响应。

## 2. Zuul异常

Zuul 中所有处理的异常都是 ZuulExceptions。现在，让我们明确ZuulException 不能被 [@ControllerAdvice捕获并通过](https://www.baeldung.com/exception-handling-for-rest-with-spring#controlleradvice)[@ExceptionHandling](https://www.baeldung.com/exception-handling-for-rest-with-spring#exceptionhandler)注释该方法 。这是因为 ZuulException是从错误过滤器中抛出的。因此，它会跳过后续的过滤器链，永远不会到达错误控制器。下图描绘了 Zuul 中错误处理的层次结构：

 

[![自定义 Zuul 异常](https://www.baeldung.com/wp-content/uploads/2022/02/zuul.png)](https://www.baeldung.com/wp-content/uploads/2022/02/zuul.png)

 

Zuul 在出现ZuulException时显示以下错误响应：

```json
{
    "timestamp": "2022-01-23T22:43:43.126+00:00",
    "status": 500,
    "error": "Internal Server Error"
}
```

在某些场景下，我们可能需要在ZuulException 的响应中自定义错误消息或状态代码。 Zuul 过滤器来拯救。在下一节中，我们将讨论如何扩展 Zuul 的错误过滤器和自定义 ZuulException。

## 3.自定义Zuul异常

spring-cloud-starter-netflix-zuul的入门包包括三种类型的过滤器：[pre](https://www.baeldung.com/spring-rest-with-zuul-proxy#filter)、[post](https://www.baeldung.com/zuul-filter-modifying-response-body)和错误过滤器。在这里，我们将深入研究错误过滤器并探索名为SendErrorFilter的 Zuul 错误过滤器的自定义。

首先，我们将禁用自动配置的默认SendErrorFilter。这使我们不必担心执行顺序，因为这是唯一的 Zuul 默认错误过滤器。让我们在application.yml中添加属性来禁用它：

```yaml
zuul:
  SendErrorFilter:
    post:
      disable: true
```

现在，让我们编写一个名为CustomZuulErrorFilter的自定义 Zuul 错误过滤器，如果底层服务不可用，它会抛出一个自定义异常：

```java
public class CustomZuulErrorFilter extends ZuulFilter {
}
```

这个自定义过滤器需要扩展 com.netflix.zuul.ZuulFilter并覆盖它的一些方法。

首先，我们必须覆盖filterType()方法并将类型作为“错误”返回。这是因为我们要为错误过滤器类型配置 Zuul 过滤器：

```java
@Override
public String filterType() {
    return "error";
}
```

之后，我们覆盖filterOrder() 并返回 -1，这样过滤器就是链中的第一个：

```java
@Override
public int filterOrder() {
    return -1;
}
```

然后，我们覆盖shouldFilter() 方法并无条件返回true，因为我们希望在所有情况下都链接此过滤器：

```java
@Override
public boolean shouldFilter() {
    return true;
}
```

最后，让我们覆盖run()方法：

```java
@Override
public Object run() {
    RequestContext context = RequestContext.getCurrentContext();
    Throwable throwable = context.getThrowable();

    if (throwable instanceof ZuulException) {
        ZuulException zuulException = (ZuulException) throwable;
        if (throwable.getCause().getCause().getCause() instanceof ConnectException) {
            context.remove("throwable");
            context.setResponseBody(RESPONSE_BODY);
            context.getResponse()
                .setContentType("application/json");
            context.setResponseStatusCode(503);
        }
    }
    return null;
}
```

让我们分解这个run()方法来了解它在做什么。首先，我们获取RequestContext的实例。接下来，我们验证从 RequestContext 获取的 throwable是否是ZuulException的实例 。然后，我们检查throwable中嵌套异常的原因是否是ConnectException的实例 。最后，我们使用响应的自定义属性设置了上下文。

请注意，在设置自定义响应之前，我们从上下文中清除可抛出的对象，以防止在后续过滤器中进行进一步的错误处理。

此外，我们还可以在我们的run()方法中设置一个自定义异常，该异常可以由后续过滤器处理：

```java
if (throwable.getCause().getCause().getCause() instanceof ConnectException) {
    ZuulException customException = new ZuulException("", 503, "Service Unavailable");
    context.setThrowable(customException);
}
```

上面的代码片段将记录堆栈跟踪并继续进行下一个过滤器。

此外，我们可以修改此示例以处理ZuulFilter 内部的多个异常。

## 4. 测试自定义 Zuul 异常

在本节中，我们将在CustomZuulErrorFilter中测试自定义 Zuul 异常。

假设存在ConnectException，上述示例在 Zuul API 的响应中的输出将是：

```json
{
    "timestamp": "2022-01-23T23:10:25.584791Z",
    "status": 503,
    "error": "Service Unavailable"
}
```

此外，我们始终可以通过在 application.yml文件中配置 error.path 属性来更改默认的 Zuul 错误转发路径/error 。

现在，让我们通过一些测试用例来验证它：

```java
@Test
public void whenSendRequestWithCustomErrorFilter_thenCustomError() {
    Response response = RestAssured.get("http://localhost:8080/foos/1");
    assertEquals(503, response.getStatusCode());
}
```

在上面的测试场景中， /foos/1的路由被故意压低，导致java.lang。连接异常。因此，我们的自定义过滤器将拦截并以 503 状态响应。

现在，让我们在不注册自定义错误过滤器的情况下进行测试：

```java
@Test
public void whenSendRequestWithoutCustomErrorFilter_thenError() {
    Response response = RestAssured.get("http://localhost:8080/foos/1");
    assertEquals(500, response.getStatusCode());
}
```

在未注册自定义错误过滤器的情况下执行上述测试用例会导致 Zuul 响应状态 500。

## 5.结论

在本教程中，我们了解了错误处理的层次结构，并深入研究了在 Spring Zuul 应用程序中配置自定义 Zuul 错误过滤器。此错误过滤器提供了自定义响应正文和响应代码的机会。