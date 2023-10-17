## 1. 简介

管理Spring Boot应用程序中的安全通信通常涉及处理复杂的配置。挑战通常始于处理信任材料，例如证书和私钥，这些材料采用各种格式，如 JKS、PKCS #12 或 PEM。对于如何处理这些格式，每种格式都有自己的一套要求。

幸运的是，[Spring Boot 3.1](https://www.baeldung.com/spring-boot-3-migration)引入了SSL Bundles，这一功能旨在简化这些复杂性。在本教程中，我们将探讨什么是 SSL 捆绑包以及它们如何简化Spring Boot 应用程序的[SSL 配置](https://www.baeldung.com/spring-boot-https-self-signed-certificate)任务。

## 2.Spring BootSSL 捆绑包

通常，一旦我们获得了信任材料，我们需要将其转换为应用程序可以使用的Java对象。这通常意味着处理诸如用于存储密钥材料的[java.security.KeyStore](https://www.baeldung.com/java-keystore)、用于管理此密钥材料的 javax.net.ssl.KeyManager以及用于创建安全套接字连接的 javax.net.ssl.SSLContext之类的类。

每个类都需要另一层理解和配置，使得该过程繁琐且容易出错。各种[Spring Boot 组件](https://www.baeldung.com/spring-component-annotation)可能还需要深入研究不同的抽象层来应用这些设置，从而给任务增加了另一层难度。

SSL 捆绑包将所有信任材料和配置设置(例如密钥库、证书和私钥)封装到一个易于管理的单元中。配置 SSL 捆绑包后，即可将其应用于一个或多个网络连接，无论它们是传入还是传出。

SSL 捆绑包的配置属性位于application.yaml或application.properties配置文件中的spring.ssl.bundle前缀下。

让我们从 JKS 捆绑包开始。我们使用spring.ssl.bundle.jks来配置使用JavaKeystore 文件的包：

```yaml
spring:
  ssl:
    bundle:
      jks:
        server:
          key:
            alias: "server"
          keystore:
            location: "classpath:server.p12"
            password: "secret"
            type: "PKCS12"
```

对于 PEM 捆绑包，我们使用spring.ssl.bundle.pem来使用 PEM 编码的文本文件配置捆绑包：

```yaml
spring:
  ssl:
    bundle:
      pem:
        client:
          truststore:
            certificate: "classpath:client.crt"
```

配置这些捆绑包后，它们可以跨微服务应用 - 无论是需要安全访问数据库的库存服务、需要安全 API 调用的用户身份验证服务，还是与支付网关安全通信的支付处理服务。

Spring Boot根据 SSL Bundle 配置自动创建Java对象，如KeyStore、KeyManager和SSLContext 。这消除了手动创建和管理这些对象的需要，使过程更加简单并且不易出错。

## 3.使用 SSL 捆绑包保护RestTemplate

[让我们从在使用RestTemplate](https://www.baeldung.com/rest-template) bean的同时利用 SSL 捆绑包开始。为此，我们将使用示例Spring Boot应用程序，但首先，我们需要生成将用作 SSL 捆绑包的密钥。

我们将使用openssl二进制文件(通常与[git](https://www.baeldung.com/git-guide)一起安装)通过从项目根目录执行以下命令来生成密钥：

```bash
$ openssl req -x509 -newkey rsa:4096 -keyout src/main/resources/key.pem -out src/main/resources/cert.pem -days 365 -passout pass:FooBar
```

现在，让我们[将此密钥转换](https://www.baeldung.com/convert-pem-to-jks)为 PKCS12 格式：

```bash
$ openssl pkcs12 -export -in src/main/resources/cert.pem -inkey src/main/resources/key.pem -out src/main/resources/keystore.p12 -name secure-service -passin pass:FooBar -passout pass:FooBar
```

因此，我们拥有配置 SSL 捆绑包的一切；让我们 在application.yml文件中定义一个名为“secure-service”的包：

```yaml
spring:
 ssl:
   bundle:
     jks:
       secure-service:
         key:
           alias: "secure-service"
         keystore:
           location: "classpath:keystore.p12"
           password: "FooBar"
           type: "PKCS12"
```

接下来，我们可以通过调用setSslBundle()方法在RestTemplate上设置捆绑包：

```java
@Bean
public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder, SslBundles sslBundles) {
    return restTemplateBuilder.setSslBundle(sslBundles.getBundle("secure-service")).build();
}
```

最后，我们可以使用配置好的RestTemplate bean 来调用 API：

```java
@Service
public class SecureServiceRestApi {
    private final RestTemplate restTemplate;

    @Autowired
    public SecureServiceRestApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String fetchData(String dataId) {
        ResponseEntity<String> response = restTemplate.exchange(
          "https://secure-service.com/api/data/{id}",
          HttpMethod.GET,
          null,
          String.class,
          dataId
        );
        return response.getBody();
    }
}
```

Spring Boot 应用程序中的 SSL Bundle 用于验证secure-service的证书，确保加密且安全的通信通道。但是，这并不限制我们在 API 端使用客户端证书进行身份验证。稍后我们将看到如何获取SSLContext来配置自定义客户端。

## 4.利用Spring Boot的自动配置SSLBundles

在Spring Boot的SSL Bundles之前，开发人员通常使用支持 SSL 配置的[经典Java类：](https://www.baeldung.com/java-ssl)

- java.security.KeyStore：这些实例用作[密钥库](https://www.baeldung.com/java-keystore-truststore-difference#java-keystore)和[信任库](https://www.baeldung.com/java-keystore-truststore-difference#java-truststore)，有效地充当加密密钥和证书的安全存储库。
- javax.net.ssl.KeyManager和javax.net.ssl.TrustManager：这些实例分别管理 SSL 通信期间的密钥和信任决策。
- javax.net.ssl.SSLContext：这些实例充当SSLEngine和[SSLSocket](https://www.baeldung.com/java-ssl#ssl-socket)对象的工厂，协调 SSL 配置在运行时的实现方式。

Spring Boot 3.1 引入了分为Java接口的结构化抽象层：

- SslStoreBundle：提供通往包含加密密钥和可信证书的KeyStore对象的网关。
- SslManagerBundle：协调并提供管理KeyManager和TrustManager对象的方法。
- SslBundle：作为一站式商店，将所有这些功能聚合到与 SSL 生态系统的统一交互模型中。

随后，Spring Boot 自动配置SslBundles bean。因此，我们可以方便地将SslBundle实例注入任何[Spring Bean](https://www.baeldung.com/spring-bean)中。这对于为旧代码库和自定义 REST 客户端配置安全通信非常有用。

例如，让我们考虑自定义安全HttpClient需要自定义SSLContext：

```java
@Component
public class SecureRestTemplateConfig {
    private final SSLContext sslContext;

    @Autowired
    public SecureRestTemplateConfig(SslBundles sslBundles) throws NoSuchSslBundleException {
        SslBundle sslBundle = sslBundles.getBundle("secure-service");
        this.sslContext = sslBundle.createSslContext();
    }

    @Bean
    public RestTemplate secureRestTemplate() {
        SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create().setSslContext(this.sslContext).build();
        HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create().setSSLSocketFactory(sslSocketFactory).build();
        HttpClient httpClient = HttpClients.custom().setConnectionManager(cm).evictExpiredConnections().build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }
}
```

在上面的代码中，我们将SslBundles实例注入到[Autowired](https://www.baeldung.com/spring-autowire)构造函数中。实际上，SslBundles为我们提供了对所有配置的 SSL 捆绑包的访问。因此，我们检索安全服务包并创建上下文。稍后，我们使用SSLContext实例创建自定义HttpClient并应用它来创建RestTemplate bean。

## 5. 将 SSL 捆绑包与数据服务结合使用

不同的数据服务具有不同程度的 SSL 配置选项，从而在配置过程中造成复杂性。

SSL Bundles引入了一种更统一的方法来跨各种数据服务进行 SSL 配置：

- 卡桑德拉：spring.cassandra.ssl
- 沙发底座：spring.couchbase.env.ssl
- Elasticsearch：spring.elasticsearch.restclient.ssl
- MongoDB：spring.data.mongodb.ssl
- Redis：spring.data.redis.ssl

现在，大多数这些服务都支持.ssl.enabled属性。此属性激活客户端库中的 SSL 支持，利用Java运行时的cacerts中找到的信任材料。

此外，.ssl.bundle属性允许我们应用命名的 SSL 捆绑包来自定义信任材料，从而实现跨多个服务连接的一致性和可重用性。

对于此示例，我们假设有一个名为mongodb-ssl-bundle的 SSL 包。该捆绑包包含必要的信任材料，以确保与[MongoDB](https://www.baeldung.com/spring-data-mongodb-tutorial)实例的连接安全。

让我们定义application.yml文件：

```java
spring:
  data:
    mongodb:
      ssl:
        enabled: true
        bundle: mongodb-ssl-bundle
```

只需添加这些属性，Spring Boot 应用程序中的 MongoDB 客户端库就会自动使用mongodb-ssl-bundle中指定的 SSL 上下文和信任材料。

## 6. 将 SSL 捆绑包与嵌入式服务器一起使用

使用 SSL 捆绑包还可以简化在Spring Boot中管理嵌入式 Web 服务器的 SSL 配置。

传统上，server.ssl. 属性用于设置每个单独的 SSL 配置。通过 SSL 捆绑包，可以将配置分组在一起，然后在多个连接之间重复使用，从而减少出错的可能性并简化整体管理。

首先，让我们探讨一下传统的个人财产方法：

```yaml
server:
  ssl:
    key-alias: "server"
    key-password: "keysecret"
    key-store: "classpath:server.p12"
    key-store-password: "storesecret"
    client-auth: NEED

```

另一方面，SSL Bundle 方法允许封装相同的配置：

```yaml
spring:
  ssl:
    bundle:
      jks:
        web-server:
          key:
            alias: "server"
            password: "keysecret"
          keystore:
            location: "classpath:server.p12"
            password: "storesecret"

```

现在，我们可以使用定义的包来保护我们的 Web 服务器：

```yaml
server:
  ssl:
    bundle: "web-server"
    client-auth: NEED

```

虽然这两种方法都可以保护嵌入式 Web 服务器，但 SSL 捆绑方法对于配置管理来说更有效。此功能也可用于其他配置，例如management.server.ssl和spring.rsocket.server.ssl。

## 七、总结

在本文中，我们探索了Spring Boot中新的SSL Bundles功能，该功能可以简化和统一配置信任材料的过程。

与传统的server.ssl.属性相比，SSL 捆绑包提供了一种结构化的方式来管理密钥库和信任库。这对于减少错误配置风险和提高跨多个服务管理 SSL 的效率特别有利。

此外，SSL 捆绑包非常适合集中管理，允许在应用程序的不同部分重复使用同一个捆绑包。

通过合并 SSL 捆绑包，开发人员不仅可以简化配置过程，还可以提升Spring Boot应用程序中嵌入式 Web 服务器的安全状况。