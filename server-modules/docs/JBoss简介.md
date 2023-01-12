## 1. 概述

[Undertow是](http://undertow.io/)JBoss的一个极其轻量级的高性能 Web 服务器。它支持带有NIO 的阻塞和非阻塞 API。

由于它是用Java编写的，因此可以在任何基于 JVM 的嵌入式应用程序中使用，甚至 JBoss 的[WilfFly](http://wildfly.org/)服务器内部也使用Undertow来提高服务器性能。

在本教程中，我们将展示 Undertow 的功能及其使用方法。

## 2. 为什么要逆流？

-   轻巧：Undertow非常轻巧，不到 1MB。在嵌入式模式下，它在运行时仅使用 4MB 的堆空间
-   Servlet 3.1：完全支持Servlet 3.1
-   Web Socket：它支持 Web Socket 功能(包括JSR-356)
-   持久连接：默认情况下，Undertow通过添加keep-alive响应标头来包含 HTTP 持久连接。它帮助支持持久连接的客户端通过重用连接细节来优化性能

## 3.使用暗流

让我们通过创建一个简单的 Web 服务器来开始使用Undertow 。

### 3.1. Maven 依赖

要使用Undertow，我们需要将以下依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>io.undertow</groupId>
    <artifactId>undertow-servlet</artifactId>
    <version>1.4.18.Final</version>
</dependency>
```

要构建可运行的 jar，我们还需要添加[maven-shade-plugin](https://maven.apache.org/plugins/maven-shade-plugin/)。这就是为什么我们还需要添加以下配置的原因：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

最新版本的Undertow在[Central Maven Repository](https://search.maven.org/classic/#search|ga|1|g%3A"io.undertow")中可用。

### 3.2. 简单服务器

通过下面的代码片段，我们可以使用 Undertow 的[Builder](http://undertow.io/javadoc/1.4.x/io/undertow/Undertow.Builder.html) API 创建一个简单的 Web 服务器：

```java
public class SimpleServer {
    public static void main(String[] args) {
        Undertow server = Undertow.builder().addHttpListener(8080, 
          "localhost").setHandler(exchange -> {
            exchange.getResponseHeaders()
            .put(Headers.CONTENT_TYPE, "text/plain");
          exchange.getResponseSender().send("Hello Baeldung");
        }).build();
        server.start();
    }
}
```

在这里，我们使用Builder API 将8080端口绑定到此服务器。另请注意，我们使用了 lambda 表达式来使用处理程序。

我们也可以使用下面的代码片段来做同样的事情而不使用 lambda 表达式：

```java
Undertow server = Undertow.builder().addHttpListener(8080, "localhost")
  .setHandler(new HttpHandler() {
      @Override
      public void handleRequest(HttpServerExchange exchange) 
        throws Exception {
          exchange.getResponseHeaders().put(
            Headers.CONTENT_TYPE, "text/plain");
          exchange.getResponseSender().send("Hello Baeldung");
      }
  }).build();
```

这里要注意的重要一点是[HttpHandler](http://undertow.io/javadoc/1.4.x/io/undertow/server/HttpHandler.html) API 的用法。它是根据我们的需要自定义Undertow应用程序的最重要的插件。

在这种情况下，我们添加了一个自定义处理程序，该处理程序将在每个请求中添加Content-Type: text/plain响应标头。

类似的方式，如果我们想在每个响应中返回一些默认文本，我们可以使用下面的代码片段：

```java
exchange.getResponseSender()
  .send("Hello Baeldung");
```

### 3.3. 安全访问

在大多数情况下，我们不允许所有用户访问我们的服务器。通常，具有有效凭据的用户可以访问。我们可以使用Undertow实现相同的机制。

为了实现它，我们需要创建一个身份管理器，它将检查用户对每个请求的真实性。

为此，我们可以使用 Undertow 的[IdentityManager ：](http://undertow.io/javadoc/1.4.x/io/undertow/security/idm/IdentityManager.html)

```java
public class CustomIdentityManager implements IdentityManager {
    private Map<String, char[]> users;

    // standard constructors
    
    @Override
    public Account verify(Account account) {
        return account;
    }
 
    @Override
    public Account verify(Credential credential) {
        return null;
    }
 
    @Override
    public Account verify(String id, Credential credential) {
        Account account = getAccount(id);
        if (account != null && verifyCredential(account, credential)) {
            return account;
        }
        return null;
    }
}
```

创建身份管理器后，我们需要创建一个领域来保存用户凭据：

```java
private static HttpHandler addSecurity(
  HttpHandler toWrap, 
  IdentityManager identityManager) {
 
    HttpHandler handler = toWrap;
    handler = new AuthenticationCallHandler(handler);
    handler = new AuthenticationConstraintHandler(handler);
    List<AuthenticationMechanism> mechanisms = Collections.singletonList(
      new BasicAuthenticationMechanism("Baeldung_Realm"));
    handler = new AuthenticationMechanismsHandler(handler, mechanisms);
    handler = new SecurityInitialHandler(
      AuthenticationMode.PRO_ACTIVE, identityManager, handler);
    return handler;
}
```

在这里，我们将[AuthenticationMode](http://undertow.io/javadoc/1.4.x/io/undertow/security/api/AuthenticationMode.html)用作PRO_ACTIVE，这意味着到达此服务器的每个请求都将传递到定义的身份验证机制以急切地执行身份验证。

如果我们将AuthenticationMode定义为CONSTRAINT_DRIVEN，那么只有那些请求将通过定义的身份验证机制，其中触发强制身份验证的约束。

现在，我们只需要在服务器启动之前将这个领域和身份管理器映射到服务器：

```java
public static void main(String[] args) {
    Map<String, char[]> users = new HashMap<>(2);
    users.put("root", "password".toCharArray());
    users.put("admin", "password".toCharArray());

    IdentityManager idm = new CustomIdentityManager(users);

    Undertow server = Undertow.builder().addHttpListener(8080, "localhost")
      .setHandler(addSecurity(e -> setExchange(e), idm)).build();

    server.start();
}

private static void setExchange(HttpServerExchange exchange) {
    SecurityContext context = exchange.getSecurityContext();
    exchange.getResponseSender().send("Hello " + 
      context.getAuthenticatedAccount().getPrincipal().getName(),
      IoCallback.END_EXCHANGE);
}
```

在这里，我们创建了两个具有凭据的用户实例。服务器启动后，要访问它，我们需要使用这两个凭据中的任何一个。

### 3.4. 网络套接字

使用UnderTow 的[WebSocketHttpExchange](http://undertow.io/javadoc/1.4.x/io/undertow/websockets/spi/WebSocketHttpExchange.html) API创建网络套接字交换通道非常简单。

例如，我们可以使用以下代码片段在路径baeldungApp上打开一个套接字通信通道：

```java
public static void main(String[] args) {
    Undertow server = Undertow.builder().addHttpListener(8080, "localhost")
      .setHandler(path().addPrefixPath("/baeldungApp", websocket(
        (exchange, channel) -> {
          channel.getReceiveSetter().set(getListener());
          channel.resumeReceives();
      })).addPrefixPath("/", resource(new ClassPathResourceManager(
        SocketServer.class.getClassLoader(),
        SocketServer.class.getPackage())).addWelcomeFiles("index.html")))
        .build();

    server.start();
}

private static AbstractReceiveListener getListener() {
    return new AbstractReceiveListener() {
        @Override
        protected void onFullTextMessage(WebSocketChannel channel, 
          BufferedTextMessage message) {
            String messageData = message.getData();
            for (WebSocketChannel session : channel.getPeerConnections()) {
                WebSockets.sendText(messageData, session, null);
            }
        }
    };
}
```

我们可以创建一个名为index.html的 HTML 页面，并使用 JavaScript 的[WebSocket](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API/Writing_WebSocket_client_applications) API 连接到该通道。

### 3.5. 文件服务器

使用Undertow，我们还可以创建一个文件服务器，它可以显示目录内容并直接从目录中提供文件：

```java
public static void main( String[] args ) {
    Undertow server = Undertow.builder().addHttpListener(8080, "localhost")
        .setHandler(resource(new PathResourceManager(
          Paths.get(System.getProperty("user.home")), 100 ))
        .setDirectoryListingEnabled( true ))
        .build();
    server.start();
}
```

我们不需要创建任何 UI 内容来显示目录内容。开箱即用的Undertow为该显示功能提供了一个页面。

## 4.Spring Boot插件

除了[Tomcat](https://tomcat.apache.org/)和[Jetty](https://www.eclipse.org/jetty/)之外， Spring Boot还支持UnderTow作为嵌入式 servlet 容器。要使用Undertow，我们需要在 pom.xml 中添加以下依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-undertow</artifactId>
    <version>1.5.6.RELEASE</version>
</dependency>
```

[Central Maven Repository](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-undertow")中提供了最新版本的Spring Boot Undertow 插件。

## 5.总结

在本文中，我们了解了Undertow以及如何使用它创建不同类型的服务器。