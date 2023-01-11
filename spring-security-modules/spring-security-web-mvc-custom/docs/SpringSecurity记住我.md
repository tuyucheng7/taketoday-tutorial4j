## 1. 概述

本教程将展示如何使用 Spring Security 在 Web 应用程序中启用和配置记住我功能。已经讨论了设置[具有安全性和简单表单登录的 MVC 应用程序。](https://www.baeldung.com/spring-security-login)

该机制将能够跨多个会话识别用户——因此首先要了解的是，记住我仅在会话超时后才会启动。默认情况下，这发生在 30 分钟不活动后，但[可以](https://www.baeldung.com/servlet-session-timeout)在web.xml中配置超时。

注意：本教程重点介绍标准的基于 cookie 的方法。对于持久化方法，请查看[Spring Security – Persistent Remember Me](https://www.baeldung.com/spring-security-persistent-remember-me)指南。

## 2. 安全配置

让我们看看如何使用Java设置安全配置：

```java
@Configuration
@EnableWebSecurity
public class SecSecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
            .build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withUsername("user1")
            .password("{noop}user1Pass")
            .authorities("ROLE_USER")
            .build();
        UserDetails admin = User.withUsername("admin1")
            .password("{noop}admin1Pass")
            .authorities("ROLE_ADMIN")
            .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/anonymous")
            .anonymous()
            .antMatchers("/login")
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .formLogin()
            .loginPage("/login.html")
            .loginProcessingUrl("/login")
            .failureUrl("/login.html?error=true")
            .and()
            .logout()
            .deleteCookies("JSESSIONID")
            .and()
            .rememberMe()
            .key("uniqueAndSecret");
        return http.build();
    }
}
```

如所见，使用rememberMe()方法的基本配置非常简单，同时通过附加选项保持非常灵活。密钥在这里很重要——它是整个应用程序的私有值秘密，将在生成令牌的内容时使用。

此外，可以使用tokenValiditySeconds()将令牌的有效时间从默认的 2 周配置为 - 例如 - 一天 ：

```java
rememberMe().key("uniqueAndSecret").tokenValiditySeconds(86400)
```

我们还可以看看等效的 XML 配置：

```java
<http use-expressions="true">
    <intercept-url pattern="/anonymous" access="isAnonymous()" />
    <intercept-url pattern="/login" access="permitAll" />
    <intercept-url pattern="/" access="isAuthenticated()" />

    <form-login login-page='/login.html' 
      authentication-failure-url="/login.html?error=true" />
    <logout delete-cookies="JSESSIONID" />

    <remember-me key="uniqueAndSecret"/>
</http>

<authentication-manager id="authenticationManager">
    <authentication-provider>
        <user-service>
            <user name="user1" password="{noop}user1Pass" authorities="ROLE_USER" />
            <user name="admin1" password="{noop}admin1Pass" authorities="ROLE_ADMIN" />
        </user-service>
    </authentication-provider>
</authentication-manager>
```

## 3. 登录表格

登录表单类似于[我们用于表单登录的表单](https://www.baeldung.com/spring-security-login#login-form)：

```xml
<html>
<head></head>

<body>
    <h1>Login</h1>

    <form name='f' action="login" method='POST'>
        <table>
            <tr>
                <td>User:</td>
                <td><input type='text' name='username' value=''></td>
            </tr>
            <tr>
                <td>Password:</td>
                <td><input type='password' name='password' /></td>
            </tr>
            <tr>
                <td>Remember Me:</td>
                <td><input type="checkbox" name="remember-me" /></td>
            </tr>
            <tr>
                <td><input name="submit" type="submit" value="submit" /></td>
            </tr>
        </table>
    </form>

</body>
</html>
```

注意新添加的复选框输入 - 映射到remember-me。这个添加的输入足以在记住我激活的情况下登录。

此默认路径也可以更改如下：

```java
.rememberMe().rememberMeParameter("remember-me-new")
```

## 4. 饼干

当用户登录时，该机制将创建一个额外的 cookie——“remember-me”cookie。

记住我 cookie包含以下数据：

-   用户名- 标识登录的主体
-   expireTime – 使 cookie 过期；默认为 2 周
-   MD5 哈希——前 2 个值——用户名和过期时间，加上密码和预定义的密钥

这里首先要注意的是用户名和密码都是 cookie 的一部分——这意味着，如果其中任何一个被更改，cookie 将不再有效。此外，可以从 cookie 中读取用户名。

此外，重要的是要了解，如果记住我 cookie 被捕获，此机制可能会受到攻击。cookie 将有效且可用，直到过期或更改凭据。

## 5. 在实践中

要轻松查看记住我机制的工作情况，可以：

-   登录时记得我活跃
-   等待会话过期(或删除浏览器中的JSESSIONID cookie)
-   刷新页面

在不记得我的情况下，cookie 过期后，用户应该被重定向回登录页面。记住我，用户现在在新令牌/cookie 的帮助下保持登录状态。

## 六. 总结

本教程展示了如何在安全配置中设置和配置记住我功能，并简要描述了进入 cookie 的数据类型。

该实现可以在[示例 Github 项目](https://github.com/eugenp/tutorials/tree/master/spring-security-modules/spring-security-web-mvc-custom)中找到——这是一个基于 Eclipse 的项目，因此它应该很容易导入并按原样运行。

当项目在本地运行时，可以在[localhost上访问](http://localhost:8080/spring-security-mvc-custom/login.html)login.html。