## 1. 概述

安全通信在现代应用程序中发挥着重要作用。客户端和服务器之间通过普通HTTP进行的通信并不安全。对于生产就绪的应用程序，我们应该在应用程序中通过TLS(传输层安全)协议启用HTTPS。在本教程中，我们将讨论如何在Spring Boot应用程序中启用TLS技术。

## 2. TLS协议

TLS为客户端和服务器之间传输的数据提供保护，是HTTPS协议的关键组件。**安全套接字层(SSL)和TLS通常可以互换使用，但[它们并不相同](https://www.baeldung.com/cs/ssl-vs-tls)。事实上，TLS是SSL的继承者**。TLS可以单向或双向实现。

### 2.1 单向TLS

在单向TLS中，只有客户端验证服务器以确保它从受信任的服务器接收数据。为了实现单向TLS，服务器与客户端共享其公共证书。

### 2.2 双向TLS

在双向TLS或相互TLS(mTLS)中，客户端和服务器会相互验证，以确保参与通信的双方都是可信的。为了实现mTLS，双方彼此共享其公共证书。

## 3. 在Spring Boot中配置TLS

### 3.1 生成密钥对

要启用TLS，我们需要创建一个[公钥/私钥对](https://www.baeldung.com/java-digital-signature#getting_keypair)。为此，我们可以使用[keytool](https://www.baeldung.com/keytool-intro)。keytool命令随默认的JDK发行版一起提供。让我们使用keytool生成密钥对并将其存储在keystore.p12文件中：

```shell
keytool -genkeypair -alias baeldung -keyalg RSA -keysize 4096 \
  -validity 3650 -dname "CN=localhost" -keypass changeit -keystore keystore.p12 \
  -storeType PKCS12 -storepass changeit
```

密钥库文件可以采用不同的[格式](https://www.baeldung.com/spring-boot-https-self-signed-certificate#generating-a-self-signed-certificate)。两种最常用的格式是Java KeyStore(JKS)和PKCS#12。JKS是Java特有的格式，而PKCS#12是一种行业标准格式，属于[公钥加密标准](https://tools.ietf.org/html/rfc3447)(PKCS)下定义的标准系列。

### 3.2 在Spring中配置TLS

让我们从配置单向TLS开始。我们在application.properties文件中配置TLS相关的属性：

```properties
# enable/disable https
server.ssl.enabled=true
# keystore format
server.ssl.key-store-type=PKCS12
# keystore location
server.ssl.key-store=classpath:keystore/keystore.p12
# keystore password
server.ssl.key-store-password=changeit
```

配置SSL协议时，我们将使用TLS并告诉服务器使用TLS 1.2：

```properties
# SSL protocol to use
server.ssl.protocol=TLS
# Enabled SSL protocols
server.ssl.enabled-protocols=TLSv1.2
```

为了验证一切正常，我们只需要运行Spring Boot应用程序：

<img src="../assets/img.png">

### 3.3 在Spring中配置mTLS

为了启用mTLS，我们可以将client-auth属性配置为need：

```properties
server.ssl.client-auth=need
```

当我们使用need值时，客户端身份验证是必需的并且是强制性的。这意味着客户端和服务器都必须共享他们的公共证书。为了在Spring Boot应用程序中存储客户端的证书，我们使用truststore文件并在application.properties文件中进行配置：

```properties
#trust store location
server.ssl.trust-store=classpath:keystore/truststore.p12
#trust store password
server.ssl.trust-store-password=changeit
```

truststore位置的路径是包含机器信任的用于SSL服务器身份验证的证书颁发机构列表的文件。truststore密码是用于访问truststore文件的密码。

## 4. 在Tomcat中配置TLS

默认情况下，Tomcat启动时使用不带任何TLS功能的HTTP协议。为了在Tomcat中启用TLS，我们配置server.xml文件：

```xml
<Connector
    protocol="org.apache.coyote.http11.Http11NioProtocol"
    port="8443" maxThreads="200"
    scheme="https" secure="true" SSLEnabled="true"
    keystoreFile="${user.home}/.keystore" keystorePass="changeit"
    clientAuth="false" sslProtocol="TLS" sslEnabledProtocols="TLSv1.2"/>
```

为了启用mTLS，我们需要将clientAuth设置为true。

## 5. 调用HTTPS API

为了调用REST API，我们使用curl工具：

```shell
curl -v http://localhost:8443/tuyucheng
```

由于我们没有指定https，它将输出一个错误：

```text
Bad Request
This combination of host and port requires TLS.
```

这个问题可以通过使用https协议解决：

```shell
curl -v https://localhost:8443/tuyucheng
```

然而，这给了我们另一个错误：

```text
curl: (60) SSL certificate problem: self signed certificate
```

当我们使用自签名证书时会发生这种情况。要解决此问题，我们必须在客户端请求中使用服务器证书。首先，我们将从服务器keystore文件中复制服务器证书tuyucheng.cer。然后我们将在curl请求中使用服务器证书以及–cacert选项：

```shell
curl --cacert tuyucheng.cer https://localhost:8443/tuyucheng
```

## 6. 总结

为了确保在客户端和服务器之间传输的数据的安全性，TLS可以单向或双向实现。在本文中，我们描述了如何在application.properties文件和Tomcat配置文件中的Spring Boot应用程序中配置TLS。