## 1. 简介

在本教程中，我们介绍在运行时更改Spring Boot应用程序的日志记录级别的方法。与许多事情一样，[Spring Boot具有内置的日志记录功能]()，可以为我们配置它。我们将探讨如何调整正在运行的应用程序的日志记录级别。

我们将介绍三种方法：使用[Spring Boot Actuator](https://www.baeldung.com/spring-boot-actuators)的loggers端点、[Logback]()中的自动扫描功能以及最后使用[Spring Boot Admin]()工具。

## 2. Spring Boot Actuator

我们将首先使用/loggers Actuator端点来显示和更改我们的日志记录级别。**/loggers端点在actuator/loggers处可用，我们可以通过将其名称附加为路径的一部分来访问特定的记录器**。

例如，我们可以使用 URL [http://localhost:8080/actuator/loggers/root](http://localhost:8080/actuator/loggers/root)访问根记录器。

### 2.1 设置

首先设置我们的应用程序以使用Spring Boot Actuator。

因此我们需要将[Spring Boot Actuator Maven依赖](https://search.maven.org/search?q=spring-boot-starter-actuator)添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
    <version>2.4.0</version>
</dependency>
```

从Spring Boot 2.x开始，默认情况下禁用大多数端点，因此我们还需要在application.properties文件中启用/loggers端点：

```properties
management.endpoints.web.exposure.include=loggers
management.endpoint.loggers.enabled=true
```

最后，我们创建一个带有一系列日志语句的控制器，以便我们可以看到我们实验的效果：

```java
@RestController
@RequestMapping("/log")
public class LoggingController {
    private Log log = LogFactory.getLog(LoggingController.class);

    @GetMapping
    public String log() {
        log.trace("This is a TRACE level message");
        log.debug("This is a DEBUG level message");
        log.info("This is an INFO level message");
        log.warn("This is a WARN level message");
        log.error("This is an ERROR level message");
        return "See the log for details";
    }
}
```

### 2.2 使用/loggers端点

现在启动我们的应用程序并访问我们的log API：

```shell
curl http://localhost:8080/log
```

然后，让我们检查日志，我们应该在其中找到三个日志语句：

```shell
2022-12-10 09:51:53.498  INFO 12208 --- [nio-8080-exec-1] c.t.t.s.b.m.logging.LoggingController      : This is an INFO level message
2022-12-10 09:51:53.498  WARN 12208 --- [nio-8080-exec-1] c.t.t.s.b.m.logging.LoggingController      : This is a WARN level message
2022-12-10 09:51:53.498 ERROR 12208 --- [nio-8080-exec-1] c.t.t.s.b.m.logging.LoggingController      : This is an ERROR level message
```

现在，让我们调用/loggers Actuator端点来检查我们的cn.tuyucheng.taketoday.spring.boot.management.logging包的日志级别：

```shell
curl http://localhost:8080/actuator/loggers/cn.tuyucheng.taketoday.spring.boot.management.logging
  {"configuredLevel":null,"effectiveLevel":"INFO"}
```

要更改日志记录级别，我们可以向 / loggers端点发出POST请求：

```shell
curl -i -X POST -H 'Content-Type: application/json' -d '{"configuredLevel": "TRACE"}'
  http://localhost:8080/actuator/loggers/cn.tuyucheng.taketoday.spring.boot.management.logging
  HTTP/1.1 204
  Date: Mon, 02 Sep 2022 13:56:52 GMT
```

如果我们再次检查日志记录级别，我们应该看到它设置为TRACE：

```shell
curl http://localhost:8080/actuator/loggers/cn.tuyucheng.taketoday.spring.boot.management.logging
  {"configuredLevel":"TRACE","effectiveLevel":"TRACE"}
```

最后，我们可以重新运行我们的日志API并查看我们的更改：

```shell
curl http://localhost:8080/log
```

现在，我们再次检查日志：

```shell
2022-12-10 09:59:20.283 TRACE 12208 --- [io-8080-exec-10] c.t.t.s.b.m.logging.LoggingController      : This is a TRACE level message
2022-12-10 09:59:20.283 DEBUG 12208 --- [io-8080-exec-10] c.t.t.s.b.m.logging.LoggingController      : This is a DEBUG level message
2022-12-10 09:59:20.283  INFO 12208 --- [io-8080-exec-10] c.t.t.s.b.m.logging.LoggingController      : This is an INFO level message
2022-12-10 09:59:20.283  WARN 12208 --- [io-8080-exec-10] c.t.t.s.b.m.logging.LoggingController      : This is a WARN level message
2022-12-10 09:59:20.283 ERROR 12208 --- [io-8080-exec-10] c.t.t.s.b.m.logging.LoggingController      : This is an ERROR level message
```

## 3. Logback自动扫描

默认情况下，我们的Spring Boot应用程序使用Logback日志库。现在让我们看看如何利用Logback的自动扫描功能来更改日志记录级别。

首先，让我们通过在src/main/resources目录下放置一个名为logback.xml的文件来添加一些Logback配置：

```xml
<configuration scan="true" scanPeriod="15 seconds">
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="cn.tuyucheng.taketoday.spring.boot.management.logging" level="INFO" />

    <root level="info">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```

关键细节在logback.xml文件的第一行。通过将scan属性设置为true，我们告诉Logback检查配置文件的更改。默认情况下，自动扫描每60秒发生一次。

将scanPeriod设置为15秒会告诉它每15秒重新加载一次，这样我们就不必在测试期间等待那么久。

让我们通过启动应用程序并再次调用我们的日志API来尝试一下：

```shell
curl http://localhost:8080/log
```

我们的输出应该反映我们包的INFO日志记录级别：

```shell
10:21:13.167 [http-nio-8080-exec-1] INFO  c.t.t.s.b.m.logging.LoggingController - This is an INFO level message
10:21:13.167 [http-nio-8080-exec-1] WARN  c.t.t.s.b.m.logging.LoggingController - This is a WARN level message
10:21:13.168 [http-nio-8080-exec-1] ERROR c.t.t.s.b.m.logging.LoggingController - This is an ERROR level message
```

现在，我们将logback.xml中的cn.tuyucheng.taketoday.spring.boot.management.logging记录器修改为TRACE：

```xml
<logger name="cn.tuyucheng.taketoday.spring.boot.management.logging" level="TRACE" />
```

15秒之后，我们在[http://localhost:8080/log](http://localhost:8080/log)重新运行日志API并检查我们的日志输出：

```shell
10:24:18.429 [http-nio-8080-exec-2] TRACE c.t.t.s.b.m.logging.LoggingController - This is a TRACE level message
10:24:18.430 [http-nio-8080-exec-2] DEBUG c.t.t.s.b.m.logging.LoggingController - This is a DEBUG level message
10:24:18.430 [http-nio-8080-exec-2] INFO  c.t.t.s.b.m.logging.LoggingController - This is an INFO level message
10:24:18.430 [http-nio-8080-exec-2] WARN  c.t.t.s.b.m.logging.LoggingController - This is a WARN level message
10:24:18.430 [http-nio-8080-exec-2] ERROR c.t.t.s.b.m.logging.LoggingController - This is an ERROR level message
```

## 4. Spring Boot Admin

更改日志记录级别的第三种方法是通过Spring Boot Admin工具。要使用Spring Boot Admin，我们需要创建一个服务器应用程序并将我们的应用程序配置为客户端。

### 4.1 Admin应用程序

要使用Spring Boot Admin更改我们的日志记录级别，我们需要设置一个新应用程序以用作我们的管理服务器。我们可以为此使用[Spring Initialzr](https://start.spring.io/)。

下面我们[spring-boot-admin-starter-server](https://search.maven.org/search?q=spring-boot-admin-starter-server)添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-server</artifactId>
    <version>2.4.1</version>
</dependency>
```

有关设置Admin服务器的详细说明，请参阅我们的[Spring Boot Admin指南](SpringBoot-Admin指南.md)中的第2节。此外，第4节包括设置安全性所需的信息，因为我们将保护我们的客户。

### 4.2 客户端配置

一旦我们有了Admin服务器，我们就需要将我们的应用程序设置为客户端。

首先，我们为[spring-boot-admin-starter-client](https://search.maven.org/search?q=spring-boot-admin-starter-client)添加一个Maven依赖：

```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-client</artifactId>
    <version>2.4.1</version>
</dependency>
```

我们还需要我们的管理服务器和客户端之间的安全性，所以我们引入[Spring Boot Security starter](https://search.maven.org/search?q=spring-boot-starter-security)：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
    <version>2.4.0</version>
</dependency>
```

接下来，我们需要在application.properties文件中进行一些配置更改。

Admin服务器在端口8080上运行，所以让我们首先更改端口并为应用程序命名：

```properties
spring.application.name=spring-boot-management
server.port=8081
```

现在，让我们添加访问服务器所需的配置：

```properties
spring.security.user.name=client
spring.security.user.password=client

spring.boot.admin.client.url=http://localhost:8080
spring.boot.admin.client.username=admin
spring.boot.admin.client.password=admin

spring.boot.admin.client.instance.metadata.user.name=${spring.security.user.name}
spring.boot.admin.client.instance.metadata.user.password=${spring.security.user.password}
```

这包括运行Admin服务器的URL以及客户端和Admin服务器的登录信息。

最后，我们需要为Admin服务器启用/health、/info和/metrics执行器端点，以便能够确定客户端的状态：

```properties
management.endpoints.web.exposure.include=httptrace,loggers,health,info,metrics
```

因为更改日志记录器级别是一个POST操作，我们还需要添加一些安全配置来忽略执行器端点的CSRF保护：

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.csrf().ignoringAntMatchers("/actuator/");
}
```

### 4.3 使用Spring Boot Admin

配置完成后，我们使用mvn spring-boot:run启动客户端和服务器应用程序。

我们先在[http://localhost:8081/log](http://localhost:8081/log)访问我们的日志API，而不做任何更改。我们现在启用了安全性，所以我们将被要求使用我们在application.properties中指定的凭据登录。

我们的日志输出应该显示反映我们的INFO日志级别的日志消息：

```shell
09:13:23.416 [http-nio-8081-exec-10] INFO  c.t.t.s.b.m.logging.LoggingController - This is an INFO level message
09:13:23.416 [http-nio-8081-exec-10] WARN  c.t.t.s.b.m.logging.LoggingController - This is a WARN level message
09:13:23.416 [http-nio-8081-exec-10] ERROR c.t.t.s.b.m.logging.LoggingController - This is an ERROR level message
```

现在，让我们登录到Spring Boot Admin服务器并更改我们的日志记录级别。让我们转到http://localhost:8080并使用管理员凭据登录。我们将被带到已注册应用程序列表，我们应该在其中看到我们的spring-boot-management应用程序：

[![管理应用列表](https://www.baeldung.com/wp-content/uploads/2019/09/admin_application_list.jpg)](https://www.baeldung.com/wp-content/uploads/2019/09/admin_application_list.jpg)

让我们选择spring-boot-management并使用左侧菜单查看Loggers：

[![管理员应用记录器默认](https://www.baeldung.com/wp-content/uploads/2019/09/admin_app_loggers_default.jpg)](https://www.baeldung.com/wp-content/uploads/2019/09/admin_app_loggers_default.jpg)

cn.tuyucheng.taketoday.spring.boot.management.logging记录器设置为INFO。让我们将其更改为TRACE并重新运行我们的日志API：

[![管理应用程序记录器跟踪](https://www.baeldung.com/wp-content/uploads/2019/09/admin_app_loggers_trace.jpg)](https://www.baeldung.com/wp-content/uploads/2019/09/admin_app_loggers_trace.jpg)

我们的日志输出现在应该反映新的记录器级别：

```shell
10:13:56.376 [http-nio-8081-exec-4] TRACE c.t.t.s.b.m.logging.LoggingController - This is a TRACE level message
10:13:56.376 [http-nio-8081-exec-4] DEBUG c.t.t.s.b.m.logging.LoggingController - This is a DEBUG level message
10:13:56.376 [http-nio-8081-exec-4] INFO  c.t.t.s.b.m.logging.LoggingController - This is an INFO level message
10:13:56.376 [http-nio-8081-exec-4] WARN  c.t.t.s.b.m.logging.LoggingController - This is a WARN level message
10:13:56.376 [http-nio-8081-exec-4] ERROR c.t.t.s.b.m.logging.LoggingController - This is an ERROR level message
```

## 5. 总结

在本文中，我们探讨了在运行时控制日志记录级别的不同方法。我们开始使用内置的执行器功能。之后，我们使用了Logback的自动扫描功能。

最后，我们学习了如何使用Spring Boot Admin来监视和更改已注册客户端应用程序中的日志记录级别。