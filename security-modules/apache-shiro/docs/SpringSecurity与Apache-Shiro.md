## 1. 概述

安全性是应用程序开发领域的主要关注点，尤其是在企业 Web 和移动应用程序领域。

在本快速教程中，我们将比较两个流行的Java安全框架——Apache [Shiro](https://shiro.apache.org/)和[Spring Security](https://spring.io/projects/spring-security)。

## 2. 一点背景

Apache Shiro 于 2004 年作为 JSecurity 诞生，并于 2008 年被 Apache 基金会接受。迄今为止，它已经发布了很多版本，截至撰写本文时最新版本是 1.5.3。

Spring Security 于 2003 年作为 Acegi 开始，并于 2008 年首次公开发布时被纳入 Spring Framework。自成立以来，它经历了多次迭代，目前的 GA 版本是 5.3.2。

这两种技术都提供身份验证和授权支持以及加密和会话管理解决方案。此外，Spring Security 提供一流的保护，以抵御 CSRF 和会话固定等攻击。

在接下来的几节中，我们将看到这两种技术如何处理身份验证和授权的示例。为了简单起见，我们将使用基于Spring Boot的基本 MVC 应用程序和[FreeMarker 模板](https://www.baeldung.com/spring-template-engines#4-freemarker-in-spring-boot)。

## 3.配置Apache Shiro

首先，让我们看看这两个框架之间的配置有何不同。

### 3.1. Maven 依赖项

由于我们将在Spring Boot应用程序中使用 Shiro，因此我们需要它的启动器和shiro-core模块：

```xml
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-spring-boot-web-starter</artifactId>
    <version>1.5.3</version>
</dependency>
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-core</artifactId>
    <version>1.5.3</version>
</dependency>
```

最新版本可以在[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.shiro")上找到。

### 3.2. 创建领域

要在内存中声明具有角色和权限的用户，我们需要创建一个扩展 Shiro 的JdbcRealm的领域。我们将定义两个用户——Tom 和 Jerry，分别具有 USER 和 ADMIN 角色：

```java
public class CustomRealm extends JdbcRealm {

    private Map<String, String> credentials = new HashMap<>();
    private Map<String, Set> roles = new HashMap<>();
    private Map<String, Set> permissions = new HashMap<>();

    {
        credentials.put("Tom", "password");
        credentials.put("Jerry", "password");

        roles.put("Jerry", new HashSet<>(Arrays.asList("ADMIN")));
        roles.put("Tom", new HashSet<>(Arrays.asList("USER")));

        permissions.put("ADMIN", new HashSet<>(Arrays.asList("READ", "WRITE")));
        permissions.put("USER", new HashSet<>(Arrays.asList("READ")));
    }
}
```

接下来，要启用此身份验证和授权的检索，我们需要重写几个方法：

```java
@Override
protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) 
  throws AuthenticationException {
    UsernamePasswordToken userToken = (UsernamePasswordToken) token;

    if (userToken.getUsername() == null || userToken.getUsername().isEmpty() ||
      !credentials.containsKey(userToken.getUsername())) {
        throw new UnknownAccountException("User doesn't exist");
    }
    return new SimpleAuthenticationInfo(userToken.getUsername(), 
      credentials.get(userToken.getUsername()), getName());
}

@Override
protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    Set roles = new HashSet<>();
    Set permissions = new HashSet<>();

    for (Object user : principals) {
        try {
            roles.addAll(getRoleNamesForUser(null, (String) user));
            permissions.addAll(getPermissions(null, null, roles));
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }
    SimpleAuthorizationInfo authInfo = new SimpleAuthorizationInfo(roles);
    authInfo.setStringPermissions(permissions);
    return authInfo;
}

```

doGetAuthorizationInfo方法使用几个辅助方法来获取用户的角色和权限：

```java
@Override
protected Set getRoleNamesForUser(Connection conn, String username) 
  throws SQLException {
    if (!roles.containsKey(username)) {
        throw new SQLException("User doesn't exist");
    }
    return roles.get(username);
}

@Override
protected Set getPermissions(Connection conn, String username, Collection roles) 
  throws SQLException {
    Set userPermissions = new HashSet<>();
    for (String role : roles) {
        if (!permissions.containsKey(role)) {
            throw new SQLException("Role doesn't exist");
        }
        userPermissions.addAll(permissions.get(role));
    }
    return userPermissions;
}

```

接下来，我们需要将此CustomRealm作为 bean 包含在我们的引导应用程序中：

```java
@Bean
public Realm customRealm() {
    return new CustomRealm();
}
```

此外，要为我们的端点配置身份验证，我们需要另一个 bean：

```java
@Bean
public ShiroFilterChainDefinition shiroFilterChainDefinition() {
    DefaultShiroFilterChainDefinition filter = new DefaultShiroFilterChainDefinition();

    filter.addPathDefinition("/home", "authc");
    filter.addPathDefinition("/", "anon");
    return filter;
}
```

在这里，使用DefaultShiroFilterChainDefinition实例，我们指定我们的/home端点只能由经过身份验证的用户访问。

这就是我们配置所需的全部，Shiro 为我们完成剩下的工作。

## 4.配置Spring Security

现在让我们看看如何在 Spring 中实现相同的目的。

### 4.1. Maven 依赖项

首先，依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

最新版本可以在[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter")上找到。

### 4.2. 配置类

接下来，我们将在类SecurityConfig中定义我们的 Spring Security 配置：

```java
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
            .disable()
            .authorizeRequests(authorize -> authorize.antMatchers("/index", "/login")
                .permitAll()
                .antMatchers("/home", "/logout")
                .authenticated()
                .antMatchers("/admin/")
                .hasRole("ADMIN"))
            .formLogin(formLogin -> formLogin.loginPage("/login")
                .failureUrl("/login-error"));
        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() throws Exception {
        UserDetails jerry = User.withUsername("Jerry")
            .password(passwordEncoder().encode("password"))
            .authorities("READ", "WRITE")
            .roles("ADMIN")
            .build();
        UserDetails tom = User.withUsername("Tom")
            .password(passwordEncoder().encode("password"))
            .authorities("READ")
            .roles("USER")
            .build();
        return new InMemoryUserDetailsManager(jerry, tom);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

```

如我们所见，我们构建了一个UserDetails 对象来声明我们的用户及其角色和权限。此外，我们使用BCryptPasswordEncoder对密码进行编码。

Spring Security 还为我们提供了HttpSecurity对象以供进一步配置。对于我们的示例，我们允许：

-   每个人都可以访问我们的索引和登录页面
-   只有经过身份验证的用户才能进入主页和注销
-   只有具有 ADMIN 角色的用户才能访问管理页面

我们还定义了对基于表单的身份验证的支持，以将用户发送到登录端点。如果登录失败，我们的用户将被重定向到/login-error。

## 5.控制器和端点

现在让我们看一下这两个应用程序的 Web 控制器映射。虽然它们将使用相同的端点，但某些实现会有所不同。

### 5.1. 视图渲染的端点

对于渲染视图的端点，实现是相同的：

```java
@GetMapping("/")
public String index() {
    return "index";
}

@GetMapping("/login")
public String showLoginPage() {
    return "login";
}

@GetMapping("/home")
public String getMeHome(Model model) {
    addUserAttributes(model);
    return "home";
}
```

我们的控制器实现，Shiro 和 Spring Security，都在根端点上返回index.ftl ，在登录端点上返回 login.ftl ，在 home 端点上返回 home.ftl 。

但是， /home端点上的方法addUserAttributes的定义在两个控制器之间会有所不同。此方法内省当前登录用户的属性。

Shiro 提供了一个SecurityUtils#getSubject来检索当前的Subject及其角色和权限：

```java
private void addUserAttributes(Model model) {
    Subject currentUser = SecurityUtils.getSubject();
    String permission = "";

    if (currentUser.hasRole("ADMIN")) {
        model.addAttribute("role", "ADMIN");
    } else if (currentUser.hasRole("USER")) {
        model.addAttribute("role", "USER");
    }
    if (currentUser.isPermitted("READ")) {
        permission = permission + " READ";
    }
    if (currentUser.isPermitted("WRITE")) {
        permission = permission + " WRITE";
    }
    model.addAttribute("username", currentUser.getPrincipal());
    model.addAttribute("permission", permission);
}
```

另一方面，Spring Security为此目的从其SecurityContextHolder的上下文中提供了一个Authentication对象：

```java
private void addUserAttributes(Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && !auth.getClass().equals(AnonymousAuthenticationToken.class)) {
        User user = (User) auth.getPrincipal();
        model.addAttribute("username", user.getUsername());
        Collection<GrantedAuthority> authorities = user.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().contains("USER")) {
                model.addAttribute("role", "USER");
                model.addAttribute("permissions", "READ");
            } else if (authority.getAuthority().contains("ADMIN")) {
                model.addAttribute("role", "ADMIN");
                model.addAttribute("permissions", "READ WRITE");
            }
        }
    }
}
```

### 5.2. POST 登录端点

在 Shiro 中，我们将用户输入的凭据映射到 POJO：

```java
public class UserCredentials {

    private String username;
    private String password;

    // getters and setters
}
```

然后我们将创建一个UsernamePasswordToken来记录用户或Subject，在：

```java
@PostMapping("/login")
public String doLogin(HttpServletRequest req, UserCredentials credentials, RedirectAttributes attr) {

    Subject subject = SecurityUtils.getSubject();
    if (!subject.isAuthenticated()) {
        UsernamePasswordToken token = new UsernamePasswordToken(credentials.getUsername(),
          credentials.getPassword());
        try {
            subject.login(token);
        } catch (AuthenticationException ae) {
            logger.error(ae.getMessage());
            attr.addFlashAttribute("error", "Invalid Credentials");
            return "redirect:/login";
        }
    }
    return "redirect:/home";
}
```

在 Spring Security 方面，这只是重定向到主页的问题。Spring 的登录过程由其UsernamePasswordAuthenticationFilter处理，对我们来说是透明的：

```java
@PostMapping("/login")
public String doLogin(HttpServletRequest req) {
    return "redirect:/home";
}
```

### 5.3. 仅限管理员端点

现在让我们看一下我们必须执行基于角色的访问的场景。假设我们有一个/admin端点，应该只允许 ADMIN 角色访问它。

让我们看看如何在 Shiro 中做到这一点：

```java
@GetMapping("/admin")
public String adminOnly(ModelMap modelMap) {
    addUserAttributes(modelMap);
    Subject currentUser = SecurityUtils.getSubject();
    if (currentUser.hasRole("ADMIN")) {
        modelMap.addAttribute("adminContent", "only admin can view this");
    }
    return "home";
}
```

在这里，我们提取了当前登录的用户，检查他们是否具有 ADMIN 角色，并相应地添加了内容。

在 Spring Security 中，无需以编程方式检查角色，我们已经在SecurityConfig中定义了谁可以访问此端点。所以现在，只需添加业务逻辑即可：

```java
@GetMapping("/admin")
public String adminOnly(HttpServletRequest req, Model model) {
    addUserAttributes(model);
    model.addAttribute("adminContent", "only admin can view this");
    return "home";
}
```

### 5.4. 注销端点

最后，让我们实现注销端点。

在 Shiro 中，我们将简单地调用Subject#logout：

```java
@PostMapping("/logout")
public String logout() {
    Subject subject = SecurityUtils.getSubject();
    subject.logout();
    return "redirect:/";
}
```

对于 Spring，我们没有为注销定义任何映射。在这种情况下，它的默认注销机制会启动，自从我们在配置中创建了一个SecurityFilterChain bean 后，它就会自动应用。

## 6. Apache Shiro 与 Spring Security

现在我们已经了解了实施差异，让我们看看其他几个方面。

在社区支持方面，Spring Framework 总体上拥有庞大的开发者社区，积极参与其开发和使用。由于 Spring Security 是保护伞的一部分，它必须享有相同的优势。Shiro 虽然很受欢迎，但并没有得到如此巨大的支持。

关于文档，Spring 再次成为赢家。

但是，Spring Security 有一些学习曲线。另一方面，白很容易理解。对于桌面应用程序，通过[shiro.ini](https://shiro.apache.org/configuration.html#Configuration-CreatingaSecurityManagerfromINI)进行配置更加容易。

但同样，正如我们在示例片段中看到的那样， Spring Security 在保持业务逻辑和安全性分离方面做得很好， 并真正将安全性作为一个横切关注点提供。

## 七. 总结

在本教程中，我们将 Apache Shiro 与 Spring Security 进行了比较。

我们只是触及了这些框架所提供的功能的皮毛，还有很多东西需要进一步探索。有很多替代方案，例如[JAAS](https://docs.oracle.com/en/java/javase/11/security/java-authentication-and-authorization-service-jaas-reference-guide.html)和[OACC](http://oaccframework.org/)。不过，凭借其优势，[Spring Security](https://www.baeldung.com/security-spring)似乎在这一点上取得了胜利。