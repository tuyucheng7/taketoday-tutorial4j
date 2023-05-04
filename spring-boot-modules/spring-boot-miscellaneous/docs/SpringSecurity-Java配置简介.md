## 1. 概述

本文介绍Spring Security的Java配置，使用户无需使用XML即可轻松配置Spring Security。

[Java 配置在Spring 3.1](http://docs.spring.io/spring/docs/3.2.x/spring-framework-reference/html/new-in-3.1.html)中被添加到Spring框架中，并在[Spring 3.2](http://docs.spring.io/spring/docs/3.2.x/spring-framework-reference/html/new-in-3.2.html)中扩展到Spring Security，并定义在一个注解为@Configuration的类中。

## 2.Maven设置

要在Maven项目中使用Spring Security，我们首先需要在项目pom.xml中具有spring-security-core依赖项：

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-core</artifactId>
    <version>5.3.3.RELEASE</version>
</dependency>
```

[总能在这里](https://search.maven.org/classic/#search|ga|1|a%3A"spring-security-core")找到最新版本。

## 3. 使用Java配置的 Web 安全

让我们从一个Spring SecurityJava 配置的基本示例开始：

```java
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) 
      throws Exception {
        auth.inMemoryAuthentication().withUser("user")
          .password(passwordEncoder().encode("password")).roles("USER");
    }
}
```

你可能已经注意到，该配置设置了一个基本的内存中身份验证配置。此外，从Spring5 开始，我们需要一个[PasswordEncoder](https://www.baeldung.com/spring-security-5-default-password-encoder) bean：

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

## 4.HTTP安全

要在Spring中启用HTTP安全性，我们需要创建一个SecurityFilterChain bean：

```java
@Bean 
 public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeRequests()
      .anyRequest().authenticated()
      .and().httpBasic();
    return http.build();
}

```

上述配置确保对应用程序的任何请求都通过基于表单的登录或HTTP基本身份验证进行身份验证。

此外，它与以下XML配置完全相似：

```xml
<http>
    <intercept-url pattern="/**" access="isAuthenticated()"/>
    <form-login />
    <http-basic />
</http>
```

## 5.表单登录

有趣的是，Spring Security会自动生成一个登录页面，它基于启用的功能并使用处理提交登录的URL的标准值：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeRequests()
      .anyRequest().authenticated()
      .and().formLogin()
      .loginPage("/login").permitAll();
    return http.build();
}
```

这里自动生成登录页面，方便快速上手运行。

## 6. 角色授权

现在让我们使用角色在每个URL上配置一些简单的授权：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeRequests()
      .antMatchers("/", "/home").access("hasRole('USER')")
      .antMatchers("/admin/**").hasRole("ADMIN")
      .and()
      // some more method calls
      .formLogin();
    return http.build();
}
```

请注意我们如何通过访问使用类型安全的API– hasRole – 以及基于表达式的API。

## 7.注销

与Spring Security的许多其他方面一样，注销具有框架提供的一些很棒的默认值。

默认情况下，注销请求会使会话无效，清除所有身份验证缓存，清除 SecurityContextHolder并重定向到登录页面。

这是一个简单的注销配置：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.logout();
    return http.build();
}
```

但是，如果你想更好地控制可用的处理程序，则更完整的实现如下所示：

```java
@Bean
 public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.logout().logoutUrl("/my/logout")
      .logoutSuccessUrl("/my/index")
      .logoutSuccessHandler(logoutSuccessHandler) 
      .invalidateHttpSession(true)
      .addLogoutHandler(logoutHandler)
      .deleteCookies(cookieNamesToClear)
      .and()
      // some other method calls
    return http.build();
}
```

## 8.认证

让我们看看另一种允许使用Spring Security进行身份验证的方法。

### 8.1. 内存中身份验证

我们将从一个简单的内存配置开始：

```java
@Autowired
public void configureGlobal(AuthenticationManagerBuilder auth) 
  throws Exception {
    auth.inMemoryAuthentication()
      .withUser("user").password(passwordEncoder().encode("password")).roles("USER")
      .and()
      .withUser("admin").password(passwordEncoder().encode("password")).roles("USER", "ADMIN");
}

```

### 8.2. JDBC 认证

要将其移动到 JDBC，你所要做的就是在应用程序中定义一个数据源——并直接使用它：

```java
@Autowired
private DataSource dataSource;

@Autowired
public void configureGlobal(AuthenticationManagerBuilder auth) 
  throws Exception {
    auth.jdbcAuthentication().dataSource(dataSource)
      .withDefaultSchema()
      .withUser("user").password(passwordEncoder().encode("password")).roles("USER")
      .and()
      .withUser("admin").password(passwordEncoder().encode("password")).roles("USER", "ADMIN");
}

```

当然，对于上述两个示例，我们还需要定义第 3 节中概述的PasswordEncoder bean。

## 9.总结

在这个快速教程中，我们回顾了Spring Security的Java配置的基础知识，并重点介绍了说明最简单配置场景的代码示例。