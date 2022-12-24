## 1. 概述

Spring Boot提供了许多自动配置以方便编写企业应用程序，但是，将相同的[日志记录]()配置应用于一组记录器总是有点麻烦。

在本快速教程中，我们将了解新的[日志组](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/html/spring-boot-features.html#boot-features-custom-log-groups)功能将如何解决此问题。

## 2. 日志组

**从**[Spring Boot 2.1](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.1-Release-Notes#logging-groups)**开始，可以将多个记录器组合在一起，然后同时配置它们**。

为了使用这个特性，首先，我们应该通过logging.group配置属性声明一个组：

```properties
logging.group.rest=cn.tuyucheng.taketoday.web,org.springframework.web,org.springframework.http
```

在这里，我们创建了一个名为rest的组，其中包含三个不同的记录器名称。**对记录器进行分组就像用逗号分隔它们各自的记录器名称一样简单**。

然后我们可以一次将配置应用于一组中的所有记录器，例如，我们在这里更改该组的日志级别以进行调试：

```properties
logging.level.rest=DEBUG
```

因此，Spring Boot对所有三个组成员应用相同的日志级别。

### 2.1 内置组

默认情况下，Spring Boot自带两个内置组：sql和web。 

目前，web组由以下记录器组成： 

-   org.springframework.core.codec
-   org.springframework.http
-   org.springframework.web
-   org.springframework.boot.actuate.endpoint.web
-   org.springframework.boot.web.servlet.ServletContextInitializerBeans

同样，sql组包含以下记录器：

-   org.springframework.jdbc.core
-   org.hibernate.SQL
-   org.jooq.tools.LoggerListener

为这些组中的任何一个配置日志级别将自动应用于所有组成员。

## 3. 总结

在这篇简短的文章中，我们熟悉了Spring Boot中的日志组，此功能使我们能够一次将日志配置应用于一组记录器。