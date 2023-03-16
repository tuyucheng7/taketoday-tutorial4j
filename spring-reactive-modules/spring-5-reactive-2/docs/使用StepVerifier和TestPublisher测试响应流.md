## 1. 概述

在本教程中，我们将仔细研究使用StepVerifier和TestPublisher测试[响应流](http://www.reactive-streams.org/)。

我们将基于包含一系列响应式操作的[Spring Reactor](https://www.baeldung.com/spring-reactor)应用程序进行介绍。

## 2. Maven依赖

Spring Reactor带有几个用于测试响应流的类。

我们可以通过添加[reactor-test](https://central.sonatype.com/artifact/io.projectreactor/reactor-test/3.5.3)依赖项来获得这些：

```xml
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-test</artifactId>
    <scope>test</scope>
    <version>3.2.3.RELEASE</version>
</dependency>
```

## 3. StepVerifier

总的来说，reactor-test有两个主要用途：

+ 使用StepVerifier创建分步测试
+ 使用TestPublisher生成预定义数据以测试下游运算符

测试响应式流时最常见的情况是我们在代码中定义了发布者(Flux或Mono)。**我们想知道当有人订阅时它的行为**。

使用StepVerifier API，我们可以根据我们期望的元素**以及流完成时发生的情况**来定义我们对已发布元素的期望。

首先，让我们创建一个包含一些运算符的发布者。

我们使用Flux.just(T elements)。此方法将创建一个发射给定元素然后完成的Flux。

由于高级运算符超出了本文的讨论范围，因此我们将只创建一个简单的发布者，它过滤长度为4的输入，并转为其大写形式：

```java
class StepByStepUnitTest {

    Flux<String> source = Flux.just("John", "Monica", "Mark", "Cloe", "Frank", "Casper", "Olivia", "Emily", "Cate")
            .filter(name -> name.length() == 4)
            .map(String::toUpperCase);
}
```

### 3.1 逐步方案

现在，让我们使用StepVerifier测试我们的源，**以测试当有人订阅时会发生什么**：

```java
@Test
void shouldReturnForLettersUpperCaseStrings() {
    StepVerifier.create(source)
        .expectNext("JOHN")
        .expectNextMatches(name -> name.startsWith("MA"))
        .expectNext("CLOE", "CATE")
        .expectComplete()
        .verify();
}
```

首先，我们使用create方法创建一个StepVerifier构建器。

接下来，我们包装正在测试的Flux源。第一个信号使用expectNext(T element)进行验证，但实际上，**我们可以将任意数量的元素传递给expectNext**。

我们还可以使用expectNextMatches并提供Predicate<T\>来进行更自定义的匹配。

对于我们最后的期望，我们期望我们的流完成。

最后，**我们使用verify()来触发我们的测试**。

### 3.2 StepVerifier中的异常

现在，让我们将Flux发布者与Mono连接起来。

**当订阅时，我们将让这个Mono立即终止并出现错误**：

```java
Flux<String> error = source.concatWith(
    Mono.error(new IllegalArgumentException("Our message"))
);
```

现在，当处理完流中的前四个元素后，**我们期望我们的流以异常终止**：

```java
@Test
void shouldThrowExceptionAfterFourElements() {
    Flux<String> error = source.concatWith(
        Mono.error(new IllegalArgumentException("Our message"))
    );

    StepVerifier.create(error)
        .expectNextCount(4)
        .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
            throwable.getMessage().equals("Our message")
        ).verify();
}
```

**我们只能使用一种方法来验证异常**。OnError信号通知订阅者发布者**已关闭并处于错误状态。因此，我们不能在之后添加更多的expect**。

如果不需要立即检查异常的类型和消息，那么我们可以使用以下其中一种专用方法：

+ expectError()：期望任何类型的错误
+ expectError(Class<? extends Throwable> clazz)：期望特定类型的错误
+ expectErrorMessage(String errorMessage)：期望有特定消息的错误
+ expectErrorMatches(Predicate<Throwable\> predicate)：期望与给定谓词匹配的错误
+ expectErrorSatisfies(Consumer<Throwable\> assertionConsumer)：使用Throwable以执行自定义断言

### 3.3 测试基于时间的发布者

**有时我们的发布者是基于时间的**。

例如，假设在我们的实际应用程序中，**事件之间有一天的延迟**。现在，很明显，我们不希望我们的测试运行一整天以验证具有这种延迟的预期行为。

**StepVerifier.withVirtualTime构建器旨在避免长时间运行的测试**。

**我们通过调用withVirtualTime创建一个构建器**。请注意，此方法不将Flux作为输入。相反，它需要一个Supplier，该Supplier在设置调度程序后惰性地创建一个被测试的Flux的实例。

为了演示我们如何测试事件之间的预期延迟，让我们创建一个以一秒为间隔运行两秒的Flux。**如果计时器运行正确，我们应该只得到两个元素**：

```java
@Test
void simpleExample() {
    StepVerifier
        .withVirtualTime(() -> Flux.interval(Duration.ofSeconds(1)).take(2))
        .expectSubscription()
        .expectNoEvent(Duration.ofSeconds(1))
        .expectNext(0L)
        .thenAwait(Duration.ofSeconds(1))
        .expectNext(1L)
        .verifyComplete();
}
```

请注意，我们应该避免在代码中较早地实例化Flux，然后让Supplier返回这个变量。相反，我们应该始终**在Lambda中实例化Flux**。

有两种主要的处理时间的期望方法：

+   thenAwait(Duration duration)：暂停对步骤的评估；在此期间可能会发生新事件
+   expectNoEvent(Duration duration)：在持续时间内出现任何事件时失败；序列将在给定的持续时间内通过

请注意，第一个信号是订阅事件，因此**每个expectNoEvent(Duration duration)都应该在expectSubscription()之前**。

### 3.4 使用StepVerifier的执行后断言

因此，正如我们所看到的，逐步描述我们的期望很简单。

但是，**有时我们需要在整个场景成功运行后验证其他状态**。

让我们创建一个自定义发布者。**它将发出一些元素，然后完成、暂停并发出另一个元素，我们将删除该元素**：

```java
Flux<Integer> source = Flux.<Integer>create(emitter -> {
    emitter.next(1);
    emitter.next(2);
    emitter.next(3);
    emitter.complete();
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        emitter.error(new RuntimeException(e));
    }
    emitter.next(4);
}).filter(number -> number % 2 == 0);
```

**我们预计它会发出2，但是会丢弃4，因为我们首先调用了emitter.complete**。

因此，让我们使用verifyThenAssertThat来验证此行为。此方法返回StepVerifier.Assertions，我们可以在其上添加断言：

```java
@Test
void droppedElements() {
    StepVerifier.create(source)
        .expectNext(2)
        .expectComplete()
        .verifyThenAssertThat()
        .hasDropped(4)
        .tookLessThan(Duration.ofMillis(1500));
}
```

## 4. 使用TestPublisher生成数据

**有时，我们可能需要一些特殊数据来触发选定的信号**。

例如，我们可能需要测试一个非常特殊的情况。

或者，我们可以选择实现我们自己的运算符并想要测试它的行为方式。

对于这两种情况，我们都可以使用TestPublisher<T\>，它允许我们**以编程方式触发各种信号**：

+ next(T value)或next(T value, T rest)：向订阅者发送一个或多个信号
+ emit(T value)：与next(T)相同，但之后调用complete()
+ complete()：使用complete信号终止源
+ error(Throwable tr)：使用error信号终止源
+ flux()：将TestPublisher包装到Flux中的工具方法
+ mono()：与flux()相同，但包装为Mono

### 4.1 创建TestPublisher

让我们创建一个简单的TestPublisher，它发出一些信号然后以异常终止：

```java
@Test
void testPublisher() {
    TestPublisher
        .<String>create()
        .next("First", "Second", "Third")
        .error(new RuntimeException("Message"));
}
```

### 4.2 实践

正如我们之前提到的，我们有时可能想要触发**一个精心挑选的信号，以密切匹配特定情况**。

现在，在这种情况下，我们完全掌握数据源尤为重要。为了实现这一目标，我们可以再次依赖TestPublisher。

首先，让我们创建一个类，它使用Flux<String\>作为构造函数参数来执行getUpperCase()操作：

```java
static class UppercaseConverter {
    private final Flux<String> source;

    UppercaseConverter(Flux<String> source) {
        this.source = source;
    }

    Flux<String> getUpperCase() {
        return source.map(String::toUpperCase);
    }
}
```

假设UppercaseConverter是我们具有复杂逻辑和运算符的类，我们需要从源发布者提供非常特殊的数据。

我们可以使用TestPublisher轻松实现这一点：

```java
@Test
void testPublisherInAction() {
    final TestPublisher<String> testPublisher = TestPublisher.create();

    UppercaseConverter uppercaseConverter = new UppercaseConverter(testPublisher.flux());

    StepVerifier.create(uppercaseConverter.getUpperCase())
        .then(() -> testPublisher.emit("aA", "bb", "ccc"))
        .expectNext("AA", "BB", "CCC")
        .verifyComplete();
}
```

在此示例中，我们在UppercaseConverter构造函数参数中创建了一个用于测试Flux发布者。然后，我们的TestPublisher发出三个元素并完成。

### 4.3 行为不端的TestPublisher

另一方面，**我们可以使用createNonCompliant工厂方法创建一个行为不端的TestPublisher**。我们需要从TestPublisher.Violation向构造函数传递一个枚举值。这些值指定我们的发布者可能忽略规范的哪些部分。

让我们看一个不会为null元素抛出NullPointerException的TestPublisher：

```java
@Test
void nonCompliant() {
    TestPublisher
        .createNoncompliant(TestPublisher.Violation.ALLOW_NULL)
        .emit("1", "2", null, "3");
}
```

除了ALLOW_NULL，我们还可以使用TestPublisher.Violation来：

+ REQUEST_OVERFLOW：允许在请求数量不足时调用next()，而不是抛出IllegalStateException
+ CLEANUP_ON_TERMINATE：允许连续多次发送任何终止信号
+ DEFER_CANCELLATION：允许我们忽略取消信号并继续发射元素

## 5. 总结

在本文中，**我们讨论了测试Spring Reactor项目响应流的各种方法**。

首先，我们了解了如何使用StepVerifier来测试发布者。然后，我们看到了如何使用TestPublisher。同样，我们看到了如何处理行为不当的TestPublisher。