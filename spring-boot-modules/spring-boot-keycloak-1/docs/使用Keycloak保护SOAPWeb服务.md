## 1. 概述

[Keycloak](https://www.keycloak.org/)是一种开源身份和访问管理服务器，可保护我们的现代应用程序(如SPA、移动应用程序、API等)。**Keycloak支持行业标准协议，如安全断言标记语言(SAML)2.0、单点登录(SSO)和OpenID Connect(OIDC)**。

此外，在本教程中，我们将学习如何利用Keycloak使用[OIDC](https://openid.net/connect/)(OpenID Connect)对SOAP Web服务进行身份验证和授权。

## 2. 开发SOAP Web服务

简而言之，让我们学习如何[使用Spring Boot构建SOAP Web服务]()。

### 2.1 Web服务操作

让我们直接定义操作：

-   **getProductDetails**：返回给定产品ID的产品详细信息，此外，我们假设具有user角色的用户可以请求此操作。
-   **deleteProduct**：删除给定产品ID的产品，此外，只有具有admin权限的用户才能请求此操作。

我们定义了两个操作和一个[RBAC(基于角色的访问控制)]()。

### 2.2 定义XSD

最重要的是，让我们定义一个product.xsd：

```xml
<xs:element name="getProductDetailsRequest">
    ...
</xs:element>
<xs:element name="deleteProductRequest">
    ...
</xs:element>
    ...
</xs:schema>
```

此外，让我们添加[wsdl4j](https://search.maven.org/search?q=g:wsdl4j a:wsdl4j)和[Spring Boot Webservices](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-web-services)依赖项：

```xml
<dependency>
    <groupId>wsdl4j</groupId>
    <artifactId>wsdl4j</artifactId>
    <version>1.6.3</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web-services</artifactId>
    <version>2.5.4</version>
</dependency>
```

### 2.3 Web服务

此外，让我们开发一个SOAP Web服务。

```java
@PayloadRoot(namespace = "http://www.tuyucheng.com/springbootsoap/keycloak", localPart = "getProductDetailsRequest")
@ResponsePayload
public GetProductDetailsResponse getProductDetails(@RequestPayload GetProductDetailsRequest request) {
    // ...
}

@PayloadRoot(namespace = "http://www.tuyucheng.com/springbootsoap/keycloak", localPart = "deleteProductRequest")
@ResponsePayload
public DeleteProductResponse deleteProduct(@RequestPayload DeleteProductRequest request) {
    // ...
}
```

我们可以使用[cURL](https://man7.org/linux/man-pages/man1/curl.1.html)、[Postman](https://www.postman.com/)、[SOAPUI](https://www.soapui.org/)等多种工具测试此Web服务。接下来，让我们看看如何保护我们的SOAP Web服务。

## 3. 配置Keycloak

首先，让我们[配置Keycloak]()以使用OpenId Connect保护我们的Web服务。

### 3.1 创建客户端

**通常，Client是需要Keycloaks的身份验证服务的应用程序**。此外，在创建客户端时，选择：

-   应用程序URL作为根URL
-   openid-connect作为客户端协议
-   Confidential作为访问类型
-   打开启用授权和启用服务帐户

此外，启用服务帐户允许我们的应用程序(客户端)使用Keycloak进行身份验证，随后，它向我们的身份验证流程提供[客户端凭证授权](https://oauth.net/2/grant-types/client-credentials/)类型流程：

<img src="../assets/img_43.png">

最后，单击Save，然后单击Credentials选项卡并记下Client secret。因此，我们需要它作为Spring Boot配置的一部分。

### 3.2 用户和角色

接下来，让我们创建两个用户并为他们分配角色和密码：

<img src="../assets/img_44.png">

首先，让我们创建角色，管理员和用户。Keycloak 允许我们创建两种角色-[Realm角色](https://www.keycloak.org/docs/latest/server_admin/#realm-roles)和[客户端角色](https://www.keycloak.org/docs/latest/server_admin/#client-roles)。但是，首先让我们创建Client Roles。

单击Clients，选择tuyucheng-soap-services客户端并单击Roles选项卡。然后，创建两个角色，admin和user：

<img src="../assets/img_45.png">

**虽然Keycloak可以从LDAP或AD(Active Directory)中获取用户，但为了简单起见，让我们手动配置用户并为他们分配角色**。

让我们创建两个用户。首先，我们点击Users，然后点击Add user：

<img src="../assets/img_46.png">

现在，让我们为用户分配角色。

再次单击Users选择用户并单击Edit，然后单击Role Mappings选项卡，从Client Roles中选择客户端，然后在Available Roles中选择一个角色。让我们将管理员角色分配给一个用户，将用户角色分配给另一个用户：

<img src="../assets/img_47.png">

<img src="../assets/img_48.png">

## 4. Spring Boot配置

同样，让我们保护我们的SOAP Web服务。

### 4.1 Keycloak–Spring Boot集成

首先，让我们添加Keycloak依赖项。

**Keycloak提供了一个适配器，它利用Spring Boot的自动配置并使集成变得容易**。现在，让我们更新我们的依赖项以包含这个[Keycloak适配器](https://search.maven.org/search?q=g:org.keycloak a:keycloak-spring-boot-starter)：

```xml
<dependency>
    <groupId>org.keycloak</groupId>
    <artifactId>keycloak-spring-boot-starter</artifactId>
    <version>15.0.2</version>			
</dependency>
```

接下来，让我们将Keycloak配置添加到我们的application.properties中：

```properties
keycloak.enabled=true
keycloak.realm=tuyucheng-soap-services
keycloak.auth-server-url=http://localhost:8080
keycloak.bearer-only=true
keycloak.credentials.secret=VI4IWImf5660vRg3d5RwSL8rSNVdnPHA
keycloak.ssl-required=external
keycloak.resource=tuyucheng-soap-services
keycloak.use-resource-role-mappings=true
```

来自[Keycloak文档](https://www.keycloak.org/docs/latest/securing_apps/#_java_adapter_config)：

-   **keycloak.enabled**：允许通过配置启用Keycloak Spring Boot适配器，默认值为true
-   **keycloak.realm**：Keycloak Realm名称，并且是强制性的
-   **keycloak.auth-server-url**：基本URL是必需的，它是Keycloak服务器的。通常，这是http(s)://host:port的形式
-   **keycloak.bearer-only**：虽然默认值为false，但请将其设置为true以便适配器验证令牌。
-   **keycloak.credentials.secret**：在Keycloak中配置的强制客户端密码
-   **keycloak.ssl-required**：默认值为external，换句话说，所有外部请求(除了localhost)都应该在https协议上
-   **keycloak.resource**：应用程序的强制客户端ID
-   **keycloak.use-resource-role-mappings**：虽然默认值为false，但请将其设置为true以便适配器在令牌内部查看用户的应用程序级角色映射

### 4.2 启用全局方法安全

除了前面的配置之外，我们还需要指定安全约束来保护我们的Web服务，这些约束使我们能够限制未经授权的访问。例如，我们应该限制user执行admin操作。

有两种设置约束的方法：

1.  在应用程序配置文件中声明安全约束和安全集合。
2.  [使用@EnableGlobalMethodSecurity](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/method/configuration/EnableGlobalMethodSecurity.html)的方法级安全性。

对于SOAP Web服务，安全约束在提供细粒度控制方面存在不足。此外，声明这些约束是冗长的。

**在本文的后面，让我们利用@EnableGlobalMethodSecurity的强大功能来保护我们的SOAP Web服务操作**。

### 4.3 定义一个KeycloakWebSecurityConfigurerAdapter

[KeycloakWebSecurityConfigurerAdapter](https://www.javadoc.io/static/org.keycloak/keycloak-spring-security-adapter/15.0.2/org/keycloak/adapters/springsecurity/config/KeycloakWebSecurityConfigurerAdapter.html)**是一个可选的便捷类，它扩展了WebSecurityConfigurerAdapter并简化了安全上下文配置**。此外，让我们定义一个类KeycloakSecurityConfig，它扩展了这个适配器并利用了@EnableGlobalMethodSecurity。

描述此层次结构的类图：

<img src="../assets/img_49.png">

现在，让我们配置KeycloakSecurityConfig类：

```java
@KeycloakConfiguration
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class KeycloakSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.csrf()
              .disable()
              .authorizeRequests()
              .anyRequest()
              .permitAll();
    }
}
```

[@KeycloakConfiguration](https://www.javadoc.io/static/org.keycloak/keycloak-spring-security-adapter/15.0.2/org/keycloak/adapters/springsecurity/KeycloakConfiguration.html)**定义了Keycloak与Spring Boot集成所需的所有注解**：

<img src="../assets/img_50.png">

### 4.4 添加授权

最后，让我们使用[@RolesAllowed](https://javaee.github.io/javaee-spec/javadocs/javax/annotation/security/RolesAllowed.html)注解([JSR-250](https://download.oracle.com/otndocs/jcp/common_annotations-1_3-mrel3-eval-spec/)的一部分)来授权我们的SOAP Web服务操作。

**鉴于此，让我们使用访问角色配置我们的方法。为此，我们使用@RolesAllowed注解**。回想一下，我们在Keycloak中定义了两个不同的角色，user和admin。让我们为每个Web服务定义一个角色：

```java
@RolesAllowed("user")
@PayloadRoot(namespace = "http://www.tuyucheng.com/springbootsoap/keycloak", localPart = "getProductDetailsRequest")
@ResponsePayload
public GetProductDetailsResponse getProductDetails(@RequestPayload GetProductDetailsRequest request) {
    // ...
}

@RolesAllowed("admin")
@PayloadRoot(namespace = "http://www.tuyucheng.com/springbootsoap/keycloak", localPart = "deleteProductRequest")
@ResponsePayload
public DeleteProductResponse deleteProduct(@RequestPayload DeleteProductRequest request) {
    // ...
}
```

这样，我们就完成了配置。

## 5. 测试应用程序

### 5.1 检查设置

现在应用程序已经准备就绪，让我们开始使用curl测试我们的SOAP Web服务：

```shell
curl -d @request.xml -i -o -X POST --header 'Content-Type: text/xml' http://localhost:18080/ws/api/v1
```

最终，如果所有配置都正确，我们会收到拒绝访问的响应：

```xml
<SOAP-ENV:Fault>
    <faultcode>SOAP-ENV:Server</faultcode>
    <faultstring xml:lang="en">Access is denied</faultstring>
</SOAP-ENV:Fault>

```

正如预期的那样，Keycloak拒绝了请求，因为请求不包含访问令牌。

### 5.2 获取访问令牌

此时，让我们从Keycloak获取一个访问令牌以访问我们的SOAP Web服务。通常，流程涉及：

<img src="../assets/img_51.png">

-   首先，用户将他的凭据发送到应用程序
-   应用程序将client-id和client-secret连同这些凭证传递给Keycloak服务器
-   最后，Keycloak根据用户凭证和角色返回访问令牌、刷新令牌和其他元数据

Keycloak为客户端公开令牌端点以请求访问令牌，通常，此端点的形式为：

<PROTOCOL>://<HOST>:<PORT>/auth/realms/<REALM_NAME>/protocol/openid-connect/token

例如：

http://localhost:8080/auth/realms/tuyucheng/protocol/openid-connect/token

现在，让我们获取访问令牌：

```shell
curl -L -X POST 'http://localhost:8080/auth/realms/tuyucheng-soap-services/protocol/openid-connect/token' \
-H 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'client_id=tuyucheng-soap-services' \
--data-urlencode 'client_secret=VI4IWImf5660vRg3d5RwSL8rSNVdnPHA' \
--data-urlencode 'username=janedoe' \
--data-urlencode 'password=password'
```

实际上，我们获得了访问令牌和刷新令牌以及元数据：

```json
{
    "access_token": "eyJh ...",
    "expires_in": 300,
    "refresh_expires_in": 1800,
    "refresh_token": "eyJh ...",
    "token_type": "Bearer",
    "not-before-policy": 0,
    "session_state": "364b8f3e-34ff-4ca0-8895-bfbdb8b466d4",
    "scope": "profile email"
}
```

此外，可配置的expires_in键定义了此令牌的生命周期。例如，上面的访问令牌将在5分钟(300秒)后过期。

### 5.3 使用访问令牌的Web服务调用

在此示例中，让我们使用在上一节中检索到的访问令牌，让我们使用访问令牌作为[Bearer Token](https://datatracker.ietf.org/doc/html/rfc6750)来调用SOAP Web服务。

```shell
curl -d @request.xml -i -o -X POST -H 'Authorization: Bearer BwcYg94bGV9TLKH8i2Q' \
  -H 'Content-Type: text/xml' http://localhost:18080/ws/api/v1
```

使用正确的访问令牌，响应是：

```xml
<ns2:getProductDetailsResponse xmlns:ns2="http://www.tuyucheng.com/springbootsoap/keycloak">
    <ns2:product>
        <ns2:id>1</ns2:id>
            ...
        </ns2:product>
</ns2:getProductDetailsResponse>
```

### 5.4 授权

回想一下，我们为具有用户角色的用户janedoe生成了访问令牌，使用用户访问令牌，让我们尝试执行管理员操作。也就是说，让我们尝试调用deleteProduct：

```shell
curl -d @request.xml -i -o -X POST -H 'Authorization: Bearer sSgGNZ3KbMMTQ' -H 'Content-Type: text/xml' \
  http://localhost:18080/ws/api/v1
```

其中request.xml的内容是：

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:key="http://www.tuyucheng.com/springbootsoap/keycloak">
    <soapenv:Header/>
    <soapenv:Body>
        <key:deleteProductRequest>
            <key:id>1</key:id>
        </key:deleteProductRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

由于用户未被授权访问管理员操作，因此我们被拒绝访问：

```xml
<SOAP-ENV:Fault>
    <faultcode>SOAP-ENV:Server</faultcode>
        <faultstring xml:lang="en">Access is denied</faultstring>
</SOAP-ENV:Fault>
```

## 6. 总结

本教程演示了如何开发SOAP Web服务、keycloak配置以及使用Keycloak保护我们的Web服务、保护REST Web服务的方式，我们必须保护我们的SOAP Web服务免受可疑用户和未经授权的访问。