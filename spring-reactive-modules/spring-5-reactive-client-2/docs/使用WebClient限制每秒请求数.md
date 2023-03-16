## 1. 简介

在本教程中，我们将看到使用[Spring 5 WebClient](https://www.baeldung.com/spring-5-webclient)限制每秒请求数的不同方法。

虽然我们通常希望利用它的非阻塞特性，但某些情况可能会迫使我们添加延迟。我们将了解其中一些场景，同时使用一些[Project Reactor](https://www.baeldung.com/reactor-core)功能来控制对服务器的请求流。

## 2. 初始设置

**我们需要限制每秒请求的典型情况是避免服务器不堪重负**。此外，某些[Web服务](https://www.baeldung.com/building-a-restful-web-service-with-spring-and-java-based-configuration)具有每小时允许的最大请求数。同样，有些控制每个客户端的并发请求数。

### 2.1 编写简单的Web服务

为了探索这种情况，我们将从一个简单的[@RestController](https://www.baeldung.com/spring-controller-vs-restcontroller)开始，它提供固定范围内的随机数：

```java
@RestController
@RequestMapping("/random")
public class RandomController {

    @GetMapping
    Integer getRandom() {
        return new Random().nextInt(50);
    }
}
```

**接下来，我们将模拟一个昂贵的操作并限制并发请求数**。

### 2.2 限制我们服务器的速率

在查看解决方案之前，让我们更改我们的服务以模拟更真实的场景。

**首先，我们将限制我们的服务器可以接受的并发请求数，当达到限制时抛出异常**。

**其次，我们将添加一个延迟来处理我们的响应，模拟昂贵的操作**。虽然有[更强大的解决方案](https://www.baeldung.com/spring-bucket4j#:~:text=Free%3A%2020%20requests%20per%20hour,per%20hour%20per%20API%20client)可用，但我们这样做只是为了说明目的：

```java
public class Concurrency {

    public static final int MAX_CONCURRENT = 5;
    static final AtomicInteger CONCURRENT_REQUESTS = new HashMap<>();

    public static int protect(IntSupplier supplier) {
        try {
            if (CONCURRENT_REQUESTS.incrementAndGet() > MAX_CONCURRENT) {
                throw new UnsupportedOperationException("max concurrent requests reached");
            }

            TimeUnit.SECONDS.sleep(2);
            return supplier.getAsInt();
        } finally {
            CONCURRENT_REQUESTS.decrementAndGet();
        }
    }
}
```

最后，让我们更改端点以使用它：

```java
@GetMapping
Integer getRandom() {
    return Concurrency.protect(() -> new Random().nextInt(50));
}
```

**现在，当我们超过MAX_CONCURRENT请求时，我们的端点拒绝处理请求，并向客户端返回错误**。

### 2.3 编写一个简单的客户端

所有示例都将遵循以下模式来生成n个请求的[Flux](https://www.baeldung.com/spring-webflux)并向我们的服务发出GET请求：

```java
Flux.range(1, n)
  	.flatMap(i -> {
    	// GET request
  	});
```

为了减少样板代码，让我们在可以在所有示例中重用的方法中实现请求部分。**我们接收一个WebClient，调用get()，并使用[ParameterizedTypeReference](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/ParameterizedTypeReference.html)和retrieve()带有[泛型](https://www.baeldung.com/java-generics)的响应主体**：

```java
public interface RandomConsumer {

    static <T> Mono<T> get(WebClient client) {
        return client.get()
              .retrieve()
              .bodyToMono(new ParameterizedTypeReference<T>() {});
    }
}
```

## 3. 使用zipWith(Flux.Interval())延迟

我们的第一个示例使用[zipWith()](https://www.baeldung.com/reactor-combine-streams)将我们的请求与固定延迟相结合：

```java
public class ZipWithInterval {

    public static Flux<Integer> fetch(
          WebClient client, int requests, int delay) {
        return Flux.range(1, requests)
              .zipWith(Flux.interval(Duration.ofMillis(delay)))
              .flatMap(i -> RandomConsumer.get(client));
    }
}
```

因此，这会将每个请求延迟delay毫秒。我们应该注意，**这种延迟适用于发送请求之前**。

## 4. 使用Flux.delayElements()延迟

Flux有一种更直接的方式来延迟它的元素：

```java
public class DelayElements {

    public static Flux<Integer> fetch(
          WebClient client, int requests, int delay) {
        return Flux.range(1, requests)
              .delayElements(Duration.ofMillis(delay))
              .flatMap(i -> RandomConsumer.get(client));
    }
}
```

**使用delayElements()，延迟直接应用于Subscriber.onNext()信号**。换句话说，它延迟了Flux.range()中的每个元素。因此，传递给flatMap()的函数将受到影响，需要更长的时间才能启动。例如，如果delay值为1000，则在我们的请求开始之前会有一秒的延迟。

### 4.1 调整我们的解决方案

因此，如果我们没有提供足够长的延迟，我们将得到一个错误：

```java
@Test
void givenSmallDelay_whenDelayElements_thenExceptionThrown() {
    int delay = 100;

    int requests = 10;
    assertThrows(InternalServerError.class, () -> {
      	DelayElements.fetch(client, requests, delay)
            .blockLast();
    });
}
```

这是因为我们每个请求等待100毫秒，但每个请求在服务器端需要两秒才能完成。因此，我们的并发请求很快就达到了限制，我们得到了500错误。

**如果我们添加足够的延迟，我们可以摆脱请求限制。但是，我们会遇到另一个问题-我们会等待不必要的时间**。

根据我们的用例，等待太多可能会显著影响性能。**因此，接下来，让我们检查一个更合适的方法来处理这个问题，因为我们知道我们服务器的局限性**。

## 5. 使用flatMap()进行并发控制

鉴于我们服务的局限性，我们最好的选择是并行发送最多Concurrency.MAX_U并发请求。**为此，我们可以向flatMap()添加一个参数以获取最大并行处理数**：

```java
public class LimitConcurrency {

    public static Flux<Integer> fetch(WebClient client, int requests, int concurrency) {
        return Flux.range(1, requests)
              .flatMap(i -> RandomConsumer.get(client), concurrency);
    }
}
```

**此参数保证最大并发请求数不超过并发数，并且我们的处理不会延迟超过必要的时间**：

```java
@Test
void givenLimitInsideServerRange_whenLimitedConcurrency_thenNoExceptionThrown() {
    int limit = Concurrency.MAX_CONCURRENT;

    int requests = 10;
    assertDoesNotThrow(() -> {
      	LimitConcurrency.fetch(client, TOTAL_REQUESTS, limit)
        	.blockLast();
    });
}
```

不过，还有一些其他选项值得讨论，具体取决于我们的场景和偏好。让我们来看看其中的一些。

## 6. 使用Resilience4j RateLimiter

[Resilience4j](https://www.baeldung.com/resilience4j)是一个多功能库，专为处理应用程序中的容错而设计。**我们将使用它来限制一个时间间隔内的并发请求数并包括超时**。

让我们首先添加[resilience4j-reactor](https://central.sonatype.com/artifact/io.github.resilience4j/resilience4j-reactor/2.0.2)和[resilience4j-ratelimiter](https://central.sonatype.com/artifact/io.github.resilience4j/resilience4j-ratelimiter/2.0.2)依赖项：

```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-reactor</artifactId>
    <version>1.7.1</version>
</dependency>
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-ratelimiter</artifactId>
    <version>1.7.1</version>
</dependency>
```

然后我们使用RateLimiter.of()构建速率限制器，提供名称、发送新请求的间隔、并发限制和超时：

```java
public class Resilience4jRateLimit {

    public static Flux<Integer> fetch(WebClient client, int requests, int concurrency, int interval) {
        RateLimiter limiter = RateLimiter.of("my-rate-limiter", RateLimiterConfig.custom()
              .limitRefreshPeriod(Duration.ofMillis(interval))
              .limitForPeriod(concurrency)
              .timeoutDuration(Duration.ofMillis(interval * concurrency))
              .build());

        // ...
    }
}
```

**现在我们使用transformDeferred()将它包含在我们的Flux中，因此它控制我们的GET请求速率**：

```java
return Flux.range(1, requests)
  	.flatMap(i -> RandomConsumer.get(client)
    	.transformDeferred(RateLimiterOperator.of(limiter))
  	);
```

我们应该注意到，如果我们将间隔定义得太低，我们仍然会遇到问题。但是，如果我们需要与其他操作共享速率限制器规范，则此方法很有用。

## 7. Guava精确节流

[Guava](https://www.baeldung.com/guava-guide)有一个通用的[速率限制器](https://www.baeldung.com/guava-rate-limiter)，非常适合我们的场景。**此外，由于它使用令牌桶算法，因此它只会在必要时阻塞而不是每次都阻塞，这与Flux.delayElements()不同**。

首先，我们需要将[Guava](https://central.sonatype.com/artifact/com.google.guava/guava/31.1-jre)添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.1-jre</version>
</dependency>
```

要使用它，我们调用RateLimiter.create()并将我们要发送的每秒最大请求数传递给它。然后，我们在发送我们的请求之前在限制器上调用acquire()以在必要时限制执行：

```java
public class GuavaRateLimit {

    public static Flux<Integer> fetch(
          WebClient client, int requests, int requestsPerSecond) {
        RateLimiter limiter = RateLimiter.create(requestsPerSecond);

        return Flux.range(1, requests)
              .flatMap(i -> {
                  limiter.acquire();

                  return RandomConsumer.get(client);
              });
    }
}
```

由于其简单性，该解决方案效果极佳-它不会使我们的代码块过长。**例如，如果出于某种原因，一个请求花费的时间比预期的要长，则下一个请求不会等待执行**。但是，只有当我们在为requestsPerSecond设置的范围内时才会出现这种情况。

## 8. 总结

在本文中，我们看到了一些可用的方法来限制WebClient的速率。之后，我们模拟了一个受控的Web服务，看看它如何影响我们的代码和测试。此外，我们使用Project Reactor和一些库来帮助我们以不同的方式实现相同的目标。