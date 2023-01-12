## 1. 概述

在本教程中，我们将使用 Jakarta EE 和 MicroProfile 提供[OAuth 2.0 授权框架](https://tools.ietf.org/html/rfc6749)的实现。最重要的是，我们将通过[Authorization Code grant type实现](https://tools.ietf.org/html/rfc6749#page-24)[OAuth 2.0 角色](https://tools.ietf.org/html/rfc6749#page-6)的交互。撰写本文的动机是为使用 Jakarta EE 实现的项目提供支持，因为它尚未提供对 OAuth 的支持。

对于最重要的角色授权服务器，我们将实现授权端点、令牌端点以及 JWK 密钥端点，这对资源服务器检索公钥很有用。

由于我们希望实施简单且易于快速设置，因此我们将使用客户和用户的预注册商店，显然还有一个用于访问令牌的 JWT 商店。

在直接进入主题之前，请务必注意本教程中的示例用于教育目的。对于生产系统，强烈建议使用成熟的、经过良好测试的解决方案，例如[Keycloak](https://www.baeldung.com/spring-boot-keycloak)。

## 2. OAuth 2.0 概述

在本节中，我们将简要概述 OAuth 2.0 角色和授权码授予流程。

### 2.1. 角色

OAuth 2.0 框架意味着以下四个角色之间的协作：

-   资源所有者：通常，这是最终用户——它是拥有一些值得保护的资源的实体
-   资源服务器：保护资源所有者数据的服务，通常通过 REST API 发布
-   客户端：使用资源所有者数据的应用程序
-   授权服务器：以过期令牌的形式向客户端授予许可或权限的应用程序

### 2.2. 授权授予类型

授权类型 是客户端如何获得使用资源所有者数据的权限，最终以访问令牌的形式。 

自然，不同类型的客户[喜欢不同类型的赠款](https://oauth2.thephpleague.com/authorization-server/which-grant/)：

-   授权码：最常用 —— 无论是Web 应用程序、本机应用程序还是单页应用程序，尽管本机和单页应用程序需要称为 PKCE 的额外保护
-   Refresh Token : 一种特殊的更新授权，适用于 Web 应用程序更新其现有令牌
-   客户端凭证：首选用于服务到服务的通信，比如当资源所有者不是最终用户时
-   Resource Owner Password : 优先用于原生应用的第一方认证， 比如当移动应用需要自己的登录页面时

此外，客户端可以使用隐式授权类型。但是，使用 PKCE 授予授权码通常更安全。

### 2.3. 授权码授予流程

由于授权代码授予流程是最常见的，让我们也回顾一下它是如何工作的，这实际上就是我们将在本教程中构建的内容。

应用程序(客户端)通过重定向到授权服务器的/authorize端点来请求许可。对于这个端点，应用程序给出了一个回调端点。

授权服务器通常会向最终用户(资源所有者)请求许可。如果最终用户授予权限，则授权服务器会使用 代码重定向回回调。

应用程序接收此代码，然后对授权服务器的/token 端点进行经过身份验证的调用 。 通过“已验证”，我们的意思是应用程序证明它是谁作为此调用的一部分。如果全部按顺序出现，则授权服务器使用令牌进行响应。

有了令牌，应用程序向 API(资源服务器)发出请求，该 API 将验证令牌。它可以要求授权服务器使用其/introspect端点验证令牌。或者，如果令牌是自包含的，资源服务器可以通过在本地验证令牌的签名来进行优化，就像 JWT 的情况一样。

### 2.4. Jakarta EE 支持什么？

还不多。在本教程中，我们将从头开始构建大部分内容。

## 3. OAuth 2.0授权服务器

在此实现中，我们将重点关注最常用的授权类型：授权码。

### 3.1. 客户和用户注册

当然，授权服务器在授权他们的请求之前需要了解客户端和用户。授权服务器为此提供 UI 是很常见的。

不过，为简单起见，我们将使用预配置的客户端：

```sql
INSERT INTO clients (client_id, client_secret, redirect_uri, scope, authorized_grant_types) 
VALUES ('webappclient', 'webappclientsecret', 'http://localhost:9180/callback', 
  'resource.read resource.write', 'authorization_code refresh_token');
@Entity
@Table(name = "clients")
public class Client {
    @Id
    @Column(name = "client_id")
    private String clientId;
    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "redirect_uri")
    private String redirectUri;

    @Column(name = "scope")
    private String scope;

    // ...
}
```

和一个预先配置的用户：

```sql
INSERT INTO users (user_id, password, roles, scopes)
VALUES ('appuser', 'appusersecret', 'USER', 'resource.read resource.write');
@Entity
@Table(name = "users")
public class User implements Principal {
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "password")
    private String password;

    @Column(name = "roles")
    private String roles;

    @Column(name = "scopes")
    private String scopes;

    // ...
}
```

请注意，为了本教程，我们使用纯文本密码，但在生产环境中，它们应该被散列。

对于本教程的其余部分，我们将展示appuser(资源所有者)如何通过实施授权代码来授予对webappclient(应用程序)的访问权限。

### 3.2. 授权端点

授权端点的主要作用是首先对用户进行身份验证，然后请求应用程序所需的权限或范围。

按照[OAuth2 规范的指示](https://tools.ietf.org/html/rfc6749#section-3.1)，此端点应支持 HTTP GET 方法，尽管它也可以支持 HTTP POST 方法。在此实现中，我们将仅支持 HTTP GET 方法。

首先，授权端点要求用户经过身份验证。规范在这里不需要特定的方式，所以让我们使用[Jakarta EE 8 Security API](https://www.baeldung.com/java-ee-8-security)中的表单身份验证：

```java
@FormAuthenticationMechanismDefinition(
  loginToContinue = @LoginToContinue(loginPage = "/login.jsp", errorPage = "/login.jsp")
)
```

用户将被重定向到/login.jsp以进行身份验证，然后可以通过 S ecurityContext API 作为CallerPrincipal使用：

```java
Principal principal = securityContext.getCallerPrincipal();
```

我们可以使用 JAX-RS 将它们放在一起：

```java
@FormAuthenticationMechanismDefinition(
  loginToContinue = @LoginToContinue(loginPage = "/login.jsp", errorPage = "/login.jsp")
)
@Path("authorize")
public class AuthorizationEndpoint {
    //...    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response doGet(@Context HttpServletRequest request,
      @Context HttpServletResponse response,
      @Context UriInfo uriInfo) throws ServletException, IOException {
        
        MultivaluedMap<String, String> params = uriInfo.getQueryParameters();
        Principal principal = securityContext.getCallerPrincipal();
        // ...
    }
}
```

此时，授权端点可以开始处理应用程序的请求，该请求必须包含response_type和client_id参数以及(可选但推荐)redirect_uri、scope和state参数。

client_id应该是一个有效的客户端，在我们的例子中来自客户端数据库表。

redirect_uri ，如果指定的话，也应该匹配我们在客户端数据库表中找到的内容。

而且，因为我们正在执行授权代码，所以response_type是代码。 

由于授权是一个多步骤过程，我们可以暂时将这些值存储在会话中：

```java
request.getSession().setAttribute("ORIGINAL_PARAMS", params);
```

然后准备询问用户应用程序可能使用哪些权限，重定向到该页面：

```java
String allowedScopes = checkUserScopes(user.getScopes(), requestedScope);
request.setAttribute("scopes", allowedScopes);
request.getRequestDispatcher("/authorize.jsp").forward(request, response);
```

### 3.3. 用户范围批准

此时，浏览器为用户呈现授权UI，用户进行选择。然后，浏览器在 HTTP POST中提交用户的选择：

```java
@POST
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_HTML)
public Response doPost(@Context HttpServletRequest request, @Context HttpServletResponse response,
  MultivaluedMap<String, String> params) throws Exception {
    MultivaluedMap<String, String> originalParams = 
      (MultivaluedMap<String, String>) request.getSession().getAttribute("ORIGINAL_PARAMS");

    // ...

    String approvalStatus = params.getFirst("approval_status"); // YES OR NO

    // ... if YES

    List<String> approvedScopes = params.get("scope");

    // ...
}
```

接下来，我们生成一个引用user_id、client_id和 redirect_uri的临时代码，应用程序稍后在访问令牌端点时将使用所有这些代码。

因此，让我们使用自动生成的 id创建一个 AuthorizationCode JPA 实体：

```java
@Entity
@Table(name ="authorization_code")
public class AuthorizationCode {
@Id
@GeneratedValue(strategy=GenerationType.AUTO)
@Column(name = "code")
private String code;

//...

}
```

然后填充它：

```java
AuthorizationCode authorizationCode = new AuthorizationCode();
authorizationCode.setClientId(clientId);
authorizationCode.setUserId(userId);
authorizationCode.setApprovedScopes(String.join(" ", authorizedScopes));
authorizationCode.setExpirationDate(LocalDateTime.now().plusMinutes(2));
authorizationCode.setRedirectUri(redirectUri);
```

当我们保存 bean 时，代码属性会自动填充，因此我们可以获取它并将其发送回客户端：

```java
appDataRepository.save(authorizationCode);
String code = authorizationCode.getCode();
```

请注意，我们的授权码将在两分钟后过期——我们应该尽可能保守地处理这个过期时间。它可以很短，因为客户端将立即用它交换访问令牌。

然后我们重定向回应用程序的redirect_uri，为其提供代码以及应用程序在其/authorize请求中指定的任何状态参数：

```java
StringBuilder sb = new StringBuilder(redirectUri);
// ...

sb.append("?code=").append(code);
String state = params.getFirst("state");
if (state != null) {
    sb.append("&state=").append(state);
}
URI location = UriBuilder.fromUri(sb.toString()).build();
return Response.seeOther(location).build();
```

再次注意redirectUri是客户端表中存在的任何内容，而不是redirect_uri请求参数。

因此，我们的下一步是让客户端接收此代码并使用令牌端点将其交换为访问令牌。

### 3.4. 令牌端点

与授权端点相反，令牌端点不需要浏览器来与客户端通信，因此我们将把它实现为 JAX-RS 端点：

```java
@Path("token")
public class TokenEndpoint {

    List<String> supportedGrantTypes = Collections.singletonList("authorization_code");

    @Inject
    private AppDataRepository appDataRepository;

    @Inject
    Instance<AuthorizationGrantTypeHandler> authorizationGrantTypeHandlers;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response token(MultivaluedMap<String, String> params,
       @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) throws JOSEException {
        //...
    }
}
```

令牌端点需要 POST，以及使用application/x-www-form-urlencoded媒体类型对参数进行编码。

正如我们所讨论的，我们将仅支持授权代码授予类型：

```java
List<String> supportedGrantTypes = Collections.singletonList("authorization_code");
```

因此，应该支持接收到的作为必需参数的grant_type ：

```java
String grantType = params.getFirst("grant_type");
Objects.requireNonNull(grantType, "grant_type params is required");
if (!supportedGrantTypes.contains(grantType)) {
    JsonObject error = Json.createObjectBuilder()
      .add("error", "unsupported_grant_type")
      .add("error_description", "grant type should be one of :" + supportedGrantTypes)
      .build();
    return Response.status(Response.Status.BAD_REQUEST)
      .entity(error).build();
}
```

接下来，我们通过 HTTP 基本身份验证检查客户端身份验证。也就是说，我们通过 Authorization 标头检查接收到的client_id和 client_secret 是否与已注册的客户端匹配：

```java
String[] clientCredentials = extract(authHeader);
String clientId = clientCredentials[0];
String clientSecret = clientCredentials[1];
Client client = appDataRepository.getClient(clientId);
if (client == null || clientSecret == null || !clientSecret.equals(client.getClientSecret())) {
    JsonObject error = Json.createObjectBuilder()
      .add("error", "invalid_client")
      .build();
    return Response.status(Response.Status.UNAUTHORIZED)
      .entity(error).build();
}
```

最后，我们将TokenResponse的生成委托给相应的授权类型处理程序：

```java
public interface AuthorizationGrantTypeHandler {
    TokenResponse createAccessToken(String clientId, MultivaluedMap<String, String> params) throws Exception;
}
```

由于我们对授权代码授予类型更感兴趣，我们提供了一个充分的实现作为 CDI bean 并使用Named注解对其进行了修饰：

```plaintext
@Named("authorization_code")
```

在运行时，根据接收到的grant_type值，通过[CDI Instance机制](https://javaee.github.io/javaee-spec/javadocs/javax/enterprise/inject/Instance.html)激活相应的实现：

```java
String grantType = params.getFirst("grant_type");
//...
AuthorizationGrantTypeHandler authorizationGrantTypeHandler = 
  authorizationGrantTypeHandlers.select(NamedLiteral.of(grantType)).get();
```

现在是生成/token响应的时候了。

### 3.5. RSA私钥和公钥

在生成令牌之前，我们需要一个 RSA 私钥来对令牌进行签名。

为此，我们将使用 OpenSSL：

```bash
# PRIVATE KEY
openssl genpkey -algorithm RSA -out private-key.pem -pkeyopt rsa_keygen_bits:2048
```

private-key.pem使用文件META-INF/microprofile-config.properties通过 MicroProfile Config signingKey属性提供给服务器：

```plaintext
signingkey=/META-INF/private-key.pem
```

服务器可以使用注入的Config对象读取属性：

```java
String signingkey = config.getValue("signingkey", String.class);
```

同样，我们可以生成对应的公钥：

```bash
# PUBLIC KEY
openssl rsa -pubout -in private-key.pem -out public-key.pem
```

并使用 MicroProfile Config verificationKey读取它：

```plaintext
verificationkey=/META-INF/public-key.pem
```

服务器应将其提供给资源服务器以供验证之用。这是通过 JWK 端点完成的。

[Nimbus JOSE+JWT](https://connect2id.com/products/nimbus-jose-jwt)是一个可以在这里提供很大帮助的库。让我们首先添加[nimbus -jose-jwt依赖项](https://search.maven.org/search?q=g:com.nimbusds AND a:nimbus-jose-jwt&core=gav)：

```xml
<dependency>
    <groupId>com.nimbusds</groupId>
    <artifactId>nimbus-jose-jwt</artifactId>
    <version>7.7</version>
</dependency>
```

现在，我们可以利用 Nimbus 的 JWK 支持来简化我们的端点：

```java
@Path("jwk")
@ApplicationScoped
public class JWKEndpoint {

    @GET
    public Response getKey(@QueryParam("format") String format) throws Exception {
        //...

        String verificationkey = config.getValue("verificationkey", String.class);
        String pemEncodedRSAPublicKey = PEMKeyUtils.readKeyAsString(verificationkey);
        if (format == null || format.equals("jwk")) {
            JWK jwk = JWK.parseFromPEMEncodedObjects(pemEncodedRSAPublicKey);
            return Response.ok(jwk.toJSONString()).type(MediaType.APPLICATION_JSON).build();
        } else if (format.equals("pem")) {
            return Response.ok(pemEncodedRSAPublicKey).build();
        }

        //...
    }
}
```

我们使用 format参数在 PEM 和 JWK 格式之间切换。我们将用于实现资源服务器的 MicroProfile JWT 支持这两种格式。

### 3.6. 令牌端点响应

现在是给定AuthorizationGrantTypeHandler创建令牌响应的时候了。在此实现中，我们将仅支持结构化的 JWT 令牌。

为了以这种格式创建令牌，我们将再次使用[Nimbus JOSE+JWT](https://connect2id.com/products/nimbus-jose-jwt)库，但还有[许多其他 JWT 库](https://jwt.io/#libraries-io)。

因此，要创建签名的 JWT，我们首先必须构建 JWT 标头：

```java
JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build();
```

然后，我们构建有效负载，它是一组标准化和自定义声明：

```java
Instant now = Instant.now();
Long expiresInMin = 30L;
Date in30Min = Date.from(now.plus(expiresInMin, ChronoUnit.MINUTES));

JWTClaimsSet jwtClaims = new JWTClaimsSet.Builder()
  .issuer("http://localhost:9080")
  .subject(authorizationCode.getUserId())
  .claim("upn", authorizationCode.getUserId())
  .audience("http://localhost:9280")
  .claim("scope", authorizationCode.getApprovedScopes())
  .claim("groups", Arrays.asList(authorizationCode.getApprovedScopes().split(" ")))
  .expirationTime(in30Min)
  .notBeforeTime(Date.from(now))
  .issueTime(Date.from(now))
  .jwtID(UUID.randomUUID().toString())
  .build();
SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaims);
```

除了标准的 JWT 声明之外，我们还添加了两个声明——upn和组——因为 MicroProfile JWT 需要它们。upn将映射到 Jakarta EE Security CallerPrincipal，组将映射到 Jakarta EE角色。

现在我们有了标头和有效负载，我们需要使用 RSA 私钥对访问令牌进行签名。相应的 RSA 公钥将通过 JWK 端点公开或通过其他方式提供，以便资源服务器可以使用它来验证访问令牌。

由于我们已将私钥作为 PEM 格式提供，我们应该检索它并将其转换为 RSAPrivateKey ：

```java
SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaims);
//...
String signingkey = config.getValue("signingkey", String.class);
String pemEncodedRSAPrivateKey = PEMKeyUtils.readKeyAsString(signingkey);
RSAKey rsaKey = (RSAKey) JWK.parseFromPEMEncodedObjects(pemEncodedRSAPrivateKey);
```

接下来，我们对 JWT 进行签名和序列化：

```java
signedJWT.sign(new RSASSASigner(rsaKey.toRSAPrivateKey()));
String accessToken = signedJWT.serialize();
```

最后我们构造一个令牌响应：

```java
return Json.createObjectBuilder()
  .add("token_type", "Bearer")
  .add("access_token", accessToken)
  .add("expires_in", expiresInMin  60)
  .add("scope", authorizationCode.getApprovedScopes())
  .build();
```

多亏了 JSON-P，它被序列化为 JSON 格式并发送给客户端：

```javascript
{
  "access_token": "acb6803a48114d9fb4761e403c17f812",
  "token_type": "Bearer",  
  "expires_in": 1800,
  "scope": "resource.read resource.write"
}
```

## 4.OAuth 2.0 客户端

在本节中，我们将使用 Servlet、MicroProfile Config 和 JAX RS 客户端 API构建基于 Web 的 OAuth 2.0 客户端。

更准确地说，我们将实现两个主要的 servlet：一个用于请求授权服务器的授权端点并使用授权代码授予类型获取代码，另一个 servlet 用于使用接收到的代码并从授权服务器的令牌端点请求访问令牌.

此外，我们将实现另外两个 servlet：一个用于使用刷新令牌授予类型获取新的访问令牌，另一个用于访问资源服务器的 API。

### 4.1. OAuth 2.0 客户端详细信息

由于客户端已经在授权服务器中注册，我们首先需要提供客户端注册信息：

-   client_id： Client Identifier，一般由授权服务器在注册过程中下发。
-   client_secret： 客户秘密。
-   redirect_uri：接收授权码的位置。
-   范围：客户端请求的权限。

此外，客户端应该知道授权服务器的授权和令牌端点：

-   authorization_uri：我们可以用来获取代码的授权服务器授权端点的位置。
-   token_uri：我们可以用来获取令牌的授权服务器令牌端点的位置。

所有这些信息都通过 MicroProfile 配置文件META-INF/microprofile-config.properties提供：

```plaintext
# Client registration
client.clientId=webappclient
client.clientSecret=webappclientsecret
client.redirectUri=http://localhost:9180/callback
client.scope=resource.read resource.write

# Provider
provider.authorizationUri=http://127.0.0.1:9080/authorize
provider.tokenUri=http://127.0.0.1:9080/token
```

### 4.2. 授权码请求

获取授权代码的流程从客户端开始，方法是将浏览器重定向到授权服务器的授权端点。

通常，当用户试图在未经授权的情况下或通过调用客户端/authorize路径显式访问受保护的资源 API 时，会发生这种情况：

```java
@WebServlet(urlPatterns = "/authorize")
public class AuthorizationCodeServlet extends HttpServlet {

    @Inject
    private Config config;

    @Override
    protected void doGet(HttpServletRequest request, 
      HttpServletResponse response) throws ServletException, IOException {
        //...
    }
}
```

在doGet()方法中，我们首先生成并存储安全状态值：

```java
String state = UUID.randomUUID().toString();
request.getSession().setAttribute("CLIENT_LOCAL_STATE", state);
```

然后，我们检索客户端配置信息：

```java
String authorizationUri = config.getValue("provider.authorizationUri", String.class);
String clientId = config.getValue("client.clientId", String.class);
String redirectUri = config.getValue("client.redirectUri", String.class);
String scope = config.getValue("client.scope", String.class);
```

然后我们将这些信息作为查询参数附加到授权服务器的授权端点：

```java
String authorizationLocation = authorizationUri + "?response_type=code"
  + "&client_id=" + clientId
  + "&redirect_uri=" + redirectUri
  + "&scope=" + scope
  + "&state=" + state;
```

最后，我们将浏览器重定向到这个 URL：

```java
response.sendRedirect(authorizationLocation);
```

处理请求后，授权服务器的授权端点将生成并附加一个代码，除了接收到的状态参数，到redirect_uri并将重定向回浏览器[http://localhost:9081/callback?code=A123&state=Y](http://localhost:9081/callback?code=A123&state=Y)。

### 4.3. 访问令牌请求

客户端回调 servlet /callback从验证接收到的状态开始：

```java
String localState = (String) request.getSession().getAttribute("CLIENT_LOCAL_STATE");
if (!localState.equals(request.getParameter("state"))) {
    request.setAttribute("error", "The state attribute doesn't match!");
    dispatch("/", request, response);
    return;
}
```

接下来，我们将使用之前收到的代码通过授权服务器的令牌端点请求访问令牌：

```java
String code = request.getParameter("code");
Client client = ClientBuilder.newClient();
WebTarget target = client.target(config.getValue("provider.tokenUri", String.class));

Form form = new Form();
form.param("grant_type", "authorization_code");
form.param("code", code);
form.param("redirect_uri", config.getValue("client.redirectUri", String.class));

TokenResponse tokenResponse = target.request(MediaType.APPLICATION_JSON_TYPE)
  .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue())
  .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), TokenResponse.class);
```

正如我们所见，此调用没有浏览器交互，请求是直接使用 JAX-RS 客户端 API 作为 HTTP POST 发出的。

由于令牌端点需要客户端身份验证，因此我们在授权标头中包含了客户端凭据client_id和client_secret。

客户端可以使用此访问令牌来调用资源服务器 API，这是下一小节的主题。

### 4.4. 受保护的资源访问

此时，我们有一个有效的访问令牌，我们可以调用资源服务器的/读取和/写入API。

为此，我们必须提供授权标头。使用 JAX-RS 客户端 API，这只需通过Invocation.Builder header()方法即可完成：

```java
resourceWebTarget = webTarget.path("resource/read");
Invocation.Builder invocationBuilder = resourceWebTarget.request();
response = invocationBuilder
  .header("authorization", tokenResponse.getString("access_token"))
  .get(String.class);
```

## 5. OAuth 2.0 资源服务器

在本节中，我们将构建一个基于 JAX-RS、MicroProfile JWT 和 MicroProfile Config 的安全 Web 应用程序。MicroProfile JWT 负责验证接收到的 JWT 并将 JWT 范围映射到 Jakarta EE 角色。

### 5.1. Maven 依赖项

除了[Java EE Web API](https://search.maven.org/search?q=g:javax AND a:javaee-web-api&core=gav)依赖项之外，我们还需要[MicroProfile Config](https://search.maven.org/search?q=g:org.eclipse.microprofile.config AND a:microprofile-config-api&core=gav)和[MicroProfile JWT](https://search.maven.org/search?q=g:org.eclipse.microprofile.jwt AND a:microprofile-jwt-auth-api&core=gav) API：

```xml
<dependency>
    <groupId>javax</groupId>
    <artifactId>javaee-web-api</artifactId>
    <version>8.0</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>org.eclipse.microprofile.config</groupId>
    <artifactId>microprofile-config-api</artifactId>
    <version>1.3</version>
</dependency>
<dependency>
    <groupId>org.eclipse.microprofile.jwt</groupId>
    <artifactId>microprofile-jwt-auth-api</artifactId>
    <version>1.1</version>
</dependency>
```

### 5.2. JWT认证机制

MicroProfile JWT 提供了 Bearer Token Authentication 机制的实现。这负责处理Authorization标头中存在的 JWT，使 Jakarta EE 安全主体作为持有 JWT 声明的JsonWebToken可用，并将范围映射到 Jakarta EE 角色。查看[Jakarta EE 安全 API](https://www.baeldung.com/java-ee-8-security)了解更多背景信息。

要在服务端启用JWT 认证机制，我们需要在 JAX-RS 应用中添加LoginConfig注解：

```java
@ApplicationPath("/api")
@DeclareRoles({"resource.read", "resource.write"})
@LoginConfig(authMethod = "MP-JWT")
public class OAuth2ResourceServerApplication extends Application {
}
```

此外，MicroProfile JWT 需要 RSA 公钥才能验证 JWT 签名。我们可以通过内省或为简单起见，通过从授权服务器手动密钥来提供此功能。无论哪种情况，我们都需要提供公钥的位置：

```plaintext
mp.jwt.verify.publickey.location=/META-INF/public-key.pem
```

最后，MicroProfile JWT 需要验证传入 JWT 的iss声明，它应该存在并匹配 MicroProfile Config 属性的值：

```plaintext
mp.jwt.verify.issuer=http://127.0.0.1:9080
```

通常，这是授权服务器的位置。

### 5.3. 安全端点

出于演示目的，我们将添加一个具有两个端点的资源 API。一个是具有resource.read范围的用户可以访问的读取端点，另一个是具有resource.write范围的用户可以访问的写入端点。

对作用域的限制是通过@RolesAllowed注解完成的：

```java
@Path("/resource")
@RequestScoped
public class ProtectedResource {

    @Inject
    private JsonWebToken principal;

    @GET
    @RolesAllowed("resource.read")
    @Path("/read")
    public String read() {
        return "Protected Resource accessed by : " + principal.getName();
    }

    @POST
    @RolesAllowed("resource.write")
    @Path("/write")
    public String write() {
        return "Protected Resource accessed by : " + principal.getName();
    }
}
```

## 6. 运行所有服务器

要运行一台服务器，我们只需要在相应的目录中调用 Maven 命令：

```bash
mvn package liberty:run-server
```

授权服务器、客户端和资源服务器将分别在以下位置运行和可用：

```plaintext
# Authorization Server
http://localhost:9080/

# Client
http://localhost:9180/

# Resource Server
http://localhost:9280/

```

因此，我们可以访问客户端主页，然后单击“获取访问令牌”以启动授权流程。收到access token后，我们就可以访问资源服务器的读写API了。

根据授予的范围，资源服务器将通过成功消息进行响应，或者我们将获得 HTTP 403 禁止状态。

## 七. 总结

在本文中，我们提供了一个 OAuth 2.0 授权服务器的实现，它可以与任何兼容的 OAuth 2.0 客户端和资源服务器一起使用。

为了说明整体框架，我们还提供了客户端和资源服务器的实现。为了实现所有这些组件，我们使用了 Jakarta EE 8 API，尤其是 CDI、Servlet、JAX RS、Jakarta EE Security。此外，我们还使用了 MicroProfile 的伪 Jakarta EE API：MicroProfile Config 和 MicroProfile JWT。

[GitHub 上](https://github.com/eugenp/tutorials/tree/master/security-modules/oauth2-framework-impl)提供了示例的完整源代码。请注意，该代码包括授权代码和刷新令牌授权类型的示例。

最后，重要的是要了解本文的教育性质，并且所给出的示例不应在生产系统中使用。