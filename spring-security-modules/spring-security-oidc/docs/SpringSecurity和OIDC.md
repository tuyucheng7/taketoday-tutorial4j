## 1.概述

在本教程中，我们将重点介绍如何使用Spring Security设置OpenID Connect(OIDC)。

我们将介绍此规范的不同方面，然后我们将看到Spring Security提供的在OAuth 2.0客户端上实现它的支持。

## 2. OpenID Connect介绍

[OpenID Connect](https://openid.net/connect/)是建立在OAuth 2.0协议之上的身份层。

因此，在深入研究OIDC之前了解[OAuth 2.0](https://tools.ietf.org/html/rfc6749)非常重要，尤其是授权码流程。

OIDC规范套件非常广泛，它包括核心功能和其他几个可选功能，以不同的组呈现。以下是主要内容：

-   Core：身份验证和使用Claims来传达最终用户信息
-   Discovery：规定客户端如何动态确定有关OpenID提供者的信息
-   Dynamic Registration：规定客户端如何向提供者注册
-   Session Management：定义如何管理OIDC会话

最重要的是，文档还区分了为该规范提供支持的OAuth 2.0身份验证服务器，将它们称为OpenID提供者(OP)和使用OIDC作为依赖方(RP)的OAuth 2.0客户端，我们将在本文中使用该术语。

还值得注意的是，客户端可以通过在其授权请求中添加openid范围来请求使用此扩展。

最后，对于本教程，了解OP将最终用户信息作为称为ID令牌的JWT发出是很有用的。

## 3.项目设置

在专注于实际开发之前，我们必须向我们的OpenID Provider注册一个OAuth 2.0客户端。

在这种情况下，我们将使用Google作为OpenID Provider，我们可以按照[这些说明](https://developers.google.com/identity/protocols/OpenID Connect#appsetup)在他们的平台上注册我们的客户端应用程序。请注意，openid作用域在默认情况下是存在的。

我们在这个过程中设置的重定向URI是我们服务中的一个端点：http://localhost:8081/login/oauth2/code/google。

我们应该从这个过程中获得一个客户端ID和一个客户端密钥。

### 3.1Maven配置

我们首先将这些依赖项添加到项目pom文件中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
    <version>2.6.1</version>
</dependency>
```

starter工件聚合了所有Spring SecurityClient相关的依赖，包括

-OAuth 2.0登录和客户端功能的spring-security-oauth2-client依赖项
-用于JWT支持的JOSE库

## 4.使用SpringBoot的基本配置

首先，我们将首先配置我们的应用程序以使用我们刚刚通过Google创建的客户端注册。

使用SpringBoot非常容易，因为我们所要做的就是定义两个应用程序属性：

```yaml
spring:
    security:
        oauth2:
            client:
                registration:
                    google:
                        client-id: <client-id>
                        client-secret: <secret>
```

现在让我们启动应用程序并尝试访问一个端点，我们将看到我们被重定向到OAuth 2.0客户端的Google登录页面。

它看起来很简单，但这里有很多事情要做。接下来，我们将探讨Spring Security如何实现这一点。

以前，在我们的WebClient和OAuth2支持文章中，我们分析了Spring Security如何处理OAuth 2.0授权服务器和客户端的内部结构。

在这里我们看到，除了客户端ID和客户端密钥之外，我们还必须提供其他数据才能成功配置ClientRegistration实例。

那么，这是如何工作的呢？

Google是一家知名的提供商，因此该框架提供了一些预定义的属性，使事情变得更容易。

我们可以在CommonOAuth2Provider枚举中查看这些配置。

对于Google，枚举类型定义了如下属性：

-将使用的默认作用域
-授权端点
-令牌端点
-UserInfo端点，它也是OIDC核心规范的一部分

### 4.1访问用户信息

Spring Security提供了向OIDCProvider注册的用户主体(Principal)的有用表示，即OidcUser实体。

除了基本的OAuth2AuthenticatedPrincipal方法外，该实体还提供了一些有用的功能：

-检索IDToken值及其包含的Claims
-获取UserInfo端点提供的Claims
-生成两个集合的聚合

我们可以在控制器中轻松访问该实体：

```java
@GetMapping("/oidc-principal")
public OidcUser getOidcUserPrincipal(@AuthenticationPrincipal OidcUser principal) {
	return principal;
}
```

或者我们可以在bean中使用SecurityContextHolder：

```java
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
if (authentication.getPrincipal() instanceof OidcUser) {
    OidcUser principal = ((OidcUser) authentication.getPrincipal());
    
    // ...
}
```

如果我们检查主体，我们会在这里看到很多有用的信息，例如用户名、电子邮件、个人资料图片和地区。

此外，需要注意的是，Spring根据从提供者接收到的作用域为主体添加权限，前缀为“SCOPE_”。例如，openid作用域变为SCOPE_openid授予的权限。

这些权限可用于限制对某些资源的访问：

```java
@EnableWebSecurity
public class MappedAuthorities {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(authorizeRequests -> authorizeRequests
                               .mvcMatchers("/my-endpoint")
                               .hasAuthority("SCOPE_openid")
                               .anyRequest()
                               .authenticated()
          );
        return http.build();
    }
}
```

## 5.OIDC在实践

到目前为止，我们已经了解了如何使用Spring Security轻松实现OIDC登录解决方案。

我们已经看到了通过将用户识别过程委托给OpenID提供者所带来的好处，OpenID提供者反过来提供了详细的有用信息，甚至以可扩展的方式。

但事实是，到目前为止，我们不需要处理任何特定于OIDC的方面。这意味着Spring为我们做了大部分工作。

因此，让我们看看幕后发生的事情，以更好地了解该规范是如何付诸实现的，并能够最大限度地利用它。

### 5.1登录过程

为了清楚地看到这一点，让我们启用RestTemplate日志来查看服务正在执行的请求：

```yaml
logging:
    level:
        org.springframework.web.client.RestTemplate: DEBUG
```

如果我们现在调用一个安全端点，我们将看到该服务正在执行常规的OAuth 2.0授权代码流。这是因为，正如我们所说，该规范是建立在OAuth 2.0之上的。

有一些差异。

首先，根据我们使用的提供者和我们配置的作用域，我们可能会看到服务正在调用我们在开头提到的UserInfo端点。

也就是说，如果授权响应检索到profile、email、address或phone作用域中的至少一个，则框架将调用UserInfo端点以获取其他信息。

尽管一切都表明谷歌应该检索profile和email作用域(因为我们在授权请求中使用它们)，但OP会检索他们的自定义对应项，https://www.googleapis.com/auth/userinfo.email和https://www.googleapis.com/auth/userinfo.profile，所以Spring不会调用端点。

这意味着我们获取的所有信息都是ID令牌的一部分。

我们可以通过创建和提供我们自己的OidcUserService实例来适应这种行为：

```java
@Configuration
public class OAuth2LoginSecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		Set<String> googleScopes = new HashSet<>();
		googleScopes.add("https://www.googleapis.com/auth/userinfo.email");
		googleScopes.add("https://www.googleapis.com/auth/userinfo.profile");

		OidcUserService googleUserService = new OidcUserService();
		googleUserService.setAccessibleScopes(googleScopes);

		http.authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest()
						.authenticated())
				.oauth2Login(oauthLogin -> oauthLogin.userInfoEndpoint()
						.oidcUserService(googleUserService));
		return http.build();
	}
}
```

我们将观察到的第二个区别是对JWKSetURI的调用。正如我们在[JWS和JWK帖子](https://www.baeldung.com/spring-security-oauth2-jws-jwk)中所解释的，这用于验证JWT格式的ID令牌签名。

接下来，我们将详细分析IDToken。

### 5.2IDToken

自然地，OIDC规范涵盖并适应了许多不同的场景。在这种情况下，我们使用的是授权代码流，并且协议指示访问令牌和ID令牌都将作为令牌端点响应的一部分进行检索。

正如我们之前所说，OidcUser实体包含IDToken中包含的Claims，以及可以使用[jwt.io](https://jwt.io/)检查的实际JWT格式的令牌。

最重要的是，Spring提供了许多方便的getter来以干净的方式获取规范定义的标准Claims。

我们可以看到IDToken包含一些强制声明：

-格式为URL的颁发者标识符(例如，“https://accounts.google.com”)
-主题id，它是发行者包含的最终用户的引用
-token的过期时间
-token发行时间
-audience，将包含我们配置的OAuth 2.0客户端ID

它还包含许多[OIDC标准声明](https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims)，例如我们之前提到的那些(名称、语言环境、图片、电子邮件)。

由于这些是标准的，我们可以期望许多提供商至少检索其中一些字段，从而促进更简单的解决方案的开发。

### 5.3Claims和Scopes

正如我们可以想象的那样，OP检索到的声明与我们(或Spring Security)配置的范围相对应。

OIDC定义了一些可用于请求OIDC定义的声明的作用域：

-profile，可用于请求默认配置文件声明(例如name、preferred_username、picture等
-email，以访问email和email_verified声明
-地址
-phone，请求phone_number和phone_number_verified声明

尽管Spring还不支持它，但该规范允许通过在授权请求中指定它们来请求单个声明。

## 6.Spring对OIDCDiscovery的支持

正如我们在介绍中所解释的，OIDC除了其核心用途外，还包括许多不同的功能。

我们将在本节中分析的功能以及以下功能在OIDC中是可选的。因此，重要的是要了解可能存在不支持它们的OP。

该规范为RP定义了一种发现机制，以发现OP并获取与其交互所需的信息。

简而言之，OP提供标准元数据的JSON文档。该信息必须由发行者位置的知名端点/.well-known/openid-configuration提供。

Spring从中受益，它允许我们使用一个简单的属性配置ClientRegistration，即颁发者位置。

但是让我们直接进入一个例子来清楚地看到这一点。

我们将定义一个自定义ClientRegistration实例：

```yaml
spring:
    security:
        oauth2:
            client:
                registration:
                    custom-google:
                        client-id: <client-id>
                        client-secret: <secret>
                provider:
                    custom-google:
                        issuer-uri: https://accounts.google.com
```

现在我们可以重新启动我们的应用程序并检查日志以确认应用程序在启动过程中调用了openid-configuration端点。

我们甚至可以浏览这个端点来查看Google提供的信息：

https://accounts.google.com/.well-known/openid-configuration

例如，我们可以看到服务必须使用的授权、令牌和UserInfo端点，以及支持的范围。

这里特别需要注意的是，如果在服务启动时发现端点不可用，我们的应用程序将无法成功完成启动过程。

## 7.OpenID Connect会话管理

该规范通过定义以下内容来补充核心功能：

-持续监控最终用户在OP的登录状态的不同方法，以便RP可以注销已注销OpenID提供程序的最终用户
-作为客户端注册的一部分，向OP注册RP注销URI的可能性，以便在最终用户注销OP时得到通知
-一种通知OP最终用户已退出站点并且可能也希望退出OP的机制

当然，并非所有OP都支持所有这些项目，其中一些解决方案只能通过User-Agent在前端实现中实现。

在本教程中，我们将重点关注Spring为列表的最后一项提供的功能，即RP发起的注销。

此时，如果我们登录到我们的应用程序，我们可以正常访问每个端点。

如果我们注销(调用/logout端点)并随后向安全资源发出请求，我们将看到无需再次登录即可获得响应。

然而，事实并非如此。如果我们检查浏览器调试控制台中的网络选项卡，我们会看到当我们第二次点击安全端点时，我们会被重定向到OP授权端点。而且由于我们仍然在那里登录，因此流程是透明地完成的，几乎立即在安全端点中结束。

当然，在某些情况下，这可能不是所需的行为。让我们看看我们如何实现这个OIDC机制来处理这个问题。

### 7.1。OpenID提供者配置

在这种情况下，我们将配置和使用Okta实例作为我们的OpenID提供程序。我们不会详细介绍如何创建实例，但我们可以按照[本指南](https://help.okta.com/en/prod/Content/Topics/Apps/apps-about-oidc.htm)的步骤进行操作，请记住Spring Security的默认回调端点将是/login/oauth2/code/okta。

在我们的应用程序中，我们可以使用属性定义客户端注册数据：

```yaml
spring:
    security:
        oauth2:
            client:
                registration:
                    okta:
                        client-id: <client-id>
                        client-secret: <secret>
                provider:
                    okta:
                        issuer-uri: https://dev-123.okta.com
```

OIDC表示可以在Discovery文档中指定OP注销端点，作为end_session_endpoint元素。

### 7.2.LogoutSuccessHandler配置_

接下来，我们必须通过提供自定义的LogoutSuccessHandler实例来配置HttpSecurity注销逻辑：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .authorizeRequests(authorizeRequests -> authorizeRequests
        .mvcMatchers("/home").permitAll()
        .anyRequest().authenticated())
      .oauth2Login(oauthLogin -> oauthLogin.permitAll())
      .logout(logout -> logout
        .logoutSuccessHandler(oidcLogoutSuccessHandler()));
    return http.build();
}
```

现在让我们看看如何使用Spring Security提供的特殊类OidcClientInitiatedLogoutSuccessHandler来为此目的创建LogoutSuccessHandler：

```java
@Autowired
private ClientRegistrationRepository clientRegistrationRepository;

private LogoutSuccessHandler oidcLogoutSuccessHandler() {
    OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
      new OidcClientInitiatedLogoutSuccessHandler(
        this.clientRegistrationRepository);

    oidcLogoutSuccessHandler.setPostLogoutRedirectUri(
      URI.create("http://localhost:8081/home"));

    return oidcLogoutSuccessHandler;
}
```

因此，我们需要在OP客户端配置面板中将此URI设置为有效的注销重定向URI。

显然，OP注销配置包含在客户端注册设置中，因为我们用于配置处理程序的只是上下文中存在的ClientRegistrationRepositorybean。

那么，现在会发生什么？

在我们登录到我们的应用程序后，我们可以向Spring Security提供的/logout端点发送请求。

如果我们在浏览器调试控制台中检查网络日志，我们将看到我们在最终访问我们配置的重定向URI之前被重定向到OP注销端点。

下次我们访问应用程序中需要身份验证的端点时，我们将强制需要再次登录我们的OP平台以获取权限。

## 8.总结

总而言之，在本文中，我们了解了很多关于OpenID Connect提供的解决方案以及我们如何使用Spring Security实现其中一些解决方案。