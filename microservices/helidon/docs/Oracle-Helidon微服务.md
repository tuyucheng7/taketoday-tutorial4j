## 一、概述

[Helidon](https://helidon.io/)是Oracle近期开源的全新Java微服务框架。它以J4C(JavaforCloud)的名称在Oracle项目内部使用。

在本教程中，我们将介绍框架的主要概念，然后我们将着手构建和运行基于Helidon的微服务。

## 2.编程模型

目前，该框架支持两种编写微服务的编程模型：HelidonSE和HelidonMP。

虽然HelidonSE被设计成一个支持反应式编程模型的微框架，但另一方面，HelidonMP是一个EclipseMicroProfile运行时，它允许JakartaEE社区以可移植的方式运行微服务。

在这两种情况下，Helidon微服务都是一个JavaSE应用程序，它从main方法启动一个微型HTTP服务器。

## 3.HelidonSE

在本节中，我们将更详细地了解HelidonSE的主要组件：WebServer、Config和Security。

### 3.1.设置网络服务器

要开始使用WebServerAPI，我们需要将所需的[Maven依赖](https://search.maven.org/search?q=a:helidon-webserver)项添加到pom.xml文件中：

```xml
<dependency>
    <groupId>io.helidon.webserver</groupId>
    <artifactId>helidon-webserver</artifactId>
    <version>0.10.4</version>
</dependency>
```

要拥有一个简单的Web应用程序，我们可以使用以下构建器方法之一：WebServer.create(serverConfig,routing)或只是WebServer.create(routing)。最后一个采用默认服务器配置，允许服务器在随机端口上运行。

下面是一个在预定义端口上运行的简单Web应用程序。我们还注册了一个简单的处理程序，它将为任何带有“/greet”路径和GET方法的HTTP请求响应问候消息：

```java
public static void main(String... args) throws Exception {
    ServerConfiguration serverConfig = ServerConfiguration.builder()
      .port(9001).build();
    Routing routing = Routing.builder()
      .get("/greet", (request, response) -> response.send("Hello World !")).build();
    WebServer.create(serverConfig, routing)
      .start()
      .thenAccept(ws ->
          System.out.println("Server started at: http://localhost:" + ws.port())
      );
}
```

最后一行是启动服务器并等待服务HTTP请求。但是如果我们在main方法中运行这个示例代码，我们会得到错误：

```bash
Exception in thread "main" java.lang.IllegalStateException: 
  No implementation found for SPI: io.helidon.webserver.spi.WebServerFactory
```

WebServer其实就是一个SPI，我们需要提供一个runtime的实现。目前，Helidon提供了基于[Netty](https://netty.io/index.html)Core的NettyWebServer实现。

这是此实现的[Maven依赖项](https://search.maven.org/search?q=a:helidon-webserver-netty)：

```xml
<dependency>
    <groupId>io.helidon.webserver</groupId>
    <artifactId>helidon-webserver-netty</artifactId>
    <version>0.10.4</version>
    <scope>runtime</scope>
</dependency>
```

现在，我们可以运行主应用程序并通过调用配置的端点来检查它是否正常工作：

```bash
http://localhost:9001/greet
```

在此示例中，我们使用构建器模式配置了端口和路径。

HelidonSE还允许使用配置模式，其中配置数据由ConfigAPI提供。这是下一节的主题。

### 3.2.配置API_

ConfigAPI提供了从配置源读取配置数据的工具。

HelidonSE为许多配置源提供了实现。默认实现由[helidon-config](https://search.maven.org/search?q=a:helidon-config)提供，其中配置源是位于类路径下的application.properties文件：

```xml
<dependency>
    <groupId>io.helidon.config</groupId>
    <artifactId>helidon-config</artifactId>
    <version>0.10.4</version>
</dependency>
```

要读取配置数据，我们只需要使用默认构建器，它默认从application.properties中获取配置数据：

```java
Config config = Config.builder().build();
```

让我们在src/main/resource目录下创建一个application.properties文件，内容如下：

```plaintext
server.port=9080
web.debug=true
web.page-size=15
user.home=C:/Users/app
```

要读取值，我们可以使用Config.get()方法，然后方便地转换为相应的Java类型：

```java
int port = config.get("server.port").asInt();
int pageSize = config.get("web.page-size").asInt();
boolean debug = config.get("web.debug").asBoolean();
String userHome = config.get("user.home").asString();
```

事实上，默认构建器会按以下优先顺序加载第一个找到的文件：application.yaml、application.conf、application.json和application.properties。最后三种格式需要额外的相关配置依赖。例如，要使用YAML格式，我们需要添加相关的YAML[配置](https://search.maven.org/search?q=a:helidon-config-yaml)依赖：

```xml
<dependency>
    <groupId>io.helidon.config</groupId>
    <artifactId>helidon-config-yaml</artifactId>
    <version>0.10.4</version>
</dependency>
```

然后，我们添加一个application.yml：

```plaintext
server:
  port: 9080  
web:
  debug: true
  page-size: 15
user:
  home: C:/Users/app
```

同样，要使用CONF(一种JSON简化格式或JSON格式)，我们需要添加[helidon-config-hocon](https://search.maven.org/search?q=a:helidon-config-hocon)依赖项。

请注意，这些文件中的配置数据可以被环境变量和Java系统属性覆盖。

我们还可以通过禁用环境变量和系统属性或通过显式指定配置源来控制默认构建器行为：

```java
ConfigSource configSource = ConfigSources.classpath("application.yaml").build();
Config config = Config.builder()
  .disableSystemPropertiesSource()
  .disableEnvironmentVariablesSource()
  .sources(configSource)
  .build();
```

除了从类路径中读取配置数据外，我们还可以使用两个外部源配置，即git和etcd配置。为此，我们需要[helidon-config-git](https://search.maven.org/search?q=a:helidon-config-git)和[helidon-git-etcd](https://search.maven.org/search?q=a:helidon-config-etcd)依赖项。

最后，如果所有这些配置源都不能满足我们的需要，Helidon允许我们为我们的配置源提供一个实现。例如，我们可以提供一个可以从数据库中读取配置数据的实现。

### 3.3.路由API_

路由API提供了将HTTP请求绑定到Java方法的机制。我们可以通过使用请求方法和路径作为匹配条件或使用RequestPredicate对象来使用更多条件来完成此操作。

因此，要配置路由，我们可以只使用HTTP方法作为标准：

```java
Routing routing = Routing.builder()
  .get((request, response) -> {} );
```

或者我们可以将HTTP方法与请求路径结合起来：

```java
Routing routing = Routing.builder()
  .get("/path", (request, response) -> {} );
```

我们还可以使用RequestPredicate进行更多控制。例如，我们可以检查现有标题或内容类型：

```java
Routing routing = Routing.builder()
  .post("/save",
    RequestPredicate.whenRequest()
      .containsHeader("header1")
      .containsCookie("cookie1")
      .accepts(MediaType.APPLICATION_JSON)
      .containsQueryParameter("param1")
      .hasContentType("application/json")
      .thenApply((request, response) -> { })
      .otherwise((request, response) -> { }))
      .build();
```

到目前为止，我们已经提供了函数式风格的处理程序。我们还可以使用Service类，它允许以更复杂的方式编写处理程序。

所以，让我们首先为我们正在使用的对象创建一个模型，Book类：

```java
public class Book {
    private String id;
    private String name;
    private String author;
    private Integer pages;
    // ...
}
```

我们可以通过实现Service.update()方法为Book类创建REST服务。这允许配置同一资源的子路径：

```java
public class BookResource implements Service {

    private BookManager bookManager = new BookManager();

    @Override
    public void update(Routing.Rules rules) {
        rules
          .get("/", this::books)
          .get("/{id}", this::bookById);
    }

    private void bookById(ServerRequest serverRequest, ServerResponse serverResponse) {
        String id = serverRequest.path().param("id");
        Book book = bookManager.get(id);
        JsonObject jsonObject = from(book);
        serverResponse.send(jsonObject);
    }

    private void books(ServerRequest serverRequest, ServerResponse serverResponse) {
        List<Book> books = bookManager.getAll();
        JsonArray jsonArray = from(books);
        serverResponse.send(jsonArray);
    }
    //...
}
```

我们还将媒体类型配置为JSON，因此为此我们需要[helidon-webserver-json](https://search.maven.org/search?q=a:helidon-webserver-json)依赖项：

```xml
<dependency>
    <groupId>io.helidon.webserver</groupId>
    <artifactId>helidon-webserver-json</artifactId>
    <version>0.10.4</version>
</dependency>
```

最后，我们使用Routing构建器的register()方法将根路径绑定到资源。在这种情况下，服务配置的路径以根路径为前缀：

```java
Routing routing = Routing.builder()
  .register(JsonSupport.get())
  .register("/books", new BookResource())
  .build();
```

我们现在可以启动服务器并检查端点：

```plaintext
http://localhost:9080/books
http://localhost:9080/books/0001-201810
```

### 3.4.安全

在本节中，我们将使用安全模块来保护我们的资源。

让我们首先声明所有必要的依赖项：

```xml
<dependency>
    <groupId>io.helidon.security</groupId>
    <artifactId>helidon-security</artifactId>
    <version>0.10.4</version>
</dependency>
<dependency>
    <groupId>io.helidon.security</groupId>
    <artifactId>helidon-security-provider-http-auth</artifactId>
    <version>0.10.4</version>
</dependency>
<dependency>
    <groupId>io.helidon.security</groupId>
    <artifactId>helidon-security-integration-webserver</artifactId>
    <version>0.10.4</version>
</dependency>
```

helidon[-security](https://search.maven.org/search?q=a:helidon-security)、[helidon-security-provider-http-auth](https://search.maven.org/search?q=a:helidon-security-provider-http-auth)和[helidon-security-integration-webserver](https://search.maven.org/search?q=a:helidon-security-integration-webserver)依赖项可从MavenCentral获得。

安全模块提供了许多用于身份验证和授权的提供程序。对于此示例，我们将使用HTTP基本身份验证提供程序，因为它相当简单，但其他提供程序的过程几乎相同。

要做的第一件事是创建一个安全实例。为简单起见，我们可以通过编程方式进行：

```java
Map<String, MyUser> users = //...
UserStore store = user -> Optional.ofNullable(users.get(user));

HttpBasicAuthProvider httpBasicAuthProvider = HttpBasicAuthProvider.builder()
  .realm("myRealm")
  .subjectType(SubjectType.USER)
  .userStore(store)
  .build();

Security security = Security.builder()
  .addAuthenticationProvider(httpBasicAuthProvider)
  .build();
```

或者我们可以使用配置方法。

在这种情况下，我们将在通过ConfigAPI加载的application.yml文件中声明所有安全配置：

```plaintext
#Config 4 Security ==> Mapped to Security Object
security:
  providers:
  - http-basic-auth:
      realm: "helidon"
      principal-type: USER # Can be USER or SERVICE, default is USER
      users:
      - login: "user"
        password: "user"
        roles: ["ROLE_USER"]
      - login: "admin"
        password: "admin"
        roles: ["ROLE_USER", "ROLE_ADMIN"]

  #Config 4 Security Web Server Integration ==> Mapped to WebSecurity Object
  web-server:
    securityDefaults:
      authenticate: true
    paths:
    - path: "/user"
      methods: ["get"]
      roles-allowed: ["ROLE_USER", "ROLE_ADMIN"]
    - path: "/admin"
      methods: ["get"]
      roles-allowed: ["ROLE_ADMIN"]
```

要加载它，我们只需要创建一个Config对象，然后调用Security.fromConfig()方法：

```java
Config config = Config.create();
Security security = Security.fromConfig(config);
```

一旦我们有了Security实例，我们首先需要使用WebSecurity.from()方法将它注册到WebServer：

```java
Routing routing = Routing.builder()
  .register(WebSecurity.from(security).securityDefaults(WebSecurity.authenticate()))
  .build();
```

我们还可以使用配置方法直接创建一个WebSecurity实例，通过该方法我们可以加载安全和Web服务器配置：

```java
Routing routing = Routing.builder()        
  .register(WebSecurity.from(config))
  .build();
```

我们现在可以为/user和/admin路径添加一些处理程序，启动服务器并尝试访问它们：

```java
Routing routing = Routing.builder()
  .register(WebSecurity.from(config))
  .get("/user", (request, response) -> response.send("Hello, I'm Helidon SE"))
  .get("/admin", (request, response) -> response.send("Hello, I'm Helidon SE"))
  .build();
```

## 4.赫利顿国会议员

HelidonMP是EclipseMicroProfile的一个实现，还提供了一个用于运行基于MicroProfile的微服务的运行时。

由于我们已经有[一篇关于EclipseMicroProfile的文章](https://www.baeldung.com/eclipse-microprofile)，我们将检查该源代码并修改它以在HelidonMP上运行。

检查代码后，我们将删除所有依赖项和插件，并将HelidonMP依赖项添加到POM文件中：

```xml
<dependency>
    <groupId>io.helidon.microprofile.bundles</groupId>
    <artifactId>helidon-microprofile-1.2</artifactId>
    <version>0.10.4</version>
</dependency>
<dependency>
    <groupId>org.glassfish.jersey.media</groupId>
    <artifactId>jersey-media-json-binding</artifactId>
    <version>2.26</version>
</dependency>
```

helidon[-microprofile-1.2](https://search.maven.org/search?q=a:helidon-microprofile-1.2)和[jersey-media-json-binding](https://search.maven.org/search?q=a:jersey-media-json-binding)依赖项可从MavenCentral获得。

接下来，我们将在src/main/resource/META-INF目录下添加beans.xml文件，内容如下：

```xml
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns="http://xmlns.jcp.org/xml/ns/javaee"
  xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
  http://xmlns.jcp.org/xml/ns/javaee/beans_2_0.xsd"
  version="2.0" bean-discovery-mode="annotated">
</beans>
```

在LibraryApplication类中，覆盖getClasses()方法，这样服务器就不会扫描资源：

```java
@Override
public Set<Class<?>> getClasses() {
    return CollectionsHelper.setOf(BookEndpoint.class);
}
```

最后，创建一个main方法并添加以下代码片段：

```java
public static void main(String... args) {
    Server server = Server.builder()
      .addApplication(LibraryApplication.class)
      .port(9080)
      .build();
    server.start();
}
```

就是这样。我们现在可以调用所有图书资源。

## 5.总结

在本文中，我们探讨了Helidon的主要组件，还展示了如何设置HelidonSE和MP。由于HelidonMP只是一个EclipseMicroProfile运行时，我们可以使用它运行任何现有的基于MicroProfile的微服务。