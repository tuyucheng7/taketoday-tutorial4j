## 1. 概述

在本教程中，我们将学习如何将Spring Boot应用程序迁移到[3.0版本](https://www.baeldung.com/spring-boot-3-spring-6-new)。要成功地将应用程序迁移到Spring Boot 3，我们必须确保我们要迁移的应用程序的当前Spring Boot版本是2.7，Java版本是17。

## 2. 核心变化

Spring Boot 3.0标志着该框架的一个重要里程碑，对其核心组件进行了多项重要修改。

### 2.1 配置属性

修改了一些属性键：

-   spring.redis已经迁移到spring.data.redis
-   spring.data.cassandra已经迁移到spring.cassandra
-   spring.jpa.hibernate.use-new-id-generator已删除
-   [server.max.http.header.size](https://www.baeldung.com/spring-boot-max-http-header-size)已移至server.max-http-request-header-size
-   spring.security.saml2.relyingparty.registration.{id}.identity-provider支持被移除

**为了识别这些属性，我们可以在我们的pom.xml中添加spring-boot-properties-migrator**：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-properties-migrator</artifactId>
    <scope>runtime</scope>
</dependency>
```

最新版本的[spring-boot-properties-migrator](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-properties-migrator/3.0.5)可从Maven Central获得。

此依赖项会生成一份报告，在启动时打印已弃用的属性名称，并在运行时临时迁移这些属性。

### 2.2 JakartaEE 10

新版本的JakartaEE 10带来了Spring Boot 3相关依赖的更新：

-   Servlet规范更新至6.0版本
-   JPA规范更新到3.1版本

因此，如果我们通过从spring-boot-starter依赖项中排除它们来管理这些依赖项，我们应该确保更新它们。

让我们从更新JPA依赖项开始：

```xml
<dependency>
    <groupId>jakarta.persistence</groupId>
    <artifactId>jakarta.persistence-api</artifactId>
    <version>3.1.0</version>
</dependency>
```

最新版本的[jakarta.persistence-api](https://central.sonatype.com/artifact/jakarta.persistence/jakarta.persistence-api/3.1.0)可从Maven Central获得。

接下来，让我们更新Servlet依赖项：

```xml
<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <version>6.0.0</version>
</dependency>
```

最新版本的[jakarta.servlet-api](https://central.sonatype.com/artifact/jakarta.servlet/jakarta.servlet-api/6.0.0)可从Maven Central获得。

**除了依赖坐标的变化，JakartaEE现在使用“jakarta”包而不是“javax”**。因此，在我们更新依赖项之后，我们可能需要更新导入语句。

### 2.3 Hibernate

如果我们通过将Hibernate依赖项从spring-boot-starter依赖项中排除来管理它，请务必确保更新它：

```xml
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.1.4.Final</version>
</dependency>
```

最新版本的[hibernate-core](https://central.sonatype.com/artifact/org.hibernate.orm/hibernate-core/6.1.4.Final)可从Maven Central获得。

### 2.4 其他变化

此外，此版本还包括核心级别的其他重大更改：

-   图像banner支持已删除：现在定义[自定义banner](https://www.baeldung.com/spring-boot-custom-banners)时，只有banner.txt文件被认为是有效文件
-   日志记录日期格式化程序：Logback和Log4J2的新默认日期格式是yyyy-MM-dd’T’HH:mm:ss.SSSXXX。如果我们想恢复旧的默认格式，我们需要在application.yaml中将属性logging.pattern.dateformat的值设置为旧值
-   @ConstructorBinding仅在构造函数级别：对于[@ConfigurationProperties](https://www.baeldung.com/configuration-properties-in-spring-boot)类，不再需要在类级别使用[@ConstructorBinding](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/context/properties/ConstructorBinding.html)，它应该被删除。但是，如果一个类或记录有多个构造函数，则必须在所需的构造函数上使用@ConstructorBinding来指定哪一个将用于属性绑定。

## 3. Web应用程序更改

最初，假设我们的应用程序是Web应用程序，我们应该考虑进行某些更改。

### 3.1 尾部斜杠匹配配置

**新的Spring Boot版本弃用了配置尾部斜杠匹配的选项，并将其默认值设置为false**。

例如，让我们定义一个具有一个简单GET端点的控制器：

```java
@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodosController {
    @GetMapping("/name")
    public List<String> findAllName(){
        return List.of("Hello","World");
    }
}
```

现在默认情况下，“GET /api/v1/todos/name/”不再匹配，将导致HTTP404错误。

我们可以通过定义一个实现[WebMvcConfigurer](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/config/annotation/WebMvcConfigurer.html)或[WebFluxConfigurer](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/config/WebFluxConfigurer.html)(如果是响应式服务)的新配置类来为所有端点启用尾部斜杠匹配：

```java
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
    }
}
```

### 3.2 响应头大小

正如我们已经提到的，属性[server.max.http.header.size](https://www.baeldung.com/spring-boot-max-http-header-size)已被弃用，取而代之的是server.max-http-request-header-size，它只检查请求标头的大小。要为响应标头也定义一个限制，让我们定义一个新的bean：

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
}
```

如果我们使用Jetty而不是Tomcat，我们应该将[TomcatServletWebServerFactory](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/web/embedded/tomcat/TomcatServletWebServerFactory.html)更改为[JettyServletWebServerFactory](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/web/embedded/jetty/JettyServletWebServerFactory.html)。此外，其他嵌入式Web容器不支持此功能。

### 3.3 其他变化

此外，此版本在Web应用程序级别还有其他重大变化：

-   优雅关机的阶段：优雅关机的SmartLifecycle实现更新了阶段。Spring现在在SmartLifecycle.DEFAULT_PHASE–2048阶段启动优雅关闭，并在SmartLifecycle.DEFAULT_PHASE–1024阶段停止Web服务器。
-   [RestTemplate](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html)上的HttpClient升级：RestTemplate更新其Apache [HttpClient5](https://hc.apache.org/httpcomponents-client-5.2.x/)版本

## 4. Actuator变化

[Actuator](https://www.baeldung.com/spring-boot-actuators)模块中引入了一些重大更改。

### 4.1 Actuator端点清理

在以前的版本中，Spring框架会自动屏蔽端点/env和/configprops上敏感键的值，这些值显示敏感信息，例如配置属性和环境变量。 

在此版本中，Spring更改了默认情况下更安全的方法。

**现在，它不再只屏蔽某些键，而是默认屏蔽所有键的值**。我们可以通过使用以下值之一设置属性management.endpoint.env.show-values(对于/env端点)或management.endpoint.configprops.show-values(对于/configprops端点)来更改此配置：

-   NEVER：不显示任何值
-   ALWAYS：显示所有值
-   WHEN_AUTHORIZED：如果用户获得授权，则显示所有值。对于[JMX](https://www.baeldung.com/java-management-extensions)，所有用户都被授权。对于HTTP，只有特定角色才能访问数据。

### 4.2 其他变化

Spring Actuator模块上发生的其他相关更新：

-   Jmx端点公开：JMX仅处理健康端点。通过配置属性management.endpoints.jmx.exposure.include和management.endpoints.jmx.exposure.exclude，我们可以自定义它。
-   httptrace端点重命名：此版本将“/httptrace”端点重命名为“/httpexchanges”
-   隔离的ObjectMapper：此版本现在隔离了负责序列化Actuator端点响应的ObjectMapper实例。我们可以通过将management.endpoints.jackson.isolated-object-mapper属性设置为false来更改此功能。

## 5. Spring Security

**Spring Boot 3仅与Spring Security 6兼容**。

在升级到Spring Boot 3.0之前，我们应该首先[将Spring Boot 2.7应用升级到Spring Security 5.8](https://docs.spring.io/spring-security/reference/5.8/migration/index.html)。

之后，我们可以将Spring Security升级到版本6和Spring Boot 3。

此版本中引入了一些重要更改：

-   [ReactiveUserDetailsService](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/core/userdetails/ReactiveUserDetailsService.html)未自动配置：在存在[AuthenticationManagerResolver](https://www.baeldung.com/spring-security-authenticationmanagerresolver)的情况下，不再自动配置ReactiveUserDetailsService。
-   [SAML2](https://www.baeldung.com/cs/saml-introduction)信赖方配置：我们之前提到新版本的Spring Boot不再支持位于spring.security.saml2.relyingparty.registration.{id}.identity-provider下的属性。相反，我们应该使用spring.security.saml2.relyingparty.registration.{id}.asserting-party下的新属性。

## 6. Spring Batch

让我们看看[Spring Batch](https://www.baeldung.com/spring-boot-spring-batch)模块中引入的一些重大变化。

### 6.1 不鼓励@EnableBatchProcessing

以前，我们可以启用[Spring Batch](https://www.baeldung.com/spring-boot-spring-batch)的自动配置，使用[@EnableBatchProcessing](https://docs.spring.io/spring-batch/docs/current/api/org/springframework/batch/core/configuration/annotation/EnableBatchProcessing.html)标注配置类。**如果我们想使用自动配置，新版本的Spring Boot不鼓励使用这个注解**。

事实上，使用这个注解(或定义一个实现[DefaultBatchConfiguration](https://docs.spring.io/spring-batch/docs/current/api/org/springframework/batch/core/configuration/support/DefaultBatchConfiguration.html)的bean)会告诉自动配置退出。

### 6.2 运行多个作业

以前，可以使用Spring Batch同时运行多个批处理作业。但是，情况已不再如此。**如果自动配置检测到单个作业，它将在应用程序启动时自动执行**。

因此，如果上下文中存在多个作业，我们需要通过使用spring.batch.job.name属性提供作业名称来指定应在启动时执行的作业。因此，这意味着如果我们要运行多个作业，我们必须为每个作业创建一个单独的应用程序。

或者，我们可以使用Quartz、Spring Scheduler等调度程序或其他替代方案来调度作业。

## 7. 总结

在本文中，我们学习了如何将Spring Boot 2.7应用程序迁移到版本3，重点介绍了Spring环境的核心组件。[其他模块也发生了一些变化](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide)，如Spring Session、Micrometer、依赖管理等。