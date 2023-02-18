## 一、概述

我们的应用程序通常需要某种形式的连接管理以更好地利用资源。

**[在本教程中，我们将了解在Java 11 的\*HttpClient\*](https://www.baeldung.com/java-9-http-client)****中我们可以使用哪些连接管理支持**。我们将介绍使用系统属性来设置池大小和默认超时，以及使用 WireMock 来模拟不同的主机。

## 2. Java *HttpClient*的连接池

**Java 11 \*HttpClient\*有一个内部连接池**。默认情况下，它的大小是无限的。

让我们通过构建可用于发送请求的*HttpClient来查看连接池的运行情况：*

```java
HttpClient client = HttpClient.newHttpClient();
复制
```

## 3.目标服务器

我们将使用[WireMock](https://www.baeldung.com/introduction-to-wiremock)服务器作为我们的模拟主机。这使我们能够使用 Jetty 的调试日志记录来跟踪正在建立的连接。

首先，让我们看看*HttpClient*创建并重用缓存连接。让我们通过在动态端口上启动 WireMock 服务器来启动我们的模拟主机：

```java
WireMockServer server = new WireMockServer(WireMockConfiguration
  .options()
  .dynamicPort());
复制
```

在我们的*setup()*方法中，让我们启动服务器并将其配置为响应任何具有 200 响应的请求：

```java
firstServer.start();
server.stubFor(WireMock
  .get(WireMock.anyUrl())
  .willReturn(WireMock
    .aResponse()
    .withStatus(200)));复制
```

接下来，让我们创建要发送的*HttpRequest*，配置为指向我们的*WireMock*端点：

```java
HttpRequest getRequest = HttpRequest.newBuilder()
  .uri(create("http://localhost:" + server.port() + "/first";))
  .build();
复制
```

现在我们有一个客户端和一个服务器要发送到，让我们发送我们的请求：

```java
HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());复制
```

为了简单起见，我们在*HttpResponse中使用了**ofString*工厂方法。*BodyHandler*内部类来创建我们的*String*响应处理程序。

当我们运行这段代码时，我们没有看到太多的事情发生，所以让我们打开一些调试来查明我们的连接是否真的建立和重用了。

## 4. Jetty调试日志配置

*鉴于 JDK 11 的ConnectionPool*类中日志记录的稀疏性，我们需要外部日志记录来帮助我们查看连接何时被重用或创建。

**因此，让我们通过在我们的类路径中创建一个\*jetty-logging.properties\*来启用 Jetty 的调试日志记录：**

```properties
org.eclipse.jetty.util.log.class=org.eclipse.jetty.util.log.StrErrLog
org.eclipse.jetty.LEVEL=DEBUG
jetty.logs=logs复制
```

在这里，我们将 Jetty 的日志记录级别设置为 DEBUG 并将其配置为写入错误输出流。

创建新连接时，Jetty 会记录一条“New HTTP Connection”消息：

```java
DBUG:oejs.HttpConnection:qtp2037764568-17-selector-ServerConnectorManager@34b9f960/0: New HTTP Connection HttpConnection@ba7665b{IDLE}复制
```

我们可以在运行测试时查找这些消息以确认连接创建活动。

## 5. 连接池——建立与复用

现在我们有了我们的客户端，以及一个在请求新连接时记录的服务器，我们准备运行一些测试。

首先，让我们验证 *HttpClient*确实使用了内部连接池。如果有正在使用的连接池，我们只会看到一条“New HTTP Connection”消息。

因此，让我们向同一台服务器发出两个请求，看看记录了多少新的连接消息：

```java
HttpResponse<String> firstResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
HttpResponse<String> secondResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
复制
```

现在让我们检查“New HTTP Connection”消息的日志输出：

```java
DBUG:oejs.HttpConnection:qtp2037764568-17-selector-ServerConnectorManager@34b9f960/0: New HTTP Connection HttpConnection@ba7665b{IDLE}复制
```

我们看到只记录了一个新的连接请求。这告诉我们，我们发出的第二个请求不需要创建新连接。

**我们的客户端在第一次调用时建立了一个连接并将其放入池中，允许第二次调用重用相同的连接。**

那么，现在让我们看看连接池是客户端独有的还是跨客户端共享的。

让我们创建第二个客户端来检查：

```java
HttpClient secondClient = HttpClient.newHttpClient();复制
```

让我们从每个客户向同一台服务器发送请求

```java
HttpResponse<String> firstResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
HttpResponse<String> secondResponse = secondClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
复制
```

当我们检查日志输出时，我们看到创建的不是一个而是两个新连接：

```java
DBUG:oejs.HttpConnection:qtp729218894-17-selector-ServerConnectorManager@51acdf2e/0: New HTTP Connection HttpConnection@3cc85dbb{IDLE}
DBUG:oejs.HttpConnection:qtp729218894-21-selector-ServerConnectorManager@51acdf2e/1: New HTTP Connection HttpConnection@6062141{IDLE}复制
```

**我们的第二个客户端导致创建到同一目的地的新连接。由此，我们可以推断出\*每个客户端\*有一个连接池。**

## 6.控制连接池大小

现在我们已经看到了连接的创建和重用，让我们看看如何控制池的大小。

**JDK 11 \*ConnectionPool\*在初始化时检查\*jdk.httpclient.connectionPoolSize\*系统属性，默认为 0（无限制）。**

我们可以将系统属性设置为 JVM 参数或以编程方式设置。由于此属性只会在初始化时读取，但是，我们将使用 JVM 参数来确保在第一次建立任何连接时设置该值。

首先，让我们运行一个测试，首先调用一个服务器，然后是另一个，然后再次返回到第一个：

```java
HttpResponse<String> firstResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
HttpResponse<String> secondResponse = client.send(secondGet, HttpResponse.BodyHandlers.ofString());
HttpResponse<String> thirdResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
复制
```

我们在日志中只看到两个连接请求，因为我们的池仍然包含我们第一个请求中建立的连接：

```java
DBUG:oejs.HttpConnection:qtp2037764568-17-selector-ServerConnectorManager@34b9f960/0: New HTTP Connection HttpConnection@1af88cae{IDLE}
DBUG:oejs.HttpConnection:qtp1932332324-26-selector-ServerConnectorManager@13d4992d/0: New HTTP Connection HttpConnection@71c7d4f{IDLE}复制
```

现在让我们通过将池大小设置为 1 来更改此默认行为：

```java
-Djdk.httpclient.connectionPoolSize=1复制
```

我们通常不会将池大小设置为 1，但在本例中，这样做使我们能够更快地达到最大池大小。

当我们使用我们的属性集再次运行测试时，我们看到创建了三个连接：

```java
DBUG:oejs.HttpConnection:qtp2104973502-22-selector-ServerConnectorManager@48b67364/0: New HTTP Connection HttpConnection@3da6a47f{IDLE}
DBUG:oejs.HttpConnection:qtp351877391-26-selector-ServerConnectorManager@3b8f0a79/0: New HTTP Connection HttpConnection@20b59486{IDLE}
DBUG:oejs.HttpConnection:qtp2104973502-18-selector-ServerConnectorManager@48b67364/1: New HTTP Connection HttpConnection@599a00c1{IDLE}复制
```

我们的财产达到了我们预期的效果！连接池大小只有一个，当调用第二个服务器时，第一个连接将从池中清除。因此，当我们的第三次调用返回到第一个服务器时，池中不再有条目，我们必须创建一个新的第三个连接。

## 7.连接保活超时

建立连接后，它将保留在我们的池中以供重用。**如果连接闲置时间过长，那么它将从我们的连接池中清除。**

**JDK 11 \*ConnectionPool\*在初始化时检查\*jdk.httpclient.keepalive.timeout\*系统属性，默认为 1200 秒（20 分钟）。**

请注意，keepalive 超时系统属性不同于[*HttpClient*的*connectTimeout*](https://www.baeldung.com/java-httpclient-timeout)方法。连接超时决定了我们要等待多长时间才能建立新的连接，而**保持连接超时决定了连接建立后要保持多长时间**。

由于 20 分钟在现代体系结构中是一个很长的时间，JDK20，构建 26 将默认值减少到 30 秒。

让我们通过 JVM 参数设置我们的 keepalive 系统属性，将它减少到 2 秒来测试这个设置：

```java
-Djdk.httpclient.keepalive.timeout=2复制
```

现在让我们运行一个测试，休眠足够的时间以便在调用之间断开连接：

```java
HttpResponse<String> firstResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
Thread.sleep(3000);
HttpResponse<String> secondResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
复制
```

正如预期的那样，我们看到为两个请求创建了一个新连接，因为第一个连接在 2 秒后断开。

```java
DBUG:oejs.HttpConnection:qtp1889057031-18-selector-ServerConnectorManager@928763c/0: New HTTP Connection HttpConnection@7d1c0d89{IDLE}
DBUG:oejs.HttpConnection:qtp1889057031-20-selector-ServerConnectorManager@928763c/1: New HTTP Connection HttpConnection@62a8bb1d{IDLE}复制
```

## 8.增强的*HttpClient*

***自 JDK 11以来，HttpClient\*得到了进一步发展，**例如各种[网络日志记录改进](https://docs.oracle.com/en/java/javase/19/core/java-networking.html)。当我们针对 Java 19 运行这些测试时，我们可以探索*HttpClient*的内部日志来监控网络活动，而不是依赖 WireMock 的 Jetty 日志记录。还有一些关于[如何使用客户端的有用方法](https://openjdk.org/groups/net/httpclient/recipes.html)。

由于***HttpClient\*还支持使用连接多路复用的 HTTP/2 (H2) 连接**，因此我们的应用程序可能不需要使用那么多连接。因此，在 JDK20 build 25 中，专门为 H2 池引入了一些额外的系统属性：

-   *jdk.httpclient.keepalivetimeout.h2 –*设置此属性以控制 H2 连接的 keepalive 超时
-   *jdk.httpclient.maxstreams* – 设置此属性以控制每个 HTTP 连接允许的最大 H2 流数（默认为 100）。

## 9.结论

在本教程中，我们了解了 Java *HttpClient*如何重用其内部连接池中的连接。我们使用带有 Jetty 日志记录的 Wiremock 来向我们展示新的连接请求何时发出。接下来，我们学习了如何控制连接池大小以及达到池限制时的效果。我们还学习了如何配置清除空闲连接的时间。

最后，我们研究了在更新的 Java 版本中所做的一些网络更改。

当我们使用 Java 11 之前的版本或需要不同的功能时，我们的[Apache HttpClient4](https://www.baeldung.com/httpclient-connection-management)教程演示了 Java 11 客户端的替代方案。