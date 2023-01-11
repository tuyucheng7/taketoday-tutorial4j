## 1. 概述

在使用Spring Security时，我们可能需要使用比默认级别(一般为log)更高的日志级别。
例如，我们可能需要检查用户的角色或端点的安全性。或者，也许我们还需要有关身份验证或授权的更多信息，例如，查看用户无法访问端点的原因。

在这个简短的教程中，我们将介绍如何修改Spring Security的日志级别。

## 2. 配置Spring Security日志记录

**像任何Spring或Java应用程序一样，我们可以使用日志库并为Spring Security模块定义日志级别**。

通常，我们可以在配置文件中配置如下内容：

```text
<logger name="org.springframework.security" level="DEBUG" />
```

**如果我们使用的是Spring Boot，我们可以在application.properties文件中进行配置**：

```properties
logging.level.org.springframework.security=DEBUG
```

同样，我们可以使用yaml语法：

```yaml
logging:
    level:
        org:
            springframework:
                security: DEBUG
```

**这样，我们可以查看有关身份验证或过滤器链的日志**。此外，我们甚至可以使用DEBUG级别进行更深入的调试。

Spring Security还提供了记录有关请求和应用过滤器的特定信息的可能性：

```java

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${spring.websecurity.debug:false}")
    boolean webSecurityDebug;

    @Override
    public void configure(WebSecurity web) {
        web.debug(webSecurityDebug);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/**")
                .permitAll();
    }
}
```

## 3. 案例

最后，为了测试我们的应用程序，让我们定义一个简单的控制器：

```java

@Controller
public class LoggingController {

    @GetMapping("/logging")
    public ResponseEntity<String> logging() {
        return new ResponseEntity<>("logging/tuyucheng", HttpStatus.OK);
    }
}
```

如果我们访问/logging 端点，我们可以观察输出的日志：

```text
...... DEBUG 11676 --- [nio-8080-exec-1] o.s.s.w.a.i.FilterSecurityInterceptor    : Authorized filter invocation [GET /logging] with attributes [permitAll]
...... DEBUG 11676 --- [nio-8080-exec-1] o.s.security.web.FilterChainProxy        : Secured GET /logging
...... DEBUG 11676 --- [nio-8080-exec-1] w.c.HttpSessionSecurityContextRepository : Did not store anonymous SecurityContext
...... DEBUG 11676 --- [nio-8080-exec-1] s.s.w.c.SecurityContextPersistenceFilter : Cleared SecurityContextHolder to complete request
```

```text
Request received for GET '/logging':

org.apache.catalina.connector.RequestFacade@62b896bb

servletPath:/logging
pathInfo:null
headers: 
host: localhost:8080
connection: keep-alive
sec-ch-ua: "Google Chrome";v="105", "Not)A;Brand";v="8", "Chromium";v="105"
sec-ch-ua-mobile: ?0
sec-ch-ua-platform: "Windows"
upgrade-insecure-requests: 1
user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36
accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9
sec-fetch-site: none
sec-fetch-mode: navigate
sec-fetch-user: ?1
sec-fetch-dest: document
accept-encoding: gzip, deflate, br
accept-language: zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7
cookie: _ga=GA1.1.271925953.1641752097; Idea-df314ed1=21f5ddd8-b550-4ca1-a802-e987a7712ede; Idea-df315290=a74c79a7-a4f0-4ba3-a0aa-892c84924251; Idea-df315291=493b73ed-4c17-4c96-83da-f8e52ddd4a5d; JSESSIONID=0BAB099D31DDAE3E09C0045B50A0C3B5


Security filter chain: [
  WebAsyncManagerIntegrationFilter
  SecurityContextPersistenceFilter
  HeaderWriterFilter
  CsrfFilter
  LogoutFilter
  RequestCacheAwareFilter
  SecurityContextHolderAwareRequestFilter
  AnonymousAuthenticationFilter
  SessionManagementFilter
  ExceptionTranslationFilter
  FilterSecurityInterceptor
]
```

## 4. 总结

在本文中，我们介绍了为Spring Security启用不同日志级别的方式。

我们了解了如何为Spring Security模块使用debug级别。此外，我们还了解了如何记录有关单个请求的特定信息。