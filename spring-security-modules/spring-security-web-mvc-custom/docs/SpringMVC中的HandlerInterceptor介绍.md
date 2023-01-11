## 1. 概述

在本教程中，我们将重点了解 Spring MVC HandlerInterceptor以及如何正确使用它。


## 2. Spring MVC 处理程序

为了理解 Spring 拦截器是如何工作的，让我们退一步看一下HandlerMapping。

HandlerMapping的目的是将处理程序方法映射到 URL。这样，DispatcherServlet将能够在处理请求时调用它。

事实上，DispatcherServlet使用HandlerAdapter来实际调用该方法。

简而言之，拦截器拦截请求并处理它们。它们有助于避免重复的处理程序代码，例如日志记录和授权检查。

现在我们了解了整体上下文，让我们看看如何使用HandlerInterceptor执行一些预处理和后处理操作。

## 3. Maven依赖

为了使用拦截器，我们需要在pom.xml中包含[spring-web](https://search.maven.org/classic/#search|ga|1|a%3A"spring-web")依赖：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>5.3.13</version>
</dependency>

```

## 4. Spring Handler 拦截器

简单地说，Spring 拦截器是一个类，它要么扩展了HandlerInterceptorAdapter类，要么实现了HandlerInterceptor接口。

HandlerInterceptor包含三个主要方法：

-   prehandle() – 在实际处理程序执行之前调用
-   postHandle() –在处理程序执行后调用
-   afterCompletion() –在完成请求并生成视图后调用

这三种方法为进行各种预处理和后处理提供了灵活性。

在我们继续之前的快速说明：要跳过理论并直接跳到示例，请直接跳到第 5 节。

这是一个简单的preHandle()实现：

```java
@Override
public boolean preHandle(
  HttpServletRequest request,
  HttpServletResponse response, 
  Object handler) throws Exception {
    // your code
    return true;
}
```

请注意，该方法返回一个布尔值。它告诉 Spring 进一步处理请求 ( true ) 或不 ( false )。

接下来，我们有一个postHandle()的实现：

```java
@Override
public void postHandle(
  HttpServletRequest request, 
  HttpServletResponse response,
  Object handler, 
  ModelAndView modelAndView) throws Exception {
    // your code
}
```

拦截器在处理请求后但在生成视图之前立即调用此方法。

例如，我们可以使用这种方法将登录用户的头像添加到模型中。

我们需要实现的最后一个方法是afterCompletion()：

```java
@Override
public void afterCompletion(
  HttpServletRequest request, 
  HttpServletResponse response,
  Object handler, Exception ex) {
    // your code
}
```

该 方法允许我们在请求处理完成后执行自定义逻辑。

此外，值得一提的是，我们可以注册多个自定义拦截器。为此，我们可以使用[DefaultAnnotationHandlerMapping](https://docs.spring.io/spring-framework/docs/4.3.7.RELEASE_to_4.3.8.RELEASE/Spring Framework 4.3.8.RELEASE/org/springframework/web/servlet/mvc/annotation/DefaultAnnotationHandlerMapping.html)。

## 5.自定义记录器拦截器

在此示例中，我们将专注于登录我们的 Web 应用程序。

首先，我们的类需要实现HandlerInterceptor：

```java
public class LoggerInterceptor implements HandlerInterceptor {
    ...
}
```

我们还需要在拦截器中启用日志记录：

```java
private static Logger log = LoggerFactory.getLogger(LoggerInterceptor.class);
```

这允许Log4J显示日志以及指示哪个类当前正在将信息记录到指定的输出。

接下来，让我们关注我们的自定义拦截器实现。

### 5.1。preHandle()方法

顾名思义，拦截器在处理请求之前调用preHandle() 。

默认情况下，此方法返回true以将请求进一步发送到处理程序方法。但是，我们可以通过返回false来告诉 Spring 停止执行。

我们可以使用该钩子来记录有关请求参数的信息，例如请求来自何处。

在我们的示例中，我们使用简单的Log4J记录器记录此信息：

```java
@Override
public boolean preHandle(
  HttpServletRequest request,
  HttpServletResponse response, 
  Object handler) throws Exception {
    
    log.info("[preHandle][" + request + "]" + "[" + request.getMethod()
      + "]" + request.getRequestURI() + getParameters(request));
    
    return true;
}

```

如我们所见，我们正在记录有关请求的一些基本信息。

万一我们在这里遇到密码，当然，我们需要确保我们不记录密码。一个简单的选择是用星号替换密码和任何其他敏感类型的数据。

这是如何执行此操作的快速实现：

```java
private String getParameters(HttpServletRequest request) {
    StringBuffer posted = new StringBuffer();
    Enumeration<?> e = request.getParameterNames();
    if (e != null) {
        posted.append("?");
    }
    while (e.hasMoreElements()) {
        if (posted.length() > 1) {
            posted.append("&");
        }
        String curr = (String) e.nextElement();
        posted.append(curr + "=");
        if (curr.contains("password") 
          || curr.contains("pass")
          || curr.contains("pwd")) {
            posted.append("");
        } else {
            posted.append(request.getParameter(curr));
        }
    }
    String ip = request.getHeader("X-FORWARDED-FOR");
    String ipAddr = (ip == null) ? getRemoteAddr(request) : ip;
    if (ipAddr!=null && !ipAddr.equals("")) {
        posted.append("&_psip=" + ipAddr); 
    }
    return posted.toString();
}
```

最后，我们的目标是获取 HTTP 请求的源 IP 地址。

这是一个简单的实现：

```java
private String getRemoteAddr(HttpServletRequest request) {
    String ipFromHeader = request.getHeader("X-FORWARDED-FOR");
    if (ipFromHeader != null && ipFromHeader.length() > 0) {
        log.debug("ip from proxy - X-FORWARDED-FOR : " + ipFromHeader);
        return ipFromHeader;
    }
    return request.getRemoteAddr();
}
```

### 5.2. postHandle()方法

拦截器在处理程序执行之后但在DispatcherServlet呈现视图之前调用此方法。

我们可以使用它为ModelAndView添加额外的属性。另一个用例是计算请求的处理时间。

在我们的例子中，我们将在DispatcherServlet呈现视图之前简单地记录我们的请求：

```java
@Override
public void postHandle(
  HttpServletRequest request, 
  HttpServletResponse response,
  Object handler, 
  ModelAndView modelAndView) throws Exception {
    
    log.info("[postHandle][" + request + "]");
}
```

### 5.3. afterCompletion()方法

我们可以使用这个方法来获取视图渲染后的请求和响应数据：

```java
@Override
public void afterCompletion(
  HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) 
  throws Exception {
    if (ex != null){
        ex.printStackTrace();
    }
    log.info("[afterCompletion][" + request + "][exception: " + ex + "]");
}
```

## 6.配置

现在我们已经将所有部分放在一起，让我们添加我们的自定义拦截器。

为此，我们需要重写addInterceptors()方法：

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new LoggerInterceptor());
}
```

我们可以通过编辑我们的 XML Spring 配置文件来实现相同的配置：

```xml
<mvc:interceptors>
    <bean id="loggerInterceptor" class="com.baeldung.web.interceptor.LoggerInterceptor"/>
</mvc:interceptors>
```

启用此配置后，拦截器将处于活动状态，并且应用程序中的所有请求都将被正确记录。

请注意，如果配置了多个 Spring 拦截器，则preHandle()方法按配置顺序执行，而postHandle()和afterCompletion()方法按相反顺序调用。

请记住，如果我们使用Spring Boot而不是 vanilla Spring ，我们不需要使用@EnableWebMvc注解我们的配置类。

## 7. 总结

本文简要介绍了使用 Spring MVC Handler Interceptors 拦截 HTTP 请求。