## 1. 概述

安全套接字层 (SSL) 是一种加密协议，可提供网络通信的安全性。在本教程中，我们将讨论可能导致 SSL 握手失败的各种情况及其发生方法。

请注意，我们的 [使用 JSSE 的 SSL 简介](https://www.baeldung.com/java-ssl) 更详细地介绍了 SSL 的基础知识。

## 2.术语

请务必注意，由于存在安全漏洞，作为标准的 SSL 已被传输层安全性 (TLS) 取代。大多数编程语言(包括 Java)都有支持 SSL 和 TLS 的库。

自 SSL 诞生以来，许多产品和语言(如 OpenSSL 和 Java)都引用了 SSL，即使在 TLS 接管之后，它们仍保留这些引用。出于这个原因，在本教程的其余部分，我们将使用术语 SSL 来泛指加密协议。

## 3.设置

出于本教程的目的，我们将使用[Java Socket API](https://www.baeldung.com/a-guide-to-java-sockets)来创建一个简单的服务器和客户端应用程序来模拟网络连接。

### 3.1. 创建客户端和服务器

在Java中，我们可以使用套接字在网络上建立[服务器和客户端](https://www.baeldung.com/cs/client-vs-server-terminology)之间的通信通道。套接字是Java中的Java安全套接字扩展 (JSSE) 的一部分。

让我们从定义一个简单的服务器开始：

```java
int port = 8443;
ServerSocketFactory factory = SSLServerSocketFactory.getDefault();
try (ServerSocket listener = factory.createServerSocket(port)) {
    SSLServerSocket sslListener = (SSLServerSocket) listener;
    sslListener.setNeedClientAuth(true);
    sslListener.setEnabledCipherSuites(
      new String[] { "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256" });
    sslListener.setEnabledProtocols(
      new String[] { "TLSv1.2" });
    while (true) {
        try (Socket socket = sslListener.accept()) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Hello World!");
        }
    }
}
```

上面定义的服务器返回消息“Hello World!” 到连接的客户端。

接下来，让我们定义一个基本客户端，我们将连接到我们的SimpleServer：

```java
String host = "localhost";
int port = 8443;
SocketFactory factory = SSLSocketFactory.getDefault();
try (Socket connection = factory.createSocket(host, port)) {
    ((SSLSocket) connection).setEnabledCipherSuites(
      new String[] { "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256" });
    ((SSLSocket) connection).setEnabledProtocols(
      new String[] { "TLSv1.2" });
    
    SSLParameters sslParams = new SSLParameters();
    sslParams.setEndpointIdentificationAlgorithm("HTTPS");
    ((SSLSocket) connection).setSSLParameters(sslParams);
    
    BufferedReader input = new BufferedReader(
      new InputStreamReader(connection.getInputStream()));
    return input.readLine();
}
```

我们的客户端打印服务器返回的消息。

### 3.2. 在Java中创建证书

SSL 在网络通信中提供保密性、完整性和真实性。就建立真实性而言，证书起着重要作用。

通常，这些证书由证书颁发机构购买和签名，但在本教程中，我们将使用自签名证书。

为此，我们可以使用 JDK 附带的keytool ：

```powershell
$ keytool -genkey -keypass password 
                  -storepass password 
                  -keystore serverkeystore.jks
```

上面的命令启动一个交互式 shell 来收集证书的信息，如公用名 (CN) 和可分辨名称 (DN)。当我们提供所有相关细节时，它会生成文件 serverkeystore.jks，其中包含服务器的私钥及其公共证书。

请注意， serverkeystore.jks 以Java专有的JavaKey Store (JKS) 格式存储。这些天，keytool 会提醒我们应该考虑使用它也支持的 PKCS#12。

我们可以进一步使用keytool 从生成的密钥库文件中提取公共证书：

```powershell
$ keytool -export -storepass password 
                  -file server.cer 
                  -keystore serverkeystore.jks
```

上面的命令将公共证书从密钥库导出为文件server.cer。让我们通过将导出的证书添加到其信任库来为客户端使用它：

```powershell
$ keytool -import -v -trustcacerts 
                     -file server.cer 
                     -keypass password 
                     -storepass password 
                     -keystore clienttruststore.jks
```

我们现在已经为服务器生成了一个密钥库，并为客户端生成了相应的信任库。当我们讨论可能的握手失败时，我们将回顾这些生成文件的使用。

有关Java密钥库用法的更多详细信息，请参阅我们[之前的教程](https://www.baeldung.com/java-keystore)。

## 4.SSL握手

SSL 握手是一种机制，客户端和服务器通过这种机制建立信任和物流，以确保它们在网络上的连接安全。

这是一个精心策划的过程，了解其中的细节有助于理解为什么它经常失败，我们打算在下一节中介绍。

SSL 握手的典型步骤是：

1.  客户端提供可能使用的 SSL 版本和密码套件的列表
2.  服务器同意特定的 SSL 版本和密码套件，并用其证书进行响应
3.  客户端从证书中提取公钥以加密的“预主密钥”响应
4.  服务器使用其私钥解密“预主密钥”
5.  客户端和服务器使用交换的“预主密钥”计算“共享秘密”
6.  客户端和服务器使用“共享密钥”交换消息确认加密和解密成功

虽然大多数步骤对于任何 SSL 握手都是相同的，但单向和双向 SSL 之间存在细微差别。让我们快速回顾一下这些差异。

### 4.1. 单向 SSL 中的握手

如果我们参考上面提到的步骤，第二步提到了证书交换。单向 SSL 要求客户端可以通过其公共证书信任服务器。这使服务器信任所有请求连接的客户端。服务器无法从客户端请求和验证可能带来安全风险的公共证书。

### 4.2. 双向 SSL 中的握手

使用单向 SSL，服务器必须信任所有客户端。但是，双向 SSL 增加了服务器也能够建立可信客户端的能力。在[双向握手](https://www.baeldung.com/cs/handshakes)期间，客户端和服务器都必须出示并接受彼此的公共证书，才能成功建立连接。

## 5. 握手失败场景

完成快速审查后，我们可以更清楚地查看故障场景。

单向或双向通信中的 SSL 握手可能因多种原因而失败。我们将逐一分析这些原因，模拟故障并了解我们如何避免此类情况。

在这些场景中的每一个中，我们都将使用 我们之前创建的SimpleClient 和SimpleServer。

### 5.1. 缺少服务器证书

让我们尝试运行 SimpleServer并通过SimpleClient连接它。虽然我们希望看到消息“Hello World!”，但出现了一个异常：

```powershell
Exception in thread "main" javax.net.ssl.SSLHandshakeException: 
  Received fatal alert: handshake_failure
```

现在，这表明出了点问题。上面的SSLHandshakeException以抽象的方式说明客户端在连接到服务器时没有收到任何证书。

为了解决这个问题，我们将使用我们之前生成的密钥库，将它们作为系统属性传递给服务器：

```powershell
-Djavax.net.ssl.keyStore=serverkeystore.jks -Djavax.net.ssl.keyStorePassword=password
```

请务必注意，密钥库文件路径的系统属性应该是绝对路径，或者密钥库文件应该放在调用Java命令以启动服务器的同一目录中。密钥库的Java系统属性不支持相对路径。

这是否有助于我们获得预期的输出？让我们在下一个小节中找出答案。

### 5.2. 不受信任的服务器证书

当我们使用上一小节中的更改再次运行SimpleServer和SimpleClient时，我们得到的输出是什么：

```powershell
Exception in thread "main" javax.net.ssl.SSLHandshakeException: 
  sun.security.validator.ValidatorException: 
  PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: 
  unable to find valid certification path to requested target
```

好吧，它并没有完全按照我们的预期工作，但看起来它由于不同的原因而失败了。

此特定故障是由于我们的服务器使用的是未由证书颁发机构 (CA) 签名的自签名证书。

真的，任何时候证书是由默认信任库中以外的东西签名的，我们都会看到这个错误。JDK 中的默认信任库通常附带有关正在使用的常见 CA 的信息。

要在这里解决这个问题，我们必须强制SimpleClient信任SimpleServer提供的证书。让我们通过将它们作为系统属性传递给客户端来使用我们之前生成的信任库：

```powershell
-Djavax.net.ssl.trustStore=clienttruststore.jks -Djavax.net.ssl.trustStorePassword=password
```

请注意，这不是一个理想的解决方案。在理想情况下，我们不应该使用自签名证书，而应该使用经过客户端默认信任的证书颁发机构 (CA) 认证的证书。

让我们转到下一个小节，看看我们现在是否获得了预期的输出。

### 5.3. 缺少客户端证书

让我们再试一次运行 SimpleServer 和 SimpleClient，应用前面小节中的更改：

```powershell
Exception in thread "main" java.net.SocketException: 
  Software caused connection abort: recv failed
```

同样，这不是我们所期望的。这里的SocketException 告诉我们服务器不能信任客户端。这是因为我们设置了双向 SSL。在我们的 SimpleServer 中，我们有：

```java
((SSLServerSocket) listener).setNeedClientAuth(true);
```

上面的代码表明 SSLServerSocket需要通过其公共证书进行客户端身份验证。

我们可以为客户端创建一个密钥库，并为服务器创建一个相应的信任库，其方式类似于我们在之前创建密钥库和信任库时使用的方法。

我们将重新启动服务器并向其传递以下系统属性：

```powershell
-Djavax.net.ssl.keyStore=serverkeystore.jks 
    -Djavax.net.ssl.keyStorePassword=password 
    -Djavax.net.ssl.trustStore=clienttruststore.jks 
    -Djavax.net.ssl.trustStorePassword=password
```

然后，我们将通过传递这些系统属性来重新启动客户端：

```powershell
-Djavax.net.ssl.keyStore=serverkeystore.jks 
    -Djavax.net.ssl.keyStorePassword=password 
    -Djavax.net.ssl.trustStore=clienttruststore.jks 
    -Djavax.net.ssl.trustStorePassword=password
```

最后，我们得到了我们想要的输出：

```powershell
Hello World!
```

### 5.4. 不正确的证书

除了上述错误之外，由于与我们创建证书的方式相关的各种原因，握手可能会失败。一个常见错误与不正确的 CN 有关。让我们探索一下我们之前创建的服务器密钥库的细节：

```powershell
keytool -v -list -keystore serverkeystore.jks
```

当我们运行上面的命令时，我们可以看到密钥库的详细信息，特别是所有者：

```powershell
...
Owner: CN=localhost, OU=technology, O=baeldung, L=city, ST=state, C=xx
...
```

此证书所有者的 CN 设置为 localhost。所有者的 CN 必须与服务器的主机完全匹配。如果有任何不匹配，将导致 SSLHandshakeException。

让我们尝试使用 CN 作为 localhost 以外的任何内容重新生成服务器证书。当我们现在使用重新生成的证书来运行SimpleServer和SimpleClient时，它会立即失败：

```powershell
Exception in thread "main" javax.net.ssl.SSLHandshakeException: 
    java.security.cert.CertificateException: 
    No name matching localhost found
```

上面的异常跟踪清楚地表明客户端期待一个名称为 localhost 的证书，但它没有找到。

请注意，默认情况下 JSSE 不强制执行主机名验证。我们通过显式使用 HTTPS在SimpleClient中启用了主机名验证：

```java
SSLParameters sslParams = new SSLParameters();
sslParams.setEndpointIdentificationAlgorithm("HTTPS");
((SSLSocket) connection).setSSLParameters(sslParams);
```

主机名验证是失败的常见原因，通常应始终强制执行以提高安全性。有关主机名验证及其在使用 TLS 的安全性中的重要性的详细信息，请参阅[本文](https://tersesystems.com/blog/2014/03/23/fixing-hostname-verification/)。

### 5.5. 不兼容的 SSL 版本

目前，有各种加密协议，包括不同版本的 SSL 和 TLS 在运行。

如前所述，一般来说，SSL 已因其加密强度而被 TLS 取代。加密协议和版本是客户端和服务器在握手期间必须达成一致的附加元素。

例如，如果服务器使用 SSL3 的加密协议，而客户端使用 TLS1.3，则它们无法就加密协议达成一致， 将生成SSLHandshakeException 。

在我们的 SimpleClient中，让我们将协议更改为与服务器协议集不兼容的协议：

```java
((SSLSocket) connection).setEnabledProtocols(new String[] { "TLSv1.1" });
```

当我们再次运行我们的客户端时，我们将得到一个SSLHandshakeException：

```powershell
Exception in thread "main" javax.net.ssl.SSLHandshakeException: 
  No appropriate protocol (protocol is disabled or cipher suites are inappropriate)
```

这种情况下的异常跟踪是抽象的，并没有告诉我们确切的问题。要解决这些类型的问题，有必要验证客户端和服务器是否使用相同或兼容的加密协议。

### 5.6. 不兼容的密码套件

客户端和服务器还必须就它们将用于加密消息的密码套件达成一致。

在握手期间，客户端将提供可能使用的密码列表，服务器将使用列表中选定的密码进行响应。如果无法选择合适的密码，服务器将生成 SSLHandshakeException 。

在我们的 SimpleClient中，让我们将密码套件更改为与我们的服务器使用的密码套件不兼容的东西：

```java
((SSLSocket) connection).setEnabledCipherSuites(
  new String[] { "TLS_RSA_WITH_AES_128_GCM_SHA256" });
```

当我们重新启动我们的客户端时，我们将得到一个SSLHandshakeException：

```powershell
Exception in thread "main" javax.net.ssl.SSLHandshakeException: 
  Received fatal alert: handshake_failure
```

同样，异常跟踪非常抽象，并没有告诉我们确切的问题。解决此类错误的方法是验证客户端和服务器使用的已启用密码套件，并确保至少有一个通用套件可用。

通常，客户端和服务器配置为使用各种密码套件，因此不太可能发生此错误。如果我们遇到此错误，通常是因为服务器已配置为使用非常有选择性的密码。出于安全原因，服务器可能会选择强制执行一组有选择的密码。

## 六，总结

在本教程中，我们学习了如何使用Java套接字设置 SSL。然后我们讨论了使用单向和双向 SSL 的 SSL 握手。最后，我们列出了 SSL 握手可能失败的可能原因并讨论了解决方案。