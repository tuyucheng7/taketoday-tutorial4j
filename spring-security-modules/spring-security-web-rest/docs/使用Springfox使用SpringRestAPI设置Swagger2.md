## 1. 概述

如今，前端和后端组件通常将 Web 应用程序分开。通常，我们将 API 公开为前端组件或第三方应用程序集成的后端组件。

在这种情况下，必须为后端 API 制定适当的规范。同时，API 文档应该信息丰富、易读且易于理解。

此外，参考文档应同时描述 API 中的每个更改。手动完成这项工作是一项乏味的工作，因此该过程的自动化是不可避免的。

在本教程中，我们将使用 Swagger 2 规范的 Springfox 实现来查看 Spring REST Web 服务的 Swagger 2。如果不熟悉 Swagger，请访问[其网页](http://swagger.io/)以了解更多信息，然后再继续本教程。

值得一提的是，最新版本的 Swagger 规范，现在称为 OpenAPI 3.0，得到了 Springdoc 项目的更好支持，应该用于[记录 Spring REST API](https://www.baeldung.com/spring-rest-openapi-documentation)。

## 进一步阅读：

## [使用 Swagger 生成Spring BootREST 客户端](https://www.baeldung.com/spring-boot-rest-client-swagger-codegen)

了解如何使用 Swagger 代码生成器生成Spring BootREST 客户端。

[阅读更多](https://www.baeldung.com/spring-boot-rest-client-swagger-codegen)→

## [Spring REST 文档简介](https://www.baeldung.com/spring-rest-docs)

本文介绍 Spring REST Docs，这是一种测试驱动的机制，可以为 RESTful 服务生成准确且可读的文档。

[阅读更多](https://www.baeldung.com/spring-rest-docs)→

## [Java 中的 Asciidoctor 简介](https://www.baeldung.com/asciidoctor)

了解如何使用 AsciiDoctor 生成文档。

[阅读更多](https://www.baeldung.com/asciidoctor)→

## 2. 目标项目

我们将使用的 REST 服务的创建不在本文的讨论范围之内。如果已经有合适的项目，请使用它。如果没有，这些链接是一个很好的起点：

-   [使用 Spring 和JavaConfig 构建 REST API 文章](https://www.baeldung.com/building-a-restful-web-service-with-spring-and-java-based-configuration)
-   [构建 RESTful Web 服务](https://spring.io/guides/gs/rest-service/)

## 3.添加Maven依赖

如上所述，我们将使用 Swagger 规范的 Springfox 实现。最新版本可以 [在 Maven Central](https://search.maven.org/classic/#search|ga|1|"Springfox Swagger2")上找到。

要将其添加到我们的 Maven 项目中，我们需要pom.xml文件中的依赖项：

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>3.0.0</version>
</dependency>
```

### 3.1。Spring Boot 依赖

对于基于Spring Boot的项目，添加一个springfox-boot-starter依赖项就足够了：

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```

我们可以添加我们需要的任何其他启动器，其版本由Spring Boot父级管理：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.4.0</version>
</dependency>
```

## 4. 将 Swagger 2 集成到项目中

### 4.1。Java 配置

Swagger 的配置主要围绕Docket bean 进行：

```java
@Configuration
public class SpringFoxConfig {                                    
    @Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          .apis(RequestHandlerSelectors.any())              
          .paths(PathSelectors.any())                          
          .build();                                           
    }
}
```

在定义Docket bean 之后，它的select()方法返回一个ApiSelectorBuilder的实例，它提供了一种控制 Swagger 暴露的端点的方法。

我们可以在RequestHandlerSelectors和PathSelectors的帮助下配置用于选择RequestHandler的谓词。对两者都使用any()将使我们的整个 API 的文档可以通过 Swagger 获得。

### 4.2. 没有Spring Boot的配置

在普通的 Spring 项目中，我们需要显式启用 Swagger 2。为此，我们必须在配置类上使用@EnableSwagger2WebMvc：

```java
@Configuration
@EnableSwagger2WebMvc
public class SpringFoxConfig {                                    
}
```

此外，如果没有 Spring Boot，我们就无法自动配置资源处理程序。

Swagger UI 添加了一组资源，我们必须将它们配置为扩展WebMvcConfigurerAdapter 并使用@EnableWebMvc 注解的类的一部分：

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("swagger-ui.html")
      .addResourceLocations("classpath:/META-INF/resources/");

    registry.addResourceHandler("/webjars/")
      .addResourceLocations("classpath:/META-INF/resources/webjars/");
}
```

### 4.3. 确认

要验证 Springfox 是否正常工作，我们可以在浏览器中访问此 URL：

http://localhost:8080/spring-security-rest/api/v2/api-docs

结果是带有大量键值对的 JSON 响应，人类可读性不强。幸运的是，Swagger为此提供了Swagger UI 。

## 5. 招摇用户界面

Swagger UI 是一个内置的解决方案，它使用户与 Swagger 生成的 API 文档的交互变得更加容易。

### 5.1。启用 Springfox 的 Swagger UI

要使用 Swagger UI，我们需要添加一个额外的 Maven 依赖项：

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>3.0.0</version>
</dependency>
```

现在我们可以通过访问在浏览器中测试它：

http://localhost:8080/your-app-root/swagger-ui/

顺便说一下，在我们的例子中，确切的 URL 将是：

http://localhost:8080/spring-security-rest/api/swagger-ui/

结果应如下所示：

[![截图_1](https://www.baeldung.com/wp-content/uploads/2016/07/Screenshot_11.png)](https://www.baeldung.com/wp-content/uploads/2016/07/Screenshot_11.png)

### 5.2. 探索 Swagger 文档

在 Swagger 的响应中是我们应用程序中定义的所有控制器的列表。单击其中任何一个将列出有效的 HTTP 方法(DELETE、GET、HEAD、OPTIONS、PATCH、POST、PUT)。

扩展每个方法会提供额外的有用数据，例如响应状态、内容类型和参数列表。也可以使用 UI 尝试每种方法。

Swagger 与我们的代码库同步的能力至关重要。为了证明这一点，我们可以向我们的应用程序添加一个新控制器：

```java
@RestController
public class CustomController {

    @RequestMapping(value = "/custom", method = RequestMethod.POST)
    public String custom() {
        return "custom";
    }
}
```

现在，如果我们刷新 Swagger 文档，我们会在控制器列表中看到custom- controller。众所周知，Swagger 的响应中只显示了一种方法( POST )。

## 6. Spring Data REST

Springfox通过其[springfox-data-rest](https://search.maven.org/search?q=g:io.springfox a:springfox-data-rest)库为[Spring Data REST](https://www.baeldung.com/spring-data-rest-intro)提供支持。

如果 Spring Boot在 classpath 上发现spring-boot-starter-data-rest ，它将处理自动配置。

现在让我们创建一个名为User的实体：

```java
@Entity
public class User {
    @Id
    private Long id;
    private String firstName;
    private int age;
    private String email;

    // getters and setters
}
```

然后我们将创建UserRepository以在User实体上添加 CRUD 操作：

```java
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
```

最后，我们将SpringDataRestConfiguration类导入[SpringFoxConfig](https://springfox.github.io/springfox/javadoc/current/springfox/documentation/spring/data/rest/configuration/SpringDataRestConfiguration.html)类：

```java
@EnableSwagger2WebMvc
@Import(SpringDataRestConfiguration.class)
public class SpringFoxConfig {
    //...
}
```

注意：我们使用@EnableSwagger2WebMvc注解来启用 Swagger，因为它已经替换了库版本 3 中的@EnableSwagger2注解。

让我们重新启动应用程序以生成 Spring Data REST API 的规范：

[![swagger_user_1](https://www.baeldung.com/wp-content/uploads/2015/12/swagger_user_1.jpg)](https://www.baeldung.com/wp-content/uploads/2015/12/swagger_user_1.jpg)

我们可以看到 Springfox使用GET、POST、PUT、PATCH和DELETE等 HTTP 方法生成了User实体的规范。

## 7. Bean 验证

Springfox 还通过其[springfox-bean-validators](https://search.maven.org/search?q=g:io.springfox a:springfox-bean-validators)库支持[bean 验证注解。](https://www.baeldung.com/javax-validation)

首先，我们将 Maven 依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-bean-validators</artifactId>
    <version>2.9.2</version>
</dependency>
```

同样，如果我们使用 Spring Boot，我们不必显式提供上述依赖项。

接下来，让我们为User实体添加一些验证注解，例如[@NotNull](https://docs.oracle.com/javaee/7/api/javax/validation/constraints/NotNull.html)和[@Min ：](https://docs.oracle.com/javaee/7/api/javax/validation/constraints/Min.html)

```java
@Entity
public class User {
    //...
    
    @NotNull(message = "First Name cannot be null")
    private String firstName;
    
    @Min(value = 15, message = "Age should not be less than 15")
    @Max(value = 65, message = "Age should not be greater than 65")
    private int age;
}
```

最后，我们将[BeanValidatorPluginsConfiguration](https://springfox.github.io/springfox/javadoc/current/springfox/bean/validators/configuration/BeanValidatorPluginsConfiguration.html)类导入SpringFoxConfig类：

```java
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SpringFoxConfig {
    //...
}
```

我们来看看API规范的变化：

[![swagger_user_2](https://www.baeldung.com/wp-content/uploads/2015/12/swagger_user_2.jpg)](https://www.baeldung.com/wp-content/uploads/2015/12/swagger_user_2.jpg)

在这里，我们可以观察到User模型在firstName上有 required。此外，还为年龄定义了最小值和最大值。

## 8. 插件

为了在 API 规范中添加特定的功能，我们可以创建一个 Springfox 插件。插件可以提供各种功能，从丰富模型和属性到自定义 API 列表和默认值。

Springfox 通过其[spi](https://github.com/springfox/springfox/tree/master/springfox-spi/src/main/java/springfox/documentation/spi)模块支持插件创建。spi 模块提供了一些接口，例如[ModelBuilderPlugin](https://springfox.github.io/springfox/javadoc/current/springfox/documentation/spi/schema/ModelBuilderPlugin.html)、[ModelPropertyBuilderPlugin](https://springfox.github.io/springfox/javadoc/current/springfox/documentation/spi/schema/ModelPropertyBuilderPlugin.html)和[ApiListingBuilderPlugin](https://springfox.github.io/springfox/javadoc/current/springfox/documentation/spi/service/ApiListingBuilderPlugin.html)，它们充当实现自定义插件的可扩展性挂钩。

为了演示这些功能，让我们创建一个插件来丰富User模型的email属性。我们将使用ModelPropertyBuilderPlugin接口并设置模式和示例的值。

首先，让我们创建EmailAnnotationPlugin类并重写support 方法以允许任何[文档类型](https://springfox.github.io/springfox/javadoc/current/springfox/documentation/spi/DocumentationType.html)，例如 Swagger 1.2 和 Swagger 2：

```java
@Component
@Order(Validators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class EmailAnnotationPlugin implements ModelPropertyBuilderPlugin {
    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }
}
```

然后我们将覆盖ModelPropertyBuilderPlugin的apply方法来设置构建器属性的值：

```java
@Override
public void apply(ModelPropertyContext context) {
    Optional<Email> email = annotationFromBean(context, Email.class);
     if (email.isPresent()) {
        context.getSpecificationBuilder().facetBuilder(StringElementFacetBuilder.class)
          .pattern(email.get().regexp());
        context.getSpecificationBuilder().example("email@email.com");
    }
}
```

因此，API 规范将显示带有@Email注解的属性的模式和示例值。

接下来，我们将@Email注解添加到User实体：

```java
@Entity
public class User {
    //...

    @Email(regexp=".@...", message = "Email should be valid")
    private String email;
}
```

最后，我们将通过注册为 bean 来启用 SpringFoxConfig 类中的 EmailAnnotationPlugin ：

```java
@Import({BeanValidatorPluginsConfiguration.class})
public class SpringFoxConfig {
    //...

    @Bean
    public EmailAnnotationPlugin emailPlugin() {
        return new EmailAnnotationPlugin();
    }
}
```

让我们看看正在运行的EmailAnnotationPlugin：

[![swagger_user_3](https://www.baeldung.com/wp-content/uploads/2015/12/swagger_user_3.jpg)](https://www.baeldung.com/wp-content/uploads/2015/12/swagger_user_3.jpg)

我们可以从User实体的email属性中看到该模式的值是相同的正则表达式 (.@...) 。

同样，示例( email@email.com ) 的值与EmailAnnotationPlugin的apply方法中定义的值相同。

## 9. 高级配置

我们的应用程序的Docket bean 可以配置为让我们更好地控制 API 文档生成过程。

### 9.1。过滤 Swagger 响应的 API

公开整个 API 的文档并不总是可取的。我们可以通过向Docket类的apis()和paths()方法传递参数来限制 Swagger 的响应。

如上所示，RequestHandlerSelectors允许使用any或none谓词，但也可用于根据基包、类注解和方法注解过滤 API。

PathSelectors使用谓词提供额外的过滤，它扫描我们应用程序的请求路径。我们可以使用any()、 none() 、regex()或ant()。

在下面的示例中，我们将使用ant()谓词指示 Swagger 仅包含来自特定包的具有特定路径的控制器：

```java
@Bean
public Docket api() {                
    return new Docket(DocumentationType.SWAGGER_2)          
      .select()                                       
      .apis(RequestHandlerSelectors.basePackage("com.baeldung.web.controller"))
      .paths(PathSelectors.ant("/foos/"))                     
      .build();
}
```

### 9.2. 自定义信息

Swagger 在其响应中也提供了一些默认值，我们可以自定义，例如“Api Documentation”、“Created by Contact Email”和“Apache 2.0”。

要更改这些值，我们可以使用apiInfo(ApiInfo apiInfo)方法 —包含有关 API 的自定义信息的ApiInfo类：

```java
@Bean
public Docket api() {                
    return new Docket(DocumentationType.SWAGGER_2)          
      .select()
      .apis(RequestHandlerSelectors.basePackage("com.example.controller"))
      .paths(PathSelectors.ant("/foos/"))
      .build()
      .apiInfo(apiInfo());
}

private ApiInfo apiInfo() {
    return new ApiInfo(
      "My REST API", 
      "Some custom description of API.", 
      "API TOS", 
      "Terms of service", 
      new Contact("John Doe", "www.example.com", "myeaddress@company.com"), 
      "License of API", "API license URL", Collections.emptyList());
}
```

### 9.3. 自定义方法响应消息

Swagger 允许通过Docket的globalResponses()方法全局覆盖 HTTP 方法的响应消息。

首先，我们需要指示 Swagger 不要使用默认响应消息。假设我们要覆盖所有GET方法的500和403响应消息。

为此，必须在Docket的初始化块中添加一些代码(为清楚起见，排除了原始代码)：

```java
.useDefaultResponseMessages(false)
.globalResponses(HttpMethod.GET, newArrayList(
    new ResponseBuilder().code("500")
        .description("500 message").build(),
    new ResponseBuilder().code("403")
        .description("Forbidden!!!!!").build()
));
```

[![截图_2](https://www.baeldung.com/wp-content/uploads/2016/07/Screenshot_2.png)](https://www.baeldung.com/wp-content/uploads/2016/07/Screenshot_2.png)

## 10. 带有 OAuth 安全 API 的 Swagger UI

Swagger UI 提供了许多非常有用的功能，到目前为止我们已经很好地介绍了这些功能。但是如果我们的 API 是安全的并且不可访问，我们就不能真正使用其中的大部分。

让我们看看如何允许 Swagger 在此示例中使用授权代码授权类型访问受 OAuth 保护的 API。

我们将配置 Swagger 以使用SecurityScheme和SecurityContext支持访问我们的安全 API：

```java
@Bean
public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2).select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build()
        .securitySchemes(Arrays.asList(securityScheme()))
        .securityContexts(Arrays.asList(securityContext()));
}
```

### 10.1。安全配置

我们将在 Swagger 配置中定义一个SecurityConfiguration bean 并设置一些默认值：

```java
@Bean
public SecurityConfiguration security() {
    return SecurityConfigurationBuilder.builder()
        .clientId(CLIENT_ID)
        .clientSecret(CLIENT_SECRET)
        .scopeSeparator(" ")
        .useBasicAuthenticationWithAccessCodeGrant(true)
        .build();
}
```

### 10.2. 安全计划

接下来，我们将定义我们的SecurityScheme；这用于描述我们的 API 是如何保护的(基本身份验证、OAuth2 ......)。

在我们这里的例子中，我们将定义一个用于保护我们的[资源服务器](https://www.baeldung.com/rest-api-spring-oauth2-angular)的 OAuth 方案：

```java
private SecurityScheme securityScheme() {
    GrantType grantType = new AuthorizationCodeGrantBuilder()
        .tokenEndpoint(new TokenEndpoint(AUTH_SERVER + "/token", "oauthtoken"))
        .tokenRequestEndpoint(
          new TokenRequestEndpoint(AUTH_SERVER + "/authorize", CLIENT_ID, CLIENT_SECRET))
        .build();

    SecurityScheme oauth = new OAuthBuilder().name("spring_oauth")
        .grantTypes(Arrays.asList(grantType))
        .scopes(Arrays.asList(scopes()))
        .build();
    return oauth;
}
```

请注意，我们使用了授权代码授权类型，我们需要为此提供令牌端点和 OAuth2 授权服务器的授权 URL。

以下是我们需要定义的范围：

```java
private AuthorizationScope[] scopes() {
    AuthorizationScope[] scopes = { 
      new AuthorizationScope("read", "for read operations"), 
      new AuthorizationScope("write", "for write operations"), 
      new AuthorizationScope("foo", "Access foo API") };
    return scopes;
}
```

这些与我们在应用程序中实际为/foos API 定义的范围同步。

### 10.3. 安全上下文

最后，我们需要为示例 API定义一个SecurityContext ：

```java
private SecurityContext securityContext() {
    return SecurityContext.builder()
      .securityReferences(
        Arrays.asList(new SecurityReference("spring_oauth", scopes())))
      .forPaths(PathSelectors.regex("/foos."))
      .build();
}
```

请注意我们在参考中使用的名称 — spring_oauth — 如何与我们之前在SecurityScheme中使用的名称同步。

### 10.4. 测试

现在我们已经准备好一切准备就绪，让我们看看我们的 Swagger UI 并尝试访问 Foo API。

我们可以在本地访问 Swagger UI：

```bash
http://localhost:8082/spring-security-oauth-resource/swagger-ui.html
```

正如我们所见，由于我们的安全配置，现在存在一个新的 Authorize 按钮：

[![招摇_1](https://www.baeldung.com/wp-content/uploads/2015/12/swagger_1-1024x362-1024x362.png)](https://www.baeldung.com/wp-content/uploads/2015/12/swagger_1-1024x362.png)

当我们单击 Authorize 按钮时，我们可以看到以下弹出窗口来授权我们的 Swagger UI 访问受保护的 API：

[![招摇_2](https://www.baeldung.com/wp-content/uploads/2015/12/swagger_2.png)](https://www.baeldung.com/wp-content/uploads/2015/12/swagger_2.png)

注意：

-   我们已经可以看到 CLIENT_ID 和 CLIENT_SECRET，因为我们之前已经预先配置了它们(但我们仍然可以更改它们)。
-   我们现在可以选择我们需要的范围。

以下是安全 API 的标记方式：

[![招摇_3](https://www.baeldung.com/wp-content/uploads/2015/12/swagger_3-1024x378-1024x378.png)](https://www.baeldung.com/wp-content/uploads/2015/12/swagger_3-1024x378.png)

现在，终于，我们可以使用我们的 API 了！

当然，几乎不用说我们需要小心我们如何在外部公开 Swagger UI，因为这个安全配置是活动的。

## 11. 总结

在本文中，我们设置 Swagger 2 来为 Spring REST API 生成文档。我们还探索了可视化和自定义 Swagger 输出的方法。最后，我们查看了 Swagger 的简单 OAuth 配置。

本教程的完整实现可以在[GitHub 项目](https://github.com/eugenp/tutorials/tree/master/spring-security-modules/spring-security-web-rest)中找到。要查看引导项目中的设置，请查看[此 GitHub 模块](https://github.com/eugenp/tutorials/tree/master/spring-boot-modules/spring-boot-mvc)。

对于 OAuth 部分，代码在我们的[spring-security-oauth](https://github.com/Baeldung/spring-security-oauth/tree/master/oauth-legacy) 存储库中可用。

如果是[REST With Spring](https://www.baeldung.com/rest-with-spring-course#master-class)的学生，请转到模块 7 的第 1 课，深入了解如何使用 Spring 和Spring Boot设置 Swagger。