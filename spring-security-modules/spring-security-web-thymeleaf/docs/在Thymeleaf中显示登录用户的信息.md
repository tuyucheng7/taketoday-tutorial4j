## 1. 概述

在这个快速教程中，我们介绍如何在Thymeleaf中显示登录用户的信息。

我们扩展在[SpringSecurity中使用Thymeleaf](SpringSecurity中使用Thymeleaf.md)文章中构建的项目。首先添加一个自定义模型来存储用户信息，并添加一个Service来检索用户信息。然后使用Thymeleaf Extras模块中的Spring Security Dialect 显示它。

## 2. UserDetails实现

UserDetails是Spring Security中的一个接口，用于保存与安全无关的用户信息。

我们将创建UserDetails接口的实现，其中包含一些自定义字段作为存储经过身份验证的用户详细信息的模型。但是，为了处理更少的字段和方法，我们继承默认的框架实现类User：

```java
public class CustomUserDetails extends User {

    private final String firstName;
    private final String lastName;
    private final String email;

    private CustomUserDetails(Builder builder) {
        super(builder.username, builder.password, builder.authorities);
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
    }

    // omitting getters and static Builder class
}
```

## 3. UserDetailsService实现

框架的UserDetailsService单一方法接口负责在认证过程中获取UserDetails。

因此，为了能够加载我们的CustomUserDetails，我们需要实现UserDetailsService接口。对于我们的示例，我们将对用户详细信息进行硬编码并存储在一个Map中，该Map将用户名作为key：

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final Map<String, CustomUserDetails> userRegistry = new HashMap<>();

    // omitting constructor

    @PostConstruct
    public void init() {
        userRegistry.put("user", new CustomUserDetails.Builder().withFirstName("Mark")
                .withLastName("Johnson")
                .withEmail("mark.johnson@email.com")
                .withUsername("user")
                .withPassword(passwordEncoder.encode("password"))
                .withAuthorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build());
        userRegistry.put("admin", new CustomUserDetails.Builder().withFirstName("James")
                .withLastName("Davis")
                .withEmail("james.davis@email.com")
                .withUsername("admin")
                .withPassword(passwordEncoder.encode("password"))
                .withAuthorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUserDetails userDetails = userRegistry.get(username);
        if (userDetails == null) {
            throw new UsernameNotFoundException(username);
        }
        return userDetails;
    }
}
```

此外，为了实现所需的loadUserByUsername() 方法，我们通过用户名从注册表Map中获取相应的CustomUserDetails对象。但是，用户详细信息将在生产环境中的存储库中存储和检索。

## 4. Spring Security配置

首先，我们需要在Spring Security的配置中添加UserDetailsService ，它将被注入到CustomUserDetailsService实现。此外，我们通过相应的方法将其设置在HttpSecurity实例上，其余的只是最低限度的安全配置，需要对用户进行身份验证并配置/login、/logout和/index端点：

```java
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    // omitting constructor

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.userDetailsService(userDetailsService)
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .successForwardUrl("/index")
                .and()
                .logout()
                .permitAll()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login");
    }
}
```

## 5. 显示登录用户信息

**Thymeleaf Extras模块提供对Authentication对象的访问权限**，并且使用Security Dialect，我们可以在 Thymeleaf页面上显示登录用户信息。

CustomUserDetails对象可以通过Authentication对象的principal字段访问，例如，我们可以使用sec:authentication=”principal.firstName”访问firstName字段：

```xml
<!DOCTYPE html>
<html xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
    <head>
        <title>Welcome to Spring Security Thymeleaf tutorial</title>
    </head>
    <body>
        <h2>Welcome</h2>
        <p>Spring Security Thymeleaf tutorial</p>
        <div sec:authorize="hasRole('USER')">Text visible to user.</div>
        <div sec:authorize="hasRole('ADMIN')">Text visible to admin.</div>
        <div sec:authorize="isAuthenticated()">Text visible only to authenticated users.</div>
        Authenticated username:
        <div sec:authentication="name"></div>
        Authenticated user's firstName:
        <div sec:authentication="principal.firstName"></div>
        Authenticated user's lastName:
        <div sec:authentication="principal.lastName"></div>
        Authenticated user's email:
        <div sec:authentication="principal.lastName"></div>
        Authenticated user roles:
        <div sec:authentication="principal.authorities"></div>
    </body>
</html>
```

或者，编写不带sec:authentication属性的Security方言表达式的等效语法是使用Spring表达式语言。因此，如果我们更倾向于这种方法，我们可以使用Spring表达式语言格式显示firstName字段：

```html
<div th:text="${#authentication.principal.firstName}"></div>
```

## 6. 总结

在本文中，我们了解了如何在Spring Boot应用程序中使用Spring Security的支持在Thymeleaf中显示登录用户的信息。