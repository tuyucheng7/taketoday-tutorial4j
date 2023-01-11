## 1. 概述

本文重点介绍如何在用户登录后将用户重定向回最初请求的URL。

之前，我们已经了解了如何使用Spring Security为不同类型的用户在登录后重定向到不同的页面，并介绍了使用Spring MVC的各种类型的重定向。

本文基于[Spring Security登录](./SpringSecurity表单登录.md)教程。

## 2. 常见做法

登录后实现重定向逻辑的最常见方法是：

+ 使用HTTP Referer头
+ 在会话中保存原始请求
+ 将原始URL附加到重定向的登录URL

**使用HTTP Referer头**是一种简单的方法，因为大多数浏览器和HTTP客户端会自动设置Referer。
但是由于Referer是可伪造的，并且依赖于客户端实现，所以一般不建议使用HTTP Referer头来实现重定向。

**将原始请求保存在会话中是实现这种重定向的一种安全且可靠的方式**。
除了原始URL，我们还可以在会话中存储原始请求属性和任何自定义属性。

在SSO实现中，通常可以看到将原始URL附加到重定向的登录URL。
当通过SSO服务进行身份验证后，用户将被重定向到最初请求的页面，并附加URL。我们必须确保附加的URL正确编码。

另一个类似的实现是将原始请求URL放在登录表单内的hidden字段中，但这并不比使用HTTP Referer好。

**在Spring Security中，原生支持前两种方法**。

必须注意的是，**对于较新版本的Spring Boot，默认情况下，Spring Security能够在登录后重定向到我们尝试访问的安全资源**。
如果我们需要始终重定向到一个特定的URL，我们可以通过特定的HttpSecurity配置强制重定向。

## 3. AuthenticationSuccessHandler

在基于表单的身份验证中，重定向在登录后立即发生，这是在Spring Security的AuthenticationSuccessHandler实例中处理的。

框架提供了三个默认实现：SimpleUrlAuthenticationSuccessHandler、SavedRequestAwareAuthenticationSuccessHandler和ForwardAuthenticationSuccessHandler。
在本文中我们重点介绍前两个实现。

### 3.1 SavedRequestAwareAuthenticationSuccessHandler

SavedRequestAwareAuthenticationSuccessHandler使用存储在会话中的已保存请求。成功登录后，用户将被重定向到原始请求中保存的URL。

对于表单登录，SavedRequestAwareAuthenticationSuccessHandler用作默认的AuthenticationSuccessHandler。

```java

@Configuration
@EnableWebSecurity
public class RedirectionSecurityConfig {

    //...

    @Override
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/login*")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin();
        return http.build();
    }
}
```

等效的XML配置为：

```xml

<http>
    <intercept-url pattern="/login" access="permitAll"/>
    <intercept-url pattern="/**" access="isAuthenticated()"/>
    <form-login/>
</http>
```

假设我们有一个受保护的端点“/secured”，对于第一次访问该端点，我们将被重定向到登录页面；填写用户凭据并提交登录表单后，我们将被重定向回最初请求的URL：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration({"/RedirectionWebSecurityConfig.xml"})
@WebAppConfiguration
class RedirectionSecurityIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserDetailsService userDetailsService;

    private MockMvc mvc;
    private UserDetails userDetails;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        userDetails = userDetailsService.loadUserByUsername("user1");
    }

    @Test
    void givenAccessSecuredResource_whenAuthenticated_thenRedirectedBack() throws Exception {
        MockHttpServletRequestBuilder securedResourceAccess = get("/secured");
        MvcResult unauthenticatedResult = mvc.perform(securedResourceAccess)
                .andExpect(status().is3xxRedirection())
                .andReturn();

        MockHttpSession session = (MockHttpSession) unauthenticatedResult.getRequest()
                .getSession();
        String loginUrl = unauthenticatedResult.getResponse()
                .getRedirectedUrl();
        mvc.perform(post(loginUrl).param("username", userDetails.getUsername())
                        .param("password", userDetails.getPassword())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/secured"))
                .andReturn();

        mvc.perform(securedResourceAccess.session(session))
                .andExpect(status().isOk());
    }
}
```

### 3.2 SimpleUrlAuthenticationSuccessHandler

与SavedRequestAwareAuthenticationSuccessHandler相比，SimpleUrlAuthenticationSuccessHandler为我们提供了更多关于重定向决策的选项。

我们可以通过setUserReferer(true)启用基于Referer的重定向：

```java
public class RefererAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    public RefererAuthenticationSuccessHandler() {
        super();
        setUseReferer(true);
    }
}
```

然后将其用作RedirectionSecurityConfig中的AuthenticationSuccessHandler：

```java
public class RedirectionSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/login*")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .successHandler(new RefererAuthenticationSuccessHandler());
        return http.build();
    }
}
```

对于XML配置：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans:beans xmlns="http://www.springframework.org/schema/security"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xmlns:beans="http://www.springframework.org/schema/beans"
             xsi:schemaLocation="
		http://www.springframework.org/schema/security
        https://www.springframework.org/schema/security/spring-security.xsd
		http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">
    <http use-expressions="true">
        <intercept-url pattern="/login*" access="permitAll"/>
        <intercept-url pattern="/**" access="isAuthenticated()"/>

        <form-login authentication-success-handler-ref="refererHandler"/>
    </http>

    <beans:bean class="cn.tuyucheng.security.RefererAuthenticationSuccessHandler" name="refererHandler"/>
</beans:beans>
```

### 3.3 背后原理

Spring Security中这些易于使用的特性没有什么神奇之处。当请求安全资源时，该请求将被一系列过滤器过滤，检查身份验证主体和权限。
如果请求会话尚未通过身份验证，则会抛出AuthenticationException。

AuthenticationException将在ExceptionTranslationFilter中捕获，在该过滤器中将启动身份验证过程，从而重定向到登录页面。

```java
public class ExceptionTranslationFilter extends GenericFilterBean {

    //...

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        //...
        handleSpringSecurityException(request, response, chain, ase);
        //...
    }

    private void handleSpringSecurityException(HttpServletRequest request, HttpServletResponse response,
                                               FilterChain chain, RuntimeException exception) throws IOException, ServletException {

        if (exception instanceof AuthenticationException) {
            sendStartAuthentication(request, response, chain, (AuthenticationException) exception);
        }
        //...
    }

    protected void sendStartAuthentication(HttpServletRequest request,
                                           HttpServletResponse response, FilterChain chain,
                                           AuthenticationException reason) throws ServletException, IOException {

        SecurityContextHolder.getContext().setAuthentication(null);
        requestCache.saveRequest(request, response);
        authenticationEntryPoint.commence(request, response, reason);
    }
    //...
}
```

登录后，我们可以在AuthenticationSuccessHandler中自定义行为，如上所示。

## 4. 总结

在这个Spring Security案例中，我们介绍了登录后重定向的常见做法，并演示了使用Spring Security的实现。

注意，**如果没有应用验证或额外的方法控制，我们提到的所有实现都容易受到某些攻击，此类攻击可能会将用户重定向到恶意网站**。

OWASP提供了一份文档来帮助我们处理未经验证的重定向和转发，如果我们需要自己构建实现，这将有很大帮助。