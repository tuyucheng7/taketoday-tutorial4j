## 1. 概述

[Resilience4j]()是一个轻量级容错库，可为Web应用程序提供各种容错和稳定性模式。在本教程中，我们将学习**如何在一个简单的Spring Boot应用程序中使用这个库**。

## 2. 设置

在本节中，我们重点**介绍为Spring Boot项目设置关键方面**。

### 2.1 Maven依赖项

首先，我们需要添加[spring-boot-starter-web](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-web)依赖项来引导一个简单的Web应用程序：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

接下来，**我们需要**[resilience4j-spring-boot2](https://search.maven.org/search?q=resilience4j-spring-boot2)**和**[spring-boot-starter-aop](https://search.maven.org/search?q=spring-boot-starter-aop)**依赖项**，**以便在我们的Spring Boot应用程序中使用注解来使用Resilience-4j库中的功能**：

```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot2</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

此外，我们还需要添加[spring-boot-starter-actuator](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-actuator)依赖项，以通过一组公开的端点监控应用程序的当前状态：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

最后，让我们添加[wiremock-jre8](https://search.maven.org/search?q=g:com.github.tomakehurst AND a:wiremock-jre8)依赖项，因为它将帮助我们使用[Mock HTTP服务器]()测试我们的REST API：

```xml
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock-jre8</artifactId>
    <scope>test</scope>
</dependency>
```

### 2.2 RestController和外部API调用程序

在使用Resilience4j库的不同功能时，我们的Web应用程序需要与外部API进行交互。因此，让我们继续为[RestTemplate]()添加一个bean，这将帮助我们进行API调用。

```java
@Bean
public RestTemplate restTemplate() {
    return new RestTemplateBuilder().rootUri("http://localhost:9090")
      .build();
}
```

接下来，让我们将ExternalAPICaller类定义为一个组件，并将restTemplate bean作为其成员使用：

```java
@Component
public class ExternalAPICaller {
    private final RestTemplate restTemplate;

    @Autowired
    public ExternalAPICaller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
```

在此之后，**我们可以定义ResilientAppController类，该类公开REST API端点并在内部使用ExternalAPICaller bean调用外部API**：

```java
@RestController
@RequestMapping("/api/")
public class ResilientAppController {
    private final ExternalAPICaller externalAPICaller;
}
```

### 2.3 Actuator端点

我们可以**通过**[Spring Boot Actuator]()**公开运行状况端点**，以了解应用程序在任何给定时间的确切状态。

因此，让我们将配置添加到application.properties文件中并启用端点：

```properties
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always

management.health.circuitbreakers.enabled=true
management.health.ratelimiters.enabled=true
```

此外，当我们需要时，我们将在同一个application.properties文件中添加特定于功能的配置。

### 2.4 单元测试

我们的Web应用程序将在真实场景中调用外部服务，但是我们可以通过**使用WireMockExtension类启动外部服务来Mock这种正在运行的服务的存在**。

因此，让我们将EXTERNAL_SERVICE定义为ResilientAppControllerUnitTest类中的静态成员：

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ResilientAppControllerUnitTest {

    @RegisterExtension
    static WireMockExtension EXTERNAL_SERVICE = WireMockExtension.newInstance()
          .options(WireMockConfiguration.wireMockConfig()
                .port(9090))
          .build();
}
```

此外，让我们添加一个TestRestTemplate的实例来调用API：

```java
@Autowired
private TestRestTemplate restTemplate;
```

### 2.5 异常处理器

Resilience4j库将根据上下文中的容错模式抛出异常，从而保护服务资源。但是，这些异常应该转换为带有对客户端有意义的状态代码的HTTP响应。

因此，让我们**定义ApiExceptionHandler类来保存不同异常的处理程序**：

```java
@ControllerAdvice
public class ApiExceptionHandler {
}
```

在探索不同的容错模式时，我们将在此类中添加处理程序。

## 3. 断路器

[断路器模式]()通过限制上游服务在部分或完全停机期间调用下游服务来**保护下游服务**。

让我们首先公开/api/circuit-breaker端点并添加@CircuitBreaker注解：

```java
@GetMapping("/circuit-breaker")
@CircuitBreaker(name = "CircuitBreakerService")
public String circuitBreakerApi() {
    return externalAPICaller.callApi();
}
```

根据需要，我们还需要在ExternalAPICaller类中定义callApi()方法，用于调用外部端点/api/external：

```java
public String callApi() {
    return restTemplate.getForObject("/api/external", String.class);
}
```

接下来，让我们在application.properties文件中添加断路器的配置：

```properties
resilience4j.circuitbreaker.instances.CircuitBreakerService.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.CircuitBreakerService.minimum-number-of-calls=5
resilience4j.circuitbreaker.instances.CircuitBreakerService.automatic-transition-from-open-to-half-open-enabled=true
resilience4j.circuitbreaker.instances.CircuitBreakerService.wait-duration-in-open-state=5s
resilience4j.circuitbreaker.instances.CircuitBreakerService.permitted-number-of-calls-in-half-open-state=3
resilience4j.circuitbreaker.instances.CircuitBreakerService.sliding-window-size=10
resilience4j.circuitbreaker.instances.CircuitBreakerService.sliding-window-type=count_based
```

从本质上讲，该配置将允许50%的失败调用处于[关闭状态]()的服务，之后它将打开电路并开始使用CallNotPermittedException拒绝请求。因此，最好在ApiExceptionHandler类中为该异常添加一个处理程序：

```java
@ExceptionHandler({CallNotPermittedException.class})
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public void handleCallNotPermittedException() {
}
```

最后，让我们通过使用EXTERNAL_SERVICE模拟下游服务停机的场景来测试/api/circuit-breaker API端点：

```java
@Test
public void testCircuitBreaker() {
    EXTERNAL_SERVICE.stubFor(WireMock.get("/api/external")
      .willReturn(serverError()));

    IntStream.rangeClosed(1, 5)
        .forEach(i -> {
            ResponseEntity response = restTemplate.getForEntity("/api/circuit-breaker", String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        });

    IntStream.rangeClosed(1, 5)
        .forEach(i -> {
            ResponseEntity response = restTemplate.getForEntity("/api/circuit-breaker", String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        });
    
    EXTERNAL_SERVICE.verify(5, getRequestedFor(urlEqualTo("/api/external")));
}
```

我们可以注意到，由于下游服务关闭，前5个调用失败。之后，电路切换到打开状态，因此，随后的5次尝试都被503 HTTP状态码拒绝，而没有实际调用底层API。

## 4. 重试

[重试模式]()**通过从暂时性问题中恢复来为系统提供弹性**，让我们首先添加带有@Retry注解的/api/retry API端点：

```java
@GetMapping("/retry")
@Retry(name = "retryApi", fallbackMethod = "fallbackAfterRetry")
public String retryApi() {
    return externalAPICaller.callApi();
}
```

此外，我们**可以选择在所有重试尝试失败时提供回退机制**，在这种情况下，我们提供了fallbackAfterRetry作为回退方法：

```java
public String fallbackAfterRetry(Exception ex) {
    return "all retries have exhausted";
}
```

接下来，让我们更新application.properties文件以添加管理重试行为的配置：

```properties
resilience4j.retry.instances.retryApi.max-attempts=3
resilience4j.retry.instances.retryApi.wait-duration=1s
resilience4j.retry.metrics.legacy.enabled=true
resilience4j.retry.metrics.enabled=true
```

因此，我们计划重试最多3次，每次延迟1s。

最后，让我们测试一下/api/retry API端点的重试行为：

```java
@Test
public void testRetry() {
    EXTERNAL_SERVICE.stubFor(WireMock.get("/api/external")
      .willReturn(ok()));
    ResponseEntity<String> response1 = restTemplate.getForEntity("/api/retry", String.class);
    EXTERNAL_SERVICE.verify(1, getRequestedFor(urlEqualTo("/api/external")));

    EXTERNAL_SERVICE.resetRequests();

    EXTERNAL_SERVICE.stubFor(WireMock.get("/api/external")
      .willReturn(serverError()));
    ResponseEntity<String> response2 = restTemplate.getForEntity("/api/retry", String.class);
    Assert.assertEquals(response2.getBody(), "all retries have exhausted");
    EXTERNAL_SERVICE.verify(3, getRequestedFor(urlEqualTo("/api/external")));
}
```

我们可以注意到，在第一种情况下，没有任何问题，因此一次尝试就足够了。另一方面，当出现问题时，有3次尝试，之后API通过回退机制做出响应。

## 5. 时间限制器

我们可以**使用**[时间限制器模式]()**为对外部系统进行的异步调用设置超时阈值**。

让我们添加在内部调用慢速API的/api/time-limiter API端点：

```java
@GetMapping("/time-limiter")
@TimeLimiter(name = "timeLimiterApi")
public CompletableFuture<String> timeLimiterApi() {
    return CompletableFuture.supplyAsync(externalAPICaller::callApiWithDelay);
}
```

此外，让我们通过在callApiWithDelay()方法中添加睡眠时间来模拟外部API调用中的延迟：

```java
public String callApiWithDelay() {
    String result = restTemplate.getForObject("/api/external", String.class);
    try {
        Thread.sleep(5000);
    } catch (InterruptedException ignore) {
    }
    return result;
}
```

接下来，我们需要在application.properties文件中提供timeLimiterApi的配置：

```properties
resilience4j.timelimiter.metrics.enabled=true
resilience4j.timelimiter.instances.timeLimiterApi.timeout-duration=2s
resilience4j.timelimiter.instances.timeLimiterApi.cancel-running-future=true
```

我们可以注意到阈值设置为2s，之后，Resilience4j库在内部使用TimeoutException取消异步操作。因此，让我们**在ApiExceptionHandler类中为该异常添加一个处理程序，以返回带有408 HTTP状态码的API响应**：

```java
@ExceptionHandler({TimeoutException.class})
@ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
public void handleTimeoutException() {
}
```

最后，让我们验证为/api/time-limiter API端点配置的时间限制器模式：

```java
@Test
public void testTimeLimiter() {
    EXTERNAL_SERVICE.stubFor(WireMock.get("/api/external").willReturn(ok()));
    ResponseEntity<String> response = restTemplate.getForEntity("/api/time-limiter", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.REQUEST_TIMEOUT);
    EXTERNAL_SERVICE.verify(1, getRequestedFor(urlEqualTo("/api/external")));
}
```

正如预期的那样，由于下游API调用被设置为需要超过5秒才能完成，因此我们见证了API调用超时。

## 6. 隔板

[bulkhead(隔板)模式]()**限制了对外部服务的最大并发调用数**。

首先我们添加带有@Bulkhead注解的/api/bulkhead API端点：

```java
@GetMapping("/bulkhead")
@Bulkhead(name="bulkheadApi")
public String bulkheadApi() {
    return externalAPICaller.callApi();
}
```

接下来，让我们在application.properties文件中定义配置以控制隔板功能：

```properties
resilience4j.bulkhead.metrics.enabled=true
resilience4j.bulkhead.instances.bulkheadApi.max-concurrent-calls=3
resilience4j.bulkhead.instances.bulkheadApi.max-wait-duration=1
```

通过这种方式，我们希望将并发调用的最大数量限制为3，因此如果隔板已满，每个线程只能等待1ms，之后，请求将被拒绝并抛出BulkheadFullException异常。此外，我们希望向客户端返回有意义的HTTP状态码，因此让我们添加一个异常处理程序：

```java
@ExceptionHandler({BulkheadFullException.class})
@ResponseStatus(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED)
public void handleBulkheadFullException() {
}
```

最后，让我们通过并行调用五个请求来测试隔板行为：

```java
@Test
public void testBulkhead() throws InterruptedException {
    EXTERNAL_SERVICE.stubFor(WireMock.get("/api/external")
      .willReturn(ok()));
    Map<Integer, Integer> responseStatusCount = new ConcurrentHashMap<>();

    IntStream.rangeClosed(1, 5)
        .parallel()
        .forEach(i -> {
            ResponseEntity<String> response = restTemplate.getForEntity("/api/bulkhead", String.class);
            int statusCode = response.getStatusCodeValue();
            responseStatusCount.put(statusCode, responseStatusCount.getOrDefault(statusCode, 0) + 1);
        });

    assertEquals(2, responseStatusCount.keySet().size());
    assertTrue(responseStatusCount.containsKey(BANDWIDTH_LIMIT_EXCEEDED.value()));
    assertTrue(responseStatusCount.containsKey(OK.value()));
    EXTERNAL_SERVICE.verify(3, getRequestedFor(urlEqualTo("/api/external")));
}
```

我们注意到**只有3个请求成功，而其他请求被拒绝并返回BANDWIDTH_LIMIT_EXCEEDED HTTP状态码**。

## 7. 速率限制器

[速率限制器模式]()**限制对资源的请求速率**。

让我们首先添加带有@RateLimiter注解的/api/rate-limiter API端点：

```java
@GetMapping("/rate-limiter")
@RateLimiter(name = "rateLimiterApi")
public String rateLimitApi() {
    return externalAPICaller.callApi();
}
```

接下来，让我们在application.properties文件中定义速率限制器的配置：

```properties
resilience4j.ratelimiter.metrics.enabled=true
resilience4j.ratelimiter.instances.rateLimiterApi.register-health-indicator=true
resilience4j.ratelimiter.instances.rateLimiterApi.limit-for-period=5
resilience4j.ratelimiter.instances.rateLimiterApi.limit-refresh-period=60s
resilience4j.ratelimiter.instances.rateLimiterApi.timeout-duration=0s
resilience4j.ratelimiter.instances.rateLimiterApi.allow-health-indicator-to-fail=true
resilience4j.ratelimiter.instances.rateLimiterApi.subscribe-for-events=true
resilience4j.ratelimiter.instances.rateLimiterApi.event-consumer-buffer-size=50
```

使用此配置，我们希望将API调用速率限制为5个请求/分钟而无需等待。**达到允许速率的阈值后，请求将被拒绝并抛出RequestNotPermitted异常**。因此，让我们在ApiExceptionHandler类中定义一个处理程序，用于将其转换为有意义的HTTP状态响应码：

```java
@ExceptionHandler({RequestNotPermitted.class})
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public void handleRequestNotPermitted() {
}
```

最后，让我们使用50个请求测试我们的限速API端点：

```java
@Test
public void testRatelimiter() {
    EXTERNAL_SERVICE.stubFor(WireMock.get("/api/external")
      .willReturn(ok()));
    Map<Integer, Integer> responseStatusCount = new ConcurrentHashMap<>();

    IntStream.rangeClosed(1, 50)
        .parallel()
        .forEach(i -> {
            ResponseEntity<String> response = restTemplate.getForEntity("/api/rate-limiter", String.class);
            int statusCode = response.getStatusCodeValue();
            responseStatusCount.put(statusCode, responseStatusCount.getOrDefault(statusCode, 0) + 1);
         });

    assertEquals(2, responseStatusCount.keySet().size());
    assertTrue(responseStatusCount.containsKey(TOO_MANY_REQUESTS.value()));
    assertTrue(responseStatusCount.containsKey(OK.value()));
    EXTERNAL_SERVICE.verify(5, getRequestedFor(urlEqualTo("/api/external")));
}
```

正如预期的那样，**只有5个请求成功，而所有其他请求都失败并返回TOO_MANY_REQUESTS HTTP状态码**。

## 8. Actuator端点

我们已将我们的应用程序配置为支持用于监控目的的Actuator端点，使用这些端点，我们可以使用一种或多种配置的容错模式来确定应用程序在一段时间内的行为方式。

首先，我们通常**可以使用对/actuator端点的GET请求找到所有公开的端点**：

```json
http://localhost:8080/actuator/
{
    "_links" : {
        "self" : {...},
        "bulkheads" : {...},
        "circuitbreakers" : {...},
        "ratelimiters" : {...},
        ...
    }
}
```

我们可以看到包含bulkheads、circuit breakers、ratelimiters等字段的JSON响应，每个字段都根据其与容错模式的关联为我们提供特定信息。

接下来，让我们看一下与重试模式相关的字段：

```json
"retries": {
    "href": "http://localhost:8080/actuator/retries",
    "templated": false
},
"retryevents": {
    "href": "http://localhost:8080/actuator/retryevents",
    "templated": false
},
"retryevents-name": {
    "href": "http://localhost:8080/actuator/retryevents/{name}",
    "templated": true
},
"retryevents-name-eventType": {
     "href": "http://localhost:8080/actuator/retryevents/{name}/{eventType}",
     "templated": true
}
```

接下来，让我们检查应用程序以查看重试实例的列表：

```json
http://localhost:8080/actuator/retries
{
    "retries" : ["retryApi"]
}
```

正如预期的那样，我们可以在配置的重试实例列表中看到retryApi实例。

最后，让我们通过浏览器向/api/retry API端点发出GET请求，并**使用/actuator/retryevents端点观察重试事件**：

```json
{
    "retryEvents": [
        {
            "retryName": "retryApi",
            "type": "RETRY",
            "creationTime": "2022-12-24T14:46:31.950822+05:30[Asia/Shanghai]",
            "errorMessage": "...",
            "numberOfAttempts": 1
        },
        {
            "retryName": "retryApi",
            "type": "RETRY",
            "creationTime": "2022-12-24T14:46:31.965661+05:30[Asia/Shanghai]",
            "errorMessage": "...",
            "numberOfAttempts": 2
        },
        {
            "retryName": "retryApi",
            "type": "ERROR",
            "creationTime": "2022-12-24T14:46:31.978801+05:30[Asia/Shanghai]",
            "errorMessage": "...",
            "numberOfAttempts": 3
        }
    ]
}
```

由于下游服务已关闭，我们可以看到3次重试尝试，任意2次尝试之间的等待时间为1秒，就像我们配置它一样。

## 9. 总结

在本文中，我们了解了如何在Sprint Boot应用程序中使用Resilience4j库；此外，**我们深入探讨了几种容错模式，例如断路器、速率限制器、时间限制器、隔板和重试**。