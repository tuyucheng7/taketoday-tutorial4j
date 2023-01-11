## 1. 概述

Spring Security允许通过继承WebSecurityConfigurerAdapter类为端点授权或身份验证管理器配置等特性自定义HTTP Security。
然而，从最近的版本开始，Spring弃用了这种方法，并鼓励基于组件的Security配置。

在本教程中，我们将通过一个示例，说明如何在Spring Boot应用程序中使用新的方式，并运行一些MVC测试。

## 2. 不使用WebSecurityConfigurerAdapter的Spring Security

**我们通常会使用继承WebSecurityConfigureAdapter类的Spring HTTP Security配置类**。

但是，从5.7.0-M2版本开始，Spring不赞成使用WebSecurityConfigureAdapter，并建议通过不使用WebSecurityConfigureAdapter的方式编写配置。

我们使用内存身份验证创建一个示例Spring Boot应用程序来演示这种新型配置。

首先，让我们定义我们的配置类：

```java

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    // config ...
}
```

我们添加了@EnableGlobalMethodSecurity注解，以支持基于不同角色的处理。

### 2.1 配置Authentication

使用WebSecurityConfigureAdapter时，我们使用AuthenticationManagerBuilder设置Authentication上下文。

**现在，如果我们想避免使用WebSecurityConfigureAdapter，可以定义UserDetailsManager或UserDetailsService bean**：

```java

@Configuration
public class UserDetailServiceConfig {

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
}
```

或者，根据我们的UserDetailService，我们甚至可以设置AuthenticationManager：

```text
@Bean
public AuthenticationManager authManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailsService userDetailService)
        throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(userDetailService)
            .passwordEncoder(bCryptPasswordEncoder)
            .and()
            .build();
}
```

### 2.2 配置HTTP Security

**更重要的是，如果我们想避免弃用HTTP Security，我们现在可以创建一个SecurityFilterChain bean**。

例如，假设我们想根据角色保护端点，并留下一个匿名入口点仅用于登录。我们还将任何DELETE请求限制为ADMIN角色。并使用基本身份验证：

```java

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

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
}
```

**HTTP Security将构建一个DefaultSecurityFilterChain对象来加载请求匹配器和过滤器**。

### 2.3 配置Web Security

此外，对于Web Security，我们现在可以使用回调接口WebSecurityCustomizer。

让我们添加一个debug级别并忽略一些路径，例如图像或脚本：

```java
public class SecurityConfig {

    @Value("${spring.security.debug:false}")
    boolean securityDebug;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(securityDebug)
                .ignoring()
                .antMatchers("/css/**", "/js/**", "/img/**", "/lib/**", "/favicon.ico");
    }
}
```

## 3. Controller

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

正如我们之前在定义HTTP Security时提到的，我们添加一个任何人都可以访问的通用/login端点、ADMIN和USER角色的特定端点，以及一个不受角色保护但仍需要身份验证的/all端点。

## 4. 测试

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

此外，对于除/login之外的所有端点，我们总是需要身份验证，就像/all端点一样。

### 4.2 测试USER角色

USER角色可以访问/login端点以及我们为USER角色授予的所有其他路径：

```java
class SecurityFilterChainIntegrationTest {

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
}
```

值得注意的是，如果USER角色尝试访问受ADMIN角色保护的端点，用户会收到“forbidden”(403)错误。

相反，没有凭据的人（例如前面例子中的匿名用户）将收到“Unauthorized”错误(401)。

### 4.3 测试ADMIN角色

根据我们的配置，具有ADMIN角色的人可以访问任何端点：

```java
class SecurityFilterChainIntegrationTest {

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
}
```

## 5. 总结

在本文中，我们了解了如何在不使用WebSecurityConfigureAdapter的情况下创建Spring Security配置，
并在创建用于身份验证、HTTP Security和Web Security的组件时替换它。