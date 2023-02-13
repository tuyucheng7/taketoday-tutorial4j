## 1. 概述

Spring Boot为许多配置属性提供了合理的默认值，但有时我们需要使用特定于案例的值来自定义这些属性。

一个常见的用例是**更改嵌入式服务器的默认监听端口**。

在本快速教程中，我们将介绍实现此目的的几种方法。

## 2. 使用属性文件

自定义Spring Boot最快最简单的方法是覆盖默认属性的值。

**对于服务器端口，我们要更改的属性是server.port**。

默认情况下，嵌入式服务器在端口8080上启动。

**那么，让我们看看如何在application.properties文件中提供不同的值**：

```properties
server.port=8081
```

现在服务器将在端口8081上启动。

如果我们使用application.yml文件，我们也可以这样做：

```yaml
server:
    port: 8081
```

如果将这两个文件放在Maven应用程序的src/main/resources目录中，Spring Boot会自动加载这两个文件。

### 2.1 特定于环境的端口

如果我们有一个应用程序部署在不同的环境中，我们可能希望它在每个系统的不同端口上运行。

我们可以通过将属性文件方法与Spring Profile相结合来轻松实现这一点。**具体来说，我们可以为每个环境创建一个属性文件**。

例如，我们将有一个包含以下内容的application-dev.properties文件：

```properties
server.port=8081
```

然后我们将添加另一个具有不同端口值的application-qa.properties文件：

```properties
server.port=8082
```

现在，属性文件配置应该足以满足大多数情况。但是，对于此目标还有其他选择，所以让我们也探讨一下。

## 3. 编程化配置

我们可以通过在启动应用程序时设置特定属性或自定义嵌入式服务器配置来以编程方式配置端口。

首先，让我们看看如何在@SpringBootApplication主类中设置属性：

```java
@SpringBootApplication
public class ChangeApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ChangeApplication.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "8083"));
        app.run(args);
    }
}
```

接下来，要自定义服务器配置，我们必须实现WebServerFactoryCustomizer接口：

```java
@Component
public class ServerPortCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        factory.setPort(8086);
    }
}
```

请注意，这只适用于Spring Boot 2.x版本。

对于Spring Boot 1.x，我们可以类似地实现EmbeddedServletContainerCustomizer接口。

## 4. 使用命令行参数

将我们的应用程序打包并运行为jar时，我们可以使用java命令设置server.port参数：

```shell
java -jar spring-boot-basic-customization-1.jar --server.port=8083
```

或者使用等效的语法：

```shell
java -jar -Dserver.port=8083 spring-boot-basic-customization-1.jar
```

## 5. 配置的优先级

最后，让我们看一下Spring Boot评估这些方法的顺序。

基本上，配置优先级为：

+ 嵌入式服务器配置
+ 命令行参数
+ 属性文件
+ @SpringBootApplication主类配置

## 6. 总结

在本文中，我们了解了如何在Spring Boot应用程序中配置服务器端口。