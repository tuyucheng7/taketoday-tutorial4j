## 1. 概述

大多数Web应用程序都有执行请求日志记录、验证或身份验证等操作的用例。而且，更重要的是，此类任务通常在一组HTTP端点之间共享。

好消息是Spring Web框架正是为此目的提供了一种[过滤机制](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/filter/package-summary.html)。

在本教程中，我们将了解如何针对一组给定的URL执行过滤器样式的任务或将其排除在外。

## 2. 过滤特定的网址

假设我们的Web应用程序需要记录有关其请求的一些信息，例如它们的路径和内容类型。一种方法是创建日志过滤器。

### 2.1 日志过滤器

首先，让我们在扩展[OncePerRequestFilter](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/filter/OncePerRequestFilter.html)类并实现[doFilterInternal](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/filter/OncePerRequestFilter.html#doFilterInternal-javax.servlet.http.HttpServletRequest-javax.servlet.http.HttpServletResponse-javax.servlet.FilterChain-)方法的LogFilter类中创建日志过滤器： 

```java
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String path = request.getRequestURI();
    String contentType = request.getContentType();
    logger.info("Request URL path : {}, Request content type: {}", path, contentType);
    filterChain.doFilter(request, response);
}
```

### 2.1 规则过滤器

假设我们只需要为选定的URL模式(即/health、/faq/*)执行日志记录任务。为此，我们将使用FilterRegistrationBean注册我们的日志过滤器，使其仅匹配所需的URL模式：

```java
@Bean
public FilterRegistrationBean<LogFilter> logFilter() {
    FilterRegistrationBean<LogFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new LogFilter());
    registrationBean.addUrlPatterns("/health","/faq/*");
    return registrationBean;
}
```

### 2.2 排除过滤器

如果我们想从执行日志记录任务中排除URL，我们可以通过两种方式轻松实现：

-   对于新URL，确保它与过滤器使用的URL模式不匹配
-   对于之前启用了日志记录的旧URL，我们可以修改URL模式以排除该URL

## 3. 过滤所有可能的网址

我们很容易地满足了我们以前用最小的努力在LogFilter中包含URL的用例。但是，如果过滤器使用通配符(*)来匹配所有可能的URL模式，它就会变得更加棘手。

在这种情况下，我们需要自己编写包含和排除逻辑。

### 3.1 自定义过滤器

客户端可以使用请求标头向服务器发送有用的信息。假设我们的Web应用程序目前仅在美国运行，这意味着我们不想处理来自其他国家/地区的请求。

让我们进一步假设我们的Web应用程序通过X-Country-Code请求标头指示语言环境。因此，每个请求都带有此信息，我们有一个使用过滤器的明确案例。

让我们实现一个过滤器来检查标头，拒绝不符合我们条件的请求：

```java
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String countryCode = request.getHeader("X-Country-Code");
    if (!"US".equals(countryCode)) {
        response.sendError(HttpStatus.BAD_REQUEST.value(), "Invalid Locale");
        return;
    }

    filterChain.doFilter(request, response);
}
```

### 3.2 过滤器注册

首先，让我们使用星号(*)通配符来注册我们的过滤器以匹配所有可能的URL模式：

```java
@Bean
public FilterRegistrationBean<HeaderValidatorFilter> headerValidatorFilter() {
    FilterRegistrationBean<HeaderValidatorFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new HeaderValidatorFilter());
    registrationBean.addUrlPatterns("*");
    return registrationBean;
}
```

在稍后的某个时间点，我们可以排除执行验证区域设置请求标头信息的任务不需要的URL模式。

## 4. 网址排除

在本节中，我们将学习如何为我们的客户过滤器排除URL。

### 4.1 天真的策略

让我们再次假设我们在/health有一个Web路由，可用于对应用程序进行ping健康检查。

到目前为止，所有请求都会触发我们的过滤器。正如我们所猜测的那样，当涉及到我们的健康检查时，这是一项开销。

因此，让我们通过从过滤器的主体中排除它们来简化我们的/health请求：

```java
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String path = request.getRequestURI();
    if ("/health".equals(path)) {
    	filterChain.doFilter(request, response);
    	return;
    }

    String countryCode = request.getHeader("X-Country-Code");
    // ... same as before
}
```

我们必须注意，在doFilter方法中添加此自定义逻辑会在/health端点和我们的过滤器之间引入耦合。因此，它不是最优的，因为如果我们更改健康检查端点而不在doFilter方法内进行相应更改，我们可能会破坏过滤逻辑。

### 4.2 使用shouldNotFilter方法

使用之前的方法，我们在URL排除和过滤器的任务执行逻辑之间引入了紧密耦合。在打算对另一部分进行更改时，可能会无意中在一部分中引入错误。

相反，我们可以通过覆盖[shouldNotFilter](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/filter/OncePerRequestFilter.html#shouldNotFilter-javax.servlet.http.HttpServletRequest-)方法来隔离两组逻辑：

```java
@Override
protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getRequestURI();
    return "/health".equals(path);
}
```

因此，doInternalFilter()方法遵守[单一责任原则](https://baeldung.com/solid-principles#s)：

```java
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String countryCode = request.getHeader("X-Country-Code");
    // ... same as before
}
```

## 5. 总结

在本教程中，我们探讨了如何从Spring Boot Web应用程序中的Servlet过滤器中排除URL模式以用于两个用例，即日志记录和请求标头验证。

此外，我们了解到，为使用通配符匹配所有可能的URL模式的过滤器排除一组特定的URL变得很棘手。