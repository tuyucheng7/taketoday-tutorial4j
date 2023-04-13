## 1. 概述

Spring Boot是一个固执己见的框架。尽管如此，我们通常最终会覆盖应用程序配置文件(如application.properties)中的自动配置属性。

但是，在Spring Cloud应用程序中，我们经常使用另一个名为bootstrap.properties的配置文件。

在这个快速教程中，我们将解释**bootstrap.properties和application.properties之间的区别**。

## 2. 什么时候使用Application配置文件？

我们使用application.yml或application.properties来**配置应用程序上下文**。

当Spring Boot应用程序启动时，它会创建一个不需要显式配置的应用程序上下文-它已经自动配置了。但是，**Spring Boot提供了不同的方式来覆盖这些属性**。

我们可以在代码、命令行参数、ServletConfig初始化参数、ServletContext初始化参数、Java系统属性、操作系统变量和应用程序属性文件中覆盖这些。

要记住的重要一点是，与其他形式的覆盖应用程序上下文属性相比，这些**应用程序属性文件具有最低的优先级**。

我们倾向于对可以在应用程序上下文中覆盖的属性进行分组：

-   核心属性(日志记录属性、线程属性)
-   集成属性(RabbitMQ属性、ActiveMQ属性)
-   Web属性(HTTP属性、MVC属性)
-   安全属性(LDAP属性、OAuth2属性)

## 3. 什么时候使用Bootstrap配置文件？

我们使用bootstrap.yml或bootstrap.properties来**配置引导上下文**。这样我们就可以很好地将引导程序的外部配置和主上下文分开。

**引导上下文负责从外部源加载配置属性并解密本地外部配置文件中的属性**。

当Spring Cloud应用程序启动时，它会创建一个引导上下文。首先要记住的是引导上下文是主应用程序的父上下文。

另一个要记住的关键点是**这两个上下文共享Environment，它是任何Spring应用程序的外部属性的来源**。与应用程序上下文不同，引导上下文使用不同的约定来定位外部配置。

例如，配置文件的来源可以是文件系统，甚至可以是git仓库。这些服务使用它们的spring-cloud-config-client依赖项来访问配置服务器。

简单来说，**配置服务器就是我们访问应用程序上下文配置文件的点**。

## 4. 快速示例

在此示例中，引导上下文配置文件配置spring-cloud-config-client依赖项以加载正确的应用程序属性文件。

让我们看一个bootstrap.properties文件的例子：

```properties
spring.application.name=config-client
spring.profiles.active=development
spring.cloud.config.uri=http://localhost:8888
spring.cloud.config.username=root
spring.cloud.config.password=s3cr3t
spring.cloud.config.fail-fast=true
management.security.enabled=false
```

## 5. 总结

与Spring Boot应用程序相比，Spring Cloud应用程序具有作为应用程序上下文父级的引导上下文。尽管它们共享相同的Environment，但它们在定位外部配置文件方面有不同的约定。

引导上下文搜索bootstrap.properties或bootstrap.yaml文件，而应用程序上下文搜索application.properties或application.yaml文件。

当然，引导上下文的配置属性在应用程序上下文的配置属性之前加载。