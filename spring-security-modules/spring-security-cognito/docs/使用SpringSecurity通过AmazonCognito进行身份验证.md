## 1. 简介

在本教程中，我们将了解如何使用[Spring Security](https://www.baeldung.com/security-spring)的 OAuth 2.0 支持通过[Amazon Cognito](https://aws.amazon.com/cognito/)进行身份验证。

在此过程中，我们将简要了解一下 Amazon Cognito 是什么以及它支持哪种[OAuth 2.0](https://oauth.net/2/)流。

最后，我们将有一个简单的单页应用程序。没有什么花哨。

## 2. 什么是亚马逊Cognito？

Cognito 是一种用户身份和数据同步服务，使我们能够轻松地跨多个设备管理我们的应用程序的用户数据。

借助 Amazon Cognito，我们可以：

-   为我们的应用程序创建、验证和授权用户
-   为使用Google、Facebook 或 Twitter等其他公共[身份提供商](https://en.wikipedia.org/wiki/Identity_provider)的应用用户创建身份
-   将我们应用的用户数据保存在键值对中

## 3. 设置

### 3.1 亚马逊学习设置

作为身份提供者，Cognito 支持 authentication_code [、implicit和client_credentials授权](https://oauth.net/2/grant-types/)。出于我们的目的，让我们设置使用authorization_code授权类型。

首先，我们需要一些 Cognito 设置：

-   [创建用户池](https://docs.aws.amazon.com/cognito/latest/developerguide/tutorial-create-user-pool.html)
-   [添加一个用户](https://docs.aws.amazon.com/cognito/latest/developerguide/signing-up-users-in-your-app.html)——我们将使用这个用户登录我们的 Spring 应用程序
-   [创建应用客户端](https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-settings-client-apps.html)
-   [配置应用客户端](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-pools-app-idp-settings.html)

在应用程序客户端的配置中，确保CallbackURL 与Spring 配置文件中的redirect-uri匹配。在我们的例子中，这将是：

```xml
http://localhost:8080/login/oauth2/code/cognito
```

Allowed OAuth flow应该是Authorization code grant。然后，在同一页面上，我们需要将Allowed OAuth 范围设置为openid。

要将用户重定向到 Cognito 的自定义登录页面，我们还需要添加一个[用户池域](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-pools-assign-domain.html)。

### 3.2. 弹簧设置

由于我们想使用 OAuth 2.0 登录，我们需要将[spring-security-oauth2-client](https://search.maven.org/search?q=a:spring-security-oauth2-client g:org.springframework.security)和[spring-security-oauth2-jose](https://search.maven.org/search?q=a:spring-security-oauth2-jose g:org.springframework.security)依赖项添加到我们的应用程序中：

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-oauth2-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-oauth2-jose</artifactId>
</dependency>
```

然后，我们需要一些配置来将所有内容绑定在一起：

```yaml
spring:
    security:
        oauth2:
            client:
                registration:
                    cognito:
                        clientId: clientId
                        clientSecret: clientSecret
                        scope: openid
                        redirect-uri: http://localhost:8080/login/oauth2/code/cognito
                        clientName: clientName
                provider:
                    cognito:
                        issuerUri: https://cognito-idp.{region}.amazonaws.com/{poolId}
                        user-name-attribute: cognito:username
```

在上述配置中，属性clientId、clientSecret、clientName和issuerUri应根据我们在 AWS 上创建的用户池和应用程序客户端进行填充。

有了这个，我们应该设置 Spring 和 Amazon Cognito！ 本教程的其余部分定义了我们应用程序的安全配置，然后只是将一些松散的部分联系起来。

### 3.3. Spring 安全配置

现在我们将添加一个安全配置类：

```java
@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
            .and()
            .authorizeRequests(authz -> authz.mvcMatchers("/")
                .permitAll()
                .anyRequest()
                .authenticated())
            .oauth2Login()
            .and()
            .logout()
            .logoutSuccessUrl("/");
        return http.build();
    }
}
```

在这里，我们首先指定我们需要防止 CSRF 攻击，然后允许每个人访问我们的登录页面。之后，我们添加了对oauth2Login的调用以连接Cognito 客户端注册。

## 4.添加登陆页面

接下来，我们添加一个简单的[Thymeleaf](https://www.baeldung.com/thymeleaf-in-spring-mvc)登录页面，以便我们知道何时登录：

```html
<div>
    <h1 class="title">OAuth 2.0 Spring Security Cognito Demo</h1>
    <div sec:authorize="isAuthenticated()">
        <div class="box">
            Hello, <strong th:text="${#authentication.name}"></strong>!
        </div>
    </div>
    <div sec:authorize="isAnonymous()">
        <div class="box">
            <a class="button login is-primary" th:href="@{/oauth2/authorization/cognito}">
              Log in with Amazon Cognito</a>
        </div>
    </div>
</div>
```

简而言之，这将在我们登录时显示我们的用户名，或者在我们未登录时显示登录链接。请密切注意链接的外观，因为它会 从我们的配置文件中获取cognito部分。

然后让我们确保将应用程序根绑定到我们的欢迎页面：

```java
@Configuration
public class CognitoWebConfiguration implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
    }
}
```

## 5. 运行应用程序

这个类将使与身份验证相关的所有内容都运行起来：

```java
@SpringBootApplication
public class SpringCognitoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringCognitoApplication.class, args);
    }
}
```

现在我们可以启动我们的应用程序，访问[http://localhost:8080](http://localhost:8080/)，然后单击登录链接。在为我们在 AWS 上创建的用户输入凭证时，我们应该能够看到一条Hello, username消息。

## 6. 总结

在本教程中，我们了解了如何通过一些简单的配置将 Spring Security 与 Amazon Cognito 集成。然后我们用几段代码把所有东西放在一起。