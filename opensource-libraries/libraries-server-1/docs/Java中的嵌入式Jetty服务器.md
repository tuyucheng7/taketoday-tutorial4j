## 1. 概述

在本文中，我们将研究[Jetty](https://www.eclipse.org/jetty/)库。Jetty 提供了一个 Web 服务器，可以作为嵌入式容器运行，并可以轻松地与javax.servlet库集成。

## 2.Maven依赖

首先，我们将 Maven 依赖项添加到[jetty-server](https://search.maven.org/classic/#search|gav|1|g%3A"org.eclipse.jetty" AND a%3A"jetty-server")和[jetty-servlet](https://search.maven.org/classic/#search|gav|1|g%3A"org.eclipse.jetty" AND a%3A"jetty-servlet")库：

```xml
<dependency>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-server</artifactId>
    <version>9.4.3.v20170317</version>
</dependency>
<dependency>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-servlet</artifactId>
    <version>9.4.3.v20170317</version>
</dependency>
```

## 3. 使用 Servlet 启动 Jetty 服务器

启动 Jetty 嵌入式容器很简单。我们需要实例化一个新的服务器对象并将其设置为在给定端口上启动：

```java
public class JettyServer {
    private Server server;

    public void start() throws Exception {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8090);
        server.setConnectors(new Connector[] {connector});
}
```

假设我们想要创建一个端点，如果一切顺利，它将以 HTTP 状态代码 200 和一个简单的 JSON 有效负载进行响应。

我们将创建一个扩展HttpServlet类的类来处理此类请求；此类将是单线程的并阻塞直到完成：

```java
public class BlockingServlet extends HttpServlet {

    protected void doGet(
      HttpServletRequest request, 
      HttpServletResponse response)
      throws ServletException, IOException {
 
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("{ "status": "ok"}");
    }
}
```

接下来，我们需要使用addServletWithMapping()方法在ServletHandler对象中注册BlockingServlet类并启动服务器：

```java
servletHandler.addServletWithMapping(BlockingServlet.class, "/status");
server.start();
```

如果我们希望测试我们的 Servlet 逻辑，我们需要使用之前创建的JettyServer类来启动我们的服务器，该类是测试设置中实际 Jetty 服务器实例的包装器：

```java
@Before
public void setup() throws Exception {
    jettyServer = new JettyServer();
    jettyServer.start();
}
```

一旦开始，我们将向/status端点发送一个测试 HTTP 请求：

```java
String url = "http://localhost:8090/status";
HttpClient client = HttpClientBuilder.create().build();
HttpGet request = new HttpGet(url);

HttpResponse response = client.execute(request);
 
assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
```

## 4. 非阻塞 Servlet

Jetty 对异步请求处理有很好的支持。

假设我们有一个巨大的 I/O 密集型资源，需要很长时间才能加载，从而在相当长的时间内阻塞正在执行的线程。如果可以同时释放该线程来处理其他请求，而不是等待一些 I/O 资源，那就更好了。

为了使用 Jetty 提供这样的逻辑，我们可以通过调用 HttpServletRequest 上的startAsync()方法来创建一个将使用[AsyncContext](https://docs.oracle.com/javaee/6/api/javax/servlet/AsyncContext.html) 类的 servlet 。此代码不会阻塞正在执行的线程，但会在单独的线程中执行 I/O 操作，并在准备就绪时使用AsyncContext.complete()方法返回结果：

```java
public class AsyncServlet extends HttpServlet {
    private static String HEAVY_RESOURCE 
      = "This is some heavy resource that will be served in an async way";

    protected void doGet(
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
 
        ByteBuffer content = ByteBuffer.wrap(
          HEAVY_RESOURCE.getBytes(StandardCharsets.UTF_8));

        AsyncContext async = request.startAsync();
        ServletOutputStream out = response.getOutputStream();
        out.setWriteListener(new WriteListener() {
            @Override
            public void onWritePossible() throws IOException {
                while (out.isReady()) {
                    if (!content.hasRemaining()) {
                        response.setStatus(200);
                        async.complete();
                        return;
                    }
                    out.write(content.get());
                }
            }

            @Override
            public void onError(Throwable t) {
                getServletContext().log("Async Error", t);
                async.complete();
            }
        });
    }
}
```

我们正在将ByteBuffer写入OutputStream，一旦整个缓冲区被写入，我们就会通过调用complete()方法发出结果已准备好返回给客户端的信号。

接下来，我们需要将AsyncServlet添加为 Jetty servlet 映射：

```java
servletHandler.addServletWithMapping(
  AsyncServlet.class, "/heavy/async");
```

我们现在可以向/heavy/async端点发送请求——该请求将由 Jetty 以异步方式处理：

```java
String url = "http://localhost:8090/heavy/async";
HttpClient client = HttpClientBuilder.create().build();
HttpGet request = new HttpGet(url);
HttpResponse response = client.execute(request);

assertThat(response.getStatusLine().getStatusCode())
  .isEqualTo(200);
String responseContent = IOUtils.toString(r
  esponse.getEntity().getContent(), StandardCharsets.UTF_8);
assertThat(responseContent).isEqualTo(
  "This is some heavy resource that will be served in an async way");
```

当我们的应用程序以异步方式处理请求时，我们应该显式配置线程池。在下一节中，我们将配置Jetty以使用自定义线程池。

## 5.码头配置

当我们在生产环境中运行 Web 应用程序时，我们可能希望调整 Jetty 服务器处理请求的方式。这是通过定义线程池并将其应用于我们的 Jetty 服务器来完成的。

为此，我们可以设置三个配置设置：

-   maxThreads – 指定 Jetty 可以在池中创建和使用的最大线程数
-   minThreads –设置 Jetty 将使用的线程池中的初始线程数
-   idleTimeout – 此值以毫秒为单位定义线程在停止并从线程池中删除之前可以空闲多长时间。池中剩余的线程数永远不会低于minThreads设置

有了这些，我们可以通过将配置的线程池传递给服务器构造函数，以编程方式配置嵌入式Jetty服务器：

```java
int maxThreads = 100;
int minThreads = 10;
int idleTimeout = 120;

QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout);

server = new Server(threadPool);
```

然后，当我们启动我们的服务器时，它将使用来自特定线程池的线程。

## 六. 总结

在这个快速教程中，我们了解了如何将嵌入式服务器与 Jetty 集成并测试了我们的 Web 应用程序。