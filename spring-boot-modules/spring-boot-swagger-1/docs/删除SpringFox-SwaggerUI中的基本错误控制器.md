## 1. 概述

在本教程中，我们将学习在[Spring Boot](https://www.baeldung.com/spring-boot)应用程序中配置[Swagger](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)以隐藏BasicErrorController公开的路径的多种方法。

## 2. 目标项目

在本文中，我们不会介绍从Spring Boot和Swagger-UI开始创建基本配置。我们可以使用已经配置的项目，也可以按照[使用Spring REST API指南设置Swagger 2](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)来创建基本配置。

## 3. 问题

**如果我们的代码包含BasicErrorController，则Swagger默认情况下也会在生成的文档中包含它的所有端点**，我们需要提供自定义配置来删除不需要的控制器。

例如，假设我们想提供一个标准RestController的API文档：

```java
@RestController
@RequestMapping("good-path")
public class RegularRestController {

    @ApiOperation(value = "This method is used to get the author name.")
    @GetMapping("/getAuthor")
    public String getAuthor() {
        return "Name Surname";
    }

    // Other similar methods
}
```

另外，假设我们的代码包含一个扩展BasicErrorController的类：

```java
@Component
@RequestMapping("my-error-controller")
public class MyErrorController extends BasicErrorController {
    // basic constructor
}
```

我们可以看到生成的文档中包含了my-error-controller：

<img src="../assets/img_2.png">

## 4. 解决方案

在本节中，我们将介绍四种不同的解决方案，以从Swagger文档中排除资源。

### 4.1 使用basePackage()排除

**通过指定我们要记录的控制器的基础包，我们可以排除不需要的资源**。

这仅在错误控制器包与标准控制器包不同时，此操作才有效。使用Spring Boot，只要我们提供一个[Docket](https://springfox.github.io/springfox/javadoc/2.7.0/index.html?springfox/documentation/spring/web/plugins/Docket.html) bean就足够了：

```java
@Configuration
public class SwaggerConfiguration {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
              .select()
              .apis(RequestHandlerSelectors.basePackage("cn.tuyucheng.taketoday.swaggerconf.controller"))
              .build();
    }
}
```

有了这个自定义配置，Swagger将仅在指定包内检查[REST](https://www.baeldung.com/rest-with-spring-series)控制器方法。因此，例如，如果我们的BasicErrorController是在包“cn.tuyucheng.taketoday.swaggerconf.error”中定义的，则不会考虑它。

### 4.2 排除注解

**或者，我们也可以指示Swagger必须只生成标注有特定Java注解的类的文档**。

在本例中，我们将其设置为RestController.class：

```java
@Bean
public Docket api() {
   return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
       .select()
       .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
       .build();
}
```

在这种情况下，BasicErrorController将被排除在Swagger文档之外，因为它没有使用@RestController注解进行修饰，这个注解只出现在我们想要记录的RegularRestController上。

### 4.3 在路径上使用正则表达式排除

另一种方法是**在自定义路径上指定正则表达式**，在这种情况下，只会记录映射到“/good-path”前缀的资源：

```java
@Bean
public Docket api() {
   return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
       .select()
       .paths(regex("/good-path/.*"))
       .build();
}
```

### 4.4 使用@ApiIgnore排除

**最后，我们可以使用**[@ApiIgnore](https://springfox.github.io/springfox/javadoc/2.9.2/index.html?springfox/documentation/annotations/ApiIgnore.html)**注解从Swagger中排除一个特定的类**：

```java
@Component
@RequestMapping("my-error-controller")
@ApiIgnore
public class MyErrorController extends BasicErrorController {
    // basic constructor
}
```

## 5. 总结

在本文中，我们介绍了四种不同的方法来在Spring Boot应用程序中配置Swagger以隐藏BasicErrorController资源。