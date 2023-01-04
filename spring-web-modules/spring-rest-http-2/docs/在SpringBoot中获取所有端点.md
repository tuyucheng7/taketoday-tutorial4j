## 1. 概述

使用REST API时，通常会检索所有 REST 端点。例如，我们可能需要将所有请求映射端点保存在数据库中。在本教程中，我们将了解如何在[Spring Boot](https://www.baeldung.com/category/spring/spring-boot/)应用程序中获取所有REST端点。

## 2. 映射端点

在Spring Boot应用程序中，我们通过在控制器类中使用@RequestMapping注解来公开REST API端点。要获取这些端点，有三个选项：事件监听器、Spring Boot Actuator或Swagger库。

## 3. 事件监听器方法

为了创建REST API服务，我们在控制器类中使用[了@RestController](https://www.baeldung.com/building-a-restful-web-service-with-spring-and-java-based-configuration#controller)和@RequestMapping，这些类在Spring应用程序上下文中注册为Spring bean。因此，当应用程序上下文在启动时就绪时，我们可以通过使用事件监听器来获取端点。有两种方法可以定义监听器。我们既可以实现[ApplicationListener](https://www.baeldung.com/spring-events#listener)接口，也可以使用[@EventListener注解](https://www.baeldung.com/spring-events#annotation-driven)。

### 3.1 ApplicationListener接口

在实现ApplicationListener时，我们必须定义onApplicationEvent()方法：

```java
@Override
public void onApplicationEvent(ContextRefreshedEvent event) {
    ApplicationContext applicationContext = event.getApplicationContext();
    RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
        .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
    Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping
        .getHandlerMethods();
    map.forEach((key, value) -> LOGGER.info("{} {}", key, value));
}
```

这样，我们使用了[ContextRefreshedEvent](https://www.baeldung.com/spring-context-events#1-contextrefreshedevent)类。当ApplicationContext初始化或刷新时发布此事件，Spring Boot提供了许多HandlerMapping实现。其中包括RequestMappingHandlerMapping类，它检测请求映射并由@RequestMapping注解使用。因此，我们在ContextRefreshedEvent事件中使用这个bean。

### 3.2 @EventListener注解

映射端点的另一种方法是使用@EventListener注解，我们直接在处理ContextRefreshedEvent的方法上使用此注解：

```java
@EventListener
public void handleContextRefresh(ContextRefreshedEvent event) {
    ApplicationContext applicationContext = event.getApplicationContext();
    RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
        .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
    Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping
        .getHandlerMethods();
    map.forEach((key, value) -> LOGGER.info("{} {}", key, value));
}
```

## 4. Actuator方法

检索所有端点列表的第二种方法是通过[Spring Boot Actuator](https://www.baeldung.com/spring-boot-actuators)功能。

### 4.1 Maven依赖

为了启用此功能，我们将[spring-boot-actuator](https://search.maven.org/artifact/org.activiti/spring-boot-starter-actuator) Maven依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### 4.2 配置

当我们添加spring-boot-actuator依赖时，默认情况下只有/health和/info端点可用。要启用所有Actuator[端点](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready-endpoints)，我们可以通过向我们的application.properties文件添加一个属性来公开它们：

```properties
management.endpoints.web.exposure.include=*
```

或者，我们可以简单地公开用于检索映射的端点：

```properties
management.endpoints.web.exposure.include=mappings
```

启用后，我们应用程序的REST API端点可在http://host/actuator/mappings获得。

## 5. Swagger

[Swagger库](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)还可用于列出REST API的所有端点。

### 5.1 Maven依赖

要将它添加到我们的项目中，我们需要在pom.xml文件中添加[springfox-boot-starter](https://search.maven.org/artifact/io.springfox/springfox-boot-starter)依赖项：

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```

### 5.2 配置

让我们通过定义[Docket](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api#1-java-configuration) bean来创建配置类：

```java
@Bean
public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build();
}
```

Docket是一个构建器类，用于配置Swagger文档的生成。要访问REST API端点，我们可以在浏览器中访问此URL：

```bash
http://host/v2/api-docs
```

## 6. 总结

在本文中，我们描述了如何使用事件监听器、Spring Boot Actuator和Swagger库在Spring Boot应用程序中检索请求映射端点。