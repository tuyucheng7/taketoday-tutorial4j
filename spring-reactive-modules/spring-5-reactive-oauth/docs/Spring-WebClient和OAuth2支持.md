## 1. 概述

Spring Security 5为Spring Webflux的非阻塞[WebClient](https://www.baeldung.com/spring-5-webclient)类提供了OAuth2支持。

在本教程中，我们将分析使用此类访问受保护资源的不同方法。我们还将深入了解Spring如何处理OAuth2授权过程。

## 2. 设置场景

为了符合[OAuth2规范](https://tools.ietf.org/html/rfc6749)，除了我们作为本教程重点的Client，自然还需要Authorization Server和Resource Server。 

我们可以使用知名的授权提供商，例如Google或Github。为了更好地理解OAuth2客户端的作用，我们还可以使用我们自己的服务器，并在[此处](https://github.com/Baeldung/spring-security-oauth)提供实现。我们不会讨论完整的配置，因为它不是本教程的主题，所以知道以下内容就足够了：

-   授权服务器将是：
    -   在端口8081上运行
    -   公开/oauth/authorize、/oauth/token和 oauth/check_token端点以执行所需的功能
    -   配置了示例用户(例如john/123)和单个OAuth客户端(fooClientIdPassword/secret)
-   资源服务器将与身份验证服务器分开，并且将是：
    -   在端口8082上运行
    -   提供一个简单的Foo对象安全资源，可使用/foos/{id}端点访问

注意：重要的是要了解几个Spring项目正在提供不同的OAuth相关功能和实现。[我们可以在这个Spring Projects矩阵](https://github.com/spring-projects/spring-security/wiki/OAuth-2.0-Features-Matrix)中看到每个库提供的内容。

WebClient和所有响应式Webflux相关功能都是Spring Security 5项目的一部分。因此，我们将在本教程中主要使用此框架。

## 3. Spring Security 5底层

为了完全理解我们将要讨论的示例，最好了解Spring Security如何在内部管理OAuth2功能。

该框架提供以下功能：

-   依靠OAuth2提供商帐户将[用户登录到应用程序](https://www.baeldung.com/spring-security-5-oauth2-login)
-   将我们的服务配置为OAuth2客户端
-   为我们管理授权程序
-   自动刷新令牌
-   如有必要，存储凭据

下图描述了Spring Security的OAuth2世界的一些基本概念：

<img src="../assets/img.png">

### 3.1 提供者

Spring定义了负责公开OAuth 2.0保护资源的OAuth2提供者角色。

在我们的示例中，我们的身份验证服务将是提供提供者功能的服务。

### 3.2 客户注册

ClientRegistration是一个实体，包含在OAuth2(或OpenID)提供程序中注册的特定客户端的所有相关信息。

在我们的场景中，它将是在身份验证服务器中注册的客户端，由bael-client-id标识。

### 3.3 授权客户

一旦最终用户(又名资源所有者)授予客户端访问其资源的权限，就会创建一个OAuth2AuthorizedClient实体。

它将负责将访问令牌关联到客户端注册和资源所有者(由Principal对象表示)。

### 3.4 Repository

此外，Spring Security还提供Repository类来访问上述实体。

特别是，ReactiveClientRegistrationRepository和ServerOAuth2AuthorizedClientRepository类在响应堆栈中使用，它们默认使用内存存储。

Spring Boot 2.x创建这些Repository类的beans并将它们自动添加到上下文中。

### 3.5 安全Web过滤器链

Spring Security 5中的关键概念之一是响应式SecurityWebFilterChain实体。

顾名思义，它表示[WebFilter](https://www.baeldung.com/spring-webflux-filters)对象的链式集合。

当我们在应用程序中启用OAuth2功能时，Spring Security会向链中添加两个过滤器：

1.  一个过滤器响应授权请求(/oauth2/authorization/{registrationId} URI)或抛出ClientAuthorizationRequiredException。它包含对ReactiveClientRegistrationRepository的引用，它负责创建授权请求以重定向用户代理。
2.  第二个过滤器根据我们添加的功能(OAuth2客户端功能或OAuth2登录功能)而有所不同。在这两种情况下，此过滤器的主要职责是创建OAuth2AuthorizedClient实例并使用ServerOAuth2AuthorizedClientRepository存储它。

### 3.6 Web客户端

Web客户端将配置一个包含对Repository的引用的ExchangeFilterFunction。

它将使用它们来获取访问令牌以将其自动添加到请求中。

## 4. Spring Security 5支持-客户端凭证流程

Spring Security允许我们将应用程序配置为OAuth2客户端。

在本文中，我们将使用WebClient实例通过“客户端凭据” 授权类型检索资源，然后使用“授权代码”流程。

我们要做的第一件事是配置客户端注册和我们将用来获取访问令牌的提供程序。

### 4.1 客户端和提供者配置

正如我们在[OAuth2登录文章](https://www.baeldung.com/spring-security-5-oauth2-login#setup)中看到的，我们可以通过编程方式配置它，或者通过使用属性来定义我们的注册来依赖Spring Boot自动配置：

```properties
spring.security.oauth2.client.registration.bael.authorization-grant-type=client_credentials
spring.security.oauth2.client.registration.bael.client-id=bael-client-id
spring.security.oauth2.client.registration.bael.client-secret=bael-secret

spring.security.oauth2.client.provider.bael.token-uri=http://localhost:8085/oauth/token
```

这些是我们使用client_credentials流检索资源所需的所有配置。

### 4.2 使用Web客户端

我们在没有最终用户与我们的应用程序交互的机器对机器通信中使用这种授权类型。

例如，假设我们有一个cron作业试图在我们的应用程序中使用WebClient获取安全资源：

```java
@Autowired
private WebClient webClient;

@Scheduled(fixedRate = 5000)
public void logResourceServiceResponse() {

    webClient.get()
        .uri("http://localhost:8084/retrieve-resource")
        .retrieve()
        .bodyToMono(String.class)
        .map(string -> "Retrieved using Client Credentials Grant Type: " + string)
        .subscribe(logger::info);
}
```

### 4.3 配置Web客户端

接下来，我们将设置我们在计划任务中自动装配的webClient实例：

```java
@Bean
WebClient webClient(ReactiveClientRegistrationRepository clientRegistrations) {
    ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
        	clientRegistrations,
        	new UnAuthenticatedServerOAuth2AuthorizedClientRepository());
    oauth.setDefaultClientRegistrationId("bael");
    return WebClient.builder()
      	.filter(oauth)
      	.build();
}
```

正如我们之前提到的，客户端注册Repository由Spring Boot自动创建并添加到上下文中。

接下来要注意的是我们正在使用UnAuthenticatedServerOAuth2AuthorizedClientRepository实例。这是因为没有最终用户会参与该过程，因为它是机器对机器的通信。最后，正如我们所说，我们将默认使用bael客户端注册。

否则，我们必须在cron作业中定义请求时指定它：

```java
webClient.get()
  	.uri("http://localhost:8084/retrieve-resource")
  	.attributes(
    	ServerOAuth2AuthorizedClientExchangeFilterFunction
      		.clientRegistrationId("bael"))
  	.retrieve()
  // ...
```

### 4.4 测试

如果我们在启用DEBUG日志记录级别的情况下运行我们的应用程序，我们将能够看到Spring Security正在为我们执行的调用：

```bash
o.s.w.r.f.client.ExchangeFunctions:
  HTTP POST http://localhost:8085/oauth/token
o.s.http.codec.json.Jackson2JsonDecoder:
  Decoded [{access_token=89cf72cd-183e-48a8-9d08-661584db4310,
    token_type=bearer,
    expires_in=41196,
    scope=read
    (truncated)...]
o.s.w.r.f.client.ExchangeFunctions:
  HTTP GET http://localhost:8084/retrieve-resource
o.s.core.codec.StringDecoder:
  Decoded "This is the resource!"
c.b.w.c.service.WebClientChonJob:
  We retrieved the following resource using Client Credentials Grant Type: This is the resource!
```

我们还会注意到，任务第二次运行时，应用程序请求资源时没有先请求令牌，因为最后一个令牌尚未过期。

## 5. Spring Security 5支持-使用授权代码流的实现

这种授权类型通常用于不太受信任的第三方应用程序需要访问资源的情况。

### 5.1 客户端和提供者配置

为了使用授权代码流程执行OAuth2流程，我们需要为我们的客户端注册和提供者定义更多属性：

```properties
spring.security.oauth2.client.registration.bael.client-name=bael
spring.security.oauth2.client.registration.bael.client-id=bael-client-id
spring.security.oauth2.client.registration.bael.client-secret=bael-secret
spring.security.oauth2.client.registration.bael.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.bael.redirect-uri=http://localhost:8080/login/oauth2/code/bael

spring.security.oauth2.client.provider.bael.token-uri=http://localhost:8085/oauth/token
spring.security.oauth2.client.provider.bael.authorization-uri=http://localhost:8085/oauth/authorize
spring.security.oauth2.client.provider.bael.user-info-uri=http://localhost:8084/user
spring.security.oauth2.client.provider.bael.user-name-attribute=name
```

除了我们在上一节中使用的属性外，这次我们还需要包括：

-   在身份验证服务器上进行身份验证的端点
-   包含用户信息的端点的URL
-   我们应用程序中端点的URL，用户代理将在身份验证后重定向到该端点

当然，对于知名的供应商，前两点不需要特别说明。

重定向端点由Spring Security自动创建。

默认情况下，为其配置的URL为/[action]/oauth2/code/[registrationId]，仅允许授权和登录操作(以避免无限循环)。

该端点负责：

-   接收验证码作为查询参数
-   用它来获取访问令牌
-   创建授权客户端实例
-   将用户代理重定向回原始端点

### 5.2 HTTP安全配置

接下来，我们需要配置SecurityWebFilterChain。

最常见的场景是使用Spring Security的OAuth2登录功能对用户进行身份验证并授予他们访问我们的端点和资源的权限。

如果这是我们的情况，那么只需在ServerHttpSecurity定义中包含oauth2Login指令就足以让我们的应用程序也作为OAuth2客户端工作：

```java
@Bean
public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http.authorizeExchange()
        .anyExchange()
        .authenticated()
        .and()
        .oauth2Login();
    return http.build();
}
```

### 5.3 配置Web客户端

现在是时候放置我们的WebClient实例了：

```java
@Bean
WebClient webClient(ReactiveClientRegistrationRepository clientRegistrations, ServerOAuth2AuthorizedClientRepository authorizedClients) {
    ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
        	clientRegistrations,
        	authorizedClients);
    oauth.setDefaultOAuth2AuthorizedClient(true);
    return WebClient.builder()
      	.filter(oauth)
      	.build();
}
```

这次我们从上下文中注入客户端注册Repository和授权客户端Repository。

我们还启用了setDefaultOAuth2AuthorizedClient选项。这样，框架将尝试从Spring Security管理的当前Authentication对象中获取客户端信息。

我们必须考虑到，所有HTTP请求都将包含访问令牌，这可能不是我们想要的行为。

稍后我们将分析指示特定WebClient事务将使用的客户端的替代方案。

### 5.4 使用Web客户端

授权代码需要一个可以计算重定向的用户代理(例如，浏览器)来执行该过程。

因此，当用户与我们的应用程序交互时，我们可以使用这种授权类型，通常调用HTTP端点：

```java
@RestController
public class ClientRestController {

	@Autowired
	WebClient webClient;

	@GetMapping("/auth-code")
	Mono<String> useOauthWithAuthCode() {
		Mono<String> retrievedResource = webClient.get()
			.uri("http://localhost:8084/retrieve-resource")
			.retrieve()
			.bodyToMono(String.class);
		return retrievedResource.map(string ->
			"We retrieved the following resource using Oauth: " + string);
	}
}
```

### 5.5 测试

最后，我们将调用端点并通过检查日志条目来分析发生了什么。

在我们调用端点后，应用程序会验证我们尚未在应用程序中进行身份验证：

```bash
o.s.w.s.adapter.HttpWebHandlerAdapter: HTTP GET "/auth-code"
...
HTTP/1.1 302 Found
Location: /oauth2/authorization/bael
```

应用程序重定向到授权服务的端点以使用提供者注册表中存在的凭据进行身份验证(在我们的例子中，我们将使用bael-user/bael-password)：

```bash
HTTP/1.1 302 Found
Location: http://localhost:8085/oauth/authorize
  ?response_type=code
  &client_id=bael-client-id
  &state=...
  &redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Flogin%2Foauth2%2Fcode%2Fbael
```

身份验证后，用户代理被发送回重定向URI，连同作为查询参数的代码以及首次发送的状态值(以避免[CSRF攻击](https://spring.io/blog/2011/11/30/cross-site-request-forgery-and-oauth2))：

```bash
o.s.w.s.adapter.HttpWebHandlerAdapter:HTTP GET "/login/oauth2/code/bael?code=...&state=...
```

然后应用程序使用代码获取访问令牌：

```bash
o.s.w.r.f.client.ExchangeFunctions:HTTP POST http://localhost:8085/oauth/token
```

它获取用户信息：

```bash
o.s.w.r.f.client.ExchangeFunctions:HTTP GET http://localhost:8084/user
```

并将用户代理重定向到原始端点：

```bash
HTTP/1.1 302 Found
Location: /auth-code
```

最后，我们的WebClient实例可以成功请求受保护的资源：

```bash
o.s.w.r.f.client.ExchangeFunctions:HTTP GET http://localhost:8084/retrieve-resource
o.s.w.r.f.client.ExchangeFunctions:Response 200 OK
o.s.core.codec.StringDecoder :Decoded "This is the resource!"
```

## 6. 另一种选择-调用中的客户端注册

早些时候，我们了解到使用setDefaultOAuth2AuthorizedClient意味着应用程序将在我们与客户端实现的任何调用中包含访问令牌。

如果我们从配置中删除此命令，我们将需要在定义请求时明确指定客户端注册。

当然，一种方法是使用clientRegistrationId，就像我们之前在处理客户端凭证流时所做的那样。

由于我们将Principal与授权客户端相关联，因此我们可以使用@RegisteredOAuth2AuthorizedClient注解获取OAuth2AuthorizedClient实例：

```java
@GetMapping("/auth-code-annotated")
Mono<String> useOauthWithAuthCodeAndAnnotation(@RegisteredOAuth2AuthorizedClient("bael") OAuth2AuthorizedClient authorizedClient) {
    Mono<String> retrievedResource = webClient.get()
      	.uri("http://localhost:8084/retrieve-resource")
      	.attributes(
        	ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient))
      	.retrieve()
      	.bodyToMono(String.class);
    return retrievedResource.map(string -> 
      	"Resource: " + string 
        	+ " - Principal associated: " + authorizedClient.getPrincipalName() 
        	+ " - Token will expire at: " + authorizedClient.getAccessToken()
          		.getExpiresAt());
}
```

## 7. 避免使用OAuth2登录功能

正如我们所指出的，最常见的情况是依靠OAuth2授权提供程序来登录我们应用程序中的用户。

但是，如果我们想避免这种情况，但仍然能够使用OAuth2协议访问受保护的资源怎么办？然后我们需要对配置进行一些更改。

对于初学者，为了一目了然，我们可以在定义重定向URI属性时使用授权操作而不是登录操作：

```properties
spring.security.oauth2.client.registration.bael.redirect-uri=http://localhost:8080/login/oauth2/code/bael
```

我们还可以删除与用户相关的属性，因为我们不会使用它们在我们的应用程序中创建委托人。

现在我们将在不包括oauth2Login命令的情况下配置SecurityWebFilterChain，而是包括oauth2Client命令。

即使我们不想依赖OAuth2登录，我们仍然希望在访问我们的端点之前对用户进行身份验证。出于这个原因，我们还将在此处包含formLogin指令：

```java
@Bean
public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http.authorizeExchange()
        .anyExchange()
        .authenticated()
        .and()
        .oauth2Client()
        .and()
        .formLogin();
    return http.build();
}
```

现在让我们运行应用程序，看看当我们使用/auth-code-annotated端点时会发生什么。

我们首先必须使用表单登录来登录我们的应用程序。

然后应用程序会将我们重定向到授权服务登录以授予对我们资源的访问权限。

注意：执行此操作后，我们应该被重定向回我们调用的原始端点。然而，Spring Security似乎正在重定向回根路径“/”，这似乎是一个错误。触发OAuth2舞蹈之后的以下请求将成功运行。

我们可以在端点响应中看到，这次授权客户端与名为bael-client-id而不是bael-user的主体相关联，该主体以Authentication Service中配置的用户命名。

## 8. Spring Framework支持-手动方式

开箱即用，Spring 5只提供了一种与OAuth2相关的服务方法，可以轻松地将Bearer令牌标头添加到请求中。这是HttpHeaders#setBearerAuth方法。

现在，我们将通过一个示例来演示如何通过手动执行OAuth2舞蹈来获取我们的安全资源。

简而言之，我们需要链接两个HTTP请求，一个从授权服务器获取身份验证令牌，另一个使用此令牌获取资源：

```java
@Autowired
WebClient client;

public Mono<String> obtainSecuredResource() {
    String encodedClientData = Base64Utils.encodeToString("bael-client-id:bael-secret".getBytes());
    Mono<String> resource = client.post()
        .uri("localhost:8085/oauth/token")
        .header("Authorization", "Basic " + encodedClientData)
        .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
        .retrieve()
        .bodyToMono(JsonNode.class)
        .flatMap(tokenResponse -> {
            String accessTokenValue = tokenResponse.get("access_token").textValue();
            return client.get()
                .uri("localhost:8084/retrieve-resource")
                .headers(h -> h.setBearerAuth(accessTokenValue))
                .retrieve()
                .bodyToMono(String.class);
          });
    return resource.map(res -> "Retrieved the resource using a manual approach: " + res);
}
```

这个例子应该可以帮助我们理解利用遵循OAuth2规范的请求是多么麻烦，并向我们展示了如何使用setBearerAuth方法。

在现实生活中，我们会让Spring Security以透明的方式为我们处理所有艰苦的工作，就像我们在前面的部分中所做的那样。

## 9. 总结

在本文中，我们学习了如何将我们的应用程序设置为OAuth2客户端，更具体地说，我们如何配置和使用WebClient在全响应堆栈中检索受保护的资源。

然后我们分析了Spring Security 5 OAuth2机制如何在底层运行以符合OAuth2规范。