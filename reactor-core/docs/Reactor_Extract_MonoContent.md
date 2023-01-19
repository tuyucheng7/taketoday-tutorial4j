## 1. 概述

在我们的[Reactor介绍](../../reactive-spring-4/docs/Reactor_Core.md)中，我们了解了Mono<T\>，它是T类型实例的发布者。

在这个教程中，我们将介绍从Mono中提取T的阻塞和非阻塞方法：block()和subscribe()。

## 2. 阻塞方式

一般来说，Mono通过在某个时间点发出一个元素来成功完成。

让我们从发布者Mono<String\>的示例开始：

```java
class MonoUnitTest {

    @Test
    void whenMonoProducesString_thenBlockAndConsume() {
        String result = blockingHelloWorld().block();
        assertEquals("Hello world!", result);
    }

    private Mono<String> blockingHelloWorld() {
        // blocking
        return Mono.just("Hello world!");
    }
}
```

在这里，只要发布者不发出值，我们就会阻塞执行。它可能需要任意时间才能完成。

为了获得更多控制，我们设置一个显式的持续时间：

```java
class MonoUnitTest {

    @Test
    void whenMonoProducesString_thenBlockAndConsume() {
        String result = blockingHelloWorld().block(Duration.of(1000, ChronoUnit.MILLIS));
        assertEquals("Hello world!", result);
    }
}
```

如果发布者在设置的持续时间内没有发出值，则会引发RuntimeException。

此外，Mono可能为空，并且上面的block()方法将返回null。在这种情况下，我们可以使用blockOptional()：

```java
class MonoUnitTest {

    @Test
    void whenMonoProducesString_thenBlockAndConsume() {
        Optional<String> result = Mono.<String>empty().blockOptional();
        assertEquals(Optional.empty(), result);
    }
}
```

**一般来说，阻塞与响应式编程的原则相矛盾**。在响应式应用程序中阻塞执行是非常不可取的。

因此，现在让我们看看如何以非阻塞的方式获取值。

## 3. 非阻塞方式

首先，我们应该使用subscribe()方法以非阻塞方式订阅。此外，我们将指定最终值的消费者：

```java
class MonoUnitTest {

    @Test
    void whenMonoProducesString_thenConsumeNonBlocking() {
        blockingHelloWorld().subscribe(result -> assertEquals("Hello world", result));
    }
}
```

在这里，即使**生成值需要一些时间，执行也会立即继续，而不会阻塞subscribe()调用**。

在某些情况下，我们希望在中间步骤中消费该值。因此，我们可以使用运算符来添加行为：

```java
class MonoUnitTest {

    @Test
    void whenMonoProducesString_thenConsumeNonBlocking() {
        blockingHelloWorld().doOnNext(result -> assertEquals("Hello world", result)).subscribe();
    }
}
```

## 4. 总结

在这篇简短的文章中，我们介绍了两种消费Mono<String\>生成的值的方法。