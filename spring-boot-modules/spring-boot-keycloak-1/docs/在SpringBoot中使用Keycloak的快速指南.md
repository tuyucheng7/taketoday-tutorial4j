## 1. 概述

在本教程中，我们将讨论设置Keycloak服务器以及使用Spring Security OAuth2.0将Spring Boot应用程序连接到它的基础知识。

## 2. 什么是Keycloak？

[Keycloak](http://www.keycloak.org/)**是一种针对现代应用程序和服务的开源身份和访问管理解决方案**。

Keycloak提供单点登录(SSO)、身份代理和社交登录、用户联合、客户端适配器、管理控制台和帐户管理控制台等功能。

在我们的教程中，我们将使用Keycloak的管理控制台使用Spring Security OAuth2.0设置和连接到Spring Boot。

## 3. 设置Keycloak服务器

在本节中，我们将设置和配置Keycloak服务器。

### 3.1 下载和安装Keycloak

有多种发行版可供选择，但是，在本教程中，我们将使用独立版本。

让我们从官方来源下载[Keycloak-19.0.1 Standalone服务器发行版](http://www.keycloak.org/downloads.html)。

**下载独立服务器发行版后，我们可以从终端解压缩并启动Keycloak**：

```bash
$ unzip keycloak-legacy-19.0.1.zip 
$ cd keycloak-legacy-19.0.1/keycloak-19.0.1/bin
$ ./standalone.sh -Djboss.socket.binding.port-offset=100
```

运行./standalone.sh后，Keycloak将启动其服务，一旦我们看到包含Keycloak 19.0.1 on JVM (powered by Quarkus 2.7.6.Final) started in 10.094类似的日志，我们就可以知道它的启动已经完成。

现在让我们打开浏览器并访问[http://localhost:8080](http://localhost:8180/)，**我们将被重定向到**http://localhost:8080/auth**以创建管理登录名**：


<img src="../assets/img.png">

让我们创建一个名为initial1的初始管理员用户，密码为zaq1!QAZ。单击Create后，我们将看到消息User Created。

现在，我们可以进入管理控制台。在登录页面上，我们将输入初始管理员用户凭据：

<img src="../assets/img_1.png">

### 3.2 创建Realm

登录成功后会将我们带到控制台并为我们打开默认的主Realm。

在这里，我们将重点介绍如何创建自定义Realm。

让我们**导航到左上角点击“Create Realm”按钮**：

<img src="../assets/img_2.png">

在下一个屏幕上，让我们**添加一个名为SpringBootKeycloak的新Realm**：

<img src="../assets/img_3.png">

单击Create按钮后，将创建一个新Realm，我们将被重定向到该Realm，下一节中的所有操作都将在这个新的SpringBootKeycloak Realm中执行。

### 3.3 创建客户端

现在我们将导航到“Clients”页面，正如我们在下图中所看到的，**Keycloak附带了已经内置的客户端**：

<img src="../assets/img_4.png">

我们仍然需要向我们的应用程序添加一个新客户端，因此我们单击Create client，我们**将新的客户端称为login-app**：

<img src="../assets/img_5.png">

在下一个屏幕中(点击Next)，出于本教程的目的，我们将**保留除Valid Redirect URIs字段之外的所有默认值，此字段应包含将使用此客户端进行身份验证的应用程序URL**：

<img src="../assets/img_6.png">

稍后，我们将创建一个在端口8081上运行的Spring Boot应用程序，该应用程序将使用此客户端。因此，我们在上面使用了[http://localhost:8081/*](http://localhost:8081/*)的重定向URL。

### 3.4 创建角色和用户

**Keycloak使用基于角色的访问；因此，每个用户都必须有一个角色**。

为此，我们需要导航到Realm roles页面：

<img src="../assets/img_7.png">

然后我们将添加user角色：

<img src="../assets/img_8.png">

现在我们有一个可以分配给用户的角色，但是由于还没有用户，因此**我们转到Users页面添加一个**：

<img src="../assets/img_9.png">

我们将添加一个名为user1的用户：

<img src="../assets/img_10.png">

创建用户后，将显示一个包含其详细信息的页面：

<img src="../assets/img_11.png">

我们现在可以转到“Credentials”选项卡，我们将初始密码设置为xsw2@WSX：

<img src="../assets/img_12.png">

最后，我们将导航到Role Mapping选项卡，我们将把user角色分配给我们的user1：

<img src="../assets/img_13.png">

## 4. 使用Keycloak的API生成访问令牌

Keycloak提供了一个用于生成和刷新访问令牌的REST API，我们可以很容易地使用这个API来创建我们自己的登录页面。

首先，我们需要通过向此URL发送POST请求来从Keycloak获取访问令牌：

```shell
http://localhost:8080/realms/SpringBootKeycloak/protocol/openid-connect/token
```

请求的正文应采用x-www-form-urlencoded格式：

```http request
client_id:<your_client_id>
username:<your_username>
password:<your_password>
grant_type:password
```

作为响应，我们将得到一个access_token和refresh_token。

访问令牌应该在对受Keycloak保护的资源的每个请求中使用，只需将其放在Authorization标头中即可：

```json
headers: {
    'Authorization': 'Bearer' + access_token
}
```

访问令牌过期后，我们可以通过向与上述相同的URL发送POST请求来刷新它，但包含刷新令牌而不是用户名和密码：

```json
{
    'client_id': 'your_client_id',
    'refresh_token': refresh_token_from_previous_request,
    'grant_type': 'refresh_token'
}
```

Keycloak将使用新的access_token和refresh_token对此做出响应。

## 5. 创建和配置Spring Boot应用程序

在本节中，我们将创建一个Spring Boot应用程序并将其配置为OAuth客户端以与Keycloak服务器进行交互。

### 5.1 依赖关系

**我们使用Spring Security OAuth2.0客户端连接到Keycloak服务器**。

首先我们在pom.xml的Spring Boot应用程序中声明[spring-boot-starter-oauth2-client](https://search.maven.org/search?q=a:spring-boot-starter-oauth2-client)依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

此外，由于我们需要将Spring Security与Spring Boot一起使用，因此我们必须添加此[依赖项](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-security")：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

现在，Spring Boot应用程序可以与Keycloak进行交互。

### 5.2 Keycloak配置

**我们将Keycloak客户端视为OAuth客户端，因此，我们需要配置Spring Boot应用程序以使用OAuth客户端**。

ClientRegistration类包含有关客户端的所有基本信息，Spring自动配置查找具有模式spring.security.oauth2.client.registration.\[registrationId\]的属性，并使用OAuth 2.0或[OpenID Connect (OIDC)]()注册客户端。

让我们配置客户端注册配置：

```properties
spring.security.oauth2.client.registration.keycloak.client-id=login-app
spring.security.oauth2.client.registration.keycloak.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.keycloak.scope=openid
```

**我们在client-id中指定的值与我们在管理控制台中命名的客户端匹配**。

**Spring Boot应用程序需要与OAuth 2.0或OIDC提供程序交互，以处理不同授权类型的实际请求逻辑。因此，我们需要配置OIDC提供程序**，它可以根据具有模式spring.security.oauth2.client.provider.\[provider name\]的属性值自动配置。

让我们配置OIDC提供程序配置：

```properties
spring.security.oauth2.client.provider.keycloak.issuer-uri=http://localhost:8080/realms/SpringBootKeycloak
spring.security.oauth2.client.provider.keycloak.user-name-attribute=preferred_username
```

我们记得，我们在端口8080上启动了Keycloak，因此在issuer-uri中指定了路径。此属性标识授权服务器的基本URI，我们输入我们在Keycloak管理控制台中创建的realm名。此外，我们可以将user-name-attribute定义为preferred_username以便使用适当的用户填充我们的控制器的Principal。

### 5.3 配置类

**我们通过创建一个SecurityFilterChain bean来配置HttpSecurity，此外，我们需要使用http.oauth2Login()启用OAuth2登录**。

让我们创建安全配置：

```java
@Configuration
@EnableWebSecurity
class SecurityConfig {

    private final KeycloakLogoutHandler keycloakLogoutHandler;

    SecurityConfig(KeycloakLogoutHandler keycloakLogoutHandler) {
        this.keycloakLogoutHandler = keycloakLogoutHandler;
    }

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
              .antMatchers("/customers*", "/users*")
              .hasRole("USER")
              .anyRequest()
              .permitAll();
        http.oauth2Login()
              .and()
              .logout()
              .addLogoutHandler(keycloakLogoutHandler)
              .logoutSuccessUrl("/");
        return http.build();
    }
}
```

在上面的代码中，**oauth2Login()方法将**[OAuth2LoginAuthenticationFilter](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/oauth2/client/web/OAuth2LoginAuthenticationFilter.html)**添加到过滤器链中，此过滤器拦截请求并应用OAuth 2身份验证所需的逻辑**。

我们在configure()方法中根据权限和角色配置访问权限，这些约束可确保对/customers/*的每个请求只有在请求它的人是具有角色USER的经过身份验证的用户时才会被授权。

最后，我们需要处理Keycloak的注销；为此，我们添加KeycloakLogoutHandler类：

```java
@Component
public class KeycloakLogoutHandler implements LogoutHandler {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakLogoutHandler.class);
    private final RestTemplate restTemplate;

    public KeycloakLogoutHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
        logoutFromKeycloak((OidcUser) auth.getPrincipal());
    }

    private void logoutFromKeycloak(OidcUser user) {
        String endSessionEndpoint = user.getIssuer() + "/protocol/openid-connect/logout";
        UriComponentsBuilder builder = UriComponentsBuilder
              .fromUriString(endSessionEndpoint)
              .queryParam("id_token_hint", user.getIdToken().getTokenValue());

        ResponseEntity<String> logoutResponse = restTemplate.getForEntity(builder.toUriString(), String.class);
        if (logoutResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Successfully logged out from Keycloak");
        } else {
            logger.error("Could not propagate logout to Keycloak");
        }
    }
}
```

KeycloakLogoutHandler类实现LogoutHandler类，并将注销请求发送到Keycloak。

现在，在我们进行身份验证后，我们将能够访问内部客户页面。

### 5.4 Thymeleaf网页

我们的网页使用Thymeleaf，其中包含三个页面：

-   external.html：面向公众的外部网页
-   customers.html：一个面向内部的页面，其访问仅限于具有user角色的经过身份验证的用户
-   layout.html：一个简单的布局，由两个片段组成，用于面向外部的页面和面向内部的页面

Thymeleaf模板的代码在[Github上]()可用，此处省略。

### 5.5 控制器

Web控制器将内部和外部URL映射到响应的Thymeleaf模板：

```java
@GetMapping(path = "/")
public String index() {
    return "external";
}
    
@GetMapping(path = "/customers")
public String customers(Principal principal, Model model) {
    addCustomers();
    model.addAttribute("customers", customerDAO.findAll());
    model.addAttribute("username", principal.getName());
    return "customers";
}
```

对于路径/customers，我们从Repository中检索所有客户并将结果作为属性添加到模型中。稍后，我们将遍历Thymeleaf中的结果。

为了能够显示用户名，我们也注入了Principal。

我们应该注意，我们在这里使用customer只是作为要显示的原始数据，仅此而已。

## 6. 演示

现在，我们已准备好测试我们的应用程序，要运行Spring Boot应用程序，我们可以通过IDE(如Spring Tool Suite(STS)或Intellij IDEA)轻松启动它，或者在终端中运行以下命令：

```shell
mvn clean spring-boot:run
```

在访问[http://localhost:8081](http://localhost:8081/)时，我们看到：

<img src="../assets/img_14.png">

现在我们点击customers链接进入内网，这是敏感信息所在的地方。

**请注意，我们已被重定向到通过Keycloak进行身份验证，以查看我们是否有权查看此内容**：

<img src="../assets/img_16.png">

一旦我们以user1(密码为xsw2@WSX)身份登录，Keycloak将验证我们是否拥有user角色的授权，我们将被重定向到受限客户页面：

<img src="../assets/img_15.png">

现在我们已经完成了连接Spring Boot和Keycloak的设置，并演示了它是如何工作的。

正如我们所看到的，**Spring Boot无缝地处理了调用Keycloak授权服务器的整个过程**，我们不必自己调用Keycloak API来生成访问令牌，甚至不必在我们对受保护资源的请求中显式发送授权标头。

接下来我们将回顾如何将Spring Security与我们现有的应用程序结合使用。

## 7. 总结

在本文中，我们配置了一个Keycloak服务器并将其与Spring Boot应用程序一起使用。

我们还学习了如何设置Spring Security并将其与Keycloak结合使用。