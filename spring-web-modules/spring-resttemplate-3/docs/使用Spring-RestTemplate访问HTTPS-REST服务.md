## 1. 概述

在本教程中，我们将了解如何使用Spring的[RestTemplate](https://www.baeldung.com/rest-template)使用通过HTTPS保护的REST服务。

## 2. 设置

我们知道要[保护REST服务](https://www.baeldung.com/spring-boot-https-self-signed-certificate)，我们需要证书和从证书生成的密钥库。我们可以从证书颁发机构(CA)获得证书，以确保应用程序是安全的，并且对于生产级应用程序是可信的。

出于本文的目的，我们将在示例应用程序中使用自签名证书。

我们将使用Spring的RestTemplate来使用HTTPS REST服务。

首先，让我们创建一个控制器类WelcomeController和一个返回简单字符串响应的/welcome端点：

```java
@RestController
public class WelcomeController {

    @GetMapping(value = "/welcome")
    public String welcome() {
       return "Welcome To SecuredRESTService";
    }
}
```

然后，让我们在src/main/resources文件夹中添加我们的密钥库 ：

<img src="../assets/img.png">

接下来，让我们将与密钥库相关的属性添加到我们的application.properties文件中：

```properties
server.port=8443
server.servlet.context-path=/
# The format used for the keystore
server.ssl.key-store-type=PKCS12
# The path to the keystore containing the certificate
server.ssl.key-store=classpath:keystore/baeldung.p12
# The password used to generate the certificate
server.ssl.key-store-password=password
# The alias mapped to the certificate
server.ssl.key-alias=baeldung
```

我们现在可以在此端点访问REST服务：https://localhost:8443/welcome

## 3. 使用安全的REST服务

Spring提供了一个方便的RestTemplate类来使用REST服务。

虽然使用简单的REST服务很简单，但在使用安全服务时，我们需要使用服务使用的证书/密钥库自定义RestTemplate。

接下来，让我们创建一个简单的RestTemplate对象并通过添加所需的证书/密钥库对其进行自定义。

### 3.1 创建RestTemplate客户端

让我们编写一个简单的控制器，它使用RestTemplate来使用我们的REST服务：

```java
@RestController
public class RestTemplateClientController {
    private static final String WELCOME_URL = "https://localhost:8443/welcome";

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/welcomeclient")
    public String greetMessage() {
        String response = restTemplate.getForObject(WELCOME_URL, String.class);
        return response;
    }
}
```

如果我们运行我们的代码并访问/welcomeclient端点，我们将收到错误消息，因为无法找到访问安全REST服务的有效证书：

```bash
javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed: 
sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested 
target at sun.security.ssl.Alerts.getSSLException(Alerts.java:192)
```

接下来，我们将了解如何解决此错误。

### 3.2 为HTTPS访问配置RestTemplate 

访问安全REST服务的客户端应用程序应在其资源文件夹中包含一个安全密钥库。此外，还需要配置RestTemplate本身。

首先，让我们将之前的密钥库baeldung.p12添加为/src/main/resources文件夹中的信任库：

<img src="../assets/img_1.png">

接下来，我们需要在application.properties文件中添加信任库详细信息：

```properties
server.port=8082
#trust store location
trust.store=classpath:keystore/baeldung.p12
#trust store password
trust.store.password=password
```

最后，让我们通过添加信任库来自定义RestTemplate：

```java
@Configuration
public class CustomRestTemplateConfiguration {

    @Value("${trust.store}")
    private Resource trustStore;

    @Value("${trust.store.password}")
    private String trustStorePassword;

    @Bean
    public RestTemplate restTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
          CertificateException, MalformedURLException, IOException {

        SSLContext sslContext = new SSLContextBuilder()
              .loadTrustMaterial(trustStore.getURL(), trustStorePassword.toCharArray()).build();
        SSLConnectionSocketFactory sslConFactory = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslConFactory).build();
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(requestFactory);
    }
}
```

让我们详细了解上面restTemplate()方法中的重要步骤。

首先，我们创建一个表示安全套接字协议实现的SSLContext对象。我们使用SSLContextBuilder类的build()方法来创建它。

我们使用SSLContextBuilder的loadTrustMaterial()方法将密钥库文件和凭证加载到SSLContext对象中。

然后，我们通过加载SSLContext创建SSLConnectionSocketFactory，这是一个用于TSL和SSL连接的分层套接字工厂。这一步的目的是验证服务器是否正在使用我们在上一步加载的可信证书列表，即对服务器进行身份验证。

现在我们可以使用自定义的RestTemplate在端点使用安全的REST服务：http://localhost:8082/welcomeclient

<img src="../assets/img_2.png">

## 4. 总结

在本文中，我们讨论了如何使用自定义的RestTemplate使用安全的REST服务。