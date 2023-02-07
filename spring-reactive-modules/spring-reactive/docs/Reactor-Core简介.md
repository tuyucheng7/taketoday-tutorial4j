## 1. 概述

[Reactor Core](https://github.com/reactor/reactor-core)是一个实现响应式编程模型的Java 8库。它建立在[Reactive Streams规范](http://www.reactive-streams.org/)之上，该规范是构建响应式应用程序的标准。

从非响应式Java开发的背景来看，响应式开发可能是一条非常陡峭的学习曲线。当将其与Java 8 Stream API进行比较时，这变得更具挑战性，因为它们可能被误认为是相同的高级抽象。

在本文中，我们将试图揭开这种编程范式的神秘面纱。我们将在Reactor中采取一些小步骤，直到我们构建了如何编写响应式代码，为后续系列中更高级的文章奠定基础。

## 2. Reactive Streams规范

在我们引出Reactor之前，我们应该看看Reactive Streams规范。这就是Reactor实现的内容，它为该库奠定了基础。

**本质上，Reactive Streams是异步流处理的规范**。

换句话说，在一个系统中，许多事件是异步产生和消费的。设想一下，每秒有数千个股票更新流进入金融应用程序，并且它必须及时响应这些更新。

其主要目标之一是解决背压问题。如果我们有一个生产者向消费者发送事件的速度快于消费者处理事件的速度，那么最终消费者将被事件淹没，耗尽系统资源。

背压意味着我们的消费者应该能够告诉生产者要发送多少数据，以防止这种情况发生，这就是规范中规定的内容。

## 3. Maven依赖

在开始之前，让我们添加[Maven](https://search.maven.org/search?q=a:reactor-core)依赖项：

```xml
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
    <version>3.4.12</version>
</dependency>
<dependency> 
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.7</version>
</dependency>
```

我们还将[Logback](https://logback.qos.ch/)添加为依赖项。这是因为我们需要记录Reactor的输出，以便更好地理解数据流。

## 4. 生成数据流

为了使应用程序具有响应性，它必须能够做的第一件事是生成数据流。

这可能类似于我们之前给出的股票更新示例。如果没有这些数据，我们就不会有任何响应，这就是为什么这是合乎逻辑的第一步。

Reactive Core为我们提供了两种数据类型，使我们能够做到这一点。

### 4.1 Flux

第一种方法是使用[Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)。这是一个可以发出0..n个元素的流。让我们尝试创建一个简单的：

```java
Flux<Integer> just = Flux.just(1, 2, 3, 4);
```

在本例中，我们有一个包含四个元素的静态流。

### 4.2 Mono

第二种方法是使用[Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html)，它是一个可以发出0..1个元素的流。让我们尝试实例化一个：

```java
Mono<Integer> just = Mono.just(1);
```

这看起来与Flux几乎完全相同，只是Mono最多仅限于一个元素。

### 4.3 为什么不只有Flux？

在进一步之前，有必要强调一下为什么我们有这两种数据类型。

首先，应该注意的是Flux和Mono都是Reactive Streams中[Publisher](http://www.reactive-streams.org/reactive-streams-1.0.3-javadoc/org/reactivestreams/Publisher.html)接口的实现。这两个类都符合规范，我们可以使用这个接口代替它们：

```java
Publisher<String> just = Mono.just("foo");
```

但实际上，知道这个基数是有用的。这是因为一些操作只对这两种类型中的一种有意义，并且因为它可以更具表现力(想象一下Repository中的findOne())。

## 5. 订阅流

现在我们对如何生成数据流有了一个高级概述，我们需要订阅它以便它发出元素。

### 5.1 收集元素

让我们使用subscribe()方法来收集流中的所有元素：

```java
@Test
void givenFlux_whenSubscribing_thenCollectElements() {
    List<Integer> elements = new ArrayList<>();
    Flux.just(1, 2, 3, 4)
        .log()
        .subscribe(elements::add);

    assertThat(elements).containsExactly(1, 2, 3, 4);
}
```

在我们订阅之前，数据不会开始流动。请注意，我们还添加了一些日志记录，这在我们查看幕后发生的事情时会很有帮助。

### 5.2 元素的流动

通过适当的日志记录，我们可以使用它来可视化数据是如何流经我们的流的：

```shell
2022-09-15 17:34:06.688  INFO   --- [main] reactor.Flux.Array.1: | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
2022-09-15 17:34:06.694  INFO   --- [main] reactor.Flux.Array.1: | request(unbounded)
2022-09-15 17:34:06.695  INFO   --- [main] reactor.Flux.Array.1: | onNext(1)
2022-09-15 17:34:06.695  INFO   --- [main] reactor.Flux.Array.1: | onNext(2)
2022-09-15 17:34:06.695  INFO   --- [main] reactor.Flux.Array.1: | onNext(3)
2022-09-15 17:34:06.695  INFO   --- [main] reactor.Flux.Array.1: | onNext(4)
2022-09-15 17:34:06.697  INFO   --- [main] reactor.Flux.Array.1: | onComplete()
```

首先，一切都在主线程上运行。我们先不详细介绍这方面的任何细节，因为我们将在本文后面进一步研究并发性。不过，它确实使事情变得简单，因为我们可以按顺序处理所有数据。

现在让我们逐个的剖析记录的日志序列：

1. onSubscribe()：当我们订阅流时，将调用该函数
2. request(unbounded)：当我们调用subscribe()时，在幕后创建一个[Subscription](http://www.reactive-streams.org/reactive-streams-1.0.0-javadoc/org/reactivestreams/Subscription.html)。此Subscription从流中请求元素。
   在这种情况下，它默认为无界，这意味着它请求每个可用的元素
3. onNext()：在每个元素上调用
4. onComplete()：在接收到最后一个元素之后，最后调用它。实际上还有一个onError()，如果有异常就会调用该方法

这是作为响应式流规范的一部分在Subscriber接口中列出的流程，实际上，这就是我们在调用onSubscribe()时在幕后实例化的流。这是一种有用的方法，但为了更好地理解发生的事情，让我们直接实现一个Subscriber接口：

```java
@Test
void givenFlux_whenGivenSubscriber_thenCollectElements() {
    List<Integer> elements = new ArrayList<>();
    Flux.just(1, 2, 3, 4)
        .log()
        .subscribe(new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                elements.add(integer);
            }
    
            @Override
            public void onError(Throwable t) {
    
            }
    
            @Override
            public void onComplete() {
    
            }
        });
    assertThat(elements).containsExactly(1, 2, 3, 4);
}
```

我们可以看到，上述流程中的每个阶段都映射到Subscriber实现中的一个方法。恰好Flux为我们提供了一个工具方法来避免这种冗长代码。

### 5.3 与Java 8 Stream的比较

这看起来与Java 8 Stream中的collect有些异曲同工之妙：

```java
List<Integer> collected = Stream.of(1, 2, 3, 4).collect(toList());
```

核心区别在于，Reactive是推送(push)模型，而Java 8 Stream是拉取(pull)模型。**在响应式编程中，事件在订阅者进入时被推送给订阅者**。

接下来要注意的是，Stream终端操作只是提取所有数据并返回结果。使用Reactive，我们可以有一个来自外部资源的无限流，多个订阅者可以临时附加和删除。我们还可以做一些其他操作，比如合并流、节流和应用背压，我们将在接下来介绍这些操作。

## 6. 背压

接下来我们应该考虑的是背压。在我们的示例中，订阅者告诉生产者一次推送每个元素。这最终可能会让订阅者承受巨大压力，消耗其所有资源。

**背压是指下游可以告诉上游发送较少的数据，以防止其不堪重负**。

我们可以修改我们的Subscriber实现以应用背压。让我们使用request()告诉上游一次只发送两个元素：

```java
@Test
void givenFlux_whenApplyBackPressure_thenPushElementsInBatches() {
    List<Integer> elements = new ArrayList<>();
    Flux.just(1, 2, 3, 4)
        .log()
        .subscribe(new Subscriber<>() {
            private Subscription s;
            int onNextElement;
    
            @Override
            public void onSubscribe(Subscription s) {
                this.s = s;
                s.request(2);
            }
    
            @Override
            public void onNext(Integer integer) {
                elements.add(integer);
                onNextElement++;
                if (onNextElement % 2 == 0)
                    s.request(2);
            }
    
            @Override
            public void onError(Throwable t) {
    
            }
    
            @Override
            public void onComplete() {
    
            }
        });
    assertThat(elements).containsExactly(1, 2, 3, 4);
}
```

现在，如果我们再次运行我们的代码，我们首先看到request(2)被调用，然后是两个onNext()调用，然后request(2)被再次调用...

```shell
2022-09-15 19:11:54.625  INFO   --- [main] reactor.Flux.Array.1 : | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
2022-09-15 19:11:54.627  INFO   --- [main] reactor.Flux.Array.1 : | request(2)
2022-09-15 19:11:54.627  INFO   --- [main] reactor.Flux.Array.1 : | onNext(1)
2022-09-15 19:11:54.627  INFO   --- [main] reactor.Flux.Array.1 : | onNext(2)
2022-09-15 19:11:54.627  INFO   --- [main] reactor.Flux.Array.1 : | request(2)
2022-09-15 19:11:54.627  INFO   --- [main] reactor.Flux.Array.1 : | onNext(3)
2022-09-15 19:11:54.627  INFO   --- [main] reactor.Flux.Array.1 : | onNext(4)
2022-09-15 19:11:54.627  INFO   --- [main] reactor.Flux.Array.1 : | request(2)
2022-09-15 19:11:54.628  INFO   --- [main] reactor.Flux.Array.1 : | onComplete()
```

本质上，这是响应式拉回压力。我们要求上游只推送一定数量的元素，并且只有在我们准备好的时候。

如果我们想象我们正在从Twitter流式传输推文，那么将由上游来决定做什么。如果推文进来了，但没有来自下游的请求，那么上游可以丢弃项目，将它们存储在缓冲区中，或者采取其他策略。

## 7. 流上的操作

我们还可以对流中的数据执行操作，根据我们认为合适的事件进行响应。

### 7.1 映射流中的数据

我们可以执行的一个简单操作是应用转换。在这种情况下，让我们将流中的所有数字加倍：

```java
@Test
void givenFlux_whenSubscribing_thenStream() {
    List<Integer> elements = new ArrayList<>();
    Flux.just(1, 2, 3, 4)
        .log()
        .map(s -> {
            LOGGER.debug("{}:{}", s, Thread.currentThread());
            return s * 2;
        }).subscribe(elements::add);

    assertThat(elements).containsExactly(2, 4, 6, 8);
}
```

map()将在onNext()被调用时应用。

### 7.2 合并两个流

然后我们可以通过将另一个流与这个流组合来使事情变得更有趣。让我们使用zip()方法来完成这个功能：

```java
@Test
void givenFlux_whenZipping_thenCombine() {
    List<String> elements = new ArrayList<>();
    Flux.just(1, 2, 3, 4).log()
        .map(s -> s * 2)
        .zipWith(Flux.range(0, Integer.MAX_VALUE), 
            (one, two) -> String.format("First Flux: %d, Second Flux: %d", one, two))
        .subscribe(elements::add);

    assertThat(elements).containsExactly(
        "First Flux: 2, Second Flux: 0",
        "First Flux: 4, Second Flux: 1",
        "First Flux: 6, Second Flux: 2",
        "First Flux: 8, Second Flux: 3");
}
```

在这里，我们创建了另一个Flux，它不断递增1，并将其与我们的原始Flux一起流式传输。我们可以通过查看日志来了解它们是如何协同工作的：

```shell
2022-09-15 19:20:20.282  INFO   --- [main] reactor.Flux.Array.1 : | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
2022-09-15 19:20:20.286  INFO   --- [main] reactor.Flux.Array.1 : | request(32)
2022-09-15 19:20:20.286  INFO   --- [main] reactor.Flux.Array.1 : | onNext(1)
2022-09-15 19:20:20.286  INFO   --- [main] reactor.Flux.Array.1 : | onNext(2)
2022-09-15 19:20:20.286  INFO   --- [main] reactor.Flux.Array.1 : | onNext(3)
2022-09-15 19:20:20.286  INFO   --- [main] reactor.Flux.Array.1 : | onNext(4)
2022-09-15 19:20:20.287  INFO   --- [main] reactor.Flux.Array.1 : | onComplete()
2022-09-15 19:20:20.287  INFO   --- [main] reactor.Flux.Range.2 : | onSubscribe([Synchronous Fuseable] FluxRange.RangeSubscription)
2022-09-15 19:20:20.287  INFO   --- [main] reactor.Flux.Range.2 : | request(32)
2022-09-15 19:20:20.288  INFO   --- [main] reactor.Flux.Range.2 : | onNext(0)
2022-09-15 19:20:20.288  INFO   --- [main] reactor.Flux.Range.2 : | onNext(1)
2022-09-15 19:20:20.288  INFO   --- [main] reactor.Flux.Range.2 : | onNext(2)
2022-09-15 19:20:20.288  INFO   --- [main] reactor.Flux.Range.2 : | onNext(3)
2022-09-15 19:20:20.288  INFO   --- [main] reactor.Flux.Array.1 : | cancel()
2022-09-15 19:20:20.288  INFO   --- [main] reactor.Flux.Range.2 : | cancel()
```

请注意，我们现在每个Flux有一个Subscription。onNext()调用也是交替进行的，因此当我们应用zip()方法时，流中每个元素的索引都将匹配。

## 8. 热流

目前，我们关注的主要是冷流。这些是易于处理的静态、固定长度的流。响应式更现实的用例可能是无限发生的事情。

例如，我们可能有一个需要不断响应的鼠标移动流或Twitter提要。这些类型的流称为热流，因为它们始终在运行并且可以在任何时间点订阅，缺少数据的开头。

### 8.1 创建ConnectableFlux

创建热流的一种方法是将冷流转换为热流。让我们创建一个永远持续的Flux，将结果输出到控制台，这将模拟来自外部资源的无限数据流：

```java
@Test
void givenFlux_whenConvertToHotStream_thenShouldStream() {
    ConnectableFlux<Object> publish = Flux.create(sink -> {
        while (true)
            sink.next(System.currentTimeMillis());
    }).publish();
    publish.subscribe(System.out::println);
    publish.subscribe(System.out::println);
    publish.connect();
}
```

通过调用publish()我们得到了一个[ConnectableFlux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/ConnectableFlux.html)。这意味着调用subscribe()不会导致它开始发送元素，从而允许我们添加多个subscriptions：

```java
publish.subscribe(System.out::println);        
publish.subscribe(System.out::println);
```

如果我们尝试运行这段代码，什么都不会发生。直到我们调用connect()，Flux才会开始发送元素：

```java
publish.connect();
```

### 8.2 节流

如果我们运行代码，我们的控制台将被日志淹没。这是在模拟一种情况，即过多的数据被传递给我们的消费者。让我们尝试通过节流来解决这个问题：

```java
@Test
void givenFlux_whenApplyThrottling_thenShouldStream() {
    ConnectableFlux<Object> publish = Flux.create(sink -> {
        while (true)
            sink.next(System.currentTimeMillis());
    }).sample(Duration.ofSeconds(2)).publish();
    publish.subscribe(System.out::println);
    publish.subscribe(System.out::println);
    publish.connect();
}
```

在这里，我们引入了一个间隔为两秒的sample()方法。现在，值只会每两秒推送一次给我们的订阅者，这意味着控制台将不那么忙碌。

当然，有多种策略可以减少向下游发送的数据量，例如窗口化和缓冲，但它们不在本文的讨论范围内。

## 9. 并发性

我们上面所有的示例目前都在主线程上运行。但是，如果需要，我们可以控制代码在哪个线程上运行。[Scheduler](https://projectreactor.io/docs/core/release/api/reactor/core/scheduler/Scheduler.html)接口提供了围绕异步代码的抽象，为我们提供了许多实现。让我们尝试订阅另一个线程到main：

```java
@Test
void givenFlux_whenParallel_thenSubscribeInDifferentThreads() throws InterruptedException {
    List<String> threadNames = new ArrayList<>();
    Flux.just(1, 2, 3, 4)
        .log()
        .map(it -> it * 2)
        .subscribeOn(Schedulers.parallel())
        .subscribe(threadNames::add);

    Thread.sleep(1000);
    assertThat(threadNames).isNotEmpty();
    assertThat(threadNames).hasSize(4);
}
```

并行调度器将导致我们的订阅在不同的线程上运行，我们可以通过查看日志来证明这一点。我们可以看到第一条日志来自主线程，而Flux正在另一个名为parallel-1的线程中运行。

```shell
2022-09-15 19:34:04.570 DEBUG   --- [main] reactor.util.Loggers : Using Slf4j logging framework
2022-09-15 19:34:04.590  INFO   --- [parallel-1] reactor.Flux.Array.1 : | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
2022-09-15 19:34:04.592  INFO   --- [parallel-1] reactor.Flux.Array.1 : | request(unbounded)
2022-09-15 19:34:04.593  INFO   --- [parallel-1] reactor.Flux.Array.1 : | onNext(1)
2022-09-15 19:34:04.593  INFO   --- [parallel-1] reactor.Flux.Array.1 : | onNext(2)
2022-09-15 19:34:04.593  INFO   --- [parallel-1] reactor.Flux.Array.1 : | onNext(3)
2022-09-15 19:34:04.593  INFO   --- [parallel-1] reactor.Flux.Array.1 : | onNext(4)
2022-09-15 19:34:04.593  INFO   --- [parallel-1] reactor.Flux.Array.1 : | onComplete()
```

并发比这更有趣，值得我们在另一篇文章中探讨它。

## 10 总结

在本文中，我们对Reactor Core进行了高层次的、端到端的概述。我们已经解释了如何发布和订阅流、应用背压、对流进行操作以及异步处理数据。这有望为我们编写响应式应用程序奠定基础。

本系列后面的文章将介绍更高级的并发和其他响应式概念。还有另一篇文章介绍了[Reactor与Spring](https://www.baeldung.com/reactor-bus)。