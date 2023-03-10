## 一、概述

在本教程中，我们将学习如何将 Spring Boot 应用程序迁移到[3.0 版本](https://www.baeldung.com/spring-boot-3-spring-6-new)。要成功将应用程序迁移到 Spring Boot 3，我们必须确保我们要迁移的应用程序的当前 Spring Boot 版本是 2.7，Java 版本是 17。

## 2. 核心变化

Spring Boot 3.0 标志着该框架的一个重要里程碑，对其核心组件进行了多项重要修改。

### 2.1. 配置属性

修改了一些属性键：

-   *spring.redis*已经迁移到*spring.data.redis*
-   *spring.data.cassandra*已经移动到*spring.cassandra*
-   *spring.jpa.hibernate.use-new-id-generator*已删除
-   [*server.max.http.header.size*](https://www.baeldung.com/spring-boot-max-http-header-size)已移至*server.max-http-request-header-size*
-   *spring.security.saml2.relyingparty.registration.{id}.identity-provider*支持被移除

**为了识别这些属性，我们可以**在我们的*pom.xml中添加****spring-boot-properties-migrator\***：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-properties-migrator</artifactId>
    <scope>runtime</scope>
</dependency>复制
```

最新版本的[spring-boot-properties-migrator](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-properties-migrator/3.0.3)可从 Maven Central 获得。

此依赖项会生成一份报告，在启动时打印已弃用的属性名称，并在运行时临时迁移这些属性。

### 2.2. 雅加达 EE 10

新版 Jakarta EE 10 带来了 Spring Boot 3 相关依赖的更新：

-   Servlet 规范更新至 6.0 版
-   JPA 规范更新到 3.1 版

*因此，如果我们通过从spring-boot-starter*依赖项中排除它们来管理这些依赖项，我们应该确保更新它们。

让我们从更新 JPA 依赖项开始：

```xml
<dependency>
    <groupId>jakarta.persistence</groupId>
    <artifactId>jakarta.persistence-api</artifactId>
    <version>3.1.0</version>
</dependency>复制
```

最新版本的[jakarta.persistence-api](https://central.sonatype.com/artifact/jakarta.persistence/jakarta.persistence-api/3.1.0)可从 Maven Central 获得。

接下来，让我们更新 Servlet 依赖项：

```xml
<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <version>6.0.0</version>
</dependency>复制
```

最新版本的[jakarta.servlet-api](https://central.sonatype.com/artifact/jakarta.servlet/jakarta.servlet-api/6.0.0)可从 Maven Central 获得。

**除了依赖坐标的变化，Jakarta EE 现在使用“ \*jakarta\* ”包而不是“ \*javax\* ”。**因此，在我们更新依赖项之后，我们可能需要更新*导入*语句。

### 2.3. 休眠

如果我们通过将 Hibernate 依赖项从*spring-boot-starter*依赖项中排除来管理它，确保更新它很重要：

```xml
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.1.4.Final</version>
</dependency>复制
```

最新版本的[hibernate-core](https://central.sonatype.com/artifact/org.hibernate.orm/hibernate-core/6.1.4.Final)可从 Maven Central 获得。

### 2.4. 其他变化

此外，此版本还包括核心级别的其他重大更改：

-   Image banner supports removed: 定义[自定义横幅](https://www.baeldung.com/spring-boot-custom-banners)，只有*banner.txt*文件被认为是有效文件
-   日志记录日期格式器：Logback 和 Log4J2 的新默认日期格式是*yyyy-MM-dd'T'HH:mm:ss.SSSXXX*
    如果我们想恢复旧的默认格式，我们需要设置属性日志记录的值*。**application.yaml*上的*pattern.dateformat*为旧值
-   *@ConstructorBinding仅在构造函数级别：对于**[@ConfigurationProperties类](https://www.baeldung.com/configuration-properties-in-spring-boot)*[*，*](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/context/properties/ConstructorBinding.html)不再需要在类型级别使用[@ConstructorBinding](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/context/properties/ConstructorBinding.html)，它应该被删除。 但是，如果一个类或记录有多个构造函数，则必须在所需的构造函数上使用*@ConstructorBinding*来指定哪一个将用于属性绑定。

## 3. Web 应用程序更改

最初，假设我们的应用程序是 Web 应用程序，我们应该考虑进行某些更改。

### 3.1. 尾部斜杠匹配配置

**新的 Spring Boot 版本弃用了配置尾部斜杠匹配的选项，并将其默认值设置为\*false\*。**

例如，让我们用一个简单的 GET 端点定义一个控制器：

```java
@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodosController {
    @GetMapping("/name")
    public List<String> findAllName(){
        return List.of("Hello","World");
    }
}复制
```

现在“ *GET /api/v1/todos/name/* ”默认不再匹配，将导致 HTTP 404 错误。

*[我们可以通过定义一个实现WebMvcConfigurer](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/config/annotation/WebMvcConfigurer.html)*或*[WebFluxConfigurer](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/config/WebFluxConfigurer.html)*的新配置类（如果它是反应式服务）来为所有端点启用尾部斜线匹配：

```java
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
    }

}复制
```

### 3.2. 响应头大小

正如我们已经提到的，属性[*server.max.http.header.size*](https://www.baeldung.com/spring-boot-max-http-header-size)已被弃用，取而代之的是*server.max-http-request-header-size，*它只检查请求标头的大小。要为响应标头也定义一个限制，让我们定义一个新的 bean：

```java
@Configuration
public class ServerConfiguration implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                connector.setProperty("maxHttpResponseHeaderSize", "100000");
            }
        });
    }
}复制
```

如果我们使用 Jetty 而不是 Tomcat，我们应该将[*TomcatServletWebServerFactory* ](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/web/embedded/tomcat/TomcatServletWebServerFactory.html)更改为[*JettyServletWebServerFactory*](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/web/embedded/jetty/JettyServletWebServerFactory.html)。此外，其他嵌入式 Web 容器不支持此功能。

### 3.3. 其他变化

此外，此版本在 Web 应用程序级别还有其他重大变化：

-   优雅关机的阶段：优雅关机的 SmartLifecycle 实现更新了阶段。*Spring 现在在SmartLifecycle.DEFAULT_PHASE – 2048*阶段启动正常关闭，并在*SmartLifecycle.DEFAULT_PHASE – 1024*阶段停止 Web 服务器。
-   [*RestTemplate*](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html)*上的HttpClient*升级：Rest Template 更新其 Apache [HttpClient5](https://hc.apache.org/httpcomponents-client-5.2.x/)版本

## 4.执行器的变化

[执行器](https://www.baeldung.com/spring-boot-actuators)模块中引入了一些重大更改。

### 4.1. 执行器端点消毒

*在以前的版本中，Spring Framework 会自动屏蔽端点/env*和*/configprops*上敏感键的值，这些值显示敏感信息，例如配置属性和环境变量。
在此版本中，Spring 更改了默认情况下更安全的方法。

**现在，它不再只屏蔽某些键，而是默认屏蔽所有键的值。**我们可以通过使用以下值之一设置属性*management.endpoint.env.show-values*（对于*/env*端点）或*management.endpoint.configprops.show-values*（对于 / *configprops端点）来更改此配置：*

-   *NEVER* : 没有显示值
-   *ALWAYS*：显示的所有值
-   *WHEN_AUTHORIZED*：如果用户被授权，则显示所有值。对于[JMX](https://www.baeldung.com/java-management-extensions)，所有用户都被授权。对于 HTTP，只有特定角色才能访问数据。

### 4.2. 其他变化

其他相关更新发生在 Spring Actuator 模块上：

-   Jmx 端点公开：JMX 仅处理健康端点。通过配置属性*management.endpoints.jmx.exposure.include*和*management.endpoints.jmx.exposure.exclude*，我们可以自定义它。
-   *httptrace*端点重命名：此版本将“ */httptrace* ”端点重命名为“ */httpexchanges* ”
-   隔离的*ObjectMapper*：此版本现在隔离了负责序列化执行器端点响应的*ObjectMapper实例。*我们可以通过将*management.endpoints.jackson.isolated-object-mapper*属性设置为*false*来更改此功能。

## 5.春季安全

**Spring Boot 3 仅与 Spring Security 6 兼容。
**

在升级到 Spring Boot 3.0 之前，我们应该先将[我们的 Spring Boot 2.7 应用升级到 Spring Security 5.8](https://docs.spring.io/spring-security/reference/5.8/migration/index.html)。
之后，我们可以将 Spring Security 升级到版本 6 和 Spring Boot 3。

此版本中引入了一些重要更改：

-   *[ReactiveUserDetailsService](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/core/userdetails/ReactiveUserDetailsService.html)* 未自动配置：在存在*[AuthenticationManagerResolver](https://www.baeldung.com/spring-security-authenticationmanagerresolver)的情况下，*不再自动配置*ReactiveUserDetailsService*。
-   [SAML2](https://www.baeldung.com/cs/saml-introduction) Relying Party Configuration：我们之前提到新版本的 Spring boot 不再支持位于*spring.security.saml2.relyingparty.registration.{id}.identity-provider*下的属性。*相反，我们应该使用spring.security.saml2.relyingparty.registration.{id}.asserting-party*
    下的新属性。

## 6.春季批

让我们看看[Spring Batch](https://www.baeldung.com/spring-boot-spring-batch)模块中引入的一些重大变化。

### 6.1. *@EnableBatchProcessing**不*鼓励_

以前，我们可以启用[Spring Batch](https://www.baeldung.com/spring-boot-spring-batch)的自动配置，使用*[@EnableBatchProcessing](https://docs.spring.io/spring-batch/docs/current/api/org/springframework/batch/core/configuration/annotation/EnableBatchProcessing.html)*注释配置类。**如果我们想使用自动配置，新版本的 Spring Boot 不鼓励使用这个注解。**

事实上，使用这个注释（或定义一个实现[*DefaultBatchConfiguration 的*](https://docs.spring.io/spring-batch/docs/current/api/org/springframework/batch/core/configuration/support/DefaultBatchConfiguration.html)bean ）告诉自动配置退出。

### 6.2. 运行多个作业

以前，可以使用 Spring Batch 同时运行多个批处理作业。但是，情况已不再如此。**如果自动配置检测到单个作业，它将在应用程序启动时自动执行。**

因此，如果上下文中存在多个作业，我们需要通过使用 spring.batch.job.name*属性*提供作业名称来指定应在启动时执行的作业。因此，这意味着如果我们要运行多个作业，我们必须为每个作业创建一个单独的应用程序。

或者，我们可以使用 Quartz、Spring Scheduler 等调度程序或其他替代方案来调度作业。

## 七、结论

在本文中，我们学习了如何将 2.7 Spring Boot 应用程序迁移到版本 3，重点关注 Spring 环境的核心组件。[其他模块也发生了](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide)一些变化，如Spring Session、Micrometer、依赖管理等。