## 1. 概述

Spring Security允许通过扩展WebSecurityConfigurerAdapter类为功能自定义HTTP安全性，例如端点授权或身份验证管理器配置。然而，在最近的版本中，Spring弃用了这种方法，并鼓励基于组件的安全配置。

在本教程中，我们将学习如何在Spring Boot应用程序中替换此弃用并运行一些MVC测试。

## 延伸阅读

## [Spring中弃用的类](https://www.baeldung.com/spring-deprecated-classes)

探索Spring和Spring Boot中已弃用的类。

[阅读更多](https://www.baeldung.com/spring-deprecated-classes)→

### [警告：“WebMvcConfigurerAdapter类型已弃用”](https://www.baeldung.com/web-mvc-configurer-adapter-deprecated)

了解如何修复Spring中的WebMvcConfigurerAdapter警告。

[阅读更多](https://www.baeldung.com/web-mvc-configurer-adapter-deprecated)→

### [Spring Security 5中的默认密码编码器](https://www.baeldung.com/spring-security-5-default-password-encoder)

了解在将明文密码迁移到Spring Security 5时如何避免IllegalArgumentException。

[阅读更多](https://www.baeldung.com/spring-security-5-default-password-encoder)→

## 2. 不使用WebSecurityConfigurerAdapter的Spring Security

**我们通常会使用扩展WebSecurityConfigureAdapter类的Spring [HTTP安全配置](https://www.baeldung.com/java-config-spring-security)类**。

但是，从版本5.7.0-M2开始，Spring不推荐使用WebSecurityConfigureAdapter，并建议通过[不使用WebSecurityConfigureAdapter的方式编写配置](https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter)。

让我们创建一个使用内存中身份验证的示例Spring Boot应用程序来演示这种新型配置。

首先，我们将定义我们的配置类：

```java
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {
    // config...
}
```

我们添加了[方法安全注解](https://www.baeldung.com/spring-security-method-security)以启用基于不同角色的处理。

### 2.1 配置身份验证

使用WebSecurityConfigureAdapter时，我们会使用AuthenticationManagerBuilder来设置我们的身份验证上下文。

**现在，如果我们想避免使用WebSecurityConfigureAdapter，我们可以定义一个UserDetailsManager或UserDetailsService组件**：

```java
@Bean
public UserDetailsService userDetailsService(BCryptPasswordEncoder bCryptPasswordEncoder) {
    InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
    manager.createUser(User.withUsername("user")
          .password(bCryptPasswordEncoder.encode("userPass"))
          .roles("USER")
          .build());
    manager.createUser(User.withUsername("admin")
          .password(bCryptPasswordEncoder.encode("adminPass"))
          .roles("ADMIN", "USER")
          .build());
    return manager;
}

@Bean
public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
}
```

或者，给定我们的UserDetailService，我们甚至可以设置一个AuthenticationManager：

```java
@Bean
public AuthenticationManager authManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailsService userDetailService) throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class)
        .userDetailsService(userDetailService)
        .passwordEncoder(bCryptPasswordEncoder)
        .and()
        .build();
}
```

同样，如果我们使用JDBC或LDAP身份验证，这将起作用。

### 2.2 配置HTTP安全

**更重要的是，如果我们想避免弃用HTTP Security，我们可以创建一个SecurityFilterChain bean**。

例如，假设我们想根据角色保护端点，并保留一个匿名入口点仅用于登录。我们还将任何DELETE请求限制为ADMIN角色，并使用[基本身份验证](https://www.baeldung.com/spring-security-basic-authentication)：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .authorizeRequests()
        .antMatchers(HttpMethod.DELETE)
        .hasRole("ADMIN")
        .antMatchers("/admin/**")
        .hasAnyRole("ADMIN")
        .antMatchers("/user/**")
        .hasAnyRole("USER", "ADMIN")
        .antMatchers("/login/**")
        .anonymous()
        .anyRequest()
        .authenticated()
        .and()
        .httpBasic()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    return http.build();
}
```

**HttpSecurity将构建一个DefaultSecurityFilterChain对象来加载请求匹配器和过滤器**。

### 2.3 配置Web安全

此外，对于Web安全，我们现在可以使用回调接口WebSecurityCustomizer。

我们将添加一个debug级别并忽略一些路径，例如图像或脚本：

```java
@Value("${spring.security.debug:false}")
boolean securityDebug;

@Bean
public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.debug(securityDebug)
        .ignoring()
        .antMatchers("/css/**", "/js/**", "/img/**", "/lib/**", "/favicon.ico");
}
```

## 3. 控制器

下面我们为应用程序定义一个简单的REST控制器类：

```java
@RestController
public class ResourceController {

    @GetMapping("/login")
    public String loginEndpoint() {
        return "Login!";
    }

    @GetMapping("/admin")
    public String adminEndpoint() {
        return "Admin!";
    }

    @GetMapping("/user")
    public String userEndpoint() {
        return "User!";
    }

    @GetMapping("/all")
    public String allRolesEndpoint() {
        return "All Roles!";
    }

    @DeleteMapping("/delete")
    public String deleteEndpoint(@RequestBody String s) {
        return "I am deleting " + s;
    }
}
```

正如我们之前在定义HTTP安全时提到的，我们将添加一个任何人都可以访问的通用/login端点、ADMIN和USER角色的特定端点，以及一个不受角色保护但仍需要身份验证的/all端点。

## 4. 测试

让我们使用MvcMock将新配置添加到[Spring Boot测试](https://www.baeldung.com/spring-boot-testing)来测试我们的端点。

### 4.1 测试匿名用户

匿名用户可以访问/login端点。但是如果他们尝试访问其他内容，返回的结果将是Unauthorized(401)：

```java
@SpringBootTest(classes = SecurityFilterChainApplication.class)
class SecurityFilterChainIntegrationTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
              .apply(springSecurity())
              .build();
    }

    @Test
    @WithAnonymousUser
    void whenAnonymousAccessLogin_thenOk() throws Exception {
        mvc.perform(get("/login"))
              .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void whenAnonymousAccessRestrictedEndpoint_thenIsUnauthorized() throws Exception {
        mvc.perform(get("/all"))
              .andExpect(status().isUnauthorized());
    }
}
```

此外，对于除/login之外的所有端点，我们始终需要身份验证，就像/all端点一样。

### 4.2 测试USER角色

USER角色可以访问通用端点以及我们授予该角色的所有其他路径：

```java
@Test
@WithUserDetails()
void whenUserAccessUserSecuredEndpoint_thenOk() throws Exception {
    mvc.perform(get("/user"))
        .andExpect(status().isOk());
}

@Test
@WithUserDetails()
void whenUserAccessRestrictedEndpoint_thenOk() throws Exception {
    mvc.perform(get("/all"))
        .andExpect(status().isOk());
}

@Test
@WithUserDetails()
void whenUserAccessAdminSecuredEndpoint_thenIsForbidden() throws Exception {
    mvc.perform(get("/admin"))
        .andExpect(status().isForbidden());
}

@Test
@WithUserDetails()
void whenUserAccessDeleteSecuredEndpoint_thenIsForbidden() throws Exception {
    mvc.perform(delete("/delete"))
        .andExpect(status().isForbidden());
}
```

值得注意的是，如果USER角色尝试访问受ADMIN角色保护的端点，则用户会收到“forbidden”(403)错误。

相反，没有凭据的人(例如前面例子中的匿名用户)将收到“Unauthorized”错误(401)。

### 4.3 测试ADMIN角色

根据我们的配置，具有ADMIN角色的人可以访问任何端点：

```java
@Test
@WithUserDetails(value = "admin")
void whenAdminAccessUserEndpoint_thenOk() throws Exception {
    mvc.perform(get("/user"))
        .andExpect(status().isOk());
}

@Test
@WithUserDetails(value = "admin")
void whenAdminAccessAdminSecuredEndpoint_thenIsOk() throws Exception {
    mvc.perform(get("/admin"))
        .andExpect(status().isOk());
}

@Test
@WithUserDetails(value = "admin")
void whenAdminAccessDeleteSecuredEndpoint_thenIsOk() throws Exception {
    mvc.perform(delete("/delete").content("{}"))
        .andExpect(status().isOk());
}
```

## 5. 总结

在本文中，我们学习了如何在不使用WebSecurityConfigureAdapter的情况下创建Spring Security配置，并在创建用于身份验证、HTTP Security和Web Security的组件时替换它。