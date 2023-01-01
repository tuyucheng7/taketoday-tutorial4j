##  1. 概述

在本文中，我们将比较Java Servlet Filter和Spring MVC [HandlerInterceptor](https://www.baeldung.com/spring-mvc-handlerinterceptor)，以及何时可能比另一个更好。

## 2. Filters

**过滤器是Web服务器的一部分，而不是Spring框架**。对于传入的请求，**我们可以使用过滤器来操纵甚至阻止请求到达任何**[Servlet](https://www.baeldung.com/java-servlets-containers-intro)，反之亦然，我们也可以阻止响应到达客户端。

[Spring Security](https://www.baeldung.com/security-spring)是使用过滤器进行身份验证和授权的一个很好的例子，要配置Spring Security，我们只需添加一个过滤器，[DelegatingFilterProxy](https://www.baeldung.com/spring-delegating-filter-proxy)，然后Spring Security可以拦截所有传入和传出的流量，这就是为什么可以在[Spring MVC](https://www.baeldung.com/spring-mvc)之外使用Spring Security的原因。

### 2.1 创建过滤器

要创建过滤器，首先，我们创建一个实现[javax.servlet.Filter](https://docs.oracle.com/javaee/7/api/javax/servlet/Filter.html)接口的类：

```java
@Component
public class LogFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.info("Hello from: " + request.getLocalAddr());
        chain.doFilter(request, response);
    }
}
```

接下来，我们覆盖doFilter方法，我们可以在其中访问或操作ServletRequest、ServletResponse或FilterChain对象，我们可以使用FilterChain对象允许或阻止请求。

最后，我们通过使用@Component注解将Filter添加到Spring上下文中，Spring会完成剩下的工作。

## 3. HandlerInterceptors

[HandlerInterceptor](https://www.baeldung.com/spring-mvc-handlerinterceptor)**是Spring MVC框架的一部分，位于**[DispatcherServlet](https://www.baeldung.com/spring-mvc-handlerinterceptor)**和我们的Controller之间**，我们可以在请求到达我们的控制器之前，以及呈现视图之前和之后拦截请求。

### 3.1 创建一个处理程序拦截器

要创建HandlerInterceptor，我们创建一个实现[org.springframework.web.servlet.HandlerInterceptor](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/HandlerInterceptor.html)接口的类，这使我们可以选择覆盖三种方法：

-   preHandle()：在调用目标处理程序之前执行
-   postHandle()：在目标处理程序之后但在DispatcherServlet呈现视图之前执行
-   afterCompletion()：请求处理和视图渲染完成后的回调

让我们将日志记录添加到我们的测试拦截器中的三个方法：

```java
public class LogInterceptor implements HandlerInterceptor {

    private Logger logger = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
          throws Exception {
        logger.info("preHandle");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
          throws Exception {
        logger.info("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
          throws Exception {
        logger.info("afterCompletion");
    }
}
```

## 4. 主要差异和用例

让我们看一下显示Filter和HandlerInterceptor在请求/响应流中的位置的图片：

<img src="../assets/img_2.png">

**过滤器在请求到达DispatcherServlet之前拦截请求，使其成为粗粒度任务的理想选择**，例如：

-   验证
-   日志记录和审计
-   图像和数据压缩
-   我们希望与Spring MVC分离的任何功能

**另一方面，HandlerInterceptor拦截DispatcherServlet和我们的Controller之间的请求**，这是在Spring MVC框架内完成的，提供对Handler和ModelAndView对象的访问。这减少了重复并允许更细粒度的功能，例如：

-   处理横切关注点，例如应用程序日志记录
-   详细的授权检查
-   操纵Spring上下文或模型

## 5. 总结

在本文中，我们介绍了Filter和HandlerInterceptor之间的区别。

**关键要点是，使用Filters，我们可以在请求到达我们的控制器之前以及在Spring MVC之外对其进行操作**。否则，HandlerInterceptor是处理特定于应用程序的横切关注点的好地方。通过提供对目标Handler和ModelAndView对象的访问，我们可以进行更细粒度的控制。