## 1. 简介

在本教程中，我们将展示如何在Spring Boot机密客户端应用程序中使用 PKCE。

## 2. 背景

代码交换证明密钥 (PKCE) 是 OAuth 协议的扩展，最初针对公共客户端，通常是 SPA Web 应用程序或移动应用程序。它用作授权代码授予流程的一部分，有助于缓解恶意第三方的某些攻击。

这些攻击的主要载体是提供商已经建立用户身份并使用 HTTP 重定向发送授权代码时发生的步骤。根据具体情况，此授权代码可能会泄漏和/或被拦截，从而允许攻击者使用它来获取有效的访问令牌。

一旦拥有此访问令牌，攻击者就可以使用它来访问受保护的资源并像合法所有者一样使用它。例如，如果此访问令牌与银行账户相关联，他们就可以访问报表、投资组合价值或其他敏感信息。

## 3. PKCE 对 OAuth 的修改

PKCE 机制为标准授权代码流添加了一些调整：

-   客户端在初始授权请求中发送两个附加参数：code_challenge和code_challenge_method
-   最后一步，当客户端用授权码交换访问令牌时，还有一个新参数：code_verifier

启用 PKCE 的客户端采取以下步骤来实现此机制：

首先，它生成一个随机字符串用作code_verifier参数。根据 [RFC 7636](https://www.rfc-editor.org/rfc/rfc7636.html#section-4.1)，此字符串的长度必须至少为 43 个八位字节但小于 128 个八位字节。关键点是使用安全随机生成器，例如 JVM 的SecureRandom或等效的。

除了长度外，允许的字符范围也有限制：仅支持字母数字 ASCII 字符以及一些符号。

接下来，客户端获取生成的值并使用支持的方法将其转换为code_challenge参数。目前，规范只[提到](https://www.rfc-editor.org/rfc/rfc7636.html#section-4.2)了两种转换方法：plain和S256。

-   plain 只是一个无操作转换，因此转换后的值与code_verifier相同
-   S256对应SHA-256哈希算法，其结果以BASE64编码

然后，客户端使用常规参数(client_id、scope、state等)构建 OAuth 授权 URL，并添加生成的code_challenge和code_challenge_method。

### 3.1。代码挑战验证

在 OAuth 授权代码流的最后一步，客户端发送原始code_verifier值以及此流定义的常规值。然后服务器根据挑战的方法验证code_verifier ：

-   对于plain方法，code_verifier和challenge必须相同
-   对于S256方法，服务器计算所提供值的 SHA-256 并在 BASE64 中对其进行编码，然后再将其与原始质询进行比较。

那么，为什么 PKCE 对授权码攻击有效呢？正如我们之前提到的，它们通常针对从授权服务器发送的包含授权代码的重定向来工作。但是，对于 PKCE，此信息不再足以完成流程，至少对于S256 方法而言。仅当客户端同时提供授权代码和验证程序时，才会发生代码换令牌交换，而这在重定向中永远不会出现。

当然，当使用 plain方法时，验证者和挑战者是相同的，所以在实际应用中使用这种方法是没有意义的。

### 3.2. 秘密客户的 PKCE

在 OAuth 2.0 中，PKCE 是可选的，主要用于移动和 Web 应用程序。然而，即将到来的 OAuth 2.1 版本不仅对公共客户端而且对秘密客户端都强制要求 PKCE。

请记住，秘密客户端通常是在云或本地服务器中运行的托管应用程序。此类客户端也使用授权代码流，但由于最终代码交换步骤发生在后端和授权服务器之间，因此用户代理(Web 或移动)永远不会“看到”访问令牌。

除此之外，这些步骤与公共客户案例中的步骤完全相同。

## 4. Spring Security 对 PKCE 的支持

从 Spring Security 5.7 开始，PKCE 完全支持 servlet 和响应式 Web 应用程序。但是，默认情况下未启用此功能，因为并非所有身份提供者都支持此扩展。Spring Boot 应用程序必须使用 2.7 或更高版本的框架，并依赖标准的依赖管理。这可确保项目选择正确的 Spring Security 版本及其传递依赖项。

PKCE 支持存在于spring-security-oauth2-client模块中。对于Spring Boot应用程序，引入此依赖项的最简单方法是使用相应的 starter 模块：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
    <version>2.7.2</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
    <version>2.7.2</version>
</dependency>

```

[这些依赖](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-webflux)项的最新版本可以从 Maven Central 下载。

依赖关系到位后，我们现在需要自定义 OAuth 2.0 登录过程以支持 PKCE。对于反应式应用程序，这意味着添加一个应用此设置的 SecurityWebFilterChain bean：

```java
@Bean
public SecurityWebFilterChain pkceFilterChain(ServerHttpSecurity http,
  ServerOAuth2AuthorizationRequestResolver resolver) {
    http.authorizeExchange(r -> r.anyExchange().authenticated());
    http.oauth2Login(auth -> auth.authorizationRequestResolver(resolver));
    return http.build();
}

```

关键步骤是在登录规范中设置自定义ServerOAuth2AuthorizationRequestResolver 。Spring Security 使用此接口的实现来为给定的客户端注册构建 OAuth 授权请求。

幸运的是，我们不必实现这个接口。相反，我们可以使用现成的DefaultServerOAuth2AuthorizationRequestResolver类，它允许我们应用进一步的自定义：

```java
@Bean
public ServerOAuth2AuthorizationRequestResolver pkceResolver(ReactiveClientRegistrationRepository repo) {
    var resolver = new DefaultServerOAuth2AuthorizationRequestResolver(repo);
    resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
    return resolver;
}

```

在这里，我们实例化请求解析器，传递一个ReactiveClientRegistrationRepository实例。然后，我们使用OAuth2AuthorizationRequestCustomizers.withPkce()，它提供了将额外的 PKCE 参数添加到授权请求 URL 所需的逻辑。

## 5. 测试

为了测试我们启用 PKCE 的应用程序，我们需要一个支持此扩展的授权服务器。在本教程中，我们将[为此目的使用 Spring Authorization Server](https://www.baeldung.com/spring-security-oauth-auth-server)。这个项目是 Spring 家族的最新成员，它允许我们快速构建一个符合 OAuth 2.1/OIDC 的授权服务器。

### 5.1。授权服务器设置

在我们的实时测试环境中，授权服务器作为独立于客户端的进程运行。该项目是一个标准的Spring BootWeb 应用程序，我们向其中添加了相关的 Maven 依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.7.2</version>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-oauth2-authorization-server</artifactId>
    <version>0.3.1</version>
</dependency>

```

 可以从 Maven Central 下载最新版本的[starter](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-web)和 [Spring Authorization Server 。](https://search.maven.org/search?q=g:org.springframework.security AND a:spring-security-oauth2-authorization-server)

为了正常工作，授权服务器需要我们提供一些配置 bean，包括RegisteredClientRepository和UserDetailsService。为了我们的测试目的，我们可以使用包含一组固定测试值的内存实现。对于本教程，前者更相关：

```java
@Bean 
public RegisteredClientRepository registeredClientRepository() {      
    var pkceClient = RegisteredClient
      .withId(UUID.randomUUID().toString())
      .clientId("pkce-client")
      .clientSecret("{noop}obscura")
      .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
      .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
      .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
      .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
      .scope(OidcScopes.OPENID)          
      .scope(OidcScopes.EMAIL)          
      .scope(OidcScopes.PROFILE)
      .clientSettings(ClientSettings.builder()
        .requireAuthorizationConsent(false)
        .requireProofKey(true)
        .build())
      .redirectUri("http://127.0.0.1:8080/login/oauth2/code/pkce")
      .build();
    
    return new InMemoryRegisteredClientRepository(pkceClient);
}

```

关键点是使用clientSettings()方法来强制对特定客户端使用 PKCE。我们通过传递一个将requireProofKey()设置为 true的ClientSettings对象来做到这一点。

在我们的测试设置中，客户端将与授权服务器在同一主机上运行，因此我们使用 127.0.0.1 作为重定向 URL 的主机名部分。值得注意的是，这里不允许使用“localhost”，因此使用等效的 IP 地址。

要完成设置，我们还需要修改应用程序属性文件中的默认端口设置：

```bash
server.port=8085
```

### 5.2. 运行实时测试

现在，让我们运行一个实时测试来验证一切是否按预期工作。我们可以直接从 IDE 运行这两个项目，或者打开两个 shell 窗口并为每个模块发出命令mvn spring-boot:run。无论采用哪种方法，一旦两个应用程序都启动，我们可以打开浏览器并将其指向http://127.0.0.1:8080。

我们应该看到 Spring Security 的默认登录页面：

[![pkce登录](https://www.baeldung.com/wp-content/uploads/2022/08/pkce-sign-in.png)](https://www.baeldung.com/wp-content/uploads/2022/08/pkce-sign-in.png)

注意地址栏中的 URL：http://localhost:8085。这意味着登录表单通过重定向来自授权服务器。为了验证此声明，我们可以在登录表单上打开 Chrome 的 DevTools(或选择的浏览器中的等效工具)并在地址栏中重新输入初始 URL：

[![PKCE 挑战](https://www.baeldung.com/wp-content/uploads/2022/08/pkce-challenge.png)](https://www.baeldung.com/wp-content/uploads/2022/08/pkce-challenge.png)

我们可以在我们的客户端应用程序对向http://127.0.0.1:8080/oauth2/authorization/pkce发出的请求生成的响应中看到 Location 标头中的 PKCE 参数：

```bash
Location: http://localhost:8085/oauth2/authorize?
  response_type=code&
  client_id=pkce-client&
  scope=openid email&
  state=sUmww5GH14yatTwnv2V5Xs0rCCJ0vz0Sjyp4tK1tsdI=&
  redirect_uri=http://127.0.0.1:8080/login/oauth2/code/pkce&
  nonce=FVO5cA3_UNVVIjYnZ9ZrNq5xCTfDnlPERAvPCm0w0ek&
  code_challenge=g0bA5_PNDxy-bdf2t9H0ximVovLqMdbuTVxmGnXjdnQ&
  code_challenge_method=S256
```

为了完成登录序列，我们将使用“用户”和“密码”作为凭据。如果我们继续跟踪请求，我们将看到代码验证器和访问令牌都不存在，这是我们的目标。

## 六. 总结

在本教程中，我们展示了如何通过几行代码在 Spring Security 应用程序中启用 OAuth 的 PKCE 扩展。此外，我们还展示了如何使用 Spring Authorization Server 库为测试目的创建定制的服务器。