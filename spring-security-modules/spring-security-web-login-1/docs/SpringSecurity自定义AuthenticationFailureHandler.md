## 1. 概述

在这个教程中，我们演示如何在Spring Boot应用程序中自定义Spring Security中的AuthenticationFailureHandler，
我们的目标是使用表单登录方法对用户进行身份验证。

## 2. 身份验证和授权

身份验证和授权通常结合使用，因为在授予对系统的访问权限时，它们起着至关重要的作用。

但是，它们有不同的含义并在验证请求时应用不同的约束：

+ **认证** - 在授权之前，它处理验证接收到的凭据；在这里，我们验证用户名和密码是否与应用程序识别的用户名和密码相匹配。
+ **授权** - 它处理关于验证成功通过身份验证的用户是否有权访问应用程序的某些功能。

我们可以自定义身份验证和授权失败的处理，但是在本文中，我们只重点关注身份验证失败。

## 3. Spring Security的AuthenticationFailureHandler

默认情况下，Spring Security为我们提供了一个处理身份验证失败的组件。

但是，我们经常会面临默认行为不足以满足我们的需求。

如果是这种情况，我们可以通过实现AuthenticationFailureHandler接口来创建自己的组件并提供所需的自定义行为：

```java
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());

        String jsonPayload = "{\"message\" : \"%s\", \"timestamp\" : \"%s\" }";
        httpServletResponse.getOutputStream().println(String.format(jsonPayload, e.getMessage(), Calendar.getInstance().getTime()));
    }
}
```

默认情况下，Spring使用包含错误信息的请求参数将用户重定向回登录页面。

在这个应用程序中，我们将返回一个401响应，其中包含有关错误的信息以及错误发生的时间戳。

除了默认组件之外，Spring还提供了其他可随时使用的组件，我们可以根据自己的需要利用这些组件：

+ **DelegatingAuthenticationFailureHandler**将**AuthenticationException**子类委托给不同的AuthenticationFailureHandler，
  这意味着我们可以为不同的AuthenticationException实例创建不同的行为。
+ **ExceptionMappingAuthenticationFailureHandler**根据AuthenticationException的完整类名将用户重定向到特定URL。
+ **ForwardAuthenticationFailureHandler**将用户转发到指定的URL，而不管AuthenticationException的类型是什么。
+ **SimpleUrlAuthenticationFailureHandler**是默认使用的组件，如果指定，它会将用户重定向到failureUrl；否则它只会返回401响应。

现在我们已经创建了自定义AuthenticationFailureHandler，我们需要配置它来覆盖Spring的默认处理程序：

```java

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user1 = User.withUsername("user1")
                .password(passwordEncoder().encode("user1Pass"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user1);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .failureHandler(authenticationFailureHandler());
        return http.build();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**注意failureHandler()方法的调用，在这里我们可以指定Spring使用我们的自定义组件而不是使用默认组件**。

## 4. 总结

在本文中，我们通过Spring的AuthenticationFailureHandler接口自定义了应用程序的身份验证失败处理程序。