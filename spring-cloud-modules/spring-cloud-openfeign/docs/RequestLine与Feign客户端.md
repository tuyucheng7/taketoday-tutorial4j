## 1. 概述

在本教程中，我们将演示如何在[Feign客户端中使用](https://www.baeldung.com/spring-cloud-openfeign)[@RequestLine](https://github.com/OpenFeign/feign/blob/master/core/src/main/java/feign/RequestLine.java)注解。@RequestLine是一个模板，用于定义用于连接 RESTful Web 服务的 URI 和查询参数。

## 2.Maven依赖

首先，让我们创建一个[Spring Boot](https://www.baeldung.com/spring-boot) Web 项目，并将[spring-cloud-starter-openfeign](https://search.maven.org/search?q=a:spring-cloud-starter-openfeign)或feign [-core](https://search.maven.org/artifact/io.github.openfeign/feign-core)依赖项包含到我们的pom.xml文件中。spring-cloud-starter-openfeign包含feign -core依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
    <version>3.1.2</version>
</dependency>
```

或者

```xml
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-core</artifactId>
    <version>11.8</version>
</dependency>
```

## 3. Feign Client中的@RequestLine

@RequestLine Feign 注解将HTTP 谓词、路径和请求参数指定为 Feign 客户端中的参数。路径和请求参数使用@Param注解指定。

通常在Spring Boot应用程序中，我们会使用@FeignClient，但如果我们不想使用spring-cloud-starter-openfeign依赖项，我们也可以使用@RequestLine。如果我们将@RequestLine与@FeignClient一起使用，则使用该依赖项会给我们一个IllegalStateException。

@FeignClient注解的字符串值是一个任意名称，用于创建 Spring Cloud LoadBalancer 客户端。我们可能会根据需要额外指定 URL 和其他参数。

让我们创建一个使用@RequestLine的接口：

```java
public interface EmployeeClient {
    @RequestLine("GET /empployee/{id}?active={isActive}")
    @Headers("Content-Type: application/json")
    Employee getEmployee(@Param long id, @Param boolean isActive);
}
```

我们还应该在定义其余API所需的标头的地方提供@Headers。

现在，我们将调用这样创建的接口来调用实际的API：

```java
EmployeeClient employeeResource = Feign.builder().encoder(new SpringFormEncoder())
  .target(EmployeeClient.class, "http://localhost:8081");
Employee employee = employeeResource.getEmployee(id, true);
```

## 4. 总结

在本文中，我们演示了如何以及何时在Feign Client 中使用 @RequestLine 注解。