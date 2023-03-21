## 1. 概述

安全性是Spring生态系统中的一等公民。因此，OAuth2几乎无需配置即可与Spring Web MVC配合使用也就不足为奇了。

但是，原生Spring解决方案并不是实现表示层的唯一方法。[Jersey](https://eclipse-ee4j.github.io/jersey/)是一个符合JAX-RS的实现，也可以与Spring OAuth2协同工作。

在本教程中，**我们将了解如何使用Spring社交登录保护Jersey应用程序**，该应用程序是使用OAuth2标准实现的。

## 2. Maven依赖

让我们添加[spring-boot-starter-jersey](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-jersey/3.0.4)工件以将Jersey集成到Spring Boot应用程序中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jersey</artifactId>
</dependency>
```

要配置安全OAuth2，我们需要[spring-boot-starter-security](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-security/3.0.4)和[spring-security-oauth2-client](https://central.sonatype.com/artifact/org.springframework.security/spring-security-oauth2-client/6.0.2)：

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

我们将使用[Spring Boot Starter Parent版本2](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-dependencies/3.0.4)管理所有这些依赖项。

## 3. Jersey表示层

我们需要一个具有几个端点的资源类来使用Jersey作为表示层。

### 3.1 资源类

这是包含端点定义的类：

```java
@Path("/")
public class JerseyResource {
    // endpoint definitions
}
```

类本身非常简单-它只有一个@Path注解。此注解的值**标识类主体中所有端点的基本路径**。

值得一提的是，该资源类不带有用于组件扫描的构造型注解。事实上，它甚至不需要是Spring bean。原因是我们不依赖Spring来处理请求映射。

### 3.2 登录页面

下面是处理登录请求的方法：

```java
@GET
@Path("login")
@Produces(MediaType.TEXT_HTML)
public String login() {
    return "Log in with <a href=\"/oauth2/authorization/github\">GitHub</a>";
}
```

此方法为以/login端点为目标的GET请求返回一个字符串。text/html内容类型指示用户的浏览器显示带有可点击链接的响应。

**我们将使用GitHub作为OAuth2提供者，因此使用链接/oauth2/authorization/github**。此链接将触发重定向到GitHub授权页面。

### 3.3 主页

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

该方法返回主页，这是一个包含登录用户名的字符串。请注意，在这种情况下，**我们从login属性中提取了用户名**。不过，另一个OAuth2提供者可能会为用户名使用不同的属性。

显然，上述方法仅适用于经过身份验证的请求。**如果请求未经身份验证，则会将其重定向到login端点**。我们将在第4节中看到如何配置此重定向。

### 3.4 使用Spring容器注册Jersey

**让我们使用Servlet容器注册资源类以启用Jersey服务**。幸运的是，这很简单：

```java
@Component
public class RestConfig extends ResourceConfig {
    public RestConfig() {
        register(JerseyResource.class);
    }
}
```

通过在ResourceConfig子类中注册JerseyResource，我们通知了Servlet容器该资源类中的所有端点。

最后一步是**向Spring容器注册ResourceConfig子类，在本例中为RestConfig**。我们使用@Component注解实现了这个注册。

## 4. 配置Spring Security

我们可以像配置普通Spring应用程序一样为Jersey配置安全性：

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

给定链中最重要的方法是oauth2Login。此方法**使用OAuth2.0提供程序配置身份验证支持**。在本教程中，提供者是GitHub。

另一个值得注意的配置是登录页面。通过向loginPage方法提供字符串“/login”，我们告诉Spring**将未经身份验证的请求重定向到/login端点**。

请注意，默认安全配置还会在/login处提供一个自动生成的页面。因此，即使我们没有配置登录页面，未经身份验证的请求仍然会被重定向到该端点。

默认配置和显式设置的区别在于，在默认情况下，应用程序返回的是生成的页面，而不是我们自定义的字符串。

## 5. 应用配置

为了拥有受OAuth2保护的应用程序，我们需要向OAuth2提供者注册客户端。之后，将客户端的凭据添加到应用程序中。

### 5.1 注册OAuth2客户端

让我们通过[注册一个GitHub应用程序](https://github.com/settings/developers)开始注册过程。登录GitHub开发者页面后，点击New OAuth App按钮打开Register a new OAuth application表单。

接下来，使用适当的值填写显示的表单。对于应用程序名称，输入任何使应用程序可识别的字符串。主页URL可以是[http://localhost:8083](http://localhost:8083)，授权回调URL可以是[http://localhost:8083/login/oauth2/code/github](http://localhost:8083/login/oauth2/code/github)。

回调URL是用户通过GitHub进行身份验证并授予应用程序访问权限后浏览器重定向到的路径。

这是注册表单的样子：

<img src="../assets/img_3.png">

现在，单击Register application按钮。**然后浏览器应重定向到GitHub应用程序的主页，其中会显示Client-ID和Client-secrets**。

### 5.2 配置Spring Boot应用程序

让我们在类路径中添加一个名为jersey-application.properties的属性文件：

```properties
server.port=8083
spring.security.oauth2.client.registration.github.client-id=<your-client-id>
spring.security.oauth2.client.registration.github.client-secret=<your-client-secret>
```

**请记住将占位符<your-client-id\>和<your-client-secret\>替换为我们自己的GitHub应用程序中的值**。

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

## 6. 身份验证的实际应用

让我们看看在GitHub上注册后如何登录我们的应用程序。

### 6.1 访问应用程序

让我们启动应用程序，然后访问地址为localhost:8083的主页。由于请求未经身份验证，我们将被重定向到登录页面：

<img src="../assets/img_4.png">

现在，当我们点击GitHub链接时，浏览器将重定向到GitHub授权页面：

<img src="../assets/img_5.png">

通过查看URL，我们可以看到重定向的请求携带了许多查询参数，例如response_type、client_id和scope：

```text
https://github.com/login/oauth/authorize?response_type=code&client_id=10f7e7dcf3b782fd5ced&scope=read:user&state=QGwByiatw1RZTTFhaopeQbzj55HWcQaIqvUle4TLSzs%3D&redirect_uri=http://localhost:8083/login/oauth2/code/github
```

response_type的值为code，表示OAuth2授权类型为授权码。同时，client_id参数有助于识别我们的应用程序。关于所有参数的含义，请前往[GitHub开发者页面](https://docs.github.com/en/developers/apps/authorizing-oauth-apps#parameters)。

当授权页面出现时，我们需要授权应用程序继续。授权成功后，浏览器将重定向到我们应用程序中预定义的端点，以及一些查询参数：

```text
http://localhost:8083/login/oauth2/code/github?code=561d99681feeb5d2edd7&state=dpTme3pB87wA7AZ--XfVRWSkuHD3WIc9Pvn17yeqw38%3D
```

在幕后，应用程序将用授权码交换访问令牌。之后，它使用此令牌获取有关已登录用户的信息。

对localhost:8083/login/oauth2/code/github的请求返回后，浏览器会返回主页。这一次，**我们应该看到一条带有我们自己用户名的问候消息**：

<img src="../assets/img_6.png">

### 6.2 如何获取用户名？

很明显，问候消息中的用户名就是我们的GitHub用户名。此时，可能会出现一个问题：我们如何从经过身份验证的用户那里获取用户名和其他信息？

在我们的示例中，我们从login属性中提取了用户名。但是，这在所有OAuth2提供者中并不相同。换句话说，**提供者可以自行决定提供某些属性的数据**。因此，我们可以说在这方面根本没有标准。

以GitHub为例，我们可以在[参考文档](https://docs.github.com/en/rest/reference/users#get-the-authenticated-user)中找到我们需要的属性。同样，其他OAuth2提供者也提供了自己的参考。

另一种解决方案是，**我们可以在调试模式下启动应用程序，并在创建OAuth2AuthenticatedPrincipal对象后设置断点**。在遍历该对象的所有属性时，我们可以深入了解用户的信息。

## 7. 测试

让我们编写一些测试来验证应用程序的行为。

### 7.1 设置环境

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

我们没有使用真实的GitHub客户端ID，而是**为OAuth2客户端定义了一个测试ID**。然后将此ID设置为spring.security.oauth2.client.registration.github.client-id属性。

此测试类中的所有注解在Spring Boot测试中都很常见，因此我们不会在本教程中介绍它们。如果你对这些注解中的任何一个不熟悉，请转到[Spring Boot中的测试](https://www.baeldung.com/spring-boot-testing)、[Spring中的集成测试](https://www.baeldung.com/integration-testing-in-spring)或[探索Spring Boot TestRestTemplate](https://www.baeldung.com/spring-boot-testresttemplate)。

### 7.2 主页

我们将证明，当未经身份验证的用户尝试访问主页时，**他们将被重定向到登录页面进行身份验证**：

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

### 7.3 登录页面

让我们验证**访问登录页面是否会导致返回授权路径**：

```java
@Test
public void whenUserAttemptsToLogin_thenAuthorizationPathIsReturned() {
    ResponseEntity response = restTemplate.getForEntity(basePath + "login", String.class);
    assertThat(response.getHeaders().getContentType()).isEqualTo(TEXT_HTML);
    assertThat(response.getBody()).isEqualTo("Log in with <a href="\"/oauth2/authorization/github\"">GitHub</a>");
}
```

### 7.4 授权端点

最后，当向授权端点发送请求时，**浏览器将使用适当的参数重定向到OAuth2提供者的授权页面**：

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

在本教程中，我们使用Jersey应用程序设置了Spring社交登录。本教程还包括向GitHub OAuth2提供者注册应用程序的步骤。