## 1. 概述

在这个快速教程中，我们将了解如何配置Spring Boot应用程序以更优雅地处理关机。

## 2. 优雅关机

从[Spring Boot 2.3](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.3-Release-Notes#graceful-shutdown)开始，Spring Boot现在支持Servlet和响应式平台上所有四种嵌入式Web服务器(Tomcat、Jetty、Undertow和Netty)的优雅关机功能。

**要启用优雅关闭，我们所要做的就是在application.properties文件中将server.shutdown属性设置为graceful**：

```properties
server.shutdown=graceful
```

然后，Tomcat、Netty和Jetty将停止在网络层接受新的请求。另一方面，Undertow将继续接受新请求，但会立即向客户端发送503 Service Unavailable响应。

**默认情况下，此属性的值等于immediate，这意味着服务器会立即关闭**。

某些请求可能会在正常关闭阶段开始之前被接受。**在这种情况下，服务器将等待这些活动请求在指定的时间内完成它们的工作**。我们可以使用spring.lifecycle.timeout-per-shutdown-phase属性来配置此宽限期：

```properties
spring.lifecycle.timeout-per-shutdown-phase=1m
```

如果我们添加它，服务器将等待最多一分钟以完成活动请求。此属性的默认值为30秒。

## 3. 总结

在这个简短的教程中，我们了解了如何利用Spring Boot 2.3中新的优雅关机功能。