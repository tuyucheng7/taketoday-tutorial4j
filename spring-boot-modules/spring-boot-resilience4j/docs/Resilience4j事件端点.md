## 1. 概述

在本文中，我们将**了解Resilience4j在内部使用的事件，以了解它提供的弹性机制，以及在Spring Boot应用程序中列出它们的端点是什么**。

我们将重用[Spring Boot Resilience4j指南](https://www.baeldung.com/spring-boot-resilience4j)文章中的项目来展示Resilience4j如何在Actuator端点下列出不同的模式事件。

## 2. 模式事件

该库在内部使用事件来驱动弹性模式的行为(允许或拒绝调用)，作为一种通信机制。此外，这些事件为监控和可观察性以及帮助进行故障排除提供了有价值的详细信息。

此外，由断路器、重试、速率限制器、隔板和时间限制器实例发出的事件分别存储在循环事件消费者缓冲区中。缓冲区的大小可根据eventConsumerBufferSize属性进行配置，默认为100个事件。

我们将查看Actuator端点下每个模式的特定发射事件列表。

## 3. 断路器

### 3.1 配置

我们将为为/api/circuit-breaker端点定义的断路器实例提供默认配置：

```yaml
resilience4j.circuitbreaker:
    configs:
        default:
            registerHealthIndicator: true
            slidingWindowSize: 10
            minimumNumberOfCalls: 5
            permittedNumberOfCallsInHalfOpenState: 3
            automaticTransitionFromOpenToHalfOpenEnabled: true
            waitDurationInOpenState: 5s
            failureRateThreshold: 50
            eventConsumerBufferSize: 50
    instances:
        externalService:
            baseConfig: default
```

### 3.2 事件

Resilience4j在actuator端点下公开与断路器相关的事件：

```text
http://localhost:8080/actuator/circuitbreakers
```

**断路器是最复杂的弹性机制，定义了最多的事件类型**。由于其实现依赖于状态机的概念，因此它使用事件来发出状态转换的信号。因此，让我们看看在从初始CLOSED状态转换到OPEN状态并返回到CLOSED状态时Actuator事件端点下列出的事件。

对于成功的调用，我们可以看到CircuitBreakerOnSuccess事件：

```json
{
    "circuitBreakerName": "externalService",
    "type": "SUCCESS",
    "creationTime": "2023-03-22T16:45:26.349252+02:00",
    "errorMessage": null,
    "durationInMs": 526,
    "stateTransition": null
}
```

让我们看看当断路器实例处理失败请求时会发生什么：

```java
@Test
void testCircuitBreakerEvents() throws Exception {
    EXTERNAL_SERVICE.stubFor(WireMock.get("/api/external")
        .willReturn(serverError()));

    IntStream.rangeClosed(1, 5)
        .forEach(i -> {
            ResponseEntity<String> response = restTemplate.getForEntity("/api/circuit-breaker", String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        });
    // ...
}
```

正如我们所观察到的，**失败的请求触发了CircuitBreakerOnErrorEvent**：

```json
{
    "circuitBreakerName": "externalService",
    "type": "ERROR",
    "creationTime": "2023-03-19T20:13:05.069002+02:00",
    "errorMessage": "org.springframework.web.client.HttpServerErrorException$InternalServerError: 500 Server Error: \"{\"error\": \"Internal Server Error\"}\"",
    "durationInMs": 519,
    "stateTransition": null
}
```

此外，这些成功/错误事件包含durationInMs属性，这是一个非常有用的性能指标。

当故障率超过配置的阈值时，实例会触发CircuitBreakerOnFailureRateExceededEvent，确定向OPEN状态的转换并触发CircuitBreakerOnStateTransitionEvent事件：

```json
{
    "circuitBreakerName": "externalService",
    "type": "FAILURE_RATE_EXCEEDED",
    "creationTime": "2023-03-19T20:13:07.554813+02:00",
    "errorMessage": null,
    "durationInMs": null,
    "stateTransition": null
},
{
    "circuitBreakerName": "externalService",
    "type": "STATE_TRANSITION",
    "creationTime": "2023-03-19T20:13:07.563623+02:00",
    "errorMessage": null,
    "durationInMs": null,
    "stateTransition": "CLOSED_TO_OPEN"
}
```

查看最后一个事件的stateTransition属性，断路器处于OPEN状态。新的调用尝试引发CallNotPermittedException，进而触发CircuitBreakerOnCallNotPermittedEvent：

```json
{
    "circuitBreakerName": "externalService",
    "type": "NOT_PERMITTED",
    "creationTime": "2023-03-22T16:50:11.897977+02:00",
    "errorMessage": null,
    "durationInMs": null,
    "stateTransition": null
}
```

在配置的waitDuration过去后，断路器将转换到中间OPEN_TO_HALF_OPEN状态，通过CircuitBreakerOnStateTransitionEvent再次发出信号：

```json
{
    "circuitBreakerName": "externalService",
    "type": "STATE_TRANSITION",
    "creationTime": "2023-03-22T16:50:14.787381+02:00",
    "errorMessage": null,
    "durationInMs": null,
    "stateTransition": "OPEN_TO_HALF_OPEN"
}
```

在OPEN_TO_HALF_OPEN状态下，如果配置的minimumNumberOfCalls成功，则CircuitBreakerOnStateTransitionEvent将再次触发切换回OPEN状态：

```json
{
    "circuitBreakerName": "externalService",
    "type": "STATE_TRANSITION",
    "creationTime": "2023-03-22T17:48:45.931978+02:00",
    "errorMessage": null,
    "durationInMs": null,
    "stateTransition": "HALF_OPEN_TO_CLOSED"
}
```

与断路器相关的事件提供了有关实例如何执行和处理请求的见解。因此，我们可以**通过分析断路器事件来识别潜在问题并跟踪性能指标**。

## 4. 重试

### 4.1 配置

对于我们的/api/retry端点，我们将使用以下配置创建一个Retry实例：

```yaml
resilience4j.retry:
    configs:
        default:
            maxAttempts: 3
            waitDuration: 100
    instances:
        externalService:
            baseConfig: default
```

### 4.2 事件

让我们检查一下重试模式在Actuator端点下列出的事件：

```text
http://localhost:8080/actuator/retryevents
```

例如，当调用失败时，将根据配置进行重试：

```java
@Test
void testRetryEvents()throws Exception {
    EXTERNAL_SERVICE.stubFor(WireMock.get("/api/external")
        .willReturn(serverError()));
    ResponseEntity<String> response = restTemplate.getForEntity("/api/retry", String.class);
     
    // ...
}
```

因此，**对于每次重试，都会发出RetryOnErrorEvent，并且Retry实例会根据其配置安排另一次重试**。正如我们所见，该事件有一个numberOfAttempts计数器字段：

```json
{
    "retryName": "retryApi",
    "type": "RETRY",
    "creationTime": "2023-03-19T22:57:51.458811+02:00",
    "errorMessage": "org.springframework.web.client.HttpServerErrorException$InternalServerError: 500 Server Error: \"{\"error\": \"Internal Server Error\"}\"",
    "numberOfAttempts": 1
}
```

因此，一旦配置的尝试分配用完，Retry实例就会发布一个RetryOnFailedEvent，同时还会让底层异常传播：

```json
{
    "retryName": "retryApi",
    "type": "ERROR",
    "creationTime": "2023-03-19T23:30:11.440423+02:00",
    "errorMessage": "org.springframework.web.client.HttpServerErrorException$InternalServerError: 500 Server Error: \"{\"error\": \"Internal Server Error\"}\"",
    "numberOfAttempts": 3
}
```

重试使用这些事件来确定是安排再次重试还是放弃并报告失败，指示进程的当前状态。因此，监视这些事件可以帮助微调重试配置以带来最大收益。

## 5. 时间限制器

### 5.1 配置

/api/time-limiter端点使用为我们的实例定义的时间限制器配置：

```yaml
resilience4j.timelimiter:
    configs:
        default:
            cancelRunningFuture: true
            timeoutDuration: 2s
    instances:
        externalService:
            baseConfig: default
```

### 5.2 事件

时间限制器事件在端点处列出：

```text
http://localhost:8080/actuator/timelimiterevents
```

时间限制器事件提供有关操作状态的信息，实例通过允许请求完成或在超过配置的超时时取消请求来对事件作出反应。

例如，如果调用在配置的时间限制内执行，则会发出TimeLimiterOnSuccessEvent：

```json
{
    "timeLimiterName": "externalService",
    "type": "SUCCESS",
    "creationTime": "2023-03-20T20:48:43.089529+02:00"
}
```

另一方面，**当调用在时间限制内失败时，会发生TimeLimiterOnErrorEvent**：

```json
{
    "timeLimiterName": "externalService",
    "type": "ERROR",
    "creationTime": "2023-03-20T20:49:12.089537+02:00"
}
```

由于我们的/api/time-limiter端点实现的延迟超过了timeoutDuration配置，因此会导致调用超时。结果，它遇到TimeoutException，然后触发TimeLimiterOnErrorEvent：

```java
@Test
void testTimeLimiterEvents() throws Exception {
    EXTERNAL_SERVICE.stubFor(WireMock.get("/api/external")
        .willReturn(ok()));
    ResponseEntity<String> response = restTemplate.getForEntity("/api/time-limiter", String.class);
    
    // ...
}
```

```json
{
    "timeLimiterName": "externalService",
    "type": "TIMEOUT",
    "creationTime": "2023-03-20T19:32:38.733874+02:00"
}
```

通过监控时间限制器事件，我们能够跟踪请求状态并解决与超时相关的问题，这可以帮助我们优化响应时间。

## 6. 隔板

### 6.1 配置

让我们使用以下配置创建我们的Bulkhead实例：

```yaml
resilience4j.bulkhead:
    configs:
        default:
            max-concurrent-calls: 3
            max-wait-duration: 1
    instances:
        externalService:
            baseConfig: default
```

### 6.2 事件

我们可以在其Actuator端点下看到隔板模式使用的特定事件：

```text
http://localhost:8080/actuator/bulkheadevents
```

**让我们看看在提交的调用超过允许的并发限制的情况下模式发出的事件**：

```java
@Test
void testBulkheadEvents() throws Exception {
    EXTERNAL_SERVICE.stubFor(WireMock.get("/api/external").willReturn(ok()));
    Map<Integer, Integer> responseStatusCount = new ConcurrentHashMap<>();
    ExecutorService executorService = Executors.newFixedThreadPool(5);

    List<Callable<Integer>> tasks = new ArrayList<>();
    IntStream.rangeClosed(1, 5)
        .forEach(
            i ->
                tasks.add(
                    () -> {
                        ResponseEntity<String> response = restTemplate.getForEntity("/api/bulkhead", String.class);
                        return response.getStatusCodeValue();
                    }));
    
    List<Future<Integer>> futures = executorService.invokeAll(tasks);
    for (Future<Integer> future : futures) {
        int statusCode = future.get();
        responseStatusCount.merge(statusCode, 1, Integer::sum);
    }
    // ...
}
```

隔板机制通过基于配置允许或拒绝调用来对事件作出反应。例如，当允许在配置的并发限制内调用时，它会消耗一个可用插槽并发出BulkheadOnCallPermittedEvent：

```json
{
    "bulkheadName": "externalService",
    "type": "CALL_PERMITTED",
    "creationTime": "2023-03-20T14:10:52.417063+02:00"
}
```

当达到配置的并发限制时，进一步的并发调用将被Bulkhead实例拒绝，**抛出BulkheadFullException，从而触发BulkheadOnCallRejectedEvent**：

```json
{
    "bulkheadName": "externalService",
    "type": "CALL_REJECTED",
    "creationTime": "2023-03-20T14:10:52.419099+02:00"
}
```

最后，当调用完成执行时，无论是成功还是出错，都会释放插槽，并触发BulkheadOnCallFinishedEvent：

```json
{
    "bulkheadName": "externalService",
    "type": "CALL_FINISHED",
    "creationTime": "2023-03-20T14:10:52.500715+02:00"
}
```

**观察隔板事件有助于确保资源隔离并在重负载或故障期间保持稳定的性能**。同样，我们可以通过跟踪允许和拒绝的调用数量，然后相应地微调Bulkhead配置来更好地平衡服务可用性和资源保护。

## 7. 速率限制器

### 7.1 配置

我们将根据配置为/api/rate-limiter端点创建我们的速率限制器实例：

```yaml
resilience4j.ratelimiter:
    configs:
        default:
            limit-for-period: 5
            limit-refresh-period: 60s
            timeout-duration: 0s
            allow-health-indicator-to-fail: true
            subscribe-for-events: true
            event-consumer-buffer-size: 50
    instances:
        externalService:
            baseConfig: default
```

### 7.2 事件

对于速率限制器模式，我们可以在端点下找到事件列表：

```text
http://localhost:8080/actuator/ratelimiterevents
```

让我们检查通过对超过配置速率限制的/api/rate-limiter端点进行并行调用而生成的事件：

```java
@Test
void testRateLimiterEvents() throws Exception {
    EXTERNAL_SERVICE.stubFor(WireMock.get("/api/external")
        .willReturn(ok()));

    IntStream.rangeClosed(1, 50)
        .forEach(i -> {
            ResponseEntity<String> response = restTemplate.getForEntity("/api/rate-limiter", String.class);
            int statusCode = response.getStatusCodeValue();
            responseStatusCount.put(statusCode, responseStatusCount.getOrDefault(statusCode, 0) + 1);
        });
    // ...
}
```

最初，每个请求在达到速率限制之前的前几次调用成功地从令牌桶中获取令牌。结果，库触发了RateLimiterOnSuccessEvent：

```json
{
    "rateLimiterName": "externalService",
    "type": "SUCCESSFUL_ACQUIRE",
    "creationTime": "2023-03-20T10:55:19.314306+02:00"
}
```

一旦令牌在配置的limit-refresh-period内用完，**进一步的调用会导致RequestNotPermitted异常，从而触发RateLimiterOnFailureEvent**：

```json
{
    "rateLimiterName": "externalService",
    "type": "FAILED_ACQUIRE",
    "creationTime": "2023-03-20T12:48:28.623726+02:00"
}
```

**速率限制器事件允许监视端点处理请求的速率**。通过跟踪成功/失败事件的数量，我们可以评估速率限制是否合适，确保客户端获得良好的服务和资源保护。

## 8. 总结

在本文中，我们看到了Resilience4j为断路器、速率限制器、隔板和时间限制器模式发出的事件以及用于访问它们的端点。