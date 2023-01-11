## 1. 概述

在本文中，我们将了解如何处理由Spring Security资源服务器生成的Spring Security异常。
为此，我们将使用一个实际示例，其中解释所有必要的配置。首先，让我们对Spring Security做一个简短的介绍。

## 2. Spring Security

SpringSecurity是Spring项目的一部分。**它对Spring项目中用户访问控制的所有功能进行分组**。
访问控制允许限制应用程序上给定的一组用户或角色可以执行的选项。
在这个方向上，**Spring Security控制对业务逻辑的调用或限制HTTP请求对某些URL的访问**。
考虑到这一点，我们必须通过告诉Spring Security安全层的行为来配置应用程序。

在我们的例子中，我们将重点关注于异常处理程序的配置。**Spring Security提供了三种不同的接口来实现这个目的并控制生成的事件**：

+ 身份验证成功处理程序
+ 身份验证失败处理程序
+ 拒绝访问处理程序

首先，让我们仔细看看配置。

## 3. Security配置

**首先，我们的配置类必须继承WebSecurityConfigurerAdapter类。这将负责管理应用程序的所有安全配置**。所以，在这里我们必须配置我们的处理程序。

一方面，我们将定义所需的配置：

```java

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .httpBasic()
                .disable()
                .authorizeRequests()
                .antMatchers("/login")
                .permitAll()
                .antMatchers("/customError")
                .permitAll()
                .antMatchers("/access-denied")
                .permitAll()
                .antMatchers("/secured")
                .hasRole("ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .failureHandler(authenticationFailureHandler())
                .successHandler(authenticationSuccessHandler())
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .and()
                .logout();
    }
}
```

有趣的是，重定向URL(如“/login”、“/customError”和“/access-denied”)不需要任何类型的限制就可以访问它们。
因此，我们将它们定义为permitAll()。

另一方面，我们必须定义Bean来定义我们可以处理的异常类型：

```text
@Bean
public AuthenticationFailureHandler authenticationFailureHandler() {
    return new CustomAuthenticationFailureHandler();
} 

@Bean
public AuthenticationSuccessHandler authenticationSuccessHandler() {
    return new CustomAuthenticationSuccessHandler();
}

@Bean
public AccessDeniedHandler accessDeniedHandler() {
    return new CustomAccessDeniedHandler();
}
```

由于AuthenticationSuccessHandler处理happy路径，我们将为异常情况定义剩余的两个bean。
这两个处理程序是我们现在必须适配和实现的，以满足我们的需要。因此，让我们继续实现他们其中的每一个。

## 4. 身份验证失败处理程序

**我们可以使用AuthenticationFailureHandler接口。它负责管理用户登录失败时产生的异常**。
该接口为我们提供了onAuthenticationFailure()方法来自定义处理程序逻辑。
**当登录尝试失败时，Spring Security将调用它**。考虑到这一点，让我们定义我们的异常处理程序，以便在发生登录失败时将我们重定向到错误页面：

```java
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        httpServletResponse.sendRedirect("/customError");
    }
}
```

## 5. 拒绝访问处理程序

**另一方面，当未经授权的用户尝试访问安全或受保护的页面时，Spring Security将抛出AccessDeniedException**。
Spring Security提供了一个默认的403拒绝访问页面，我们可以对其进行自定义。
这由AccessDeniedHandler接口管理。**此外，它提供了handle()方法，用于在将用户重定向到403页面之前自定义逻辑**：

```java
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exc) throws IOException {
        response.sendRedirect("/access-denied");
    }
}
```

## 6. 总结

在这篇文章中，我们学习了如何处理Spring Security异常以及如何通过创建和自定义类来控制它们。
此外，我们创建了一个功能齐全的案例，帮助我们理解涉及的概念。