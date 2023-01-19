## 1. 概述

在本教程中，我们将介绍几种在Reactor中处理异常的方法。
代码案例中介绍的操作符是在Mono和Flux类中定义的，这里我们只关注Flux类中方法的用法。

## 2. Maven依赖

```xml

<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
    <version>3.4.12</version>
</dependency>
```

## 3. 直接在管道运算符中抛出异常

处理异常的最简单方法是抛出异常。如果在处理流元素的过程中发生了异常，
**我们可以用throw关键字抛出异常，就像正常的方法执行一样**。

假设我们需要将字符串流解析为整数。如果元素不是数字形式的字符串，我们需要抛出异常。

使用map运算符进行这种转换是一种常见的做法：

```java
class ExceptionUnitTest {

    @Test
    void givenInvalidElement_whenPipelineThrowsException_thenErrorIsSentDownstream() {
        Function<String, Integer> mapper = input -> {
            if (input.matches("\\D"))
                throw new NumberFormatException();
            else
                return Integer.parseInt(input);
        };

        Flux<String> inFlux = Flux.just("1", "1.5", "2");
        Flux<Integer> outFlux = inFlux.map(mapper);

        StepVerifier.create(outFlux)
                .expectNext(1)
                .expectError(NumberFormatException.class)
                .verify();
    }
}
```

正如我们所见，如果输入元素无效，运算符会抛出异常。当我们以这种方式抛出异常时，**Reactor会捕获它并向下游发出错误信号**：

这种解决方案可行，但并不优雅。正如[Reactive Streams规范2.13](https://github.com/reactive-streams/reactive-streams-jvm#2-subscriber-code)
中所指定的，操作符必须正常返回。
Reactor通过将异常转换为错误信号来帮助我们。但是，我们可以做得更好。

本质上，**响应流依赖onError方法来指示故障情况**。在大多数情况下，此条件**必须通过在发布者上调用error方法来触发**。
在这个用例中使用异常使我们回到传统编程方式。

## 4. 在handle运算符中处理异常

与map操作符类似，我们可以使用handle操作符逐个处理流中的元素。
不同之处在于**Reactor为handle操作符提供了一个输出接收器**，允许我们应用更复杂的转换。

让我们更新上一节中的示例以使用handle运算符：

```java
class ExceptionUnitTest {

    @Test
    void givenInvalidElement_whenHandleCallsSinkErrorMethod_thenErrorIsSentDownstream() {
        BiConsumer<String, SynchronousSink<Integer>> handler = (input, sink) -> {
            if (input.matches("\\D"))
                sink.error(new NumberFormatException());
            else
                sink.next(Integer.parseInt(input));
        };

        Flux<String> inFlux = Flux.just("1", "1.5", "2");
        Flux<Integer> outFlux = inFlux.handle(handler);

        StepVerifier.create(outFlux)
                .expectNext(1)
                .expectError(NumberFormatException.class)
                .verify();
    }
}
```

与map操作符不同，**handle操作符接收一个消费者函数，对输入中的每个元素调用一次**。
这个消费者有两个参数：一个来自上游的元素和一个构建发送到下游的输出的SynchronousSink。

如果输入元素是一个数字形式的字符串，我们调用接收器的next方法，为它提供从输入元素转换而来的整数。
如果它不是数字字符串，我们将通过调用error方法来指示异常情况。

**请注意，调用error方法将取消对上游的订阅，并在下游调用onError方法**。error和onError的这种协作是在响应流中处理异常的标准方法。

## 5. 在flatMap运算符中处理异常

**另一个支持错误处理的常用运算符是flatMap**。此运算符将输入元素转换为Publishers，
然后将Publishers展平为新的流。我们可以利用这些发布者来表示错误状态。

下面使用flatMap改写之前的例子：

```java
class ExceptionUnitTest {

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
}
```

请注意，**在异常处理方面，handle和flatMap之间的唯一区别在于，
handle操作符在接收器上调用error方法，而flatMaps在Publisher上调用它**。

**如果我们处理由Flux对象表示的流，我们还可以使用concatMap来处理异常**。此方法的行为方式与flatMap大致相同，但它不支持异步处理。

## 6. 避免NullPointerException

本节介绍空引用的处理，这通常会导致NullPointerExceptions，这是Java中常见的异常。
为避免此异常，我们通常将变量与null进行比较，如果该变量实际上为null，则将执行定向到不同的分支。

```java
class ExceptionUnitTest {

    @Test
    void givenNullElement_whenPipelineOperatorExecutes_thenNpeIsSentToDownstream() {
        Function<String, Integer> mapper = input -> {
            if (input == null)
                return 0;
            else
                return Integer.parseInt(input);
        };

        Flux<String> inFlux = Flux.just("1", null, "2");
        Flux<Integer> outFlux = inFlux.map(mapper);

        StepVerifier.create(outFlux)
                .expectNext(1)
                .expectError(NullPointerException.class)
                .verify();
    }
}
```

我们可能认为上述代码不会发生NullPointerException，因为我们已经处理了输入值为null的情况。然而，事实并不是这样的。

显然，在我们上面的测试代码中，**NullPointerException在下游触发了一个错误，这意味着我们的空检查不起作用**。

[Reactive Streams规范的2.13条](https://github.com/reactive-streams/reactive-streams-jvm#2-subscriber-code)
规定“调用onSubscribe、onNext、onError或onComplete必须正常返回，
除非提供的任何参数为null，在这种情况下，它必须向调用方抛出java.lang.NullPointerException”。

**根据规范的要求，当一个null元素到达map函数时，Reactor会抛出NullPointerException**。

所以，当空值到达某个流时，我们无法处理它或在将其传递到下游之前将其转换为非空值。
**因此，避免NullPointerException的唯一方法是确保null值不会进入管道**。

## 7. 总结

在本文中，我们介绍了Reactor中的异常处理。我们演示了几个例子并阐明了过程。
我们还介绍了处理响应流时可能发生的一种特殊异常情况 - NullPointerException。