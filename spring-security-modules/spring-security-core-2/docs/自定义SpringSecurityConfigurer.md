## 1. 概述

Spring Security Java配置支持为我们提供了强大的流式API-为应用程序定义安全映射和规则。

在这篇简短的文章中，我们将了解如何实际定义一个自定义配置器；**这是将自定义逻辑引入标准安全配置的一种先进且灵活的方式**。

对于此处的快速示例，我们将添加根据给定的错误状态码列表为经过身份验证的用户记录错误的功能。

## 2. 自定义SecurityConfigurer

要定义我们的Configurer，首先**我们需要扩展AbstractHttpConfigurer类**：

```java
public class ClientErrorLoggingConfigurer extends AbstractHttpConfigurer<ClientErrorLoggingConfigurer, HttpSecurity> {
    private List<HttpStatus> errorCodes;

    public ClientErrorLoggingConfigurer(List<HttpStatus> errorCodes) {
        this.errorCodes = errorCodes;
    }

    public ClientErrorLoggingConfigurer() {

    }

    @Override
    public void init(HttpSecurity http) throws Exception {
        // initialization code
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(new ClientErrorLoggingFilter(errorCodes), FilterSecurityInterceptor.class);
    }
}
```

在这里，**我们需要重写的主要方法是configure()方法**-它包含此配置器将应用到的安全配置。

在我们的示例中，我们在最后一个Spring Security过滤器之后注册了一个新过滤器。此外，由于我们打算记录响应状态错误码，因此我们添加了一个errorCodes属性，我们可以使用它来控制我们将记录的错误码。

我们还可以选择在init()方法中添加额外的配置，该方法在configure()方法之前执行。

接下来，让我们定义我们在自定义实现中注册的Spring Security过滤器类：

```java
public class ClientErrorLoggingFilter extends GenericFilterBean {
    private static final Logger logger = LogManager.getLogger(ClientErrorLoggingFilter.class);

    private final List<HttpStatus> errorCodes;

    public ClientErrorLoggingFilter(List<HttpStatus> errorCodes) {
        this.errorCodes = errorCodes;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // ...
        chain.doFilter(request, response);
    }
}
```

这是一个标准的Spring过滤器类，它扩展了GenericFilterBean并重写了doFilter()方法。它有两个属性，分别表示我们将用于记录日志的logger和errorCodes列表。

让我们仔细看看doFilter()方法：

```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();

if (auth == null) {
    chain.doFilter(request, response);
    return;
}

int status = ((HttpServletResponse) response).getStatus();
if (status < 400 || status >= 500) {
    chain.doFilter(request, response);
    return;
}

if (errorCodes == null) {
    logger.debug("User " + auth.getName() + " encountered error " + status);
} else {
    if (errorCodes.stream().anyMatch(s -> s.value() == status)) {
        logger.debug("User " + auth.getName() + " encountered error " + status);
    }
}
```

如果状态码是客户端错误状态码(介于400和500之间)，那么我们将检查errorCodes列表。

如果它是空的，那么我们将显示任何客户端错误状态码。否则，我们将首先检查错误码是否是给定状态码列表的一部分。

## 3. 使用自定义Configurer

现在我们有了自定义API，**我们可以通过定义bean然后使用HttpSecurity的apply()方法将其添加到Spring Security配置中**：

```java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
              .antMatchers("/admin*")
              .hasAnyRole("ADMIN")
              .anyRequest()
              .authenticated()
              .and()
              .formLogin()
              .and()
              .apply(clientErrorLogging());
        return http.build();
    }

    @Bean
    public ClientErrorLoggingConfigurer clientErrorLogging() {
        return new ClientErrorLoggingConfigurer();
    }
}
```

我们还可以使用要记录的特定错误码列表来定义bean：

```java
@Bean
public ClientErrorLoggingConfigurer clientErrorLogging() {
    return new ClientErrorLoggingConfigurer(List.of(HttpStatus.NOT_FOUND));
}
```

就这样！现在我们的安全配置将包含自定义过滤器并显示日志消息。

如果我们希望在默认情况下添加自定义配置器，我们可以使用META-INF/spring.factories文件：

```properties
org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer=cn.tuyucheng.taketoday.dsl.ClientErrorLoggingConfigurer
```

要手动禁用它，我们可以使用disable()方法：

```java
//...
.apply(clientErrorLogging()).disable();
```

## 4. 总结

在这个快速教程中，我们介绍了Spring Security配置支持的一个高级特性-**如何定义我们自己的SecurityConfigurer**。