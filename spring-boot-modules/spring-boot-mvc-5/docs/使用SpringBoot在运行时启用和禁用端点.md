## 1. 概述

Spring Boot应用程序中的端点是与之交互的机制。有时，例如在计划外维护窗口期间，我们可能希望暂时限制应用程序与外部的交互。

在本教程中，我们将学习使用一些流行的库(例如[Spring Cloud](https://www.baeldung.com/spring-cloud-series)、[Spring Actuator](https://www.baeldung.com/spring-boot-actuators)和[Apache的Commons Configuration](https://commons.apache.org/proper/commons-configuration/))在Spring Boot应用程序中在运行时启用和禁用端点。

## 2. 设置

在本节中，让我们专注于为我们的Spring Boot项目设置关键方面。

### 2.1 Maven依赖项

首先，我们需要Spring Boot应用程序公开/refresh端点，所以让我们在项目的pom.xml文件中添加[spring-boot-starter-actuator](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-actuator/3.0.3)依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
    <version>2.7.5</version>
</dependency>
```

接下来，由于稍后我们需要@RefreshScope注解来重新加载环境中的属性源，因此让我们添加[spring-cloud-starter](https://search.maven.org/search?q=g: org.springframework.cloud AND a:spring-cloud-starter)依赖项：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter</artifactId>
    <version>3.1.5</version>
</dependency>
```

此外，我们还必须在项目的pom.xml文件的依赖管理部分添加[Spring Cloud的](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-dependencies/2022.0.1)，以便Maven使用兼容版本的spring-cloud-starter：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2021.0.5</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

最后，因为我们需要在运行时重新加载文件的能力，所以我们还要添加[commons-configuration](https://central.sonatype.com/artifact/commons-configuration/commons-configuration/20040121.140929)依赖项：

```xml
<dependency>
    <groupId>commons-configuration</groupId>
    <artifactId>commons-configuration</artifactId>
    <version>1.10</version>
</dependency>
```

### 2.2 配置

首先，让我们将配置添加到application.properties文件以在我们的应用程序中启用/refresh端点：

```properties
management.server.port=8081
management.endpoints.web.exposure.include=refresh
```

接下来，让我们定义一个额外的源，我们可以使用它来重新加载属性：

```properties
dynamic.endpoint.config.location=file:extra.properties
```

此外，让我们在application.properties文件中定义spring.properties.refreshDelay属性：

```properties
spring.properties.refreshDelay=1
```

最后，让我们在extra.properties文件中添加两个属性：

```properties
endpoint.foo=false
endpoint.regex=.*
```

在后面的部分中，我们将了解这些附加属性的核心意义。

### 2.3 API端点

首先，让我们在/foo路径中定义一个可用的示例GET API：

```java
@GetMapping("/foo")
public String fooHandler() {
    return "foo";
}
```

接下来，让我们分别在/bar1和/bar2路径上定义另外两个可用的GET API：

```java
@GetMapping("/bar1")
public String bar1Handler() {
    return "bar1";
}

@GetMapping("/bar2")
public String bar2Handler() {
    return "bar2";
}
```

在以下部分中，我们将学习如何切换单个端点，例如/foo。此外，我们还将看到如何切换一组端点，即/bar1和/bar2，可通过简单的正则表达式识别。

### 2.4 配置DynamicEndpointFilter

要在运行时切换一组端点，我们可以使用[Filter](https://www.baeldung.com/spring-boot-add-filter)。通过使用endpoint.regex模式匹配请求的端点，我们可以允许它成功或发送503 HTTP响应状态以表示匹配不成功。

因此，让我们通过扩展OncePerRequestFilter来定义DynamicEndpointFilter类：

```java
public class DynamicEndpointFilter extends OncePerRequestFilter {
    private Environment environment;

    // ...
}
```

此外，我们需要通过覆盖doFilterInternal()方法来添加模式匹配的逻辑：

```java
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String path = request.getRequestURI();
    String regex = this.environment.getProperty("endpoint.regex");
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(path);
    boolean matches = matcher.matches();

    if (!matches) {
        response.sendError(HttpStatus.SERVICE_UNAVAILABLE.value(), "Service is unavailable");
    } else {
        filterChain.doFilter(request,response);
    }
}
```

我们必须注意endpoint.regex属性的初始值是“.*”，允许所有请求通过此过滤器。

## 3. 切换使用环境属性

在本节中，我们将学习如何从extra.properties文件热重新加载环境属性。

### 3.1 重新加载配置

为此，让我们首先使用FileChangedReloadingStrategy为PropertiesConfiguration定义一个bean：

```java
@Bean
@ConditionalOnProperty(name = "dynamic.endpoint.config.location", matchIfMissing = false)
public PropertiesConfiguration propertiesConfiguration(
      @Value("${dynamic.endpoint.config.location}") String path,
      @Value("${spring.properties.refreshDelay}") long refreshDelay) throws Exception {
    String filePath = path.substring("file:".length());
    PropertiesConfiguration configuration = new PropertiesConfiguration(new File(filePath).getCanonicalPath());
    FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
    fileChangedReloadingStrategy.setRefreshDelay(refreshDelay);
    configuration.setReloadingStrategy(fileChangedReloadingStrategy);
    return configuration;
}
```

我们必须注意，属性的来源是使用application.properties文件中的dynamic.endpoint.config.location属性派生的。此外，根据spring.properties.refreshDelay属性的定义，重新加载会延迟1秒。

接下来，我们需要在运行时读取端点特定的属性。因此，让我们使用属性获取器定义EnvironmentConfigBean：

```java
@Component
public class EnvironmentConfigBean {

    private final Environment environment;

    public EnvironmentConfigBean(@Autowired Environment environment) {
        this.environment = environment;
    }

    public String getEndpointRegex() {
        return environment.getProperty("endpoint.regex");
    }

    public boolean isFooEndpointEnabled() {
        return Boolean.parseBoolean(environment.getProperty("endpoint.foo"));
    }

    public Environment getEnvironment() {
        return environment;
    }
}
```

接下来，让我们创建一个FilterRegistrationBean来注册DynamicEndpointFilter：

```java
@Bean
@ConditionalOnBean(EnvironmentConfigBean.class)
public FilterRegistrationBean<DynamicEndpointFilter> dynamicEndpointFilterFilterRegistrationBean(EnvironmentConfigBean environmentConfigBean) {
    FilterRegistrationBean<DynamicEndpointFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new DynamicEndpointFilter(environmentConfigBean.getEnvironment()));
    registrationBean.addUrlPatterns("*");
    return registrationBean;
}
```

### 3.2 确认

首先，让我们运行应用程序并访问/bar1或/bar2 API：

```shell
$ curl -iXGET http://localhost:9090/bar1
HTTP/1.1 200 
Content-Type: text/plain;charset=ISO-8859-1
Content-Length: 4
Date: Sat, 12 Nov 2022 12:46:32 GMT

bar1
```

正如预期的那样，我们得到了200 OK HTTP响应，因为我们保留了endpoint.regex属性的初始值以启用所有端点。

接下来，让我们通过更改extra.properties文件中的endpoint.regex属性来仅启用/foo端点：

```properties
endpoint.regex=.*/foo
```

继续，让我们看看我们是否能够访问/bar1 API端点：

```shell
$ curl -iXGET http://localhost:9090/bar1
HTTP/1.1 503 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sat, 12 Nov 2022 12:56:12 GMT
Connection: close

{"timestamp":1668257772354,"status":503,"error":"Service Unavailable","message":"Service is unavailable","path":"/springbootapp/bar1"}
```

正如预期的那样，DynamicEndpointFilter禁用了此端点并发送了带有HTTP 503状态代码的错误响应。

最后，我们还可以检查我们是否能够访问/foo API端点：

```shell
$ curl -iXGET http://localhost:9090/foo
HTTP/1.1 200 
Content-Type: text/plain;charset=ISO-8859-1
Content-Length: 3
Date: Sat, 12 Nov 2022 12:57:39 GMT

foo
```

完美的！看起来我们做对了。

## 4. 切换使用Spring Cloud和Actuator

在本节中，我们将学习另一种方法，即使用@RefreshScope注解和执行器/refresh端点在运行时切换API端点。

### 4.1 使用@RefreshScope的端点配置

首先，我们需要定义用于切换端点的配置bean并使用@RefreshScope对其进行注解：

```java
@Component
@RefreshScope
public class EndpointRefreshConfigBean {

    private boolean foo;
    private String regex;

    public EndpointRefreshConfigBean(@Value("${endpoint.foo}") boolean foo, @Value("${endpoint.regex}") String regex) {
        this.foo = foo;
        this.regex = regex;
    }
    // getters and setters
}
```

接下来，我们需要通过创建包装类(例如[ReloadableProperties](https://www.baeldung.com/spring-reloading-properties#2-reloading-properties-instance)和[ReloadablePropertySource](https://www.baeldung.com/spring-reloading-properties#1-reloading-environment-properties))使这些属性可发现和可重新加载。

最后，让我们更新我们的API处理程序以使用EndpointRefreshConfigBean的实例来控制切换流：

```java
@GetMapping("/foo")
public ResponseEntity<String> fooHandler() {
    if (endpointRefreshConfigBean.isFoo()) {
        return ResponseEntity.status(200).body("foo");
    } else {
        return ResponseEntity.status(503).body("endpoint is unavailable");
    }
}
```

### 4.2 确认

首先，让我们在endpoint.foo属性的值设置为true时验证/foo端点：

```shell
$ curl -isXGET http://localhost:9090/foo
HTTP/1.1 200
Content-Type: text/plain;charset=ISO-8859-1
Content-Length: 3
Date: Sat, 12 Nov 2022 15:28:52 GMT

foo
```

接下来，让我们将endpoint.foo属性的值设置为false，并检查端点是否仍可访问：

```properties
endpoint.foo=false
```

我们会注意到/foo端点仍处于启用状态。那是因为我们需要通过调用/refresh端点来重新加载属性源。所以，让我们做一次：

```shell
$ curl -Is --request POST 'http://localhost:8081/actuator/refresh'
HTTP/1.1 200
Content-Type: application/vnd.spring-boot.actuator.v3+json
Transfer-Encoding: chunked
Date: Sat, 12 Nov 2022 15:34:24 GMT

```

最后，让我们尝试访问/foo端点：

```shell
$ curl -isXGET http://localhost:9090/springbootapp/foo
HTTP/1.1 503
Content-Type: text/plain;charset=ISO-8859-1
Content-Length: 23
Date: Sat, 12 Nov 2022 15:35:26 GMT
Connection: close

endpoint is unavailable
```

我们可以看到端点在刷新后被禁用。

### 4.3 优点和缺点

与直接从环境中获取属性相比，Spring Cloud和Actuator方法有优点也有缺点。

首先，当我们依赖/refresh端点时，我们比基于时间的文件重新加载策略有更好的控制。所以应用程序不会在后台进行不必要的I/O调用。但是，在分布式系统的情况下，我们需要确保我们正在为所有节点调用/refresh端点。

其次，使用@RefreshScope注解管理配置bean需要我们在EndpointRefreshConfigBean类中显式定义成员变量以映射到extra.properties文件中的属性。因此，这种方法增加了在我们添加或删除属性时在配置bean中更改代码的开销。

最后，我们还必须注意脚本可以轻松解决第一个问题，而第二个问题更具体到我们如何利用这些属性。如果我们将基于正则表达式的URL模式与过滤器一起使用，那么我们可以使用单个属性控制多个端点，而无需更改配置bean的代码。

## 5. 总结

在本文中，我们探讨了在Spring Boot应用程序运行时切换API端点的多种策略。在这样做的同时，我们利用了一些核心概念，例如[属性的热重载](https://www.baeldung.com/spring-reloading-properties)和@RefreshScope注解。