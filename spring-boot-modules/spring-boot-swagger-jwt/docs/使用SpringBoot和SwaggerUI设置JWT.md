## 1. 简介

在这个简短的教程中，我们将了解如何配置Swagger UI以在它调用我们的API时包含JSON Web Token(JWT)。

## 2. Maven依赖

在这个例子中，我们将使用[springfox-boot-starter](https://search.maven.org/search?q=a:springfox-boot-starter)，它包括开始使用Swagger和Swagger UI所需的所有依赖项，让我们将它添加到我们的 pom.xml文件中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```

## 3. Swagger配置

首先，我们需要定义我们的ApiKey以将JWT作为authorization标头包含在内：

```java
private ApiKey apiKey() { 
    return new ApiKey("JWT", "Authorization", "header"); 
}
```

接下来，让我们使用全局AuthorizationScope配置JWT SecurityContext：

```java
private SecurityContext securityContext() { 
    return SecurityContext.builder().securityReferences(defaultAuth()).build(); 
} 

private List<SecurityReference> defaultAuth() { 
    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything"); 
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1]; 
    authorizationScopes[0] = authorizationScope; 
    return Arrays.asList(new SecurityReference("JWT", authorizationScopes)); 
}
```

然后，我们将API Docket bean配置为包含API信息、安全上下文和安全方案：

```java
@Bean
public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .securityContexts(Arrays.asList(securityContext()))
        .securitySchemes(Arrays.asList(apiKey()))
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build();
}
```

```java
private ApiInfo apiInfo() {
    return new ApiInfo(
        "My REST API",
        "Some custom description of API.",
        "1.0.0",
        "Terms of service",
        new Contact("Tuyuchengn", "www.tuyucheng.com", "tuyucheng@gmail.com"),
        "License of API",
        "API license URL",
        Collections.emptyList());
}
```

## 4. RestController

在我们的ClientsRestController中，让我们编写一个简单的getClients端点来返回客户端列表：

```java
@RestController(value = "/clients")
@Api( tags = "Clients")
public class ClientsRestController {

    @ApiOperation(value = "This method is used to get the clients.")
    @GetMapping
    public List<String> getClients() {
        return Arrays.asList("First Client", "Second Client");
    }
}
```

## 5. Swagger的用户界面

现在，当我们启动应用程序时，我们可以通过http://localhost:8080/swagger-ui/访问Swagger UI。

以下是带有Authorize按钮的Swagger UI：

<img src="../assets/img.png">

当我们点击Authorize按钮时，Swagger UI将请求我们输入JWT。

我们只需要输入我们的令牌并单击Authorize，从此之后，对我们API发出的所有请求将自动在HTTP标头中包含令牌：

<img src="../assets/img_2.png">

## 6. 使用JWT的API请求

当向我们的API发送请求时，我们可以看到有一个带有令牌值的“Authorization”标头：

<img src="../assets/img_1.png">

## 7. 总结

在本文中，我们了解了Swagger UI如何提供自定义配置来设置JWT，这在处理我们的应用程序授权时会很有帮助。在Swagger UI中授权后，所有的请求都会自动包含我们的JWT。