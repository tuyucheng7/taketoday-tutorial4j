## 1. 概述

Spring框架版本5.0到5.0.4、4.3到4.3.14和其他旧版本在Windows系统上存在目录或路径遍历安全漏洞。

错误配置静态资源允许恶意用户访问服务器的文件系统。**例如，使用file:协议提供静态资源将提供对Windows上文件系统的非法访问**。

Spring框架承认了[该漏洞](https://tanzu.vmware.com/security/cve-2018-1271)并在后续版本中对其进行了修复。

因此，此修复可保护应用程序免受路径遍历攻击。但是，通过此修复，一些早期的URL现在会引发org.springframework.security.web.firewall.RequestRejectedException异常。

最后，**在本教程中，让我们了解路径遍历攻击上下文中的org.springframework.security.web.firewall.RequestRejectedException和StrictHttpFirewall**。

## 2. 路径遍历漏洞

路径遍历或目录遍历漏洞允许在Web文档根目录之外进行非法访问。例如，操纵URL可以提供对文档根目录之外的文件的未经授权的访问。

尽管大多数最新和流行的Web服务器抵消了大多数这些攻击，但攻击者仍然可以使用特殊字符(如“./”、“../”)的URL编码来绕过Web服务器安全并获得非法访问。

此外，[OWASP](https://owasp.org/www-community/attacks/Path_Traversal)还讨论了路径遍历漏洞以及解决这些漏洞的方法。

## 3. Spring框架漏洞

现在，在我们学习如何修复它之前，让我们尝试重现这个漏洞。

首先，让我们克隆Spring Framework MVC示例。然后，**我们修改pom.xml并将现有的Spring版本替换为易受攻击的版本**。

在C盘根目录下，克隆以下仓库：

```shell
git clone git@github.com:spring-projects/spring-mvc-showcase.git
```

在克隆的项目中，修改pom.xml中使用的Spring版本：

```xml
<org.springframework-version>5.0.0.RELEASE</org.springframework-version>
```

接下来，编辑Web配置类WebMvcConfig并修改addResourceHandlers方法以使用file协议将resources映射到本地文件目录：

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/resources/**")
          .addResourceLocations("file:./src/", "/resources/");
}
```

然后，构建工件并运行我们的Web应用程序：

```shell
mvn jetty:run
```

当服务器启动时，调用URL：

```shell
curl 'http://localhost:8080/spring-mvc-showcase/resources/%255c%255c%252e%252e%255c/%252e%252e%255c/%252e%252e%255c/%252e%252e%255c/%252e%252e%255c/windows/system.ini'
```

%252e%252e%255c是..\的双重编码形式，%255c%255c是\\\的双重编码形式。

**不稳定的是，响应将是Windows系统文件system.ini的内容**。

## 4. Spring Security HttpFirewall接口

[Servlet规范](https://javaee.github.io/servlet-spec/downloads/servlet-4.0/servlet-4_0_FINAL.pdf)没有精确定义servletPath和pathInfo之间的区别。因此，在这些值的转换中，Servlet容器之间存在不一致。

例如，在Tomcat 9上，对于URL [http://localhost:8080/api/v1/users/1](http://localhost:8080/api/v1/users/1)，URI /1旨在成为路径变量。

另一方面，以下返回/api/v1/users/1：

```java
request.getServletPath()
```

但是，以下调用返回null：

```java
request.getPathInfo()
```

无法将路径变量与URI区分开来可能会导致潜在的攻击，例如路径遍历/目录遍历攻击。例如，用户可以通过在URL中包含\\\、/../、..\来利用服务器上的系统文件。不幸的是，只有一些Servlet容器规范化了这些URL。

于是[Spring Security](https://spring.io/projects/spring-security)来拯救。Spring Security在容器中的行为始终如一，并使用HttpFirewall接口规范化这些类型的恶意URL。此接口有两个实现。

### 4.1 DefaultHttpFirewall

**首先，我们不要混淆实现类的名称。换句话说，这不是默认的HttpFirewall实现**。

防火墙尝试对URL进行清理或规范化，并跨容器标准化servletPath和pathInfo。此外，我们还可以通过显式声明@Bean来覆盖默认的HttpFirewall行为：

```java
@Bean
public HttpFirewall getHttpFirewall() {
    return new DefaultHttpFirewall();
}
```

但是，StrictHttpFirewall提供了一个健壮且安全的实现，并且是推荐的实现。

### 4.2 StrictHttpFirewall

**StrictHttpFirewall是HttpFirewall的默认和更严格的实现**。相比之下，与DefaultHttpFirewall不同，[StrictHttpFirewall](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/web/firewall/StrictHttpFirewall.html)拒绝任何未规范化的URL，提供更严格的保护。此外，此实现还可以保护应用程序免受其他几种攻击，如[跨站点跟踪(XST)](https://owasp.org/www-community/attacks/Cross_Site_Tracing)和[HTTP动词篡改](https://owasp.org/www-project-web-security-testing-guide/stable/4-Web_Application_Security_Testing/07-Input_Validation_Testing/03-Testing_for_HTTP_Verb_Tampering)。

此外，此实现是可自定义的，并且具有合理的默认值。换句话说，我们可以禁用(不推荐)一些功能，比如允许分号作为URI的一部分：

```java
@Bean
public HttpFirewall getHttpFirewall() {
    StrictHttpFirewall strictHttpFirewall = new StrictHttpFirewall();
    strictHttpFirewall.setAllowSemicolon(true);
    return strictHttpFirewall;
}
```

简而言之，StrictHttpFirewall通过org.springframework.security.web.firewall.RequestRejectedException拒绝可疑请求。

**最后，让我们使用[Spring MVC](https://www.baeldung.com/rest-with-spring-series)和[Spring Security](https://www.baeldung.com/security-spring)开发一个对用户进行CRUD操作的用户管理应用程序**，并了解StrictHttpFirewall的实际应用。

## 5. 依赖

让我们声明[Spring Security](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-security/3.0.4)和[Spring Web](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-web/3.0.4)依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
    <version>2.5.4</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.5.4</version>
</dependency>
```

## 6. Spring Security配置

接下来，让我们通过创建一个创建SecurityFilterChain bean的配置类来使用基本身份验证来保护我们的应用程序：

```java
@Configuration
public class HttpFirewallConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
              .disable()
              .authorizeRequests()
              .antMatchers("/error")
              .permitAll()
              .anyRequest()
              .authenticated()
              .and()
              .httpBasic();
        return http.build();
    }
}
```

默认情况下，Spring Security提供了一个默认密码，每次重启都会更改。因此，让我们在application.properties中创建一个默认的用户名和密码：

```properties
spring.security.user.name=user
spring.security.user.password=password
```

稍后，我们将使用这些凭据访问受保护的REST API。

## 7. 构建受保护的REST API

现在，让我们构建我们的用户管理REST API：

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserApi {

    private final UserServiceImpl userServiceImpl;

    public UserApi(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping
    public ResponseEntity<Response> createUser(@RequestBody User user) {
        if (StringUtils.isBlank(user.getId())) {
            user.setId(UUID.randomUUID().toString());
        }
        userServiceImpl.saveUser(user);
        Response response = new Response(HttpStatus.CREATED.value(), "User created successfully", System.currentTimeMillis());
        URI location = URI.create("/users/" + user.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable("userId") String userId) {
        return userServiceImpl.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user exists with the given Id"));
    }

    @GetMapping()
    public List<User> getAllUsers() {
        return userServiceImpl.findAll().orElse(new ArrayList<>());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Response> deleteUser(@PathVariable("userId") String userId) {
        userServiceImpl.deleteUser(userId);
        return ResponseEntity.ok(new Response(200, "The user has been deleted successfully", System.currentTimeMillis()));
    }
}
```

现在，让我们构建并运行应用程序：

```shell
mvn spring-boot:run
```

## 8. 测试API

现在，让我们使用http-request创建一个用户：

```http request
### [Spring Security – Request Rejected Exception] -- create user
POST http://localhost:8080/api/v1/users
Authorization: Basic user password
Content-Type: application/json
Accept: application/json

< ./request.json
```

这是request.json文件：

```json
{
    "id": "1",
    "username": "navuluri",
    "email": "bhaskara.navuluri@mail.com"
}
```

因此，响应是：

```http request
HTTP/1.1 201
Location: /users/1
Content-Type: application/json
{
    "code": 201,
    "message": "User created successfully",
    "timestamp": 1664269054955
}
```

现在，让我们配置StrictHttpFirewall以拒绝来自所有HTTP方法的请求：

```java
@Bean
public HttpFirewall configureFirewall() {
    StrictHttpFirewall strictHttpFirewall = new StrictHttpFirewall();
    strictHttpFirewall.setAllowedHttpMethods(Collections.emptyList());
    return strictHttpFirewall;
}
```

接下来，让我们再次调用API。由于我们配置了StrictHttpFirewall来限制所有的HTTP方法，所以这一次，我们得到了一个错误。

在日志中，我们可以看到以下异常：

```shell
org.springframework.security.web.firewall.RequestRejectedException: 
The request was rejected because the HTTP method "POST" was not included
  within the list of allowed HTTP methods []
```

**从Spring Security 5.4开始，当出现RequestRejectedException时，我们可以使用RequestRejectedHandler来自定义HTTP状态码**：

```java
@Configuration
public class HttpFirewallConfiguration extends WebSecurityConfigurerAdapter {

    /*
     Use this bean if you are using Spring Security 5.4 and above
     */
    @Bean
    public RequestRejectedHandler requestRejectedHandler() {
        return new HttpStatusRequestRejectedHandler(); // Default status code is 400. Can be customized
    }
}
```

请注意，使用HttpStatusRequestRejectedHandler时的默认HTTP状态码是400。但是，我们可以通过在HttpStatusRequestRejectedHandler类的构造函数中传递状态码来自定义它。

现在，让我们重新配置StrictHttpFirewall以允许URL中的\\\以及HTTP GET、POST、DELETE和OPTIONS方法：

```java
strictHttpFirewall.setAllowBackSlash(true);
strictHttpFirewall.setAllowedHttpMethods(Arrays.asList("GET","POST","DELETE", "OPTIONS");
```

接下来，调用API：

```http request
### [Spring Security – Request Rejected Exception] -- create user
POST http://localhost:8080/api<strong>\\</strong>v1/users
Authorization: Basic user password
Content-Type: application/json
Accept: application/json

< ./request.json
```

这是返回的响应：

```json
{
    "code": 201,
    "message": "User created successfully",
    "timestamp": 1664269154955
}
```

最后，让我们通过删除StrictHttpFirewall的@Bean声明来恢复StrictHttpFirewall原始严格功能。

接下来，让我们尝试使用可疑URL调用我们的API：

```http request
### [Spring Security – Request Rejected Exception] -- create user - 2
POST http://localhost:8080/api/v1<strong>//</strong>users
Authorization: Basic user password
Content-Type: application/json
Accept: application/json

< ./request.json

### [Spring Security – Request Rejected Exception] -- create user - 2
POST http://localhost:8080/api/v1<strong>\\</strong>users
Authorization: Basic user password
Content-Type: application/json
Accept: application/json

< ./request.json
```

此时，上述所有请求都失败并显示错误日志：

```java
org.springframework.security.web.firewall.RequestRejectedException: 
The request was rejected because the URL contained a potentially malicious String "//"
```

## 9. 总结

本文介绍了Spring Security对可能导致路径遍历/目录遍历攻击的恶意URL的保护。

DefaultHttpFirewall尝试规范化恶意URL。而StrictHttpFirewall会以RequestRejectedException拒绝请求。除了路径遍历攻击外，StrictHttpFirewall还可以保护我们免受其他几种攻击。因此，强烈建议使用StrictHttpFirewall及其默认配置。