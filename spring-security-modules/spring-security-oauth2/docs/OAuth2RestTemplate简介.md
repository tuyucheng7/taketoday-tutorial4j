## 1. 概述

在本教程中，我们将学习如何使用 Spring OAuth2RestTemplate进行 OAuth2 REST 调用。

我们将创建一个能够列出 GitHub 帐户的存储库的 Spring Web 应用程序。

## 2.Maven配置

首先，我们需要将[spring-boot-starter-security](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-security)和 [spring-security-oauth2-autoconfigure](https://search.maven.org/search?q=g:org.springframework.security.oauth.boot a:spring-security-oauth2-autoconfigure)依赖项添加到我们的pom.xml中。在构建 Web 应用程序时，我们还需要包含 [spring-boot-starter-web](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-web)和 [spring-boot-starter-thymeleaf](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-thymeleaf)工件。

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

## 3. OAuth2 属性

接下来，让我们将 OAuth 配置添加到我们的application.properties文件中，以便能够连接 GitHub 帐户：

```properties
github.client.clientId=[CLIENT_ID]
github.client.clientSecret=[CLIENT_SECRET]
github.client.userAuthorizationUri=https://github.com/login/oauth/authorize
github.client.accessTokenUri=https://github.com/login/oauth/access_token
github.client.clientAuthenticationScheme=form

github.resource.userInfoUri=https://api.github.com/user
github.resource.repoUri=https://api.github.com/user/repos
```

请注意，我们需要将[ CLIENT_ID]和 [CLIENT_SECRET]替换为来自 GitHub OAuth 应用程序的值。我们可以按照[创建 OAuth 应用程序](https://docs.github.com/en/developers/apps/building-oauth-apps/creating-an-oauth-app)指南在 GitHub 上注册新应用程序：

[![github-app-注册](https://www.baeldung.com/wp-content/uploads/2022/03/github-app-registering.png)](https://www.baeldung.com/wp-content/uploads/2022/03/github-app-registering.png)

让我们确保授权回调 URL 设置为http://localhost:8080，这会将 OAuth 流重定向到我们的 Web 应用程序主页。

## 4. OAuth2RestTemplate配置

现在，是时候创建一个安全配置来为我们的应用程序提供 OAuth2 支持了。

### 4.1。安全配置类

首先，让我们创建 Spring 的安全配置：

```java
@Configuration
@EnableOAuth2Client
public class SecurityConfig {
    OAuth2ClientContext oauth2ClientContext;

    public SecurityConfig(OAuth2ClientContext oauth2ClientContext) {
        this.oauth2ClientContext = oauth2ClientContext;
    }

    ...
}
```

@EnableOAuth2Client使 我们能够访问 OAuth2 上下文，我们将使用它来创建OAuth2RestTemplate。

### 4.2. OAuth2RestTemplate Bean

其次，我们将为 OAuth2RestTemplate创建 bean ：

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

有了这个，我们使用 OAuth2 属性和上下文来创建模板的实例。

@ConfigurationProperties注解将所有 github.client属性注入到 AuthorizationCodeResourceDetails实例。

### 4.3. 身份验证过滤器

第三，我们需要一个身份验证过滤器来处理 OAuth2 流程：

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

在这里，我们指示过滤器在我们的应用程序的/login/github URL 上启动 OAuth2 流。

### 4.4. Spring 安全配置

最后，让我们注册 OAuth2ClientContextFilter并创建一个 Web 安全配置：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/", "/login", "/error")
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

我们保护我们的 Web 应用程序路径并确保 OAuth2ClientAuthenticationProcessingFilter在BasicAuthenticationFilter之前注册。

## 5. 使用OAuth2RestTemplate

OAuth2RestTemplate的主要目标 是减少进行基于 OAuth2 的 API 调用所需的代码。基本满足了我们应用的两个需求：

-   处理 OAuth2 身份验证流程
-   扩展 Spring RestTemplate以进行 API 调用

我们现在可以将OAuth2RestTemplate用作 Web 控制器中的自动连接 bean。

### 5.1。登录

让我们使用 login 和 home 选项创建index.html文件：

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

### 5.2. 家

现在，让我们创建一个控制器来迎接经过身份验证的 GitHub 用户：

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

请注意，我们在welcome方法中有一个 security Principal参数。我们使用Principal的名称作为 UI 模型的属性。

我们来看看home.html模板：

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

此外，我们正在添加一个链接以查看用户的存储库列表和一个注销选项。

### 5.3. GitHub 存储库

现在，是时候使用在前一个控制器中创建的OAuth2RestTemplate来呈现用户拥有的所有 GitHub 存储库了。

首先，我们需要创建 GithubRepo类来表示存储库：

```java
public class GithubRepo {
    Long id;
    String name;

    // getters and setters

}
```

其次，让我们添加一个存储库映射到之前的 AppController：

```java
@GetMapping("/repos")
public String repos(Model model) {
    Collection<GithubRepo> repos = restTemplate.getForObject("https://api.github.com/user/repos", Collection.class);
    model.addAttribute("repos", repos);
    return "repositories";
}
```

OAuth2RestTemplate处理所有向 GitHub 发出请求的样板代码。此外，它将 REST 响应转换为GithubRepo集合。

最后，让我们创建repositories.html模板来遍历 repositories 集合：

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

## 六. 总结

在本文中，我们学习了如何使用 OAuth2RestTemplate来简化对GitHub 等OAuth2 资源服务器的 REST 调用。

我们浏览了运行 OAuth2 流程的 Web 应用程序的构建块。然后，我们看到了如何调用 REST API 来检索所有 GitHub 用户的存储库。