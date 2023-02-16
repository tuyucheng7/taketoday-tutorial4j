## 1. 概述

Project Reactor为JVM提供了一个完全非阻塞的编程基础。它提供了Reactive Streams规范的实现，并提供可组合的异步API，例如Flux。Flux是一个响应式流发布者，具有多个响应式运算符。它发出0到N个元素，然后成功或错误地完成。它可以根据我们的需要[以几种不同的方式创建](https://www.baeldung.com/flux-sequences-reactor)。

## 2. 理解Flux

**Flux是一个Reactive Stream发布者，可以发出0到N个元素**。它有几个用于生成、编排和转换Flux序列的运算符。Flux可以成功完成，也可以错误完成。

Flux API在Flux上提供了多个静态工厂方法用于创建源或从多个回调类型生成。它还提供实例方法和运算符来构建异步处理管道。该管道生成一个异步序列。

在接下来的部分中，让我们看看Flux中generate()和create()方法的一些用法。

## 3. Maven依赖

我们需要[reactor-core](https://search.maven.org/search?q=a:reactor-core)和[reactor-test](https://search.maven.org/search?q=a:reactor-test) Maven依赖项：

```xml
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
    <version>3.4.17</version>
</dependency>
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-test</artifactId>
    <version>3.4.17</version>
    <scope>test</scope>
</dependency>
```

## 4. generate

Flux API的generate()方法提供了一种简单直接的编程方法来创建Flux。generate()方法采用生成器函数来生成一系列元素。

generate方法有三种变体：

+ generate(Consumer<SynchronousSink<T\>> generator)
+ generate(Callable<S\> stateSupplier, BiFunction<S, SynchronousSink<T\>, S> generator)
+ generate(Callable<S\> stateSupplier, BiFunction<S, SynchronousSink<T\>, S> generator, Consumer<? super S> stateConsumer)

**generate方法根据需要计算并发出值**。最好在计算可能不会在下游使用的元素过于昂贵的情况下使用。如果发出的事件受应用程序状态的影响，也可以使用它。

### 4.1 示例

在这个例子中，让我们使用generate(Callable<S\> stateSupplier, BiFunction<S, SynchronousSink<T\>, S> generator)来生成一个Flux：

```java
public class CharacterGenerator {

    public Flux<Character> generateCharacters() {
        return Flux.generate(() -> 97, (state, sink) -> {
            char value = (char) state.intValue();
            sink.next(value);
            if (value == 'z') {
                sink.complete();
            }
            return state + 1;
        });
    }
}
```

在generate()方法中，我们提供两个函数作为参数：

+ 第一个是Callable函数。此函数使用值为97定义生成器的初始状态
+ 第二个是BiFunction。这是一个消费[SynchronousSink](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/SynchronousSink.html)的生成器函数。每当调用接收器的next()方法时，此SynchronousSink都会返回一个元素

顾名思义，SynchronousSink实例以同步的方式工作。但是，**我们不能在每次生成器调用中多次调用SynchronousSink对象的next方法**。

让我们使用[StepVerifier](https://www.baeldung.com/reactive-streams-step-verifier-test-publisher)验证生成的序列：

```java
@Test
void whenGeneratingCharacters_thenCharactersAreProduced() {
    CharacterGenerator characterGenerator = new CharacterGenerator();
    Flux<Character> characterFlux = characterGenerator.generateCharacters().take(3);

    StepVerifier.create(characterFlux)
        .expectNext('a', 'b', 'c')
        .expectComplete()
        .verify();
}
```

在此示例中，订阅者仅请求三个元素。因此，生成的序列以发出三个字符a、b和c结束。expectNext()期望从Flux中得到我们期望的元素。expectComplete()指示元素从Flux发射完成。

## 5. create

**当我们想要计算不受应用程序状态影响的多个(0到无穷大)值时，使用Flux中的create()方法**。这是因为Flux#create()方法的底层方法一直在计算元素。

此外，下游系统决定了它需要多少元素。因此，如果下游系统无法跟上，已经发出的元素将被缓冲或删除。

默认情况下，发出的元素会被缓冲，直到下游系统请求更多元素。

### 5.1 示例

现在让我们演示create()方法的示例：

```java
public class CharacterCreator {
    public Consumer<List<Character>> consumer;

    public Flux<Character> createCharacterSequence() {
        return Flux.create(sink -> CharacterCreator.this.consumer = items -> items.forEach(sink::next));
    }
}
```

我们可以注意到create运算符要求我们提供[FluxSink](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/FluxSink.html)作为消费者函数参数，而不是generate()中使用的SynchronousSink。在这种情况下，我们将为items列表中的每个元素调用next()，逐个发出。

现在让我们将CharacterCreator与两个字符序列一起使用：

```java
@Test
void whenCreatingCharactersWithMultipleThreads_thenSequenceIsProducedAsynchronously() {
    CharacterGenerator characterGenerator = new CharacterGenerator();
    List<Character> sequence1 = characterGenerator.generateCharacters()
        .take(3)
        .collectList()
        .block();
    List<Character> sequence2 = characterGenerator.generateCharacters()
        .take(2)
        .collectList()
        .block();
}
```

我们在上面的代码片段中创建了两个序列sequence1和sequence2。这些序列用作字符元素的源。请注意，我们使用CharacterGenerator实例来获取字符序列。

现在让我们定义一个characterCreator实例和两个线程实例：

```java
CharacterCreator characterCreator = new CharacterCreator();
Thread producerThread1 = new Thread(() -> characterCreator.consumer.accept(sequence1));
Thread producerThread2 = new Thread(() -> characterCreator.consumer.accept(sequence2));
```

现在我们创建了两个线程实例，它们将向发布者提供元素。当调用accept运算符时，字符元素开始流入序列源。接下来，我们订阅新的合并序列：

```java
List<Character> consolidated = new ArrayList<>();
characterCreator.createCharacterSequence().subscribe(consolidated::add);
```

请注意，createCharacterSequence返回我们订阅的Flux并消费consolidated列表中的元素。接下来，让我们触发看到元素在两个不同线程上移动的整个过程：

```java
producerThread1.start();
producerThread2.start();
producerThread1.join();
producerThread2.join();
```

最后，让我们验证操作的结果：

```java
assertThat(consolidated).containsExactlyInAnyOrder('a', 'b', 'c', 'a', 'b');
```

接收序列中的前三个字符来自sequence1，后两个字符来自sequence2。由于这是一个异步操作，因此无法保证这些序列中元素的顺序。

## 6. create与generate的比较

以下是create和generate操作之间的一些差异：

|              Flux create               |              Flux generate               |
|:--------------------------------------:|:----------------------------------------:|
|    此方法接收Consumer<FluxSink\>的实例作为参数     |  此方法接收Consumer<SynchronousSink\>的实例作为参数  |
|            create方法只调用消费者一次            |       generate方法根据下游应用的需要多次调用消费者方法       |
|            消费者可以立即发出0..N个元素            |                 只能发射一个元素                 |
|  发布者不知道下游状态。因此create接收溢出策略作为流量控制的附加参数  |             发布者根据下游应用需求生成元素              |
|       FluxSink允许我们在需要时使用多个线程发出元素       |           对多线程没有用，因为它一次只发出一个元素           |

## 7. 总结

在本文中，我们讨论了Flux API的create和generate方法之间的区别。

首先，我们介绍了响应式编程的概念并简要概述了Flux API。然后我们讨论了Flux API的create和generate方法。最后，我们列出了Flux API中create和generate方法之间的一些差异。