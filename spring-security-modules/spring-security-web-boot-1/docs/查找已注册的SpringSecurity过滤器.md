## 1. 概述

Spring Security基于一系列Servlet过滤器。每个过滤器都有特定的职责，并且根据配置添加或删除过滤器。

在本教程中，**我们将讨论获取已注册的Spring Security过滤器的不同方法**。

## 2. 安全调试

首先，我们将启用安全调试，它将记录每个请求的详细安全信息。

我们可以使用@EnableWebSecurity注解的debug属性启用安全调试：

```java
@EnableWebSecurity(debug = true)
```

这样，当我们向服务器发送请求时，所有的请求信息都会被记录下来。

我们还能够看到整个安全过滤器链：

```shell
Security filter chain: [
    WebAsyncManagerIntegrationFilter
    SecurityContextPersistenceFilter
    HeaderWriterFilter
    LogoutFilter
    UsernamePasswordAuthenticationFilter
    // ...
]
```

## 3. 日志记录

接下来，我们将通过为FilterChainProxy启用日志记录来获取我们的安全过滤器。

我们可以通过将以下行添加到application.properties来启用日志记录：

```properties
logging.level.org.springframework.security.web.FilterChainProxy=DEBUG
```

以下是相关日志：

```shell
DEBUG o.s.security.web.FilterChainProxy - /foos/1 at position 1 of 12 in additional filter chain; firing Filter: 'WebAsyncManagerIntegrationFilter'
DEBUG o.s.security.web.FilterChainProxy - /foos/1 at position 2 of 12 in additional filter chain; firing Filter: 'SecurityContextPersistenceFilter'
DEBUG o.s.security.web.FilterChainProxy - /foos/1 at position 3 of 12 in additional filter chain; firing Filter: 'HeaderWriterFilter'
DEBUG o.s.security.web.FilterChainProxy - /foos/1 at position 4 of 12 in additional filter chain; firing Filter: 'LogoutFilter'
DEBUG o.s.security.web.FilterChainProxy - /foos/1 at position 5 of 12 in additional filter chain; firing Filter: 'UsernamePasswordAuthenticationFilter'
...
```

## 4. 以编程方式获取过滤器

现在，我们将了解如何以编程方式获取已注册的安全过滤器。

我们将使用FilterChainProxy来获取安全过滤器。

首先，让我们自动注入SecurityFilterChain bean：

```java
@Autowired
@Qualifier("springSecurityFilterChain")
private Filter springSecurityFilterChain;
```

在这里，我们在@Qualifier注解中指定的bean名称为springSecurityFilterChain，类型为Filter而不是FilterChainProxy。这是因为[WebSecurityConfiguration](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/configuration/WebSecurityConfiguration.html#springSecurityFilterChain--)中的springSecurityFilterChain()方法创建了Spring Security过滤器链，返回类型为Filter而不是FilterChainProxy。

接下来，我们将此对象强制转换为FilterChainProxy并调用getFilterChains()方法：

```java
public void getFilters() {
    FilterChainProxy filterChainProxy = (FilterChainProxy) springSecurityFilterChain;
    List<SecurityFilterChain> list = filterChainProxy.getFilterChains();
    list.stream().flatMap(chain -> chain.getFilters().stream())
        .forEach(filter -> System.out.println(filter.getClass()));
}
```

下面是一个示例输出：

```shell
class org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter
class org.springframework.security.web.context.SecurityContextPersistenceFilter
class org.springframework.security.web.header.HeaderWriterFilter
class org.springframework.security.web.authentication.logout.LogoutFilter
class org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
...
```

请注意，从Spring Security 3.1开始，**FilterChainProxy是使用SecurityFilterChain列表配置的**。但是，大多数应用程序只需要一个SecurityFilterChain。

## 5. 重要的Spring Security过滤器

最后，让我们看一些重要的Security过滤器：

+ UsernamePasswordAuthenticationFilter：处理身份认证，默认响应"/login" URL
+ AnonymousAuthenticationFilter：当SecurityContextHolder中没有authentication对象时，它会创建一个匿名authentication对象并将其放在那里
+ FilterSecurityInterceptor：在访问被拒绝时引发异常
+ ExceptionTranslationFilter：捕获Spring Security异常

## 6. 总结

在这篇快速文章中，我们探讨了如何以编程方式和使用日志来查找已注册的Spring Security过滤器。