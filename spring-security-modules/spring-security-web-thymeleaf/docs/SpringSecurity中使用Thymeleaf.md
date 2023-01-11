## 1. 概述

**在本快速教程中，我们重点介绍使用Thymeleaf与Spring Security**。我们创建一个Spring Boot应用程序，在其中演示Security方言的用法。

我们选择的前端技术是[Thymeleaf](http://www.thymeleaf.org/index.html)，它是一个现代的服务器端Web模板引擎，与Spring MVC框架具有良好的集成。

最后，Spring Security Dialect是一个Thymeleaf附加模块，它有助于将这两者集成在一起。

## 2. 依赖

首先，我们将新的依赖添加到我们的Maven pom.xml中：

```xml
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity5</artifactId>
</dependency>
```

## 3. Spring Security配置

接下来，让我们定义Spring Security的配置，我们还需要至少两个不同的用户来演示安全方言的用法：

```java

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    // [...] 
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user").password(passwordEncoder().encode("password")).roles("USER")
                .and()
                .withUser("admin").password(passwordEncoder().encode("admin")).roles("ADMIN");
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

如我们所见，在configureGlobal(AuthenticationManagerBuilder auth)中，我们定义了两个用户名和密码，我们可以使用这些凭证来访问我们的应用程序。

这两个用户有不同的角色：分别是ADMIN和USER，因此我们可以根据角色向他们呈现特定的内容。

## 4. 安全方言

**Spring Security方言允许我们根据用户角色、权限或其他安全表达式有条件地显示内容**。它还允许我们能够访问 Spring Authentication对象。

让我们看一下index页面，其中包含安全方言的示例：

```xml
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
    <head>
        <title>Welcome to Spring Security Thymeleaf tutorial</title>
    </head>
    <body>
        <h2>Welcome</h2>
        <p>Spring Security Thymeleaf tutorial</p>
        <div sec:authorize="hasRole('USER')">Text visible to user.</div>
        <div sec:authorize="hasRole('ADMIN')">Text visible to admin.</div>
        <div sec:authorize="isAuthenticated()">
            Text visible only to authenticated users.
        </div>
        Authenticated username:
        <div sec:authentication="name"></div>
        Authenticated user roles:
        <div sec:authentication="principal.authorities"></div>
    </body>
</html>
```

我们可以看到特定于Spring Security Dialect的属性：sec:authorize和sec:authentication。

### 4.1 sec:authorize

**简单地说，我们使用sec:authorize属性来控制显示的内容**。

例如，如果我们只想向角色为USER的用户显示内容，我们可以执行以下操作：

```html
<div sec:authorize="hasRole('USER')">
```

并且，如果我们想授权对所有经过身份验证的用户的访问，我们可以使用以下表达式：

```html
<div sec:authorize="isAuthenticated()">
```

### 4.2 sec:authentication

Spring Security [Authentication](https://docs.spring.io/spring-security/site/docs/5.0.3.RELEASE/api/org/springframework/security/core/Authentication.html)接口公开了有关经过身份验证的主体或身份验证请求的有用方法。

**要使用Thymeleaf访问Authentication对象，我们可以简单地使用<div sec:authentication="name"\>或<div sec:authentication="principal.authorities"\>**，前者使我们可以访问经过身份验证的用户的名称，后者使我们可以访问经过身份验证的用户的角色。

## 5. 总结

在本文中，我们在一个简单的Spring Boot应用程序中介绍了Thymeleaf中的Spring Security支持。