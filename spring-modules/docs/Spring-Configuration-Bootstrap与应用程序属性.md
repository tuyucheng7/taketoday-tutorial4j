## 一、概述

Spring Boot 是一个固执己见的框架。尽管如此，我们通常最终会覆盖应用程序配置文件中的自动配置属性，例如*application.properties*。

然而，在 Spring Cloud 应用程序中，我们经常使用另一个名为*bootstrap.properties*的配置文件。

在本快速教程中，我们将解释***bootstrap.properties\*****和*****application.properties\*****之间的区别**。

## 2、什么时候使用应用配置文件？

我们使用*application.yml*或*application.properties* **来配置应用程序上下文**。

当 Spring Boot 应用程序启动时，它会创建一个不需要显式配置的应用程序上下文——它已经自动配置了。但是，**Spring Boot 提供了不同的方式来覆盖这些属性**。

我们可以在代码、命令行参数、*ServletConfig*初始化参数、*ServletContext*初始化参数、Java 系统属性、操作系统变量和应用程序属性文件中覆盖这些。

要记住的重要一点是，与其他形式的覆盖应用程序上下文属性相比，这些应用程序属性文件**具有最低的优先级。**

我们倾向于对可以在应用程序上下文中覆盖的属性进行分组：

-   核心属性（日志属性、线程属性）
-   集成属性（*RabbitMQ*属性、*ActiveMQ*属性）
-   Web 属性（*HTTP*属性、*MVC*属性）
-   安全属性（*LDAP*属性、*OAuth2*属性）

## 3、什么时候使用Bootstrap配置文件？

我们使用*bootstrap.yml*或*bootstrap.properties* **来配置引导上下文**。这样我们就可以很好地将引导程序的外部配置和主上下文分开。

引导**上下文负责从外部源加载配置属性**并解密本地外部配置文件中的属性。

当 Spring Cloud 应用程序启动时，它会创建一个*引导 上下文*。首先要记住的是*引导 上下文*是主应用程序的父上下文。

另一个要记住的关键点是这两个上下文共享环境***，\*它是任何 Spring 应用程序的外部属性的来源**。与应用上下文不同，引导上下文使用不同的约定来定位外部配置。

例如，配置文件的来源可以是文件系统，甚至可以是 git 存储库。这些服务使用它们的*spring-cloud-config-client*依赖项来访问配置服务器。

简单来说，**配置服务器就是我们访问应用程序上下文配置文件的点**。

## 4. 快速示例

在此示例中，引导上下文配置文件配置*spring-cloud-config-client*依赖项以加载正确的应用程序属性文件。

*让我们看一个bootstrap.properties*文件的例子：

```plaintext
spring.application.name=config-client
spring.profiles.active=development
spring.cloud.config.uri=http://localhost:8888
spring.cloud.config.username=root
spring.cloud.config.password=s3cr3t
spring.cloud.config.fail-fast=true
management.security.enabled=false复制
```

## 5.结论

与 Spring Boot 应用程序相比，Spring Cloud 应用程序具有作为应用程序上下文父级的引导上下文。尽管它们共享相同的*Environment*，但它们在定位外部配置文件方面有不同的约定。

引导上下文正在搜索*bootstrap.properties*或*bootstrap.yaml 文件，*而应用程序上下文正在搜索*application.properties*或*application.yaml 文件*。

当然，引导上下文的配置属性在应用程序上下文的配置属性之前加载。