## 1. 概述

从5.1.x GA开始，Spring Security添加了对WebFlux的OAuth支持。

我们将讨论如何配置我们的WebFlux应用程序以使用OAuth2登录支持。我们还将讨论如何使用WebClient访问OAuth2安全资源。

Webflux 的OAuth登录配置类似于标准Web MVC应用程序的配置。有关这方面的更多详细信息，另请参阅我们关于[Spring OAuth2 Login元素](https://www.baeldung.com/spring-security-5-oauth2-login)的文章。

## 2. Maven配置

首先，我们将创建一个简单的Spring Boot应用程序并将这些依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-oauth2-client</artifactId>
</dependency>
```

Maven Central上提供了[spring-boot-starter-security](https://search.maven.org/search?q=spring-boot-starter-security)、[spring-boot-starter-webflux](https://search.maven.org/search?q=a:spring-boot-starter-webflux)和[spring-security-oauth2-client](https://search.maven.org/search?q=a:spring-security-oauth-client)依赖项。

## 3. RestController

接下来，我们将添加一个简单的控制器来在主页上显示用户名：

```java
@RestController
public class MainController {

	@GetMapping("/")
	public Mono<String> index(@AuthenticationPrincipal Mono<OAuth2User> oauth2User) {
		return oauth2User
			.map(OAuth2User::getName)
			.map(name -> String.format("Hi, %s", name));
	}
}
```

请注意，我们将显示从OAuth2客户端UserInfo端点获得的用户名。

## 4. 使用谷歌登录

现在，我们将配置我们的应用程序以支持使用Google登录。

首先，我们需要在[Google Developer Console](https://console.developers.google.com/)新建一个项目。

现在，我们需要添加OAuth2凭据(Create Credentials > OAuth Client ID)。

接下来，我们将其添加到“Authorized Redirect URIs”：

```bash
http://localhost:8080/login/oauth2/code/google
```

然后，我们需要配置application.yml以使用Client ID和Secret：

```yaml
spring:
    security:
        oauth2:
            client:
                registration:
                    google:
                        client-id: YOUR_APP_CLIENT_ID
                        client-secret: YOUR_APP_CLIENT_SECRET
```

由于我们的路径中有spring-security-oauth2-client，我们的应用程序将得到保护。

用户将被重定向到使用Google登录，然后才能访问我们的主页。

## 5. 使用Auth Provider登录

我们还可以将我们的应用程序配置为从自定义授权服务器登录。

在下面的示例中，我们将使用[上一篇文章](https://www.baeldung.com/rest-api-spring-oauth2-angularjs)中的授权服务器。

这一次，我们需要配置更多的属性，而不仅仅是ClientID和Client Secret：

```yaml
security:
    oauth2:
        client:
            registration:
                custom:
                    client-id: fooClientIdPassword
                    client-secret: secret
                    scopes: read,foo
                    authorization-grant-type: authorization_code
                    redirect-uri-template: http://localhost:8080/login/oauth2/code/custom
            provider:
                custom:
                    authorization-uri: http://localhost:8081/spring-security-oauth-server/oauth/authorize
                    token-uri: http://localhost:8081/spring-security-oauth-server/oauth/token
                    user-info-uri: http://localhost:8088/spring-security-oauth-resource/users/extra
                    user-name-attribute: user_name
```

在这种情况下，我们还需要为OAuth2客户端指定 范围、 授权类型和重定向URI。我们还将提供授权服务器的授权和令牌URI。

最后，我们还需要配置UserInfo端点，以便能够获取用户身份验证详细信息。

## 6. 安全配置

默认情况下，Spring Security保护所有路径。因此，如果我们只有一个OAuth客户端，我们将被重定向到授权该客户端并登录。

如果注册了多个OAuth客户端，则会自动创建一个登录页面以选择登录方式。

如果愿意，我们可以更改它并提供详细的安全配置：

```java
@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain configure(ServerHttpSecurity http) throws Exception {
		return http.authorizeExchange()
			.pathMatchers("/about").permitAll()
			.anyExchange().authenticated()
			.and().oauth2Login()
			.and().build();
	}
}
```

在此示例中，我们保护了除“/about”之外的所有路径。

## 7. WebClient

除了使用OAuth2对用户进行身份验证之外，我们还可以做更多的事情。我们可以使用WebClient使用OAuth2AuthorizedClient访问OAuth2安全资源。

现在，让我们配置我们的WebClient：

```java
@Bean
public WebClient webClient(ReactiveClientRegistrationRepository clientRegistrationRepo, ServerOAuth2AuthorizedClientRepository authorizedClientRepo) {
    ServerOAuth2AuthorizedClientExchangeFilterFunction filter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepo, authorizedClientRepo);
    
    return WebClient.builder().filter(filter).build();
}
```

然后，我们可以检索OAuth2安全资源：

```java
@Autowired
private WebClient webClient;

@GetMapping("/foos/{id}")
public Mono<Foo> getFooResource(@RegisteredOAuth2AuthorizedClient("custom") OAuth2AuthorizedClient client, @PathVariable final long id){
    return webClient
        .get()
        .uri("http://localhost:8088/spring-security-oauth-resource/foos/{id}", id)
        .attributes(oauth2AuthorizedClient(client))
        .retrieve()
        .bodyToMono(Foo.class); 
}
```

请注意，我们使用OAuth2AuthorizedClient中的AccessToken检索了远程资源Foo。

## 8. 总结

在这篇快速文章中，我们了解了如何配置WebFlux应用程序以使用OAuth2登录支持以及如何使用WebClient访问OAuth2安全资源。