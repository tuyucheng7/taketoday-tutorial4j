## 1. 概述

在本教程中，我们将研究Spring Boot应用程序中Reactor Netty服务器的不同配置选项。

## 2. Reactor Netty是什么？

在开始之前，让我们看看Reactor Netty是什么以及它与Spring Boot的关系。

Reactor Netty是一个异步事件驱动的网络应用程序框架。
它为TCP、HTTP和UDP客户端和服务器提供非阻塞和背压。顾名思义，它基于Netty框架。

Spring WebFlux是Spring框架的一部分，为Web应用程序提供响应式编程支持。
如果我们在Spring Boot应用程序中使用WebFlux，Spring Boot会自动将Reactor Netty配置为默认服务器。
除此之外，我们可以显式地将Reactor Netty添加到我们的项目中，Spring Boot也应该自动配置它。

现在，我们将构建一个应用程序，了解如何自定义我们的自动配置的Reactor Netty服务器。之后，我们将介绍一些常见的配置场景。

## 3. Gradle依赖

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
}
```

这也将把spring-boot-starter-reactor-netty作为传递性依赖引入到我们的项目中。

## 4. 服务器配置

### 4.1 使用属性文件

作为第一种方法，我们可以通过属性文件配置Netty服务器。Spring Boot在应用程序属性文件中公开了一些常见的服务器配置：

让我们在application.properties中定义服务器端口：

```properties
server.port=8088
```

或者我们可以使用application.yml：

```yaml
server:
    port: 8088
```

除了服务器端口，Spring Boot还有许多其他可用的服务器配置选项。以server前缀开头的属性允许我们覆盖默认的服务器配置。

### 4.2 使用编程配置

现在，让我们看看如何通过代码配置我们的嵌入式Netty服务器。
为此，Spring Boot为我们提供了WebServerFactoryCustomizer和NettyServerCustomizer类。

让我们使用这些类来配置Netty端口，就像我们之前使用属性文件所做的那样：

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
private record PortCustomizer(int port) implements NettyServerCustomizer {

    @Override
    public HttpServer apply(HttpServer httpServer) {
        return httpServer.port(port);
    }
}
```

并将其添加到服务器工厂：

```text
serverFactory.addServerCustomizers(new PortCustomizer(8443));
```

在配置嵌入式Reactor Netty服务器时，这两种方法为我们提供了很大的灵活性。

此外，我们还可以自定义EventLoopGroup：

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

然而，在这种情况下有一个警告。由于Spring Boot自动配置Netty服务器，
我们可能需要通过显式定义NettyReactiveWebServerFactory bean来跳过自动配置。

为此，我们应该在配置类中定义我们的bean，并在其中添加定制器：

```java

@Configuration
@Profile("skipAutoConfig")
public class CustomNettyWebServerFactory {

    @Bean
    public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() {
        NettyReactiveWebServerFactory webServerFactory = new NettyReactiveWebServerFactory();
        webServerFactory.addServerCustomizers(new EventLoopNettyCustomizer());
        return webServerFactory;
    }
}
```

## 5. SSL配置

现在让我们看看如何配置SSL。

我们使用SslServerCustomizer类，它是NettyServerCustomizer的另一个实现：

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

现在，我们将看看如何使用Logback配置访问日志记录。

Spring Boot允许我们在Tomcat、Jetty和Undertow的应用程序属性文件中配置访问日志记录。但是，Netty目前还没有这种支持。

要启用Netty访问日志记录，我们应该在运行我们的应用程序时设置-Dreactor.netty.http.server.accessLogEnabled=true：

```shell
gradle bootRun -Dreactor.netty.http.server.accessLogEnabled=true
```

## 7. 总结

在本文中，我们介绍了如何在Spring Boot应用程序中配置Reactor Netty服务器。

首先，我们使用了通用的Spring Boot基于属性文件的配置能力。然后，我们介绍了如何以细粒度的方式以编程方式配置Netty。