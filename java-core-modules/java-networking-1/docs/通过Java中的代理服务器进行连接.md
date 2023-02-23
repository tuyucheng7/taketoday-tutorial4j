## 1. 概述

代理服务器充当客户端应用程序和其他服务器之间的中介。在企业环境中，我们经常使用它们来帮助控制用户通常跨越网络边界消费的内容。

在本教程中，我们将了解**如何通过Java中的代理服务器进行连接**。

首先，我们将探索更老的、更全局的方法，它在JVM范围内使用系统属性进行配置。之后，我们将介绍Proxy类，它通过允许在每个连接的基础上进行配置来为我们提供更多控制。

## 2. 设置

要运行本文中的示例，我们需要访问代理服务器。[Squid](http://www.squid-cache.org/)是一种流行的实现，可用于大多数操作系统。Squid的默认配置对于我们的大多数示例来说已经足够好了。

## 3. 使用全局设置

Java公开了一组系统属性，可用于配置JVM范围内的行为。如果适用于用例，这种“一刀切”的方法通常最容易实现。

我们可以**在调用JVM时从命令行设置所需的属性**。作为替代方案，我们也可以通过**在运行时调用System.setProperty()来设置它们**。

### 3.1 可用的系统属性

Java为HTTP、HTTPS、FTP和SOCKS协议提供了代理处理程序。可以为每个处理程序定义一个代理作为主机名和端口号：

-   **http.proxyHost**：HTTP代理服务器的主机名
-   **http.proxyPort**：HTTP代理服务器的端口号–属性是可选的，如果未提供则默认为80
-   **http.nonProxyHosts**：应该绕过代理的主机模式的竖线分隔(“|”)列表–如果设置则适用于HTTP和HTTPS处理程序
-   **socksProxyHost**：SOCKS代理服务器的主机名
-   **socksProxyPort**：SOCKS代理服务器的端口号

如果指定nonProxyHosts，主机模式可以以通配符(“*”)开头或结尾。可能需要转义Windows平台上的“|”分隔符。可以在[Oracle关于网络属性的官方Java文档](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/doc-files/net-properties.html)中找到所有可用的代理相关系统属性的详尽列表。

### 3.2 通过命令行参数设置

我们可以通过将设置作为系统属性传递来在命令行上定义代理：

```shell
java -Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=3128 proxies.networking.cn.tuyucheng.taketoday.CommandLineProxyDemo
```

当以这种方式启动一个进程时，我们可以简单地在URL上使用openConnection()而无需任何额外的工作：

```java
URL url = new URL(RESOURCE_URL);
URLConnection con = url.openConnection();
```

### 3.3 使用System.setProperty(String, String)设置

如果我们无法在命令行上设置代理属性，我们可以在我们的程序中调用System.setProperty()来设置它们：

```java
System.setProperty("http.proxyHost", "127.0.0.1");
System.setProperty("http.proxyPort", "3128");

URL url = new URL(RESOURCE_URL);
URLConnection con = url.openConnection();
// ...
```

如果我们稍后手动取消设置相关的系统属性，那么将不再使用代理：

```java
System.setProperty("http.proxyHost", null);
```

### 3.4 全局配置的限制

尽管使用具有系统属性的全局配置很容易实现，但**这种方法限制了我们可以做的事情，因为设置适用于整个JVM**。出于这个原因，为特定协议定义的设置在JVM的生命周期内是有效的，或者直到它们被取消设置。

要绕过此限制，可能很想根据需要打开和关闭设置。为了在多线程程序中安全地执行此操作，有必要采取措施来防止并发问题。

作为替代方案，**Proxy API提供了对代理配置的更精细控制**。

## 4. 使用Proxy API

Proxy类为我们提供了一种在每个连接的基础上配置代理的灵活方法。如果存在任何现有的JVM范围的代理设置，使用Proxy类的基于连接的代理设置将覆盖它们。

我们可以通过Proxy.Type定义三种类型的代理：

-   **HTTP**：使用HTTP协议的代理
-   **SOCKS**：使用SOCKS协议的代理
-   **DIRECT**：没有代理的明确配置的直接连接

### 4.1 使用HTTP代理

要使用HTTP代理，我们首先**使用Proxy和Proxy.Type.HTTP类型包装一个SocketAddress实例**。接下来，我们只需将Proxy实例传递给URLConnection.openConnection()：

```java
URL weburl = new URL(URL_STRING);
Proxy webProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 3128));
HttpURLConnection webProxyConnection = (HttpURLConnection) weburl.openConnection(webProxy);
```

简而言之，这意味着我们将连接到URL_STRING，然后通过托管在127.0.0.1:3128的代理服务器路由该连接。

### 4.2 使用直接代理

我们可能需要直接连接到主机。在这种情况下，**我们可以使用静态Proxy.NO_PROXY实例显式绕过可能在全局配置的代理**。在幕后，API为我们构造了一个新的Proxy实例，使用Proxy.Type.DIRECT作为类型：

```java
HttpURLConnection directConnection = (HttpURLConnection) weburl.openConnection(Proxy.NO_PROXY);
```

基本上，如果没有全局配置的代理，那么这与不带参数调用openConnection()是一样的。

### 4.3 使用SOCKS代理

使用URLConnection时，使用SOCKS代理类似于HTTP变体。**我们首先使用Proxy.Type.SOCKS类型用Proxy包装SocketAddress实例**。之后，我们将Proxy实例传递给URLConnection.openConnection：

```java
Proxy socksProxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 1080));
HttpURLConnection socksConnection = (HttpURLConnection) weburl.openConnection(socksProxy);
```

**连接到TCP套接字时也可以使用SOCKS代理**。首先，我们使用Proxy实例构造一个Socket。之后，我们**将目标SocketAddress实例传递给Socket.connect()**：

```java
Socket proxySocket = new Socket(socksProxy);
InetSocketAddress socketHost = new InetSocketAddress(SOCKET_SERVER_HOST, SOCKET_SERVER_PORT);
proxySocket.connect(socketHost);
```

## 5. 总结

在本文中，我们研究了如何在核心Java中使用代理服务器。

首先，我们研究了使用系统属性通过代理服务器进行连接的更旧的、更全局的连接方式。然后，我们看到了如何使用Proxy类，它在通过代理服务器连接时提供细粒度的控制。