## 1. 概述

在本快速教程中，我们将重点介绍使用 Spring Security OAuth2 实现设置 OpenID Connect。

[OpenID Connect](https://openid.net/connect/)是建立在 OAuth 2.0 协议之上的简单身份层。

而且，更具体地说，我们将学习如何使用来自[Google的](https://developers.google.com/identity/protocols/OpenIDConnect)[OpenID Connect 实现](https://developers.google.com/identity/protocols/OpenIDConnect)对用户进行身份验证。

## 2.Maven配置

首先，我们需要将以下依赖项添加到我们的Spring Boot应用程序中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.security.oauth</groupId>
    <artifactId>spring-security-oauth2</artifactId>
</dependency>
```

## 3. Id 令牌

在深入了解实现细节之前，让我们快速了解一下 OpenID 的工作原理，以及我们将如何与之交互。

在这一点上，对 OAuth2 的理解当然很重要，因为 OpenID 是建立在 OAuth 之上的。

首先，为了使用身份功能，我们将使用一个名为openid的新 OAuth2 范围。这将在我们的访问令牌中产生一个额外的字段——“ id_token ”。

id_token是一个 JWT(JSON Web 令牌)，其中包含有关用户的身份信息，由身份提供者(在我们的例子中为 Google)签名。

最后，服务器(授权码)和隐式流都是最常用的获取id_token的方式，在我们的示例中，我们将使用服务器流。

## 3. OAuth2客户端配置

接下来，让我们配置我们的 OAuth2 客户端 - 如下：

```java
@Configuration
@EnableOAuth2Client
public class GoogleOpenIdConnectConfig {
    @Value("${google.clientId}")
    private String clientId;

    @Value("${google.clientSecret}")
    private String clientSecret;

    @Value("${google.accessTokenUri}")
    private String accessTokenUri;

    @Value("${google.userAuthorizationUri}")
    private String userAuthorizationUri;

    @Value("${google.redirectUri}")
    private String redirectUri;

    @Bean
    public OAuth2ProtectedResourceDetails googleOpenId() {
        AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
        details.setClientId(clientId);
        details.setClientSecret(clientSecret);
        details.setAccessTokenUri(accessTokenUri);
        details.setUserAuthorizationUri(userAuthorizationUri);
        details.setScope(Arrays.asList("openid", "email"));
        details.setPreEstablishedRedirectUri(redirectUri);
        details.setUseCurrentUri(false);
        return details;
    }

    @Bean
    public OAuth2RestTemplate googleOpenIdTemplate(OAuth2ClientContext clientContext) {
        return new OAuth2RestTemplate(googleOpenId(), clientContext);
    }
}
```

这是application.properties：

```bash
google.clientId=<your app clientId>
google.clientSecret=<your app clientSecret>
google.accessTokenUri=https://www.googleapis.com/oauth2/v3/token
google.userAuthorizationUri=https://accounts.google.com/o/oauth2/auth
google.redirectUri=http://localhost:8081/google-login
```

注意：

-   首先需要从[Google Developers Console](https://console.developers.google.com/project/_/apiui/credential)为的 Google Web 应用程序获取 OAuth 2.0 凭据。
-   我们使用范围openid来获取id_token。
-   我们还使用额外范围的电子邮件将用户电子邮件包含在id_token身份信息中。
-   重定向 URI http://localhost:8081/google-login与我们的 Google Web 应用程序中使用的相同。

## 4. 自定义 OpenID 连接过滤器

现在，我们需要创建自己的自定义OpenIdConnectFilter以从id_token中提取身份验证- 如下：

```java
public class OpenIdConnectFilter extends AbstractAuthenticationProcessingFilter {

    public OpenIdConnectFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
        setAuthenticationManager(new NoopAuthenticationManager());
    }
    @Override
    public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) 
      throws AuthenticationException, IOException, ServletException {
        OAuth2AccessToken accessToken;
        try {
            accessToken = restTemplate.getAccessToken();
        } catch (OAuth2Exception e) {
            throw new BadCredentialsException("Could not obtain access token", e);
        }
        try {
            String idToken = accessToken.getAdditionalInformation().get("id_token").toString();
            String kid = JwtHelper.headers(idToken).get("kid");
            Jwt tokenDecoded = JwtHelper.decodeAndVerify(idToken, verifier(kid));
            Map<String, String> authInfo = new ObjectMapper()
              .readValue(tokenDecoded.getClaims(), Map.class);
            verifyClaims(authInfo);
            OpenIdConnectUserDetails user = new OpenIdConnectUserDetails(authInfo, accessToken);
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        } catch (InvalidTokenException e) {
            throw new BadCredentialsException("Could not obtain user details from token", e);
        }
    }
}
```

这是我们简单的OpenIdConnectUserDetails：

```java
public class OpenIdConnectUserDetails implements UserDetails {
    private String userId;
    private String username;
    private OAuth2AccessToken token;

    public OpenIdConnectUserDetails(Map<String, String> userInfo, OAuth2AccessToken token) {
        this.userId = userInfo.get("sub");
        this.username = userInfo.get("email");
        this.token = token;
    }
}
```

注意：

-   Spring Security JwtHelper解码id_token。
-   id_token始终包含“子”字段，这是用户的唯一标识符。
-   id_token还将包含“电子邮件”字段，因为我们将电子邮件范围添加到我们的请求中。

### 4.1。验证 ID 令牌

在上面的示例中，我们使用了JwtHelper 的decodeAndVerify()方法从id_token中提取信息，同时也对其进行验证。

第一步是验证它是否使用[Google Discovery](https://developers.google.com/identity/protocols/OpenIDConnect#discovery)文档中指定的证书之一进行签名。

这些变化大约每天一次，因此我们将使用一个名为[jwks-rsa](https://search.maven.org/classic/#search|ga|1|jwks)的实用程序库来读取它们：

```xml
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>jwks-rsa</artifactId>
    <version>0.3.0</version>
</dependency>
```

让我们将包含证书的 URL 添加到application.properties文件中：

```java
google.jwkUrl=https://www.googleapis.com/oauth2/v2/certs
```

现在我们可以读取这个属性并构建RSAVerifier对象：

```java
@Value("${google.jwkUrl}")
private String jwkUrl;    

private RsaVerifier verifier(String kid) throws Exception {
    JwkProvider provider = new UrlJwkProvider(new URL(jwkUrl));
    Jwk jwk = provider.get(kid);
    return new RsaVerifier((RSAPublicKey) jwk.getPublicKey());
}
```

最后，我们还将验证解码后的 id 令牌中的声明：

```java
public void verifyClaims(Map claims) {
    int exp = (int) claims.get("exp");
    Date expireDate = new Date(exp  1000L);
    Date now = new Date();
    if (expireDate.before(now) || !claims.get("iss").equals(issuer) || 
      !claims.get("aud").equals(clientId)) {
        throw new RuntimeException("Invalid claims");
    }
}
```

verifyClaims ()方法正在检查 id 令牌是否由 Google 颁发并且未过期。

可以在[Google 文档](https://developers.google.com/identity/protocols/OpenIDConnect#validatinganidtoken)中找到更多信息。

## 5.安全配置

接下来，让我们讨论一下我们的安全配置：

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private OAuth2RestTemplate restTemplate;

    @Bean
    public OpenIdConnectFilter openIdConnectFilter() {
        OpenIdConnectFilter filter = new OpenIdConnectFilter("/google-login");
        filter.setRestTemplate(restTemplate);
        return filter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .addFilterAfter(new OAuth2ClientContextFilter(), 
          AbstractPreAuthenticatedProcessingFilter.class)
        .addFilterAfter(OpenIdConnectFilter(), 
          OAuth2ClientContextFilter.class)
        .httpBasic()
        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/google-login"))
        .and()
        .authorizeRequests()
        .anyRequest().authenticated();
        return http.build();
    }
}
```

注意：

-   我们在 OAuth2ClientContextFilter 之后添加了我们的自定义OpenIdConnectFilter
-   我们使用简单的安全配置将用户重定向到“ /google-login ”以通过 Google 进行身份验证

## 6. 用户控制器

接下来，这是一个简单的控制器来测试我们的应用程序：

```java
@Controller
public class HomeController {
    @RequestMapping("/")
    @ResponseBody
    public String home() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return "Welcome, " + username;
    }
}
```

响应示例(重定向到 Google 以批准应用权限后)：

```bash
Welcome, example@gmail.com
```

## 7. OpenID Connect 流程示例

最后，让我们看一个示例 OpenID Connect 身份验证过程。

首先，我们将发送一个身份验证请求：

```bash
https://accounts.google.com/o/oauth2/auth?
    client_id=sampleClientID
    response_type=code&
    scope=openid%20email&
    redirect_uri=http://localhost:8081/google-login&
    state=abc
```

响应(在用户批准后)是重定向到：

```bash
http://localhost:8081/google-login?state=abc&code=xyz
```

接下来，我们将交换访问令牌和id_token的代码：

```bash
POST https://www.googleapis.com/oauth2/v3/token 
    code=xyz&
    client_id= sampleClientID&
    client_secret= sampleClientSecret&
    redirect_uri=http://localhost:8081/google-login&
    grant_type=authorization_code
```

这是一个示例响应：

```java
{
    "access_token": "SampleAccessToken",
    "id_token": "SampleIdToken",
    "token_type": "bearer",
    "expires_in": 3600,
    "refresh_token": "SampleRefreshToken"
}
```

最后，实际id_token的信息如下所示：

```bash
{
    "iss":"accounts.google.com",
    "at_hash":"AccessTokenHash",
    "sub":"12345678",
    "email_verified":true,
    "email":"example@gmail.com",
     ...
}
```

因此，可以立即看到令牌内的用户信息对于向我们自己的应用程序提供身份信息有多么有用。

## 8. 总结

在这个快速介绍教程中，我们学习了如何使用 Google 的 OpenID Connect 实现对用户进行身份验证。