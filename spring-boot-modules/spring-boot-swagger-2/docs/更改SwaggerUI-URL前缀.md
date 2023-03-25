## 1. 概述

作为优秀的开发人员，我们已经知道文档对于构建REST API至关重要，因为它可以帮助API的使用者无缝工作。如今，大多数Java开发人员都在使用[Spring Boot](https://www.baeldung.com/spring-boot)。截至今天，有两个工具使用[Springfox](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)和[SpringDoc](https://www.baeldung.com/spring-rest-openapi-documentation)简化了[Swagger](https://en.wikipedia.org/wiki/Swagger_(software)) API文档的生成和维护。

在本教程中，**我们将了解如何更改这些工具默认提供的Swagger-UI URL前缀**。

## 2. 使用Springdoc更改Swagger UI URL前缀

首先，我们可以检查[如何使用OpenAPI 3.0设置REST API文档](https://www.baeldung.com/spring-rest-openapi-documentation)。

首先，按照上面的教程，我们需要添加对SpringDoc的依赖：

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.0.2</version>
</dependency>
```

swagger-ui的默认URL为[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)。

让我们看一下自定义swagger-UI URL的两种方法，假设从/myproject开始。

### 2.1 使用application.properties文件

由于我们已经熟悉Spring中的许多不同属性，因此我们需要将以下属性添加到application.properties文件中：

```properties
springdoc.swagger-ui.disable-swagger-default-url=true
springdoc.swagger-ui.path=/myproject
```

### 2.2 使用配置类

我们还可以通过在配置文件中进行以下更改来完成此更改：

```java
@Component
public class SwaggerConfiguration implements ApplicationListener<ApplicationPreparedEvent> {

    @Override
    public void onApplicationEvent(final ApplicationPreparedEvent event) {
        ConfigurableEnvironment environment = event.getApplicationContext().getEnvironment();
        Properties props = new Properties();
        props.put("springdoc.swagger-ui.path", swaggerPath());
        environment.getPropertySources()
              .addFirst(new PropertiesPropertySource("programmatically", props));
    }

    private String swaggerPath() {
        return "/myproject"; // TODO: implement your logic here.
    }
}
```

在这种情况下，我们需要在应用程序启动之前注册监听器：

```java
public static void main(String[] args) {
    SpringApplication application = new SpringApplication(SampleApplication.class);
    application.addListeners(new SwaggerConfiguration());
    application.run(args);
}
```

## 3. 使用Springfox更改Swagger UI URL前缀

我们可以通过[使用Swagger设置示例和描述](https://www.baeldung.com/swagger-set-example-description)和[使用Spring REST API设置Swagger 2](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)来了解如何设置REST API文档。

首先，按照上面给出的教程，我们需要添加对Springfox的依赖：

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>3.0.0</version>
</dependency>
```

比方说，我们想将此URL更改为[http://localhost:8080/myproject/swagger-ui/index.html](http://localhost:8080/myproject/swagger-ui/index.html)。让我们回顾一下可以帮助我们实现这一目标的两种方法。

### 3.1 使用application.properties文件

同样，如上面的SpringDoc示例，在application.properties文件中添加以下属性将帮助我们成功更改它。

```properties
springfox.documentation.swagger-ui.base-url=myproject
```

### 3.2 在配置中使用Docket Bean

```java
@Bean
public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build();
}

@Override
public void addViewControllers(ViewControllerRegistry registry) {
   registry.addRedirectViewController("/myproject", "/");
}
```

## 4. 添加重定向控制器

我们还可以向API端点添加重定向逻辑。在这种情况下，我们使用SpringDoc还是Springfox都没有关系：

```java
@Controller
public class SwaggerController {

@RequestMapping("/myproject")
public String getRedirectUrl() {
       return "redirect:swagger-ui.html";
    }
}
```

## 5. 总结

在本文中，我们学习了如何使用Springfox和SpringDoc更改REST API文档的默认swagger-ui URL。