## 1. 概述

当谈到 SSL 连接时，我们应该使用 TLSv1.2。实际上，它是Java8 的默认 SSL 协议。

虽然Java7 支持 TLSv1.2，但 默认的是 TLS v1.0，现在它太弱了。

在本教程中，我们将讨论配置Java7 以使用 TLSv1.2 的各种选项。

## 2. 使用JavaVM 参数

如果我们使用Java1.7.0_95 或更高版本，我们可以添加 jdk.tls.client.protocols 属性作为java 命令行参数以支持 TLSv1.2：

```bash
java -Djdk.tls.client.protocols=TLSv1.2 <Main class or the Jar file to run>
```

但是Java1.7.0_95 只提供给从 Oracle 购买支持的客户。因此，我们将在下面查看其他选项以在Java7 上启用 TLSv1.2。

## 3.使用SSLSocket

[在第一个示例中，我们将使用SSLSocketFactory](https://www.baeldung.com/java-ssl)启用 TLSv1.2 。

首先，我们可以通过调用SSLSocketFactory# getDefault工厂方法来创建一个默认的SSLSocketFactory对象 。

然后，我们只需将我们的主机和端口传递给SSLSocket# createSocket：

```java
SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
SSLSocket sslSocket = (SSLSocket) socketFactory.createSocket(hosturl, port);
```

上面创建的默认SSLSocket没有与之关联的任何 SSL 协议。我们可以通过几种方式将 SSL 协议关联到我们的 SSLSocket 。

在第一种方法中，我们可以将支持的 SSL 协议数组传递给SSLSocket 实例上的 setEnabledProtocols方法：

```java
sslSocket.setEnabledProtocols(new String[] {"TLSv1.2"});
```

或者，我们可以使用SSLParameters，使用相同的数组：

```java
SSLParameters params = new SSLParameters();
params.setProtocols(new String[] {"TLSv1.2"});
sslSocket.setSSLParameters(params);
```

## 4.使用SSLContext

直接设置 SSLSocket 只会改变一个连接。我们可以使用 SSLContext 来改变我们创建 SSLSocketFactory 的方式 。

因此，我们不使用SSLSocketFactory#getInstance，而是使用 SSLContext#getInstance， 将“ TLSv1.2 ”作为参数。我们现在 可以从中获取我们的 SSLSocketFactory ：

```java
SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
sslContext.init(null, null, new SecureRandom());
SSLSocketFactory socketFactory = sslContext.getSocketFactory();
SSLSocket socket = (SSLSocket) socketFactory.createSocket(url, port);
```

作为一个简短的旁注， 在使用 SSL 时始终记得使用SecureRandom 。

## 5. 使用HttpsURLConnection

当然，我们并不总是直接创建套接字。通常，我们处于应用程序协议级别。

所以，最后，让我们看看如何在[HttpsURLConnection](https://www.baeldung.com/java-http-request)上启用 TLSv1.2 。

首先，我们需要一个 URL实例。假设我们正在连接到[https://example.org](https://example.org/)：

```java
URL url = new URL("https://" + hosturl + ":" + port);
```

现在，我们可以像以前一样设置我们的SSLContext：

```java
SSLContext sslContext = SSLContext.getInstance("TLSv1.2"); 
sslContext.init(null, null, new SecureRandom());
```

然后，我们的最后一步是创建连接并为其提供SSLSocketFactory：

```java
HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
connection.setSSLSocketFactory(sslContext.getSocketFactory());
```

## 六，总结

在这篇快速文章中，我们展示了几种在Java7 上启用 TLSv1.2 的方法。