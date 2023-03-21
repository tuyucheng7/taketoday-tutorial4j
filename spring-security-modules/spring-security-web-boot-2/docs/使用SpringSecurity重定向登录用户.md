## 1. 概述

**网站通常会阻止用户在登录后访问登录页面**。一种常见的方法是将用户重定向到另一个页面，通常是登录后应用程序的首页。

在本教程中，我们将探索使用Spring Security实现此解决方案的多种方法。

另外，要了解更多关于如何快速实现登录的信息，我们可以从[这篇文章](https://www.baeldung.com/spring-security-login)开始。

## 2. 身份验证

首先，我们需要一种方法来验证authentication。

换句话说，**我们需要从SecurityContext获取Authentication详细信息并验证用户是否已登录**：

```java
@Controller
class UsersController {

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass()))
            return false;
        return authentication.isAuthenticated();
    }
}
```

**我们将在以下所有负责重定向的组件中使用它**。

## 3. 从登录控制器重定向

实现我们目标的最简单方法是在控制器中为登录页面定义一个端点。

如果用户通过身份验证，我们需要返回一个特定页面，否则返回登录页面：

```java
@GetMapping("/userMainPage")
public String getUserPage() {
    return "userMainPage";
}

@GetMapping("/loginUser")
public String getUserLoginPage() {
    if (isAuthenticated())
        return "redirect:userMainPage";
    return "loginUser";
}
```

## 4. 使用拦截器

**重定向用户的另一种方法是通过登录页面URI上的拦截器**。

拦截器将在请求到达控制器之前将其拦截。因此，我们可以根据authentication来决定是让它继续进行还是阻止它并返回重定向响应。

如果用户已通过身份验证，则需要在响应中修改两件事：

+ 将状态码设置为HttpStatus.SC_TEMPORARY_REDIRECT
+ 添加带有重定向URL的Location标头

最后，我们将通过返回false来中断执行链：

```java
class LoginPageInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        UrlPathHelper urlPathHelper = new UrlPathHelper();
        if ("/loginUser".equals(urlPathHelper.getLookupPathForRequest(request)) && isAuthenticated()) {

            String encodedRedirectURL = response.encodeRedirectURL(request.getContextPath() + "/userMainPage");
            response.setStatus(HttpStatus.SC_TEMPORARY_REDIRECT);
            response.setHeader("Location", encodedRedirectURL);

            return false;
        } else {
            return true;
        }
    }
    
    // isAuthenticated method 
}
```

**我们还需要将拦截器添加到Spring MVC生命周期中**：

```java
@Configuration
public class LoginRedirectMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginPageInterceptor());
    }
}
```

我们可以使用Spring的基于XML Schema的配置来实现相同的目的：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:beans="http://www.springframework.org/schema/beans"
       xmlns:security="http://www.springframework.org/schema/security"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="
        http://www.springframework.org/schema/mvc
        http://www.springframework.org/schema/mvc/spring-mvc-4.3.xsd
       http://www.springframework.org/schema/security
        http://www.springframework.org/schema/security/spring-security-5.2.xsd
		http://www.springframework.org/schema/beans
		http://www.springframework.org/schema/beans/spring-beans.xsd">

    <mvc:interceptors>
        <mvc:interceptor>
            <mvc:mapping path="/loginUser"/>
            <bean class="cn.tuyucheng.taketoday.loginredirect.LoginPageInterceptor"/>
        </mvc:interceptor>
    </mvc:interceptors>
</beans>
```

## 5. 使用过滤器

**类似地，我们可以实现一个Spring过滤器**。

过滤器可以使用Spring Security的过滤器链直接应用于SecurityContext。因此，它可以在创建authentication后立即拦截请求。

让我们扩展GenericFilterBean，重写doFilter方法，并验证authentication：

```java
class LoginPageFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;

        if (isAuthenticated() && "/loginUser".equals(servletRequest.getRequestURI())) {

            String encodedRedirectURL = ((HttpServletResponse) response).encodeRedirectURL(servletRequest.getContextPath() + "/userMainPage");

            servletResponse.setStatus(HttpStatus.SC_TEMPORARY_REDIRECT);
            servletResponse.setHeader("Location", encodedRedirectURL);
        }

        chain.doFilter(servletRequest, servletResponse);
    }

    // isAuthenticated method 
}
```

**我们需要在过滤器链中的UsernamePasswordAuthenticationFilter之后添加该过滤器**。

此外，我们需要授权对登录页面URI的请求，以便为其启用过滤器链：

```java
@Configuration
@EnableWebSecurity
class LoginRedirectSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.addFilterAfter(new LoginPageFilter(), UsernamePasswordAuthenticationFilter.class)
              .authorizeRequests()
              .antMatchers("/loginUser")
              .permitAll()
              .antMatchers("/user*")
              .hasRole("USER")
              .and()
              .formLogin()
              .loginPage("/loginUser")
              .loginProcessingUrl("/user_login")
              .failureUrl("/loginUser?error=loginError")
              .defaultSuccessUrl("/userMainPage")
              .permitAll()
              .and()
              .logout()
              .logoutUrl("/user_logout")
              .logoutSuccessUrl("/loginUser")
              .deleteCookies("JSESSIONID")
              .and()
              .csrf()
              .disable();
        return http.build();
    }
}
```

最后，如果我们选择使用XML配置，我们可以为过滤器定义bean，并将其添加到HTTP标签中的过滤器链中：

```xml
<security:http pattern="/**" use-expressions="true" auto-config="true">
    <security:intercept-url pattern="/loginUser" access="permitAll"/>
    <security:intercept-url pattern="/user*" access="hasRole('ROLE_USER')"/>

    <security:form-login login-page="/loginUser"
                         login-processing-url="/user_login"
                         authentication-failure-url="/loginUser?error=loginError"
                         default-target-url="/userMainPage"/>
    <security:csrf disabled="true"/>
    <security:logout logout-url="/user_logout" delete-cookies="JSESSIONID" logout-success-url="/loginUser"/>
    <security:custom-filter after="BASIC_AUTH_FILTER" ref="loginPageFilter"/>
</security:http>

<beans:bean id="loginPageFilter" class="LoginPageFilter"/>
```

## 6. 总结

在本教程中，我们探讨了如何使用Spring Security将已登录用户从登录页面重定向到其他页面的多种方法。

另一个可能相关的教程是[使用Spring Security登录后重定向到不同的页面](https://www.baeldung.com/spring_redirect_after_login)，其中我们学习如何将不同类型的用户重定向到特定页面。