## 1. 概述

在本文中，我们将研究Guava库中的[RateLimiter](https://google.github.io/guava/releases/22.0/api/docs/index.html?com/google/common/util/concurrent/RateLimiter.html) 类。

RateLimiter类是一个允许我们调节某些处理发生的速率的结构。如果我们创建一个具有 N 个许可的RateLimiter——这意味着该进程每秒最多可以发出 N 个许可。

## 2.Maven依赖

我们将使用 Guava 的库：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

最新版本可以在[这里](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")找到。

## 3. 创建和使用RateLimiter 

假设我们想将doSomeLimitedOperation()的执行速率限制为每秒 2 次。

我们可以使用其create()工厂方法创建一个RateLimiter实例：

```java
RateLimiter rateLimiter = RateLimiter.create(2);
```

接下来，为了从RateLimiter 获得执行许可，我们需要调用acquire()方法：

```java
rateLimiter.acquire(1);
```

为了检查是否有效，我们将对 throttled 方法进行 2 次后续调用：

```java
long startTime = ZonedDateTime.now().getSecond();
rateLimiter.acquire(1);
doSomeLimitedOperation();
rateLimiter.acquire(1);
doSomeLimitedOperation();
long elapsedTimeSeconds = ZonedDateTime.now().getSecond() - startTime;
```

为了简化我们的测试，我们假设doSomeLimitedOperation()方法立即完成。

在这种情况下，对acquire()方法的两次调用不应阻塞，并且经过的时间应少于或低于一秒——因为可以立即获得两种许可：

```java
assertThat(elapsedTimeSeconds <= 1);
```

此外，我们可以在一次acquire()调用中获取所有许可：

```java
@Test
public void givenLimitedResource_whenRequestOnce_thenShouldPermitWithoutBlocking() {
    // given
    RateLimiter rateLimiter = RateLimiter.create(100);

    // when
    long startTime = ZonedDateTime.now().getSecond();
    rateLimiter.acquire(100);
    doSomeLimitedOperation();
    long elapsedTimeSeconds = ZonedDateTime.now().getSecond() - startTime;

    // then
    assertThat(elapsedTimeSeconds <= 1);
}
```

例如，如果我们需要每秒发送 100 个字节，这将很有用。我们可以一次发送一百次一个字节以获得一个许可。另一方面，我们可以一次发送所有 100 个字节，在一个操作中获得所有 100 个许可。

## 4. 阻塞式取证

现在，让我们考虑一个稍微复杂一点的例子。

我们将创建一个具有 100 个许可的RateLimiter 。然后我们将执行一个需要获得 1000 个许可的动作。根据RateLimiter 的规范，这样的操作至少需要 10 秒才能完成，因为我们每秒只能执行 100 个操作单元：

```java
@Test
public void givenLimitedResource_whenUseRateLimiter_thenShouldLimitPermits() {
    // given
    RateLimiter rateLimiter = RateLimiter.create(100);

    // when
    long startTime = ZonedDateTime.now().getSecond();
    IntStream.range(0, 1000).forEach(i -> {
        rateLimiter.acquire();
        doSomeLimitedOperation();
    });
    long elapsedTimeSeconds = ZonedDateTime.now().getSecond() - startTime;

    // then
    assertThat(elapsedTimeSeconds >= 10);
}
```

请注意，我们在这里如何使用acquire()方法——这是一个阻塞方法，我们在使用它时应该谨慎。当调用acquire()方法时，它会阻塞正在执行的线程，直到获得许可为止。

调用不带参数的acquire()与以一个作为参数调用它是一样的——它将尝试获取一个许可。

## 5. 超时获取许可

RateLimiter API 还有一个非常有用的acquire ()方法，它接受超时和TimeUnit作为参数。

在没有可用许可时调用此方法将导致它等待指定的时间然后超时——如果在超时时间内没有足够的可用许可。

当在给定的超时时间内没有可用的许可时，它返回false。如果acquire()成功，则返回true：

```java
@Test
public void givenLimitedResource_whenTryAcquire_shouldNotBlockIndefinitely() {
    // given
    RateLimiter rateLimiter = RateLimiter.create(1);

    // when
    rateLimiter.acquire();
    boolean result = rateLimiter.tryAcquire(2, 10, TimeUnit.MILLISECONDS);

    // then
    assertThat(result).isFalse();
}
```

我们创建了一个具有一个许可的RateLimiter，因此尝试获得两个许可将始终导致tryAcquire()返回false。

## 六. 总结

在本快速教程中，我们了解了Guava库中的RateLimiter结构。

我们学习了如何使用RateLimtiter来限制每秒的许可数。我们看到了如何使用它的阻塞 API，我们还使用显式超时来获取许可。