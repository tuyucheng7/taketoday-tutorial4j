## 1. 概述

在[Project Reactor简介](https://www.baeldung.com/reactor-core)中，我们了解了Mono<T\>，它是T类型实例的发布者。

在本快速教程中，我们将演示从Mono中提取T的阻塞和非阻塞方式：block和subscribe。

## 2. 阻塞方式

通常，Mono通过在某个时间点发出一个元素来成功完成。

让我们从一个示例发布者Mono<String\>开始：

```java
@Test
void whenMonoProducesString_thenBlockAndConsume() {
    String result = blockingHelloWorld().block();
    assertEquals("Hello world!", result);
}

private Mono<String> blockingHelloWorld() {
    // blocking
    return Mono.just("Hello world!");
}
```

在这里，只要发布者不发出值，我们就会阻塞执行。但是，它可能需要任意长的时间才能完成。

为了获得更多控制，我们将设置一个明确的持续时间：

```java
@Test
void whenMonoProducesString_thenBlockAndConsume() {
    String result = blockingHelloWorld().block(Duration.of(1000, ChronoUnit.MILLIS));
    assertEquals("Hello world!", result);
}
```

如果发布者在设置的持续时间内没有发出值，则会引发RuntimeException。

此外，Mono可能为空，上面的block()方法将返回null。在这种情况下，我们可以使用blockOptional：

```java
@Test
void whenMonoProducesString_thenBlockAndConsume() {
    Optional<String> result = Mono.<String>empty().blockOptional();
    assertEquals(Optional.empty(), result);
}
```

**一般来说，阻塞与响应式编程的原则相矛盾**。强烈建议不要阻塞响应式应用程序中的执行。

那么现在让我们看看如何以非阻塞方式获取值。

## 3. 非阻塞方式

首先，我们应该使用subscribe()方法以非阻塞方式订阅。此外，我们还将指定最终值的消费者：

```java
@Test
void whenMonoProducesString_thenConsumeNonBlocking() {
    blockingHelloWorld()
        .subscribe(result -> assertEquals("Hello world", result));
}
```

在这里，**即使生成值需要一些时间，执行也会立即继续，而不会阻塞subscribe()调用**。

在某些情况下，我们希望在中间步骤中消费该值。因此，我们可以使用运算符来添加行为：

```java
@Test
void whenMonoProducesString_thenConsumeNonBlocking() {
    blockingHelloWorld()
        .doOnNext(result -> assertEquals("Hello world", result))
        .subscribe();
}
```

## 4. 总结

在这篇简短的文章中，我们探讨了两种消费Mono<String\>生成的值的方法。