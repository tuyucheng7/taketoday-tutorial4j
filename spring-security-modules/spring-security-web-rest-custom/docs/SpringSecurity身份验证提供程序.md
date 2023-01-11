## 1. 概述

在本教程中，我们将学习如何在 Spring Security中设置身份验证提供程序，与使用简单UserDetailsService的标准场景相比，它具有更大的灵活性。

## 2. 身份验证提供者

Spring Security 提供了多种执行身份验证的选项。这些选项遵循简单的合同；Authentication请求由 AuthenticationProvider 处理，并返回具有完整凭据的完全身份验证的对象。

标准和最常见的实现是DaoAuthenticationProvider，它从简单的只读用户 DAO UserDetailsS ervice 中检索用户详细信息。此用户详细信息服务只有访问用户名才能检索完整的用户实体，这对于大多数情况来说已经足够了。

更多自定义场景仍需要访问完整的身份验证请求才能执行身份验证过程。例如，当针对某些外部第三方服务(例如[Crowd](https://www.atlassian.com/software/crowd))进行身份验证时，身份验证请求中的用户名和密码都是必需的。

对于这些更高级的场景，我们需要定义一个自定义 Authentication Provider：

```java
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) 
      throws AuthenticationException {
 
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        if (shouldAuthenticateAgainstThirdPartySystem()) {
 
            // use the credentials
            // and authenticate against the third-party system
            return new UsernamePasswordAuthenticationToken(
              name, password, new ArrayList<>());
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
```

请注意，在返回的Authentication对象上设置的授权权限是空的。这是因为权限当然是特定于应用程序的。

## 3.注册Auth Provider

现在我们已经定义了身份验证提供程序，我们需要使用可用的命名空间支持在 XML 安全配置中指定它：

```xml
<http use-expressions="true">
    <intercept-url pattern="/" access="isAuthenticated()"/>
    <http-basic/>
</http>

<authentication-manager>
    <authentication-provider
      ref="customAuthenticationProvider" />
</authentication-manager>
```

## 4.Java配置

接下来，我们来看看对应的Java配置：

```java
@Configuration
@EnableWebSecurity
@ComponentScan("com.baeldung.security")
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationProvider authProvider;

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authProvider);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic();
        return http.build();
    }

}
```

## 5. 执行认证

在后端使用或不使用此自定义身份验证提供程序时，从客户端请求身份验证基本相同。

我们将使用一个简单的curl命令来发送经过身份验证的请求：

```bash
curl --header "Accept:application/json" -i --user user1:user1Pass 
    http://localhost:8080/spring-security-custom/api/foo/1
```

出于本示例的目的，我们使用基本身份验证保护 REST API。

我们从服务器返回预期的 200 OK：

```bash
HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
Set-Cookie: JSESSIONID=B8F0EFA81B78DE968088EBB9AFD85A60; Path=/spring-security-custom/; HttpOnly
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Sun, 02 Jun 2013 17:50:40 GMT
```

## 六. 总结

在本文中，我们探讨了 Spring Security 的自定义身份验证提供程序的示例。

本文的完整实现可以在[GitHub 项目](https://github.com/eugenp/tutorials/tree/master/spring-security-modules/spring-security-web-rest-custom)中找到。