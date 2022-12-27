## 1. 概述

描述RESTful API在文档中起着重要作用，用于记录REST API的一种常用工具是[Swagger 2](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)。但是，用于添加说明的一个有用属性已被弃用。在本教程中，我们将使用Swagger 2和[OpenAPI 3](https://www.baeldung.com/spring-rest-openapi-documentation)找到已弃用的描述属性的解决方案，我们将演示如何使用这些属性来描述[Spring Boot](https://www.baeldung.com/spring-boot-start) REST API应用程序。

## 2. 接口说明

默认情况下，Swagger为REST API类名生成一个空描述，因此，我们需要指定一个合适的注解来描述一个REST API。我们可以使用带有@Api注解的Swagger 2，也可以使用OpenAPI 3中的@Tag注解。

## 3. Swagger 2

要将Swagger 2用于Spring Boot REST API，我们可以使用[Springfox](https://github.com/springfox/springfox)库，我们需要在pom.xml文件中添加[springfox-boot-starter](https://search.maven.org/search?q=a:springfox-boot-starter)依赖：

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```

Springfox库提供了@Api注解来配置一个类作为Swagger资源，之前@Api注解提供了一个description属性来自定义API文档：

```java
@Api(value = "", description = "")
```

但是，如前所述，**不推荐使用description属性**。幸运的是，还有另一种选择，我们可以**使用tags属性**：

```java
@Api(value = "", tags = {"tag_name"})
```

在Swagger 1.5中，我们将使用@SwaggerDefinition注解来定义标签，但是，在Swagger 2中不再支持它。因此，在Swagger 2中，我们在Docket bean中定义标签和描述：

```java
@Configuration
public class SwaggerConfiguration {

    public static final String BOOK_TAG = "book service";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
              .select()
              .apis(RequestHandlerSelectors.any())
              .paths(PathSelectors.any())
              .build()
              .tags(new Tag(BOOK_TAG, "the book API with description api tag"));
    }
}
```

在这里，我们在Docket bean中使用Tag类来创建我们的标签。这样，我们就可以在我们的控制器中引用标签：

```java
@RestController
@RequestMapping("/api/book")
@Api(tags = {SwaggerConfiguration.BOOK_TAG})
public class BookController {

    @GetMapping("/")
    public List<String> getBooks() {
        return Arrays.asList("book1", "book2");
    }
}
```

## 4. OpenAPI 3

OpenAPI 3是OpenAPI规范的最新版本，它是OpenAPI 2(Swagger 2)的继承者。为了使用OpenAPI 3描述API，我们可以使用@Tag注解，此外，@Tag注解提供了描述和外部链接。让我们定义BookController类：

```java
@RestController
@RequestMapping("/api/book")
@Tag(name = "book service", description = "the book API with description tag annotation")
public class BookController {

    @GetMapping("/")
    public List<String> getBooks() {
        return Arrays.asList("book1", "book2");
    }
}
```

## 5. 总结

在这篇简短的文章中，我们描述了如何在Spring Boot应用程序中向REST API添加描述，我们研究了如何使用Swagger 2和OpenAPI 3来实现这一点。对于Swagger部分，可以[在GitHub上](https://github.com/eugenp/tutorials/tree/master/spring-boot-modules/spring-boot-swagger)获取代码，要查看OpenAPI 3示例代码，请在[GitHub上](https://github.com/eugenp/tutorials/tree/master/spring-boot-modules/spring-boot-springdoc)查看其模块。