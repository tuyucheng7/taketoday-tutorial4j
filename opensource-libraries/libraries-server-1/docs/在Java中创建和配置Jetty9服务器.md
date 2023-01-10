## 1. 概述

在本文中，我们将讨论以编程方式创建和配置 Jetty 实例。

[Jetty](https://www.eclipse.org/jetty/)是一个 HTTP 服务器和 servlet 容器，设计为轻量级且易于嵌入。我们将了解如何设置和配置服务器的一个或多个实例。

## 2.Maven依赖

首先，我们要将[具有以下 Maven 依赖项的 Jetty 9 添加](https://search.maven.org/classic/#search|ga|1|g%3A"org.eclipse.jetty" AND a%3A"jetty-server")到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-server</artifactId>
    <version>9.4.8.v20171121</version>
</dependency>
<dependency>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-webapp</artifactId>
    <version>9.4.8.v20171121</version>
</dependency>
```

## 3. 创建基本服务器

使用 Jetty 启动嵌入式服务器就像编写代码一样简单：

```java
Server server = new Server();
server.start();
```

关闭它同样简单：

```java
server.stop();
```

## 4.处理程序

现在我们的服务器已经启动并运行，我们需要指示它如何处理传入的请求。这可以使用Handler接口来执行。

我们可以自己创建一个，但 Jetty 已经为最常见的用例提供了一组实现。让我们来看看其中的两个。

### 4.1. WebApp上下文

WebAppContext类允许将请求处理委托给现有的 Web 应用程序。该应用程序可以作为 WAR 文件路径或 webapp 文件夹路径提供。

如果我们想在“myApp”上下文中公开一个应用程序，我们会这样写：

```java
Handler webAppHandler = new WebAppContext(webAppPath, "/myApp");
server.setHandler(webAppHandler);
```

### 4.2. 处理程序集合

对于复杂的应用程序，我们甚至可以使用HandlerCollection类指定多个处理程序。

假设我们已经实现了两个自定义处理程序。第一个只执行日志记录操作，而第二个创建并向用户发送实际响应。我们希望按此顺序处理每个传入的请求。

方法如下：

```java
Handler handlers = new HandlerCollection();
handlers.addHandler(loggingRequestHandler);
handlers.addHandler(customRequestHandler);
server.setHandler(handlers);
```

## 5.连接器

接下来我们要做的是配置服务器将侦听的地址和端口并添加空闲超时。

Server类声明了两个方便的构造函数，可用于绑定到特定端口或地址。

虽然这在处理小型应用程序时可能没问题，但如果我们想在不同的套接字上打开多个连接，这就不够了。

在这种情况下，Jetty 提供了Connector接口，更具体地说是ServerConnector类，它允许定义各种连接配置参数：

```java
ServerConnector connector = new ServerConnector(server);
connector.setPort(80);
connector.setHost("169.20.45.12");
connector.setIdleTimeout(30000);
server.addConnector(connector);
```

使用此配置，服务器将侦听 169.20.45.12:80。在此地址上建立的每个连接都有 30 秒的超时时间。

如果我们需要配置其他套接字，我们可以添加其他连接器。

## 六. 总结

在本快速教程中，我们重点介绍了如何使用 Jetty 设置嵌入式服务器。我们还了解了如何使用Handlers和Connectors执行进一步的配置。