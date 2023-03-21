## 1. 概述

在本教程中，我们将学习**如何使用Spring OAuth2RestTemplate进行OAuth2 REST调用**。

我们将创建一个能够列出GitHub帐户仓库的SpringWeb应用程序。

## 2. Maven配置

首先，我们需要将[spring-boot-starter-security](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-security/3.0.4)和[spring-security-oauth2-autoconfigure](https://central.sonatype.com/artifact/org.springframework.security.oauth.boot/spring-security-oauth2-autoconfigure/2.6.8)依赖项添加到我们的pom.xml中。在构建Web应用程序时，我们还需要包含[spring-boot-starter-web](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-web/3.0.4)和[spring-boot-starter-thymeleaf](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-thymeleaf/3.0.4)工件。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.security.oauth.boot</groupId>
    <artifactId>spring-security-oauth2-autoconfigure</artifactId>
    <version>2.6.8</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

## 3. OAuth2属性

接下来，让我们将OAuth配置添加到我们的application.properties文件中，以便能够连接GitHub帐户：

```properties
github.client.clientId=[CLIENT_ID]
github.client.clientSecret=[CLIENT_SECRET]
github.client.userAuthorizationUri=https://github.com/login/oauth/authorize
github.client.accessTokenUri=https://github.com/login/oauth/access_token
github.client.clientAuthenticationScheme=form

github.resource.userInfoUri=https://api.github.com/user
github.resource.repoUri=https://api.github.com/user/repos
```

请注意，我们需要将\[CLIENT_ID\]和\[CLIENT_SECRET\]替换为来自GitHub OAuth应用程序的值。我们可以按照[创建OAuth应用程序](https://docs.github.com/en/developers/apps/building-oauth-apps/creating-an-oauth-app)指南在GitHub上注册新应用程序：

<img src="../assets/img_7.png">

让我们确保授权回调URL设置为[http://localhost:8080](http://localhost:8080)，这会将OAuth流重定向到我们的Web应用程序主页。

## 4. OAuth2RestTemplate配置

现在，是时候创建一个安全配置来为我们的应用程序提供OAuth2支持了。

### 4.1 安全配置类

首先，让我们创建Spring的安全配置：

```java
@Configuration
@EnableOAuth2Client
public class SecurityConfig {
    OAuth2ClientContext oauth2ClientContext;

    public SecurityConfig(OAuth2ClientContext oauth2ClientContext) {
        this.oauth2ClientContext = oauth2ClientContext;
    }

    // ...
}
```

@EnableOAuth2Client使我们能够访问OAuth2上下文，我们将使用它来创建OAuth2RestTemplate。

### 4.2 OAuth2RestTemplateBean

其次，我们将为OAuth2RestTemplate创建bean：

```java
@Bean
public OAuth2RestTemplate restTemplate() {
    return new OAuth2RestTemplate(githubClient(), oauth2ClientContext);
}

@Bean
@ConfigurationProperties("github.client")
public AuthorizationCodeResourceDetails githubClient() {
    return new AuthorizationCodeResourceDetails();
}
```

有了这个，我们使用OAuth2属性和上下文来创建模板的实例。

@ConfigurationProperties注解将所有github.client属性注入到AuthorizationCodeResourceDetails实例。

### 4.3 身份验证过滤器

第三，我们需要一个身份验证过滤器来处理OAuth2流程：

```java
private Filter oauth2ClientFilter() {
    OAuth2ClientAuthenticationProcessingFilter oauth2ClientFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/github");
    OAuth2RestTemplate restTemplate = restTemplate();
    oauth2ClientFilter.setRestTemplate(restTemplate);
    UserInfoTokenServices tokenServices = new UserInfoTokenServices(githubResource().getUserInfoUri(), githubClient().getClientId());
    tokenServices.setRestTemplate(restTemplate);
    oauth2ClientFilter.setTokenServices(tokenServices);
    return oauth2ClientFilter;
}

@Bean
@ConfigurationProperties("github.resource")
public ResourceServerProperties githubResource() {
    return new ResourceServerProperties();
}
```

在这里，我们指示过滤器在我们应用程序的/login/github URL上启动OAuth2流。

### 4.4 Spring Security配置

最后，让我们注册OAuth2ClientContextFilter并创建一个Web安全配置：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/", "/login**", "/error**")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .logout()
        .logoutUrl("/logout")
        .logoutSuccessUrl("/")
        .and()
        .addFilterBefore(oauth2ClientFilter(), BasicAuthenticationFilter.class);
    return http.build();
}

@Bean
public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
    FilterRegistrationBean<OAuth2ClientContextFilter> registration = new FilterRegistrationBean<>();
    registration.setFilter(filter);
    registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
    return registration;
}
```

我们保护我们的Web应用程序路径并确保OAuth2ClientAuthenticationProcessingFilter在BasicAuthenticationFilter之前注册。

## 5. 使用OAuth2RestTemplate

**OAuth2RestTemplate的主要目标是减少进行基于OAuth2的API调用所需的代码**。它基本上满足了我们应用程序的两个需求：

-   处理OAuth2身份验证流程
-   扩展Spring RestTemplate以进行API调用

我们现在可以在Web控制器中将OAuth2RestTemplate用作自动注入的bean。

### 5.1 登录

让我们创建带有登录和主页选项的index.html文件：

```xml
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
    <head>
        <title>OAuth2Client</title>
    </head>
    <body>
        <h3>
            <a href="/login/github" th:href="@{/home}" th:if="${#httpServletRequest?.remoteUser != undefined }">
                Go to Home
            </a>
            <a href="/hello" th:href="@{/login/github}" th:if="${#httpServletRequest?.remoteUser == undefined }">
                GitHub Login
            </a>
        </h3>
    </body>
</html>
```

未经身份验证的用户将看到登录选项，而经过身份验证的用户可以访问主页。

### 5.2 主页

现在，让我们创建一个控制器来问候经过身份验证的GitHub用户：

```java
@Controller
public class AppController {

    OAuth2RestTemplate restTemplate;

    public AppController(OAuth2RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/home")
    public String welcome(Model model, Principal principal) {
        model.addAttribute("name", principal.getName());
        return "home";
    }
}
```

请注意，我们在welcome方法中有一个security Principal参数。我们使用Principal的名称作为UI模型的属性。

让我们来看看home.html模板：

```xml
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
    <head>
        <title>Home</title>
    </head>
    <body>
        <p>
            Welcome <b th:inline="text"> [[${name}]] </b>
        </p>
        <h3>
            <a href="/repos">View Repositories</a><br/><br/>
        </h3>

        <form th:action="@{/logout}" method="POST">
            <input type="submit" value="Logout"/>
        </form>
    </body>
</html>
```

此外，我们还添加了查看用户仓库列表的链接和注销选项。

### 5.3 GitHub仓库

现在，是时候使用在之前的控制器中创建的OAuth2RestTemplate来呈现用户拥有的所有GitHub仓库了。

首先，我们需要创建GithubRepo类来表示一个仓库：

```java
public class GithubRepo {
    Long id;
    String name;

    // getters and setters
}
```

其次，让我们在之前的AppController中添加一个访问仓库的映射：

```java
@GetMapping("/repos")
public String repos(Model model) {
    Collection<GithubRepo> repos = restTemplate.getForObject("https://api.github.com/user/repos", Collection.class);
    model.addAttribute("repos", repos);
    return "repositories";
}
```

**OAuth2RestTemplate处理向GitHub发出请求的所有样板代码**。此外，它将REST响应转换为GithubRepo集合。

最后，让我们创建repositories.html模板来遍历repositories集合：

```xml
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
    <head>
        <title>Repositories</title>
    </head>
    <body>
        <p>
            <h2>Repos</h2>
        </p>
        <ul th:each="repo: ${repos}">
            <li th:text="${repo.name}"></li>
        </ul>
    </body>
</html>
```

## 6. 总结

在本文中，我们学习了**如何使用OAuth2RestTemplate来简化对GitHub等OAuth2资源服务器的REST调用**。

我们浏览了运行OAuth2流程的Web应用程序的构建块。然后，我们看到了如何调用REST API来检索所有GitHub用户的仓库。