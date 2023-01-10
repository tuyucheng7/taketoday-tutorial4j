## 1. 概述

在本教程中，我们将了解如何使用profiles禁用Spring Cloud的Discovery客户端。
当我们希望在不更改代码的情况下启用/禁用服务发现的情况下，这可能很有用。

## 2. 构建Eureka Server和Eureka Client

让我们从创建Eureka Server和Discovery Client开始。

首先，我们可以参考[Spring Cloud Netflix Eureka](Spring-Cloud-Netflix-Eureka介绍.md)教程的第2节来构建我们的Eureka Server。

### 2.1 Discovery Client

接下来是创建另一个将在服务器上注册的应用程序。让我们将此应用程序配置为Discovery Client。

让我们将Web和Eureka Client starter依赖项添加到我们的pom.xml中：

```
<dependencies>
  <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
</dependencies>

<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-parent</artifactId>
      <version>${spring-cloud-dependencies.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<properties>
  <spring-cloud-dependencies.version>2021.0.1</spring-cloud-dependencies.version>
</properties>
```

### 2.2 添加配置属性

```
eureka.client.serviceUrl.defaultZone=${EUREKA_URI:http://localhost:8761/eureka}
eureka.instance.preferIpAddress=false
spring.application.name=spring-cloud-eureka-client
```

这将确保当应用程序启动时，它会在Eureka服务器上注册自己，服务器地址为上面指定的URL。该应用程序名为spring-cloud-eureka-client。

我们应该注意，通常我们还会在配置类上使用@EnableDiscoveryClient注解来启用服务发现。
但是，如果我们使用Spring Cloud starters，则不需要该注解。服务发现默认启用。
另外，当它在类路径上找到Netflix Eureka Client时，它会自动配置它。

### 2.3 Hello World Controller

为了测试我们的应用程序，我们需要一个可以访问的URL。让我们创建一个返回"Hello World"的简单控制器：

```java

@RestController
public class HelloWorldController {

  @RequestMapping("/hello")
  public String hello() {
    return "Hello World!";
  }
}
```

现在，我们可以运行Eureka Server和Discovery Client。当我们运行应用程序时，Discovery Client将注册到Eureka Server。
我们可以在Eureka Server仪表板上看到注册的服务：

<img src="../../assets/eureka-3.png">

## 3. 基于Profile的配置

在某些情况下，我们可能想要禁用服务注册。一个原因可能是环境。

例如，我们可能希望在本地开发环境中禁用服务发现，因为每次我们想要在本地测试时都运行Eureka Server是不必要的。
让我们看看如何实现这一目标。

我们将更改application.properties文件中的属性以启用和禁用每个Profile的服务发现。

### 3.1 使用单独的属性文件

一种简单且常用的方法是在每个环境中使用单独的属性文件。

因此，让我们创建另一个名为application-dev.properties的属性文件：

```properties
spring.cloud.discovery.enabled=false
```

我们可以使用spring.cloud.discovery.enabled属性启用/禁用服务发现。我们将其设置为false以禁用Discovery Client。

当dev profile处于激活状态时，将使用此文件而不是原始属性文件。

### 3.2 使用多文档文件

如果我们不想在每个环境中使用单独的文件，还有一种选择是使用多文档属性文件。

我们将在application.properties中添加两个属性：

```properties
#---
spring.config.activate.on-profile=dev
spring.cloud.discovery.enabled=false
```

我们使用“#-”将我们的属性文件分成两部分。此外，我们将使用spring.config.activate.on-profile属性。
这两行结合使用，指示应用程序仅在Profile处于激活状态时才读取当前部分中定义的属性。在我们的例子中，我们将使用dev Profile。

与之前一样，我们将spring.cloud.discovery.enabled属性设置为false。

这将禁用dev Profile中的Discovery Client，但在dev不处于激活状态时保持开启。

## 4. 测试

现在，我们可以运行Eureka Server和Discovery Client并测试一切是否按预期工作。
我们还没有添加Profile。当我们运行应用程序时，Discovery Client将注册到Eureka Server。
我们可以在Eureka Server仪表板上：

<img src="../../assets/eureka-3.png">

### 4.1 使用Profile进行测试

接下来，我们将在运行应用程序时添加Profile。我们可以添加命令行参数-Dspring.profiles.active=dev来启用dev Profile。
在IDEA中，我们在以下位置简单配置：

<img src="../../assets/eureka-4.png">

当我们运行应用程序时，我们可以看到客户端这次没有向Eureka Server注册：

<img src="../../assets/eureka-5.png">