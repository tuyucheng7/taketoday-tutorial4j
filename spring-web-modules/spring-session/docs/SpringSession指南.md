## 1. 概述

Spring Session的简单目标是将会话管理从存储在服务器中的 HTTP 会话的限制中解放出来。

该解决方案可以轻松地在云服务之间共享会话数据，而无需绑定到单个容器(即 Tomcat)。此外，它支持同一浏览器中的多个会话以及在标头中发送会话。

在本文中，我们将使用Spring Session来管理 Web 应用程序中的身份验证信息。虽然Spring Session可以使用 JDBC、Gemfire 或 MongoDB 来持久化数据，但我们将使用Redis。

有关Redis的介绍，请查看[这篇](https://www.baeldung.com/spring-data-redis-tutorial)文章。

## 2. 一个简单的项目

让我们首先创建一个简单的Spring Boot项目，以用作稍后会话示例的基础：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.2</version>
    <relativePath/>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

我们的应用程序使用Spring Boot运行，父 pom 为每个条目提供版本。可以在此处找到每个依赖项的最新版本：[spring-boot-starter-security](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-security")、[spring-boot-starter-web](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-web")、[spring-boot-starter-test](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-test")。

让我们在application.properties中为我们的 Redis 服务器添加一些配置属性：

```plaintext
spring.redis.host=localhost
spring.redis.port=6379
```

## 3. Spring Boot配置

对于 Spring Boot，添加以下依赖项就足够了，自动配置将处理其余部分：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
</dependency>
```

我们使用启动父pom在这里设置版本，所以这些可以保证与我们的其他依赖项一起工作。可以在此处找到每个依赖项的最新版本：[spring-boot-starter-data-redis](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-data-redis")、[spring-session](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.session" AND a%3A"spring-session")。

## 4. 标准 Spring 配置(无引导)

让我们也看看在没有Spring Boot的情况下集成和配置spring- session——只是使用普通的 Spring。

### 4.1. 依赖关系

首先，如果我们要将spring-session添加到标准的 Spring 项目中，我们需要明确定义：

```xml
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session</artifactId>
    <version>1.2.2.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-redis</artifactId>
    <version>1.5.0.RELEASE</version>
</dependency>
```

这些模块的最新版本可以在这里找到：[spring-session](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.session" AND a%3A"spring-session")、[spring-data-redis](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.data" AND a%3A"spring-data-redis")。

### 4.2. 春季会议配置

现在让我们为Spring Session添加一个配置类：

```java
@Configuration
@EnableRedisHttpSession
public class SessionConfig extends AbstractHttpSessionApplicationInitializer {
    @Bean
    public JedisConnectionFactory connectionFactory() {
        return new JedisConnectionFactory();
    }
}
```

@EnableRedisHttpSession和AbstractHttpSessionApplicationInitializer的扩展将在我们所有的安全基础设施前面创建并连接一个过滤器，以查找活动会话并从存储在Redis中的值填充安全上下文。

现在让我们用一个控制器和安全配置来完成这个应用程序。

## 5. 应用配置

导航到我们的主应用程序文件并添加一个控制器：

```java
@RestController
public class SessionController {
    @RequestMapping("/")
    public String helloAdmin() {
        return "hello admin";
    }
}
```

这将为我们提供一个端点进行测试。

接下来，添加我们的安全配置类：

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.withUsername("admin")
            .password(passwordEncoder.encode("password"))
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic()
            .and()
            .authorizeRequests()
            .antMatchers("/")
            .hasRole("ADMIN")
            .anyRequest()
            .authenticated();
        return http.build();
    }

    @Bean 
    public PasswordEncoder passwordEncoder() { 
        return new BCryptPasswordEncoder(); 
    } 
}
```

这通过基本身份验证保护我们的端点并设置用户进行测试。

## 6.测试

最后，让我们测试一切——我们将在这里定义一个简单的测试，它将允许我们做两件事：

-   使用实时 Web 应用程序
-   与 Redis 对话

让我们先设置一下：

```java
public class SessionControllerTest {

    private Jedis jedis;
    private TestRestTemplate testRestTemplate;
    private TestRestTemplate testRestTemplateWithAuth;
    private String testUrl = "http://localhost:8080/";

    @Before
    public void clearRedisData() {
        testRestTemplate = new TestRestTemplate();
        testRestTemplateWithAuth = new TestRestTemplate("admin", "password", null);

        jedis = new Jedis("localhost", 6379);
        jedis.flushAll();
    }
}
```

请注意我们是如何设置这两个客户端的——HTTP 客户端和 Redis 客户端。当然，此时服务器(和 Redis)应该启动并运行——这样我们就可以通过这些测试与它们通信。

让我们首先测试Redis是否为空：

```java
@Test
public void testRedisIsEmpty() {
    Set<String> result = jedis.keys("");
    assertEquals(0, result.size());
}
```

现在测试我们的安全性是否为未经身份验证的请求返回 401：

```java
@Test
public void testUnauthenticatedCantAccess() {
    ResponseEntity<String> result = testRestTemplate.getForEntity(testUrl, String.class);
    assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
}
```

接下来，我们测试Spring Session是否正在管理我们的身份验证令牌：

```java
@Test
public void testRedisControlsSession() {
    ResponseEntity<String> result = testRestTemplateWithAuth.getForEntity(testUrl, String.class);
    assertEquals("hello admin", result.getBody()); //login worked

    Set<String> redisResult = jedis.keys("");
    assertTrue(redisResult.size() > 0); //redis is populated with session data

    String sessionCookie = result.getHeaders().get("Set-Cookie").get(0).split(";")[0];
    HttpHeaders headers = new HttpHeaders();
    headers.add("Cookie", sessionCookie);
    HttpEntity<String> httpEntity = new HttpEntity<>(headers);

    result = testRestTemplate.exchange(testUrl, HttpMethod.GET, httpEntity, String.class);
    assertEquals("hello admin", result.getBody()); //access with session works worked

    jedis.flushAll(); //clear all keys in redis

    result = testRestTemplate.exchange(testUrl, HttpMethod.GET, httpEntity, String.class);
    assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    //access denied after sessions are removed in redis
}
```

首先，我们的测试使用管理员身份验证凭据确认我们的请求成功。

然后我们从响应标头中提取会话值，并在我们的第二个请求中将其用作我们的身份验证。我们对此进行验证，然后清除Redis中的所有数据。

最后，我们使用会话 cookie 发出另一个请求并确认我们已注销。这证实了Spring Session正在管理我们的会话。

## 七. 总结

Spring Session是一个用于管理 HTTP 会话的强大工具。通过将我们的会话存储简化为一个配置类和一些 Maven 依赖项，我们现在可以将多个应用程序连接到同一个Redis实例并共享身份验证信息。