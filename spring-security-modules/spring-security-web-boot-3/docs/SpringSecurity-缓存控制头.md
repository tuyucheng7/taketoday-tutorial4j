## 1. 概述

在本文中，我们将探讨如何使用Spring Security控制HTTP缓存。

我们将演示其默认行为，并解释其背后的原因。然后，我们将研究部分或完全改变这种行为的方法。

## 2. 默认缓存行为

通过有效地使用缓存控制头，我们可以指示浏览器缓存资源并避免网络跳跃。这减少了延迟，也减少了我们服务器上的负载。

默认情况下，Spring Security会为我们设置特定的缓存控制标头值，而无需我们进行任何配置。

首先，让我们为我们的应用程序设置Spring Security：

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity
public class SpringSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.build();
    }
}
```

我们只是简单的返回一个SecurityFilterChain bean，这意味着我们不需要进行身份验证即可访问端点，从而使我们能够专注于纯粹的测试缓存。

接下来，让我们实现一个简单的REST端点：

```java
@Controller
public class ResourceEndpoint {

    @GetMapping(value = "/default/users/{name}")
    public ResponseEntity<UserDto> getUserWithDefaultCaching(@PathVariable String name) {
        return ResponseEntity.ok(new UserDto(name));
    }
}

public class UserDto {
    public final String name;

    public UserDto(String name) {
        this.name = name;
    }
}
```

生成的cache-control标头将如下所示：

```text
[cache-control: no-cache, no-store, max-age=0, must-revalidate]
```

最后，让我们实现一个访问此端点的测试，并断言响应中发送了哪些标头：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = AppRunner.class)
class ResourceEndpointIntegrationTest {

    @LocalServerPort
    private int serverPort;

    @Test
    void whenGetRequestForUser_shouldRespondWithDefaultCacheHeaders() {
        given().when()
              .get(getBaseUrl() + "/default/users/Michael")
              .then()
              .headers("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate")
              .header("Pragma", "no-cache");
    }

    private String getBaseUrl() {
        return String.format("http://localhost:%d", serverPort);
    }
}
```

本质上，这意味着浏览器永远不会缓存此响应。

虽然这看起来效率低下，但这种默认行为实际上有一个很好的原因-**如果一个用户注销而另一个用户登录，我们不希望他们能够看到以前的用户资源**。默认情况下不缓存任何内容要安全得多，并且将缓存的启用交由我们负责。

## 3. 覆盖默认缓存行为

有时我们可能正在处理我们确实想要缓存的资源。如果我们要启用它，最安全的做法是在每个资源的基础上进行。这意味着默认情况下仍然不会缓存任何其他资源。

为此，让我们尝试使用CacheControl缓存在单个处理程序方法中重写缓存控制标头。[CacheControl](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/http/CacheControl.html)类是一个流式的构建器，它使我们可以轻松创建不同类型的缓存：

```java
@GetMapping("/users/{name}")
public ResponseEntity<UserDto> getUser(@PathVariable String name) {
    return ResponseEntity
        .ok()
        .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
        .body(new UserDto(name));
}
```

让我们在测试中访问这个端点，并断言我们已经更改了缓存控制头：

```java
@Test
void whenGetRequestForUser_shouldRespondMaxAgeCacheControl() {
    given().when()
        .get(getBaseUrl() + "/users/Michael")
        .then()
        .header("Cache-Control", "max-age=60");
}
```

如我们所见，已经覆盖了默认值，现在我们的响应将被浏览器缓存60秒。

## 4. 关闭默认缓存行为

我们也可以完全关闭Spring Security的默认缓存控制头。这是一件非常冒险的事情，并不推荐。但是，如果我们真的想要这样做，那么我们可以通过创建一个SecurityFilterChain bean来实现：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.headers().disable();
    return http.build();
}
```

现在，让我们再次向端点发出请求，看看我们得到什么响应：

```java
@Test
void whenGetRequestForUser_shouldRespondWithDefaultCacheHeaders() {
    given().when()
        .get(getBaseUrl() + "/default/users/Michael")
        .then()
        .headers(new HashMap<String, Object>());
}
```

正如我们所见，根本没有设置缓存标头。

## 5. 总结

本文演示了Spring Security默认情况下如何禁用HTTP缓存。我们还了解了如何在我们认为合适的情况下禁用或修改此行为。