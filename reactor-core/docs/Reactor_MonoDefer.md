## 1. 概述

在响应式编程中，我们可以通过多种方式创建Mono或Flux类型的发布者。
在本文中，我们将看看如何使用defer()方法来延迟Mono发布者的执行。

## 2. Mono.defer

我们可以使用Mono的defer()方法创建一个冷发布者，它最多可以产生一个值。让我们看看defer的方法签名：

```text
public static <T> Mono<T> defer(Supplier<? extends Mono<? extends T>> supplier)
```

这里，defer()接收创建Mono发布者的Supplier作为参数，并在下游订阅时惰性地返回该Mono。

然而问题是，什么是冷发布者和惰性发布者？

**仅当消费者订阅冷发布者时，执行线程才会评估冷发布者。而热发布者在任何订阅之前都热切地评估**。
Mono.just()方法提供了Mono类型的热发布者。

## 3. 它是如何工作的？

让我们探讨一个生成Mono类型的Supplier的案例：

```java
class MonoUnitTest {

    private Mono<String> sampleMsg(String str) {
        log.debug("Call to Retrieve Sample Message!! --> {} at : {}", str, System.currentTimeMillis());
        return Mono.just(str);
    }
}
```

在这里，这个方法返回一个热Mono发布者。让我们热切地订阅以下内容：

```java
class MonoUnitTest {

    @Test
    void whenUsingMonoJust_thenEagerEvaluation() throws InterruptedException {
        Mono<String> msg = sampleMsg("Eager Publisher");

        log.debug("Intermediate Test Message....");

        StepVerifier.create(msg)
                .expectNext("Eager Publisher")
                .verifyComplete();

        Thread.sleep(5000);

        StepVerifier.create(msg)
                .expectNext("Eager Publisher")
                .verifyComplete();
    }
}
```

在执行时，我们可以在日志中看到以下内容：

```text
01:55:55.064 [main] DEBUG [c.t.t.reactor.mono.MonoUnitTest] >>> Call to Retrieve Sample Message!! --> Eager Publisher at: 1662486955063 
01:55:55.095 [main] DEBUG [reactor.util.Loggers] >>> Using Slf4j logging framework 
01:55:55.095 [main] DEBUG [c.t.t.reactor.mono.MonoUnitTest] >>> Intermediate Test Message....
```

在这里，我们可以注意到：

+ 根据代码顺序，主线程急切地执行方法sampleMsg()。
+ 在使用StepVerifier的两个订阅中，主线程使用相同的sampleMsg()输出。因此，没有新的计算。

让我们看看Mono.defer()如何将其转换为冷(惰性)发布者：

```java
class MonoUnitTest {
    
    @Test
    void whenUsingMonoDefer_thenLazyEvaluation() throws InterruptedException {
        Mono<String> deferMsg = Mono.defer(() -> sampleMsg("Lazy Publisher"));
        log.debug("Intermediate Test Message....");

        StepVerifier.create(deferMsg)
                .expectNext("Lazy Publisher")
                .verifyComplete();

        Thread.sleep(5000);

        StepVerifier.create(deferMsg)
                .expectNext("Lazy Publisher")
                .verifyComplete();
    }
}
```

执行此方法时，我们可以在控制台中看到以下日志：

```text
01:58:37.523 [main] DEBUG [reactor.util.Loggers] >>> Using Slf4j logging framework 
01:58:37.525 [main] DEBUG [c.t.t.reactor.mono.MonoUnitTest] >>> Intermediate Test Message.... 
01:58:37.544 [main] DEBUG [c.t.t.reactor.mono.MonoUnitTest] >>> Call to Retrieve Sample Message!! --> Lazy Publisher at: 1662487117544 
01:58:42.557 [main] DEBUG [c.t.t.reactor.mono.MonoUnitTest] >>> Call to Retrieve Sample Message!! --> Lazy Publisher at: 1662487122557 
```

在这里，我们可以从输出的日志顺序中注意到几点：

+ StepVerifier在每个订阅上执行方法sampleMsg()，而不是在我们定义它的时候。
+ 延迟5秒后，方法sampleMsg()的第二个消费者再次执行它。

## 4. Mono.defer的用例

让我们看看Mono.defer()方法的可能使用场景：

+ 当我们必须有条件地订阅发布者时。
+ 当每个订阅的执行可能产生不同的结果时。
+ **deferContextual可用于当前基于上下文的发布者计算**。

### 4.1 示例用法

让我们看一个使用Mono.defer()方法的示例：

```java
class MonoUnitTest {

    @Test
    void whenEmptyList_thenMonoDeferExecuted() {
        Mono<List<String>> emptyList = Mono.defer(this::monoOfEmptyList);

        // Empty list, hence Mono publisher in switchIfEmpty executed after condition evaluation ...
        Flux<String> emptyListElements = emptyList.flatMapIterable(l -> l)
                .switchIfEmpty(Mono.defer(() -> sampleMsg("EmptyList")))
                .log();

        StepVerifier.create(emptyListElements)
                .expectNext("EmptyList")
                .verifyComplete();
    }
}
```

这里，Mono发布者sampleMsg()的Supplier被置于switchIfEmpty方法中，用于条件执行。因此，sampleMsg()仅在它被延迟订阅时执行。

现在，让我们看一下非空集合的相同代码：

```java
class MonoUnitTest {

    @Test
    void whenNonEmptyList_thenMonoDeferNotExecuted() {
        Mono<List<String>> nonEmptyList = Mono.defer(this::monoOfList);

        // Non-empty list, hence Mono publisher in switchIfEmpty won't evaluated ...
        Flux<String> listElements = nonEmptyList.flatMapIterable(l -> l)
                .switchIfEmpty(Mono.defer(() -> sampleMsg("NonEmptyList")))
                .log();

        StepVerifier.create(listElements)
                .expectNext("one", "two", "three", "four")
                .verifyComplete();
    }

    private Mono<List<String>> monoOfList() {
        List<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");
        list.add("three");
        list.add("four");
        return Mono.just(list);
    }
}
```

这里，sampleMsg()没有被执行，因为它没有被订阅。

## 5. 总结

在本文中，我们讲解了Mono.defer()方法和热/冷发布者。
此外，我们介绍了如何将热发布者转换为冷发布者。最后，我们还介绍了它们的实际使用案例。