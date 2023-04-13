## 1. 概述

Spring Boot CXF启动器提供了一种简单的方法来配置和启动Apache CXF，以便在Spring Boot应用程序中使用CXF。

在本教程中，我们将演示如何使用Spring Boot CXF启动器编写一个简单的应用程序。

## 2. Spring Boot Actuator

当检测到[Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html)存在时，应用程序可能会受益于指标支持自动配置(基于[Micrometer](https://micrometer.io/)库)。检测层自动(或以编程方式)跟踪与请求处理相关的服务器端指标，并将其与其他指标一起公开。有关详细信息，请查看[Micrometer集成](https://cxf.apache.org/docs/micrometer.html)文档。

|                  属性                   |                             描述                             |         默认值         |
|:-------------------------------------:|:----------------------------------------------------------:|:-------------------:|
|          cxf.metrics.enabled          |                    启用或禁用指标自动配置                    |        true         |
|       cxf.metrics.jaxrs.enabled       |                启用或禁用JAX-RS指标自动配置                |         true          |
|       cxf.metrics.jaxws.enabled       |                启用或禁用JAX-WS指标自动配置                |         true          |
|  cxf.metrics.server.autoTimeRequests  |CXF处理的请求是否应该自动计时。如果发出的时间序列数量由于请求映射时间而增长太大，请将其设置为“false”并根据需要在每次调用的基础上使用[@Timed](https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-core/src/main/java/io/micrometer/core/annotation/Timed.java)或[@TimeSet](https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-core/src/main/java/io/micrometer/core/annotation/TimedSet.java)。|         true          |
|cxf.metrics.server.requestsMetricName|                已接收请求的指标名称(服务器端)                |cxf.server.requests|
|     cxf.metrics.server.maxUriTags     |允许的唯一URI标记值的最大数量。达到标签值的最大数量后，具有额外标签值的指标将被过滤器拒绝。|         100         |
|cxf.metrics.client.requestsMetricName|                已发送请求的指标名称(客户端)                |cxf.client.requests|
|     cxf.metrics.client.maxUriTags     |允许的唯一URI标记值的最大数量。达到标签值的最大数量后，具有额外标签值的指标将被过滤器拒绝。|         100         |

## 3. Spring Boot CXF JAX-WS启动器

### 3.1 功能

使用“/services/*” URL模式注册CXFServlet，以便为CXF JAX-WS端点提供服务。

## 3.2 设置

我们需要添加以下Maven依赖项：

```xml
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-spring-boot-starter-jaxws</artifactId>
    <version>3.1.12</version>
</dependency>
```

最新版本的cxf-spring-boot-starter-jaxws可以在[Maven Central](https://central.sonatype.com/artifact/org.apache.cxf/cxf-spring-boot-starter-jaxws/4.0.0)上找到。

## 3.3 其他配置

+ “cxf.path”：自定义CXFServlet URL模式
+ “cxf.servlet.init”：映射属性自定义CXFServlet属性，例如“services-list-path”(默认位于“/services”)等。
+ “cxf.servlet.loadOnStartup”：设置CXFServlet的loadOnStartup优先级(默认为-1)
+ “cxf.servlet.enabled”：启用/禁用CXFServlet注册(自[3.3.12/3.4.5](https://issues.apache.org/jira/issues/?jql=project+%3D+CXF+AND+fixVersion+%3D+3.5.0)起)

如果需要，可以使用Spring @ImportResource注解来导入类路径上可用的现有JAX-WS上下文。

## 4. API文档

JAX-WS端点支持WSDL。

## 4.1 服务注册中心

将JAX-WS端点发布到众所周知的服务注册中心(例如Netflix Eureka)可以类似于注册JAX-RS端点的方式来实现，并在[JAX-RS Spring Boot Scan](https://github.com/apache/cxf/tree/master/distribution/src/main/release/samples/jax_rs/spring_boot_scan/application)演示中显示。

## 4.2 示例

考虑以下配置实例：

```java
@Configuration
public class WebServiceConfig {
    @Autowired
    private Bus bus;

    @Bean
    public Endpoint endpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, new HelloPortImpl());
        endpoint.publish("/Hello");
        return endpoint;
    }
}
```

将CXF JAX-WS启动器与此配置一起使用足以启用CXF JAX-WS端点，该端点将响应SOAP请求URI，例如[http://localhost:8080/services/Hello](http://localhost:8080/services/Hello)。

另请参阅[JAX-WS Spring Boot](https://github.com/apache/cxf/tree/master/distribution/src/main/release/samples/jaxws_spring_boot)。

## 特征

使用“/services/”URL模式注册CXFServlet以服务于CXFJAX-RS端点。

可选择自动发现JAX-RS根资源和提供程序并创建JAX-RS端点。

[请注意，本节](https://cxf.apache.org/docs/jaxrsclientspringboot.html)介绍了在SpringBoot应用程序中使用CXFJAX-RS客户端。

## 设置

JAX-RS启动器

```xml
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-spring-boot-starter-jaxrs</artifactId>
    <version>3.1.12</version>
</dependency>
```

## 其他配置

+ cxf.path：自定义CXFServletURL模式。
+ cxf.servlet.init：映射属性自定义CXFServlet属性，例如“services-list-path”(默认位于“/services”)等。
+ cxf.servlet.loadOnStartup：设置CXFServlet的loadOnStartup优先级(默认为-1)
+ cxf.servlet.enabled：”启用/禁用CXFServlet注册(自3.3.12/3.4.5起[)](https://issues.apache.org/jira/issues/?jql=project+%3D+CXF+AND+fixVersion+%3D+3.5.0)
+ cxf.jaxrs.server.path：自定义JAX-RS服务器端点地址(默认为“/”)。

可以自动发现使用JAX-RS@Path和@Provider注解的JAX-RS根资源和提供程序以及使用CXF[@Provider注解的本机CXF提供程序。](https://github.com/apache/cxf/blob/cxf-3.1.6/core/src/main/java/org/apache/cxf/annotations/Provider.java)

使用“cxf.jaxrs.component-scan”属性从自动发现的JAX-RS根资源和标记为Spring组件的提供程序创建JAX-RS端点(使用Spring@Component注解或从@Bean方法创建和返回).

使用“cxf.jaxrs.component-scan-packages”属性来限制哪些自动发现的Spring组件被接受为JAX-RS资源或提供程序类。它设置给定bean实例的类必须包含的包的逗号分隔列表。注意，如果设置此属性，则仅当给定bean是单例时才有效。它可以与“cxf.jaxrs.component-scan-beans”属性一起使用或作为替代方案使用。此属性从CXF3.1.11开始可用。

使用“cxf.jaxrs.component-scan-beans”属性来限制哪些自动发现的Spring组件被接受为JAX-RS资源或提供程序类。它设置了一个以逗号分隔的已接受bean名称列表——只有当它的bean名称在此列表中时，自动发现的组件才会被接受。它可以与“cxf.jaxrs.component-scan-packages”属性一起使用或替代。此属性从CXF3.1.11开始可用。

使用“cxf.jaxrs.classes-scan”属性从自动发现的JAX-RS根资源和提供程序类创建JAX-RS端点。此类类不必使用Spring@Component进行注解。此属性需要附带一个“cxf.jaxrs.classes-scan-packages”属性，该属性设置要扫描的包的逗号分隔列表。

请注意，虽然“cxf.jaxrs.component-scan”和“cxf.jaxrs.classes-scan”是互斥的，但“cxf.jaxrs.component-scan”可以与“cxf.jaxrs.classes-scan-packages”一起使用"属性启用JAX-RS资源和提供程序的自动发现，这些资源和提供程序可能会或可能不会被标记为Spring组件。

如果需要，可以使用SpringImportResource注解导入类路径上可用的现有JAX-RS上下文，而不是自动发现资源。

## API文档

### 昂首阔步

请参阅CXF[Swagger2Feature文档](https://cxf.apache.org/docs/swagger2feature.html#SwaggerFeature/Swagger2Feature-EnablinginSpringBoot)，了解如何在SpringBoot中启用Swagger2Feature以及如何自动激活SwaggerUI。

### WADL

如果cxf-rt-rs-service-description模块在运行时类路径上可用，CXF会自动加载WADL提供程序。

## 服务注册刊物

[JAX-RSSpringBootScan](https://github.com/apache/cxf/tree/master/distribution/src/main/release/samples/jax_rs/spring_boot_scan/application)演示中展示了将JAX-RS端点发布到知名服务注册表(例如NetflixEurekaRegistry)中。

## 例子

### 手动配置

考虑以下配置实例：

JAX-RS配置

```java
@SpringBootApplication
public class SampleRestApplication {
    @Autowired
    private Bus bus;
 
    public static void main(String[] args) {
        SpringApplication.run(SampleRestApplication.class, args);
    }
  
    @Bean
    public Server rsServer() {
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
        endpoint.setBus(bus);
        endpoint.setAddress("/");
        // Register 2 JAX-RS root resources supporting "/sayHello/{id}" and "/sayHello2/{id}" relative paths
        endpoint.setServiceBeans(Arrays.<Object>asList(new HelloServiceImpl1(), new HelloServiceImpl2()));
        endpoint.setFeatures(Arrays.asList(new Swagger2Feature()));
        return endpoint.create();
    }
}
```

将CXFJAX-RS启动器与此配置一起使用足以启用CXFJAX-RS端点，该端点将响应HTTP请求URI，例如

“[http://localhost:8080/services/sayHello/ApacheCxfUser](http://localhost:8080/services/Hello)”。[上面的代码还使Swagger文档在“http://localhost:8080/services/swagger.json](http://localhost:8080/services/Hello)”可用。

另请参阅[JAX-RSSpringBoot](https://github.com/apache/cxf/tree/master/distribution/src/main/release/samples/jax_rs/spring_boot)演示。

### 自动配置

如果启用了自动发现，则可以简化手动配置部分中显示的SpringBoot应用程序示例。

例如，给定以下application.yml属性：

应用程序属性

```yaml
cxf:
    jaxrs:
        component-scan: true
        classes-scan-packages: org.apache.cxf.jaxrs.swagger
```

该应用程序变得简单：

JAX-RS自动配置

```java
@SpringBootApplication
public class SampleScanRestApplication {
    public static void main(String[] args) {
        SpringApplication.run(SampleScanRestApplication.class, args);
    }
}
```

此应用程序将启用CXFJAX-RS端点，该端点将响应HTTP请求URI，例如

“[http://localhost:8080/services/sayHello/ApacheCxfUser](http://localhost:8080/services/Hello)”。[上面的代码还使Swagger文档在“http://localhost:8080/services/swagger.json](http://localhost:8080/services/Hello)”可用。

另请参阅[JAX-RSSpringBootScan](https://github.com/apache/cxf/tree/master/distribution/src/main/release/samples/jax_rs/spring_boot_scan/application)演示。