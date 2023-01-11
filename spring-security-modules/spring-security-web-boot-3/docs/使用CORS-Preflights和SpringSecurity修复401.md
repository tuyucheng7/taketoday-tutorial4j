## 1. 概述

在这个教程中，我们将学习如何解决“Response for preflight has invalid HTTP status code 401”错误，
该错误可能发生在支持跨域通信并使用Spring Security的应用程序中。

首先，我们将了解什么是跨域请求，然后我们解决一个存在该问题的案例。

## 2. 跨域请求

简而言之，跨源请求是指请求的源和目标不同的HTTP请求。
例如，当Web应用程序从一个域提供服务，浏览器向另一个域中的服务器发送AJAX请求时，就会出现这种情况。

为了管理跨域请求，服务器需要启用一种称为CORS(跨域资源共享)的特殊机制。

CORS中的第一步是一个OPTIONS请求，以确定请求的目标是否支持它。**这称为pre-flight请求**。

然后，服务器可以使用一组标头响应pre-flight请求：

+ **Access-Control-Allow-Origin**：定义哪些源可以访问资源。“*”代表任何源。
+ **Access-Control-Allow-Methods**：表示允许跨域请求的HTTP方法。
+ **Access-Control-Allow-Headers**：表示允许跨域请求的请求头。
+ **Access-Control-Max-Age**：定义缓存的pre-flight请求结果的过期时间。

所以，如果pre-flight请求不满足这些响应头确定的条件，实际的后续请求会抛出与跨域请求相关的错误。

**将CORS支持添加到我们的基于Spring的服务中很容易，但如果配置不正确，这个pre-flight请求将始终失败并返回401**。

## 3. 创建支持CORS的REST API

为了模拟这个问题，我们首先创建一个支持跨域请求的简单REST API：

```java

@RestController
@CrossOrigin("http://localhost:4200")
public class ResourceController {

    @GetMapping("/user")
    public String user(Principal principal) {
        return principal.getName();
    }
}
```

@CrossOrigin注解确保我们的API只能从其参数中指定的源访问。

## 4. 保护我们的REST API

现在让我们使用Spring Security保护我们的REST API：

```java

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .httpBasic();
    }
}
```

在这个配置类中，我们对所有传入的请求强制授权。因此，它将拒绝所有没有有效authorization token的请求。

## 5. Pre-flight请求

现在让我们尝试使用curl进行pre-flight请求：

```text
curl -v -H "Access-Control-Request-Method: GET" -H "Origin: http://localhost:4200" 
  -X OPTIONS http://localhost:8080/user
...
< HTTP/1.1 401
...
< WWW-Authenticate: Basic realm="Realm"
...
< Vary: Origin
< Vary: Access-Control-Request-Method
< Vary: Access-Control-Request-Headers
< Access-Control-Allow-Origin: http://localhost:4200
< Access-Control-Allow-Methods: POST
< Access-Control-Allow-Credentials: true
< Allow: GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, PATCH
...
```

从该命令的输出中，我们可以看到请求被401拒绝。

由于这是一个curl命令，我们不会在输出中看到错误“Response for preflight has invalid HTTP status code 401”。

但是我们可以通过创建一个前端应用程序，使用来自不同域的REST API，并在浏览器中运行它来重现这个错误。

## 6. 解决方案

**我们没有在Spring Security配置中明确排除pre-flight请求的授权**。记住，默认情况下，Spring Security保护所有端点。

**因此，我们的API在OPTIONS请求中也需要一个authorization token**。

Spring提供了一个开箱即用的解决方案，用于从授权检查中排除OPTIONS请求：

```java

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // ...
        http.cors();
    }
}
```

cors()方法将Spring提供的CorsFilter添加到应用程序上下文中，从而绕过OPTIONS请求的授权检查。

现在我们可以再次测试我们的应用程序并观察它是否正常返回。

## 7. 总结

在这篇简短的文章中，我们学习了如何解决与Spring Security和跨域请求相关的错误
“Response for preflight has invalid HTTP status code 401”。

**请注意，在该示例中，客户端和API应在不同的域或端口上运行以重现问题**。
例如，当在本地机器上运行时，我们可以将默认主机名映射到客户端，并将机器IP地址映射到我们的REST API。