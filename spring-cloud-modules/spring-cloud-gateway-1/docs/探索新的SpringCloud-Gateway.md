## 1. 概述

在本教程中，我们将探讨[Spring Cloud Gateway](https://cloud.spring.io/spring-cloud-gateway/)项目的主要功能，这是一个基于Spring 5、Spring Boot 2和Project Reactor的新API。

该工具提供开箱即用的路由机制，通常用于微服务应用程序，作为将多个服务隐藏在单个外观后面的一种方式。

关于不使用Spring Cloud Gateway项目的网关模式的解释，请查看我们[之前](https://www.baeldung.com/spring-cloud-gateway-pattern)的文章。

## 2. 路由处理程序

专注于路由请求，Spring Cloud Gateway将请求转发到网关处理程序映射(Gateway Handler Mapping)，网关处理程序映射决定应该如何处理与特定路由匹配的请求。

让我们从一个快速示例开始，说明网关处理程序如何使用RouteLocator解析路由配置：

```java
@Bean
public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("r1", r -> r.host("**.tuyucheng.com")
            .and()
            .path("/tuyucheng")
            .uri("http://tuyucheng.com"))
        .route(r -> r.host("**.tuyucheng.com")
            .and()
            .path("/myOtherRouting")
            .filters(f -> f.prefixPath("/myPrefix"))
            .uri("http://othersite.com")
            .id("myOtherID"))
    .build();
}
```

请注意我们如何使用此API的主要构建块：

-   Route：网关的主要API。它由给定的标识(ID)、目的地(URI)以及一组谓词和过滤器定义
-   Predicate：Java 8 Predicate—用于使用标头、方法或参数匹配HTTP请求
-   Filter：一个标准的Spring WebFilter

## 3. 动态路由

就像[Zuul](https://www.baeldung.com/spring-rest-with-zuul-proxy)一样，Spring Cloud Gateway提供了将请求路由到不同服务的方法。

可以使用纯Java(RouteLocator，如第2节中的示例所示)或使用属性配置来创建路由配置：

```yaml
spring:
    application:
        name: gateway-service
    cloud:
        gateway:
            routes:
                -   id: tuyucheng
                    uri: tuyucheng.com
                -   id: myOtherRouting
                    uri: localhost:9999
```

## 4. 路由工厂

Spring Cloud Gateway使用Spring WebFlux HandlerMapping基础设施匹配路由。

它还包括许多内置的路由谓词工厂。所有这些谓词都匹配HTTP请求的不同属性。多个路由谓词工厂可以通过逻辑“与(and)”组合起来。

路由匹配既可以通过编程方式应用，也可以使用不同类型的路由谓词工厂通过配置属性文件应用。

我们的文章[Spring Cloud Gateway路由谓词工厂](https://www.baeldung.com/spring-cloud-gateway-routing-predicate-factories)更详细地探讨了路由工厂。

## 5. WebFilter工厂

路由筛选器使修改传入HTTP请求或传出HTTP响应成为可能。

Spring Cloud Gateway包括许多内置的WebFilter工厂以及创建自定义过滤器的可能性。

我们的文章[Spring Cloud Gateway WebFilter Factories](https://www.baeldung.com/spring-cloud-gateway-webfilter-factories)更详细地探讨了WebFilter工厂。

## 6. Spring Cloud DiscoveryClient支持

Spring Cloud Gateway可以轻松地与服务发现和注册库集成，例如Eureka Server和Consul：

```java
@Configuration
@EnableDiscoveryClient
public class GatewayDiscoveryConfiguration {

    @Bean
    public DiscoveryClientRouteDefinitionLocator discoveryClientRouteLocator(DiscoveryClient discoveryClient) {

        return new DiscoveryClientRouteDefinitionLocator(discoveryClient);
    }
}
```

### 6.1 LoadBalancerClient过滤器

LoadBalancerClientFilter使用ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR在交换属性中查找URI。

如果URL具有lb方案(例如lb://tuyucheng-service)，它将使用Spring Cloud LoadBalancerClient将名称(即tuyucheng-service)解析为实际主机和端口。

未修改的原始URL放在ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR属性中。

## 7. 监控

Spring Cloud Gateway使用Actuator API，这是一个著名的Spring Boot库，它提供了多种开箱即用的服务来监控应用程序。

安装和配置Actuator API后，可以通过访问/gateway/端点来可视化网关监控功能。

## 8. 实施

现在，我们将创建一个使用Spring Cloud Gateway作为使用路径谓词的代理服务器的简单示例。

### 8.1 依赖关系

Spring Cloud Gateway目前位于里程碑仓库中，版本为2.0.0.RC2。这也是我们在这里使用的版本。

要添加项目，我们将使用依赖管理系统：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-gateway</artifactId>
            <version>2.0.0.RC2</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

接下来，我们将添加必要的依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-actuator</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

### 8.2 代码实现

现在我们在application.yml文件中创建一个简单的路由配置：

```yaml
spring:
    cloud:
        gateway:
            routes:
                - id: tuyucheng_route
                  uri: http://tuyucheng.com
                  predicates:
                      - Path=/tuyucheng/
management:
    endpoints:
        web:
            exposure:
                include: "*"
```

这是网关应用程序代码：

```java
@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
```

应用程序启动后，我们可以访问url [http://localhost/actuator/gateway/routes/tuyucheng_route](http://localhost/actuator/gateway/routes/tuyucheng_route)来检查创建的所有路由配置：

```json
{
    "id": "tuyucheng_route",
    "predicates": [
        {
            "name": "Path",
            "args": {
                "_genkey_0": "/tuyucheng"
            }
        }
    ],
    "filters": [],
    "uri": "http://tuyucheng.com",
    "order": 0
}
```

我们看到相对url “/tuyucheng”被配置为路由。因此，点击url [http://localhost/tuyucheng](http://localhost/tuyucheng)，我们将被重定向到“http://tuyucheng.com”，正如我们示例中所配置的那样。

## 9. 总结

在本文中，我们探讨了Spring Cloud Gateway的一些功能和组件。这个新的API为网关和代理支持提供开箱即用的工具。