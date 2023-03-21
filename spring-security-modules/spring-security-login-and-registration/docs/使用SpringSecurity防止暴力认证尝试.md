## 1. 概述

在本快速教程中，我们将实现一个基本解决方案，以防止**使用Spring Security进行[暴力认证尝试](https://www.baeldung.com/cs/brute-force-cybersecurity-string-search)**。

简单地说-我们将记录来自单个IP地址的失败尝试次数。如果该特定IP超过一定数量的请求-它将被阻止24小时。

## 延伸阅读

### [Spring方法安全简介](https://www.baeldung.com/spring-security-method-security)

使用Spring Security框架的方法级安全指南。

[阅读更多](https://www.baeldung.com/spring-security-method-security)→

### [Spring Security过滤器链中的自定义过滤器](https://www.baeldung.com/spring-security-custom-filter)

显示在Spring Security上下文中添加自定义过滤器的步骤的快速指南。

[阅读更多](https://www.baeldung.com/spring-security-custom-filter)→

### [用于响应式应用程序的Spring Security 5](https://www.baeldung.com/spring-security-5-reactive)

Spring Security 5框架用于保护响应式应用程序的功能的快速实用示例。

[阅读更多](https://www.baeldung.com/spring-security-5-reactive)→

## 2. AuthenticationFailureListener

让我们首先定义一个AuthenticationFailureListener-监听AuthenticationFailureBadCredentialsEvent事件并通知我们身份验证失败：

```java
@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            loginAttemptService.loginFailed(request.getRemoteAddr());
        } else {
            loginAttemptService.loginFailed(xfHeader.split(",")[0]);
        }
    }
}
```

请注意，当身份验证失败时，我们如何通知LoginAttemptService不成功尝试的IP地址。在这里，我们从HttpServletRequest bean获取IP地址，它还在X-Forwarded-For标头中为我们提供由代理服务器转发的请求的原始地址。

**我们还注意到X-Forwarded-For标头是多值的，可以对其进行调整以轻松覆盖原始IP**。出于这个原因，我们不应该假设标头是可信的；相反，我们必须首先检查它是否包含请求的远程地址。否则，攻击者可能在标头的第一个索引处设置一个不同于他自己的IP，以避免阻止他自己的IP。如果我们阻止其中一个IP地址，则攻击者可以添加另一个IP地址，依此类推。这意味着他可以暴力破解标头IP地址来欺骗请求。

## 3. LoginAttemptService

现在让我们讨论一下我们的LoginAttemptService实现；简单地说-我们将每个IP地址的错误尝试次数保留24小时。block方法将检查来自给定IP的请求是否未达到允许的限制。

```java
@Service
public class LoginAttemptService {

    public static final int MAX_ATTEMPT = 10;
    private LoadingCache<String, Integer> attemptsCache;

    @Autowired
    private HttpServletRequest request;

    public LoginAttemptService() {
        super();
        attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build(new CacheLoader<String, Integer>() {
            @Override
            public Integer load(final String key) {
                return 0;
            }
        });
    }

    public void loginFailed(final String key) {
        int attempts;
        try {
            attempts = attemptsCache.get(key);
        } catch (final ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked() {
        try {
            return attemptsCache.get(getClientIP()) >= MAX_ATTEMPT;
        } catch (final ExecutionException e) {
            return false;
        }
    }

    private String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
```

这是getClientIP()方法：

```java
private String getClientIP() {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
        return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0];
}
```

请注意，我们有一些额外的逻辑来**识别客户端的原始IP地址**。在大多数情况下，这不是必需的，但在某些网络场景中，它是必需的。

对于这些罕见的情况，我们使用X-Forwarded-For标头来获取原始IP；下面是此标头的语法：

```text
X-Forwarded-For: clientIpAddress, proxy1, proxy2
```

请注意**不成功的身份验证尝试如何增加该IP的尝试次数**，但对于成功的身份验证，计数器不会被重置。

从这一点来看，只需**在进行身份验证时检查计数器即可**。

## 4. UserDetailsService

现在，让我们在自定义的UserDetailsService实现中添加额外的检查；当我们加载UserDetails时，**我们首先需要检查这个IP地址是否被阻止**：

```java
@Service("userDetailsService")
@Transactional
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (loginAttemptService.isBlocked()) {
            throw new RuntimeException("blocked");
        }

        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return new org.springframework.security.core.userdetails.User(
                      " ", " ", true, true, true, true,
                      getAuthorities(Arrays.asList(roleRepository.findByName("ROLE_USER"))));
            }

            return new org.springframework.security.core.userdetails.User(
                  user.getEmail(), user.getPassword(), user.isEnabled(), true, true, true,
                  getAuthorities(user.getRoles()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

另外，请注意Spring的另一个非常有趣的功能-**我们需要HTTP请求，所以我们只是将它注入进来**。

现在，这很酷。我们必须在我们的web.xml中添加一个快速监听器才能使其工作，它使事情变得容易得多。

```xml
<listener>
    <listener-class>
        org.springframework.web.context.request.RequestContextListener
    </listener-class>
</listener>
```

就是这样-我们在web.xml中定义了这个新的RequestContextListener以便能够访问来自UserDetailsService的请求。

## 5. 修改AuthenticationFailureHandler

最后-让我们修改我们的CustomAuthenticationFailureHandler以自定义我们的新错误消息。

我们正在处理用户确实被阻止24小时的情况-我们会通知用户他的IP被阻止，因为他超过了允许的最大错误身份验证尝试次数。在此类中，如果用户被阻止，我们还会在每次失败时检查：

```java
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private MessageSource messages;

    @Override
    public void onAuthenticationFailure(...) {
        // ...

        String errorMessage = messages.getMessage("message.badCredentials", null, locale);
        if (exception.getMessage().equalsIgnoreCase("blocked")) {
            errorMessage = messages.getMessage("auth.message.blocked", null, locale);
        }

        // ...
    }
}
```

## 6. 总结

重要的是要了解这是处理暴力密码尝试的良好开端，但还有改进的余地。生产级暴力破解策略可能涉及比IP块更多的元素。