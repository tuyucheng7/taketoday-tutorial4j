## 1. 概述

在本教程中，我们将专注于将新的 Facebook 登录添加到现有的表单登录应用程序。

我们将使用 Spring Social 支持与 Facebook 交互并保持简洁明了。

## 2.Maven配置

首先，我们需要将spring-social-facebook依赖添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.social</groupId>
    <artifactId>spring-social-facebook</artifactId>
    <version>2.0.3.RELEASE</version>
</dependency>
```

## 3. 安全配置——只需表单登录

让我们首先从简单的安全配置开始，其中我们只有基于表单的身份验证：

```java
@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = { "com.baeldung.security" })
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) 
      throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .csrf().disable()
        .authorizeRequests()
        .antMatchers("/login").permitAll()
        .anyRequest().authenticated()
        .and()
        .formLogin().loginPage("/login").permitAll();
    } 
}
```

我们不会在这个配置上花费太多时间——如果想更好地理解它，请查看[表单登录文章](https://www.baeldung.com/spring-security-login)。

## 4. Facebook 属性

接下来，让我们在application.properties中配置 Facebook 属性：

```bash
spring.social.facebook.appId=YOUR_APP_ID
spring.social.facebook.appSecret=YOUR_APP_SECRET
```

注意：

-   我们需要创建一个 Facebook 应用来获取appId和appSecret
-   在 Facebook 应用程序设置中，确保添加平台“网站”并且http://localhost:8080/是“站点 URL”

## 5. 安全配置 - 添加 Facebook

现在，让我们在系统中添加一种新的身份验证方式——由 Facebook 驱动：

```java
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private FacebookConnectionSignup facebookConnectionSignup;

    @Value("${spring.social.facebook.appSecret}")
    String appSecret;
    
    @Value("${spring.social.facebook.appId}")
    String appId;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .authorizeRequests()
        .antMatchers("/login","/signin/","/signup/").permitAll()
        ...
    } 

    @Bean
    public ProviderSignInController providerSignInController() {
        ConnectionFactoryLocator connectionFactoryLocator = 
            connectionFactoryLocator();
        UsersConnectionRepository usersConnectionRepository = 
            getUsersConnectionRepository(connectionFactoryLocator);
        ((InMemoryUsersConnectionRepository) usersConnectionRepository)
            .setConnectionSignUp(facebookConnectionSignup);
        return new ProviderSignInController(connectionFactoryLocator, 
            usersConnectionRepository, new FacebookSignInAdapter());
    }
    
    private ConnectionFactoryLocator connectionFactoryLocator() {
        ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
        registry.addConnectionFactory(new FacebookConnectionFactory(appId, appSecret));
        return registry;
    }
    
    private UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator 
        connectionFactoryLocator) {
        return new InMemoryUsersConnectionRepository(connectionFactoryLocator);
    }
}
```

让我们仔细看看新的配置：

-   我们使用 ProviderSignInController 来启用 Facebook 身份验证，这需要两件事：
    首先，使用 我们之前定义的 Facebook 属性 注册为 FacebookConnectionFactory的ConnectionFactoryLocator 。 第二，一个 InMemoryUsersConnectionRepository。
-   通过向“ /signin/facebook ”发送POST ——该控制器将使用 Facebook 服务提供商启动用户登录
-   我们正在设置一个SignInAdapter来处理我们应用程序中的登录逻辑
-   我们还设置了ConnectionSignUp以在用户首次使用 Facebook 进行身份验证时隐式处理注册用户

## 6.登录适配器

简单地说，这个适配器是上面的控制器(驱动 Facebook 用户登录流程)和我们特定的本地应用程序之间的桥梁：

```java
public class FacebookSignInAdapter implements SignInAdapter {
    @Override
    public String signIn(
      String localUserId, 
      Connection<?> connection, 
      NativeWebRequest request) {
        
        SecurityContextHolder.getContext().setAuthentication(
          new UsernamePasswordAuthenticationToken(
          connection.getDisplayName(), null, 
          Arrays.asList(new SimpleGrantedAuthority("FACEBOOK_USER"))));
        
        return null;
    }
}
```

请注意，使用 Facebook 登录的用户将拥有FACEBOOK_USER角色，而使用表单登录的用户将拥有USER 角色。


## 7.连接注册

当用户第一次使用 Facebook 进行身份验证时，他们在我们的应用程序中没有现有帐户。

这就是我们需要为他们自动创建该帐户的地方；我们将使用ConnectionSignUp来驱动该用户创建逻辑：

```java
@Service
public class FacebookConnectionSignup implements ConnectionSignUp {

    @Autowired
    private UserRepository userRepository;

    @Override
    public String execute(Connection<?> connection) {
        User user = new User();
        user.setUsername(connection.getDisplayName());
        user.setPassword(randomAlphabetic(8));
        userRepository.save(user);
        return user.getUsername();
    }
}
```

如所见，我们为新用户创建了一个帐户——使用他们的DisplayName作为用户名。

## 8. 前端

最后，让我们看看我们的前端。

我们现在将在我们的登录页面上支持这两个身份验证流程——表单登录和 Facebook：

```html
<html>
<body>
<div th:if="${param.logout}">You have been logged out</div>
<div th:if="${param.error}">There was an error, please try again</div>

<form th:action="@{/login}" method="POST" >
    <input type="text" name="username" />
    <input type="password" name="password" />
    <input type="submit" value="Login" />
</form>
	
<form action="/signin/facebook" method="POST">
    <input type="hidden" name="scope" value="public_profile" />
    <input type="submit" value="Login using Facebook"/>
</form>
</body>
</html>
```

最后——这里是index.html：

```html
<html>
<body>
<nav>
    <p sec:authentication="name">Username</p>      
    <a th:href="@{/logout}">Logout</a>                     
</nav>

<h1>Welcome, <span sec:authentication="name">Username</span></h1>
<p sec:authentication="authorities">User authorities</p>
</body>
</html>
```

请注意此索引页面如何显示用户名和权限。

就是这样——我们现在有两种方法可以在应用程序中进行身份验证。

## 9. 总结

在这篇快速文章中，我们学习了如何使用spring-social-facebook为我们的应用程序实现二级身份验证流程。