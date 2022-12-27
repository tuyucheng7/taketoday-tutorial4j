## 1. 概述

在本教程中，我们将重点介绍如何测试受保护并使用Keycloak通过Swagger UI进行身份验证和授权的REST服务。

## 2. 挑战

与其他Web资源一样，REST API通常是安全的。因此，服务消费者(例如Swagger UI)不仅需要自己处理HTTP调用，还需要向服务提供者提供身份验证信息。

Keycloak是一个IAM服务器，它允许在服务提供商实现之外进行身份验证和授权。它是架构的一部分，如下图所示：

<img src="../assets/img.png">

正如我们所看到的，服务提供者和服务消费者都需要联系Keycloak服务器。首先，我们需要安装一个Keycloak服务器并将其作为REST服务提供者[集成到Spring Boot应用程序中](https://www.baeldung.com/spring-boot-keycloak)。然后，我们需要扩展Swagger UI。

## 3. 自定义Swagger UI

我们可以通过在HTML中包含下面这样的脚本来直接扩展Swagger UI：

```html
<script src="keycloak/keycloak.js"></script>
<script>
    var keycloak = Keycloak('keycloak.json');
    keycloak.init({onLoad: 'login-required'})
            .success(function (authenticated) {
                console.log('Login Successful');
                window.authorizations.add("oauth2", new ApiKeyAuthorization("Authorization", "Bearer " + keycloak.token, "header"));
            }).error(function () {
                console.error('Login Failed');
                window.location.reload();
            }
    );
</script>
```

该脚本作为[NPM包](https://www.npmjs.com/package/keycloak-js)提供，因此可以fork [Swagger UI源代码仓库](https://github.com/swagger-api/swagger-ui)并通过相应的依赖项扩展项目。

## 4. 使用标准

通过特定于供应商的代码扩展Swagger UI仅适用于非常特殊的情况。因此，我**们应该更喜欢使用独立于供应商的标准**，以下部分将描述如何实现这一点。

### 4.1 现有标准

首先，我们需要知道存在哪些标准。对于身份验证和授权，有一个类似于[OAuth2](https://oauth.net/2/)这样的协议。对于SSO，我们可以使用[OpenID Connect](https://openid.net/connect/)(OIDC)作为[OAuth2的扩展](https://developer.okta.com/blog/2019/10/21/illustrated-guide-to-oauth-and-oidc)。

描述REST API的标准是[OpenAPI](https://www.openapis.org/)，该标准包括定义多个[安全方案](https://swagger.io/docs/specification/authentication)，包括OAuth2和OIDC：

```yaml
paths:
    /api/v1/products:
        get:
            # ...
            security:
                  - my_oAuth_security_schema:
                      - read_access
...
securitySchemes:
    my_oAuth_security_schema:
        type: oauth2
        flows:
            implicit:
                authorizationUrl: https://api.example.com/oauth2/authorize
                scopes:
                    read_access: read data
                    write_access: modify data
```

### 4.2 扩展服务提供商

在代码优先的方法中，服务提供商可以根据代码生成OpenAPI文档。因此，安全方案也必须以这种方式提供。例如，使用包含SpringFox的Spring Boot，我们可以编写这样一个配置类：

```java
@Configuration
public class OpenAPISecurityConfig {

    @Autowired
    void addSecurity(Docket docket) {
        docket
              .securitySchemes(of(authenticationScheme()))
              .securityContexts(of(securityContext()));
    }

    private SecurityScheme authenticationScheme() {
        return new OAuth2SchemeBuilder("implicit")
              .name("my_oAuth_security_schema")
              .authorizationUrl("https://api.example.com/oauth2/authorize")
              .scopes(authorizationScopes())
              .build();
    }

    private List<AuthorizationScope> authorizationScopes() {
        return Arrays.asList(
              new AuthorizationScope("read_access", "read data"),
              new AuthorizationScope("write_access", "modify data")
        );
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
              .securityReferences(readAccessAuth())
              .operationSelector(operationContext ->
                    HttpMethod.GET.equals(operationContext.httpMethod())
              )
              .build();
    }

    private List<SecurityReference> readAccessAuth() {
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[] { authorizationScopes().get(0) };
        return of(new SecurityReference("my_oAuth_security_schema", authorizationScopes));
    }
}
```

当然，使用其他技术会导致不同的实现，但是我们应该始终注意必须生成的OpenAPI。

### 4.3 扩展服务消费者

Swagger UI默认支持OpenAPI身份验证方案，无需自定义它。然后，我们将有可能进行身份验证：

<img src="../assets/img_1.png">

其他客户端会有不同的解决方案。例如，有一个用于Angular应用程序的[NPM模块](https://github.com/manfredsteyer/angular-oauth2-oidc)，它以直接的方式提供OAuth2和OpenID Connect(OIDC)。

### 4.4 Swagger UI限制

Swagger UI从3.38.0版本开始支持OpenID Connect Discovery(从3.14.8版本开始支持Swagger Editor)，不幸的是，SpringFox在当前的3.0.0版本中封装了一个Swagger UI 3.26.2。因此，**如果我们想要包含更新版本的Swagger UI，我们需要使用与SpringFox相同的目录结构将其直接包含在我们的应用程序中**，以掩盖SpringFox打包的文件：

<img src="../assets/img_2.png">

相反，[SpringDoc 1.6.1](https://search.maven.org/artifact/org.springdoc/springdoc-openapi-ui/1.6.1/jar)没有打包Swagger UI，而是声明了对[Swagger UI 4.1.3](https://search.maven.org/artifact/org.webjars/swagger-ui/4.1.3/jar)的传递依赖性，因此我们不会在SpringDoc上遇到任何问题。

## 5. 总结

在本文中，我们指出了在使用Keycloak作为IAM的情况下使用Swagger UI测试REST服务的可能性，最好的解决方案是使用OpenAPI、OAuth2 和 OpenID Connect等标准，这些标准都受到工具的支持。