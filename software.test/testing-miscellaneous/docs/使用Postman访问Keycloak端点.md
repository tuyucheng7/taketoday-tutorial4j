## 一、简介

在本教程中，我们将从快速回顾 OAuth 2.0、OpenID 和 Keycloak 开始。然后我们将了解 Keycloak REST API 以及如何在 Postman 中调用它们。

## 2.OAuth 2.0

[OAuth 2.0](https://tools.ietf.org/html/rfc6749)是一个授权框架，允许经过身份验证的用户通过令牌向第三方授予访问权限。令牌通常仅限于某些生命周期有限的范围。因此，它是用户凭据的安全替代方案。

OAuth 2.0 包含四个主要组件：

-   **资源所有者**——拥有受保护资源或数据的最终用户或系统
-   **资源服务器**——服务公开受保护的资源，通常通过基于 HTTP 的 API
-   **客户端**——代表资源所有者调用受保护的资源
-   **授权服务器**——颁发一个 OAuth 2.0 令牌，并在对资源所有者进行身份验证后将其交付给客户端

OAuth 2.0 是一个[具有一些标准流程的协议](https://auth0.com/docs/protocols/protocol-oauth2)，但我们对这里的授权服务器组件特别感兴趣。

## 3. OpenID 连接

[OpenID Connect 1.0](https://openid.net/connect/) (OIDC) 建立在 OAuth 2.0 之上，为协议添加身份管理层。因此，它允许客户端通过标准 OAuth 2.0 流程验证最终用户的身份并访问基本配置文件信息。OIDC[向 OAuth 2.0 引入了一些标准范围](https://www.baeldung.com/spring-security-openid-connect)，例如*openid*、*profile*和*email*。

## 4. Keycloak 作为授权服务器

**JBoss 开发了[Keycloak](http://www.keycloak.org/)作为基于 Java 的开源身份和访问管理解决方案。**除了支持 OAuth 2.0 和 OIDC，它还提供身份代理、用户联合和 SSO 等功能。

我们可以将 Keycloak 用作[带有管理控制台的独立服务器](https://www.baeldung.com/spring-boot-keycloak)，或者[将其嵌入到 Spring 应用程序中](https://www.baeldung.com/keycloak-embedded-in-spring-boot-app)。一旦我们的 Keycloak 以这些方式中的任何一种运行，我们就可以尝试端点。

## 5. Keycloak端点

Keycloak 为 OAuth 2.0 流程公开了各种 REST 端点。

要将这些端点与[Postman](https://www.postman.com/)一起使用，我们将首先创建一个名为“ *Keycloak”的环境。*” 然后我们将为 Keycloak 授权服务器 URL、领域、OAuth 2.0 客户端 ID 和客户端密码添加一些键/值条目：

[![截图 2020-10-22-at-6.25.07-PM](https://www.baeldung.com/wp-content/uploads/2020/10/Screen-Shot-2020-10-22-at-6.25.07-PM-1024x497.png)](https://www.baeldung.com/wp-content/uploads/2020/10/Screen-Shot-2020-10-22-at-6.25.07-PM.png)

最后，我们将创建一个集合，我们可以在其中组织我们的 Keycloak 测试。现在我们准备探索可用的端点。

### 5.1. OpenID 配置端点

配置端点就像根目录。它返回所有其他[可用端点、支持的范围和声明以及签名算法](https://www.baeldung.com/spring-security-openid-connect)。

让我们在 Postman 中创建一个请求：***{{server}}\* /auth/realms/ \*{{realm}}\* /.well-known/openid-configuration。**Postman在运行时从所选环境设置*{{server}}*和*{{realm}}*的值：

[![邮递员端点openidconfig](https://www.baeldung.com/wp-content/uploads/2020/10/postman-endpoint-openidconfig-1024x246.png)](https://www.baeldung.com/wp-content/uploads/2020/10/postman-endpoint-openidconfig.png)

然后我们将执行请求，如果一切顺利，我们将得到响应：

```json
{
    "issuer": "http://localhost:8083/auth/realms/baeldung",
    "authorization_endpoint": "http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/auth",
    "token_endpoint": "http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/token",
    "token_introspection_endpoint": "http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/token/introspect",
    "userinfo_endpoint": "http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/userinfo",
    "end_session_endpoint": "http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/logout",
    "jwks_uri": "http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/certs",
    "check_session_iframe": "http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/login-status-iframe.html",
    "grant_types_supported": [...],
    ...
    "registration_endpoint": "http://localhost:8083/auth/realms/baeldung/clients-registrations/openid-connect",
    ...
    "introspection_endpoint": "http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/token/introspect"
}复制
```

如前所述，我们可以在响应中看到所有可用的端点，例如“ *authorization_endpoint* ”、“ *token_endpoint* ”等。

此外，响应中还有其他有用的属性。*例如，我们可以从“ grant_types_supported* ”中找出所有受支持的授权类型，或者从“scopes_supported”中找出所有受支持的范围*。*”

### 5.2. 授权端点

让我们继续我们的旅程，使用负责[OAuth 2.0 授权代码流的](https://auth0.com/docs/flows/authorization-code-flow)授权端点。它在 OpenID 配置响应中作为*“authorization_endpoint”*可用。

**端点是：**

***{{server}}\* /auth/realms/ \*{{realm}}\* /protocol/openid-connect/auth?response_type=code&client_id=jwtClient**

此外，此端点接受*scope*和*redirect_uri*作为可选参数。

我们不会在 Postman 中使用这个端点。相反，我们通常通过浏览器启动授权代码流。如果没有可用的活动登录 cookie，Keycloak 会将用户重定向到登录页面。最后，授权代码被传送到重定向 URL。

接下来我们将看到如何获取访问令牌。

### 5.3. 令牌端点

令牌端点允许我们检索访问令牌、刷新令牌或 ID 令牌。OAuth 2.0 支持不同的授权类型，例如*authorization_code*、*refresh_token*或*password。*

**令牌端点是：\*{{server}}\* /auth/realms/ \*{{realm}}\* /protocol/openid-connect/token**

但是，每种授权类型都需要一些专用的表单参数。

我们将首先测试我们的令牌端点以获得授权代码的访问令牌。我们必须在请求正文中传递这些表单参数：*client_id*、*client_secret*、*grant_type*、*code*和*redirect_uri*。令牌端点还接受*范围*作为可选参数：

[![邮递员令牌授权码](https://www.baeldung.com/wp-content/uploads/2020/10/postman-token-authcode-1024x383.png)](https://www.baeldung.com/wp-content/uploads/2020/10/postman-token-authcode.png)

如果我们想绕过授权代码流程，*密码*授予类型是我们的选择*。*在这里我们需要用户凭据，因此当我们的网站或应用程序上有内置登录页面时，我们可以使用此流程。*
*

 让我们创建一个 Postman 请求并在正文中传递表单参数*client_id*、*client_secret*、*grant_type*、*username*和*password ：*

[![邮局令牌密码](https://www.baeldung.com/wp-content/uploads/2020/10/postamn-token-password-1024x379.png)](https://www.baeldung.com/wp-content/uploads/2020/10/postamn-token-password.png)

**在执行此请求之前，我们必须将\*用户名\*和\*密码\*变量添加到 Postman 的环境键/值对中。**

另一个有用的授权类型是*refresh_token*。当我们从先前对令牌端点的调用中获得有效的刷新令牌时，我们可以使用它。刷新令牌流需要参数*client_id*、*client_secret*、*grant_type*和*refresh_token*。

我们需要响应*access_token*来测试其他端点。为了加快我们使用 Postman 的测试，我们可以在令牌端点请求的*测试部分编写一个脚本：*

```javascript
var jsonData = JSON.parse(responseBody);
postman.setEnvironmentVariable("refresh_token", jsonData.refresh_token);
postman.setEnvironmentVariable("access_token", jsonData.access_token);复制
```

[![邮递员测试脚本](https://www.baeldung.com/wp-content/uploads/2020/10/postman-test-script-1024x185.png)](https://www.baeldung.com/wp-content/uploads/2020/10/postman-test-script.png)

### 5.4. 用户信息端点

当我们拥有有效的访问令牌时，我们可以从用户信息端点检索用户配置文件数据。

**用户信息端点位于：\*{{server}}\* /auth/realms/ \*{{realm}}\* /protocol/openid-connect/userinfo**

*现在我们将为它创建一个 Postman 请求，并在Authorization*标头中传递访问令牌：

[![邮寄用户信息](https://www.baeldung.com/wp-content/uploads/2020/10/postamn-userinfo-1024x236.png)](https://www.baeldung.com/wp-content/uploads/2020/10/postamn-userinfo.png)

然后我们将执行请求。这是成功的响应：

```json
{
    "sub": "a5461470-33eb-4b2d-82d4-b0484e96ad7f",
    "preferred_username": "john@test.com",
    "DOB": "1984-07-01",
    "organization": "baeldung"
}复制
```

### 5.5. 令牌内省端点

如果资源服务器需要验证访问令牌是否处于活动状态或需要更多关于它的元数据，尤其是对于[不透明的访问令牌](https://auth0.com/docs/tokens/access-tokens)，那么令牌内省端点就是答案。在这种情况下，资源服务器将内省过程与[安全配置集成](https://www.baeldung.com/spring-security-oauth-resource-server)在一起。

**我们将调用 Keycloak 的内省端点：\*{{server}}\* /auth/realms/ \*{{realm}}\* /protocol/openid-connect/token/introspect**

然后我们将在 Postman 中创建一个内省请求，并将*client_id*、*client_secret*和*token*作为表单参数传递：

[![邮递员反省](https://www.baeldung.com/wp-content/uploads/2020/10/postman-introspect-1024x314.png)](https://www.baeldung.com/wp-content/uploads/2020/10/postman-introspect.png)

如果*access_token*有效，那么我们将得到我们的回应：

```json
{
    "exp": 1601824811,
    "iat": 1601824511,
    "jti": "d5a4831d-7236-4686-a17b-784cd8b5805d",
    "iss": "http://localhost:8083/auth/realms/baeldung",
    "sub": "a5461470-33eb-4b2d-82d4-b0484e96ad7f",
    "typ": "Bearer",
    "azp": "jwtClient",
    "session_state": "96030af2-1e48-4243-ba0b-dd4980c6e8fd",
    "preferred_username": "john@test.com",
    "email_verified": false,
    "acr": "1",
    "scope": "profile email read",
    "DOB": "1984-07-01",
    "organization": "baeldung",
    "client_id": "jwtClient",
    "username": "john@test.com",
    "active": true
}复制
```

但是，如果我们使用无效的访问令牌，则响应将是：

```json
{
    "active": false
}复制
```

## 六，结论

在本文中，使用运行中的 Keycloak 服务器，我们为授权、令牌、用户信息和内省端点创建了 Postman 请求。