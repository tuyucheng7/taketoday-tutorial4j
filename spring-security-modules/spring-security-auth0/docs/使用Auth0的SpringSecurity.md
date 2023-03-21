## 1. 概述

**[Auth0](https://auth0.com/)为各种类型的应用程序(如本机应用程序、单页应用程序和Web应用程序)提供身份验证和授权服务**。此外，**它还允许实现各种功能，如单点登录、社交App登录和多因素身份验证**。

在本教程中，我们将通过分步指南探索Auth0的Spring Security，以及Auth0帐户的关键配置。

## 2. 设置Auth0

### 2.1 Auth0注册

**首先，我们将[注册](https://auth0.com/signup)一个免费的Auth0账户，该账号为多达7k的活动用户提供无限登录的访问权限**。如果你已经有一个账号，我们可以跳过这一节：

<img src="../assets/img-1.png">

### 2.2 仪表板

登录到Auth0帐户后，我们将看到一个仪表板，其中突出显示了登录记录、最新登录和最新注册等详细信息：

<img src="../assets/img-2.png">

### 2.3 创建新应用程序

然后，从Applications菜单中，我们将为Spring Boot创建一个新的[OpenID Connect(OIDC)应用程序](https://www.baeldung.com/spring-security-openid-connect)。

此外，我们将从Native、Single-Page Apps和Machine to Machine Apps等可用选项中**选择Regular Web Applications作为应用程序类型**：

<img src="../assets/img-3.png">

选择常规Web应用程序，填写App的名称：

<img src="../assets/img-4.png">

### 2.4 应用程序设置

接下来，我们会得到一些应用程序URI，例如指向应用程序的Callback URL和Logout URL：

<img src="../assets/img-5.png">

### 2.5 客户端凭据

最后，我们会得到与我们的应用关联的Domain、Client ID和Client Secret的值：

<img src="../assets/img-6.png">

请保存好这些凭据，因为它们是我们Spring Boot应用程序中的Auth0设置所必需的。

## 3. Spring Boot应用程序设置

现在我们的Auth0帐户已准备好关键配置，我们准备将Auth0安全性集成到Spring Boot应用程序中。

### 3.1 Maven

首先，让我们将最新的[mvc-auth-commons](https://central.sonatype.com/artifact/com.auth0/mvc-auth-commons/1.9.4) Maven依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>mvc-auth-commons</artifactId>
    <version>1.2.0</version>
</dependency>
```

### 3.2 Gradle

同样，在使用Gradle时，我们可以在build.gradle文件中添加mvc-auth-commons依赖：

```groovy
compile 'com.auth0:mvc-auth-commons:1.2.0'
```

### 3.3 application.properties

我们的Spring Boot应用程序需要Client Id和Client Secret等信息来启用Auth0帐户的身份验证。因此，我们需要将它们添加到application.properties文件中：

```properties
com.auth0.domain=dev-o9mb6xdi.us.auth0.com
com.auth0.clientId={clientId}
com.auth0.clientSecret={clientSecret}
```

### 3.4 AuthConfig

接下来，我们创建AuthConfig类以从application.properties文件中读取Auth0属性：

```java
@Configuration
@EnableWebSecurity
public class AuthConfig {
    @Value(value = "${com.auth0.domain}")
    private String domain;

    @Value(value = "${com.auth0.clientId}")
    private String clientId;

    @Value(value = "${com.auth0.clientSecret}")
    private String clientSecret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
              .disable()
              .authorizeRequests()
              .antMatchers("/callback", "/login", "/").permitAll()
              .anyRequest().authenticated()
              .and()
              .formLogin()
              .loginPage("/login")
              .and()
              .logout().logoutSuccessHandler(logoutSuccessHandler()).permitAll();
        return http.build();
    }
}
```

此外，AuthConfig类配置为[通过创建SecurityFilterChain bean启用Web安全](https://www.baeldung.com/spring-boot-security-autoconfiguration#configuring-spring-boot-security)。

### 3.5 AuthenticationController

最后，我们将[AuthenticationController](https://javadoc.io/doc/com.auth0/mvc-auth-commons/latest/com/auth0/AuthenticationController.html)类的bean引用添加到已经讨论过的AuthConfig类：

```java
@Bean
public AuthenticationController authenticationController() throws UnsupportedEncodingException {
    JwkProvider jwkProvider = new JwkProviderBuilder(domain).build();
    return AuthenticationController.newBuilder(domain, clientId, clientSecret)
          .withJwkProvider(jwkProvider)
          .build();
}
```

在这里，我们在构建AuthenticationController类的实例时使用了[JwkProviderBuilder](https://javadoc.io/doc/com.auth0/jwks-rsa/latest/com/auth0/jwk/JwkProviderBuilder.html)类。我们将使用它获取公钥来验证token的签名(默认情况下，token使用RS256非对称签名算法进行签名)。

此外，authenticationController bean提供用于登录的授权URL并处理回调请求。

## 4. AuthController

接下来，我们将为登录和回调功能创建AuthController类：

```java
@Controller
public class AuthController {

    @Autowired
    private AuthConfig config;

    @Autowired
    private AuthenticationController authenticationController;
}
```

在这里，我们注入了上一节中讨论的AuthConfig和AuthenticationController类型的bean。

### 4.1 登录

让我们创建允许我们的Spring Boot应用程序对用户进行身份验证的login方法：

```java
@GetMapping(value = "/login")
protected void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String redirectUri = "http://localhost:8080/callback";
    String authorizeUrl = authenticationController.buildAuthorizeUrl(request, response, redirectUri)
          .withScope("openid email")
          .build();
    response.sendRedirect(authorizeUrl);
}
```

buildAuthorizeUrl方法生成Auth0授权URL并重定向到默认的Auth0登录页面。

### 4.2 回调

一旦用户使用Auth0凭据登录，回调请求将发送到我们的Spring Boot应用程序。为此，让我们创建callback方法：

```java
@GetMapping(value = "/callback")
public void callback(HttpServletRequest request, HttpServletResponse response) throws IOException, IdentityVerificationException {
    Tokens tokens = authenticationController.handle(request, response);

    DecodedJWT jwt = JWT.decode(tokens.getIdToken());
    TestingAuthenticationToken authToken2 = new TestingAuthenticationToken(jwt.getSubject(), jwt.getToken());
    authToken2.setAuthenticated(true);

    SecurityContextHolder.getContext().setAuthentication(authToken2);
    response.sendRedirect(config.getContextPath(request) + "/");
}
```

我们处理回调请求，获取代表认证成功的accessToken和idToken。然后，我们创建了[TestingAuthenticationToken](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/authentication/TestingAuthenticationToken.html)对象来[设置SecurityContextHolder中的authentication](https://www.baeldung.com/manually-set-user-authentication-spring-security)。

但是，我们可以创建[AbstractAuthenticationToken](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/authentication/AbstractAuthenticationToken.html)类的实现以获得更好的可用性。

## 5. HomeController

最后，我们将为应用程序的登录页面创建具有默认映射的HomeController：

```java
@Controller
public class HomeController {

    @GetMapping(value = "/")
    @ResponseBody
    public String home(final Authentication authentication) {
        TestingAuthenticationToken token = (TestingAuthenticationToken) authentication;
        DecodedJWT jwt = JWT.decode(token.getCredentials().toString());
        String email = jwt.getClaims().get("email").asString();
        return "Welcome, " + email + "!";
    }
}
```

这里，我们从idToken中提取了[DecodedJWT](https://www.javadoc.io/doc/com.auth0/java-jwt/latest/com/auth0/jwt/interfaces/DecodedJWT.html)对象。此外，用户信息(如电子邮件)是从claims中获取的。

就是这样！我们的Spring Boot应用程序已准备好提供Auth0安全支持。让我们使用Maven命令运行我们的应用程序：

```shell
mvn spring-boot:run
```

在[localhost:8080/login](http://localhost:8080/login)访问应用程序时，我们可以看到Auth0提供的默认登录页面：

<img src="../assets/img-7.png">

使用注册用户的凭据登录后，将显示包含用户电子邮件的欢迎消息：

<img src="../assets/img-8.png">

此外，我们可以在默认登录屏幕上找到一个“Sign up”按钮，用于自行注册。

## 6. 注册

### 6.1 自行注册

第一次登录时，我们可以使用“Sign up”按钮创建一个Auth0帐户，然后提供电子邮件和密码等信息：

<img src="../assets/img-1.png">

### 6.2 创建用户

或者，我们可以从Auth0帐户的Users菜单中创建一个新用户：

<img src="../assets/img-9.png">

输入对应的信息：

<img src="../assets/img-10.png">

### 6.3 连接设置

此外，我们可以选择各种类型的连接，例如数据库和社交登录，用于注册/登录我们的Spring Boot应用程序：

我们可以选择一系列社交软件，如Github、Google：

<img src="../assets/img-11.png">

## 7. LogoutController

现在我们已经了解了登录和回调功能，我们可以向我们的Spring Boot应用程序添加[注销功能](https://www.baeldung.com/spring-security-logout)。

让我们创建一个实现LogoutSuccessHandler的LogoutController类：

```java
@Controller
public class LogoutController implements LogoutSuccessHandler {
    @Autowired
    private AuthConfig config;

    @Override
    public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse res,
                                Authentication authentication) {
        if (req.getSession() != null) {
            req.getSession().invalidate();
        }
        String returnTo = "http://localhost:8080/";
        String logoutUrl = "https://dev-o9mb6xdi.us.auth0.com/v2/logout?client_id=" + config.getClientId() + "&returnTo=" + returnTo;
        res.sendRedirect(logoutUrl);
    }
}
```

在这里，onLogoutSuccess方法被覆盖以调用/v2/logout Auth0注销URL。

## 8. Auth0 Management API

到目前为止，我们已经在Spring Boot应用程序中集成了Auth0安全性。现在，让我们在同一个应用程序中与Auth0 Management API(系统API)进行交互。

### 8.1 创建新App

首先，要访问Auth0 Management API，我们将在Auth0帐户中创建一个Machine to Machine Application：

<img src="../assets/img-12.png">

### 8.2 授权

然后，我们将向Auth0 Management API添加授权，使其具有读取/创建用户的权限：

<img src="../assets/img-13.png">

### 8.3 客户端凭据

最后，我们可以得到Client Id和Client Secret，以从我们的Spring Boot应用程序访问Auth0 Management App：

<img src="../assets/img-14.png">

### 8.4 Access Token

让我们使用上一节中得到的客户端凭据为Auth0 Management App生成访问令牌：

```java
public String getManagementApiToken() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    JSONObject requestBody = new JSONObject();
    requestBody.put("client_id", "hfUOjedOrkZhgDIkpv7QrO9gIL7c7gdl");
    requestBody.put("client_secret", "2fWGr-LK3boq2axR0Z_aYUq0RTE5u8Z-tZiN_nRXOyEQVTPqPMIyWyAHeNm1e7Jx");
    requestBody.put("audience", "https://dev-o9mb6xdi.us.auth0.com/api/v2/");
    requestBody.put("grant_type", "client_credentials");

    HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

    RestTemplate restTemplate = new RestTemplate();
    HashMap<String, String> result = restTemplate.postForObject("https://dev-o9mb6xdi.us.auth0.com/oauth/token", request, HashMap.class);
    return result.get("access_token");
}
```

在这里，我们向/oauth/token Auth0令牌URL发出REST请求，以获取访问和刷新令牌。

此外，我们可以将这些客户端凭据存储在application.properties文件中，并使用AuthConfig类读取它。

### 8.5 UserController

之后，让我们创建一个包含users方法的UserController类：

```java
@Controller
public class UserController {

    @GetMapping(value = "/users")
    @ResponseBody
    public ResponseEntity<String> users(HttpServletRequest request, HttpServletResponse response) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getManagementApiToken());

        HttpEntity<String> entity = new HttpEntity<String>(headers);

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate
              .exchange("https://dev-o9mb6xdi.us.auth0.com/api/v2/users", HttpMethod.GET, entity, String.class);
    }
}
```

users方法通过使用上一节中生成的访问令牌向/api/v2/users Auth0 API发出GET请求来获取所有用户的列表。

因此，当我们访问[localhost:8080/users](http://localhost:8080/users)时，可以得到包含所有用户的JSON响应：

```json
[
    {
        "created_at": "2022-04-01T17:52:28.767Z",
        "email": "tuyucheng2000@163.com",
        "email_verified": false,
        "identities": [
            {
                "connection": "Username-Password-Authentication",
                "provider": "auth0",
                "user_id": "62473bdc9bf338006971b250",
                "isSocial": false
            }
        ],
        "name": "tuyucheng2000@163.com",
        "nickname": "tuyucheng2000",
        "picture": "https://s.gravatar.com/avatar/bb867b73be298109d59a71b441e6d53d?s=480&r=pg&d=https%3A%2F%2Fcdn.auth0.com%2Favatars%2Ftu.png",
        "updated_at": "2022-10-19T12:54:21.800Z",
        "user_id": "auth0|62473bdc9bf338006971b250",
        "last_login": "2022-10-19T12:54:21.800Z",
        "last_ip": "45.62.168.61",
        "logins_count": 4
    }
]
```

### 8.6 创建用户

同样，我们可以通过向/api/v2/users Auth0 API发出POST请求来创建用户：

```java
@GetMapping(value = "/createUser")
@ResponseBody
public ResponseEntity<String> createUser(HttpServletResponse response) {
    JSONObject request = new JSONObject();
    request.put("email", "norman.lewis@email.com");
    request.put("given_name", "Norman");
    request.put("family_name", "Lewis");
    request.put("connection", "Username-Password-Authentication");
    request.put("password", "Pa33w0rd");
    // ...
    return restTemplate.postForEntity("https://dev-o9mb6xdi.us.auth0.com/api/v2/users", request.toString(), String.class);
}
```

然后，让我们访问[localhost:8080/createUser](http://localhost:8080/createUser)并验证新用户的详细信息：

```json
{
    "created_at": "2022-04-01T17:54:44.296Z",
    "email": "norman.lewis@email.com",
    "email_verified": false,
    "family_name": "Lewis",
    "given_name": "Norman",
    "identities": [
        {
            "connection": "Username-Password-Authentication",
            "user_id": "62473c647ca173006f55bf23",
            "provider": "auth0",
            "isSocial": false
        }
    ],
    "name": "norman.lewis@email.com",
    "nickname": "norman.lewis",
    "picture": "https://s.gravatar.com/avatar/b3d4fa673c5dc9c06d7c4953605a8fd1?s=480&r=pg&d=https%3A%2F%2Fcdn.auth0.com%2Favatars%2Fno.png",
    "updated_at": "2022-04-01T17:54:44.296Z",
    "user_id": "auth0|62473c647ca173006f55bf23"
}
```

类似地，**我们可以执行各种操作，例如列出所有连接、创建连接、列出所有客户端以及使用Auth0 API创建客户端**，具体取决于我们的权限。

## 9. 总结

在本教程中，我们通过Spring Boot和Spring Security集成了Auth0。

首先，我们使用基本配置设置Auth0帐户。然后，我们创建了一个Spring Boot应用程序并配置了application.properties，以便将Spring Security与Auth0集成。

接下来，我们研究了如何为Auth0 Management API创建API令牌。最后，我们研究了获取所有用户和创建用户等功能。