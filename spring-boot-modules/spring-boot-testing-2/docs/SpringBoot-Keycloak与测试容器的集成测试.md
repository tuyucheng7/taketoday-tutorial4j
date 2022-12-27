## 1. 简介

在验证应用程序是否正常工作时，集成测试至关重要。此外，**我们应该正确测试身份验证**，因为它是一个敏感部分。测试容器允许我们在测试阶段启动Docker容器，以针对实际的技术堆栈运行我们的测试。

在本文中，**我们将了解如何使用Testcontainers针对实际的**[Keycloak](https://www.baeldung.com/spring-boot-keycloak)**实例设置集成测试**。

## 2. 使用Keycloak设置Spring Security

我们需要设置[Spring Security](https://www.baeldung.com/security-spring)、Keycloak配置，最后是Testcontainer。

### 2.1 设置Spring Boot和Spring Security

让我们从设置安全性开始，这要归功于Spring Security，我们需要[spring-boot-starter-security](https://search.maven.org/search?q=a:spring-boot-starter-security)依赖项。所以，让我们将它添加到我们的pom中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

我们将使用spring-boot父pom，因此我们不需要指定在其依赖管理中指定的库的版本。

接下来，让我们创建一个简单的控制器来返回一个用户：

```java
@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("me")
    public UserDto getMe() {
        return new UserDto(1L, "janedoe", "Doe", "Jane", "jane.doe@tuyucheng.com");
    }
}
```

此时，**我们有一个安全控制器可以响应“/users/me”上的请求**，启动应用程序时，Spring Security会为用户“user”生成一个密码，该密码在应用程序日志中可见。

### 2.2 配置Keycloak

**启动本地Keycloak的最简单方法是使用**[Docker](https://www.baeldung.com/ops/docker-guide)，因此，让我们运行一个已经配置了管理员帐户的Keycloak容器：

```bash
docker run -p 8081:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:17.0.1 start-dev
```

接下来我们打开浏览器访问[http://localhost:8081](http://localhost:8081/)以访问Keycloak控制台：

<img src="../assets/img_1.png">

接下来，创建我们的Realm，我们称之为tuyucheng：

<img src="../assets/img_2.png">

我们需要添加一个客户端，我们将其命名为tuyucheng-api：

<img src="../assets/img_3.png">

最后，让我们使用“Users”菜单添加一个Jane Doe用户：

<img src="../assets/img_4.png">

现在我们已经创建了用户，我们必须为其分配一个密码。让我们选择s3cr3t并取消选中temporary按钮：

<img src="../assets/img_5.png">

**我们现在已经用一个tuyucheng-api客户端和一个Jane Doe用户设置了我们的Keycloak Realm**。

接下来我们将配置Spring以使用Keycloak作为身份提供程序。

### 2.3 将两者放在一起

首先，我们会将识别控制委托给Keycloak服务器。为此，我们将使用[spring-boot-starter-oauth2-resource-server](https://search.maven.org/search?q=a:spring-boot-starter-oauth2-resource-server)库，它将允许我们使用Keycloak服务器验证JWT令牌，因此，让我们将它添加到我们的pom中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

接下来我们继续配置Spring Security以添加[OAuth 2资源服务器支持](https://www.baeldung.com/spring-security-oauth-resource-server)：

```java
@Configuration
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true", matchIfMissing = true)
public class WebSecurityConfiguration {

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new NullAuthenticatedSessionStrategy();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf()
              .disable()
              .cors()
              .and()
              .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
              .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
              .build();
    }
}
```

我们设置了一个新的过滤器链，该链将应用于所有传入的请求，它将针对我们的Keycloak服务器验证绑定的JWT令牌。

由于我们正在构建具有仅承载身份验证的无状态应用程序，因此我们将**使用NullAuthenticatedSessionStrategy作为会话策略**。此外，@ConditionalOnProperty允许我们通过将keycloak.enabled属性设置为false来[禁用Keycloak配置](https://www.baeldung.com/spring-keycloak-security-disable)。

最后，让我们在application.properties文件中添加连接到Keycloak所需的配置：

```properties
keycloak.enabled=true
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8180/auth/realms/tuyucheng-api
```

**我们的应用程序现在是安全的，可以在每个请求上查询Keycloak以验证身份验证**。

## 3. 为Keycloak设置测试容器

### 3.1 导出Realm配置

Keycloak容器在没有任何配置的情况下启动，因此，我们必须**在容器启动时将其作为JSON文件导入**，让我们从当前运行的实例中导出这个文件：

<img src="../assets/img_6.png">

不幸的是，Keycloak不会通过管理界面导出用户，我们可以登录容器并使用kc.sh export命令。对于我们的示例，手动编辑生成的realm-export.json文件并将我们的Jane Doe添加到其中会更容易，让我们在最后一个花括号之前添加这个配置：

```json
"users": [
    {
        "username": "janedoe",
        "email": "jane.doe@tuyucheng.com",
        "firstName": "Jane",
        "lastName": "Doe",
        "enabled": true,
        "credentials": [
            {
                "type": "password",
                "value": "s3cr3t"
            }
        ],
        "clientRoles": {
            "account": [
                 "view-profile",
                 "manage-account"
            ]
        }
    }
]
```

最后我们将realm-export.json文件包含到我们项目的src/test/resources/keycloak文件夹中，我们将在Keycloak容器的启动期间使用它。

### 3.2 设置测试容器

让我们添加[testcontainers](https://search.maven.org/search?q=a:testcontainers)依赖项以及[testcontainers-keycloak](https://search.maven.org/artifact/com.github.dasniko/testcontainers-keycloak)，它允许我们启动一个Keycloak容器：

```xml
<dependency>
    <groupId>com.github.dasniko</groupId>
    <artifactId>testcontainers-keycloak</artifactId>
    <version>2.1.2</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.16.3</version>
</dependency>
```

接下来，让我们创建一个类，我们所有的测试都将从该类派生，我们用它来配置由Testcontainers启动的Keycloak容器：

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class KeycloakTestContainers {

    static {
        keycloak = new KeycloakContainer().withRealmImportFile("keycloak/realm-export.json");
        keycloak.start();
    }
}
```

静态声明和启动我们的容器将确保它在我们所有的测试中被实例化和启动一次，我们**使用KeycloakContainer对象的withRealmImportFile方法指定Realm的配置以在启动时导入**。

### 3.3 Spring Boot测试配置

Keycloak容器使用随机端口，因此，我们需要在启动后覆盖application.properties中定义的spring.security.oauth2.resourceserver.jwt.issuer-uri配置。为此，我们将使用方便的[@DynamicPropertySource](https://www.baeldung.com/spring-dynamicpropertysource)注解：

```java
@DynamicPropertySource
static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
    registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> keycloak.getAuthServerUrl() + "/realms/tuyucheng");
}
```

## 4. 创建集成测试

现在我们的主要测试类负责启动我们的Keycloak容器和配置Spring属性，让我们创建一个调用我们的UserController的[集成测试](https://www.baeldung.com/integration-testing-in-spring)。

### 4.1 获取访问令牌

首先，让我们在抽象类KeycloakTestContainers中添加一个方法，用于使用Jane Doe的凭据请求令牌：

```java
URI authorizationURI = new URIBuilder(keycloak.getAuthServerUrl() + "/realms/tuyucheng/protocol/openid-connect/token").build();
WebClient webclient = WebClient.builder().build();
MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
formData.put("grant_type", Collections.singletonList("password"));
formData.put("client_id", Collections.singletonList("tuyucheng-api"));
formData.put("username", Collections.singletonList("jane.doe@tuyucheng.com"));
formData.put("password", Collections.singletonList("s3cr3t"));

String result = webclient.post()
    .uri(authorizationURI)
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .body(BodyInserters.fromFormData(formData))
    .retrieve()
    .bodyToMono(String.class)
    .block();
```

在这里，我们使用Webflux的WebClient发布一个表单，其中包含获取访问令牌所需的不同参数。

最后，我们将**解析Keycloak服务器响应以从中提取令牌**。具体来说，我们生成一个包含Bearer关键字的经典身份验证字符串，后跟令牌的内容，准备在标头中使用：

```java
JacksonJsonParser jsonParser = new JacksonJsonParser();
return "Bearer " + jsonParser.parseMap(result)
    .get("access_token")
    .toString();
```

### 4.2 创建集成测试

让我们针对我们配置的Keycloak容器快速设置集成测试，我们将使用RestAssured和Hamcrest进行测试，首先添加[Rest Assured](https://search.maven.org/search?q=a:rest-assured)依赖项：

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <scope>test</scope>
</dependency>
```

现在，我们可以使用抽象KeycloakTestContainers类创建我们的测试：

```java
@Test
void givenAuthenticatedUser_whenGetMe_shouldReturnMyInfo() {

    given().header("Authorization", getJaneDoeBearer())
        .when()
        .get("/users/me")
        .then()
        .body("username", equalTo("janedoe"))
        .body("lastname", equalTo("Doe"))
        .body("firstname", equalTo("Jane"))
        .body("email", equalTo("jane.doe@tuyucheng.com"));
}
```

因此，**我们从Keycloak获取的访问令牌被添加到请求的Authorization标头中**。

## 5. 总结

在本文中，我们**针对由Testcontainers管理的实际Keycloak设置了集成测试**，在每次启动测试阶段时，我们都会导入一个Realm配置以拥有一个预配置的环境。