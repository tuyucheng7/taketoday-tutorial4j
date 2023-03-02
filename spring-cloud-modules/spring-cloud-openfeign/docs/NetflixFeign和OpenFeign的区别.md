## 1. 概述

在本教程中，我们将描述[Spring Cloud Netflix Feign](https://spring.io/projects/spring-cloud-netflix)和[Spring Cloud OpenFeign](https://spring.io/projects/spring-cloud-openfeign)之间的区别。

## 2. Feign

**[Feign](https://www.baeldung.com/intro-to-feign)通过提供注解支持使我们更容易编写Web服务客户端**，该注解支持允许我们仅使用接口实现客户端。

最初，Feign是由Netflix创建和发布的，作为其[Netflix OSS](https://netflix.github.io/)项目的一部分。如今，它是一个开源项目。

### 2.1 Spring Cloud Netflix Feign

Spring Cloud Netflix将Netflix OSS产品集成到[Spring Cloud](https://www.baeldung.com/spring-cloud-series)生态系统中。这包括Feign、Eureka、Ribbon和许多其他工具和实用程序。然而，Feign被赋予了自己的Spring Cloud Starter以允许仅访问Feign。

### 2.2 OpenFeign

最终，Netflix决定在内部停止使用Feign并停止其开发。由于这一决定，Netflix在名为[OpenFeign](https://github.com/OpenFeign/feign)的新项目下将Feign完全转移到开源社区。

幸运的是，它继续得到开源社区的大力支持，并见证了许多新功能和更新。

### 2.3 Spring Cloud OpenFeign

与其前身类似，Spring Cloud OpenFeign将前身项目集成到Spring Cloud生态系统中。

方便的是，此集成添加了对Spring MVC注解的支持并提供了相同的[HttpMessageConverters](https://www.baeldung.com/spring-httpmessageconverter-rest)。

让我们将[Spring Cloud OpenFeign](https://www.baeldung.com/spring-cloud-openfeign)中的Feign实现与使用Spring Cloud Netflix Feign的实现进行比较。

## 3. 依赖关系

首先，我们必须将[spring-cloud-starter-feign](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-feign/1.4.7.RELEASE)和[spring-cloud-dependencies](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-dependencies/2022.0.1)依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-feign</artifactId>
    <versionId>1.4.7.RELEASE</versionID>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-dependencies</artifactId>
    <version>Hoxton.SR8</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

请注意，此库仅适用于Spring Boot 1.4.7或更早版本。因此，我们的pom.xml必须使用任何Spring Cloud依赖项的兼容版本。

## 4. 使用Spring Cloud Netflix Feign实现

现在，我们可以使用@EnableFeignClients为任何使用@FeignClient的接口启用组件扫描。

对于我们使用Spring Cloud Netflix Feign项目开发的每个示例，我们使用以下导入：

```java
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
```

对于旧版本和新版本，所有功能的实现完全相同。

## 5. 使用Spring Cloud OpenFeign实现

相比之下，我们的[Spring Cloud OpenFeign教程](https://www.baeldung.com/spring-cloud-openfeign)包含与Spring Netflix Feign实现相同的示例。

这里唯一的区别是我们的导入来自不同的包：

```java
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
```

其他一切都是一样的，由于这两个库之间的关系，这应该不足为奇。

## 6. 比较

从根本上说，Feign的这两个实现是相同的。我们可以将此归因于Netflix Feign是OpenFeign的祖先。

但是，Spring Cloud OpenFeign包含Spring Cloud Netflix Feign中不可用的新选项和功能。

最近，我们可以获得对[Micrometer](https://micrometer.io/)、[Dropwizard Metrics](https://metrics.dropwizard.io/)、[Apache HTTP Client 5](https://hc.apache.org/httpcomponents-client-5.0.x/index.html)、[Google HTTP client](https://googleapis.github.io/google-http-java-client)等的支持。

## 7. 总结

本文比较了OpenFeign和Netflix Feign的Spring Cloud集成。与往常一样，你可以在GitHub上找到[Spring Cloud OpenFeign](https://github.com/eugenp/tutorials/tree/master/spring-cloud-modules/spring-cloud-openfeign)和[Netflix Feign](https://github.com/eugenp/tutorials/tree/master/spring-cloud-modules/spring-cloud-netflix-feign)的源代码。