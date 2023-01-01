## 1. 概述

如今，前端和后端组件通常将Web应用程序分开。通常，我们将API公开为前端组件或第三方应用程序集成的后端组件。在这种情况下，必须为后端API制定适当的规范。同时，API文档应该信息丰富、易读且易于理解。此外，参考文档应同时描述API中的每个更改。手动完成这些任务是一项繁琐的工作，因此将该过程自动化是不可避免的。

在本教程中，我们将使用Swagger 2规范的Springfox实现来了解Spring REST Web服务的Swagger 2。**值得一提的是，最新版本的Swagger规范(现在称为OpenAPI 3.0)得到了Springdoc项目的更好支持，应该用于记录Spring REST API**。

## 2. Maven依赖

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```

## 3. 将Swagger 2集成到项目中

### 3.1 Java配置

Swagger的配置主要围绕Docket bean：

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

在定义了Docket bean之后，它的select()方法返回一个ApiSelectorBuilder的实例，该实例提供了一种控制Swagger公开的端点的方法。

我们可以在RequestHandlerSelectors和PathSelectors的帮助下配置用于选择RequestHandlers的谓词。对这两者都使用any()将使我们的整个API的文档可以通过Swagger获得。

### 3.2 不使用Spring Boot的配置

**在普通的Spring项目中，我们需要显式启用Swagger 2。为此，我们必须在配置类上使用@EnableSwagger2WebMvc**：

```java
@Configuration
@EnableSwagger2WebMvc
public class SpringFoxConfig {
}
```

此外，如果没有Spring Boot，我们就无法自动配置资源处理程序。

Swagger UI添加了一组资源，我们必须将它们配置为继承WebMvcConfigurerAdapter并使用@EnableWebMvc标注的类的一部分：

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("swagger-ui.html")
            .addResourceLocations("classpath:/META-INF/resources/");
            
    registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/");
}
```

### 3.3 验证

要验证Springfox是否正常工作，我们可以在浏览器中访问以下URL：

```text
http://localhost:8080/api/v2/api-docs
```

返回的结果是带有大量键值对的JSON响应，可读性不强。幸运的是，Swagger为此提供了Swagger UI。

## 4. Swagger UI

Swagger UI是一个内置的解决方案，它使用户与Swagger生成的API文档的交互变得更加容易。

### 4.1 启用Springfox的Swagger UI

要使用Swagger UI，我们需要添加一个额外的Maven依赖项：

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>3.0.0</version>
</dependency>
```

如果我们添加了springfox-boot-starter依赖项，则它已包含以上依赖。

现在我们可以通过浏览器访问以下地址：

```text
http://localhost:8080/your-app-root/swagger-ui/index.html#/
```

**顺便说一下，在我们的例子中，确切的URL将是**：

```text
http://localhost:8080/swagger-ui/index.html#/
```

访问的结果应如下所示：

<img src="../assets/img.png">

### 4.2 Swagger文档

在Swagger的响应中是我们应用程序中定义的所有控制器的列表。单击其中任何一个都会列出有效的HTTP方法(DELETE、GET、HEAD、OPTIONS、PATCH、POST、PUT)。

展开每个方法会提供额外的有用数据，例如响应状态、内容类型和参数列表。也可以使用UI尝试访问每种方法。

Swagger与我们的代码库同步的能力至关重要。为了演示这一点，我们可以向我们的应用程序添加一个新控制器：

```java
@RestController
public class CustomController {

    @RequestMapping(value = "/custom", method = RequestMethod.POST)
    public String custom() {
        return "custom";
    }
}
```

现在，如果我们刷新Swagger文档，我们会在控制器列表中看到custom-controller，并且Swagger的响应中只显示了一种方法(POST)。

## 5. Spring Data REST

Springfox通过其springfox-data-rest库为Spring Data REST提供支持。

**如果Spring Boot发现类路径上有spring-boot-starter-data-rest依赖，它会处理自动配置**。

现在让我们创建一个名为User的实体类：

```java
@Entity
@Setter
@Getter
public class User {

    @Id
    private Long id;
    private String firstName;
    private int age;
    private String email;
}
```

然后创建一个与之对应的UserRepository：

```java
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
```

最后，我们将SpringDataRestConfiguration类导入SpringFoxConfig类：

```java
@EnableSwagger2WebMvc
@Import(SpringDataRestConfiguration.class)
public class SpringFoxConfig {
    //...
}
```

注意：我们使用@EnableSwagger2WebMvc注解来启用Swagger，因为它已经在版本3中的替换了@EnableSwagger2注解。

让我们重新启动应用程序以生成Spring Data REST API的规范：

<img src="../assets/img_1.png">

我们可以看到，Springfox使用GET、POST、PUT、PATCH和DELETE等HTTP方法生成了User实体的规范。

## 6. Bean Validations

Springfox还通过其springfox-bean-validators库支持bean validation注解。

首先，我们将Maven依赖项添加到POM中：

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-bean-validators</artifactId>
    <version>2.9.2</version>
</dependency>
```

同样，**如果我们使用Spring Boot，我们不必显式添加上述依赖项**。

接下来，我们为User实体添加一些校验注解，比如@NotNull和@Min：

```java
public class User {

    @Id
    private Long id;

    @NotNull(message = "First Name cannot be null")
    private String firstName;

    @Min(value = 15, message = "Age should not be less than 15")
    @Max(value = 65, message = "Age should not be greater than 65")
    private int age;

    @Email(regexp = ".*@.*\\..*", message = "Email should be valid")
    private String email;
}
```

最后，我们需要将BeanValidatorPluginsConfiguration类导入SpringFoxConfig类：

```java
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SpringFoxConfig {
    //...
}
```

让我们看看API规范的变化：

<img src="../assets/img_2.png">

在这里，我们可以观察到User模型中的firstName标有”*required“。此外，还定义了age的最小值和最大值。

## 7. 插件

为了在API规范中添加特定的功能，我们可以创建一个Springfox插件。插件可以提供各种功能，从丰富模型和属性到自定义API列表和默认值。

Springfox通过其spi模块支持插件创建。spi模块提供了一些接口，例如ModelBuilderPlugin、ModelPropertyBuilderPlugin和ApiListingBuilderPlugin，它们充当实现自定义插件的可扩展性钩子。

为了演示这些功能，让我们创建一个插件来丰富User模型的email属性。我们将使用ModelPropertyBuilderPlugin接口并设置pattern和example的值。

首先，我们创建EmailAnnotationPlugin类并重写support方法以允许任何文档类型，例如Swagger 1.2和Swagger 2：

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

然后我们重写ModelPropertyBuilderPlugin的apply方法来设置构建器属性的值：

```java
@Override
public void apply(ModelPropertyContext context) {
    Optional<Email> email = annotationFromBean(context, Email.class);
    if (email.isPresent()) {
        context.getSpecificationBuilder().facetBuilder(StringElementFacetBuilder.class).pattern(email.get().regexp());
        context.getSpecificationBuilder().example("email@email.com");
    }
}
```

因此，API规范将显示带有@Email注解的属性的pattern和example值。接下来，我们将@Email注解添加到User实体：

```java
@Entity
public class User {
    //...

    @Email(regexp = ".*@.*\\..*", message = "Email should be valid")
    private String email;
}
```

最后，我们通过将EmailAnnotationPlugin注册为bean来启用SpringFoxConfig类中的EmailAnnotationPlugin：

```java
@Import({BeanValidatorPluginsConfiguration.class})
public class SpringFoxConfig {

    @Bean
    public EmailAnnotationPlugin emailPlugin() {
        return new EmailAnnotationPlugin();
    }
}
```

下面是配置了EmailAnnotationPlugin之后的效果：

<img src="../assets/img_3.png">

我们可以看到，pattern的值与User实体的email属性中的regex相同。

同样，example(email@email.com)的值与EmailAnnotationPlugin的apply方法中定义的值相同。

## 8. 高级配置

Docket bean可以配置为让我们更好地控制API文档生成过程。

### 8.1 Swagger响应的过滤API

公开整个API的文档并不总是可取的。我们可以通过向Docket类的apis()和paths()方法传递参数来限制Swagger的响应。如上所示，RequestHandlerSelectors允许使用any或none谓词，但也可用于根据根包、类注解和方法注解过滤API。PathSelectors使用谓词提供额外的过滤，它扫描应用程序的请求路径。我们可以使用any()、none()、regex()或ant()。

在下面的例子中，我们使用ant()谓词指示Swagger仅包含来自特定包的具有特定路径的控制器：

```java
@Bean
public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.baeldung.web.controller"))
            .paths(PathSelectors.ant("/foos/*"))
            .build();
}
```

### 8.2 自定义信息

Swagger在其响应中也提供了一些默认值，我们可以对其进行自定义，例如“Api Documentation”、“Created by Contact Email”和“Apache 2.0”。要更改这些值，我们可以使用apiInfo(ApiInfo apiInfo)方法，包含有关API的自定义信息的ApiInfo类：

```java
@Bean
public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.example.controller"))
            .paths(PathSelectors.ant("/foos/*"))
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

### 8.3 自定义方法响应消息

Swagger允许通过Docket的globalResponses()方法全局覆盖HTTP方法的响应消息。首先，我们需要指示Swagger不要使用默认响应消息。假设我们想覆盖所有GET方法的500和403响应消息。为了实现这一点，必须在Docket的初始化块中添加一些代码(为清楚起见，排除了原始代码)：

```java
.useDefaultResponseMessages(false)
.globalResponses(HttpMethod.GET, newArrayList(
        new ResponseBuilder().code("500")
                .description("500 message").build(),
        new ResponseBuilder().code("403")
                .description("Forbidden!!!!!")).build()
));
```

<img src="../assets/img_4.png">

## 9. Swagger UI与OAuth

Swagger UI提供了许多非常有用的功能，到目前为止我们已经很好地介绍了这些功能。但是如果我们的API是受保护的并且不可访问，我们就不能真正使用其中的大部分。让我们看看如何允许Swagger在此示例中使用授权代码授权类型访问受OAuth保护的API。

我们将配置Swagger以使用SecurityScheme和SecurityContext支持访问我们的Security API：

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

### 9.1 Security配置

我们将在Swagger配置中定义一个SecurityConfiguration bean并设置一些默认值：

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

### 9.2 SecurityScheme

接下来，我们定义SecurityScheme；这用于描述我们的API是如何保护的(基本身份验证、OAuth2...)。

在本例中，我们将定义一个OAuth Schema，用于保护资源服务器：

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

请注意，我们使用了授权码授权类型，我们需要为此提供token端点和OAuth2授权服务器的授权URL。

以下是我们需要定义的范围：

```java
private AuthorizationScope[] scopes() {
    AuthorizationScope[] scopes = {
            new AuthorizationScope("read", "for read operations"),
            new AuthorizationScope("write", "for write operations"),
            new AuthorizationScope("foo", "Access foo API")};
    return scopes;
}
```

这些与我们在应用程序中实际为/foos API定义的作用域同步。

### 9.3 SecurityContext

最后，我们需要为示例API定义一个SecurityContext：

```java
private SecurityContext securityContext() {
    return SecurityContext.builder()
            .securityReferences(
                    Arrays.asList(new SecurityReference("spring_oauth", scopes())))
            .forPaths(PathSelectors.regex("/foos.*"))
            .build();
}
```

请注意我们在这里引用的名称spring_oauth，它与我们之前在SecurityScheme中使用的名称同步。

### 9.4 测试

现在我们已经准备好一切准备就绪，让我们看看我们的Swagger UI并尝试访问Foo API。

我们可以在本地访问Swagger UI：

```text
http://localhost:8080/swagger-ui.html/
```

## 10. 总结

在本文中，我们设置Swagger 2来为Spring REST API生成文档，我们还介绍了可视化和自定义Swagger输出的方法。最后，我们演示了Swagger的简单OAuth配置。