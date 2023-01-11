## 1. 概述

安全性是 Spring 生态系统中的一等公民。因此，OAuth2 几乎无需配置即可与 Spring Web MVC 一起工作也就不足为奇了。

然而，原生 Spring 解决方案并不是实现表示层的唯一方法。[Jersey](https://eclipse-ee4j.github.io/jersey/)是一个符合 JAX-RS 的实现，也可以与 Spring OAuth2 协同工作。

在本教程中，我们将了解如何使用 Spring Social Login 保护 Jersey 应用程序，该应用程序是使用 OAuth2 标准实现的。

## 2.Maven依赖

让我们添加[spring-boot-starter-jersey](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-jersey)工件以将 Jersey 集成到Spring Boot应用程序中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jersey</artifactId>
</dependency>
```

要配置安全 OAuth2，我们需要[spring-boot-starter-security](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-security)和[spring-security-oauth2-client](https://search.maven.org/search?q=g:org.springframework.security a:spring-security-oauth2-client)：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-oauth2-client</artifactId>
</dependency>

```

我们将使用[Spring Boot Starter Parent 版本 2管理所有这些依赖项](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-dependencies)。

## 3. Jersey 表示层

我们需要一个具有几个端点的资源类来使用 Jersey 作为表示层。

### 3.1。资源类

这是包含端点定义的类：

```java
@Path("/")
public class JerseyResource {
    // endpoint definitions
}
```

类本身非常简单——它只有一个@Path注解。此注解的值标识类主体中所有端点的基本路径。

值得一提的是，该资源类不携带用于组件扫描的构造型注解。事实上，它甚至不需要是 Spring bean。原因是我们不依赖 Spring 来处理请求映射。

### 3.2. 登录页面

下面是处理登录请求的方法：

```java
@GET
@Path("login")
@Produces(MediaType.TEXT_HTML)
public String login() {
    return "Log in with <a href="/oauth2/authorization/github">GitHub</a>";
}
```

此方法为以/login端点为目标的 GET 请求返回一个字符串。text/html内容类型指示用户的浏览器显示带有可点击链接的响应。

我们将使用 GitHub 作为 OAuth2 提供者，因此使用链接/oauth2/authorization/github。此链接将触发重定向到 GitHub 授权页面。

### 3.3. 主页

让我们定义另一种方法来处理对根路径的请求：

```java
@GET
@Produces(MediaType.TEXT_PLAIN)
public String home(@Context SecurityContext securityContext) {
    OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) securityContext.getUserPrincipal();
    OAuth2AuthenticatedPrincipal authenticatedPrincipal = authenticationToken.getPrincipal();
    String userName = authenticatedPrincipal.getAttribute("login");
    return "Hello " + userName;
}
```

该方法返回主页，这是一个包含登录用户名的字符串。请注意，在这种情况下，我们从登录属性中提取了用户名。不过，另一个 OAuth2 提供者可能会为用户名使用不同的属性。

显然，上述方法仅适用于经过身份验证的请求。如果请求未经身份验证，它将被重定向到登录端点。我们将在第 4 节中看到如何配置此重定向。

### 3.4. 使用 Spring 容器注册 Jersey

让我们使用 servlet 容器注册资源类以启用 Jersey 服务。幸运的是，这很简单：

```java
@Component
public class RestConfig extends ResourceConfig {
    public RestConfig() {
        register(JerseyResource.class);
    }
}
```

通过在ResourceConfig子类中注册JerseyResource，我们通知了 servlet 容器该资源类中的所有端点。

最后一步是向Spring 容器注册ResourceConfig子类，在本例中为RestConfig 。我们使用@Component注解实现了这个注册。

## 4.配置Spring Security

我们可以像配置普通 Spring 应用程序一样为 Jersey 配置安全性：

```java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/login")
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .oauth2Login()
            .loginPage("/login");
        return http.build();
    }
}
```

给定链中最重要的方法是oauth2Login。此方法使用 OAuth 2.0 提供程序配置身份验证支持。在本教程中，提供者是 GitHub。

另一个值得注意的配置是登录页面。通过向loginPage方法提供字符串“/login”，我们告诉 Spring将未经身份验证的请求重定向到/login端点。

请注意，默认安全配置还在/login处提供了一个自动生成的页面。因此，即使我们没有配置登录页面，未经身份验证的请求仍然会被重定向到该端点。

默认配置和显式设置的区别在于，在默认情况下，应用程序返回的是生成的页面，而不是我们自定义的字符串。

## 5. 应用配置

为了拥有受 OAuth2 保护的应用程序，我们需要向 OAuth2 提供者注册客户端。之后，将客户端的凭据添加到应用程序。

### 5.1。注册 OAuth2 客户端

[让我们通过注册一个 GitHub 应用程序](https://github.com/settings/developers)开始注册过程。登陆 GitHub 开发者页面后，点击New OAuth App按钮打开Register a new OAuth application form。

接下来，用适当的值填写显示的表格。对于应用程序名称，输入任何使应用程序可识别的字符串。主页 URL 可以是http://localhost:8083，授权回调 URL 是http://localhost:8083/login/oauth2/code/github。

回调 URL 是用户通过 GitHub 进行身份验证并授予应用程序访问权限后浏览器重定向到的路径。

这是注册表的样子：

 

[![注册一个新的 oauth 应用程序](https://www.baeldung.com/wp-content/uploads/2020/09/register-a-new-oauth-application-300x294-1.png)](https://www.baeldung.com/wp-content/uploads/2020/09/register-a-new-oauth-application-300x294-1.png)

现在，单击注册应用程序按钮。然后浏览器应重定向到 GitHub 应用程序的主页，该主页显示客户端 ID 和客户端密码。

### 5.2. 配置Spring Boot应用程序

让我们在类路径中添加一个名为jersey-application.properties的属性文件：

```java
server.port=8083
spring.security.oauth2.client.registration.github.client-id=<your-client-id>
spring.security.oauth2.client.registration.github.client-secret=<your-client-secret>
```

请记住将占位符<your-client-id>和<your-client-secret>替换为我们自己的 GitHub 应用程序中的值。

最后，将此文件作为属性源添加到Spring Boot应用程序：

```java
@SpringBootApplication
@PropertySource("classpath:jersey-application.properties")
public class JerseyApplication {
    public static void main(String[] args) {
        SpringApplication.run(JerseyApplication.class, args);
    }
}
```

## 6. 身份验证在行动

让我们看看在注册 GitHub 后如何登录到我们的应用程序。

### 6.1。访问应用程序

让我们启动应用程序，然后访问地址为localhost:8083的主页。由于请求未经身份验证，我们将被重定向到登录页面：

 

[![登录页面](https://www.baeldung.com/wp-content/uploads/2020/09/login-page-300x46-1.png)](https://www.baeldung.com/wp-content/uploads/2020/09/login-page-300x46-1.png)

现在，当我们点击 GitHub 链接时，浏览器将重定向到 GitHub 授权页面：

 

[![github授权页面](https://www.baeldung.com/wp-content/uploads/2020/09/github-authorize-page-261x300-1.png)](https://www.baeldung.com/wp-content/uploads/2020/09/github-authorize-page-261x300-1.png)

通过查看 URL，我们可以看到重定向的请求携带了许多查询参数，例如response_type、client_id和scope：

```http
https://github.com/login/oauth/authorize?response_type=code&client_id=c30a16c45a9640771af5&scope=read:user&state=dpTme3pB87wA7AZ--XfVRWSkuHD3WIc9Pvn17yeqw38%3D&redirect_uri=http://localhost:8083/login/oauth2/code/github
```

response_type的值为code ，表示 OAuth2 授权类型为授权码。同时，client_id参数有助于识别我们的应用程序。关于所有参数的含义，请前往[GitHub 开发者页面](https://docs.github.com/en/developers/apps/authorizing-oauth-apps#parameters)。

当授权页面出现时，我们需要授权应用程序继续。授权成功后，浏览器将重定向到我们应用程序中预定义的端点，以及一些查询参数：

```http
http://localhost:8083/login/oauth2/code/github?code=561d99681feeb5d2edd7&state=dpTme3pB87wA7AZ--XfVRWSkuHD3WIc9Pvn17yeqw38%3D
```

在幕后，应用程序将用授权码交换访问令牌。之后，它使用这个令牌来获取登录用户的信息。

对localhost:8083/login/oauth2/code/github的请求返回后，浏览器返回首页。这一次，我们应该看到一条带有我们自己用户名的问候消息：

[![主页](https://www.baeldung.com/wp-content/uploads/2020/09/home-page-300x47-1.png)](https://www.baeldung.com/wp-content/uploads/2020/09/home-page-300x47-1.png)

 

### 6.2. 如何获取用户名？

很明显，问候消息中的用户名就是我们的 GitHub 用户名。这时，可能会出现一个问题：我们如何才能从经过身份验证的用户那里获取用户名和其他信息？

在我们的示例中，我们从登录属性中提取了用户名。但是，这在所有 OAuth2 提供者中并不相同。换句话说，提供者可以自行决定提供某些属性的数据。因此，我们可以说在这方面根本没有标准。

以 GitHub 为例，我们可以在[参考文档](https://docs.github.com/en/rest/reference/users#get-the-authenticated-user)中找到我们需要的属性。同样，其他 OAuth2 提供者也提供自己的参考。

另一种解决方案是，我们可以在调试模式下启动应用程序，并在创建OAuth2AuthenticatedPrincipal对象后设置断点。在遍历该对象的所有属性时，我们将深入了解用户的信息。

## 7. 测试

让我们编写一些测试来验证应用程序的行为。

### 7.1。设置环境

这是将保存我们的测试方法的类：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(properties = "spring.security.oauth2.client.registration.github.client-id:test-id")
public class JerseyResourceUnitTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String basePath;

    @Before
    public void setup() {
        basePath = "http://localhost:" + port + "/";
    }

    // test methods
}
```

我们没有使用真实的 GitHub 客户端 ID，而是为 OAuth2 客户端定义了一个测试 ID。然后将此 ID 设置为spring.security.oauth2.client.registration.github.client-id属性。

此测试类中的所有注解在Spring Boot测试中都很常见，因此我们不会在本教程中介绍它们。如果这些注解中的任何一个不清楚，请转到[Spring Boot](https://www.baeldung.com/spring-boot-testing)中的测试、Spring 中的[集成测试](https://www.baeldung.com/integration-testing-in-spring)或[探索Spring BootTestRestTemplate](https://www.baeldung.com/spring-boot-testresttemplate)。

### 7.2. 主页

我们将证明，当未经身份验证的用户尝试访问主页时，他们将被重定向到登录页面进行身份验证：

```java
@Test
public void whenUserIsUnauthenticated_thenTheyAreRedirectedToLoginPage() {
    ResponseEntity<Object> response = restTemplate.getForEntity(basePath, Object.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    assertThat(response.getBody()).isNull();

    URI redirectLocation = response.getHeaders().getLocation();
    assertThat(redirectLocation).isNotNull();
    assertThat(redirectLocation.toString()).isEqualTo(basePath + "login");
}
```

### 7.3. 登录页面

让我们验证访问登录页面将导致返回授权路径：

```java
@Test
public void whenUserAttemptsToLogin_thenAuthorizationPathIsReturned() {
    ResponseEntity response = restTemplate.getForEntity(basePath + "login", String.class);
    assertThat(response.getHeaders().getContentType()).isEqualTo(TEXT_HTML);
    assertThat(response.getBody()).isEqualTo("Log in with <a href=""/oauth2/authorization/github"">GitHub</a>");
}
```

### 7.4. 授权端点

最后，当向授权端点发送请求时，浏览器将使用适当的参数重定向到 OAuth2 提供者的授权页面：

```java
@Test
public void whenUserAccessesAuthorizationEndpoint_thenTheyAresRedirectedToProvider() {
    ResponseEntity response = restTemplate.getForEntity(basePath + "oauth2/authorization/github", String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    assertThat(response.getBody()).isNull();

    URI redirectLocation = response.getHeaders().getLocation();
    assertThat(redirectLocation).isNotNull();
    assertThat(redirectLocation.getHost()).isEqualTo("github.com");
    assertThat(redirectLocation.getPath()).isEqualTo("/login/oauth/authorize");

    String redirectionQuery = redirectLocation.getQuery();
    assertThat(redirectionQuery.contains("response_type=code"));
    assertThat(redirectionQuery.contains("client_id=test-id"));
    assertThat(redirectionQuery.contains("scope=read:user"));
}
```

## 8. 总结

在本教程中，我们使用 Jersey 应用程序设置了 Spring Social Login。本教程还包括向 GitHub OAuth2 提供程序注册应用程序的步骤。