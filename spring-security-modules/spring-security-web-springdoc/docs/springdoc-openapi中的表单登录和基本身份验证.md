## 一、概述

[Springdoc-OpenAPI](https://springdoc.org/)是一个基于[OpenAPI 3](https://spec.openapis.org/oas/latest.html)规范自动为 Spring Boot 应用程序生成服务文档的库。

通过用户界面与我们的 API 交互而不需要实现 API 会很方便。因此，让我们看看如果涉及授权，我们如何使用端点。

在本教程中，我们将学习如何使用 Spring Security 通过表单登录和基本身份验证在 Springdoc 中管理安全端点访问。

## 2.项目设置

我们将设置一个 Spring Boot Web 应用程序，公开一个由 Spring Security 保护的 API，并使用 Springdoc 生成文档。

### 2.1. 依赖关系

让我们为我们的项目声明所需的 Maven 依赖项。首先，我们将添加[springdoc-openapi-ui](https://search.maven.org/artifact/org.springdoc/springdoc-openapi-ui/1.6.13/jar)，负责与[Swagger-UI](https://swagger.io/tools/swagger-ui/)集成并提供默认可访问的可视化工具：

```java
http://localhost:8080/swagger-ui.html
```

其次，添加[springdoc-openapi-security](https://search.maven.org/artifact/org.springdoc/springdoc-openapi-security/1.6.13/jar)模块提供了对 Spring Security 的支持：

```java
<dependency>
     <groupId>org.springdoc</groupId>
     <artifactId>springdoc-openapi-ui</artifactId>
     <version>1.6.13</version>
</dependency>
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-security</artifactId>
    <version>1.6.13</version>
</dependency>
```

### 2.2. 示例 API

对于本文，我们将实现一个虚拟 REST 控制器作为使用 Springdoc 生成文档的源。此外，我们将举例说明通过 Swagger-UI与FooController的受保护端点交互的身份验证方法。

```java
@RestController
@RequestMapping(value = "foos", produces = MediaType.APPLICATION_JSON_VALUE)
@OpenAPIDefinition(info = @Info(title = "Foos API", version = "v1"))
public class FooController {

    @GetMapping(value = "/{id}")
    public FooDTO findById(@PathVariable("id") final Long id) {
        return new FooDTO(randomAlphabetic(STRING_LENGTH));
    }

    @GetMapping
    public List<FooDTO> findAll() {
        return Lists.newArrayList(new FooDTO(randomAlphabetic(STRING_LENGTH)),
          new FooDTO(randomAlphabetic(STRING_LENGTH)), new FooDTO(randomAlphabetic(STRING_LENGTH)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FooDTO create(@RequestBody final FooDTO fooDTO) {
        return fooDTO;
    }
}
```

### 2.3. 用户凭证

我们将使用 Spring Security 的内存中身份验证来注册我们的测试用户凭据：

```java
@Autowired
public void configureGlobal(AuthenticationManagerBuilder auth, PasswordEncoder passwordEncoder) throws Exception {
    auth.inMemoryAuthentication()
      .withUser("user")
      .password(passwordEncoder.encode("password"))
      .roles("USER");
}
```

## 3. 基于表单的登录认证

让我们看看我们如何进行身份验证以与我们基于表单的登录安全记录端点进行交互。

### 3.1. 安全配置

在这里，我们定义了安全配置以授权使用表单登录的请求：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf().disable()
      .authorizeRequests()
      .antMatchers("/v3/api-docs/",
        "/swagger-ui/",
        "/swagger-ui.html").permitAll()
      .anyRequest().authenticated()
      .and()
      .formLogin()
      .defaultSuccessUrl("/foos");
    return http.build();
}
```

### 3.2. 登录文档

默认情况下，未记录框架提供的登录端点。因此我们需要通过设置相应的配置属性使其可见。此外，可以在库的[文档](https://springdoc.org/#springdoc-openapi-core-properties)中找到有用的配置属性：

```java
springdoc.show-login-endpoint=true
```

之后，Springdoc 将检测配置的 Spring Security 的 Form Login，并在 Swagger-UI 中生成文档。因此，它将使用用户名和密码请求参数以及特定的application/x-www-form-urlencoded请求主体类型添加/login端点：

[![登录端点 swagger ui](https://www.baeldung.com/wp-content/uploads/2022/12/2_login-endpoint-swagger-ui.png)](https://www.baeldung.com/wp-content/uploads/2022/12/2_login-endpoint-swagger-ui.png)

身份验证后，我们将调用受保护的FooController端点。此外，由于defaultSucccesfulUrl安全配置，我们从/foos端点获得成功登录的响应：

[![成功登录招摇](https://www.baeldung.com/wp-content/uploads/2022/12/2_successful-login-swagger.png)](https://www.baeldung.com/wp-content/uploads/2022/12/2_successful-login-swagger.png)

### 3.3. 注销文档

能够注销有助于在 Swagger-UI 中进行用户切换，这很有用。例如，应用基于角色的 API 授权时。

Springdoc 不提供像 login 那样自动检测注销端点的方法。在这种情况下，我们需要定义一个伪造的 REST 控制器，为/logout路径公开一个请求后映射。但是，我们不需要添加实现，因为 Spring Security 会拦截并处理请求：

```java
@RestController
public class LogoutController {

    @PostMapping("logout")
    public void logout() {}
}
```

通过添加LogoutController，该库将生成文档并使注销在 Swagger-UI 中可用：

[![Swagger-UI 中的注销端点](https://www.baeldung.com/wp-content/uploads/2022/12/2_logout-controller-endpoint-swagger-ui.png)](https://www.baeldung.com/wp-content/uploads/2022/12/2_logout-controller-endpoint-swagger-ui.png)

## 4. 基本认证

在处理基本身份验证安全端点时，我们不需要直接调用登录。另一方面，OpenAPI支持一套标准的[安全方案，](https://swagger.io/docs/specification/authentication/)包括Basic Auth，我们可以相应配置Springdoc。

### 4.1. 安全配置

使用基本身份验证保护端点的简单安全配置：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf().disable()
      .authorizeRequests()
      .antMatchers("/v3/api-docs/",
        "/swagger-ui/",
        "/swagger-ui.html").permitAll()
      .anyRequest().authenticated()
      .and()
      .httpBasic();
    return http.build();
}
```

### 4.2. Springdoc 安全方案

要配置 OpenAPI 安全方案，我们需要提供一个基于@SecurityScheme注解的配置：

```java
@Configuration
@SecurityScheme(
  type = SecuritySchemeType.HTTP,
  name = "basicAuth",
  scheme = "basic")
public class SpringdocConfig {}
```

然后，我们还必须使用@SecurityRequirement(name = “basicAuth”)注释我们的FooController。如果我们只想保护某些端点或使用不同的方案，我们可以在方法级别应用此注释：

```java
@RestController
@OpenAPIDefinition(info = @Info(title = "Foos API", version = "v1"))
@SecurityRequirement(name = "basicAuth")
@RequestMapping(value = "foos", produces = MediaType.APPLICATION_JSON_VALUE)
public class FooController {
    ...
}
```

因此，授权按钮将在 Swagger-UI 中可用：

[![Swagger-UI 中的授权按钮](https://www.baeldung.com/wp-content/uploads/2022/12/2_authorize-button-swagger-ui.png)](https://www.baeldung.com/wp-content/uploads/2022/12/2_authorize-button-swagger-ui.png)

然后，我们可以以以下形式提供我们的用户凭据：

[![授权基本身份验证](https://www.baeldung.com/wp-content/uploads/2022/12/2_basic-authentication-form-swagger.png)](https://www.baeldung.com/wp-content/uploads/2022/12/2_basic-authentication-form-swagger.png)

随后，当调用任何FooController端点时，带有凭据的授权标头将包含在请求中，如生成的 curl 命令所示。因此，我们将被授权执行请求：

[![Basic Auth 的授权标头](https://www.baeldung.com/wp-content/uploads/2022/12/2_authorization-headers-swagger-ui.png)](https://www.baeldung.com/wp-content/uploads/2022/12/2_authorization-headers-swagger-ui.png)

## 5.结论

在本文中，我们了解了如何在 Springdoc 中配置身份验证，以通过 Swagger-UI 中生成的文档访问受保护的端点。最初，我们进行了基于表单的登录设置。然后，我们为基本身份验证配置了安全方案。