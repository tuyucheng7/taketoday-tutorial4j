## 1. 概述

在本教程中，我们学习如何在[Spring Boot Actuator]()中启用所有端点，看看如何通过我们的属性文件来控制我们的端点。最后，我们将概述如何保护我们的端点。

就Actuator端点的配置方式而言，[Spring Boot 1.x和Spring Boot 2.x之间]()发生了一些变化。

## 2. 设置

为了使用Actuator，我们需要在我们的Maven配置中包含[spring-boot-starter-actuator](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-actuator)：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
    <version>2.5.1</version>
</dependency>
```

此外，从Spring Boot 2.0开始，**如果我们希望通过HTTP公开端点，我们需要包含[web starter](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-web)**：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.5.1</version>
</dependency>
```

## 3. 启用和公开端点

从Spring Boot 2开始，**我们必须启用并公开我们的端点**。默认情况下，启用除/shutdown之外的所有端点，并且仅公开/health和/info。所有端点都可以在/actuator中找到，即使我们为应用程序配置了不同的根上下文也是如此。

这意味着一旦我们将适当的starter依赖项添加到我们的Maven配置中，我们就可以在http://localhost:8080/actuator/health和http://localhost:8080/actuator/info访问/health和/info端点。

让我们访问http://localhost:8080/actuator并查看可用端点列表，因为Actuator端点已启用HATEOS，我们应该看到/health和/info。

```json
{
    "_links": {
        "self": {
            "href": "http://localhost:8080/actuator",
            "templated": false
        },
        "health": {
            "href": "http://localhost:8080/actuator/health",
            "templated": false
        },
        "info": {
            "href": "http://localhost:8080/actuator/info",
            "templated": false
        }
    }
}
```

### 3.1 公开所有端点

现在，让我们通过修改application.properties文件来公开除/shutdown之外的所有端点：

```properties
management.endpoints.web.exposure.include=*
```

重新启动我们的服务器并再次访问/actuator端点，我们应该看到除/shutdown之外的其他可用端点：

```json
{
    "_links": {
        "self": {
            "href": "http://localhost:8080/actuator",
            "templated": false
        },
        "beans": {
            "href": "http://localhost:8080/actuator/beans",
            "templated": false
        },
        "caches": {
            "href": "http://localhost:8080/actuator/caches",
            "templated": false
        },
        "health": {
            "href": "http://localhost:8080/actuator/health",
            "templated": false
        },
        "info": {
            "href": "http://localhost:8080/actuator/info",
            "templated": false
        },
        "conditions": {
            "href": "http://localhost:8080/actuator/conditions",
            "templated": false
        },
        "configprops": {
            "href": "http://localhost:8080/actuator/configprops",
            "templated": false
        },
        "env": {
            "href": "http://localhost:8080/actuator/env",
            "templated": false
        },
        "loggers": {
            "href": "http://localhost:8080/actuator/loggers",
            "templated": false
        },
        "heapdump": {
            "href": "http://localhost:8080/actuator/heapdump",
            "templated": false
        },
        "threaddump": {
            "href": "http://localhost:8080/actuator/threaddump",
            "templated": false
        },
        "metrics": {
            "href": "http://localhost:8080/actuator/metrics",
            "templated": false
        },
        "scheduledtasks": {
            "href": "http://localhost:8080/actuator/scheduledtasks",
            "templated": false
        },
        "mappings": {
            "href": "http://localhost:8080/actuator/mappings",
            "templated": false
        }
    }
}
```

### 3.2 公开特定端点

某些端点可能会暴露敏感数据，因此让我们了解如何更加细化我们暴露的端点。

management.endpoints.web.exposure.include属性也可以采用逗号分隔的端点列表。所以，让我们只公开/beans和/loggers：

```properties
management.endpoints.web.exposure.include=beans,loggers
```

除了在属性中包含某些端点之外，我们还可以排除端点。下面我们公开除/threaddump之外的所有端点：

```properties
management.endpoints.web.exposure.include=*
management.endpoints.web.exposure.exclude=threaddump
```

include和exclude属性都接收端点列表，**exclude属性优先于include**。

### 3.3 启用特定端点

接下来，让我们了解如何更细粒度地了解我们启用了哪些端点。

首先，我们需要关闭启用所有端点的默认值：

```properties
management.endpoints.enabled-by-default=false
```

接下来，让我们只启用和公开/health端点：

```properties
management.endpoint.health.enabled=true
management.endpoints.web.exposure.include=health
```

使用此配置，我们只能访问/health端点。

### 3.4 启用shutdown端点

由于其敏感性，**/shutdown端点默认处于禁用状态**。

现在让我们通过在application.properties文件中添加一行来启用它：

```properties
management.endpoint.shutdown.enabled=true
```

现在，当我们调用/actuator端点时，我们应该会看到它已列出。**/shutdown端点只接受POST请求**，所以让我们优雅地关闭我们的应用程序：

```shell
curl -X POST http://localhost:8080/actuator/shutdown
```

## 4. 保护端点

在实际应用程序中，我们很可能会对应用程序添加安全性。考虑到这一点，让我们保护我们的Actuator端点。

首先，我们通过添加[security starter](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-security) Maven依赖项来为我们的应用程序添加安全性：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
    <version>2.5.1</version>
</dependency>
```

对于最基本的安全性，这就是我们所要做的。只需添加security starter，我们就自动将基本身份验证应用于除/info和/health之外的所有公开端点。

现在，让我们自定义我们的安全性以将/actuator端点限制为ADMIN角色。

首先从排除默认安全配置开始：

```java
@SpringBootApplication(exclude = { 
    SecurityAutoConfiguration.class, 
    ManagementWebSecurityAutoConfiguration.class 
})
```

注意ManagementWebSecurityAutoConfiguration.class，因为这会允许我们将我们自己的安全配置应用到/actuator。

在我们的配置类中，让我们配置几个用户和角色，以便我们有一个ADMIN角色可以使用：

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    auth.inMemoryAuthentication()
        .withUser("user")
        .password(encoder.encode("password"))
        .roles("USER")
        .and()
        .withUser("admin")
        .password(encoder.encode("admin"))
        .roles("USER", "ADMIN");
}
```

Spring Boot为我们提供了一个方便的请求匹配器，用于我们的Actuator端点。

让我们用它来将/actuator锁定为仅ADMIN角色：

```java
http.requestMatcher(EndpointRequest.toAnyEndpoint())
    .authorizeRequests((requests) -> requests.anyRequest().hasRole("ADMIN"));
```

## 5. 总结

在本教程中，我们学习了Spring Boot默认情况下如何配置Actuator。之后，我们在application.properties文件中自定义启用、禁用和公开的端点。由于Spring Boot默认以不同方式配置/shutdown端点，因此我们学习了如何单独启用它。

在学习了基础知识之后，我们接下来学习了如何配置Actuator安全性。