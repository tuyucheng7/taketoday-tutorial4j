## 1. 概述

在本教程中，我们将研究Spring Boot应用程序中Reactor Netty服务器的不同配置选项。最后，我们将有一个展示不同配置方法的应用程序。

## 2. Reactor Netty是什么？

在开始之前，让我们看看Reactor Netty是什么以及它与Spring Boot的关系。

Reactor Netty是一个[异步事件驱动的网络应用程序框架](https://projectreactor.io/docs/netty/snapshot/reference/index.html#getting-started)，它为TCP、HTTP和UDP客户端和服务器提供非阻塞和背压。顾名思义，它基于[Netty框架](https://www.baeldung.com/netty)。

[Spring WebFlux](https://www.baeldung.com/spring-webflux)是Spring框架的一部分，为Web应用程序提供响应式编程支持。如果我们在Spring Boot应用程序中使用WebFlux，**Spring Boot会自动将Reactor Netty配置为默认服务器**。除此之外，我们可以显式地将Reactor Netty添加到我们的项目中，Spring Boot也应该自动配置它。

现在，我们将构建一个应用程序，来了解如何自定义我们的自动配置的Reactor Netty服务器。之后，我们将介绍一些常见的配置场景。

## 3. Gradle依赖

首先，我们将添加所需的Maven依赖项。

要使用Reactor Netty服务器，我们将在我们的pom文件中添加[spring-boot-starter-webflux](https://search.maven.org/search?q=a:spring-boot-starter-webflux)作为依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

这也会将[spring-boot-starter-reactor-netty](https://search.maven.org/search?q=a:spring-boot-starter-reactor-netty)作为传递性依赖引入到我们的项目中。

## 4. 服务器配置

### 4.1 使用属性文件

作为第一个选项，我们可以通过属性文件配置Netty服务器。Spring Boot在application.properties文件中公开了一些常见的服务器配置：

让我们在application.properties中定义服务器端口：

```properties
server.port=8088
```

或者我们可以在application.yml中做同样的事情：

```yaml
server:
    port: 8088
```

除了服务器端口，Spring Boot还有许多其他可用的[服务器配置选项](https://www.baeldung.com/spring-boot-application-configuration)，**以server前缀开头的属性允许我们覆盖默认的服务器配置**。我们可以在[Spring文档](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)中的EMBEDDED SERVER CONFIGURATION部分下轻松查找这些属性。

### 4.2 使用编程配置

现在，让我们看看如何**通过代码配置我们的嵌入式Netty服务器**。为此，Spring Boot为我们提供了WebServerFactoryCustomizer和NettyServerCustomizer类。

让我们使用这些类来[配置Netty端口](https://www.baeldung.com/spring-boot-change-port)，就像我们之前使用[属性文件](https://www.baeldung.com/properties-with-spring)所做的那样：

```java
@Component
public class NettyWebServerFactoryPortCustomizer implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {

    @Override
    public void customize(NettyReactiveWebServerFactory factory) {
        serverFactory.setPort(8443);
    }
}
```

Spring Boot将在启动期间选择我们的NettyReactiveWebServerFactory组件并配置服务器端口。

或者，我们可以实现NettyServerCustomizer：

```java
private static class PortCustomizer implements NettyServerCustomizer {
    private final int port;

    private PortCustomizer(int port) {
        this.port = port;
    }
    @Override
    public HttpServer apply(HttpServer httpServer) {
        return httpServer.port(port);
    }
}
```

并将其添加到服务器工厂：

```java
serverFactory.addServerCustomizers(new PortCustomizer(8443));
```

在配置嵌入式Reactor Netty服务器时，这两种方法为我们提供了很大的灵活性。

**此外，我们还可以自定义EventLoopGroup**：

```java
private static class EventLoopNettyCustomizer implements NettyServerCustomizer {

    @Override
    public HttpServer apply(HttpServer httpServer) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        eventLoopGroup.register(new NioServerSocketChannel());
        return httpServer.runOn(eventLoopGroup);
    }
}
```

然而，在这种情况下有一个警告。由于Spring Boot会自动配置Netty服务器，**我们可能需要通过显式定义NettyReactiveWebServerFactory bean来跳过自动配置**。

为此，我们应该在配置类中定义我们的bean，并在其中添加自定义器：

```java
@Bean
public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() {
    NettyReactiveWebServerFactory webServerFactory = new NettyReactiveWebServerFactory();
    webServerFactory.addServerCustomizers(new EventLoopNettyCustomizer());
    return webServerFactory;
}
```

接下来，我们将继续介绍一些常见的Netty配置方案。

## 5. SSL配置

现在让我们看看如何配置SSL。

我们将使用SslServerCustomizer类，它是NettyServerCustomizer的另一个实现：

```java
@Component
public class NettyWebServerFactorySslCustomizer implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {

    @Override
    public void customize(NettyReactiveWebServerFactory serverFactory) {
        Ssl ssl = new Ssl();
        ssl.setEnabled(true);
        ssl.setKeyStore("classpath:sample.jks");
        ssl.setKeyAlias("alias");
        ssl.setKeyPassword("password");
        ssl.setKeyStorePassword("secret");
        Http2 http2 = new Http2();
        http2.setEnabled(false);
        serverFactory.addServerCustomizers(new SslServerCustomizer(ssl, http2, null));
        serverFactory.setPort(8443);
    }
}
```

在这里，我们定义了与密钥库相关的属性，禁用了HTTP/2，并将端口设置为8443。

## 6. 访问日志配置

现在，我们将了解如何使用[Logback](https://www.baeldung.com/logback)配置访问日志记录。

Spring Boot允许我们在应用程序属性文件中为Tomcat、Jetty和Undertow配置访问日志记录。但是，Netty目前还没有这种支持。

要启用Netty访问日志记录，我们应该在运行我们的应用程序时**设置-Dreactor.netty.http.server.accessLogEnabled=true**：

```shell
mvn spring-boot:run -Dreactor.netty.http.server.accessLogEnabled=true
```

## 7. 总结

在本文中，我们介绍了如何在Spring Boot应用程序中配置Reactor Netty服务器。

首先，我们使用了通用的Spring Boot基于属性文件的配置能力。然后，我们探讨了如何以细粒度的方式以编程方式配置Netty。