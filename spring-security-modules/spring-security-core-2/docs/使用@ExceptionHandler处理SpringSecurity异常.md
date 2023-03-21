## 1. 概述

在本教程中，我们将学习如何使用@ExceptionHandler和@ControllerAdvice全局处理Spring Security异常。**[ControllerAdvice](https://www.baeldung.com/exception-handling-for-rest-with-spring)是一个拦截器，它允许我们在整个应用程序中使用相同的异常处理**。

## 2. Spring Security异常

AuthenticationException和AccessDeniedException等Spring Security核心异常是运行时异常。由于**这些异常是由DispatcherServlet后面的身份验证过滤器在调用控制器方法之前引发的**，因此@ControllerAdvice将无法捕获这些异常。

Spring Security异常可以通过添加自定义过滤器和构造响应体来直接处理。若要通过@ExceptionHandler和@ControllerAdvice在全局级别处理这些异常，我们需要[AuthenticationEntryPoint](https://www.baeldung.com/spring-security-basic-authentication)的自定义实现。**AuthenticationEntryPoint用于发送从客户端请求凭据的HTTP响应**。尽管安全入口点有多个内置实现，但我们需要编写一个自定义实现来发送自定义响应消息。

首先，让我们看看如何在不使用@ExceptionHandler的情况下全局处理安全异常。

## 3. 不使用@ExceptionHandler

Spring Security异常从AuthenticationEntryPoint开始。让我们为AuthenticationEntryPoint编写一个拦截安全异常的实现。

### 3.1 配置AuthenticationEntryPoint

让我们实现AuthenticationEntryPoint并覆盖commence()方法：

```java
@Component("customAuthenticationEntryPoint")
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        RestError re = new RestError(HttpStatus.UNAUTHORIZED.toString(), "Authentication failed");

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        OutputStream responseStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(responseStream, re);
        responseStream.flush();
    }
}
```

在这里，我们使用ObjectMapper作为响应正文的消息转换器。

### 3.2 配置SecurityConfig

接下来，让我们配置SecurityConfig来拦截用于身份验证的路径。在这里，我们将配置“/login”作为上述实现的路径。此外，我们将为“admin”用户配置“ADMIN”角色：

```java
@Configuration
@EnableWebSecurity
public class CustomSecurityConfig {

    @Autowired
    @Qualifier("customAuthenticationEntryPoint")
    AuthenticationEntryPoint authEntryPoint;

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withUsername("admin")
              .password("password")
              .roles("ADMIN")
              .build();
        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
        userDetailsManager.createUser(admin);
        return userDetailsManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.requestMatchers()
              .antMatchers("/login")
              .and()
              .authorizeRequests()
              .anyRequest()
              .hasRole("ADMIN")
              .and()
              .httpBasic()
              .and()
              .exceptionHandling()
              .authenticationEntryPoint(authEntryPoint);
        return http.build();
    }
}
```

### 3.3 配置RestController

现在，让我们编写一个处理此端点“/login”的RestController：

```java
@RestController
@RequestMapping
public class LoginController {

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> login() {
        return ResponseEntity.ok(new RestResponse("Success"));
    }
}
```

### 3.4 测试

最后，让我们使用MockMvc来测试这个端点。

首先，让我们编写一个身份验证成功的测试用例：

```java
@ExtendWith(SpringExtension.class)
@WebMvcTest(CustomSecurityConfig.class)
@Import({LoginController.class, CustomAuthenticationEntryPoint.class, DelegatedAuthenticationEntryPoint.class})
class SecurityConfigUnitTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenUserAccessLogin_shouldSucceed() throws Exception {
        mvc.perform(formLogin("/login").user("username", "admin")
                    .password("password", "password")
                    .acceptMediaType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk());
    }
}
```

接下来我们来看一个身份验证失败的场景：

```java
@Test
void whenUserAccessWithWrongCredentialsWithDelegatedEntryPoint_shouldFail() throws Exception {
    RestError re = new RestError(HttpStatus.UNAUTHORIZED.toString(), "Authentication failed");
    mvc.perform(formLogin("/login").user("username", "admin")
                .password("password", "wrong")
                .acceptMediaType(MediaType.APPLICATION_JSON))
          .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$.errorMessage", is(re.getErrorMessage())));
}
```

现在，让我们看看如何使用@ControllerAdvice和@ExceptionHandler实现相同的效果。

## 4. 使用@ExceptionHandler

这种方法允许我们使用完全相同的异常处理技术，但在ControllerAdvice中以一种更清晰、更好的方式使用带有@ExceptionHandler注解的方法。

### 4.1 配置AuthenticationEntryPoint

与上述方法类似，我们将实现AuthenticationEntryPoint然后将异常处理程序委托给HandlerExceptionResolver：

```java
@Component("delegatedAuthenticationEntryPoint")
public class DelegatedAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        resolver.resolveException(request, response, null, authException);
    }
}
```

在这里，我们注入了DefaultHandlerExceptionResolver，并将处理程序委托给这个解析器。现在可以使用带有异常处理程序方法的ControllerAdvice来处理此安全异常。

### 4.2 配置ExceptionHandler

现在，对于[异常处理程序](https://www.baeldung.com/exception-handling-for-rest-with-spring)的主要配置，我们将扩展ResponseEntityExceptionHandler并使用@ControllerAdvice标注这个类：

```java
@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({AuthenticationException.class})
    @ResponseBody
    public ResponseEntity<RestError> handleAuthenticationException(Exception ex) {
        RestError re = new RestError(HttpStatus.UNAUTHORIZED.toString(), "Authentication failed at controller advice");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(re);
    }
}
```

### 4.3 配置SecurityConfig

现在，让我们为这个委托的身份验证入口点编写一个安全配置：

```java
@Configuration
@EnableWebSecurity
@Order(101)
public class DelegatedSecurityConfig {

    @Autowired
    @Qualifier("delegatedAuthenticationEntryPoint")
    AuthenticationEntryPoint authEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.requestMatchers()
              .antMatchers("/login-handler")
              .and()
              .authorizeRequests()
              .anyRequest()
              .hasRole("ADMIN")
              .and()
              .httpBasic()
              .and()
              .exceptionHandling()
              .authenticationEntryPoint(authEntryPoint);
        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.withUsername("admin")
              .password("password")
              .roles("ADMIN")
              .build();
        return new InMemoryUserDetailsManager(admin);
    }
}
```

对于“/login-handler”端点，我们使用上面实现的DelegatedAuthenticationEntryPoint配置了异常处理程序。

### 4.4 配置RestController

让我们为“/login-handler”端点配置RestController：

```java
@PostMapping(value = "/login-handler", produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<RestResponse> loginWithExceptionHandler() {
    return ResponseEntity.ok(new RestResponse("Success"));
}
```

### 4.5 测试

现在让我们测试这个端点：

```java
@ExtendWith(SpringExtension.class)
@WebMvcTest(DelegatedSecurityConfig.class)
@Import({LoginController.class, CustomAuthenticationEntryPoint.class, DelegatedAuthenticationEntryPoint.class})
class DelegatedSecurityConfigUnitTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenUserAccessLogin_shouldSucceed() throws Exception {
        mvc.perform(formLogin("/login-handler").user("username", "admin")
                    .password("password", "password")
                    .acceptMediaType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk());
    }

    @Test
    void whenUserAccessWithWrongCredentialsWithDelegatedEntryPoint_shouldFail() throws Exception {
        RestError re = new RestError(HttpStatus.UNAUTHORIZED.toString(), "Authentication failed at controller advice");
        mvc.perform(formLogin("/login-handler").user("username", "admin")
                    .password("password", "wrong")
                    .acceptMediaType(MediaType.APPLICATION_JSON))
              .andExpect(status().isUnauthorized())
              .andExpect(jsonPath("$.errorMessage", is(re.getErrorMessage())));
    }
}
```

在成功的测试中，我们使用预配置的用户名和密码测试了端点。在失败的测试中，我们验证了响应正文中状态码和错误消息的响应。

## 5. 总结

在本文中，**我们学习了如何使用@ExceptionHandler全局处理Spring Security异常**。此外，我们还创建了一个功能齐全的示例，帮助我们理解所涉及的概念。