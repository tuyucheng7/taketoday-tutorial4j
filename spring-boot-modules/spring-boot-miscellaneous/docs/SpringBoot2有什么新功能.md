## 1. 概述

Spring Boot为Spring生态系统带来了一种自以为是的方法，于2014年年中首次发布。Spring Boot经历了大量的发展和改进。它的2.0版今天准备在2018年初发布。

这个流行的库试图在不同的领域帮助我们：

-   依赖管理：通过启动器和各种包管理器集成
-   自动配置：尝试最小化Spring应用程序准备运行所需的配置量，并倾向于约定而不是配置
-   生产就绪功能：例如Actuator、更好的日志记录、监控、指标或各种PaaS集成
-   增强开发体验：使用多个测试实用程序或使用spring-boot-devtools提供更好的反馈循环

在本文中，我们将探讨为Spring Boot 2.0计划的一些更改和功能。我们还将描述这些变化如何帮助我们提高工作效率。

## 2. 依赖关系

### 2.1 Java基线

**Spring Boot 2.x将不再支持Java 7及以下版本**，Java 8是最低要求。

它也是第一个支持Java 9的版本。没有计划在1.x分支上支持Java 9。这意味着**如果你想使用最新的Java版本并利用这个框架，Spring Boot 2.x是你唯一的选择**。

### 2.2 BOM

随着Spring Boot的每个新版本的发布，Java生态系统的各种依赖项的版本都会得到升级。这是在Boot [bill of materials(又名BOM)](https://www.baeldung.com/spring-maven-bom)中定义的。

在2.x中也不例外。列出它们没有意义，但我们可以检查[spring-boot-dependencies.pom](https://github.com/spring-projects/spring-boot/blob/2.0.x/spring-boot-project/spring-boot-dependencies/pom.xml)以查看在任何给定时间点正在使用的版本。

关于最低要求版本的一些亮点：

-   Tomcat最低支持版本为8.5
-   Hibernate最低支持版本为5.2
-   Gradle最低支持版本为3.4

### 2.3 Gradle插件

**Gradle插件经历了重大改进和一些重大更改**。

**为了创建fat jar，Gradle的bootRepackage任务被替换为bootJar和bootWar以分别构建jar和war**。

如果我们想使用Gradle插件运行我们的应用程序，在1.x中，我们可以执行gradle bootRun。**在2.x中，bootRun扩展了Gradle的JavaExec**。这意味着我们可以更轻松地使用我们通常在经典JavaExec任务中使用的相同配置来配置它。

有时我们发现自己想要利用Spring Boot BOM。但有时我们不想构建完整的Boot应用程序或将其重新打包。

在这方面，有趣的是，**Spring Boot 2.x将不再默认应用依赖管理插件**。

如果我们想要Spring Boot依赖管理，我们应该添加：

```groovy
apply plugin: 'io.spring.dependency-management'
```

在上述情况下，这为我们提供了更大的灵活性和更少的配置。

## 3. 自动配置

### 3.1 安全

在2.x中，安全配置得到了极大的简化。**默认情况下，一切都是安全的，包括静态资源和Actuator端点**。

一旦用户显式配置安全性，Spring Boot默认值将停止影响。然后用户可以在一个位置配置所有访问规则。

这将防止我们在WebSecurityConfigurerAdapter排序问题上挣扎。这些问题通常在以自定义方式配置Actuator和App安全规则时发生。

让我们看一下混合Actuator和应用程序规则的简单安全片段：

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

### 3.2 响应式支持

**Spring Boot 2为不同的响应式模块带来了一组新的启动器。一些示例是WebFlux，以及MongoDB、Cassandra或Redis的响应式对应物**。

还有用于WebFlux的测试实用程序。特别是，我们可以利用@WebFluxTest。这与最初在1.4.0中作为各种测试切片的一部分引入的旧@WebMvcTest类似。

## 4. 生产就绪功能

Spring Boot带来了一些有用的工具，使我们能够创建生产就绪的应用程序。除此之外，我们还可以利用Spring Boot Actuator。

Actuator包含各种用于简化监控、跟踪和常规应用程序自省的工具。有关Actuator的更多详细信息，请参阅我们[之前的文章](https://www.baeldung.com/spring-boot-actuators)。

**2.0版本的Actuator已通过重大改进**。此迭代侧重于简化自定义。它还支持其他Web技术，包括新的响应式模块。

### 4.1 技术支持

**在Spring Boot 1.x中，Actuator端点仅支持Spring MVC。然而，在2.x中，它变得独立且可插拔**。Spring Boot现在为WebFlux、Jersey和Spring MVC带来了开箱即用的支持。

和以前一样，JMX仍然是一个选项，可以通过配置启用或禁用。

### 4.2 创建自定义端点

**新的Actuator基础设施与技术无关。正因为如此，开发模型已经从头开始重新设计**。

新模型还带来了更大的灵活性和表现力。

让我们看看如何为Actuator创建fruits端点：

```java
@Endpoint(id = "fruits")
public class FruitsEndpoint {

    @ReadOperation
    public Map<String, Fruit> fruits() {
        // ... 
    }

    @WriteOperation
    public void addFruits(@Selector String name, Fruit fruit) {
        // ... 
    }
}
```

一旦我们在ApplicationContext中注册了FruitsEndpoint，就可以使用我们选择的技术将其公开为Web端点。我们也可以根据我们的配置通过JMX公开它。

将我们的端点转换为Web端点，这将导致：

-   /application/fruits上的GET请求返回水果
-   /applications/fruits/{a-fruit}上的POST处理应包含在有效负载中的水果

还有更多的可能性。我们可以检索更细粒度的数据。此外，我们可以为每个底层技术定义特定的实现(例如，JMX与Web)。出于本文的目的，我们将把它作为一个简单的介绍，而不会涉及太多细节。

### 4.3 Actuator的安全性

在Spring Boot 1.x中，Actuator定义了自己的安全模型。此安全模型与我们的应用程序使用的安全模型不同。

当用户试图改进安全性时，这是许多痛点的根源。

**在2.x中，安全配置应该使用与应用程序其余部分相同的配置进行配置**。

默认情况下，大多数Actuator端点都处于禁用状态。这与Spring Security是否存在于类路径中无关。除了status和info之外，所有其他端点都需要由用户启用。

### 4.4 其他重要变化

-   大多数配置属性现在都在management.xxx下，例如：management.endpoints.jmx
-   某些端点具有新格式。例如：env、flyway或liquibase
-   预定义端点路径不再可配置

## 5. 增强开发体验

### 5.1 更好的反馈

Spring Boot在1.3中引入了devtools。

它负责解决典型的开发问题。例如，视图技术的缓存。它还执行自动重启和浏览器实时重新加载。此外，它还使我们能够远程调试应用程序。

**在2.x中，当我们的应用程序由devtools重新启动时，将打印出“delta(增量)”报告**。这份报告将指出发生了什么变化以及它可能对我们的应用程序产生的影响。

假设我们定义了一个JDBC Datasource来覆盖由Spring Boot配置的数据源。

Devtools将指示不再创建自动配置的那个。它还将指出原因，即@ConditionalOnMissingBean中类型javax.sql.DataSource的不利匹配。执行重启后，Devtools将打印此报告。

### 5.2 重大变化

由于JDK 9问题，devtools正在放弃对通过HTTP进行远程调试的支持。

## 6. 总结

在本文中，我们介绍了Spring Boot 2将带来的一些变化。

我们讨论了依赖关系以及Java 8如何成为最低支持版本。

接下来，我们讨论了自动配置、Actuator及其获得的许多改进。

最后，我们讨论了所提供的开发工具中发生的一些小调整。