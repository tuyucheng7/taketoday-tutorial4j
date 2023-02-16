## 1. 概述

在本教程中，我们将介绍几种在[Reactor](https://www.baeldung.com/reactor-core)中处理异常的方法。代码示例中介绍的运算符在[Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html)和[Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)类中都有定义。但是，**我们只关注Flux类中的方法**。

## 2. Maven依赖

让我们从添加[Reactor核心依赖项](https://search.maven.org/search?q=a:reactor-core)开始：

```xml
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
    <version>3.4.9</version>
</dependency>
```

## 3. 直接在管道运算符中抛出异常

处理异常的最简单方法是抛出它。如果在处理流元素的过程中发生了异常，**我们可以使用throw关键字抛出异常，就像正常的方法执行一样**。

假设我们需要将字符串流解析为整数。如果元素不是数字形式的字符串，我们需要抛出异常。

使用map运算符进行此类转换是一种常见的做法：

```java
Function<String, Integer> mapper = input -> {
    if (input.matches("\\D"))
        throw new NumberFormatException();
    else
        return Integer.parseInt(input);
};
Flux<String> inFlux = Flux.just("1", "1.5", "2");
Flux<Integer> outFlux = inFlux.map(mapper);
```

如我们所见，如果输入元素无效，运算符将抛出异常。当我们以这种方式抛出异常时，**Reactor会捕获它并向下游发出错误信号**：

```java
StepVerifier.create(outFlux)
    .expectNext(1)
    .expectError(NumberFormatException.class)
    .verify();
```

该解决方案有效，但并不优雅。正如[Reactive Streams规范规则2.13](https://github.com/reactive-streams/reactive-streams-jvm#2-subscriber-code)中所指定的，运算符必须正常返回。Reactor通过将异常转换为错误信号来帮助我们。但是，我们可以做得更好。

本质上，**响应流依赖于onError方法来指示故障情况**。在大多数情况下，此条件**必须通过在[Publisher](https://www.reactive-streams.org/reactive-streams-1.0.3-javadoc/org/reactivestreams/Publisher.html)上调用error方法来触发**。在此用例中使用异常将我们带回传统编程。

## 4. 在handle运算符中处理异常

与map运算符类似，我们可以使用handle运算符逐个处理流中的元素。不同之处在于**Reactor为handle运算符提供了一个输出接收器**，允许我们应用更复杂的转换。

让我们更新上一节中的示例以使用handle运算符：

```java
BiConsumer<String, SynchronousSink<Integer>> handler = (input, sink) -> {
    if (input.matches("\\D"))
        sink.error(new NumberFormatException());
    else
        sink.next(Integer.parseInt(input));
};

Flux<String> inFlux = Flux.just("1", "1.5", "2");
Flux<Integer> outFlux = inFlux.handle(handler);
```

与map运算符不同，**handle运算符接收一个消费者([BiConsumer](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/function/BiConsumer.html))函数，为每个元素调用一次**。这个消费者有两个参数：一个来自上游的元素和一个构建要发送到下游的输出的[SynchronousSink](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/SynchronousSink.html)。

如果输入元素是一个数字形式的字符串，我们调用接收器上的next方法，为它提供从输入转换而来的整数。如果它不是数字字符串，我们将通过使用Exception对象调用error方法来指示这种情况。

请注意，**调用error方法将取消对上游的订阅并在下游调用onError方法**。error和onError的这种协作是在响应流中处理异常的标准方法。

让我们验证输出流：

```java
StepVerifier.create(outFlux)
    .expectNext(1)
    .expectError(NumberFormatException.class)
    .verify();
```

## 5. 在flatMap运算符中处理异常

**另一个支持错误处理的常用运算符是[flatMap](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#flatMap-java.util.function.Function-)**。此运算符将输入元素转换为Publisher，然后将Publishers展平为新的流。我们可以利用这些Publisher来表示错误状态。

让我们使用flatMap尝试相同的示例：

```java
@Test
void givenInvalidElement_whenFlatMapCallsMonoErrorMethod_thenErrorIsSentToDownstream() {
    Function<String, Publisher<Integer>> mapper = input -> {
        if (input.matches("\\D"))
            return Mono.error(new NumberFormatException());
        else
            return Mono.just(Integer.parseInt(input));
    };

    Flux<String> inFlux = Flux.just("1", "1.5", "2");
    Flux<Integer> outFlux = inFlux.flatMap(mapper);

    StepVerifier.create(outFlux)
        .expectNext(1)
        .expectError(NumberFormatException.class)
        .verify();
}
```

不出所料，结果和之前一样。

请注意，**在异常处理方面handle和flatMap之间的唯一区别在于，handle运算符在接收器上调用error方法，而flatMaps在Publisher上调用它**。

**如果我们正在处理由Flux对象表示的流，我们也可以使用[concatMap](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#concatMap-java.util.function.Function-)来处理错误**。此方法的行为方式与flatMap大致相同，但它不支持异步处理。

## 6. 避免NullPointerException

本节介绍空引用的处理，这通常会导致NullPointerExceptions，这是Java中常见的异常。为避免此异常，我们通常将变量与null进行比较，如果该变量实际上为null，则将执行定向到不同的分支。

```java
Function<String, Integer> mapper = input -> {
    if (input == null)
        return 0;
    else
        return Integer.parseInt(input);
};
```

我们可能认为上述代码不会发生NullPointerException，因为我们已经处理了输入值为null的情况。然而，事实并不是这样的：

```java
Flux<String> inFlux = Flux.just("1", null, "2");
Flux<Integer> outFlux = inFlux.map(mapper);

StepVerifier.create(outFlux)
    .expectNext(1)
    .expectError(NullPointerException.class)
    .verify();
```

显然，在我们上面的测试代码中**NullPointerException触发了下游的错误，这意味着我们的空检查不起作用**。

要理解为什么会发生这种情况，我们需要回到Reactive Streams规范。[规范的规则2.13条](https://github.com/reactive-streams/reactive-streams-jvm#2-subscriber-code)规定“调用onSubscribe、onNext、onError或onComplete必须正常返回，除非提供的任何参数为null，在这种情况下它必须向调用者抛出java.lang.NullPointerException”。

**根据规范的要求，当null值到达map函数时，Reactor会抛出NullPointerException**。

因此，当空值到达某个流时，我们无能为力。在将其传递给下游之前，我们无法处理它或将其转换为非空值。**因此，避免NullPointerException的唯一方法是确保null值不会进入管道**。

## 7. 总结

在本文中，我们介绍了Reactor中的异常处理。我们讨论了几个示例并阐明了过程。我们还介绍了处理响应流时可能发生的一种特殊异常情况-NullPointerException。