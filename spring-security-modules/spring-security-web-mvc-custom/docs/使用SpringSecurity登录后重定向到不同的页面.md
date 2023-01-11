## 1. 概述

Web 应用程序的一个常见要求是在登录后将不同类型的用户重定向到不同的页面。例如，将标准用户重定向到/homepage.html页面，将管理员用户重定向到/console.html页面就是一个例子。

本文将展示如何使用 Spring Security 快速安全地实现此机制。这篇文章也是建立在[Spring MVC 教程](https://www.baeldung.com/spring-mvc-tutorial)之上的，该教程涉及设置项目所需的核心 MVC 东西。

## 2. Spring 安全配置

Spring Security 提供了一个组件，该组件直接负责决定认证成功后要做什么—— AuthenticationSuccessHandler。

### 2.1。基本配置

让我们首先配置一个基本的 @Configuration 和@Service类：

```java
@Configuration
@EnableWebSecurity
public class SecSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            // ... endpoints
            .formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/homepage.html", true)
            // ... other configuration   
        return http.build();
    }
}
```

此配置中要关注的部分是defaultSuccessUrl()方法。成功登录后，任何用户都将被重定向到homepage.html。

此外，我们需要配置用户及其角色。出于本文的目的，我们将实现一个简单的UserDetailService，其中包含两个用户，每个用户都有一个角色。有关此主题的更多信息，请阅读我们的文章[Spring Security – Roles and Privileges](https://www.baeldung.com/role-and-privilege-for-spring-security-registration)。

```java
@Service
public class MyUserDetailsService implements UserDetailsService {

    private Map<String, User> roles = new HashMap<>();

    @PostConstruct
    public void init() {
        roles.put("admin2", new User("admin", "{noop}admin1", getAuthority("ROLE_ADMIN")));
        roles.put("user2", new User("user", "{noop}user1", getAuthority("ROLE_USER")));
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return roles.get(username);
    }

    private List<GrantedAuthority> getAuthority(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}

```

另请注意，在这个简单的示例中，我们不会使用密码编码器，因此[密码以{noop}](https://www.baeldung.com/spring-security-5-default-password-encoder)为前缀。

### 2.2. 添加自定义成功处理程序

我们现在有两个具有两个不同角色的用户：user和admin。成功登录后，两者都将被重定向到hompeage.html。让我们看看如何根据用户的角色进行不同的重定向。

首先，我们需要将自定义成功处理程序定义为 bean：

```java
@Bean
public AuthenticationSuccessHandler myAuthenticationSuccessHandler(){
    return new MySimpleUrlAuthenticationSuccessHandler();
}

```

然后将defaultSuccessUrl调用替换为successHandler方法，该方法接受我们自定义的成功处理程序作为参数：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        // endpoints
        .formLogin()
            .loginPage("/login.html")
            .loginProcessingUrl("/login")
            .successHandler(myAuthenticationSuccessHandler())
        // other configuration      
    return http.build();
}

```

### 2.3. XML 配置

在查看我们的自定义成功处理程序的实现之前，我们还要查看等效的 XML 配置：

```xml
<http use-expressions="true" >
    <!-- other configuration -->
    <form-login login-page='/login.html' 
      authentication-failure-url="/login.html?error=true"
      authentication-success-handler-ref="myAuthenticationSuccessHandler"/>
    <logout/>
</http>

<beans:bean id="myAuthenticationSuccessHandler"
  class="com.baeldung.security.MySimpleUrlAuthenticationSuccessHandler" />

<authentication-manager>
    <authentication-provider>
        <user-service>
            <user name="user1" password="{noop}user1Pass" authorities="ROLE_USER" />
            <user name="admin1" password="{noop}admin1Pass" authorities="ROLE_ADMIN" />
        </user-service>
    </authentication-provider>
</authentication-manager>
```

## 3. 自定义身份验证成功处理程序

除了AuthenticationSuccessHandler接口，Spring 还为这个策略组件提供了一个合理的默认值—— AbstractAuthenticationTargetUrlRequestHandler和一个简单的实现—— SimpleUrlAuthenticationSuccessHandler。通常，这些实现将在登录后确定 URL 并执行到该 URL 的重定向。

虽然有些灵活，但确定此目标 URL 的机制不允许以编程方式进行确定——因此我们将实现接口并提供成功处理程序的自定义实现。此实现将根据用户的角色确定用户登录后重定向到的 URL。 

首先，我们需要重写onAuthenticationSuccess方法：

```java
public class MySimpleUrlAuthenticationSuccessHandler
  implements AuthenticationSuccessHandler {
 
    protected Log logger = LogFactory.getLog(this.getClass());

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
      HttpServletResponse response, Authentication authentication)
      throws IOException {
 
        handle(request, response, authentication);
        clearAuthenticationAttributes(request);
    }

```

我们自定义的方法调用了两个辅助方法：

```java
protected void handle(
        HttpServletRequest request,
        HttpServletResponse response, 
        Authentication authentication
) throws IOException {

    String targetUrl = determineTargetUrl(authentication);

    if (response.isCommitted()) {
        logger.debug(
                "Response has already been committed. Unable to redirect to "
                        + targetUrl);
        return;
    }

    redirectStrategy.sendRedirect(request, response, targetUrl);
}

```

以下方法执行实际工作并将用户映射到目标 URL：

```java
protected String determineTargetUrl(final Authentication authentication) {

    Map<String, String> roleTargetUrlMap = new HashMap<>();
    roleTargetUrlMap.put("ROLE_USER", "/homepage.html");
    roleTargetUrlMap.put("ROLE_ADMIN", "/console.html");

    final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    for (final GrantedAuthority grantedAuthority : authorities) {
        String authorityName = grantedAuthority.getAuthority();
        if(roleTargetUrlMap.containsKey(authorityName)) {
            return roleTargetUrlMap.get(authorityName);
        }
    }

    throw new IllegalStateException();
}

```

请注意，此方法将返回用户拥有的第一个角色的映射 URL。因此，如果用户具有多个角色，则映射的 URL 将与权限集合中给出的第一个角色匹配。

```java
protected void clearAuthenticationAttributes(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session == null) {
        return;
    }
    session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
}
```

determineTargetUrl - 这是该策略的核心 - 只需查看用户类型(由权限确定)并根据此角色选择目标 URL。

因此，由ROLE_ADMIN权限确定的管理员用户在登录后将被重定向到控制台页面，而由ROLE_USER确定的标准用户将被重定向到主页。

## 4. 总结

与往常一样，本文中提供的代码可[在 GitHub 上](https://github.com/eugenp/tutorials/tree/master/spring-security-modules/spring-security-web-mvc-custom)获得。这是一个基于 Maven 的项目，因此它应该很容易导入和运行。