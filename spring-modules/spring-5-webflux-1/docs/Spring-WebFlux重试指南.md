## 1. 概述

当我们在分布式云环境中构建应用程序时，我们需要针对故障进行设计，这通常涉及重试。

Spring WebFlux为我们提供了一些工具，用于重试失败的操作。

在本教程中，我们将了解如何为Spring WebFlux应用程序添加和配置重试。

## 2. 用例

对于我们的示例，我们将使用[MockWebServer](https://www.baeldung.com/spring-mocking-webclient#mockwebserver)并模拟一个外部系统暂时不可用，然后变为可用。

让我们为连接到此REST服务的组件创建一个简单的测试：

```java
class ExternalConnectorIntegrationTest {
    private ExternalConnector externalConnector;

    private MockWebServer mockExternalService;

    @BeforeEach
    void setup() throws IOException {
        externalConnector = new ExternalConnector(WebClient.builder()
                .baseUrl("http://localhost:8090")
                .build());
        mockExternalService = new MockWebServer();
        mockExternalService.start(8090);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockExternalService.shutdown();
    }

    @Test
    void givenExternalServiceReturnsError_whenGettingData_thenRetryAndReturnResponse() throws Exception {
        mockExternalService.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE.code()));
        mockExternalService.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE.code()));
        mockExternalService.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE.code()));
        mockExternalService.enqueue(new MockResponse().setBody("stock data"));

        StepVerifier.create(externalConnector.getData("ABC"))
                .expectNextMatches(response -> response.equals("stock data"))
                .verifyComplete();

        verifyNumberOfGetRequests(4);
    }

    private void verifyNumberOfGetRequests(int times) throws Exception {
        for (int i = 0; i < times; i++) {
            RecordedRequest recordedRequest = mockExternalService.takeRequest();
            assertThat(recordedRequest.getMethod()).isEqualTo("GET");
            assertThat(recordedRequest.getPath()).isEqualTo("/data/ABC");
        }
    }
}
```

## 3. 添加重试

Mono和Flux API中内置了两个关键的重试运算符。

### 3.1 retry

首先，让我们使用retry方法，该方法可以防止应用程序立即返回错误并重新订阅指定的次数：

```java
public Mono<String> getData(String stockId) {
    return webClient.get()
        .uri(PATH_BY_ID, stockId)
        .retrieve()
        .bodyToMono(String.class)
        .retry(3);
}
```

无论Web客户端返回什么错误，此操作最多都会重试3次。

### 3.2 retryWhen

接下来，让我们尝试使用retryWhen方法的可配置策略：

```java
public Mono<String> getData(String stockId) {
    return webClient.get()
          .uri(PATH_BY_ID, stockId)
          .retrieve()
          .bodyToMono(String.class)
          .retryWhen(Retry.max(3));
}
```

这允许我们配置一个[Retry](https://projectreactor.io/docs/core/release/api/reactor/util/retry/Retry.html)对象来描述所需的逻辑。

在这里，我们使用了max策略来重试最大尝试次数，这等效于我们的第一个示例，但允许我们使用更多配置选项。特别是，我们应该注意，在这种情况下，**每次重试都会尽可能快地发生**。

## 4. 添加延迟

无延迟重试的主要缺点是，这不会给失败的服务时间进行恢复。它可能会压倒它，使问题变得更糟并降低恢复的机会。

### 4.1 fixedDelay

我们可以使用fixedDelay策略在每次尝试之间添加延迟：

```java
public Mono<String> getData(String stockId) {
    return webClient.get()
            .uri(PATH_BY_ID, stockId)
            .retrieve()
            .bodyToMono(String.class)
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)));
}
```

这种配置允许尝试之间有两秒钟的延迟，这可能会增加成功的机会。但是，如果服务器遇到更长的中断时间，那么我们需要等待更长的时间。但是，如果我们将所有延迟都配置为较长的时间，那么短暂的延迟会进一步降低我们的服务速度。

### 4.2 backoff

我们可以使用backoff策略，而不是以固定的时间间隔重试：

```java
public Mono<String> getData(String stockId) {
    return webClient.get()
            .uri(PATH_BY_ID, stockId)
            .retrieve()
            .bodyToMono(String.class)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)));
}
```

实际上，**这会逐渐增加尝试之间的延迟**，在我们的示例中大致为2、4和8秒的间隔。**这使外部系统有更好的机会从常见的连接问题中恢复或处理积压的工作**。

### 4.3 jitter

backoff策略的另一个好处是它为计算的延迟间隔增加了随机性或[抖动](https://www.baeldung.com/resilience4j-backoff-jitter#jitter)。因此，**抖动有助于减少多个客户端同步重试的重试风暴**。

默认情况下，该值设置为0.5，这对应于计算延迟最多50%的抖动。

让我们使用jitter方法配置一个不同的值0.75，来表示最多75%计算延迟的抖动：

```java
public Mono<String> getData(String stockId) {
    return webClient.get()
            .uri(PATH_BY_ID, stockId)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)).jitter(0.75));
}
```

我们应该注意，该值的可能范围在0(无抖动)和1(至多为计算延迟100%的抖动)之间。

## 5. 过滤错误

此时，来自服务的任何错误都会导致重试尝试，包括4xx错误，例如400:Bad Request或401:Unauthorized。

显然，我们不应该重试此类客户端错误，因为服务器响应不会有任何不同。因此，让我们看看如何**仅在出现特定错误的情况下应用重试策略**。

首先，让我们创建一个异常来表示服务器错误：

```java
public class ServiceException extends RuntimeException {
    private final int statusCode;

    public ServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
```

接下来，我们为5xx错误创建一个error Mono，并使用filter方法配置我们的策略：

```java
public Mono<String> getData(String stockId) {
    return webClient.get()
            .uri(PATH_BY_ID, stockId)
            .retrieve()
            .onStatus(HttpStatus::is5xxServerError,
                    response -> Mono.error(new ServiceException("Server error", response.rawStatusCode())))
            .bodyToMono(String.class)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                    .filter(throwable -> throwable instanceof ServiceException));
}
```

现在，我们只在WebClient管道中抛出ServiceException时尝试重试。

## 6. 处理用尽的重试

最后，我们可以解释所有重试尝试都不成功的可能性。在这种情况下，该策略的默认行为是传播RetryExhaustedException，包装最后一个错误。

相反，让我们使用onRetryExhaustedThrow方法覆盖此行为，并为我们的ServiceException提供一个生成器：

```java
public Mono<String> getData(String stockId) {
    return webClient.get()
            .uri(PATH_BY_ID, stockId)
            .retrieve()
            .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new ServiceException("Server error", response.rawStatusCode())))
            .bodyToMono(String.class)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                    .filter(throwable -> throwable instanceof ServiceException)
                    .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                        throw new ServiceException("External Service failed to process after max retries", HttpStatus.SERVICE_UNAVAILABLE.value());
                    }));
}
```

现在，在一系列失败的重试结束时，请求将失败，并抛出ServiceException。

## 7. 总结

在本文中，我们研究了如何使用retry和retryWhen方法在Spring WebFlux应用程序中添加重试。

最初，我们为失败的操作添加了最大重试次数，然后我们通过使用和配置各种策略来引入重试之间的延迟。

最后，我们研究了只重试某些类型的错误，并在所有重试都用尽时自定义行为。