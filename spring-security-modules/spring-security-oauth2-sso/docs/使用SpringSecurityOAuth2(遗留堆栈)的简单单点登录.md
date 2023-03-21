## 1. 概述

在本教程中，我们将讨论如何使用Spring Security OAuth和Spring Boot实现SSO-单点登录。

我们将使用三个独立的应用程序：

-   授权服务器：作为中央身份验证机制
-   两个客户端应用程序：使用SSO的应用程序

简而言之，当用户尝试访问客户端应用程序中的安全页面时，他们将被重定向到首先通过身份验证服务器进行身份验证。

我们将使用OAuth2中的授权代码授权类型来驱动身份验证委托。

**注意**：本文使用的是[Spring OAuth遗留项目](https://spring.io/projects/spring-security-oauth)。对于使用新Spring Security 5堆栈的本文版本，请查看我们的文章[使用Spring Security OAuth2进行简单单点登录](https://www.baeldung.com/sso-spring-security-oauth2)。

## 2. 客户端应用程序

让我们从我们的客户端应用程序开始；当然，我们将使用Spring Boot来最小化配置：

### 2.1 Maven依赖项

首先，我们的pom.xml中需要以下依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.security.oauth.boot</groupId>
    <artifactId>spring-security-oauth2-autoconfigure</artifactId>
    <version>2.0.1.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity4</artifactId>
</dependency>
```

### 2.2 安全配置

接下来是最重要的部分，我们的客户端应用程序的安全配置：

```java
@Configuration
@EnableOAuth2Sso
public class UiSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**")
              .authorizeRequests()
              .antMatchers("/", "/login**")
              .permitAll()
              .anyRequest()
              .authenticated();
    }
}
```

当然，此配置的核心部分是我们用于启用单点登录的[@EnableOAuth2Sso](https://docs.spring.io/spring-boot/docs/1.5.7.RELEASE/api/org/springframework/boot/autoconfigure/security/oauth2/client/EnableOAuth2Sso.html)注解。

请注意，我们需要扩展WebSecurityConfigurerAdapter-没有它，所有路径都将受到保护，因此当用户尝试访问任何页面时都将被重定向到登录。在我们的例子中，首页和登录页面是唯一无需身份验证即可访问的页面。

最后，我们还定义了一个RequestContextListener bean来处理请求作用域。

和application.yml：

```yaml
server:
    port: 8082
    servlet:
        context-path: /ui
    session:
        cookie:
            name: UISESSION
security:
    basic:
        enabled: false
    oauth2:
        client:
            clientId: SampleClientId
            clientSecret: secret
            accessTokenUri: http://localhost:8081/auth/oauth/token
            userAuthorizationUri: http://localhost:8081/auth/oauth/authorize
        resource:
            userInfoUri: http://localhost:8081/auth/user/me
spring:
    thymeleaf:
        cache: false
```

一些快速说明：

-   我们禁用了默认的基本身份验证
-   accessTokenUri是获取访问令牌的URI
-   userAuthorizationUri是用户将被重定向到的授权URI
-   userInfoUri获取当前用户详细信息的用户端点的URI

另请注意，在我们的示例中，我们推出了授权服务器，但当然我们也可以使用其他第三方提供商，例如Facebook或GitHub。

### 2.3 前端

现在，让我们看一下我们的客户端应用程序的前端配置。我们不打算在这里重点讨论，主要是因为我们已经在[网站上进行了介绍](https://www.baeldung.com/spring-thymeleaf-3)。

我们这里的客户端应用程序有一个非常简单的前端；这是index.html：

```html
<h1>Spring Security SSO</h1>
<a href="securedPage">Login</a>
```

和securedPage.html：

```html
<h1>Secured Page</h1>
Welcome, <span th:text="${#authentication.name}">Name</span>
```

securePage.html页面需要对用户进行身份验证。如果未经身份验证的用户尝试访问securePage.html，他们将首先被重定向到登录页面。

## 3. 认证服务器

现在让我们在这里讨论我们的授权服务器。

### 3.1 Maven依赖项

首先，我们需要在pom.xml中定义依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.security.oauth</groupId>
    <artifactId>spring-security-oauth2</artifactId>
    <version>2.3.3.RELEASE</version>
</dependency>
```

### 3.2 OAuth配置

重要的是要了解我们将在这里将授权服务器和资源服务器作为一个可部署单元一起运行。

让我们从资源服务器的配置开始-它兼作我们的主要Boot应用程序：

```java
@SpringBootApplication
@EnableResourceServer
public class AuthorizationServerApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(AuthorizationServerApplication.class, args);
    }
}
```

然后，我们将配置授权服务器：

```java
@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("permitAll()")
              .checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
              .withClient("SampleClientId")
              .secret(passwordEncoder.encode("secret"))
              .authorizedGrantTypes("authorization_code")
              .scopes("user_info")
              .autoApprove(true)
              .redirectUris("http://localhost:8082/ui/login","http://localhost:8083/ui2/login");
    }
}
```

请注意，我们如何仅启用使用authorization_code授权类型的简单客户端。

另外，请注意autoApprove是如何设置为true的，这样我们就不会被重定向和提升为手动批准任何范围。

### 3.3 安全配置

首先，我们将通过application.properties禁用默认的基本身份验证：

```properties
server.port=8081
server.servlet.context-path=/auth
```

现在，让我们转到配置并定义一个简单的表单登录机制：

```java
@Configuration
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requestMatchers()
              .antMatchers("/login", "/oauth/authorize")
              .and()
              .authorizeRequests()
              .anyRequest().authenticated()
              .and()
              .formLogin().permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
              .withUser("john")
              .password(passwordEncoder().encode("123"))
              .roles("USER");
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
```

请注意，我们使用了简单的内存中身份验证，但我们可以简单地将其替换为自定义UserDetailsService。

### 3.4 用户端点

最后，我们将创建我们之前在配置中使用的用户端点：

```java
@RestController
public class UserController {
    @GetMapping("/user/me")
    public Principal user(Principal principal) {
        return principal;
    }
}
```

自然地，这将返回带有JSON表示的用户数据。

## 4. 总结

在这个快速教程中，我们重点介绍了使用Spring Security Oauth2和Spring Boot实现单点登录。