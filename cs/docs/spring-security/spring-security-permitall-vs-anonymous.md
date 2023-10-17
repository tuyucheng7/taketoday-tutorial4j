## 1. 概述

[*在本教程中，我们将了解 Spring Security 框架中HttpSecurity*](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html)类的*PermitAll()*和*anonymous()*方法。[Spring Security Framework](https://www.baeldung.com/security-spring)有助于防止漏洞攻击并支持 Web 应用程序的身份验证和授权。利用它，Web 应用程序可以控制对服务器资源（例如 HTML 表单、CSS 文件、JS 文件、Web 服务端点等）的访问。它还有助于启用 RBAC（基于角色的访问控制）来访问服务器资源。

**Web 应用程序的某些部分始终是用户只能在身份验证后才能访问的。然而，也有一些部分用户身份验证并不重要。有趣的是，在某些情况下，经过身份验证的用户无法访问某些服务器资源。**

我们很快就会讨论所有这些，并了解*PermitAll()*和*anonymous() 方法如何帮助使用*[Spring Security Expressions](https://www.baeldung.com/spring-security-expressions) 定义这些类型的安全访问。

## 2. 安全要求

在我们继续之前，让我们想象一个具有以下要求的电子商务网站：

- 匿名用户和经过身份验证的用户都可以查看网站上的产品
- 匿名和经过身份验证的用户请求的审核条目
- 匿名用户可以访问用户注册表单，而经过身份验证的用户则无法访问
- 只有经过身份验证的用户才能查看他们的购物车

## 3. 控制器和WebSecurity配置

首先，让我们定义具有电子商务网站端点的控制器类：

```java
@RestController
public class EcommerceController {
    @GetMapping("/private/showCart")
    public @ResponseBody String showCart() {
        return "Show Cart";
    }

    @GetMapping("/public/showProducts")
    public @ResponseBody String listProducts() {
        return "List Products";
    }

    @GetMapping("/public/registerUser")
    public @ResponseBody String registerUser() {
        return "Register User";
    }
}复制
```

早些时候，我们讨论了网站的安全要求。*让我们在EcommerceWebSecruityConfig*类中实现这些：

```java
@Configuration
@EnableWebSecurity
public class EcommerceWebSecurityConfig {
    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.withUsername("spring")
          .password(passwordEncoder.encode("secret"))
          .roles("USER")
          .build();

        return new InMemoryUserDetailsManager(user);
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.addFilterAfter(new AuditInterceptor(), AnonymousAuthenticationFilter.class)
          .authorizeRequests()
          .antMatchers("/private/**").authenticated().and().httpBasic()
          .and().authorizeRequests()
          .antMatchers("/public/showProducts").permitAll()
          .antMatchers("/public/registerUser").anonymous();

        return http.build();
    }
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}复制
```

基本上，我们定义了以下内容：

- **[\*AnonymousAuthenticationFilter\*](https://docs.spring.io/spring-security/reference/6.1-SNAPSHOT/servlet/authentication/anonymous.html#anonymous-config)之后的\*AuditInterceptor\*过滤器，用于记录匿名和经过身份验证的用户发出的请求**
- **用户必须强制进行身份验证才能访问路径为\*/private 的URL\***
- **所有用户都可以访问路径\*/public/showProducts\***
- **只有匿名用户才能访问路径\*/public/registerUser\***

我们还配置了一个用户*spring*，它将在整篇文章中用于调用*EcommerceController*中定义的 Web 服务端点。

## *4.HttpSecurity*中的*permitAll()*方法

*基本上，在EcommerceWebSecurityConfig*类中，我们使用[*PermitAll()*](https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html#authorize-requests)为all打开端点*/public/showProducts* *。* 现在，让我们看看这是否有效：

```java
@WithMockUser(username = "spring", password = "secret")
@Test
public void givenAuthenticatedUser_whenAccessToProductLinePage_thenAllowAccess() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/public/showProducts"))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().string("List Products"));
}

@WithAnonymousUser
@Test
public void givenAnonymousUser_whenAccessToProductLinePage_thenAllowAccess() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/public/showProducts"))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().string("List Products"));
}复制
```

正如预期的那样，匿名用户和经过身份验证的用户都可以访问该页面。

此外，在 Spring Security 6 中，*permitAll()*有助于非常有效地保护 JS 和 CSS 文件等静态资源**。此外，[我们应该始终选择使用\*permitAll()\*，而不是忽略](https://docs.spring.io/spring-security/reference/6.1-SNAPSHOT/servlet/authorization/authorize-http-requests.html#favor-permitall)Spring Security过滤器链中的静态资源。**因为过滤器链将无法在被忽略的静态资源上设置安全标头。

## *5.HttpSecurity*中的*anonymous()*方法

在我们开始实现电子商务网站的要求之前，了解[*匿名（）*](https://docs.spring.io/spring-security/reference/6.1-SNAPSHOT/servlet/authentication/anonymous.html#anonymous-overview)表达式背后的想法很重要。

符合Spring Security原则，我们需要为所有用户定义权限和限制。这对于匿名用户也有效。因此，它们与 ROLE_ANONYMOUS 相关联。

### 5.1. 实施*审计拦截器*

*[Spring Security 在AnonymousAuthenticationFilter](https://docs.spring.io/spring-security/reference/6.1-SNAPSHOT/servlet/authentication/anonymous.html#anonymous-config)*中填充匿名用户的*Authentication*对象。它有助于通过电子商务网站上的拦截器审核匿名用户和注册用户执行的操作。

*以下是我们之前在EcommerceWebSecurityConfig*类中配置的*AuditInterceptor*的概要：

```java
public class AuditInterceptor extends OncePerRequestFilter {
    private final Logger logger = LoggerFactory.getLogger(AuditInterceptor.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            logger.info("Audit anonymous user");
        }
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            logger.info("Audit registered user");
        }
        filterChain.doFilter(request, response);
    }
}复制
```

即使对于匿名用户，*Authentication*对象也不为 null。*这导致了AuditInterceptor*的稳健实现。它具有单独的流程用于审核匿名用户和经过身份验证的用户。

### 5.2. 拒绝经过身份验证的用户访问注册用户屏幕

*在EcommerceWebSecurityConfig*类中，使用表达式*[anonymous()](https://docs.spring.io/spring-security/reference/6.1-SNAPSHOT/servlet/authentication/anonymous.html#page-title)，*我们确保只有匿名用户才能访问端点*public/registerUser*。而经过身份验证的用户无法访问它。

我们看看是否达到了预期的效果：

```java
@WithAnonymousUser
@Test
public void givenAnonymousUser_whenAccessToUserRegisterPage_thenAllowAccess() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/public/registerUser"))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().string("Register User"));
}复制
```

因此，匿名用户可以访问用户注册页面。

同样，它是否能够拒绝经过身份验证的用户的访问？让我们来了解一下：

```java
@WithMockUser(username = "spring", password = "secret")
@Test
public void givenAuthenticatedUser_whenAccessToUserRegisterPage_thenDenyAccess() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/public/registerUser"))
      .andExpect(MockMvcResultMatchers.status().isForbidden());
}复制
```

上述方法成功拒绝经过身份验证的用户访问用户注册页面。

*与PermitAll()*方法不同，*anonymous()*也可用于在不需要身份验证的情况下为静态资源提供服务。

## 六，结论

在本教程中，我们借助示例演示了*permitAll()*和*anonymous()*方法之间的区别*。*

当我们拥有只能由匿名用户访问的公共内容时，使用*anonymous() 。*相反，当我们希望允许所有用户访问特定 URL 而不区分他们的身份验证状态时，请使用*PermitAll() 。*