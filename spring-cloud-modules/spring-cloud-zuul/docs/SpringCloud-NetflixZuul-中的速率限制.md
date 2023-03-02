## 1. 简介

[Spring Cloud Netflix Zuul](https://github.com/spring-cloud/spring-cloud-netflix)是一个封装了[Netflix Zuul](https://github.com/Netflix/zuul)的开源网关。它为Spring Boot应用程序添加了一些特定功能。不幸的是，没有提供开箱即用的速率限制。

**在本教程中，我们将探索[Spring Cloud Zuul RateLimit](https://github.com/marcosbarbero/spring-cloud-zuul-ratelimit)，它添加了对速率限制请求的支持**。

## 2. Maven配置

除了Spring Cloud Netflix Zuul依赖项之外，我们还需要将[Spring Cloud Zuul RateLimit](https://mvnrepository.com/artifact/com.marcosbarbero.cloud/spring-cloud-zuul-ratelimit)添加到我们应用程序的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
</dependency>
<dependency>
    <groupId>com.marcosbarbero.cloud</groupId>
    <artifactId>spring-cloud-zuul-ratelimit</artifactId>
    <version>2.2.0.RELEASE</version>
</dependency>
```

## 3. 控制器示例

首先，让我们创建几个REST端点，我们将在其上应用速率限制。

下面是一个带有两个端点的简单Spring Controller类：

```java
@Controller
@RequestMapping("/greeting")
public class GreetingController {

	@GetMapping("/simple")
	public ResponseEntity<String> getSimple() {
		return ResponseEntity.ok("Hi!");
	}

	@GetMapping("/advanced")
	public ResponseEntity<String> getAdvanced() {
		return ResponseEntity.ok("Hello, how you doing?");
	}
}
```

正如我们所看到的，没有特定于速率限制端点的代码。这是因为我们将在application.yml文件中的Zuul属性中对其进行配置。因此，保持我们的代码解耦。

## 4. Zuul属性

其次，让我们在application.yml文件中添加以下Zuul属性：

```yaml
zuul:
    routes:
        serviceSimple:
            path: /greeting/simple
            url: forward:/
        serviceAdvanced:
            path: /greeting/advanced
            url: forward:/
    ratelimit:
        enabled: true
        repository: JPA
        policy-list:
            serviceSimple:
                - limit: 5
                  refresh-interval: 60
                  type:
                      - origin
            serviceAdvanced:
                - limit: 1
                  refresh-interval: 2
                  type:
                      - origin
    strip-prefix: true
```

在zuul.routes下，我们提供l端点详细信息。在zuul.ratelimit.policy-list下，我们为端点提供速率限制配置。limit属性指定在refresh-interval内可以调用端点的次数。

如我们所见，我们为serviceSimple端点添加了每60秒5个请求的速率限制。相反，serviceAdvanced的速率限制为每2秒1个请求。

type配置指定我们要遵循的速率限制方法。以下是可能的值：

-   origin：基于用户源请求的速率限制
-   url：基于下游服务请求路径的速率限制
-   user：基于经过身份验证的用户名或“anonymous”的速率限制
-   no-value：充当每个服务的全局配置。要使用这种方法请不要设置参数“type”

## 5. 测试速率限制

### 5.1 在速率限制内请求

接下来，让我们测试速率限制：

```java
@Test
public void whenRequestNotExceedingCapacity_thenReturnOkResponse() {
    ResponseEntity<String> response = restTemplate.getForEntity(SIMPLE_GREETING, String.class);
    assertEquals(OK, response.getStatusCode());

    HttpHeaders headers = response.getHeaders();
    String key = "rate-limit-application_serviceSimple_127.0.0.1";

    assertEquals("5", headers.getFirst(HEADER_LIMIT + key));
    assertEquals("4", headers.getFirst(HEADER_REMAINING + key));
    assertThat(
      	parseInt(headers.getFirst(HEADER_RESET + key)),
      	is(both(greaterThanOrEqualTo(0)).and(lessThanOrEqualTo(60000)))
    );
}
```

在这里，我们对端点/greeting/simple进行了一次调用。请求成功，因为它在速率限制内。

另一个关键点是，**对于每个响应，我们都会返回标头，为我们提供有关速率限制的更多信息**。对于上述请求，我们将得到以下标头：

```shell
X-RateLimit-Limit-rate-limit-application_serviceSimple_127.0.0.1: 5
X-RateLimit-Remaining-rate-limit-application_serviceSimple_127.0.0.1: 4
X-RateLimit-Reset-rate-limit-application_serviceSimple_127.0.0.1: 60000
```

换句话说：

-   X-RateLimit-Limit-[key]：为端点配置的限制
-   X-RateLimit-Remaining-[key]：调用端点的剩余尝试次数
-   X-RateLimit-Reset-[key]：端点配置的refresh-interval的剩余毫秒数

此外，如果我们立即再次触发同一个端点，我们可以得到：

```shell
X-RateLimit-Limit-rate-limit-application_serviceSimple_127.0.0.1: 5
X-RateLimit-Remaining-rate-limit-application_serviceSimple_127.0.0.1: 3
X-RateLimit-Reset-rate-limit-application_serviceSimple_127.0.0.1: 57031
```

**请注意剩余尝试次数和剩余毫秒数的减少**。

### 5.2 超出速率限制的请求

让我们看看当我们超过速率限制时会发生什么：

```java
@Test
public void whenRequestExceedingCapacity_thenReturnTooManyRequestsResponse() throws InterruptedException {
    ResponseEntity<String> response = this.restTemplate.getForEntity(ADVANCED_GREETING, String.class);
    assertEquals(OK, response.getStatusCode());
    
    for (int i = 0; i < 2; i++) {
        response = this.restTemplate.getForEntity(ADVANCED_GREETING, String.class);
    }

    assertEquals(TOO_MANY_REQUESTS, response.getStatusCode());

    HttpHeaders headers = response.getHeaders();
    String key = "rate-limit-application_serviceAdvanced_127.0.0.1";

    assertEquals("1", headers.getFirst(HEADER_LIMIT + key));
    assertEquals("0", headers.getFirst(HEADER_REMAINING + key));
    assertNotEquals("2000", headers.getFirst(HEADER_RESET + key));

    TimeUnit.SECONDS.sleep(2);

    response = this.restTemplate.getForEntity(ADVANCED_GREETING, String.class);
    assertEquals(OK, response.getStatusCode());
}
```

在这里，我们快速连续两次调用端点/greeting/advanced。由于我们将速率限制配置为每2秒一个请求，因此**第二次调用将失败**。结果，错误代码**429(Too Many Requests)**返回给客户端。

以下是达到速率限制时返回的标头：

```shell
X-RateLimit-Limit-rate-limit-application_serviceAdvanced_127.0.0.1: 1
X-RateLimit-Remaining-rate-limit-application_serviceAdvanced_127.0.0.1: 0
X-RateLimit-Reset-rate-limit-application_serviceAdvanced_127.0.0.1: 268
```

之后，我们等待2秒。这是为端点配置的刷新间隔。最后，我们再次触发端点并获得成功响应。

## 6. 自定义Key生成器

**我们可以使用自定义key生成器自定义响应标头中发送的key**。这很有用，因为应用程序可能需要控制type属性提供的选项之外的key策略。

例如，这可以通过创建自定义RateLimitKeyGenerator实现来完成。我们可以添加更多限定符或完全不同的东西：

```java
@Bean
public RateLimitKeyGenerator rateLimitKeyGenerator(RateLimitProperties properties, RateLimitUtils rateLimitUtils) {
    return new DefaultRateLimitKeyGenerator(properties, rateLimitUtils) {
        @Override
        public String key(HttpServletRequest request, Route route, RateLimitProperties.Policy policy) {
            return super.key(request, route, policy) + "_" + request.getMethod();
        }
    };
}
```

上面的代码将REST方法名称附加到key中。例如：

```shell
X-RateLimit-Limit-rate-limit-application_serviceSimple_127.0.0.1_GET: 5
```

另一个关键点是RateLimitKeyGenerator bean将由spring-cloud-zuul-ratelimit自动配置。

## 7. 自定义错误处理

该框架支持速率限制数据存储的各种实现。例如，提供了Spring Data JPA和Redis。**默认情况下，失败只是使用DefaultRateLimiterErrorHandler类记录为错误**。

当我们需要以不同方式处理错误时，我们可以定义一个自定义的RateLimiterErrorHandler bean：

```java
@Bean
public RateLimiterErrorHandler rateLimitErrorHandler() {
    return new DefaultRateLimiterErrorHandler() {
        @Override
        public void handleSaveError(String key, Exception e) {
            // implementation
        }

        @Override
        public void handleFetchError(String key, Exception e) {
            // implementation
        }

        @Override
        public void handleError(String msg, Exception e) {
            // implementation
        }
    };
}
```

与RateLimitKeyGenerator bean类似，RateLimiterErrorHandler bean也会自动配置。

## 8. 总结

在本文中，我们了解了如何使用Spring Cloud Netflix Zuul和Spring Cloud Zuul RateLimit对API进行速率限制。