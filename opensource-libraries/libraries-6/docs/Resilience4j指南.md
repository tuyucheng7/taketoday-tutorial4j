## 1. 概述

在本教程中，我们将讨论[Resilience4j](https://resilience4j.github.io/resilience4j/)库。

该库通过管理远程通信的容错来帮助实现弹性系统。

该库的灵感来自 [Hystrix](https://www.baeldung.com/introduction-to-hystrix) ，但提供了更方便的 API 和许多其他功能，如 Rate Limiter(阻止过于频繁的请求)、Bulkhead(避免太多并发请求)等。

## 2.Maven 设置

首先，我们需要将目标模块添加到我们的pom.xml(例如，我们在此处添加断路器)：

```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-circuitbreaker</artifactId>
    <version>0.12.1</version>
</dependency>
```

在这里，我们使用 断路器 模块。所有模块及其最新版本都可以在[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"io.github.resilience4j")上找到。

在接下来的部分中，我们将介绍库中最常用的模块。

## 3.断路器

请注意，对于此模块，我们需要上面显示的resilience4j-circuitbreaker依赖项。

[断路器模式](https://martinfowler.com/bliki/CircuitBreaker.html)帮助我们在远程服务关闭时防止级联故障。

在多次尝试失败后，我们可以认为该服务不可用/过载，并急切地拒绝所有后续对它的请求。通过这种方式，我们可以为可能失败的调用节省系统资源。

让我们看看如何使用 Resilience4j 实现这一目标。

首先，我们需要定义要使用的设置。最简单的方法是使用默认设置：

```java
CircuitBreakerRegistry circuitBreakerRegistry
  = CircuitBreakerRegistry.ofDefaults();
```

也可以使用自定义参数：

```java
CircuitBreakerConfig config = CircuitBreakerConfig.custom()
  .failureRateThreshold(20)
  .ringBufferSizeInClosedState(5)
  .build();
```

在这里，我们将速率阈值设置为 20%，并且至少要尝试调用 5 次。

然后，我们创建一个 CircuitBreaker对象并通过它调用远程服务：

```java
interface RemoteService {
    int process(int i);
}

CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
CircuitBreaker circuitBreaker = registry.circuitBreaker("my");
Function<Integer, Integer> decorated = CircuitBreaker
  .decorateFunction(circuitBreaker, service::process);
```

最后，让我们通过 JUnit 测试看看它是如何工作的。

我们将尝试调用该服务 10 次。我们应该能够验证调用至少尝试了 5 次，然后在 20% 的调用失败后立即停止：

```java
when(service.process(any(Integer.class))).thenThrow(new RuntimeException());

for (int i = 0; i < 10; i++) {
    try {
        decorated.apply(i);
    } catch (Exception ignore) {}
}

verify(service, times(5)).process(any(Integer.class));
```

### 3.1. 断路器的状态和设置

CircuitBreaker可以 处于以下三种状态之一：

-   关闭- 一切正常，不涉及短路
-   OPEN – 远程服务器已关闭，对它的所有请求都已短路
-   HALF_OPEN – 自进入 OPEN 状态后配置的时间量已经过去， CircuitBreaker允许请求检查远程服务是否重新在线

我们可以配置以下设置：

-   断路器 打开并开始短路调用的故障率阈值
-   定义断路器在切换到半开之前应保持打开状态的等待时间
-   CircuitBreaker半开或半闭时环形缓冲区的大小
-   处理CircuitBreaker事件的自定义CircuitBreakerEventListener
-   一个自定义Predicate，它评估一个异常是否应该算作失败，从而增加失败率

## 4.限速器

与上一节类似，此功能需要[resilience4j-ratelimiter](https://search.maven.org/classic/#search|ga|1|a%3A"resilience4j-ratelimiter") 依赖项。

顾名思义，此功能允许限制对某些服务的访问。它的 API 与CircuitBreaker 的非常相似——有Registry、Config和Limiter类。

下面是它的外观示例：

```java
RateLimiterConfig config = RateLimiterConfig.custom().limitForPeriod(2).build();
RateLimiterRegistry registry = RateLimiterRegistry.of(config);
RateLimiter rateLimiter = registry.rateLimiter("my");
Function<Integer, Integer> decorated
  = RateLimiter.decorateFunction(rateLimiter, service::process);
```

现在，如有必要，所有对装饰服务块的调用都将符合速率限制器配置。

我们可以配置如下参数：

-   限额刷新周期
-   刷新周期的权限限制
-   默认等待权限持续时间

## 5. 舱壁

在这里，我们首先需要[resilience4j-bulkhead](https://search.maven.org/classic/#search|ga|1|resilience4j-bulkhead)依赖项。

可以限制对特定服务的并发调用数。

让我们看一个使用 Bulkhead API 来配置最大并发调用数的示例：

```java
BulkheadConfig config = BulkheadConfig.custom().maxConcurrentCalls(1).build();
BulkheadRegistry registry = BulkheadRegistry.of(config);
Bulkhead bulkhead = registry.bulkhead("my");
Function<Integer, Integer> decorated
  = Bulkhead.decorateFunction(bulkhead, service::process);
```

为了测试此配置，我们将调用模拟服务的方法。

然后，我们确保Bulkhead不允许任何其他调用：

```java
CountDownLatch latch = new CountDownLatch(1);
when(service.process(anyInt())).thenAnswer(invocation -> {
    latch.countDown();
    Thread.currentThread().join();
    return null;
});

ForkJoinTask<?> task = ForkJoinPool.commonPool().submit(() -> {
    try {
        decorated.apply(1);
    } finally {
        bulkhead.onComplete();
    }
});
latch.await();
assertThat(bulkhead.isCallPermitted()).isFalse();
```

我们可以配置以下设置：

-   bulkhead 允许的最大并行执行量
-   尝试进入饱和舱壁时线程将等待的最长时间

## 6.重试

对于此功能，我们需要将[resilience4j-retry](https://search.maven.org/classic/#search|ga|1|resilience4j-retry)库添加到项目中。

我们可以使用重试 API自动重试失败的调用：

```java
RetryConfig config = RetryConfig.custom().maxAttempts(2).build();
RetryRegistry registry = RetryRegistry.of(config);
Retry retry = registry.retry("my");
Function<Integer, Void> decorated
  = Retry.decorateFunction(retry, (Integer s) -> {
        service.process(s);
        return null;
    });
```

现在让我们模拟在远程服务调用期间抛出异常的情况，并确保库自动重试失败的调用：

```java
when(service.process(anyInt())).thenThrow(new RuntimeException());
try {
    decorated.apply(1);
    fail("Expected an exception to be thrown if all retries failed");
} catch (Exception e) {
    verify(service, times(2)).process(any(Integer.class));
}
```

我们还可以配置如下：

-   最大尝试次数
-   重试前的等待时间
-   一个自定义函数，用于在失败后修改等待间隔
-   一个自定义Predicate，它评估异常是否会导致重试调用

## 7.缓存

Cache 模块需要[resilience4j-cache](https://search.maven.org/classic/#search|ga|1|resilience4j-cache)依赖项。

初始化看起来与其他模块略有不同：

```java
javax.cache.Cache cache = ...; // Use appropriate cache here
Cache<Integer, Integer> cacheContext = Cache.of(cache);
Function<Integer, Integer> decorated
  = Cache.decorateSupplier(cacheContext, () -> service.process(1));
```

这里缓存由使用的 [JSR-107 缓存](https://www.baeldung.com/jcache)实现完成，Resilience4j 提供了一种应用它的方法。

请注意，没有用于装饰函数的 API(如Cache.decorateFunction(Function))，该 API 仅支持 Supplier和Callable类型。

## 8. 时间限制器

对于这个模块，我们必须添加[resilience4j-timelimiter](https://search.maven.org/classic/#search|ga|1|resilience4j-timelimiter)依赖项。

可以使用 TimeLimiter来限制调用远程服务所花费的时间。

为了演示，让我们设置一个配置超时为 1 毫秒的TimeLimiter ：

```java
long ttl = 1;
TimeLimiterConfig config
  = TimeLimiterConfig.custom().timeoutDuration(Duration.ofMillis(ttl)).build();
TimeLimiter timeLimiter = TimeLimiter.of(config);
```

接下来，让我们验证 Resilience4j 是否以预期的超时时间调用Future.get() ：

```java
Future futureMock = mock(Future.class);
Callable restrictedCall
  = TimeLimiter.decorateFutureSupplier(timeLimiter, () -> futureMock);
restrictedCall.call();

verify(futureMock).get(ttl, TimeUnit.MILLISECONDS);
```

我们也可以将它与CircuitBreaker结合起来：

```java
Callable chainedCallable
  = CircuitBreaker.decorateCallable(circuitBreaker, restrictedCall);
```

## 9. 附加模块

Resilience4j 还提供了许多附加模块，这些模块简化了它与流行框架和库的集成。

一些更知名的集成是：

-  Spring Boot– resilience4j-spring-boot模块
-   Ratpack – resilience4j-ratpack模块
-   改造——resilience4j- 改造模块
-   Vertx – resilience4j-vertx模块
-   Dropwizard – resilience4j-metrics模块
-   Prometheus – resilience4j-prometheus模块

## 10.总结

在本文中，我们了解了 Resilience4j 库的不同方面，并学习了如何使用它来解决服务器间通信中的各种容错问题。