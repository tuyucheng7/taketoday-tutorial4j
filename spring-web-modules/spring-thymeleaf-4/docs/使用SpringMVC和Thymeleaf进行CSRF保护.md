## 1. 简介

[Thymeleaf](http://www.thymeleaf.org/)是一个Java模板引擎，用于处理和创建HTML、XML、JavaScript、CSS和纯文本。有关Thymeleaf和Spring的介绍，请查看[这篇文章](https://www.baeldung.com/thymeleaf-in-spring-mvc)。

在本文中，我们将讨论如何使用Thymeleaf应用程序在Spring MVC中防止跨站点请求伪造(CSRF)攻击。更具体地说，我们将测试针对HTTP POST方法的CSRF攻击。

CSRF是一种攻击，它迫使最终用户在当前已通过身份验证的Web应用程序中执行不需要的操作。

## 2. Maven依赖

首先，让我们看看将Thymeleaf与Spring集成所需的配置。我们的依赖项中需要thymeleaf-spring库：

```xml
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf</artifactId>
    <version>3.0.11.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf-spring5</artifactId>
    <version>3.0.11.RELEASE</version>
</dependency>
```

请注意，对于Spring 4项目，必须使用thymeleaf-spring4库而不是thymeleaf-spring5。可以在[此处](https://search.maven.org/search?q=a:thymeleaf-spring5)找到最新版本的依赖项。

此外，为了使用Spring Security，我们需要添加以下依赖项：

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-web</artifactId>
    <version>5.7.3</version>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
    <version>5.7.3</version>
</dependency>
```

两个Spring Security相关库的最新版本可在[此处](https://search.maven.org/search?q=a:spring-security-web)和[此处](https://search.maven.org/search?q=a:spring-security-config)获得。

## 3. Java配置

除了[此处](https://www.baeldung.com/thymeleaf-in-spring-mvc)介绍的Thymeleaf配置外，我们还需要添加Spring Security的配置。为此，我们需要创建类：

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebMVCSecurity {

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withUsername("user1")
              .password("{noop}user1Pass")
              .authorities("ROLE_USER")
              .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
              .antMatchers("/resources/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
              .anyRequest()
              .authenticated()
              .and()
              .httpBasic();
        return http.build();
    }
}
```

有关安全配置的更多详细信息和说明，请参阅[Security与Spring](https://www.baeldung.com/security-spring)系列。

Java配置默认启用CSRF保护，为了禁用这个有用的功能，我们需要在configure(...)方法中添加：

```java
.csrf().disable()
```

在XML配置中我们需要手动指定CSRF保护，否则将不起作用：

```xml
<security:http
        auto-config="true"
        disable-url-rewriting="true"
        use-expressions="true">
    <security:csrf/>

    <!-- Remaining configuration ... -->
</security:http>
```

另请注意，如果我们使用带有登录表单的登录页面，我们需要始终在登录表单中将CSRF令牌作为隐藏参数手动包含在代码中：

```html
<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
```

对于其余表单，CSRF令牌将自动添加到具有隐藏输入的表单中：

```html
<input type="hidden" name="_csrf" value="32e9ae18-76b9-4330-a8b6-08721283d048" /> 
<!-- Example token -->
```

## 4. 视图配置

让我们继续HTML文件的主要部分，包括表单操作和测试程序创建。在第一个视图中，我们尝试将新学生添加到列表中：

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Add Student</title>
</head>
<body>
<h1>Add Student</h1>
<form action="#" th:action="@{/saveStudent}" th:object="${student}"
      method="post">
    <ul>
        <li th:errors="*{id}" />
        <li th:errors="*{name}" />
        <li th:errors="*{gender}" />
        <li th:errors="*{percentage}" />
    </ul>
    <!-- Remaining part of HTML -->
</form>
</body>
</html>
```

在此视图中，我们通过提供id、name、gender和percentage(可选，如表单验证中所述)将学生添加到列表中。在我们执行此表单之前，我们需要提供用户名和密码，以便在Web应用程序中对我们进行身份验证。

### 4.1 浏览器CSRF攻击测试

现在我们继续第二个HTML视图，它的目的是尝试进行CSRF攻击：

```html
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
<body>
<form action="http://localhost:8080/spring-thymeleaf/saveStudent" method="post">
    <input type="hidden" name="payload" value="CSRF attack!"/>
    <input type="submit"/>
</form>
</body>
</html>
```

我们知道action URL是http://localhost:8080/spring-thymeleaf/saveStudent，黑客想要访问这个页面来进行攻击。

为了测试，在另一个浏览器中打开HTML文件，而不登录到应用程序。当你尝试提交表单时，我们将收到页面：

<img src="../assets/img.png"> 

我们的请求被拒绝了，因为我们发送了一个没有CSRF令牌的请求。

请注意，HTTP会话用于存储CSRF令牌。当发送请求时，Spring将生成的令牌与存储在会话中的令牌进行比较，以确认用户没有被黑客攻击。

### 4.2 JUnit CSRF攻击测试

如果你不想使用浏览器测试CSRF攻击，你也可以通过快速集成测试来完成；让我们从该测试的Spring配置开始：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebApp.class, WebMVCConfig.class, WebMVCSecurity.class, InitSecurity.class })
public class CsrfEnabledIntegrationTest {
    // configuration
}
```

并继续进行实际测试：

```java
@Test
public void addStudentWithoutCSRF() throws Exception {
    mockMvc.perform(post("/saveStudent").contentType(MediaType.APPLICATION_JSON)
        .param("id", "1234567").param("name", "Joe").param("gender", "M")
        .with(testUser())).andExpect(status().isForbidden());
}

@Test
public void addStudentWithCSRF() throws Exception {
    mockMvc.perform(post("/saveStudent").contentType(MediaType.APPLICATION_JSON)
        .param("id", "1234567").param("name", "Joe").param("gender", "M")
        .with(testUser()).with(csrf())).andExpect(status().isOk());
}
```

由于缺少CSRF令牌，第一个测试将导致禁止状态，而第二个测试将正确执行。

## 5. 总结

在本文中，我们讨论了如何使用Spring Security和Thymeleaf框架来防止CSRF攻击。