## 1. 概述

Cloud Foundry 用户帐户和身份验证 (CF UAA) 是一种身份管理和授权服务。更准确地说，它是一个 OAuth 2.0 提供程序，允许向客户端应用程序进行身份验证和颁发令牌。

在本教程中，我们将介绍设置 CF UAA 服务器的基础知识。然后我们将看看如何使用它来保护资源服务器应用程序。

[不过在此之前，我们先明确一下UAA在OAuth 2.0](https://auth0.com/docs/protocols/oauth2)授权框架中的作用。

## 2. Cloud Foundry UAA 和 OAuth 2.0

让我们首先了解 UAA 与 OAuth 2.0 规范的关系。

OAuth 2.0 规范定义了四个可以相互连接的[参与者：资源所有者、资源服务器、客户端和授权服务器。](https://tools.ietf.org/html/rfc6749#section-1.1)

作为 OAuth 2.0 提供者，UAA 扮演着授权服务器的角色。这意味着它的主要目标是为客户端应用程序颁发访问令牌并为资源服务器验证这些令牌。

为了让这些参与者能够交互，我们需要先搭建一个UAA服务器，然后再实现两个应用：一个作为客户端，一个作为资源服务器。

我们将对 客户端使用[authorization_code授权流程。](https://tools.ietf.org/html/rfc6749#section-1.3.1)我们将对资源服务器使用不记名令牌授权。为了更安全和高效的握手，我们将使用签名的 JWT 作为我们的[访问令牌](https://tools.ietf.org/html/rfc6749#section-1.4)。

## 3. 设置 UAA 服务器

首先，我们将安装 UAA 并使用一些演示数据填充它。

安装后，我们将注册一个名为webappclient 的客户端应用程序。然后，我们将创建一个名为appuser的用户，该用户具有两个角色resource.read和resource.write。

### 3.1. 安装

UAA 是一个JavaWeb 应用程序，可以在任何兼容的 servlet 容器中运行。在本教程中，我们将[使用 Tomcat](https://tomcat.apache.org/whichversion.html)。

让我们继续下载[UAA war](https://search.maven.org/search?q=g:org.cloudfoundry.identity AND a:cloudfoundry-identity-uaa)并将其存放到我们的 Tomcat部署中：

```bash
wget -O $CATALINA_HOME/webapps/uaa.war 
  https://search.maven.org/remotecontent?filepath=org/cloudfoundry/identity/cloudfoundry-identity-uaa/4.27.0/cloudfoundry-identity-uaa-4.27.0.war
```

不过，在我们启动它之前，我们需要配置它的数据源和 JWS 密钥对。

### 3.2. 所需配置

默认情况下，UAA 从其类路径上的uaa.yml读取配置。但是，因为我们刚刚下载了 war文件，所以我们最好在我们的文件系统上告诉 UAA 一个自定义位置。

我们可以通过设置 UAA_CONFIG_PATH属性来做到这一点：

```bash
export UAA_CONFIG_PATH=~/.uaa
```

或者，我们可以设置CLOUD_FOUNDRY_CONFIG_PATH。或者，我们可以使用 UAA_CONFIG_URL 指定远程位置。

[然后，我们可以将UAA 所需的配置](https://raw.githubusercontent.com/cloudfoundry/uaa/4.27.0/uaa/src/main/resources/required_configuration.yml)到我们的配置路径中：

```bash
wget -qO- https://raw.githubusercontent.com/cloudfoundry/uaa/4.27.0/uaa/src/main/resources/required_configuration.yml 
  > $UAA_CONFIG_PATH/uaa.yml
```

请注意，我们正在删除最后三行，因为我们稍后将替换它们。

### 3.3. 配置数据源

因此，让我们配置数据源，UAA 将在其中存储有关客户端的信息。

出于本教程的目的，我们将使用 HSQLDB：

```bash
export SPRING_PROFILES="default,hsqldb"
```

当然，由于这是一个Spring Boot应用程序，我们也可以在uaa.yml 中将其指定为 spring.profiles属性。

### 3.4. 配置 JWS 密钥对

由于我们使用的是 JWT，所以UAA 需要有一个私钥来对 UAA 发布的每个 JWT 进行签名。

OpenSSL 使这变得简单：

```bash
openssl genrsa -out signingkey.pem 2048
openssl rsa -in signingkey.pem -pubout -out verificationkey.pem
```

授权服务器将使用私钥签署JWT，我们的客户端和资源服务器将使用公钥验证该签名。

我们会将它们导出到JWT_TOKEN_SIGNING_KEY和JWT_TOKEN_VERIFICATION_KEY：

```bash
export JWT_TOKEN_SIGNING_KEY=$(cat signingkey.pem)
export JWT_TOKEN_VERIFICATION_KEY=$(cat verificationkey.pem)

```

同样，我们可以 通过 jwt.token.signing-key和 jwt.token.verification-key属性在uaa.yml中指定这些。

### 3.5. 启动 UAA

最后，让我们开始吧：

```bash
$CATALINA_HOME/bin/catalina.sh run
```

此时，我们应该在[http://localhost:8080/uaa](http://localhost:8080/uaa)有一个可用的可用 UAA 服务器。

如果我们访问[http://localhost:8080/uaa/info](http://localhost:8080/uaa/info)，那么我们会看到一些基本的启动信息

### 3.6. 安装 UAA 命令行客户端

CF UAA Command-Line Client 是管理 UAA 的主要工具，但要使用它，我们需要[先安装 Ruby](https://rubygems.org/)：

```bash
sudo apt install rubygems
gem install cf-uaac
```

然后，我们可以配置uaac指向我们正在运行的 UAA 实例：

```bash
uaac target http://localhost:8080/uaa
```

请注意，如果我们不想使用命令行客户端，我们当然可以使用 UAA 的 HTTP 客户端。

### 3.7. 使用 UAAC 填充客户端和用户

现在我们已经安装了uaac，让我们用一些演示数据填充 UAA。至少，我们需要：一个client、一个user以及resource.read和resource.write组。

因此，要进行任何管理，我们都需要自己进行身份验证。我们将选择 UAA 附带的默认管理员，它有权创建其他客户端、用户和组：

```bash
uaac token client get admin -s adminsecret
```

(当然，我们肯定需要在发布之前通过[oauth-clients.xml](https://github.com/cloudfoundry/uaa/blob/master/uaa/src/main/webapp/WEB-INF/spring/oauth-clients.xml)文件更改此帐户！)

基本上，我们可以将此命令解读为：“给我一个令牌，使用带有admin的client_id和adminsecret的秘密的客户端凭证”。

如果一切顺利，我们将看到一条成功消息：

```plaintext
Successfully fetched token via client credentials grant.
```

令牌存储在uaac的状态中。

现在，以admin身份运行，我们可以使用client add 注册一个名为webappclient的 客户端：

```bash
uaac client add webappclient -s webappclientsecret  
--name WebAppClient  
--scope resource.read,resource.write,openid,profile,email,address,phone  
--authorized_grant_types authorization_code,refresh_token,client_credentials,password  
--authorities uaa.resource  
--redirect_uri http://localhost:8081/login/oauth2/code/uaa
```

而且，我们可以使用用户添加注册一个名为appuser的用户：

```bash
uaac user add appuser -p appusersecret --emails appuser@acme.com
```

接下来，我们将添加两个组——resource.read和resource.write——使用 组添加：

```bash
uaac group add resource.read
uaac group add resource.write
```

最后，我们将使用 member add将这些组分配给appuser ：

```bash
uaac member add resource.read appuser
uaac member add resource.write appuser
```

呸！所以，到目前为止我们所做的是：

-   安装和配置 UAA
-   安装uaac
-   添加了演示客户端、用户和组

所以，让我们牢记这些信息并跳到下一步。

## 4.OAuth 2.0 客户端

在本节中，我们将使用Spring Boot创建一个 OAuth 2.0 客户端应用程序。

### 4.1. 应用设置

让我们首先访问[Spring Initializr](https://start.spring.io/)并生成一个Spring BootWeb 应用程序。我们只选择Web和OAuth2 Client组件：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

在此示例中，我们使用了[2.1.3 版](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-parent&core=gav)的 Spring Boot。

接下来，我们需要注册我们的客户端，webapp客户端。

很简单，我们需要为应用程序提供client-id、client-secret和 UAA 的issuer-uri。我们还将指定此客户端希望用户授予它的 OAuth 2.0 范围：

```plaintext
#registration
spring.security.oauth2.client.registration.uaa.client-id=webappclient
spring.security.oauth2.client.registration.uaa.client-secret=webappclientsecret
spring.security.oauth2.client.registration.uaa.scope=resource.read,resource.write,openid,profile

#provider
spring.security.oauth2.client.provider.uaa.issuer-uri=http://localhost:8080/uaa/oauth/token
```

有关这些属性的更多信息，我们可以查看有关[注册](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/security/oauth2/client/OAuth2ClientProperties.Registration.html)和[提供程序](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/security/oauth2/client/OAuth2ClientProperties.Provider.html)bean 的Java文档。

由于我们已经为 UAA 使用了端口 8080，所以让我们在 8081 上运行：

```plaintext
server.port=8081
```

### 4.2. 登录

现在如果我们访问/login路径，我们应该有一个所有注册客户端的列表。在我们的案例中，我们只有一个注册客户：

[![UAA-1](https://www.baeldung.com/wp-content/uploads/2019/04/uaa-1.png)](https://www.baeldung.com/wp-content/uploads/2019/04/uaa-1.png)
单击该链接会将我们重定向到 UAA 登录页面：

[![uaa1](https://www.baeldung.com/wp-content/uploads/2019/04/uaa1.png)](https://www.baeldung.com/wp-content/uploads/2019/04/uaa1.png)

在这里，让我们使用appuser/appusersecret登录。

提交表格应该将我们重定向到批准表格，用户可以在其中授权或拒绝访问我们的客户：

[![无人机2](https://www.baeldung.com/wp-content/uploads/2019/04/uaa2.png)](https://www.baeldung.com/wp-content/uploads/2019/04/uaa2.png)

然后用户可以授予她想要的特权。出于我们的目的，我们将选择 除 resource:write 之外的所有内容。

无论用户检查什么，都将是生成的访问令牌中的范围。

[为了证明这一点，我们可以索引路径http://localhost:8081](http://localhost:8081/)中显示的令牌，并使用[JWT 调试器](https://jwt.io/#debugger-io)对其进行解码。 我们应该在批准页面上看到我们检查的范围：

```javascript
{
  "jti": "f228d8d7486942089ff7b892c796d3ac",
  "sub": "0e6101d8-d14b-49c5-8c33-fc12d8d1cc7d",
  "scope": [
    "resource.read",
    "openid",
    "profile"
  ],
  "client_id": "webappclient"
  // more claims
}
```

一旦我们的客户端应用程序收到此令牌，它就可以对用户进行身份验证，他们将有权访问该应用程序。

现在，一个不显示任何数据的应用程序不是很有用，所以我们的下一步将是建立一个资源服务器——它有用户的数据——并将客户端连接到它。

完成的资源服务器将有两个受保护的 API：一个需要resource.read范围，另一个需要resource.write。

我们将看到，使用我们授予的范围的客户端将能够调用读取API 但不能 写入。

## 5.资源服务器

资源服务器托管用户的受保护资源。

它通过Authorization标头并与授权服务器协商对客户端进行身份验证——在我们的例子中，这是 UAA。

### 5.1. 申请设置

为了创建我们的资源服务器，我们将再次使用[Spring Initializr](https://start.spring.io/)来生成一个Spring BootWeb 应用程序。这次，我们将选择Web和OAuth2 资源服务器组件：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

与客户端应用程序一样，我们使用[的是 2.1.3 版](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-parent&core=gav)的 Spring Boot。

下一步是在application.properties文件中指示正在运行的 CF UAA 的位置：

```plaintext
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/uaa/oauth/token
```

当然，我们也在这里选择一个新端口。8082 可以正常工作：

```plaintext
server.port=8082
```

就是这样！我们应该有一个可用的资源服务器，默认情况下，所有请求都需要在Authorization 标头中提供有效的访问令牌。

### 5.2. 保护资源服务器 API

接下来，让我们添加一些值得保护的端点。

我们将添加一个具有两个端点的RestController ，一个授权给具有resource.read范围的用户，另一个授权给具有 resource.write 范围的用户：

```java
@GetMapping("/read")
public String read(Principal principal) {
    return "Hello write: " + principal.getName();
}

@GetMapping("/write")
public String write(Principal principal) {
    return "Hello write: " + principal.getName();
}
```

接下来，我们将覆盖默认的Spring Boot配置以保护这两个资源：

```java
@EnableWebSecurity
public class OAuth2ResourceServerSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/read/")
            .hasAuthority("SCOPE_resource.read")
            .antMatchers("/write/")
            .hasAuthority("SCOPE_resource.write")
            .anyRequest()
            .authenticated()
            .and()
            .oauth2ResourceServer()
            .jwt();
        return http.build();
    }
}
```

请注意，访问令牌中提供的范围在转换为[Spring Security ](https://www.baeldung.com/role-and-privilege-for-spring-security-registration)[GrantedAuthority](https://www.baeldung.com/role-and-privilege-for-spring-security-registration)时以SCOPE_为前缀。

### 5.3. 从客户端请求受保护的资源

在客户端应用程序中，我们将使用RestTemplate 调用这两个受保护的资源。在发出请求之前，我们从上下文中检索访问令牌并将其添加到授权标头中：

```java
private String callResourceServer(OAuth2AuthenticationToken authenticationToken, String url) {
    OAuth2AuthorizedClient oAuth2AuthorizedClient = this.authorizedClientService.
      loadAuthorizedClient(authenticationToken.getAuthorizedClientRegistrationId(), 
      authenticationToken.getName());
    OAuth2AccessToken oAuth2AccessToken = oAuth2AuthorizedClient.getAccessToken();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + oAuth2AccessToken.getTokenValue());

    // call resource endpoint

    return response;
}
```

但是请注意，如果我们[使用 WebClient而不是 RestTemplate](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#servlet-webclient) ，我们可以删除这个样板。

然后，我们将添加两个对资源服务器端点的调用：

```java
@GetMapping("/read")
public String read(OAuth2AuthenticationToken authenticationToken) {
    String url = remoteResourceServer + "/read";
    return callResourceServer(authenticationToken, url);
}

@GetMapping("/write")
public String write(OAuth2AuthenticationToken authenticationToken) {
    String url = remoteResourceServer + "/write";
    return callResourceServer(authenticationToken, url);
}
```

正如预期的那样， / read API的调用会成功，但 / write API 的调用不会成功。HTTP 状态 403 告诉我们用户未被授权。

## 六. 总结

在本文中，我们首先简要概述了 OAuth 2.0，因为它是 UAA(一个 OAuth 2.0 授权服务器)的基础。然后，我们将其配置为为客户端颁发访问令牌并保护资源服务器应用程序。