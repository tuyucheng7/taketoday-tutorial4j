## 1. 概述

在本文中，我们将探讨[Spring Security 5](https://spring.io/projects/spring-security/)框架用于保护响应式应用程序的新特性。此版本与Spring 5和Spring Boot 2保持一致。

在本文中，我们不会详细介绍响应式应用程序本身，这是Spring 5框架的新特性。请务必查看文章[Reactor Core简介](https://www.baeldung.com/reactor-core)以了解更多详细信息。

## 2. Maven依赖

我们将使用Spring Boot启动器来引导我们的项目以及所有必需的依赖项。

基本设置需要父声明、Web启动器和Security启动器依赖项。我们还需要Spring Security测试框架：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.6.1</version>
    <relativePath/>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

我们可以在[Maven Central](https://search.maven.org/search?q=a:spring-boot-starter-security)上查看Spring Boot Security启动器的当前版本。

## 3. 项目构建

### 3.1 启动响应式应用程序

我们不会使用标准的@SpringBootApplication配置，而是配置一个基于Netty的Web服务器。**Netty是一个基于NIO的异步框架，是响应式应用程序的核心基础**。

@EnableWebFlux注解为应用程序启用了标准的Spring Web Reactive配置：

```java
@ComponentScan(basePackages = {"cn.tuyucheng.taketoday.reactive.security"})
@EnableWebFlux
public class SpringSecurity5Application {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringSecurity5Application.class)) {
            context.getBean(DisposableServer.class).onDispose().block();
        }
    }
}
```

在这里，我们创建一个新的Spring应用程序上下文，并通过在Netty上下文上调用.onClose().block()方法链来等待Netty关闭。

Netty关闭后，Spring上下文会由try-with-resources块自动关闭。

我们还需要创建一个基于Netty的HTTP服务器、一个HTTP请求处理程序以及服务器和处理程序之间的适配器：

```java
@Bean
public DisposableServer disposableServer(ApplicationContext context) {
    HttpHandler handler = WebHttpHandlerBuilder.applicationContext(context).build();
    ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(handler);
    HttpServer httpServer = HttpServer.create().host("localhost").port(8083);
    return httpServer.handle(adapter).bindNow();
}
```

### 3.2 Spring Security配置类

对于我们基本的Spring Security配置，我们将创建一个配置类SecurityConfig。

要在Spring Security 5中启用WebFlux支持，我们只需要指定@EnableWebFluxSecurity注解：

```java
@EnableWebFluxSecurity
public class SecurityConfig {
    // ...
}
```

**现在我们可以利用ServerHttpSecurity类来构建我们的安全配置**。

**这个类是Spring 5的一个新特性**。它类似于HttpSecurity构建器，但仅适用于WebFlux应用程序。

ServerHttpSecurity已经预先配置了一些合理的默认值，因此我们可以完全跳过这个配置。但对于初学者而言，我们将提供以下最小配置：

```java
@Bean
public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http.authorizeExchange()
        .anyExchange().authenticated()
        .and().build();
}
```

此外，我们还需要一个UserDetailsService。Spring Security为我们提供了一个方便的模拟User构建器和UserDetailsService的内存中实现：

```java
@Bean
public MapReactiveUserDetailsService userDetailsService() {
    UserDetails user = User
        .withUsername("user")
        .password(passwordEncoder().encode("password"))
        .roles("USER")
        .build();

    return new MapReactiveUserDetailsService(user);
}
```

由于我们使用响应式框架，因此UserDetailsService也应该是响应式的。**如果我们查看ReactiveUserDetailsService接口，我们会看到它的findByUsername方法实际上返回了一个Mono发布者**：

```java
public interface ReactiveUserDetailsService {

    Mono<UserDetails> findByUsername(String username);
}
```

现在我们可以运行我们的应用程序并观察一个常规的HTTP基本身份验证表单。

## 4. 登录表单样式

Spring Security 5中一个小而显著的改进是使用Bootstrap 4 CSS框架的新样式登录表单。登录表单中的样式表链接到CDN，因此我们只能在连接到网络时才能看到此改进。

要使用新的登录表单，让我们将相应的formLogin()构建器方法添加到ServerHttpSecurity构建器中：

```java
@Bean
public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http.authorizeExchange()
        .pathMatchers("/admin").hasAuthority("ROLE_ADMIN")
        .anyExchange().authenticated()
        .and().formLogin()
        .and().csrf().disable()
        .build();
}
```

如果我们现在访问应用程序的主页，我们会看到它看起来比以前版本的Spring Security使用的默认表单要好得多：

<img src="../assets/img.png">

**请注意，这不是一个生产就绪的表单，但它是我们应用程序的一个很好的起步程序**。

如果我们现在登录，然后访问[http://localhost:8083/logout](http://localhost:8083/logout) URL，我们可以看到注销确认表单，该表单也是经过样式化的。

<img src="../assets/img_1.png">

## 5. 响应式端点安全性

为了看到身份验证表单背后的内容，让我们实现一个简单的响应式控制器来迎接用户：

```java
@RestController
public class GreetingController {

    @GetMapping("/")
    public Mono<String> greet(Mono<Principal> principal) {
        return principal
              .map(Principal::getName)
              .map(name -> String.format("Hello, %s", name));
    }
}
```

登录后，我们可以看到返回的消息。让我们添加另一个只能由管理员访问的响应式处理程序：

```java
@GetMapping("/admin")
public Mono<String> greetAdmin(Mono<Principal> principal) {
    return principal
        .map(Principal::getName)
        .map(name -> String.format("Admin access: %s", name));
}
```

现在让我们在我们的UserDetailsService中创建一个角色为ADMIN的第二个用户：

```java
UserDetails admin = User
    .withUsername("admin")
    .password(passwordEncoder().encode("password"))
    .roles("ADMIN")
    .build();
```

现在，我们可以为“/admin” URL添加一个匹配器规则，该规则要求用户具有ROLE_ADMIN权限。

**请注意，我们必须将匹配器放在.anyExchange()方法链调用之前**。此调用适用于其他匹配器尚未覆盖的所有其他URL：

```java
@Bean
public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http.authorizeExchange()
        .pathMatchers("/admin").hasAuthority("ROLE_ADMIN")
        .anyExchange().authenticated()
        .and().formLogin()
        .and().build();
}
```

如果我们现在使用user或admin登录，我们可以看到他们都能成功访问主页，因为我们已经让所有经过身份验证的用户都可以访问它。

**但只有admin用户可以访问**[http://localhost:8083/admin](http://localhost:8080/admin) **URL。**

## 6. 响应式方法安全性

我们已经了解了如何保护URL，但是方法呢？

要为响应式应用启用基于方法的Spring Security，我们只需要将@EnableReactiveMethodSecurity注解添加到我们的SecurityConfig类：

```java
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {
    // ...
}
```

现在让我们创建一个包含以下内容的响应式GreetingService：

```java
@Service
public class GreetingService {

    public Mono<String> greet() {
        return Mono.just("Hello from service!");
    }
}
```

我们可以将其注入到控制器中，并添加一个端点调用该Service，然后访问[http://localhost:8080/greetingService](http://localhost:8080/greetingService)看看它是否确实有效：

```java
@RestController
public class GreetingController {
    private final GreetingService greetingService;

    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping("/greetingService")
    public Mono<String> greetingService() {
        return greetingService.greet();
    }
}
```

但是，如果我们现在将@PreAuthorize("hasRole('ADMIN')")注解添加到Service方法上，那么普通用户将无法访问greetingService URL：

```java
@Service
public class GreetingService {

    @PreAuthorize("hasRole('ADMIN')")
    public Mono<String> greet() {
        // ...
    }
}
```

## 7. 在测试中Mock用户

让我们看看如何为我们的响应式Spring应用程序编写测试。

首先，我们将使用注入的应用程序上下文创建一个测试类：

```java
@ContextConfiguration(classes = SpringSecurity5Application.class)
public class SecurityTest {
    @Autowired
    ApplicationContext context;

    // ...
}
```

现在我们将设置一个简单的响应式WebTestClient，这是Spring 5测试框架的一个特性：

```java
private WebTestClient webTestClient;

@BeforeEach
void setUp() {
    webTestClient = WebTestClient
        .bindToApplicationContext(context)
        .configureClient()
        .build();
}
```

下面是一个测试用例，当我们没有经过登录访问主页面时，我们应该重定向到登录页面：

```java
@Test
void whenNoCredentials_thenRedirectToLogin() {
    webTestClient.get()
        .uri("/")
        .exchange()
        .expectStatus().is3xxRedirection();
}
```

**如果我们现在将@WithMockUser注解添加到测试方法上，我们可以为该方法提供一个经过身份验证的用户**。

该用户的登录名和密码分别为user和password，角色为USER。当然，这些都可以使用@WithMockUser注解的参数进行配置。

现在我们可以检查经过授权的用户是否能得到正确的响应消息：

```java
@Test
@WithMockUser
void whenHasCredentials_thenSeesGreeting() {
    webTestClient.get()
        .uri("/")
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class).isEqualTo("Hello, user");
}
```

@WithMockUser注解从Spring Security 4开始可用。但是，这在Spring Security 5中也进行了更新，以涵盖响应式端点和方法。

## 8. 总结

在本教程中，我们介绍了Spring Security 5版本的新特性，尤其是在响应式编程领域。