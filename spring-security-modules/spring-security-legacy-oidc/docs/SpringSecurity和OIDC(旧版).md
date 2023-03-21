## 1. 概述

在这个快速教程中，我们将重点介绍使用Spring Security OAuth2实现设置OpenID Connect。

[OpenID Connect](https://openid.net/connect/)是建立在OAuth 2.0协议之上的简单身份层。

而且，更具体地说，我们将学习如何使用来自[Google的OpenID Connect实现](https://developers.google.com/identity/protocols/OpenIDConnect)对用户进行身份验证。

## 2. Maven配置

首先，我们需要将以下依赖项添加到我们的SpringBoot应用程序中：

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

## 3. Id Token

在深入了解实现细节之前，让我们快速了解一下OpenID的工作原理，以及我们将如何与之交互。

在这一点上，对OAuth2的理解当然很重要，因为OpenID是建立在OAuth之上的。

首先，为了使用身份功能，我们将使用一个名为openid的新OAuth2范围。**这将在我们的访问令牌中产生一个额外的字段-“id_token”**。

id_token是一个JWT(JSON Web Token)，其中包含有关用户的身份信息，由身份提供者(在我们的例子中为Google)签名。

最后，服务器(授权码)和隐式流都是最常用的获取id_token的方式，在我们的示例中，我们将使用**服务器流**。

## 3. OAuth2客户端配置

接下来，让我们配置OAuth2客户端–如下所示：

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

```properties
google.clientId=<your app clientId>
google.clientSecret=<your app clientSecret>
google.accessTokenUri=https://www.googleapis.com/oauth2/v3/token
google.userAuthorizationUri=https://accounts.google.com/o/oauth2/auth
google.redirectUri=http://localhost:8081/google-login
```

注意：

-   你首先需要从[Google开发控制台](https://console.developers.google.com/project/_/apiui/credential)为你的Google Web应用程序获取OAuth 2.0凭据。
-   我们使用范围openid来获取id_token。
-   我们还使用了一个额外范围的email将用户电子邮件包含在id_token身份信息中。
-   重定向URI [http://localhost:8081/google-login](http://localhost:8081/google-login)与我们的Google Web应用程序中使用的URI相同。

## 4. 自定义OpenID Connect过滤器

现在，我们需要创建自己的自定义OpenIdConnectFilter以从id_token中提取authentication-如下所示：

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
-   id_token始终包含“sub”字段，这是用户的唯一标识符。
-   id_token还将包含“email”字段，因为我们在请求中添加了email范围。

### 4.1 验证ID令牌

在上面的示例中，我们使用了JwtHelper的decodeAndVerify()方法从id_token中提取信息，同时也对其进行验证。

第一步是验证它是否使用[Google Discovery](https://developers.google.com/identity/protocols/OpenIDConnect#discovery)文档中指定的证书之一进行签名。

它们大约每天更改一次，因此我们将使用一个名为[jwks-rsa](https://central.sonatype.com/artifact/com.auth0/jwks-rsa/0.22.0)的实用程序库来读取它们：

```xml
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>jwks-rsa</artifactId>
    <version>0.3.0</version>
</dependency>
```

让我们将包含证书的URL添加到application.properties文件中：

```properties
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

最后，我们还将验证解码后的id令牌中的claims：

```java
public void verifyClaims(Map claims) {
    int exp = (int) claims.get("exp");
    Date expireDate = new Date(exp * 1000L);
    Date now = new Date();
    if (expireDate.before(now) || !claims.get("iss").equals(issuer) || !claims.get("aud").equals(clientId)) {
        throw new RuntimeException("Invalid claims");
    }
}
```

verifyClaims()方法检查id令牌是否由Google颁发并且未过期。

你可以在[Google文档](https://developers.google.com/identity/protocols/OpenIDConnect#validatinganidtoken)中找到更多相关信息。

## 5. 安全配置

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
        http.addFilterAfter(new OAuth2ClientContextFilter(),
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

-   我们在OAuth2ClientContextFilter之后添加了自定义OpenIdConnectFilter
-   我们使用简单的安全配置将用户重定向到“/google-login”以通过Google进行身份验证

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

响应示例(重定向到Google以批准应用权限后)：

```shell
Welcome, example@gmail.com
```

## 7. OpenID Connect流程示例

最后，让我们看一个示例OpenID Connect身份验证过程。

首先，我们将发送一个**身份验证请求**：

```shell
https://accounts.google.com/o/oauth2/auth?
    client_id=sampleClientID
    response_type=code&
    scope=openid%20email&
    redirect_uri=http://localhost:8081/google-login&
    state=abc
```

响应(**用户批准后**)是重定向到：

```shell
http://localhost:8081/google-login?state=abc&code=xyz
```

接下来，我们将交换访问令牌和id_token的code：

```shell
POST https://www.googleapis.com/oauth2/v3/token 
    code=xyz&
    client_id= sampleClientID&
    client_secret= sampleClientSecret&
    redirect_uri=http://localhost:8081/google-login&
    grant_type=authorization_code
```

这是一个示例响应：

```json
{
    "access_token": "SampleAccessToken",
    "id_token": "SampleIdToken",
    "token_type": "bearer",
    "expires_in": 3600,
    "refresh_token": "SampleRefreshToken"
}
```

最后，实际id_token的信息如下所示：

```json
{
    "iss":"accounts.google.com",
    "at_hash":"AccessTokenHash",
    "sub":"12345678",
    "email_verified":true,
    "email":"example@gmail.com",
    ...
}
```

因此，你可以立即看到令牌中的用户信息对于向我们自己的应用程序提供身份信息是多么有用。

## 8. 总结

在这个快速介绍教程中，我们学习了如何使用Google的OpenID Connect实现对用户进行身份验证。