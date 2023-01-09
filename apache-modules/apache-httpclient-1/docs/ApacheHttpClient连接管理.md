## 1. 概述

在本教程中，我们将介绍 HttpClient 4 中连接管理的基础知识。

我们将介绍BasicHttpClientConnectionManager和PoolingHttpClientConnectionManager的使用，以强制安全、符合协议且高效地使用 HTTP 连接。

## 2.用于低级单线程连接的BasicHttpClientConnectionManager

BasicHttpClientConnectionManager自 HttpClient 4.3.3 起可用，作为 HTTP 连接管理器的最简单实现。

我们用它来创建和管理一次只能有一个线程使用的单个连接。

## 延伸阅读：

## [高级 Apache HttpClient 配置](https://www.baeldung.com/httpclient-advanced-config)

高级用例的 HttpClient 配置。

[阅读更多](https://www.baeldung.com/httpclient-advanced-config)→

## [Apache HttpClient – 发送自定义 Cookie](https://www.baeldung.com/httpclient-cookies)

如何使用 Apache HttpClient 发送自定义 Cookie。

[阅读更多](https://www.baeldung.com/httpclient-cookies)→

## [带 SSL 的 Apache HttpClient](https://www.baeldung.com/httpclient-ssl)

如何使用 SSL 配置 HttpClient 的示例。

[阅读更多](https://www.baeldung.com/httpclient-ssl)→


示例 2.1。获取低级别连接的连接请求 ( HttpClientConnection )



```java
BasicHttpClientConnectionManager connManager
 = new BasicHttpClientConnectionManager();
HttpRoute route = new HttpRoute(new HttpHost("www.baeldung.com", 80));
ConnectionRequest connRequest = connManager.requestConnection(route, null);
```

requestConnection方法从管理器获取特定路由连接的连接池。路由参数指定到目标主机或目标主机本身的“代理跃点”路由。

可以直接使用HttpClientConnection运行请求。但是，请记住，这种低级方法冗长且难以管理。低级连接对于访问套接字和连接数据(如超时和目标主机信息)很有用。但是对于标准执行，HttpClient是一个更容易处理的 API。

## 3. 使用PoolingHttpClientConnectionManager获取和管理多线程连接池

PoolingHttpClientConnectionManager将为我们使用的每个路由或目标主机创建和管理连接池。管理器可以打开的并发连接池的默认大小是每个路由或目标主机两个， 打开的连接总数为 20 个。

首先，让我们看一下如何在一个简单的 HttpClient 上设置这个连接管理器：

示例 3.1。在 HttpClient 上设置 PoolingHttpClientConnectionManager

```java
HttpClientConnectionManager poolingConnManager
  = new PoolingHttpClientConnectionManager();
CloseableHttpClient client
 = HttpClients.custom().setConnectionManager(poolingConnManager)
 .build();
client.execute(new HttpGet("/"));
assertTrue(poolingConnManager.getTotalStats().getLeased() == 1);
```

接下来，让我们看看在两个不同线程中运行的两个 HttpClient 如何使用同一个连接管理器：

示例 3.2。使用两个 HttpClient 分别连接一个目标主机

```java
HttpGet get1 = new HttpGet("/");
HttpGet get2 = new HttpGet("http://google.com"); 
PoolingHttpClientConnectionManager connManager 
  = new PoolingHttpClientConnectionManager(); 
CloseableHttpClient client1 
  = HttpClients.custom().setConnectionManager(connManager).build();
CloseableHttpClient client2 
  = HttpClients.custom().setConnectionManager(connManager).build();

MultiHttpClientConnThread thread1
 = new MultiHttpClientConnThread(client1, get1); 
MultiHttpClientConnThread thread2
 = new MultiHttpClientConnThread(client2, get2); 
thread1.start();
thread2.start();
thread1.join();
thread2.join();
```

请注意，我们使用的是一个非常简单的自定义线程实现：

示例 3.3。执行 GET 请求的自定义线程

```java
public class MultiHttpClientConnThread extends Thread {
    private CloseableHttpClient client;
    private HttpGet get;
    
    // standard constructors
    public void run(){
        try {
            HttpResponse response = client.execute(get);  
            EntityUtils.consume(response.getEntity());
        } catch (ClientProtocolException ex) {    
        } catch (IOException ex) {
        }
    }
}
```

注意EntityUtils.consume(response.getEntity)调用。这是使用响应(实体)的全部内容所必需的，以便管理器可以将连接释放回池中。

## 4.配置连接管理器

池连接管理器的默认值选择得很好。但是，根据我们的用例，它们可能太小了。

那么，让我们看看如何配置

-   连接总数
-   每个(任何)路由的最大连接数
-   每条特定路由的最大连接数

例 4.1。将可以打开和管理的连接数增加到默认限制之外

```java
PoolingHttpClientConnectionManager connManager 
  = new PoolingHttpClientConnectionManager();
connManager.setMaxTotal(5);
connManager.setDefaultMaxPerRoute(4);
HttpHost host = new HttpHost("www.baeldung.com", 80);
connManager.setMaxPerRoute(new HttpRoute(host), 5);
```

让我们回顾一下 API：

-   setMaxTotal(int max) – 设置最大总打开连接数
-   setDefaultMaxPerRoute(int max) – 设置每个路由的最大并发连接数，默认为两个
-   setMaxPerRoute(int max) – 设置特定路由的并发连接总数，默认为2

因此，在不更改默认值的情况下，我们将很容易达到连接管理器的限制。

让我们看看它是什么样的：

例 4.2。使用线程执行连接

```java
HttpGet get = new HttpGet("http://www.baeldung.com");
PoolingHttpClientConnectionManager connManager 
  = new PoolingHttpClientConnectionManager();
CloseableHttpClient client = HttpClients.custom().
    setConnectionManager(connManager).build();
MultiHttpClientConnThread thread1 
  = new MultiHttpClientConnThread(client, get);
MultiHttpClientConnThread thread2 
  = new MultiHttpClientConnThread(client, get);
MultiHttpClientConnThread thread3 
  = new MultiHttpClientConnThread(client, get);
thread1.start();
thread2.start();
thread3.start();
thread1.join();
thread2.join();
thread3.join();
```

请记住，默认情况下每个主机连接限制为两个。所以，在这个例子中，我们希望三个线程向同一个主机发出三个请求，但是只会并行分配两个连接。

让我们看一下日志。

我们有三个线程在运行，但只有两个租用连接：

```bash
[Thread-0] INFO  o.b.h.c.MultiHttpClientConnThread
 - Before - Leased Connections = 0
[Thread-1] INFO  o.b.h.c.MultiHttpClientConnThread
 - Before - Leased Connections = 0
[Thread-2] INFO  o.b.h.c.MultiHttpClientConnThread
 - Before - Leased Connections = 0
[Thread-2] INFO  o.b.h.c.MultiHttpClientConnThread
 - After - Leased Connections = 2
[Thread-0] INFO  o.b.h.c.MultiHttpClientConnThread
 - After - Leased Connections = 2
```

## 5.连接保活策略

根据HttpClient 4.3.3。参考：“如果`Keep-Alive`响应中不存在标头，则HttpClient假定连接可以无限期保持活动状态。” ([请参阅 HttpClient 参考](https://hc.apache.org/httpcomponents-client-4.5.x/current/tutorial/pdf/httpclient-tutorial.pdf))。

为了解决这个问题并能够管理死连接，我们需要一个定制的策略实现并将其构建到HttpClient中。

示例 5.1。自定义保持活动策略

```java
ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
    @Override
    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
        HeaderElementIterator it = new BasicHeaderElementIterator
            (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
        while (it.hasNext()) {
            HeaderElement he = it.nextElement();
            String param = he.getName();
            String value = he.getValue();
            if (value != null && param.equalsIgnoreCase
               ("timeout")) {
                return Long.parseLong(value)  1000;
            }
        }
        return 5  1000;
    }
};
```

该策略将首先尝试应用标头中声明的主机的Keep-Alive策略。如果响应标头中不存在该信息，它将使连接保持活动状态五秒钟。

现在让我们使用此自定义策略创建一个客户端：

```java
PoolingHttpClientConnectionManager connManager 
  = new PoolingHttpClientConnectionManager();
CloseableHttpClient client = HttpClients.custom()
  .setKeepAliveStrategy(myStrategy)
  .setConnectionManager(connManager)
  .build();
```

## 6.连接持久化/重用

HTTP/1.1 规范指出，如果连接尚未关闭，我们可以重用连接。这称为连接持久性。

一旦管理器释放连接，它就会保持打开状态以供重用。

当使用只能管理单个连接的BasicHttpClientConnectionManager时，连接必须在再次租回之前释放：

示例 6.1。BasicHttpClientConnectionManager 连接重用

```java
BasicHttpClientConnectionManager basicConnManager = 
    new BasicHttpClientConnectionManager();
HttpClientContext context = HttpClientContext.create();

// low level
HttpRoute route = new HttpRoute(new HttpHost("www.baeldung.com", 80));
ConnectionRequest connRequest = basicConnManager.requestConnection(route, null);
HttpClientConnection conn = connRequest.get(10, TimeUnit.SECONDS);
basicConnManager.connect(conn, route, 1000, context);
basicConnManager.routeComplete(conn, route, context);

HttpRequestExecutor exeRequest = new HttpRequestExecutor();
context.setTargetHost((new HttpHost("www.baeldung.com", 80)));
HttpGet get = new HttpGet("http://www.baeldung.com");
exeRequest.execute(get, conn, context);

basicConnManager.releaseConnection(conn, null, 1, TimeUnit.SECONDS);

// high level
CloseableHttpClient client = HttpClients.custom()
  .setConnectionManager(basicConnManager)
  .build();
client.execute(get);
```

让我们来看看会发生什么。

请注意，我们首先使用低级连接，这样我们就可以完全控制何时释放连接，然后使用 HttpClient 进行正常的高级连接。

复杂的低级逻辑在这里不是很相关。我们唯一关心的是releaseConnection调用。这释放了唯一可用的连接并允许它被重用。

然后客户端再次运行 GET 请求并成功。

如果我们跳过释放连接，我们将从 HttpClient 得到一个 IllegalStateException：

```bash
java.lang.IllegalStateException: Connection is still allocated
  at o.a.h.u.Asserts.check(Asserts.java:34)
  at o.a.h.i.c.BasicHttpClientConnectionManager.getConnection
    (BasicHttpClientConnectionManager.java:248)
```

请注意，现有连接并未关闭，只是被释放，然后由第二个请求重新使用。

与上面的示例相反，PoolingHttpClientConnectionManager允许透明地重用连接，而无需隐式释放连接：

示例 6.2。PoolingHttpClientConnectionManager：重用线程连接

```java
HttpGet get = new HttpGet("http://echo.200please.com");
PoolingHttpClientConnectionManager connManager 
  = new PoolingHttpClientConnectionManager();
connManager.setDefaultMaxPerRoute(5);
connManager.setMaxTotal(5);
CloseableHttpClient client = HttpClients.custom()
  .setConnectionManager(connManager)
  .build();
MultiHttpClientConnThread[] threads 
  = new  MultiHttpClientConnThread[10];
for(int i = 0; i < threads.length; i++){
    threads[i] = new MultiHttpClientConnThread(client, get, connManager);
}
for (MultiHttpClientConnThread thread: threads) {
     thread.start();
}
for (MultiHttpClientConnThread thread: threads) {
     thread.join(1000);     
}
```

上面的示例有 10 个线程运行 10 个请求但只共享 5 个连接。

当然，这个例子依赖于服务器的Keep-Alive超时。为了确保连接在重新使用之前不会死亡，我们应该为客户端配置一个Keep-Alive策略(参见示例 5.1)。

## 7. 配置超时——使用连接管理器的套接字超时

我们在配置连接管理器时可以设置的唯一超时是套接字超时：

示例 7.1。将套接字超时设置为 5 秒

```java
HttpRoute route = new HttpRoute(new HttpHost("www.baeldung.com", 80));
PoolingHttpClientConnectionManager connManager 
  = new PoolingHttpClientConnectionManager();
connManager.setSocketConfig(route.getTargetHost(),SocketConfig.custom().
    setSoTimeout(5000).build());
```

有关 HttpClient 中超时的更深入讨论，[请参见此处](https://www.baeldung.com/httpclient-timeout)。

## 8.连接驱逐

我们使用连接驱逐来检测空闲和过期的连接并关闭它们。我们有两种选择来做到这一点：

1.  依靠HttpClient在运行请求之前检查连接是否失效。这是一个昂贵的选择，并不总是可靠的。
2.  创建一个监控线程来关闭空闲和/或关闭的连接

例 8.1。将HttpClient设置为检查失效连接

```java
PoolingHttpClientConnectionManager connManager 
  = new PoolingHttpClientConnectionManager();
CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(
    RequestConfig.custom().setStaleConnectionCheckEnabled(true).build()
).setConnectionManager(connManager).build();
```

例 8.2。使用陈旧的连接监视器线程

```java
PoolingHttpClientConnectionManager connManager 
  = new PoolingHttpClientConnectionManager();
CloseableHttpClient client = HttpClients.custom()
  .setConnectionManager(connManager).build();
IdleConnectionMonitorThread staleMonitor
 = new IdleConnectionMonitorThread(connManager);
staleMonitor.start();
staleMonitor.join(1000);
```

让我们看一下IdleConnectionMonitorThread 类：

```java
public class IdleConnectionMonitorThread extends Thread {
    private final HttpClientConnectionManager connMgr;
    private volatile boolean shutdown;

    public IdleConnectionMonitorThread(
      PoolingHttpClientConnectionManager connMgr) {
        super();
        this.connMgr = connMgr;
    }
    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(1000);
                    connMgr.closeExpiredConnections();
                    connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException ex) {
            shutdown();
        }
    }
    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}
```

## 9.连接关闭

我们可以优雅地关闭连接(我们尝试在关闭之前刷新输出缓冲区)，或者我们可以通过调用shutdown方法强制关闭连接(不刷新输出缓冲区)。

要正确关闭连接，我们需要执行以下所有操作：

-   使用并关闭响应(如果可关闭)

-   关闭客户端

-   关闭并关闭连接管理器

示例 9.1。关闭连接并释放资源

```java
connManager = new PoolingHttpClientConnectionManager();
CloseableHttpClient client = HttpClients.custom()
  .setConnectionManager(connManager).build();
HttpGet get = new HttpGet("http://google.com");
CloseableHttpResponse response = client.execute(get);

EntityUtils.consume(response.getEntity());
response.close();
client.close();
connManager.close();

```

如果我们在没有关闭连接的情况下关闭管理器，所有连接将被关闭并释放所有资源。

请务必记住，这不会刷新现有连接可能正在进行的任何数据。

## 10.总结

在本文中，我们讨论了如何使用 HttpClient 的 HTTP 连接管理 API 来处理管理连接的整个过程。这包括打开和分配它们，管理多个代理的并发使用，最后关闭它们。

我们看到了BasicHttpClientConnectionManager如何成为处理单个连接的简单解决方案以及它如何管理低级连接。

我们还看到了PoolingHttpClientConnectionManager与HttpClient API 的结合如何提供高效且符合协议的 HTTP 连接使用。