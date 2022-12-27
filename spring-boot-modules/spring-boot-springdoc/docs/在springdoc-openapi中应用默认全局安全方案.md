## 1. 概述

**在本教程中，我们将学习如何配置默认的全局安全方案，并在Spring MVC Web应用程序中使用springdoc-openapi库将其应用为API的默认安全要求**。此外，我们还将讨论如何覆盖这些默认安全要求。

[OpenAPI规范](https://github.com/OAI/OpenAPI-Specification/tree/3.0.1)允许我们为API定义一组安全方案，我们可以全局配置API的安全要求，或者为每个端点应用/删除它们。

## 2. 设置

当我们使用Spring Boot构建Maven项目时，让我们探索一下项目的设置。在本节结束时，我们将提供一个简单的Web应用程序。

### 2.1 依赖关系

该示例有两个依赖项，**第一个依赖项是**[spring-boot-starter-web](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web)，这是构建Web应用程序的主要依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.7.1</version>
</dependency>
```

**另一个依赖项是**[springdoc-openapi-ui](https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-ui)，它是将以HTML、JSON或YAML格式呈现API文档的库：

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.6.9</version>
</dependency>
```

### 2.2 应用程序入口点

依赖项准备就绪后，让我们定义应用程序的入口点。

**我们将使用@SpringBootApplication注解来引导应用程序并使用SpringApplication工具类来启动它**：

```java
@SpringBootApplication
public class DefaultGlobalSecuritySchemeApplication {
    public static void main(String[] args) {
        SpringApplication.run(DefaultGlobalSecuritySchemeApplication.class, args);
    }
}
```

## 3. springdoc-openapi基础配置

一旦我们配置了Spring MVC，让我们看看API语义信息。

**我们通过向DefaultGlobalSecuritySchemeApplication类添加springdoc-openapi注解来定义默认的全局安全方案和API元数据**，为了定义全局安全方案，我们将使用@SecurityScheme注解：

```java
@SecurityScheme(type = SecuritySchemeType.APIKEY, name = "api_key", in = SecuritySchemeIn.HEADER)
```

我们选择了APIKEY安全方案类型，但我们可以配置其他安全方案，例如[JWT](https://www.baeldung.com/openapi-jwt-authentication)。定义安全方案后，我们将添加元数据并为API建立默认安全要求，我们使用@OpenApiDefinition注解来执行此操作：

```java
@OpenAPIDefinition(info = @Info(title = "Apply Default Global SecurityScheme in springdoc-openapi", version = "1.0.0"), security = {@SecurityRequirement(name = "api_key")})
```

此处，**info属性定义了API元数据**。此外，**security属性决定了默认的全局安全要求**。

让我们看看带有注解的HTML文档会是什么样子，我们将看到适用于整个API的元数据和安全按钮：

<img src="../assets/img_28.png">

## 4. 控制器

现在我们已经配置了Spring框架和springdoc-openapi库，**让我们向上下文基本路径添加一个REST控制器**。为此，我们将使用@RestController和@RequestMapping注解：

```java
@RestController
@RequestMapping("/")
public class DefaultGlobalSecuritySchemeOpenApiController {
    // ...
}
```

之后，**我们将定义两个端点或**[路径](https://swagger.io/docs/specification/paths-and-operations/)。

第一个端点将是/login端点，它将接收用户凭证并对用户进行身份验证。如果身份验证成功，则该端点将返回一个令牌。

API的另一个端点是/ping端点，需要/login方法生成的令牌。在执行请求之前，该方法将验证令牌并检查用户是否已获得授权。

总之，**/login端点对用户进行身份验证并提供令牌，/ping端点接收/login端点返回的令牌并检查它是否有效以及用户是否可以执行操作**。

### 4.1 login()方法

**此方法没有任何安全要求。因此，我们需要覆盖默认的安全需求配置**。

首先，我们需要告诉Spring这是我们API的端点，因此我们将添加注解@RequestMapping来配置端点：

```java
@RequestMapping(method = RequestMethod.POST, value = "/login", produces = {"application/json"}, consumes = {"application/json"})
```

之后，我们需要向端点添加语义信息，所以我们将使用@Operation和@SecurityRequirements注解。@Operation将定义端点，@SecurityRequirements将定义适用于端点的特定安全要求集：

```java
@Operation(operationId = "login", responses = {
	@ApiResponse(responseCode = "200", description = "api_key to be used in the secured-ping entry point", 
		content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TokenDto.class))}),
	@ApiResponse(responseCode = "401", description = "Unauthorized request", 
		content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationExceptionDto.class))})})
@SecurityRequirements()
```

例如，下面时状态代码为200的响应的HTML文档：

<img src="../assets/img_29.png">

最后，让我们看看login()方法的签名：

```java
public ResponseEntity login(@Parameter(name = "LoginDto", description = "Login") @Valid @RequestBody(required = true) LoginDto loginDto) {
    // ...
}
```

正如我们所见，API请求的主体接收一个LoginDto实例，我们还必须使用语义信息修饰DTO，以在文档中显示这些信息：

```java
public class LoginDto {
    private String user;
    private String pass;

    // ...

    @Schema(name = "user", required = true)
    public String getUser() {
        return user;
    }

    @Schema(name = "pass", required = true)
    public String getPass() {
        return pass;
    }
}
```

在这里，我们可以看到/login端点HTML文档的外观：

<img src="../assets/img_30.png">

### 4.2 ping()方法

此时，我们将定义ping()方法，**ping()方法将使用默认的全局安全方案**：

```java
@Operation(operationId = "ping", responses = {
	@ApiResponse(responseCode = "200", description = "Ping that needs an api_key attribute in the header", 
		content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PingResponseDto.class), 
			examples = {@ExampleObject(value = "{pong: '2022-06-17T18:30:33.465+02:00'}")})}),
	@ApiResponse(responseCode = "401", description = "Unauthorized request", 
		content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationExceptionDto.class))}),
	@ApiResponse(responseCode = "403", description = "Forbidden request", 
		content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationExceptionDto.class))})})
@RequestMapping(method = RequestMethod.GET, value = "/ping", produces = { "application/json" })
public ResponseEntity ping(@RequestHeader(name = "api_key", required = false) String api_key) {
    // ...
}
```

login()和ping()方法之间的主要区别在于它将应用的安全要求，login()根本没有任何安全要求，但ping()方法将具有在API级别定义的安全性。因此，HTML文档将表示仅针对/ping端点显示锁定的情况：

<img src="../assets/img_31.png">

## 5. REST API文档URL

此时，我们已经准备好Spring MVC Web应用程序，我们可以启动服务器：

```bash
mvn spring-boot:run -Dstart-class="cn.tuyucheng.taketoday.defaultglobalsecurityscheme.DefaultGlobalSecuritySchemeApplication"
```

服务器准备就绪后，我们可以在http://localhost:8080/swagger-ui-custom.html URL上看到HTML文档，如前面的示例所示。

API定义的JSON版本位于http://localhost:8080/api-docs，YAML版本位于http://localhost:8080/api-docs.yaml。

**这些输出可用于使用**[swagger-codegen-maven-plugin](https://mvnrepository.com/artifact/io.swagger.core.v3)**以不同语言构建API的**[客户端或服务器](https://www.baeldung.com/spring-boot-rest-client-swagger-codegen)。

## 6. 总结

在本文中，我们学习了如何使用springdoc-openapi库来定义默认的全局安全方案。此外，我们还看到了如何将其作为默认安全要求应用于API。此外，我们还学习了如何更改特定端点的默认安全要求。

并且我们知道，我们可以使用springdoc-openapi的JSON和YAML输出自动生成代码。