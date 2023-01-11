## 1. 简介

在本教程中，我们将重点关注 Spring MVC HandlerInterceptor。更具体地说，我们将在处理请求之前和之后更改 Spring MVC 的模型参数。

如果想了解HandlerInterceptor 的基础知识，请查看这篇[文章](https://www.baeldung.com/spring-mvc-handlerinterceptor)。

## 2.Maven依赖

为了使用拦截器，需要在pom.xml文件的依赖项部分中包含以下部分：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>5.3.13</version>
</dependency>

```

最新版本可以在[这里](https://search.maven.org/classic/#search|ga|1|a%3A"spring-web")找到。

此依赖项仅涵盖 Spring Web，因此不要忘记为完整的 Web 应用程序添加 s pring-core和spring-context，以及选择的日志库。

## 3.自定义实现

HandlerInterceptor的用例之一是向模型添加公共/用户特定参数，这些参数将在每个生成的视图上可用。

在我们的示例中，我们将使用自定义拦截器实现将登录用户的用户名添加到模型参数中。在更复杂的系统中，我们可能会添加更具体的信息，例如：用户头像路径、用户位置等。

让我们从定义我们的新拦截器类开始：

```java
public class UserInterceptor extends HandlerInterceptorAdapter {

    private static Logger log = LoggerFactory.getLogger(UserInterceptor.class);

    ...
}
```

我们扩展了HandlerInterceptorAdapter，因为我们只想实现preHandle()和postHandle()方法。

正如我们之前提到的，我们希望将登录用户的名称添加到模型中。首先，我们需要检查用户是否登录。我们可以通过检查SecurityContextHolder来获取这些信息：

```java
public static boolean isUserLogged() {
    try {
        return !SecurityContextHolder.getContext().getAuthentication()
          .getName().equals("anonymousUser");
    } catch (Exception e) {
        return false;
    }
}
```

当建立HttpSession但没有人登录时，Spring Security 上下文中的用户名等于anonymousUser。接下来，我们继续执行preHandle()：

### 3.1。方法preHandle()

在处理请求之前，我们无法访问模型参数。为了添加用户名，我们需要使用HttpSession来设置参数：

```java
@Override
public boolean preHandle(HttpServletRequest request,
  HttpServletResponse response, Object object) throws Exception {
    if (isUserLogged()) {
        addToModelUserDetails(request.getSession());
    }
    return true;
}
```

如果我们在处理请求之前使用其中的一些信息，这一点至关重要。如我们所见，我们正在检查用户是否已登录，然后通过获取会话向我们的请求添加参数：

```java
private void addToModelUserDetails(HttpSession session) {
    log.info("=============== addToModelUserDetails =========================");
    
    String loggedUsername 
      = SecurityContextHolder.getContext().getAuthentication().getName();
    session.setAttribute("username", loggedUsername);
    
    log.info("user(" + loggedUsername + ") session : " + session);
    log.info("=============== addToModelUserDetails =========================");
}
```

我们使用SecurityContextHolder来获取loggedUsername。可以覆盖 Spring Security UserDetails实现以获取电子邮件而不是标准用户名。

### 3.2. 方法postHandle()

处理请求后，我们的模型参数可用，因此我们可以访问它们以更改值或添加新值。为此，我们使用重写的postHandle()方法：

```java
@Override
public void postHandle(
  HttpServletRequest req, 
  HttpServletResponse res,
  Object o, 
  ModelAndView model) throws Exception {
    
    if (model != null && !isRedirectView(model)) {
        if (isUserLogged()) {
        addToModelUserDetails(model);
    }
    }
}
```

让我们看一下实现细节。

首先，最好检查一下模型是否不为空。它会阻止我们遇到NullPointerException。

此外，我们可以检查一个View是否不是 Redirect View 的一个实例。

请求被处理后再重定向后，不需要添加/更改参数，因为立即，新的控制器将再次执行处理。为了检查视图是否被重定向，我们引入了以下方法：

```java
public static boolean isRedirectView(ModelAndView mv) {
    String viewName = mv.getViewName();
    if (viewName.startsWith("redirect:/")) {
        return true;
    }
    View view = mv.getView();
    return (view != null && view instanceof SmartView
      && ((SmartView) view).isRedirectView());
}
```

最后，我们再次检查用户是否登录，如果是，我们正在向 Spring 模型添加参数：

```java
private void addToModelUserDetails(ModelAndView model) {
    log.info("=============== addToModelUserDetails =========================");
    
    String loggedUsername = SecurityContextHolder.getContext()
      .getAuthentication().getName();
    model.addObject("loggedUsername", loggedUsername);
    
    log.trace("session : " + model.getModel());
    log.info("=============== addToModelUserDetails =========================");
}
```

请注意，日志记录非常重要，因为此逻辑在我们应用程序的“幕后”工作。很容易忘记我们正在更改每个视图上的一些模型参数而没有正确记录它。

## 4.配置

要将我们新创建的拦截器添加到 Spring 配置中，我们需要覆盖实现 WebMvcConfigurer 的WebConfig类中的 addInterceptors()方法：

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new UserInterceptor());
}
```

我们可以通过编辑我们的 XML Spring 配置文件来实现相同的配置：

```xml
<mvc:interceptors>
    <bean id="userInterceptor" class="com.baeldung.web.interceptor.UserInterceptor"/>
</mvc:interceptors>
```

从这一刻起，我们可以访问所有生成视图上的所有用户相关参数。

请注意，如果配置了多个 Spring Interceptor，preHandle()方法按配置顺序执行，而postHandle()和afterCompletion()方法按相反顺序调用。

## 5. 总结

本教程介绍了使用 Spring MVC 的 HandlerInterceptor 拦截 Web 请求以提供用户信息。

在这个特定的示例中，我们专注于在我们的 Web 应用程序中将登录用户的详细信息添加到模型参数中。可以通过添加更多详细信息来扩展此HandlerInterceptor实现。

[GitHub 上](https://github.com/eugenp/tutorials/tree/master/spring-security-modules/spring-security-web-mvc-custom)提供了所有示例和配置。

### 5.1。系列文章

该系列的所有文章：

-   [Spring MVC Handler 拦截器简介](https://www.baeldung.com/spring-mvc-handlerinterceptor)
-   使用 Handler Interceptor 更改 Spring 模型参数(这个)