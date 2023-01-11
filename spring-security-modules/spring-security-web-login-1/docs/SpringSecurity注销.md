## 1. 概述

本文以我们的[表单登录](./SpringSecurity表单登录.md)教程为基础，并将重点介绍如何使用Spring Security配置注销。

## 2. 基本配置

使用logout()方法的Spring注销功能的基本配置非常简单：

```java

@Configuration
@EnableWebSecurity
public class SecSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //...
                .logout();
        //...
    }
    //...
}
```

使用XML配置：

```text
<http>
    ...    
    <logout/>
</http>
```

该元标签启用默认注销机制，配置为使用以下注销url：/logout，在Spring Security 4之前是/j_spring_security_logout。

## 3. JSP和注销链接

在Web应用程序中提供注销链接的方法是：

```html
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head></head>
<body>
<a href="<c:url value="/logout" />">Logout</a>
</body>
</html>
```

## 4. 自定义项

### 4.1 logoutSuccessUrl()

注销过程成功执行后，Spring Security会将用户重定向到指定页面。默认情况下是根页面(“/”)，但这是可配置的：

```text
//...
.logout()
.logoutSuccessUrl("/afterlogout.html")
//...
```

这也可以使用XML配置来完成：

```text
<logout logout-success-url="/afterlogout.html" />
```

根据应用程序的不同，一个好的做法是将用户重定向回登录页面：

```text
//...
.logout()
.logoutSuccessUrl("/login.html")
//...
```

### 4.2 logoutUrl()

与Spring Security中的其他默认值类似，实际触发注销机制的URL也有一个默认值/logout。

但是，此默认值也是可以更改的，以确保不会公布有关我们的应用程序使用的是什么框架：

```text
.logout()
.logoutUrl("/perform_logout")
```

XML配置：

```text
<logout 
  logout-success-url="/anonymous.html" 
  logout-url="/perform_logout" />
```

### 4.3 invalidateHttpSession和deleteCookies

这两个属性控制session失效以及用户注销时要删除的cookie列表。
因此，invalidateHttpSession允许设置session，以便在注销时不会使其无效(默认情况下为true)。

deleteCookies方法也很简单：

```text
.logout()
.logoutUrl("/perform_logout")
.invalidateHttpSession(true)
.deleteCookies("JSESSIONID")
```

XML配置：

```text
<logout 
  logout-success-url="/anonymous.html" 
  logout-url="/perform_logout"
  delete-cookies="JSESSIONID" />
```

### 4.4 logoutSuccessHandler()

对于更高级的场景，如果Spring Security提供的名称空间不够灵活，可以将Spring Context中的LogoutSuccessHandler bean替换为自定义引用：

```text
@Bean
public LogoutSuccessHandler logoutSuccessHandler() {
    return new CustomLogoutSuccessHandler();
}

//...
.logout()
.logoutSuccessHandler(logoutSuccessHandler());
//...
```

等效的XML配置是：

```text
<logout 
  logout-url="/perform_logout"
  delete-cookies="JSESSIONID"
  success-handler-ref="customLogoutSuccessHandler" />

...
<beans:bean name="customUrlLogoutSuccessHandler" />
```

**用户成功注销时需要运行的任何自定义应用程序逻辑都可以通过自定义LogoutSuccessHandler实现**。
例如，一种简单的审计机制，用于跟踪用户触发注销时所在的最后一页：

```java
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private AuditService auditService;

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        String refererUrl = request.getHeader("Referer");
        auditService.track("Logout from: " + refererUrl);
        super.onLogoutSuccess(request, response, authentication);
    }
}
```

此外，请记住，此自定义bean有责任确定用户在注销后被重定向到的目的地。
因此，将logoutSuccessHandler属性与logoutSuccessUrl同时使用是不建议的，因为两者都涵盖了相似的功能。

## 5. 总结

在这个例子中，我们首先使用Spring Security配置了一个简单的注销示例，然后我们讨论了更高级的可用配置。