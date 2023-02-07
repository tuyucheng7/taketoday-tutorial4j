## 1. 概述

在本文中，我们将研究新的Spring Cloud Bus项目。Spring Cloud Bus使用轻量级消息代理来链接分布式系统节点。主要用途是广播配置更改或其他管理信息。我们可以把它想象成一个分布式的[Actuator](https://www.baeldung.com/spring-boot-actuators)。

该项目使用AMQP代理作为传输，但可以使用Apache Kafka或Redis代替RabbitMQ。尚不支持其他传输方式。

在本教程的过程中，我们将使用RabbitMQ作为我们的主要传输-我们自然也会运行它。

## 2. 先决条件

在我们开始之前，建议已经完成了[Spring Cloud配置快速入门](https://www.baeldung.com/spring-cloud-configuration)。我们将采用现有的云配置服务器和客户端来扩展它们并添加有关配置更改的自动通知。

### 2.1 RabbitMQ

让我们从RabbitMQ开始，我们建议将[RabbitMQ作为Docker镜像](https://hub.docker.com/_/rabbitmq/)运行。设置起来非常简单-要让RabbitMQ在本地运行，我们需要[安装Docker](https://www.docker.com/get-docker)并在成功安装Docker后运行以下命令：

```shell
docker pull rabbitmq:3-management
```

此命令将RabbitMQ Docker镜像与默认安装和启用的管理插件一起拉取。

接下来，我们可以运行RabbitMQ：

```shell
docker run -d --hostname my-rabbit --name some-rabbit -p 15672:15672 -p 5672:5672 rabbitmq:3-management
```

执行命令后，我们可以转到Web浏览器并访问[http://localhost:15672](http://localhost:15672/)，这将显示管理控制台登录表单。默认用户名是：“guest”；密码：“guest”。RabbitMQ还将监听端口5672。

## 3. 为Cloud Config Client添加Actuator

我们应该同时运行云配置服务器和云配置客户端。要刷新配置更改，每次都需要重新启动客户端-这并不理想。

让我们停止配置客户端并使用@RefreshScope标注ConfigClient控制器类：

```java
@SpringBootApplication
@RestController
@RefreshScope
public class SpringCloudConfigClientApplication {
    // Code here...
}
```

最后，让我们更新pom.xml文件并添加Actuator：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-actuator</artifactId>
    <version>2.2.6.RELEASE</version>
</dependency>
```

最新版本可以在[这里](https://search.maven.org/artifact/org.activiti/spring-boot-starter-actuator)找到。

默认情况下，Actuator添加的所有敏感端点都是安全的。这包括“/refresh”端点。为简单起见，我们将通过更新application.yml来关闭安全性：

```yaml
management:
    security:
        enabled: false
```

此外，从Spring Boot 2开始，[默认情况下不会公开Actuator端点](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready-endpoints)。为了使它们可供访问，我们需要在application.yml中添加以下内容：

```yaml
management:
    endpoints:
        web:
            exposure:
                include: "*"
```

让我们先启动客户端，并在GitHub上的属性文件中将用户角色从现有的“Developer”更新为“Programmer”。配置服务器将立即显示更新的值；但是，客户端不会。为了让客户端看到新文件，我们只需要发送一个空的POST请求到'/refresh'端点，这是由Actuator添加的：

```shell
$> curl -X POST http://localhost:8080/actuator/refresh
```

我们将获得显示更新属性的JSON文件：

```json
[
    "user.role"
]
```

最后，我们可以检查用户角色是否已更新：

```shell
$> curl http://localhost:8080/whoami/Mr_Pink
Hello Mr_Pink! You are a(n) Programmer and your password is 'd3v3L'.
```

用户角色已通过调用“/refresh”端点成功更新。客户端在不重新启动的情况下更新了配置。

## 4. Spring Cloud Bus

通过使用Actuator，我们可以刷新客户端。然而，在云环境中，我们需要访问每个客户端并通过访问Actuator端点重新加载配置。

为了解决这个问题，我们可以使用Spring Cloud Bus。

### 4.1 客户端

我们需要更新云配置客户端，以便它可以订阅RabbitMQ交换机：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
    <version>2.2.1.RELEASE</version>
</dependency>
```

最新版本可以在[这里](https://search.maven.org/search?q=spring-cloud-starter-bus-amqp)找到。

要完成配置客户端更改，我们需要添加RabbitMQ详细信息并在application.yml文件中启用Cloud Bus：

```yaml
---
spring:
    rabbitmq:
        host: localhost
        port: 5672
        username: guest
        password: guest
    cloud:
        bus:
            enabled: true
            refresh:
                enabled: true
```

请注意，我们使用的是默认用户名和密码。这需要针对现实生活中的生产应用程序进行更新。对于本教程，这很好。

现在，客户端将有另一个端点”/bus-refresh“。调用此端点将导致：

-  从配置服务器获取最新配置并更新由@RefreshScope标注的配置
-  向AMQP交换器发送消息，通知刷新事件
-  所有订阅的节点也将更新它们的配置

这样，我们就不需要去各个节点触发配置更新。

### 4.2 服务器

最后，让我们向配置服务器添加两个依赖项以完全自动化配置更改。

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-monitor</artifactId>
    <version>2.2.2.RELEASE</version>
</dependency>
```

最新版本可以在[此处](https://search.maven.org/search?q=a:spring-cloud-config-monitor)找到。

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
    <version>3.0.4.RELEASE</version>
</dependency>
```

最新版本可在[此处](https://search.maven.org/search?q=a:spring-cloud-starter-stream-rabbit)找到。

我们将使用spring-cloud-config-monitor来监视配置更改并使用RabbitMQ作为传输的广播事件。

我们只需要更新application.properties并提供RabbitMQ详细信息：

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

### 4.3 GitHub Webhook

现在一切都准备好了。一旦服务器收到有关配置更改的通知，它会将此作为消息广播到RabbitMQ。当发送配置更改事件时，客户端将监听消息并更新其配置。但是，服务器现在将如何进行修改？

我们需要配置一个GitHub Webhook。让我们转到GitHub并打开我们保存配置属性的仓库。现在，让我们选择”Settings“和”Webhook“。让我们点击“Add webhook”按钮。

有效负载URL是我们的配置服务器“/monitor”端点的URL。在我们的例子中，URL将是这样的：

```shell
http://root:s3cr3t@REMOTE_IP:8888/monitor
```

我们只需要将下拉菜单中的Content type更改为application/json即可。接下来，请将Secret保留为空并单击”Add webhook“按钮-之后，我们就都设置好了。

## 5. 测试

让我们确保所有应用程序都在运行。如果我们返回并检查客户端，它会将user.role显示为“Programmer”，将user.password显示为“d3v3L”：

```shell
$> curl http://localhost:8080/whoami/Mr_Pink
Hello Mr_Pink! You are a(n) Programmer and your password is 'd3v3L'.
```

以前，我们必须使用“/refresh”端点来重新加载配置更改。让我们打开属性文件，将user.role改回Developer并推送更改：

```properties
user.role=Programmer
```

如果我们现在检查客户端，我们将看到：

```shell
$> curl http://localhost:8080/whoami/Mr_Pink
Hello Mr_Pink! You are a(n) Developer and your password is 'd3v3L'.
```

配置客户端几乎同时更新了它的配置而无需重新启动和显式刷新。我们可以回到GitHub并打开最近创建的Webhook。在最底部，有最近的交付。我们可以选择列表顶部的一个(假设这是第一个更改-反正只有一个)并检查已发送到配置服务器的JSON。

我们还可以检查配置和服务器日志，我们将看到条目：

```shell
o.s.cloud.bus.event.RefreshListener: Received remote refresh request. Keys refreshed []
```

## 6. 总结

在本文中，我们采用了现有的Spring Cloud配置服务器和客户端，并添加了Actuator端点来刷新客户端配置。接下来，我们使用Spring Cloud Bus来广播配置更改和自动化客户端更新。我们还配置了GitHub Webhook并测试了整个设置。