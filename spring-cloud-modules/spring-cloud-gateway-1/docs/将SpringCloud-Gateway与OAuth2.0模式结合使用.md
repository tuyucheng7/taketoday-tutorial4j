## 1. 简介

Spring Cloud Gateway 是一个库，它允许我们基于Spring Boot快速创建轻量级 API 网关，我们已经在之前的文章中介绍过。

这一次，我们将展示如何在它之上快速实现 OAuth 2.0 模式。

## 2. OAuth 2.0 快速回顾

OAuth 2.0 标准是一个在整个互联网上广泛使用的成熟标准，作为一种安全机制，用户和应用程序可以通过它安全地访问资源。

虽然详细描述该标准超出了本文的范围，但让我们先快速回顾一下几个关键术语：

-   资源：只能由授权客户端检索的任何类型的信息
-   客户端：通常通过 REST API 使用资源的应用程序
-   资源服务器：负责为授权客户端提供资源的服务
-   资源所有者：拥有资源并最终负责向客户端授予访问权限的实体(人或应用程序)
-   令牌：客户端获取的一条信息，作为请求的一部分发送到资源服务器以对其进行身份验证
-   身份提供者 (IdP)：验证用户凭据并向客户端颁发访问令牌。
-   身份验证流程：客户端必须经过的一系列步骤才能获得有效令牌。

对于该标准的全面描述，一个好的起点是 Auth0[关于此主题的文档](https://auth0.com/docs/get-started/authentication-and-authorization-flow)。

## 3. OAuth 2.0 模式

Spring Cloud Gateway 主要用于以下角色之一：

-   OAuth客户端
-   OAuth 资源服务器

让我们更详细地讨论每个案例。

### 3.1. Spring Cloud Gateway 作为 OAuth 2.0 客户端

在这种情况下，任何未经身份验证的传入请求都将启动授权代码流。一旦令牌被网关获取，它就会在向后端服务发送请求时使用：

[![OAuth 2.0 授权代码流程](https://www.baeldung.com/wp-content/uploads/2022/01/oauth2_authorization_flow.png)](https://www.baeldung.com/wp-content/uploads/2022/01/oauth2_authorization_flow.png)

这种模式的一个很好的例子是社交网络提要聚合器应用程序：对于每个支持的网络，网关将充当 OAuth 2.0 客户端。

因此，前端(通常是使用 Angular、React 或类似 UI 框架构建的 SPA 应用程序)可以代表最终用户无缝访问这些网络上的数据。更重要的是：它可以做到这一点，而无需用户向聚合器透露他们的凭据。

### 3.2. Spring Cloud Gateway 作为 OAuth 2.0 资源服务器

在这里，网关充当看门人，强制每个请求在发送到后端服务之前都具有有效的访问令牌。此外，它还可以根据关联的范围检查令牌是否具有访问给定资源的适当权限：

[![Spring Gateway 资源服务器](https://www.baeldung.com/wp-content/uploads/2022/01/spring_gateway_resource_server.png)](https://www.baeldung.com/wp-content/uploads/2022/01/spring_gateway_resource_server.png)

重要的是要注意这种权限检查主要在粗略级别上运行。细粒度的访问控制(例如，对象/字段级权限)通常在后端使用域逻辑实现。
在此模式中需要考虑的一件事是后端服务如何验证和授权任何转发的请求。主要有两种情况：

-   Token propagation : API Gateway将接收到的token原样转发给后端
-   令牌替换：API 网关在发送请求之前用另一个令牌替换传入的令牌。

在本教程中，我们将仅介绍令牌传播情况，因为这是最常见的情况。第二个也是可能的，但需要额外的设置和编码，这会分散我们对我们想要在这里展示的要点的注意力。

## 4. 示例项目概述

为了展示如何将 Spring Gateway 与我们目前描述的 OAuth 模式结合使用，让我们构建一个公开单个端点的示例项目：/quotes/{symbol}。访问此端点需要配置的身份提供者颁发的有效访问令牌。

在我们的例子中，我们将使用[嵌入式 Keycloak identity provider](https://www.baeldung.com/keycloak-embedded-in-spring-boot-app)。唯一需要的更改是添加一个新的客户端应用程序和一些用于测试的用户。

为了让事情变得更有趣，我们的后端服务将根据与请求关联的用户返回不同的报价。拥有黄金角色的用户获得较低的价格，而其他人获得正常价格(毕竟生活是不公平的 ;^))。

我们将使用 Spring Cloud Gateway 作为此服务的前端，通过仅更改几行配置，我们将能够将其角色从 OAuth 客户端切换为资源服务器。

## 5.项目设置

### 5.1. Keycloak IdP

我们将在本教程中使用的嵌入式 Keycloak 只是一个常规的 SpringBoot 应用程序，我们可以从[GitHub](https://github.com/Baeldung/spring-security-oauth)克隆它并使用 Maven 构建它：

```bash
$ git clone https://github.com/Baeldung/spring-security-oauth
$ cd oauth-rest/oauth-authorization/server
$ mvn install
```

注意：该项目目前以Java13+ 为目标，但也可以使用Java11 构建和运行良好。我们只需要将-Djava.version=11添加到 Maven 的命令中。

接下来，我们将替换这个src / main/resources/baeldung- [domain.json](https://raw.githubusercontent.com/eugenp/tutorials/master/spring-cloud/spring-cloud-gateway/baeldung-realm.json)。修改后的版本具有与原始版本相同的可用配置，外加一个额外的客户端应用程序 ( quotes-client )、两个用户组(golden_和silver_customers)和两个角色(gold和silver)。

我们现在可以使用spring-boot:run maven 插件启动服务器：

```bash
$ mvn spring-boot:run
... many, many log messages omitted
2022-01-16 10:23:20.318
  INFO 8108 --- [           main] c.baeldung.auth.AuthorizationServerApp   : Started AuthorizationServerApp in 23.815 seconds (JVM running for 24.488)
2022-01-16 10:23:20.334
  INFO 8108 --- [           main] c.baeldung.auth.AuthorizationServerApp   : Embedded Keycloak started: http://localhost:8083/auth to use keycloak
```

服务器启动后，我们可以通过将浏览器指向http://localhost:8083/auth/admin/master/console/#/realms/baeldung来访问它。使用管理员凭据 ( bael-admin/pass ) 登录后，我们将看到领域的管理屏幕：

[![Keycloak Baeldung 领域管理屏幕](https://www.baeldung.com/wp-content/uploads/2022/01/keycloak-baeldung-realm.png)](https://www.baeldung.com/wp-content/uploads/2022/01/keycloak-baeldung-realm.png)

要完成 IdP 设置，让我们添加几个用户。第一个是 Maxwell Smart，golden_customer组的成员。第二位是 John Snow，我们不会将其添加到任何组。

使用提供的配置，golden_customers组的成员将自动承担黄金角色。

### 5.2. 后端服务

报价后端需要常规的Spring BootReactive MVC 依赖项，以及[资源服务器启动器依赖项](https://search.maven.org/search?q=spring-boot-starter-oauth2-resource-server)：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
    <version>2.6.2</version>
</dependency>

```

请注意，我们有意省略了依赖项的版本。这是在使用SpringBoot的父POM或者依赖管理部分对应的BOM时推荐的做法。

在主应用程序类中，我们必须使用@EnableWebFluxSecurity启用 Web 流量安全：

```java
@SpringBootApplication
@EnableWebFluxSecurity
public class QuotesApplication {    
    public static void main(String[] args) {
        SpringApplication.run(QuotesApplication.class);
    }
}
```

端点实现使用提供的BearerAuthenticationToken来检查当前用户是否具有黄金角色：

```java
@RestController
public class QuoteApi {
    private static final GrantedAuthority GOLD_CUSTOMER = new SimpleGrantedAuthority("gold");

    @GetMapping("/quotes/{symbol}")
    public Mono<Quote> getQuote(@PathVariable("symbol") String symbol,
      BearerTokenAuthentication auth ) {
        
        Quote q = new Quote();
        q.setSymbol(symbol);        
        if ( auth.getAuthorities().contains(GOLD_CUSTOMER)) {
            q.setPrice(10.0);
        }
        else {
            q.setPrice(12.0);
        }
        return Mono.just(q);
    }
}

```

现在，Spring 如何获得用户角色？毕竟，这不是像scopes或email这样的标准声明。事实上，这里没有魔法：我们必须提供一个自定义的ReactiveOpaqueTokenIntrospection，它从 Keycloak 返回的自定义字段中提取这些角色。该 bean 可在线获取，与 Spring[关于此主题的文档](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/opaque-token.html#oauth2resourceserver-opaque-authorization-extraction)中显示的基本相同，只是针对我们的自定义字段进行了一些小的更改。

我们还必须提供访问身份提供者所需的配置属性：

```ini
spring.security.oauth2.resourceserver.opaquetoken.introspection-uri=http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/token/introspect
spring.security.oauth2.resourceserver.opaquetoken.client-id=quotes-client
spring.security.oauth2.resourceserver.opaquetoken.client-secret=<CLIENT SECRET>

```

最后，要运行我们的应用程序，我们可以在 IDE 中导入它或从 Maven 运行它。该项目的 POM 包含一个用于此目的的配置文件：

```bash
$ mvn spring-boot:run -Pquotes-application
```

该应用程序现在可以为 http://localhost:8085/quotes上的请求提供服务。我们可以使用curl检查它是否正在响应 ：

```bash
$ curl -v http://localhost:8085/quotes/BAEL
```

正如预期的那样，我们收到401 Unauthorized响应，因为没有 发送Authorization标头。

## 6. Spring Gateway 作为 OAuth 2.0 资源服务器

保护充当资源服务器的[Spring Cloud Gateway 应用程序与常规资源服务没有什么不同。](https://search.maven.org/search?q=spring-cloud-starter-gateway)因此，我们必须添加与后端服务相同的起始依赖项也就不足为奇了：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
    <version>3.1.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
    <version>2.6.2</version>
</dependency>

```

因此，我们还必须将@EnableWebFluxSecurity添加到我们的启动类：

```java
@SpringBootApplication
@EnableWebFluxSecurity
public class ResourceServerGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResourceServerGatewayApplication.class,args);
    }
}

```

与安全相关的配置属性与后端使用的相同：

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        opaquetoken:
          introspection-uri: http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/token/introspect
          client-id: quotes-client
          client-secret: <code class="language-css"><CLIENT SECRET> 
```

接下来，我们只需添加路由声明，就像我们在[之前关于 Spring Cloud Gateway 设置的文章中](https://www.baeldung.com/spring-cloud-gateway)所做的那样：

```yaml
... other properties omitted
  cloud:
    gateway:
      routes:
      - id: quotes
        uri: http://localhost:8085
        predicates:
        - Path=/quotes/

```

请注意，除了安全依赖项和属性之外，我们没有对网关本身进行任何更改。要运行网关应用程序，我们将使用spring-boot:run，使用具有所需设置的特定配置文件：

```bash
$ mvn spring-boot:run -Pgateway-as-resource-server
```

### 6.1. 测试资源服务器

现在我们有了拼图的所有部分，让我们把它们放在一起。首先，我们必须确保我们有 Keycloak、报价后端和网关都在运行。

接下来，我们需要从 Keycloak 获取访问令牌。在这种情况下，最直接的获取方式是使用密码授予流程(又名“资源所有者”)。这意味着向 Keycloak 发送一个 POST 请求，传递其中一个用户的用户名/密码，以及报价客户端应用程序的客户端 ID 和密码：

```bash
$ curl -L -X POST 
  'http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/token' 
  -H 'Content-Type: application/x-www-form-urlencoded' 
  --data-urlencode 'client_id=quotes-client' 
  --data-urlencode 'client_secret=0e082231-a70d-48e8-b8a5-fbfb743041b6' 
  --data-urlencode 'grant_type=password' 
  --data-urlencode 'scope=email roles profile' 
  --data-urlencode 'username=john.snow' 
  --data-urlencode 'password=1234'

```

响应将是一个包含访问令牌以及其他值的 JSON 对象：

```json
{
	"access_token": "...omitted",
	"expires_in": 300,
	"refresh_expires_in": 1800,
	"refresh_token": "...omitted",
	"token_type": "bearer",
	"not-before-policy": 0,
	"session_state": "7fd04839-fab1-46a7-a179-a2705dab8c6b",
	"scope": "profile email"
}

```

我们现在可以使用返回的访问令牌来访问/quotes API：

```bash
$ curl --location --request GET 'http://localhost:8086/quotes/BAEL' 
--header 'Accept: application/json' 
--header 'Authorization: Bearer xxxx...'
```

生成 JSON 格式的报价：

```json
{
  "symbol":"BAEL",
  "price":12.0
}
```

让我们重复这个过程，这次使用 Maxwell Smart 的访问令牌：

```json
{
  "symbol":"BAEL",
  "price":10.0
}
```

我们看到我们的价格较低，这意味着后端能够正确识别关联用户。我们还可以使用没有Authorization标头的 curl 请求来检查未经身份验证的请求是否不会传播到后端 ：

```bash
$ curl  http://localhost:8086/quotes/BAEL
```

检查网关日志，我们发现没有与请求转发过程相关的消息。这表明响应是在网关处生成的。

## 7. Spring Gateway 作为 OAuth 2.0 客户端

对于启动类，我们将使用与资源服务器版本相同的类。我们将使用它来强调所有安全行为都来自可用的库和属性。

事实上，比较两个版本时唯一明显的区别在于配置属性。在这里，我们需要使用issuer-uri属性或各种端点(授权、令牌和自省)的单独设置来配置提供者详细信息。

我们还需要定义我们的应用程序客户端注册详细信息，其中包括请求的范围。这些范围通知 IdP 哪些信息项集将通过内省机制可用：

```yaml
... other propeties omitted
  security:
    oauth2:
      client:
        provider:
          keycloak:
            issuer-uri: http://localhost:8083/auth/realms/baeldung
        registration:
          quotes-client:
            provider: keycloak
            client-id: quotes-client
            client-secret: <CLIENT SECRET>
            scope:
            - email
            - profile
            - roles

```

最后，路由定义部分有一个重要的变化。我们必须将TokenRelay过滤器添加到任何需要传播访问令牌的路由：

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: quotes
        uri: http://localhost:8085
        predicates:
        - Path=/quotes/
        filters:
        - TokenRelay=

```

或者，如果我们希望所有路由都启动授权流程，我们可以将 TokenRelay过滤器添加到 default-filters 部分：

```yaml
spring:
  cloud:
    gateway:
      default-filters:
      - TokenRelay=
      routes:
... other routes definition omitted
```

### 7.1. 将 Spring Gateway 作为 OAuth 2.0 客户端进行测试

对于测试设置，我们还需要确保我们的项目的三个部分正在运行。然而，这一次，我们将使用包含所需属性的不同[Spring Profile](https://www.baeldung.com/spring-profiles)来运行网关，以使其充当 OAuth 2.0 客户端。示例项目的 POM 包含一个配置文件，允许我们在启用此配置文件的情况下启动它：

```bash
$ mvn spring-boot:run -Pgateway-as-oauth-client
```

网关运行后，我们可以通过将浏览器指向 http://localhost:8087/quotes/BAEL 来测试它。如果一切正常，我们将被重定向到 IdP 的登录页面：

[![登录页面](https://www.baeldung.com/wp-content/uploads/2022/01/maxwell-login.png)](https://www.baeldung.com/wp-content/uploads/2022/01/maxwell-login.png)

由于我们使用了 Maxwell Smart 的凭据，我们再次获得了更低价格的报价：

[![麦克斯韦的名言](https://www.baeldung.com/wp-content/uploads/2022/01/maxwell-quote.png)](https://www.baeldung.com/wp-content/uploads/2022/01/maxwell-quote.png)

为了结束我们的测试，我们将使用匿名/隐身浏览器窗口并使用 John Snow 的凭据测试此端点。这次我们得到的是常规报价：

[![雪的报价](https://www.baeldung.com/wp-content/uploads/2022/01/snows-quote.png)](https://www.baeldung.com/wp-content/uploads/2022/01/snows-quote.png)

## 八. 总结

在本文中，我们探讨了一些 OAuth 2.0 安全模式以及如何使用 Spring Cloud Gateway 实现它们。像往常一样，所有代码都可以[在 GitHub 上找到](https://github.com/eugenp/tutorials/tree/master/spring-cloud-modules/spring-cloud-gateway)。