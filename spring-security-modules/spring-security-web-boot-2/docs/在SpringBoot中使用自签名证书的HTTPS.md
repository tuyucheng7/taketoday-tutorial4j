## 1. 概述

在本教程中，我们将学习如何在Spring Boot中启用HTTPS。为此，我们还将生成一个自签名证书，并配置一个简单的应用程序。

## 2. 生成自签名证书

在开始之前，我们将创建一个自签名证书。我们使用以下任一证书格式：

+ PKCS12：公钥加密标准是一种密码保护的格式，可以包含多个证书和密钥；这是一种业界广泛使用的格式。
+ JKS：Java KeyStore类似于PKCS12；它是一种专有格式，仅限于Java环境。

我们可以使用keytool或OpenSSL工具从命令行生成证书。
Keytool是JDK所一起提供的，OpenSSL可以从[这里](https://www.openssl.org/)下载。

为了简单，我们使用keytool。

### 2.1 生成Keystore

现在我们将创建一组加密密钥，并将它们存储在密钥库中。

我们可以使用以下命令生成PKCS12密钥库格式：

```shell
keytool -genkeypair -alias tuyucheng -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore tuyucheng.p12 -validity 3650
```

我们可以在同一个密钥库中存储任意数量的密钥对，每个密钥对都由唯一的别名标识。

为了以JKS格式生成我们的密钥库，我们可以使用以下命令：

```shell
keytool -genkeypair -alias tuyucheng -keyalg RSA -keysize 2048 -keystore tuyucheng.jks -validity 3650
```

我们建议使用PKCS12格式，这是一种行业标准格式。因此，如果我们已经有JKS密钥库，我们可以使用以下命令将其转换为PKCS12格式：

```shell
keytool -importkeystore -srckeystore tuyucheng.jks -destkeystore tuyucheng.p12 -deststoretype pkcs12
```

我们必须提供源密钥库密码并设置新的密钥库密码。稍后我们需要用到别名和密钥库密码。

## 3. 在Spring Boot中启用HTTPS

Spring Boot提供了一组server.ssl.*属性。我们将在应用程序中使用这些属性来配置HTTPS。

我们从一个带有Spring Security的简单Spring Boot应用程序开始，该应用程序包含一个由“/welcome”端点处理的欢迎页面。

然后我们将上一步生成的名为“tuyucheng.p12”的文件复制到项目的“src/main/resources/keystore”目录下。

### 3.1 配置SSL属性

以下是SSL相关的属性：

```properties
server.ssl.enabled=true
# The format used for the keystore
server.ssl.key-store-type=PKCS12
# The path to the keystore containing the certificate
server.ssl.key-store=classpath:keystore/tuyucheng.p12
# The password used to generate the certificate
server.ssl.key-store-password=tu001118
# The alias mapped to the certificate
server.ssl.key-alias=tuyucheng
```

由于我们使用的是支持Spring Security的应用程序，因此我们将其配置为仅接受HTTPS请求：

```properties
server.ssl.enabled=true
```

## 4. 调用HTTPS URL

现在我们已经在应用程序中启用了HTTPS，让我们继续介绍客户端，并探索如何使用自签名证书调用HTTPS端点。

首先，我们需要创建一个信任库。由于我们已经生成了一个PKCS12文件，我们可以使用与信任库相同的文件。让我们为信任库详细信息定义新属性：

```properties
#信任库存储位置
trust.store=classpath:keystore/tuyucheng.p12
#信任库存储密码
trust.store.password=tuyucheng
```

然后我们需要准备一个带有信任库的SSLContext，并创建一个自定义的RestTemplate：

```java

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HttpsEnabledApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("ssl")
class HttpsApplicationIntegrationTest {

    private static final String WELCOME_URL = "https://localhost:8443/welcome";

    @Value("${trust.store}")
    private Resource trustStore;

    @Value("${trust.store.password}")
    private String trustStorePassword;

    RestTemplate restTemplate() throws Exception {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(trustStore.getURL(), trustStorePassword.toCharArray()).build();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }
}
```

为了演示，让我们确保Spring Security允许任何传入请求：

```java

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/**").permitAll();
    }
}
```

最后，我们可以调用HTTPS端点：

```java
class HttpsApplicationIntegrationTest {

    @Test
    void whenGETanHTTPSResource_thenCorrectResponse() throws Exception {
        ResponseEntity<String> response = restTemplate().getForEntity(WELCOME_URL, String.class, Collections.emptyMap());
        assertEquals("<h1>Welcome to Secured Site</h1>", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
```

## 5. 总结

在本文中，我们首先学习了如何生成自签名证书以在Spring Boot应用程序中启用HTTPS。然后我们介绍了如何调用支持HTTPS的端点。