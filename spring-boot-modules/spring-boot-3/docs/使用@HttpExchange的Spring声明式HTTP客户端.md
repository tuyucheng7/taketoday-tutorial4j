## 1. 概述

**声明性HTTP接口是一个Java接口，它有助于减少样板代码，生成实现此接口的代理，并在框架级别执行交换**。

例如，如果我们想使用URL [https://server-address.com/api/resource/id](https://server-address.com/api/resource/id)上的API，那么我们需要创建和配置[RestTemplate](https://howtodoinjava.com/spring-boot2/resttemplate/spring-restful-client-resttemplate-example/)或[WebClient](https://howtodoinjava.com/spring-webflux/webclient-get-post-example/) bean，并使用其交换方法来调用API，解析响应和处理错误。

大多数情况下，创建和配置bean以及调用远程API的代码非常相似，因此可以由框架抽象出来，因此我们不需要在每个应用程序中都重复编写这些代码。我们可以简单地使用接口上的注解来表达远程API的详细信息，并让框架在后台创建一个实现。

例如，如果我们想要使用HTTP GET /users API，那么我们可以简单地写：

```java
public interface UserClient {
    @GetExchange("/users")
    Flux<User> getAll();
}
```

Spring会在运行时提供接口和exchange的实现，我们只需要调用getAll()方法即可。

```java
@Autowired
UserClient userClient;

userClient.getAll().subscribe(
    data -> log.info("User: {}", data)
);
```

## 2. Maven依赖项

声明式HTTP接口功能是[spring-web](https://mvnrepository.com/artifact/org.springframework/spring-web)依赖项的一部分，当我们包含[spring-boot-starter-web](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-web/3.0.4)或[spring-boot-starter-webflux](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-webflux/3.0.4)时，它会被传递依赖进来。如果我们想添加响应式支持，则使用后面的依赖项。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

## 3. 创建HTTP服务接口

**在Spring中，HTTP服务接口是具有@HttpExchange方法的Java接口**。带注解的方法被视为HTTP端点，细节通过注解属性和输入方法参数类型静态定义。

### 3.1 交换方法

我们可以使用以下注解将方法标记为HTTP服务端点：

-   **@HttpExchange**：是用于指定HTTP端点的通用注解。在接口级别使用时，它适用于所有方法
-   **@GetExchange**：指定HTTP GET请求的@HttpExchange
-   **@PostExchange**：指定HTTP POST请求的@HttpExchange
-   **@PutExchange**：指定HTTP PUT请求的@HttpExchange
-   **@DeleteExchange**：指定HTTP DELETE请求的@HttpExchange
-   **@PatchExchange**：指定HTTP PATCH请求的@HttpExchange

### 3.2 方法参数

交换方法在方法签名中支持以下方法参数：

-   URI：设置请求的URL
-   @PathVariable：将请求URL中的值替换为占位符
-   @RequestBody：提供请求的正文
-   @RequestParam：添加请求参数。当“content-type”设置为“application/x-www-form-urlencoded”时，请求参数在请求正文中进行编码。否则，它们将作为URL查询参数添加
-   @RequestHeader：添加请求标头名称和值
-   @RequestPart：可用于添加请求部分(表单字段、资源或HttpEntity等)
-   @CookieValue：将cookie添加到请求中

```java
@PutExchange
void update(@PathVariable Long id, @RequestBody User user);
```

### 3.3 返回值

HTTP交换方法可以返回以下值：

-   阻塞或反应(Mono/Flux)
-   只有特定的响应信息，例如状态代码和/或响应标头
-   如果该方法被视为仅执行，则为void

对于阻塞交换方法，我们通常应该返回ResponseEntity，对于响应式方法，我们可以返回Mono/Flux类型。

```java
// Blocking
@GetExchange("/{id}")
User getById(...);

// Reactive
@GetExchange("/{id}")
Mono<User> getById(...);
```

## 4. 构建HttpServiceProxyFactory

HttpServiceProxyFactory是一个从HTTP服务接口创建客户端代理的工厂。使用其HttpServiceProxyFactory.builder(client).build()方法获取代理bean的实例。

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.tuyucheng.taketoday.declarativehttpclient.web.UserClient;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebConfig {
    
    @Bean
    WebClient webClient(ObjectMapper objectMapper) {
        return WebClient.builder()
              .baseUrl("https://jsonplaceholder.typicode.com/")
              .build();
    }
    
    @SneakyThrows
    @Bean
    UserClient postClient(WebClient webClient) {
        HttpServiceProxyFactory httpServiceProxyFactory =
              HttpServiceProxyFactory.builder(WebClientAdapter.forClient(webClient))
                    .build();
        return httpServiceProxyFactory.createClient(UserClient.class);
    }
}
```

请注意，我们已经在WebClient bean中设置了远程API的基本URL，因此我们只需要在交换方法中使用相对路径。

## 5. HTTP服务接口示例

以下是与[https://jsonplaceholder.typicode.com/users/](https://jsonplaceholder.typicode.com/users/)端点交互并执行各种操作的HTTP接口示例。

```java
import cn.tuyucheng.taketoday.declarativehttpclient.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@HttpExchange(url = "/users", accept = "application/json", contentType = "application/json")
public interface UserClient {

    @GetExchange("/")
    Flux<User> getAll();

    @GetExchange("/{id}")
    Mono<User> getById(@PathVariable("id") Long id);

    @PostExchange("/")
    Mono<ResponseEntity<Void>> save(@RequestBody User user);

    @PutExchange("/{id}")
    Mono<ResponseEntity<Void>> update(@PathVariable Long id, @RequestBody User user);

    @DeleteExchange("/{id}")
    Mono<ResponseEntity<Void>> delete(@PathVariable Long id);
}
```

请注意，我们创建了一个User类型的记录来保存用户信息。

```java
public record User(Long id, String name, String username, String email) {
}
```

现在我们可以将UserClient bean注入应用程序类并调用方法来获取API响应。

```java
@Autowired
UserClient userClient;

//Get All Users
userClient.getAll().subscribe(
    data -> log.info("User: {}", data)
);

//Get User By Id
userClient.getById(1L).subscribe(
    data -> log.info("User: {}", data)
);

//Create a New User
userClient.save(new User(null, "Lokesh", "lokesh", "admin@email.com"))
    .subscribe(
        data -> log.info("User: {}", data)
    );

//Delete User By Id
userClient.delete(1L).subscribe(
    data -> log.info("User: {}", data)
);
```

## 6. 总结

在这个Spring教程中，我们通过示例学习了创建和使用声明式HTTP客户端接口。我们学习了在接口中创建交换方法，然后使用Spring框架创建的代理实现调用它们。我们还学习了使用HttpServiceProxyFactory和WebClient bean创建接口代理。