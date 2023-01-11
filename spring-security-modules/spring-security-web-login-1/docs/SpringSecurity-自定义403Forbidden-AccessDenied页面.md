## 1. 概述

在本文中，我们将演示如何在Spring Security项目中自定义拒绝访问页面。

这可以通过web.xml文件中的Spring Security配置或Web应用程序配置来实现。

## 2. 自定义JSP

每当用户尝试访问仅限于他们没有的角色的页面时，应用程序将返回状态码403，这意味着访问被拒绝。

为了将Spring 403状态响应页面替换为自定义页面，我们首先创建一个名为accessDenied.jsp的JSP文件：

```html
<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Access Denied</title>
</head>
<body>
<h2>Sorry, you do not have permission to view this page.</h2>

Click <a href="<c:url value="/homepage.html" /> ">here</a> to go back to the Homepage.
</body>
</html>
```

## 3. Spring Security配置

默认情况下，Spring Security定义了一个ExceptionTranslationFilter，用于处理AuthenticationException和AccessDeniedException类型的异常。
后者是通过一个名为accessDeniedHandler的属性完成的，该属性使用AccessDeniedHandlerImpl类。

为了自定义此行为以使用我们在上面创建的自己的页面，需要覆盖ExceptionTranslationFilter类的属性。
这可以通过Java配置或XML配置来完成。

### 3.1 访问拒绝页面

使用Java，我们可以在配置HttpSecurity元素的同时使用accessDeniedPage()或accessDeniedHandler()方法自定义403错误处理过程。

让我们创建一个身份验证配置，将“/admin/**” URL限制为ADMIN角色，并将拒绝访问页面设置为我们的自定义accessDenied.jsp页面：

```text
@Override
protected void configure(final HttpSecurity http) throws Exception {
    http
            // ...
            .and()
            .exceptionHandling().accessDeniedPage("/accessDenied.jsp");
}
```

下面是拒绝访问页面的等效XML配置：

```xml

<http use-expressions="true">
    <access-denied-handler error-page="/accessDenied"/>
</http>
```

### 3.2 拒绝访问处理程序

使用拒绝访问处理程序的优点是我们可以定义在重定向到403页面之前要执行的自定义逻辑。
为此，我们需要创建一个实现AccessDeniedHandler接口并重写handle()方法的类。

让我们创建一个自定义AccessDeniedHandler类，该类为每次访问被拒绝的用户记录日志，其中包含访问的用户和他们尝试访问的受保护URL：

```java
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    public static final Logger LOG = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exc) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            LOG.warn("User: " + auth.getName() + " attempted to access the protected URL: " + request.getRequestURI());
        }

        response.sendRedirect(request.getContextPath() + "/accessDenied");
    }
}
```

在安全配置中，我们需要将CustomAccessDeniedHandler定义为bean并设置自定义AccessDeniedHandler：

```text
@Bean
public AccessDeniedHandler accessDeniedHandler(){
    return new CustomAccessDeniedHandler();
}

//...
.exceptionHandling().accessDeniedHandler(accessDeniedHandler());
```

如果我们想使用XML配置上面定义的CustomAccessDeniedHandler类，稍微会略有不同：

```text

<bean name="customAccessDeniedHandler"
      class="cn.tuyucheng.security.CustomAccessDeniedHandler"/>

<http use-expressions="true">
    <access-denied-handler ref="customAccessDeniedHandler"/>
</http>
```

## 4. 应用程序配置

**可以使用Web应用程序的web.xml文件，通过定义<error-page\>标签来处理拒绝访问错误**。
这包含两个子标签，一个是<error-code\>，它指定要拦截的状态码，以及<location\>，它表示在遇到错误码时用户将被重定向到的URL：

```xml

<error-page>
    <error-code>403</error-code>
    <location>/accessDenied</location>
</error-page>
```

如果应用程序没有web.xml文件，就像Spring Boot的情况一样，Spring注解目前没有提供<error-page>标签的确切替代方案。
根据Spring文档，在这种情况下，推荐的方法是使用第3节中介绍的方法accessDeniedPage()和accessDeniedHandler()。

## 5. 总结

在这篇文章中，我们详细介绍了使用自定义403页面处理拒绝访问错误的各种方法。