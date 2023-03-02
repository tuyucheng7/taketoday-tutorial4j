## 1. 概述

在本教程中，我们将介绍如何使用Profile禁用Spring Cloud的客户端服务发现。这在我们希望在不对代码进行任何更改的情况下启用/禁用服务发现的情况下非常有用。

## 2. 设置Eureka Server和Eureka Client

让我们从创建Eureka Server和Discovery Client开始。

首先，我们可以使用[Spring Cloud Netflix Eureka教程](https://www.baeldung.com/spring-cloud-netflix-eureka)的第2部分设置我们的Eureka服务器。

### 2.1 Discovery Client设置

下一部分是创建另一个将在服务器上注册自己的应用程序。让我们将此应用程序设置为Discovery Client。

让我们将[Web](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-web/3.0.3)和[Eureka Client](https://search.maven.org/search?q=spring-cloud-starter-netflix-eureka-client)启动器依赖项添加到我们的pom.xml中：

```xml
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
```

我们还需要确保我们的Cloud Starter出现在依赖管理部分，并且Spring Cloud版本已设置。

使用[Spring Initializr](https://start.spring.io/)创建项目时，这些已经设置好了。如果没有，我们可以将它们添加到我们的pom.xml文件中：

```xml
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

一旦我们有了依赖项，我们需要做的就是将我们新的客户端应用程序的配置属性添加到application.properties文件中：

```properties
eureka.client.serviceUrl.defaultZone=${EUREKA_URI:http://localhost:8761/eureka}
eureka.instance.preferIpAddress=false
spring.application.name=spring-cloud-eureka-client
```

这将确保当应用程序启动时，它会在Eureka服务器上注册自己，该服务器位于上面指定的URL。它将被称为spring-cloud-eureka-client。

我们应该注意，通常情况下，我们还会在配置类上使用@EnableDiscoveryClient注解来启用Discovery Clients。但是，如果我们使用Spring Cloud启动器，则不需要注解。默认情况下启用Discovery Client。另外，当它在类路径上找到Netflix Eureka Client时，它会自动配置它。

### 2.3 HelloWorldController

为了测试我们的应用程序，我们需要一个可以访问的示例URL。让我们创建一个简单的控制器，它将返回一条问候消息：

```java
@RestController
public class HelloWorldController {

	@RequestMapping("/hello")
	public String hello() {
		return "Hello World!";
	}
}
```

现在，是时候运行Eureka Server和Discovery Client了。当我们运行应用程序时，Discovery Client将向Eureka Server注册。我们可以在Eureka Server仪表板上看到相同的内容：

<img src="../assets/img_1.png">

## 3. 基于Profile的配置

在某些情况下，我们可能希望禁用服务注册。一个原因可能是环境。

例如，我们可能希望在本地开发环境中禁用Discovery Clients，因为每次我们想在本地进行测试时都不需要运行Eureka服务器。让我们看看如何实现这一目标。

我们将更改application.properties文件中的属性以启用和禁用每个Profile的Discovery Client。

### 3.1 使用单独的属性文件

一种简单且流行的方法是[为每个环境使用单独的属性文件](https://www.baeldung.com/spring-profiles#2-profile-specific-properties-files)。

因此，让我们创建另一个名为application-dev.properties的属性文件：

```properties
spring.cloud.discovery.enabled=false
```

我们可以使用spring.cloud.discovery.enabled属性启用/禁用Discovery Client。我们已将其设置为false以禁用Discovery Client。

当dev Profile处于激活状态时，将使用此文件而不是原始属性文件。

### 3.2 使用多文档文件

如果我们不想为每个环境使用单独的文件，另一种选择是使用[多文档属性文件](https://www.baeldung.com/spring-profiles#3-multi-document-files)。

我们将添加两个属性来执行此操作：

```properties
#---
spring.config.activate.on-profile=dev
spring.cloud.discovery.enabled=false
```

对于这种技术，我们使用“#—”将我们的属性文件分成两部分。此外，我们将使用spring.config.activate.on-profile属性。这两行结合使用，**指示应用程序仅在Profile处于激活状态时才读取当前部件中定义的属性**。在我们的例子中，我们将使用dev Profile。

与之前一样，我们将spring.cloud.discovery.enabled属性设置为false。

这将禁用dev Profile中的Discovery Client，但在Profile未处于激活状态时保持启用状态。

## 4. 测试

现在，是时候运行Eureka Server和Discovery Client并测试一切是否按预期工作了。我们还没有添加Profile。当我们运行应用程序时，Discovery Client将向Eureka Server注册。我们可以在Eureka Server仪表板上看到相同的内容：

<img src="../assets/img_1.png">

### 4.1 使用Profile进行测试

接下来，我们将在运行应用程序时添加Profile。我们可以添加命令行参数-Dspring.profiles.active=dev以启用dev Profile。当我们运行应用程序时，我们可以看到客户端这次没有向Eureka Server注册：

<img src="../assets/img.png">

## 5. 总结

在本教程中，我们学习了如何使用属性来添加基于Profile的配置。我们使用相同的方法根据激活的Profile禁用Discovery Client。