## 1. 概述

在本文中，我们将看到如何初始化和配置OkHttpClient以信任自签名证书。为此，我们将设置一个最小的支持 HTTPS 的Spring Boot应用程序，该应用程序由自签名证书保护。

有关该库的更多细节，请参阅我们[关于 OkHttp 的文章集。](https://www.baeldung.com/tag/okhttp/)

## 2. 基本原理

在我们深入研究负责完成这项工作的代码之前，让我们先了解一下底线。SSL的[本质](https://www.baeldung.com/java-ssl)是它在任何两方(通常是客户端和服务器)之间建立安全连接。此外，它有助于保护通过网络传输的数据的隐私和完整性。

[JSSE API](https://docs.oracle.com/en/java/javase/11/security/java-secure-socket-extension-jsse-reference-guide.html)是JavaSE 的一个安全组件，它为 SSL/TLS 协议提供完整的 API 支持。

SSL 证书，又名数字证书，在建立 TLS 握手、促进通信双方之间的加密和信任方面起着至关重要的作用。自签名 SSL 证书不是由知名且受信任的证书颁发机构 (CA) 颁发的证书。它们可以很容易地由开发人员[生成和签名](https://www.baeldung.com/openssl-self-signed-cert)，以便为他们的软件启用 HTTPS。

由于自签名证书不可信，浏览器和标准 HTTPS 客户端(如[OkHttp](https://www.baeldung.com/guide-to-okhttp)和[Apache HTTP Client](https://www.baeldung.com/httpclient-ssl) )默认情况下都不信任它们。

[最后，我们可以使用 Web 浏览器或 OpenSSL 命令行实用程序](https://www.baeldung.com/linux/ssl-certificates)方便地获取服务器证书。

## 3. 搭建测试环境

为了演示使用 OkHttp 接受和信任自签名证书的应用程序，让我们快速配置和启动一个启用 HTTPS(由自签名证书保护)[的简单Spring Boot应用程序。](https://www.baeldung.com/spring-boot-https-self-signed-certificate)

默认配置将启动 Tomcat 服务器侦听端口 8443 并公开可在“https://localhost:8443/welcome”访问的安全 REST API 。

现在，让我们使用 OkHttp 客户端向该服务器发出 HTTPS 请求并使用“/welcome” API。

## 4.OkHttpClient和SSL

本节将初始化一个OkHttpClient，并使用它来连接我们刚刚搭建的测试环境。此外，我们将检查路径中遇到的错误，并逐步实现使用 OkHttp 信任自签名证书的最终目标。

首先，让我们为OkHttpClient创建一个构建器：

```java
OkHttpClient.Builder builder = new OkHttpClient.Builder();
```

另外，让我们声明我们将在整个教程中使用的 HTTPS URL：

```java
int SSL_APPLICATION_PORT = 8443;
String HTTPS_WELCOME_URL = "https://localhost:" + SSL_APPLICATION_PORT + "/welcome";
```

### 4.1. SSLHandshakeException _

在没有为 SSL 配置OkHttpClient的情况下，如果我们尝试使用 HTTPS URL，我们会得到一个安全异常：

```java
@Test(expected = SSLHandshakeException.class)
public void whenHTTPSSelfSignedCertGET_thenException() {
    builder.build()
    .newCall(new Request.Builder()
    .url(HTTPS_WELCOME_URL).build())
    .execute();
}
```

堆栈跟踪是：

```java
javax.net.ssl.SSLHandshakeException: PKIX path building failed: 
    sun.security.provider.certpath.SunCertPathBuilderException:
    unable to find valid certification path to requested target
    ...
```

上述错误恰恰意味着服务器使用了未由证书颁发机构 (CA) 签名的自签名证书。

因此，客户端无法验证直至根证书[的信任链](https://docs.oracle.com/cd/E19146-01/821-1828/ginal/index.html)，因此抛出[SSLHandshakeException](https://www.baeldung.com/java-ssl-handshake-failures)。

### 4.2. SSLPeerUnverifiedException _

现在，让我们配置信任证书的OkHttpClient，无论其性质如何——CA 签名或自签名。

首先，我们需要创建自己的[TrustManager](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/javax/net/ssl/X509TrustManager.html) 来取消默认证书验证并使用我们的自定义实现覆盖它们：

```java
TrustManager TRUST_ALL_CERTS = new X509TrustManager() {
    @Override
    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
    }

    @Override 
    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
    }

    @Override
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return new java.security.cert.X509Certificate[] {};
    }
};
```

接下来，我们将使用上面的TrustManager来初始化[SSLContext](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/javax/net/ssl/SSLContext.html)，并设置OkHttpClient构建器的[SSLSocketFactory](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/javax/net/ssl/SSLSocketFactory.html)：

```java
SSLContext sslContext = SSLContext.getInstance("SSL");
sslContext.init(null, new TrustManager[] { TRUST_ALL_CERTS }, new java.security.SecureRandom());
builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) TRUST_ALL_CERTS);
```

再次，让我们运行测试。不难相信，即使经过上述调整，使用 HTTPS URL 也会引发错误：

```java
@Test(expected = SSLPeerUnverifiedException.class)
public void givenTrustAllCerts_whenHTTPSSelfSignedCertGET_thenException() {
    // initializing the SSLContext and set the sslSocketFactory
    builder.build()
        .newCall(new Request.Builder()
        .url(HTTPS_WELCOME_URL).build())
        .execute();
}
```

确切的错误是：

```java
javax.net.ssl.SSLPeerUnverifiedException: Hostname localhost not verified:
    certificate: sha256/bzdWeeiDwIVjErFX98l+ogWy9OFfBJsTRWZLB/bBxbw=
    DN: CN=localhost, OU=localhost, O=localhost, L=localhost, ST=localhost, C=IN
    subjectAltNames: []
```

这是由于一个众所周知的问题——[主机名验证失败](https://tersesystems.com/blog/2014/03/23/fixing-hostname-verification/)。大多数HTTP 库针对证书的 SubjectAlternativeName 的 DNS Name 字段执行主机名验证，这在服务器的自签名证书中不可用，如上面的详细堆栈跟踪所示。

### 4.3. 覆盖HostnameVerifier

正确配置OkHttpClient的最后一步是禁用默认的HostnameVerifier并将其替换为绕过主机名验证的另一个。

让我们加入最后一部分定制：

```java
builder.hostnameVerifier(new HostnameVerifier() {
    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
});

```

现在，让我们最后一次运行我们的测试：

```java
@Test
public void givenTrustAllCertsSkipHostnameVerification_whenHTTPSSelfSignedCertGET_then200OK() {
    // initializing the SSLContext and set the sslSocketFactory
    // set the custom hostnameVerifier
    Response response = builder.build()
        .newCall(new Request.Builder()
        .url(HTTPS_WELCOME_URL).build())
        .execute();
    assertEquals(200, response.code());
    assertNotNull(response.body());
    assertEquals("<h1>Welcome to Secured Site</h1>", response.body()
        .string());
}
```

最后，OkHttpClient能够成功使用由自签名证书保护的 HTTPS URL。

## 5.总结

在本教程中，我们了解了如何为OkHttpClient配置 SSL， 以便它能够信任自签名证书并使用任何 HTTPS URL。

然而，需要考虑的重要一点是，尽管这种设计完全省略了证书验证和主机名验证，但客户端和服务器之间的所有通信仍然是加密的。两方之间的信任丢失，但 SSL 握手和加密没有受到损害。