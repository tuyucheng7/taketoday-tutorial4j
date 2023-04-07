## 1. 概述

Spring Boot 为 Spring 生态系统带来了一种自以为是的方法。首次发布于 2014 年年中。Spring Boot 经历了很多发展和改进。它的 2.0 版今天准备在 2018 年初发布。

这个流行的图书馆试图在不同的领域帮助我们：

-   依赖管理。通过启动器和各种包管理器集成
-   自动配置。尝试最小化 Spring 应用程序准备运行所需的配置量，并支持约定优于配置
-   生产就绪功能。例如Actuator、更好的日志记录、监控、指标或各种 PaaS 集成
-   增强开发体验。使用多个测试实用程序或使用spring-boot-devtools 的更好的反馈循环

在本文中，我们将探讨为Spring Boot2.0 计划的一些更改和功能。我们还将描述这些变化如何帮助我们提高工作效率。

## 2.依赖关系

### 2.1.Java基线

Spring Boot 2.x 将不再支持Java 7及以下版本，Java 8是最低要求。

它也是第一个支持Java 9的版本。没有计划在 1.x 分支上支持Java 9。这意味着如果你想使用最新的Java版本并利用这个框架，Spring Boot 2.x 是你唯一的选择。

### 2.2. 材料清单

随着Spring Boot的每个新版本的发布，Java 生态系统的各种依赖项的版本都会得到升级。这是在 Boot [bill of materials aka BOM](https://www.baeldung.com/spring-maven-bom)中定义的。

在 2.x 中也不例外。列出它们没有意义，但我们可以查看[spring-boot-dependencies.pom](https://github.com/spring-projects/spring-boot/blob/2.0.x/spring-boot-project/spring-boot-dependencies/pom.xml)以查看在任何给定时间点正在使用的版本。

关于最低要求版本的一些亮点：

-   Tomcat 最低支持版本为 8.5
-   Hibernate 最低支持版本是 5.2
-   Gradle 最低支持版本为 3.4

### 2.3. 摇篮插件

Gradle 插件已经过重大改进和一些重大更改。

为了创建 fat jar，bootRepackage Gradle 的任务被替换为bootJar和bootWar以分别构建 jar 和 wars。

如果我们想使用 Gradle 插件运行我们的应用程序，在 1.x 中，我们可以执行gradle bootRun。 在 2.x 中，bootRun扩展了 Gradle 的JavaExec。这意味着我们可以更轻松地使用我们通常在经典JavaExec任务中使用的相同配置来配置它。

有时我们发现自己想要利用Spring BootBOM。但有时我们不想构建完整的 Boot 应用程序或将其重新打包。

在这方面，有趣的是，Spring Boot 2.x 将不再默认应用依赖管理插件。

如果我们想要Spring Boot依赖管理，我们应该添加：

```groovy
apply plugin: 'io.spring.dependency-management'
```

这为我们在上述场景中以更少的配置提供了更大的灵活性。

## 3.自动配置

### 3.1. 安全

在 2.x 中，安全配置得到了极大的简化。默认情况下，一切都是安全的，包括静态资源和执行器端点。

一旦用户显式配置安全性，Spring Boot 默认值将停止影响。然后用户可以在一个地方配置所有访问规则。

这将防止我们在WebSecurityConfigurerAdapter排序问题上挣扎。这些问题通常在以自定义方式配置 Actuator 和 App 安全规则时发生。

让我们看一下混合执行器和应用程序规则的简单安全片段：

```java
http.authorizeRequests()
  .requestMatchers(EndpointRequest.to("health"))
    .permitAll() // Actuator rules per endpoint
  .requestMatchers(EndpointRequest.toAnyEndpoint())
    .hasRole("admin") // Actuator general rules
  .requestMatchers(PathRequest.toStaticResources().atCommonLocations()) 
    .permitAll() // Static resource security 
  .antMatchers("/**") 
    .hasRole("user") // Application security rules 
  // ...
```

### 3.2. 反应性支持

Spring Boot 2 为不同的反应模块带来了一组新的启动器。一些示例是 WebFlux，以及 MongoDB、Cassandra 或 Redis 的响应式对应物。

还有用于 WebFlux 的测试实用程序。特别是，我们可以利用@WebFluxTest。这与最初在 1.4.0 中作为各种测试片段的一部分引入的旧@WebMvcTest类似。

## 4. 生产就绪功能

Spring Boot 带来了一些有用的工具，使我们能够创建生产就绪的应用程序。除此之外，我们还可以利用Spring BootActuator。

Actuator 包含各种用于简化监控、跟踪和一般应用自省的工具。有关执行器的更多详细信息，请参阅我们[之前的文章](https://www.baeldung.com/spring-boot-actuators)。

其 2 版执行器已通过重大改进。此迭代专注于简化定制。它还支持其他网络技术，包括新的反应模块。

### 4.1. 技术支持

在Spring Boot 1.x中，执行器端点仅支持 Spring-MVC。然而，在 2.x 中，它变得独立且可插入。Spring Boot 现在为 WebFlux、Jersey 和 Spring-MVC 带来了开箱即用的支持。

和以前一样，JMX 仍然是一个选项，可以通过配置启用或禁用。

### 4.2. 创建自定义端点

新的执行器基础设施与技术无关。正因为如此，开发模型已经从头开始重新设计。

新模型还带来了更大的灵活性和表现力。

让我们看看如何为执行器创建Fruits端点：

```java
@Endpoint(id = "fruits")
public class FruitsEndpoint {

    @ReadOperation
    public Map<String, Fruit> fruits() { ... }

    @WriteOperation
    public void addFruits(@Selector String name, Fruit fruit) { ... }
}
```

一旦我们在ApplicationContext 中注册了FruitsEndpoint ，就可以使用我们选择的技术将其公开为 Web 端点。我们也可以根据我们的配置通过 JMX 公开它。

将我们的端点转换为 Web 端点，这将导致：

-   GET在/application/fruits返回水果
-   在/applications/fruits/{a-fruit}上发布处理应包含在有效负载中的水果

还有更多的可能性。我们可以检索更细粒度的数据。此外，我们可以为每个底层技术定义特定的实现(例如，JMX 与 Web)。出于本文的目的，我们将把它作为一个简单的介绍，而不涉及太多细节。

### 4.3. 执行器的安全性

在Spring Boot 1.x中，Actuator 定义了自己的安全模型。此安全模型与我们的应用程序使用的安全模型不同。

当用户试图改进安全性时，这是许多痛点的根源。

在 2.x 中，安全配置应该使用与应用程序其余部分相同的配置进行配置。

默认情况下，大多数执行器端点都是禁用的。这与 Spring Security 是否在类路径中无关。除了状态和信息之外，所有其他端点都需要由用户启用。

### 4.4. 其他重要变化

-   大多数配置属性现在都在management.xxx下，例如：management.endpoints.jmx
-   某些端点具有新格式。例如：env、flyway或liquibase
-   预定义端点路径不再可配置

## 5. 增强开发体验

### 5.1. 更好的反馈

Spring Boot在 1.3 中引入了devtools 。

它负责解决典型的开发问题。例如，视图技术的缓存。它还执行自动重启和浏览器实时重新加载。此外，它使我们能够远程调试应用程序。

在 2.x 中，当我们的应用程序被devtools重新启动时，将打印出“delta”报告。这份报告将指出发生了什么变化以及它可能对我们的应用程序产生的影响。

假设我们定义了一个 JDBC 数据源来覆盖由Spring Boot配置的数据源。

D evtools将指示不再创建自动配置的那个。它还将指出原因，即@ConditionalOnMissingBean中类型javax.sql.DataSource 的不利匹配。执行重启后，Devtools将打印此报告。

### 5.2. 重大变化

由于 JDK 9 问题，devtools 正在放弃对通过 HTTP 进行远程调试的支持。

## 6.总结

在本文中，我们介绍了Spring Boot 2将带来的一些变化。

我们讨论了依赖关系以及Java 8如何成为最低支持版本。

接下来，我们讨论了自动配置。我们专注于安全等。我们还讨论了执行器及其收到的许多改进。

最后，我们讨论了所提供的开发工具中发生的一些小调整。