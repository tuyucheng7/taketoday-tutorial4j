## 1. 概述

在本快速教程中，我们将实施一个基本解决方案，以防止使用 Spring Security进行[暴力认证尝试。](https://www.baeldung.com/cs/brute-force-cybersecurity-string-search)

简单地说——我们将记录来自单个 IP 地址的失败尝试次数。如果该特定 IP 超过一定数量的请求 - 它将被阻止 24 小时。

## 延伸阅读：

## [Spring方法安全简介](https://www.baeldung.com/spring-security-method-security)

使用 Spring Security 框架的方法级安全指南。

[阅读更多](https://www.baeldung.com/spring-security-method-security)→

## [Spring Security 过滤器链中的自定义过滤器](https://www.baeldung.com/spring-security-custom-filter)

显示在 Spring Security 上下文中添加自定义过滤器的步骤的快速指南。

[阅读更多](https://www.baeldung.com/spring-security-custom-filter)→

## [用于响应式应用程序的 Spring Security 5](https://www.baeldung.com/spring-security-5-reactive)

Spring Security 5 框架用于保护反应式应用程序的功能的快速实用示例。

[阅读更多](https://www.baeldung.com/spring-security-5-reactive)→

## 2. AuthenticationFailureListener

让我们从定义AuthenticationFailureListener开始——监听AuthenticationFailureBadCredentialsEvent事件并通知我们身份验证失败：

```java
@Component
public class AuthenticationFailureListener implements 
  ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

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

请注意，当身份验证失败时，我们如何通知LoginAttemptService不成功尝试的 IP 地址。在这里，我们从HttpServletRequest bean 获取 IP 地址，它还在X-Forwarded-For 标头中为我们提供由代理服务器转发的请求 的原始地址。

## 3. AuthenticationSuccessEventListener

我们还定义一个AuthenticationSuccessEventListener——它监听AuthenticationSuccessEvent事件并通知我们身份验证成功：

```java
@Component
public class AuthenticationSuccessEventListener implements 
  ApplicationListener<AuthenticationSuccessEvent> {
    
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    public void onApplicationEvent(final AuthenticationSuccessEvent e) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            loginAttemptService.loginSucceeded(request.getRemoteAddr());
        } else {
            loginAttemptService.loginSucceeded(xfHeader.split(",")[0]);
        }
    }
}
```

请注意 - 与失败侦听器类似，我们通知LoginAttemptService身份验证请求源自的 IP 地址。

## 4.登录尝试服务

现在——让我们讨论我们的LoginAttemptService实现；简单地说——我们将每个 IP 地址的错误尝试次数保留 24 小时：

```java
@Service
public class LoginAttemptService {

    private final int MAX_ATTEMPT = 10;
    private LoadingCache<String, Integer> attemptsCache;

    public LoginAttemptService() {
        super();
        attemptsCache = CacheBuilder.newBuilder().
          expireAfterWrite(1, TimeUnit.DAYS).build(new CacheLoader<String, Integer>() {
            public Integer load(String key) {
                return 0;
            }
        });
    }

    public void loginSucceeded(String key) {
        attemptsCache.invalidate(key);
    }

    public void loginFailed(String key) {
        int attempts = 0;
        try {
            attempts = attemptsCache.get(key);
        } catch (ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked(String key) {
        try {
            return attemptsCache.get(key) >= MAX_ATTEMPT;
        } catch (ExecutionException e) {
            return false;
        }
    }
}
```

请注意不成功的身份验证尝试如何增加该 IP 的尝试次数，而成功的身份验证会重置该计数器。

从这一点来看，这只是我们验证时检查计数器的问题。

## 5.用户详情服务

现在，让我们在自定义的UserDetailsService实现中添加额外的检查；当我们加载UserDetails时，我们首先需要检查这个 IP 地址是否被阻止：

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
        String ip = getClientIP();
        if (loginAttemptService.isBlocked(ip)) {
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

这是getClientIP()方法：

```java
private String getClientIP() {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null){
        return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0];
}
```

请注意，我们有一些额外的逻辑来识别 Client 的原始 IP 地址。在大多数情况下，这不是必需的，但在某些网络场景中，它是必需的。

对于这些罕见的情况，我们使用X-Forwarded-For标头来获取原始 IP；这是此标头的语法：

```bash
X-Forwarded-For: clientIpAddress, proxy1, proxy2
```

另外，请注意 Spring 的另一个非常有趣的功能——我们需要 HTTP 请求，所以我们只是将它连接起来。

现在，这很酷。我们必须在我们的web.xml中添加一个快速侦听器才能使其工作，它使事情变得容易得多。

```xml
<listener>
    <listener-class>
        org.springframework.web.context.request.RequestContextListener
    </listener-class>
</listener>
```

就是这样——我们在web.xml中定义了这个新的RequestContextListener以便能够访问来自UserDetailsService的请求。

## 6.修改AuthenticationFailureHandler

最后 - 让我们修改我们的CustomAuthenticationFailureHandler以自定义我们的新错误消息。

我们正在处理用户确实被阻止 24 小时的情况——我们通知用户他的 IP 被阻止是因为他超过了允许的最大错误身份验证尝试次数：

```java
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private MessageSource messages;

    @Override
    public void onAuthenticationFailure(...) {
        ...

        String errorMessage = messages.getMessage("message.badCredentials", null, locale);
        if (exception.getMessage().equalsIgnoreCase("blocked")) {
            errorMessage = messages.getMessage("auth.message.blocked", null, locale);
        }

        ...
    }
}
```

## 七. 总结

了解这是处理暴力密码尝试的良好开端很重要，但还有改进的余地。生产级暴力破解策略可能涉及比 IP 块更多的元素。