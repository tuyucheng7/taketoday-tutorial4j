## 1. 概述

[OpenFeign](https://www.baeldung.com/spring-cloud-openfeign)是一个声明式 REST 客户端，我们可以在[Spring Boot](https://www.baeldung.com/spring-boot)应用程序中使用它。假设我们有一个[使用 OAuth2 保护的 REST API](https://www.baeldung.com/spring-security-oauth-resource-server)，并且我们想使用 OpenFeign 调用它。在这种情况下，我们需要使用 OpenFeign 提供访问令牌。

在本教程中，我们将介绍如何向 OpenFeign 客户端添加 OAuth2 支持。

## 2.服务到服务认证

服务到服务的身份验证是 API 安全中的一个热门话题。我们可以使用[mTLS](https://www.baeldung.com/spring-tls-setup)或[JWT](https://www.baeldung.com/spring-security-oauth-jwt)为 REST API 提供身份验证机制。然而，OAuth2 协议是保护 API 的事实解决方案。假设我们想使用另一个服务(客户端角色)调用一个安全服务(服务器角色)。在这种情况下，我们使用[客户端凭证](https://datatracker.ietf.org/doc/html/rfc6749#section-1.3.4)授予类型。我们通常使用客户端凭据在两个没有最终用户的 API 或系统之间进行身份验证。下图显示了此资助类型中的主要参与者：

[![openfeign 客户端凭证1](https://www.baeldung.com/wp-content/uploads/2022/02/openfeign-client-credential1.png)](https://www.baeldung.com/wp-content/uploads/2022/02/openfeign-client-credential1.png)

在客户端凭据中，客户端服务使用令牌端点从授权服务器获取访问令牌。然后它使用访问令牌访问受资源服务器保护的资源。资源服务器验证访问令牌，如果有效，则为请求提供服务。

### 2.1. 授权服务器

让我们设置一个授权服务器来颁发访问令牌。现在为了简单起见，我们将使用[嵌入在 Spring Boot](https://www.baeldung.com/keycloak-embedded-in-spring-boot-app)应用程序中的 Keycloak。假设我们使用[GitHub 上可用](https://github.com/Baeldung/spring-security-oauth/tree/master/oauth-resource-server/authorization-server)的授权服务器项目。首先，我们在嵌入式 Keycloak 服务器中的 realm master中定义支付应用程序客户端：

[![openfeign支付客户端1](https://www.baeldung.com/wp-content/uploads/2022/02/openfeign-payment_client1.png)](https://www.baeldung.com/wp-content/uploads/2022/02/openfeign-payment_client1.png)

我们将访问类型设置为凭据 并启用服务帐户启用选项。然后，我们将领域详细信息导出为feign-realm.json并在我们的application-feign.yml中设置领域文件：

```yaml
keycloak:
  server:
    contextPath: /auth
    adminUser:
      username: bael-admin
      password: pass
    realmImportFile: feign-realm.json

```

现在，授权服务器已准备就绪。最后，我们可以使用–spring.profiles.active=feign选项运行应用程序。由于我们在本教程中专注于 OpenFeign OAuth2 支持，因此我们不需要深入研究它。

### 2.2. 资源服务器

现在我们已经配置了授权服务器，让我们设置资源服务器。为此，我们将使用[GitHub 上提供](https://github.com/Baeldung/spring-security-oauth/tree/master/oauth-resource-server/resource-server-jwt)的资源服务器项目。首先，我们将Payment类添加为资源：

```java
public class Payment {

    private String id;
    private double amount;

   // standard getters and setters
}

```

然后，我们在PaymentController类中声明一个 API：

```java
@RestController
public class PaymentController {

    @GetMapping("/payments")
    public List<Payment> getPayments() {
        List<Payment> payments = new ArrayList<>();
        for(int i = 1; i < 6; i++){
            Payment payment = new Payment();
            payment.setId(String.valueOf(i));
            payment.setAmount(2);
            payments.add(payment);
        }
        return payments;
    }

}
```

getPayments () API 返回付款列表。此外，我们在application-feign.yml文件中配置资源服务器：

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8083/auth/realms/master
```

现在，getPayments() API 使用 OAuth2 授权服务器是安全的，我们必须提供有效的访问令牌以调用此 API：

```plaintext
curl --location --request POST 'http://localhost:8083/auth/realms/master/protocol/openid-connect/token' 
  --header 'Content-Type: application/x-www-form-urlencoded' 
  --data-urlencode 'client_id=payment-app' 
  --data-urlencode 'client_secret=863e9de4-33d4-4471-b35e-f8d2434385bb' 
  --data-urlencode 'grant_type=client_credentials'
```

获取访问令牌后，我们将其设置在请求的授权标头中：

```plaintext
curl --location --request GET 'http://localhost:8081/resource-server-jwt/payments' 
  --header 'Authorization: Bearer Access_Token' 
```

现在，我们想使用 OpenFeign 而不是[cURL](https://www.baeldung.com/curl-rest)或[Postman](https://www.baeldung.com/postman-testing-collections)来调用安全 API 。

## 3.OpenFeign客户端

### 3.1. 依赖关系

要使用 Spring Cloud OpenFeign 调用安全 API，我们需要将[spring-cloud-starter-openfeign 添加](https://search.maven.org/search?q=g:org.springframework.cloud AND a:spring-cloud-starter-openfeign)到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
    <version>3.1.0</version>
</dependency>
```

此外，我们需要将[spring-cloud-dependencies](https://search.maven.org/search?q=g:org.springframework.cloud AND a:spring-cloud-dependencies)添加到pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-dependencies</artifactId>
    <version>2021.0.0</version>
    <type>pom</type>
</dependency>
```

### 3.2. 配置

首先，我们需要在主类中添加[@EnableFeignClients ：](https://www.baeldung.com/spring-cloud-openfeign#client)

```java
@SpringBootApplication
@EnableFeignClients
public class ExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
}

```

然后，我们定义调用getPayments() API的PaymentClient接口。此外，我们需要将[@FeignClient](https://www.baeldung.com/spring-cloud-openfeign#client)添加到我们的PaymentClient接口：

```java
@FeignClient(
  name = "payment-client", 
  url = "http://localhost:8081/resource-server-jwt", 
  configuration = OAuthFeignConfig.class)
public interface PaymentClient {

    @RequestMapping(value = "/payments", method = RequestMethod.GET)
    List<Payment> getPayments();
}

```

我们根据资源服务器的地址设置url 。在这种情况下，@FeignClient 的主要参数是支持 OpenFeign 的 OAuth2的配置属性。之后，我们定义一个PaymentController类并将PaymentClient注入其中：

```java
@RestController
public class PaymentController {

    private final PaymentClient paymentClient;

    public PaymentController(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    @GetMapping("/payments")
    public List<Payment> getPayments() {
        List<Payment> payments = paymentClient.getPayments();
        return payments;
    }
}
```

## 4.OAuth2 支持

### 4.1. 依赖关系

要将 OAuth2 支持添加到 Spring Cloud OpenFeign，我们需要将[spring-security-oauth2-client](https://search.maven.org/search?q=g:org.springframework.security AND a:spring-security-oauth2-client)和[spring-boot-starter-security](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-security)添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
    <version>2.6.1</version>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-oauth2-client</artifactId>
    <version>5.6.0</version>
</dependency>
```

### 4.2. 配置

现在，我们要创建一个配置。这个想法是获取访问令牌并将其添加到 OpenFeign 请求。 拦截器可以为每个 HTTP 请求/响应执行此任务。添加拦截器是 Feign 提供的一个有用的特性。我们将使用[RequestInterceptor](https://www.baeldung.com/spring-cloud-openfeign#1-implementing-requestinterceptor) ，它 通过添加 Authorization Bearer 标头将 OAuth2 访问令牌注入到 OpenFeign 客户端的请求中。让我们定义OAuthFeignConfig配置类并定义requestInterceptor() bean：

```java
@Configuration
public class OAuthFeignConfig {

    public static final String CLIENT_REGISTRATION_ID = "keycloak";

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuthFeignConfig(OAuth2AuthorizedClientService oAuth2AuthorizedClientService,
      ClientRegistrationRepository clientRegistrationRepository) {
        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(CLIENT_REGISTRATION_ID);
        OAuthClientCredentialsFeignManager clientCredentialsFeignManager =
          new OAuthClientCredentialsFeignManager(authorizedClientManager(), clientRegistration);
        return requestTemplate -> {
            requestTemplate.header("Authorization", "Bearer " + clientCredentialsFeignManager.getAccessToken());
        };
    }
}
```

在requestInterceptor() bean 中，我们使用ClientRegistration和OAuthClientCredentialsFeignManager类来注册 oauth2 客户端并从授权服务器获取访问令牌。为此，我们需要在application.properties文件中定义oauth2客户端属性：

```java
spring.security.oauth2.client.registration.keycloak.authorization-grant-type=client_credentials
spring.security.oauth2.client.registration.keycloak.client-id=payment-app
spring.security.oauth2.client.registration.keycloak.client-secret=863e9de4-33d4-4471-b35e-f8d2434385bb
spring.security.oauth2.client.provider.keycloak.token-uri=http://localhost:8083/auth/realms/master/protocol/openid-connect/token
```

让我们创建OAuthClientCredentialsFeignManager类并定义getAccessToken()方法：

```java
public String getAccessToken() {
    try {
        OAuth2AuthorizeRequest oAuth2AuthorizeRequest = OAuth2AuthorizeRequest
          .withClientRegistrationId(clientRegistration.getRegistrationId())
          .principal(principal)
          .build();
        OAuth2AuthorizedClient client = manager.authorize(oAuth2AuthorizeRequest);
        if (isNull(client)) {
            throw new IllegalStateException("client credentials flow on " + clientRegistration.getRegistrationId() + " failed, client is null");
        }
        return client.getAccessToken().getTokenValue();
    } catch (Exception exp) {
        logger.error("client credentials error " + exp.getMessage());
    }
    return null;
}
```

我们使用OAuth2AuthorizeRequest 和OAuth2AuthorizedClient类从授权服务器获取访问令牌。现在对于每个请求，OpenFeign 拦截器管理 oauth2 客户端并将访问令牌添加到请求中。

## 5.测试

为了测试 OpenFeign 客户端，让我们创建PaymentClientUnitTest类：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class PaymentClientUnitTest {

    @Autowired
    private PaymentClient paymentClient;

    @Test
    public void whenGetPayment_thenListPayments() {
        List<Payment> payments = paymentClient.getPayments();
        assertFalse(payments.isEmpty());
    }
}

```

在此测试中，我们调用getPayments() API。底层的PaymentClient连接到 OAuth2 客户端并使用拦截器获取访问令牌。

## 六. 总结

在本文中，我们设置了调用安全 API 所需的环境。然后，我们通过一个实例配置OpenFeign来调用安全API。为此，我们将拦截器添加并配置到 OpenFeign。拦截器管理 OAuth2 客户端并将访问令牌添加到请求中。