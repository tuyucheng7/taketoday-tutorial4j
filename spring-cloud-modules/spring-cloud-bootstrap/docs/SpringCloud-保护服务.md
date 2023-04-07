## 1. 概述

在上一篇文章[Spring Cloud–Bootstrapping](https://www.baeldung.com/spring-cloud-bootstrapping)中，我们构建了一个基本的Spring Cloud应用程序。本文介绍如何保护它。

我们自然会使用Spring Security来使用Spring Session和Redis共享会话。这种方法设置简单，易于扩展到许多业务场景。如果你不熟悉Spring Session，请查看[这篇](https://www.baeldung.com/spring-session)文章。

**共享会话使我们能够在我们的网关服务中记录用户并将该身份验证传播到我们系统的任何其他服务**。

如果你不熟悉Redis或Spring Security，此时最好快速回顾一下这些主题。虽然本文的大部分内容都是为应用程序准备的复制粘贴，但没有其他方法可以替代理解幕后发生的事情。

有关Redis的介绍，请阅读[本教程](https://www.baeldung.com/spring-data-redis-tutorial)。有关Spring Security的介绍，请阅读[spring-security-login](https://www.baeldung.com/spring-security-login)、[role-and-privilege-for-spring-security-registration](https://www.baeldung.com/role-and-privilege-for-spring-security-registration)和[spring-security-session](https://www.baeldung.com/spring-security-session)。要全面了解SpringSecurity，请查看[learn-spring-security-the-master-class](http://courses.baeldung.com/p/learn-spring-security-the-master-class)。

## 2. Maven设置

让我们首先为系统中的每个模块添加[spring-boot-starter-security](https://search.maven.org/search?q=a:spring-boot-starter-security)依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

因为我们使用Spring依赖管理，所以我们可以省略spring-boot-starter依赖的版本。

第二步，让我们使用[spring-session](https://search.maven.org/search?q=a:spring-session)、[spring-boot-starter-data-redis](https://search.maven.org/search?q=a:spring-boot-starter-data-redis)依赖项修改每个应用程序的pom.xml：

```xml
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

我们的应用程序中只有四个将绑定到Spring Session中：**discovery、gateway、book-service和rating-service**。

接下来，在与主应用程序文件相同的目录中的所有三个服务中添加一个会话配置类：

```java
@EnableRedisHttpSession
public class SessionConfig extends AbstractHttpSessionApplicationInitializer {
}
```

最后，将这些属性添加到我们的git仓库中的三个*.properties文件中：

```properties
spring.redis.host=localhost
spring.redis.port=6379
```

现在让我们进入特定于服务的配置。

## 3. 保护配置服务

配置服务包含通常与数据库连接和API密钥相关的敏感信息。我们不能泄露这些信息，所以让我们直接进入并保护此服务。

让我们将安全属性添加到配置服务的src/main/resources中的application.properties文件：

```properties
eureka.client.serviceUrl.defaultZone=http://discUser:discPassword@localhost:8082/eureka/
security.user.name=configUser
security.user.password=configPassword
security.user.role=SYSTEM
```

这将设置我们的服务以使用发现登录。此外，我们正在使用application.properties文件配置我们的安全性。

现在让我们配置我们的发现服务。

## 4. 保护发现服务

我们的发现服务保存有关应用程序中所有服务位置的敏感信息。它还会注册这些服务的新实例。

如果恶意客户端获得访问权限，他们将了解我们系统中所有服务的网络位置，并能够将他们自己的恶意服务注册到我们的应用程序中。发现服务的安全至关重要。

### 4.1 安全配置

让我们添加一个安全过滤器来保护其他服务将使用的端点：

```java
@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.inMemoryAuthentication().withUser("discUser")
              .password("discPassword").roles("SYSTEM");
    }

    @Override
    protected void configure(HttpSecurity http) {
        http.sessionManagement()
              .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
              .and().requestMatchers().antMatchers("/eureka/**")
              .and().authorizeRequests().antMatchers("/eureka/**")
              .hasRole("SYSTEM").anyRequest().denyAll().and()
              .httpBasic().and().csrf().disable();
    }
}
```

这将使用“SYSTEM”用户设置我们的服务。这是一个基本的Spring Security配置，有一些变化。让我们来看看这些变化：

- @Order(1)：告诉Spring首先注入这个安全过滤器，以便它在任何其他过滤器之前被访问
- .sessionCreationPolicy：告诉Spring在用户登录此过滤器时始终创建会话
- .requestMatchers：限制此过滤器适用的端点

我们刚刚设置的安全过滤器配置了一个仅适用于发现服务的隔离身份验证环境。

### 4.2 保护Eureka仪表板

由于我们的发现应用程序有一个很好的UI来查看当前注册的服务，因此让我们使用第二个安全过滤器公开它，并将这个过滤器绑定到我们应用程序其余部分的身份验证中。请记住，没有@Order()标记意味着这是要评估的最后一个安全过滤器：

```java
@Configuration
public static class AdminSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
              .and().httpBasic().disable().authorizeRequests()
              .antMatchers(HttpMethod.GET, "/").hasRole("ADMIN")
              .antMatchers("/info", "/health").authenticated().anyRequest()
              .denyAll().and().csrf().disable();
    }
}
```

在SecurityConfig类中添加此配置类。这将创建第二个安全过滤器来控制对我们UI的访问。这个过滤器有一些不寻常的特征，让我们看看这些：

- httpBasic().disable()：告诉Spring Security禁用此过滤器的所有身份验证过程
- sessionCreationPolicy：我们将其设置为NEVER以指示我们要求用户在访问受此过滤器保护的资源之前已经通过身份验证

此过滤器永远不会设置用户会话并依赖Redis来填充共享安全上下文。因此，它依赖于另一个服务(网关)来提供身份验证。

### 4.3 使用配置服务进行身份验证

在发现服务中，让我们将两个属性附加到src/main/resources中的bootstrap.properties：

```properties
spring.cloud.config.username=configUser
spring.cloud.config.password=configPassword
```

这些属性将允许发现服务在启动时通过配置服务进行身份验证。

让我们更新Git仓库中的discovery.properties

```properties
eureka.client.serviceUrl.defaultZone=http://discUser:discPassword@localhost:8082/eureka/
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

我们已将基本身份验证凭据添加到我们的发现服务，以允许它与配置服务进行通信。此外，我们通过告诉我们的服务不要自行注册，将Eureka配置为以独立模式运行。

让我们将文件提交到git仓库。否则，将无法检测到更改。

## 5. 保护网关服务

我们的网关服务是我们想要向世界公开的应用程序的唯一部分。因此，它需要安全性来确保只有经过身份验证的用户才能访问敏感信息。

### 5.1 安全配置

让我们像我们的发现服务一样创建一个SecurityConfig类，并用以下内容重写方法：

```java
@Autowired
public void configureGlobal(AuthenticationManagerBuilder auth) {
    auth.inMemoryAuthentication().withUser("user").password("password")
        .roles("USER").and().withUser("admin").password("admin")
        .roles("ADMIN");
}

@Override
protected void configure(HttpSecurity http) {
    http.authorizeRequests().antMatchers("/book-service/books")
        .permitAll().antMatchers("/eureka/**").hasRole("ADMIN")
        .anyRequest().authenticated().and().formLogin().and()
        .logout().permitAll().logoutSuccessUrl("/book-service/books")
        .permitAll().and().csrf().disable();
}
```

这个配置非常简单。我们声明了一个带有表单登录的安全过滤器，可以保护各种端点。

/eureka/**上的安全性是为了保护我们将从Eureka状态页面的网关服务提供的一些静态资源。如果你使用本文构建项目，请将[Github](https://github.com/eugenp/tutorials/tree/master/spring-cloud-modules/spring-cloud-bootstrap)上的网关项目中的resource/static文件夹复制到你的项目中。

现在我们修改配置类上的@EnableRedisHttpSession注解：

```java
@EnableRedisHttpSession(redisFlushMode = RedisFlushMode.IMMEDIATE)
```

我们将刷新模式设置为IMMEDIATE以立即保留会话中的任何更改。这有助于为重定向准备身份验证令牌。

最后，让我们添加一个ZuulFilter，它将在登录后转发我们的身份验证令牌：

```java
@Component
public class SessionSavingZuulPreFilter extends ZuulFilter {

    @Autowired
    private SessionRepository repository;

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpSession httpSession = context.getRequest().getSession();
        Session session = repository.getSession(httpSession.getId());

        context.addZuulRequestHeader("Cookie", "SESSION=" + httpSession.getId());
        return null;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }
}
```

此过滤器将在登录后重定向时抓取请求，并将会话密钥作为cookie添加到标头中。这将在登录后将身份验证传播到任何支持服务。

### 5.2 使用配置和发现服务进行身份验证

让我们将以下身份验证属性添加到网关服务的src/main/resources中的bootstrap.properties文件：

```properties
spring.cloud.config.username=configUser
spring.cloud.config.password=configPassword
eureka.client.serviceUrl.defaultZone=http://discUser:discPassword@localhost:8082/eureka/
```

接下来，让我们更新Git仓库中的gateway.properties

```properties
management.security.sessions=always

zuul.routes.book-service.path=/book-service/**
zuul.routes.book-service.sensitive-headers=Set-Cookie,Authorization
hystrix.command.book-service.execution.isolation.thread.timeoutInMilliseconds=600000

zuul.routes.rating-service.path=/rating-service/**
zuul.routes.rating-service.sensitive-headers=Set-Cookie,Authorization
hystrix.command.rating-service.execution.isolation.thread.timeoutInMilliseconds=600000

zuul.routes.discovery.path=/discovery/**
zuul.routes.discovery.sensitive-headers=Set-Cookie,Authorization
zuul.routes.discovery.url=http://localhost:8082
hystrix.command.discovery.execution.isolation.thread.timeoutInMilliseconds=600000
```

我们添加了会话管理以始终生成会话，因为我们只有一个安全过滤器，我们可以在属性文件中设置它。接下来，我们添加Redis主机和服务器属性。

此外，我们添加了一条路由，用于将请求重定向到我们的发现服务。由于独立的发现服务不会自行注册，因此我们必须使用URL方案定位该服务。

我们可以从配置git仓库的gateway.properties文件中删除serviceUrl.defaultZone属性。该值在引导程序文件中重复。

让我们将文件提交到Git仓库，否则，将无法检测到更改。

## 6. 保护图书服务

图书服务将保存由各种用户控制的敏感信息。此服务必须受到保护，以防止我们系统中受保护信息的泄露。

### 6.1 安全配置

为了保护我们的图书服务，我们将从网关复制SecurityConfig类并用以下内容重写该方法：

```java
@Override
protected void configure(HttpSecurity http) {
    http.httpBasic().disable().authorizeRequests()
        .antMatchers("/books").permitAll()
        .antMatchers("/books/*").hasAnyRole("USER", "ADMIN")
        .authenticated().and().csrf().disable();
}
```

### 6.2 属性

将这些属性添加到图书服务的src/main/resources中的bootstrap.properties文件中：

```properties
spring.cloud.config.username=configUser
spring.cloud.config.password=configPassword
eureka.client.serviceUrl.defaultZone=http://discUser:discPassword@localhost:8082/eureka/
```

让我们将属性添加到git仓库中的book-service.properties文件：

```properties
management.security.sessions=never
```

我们可以从配置git仓库中的book-service.properties文件中删除serviceUrl.defaultZone属性。该值在引导程序文件中重复。

请记住提交这些更改，以便图书服务接收它们。

## 7. 保护评级服务

评级服务也需要得到保护。

### 7.1 安全配置

为了保护我们的评级服务，我们将从网关复制SecurityConfig类并用以下内容重写该方法：

```java
@Override
protected void configure(HttpSecurity http) {
    http.httpBasic().disable().authorizeRequests()
        .antMatchers("/ratings").hasRole("USER")
        .antMatchers("/ratings/all").hasAnyRole("USER", "ADMIN").anyRequest()
        .authenticated().and().csrf().disable();
}
```

我们可以从网关服务中删除configureGlobal()方法。

### 7.2 属性

将这些属性添加到评级服务的src/main/resources中的bootstrap.properties文件中：

```properties
spring.cloud.config.username=configUser
spring.cloud.config.password=configPassword
eureka.client.serviceUrl.defaultZone=http://discUser:discPassword@localhost:8082/eureka/
```

让我们将属性添加到我们的git仓库中的rating-service.properties文件中：

```properties
management.security.sessions=never
```

我们可以从配置git仓库中的rating-service.properties文件中删除serviceUrl.defaultZone属性。该值在引导程序文件中重复。

请记住提交这些更改，以便评级服务将接收它们。

## 8. 运行和测试

启动Redis和应用程序的所有服务：config、discovery、gateway、book-service和rating-service。现在让我们测试一下！

首先，让我们在我们的网关项目中创建一个测试类，并为我们的测试创建一个方法：

```java
public class GatewayApplicationLiveTest {
    @Test
    public void testAccess() {
        // ...
    }
}
```

接下来，让我们设置我们的测试，并通过在我们的测试方法中添加以下代码片段来验证我们是否可以访问我们未受保护的/book-service/books资源：

```java
TestRestTemplate testRestTemplate = new TestRestTemplate();
String testUrl = "http://localhost:8080";

ResponseEntity<String> response = testRestTemplate
    .getForEntity(testUrl + "/book-service/books", String.class);
Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
Assert.assertNotNull(response.getBody());
```

运行此测试并验证结果。如果我们看到失败，则确认整个应用程序已成功启动并且配置已从我们的配置git仓库加载。

现在让我们通过将此代码附加到测试方法的末尾来测试我们的用户在作为未授权用户访问受保护资源时将被重定向以登录：

```java
response = testRestTemplate
    .getForEntity(testUrl + "/home/index.html", String.class);
Assert.assertEquals(HttpStatus.FOUND, response.getStatusCode());
Assert.assertEquals("http://localhost:8080/login", response.getHeaders()
    .get("Location").get(0));
```

再次运行测试，确认成功。

接下来，让我们实际登录，然后使用我们的会话访问用户保护的结果：

```java
MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
form.add("username", "user");
form.add("password", "password");
response = testRestTemplate
    .postForEntity(testUrl + "/login", form, String.class);
```

现在，让我们从cookie中提取会话并将其传播到以下请求：

```java
String sessionCookie = response.getHeaders().get("Set-Cookie")
    .get(0).split(";")[0];
HttpHeaders headers = new HttpHeaders();
headers.add("Cookie", sessionCookie);
HttpEntity<String> httpEntity = new HttpEntity<>(headers);

```

并请求受保护的资源：

```java
response = testRestTemplate.exchange(testUrl + "/book-service/books/1",
    HttpMethod.GET, httpEntity, String.class);
Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
Assert.assertNotNull(response.getBody());
```

再次运行测试以确认结果。

现在，让我们尝试使用相同的会话访问管理部分：

```java
response = testRestTemplate.exchange(testUrl + "/rating-service/ratings/all",
    HttpMethod.GET, httpEntity, String.class);
Assert.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
```

再次运行测试，正如预期的那样，我们被限制以普通老用户身份访问管理区域。

下一个测试将验证我们是否可以以管理员身份登录并访问受管理员保护的资源：

```java
form.clear();
form.add("username", "admin");
form.add("password", "admin");
response = testRestTemplate
    .postForEntity(testUrl + "/login", form, String.class);

sessionCookie = response.getHeaders().get("Set-Cookie").get(0).split(";")[0];
headers = new HttpHeaders();
headers.add("Cookie", sessionCookie);
httpEntity = new HttpEntity<>(headers);

response = testRestTemplate.exchange(testUrl + "/rating-service/ratings/all",
    HttpMethod.GET, httpEntity, String.class);
Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
Assert.assertNotNull(response.getBody());
```

我们的考验越来越大了！但是我们可以看到，当我们运行它时，通过以管理员身份登录，我们获得了对管理资源的访问权限。

我们的最终测试是通过我们的网关访问我们的发现服务器。为此，将此代码添加到我们测试的末尾：

```java
response = testRestTemplate.exchange(testUrl + "/discovery",
    HttpMethod.GET, httpEntity, String.class);
Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
```

最后一次运行此测试以确认一切正常。成功！！！

你错过了吗？因为我们登录了我们的网关服务并查看了我们的图书、评级和发现服务上的内容，而无需登录四个单独的服务器！

通过利用Spring Session在服务器之间传播我们的身份验证对象，我们能够在网关上登录一次并使用该身份验证访问任意数量的支持服务上的控制器。

## 9. 总结

云中的安全性肯定会变得更加复杂。但是在Spring Security和Spring Session的帮助下，我们可以轻松解决这个关键问题。

我们现在有一个围绕我们的服务提供安全性的云应用程序。使用Zuul和Spring Session，我们可以让用户只登录一个服务，并将该身份验证传播到我们的整个应用程序。这意味着我们可以轻松地将我们的应用程序分成适当的域，并按照我们认为合适的方式保护它们中的每一个。