## 1. 简介

Apache HttpClient 是一个低级、轻量级的客户端 HTTP 库，用于与 HTTP 服务器通信。[在本教程中，我们将学习如何在使用HttpClient](https://www.baeldung.com/httpclient-guide)时配置受支持的传输层安全 (TLS) 版本。我们将从概述 TLS 版本协商在客户端和服务器之间的工作方式开始。之后，我们将研究使用 HttpClient 时配置支持的 TLS 版本的三种不同方式。

## 2. TLS 版本协商

TLS 是一种互联网协议，可在两方之间提供安全、可信的通信。它封装了HTTP等应用层协议。TLS 协议自 1999 年首次发布以来已经修改了数次。因此，客户端和服务器在建立新连接时首先就将使用哪个版本的 TLS 达成一致非常重要。TLS 版本在客户端和服务器交换 hello 消息后达成一致：

1.  客户端发送支持的 TLS 版本列表。
2.  服务器选择一个并将所选版本包含在响应中。
3.  客户端和服务器使用所选版本继续连接设置。

由于[降级攻击](https://en.wikipedia.org/wiki/Downgrade_attack)的风险，正确配置 Web 客户端支持的 TLS 版本非常重要。请注意，为了使用最新版本的 TLS (TLS 1.3)，我们必须使用Java11 或更高版本。

## 3.静态设置TLS版本

### 3.1. SSLConnectionSocketFactory

让我们使用HttpClients #custom构建器方法公开的HttpClientBuilder 来自定义我们的HTTPClient配置。这个构建器模式允许我们传入我们自己的SSLConnectionSocketFactory，它将使用所需的一组受支持的 TLS 版本进行实例化：

```java
SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
  SSLContexts.createDefault(),
  new String[] { "TLSv1.2", "TLSv1.3" },
  null,
  SSLConnectionSocketFactory.getDefaultHostnameVerifier());

CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
```

返回的Httpclient对象现在可以执行 HTTP 请求。通过在SSLConnectionSocketFactory构造函数中明确设置支持的协议，客户端将仅支持通过 TLS 1.2 或 TLS 1.3 进行的通信。请注意，在 4.3 之前的 Apache HttpClient 版本中，该类称为SSLSocketFactory。

### 3.2.Java运行时参数

或者，我们可以使用Java的https.protocols系统属性配置支持的 TLS 版本。此方法避免了将值硬编码到应用程序代码中。相反，我们将配置HttpClient以在设置连接时使用系统属性。HttpClient API 提供了两种方法来执行此操作。第一个是通过HttpClients#createSystem：

```java
CloseableHttpClient httpClient = HttpClients.createSystem();
```

如果需要更多的客户端配置，我们可以使用 builder 方法来代替：

```java
CloseableHttpClient httpClient = HttpClients.custom().useSystemProperties().build();
```

这两种方法都告诉HttpClient在连接配置期间使用系统属性。这允许我们在应用程序运行时使用命令行参数设置所需的 TLS 版本。例如：

```bash
$ java -Dhttps.protocols=TLSv1.1,TLSv1.2,TLSv1.3 -jar webClient.jar
```

## 4.动态设置TLS版本

也可以根据主机名和端口等连接详细信息设置 TLS 版本。我们将扩展SSLConnectionSocketFactory并覆盖prepareSocket方法。客户端在发起新连接之前调用prepareSocket方法。这将使我们决定在每个连接的基础上使用哪些 TLS 协议。也可以启用对旧 TLS 版本的支持，但前提是远程主机具有特定的子域：

```java
SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(SSLContexts.createDefault()){

    @Override
    protected void prepareSocket(SSLSocket socket) {

        String hostname = socket.getInetAddress().getHostName();
        if (hostname.endsWith("internal.system.com")){
            socket.setEnabledProtocols(new String[] { "TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3" });
        }
        else {
            socket.setEnabledProtocols(new String[] {"TLSv1.3"});
        }
    }
};<br />
CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

```

在上面的示例中，prepareSocket方法首先获取SSLSocket将连接到的远程主机名。然后使用主机名确定要启用的 TLS 协议。现在，我们的 HTTP 客户端将对每个请求强制执行 TLS 1.3，除非目标主机名的格式为  .internal.example.com。由于能够在创建新的SSLSocket之前插入自定义逻辑，我们的应用程序现在可以自定义 TLS 通信细节。

## 5.总结

在本文中，我们研究了在使用 Apache HttpClient 库时配置支持的 TLS 版本的三种不同方式。我们已经了解了如何为所有连接或基于每个连接设置 TLS 版本。