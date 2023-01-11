## 1. 概述

在本教程中，我们将介绍如何配置Spring Security，以**使用配置中的两个不同Spring Security http元素来处理两个不同的登录页面**。

## 2. 配置两个Http元素

我们可能需要两个登录页面的一种情况是，我们有一个用于应用程序管理员的页面和一个用于普通用户的不同页面。

我们将配置两个http元素，它们将通过与每个相关联的URL模式进行区分：

+ /user*用于需要正常用户身份验证才能访问的页面。
+ /admin*用于将由管理员访问的页面。

每个http元素都有一个不同的登录页面和不同的登录处理URL。

为了配置两个不同的http元素，让我们创建两个带有@Configuration注解的静态类，它们继承WebSecurityConfigurerAdapter。

两个静态类都将放置在常规的@Configuration类中：

```java

@Configuration
@EnableWebSecurity
public class MultipleLoginSecurityConfig {

}
```

让我们为“ADMIN”用户定义WebSecurityConfigurerAdapter：

```java

@Configuration
@Order(1)
public static class App1ConfigurationAdapter extends WebSecurityConfigurerAdapter {

    public App1ConfigurationAdapter() {
        super();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/admin*").authorizeRequests()
                .anyRequest().hasRole("ADMIN")
                // log in
                .and()
                .formLogin()
                .loginPage("/loginAdmin")
                .loginProcessingUrl("/admin_login")
                .failureUrl("/loginAdmin?error=loginError")
                .defaultSuccessUrl("/adminPage")
                // logout
                .and()
                .logout()
                .logoutUrl("/admin_logout")
                .logoutSuccessUrl("/protectedLinks")
                .deleteCookies("JSESSIONID")
                .and()
                .exceptionHandling()
                .accessDeniedPage("/403")
                .and()
                .csrf().disable();
    }
}
```

现在，让我们为普通用户定义WebSecurityConfigurerAdapter：

```java

@Configuration
@Order(2)
public static class App2ConfigurationAdapter extends WebSecurityConfigurerAdapter {

    public App2ConfigurationAdapter() {
        super();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/user*").authorizeRequests()
                .anyRequest().hasRole("USER")
                // log in
                .and()
                .formLogin()
                .loginPage("/loginUser")
                .loginProcessingUrl("/user_login")
                .failureUrl("/loginUser?error=loginError")
                .defaultSuccessUrl("/userPage")
                // logout
                .and()
                .logout()
                .logoutUrl("/user_logout")
                .logoutSuccessUrl("/protectedLinks")
                .deleteCookies("JSESSIONID")
                .and()
                .exceptionHandling()
                .accessDeniedPage("/403")
                .and()
                .csrf().disable();
    }
}
```

请注意，通过在每个静态类上添加@Order注解，我们指定了在请求URL时基于模式匹配考虑这两个类的顺序。

**两个配置类的顺序不能相同**。

## 3. 自定义登录页面

我们将为每种类型的用户创建自己的自定义登录页面。对于管理员用户，处理登录的URL为“admin_login”，如配置中所定义：

```html
<!DOCTYPE html>
<html>
<head>
    <meta content="text/html; charset=UTF-8" http-equiv="Content-Type"/>
    <title>Insert title here</title>
</head>
<body>

<p>Admin login page</p>
<form action="admin_login" method="POST" name="f">

    <table>
        <tr>
            <td>User:</td>
            <td><input name="username" type="text"/></td>
        </tr>
        <tr>
            <td>Password:</td>
            <td><input name="password" type="password"/></td>
        </tr>
        <tr>
            <td><input name="submit" type="submit" value="submit"/></td>
        </tr>
    </table>
</form>
<p th:if="${param.error}">Login failed!</p>
</body>
</html>
```

普通用户登录页面类似，只是处理登录的URL为“user_login”。

## 4. 身份验证配置

**现在我们需要为应用程序配置身份验证**。让我们看看实现这一点的两种方法。一种使用公共源进行用户身份验证，另一种使用两个单独的源。

### 4.1 使用公共用户身份验证源

如果两个登录页面共享一个用于验证用户的公共源，你可以创建一个UserDetailsService类型的bean来处理验证。

让我们使用InMemoryUserDetailsManager来演示这个场景，该管理器定义了两个用户，一个用户的角色是“USER”，另一个是“ADMIN”：

```java
public class MultipleLoginSecurityConfig {

    @Bean
    public static PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() throws Exception {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("user").password(encoder().encode("userPass")).roles("USER").build());
        manager.createUser(User.withUsername("admin").password(encoder().encode("adminPass")).roles("ADMIN").build());
        return manager;
    }
}
```

### 4.2 使用两个不同的用户身份验证源

如果你有不同的用户身份验证源(一个用于管理员，一个用于普通用户)，
你可以在每个静态@Configuration类中配置一个AuthenticationManagerBuilder。

```java
public static class App1ConfigurationAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password(encoder().encode("admin"))
                .roles("ADMIN");
    }
}

public static class App2ConfigurationAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user")
                .password(encoder().encode("user"))
                .roles("USER");
    }
}
```

在这种情况下，将不再使用上一节中的UserDetailsService bean。

## 5. 总结

在这个教程中，我们介绍了如何在同一个Spring Security应用程序中实现两个不同的登录页面。