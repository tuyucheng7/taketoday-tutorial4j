## 1. 概述

[OpenAPI](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md)是一种与语言无关且与平台无关的规范，可对REST API进行标准化。OpenAPI使用户可以轻松理解API，而无需深入研究代码。[Swagger-UI](https://swagger.io/tools/swagger-ui/)根据此OpenAPI规范生成可视化文档，帮助可视化和测试REST API。

在本教程中，让我们学习如何在[Spring Boot](https://www.baeldung.com/spring-boot)应用程序中使用[Springdoc-OpenAPI](https://springdoc.org/)为我们的OpenAPI生成OpenAPI文档、测试REST API和配置[JWT身份验证](https://jwt.io/)。

## 2. Swagger-UI

Swagger-UI是HTML、Javascript和CSS文件的集合，可生成基于OpenAPI规范的用户界面。**让我们使用Springdoc-OpenAPI库为REST API自动生成OpenAPI文档，并使用Swagger-UI来可视化这些API**。

当应用程序中的API数量不断增加时，编写OpenAPI文档规范可能会充满挑战。Springdoc-OpenAPI帮助我们自动生成OpenAPI文档，此外，让我们尝试使用这个库并生成OpenAPI文档。

### 2.1 依赖关系

首先，我们添加[Springdoc-OpenAPI](https://search.maven.org/search?q=springdoc-openapi-ui)依赖项：

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.6.9</version>
</dependency>
```

此依赖项还将Swagger-UI web-jars添加到我们的Spring Boot应用程序中。

### 2.2 配置

接下来，让我们启动应用程序并在浏览器中访问http://localhost:8080/swagger-ui.html。

结果，我们得到了Swagger-UI页面：

<img src="../assets/img_14.png">

**同样，OpenAPI v3.0文档将在**http://localhost:8080/v3/api-docs**上提供**。

此外，让我们使用以下[@OpenAPIDefinition](https://docs.swagger.io/swagger-core/v2.2.0/apidocs/io/swagger/v3/oas/annotations/OpenAPIDefinition.html)为我们的用户API添加描述、服务条款和其他元信息：

```java
@Configuration
@OpenAPIDefinition(
      info =@Info(
            title = "User API",
            version = "${api.version}",
            contact = @Contact(
                  name = "Tuyucheng", email = "user-apis@tuyucheng.com", url = "https://www.tuyucheng.com"
            ),
            license = @License(
                  name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"
            ),
            termsOfService = "${tos.uri}",
            description = "${api.description}"
      ),
      servers = @Server(
            url = "${api.server.url}",
            description = "Production"
      )
)
public class OpenAPISecurityConfiguration {}
```

此外，我们可以[外部化](https://www.baeldung.com/properties-with-spring)配置和元信息。例如，在application.properties或application.yaml文件中定义api.version、tos.uri和api.description。

### 2.3 测试

最后，让我们测试一下Swagger-UI并查看OpenAPI文档。

为此，请启动应用程序并打开Swagger-UI的网址http://localhost:8080/swagger-ui/index.html：

<img src="../assets/img_15.png">

同样，OpenAPI文档将在http://localhost:8080/v3/api-docs上提供：

```json
{
    "openapi": "3.0.1",
    "info": {
        "title": "User API",
        "description": "The User API is used to create, update, and delete users. Users can be created with or without an associated account. If an account is created, the user will be granted the <strong>ROLE_USER</strong> role. If an account is not created, the user will be granted the <b>ROLE_USER</b> role.",
        "termsOfService": "terms-of-service",
        "contact": {
            "name": "Tuyucheng",
            "url": "https://www.tuyucheng.com",
            "email": "user-apis@tuyucheng.com"
        },
        "license": {
            "name": "Apache 2.0",
            "url": "https://www.apache.org/licenses/LICENSE-2.0"
        },
        "version": "1.0.0"
    }
    // ...
}
```

## 3. JWT认证

Springdoc-OpenAPI基于我们的应用程序REST API生成文档，此外，可以使用[Springdoc-OpenAPI注解](https://www.baeldung.com/spring-rest-openapi-documentation)自定义此文档。

在本节中，我们学习如何为OpenAPI配置基于JWT的身份验证。我们可以为每个操作、类或全局级别的OpenAPI配置JWT身份验证。

### 3.1 每个操作的配置

首先，让我们声明仅针对特定操作的JWT身份验证，让我们定义这个配置：

```java
@Configuration
@SecurityScheme(
      name = "Bearer Authentication",
      type = SecuritySchemeType.HTTP,
      bearerFormat = "JWT",
      scheme = "bearer"
)
public class OpenAPI30Configuration {}
```

[@SecurityScheme](https://docs.swagger.io/swagger-core/v2.2.0/apidocs/io/swagger/v3/oas/annotations/security/SecurityScheme.html)注解将[securitySchemes](https://swagger.io/docs/specification/authentication/)添加到OneAPI规范的[组件](https://swagger.io/docs/specification/components/)部分，@SecurityScheme定义了我们的API可以使用的安全机制，**支持的安全方案包括APIKey、HTTP Authentication(Basic和Bearer)、OAuth2和OpenID Connect**。在本例中，让我们使用[HTTP Bearer Authentication](https://swagger.io/docs/specification/authentication/bearer-authentication/)作为我们的安全方案。

**对于基于HTTP Bearer token的身份验证，我们需要选择安全方案作为bearerAuth，承载格式为JWT**。

由于我们只想保护特定的操作，因此我们需要指定需要身份验证的操作。对于操作级别的身份验证，我们应该在操作上使用[@SecurityRequirement](https://docs.swagger.io/swagger-core/v2.1.1/apidocs/io/swagger/v3/oas/annotations/security/SecurityRequirement.html)注解：

```java
@Operation(summary = "Delete user details", description = "Delete user details. The operation deletes the details of the user that is " + "associated with the provided JWT token.")
@SecurityRequirement(name = "Bearer Authentication")
@DeleteMapping
public String deleteUser(Authentication authentication) {}
```

完成这些配置后，让我们重新部署应用程序并访问http://localhost:8080/swagger-ui.html：

<img src="../assets/img_16.png">

单击该<img src="../assets/img_17.png">图标会打开一个登录对话框，供用户提供访问令牌以调用该操作：

<img src="../assets/img_18.png">

对于此示例，可以通过向身份验证API提供john/password或jane/password来获取JWT令牌：

<img src="../assets/img_19.png">

获得JWT令牌后，我们可以将其传递到Value文本框中，然后单击Authorize按钮，然后单击Close按钮：

<img src="../assets/img_20.png">

有了JWT令牌，让我们调用deleteUser API：

<img src="../assets/img_21.png">

因此，我们看到该操作将提供一个JWT令牌，如图标<img src="../assets/img_22.png">所示，并且Swagger-UI将此令牌作为Authorization标头中的HTTP Bearer提供。最后，有了这个配置，我们可以成功调用受保护的deleteUser API。

到目前为止，我们就已经完成了操作级别的安全配置。同样，让我们检查一下OpenAPI JWT安全类和全局配置。

### 3.2 类级配置

同样，我们可以为一个类中的所有操作提供OpenAPI身份验证，在包含所有API的类上声明@SecurityRequirement注解，这样做将为该特定类中的所有API提供身份验证：

```java
@RequestMapping("/api/user")
@RestController
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User", description = "The User API. Contains all the operations that can be performed on a user.")
public class UserApi {}
```

因此，此配置可确保UserApi类中所有操作的安全性。因此，假设该类有两个操作，则Swagger-UI看起来像这样：

<img src="../assets/img_23.png">

### 3.3 全局配置

通常，我们更愿意为应用程序中的所有API保留OpenAPI身份验证，**对于这些情况，我们可以使用Spring @Bean注解在全局级别声明安全性**：

```java
@Configuration
public class OpenAPI30Configuration {
    @Bean
    publicOpenAPIcustomizeOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
              .addSecurityItem(new SecurityRequirement()
                    .addList(securitySchemeName))
              .components(new Components()
                    .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                          .name(securitySchemeName)
                          .type(SecurityScheme.Type.HTTP)
                          .scheme("bearer")
                          .bearerFormat("JWT")));
    }
}
```

通过此全局配置，Springdoc-OpenAPI将JWT身份验证配置到应用程序中的所有OpenAPI：

<img src="../assets/img_24.png">

让我们尝试调用GET API：

<img src="../assets/img_25.png">

最终，我们得到HTTP 401 Unauthorized，因此可以确保我们的API是安全的，因为我们没有提供JWT令牌。接下来，让我们提供JWT令牌并检查行为。

单击Authorize按钮并提供JWT令牌以调用操作，我们可以从Swagger控制台中可用的身份验证API获取bearer令牌：

<img src="../assets/img_26.png">

最后，配置JWT令牌后，让我们重新调用API：

<img src="../assets/img_27.png">

此时，使用正确的JWT令牌，我们可以成功调用受保护的API。

## 4. 总结

在本教程中，我们学习了如何为我们的OpenAPI配置JWT身份验证，Swagger-UI提供了一个工具来记录和测试基于OneAPI规范的REST API，Swaggerdoc-OpenAPI工具可帮助我们根据作为Spring Boot应用程序一部分的REST API生成此规范。