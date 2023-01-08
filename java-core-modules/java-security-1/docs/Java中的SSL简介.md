## 1. 概述

在本教程中，我们将介绍 SSL 并探讨如何使用 JSSE(Java 安全套接字扩展)API 在Java中使用它。

## 2.简介

简而言之，安全套接字层 (SSL) 可在两方(通常是客户端和服务器)之间实现安全连接。

SSL 在通过网络连接运行的两个设备之间提供安全通道。SSL 的一个常见示例是启用 Web 浏览器和 Web 服务器之间的安全通信。

在这种特定情况下，网络浏览器将使用 HTTPS(S 代表安全)连接来访问不同网络服务器提供的资源。

SSL 是支持三个主要信息安全原则所必需的：

-   加密：保护各方之间的数据传输

-   身份验证：确保我们连接的服务器确实是正确的服务器

-   数据完整性：保证所请求的数据是有效交付的

Java 提供了几个基于安全的 API，帮助开发人员与客户端建立安全连接，以加密格式接收和发送消息：

-   Java 安全套接字扩展 (JSSE)
-   Java 密码体系结构 (JCA)
-   Java 加密扩展 (JCE)

在接下来的部分中，我们将介绍Java用于启用安全通信的安全套接字扩展。

## 3.JSSE接口

Java 安全 API 广泛使用工厂设计模式。

事实上，一切都是使用 JSSE 中的工厂实例化的。

### 3.1. SSLSocket工厂

javax.net.ssl.SSLSocketFactory用于创建 SSLSocket对象。

此类包含三组 API。

第一组包含一个静态getDefault()方法，用于检索默认实例，而默认实例又可以创建 SSLSocket 实例。

第二组包含五个可用于创建 SSLSocket实例的方法：

-   套接字 createSocket(String host, int port)
-   Socket createSocket(String host, int port, InetAddress clientHost, int clientPort)
-   套接字 createSocket(InetAddress host, int port)
-   套接字 createSocket(InetAddress 主机，int 端口，InetAddress clientHost，int clientPort)
-   Socket createSocket(Socket socket, String host, int port, boolean autoClose)

我们可以通过获取默认实例或使用 javax.net.ssl 直接使用此类。包含获取SSLSocketFactory实例的方法的 SSLContext 对象。

### 3.2. SSL套接字

此类扩展 Socket类并提供安全套接字。这样的套接字是普通的流套接字。

此外，它们还在底层网络传输协议上添加了一层安全保护。

SSLSocket实例在指定端口构建到指定主机的 SSL 连接。

这允许将连接的客户端绑定到给定的地址和端口。

### 3.3. SSLServerSocketFactory

SSLServerSocketFactory类与SSLSocketFactory非常相似，区别在于它创建 SSLServerSocket实例代替SSLSocket实例。

类似地，这些方法被称为createServerSocket，类似于SSLSocketFactory类。

### 3.4. SSL服务器套接字

SSLServerSocket类类似于SSLSocket类。SSLServerSocket 类上的方法是SSLSocket类方法的子集。它们作用于 SSL 连接的另一端

## 4. SSL 范例

让我们提供一个示例，说明如何创建到服务器的安全连接：

```java
String host = getHost(...);
Integer port = getPort(...);
SSLSocketFactory sslsocketfactory = SSLSocketFactory.getDefault();
SSLSocket sslsocket = (SSLSocket) sslsocketfactory
  .createSocket(host, port);
InputStream in = sslsocket.getInputStream();
OutputStream out = sslsocket.getOutputStream();

out.write(1);
while (in.available() > 0) {
    System.out.print(in.read());
}

System.out.println("Secured connection performed successfully");


```

如果我们收到错误“javax.net.ssl.SSLHandshakeException：sun.security.validator.ValidatorException：PKIX 路径构建失败：sun.security.provider.certpath.SunCertPathBuilderException：在建立SSL 连接”，它表示我们在Java信任库中没有我们尝试连接的服务器的公共证书。

信任库是包含Java用来验证安全连接的可信证书的文件。

为了解决这个问题，我们有几个选择：

-   将服务器的公共证书添加到Java 使用的默认cacerts信任库。在启动 SSL 连接时
-   设置javax.net。SSL。trustStore环境变量指向信任库文件，以便应用程序可以选择包含我们正在连接的服务器的公共证书的文件。

将新证书安装到Java默认信任库的步骤是：

1.  从服务器中提取证书：openssl s_client -connect server:443
2.  使用密钥工具将证书导入信任库：keytool -import -alias alias.server.com -keystore $JAVA_HOME/jre/lib/security/cacerts

完成此操作后，我们应该能够再次运行该示例并获得成功执行安全连接消息。

## 5.总结

在本文中，我们介绍了 SSL 和为Java实现 SSL 的 JSSE API。通过使用 SSL 和 JSSE，我们可以使我们的Java应用程序以及应用程序之间和应用程序内部的通信更加安全。