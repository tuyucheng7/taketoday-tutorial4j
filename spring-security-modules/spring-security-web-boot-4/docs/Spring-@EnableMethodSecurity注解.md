## 1. 概述

使用[Spring Security](https://www.baeldung.com/security-spring)，我们可以为端点等方法配置应用程序的身份验证和授权。例如，如果用户在我们的域上进行了身份验证，我们可以通过对现有方法应用限制来分析他对应用程序的使用情况。

使用[@EnableGlobalMethodSecurity](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html#jc-enable-global-method-security)注解一直是标准，直到5.6版本[@EnableMethodSecurity](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html#_enablemethodsecurity)引入了一种更灵活的方法来配置[方法安全性](https://www.baeldung.com/spring-security-method-security)的授权。

在本教程中，我们将看到@EnableMethodSecurity如何替换旧注解。我们还将看到其前身和一些代码示例之间的区别。

## 2. @EnableMethodSecurity与@EnableGlobalMethodSecurity

如果我们首先查看方法授权如何与@EnableGlobalMethodSecurity一起使用，我们可以了解更多关于这个主题的信息。

### 2.1 @EnableGlobalMethodSecurity

[@EnableGlobalMethodSecurity](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/method/configuration/EnableGlobalMethodSecurity.html)是一个函数式接口，我们需要与[@EnableWebSecurity](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/configuration/EnableWebSecurity.html)一起创建我们的安全层并获得方法授权。

让我们创建一个示例配置类：

```java
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Configuration
public class SecurityConfig {
    // security beans
}
```

所有方法安全实现都使用一个在需要授权时触发的[MethodInterceptor](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/aopalliance/intercept/MethodInterceptor.html)。在这种情况下，[GlobalMethodSecurityConfiguration](https://docs.spring.io/spring-security/site/docs/6.0.0-M3/api/org/springframework/security/config/annotation/method/configuration/GlobalMethodSecurityConfiguration.html)类是启用全局方法安全性的基本配置。

**[methodSecurityInterceptor()](https://docs.spring.io/spring-security/site/docs/6.0.0-M3/api/org/springframework/security/config/annotation/method/configuration/GlobalMethodSecurityConfiguration.html#methodSecurityInterceptor(org.springframework.security.access.method.MethodSecurityMetadataSource))方法使用元数据为我们可能想要使用的不同授权类型创建MethodInterceptor bean**。

Spring Security支持三种内置的方法安全注解：

-   prePostEnabled用于Spring pre/post注解
-   securedEnabled用于Spring @Secured注解
-   jsr250Enabled用于标准Java @RoleAllowed注解

此外，在methodSecurityInterceptor()方法中，还设置了：

-   [AccessDecisionManager](https://docs.spring.io/spring-security/site/docs/6.0.0-M3/api/org/springframework/security/access/AccessDecisionManager.html)，它使用[基于投票](https://docs.spring.io/spring-security/reference/servlet/authorization/architecture.html#authz-voting-based)的机制“决定”是否授予访问权限
-   [AuthenticationManager](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/authentication/AuthenticationManager.html)，我们从安全上下文中获取并负责身份验证
-   [AfterInvocationManager](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/access/intercept/AfterInvocationManager.html)，负责为pre/post表达式提供处理程序

该框架具有拒绝或授予对特定方法的访问权限的投票机制。我们可以将其作为[Jsr250Voter](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/access/annotation/Jsr250Voter.html)的示例进行检查：

```java
@Override
public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> definition) {
    boolean jsr250AttributeFound = false;
    for (ConfigAttribute attribute : definition) {
        if (Jsr250SecurityConfig.PERMIT_ALL_ATTRIBUTE.equals(attribute)) {
            return ACCESS_GRANTED;
        }
        if (Jsr250SecurityConfig.DENY_ALL_ATTRIBUTE.equals(attribute)) {
            return ACCESS_DENIED;
        }
        if (supports(attribute)) {
            jsr250AttributeFound = true;
            // Attempt to find a matching granted authority
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (attribute.getAttribute().equals(authority.getAuthority())) {
                    return ACCESS_GRANTED;
                }
            }
        }
    }
    return jsr250AttributeFound ? ACCESS_DENIED : ACCESS_ABSTAIN;
}
```

**投票时，Spring Security从当前方法中提取元数据属性，例如，我们的REST端点。最后，它根据用户授予的权限检查它们**。

我们还应该注意投票器不支持投票制度并弃权的可能性。

然后，我们的AccessDecisionManager会评估可用投票器的所有回复：

```java
for (AccessDecisionVoter voter : getDecisionVoters()) {
    int result = voter.vote(authentication, object, configAttributes);
    switch (result) {
        case AccessDecisionVoter.ACCESS_GRANTED:
            return;
        case AccessDecisionVoter.ACCESS_DENIED:
            deny++;
            break;
        default:
            break;
    }
}
if (deny > 0) {
    throw new AccessDeniedException(this.messages.getMessage("AbstractAccessDecisionManager.accessDenied", "Access is denied"));
}
```

如果我们想自定义我们的beans，我们可以扩展GlobalMethodSecurityConfiguration类。 例如，我们可能想要一个[自定义的安全表达式](https://www.baeldung.com/spring-security-create-new-custom-security-expression)，而不是Spring Security内置的[Spring EL](https://docs.spring.io/spring-security/reference/servlet/authorization/expression-based.html#el-access)。或者我们可能想要创建我们的[自定义安全投票器](https://www.baeldung.com/spring-security-custom-voter)。

### 2.2 @EnableMethodSecurity

使用@EnableMethodSecurity，我们可以看到Spring Security将授权类型转移到基于bean的配置的意图。

我们现在为每种类型提供一个配置，而不是全局配置。例如，让我们看看[Jsr250MethodSecurityConfiguration](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/access/annotation/Jsr250SecurityConfig.html)：

```java
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
class Jsr250MethodSecurityConfiguration {
    // ...
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    Advisor jsr250AuthorizationMethodInterceptor() {
        return AuthorizationManagerBeforeMethodInterceptor.jsr250(this.jsr250AuthorizationManager);
    }

    @Autowired(required = false)
    void setGrantedAuthorityDefaults(GrantedAuthorityDefaults grantedAuthorityDefaults) {
        this.jsr250AuthorizationManager.setRolePrefix(grantedAuthorityDefaults.getRolePrefix());
    }
}
```

**MethodInterceptor本质上包含一个[AuthorizationManager](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/authorization/AuthorizationManager.html)，它现在将检查和返回AuthorizationDecision对象的责任委托给适当的实现**，在本例中为AuthenticatedAuthorizationManager：

```java
@Override
public AuthorizationDecision check(Supplier<Authentication> authentication, T object) {
    boolean granted = isGranted(authentication.get());
    return new AuthorityAuthorizationDecision(granted, this.authorities);
}

private boolean isGranted(Authentication authentication) {
    return authentication != null && authentication.isAuthenticated() && isAuthorized(authentication);
}

private boolean isAuthorized(Authentication authentication) {
    Set<String> authorities = AuthorityUtils.authorityListToSet(this.authorities);
    for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
        if (authorities.contains(grantedAuthority.getAuthority())) {
            return true;
        }
    }
    return false;
}
```

如果我们无权访问资源，MethodInterceptor会抛出AccessDeniedException：

```java
AuthorizationDecision decision = this.authorizationManager.check(AUTHENTICATION_SUPPLIER, mi);
if (decision != null && !decision.isGranted()) {
    // ...
    throw new AccessDeniedException("Access Denied");
}
```

## 3. @EnableMethodSecurity特性

与以前的遗留实现相比，@EnableMethodSecurity带来了次要和主要的改进。

### 3.1 次要改进

仍然支持所有授权类型。例如，它仍然符合[JSR-250](https://www.jcp.org/en/jsr/detail?id=250)。但是，我们不需要将prePostEnabled添加到注解中，因为它现在默认为true：

```java
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
```

如果我们想禁用它，我们需要将prePostEnabled设置为false。

### 3.2 主要改进

GlobalMethodSecurityConfiguration类不再使用。**Spring Security将其替换为分段配置和[AuthorizationManager](https://docs.spring.io/spring-security/reference/servlet/authorization/architecture.html#_the_authorizationmanager)，这意味着我们可以在不扩展任何基本配置类的情况下定义我们的authorization bean**。

值得注意的是AuthorizationManager接口是泛型的并且可以适应任何对象，尽管标准安全适用于MethodInvocation：

```java
AuthorizationDecision check(Supplier<Authentication> authentication, T object);
```

**总的来说，这为我们提供了使用[委托](https://en.wikipedia.org/wiki/Delegation_(object-oriented_programming))的细粒度授权**。因此，实际上，我们为每种类型都有一个AuthorizationManager。当然，我们也可以创建自己的。

此外，这也意味着@EnableMethodSecurity不允许像遗留实现中那样使用AspectJ方法拦截器进行[@AspectJ](https://www.baeldung.com/aspectj)注解：

```java
public final class AspectJMethodSecurityInterceptor extends MethodSecurityInterceptor {
    public Object invoke(JoinPoint jp) throws Throwable {
        return super.invoke(new MethodInvocationAdapter(jp));
    }
    // ...
}
```

但是，我们仍然有完整的[AOP](https://www.baeldung.com/spring-aop)支持。例如，让我们看一下前面讨论的Jsr250MethodSecurityConfiguration使用的拦截器：

```java
public final class AuthorizationManagerBeforeMethodInterceptor implements Ordered, MethodInterceptor, PointcutAdvisor, AopInfrastructureBean {
    // ...
    public AuthorizationManagerBeforeMethodInterceptor(Pointcut pointcut, AuthorizationManager<MethodInvocation> authorizationManager) {
        Assert.notNull(pointcut, "pointcut cannot be null");
        Assert.notNull(authorizationManager, "authorizationManager cannot be null");
        this.pointcut = pointcut;
        this.authorizationManager = authorizationManager;
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        attemptAuthorization(mi);
        return mi.proceed();
    }
}
```

## 4. 自定义AuthorizationManager应用程序

那么让我们看看如何创建自定义授权管理器。

假设我们有要为其应用策略的端点。我们只希望通过授权的用户访问该策略。否则，我们将阻止该用户。

作为第一步，我们通过添加一个字段来定义我们的用户来访问受限策略：

```java
public class SecurityUser implements UserDetails {
    private String userName;
    private String password;
    private List<GrantedAuthority> grantedAuthorityList;
    private boolean accessToRestrictedPolicy;

    // getters and setters
}
```

现在，让我们看看我们的身份验证层来定义我们系统中的用户。为此，我们将创建一个自定义的[UserDetailService](https://docs.spring.io/spring-security/site/docs/5.7.5/api/org/springframework/security/core/userdetails/UserDetailsService.html)。我们将使用内存Map来存储用户：

```java
public class CustomUserDetailService implements UserDetailsService {
    private final Map<String, SecurityUser> userMap = new HashMap<>();

    public CustomUserDetailService(BCryptPasswordEncoder bCryptPasswordEncoder) {
        userMap.put("user", createUser("user", bCryptPasswordEncoder.encode("userPass"), false, "USER"));
        userMap.put("admin", createUser("admin", bCryptPasswordEncoder.encode("adminPass"), true, "ADMIN", "USER"));
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return Optional.ofNullable(map.get(username))
              .orElseThrow(() -> new UsernameNotFoundException("User " + username + " does not exists"));
    }

    private SecurityUser createUser(String userName, String password, boolean withRestrictedPolicy, String... role) {
        return SecurityUser.builder().withUserName(userName)
              .withPassword(password)
              .withGrantedAuthorityList(Arrays.stream(role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()))
              .withAccessToRestrictedPolicy(withRestrictedPolicy);
    }
}
```

一旦用户存在于我们的系统中，我们希望通过检查他是否可以访问某些受限策略来限制他可以访问的信息。

为了演示，我们创建了一个Java注解@Policy以应用于方法和策略枚举PolicyEnum：

```java
@Target(METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Policy {
    PolicyEnum value();
}
```

```java
public enum PolicyEnum {
    RESTRICTED, OPEN
}
```

让我们创建要应用此策略的服务：

```java
@Service
public class PolicyService {
    @Policy(PolicyEnum.OPEN)
    public String openPolicy() {
        return "Open Policy Service";
    }

    @Policy(PolicyEnum.RESTRICTED)
    public String restrictedPolicy() {
        return "Restricted Policy Service";
    }
}
```

我们不能使用内置的授权管理器，例如Jsr250AuthorizationManager。它不知道何时以及如何拦截服务策略检查。因此，让我们定义我们的自定义管理器：

```java
public class CustomAuthorizationManager<T> implements AuthorizationManager<MethodInvocation> {
    // ...
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, MethodInvocation methodInvocation) {
        if (hasAuthentication(authentication.get())) {
            Policy policyAnnotation = AnnotationUtils.findAnnotation(methodInvocation.getMethod(), Policy.class);
            SecurityUser user = (SecurityUser) authentication.get().getPrincipal();
            return new AuthorizationDecision(Optional.ofNullable(policyAnnotation)
                  .map(Policy::value).filter(policy -> policy == PolicyEnum.OPEN || (policy == PolicyEnum.RESTRICTED && user.hasAccessToRestrictedPolicy())).isPresent());
        }
        return new AuthorizationDecision(false);
    }

    private boolean hasAuthentication(Authentication authentication) {
        return authentication != null && isNotAnonymous(authentication) && authentication.isAuthenticated();
    }

    private boolean isNotAnonymous(Authentication authentication) {
        return !this.trustResolver.isAnonymous(authentication);
    }
}
```

当服务方法被触发时，我们会检查用户是否具有authentication。然后，如果策略是OPEN，我们授予访问权限。如果是RESTRICTED，我们会检查用户是否可以访问受限策略。

为此，我们需要定义一个MethodInterceptor，它将在执行之前就位，例如在执行之前，但也可以在执行之后。因此，让我们将它与我们的安全配置类包装在一起：

```java
@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity, UserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public UserDetailsService userDetailsService(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return new CustomUserDetailService(bCryptPasswordEncoder);
    }

    @Bean
    public AuthorizationManager<MethodInvocation> authorizationManager() {
        return new CustomAuthorizationManager<>();
    }

    @Bean
    @Role(ROLE_INFRASTRUCTURE)
    public Advisor authorizationManagerBeforeMethodInterception(AuthorizationManager<MethodInvocation> authorizationManager) {
        JdkRegexpMethodPointcut pattern = new JdkRegexpMethodPointcut();
        pattern.setPattern("com.baeldung.enablemethodsecurity.services.*");
        return new AuthorizationManagerBeforeMethodInterceptor(pattern, authorizationManager);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
              .disable()
              .authorizeRequests()
              .anyRequest()
              .authenticated()
              .and()
              .sessionManagement()
              .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

我们正在使用AuthorizationManagerBeforeMethodInterceptor。它符合我们的策略服务模式并使用自定义授权管理器。

此外，我们还需要让我们的AuthenticationManager知道自定义的UserDetailsService。然后，当Spring Security拦截服务方法时，我们可以访问我们的自定义用户并检查用户的策略访问。

## 5. 测试

让我们定义一个REST控制器：

```java
@RestController
public class ResourceController {
    // ...
    @GetMapping("/openPolicy")
    public String openPolicy() {
        return policyService.openPolicy();
    }

    @GetMapping("/restrictedPolicy")
    public String restrictedPolicy() {
        return policyService.restrictedPolicy();
    }
}
```

我们将在我们的应用程序中使用[Spring Boot Test](https://www.baeldung.com/spring-boot-testing)来模拟方法安全性：

```java
@SpringBootTest(classes = EnableMethodSecurityApplication.class)
public class EnableMethodSecurityTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
              .apply(springSecurity())
              .build();
    }

    @Test
    @WithUserDetails(value = "admin")
    public void whenAdminAccessOpenEndpoint_thenOk() throws Exception {
        mvc.perform(get("/openPolicy"))
              .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "admin")
    public void whenAdminAccessRestrictedEndpoint_thenOk() throws Exception {
        mvc.perform(get("/restrictedPolicy"))
              .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails()
    public void whenUserAccessOpenEndpoint_thenOk() throws Exception {
        mvc.perform(get("/openPolicy"))
              .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails()
    public void whenUserAccessRestrictedEndpoint_thenIsForbidden() throws Exception {
        mvc.perform(get("/restrictedPolicy"))
              .andExpect(status().isForbidden());
    }
}
```

所有响应都应经过授权，但用户调用他无权访问受限策略的服务的响应除外。

## 6. 总结

在本文中，我们了解了@EnableMethodSecurity的主要特性以及它如何替代@EnableGlobalMethodSecurity。

我们还通过执行流程了解了这些注解之间的区别。然后，我们讨论了@EnableMethodSecurity如何通过基于bean的配置提供更大的灵活性。最后，我们了解了如何创建自定义授权管理器和MVC测试。