## 1. 简介

在本教程中，我们将重点关注 Spring MVC HandlerInterceptor。

更具体地说，我们将展示使用拦截器的更高级用例——我们将通过设置自定义计数器和手动跟踪会话来模拟会话超时逻辑。

如果想了解 Spring 中HandlerInterceptor 的基础知识，请查看这篇[文章](https://www.baeldung.com/spring-mvc-handlerinterceptor)。

## 2.Maven依赖

为了使用拦截器，需要在pom.xml文件的依赖项部分中包含以下部分：

```java
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>5.3.13</version>
</dependency>Copy
```

最新版本可以在[这里](https://search.maven.org/classic/#search|ga|1|a%3A"spring-web")找到。此依赖项仅涵盖 Spring Web，因此不要忘记为完整(最小)Web 应用程序添加 s pring-core和spring-context。

## 3. 会话超时的自定义实现

在此示例中，我们将为系统中的用户配置最长不活动时间。在那之后，他们将自动从应用程序中注销。

这个逻辑只是一个概念证明——我们当然可以使用会话超时轻松实现相同的结果——但结果不是这里的重点，拦截器的使用才是。

因此，我们要确保如果用户不活跃，会话将失效。例如，如果用户忘记注销，非活动时间计数器将阻止未经授权的用户访问该帐户。为此，我们需要为非活动时间设置常量：

```java
private static final long MAX_INACTIVE_SESSION_TIME = 5  10000;
```

出于测试目的，我们将其设置为 50 秒；别忘了，它是以毫秒计算的。

现在，我们需要跟踪应用程序中的每个会话，因此我们需要包含这个 Spring 接口：

```java
@Autowired
private HttpSession session;
```

让我们继续使用preHandle()方法。

### 3.1。预句柄()

在此方法中，我们将包括以下操作：

-   设置计时器以检查请求的处理时间
-   检查用户是否登录(使用[本文中的](https://www.baeldung.com/spring-model-parameters-with-handler-interceptor)UserInterceptor方法)
-   自动注销，如果用户的非活动会话时间超过最大允许值

让我们看一下实现：

```java
@Override
public boolean preHandle(
  HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
    log.info("Pre handle method - check handling start time");
    long startTime = System.currentTimeMillis();
    request.setAttribute("executionTime", startTime);
}

```

在这部分代码中，我们设置了处理执行的开始时间。从这一刻起，我们将计算完成每个请求处理的秒数。在下一部分中，我们将提供会话时间的逻辑，前提是有人在他的 HTTP 会话期间登录：

```java
if (UserInterceptor.isUserLogged()) {
    session = request.getSession();
    log.info("Time since last request in this session: {} ms",
      System.currentTimeMillis() - request.getSession().getLastAccessedTime());
    if (System.currentTimeMillis() - session.getLastAccessedTime()
      > MAX_INACTIVE_SESSION_TIME) {
        log.warn("Logging out, due to inactive session");
        SecurityContextHolder.clearContext();
        request.logout();
        response.sendRedirect("/spring-rest-full/logout");
    }
}
return true;

```

首先，我们需要从请求中获取会话。

接下来，我们做一些控制台日志记录，关于谁登录，以及已经过去了多长时间，因为用户在我们的应用程序中执行了任何操作。我们可以使用session.getLastAccessedTime()来获取此信息，从当前时间中减去它并与我们的MAX_INACTIVE_SESSION_TIME 进行比较。

如果时间超过我们允许的时间，我们清除上下文，注销请求，然后(可选)发送重定向作为对默认注销视图的响应，默认注销视图在 Spring Security 配置文件中声明。

为了完成处理时间的计数器示例，我们还实现了 postHandle()方法，该方法将在下一小节中介绍。

### 3.2. 后处理()

这个方法只是为了获取信息，处理当前请求需要多长时间。正如在前面的代码片段中看到的，我们在 Spring 模型中设置了executionTime 。现在是时候使用它了：

```java
@Override
public void postHandle(
  HttpServletRequest request, 
  HttpServletResponse response,
  Object handler, 
  ModelAndView model) throws Exception {
    log.info("Post handle method - check execution time of handling");
    long startTime = (Long) request.getAttribute("executionTime");
    log.info("Execution time for handling the request was: {} ms",
      System.currentTimeMillis() - startTime);
}
```

实现很简单——我们检查执行时间并从当前系统时间中减去它。只要记住将模型的值转换为long。

现在我们可以正确记录执行时间。

## 4.拦截器的配置

要将我们新创建的拦截器添加到 Spring 配置中，我们需要覆盖实现 WebMvcConfigurer 的WebConfig类中的 addInterceptors()方法：

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new SessionTimerInterceptor());
}
```

我们可以通过编辑我们的 XML Spring 配置文件来实现相同的配置：

```xml
<mvc:interceptors>
    <bean id="sessionTimerInterceptor" class="com.baeldung.web.interceptor.SessionTimerInterceptor"/>
</mvc:interceptors>
```

此外，我们需要添加监听器，以便自动创建ApplicationContext：

```java
public class ListenerConfig implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext sc) throws ServletException {
        sc.addListener(new RequestContextListener());
    }
}
```

## 5. 总结

本教程展示了如何使用 Spring MVC 的HandlerInterceptor拦截 Web 请求，以便手动进行会话管理/超时。

像往常一样，所有示例和配置都可以在[GitHub](https://github.com/eugenp/tutorials/tree/master/spring-security-modules/spring-security-web-mvc-custom)上找到。

### 5.1。系列文章

该系列的所有文章：

-   [Spring MVC Handler 拦截器简介](https://www.baeldung.com/spring-mvc-handlerinterceptor)
-   [使用处理程序拦截器更改 Spring 模型参数](https://www.baeldung.com/spring-model-parameters-with-handler-interceptor)
-   + 当前文章