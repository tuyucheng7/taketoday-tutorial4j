## 1. 概述

在本文中，我们将通过在标准登录表单中添加一个额外的字段来使用Spring Security实现自定义身份验证场景。

重点介绍两种不同的方法，以展示框架的多功能性和灵活的使用方法。

第一种方法是一个简单的解决方案，它侧重于重用现有的核心Spring Security实现。

第二种方法是一种更为自定义的解决方案，可能更适合高级用例。

## 2. Maven依赖

```text
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.6.1</version>
    <relativePath/>
</parent>
 
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
     </dependency>
    <dependency>
        <groupId>org.thymeleaf.extras</groupId>
        <artifactId>thymeleaf-extras-springsecurity5</artifactId>
    </dependency>
</dependencies>
```

## 3. 项目配置

在我们的第一种方法中，我们重点关注重用Spring Security提供的实现。
特别是，我们将重用DaoAuthenticationProvider和UsernamePasswordToken，因为它们的“开箱即用”的。

主要组成部分包括：

+ **SimpleAuthenticationFilter** – UsernamePasswordAuthenticationFilter的子类。
+ **SimpleUserDetailsService** – UserDetailsService的实现。
+ **User** – Spring Security提供的User类的子类，用于声明我们的额外domain字段。
+ **声明安全规则并注入依赖bean** - SecurityConfig 我们的Spring，Security配置类，将SimpleAuthenticationFilter插入过滤器链。
+ **login.html** – 一个收集username、password和domain的登录页面。

### 3.1 SimpleAuthenticationFilter

在我们的SimpleAuthenticationFilter中，**domain和username字段是从HttpServletRequest对象中获取的**。
我们拼接这些值并使用它们来创建UsernamePasswordAuthenticationToken的实例。

然后将token传递给AuthenticationProvider进行身份验证：

```java
public class SimpleAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public static final String SPRING_SECURITY_FORM_DOMAIN_KEY = "domain";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        UsernamePasswordAuthenticationToken authRequest = getAuthRequest(request);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private UsernamePasswordAuthenticationToken getAuthRequest(HttpServletRequest request) {
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        String domain = obtainDomain(request);

        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        if (domain == null) {
            domain = "";
        }

        String usernameDomain = String.format("%s%s%s", username.trim(), String.valueOf(Character.LINE_SEPARATOR), domain);
        return new UsernamePasswordAuthenticationToken(usernameDomain, password);
    }

    private String obtainDomain(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_DOMAIN_KEY);
    }
}
```

### 3.2 SimpleUserDetailsService

UserDetailsService定义了一个名为loadUserByUsername的方法。
我们的实现获取username和domain，然后将这些值传递给我们的UserRepository以获取User：

```java

@Service("userDetailsService")
public class SimpleUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public SimpleUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String[] usernameAndDomain = StringUtils.split(username, String.valueOf(Character.LINE_SEPARATOR));
        if (usernameAndDomain == null || usernameAndDomain.length != 2) {
            throw new UsernameNotFoundException("Username and domain must be provided");
        }
        User user = userRepository.findUser(usernameAndDomain[0], usernameAndDomain[1]);
        if (user == null) {
            throw new UsernameNotFoundException(
                    String.format("Username not found for domain, username=%s, domain=%s", usernameAndDomain[0], usernameAndDomain[1]));
        }
        return user;
    }
}
```

### 3.3 Spring Security配置

我们的配置与标准Spring Security配置不同，因为我们通过调用addFilterBefore将SimpleAuthenticationFilter插入到默认值之前的过滤器链中：

```java
public class SecurityConfig extends AbstractHttpConfigurer<SecurityConfig, HttpSecurity> {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        http.addFilterBefore(authenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
    }
}
```

我们能够使用提供的DaoAuthenticationProvider，因为我们使用SimpleUserDetailsService对其进行了配置。
回想一下，我们的SimpleUserDetailsService知道如何解析出我们的username和domain字段，并返回适当的用户以在身份验证时使用：

```java
public class SecurityConfig extends AbstractHttpConfigurer<SecurityConfig, HttpSecurity> {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
```

由于我们使用的是SimpleAuthenticationFilter，因此我们配置了自己的AuthenticationFailureHandler，以确保正确处理失败的登录尝试：

```java
public class SecurityConfig extends AbstractHttpConfigurer<SecurityConfig, HttpSecurity> {

    public SimpleAuthenticationFilter authenticationFilter(AuthenticationManager authenticationManager) throws Exception {
        SimpleAuthenticationFilter filter = new SimpleAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationFailureHandler(failureHandler());
        return filter;
    }

    public SimpleUrlAuthenticationFailureHandler failureHandler() {
        return new SimpleUrlAuthenticationFailureHandler("/login?error=true");
    }
}
```

### 3.4 登录页面

我们使用的登录页面用于提交由SimpleAuthenticationFilter获取的额外domain字段：

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org" lang="en">
<head>
    <title>Login page</title>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-/Y6pD6FV/Vv2HJnA6t+vslU6fwYXjCFtcEpHbNJ0lyAFsXTsjBbfaDjzALeQsN6M" crossorigin="anonymous">
    <link href="http://getbootstrap.com/docs/4.0/examples/signin/signin.css" rel="stylesheet" crossorigin="anonymous"/>
    <link rel="stylesheet" href="/css/main.css" th:href="@{/css/main.css}"/>
</head>
<body>
<div class="container">
    <form class="form-signin" th:action="@{/login}" method="post">
        <h2 class="form-signin-heading">Please sign in</h2>
        <p>Example: user / domain / password</p>
        <p th:if="${param.error}" class="error">Invalid user, password, or domain</p>
        <p>
            <label for="username" class="sr-only">Username</label>
            <input type="text" id="username" name="username" class="form-control" placeholder="Username" required
                   autofocus/>
        </p>
        <p>
            <label for="domain" class="sr-only">Domain</label>
            <input type="text" id="domain" name="domain" class="form-control" placeholder="Domain" required autofocus/>
        </p>
        <p>
            <label for="password" class="sr-only">Password</label>
            <input type="password" id="password" name="password" class="form-control" placeholder="Password" required
                   autofocus/>
        </p>
        <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
        <br/>
        <p><a href="/index" th:href="@{/index}">Back to home page</a></p>
    </form>
</div>
</body>
</html>
```

当我们运行应用程序并访问http://localhost:8081时，我们会看到一个访问受保护页面的链接，
单击该链接将显示登录页面。正如预期的那样，我们可以看到额外domain字段：

<img src="../assets/img.png">

### 3.5 概括

在我们的第一个示例中，通过“伪造”username字段，我们能够重用DaoAuthenticationProvider和UsernamePasswordAuthenticationToken。

因此，我们能够以最少的配置和代码添加对额外字段的支持。

## 4. 自定义项目配置

我们的第二种方法与第一种方法非常相似，但可能更适合场景特定的用例。

其关键组成部分包括：

+ **CustomAuthenticationFilter** – UsernamePasswordAuthenticationFilter的子类。
+ **CustomUserDetailsService** – 声明loadUserByUsernameAndDomain方法的自定义接口。
+ **CustomUserDetailsServiceImpl** – CustomUserDetailsService的实现。
+ **CustomUserDetailsAuthenticationProvider** – AbstractUserDetailsAuthenticationProvider的子类。
+ **CustomAuthenticationToken** – UsernamePasswordAuthenticationToken的子类。
+ **User** – Spring Security提供的User类的子类，它声明了我们的额外domain字段。
+ **SecurityConfig** - Spring Security配置类，将CustomAuthenticationFilter插入过滤器链，声明安全规则并注入依赖bean。
+ **login.html** – 收集username、password和domain的登录页面。

### 4.1 CustomAuthenticationFilter

在我们的CustomAuthenticationFilter中，我们从请求中获取username、password和domain字段。
这些值用于创建CustomAuthenticationToken的实例，该实例被传递给AuthenticationProvider进行身份验证：

```java
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public static final String SPRING_SECURITY_FORM_DOMAIN_KEY = "domain";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        CustomAuthenticationToken authRequest = getAuthRequest(request);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private CustomAuthenticationToken getAuthRequest(HttpServletRequest request) {
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        String domain = obtainDomain(request);

        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        if (domain == null) {
            domain = "";
        }

        return new CustomAuthenticationToken(username, password, domain);
    }

    private String obtainDomain(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_DOMAIN_KEY);
    }
}
```

### 4.2 CustomUserDetailsService

我们自定义的CustomUserDetailsService接口定义了一个名为loadUserByUsernameAndDomain的方法。

其实现类CustomUserDetailsServiceImpl简单地实现该方法并委托给我们的CustomUserRepository以获取User：

```java

@Service("userDetailsService")
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsernameAndDomain(String username, String domain) throws UsernameNotFoundException {
        if (StringUtils.isAnyBlank(username, domain)) {
            throw new UsernameNotFoundException("Username and domain must be provided");
        }
        User user = userRepository.findUser(username, domain);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("Username not found for domain, username=%s, domain=%s", username, domain));
        }
        return user;
    }
}

@Repository("userRepository")
public class CustomUserRepository implements UserRepository {

    private final PasswordEncoder passwordEncoder;

    public CustomUserRepository(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findUser(String username, String domain) {
        if (StringUtils.isAnyBlank(username, domain)) {
            return null;
        } else {
            Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
            return new User(username, domain, passwordEncoder.encode("secret"),
                    true, true, true, true, authorities);
        }
    }
}
```

### 4.3 CustomUserDetailsAuthenticationProvider

我们的CustomUserDetailsAuthenticationProvider扩展了AbstractUserDetailsAuthenticationProvider，并委托给我们的CustomUserDetailService来检索User。
这个类最重要的功能是retrieveUser方法的实现。

请注意，我们必须将AuthenticationToken强转为CustomAuthenticationToken才能访问我们的自定义domain字段：

```java
public class CustomUserDetailsAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    // ...

    private final CustomUserDetailsService userDetailsService;

    public CustomUserDetailsAuthenticationProvider(PasswordEncoder passwordEncoder, CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        CustomAuthenticationToken auth = (CustomAuthenticationToken) authentication;
        UserDetails loadedUser;

        try {
            loadedUser = this.userDetailsService.loadUserByUsernameAndDomain(auth.getPrincipal().toString(), auth.getDomain());
        } catch (UsernameNotFoundException notFound) {
            if (authentication.getCredentials() != null) {
                String presentedPassword = authentication.getCredentials().toString();
                passwordEncoder.matches(presentedPassword, userNotFoundEncodedPassword);
            }
            throw notFound;
        } catch (Exception repositoryProblem) {
            throw new InternalAuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
        }

        if (loadedUser == null) {
            throw new InternalAuthenticationServiceException("UserDetailsService returned null, " + "which is an interface contract violation");
        }
        return loadedUser;
    }
}
```

### 4.4 CustomAuthenticationToken

对于CustomAuthenticationToken，我们只是简单的继承UsernamePasswordAuthenticationToken，并添加一个额外的字段：

```java
public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final String domain;

    public CustomAuthenticationToken(Object principal, Object credentials, String domain) {
        super(principal, credentials);
        this.domain = domain;
        super.setAuthenticated(false);
    }

    public CustomAuthenticationToken(Object principal, Object credentials, String domain, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.domain = domain;
        super.setAuthenticated(true); // must use super, as we override
    }

    public String getDomain() {
        return this.domain;
    }
}
```

### 4.5 概括

第二种方法与之前介绍的简单方法几乎相同，
通过实现我们自己的AuthenticationProvider和CustomAuthenticationToken，我们避免了使用自定义解析逻辑调整我们的username字段。

## 5. 总结

在本文中，我们在Spring Security中实现了一个登录表单，它包含了一个额外的domain字段。我们以两种不同的方式实现了这一点：

+ 在简单方法中，我们最大限度地减少了需要编写的代码量。
  **通过使用自定义解析逻辑调整username，我们能够重用DaoAuthenticationProvider和UsernamePasswordAuthentication**。
+ 在更加自定义的方法中，我们通过扩展AbstractUserDetailsAuthenticationProvider并为我们自己的CustomUserDetailsService
  提供CustomAuthenticationToken来提供自定义字段支持。