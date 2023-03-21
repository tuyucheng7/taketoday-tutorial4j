## 1. 概述

在本教程中，我们将讨论注册过程的一个关键部分，即**密码编码**，这基本上不是以明文形式存储密码。

Spring Security支持一些编码机制，在本教程中，**我们将使用BCrypt**，因为它通常是可用的最佳解决方案。

大多数其他机制，如MD5PasswordEncoder和ShaPasswordEncoder使用较弱的算法，现在已被弃用。

## 延伸阅读

### [Spring Security 5中的新密码存储](https://www.baeldung.com/spring-security-5-password-storage)

了解Spring Security 5中的密码加密和迁移到更好的加密算法的快速指南。

[阅读更多](https://www.baeldung.com/spring-security-5-password-storage)→

### [仅允许从接受位置使用Spring Security进行身份验证](https://www.baeldung.com/spring-security-restrict-authentication-by-geography)

了解如何使用Spring Security仅允许用户从接受的位置进行身份验证。

[阅读更多](https://www.baeldung.com/spring-security-restrict-authentication-by-geography)→

### [Spring Security-注册后自动登录用户](https://www.baeldung.com/spring-security-auto-login-user-after-registration)

了解如何在完成注册过程后快速自动验证用户。

[阅读更多](https://www.baeldung.com/spring-security-auto-login-user-after-registration)→

## 2. 定义密码编码器

我们将首先在我们的配置中将简单的BCryptPasswordEncoder定义为一个bean：

```java
@Bean
public PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
}
```

较旧的实现(例如SHAPasswordEncoder)要求客户端在对密码进行编码时传入盐值。

然而，**BCrypt将在内部生成一个随机盐**。理解这一点很重要，因为这意味着每次调用都会有不同的结果，所以我们只需要对密码进行一次编码。

为了使这种随机盐生成有效，BCrypt会将盐存储在哈希值本身中。例如，以下哈希值：

```text
$2a$10$ZLhnHxdpHETcxmtEStgpI./Ri1mksgJ9iDP36FmfMdYyVg9g0b2dq
```

用$分隔三个字段：

1.  “2a”代表BCrypt算法版本
2.  “10”代表算法的强度
3.  “ZLhnHxdpHETcxmtEStgpI.”部分实际上是随机生成的盐。基本上，前22个字符是盐。最后一个字段的剩余部分是纯文本的实际哈希版本。

另外，请注意BCrypt算法生成长度为60的字符串，因此我们需要确保密码将存储在可以容纳它的列中。一个常见的错误是创建一个不同长度的列，然后在身份验证时得到一个Invalid Username or Password错误。

## 3. 注册时对密码进行编码

我们将在我们的UserService中使用PasswordEncoder在用户注册过程中对密码进行哈希处理：

```java
@Autowired
private PasswordEncoder passwordEncoder;

@Override
public User registerNewUserAccount(UserDto accountDto) throws EmailExistsException {
    if (emailExist(accountDto.getEmail())) {
        throw new EmailExistsException("There is an account with that email adress:" + accountDto.getEmail());
    }
    User user = new User();
    user.setFirstName(accountDto.getFirstName());
    user.setLastName(accountDto.getLastName());
    
    user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
    
    user.setEmail(accountDto.getEmail());
    user.setRole(new Role(Integer.valueOf(1), user));
    return repository.save(user);
}
```

## 4. 在身份验证时对密码进行编码

现在我们将处理这个过程的另一半，并在用户进行身份验证时对密码进行编码。

首先，我们需要将之前定义的密码编码器bean注入到我们的身份验证提供程序中：

```java
@Autowired
private UserDetailsService userDetailsService;

@Bean
public DaoAuthenticationProvider authProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(encoder());
    return authProvider;
}
```

安全配置很简单：

-   我们注入了UserDetailsService的实现
-   我们定义了一个引用我们的UserDetailsService的身份验证提供程序
-   我们还启用密码编码器

最后，我们需要在我们的安全XML配置中**引用这个authProvider**：

```xml
<authentication-manager>
    <authentication-provider ref="authProvider"/>
</authentication-manager>
```

或者，如果我们使用Java配置：

```java
@Configuration
@ComponentScan(basePackages = { "cn.tuyucheng.taketoday.security" })
@EnableWebSecurity
public class SecSecurityConfig {

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
              .authenticationProvider(authProvider())
              .build();
    }

    // ...
}
```

## 5. 总结

这篇简短的文章继续注册系列，展示了如何利用简单但功能强大的BCrypt实现将密码正确存储在数据库中。