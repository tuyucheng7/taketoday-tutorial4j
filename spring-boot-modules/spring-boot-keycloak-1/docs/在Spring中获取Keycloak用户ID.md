## 1. 概述

[Keycloak]()是一个开源身份和访问管理(IAM)系统，可以与Spring Boot应用程序很好地集成。在本教程中，我们将描述如何在Spring Boot应用程序中获取Keycloak用户ID。

## 2. 问题陈述

Keycloak提供了保护REST API、用户联合、细粒度授权、社交登录、双因素身份验证(2FA)等功能。此外，我们可以使用它来实现OpenID Connect([OIDC](https://openid.net/connect/))的[单点登录(SSO)]()。**假设我们有一个由OIDC使用Keycloak保护的Spring Boot应用程序，我们想在Spring Boot应用程序中获取一个用户ID，在这种情况下，我们需要在Spring Boot应用程序中获取访问令牌或安全上下文**。

### 2.1 Keycloak服务器作为授权服务器

为了简单起见，我们将使用[嵌入在Spring Boot]()应用程序中的Keycloak，假设我们正在使用[GitHub上]()可用的授权服务器项目。首先，我们将在嵌入式Keycloak服务器中的realm tuyucheng中定义customerClient客户端：

<img src="../assets/img_52.png">

然后，我们将realm详细信息导出为customer-realm.json并在我们的application-customer.yml中设置realm文件：

```yaml
keycloak:
    server:
        contextPath: /auth
        adminUser:
            username: tuyucheng-admin
            password: pass
        realmImportFile: customer-realm.json
```

最后，我们可以使用–spring.profiles.active=customer选项运行应用程序。现在，授权服务器已准备就绪。运行服务器后，我们可以在http://localhost:8083/auth访问授权服务器的欢迎页面。

### 2.2 资源服务器

现在我们已经配置了授权服务器，让我们设置资源服务器。为此，我们将使用[GitHub上提供]()的资源服务器项目。首先，让我们将application-embedded.properties文件添加为资源：

```properties
keycloak.auth-server-url=http://localhost:8083/auth
keycloak.realm=tuyucheng
keycloak.resource=customerClient
keycloak.public-client=true
keycloak.principal-attribute=preferred_username
```

现在，资源服务器使用OAuth2授权服务器是安全的，我们必须登录到SSO服务器才能访问资源，我们可以使用–spring.profiles.active=embedded选项运行应用程序。

## 3. 获取Keycloak用户ID

从Keycloak获取用户ID可以通过几种方式完成：使用访问令牌或客户端映射器。

### 3.1 通过访问令牌

在[Spring Boot应用程序]()CustomUserAttrController类的基础上，让我们修改getUserInfo()方法以获取用户ID：

```java
@GetMapping(path = "/users")
public String getUserInfo(Model model) {

    KeycloakAuthenticationToken authentication = (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

    Principal principal = (Principal) authentication.getPrincipal();

    String userIdByToken = "";

    if (principal instanceof KeycloakPrincipal) {
        KeycloakPrincipal<KeycloakSecurityContext> kPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) principal;
        IDToken token = kPrincipal.getKeycloakSecurityContext().getIdToken();
        userIdByToken = token.getSubject();
    }

    model.addAttribute("userIDByToken", userIdByToken);
    return "userInfo";
}
```

正如我们所看到的，首先我们从KeycloakAuthenticationToken类中获得了Principal，然后，我们使用getSubject()方法从IDToken中提取用户ID。

### 3.2 通过客户端映射器

我们可以在客户端映射器中添加一个用户ID，并在Spring Boot应用程序中获取它。首先，我们在customerClient客户端中定义一个客户端映射器：

<img src="../assets/img_53.png">

然后，我们在CustomUserAttrController类中获取用户ID：

```java
@GetMapping(path = "/users")
public String getUserInfo(Model model) {

    KeycloakAuthenticationToken authentication = (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

    Principal principal = (Principal) authentication.getPrincipal();

    String userIdByMapper = "";

    if (principal instanceof KeycloakPrincipal) {
        KeycloakPrincipal<KeycloakSecurityContext> kPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) principal;
        IDToken token = kPrincipal.getKeycloakSecurityContext().getIdToken();
        userIdByMapper = token.getOtherClaims().get("user_id").toString();
    }

    model.addAttribute("userIDByMapper", userIdByMapper);
    return "userInfo";
}
```

我们使用IDToken中的getOtherClaims()方法来获取映射器，然后，我们将用户ID添加到模型属性中。

### 3.3 Thymeleaf

我们将修改userInfo.html模板以显示用户ID信息：

```xml
<div id="container">
    <h1>
        User ID By Token: <span th:text="${userIDByToken}">--userID--</span>.
    </h1>
    <h1>
        User ID By Mapper: <span th:text="${userIDByMapper}">--userID--</span>.
    </h1>
</div>
```

### 3.4 测试

运行应用程序后，我们可以导航到http://localhost:8081/users，输入tuyucheng:tuyucheng作为凭据，将返回以下内容：

<img src="../assets/img_54.png">

## 4. 总结

在本文中，我们研究了在Spring Boot应用程序中从Keycloak获取用户ID，我们首先设置调用安全应用程序所需的环境，然后，我们描述了使用IDToken和客户端映射器在Spring Boot应用程序中获取Keycloak用户ID。

与往常一样，本教程的完整源代码可在[GitHub]()上获得，此外，授权服务器源代码[可在GitHub]()上获得。